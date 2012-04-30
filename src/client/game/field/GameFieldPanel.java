package client.game.field;

import client.data.GameObject;
import client.data.RunningGame;
import client.game.InnerGameFrame;
import client.net.Clientsocket;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.MapManager;

public class GameFieldPanel extends JPanel implements MouseListener
{

    /**backgroundmap*/
    BufferedImage backgroundMap;
    /**the width the map will be rendered. */
    public static int MAP_WIDTH = 500;
    /** the Height of the Map, is set after rendering.  */
    public static int MAP_HEIGHT = (MAP_WIDTH * 4 / 7);
    
    /**
     * Image for DoubleBufferedImage
     */
    private Image dbImage;
    private Graphics dbg;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private Clientsocket socket;
    private Image bil;
    /**angel for showing the movingRange*/
    private double angel=0;
    private double ang=4;
    
    ChoseObject dr;
    /**Point of mousePressed*/
    int xP,yP;
    /**JPanel InnerGameFrame to add deleteButton*/
    InnerGameFrame inner;
    /**Gridbagcontraints to add Position of delete Button*/
    GridBagConstraints cl;
    /**Integer to get how many Clicks*/
    static int clickCount=0;
    /**delete Button which appears, when its Possible to click on it (if target is drawn)*/
    JButton delete;

    Graphics2D gd;
    Timer timerslow;
    Timer timerfast;
    
     //-- flag if we should write all the drawing to the log
     boolean logRedraw = false;
     
