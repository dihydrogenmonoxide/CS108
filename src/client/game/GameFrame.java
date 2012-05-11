package client.game;

import client.data.RunningGame;
import client.events.GameEvent;
import client.events.GameEventListener;
import client.events.NetEvent;
import client.net.Clientsocket;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import shared.Log;
import shared.Protocol;
import shared.Settings;

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
    final static int screenX = screen.getDisplayMode().getWidth();
    /**
     * actual height of the screen.
     */
    final static int screenY = screen.getDisplayMode().getHeight();	
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
        super();
        this.socket = s;
        this.lobby = lobbyParent;

        game = new JFrame("SwissDefcon Game");

        //-- add content
        innerGameFrame = new InnerGameFrame(this, socket);
        
        
        game.setBackground(Color.black);
        
        
        game.setContentPane(innerGameFrame);
       
        
        
        //-- set size and location
        try
        {
            
            
            //now set fullscreen
            if (screen.isFullScreenSupported() && Settings.FULLSCREEN)
            {
                game.setUndecorated(true);
                screen.setFullScreenWindow(game);
            }
            else
            {
                throw new Exception();
            }
        } catch (Exception e)
        {
            Log.ErrorLog("Sorry, no fullscreen for you");
             game.setSize(new Dimension(1000,600));
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
              closeGame();  
            }    
        });
        
        socket.addGameEventListener(new GameEventListener(){

            @Override
            public void received(GameEvent evt)
            {
                if(evt.getType() == Protocol.GAME_LOST_OR_WON)
                {
                   String msg;  
                   if(evt.getMsg().getIntArgument(1) == 0)
                    {
                        msg = "Du hast gewonnen";
                    }else{
                        msg = "Du hast verloren";
                    }
                    JOptionPane.showMessageDialog(lobbyParent, msg, "Spiel fertig", JOptionPane.INFORMATION_MESSAGE);
                    closeGame();
                }
            }

            @Override
            public void received(NetEvent evt)
            {
            }
            }
        );
        
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
