package client.game;

import client.data.GameObject;
import client.data.RunningGame;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
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
    public static int MAP_HEIGHT = 0;
    /**
     * Buffered image to Paint Map
     */
    private BufferedImage backgroundMap;
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

        /*
         * durch das BufferedImage funktioniert es nocht nicht ganz so, wie es
         * sollte. wenn ihr schnell drückt könnt ihr es jedoch sehen;)
         */

        this.addMouseListener(this);

        //static framerate:
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {

            public void run()
            {
                repaint();
            }
        }, 0, 300);
    }

    public void paintComponent(Graphics g)
    {
        boolean logRedraw = false;
        try
        {
            //-- paint background
            g.drawImage(backgroundMap, 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), 0, 0, backgroundMap.getWidth(), backgroundMap.getHeight(), new Color(0, 0, 0), null);
            if (logRedraw)
            {
                Log.DebugLog("GameField: redrawing now!");
                Log.DebugLog("map width =" + MAP_WIDTH + " height=" + MAP_HEIGHT);
            }
            //-- paint objects
            Collection<GameObject> c = RunningGame.getObjects().values();
            Iterator<GameObject> objIter = c.iterator();
            while (objIter.hasNext())
            {
                GameObject obj = objIter.next();
                BufferedImage objImg = obj.getImg();
                Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), new Dimension(MAP_WIDTH, MAP_HEIGHT));
                if (logRedraw)
                {
                    Log.DebugLog("GameField: x=" + obj.getLocation().getX() + " y=" + obj.getLocation().getY() + "berechnet: pixelX=" + pixelCoords.width + " pixelY=" + pixelCoords.height);
                }
                //-- draw image

                //-- width of the image
                int imageDim = 20;


                if (objImg != null)
                {

                    g.drawImage(objImg, pixelCoords.width - imageDim / 2, pixelCoords.height - imageDim / 2, 20, 20, null);
                }
            }

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
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, new Dimension(MAP_WIDTH, MAP_HEIGHT)));
    }

    public void mousePressed(MouseEvent e)
    {
        Log.DebugLog("User clicked on the map at (" + e.getX() + "," + e.getY() + ") with the button choice: " + but.choice.toString());
        Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(e.getX(), e.getY(), new Dimension(MAP_WIDTH, MAP_HEIGHT)));
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
