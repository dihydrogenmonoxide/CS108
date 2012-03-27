package server.GamePlayObjects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Settings {
	
	public static class Tank
	{
		final static int movingRange=10000;
		final static int attackRange=1000;
		final static String[] attackableBy = {"Tank", "Building"};
		final static int attackPoints=78;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}
	
	public static class Jet
	{
		final static int movingRange=10000;
		final static int attackRange=5000;
		final static String[] attackableBy = {"Tank", "Building"};
		final static int attackPoints=100;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}
	
	public static class Bomber
	{
		final static int movingRange=10000;
		final static int attackRange=5000;
		final static String[] attackable = {"Tank", "Building"};
		final static int attackPoints=100;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}
	
	public static class Reproductioncenter
	{
		final static int movingRange=10000;
		final static int attackRange=5000;
		final static String[] attackable = {"Tank", "Building"};
		final static int attackPoints=100;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}
	
	public static class Flak
	{
		final static int movingRange=10000;
		final static int attackRange=5000;
		final static String[] attackableBy = {"Tank", "Building"};
		final static int attackPoints=100;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}
	
	public static class ATT
	{
		final static int movingRange=10000;
		final static int attackRange=5000;
		final static String[] attackableBy = {"Tank", "Building"};
		final static int attackPoints=100;
		final static int healthPoints=1000;
		final static int ammunation=10;
		final static int price=7000;
		
	}

}
