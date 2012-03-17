package client.events;
import java.util.EventListener;
public interface ServerSelectedListener extends EventListener {
    public abstract void serverSelected(ServerSelectedEvent e);
}