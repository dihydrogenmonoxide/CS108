package server.parser;

import java.util.NoSuchElementException;
import java.util.UUID;

import server.MainServer;
import server.exceptions.PlayerNotFoundException;
import server.net.PlayerSocket;
import server.players.Player;
import server.server.Server;
import shared.Log;
import shared.Protocol;
import shared.Settings;

public class Parser 
{


	public Parser()
	{
		
	}
	
	/**
	 * Parses the given Message and sends the corresponding answers
	 * @param data the Message the Server socket received
	 * @return What the Socket should answer
	 */
	public void Parse(String data, PlayerSocket sock)
	{
		String parsedData;
		
		if(data.length() >= 5)
		{
			parsedData = data.substring(0, 5).toUpperCase();
			if(sock.getPlayer() == null && parsedData.compareTo(Protocol.CON_AUTH.toString()) != 0)
			{
				Log.ErrorLog("Critical: A player wasn't connected propperly");
				return;
			}
		}
		else
		{
			Log.WarningLog("Received a too short command");
			return;
		}

		Protocol command = Protocol.fromString(parsedData);
		
		switch(command)
		{
		case CON_AUTH:
			handleAuthentication(data, sock);
			return;
			
		case CON_PING:
			sock.sendData(Protocol.CON_PONG.toString());
			return;
			
		case CON_NICK:
			handleNick(data, sock);
			return;
			
		case CON_EXIT:
			sock.close();
			return;
			
		case CON_MY_ID:
			sock.sendData(Protocol.CON_MY_ID.str() + sock.getPlayer().getID());
			return;
		case LOBBY_SCORE:
			MainServer.getScoreManager().printScore(sock.getPlayer());
			return;
		}
		
		if(!sock.getPlayer().isNickSet())
		{
			sock.sendData(Protocol.CON_ERROR.str()+" Bitte Nick setzen!");
			return;
		}
		
		switch(command)
		{
		case GAME_MAKE:
			handleMakeGame(data, sock);
			break;
			
		case GAME_JOIN:
			handleGameJoin(data, sock);			
			break;
			
		case GAME_QUIT:
			handleGameQuit(sock);
			break;
			
		case CHAT_MESSAGE:
			handleChat(data, sock);
			break;
		case GAME_RESET:
			if(!sock.getPlayer().isInActiveGame())
			{
				sock.sendData(Protocol.CON_ERROR.str()+"Game isn't running");
				break;
			}
			sock.getPlayer().getServer().getLogicManager().resendEverything(sock.getPlayer());
			break;
		case GAME_SPAWN_OBJECT:
			if(!sock.getPlayer().isInActiveGame())
			{
				sock.sendData(Protocol.CON_ERROR.str()+"Game isn't running");
				break;
			}
			sock.getPlayer().getServer().getLogicManager().buildObject(data, sock.getPlayer());
			break;
		case GAME_UPDATE_OBJECT:
			if(!sock.getPlayer().isInActiveGame())
			{
				sock.sendData(Protocol.CON_ERROR.str()+"Game isn't running");
				break;
			}
			sock.getPlayer().getServer().getLogicManager().updateObject(data, sock.getPlayer());
			break;
		case GAME_BUILD_PHASE:
			if(!sock.getPlayer().isInActiveGame())
			{
				sock.sendData(Protocol.CON_ERROR.str()+"Game isn't running");
				break;
			}
			sock.getPlayer().getServer().getLogicManager().finishedBuilding(sock.getPlayer());
			break;
			
		case GAME_VOTESTART:
			sock.getPlayer().voteStart();
			break;
			
		case GAME_UNDO:
			sock.getPlayer().removeObject();
			break;
			
		default:
			Log.InformationLog("Received: \'"+data+"\'" );
			sock.sendData(Protocol.CON_ERROR.str() + "not implemented yet");	
			break;
		}		
	}
	

