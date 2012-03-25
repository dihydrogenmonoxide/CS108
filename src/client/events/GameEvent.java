package client.events;

import shared.Protocol;

public class GameEvent extends NetEvent {
	/**the command reveived.*/
	private Protocol evt;
	/**the message (without the game number).*/
	private String message;
	/**The game number (between 0 and 99).*/
	private int game;
	public GameEvent(Object arg0, Protocol p, String plain){
		super(arg0, 2);
		this.evt = p;
		this.game = Integer.valueOf((String) plain.subSequence(8, 9));
		this.message = plain.substring(10);
	}
	/*constructs a game event formally*/
	public GameEvent(Object arg0, Protocol e, int game, String msg) {
		super(arg0, e.ordinal());
		this.evt = e;
		this.message = msg;
		this.game = game;
	}
	
	public int  getGame(){
		return game;
	}
	public Protocol getType(){
		return evt;
	}
	
	public String getMsg(){
		return message;
	}
}
