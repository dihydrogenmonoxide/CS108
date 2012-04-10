package client.game;


import java.awt.*;
import javax.swing.*;

class GrafikPanel extends JPanel{
    private int ystart,xstart;
    private int yend, xend;
    public GrafikPanel(){
        ystart= 40;
        xstart= 100;
        yend =700;
        xend=700;
    }
    public void paintComponent(Graphics g){
    	g.setColor(Color.red);
        g.drawLine(100,40,xstart,ystart);
    }
    public void setI(){
        if (xend>xstart&&yend>ystart){
        	xstart++;
        	ystart++;
        }
    }
}
class Kurve extends Thread {
    private GrafikPanel line;
    public Kurve(GrafikPanel line) {
        this.line = line;
    }
    public void run() {
        while(true){
            try {sleep(1);} catch (InterruptedException e) {}
            line.repaint();
            line.setI();
        }
    }
}