package client.game.field;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.List;

public class ChangingLists {

    void updateLine(List<Line2D> line, double xdif, double ydif, double xmove, double ymove, double yend2, double xend2, Graphics g, int i)
    {
		if(xdif==0)
		{
			if(ymove<=yend2)
			{
				ymove++;
				  
			}
			if(ymove>=yend2)
			{
				ymove--;
				}
			}

		else
		{
			if(ydif==0)
			{
				if(xmove<=xend2)
				{
					xmove++;
				}
				if(xmove>=xend2)
				{
					xmove--;
				}
			}
			else
			{
				double fact=xdif/ydif;
				
				if(xdif<=0&&ydif<=0)
				{
					if(xmove>=xend2)
					{
						xmove-=fact;
						ymove--;
					}
				}
		  
				if(xdif<=0&&ydif>=0)
				{
					if(ymove<=yend2)
					{
						xmove+=fact;
						ymove++;
					}
				}
		  
				if(xdif>=0&&ydif<=0)
				{
					if(xmove<=xend2)
					{
						xmove-=fact;
						ymove--;
					}
				}
		  
				if(xdif>=0&&ydif>=0)
				{
					if(xmove<=xend2)
					{
						xmove+=fact;
						ymove++;
					}
				}
			}
		}
		line.get(i).setLine(xmove, ymove, line.get(i).getX2(), line.get(i).getY2());

	
	}
    void drawArrow(List<Polygon> pol, int startX, int startY, int endX, int endY)
    {
	    double radians=90*Math.PI/180;
	    if(endX-startX!=0)
	    {
	    	radians = Math.atan((endY-startY)/(endX-startX));
	    }
	
	    Polygon poly = new Polygon();
	    double rad1= Math.toRadians(30);
	    double rad=Math.toRadians(-30);
		double y=(int) (Math.cos(rad)*30);
		double x=(int) (Math.sin (rad) * 30);
		double y1 = (int) (Math.cos(rad1)*30);
		double x1 = (int) (Math.sin (rad1) * 30);
		poly.addPoint((int)endX,(int) endY);
		poly.addPoint((int)(x1+endX),(int)(y1+endY));
		poly.addPoint((int)(x+endX),(int)(y+endY));
		
		//TODO rotate Arrows
		if(radians<=0&&endX-startX<=0||endY-startY<=0&&radians>0)
		{
//			gd.rotate(radians-Math.toRadians(90),endX, endY);
		}else
		{
//			gd.rotate(radians+Math.toRadians(90), endX, endY);
		}
		pol.add(poly);
    }
}
