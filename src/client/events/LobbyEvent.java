package client.events;

import shared.Protocol;

/**
 * Event which is fired when the clientsocket receives information for the lobby.
 * */
public class LobbyEvent extends NetEvent 
{
	private Protocol section;
	private String message;
	
	public LobbyEvent(Object arg0, int Id, Protocol lobbyUpdate, String msg) 
	{
		super(arg0, Id);
		this.section = lobbyUpdate;
		this.message = msg;
	}
	public Protocol getSection(){
		return section;
	}
	public String getMsg(){
		return message;
	}
}
