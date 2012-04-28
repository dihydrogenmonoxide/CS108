package client.game.field;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.data.RunningGame;

import shared.Log;
import shared.game.MapManager;

public class Background extends JPanel
{

    /**
     * Buffered image to Paint Map
     */
    private BufferedImage bgMap;
    /**
     * the width the map will be rendered
     */
    public static int MAP_WIDTH = 500;
    /**
     * the Height of the Map, is set after rendering.
     */
    public static int MAP_HEIGHT = (MAP_WIDTH * 4 / 7);

    public Background()
    {
        
    }
    
    /**this function will render the background map of the game*/
    public void paint(Graphics g)
    {        
        //-- determine if we have to render the map (when the size changes or at the start)
        if (bgMap == null || bgMap.getWidth() != getWidth())
        {
            Log.DebugLog("Map manager: rendered Map");
            //-- render map
            bgMap = MapManager.renderMap(RunningGame.getMyFieldId(), this.getWidth());
            MAP_WIDTH = bgMap.getWidth();
            MAP_HEIGHT = bgMap.getHeight();
        }

        //-- paint background
        g.drawImage(bgMap, 0, 0, bgMap.getWidth(), bgMap.getHeight(), 0, 0, bgMap.getWidth(), bgMap.getHeight(), Color.BLACK, null);
    }
}

