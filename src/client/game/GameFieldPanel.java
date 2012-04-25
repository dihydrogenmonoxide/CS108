package client.game;

import client.data.GameObject;
import client.data.GamePhases;
import client.data.RunningGame;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;
import shared.game.MapManager;
import javax.swing.JTextField;

public class GameFieldPanel extends JPanel implements MouseListener
{

    /**
     * the client socket.
     */
    private Clientsocket socket;
    /**
     * GameFrame
     */
    GameFrame game;
    /**
     * image which holds the background.
     */
    private BufferedImage bgMap;
    private Dimension bgMapDim;
    
    private Image buffer;
    private Graphics bG;
    
    private boolean enableLogging;

    public GameFieldPanel(Clientsocket s, GameFrame gameFrame)
    {
        this.game = gameFrame;
        this.socket = s;
        this.setPreferredSize(this.getMaximumSize());

        this.setBackground(Color.BLUE);

        addMouseListener(this);

        //static framerate:
        Timer timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask()
        {

            public void run()
            {
                repaint();
            }
        }, 0, 100);
    }

    public void paintComponent(Graphics g)
    {
        //-- render the map on start or while resizing
        if (bgMap == null || bgMap.getWidth() != getWidth() || buffer == null)
        {
            Log.DebugLog("Map manager: rendered Map");
            bgMap = MapManager.renderMap(RunningGame.getMyFieldId(), this.getWidth());
            bgMapDim = new Dimension(bgMap.getWidth(), bgMap.getHeight());
            buffer = createImage(getWidth(), getHeight());
            bG = buffer.getGraphics();
        }
        drawToBuffer();
        
        //-- swap buffer
        g.drawImage(buffer, 0, 0, this);
    }

    private void drawToBuffer()
    {
        //--draw background
        bG.drawImage(bgMap, 0, 0, bgMap.getWidth(), bgMap.getHeight(), 0, 0, bgMap.getWidth(), bgMap.getHeight(), Color.BLACK, null);

        //-- draw all objects
        Collection<GameObject> c = RunningGame.getObjects().values();
        Iterator<GameObject> objIter = c.iterator();
        while (objIter.hasNext())
        {
            GameObject obj = objIter.next();
            BufferedImage objImg = obj.getImg();
            Dimension pixelCoords = Coordinates.coordToPixel(obj.getLocation(), bgMapDim);
            if (enableLogging)
            {
                Log.DebugLog("Map manager: x=" + obj.getLocation().getX() + " y=" + obj.getLocation().getY() + "berechnet: pixelX=" + pixelCoords.width + " pixelY=" + pixelCoords.height);
            }
            //-- draw image
            //-- width of the image
            int imageDim = 20;
            if (objImg != null)
            {
                bG.drawImage(objImg, pixelCoords.width - imageDim / 2, pixelCoords.height - imageDim / 2, 20, 20, null);
            }
        }
        
      
    }

    /**
     * sends a spawn request to the server.
     *
     * @param c   the Coordinates where to spawn
     * @param obj the object to spawn
     */
    public void spawnObject(int x, int y, Protocol obj)
    {
        Log.InformationLog("Trying to spawn Object: " + obj.str() + ", x=" + x + ", y=" + y + ", m_width: " + bgMap.getWidth() + ", m_heigth: " + bgMap.getHeight());
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, new Dimension(bgMap.getWidth(), bgMap.getHeight())));
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        //-- make a decision: show objectInfo or create Object:
        // TODO make this decision
        GameButtonsPanel.button choice = GameButtonsPanel.choice;
        Log.DebugLog("User clicked on the map at (" + e.getX() + "," + e.getY() + ") with the button choice: " + choice.toString());
        Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(e.getX(), e.getY(), bgMapDim));
        Log.DebugLog("sending request to create:" + choice);
        switch (GameButtonsPanel.choice)
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
                //TODO you cant add Bank in Panel
                spawnObject(e.getX(), e.getY(), Protocol.OBJECT_BANK);
                break;
            case NONE:
            default:
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
