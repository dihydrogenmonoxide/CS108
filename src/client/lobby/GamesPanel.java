package client.lobby;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import shared.Log;
import shared.Protocol;

import client.data.GamesManager;
import client.events.LobbyEvent;
import client.events.LobbyEventListener;
import client.events.NetEvent;
import client.game.GameFrame;
import client.net.Clientsocket;

/**a panel which offers the option to join and list games. You can also create games.*/
public class GamesPanel extends JPanel {

	/**serial id, obviously must have ^^. but never pi.*/
	private static final long serialVersionUID = 314159265358979L;
	
	/**the connection socket.*/
	private Clientsocket socket;
	/**button to join a game.*/
	private JButton joinButton;

	/**button to leave a  game.*/
	private JButton leaveButton;
	
	/**button to create a new game.*/
	private JButton createButton;
	
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
	
	/**Button to start a game.*/
	private JButton startButton;
	
	
	/**Frame which contains the GUI for the Game.*/
	GameFrame game;

	/**creates a dialog where the user can join, create and start games.
	 * @param s the socket used for the connection.
	 * @param lobbyParent the parent of the lobby.*/
	public GamesPanel(Clientsocket s, final JFrame lobbyParent) 
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
			public boolean isCellEditable(final int rowIndex, final int vColIndex) 
			{
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
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 27;
		this.add(startButton, c);
		
		this.setOpaque(false);
		
		
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				socket.sendData(Protocol.GAME_MAKE.toString());
				
			}
		});
		
	/*	startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				game = new GameFrame(lobbyParent);
				lobbyParent.setVisible(false);
				
			}
		});*/
		
		
		// LISTENERS
		gamesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
				String[] info = GamesManager.getInfo(Integer.valueOf(temp.get(0)));
				if (info != null)
				{
					gameSettings.setText(info.toString());
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
				if (0 <= gamesTable.getSelectedRow())
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

		socket.addLobbyEventListener(new LobbyEventListener()
		{
			@Override
			public void received(final LobbyEvent evt) throws Exception 
			{
				Log.DebugLog("GameList: " + evt.getSection() + " " + evt.getMsg());
				switch(evt.getSection())
				{
				case LOBBY_UPDATE:
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
		Log.DebugLog("GamesPanel: refresh game list.");
		gamesData = GamesManager.makeVector();
		Log.DebugLog("-->repaint, How many games here: " + gamesData.size());
		updateGameTable();
		
		
	}
	
	/**updates the gameTable.*/
	public final void updateGameTable()
	{
		Vector<String> columns = new Vector<String>();
		columns.add("ID");
		columns.add("Spieler");
		columns.add("Name");
		

		DefaultTableModel model = (DefaultTableModel) gamesTable.getModel();
		
		if (gamesData != null && 0 < gamesData.size())
		{
			Log.DebugLog("paint gamelist");
			model.setDataVector(gamesData, columns);
		}
		else
		{
			Log.DebugLog("empty gamelist");
			model.setDataVector(new Vector<Vector<String>>(), columns);
		}
		
		gamesTable.revalidate();
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
