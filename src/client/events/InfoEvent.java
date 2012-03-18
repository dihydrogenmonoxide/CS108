package client.events;

import java.util.EventObject;

public class InfoEvent extends EventObject {

	private int infoId;
	private String message;
	
	public InfoEvent(Object source,int id, String msg) {
		super(source);
		this.infoId = id;
		this.message = msg;
	}
	
	public int getId(){
		return this.infoId;
	}
	public String getMsg(){
		return this.message;
	}
}
