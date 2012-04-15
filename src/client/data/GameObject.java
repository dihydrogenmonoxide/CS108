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

    /**holds all the infos about a specific type.
     constructor: imagePath, isMovable, isSelectable, MovingRange, AttackRange, HealthPoints
     */
    static enum ObjectType
    {

        TANK("./bilder/Panzer.png", GameSettings.Tank.movingRange),
        FIGHTER,
        BOMBER, 
        ANTIAIR, 
        BUNKER, 
        RADAR, 
        REPRO, 
        BANK;
    };
    ObjectType type;
    int objectId;
    int playerId;
    int healthPoints;
    Coordinates location;
}
