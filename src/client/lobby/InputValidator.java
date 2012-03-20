package client.lobby;

public class InputValidator {
	/**sanitizes Chatmessages, is nothing is done here as of now*/
	public static String ChatMessage(String s){
		
		//return s.replaceAll("[^A-Za-z0-9 ;:()./]", "");
		return s;
	}

	/**sanitizes the UserName*/
	public static String UserName(String s){
		return  s.replaceAll("[^A-Za-z0-9]", "");
	}
}

