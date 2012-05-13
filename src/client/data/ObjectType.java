/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.data;

import client.resources.ResourceLoader;
import java.awt.image.BufferedImage;
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

    TANK(Protocol.OBJECT_TANK, "images/Panzer.png", true, true, GameSettings.Tank.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints, GameSettings.Tank.price),
    FIGHTER(Protocol.OBJECT_FIGHTER_JET, "images/Flugzeug.png", true, true, GameSettings.Jet.movingRange, GameSettings.Jet.attackRange, GameSettings.Jet.healthPoints, GameSettings.Jet.price),
    BOMBER(Protocol.OBJECT_BOMBER, "images/Bomber.png", true, true, GameSettings.Bomber.movingRange, GameSettings.Bomber.attackRange, GameSettings.Bomber.healthPoints, GameSettings.Bomber.price),
    ANTIAIR(Protocol.OBJECT_STATIONARY_ANTI_AIR, "images/Flugabwehr.png", false, true, GameSettings.Flak.attackRange, GameSettings.Flak.healthPoints, GameSettings.Flak.price),
    BUNKER(Protocol.OBJECT_STATIONARY_ANTI_TANK, "images/Landabwehr.png", false, true, GameSettings.ATT.attackRange,  GameSettings.ATT.healthPoints,  GameSettings.ATT.price),
    //RADAR("./bilder/Radar.png", true, true, GameSettings.Tank.movingRange, GameSettings.Tank.attackRange, GameSettings.Tank.healthPoints), 
    REPRO(Protocol.OBJECT_REPRODUCTION_CENTER, "images/Repro.png", false, true, GameSettings.Reproductioncenter.movingRange, GameSettings.Reproductioncenter.healthPoints, GameSettings.Reproductioncenter.price),
    BANK(Protocol.OBJECT_BANK, "images/Bank.png", false, true, GameSettings.Bank.movingRange, GameSettings.Tank.healthPoints, GameSettings.Tank.price);
    
    Protocol protocol;
    String imagePath;
    boolean isMovable;
    boolean isSelectable;
    int movingRange;
    int attackRange;
    int initHealthPoints;
    int value;
    BufferedImage img;
    ResourceLoader res = new ResourceLoader();

    ObjectType(Protocol p, String path, boolean isMovable, boolean isSelectable, int movingRange, int attackRange, int initHealthPoints, int price)
    {
        this.protocol = p;
        this.imagePath = path;
        this.isMovable = isMovable;
        this.isSelectable = isSelectable;
        this.movingRange = movingRange;
        this.attackRange = attackRange;
        this.initHealthPoints = initHealthPoints;
        this.value = price;
        setImage();
    }

    ObjectType(Protocol p, String path, boolean isMovable, boolean isSelectable, int attackRange, int initHealthPoints, int price)
    {
        this(p, path, isMovable, isSelectable, 0, attackRange, initHealthPoints, price);
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
            this.img = ImageIO.read(res.load(imagePath));
        } catch (IOException ex)
        {
            Log.ErrorLog("ObjectType, could not load image");
        }
    }
    
    public String getImagePath()
    {
        return imagePath;
    }

    public boolean isMovable()
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
    
    public int getValue()
    {
        return value;
    }

    public BufferedImage getImg()
    {
        return img;
    }
};
