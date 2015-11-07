package remote;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;

abstract class ScreenCapture implements Runnable{

	 static BufferedImage screencapture;
	 static ByteArrayOutputStream baos;
	 static byte[] ImageInBytes;

	   public static void main(String args[]) throws
	       AWTException, IOException, InterruptedException {

	  // Open your connection to a server, at port 1234
	  final Socket ClientSocket = new Socket("localhost",1234);

	  final DataOutputStream dos= new DataOutputStream(ClientSocket.getOutputStream());
	  DataInputStream in = new DataInputStream(ClientSocket.getInputStream() );
	  baos = new ByteArrayOutputStream();

	  try{
	      //First thread that is Taking screenshot
	      Thread TakeScreenShotthread = new Thread () 
	      {
	          public void run () {        

	          // Capture Screen using BufferedImage Library
	           try {
	               screencapture = new Robot().createScreenCapture(
	               new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) );
	               System.out.println("thread1 is running...");

	            } catch (HeadlessException e) {

	                    e.printStackTrace();
	            } catch (AWTException e) {

	                    e.printStackTrace();
	            }
	        } 
	    };

	    //Thread 2 that is Sending Screenshot to server
	    Thread sendingScreenShotThread =new Thread () {
	          public void run () {
	              //Sending Screen to Server
	               try {
	                      ImageIO.write(screencapture, "jpg", baos);
	                      ImageInBytes = baos.toByteArray();
	                      dos.write(ImageInBytes);
	                     // File Rif = new File(System.currentTimeMillis() + ".jpg");
	                      //ImageIO.write(screencapture, "jpg", Rif);
	                      System.out.println("thread2 is running...");

	                } catch (IOException e) {
	                e.printStackTrace();
	                }       
	               finally{

	                    try {
	                        baos.flush();
	                    } catch (IOException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                }          
	          }
	        };
	        TakeScreenShotthread.start();
	        TakeScreenShotthread.sleep(1000);
	        sendingScreenShotThread.start();
	        sendingScreenShotThread.sleep(1000);
	        TakeScreenShotthread.run();
	        sendingScreenShotThread.run();
	  }finally
	  {
	       //Closing Clients
	                in.close();
	                baos.close();
	                ClientSocket.close();
	  }  
	  }
	 }
