package client.events;

/**
 * Event fired when the Client is receiving a chatmsg.
 * */
public class ChatEvent extends NetEvent {
	private String msg;
	public ChatEvent(Object arg0, int m, String msg) {
		super(arg0, m);
		// TODO Auto-generated constructor stub
	}

	public String getMsg(){
		return this.msg;
	}
}
