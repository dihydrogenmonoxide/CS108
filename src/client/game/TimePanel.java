package client.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeoutException;


import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import client.net.Clientsocket;


public class TimePanel extends JPanel{
    
    /**TimerLabel to show Countdown*/
	private JLabel timerLabel;
	private Timer timer;
	private int zahl;
	private JFrame gameFrame;
	private GlassPane GlassPane;
	private Clientsocket socket;
 
	
	public TimePanel(JToggleButton ready, JFrame gameFrame, GameChatPanel gameChat, Clientsocket s){
		this.socket=s;
		this.gameFrame = gameFrame;
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		



		timerLabel = new JLabel("   Start  ");		
		timerLabel.setForeground(Color.red);
		timerLabel.setBackground(Color.black);
		timerLabel.setOpaque(true);
	    Border thickBorder = new LineBorder(Color.red, 7);
		timerLabel.setBorder(thickBorder);
		timerLabel.setAlignmentX(CENTER_ALIGNMENT);
		timerLabel.setAlignmentY(CENTER_ALIGNMENT);
		this.add(timerLabel,c);		
		
		Font curFont = timerLabel.getFont();
		timerLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 50));
		
		zahl=74;//Rundenzeit
		
		TimeClassMin tcMin = new TimeClassMin(zahl, ready, gameChat);
		timer = new Timer(1000, tcMin);
		timer.start();
		
		
	}

	

	public class TimeClassMin implements ActionListener {
		int sec;
		int min;
		int zahl;
		int zero;
		JToggleButton ready;
		GameChatPanel gameChat;

		public TimeClassMin(int zahl, JToggleButton ready, GameChatPanel gameChat){
			this.zahl=zahl;
			this.ready = ready;
			this.gameChat = gameChat;
		}

		public void actionPerformed(ActionEvent f){
			zahl--;
			min=zahl/60;
			sec=zahl%60;
			if(zahl%60<10||zahl%60<70){
				timerLabel.setText(min+"   :   "+zero+sec);
				zero=0;
			}
			if(sec>=10){
				timerLabel.setText(min +"   :   "+ sec);
			} 
			if (sec==0&& min==0){
				timer.stop();
				timerLabel.setText("  Ende!  ");
				GlassPane = new GlassPane(ready, gameFrame.getContentPane(), gameChat, socket);
				gameFrame.setGlassPane(GlassPane);
				GlassPane.setVisible(true);

			}
			
		}

	}
}