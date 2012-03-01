package client.discovery;




import shared.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DiscoveryClient
implements Runnable
{
	private int i_Timeout;
	private List<ServerAddress> lSA_Servers = new ArrayList<ServerAddress>();
	

	/**
	 * This is the constructor for the Client-Discovery-Implementation.
	 * <p>
	 * It automatically acquires the ServerIP and ServerPort and puts them into a list.
	 * <p>
	 * IMPORTANT: Don't forget to start it after creating the class!
	 * @param i_ServerPort your Server's Port
	 */
	public DiscoveryClient(int i_Timeout)
	{
		this.i_Timeout = i_Timeout;
		this.lSA_Servers.clear();
	}
	
	
	public void run() 
	{
		List<Thread> lTH_Threads = new ArrayList<Thread>();
		Log.InformationLog("New Discovery CLient Started");
		try
		{
			Enumeration<NetworkInterface> eNI_Interface = NetworkInterface.getNetworkInterfaces();
			while (eNI_Interface.hasMoreElements())
			{
				NetworkInterface NI_Interface =eNI_Interface.nextElement();
				if(NI_Interface.isLoopback()||!NI_Interface.isUp())
					continue;
				
				//creating a Thread for every socket that receives the server information
				Reciver R_rec = new Reciver();
				R_rec.setOptions(lSA_Servers, NI_Interface);
				Thread T_Thread = new Thread(R_rec);
				T_Thread.start();
				lTH_Threads.add(T_Thread);
			}
		}
		catch(SocketException e)
		{
			
		}
		Log.DebugLog("Started a total of "+lTH_Threads.size()+" Threads");
		
		try 
		{
			Thread.sleep(i_Timeout);
		}
		catch (InterruptedException e)
		{
			
		}
		
		
		
		//stopping all threads after the time ran out
		for(Thread T_Thread : lTH_Threads)
		{
			T_Thread.interrupt();
		}
		
		Log.DebugLog("Stopped all Threads, not looking for servers anymore");
		
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

class Reciver
implements Runnable
{
	private static final int i_retries = 10;
	private static final int i_sleep = 1;
	private static final int i_Port = 9001;
	private static final String s_Address = "225.6.7.8";
	private static final int i_BuffSZ = 250;
	
	
	private List<ServerAddress> lSA_Servers;
	private NetworkInterface NA_Interface;
	private InetAddress IA_MultiCastGroup;
	
	
	public void run() 
	{
		if(this.lSA_Servers == null)
		{
			Log.ErrorLog("Options not set - exiting thread");
			return;
			
		}
		
		MulticastSocket MS_socket;
		try
		{
			MS_socket = SetUp();
			//sends a message like "SERV 192.168.1.11 12345" so others know where to connect to 
			byte[] ab_MSG = new byte[i_BuffSZ];
			DatagramPacket DP_packet = new DatagramPacket(ab_MSG,ab_MSG.length);
			int i_Success = 0;
			int i_Total = 0;
			DatagramPacket DP_alive = new DatagramPacket("ALIV".getBytes(), "ALIV".getBytes().length, IA_MultiCastGroup, i_Port);

			MS_socket.setNetworkInterface(NA_Interface);
			MS_socket.send(DP_alive);
		
			while(true)
			{
				i_Total++;
				try
				{
					MS_socket.receive(DP_packet);
					Parse(DP_packet);
					i_Success++;					
				}
				catch(IOException e)
				{
					Log.WarningLog("Couldn't send packet(out of "+i_Total+" "+i_Success+"were sent successful");
					
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
		catch(SocketException e)
		{
			Log.ErrorLog("Couldn't assignt he network adapter: "+e.getMessage());	
		}
		catch(MCSException e)
		{
			Log.ErrorLog("Couldn't create the MulticastSocket: "+e.getMessage());
		}
		catch (IOException e) 
		{
			Log.ErrorLog("Couldn't send the keep alive: "+e.getMessage());
		}	
	}
	
	public void setOptions(List<ServerAddress> lSA_Servers, NetworkInterface NA_Interface)
	{
		this.lSA_Servers = lSA_Servers;
		this.NA_Interface = NA_Interface;
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
		String s_MSG = new String(DP_MSG.getData(), 0, DP_MSG.getLength());
		if(s_MSG.toUpperCase().startsWith("ALIV"))
		{
			return;
		}
		String[] as_MSG = s_MSG.split("\\s");
		if(as_MSG.length == 2)
		{
			try 
			{
				InetAddress IA_Address = DP_MSG.getAddress();
				int i_Port = Integer.parseInt(as_MSG[1]);
				if(!AlreadyFound(IA_Address, i_Port))
				{
					this.lSA_Servers.add(new ServerAddress(IA_Address, i_Port, this.NA_Interface));
					Log.DebugLog("Found a new Server: "+IA_Address+":"+i_Port);
				}
			}
			catch(NumberFormatException e)
			{
				Log.WarningLog("Seems\'"+s_MSG+"\' doesn\'t represent valid Serverdata (port):"+e.getMessage());
			}
		}
		else
		{
			Log.WarningLog("Seems\'"+s_MSG+"\' doesn\'t represent valid Serverdata");
		}
				
	}
	
	/**
	 * Checks whether the Server is already in the List or not.
	 * <p>
	 * It also checks whether the port is in a valid range or not.
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
}

