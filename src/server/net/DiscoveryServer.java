package server.net;


import shared.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import shared.InputValidator;

public class DiscoveryServer
implements Runnable
{
	private static final int i_retries = 10;
	private static final int i_sleep = 250;
	private static final int i_Port = Settings.DISCOVERY_DEFAULT_PORT;
	private static final String s_Address = Settings.DISCOVERY_MULITCAST_GROUP;

	//assigns serverName, has to improved (likely by regex to eliminate spaces, etc.)
	private String s_ServerName = InputValidator.UserName(System.getProperty("user.name"));
	
	private int i_ServerPort;
	private InetAddress IA_MultiCastGroup;
	
	private static Thread t_thread;
	private boolean b_active = true;
	
	/**
	 * This is the constructor for the Server-Discovery-Implementation.
	 * <p>
	 * It automatically acquires the ServerIP, only the Serverport needs to be passed on.
	 * 
	 * @param i_ServerPort your Server's Port
	 */
	public DiscoveryServer (int i_ServerPort)
	{
		if (i_ServerPort > 0xFFFF || i_ServerPort < 0)
		{
			Log.DebugLog(i_ServerPort + " isn't a Valid Port - the datagram creation will throw an Error");
		}
		this.i_ServerPort = i_ServerPort;
		
		t_thread = new Thread(this);
		t_thread.setDaemon(true);
		t_thread.setPriority(Thread.MIN_PRIORITY);
		t_thread.start();
	}
	
	/**
	 * Stops the discovery Server
	 */
	public void stop_()
	{
		b_active = false;
	}
	
	
	public void run() 
	{
		Log.InformationLog("New Discovery Server Started");
		MulticastSocket MS_socket;
		try
		{
			MS_socket = SetUp(); 
			
			byte[] ab_MSG = (Protocol.DISC_SERVER.str()+this.i_ServerPort+" "+this.s_ServerName).getBytes();
			DatagramPacket DP_packet = new DatagramPacket(ab_MSG,ab_MSG.length,IA_MultiCastGroup,i_Port);
			
			Enumeration<NetworkInterface> eNI_Interface = NetworkInterface.getNetworkInterfaces();
			List<NetworkInterface> interfaces = new Vector<NetworkInterface>();
			while(eNI_Interface.hasMoreElements())
			{
				NetworkInterface networkInterface = eNI_Interface.nextElement();
				if(networkInterface.isLoopback()||!networkInterface.isUp())
					continue;
				interfaces.add(networkInterface);
			}
			
			if(interfaces.size() == 0)
			{
				Log.ErrorLog("No valid network interface found, multicast discovery offline");
				return;
			}
			
			while(b_active)
			{
				
				Iterator<NetworkInterface> iter = interfaces.iterator();
				while (iter.hasNext())
				{
					NetworkInterface networkInterface = iter.next();
					try
					{
						//Sending it on all Network Interfaces (even into the virtual box)
						MS_socket.setNetworkInterface(networkInterface);
						MS_socket.send(DP_packet);
					}
					catch(IOException e)
					{
						Log.DebugLog("Faulty Adapter : "+networkInterface.getDisplayName()+ " - removing it");
						iter.remove();
						if(interfaces.size() == 0)
						{
							Log.ErrorLog("All network interfaces failed, discovery offline");
							return;
						}
					}
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
		catch(MCSException | SocketException e)
		{
			Log.ErrorLog("Couldn't create the MulticastSocket: "+e.getMessage());
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
