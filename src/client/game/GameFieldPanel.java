package client.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.net.Clientsocket;

public class GameFieldPanel extends JPanel {
	/**Buffered image to Paint Map*/
	private BufferedImage img;
	/**Image for DoubleBufferedImage*/
	private Image dbImage;
	private Graphics dbg;
	private GameButtonsPanel but;
	
	private Clientsocket socket;


	public GameFieldPanel(Clientsocket s){
		this.socket = s;
		
		try {
			img = ImageIO.read(new File("bilder/full.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel gameLabel = new JLabel();
		c.ipadx=(int)(img.getWidth()*0.45);
		c.ipady=(int)(img.getHeight()*0.45);
		c.gridwidth=20;
		c.gridx=0;
		c.gridy=0;
		this.add(gameLabel,c);
		
		but = new GameButtonsPanel(socket);
		but.selected();
		/*durch das BufferedImage funktioniert es nocht nicht ganz so, wie es sollte. wenn ihr schnell drückt könnt ihr es jedoch sehen;)*/
		addMouseListener(new MyMouseListener());

	}
	
	
	public void paintComponent( Graphics g ) {
		try{
			g.drawImage(img, 0, 0,(int)( img.getWidth()*0.45),(int)(img.getHeight()*0.45) , 0, 0, img.getWidth(), img.getHeight(), new Color(0, 0, 0), null);
			repaint();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g){
		dbImage= createImage(getWidth(), getHeight());
		dbg= dbImage.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbImage,0,0,this);
	}
	

	class MyMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent event)
		{
			Graphics g = getGraphics();
			int x = event.getX();
			int y = event.getY();
			g.setColor(Color.green);
	        g.fillOval(x-10,y-10,20,20);
	         
	      }
	  }
	
}
