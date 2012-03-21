package client.lobby;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.*;

import shared.Log;
import shared.ServerAddress;
import shared.User;
import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;
import client.net.DiscoveryClient;
/**
 * This creates a dialog to join a server.
 * @return the Server which to Connect to.
 * */
public class SelectServer extends JPanel {
	/**Scan period between the scans of the Discovery client.*/
	private static final int SCAN_PERIOD = 6000;
	/**Default server port for the "enter ip" dialog.*/
	private static final int DEFAULT_SERVER_PORT = 9003;
	/**serialid.*/
	private static final long serialVersionUID = 1L;
	/**List of listeners.  */
	private javax.swing.event.EventListenerList listeners =  new javax.swing.event.EventListenerList();
	/**timer for repeating the search.*/
	private Timer timer;
	/**List to hold all the found Server.*/
	private Vector<ServerAddress> foundServers;
	/**optionlist which displays all the servers. Content not specified because its an Vector or an Array (depends on servers)*/
	private JList listServers;
	/**button to join the server.*/
	private JButton buttonJoin;
	/**button to open a dialog to enter a custom ip.*/
	private JButton buttonIp;
	/**displays errors.*/
	private JLabel labelError;
	/**input for the desired username.*/
	private JFormattedTextField inputUsername;
	/**message to display if no server found.*/
	private String[] msgNoServers = {"suchen ...", "bitte haben Sie Geduld"};
	/** holds all User relevant infos.*/
	private User user;
	/**boolean if DiscoveryClient already started.*/
	private boolean isSearching = false;
	
	/**Displays the UI to select a server.
	 * Needs now arguments
	 * @param u the assigned User
	 * 
	 * */
	public SelectServer(User u)
	{
		this.user = u;

		Log.DebugLog("Choose a server");

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel labelDialog = new JLabel();
		labelDialog.setText("Wählen Sie ihren Server:");
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

		this.startSearch();

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

		listServers.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(final KeyEvent arg0) 
			{	
			}

			@Override
			public void keyReleased(final KeyEvent arg0) 
			{
			}

			@Override
			public void keyTyped(final KeyEvent arg0) 
			{
				if(arg0.getKeyCode( )== KeyEvent.VK_ENTER)
				{
					buttonJoin.doClick();
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
		labelUser.setText("Wählen Sie ihren Benutzernamen:");
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
		if(0<user.getUserName().length())
		{
			inputUsername.setText(InputValidator.UserName(user.getUserName()));
		}
		else
		{
			inputUsername.setText(InputValidator.UserName(System.getProperty("user.name")));
		}
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
					sUsername = InputValidator.UserName(inputUsername.getText());
				}
				catch (NullPointerException e)
				{
					Log.DebugLog("-->no Username given, set to default");
					sUsername = "fox1337";
				}

				try
				{
					Log.DebugLog("-->choosen " + foundServers.elementAt(listServers.getSelectedIndex()) + " as Server");
					ServerAddress a = foundServers.elementAt(listServers.getSelectedIndex());
					//connect to server
					serverSelected(new ServerSelectedEvent("Server selected", a, sUsername));
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					Log.DebugLog("--Tried to choose an invalid/inactive Server");
					if (listServers.getSelectedIndex() < 0)
					{
						labelError.setText("kein Server ausgewählt");
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

		buttonIp = new JButton("IP eingeben ...");
		buttonIp.setEnabled(true);
		buttonIp.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent arg0) 
			{
				Log.DebugLog("User wants to enter its own IP, take care...");
				String[] stringIP=JOptionPane.showInputDialog("Gebe eine Serveraddresse an: \n IP:PORT (z.Bsp. 192.168.1.1:9003)\n (Standardport "+DEFAULT_SERVER_PORT+")").split(":");
				try {
					if (InputValidator.isIP(stringIP[0]) == false)
					{
						JOptionPane.showMessageDialog(buttonIp, "Warum konnte der inhaftierte Programmierer nicht aus dem Gefängnis ausbrechen? \n \n \n 404 Feil not found \n \n (not an ip)", "ID 10 T - Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					InetAddress addressIP = InetAddress.getByName(stringIP[0]);
					int port = DEFAULT_SERVER_PORT;
					if (stringIP.length >= 2)
					{
						Integer.valueOf(stringIP[1]);
					}
					ServerAddress addressServer = new ServerAddress(addressIP, port, NetworkInterface.getByInetAddress(addressIP));
					serverSelected(new ServerSelectedEvent("Server selected", addressServer , InputValidator.UserName(inputUsername.getText())));
				} catch (UnknownHostException|NullPointerException | SocketException  e) {
					Log.DebugLog("PEBKAC -> user to stupid to enter ip --> abort");
					Log.ErrorLog("User not worthy of this game");
				}
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 5;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 7;
		this.add(buttonIp, c);
		
		
		this.setOpaque(false);
	}

	/**
	 *This method sets a Timer, so we will scan every 6sec for new servers.
	 *Servers found are copied in vs_Servers and displayed then
	 *in the SelectList
	 */
	public void startSearch()
	{
		if(isSearching){return;}
		
		isSearching = true;
		
		timer = new Timer();
		int scanDelay = 1000;   

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
		}, scanDelay, SCAN_PERIOD);
	}

	/**
	 * Stops the timer, so it will not search for servers.
	 * */
	public final void stopSearch() 
	{
		timer.cancel();
		timer.purge();
		isSearching = false;
	}

	/** 
	 * adds serverSelected listeners.
	 * @param listener
	 */
	public void addServerSelectedListener(ServerSelectedListener listener) {
		listenerList.add(ServerSelectedListener.class, listener);
	}

	/**
	 * removes serverSelected listeners.
	 * @param listener
	 */
	public void removeServerSelectedListener(ServerSelectedListener listener) {
		listenerList.remove(ServerSelectedListener.class, listener);
	}

	/**
	 * Fires the ServerSelectedEvent to all the Listeners
	 * @param evt
	 */
	void serverSelected(ServerSelectedEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i=0; i<listeners.length; i+=2) {
			if (listeners[i]==ServerSelectedListener.class) {
				ServerSelectedListener listener = (ServerSelectedListener)listeners[i+1];
				listener.serverSelected(evt);
			}
		}
	}
}
