package client.game;



import client.data.GamePhases;
import client.data.RunningGame;
import client.game.field.GameFieldPanel;
import client.net.Clientsocket;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;


class GlassPane extends JComponent 
{
	Point point;
	Font font1 = new Font("TimesRoman",Font.PLAIN,36);
	Clientsocket socket;
	
	public void paint(Graphics g) 
	{
		if (point != null)
		{
			if(RunningGame.getGamePhase()==GamePhases.ANIM)
			{
				g.setFont(font1);
				g.setColor(Color.red);
				g.fillRect(0, GameFieldPanel.MAP_HEIGHT, GameFieldPanel.MAP_WIDTH, GameFrame.screenY-GameFieldPanel.MAP_HEIGHT);
				g.setColor(Color.blue);
				g.drawString("Game is in Animation Phase", 50, GameFieldPanel.MAP_HEIGHT+100);	
			}
			if(RunningGame.getGamePhase()==GamePhases.BUILD)
			{
				g.setFont(font1);
				g.setColor(Color.green);
				g.drawString("Please wait for opponents", 250, 250);
			}
		}
	}

	public void setPoint(Point p)
	{
		point = p;
	}

	public GlassPane( Container contentPane, Clientsocket s) 
	{
                super();
		this.socket=s;
		CBListener listener = new CBListener( this, contentPane);
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
}

class CBListener extends MouseInputAdapter
{
	Toolkit toolkit;
	GlassPane glassPane;

	public CBListener(GlassPane glassPane, Container contentPane)
	{
		toolkit = Toolkit.getDefaultToolkit();
		this.glassPane = glassPane;
	}

	public void mouseMoved(MouseEvent e) 
	{
		redispatchMouseEvent(e, false);
	}

	public void mouseDragged(MouseEvent e)
	{
		redispatchMouseEvent(e, false);
	}

	public void mouseClicked(MouseEvent e) 
	{
		redispatchMouseEvent(e, false);
	}

	public void mouseEntered(MouseEvent e) 
	{
		redispatchMouseEvent(e, false);
	}

	public void mouseExited(MouseEvent e)
	{
		redispatchMouseEvent(e, false);
	}

	public void mousePressed(MouseEvent e)
	{
		redispatchMouseEvent(e, false);
	}

	public void mouseReleased(MouseEvent e) 
	{
		redispatchMouseEvent(e, true);
	}

	private void redispatchMouseEvent(MouseEvent e, boolean repaint) 
	{
		
		Point glassPanePoint = e.getPoint();
		
		
		if (repaint)
		{
		
			toolkit.beep();
			
			glassPane.setPoint(glassPanePoint);
			glassPane.repaint();
		}
	}

}