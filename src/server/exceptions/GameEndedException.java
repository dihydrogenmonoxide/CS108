package server.exceptions;

@SuppressWarnings("serial")
public class GameEndedException 
extends Exception
{

	public GameEndedException()
	{
		super();
	}
	
	public GameEndedException(String s)
	{
		super(s);
	}
}

