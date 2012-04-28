package client.game;


import client.data.RunningGame;
import client.game.field.GameFieldPanel;
import client.net.Clientsocket;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

public class InnerGameFrame extends JPanel {
	private GlassPane GlassPane;
	/**the Connection made to the Server.*/
	private Clientsocket socket;
	/**Panel for gameChat*/
	private GameChatPanel gameChat;
	
	private JToggleButton ready;
                
	GridBagConstraints c;
	
	JButton leave;
	
	
	public InnerGameFrame(final GameFrame gameFrame, Clientsocket s){
		this.socket = s;
		this.gameChat=gameChat;
                
        JFrame game = gameFrame.getFrame();
		
		
		this.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		
		GameFieldPanel gameField = new GameFieldPanel(socket, this, c);
                gameField.setOpaque(true);
                c.fill = GridBagConstraints.BOTH;
                gameField.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                c.anchor = GridBagConstraints.CENTER;
		c.weightx = 4.0;
                c.weighty = 4.0;
		c.gridwidth = 6;
		c.gridx = 0;
		c.gridy = 0;
		this.add(gameField, c);
                
                
                
                
        c.fill = GridBagConstraints.NONE;
                
		gameChat = new GameChatPanel(socket);
		c.ipady = 2;
		c.weightx = 0.0;
		c.gridwidth = 4;
		c.gridx = 6;
		c.gridy = 0;
		this.add(gameChat, c);
		
		GameButtonsPanel buttons = new GameButtonsPanel( socket, game);
		c.weighty = 0.0;
		c.weightx= 0.0;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth=7;
		c.gridx=0;
		c.gridy=3;
		this.add(buttons, c);
		
		ready= new JToggleButton("ready");
		ready.setSelected(false);
		c.gridwidth=1;
		c.gridx=1;
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
		c.gridx=0;
		c.gridy=2;
		this.add(leave,c);
		
		leave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                            gameFrame.closeGame();
			}
		});
		
		
		
		
		
		JTextArea money = new JTextArea();
		Font f = new Font( Font.SERIF, Font.PLAIN, 20 );
		money.setFont(f);
		money.setOpaque(false);
		money.setForeground(new Color(150,150,150));
		money.setText("Kontostand: "+ Long.toString(RunningGame.getMoney()));
		money.setEditable(false);
        money.setLineWrap(true);
        money.setWrapStyleWord(true);
		c.gridx=7;
		c.gridy=2;
		this.add(money,c);
		
		
		
		GlassPane = new GlassPane(game.getContentPane(), socket);
		game.setGlassPane(GlassPane);
		
		
		this.setOpaque(false);
	}
	

		
}

