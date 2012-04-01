package server.logic;

import server.Server;




public class LogicManager 
extends Thread
{
	private Server server;
	
	public LogicManager(Server server)
	{
		this.server = server;
		this.start();
	}

}
