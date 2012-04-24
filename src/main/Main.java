package main;

import server.MainServer;

public class Main 
{
	public static void main(String[] args)
	{
		try
		{
			if(args.length > 1)
			{
				if(args[0].toLowerCase().equals("server"))
				{
					int port = Integer.parseInt(args[1]);
					MainServer.startServer(port);
					return;
				}
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println(
					"How to use:\n" +
					"to start a server:\n" +
					"java -jar this.jar server PORT\n\n" +
					"to start a client:\n" +
					"java -jar this.jar client IP:PORT");
		}
		
		//TODO start client here
	}
}
