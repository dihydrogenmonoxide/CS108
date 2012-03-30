package client.lobby;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
	/**button to join or leave a game.*/
	private JButton toggleButton;
	/**holding the info for the UI, just a simplified version of games.*/
	private Vector<Vector<String>> gamesData = new Vector<Vector<String>>();
	/**table with all the games.*/
	private JTable gamesTable;
	/**scrollable list for the games.*/
	private JScrollPane gamesScroll;
	/**whether the Player has joined a game or not.*/
	private boolean isJoined = false;
	/**the number of the joined game.*/
	private int joinedGame = 0;
	/**game name about the joined game.*/
	private JList<String> infoName = new JList<String>();
	/**vector holding the infos.*/
	private Vector<String> infoNameV = new Vector<String>();
	/**player count of the joined game.*/
	private JList<String> infoCount = new JList<String>();
	/**vector holding the infos.*/
	private Vector<String> infoCountV = new Vector<String>();
	/**Player of the joined game.*/
	private JList<String> infoPlayers = new JList<String>();
	/**vector holding the infos.*/
	private Vector<String> infoPlayersV = new Vector<String>();



	/**panel where for creating a new game.*/
	private JScrollPane createScroll;
	/**button to create a new game.*/
	private JButton createButton;
	/**label where creation settings are shown.*/
	private JLabel createSetting;
	/**label where game options are shown.*/
	private JPanel gameInfo;
	/**Button to start a game.*/
	private JButton startButton;


	/**Frame which contains the GUI for the Game.*/
	GameFrame game;

	/**creates a dialog where the user can join, create and start games.
	 * @param s the socket used for the connection.
	 * @param lobbyParent the parent of the lobby.*/
	public GamesPanel(final Clientsocket s, final JFrame lobbyParent) 
	{
		this.socket = s;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		makeGameChooser(c);
		makeGameCreator(c);

		this.setOpaque(false);


		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				socket.sendData(Protocol.GAME_MAKE.toString());

			}
		});
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				game = new GameFrame(lobbyParent, s);
				lobbyParent.setVisible(false);

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
					if(isJoined)
					{
						refreshJoinedGame(joinedGame);
					}
					
					break;
				default:
					break;
				}
			}

			//TODO Listener which frees the GUI when one could not join a game.
			
			@Override
			public void received(final NetEvent evt) 
			{
			}
		});
	}
	/**This function creates an UI where the player can create new games.
	 * @param c the layoutmanager
	 */
	private void makeGameCreator(GridBagConstraints c) {
		createScroll = new JScrollPane();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		c.weightx = 3;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 17;
		this.add(createScroll, c);

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
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 27;
		this.add(createButton, c);

		startButton = new JButton("Spiel starten");
		startButton.setEnabled(true);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 28;
		this.add(startButton, c);
		
	}
	
	
	/**Creates the Panel in which a user can choose a game and join it.
	 * @param c
	 */
	private void makeGameChooser(GridBagConstraints c) {
		/*
		 * Create the Listing with all the Games
		 * */
		
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
		gamesScroll.setPreferredSize(new Dimension(300, 80));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 50;
		c.ipadx = 50;
		c.weightx = 0.0;
		c.gridwidth = 4;
		c.gridheight = 10;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gamesScroll, c);
		
		/*
		 * Create the Panel with all the Infos if the player joined the game.
		 * */

		gameInfo = new JPanel();
		gameInfo.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		gameInfo.setBackground(new Color(255, 255, 255));
		gameInfo.setOpaque(true);
		gameInfo.setForeground(new Color(50, 50, 50));
		gameInfo.setVisible(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 4;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 11;
		c.insets = new Insets(10, 0, 0, 0);
		this.add(gameInfo, c);
		
		
		JButton voteButton = new JButton();
		voteButton.setText("ich bin bereit");
		//TODO implement this button
		
		
		infoName.setListData(infoNameV);
		infoCount.setListData(infoCountV);
		infoPlayers.setListData(infoPlayersV);
		
		//hack to disable selection.
		infoName.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });
		infoCount.setCellRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });
		infoPlayers.setCellRenderer(new DefaultListCellRenderer() {

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });
		
		gameInfo.setPreferredSize(new Dimension(300, 65));
		gameInfo.setLayout(new BorderLayout(50, 0));
		gameInfo.add(BorderLayout.WEST, infoName);
		gameInfo.add(BorderLayout.CENTER, infoCount);
		gameInfo.add(BorderLayout.EAST, infoPlayers);
		gameInfo.add(BorderLayout.SOUTH, voteButton);
		
		gameInfo.validate();
		
		/*
		 * Create the toggle button
		 * */
		toggleButton = new JButton("beitreten");
		c.fill = GridBagConstraints.HORIZONTAL;
		toggleButton.setEnabled(false);
		c.ipady = 1;
		c.gridwidth = 4;
		
		c.gridx = 0;
		c.gridy = 14;
		this.add(toggleButton, c);

		// LISTENERS
		gamesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				if (0 < gamesTable.getSelectedRow())
				{
					Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
					String[] info = GamesManager.getInfo(Integer.valueOf(temp.get(0)));
					if (info != null)
					{
						//do nothing
						
					}
					else
					{
						//do nothing
					}
					//gameSettings.setText(temp.get(0) + " : " + temp.get(1) + " : " + temp.get(2));
				}
			}

		});

		toggleButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e) {


				if (!isJoined)
				{
					if (0 <= gamesTable.getSelectedRow())
					{
						//get the game which was joined
						Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
						joinedGame = Integer.valueOf(temp.get(0));
						
						refreshJoinedGame(joinedGame);
						
						//send the command
						socket.sendData(Protocol.GAME_JOIN.str() + joinedGame);

						//toggle
						gameInfo.setVisible(true);
						gamesScroll.setVisible(false);
						toggleButton.setText("verlassen");
						isJoined = true;
					}
				}
				else
				{
					socket.sendData(Protocol.GAME_QUIT.str());
					gameInfo.setVisible(false);
					gamesScroll.setVisible(true);	
					toggleButton.setText("beitreten");
					isJoined = false;
				}


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
			toggleButton.setEnabled(true);
		}
		else
		{
			toggleButton.setEnabled(false);
		}
	}
	/**
	 * @param gameId
	 */
	private void refreshJoinedGame(int gameId) {
		//set the infos
		infoNameV.clear();
		infoCountV.clear();
		infoPlayersV.clear();
		
		String[] infos = GamesManager.getInfo(gameId);
		infoNameV.add("Spiel:");
		infoNameV.add(infos[1]);
		infoCountV.add("Mitspieler:");
		infoCountV.add(infos[2]);
		
		gameInfo.setPreferredSize(new Dimension(300,15+(7*infos.length-3)));
		
		for (int i = 0; i < infos.length - 3; i++)
		{
			infoPlayersV.add(infos[3 + i]);
			Log.DebugLog("added"+infos[3+i]);
		}
		
		infoName.updateUI();
		infoCount.updateUI();
		infoPlayers.updateUI();
	}
}