	private void handleAuthentication(String data, PlayerSocket sock)
	{
		if(data.length()>7)
		{
			String s_PlayerID = data.substring(6, data.length());
			try
			{
				Player p = MainServer.getPlayerManager().findUUID(s_PlayerID);
				MainServer.printInformation("The Player "+p.getNick()+" just reconnected");
				sock.sendData(Protocol.CON_HASH.str()+s_PlayerID);
				p.reconnect(sock);
				sock.setPlayer(p);
			}
			catch(PlayerNotFoundException e)
			{
				sock.sendData(Protocol.CON_ERROR.str() + "Unknown UUID, you're not allowed to reconnect");
			}
		}
		else
		{
			String uuid = UUID.randomUUID().toString();
			try
			{
				sock.setPlayer(new Player(uuid, sock, MainServer.getPlayerManager().reserveID()));
				sock.sendData(Protocol.CON_HASH.str() + uuid);
			}
			catch(NoSuchElementException e)
			{
				sock.sendData(Protocol.CON_ERROR.str() + "All seats taken - server is full!");
				sock.sendData(Protocol.CON_EXIT.toString());
				sock.close();
			}
		}
	}


	private void handleGameQuit(PlayerSocket sock) {
		if(sock.getPlayer().getServer() != null)
		{
			sock.getPlayer().getServer().removePlayer(sock.getPlayer());
			sock.sendData(Protocol.CHAT_MESSAGE.str() + "*du bist nun in der Lobby*");
			return;
		}
		sock.sendData(Protocol.CON_ERROR.str() + "you can't leave a server you're not in");
	}


	private void handleGameJoin(String data, PlayerSocket socket) {
		if(socket.getPlayer().getServer() != null)
		{
			socket.sendData(Protocol.CON_ERROR.str() + "Already on a server!");
			return;
		}
		
		
		if(data.length() > 6)
		{
			data = data.substring(6, data.length());
			try
			{
				int id = Integer.parseInt(data);
				Server serv = MainServer.getServerManager().findServer(id);
				if(serv != null)
				{
					if(serv.getPlayerAmount() >= 5)
					{
						socket.sendData(Protocol.CON_ERROR.str() + "this server is full!");
					}
					else
					{
						serv.addPlayer(socket.getPlayer());
						socket.sendData(Protocol.CHAT_MESSAGE.str() + "*du bist nun im Spiel \'"+serv.getServername()+"\'*");
					}
					return;
				}
			}
			catch(NumberFormatException e)
			{
			}
		}
		
		socket.sendData(Protocol.CON_ERROR.str() + "the specified server was not found");
	}


	/**
	 * Handles the creation of a game
	 * @param data the data
	 * @param sock the socket
	 */
	private void handleMakeGame(String data, PlayerSocket sock) 
	{
		if(sock.getPlayer().getServer() != null)
		{
			sock.sendData(Protocol.CON_ERROR.str() + "already on a server, leave this one to create another one");
			return;
		}
		
		try
		{
			
			
			long population, money;
			
			String difficulty = "";
			
			if(data.length() > 12)
			{
				difficulty = data.substring(6, 11);
				data = data.substring(12, data.length());
				Log.DebugLog("Difficulty: '"+difficulty+"'");
			}
			else
			{
				data = "";
			}
			
			switch(Protocol.fromString(difficulty))
			{
			case DIF_DEMO:
				population = Settings.GameValuesPresentation.DEFAULT_POPULATION;
				money = Settings.GameValuesPresentation.DEFAULT_MONEY;
				break;
			case DIF_RUSH:
				population = Settings.GameValuesRush.DEFAULT_POPULATION;
				money = Settings.GameValuesRush.DEFAULT_MONEY;
				break;
			default:
				population = Settings.GameValuesNormal.DEFAULT_POPULATION;
				money = Settings.GameValuesNormal.DEFAULT_MONEY;
				break;
			}
			

			
			if(data.length() > 3)
			{
				if(data.length() > 15)
					data = data.substring(0, 15);
			}
			else
			{
				data = "UnknownGame";
			}

			Server serv = new Server(data ,MainServer.getServerManager().reserveID(), population, money);
			serv.addPlayer(sock.getPlayer());
			sock.sendData(Protocol.CHAT_MESSAGE.str() + "*du bist nun im Spiel \'"+serv.getServername()+"\'*");
		}
		catch(NoSuchElementException e)
		{
			sock.sendData(Protocol.CON_ERROR.str() + "Maximum amount of Servers reached, please join an existing one.");
		}
	}

