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
