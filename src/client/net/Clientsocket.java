package client.net;
import client.events.ChatEventListener;
import client.events.GameEventListener;
import client.events.InfoEventListener;
import client.events.LobbyEventListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import shared.*;

public class Clientsocket 
implements Runnable
{
	
	/**how many reconnects failed so far.*/
	private int failedReconnections = 0;
	
	private Socket socket;
	private ServerAddress serverAddress;
	private Thread receiverThread;
	private Thread senderThread;
	private boolean b_connected;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private BlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();
	
	private String playerID = "";
	
	/**Parser to parse the received messages.*/
	private ClientParser parser;
	
	/**
	 * Creates the Socket to communicate with the server
	 * @param serverAddress The Server you'd like to connect to
	 */
	public Clientsocket(ServerAddress serverAddress)
	throws SocketCreationException
	{	
		this.serverAddress = serverAddress;
		try
		{
			socket = new Socket();
			socket.connect(new InetSocketAddress(this.serverAddress.getAddress(), this.serverAddress.getPort()), Settings.SocketTimeout.TIMEOUT);
			socket.setKeepAlive(true);
			socket.setSoTimeout(Settings.SocketTimeout.TIMEOUT);
			socket.setTcpNoDelay(true);
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
			//TODO SERVER failure creating objectoutput and or inputstream ("invalid argument")
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			
			//Authenticating with the server
			outputStream.writeUTF(Protocol.CON_AUTH.toString());
			outputStream.flush();
			String s_Answer = inputStream.readUTF();
			if(s_Answer.startsWith(Protocol.CON_HASH.str()))
			{
				s_Answer = s_Answer.substring(6);
				Log.InformationLog("Received the hash: \'" + s_Answer+"\'");
				this.playerID = s_Answer;
			}
			else
			{
				throw new SocketCreationException("Authentication failed: "+s_Answer);
			}
			
			commandQueue.clear();			
		} 
		catch(EOFException e)
		{
			throw new SocketCreationException("The server closed the connection: "+e.getMessage());
		}
		catch (IOException e)
		{
			throw new SocketCreationException("Failed to create input and output streams: "+e.getMessage());
		}
		
		this.receiverThread = new Thread(this);
		this.receiverThread.start();
		
		this.senderThread = new Thread(this);
		this.senderThread.start();
	}

	
	public void run()
	{
		if(Thread.currentThread() == this.receiverThread)
		{
			Log.InformationLog("Started a Receiver");
			Receiver();
			Log.InformationLog("Shut down a Receiver");
		}
		else if(Thread.currentThread() == this.senderThread)
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
					String s = inputStream.readUTF();
					parser.parse(s);					
				}
				catch(SocketTimeoutException  e3)
				{
					Log.ErrorLog("Disconnected! Initiating reconnect! "+e3.getMessage());
					socket.close();
					
					parser.parse(Protocol.CON_TIMEOUT.str()+Settings.SocketTimeout.TIMEOUT);
					try
					{
						this.reconnect();
						failedReconnections = 0;
					}
					catch(SocketCreationException e1)
					{
						Log.WarningLog("A reconnect failed: "+e1.getMessage());
						if(failedReconnections >= Settings.SocketTimeout.MAX_RETRIES-1)
						{
							parser.parse(Protocol.CON_FAIL.toString());
							b_connected = false;
							this.socket.close();
							Log.ErrorLog("Failed to reconnect too often, shutting down");
						}
						failedReconnections++;
					}
				}
				catch(EOFException e)
				{
					Log.ErrorLog("Reading Error: "+e.getMessage());
					socket.close();
					try
					{
						this.reconnect();
						failedReconnections = 0;
					}
					catch(SocketCreationException e1)
					{
						Log.WarningLog("A reconnect failed: "+e1.getMessage());
						if(failedReconnections >= Settings.SocketTimeout.MAX_RETRIES-1)
						{
							parser.parse(Protocol.CON_FAIL.toString());
							Log.ErrorLog("Connection reset by beer");
							b_connected = false;
							inputStream.close();
							outputStream.close();
							socket.close();
						}
						failedReconnections++;
					}
				}
				catch(IOException e1)
				{
					if(!b_connected)
					{
						socket.close();
						Log.DebugLog("Socket closed - exiting");
						return;
					}
					
					if(!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown())
					{
						Log.ErrorLog("Socket Closed unexpectedly: "+e1.getMessage());
						parser.parse(Protocol.CON_TIMEOUT.str()+Settings.SocketTimeout.TIMEOUT);
						try
						{
							this.reconnect();
							failedReconnections = 0;
						}
						catch(SocketCreationException e2)
						{
							Log.WarningLog("A reconnect failed: "+e2.getMessage());
							if(failedReconnections >= Settings.SocketTimeout.MAX_RETRIES)
							{
								parser.parse(Protocol.CON_FAIL.toString());
								b_connected = false;
								this.socket.close();
								Log.ErrorLog("Failed to reconnect too often, shutting down");
							}
							failedReconnections++;
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
					if(commandQueue.isEmpty()) 
						outputStream.writeUTF(Protocol.CON_PING.toString());
					
					int counter = 0;
					while(!commandQueue.isEmpty())//Gleiche Frage: Warum ohne Timeout?
					{
						counter++;
						try 
						{
							if(counter % 200 == 0)
								outputStream.flush();
							outputStream.writeUTF(commandQueue.take());
						} 
						catch (InterruptedException e)
						{
							Log.ErrorLog("This shouldn't be interrupted!");
							return;
						}
					}
					outputStream.flush();
					
					if(commandQueue.isEmpty())	
					{
						synchronized(this.senderThread)
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
						socket.close();
						Log.DebugLog("Socket closed - exiting");
						return;
					}
					
					if(!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown())
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
			commandQueue.put(s_Data);			
			synchronized(this.senderThread)
			{
				senderThread.notify();
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
			commandQueue.put(Protocol.CHAT_MESSAGE.str() + s_MSG);
			synchronized(this.senderThread)
			{
				senderThread.notify();
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
			outputStream.close();
			inputStream.close();
			socket.close();
		}
		catch(IOException e)
		{
			Log.WarningLog("Already closed the streams");
		}
		
		
		if(this.socket.isClosed() || !this.socket.isConnected())
		{
			Log.DebugLog("Socket was Closed... Attempting reconnect");
			this.senderThread.interrupt();
			try 
			{
				socket = new Socket();
				socket.connect(new InetSocketAddress(this.serverAddress.getAddress(), this.serverAddress.getPort()), Settings.SocketTimeout.TIMEOUT);
				socket.setSoTimeout(Settings.SocketTimeout.TIMEOUT);
				socket.setKeepAlive(true);
				socket.setTcpNoDelay(true);
			} 
			catch (IOException e) 
			{
				throw new SocketCreationException("Couldn't connect: "+e.getMessage());
			}
			
			try 
			{
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());
				
				//Authenticating with the server
				outputStream.writeUTF(Protocol.CON_AUTH.str()+this.playerID);
				outputStream.flush();
				String s_Answer = inputStream.readUTF();
				if(s_Answer.equals(Protocol.CON_HASH+" "+this.playerID))
				{
					Log.InformationLog("Reconnected!");
				}
				else
				{
					throw new SocketCreationException("Failed to reconnect: the Server didn't accept the playerID "+s_Answer);
				}
				
				commandQueue.clear();			
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
				this.senderThread = new Thread(this);
				senderThread.start();
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
			outputStream.writeUTF(Protocol.CON_PING.toString());
			outputStream.flush();
		} 
		catch (IOException e) 
		{
			throw new SocketCreationException("Failed to send a reconnect ping: "+e.getMessage());
		}
		failedReconnections = 0;
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
