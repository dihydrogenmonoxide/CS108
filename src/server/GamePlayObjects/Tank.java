package server.GamePlayObjects;

import shared.Coordinates;
import shared.User;

/**
 * The Server Side Class Tank. Range is the Range in which the Tank can Attack.
 * movingRange is the Range the Tank can move in one Phase.
 * 
 * @author lucius
 * 
 */
public class Tank extends Unit implements GamePlayObject, InterAct {
	private Coordinates position;
	private int healthPoints;
	private int range;
	private int attackPoints;
	private Coordinates target;
	private int movingRange;
	private User Owner;

	public Tank(Coordinates pos, User owner) {
		this.position = pos;
		this.healthPoints = 1000;
		this.range = 10;
		this.attackPoints = 20;
		this.build();
		this.Owner = owner;
		this.movingRange = 50;

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
	public void move() {
		int direction = 1;
		int alreadyMovedSquared = 0;
		double deltaY = this.target.getY() - this.position.getY();
		double deltaX = this.target.getX() - this.position.getX();

		if (deltaY < 0 && deltaX < 0)
			direction = -1;
		while (target != null
				&& !(this.target.getX() == this.position.getX() && this.target
						.getY() == this.position.getY())
				&& alreadyMovedSquared <= this.movingRange * this.movingRange) {

			if (this.target.getX() == this.position.getX()) {
				this.position.moveY(1);
				alreadyMovedSquared++;

			} else if (this.target.getY() == this.position.getY()) {
				this.position.moveX(1);
				alreadyMovedSquared++;

			} else {

				this.position.moveX(10*direction);

				int mY = (int) Math.round(deltaY / deltaX * direction * 10);

				this.position.moveY(mY);

				alreadyMovedSquared += 100 + mY;
			}

		}

	}
}
