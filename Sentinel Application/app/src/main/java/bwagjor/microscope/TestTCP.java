package bwagjor.microscope;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TestTCP {

    ImageView imageView;
    public static final String SERVERIP = "192.168.1.5"; //your computer IP address
    public static final int SERVERPORT = 100;
    public boolean mRun=false;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;}

/*

InetAddress serverAddr = InetAddress.getByName(SERVERIP);
socket = new Socket(serverAddr, SERVERPORT);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        int len = dataInputStream.readInt();
        byte[] ba= new byte [len];
        if (len>0){
        dataInputStream.readFully(ba);
        }

        Log.d("St--", ":" + ba.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(ba, 0,
        ba.length);

        imageView.setImageBitmap(bitmap);*/