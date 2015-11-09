
package remote;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ShortProcessor;
import mmcorej.CMMCore;

public class RPlugin extends Thread implements MMPlugin {
	public static final String menuName = "Remote Access";
	public static final String description = "MicroView Client";
	public static final int SERVERPORT = 100;
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
	private CommsWindow frame;
	@Override
	public void setApp(ScriptInterface app) {
		//Initiate the core and application objects
		app_ = app;
		core_ = app.getMMCore();
		//Open the GUI
		CommsWindow frame = new CommsWindow(app_);
		System.out.println("HERE IS ME SETING THE APP");
	/*	try {
			ImageTCPServer server = new ImageTCPServer(null, app_);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void show() {
		//Originally to be used for selection between multi user streaming and single user control
		/*mScope = JOptionPane.showConfirmDialog(null,
				"Welcome To The Remote Access Server for Micromanager and MicroView UQ", "Remote Access",
				JOptionPane.YES_NO_OPTION);
		//ImageTCPServer server = new ImageTCPServer(null, core_);*/
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
	 * Method to send the Images from server to client
	 * 
	 * @param image
	 *            the image sent by the server
	 */
	public void sendImage(BufferedImage bi) {

	}

	//Depreciated class used for testing early camera communication
	public void tute() {
		try {
			core_.loadDevice("TCamera", "ProgRes", "Jenoptik-ProgRes");
			//core_.loadDevice("TCamera", "DemoCamera", "DCam");
			core_.initializeDevice("TCamera");
			core_.setExposure(50);
			core_.snapImage();
			System.out.println(core_.getBytesPerPixel());
			if (core_.getBytesPerPixel() == 1) {
				// 8-bit grayscale pixels
				byte[] img = (byte[]) core_.getImage();

				System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
				System.out.println("Pixel [0,0] value = " + img[0]);
				
			} else if (core_.getBytesPerPixel() == 2) {
				// 16-bit grayscale pixels
				System.out.println("Trying to take image 2 bytes per pixel");
				core_.snapImage();
				Object img = core_.getImage();

				long width_ = core_.getImageWidth();
				long height_ = core_.getImageHeight();

				long byteDepth = core_.getBytesPerPixel();

				ShortProcessor ip = new ShortProcessor((int) width_, (int) height_);
				ip.setPixels(img);
				ImagePlus imp = new ImagePlus("testing.png", ip);
				BufferedImage bi = imp.getBufferedImage();
				byte[] imageBytes = ((DataBufferByte) bi.getData().getDataBuffer()).getData();
				FileSaver fs = new FileSaver(imp);
				fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\SETUP.jpg");
				System.out.println("Image Saved");
				String info = core_.getVersionInfo();
				System.out.println(info);

			} else {
				System.out
						.println("Dont' know how to handle images with " + core_.getBytesPerPixel() + " byte pixels.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

	
	 // Method to convert a stream into a byte array
	 
	public static byte[] readExactly(InputStream input, int size) throws IOException {
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

	public CMMCore getCore() {
		return core_;

	}

}
