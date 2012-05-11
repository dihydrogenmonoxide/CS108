package client.lobby;


import client.events.*;
import client.game.GameFrame;
import client.net.Clientsocket;
import client.resources.ResourceLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import shared.Log;
import shared.Protocol;
import shared.ServerAddress;
import shared.User;


/** this is the lobby at the start.
 * it allows joining a server, chatting with users, joining a game.
 * @ param none
 */
public class ClientLobby extends JFrame {
	/**actual width of the screen.*/
	private int screenX;
	/**actual height of the screen.*/
	private int screenY;

	/**JFrame which contains the GUI for the Lobby.*/
	private JFrame lobbyParent;
	/**width of the lobby in pixel.*/
	private int iLobbyX = 900; 
	/**height of the lobby in pixel.*/
	private int iLobbyY = 575;

	/**The Server Selecting Dialog.*/
	private SelectServer s;
	/**the Connection made to the Server.*/
	private Clientsocket socket;
	/**the Lobby.*/
	private InnerLobby l;
	/**the User which holds all User based Infos.*/
	private User user;
	
	/**Frame which contains the GUI for the Game.*/
	private GameFrame game;

	/**check if the player is in a game.*/
	private boolean isInGame = false;
	
        private ResourceLoader res = new ResourceLoader();
	
	/**creates the lobby.*/
	public ClientLobby(User u, ServerAddress addressServer)
	{
                super();
		this.user = u;

		/*
		 * Get Infos about the screen
		 * */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();
		DisplayMode disp = screen.getDisplayMode();
		screenX = disp.getWidth();
		screenY = disp.getHeight();
		


		JPanel bg = createBackground();
		
		/*
		 * Set up lobby / inputs
		 * */
		lobbyParent = new JFrame("SwissDefcon Lobby");
		lobbyParent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lobbyParent.setSize(iLobbyX, iLobbyY);
		lobbyParent.setResizable(false);
		lobbyParent.setLocation(screenX / 2 - iLobbyX / 2, screenY / 2 - iLobbyY / 2);
		lobbyParent.setContentPane(bg);
		
		s = new SelectServer(user);
		s.addServerSelectedListener(new ServerSelectedListener()
		{
			public void serverSelected(final ServerSelectedEvent ev)
			{
				ServerAddress server = ev.getServer();
				String desiredNick = ev.getUsername();
				try
				{
					Log.InformationLog("-->Connecting to " + ev.getServer().getServerName() + "(" + server.getAddress().getHostAddress() + ") as " + ev.getUsername() + "(desired Username)");
					
					//stop discovery
					s.stopSearch();
					s.setVisible(false);
					
					//make Connection
					socket = new Clientsocket(server);
					//JOptionPane.showMessageDialog(lobbyParent, "Verbunden mit Server");
					
					l = new InnerLobby(socket, user, lobbyParent);
					lobbyParent.add(l);
					
					//request nick
					socket.sendData(Protocol.CON_NICK.str() + desiredNick);
					
					//get my id
					socket.sendData(Protocol.CON_MY_ID.str());
					
					socket.addGameEventListener(new GameEventListener()
					{
						public void received(GameEvent evt) 
						{
							if (evt.getType() == Protocol.GAME_BEGIN)
							{
								lobbyParent.setVisible(false);
								game = new GameFrame(lobbyParent, socket);
							}
						}
						@Override
						public void received(NetEvent evt) {
						}	
					});
					
					socket.addInfoEventListener(new InfoEventListener()
					{
						@Override
						public void received(final InfoEvent evt)
						{
							 if (evt.getId() == -1)
							 {
								Log.InformationLog("Connection to server broken, starting ServerSelect");
								JOptionPane.showMessageDialog(lobbyParent, "Verbindungsunterbruch", "Connection Error", JOptionPane.ERROR_MESSAGE);
								if (game != null)
								{	
									game.closeGame();
									game = null;
								}
								socket.disconnect();
								lobbyParent.remove(l);
								lobbyParent.validate();
								lobbyParent.repaint();
								s.setVisible(true);
								s.startSearch();
							 }
						}
					});
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.WarningLog("-->connection broken, could not connect");
					JOptionPane.showMessageDialog(lobbyParent, "Konnte nicht mit Server verbinden: ", "Connection Error", JOptionPane.ERROR_MESSAGE);
					if (socket != null) { socket.disconnect(); }
					s.setVisible(true);
					s.startSearch();
					
				}
			}
		});
		lobbyParent.add(s);
		Log.InformationLog("you can now select a server");    
                
                //-- if a server is given, connect to the suggested server
                if(addressServer != null)
                {
                    s.serverSelected(new ServerSelectedEvent("Server selected", addressServer , u.getUserName()));
                }

		lobbyParent.setVisible(true);
	}

    public ClientLobby(User user)
    {
        this(user, null);
    }
	
	

	/**
	 * Set up background.
	 * The bg-image is drawn in a JPanel which is laid under all the other panes with User-IO
	 * @return bg a panel with the desired background.
	 */
	private JPanel createBackground() {
		
		JPanel bg = new JPanel()
		{
			private static final long serialVersionUID = 1L;
			
			/**paint the swiss map in the background*/
			public void paintComponent(Graphics g)
			{
				BufferedImage img = null;
				try 
				{
					img = ImageIO.read(res.load("images/lobby_bg.jpg"));
					g.drawImage(img, 0, 0, iLobbyX, iLobbyY, 0, 0, img.getWidth(), img.getHeight(), new Color(0, 0, 0), null);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};
		return bg;
	}



}


