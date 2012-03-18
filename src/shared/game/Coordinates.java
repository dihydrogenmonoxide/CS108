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
