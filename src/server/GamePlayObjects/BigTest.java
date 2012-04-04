package server.GamePlayObjects;

import server.Server;
import server.exceptions.GameObjectBuildException;
import server.players.Player;
import shared.game.Coordinates;
import test.gamePlayObjects.TestServer;

public class BigTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server s=TestServer.startTestServer();
		GamePlayObjectManager Manager= new GamePlayObjectManager(s);
		for(Player p:s.getPlayers())
		{	
			p.addMoney(100000000);
			p.addPopulation(1000000);
			
			for(int x=450000;x<800000;x=x+10000)
			{
				for (int y=100000;y<300000;y=y+10000)
				{
					try{
				new Tank(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
				
					}
				catch(GameObjectBuildException e){
					System.out.println(e.getMessage()+ " at "+ x+"/"+y);
				}
					
					try{
						new Jet(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
					try{
						new Bomber(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
							
					}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
					try{
						new Flak(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
					try{
						new ATT(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
					try{
						new Bank(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
					try{
						new Reproductioncenter(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ x+"/"+y);
						}
					
				
					
					
				}
				
			}
		}
for(int i=0; i<100;i++)
{	
	int k=0;
	int b=0;
	for(GamePlayObject O: Manager.getObjectList())
	{
		k++;
		if(O instanceof Bomber)b++;
	}
	System.out.println("Round "+i+" :" +k+" Objects");
	System.out.println("Round "+i+" :" +b+" Bombers");
	for(Player P:s.getPlayers())
	{
	System.out.println("Player with id" +P.getID()+ " has "+ P.getMoney()+ " Money and "+P.getPopulation()+" Population" );	
	}
		for(Player p:s.getPlayers())
		{
			for(GamePlayObject o:Manager.getPlayersObjectList(p))
			{
				o.setTarget(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)));
				
			}
			
		}
		Manager.round();
	}

/*for(GamePlayObject O: Manager.getObjectList())
{
	System.out.println( O.getClass().getName() +" with id"+ O.getId()+" of "+O.getOwner().getID()+" has "+O.getHealthPoints()+" HPs");
}
*/
for(Player P:s.getPlayers())
{
System.out.println("Player with id" +P.getID()+ " has "+ P.getMoney()+ " Money and "+P.getPopulation()+" Population" );	
}

}


	
	
}
