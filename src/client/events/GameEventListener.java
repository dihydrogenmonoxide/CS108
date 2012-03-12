package client.events;


public interface GameEventListener extends NetEventListener {
	public abstract void received(GameEvent evt);
}
