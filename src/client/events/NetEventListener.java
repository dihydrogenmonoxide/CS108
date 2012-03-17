package client.events;

import java.util.EventListener;

public interface NetEventListener extends EventListener {
	public abstract void received(NetEvent evt);
}
