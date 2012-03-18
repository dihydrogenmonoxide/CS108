package client.events;

public interface LobbyEventListener extends NetEventListener {
	public void received(LobbyEvent evt) throws Exception;
}
