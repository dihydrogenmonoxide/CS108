package test.discovery;

import server.net.*;

public class Server {

	/**
	 * Testserver, so I can run the client test on another machine
	 * 
	 * @param args does nothing
	 */
	public static void main(String[] args) 
	{
		DiscoveryServer s = new DiscoveryServer(10000);
		Thread t = new Thread(s);
		t.start();
		
	/*	DiscoveryServer ss = new DiscoveryServer(10001);
		Thread tt = new Thread(ss);
		tt.start();
		
		DiscoveryServer sss = new DiscoveryServer(10002);
		Thread ttt = new Thread(sss);
		ttt.start();
		
		DiscoveryServer ssss = new DiscoveryServer(10003);
		Thread ft = new Thread(ssss);
		ft.start();*/
		
		
		try {
			t.join();
			//this waits for ever, as the thread ain't gonna end
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}		
	}

}
