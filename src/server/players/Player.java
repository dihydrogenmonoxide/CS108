package server.players;

import java.util.EmptyStackException;
import java.util.Stack;

import server.MainServer;
import server.Server;
import server.GamePlayObjects.GamePlayObject;
import server.net.*;
import shared.Log;
import shared.Protocol;
import shared.Settings;

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
	private boolean b_ConnectionLost = false;
	private int fieldID = 0;
	private long money = 0;
	private long population = 0;
	private boolean voted = false;
	private boolean finishedBuilding = false;
	private Stack<GamePlayObject> objectStack = new Stack<GamePlayObject>();
	
	/**
	 * Creates a new Player on the Server;
	 * @param s_ID The unique token the Server assigned
	 */
	public Player(String s_ID, PlayerSocket ps_sock, int i_ID)
	{
		this.s_PlayerToken = s_ID;
		this.ps_sock = ps_sock;
		MainServer.getPlayerManager().addPlayer(this);
		this.i_ID = i_ID+100;
		MainServer.printInformation("A New Player connected - Assigned ID: "+this.i_ID);
	}
	
	
	/**
	 * Sets the FieldID
	 * @param ID the field ID
	 */
	public void setFieldID(int ID)
	{
		fieldID = ID;
	}

	@Override
	public int compareTo(Player o)
	{
		return o.i_ID-this.i_ID;
	}
	
	/**
	 * Returns the Field ID if the Player is on a Server (otherwise returns 0)
	 * @return the field ID
	 */
	public int getFieldID()
	{
		return this.fieldID;
	}
	
	/**
	 * Returns the amount of money a player owns
	 * @return the amount of money
	 */
	public long getMoney()
	{
		return money;
	}
	
	/**
	 * Removes the specified amount of money off this players account (does not check whether this player still owns, this is checked by the object constructor)
	 * @param money removes this amount of money off the players account
	 */
	public void removeMoney(long money)
	{
		this.money -= money;
	}
	
	/**
	 * Adds this amount of money the the players account
	 * @param money adds it to the players account
	 */
	public void addMoney(long money)
	{
		this.money += money;
	}
	
	/**
	 * gets the population count
	 * @return the population
	 */
	public long getPopulation()
	{
		return population;
	}
	
	/**
	 * Removes the specified population off a players field
	 * @param deaths the population to remove
	 */
	public void removePopulation(long deaths)
	{
		population -= deaths;
	}
	
	/**
	 * Adds the specified amount of people to the population
	 * @param births the amount of people to add
	 */
	public void addPopulation(long births)
	{
		population += births;
	}
	
	/**
	 * 
	 * @param s_Nick The nickname you'd like to assign to the specified player
	 */
	public void setNick(String s_Nick)
	{
		String s_oldNick = this.s_Nick;
		this.s_Nick = s_Nick;
		if(!b_NameSet)
		{
			MainServer.printInformation("The Player with the ID "+this.i_ID+" set his name to \'"+s_Nick+"\'");
			for(Player play : MainServer.getPlayerManager().getPlayers())
			{
				if(play != this)
				{
					if(play.isInLobby())
						ps_sock.sendData(Protocol.LOBBY_JOIN.str()+play.getID()+" "+play.getNick());
					ps_sock.sendData(Protocol.CON_NICK.str()+play.getID()+" "+play.getNick());
				}
					
					
				
			}
			for(Server s : MainServer.getServerManager().getServers())
			{
				ps_sock.sendData(Protocol.GAME_BROADCAST.str()+s.getID()+" "+s.getPlayerAmount()+"  "+s.getServername());
				for(Player p : s.getPlayers())
				{
					ps_sock.sendData(Protocol.GAME_JOIN.str()+s.getID()+" "+p.getID()+" "+p.getNick()); 
				}
			}
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_JOIN.str()+ps_sock.getPlayer().getID()+" "+ps_sock.getPlayer().getNick());
			MainServer.getGUI().addPlayer(this);
			b_NameSet = true;
		}
		else
		{
			MainServer.printInformation("The Player with the ID "+this.i_ID+" changed his name: \'"+s_oldNick+"\' -> \'"+s_Nick+"\'");
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
		try
		{
			ps_sock.sendData(s_MSG);
		}
		catch(NullPointerException e)
		{
			Log.ErrorLog("Player "+s_Nick+"("+i_ID+") isn't connected to this server it seems");
		}
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
		MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.s_Nick+" lost the connection - trying to reconnect!", this);

		b_ConnectionLost = true;
		ps_sock.close();
		if(this.s_server != null)
			this.s_server.pause();
		try 
		{
			Thread.sleep(Settings.SocketTimeout.TIMEOUT*Settings.SocketTimeout.MAX_RETRIES);
		}
		catch (InterruptedException e)
		{
			Log.WarningLog("Failed to sleep and therefore immediatly disconnected the player");
		}
		
		if(this.b_ConnectionLost)
		{
			if(this.s_server != null)
			{
				this.s_server.removePlayer(this);
				this.s_server.resume();
			}
			this.disconnect();
		}
			
	}

	/**
	 * This is called when the player reconnects
	 * @param ps_socket the new socket
	 */
	public void reconnect(PlayerSocket ps_socket) 
	{
		b_ConnectionLost = true;
		this.ps_sock = ps_socket;
		MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.s_Nick+" reconnected!", this);
		if(this.s_server != null)
			this.s_server.resume();
		
		for(Player play : MainServer.getPlayerManager().getPlayers())
		{
			if(play != this)
			{
				if(play.isInLobby())
					ps_sock.sendData(Protocol.LOBBY_JOIN.str()+play.getID()+" "+play.getNick());
				ps_sock.sendData(Protocol.CON_NICK.str()+play.getID()+" "+play.getNick());
			}			
		}
		for(Server s : MainServer.getServerManager().getServers())
		{
			ps_sock.sendData(Protocol.GAME_BROADCAST.str()+s.getID()+" "+s.getPlayerAmount()+"  "+s.getServername());
			for(Player p : s.getPlayers())
			{
				ps_sock.sendData(Protocol.GAME_JOIN.str()+s.getID()+" "+p.getID()+" "+p.getNick()); 
			}
		}
	}

	/**
	 * This is called when the player disconnects
	 */
	public synchronized void disconnect() 
	{
		if(!b_quit)
		{
			MainServer.getGUI().removePlayer(this);
			b_quit = true;
			MainServer.getPlayerManager().removePlayer(this);
			if(this.b_ConnectionLost)
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.s_Nick+" timed out.", this);
			else
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.s_Nick+" quit.", this);
			
			if(this.isInLobby())
				MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_QUIT.str()+this.i_ID+" "+this.s_Nick);
			else
				MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_QUIT.str()+this.s_server.getID()+" "+this.i_ID+" "+this.s_Nick);
		
			ps_sock.close();
		}
	}
	
	
	@Override
	public String toString()
	{
		return this.s_Nick;
		
	}
	
	/**
	 * casts this {@link Player}'s vote to start the {@link Server}
	 */
	public void voteStart()
	{
		if(voted)
		{
			sendData(Protocol.CON_ERROR+"Already voted!");
		}
		else
		{
			if(s_server == null)
			{
				sendData(Protocol.CON_ERROR+"Can't vote as you're not in a server");
			}
			else
			{
				s_server.addVote();
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE+"\t"+this.getNick()+" voted to start the game.", this);
				voted = true;
			}
		}
	}


	/**
	 * returns whether the {@link Player} voted to start the {@link Server} yet or not
	 * @return whether the {@link Player} voted or not
	 */
	public boolean voted()
	{
		return voted;
	}


	/**
	 * resets the voted() to false<p>
	 * this is automatically called when a {@link Player} quits a {@link Server}
	 */
	public void resetVoted()
	{
		voted = false;		
	}


	/** 
	 * @param o adds this {@link GamePlayObject} to the {@link Stack} of currently built {@link GamePlayObject} in this build phase
	 */
	public void addObject(GamePlayObject o)
	{
		objectStack.push(o);
	}
	
	/**
	 * removes the topmost {@link GamePlayObject}
	 */
	public void removeObject()
	{
		try
		{
			GamePlayObject o = objectStack.pop();
			o.destruct();
			sendData(o.toProtocolString());
		}
		catch(EmptyStackException e)
		{
			sendData(Protocol.CON_ERROR.str()+"Already reverted all objects for this round.");
		}		
	}
	
	/**
	 * @param b whether a {@link Player} finished building in the current build phase or not
	 */
	public void finishedBuilding(boolean b)
	{
		finishedBuilding = b;
	}
	
	
	/**
	 * @return whether a {@link Player} finished building this round or not
	 */
	public boolean finishedBuilding()
	{
		return finishedBuilding;
	}
	
	/**
	 * ends the build phase and resets everything
	 */
	public void endBuildPhase()
	{
		objectStack.clear();
		finishedBuilding = false;
	}
}
