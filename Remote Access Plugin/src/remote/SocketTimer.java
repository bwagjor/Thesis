package remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class used to track the time since the last image was requested by a Sentinel client
 */

public class SocketTimer {
	ServerSocket server;
	Socket client;
	ImageTCPServer ITS;
	Timer timer;

	public SocketTimer(ServerSocket server, Socket client, ImageTCPServer ITS) {
		this.server = server;
		this.client = client;
		this.ITS = ITS;
		timer = new Timer();
		System.out.println("should be starting timing");
		timer.schedule(new TimeTask(), 3000);
	}
	public void cancel(){
		System.out.println("CANCELED");
		timer.cancel();
	}

	class TimeTask extends TimerTask {
		public void run() {
			System.out.println("Time out - resetting socket");
			/*client.shutdownOutput();
			client.close();
			server.close();*/
			ITS.interrupt();
			//ITS.run();
			//timer.cancel();
			
		}
	}

}
