package client.game;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import shared.Protocol;

import client.net.Clientsocket;
 
/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 27.09.2011
  * @author
  */
 
public class GameFrame extends JDialog {
	/**JFrame which contains the GUI for the Lobby.*/
	private JFrame game;
	/**actual width of the screen.*/
	private int screenX;
	/**actual height of the screen.*/
	private int screenY;	
	/**the Connection to listen to*/
	private Clientsocket socket;
	/**Frame for Game*/
	private InnerGameFrame gameFrame;

	
 
	public GameFrame(final JFrame lobbyParent, Clientsocket s) {
		this.socket=s;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();	
		DisplayMode disp = screen.getDisplayMode();
		screenX = disp.getWidth();
		screenY = disp.getHeight();
	  
                
                
		JPanel bg = createBackground();
		game = new JFrame("SwissDefcon Game");
                
                
                //-- add content
		game.setContentPane(bg);
		gameFrame= new InnerGameFrame(game, socket);
		game.add(gameFrame);
		
                //-- set size and location
                game.setSize(game.getPreferredSize());
		game.setLocation(screenX / 2 - game.getWidth() / 2, screenY / 2 - game.getHeight() / 2);
                
		game.setResizable(false);
		game.setVisible(true);
		
		/**WindowClosingEvent to return to InnerLobby*/
		game.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				game.dispose();
                                //TODO notifiy the GameObjectManager to reset everything.
				socket.sendData(Protocol.GAME_QUIT.str());
				lobbyParent.setVisible(true);
			}
		});	
		
                //TODO listener which closes the game if something unforeseen happens.
		

	}
	
	private JPanel createBackground() {
		
		JPanel bg = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
					g.setColor(Color.orange);
					g.fillRect( 0, 0, this.getWidth(), this.getHeight());
			}
		};
		return bg;
	}
}

