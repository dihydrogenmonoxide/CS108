package client.events;

import java.util.EventObject;

import shared.ServerAddress;

/**
 *  Small event, when Users selects a Game to join.
 * @author oliver
 *
 */
public class GameSelectedEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	private ServerAddress a;
	private String gameId;
	public GameSelectedEvent(Object source, ServerAddress _a, String _gameId) {
		super(source);
		this.a= _a;
		this.gameId = _gameId;
	}
	public ServerAddress getServer(){
		return a;
	}
	public String getGame(){
		return this.gameId;
	}
}
