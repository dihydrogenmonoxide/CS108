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
	private boolean b_quit = false;
	/**
	 * Creates a new Player on the Server;
	 * @param s_ID The unique token the Server assigned
	 */
	public Player(String s_ID, PlayerSocket ps_sock, int i_ID)
	{
		this.s_PlayerToken = s_ID;
		this.ps_sock = ps_sock;
		MainServer.getPlayerManager().addPlayer(this);
		//TODO : free a number once someone quits and make sure there aren't more then 99 players
		this.i_ID = i_ID+100;
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
		this.s_Nick = s_Nick;
		if(!b_NameSet)
		{
			MainServer.printInformation("The Player with the ID "+this.i_ID+" set his name to \'"+s_Nick+"\'");
			for(Player play : MainServer.getPlayerManager().getPlayers())
			{
				if(play != this)
					ps_sock.sendData("VNICK "+play.getID()+" "+play.getNick());
			}
			MainServer.getPlayerManager().broadcastMessage_everyone("LJOIN "+ps_sock.getPlayer().getID()+" "+ps_sock.getPlayer().getNick());
		}
		else
		{
			MainServer.printInformation("The Player with the ID "+this.i_ID+" changed his name: \'"+this.s_Nick+"\' -> \'"+s_Nick+"\'");
		}
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
	 * @return the playerID
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
	 * sets the server for the current player
	 * @param s_serv the server
	 */
	public void setServer(Server s_serv) 
	{
		this.s_server = s_serv;
	}

	/**
	 * Sends the Data to the specified Player
	 * @param s_MSG the data
	 */
	public void sendData(String s_MSG)
	{
		ps_sock.sendData(s_MSG);
	}
	
	/**
	 * Returns whether the player is in the lobby or not
	 * @return whether the player is in the lobby or not
	 */
	public boolean isInLobby()
	{
		return (this.s_server == null);
	}

	/**
	 * This gets called when the Player lost the connection and may tries to reconnect
	 */
	public void connectionLost()
	{
		MainServer.printInformation("The Player "+this.getNick()+" lost the connection - pausing and waiting for reconnect");
		MainServer.getPlayerManager().broadcastMessage("CCHAT [SERVER]\t"+this.s_Nick+" lost the connection - trying to reconnect!", this);
		//TODO implement
	}

	/**
	 * This is called when the player reconnects
	 * @param ps_socket the new socket
	 */
	public void reconnect(PlayerSocket ps_socket) 
	{
		// TODO what to do when the player reconnects?
		this.ps_sock = ps_socket;
		MainServer.getPlayerManager().broadcastMessage("CCHAT [SERVER]\t"+this.s_Nick+" reconnected!", this);
		
	}

	/**
	 * This is called when the player disconnects
	 */
	public void disconnect() 
	{
		// TODO what to send out when a player quits?
		if(!b_quit)
		{
			MainServer.getPlayerManager().removePlayer(this);
			
			MainServer.getPlayerManager().broadcastMessage("CCHAT [SERVER]\t"+this.s_Nick+" quit.", this);
			MainServer.getPlayerManager().broadcastMessage_everyone("LQUIT "+this.i_ID+" "+this.s_Nick);
			b_quit = true;
		}
	}
}
