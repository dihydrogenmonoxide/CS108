package client.game;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Line extends JPanel implements ActionListener{
	Timer timer;

	
	private double xstart=100;
	private int ystart=100;
	private int xend=200;
	private int yend=110;
	final double xdif=xend-xstart;
	double ydif=yend-ystart;
	public Line() {
		timer = new Timer(1000, this);
	    timer.start();
    }

	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
	    g2d.setStroke(new BasicStroke(3));    
	    g2d.setColor(Color.red);
		g2d.drawLine((int)xstart, ystart, (int) xstart, ystart);
	   
	}

	public void actionPerformed(ActionEvent e) {

		if(xdif==0){
			if(ystart<=yend){
				ystart++;
				  
			}
			if(ystart>=yend){
				ystart--;
				}
			}

		else{
			if(ydif==0){
				if(xstart<=xend){
					xstart++;
				}
				if(xstart>=xend){
					  xstart--;
				}
			}
			else{
				double fact=xdif/ydif;
				if(xdif<=0&&ydif<=0){
					if(xstart>=xend){
						xstart-=fact;
						ystart--;
					}
				}
		  
				if(xdif<=0&&ydif>=0){
					if(xstart>=xend){
						xstart+=fact;
						ystart++;
					}
				}
		  
				if(xdif>=0&&ydif<=0){
					if(xstart<=xend){
						xstart-=fact;
						ystart--;
					}
				}
		  
				if(xdif>=0&&ydif>=0){
					if(xstart<=xend){
						xstart+=fact*02;
						ystart++;
					}
				}
			}
		}
		repaint();
	}
}