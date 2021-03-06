
package server.GamePlayObjects;

import java.awt.List;
import java.util.LinkedList;

import server.exceptions.GameEndedException;
import server.players.Player;
import server.server.Server;
import shared.Log;
import shared.game.Coordinates;

public class GamePlayObjectManager {

	private LinkedList<GamePlayObject> AllObjects;
	private LinkedList<Defensive> Defensives;
	private LinkedList<Unit> Units;
	private int maxid;
	private Server Server;
	private int maxRounds;

	
	
	public GamePlayObjectManager(Server server) {
		this.AllObjects = new LinkedList<GamePlayObject>();
		this.Defensives = new LinkedList<Defensive>();
		
		this.Units = new LinkedList<Unit>();
		this.maxid=1000000;
		this.Server=server;
		this.maxRounds=100;
	}
	public GamePlayObjectManager(Server server, int maxRounds) {
		this.AllObjects = new LinkedList<GamePlayObject>();
		this.Defensives = new LinkedList<Defensive>();
		
		this.Units = new LinkedList<Unit>();
		this.maxid=1000000;
		this.Server=server;
		this.maxRounds=maxRounds;
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
			Log.DebugLog("Kein Defensive in die Methode GamePlayObjectManager.removeDefensive(Defensive O) gegeben");
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
			Log.DebugLog("Keine Unit in die Methode GamePlayObjectManager.addUnit(Unit u) gegeben");
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
	 * Deletes all Objects of a Player
	 * @param Player p
	 */
	
	public void deleteAllObjectsOfPlayer(Player p){
		LinkedList<GamePlayObject> toDelete=getPlayersObjectList(p);
		for(GamePlayObject O:toDelete)
		{
				Defensives.remove(O);
				Units.remove(O);
				AllObjects.remove(O);
			
		}
		
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
			Log.DebugLog("Kein Defensive in die Methode GamePlayObjectManager.removeDefensive(Defensive O) gegeben");
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
			Log.DebugLog("Keine Unit in die Methode GamePlayObjectManager.removeUnit(Unit u) gegeben");
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
	 * 
	 * If Maxrounds <=0 or only one Player is Playing, the Game is ended.
	 */
	public void round() throws GameEndedException{
		LinkedList<Player> PlayerHelpList1=new LinkedList<Player>(Server.getPlayers());
		for(Player p:Server.getPlayers())
		{
			if(p.getPopulation()<=0)
			{
				deleteAllObjectsOfPlayer(p);
				p.removeMoney(p.getMoney());
				Server.suspendPlayer(p);
				PlayerHelpList1.remove(p);
				
			}
			
		}
		if(this.maxRounds<=0 || PlayerHelpList1.size()<=1)
		{
			long maxMoney=0;
			Player winner=null;
			for(Player p:Server.getPlayers())
				{
				if(p.getMoney()>maxMoney && p.getPopulation()>0)
				{
					maxMoney=p.getMoney();
					winner=p;
					
				}
				
				}
			throw new GameEndedException("Game Ended",winner);
		}
		else
		{
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

		
		
		
		
		
		
		for (Unit U : Units) {
			U.moveProv();

		}
		for (Unit U : Units) {
			U.move();

		}

		for (Unit U : Units) {
			U.attack();
		}
		for (Defensive D : Defensives) {
			D.clearTargetList();
		}


		this.maxRounds--;

		}
		}

}
