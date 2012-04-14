package client.events;

import java.util.EventListener;

public interface InfoEventListener extends EventListener 
{
		public abstract void received(InfoEvent evt);
}
