package client.events;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import client.net.NetworkException;

public interface InfoEventListener extends EventListener 
{
		public abstract void received(InfoEvent evt);
}
