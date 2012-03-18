package client.net;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import shared.Log;
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

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList infoListeners =  new javax.swing.event.EventListenerList();

	/**List of ChatEventListener.  */
	private javax.swing.event.EventListenerList chatListeners =  new javax.swing.event.EventListenerList();

	/**List of LobbyEventListener.  */
	private javax.swing.event.EventListenerList lobbyListeners =  new javax.swing.event.EventListenerList();

	/**List of GameEventListener.  */
	private javax.swing.event.EventListenerList gameListeners =  new javax.swing.event.EventListenerList();

	/**receives a String and fires the appropiate Event.
	 * This method receives a String from the socket and then determines
	 * the correct event / action to be taken.
	 * @param msg the message to be evaluated.
	 * */
	public void parse(String msg){

		//catch empty messages
		if (msg.length() < 5) 
		{
			return;
		}

		//catch PONGs
		if(msg.equals("VPONG")){return;}

		try
		{
			Log.DebugLog("Parser, received Message: " + msg);

			String region = (String) msg.subSequence(0, 1);
			msg = msg.substring(1);
			SimpleAttributeSet attrs = new SimpleAttributeSet();

			/*
			 * Here the message is split up and the appropiate event is fired.
			 * Type of messages:
			 * V : message concerning the connection between client and server
			 * G : message concerning the game
			 * L : message conecerning the lobby
			 * C : chat messages
			 * */
			switch(region)
			{

			case "V":
				/*
				 * All the messages concernning the connection
				 * already implemented:
				 * NICK : when the client changes its nickname -> inform via chat
				 * PONG : send by the server to test the connection, is disabled
				 * TOUT : connection timeout -> inform via chat
				 * FAIL : connection broken -> return to SelectServer
				 * EXIT : connection closed by server or client -> return to SelectServer
				 * */
				Log.DebugLog("->connection: " + msg);
				StyleConstants.setForeground(attrs, Color.red);

				//get the subcommand
				String command = (String) msg.subSequence(0, 4);

				switch(command)
				{
				case "NICK":
					Log.DebugLog("-->request nick: " + msg);
					this.chatMsgReceived(new ChatEvent(msg, 12, "<client>Requested Nick: "+msg, attrs));
					break;


				case "PONG": //just in case
					Log.DebugLog("-->pong from Server");
					return;

				case "TOUT":
					Log.ErrorLog("--> connection broke, reconnect");
					this.chatMsgReceived(new ChatEvent(msg, 12, "<client> connection broken, trying to reconnect", attrs));
					break;
				case "FAIL":
					Log.ErrorLog("--> Connection failed, returning to Select server");
					this.infoReceived(new InfoEvent(msg,-1,"<server disconnected>"));
					break;
				case "EXIT":
					Log.ErrorLog("-->connection was closed");
					this.infoReceived(new InfoEvent(msg,-1,"<server closed the connection>"));
					break;	
				default:

					this.chatMsgReceived(new ChatEvent(msg, 12, "<debug>"+msg, attrs));

					break;
				}

				break;


			case "G": 
				/*messages for the game:
				 * implemented: 
				 * none
				 * 
				 * */
				Log.DebugLog("->game: " + msg);

				this.gameReceived(new GameEvent(msg, 12));

				break;


			case "L": 
				/*All messages concerning the Lobby / userstatus
				 * implemented:
				 * none
				 * 
				 * */
				Log.DebugLog("->lobby: " + msg);

				this.lobbyReceived(new LobbyEvent(msg, 12));


				break;



			case "C":
				/*
				 * chat messages
				 * Server messages are colored red.
				 * Private messages blue
				 * */
				Log.DebugLog("->chat: " + msg);

				if(msg.subSequence(0,13).equals("CHAT [SERVER]")){
					StyleConstants.setBackground(attrs, Color.red);
				}
				if(msg.subSequence(0,10).equals("CHAT [from")){
					StyleConstants.setForeground(attrs, Color.blue);
				}

				//fire Event
				this.chatMsgReceived(new ChatEvent(msg, 12, msg.substring(5), attrs));

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
		chatListeners.add(LobbyEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeLobbyEventListener(LobbyEventListener listener) 
	{
		chatListeners.remove(LobbyEventListener.class, listener);
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
		chatListeners.add(GameEventListener.class, listener);
	}

	/**
	 * removes LobbyEvent listeners.
	 * @param listener
	 */
	public void removeGameEventListener(GameEventListener listener) 
	{
		chatListeners.remove(GameEventListener.class, listener);
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
