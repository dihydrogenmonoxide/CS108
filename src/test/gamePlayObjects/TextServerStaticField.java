package test.gamePlayObjects;

import server.MainServer;
import server.players.Player;
import server.server.Server;

public class TextServerStaticField 
{
	private TextServerStaticField()
	{
		
	}
	public static void main(String a[])
	{
		startTestServer();
	}
	
	/**
	 * A fakeserver with a Player on Field 1
	 * @return
	 */
	public static Server startTestServer()
	{
		MainServer.startServer(9009);

		Player p = new Player("derp", null, 101);
		p.setNick("DAFUQ");
		
		Server s = new Server("hurp", 201);
		s.addPlayer(p);
		p.setFieldID(1);
		return s;
	}

}
