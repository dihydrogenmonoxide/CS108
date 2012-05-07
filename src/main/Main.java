package main;

import client.MainClient;
import server.MainServer;
import shared.Log;

public class Main 
{
	public static void main(String[] args)
	{
		try
		{
                        Log.InformationLog("Starting SwissDefcon "+args.toString());
			if(args.length > 1)
			{
				if(args[0].toLowerCase().equals("server"))
				{
					int port = Integer.parseInt(args[1]);
					MainServer.startServer(port);
					return;
				}
                                
                                if(args[0].toLowerCase().equals("client"))
				{
                                        if(1 < args.length)
                                        {
                                            //-- start client with a specified server
                                            String[] address = args[1].split(":");
                                            String ip = address[0];
                                            int port = Integer.parseInt(address[1]);
                                            MainClient.startClient(ip, port);
                                        }
                                        else
                                        {
                                            //-- start client in server search mode
                                            MainClient.startClient();
                                        }
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
					"java -jar this.jar client IP:PORT"+
                                        "or to automatically search a server:"+
                                        "java -jar this.jar client");
                                        
		}
	}
}
