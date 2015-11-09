package remote;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.micromanager.api.ScriptInterface;

import mmcorej.CMMCore;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class extends the Thread class so we can receive and send messages at the
 * same time
 */
public class ImageTCPServer extends Thread {

	public static final int SERVERPORT = 100;
	//private boolean running = false;
	private OnMessageReceived messageListener;
	private Imaging image;
	//public CMMCore core_;
	private BufferedImage bi;
	private final ScriptInterface app_;
	private final CMMCore core_;
	//private final Preferences prefs_;

	public static void main(String[] args) {

		// opens the window where the messages will be received and sent
		//CommsWindow frame = new CommsWindow(app_);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.pack();
		//frame.setVisible(true);
		// image = new Imaging(core_);

	}

	/**
	 * Constructor of the class
	 * 
	 * @param messageListener
	 *            listens for the messages
	 * @param core_
	 *            The core from the gui
	 * @throws Exception 
	 */
	public ImageTCPServer(OnMessageReceived messageListener, ScriptInterface app_) throws Exception {
		this.messageListener = messageListener;
		this.app_ = app_;
	    core_ = app_.getMMCore();
	   // System.out.println(core_.getCameraDevice());
		//this.core_ = core_;
		//image = new Imaging(core_);
		/*CommsWindow frame = new CommsWindow(core_);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		System.out.println("Setup JFrame");*/
	}

/*	*//**
	 * Constructor of the class
	 * 
	 * @param messageListener
	 *            listens for the messages
	 *//*
	public ImageTCPServer(OnMessageReceived messageListener) {
		this.messageListener = messageListener;

		
		System.out.println("Constructor - message");
	}*/

	@Override
	public void run() {
		super.run();


		try {
			// create a server socket. A server socket waits for requests to
			// come in over the network.
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);
			System.out.println("Wating for new client");
			// create client socket... the method accept() listens for a
			// connection to be made to this socket and accepts it.
			Socket client = serverSocket.accept();
			System.out.println("New Client Found");
			
			try {

				// read the message received from client
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				// in this while we wait to receive messages from client (it's
				// an infinite loop)
				// this while it's like a listener for messages
				//SocketTimer st = new SocketTimer(serverSocket,client,this);
				//if (this.isInterrupted()) throw new Exception("Timeout");
				while (in.read()!=-1) {
					
					
					//st.cancel();
					String message = in.readLine();
					if (message != null && messageListener != null) {
						// call the method messageReceived from ServerBoard
						// class
						messageListener.messageReceived(message);
						takeImage(message, client);
						//st = new SocketTimer(serverSocket,client,this);
					}
				}

			} catch (Exception e) {
				System.out.println("Error detected, most probably disconnected");
				e.printStackTrace();
			} finally {
				client.shutdownOutput();
				client.close();
				serverSocket.close();
				System.out.println("Closing this socket and reopening");
				run();
			}

		} catch (Exception e) {
			System.out.println("Server Error");
			e.printStackTrace();
		}

	}

	private void takeImage(String message, Socket client) throws Exception {
		//System.loadLibrary("MMCoreJ_wrap");
		//core_ = new CMMCore();
		/*core_.loadDevice("TCamera", "DemoCamera", "DCam");
		core_.initializeAllDevices();
		core_.setExposure(50);*/
		core_.snapImage();
		image = new Imaging(core_);
		bi = image.CaptureBufferedImage(message);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		sendBytes(imageInByte, client);
	}

	public void sendBytes(byte[] myByteArray, Socket socket) throws IOException {
		sendBytes(myByteArray, 0, myByteArray.length, socket);
	}

	public void sendBytes(byte[] myByteArray, int start, int len, Socket socket) throws IOException {
		if (len < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + start);
		// Other checks if needed.

		// May be better to save the streams in the support class;
		// just like the socket variable.
		OutputStream out = socket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeInt(len);
		if (len > 0) {
			dos.write(myByteArray, start, len);
		}
	}

	// Declare the interface. The method messageReceived(String message) will
	// must be implemented in the ServerBoard
	// class at on startServer button click
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}

}