package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Panel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class MapMakingAssistance extends JFrame
{

	private JPanel contentPane;
	private JFileChooser fc1 = new JFileChooser();

	private IM iw;

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
					MapMakingAssistance frame = new MapMakingAssistance();
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
	public MapMakingAssistance()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 431, 373);
		this.setTitle("Imageshizzle");
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setMnemonic('o');
		mntmOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				OpenImg();
			}
		});
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmOpen);

		fc1.setFileFilter(new FileFilter()
		{
			public boolean accept(File f)
			{
				return f.getName().toLowerCase().endsWith(".jpg")
						|| f.getName().toLowerCase().endsWith(".png")
						|| f.getName().toLowerCase().endsWith(".gif")
						|| f.getName().toLowerCase().endsWith(".tif")
						|| f.getName().toLowerCase().endsWith(".bmp")
						|| f.isDirectory();
			}

			public String getDescription()
			{
				return "Images";
			}
		});

	}

	private void OpenImg()
	{
		if (fc1.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			if (iw != null)
				contentPane.remove(iw);

			iw = new IM(fc1.getSelectedFile().getAbsolutePath());
			iw.setIgnoreRepaint(false);
			this.getContentPane().add(iw, BorderLayout.CENTER);
			this.pack();
			
			
			iw.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent arg0)
				{
					int x = (int) ((arg0.getLocationOnScreen().getX() - iw.getX() - getX() - getContentPane().getX() - 8) * 350000 /iw.getWidth()  + 450000); 
					int y = (int) ((arg0.getLocationOnScreen().getY() - iw.getY() - getY() - getContentPane().getY() - 30) * 200000 /iw.getHeight() + 100000);
					System.out.println(" {"+x+"/"+y+"},");
				}
			});
		}

	}
}

class IM extends Panel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4335642054878074768L;
	public BufferedImage im;

	public IM(String imageName)
	{
		try
		{
			File input = new File(imageName);
			im = ImageIO.read(input);
		} catch (IOException e)
		{
			System.out.println("Error:" + e.getMessage());
		}
		this.setIgnoreRepaint(false);
		this.setPreferredSize(new Dimension(im.getWidth(), im.getHeight()));
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
