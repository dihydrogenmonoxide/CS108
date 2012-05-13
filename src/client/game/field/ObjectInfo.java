package client.game.field;

import client.data.GameObject;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class ObjectInfo extends JPanel{
	

	public ObjectInfo(Graphics g, GameObject obj){
		g.setColor(Color.green);
		Font f = new Font( Font.SERIF, Font.PLAIN, 18 );
		g.setFont(f);
    	g.drawString("Lebenspunkte: "+ Integer.toString(obj.getHealth()), 10, 20);    	
    	g.drawString("Objektwert:  "+Integer.toString(obj.getValue()), 10, 50);
    
	}
	

	
}
