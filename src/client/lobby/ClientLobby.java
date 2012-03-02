package client.lobby;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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


		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JList serverList;
		String[] entries = { "Entry 1", "Entry 2", "Entry 3",
				"Entry 4", "Entry 5", "Entry 6" };
		serverList = new JList(entries);
		serverList.setFixedCellWidth(100);
		serverList.setVisibleRowCount(4);
		pane.add(serverList, c);

		JButton button = new JButton("join server");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;      //make this component tall
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(button, c);



		lobby.setContentPane(pane);









		lobby.setVisible(true);


	}
}
