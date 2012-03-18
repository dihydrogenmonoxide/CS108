package client.events;

import javax.swing.text.SimpleAttributeSet;

/**
 * Event fired when the Client is receiving a chatmsg.
 * */
public class ChatEvent extends NetEvent {
	private String msg;
	SimpleAttributeSet attrs;
	public ChatEvent(Object arg0, int m, String message, SimpleAttributeSet attributes) {
		super(arg0, m);
		this.msg = message;
		this.attrs = attributes;
	}
	/**Return the Message hold by the Event.*/
	public String getMsg()
	{
		return this.msg;
	}
	/** holds the Style Attributs of the Message.*/
	public SimpleAttributeSet getAttrs(){
		return this.attrs;
	}
	/**Returns the Message with a Newspace at the end*/
	public String getMsgNsp() {
		return this.msg+"\n";
	}
}
