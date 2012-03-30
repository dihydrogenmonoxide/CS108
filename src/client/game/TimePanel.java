package client.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JTextField;


public class TimePanel extends JPanel{
    
    
	JLabel promptLabel, timerLabel;
	JTextField tf;
	JButton buttonMin;
	Timer timer;

	
	public TimePanel(){
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		



		timerLabel = new JLabel("Waiting…");
		c.ipadx=40;
		c.ipady=40;
		timerLabel.setForeground(Color.red);
		timerLabel.setBackground(Color.black);
		this.add(timerLabel,c);		
		
		Font curFont = timerLabel.getFont();
		timerLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 35));
		
		
		int zahl = 122;  //rundenzeit
		int min=0;
		min=zahl/60;
		int sec=zahl%60;
		
		timerLabel.setText(min +": " + sec);
		TimeClassMin tcMin = new TimeClassMin(zahl);
		timer = new Timer(1000, tcMin);
		timer.start();
		
		
	}

	

	public class TimeClassMin implements ActionListener {
		int sec;
		int min;
		int zahl;

		public TimeClassMin(int zahl){
			this.zahl=zahl;
		}

		public void actionPerformed(ActionEvent f){
			zahl--;
			min=zahl/60;
			sec=zahl%60;
			if(sec>=1||min >= 1){
				timerLabel.setText(min +": " + sec);
			} else {
				timer.stop();
				timerLabel.setText("Ende!");
			}
		}

	}
}