package client.game.field;

import client.data.GameObject;
import client.data.RunningGame;
import client.game.GameButtonsPanel;
import client.net.Clientsocket;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JButton;
import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;

public class ChoseObject 
{
	
    /**boolean to take object*/
    static boolean pressed=false;
    /**position to handle the chosen Object from GameObjectList*/
	int xObject,yObject;
    /**radius to move object*/
    int radius;
    /**the Connection made to the Server.*/
    Clientsocket socket;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private GameButtonsPanel but;
    /**delete Button to set Visible true*/
    JButton delete;
    GameObject obj;
    /**Dimension of the ObjectImage*/
    double imageDim;
    
	public ChoseObject(Clientsocket s, JButton delete, double imageDim)
	{
		this.delete=delete;
		this.socket=s;
		this.imageDim=imageDim;
	}
	
	
	/**go true every Object which already exists. If point which is pressed equals Object, make pressed true, to draw TargetRadius*/
	public void target(int x , int y)
	{
		Collection<GameObject> c = RunningGame.getObjects().values();
		Iterator<GameObject> objIter = c.iterator();
        while(objIter.hasNext())
        {
        	obj = objIter.next();
	    	Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
	    	
        	xObject= pixelCoords.width;
        	yObject= pixelCoords.height;
            if(x > xObject-imageDim/2 && x < xObject+imageDim/2 && y > yObject-imageDim/2 && y < yObject+imageDim/2&&!obj.hasMoved())//TODO i don't think thats best way to check for moving range
            {
            	GameFieldPanel.fastTimer();
    			radius= Coordinates.radCoordToPixel(obj.movingRange(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
    			pressed=true;
    			if(obj.isMovable()){
    				GameFieldPanel.clickCount=1;    
    			}
    			delete.setVisible(true);
        		return;
        	}
            delete.setVisible(false);
            pressed=false;
        }
        
        add(x,y);
		
    }
	    
	/**add new Objects to the ObjectList*/
    public void add(int x, int y)
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
    }
    
    void objectPressed(int x, int y)
    {
		if(Math.pow(x-xObject,2)+Math.pow(y-yObject,2)<= Math.pow(radius, 2))
		{
            Log.DebugLog("you have clicked in the radius, trying to move object");
			if(obj != null)
                    {
                        RunningGame.moveObject(x,y,obj, socket);
                    }
    	}
		pressed=false;
    	delete.setVisible(false);
		GameFieldPanel.clickCount=0;
		GameFieldPanel.slowTimer();
    }
    
    public GameObject getSelectedObject()
    {
        return obj;
    }
    
    public int getRadius(){
    	return radius;
    }
    
    public int objectPositionX(){
    	return xObject;
    }
    public int objectPositionY(){
    	return yObject;
    }
    
}
