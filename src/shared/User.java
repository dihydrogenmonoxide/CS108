package shared;

public class User {
	private String username;
	
	public User(String username){
		this.username = username;
		
	}

	
	public User() {
		this.username = "";
	}


	public String getUserName(){
		return this.username;
	}
	
	public void setUserName(String s){
		this.username = s; 
	}
	
}


