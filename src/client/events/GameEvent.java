package client.events;

import client.net.Message;
import shared.Protocol;

/**A Game Event, which hold information of the change of the gamestatus we just received from the server.*/
public class GameEvent extends NetEvent {
	/**the command received.*/
	private Protocol evt;
	/**the message.*/
	private Message message;
        /**create a new Event.
         @param arg0 the object reference
         @param p   the command issued by the server
         @param message the message
         */
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
	
	public Message getMsg(){
		return message;
	}
}
