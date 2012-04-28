package client.data;

import client.game.field.DrawableObject;
import java.awt.Color;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import shared.Log;
import shared.Protocol;
import shared.game.Coordinates;

/**
 * This class holds all the informations about a Game the input comes from the
 * Clientparser which will simultaneous fire an Event, so the GUI redraws /
 * rebuilds with informations from here. Everything in here is static, so it is
 * accessible from everywhere without problems.
 *
 * @author f0x918
 */
public class RunningGame
{

    static boolean isRunning = false;
    static int gameId;
    static int myFieldId;
    /**the time remaining for the current state. */
    static volatile int remainingTime = 0;
    static Timer timer;

    /**boolean if the game is paused or not, a little workaround so all the external classes
     just need to use getGamePhase(). Otherwise they need to check something like isGamePause().*/
    public static boolean isPaused = false;
    /**the actual phase the game is in.*/
    static GamePhases state = GamePhases.PAUSE;
    
    /**the money of the current player*/
    static long myMoney;
    /**the population of the current player*/
    static long myPopulation;
    
    //-- color management
    static Color myColor = new Color(255, 51, 204);
    static Color firstEnemy = new Color(51, 204, 255);
    static Color secondEnemy = new Color(51, 255, 102);
    static Color thirdEnemy = new Color(255, 204, 51);
    static Color fourthEnemy = new Color(12, 12, 12);
    static Color[] enemyColors = { firstEnemy, secondEnemy, thirdEnemy, fourthEnemy };
    static int colorCounter;
    static HashMap<Integer, Color> playerColor = new HashMap<Integer, Color>();
    
    //-- object management
    private static ConcurrentHashMap<Integer, GameObject> objects = new ConcurrentHashMap<Integer, GameObject>();

    /**
     * Sets the Build time of the running game. if the Build time is over 0 the
     * game changes in the state BUILD. Then a timer is started which decreases
     * the build time until it reaches 0; Otherwise the state is changed to
     * ANIM.
     */
    public static void setBuildTime(int seconds)
    {
       stopTimer();
       resetObjectLocations();
       
       if(0 < seconds)
       {
       //-- stop existing timers.
       remainingTime = seconds;
       state = GamePhases.BUILD;
       Log.InformationLog("Entered Build Phase");
       startTimer();
       }
       else
       {
           state = GamePhases.ANIM;
           Log.InformationLog("exit Build Phase");
       }
       
       
       
    }
    /**this method returns the remaining build time.*/
    public static int getBuildTime(){
        if(state == GamePhases.BUILD)
        {
            return remainingTime;
        }
        return 0;
    }
    
    /**Sets the Animation time until the next round.
     if seconds is smaller than 0 nothing happens.*/
    public static void setAnim(int seconds)
    {
        if(0 <= seconds)
        {
            stopTimer();
            remainingTime = seconds;
            state = GamePhases.ANIM;
            Log.InformationLog("Entered Anim Phase");
            startTimer();
        }
    }
    
    /**this method returns the remaining animation time.*/
    public static int getAnimTime(){
        if(state == GamePhases.ANIM)
        {
            return remainingTime;
        }
        return 0;
    }
    
