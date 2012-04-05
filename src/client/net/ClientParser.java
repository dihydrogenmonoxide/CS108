package client.net;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import shared.Log;
import shared.Protocol;
import shared.Settings;
import client.data.GamesManager;
import client.data.PlayerManager;
import client.events.ChatEvent;
import client.events.ChatEventListener;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.InfoEvent;
import client.events.InfoEventListener;
import client.events.LobbyEvent;
import client.events.LobbyEventListener;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;

/**
 * Parser for all Messages, fires the correct Event.
 * */
public class ClientParser {
	/**different types of Chat messages.*/
	private enum msgType 
	{
		/**messages from the server.*/
		SERVER,
		/**messages from the client.*/
		CLIENT,
		/**private messages.*/
		PRIVATE,
		/**normal messages.*/
		MSG,
		/**infos for the player.*/
		INFO,
		/**infos concerning the game.*/
		GAME,
		/**debug messages.*/
		DEBUG,
		/**Error messages.*/
		ERROR,
	}

	/**receives a String and fires the apropiate Event.
	 * This method receives a String from the socket and then determines
	 * the correct event / action to be taken.
	 * @param msg the message to be evaluated.
	 * */
	public final void parse(final String msg)
	{

		//catch empty messages
		if (msg.length() < 5) 
		{
			return;
		}

		//catch PONGs
		if (msg.equals(Protocol.CON_PONG.toString()))
		{
			return;
		}

		// invoke the appropiate method.
		try
		{
			Log.DebugLog("Parser, received Message: " + msg);

			Protocol section = getSection(msg);
			switch(section)
			{
			case CONNECTION:
				handleConnection(msg);
				break;
			case GAME: 
				handleGame(msg);
				break;
			case LOBBY: 
				handleLobby(msg);
				break;
			case CHAT:
				handleChat(msg);
				break;
			default: //wrong formated
				Log.DebugLog("-> wrong format" + msg);
			}
		}
		//never ever gonna give you up:
		catch (final Exception e)
		{
			Log.ErrorLog("Parser, error could not parse message");
			Log.ErrorLog("--- " + msg + " ---");
			//XXX uncomment for debugging:
			e.printStackTrace();
		}
	}


	/**
	 * Returns the section of a Message.
	 * @param msg the messages
	 * @return the section as ENUM
	 */
	private Protocol getSection(final String msg) 
	{
		String section = (String) msg.subSequence(0, 1);
		return Protocol.fromString(section);
	}

	/**
	 * Extracts the Command of an Message.
	 * @param msg the message
	 * @return the command as ENUM
	 */
	private Protocol getCommand(final String msg) 
	{
		return Protocol.fromString((String) msg.subSequence(0, 5));
	}

	/**Extracts the Argument of a received Message after the id.
	 * @param msg the message
	 * @return the argument after the id*/
	private String getArgument(final String msg) 
	{
		return msg.substring(10);
	}

	/**extracts the first Game / User or Object id from a message
	 * e.g. "GJOIN 101 oliver"   --> result will be "101" 
	 * @param msg the message received by the parser, with the command.
	 * @return the id */
	private String getId(final String msg) {
		return (String) msg.subSequence(6, 9);
	}

