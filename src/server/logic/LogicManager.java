package server.logic;

import server.Server;
import shared.Protocol;




public class LogicManager 
implements Runnable
{
	private Server server;
	private Thread thread = new Thread(this);
	
	public LogicManager(Server server)
	{
		this.server = server;
	}

	@Override
	public void run() 
	{
		//TODO Implement
		//TODO implement protocol to notify the clients that the game began
	}
	
	public void startGame()
	{
		thread.start();
		server.broadcastMessage(Protocol.GAME_BEGIN.str());
	}
}
