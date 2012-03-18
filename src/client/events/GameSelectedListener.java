package client.events;
import java.util.EventListener;
public interface GameSelectedListener extends EventListener {
    public abstract void gameSelected(GameSelectedEvent e);
}