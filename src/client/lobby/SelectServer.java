package client.lobby;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.Log;
import shared.ServerAddress;
import client.discovery.DiscoveryClient;

public class SelectServer extends JPanel{
	private static final long serialVersionUID = 1L;
	private Vector<ServerAddress> vs_Servers;
	private JList jl_Dialog;
	private JButton bt_Join;
	String[] sa_NoServers={"suchen ...","bitte haben Sie Geduld"};

	public SelectServer()
	{
		Log.DebugLog("Choose a server");


		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel label = new JLabel();
		label.setText("W�hlen Sie ihren Server:");
		c.gridx = 0;
		c.gridy = 0;
		this.add(label, c);
		
		jl_Dialog = new JList(new DefaultListModel());
		jl_Dialog.setVisibleRowCount(5);
		jl_Dialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jl_Dialog.setLayoutOrientation(JList.VERTICAL);
		
		vs_Servers = new Vector<ServerAddress>();
		
		jl_Dialog.setListData(sa_NoServers);
		jl_Dialog.setEnabled(false); //because no server found yet

		//suche regelm�ssig nach servern
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
					jl_Dialog.setEnabled(true);
				}
				jl_Dialog.repaint();
				t.interrupt();
			}
		}, i_Delay, i_Period);
		
		jl_Dialog.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent evt) {
		          Log.DebugLog("Choosen "+vs_Servers.elementAt(evt.getFirstIndex())+" as Server");
		          bt_Join.setEnabled(true);
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
		
		
		
		this.add(jsp_ServerPane, c);

		bt_Join = new JButton("Server beitreten");
		bt_Join.setEnabled(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;      //make this component tall
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 3;
		this.add(bt_Join, c);
	}
}
