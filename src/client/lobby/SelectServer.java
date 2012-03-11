package client.lobby;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.*;

import shared.Log;
import shared.ServerAddress;
import client.net.DiscoveryClient;
/**
 * This creates a dialog to join a server.
 * @return the Server which to Connect to.
 * */
public class SelectServer extends JPanel {
	/**serialid.*/
	private static final long serialVersionUID = 1L;
	/**List to hold all the found Server.*/
	private Vector<ServerAddress> foundServers;
	/**optionlist which displays all the servers.*/
	private JList listServers;
	/**button to join the server.*/
	private JButton buttonJoin;
	/**displays errors.*/
	private JLabel labelError;
	/**input for the desired username.*/
	private JFormattedTextField inputUsername;
	/**message to display if no server found.*/
	private String[] msgNoServers = {"suchen ...", "bitte haben Sie Geduld"};

	/**Displays the UI to select a server.
	 * Needs now arguments
	 * 
	 * */
	public SelectServer()
	{
		Log.DebugLog("Choose a server");

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel labelDialog = new JLabel();
		labelDialog.setText("WŠhlen Sie ihren Server:");
		labelDialog.setBackground(new Color(255, 255, 255));
		labelDialog.setOpaque(true);
		labelDialog.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		this.add(labelDialog, c);

		listServers = new JList(new DefaultListModel());
		listServers.setVisibleRowCount(5);
		listServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listServers.setLayoutOrientation(JList.VERTICAL);

		foundServers = new Vector<ServerAddress>();

		listServers.setListData(msgNoServers);
		listServers.setEnabled(false); //because no server found yet

		/*
		 *Timer, so we will scan every 6sec for new servers.
		 *Servers found are copied in vs_Servers and displayed 
		 *in the SelectList
		 * */
		Timer timer = new Timer();
		int scanDelay = 1000;   
		int scanPeriod = 6000;

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				DiscoveryClient s = new DiscoveryClient();
				Thread t = new Thread(s);
				t.start();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// not important
					e.printStackTrace();
				}
				int selected = listServers.getSelectedIndex();
				foundServers.clear();
				for (ServerAddress a : s.GetList())
				{
					foundServers.add(a);
				}

				if (foundServers.isEmpty()) //no server active
				{
					listServers.clearSelection();
					listServers.setEnabled(false);
					listServers.setListData(msgNoServers);
					buttonJoin.setEnabled(false);
				} else
				{	
					listServers.setListData(foundServers);
					listServers.setSelectedIndex(selected);
					listServers.setEnabled(true);
				}
				listServers.repaint();
				t.interrupt();
			}
		}, scanDelay, scanPeriod);

		listServers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent evt) {
				try
				{
					Log.DebugLog("Choosen " + foundServers.elementAt(evt.getFirstIndex()) + " as Server");
					buttonJoin.setEnabled(true);
				} catch (ArrayIndexOutOfBoundsException e)
				{
					Log.DebugLog("Tried to choose an invalid/inactive Server");

				}
			}
		});

		JScrollPane serverScroll = new JScrollPane(listServers);
		serverScroll.setPreferredSize(new Dimension(250, 80));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 10, 0);
		this.add(serverScroll, c);


		JLabel labelUser = new JLabel();
		labelUser.setText("WŠhlen Sie ihren Benutzernamen:");
		labelUser.setBackground(new Color(255, 255, 255));
		labelUser.setOpaque(true);
		labelUser.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = -1;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 5, 2, 5);
		this.add(labelUser, c);

		inputUsername = new JFormattedTextField();
		inputUsername.setColumns(10);
		inputUsername.setText(checkUsername(System.getProperty("user.name")));
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = -1;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 10, 5);
		this.add(inputUsername, c);


		buttonJoin = new JButton("Server beitreten");
		buttonJoin.setEnabled(false);
		buttonJoin.addActionListener(new ActionListener()
		{
			/**Actionlistener for the "join Server" button. 
			 * validates the Username and set it to default if null.
			 * then validates Server Selection.
			 * Establishes a connection to the selected server and then throw event.
			 * */
			public void actionPerformed(final ActionEvent arg0) {
				Log.DebugLog("Trying to join Server");

				String sUsername;
				try
				{
					sUsername = checkUsername(inputUsername.getText());
				}
				catch (NullPointerException e)
				{
					Log.DebugLog("-->no Username given, set to default");
					sUsername = "fox1337";
				}

				try
				{
					Log.DebugLog("-->choosen " + foundServers.elementAt(listServers.getSelectedIndex()) + " as Server");

					//connect to server
					try
					{
						/*
						 * 
						 * 
						 *   DO STUFF HERE, idee: set flag and  throw event to ClientLobby
						 * 
						 * 
						 * 
						 * */	
						Log.InformationLog("-->Connected to **** as " + sUsername);
					}
					catch (Exception e)
					{
						Log.DebugLog("-->Could not join Server");
						labelError.setText("konnte nicht mit Server verbinden");
						labelError.setVisible(true);
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					Log.DebugLog("--Tried to choose an invalid/inactive Server");
					if (listServers.getSelectedIndex() < 0)
					{
						labelError.setText("kein Server ausgewÃ¤hlt");
						labelError.setVisible(true);
					}
				}
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 20;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 5;
		this.add(buttonJoin, c);

		labelError = new JLabel();
		labelError.setText("");
		labelError.setBackground(new Color(255, 50, 50));
		labelError.setOpaque(true);
		labelError.setForeground(new Color(255, 255, 255));
		labelError.setVisible(false);
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = 5;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(10, 5, 2, 5);
		this.add(labelError, c);

		this.setOpaque(false);

	}

	/**sanitize the given username.
	 * @param sUsername to check
	 * @return sanitized username*/
	public final String checkUsername(final String sUsername)
	{		
		return sUsername.replaceAll("[^A-Za-z0-9]", "");
	}
}
