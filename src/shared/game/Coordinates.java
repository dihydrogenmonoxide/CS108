

package shared.game;


public class Coordinates {
    public static final int coordEndY = 300000;
    /**Starting point of the coordinates on the X axis.*/
    public static final int coordStartX = 450000;
    /**Ending point of the coordinates on the X axis.*/
    public static final int coordEndX = 800000;
    public static final int coordStartY = 100000;
    
    
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
        @param x the point on the x axis
        @param y the point on the y axis
        @param totLength the total length of the map in pixels
        @param totLength the total Height of the map in pixels
        @return a Coordinate object with the desired Coordinates.
        */
       static Coordinates pixelToCoord(int x, int y, int totLength, int totHeight)
        {
            //-- get x coords
            int coordDeltaX = coordEndX - coordStartX;
            int coordX = coordStartX + coordDeltaX * x / totLength;
            
            //-- get y coords
            int coordDeltaY = coordEndY - coordStartY;        
            int coordY = coordStartY + coordDeltaY*y/totHeight;
            
            //-- create coordinates
            return new Coordinates(coordX, coordY);
        }
        
}
