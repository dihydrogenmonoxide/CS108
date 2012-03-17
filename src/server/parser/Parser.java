package server.parser;

import java.util.UUID;

import server.MainServer;
import server.net.PlayerSocket;
import server.players.Player;
import shared.Log;

public class Parser 
{
	
	/**
	 * Parses the given Message and sends the corresponding answers
	 * @param s_MSG the Message the Server socket received
	 * @return What the Socket should answer
	 */
	public void Parse(String s_MSG, PlayerSocket ps_sock)
	{
		Log.InformationLog("Received: \'"+s_MSG+"\'" );
		
		switch(s_MSG.substring(0, 5).toUpperCase())
		{
		case "VAUTH":
			if(s_MSG.length()>6)
			{
				String s_PlayerID = s_MSG.substring(6, s_MSG.length());
				//TODO reconnect the player
				Log.InformationLog("Reconnected a player");
				ps_sock.sendData("VHASH "+s_PlayerID);
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
			//TODO verify nick etc
			ps_sock.getPlayer().setNick(s_MSG);
			break;
			
		case "CCHAT":
			s_MSG = s_MSG.substring(6, s_MSG.length());
			if(s_MSG.toUpperCase().startsWith("/MSG"))
			{
				// TODO implement pvt chatting
				
			}
			else
			{
				MainServer.getPlayerManager().broadcastMessage("CCHAT <"+ps_sock.getPlayer().getNick()+">\t"+s_MSG,	ps_sock.getPlayer());
			}
			break;
			
		default:
			ps_sock.sendData("VERRO not implemented yet");	
			break;
		}		
	}

}
