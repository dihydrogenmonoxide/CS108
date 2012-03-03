package client.lobby;


import javax.imageio.ImageIO;
import javax.swing.*;

import shared.Log;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


/** this is the lobby at the start.
 * it allows joining a server, chatting with users, joining a game.
 * @ param none
 */
public class ClientLobby extends JFrame{
	private int screenX;
	private int screenY;

	private JFrame lobby;
	private int i_LobbyX=900;
	private int i_LobbyY=575;


	public ClientLobby()
	{
		/*
		 * Get Infos about the screen
		 * */
		GraphicsEnvironment ge= GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen=ge.getDefaultScreenDevice();
		DisplayMode disp = screen.getDisplayMode();
		screenX = disp.getWidth();
		screenY = disp.getHeight();



		/*
		 * Set up background
		 * The bg-image is drawn in a JPanel which is laid under all the other panes with User-IO
		 * */
		JPanel bg = new JPanel(){
			/**paint the swiss map in the background*/
			public void paintComponent(Graphics g)
			{
				BufferedImage img = null;
				try 
				{
					img = ImageIO.read(new File("lobby_bg.jpg"));
					g.drawImage(img, 0, 0, i_LobbyX, i_LobbyY, 0, 0, img.getWidth(), img.getHeight(), new Color(0,0,0), null);
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
		lobby.setSize(i_LobbyX,i_LobbyY);
		lobby.setResizable(false);
		lobby.setLocation(screenX/2-i_LobbyX/2,screenY/2-i_LobbyX/2);
		lobby.setContentPane(bg);
		
		SelectServer s=new SelectServer();
		lobby.add(s);
		Log.InformationLog("you can now select a server");
		
		
		lobby.setVisible(true);
	}


}