	/**handles the connection messages.
	* NICK : when the client changes its nickname -> inform via chat, replace in hashmap
	* PONG : send by the server to test the connection, is disabled
	* TOUT : connection timeout -> inform via chat
	* FAIL : connection broken -> return to SelectServer
	* EXIT : connection closed by server or client -> return to SelectServer
	* @param msg the message
	* */
	private void handleConnection(final String msg) {
		Log.DebugLog("->connection: " + msg);

		//get the command
		Protocol command = getCommand(msg);

		switch(command)
		{
		case CON_NICK:	
			handleNickChange(msg);
			break;
		case CON_PONG: //just in case
			Log.DebugLog("-->pong from Server");
			return;
		case CON_TIMEOUT:
			Log.ErrorLog("--> connection broke, reconnect");
			sendChatMessage("<client> connection broken, trying to reconnect", msgType.ERROR);
			break;
		case CON_FAIL:
			Log.ErrorLog("--> Connection failed, returning to Select server");
			this.infoReceived(new InfoEvent(msg, -1, "<server disconnected>"));
			break;
		case CON_EXIT:
			Log.ErrorLog("-->connection was closed");
			this.infoReceived(new InfoEvent(msg, -1, "<server closed the connection>"));
			break;	
		case CON_MY_ID:
			Log.DebugLog("-->received my id");
			PlayerManager.setMyId(Integer.valueOf((String) msg.subSequence(6, 9)));
			break;
		default:
			sendChatMessage("<debug>" + msg, msgType.DEBUG);
			break;
		}
	}



	/**handles if a user changes his nickname or joins.
	 * @param msg the message with the nickchange.
	 * */
	private void handleNickChange(final String msg) {
		Log.DebugLog(msg);
		String oldNick = PlayerManager.getNamebyId(getId(msg));
		if (oldNick != null)
		{	
			if (oldNick.equals(getArgument(msg)))
			{
				Log.DebugLog("--> nickchange, but same nick as before: " + oldNick);
			}
			else
			{
				Log.DebugLog("--> nickchange: " + oldNick + " to " + getArgument(msg));
				sendChatMessage("<lobby>changed Nick: " + oldNick + " to " + getArgument(msg), msgType.INFO);
			}
		}
		else
		{
			Log.DebugLog("--> new nick: " + getArgument(msg));
			sendChatMessage("<lobby>new User : " + getArgument(msg), msgType.INFO);
		}
		PlayerManager.addPlayer(getId(msg), (String) getArgument(msg));
	}


