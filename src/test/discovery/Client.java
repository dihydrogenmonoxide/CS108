package test.discovery;


import client.discovery.*;
import shared.*;

public class Client 
{

	/**
	 * Testing whether it's able to find servers or not.
	 * 
	 * @param args does nothing
	 */
	public static void main(String[] args) 
	{
		DiscoveryClient s = new DiscoveryClient();
		Thread t = new Thread(s);
		t.start();
		
	
		for(ServerAddress a : s.GetList())
		{
			System.out.println("IP: "+a.getAddress()+" Port: "+a.getPort());
			System.out.println("----");
		}
		
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			
			e.printStackTrace();
		}
		
		for(ServerAddress a : s.GetList())
		{
			System.out.println("IP: "+a.getAddress()+" Port: "+a.getPort()+" Servername: "+a.getServerName());
			System.out.println("----");
		}
		
		System.exit(0);
	}

}
