package shared.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameSettings {
	//TODO moving and attack ranges are too big
	
	
	public static class Tank
	{
		final static public int movingRange=10000;	
		final static public int attackRange=1000;
		final static public String[] attackableBy = {"Tank", "Building"};
		final static public int attackPoints=78;
		final static public int healthPoints=1000;
		final static public int ammunation=10;
		final static public int price=7000;
		
	}
	
	public static class Jet
	{
		final static public int movingRange=10000;
		final static public int attackRange=5000;
		final static public String[] attackableBy = {"Tank", "Building"};
		final static public int attackPoints=100;
		final static public int healthPoints=1000;
		final static public int ammunation=10;
		final static public int price=7000;
		
	}
	
	public static class Bomber
	{
		final static public int movingRange=100000;
		final static public int attackRange=5000;
		final static public String[] attackable = {"Tank", "Building"};
		final static public int attackPoints=250;
		final static public int healthPoints=1000;
		final static public int ammunation=5;
		final static public int price=7000;
		
	}
	
	public static class Reproductioncenter
	{
		final static public int movingRange=10000;
		final static public int attackRange=5000;
		final static public String[] attackable = {""};
		final static public double attackPoints=0.0014;//Hier, Reproduktionsrate
		final static public int healthPoints=1000;
		final static public int ammunation=1;
		final static public int price=7000;
		
	}
	
	public static class Flak
	{
		final static public int movingRange=10000;
		final static public int attackRange=5000;
		final static public String[] attackable = {"Flying"};
		final static public int attackPoints=10;
		final static public int healthPoints=1000;
		final static public int ammunation=10;
		final static public int price=7000;
		
	}
	
	public static class ATT
	{
		final static public int movingRange=10000;
		final static public int attackRange=5000;
		final static public String[] attackableBy = {"Tank"};
		final static public int attackPoints=100;
		final static public int healthPoints=1000;
		final static public int ammunation=10;
		final static public int price=7000;
		
	}
	
	public static class Bank
	{
		final static public int movingRange=10000;
		final static public int attackRange=5000;
		final static public String[] attackable = {};
		final static public double attackPoints=0.0001;//Hier:GeldRate
		final static public int healthPoints=1000;
		final static public int ammunation=1;
		final static public int price=7000;
		
	}
	
	

}
