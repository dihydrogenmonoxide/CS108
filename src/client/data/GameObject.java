package client.data;

import client.game.field.DrawableObject;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.GameSettings;

/**
 * This is class holds all the Informations about a specific GameObject (e.g. a
 * building) Especially which type it is, if its movable, what image should be
 * drawn etc.
 *
 * @author fox918
 */
public class GameObject implements DrawableObject
{    
    ObjectType type;
    int objectId;
    int playerId;
    int healthPoints;
    Coordinates location;
    int money=0;
    
    
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
        this.location = coords;
        this.healthPoints = healthPoints;
         //-- determine wheter to delete it (healthpoints == 0)
           if(healthPoints <= 0)
           {
               RunningGame.deleteObject(objectId);
           }
    }
    
    public String makeMove(Coordinates newCoords)
    {
        return null;
        //TODO implement this;
    }
    
    //TODO markierung if friend or foe
    public BufferedImage getImg(){
        return type.getImg();
    }

    public Coordinates getLocation()
    {
        return location;
    }
    //TODO make methode that gets Pixelvalue out of MovingRange
    public int movingRange(){
    	return type.getMovingRange();
    }
    
    public int getID(){
		return objectId;
    }
    
    public int getHealth(){
    	return healthPoints;
    }
    
    public int getMoney(){
    	//TODO add Money to Object
    	return money;
    }
    public boolean hasMovingRange(){
    	return type.getMovable();
    }
    /**
     Returns the Protocol of the type.
     */
    public Protocol getProtocol()
    {
        return type.getProtocol();
    }
    
}
