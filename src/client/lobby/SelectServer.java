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
 * 
 * @param none
 * @return the Server which to Connect to.
 * */
public class SelectServer extends JPanel{

	private static final long serialVersionUID = 1L;
	private Vector<ServerAddress> vs_Servers;	//holds the found servers
	private JList jl_Dialog;
	private JButton bt_Join;
	private JLabel lbl_Error;
	private JFormattedTextField jft_Username;
	private String[] sa_NoServers={"suchen ...","bitte haben Sie Geduld"}; //printed when no servers active

	public SelectServer()
	{
		Log.DebugLog("Choose a server");

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel lbl_Dialog = new JLabel();
		lbl_Dialog.setText("Wählen Sie ihren Server:");
		lbl_Dialog.setBackground(new Color (255,255,255));
		lbl_Dialog.setOpaque(true);
		lbl_Dialog.setForeground(new Color (50,50,50));
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		this.add(lbl_Dialog, c);

		jl_Dialog = new JList(new DefaultListModel());
		jl_Dialog.setVisibleRowCount(5);
		jl_Dialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jl_Dialog.setLayoutOrientation(JList.VERTICAL);

		vs_Servers = new Vector<ServerAddress>();

		jl_Dialog.setListData(sa_NoServers);
		jl_Dialog.setEnabled(false); //because no server found yet

		/*
		 *Timer, so we will scan every 6sec for new servers.
		 *Servers found are copied in vs_Servers and displayed in the SelectList
		 * */
		Timer timer = new Timer();
		int i_Delay = 1000;   
		int i_Period = 6000;

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
				int i_temp= jl_Dialog.getSelectedIndex();
				vs_Servers.clear();
				for(ServerAddress a : s.GetList())
				{
					vs_Servers.add(a);
				}

				if(vs_Servers.isEmpty()) //no server active
				{
					jl_Dialog.clearSelection();
					jl_Dialog.setEnabled(false);
					jl_Dialog.setListData(sa_NoServers);
					bt_Join.setEnabled(false);
				}else
				{	
					jl_Dialog.setListData(vs_Servers);
					jl_Dialog.setSelectedIndex(i_temp);
					jl_Dialog.setEnabled(true);
				}
				jl_Dialog.repaint();
				t.interrupt();
			}
		}, i_Delay, i_Period);

		jl_Dialog.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				try{
					Log.DebugLog("Choosen "+vs_Servers.elementAt(evt.getFirstIndex())+" as Server");
					bt_Join.setEnabled(true);
				}catch(ArrayIndexOutOfBoundsException e){
					Log.DebugLog("Tried to choose an invalid/inactive Server");

				}
			}
		});

		JScrollPane jsp_ServerPane = new JScrollPane(jl_Dialog);
		jsp_ServerPane.setPreferredSize(new Dimension(250, 80));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		c.insets=new Insets(0,0,10,0);
		this.add(jsp_ServerPane, c);


		JLabel lbl_User = new JLabel();
		lbl_User.setText("Wählen Sie ihren Benutzernamen:");
		lbl_User.setBackground(new Color (255,255,255));
		lbl_User.setOpaque(true);
		lbl_User.setForeground(new Color (50,50,50));
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = -1;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		c.insets=new Insets(10,5,2,5);
		this.add(lbl_User, c);

		jft_Username = new JFormattedTextField();
		jft_Username.setColumns(10);
		jft_Username.setText(checkUsername(System.getProperty("user.name")));
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = -1;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		c.insets=new Insets(0,0,10,5);
		this.add(jft_Username, c);


		bt_Join = new JButton("Server beitreten");
		bt_Join.setEnabled(false);
		bt_Join.addActionListener(new ActionListener(){
			/**Actionlistener for the "join Server" button. 
			 * validates the Username and set it to default if null.
			 * then validates Server Selection.
			 * Establishes a connection to the selected server and then throw event.
			 * */
			public void actionPerformed(ActionEvent arg0) {
				Log.DebugLog("Trying to join Server");

				String s_Username;
				try
				{
					s_Username = checkUsername(jft_Username.getText());
				}
				catch(NullPointerException e)
				{
					Log.DebugLog("-->no Username given, set to default");
					s_Username = "fox1337";
				}

				try
				{
					Log.DebugLog("-->choosen "+vs_Servers.elementAt(jl_Dialog.getSelectedIndex())+" as Server");
					
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
						Log.InformationLog("-->Connected to **** as "+s_Username);
					}
					catch(Exception e)
					{
						Log.DebugLog("-->Could not join Server");
						lbl_Error.setText("konnte nicht mit Server verbinden");
						lbl_Error.setVisible(true);
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					Log.DebugLog("--Tried to choose an invalid/inactive Server");
					if(jl_Dialog.getSelectedIndex()<0)
					{
						lbl_Error.setText("kein Server ausgewählt");
						lbl_Error.setVisible(true);
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
		this.add(bt_Join, c);

		lbl_Error = new JLabel();
		lbl_Error.setText("");
		lbl_Error.setBackground(new Color (255,50,50));
		lbl_Error.setOpaque(true);
		lbl_Error.setForeground(new Color (255,255,255));
		lbl_Error.setVisible(false);
		c.fill = GridBagConstraints.LINE_END;
		c.ipady = 5;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 6;
		c.insets=new Insets(10,5,2,5);
		this.add(lbl_Error, c);

		this.setOpaque(false);

	}

	/**sanitize the given username*/
	public String checkUsername(String s_Username)
	{		
		return s_Username.replaceAll("[^A-Za-z0-9]", "");
	}
}
