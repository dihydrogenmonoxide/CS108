package client;

import client.lobby.ClientLobby;
import shared.User;

public class MainClient 
{
	public MainClient() 
	{
	User user = new User();
	ClientLobby lobby = new ClientLobby(user);
	}
}
