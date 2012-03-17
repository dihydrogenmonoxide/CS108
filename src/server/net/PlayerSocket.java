package server.net;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.parser.Parser;
import shared.Log;

class PlayerSocket
implements Runnable
{
	private Socket S_socket;
	private Parser P_Parser;
	
	public PlayerSocket(Socket S_Sock, Parser P_Parser)
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
					if(s_Answer != "")
					{
						OOS_MSG.writeUTF(s_Answer);				
						OOS_MSG.flush();
					}
				}
				catch(EOFException e2)
				{
					//the client closed the socket wirthout saying good bye
					Log.DebugLog("Client Disconnected without saying bye");
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
		catch (IOException e1) 
		{
			Log.ErrorLog("Clouldn't create an input or output-stream: "+e1.getMessage());
		}
	}	
}