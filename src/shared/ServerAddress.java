package shared;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class ServerAddress 
{
	private InetAddress IA_InetAddress;
	private int i_Port;
	private NetworkInterface NI_Interface;
	
	/**
	 * This Class stores the discovered Server data.
	 * 
	 * @param IA_InetAddress The Server's IP
	 * @param i_Port The Server's Port
	 */
	
	public ServerAddress(InetAddress IA_InetAddress, int i_Port, NetworkInterface NI_Interface)
	{
		this.IA_InetAddress = IA_InetAddress;
		this.i_Port = i_Port;
		this.NI_Interface = NI_Interface;
	}
	
	/**
	 * 
	 * @return The Server's Address for you to connect to.
	 */
	public InetAddress getAddress()
	{
		//bad practice in c++, but this is java, isn't it?
		return this.IA_InetAddress;
	}
	
	/**
	 * 
	 * @return The Server's Port for you to connect to.
	 */
	public int getPort()
	{
		return this.i_Port;
	}
	
	/**
	 * 
	 * @return The Interface it was discovered on
	 */
	public NetworkInterface getInterface()
	{
		return NI_Interface;
	}

}
