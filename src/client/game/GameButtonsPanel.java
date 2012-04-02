package client.game;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import client.net.Clientsocket;

	
	
public class GameButtonsPanel extends JPanel{
	/***/
	private JButton ready;
	
	private JToggleButton tank;
	
	private JToggleButton radar;
	
	private JToggleButton luftabwehr;
	
	private JToggleButton landabwehr;
	
	private JToggleButton repro;
	
	private JToggleButton jagd;
	
	private JToggleButton bomber;
	
	private JToggleButton rakete;
	
	private JToggleButton geld;
	
	TimePanel time;

	private Clientsocket socket;

	
	public GameButtonsPanel(Clientsocket s){
		
		this.socket = s;
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		JLabel attack = new JLabel();
		attack.setText("Angriffelemente:");
		attack.setBackground(new Color(255, 255, 255));
		attack.setOpaque(true);
		attack.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		this.add(attack, c);
		
		
		rakete = new JToggleButton("Bild");
		rakete.setToolTipText("Rakete");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=1;
		c.insets = new Insets(2,0,0,0);
		this.add(rakete, c);
		
		tank = new JToggleButton("Bild");
		tank.setToolTipText("Panzer");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=2;
		c.insets = new Insets(2,0,0,0);
		this.add(tank, c);

		jagd = new JToggleButton("Bild");
		jagd.setToolTipText("Jagdflugzeug");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=1;
		c.gridy=1;
		c.insets = new Insets(2,5,0,0);
		this.add(jagd, c);
		
		bomber = new JToggleButton("Bild");
		bomber.setToolTipText("Bomber");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=1;
		c.gridy=2;
		c.insets = new Insets(2,5,0,0);
		this.add(bomber, c);
		
		
		JLabel defense = new JLabel();
		defense.setText("Verteidigungselemente");
		defense.setBackground(new Color(255, 255, 255));
		defense.setOpaque(true);
		defense.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0,20,0,0);
		this.add(defense, c);
		
		radar= new JToggleButton("Bild");
		radar.setToolTipText("Radar");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=2;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(radar, c);
		
		luftabwehr = new JToggleButton("Bild");
		luftabwehr.setToolTipText("Luftabwehr");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=2;
		c.gridy=2;
		c.insets = new Insets(2,20,0,0);
		this.add(luftabwehr, c);
		
		landabwehr = new JToggleButton("Bild");
		landabwehr.setToolTipText("Landabwehr");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=3;
		c.gridy=1;
		c.insets = new Insets(2,0,0,0);
		this.add(landabwehr, c);
		
		JLabel gebaude = new JLabel();
		gebaude.setText("Aufbauelemente");
		gebaude.setBackground(new Color(255, 255, 255));
		gebaude.setOpaque(true);
		gebaude.setForeground(new Color(50, 50, 50));
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(2,20,0,0);
		this.add(gebaude, c);
		
		
		repro = new JToggleButton("Bild");
		repro.setToolTipText("Reproduktionszentrum");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=4;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(repro, c);
		
		
		geld = new JToggleButton("Bild");
		geld.setToolTipText("Regionalbank");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=4;
		c.gridy=2;
		c.insets = new Insets(2,20,0,0);
		this.add(geld, c);
		
		
		
		
		
		
		time= new TimePanel();
		c.gridwidth=3;
		c.gridheight=3;
		c.gridx=6;
		c.gridy=0;
		this.add(time, c);
		
		ButtonGroup group = new ButtonGroup();
		group.add(tank);
		group.add(radar);
		group.add(luftabwehr);
		group.add(landabwehr);
		group.add(repro);
		group.add(jagd);
		group.add(bomber);
		group.add(rakete);
		group.add(geld);
		
		this.setOpaque(false);
		
	
	}

}
