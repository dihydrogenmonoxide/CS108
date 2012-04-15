package client.game;

import client.data.RunningGame;
import client.net.Clientsocket;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.MapManager;

public class GameFieldPanel extends JPanel implements MouseListener
{

    /**
     * the width the map will be rendered
     */
    public static int MAP_WIDTH = 500;
    /**
     * the Height of the Map, is set after rendering.
     */
    //TODO find relation MAP_Width to MAP_height
    
    public static int MAP_HEIGHT=(MAP_WIDTH*4/7);
    /**
     * Buffered image to Paint Map
     */
    private BufferedImage backgroundMap;
    private BufferedImage map;
    
    /**
     * Image for DoubleBufferedImage
     */
    private Image dbImage;
    private Graphics dbg;
    private GameButtonsPanel but;
    private Clientsocket socket;
    private Image bil;
    
    //if the background is rendered already
    boolean isRendered = false;

    public GameFieldPanel(Clientsocket s)
    {
        this.socket = s;
        
        this.setPreferredSize(this.getMaximumSize());



        //TODO decide which field to highlight and which are inactive.


        this.setBackground(Color.blue);
        
        this.addMouseListener(this);
 

    }
    
    

    public void paintComponent(Graphics g)
    {
        try
        {
           
            g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
            //repaint();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g)
    {
        //XXX not exactly nice way to do it
        if (!isRendered)
        {
                MAP_WIDTH = getWidth();
                backgroundMap = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_WIDTH);
                MAP_HEIGHT = backgroundMap.getHeight();
                isRendered = true;
        }
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        paintComponent(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }
    

    /**
     * sends a spawn request to the server.
     *
     * @param c   the Coordinates where to spawn
     * @param obj the object to spawn
     */
    public void spawnObject(int x, int y, Protocol obj)
    {
        Log.InformationLog("Trying to spawn Object: " + obj.str() + ", x=" + x + ", y=" + y + ", m_width" + MAP_WIDTH + ", m_heigth" + MAP_HEIGHT);
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, MAP_WIDTH, MAP_HEIGHT));
    }

           
    public void mousePressed(MouseEvent e)
    {
        Log.DebugLog("User clicked on the map at (" + e.getX() + "," + e.getY() + ") with the button choice: " + but.choice.toString());
        Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(e.getX(), e.getY(), MAP_WIDTH, backgroundMap.getHeight()));
        Log.DebugLog("sending request to create:" + but.choice);
        switch (but.choice)
        {
            case TANK:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_TANK);
                break;
            case FIGHTER:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_FIGHTER_JET);
                break;
            case BOMBER:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_BOMBER);
                break;
            case ANTIAIR:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_STATIONARY_ANTI_AIR);
                break;
            case BUNKER:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_STATIONARY_ANTI_TANK);
                break;
            case REPRO:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_REPRODUCTION_CENTER);
                break;
            case BANK:
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_BANK);
                break;
            case NONE:
            default:
        }
    }
                
    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }
}
