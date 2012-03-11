package server.parser;

import shared.Log;

public class Parser 
{
	
	/**
	 * Parses the given Message and sends the corresponding answers
	 * @param s_MSG the Message the Server socket received
	 * @return What the Socket should answer
	 */
	public String Parse(String s_MSG)
	{
		Log.InformationLog("Recived: \'"+s_MSG+"\'" );
		
		return "Brool story co\nline2\nline3";
	}

}
