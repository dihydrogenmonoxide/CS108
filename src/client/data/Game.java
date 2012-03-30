package client.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import shared.Log;
/**holds all the infos about a game.*/
class Game {
	/**the id of the game.*/
	private int id;
	
	/**which players are in there.*/
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	/**the name of game ^^.*/
	private String name;


	/**initializes the game info from the Eventparser.
	 * @param gameId the id of the game.
	 * @param gameName the name of the game.
	 * */
	public Game(final int gameId, final String gameName)
	{
		this.id = gameId;
		this.name = gameName;
		Log.DebugLog("Game created: " + this.toString());
	}
	
	/**returns an Vector containing the id, the playercount and the name.
	 * @return the vector for the GUI.*/
	public Vector<String> makeVector()
	{
		Vector<String> r = new Vector<String>();
		r.add(String.valueOf(id));
		r.add(String.valueOf(players.size()));
		r.add(name);
		return r;
	}
	
	/**returns the id of the game.
	 * @return id the id*/
	public int getId()
	{
		return id;
	}
	
	/**returns how many players are in this game.
	 * @return playerCount how many Players are in the game.*/
	public int getPlayerCount()
	{
		return players.size();
	}
	
	/**converts this to a String for logging.
	 * @return String representation.*/
	public String toString()
	{
		return id + " " + players.size() + " " + name;
	}

	/**adds a player to a game.
	 * @param p the player to add to the game.*/
	public void addPlayer(final Player p) 
	{
		Log.DebugLog("Game: player added to game " + name + ":" + p.getName());
		players.put(p.getId(), p);
	}
	
	/**removes a player from a game.
	 * @param playerId the message received by the parser.*/
	public void removePlayer(final int playerId) 
	{
		players.remove(playerId);
		if (players.size() <= 0)
		{
			//XXX GamesManager.removeGame(this.id);
		}
	}
	
	/**make a String[] containing everything about a game.
	 * outline : int id, String name, int playerCount,  player 1, player 2, ....
	 * @return a vector containing everything.*/
	public String[] makeLongInfo()
	{
		String[] s = new String[3 + players.size()];
		s[0] = String.valueOf(this.id);
		s[1] = this.name;
		s[2] = String.valueOf(players.size());
		
		//iterate through all players
		Collection<Player> c = players.values();
		Iterator<Player> pIter = c.iterator();
		int count = 0;
		while (pIter.hasNext())
		{
			Player p = pIter.next();
			s[3 + count] = p.getName();
			count++;
		}
		return s;
	}

	/**set the Name of the game.
	 * @param s the name of the game.*/
	public void setName(final String s) 
	{
		this.name = s;
	}

	/**return the name of the game.
	 * @return name the name of the game.*/
	public String getName() {
		return name;
	}
	
}
