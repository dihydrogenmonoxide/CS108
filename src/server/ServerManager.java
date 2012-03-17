package server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ServerManager
{
	private List<Server> l_Servers = new Vector<Server>();
	private List<Server> l_locked;
	
	public ServerManager()
	{
		l_locked = Collections.unmodifiableList(l_Servers);
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
	
	public List<Server> getServers()
	{
		return this.l_locked;
	}
}
