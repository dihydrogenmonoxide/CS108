package client.net;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;
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
	
	/**how many reconnects failed so far.*/
	private int i_ReconnectionsFailed = 0;
	
	private Socket S_sock;
	private ServerAddress SA_Server;
	private Thread T_Thread_rec;
	private Thread T_Thread_send;
	private boolean b_connected;
	private ObjectOutputStream OOS_MSG;
	private ObjectInputStream OIS_MSG;
	
	private BlockingQueue<String> bq_Queue = new LinkedBlockingQueue<String>();
	
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
			S_sock = new Socket();
			S_sock.connect(new InetSocketAddress(this.SA_Server.getAddress(), this.SA_Server.getPort()), Settings.SocketTimeout.TIMEOUT);
			S_sock.setKeepAlive(true);
			S_sock.setSoTimeout(Settings.SocketTimeout.TIMEOUT);
			S_sock.setTcpNoDelay(true);
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
			OOS_MSG.writeUTF(Protocol.CON_AUTH.toString());
			OOS_MSG.flush();
			String s_Answer = OIS_MSG.readUTF();
			if(s_Answer.startsWith(Protocol.CON_HASH.str()))
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
				catch(SocketTimeoutException  e3)
				{
					Log.ErrorLog("Disconnected! Initiating reconnect! "+e3.getMessage());
					S_sock.close();
					
					parser.parse(Protocol.CON_TIMEOUT.str()+Settings.SocketTimeout.TIMEOUT);
					try
					{
						this.reconnect();
						i_ReconnectionsFailed = 0;
					}
					catch(SocketCreationException e1)
					{
						Log.WarningLog("A reconnect failed: "+e1.getMessage());
						if(i_ReconnectionsFailed >= Settings.SocketTimeout.MAX_RETRIES-1)
						{
							parser.parse(Protocol.CON_FAIL.toString());
							b_connected = false;
							this.S_sock.close();
							Log.ErrorLog("Failed to reconnect too often, shutting down");
						}
						i_ReconnectionsFailed++;
					}
				}
				catch(EOFException e)
				{
					Log.ErrorLog("Reading Error: "+e.getMessage());
					S_sock.close();
					try
					{
						this.reconnect();
						i_ReconnectionsFailed = 0;
					}
					catch(SocketCreationException e1)
					{
						Log.WarningLog("A reconnect failed: "+e1.getMessage());
						if(i_ReconnectionsFailed >= Settings.SocketTimeout.MAX_RETRIES-1)
						{
							parser.parse(Protocol.CON_FAIL.toString());
							Log.ErrorLog("Connection reset by beer");
							b_connected = false;
							OIS_MSG.close();
							OOS_MSG.close();
							S_sock.close();
						}
						i_ReconnectionsFailed++;
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
						Log.ErrorLog("Socket Closed unexpectedly: "+e1.getMessage());
						parser.parse(Protocol.CON_TIMEOUT.str()+Settings.SocketTimeout.TIMEOUT);
						try
						{
							this.reconnect();
							i_ReconnectionsFailed = 0;
						}
						catch(SocketCreationException e2)
						{
							Log.WarningLog("A reconnect failed: "+e2.getMessage());
							if(i_ReconnectionsFailed >= Settings.SocketTimeout.MAX_RETRIES)
							{
								parser.parse(Protocol.CON_FAIL.toString());
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
						OOS_MSG.writeUTF(Protocol.CON_PING.toString());
					
					while(!bq_Queue.isEmpty())
					{
						try 
						{
							OOS_MSG.writeUTF(bq_Queue.take());
						} 
						catch (InterruptedException e)
						{
							Log.ErrorLog("This shouldn't be interrupted!");
							return;
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
								Thread.currentThread().wait(Settings.SocketTimeout.WAIT_BETWEEN_PINGS);
							} catch (InterruptedException e) 
							{
								Log.DebugLog("Waiting in the send Thread was interrupted");
								return;
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
			bq_Queue.put(Protocol.CHAT_MESSAGE.str() + s_MSG);
			//TODO remove this in the final version
		/*	for(int i = 1000; i != 0; i--)
			{
			//	bq_Queue.put("CCHAT derp "+i+" : "+System.currentTimeMillis());
				if(i%2 == 0)
					bq_Queue.put("GMAKE asdasd"+i);
				else
					bq_Queue.put("GQUIT");
			}*/
			//end removal
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
		if(!b_connected)
			throw new SocketCreationException("This connection was manually closed! Can't reopen!");
		
		try
		{
			OOS_MSG.close();
			OIS_MSG.close();
			S_sock.close();
		}
		catch(IOException e)
		{
			Log.WarningLog("Already closed the streams");
		}
		
		
		if(this.S_sock.isClosed() || !this.S_sock.isConnected())
		{
			Log.DebugLog("Socket was Closed... Attempting reconnect");
			this.T_Thread_send.interrupt();
			try 
			{
				S_sock = new Socket();
				S_sock.connect(new InetSocketAddress(this.SA_Server.getAddress(), this.SA_Server.getPort()), Settings.SocketTimeout.TIMEOUT);
				S_sock.setSoTimeout(Settings.SocketTimeout.TIMEOUT);
				S_sock.setKeepAlive(true);
				S_sock.setTcpNoDelay(true);
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
				OOS_MSG.writeUTF(Protocol.CON_AUTH.str()+this.s_PlayerID);
				OOS_MSG.flush();
				String s_Answer = OIS_MSG.readUTF();
				if(s_Answer.equals(Protocol.CON_HASH+" "+this.s_PlayerID))
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
	
			
			try
			{
				this.T_Thread_send = new Thread(this);
				T_Thread_send.start();
			}
			catch(IllegalThreadStateException e)
			{
				Log.InformationLog("Couldn't start the sender thread!");
				throw new SocketCreationException("Failed to start the sender thread: "+e.getMessage());
			}
		}
		
		
		try 
		{
			Log.InformationLog("Sennding a Ping");
			OOS_MSG.writeUTF(Protocol.CON_PING.toString());
			OOS_MSG.flush();
		} 
		catch (IOException e) 
		{
			throw new SocketCreationException("Failed to send a reconnect ping: "+e.getMessage());
		}
		i_ReconnectionsFailed = 0;
	}
	
	/**
	 * Closes the Socket and notifies the Server of the disconnect as last action.
	 */
	public void disconnect()
	{
		this.sendData(Protocol.CON_EXIT.toString());
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
