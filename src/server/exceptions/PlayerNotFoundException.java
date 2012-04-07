package server.exceptions;

@SuppressWarnings("serial")
public class PlayerNotFoundException 
extends Exception
{

	public PlayerNotFoundException()
	{
	}

	public PlayerNotFoundException(String arg0)
	{
		super(arg0);
	}
}
