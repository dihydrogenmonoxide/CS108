package client.data;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import shared.Log;

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
    static int fieldId;
    static volatile int remainingBuildTime = 0;
    static Timer timer;

    static enum GamePhases
    {

        ANIM, BUILD, PAUSE
    };
    static GamePhases state = GamePhases.PAUSE;
    private static ConcurrentHashMap<Integer, GameObject> objects;

    /**
     * Sets the Build time of the running game. if the Build time is over 0 the
     * game changes in the state BUILD. Then a timer is started which decreases
     * the build time until it reaches 0; Otherwise the state is changed to
     * ANIM.
     */
    public static void setBuildTime(int seconds)
    {
        //-- delete the timer;
         if (timer != null)
            {
                timer.cancel();
                timer.purge();
            }
        
         //-- set the remainigBuildTime and restart the timer if necessary
        if (seconds <= 0)
        {
            remainingBuildTime = 0;
            state = GamePhases.ANIM;   
        } else
        {
            remainingBuildTime = seconds;
            state = GamePhases.BUILD;
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {

                public void run()
                {
                    if (0 <= remainingBuildTime)
                    {
                        remainingBuildTime--;
                        if (remainingBuildTime % 5 == 0)
                        {
                            Log.DebugLog("remainig build time is: " + remainingBuildTime);
                        }
                    }
                }
            }, 0, 1000);
        }
        Log.DebugLog("Set the Build time to " + remainingBuildTime + " and the state to " + state.toString());
    }
    
    /**this method returns the remaining build time.*/
    public static int getBuildTime(){
        return remainingBuildTime;
    }

    /**
     * starts a new game. Sets the GameId and the field which the player has.
     */
    public static void initGame(int gId, int fId)
    {
        Log.InformationLog("a new game has begun.");
        gameId = gId;
        fieldId = fId;
        Log.DebugLog("->initialised a new Game with the id " + gameId + " and the fieldId " + fieldId);
        isRunning = true;
    }

    /**
     * this method clears all the fields in this class. is used at the end of a
     * game, so everything is ready for a new game.
     */
    public static void hardReset()
    {

        isRunning = false;
    }

    public static int getMyFieldId()
    {
        return fieldId;
    }
}
