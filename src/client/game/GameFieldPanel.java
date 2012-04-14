package client.game;

import client.data.RunningGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import client.net.Clientsocket;
import shared.game.MapManager;
import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;

public class GameFieldPanel extends JPanel implements MouseListener
{
	public  final int MAP_WIDTH;
    public final int MAP_HEIGHT;
    /**
     * Buffered image to Paint Map
     */
    private BufferedImage img;
    /**
     * Image for DoubleBufferedImage
     */
    private Image dbImage;
    private Graphics dbg;
    private GameButtonsPanel but;
    private Clientsocket socket;
    private Image bil;

    public GameFieldPanel(Clientsocket s, int MAP_WIDTH, int MAP_HEIGHT)
    {
    	this.MAP_WIDTH= MAP_WIDTH;
    	this.MAP_HEIGHT= MAP_HEIGHT;
        
        
        if(MAP_HEIGHT!=0){
        	img = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_HEIGHT);
        	MAP_WIDTH = img.getWidth();
        }
        
        
        //TODO decide which field to highlight and which are inactive.
        else{
        	img = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_WIDTH);
        	MAP_HEIGHT = img.getHeight();
        }
        
        
        this.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));

        //but = new GameButtonsPanel(socket);
        //but.selected();
		/*
         * durch das BufferedImage funktioniert es nocht nicht ganz so, wie es
         * sollte. wenn ihr schnell drückt könnt ihr es jedoch sehen;)
         */

        this.addMouseListener(this);
    }

    public void paintComponent(Graphics g)
    {
        try
        {
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), 0, 0, img.getWidth(), img.getHeight(), new Color(0, 0, 0), null);
            //repaint();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g)
    {
        dbImage = createImage(getWidth(), getHeight());
        dbg = dbImage.getGraphics();
        paintComponent(dbg);
        g.drawImage(dbImage, 0, 0, this);


    }

    public void mousePressed(MouseEvent e)
    {
        Log.DebugLog("User clicked on the map at (" + e.getX() + "," + e.getY() + ") with the button choice: " + but.choice.toString());
        Log.DebugLog("this point has the coordinates: " + Coordinates.pixelToCoord(e.getX(), e.getY(), MAP_WIDTH, img.getHeight()));
        Log.DebugLog("sending request to create:" + but.choice);
        switch (but.choice)
        {
            case TANK:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_TANK);
                break;
            case FIGHTER:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_FIGHTER_JET);
                break;
            case BOMBER:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_BOMBER);
                break;
            case ANTIAIR:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_STATIONARY_ANTI_AIR);
                break;
            case BUNKER:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_STATIONARY_ANTI_TANK);
                break;
            case REPRO:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_REPRODUCTION_CENTER);
                break;
            case BANK:
                spawnObject(e.getX(),e.getY(),Protocol.OBJECT_BANK);
                break;
            case NONE:
            default:
        }
    }
    /**sends a spawn request to the server.
     @param c the Coordinates where to spawn
     @param obj the object to spawn*/
    public void spawnObject(int x, int y, Protocol obj)
    {
        socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + obj.str() + Coordinates.pixelToCoord(x, y, MAP_WIDTH, MAP_HEIGHT));
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
