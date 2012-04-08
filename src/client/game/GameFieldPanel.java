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

public class GameFieldPanel extends JPanel implements MouseListener
{

    private static final int MAP_WIDTH = 1000;
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

    public GameFieldPanel(Clientsocket s)
    {
        this.socket = s;

        //TODO decide which field to highlight and which are inactive.
        img = MapManager.renderMap(RunningGame.getMyFieldId(), MAP_WIDTH);


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
        switch (but.choice)
        {
            case TANK:
            case FIGHTER:
            case BOMBER:
            case ANTIAIR:
            case BUNKER:
            case RADAR:
            case REPRO:
            case BANK:
            case NONE:
            
            
            //XXX this is just for testing purposes:
            default:
                
                socket.sendData(Protocol.GAME_SPAWN_OBJECT.str() + Protocol.OBJECT_BANK.str() + pixelToCoordX(e.getX()) + " " + pixelToCoordY(e.getY()));
                Graphics g = getGraphics();
                int x = e.getX();
                int y = e.getY();
                try
                {
                    bil = ImageIO.read(new File("bilder/Bank.png"));
                    g.drawImage(bil, x, y, 20, 20, null);
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                
            //TODO send a request to the server.
             
           
            /*
            * How to solve this:
            * if player clicks on the map, send a request to the server
            * don't draw anything.
            * the server will then send us that a new object is created which is then drawn.
            * to do this we will have to make a class for each drawable object 
            * with it's unique draw/paint method.
            */

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
    
    /**converts pixel coordinates of the X axis to swiss map coordinates.*/
    public int pixelToCoordX(int x)
    {
        /*Das schweizer koordinatennetz geht von O-W von 450000 bis 800000.*/
        int coordStart = 450000;
        int coordEnd = 800000;
        int coordDelta = coordEnd - coordStart;        
        return coordStart + coordDelta*x/MAP_WIDTH;
        
    }
    
    /**converts pixel coordinates of the Y axis to swiss map coordinates.*/
    public int pixelToCoordY(int y)
    {
        /*Das schweizer koordinatennetz geht von S-N von 100000 bis 300000.*/
        int coordStart = 100000;
        int coordEnd = 300000;
        int coordDelta = coordEnd - coordStart;        
        return coordStart + coordDelta*y/img.getHeight();
    }
}
