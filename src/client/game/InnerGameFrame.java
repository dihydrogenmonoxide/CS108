package client.game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import client.lobby.ChatPanel;
import client.lobby.GamesPanel;
import client.net.Clientsocket;

public class InnerGameFrame extends JPanel {
	
	private Clientsocket socket;

	public InnerGameFrame(Clientsocket s){
		this.socket = s;
		
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

	
		GameFieldFrame gameField = new GameFieldFrame();
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gameField, c);
		
		GameChatPanel gameChat = new GameChatPanel(s);
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		this.add(gameChat, c);
		
		GameButtonsPanel buttons = new GameButtonsPanel();
		c.ipady=2;
		c.ipadx=1;
		c.weightx= 0.0;
		c.gridwidth=2;
		c.gridx=0;
		c.gridy=2;
		this.add(buttons, c);
		
		this.setOpaque(false);

	}
}