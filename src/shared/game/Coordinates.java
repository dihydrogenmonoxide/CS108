

package shared.game;

import java.awt.Dimension;
import shared.Log;


public class Coordinates {
    /**Starting point of the coordinates on the X axis.*/
     static final int coordStartX = 450000;
    /**Ending point of the coordinates on the X axis.*/
     static final int coordEndX = 800000;
     /**Starting point of the coordinates on the Y axis.*/
     static final int coordStartY = 100000;
     /**Ending point of the coordinates on the X axis.*/
     static final int coordEndY = 300000;
    
	private int x;
	private int y;
	
	public Coordinates(int x, int y){
		this.x=x;
		this.y=y;
		
	}
	
	public double getDistance(Coordinates other)
	{
		double a=this.getX()-other.getX();
		
		double a2=a*a;
		
		double b=this.getY()-other.getY();
		
		double b2=b*b;
		
		double c= Math.sqrt(a2+b2);
		 return c;
		
	}
	
	public void moveX(int diffx){
		this.x+=diffx;
		
	}
	
	public boolean equals(Coordinates other){
		if(this.getX()==other.getX() && this.getY()==other.getY())
			return true;
		
		else
			return false;
	}
	
	public void moveY(int diffy){
		this.y+=diffy;
		
	}
	
	public int getX(){
		
		return this.x;
	}
	
        public int getY(){
		
		return this.y;
	}
        /**returns the coordinates as "x y".
         @return the Coordinates as String*/
        public String toString(){
            return this.x + " " + this.y;
        }
        
       /**converts from pixel to Coordinates.
        * it assumes pixel (0,0) is the upper left pixel. it has the coordinate 450'000/100'000
        @param x the point on the x axis
        @param y the point on the y axis
        @param totWidth the total length of the map in pixels
        @param totWidth the total Height of the map in pixels
        @return a Coordinate object with the desired Coordinates.
        */
      public static Coordinates pixelToCoord(int x, int y, Dimension image)
        {
            if(image.width == 0 || image.height == 0){
                Log.ErrorLog("Coordinates: Division by Zero, you got a bug in your code");
                return null;
            }
            
            //-- get x coords
            int coordDeltaX = coordEndX - coordStartX;
            int coordX = coordEndX - coordDeltaX * (image.width-x) / image.width;
            
            //-- get y coords
            int coordDeltaY = coordEndY - coordStartY;        
            int coordY = coordStartY + coordDeltaY*(y)/image.height;
            
            //-- create coordinates
            return new Coordinates(coordX, coordY);
        }
        
      /**calculates the pixels for given Coordinates.
       @param */
      public static Dimension coordToPixel(Coordinates coord, Dimension image)
        {
            if(image.width == 0 || image.height == 0){
                Log.ErrorLog("Coordinates: Division by Zero, you got a bug in your code");
                return null;
            }
            
            //-- get X pixel
            int coordDeltaX = coordEndX - coordStartX;
            int pixelX = (coord.x - coordStartX)*image.width/coordDeltaX;
             
            //-- get Y pixel
            int coordDeltaY = coordEndY - coordStartY;      
            int pixelY = (coord.y - coordStartY)*image.height/coordDeltaY;
            
            //-- create coordinates
            return new Dimension(pixelX, pixelY);
        }
      
      public static int radCoordToPixel(int i, Dimension image){
    	  if(i==0){
    		  Log.ErrorLog("Division by Zero, you got a bug in your code");
    	  }
    	  int coordDeltaX= coordEndX-coordStartX;
    	  int rad= i*image.width/coordDeltaX;
    	  return rad;
      }
      
}
