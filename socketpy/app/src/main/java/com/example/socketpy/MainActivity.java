package com.example.socketpy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Socket sock;
    DataOutputStream dos;
    BufferedInputStream bis;
    FileOutputStream fos;
    FileOutputStream fos2;
    InputStream dis;
    Bitmap b, c;
    int[] result = new int[2];
    int num = 0;
    private Thread checkUpdate = new Thread() {
        @Override
        public void run() {
            //결과값 받기//
            try {
                dis = sock.getInputStream();
                while (true) {
                    result[num] = dis.read();
                    num++;
                    if (num == 2)
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < num; i++) {
                Log.w("r", String.valueOf(result[i]));
            }
            //컨투어 이미지 받기//
            try {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Client/" + "Contour.jpg");
                file.createNewFile();
                File file2 = new File(Environment.getExternalStorageDirectory().getPath() + "/Client/" + "Contour2.jpg");
                file2.createNewFile();
                fos = new FileOutputStream(file, false);
                fos2 = new FileOutputStream(file2, false);
                String image, image2;
                int img;
                int size = 65535;
                byte[] imgData = new byte[size];
                try {
                    img = dis.read(imgData);
                    fos.write(imgData, 0, img);
                    img = dis.read(imgData);
                    fos2.write(imgData, 0, img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos.close();
                fos2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Thread sendThread = new Thread() {
        @Override
        public void run() {
            try {
                int len = bitmapToByteArray(b).length;
                byte[] bsize = intToByteArray(len);
                dos.write(bsize);
                int size = 65535;
                byte[] data = new byte[size];
                try {
                    while ((len = bis.read(data)) != -1) {
                        dos.write(data, 0, len);
                    }

                    System.out.println("데이터보내기 끝 직전");
                    dos.flush();
                    //bis.close();
                    System.out.println("데이터끝");
                    System.out.println("보낸 파일의 사이즈 : " + bitmapToByteArray(b).length);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                int len2 = bitmapToByteArray(c).length;
                bis = new BufferedInputStream(new ByteArrayInputStream(bitmapToByteArray(c)));
                byte[] bsize = new byte[]{
                        (byte) ((len2 >> 24)),
                        (byte) ((len2 >> 16)),
                        (byte) ((len2 >> 8)),
                        (byte) ((len2)),
                };
                dos.write(bsize);
                int size2 = 65535;
                byte[] data2 = new byte[size2];
                try {
                    while ((len2 = bis.read(data2)) != -1) {
                        dos.write(data2, 0, len2);
                    }

                    System.out.println("데이터보내기 끝 직전");
                    dos.flush();
                    //bis.close();
                    System.out.println("데이터끝");
                    System.out.println("보낸 파일의 사이즈 : " + bitmapToByteArray(c).length);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        //send_button = findViewById(R.id.Button01);
        //send_button.setOnClickListener(this);
        (new Thread() {
            public void run() {
                try {

                    connect("221.167.232.253", 8080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void connect(String ip, int port) {
        b = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Client/" + "image.jpg");
        c = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Client/" + "image2.jpg");
        try {
            Log.d("TCP", "server connecting");
            sock = new Socket(ip, port);
            System.out.println("데이터찾는중");
            dos = new DataOutputStream(sock.getOutputStream());
            bis = new BufferedInputStream(new ByteArrayInputStream(bitmapToByteArray(b)));
            checkUpdate.start();
            sendThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) ((value >> 24)),
                (byte) ((value >> 16)),
                (byte) ((value >> 8)),
                (byte) ((value)),
        };
    }

    @Override

    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            sock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}