package main;

import client.MainClient;
import server.MainServer;
import shared.Log;
import shared.Settings;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			Log.InformationLog("Starte SwissDefcon ");
			if (args.length > 0)
			{
				if (args[0].toLowerCase().equals("server"))
				{
					if (1 < args.length)
					{
						int port = Integer.parseInt(args[1]);
						MainServer.startServer(port);
					}
					else
					{
						MainServer.startServer();
					}
					return;
				}

				if (args[0].toLowerCase().equals("client"))
				{
					if (1 < args.length)
					{
						// -- start client with a specified server
						String[] address = args[1].split(":");
						if(address.length == 1)
						{
							String ip = address[0];
							int port = Settings.SERVER_DEFAULT_PORT;
							MainClient.startClient(ip, port);
						}
						else if (address.length == 2)
						{
							String ip = address[0];
							int port = Integer.parseInt(address[1]);
							MainClient.startClient(ip, port);
						}
					}
					return;
				}
			}
			else
			{
				/**if no arguments are passed, we'll just run a client in discovery mode*/
				MainClient.startClient();
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println("Anleitung:\n" + "Server starten:\n"
					+ "java -jar dieses.jar server"
					+ "Server mit Portangabe starten:\n"
					+ "java -jar dieses.jar server PORT\n\n"
					+ "Client starten:\n"
					+ "java -jar dieses.jar client IP:PORT \n"
					+ "Client mit automatischer Suche starten:\n"
					+ "java -jar dieses.jar client\n\n" + "Starte beides:"
					+ "java -jar dieses.jar beides");

		}
	}
}
