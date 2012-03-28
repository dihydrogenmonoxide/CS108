<<<<<<< HEAD:src/shared/Coordinates.java
package shared;

public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y){
		this.x=x;
		this.y=y;
		
	}
	
	public double getDistance(Coordinates other)
	{
		return Math.cbrt((this.x-other.x)*(this.x-other.x) + 
				(this.y-other.y)*(this.y-other.y));
		
	}
	
	public void moveX(int diffx){
		this.x+=diffx;
		
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

}
=======
package shared.game;

public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y){
		this.x=x;
		this.y=y;
		
	}
	
	public double getDistance(Coordinates other)
	{
		return Math.cbrt((this.x-other.x)*(this.x-other.x) + 
				(this.y-other.y)*(this.y-other.y));
		
	}

}
>>>>>>> 6f4846c53e727d95dffc80aa2c0a1c6d0c33097a:src/shared/game/Coordinates.java
