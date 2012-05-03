package client.game.field;

import client.data.GameObject;
import client.data.GamePhases;
import client.data.RunningGame;
import client.game.InnerGameFrame;
import client.net.Clientsocket;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
    /**
     * the Height of the Map, is set after rendering.
     */
    public static int MAP_HEIGHT=(MAP_WIDTH*4/7);
    
    /**
     * Image for DoubleBufferedImage
     */
    private Image dbImage;
    private Graphics dbg;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private Clientsocket socket;
    /**angel for showing the movingRange*/
    private double angel=0;
    /** width of the image*/
    double imageDim;
    
    ChoseObject dr;
    /**Point of mousePressed*/
    int xP,yP;
    /**Integer to get how many Clicks*/
    static int clickCount=0;
    /**delete Button which appears, when its Possible to click on it (if target is drawn)*/
    JButton delete;

    /**Two timer to repaint one is fast when object is clicked and the other slower*/
    static Timer timerslow;
	static Timer timerfast;
	static Timer animtimer;
    
	/** flag if we should write all the drawing to the log*/
	boolean logRedraw = false;
//	int xab=0;
//	int yab=0;
//	private double xstart;
//	private int ystart;
//	private int xend;
//	private int yend;
//	double xdif=xend-xstart;
//	double ydif=yend-ystart;
     
	/**ArrayList, which holds Lines*/
	static List<Lines> line=new ArrayList<Lines>();
    
        
    public GameFieldPanel(Clientsocket s, JButton delete2)
    {
        this.socket = s;
        this.delete=delete2;

        
        imageDim = MAP_WIDTH/50*1.5;
        this.setPreferredSize(this.getMaximumSize());
        
		delete.addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{
				RunningGame.deleteObject(dr.obj.getID());
				delete.setVisible(false);
				clickCount=0;
				slowTimer();
				dr.pressed=false;
				
			}
		});       
         
        
        this.addMouseListener(this);
        this.setOpaque(false);
        	
        /**static framerate fast one to draw Radar, if object is pressed, otherwise slow one is token*/
        timerslow= new Timer(200, new ActionListenerSlow());
        timerfast= new Timer(70,new ActionListerFast());
        animtimer= new Timer(40, new ActionListenerAnim());
        timerslow.start();
        
        dr= new ChoseObject(socket,delete, imageDim);
    }

	
    	
    
    public void paintComponent(Graphics g)
    {   
        try
        {
            /** paint background*/
            g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
            
            /**some logging (maybe)*/
            if (logRedraw)
            {
                Log.DebugLog("GameField: redrawing now!");
                Log.DebugLog("map width =" + MAP_WIDTH + " height=" + MAP_HEIGHT);
            }
            
            /** paint objects*/
            Collection<GameObject> c = RunningGame.getObjects().values();
            Iterator<GameObject> objIter = c.iterator();
            while (objIter.hasNext())
            {
                GameObject obj = objIter.next();
                BufferedImage objImg = obj.getImg();
                Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                
                /**some logging (maybe)*/
                if (logRedraw)
                {
                    Log.DebugLog("GameField: x=" + obj.getLocation().getX() + " y=" + obj.getLocation().getY() + "berechnet: pixelX=" + pixelCoords.width + " pixelY=" + pixelCoords.height);
                }
                
                /** draw image*/
                

                if (objImg != null)
                {
                        /** draw a line if object has moved*/
                       if(obj.hasMoved())
                       {
                           Dimension oldPixelCoords = Coordinates.coordToPixel(obj.getOldLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                           g.setColor(Color.orange);
                           Lines l =new Lines(oldPixelCoords.width,oldPixelCoords.height,pixelCoords.width, pixelCoords.height);
                           line.add(l);
                           for (Lines f:line)
                           {
                        	   g.drawLine(f.xs, f.ys, f.xe, f.ye);
                           }
                           if(logRedraw)
                           {
                               Log.DebugLog("drawing line for this object");
                           }
                       }
                       
                       /** draw Object*/
                       g.drawImage(objImg, pixelCoords.width - (int)imageDim / 2, pixelCoords.height - (int)imageDim / 2,(int) imageDim,(int) imageDim, null);
                    
                }
            }
            if(dr.pressed)
            {
            	/**n is the starting value of the transparence*/
        		double n=255;
        		/**a is the value which reduce the Color*/
        		double a=n/(360/2-5);
        		/**transparence of the radar*/
      		  	double transp=n;
      		  	

  		  		int y1=0;
  		  		int x1=0;
  		  		int y;
  		  		int x;
      		  	if(clickCount==1)
      		  	{
                	ObjectInfo inf =new ObjectInfo(g, dr.obj);
      		  	}

      		  	Polygon poly= new Polygon();
      		  	/**draws Radar around Object with ObjectRadius*/
      		  	for(int i=0; i<360/2-5;i++){
      		  		double rad1= Math.toRadians(angel);
          		  	g.setColor(new Color(103, 200, 255,(int)transp));
    		  		y=y1;
    		  		x=x1;
      		  		y1 = (int) (Math.cos(rad1)*dr.radius);
      		  		x1 = (int) (Math.sin (rad1) * dr.radius);
      		  		poly.addPoint(dr.xObject,dr.yObject);
    		  		poly.addPoint(x1+dr.xObject,y1+dr.yObject);
      		  		poly.addPoint(x+dr.xObject,y+dr.yObject);
    		  		g.fillPolygon(poly);
      		  		angel-=2;
      		  		transp-=a;
    		  		poly.reset();
      		  	}

            } 

        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(RunningGame.getGamePhase()==GamePhases.ANIM)
        {
//        	timerfast.stop();
//        	timerslow.stop();
//        	animtimer.start();
//        	xdif=xend-xstart;
//        	ydif=xend-ystart;
//        	g.setColor(Color.red);
//    		for(Lines l: line)
//    		{
//    			g.drawLine(l.xs, l.ys, l.xe, l.ye);
//    			xstart=l.xs;
//    			xend=l.xe;
//    			ystart=l.ys;
//    			yend=l.ye;
//    		}
        	
    	line.clear();

        }
        

        
    }

    public void paint(Graphics g)
    {
        /** drawing background map*/
        
        /** determine if we have to render the map (when the size changes or at the start)*/
        if (backgroundMap == null || backgroundMap.getWidth() != getWidth())
        {
            Log.DebugLog("Map manager: rendered Map");
            /** render map*/
            backgroundMap = MapManager.renderMap(RunningGame.getMyFieldId(), this.getWidth());
            MAP_WIDTH = backgroundMap.getWidth();
            MAP_HEIGHT = backgroundMap.getHeight();
        }

        /** paint background*/
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
    	if(clickCount==0)
    	{
    		dr.target(xP,yP);

    	}
    	/**if count is Bigger then 0 draw Line if new click is inside TargetRadius*/
    	else
    	{
    		dr.objectPressed(xP, yP);
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
    
    class ActionListenerSlow implements ActionListener 
    {
    	  public void actionPerformed(ActionEvent e) 
    	  {
    		  
    		  repaint();
    	  }
    }
    class ActionListerFast implements ActionListener 
    {
    	  public void actionPerformed(ActionEvent e) 
    	  {
    		  repaint();
    	  }
    }
    class ActionListenerAnim implements ActionListener 
    {
    	  public void actionPerformed(ActionEvent e) 
    	  {
    		  
    	  }
    }
    
    
    static void fastTimer()
    {
    	timerslow.stop();
    	timerfast.start();
    }
    static void slowTimer()
    {
    	timerfast.stop();
    	timerslow.start();
    }
    
}
