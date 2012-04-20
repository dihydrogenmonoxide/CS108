package client.game;

import client.data.GameObject;
import client.data.RunningGame;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.MapManager;

public class GameFieldPanel extends JPanel implements MouseListener
{

    /**
     * the width the map will be rendered
     */
    public static int MAP_WIDTH = 500;
    /**
     * the Height of the Map, is set after rendering.
     */
    //TODO find relation MAP_Width to MAP_height
    
    public static int MAP_HEIGHT=(MAP_WIDTH*4/7);
    /**
     * Buffered image to Paint Map
     */
    private BufferedImage backgroundMap;
    /**
     * Image for DoubleBufferedImage
     */
    private Image dbImage;
    private Graphics dbg;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private GameButtonsPanel but;
    private Clientsocket socket;
    private Image bil;
    //if the background is rendered already
    boolean isRendered = false;
    /**position to handle the chosen Object from GameObjectList*/
    private int x1,y1;
    /**radius to move object*/
    private int radius;
    /**boolean to see if place is free where you want to add Object*/
    private boolean frei= true;
    /**angel for showing the movingRange*/
    private double angel=0;
    /**boolean to take object*/
    private boolean pressed=false;
    /**boolean to draw line*/
    private boolean lineTrue=false;
    /**List which holds the lines*/
    List<Linie> line = new ArrayList<Linie>();
    
    
    public GameFieldPanel(Clientsocket s)
    {
        this.socket = s;

        this.setPreferredSize(this.getMaximumSize());



        //TODO decide which field to highlight and which are inactive.


        this.setBackground(Color.blue);
         

        this.addMouseListener(this);

        //static framerate:
        Timer timer = new Timer();
        
    	timer.scheduleAtFixedRate(new TimerTask()
    	{

    		public void run()
    		{
    			angel++;
    			repaint();
    		}
    	}, 0, 100);
    }

	
    	
    
    public void paintComponent(Graphics g)
    {
        boolean logRedraw = false;
        try
        {
            //-- paint background
            g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
            if (logRedraw)
            {
                Log.DebugLog("GameField: redrawing now!");
                Log.DebugLog("map width =" + MAP_WIDTH + " height=" + MAP_HEIGHT);
            }
            //-- paint objects
            Collection<GameObject> c = RunningGame.getObjects().values();
            Iterator<GameObject> objIter = c.iterator();
            while (objIter.hasNext())
            {
                GameObject obj = objIter.next();
                BufferedImage objImg = obj.getImg();
                Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                if (logRedraw)
                {
                    Log.DebugLog("GameField: x=" + obj.getLocation().getX() + " y=" + obj.getLocation().getY() + "berechnet: pixelX=" + pixelCoords.width + " pixelY=" + pixelCoords.height);
                }
                //-- draw image

                //-- width of the image
                int imageDim = 20;


                if (objImg != null)
                {

                    g.drawImage(objImg, pixelCoords.width - imageDim / 2, pixelCoords.height - imageDim / 2, 20, 20, null);
                }
            }
            if(pressed){
            	double n=60;
      		  	double fact=3;
            	double f=360*fact;
            	double a=n/f;
            	Graphics2D gd = (Graphics2D) g;
      		  	gd.setStroke(new BasicStroke(3));
      		  	double farbe=n;
      		  	gd.setColor(new Color(0,255,0,(int)farbe));

      		  	for(int i=0; i<(360*fact);i++){
      		  		double rad= Math.toRadians(angel);
      		  		int x = (int) (Math.cos (rad) * radius);
      		  		int y = (int) (Math.sin (rad) * radius);
      		  		gd.drawLine (x1, y1, x + x1, y + y1);
      		  		gd.setColor(new Color(0,255,0,(int)(farbe)));
      		  		angel-=1/fact;
      		  		farbe-=a;
      		  	}

            }
            for (Linie l : line)
            {
            	g.setColor(Color.orange);
                g.drawLine(l.xs, l.ys,l.xe,l.ye);
            }
            
            

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g)
    {
        //XXX not exactly nice way to do it
        if (!isRendered)
        {
            MAP_WIDTH = getWidth();
            backgroundMap = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_WIDTH);
            MAP_HEIGHT = backgroundMap.getHeight();
            isRendered = true;
        }
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        paintComponent(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }

    /**
     * sends a spawn request to the server.
     *
     * @param c   the Coordinates where to spawn
     * @param obj the object to spawn
     */
    public void spawnObject(int x, int y, Protocol obj)
    {
        Log.InformationLog("Trying to spawn Object: " + obj.str() + ", x=" + x + ", y=" + y + ", m_width" + MAP_WIDTH + ", m_heigth" + MAP_HEIGHT);
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, new Dimension(MAP_WIDTH, MAP_HEIGHT)));
    }
    int count =0;
    int xP;
    int yP;
    public void mousePressed(MouseEvent e)
    {
    	xP= e.getX();
        yP= e.getY();
    	lineTrue=false;
    	if(count==0){
    		target(xP,yP);
    		add(xP,yP);
    	}
    	else{
    		if( (x1+radius>=xP) && (x1-radius<=xP) && (y1+radius>=yP) && (y1-radius<=yP)){
    			//TODO update Object list with new Points
    			//TODO check if line from object is already set
    			Linie li = new Linie(x1,y1,xP,yP);
    			line.add(li);
        	}
    		else{
    			pressed=false;
    		}
    		count=0;
		}

    	
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {

    	
    }
    
    public void target(int x , int y){
    	Collection<GameObject> c = RunningGame.getObjects().values();
        Iterator<GameObject> objIter = c.iterator();
        GameObject obj = null;
        while(objIter.hasNext()){
        	obj = objIter.next();
        	Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
        	x1= pixelCoords.width - 20 / 2;
        	y1= pixelCoords.height - 20 / 2;
            if(x > x1 && x < x1+20 && y > y1 && y < y1+20){
    			radius= obj.movingRange();
    			pressed=true;
        		frei = false;
        		count++;
            	x1=x1+10;
            	y1=y1+10;
        		return;
        	}
        	else{
        		pressed=false;
        		frei = true;
        	}      	

        }

    }
    
    public void add(int x, int y){
    	if(frei){
            
        	
        	Log.DebugLog("User clicked on the map at (" + x + "," + y + ") with the button choice: " + but.choice.toString());
            Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(x, y, new Dimension(MAP_WIDTH, MAP_HEIGHT)));
            Log.DebugLog("sending request to create:" + but.choice);
            switch (but.choice)
            {
                case TANK:
                    spawnObject(x, y, Protocol.OBJECT_TANK);
                    break;
                case FIGHTER:
                    spawnObject(x, y, Protocol.OBJECT_FIGHTER_JET);
                    break;
                case BOMBER:
                    spawnObject(x, y, Protocol.OBJECT_BOMBER);
                    break;
                case ANTIAIR:
                    spawnObject(x, y, Protocol.OBJECT_STATIONARY_ANTI_AIR);
                    break;
                case BUNKER:
                    spawnObject(x, y, Protocol.OBJECT_STATIONARY_ANTI_TANK);
                    break;
                case REPRO:
                    spawnObject(x, y, Protocol.OBJECT_REPRODUCTION_CENTER);
                    break;
                case BANK:
                    spawnObject(x, y, Protocol.OBJECT_BANK);
                    break;
                case NONE:
                default:
            }
        count=0;
        }
    }
    
    class Linie{
    	int xs,ys,xe,ye;
    	public Linie(int xstart, int ystart, int xend, int yend){
    		xs=xstart;
    		ys =ystart;
    		xe=xend;
    		ye=yend;
    	}
    	
    }
}
