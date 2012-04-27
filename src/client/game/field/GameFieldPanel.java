package client.game.field;

import client.data.GameObject;
import client.data.RunningGame;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.NetEvent;
import client.game.GameFrame;
import client.game.InnerGameFrame;
import client.net.Clientsocket;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.MapManager;
import javax.swing.JTextField;

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
    private Clientsocket socket;
    private Image bil;
    //if the background is rendered already
    boolean isRendered = false;
    /**angel for showing the movingRange*/
    private double angel=0;
    private double ang=4;
    
    ChoseObject dr;
    /**Point of mousePressed*/
    int xP,yP;
    /**GameFrame */
    GameFrame game;
    /**JPanel InnerGameFrame to add deleteButton*/
    InnerGameFrame inner;
    /**Gridbagcontraints to add Position of delete Button*/
    GridBagConstraints cl;
    /**Integer to get how many Clicks*/
    static int count=0;
    /**ArrayList, which holds Lines*/
    static List<Lines> line=new ArrayList<Lines>();
    /**delete Button which appears, when its Possible to click on it (if target is drawn)*/
    JButton delete;

    Graphics2D gd;
    Timer timerslow;
    Timer timerfast;
    
        
    public GameFieldPanel(Clientsocket s, GameFrame gameFrame, InnerGameFrame innerGameFrame, GridBagConstraints c)
    {
    	this.cl=c;
    	this.inner = innerGameFrame;
    	this.game= gameFrame;
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
				dr.removeLine(dr.xObject,dr.yObject);
				timerfast.stop();
				timerslow.start();
				dr.pressed=false;
				delete.setVisible(false);
				count=0;
				
			}
		});

        //TODO decide which field to highlight and which are inactive.		
		
		
        
        this.setBackground(Color.blue);
         
        
        this.addMouseListener(this);
        
        	
        //static framerate:
        timerslow= new Timer(200, new ActionListenerSlow());
        timerfast= new Timer(70,new ActionListerFast());
        timerslow.start();
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

      		  	if(count==1){
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

        	for (Lines l : line)
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
    public void mousePressed(MouseEvent e)
    {
    	dr= new ChoseObject(socket,delete, gd, timerslow, timerfast);
    	xP= e.getX();
        yP= e.getY();
    	dr.lineTrue=false;
    	/**
    	 * if count==0 draw new object or draw target around chousen object*/
    	if(count==0){
    		dr.target(xP,yP);
    		dr.add(xP,yP);


    	}
    	/**if count is Bigger then 0 draw Line if new click is inside TargetRadius*/
    	else{


    		delete.setVisible(false);
    		inner.revalidate();
    		inner.repaint();
    		if((xP-dr.xObject)*(xP-dr.xObject)+(yP-dr.yObject)*(yP-dr.yObject)<= (dr.radius*dr.radius)){
    			//TODO update Object list with new Points
    			Lines li = new Lines(dr.xObject,dr.yObject,xP,yP);
    			if(dr.lineExist(dr.xObject, dr.yObject)){
    				line.add(li);
    			}
    			dr.pressed=false;
    			count=0;
        	}
    		/**if second click is outside of TargetRadius remove targetRadius and count is zero to draw no Object take one on Panel*/
    		else{
    			timerfast.stop();
    			timerslow.start();
    			dr.pressed=false;
        		count=0;
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
