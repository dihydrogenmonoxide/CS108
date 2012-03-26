package client.lobby;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
 
/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 27.09.2011
  * @author
  */
 
public class GameFrame extends JDialog {
	private int iLobbyX=900;
	private int iLobbyY=550;
	/**JFrame which contains the GUI for the Lobby.*/
	private JFrame game;
	/**actual width of the screen.*/
	private int screenX;
	/**actual height of the screen.*/
	private int screenY;	
 
	public GameFrame() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		DisplayMode disp = screen.getDisplayMode();
		screenX = disp.getWidth();
		screenY = disp.getHeight();
		
		
	  
		JPanel bg = createBackground();
		
		game = new JFrame("SwissDefcon Game");
		game.setSize(iLobbyX, iLobbyY);
		game.setLocation(screenX / 2 - iLobbyX / 2, screenY / 2 - iLobbyY / 2);
		game.setBackground(Color.orange);
		game.setContentPane(bg);
	  
		game.setResizable(false);
		game.setVisible(true);

	  
	  
		

	}

  private JPanel createBackground() {
		
		JPanel bg = new JPanel()
		{			
			/**paint the swiss map in the background*/
			public void paintComponent(Graphics g)
			{
				BufferedImage img = null;
				try 
				{
					img = ImageIO.read(new File("full.png"));
					g.drawImage(img, 0, 0, iLobbyX, iLobbyY, 0, 0, img.getWidth()+200, img.getHeight()+300, Color.black, this);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};
		return bg;
	}
}