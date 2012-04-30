package client.game.field;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class MoveObjects extends JPanel implements ActionListener{
	Timer timer;

	
	private double xstart;
	private int ystart;
	private int xend;
	private int yend;
	final double xdif=xend-xstart;
	double ydif=yend-ystart;
	List<Lines> line;
	public MoveObjects(List<Lines> line) {
		this.line=line;
		timer = new Timer(1000, this);
	    timer.start();
	    
    }

	public void paint(Graphics g) {

		g.setColor(Color.red);
		for(Lines l: line){
			g.drawLine(l.xs, l.ys, l.xe, l.ye);
			xstart=l.xs;
			xend=l.xe;
			ystart=l.ys;
			yend=l.ye;
		}
	   
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