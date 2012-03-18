package server.net;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import server.MainServer;
import server.parser.Parser;
import server.players.Player;
import shared.Log;

public class PlayerSocket
implements Runnable
{
	private final static int i_Timeout = 4000;
	private Socket S_socket;
	private Parser P_Parser;
	private Player p_player;
	
	private Thread t_thread_send;
	private Thread t_thread_rec;
	private ObjectInputStream OIS_MSG;
	private ObjectOutputStream OOS_MSG;
	private boolean b_active = true;
	
	BlockingQueue<String> bq_Queue = new LinkedBlockingQueue<String>();
	
	public PlayerSocket(Socket S_Sock, Parser P_Parser)
	{
		this.S_socket = S_Sock;
		this.P_Parser = P_Parser;
		
		try 
		{
			OIS_MSG = new ObjectInputStream(S_socket.getInputStream());
			OOS_MSG = new ObjectOutputStream(S_socket.getOutputStream());
			
			S_socket.setKeepAlive(true);
			
			t_thread_send = new Thread(this);
			t_thread_send.start();
			t_thread_rec = new Thread(this);
			t_thread_rec.start();
		}
		catch (IOException e1) 
		{
			Log.ErrorLog("Clouldn't create an input or output-stream on the Server: "+e1.getMessage());
			b_active = false;
			return;
		}
		
		Log.DebugLog("New Socket open: "+S_socket.getInetAddress());
	}

	
	public void run()
	{
		if(Thread.currentThread() == this.t_thread_rec)
		{
			Log.InformationLog("Started a Receiver on the server");
			Receiver();
			Log.InformationLog("Shut down a Receiver on the server");
		}
		else if(Thread.currentThread() == this.t_thread_send)
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
					if(bq_Queue.isEmpty())	
					{
						synchronized(this.t_thread_send)
						{
							try 
							{
								//Wait for Data that needs to be sent and send a VPING if nothing was sent for too long
								Thread.currentThread().wait(i_Timeout);
								
								if(bq_Queue.isEmpty() && !this.S_socket.isClosed())
								{
									//the wait was interrupted by a timeout, this client has lost the connection!
									this.P_Parser.Parse("VTOUT "+i_Timeout, this);
									this.b_active = false;
									this.S_socket.close();
									this.getPlayer().connectionLost();
									return;
								}
							} catch (InterruptedException e) 
							{
								Log.DebugLog("Waiting in the send Thread was interrupted");
							}		
						}
					}
					
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
				}
				catch(EOFException e)
				{
					//the client closed the socket without saying good bye
					Log.DebugLog("Client Disconnected without saying bye");
					this.S_socket.close();
					MainServer.getPlayerManager().removePlayer(this.p_player);
					return;
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
					if(!S_socket.isConnected() || S_socket.isInputShutdown() || S_socket.isOutputShutdown())
					{
						Log.InformationLog("Someone just disconnected: " +S_socket.getInetAddress().getHostAddress());
						S_socket.close();
						OIS_MSG.close();
						OOS_MSG.close();
						return;
					}
					Log.WarningLog("Failed to receive or send: "+e.getMessage());
				}
			}
			while(b_active);
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
					P_Parser.Parse(OIS_MSG.readUTF(), this);
				}
				catch(EOFException e)
				{
					//the client closed the socket without saying good bye
					Log.DebugLog("Client Disconnected without saying bye");
					MainServer.getPlayerManager().removePlayer(this.p_player);
					this.S_socket.close();
					return;
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
					if(!S_socket.isConnected() || S_socket.isInputShutdown() || S_socket.isOutputShutdown())
					{
						Log.InformationLog("Someone just disconnected: " +S_socket.getInetAddress().getHostAddress());
						S_socket.close();
						OIS_MSG.close();
						OOS_MSG.close();
						return;
					}
					
					Log.WarningLog("Failed to receive or send: "+e.getMessage());
				}
			}
			while(b_active);
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
			bq_Queue.put(s_MSG);
			synchronized(this.t_thread_send)
			{
				t_thread_send.notify();
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
		return this.p_player;
	}
	
	/**
	 * Set the player corresponding to this socket
	 * @param p_player
	 */
	public void setPlayer(Player p_player)
	{
		this.p_player = p_player;
	}
	
	/**
	 * flushes & closes this socket
	 */
	public void close()
	{
		this.sendData("VEXIT");
		this.b_active = false;
	}
}