
package archive;

import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import mmcorej.CMMCore;

import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;

import remote.TCPServer.OnMessageReceived;

public class RPlugin extends Thread implements MMPlugin {
	public static final String menuName = "Remote Access";
	public static final String description = "MicroView Client";
	public static final int SERVERPORT = 100;
	private boolean running = false;
	private PrintWriter mOut;
	private OnMessageReceived messageListener;
	public BufferedImage currentImage;
	static ByteArrayOutputStream baos;
	static byte[] ImageInBytes;
	int mScope;

	// Provides access to the Micro-Manager Java API (for GUI control and high-
	// level functions).
	private ScriptInterface app_;
	// Provides access to the Micro-Manager Core API (for direct hardware
	// control)
	private CMMCore core_;

	@Override
	public void setApp(ScriptInterface app) {
		app_ = app;
		core_ = app.getMMCore();
		// opens the window where the messages will be received and sent
		CommsWindow frame = new CommsWindow();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		//tute();
		System.out.println(core_.getImageBufferSize());

	}

	@Override
	public void dispose() {
		// We do nothing here as the only object we create, our dialog, should
		// be dismissed by the user.
	}

	@Override
	public void show() {
		mScope = JOptionPane
				.showConfirmDialog(
						null,
						"Welcome To The Remote Access Server for Micromanager and MicroView UQ",
						"Remote Access", JOptionPane.YES_NO_OPTION);
	}

	@Override
	public String getInfo() {
		return "Initiates the TCP/IP Server necessary for the bi-directional communication utilised in the MicrView UQ Application";
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getCopyright() {
		return "Developed at The University of Queensland";
	}

	/**
	 * Method to send the messages from server to client
	 * 
	 * @param message
	 *            the message sent by the server
	 */
	public void sendMessage(String message) {
		if (mOut != null && !mOut.checkError()) {
			mOut.println(message);
			mOut.flush();
		}
	}

	/**
	 * Method to send the Images from server to client
	 * 
	 * @param image
	 *            the image sent by the server
	 */
	public void sendImage(BufferedImage bi) {

	}
	public void testing(){
		try {
			
			core_.loadDevice("TCamera", "DemoCamera", "DCam");
			core_.initializeDevice("TCamera");
			System.out.println("1");
			core_.snapImage();
			System.out.println("2");
			Object img = core_.getImage();
			System.out.println("3");

			long width_ = core_.getImageWidth();
			long height_ = core_.getImageHeight();

			long byteDepth = core_.getBytesPerPixel();

			ShortProcessor ip = new ShortProcessor((int) width_,
					(int) height_);
			ip.setPixels(img);
			ImagePlus imp = new ImagePlus("testing.png", ip);
			//BufferedImage bi = imp.getBufferedImage();
			FileSaver fs = new FileSaver(imp);
			fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test.jpg");
			System.out.println("Image Saved");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void tute() {
		try {
			core_.loadDevice("TCamera", "DemoCamera", "DCam");
			core_.initializeDevice("TCamera");
			core_.setExposure(50);
			core_.snapImage();

			if (core_.getBytesPerPixel() == 1) {
				// 8-bit grayscale pixels
				byte[] img = (byte[]) core_.getImage();

				System.out.println("Image snapped, " + img.length
						+ " pixels total, 8 bits each.");
				System.out.println("Pixel [0,0] value = " + img[0]);
			} else if (core_.getBytesPerPixel() == 2) {
				// 16-bit grayscale pixels
				System.out.println("Trying to take image");
				core_.snapImage();
				Object img = core_.getImage();

				long width_ = core_.getImageWidth();
				long height_ = core_.getImageHeight();

				long byteDepth = core_.getBytesPerPixel();

				ShortProcessor ip = new ShortProcessor((int) width_,
						(int) height_);
				ip.setPixels(img);
				ImagePlus imp = new ImagePlus("testing.png", ip);
				BufferedImage bi = imp.getBufferedImage();
				byte[] imageBytes = ((DataBufferByte) bi.getData()
						.getDataBuffer()).getData();
				FileSaver fs = new FileSaver(imp);
				fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test.jpg");
				System.out.println("Image Saved");

			} else {
				System.out.println("Dont' know how to handle images with "
						+ core_.getBytesPerPixel() + " byte pixels.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		
	}

	/**
	 * Method to convert a stream into a byte array
	 */
	public static byte[] readExactly(InputStream input, int size)
			throws IOException {
		byte[] data = new byte[size];
		int index = 0;
		while (index < size) {
			int bytesRead = input.read(data, index, size - index);
			if (bytesRead < 0) {
				throw new IOException("Insufficient data in stream");
			}
			index += size;
		}
		return data;
	}
	
	@Override
	public void run() {
		super.run();

		running = true;

		try {
			System.out.println("S: Connecting...");

			// create a server socket. A server socket waits for requests to
			// come in over the network.
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);

			// create client socket... the method accept() listens for a
			// connection to be made to this socket and accepts it.
			Socket client = serverSocket.accept();
			tute();
			System.out.println("S: Receiving...gg");
			//byte[] ba = tute();
			//int len = ba.length;
			OutputStream out = client.getOutputStream();
			DataOutputStream dos = new DataOutputStream(out);

			/**dos.writeInt(len);
			if (len > 0) {
				dos.write(ba, 0, len);
			} **/

			try {

				
				// read the message received from client
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));

				// in this while we wait to receive messages from client (it's
				// an infinite loop)
				// this while it's like a listener for messages
				while (running) {
					String message = in.readLine();
					
					if (message != null && messageListener != null) {
						// call the method messageReceived from ServerBoard
						System.out.println(message);
						if (message=="zoom in"){
							core_.setPixelSizeUm("20", 20);
						}
						messageListener.messageReceived(message);
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

	// Declare the interface. The method messageReceived(String message) will
	// must be implemented in the ServerBoard
	// class at on startServer button click
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}

}
