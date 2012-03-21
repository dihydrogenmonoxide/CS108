package client.lobby;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import shared.Log;
import shared.Protocol;
import client.events.ChatEvent;
import client.events.ChatEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;

/**This class provides a Chatpanel as JPanel, everything is included, only a socket is needed.*/
public class ChatPanel extends JPanel {
	/**the Connection to listen to*/
	private Clientsocket socket;
	/**textfield to write messages*/
	private JFormattedTextField inputChat;

	/**button to send a message.*/
	private JButton sendButton;

	/**panel where chat entries are listed.*/
	private JScrollPane chatScroll;

	/** holds the StyledDocuments chatContent with the chat.*/
	JTextPane chatPane;

	/** holds all the Chat-messages.*/
	private StyledDocument chatContent;

	/**This Class provides a Pnael with a chat. 
	 * It requires a socket to add its event-listeners
	 * @param s the Clientsocket on which the messages are received*/
	public ChatPanel(final Clientsocket s) 
	{
		this.socket = s;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		/*
		 * 
		 *  MAKE THE GUI
		 * 
		 * /
		  
		/* The styled Document which holds the actual chat messages*/
		chatContent = new DefaultStyledDocument();

		chatPane = new JTextPane();
		chatPane.setEditable(false);
		chatPane.setFocusable(false);
		chatPane.setDocument(chatContent);


		chatScroll = new JScrollPane(chatPane);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 400;
		c.ipady = 460;
		c.weightx = 0.0;
		c.gridwidth = 20;
		c.gridheight = 28;
		c.gridx = 6;
		c.gridy = 0;
		c.insets = new Insets(0, 20, 0, 0);
		this.add(chatScroll, c);


		inputChat = new JFormattedTextField("Nachricht hier eingeben");
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = 1;
		c.ipadx = 200;
		c.weightx = 0.0;
		c.gridwidth = 14;
		c.gridx = 6;
		c.gridy = 29;
		c.insets = new Insets(0, 20, 0, 0);
		this.add(inputChat, c);
		


		sendButton = new JButton("senden");
		sendButton.setEnabled(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		chatScroll.setPreferredSize(new Dimension(20, 20));
		c.ipady = 2;
		c.ipadx = 5;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 25;
		c.gridy = 29;
		this.add(sendButton, c);
		
		this.setOpaque(false);
		
		
		
		/*
		 * 
		 *  ADD EVENT LISTENERS
		 * 
		 */

		// Listener for new Chatmessages
		socket.addChatEventListener(new ChatEventListener()
		{
			@Override
			public void received(final NetEvent evt) 
			{
				try
				{
					chatContent.insertString(chatContent.getLength(), "Received following Message from Server=" + evt.getMsgId(), chatContent.getStyle("HTMLDocument"));
				}
				catch (BadLocationException e)
				{
					Log.ErrorLog("Chat receiving Error");
				}

			}
			@Override
			public void received(final ChatEvent evt) 
			{
				try
				{
					chatContent.insertString(chatContent.getLength(), evt.getMsgNsp(), evt.getAttrs());
					chatPane.select(chatContent.getLength(), chatContent.getLength());
				} 
				catch (BadLocationException e)
				{
					Log.ErrorLog("Chat receiving Error");
				}
			}

		});
		
		//Listener for the "senden" button
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent arg0) {
				String message;
				try
				{
					message = InputValidator.ChatMessage(inputChat.getText());
					if (message.subSequence(0, 1).equals("/"))
					{
						if (message.substring(0, 4).equalsIgnoreCase(Protocol.CHAT_PREF_PRIVATE.toString()))
						{
								Log.InformationLog("Chat sending private message: " + message);
								socket.sendChatMessage(message);
						}
						else
						{
						Log.InformationLog("Chat sending command: " + message.substring(1));
						socket.sendData(message.substring(1));	
						}
					}
					else
					{
					Log.InformationLog("Chat Message sending: " + message);
					socket.sendChatMessage(message);
					}
					inputChat.setText("");
					sendButton.setEnabled(false);
				}
				catch (NullPointerException e)
				{
					Log.DebugLog("-->no Message written, set to default");
				}
			}
		});
		
		//Listener to clear the Chat-input
		inputChat.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(final FocusEvent arg0) 
			{
				inputChat.setText("");
			}

			@Override
			public void focusLost(final FocusEvent arg0) 
			{
			}
		});
		
		
		/* Key Input Listeners, will activate the "Send Button" if msg-length>3 and send the message with enter*/
		inputChat.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(final KeyEvent arg0) 
			{
			}

			@Override
			public void keyReleased(final KeyEvent arg0) 
			{
				boolean chatMsgValid = 1 <= inputChat.getText().length();
				sendButton.setEnabled(chatMsgValid);
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && chatMsgValid)
				{
					sendButton.doClick();
				}
			}

			@Override
			public void keyTyped(final KeyEvent arg0) 
			{
			}

		});

		
	}
}
