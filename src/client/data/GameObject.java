package client.data;

/**
 * This is class holds all the Informations about a specific GameObject (e.g. a building)
 * Especially which type it is, if its movable, what image should be drawn etc.
 * @author fox918
 */
class GameObject
{
    static enum ObjectType{TANK, FIGHTER, BOMBER, ANTIAIR, BUNKER, RADAR, REPRO, BANK};
    ObjectType type;
    int id;
    
}
