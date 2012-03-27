package server.GamePlayObjects;



import shared.game.Coordinates;
import shared.User;

/**
 * The Server Side Class Tank. Range is the Range in which the Tank can Attack.
 * movingRange is the Range the Tank can move in one Phase.
 * 
 * @author lucius
 * 
 */
public class Bomber extends Unit implements GamePlayObject, InterAct {
	private Coordinates position;
	private int healthPoints;
	private int range;
	private int attackPoints;
	private Coordinates target;
	private double movingRangeSquared;
	private User Owner;

	public Bomber(Coordinates pos, User owner) {
		this.position = pos;
		this.healthPoints = 1000;
		this.range = 10;
		this.attackPoints = 20;
		this.build();
		this.Owner = owner;
		this.movingRangeSquared = 500;

	}

	public void draw() {
	}

	public Coordinates getPos() {
		return this.position;
	}

	/**
	 * Asks the Server if the Object can be build here. //If true, the Object
	 * gets build.
	 * 
	 */
	public void build() {
		/*
		 * To Do: Ask the Server if a Tank can be build at this.position if
		 * True, build the Object. if false, destroi it and message to Client:
		 * Can't be build here
		 */

	}

	/**
	 * Deletes all References //that the GarbageCollector kills the Object.
	 * 
	 */
	public void destruct() {
		// To do: set all references to null

	}

	/**
	 * Returns the Health Points of the Object
	 * 
	 */
	public int getHealthPoints() {

		return this.healthPoints;
	}

	/**
	 * Hits this Object with Damage.
	 * 
	 */
	public void damage(int damPoints) {
		this.healthPoints -= damPoints;
		if (this.getHealthPoints() <= 0) {
			this.destruct();
		}

	}

	/**
	 * get the Range of the Tank
	 * 
	 */
	public int getRange() {

		return this.range;
	}

	/**
	 * Attack the target
	 * 
	 */

	public void attack(GamePlayObject target) {
		if (this.isAttackableObject(target) && this.isInRange(target)) {
			target.damage(this.getAttackPoints());

		}

	}

	/**
	 * get the Tank's AttackPoints
	 * 
	 * @return
	 */
	private int getAttackPoints() {

		return this.attackPoints;
	}

	/**
	 * is the target in the Tank's attack Range
	 * 
	 */

	public boolean isInRange(GamePlayObject target) {
		if (this.range >= this.position.getDistance(target.getPos()))
			return true;
		return false;
	}

	/**
	 * is the target Attackable by a Tank (Planes are not attackable by a
	 * Tank..., Objects of the same Owner should not attack each other....
	 */

	public boolean isAttackableObject(GamePlayObject target) {

		if ((target instanceof Building || target instanceof Tank)
				&& this.getOwner() != target.getOwner())
			return true;
		return false;
	}

	/**
	 * get the current Target of the Tank.
	 * 
	 * @return Coordinates of the Target to Move
	 */
	public Coordinates getTarget() {

		return this.target;

	}

	/**
	 * Set Coordinates as Moving Target
	 * 
	 * @param target
	 */
	public void setTarget(Coordinates target) {

		this.target = target;
	}

	/**
	 * returns the Owner of this Object.
	 */
	public User getOwner() {
		return this.Owner;

	}

	/**
	 * Moves the Object to its Target. Because of DivisionbynullException and
	 * Overflowing he moves as long as the X or Y coordinates are not Reached
	 * with a Stepsize of 10 for X, and an Y Stepsize of deltaY/deltaX*10
	 * 
	 */
	public boolean move() {
		int direction = 1;
		int directionY=1;
		
		double deltaY = this.target.getY() - this.position.getY();
		double deltaX = this.target.getX() - this.position.getX();

		if (deltaX < 0)
			direction = -1;
		
		if(deltaY<0)
			directionY=-1;
		if (target != null
				&& !(this.target.getX() == this.position.getX() && this.target
						.getY() == this.position.getY())
				&& this.movingRangeSquared>0) {

			if (this.target.getX() == this.position.getX()) {
				this.position.moveY(5*directionY);
				System.out.println("Moved 5 in X");
				this.movingRangeSquared-=5;
				System.out.println("this.movingRangeSquared ist : "+ this.movingRangeSquared);
				return true;

			} else if (this.target.getY() == this.position.getY()) {
				this.position.moveX(5*direction);
				System.out.println("Moved 5 in Y");
				this.movingRangeSquared-=5;
				System.out.println("this.movingRangeSquared ist : "+ this.movingRangeSquared);
				return true;
			} else {

				this.position.moveX(5*direction);
				System.out.println("Steigung ist: "+deltaY/deltaX);
				int mY = (int) Math.round(deltaY / deltaX * direction*5 );

				this.position.moveY(mY);
				System.out.println("Moved "+5*direction+" in X Direction and "+mY+" in Y Direction");

				this.movingRangeSquared -= Math.cbrt((double)25 + mY*mY);
				System.out.println("this.movingRangeSquared ist : "+ this.movingRangeSquared);
				return true;
			}
			
			
		}
		else
			return false;

	}
}

