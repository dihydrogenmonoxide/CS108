package client.game;

import client.data.RunningGame;
import client.net.Clientsocket;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import shared.Log;
import shared.Protocol;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 27.09.2011
 * @author
 */
public class GameFrame
{
    /**reference to myself for listeners*/
    public GameFrame GameFrame;
    
    /**the actual screen.*/
    private final static GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    /**
     * actual width of the screen.
     */
    private final static int screenX = screen.getDisplayMode().getWidth();
    /**
     * actual height of the screen.
     */
    private final static int screenY = screen.getDisplayMode().getHeight();
    ;	
	/**the Connection to listen to*/
	private Clientsocket socket;
        
        /**reference to the lobby*/
        private JFrame lobby;
        /**reference to the Frame the game is running in*/
        private JFrame game;
    /**
     * Frame for Game
     */
    private InnerGameFrame innerGameFrame;

    public GameFrame(final JFrame lobbyParent, Clientsocket s)
    {
        this.socket = s;
        this.lobby = lobbyParent;

        game = new JFrame("SwissDefcon Game");

        //-- add content
        innerGameFrame = new InnerGameFrame(this, socket);
        
        
        game.setBackground(Color.CYAN);
        
        
        game.setContentPane(innerGameFrame);
       
        
        
        //-- set size and location
        try
        {
            
            //now set fullscreen
            if (screen.isFullScreenSupported())
            {
                game.setUndecorated(true);
                screen.setFullScreenWindow(game);
            }
            
        } catch (Exception e)
        {
            Log.ErrorLog("Sorry, no fullscreen for you");
             game.setSize(game.getPreferredSize());
             game.setLocation(screenX / 2 - game.getWidth() / 2, screenY / 2 - game.getHeight() / 2);
        }

        game.setPreferredSize(game.getMaximumSize());
        game.revalidate();
        
        game.setVisible(true);

        /**
         * WindowClosingEvent to return to InnerLobby
         */
        game.addWindowListener(new WindowAdapter()
        {

            public void windowClosing(WindowEvent e)
            {
              GameFrame.closeGame();  
            }    
        });
        //TODO listener which closes the game if something unforeseen happens.
        //would recommend an infoEventListener
        
    }
    
    /**gets you the Frame the game is in.*/
    public JFrame getFrame(){
        return game;
    }
    
    public void closeGame()
            {
                screen.setFullScreenWindow(null);
                game.dispose();
                RunningGame.hardReset();
                socket.sendData(Protocol.GAME_QUIT.str());
                lobby.setVisible(true);
            } 
}
