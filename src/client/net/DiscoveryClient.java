package client.net;




import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import shared.*;

public class DiscoveryClient
implements Runnable
{
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
		Log.InformationLog("New Discovery CLient Started");
		List<Thread> lT_Threads = new ArrayList<Thread>();
		try
		{
			Enumeration<NetworkInterface> eNI_Interface = NetworkInterface.getNetworkInterfaces();
			while (eNI_Interface.hasMoreElements())
			{
				NetworkInterface NI_Interface =eNI_Interface.nextElement();
				if(NI_Interface.isLoopback()||!NI_Interface.isUp())
					continue;
				
				//creating a Thread for every socket that receives the server information
				Receiver R_rec = new Receiver(this, NI_Interface);
				Thread T_Thread = new Thread(R_rec);
				T_Thread.start();
				lT_Threads.add(T_Thread);
				
				//sleeping a little bit to prevent some errors
				try
				{
					Thread.sleep(10);
				}
				catch(InterruptedException e)
				{
					//Exit on interrupt
					return;					
				}
								
				
			}
		}
		catch(SocketException e)
		{
			
		}	
		
		Log.DebugLog("Started " + lT_Threads.size() + " Threads");
		
		try
		{
			Thread.sleep(2500);
		}
		catch(InterruptedException e)
		{
			
		}
		
		for(Thread T_Thread: lT_Threads)
		{
			T_Thread.interrupt();
		}
		
		Log.DebugLog("Stopped " + lT_Threads.size() + " Threads");
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
	
	protected synchronized void Parse(DatagramPacket DP_MSG)
	{
		String s_MSG = new String(DP_MSG.getData(), 0, DP_MSG.getLength());
		
		if(s_MSG.toUpperCase().startsWith(Protocol.DISC_ALIVE.toString()))
		{
			return;
		}
		String[] as_MSG = s_MSG.split("\\s");
		if (as_MSG.length == 3)
		{
			try 
			{
				InetAddress IA_Address = DP_MSG.getAddress();
				int i_Port = Integer.parseInt(as_MSG[1]);
				String s_ServerName = as_MSG[2];
				if( ! AlreadyFound(IA_Address, i_Port) )
				{
					lSA_Servers.add(new ServerAddress(IA_Address, i_Port, null, s_ServerName));
					Log.DebugLog("Found a new Server: "+IA_Address+":"+i_Port+" Servername: "+s_ServerName);
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
		for(ServerAddress SA_Address: lSA_Servers)
		{
			if(SA_Address.getAddress().equals(IA_Address) && i_Port == SA_Address.getPort()) return true;
		}
		return false;		
	}	
}

class Receiver
implements Runnable
{
	private static final int i_retries = 10;
	private static final int i_sleep = 1;
	private static final int i_Port = Settings.DISCOVERY_DEFAULT_PORT;
	private static final String s_Address = Settings.DISCOVERY_MULITCAST_GROUP;
	private static final int i_BuffSZ = 250;
	
	
	private DiscoveryClient DC_Client;
	private NetworkInterface NA_Interface;
	private InetAddress IA_MultiCastGroup;
	
	
	public Receiver (DiscoveryClient DC_Client, NetworkInterface NA_Interface)
	{
		this.NA_Interface = NA_Interface;
		this.DC_Client = DC_Client;
		
	}
	
	
	public void run() 
	{
		if(this.DC_Client == null || this.NA_Interface == null)
		{
			Log.ErrorLog("Null ptr recived");
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
			DatagramPacket DP_alive = new DatagramPacket(Protocol.DISC_ALIVE.toString().getBytes(), Protocol.DISC_ALIVE.toString().getBytes().length, IA_MultiCastGroup, i_Port);

			MS_socket.setNetworkInterface(NA_Interface);
			MS_socket.send(DP_alive);
		
			while(true)
			{
				i_Total++;
				try
				{
					MS_socket.setNetworkInterface(NA_Interface);
					MS_socket.receive(DP_packet);
					this.DC_Client.Parse(DP_packet);
					i_Success++;	
					Thread.sleep(i_sleep);
				}
				catch (InterruptedException e) 
				{
					return;
				}
				catch(IOException e)
				{
					Log.WarningLog("Couldn't send packet(out of "+i_Total+" "+i_Success+"were sent successful");
					
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

