package client.lobby;

import client.data.GamesManager;
import client.data.PlayerManager;
import client.events.LobbyEvent;
import client.events.LobbyEventListener;
import client.events.NetEvent;
import client.game.GameFrame;
import client.net.Clientsocket;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import shared.Log;
import shared.Protocol;

/**
 * a panel which offers the option to join and list games. You can also create games.
 */
public class GamesPanel extends JPanel
{

    /**
     * serial id, obviously must have ^^. but never pi.
     */
    private static final long serialVersionUID = 314159265358979L;
    /**
     * the connection socket.
     */
    private Clientsocket socket;
    /**
     * button to join or leave a game.
     */
    private JButton toggleButton;
    /**
     * holding the info for the UI, just a simplified version of games.
     */
    private Vector<Vector<String>> gamesData = new Vector<Vector<String>>();
    /**
     * table with all the games.
     */
    private JTable gamesTable;
    /**
     * scrollable list for the games.
     */
    private JScrollPane gamesScroll;
    /**
     * whether the Player has joined a game or not.
     */
    private boolean isJoined = false;
    /**
     * the number of the joined game.
     */
    private int joinedGame = 0;
    /**
     * game name about the joined game.
     */
    private JList<String> infoName = new JList<String>();
    /**
     * vector holding the infos.
     */
    private Vector<String> infoNameV = new Vector<String>();
    /**
     * player count of the joined game.
     */
    private JList<String> infoCount = new JList<String>();
    /**
     * vector holding the infos.
     */
    private Vector<String> infoCountV = new Vector<String>();
    /**
     * Player of the joined game.
     */
    private JList<String> infoPlayers = new JList<String>();
    /**
     * vector holding the infos.
     */
    private Vector<String> infoPlayersV = new Vector<String>();
    /**
     * Panel which hold all the infos of a joined game.
     */
    private JPanel gameInfo;
    /**
     * Panel to create a new game.
     */
    private JPanel createPanel;
    /**
     * Frame which contains the GUI for the Game.
     */
    public GameFrame game;
    private JTextField newGameName = new JTextField();
    private JFrame lobbyParent;

    /**
     * creates a dialog where the user can join, create and start games.
     *
     * @param s           the socket used for the connection.
     * @param lobbyParent the parent of the lobby.
     */
    public GamesPanel(final Clientsocket s, final JFrame lobbyParent)
    {
        this.socket = s;
        this.lobbyParent = lobbyParent;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        makeGameChooser(c);
        makeGameCreator(c);

        this.setOpaque(false);

        /*
         * This code is a bit dirty ^^ here is what it makes: if you have joined
         * a game it updates the joined Game or otggles to the All Games view if
         * you quit the game.
         *
         * if you have not joined the game, it checks if you created a game and
         * gets you to the game view of this game.
         */
        socket.addLobbyEventListener(new LobbyEventListener()
        {

            @Override
            public void received(final LobbyEvent evt) throws Exception
            {
                Log.DebugLog("GameList: " + evt.getSection().str() + " " + evt.getMsg() + "; " + evt.getMsg().getCommand());
                switch (evt.getSection())
                {
                    case LOBBY_UPDATE:
                        refreshGameList();
                        if (isJoined)
                        {
                            refreshJoinedGame(joinedGame);
                            if (evt.getMsg().getCommand() == Protocol.GAME_QUIT)
                            {
                                try
                                {
                                    int playerId = evt.getMsg().getIntArgument(2);
                                    Log.DebugLog(playerId + "");
                                    if (PlayerManager.myId() == playerId)
                                    {
                                        displayAllGames();
                                        Log.DebugLog("quit the game");
                                    }
                                    refreshGameList();
                                } catch (Exception e)
                                {
                                    Log.ErrorLog("GameList: wrong formatted message");
                                }
                            }
                        } else
                        {
                            if (evt.getMsg().getCommand() == Protocol.GAME_JOIN)
                            {
                                try
                                {
                                    int playerId = evt.getMsg().getIntArgument(2);
                                    Log.DebugLog(playerId + "");
                                    if (PlayerManager.myId() == playerId)
                                    {
                                        joinedGame = evt.getMsg().getIntArgument(1);
                                        refreshJoinedGame(joinedGame);
                                        displayJoinedGame();
                                        Log.DebugLog("You created a game: " + joinedGame);
                                    }
                                } catch (Exception e)
                                {
                                    Log.ErrorLog("GameList: wrong formatted message");
                                }
                            }
                        }

                        break;
                    default:
                        break;
                }
            }

            //TODO Listener which frees the GUI when one could not join a game.
            @Override
            public void received(final NetEvent evt)
            {
            }
        });
    }

