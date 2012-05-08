package server.GamePlayObjects;





import shared.game.GameSettings;
import java.util.LinkedList;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.game.Coordinates;
import shared.game.MapManager;
import shared.Protocol;
import shared.User;

/**
 * The Server Side Class Bank.
 * 
 * @author lucius
 * 
 */
public class Bank implements GamePlayObject, Building, Unit {
	private int id;
	private Coordinates position;
	private int healthPoints;
	private int range;
	private double attackPoints;
	private Coordinates target;
	private int movingRange;
	private Player Owner;
	private LinkedList<GamePlayObject> possibleTargets;
	public GamePlayObjectManager Manager;
	private int ammunation;
	private int price;
	private Coordinates PosAtEnd;

	public Bank(Coordinates pos, Player owner, GamePlayObjectManager manager)
			throws GameObjectBuildException {

		this.position = pos;
		this.healthPoints = GameSettings.Bank.healthPoints;
		this.range = GameSettings.Bank.attackRange;
		this.attackPoints = GameSettings.Bank.attackPoints;

		this.Owner = owner;
		this.movingRange = GameSettings.Bank.movingRange;
		this.Manager = manager;
		this.possibleTargets = new LinkedList<GamePlayObject>();
		this.ammunation = GameSettings.Bank.ammunation;
		this.price = GameSettings.Bank.price;
		this.build();

	}

	/**
	 * Funktionsrumpf für die Drawfunktion des Clients.
	 * 
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * Funktionsrumpf für die Drawfunktion des Clients.
	 * 
	 */
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
	 * Checks if the Object can be build here. //If true, the Object
	 * gets build.
	 * 
	 * @throws GameObjectBuildException
	 * 
	 */
	public void build() throws GameObjectBuildException {

		

		if (!MapManager.isInside(this.getOwner().getFieldID(), this.getPos().getX(), this.getPos().getY())) 
		{
			throw new GameObjectBuildException("Wrong Position");

		} else if (Owner.getMoney()<price) {
			throw new GameObjectBuildException("No Money");
		} else {
			Manager.addUnit(this);
			
			this.getOwner().removeMoney(this.getPrice());
		}

	}
	
	public long getPrice(){
		return (long)this.price;
	}
	
	

	
	/**
	 * never used.
	 */
	public void destruct() {
		
		

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
		return null;

	}

	public void addToPossibleTargets(GamePlayObject O) {
		

	}
	/**
	 * Clears the Targetlist
	 */
	public void clearTargetList() {
	

	}

	/**
	 * Attacks the first Target in the List while it isnt dead and the ammunation is >0
	 */
	public void attack() {
		this.getOwner().addMoney((long)(this.Owner.getMoney()*this.getAttackPoints()));
	}

	/**
	 * get the Tank's AttackPoints
	 * 
	 * @return
	 */
	private double getAttackPoints() {

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
		return false;
	}

	/**
	 * Senseless for Buildings
	 */
	public Coordinates getTarget() {

		return this.target;

	}

	/**
	 * Set Coordinates as Moving Target
	 * Senseless for Buildings, but is in the Interface
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
	 * Send the Move to all other Defensive Objects
	 * Moves the Object to its Target. 
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
		
		
		
			this.setTarget(this.getPos());
			this.PosAtEnd = this.getPos();

	}
	/**
	 * CHecks if the GamePlayObject O moves through its Range, and adds it to the possibleTargetList if its so
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
	public void setId(int id) {
		if(id<1000000 || id>9999999)
			throw new IllegalArgumentException();
		else
		this.id=id;
		
	}
	
	@Override
	public String toProtocolString()
	{
		return Protocol.GAME_UPDATE_OBJECT.str()+Protocol.OBJECT_BANK.str()+position.getX()+" "+position.getY()+" "+id+" "+Owner.getID()+" "+healthPoints;
	}
        
         public String toTargetString()
        {
            return Protocol.GAME_UPDATE_OBJECT.str()+Protocol.OBJECT_BANK.str()+target.getX()+" "+target.getY()+" "+id+" "+Owner.getID()+" "+healthPoints;
        }
}




