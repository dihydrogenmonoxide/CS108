/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import shared.Log;
import shared.Protocol;
import shared.game.GameSettings;

/**
 * holds all the infos about a specific type. constructor: imagePath, isMovable,
 * isSelectable, MovingRange, AttackRange, HealthPoints
 *
 */
public enum ObjectType
{

    TANK(Protocol.OBJECT_TANK, "bilder/Panzer.png", true, true, GameSettings.Tank.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    FIGHTER(Protocol.OBJECT_FIGHTER_JET, "bilder/Flugzeug.png", true, true, GameSettings.Jet.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    BOMBER(Protocol.OBJECT_BOMBER, "bilder/Bomber.png", true, true, GameSettings.Bomber.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    ANTIAIR(Protocol.OBJECT_STATIONARY_ANTI_AIR, "bilder/Flugabwehr.png", false, true, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    BUNKER(Protocol.OBJECT_STATIONARY_ANTI_TANK, "bilder/Landabwehr.png", false, true, GameSettings.ATT.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    //RADAR("./bilder/Radar.png", true, true, GameSettings.Tank.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints), 
    REPRO(Protocol.OBJECT_REPRODUCTION_CENTER, "bilder/Repro.png", false, true, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints),
    BANK(Protocol.OBJECT_BANK, "bilder/Bank.png", false, true, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints);
    
    Protocol protocol;
    String imagePath;
    boolean isMovable;
    boolean isSelectable;
    int movingRange;
    int attackRange;
    int initHealthPoints;
    BufferedImage img;

    /**
     * 
     */
    ObjectType(Protocol p, String path, boolean isMovable, boolean isSelectable, int movingRange, int attackRange, int initHealthPoints)
    {
        this.protocol = p;
        this.imagePath = path;
        this.isMovable = isMovable;
        this.isSelectable = isSelectable;
        this.movingRange = movingRange;
        this.attackRange = attackRange;
        this.initHealthPoints = initHealthPoints;
        setImage();
    }

    ObjectType(Protocol p, String path, boolean isMovable, boolean isSelectable, int attackRange, int initHealthPoints)
    {
        this(p, path, isMovable, isSelectable, 0, attackRange, initHealthPoints);
    }

    /**
     * converts from Protocol to ObjectType.
     *
     * @param objectType the Protocol representation of the object.
     */
    static ObjectType fromProtocol(final Protocol objectType)
    {
        for (ObjectType p : ObjectType.values())
        {
            if (p.getProtocol() == objectType)
            {
                return p;
            }
        }
        return null;
    }

    private void setImage()
    {
        try
        {
            this.img = ImageIO.read(new File(imagePath));
        } catch (IOException ex)
        {
            Log.DebugLog("ObjectType, could not load image");
        }
    }
    
    public String getImagePath()
    {
        return imagePath;
    }

    public boolean getMovable()
    {
        return isMovable;
    }

    public boolean getSelectable()
    {
        return isSelectable;
    }

    public int getMovingRange()
    {
        return movingRange;
    }

    public int getAttackRange()
    {
        return attackRange;
    }

    public int getInitHealthPoints()
    {
        return initHealthPoints;
    }

    public Protocol getProtocol()
    {
        return protocol;
    }

    public BufferedImage getImg()
    {
        return img;
    }
};
