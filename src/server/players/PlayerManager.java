package server.players;

import java.util.*;

import server.MainServer;

public class PlayerManager 
{
	private List<Player> l_players = new Vector<Player>();
	private List<Player> l_locked;
	
	public PlayerManager()
	{
		l_locked = Collections.unmodifiableList(l_players);
	}
	
	/**
	 * Broadcast a message to all players in the same lobby or server
	 * @param s_MSG the message
	 * @param p_player the initiator of the broadcast
	 */
	public void broadcastMessage(String s_MSG, Player p_player)
	{
		for(Player p : l_players)
		{
			//will this cause null ptr exception?
			if(p.getServer() == p_player.getServer())
			{
				p.sendMessage(s_MSG);				
			}			
		}		
	}


	/**
	 * Returns a !!READ ONLY!! list of all players on the server
	 * @return the list of all connected players
	 */
	public List<Player> getPlayers()
	{
		return l_locked;
	}
	
	/**
	 * Add a Player to the PlayerManager. This is automatically done when you create a new Player!
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
		}
		else
		{
			Iterator<Player> i_players = this.l_players.iterator();
			while(i_players.hasNext())
			{
				Player p = i_players.next();
				if(p == p_player)
				{
					MainServer.printInformation("Removed "+p.getNick());					
				}
			}
		}
		
	}
}