	/**
	 * messages for the game.
	 * implemented: 
	 * QUIT : a player leaves the game 
	 * GAME : actual status of a game
	 * JOIN : a player has joined a game
	 * @param msg the message
	 * */
	private void handleGame(final String msg) {
		Log.DebugLog("->game: " + msg);

		//get the command
		Protocol command = getCommand(msg);

		switch(command)
		{
		case GAME_BROADCAST:
			Log.DebugLog("-->game broadcast: " + msg);
			GamesManager.addGame(msg);
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg.substring(6)));
			break;
		case GAME_JOIN:
			Log.DebugLog("-->user joined game: " + msg);
			GamesManager.addPlayer(getId(msg), (String) getArgument(msg).subSequence(0, 3));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_JOIN, msg));
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg));
			break;

		case GAME_QUIT:
			Log.DebugLog("-->user quit game: " + msg);
			GamesManager.removePlayer(getId(msg), (String) getArgument(msg).subSequence(0, 3));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_QUIT, msg));
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg));
			break;

		case GAME_BEGIN:
			sendChatMessage("Starting the game", msgType.GAME);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_BEGIN, msg));
			break;

		case GAME_PAUSE:
			//TODO CLIENTPARSER implement freeze 
			sendChatMessage("game paused", msgType.GAME);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_PAUSE, msg));
			break;

		case GAME_RESUME:
			//TODO CLIENTPARSER implement free GUI
			sendChatMessage("game resumed", msgType.GAME);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_RESUME, msg));
			break;

		case GAME_RESET:
			//TODO CLIENTPARSER implement reset GameManager
			this.gameReceived(new GameEvent(msg, Protocol.GAME_RESET, msg));
			break;

		case GAME_BUILD_PHASE:
			//TODO CLIENTPARSER implement enable User interaction on map.
			this.gameReceived(new GameEvent(msg, Protocol.GAME_BUILD_PHASE, msg));
			break;

		case GAME_ANIMATION_PHASE:
			//TODO CLIENTPARSER implement freeze user interaction on map.
			this.gameReceived(new GameEvent(msg, Protocol.GAME_ANIMATION_PHASE, msg));
			break;

		case GAME_MONEY:
			//TODO CLIENTPARSER implement add the Money to a player via GameManager
			this.gameReceived(new GameEvent(msg, Protocol.GAME_MONEY, msg));
			break;

		case GAME_SPAWN_OBJECT:
			//TODO CLIENTPARSER implement notify GameManager
			this.gameReceived(new GameEvent(msg, Protocol.GAME_SPAWN_OBJECT, msg));
			break;

		case GAME_UPDATE_OBJECT:
			//TODO CLIENTPARSER implement notify GameManager
			this.gameReceived(new GameEvent(msg, Protocol.GAME_UPDATE_OBJECT, msg));
			break;

		case GAME_LAUNCH_MISSILE:
			//TODO CLIENTPARSER implement ARMAGEDDON
			this.gameReceived(new GameEvent(msg, Protocol.GAME_LAUNCH_MISSILE, msg));
			break;

		default:
			Log.ErrorLog("--> wrong formatted " + msg);
		}
	}


	/**
	 * All messages concerning the Lobby / userstatus.
	 * implemented:
	 * QUIT : a user has quit the lobby
	 * JOIN : a user has joined the lobby -> announce in chat
	 * @param msg the message
	 * */
	private void handleLobby(final String msg) {

		Log.DebugLog("->lobby: " + msg);
		Protocol command = getCommand(msg);

		switch(command)
		{
		case LOBBY_QUIT:
			if (PlayerManager.getNamebyId(getId(msg)) == null) { return; }
			Log.DebugLog("-->User quit lobby");
			sendChatMessage("<lobby> User left for a game: " + PlayerManager.getNamebyId(getId(msg)), msgType.INFO);
			break;
		case LOBBY_JOIN:
			if (PlayerManager.getNamebyId((String) msg.subSequence(7, 9)) == null) { return; }
			Log.DebugLog("-->User joined");
			sendChatMessage("<lobby> User joined lobby: " + PlayerManager.getNamebyId(getId(msg)), msgType.INFO);
			break;
		default:
			Log.DebugLog("-->wrong format");
			sendChatMessage("<debug>" + msg, msgType.DEBUG);
		}
	}


	/**
	 * handle chat messages.
	 * determine the type of the messages.
	 * @param msg the message
	 * */
	private void handleChat(final String msg) { 
		Log.DebugLog("->chat: " + msg);

		msgType t = msgType.MSG;
		if (msg.subSequence(1, 14).equals("CHAT [SERVER]"))
		{
			t = msgType.SERVER;
		}
		if (msg.subSequence(1, 11).equals("CHAT [from") || msg.subSequence(1, 9).equals("CHAT [to"))
		{
			t = msgType.PRIVATE;
		}
		if (msg.subSequence(1, 7).equals("CHAT *"))
		{
			t = msgType.INFO;
		}

		sendChatMessage(msg.substring(5), t);

	}

	/**Formats a chatmessage according to its type, then creates an Chatevent and send it.
	 * @param msg the Chatmessage
	 * @param type how to format the message
	 * */
	@SuppressWarnings("unused")
	private void sendChatMessage(final String msg, final msgType type)
	{
		SimpleAttributeSet msgStyle = new SimpleAttributeSet();

		//-- check if we should print debug messages
		if (type == msgType.DEBUG && !Settings.PRINT_DEBUG_MSG_IN_CHAT)
		{
			return;
		}
		
		switch(type)
		{
		case DEBUG:
			StyleConstants.setBackground(msgStyle, Color.orange);
			break;
		case SERVER:
			StyleConstants.setForeground(msgStyle, Color.red);
			break;
		case ERROR:
			StyleConstants.setBold(msgStyle, true);
			StyleConstants.setAlignment(msgStyle, StyleConstants.ALIGN_CENTER);
			StyleConstants.setBackground(msgStyle, Color.red);
			StyleConstants.setForeground(msgStyle, Color.yellow);
			break;
		case INFO:
			StyleConstants.setForeground(msgStyle, Color.magenta);
			
			break;
		case PRIVATE:
			StyleConstants.setAlignment(msgStyle, StyleConstants.ALIGN_RIGHT);
			StyleConstants.setForeground(msgStyle, Color.blue);
			break;
		case GAME:
			StyleConstants.setForeground(msgStyle, Color.green);
			break;
		case CLIENT:
		case MSG:
		default:
			break;
		}
		
		this.chatMsgReceived(new ChatEvent(msg, 12, msg, msgStyle));
	}

	////********************************** LISTENERS

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList infoListeners =  new javax.swing.event.EventListenerList();

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList chatListeners =  new javax.swing.event.EventListenerList();

	/**List of LobbyEventListener.  */
	private javax.swing.event.EventListenerList lobbyListeners =  new javax.swing.event.EventListenerList();

	/**List of GameEventListener.  */
	private javax.swing.event.EventListenerList gameListeners =  new javax.swing.event.EventListenerList();


	/** 
	 * adds ChatEvent listeners.
	 * @param listener
	 */
	public final void addChatEventListener(ChatEventListener listener) 
	{
		chatListeners.add(ChatEventListener.class, listener);
	}

	/**
	 * removes ChatEvent listeners.
	 * @param listener
	 */
	public void removeChatEventListener(ChatEventListener listener) {
		chatListeners.remove(ChatEventListener.class, listener);
	}

	/**
	 * Fires the ChatEvent to all the Listeners
	 * @param evt
	 */
	void chatMsgReceived(ChatEvent evt) {
		Object[] listeners = chatListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==ChatEventListener.class) {
				ChatEventListener listener = (ChatEventListener)listeners[i+1];
				listener.received(evt);
			}
		}
	}

	/** 
	 * adds ChatEvent listeners.
	 * @param listener
	 */
	public void addInfoEventListener(InfoEventListener listener) 
	{
		infoListeners.add(InfoEventListener.class, listener);
	}

	/**
	 * removes ChatEvent listeners.
	 * @param listener
	 */
	public void removeInfoEventListener(InfoEventListener listener) 
	{
		infoListeners.remove(InfoEventListener.class, listener);
	}

	/**
	 * Fires the ChatEvent to all the Listeners
	 * @param evt
	 * @throws NetworkException 
	 */
	void infoReceived(InfoEvent evt)
	{
		Object[] listeners = infoListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==InfoEventListener.class) {
				InfoEventListener listener = (InfoEventListener)listeners[i+1];
				listener.received(evt);
			}
		}
	}


	/** 
	 * adds LobbyEvent listeners.
	 * @param listener
	 */
	public void addLobbyEventListener(LobbyEventListener listener) 
	{
		lobbyListeners.add(LobbyEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeLobbyEventListener(LobbyEventListener listener) 
	{
		lobbyListeners.remove(LobbyEventListener.class, listener);
	}

	/**
	 * Fires the LobbyEvent to all the Listeners
	 * @param evt
	 */
	void lobbyReceived(LobbyEvent evt) 
	{
		Object[] listeners = lobbyListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==LobbyEventListener.class) {
				LobbyEventListener listener = (LobbyEventListener)listeners[i+1];
				try {
					listener.received(evt);
				} catch (Exception e) {
					//should not occur
					e.printStackTrace();
				}
			}
		}
	}

	/** 
	 * adds GameEvent listeners.
	 * @param listener
	 */
	public void addGameEventListener(GameEventListener listener) 
	{
		gameListeners.add(GameEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeGameEventListener(GameEventListener listener) 
	{
		gameListeners.remove(GameEventListener.class, listener);
	}

	/**
	 * Fires the LobbyEvent to all the Listeners
	 * @param evt
	 */
	void gameReceived(GameEvent evt) 
	{
		Object[] listeners = gameListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==GameEventListener.class) {
				GameEventListener listener = (GameEventListener)listeners[i+1];
				listener.received(evt);
			}
		}
	}
}
