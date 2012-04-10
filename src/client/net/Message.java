/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.net;

import shared.Protocol;

/**
 * Contains a received message. Has all the needed Methods to work with a
 * received message.
 *
 * @author fox918
 */
public class Message
{

    String message = null;
    String[] messageParts;

    /**
     * inits a message with a received String.
     *
     * @param s the Information a message is holding.
     * @throws an Exception if the String is too short.
     */
    public Message(String s) throws Exception
    {
        if (s.length() < 5)
        {
            throw ParserException("too short command");
        }
        this.message = s;
        //-- split message at " "
        this.messageParts = message.split("\\s+");
    }
    
     /**
     * return a part of the message. Cast this part to Long. is used for getting
     * large numbers (eg the Money count).
     *
     * @param whichArgument which argument you want.
     */
    public long getLongArgument(int whichArgument)
    {
        return Long.valueOf(messageParts[whichArgument]);
    }

    /**
     * return a part of the message. Cast this part to int. is used for getting
     * numbers (eg the gameId).
     *
     * @param whichArgument which argument you want.
     */
    public int getIntArgument(int whichArgument)
    {
        return Integer.valueOf(messageParts[whichArgument]);
    }

    /**
     * return a part of the message. is used for getting Strings (eg the player
     * name).
     *
     * @param whichArgument which argument you want.
     */
    public String getStringArgument(int whichArgument)
    {
        return messageParts[whichArgument];
    }
    
    /**get the Protocol of a given Argument, used to extract objects out of messages.*/
    public Protocol getProtocolArgument(int i)
    {
        return Protocol.fromString(getStringArgument(i));
    }

    /**
     * gets you the section of this message.s
     *
     * @return the section where this message belongs to.
     */
    public Protocol getSection()
    {
        String section = (String) message.subSequence(0, 1);
        return Protocol.fromString(section);
    }

    /**
     * Extracts the Command of an Message.
     *
     * @param msg the message
     * @return the command as ENUM
     */
    public Protocol getCommand()
    {
        return Protocol.fromString((String) messageParts[0]);
    }

    /**
     * Exception to be thrown if something occurs.
     */
    public Exception ParserException(String cause)
    {
        throw new UnsupportedOperationException(cause);
    }

    /**
     * returns the message hold by this object
     *
     * @return the message
     */
    public String toString()
    {

        return this.message;
    }
//    static Pattern firstNumber = Pattern.compile("^[a-zA-Z ]+([0-9]+).*");
//    private int getFirstNumber(String s)
//    {
//        Matcher m = firstNumber.matcher(s);
//
//        if (m.find())
//        {
//            return Integer.valueOf(m.group(1));
//        }
//        return 0;
//    }

    
}
