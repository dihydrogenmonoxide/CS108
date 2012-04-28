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
    Coordinates oldLocation;
    int money=0;
    
    /** constructs a new GameObject, which is the representation of an object for the client.
     * @param type which type the object has
     * @param coords at which coordinates the object is
     * @param objectId the id which identifies this object
     * @param playerId the id of the player this object belongs
     * @param healthPoints how much healthPoints the object has
     */ 
    GameObject(ObjectType type, Coordinates coords, int objectId, int playerId, int healthPoints)
    {
        this.type = type;
        this.location = coords;
        this.healthPoints = healthPoints;
        this.objectId = objectId;
        this.playerId = playerId;
    }
    
    /**updates this objects, set new location and set the healthPoints
     * @param coords the new coordinates of the object
     * @parram healthPoints new health of the object
     */
    public void update(Coordinates coords, int healthPoints)
    {
        //-- determine if object was moved
        if(coords != location)
        {
            oldLocation = location;
            location = coords;
        }
        
        this.healthPoints = healthPoints;
         //-- determine wheter to delete it (healthpoints == 0)
           if(healthPoints <= 0)
           {
               RunningGame.deleteObject(objectId);
           }
    }
    
    /**determine if the object has been moved since the last resetOldLocation.
     * @return when the object has moved: true
     */
    public boolean hasMoved()
    {
        return location!=oldLocation;
    }
    
    /** get the old Location of this object.
     * @return the old location of this object
     **/
    public Coordinates getOldLocation()
    {
        return oldLocation;
    }
    
    
    /** get the image which represents this object.
     * @return the image of this object
     */
    public BufferedImage getImg(){
        //TODO markierung if friend or foe
    
        return type.getImg();
    }

    /** get the Location of this object.
     * @return the location of this object now.
     */
    public Coordinates getLocation()
    {
        return location;
    }
    
   /** get the moving range of this object.
     * @return the moving range of this object.
     */
    public int movingRange(){
    	return type.getMovingRange();
    }
    
     /** get the object Id of this object.
     * @return the object Id of this object.
     */
    public int getID(){
		return objectId;
    }
    
     /** get the health of this object.
     * @return the health of this object.
     */
    public int getHealth(){
    	return healthPoints;
    }
    
      /** get the maximum health of this object.
     * @return the maximum health of this object..
     */
    public int getInitHealth(){
    	return type.initHealthPoints;
    }
    
    /** get the value of this object (how much it costs).
     * @return the value of this object.
     */
    public int getValue(){
    	return type.getValue();
    }
    
    /** whether this object is movable or not.
     * @return whether this object is movable or not.
     */
    public boolean isMovable(){
    	return type.isMovable();
    }
    
    /** whether this object can be selected or not.
     * @return whether this object be selected or not.
     */
    public boolean isSelectable()
    {
        return PlayerManager.myId() == this.playerId && type.isSelectable;
    }
    
    /** resets the old location of this object, object.hasMoved is now false.
     */
    public void resetOldLocation()
    {
        oldLocation = location;
    }
    
    /**
     Returns the Protocol representation of the type.
     */
    public Protocol getProtocol()
    {
        return type.getProtocol();
    }
    
}
