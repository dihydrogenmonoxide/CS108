package shared;
/**Provides the actual messages to be send in the Protocol.*/
public enum Protocol {
	/**command not known:*/
	UNKNOWN_COMMAND("command not known"),
	/**for the discovery service.*/
	DISCOVERY("D"),
	/**start of all connection messages.*/
	CONNECTION("V"),
	/**start of all lobby messages.*/
	LOBBY("L"),
	/**start of all chat messages.*/
	CHAT("C"),
	/**start of all game messages.*/
	GAME("G"),
	/**start of all game objects.*/
	OBJECT("OB"),
	/**the tank*/
	OBJECT_TANK(OBJECT, "TNK"),
	/**the fighter plane*/
	OBJECT_FIGHTER_JET(OBJECT, "JET"),
	/**the bomber*/
	OBJECT_BOMBER(OBJECT, "BMB"),
	/**the reproduction center*/
	OBJECT_REPRODUCTION_CENTER(OBJECT, "REP"),
	/**the stationary antitank*/
	OBJECT_STATIONARY_ANTI_TANK(OBJECT, "SAT"),
	/**the stationary anti aircraft weapon*/
	OBJECT_STATIONARY_ANTI_AIR(OBJECT, "SAA"),
	/**the bank*/
	OBJECT_BANK(OBJECT, "BNK"),
	/**alive beacon.*/
	DISC_ALIVE(DISCOVERY, "ALIV"),
	/**server beacon.*/
	DISC_SERVER(DISCOVERY, "SERV"),
	/**when the connection has a timeout.*/
	CON_TIMEOUT(CONNECTION, "TOUT"),
	/**authentication messages.*/
	CON_AUTH(CONNECTION, "AUTH"),
	/**hash for the authentication.*/
	CON_HASH(CONNECTION, "HASH"),
	/**ping messages for testing the connection.*/
	CON_PING(CONNECTION, "PING"),
	/**pong which is the answer to a ping.*/
	CON_PONG(CONNECTION, "PONG"),
	/**connection exit / socket closed, or too many timeouts.*/
	CON_EXIT(CONNECTION, "EXIT"),
	/**connection fail, usually too many timeouts.*/
	CON_FAIL(CONNECTION, "FAIL"),
	/**changing the nick.*/
	CON_NICK(CONNECTION, "NICK"),
	/**connection error(usually unknown command).*/
	CON_ERROR(CONNECTION, "ERRO"),
	/**requesting the id.*/
	CON_MY_ID(CONNECTION, "MYID"),
	/**update the Lobby.*/
	LOBBY_UPDATE(LOBBY, "UPDT"),
	/**quit the lobby.*/
	LOBBY_QUIT(LOBBY, "QUIT"),
	/**join the lobby.*/
	LOBBY_JOIN(LOBBY, "JOIN"),
	/**chat messages.*/
	CHAT_MESSAGE(CHAT, "CHAT"),
	/**prefix for private messages.*/
	CHAT_PREF_PRIVATE("/MSG"),
	/**make a game.*/
	GAME_MAKE(GAME,  "MAKE"),
	/**join a game.*/
	GAME_JOIN(GAME, "JOIN"),
	/**game information broadcasted to all.*/
	GAME_BROADCAST(GAME, "GAME"),
	/**quit a game.*/
	GAME_QUIT(GAME, "QUIT"),
	/**pause a game	 */
	GAME_PAUSE(GAME, "PAUS"),
	/**resume a game	 */
	GAME_RESUME(GAME, "RESU"),
	/**vote to start the game */
	GAME_VOTESTART(GAME, "VOTE"),
	/**forces the server to send all unit/building data */
	GAME_RESET(GAME, "RSET"),
	/**Tells the client to start/end a build phase */
	GAME_BUILD_PHASE(GAME, "BUIL"),
	/**Tells the client to start/end an animation phase */
	GAME_ANIMATION_PHASE(GAME, "ANIM"),
	/**Tells the client how much money it owns */
	GAME_MONEY(GAME, "MONY"),
	/**Tells the server to spawn the specified object at the given location */
	GAME_SPAWN_OBJECT(GAME, "SPWN"),
	/**Updates the given object */
	GAME_UPDATE_OBJECT(GAME, "UPDT"),
	/**Starts the game*/
	GAME_BEGIN(GAME, "BEGI"),	
	/**Updates the population size*/
	GAME_POPULATION(GAME, "POPU"),	
	/**undo the last built object*/
	GAME_UNDO(GAME, "UNDO"),
	/**tells the client that it should go back into pre game mode*/
	GAME_LOST_OR_WON(GAME, "LOST");
	
	/**holds the String belonging to the Enum.*/
	private final String message;

	/**just the Constructor to assign the String to the Enum.
	 * @param str the String belonging to the enum.*/
	private Protocol(final String str)
	{
		this.message = str;
	}
	/**just the Constructor to assign the String to the Enum.
	 * @param section the section belonging to the command
	 * @param str the String belonging to the enum.*/
	private Protocol(final Protocol section, final String str)
	{
		this.message = section.toString() + str;
	}

	/**Translate a String to an enum.
	 * @return b the enum matching your string.
	 * @param str the Command you are searching for.
	 * */
	public static Protocol fromString(final String str) {
		String command;
		if (str != null) {
			//-- remove leading and trailing whitespaces
			command = str.replaceAll("^[ \t]+|[ \t]+$", "");
				
			//--cut the message
			if (5 <= command.length())
			{
				command = (String) str.subSequence(0, 5);
			}
			else
			{
				command = str;
			}
			
			//--search for the command
			for (Protocol b : Protocol.values()) {
				if (command.equalsIgnoreCase(b.message)) 
				{
					return b;
				}
			}


		


		}
		return UNKNOWN_COMMAND;
	}

	/**Overides the toString method.
	 * @return message the String belonging to the enum.*/
	public String toString()
	{
		return message;
	}

	/**return the COMMAND+" ", better readable than toString().
	 * @return message the COMMAND plus one space.s*/
	public String str()
	{
		return message + " ";
	}
}
