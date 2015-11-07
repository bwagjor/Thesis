package archive;

import javax.swing.*;

import mmcorej.CMMCore;
import remote.CommsWindow;
import remote.Imaging;
import remote.RPlugin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class extends the Thread class so we can receive and send messages at the same time
 */
public class TCPServer extends Thread {

    public static final int SERVERPORT = 100;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;
    private RPlugin plug;
    private Imaging image;
    private CMMCore core_;
    

    public static void main(String[] args) {

        //opens the window where the messages will be received and sent
        CommsWindow frame = new CommsWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
       // image = new Imaging(core_);

    }

    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener, CMMCore core_) {
        this.messageListener = messageListener;
        this.core_ = core_;
        image = new Imaging(core_);
        CommsWindow frame = new CommsWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        System.out.println("RECEIVED!!");
    }
    
    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
       
;
        System.out.println("RECEIVED");
    }

    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public void run() {
        super.run();

        running = true;

        try {
            System.out.println("S: Connecting...");

            //create a server socket. A server socket waits for requests to come in over the network.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);
            System.out.println("Waiting to find client");
            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            Socket client = serverSocket.accept();
            System.out.println("S: Receiving... this is it");

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                //read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                        System.loadLibrary("MMCoreJ_wrap");
                        core_ = new CMMCore();
                        core_.loadDevice("TCamera", "DemoCamera", "DCam");
                        core_.initializeAllDevices();
            			core_.setExposure(50);

                        image = new Imaging(core_);
                        if (message.equals("high exposure")) image.CaptureImageE();
                        else image.CaptureImage();
                        /*image.capture1();
                        if(image.capture2()) System.out.println("IMAGE SAVED");
                        else
                        System.out.println("FAILED");*/
                
                    }
                }

            } catch (Exception e) {
                System.out.println("S: Error");
                e.printStackTrace();
            } finally {
                client.close();
                System.out.println("S: Done.");
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}