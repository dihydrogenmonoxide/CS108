package server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import server.players.*;
public class Server 
implements Comparable<Server>
{
	private List<Player> l_players = new Vector<Player>();
	private List<Player> l_locked;
	private String s_servername;
	private int i_ServerID;
	private static int i_NumServers = 0;
	
	public Server(String s_Servername)
	{
		// TODO implement the whole server a user can start when he's in the lobby
		s_servername = s_Servername;
		l_locked = Collections.unmodifiableList(l_players);
		i_ServerID = i_NumServers++;
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
	 * Add a Player to the Server.
	 * @param p_Player the player
	 */
	public void addPlayer(Player p_Player)
	{
		process(p_Player, true);
	}
	
	
	/**
	 * Manually remove a player on quit
	 * @param p_player the player to remove
	 */
	public void removePlayer(Player p_player)
	{
		process(p_player, false);
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

}
