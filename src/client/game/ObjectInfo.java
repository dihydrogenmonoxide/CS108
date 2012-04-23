package client.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import client.data.GameObject;
import client.data.RunningGame;

public class ObjectInfo extends JPanel{

	public ObjectInfo(Graphics2D gd, Collection<GameObject> c){
				
		gd.setColor(Color.green);
    	gd.drawString("Lebenspunkte: "+ RunningGame.getHealth(), 10, 10);
    	
    
	}
	
	
	
	
}
