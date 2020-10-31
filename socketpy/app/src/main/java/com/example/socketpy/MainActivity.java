package com.example.socketpy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int BUFFER_SIZE = 1024;
    Socket sock;
    Button send_button;
    DataOutputStream dos;
    BufferedInputStream bis;
    InputStream dis;
    Bitmap b,c;
    int result = 0;
    byte[] w = new byte[1024];
    private Thread checkUpdate = new Thread() {
        @Override
        public void run() {
            try {
                dis = sock.getInputStream();
                while (true) {
                    result = dis.read();
                    Log.w("1번사진", String.valueOf(result));
                    break;
                }
                while (true) {
                    result = dis.read();
                    Log.w("2번사진", String.valueOf(result));
                    break;
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
        send_button = findViewById(R.id.Button01);
        send_button.setOnClickListener(this);
        (new Thread() {
            public void run() {
                try {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                    connect("192.168.43.244", 8080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        (new Thread() {
            public void run() {
                try {
                    int len = bitmapToByteArray(b).length;
                    byte[] bsize = new byte[]{
                            (byte) ((len >> 24)),
                            (byte) ((len >> 16)),
                            (byte) ((len >> 8)),
                            (byte) ((len)),
                    };
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
                    Thread.sleep(5000);
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
        }).start();
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