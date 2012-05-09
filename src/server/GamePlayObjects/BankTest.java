package server.GamePlayObjects;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import server.exceptions.GameObjectBuildException;
import server.players.Player;
import server.server.Server;
import shared.game.Coordinates;

public class BankTest {

	Server Server;
	Player Player;
	GamePlayObjectManager Manager;
	@Before
	public void setUp() throws Exception {
		this.Server = null; // To do: Testserver.bla();
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
		Bank Bank1,Bank2,Bank3,Bank4;
		
		try {
			 Bank1=new Bank(new Coordinates(0,0),Player, Manager);//Falsche Position, mit Geld
		
		} catch (GameObjectBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			assert Manager.getObjectList().isEmpty(): "Falsche Position, mit Geld, Check"; 
		}
		
		
		try {
			 Bank2=new Bank(new Coordinates(0,0),Player, Manager);//Richtige Position, mit Geld
		} catch (GameObjectBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Player.removeMoney(Player.getMoney());//Kohle Weg
		try {
			Bank Bank1=new Bank(new Coordinates(0,0),Player, Manager);//Falsche Position, ohne Geld
		} catch (GameObjectBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Bank Bank2=new Bank(new Coordinates(0,0),Player, Manager);//Richtige Position, ohne Geld
		} catch (GameObjectBuildException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
