package server.GamePlayObjects;

import shared.*;

public class Test {
	
	public static void main(String[] args){
		Tank Tank1= new Tank(new Coordinates(5,5), new User("Lucius"));
		Tank Tank2= new Tank(new Coordinates(10,5), new User("Frank"));
		Tank Tank3= new Tank(new Coordinates(100,5), new User("Fabio"));
		int counter =0;
		while(counter < 100 &&(Tank1.getHealthPoints()>0 && Tank1.getHealthPoints()>0 && Tank1.getHealthPoints()>0))
		{
			Tank1.attack(Tank2);
			Tank1.attack(Tank3);
			
			Tank2.attack(Tank1);
			Tank2.attack(Tank3);
			
			Tank3.attack(Tank1);
			Tank3.attack(Tank2);
			
			System.out.println("Tank 1 has "+Tank1.getHealthPoints()+" HealthPoints");
			System.out.println("Tank 2 has "+Tank2.getHealthPoints()+" HealthPoints");
			System.out.println("Tank 3 has "+Tank3.getHealthPoints()+" HealthPoints");
			counter++;
		}
		
		
		
		
	}

}
