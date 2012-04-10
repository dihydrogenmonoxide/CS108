package client.data;

class Player {
	private String name;
	private int id;
        private int money;
        public int getMoney()
        {
            return money;
        }
        public void setMoney(int m)
        {
            money = m;
        }
	public int getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public Player(int playerId, String playerName){
		this.id=playerId;
		this.name=playerName;
	}
}
