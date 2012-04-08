package test.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import shared.Log;
import shared.game.MapManager;

public class MapTest
{
	private final static int xSize = 1300;

	private JFrame frame;
	private IMG iw;
	private JFileChooser fc1 = new JFileChooser();

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
		iw = new IMG(xSize);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);
		contentPane.add(iw, BorderLayout.CENTER);
		fc1.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.getName().toLowerCase().endsWith(".png")
						|| f.isDirectory();
			}

			public String getDescription()
			{
				return "Portäibl netwörk gräfiks (*.png)";
			}

		});

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setMnemonic('s');
		mntmSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				save();
			}
		});
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmSave);
		
		frame.pack();
	}
	
	private void save()
	{
		if (fc1.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				if (fc1.getSelectedFile().getAbsolutePath()
						.toLowerCase().endsWith(".png"))
					ImageIO.write(iw.im, "png", fc1.getSelectedFile());
				else
					ImageIO.write(iw.im, "png", new File(fc1
							.getSelectedFile().getAbsolutePath()
							+ ".png"));
			} catch (IOException e)
			{
				// true story
			}
		}

	}

}

@SuppressWarnings("serial")
class IMG extends Panel
{

	// note: this implementation is in fact QUICK AND DIRTY! do not use for the
	// game GUI like that!
	// no really! don't! (you can of course use it if you optimize it)
	public BufferedImage im;

	public IMG(int szx)
	{
		long time = System.currentTimeMillis();
		im = MapManager.renderMap(4, szx);
		Log.InformationLog("Rendered a new map in "
				+ (System.currentTimeMillis() - time) + " MS");
		this.setIgnoreRepaint(false);
		this.setPreferredSize(new Dimension(im.getWidth(), im.getHeight()));
	}

	public void paint(Graphics g)
	{
		g.drawImage(im, 0, 0, null);
	}
}
