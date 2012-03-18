package client.events;

public interface ChatEventListener extends NetEventListener {
	public abstract void received(ChatEvent evt);
}
