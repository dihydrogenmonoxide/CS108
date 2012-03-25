package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import shared.Log;
import shared.game.MapManager;

public class MapTest
{

	private JFrame frame;

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
					MapTest window = new MapTest();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MapTest()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setTitle("Map Test");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		IMG iw = new IMG(1500);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);
		contentPane.add(iw, BorderLayout.CENTER);
		frame.pack();
	}

}

@SuppressWarnings("serial")
class IMG extends Panel
{
	
	//note: this implementation is in fact QUICK AND DIRTY! do not use for the game GUI like that!
	//no really! don't! (you can of course use it if you optimize it)
	public BufferedImage  im;
	public IMG(int szx) 
	{
		long time = System.currentTimeMillis();
		im = MapManager.renderMap(4, szx);
		Log.InformationLog("Rendered a new map in "+(System.currentTimeMillis()-time)+" MS");
		this.setIgnoreRepaint(false);
		this.setPreferredSize(new Dimension(im.getWidth(), im.getHeight()));
	}


	public void paint(Graphics g) 
	{
		g.drawImage(im, 0, 0, null);
	}
}
