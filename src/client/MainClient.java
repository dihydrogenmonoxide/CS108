package client;

import client.lobby.ClientLobby;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.InputValidator;
import shared.Log;
import shared.ServerAddress;
import shared.User;

public class MainClient 
{

    public static void startClient()
    {
        User user = new User();
	ClientLobby lobby = new ClientLobby(user);
    }

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
        } catch (UnknownHostException | SocketException ex)
        {
            Log.ErrorLog("thou shalt pass a known IP address");
        }
    }
    
    public MainClient()
    {
        startClient();
    }
}
