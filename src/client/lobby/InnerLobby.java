package client.lobby;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import client.net.Clientsocket;
import javax.swing.JPanel;

import shared.Log;
import shared.ServerAddress;

import client.events.GameSelectedEvent;
import client.events.GameSelectedListener;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;
/**Class which displays the lobby of a Server.
 * Allow to chat with other users, start a game, join a game.
 * Will throw an Event if a Game is chosen to join.*/
public class InnerLobby extends JPanel {
	
	JFormattedTextField inputChat;
	/** textfield to write messages*/
	JButton sendButton;
	/**button to send a message.*/
	JButton joinButton;
	/**button to join a game.*/
	JButton createButton;
	/**button to create a new game.*/
	JScrollPane chatScroll;
	/**panel where chat entries are listed*/
	JScrollPane gamesScroll;
	/**panel where games are listed*/
	JScrollPane createScroll;
	/**panel where for creating a new game*/
	JLabel createSetting;
	/**label where creation settings are shown*/
	JLabel gameSettings;
	/**label where game options are shown*/
	
	/**List of listeners. */
	private javax.swing.event.EventListenerList listeners =  new javax.swing.event.EventListenerList();
	/**Socket / Connection to server*/
	Clientsocket socket;
	
	public InnerLobby(){
		
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
		gameSettings.setText("Infos zum ausgewŠhlten Spiel");		//Spieldaten einfŸgen
		gamesScroll.setPreferredSize(new Dimension(400, 80));
		gameSettings.setBackground(new Color(255, 255, 255));
		gameSettings.setOpaque(true);
		gameSettings.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.CENTER;
		c.gridwidth = 4;
		c.gridheight= 3;
		c.gridx = 0;
		c.gridy = 11;
		c.insets=new Insets(10,0,0,0);
		this.add(gameSettings, c);
		
		joinButton = new JButton("join");
		joinButton.setEnabled(true);
		//action Listener
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady=2;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 14;
		this.add(joinButton, c);
		
		
		joinButton= new JButton("start");
		joinButton.setEnabled(true);
		gamesScroll.setPreferredSize(new Dimension(20, 20));
		//action Listener
		c.fill=GridBagConstraints.HORIZONTAL;
		c.ipady=2;
		c.weightx=0.0;
		c.gridwidth=1;
		c.gridx=3;
		c.gridy=14;
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
		c.gridheight= 3;
		c.gridx = 0;
		c.gridy = 24;
		this.add(createSetting, c);
		
		createButton= new JButton("create");
		createButton.setEnabled(true);
		createScroll.setPreferredSize(new Dimension(20, 20));

		c.fill=GridBagConstraints.HORIZONTAL;
		c.ipady=2;
		c.weightx=0.0;
		c.gridwidth=1;
		c.gridx=2;
		c.gridy=27;
		this.add(createButton, c);
		
		
		
		chatScroll = new JScrollPane();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx=400;
		c.ipady = 460;
		c.weightx = 0.0;
		c.gridwidth = 20;
		c.gridheight=28;
		c.gridx = 6;
		c.gridy = 0;
		c.insets=new Insets(0,20,0,0);
		this.add(chatScroll, c);
		
		
		inputChat = new JFormattedTextField("Nachricht hier eingeben");
		//inputChat.setText(checkMessage("Nachricht hier eingeben"));
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = 1;
		c.ipadx=200;
		c.weightx = 0.0;
		c.gridwidth = 14;
		c.gridx = 6;
		c.gridy = 29;
		c.insets = new Insets(0, 20, 0, 0);
		this.add(inputChat, c);
		
		sendButton = new JButton("senden");
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener()
		{
			/**Actionlistener for the "join Server" button. 
			 * validates the Username and set it to default if null.
			 * then validates Server Selection.
			 * Establishes a connection to the selected server and then throw event.
			 * */
			public void actionPerformed(final ActionEvent arg0) {
				
				String message;
				try
				{
					message = checkMessage(inputChat.getText());
				}
				catch (NullPointerException e)
				{
					Log.DebugLog("-->no Message written, set to default");
					message = "dismissed";
				}
				/*
				 * 
				 * 
				 * 
				 * Chat zum server
				 * 
				 * 
				 * 
				 * 
				 * 
				 */
				
				
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		chatScroll.setPreferredSize(new Dimension(20, 20));
		c.ipady=2;
		c.ipadx=5;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 25;
		c.gridy = 29;
		this.add(sendButton, c);
		
		
		this.setOpaque(false);

	
	
	
	}
	
	
	public InnerLobby(Clientsocket s){
		this();
		this.socket = s;
	}
	
	/**sanitize the written chat.
	 * @param message to check
	 * @return sanitized message*/
	private String checkMessage(final String message)
	{		
		return message.replaceAll("[^A-Za-z0-9]", "");
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
