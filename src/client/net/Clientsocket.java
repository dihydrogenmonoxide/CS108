package client.net;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.PriorityQueue;

import client.events.ChatEventListener;
import client.events.GameEventListener;
import client.events.LobbyEventListener;

import shared.*;

public class Clientsocket 
implements Runnable
{
	private Socket S_sock;
	private ServerAddress SA_Server;
	private Thread T_Thread;
	private boolean b_connected;
	PriorityQueue<String> PQS_Queue = new PriorityQueue<String>();
	/**Parser to parse the received messages.*/
	private ClientParser parser;
	
	/**
	 * Creates the Socket to communicate with the server
	 * @param SA_Server The Server you'd like to connect to
	 */
	public Clientsocket(ServerAddress SA_Server)
	throws SocketCreationException
	{	
		this.SA_Server = SA_Server;
		try
		{
			this.S_sock = new Socket(this.SA_Server.getAddress(), this.SA_Server.getPort());
		}
		catch (IOException e)
		{
			Log.ErrorLog("Couldn't create the Socket: "+e.getMessage());
			throw new SocketCreationException(e.getMessage());
		}
		
		PQS_Queue.clear();
		
		this.b_connected = true;
		
		this.T_Thread = new Thread(this);
		this.T_Thread.start();
		this.parser = new ClientParser();
	}

	
	public void run() 
	{
		if(Thread.currentThread() != T_Thread)
		{
			Log.DebugLog("RTFM - Socket not running");
			return;
		}
		
		Log.DebugLog("Started a new Socket");
		
		try 
		{
			S_sock.setKeepAlive(true);
			
			ObjectOutputStream OOS_MSG = new ObjectOutputStream(S_sock.getOutputStream());
			ObjectInputStream OIS_MSG = new ObjectInputStream(S_sock.getInputStream());
			
			do
			{
				try
				{
					OOS_MSG.writeUTF("TEST: "+this.S_sock.getLocalPort()+" "+S_sock.getSoTimeout());
					OOS_MSG.flush();
					String s = OIS_MSG.readUTF();
					Log.InformationLog("zeh answer:" +s);
					
					parser.parse(s);
					
					
					b_connected = false;
				}
				catch(IOException e1)
				{
					if(S_sock.isClosed() || !b_connected)
					{
						if(!b_connected)
							S_sock.close();
						Log.DebugLog("Socket closed - exiting");
						return;
					}
					
					Log.ErrorLog("Socket IE Error: "+e1.getMessage());
				}
			}
			while(b_connected);
		}
		catch (IOException e) 
		{
			Log.ErrorLog("Couldn't create a ObjectStream:"+e.getMessage());
		}
		Log.DebugLog("Closed a Socket");		
	}
	
	
	public void addChatEventListener(ChatEventListener e){
		parser.addChatEventListener(e);
	}

	public void removeChatEventListener(ChatEventListener e){
		parser.removeChatEventListener(e);
	}
	
	public void addLobbyEventListener(LobbyEventListener e){
		parser.addLobbyEventListener(e);
	}
	
	public void removeLobbyEventListener(LobbyEventListener e){
		parser.removeLobbyEventListener(e);
	}
	
	public void addGameEventListener(GameEventListener e){
		parser.addGameEventListener(e);
	}
	
	public void removeGameEventListener(GameEventListener e){
		parser.removeGameEventListener(e);
	}
	
}
