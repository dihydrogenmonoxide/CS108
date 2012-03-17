package client.lobby;


import javax.imageio.ImageIO;
import javax.swing.*;

import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;
import client.net.Clientsocket;

import shared.Log;
import shared.ServerAddress;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


/** this is the lobby at the start.
 * it allows joining a server, chatting with users, joining a game.
 * @ param none
 */
public class ClientLobby extends JFrame {
	/**actual width of the screen.*/
	private int screenX;
	/**actual height of the screen.*/
	private int screenY;

	/**JFrame which contains the GUI for the Lobby.*/
	private JFrame lobbyParent;
	/**width of the lobby in pixel.*/
	private int iLobbyX = 900; 
	/**height of the lobby in pixel.*/
	private int iLobbyY = 575;

	private PopupFactory factory=PopupFactory.getSharedInstance();
	private SelectServer s;
	private Clientsocket socket;
	private InnerLobby l;
	
	/**creates the lobby.*/
	public ClientLobby()
	{
		/*
		 * Get Infos about the screen
		 * */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();
		DisplayMode disp = screen.getDisplayMode();
		screenX = disp.getWidth();
		screenY = disp.getHeight();



		/*
		 * Set up background
		 * The bg-image is drawn in a JPanel which is laid under all the other panes with User-IO
		 * */
		JPanel bg = new JPanel()
		{
			private static final long serialVersionUID = 1L;
			
			/**paint the swiss map in the background*/
			public void paintComponent(Graphics g)
			{
				BufferedImage img = null;
				try 
				{
					img = ImageIO.read(new File("lobby_bg.jpg"));
					g.drawImage(img, 0, 0, iLobbyX, iLobbyY, 0, 0, img.getWidth(), img.getHeight(), new Color(0, 0, 0), null);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};
		
		/*
		 * Set up lobby / inputs
		 * */
		lobbyParent = new JFrame("SwissDefcon Lobby");
		lobbyParent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lobbyParent.setSize(iLobbyX, iLobbyY);
		lobbyParent.setResizable(false);
		lobbyParent.setLocation(screenX / 2 - iLobbyX / 2, screenY / 2 - iLobbyX / 2);
		lobbyParent.setContentPane(bg);
		
		s = new SelectServer();
		s.addServerSelectedListener(new ServerSelectedListener()
		{
			public void serverSelected(final ServerSelectedEvent ev)
			{
				ServerAddress server = ev.getServer();
				String desiredNick = ev.getUsername();
				try
				{
					Log.InformationLog("-->Connecting to " + ev.getServer().getServerName() + "(" + server.getAddress().getHostAddress() + ") as " + ev.getUsername() + "(desired Username)");
					
					//make Connection
					socket = new Clientsocket(server);
					//JOptionPane.showMessageDialog(lobbyParent, "Verbunden mit Server");
					

					s.stopSearch();
					s.setVisible(false);
					
					l = new InnerLobby(socket);
					lobbyParent.add(l);
					
					/*add game listener here*/
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.WarningLog("-->Could not connect to " + server.getServerName() + "(" + server.getAddress().getHostAddress()+ ") as " + desiredNick);
					JOptionPane.showMessageDialog(lobbyParent, "Konnte nicht mit Server verbinden", "Connection Error", JOptionPane.ERROR_MESSAGE);
					s.setVisible(true);
					s.startSearch();
					
				}
			}
		});
		lobbyParent.add(s);
		Log.InformationLog("you can now select a server");
		


		lobbyParent.setVisible(true);
	}


}


