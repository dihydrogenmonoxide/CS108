package test.discovery;


import client.discovery.*;
import shared.*;

public class Client 
{

	/**
	 * Testing wheter it's able to find servers or not.
	 * 
	 * @param args does nothing
	 */
	public static void main(String[] args) 
	{
		DiscoveryClient s = new DiscoveryClient();
		Thread t = new Thread(s);
		t.start();
		
		while(true)
		{
			for(ServerAddress a : s.GetList())
			{
				System.out.println("IP: "+a.getAddress()+" Port: "+a.getPort());				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

}
