package client.lobby;

public class InputValidator {
	/**sanitizes Chatmessages, is nothing is done here as of now.
	 * @return the sanitized chatmessage
	 * */
	public static String ChatMessage(String s)
	{
		
		//return s.replaceAll("[^A-Za-z0-9 ;:()./]", "");
		return s;
	}

	/**sanitizes the UserName.
	 * @return the sanitized username.
	 * */
	public static String UserName(String s)
	{
		return  s.replaceAll("[^A-Za-z0-9]", "");
	}
	
	/**check if the string is a valid ip.
	 * param s the ip as string
	 * @return wheter the string is an ip or not.
	 * */
	public static boolean isIP(final String s)
	{
		//regex from the interwebs
	return s.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
	+			  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
	+			  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." 
	+			  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		
	}
}

