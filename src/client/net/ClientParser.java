package client.net;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import shared.Log;
import shared.Protocol;
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
	
	/**assigns each userid a nickname.*/
	private HashMap<String, String> users = new HashMap<String, String>();
	/**assigns each gameID a gameName.*/
	private HashMap<String, String> games = new HashMap<String, String>();

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList infoListeners =  new javax.swing.event.EventListenerList();

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList chatListeners =  new javax.swing.event.EventListenerList();

	/**List of LobbyEventListener.  */
	private javax.swing.event.EventListenerList lobbyListeners =  new javax.swing.event.EventListenerList();

	/**List of GameEventListener.  */
	private javax.swing.event.EventListenerList gameListeners =  new javax.swing.event.EventListenerList();
	
	/**holds the default attributes for messages.*/
	private SimpleAttributeSet defaultStyle;

	/**receives a String and fires the appropiate Event.
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

		//reset message style
		defaultStyle = new SimpleAttributeSet();
		
		try
		{
			Log.DebugLog("Parser, received Message: " + msg);

			Protocol section = getSection(msg);
			
			//holds the basic style for messages
			

			/*
			 * Here the message is split up and the appropiate method is invoked.
			 * Type of messages:
			 * V : message concerning the connection between client and server
			 * G : message concerning the game
			 * L : message conecerning the lobby
			 * C : chat messages
			 * */
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
		catch (final Exception e)
		{
			Log.ErrorLog("Parser, error could not parse message");
		}
	}


	/**
	 * Returns the section of a command.
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
	
	
	/**handles the connection messages.
	 * @param msg the message*/
	private void handleConnection(final String msg) {
		/*
		 * All the messages concernning the connection
		 * already implemented:
		 * NICK : when the client changes its nickname -> inform via chat, replace in hashmap
		 * PONG : send by the server to test the connection, is disabled
		 * TOUT : connection timeout -> inform via chat
		 * FAIL : connection broken -> return to SelectServer
		 * EXIT : connection closed by server or client -> return to SelectServer
		 * */
		Log.DebugLog("->connection: " + msg);
		StyleConstants.setForeground(defaultStyle, Color.red);

		//get the subcommand
		Protocol command = getCommand(msg);

		switch(command)
		{
		case CON_NICK:	
			handleNickChange(msg, defaultStyle);
			break;
		case CON_PONG: //just in case
			Log.DebugLog("-->pong from Server");
			return;
		case CON_TIMEOUT:
			Log.ErrorLog("--> connection broke, reconnect");
			this.chatMsgReceived(new ChatEvent(msg, 12, "<client> connection broken, trying to reconnect", defaultStyle));
			break;
		case CON_FAIL:
			Log.ErrorLog("--> Connection failed, returning to Select server");
			this.infoReceived(new InfoEvent(msg, -1, "<server disconnected>"));
			break;
		case CON_EXIT:
			Log.ErrorLog("-->connection was closed");
			this.infoReceived(new InfoEvent(msg, -1, "<server closed the connection>"));
			break;	
		default:
			this.chatMsgReceived(new ChatEvent(msg, 12, "<debug>" + msg, defaultStyle));
			break;
		}
	}



	/**handles if a user changes his nickname or joins.
	 * @param msg the message with the nickchange.
	 * @param attrs the default styling.
	 * */
	private void handleNickChange(final String msg, SimpleAttributeSet attrs) {
		Log.DebugLog(msg);
		String oldNick = users.get(msg.subSequence(7, 9));
		if (oldNick != null)
		{	
			Log.DebugLog("--> nickchange: " + oldNick + " to " + msg.substring(10));
			this.chatMsgReceived(new ChatEvent(msg, 12, "<lobby>changed Nick: " + oldNick + " to " + msg.substring(10), attrs));
		}
		else
		{
			Log.DebugLog("--> new nick: " + msg.substring(10));
			this.chatMsgReceived(new ChatEvent(msg, 12, "<lobby>new User : " + msg.substring(10), attrs));
		}
		users.put((String) msg.subSequence(7, 9), (String) msg.substring(10));
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
		
		//get the subcommand
		Protocol command = getCommand(msg);

		switch(command)
		{
		case GAME_BROADCAST:
			Log.DebugLog("-->game broadcast: " + msg);
			this.lobbyReceived(new LobbyEvent(msg, 12, "GAME", msg.substring(7)));
			break;
		case GAME_JOIN:
			Log.DebugLog("-->user joined game: " + msg);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_JOIN, msg));
			break;
		
		case GAME_QUIT:
			Log.DebugLog("-->user quit game: " + msg);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_QUIT, msg));
			break;
			
		default:
			Log.ErrorLog("--> wrong formatted " + msg);
		}
		//this.gameReceived(new GameEvent(msg, 12));
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
			if (users.get(msg.subSequence(7, 9)) == null) { return; }
			Log.DebugLog("-->User quit lobby");
			this.chatMsgReceived(new ChatEvent(msg, 12, "<lobby> User left for a game: " + users.get(msg.subSequence(7, 9)), defaultStyle));
			break;
		case LOBBY_JOIN:
			if (users.get(msg.subSequence(7, 9)) == null) { return; }
			Log.DebugLog("-->User joined");
			this.chatMsgReceived(new ChatEvent(msg, 12, "<lobby> User joined lobby: " + users.get(msg.subSequence(7, 9)), defaultStyle));
			break;
		default:
			Log.DebugLog("-->wrong format");
			this.chatMsgReceived(new ChatEvent(msg, 12, "<debug>" + msg, defaultStyle));
		}
	}


	/**
	 * handle chat messages.
	 * Server messages are colored red.
	 * Private messages blue.
	 * @param msg the message
	 * */
	private void handleChat(final String msg) { 
		Log.DebugLog("->chat: " + msg);

		if (msg.subSequence(1, 14).equals("CHAT [SERVER]"))
		{
			StyleConstants.setBackground(defaultStyle, Color.red);
		}
		if (msg.subSequence(1, 11).equals("CHAT [from") || msg.subSequence(1, 9).equals("CHAT [to"))
		{
			StyleConstants.setForeground(defaultStyle, Color.blue);
		}

		//fire Event
		this.chatMsgReceived(new ChatEvent(msg, 12, msg.substring(5), defaultStyle));
	}



	////********************************** LISTENERS

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
