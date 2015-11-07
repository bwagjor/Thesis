package remote;

import ij.ImagePlus;
import ij.io.FileSaver;
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
		Object image = null;
		System.out.println("1");
		try {
			System.out.println("2");
			core_.snapImage();
			System.out.println("3");
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
		return fs
				.saveAsJpeg("C:\\Users\\benja_000\\Desktop\\New folder\\test.jpg");

	}

	public boolean capture2() {
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
