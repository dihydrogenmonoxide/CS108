package client.lobby;


import javax.swing.*;
import java.awt.*;


/** this is the lobby at the start.
 * it allows joining a server, chatting with users, joining a game.
 * @ param none
 */
public class ClientLobby extends JFrame{
	private int screenX;
	private int screenY;

	private JFrame lobby;
	private int lobbyX=600;
	private int lobbyY=600;


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
		 * Set up lobby
		 * */
		lobby = new JFrame("SwissDefcon Lobby");
		lobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lobby.setSize(lobbyX,lobbyY);
		lobby.setLocation(screenX/2-lobbyX/2,screenY/2-lobbyX/2);

		SelectServer s=new SelectServer();
		lobby.setContentPane(s);

		lobby.setVisible(true);
		

	}

}
