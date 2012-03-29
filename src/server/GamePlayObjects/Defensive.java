package server.GamePlayObjects;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D.Double;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.User;
import shared.game.Coordinates;
public class Defensive extends Unit implements GamePlayObject {
	
	
	Defensive()
	{}
	boolean isInRange(GamePlayObject target){return false;}
	
	void addToTargets(Unit U){}
	void checkLine(Coordinates Target, GamePlayObject O){}
	void moveProv(){}
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
	public void setId(int id){}
	public void clearTargetList(){}
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Coordinates getPos() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Player getOwner() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void build() throws GameObjectBuildException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destruct() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getHealthPoints() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void damage(int damPoints) {
		// TODO Auto-generated method stub
		
	}
	
	

}
