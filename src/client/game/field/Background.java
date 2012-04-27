package client.game.field;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.data.RunningGame;

import shared.Log;
import shared.game.MapManager;

public class Background extends JPanel{
	 /**
     * Buffered image to Paint Map
     */
    private BufferedImage backgroundMap;
    /**
     * the width the map will be rendered
     */
    public static int MAP_WIDTH = 500;
    /**
     * the Height of the Map, is set after rendering.
     */
    
    public static int MAP_HEIGHT=(MAP_WIDTH*4/7);
    
    boolean isRendered=false;
    
    
	
	
	public Background(){
		
	}
	
	public void paint(Graphics g){
		boolean logRedraw=false;
        //-- paint background
		rendered();
        g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
        if (logRedraw)
        {
            Log.DebugLog("GameField: redrawing now!");
            Log.DebugLog("map width =" + MAP_WIDTH + " height=" + MAP_HEIGHT);
        }
		
	}

	void rendered(){
		   //XXX not exactly nice way to do it
        if (!isRendered)
        {
            MAP_WIDTH = getWidth();
            backgroundMap = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_WIDTH);
            MAP_HEIGHT = backgroundMap.getHeight();
            isRendered = true;
        }
	}
}
