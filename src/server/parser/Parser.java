package server.parser;

import java.util.NoSuchElementException;
import java.util.UUID;

import server.MainServer;
import server.Server;
import server.net.PlayerSocket;
import server.players.Player;
import shared.Log;

public class Parser 
{

	public Parser()
	{
		
	}
	
	/**
	 * Parses the given Message and sends the corresponding answers
	 * @param s_MSG the Message the Server socket received
	 * @return What the Socket should answer
	 */
	public void Parse(String s_MSG, PlayerSocket ps_sock)
	{
		switch(s_MSG.substring(0, 5).toUpperCase())
		{
		case "VAUTH"://tested & works ~frank
			if(s_MSG.length()>7)
			{
				String s_PlayerID = s_MSG.substring(6, s_MSG.length());
				Player p = MainServer.getPlayerManager().findUUID(s_PlayerID);
				if(p != null)
				{
					MainServer.printInformation("The Player "+p.getNick()+" just reconnected");
					ps_sock.sendData("VHASH "+s_PlayerID);
					p.reconnect(ps_sock);
					ps_sock.setPlayer(p);
				}
				else
				{
					ps_sock.sendData("VERRO Unknown UUID, you're not allowed to reconnect");
					break;
				}
			}
			else
			{
				String uuid = UUID.randomUUID().toString();
				try
				{
					ps_sock.setPlayer(new Player(uuid, ps_sock, MainServer.getPlayerManager().reserveID()));
					ps_sock.sendData("VHASH "+uuid);
				}
				catch(NoSuchElementException e)
				{
					ps_sock.sendData("VERRO All seats taken - server is full!");
					ps_sock.sendData("VEXIT");
					ps_sock.close();
				}
			}
			break;
			
		case "VPING"://tested & works ~frank
			ps_sock.sendData("VPONG");
			break;
			
		case "VNICK"://tested & works ~frank
			if(s_MSG.length() < 7)
			{
				s_MSG = "anon";
			}
			else
			{
				s_MSG = s_MSG.substring(6, s_MSG.length());
				//remove anything that isn't a-z or 0-9
				s_MSG = s_MSG.replaceAll("[^a-zA-Z0-9]", "");
				if(s_MSG.length() > 15)
					s_MSG = s_MSG.substring(0, 15);
			}
			
			if(s_MSG.length() < 4)
				s_MSG = "anon";
			
			// make sure no nicks are used twice
			Player p = MainServer.getPlayerManager().findPlayer(s_MSG);
			if(p != null)
			{
				int i = 0;
				while(p != null)
				{
					i++;
					p = MainServer.getPlayerManager().findPlayer(s_MSG+i);
				}
				s_MSG = s_MSG+i;
			}
			
			ps_sock.getPlayer().setNick(s_MSG);
			MainServer.getPlayerManager().broadcastMessage_everyone("VNICK "+ps_sock.getPlayer().getID()+" "+s_MSG);
			break;
			
		case "VEXIT"://tested & working ~frank
			ps_sock.close();
			break;
			
		case "VMYID"://tested & working ~frank
			ps_sock.sendData("WMYID "+ps_sock.getPlayer().getID());
			break;
			
		case "GMAKE"://tested & works ~frank
			if(ps_sock.getPlayer().getServer() != null)
			{
				ps_sock.sendData("VERRO already on a server, leave this one to create another one");
				break;
			}
			try
			{
				if(s_MSG.length() > 7)
				{
					s_MSG = s_MSG.substring(6, s_MSG.length());
					if(s_MSG.length() > 15)
						s_MSG = s_MSG.substring(0, 15);
				}
				else
				{
					s_MSG = "UnknownGame";
				}
				
				if(s_MSG.length() < 4)
					s_MSG = "UnknownGame";

				Server serv = new Server(s_MSG ,MainServer.getServerManager().reserveID());
				serv.addPlayer(ps_sock.getPlayer());
				ps_sock.sendData("CCHAT *chatting in server \'"+serv.getServername()+"\'*");
			}
			catch(NoSuchElementException e)
			{
				ps_sock.sendData("VERRO Maximum amount of Servers reached, please join an existing one.");
			}
			break;
			
		case "GJOIN"://tested & works ~frank
			
			if(ps_sock.getPlayer().getServer() != null)
			{
				ps_sock.sendData("VERRO ALready on a server!");
				break;
			}
			
			
			if(s_MSG.length() > 6)
			{
				s_MSG = s_MSG.substring(6, s_MSG.length());
				try
				{
					int id = Integer.parseInt(s_MSG);
					Server serv = MainServer.getServerManager().findServer(id);
					if(serv != null)
					{
						if(serv.getPlayerAmount() >= 5)
						{
							ps_sock.sendData("VERRO this server is full!");
						}
						else
						{
							serv.addPlayer(ps_sock.getPlayer());
							ps_sock.sendData("CCHAT *chatting in server \'"+serv.getServername()+"\'*");
						}
						break;
					}
				}
				catch(NumberFormatException e)
				{
				}
			}
			
			ps_sock.sendData("VERRO the specified server was not found");			
			break;
			
		case "GQUIT"://tested & works ~frank
			if(ps_sock.getPlayer().getServer() != null)
			{
				ps_sock.getPlayer().getServer().removePlayer(ps_sock.getPlayer());
				ps_sock.sendData("CCHAT *chatting in lobby*");
				break;
			}
			ps_sock.sendData("VERRO you can't leave a server you're not in");
			break;
			
		case "CCHAT"://tested & works ~Frank
			if(s_MSG.length() < 7)
			{
				ps_sock.sendData("CCHAT [SERVER]\tYou're a quiet person, are you?");
				break;
			}
			s_MSG = s_MSG.substring(6, s_MSG.length());
			
			if(s_MSG.toUpperCase().startsWith("/MSG"))
			{
				s_MSG = s_MSG.substring(5, s_MSG.length());
				//Split by one or more whitespaces
				String[] s = s_MSG.split("\\s+");
				Player p_player = MainServer.getPlayerManager().findPlayer(s[0]);
				if(p_player != null)
				{
					s_MSG = s_MSG.substring(s[0].length(), s_MSG.length());
					if(p_player == ps_sock.getPlayer())
					{
						ps_sock.sendData("CCHAT [SERVER]\tLast time I checked, you were sane... player.IsTalkingWithOneself = true;");
						break;
					}
					else
					{
						ps_sock.sendData("CCHAT [to "+p_player.getNick()+"]\t"+s_MSG);
						p_player.sendData("CCHAT [from "+p_player.getNick()+"]\t"+s_MSG);
						break;
					}
				}
				else
				{
					ps_sock.sendData("CCHAT [SERVER]\t Player \'"+s[0]+"\' isn't playing on this server...");
					break;
				}
			}
			else
			{
				MainServer.getPlayerManager().broadcastMessage("CCHAT <"+ps_sock.getPlayer().getNick()+">\t"+s_MSG,	ps_sock.getPlayer());
			}
			break;
			
		default:
			Log.InformationLog("Received: \'"+s_MSG+"\'" );
			ps_sock.sendData("VERRO not implemented yet");	
			break;
		}		
	}

}
