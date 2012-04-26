package client.game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;

import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import client.data.GameObject;
import client.data.RunningGame;
import client.net.Clientsocket;

public class ChoseObject {
	
    /**boolean to take object*/
    static boolean pressed=false;
    /**boolean to draw line*/
    static boolean lineTrue=false;
    /**position to handle the chosen Object from GameObjectList*/
	static int xObject,yObject;
    /**radius to move object*/
    static int radius;
//    /**Point of mouseListener*/
//    static int xMouse, yMouse;
    /**boolean to see if place is free where you want to add Object*/
    boolean frei= true;
    /**the Connection made to the Server.*/
    Clientsocket socket;
    /**Buttonspanel to choce pressed button from ButtonGroup*/
    private GameButtonsPanel but;
    /**delete Button to set Visible true*/
    JButton delete;
    GameObject obj = null;
    Graphics2D gd;
    
	public ChoseObject(Clientsocket s, GameButtonsPanel butons, JButton delete, Graphics2D gd){
		this.delete=delete;
		this.but=butons;
		this.socket=s;
		this.gd=gd;
	}
	
	
	/**go true every Object which already exists. If point which is pressed equals Object, make pressed true, to draw TargetRadius*/
	public void target(int x , int y){
		Collection<GameObject> c = RunningGame.getObjects().values();
        Iterator<GameObject> objIter = c.iterator();
        while(objIter.hasNext()){
        	obj = objIter.next();
//  		  	new ObjectInfo(gd,c,obj);	
	    	Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
        	xObject= pixelCoords.width - 20 / 2;
        	yObject= pixelCoords.height - 20 / 2;
            if(x > xObject && x < xObject+20 && y > yObject && y < yObject+20){
    			radius= Coordinates.radCoordToPixel(obj.movingRange(), new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT));
    			pressed=true;
        		frei = false;
            	xObject=xObject+10;
            	yObject=yObject+10;
        		GameFieldPanel.count=1;    
        		delete.setVisible(true);
        		return;
        	}
        	else{
        		pressed=false;
        		frei = true;
        	}      	

        }

    }
	    
	/**add new Objects to the ObjectList*/
    public void add(int x, int y){
    	if(frei){
            
        	
        	Log.DebugLog("User clicked on the map at (" + x + "," + y + ") with the button choice: " + but.choice.toString());
            Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(x, y, new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT)));
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
                	//TODO you cant add Bank in Panel
                    spawnObject(x, y, Protocol.OBJECT_BANK);
                    break;
                case NONE:
                default:
            }
        GameFieldPanel.count=0;
        }
    }
    
    
    /**
     * sends a spawn request to the server.
     *
     * @param c   the Coordinates where to spawn
     * @param obj the object to spawn
     */
    public void spawnObject(int x, int y, Protocol obj)
    {
        Log.InformationLog("Trying to spawn Object: " + obj.str() + ", x=" + x + ", y=" + y + ", m_width" + GameFieldPanel.MAP_WIDTH + ", m_heigth" + GameFieldPanel.MAP_HEIGHT);
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, new Dimension(GameFieldPanel.MAP_WIDTH, GameFieldPanel.MAP_HEIGHT)));
    }

    /**check if Object has already a line in this round or not*/
	boolean drawLine= true;
    boolean lineExist(int xstart, int ystart){

    	for (Lines l : GameFieldPanel.line){
    		if(l.xs==xstart&&l.ys==ystart){
    			drawLine=false;
    			break;
    		}else{
    			drawLine=true;
    		}
    	}
		return drawLine;
    }
}
