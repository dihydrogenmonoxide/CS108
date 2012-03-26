package client.lobby;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import client.net.Clientsocket;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import shared.Log;
import shared.ServerAddress;
import shared.User;

import client.events.ChatEvent;
import client.events.ChatEventListener;
import client.events.GameSelectedEvent;
import client.events.GameSelectedListener;
import client.events.NetEvent;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;
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