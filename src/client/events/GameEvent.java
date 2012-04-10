package client.events;

import client.net.Message;
import shared.Log;
import shared.Protocol;

public class GameEvent extends NetEvent {
	/**the command reveived.*/
	private Protocol evt;
	/**the message (without the game number).*/
	private Message message;
	public GameEvent(Object arg0, Protocol p, Message message){
		super(arg0, 2);
		this.evt = p;
		this.message = message;
		
		
	}
	
	public int  getGame(){
		return message.getIntArgument(1);
	}
	public Protocol getType(){
		return evt;
	}
	
	public String getMsg(){
		return message.toString();
	}
}