     /**ArrayList, which holds Lines*/
     static List<Lines> line=new ArrayList<Lines>();
    
        
    public GameFieldPanel(Clientsocket s, InnerGameFrame innerGameFrame, GridBagConstraints c)
    {
    	this.cl=c;
    	this.inner = innerGameFrame;
        this.socket = s;

        this.setPreferredSize(this.getMaximumSize());

        delete = new JButton("delete");
		delete.setOpaque(false);
		cl.gridx=5;
		cl.gridy=2;
		delete.setVisible(false);
		inner.add(delete,cl);
		
		delete.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				RunningGame.deleteObject(dr.obj.getID());
				timerfast.stop();
				timerslow.start();
				dr.pressed=false;
				delete.setVisible(false);
				clickCount=0;
				
			}
		});       
        this.setBackground(Color.blue);
         
        
        this.addMouseListener(this);
        this.setOpaque(false);
        	
        //static framerate:
        timerslow= new Timer(300, new ActionListenerSlow());
        timerfast= new Timer(70,new ActionListerFast());
        timerslow.start();
        
        dr= new ChoseObject(socket,delete, gd, timerslow, timerfast);
    }

	
    	
    
    public void paintComponent(Graphics g)
    {   
        try
        {
            //-- paint background
            g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
            
            //some logging (maybe)
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
                
                //some logging (maybe)
                if (logRedraw)
                {
                    Log.DebugLog("GameField: x=" + obj.getLocation().getX() + " y=" + obj.getLocation().getY() + "berechnet: pixelX=" + pixelCoords.width + " pixelY=" + pixelCoords.height);
                }
                
                //-- draw image
                //-- width of the image
                double imageDim = MAP_WIDTH/50*1.5;


                if (objImg != null)
                {
                        //-- draw a line if object has moved
                       if(obj.hasMoved())
                       {
                           Dimension oldPixelCoords = Coordinates.coordToPixel(obj.getOldLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                           g.setColor(Color.orange);
                           Lines l =new Lines(oldPixelCoords.width,oldPixelCoords.height,pixelCoords.width, pixelCoords.height);
                           line.add(l);
                           for (Lines f:line){
                        	   g.drawLine(f.xs, f.ys, f.xe, f.ye);
                           }
                           if(logRedraw)
                           {
                               Log.DebugLog("drawing line for this object");
                           }
                       }
                       
                       //-- draw image
                       g.drawImage(objImg, pixelCoords.width - (int)imageDim / 2, pixelCoords.height - (int)imageDim / 2,(int) imageDim,(int) imageDim, null);
                    
                }
            }
            if(dr.pressed){
        		double n=255;
        		double a=n/(360/4);
            	gd= (Graphics2D) g;
            
      		  	double farbe=n;
  		  		double rad= Math.toRadians(ang);

  		  		int y1=0;
  		  		int x1=0;
      		  	int y = (int) (Math.cos (rad) * dr.radius);
      		  	int x = (int) (Math.sin (rad) * dr.radius);

      		  	if(clickCount==1){
                	ObjectInfo inf =new ObjectInfo(gd, dr.obj);
      		  	}
      		  	
      		  	/**draws Radar around Object with ObjectRadius*/
      		  	for(int i=0; i<360/4-5;i++){
      		  		Polygon poly= new Polygon();
      		  		double rad1= Math.toRadians(angel);
    		  		y=y1;
    		  		x=x1;
      		  		y1 = (int) (Math.cos(rad1)*dr.radius);
      		
      		  		x1 = (int) (Math.sin (rad1) * dr.radius);
      		  		poly.addPoint(dr.xObject,dr.yObject);
    		  		poly.addPoint(x1+dr.xObject,y1+dr.yObject);
      		  		poly.addPoint(x+dr.xObject,y+dr.yObject);
      		  		gd.setColor(new Color(0,255,0,(int)farbe));
    		  		gd.fillPolygon(poly);

      		  		angel-=4;
      		  		farbe-=a;
      		  	}

            } 

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if(RunningGame.getGamePhase().equals("ANIM")){
        	//TODO is never true
        	System.out.println("Game is in animation phase");
        	//TODO draw somethings in animation phase; probably lines gets smaller
        	new MoveObjects(line);
        }

        
    }

    public void paint(Graphics g)
    {
        //-- drawing background map
        
        //-- determine if we have to render the map (when the size changes or at the start)
        if (backgroundMap == null || backgroundMap.getWidth() != getWidth())
        {
            Log.DebugLog("Map manager: rendered Map");
            //-- render map
            backgroundMap = MapManager.renderMap(RunningGame.getMyFieldId(), this.getWidth());
            MAP_WIDTH = backgroundMap.getWidth();
            MAP_HEIGHT = backgroundMap.getHeight();
        }

        //-- paint background
        g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), Color.BLACK, null);
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        paintComponent(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }

    
    
    
    public void mousePressed(MouseEvent e)
    {
    	xP= e.getX();
        yP= e.getY();
    	/**
    	 * if count==0 draw new object or draw target around chousen object*/
    	if(clickCount==0){
    		dr.target(xP,yP);
    		dr.add(xP,yP);


    	}
    	/**if count is Bigger then 0 draw Line if new click is inside TargetRadius*/
    	else{
    		delete.setVisible(false);
    		inner.revalidate();
    		inner.repaint();
    		if(Math.pow(xP-dr.xObject,2)+Math.pow(yP-dr.yObject,2)<= Math.pow(dr.radius, 2)){
                Log.DebugLog("you have clicked in the radius, trying to move object");
    			if(dr.getSelectedObject() != null)
                        {
                            RunningGame.moveObject(xP,yP,dr.getSelectedObject(), socket);
                        }
    			dr.pressed=false;
    			clickCount=0;
        	}
    		
    		/**if second click is outside of TargetRadius remove targetRadius and count is zero to draw no Object take one on Panel*/
    		else{
    			timerfast.stop();
    			timerslow.start();
    			dr.pressed=false;
        		clickCount=0;
    		}
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
    
    class ActionListenerSlow implements ActionListener {
    	  public void actionPerformed(ActionEvent e) {
    		  repaint();

    	  }
    }
    class ActionListerFast implements ActionListener {
    	  public void actionPerformed(ActionEvent e) {
    		  repaint();

    	  }
    }
    
    
    	
}
