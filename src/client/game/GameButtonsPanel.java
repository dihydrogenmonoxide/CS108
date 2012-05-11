package client.game;

import client.net.Clientsocket;
import client.resources.ResourceLoader;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.Border;

	
/**this class provides the buttons to build things.*/
public class GameButtonsPanel extends JPanel{
	/**Button to send status ready*/
	private JButton ready;
	/**Button for tank*/
	private JToggleButton tank;
	/**Button for Luftabwehr*/
	private JToggleButton luftabwehr;
	/**Button for Landabwehr*/
	private JToggleButton bunker;
	/**Button for Reproduktionszentrum*/
	private JToggleButton repro;
	/**Button for Jagdflugzeuge*/
	private JToggleButton jagd;
	/**Button for Bomber*/
	private JToggleButton bomber;
	/**Button for Regionalbank*/
	private JToggleButton geld;
	/**Time Panel which shows countdown*/
	private TimePanel time;
	/**the Connection made to the Server.*/
	private Clientsocket socket;
        
        private ResourceLoader res = new ResourceLoader();
	        
     /**
     * all the available buttons which can be selected at the moment.
     */
    public static enum button
    {

        TANK, FIGHTER, BOMBER, ANTIAIR, BUNKER, RADAR, REPRO, BANK, NONE
    };
    /**
     * holds the selected button.
     */
    public static button choice = button.NONE;

    /**small actionclass which is responsible for assigning choice the correct value.*/
    class buttonAction extends AbstractAction
    {

        button button;
        /**Assigns the icon and the button to the action.
         @param icon the icon of the button.
         @param b the type of button.*/
        public buttonAction(ImageIcon icon, button b)
        {
            super("",icon);
            button = b;
        }

        public void actionPerformed(ActionEvent e)
        {
            choice = button;
        }
    }
        
        
	final ButtonGroup group;

	
	public GameButtonsPanel(Clientsocket s, JFrame game){
                super();
		Color bg = Color.black;
		this.socket = s;
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
                
                //the color of the text for all labels
		Color lblTxtColor = Color.red;
                
                Border cyan = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.cyan);
                Border purple = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.MAGENTA);
                Border buttonBorder = BorderFactory.createCompoundBorder(cyan, purple);
                
		JLabel attack = new JLabel();
		attack.setText("Angriffelemente:");
                attack.setForeground(lblTxtColor);
		attack.setOpaque(false);
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		this.add(attack, c);
		
		
		ImageIcon tan= new ImageIcon(res.load("images/Panzer.png"));
		tan.setImage(tan.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		tank = new JToggleButton(tan, false);
                tank.setAction(new buttonAction(tan, button.TANK));
		tank.setBackground(bg);
		tank.setToolTipText("Panzer");
                tank.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=2;
		c.insets = new Insets(2,0,0,0);
		this.add(tank, c);

		ImageIcon jag= new ImageIcon(res.load("images/Flugzeug.png"));
		jag.setImage(jag.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		jagd = new JToggleButton(jag,false);
		jagd.setAction(new buttonAction(jag, button.FIGHTER));
		jagd.setBackground(bg);
		jagd.setToolTipText("Jagdflugzeug");
                jagd.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=1;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(jagd, c);
		
		ImageIcon bom= new ImageIcon(res.load("images/Bomber.png"));
		bom.setImage(bom.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		bomber = new JToggleButton(bom, false);
		bomber.setAction(new buttonAction(bom, button.BOMBER));
		bomber.setBackground(bg);
		bomber.setToolTipText("Bomber");
                bomber.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=0;
		c.gridy=1;
		c.insets = new Insets(2,0,0,0);
		this.add(bomber, c);
		
		
		JLabel defense = new JLabel();
		defense.setText("Verteidigungselemente:");
		defense.setOpaque(false
				);
		defense.setForeground(lblTxtColor);
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0,20,0,0);
		this.add(defense, c);
		
		ImageIcon lan= new ImageIcon(res.load("images/Landabwehr.png"));
		lan.setImage(lan.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		bunker= new JToggleButton(lan, false);
		bunker.setAction(new buttonAction(lan, button.BUNKER));
		bunker.setBackground(bg);
		bunker.setToolTipText("Landabwehr");
			bunker.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=2;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(bunker, c);
		
		ImageIcon luf= new ImageIcon(res.load("images/Flugabwehr.png"));
		luf.setImage(luf.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		luftabwehr = new JToggleButton(luf, false);
		luftabwehr.setAction(new buttonAction(luf, button.ANTIAIR));
		luftabwehr.setBackground(bg);
		luftabwehr.setToolTipText("Luftabwehr");
                luftabwehr.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=3;
		c.gridy=1;
		c.insets = new Insets(2,0,0,0);
		this.add(luftabwehr, c);
		
		
		JLabel gebaude = new JLabel();
		gebaude.setText("Aufbauelemente:");
		gebaude.setOpaque(false);
		gebaude.setForeground(lblTxtColor);
		c.fill = GridBagConstraints.LINE_END;
		c.gridwidth = 2;
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(2,20,0,0);
		this.add(gebaude, c);
		
		
		ImageIcon rep= new ImageIcon(res.load("images/Repro.png"));
		rep.setImage(rep.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		repro = new JToggleButton(rep, false);
		repro.setAction(new buttonAction(rep, button.REPRO));
		repro.setBackground(bg);
		repro.setToolTipText("Reproduktionszentrum");
                repro.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=4;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(repro, c);
		
		ImageIcon gel= new ImageIcon(res.load("images/Bank.png"));
		gel.setImage(gel.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT));
		geld = new JToggleButton(gel, false);
		geld.setAction(new buttonAction(gel, button.BANK));
		geld.setBackground(bg);
		geld.setToolTipText("Regionalbank");
                geld.setBorder(buttonBorder);
		c.ipadx=1;
		c.ipady=1;
		c.gridwidth=1;
		c.gridheight=1;
		c.gridx=5;
		c.gridy=1;
		c.insets = new Insets(2,20,0,0);
		this.add(geld, c);
		
		
		
		time= new TimePanel(game, socket );
		time.setMinimumSize(new Dimension(200,80));
		c.gridwidth=3;
		c.gridheight=3;
		c.gridx=6;
		c.gridy=0;
		this.add(time, c);
                time.setOpaque(false);
		
		group = new ButtonGroup();
		group.add(tank);
		group.add(bunker);
		group.add(luftabwehr);
		group.add(repro);
		group.add(jagd);
		group.add(bomber);
		group.add(geld);
		this.setOpaque(false);
	}
}
