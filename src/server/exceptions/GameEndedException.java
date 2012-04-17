package server.exceptions;

import server.players.Player;

@SuppressWarnings("serial")
public class GameEndedException 
extends Exception

{
	private Player winner=null;
	public GameEndedException()
	{
		super();
	}
	
	public GameEndedException(String s)
	{
		super(s);
	}
	
	public GameEndedException(String s, Player winner)
	{
		super(s);
		this.winner=winner;
	}
	public Player getWinner(){
		return this.winner;
	}
}

