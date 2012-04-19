package server.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import server.MainServer;
import server.GamePlayObjects.GamePlayObject;
import server.GamePlayObjects.GamePlayObjectManager;
import server.logic.LogicManager;
import server.players.*;
import shared.Log;
import shared.Protocol;
import shared.Settings;


public class Server 
implements Comparable<Server>
{
	private List<Player> playerList = new Vector<Player>();
	private List<Player> playerListLocked;
	private String serverName;
	private int serverID;
	private Queue<Integer> availableFieldIDs = new LinkedList<Integer>();
	private int startVotes = 0;
	private GamePlayObjectManager objectManager;
	private LogicManager logicManager;
	private boolean isGameRunning = false;
	private boolean isPaused = false;
	private List<String> previousWinners = new Vector<String>();
	
	@SuppressWarnings("unchecked")
	public Server(String s_Servername, int i_ID)
	
	{
		for(int i = 1; i != 6; i++)
		{
			availableFieldIDs.offer(i);
		}
		
		Collections.shuffle((List<Integer>) availableFieldIDs);
		
		serverName = s_Servername;
		playerListLocked = Collections.unmodifiableList(playerList);
		serverID = i_ID+200;
		
		objectManager = new GamePlayObjectManager(this);
		logicManager = new LogicManager(this);
		
		MainServer.getServerManager().addServer(this);
		MainServer.printInformation("New server '"+serverName+"' created");
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+this.getPlayerAmount()+"  "+this.getServername());
	}
	
	
	@Override
	public int compareTo(Server o)
	{
		return o.serverID - this.serverID;
	}
	

	/**
	 * Returns all players on the current Server
	 * @return a list of players
	 */
	public List<Player> getPlayers()
	{
		return playerListLocked;
	}
	
	/**
	 * Adds a winner
	 * @param winner the winner message
	 */
	public void addWinner(String winner)
	{
		previousWinners.add(winner);
	}
	
	/**
	 * adds a vote to start the game
	 */
	public void addVote()
	{
		startVotes++;
		//TODO SERVER uncomment after oli is done testing
		if(/*l_players.size() > 1 && startVotes > l_players.size() / 2 &*/ !isPaused)
			startGame();
	}
	
	/**
	 * broadcasts previous winners
	 */
	public void broadcastPreviousWinners()
	{
		if(previousWinners.size() > 0)
		{
			broadcastMessage(Protocol.CHAT_MESSAGE.str()+"\tPrevious winners:");
			for(String s : previousWinners)
			{
				broadcastMessage(Protocol.CHAT_MESSAGE.str()+s);
			}
		}
	}
	
	
	private void startGame()
	{
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+0+"  "+this.getServername());
		logicManager.startGame();
		isGameRunning = true;
	}
	
	
	/**
	 * This is called when a {@link Player} is defeated
	 * @param p the {@link Player} to remove
	 */
	public void suspendPlayer(Player p)
	{
		broadcastMessage(Protocol.CHAT_MESSAGE.str()+"\t"+p.getNick()+" is defeated!");
		p.sendData(Protocol.GAME_LOST_OR_WON.str()+"1");
	}


	/**
	 * returns the {@link GamePlayObjectManager} of this {@link Server}
	 * @return {@link GamePlayObjectManager}
	 */
	public GamePlayObjectManager getObjectManager()
	{
		return objectManager;
	}
	
	/**
	 * Add a {@link Player} to the {@link Server}.
	 * @param player the {@link Player}
	 */
	public void addPlayer(Player player)
	{	
		if(isGameRunning)
		{
			player.sendData(Protocol.CON_ERROR.str()+"Can't join a running game");
		}
		else
		{
			player.setServer(this);
			process(player, true);
			player.setFieldID(availableFieldIDs.remove());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_JOIN.str()+this.serverID+" "+player.getID()+" "+player.getNick());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_QUIT.str()+player.getID());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+this.getPlayerAmount()+"  "+this.getServername());
		}
	}
	
	/**
	 * Returns the amount of players connected to this {@link Server}
	 * @return the amount of players
	 */
	public int getPlayerAmount()
	{
		return this.playerListLocked.size();
	}
	
	/**
	 * Broadcasts the message to all {@link Player}'s on this {@link Server}
	 * @param message the message to broadcast
	 */
	public void broadcastMessage(String message)
	{
		for(Player p : getPlayers())
		{
			try
			{
				p.sendData(message);
			}
			catch(NullPointerException e)
			{
				Log.WarningLog("Seens you were trying to send data to a player that isn't really connected");
			}
		}
	}
	
	
	/**
	 * Manually remove a player on quit
	 * @param player the player to remove
	 */
        
        
	public void removePlayer(Player player)
	{
		if(isGameRunning)
		{
			//TODO SERVER test if this works; fix round end votes!   
			for(GamePlayObject o : objectManager.getPlayersObjectList(player))
			{
				o.damage(o.getHealthPoints());
			}
		}
		
		if(player.voted())
		{
			player.resetVoted();
			startVotes--;
		}
		
		player.setServer(null);
		process(player, false);
		if(player.getFieldID() >= 1 && player.getFieldID() <= 5)
		{
			availableFieldIDs.offer(player.getFieldID());
		}
		else
		{
			Log.ErrorLog("Error: this player hadn't had a valit field ID");
		}
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_JOIN.str()+player.getID());
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_QUIT.str()+this.serverID+" "+player.getID()+" "+player.getNick());
		if(this.getPlayerAmount() == 0)
		{
			MainServer.getServerManager().removeServer(this);
			MainServer.printInformation("Server '"+serverName+"' closed");
		}			
		if(!isGameRunning)
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+this.getPlayerAmount()+"  "+this.getServername());
	}
	
	/**
	 * @return the {@link Server}'s current status
	 */
	public boolean isGameRunning()
	{
		return isGameRunning;
	}
	
	/**
	 * @return the {@link Server}'s current status
	 */
	public boolean isPaused()
	{
		return isPaused;
	}
	
	private synchronized void process(Player player, boolean add)
	{
		if(add)
		{
			this.playerList.add(player);
			MainServer.printInformation("added "+player.getNick()+" to "+this.serverName);
		}
		else
		{
			Iterator<Player> i_players = this.playerList.iterator();
			while(i_players.hasNext())
			{
				Player p = i_players.next();
				if(p == player)
				{
					i_players.remove();
					MainServer.printInformation("Removed "+p.getNick()+" from "+this.serverName);					
				}
			}
		}
	}
	
	/**
	 * Returns the server's name
	 * @return the server's name
	 */
	public String getServername()
	{
		return this.serverName;
	}
	
	/**
	 * pauses the server. it's called when a player loses the connection
	 */
	public void pause()
	{
		isPaused = true;
		if(isGameRunning)
			logicManager.pause();
	}
	
	/**
	 * resumes the server. it is called when a player reconnects or times out
	 */
	public void resume()
	{
		isPaused = false;
		if(playerList.size() > 1 && startVotes > playerList.size() / 2)
			startGame();
		if(isGameRunning)
			logicManager.resume();
	}


	/**
	 * returns the serverID
	 * @return the serverID
	 */
	public int getID()
	{
		return this.serverID;
	}

	/**
	 * @return the {@link LogicManager}
	 */
	public LogicManager getLogicManager()
	{
		return logicManager;
	}


	/**
	 * resets the money and population
	 */
	public void resetFields()
	{
		for(Player p : playerList)
		{
			p.addMoney(Settings.GameValues.DEFAULT_MONEY-p.getMoney());
			p.addPopulation(Settings.GameValues.DEFAULT_POPULATION-p.getPopulation());
		}
		Log.DebugLog("reset population & money");
	}
}
