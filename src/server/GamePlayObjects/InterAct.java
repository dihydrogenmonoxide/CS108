package server.GamePlayObjects;

/**
 * Interface of all Objects of the Game which can attack other Objects.
 * @author lucius
 *
 */
public interface InterAct {
	
	//Returns the Range of an Object which can InterAct(=Attack another)
	int getRange();
	
	//Damages the target which is another GamePlayObject owned by an enemy.
	void attack(GamePlayObject target);
	
	//Checks if an Object can be attacked.
	boolean isInRange(GamePlayObject target);
	
	//Checks if the targeted Object can be Attacked( A Bomber cannot attack a Jet)
	boolean isAttackableObject(GamePlayObject target);
	
	

}
