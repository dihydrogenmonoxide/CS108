package client.game.field;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.List;

import client.game.GameFrame;

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
    
    
    void drawArrow(Graphics2D gd, double imageDim, List<Polygon> pol, int startX, int startY, int endX, int endY)
    {
        double arrowWidth = imageDim/2;
        double theta = 0.423 ;
        double length;
        double breite;
        double länge;
        double baseX, baseY ;
        double diffX= (double)endX - startX ;
		double diffY= (double)endY - startY ;

        length = (double)Math.sqrt( diffX * diffX + diffY * diffY ) ;
        breite = arrowWidth / ( length ) ;
        länge = arrowWidth / ( 2.0 * ( (double)Math.tan( theta ) / 2.0 ) * length ) ;

        // find the base of the arrow
        baseX = ( (double)endX - länge * diffX);
        baseY = ( (double)endY - länge * diffY);

        Polygon poly = new Polygon();
        poly.addPoint((int)(endX),(int)(endY));
        poly.addPoint( (int)(baseX + breite * -diffY),(int)( baseY + breite * diffX));
        poly.addPoint((int) (baseX - breite * -diffY),(int) (baseY - breite * diffX));

        pol.add(poly);
    }
}
