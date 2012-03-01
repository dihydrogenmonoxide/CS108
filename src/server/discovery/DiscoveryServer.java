package server.discovery;


import shared.*;

import java.io.IOException;
import java.net.DatagramPacket;
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
	
	private int i_ServerPort;
	private InetAddress IA_MultiCastGroup;
	
	/**
	 * This is the constructor for the Server-Discovery-Implementation.
	 * <p>
	 * It automatically acquires the ServerIP, only the Serverport needs to be passed on.
	 * <p> 
	 * IMPORTANT: Don't forget to start it after creating the class!
	 * @param i_ServerPort your Server's Port
	 */
	public DiscoveryServer(int i_ServerPort)
	{
		if(i_ServerPort > 0xFFFF || i_ServerPort < 0)
			Log.DebugLog(i_ServerPort+" isn't a Valid Port - the datagram creation will throw an Error");
		this.i_ServerPort = i_ServerPort;
	}
	
	
	public void run() 
	{
		Log.InformationLog("New Discovery Server Started");
		MulticastSocket MS_socket;
		try
		{
			MS_socket = SetUp();
			//sends a message like "SERV 192.168.1.11 12345" so others know where to connect to 
			byte[] ab_MSG = ("SERV "+InetAddress.getLocalHost()+" "+this.i_ServerPort).getBytes();
			DatagramPacket DP_packet = new DatagramPacket(ab_MSG,ab_MSG.length,IA_MultiCastGroup,i_Port);
			int i_Success = 0;
			int i_Total = 0;
			Log.DebugLog("Packet: \'"+new String(DP_packet.getData())+"\'");
			while(true)
			{
				i_Total++;
				try 
				{
					MS_socket.send(DP_packet);
					i_Success++;
				}
				catch (IOException e) 
				{
					Log.WarningLog("Failed to sent packet("+i_Success+" out of "+i_Total+" were sent: "+e.getMessage());
				}
				
				try 
				{
					Thread.sleep(i_sleep);
				} catch (InterruptedException e) 
				{
					Log.DebugLog("Thread.Sleep failed :"+e.getMessage());
				}							
			}
		}
		catch(MCSException e)
		{
			Log.ErrorLog("Couldn't create the MulticastSocket: "+e.getMessage());
		} catch (UnknownHostException e) {
			Log.ErrorLog("Couldn't get this computer\'s Address "+e.getMessage());
		}		
	}
	
	private MulticastSocket SetUp() 
			throws MCSException
	{
		int i_retry = i_retries;
		MulticastSocket MS_socket;
		while(i_retry >= 0)
		{
			try 
			{
				IA_MultiCastGroup = InetAddress.getByName(s_Address);
				MS_socket = new MulticastSocket(i_Port);
				MS_socket.joinGroup(IA_MultiCastGroup);
				
				Log.InformationLog("Socket created after "+(i_retries-i_retry)+" failed attempts");
				
				return MS_socket;
			}
			catch (UnknownHostException e) 
			{
				Log.WarningLog(e.getMessage());
				if(i_retry <= 1)
					throw new MCSException("Unknown Host:"+e.getMessage());
			}
			catch (IOException e) 
			{
				Log.WarningLog(e.getMessage());
				if(i_retry <= 1)
					throw new MCSException("IOException:"+e.getMessage());
			}
			i_retry--;						
		}
		return null;
	}
}
