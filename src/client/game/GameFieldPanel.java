package client.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameFieldPanel extends JPanel {
	private BufferedImage img;
	private Image dbImage;
	private Graphics dbg;


	public GameFieldPanel(){
		try {
			img = ImageIO.read(new File("full.png"));
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
		

		
	}
	
	
	public void paintComponent( Graphics g ) {
		//super.paint( g );
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
	

}
