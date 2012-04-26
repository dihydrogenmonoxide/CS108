package client.game.field;

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
	

	public ObjectInfo(Graphics2D gd, GameObject obj){
				
		gd.setColor(Color.green);
		Font f = new Font( Font.SERIF, Font.PLAIN, 18 );
		gd.setFont(f);
    	gd.drawString("Lebenspunkte: "+ Integer.toString(obj.getHealth()), 10, 20);    	
    	
    	gd.drawString("Objektwer:  "+Integer.toString(obj.getMoney()), 10, 50);
    
	}
	
	
	
	
}
