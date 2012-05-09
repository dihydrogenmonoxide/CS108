package client.game.field;

import client.data.GameObject;
import client.data.GamePhases;
import client.data.RunningGame;
import client.game.InnerGameFrame;
import client.net.Clientsocket;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.Line;
import javax.swing.Action;
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
    static Timer timerSlow;
	static Timer timerFast;
	static Timer timerAnim;
    
	/** flag if we should write all the drawing to the log*/
	boolean logRedraw = false;
	     	
	/**ArrayList, which holds Lines*/
	static List<Line2D> line=new ArrayList<Line2D>();
	static List<Polygon> pol = new ArrayList<Polygon>();
	
	ChangingLists list;
    
        
    public GameFieldPanel(Clientsocket s, JButton delete2)
    {
        super();
        this.socket = s;
        this.delete=delete2;

        
        imageDim = MAP_WIDTH/50*1.5;
        this.setPreferredSize(this.getMaximumSize());
        
		delete.addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{
				RunningGame.deleteObject(dr.obj.getID());
				//TODO send update to server
				RunningGame.deleteObjectServer(xP, yP, dr.getSelectedObject(), socket);
				delete.setVisible(false);
				clickCount=0;
				slowTimer();
				dr.pressed=false;
				
			}
		});       
         
        
        this.addMouseListener(this);
        this.setOpaque(false);
        	
        /**static framerate fast one to draw Radar, if object is pressed, otherwise slow one is token*/
        timerSlow= new Timer(200, new PaintAction());
        timerFast= new Timer(70,new PaintAction());
        timerAnim= new Timer(30, new PaintAction());
        timerSlow.start();
        
        dr= new ChoseObject(socket,delete, imageDim);
        list= new ChangingLists();
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
            Graphics2D gd = (Graphics2D)g;
            
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
                           Dimension newPixelCoodrs = Coordinates.coordToPixel(obj.getLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                    	   if (RunningGame.getGamePhase()==GamePhases.BUILD){
                    		   
                        	   g.setColor(Color.orange);
                    		   Line2D l = new Line2D.Double(oldPixelCoords.width,oldPixelCoords.height,pixelCoords.width, pixelCoords.height);
                    		   line.add(l);
                    		   if(dr.getSelectedObject().hasMoved()){
                    			   list.drawArrow(pol, oldPixelCoords.width, oldPixelCoords.height, newPixelCoodrs.width, newPixelCoodrs.height);
                    		   }
                    		   
                    	   }
                    	   if(RunningGame.getGamePhase()==GamePhases.ANIM){
                    		   g.setColor(Color.red);
                    	   }
                    	   for (Line2D f:line)
                           {
                        	   g.drawLine((int)f.getX1(),(int) f.getY1(),(int) f.getX2(),(int) f.getY2());
                           }
                    	   for(Polygon p:pol){
                    		   gd.setColor(Color.white);
                    		   gd.fillPolygon(p);
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
  		  		ObjectInfo inf =new ObjectInfo(g, dr.getSelectedObject());
            	/**transp is the starting value of the transparence*/
        		double transp=255;
        		/**a is the value which reduce the Color*/
        		double a=transp/(360/2-5);

  		  		int y1=0;
  		  		int x1=0;
  		  		int y;
  		  		int x;
  		  		
      		  	Polygon polyRad= new Polygon();
      		  	/**draws Radar around Object with ObjectRadius*/
      		  	for(int i=0; i<360/2-5;i++)
      		  	{
      		  		double rad1= Math.toRadians(angel);
          		  	g.setColor(new Color(103, 200, 255,(int)transp));
    		  		y=y1;
    		  		x=x1;
      		  		y1 = (int) (Math.cos(rad1)*dr.getRadius());
      		  		x1 = (int) (Math.sin (rad1) * dr.getRadius());
      		  		polyRad.addPoint(dr.objectPositionX(),dr.objectPositionY());
    		  		polyRad.addPoint(x1+dr.objectPositionX(),y1+dr.objectPositionY());
      		  		polyRad.addPoint(x+dr.objectPositionX(),y+dr.objectPositionY());
    		  		g.fillPolygon(polyRad);
      		  		angel-=2;
      		  		transp-=a;
    		  		polyRad.reset();
      		  	}

            } 

        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(RunningGame.getGamePhase()==GamePhases.ANIM)
        {
        	dr.pressed=false;
        	
        	timerFast.stop();
        	timerSlow.stop();
        	timerAnim.start();

        	for(int i=0; i<line.size();i++)
        	{
				double xstart=line.get(i).getX1();;
				double ystart=line.get(i).getY1();
				double xend=line.get(i).getX2();
				double yend=line.get(i).getY2();
				double xmove= xstart;
				double ymove=ystart;
				double xdif=line.get(i).getX2()-line.get(i).getX1();
				double ydif=line.get(i).getY2()-line.get(i).getY1();
		    	list.updateLine(line, xdif, ydif,xmove, ymove, yend,xend, g ,i);
        	}
        	
        	if(RunningGame.getAnimTime()<=0)
        	{
            	line.clear();
        		timerAnim.stop();
            	slowTimer();
        	}

        }
        
    }
    
    public void paint(Graphics g)
    {
        /** drawing background map*/
        
        /** determine if we have to render the map (when the size changes or at the start)*/
        if (backgroundMap == null || backgroundMap.getWidth() != getWidth())
        {
        	removeObject();
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
    
    class PaintAction implements ActionListener 
    {
    	  public void actionPerformed(ActionEvent e) 
    	  {
    		  
    		  repaint();
    	  }
    }
      
    static void fastTimer()
    {
    	timerSlow.stop();
    	timerFast.start();
    }
    static void slowTimer()
    {
    	timerFast.stop();
    	timerSlow.start();
    }
    public static void removeObject(){
    	if(line!=null){
    		pol.clear();
    		line.clear();
    	}
    	
	}
}
