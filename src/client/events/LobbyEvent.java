package client.events;

import client.net.Message;
import shared.Protocol;

/**
 * Event which is fired when the clientsocket receives information for the lobby.
 * */
public class LobbyEvent extends NetEvent 
{
	private Protocol section;
	private Message message;
	
	public LobbyEvent(Object arg0, int Id, Protocol lobbyUpdate, Message msg) 
	{
		super(arg0, Id);
		this.section = lobbyUpdate;
		this.message = msg;
	}
	public Protocol getSection(){
		return section;
	}
	public Message getMsg(){
		return message;
	}
}
