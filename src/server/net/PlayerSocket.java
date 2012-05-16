package server.net;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import server.parser.Parser;
import server.players.Player;
import shared.Log;
import shared.Protocol;
import shared.Settings;

public class PlayerSocket
implements Runnable
{

	private Socket socket;
	private Parser parser;
	private Player player;
	
	private boolean connectionLost = false;
	
	private Thread senderThread;
	private Thread receiverThread;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private boolean isActive = true;
	
	BlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();
	
	public PlayerSocket(Socket sock, Parser pars)
	{
		this.socket = sock;
		this.parser = pars;
		
		try 
		{
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			
			socket.setKeepAlive(true);
			
			senderThread = new Thread(this);
			senderThread.start();
			receiverThread = new Thread(this);
			receiverThread.start();
		}
		catch (IOException e1) 
		{
			Log.ErrorLog("Clouldn't create an input or output-stream on the Server: "+e1.getMessage());
			isActive = false;
			return;
		}
		
		Log.DebugLog("New Socket open: "+socket.getInetAddress());
	}

	
	public void run()
	{
		if(Thread.currentThread() == this.receiverThread)
		{
			Log.InformationLog("Started a Receiver on the server");
			Receiver();
			Log.InformationLog("Shut down a Receiver on the server");
		}
		else if(Thread.currentThread() == this.senderThread)
		{
			Log.InformationLog("Started a Sender on the server");
			Sender();
			Log.InformationLog("Shut down a Sender on the server");
		}
		else
		{
			Log.DebugLog("RTFM - Socket not running");
			return;
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
					{
						synchronized(this.senderThread)
						{
							try 
							{
								//Wait for Data that needs to be sent and send a VPING if nothing was sent for too long
								Thread.currentThread().wait(Settings.SocketTimeout.TIMEOUT);
								
								if(connectionLost)
									return;
								
								if(commandQueue.isEmpty() && !this.socket.isClosed() && isActive)
								{
									if(connectionLost)
										return;
								}
							} catch (InterruptedException e) 
							{
								Log.DebugLog("Waiting in the send Thread was interrupted");
							}		
						}
					}
					
					int counter = 0;
					while(!commandQueue.isEmpty())
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
						}
					}
					outputStream.flush();//Frage an Frank: Why?
				}
				catch(EOFException e)
				{
					//the client closed the socket without saying good bye
					Log.DebugLog("Client Disconnected without saying bye");
					this.isActive = false;
					
					if(connectionLost)
						return;
							
					connectionLost = true;
					this.player.disconnect();
					return;
				}
				catch(IOException e)
				{
					if(socket.isClosed())
					{
						Log.InformationLog("CLosed a socket");
						inputStream.close();
						outputStream.close();
						return;
					}
					if(!socket.isConnected() || socket.isInputShutdown() || socket.isOutputShutdown())
					{
						Log.InformationLog("Someone just disconnected: " +socket.getInetAddress().getHostAddress());
						socket.close();
						inputStream.close();
						outputStream.close();
						return;
					}
					Log.WarningLog("Failed to receive or send: "+e.getMessage());
				}
			}
			while(isActive);
		}
		catch(IOException e1)
		{
			Log.WarningLog("Failed to close a Socket/stream: "+e1.getMessage());			
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
					parser.Parse(inputStream.readUTF(), this);
				}
				catch(SocketTimeoutException  e)
				{
					this.parser.Parse(Protocol.CON_TIMEOUT.str()+Settings.SocketTimeout.TIMEOUT, this);
					this.isActive = false;
					this.socket.close();
					
					if(connectionLost)
						return;
					
					connectionLost = true;
					if(this.getPlayer() != null)
						this.getPlayer().connectionLost(socket);
					else
						Log.WarningLog("A player disconnected before he was really connected");
					return;
				}
				catch(EOFException e)
				{
					//the client closed the socket without saying good bye
					Log.DebugLog("Client Disconnected without saying bye");
					close();
 					return;
				}
				catch(IOException e)
				{
					if(socket.isClosed())
					{
						Log.InformationLog("CLosed a socket");
						inputStream.close();
						outputStream.close();
						return;
					}
					if(!socket.isConnected() || socket.isInputShutdown() || socket.isOutputShutdown())
					{
						Log.InformationLog("Someone just disconnected: " +socket.getInetAddress().getHostAddress());
						socket.close();
						inputStream.close();
						outputStream.close();
						return;
					}
					
					Log.WarningLog("Failed to receive or send: "+e.getMessage());
				}
			}
			while(isActive);
		}
		catch(IOException e1)
		{
			Log.WarningLog("Failed to close a Socket/stream: "+e1.getMessage());			
		}
	}


	public void sendData(String s_MSG)
	{
		try 
		{
			commandQueue.put(s_MSG);
			synchronized(this.senderThread)
			{
				senderThread.notify();
			}
		}
		catch (InterruptedException e) 
		{
			Log.ErrorLog("\'"+s_MSG+"\' was not sent: "+e.getMessage());
		}
	}	
	
	/**
	 * Returns the Player that corresponds to this socket
	 * @return the player using this socket
	 */
	public Player getPlayer()
	{
		return this.player;
	}
	
	/**
	 * Set the player corresponding to this socket
	 * @param p_player
	 */
	public void setPlayer(Player p_player)
	{
		this.player = p_player;
	}
	
	/**
	 * flushes & closes this socket
	 */
	public void close()
	{
		if(!connectionLost)
		{
			this.sendData(Protocol.CON_EXIT.toString());
			this.player.disconnect();
		}
		this.isActive = false;
		
	}


	public Socket getSocket()
	{
		return socket;
	}
}