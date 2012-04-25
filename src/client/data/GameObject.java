package client.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import shared.game.Coordinates;
import shared.game.GameSettings;

/**
 * This is class holds all the Informations about a specific GameObject (e.g. a
 * building) Especially which type it is, if its movable, what image should be
 * drawn etc.
 *
 * @author fox918
 */
public class GameObject
{    
    ObjectType type;
    int objectId;
    int playerId;
    int healthPoints;
    boolean wasUpdated = false;
    Coordinates location;
    Coordinates oldLocation;
    
    
    GameObject(ObjectType type, Coordinates coords, int objectId, int playerId, int healthPoints)
    {
        this.type = type;
        this.location = coords;
        this.healthPoints = healthPoints;
        this.objectId = objectId;
        this.playerId = playerId;
    }
    
    public void update(Coordinates coords, int healthPoints)
    {
        this.oldLocation = this.location;
        this.location = coords;
        this.healthPoints = healthPoints;
        this.wasUpdated = true;
         //-- determine wheter to delete it (healthpoints == 0)
           if(healthPoints <= 0)
           {
               RunningGame.deleteObject(objectId);
           }
    }    
    //TODO markierung if friend or foe
    public BufferedImage getImg(){
        return type.getImg();
    }
    /**returns the color of all lines for this object. Depends if friend or foe*/
    public Color getColor()
    {
        //TODO implement friend or foe
        return new Color (0,255,0);
    }

    public Coordinates getLocation()
    {
        return location;
    }
    
    public Coordinates getOldLocation()
    {
        return oldLocation;
    }
    
    /**if this object was updated since the last toggle.*/
    public boolean wasUpdated()
    {
        return wasUpdated;
    }
    
    /**when the object has been drawn for the new round.*/
    public void isUpdated()
    {
        wasUpdated = false;
    }
    
    /**the moving range of this object.*/
    public int movingRange(){
    	return type.getMovingRange();
    }
    
    /**the id of this object.*/
    public int getID(){
		return objectId;
    }
    
    /**returns the current Healthpoints of the object.*/
    public int getHealth(){
    	return healthPoints;
    }
    
}
