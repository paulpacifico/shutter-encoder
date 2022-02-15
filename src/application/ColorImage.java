/*******************************************************************************************
* Copyright (C) 2022 PACIFICO PAUL
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
* 
********************************************************************************************/

package application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.DCRAW;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPLAY;
import library.FFPROBE;
import library.XPDF;
import settings.Colorimetry;
import settings.InputAndOutput;

public class ColorImage {
	public static JFrame frame;
	private static int taskBarHeight;
	private static JPanel image = new JPanel();
	private static Thread runProcess = new Thread();
	/*
	 * Composants
	 */
	private JLabel quit;
	private JLabel fullscreen;
	private JLabel reduce;
	private static JPanel topPanel;
	private JScrollBar scrollBar = new JScrollBar();
	int scrollValue = 0;
	private static JLabel title = new JLabel(Shutter.language.getProperty("frameColorImage"));
	ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
	private static String currentImage = "";
	private JLabel topImage;
	private JLabel bottomImage;	
	private static JButton btnOK;
	private static JButton btnOriginal;
	private static JButton btnPreview;
	private static JButton btnExportImage;
	private static JButton btnPrevious;
	private static JButton btnNext;
	private static JButton btnReset;
	private static JPanel backgroundPanel;
	public static JComboBox<String> comboRGB = new JComboBox<String>();
	public static JSlider sliderExposure = new JSlider();
	public static JSlider sliderGamma = new JSlider();
	public static JSlider sliderContrast = new JSlider();
	public static JSlider sliderHighlights = new JSlider();
	public static JSlider sliderMediums = new JSlider();
	public static JSlider sliderShadows = new JSlider();
	public static JSlider sliderWhite = new JSlider();
	public static JSlider sliderBlack = new JSlider();
	public static JSlider sliderBalance = new JSlider();
	public static JSlider sliderHUE = new JSlider();
	public static JSlider sliderRED = new JSlider();
	public static JSlider sliderGREEN = new JSlider();
	public static JSlider sliderBLUE = new JSlider();		
	public static JSlider sliderVibrance = new JSlider();
	public static JComboBox<String> comboVibrance = new JComboBox<String>();
	public static JSlider sliderSaturation = new JSlider();
	public static JSlider sliderGrain = new JSlider();
	public static JSlider sliderVignette = new JSlider();
	public static JComboBox<String> comboRotate = new JComboBox<String>();
	public static JSlider sliderAngle = new JSlider();
	public static JSlider positionVideo;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	/*
	 * Valeurs
	 */
    private static boolean dragWindow;
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;
    public static int allR = 0;
    public static int allG = 0;
    public static int allB = 0;
    public static int highR = 0;
    public static int highG = 0;
    public static int highB = 0;
    public static int mediumR = 0;
    public static int mediumG = 0;
    public static int mediumB = 0;
    public static int lowR = 0;
    public static int lowG = 0;
    public static int lowB = 0;
    private static String balanceAll = "";
    private static String balanceHigh = "";
    private static String balanceMedium = "";
    private static String balanceLow = "";
    public static int vibranceValue = 0;
    public static int vibranceR = 0;
    public static int vibranceG = 0;
    public static int vibranceB = 0;
    
    
 	public ColorImage() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameColorImage"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(1200, 720);
		frame.setResizable(false);		
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    		taskBarHeight = (int) (dim.getHeight() - winSize.height);			
		}
		
		dragWindow = false;
		
    	frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e2)
		    {
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
		    	frame.setShape(shape1);
		    }
 		});
		
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getX() >= (720 - 20) && e.getY() >= (720 - 20) && dragWindow)
				{
			        frame.setSize(e.getX() + 20, e.getY() + 20);	
				}

				resizeAll();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x > frame.getSize().width - 20 || MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y > frame.getSize().height - 20)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				 else 
				{
					if (dragWindow == false)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}				
			
		});

		frame.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.SE_RESIZE_CURSOR)
					dragWindow = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				if (dragWindow)
				{
					if (frame.getSize().width < 1200 || frame.getSize().height < 720)
					{						
						frame.setSize(1200, 720);							
						resizeAll();
					}     				
				}
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();	
					
				loadImage(true);
					
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
					
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				dragWindow = false;
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.SE_RESIZE_CURSOR)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		frame.addWindowListener(new WindowListener() {			
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {	
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {										
				
				frame.toFront();
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});
		
		topPanel();
		
		scrollBar = new JScrollBar();
		scrollBar.setBackground(new Color(50,50,50));
		scrollBar.setOrientation(JScrollBar.VERTICAL);
		scrollBar.setSize(11, frame.getHeight() - topPanel.getHeight());
		scrollBar.setLocation(194, topPanel.getHeight());
		
		scrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {
					int scrollIncrement = scrollBar.getValue() - scrollValue;
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JLabel || c instanceof JSlider && c.getName() != null || c instanceof JComboBox)
						{
							c.setLocation(c.getLocation().x, c.getLocation().y - scrollIncrement);
						}
					}
					scrollValue = scrollBar.getValue();
		      }			
			
		});
		
		frame.getContentPane().add(scrollBar);
		
		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (scrollBar.isVisible())
					scrollBar.setValue(scrollBar.getValue() + e.getWheelRotation() * 10);				
			}
			
		});			
		
		btnPrevious = new JButton(Shutter.language.getProperty("btnPrevious"));
		btnPrevious.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));	
		btnPrevious.setMargin(new Insets(0,0,0,0));
		btnPrevious.setBounds(14, frame.getHeight() - 33, 84, 21);	
		frame.getContentPane().add(btnPrevious);
		
		btnPrevious.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
	      		if (Shutter.fileList.getSelectedIndex() > 0)
	      		{     			
	      			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	
	      			Shutter.fileList.setSelectedIndex(Shutter.fileList.getSelectedIndex() - 1);
	      			
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
	      			
					if (Shutter.scanIsRunning)
					{
						File dir = new File(Shutter.liste.firstElement());
			        	for (File f : dir.listFiles())
			        	{
				        	if (f.isHidden() == false && f.isFile())
				        	{    	    
				        		FFPROBE.Data(f.toString());
				        	}
			        	}
					}
					else		 
					{
						if (Shutter.inputDeviceIsRunning == false)
							FFPROBE.Data(Shutter.fileList.getSelectedValue().toString());
					}
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
					
					positionVideo.setValue(0);
					positionVideo.setMaximum(FFPROBE.totalLength);
					
     				loadImage(true);
     				
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
     				
     				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     				
     				Shutter.enableAll();
	      		}	
			}
			
		});
		
		btnNext = new JButton(Shutter.language.getProperty("btnNext"));
		btnNext.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnNext.setMargin(new Insets(0,0,0,0));
		btnNext.setBounds(btnPrevious.getX() + btnPrevious.getWidth() + 6, btnPrevious.getY(), 84, 21);		
		frame.getContentPane().add(btnNext);
		
		btnNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
	      		if (Shutter.fileList.getSelectedIndex() < Shutter.liste.getSize())
	      		{      				
	      			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	      			Shutter.fileList.setSelectedIndex(Shutter.fileList.getSelectedIndex() + 1);
	      			
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
	      			
					if (Shutter.scanIsRunning)
					{
						File dir = new File(Shutter.liste.firstElement());
			        	for (File f : dir.listFiles())
			        	{
				        	if (f.isHidden() == false && f.isFile())
				        	{    	    
				        		FFPROBE.Data(f.toString());
				        	}
			        	}
					}
					else		 
					{
						if (Utils.inputDeviceIsRunning == false)
							FFPROBE.Data(Shutter.fileList.getSelectedValue().toString());
					}
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
					
					positionVideo.setValue(0);
					positionVideo.setMaximum(FFPROBE.totalLength);
					
	      			loadImage(true);
	      			
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
     				
     				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     				
     				Shutter.enableAll();
	      		}
			}
			
		});
				
		btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(14, btnPrevious.getY() - 21 - 7, btnPrevious.getWidth() + btnNext.getWidth() + 6, 21);		
		frame.getContentPane().add(btnReset);		
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				allR = 0;
				allG = 0;
				allB = 0;
				highR = 0;
				highG = 0;
				highB = 0;
				mediumR = 0;
				mediumG = 0;
			  	mediumB = 0;
			  	lowR = 0;
			  	lowG = 0;
			  	lowB = 0;
			  	vibranceValue = 0;
			  	vibranceR = 0;
			  	vibranceG = 0;
			  	vibranceB = 0;
				balanceAll = "";
				balanceHigh = "";
				balanceMedium = "";
				balanceLow = "";
				sliderExposure.setValue(0);
				sliderGamma.setValue(0);
				sliderContrast.setValue(0);
				sliderHighlights.setValue(0);
				sliderMediums.setValue(0);
				sliderShadows.setValue(0);
				sliderWhite.setValue(0);
				sliderBlack.setValue(0);
				sliderBalance.setValue(6500);
				sliderHUE.setValue(0);
				sliderRED.setValue(0);
				sliderGREEN.setValue(0);
				sliderBLUE.setValue(0);
				sliderVibrance.setValue(0);	
				sliderSaturation.setValue(0);
				sliderGrain.setValue(0);
				sliderVignette.setValue(0);
				sliderAngle.setValue(0);			
					
				//important
				comboRGB.setSelectedIndex(0);
				comboVibrance.setSelectedIndex(0);
				comboRotate.setSelectedIndex(0);
				
				//pas besoin car déjà chargé par ComboRotate
				//loadImage(true);
			}
			
		});
		
		backgroundPanel = new JPanel();
		backgroundPanel.setBackground(new Color(50, 50, 50));
		backgroundPanel.setOpaque(true);
		backgroundPanel.setSize(194, 72);	
		backgroundPanel.setLocation(0, frame.getHeight() - backgroundPanel.getHeight());	
		frame.getContentPane().add(backgroundPanel);
				
		JLabel lblExposure = new JLabel(Shutter.language.getProperty("lblExposure"));
		lblExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblExposure.setBounds(12, 62, 180, 16);		
		frame.getContentPane().add(lblExposure);

		frame.add(lblExposure);
		
		sliderExposure.setName("sliderExposure");
		sliderExposure.setMaximum(100);
		sliderExposure.setMinimum(-100);
		sliderExposure.setValue(0);		
		sliderExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderExposure.setBounds(12, lblExposure.getY() + lblExposure.getHeight(), 180, 22);	
		
		sliderExposure.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderExposure.setValue(0);	
					lblExposure.setText(Shutter.language.getProperty("lblExposure"));
				}
			}		

		});
		
		sliderExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderExposure.getValue() == 0)
				{
					lblExposure.setText(Shutter.language.getProperty("lblExposure"));
				}
				else
				{
					lblExposure.setText(Shutter.language.getProperty("lblExposure") + " " + sliderExposure.getValue());
				}	
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderExposure);
		
		JLabel lblGamma = new JLabel(Shutter.language.getProperty("lblGamma"));
		lblGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGamma.setBounds(12, sliderExposure.getY() + sliderExposure.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblGamma);

		frame.add(lblGamma);
		
		sliderGamma.setName("sliderGamma");
		sliderGamma.setMaximum(90);
		sliderGamma.setMinimum(-90);
		sliderGamma.setValue(0);		
		sliderGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGamma.setBounds(12, lblGamma.getY() + lblGamma.getHeight(), 180, 22);	
		
		sliderGamma.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGamma.setValue(0);	
					lblGamma.setText(Shutter.language.getProperty("lblGamma"));
				}
			}		

		});
	
		sliderGamma.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGamma.getValue() == 0)
				{
					lblGamma.setText(Shutter.language.getProperty("lblGamma"));
				}
				else
				{
					lblGamma.setText(Shutter.language.getProperty("lblGamma") + " " + sliderGamma.getValue());
				}	
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderGamma);
				
		JLabel lblContrast = new JLabel(Shutter.language.getProperty("lblContrast"));
		lblContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblContrast.setBounds(12, sliderGamma.getY() + sliderGamma.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblContrast);
		
		frame.add(lblContrast);
		
		sliderContrast.setName("sliderContrast");
		sliderContrast.setMaximum(100);
		sliderContrast.setMinimum(-100);
		sliderContrast.setValue(0);		
		sliderContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderContrast.setBounds(12, lblContrast.getY() + lblContrast.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderContrast.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderContrast.setValue(0);	
					lblContrast.setText(Shutter.language.getProperty("lblContrast"));
				}
			}

		});
		
		sliderContrast.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderContrast.getValue() == 0)
				{
					lblContrast.setText(Shutter.language.getProperty("lblContrast"));
				}
				else
				{
					lblContrast.setText(Shutter.language.getProperty("lblContrast") + " " + sliderContrast.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderContrast);
		
		JLabel lblWhite = new JLabel(Shutter.language.getProperty("lblWhite"));
		lblWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblWhite.setBounds(12, sliderContrast.getY() + sliderContrast.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblWhite);
		
		frame.add(lblWhite);
		
		sliderWhite.setName("sliderWhite");
		sliderWhite.setMaximum(100);
		sliderWhite.setMinimum(-100);
		sliderWhite.setValue(0);		
		sliderWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderWhite.setBounds(12, lblWhite.getY() + lblWhite.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderWhite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderWhite.setValue(0);	
					lblWhite.setText(Shutter.language.getProperty("lblWhite"));
				}
			}

		});
		
		sliderWhite.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderWhite.getValue() == 0)
				{
					lblWhite.setText(Shutter.language.getProperty("lblWhite"));
				}
				else
				{
					lblWhite.setText(Shutter.language.getProperty("lblWhite") + " " + sliderWhite.getValue());
				}

				loadImage(false);
			}
			
		});
		
		frame.add(sliderWhite);
		
		JLabel lblBlack = new JLabel(Shutter.language.getProperty("lblBlack"));
		lblBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBlack.setBounds(12, sliderWhite.getY() + sliderWhite.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblBlack);
		
		frame.add(lblBlack);
		
		sliderBlack.setName("sliderBlack");
		sliderBlack.setMaximum(100);
		sliderBlack.setMinimum(-100);
		sliderBlack.setValue(0);		
		sliderBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBlack.setBounds(12, lblBlack.getY() + lblBlack.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBlack.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBlack.setValue(0);	
					lblBlack.setText(Shutter.language.getProperty("lblBlack"));
				}
			}

		});
		
		sliderBlack.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBlack.getValue() == 0)
				{
					lblBlack.setText(Shutter.language.getProperty("lblBlack"));
				}
				else
				{
					lblBlack.setText(Shutter.language.getProperty("lblBlack") + " " + sliderBlack.getValue());
				}

				loadImage(false);
			}
			
		});
		
		frame.add(sliderBlack);
		
		JLabel lblHighlights = new JLabel(Shutter.language.getProperty("lblHighlights"));
		lblHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHighlights.setBounds(12, sliderBlack.getY() + sliderBlack.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblHighlights);
		
		frame.add(lblHighlights);
		
		sliderHighlights.setName("sliderHighlights");
		sliderHighlights.setMaximum(100);
		sliderHighlights.setMinimum(-100);
		sliderHighlights.setValue(0);		
		sliderHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHighlights.setBounds(12, lblHighlights.getY() + lblHighlights.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderHighlights.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderHighlights.setValue(0);	
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights"));
				}
			}

		});
		
		sliderHighlights.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHighlights.getValue() == 0)
				{
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights"));
				}
				else
				{
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights") + " " + sliderHighlights.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderHighlights);
		
		JLabel lblMediums = new JLabel(Shutter.language.getProperty("lblMediums"));
		lblMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblMediums.setBounds(12, sliderHighlights.getY() + sliderHighlights.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblMediums);
		
		frame.add(lblMediums);
		
		sliderMediums.setName("sliderMediums");
		sliderMediums.setMaximum(100);
		sliderMediums.setMinimum(-100);
		sliderMediums.setValue(0);		
		sliderMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderMediums.setBounds(12, lblMediums.getY() + lblMediums.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderMediums.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderMediums.setValue(0);	
					lblMediums.setText(Shutter.language.getProperty("lblMediums"));
				}
			}

		});
		
		sliderMediums.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderMediums.getValue() == 0)
				{
					lblMediums.setText(Shutter.language.getProperty("lblMediums"));
				}
				else
				{
					lblMediums.setText(Shutter.language.getProperty("lblMediums") + " " + sliderMediums.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderMediums);
		
		JLabel lblShadows = new JLabel(Shutter.language.getProperty("lblShadows"));
		lblShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblShadows.setBounds(12, sliderMediums.getY() + sliderMediums.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblShadows);
		
		frame.add(lblShadows);
		
		sliderShadows.setName("sliderShadows");
		sliderShadows.setMaximum(100);
		sliderShadows.setMinimum(-100);
		sliderShadows.setValue(0);		
		sliderShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderShadows.setBounds(12, lblShadows.getY() + lblShadows.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderShadows.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderShadows.setValue(0);	
					lblShadows.setText(Shutter.language.getProperty("lblShadows"));
				}
			}

		});
		
		sliderShadows.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderShadows.getValue() == 0)
				{
					lblShadows.setText(Shutter.language.getProperty("lblShadows"));
				}
				else
				{
					lblShadows.setText(Shutter.language.getProperty("lblShadows") + " " + sliderShadows.getValue());
				}

				loadImage(false);
			}
			
		});
		
		frame.add(sliderShadows);
						
		JLabel lblBalance = new JLabel(Shutter.language.getProperty("lblBalance"));
		lblBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBalance.setBounds(12, sliderShadows.getY() + sliderShadows.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblBalance);
		
		frame.add(lblBalance);
		
		sliderBalance.setName("sliderBalance");
		sliderBalance.setMaximum(12000);
		sliderBalance.setMinimum(1000);
		sliderBalance.setValue(6500);		
		sliderBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBalance.setBounds(12, lblBalance.getY() + lblBalance.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBalance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBalance.setValue(6500);	
					lblBalance.setText(Shutter.language.getProperty("lblBalance"));
				}
			}

		});
		
		sliderBalance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBalance.getValue() == 6500)
				{
					lblBalance.setText(Shutter.language.getProperty("lblBalance"));
				}
				else
				{
					lblBalance.setText(Shutter.language.getProperty("lblBalance") + " " + sliderBalance.getValue() + "k");
				}

				loadImage(false);
			}
			
		});
		
		frame.add(sliderBalance);
		
		JLabel lblHUE = new JLabel(Shutter.language.getProperty("lblHUE"));
		lblHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHUE.setBounds(12, sliderBalance.getY() + sliderBalance.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblHUE);
		
		frame.add(lblHUE);
		
		sliderHUE.setName("sliderHUE");
		sliderHUE.setMaximum(100);
		sliderHUE.setMinimum(-100);
		sliderHUE.setValue(0);		
		sliderHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHUE.setBounds(12, lblHUE.getY() + lblHUE.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderHUE.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderHUE.setValue(0);	
					lblHUE.setText(Shutter.language.getProperty("lblHUE"));
				}
			}

		});
		
		sliderHUE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHUE.getValue() == 0)
				{
					lblHUE.setText(Shutter.language.getProperty("lblHUE"));
				}
				else
				{
					lblHUE.setText(Shutter.language.getProperty("lblHUE") + " " + sliderHUE.getValue());
				}

				loadImage(false);
			}
			
		});
		
		frame.add(sliderHUE);
		
		JLabel lblRGB = new JLabel(Shutter.language.getProperty("lblRGB"));
		lblRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblRGB.setBounds(12, sliderHUE.getY() + sliderHUE.getHeight() + 6, lblRGB.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblRGB);
		
		frame.add(lblRGB);
		
		comboRGB.setName("comboRGB");
		comboRGB.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("setAll"), Shutter.language.getProperty("setHigh"), Shutter.language.getProperty("setMedium"), Shutter.language.getProperty("setLow")}));
		comboRGB.setMaximumRowCount(10);
		comboRGB.setEditable(false);
		comboRGB.setSelectedIndex(0);
		comboRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboRGB.setBounds(lblRGB.getX() + lblRGB.getWidth() + 7, lblRGB.getY() - 3, 70, 22);		
		frame.getContentPane().add(comboRGB);
		
		comboRGB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
				{
					sliderRED.setValue(allR);
					sliderGREEN.setValue(allG);
					sliderBLUE.setValue(allB);	
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
				{
					sliderRED.setValue(lowR);
					sliderGREEN.setValue(lowG);
					sliderBLUE.setValue(lowB);						
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
				{
					sliderRED.setValue(mediumR);
					sliderGREEN.setValue(mediumG);
					sliderBLUE.setValue(mediumB);					
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
				{
					sliderRED.setValue(highR);
					sliderGREEN.setValue(highG);
					sliderBLUE.setValue(highB);	
				}				
			}
			
		});
		
		JLabel lblR = new JLabel(Shutter.language.getProperty("lblRED"));
		lblR.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblR.setBounds(12, comboRGB.getY() + comboRGB.getHeight() + 3, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblR);
		
		frame.add(lblR);
				
		sliderRED.setName("sliderRED");
		sliderRED.setMaximum(100);
		sliderRED.setMinimum(-100);
		sliderRED.setValue(0);		
		sliderRED.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderRED.setBounds(12, lblR.getY() + lblR.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderRED.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderRED.setValue(0);
					lblR.setText(Shutter.language.getProperty("lblRED"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						allR = sliderRED.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						lowR = sliderRED.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						mediumR = sliderRED.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						highR = sliderRED.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allR = sliderRED.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowR = sliderRED.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumR = sliderRED.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highR = sliderRED.getValue();
			}
		
		});
		
		sliderRED.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderRED.getValue() == 0)
				{
					lblR.setText(Shutter.language.getProperty("lblRED"));
				}
				else
				{
					lblR.setText(Shutter.language.getProperty("lblRED") + " " + sliderRED.getValue());
				}
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allR = sliderRED.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowR = sliderRED.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumR = sliderRED.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highR = sliderRED.getValue();
														
				loadImage(false);
			}
			
		});
		
		frame.add(sliderRED);
		
		JLabel lblG = new JLabel(Shutter.language.getProperty("lblGREEN"));
		lblG.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblG.setBounds(12, sliderRED.getY() + sliderRED.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblG);
		
		frame.add(lblG);
		
		sliderGREEN.setName("sliderGREEN");
		sliderGREEN.setMaximum(100);
		sliderGREEN.setMinimum(-100);
		sliderGREEN.setValue(0);		
		sliderGREEN.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGREEN.setBounds(12, lblG.getY() + lblG.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderGREEN.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGREEN.setValue(0);
					lblG.setText(Shutter.language.getProperty("lblGREEN"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						allG = sliderGREEN.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						lowG = sliderGREEN.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						mediumG = sliderGREEN.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						highG = sliderGREEN.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allG = sliderGREEN.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowG = sliderGREEN.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumG = sliderGREEN.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highG = sliderGREEN.getValue();
			}
		
		});
		
		sliderGREEN.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGREEN.getValue() == 0)
				{
					lblG.setText(Shutter.language.getProperty("lblGREEN"));
				}
				else
				{
					lblG.setText(Shutter.language.getProperty("lblGREEN") + " " + sliderGREEN.getValue());
				}
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allG = sliderGREEN.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowG = sliderGREEN.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumG = sliderGREEN.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highG = sliderGREEN.getValue();
														
				loadImage(false);
			}
			
		});
		
		frame.add(sliderGREEN);
		
		JLabel lblB = new JLabel(Shutter.language.getProperty("lblBLUE"));
		lblB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblB.setBounds(12, sliderGREEN.getY() + sliderGREEN.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblB);
		
		frame.add(lblB);
		
		sliderBLUE.setName("sliderBLUE");
		sliderBLUE.setMaximum(100);
		sliderBLUE.setMinimum(-100);
		sliderBLUE.setValue(0);		
		sliderBLUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBLUE.setBounds(12, lblB.getY() + lblB.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBLUE.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBLUE.setValue(0);
					lblB.setText(Shutter.language.getProperty("lblBLUE"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						allB = sliderBLUE.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						lowB = sliderBLUE.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						mediumB = sliderBLUE.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						highB = sliderBLUE.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allB = sliderBLUE.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowB = sliderBLUE.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumB = sliderBLUE.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highB = sliderBLUE.getValue();
			}
		
		});
		
		sliderBLUE.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBLUE.getValue() == 0)
				{
					lblB.setText(Shutter.language.getProperty("lblBLUE"));
				}
				else
				{
					lblB.setText(Shutter.language.getProperty("lblBLUE") + " " + sliderBLUE.getValue());
				}
							
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					allB = sliderBLUE.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					lowB = sliderBLUE.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					mediumB = sliderBLUE.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					highB = sliderBLUE.getValue();
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderBLUE);
		
		JLabel lblVibrance = new JLabel(Shutter.language.getProperty("lblVibrance"));
		lblVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVibrance.setBounds(12, sliderBLUE.getY() + sliderBLUE.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblVibrance);
		
		frame.add(lblVibrance);
		
		sliderVibrance.setName("sliderVibrance");
		sliderVibrance.setMaximum(100);
		sliderVibrance.setMinimum(-100);
		sliderVibrance.setValue(0);		
		sliderVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVibrance.setBounds(12, lblVibrance.getY() + lblVibrance.getHeight(), sliderExposure.getWidth(), 22);	
				
		frame.add(sliderVibrance);			
		
		comboVibrance.setName("comboVibrance");
		comboVibrance.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("intensity"), Shutter.language.getProperty("red"), Shutter.language.getProperty("green"), Shutter.language.getProperty("blue")}));
		comboVibrance.setMaximumRowCount(10);
		comboVibrance.setEditable(false);
		comboVibrance.setSelectedIndex(0);
		comboVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboVibrance.setBounds(scrollBar.getX() - scrollBar.getWidth() - 64, lblVibrance.getY() - 3, 70, 22);		
		frame.getContentPane().add(comboVibrance);
		
		comboVibrance.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
				{
					sliderVibrance.setValue(vibranceValue);
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
				{
					sliderVibrance.setValue(vibranceR);					
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
				{
					sliderVibrance.setValue(vibranceG);					
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
				{
					sliderVibrance.setValue(vibranceB);
				}				
			}
			
		});
		
		sliderVibrance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderVibrance.setValue(0);	
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance"));
				
					if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
						vibranceValue = sliderVibrance.getValue();	
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
						vibranceR = sliderVibrance.getValue();					
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
						vibranceG = sliderVibrance.getValue();				
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						vibranceB = sliderVibrance.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
					vibranceValue = sliderVibrance.getValue();	
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
					vibranceR = sliderVibrance.getValue();					
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
					vibranceG = sliderVibrance.getValue();				
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
					vibranceB = sliderVibrance.getValue();
			}			

		});
		
		sliderVibrance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVibrance.getValue() == 0)
				{
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance"));
				}
				else
				{
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance") + " " + sliderVibrance.getValue());
				}
				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
					vibranceValue = sliderVibrance.getValue();	
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
					vibranceR = sliderVibrance.getValue();					
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
					vibranceG = sliderVibrance.getValue();				
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
					vibranceB = sliderVibrance.getValue();
				
				loadImage(false);
			}
			
		});
				
		JLabel lblSaturation = new JLabel(Shutter.language.getProperty("lblSaturation"));
		lblSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblSaturation.setBounds(12, sliderVibrance.getY() + sliderVibrance.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblSaturation);
		
		frame.add(lblSaturation);
		
		sliderSaturation.setName("sliderSaturation");
		sliderSaturation.setMaximum(100);
		sliderSaturation.setMinimum(-100);
		sliderSaturation.setValue(0);		
		sliderSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderSaturation.setBounds(12, lblSaturation.getY() + lblSaturation.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderSaturation.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderSaturation.setValue(0);	
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation"));
				}
			}

		});
		
		sliderSaturation.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderSaturation.getValue() == 0)
				{
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation"));
				}
				else
				{
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation") + " " + sliderSaturation.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderSaturation);
			
		JLabel lblGrain = new JLabel(Shutter.language.getProperty("lblGrain"));
		lblGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGrain.setBounds(12, sliderSaturation.getY() + sliderSaturation.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblGrain);
		
		frame.add(lblGrain);
		
		sliderGrain.setName("sliderGrain");
		sliderGrain.setMaximum(100);
		sliderGrain.setMinimum(-100);
		sliderGrain.setValue(0);		
		sliderGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGrain.setBounds(12, lblGrain.getY() + lblGrain.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderGrain.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGrain.setValue(0);	
					lblGrain.setText(Shutter.language.getProperty("lblGrain"));
				}
			}

		});
		
		sliderGrain.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGrain.getValue() == 0)
				{
					lblGrain.setText(Shutter.language.getProperty("lblGrain"));
				}
				else
				{
					lblGrain.setText(Shutter.language.getProperty("lblGrain") + " " + sliderGrain.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderGrain);	
				
		JLabel lblVignette = new JLabel(Shutter.language.getProperty("lblVignette"));
		lblVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVignette.setBounds(12, sliderGrain.getY() + sliderGrain.getHeight() + 4, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblVignette);
		
		frame.add(lblVignette);
		
		sliderVignette.setName("sliderVignette");
		sliderVignette.setMaximum(100);
		sliderVignette.setMinimum(-100);
		sliderVignette.setValue(0);		
		sliderVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVignette.setBounds(12, lblVignette.getY() + lblVignette.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderVignette.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderVignette.setValue(0);	
					lblVignette.setText(Shutter.language.getProperty("lblVignette"));
				}
			}

		});
		
		sliderVignette.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVignette.getValue() == 0)
				{
					lblVignette.setText(Shutter.language.getProperty("lblVignette"));
				}
				else
				{
					lblVignette.setText(Shutter.language.getProperty("lblVignette") + " " + sliderVignette.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderVignette);		
		
		JLabel lblRotate = new JLabel(Shutter.language.getProperty("caseRotate"));
		lblRotate.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblRotate.setBounds(12, sliderVignette.getY() + sliderVignette.getHeight() + 6, lblRotate.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblRotate);
		
		frame.add(lblRotate);

		comboRotate = new JComboBox<String>();
		comboRotate.setName("comboRotate");
		comboRotate.setModel(new DefaultComboBoxModel<String>(new String[] { Shutter.language.getProperty("aucun"), "90", "-90", "180" }));
		comboRotate.setSelectedIndex(0);
		comboRotate.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboRotate.setEditable(false);
		comboRotate.setBounds(lblRotate.getX() + lblRotate.getWidth() + 7, lblRotate.getY() - 3, 80, 22);
		comboRotate.setMaximumRowCount(20);
		frame.add(comboRotate);
		
		comboRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {	
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();	
					
				loadImage(true);
					
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
					
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
			}
			
		});
		
		JLabel lblAngle = new JLabel(Shutter.language.getProperty("caseAngle"));
		lblAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblAngle.setBounds(12, comboRotate.getY() + comboRotate.getHeight() + 3, lblExposure.getSize().width, 16);		
		frame.getContentPane().add(lblAngle);
		
		sliderAngle.setName("sliderAngle");
		sliderAngle.setMaximum(100);
		sliderAngle.setMinimum(-100);
		sliderAngle.setValue(0);		
		sliderAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderAngle.setBounds(12, lblAngle.getY() + lblAngle.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderAngle.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderAngle.setValue(0);	
					lblAngle.setText(Shutter.language.getProperty("caseAngle"));
				}
			}

		});
		
		sliderAngle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderAngle.getValue() == 0)
				{
					lblAngle.setText(Shutter.language.getProperty("caseAngle"));
				}
				else
				{
					lblAngle.setText(Shutter.language.getProperty("caseAngle") + " " + sliderAngle.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		frame.add(sliderAngle);	
		
		//IMPORTANT
		if ((sliderAngle.getY() + sliderAngle.getHeight() + 7) - backgroundPanel.getY() >= 7)		
		{
			scrollBar.setMaximum((sliderAngle.getY() + sliderAngle.getHeight() + 7) - backgroundPanel.getY());
			scrollBar.setVisible(true);
		}
		else
			scrollBar.setVisible(false);
								
		loadImage(true);
				
		positionVideo = new JSlider();
		if (Shutter.scanIsRunning)
		{
			File dir = new File(Shutter.liste.firstElement());
        	for (File f : dir.listFiles())
        	{
	        	if (f.isHidden() == false && f.isFile())
	        	{    	    
	        		FFPROBE.Data(f.toString());
	        	}
        	}
		}
		else		 
		{
			if (Utils.inputDeviceIsRunning == false)
			{
				if (Shutter.fileList.getSelectedValue() == null)
					Shutter.fileList.setSelectedIndex(0);
				else
					FFPROBE.Data(Shutter.fileList.getSelectedValue().toString());
			}
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
		
		if (FFPROBE.totalLength > 100) //Plus d'une image
			positionVideo.setEnabled(true);
		else
			positionVideo.setEnabled(false);
		
		positionVideo.setMaximum(FFPROBE.totalLength);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		positionVideo.setBounds(212, frame.getHeight() - 33, sliderExposure.getWidth(), 22);	
		frame.getContentPane().add(positionVideo); 
		
		positionVideo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {								
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
						
					loadImage(true);
						
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
			}
			
		});
		
		btnOriginal = new JButton(Shutter.language.getProperty("btnOriginal"));
		btnOriginal.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOriginal.setBounds(positionVideo.getX() + positionVideo.getWidth() + 9, frame.getHeight() - 33, btnOriginal.getPreferredSize().width, 21);		
		frame.getContentPane().add(btnOriginal); 
		
		btnOriginal.addMouseListener(new MouseAdapter(){

			public void mousePressed(MouseEvent e) {	    		
				File file = new File(Shutter.dirTemp + "preview.bmp");  

				try {
		           	image.removeAll();  
		           	
		           	//On charge l'image après la création du fichier pour avoir le bon ratio
		    		image();	
					
					Image imageBMP = ImageIO.read(file);
		            ImageIcon imageIcon = new ImageIcon(imageBMP);
		    		JLabel newImage = new JLabel(imageIcon);
		            imageIcon.getImage().flush();
		            
		    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
		    		newImage.setBounds(0, 0, image.getWidth(), image.getHeight());  
		    		
		    		image.add(newImage);
		    		image.repaint();
		    		frame.getContentPane().repaint();
		    		
				} catch (Exception e1) {}
	    		
			}
			
			public void mouseReleased(MouseEvent e) {
	    		
				loadImage(false);
			}
			
		});
		
		btnPreview = new JButton(Shutter.language.getProperty("preview"));
		btnPreview.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnPreview.setMargin(new Insets(0,0,0,0));
		btnPreview.setBounds(btnOriginal.getX() + btnOriginal.getWidth() + 9, frame.getHeight() - 33, 120, 21);		
		frame.getContentPane().add(btnPreview); 
		
		btnPreview.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				try {
					
					File file = new File (Shutter.fileList.getSelectedValue().toString());
					
					InputAndOutput.getInputAndOutput();
					
					//Slider
					if (positionVideo.getValue() > 0 && FFPROBE.totalLength > 100)
					{
						DecimalFormat tc = new DecimalFormat("00");			
						String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
						String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
						String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
						
						InputAndOutput.inPoint = " -ss " + h + ":" + m + ":" + s + ".0";
					}
					
					String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
					boolean isRaw = false;		    		

					//Erreur FFPROBE avec les fichiers RAW
					switch (extension.toLowerCase()) { 
						case ".3fr":
						case ".arw":
						case ".crw":
						case ".cr2":
						case ".cr3":
						case ".dng":
						case ".kdc":
						case ".mrw":
						case ".nef":
						case ".nrw":
						case ".orf":
						case ".ptx":
						case ".pef":
						case ".raf":
						case ".r3d":
						case ".rw2":
						case ".srw":
						case ".x3f":
							isRaw = true;
						}
						
					if (extension.toLowerCase().equals(".pdf"))
					{
						 XPDF.toFFPROBE(file.toString());	
						 do
							Thread.sleep(100);
						 while (XPDF.isRunning);
					}				
					else if (isRaw)
					{
						 EXIFTOOL.run(file.toString());	
						 do
						 	Thread.sleep(100);						 
						 while (EXIFTOOL.isRunning);
					}
					else
					{
						if (Utils.inputDeviceIsRunning == false)
							FFPROBE.Data(file.toString());
						
			        	do
			            	Thread.sleep(100);   
			        	while (FFPROBE.isRunning);
					}
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.isRunning);
										
					//EQ
					String eq = setEQ(false);					
					
					if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.entrelaced != null && FFPROBE.entrelaced.equals("1"))
						eq += ",yadif=0:" + FFPROBE.fieldOrder + ":0";		

					if (comboRotate.getSelectedIndex() == 1 || comboRotate.getSelectedIndex() == 2 || comboRotate.getSelectedIndex() == 3)
					{
						eq = setRotate(eq);
					}
					
					String filter = " -vf " + '"' + eq;
					
					String compression = " -q:v 0";
					if (Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG") && extension.toLowerCase().equals(".pdf") == false && isRaw == false)
					{
						int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(Shutter.comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));						
						compression = " -q:v " + q;
					}	
					
					//EXR gamma
					String EXRGamma = Colorimetry.setEXRGamma(extension);
					
					//FFPLAY
					if (extension.toLowerCase().equals(".pdf"))
					{
						XPDF.toFFPLAY(filter + '"');
					}
					else if (isRaw)
					{	
						DCRAW.toFFPLAY(filter + '"');
					}
					else if (Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG"))
					{
						String cmd = filter + '"' + " -an -c:v mjpeg" + compression + " -vframes 1 -f nut pipe:play |";
						FFMPEG.toFFPLAY(InputAndOutput.inPoint + EXRGamma + " -i " + '"' + file + '"' + InputAndOutput.outPoint + cmd);
					}
					else
						FFPLAY.run(InputAndOutput.inPoint + EXRGamma + " -fs -i " + '"' + file + '"' + filter + '"');

					do {
						Thread.sleep(100);
					} while (FFMPEG.isRunning == false && FFPLAY.isRunning == false && XPDF.isRunning == false && DCRAW.isRunning == false);
					
					Utils.changeFrameVisibility(frame, true);
					
					do {
						Thread.sleep(100);
					} while((FFMPEG.isRunning && FFMPEG.error == false) || (FFPLAY.isRunning && FFPLAY.error == false) || (XPDF.isRunning && XPDF.error == false) || (DCRAW.isRunning && DCRAW.error == false));	
									
						            	
				} catch (InterruptedException e1) {}				
				finally
				{				
					FFMPEG.enableAll();
					Shutter.caseRunInBackground.setEnabled(false);	
					Shutter.caseRunInBackground.setSelected(false);
					Shutter.btnCancel.setEnabled(false);
					Shutter.tempsRestant.setVisible(false);
					Shutter.progressBar1.setValue(0);
	            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					Utils.changeFrameVisibility(frame, false);
					if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
						Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
					else
						Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
				}	
			}        			
		});
		
		btnExportImage = new JButton(Shutter.language.getProperty("btnExportImage"));
		btnExportImage.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnExportImage.setBounds(btnPreview.getX() + btnPreview.getWidth() + 9, frame.getHeight() - 33, btnExportImage.getPreferredSize().width, 21);		
		frame.getContentPane().add(btnExportImage); 
		
		btnExportImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//Important permet de lancer le runtime process dans FFMPEG
				boolean display = false;
				if (Shutter.caseDisplay.isSelected())
				{
					display = true;
					Shutter.caseDisplay.setSelected(false);
				}
				
				try {							
	            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					File file = new File (Shutter.fileList.getSelectedValue().toString());
					String ext = file.toString().substring(file.toString().lastIndexOf("."));
					
					InputAndOutput.getInputAndOutput();	
					
					//Slider
					if (positionVideo.getValue() > 0 && FFPROBE.totalLength > 100)
					{
						DecimalFormat tc = new DecimalFormat("00");			
						String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
						String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
						String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
						
						InputAndOutput.inPoint = " -ss " + h + ":" + m + ":" + s + ".0";
					}
					
					 //Dossier de sortie
					String sortie;					
					if (Shutter.caseChangeFolder1.isSelected())
						sortie = Shutter.lblDestination1.getText();
					else
					{
						sortie =  file.getParent();
						Shutter.lblDestination1.setText(sortie);
					}
					
					//Fichier de sortie
					String newExtension = ".jpg";							
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
						newExtension = Shutter.comboFilter.getSelectedItem().toString();
					
					File fileOut = new File(sortie + "/" + file.getName().replace(ext,  newExtension)); 	
					if(fileOut.exists())
					{
						int n = 1;
						do {							
							fileOut = new File(sortie + "/" + file.getName().replace(ext, "_" + n + newExtension));	
							n++;
						} while (fileOut.exists());
					}
					
					String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
					boolean isRaw = false;		    		

					//Erreur FFPROBE avec les fichiers RAW
					switch (extension.toLowerCase()) { 
						case ".3fr":
						case ".arw":
						case ".crw":
						case ".cr2":
						case ".cr3":
						case ".dng":
						case ".kdc":
						case ".mrw":
						case ".nef":
						case ".nrw":
						case ".orf":
						case ".ptx":
						case ".pef":
						case ".raf":
						case ".r3d":
						case ".rw2":
						case ".srw":
						case ".x3f":
							isRaw = true;
						}
						
					if (extension.toLowerCase().equals(".pdf"))
					{
						 XPDF.toFFPROBE(file.toString());	
						 do
							Thread.sleep(100);
						 while (XPDF.isRunning);
					}				
					else if (isRaw)
					{
						 EXIFTOOL.run(file.toString());	
						 do
						 	Thread.sleep(100);						 
						 while (EXIFTOOL.isRunning);
					}
					else
					{
						if (Utils.inputDeviceIsRunning == false)
							FFPROBE.Data(file.toString());
			        	
						do
			            	Thread.sleep(100);   
			        	while (FFPROBE.isRunning);
					}	
					
					 // Analyse des données
					 FFPROBE.FrameData(file.toString());	
					 do
					 {
					 	Thread.sleep(100);	
					 }
					 while (FFPROBE.isRunning);
					
					//EQ
					String eq = setEQ(false);	
										
					if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.entrelaced != null && FFPROBE.entrelaced.equals("1"))
						eq += ",yadif=0:" + FFPROBE.fieldOrder + ":0";				 	

					if (comboRotate.getSelectedIndex() == 1 || comboRotate.getSelectedIndex() == 2 || comboRotate.getSelectedIndex() == 3)
					{
						eq = setRotate(eq);
					}
					
					String compression = " -q:v 0";
					if (Shutter.comboFonctions.getSelectedItem().equals("JPEG"))
					{
						int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(Shutter.comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));
						compression = " -q:v " + q;
					}
					
					String cmd = " -vf " + '"' + eq + '"' + " -vframes 1" + compression + " -an -y ";
					
					//EXR gamma
					String EXRGamma = Colorimetry.setEXRGamma(extension);
					
					if (extension.toLowerCase().equals(".pdf"))
					{
						XPDF.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + '"' + fileOut + '"');
					}
					else if (isRaw)
					{
						DCRAW.run(" -v -w -c -q 0 -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + '"' + fileOut + '"');
					}
					else if (Shutter.inputDeviceIsRunning) //Screen capture	
					{
						frame.setVisible(false);
						FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + fileOut + '"');
					}
					else
	          			FFMPEG.run(InputAndOutput.inPoint + EXRGamma + " -i " + '"' + file.toString() + '"' + cmd + '"' + fileOut + '"');		
					
					 do {
		            	Thread.sleep(100);  
		            } while((FFMPEG.isRunning && FFMPEG.error == false) || (XPDF.isRunning && XPDF.error == false) || (DCRAW.isRunning && DCRAW.error == false));
					 
					frame.setVisible(true);
					
				} catch (InterruptedException e1) {}
		        finally 
		        {
					FFMPEG.enableAll();
					Shutter.caseRunInBackground.setEnabled(false);	
					Shutter.caseRunInBackground.setSelected(false);
					Shutter.btnCancel.setEnabled(false);
					Shutter.tempsRestant.setVisible(false);
					Shutter.progressBar1.setValue(0);
	            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		        	
		    		if (display)
		    			Shutter.caseDisplay.setSelected(true);
		        }
				
				if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
				
			}        			
		});
		
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOK.setBounds(btnExportImage.getX() + btnExportImage.getWidth() + 9, frame.getHeight() - 33, frame.getWidth() - (btnExportImage.getX() + btnExportImage.getWidth()) - 25, 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setEQ(true);
				
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            Utils.changeFrameVisibility(frame, true);
	            
	            //Suppression image temporaire	    		
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();				
			}
			
		});
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
		
		Utils.changeFrameVisibility(frame, false);		
	}
				
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getWidth(), 52);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 3, 15, 15);
		
		quit.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				if (accept)		
				{
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);		            
		            Utils.changeFrameVisibility(frame, true);
		            
					//Suppression image temporaire
							    		
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();
									
					Shutter.caseColor.setSelected(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				quit.setIcon(new FlatSVGIcon("contents/quit_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				quit.setIcon(new FlatSVGIcon("contents/quit.svg", 15, 15));
				accept = false;
			}

						
		});
		
		fullscreen = new JLabel(new FlatSVGIcon("contents/max.svg", 15, 15));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 20, 3, 15, 15);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				GraphicsConfiguration config = frame.getGraphicsConfiguration();
				GraphicsDevice myScreen = config.getDevice();
				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice[] allScreens = env.getScreenDevices();
				int screenIndex = -1;
				for (int i = 0; i < allScreens.length; i++) {
				    if (allScreens[i].equals(myScreen))
				    {
				    	screenIndex = i;
				        break;
				    }
				}

				int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	
				int screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();

				if (accept && frame.getHeight() < screenHeight - taskBarHeight)
				{
					if (ImageHeight > ImageWidth)
					{
						frame.setBounds(0,0, screenWidth, screenHeight - taskBarHeight); 	
					}
					else
					{
						int setWidth = (int) ((float) (screenHeight - topPanel.getHeight() - 22 - 17 - taskBarHeight) * ((float) ImageWidth / ImageHeight)) + backgroundPanel.getWidth();
						if (setWidth <= screenWidth)
							frame.setSize(setWidth, screenHeight - taskBarHeight); 
						else
							frame.setSize(screenWidth, screenHeight - taskBarHeight);						
							
						if (System.getProperty("os.name").contains("Windows"))
						{
							frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width,
		        		   					  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);		        		
						}
						else
		        		{
		        			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width,
		        							  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);	
		        		}
		        			
					}						
				}
				else if (accept)
				{
					frame.setSize(1200, 720);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
				}

				resizeAll();
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();	
					
				loadImage(true);
					
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
					
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				fullscreen.setIcon(new FlatSVGIcon("contents/max_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max.svg", 15, 15));
				accept = false;
			}
			
			
		});
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(fullscreen.getLocation().x - 20, 3, 15, 15);
			
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce_pressed.svg", 15, 15));
				accept = true;
			}

			@SuppressWarnings("static-access")
			@Override
			public void mouseReleased(MouseEvent e) {		
				
				if (accept)
				{					
					
					frame.setState(frame.ICONIFIED);	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				reduce.setIcon(new FlatSVGIcon("contents/reduce_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce.svg", 15, 15));
				accept = false;
			}
			
			
		});
	
		bottomImage = new JLabel();
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);
		bottomImage.setBounds(0 ,0, frame.getSize().width, 52);
		
		bottomImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {	
				
				if (down.getClickCount() == 2 && down.getButton() == MouseEvent.BUTTON1)
				{					
					GraphicsConfiguration config = frame.getGraphicsConfiguration();
					GraphicsDevice myScreen = config.getDevice();
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] allScreens = env.getScreenDevices();
					int screenIndex = -1;
					for (int i = 0; i < allScreens.length; i++) {
					    if (allScreens[i].equals(myScreen))
					    {
					    	screenIndex = i;
					        break;
					    }
					}

					int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	
					int screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();

					if (frame.getHeight() < screenHeight - taskBarHeight)
					{
						if (ImageHeight > ImageWidth)
						{
							frame.setBounds(0,0, screenWidth, screenHeight - taskBarHeight); 	
						}
						else
						{
							int setWidth = (int) ((float) (screenHeight - topPanel.getHeight() - 22 - 17 - taskBarHeight) * ((float) ImageWidth / ImageHeight)) + backgroundPanel.getWidth();
							if (setWidth <= screenWidth)
								frame.setSize(setWidth, screenHeight - taskBarHeight); 
							else
								frame.setSize(screenWidth, screenHeight - taskBarHeight);						
								
							if (System.getProperty("os.name").contains("Windows"))
							{
								frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width,
			        		   					  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);		        		
							}
							else
			        		{
			        			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width,
			        							  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);	
			        		}
			        			
						}						
					}
					else
					{
						frame.setSize(1200, 720);
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
						frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
					}
										
					resizeAll();		
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
						
						loadImage(true);
						
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}				
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;	
				
				frame.toFront();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (Shutter.fileList.getSelectedValue() == null)
					Shutter.fileList.setSelectedIndex(0);
				
				if (Shutter.liste.getSize() > 0 && Shutter.fileList.getSelectedValue().equals(currentImage) == false)
				{					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
						
					loadImage(true);
						
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
				}				
			}

			@Override
			public void mouseEntered(MouseEvent e) {					
			}

			@Override
			public void mouseExited(MouseEvent e) {				
			}		

		 });
		 		
		bottomImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());

		topPanel.add(quit);	
		topPanel.add(fullscreen);
		topPanel.add(reduce);
		topPanel.add(topImage);
		topPanel.add(bottomImage);
		
		frame.getContentPane().add(topPanel);
	}
	
	private static void image()
	{		
		image.removeAll(); 
		
		final int containerWidth;
		final int containerHeight;
		
		if (finalHeight > (float) (frame.getWidth() - 48 - sliderExposure.getWidth()) / 1.77f || ImageHeight > ImageWidth)
		{
			containerHeight = (frame.getHeight() - topPanel.getHeight() - 35 - 17);
			containerWidth =  (int) Math.floor((float) (frame.getHeight() - topPanel.getHeight() - 35 - 17) * ImageWidth / ImageHeight);
		}
		else
		{
			containerHeight =  (int) Math.floor((float) (frame.getWidth() - 48 - sliderExposure.getWidth()) * ImageHeight / ImageWidth);
			containerWidth = (frame.getWidth() - 48 - sliderExposure.getWidth());	
		}
		
		image.setSize(containerWidth, containerHeight);
		
		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		
		if (finalHeight > (float) (frame.getWidth() - 48 - sliderExposure.getWidth()) / 1.77f || ImageHeight > ImageWidth)
			image.setLocation((int) Math.floor((float) (frame.getWidth() - containerWidth) / 2) + 100, 62);
		else
			image.setLocation(212,  (int) Math.floor((float) (frame.getHeight() - containerHeight) / 2) + 10);

		image.setLayout(null);        
		image.setOpaque(false);

		frame.getContentPane().add(image);
	}
	
	public static void loadImage(boolean forceRefresh) {
				
		if (forceRefresh)
		{
			Thread waitProcess = new Thread (new Runnable() {
				
				@Override
				public void run() {
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (FFMPEG.isRunning || runProcess.isAlive());
				}
			});
			waitProcess.start();
		}
		
		if (forceRefresh || (FFMPEG.isRunning == false && runProcess.isAlive() == false))
		{
			runProcess = new Thread (new Runnable() {
			
			@Override
			public void run() {
				
				//Important permet de lancer le runtime process dans FFMPEG
				boolean display = false;
				if (Shutter.caseDisplay.isSelected())
				{
					display = true;
					Shutter.caseDisplay.setSelected(false);
				}
				
		        try
		        {		 
					
					if (Shutter.fileList.getSelectedValue() == null)
						Shutter.fileList.setSelectedIndex(0);
					
					currentImage = Shutter.fileList.getSelectedValue();
		        	
		        	File file = new File (Shutter.fileList.getSelectedValue().toString());
		        			        						
					if (Shutter.scanIsRunning)
					{
			            File dir = new File(Shutter.liste.firstElement());
			            for (File f : dir.listFiles())
			            {
			            	if (f.isHidden() == false && f.isFile())
			            	{    	            
			            		file = new File(f.toString());
			            		break;
			            	}
			            }
					}
					
					String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
					boolean isRaw = false;
		    		
					File preview = new File(Shutter.dirTemp + "preview.bmp");
					if (preview.exists() == false)
					{
						//Erreur FFPROBE avec les fichiers RAW
						switch (extension.toLowerCase()) { 
							case ".3fr":
							case ".arw":
							case ".crw":
							case ".cr2":
							case ".cr3":
							case ".dng":
							case ".kdc":
							case ".mrw":
							case ".nef":
							case ".nrw":
							case ".orf":
							case ".ptx":
							case ".pef":
							case ".raf":
							case ".r3d":
							case ".rw2":
							case ".srw":
							case ".x3f":
								isRaw = true;
						}
						
						if (extension.toLowerCase().equals(".pdf"))
						{
							 XPDF.toFFPROBE(file.toString());	
							 do
								Thread.sleep(100);
							 while (XPDF.isRunning);
						}				
						else if (isRaw)
						{
							 EXIFTOOL.run(file.toString());	
							 do
							 	Thread.sleep(100);						 
							 while (EXIFTOOL.isRunning);
						}
						else
						{
							if (Utils.inputDeviceIsRunning == false)
								FFPROBE.Data(file.toString());
							
				        	do
				            	Thread.sleep(100);   
				        	while (FFPROBE.isRunning);
						}	
						
						if (comboRotate.getSelectedIndex() == 1 || comboRotate.getSelectedIndex() == 2)
						{
							Integer iw = ImageWidth;
							Integer ih = ImageHeight;
							ImageWidth = ih;
							ImageHeight = iw;
						}
						
		        		finalWidth = (int) Math.floor(((frame.getHeight() - topPanel.getHeight() - 35 - 17) * ImageWidth) / ImageHeight);
			        	
		        		finalHeight = (int) Math.floor(((frame.getWidth() - 48 - sliderExposure.getWidth()) * ImageHeight) / ImageWidth);
		        	                    	        						
						Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder")+ " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
					}
					
						//InOut	
						InputAndOutput.getInputAndOutput();
						
						//Slider
						if (positionVideo.getValue() > 0 && FFPROBE.totalLength > 100)
						{
							DecimalFormat tc = new DecimalFormat("00");			
							String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
							String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
							String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
							
							InputAndOutput.inPoint = " -ss " + h + ":" + m + ":" + s + ".0";
						}
												
						String deinterlace = "";
						
						if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.entrelaced != null && FFPROBE.entrelaced.equals("1"))
							deinterlace = " -vf yadif=0:" + FFPROBE.fieldOrder + ":0";		
						
						String rotate = "";

						if (comboRotate.getSelectedIndex() == 1 || comboRotate.getSelectedIndex() == 2 || comboRotate.getSelectedIndex() == 3)
						{
							if (deinterlace != "")
							{
								rotate = setRotate(deinterlace);
							}
							else
								rotate = " -vf " + setRotate("");
						}
						
						//Création du fichier preview																		
						String cmd = deinterlace + rotate + " -vframes 1 -an -s " + (frame.getWidth() - 48 - sliderExposure.getWidth()) + "x" + finalHeight + " -y ";	
						if (finalHeight > (float) (frame.getWidth() - 48 - sliderExposure.getWidth()) / 1.77f || ImageHeight > ImageWidth)
							cmd = deinterlace + rotate + " -vframes 1 -an -s " + Math.round(finalWidth / 2) * 2 + "x" +  Math.round((frame.getHeight() - topPanel.getHeight() - 35 - 17) / 2) * 2 + " -y ";
					
						//EXR gamma
						String EXRGamma = Colorimetry.setEXRGamma(extension);
						
						if (new File(Shutter.dirTemp + "preview.bmp").exists() == false)
						{											   		
							if (extension.toLowerCase().equals(".pdf"))
							{
								XPDF.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (isRaw)
							{	
								DCRAW.run(" -v -w -c -q 0 -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (Shutter.inputDeviceIsRunning) //Screen capture		
							{
								frame.setVisible(false);
								FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');
							}
							else									
			          			FFMPEG.run(InputAndOutput.inPoint + EXRGamma + " -i " + '"' + file.toString() + '"' + cmd + '"' + preview + '"');			
							
				            do {
				            	Thread.sleep(100);  
				            } while((FFMPEG.isRunning && FFMPEG.error == false) || (XPDF.isRunning && XPDF.error == false) || (DCRAW.isRunning && DCRAW.error == false));
				            
				            frame.setVisible(true);
						}					
							
						//EQ
						String eq = setEQ(false);
						
						//Histogram
						//String histogram = setHistogram(eq); //Strange colors with the overlay
						
						String finalEQ = "";
						if (eq != "")
							finalEQ = " -vf " + '"' + eq + '"';						
						
						//Screen capture
						if (Shutter.inputDeviceIsRunning && preview.exists() == false)
						{
							cmd = " -vframes 1 -an -s " + (frame.getWidth() - 48 - sliderExposure.getWidth()) + "x" + finalHeight + finalEQ;	
							if (finalHeight > (float) (frame.getWidth() - 48 - sliderExposure.getWidth()) / 1.77f || ImageHeight > ImageWidth)
								cmd = " -vframes 1 -an -s " + finalWidth + "x" + (frame.getHeight() - topPanel.getHeight() - 35 - 17) + finalEQ;	
							
							frame.setVisible(false);
							FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');
						}				
						else
							FFMPEG.run(EXRGamma + " -v quiet -i " + '"' + preview + '"' + finalEQ +  " -c:v bmp -f image2pipe pipe:-");

						do {
	    					Thread.sleep(10);
	    				} while (FFMPEG.process.isAlive() == false);
						
						frame.setVisible(true);

						InputStream videoInput = FFMPEG.process.getInputStream();			
						
						InputStream is = new BufferedInputStream(videoInput);
						Image imageBMP = ImageIO.read(is);

	          			if (FFMPEG.error == false && imageBMP != null)
			            {
				           	image.removeAll();  
				           	
				           	//On charge l'image après la création du fichier pour avoir le bon ratio
				    		image();	            	    		
				    		
				            ImageIcon imageIcon = new ImageIcon(imageBMP);
				    		JLabel newImage = new JLabel(imageIcon);
				            imageIcon.getImage().flush();
				            
				    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
				            if (finalHeight > (float) (frame.getWidth() - 48 - sliderExposure.getWidth()) / 1.77f || ImageHeight > ImageWidth)
				            	newImage.setBounds(0, 0,  (int) Math.floor((float) (frame.getHeight() - topPanel.getHeight() - 35 - 17) * ImageWidth / ImageHeight), (frame.getHeight() - topPanel.getHeight() - 35 - 17));  
				            else
				            	newImage.setBounds(0, 0, (frame.getWidth() - 48 - sliderExposure.getWidth()),  (int) Math.floor((float) (frame.getWidth() - 48 - sliderExposure.getWidth()) * ImageHeight / ImageWidth)); 
				    		
				    		image.add(newImage);
				    		image.repaint();
							frame.getContentPane().repaint();
				    		
							Shutter.tempsRestant.setVisible(false);
				            Shutter.progressBar1.setValue(0);	      
			            }
			        }
				    catch (Exception e)
				    {				
				    	e.printStackTrace();
			 	        JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
				    }
			        finally 
			        {
			        	Shutter.enableAll();
	          			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
	        			else
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
	          			
			    		if (display)
			    			Shutter.caseDisplay.setSelected(true);
			        }
				}
			});
			runProcess.start();
		}
	}
	
	protected static String setHistogram(String eq) {
		String histogram = "";
		
		if (eq != "") 
			histogram = " -vf " + '"' + eq + ",";
		else
			histogram = " -vf " + '"';
		
		histogram += "split=2[a][b];[b]format=yuva444p,histogram=levels_mode=logarithmic:components=1:level_height=50:fgopacity=0.5:bgopacity=0.5,scale=-1:30[b];[a][b]overlay=x=main_w-overlay_w:y=main_h-overlay_h" + '"';
		
		//histogram += "split=2[a][b];[b]format=gbrp,waveform=filter=lowpass:scale=ire:graticule=green:flags=numbers+dots:components=7:display=overlay:bgopacity=0.5[b];[a][b]overlay=x=main_w-overlay_w:y=main_h-overlay_h" + '"';
				
		return histogram;
	}

	protected static String setGrain(String eq) {
		if (sliderGrain.getValue() != 0)
		{		
			if (eq != "")
				eq += ",";

			if (sliderGrain.getValue() > 0)
				eq += "unsharp=la=" + (float) sliderGrain.getValue() / 50;
			else
				eq += "bm3d=sigma=" + (float) (0 - sliderGrain.getValue()); 
		}
		
		return eq;
	}
	
	protected static String setRotate(String eq) {

		if (comboRotate.getSelectedIndex() != 0)
		{
			if (eq != "")
				eq += ",";
			
			switch (comboRotate.getSelectedItem().toString()) 
			{
				case "90":
					eq += "transpose=1";
					break;
				case "-90":
					eq += "transpose=2";
					break;
				case "180":
					eq += "transpose=1,transpose=1";
					break;
			}
		}

		return eq;
	}
	
	protected static String setAngle(String eq) {
		
		if (sliderAngle.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			float angle;
			if (sliderAngle.getValue() > 0)
				angle = (float) ((float) ((float) sliderAngle.getValue() / 10) * Math.PI) / 180;
			else
				angle = (float) ((float) (0 - (float) sliderAngle.getValue() / 10) * Math.PI) / 180;
			
			float ratio = (float) ImageWidth / ImageHeight;
			float h = (float) ( (float) ImageHeight / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
			float w = (float) h * ratio;
			if (ratio < 1)
			{
				ratio = (float) ImageHeight / ImageWidth;
				w = (float) ( (float) ImageWidth / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
				h = (float) w * ratio;
			}
			

			
			w = (float) (2 - ((float) ImageWidth / w));
			h = (float) (2 - ((float) ImageHeight / h));			
			
			eq += "rotate=" + ((float) sliderAngle.getValue() / 10) + "*PI/180:ow=iw*" + w + ":oh=ih*" + h; 
		}
		
		return eq;
	}
	
	protected static String setVignette(String eq) {
		if (sliderVignette.getValue() != 0)
		{		
			if (eq != "")
				eq += ",";

			if (sliderVignette.getValue() > 0)
				eq += "vignette=PI/" + (float) (100 - sliderVignette.getValue()) / 5; 
			else
				eq += "vignette=PI/" + (float) (100 + sliderVignette.getValue()) / 5 + ":mode=backward"; 
		}
		
		return eq;
	}

	protected static String setVibrance(String eq) {
		if (vibranceValue != 0)
		{
			if (eq != "")
				eq += ",";			

			eq += "vibrance=" + (float) (vibranceValue) / 50 + ":rbal=" + (float) (100 + vibranceR) / 100 + ":gbal=" + (float) (100 + vibranceG) / 100 + ":bbal=" + (float) (100 + vibranceB) / 100;
		}
		
		return eq;
	}
	
	protected static String setSaturation(String eq) {
		if (sliderSaturation.getValue() != 0)
		{
			if (eq != "")
				eq += ",";			
			
			eq += "eq=saturation=" + ((float) (sliderSaturation.getValue() + 100) / 100);
		}
		
		return eq;
	}

	protected static String setBalance(String eq) {		
		float r = (float) sliderRED.getValue() / 400;
		float g = (float) sliderGREEN.getValue() / 400;
		float b = (float) sliderBLUE.getValue() / 400;
		
		if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
			balanceAll = "rs="+r+":gs="+g+":bs="+b+":rm="+r+":gm="+g+":bm="+b+":rh="+r+":gh="+g+":bh="+b;			
			
		if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
			balanceLow = "rs="+r+":gs="+g+":bs="+b;	
		
		else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
			balanceMedium = "rm="+r+":gm="+g+":bm="+b;	
		
		else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
			balanceHigh = "rh="+r+":gh="+g+":bh="+b;
		
		if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
		{
			if (eq != "")
				eq += ",";
			
			eq += "colorbalance=" + balanceAll;
		}
		
		//Permet de compléter tout l'eq à chaque fois
		if (balanceLow != "" || balanceMedium != "" || balanceHigh != "")
		{
			if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
				eq += ",colorbalance=";
			else if (eq != "")
				eq += ",colorbalance=";
			else
				eq = "colorbalance=";
			
			if (balanceLow == "")
				balanceLow = "rs=0:gs=0:bs=0";
			
			if (balanceMedium == "")
				balanceMedium = "rm=0:gm=0:bm=0";
			
			if (balanceHigh == "")
				balanceHigh = "rh=0:gh=0:bh=0";
			
			eq += balanceLow + ":" + balanceMedium + ":" + balanceHigh;
		}


		return eq;
	}

	protected static String setContrast(String eq) {
		if (sliderContrast.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "eq=contrast=" + (1 + (float) sliderContrast.getValue() / 100); 
		}
		
		return eq;
	}
	
	protected static String setWB(String eq) {
		if (sliderBalance.getValue() != 6500)
		{
			if (eq != "")
				eq += ",";

			eq += "colortemperature=" + sliderBalance.getValue(); 
		}
		
		return eq;
	}
	
	protected static String setHUE(String eq) {
		if (sliderHUE.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "hue=h=" + sliderHUE.getValue(); 
		}
		
		return eq;
	}
	
	protected static String setWhite(String eq) {
		if (sliderWhite.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
				
			if (sliderWhite.getValue() > 0)
			{
				float value = 1 - (float) sliderWhite.getValue() / 200;				
				eq += "colorlevels=rimax=" + value + ":gimax=" + value + ":bimax=" + value; 
			}
			else
			{
				float value = 1 + (float) sliderWhite.getValue() / 200;
				eq += "colorlevels=romax=" + value + ":gomax=" + value + ":bomax=" + value; 
			}
		}
		
		return eq;
	}
	
	protected static String setBlack(String eq) {
		if (sliderBlack.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
				
			if (sliderBlack.getValue() > 0)
			{
				float value = (float) sliderBlack.getValue() / 200;				
				eq += "colorlevels=romin=" + value + ":gomin=" + value + ":bomin=" + value; 				 
			}
			else
			{
				float value = 0 - (float) sliderBlack.getValue() / 200;
				eq += "colorlevels=rimin=" + value + ":gimin=" + value + ":bimin=" + value;
			}
				
		}
		
		return eq;
	}

	protected static String setShadows(String eq) {
		if (sliderShadows.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
										
			if (sliderShadows.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 0.25/" + (0.25f - (float) (0 - (float) sliderShadows.getValue() / 500)) + " 0.5/0.5 0.75/0.75 0.875/0.875 1/1'"; 
			else
				eq += "curves=master=" + "'0/0 " + (0.25f - (float) sliderShadows.getValue() / 500) + "/0.25 0.5/0.5 0.625/0.625 0.75/0.75 0.875/0.875 1/1" + "'"; 
		}
		
		return eq;
	}
	
	protected static String setMediums(String eq) {
		if (sliderMediums.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
					
			if (sliderMediums.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 " + (0.5 - (float) sliderMediums.getValue() / 400) + "/" + (0.5 + (float) sliderMediums.getValue() / 400) + " 1/1" + "'"; 										
			else
				eq += "curves=master=" + "'" + "0/0 " + (0.5 + (float) (0 - (float) sliderMediums.getValue() / 400)) + "/" + (0.5 - (float) (0 - (float) sliderMediums.getValue() / 400)) + " 1/1" + "'"; 
		}
		
		return eq;
	}

	protected static String setHighlights(String eq) {
		if (sliderHighlights.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			if (sliderHighlights.getValue() > 0)
				eq += "curves=master=" + "'" + "0/0 0.125/0.125 0.25/0.25 0.375/0.375 0.5/0.5 " + (0.75f - (float) sliderHighlights.getValue() / 500) + "/0.75 1/1" + "'"; 										
			else
				eq += "curves=master=" + "'" + "0/0 0.125/0.125 0.25/0.25 0.375/0.375 0.5/0.5 0.75/" + (0.75f - (float) (0 - (float) sliderHighlights.getValue() / 500)) + " 1/1'"; 
		}
		
		return eq;
	}

	protected static String setExposure(String eq) {
		if (sliderExposure.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "exposure=" + (float) ((float) sliderExposure.getValue() / 100) * 3; 
		}
		
		return eq;
	}
	
	protected static String setGamma(String eq) {		
		if (sliderGamma.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "eq=gamma=" + (1 + (float) sliderGamma.getValue() / 100); 
		}
		
		return eq;
	}

	public static String setEQ(boolean finalEQ) {
		
		String eq = "";

		if (finalEQ == false)
		{
			//LUTs
			eq = Colorimetry.setLUT(eq);	
			
			//Levels
			eq = Colorimetry.setLevels(eq);
			
			//Colormatrix
			eq = Colorimetry.setColormatrix(eq);
		}
		
		//Exposure
		eq = setExposure(eq);
		
		//Gamma
		eq = setGamma(eq);
		
		//Contrast
		eq = setContrast(eq);
		
		//White
		eq = setWhite(eq);

		//Black
		eq = setBlack(eq);
		
		//Highlights 
		eq = setHighlights(eq);
		
		//Mediums 
		eq = setMediums(eq);
		
		//Shadows 
		eq = setShadows(eq);
				
		//White Balance 
		eq = setWB(eq);
		
		//Hue
		eq = setHUE(eq);
				
		//Balance
		eq = setBalance(eq);
		
		//Vibrance
		eq = setVibrance(eq);
		
		//Saturation
		eq = setSaturation(eq);
		
		//Grain
		eq = setGrain(eq);
		
		if (finalEQ)
		{
			//Rotate
			eq = setRotate(eq);
		}
		
		//Angle
		eq = setAngle(eq);
		
		//Vignette
		eq = setVignette(eq);
		
		//FinalEQ
		Shutter.finalEQ = eq.replace("\"", "'");
		
		return eq;
	}
	
	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(100);
				} while (frame == null && frame.isVisible() == false);
								
				File fXmlFile = encFile;
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
				
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						
						for (Component p : frame.getContentPane().getComponents())
						{				
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{								
								if (p instanceof JSlider)
								{
									//Value
									((JSlider) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																		
									//State
									((JSlider) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JSlider) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
								else if (p instanceof JComboBox)
								{		
									if (p.getName().equals("comboRotate"))
									{
										comboRotate.setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
									}								
								}
							}							
						}
						
						if  (eElement.getElementsByTagName("Type").item(0).getFirstChild().getTextContent().equals("String"))
						{							
							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("allR"))
								allR = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("allG"))
								allG = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("allB"))
							{
								allB = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								comboRGB.setSelectedIndex(0);
								setBalance("");
							}
							
							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("highR"))
								highR = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("highG"))
								highG = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("highB"))
							{
								highB = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								comboRGB.setSelectedIndex(1);
								setBalance("");
							}
							
							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("mediumR"))
								mediumR = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("mediumG"))
								mediumG = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("mediumB"))
							{
								mediumB = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								comboRGB.setSelectedIndex(2);
								setBalance("");
							}
							
							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("lowR"))
								lowR = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("lowG"))
								lowG = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("lowB"))
							{
								lowB = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								comboRGB.setSelectedIndex(3);
								setBalance("");
							}
							
							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("vibranceValue"))
								vibranceValue = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("vibranceR"))
								vibranceR = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("vibranceG"))
								vibranceG = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
							else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("vibranceB"))
							{
								vibranceB = Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								comboVibrance.setSelectedIndex(0);
							}
						}
					}
				}		
				
			comboRGB.setSelectedIndex(0);	
				
			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {}
			} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
			
			Shutter.enableAll();
			
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}
		
	private void resizeAll() {
		
		topPanel.setBounds(0,0,frame.getSize().width, 52);
		
		topImage.setLocation(frame.getSize().width / 2 - topImage.getSize().width / 2, 0);
		quit.setLocation(frame.getSize().width - 20, 3);	
		fullscreen.setLocation(quit.getLocation().x - 20, 3);
		reduce.setLocation(fullscreen.getLocation().x - 20, 3);
		
		scrollBar.setSize(11, frame.getHeight() - topPanel.getHeight());
						
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);					
		bottomImage.setBounds(0 ,0, frame.getSize().width, 52);
		
		title.setBounds(0, 0, frame.getWidth(), 52);
		
		positionVideo.setBounds(212, frame.getHeight() - 33, sliderExposure.getWidth(), 22);
		btnOriginal.setBounds(positionVideo.getX() + positionVideo.getWidth() + 9, frame.getHeight() - 33, btnOriginal.getPreferredSize().width, 21);	
		btnPreview.setBounds(btnOriginal.getX() + btnOriginal.getWidth() + 9, frame.getHeight() - 33, 120, 21);	
		btnExportImage.setBounds(btnPreview.getX() + btnPreview.getWidth() + 9, frame.getHeight() - 33, btnExportImage.getPreferredSize().width, 21);	
		btnOK.setBounds(btnExportImage.getX() + btnExportImage.getWidth() + 9, frame.getHeight() - 33, frame.getWidth() - (btnExportImage.getX() + btnExportImage.getWidth()) - 25, 21); 	
		
		btnPrevious.setBounds(14, frame.getHeight() - 33, 84, 21);
		btnNext.setBounds(btnPrevious.getX() + btnPrevious.getWidth() + 6, btnPrevious.getY(), 84, 21);		
		btnReset.setBounds(14, btnPrevious.getY() - 21 - 7, btnPrevious.getWidth() + btnNext.getWidth() + 6, 21);	
		backgroundPanel.setLocation(0, frame.getHeight() - backgroundPanel.getHeight());
		
		scrollBar.setValue(0);
		
		if ((sliderAngle.getY() + sliderAngle.getHeight() + 7) - backgroundPanel.getY() >= 7)		
		{
			scrollBar.setMaximum((sliderAngle.getY() + sliderAngle.getHeight() + 7) - backgroundPanel.getY());
			scrollBar.setVisible(true);
		}
		else
			scrollBar.setVisible(false);
	}
}