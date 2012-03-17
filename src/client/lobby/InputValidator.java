package client.lobby;

public class InputValidator {
	/**sanitizes Chatmessages*/
	public static String ChatMessage(String s){
		return s.replaceAll("[^A-Za-z0-9 ;:()./]", "");
	}

	/**sanitizes the UserName*/
	public static String UserName(String s){
		return  s.replaceAll("[^A-Za-z0-9]", "");
	}
}

