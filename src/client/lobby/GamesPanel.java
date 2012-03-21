package client.lobby;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import shared.Log;
import shared.Protocol;

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
	private Vector<Vector> gamesData = new Vector();

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
		c.fill = GridBagConstraints.HORIZONTAL;
		joinButton.setEnabled(false);
		c.ipady = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.gridx = 1;
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
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 27;
		this.add(createButton, c);

		this.setOpaque(false);
		
		
		// LISTENERS
		joinButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(0<=gamesTable.getSelectedRow())
				{
					Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
					socket.sendData(Protocol.GAME_JOIN.toString()+" 2"+temp.get(0));
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
				Log.DebugLog("GAMELIST: " + evt.getSection());
				Log.DebugLog("GAMELIST: " + evt.getMsg());
				String section = evt.getSection();
				String message = evt.getMsg();
				switch(section)
				{
				case "GAME":
					Vector<String> temp = new Vector<String>();
					temp.add((String) message.subSequence(0, 2));
					temp.add((String) message.subSequence(3, 4));
					temp.add(message.substring(5));
					Log.DebugLog("Added game to list: " + temp.toString());
					addToGameTable(temp);
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
	/**adds a server to the list.
	 * Checks if there are duplicates and deletes them
	 * @param v the vector to be added
	 * */
	private void addToGameTable(final Vector<String> v)
	{
		Vector<String> temp = new Vector<String>();
		//for the duplicates
		for (Vector<String> t:gamesData)
		{
			if (t.get(0).equals(v.get(0)))
			{
				temp = t;
			}
		}
		gamesData.remove(temp);
		if ( ! v.get(1).equals("0")) //only add if more than 0 player ^
		{
			gamesData.add(v);
		}

		gamesTable.updateUI();
		gamesTable.repaint();
		if(0<gamesTable.getRowCount())
		{
			joinButton.setEnabled(true);
		}
		else
		{
			joinButton.setEnabled(false);
		}
	}
}
