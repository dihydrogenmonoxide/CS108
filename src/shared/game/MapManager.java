package shared.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MapManager
{
	//TODO test "is inside" and redefine the areas
	private final static Color polyLineColor = new Color(255, 0, 0);
	private final static Color polyFillColor = new Color(0, 0, 0);
	private final static Color highlightLineColor = new Color(0, 255, 0);
	private final static Color highlightFillColor = new Color(0, 0, 125);
	private final static int numSteps = 10;
	private final static int stepSize = 1000;


	private final static Polygon[] fields =
		{createField1(),
		 createField2(),
		 createField3(),
		 createField4(),
		 createField5()};
	
	
	private MapManager()
	{
		
	}
	
	/**
	 * Adds the array to the polygon
	 * @param polygon the polygon
	 * @param field the array
	 */
	private static void addToPolygon(Polygon polygon, int[][] field)
	{
		for(int i = 0; i != field.length; i++)
			polygon.addPoint(field[i][0] - 450000, field[i][1] - 100000);
	}
	
	/**
	 * Adds the array to the polygon, but reverses the order	
	 * @param polygon the polygon
	 * @param field the array
	 */
	private static void addToPolygonReversed(Polygon polygon, int[][] field)
	{
		for(int i = field.length-1; i != -1; i--)
			polygon.addPoint(field[i][0] - 450000, field[i][1] - 100000);
	}
	
	/**
	 * Creates a polygon for field 1
	 * @return the polygon
	 */
	private static Polygon createField1()
	{
		Polygon returnValue = new Polygon();
		addToPolygon(returnValue, Map.f1);
		addToPolygon(returnValue, Map.s1n2);
		addToPolygon(returnValue, Map.s1n2n3n4);
		addToPolygon(returnValue, Map.s1n3);
		return returnValue;
	}
	
	/**
	 * Creates a polygon for field 2
	 * @return the polygon
	 */
	private static Polygon createField2()
	{
		Polygon returnValue = new Polygon();
		addToPolygon(returnValue, Map.s1n2);
		addToPolygon(returnValue, Map.s1n2n3n4);
		addToPolygon(returnValue, Map.s2n4);
		addToPolygon(returnValue, Map.f2);
		return returnValue;
	}
	
	/**
	 * Creates a polygon for field 3
	 * @return the polygon
	 */
	private static Polygon createField3()
	{
		Polygon returnValue = new Polygon();
		addToPolygon(returnValue, Map.s3n5);
		addToPolygon(returnValue, Map.s3n4n5);
		addToPolygon(returnValue, Map.s1n2n3n4);
		addToPolygon(returnValue, Map.s1n3);
		addToPolygon(returnValue, Map.f3);
		return returnValue;
	}
	
	/**
	 * Creates a polygon for field 4
	 * @return the polygon
	 */
	private static Polygon createField4()
	{
		Polygon returnValue = new Polygon();
		addToPolygon(returnValue, Map.s3n4n5);
		addToPolygon(returnValue, Map.s1n2n3n4);
		addToPolygon(returnValue, Map.s2n4);
		addToPolygon(returnValue, Map.f4);
		addToPolygon(returnValue, Map.s4n5);
		return returnValue;
	}
	
	/**
	 * Creates a polygon for field 5
	 * @return the polygon
	 */
	private static Polygon createField5()
	{
		Polygon returnValue = new Polygon();
		addToPolygon(returnValue, Map.s3n5);
		addToPolygon(returnValue, Map.s3n4n5);
		addToPolygonReversed(returnValue, Map.s4n5);
		addToPolygon(returnValue, Map.f5);
		return returnValue;
	}
	
	
	/**
	 * Renders the Map with a given x-size
	 * @param fieldNum the field to highlight (enter 0 to highlight none)
	 * @param sizeX the y size in pixels
	 * @return the map
	 */
	public static BufferedImage renderMap(int fieldNum, int sizeX)
	{
		BufferedImage returnValue = new BufferedImage(sizeX, sizeX*4/7, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = returnValue.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		//Anti aliasing makes the rendering slow tho
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform scaleMatrix = new AffineTransform();
		double scale = sizeX/350000.0;
		scaleMatrix.scale(scale, scale);
		
		for(int i = 0; i != fields.length; i++)
		{
			if(i == fieldNum-1)
				continue;
			drawPolygon( g, scaleMatrix, fields[i], polyLineColor, polyFillColor, sizeX);
			
		}
		
		if(fieldNum >= 1 && fieldNum <=5)
		{
			drawPolygon( g, scaleMatrix, fields[fieldNum-1], highlightLineColor, highlightFillColor, sizeX);
		}
		return returnValue;
	}

	/**
	 * Draws & fills the polygon
	 * @param g the graphics
	 * @param scaleMatrix the scale matrix
	 * @param polygon the polygon you want to draw
	 * @param lineColor the line color
	 * @param fillColor the fill color
	 */
	private static void drawPolygon(Graphics2D g, AffineTransform scaleMatrix, Polygon polygon, Color lineColor, Color fillColor, int sizeX)
	{
		BufferedImage temporaryImage = new BufferedImage(sizeX, sizeX*4/7, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = temporaryImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setTransform(scaleMatrix);
		g2.setColor(fillColor);
		g2.fillPolygon(polygon);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
		for(int i = 0; i != numSteps; i++)
		{
			g2.setColor(makeColor(lineColor, fillColor, i));
			g2.setStroke(new BasicStroke((numSteps-i)*stepSize));
			g2.drawPolygon(polygon);
		}
		
		g.drawImage(temporaryImage, null , 0, 0);
	}
	
	/**
	 * Calculates the color for the smooth "line" around the map areas
	 * @param lineColor the line color
	 * @param fillColor the fill color
	 * @param step the current step
	 * @return the color
	 */
	private static Color makeColor(Color lineColor, Color fillColor, int step)
	{
		float red = ((float) lineColor.getRed())/numSteps*step + ((float) fillColor.getRed())/numSteps*(numSteps-step);
		float green = ((float) lineColor.getGreen())/numSteps*step + ((float) fillColor.getGreen())/numSteps*(numSteps-step);
		float blue = ((float) lineColor.getBlue())/numSteps*step+ ((float) fillColor.getBlue())/numSteps*(numSteps-step);
		return new Color((int) red, (int) green, (int) blue);
	}
	
	/**
	 * Checks a point whether it is inside a certain area or not
	 * @param fieldNumber the fieldNumber
	 * @param xCoordinate the x Coordinate (450'000 - 800'000)
	 * @param yCoordinate the y Coordinate (100'000 - 300'000)
	 * @return whether it is inside the field or not
	 */
	public static boolean isInside(int fieldNumber, int xCoordinate, int yCoordinate)
	{
		float x = xCoordinate - 450000;
		float y = yCoordinate - 100000;
		for(Polygon p : fields)
		{
			if(p.contains(x, y))
				return true;
		}
		return false;
	}
	
	/**
	 * returns the fieldID of a certain point (or 0 if it isn't inside any field)
	 * @param xCoordinate the x Coordinate (450'000 - 800'000)
	 * @param yCoordinate the y Coordinate (100'000 - 300'000)
	 * @return the fieldID
	 */
	public static int getFieldNumber(int xCoordinate, int yCoordinate)
	{
		float x = xCoordinate - 450000;
		float y = yCoordinate - 100000;
		for(int i = 0; i != fields.length; i++)
		{
			
			if(fields[i].contains(x, y))
				return i;
		}
		return 0;
	}
	
	
}

class Map
{
	public static final int [][] f1 =
		{{630925,241342},
		 {632505,244405},
		 {615387,256419},
		 {620917,264428},
		 {599586,289870},
		 {589578,287985},
		 {587735,289870},
		 {573513,283510},
		 {573513,286101},
		 {546388,295524},
		 {542964,294817},
		 {539541,296230},
		 {525846,276442},
		 {522159,277385},
		 {523476,269846},
		 {516629,267726},
		 {520842,254299},
		 {515312,246525},
		 {520316,242991},
		 {513732,239929},
		 {492136,238280},
		 {478442,241578},
		 {471068,248881},
		 {475282,257832},
		 {450790,268433},
		 {453160,263486},
		 {451843,261130},
		 {467644,244640},
		 {460534,240164},
		 {467118,227208},
		 {464747,223674}};
	
	public static final int [][] s1n2 = 
		{{490293,203415},
		 {555605,218021},
		 {574303,226737},
		 {602219,231919}};
	
	public static final int [][] s1n2n3n4 =
		{{622761,218963}};
	
	public static final int [][] s1n3 =
		{{637509,238987}};
	
	public static final int [][] s2n4 =
		{{606170,200117},
		 {579307,177738},
		 {567456,136277}};
	
	public static final int [][] s4n5 =
		{{631452,124499},
		 {644356,163839},
		 {646726,178209},
		 {641723,190694}};
	
	public static final int [][] s3n5 =
		{{719149,175147},
		 {709142,204829}};
	
	public static final int [][] s3n4n5 =
		{{662528,211660}};
	
	public static final int [][] f2 =
		{{545598,138398},
		 {544544,134157},
		 {531640,133922},
		 {532430,137220},
		 {523476,147349},
		 {533747,145936},
		 {536644,148292},
		 {520316,165017},
		 {521896,167137},
		 {488976,189752}};
	
	public static final int [][] f3 =
		{{644620,237573},
		 {643039,256654},
		 {664108,272438},
		 {666741,270553},
		 {674379,275029},
		 {670428,281861},
		 {678066,286572},
		 {678592,290812},
		 {680699,290812},
		 {682279,294346},
		 {679646,298115},
		 {688073,298586},
		 {693077,291519},
		 {685966,283510},
		 {706245,231213},
		 {713355,229799},
		 {717042,234275},
		 {720729,229328},
		 {721519,243462},
		 {739691,251001},
		 {742588,244876},
		 {745221,246289},
		 {755229,241813},
		 {756809,242756},
		 {759179,241813},
		 {768660,257126},
		 {775507,253121},
		 {771030,244405},
		 {774717,238751},
		 {764710,234982},
		 {765763,226030},
		 {779194,215901},
		 {781565,221554},
		 {788148,224852},
		 {788939,222968},
		 {796576,225088},
		 {798683,217550},
		 {791045,210718},
		 {796839,185276},
		 {789729,179858},
		 {783935,184805},
		 {784725,186454},
		 {779458,187161},
		 {778668,193050},
		 {751015,186454},
		 {750225,178680}};
	
	public static final int [][] f4 =
		{{572460,129681},
		 {568246,129917},
		 {584048,125677},
		 {584048,128504}};
	
	public static final int [][] f5 =
		{{640933,124028},
		 {642776,117196},
		 {638036,115547},
		 {649887,102591},
		 {653310,103297},
		 {654627,108009},
		 {656471,102826},
		 {662264,107538},
		 {660158,109658},
		 {662264,111307},
		 {662264,113427},
		 {666478,115076},
		 {664635,110836},
		 {673062,112485},
		 {674905,117903},
		 {718359,132037},
		 {724153,131802},
		 {732317,137691}};
}