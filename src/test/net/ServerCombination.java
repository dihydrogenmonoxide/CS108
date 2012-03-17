package test.net;

import server.net.DiscoveryServer;
import server.net.Serversocket;
import server.parser.Parser;
import shared.SocketCreationException;

public class ServerCombination {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create discovery Server
		DiscoveryServer discS = new DiscoveryServer(9002);
		Thread t = new Thread(discS);
		t.start();
		
		//create socket Server
		Parser p = new Parser();
		try {
			Serversocket sockS = new Serversocket(9002, p);
			sockS.start_();
		} catch (SocketCreationException e) {
			e.printStackTrace();
		}
		
	}

}
