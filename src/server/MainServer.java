package server;

import java.awt.EventQueue;

import shared.*;
import server.net.*;
import server.parser.*;
import server.players.*;


public class MainServer 
{
	private static int i_Serverport = 9002;
	private static Serversocket ss_sock;
	private static Parser p_parser;
	private static DiscoveryServer ds_serv;
	private static PlayerManager pm_PlaM;
	private static ServerManager sm_ServM;
	private static ServerUI sui_UI;
	
	private MainServer()
	{
		//this class can't be instanced!
	}
	
	/**
	 * Starts a Server with discovery in LAN environments that provides lobby functionality and can hold multiple games
	 * <p>
	 * If the Serverport isn't specified (=0) it'll use a default port
	 * @param i_Port The Port on which the server's listening
	 */
	public static void startServer(int i_Port)
	{
		if(i_Port < 1024 || i_Port >= 0xFFFF)
		{
			MainServer.printInformation("No Port specified, defaulting to 9002");
		}
		else
		{
			i_Serverport = i_Port;
		}
		
		//creating the server UI
		sui_UI = new ServerUI();
		sui_UI.setVisible(true);
		
		p_parser = new Parser();
		try
		{
			ss_sock = new Serversocket(i_Serverport, p_parser);
			ss_sock.start_();
			ds_serv = new DiscoveryServer(i_Serverport);
		} 
		catch (SocketCreationException e) 
		{
			MainServer.printInformation("Starting the Server failed: "+e.getMessage());
			return;
		}
		pm_PlaM = new PlayerManager();
		sm_ServM = new ServerManager();
		
		MainServer.printInformation("Server is up and running!");
	}
	
	/**
	 * Prints the Information onto the ServerUI
	 * @param s_MSG
	 */
	public static void printInformation(String s_MSG)
	{
		sui_UI.printText(s_MSG);
	}

	/**
	 * Return the PlayerManager
	 * @return the PlayerManager
	 */
	public static PlayerManager getPlayerManager() 
	{
		return pm_PlaM;		
	}
	
	/**
	 * returns the ServerManager	
	 * @return the ServerManager
	 */
	public static ServerManager getServerManager() 
	{
		return sm_ServM;		
	}
	
	/**
	 * Returns the Serversocket
	 * @return the Serversocket
	 */
	public static Serversocket getServersocket()
	{
		return ss_sock;
	}
	
	/**
	 * Returns the DiscoveryServer
	 * @return the DiscoveryServer
	 */
	public static DiscoveryServer getDiscoveryServer()
	{
		return ds_serv;
	}
}
