package server.parser;

import java.util.Formatter;
import java.util.UUID;

import server.MainServer;
import server.net.PlayerSocket;
import server.players.Player;
import shared.Log;

public class Parser 
{
	private Formatter f_fmt;
	public Parser()
	{
		f_fmt = new Formatter();
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
		case "VAUTH":
			if(s_MSG.length()>6)
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
				}
			}
			else
			{
				String uuid = UUID.randomUUID().toString();
				ps_sock.setPlayer(new Player(uuid, ps_sock));
				ps_sock.sendData("VHASH "+uuid);
			}
			break;
			
		case "VPING":
			ps_sock.sendData("VPONG");
			break;
			
		case "VNICK":
			s_MSG = s_MSG.substring(6, s_MSG.length());
			//remove anything that isn't a-z or 0-9
			s_MSG = s_MSG.replaceAll("[^a-zA-Z0-9]", "");
			if(s_MSG.length() < 4)
				s_MSG = "anon";
			if(s_MSG.length() > 15)
				s_MSG = s_MSG.substring(0, 15);
		
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
			//TODO use this in future
			String ret = this.f_fmt.format("VNICK %02d %s", ps_sock.getPlayer().getID(), s_MSG).toString();
			Log.DebugLog(ret);
			ps_sock.getPlayer().setNick(s_MSG);
			MainServer.getPlayerManager().broadcastMessage(ret, ps_sock.getPlayer());
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
