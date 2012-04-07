package server.exceptions;

@SuppressWarnings("serial")
public class GameObjectBuildException 
extends Exception
{

	public GameObjectBuildException()
	{
		super();
	}
	
	public GameObjectBuildException(String s)
	{
		super(s);
	}
}
