package test.gamePlayObjects;

import server.MainServer;
import server.players.Player;
import server.server.Server;

public class TestServer 
{
	private TestServer()
	{
		
	}
	public static void main(String a[])
	{
		startTestServer();
	}
	
	public static Server startTestServer()
	{
		MainServer.startServer(9009);
		for(int i = 1; i != 6; i++)
		{
			Player p = new Player("derp", null, i+100);
		}
		
		Server s = new Server("hurp", 201, 10000, 10000);
		for(Player p : MainServer.getPlayerManager().getPlayers())
		{
			s.addPlayer(p);
		}
		return s;
	}

}
