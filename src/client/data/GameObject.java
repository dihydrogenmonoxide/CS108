package client.data;

import shared.game.Coordinates;
import shared.game.GameSettings;

/**
 * This is class holds all the Informations about a specific GameObject (e.g. a
 * building) Especially which type it is, if its movable, what image should be
 * drawn etc.
 *
 * @author fox918
 */
class GameObject
{    
    ObjectType type;
    int objectId;
    int playerId;
    int healthPoints;
    Coordinates location;
    
    
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
}
