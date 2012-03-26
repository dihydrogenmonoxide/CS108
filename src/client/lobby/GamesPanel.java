package client.lobby;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.Log;
import shared.Protocol;

import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.LobbyEvent;
import client.events.LobbyEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;


public class GamesPanel extends JPanel {

	/**the connection socket.*/
	private Clientsocket socket;
	/**button to join a game.*/
	private JButton joinButton;

	/**button to leave a  game.*/
	private JButton leaveButton;
	
	/**button to create a new game.*/
	private JButton createButton;

	/**holds all the open games.*/
	private HashMap<Integer, GameOverview> games = new HashMap<Integer, GameOverview>();
	
	/**holding the info for the UI, just a simplified version of games.*/
	private Vector<Vector<String>> gamesData = new Vector<Vector<String>>();
	
	/**table with all the games.*/
	private JTable gamesTable;

	/**scrollable list for the games.*/
	private JScrollPane gamesScroll;

	/**panel where for creating a new game.*/
	private JScrollPane createScroll;

	/**label where creation settings are shown.*/
	private JLabel createSetting;

	/**label where game options are shown.*/
	private JLabel gameSettings;
	
	/**Button to start a game*/
	private JButton startButton;
	
	/**how many players are in the game.*/
	public int playerCount;
	

	/**Inner class holding all the Infos about a game.*/
	private class GameOverview {
		/**the id of the game.*/
		private int id;

		/**which players are in there.*/
		private HashMap<Integer, String> players = new HashMap<Integer, String>();
		/**the name of game ^^.*/
		private String name;


		/**initializes the game info from the Eventparser.
		 * @param init the message from the parser.
		 * */
		public GameOverview(final String init)
		{
			id = Integer.valueOf((String) init.subSequence(0, 2));
			playerCount = Integer.valueOf((String) init.subSequence(3, 4));
			name = init.substring(5);
			Log.DebugLog("GameOverview created: " + this.toString());
		}
		
		/**returns an Vector containing the id, the playercount and the name.
		 * @return the vector for the GUI.*/
		public Vector<String> makeInfo()
		{
			Vector<String> r = new Vector<String>();
			r.add(String.valueOf(id));
			r.add(String.valueOf(playerCount));
			r.add(name);
			return r;
		}
		/**returns the id of the game.
		 * @return id the id*/
		public int getId()
		{
			return id;
		}
		/**returns how many players are in this game.
		 * @return playerCount how many Players are in the game.*/
		public int getPlayerCount()
		{
			return playerCount;
		}
		/**converts this to a String for logging.
		 * @return String representation.*/
		public String toString()
		{
			return id + " " + playerCount + " " + name;
		}

		/**adds a player to a game.
		 * @param msg the message received by the parser.*/
		public void addPlayer(final String msg) {
			Log.DebugLog("player added to game " + name + ":" + msg);
			int playerId = Integer.valueOf((String) msg.subSequence(1, 3));
			players.put(playerId, msg.substring(4));
		}
		/**removes a player from a game.
		 * @param msg the message received by the parser.*/
		public void removePlayer(final String msg) {
			int playerId = Integer.valueOf((String) msg.subSequence(1, 3));
			players.remove(playerId);
		}
	}

	/**creates a dialog where the user can join, create and start games.*/
	public GamesPanel(Clientsocket s) 
	{
		this.socket = s;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		//TODO move the game listing in a separate class

		Vector<String> columns = new Vector<String>();
		columns.add("ID");
		columns.add("Spieler");
		columns.add("Name");

		gamesTable = new JTable(gamesData, columns)
		{
			//hack to disable editing
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(final int rowIndex, final int vColIndex) {
				return false;
			}
		};
		gamesTable.setCellSelectionEnabled(false);
		gamesTable.setRowSelectionAllowed(true);
		gamesTable.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		gamesTable.setFillsViewportHeight(true);


		

		gamesScroll = new JScrollPane(gamesTable);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 50;
		c.ipadx = 50;
		c.weightx = 0.0;
		c.gridwidth = 4;
		c.gridheight = 10;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gamesScroll, c);

		gameSettings = new JLabel();
		gameSettings.setText("Infos zum ausgewählten Spiel");		
		gamesScroll.setPreferredSize(new Dimension(300, 80));
		gameSettings.setBackground(new Color(255, 255, 255));
		gameSettings.setOpaque(true);
		gameSettings.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.CENTER;
		c.gridwidth = 4;
		c.gridheight = 3;
		c.gridx = 0;
		c.gridy = 11;
		c.insets = new Insets(10, 0, 0, 0);
		this.add(gameSettings, c);

