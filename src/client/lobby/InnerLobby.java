package client.lobby;

import javax.swing.JPanel;

import client.events.GameSelectedEvent;
import client.events.GameSelectedListener;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;
/**Class which displays the lobby of a Server.
 * Allow to chat with other users, start a game, join a game.
 * Will throw an Event if a Game is chosen to join.*/
public class InnerLobby extends JPanel {
	/**List of listeners. */
	private javax.swing.event.EventListenerList listeners =  new javax.swing.event.EventListenerList();
	
	
	
	
	
	
	
	
	
	
	
	/** 
	 * adds serverSelected listeners.
	 * @param listener
	 */
    public void addGameSelectedListener(GameSelectedListener listener) {
        listenerList.add(GameSelectedListener.class, listener);
    }

    /**
     * removes serverSelected listeners.
     * @param listener
     */
    public void removeGameSelectedListener(GameSelectedListener listener) {
        listenerList.remove(GameSelectedListener.class, listener);
    }

   /**
    * Fires the ServerSelectedEvent to all the Listeners
    * @param evt
    */
    void gameSelected(GameSelectedEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == GameSelectedListener.class) {
                GameSelectedListener listener = (GameSelectedListener) listeners[i + 1];
				listener.gameSelected(evt);
            }
        }
    }
}
