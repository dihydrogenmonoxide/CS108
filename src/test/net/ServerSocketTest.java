package test.net;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import client.net.Clientsocket;
import server.net.*;
import server.parser.*;
import shared.ServerAddress;
import shared.SocketCreationException;

public class ServerSocketTest 
{


	public static void main(String[] args) 
	{
		Parser p = new Parser();
		try {
			Serversocket s = new Serversocket(9002, p);
			s.start_();
			
			Clientsocket c = new Clientsocket(new ServerAddress(InetAddress.getByName("127.0.0.1"), 9002, (NetworkInterface) null));
			Clientsocket c2 = new Clientsocket(new ServerAddress(InetAddress.getByName("127.0.0.1"), 9002, (NetworkInterface) null));
			c.sendChatMessage("TEST  hallo ");
			c2.sendData("CCHAT ROFLMAO asdsa dasdaf sadsad asdsadas dasdas dasdasdasd");
			
			Thread.sleep(100000);
			s.stop_();
		
		} catch (SocketCreationException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		



	}

}
