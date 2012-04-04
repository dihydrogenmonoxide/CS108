package client.game;



import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;


class GlassPane extends JComponent {
	Point point;
	Font font1 = new Font("TimesRoman",Font.PLAIN,36);

	public void paint(Graphics g) {
		if (point != null) {
			g.setColor(Color.green);
			g.setFont(font1);
			g.drawString("Please wait for opponents", 250, 250);
			
				
			}
		}
	

	public void setPoint(Point p) {
		point = p;
	}

	public GlassPane(JToggleButton ready, GameChatPanel gameChat, Container contentPane) {
		CBListener listener = new CBListener(ready, gameChat, this, contentPane);
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
}

class CBListener extends MouseInputAdapter {
	Toolkit toolkit;
	Component liveButton;
	Component gameChat;
	GlassPane glassPane;
	Container contentPane;
	boolean inDrag = false;

	public CBListener(Component liveButton, Component gameChat, GlassPane glassPane, Container contentPane) {
		toolkit = Toolkit.getDefaultToolkit();
		this.liveButton = liveButton;
		this.gameChat = gameChat;
		this.glassPane = glassPane;
		this.contentPane = contentPane;
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
		inDrag = false;
	}

	private void redispatchMouseEvent(MouseEvent e,
			boolean repaint) {
		boolean inButton = false;
		Point glassPanePoint = e.getPoint();
		Component component = null;
		Container container = contentPane;
		Point containerPoint = SwingUtilities.convertPoint(
				glassPane,
				glassPanePoint, 
				contentPane);
		int eventID = e.getID();


		component = SwingUtilities.getDeepestComponentAt(
				container,
				containerPoint.x,
				containerPoint.y);

		if (component == null) {
			return;
		}
		
		/*ChatPanel doe't work yet if glassPane is set true*/
		
		
		if (component.equals(gameChat)) {	//Button funktioniert
			inButton = true;
			testForDrag(eventID);
		}

		if (inButton || inDrag) {
			Point componentPoint = SwingUtilities.convertPoint(
					glassPane,
					glassPanePoint, 
					component);
			component.dispatchEvent(new MouseEvent(component,
					eventID,
					e.getWhen(),
					e.getModifiers(),
					componentPoint.x,
					componentPoint.y,
					e.getClickCount(),
					e.isPopupTrigger()));
		}

		if (repaint) {
			toolkit.beep();
			glassPane.setPoint(glassPanePoint);
			glassPane.repaint();
		}
	}

	private void testForDrag(int eventID) {
		if (eventID == MouseEvent.MOUSE_PRESSED) {
			inDrag = true;
		}
	}
}