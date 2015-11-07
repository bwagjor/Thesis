package bwagjor.microscope;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MicroTCPClient {

    private String serverMessage;

    //public static final String SERVERIP = "192.168.1.11"; //controlling computer IP address
    //public static final String SERVERIP = "10.89.188.92"; //controlling computer IP address
    public static final int SERVERPORT = 100;//The port the server is transmitting via
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    String SERVERIP;
    private byte[] ba;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public MicroTCPClient(OnMessageReceived listener,String IP) {
        mMessageListener = listener;
        this.SERVERIP=IP;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
        //socket.close();
    }

    public void run() {

        mRun = true;

        try {

            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

           // Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

               // Log.e("TCP Client", "C: Sent.");

               // Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    ba =readBytes(socket);

                    if (ba.length != 0 && !ba.equals(null) ) {
                        //call the method messageReceived from MyActivity class
                        Bitmap bitmap = BitmapFactory.decodeByteArray(ba, 0, ba.length);
                        mMessageListener.messageReceived(bitmap);
                    }
                    serverMessage = null;

                }

                //Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                //Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                System.out.println("Closing Socket");
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    public byte[] readBytes(Socket socket) throws IOException {
        // Again, probably better to store these objects references in the support class
        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        int len = dis.readInt();
        byte[] data = new byte[len];
        if (len > 0) {
            dis.readFully(data);
        }
        return data;
    }
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(Bitmap image);
    }
}