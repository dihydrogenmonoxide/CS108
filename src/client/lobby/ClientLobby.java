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
		 * Set up lobby
		 * */
		lobby = new JFrame("SwissDefcon Lobby");
		lobby.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lobby.setSize(i_LobbyX,i_LobbyY);
		lobby.setLocation(screenX/2-i_LobbyX/2,screenY/2-i_LobbyX/2);

		SelectServer s=new SelectServer();
		s.setScreen(i_LobbyX,i_LobbyY);
		lobby.setContentPane(s);
		lobby.setVisible(true);


	}


}
