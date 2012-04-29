package client.game;

import client.data.RunningGame;
import client.net.Clientsocket;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class TimePanel extends JPanel{
    
    /**TimerLabel to show Countdown*/
	private JLabel timerLabel;
	private Timer timer;
	private int rundenzeit;
	private JFrame gameFrame;
	private GlassPane GlassPane;
	private Clientsocket socket;
 

	public TimePanel( JFrame gameFrame, Clientsocket s){
		this.socket=s;
		this.gameFrame = gameFrame;





		timerLabel = new JLabel(" Start ");	
		timerLabel.setPreferredSize(new Dimension(200,70));
		timerLabel.setForeground(Color.red);
		timerLabel.setBackground(Color.black);
		timerLabel.setOpaque(true);
		timerLabel.setBorder(new LineBorder(Color.red, 7));
		timerLabel.setHorizontalAlignment(JLabel.CENTER);
		timerLabel.setVerticalAlignment(JLabel.CENTER);
                
                
		this.add(timerLabel);		

		Font curFont = timerLabel.getFont();
		timerLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 50));
                
                
		TimeClassMin tcMin = new TimeClassMin();
		timer = new Timer(1000, tcMin);
		timer.start();


	}

	public String makeTime(int seconds){
          return String.format("%02d:%02d", seconds/60, seconds%60);
        }

	public class TimeClassMin implements ActionListener {


		public TimeClassMin(){
		}

		public void actionPerformed(ActionEvent f){
                        rundenzeit = RunningGame.getBuildTime();

			timerLabel.setText(makeTime(rundenzeit));

			if (rundenzeit == 0){
				timerLabel.setText("  Ende!  ");
				GlassPane = new GlassPane(gameFrame.getContentPane(), socket);
				gameFrame.setGlassPane(GlassPane);
				GlassPane.setVisible(true);
			}
                        else
                        {
                            if(GlassPane != null)
                            {
                                GlassPane.setVisible(false);
                            }
                        }

		}

	}
}