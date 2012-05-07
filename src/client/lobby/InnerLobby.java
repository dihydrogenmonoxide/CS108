package client.lobby;

import client.events.GameSelectedEvent;
import client.events.GameSelectedListener;
import client.net.Clientsocket;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import shared.User;
/**Class which displays the lobby of a Server.
 * Allow to chat with other users, start a game, join a game.
 * Will throw an Event if a Game is chosen to join.*/
public class InnerLobby extends JPanel {


	/**List of listeners. */
	private javax.swing.event.EventListenerList listeners =  new javax.swing.event.EventListenerList();

	/**Socket / Connection to server.*/
	private Clientsocket socket;
	/**hold all the user information*/
	private User user;

	/**initializes the Lobby.
	 * @param s the socket to the server
	 * @param u the current user*/
	public InnerLobby(final Clientsocket s, final User u, JFrame lobbyParent){
                super();
		this.user = u;
		this.socket = s;
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		GamesPanel games = new GamesPanel(socket, lobbyParent);
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		this.add(games, c);
		
		ChatPanel chat = new ChatPanel(socket);
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		this.add(chat, c);

		this.setOpaque(false);
	}

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