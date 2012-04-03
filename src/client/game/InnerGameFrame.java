package client.game;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

import client.lobby.ChatPanel;
import client.lobby.GamesPanel;
import client.net.Clientsocket;

public class InnerGameFrame extends JPanel {
	static private GlassPane GlassPane;

	private Clientsocket socket;
	JPanel glass = new JPanel(new GridLayout());
	JLabel padding = new JLabel();
	JProgressBar waiter = new JProgressBar();
	GameChatPanel gameChat;
	
	
	public InnerGameFrame(JFrame gameFrame, Clientsocket s){
		this.socket = s;
		
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
	
		GameFieldPanel gameField = new GameFieldPanel();
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gameField, c);
		
		gameChat = new GameChatPanel(socket);
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 0;
		this.add(gameChat, c);
		
		GameButtonsPanel buttons = new GameButtonsPanel( socket);
		c.ipady=2;
		c.ipadx=1;
		c.weightx= 0.0;
		c.gridwidth=3;
		c.gridx=0;
		c.gridy=2;
		this.add(buttons, c);
		
		JToggleButton ready= new JToggleButton("ready");
		ready.setSelected(false);
		c.gridx=3;
		c.gridy=2;
		this.add(ready, c);
		
		ready.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlassPane.setVisible(e.getStateChange() 
						== ItemEvent.SELECTED);
			}
		});
		
		GlassPane = new GlassPane(ready, gameChat,
		gameFrame.getContentPane());
		gameFrame.setGlassPane(GlassPane);
	
		
		this.setOpaque(false);
	}
	
	
	
}

