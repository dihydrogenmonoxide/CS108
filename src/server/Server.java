package server;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import server.GamePlayObjects.GamePlayObject;
import server.GamePlayObjects.GamePlayObjectManager;
import server.logic.LogicManager;
import server.players.*;
import shared.Log;
import shared.Protocol;
public class Server 
implements Comparable<Server>
{
	private List<Player> l_players = new Vector<Player>();
	private List<Player> l_locked;
	private String s_servername;
	private int i_ServerID;
	private Queue<Integer> availableFieldIDs = new LinkedList<Integer>();
	private int startVotes = 0;
	private GamePlayObjectManager objectManager;
	private LogicManager logicManager;
	private boolean isGameRunning = false;
	private boolean isPaused = false;
	
	public Server(String s_Servername, int i_ID)
	
	{
		for(int i = 1; i != 6; i++)
		{
			availableFieldIDs.offer(i);
		}
		
		Collections.shuffle((List<Integer>) availableFieldIDs);
		
		s_servername = s_Servername;
		l_locked = Collections.unmodifiableList(l_players);
		i_ServerID = i_ID+200;
		
		objectManager = new GamePlayObjectManager(this);
		logicManager = new LogicManager(this);
		
		MainServer.getServerManager().addServer(this);
		MainServer.printInformation("New server '"+s_servername+"' created");
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+this.getPlayerAmount()+"  "+this.getServername());
	}
	
	
	@Override
	public int compareTo(Server o)
	{
		return o.i_ServerID - this.i_ServerID;
	}
	

	/**
	 * Returns all players on the current Server
	 * @return a list of players
	 */
	public List<Player> getPlayers()
	{
		return l_locked;
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
	
	
	private void startGame()
	{
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+0+"  "+this.getServername());
		logicManager.startGame();
		isGameRunning = true;
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
	 * @param p_Player the {@link Player}
	 */
	public void addPlayer(Player p_Player)
	{	
		if(isGameRunning)
		{
			p_Player.sendData(Protocol.CON_ERROR.str()+"Can't join a running game");
		}
		else
		{
			p_Player.setServer(this);
			process(p_Player, true);
			p_Player.setFieldID(availableFieldIDs.remove());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_JOIN.str()+this.i_ServerID+" "+p_Player.getID()+" "+p_Player.getNick());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_QUIT.str()+p_Player.getID());
			MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_BROADCAST.str()+this.getID()+" "+this.getPlayerAmount()+"  "+this.getServername());
		}
	}
	
	/**
	 * Returns the amount of players connected to this {@link Server}
	 * @return the amount of players
	 */
	public int getPlayerAmount()
	{
		return this.l_locked.size();
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
	 * @param p_player the player to remove
	 */
        
        
	public void removePlayer(Player p_player)
	{
		if(isGameRunning)
		{
			//TODO SERVER test if this works; fix round end votes!   
			for(GamePlayObject o : objectManager.getPlayersObjectList(p_player))
			{
				o.destruct();
			}
		}
		
		if(p_player.voted())
		{
			p_player.resetVoted();
			startVotes--;
		}
		
		p_player.setServer(null);
		process(p_player, false);
		if(p_player.getFieldID() >= 1 && p_player.getFieldID() <= 5)
		{
			availableFieldIDs.offer(p_player.getFieldID());
		}
		else
		{
			Log.ErrorLog("Error: this player hadn't had a valit field ID");
		}
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.LOBBY_JOIN.str()+p_player.getID());
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.GAME_QUIT.str()+this.i_ServerID+" "+p_player.getID()+" "+p_player.getNick());
		if(this.getPlayerAmount() == 0)
		{
			MainServer.getServerManager().removeServer(this);
			MainServer.printInformation("Server '"+s_servername+"' closed");
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
	
	private synchronized void process(Player p_player, boolean b_add)
	{
		if(b_add)
		{
			this.l_players.add(p_player);
			MainServer.printInformation("added "+p_player.getNick()+" to "+this.s_servername);
		}
		else
		{
			Iterator<Player> i_players = this.l_players.iterator();
			while(i_players.hasNext())
			{
				Player p = i_players.next();
				if(p == p_player)
				{
					i_players.remove();
					MainServer.printInformation("Removed "+p.getNick()+" from "+this.s_servername);					
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
		return this.s_servername;
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
		if(l_players.size() > 1 && startVotes > l_players.size() / 2)
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
		return this.i_ServerID;
	}

	/**
	 * @return the {@link LogicManager}
	 */
	public LogicManager getLogicManager()
	{
		return logicManager;
	}
}
