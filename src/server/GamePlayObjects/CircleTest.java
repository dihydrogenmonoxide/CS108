package server.GamePlayObjects;

import shared.game.Coordinates;

class CircleTest {
	/**
	 * Returns if a Line(x1/y1) to (x2/y2)
	 *  or Part of it is in The Range of Object at Position (x3/y3) with Range r
	 * @param x1 
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param r
	 * @return Boolean true if is in Range, false if not
	 */
	public static boolean checkLine(int x1, int y1, int x2, int y2, int x3,
			int y3, int r) {
	double sx;
	double sy;
		if(x1==x2)
	{
		sx=x1;
		sy=y3;
	}
		else if(y1==y2)
		{
			sy=y1;
			sx=x3;
		}
		else
		{
		double hm1 = (y2 - y1);
		double hm2 = (x2 - x1);
		double m;
	
		m = hm1 / hm2;
		
		double q = y1 - m * x1;
		double c = y3 + 1 / m * x3;
		double h1 = c - q;
		double h2 = m + 1 / m;
		 sx = h1 / h2;
		sy = m * sx + q;
		}
		Coordinates Position = new Coordinates(x3, y3);
		
		if (x1 < x2) {

			if ((Position.getDistance(new Coordinates(x1, y1)) < r)
					|| (Position.getDistance(new Coordinates(x2, y2)) < r)
					|| (x1 <= sx && sx <= x2)
					&& Position
							.getDistance(new Coordinates((int) sx, (int) sy)) <= r)
				return true;
			else
				return false;
		} else {

			if ((Position.getDistance(new Coordinates(x1, y1)) < r)
					|| (Position.getDistance(new Coordinates(x2, y2)) < r)
					|| (x2 <= sx && sx <= x1)
					&& Position
							.getDistance(new Coordinates((int) sx, (int) sy)) <= r)
				return true;
			else
				return false;
		}

	}

}
