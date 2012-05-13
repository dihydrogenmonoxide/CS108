package client;

import client.lobby.ClientLobby;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import shared.InputValidator;
import shared.Log;
import shared.ServerAddress;
import shared.User;

public class MainClient 
{
    
    /**starts a client with default settings, not connecting to anything*/
    public static void startClient()
    {
        User user = new User();
	ClientLobby lobby = new ClientLobby(user);
    }

    /**start a client trying to connect to the specified ip and port
     @param ip the ip of the server
     @param port the port of the server
     */
    public static void startClient(String ip, int port)
    {
        try
        {
            //-- create user
            User user = new User(InputValidator.UserName(System.getProperty("user.name")));  
            //-- set address
            InetAddress addressIP = InetAddress.getByName(ip);
            ServerAddress addressServer = new ServerAddress(addressIP, port, NetworkInterface.getByInetAddress(addressIP));
            
            //-- start lobby
            ClientLobby lobby = new ClientLobby(user, addressServer);
        } catch (SocketException ex)
        {
            Log.ErrorLog("thou shalt pass a known IP address");
        } catch(UnknownHostException ex)
        {
             Log.ErrorLog("thou shalt pass a known Host");
        }
    }
    
    /**if directly called, start a client*/
    public MainClient()
    {
        startClient();
    }
}
