package client.data;

class Player {
	private String name;
	private int id;
        private int money;
        public int getMoney()
        {
            return money;
        }
	public int getId(){
		return id;
	}
	public String getName(){
		return name;
	}
        /**Create a new Player
         @param playerId the Id of the player
         @param playerName the Name of the player
         */
	public Player(int playerId, String playerName){
		this.id=playerId;
		this.name=playerName;
	}
}
