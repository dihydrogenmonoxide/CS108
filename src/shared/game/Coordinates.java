
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

}
