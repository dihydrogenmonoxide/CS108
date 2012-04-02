package client.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeoutException;


import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class TimePanel extends JPanel{
    
    
	JLabel timerLabel;
	Timer timer;
	int zahl;
	
	public TimePanel(){
		
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
		
		zahl=120;//Rundenzeit
		
		TimeClassMin tcMin = new TimeClassMin(zahl);
		timer = new Timer(1000, tcMin);
		timer.start();
		
		
	}

	

	public class TimeClassMin implements ActionListener {
		int sec;
		int min;
		int zahl;
		int zero;

		public TimeClassMin(int zahl){
			this.zahl=zahl;
		}

		public void actionPerformed(ActionEvent f){
			zahl--;
			min=zahl/60;
			sec=zahl%60;
			if(zahl%60<10){
				timerLabel.setText(min+"   :   "+zero+sec);
				zero=0;
			}else{
				if(sec>=1||min >= 1){
					timerLabel.setText(min +"   :   "+ sec);
				} else {
					timer.stop();
					timerLabel.setText("Ende!");
				}
			}
		}

	}
}