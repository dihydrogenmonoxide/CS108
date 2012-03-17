package server.players;

import server.MainServer;
import server.Server;
import server.net.*;

public class Player 
implements Comparable<Player>
{
	private String s_Nick = "JohnDoe";
	private String s_ID;
	private Server s_server;
	private PlayerSocket ps_sock;
	
	/**
	 * Creates a new Player on the Server;
	 * @param s_ID The unique playerID the Server assigned
	 */
	public Player(String s_ID, PlayerSocket ps_sock)
	{
		this.s_ID = s_ID;
		this.ps_sock = ps_sock;
		MainServer.getPlayerManager().addPlayer(this);
	}

	@Override
	public int compareTo(Player o)
	{
		// TODO Implement compareto function
		return 0;
	}
	
	/**
	 * 
	 * @param s_Nick The nickname you'd like to assign to the specified player
	 */
	public void setNick(String s_Nick)
	{
		this.s_Nick = s_Nick;
	}

	/**
	 * 
	 * @return The Players current nickname
	 */
	public String getNick() 
	{
		return s_Nick;
	}
	
	/**
	 * 
	 * @return the unique playerID
	 */
	public String getID()
	{
		return this.s_ID;
	}

	/**
	 * Returns the Players current server or 'null' if the Player is in the Lobby
	 * @return the Server
	 */
	public Server getServer() 
	{
		return this.s_server;
	}

	/**
	 * Sends the Data to the specified Player
	 * @param s_MSG the data
	 */
	public void sendMessage(String s_MSG)
	{
		ps_sock.sendData(s_MSG);
	}


}
