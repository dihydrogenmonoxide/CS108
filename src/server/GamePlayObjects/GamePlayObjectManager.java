
package server.GamePlayObjects;

import java.util.LinkedList;

import server.Server;
import server.players.Player;
import shared.game.Coordinates;

public class GamePlayObjectManager {

	private LinkedList<GamePlayObject> AllObjects;
	private LinkedList<Defensive> Defensives;
	private LinkedList<Unit> Units;
	private int maxid;
	private Server Server;

	public GamePlayObjectManager(Server server) {
		this.AllObjects = new LinkedList<GamePlayObject>();
		this.Defensives = new LinkedList<Defensive>();
		
		this.Units = new LinkedList<Unit>();
		this.maxid=1000000;
		this.Server=server;
	}
	
	public Server getServer(){
		return this.Server;
		
	}
	
	public void distributeId(GamePlayObject o){
		if(this.maxid>9999999)
			this.maxid=1000000;
		o.setId(this.maxid);
		this.maxid++;
		
	}
	/**
	 * Ad an Object of Type Defensive to the List of Defensive Objects and to
	 * the List of all GamePlayObjects in the List AllObjects.
	 * 
	 * @param Defensive
	 *            O
	 */
	public void addDefensive(Defensive O) {
		if (O instanceof Defensive) {
			Defensives.add(O);
			if (AllObjects.contains(O)) {
			} else {
				AllObjects.add(O);
				this.distributeId(O);
			}

		} else
			throw new IllegalArgumentException();
	}

	/**
	 * Ad an Object of Type Unit to the List of Unit Objects and to the List of
	 * all GamePlayObjects in the List AllObjects.
	 * 
	 * @param Unit
	 *            U
	 */
	public void addUnit(Unit U) {
		if (U instanceof Unit) {
			Units.add(U);

			if (AllObjects.contains(U)) {
			} else
			{
				AllObjects.add(U);
				this.distributeId(U);
			}
		} else
			throw new IllegalArgumentException();
	}
	
	public GamePlayObject getObjectById(int id){
		for(GamePlayObject O:AllObjects){
			if(O.getId()==id)return O;
			
		}
		GamePlayObject O=null;
		return O;
		
	}
	public LinkedList<GamePlayObject> getPlayersObjectList(Player player){
		LinkedList<GamePlayObject> Playerslist= new LinkedList<GamePlayObject>();
		for(GamePlayObject O:AllObjects){
			if(O.getOwner()==player)Playerslist.add(O);
			
		}
		
		return Playerslist;
		
	}

	/**
	 * 
	 * Ad an Object of Type Building to the List of Building Objects and to the
	 * List of all GamePlayObjects in the List AllObjects.
	 * 
	 * @param Building
	 *            U
	 * 
	 */


	/**
	 * never used
	 * 
	 * @param O
	 */
	public void removeDefensive(Defensive O) {
		if (O instanceof Defensive) {
			Defensives.remove(O);
			AllObjects.remove(O);

		} else
			throw new IllegalArgumentException();
	}

	/**
	 * never used
	 * 
	 * @param O
	 */
	public void removeUnit(Unit U) {
		if (U instanceof Unit) {
			Units.remove(U);
			AllObjects.remove(U);
		} else
			throw new IllegalArgumentException();
	}


	

	/**
	 * Sends the Move the Object O will do in the Next round to all Defensives
	 * 
	 * @param Target
	 * @param O
	 */
	public void sendMoving(Coordinates Target, GamePlayObject O) {
		for (Defensive D : Defensives) {
			
			D.checkLine(Target, O);
		}
	}

	/**
	 * Returns the List with all GamePlayObjects
	 * 
	 * @return LinkedList<GamePlayObject>
	 */
	public LinkedList<GamePlayObject> getObjectList() {
		return this.AllObjects;
	}

	/**
	 * The Function which calculates a Round: Fist Every Unit calculates its
	 * move
	 * 
	 * Every Unit sends to every Defensive it's Moving Every Defensive saves the
	 * Objects which move through its Range in a List of possible Targets
	 * 
	 * Every Unit Moves
	 * 
	 * Every Unit shoots its ammunation at a randomly chosen Target of the
	 * possibleTarget List
	 * 
	 * A Helplist is generated
	 * 
	 * Every Object which lives(HP>0) gets copied to the AllObjects List If an
	 * Object is dead, it gets removed from the Unit/Building/Defensive list
	 * 
	 * The PossibleTargetList gets cleared
	 * 
	 * Round finished.
	 */
	public void round() {
		for (Unit U : Units) {
			U.moveProv();

		}
		for (Unit U : Units) {
			U.move();

		}

		for (Unit U : Units) {
			U.attack();
		}

		LinkedList<GamePlayObject> saveAllObjects = new LinkedList<GamePlayObject>();

		for (GamePlayObject e : AllObjects) {

			if (e.getHealthPoints() > 0) {

				saveAllObjects.add(e);
			} else {

				Defensives.remove(e);
				Units.remove(e);
				

			}

		}
		AllObjects.clear();
		for (GamePlayObject e : saveAllObjects) {
			AllObjects.add(e);
		}

		saveAllObjects.clear();

		for (Defensive D : Defensives) {
			D.clearTargetList();
		}

	}

}
