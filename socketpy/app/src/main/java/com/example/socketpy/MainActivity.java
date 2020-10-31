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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int BUFFER_SIZE = 1024;
    Socket sock;
    Button send_button;
    DataOutputStream dos;
    BufferedInputStream bis;
    InputStream dis;
    Bitmap b;
    int result = 0;
    byte[] w = new byte[1024];
    private Thread checkUpdate = new Thread() {
        @Override
        public void run() {
            try {
                dis = sock.getInputStream();
                while (true) {
                    //데이터 수신 （5)
                    result = dis.read(w);
                    if (result <= 0) continue;
                    System.out.println(result);
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
                    connect("221.167.232.253", 8080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void connect(String ip, int port) {
        b = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/Client/" + "image.jpg");
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
                    int len;
                    int size = 65535;
                    byte[] data = new byte[size];
                    try {
                        while ((len = bis.read(data)) != -1) {
                            dos.write(data, 0, len);
                        }
                        System.out.println("데이터보내기 끝 직전");
                        dos.flush();
                        dos.close();
                        bis.close();
                        System.out.println("데이터끝");
                        System.out.println("보낸 파일의 사이즈 : " + bitmapToByteArray(b).length);
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