package client.game;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

import shared.Protocol;

import client.data.RunningGame;
import client.lobby.ChatPanel;
import client.lobby.GamesPanel;
import client.net.Clientsocket;

public class InnerGameFrame extends JPanel {
	private GlassPane GlassPane;
	/**the Connection made to the Server.*/
	private Clientsocket socket;
	/**Panel for gameChat*/
	private GameChatPanel gameChat;
	
	private JToggleButton ready;
	
	JButton leave;
	
	public InnerGameFrame(final JFrame gameFrame, Clientsocket s, int screenX, int screenY, final JFrame lobbyParent){
		this.socket = s;
		this.gameChat=gameChat;
		
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int MAP_HEIGHT=0;
		int MAP_WIDTH = 0;
		if(screenY*1.75<=screenX){
        	MAP_HEIGHT= screenY-150;
		}
		else{
			MAP_WIDTH=screenX-360;
		}
		
		
		GameFieldPanel gameField = new GameFieldPanel(socket, MAP_WIDTH, MAP_HEIGHT);
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
		
		GameButtonsPanel buttons = new GameButtonsPanel( socket, gameFrame);
		c.weightx= 0.0;
		c.gridwidth=3;
		c.gridx=0;
		c.gridy=2;
		this.add(buttons, c);
		
		ready= new JToggleButton("ready");
		ready.setSelected(false);
		c.gridwidth=1;
		c.gridx=3;
		c.gridy=2;
		c.insets= new Insets(0,50,0,0);
		this.add(ready, c);
		
		ready.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				GlassPane.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				GameChatPanel glassChat = new GameChatPanel(socket);
				GlassPane.add(glassChat);
			}
		});
		

		leave = new JButton("Beenden");
		c.gridx=3;
		c.gridy=2;
		c.insets = new Insets(100, 0, 0, 0);
		this.add(leave,c);
		
		leave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int eingabe = JOptionPane.showConfirmDialog(null,
                        "Willst du das Spiel wirklich beenden?", null,JOptionPane.YES_NO_OPTION);
				if(eingabe==0){
					/**WindowClosingEvent to return to InnerLobby*/
					gameFrame.dispose();
					RunningGame.hardReset();
					socket.sendData(Protocol.GAME_QUIT.str());
					lobbyParent.setVisible(true);
	
				}
			}
		});
		
		
		
		GlassPane = new GlassPane(gameFrame.getContentPane()
				, socket);
		gameFrame.setGlassPane(GlassPane);
		
		
		
		this.setOpaque(false);
	}
	
	
	
}

