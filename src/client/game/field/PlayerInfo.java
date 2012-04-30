package client.game.field;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import client.data.RunningGame;
import client.game.TimePanel.TimeClassMin;

public class PlayerInfo extends JPanel {
	private long money;
	
	private long population;
	
	private JTextArea moneyPanel;
	private JTextArea populationPanel;
	
	public PlayerInfo(){
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		
		moneyPanel = new JTextArea();
		Font f = new Font( Font.SERIF, Font.PLAIN, 20 );
		moneyPanel.setFont(f);
		moneyPanel.setOpaque(false);
		moneyPanel.setForeground(new Color(150,150,150));
		moneyPanel.setText("Kontostand: "+ Long.toString(RunningGame.getMoney()));
		moneyPanel.setEditable(false);
        moneyPanel.setLineWrap(true);
        moneyPanel.setWrapStyleWord(true);
		c.gridx=7;
		c.gridy=2;
		this.add(moneyPanel,c);
	
		
		populationPanel = new JTextArea();
		populationPanel.setFont(f);
		populationPanel.setOpaque(false);
		populationPanel.setForeground(new Color(150,150,150));
		populationPanel.setText("Population: "+ RunningGame.getPopulation());
		populationPanel.setEditable(false);
		populationPanel.setLineWrap(true);
		populationPanel.setWrapStyleWord(true);
		c.gridx=7;
		c.gridy=3;
		this.add(populationPanel,c);
		
		this.setOpaque(false);
		Update tcMin = new Update();
		Timer timer = new Timer(1000, tcMin);
		timer.start();
	}
	
	
	
	public String longToString(long i){
        return Long.toString(i);
      }

	private class Update implements ActionListener {


		public Update(){
		}

		public void actionPerformed(ActionEvent f){
            money=  RunningGame.getMoney();

			moneyPanel.setText("Kontostand:" + longToString(money));

			population= RunningGame.getPopulation();
			
			populationPanel.setText("Population:"+longToString(population));
			
		}

	}
	

}
