package server.logic;

import server.MainServer;
import server.GamePlayObjects.ATT;
import server.GamePlayObjects.Bank;
import server.GamePlayObjects.Bomber;
import server.GamePlayObjects.Flak;
import server.GamePlayObjects.GamePlayObject;
import server.GamePlayObjects.Jet;
import server.GamePlayObjects.Reproductioncenter;
import server.GamePlayObjects.Tank;
import server.exceptions.GameEndedException;
import server.exceptions.GameObjectBuildException;
import server.parser.Parser;
import server.players.Player;
import server.server.Server;
import shared.Log;
import shared.Protocol;
import shared.Settings;
import shared.game.Coordinates;




public class LogicManager 
implements Runnable
{
	private final static int buildTimeInSeconds = 60;
	private final static int animationTimeInSeconds = 2;
	private Server server;
	private Thread thread = new Thread(this);
	private int voteCount = 0;	
	private boolean isPaused = false;
	private boolean isInBuildPhase = false;
	private int remainingTime = 0;

	
	public LogicManager(Server server)
	{
		this.server = server;
	}

	@Override
	public void run() 
	{
		try
		{
			server.resetFields();
			while(server.isGameRunning())
			{
				buildPhase();
				server.getObjectManager().round();
				updateClients();
				animationPhase();
			}
		}
		catch (GameEndedException e)
		{
			Player winner = e.getWinner();
			server.broadcastMessage(Protocol.CHAT_MESSAGE.str()+"\t"+winner.getNick()+" won the game with a "+winner.getMoney()+" points");
			server.broadcastPreviousWinners();
			server.addWinner("\t"+winner.getNick()+": "+winner.getMoney()+" points");
			winner.sendData(Protocol.GAME_LOST_OR_WON.str()+"0");
			return;
		}
	}
	
	private void updateClients()
	{
		for(Player p : server.getPlayers())
		{
			resendEverything(p);
		}
	}

	/**
	 * Starts the game. This is called automatically by the {@link Server}
	 */
	public void startGame()
	{
		for(Player p : server.getPlayers())
		{
			//that the Players can Start with Money, they have to get some Money and Population.
			//So i give them some, sorry for changing your code Frank. 
			//Author: Lucius
			p.addMoney(Settings.GameValues.DEFAULT_MONEY);
			p.addPopulation(Settings.GameValues.DEFAULT_POPULATION);
			p.sendData(Protocol.GAME_BEGIN.str()+server.getID()+" "+p.getFieldID());
			resendEverything(p);
		}
		
		thread.start();
	}
	
	private void animationPhase()
	{
		server.broadcastMessage(Protocol.GAME_ANIMATION_PHASE.str()+animationTimeInSeconds);
		try
		{
			remainingTime = animationTimeInSeconds+1;
			for(; remainingTime != 0; remainingTime--)
			{
				Thread.sleep(1000);
				if(isPaused)
				{
					remainingTime++;
				}
			}
		}
		catch(InterruptedException e)
		{
			Log.WarningLog("The animationphase was interruped, this should not be done!");
		}
		finally
		{
			server.broadcastMessage(Protocol.GAME_ANIMATION_PHASE.str()+0);
		}
	}

	
	private void buildPhase()
	{
		voteCount = 0;
		for(Player p : server.getPlayers())
		{
			p.endBuildPhase();
		}
		server.broadcastMessage(Protocol.GAME_BUILD_PHASE.str()+buildTimeInSeconds);
		isInBuildPhase = true;
		try
		{
			remainingTime = buildTimeInSeconds+2;
			for(; remainingTime != 0; remainingTime--)
			{
				Thread.sleep(1000);
				if(isPaused)
				{
					remainingTime++;
				}
			}
		}
		catch (InterruptedException e)
		{
			Log.DebugLog("All Players finished building before tiem ran out");
		}
		isInBuildPhase = false;
		server.broadcastMessage(Protocol.GAME_BUILD_PHASE.str()+0);
	}
	
	public void resendEverything(Player player)
	{
		//TODO SERVER make sure that destroyed objects are updated aswell! (and are deleted next round) <--- test this
		for(GamePlayObject o :server.getObjectManager().getObjectList())
		{
			player.sendData(o.toProtocolString());
		}
		player.sendData(Protocol.GAME_MONEY.str()+player.getMoney());
		player.sendData(Protocol.GAME_POPULATION.str()+player.getPopulation());
	}

	public synchronized void finishedBuilding(Player player)
	{
		if(isInBuildPhase && !player.finishedBuilding())
		{
			voteCount++;
			player.finishedBuilding(true);
			if(voteCount >= server.getPlayerAmount())
			{
				thread.interrupt();
				Log.InformationLog("Everyone has finished b4 time ran out");
			}
		}
		else
		{
			player.sendData(Protocol.CON_ERROR.str()+"You already stated, that you're finished!");
		}
	}

	public void buildObject(String parserString, Player player)
	{
		if(server.isPaused())
		{
			player.sendData(Protocol.CON_ERROR.str()+"The Server is paused, please wait for it to resume.");
			return;
		}
		if(!isInBuildPhase)
		{
			player.sendData(Protocol.CON_ERROR.str()+"You can't build outside a build phase.");
			return;
		}
		
		String[] s = parserString.split("\\s+");
		if(s.length == 4)
		{
			int x = Integer.parseInt(s[2]);
			int y = Integer.parseInt(s[3]);
			synchronized (player)
			{
				try
				{
					GamePlayObject o;
					
					switch(Protocol.fromString(s[1]))
					{
					case OBJECT_BOMBER:
						o = new Bomber(new Coordinates(x, y), player, server.getObjectManager());
						break;
					case OBJECT_FIGHTER_JET:
						o = new Jet(new Coordinates(x, y), player, server.getObjectManager());
						break;
					case OBJECT_REPRODUCTION_CENTER:
						o = new Reproductioncenter(new Coordinates(x, y), player, server.getObjectManager());
						break;
					case OBJECT_STATIONARY_ANTI_AIR:
						o = new Flak(new Coordinates(x, y), player, server.getObjectManager());
						break;
					case OBJECT_STATIONARY_ANTI_TANK:
						o = new ATT(new Coordinates(x, y), player, server.getObjectManager());
						break;
					case OBJECT_TANK:
						o = new Tank(new Coordinates(x, y), player, server.getObjectManager());
						break;	
					case OBJECT_BANK:
						o = new Bank(new Coordinates(x, y), player, server.getObjectManager());
						break;	
					default:
						Log.ErrorLog("Couldn't build the object - \'"+parserString+"\' isn't in a valid format");
						return;
					}
					
					player.sendData(o.toProtocolString());
					player.addObject(o);
					player.sendData(Protocol.GAME_MONEY.str()+player.getMoney());
					return;
				}
				catch(GameObjectBuildException e)
				{
					Log.InformationLog(player.getNick()+": "+e.getMessage());
					player.sendData(Protocol.CON_ERROR.str()+e.getMessage());
					return;
				}
				
			}
		}
		Log.ErrorLog("Couldn't build the object - \'"+parserString+"\' isn't in a valid format");
	}
	
	/**
	 * updates the given object
	 * @param parserString the options received from the {@link Parser}
	 * @param player the {@link Player} requesting the update
	 */

	public void updateObject(String parserString, Player player)
	{
		if(server.isPaused())
		{
			player.sendData(Protocol.CON_ERROR.str()+"The Server is paused, please wait for it to resume.");
			return;
		}
		if(!isInBuildPhase)
		{
			player.sendData(Protocol.CON_ERROR.str()+"You can't build outside a build phase.");
			return;
		}
		
		String[] s = parserString.split("\\s+");
		if(s.length == 5)
		{
			int x = Integer.parseInt(s[2]);
			int y = Integer.parseInt(s[3]);
			int id = Integer.parseInt(s[4]);
			synchronized (player)
			{
				GamePlayObject o = server.getObjectManager().getObjectById(id);
				if(o != null)
				{
					if(o.getOwner() == player)
					{
						o.setTarget(new Coordinates(x, y));
						player.sendData(o.toTargetString());
					}
					else
					{
						player.sendData(Protocol.CON_ERROR.str()+"trying to move another players stuff... so silly!");
						MainServer.printInformation(player.getNick()+" just attempted to move "+o.getOwner().getNick()+"\'s object!");
					}
				}
				else
				{
					player.sendData(Protocol.CON_ERROR.str()+"Can't find an object with the ID "+id);
					Log.WarningLog("no object with the ID "+id+" found - can't move it therefore");
				}
				return;
			}
		}
		Log.ErrorLog("Couldn't move the object - \'"+parserString+"\' isn't in a valid format");
	}

	/**
	 * resumes the {@link Server} and notifies all {@link Player}s about it
	 */
	public void resume()
	{
		isPaused = false;
		server.broadcastMessage(Protocol.GAME_RESUME.str());
		if(isInBuildPhase)
			server.broadcastMessage(Protocol.GAME_BUILD_PHASE.str()+remainingTime);
		else
			server.broadcastMessage(Protocol.GAME_ANIMATION_PHASE.str()+remainingTime);
	}

	/**
	 * pauses the {@link Server} and notifies all {@link Player}s
	 */
	public void pause()
	{
		isPaused = true;
		server.broadcastMessage(Protocol.GAME_PAUSE.str());
        }
}
