package client.game.field;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.Timer;

import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import client.data.GameObject;
import client.data.RunningGame;
import client.game.GameButtonsPanel;
import client.net.Clientsocket;

public class ChoseObject 
{
	
    /**boolean to take object*/
    static boolean pressed=false;
    /**position to handle the chosen Object from GameObjectList*/
	static int xObject,yObject;
    /**radius to move object*/
    static int radius;
    /**boolean to see if place is free where you want to add Object*/
    boolean frei= true;
    /**the Connection made to the Server.*/
    Clientsocket socket;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private GameButtonsPanel but;
    /**delete Button to set Visible true*/
    JButton delete;
    GameObject obj;
    /**Two timer to repaint one is fast when object is clicked and the other slower*/
    Timer timerslow,timerfast;
    
	public ChoseObject(Clientsocket s, JButton delete, Timer timerslow, Timer timerfast)
	{
		this.timerfast=timerfast;
		this.timerslow=timerslow;
		this.delete=delete;
		this.socket=s;
	}
	
	
	/**go true every Object which already exists. If point which is pressed equals Object, make pressed true, to draw TargetRadius*/
	public void target(int x , int y){
		Collection<GameObject> c = RunningGame.getObjects().values();
		Iterator<GameObject> objIter = c.iterator();
        while(objIter.hasNext())
        {
        	obj = objIter.next();
	    	Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
        	xObject= pixelCoords.width;
        	yObject= pixelCoords.height;
            if(x > xObject-10 && x < xObject+10 && y > yObject-10 && y < yObject+10&&obj.isMovable())//TODO i don't think thats best way to check for moving range
            {
            	timerslow.stop();
        		timerfast.start();
    			radius= Coordinates.radCoordToPixel(obj.movingRange(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
    			pressed=true;
        		frei = false;
        		GameFieldPanel.clickCount=1;    
        		delete.setVisible(true);
        		return;
        	}
        	else
        	{
        		timerfast.stop();
        		timerslow.start();
        		pressed=false;
        		frei = true;
        	}      	

        }

    }
	    
	/**add new Objects to the ObjectList*/
    public void add(int x, int y)
    {
    	if(frei)
    	{
            	
            Log.DebugLog("User clicked on the map at (" + x + "," + y + ") with the button choice: " + but.choice.toString());
            Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(x, y, new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT)));
            Log.DebugLog("sending request to create:" + but.choice);
            switch (but.choice)
            {
                case TANK:
                    RunningGame.spawnObject(x, y, Protocol.OBJECT_TANK, socket);
                    break;
                case FIGHTER:
                    RunningGame.spawnObject(x, y, Protocol.OBJECT_FIGHTER_JET, socket);
                    break;
                case BOMBER:
                	RunningGame.spawnObject(x, y, Protocol.OBJECT_BOMBER, socket);
                    break;
                case ANTIAIR:
                	RunningGame.spawnObject(x, y, Protocol.OBJECT_STATIONARY_ANTI_AIR, socket);
                    break;
                case BUNKER:
                	RunningGame.spawnObject(x, y, Protocol.OBJECT_STATIONARY_ANTI_TANK, socket);
                    break;
                case REPRO:
                	RunningGame.spawnObject(x, y, Protocol.OBJECT_REPRODUCTION_CENTER, socket);
                    break;
                case BANK:
                	RunningGame.spawnObject(x, y, Protocol.OBJECT_BANK, socket);
                    break;
                case NONE:
                default:
            }
        GameFieldPanel.clickCount=0;
        }
    }
    
    /**returns which object is selected, if at all oO */
    public GameObject getSelectedObject()
    {
        return obj;
    }
    
   
}