    /**clears the existing timer.*/
    static void stopTimer()
    {
          //-- delete the timer;
         if (timer != null)
            {
                timer.cancel();
                timer.purge();
            }
    }
    /**starts a Timer and counts down the remaining time.
      Note that after the Build phase the client goes automatically to the Anim phase.
        But not from the Anim Phase automatically to the Build phase.*/
    static void startTimer()
    {
        //-- just to make sure no other timer is running.
        stopTimer();
        
         //-- set the remainigBuildTime and restart the timer if necessary
        if (remainingTime <= 0)
        {
            remainingTime = 0;
            state = GamePhases.ANIM;
            Log.DebugLog("time ran out");
        } else
        {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {

                public void run()
                {
                    if (0 < remainingTime)
                    {
                        remainingTime--;
                        if (remainingTime % 5 == 0)
                        {
                            Log.DebugLog("remainig " + state + " time is: " + remainingTime);
                        }
                    }
                }
            }, 0, 1000);
        }
        Log.DebugLog("Started new Timer with " + remainingTime + "s in the phase" + state);
    }
    
    /**set if a game is paused or not.*/
    public static void setPaused(boolean b){
        isPaused = b;
    }
    
    /**return the Phase the game is in.
     @return state the phase the game is in.*/
    public static GamePhases getGamePhase()
    {
        if (isPaused)
        {
            return GamePhases.PAUSE;
        }
        return state;
    }
    
    /**
     * starts a new game. Sets the GameId and the field which the player has.
     */
    public static void initGame(int gId, int fId)
    {
        Log.InformationLog("a new game has begun.");
        gameId = gId;
        myFieldId = fId;
        Log.DebugLog("->initialised a new Game with the id " + gameId + " and the fieldId " + myFieldId);
        isRunning = true;
    }

    /**
     * this method clears all the fields in this class. is used at the end of a
     * game, so everything is ready for a new game.
     */
    public static void hardReset()
    {
        //TODO RunningGame implement hard reset
        myPopulation = 0;
        myMoney = 0;
        myFieldId = 0;
        objects.clear();
        isRunning = false;
    }
   

    /**creates an object if it doesn't exist, otherwise an existing object will be updated.
     * @param objectType the type of the object
     * @param xCoords new x Coordinates
     * @param yCoords new y Coordinates
     * @param playerId the player Id
     * @param objectId the id of this object
     * @param health the new health of this object
     */
    public static void updateObj(Protocol objectType, int xCoords, int yCoords, int objectId, int playerId, int health)
    {
        GameObject obj = objects.get(objectId);
        ObjectType type = ObjectType.fromProtocol(objectType);
        Coordinates coords = new Coordinates(xCoords, yCoords);
        if(obj != null)
        { 
            Log.DebugLog("Running Game: update object with id:" + objectId);
            obj.update(coords, health);
        } 
        else
        {
            Log.DebugLog("Running Game: creating object with id:" + objectId);
            objects.put(objectId, new GameObject(type, coords, objectId, playerId, health));
        }
    }
    
    /**deletes the object with the given id
     * @param objectId the id of the object
     */
    public static void deleteObject(int objectId)
    {
        Log.DebugLog("object with the id=" + objectId + " has been removed");
        objects.remove(objectId);
    }

    /**set my population.*/
    public static void setPop(long population)
    {
        myPopulation = population;
    }
    
    /**set my money.*/
     public static void setMoney(long money)
    {
        myMoney = money;
    }

     /** returns all objects which the client knows
      * @return all objects
      */
    public static ConcurrentHashMap<Integer, GameObject> getObjects()
    {
        return objects;
    }
    
    /**returns myMoney.
     * @return my Money
     */
    public static long getMoney(){
    	return myMoney;
    }
    
        /**returns my Population.
     * @return my Population
     */
    public static long getPopulation(){
    	return myPopulation;
    }

    /**returns the field id of the given Player.
     * @return the field id of the given Player
     */
    public static int getMyFieldId()
    {
        return myFieldId;
    }
    
    /**resets the oldLocation of all GameObjects.
     * this is to tell them that a new round has begun and
     * the objects have not been moved since.
     */
    private static void resetObjectLocations()
    {
            Collection<GameObject> c = objects.values();
            Iterator<GameObject> objIter = c.iterator();
            while (objIter.hasNext())
            {
                objIter.next().resetOldLocation();
            }
    }
    
    /** returns a color for each player.
     * @param playerId the if of the player.
     */
    public static Color getPlayerColor(int playerId)
    { 
       Color temp = new Color(0,0,0);
       
       //-- check if it's me
       if(playerId == PlayerManager.myId())
       {
           temp = myColor;
       }
       
       //-- get color for enemy
       if(null == playerColor.get(playerId))
       {
           playerColor.put(playerId, enemyColors[colorCounter]);
          
       }
       else
       {
           temp = playerColor.get(playerId);
       }
       
       return temp;
    }
    
}
