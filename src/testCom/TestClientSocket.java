package testCom;
import java.io.*;
import java.net.*;

/**
 * Creates a ClientSocket, which connects with a ServerSocket at Localhost.
 * The Server must be started before.
 * While the connection is establisht, he sends "hello"
 * @author lucius
 *
 */
public class TestClientSocket {
	public static int portNum=20000;
	public static String host="localhost";
	
	public static void main(String[] args){
		try{
			//Creates a Client Socket, which connects to a host named/with the InetAdress host.
			//at the Server Port portNum
			Socket C_Socket = new Socket(host, portNum);
		
		System.out.println("Client ist connected");
		//Creates a SocketChannel with an Io_InputStream and an Io_Outputstream.
		//The can be used with send(xy) and receive()
		SocketChannel channel= new SocketChannel(C_Socket);
		
		
		System.out.println("Channel is created");
		//While the connection exists, the Client send hello and Prints what he receives.
		while(C_Socket.isConnected())
		{
		
		channel.send("hallo");
		String s=channel.receive().toString();
		System.out.println("Got Message from Server: "+s);
		}
		}
		catch( IOException e)
		{
			System.out.println("IOException CLient");
			
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println("ClassNotFoundExcetion Client");
			
		}
		
		
	}

}
