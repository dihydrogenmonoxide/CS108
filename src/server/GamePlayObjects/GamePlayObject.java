package server.GamePlayObjects;

import shared.*;

public interface GamePlayObject {
	//Loads the Image and displays it on the client GUI
		void draw();
		
		
		
		//Returns the Coordinates where the Object is.
		Coordinates getPos();
		
		//Returns the Owner of the Object.
		User getOwner();
		
		//Asks the Server if the Object can be build here.
		//If true, the Object gets build.
		void build();
		
		//Loads the Animation of its destruction and deletes all References
		//that the GarbageCollector kills the Object.
		void destruct();
		
		//Returns the Health Points of the Object
		int getHealthPoints();
		
		//Hits the Object with Damage.
		void damage(int damPoints);
		
		
		
		
		

}
