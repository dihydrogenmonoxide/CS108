package test.discovery;

import server.discovery.*;

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
		
		
		try {
			t.join();
			//this waits for ever, as the thread ain't gonna end
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

}
