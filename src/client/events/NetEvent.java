package client.events;

import java.util.EventObject;

/**
 *This is the Abstract Class for all Events relating to the Network. 
 * */
public abstract class NetEvent extends EventObject {
	private int messageId;
	public NetEvent(Object arg0, int Id) {
		super(arg0);
		this.messageId = Id;
	}
	
	public int getMsgId(){
		return this.messageId;
	}
}
