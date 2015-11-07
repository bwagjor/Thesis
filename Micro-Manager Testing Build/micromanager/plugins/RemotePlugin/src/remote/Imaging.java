package remote;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.FileSaver;
import ij.plugin.filter.LutApplier;
import ij.plugin.frame.ColorPicker;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import mmcorej.CMMCore;

public class Imaging {
	private final CMMCore core_;

	public Imaging(CMMCore core_) {
		this.core_ = core_;
	}

	public boolean CaptureImage() throws Exception {
		core_.loadDevice("Camera", "DemoCamera", "DCam");
		core_.initializeAllDevices();
		core_.setExposure(50);
		core_.snapImage();
		Object image = null;
		try {
			core_.snapImage();
			image = core_.getImage();
		} catch (NullPointerException e) {
			throw new NullPointerException("Error: No live CMMCore!");
		}

		int width = (int) core_.getImageWidth();
		int height = (int) core_.getImageHeight();
		ImageProcessor ip;
		if (image instanceof byte[]) {
			ip = new ByteProcessor(width, height);
			ip.setPixels((byte[]) image);
		} else if (image instanceof short[]) {
			ip = new ShortProcessor(width, height);
			ip.setPixels((short[]) image);
		} else
			return false;

		System.out.println("Trying To IMAGE");
		ImagePlus imp = new ImagePlus("testing2.jpg", ip);
		FileSaver fs = new FileSaver(imp);
		return fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test.jpg");

	}

	public BufferedImage CaptureBufferedImage(String message) throws Exception {
		String[] commands = message.split(",");

		int exposure = Integer.parseInt(commands[0]);
		String lut = commands[1];
	
		core_.setExposure(exposure);
		// core_.snapImage();
		Object image = null;
		try {
			core_.snapImage();
			image = core_.getImage();
		} catch (NullPointerException e) {
			throw new NullPointerException("Error: No live CMMCore!");
		}

		int width = (int) core_.getImageWidth();
		int height = (int) core_.getImageHeight();
		ImageProcessor ip;
		if (image instanceof byte[]) {
			ip = new ByteProcessor(width, height);
			ip.setPixels((byte[]) image);
		} else if (image instanceof short[]) {
			ip = new ShortProcessor(width, height);
			ip.setPixels((short[]) image);
		} else
			throw new Exception("Don't know how to handle these images");
		ImagePlus imp = new ImagePlus("testing2.jpg", ip);
		if (!lut.equals("None")) {
			WindowManager.setTempCurrentImage(imp);
			IJ.run(lut);
		}

		BufferedImage bi = imp.getBufferedImage();
		// FileSaver fs = new FileSaver(imp);
		// fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New
		// folder\\testRUN.jpg");
		return bi;

	}

	public boolean CaptureImageE() throws Exception {
		core_.loadDevice("Camera", "DemoCamera", "DCam");
		core_.initializeAllDevices();
		core_.setExposure(100);
		Object image = null;
		try {
			core_.snapImage();
			image = core_.getImage();
		} catch (NullPointerException e) {
			throw new NullPointerException("Error: No live CMMCore!");
		}

		int width = (int) core_.getImageWidth();
		int height = (int) core_.getImageHeight();
		ImageProcessor ip;
		if (image instanceof byte[]) {
			ip = new ByteProcessor(width, height);
			ip.setPixels((byte[]) image);
		} else if (image instanceof short[]) {
			ip = new ShortProcessor(width, height);
			ip.setPixels((short[]) image);
		} else
			return false;

		System.out.println("Trying to take image with high exposure");
		ImagePlus imp = new ImagePlus("testing2.jpg", ip);
		FileSaver fs = new FileSaver(imp);
		return fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test.jpg");

	}

	public boolean capture1() {
		try {
			core_.snapImage();

			if (core_.getBytesPerPixel() == 1) {
				// 8-bit grayscale pixels
				System.out.println("WE got HERE #01");
				byte[] img2 = (byte[]) core_.getImage();

				long width_ = core_.getImageWidth();
				long height_ = core_.getImageHeight();

				long byteDepth = core_.getBytesPerPixel();

				ShortProcessor ip = new ShortProcessor((int) width_, (int) height_);
				ip.setPixels(img2);
				ImagePlus imp = new ImagePlus("testing.png", ip);
				BufferedImage bi = imp.getBufferedImage();
				byte[] imageBytes = ((DataBufferByte) bi.getData().getDataBuffer()).getData();
				FileSaver fs = new FileSaver(imp);
				fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\testTute.jpg");
				System.out.println("Image Saved");
				String info = core_.getVersionInfo();
				System.out.println(info);
				return true;
			} else if (core_.getBytesPerPixel() == 2) {
				// 16-bit grayscale pixels
				System.out.println("WE got HERE #02");
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
				fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\testTute.jpg");
				System.out.println("Image Saved");
				String info = core_.getVersionInfo();
				System.out.println(info);
				return true;

			} else {
				System.out
						.println("Dont' know how to handle images with " + core_.getBytesPerPixel() + " byte pixels.");
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return true;

	}

	public boolean capture2() {
		try {
			core_.snapImage();
			System.out.println("2");
			Object img = core_.getImage();
			System.out.println("3");

			long width_ = core_.getImageWidth();
			long height_ = core_.getImageHeight();

			long byteDepth = core_.getBytesPerPixel();

			ShortProcessor ip = new ShortProcessor((int) width_, (int) height_);
			ip.setPixels(img);
			ImagePlus imp = new ImagePlus("testing.png", ip);
			// BufferedImage bi = imp.getBufferedImage();
			FileSaver fs = new FileSaver(imp);
			fs.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test2.jpg");
			System.out.println("Image Saved");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
