package server.UI;

import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import javax.swing.JTextArea;

import server.MainServer;
import server.players.Player;
import shared.Log;
import shared.Protocol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ServerUI {

	private JFrame frmSwissDefconServer;
	private JTextField txtServerconsole;
	private JTextArea txtrServeroutput;
	private JTextArea txtpnStdout;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	
	private PopupMenu popupMenu;
	private JList<Player> list;
	
	private DefaultListModel<Player> playerList = new DefaultListModel<Player>();
	private JLabel lblPlayers;


	/**
	 * Create the application.
	 */
	public ServerUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSwissDefconServer = new JFrame();
		frmSwissDefconServer.setTitle("SwissDefcon Server");
		frmSwissDefconServer.setBounds(100, 100, 889, 535);
		frmSwissDefconServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		txtServerconsole = new JTextField();
		txtServerconsole.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					MainServer.getPlayerManager().broadcastMessage_everyone(txtServerconsole.getText());
					
					printText("Send \'"+txtServerconsole.getText()+"\' to "+MainServer.getPlayerManager().getPlayers().size()+" Players");
					txtServerconsole.setText("");
				}
			}
		});
		txtServerconsole.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if("Enter any Commands here and they\'re broadcasted to everyone".compareTo(txtServerconsole.getText()) == 0)
						txtServerconsole.setText("");				
			}
			@Override
			public void focusLost(FocusEvent e) {
				if(txtServerconsole.getText().length() == 0)
				{
					txtServerconsole.setText("Enter any Commands here and they\'re broadcasted to everyone");
				}
			}
		});
		txtServerconsole.setText("Enter any Commands here and they\'re broadcasted to everyone");
		
		txtServerconsole.setColumns(1);
		
		txtpnStdout = new JTextArea();
		txtpnStdout.setForeground(Color.GREEN);
		txtpnStdout.setBackground(Color.BLACK);
		txtpnStdout.setText("STD::OUT:\n");
		
		txtrServeroutput = new JTextArea();
		txtrServeroutput.setForeground(Color.GREEN);
		txtrServeroutput.setBackground(Color.BLACK);
		txtrServeroutput.setText("Serveroutput:");
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(txtpnStdout);
		scrollPane.setPreferredSize(new Dimension(400, 450));
		scrollPane.setAutoscrolls(true);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setViewportView(txtrServeroutput);
		scrollPane_1.setPreferredSize(new Dimension(400, 450));
		scrollPane_1.setAutoscrolls(true);
		JPanel contentPane = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane_1, scrollPane);
		contentPane.setLayout(new BorderLayout());
		
				
		list = new JList<Player>(playerList);
		list.setForeground(Color.GREEN);
		list.setBackground(Color.BLACK);
		list.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane2 = new JScrollPane(list);
		scrollPane2.setPreferredSize(new Dimension(120, 450));
		contentPane.add(scrollPane2, BorderLayout.WEST);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		lblPlayers = new JLabel("Players:");
		lblPlayers.setForeground(Color.GREEN);
		lblPlayers.setBackground(new Color(0, 0, 0));
		lblPlayers.setOpaque(true);
		scrollPane2.setColumnHeaderView(lblPlayers);
		playerList.setSize(100);
		
		popupMenu = new PopupMenu();
		MenuItem menuItem = new MenuItem("Kick");
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Player p = playerList.get(list.getSelectedIndex());
				if(p != null)
				{
					p.sendData(Protocol.CON_EXIT.str()+"The Sereradmin kicked you");
					p.disconnect();
				}
				else
				{
					Log.WarningLog("Couldn't kick a Player: can't find the ID "+list.getSelectedIndex());
				}
				
				
			}
		});
	    popupMenu.add(menuItem);
		
		list.add(popupMenu);
		
		list.addMouseListener(new MouseAdapter() {
		     public void mouseClicked(MouseEvent me) {
		       // if right mouse button clicked (or me.isPopupTrigger())
		       if (SwingUtilities.isRightMouseButton(me)
		           && !list.isSelectionEmpty()
		           && list.locationToIndex(me.getPoint())
		              == list.getSelectedIndex()) {
		               popupMenu.show(list, me.getX(), me.getY());
		               }
		           }
		        }
		     );
		
		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane2, splitPane);
		splitPane1.setOneTouchExpandable(true);
		splitPane1.setContinuousLayout(true);
		contentPane.add(splitPane1, BorderLayout.CENTER);
		contentPane.add(txtServerconsole, BorderLayout.SOUTH);
		
		splitPane.setResizeWeight(0.5);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		frmSwissDefconServer.setContentPane(contentPane);
		
		OutputStream stdout = new OutputStream()
		{
			@Override
			public void write(final int b) throws IOException 
			{
				stdout(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException 
			{
				stdout(new String(b, off, len));
			}
			
			@Override
			public void write(byte[] b) throws IOException 
			{
				write(b, 0, b.length);
			}
		};
		
		System.setOut(new PrintStream(stdout, true));
		//TODO SERVER uncomment
	//	System.setErr(new PrintStream(stdout, true));
		this.frmSwissDefconServer.pack();
	}

	public void setVisible(boolean b)
	{
		this.frmSwissDefconServer.setVisible(b);		
	}
	
	public void printText(String s)
	{
		txtrServeroutput.append("\n"+s);
	}

	private void stdout(String s)
	{
		txtpnStdout.append(s);
	}
	
	/**
	 * Adds or updates the player in the list on the ServerUI
	 * @param p the player
	 */
	public synchronized void addPlayer(Player p)
	{
		playerList.add(p.getID()-101, p);
	}
	
	/**
	 * Removes the specified player
	 * @param p the player to remove
	 */
	public synchronized void removePlayer(Player p)
	{
		playerList.remove(p.getID()-101);
	}
}
