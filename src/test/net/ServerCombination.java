package test.net;

import client.MainClient;
import server.MainServer;


public class ServerCombination
{
	public static void main(String[] args)
	{
		MainServer.startServer(9003);
		
		new MainClient();
		// sorry, not supported
		//new MainClient();
	}
}
