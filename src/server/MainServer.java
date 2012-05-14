package server;

import shared.*;
import server.UI.ServerUI;
import server.net.*;
import server.parser.*;
import server.players.*;
import server.server.ServerManager;


public class MainServer 
{
	private static int serverPort = Settings.SERVER_DEFAULT_PORT;
	private static Serversocket socket;
	private static Parser parser;
	private static DiscoveryServer discoverServer;
	private static PlayerManager playerManager;
	private static ServerManager serverManager;
	private static ServerUI serverUI;

    public static void startServer()
    {
        startServer(shared.Settings.SERVER_DEFAULT_PORT);
    }
	
	private MainServer()
	{
		//this class can't be instanced!
	}
	
	/**
	 * Starts a Server with discovery in LAN environments that provides lobby functionality and can hold multiple games
	 * <p>
	 * If the Serverport isn't specified (=0) it'll use a default port
	 * @param port The Port on which the server's listening
	 */
	public static void startServer(int port)
	{
		if (port < 1024 || port >= 0xFFFF)
		{
			MainServer.printInformation("No Port specified, defaulting to " + serverPort);
		}
		else
		{
			serverPort = port;
		}
		
		//creating the server UI
		serverUI = new ServerUI();
		serverUI.setVisible(true);
		
		parser = new Parser();
		try
		{
			socket = new Serversocket(serverPort, parser);
			socket.start_();
			discoverServer = new DiscoveryServer(serverPort);
		} 
		catch (SocketCreationException e) 
		{
			MainServer.printInformation("Starting the Server failed: "+e.getMessage());
			return;
		}
		playerManager = new PlayerManager();
		serverManager = new ServerManager();
		
		MainServer.printInformation("Server is up and running!");
	}
	
	/**
	 * Prints the Information onto the ServerUI
	 * @param data
	 */
	public static void printInformation(String data)
	{
		serverUI.printText(data);
	}

	/**
	 * Return the PlayerManager
	 * @return the PlayerManager
	 */
	public static PlayerManager getPlayerManager() 
	{
		return playerManager;		
	}
	
	/**
	 * returns the UI
	 * @return the UI
	 */
	public static ServerUI getGUI()
	{
		return serverUI;
	}
	
	/**
	 * returns the ServerManager	
	 * @return the ServerManager
	 */
	public static ServerManager getServerManager() 
	{
		return serverManager;		
	}
	
	/**
	 * Returns the Serversocket
	 * @return the Serversocket
	 */
	public static Serversocket getServersocket()
	{
		return socket;
	}
	
	/**
	 * Returns the DiscoveryServer
	 * @return the DiscoveryServer
	 */
	public static DiscoveryServer getDiscoveryServer()
	{
		return discoverServer;
	}
}
