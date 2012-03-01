package client.discovery;




import shared.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryClient
implements Runnable
{
	private static final int i_retries = 10;
	private static final int i_sleep = 1;
	private static final int i_Port = 9001;
	private static final String s_Address = "225.6.7.8";
	private static final int i_BuffSZ = 250;
	
	private InetAddress IA_MultiCastGroup;
	
	private List<ServerAddress> lSA_Servers = new ArrayList<ServerAddress>();
	

	/**
	 * This is the constructor for the Client-Discovery-Implementation.
	 * <p>
	 * It automatically acquires the ServerIP and ServerPort and puts them into a list.
	 * <p>
	 * IMPORTANT: Don't forget to start it after creating the class!
	 * @param i_ServerPort your Server's Port
	 */
	public DiscoveryClient()
	{
		this.lSA_Servers.clear();
	}
	
	
	public void run() 
	{
		Log.InformationLog("New Discovery Server Started");
		MulticastSocket MS_socket;
		try
		{
			MS_socket = SetUp();
			//sends a message like "SERV 192.168.1.11 12345" so others know where to connect to 
			byte[] ab_MSG = new byte[i_BuffSZ];
			DatagramPacket DP_packet = new DatagramPacket(ab_MSG,ab_MSG.length);
			int i_Success = 0;
			int i_Total = 0;
			while(true)
			{
				i_Total++;
				try 
				{
					MS_socket.receive(DP_packet);
					Parse(DP_packet);
					i_Success++;
				}
				catch (IOException e) 
				{
					Log.WarningLog("Failed to recive packet("+i_Success+" out of "+i_Total+" were recived: "+e.getMessage());
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

	private void Parse(DatagramPacket DP_MSG)
	{
		String s_MSG = DP_MSG.getData().toString();
		String[] as_MSG = s_MSG.split(" ");
		if(as_MSG.length == 3)
		{
			try 
			{
				InetAddress IA_Address = InetAddress.getByName(as_MSG[1]);
				int i_Port = Integer.parseInt(as_MSG[2]);
				if(!AlreadyFound(IA_Address, i_Port))
					this.lSA_Servers.add(new ServerAddress(IA_Address, i_Port));
				else
					Log.DebugLog("Already found the server "+IA_Address+":"+i_Port);
			} 
			catch (UnknownHostException e) 
			{
				Log.WarningLog("Seems\'"+s_MSG+"\' doesn\'t represent valid Serverdata :"+e.getMessage());
			}
			catch(NumberFormatException e)
			{
				Log.WarningLog("Seems\'"+s_MSG+"\' doesn\'t represent valid Serverdata :"+e.getMessage());
			}
		}
		else
		{
			Log.WarningLog("Seems\'"+s_MSG+"\' doesn\'t represent valid Serverdata");
		}
				
	}

	/**
	 * Checks wheter the Server is already int he List or not.
	 * <p>
	 * It also checks wheter the port is in a valid range or not.
	 * 
	 * @param IA_Address The Server's IP
	 * @param i_Port The Server's Port
	 * @return Whether the Server already is in the list or not.
	 */
	private boolean AlreadyFound(InetAddress IA_Address, int i_Port)
	{
		if(i_Port > 0xFFFF||i_Port < 0)
		{
			Log.DebugLog(i_Port+" is no valid Port - not adding it to the list");
					return false;
		}
		for(ServerAddress SA_Address: this.lSA_Servers)
		{
			if(SA_Address.getAddress().equals(IA_Address) && i_Port == SA_Address.getPort()) return true;
		}
		return false;		
	}
	
	
	/**
	 * Clears the List of Servers it found so far.
	 */
	public void ClearServerlist()
	{
		this.lSA_Servers.clear();
	}
	
	/**
	 * 
	 * @return The Servers it discovered so far (unsorted).
	 */
	public List<ServerAddress> GetList()
	{
		return this.lSA_Servers;
	}
}