	/**
	 * Handles incoming chat messages.
	 * @param s_MSG the message
	 * @param ps_sock the player socket
	 */
	private void handleChat(String s_MSG, PlayerSocket ps_sock) {
		if(s_MSG.length() < 7)
		{
			ps_sock.sendData(Protocol.CHAT_MESSAGE.str() + "[SERVER]\tBist du eine stille Person? Forever Alone!");
			return;
		}
		s_MSG = s_MSG.substring(6);
		
		if (s_MSG.toUpperCase().startsWith(Protocol.CHAT_PREF_PRIVATE.toString()))
		{
			s_MSG = s_MSG.substring(5, s_MSG.length());
			//Split by one or more whitespaces
			String[] s = s_MSG.split("\\s+");
			try
			{
				Player p_player = MainServer.getPlayerManager().findPlayer(s[0]);
				s_MSG = s_MSG.substring(s[0].length(), s_MSG.length());
				if(p_player == ps_sock.getPlayer())
				{
					ps_sock.sendData(Protocol.CHAT_MESSAGE.str() + "[SERVER]\tWas sagt ein Informatiker wenn er auf die Welt kommt? 'Hallo Welt!' player.IsTalkingWithOneself = true;");
					return;
				}
				else
				{
					ps_sock.sendData(Protocol.CHAT_MESSAGE.str() + "[von "+p_player.getNick()+"]\t"+s_MSG);
					p_player.sendData(Protocol.CHAT_MESSAGE.str() + "[zu "+ps_sock.getPlayer().getNick()+"]\t"+s_MSG);
					return;
				}
			}
			catch (PlayerNotFoundException e)
			{
				ps_sock.sendData(Protocol.CHAT_MESSAGE.str() + "[SERVER]\t Spieler \'"+s[0]+"\' ist nicht bekannt...");
			}
		}
		else
		{
			MainServer.getPlayerManager().broadcastMessage(Protocol.CHAT_MESSAGE.str() + "<"+ps_sock.getPlayer().getNick()+">\t"+s_MSG,	ps_sock.getPlayer());
		}
	}

	/**
	 * Assigns a nick to a player.
	 * @param data the message
	 * @param sock the player socket
	 */
	@SuppressWarnings("unused")
	private void handleNick(String data, PlayerSocket sock) {
		if(data.length() < 7)
		{
			data = "anon";
		}
		else
		{
			data = data.substring(6, data.length());
			//remove anything that isn't a-z or 0-9
			data = data.replaceAll("[^a-zA-Z0-9]", "");
			if(data.length() > 15)
				data = data.substring(0, 15);
		}
		
		if(data.length() < 4)
			data = "anon";
		
		// make sure no nicks are used twice
		int i = 0;
		try
		{
			Player p = MainServer.getPlayerManager().findPlayer(data);
			while(true)
			{
				try
				{
					i++;
					p = MainServer.getPlayerManager().findPlayer(data+i);
				}
				catch (PlayerNotFoundException e)
				{
					data = data+i;
					break;
				}
			}
		}
		catch (PlayerNotFoundException e) 
		{
			
		}
		
		sock.getPlayer().setNick(data);
		MainServer.getPlayerManager().broadcastMessage_everyone(Protocol.CON_NICK.str()+sock.getPlayer().getID()+" "+data);
	}

}
