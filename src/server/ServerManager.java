package server;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Vector;


public class ServerManager
{
	private List<Server> l_Servers = new Vector<Server>();
	private List<Server> l_locked;
	private Queue<Integer> qi_AvailableIDs = new LinkedList<Integer>();

	
	public ServerManager()
	{
		l_locked = Collections.unmodifiableList(l_Servers);
		
		for(int i = 1; i != 100; i++)
		{
			qi_AvailableIDs.offer(i);
		}
	}
	
	/**
	 * Adds a Server to the list. This is automatically done when you create the server!
	 * @param s_Server the Server
	 */
	public void addServer(Server s_Server)
	{
		process(s_Server, true);
	}
	
	
	/**
	 * Manually remove a server on quit
	 * @param s_Server the server to remove
	 */
	public void removeServer(Server s_Server)
	{
		process(s_Server, false);
	}
	
	private synchronized void process(Server s_Server, boolean b_add)
	{
		if(b_add)
		{
			this.l_Servers.add(s_Server);
		}
		else
		{
			Iterator<Server> i_servers = this.l_Servers.iterator();
			while(i_servers.hasNext())
			{
				Server p = i_servers.next();
				if(p == s_Server)
				{
					MainServer.printInformation("Removed "+p.getServername()+" from the list of all servers");					
				}
			}
		}
	}
	
	/**
	 * Returns a ServerID that remains allocated until the last player quits the Server
	 * @return the ServerID
	 * @throws NoSuchElementException if the max. amount of server is reached
	 */
	public int reserveID()
	throws NoSuchElementException
	{
		return this.qi_AvailableIDs.remove();
	}
	
	/**
	 * returns a list of all servers
	 * @return the list of servers
	 */
	public List<Server> getServers()
	{
		return this.l_locked;
	}

	/**
	 * Returns the Server with the specified ID
	 * @param id the ID you're looking for
	 * @return teh corresponding server
	 */
	public Server findServer(int id) 
	{
		for(Server s : this.l_Servers)
		{
			if(s.getID() == id)
				return s;
		}
		return null;
	}
}
