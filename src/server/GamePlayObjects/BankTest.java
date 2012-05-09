package server.GamePlayObjects;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import server.server.Server;
import shared.game.Coordinates;
import shared.game.GameSettings;
import test.gamePlayObjects.TextServerStaticField;

public class BankTest {
	
	/**
	 * Set up a UnitTest for the class bank:
	 * To instance a Bank, you need a Server, a Player and a GamePlayObjectManager
	 * so i instance a fakeServer, a Player and a GamePlayObjectManager
	 */

	Server Server;
	Player Player;
	GamePlayObjectManager Manager;
	@Before
	/**
	 * Set up a UnitTest for the class bank:
	 * To instance a Bank, you need a Server, a Player and a GamePlayObjectManager
	 * so i instance a fakeServer, a Player and a GamePlayObjectManager
	 */
	public void setUp() throws Exception {
		this.Server = TextServerStaticField.startTestServer();
		this.Player=null;
		for (Player p : Server.getPlayers()) {
			Player = p;
		}
		this.Manager= new GamePlayObjectManager(Server);
		Player.addMoney(14000);
		Player.addPopulation(10);

	}

	@Test
	public void test() {
		Bank Bank1=null;
		
		test_Constructor();
		
		for(GamePlayObject O:Manager.getPlayersObjectList(Player))
				{
					if(O instanceof Bank)
						{
						Bank1=(Bank)O;
						}
			
				}
		
		assertTrue(Bank1!=null);
		
		//Test if the Bank can get Bombed
		test_damage(Bank1);
		
		//Test if the Bank adds Money
		long before=Bank1.getOwner().getMoney();
		Bank1.attack();
		assertTrue(Bank1.getOwner().getMoney()==before+GameSettings.Bank.attackPoints);
		
		
		
		
		
		
	}

	private void test_damage(Bank Bank1) {
		//Now Bank1 is generated, Testing its Methods:
		//Test if the Bank can be attacked:
		int before=Bank1.getHealthPoints();
		assertTrue(Bank1.getHealthPoints()==GameSettings.Bank.healthPoints);
		int damage=100;
		Bank1.damage(damage);
		assertTrue(before-damage==Bank1.getHealthPoints());
	}

	private void test_Constructor() {
		Bank Bank1;
		Bank Bank2;
		Bank Bank3;
		try {
			 Bank1=new Bank(new Coordinates(0,0),Player, Manager);//Falsche Position, mit Geld
		
		} catch (GameObjectBuildException e) {
			assertTrue(true);
			
		}
		
			
		
		
		
		
		
		
		Player.removeMoney(Player.getMoney());//Kohle Weg
		try {
			 Bank3=new Bank(new Coordinates(0,0),Player, Manager);//Falsche Position, ohne Geld
		} catch (GameObjectBuildException e) {
			assertTrue(true);
			
		}
		
			
		
		try {
			Bank3=new Bank(new Coordinates(523871,238019),Player, Manager);//Richtige Position, ohne Geld
		} catch (GameObjectBuildException e) {
			assertTrue(true);
			
		}
		
			Player.addMoney(10000);
			
			try {
				 Bank2=new Bank(new Coordinates(523871,238019),Player, Manager);//Richtige Position, mit Geld
			} catch (GameObjectBuildException e) {
				e.printStackTrace();
				assertTrue(false); 
				
			}
			
				
		
	}

}
