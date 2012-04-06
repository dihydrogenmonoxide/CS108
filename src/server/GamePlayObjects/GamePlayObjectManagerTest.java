package server.GamePlayObjects;

import jUnit.Before;
import shared.User;
import shared.game.Coordinates;

public class GamePlayObjectManagerTest {
	GamePlayObjectManager Man= new GamePlayObjectManager();
	Tank Tank1= new Tank(new Coordinates(0,0), new User("Lucius"), Man);
	Tank Tank2= new Tank(new Coordinates(434556,120000), new User("Lucius"), Man);
	
	Tank Tank3= new Tank(new Coordinates(734556,240000), new User("Ales"), Man);
	
	Tank Tank4= new Tank(new Coordinates(334556,198754), new User("Lucus"), Man);
	
	Tank Tank5= new Tank(new Coordinates(534556,185498), new User("Lius"), Man);
	
	Tank Tank6= new Tank(new Coordinates(434777,128976), new User("cius"), Man);
	
	Tank Tank7= new Tank(new Coordinates(634556,0), new User("Ls"), Man);
	
	Tank Tank8= new Tank(new Coordinates(900000,400000), new User("Luciasdfs"), Man);
	
	Tank Tank9= new Tank(new Coordinates(200000,230000), new User("Luciuds"), Man);
	
	Tank Tank10= new Tank(new Coordinates(470556,140000), new User("Luciufggas"), Man);
	
	@Before
	public void setUp() throws Exception {
		System.out.println(Man.toString());
	}

	@Test
	public void testAddDefensiveDefensive() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddUnit() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveDefensiveDefensive() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveUnit() {
		fail("Not yet implemented");
	}

	@Test
	public void testRound() {
		fail("Not yet implemented");
	}

}
