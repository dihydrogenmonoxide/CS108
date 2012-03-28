package server.GamePlayObjects;

import server.exceptions.GameObjectBuildException;
import shared.User;
import shared.game.Coordinates;

public class TankTest{
	
	public static void main(String[] args){
		
		GamePlayObjectManager Man= new GamePlayObjectManager();
		try{
		Tank Tank1= new Tank(new Coordinates(657400,230000), new User("Lucius"), Man);
		
		
		Tank Tank2= new Tank(new Coordinates(657400,231000), new User("Luciu2s"), Man);
		Tank2.setTarget(new Coordinates(350000, 260000));
		
		Tank Tank3= new Tank(new Coordinates(734556,240000), new User("Ales"), Man);
		Tank3.setTarget(new Coordinates(350000, 250000));
		
		Tank1.setTarget(new Coordinates(350000, 261000));
		for(int i=0; i<100;i++)
		{
		Man.round();
		System.out.println("Round: "+String.valueOf(i));
		for (GamePlayObject e:Man.getObjectList())
		{
			System.out.println(e.getOwner().getUserName()+" lives with HP:"+e.getHealthPoints());
		}
		}
		}
		
		catch(GameObjectBuildException e)
		{
			System.out.println("BuildException");
		}
		
		
	}
	
	
}