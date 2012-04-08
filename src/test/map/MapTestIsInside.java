package test.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import javax.swing.JFrame;

import shared.game.MapManager;

@SuppressWarnings("serial")
public class MapTestIsInside extends JFrame
{
	private IMGT iw;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MapTestIsInside frame = new MapTestIsInside();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MapTestIsInside()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 431, 373);
		this.setTitle("Imageshizzle");
		iw = new IMGT();
		iw.setIgnoreRepaint(false);
		this.getContentPane().add(iw, BorderLayout.CENTER);
		this.pack();
		
		
		iw.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				int x = (int) ((arg0.getX()) * 350000 /iw.getWidth()  + 450000); 
				int y = (int) ((arg0.getY()) * 200000 /iw.getHeight() + 100000);
				int fieldID = MapManager.getFieldNumber(x, y);
				if(!MapManager.isInside(fieldID, x, y))
					System.out.println("DAFUQ? "+"("+x+"/"+y+") -> "+fieldID);
				else
					System.out.println("("+x+"/"+y+") -> "+fieldID);
			}
		});
		
		pack();	

	}

}

class IMGT extends Panel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4335642054878074768L;
	public BufferedImage im;

	public IMGT()
	{
		im = MapManager.renderMap(0, 900);
		setPreferredSize(new Dimension(im.getWidth(), im.getHeight()));
	}

	public void paint(Graphics g)
	{
		g.drawImage(im, 0, 0, null);
	}

	BufferedImage deepCopy()
	{
		ColorModel cm = im.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = im.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

}
