package client.data;

import java.util.concurrent.ConcurrentHashMap;

import shared.Log;
/**this class holds all the Players known to the client.
 * all variables and methods are static.*/
public class PlayerManager {
	/**the Hashmap where all the players are saved in. 
	 * key is the playerId.*/
	private static ConcurrentHashMap<Integer, Player> players= new ConcurrentHashMap<Integer, Player>();
	
	/**remove a player from the Hashmap.
	 * (likely not to be used)
	 * @param playerId the id of the player.*/
	public static void removePlayer(int playerId){
		if(199<playerId && playerId<100){
			Log.ErrorLog("tried to remove invalid player");
			return;
		}
		players.remove(playerId);
	}
	/**same as above, wrapper for String.
	 * @param s String of the id of the player.*/
	public static void removePlayer(String s)
	{
		int playerId = Integer.valueOf(s);
		removePlayer(playerId);
	}
	/**Adds a player to the Hashmap.
	 * If the player already exists, his entry will be overwritten.
	 * @param playerId the Id of the player.
	 * @param playerName the name of the player.*/
	public static void addPlayer(int playerId, String playerName)
	{
		if(199<playerId && playerId<100){
			Log.ErrorLog("tried to add invalid player");
			return;
		}
		Player p = new Player(playerId, playerName);
		players.put(p.getId(), p);
	}
	/**wrapper for Strings.
	 * String format: "101 Hans"  id, name
	 * @param id the id as String
	 * @param name the playername as String.*/
	public static void addPlayer(String id, String name) 
	{
		addPlayer(Integer.valueOf(id), name);
	}
	/**return the name of a player with the given id.
	 * If the player doesn't exist return null.
	 * @param id the id of the player
	 * @return the name of the player*/
	public static  String getNamebyId(final String id) 
	{
		int i = Integer.valueOf(id);
		if (players.get(i) != null)
		{
			return players.get(i).getName();
		}
		return null;
	}
	/**return the Id of a player with the given name.
	 * @param name the name of the player.
	 * @return the id of the player.*/
	public static int getIdbyName(final String name)
	{
		Player p = players.get(name);
		return p.getId();
	}
	
	/**retuns the player matching the given id.
	 * only accessible in this packet.
	 * other packets should use getIdbyName or getNamebyId.
	 * @param id the id of the player
	 * @return the player matching this id*/
	static Player getPlayerbyId(final int id)
	{
		return players.get(id);
	}
	
}
