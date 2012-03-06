package server.net;

import shared.*;
import server.parser.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Serversocket 
implements Runnable
{
	Thread T_Thread;
	private boolean running = false;
	private ServerSocket SS_Socket;
	private Parser P_Parser;
	private int i_Port;
	
	
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
	{
		if(i_Port > 0xFFFF || i_Port <0)
		{
			Log.ErrorLog("Invalid Port number, can't make the ServerSocket : "+i_Port);
			return;
		}
		Log.InformationLog("New ServerSocket initialized");
		
		this.P_Parser = P_Parser;
		this.i_Port = i_Port;
		
		try
		{
			SS_Socket = new ServerSocket(i_Port);
		}
		catch(IOException e)
		{
			Log.ErrorLog("Couldn't Create the Socket: "+e.getMessage());			
		}
		
		T_Thread = new Thread(this);
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

		running = true;
		Log.InformationLog("Stopped a Socket");
	}

	
	/**
	 * This is where the new Thread pops in - Do not call this function - call start() or stop()
	 */
	public void run() 
	{
		if(Thread.currentThread() != this.T_Thread)
		{
			Log.DebugLog("RTFM");
			return;			
		}
		while(true)
		{
			try
			{
				Socket S_Sock = this.SS_Socket.accept();
				new ConnectionHandler(S_Sock, P_Parser);
			} 
			catch (IOException e) 
			{
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
	}

	
	public void run()
	{
		try
		{
			ObjectInputStream OIS_MSG = new ObjectInputStream(S_socket.getInputStream());
			String s_MSG = OIS_MSG.readUTF();
			OIS_MSG.close();
			
			String s_Answer = P_Parser.Parse(s_MSG);
			
			ObjectOutputStream OOS_MSG = new ObjectOutputStream(S_socket.getOutputStream());
			OOS_MSG.writeUTF(s_Answer);
			OOS_MSG.close();
			
			S_socket.close();			
		}
		catch(IOException e)
		{
			Log.WarningLog("Failed to recive or send");
		}		
	}	
}