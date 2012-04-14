package client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * SocketChannel
 * This class creates a communication channel using a socket. 
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 */

public class SocketChannel {
  /**
   * input stream from the socket
   */
  private ObjectInputStream in;

  /**
   * output stream to the socket
   */
  private ObjectOutputStream out;

  /**
   * socket
   */
  private Socket soc;

  /**
   * Constructs a socket channel on the given socket s.
   * @param s A socket object.
   */
  public SocketChannel(Socket s) throws IOException {
    soc = s;
    if (checkOrder(s)) {
      in = new ObjectInputStream(s.getInputStream());
      out = new ObjectOutputStream(s.getOutputStream());
    }
    else {
      out = new ObjectOutputStream(s.getOutputStream());
      in = new ObjectInputStream(s.getInputStream());
    }
  }

  /**
   * Sends an object
   */
  public void send(Object v) throws IOException {
    synchronized (out) {
      out.writeObject(v);
    }
  }

  /**
   * Receives an object
   */
  public Object receive() throws IOException, ClassNotFoundException {
    Object v = null;
    synchronized (in) {
      v = in.readObject();
    }
    return v;
  }

  /**
   * Closes the channel.
   */
  public void close() {
    if (in != null) {
      try { in.close(); } catch (IOException e) { }
    }
    if (out != null) {
      try { out.close(); } catch (IOException e) { }
    }
    if (soc != null) {
      try { soc.close(); } catch (IOException e) { }
    }
  }

  /*
   * Determines the order of stream initialization.
   * @param s A socket's object.  
   */
  private boolean checkOrder(Socket s) throws IOException {
    // first use the port numbers as the key
    int p1 = s.getLocalPort();
    int p2 = s.getPort();
    if (p1 < p2) return true;
    else if (p1 > p2) return false;

    // second use the inetaddr as the key
    int a1 = s.getLocalAddress().hashCode();
    int a2 = s.getInetAddress().hashCode();
    if (a1 < a2) return true;
    else if (a1 > a2) return false;

    // this shouldn't happen
    throw new IOException();
  }
}