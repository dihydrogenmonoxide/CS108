package client.lobby;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import client.net.Clientsocket;


public class GamesPanel extends JPanel {
	
	/**the connection socket*/
	private Clientsocket socket;
	/**button to join a game.*/
	private JButton joinButton;

	/**button to create a new game.*/
	private JButton createButton;



	/**panel where games are listed.*/
	private JScrollPane gamesScroll;

	/**panel where for creating a new game.*/
	private JScrollPane createScroll;

	/**label where creation settings are shown.*/
	private JLabel createSetting;

	/**label where game options are shown.*/
	private JLabel gameSettings;

	public GamesPanel(Clientsocket s) 
	{
		this.socket = s;
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		gamesScroll = new JScrollPane();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		c.ipadx=50;
		c.weightx = 0.0;
		c.gridwidth = 4;
		c.gridheight=10;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gamesScroll, c);

		gameSettings = new JLabel();
		gameSettings.setText("Infos zum ausgew�hlten Spiel");		//Spieldaten einf�gen
		gamesScroll.setPreferredSize(new Dimension(400, 80));
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

		joinButton = new JButton("join");
		joinButton.setEnabled(true);
		//action Listener
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 14;
		this.add(joinButton, c);


		joinButton = new JButton("start");
		joinButton.setEnabled(true);
		gamesScroll.setPreferredSize(new Dimension(20, 20));
		//action Listener
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 3;
		c.gridy = 14;
		this.add(joinButton, c);


		createScroll = new JScrollPane();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 80;
		c.weightx = 3;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 17;
		this.add(createScroll, c);



		createSetting = new JLabel();
		createSetting.setText("daten zum erstellenden Spiel");		//Spieldaten kreieren
		createSetting.setBackground(new Color(255, 255, 255));
		createSetting.setOpaque(true);
		createSetting.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.CENTER;
		c.gridwidth = 4;
		c.gridheight = 3;
		c.gridx = 0;
		c.gridy = 24;
		this.add(createSetting, c);

		createButton = new JButton("create");
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
	}
}
