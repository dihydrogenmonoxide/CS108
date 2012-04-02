package shared;

import java.text.SimpleDateFormat;
import java.util.Date;



public class Log 
{
	private static final boolean b_LogError = true;
	private static final boolean b_LogWarning = true;
	private static final boolean b_LogDebug = true;
	private static final boolean b_LogInformation = true;
	private static final boolean b_PrintTime = true;
	private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	private Log()
	{
		
	}
	
	public static void ErrorLog(String s_MSG)
	{
		if(b_LogError)
			_Log("E\\ "+s_MSG);	
	}
	
	public static void WarningLog(String s_MSG)
	{
		if(b_LogWarning)
			_Log("W\\ "+s_MSG);
	}
	
	public static void DebugLog(String s_MSG)
	{
		if(b_LogDebug)
			_Log("D\\ "+s_MSG);
	}
	
	public static void InformationLog(String s_MSG)
	{
		if(b_LogInformation)
			_Log("I\\ "+s_MSG);
	}
	
	private static void _Log(String s_MSG)
	{
		System.out.println(time()+s_MSG);
	}
	
	private static String time(){
		return b_PrintTime ? format.format(new Date()) + " " : "";
	}
}
