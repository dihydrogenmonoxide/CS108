package client.lobby;


import javax.imageio.ImageIO;
import javax.swing.*;

import client.events.ServerSelectedEvent;
import client.events.ServerSelectedListener;

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
	private JFrame lobby;
	/**width of the lobby in pixel.*/
	private int iLobbyX = 900; 
	/**height of the lobby in pixel.*/
	private int iLobbyY = 575;

	private PopupFactory factory=PopupFactory.getSharedInstance();
	private SelectServer s;
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
		lobby = new JFrame("SwissDefcon Lobby");
		lobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lobby.setSize(iLobbyX, iLobbyY);
		lobby.setResizable(false);
		lobby.setLocation(screenX / 2 - iLobbyX / 2, screenY / 2 - iLobbyX / 2);
		lobby.setContentPane(bg);
		
		s = new SelectServer();
		s.addServerSelectedListener(new ServerSelectedListener(){
			public void serverSelected(ServerSelectedEvent ev){
				try
				{
					
				    Popup popup = factory.getPopup(lobby,new JLabel("Well, this is just a warning message"), 50, 50);
				    popup.show();
					/*
					 * 
					 * 
					 *   DO STUFF HERE
					 * 
					 * 
					 * 
					 * */	
					Log.InformationLog("-->Connected to **** as ******");
				}
				catch (Exception e)
				{
					
				}	
			}
		});
		lobby.add(s);
		Log.InformationLog("you can now select a server");
		


		lobby.setVisible(true);
	}


}


