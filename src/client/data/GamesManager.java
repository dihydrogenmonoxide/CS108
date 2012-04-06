package client.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import shared.Log;

/**class which holds all the games the client knows about.
 * all the methos and variables are static.*/
public class GamesManager {
	/**Hashmap which holds all the games.
	 * key: the id of the game.*/
	private static ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<Integer, Game>();

	/**adds a game to the list. 
	 * Creates a new Game if the id doesn't exist. otherwise will only update the name.
	 * @param id the id of the game.
	 * @param name the name of the game.
	 * */
	public static void addGame(final int id, final String name)
	{
		if (games.get(id) == null)
		{
			Log.DebugLog("GameManager added game to list: " + id + " : " + name);
			Game g = new Game(id, name);
			games.put(id, g);
		}
		else
		{
			Log.DebugLog("GameManager updated game: " + id + " : " + name);
			games.get(id).setName(name);
		}
	}
	/**same as above, just a wrapper for String.
	 * String format: "201 2 Hanspeter", id count name
	 * @param msg the string describing the game
	 * */
	@SuppressWarnings("unused")
	public static void addGame(final String msg)
	{
		int id = Integer.valueOf((String) msg.subSequence(6, 9));
		//XXX playerCount generated automatic
		int playerCount = Integer.valueOf((String) msg.subSequence(10, 11));
		String name = msg.substring(12);
                addGame(id, name);
                if(playerCount == 0)
                {
                    games.get(id).setVisible(false);
                    Log.DebugLog("GameManager: set " + id + " invisible");
                }
                else
                {
                    games.get(id).setVisible(true);
                }
                
	}

	/**adds a player to a game.
	 * only accessable in this packet.
	 * @param gameId the id of the game
	 * @param p the Player to add.*/
	static void addPlayer(final int gameId, final Player p)
	{
		Game g = games.get(gameId);
		if (g != null)
		{
			g.addPlayer(p);
		}
	}

	/**remove a player from the game.
	 * @param gameId the gameId of the game.
	 * @param playerId the id of the player to remove.*/
	public static void removePlayer(final int gameId, final int playerId)
	{
		Game g = games.get(gameId);
		if (g != null)
		{
			g.removePlayer(playerId);
		}
	}
	/**get Infos about a game.
	 * @param gameId the id of the game.
	 * @return */
	public static String[] getInfo(final int gameId)
	{
		Game g = games.get(gameId);
		if (g != null)
		{
			return g.makeLongInfo();
		}
		return null;
	}
	/**removes a game from the list.
	 * @param id the gameId*/
	public static void removeGame(final int id) 
	{
		games.remove(id);
	}

	/**generate a Vector<Vector<String>> used for display all games in a JTable.
	 * @return res the Vector holding all the Vecotrs with the infos about the games */
	public static Vector<Vector<String>> makeVector()
	{
		Vector<Vector<String>> res = new Vector<Vector<String>>();

		Collection<Game> c = games.values();
		Iterator<Game> gIter = c.iterator();

		//iterate through all games
		while (gIter.hasNext())
		{
			Game g = gIter.next();
			//remove all empty games
			if (0 < g.getPlayerCount() && g.getVisible())
			{
				Vector<String> v = g.makeVector();
				Log.DebugLog("-" + v.get(0) + ":" + v.get(1) + ":" + v.get(2));
				res.add(v);
			}
		}
		Log.DebugLog("->list refreshed, holding " + res.size() + " games");
		return res;
	}
	/**
	 * adds a player to a game.
	 * Wrapper for all classes not in this packet.
	 * @param gameId the id of the game.
	 * @param playerId the id of the player.*/
	public static void addPlayer(final String gameId, final String playerId) {
		Log.DebugLog("GameManager added Player " + playerId + ":" + PlayerManager.getNamebyId(playerId) + " to game " + gameId);
		addPlayer(Integer.valueOf(gameId), PlayerManager.getPlayerbyId(Integer.valueOf(playerId)));
	}
	/**
	 * removes a player from a game.
	 * Wrapper for all classes not in this packet.
	 * @param gameId the id of the game.
	 * @param playerId the id of the player.*/
	public static void removePlayer(final String gameId, final String playerId) {
		Log.DebugLog("GameManager removed Player " + playerId + " from game " + gameId);
		removePlayer(Integer.valueOf(gameId), Integer.valueOf(playerId));
	}
}
