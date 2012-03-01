package server.discovery;


import shared.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class DiscoveryServer
implements Runnable
{
	private static final int i_retries = 10;
	private static final int i_sleep = 250;
	private static final int i_Port = 9001;
	private static final String s_Address = "225.6.7.8";
	
	private MulticastSocket MS_socket;
	
	
	public DiscoveryServer()
	{
		
		
	}

	@Override
	public void run() 
	{
		
		
	}
	
	private void SetUp(int i_ServerPort) 
			throws MCSException
	{
		int i_retry = i_retries;
		while(i_retry >= 0)
		{
			try 
			{
				InetAddress IA_MultiCastGroup = InetAddress.getByName(s_Address);
				MS_socket = new MulticastSocket(i_Port);
				MS_socket.joinGroup(IA_MultiCastGroup);
			}
			catch (UnknownHostException e) 
			{
				if(i_retry <= 1)
					throw new MCSException("Unknown Host:"+e.getMessage());
			}
			catch (IOException e) 
			{
				if(i_retry <= 1)
					throw new MCSException("IOException:"+e.getMessage());
			}
			i_retry--;						
		}
	}
}
