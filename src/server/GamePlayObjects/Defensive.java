package server.GamePlayObjects;


import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.User;
import shared.game.Coordinates;
public interface Defensive extends GamePlayObject{
	
	

	boolean isInRange(GamePlayObject target);
	
	void addToTargets(Unit U);
	void checkLine(Coordinates Target, GamePlayObject O);
	void moveProv();
	
	public void draw();
	public void setId(int id);
	public void clearTargetList();
	
	public int getId();
	
	public Coordinates getPos();

	public Player getOwner();

	void build() throws GameObjectBuildException;

	public void destruct();

	public int getHealthPoints();

	public void damage(int damPoints);
	
	

}
