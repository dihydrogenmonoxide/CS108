package client.net;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import client.events.ChatEventListener;
import client.events.GameEventListener;
import client.events.LobbyEventListener;
import client.events.InfoEventListener;

import shared.*;

public class Clientsocket 
implements Runnable
{
	private final static int i_Timeout = 4000;
	private final static int i_Wait = 500;
	private final static int i_MaxReconnect = 2;
	
	private int i_ReconnectionsFailed = 0;
	
	private Socket S_sock;
	private ServerAddress SA_Server;
	private Thread T_Thread_rec;
	private Thread T_Thread_send;
	private boolean b_connected;
	private ObjectOutputStream OOS_MSG;
	private ObjectInputStream OIS_MSG;
	
	BlockingQueue<String> bq_Queue = new LinkedBlockingQueue<String>();
	
	private String s_PlayerID = "";
	
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
			S_sock.setKeepAlive(true);
			S_sock.setSoTimeout(i_Timeout);
		}
		catch (IOException e)
		{
			Log.ErrorLog("Couldn't create the Socket: "+e.getMessage());
			throw new SocketCreationException(e.getMessage());
		}
		
		
		this.b_connected = true;
		this.parser = new ClientParser();
		
		try 
		{
			OOS_MSG = new ObjectOutputStream(S_sock.getOutputStream());
			OIS_MSG = new ObjectInputStream(S_sock.getInputStream());
			
			//Authenticating with the server
			OOS_MSG.writeUTF("VAUTH");
			OOS_MSG.flush();
			String s_Answer = OIS_MSG.readUTF();
			if(s_Answer.startsWith("VHASH "))
			{
				s_Answer = s_Answer.substring(6);
				Log.InformationLog("Received the hash: \'" + s_Answer+"\'");
				this.s_PlayerID = s_Answer;
			}
			else
			{
				throw new SocketCreationException("Authentication failed: "+s_Answer);
			}
			
			bq_Queue.clear();			
		} 
		catch(EOFException e)
		{
			throw new SocketCreationException("The server closed the connection: "+e.getMessage());
		}
		catch (IOException e)
		{
			throw new SocketCreationException("Failed to create input and output streams: "+e.getMessage());
		}
		
		this.T_Thread_rec = new Thread(this);
		this.T_Thread_rec.start();
		
		this.T_Thread_send = new Thread(this);
		this.T_Thread_send.start();
	}

	
	public void run()
	{
		if(Thread.currentThread() == this.T_Thread_rec)
		{
			Log.InformationLog("Started a Receiver");
			Receiver();
			Log.InformationLog("Shut down a Receiver");
		}
		else if(Thread.currentThread() == this.T_Thread_send)
		{
			Log.InformationLog("Started a Sender");
			Sender();
			Log.InformationLog("Shut down a Sender");
		}
		else
		{
			Log.DebugLog("RTFM - Socket not running");
			return;
		}
	}
	
	private void Receiver()
	{
		try
		{
			do
			{
				try
				{
					String s = OIS_MSG.readUTF();
					parser.parse(s);					
				}
				catch(EOFException e2)
				{
					Log.ErrorLog("Reading Error: "+e2.getMessage());
					this.S_sock.close();
					
					parser.parse("VTOUT "+i_Timeout);
					try
					{
						this.reconnect();
						i_ReconnectionsFailed = 0;
					}
					catch(SocketCreationException e1)
					{
						Log.WarningLog("A reconnect failed: "+e1.getMessage());
						if(i_ReconnectionsFailed >= i_MaxReconnect)
						{
							parser.parse("VFAIL");
							b_connected = false;
							this.S_sock.close();
							Log.ErrorLog("Failed to reconnect too often, shutting down");
						}
						i_ReconnectionsFailed++;
					}
				}
				catch(SocketTimeoutException  e3)
				{
					//A timeout occurred!
					Log.ErrorLog("Disconnected! Initiating reconnect! "+e3.getMessage());
					this.S_sock.close();
				}
				catch(IOException e1)
				{
					if(!b_connected)
					{
						S_sock.close();
						Log.DebugLog("Socket closed - exiting");
						return;
					}
					
					if(!S_sock.isConnected() || S_sock.isClosed() || S_sock.isInputShutdown() || S_sock.isOutputShutdown())
					{
						Log.ErrorLog("Socket Closed unexpectedly: "+e1.getMessage());
						parser.parse("VTOUT "+i_Timeout);
						try
						{
							this.reconnect();
							i_ReconnectionsFailed = 0;
						}
						catch(SocketCreationException e2)
						{
							Log.WarningLog("A reconnect failed: "+e2.getMessage());
							if(i_ReconnectionsFailed >= i_MaxReconnect)
							{
								parser.parse("VFAIL");
								b_connected = false;
								this.S_sock.close();
								Log.ErrorLog("Failed to reconnect too often, shutting down");
							}
							i_ReconnectionsFailed++;
						}
					}
					Log.ErrorLog("Socket IO Error : "+e1.getMessage());
				}
			}
			while(b_connected);
		}
		catch(IOException e)
		{
			Log.ErrorLog("Failed to close a Socket: "+e.getMessage());
		}
	}

	private void Sender()
	{
		try
		{
			do
			{
				try
				{
					if(bq_Queue.isEmpty()) 
						OOS_MSG.writeUTF("VPING");
					
					while(!bq_Queue.isEmpty())
					{
						try 
						{
							OOS_MSG.writeUTF(bq_Queue.take());
						} 
						catch (InterruptedException e)
						{
							Log.ErrorLog("This shouldn't be interrupted!");
						}
					}
					OOS_MSG.flush();
					
					if(bq_Queue.isEmpty())	
					{
						synchronized(this.T_Thread_send)
						{
							try 
							{
								//Wait for Data that needs to be sent and send a VPING if nothing was sent for too long
								Thread.currentThread().wait(i_Wait);
							} catch (InterruptedException e) 
							{
								Log.DebugLog("Waiting in the send Thread was interrupted");
							}		
						}
					}						
				}
				catch(IOException e1)
				{
					if(!b_connected)
					{
						S_sock.close();
						Log.DebugLog("Socket closed - exiting");
						return;
					}
					
					if(!S_sock.isConnected() || S_sock.isClosed() || S_sock.isInputShutdown() || S_sock.isOutputShutdown())
					{
						Log.ErrorLog("Socket is closed! "+e1.getMessage());
						return;
					}
					
					Log.ErrorLog("Socket IO Error: "+e1.getMessage());
					return;
				}
			}
			while(b_connected);
		}
		catch(IOException e)
		{
			Log.ErrorLog("Failed to close a Socket : "+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param s_Data the Data you'd like to send
	 * @return The ID of the Data you sent (for the Event Listener)
	 */
	
	public void sendData(String s_Data)
	{
		try 
		{
			bq_Queue.put(s_Data);
			synchronized(this.T_Thread_send)
			{
				T_Thread_send.notify();
			}
		}
		catch (InterruptedException e) 
		{
			Log.ErrorLog("\'"+s_Data+"\' was not sent: "+e.getMessage());
		}
	}
	
	/**
	 * Sends a chat message to everyone
	 * <p>
	 * To send a Message to a single user type /msg NICKNAME MESSAGE
	 * <p>
	 * example: /msg otto hello otto
	 * 
	 * @param s_MSG the Chat message
	 */
	public void sendChatMessage(String s_MSG)
	{
		try 
		{
			bq_Queue.put("CCHAT " + s_MSG);
			synchronized(this.T_Thread_send)
			{
				T_Thread_send.notify();
			}
		}
		catch (InterruptedException e) 
		{
			Log.ErrorLog("The chat message \'"+s_MSG+"\' was not sent: "+e.getMessage());
		}
	}
	
	/**
	 * Reconnects if the Connection is lost.
	 * @throws SocketCreationException
	 */
	public void reconnect()
	throws SocketCreationException
	{
		//TODO takes very long, check that
		if(!b_connected)
			throw new SocketCreationException("THis connection was manually closed! Can't reopen!");
		
		if(this.S_sock.isClosed() || !this.S_sock.isConnected())
		{
			Log.DebugLog("Socket was Closed... Attempting reconnect");
			try 
			{
				this.S_sock = new Socket(this.SA_Server.getAddress(), this.SA_Server.getPort());
				S_sock.setSoTimeout(i_Timeout);
				S_sock.setKeepAlive(true);
			} 
			catch (IOException e) 
			{
				throw new SocketCreationException("Couldn't connect: "+e.getMessage());
			}
			
			try 
			{
				OOS_MSG = new ObjectOutputStream(S_sock.getOutputStream());
				OIS_MSG = new ObjectInputStream(S_sock.getInputStream());
				
				//Authenticating with the server
				OOS_MSG.writeUTF("VAUTH "+this.s_PlayerID);
				OOS_MSG.flush();
				String s_Answer = OIS_MSG.readUTF();
				if(s_Answer.equals("VHASH "+this.s_PlayerID))
				{
					Log.InformationLog("Reconnected!");
				}
				else
				{
					throw new SocketCreationException("Failed to reconnect: the Server didn't accept the playerID "+s_Answer);
				}
				
				bq_Queue.clear();			
			} 
			catch(EOFException e)
			{
				throw new SocketCreationException("The server closed the connection: "+e.getMessage());
			}
			catch (IOException e)
			{
				throw new SocketCreationException("Failed to create input and output streams: "+e.getMessage());
			}
			
			//restarting both threads in case one of them shut down
			try
			{
				this.T_Thread_send.start();
			}
			catch(IllegalThreadStateException e)
			{
				Log.InformationLog("Receiver Thread is still running!");
			}
			
			try
			{
				this.T_Thread_send.start();
			}
			catch(IllegalThreadStateException e)
			{
				Log.InformationLog("Sender Thread is still running!");
			}
		}
		
		
		try 
		{
			Log.InformationLog("Sennding a Ping");
			OOS_MSG.writeUTF("VPING");
			OOS_MSG.flush();
		} 
		catch (IOException e) 
		{
			throw new SocketCreationException("Failed to send a reconnect ping: "+e.getMessage());
		}
		
		
	}
	
	/**
	 * Closes the Socket and notifies the Server of the disconnect as last action.
	 */
	public void disconnect()
	{
		this.sendData("VEXIT");
		this.b_connected = false;
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


	public void addInfoEventListener(InfoEventListener infoEventListener) {
		parser.addInfoEventListener(infoEventListener);
	}
	
	public void removeInfoEventListener(InfoEventListener e) {
		parser.removeInfoEventListener(e);
	}
}
