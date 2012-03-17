package server.players;

import server.MainServer;
import server.Server;
import server.net.*;

public class Player 
implements Comparable<Player>
{
	private String s_Nick = "JohnDoe";
	private String s_PlayerToken;
	private Server s_server;
	private PlayerSocket ps_sock;
	private int i_ID;
	private boolean b_NameSet = false;
	
	private static int i_numplayers = 0;
	
	/**
	 * Creates a new Player on the Server;
	 * @param s_ID The unique token the Server assigned
	 */
	public Player(String s_ID, PlayerSocket ps_sock)
	{
		this.s_PlayerToken = s_ID;
		this.ps_sock = ps_sock;
		MainServer.getPlayerManager().addPlayer(this);
		this.i_ID = i_numplayers++;
		MainServer.printInformation("A New Player connected - Assigned ID: "+this.i_ID);
	}

	@Override
	public int compareTo(Player o)
	{
		return o.i_ID-this.i_ID;
	}
	
	/**
	 * 
	 * @param s_Nick The nickname you'd like to assign to the specified player
	 */
	public void setNick(String s_Nick)
	{
		if(b_NameSet)
			MainServer.printInformation("The Player with the ID "+this.i_ID+" set his name to \'"+s_Nick+"\'");
		else
			MainServer.printInformation("The Player with the ID "+this.i_ID+" changed his name: \'"+this.s_Nick+"\' -> \'"+s_Nick+"\'");
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
	 * This is a number the player and his actions can be linked to. 
	 * @return the unique playerID
	 */
	public int getID()
	{
		return this.i_ID;
	}
	
	/**
	 * Returns the Players UUID
	 * @return the token
	 */
	public String getToken()
	{
		return this.s_PlayerToken;
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
	
	public boolean isInLobby()
	{
		return (this.s_server == null);
	}

	/**
	 * This gets called when the Player lost the connection and may tries to reconnect
	 */
	public void connectionLost()
	{
		//TODO implement
	}


}
