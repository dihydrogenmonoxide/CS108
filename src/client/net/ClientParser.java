package client.net;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import shared.Log;
import client.events.ChatEvent;
import client.events.ChatEventListener;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.LobbyEvent;
import client.events.LobbyEventListener;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;

/**
 * Parser for all Messages, fires the correct Event.
 * */
public class ClientParser {

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList chatListeners =  new javax.swing.event.EventListenerList();

	/**List of LobbyEventListener.  */
	private javax.swing.event.EventListenerList lobbyListeners =  new javax.swing.event.EventListenerList();

	/**List of GameEventListener.  */
	private javax.swing.event.EventListenerList gameListeners =  new javax.swing.event.EventListenerList();


	public void parse(String msg){

		//catch empty messages
		if (msg.length() < 5) 
		{
			return;
		}

		//catch PONGs
		if(msg.equals("VPONG")){return;}
		
		Log.DebugLog("Parser, received Message: " + msg);

		String region = (String) msg.subSequence(0, 1);
		msg = msg.substring(1);
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		
		
		switch(region)
		{
		
		case "V": // Connection messages
			Log.DebugLog("->connection: "+msg);
			StyleConstants.setForeground(attrs, Color.red);
			
			//get the subcommand
			String command = (String) msg.subSequence(0, 4);
			
			switch(command)
			{
			case "NICK":
				Log.DebugLog("-->request nick: "+msg);
				this.chatMsgReceived(new ChatEvent(msg, 12, "<client>Requested Nick: "+msg, attrs));
				break;
				
				
			case "PONG": //just in case
				Log.DebugLog("-->pong from Server");
				return;
			}
			
			//Just for Testing purpose:
			// TODO remove the following (or comment it)
			this.chatMsgReceived(new ChatEvent(msg, 12, "<debug>"+msg, attrs));

			break;
			
			
		case "G": //Game messages
			Log.DebugLog("->game: "+msg);
			
			this.gameReceived(new GameEvent(msg, 12));

			break;
			
			
		case "L": //Lobby messages
			Log.DebugLog("->lobby: "+msg);
			
			this.lobbyReceived(new LobbyEvent(msg, 12));


			break;
			
			
			
		case "C":	//Chat messages	
			Log.DebugLog("->chat: "+msg);
			

			//declare Attributes (color, font, etc...)
			StyleConstants.setBackground(attrs, Color.white);
			//fire Event
			this.chatMsgReceived(new ChatEvent(msg, 12, msg, attrs));

			break;
			
			
		default: //wrong formated
			Log.DebugLog("Parser, received wrong formatted message:" + msg);
		}	
	}



	////********************************** LISTENERS

	/** 
	 * adds ChatEvent listeners.
	 * @param listener
	 */
	public void addChatEventListener(ChatEventListener listener) 
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
	 * adds LobbyEvent listeners.
	 * @param listener
	 */
	public void addLobbyEventListener(LobbyEventListener listener) {
		chatListeners.add(LobbyEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeLobbyEventListener(LobbyEventListener listener) {
		chatListeners.remove(LobbyEventListener.class, listener);
	}

	/**
	 * Fires the LobbyEvent to all the Listeners
	 * @param evt
	 */
	void lobbyReceived(LobbyEvent evt) {
		Object[] listeners = lobbyListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==LobbyEventListener.class) {
				LobbyEventListener listener = (LobbyEventListener)listeners[i+1];
				listener.received(evt);
			}
		}
	}

	/** 
	 * adds GameEvent listeners.
	 * @param listener
	 */
	public void addGameEventListener(GameEventListener listener) {
		chatListeners.add(GameEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeGameEventListener(GameEventListener listener) {
		chatListeners.remove(GameEventListener.class, listener);
	}

	/**
	 * Fires the LobbyEvent to all the Listeners
	 * @param evt
	 */
	void gameReceived(GameEvent evt) {
		Object[] listeners = gameListeners.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==GameEventListener.class) {
				GameEventListener listener = (GameEventListener)listeners[i+1];
				listener.received(evt);
			}
		}
	}
}
