package client.events;

import java.util.EventObject;

import shared.ServerAddress;

/**
 *  
 * @author oliver
 *
 */
public class ServerSelectedEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	private ServerAddress a;
	public ServerSelectedEvent(Object source, ServerAddress _a) {
		super(source);
		this.a= _a;
	}
	public ServerAddress getServer(){
		return a;
	}
}