    /**
     * This function creates an UI where the player can create new games.
     *
     * @param c the layoutmanager
     */
    private void makeGameCreator(GridBagConstraints c)
    {

        JButton startButton;

        createPanel = new JPanel();
        createPanel.setBackground(new Color(255, 255, 255));
        createPanel.setOpaque(true);
        createPanel.setForeground(new Color(50, 50, 50));
        createPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
        createPanel.setSize(new Dimension(300, 300));
        createPanel.setPreferredSize(new Dimension(300, 150));
        createPanel.setLayout(new BoxLayout(createPanel, BoxLayout.PAGE_AXIS));
        c.fill = GridBagConstraints.CENTER;
        c.gridwidth = 4;
        c.gridheight = 10;
        c.gridx = 0;
        c.gridy = 24;
        this.add(createPanel, c);


        /*
         * set the name:
		 *
         */
        JLabel lblName = new JLabel("Spielname: ");
        newGameName.setPreferredSize(new Dimension(100, 20));

        JPanel name = new JPanel();
        name.add(lblName);
        name.add(newGameName);
        createPanel.add(name);


        /*
         * create the dialog to choose the difficulty:
		 *
         */

        JLabel title = new JLabel("Wähle den Schwierigkeitsgrad:");
        title.setHorizontalTextPosition(SwingConstants.CENTER);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(300, 30));

        JRadioButton normal = new JRadioButton("normal");
        normal.setMnemonic(KeyEvent.VK_B);
        normal.setPreferredSize(new Dimension(100, 30));
        normal.setActionCommand("normal");
        normal.setSelected(true);

        JRadioButton middle = new JRadioButton("mittel");
        middle.setMnemonic(KeyEvent.VK_C);
        middle.setHorizontalAlignment(SwingConstants.CENTER);
        middle.setPreferredSize(new Dimension(100, 30));
        middle.setActionCommand("mittel");

        JRadioButton chuckNorris = new JRadioButton("Chuck Norris");
        chuckNorris.setMnemonic(KeyEvent.VK_R);
        //chuckNorris.setPreferredSize(new Dimension(100, 30));
        chuckNorris.setActionCommand("Chuck Norris");

        ButtonGroup group = new ButtonGroup();
        group.add(normal);
        group.add(middle);
        group.add(chuckNorris);

        JPanel difficulty = new JPanel();
        difficulty.setLayout(new BorderLayout());
        difficulty.add(BorderLayout.NORTH, title);
        difficulty.add(BorderLayout.WEST, normal);
        difficulty.add(BorderLayout.CENTER, middle);
        difficulty.add(BorderLayout.EAST, chuckNorris);
        createPanel.add(difficulty);


        /*
         * the buttons
		 *
         */

        JButton createButton = new JButton("erstellen");

        startButton = new JButton("Spiel starten");

        JPanel buttons = new JPanel();
        buttons.add(createButton);
        buttons.add(startButton);

        createPanel.add(buttons);


        createButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(final ActionEvent e)
            {
                if (newGameName.getText() != null && 2 < newGameName.getText().length())
                {
                    socket.sendData(Protocol.GAME_MAKE.str() + newGameName.getText());
                }
            }
        });

        //TODO remove this after testing, game should start automatic.
        // game should then be started from an GameEvent listener in the ClientLobby. (therefore the lobbyParent parameter is not needed then.)
        startButton.addActionListener(new ActionListener()
        {

            public void actionPerformed(final ActionEvent e)
            {
                game = new GameFrame(lobbyParent, socket);
                lobbyParent.setVisible(false);
            }
        });


    }

    /**
     * Creates the Panel in which a user can choose a game and join it.
     *
     * @param c
     */
    private void makeGameChooser(GridBagConstraints c)
    {
        /*
         * Create the Listing with all the Games
		 *
         */

        Vector<String> columns = new Vector<String>();
        columns.add("ID");
        columns.add("Spieler");
        columns.add("Name");

        gamesTable = new JTable(gamesData, columns)
        {
            //hack to disable editing

            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(final int rowIndex, final int vColIndex)
            {
                return false;
            }
        };
        gamesTable.setCellSelectionEnabled(false);
        gamesTable.setRowSelectionAllowed(true);
        gamesTable.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        gamesTable.setFillsViewportHeight(true);

        gamesScroll = new JScrollPane(gamesTable);
        gamesScroll.setPreferredSize(new Dimension(300, 80));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 50;
        c.ipadx = 50;
        c.weightx = 0.0;
        c.gridwidth = 4;
        c.gridheight = 10;
        c.gridx = 0;
        c.gridy = 0;
        this.add(gamesScroll, c);

        /*
         * Create the Panel with all the Infos if the player joined the game.
		 *
         */

        gameInfo = new JPanel();
        gameInfo.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
        gameInfo.setBackground(new Color(255, 255, 255));
        gameInfo.setOpaque(true);
        gameInfo.setForeground(new Color(50, 50, 50));
        gameInfo.setVisible(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 11;
        c.insets = new Insets(10, 0, 0, 0);
        this.add(gameInfo, c);


        JButton voteButton = new JButton();
        voteButton.setText("ich bin bereit");
        voteButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                socket.sendData(Protocol.GAME_VOTESTART.str());
            }
        });


        infoName.setListData(infoNameV);
        infoCount.setListData(infoCountV);
        infoPlayers.setListData(infoPlayersV);

        //hack to disable selection.
        infoName.setCellRenderer(new DefaultListCellRenderer()
        {

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });
        infoCount.setCellRenderer(new DefaultListCellRenderer()
        {

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });
        infoPlayers.setCellRenderer(new DefaultListCellRenderer()
        {

            public Component getListCellRendererComponent(JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, false, false);
                return this;
            }
        });

        gameInfo.setPreferredSize(new Dimension(300, 65));
        gameInfo.setLayout(new BorderLayout(50, 0));
        gameInfo.add(BorderLayout.WEST, infoName);
        gameInfo.add(BorderLayout.CENTER, infoCount);
        gameInfo.add(BorderLayout.EAST, infoPlayers);
        gameInfo.add(BorderLayout.SOUTH, voteButton);

        gameInfo.validate();

        /*
         * Create the toggle button
		 *
         */
        toggleButton = new JButton("beitreten");
        c.fill = GridBagConstraints.HORIZONTAL;
        toggleButton.setEnabled(false);
        c.ipady = 0;
        c.gridwidth = 4;

        c.gridx = 0;
        c.gridy = 14;
        this.add(toggleButton, c);

        JSeparator separator = new JSeparator();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 16;
        this.add(separator, c);

        // LISTENERS
        gamesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(final ListSelectionEvent e)
            {
                if (0 < gamesTable.getSelectedRow())
                {
                    Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
                    String[] info = GamesManager.getInfo(Integer.valueOf(temp.get(0)));
                    if (info != null)
                    {
                        //do nothing
                    } else
                    {
                        //do nothing
                    }
                    //gameSettings.setText(temp.get(0) + " : " + temp.get(1) + " : " + temp.get(2));
                }
            }
        });

        toggleButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(final ActionEvent e)
            {


                if (!isJoined)
                {
                    if (0 <= gamesTable.getSelectedRow())
                    {
                        //get the game which was joined
                        Vector<String> temp = gamesData.get(gamesTable.getSelectedRow());
                        joinedGame = Integer.valueOf(temp.get(0));

                        //send the command
                        socket.sendData(Protocol.GAME_JOIN.str() + joinedGame);

                        //toggle
                        displayJoinedGame();
                    }
                } else
                {
                    socket.sendData(Protocol.GAME_QUIT.str());
                    displayAllGames();
                }
            }
        });
    }

    /**
     *
     */
    private void displayAllGames()
    {
        gameInfo.setVisible(false);
        gamesScroll.setVisible(true);
        toggleButton.setText("beitreten");
        createPanel.setVisible(true);
        isJoined = false;
    }

    /**
     *
     */
    private void displayJoinedGame()
    {
        refreshJoinedGame(joinedGame);
        gameInfo.setVisible(true);
        gamesScroll.setVisible(false);
        toggleButton.setText("verlassen");
        createPanel.setVisible(false);
        isJoined = true;
    }

    /**
     * refreshes a game to the list. Checks if there are games with 0 players
     * and deletes them
	 *
     */
    private void refreshGameList()
    {
        Log.DebugLog("GamesPanel: refresh game list.");
        gamesData = GamesManager.makeVector();
        Log.DebugLog("-->repaint, How many games here: " + gamesData.size());
        updateGameTable();
    }

    /**
     * updates the gameTable.
     */
    public final void updateGameTable()
    {
        Vector<String> columns = new Vector<String>();
        columns.add("ID");
        columns.add("Spieler");
        columns.add("Name");


        DefaultTableModel model = (DefaultTableModel) gamesTable.getModel();

        if (gamesData != null && 0 < gamesData.size())
        {
            Log.DebugLog("paint gamelist");
            model.setDataVector(gamesData, columns);
        } else
        {
            Log.DebugLog("empty gamelist");
            model.setDataVector(new Vector<Vector<String>>(), columns);
        }

        gamesTable.revalidate();
        gamesTable.updateUI();
        gamesTable.repaint();
        if (0 < gamesTable.getRowCount())
        {
            toggleButton.setEnabled(true);
        } else
        {
            toggleButton.setEnabled(false);
        }
    }

    /**
     * @param gameId
     */
    private void refreshJoinedGame(int gameId)
    {
        //set the infos
        infoNameV.clear();
        infoCountV.clear();
        infoPlayersV.clear();

        String[] infos = GamesManager.getInfo(gameId);
        if (infos != null)
        {
            infoNameV.add("Spiel:");
            infoNameV.add(infos[1]);
            infoCountV.add("Mitspieler:");
            infoCountV.add(infos[2]);
            gameInfo.setPreferredSize(new Dimension(300, 15 + (7 * infos.length - 3)));

            for (int i = 0; i < infos.length - 3; i++)
            {
                infoPlayersV.add(infos[3 + i]);
                Log.DebugLog("added" + infos[3 + i]);
            }
        } else
        {
            //your game doesn't exist anymore
            displayAllGames();
        }



        infoName.updateUI();
        infoCount.updateUI();
        infoPlayers.updateUI();
    }
}
