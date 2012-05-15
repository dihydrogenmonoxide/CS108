package server.score;

import java.util.*;

import server.players.Player;
import shared.Log;
import shared.Protocol;

public class ScoreManager
{
	private class Score
	implements Comparable<Score>
	{
		private String name;
		private long score;
		
		/**
		 * Saves a Score
		 * @param nick the nickname
		 * @param points the score
		 */
		public Score(String nick, long points)
		{
			name = nick;
			score = points;
		}
		
		/**
		 * 
		 * @return the {@link Player}'s Score
		 */
		public long getScore()
		{
			return score;
		}
		
		@Override
		public String toString()
		{
			return name+" mit "+score+" Punkten";
		}

		@Override
		public int compareTo(Score o)
		{
			return (int) (o.getScore()-score);
		}
	}
	
	private List<Score> topScores = new LinkedList<Score>();
	
	
	/**
	 * Adds a score to the top ten
	 * @param winner a {@link Player} that won a round
	 */
	public synchronized void addScore(Player winner)
	{
		Score s = new Score(winner.getNick(), winner.getMoney());
		topScores.add(s);
		Collections.sort(topScores);
		if(topScores.size() > 10)
		{
			Log.DebugLog("A player just fell out of the top 10");
			topScores.remove(10);
		}
	}
	
	/**
	 * Prints the top ten
	 * @param player the {@link Player} requesting the top ten
	 */
	public void printScore(Player player)
	{
		Log.DebugLog("Printing the Score for "+player.getNick());
		if(topScores.size() > 0)
		{
			player.sendData(Protocol.CHAT_MESSAGE.str()+"\tDie besten Spieler:");
			for(Score s : topScores)
			{
				player.sendData(Protocol.CHAT_MESSAGE.str()+"\t"+s);
			}
		}
		else
		{
			player.sendData(Protocol.CHAT_MESSAGE.str()+"\tEs wurde noch kein Spiel gewonnen auf diesem Server!");
		}
	}	
}

