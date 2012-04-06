package client.game;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import client.net.Clientsocket;

	
	
public class GameButtonsPanel extends JPanel{
	/**Button to send status ready*/
	private JButton ready;
	/**Button for tank*/
	private JToggleButton tank;
	/**Button for Radar*/
	private JToggleButton radar;
	/**Button for Luftabwehr*/
	private JToggleButton luftabwehr;
	/**Button for Landabwehr*/
	private JToggleButton landabwehr;
	/**Button for Reproduktionszentrum*/
	private JToggleButton repro;
	/**Button for Jagdflugzeuge*/
	private JToggleButton jagd;
	/**Button for Bomber*/
	private JToggleButton bomber;
	/**Button for Raketen*/
	private JToggleButton rakete;
	/**Button for Regionalbank*/
	private JToggleButton geld;
	/**Time Panel which shows countdown*/
	private TimePanel time;
	/**the Connection made to the Server.*/
	private Clientsocket socket;
	
	static String choice;

	
	final ButtonGroup group;

	
	public GameButtonsPanel(Clientsocket s, JToggleButton ready, JFrame gameFrame, GameChatPanel gameChat){
		Color bg = Color.green;
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
		
		ImageIcon rak= new ImageIcon("bilder/Rakete.png");
		rak.setImage(rak.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		rakete = new JToggleButton(rak,false);
		rakete.setActionCommand("rakete");
		rakete.setBackground(bg);
		rakete.setToolTipText("Rakete");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=1;
		c.insets = new Insets(2,0,0,0);
		this.add(rakete, c);
		
		ImageIcon tan= new ImageIcon("bilder/Panzer.png");
		tan.setImage(tan.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		tank = new JToggleButton(tan, false);
		tank.setActionCommand("tank");
		tank.setBackground(bg);
		tank.setToolTipText("Panzer");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=2;
		c.insets = new Insets(2,0,0,0);
		this.add(tank, c);

		ImageIcon jag= new ImageIcon("bilder/Flugzeug.png");
		jag.setImage(jag.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		jagd = new JToggleButton(jag,false);
		jagd.setActionCommand("jagd");
		jagd.setBackground(bg);
		jagd.setToolTipText("Jagdflugzeug");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=1;
		c.gridy=1;
		c.insets = new Insets(2,5,0,0);
		this.add(jagd, c);
		
		ImageIcon bom= new ImageIcon("bilder/Bomber.png");
		bom.setImage(bom.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		bomber = new JToggleButton(bom, false);
		bomber.setActionCommand("bomber");
		bomber.setBackground(bg);
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
		
		ImageIcon rad= new ImageIcon("bilder/Radar.png");
		rad.setImage(rad.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		radar= new JToggleButton(rad, false);
		radar.setActionCommand("radar");
		radar.setBackground(bg);
		radar.setToolTipText("Radar");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=2;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(radar, c);
		
		ImageIcon luf= new ImageIcon("bilder/Flugabwehr.png");
		luf.setImage(luf.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		luftabwehr = new JToggleButton(luf, false);
		luftabwehr.setActionCommand("luft");
		luftabwehr.setBackground(bg);
		luftabwehr.setToolTipText("Luftabwehr");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=2;
		c.gridy=2;
		c.insets = new Insets(2,20,0,0);
		this.add(luftabwehr, c);
		
		ImageIcon lan= new ImageIcon("bilder/Landabwehr.png");
		lan.setImage(lan.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		landabwehr = new JToggleButton(lan, false);
		landabwehr.setActionCommand("land");
		landabwehr.setBackground(bg);
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
		
		
		ImageIcon rep= new ImageIcon("bilder/Repro.png");
		rep.setImage(rep.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		repro = new JToggleButton(rep, false);
		repro.setActionCommand("repro");
		repro.setBackground(bg);
		repro.setToolTipText("Reproduktionszentrum");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=4;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(repro, c);
		
		ImageIcon gel= new ImageIcon("bilder/Bank.png");
		gel.setImage(gel.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		geld = new JToggleButton(gel, false);
		geld.setActionCommand("geld");
		geld.setBackground(bg);
		geld.setToolTipText("Regionalbank");
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=4;
		c.gridy=2;
		c.insets = new Insets(2,20,0,0);
		this.add(geld, c);
		
		
		
		time= new TimePanel(ready, gameFrame, gameChat, socket );
		c.gridwidth=3;
		c.gridheight=3;
		c.gridx=6;
		c.gridy=0;
		this.add(time, c);
		
		group = new ButtonGroup();
		group.add(tank);
		group.add(radar);
		group.add(luftabwehr);
		group.add(landabwehr);
		group.add(repro);
		group.add(jagd);
		group.add(bomber);
		group.add(rakete);
		group.add(geld);
		System.out.println(choice);
		this.setOpaque(false);
		
		selected();
	}


	public String selected() {

		class VoteActionListener implements ActionListener {
			public void actionPerformed(ActionEvent ex) {
				choice = group.getSelection().getActionCommand();
				System.out.println("ACTION Candidate Selected: " + choice);
			}
		}

		ActionListener al = new VoteActionListener();
	    tank.addActionListener(al);
	    radar.addActionListener(al);
	    luftabwehr.addActionListener(al);
	    landabwehr.addActionListener(al);
	    repro.addActionListener(al);
	    jagd.addActionListener(al);
	    bomber.addActionListener(al);
	    rakete.addActionListener(al);
	    geld.addActionListener(al);

	    
	    
	    return choice;
		
	}

}
