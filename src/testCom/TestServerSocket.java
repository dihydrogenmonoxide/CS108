package testCom;

import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.io.*;

public class TestServerSocket {
	public static int portNum=20000;
	
	public static void main(String[] args)
	{
		try
		{
		ServerSocket so = new ServerSocket( portNum );
	    System.out.println("Server startet on port "+portNum);
		
			System.out.println("waiting for connection ... ");
            Socket C_Socket = so.accept();
 	    System.out.println("new connection from " 
                              +  C_Socket.getInetAddress() );
 	    
    
          SocketChannel channel = new SocketChannel(C_Socket);
          System.out.println("Object Stream created "); 
          while(C_Socket.isConnected())
          	{
           
    
            Object o =channel.receive();
 	    System.out.println("message 1 " + o); 
 	    System.out.println("Returning Echo");
 	    channel.send(o);
            //o = c.receive();
 	    
          	}
            channel.close();
			
			
			
			
		
		}
		catch(IOException e)
		{
			System.out.println("IoException from Server");
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println("Classnotofundexception from Server");
		}
		
	}

}
