package server.GamePlayObjects;

import java.util.LinkedList;

import javax.annotation.processing.ProcessingEnvironment;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.game.Coordinates;
import shared.game.MapManager;
import shared.Protocol;
import shared.User;

/**
 * The Server Side Class Tank. Range is the Range in which the Tank can Attack.
 * movingRange is the Range the Tank can move in one Phase.
 * 
 * @author lucius
 * 
 */
public class Tank implements GamePlayObject, Defensive, Unit {
	private int id;
	private Coordinates position;
	private int healthPoints;
	private int range;
	private int attackPoints;
	private Coordinates target;
	private int movingRange;
	private Player Owner;
	private LinkedList<GamePlayObject> possibleTargets;
	public GamePlayObjectManager Manager;
	private int ammunation;
	private int price;
	private Coordinates PosAtEnd;

	public Tank(Coordinates pos, Player owner, GamePlayObjectManager manager)
			throws GameObjectBuildException {

		this.position = pos;
		this.healthPoints = Settings.Tank.healthPoints;
		this.range = Settings.Tank.attackRange;
		this.attackPoints = Settings.Tank.attackPoints;

		this.Owner = owner;
		this.movingRange = Settings.Tank.movingRange;
		this.Manager = manager;
		this.possibleTargets = new LinkedList<GamePlayObject>();
		this.ammunation = Settings.Tank.ammunation;
		this.price = Settings.Tank.price;
		this.build();

	}

	/**
	 * Funktionsrumpf für die Drawfunktion des Clients.
	 * 
	 */
	public int getId() {
		return this.id;
	}

	public void draw() {
	}

	/**
	 * Returns the Coordinates
	 */
	public Coordinates getPos() {
		return this.position;
	}

	/**
	 * Returns the Position the Tank will have at the of the Round.
	 * 
	 * @return Position at the End of the Round.
	 */
	public Coordinates getPosAtEnd() {
		return this.PosAtEnd;

	}

	/**
	 * Checks if the Object can be build here. //If true, the Object gets build.
	 * 
	 * @throws GameObjectBuildException
	 * 
	 */
	public void build() throws GameObjectBuildException {

		if (!MapManager.isInside(this.getOwner().getFieldID(), this.getPos().getX(), this.getPos().getY())) 
		{
			throw new GameObjectBuildException("Wrong Position");
		} else if (Owner.getMoney() < price) {
			throw new GameObjectBuildException("No Money");
		} else {
			Manager.addDefensive(this);
			Manager.addUnit(this);
			this.getOwner().removeMoney(this.getPrice());
		}

	}

	public long getPrice() {
		return (long) this.price;
	}

	/**
	 * Deletes all References //that the GarbageCollector kills the Object.
	 * 
	 */
	public void saveLiving() {
		if (this.getHealthPoints() > 0) {
			Manager.addDefensive(this);
			Manager.addUnit(this);
		}

	}

	/**
	 * never used.
	 */
	public void destruct() {
		Manager.removeDefensive(this);
		Manager.removeUnit(this);

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
	public GamePlayObject selectTarget() {
		if (this.possibleTargets.isEmpty())
			return null;

		GamePlayObject G = this.possibleTargets.peek();
		while (G.getHealthPoints() <= 0 && !this.possibleTargets.isEmpty()) {
			G = this.possibleTargets.pop();

		}
		if (this.possibleTargets.isEmpty())
			return null;
		return G;

	}

	public void addToPossibleTargets(GamePlayObject O) {
		if (this.possibleTargets.contains(O)) {
		}

		else {
			if (this.isAttackableObject(O)) {
				this.possibleTargets.add(O);

			}
		}

	}

	/**
	 * Clears the Targetlist
	 */
	public void clearTargetList() {
		this.possibleTargets.clear();

	}

	/**
	 * Attacks the first Target in the List while it isnt dead and the
	 * ammunation is >0
	 */
	public void attack() {
		try {
			while (this.ammunation > 0) {
				while (this.selectTarget().getHealthPoints() < 0) {
				}
				this.selectTarget().damage(getAttackPoints());
				this.getOwner().addMoney((long) this.getAttackPoints());

				this.ammunation--;

			}
		} catch (NullPointerException e) {
		} finally {
			this.ammunation = Settings.Tank.ammunation;
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
	public Player getOwner() {
		return this.Owner;

	}

	/**
	 * Send the Move to all other Defensive Objects Moves the Object to its
	 * Target.
	 * 
	 */
	public void move() {

		
		this.Manager.sendMoving(this.getPosAtEnd(), this);
		this.position = this.getPosAtEnd();

	}

	/**
	 * Calculates the Move of this Round.
	 */
	public void moveProv() {

		if (this.getTarget() == null || this.getTarget().equals(this.getPos())) {
			this.setTarget(this.getPos());
			this.PosAtEnd = this.getPos();

		}
		if (this.position.getDistance(getTarget()) <= this.movingRange) {

			this.PosAtEnd = new Coordinates(getTarget().getX(), getTarget()
					.getY());

		}

		else {

			double direction = (getTarget().getY() - this.position.getY())
					/ (getTarget().getX() - this.position.getX());

			double diffY = (getTarget().getY() - this.position.getY());
			double diffX = (getTarget().getX() - this.position.getX());
			double dir2 = diffY / diffX;
			direction = dir2;

			double nenner = (this.movingRange * this.movingRange);

			double zaehler = (1 + (direction * direction));

			double bruch = nenner / zaehler;

			int deltaX = (int) (Math.round(Math.sqrt(bruch)));

			int deltaY = (int) Math.round(deltaX * direction);

			this.PosAtEnd = new Coordinates(this.getPos().getX(), this.getPos()
					.getY());

			if (getTarget().getY() - this.position.getY() < 0) {
				if (deltaY < 0) {
					this.PosAtEnd.moveY(deltaY);
				} else {
					this.PosAtEnd.moveY(-deltaY);
				}

			} else if (deltaY < 0) {
				this.PosAtEnd.moveY(-deltaY);
			} else {
				this.PosAtEnd.moveY(deltaY);
			}
			if (getTarget().getX() - this.position.getX() < 0) {
				if (deltaX < 0) {
					this.PosAtEnd.moveX(deltaX);
				} else {
					this.PosAtEnd.moveX(-deltaX);
				}

			} else if (deltaX < 0) {
				this.PosAtEnd.moveX(-deltaX);
			} else {
				this.PosAtEnd.moveX(deltaX);
			}

		}

	}

	/**
	 * CHecks if the GamePlayObject O moves through its Range, and adds it to
	 * the possibleTargetList if its so
	 */
	public void checkLine(Coordinates Target, GamePlayObject O) {

		if (CircleTest.checkLine(Target.getX(), Target.getY(), O.getPos()
				.getX(), O.getPos().getY(), this.PosAtEnd.getX(), this.PosAtEnd
				.getY(), this.getRange())
				&& this.isAttackableObject(O)) {
			this.possibleTargets.add(O);

		}

	}

	/**
	 * sensless, but must be implemented.
	 */
	public void attack(GamePlayObject O) {
	}

	@Override
	public void addToTargets(Unit U) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId(int id) {
		if (id < 1000000 || id > 9999999)
			throw new IllegalArgumentException();
		else
			this.id = id;

	}


	@Override
	public String toProtocolString()
	{
		return Protocol.GAME_UPDATE_OBJECT.str()+Protocol.OBJECT_TANK.str()+position.getX()+" "+position.getY()+" "+id+" "+healthPoints;
	}
}
