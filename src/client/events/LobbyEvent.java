package client.events;

/**
 * Event which is fired when the clientsocket receives information for the lobby.
 * */
public class LobbyEvent extends NetEvent 
{
	private String section;
	private String message;
	
	public LobbyEvent(Object arg0, int Id, String id, String msg) 
	{
		super(arg0, Id);
		this.section = id;
		this.message = msg;
	}
	public String getSection(){
		return section;
	}
	public String getMsg(){
		return message;
	}
}
