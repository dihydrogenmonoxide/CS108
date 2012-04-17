package server.GamePlayObjects;

import server.Server;
import server.exceptions.GameEndedException;
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
		try
		{
			int a=0,b=0;
		GamePlayObjectManager Manager= new GamePlayObjectManager(s, 10);
		for(Player p:s.getPlayers())
		{	
			p.addMoney(100000000);
			p.addPopulation(500000);
			
			for(int x=0;x<10000;x=x+1)
			{
				
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
				new Tank(new Coordinates((int)(350000*Math.random()+450000),(int)(200000*Math.random()+100000)), p, Manager);
				
					}
				catch(GameObjectBuildException e){
					System.out.println(e.getMessage()+ " at "+ a+"/"+b);
				}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new Jet(new Coordinates(a,b),p, Manager);
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new Bomber(new Coordinates(a,b),p, Manager);
					}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new Flak(new Coordinates(a,b),p, Manager);
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new ATT(new Coordinates(a,b),p, Manager);
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new Bank(new Coordinates(a,b),p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
					try{
						a=(int)(350000*Math.random()+450000);
						b=(int)(350000*Math.random()+100000);
						new Reproductioncenter(new Coordinates(a,b),p, Manager);
						
							}
						catch(GameObjectBuildException e){
							System.out.println(e.getMessage()+ " at "+ a+"/"+b);
						}
					
				
					
					
				}
				
			}
		
for(int i=0; i<100;i++)
{	
	int k=0;
	int c=0;
	for(GamePlayObject O: Manager.getObjectList())
	{
		k++;
		if(O instanceof Bomber)c++;
	}
	System.out.println("Round "+i+" :" +k+" Objects");
	System.out.println("Round "+i+" :" +c+" Bombers");
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

	}
catch(GameEndedException e)
{
	System.out.println(e.getMessage()+" Winner is"+e.getWinner().getNick()+" with" +e.getWinner().getMoney()+" Money");
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
