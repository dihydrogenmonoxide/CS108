package server.GamePlayObjects;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.*;
import shared.game.Coordinates;

public interface GamePlayObject {
	
	
	//Loads the Image and displays it on the client GUI
		void draw();
		
		int getId();
		
		//Returns the Coordinates where the Object is.
		Coordinates getPos();
		
		//Returns the Owner of the Object.
		Player getOwner();
		
		void setTarget(Coordinates target);
		
		//Asks the Server if the Object can be build here.
		//If true, the Object gets build.
		void build() throws GameObjectBuildException;
		
		//Loads the Animation of its destruction and deletes all References
		//that the GarbageCollector kills the Object.
		void destruct();
		
		//Returns the Health Points of the Object
		int getHealthPoints();
		
		//Hits the Object with Damage.
		void damage(int damPoints);
		
		void setId(int id);
		
		int getRange();
		
		//Damages the target which is another GamePlayObject owned by an enemy.
		void attack(GamePlayObject target);
		
		//Checks if an Object can be attacked.
		boolean isInRange(GamePlayObject target);
		
		//Checks if the targeted Object can be Attacked( A Bomber cannot attack a Jet)
		boolean isAttackableObject(GamePlayObject target);
		
		
		

}
