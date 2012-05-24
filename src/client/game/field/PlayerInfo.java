package client.game.field;

import client.data.RunningGame;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class PlayerInfo extends JPanel {
	private long money;
	
	private long population;
	
	private JTextArea moneyPanel;
	private JTextArea populationPanel;
	private JTextArea populationPan;
	private JTextArea moneyPan;
	
	public PlayerInfo(){
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		
		moneyPanel = new JTextArea();
		Font f = new Font( Font.SERIF, Font.PLAIN, 20 );
		moneyPanel.setFont(f);
		moneyPanel.setOpaque(false);
		moneyPanel.setForeground(new Color(150,150,150));
		moneyPanel.setText("Kontostand:");
		moneyPanel.setEditable(false);
        moneyPanel.setWrapStyleWord(true);
		c.gridy=0;
		this.add(moneyPanel,c);
		
		moneyPan = new JTextArea();
		moneyPan.setFont(f);
		moneyPan.setForeground(new Color(150,150,150));
		moneyPan.setOpaque(false);
		moneyPan.setText(Long.toString(RunningGame.getMoney()));
		moneyPan.setEditable(false);
		c.gridy=1;
		this.add(moneyPan,c);
	
		
		populationPanel = new JTextArea();
		populationPanel.setFont(f);
		populationPanel.setOpaque(false);
		populationPanel.setForeground(new Color(150,150,150));
		populationPanel.setText("Population:");
		populationPanel.setEditable(false);
		populationPanel.setWrapStyleWord(true);
		c.gridy=2;
		this.add(populationPanel,c);
		
		populationPan = new JTextArea();
		populationPan.setFont(f);
		populationPan.setOpaque(false);
		populationPan.setForeground(new Color(150,150,150));
		populationPan.setText(Long.toString(RunningGame.getPopulation()));
		populationPan.setEditable(false);
		c.gridy=3;
		this.add(populationPan,c);
		
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

			moneyPan.setText(longToString(money));

			population= RunningGame.getPopulation();
			
			populationPan.setText(longToString(population));
			
		}

	}
	

}
