/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.game.field;

import java.awt.image.BufferedImage;
import shared.game.Coordinates;

/**
 *
 * @author oliverwisler
 */
public interface DrawableObject
{
    public BufferedImage getImg();
    public Coordinates getLocation();
}
