package server;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

import server.players.Player;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JScrollPane;

public class ServerUI {

	private JFrame frmSwissDefconServer;
	private JTextField txtServerconsole;
	private JTextArea txtrServeroutput;
	private JTextArea txtpnStdout;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;


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
		frmSwissDefconServer.setTitle("Swiss Defcon Server");
		frmSwissDefconServer.setBounds(100, 100, 950, 400);
		frmSwissDefconServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		txtServerconsole = new JTextField();
		txtServerconsole.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					for(Player p : MainServer.getPlayerManager().getPlayers())
					{
						p.sendMessage(txtServerconsole.getText());
					}
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
		frmSwissDefconServer.getContentPane().add(txtServerconsole, BorderLayout.SOUTH);
		txtServerconsole.setColumns(10);
		
		txtpnStdout = new JTextArea();
		txtpnStdout.setForeground(Color.GREEN);
		txtpnStdout.setBackground(Color.BLACK);
		txtpnStdout.setText("STD::OUT:\n");
		
		txtrServeroutput = new JTextArea();
		txtrServeroutput.setForeground(Color.GREEN);
		txtrServeroutput.setBackground(Color.BLACK);
		txtrServeroutput.setText("Serveroutput:");
		
		scrollPane = new JScrollPane();
		frmSwissDefconServer.getContentPane().add(scrollPane, BorderLayout.EAST);
		scrollPane.setViewportView(txtpnStdout);
		scrollPane.setAutoscrolls(true);
		scrollPane.setPreferredSize(new Dimension(550, 450));
		
		scrollPane_1 = new JScrollPane();
		frmSwissDefconServer.getContentPane().add(scrollPane_1, BorderLayout.CENTER);
		scrollPane_1.setViewportView(txtrServeroutput);
		scrollPane_1.setAutoscrolls(true);
		scrollPane_1.setPreferredSize(new Dimension(400, 450));
		
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
		System.setErr(new PrintStream(stdout, true));
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
}