		joinButton = new JButton("beitreten");
		//gamesScroll.setPreferredSize(new Dimension(20, 20));
		c.fill = GridBagConstraints.HORIZONTAL;
		joinButton.setEnabled(false);
		c.ipady = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridx = 0;
		c.gridy = 14;
		this.add(joinButton, c);

		

		leaveButton = new JButton("verlassen");
		leaveButton.setEnabled(true);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridx = 3;
		c.gridy = 14;
		this.add(leaveButton, c);
		
		createScroll = new JScrollPane();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		c.weightx = 3;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 17;
		this.add(createScroll, c);


		//TODO move the game creating to a separate class
		createSetting = new JLabel();
		createSetting.setText("daten zum erstellenden Spiel");
		createSetting.setBackground(new Color(255, 255, 255));
		createSetting.setOpaque(true);
		createSetting.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.CENTER;
		c.gridwidth = 4;
		c.gridheight = 3;
		c.gridx = 0;
		c.gridy = 24;
		this.add(createSetting, c);

		createButton = new JButton("erstellen");
		createButton.setEnabled(true);
		createScroll.setPreferredSize(new Dimension(20, 20));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 1;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 27;
		this.add(createButton, c);
		
		startButton = new JButton("Spiel starten");
		startButton.setEnabled(true);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx=0.0;
		c.gridwidth=1;
		c.gridx=3;
		c.gridy=27;
		this.add(startButton,c);
		
		

		this.setOpaque(false);
		
		
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				socket.sendData(Protocol.GAME_MAKE.toString());
				
			}
		});
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new GameFrame();
				ClientLobby.close();
				
			}
		});
		
		
		// LISTENERS
		gamesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
				GameOverview g = games.get(Integer.valueOf(temp.get(0)));
				if (g != null)
				{
					gameSettings.setText(g.toString());
				}
				else
				{
					gameSettings.setText("Please Select a game");
				}
				//gameSettings.setText(temp.get(0) + " : " + temp.get(1) + " : " + temp.get(2));
				
			}
			
		});
		
		joinButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0<=gamesTable.getSelectedRow())
				{
					Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
					socket.sendData(Protocol.GAME_JOIN.str() + makeGameId(Integer.valueOf(temp.get(0))));
					gamesTable.setEnabled(false);
				}

			}
		});
		
		leaveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
					socket.sendData(Protocol.GAME_QUIT.toString());
					gamesTable.setEnabled(true);
			}
		});
		
		socket.addGameEventListener(new GameEventListener() {
			@Override
			public void received(GameEvent evt) {
				Log.DebugLog("GamePanel received message "+evt.getMsg());
				
				GameOverview g = games.get(evt.getGame());
				switch (evt.getType()){
				case GAME_JOIN:
					g.addPlayer(evt.getMsg());
					break;
				case GAME_QUIT:
					g.removePlayer(evt.getMsg());
					break;
				default:
					break;
				}
				
			}

			@Override
			public void received(NetEvent evt) {
			}
			
		});

		socket.addLobbyEventListener(new LobbyEventListener()
		{
			@Override
			public void received(final LobbyEvent evt) throws Exception 
			{
				Log.DebugLog("GameList: " + evt.getSection() + " " + evt.getMsg());
				String section = evt.getSection();
				String message = evt.getMsg();
				switch(section)
				{
				case "GAME": //XXX not nice
					GameOverview g = new GameOverview(message);
					games.put(g.getId(), g);
					refreshGameList();
					break;
				default:
					break;
				}
			}

			@Override
			public void received(final NetEvent evt) 
			{
			}
		});
	}
	/**refreshes a game to the list.
	 * Checks if there are games with 0 players and deletes them
	 * */
	private void refreshGameList() {
		
		Collection<GameOverview> c = games.values();
		Iterator<GameOverview> gIter = c.iterator();

		gamesData.clear();
		//iterate through all games
		while (gIter.hasNext())
		{
			GameOverview g = gIter.next();

			//remove all empty games
			if (g.getPlayerCount() <= 0)
			{
				games.remove(g.getId());
			}
			else
			{
				gamesData.add(g.makeInfo());
			}
		}

		gamesTable.updateUI();
		gamesTable.repaint();
		if (0 < gamesTable.getRowCount())
		{
			joinButton.setEnabled(true);
		}
		else
		{
			joinButton.setEnabled(false);
		}
		
	}
	/**formats an int to an correct gameId eg 2XX.
	 * @param i the int.
	 * @return the proper GameId.
	 * */
	public final String makeGameId(final int i)
	{
		String str = "2" + String.format("%02d", i);
		return str;
	}
}
