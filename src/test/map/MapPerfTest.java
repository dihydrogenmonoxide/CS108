package test.map;

import shared.game.MapManager;

public class MapPerfTest
{
	private final static int SIZEX = 700;
	private static final int MAXLOOP = 100;
	
	public static void main(String[] args) 
	{
		long t = System.currentTimeMillis();
		for(int i = 0; i != MAXLOOP; i++)
		{
			MapManager.renderMap(i%4+1, SIZEX);
		}
		System.out.println("Average renderingtime at a x-res of "+SIZEX+ ": "+(((double)System.currentTimeMillis()-t)/MAXLOOP)+"MS");
	}
}
