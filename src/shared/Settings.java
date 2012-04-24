package shared;

public final class Settings {
	public static final String DISCOVERY_MULITCAST_GROUP = "225.6.7.8";
	public static final int DISCOVERY_DEFAULT_PORT = 9002;
	public static final int DISCOVERY_CLIENT_SCAN_PERIOD = 6000;
	public static final int SERVER_DEFAULT_PORT = 9003;
	public static final boolean PRINT_DEBUG_MSG_IN_CHAT = true;
        public static final boolean FULLSCREEN = false;
	
	public final class SocketTimeout
	{
		/**the connection timeout in microseconds.*/
		public static final int TIMEOUT = 4000;
		/**the time to wait between reconnects.*/
		public static final int WAIT_BETWEEN_PINGS = 500;
		/**how many reconnects.*/
		public static final int MAX_RETRIES = 5;
	}	
	
	public final class GameValues
	{
		//TODO SETTINGS set to a sane  default money amount when done testing
		/**The default money value a player receives on round begin*/
		public static final long DEFAULT_MONEY = 100000000;
		/**The default Population amount*/
		public static final long DEFAULT_POPULATION = 100000;
	}
}
