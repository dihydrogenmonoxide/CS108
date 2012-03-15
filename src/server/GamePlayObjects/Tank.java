package server.GamePlayObjects;

import shared.Coordinates;
import shared.User;

public class Tank extends Unit implements GamePlayObject, InterAct{
	private Coordinates position;
	private int healthPoints;
	private int range;
	private int attackPoints;
	private Coordinates target;
	private int movingRange;
	private User Owner;
	
	public Tank(Coordinates pos, User owner){
		this.position=pos;
		this.healthPoints=1000;
		this.range=10;
		this.attackPoints=20;
		this.build();
		this.Owner=owner;
		
	}
	public void draw(){}
	
	public Coordinates getPos(){
		return this.position;
	}
	
	//Asks the Server if the Object can be build here.
	//If true, the Object gets build.
	public void build(){
		/*To Do: Ask the Server if a Tank can be build at this.position
		 * if True, build the Object.
		 * if false, destroi it and message to Client: Can't be build here
		 */
		
	}
	
	//Loads the Animation of its destruction and deletes all References
	//that the GarbageCollector kills the Object.
	public void destruct(){
		// To do: set all references to null
		
		
	}
	
	//Returns the Health Points of the Object
	public int getHealthPoints(){
		
		return this.healthPoints;
	}
	
	//Hits this Object with Damage.
	public void damage(int damPoints){
		this.healthPoints-=damPoints;
		if(this.getHealthPoints()<=0)
		{
			this.destruct();
		}
		
	}
	
	//get the Range of the Tank
	public int getRange() {
		
		return this.range;
	}
	
	//Attack the target
	@Override
	public void attack(GamePlayObject target) {
		if(this.isAttackableObject(target) && this.isInRange(target))
		{
			target.damage(this.getAttackPoints());
			
		}
		
	}
	
	//get the Tank's AttackPoints
	private int getAttackPoints() {
		
		return this.attackPoints;
	}
	
	//is the target in the Tank's Range
	@Override
	public boolean isInRange(GamePlayObject target) {
		if(this.range>=this.position.getDistance(target.getPos()))return true;
		return false;
	}
	
	//is the target Attackable by a Tank
	@Override
	public boolean isAttackableObject(GamePlayObject target) {
		
		return true;//this is for Testing, that Tank1 can Attack Tank2 in the Test.
					//Remark that Tank3 is out of Range!!!
		
		//The Code below works too.
		//if( target instanceof Building && this.getOwner()!=target.getOwner())return true;
		//return false;
	}
	
	//get the current Target of the Tank.
	public Coordinates getTarget(){
		
		return this.target;
		
	}
	
	
	public void setTarget(Coordinates target){
		
			this.target=target;
	}
	

	public User getOwner(){
		return this.Owner;
		
	}
}
