package client.net;

import client.data.GamesManager;
import client.data.PlayerManager;
import client.data.RunningGame;
import client.events.*;
import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import shared.Log;
import shared.Protocol;
import shared.Settings;

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
	 * @param message the message to be evaluated.
	 * */
	public final void parse(final String message)
	{
		try
		{
                        Message msg = new Message(message);
                        
                        //-- don't parse pongs
                        if(msg.getCommand() == Protocol.CON_PONG)
                        {
                            return;
                        };
                       
                        //Log.DebugLog("Parser, received Message: " + msg);
                        
                        //-- divide and conquer
			switch (msg.getSection())
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
			Log.ErrorLog("--- " + message + " ---");
			//XXX uncomment for debugging:
			//e.printStackTrace();
		}
	}

        
	/**handles the connection messages.
	* NICK : when the client changes its nickname -> inform via chat, replace in hashmap
	* PONG : send by the server to test the connection, is disabled
	* TOUT : connection timeout -> inform via chat
	* FAIL : connection broken -> return to SelectServer
	* EXIT : connection closed by server or client -> return to SelectServer
	* @param msg the message
	* */
	private void handleConnection(final Message msg) {
		Log.DebugLog("->connection: " + msg);

		switch(msg.getCommand())
		{
		case CON_NICK:	
			handleNickChange(msg);
			break;
		case CON_PONG: //just in case
			Log.DebugLog("-->pong from Server");
			return;
		case CON_TIMEOUT:
			Log.ErrorLog("--> connection broke, reconnect");
			sendChatMessage("<client> Verbindung unterbrochen, neuer Versuch", msgType.ERROR);
			break;
		case CON_FAIL:
			Log.ErrorLog("--> Connection failed, returning to Select server");
			this.infoReceived(new InfoEvent(msg, -1, "<konnte nicht mit Server verbinden>"));
			break;
		case CON_EXIT:
			Log.ErrorLog("-->connection was closed");
			this.infoReceived(new InfoEvent(msg, -1, "<Server hat die Verbindung unterbrochen>"));
			break;	
		case CON_MY_ID:
			Log.DebugLog("-->received my id");
			PlayerManager.setMyId(msg.getIntArgument(1));
			break;
		default:
			sendChatMessage("<debug>" + msg, msgType.DEBUG);
			break;
		}
	}



	/**handles if a user changes his nickname or joins.
	 * @param msg the message with the nickchange.
	 * */
	private void handleNickChange(final Message msg) {
		Log.DebugLog(""+msg);
		final String oldNick = PlayerManager.getNamebyId(msg.getIntArgument(1));
                final String newNick = msg.getStringArgument(2);
		if (oldNick != null)
		{	
			if (oldNick.equals(newNick))
			{
				Log.DebugLog("--> nickchange, but same nick as before: " + oldNick);
			}
			else
			{
				Log.DebugLog("--> nickchange: " + oldNick + " to " + newNick);
				sendChatMessage("<lobby>Benutzername gewechselt: " + oldNick + " ist nun " + newNick, msgType.INFO);
			}
		}
		else
		{
			Log.DebugLog("--> new nick: " + newNick);
			sendChatMessage("<lobby>neuer Mitspieler : " + newNick, msgType.INFO);
		}
		PlayerManager.addPlayer(msg.getIntArgument(1), (String) newNick);
	}


	/**
	 * messages for the game.
	 * implemented: 
	 * QUIT : a player leaves the game 
	 * GAME : actual status of a game
	 * JOIN : a player has joined a game
	 * @param msg the message
	 * */
	private void handleGame(final Message msg) {
		Log.DebugLog("->game: " + msg);

		switch(msg.getCommand())
		{
		case GAME_BROADCAST:
			Log.DebugLog("-->game broadcast: " + msg);
			GamesManager.addGame(msg.getIntArgument(1),msg.getIntArgument(2),msg.getStringArgument(3));
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg));
			break;
		case GAME_JOIN:
			Log.DebugLog("-->user joined game: " + msg);
			GamesManager.addPlayer(msg.getIntArgument(1), msg.getIntArgument(2));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_JOIN, msg));
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg));
			break;

		case GAME_QUIT:
			Log.DebugLog("-->user quit game: " + msg);
			GamesManager.removePlayer(msg.getIntArgument(1), msg.getIntArgument(2));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_QUIT, msg));
			this.lobbyReceived(new LobbyEvent(msg, 12, Protocol.LOBBY_UPDATE , msg));
			break;

		case GAME_BEGIN:
                        RunningGame.initGame(msg.getIntArgument(1), msg.getIntArgument(2));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_BEGIN, msg));
                        sendChatMessage("Lasst das Spiel beginnen", msgType.GAME);
			break;

		case GAME_PAUSE:
			RunningGame.setPaused(true);
			sendChatMessage("Spiel pausiert", msgType.GAME);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_PAUSE, msg));
			break;

		case GAME_RESUME:
			RunningGame.setPaused(false);
			sendChatMessage("Spiel wird fortgeführt", msgType.GAME);
			this.gameReceived(new GameEvent(msg, Protocol.GAME_RESUME, msg));
			break;

		case GAME_RESET:
			RunningGame.hardReset();
			this.gameReceived(new GameEvent(msg, Protocol.GAME_RESET, msg));
			break;

		case GAME_BUILD_PHASE:
			RunningGame.setBuildTime(msg.getIntArgument(1));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_BUILD_PHASE, msg));
			break;

		case GAME_ANIMATION_PHASE:
			RunningGame.setAnim(msg.getIntArgument(1));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_ANIMATION_PHASE, msg));
			break;

		case GAME_MONEY:
			RunningGame.setMoney(msg.getLongArgument(1));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_MONEY, msg));
			break;

		case GAME_UPDATE_OBJECT:
			RunningGame.updateObj(msg.getProtocolArgument(1), msg.getIntArgument(2), msg.getIntArgument(3), msg.getIntArgument(4), msg.getIntArgument(5), msg.getIntArgument(6));
			this.gameReceived(new GameEvent(msg, Protocol.GAME_UPDATE_OBJECT, msg));
			break;
                    
                case GAME_POPULATION:
                        RunningGame.setPop(msg.getLongArgument(1));
                        break;
                case GAME_LOST_OR_WON:
                        Log.InformationLog("Spiel beendet:" + msg);
                        this.gameReceived(new GameEvent(msg, Protocol.GAME_LOST_OR_WON, msg));
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
	private void handleLobby(final Message msg) {

		Log.DebugLog("->lobby: " + msg);
		switch(msg.getCommand())
		{
		case LOBBY_QUIT:
			if (PlayerManager.getNamebyId(msg.getIntArgument(1)) == null) { return; }
			Log.DebugLog("-->User quit lobby");
			sendChatMessage("<lobby> ein Spieler hat die Lobby verlassen: " + PlayerManager.getNamebyId(msg.getIntArgument(1)), msgType.INFO);
			break;
		case LOBBY_JOIN:
			if (PlayerManager.getNamebyId(msg.getIntArgument(1)) == null) { return; }
			Log.DebugLog("-->User joined");
			sendChatMessage("<lobby> neuer Spieler in der Lobby: " + PlayerManager.getNamebyId(msg.getIntArgument(1)), msgType.INFO);
			break;
		default:
			Log.DebugLog("-->wrong format");
			//TODO comment this for final version
                        sendChatMessage("<debug>" + msg, msgType.DEBUG);
		}
	}


	/**
	 * handle chat messages.
	 * determine the type of the messages.
	 * @param message the message
	 * */
	private void handleChat(final Message msg) { 
	//	Log.DebugLog("->chat: " + msg);
            
                final String message = msg.toString();
                
		msgType t = msgType.MSG;
		if (message.subSequence(1, 14).equals("CHAT [SERVER]"))
		{
			t = msgType.SERVER;
		}
		if (message.subSequence(1, 11).equals("CHAT [von") || message.subSequence(1, 9).equals("CHAT [zu"))
		{
			t = msgType.PRIVATE;
		}
		if (message.subSequence(1, 7).equals("CHAT *"))
		{
			t = msgType.INFO;
		}

		sendChatMessage(message.substring(5), t);

	}

	/**Formats a chatmessage according to its type, then creates an Chatevent and send it.
	 * @param msg the Chatmessage
	 * @param type how to format the message
	 * */
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
		for (int i=0; i<listeners.length; i+=2) {//Frage an Oli, Why? geht es nicht mit instanceof?
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
		for (int i=0; i<listeners.length; i+=2) {//Frage an Oli: Why?
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
		for (int i=0; i<listeners.length; i+=2) {//Frage an Oli: Why?
			if (listeners[i]==LobbyEventListener.class) {
				LobbyEventListener listener = (LobbyEventListener)listeners[i+1];
				try {
					listener.received(evt);
				} catch (Exception e) {
					//should not occur
					//e.printStackTrace();
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
