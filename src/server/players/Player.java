package server.players;

import java.net.Socket;
import java.util.EmptyStackException;
import java.util.Stack;

import server.MainServer;
import server.GamePlayObjects.GamePlayObject;
import server.net.*;
import server.server.Server;
import shared.Log;
import shared.Protocol;
import shared.Settings;
import shared.game.GameSettings;

public class Player 
implements Comparable<Player>
{
	private String nick = "JohnDoe";
	private String playerToken;
	private Server server;
	private PlayerSocket socket;
	private int playerID;
	private volatile boolean isNameSet = false;
	private boolean isInactive = false;
	private volatile boolean isConnectionLost = false;
	private int fieldID = 0;
	private long money = 0;
	private long population = 0;
	private boolean voted = false;
	private boolean finishedBuilding = false;
	private Stack<GamePlayObject> objectStack = new Stack<GamePlayObject>();
	
	/**
	 * Creates a new Player on the Server;
	 * @param authToken The unique token the Server assigned
	 */
	public Player(String authToken, PlayerSocket sock, int id)
	{
		this.playerToken = authToken;
		this.socket = sock;
		MainServer.getPlayerManager().addPlayer(this);
		this.playerID = id+100;
		MainServer.getGUI().addPlayer(this);
		MainServer.printInformation("A New Player connected - Assigned ID: "+this.playerID);
	}
	
	/**
	 * 
	 * @return whether a {@link Player} set it's nickname or not
	 */
	public boolean isNickSet()
	{
		return isNameSet;
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
		return o.playerID-this.playerID;
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
	 * @param nickName The nickname you'd like to assign to the specified player
	 */
	public void setNick(String nickName)
	{
		String s_oldNick = this.nick;
		this.nick = nickName;
		if(!isNameSet)
		{
			for(Player play : MainServer.getPlayerManager().getPlayers())
			{
				if(play != this)
				{
					if(play.isInLobby())
						socket.sendData(Protocol.LOBBY_JOIN.str()+play.getID()+" "+play.getNick());
					socket.sendData(Protocol.CON_NICK.str()+play.getID()+" "+play.getNick());
				}
			}
			for(Server s : MainServer.getServerManager().getServers())
			{
				socket.sendData(Protocol.GAME_BROADCAST.str()+s.getID()+" "+s.getPlayerAmount()+"  "+s.getServername());
				for(Player p : s.getPlayers())
				{
					socket.sendData(Protocol.GAME_JOIN.str()+s.getID()+" "+p.getID()+" "+p.getNick()); 
				}
				if(s.isGameRunning())
					socket.sendData(Protocol.GAME_BROADCAST.str()+s.getID()+" "+0+"  "+s.getServername());
			}
			if(socket != null)
				MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_JOIN.str()+socket.getPlayer().getID()+" "+socket.getPlayer().getNick());
			isNameSet = true;
		}
		
		MainServer.printInformation("The Player with the ID "+this.playerID+" changed his name: \'"+s_oldNick+"\' -> \'"+nickName+"\'");
		MainServer.getGUI().addPlayer(this);
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.CON_NICK.str()+getID()+" "+getNick());
	}

	/**
	 * 
	 * @return The Players current nickname
	 */
	public String getNick() 
	{
		return nick;
	}
	
	/**
	 * This is a number the player and his actions can be linked to. 
	 * @return the playerID
	 */
	public int getID()
	{
		return this.playerID;
	}
	
	/**
	 * Returns the Players UUID
	 * @return the token
	 */
	public String getToken()
	{
		return this.playerToken;
	}

	/**
	 * Returns the Players current server or 'null' if the Player is in the Lobby
	 * @return the Server
	 */
	public Server getServer() 
	{
		return this.server;
	}
	
	/**
	 * sets the server for the current player
	 * @param s_serv the server
	 */
	public void setServer(Server s_serv) 
	{
		this.server = s_serv;
	}

	/**
	 * Sends the Data to the specified Player
	 * @param data the data
	 */
	public void sendData(String data)
	{
		if(socket == null)
		{
			Log.ErrorLog("Player "+nick+"("+playerID+") isn't connected to this server it seems");
			return;
		}
		socket.sendData(data);

	}
			
	/**
	 * Returns whether the player is in the lobby or not
	 * @return whether the player is in the lobby or not
	 */
	public boolean isInLobby()
	{
		return (this.server == null);
	}

	/**
	 * This gets called when the Player lost the connection and may tries to reconnect
	 */
	public void connectionLost(Socket sock)
	{
		if(isConnectionLost)
			return;
		
		isConnectionLost = true;
		
		MainServer.printInformation("The Player "+this.getNick()+" lost the connection - pausing and waiting for reconnect");
		MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.nick+" Verbindungsunterbruch - neuer Versuch!", this);


		socket.close();
		if(this.server != null)
			this.server.pause();
		try 
		{
			Thread.sleep(Settings.SocketTimeout.TIMEOUT*Settings.SocketTimeout.MAX_RETRIES);
		}
		catch (InterruptedException e)
		{
			Log.WarningLog("Failed to sleep and therefore immediatly disconnected the player");
		}
		
		if(this.isConnectionLost)
		{
			this.disconnect();
		}
		isConnectionLost = false;
			
	}

	/**
	 * This is called when the player reconnects
	 * @param sock the new socket
	 */
	public void reconnect(PlayerSocket sock) 
	{
		isConnectionLost = false;
		this.socket = sock;
		MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.nick+" ist auferstanden!", this);
		if(this.server != null)
			this.server.resume();
		
		for(Player play : MainServer.getPlayerManager().getPlayers())
		{
			if(play != this)
			{
				if(play.isInLobby())
					socket.sendData(Protocol.LOBBY_JOIN.str()+play.getID()+" "+play.getNick());
				socket.sendData(Protocol.CON_NICK.str()+play.getID()+" "+play.getNick());
			}			
		}
		for(Server s : MainServer.getServerManager().getServers())
		{
			socket.sendData(Protocol.GAME_BROADCAST.str()+s.getID()+" "+s.getPlayerAmount()+"  "+s.getServername());
			for(Player p : s.getPlayers())
			{
				socket.sendData(Protocol.GAME_JOIN.str()+s.getID()+" "+p.getID()+" "+p.getNick()); 
			}
		}
	}

	/**
	 * This is called when the player disconnects
	 */
	public synchronized void disconnect() 
	{
		if(!isInactive)
		{
			isInactive = true;
			MainServer.getGUI().removePlayer(this);
			MainServer.getPlayerManager().removePlayer(this);
			if(this.isConnectionLost)
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.nick+" hat die Verbindung verloren.", this);
			else
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t"+this.nick+" verlässt uns.", this);
			
			if(this.isInLobby())
				MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_QUIT.str()+this.playerID+" "+this.nick);
			else
				MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_QUIT.str()+this.server.getID()+" "+this.playerID+" "+this.nick);
		
			if(server != null)
			{
				server.resume();
				server.removePlayer(this);
				if(server.isGameRunning())
				{
					Log.InformationLog("Removing a players objects as he quit");
					for(GamePlayObject o :server.getObjectManager().getPlayersObjectList(this))
					{
						Log.InformationLog("Removed a "+o.getClass()+" ("+o.getHealthPoints()+")");
						o.damage(o.getHealthPoints());
					}
				}
			}
			if(socket != null)
				socket.close();
		}
	}
	
	
	@Override
	public String toString()
	{
		return this.nick;
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
			if(server == null)
			{
				sendData(Protocol.CON_ERROR+"Can't vote as you're not in a server");
			}
			else
			{
				server.addVote();
				MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE+"\t"+this.getNick()+" ist bereit für das Spiel.", this);
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
			if(o instanceof server.GamePlayObjects.ATT)
				addMoney(GameSettings.ATT.price);
			else if(o instanceof server.GamePlayObjects.Tank)
				addMoney(GameSettings.Tank.price);
			else if(o instanceof server.GamePlayObjects.Bank)
				addMoney(GameSettings.Bank.price);
			else if(o instanceof server.GamePlayObjects.Bomber)
				addMoney(GameSettings.Bomber.price);
			else if(o instanceof server.GamePlayObjects.Reproductioncenter)
				addMoney(GameSettings.Reproductioncenter.price);
			else if(o instanceof server.GamePlayObjects.Flak)
				addMoney(GameSettings.Flak.price);
			else
				Log.WarningLog("Couldn't refund an object: "+o.getClass());
			o.damage(o.getHealthPoints());
			sendData(o.toProtocolString());
			sendData(Protocol.GAME_MONEY.str()+getMoney());
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


	/**
	 * 
	 * @return whether the {@link Player} is in a running {@link Server} or not
	 */
	public boolean isInActiveGame()
	{
		if(server == null)
			return false;
		return server.isGameRunning();
	}
}
