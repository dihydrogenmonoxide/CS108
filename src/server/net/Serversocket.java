package server.net;

import shared.*;
import server.parser.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class Serversocket 
implements Runnable
{
	Thread T_Thread;
	private boolean running = false;
	private ServerSocket SS_Socket;
	private Parser P_Parser;
	private int i_Port;
	private List<Socket> lS_Socks = new java.util.LinkedList<Socket>();
	
	
	/**
	 * Sets up a Server socket on the desired Port
	 * <p>
	 * call start_() to enable it and stop_() to disable it
	 * <p>
	 * Listens on all Valid NetworkInterfaces
	 * 
	 * @param i_Port The Port it's listening on
	 */
	public Serversocket(int i_Port, Parser P_Parser)
	throws SocketCreationException
	{
		if(i_Port > 0xFFFF || i_Port <0)
		{
			Log.ErrorLog("Invalid Port number, can't make the ServerSocket : "+i_Port);
			throw new SocketCreationException("Invalid port");
		}
		Log.InformationLog("New ServerSocket initialized");
		
		this.P_Parser = P_Parser;
		this.i_Port = i_Port;
		
		try
		{
			SS_Socket = new ServerSocket(this.i_Port);
		}
		catch(IOException e)
		{
			Log.ErrorLog("Couldn't Create the Socket: "+e.getMessage());
			throw new SocketCreationException("Failed creating the Socket: "+e.getMessage());
		}
		
		T_Thread = new Thread(this);
		lS_Socks.clear();
	}
	
	/**
	 * Starts the ServerSocket - it'll be listening to any incoming traffic afterwards
	 */
	public void start_()
	{
		if(running)
		{
			Log.DebugLog("Socket is already running - can't start the same socket twice");
			return;
		}
		
		if(SS_Socket == null)
		{
			Log.ErrorLog("Can't start the socket as it couldn't be created");
			return;
		}

		T_Thread.start();
		
		running = true;
		Log.InformationLog("Started a Socket");
	}
	
	/**
	 * Stops the ServerSocket - it won't be listening anymore
	 * <p>
	 * You can re-enable it at any time by calling start_()
	 */
	public void stop_()
	{
		if(!running)
		{
			Log.DebugLog("Socket is not running - can't stop something that isn't running");
			return;
		}
		
		
		T_Thread.interrupt();
		try 
		{
			this.SS_Socket.close();
		} catch (IOException e) 
		{
			Log.WarningLog("Closing a socket failed");
		}

		running = false;
		Log.InformationLog("Stopped a Socket");
	}

	
	/**
	 * This is where the new Thread pops in - Do not call this function - call start() or stop()
	 */
	public void run() 
	{
		if(Thread.currentThread() != this.T_Thread)
		{
			Log.DebugLog("RTFM - Socket not running");
			return;			
		}
		
		Log.DebugLog("Listeing to connection attempts on "+this.i_Port);
		
		while(true)
		{
			try
			{
				//listening to connection attempts and opening a Socket
				Socket S_Sock = this.SS_Socket.accept();
				lS_Socks.add(S_Sock);
				new ConnectionHandler(S_Sock, P_Parser);
			} 
			catch (IOException e) 
			{
				//Closing all Sockets in Case the Server was terminated
				if(SS_Socket.isClosed())
				{
					for(Socket S_Socket : lS_Socks)
					{
						try 
						{
							S_Socket.close();
						}
						catch (IOException e1)
						{
							Log.WarningLog("Clouldn\'t close a Socket: "+e1.getMessage());							
						}
					}
					Log.InformationLog("Closed all Sockets (and therefore terminated all connections)");
					return;
				}
				Log.WarningLog("Failed Creating a Socket: "+e.getMessage());
			}
			
		}
		
		
	}
}


class ConnectionHandler
implements Runnable
{
	private Socket S_socket;
	private Parser P_Parser;
	
	public ConnectionHandler(Socket S_Sock, Parser P_Parser)
	{
		this.S_socket = S_Sock;
		this.P_Parser = P_Parser;
		Thread T_Thread = new Thread(this);
		T_Thread.start();
		Log.DebugLog("New Socket open: "+S_socket.getInetAddress());
	}

	
	public void run()
	{
		boolean b_active = true;

		try 
		{
			ObjectInputStream OIS_MSG = new ObjectInputStream(S_socket.getInputStream());
			ObjectOutputStream OOS_MSG = new ObjectOutputStream(S_socket.getOutputStream());
			S_socket.setKeepAlive(true);
			
			do
			{
				try
				{
					//reading what we received
					String s_MSG = OIS_MSG.readUTF();
					//parsing&handling it
					String s_Answer = P_Parser.Parse(s_MSG);
					//Confirming
					OOS_MSG.writeUTF(s_Answer);				
					OOS_MSG.flush();
				}
				catch(IOException e)
				{
					if(S_socket.isClosed())
					{
						Log.InformationLog("CLosed a socket");
						OIS_MSG.close();
						OOS_MSG.close();
						return;
					}
					if(!S_socket.isInputShutdown())
					{
						Log.InformationLog("Someone just disconnected: " +S_socket.getInetAddress().getHostAddress());
						S_socket.close();
						OIS_MSG.close();
						OOS_MSG.close();
						return;
					}
					Log.WarningLog("Failed to recive or send");
				}
			}
			while(b_active);
		}
		catch (IOException e1) 
		{
			Log.ErrorLog("Clouldn't create an input or output-stream: "+e1.getMessage());
		}
	}	
}