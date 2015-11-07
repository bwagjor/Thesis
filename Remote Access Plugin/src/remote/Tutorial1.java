package remote;

import mmcorej.CMMCore;


public class Tutorial1 {
   public static void main(String[] args) {
		
      CMMCore core_ = new CMMCore();
      try {
         core_.loadDevice("Camera", "DemoCamera", "DCam");
         core_.initializeDevice("Camera");

         core_.setExposure(50);
         core_.snapImage();
			
         if (core_.getBytesPerPixel() == 1) {
            // 8-bit grayscale pixels
            byte[] img = (byte[])core_.getImage();
            System.out.println("Image snapped, " + img.length + " pixels total, 8 bits each.");
            System.out.println("Pixel [0,0] value = " + img[0]);
         } else if (core_.getBytesPerPixel() == 2){
            // 16-bit grayscale pixels
            short[] img = (short[])core_.getImage();
            System.out.println("Image snapped, " + img.length + " pixels total, 16 bits each.");
            System.out.println("Pixel [0,0] value = " + img[0]);             
         } else {
            System.out.println("Dont' know how to handle images with " +
                  core_.getBytesPerPixel() + " byte pixels.");             
         }
      } catch (Exception e) {
         System.out.println(e.getMessage());
         System.exit(1);
      }		
   }	
}