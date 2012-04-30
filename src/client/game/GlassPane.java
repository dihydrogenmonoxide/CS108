package client.game;



import client.data.RunningGame;
import client.net.Clientsocket;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;


class GlassPane extends JComponent {
	Point point;
	Font font1 = new Font("TimesRoman",Font.PLAIN,36);
	Clientsocket socket;
	
	public void paint(Graphics g) {
		if (point != null) {
			if(RunningGame.getGamePhase().equals("ANIM")){
				g.setFont(font1);
				g.setColor(Color.red);
				g.fillRect(200, 500, 500, 600);
				g.setColor(Color.black);
				g.drawString("Game is in Animation Phase", 250, 550);		
			}
			else{
				g.setFont(font1);
				g.setColor(Color.green);
				g.drawString("Please wait for opponents", 250, 250);
			}
		}
	}

	public void setPoint(Point p) {
		point = p;
	}

	public GlassPane( Container contentPane, Clientsocket s) {
		this.socket=s;
		CBListener listener = new CBListener( this, contentPane);
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
}

class CBListener extends MouseInputAdapter {
	Toolkit toolkit;
	GlassPane glassPane;

	public CBListener(GlassPane glassPane, Container contentPane) {
		toolkit = Toolkit.getDefaultToolkit();
		this.glassPane = glassPane;
	}

	public void mouseMoved(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseDragged(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseClicked(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseEntered(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseExited(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mousePressed(MouseEvent e) {
		redispatchMouseEvent(e, false);
	}

	public void mouseReleased(MouseEvent e) {
		redispatchMouseEvent(e, true);
	}

	private void redispatchMouseEvent(MouseEvent e,
			boolean repaint) {
		
		Point glassPanePoint = e.getPoint();
		
		
		if (repaint) {
		
			toolkit.beep();
			
			glassPane.setPoint(glassPanePoint);
			glassPane.repaint();
		}
	}

}