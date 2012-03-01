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
		DiscoveryClient s = new DiscoveryClient(5000);
		Thread t = new Thread(s);
		t.start();
		
		while(true)
		{
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
		}
		

	}

}
