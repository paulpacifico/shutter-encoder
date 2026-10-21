/*******************************************************************************************
* Copyright (C) 2026 PACIFICO PAUL
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

package shutterencoder.ui.others;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.Bmxtranswrap;
import shutterencoder.library.Dcraw;
import shutterencoder.library.Dvdauthor;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.Xpdfreader;
import shutterencoder.library.Tsmuxer;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

	public class RenderQueue {
	public static JFrame frame;

	/*
	 * Composants
	 */
	private static JPanel topPanel;
	private static JLabel title = new JLabel(Main.language.getProperty("frameFileDeRendus"));
	private JLabel quit;
	private JLabel fullscreen;
	private JLabel reduce;
	private JLabel topImage;
	public static JButton btnStartRender;
	public static JCheckBox caseRunParallel;
	public static JComboBox<String> parallelValue;
	public static JTable table;
	public static DefaultTableModel tableRow;
	public static JScrollPane scrollPane;		
	private static int complete;
	private static Runnable queueBuildCompletion;
	private boolean drag = false;
	public static int filesCompleted = 0;
	
	private static int MousePositionX;
	private static int MousePositionY;

	public static synchronized void setQueueBuildCompletion(Runnable completion) {
		queueBuildCompletion = completion;
	}

	public static void completeQueueBuild() {
		if (frame != null)
			frame.toFront();

		Runnable completion;
		synchronized (RenderQueue.class) {
			completion = queueBuildCompletion;
			queueBuildCompletion = null;
		}

		if (completion != null)
			SwingUtilities.invokeLater(completion);
	}
	
	public RenderQueue() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Utils.background);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Main.language.getProperty("frameFileDeRendus"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 332);
		frame.setResizable(true);
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45,45,45)));
			
			if (System.getProperty("os.name").contains("Mac") == false)
				frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("resources/icon.png"))).getImage());
			
			frame.setLocation(Main.frame.getX() + (Main.frame.getWidth() - frame.getWidth()) / 2, Main.frame.getY() + (Main.frame.getHeight() - frame.getHeight()) / 2);
		}
				
		topPanel();
		table();				
		
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we)
			{
				
				frame.toFront();
		    }
			
			public void windowClosed(WindowEvent arg0) {
				
				if (FFmpeg.isRunning || Dcraw.isRunning || Xpdfreader.isRunning || Dvdauthor.isRunning || Tsmuxer.isRunning || Bmxtranswrap.isRunning)
					Main.btnCancel.doClick();
				
				if (Main.btnCancel.isEnabled() == false)		
				{
					//File de rendus
					if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionMerge")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionInsert")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")) == false
							&& Main.comboFonctions.getSelectedItem().equals("DVD Rip") == false
							&& Main.comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionVideoLevels")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionNormalization")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSceneDetection")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionBlackDetection")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionOfflineDetection")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionWeb")) == false
							&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("itemMyFunctions")) == false)
					{
						Main.iconList.setVisible(true);
						if (Main.iconPresets.isVisible())
						{
							Main.iconPresets.setLocation(Main.iconList.getX() + Main.iconList.getWidth() + 2, 45);
							Main.btnCancel.setBounds(207 + Main.iconList.getWidth(), 46, 101 - Main.iconList.getWidth() -  4, 21);
						}
						else
						{
							Main.iconPresets.setBounds(180, 45, 21, 21);
							Main.btnCancel.setBounds(207, 46, 97, 21);
						}						
					}
					else
					{
						Main.iconList.setVisible(false);
						
						if (Main.iconPresets.isVisible())
						{
							Main.iconPresets.setBounds(180, 45, 21, 21);
							Main.btnCancel.setBounds(207, 46, 97, 21);
						}
						else
						{
							Main.btnCancel.setBounds(184, 46, 120, 21);
						}
					}
					
					Utils.changeFrameVisibility(frame, true);
					Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
				}				
			}
		});
		
    	frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e2)
		    {
		    	if (System.getProperty("os.name").contains("Mac") && drag)
				{
					frame.setShape(null);
				}
				else
				{
					Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		            Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
		            shape1.add(shape2);
		    		frame.setShape(shape1);
				}
		    }
 		});
    	
    	frame.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (drag == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
			}
    		
    	});
    	
    	frame.addMouseListener(new MouseAdapter(){
    		
    		@Override
			public void mousePressed(MouseEvent e) {
				if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR) || frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) || frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
					drag = true;
			}

    		@Override
			public void mouseReleased(MouseEvent e) {
				drag = false;
				
				Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	            Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
	            shape1.add(shape2);
	    		frame.setShape(shape1);
			}
    		
    		
    	});
    	
    	frame.addMouseMotionListener(new MouseMotionListener(){
    		
    		@Override
			public void mouseDragged(MouseEvent e) {
				
				if (drag)
				{
					if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
						frame.setSize(e.getX(), e.getY());		
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
						frame.setSize(e.getX(), frame.getHeight());	
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
						frame.setSize(frame.getWidth(), e.getY());	
					
					if (frame.getWidth() < 400)
						frame.setSize(400, frame.getHeight());
					
					if (frame.getHeight() < 90)
						frame.setSize(frame.getWidth(), 90);
					
					resizeAll();
				}
			}
						
			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getX() >= frame.getWidth() - 10 && e.getY() >= frame.getHeight() - 10)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				else if (e.getX() >= frame.getWidth() - 10)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				else if (e.getY() >= frame.getHeight() - 10)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				else if (drag == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
				
			}
    		
    	});
		    	
		Utils.changeFrameVisibility(frame, false);
		
	}
		
	private void topPanel() {	
		
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(Utils.background);
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		
		quit = new JLabel(new FlatSVGIcon("resources/quit.svg", 15, 15));
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
				
		fullscreen = new JLabel(new FlatSVGIcon("resources/expand.svg", 15, 15));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15, 15);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				if (accept)
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

					double dpiScaleFactor = 1.0;
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			        	dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
			        }
			        
			        int screenHeight = (int) (allScreens[screenIndex].getDisplayMode().getHeight() * dpiScaleFactor);	
					int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);			    				

					if (accept && frame.getHeight() < screenHeight - Main.taskBarHeight)
					{		
						frame.setSize(screenWidth, screenHeight - Main.taskBarHeight);
						
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
					else if (accept)
					{
						frame.setSize(600, 332);
						frame.setLocation(Main.frame.getX() + (Main.frame.getWidth() - frame.getWidth()) / 2, Main.frame.getY() + (Main.frame.getHeight() - frame.getHeight()) / 2);
					}
					
					resizeAll();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				fullscreen.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15));
				accept = false;
			}
			
			
		});
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Main.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(Utils.c42);
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45,45,45)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
		
		reduce = new JLabel(new FlatSVGIcon("resources/minimize.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(fullscreen.getLocation().x - 20, 4, 15, 15);
		
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
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
				reduce.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15));
				accept = false;
			}
			
			
		});
				
		topPanel.add(quit);	
		topPanel.add(fullscreen);
		topPanel.add(reduce);
		topPanel.add(topImage);
		
		quit.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				if (accept)		
				{		  
					if (FFmpeg.isRunning || Dcraw.isRunning || Xpdfreader.isRunning || Dvdauthor.isRunning || Tsmuxer.isRunning || Bmxtranswrap.isRunning)
						Main.btnCancel.doClick();
					
					if (Main.btnCancel.isEnabled() == false)		
					{						
						//File de rendus
						if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionMerge")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionInsert")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")) == false
								&& Main.comboFonctions.getSelectedItem().equals("DVD Rip") == false
								&& Main.comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionVideoLevels")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionNormalization")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSceneDetection")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionBlackDetection")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionOfflineDetection")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionWeb")) == false
								&& Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("itemMyFunctions")) == false)
						{
							Main.iconList.setVisible(true);
							
							if (Main.iconPresets.isVisible())
							{
								Main.iconPresets.setLocation(Main.iconList.getX() + Main.iconList.getWidth() + 2, 45);
								Main.btnCancel.setBounds(207 + Main.iconList.getWidth(), 46, 101 - Main.iconList.getWidth() -  4, 21);
							}
							else
							{
								Main.iconPresets.setBounds(180, 45, 21, 21);
								Main.btnCancel.setBounds(207, 46, 97, 21);
							}
							
						}
						else
						{
							Main.iconList.setVisible(false);
							
							if (Main.iconPresets.isVisible())
							{
								Main.iconPresets.setBounds(180, 45, 21, 21);
								Main.btnCancel.setBounds(207, 46, 97, 21);
							}
							else
							{
								Main.btnCancel.setBounds(184, 46, 120, 21);
							}
						}
						
						Utils.changeFrameVisibility(frame, true);
						Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15));
				accept = false;
			}

						
		});
		
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		frame.getContentPane().add(topPanel);						

		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e))
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

					double dpiScaleFactor = 1.0;
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			        	dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
			        }

			        int screenHeight = (int) (allScreens[screenIndex].getDisplayMode().getHeight() * dpiScaleFactor);	
					int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);		
			        
					if (frame.getHeight() < screenHeight - Main.taskBarHeight)
					{
						frame.setSize(screenWidth, screenHeight - Main.taskBarHeight);
						
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
					else
					{
						frame.setSize(600, 332);
						frame.setLocation(Main.frame.getX() + (Main.frame.getWidth() - frame.getWidth()) / 2, Main.frame.getY() + (Main.frame.getHeight() - frame.getHeight()) / 2);			
					}
					
					resizeAll();
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
			}

			@Override
			public void mouseEntered(MouseEvent e) {					
			}

			@Override
			public void mouseExited(MouseEvent e) {				
			}		

		 });
		 		
		topImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void table() {
		
		JLabel columnFile = new JLabel(Main.language.getProperty("columnFile"));
		columnFile.setFont(new Font("SansSerif", Font.PLAIN, 11));
		
		tableRow = new DefaultTableModel(new Object[][] {}, new String[] {Main.language.getProperty("columnFile"), Main.language.getProperty("columnCommand"), Main.language.getProperty("destination")});
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        table = new JTable(tableRow)
        {
			public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
               return true;
            }                   
        };
        table.setForeground(new Color(235,235,240));
        table.setBackground(Utils.c35);
        table.setDefaultRenderer(String.class, new BoardTableCellRenderer());
		table.setShowVerticalLines(false);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(17);
		table.setOpaque(true);

		table.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && table.getSelectedRowCount() > 0)		
				{
					do
					{
						tableRow.removeRow(table.getSelectedRow());
					}while(table.getSelectedRows().length > 0);
				}
				
				if (table.getRowCount() == 0)
				{
					btnStartRender.setEnabled(false);
					caseRunParallel.setEnabled(true);
					parallelValue.setEnabled(true);					
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {	
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		});
		
		table.getModel().addTableModelListener(new TableModelListener() {

		      public void tableChanged(TableModelEvent e) {
		    	  
					Main.lblCurrentEncoding.setText(Main.language.getProperty("lblEncodageEnCours"));					
					if (Main.caseChangeFolder1.isSelected() == false)
						Main.lblDestination1.setText(Main.language.getProperty("sameAsSource"));
		      }

		    });
		
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add(table);
		scrollPane.setBackground(Utils.c42);
		scrollPane.setOpaque(true);
		scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setValue(RenderQueue.scrollPane.getVerticalScrollBar().getMaximum());
		frame.getContentPane().add(scrollPane);
		
		caseRunParallel = new JCheckBox(Main.language.getProperty("caseRunParallel"));
		caseRunParallel.setName("caseRunParallel");
		caseRunParallel.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseRunParallel.setSize(caseRunParallel.getPreferredSize().width, 23);
		frame.getContentPane().add(caseRunParallel);
		
		caseRunParallel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseRunParallel.isSelected())
				{
					parallelValue.setEnabled(true);
				}
				else
					parallelValue.setEnabled(false);
			}
			
		});
		
		parallelValue = new JComboBox<String>();
		parallelValue.setName("parallelValue");
		parallelValue.setModel(new DefaultComboBoxModel<String>(new String[] { "2","3","4","5","6","7","8","9","10" }));
		parallelValue.setSelectedIndex(0);
		parallelValue.setMaximumRowCount(10);
		parallelValue.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		parallelValue.setEditable(false);	
		parallelValue.setEnabled(false);
		frame.getContentPane().add(parallelValue);
		
		btnStartRender = new JButton(Main.language.getProperty("btnStartRender"));
		btnStartRender.setFont(new Font(Main.boldFont, Font.PLAIN, 12));
		btnStartRender.setEnabled(false);
		frame.getContentPane().add(btnStartRender);
				
		btnStartRender.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//Temps écoulé
				FFmpeg.lblElapsedTime.setVisible(false);
				FFmpeg.elapsedTime = 0;
				FFmpeg.previousElapsedTime = 0;
				
				btnStartRender.setEnabled(false);
				caseRunParallel.setEnabled(false);
				parallelValue.setEnabled(false);
				complete = 0;
				
				Thread render = new Thread(new Runnable() {
					
				public void run() {
					
						int currentProcessedFiles = 0;
						int sendCommands = 0;
						filesCompleted = 0;
						
						for (int i = 0 ; i < tableRow.getRowCount() ; i++)
						{
							String cli[] = tableRow.getValueAt(i, 1).toString().split(" ");
							String cmd = tableRow.getValueAt(i, 1).toString();
							String fileName = tableRow.getValueAt(i, 0).toString();		
							String c = cmd.substring(0, cmd.length() - 1);
							File fileInput = new File(c.substring(c.indexOf("\"") + 1).split("\"")[0]);	
							File fileOut = new File(c.substring(c.lastIndexOf("\"") + 1));	
							if (fileOut.toString().contains("pipe"))
							{
								String s[] = fileOut.toString().split("\\|");
								fileOut = new File(s[0]); 
							}	
							
							VideoPlayerCore.videoPath = null;
							
							if (cmd.contains("pipe:1"))
							{
								Main.caseDisplay.setSelected(true);
							}
							else
								Main.caseDisplay.setSelected(false);
							
							switch (cli[0].toString())
							{
								case "ffmpeg" :
									if (caseRunParallel.isSelected())
									{
										FFmpeg.run(cmd.toString().replace("ffmpeg"," -v verbose"));
										
										//IMPORTANT avoid a bug
										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {}
									}
									else											
										FFmpeg.run(cmd.toString().replace("ffmpeg",""));
									
									break;
								case "bmxtranswrap" :
									Bmxtranswrap.run(cmd.toString().replace("bmxtranswrap",""));
									break;
								case "dvdauthor" :
									Dvdauthor.run(cmd.toString().replace("dvdauthor",""));
									break;
								case "tsMuxeR" :
									Tsmuxer.run(cmd.toString().replace("tsMuxeR",""));
									break;
								case "dcraw" :
									Dcraw.run(cmd.toString().replace("dcraw",""));
									break;
								case "pdftoppm" :
									Xpdfreader.run(cmd.toString().replace("pdftoppm",""));
									break;
							}	
							
							currentProcessedFiles += 1;
							sendCommands += 1;
							UIController.disableAll();
							
							if (caseRunParallel.isSelected())
							{
								Main.btnStart.setEnabled(false);
								table.setRowSelectionInterval(0, i);
								Main.lblCurrentEncoding.setText(filesCompleted + "/" + tableRow.getRowCount() + " " + Main.language.getProperty("filesEnded")); 
								Main.progressBar.setMaximum(tableRow.getRowCount());
								Main.progressBar.setIndeterminate(true);
							}
							else
							{
								table.setRowSelectionInterval(i, i);
								Main.lblCurrentEncoding.setText(fileName); 	
							}
							
							try {	
							
								if (caseRunParallel.isSelected() == false)
								{									
									do {										
										Thread.sleep(100);											
									} while (FFmpeg.runProcess.isAlive() || Bmxtranswrap.isRunning || Dcraw.isRunning || Xpdfreader.isRunning || Dvdauthor.isRunning || Tsmuxer.isRunning);
																	
									//Permet d'attendre si un autre processus se lance
									Thread.sleep(1000);
									
									do {
										Thread.sleep(100);										
									} while (FFmpeg.runProcess.isAlive() || Bmxtranswrap.isRunning || Dcraw.isRunning || Xpdfreader.isRunning || Dvdauthor.isRunning || Tsmuxer.isRunning);	
								}
								else
								{									
									int maxProcesses = Integer.parseInt(parallelValue.getSelectedItem().toString());
									
									if (i == tableRow.getRowCount() - 1 && filesCompleted < tableRow.getRowCount()) //Waiting all files to complete
									{		
										do {
											
											Thread.sleep(100);		
											Main.lblCurrentEncoding.setText(RenderQueue.filesCompleted + "/" + RenderQueue.tableRow.getRowCount() + " " + Main.language.getProperty("filesEnded"));	
											Main.progressBar.setValue(RenderQueue.filesCompleted);
											
											if (FFmpeg.error || FFmpeg.cancelled)
											{															
												filesCompleted++;
												FFmpeg.error = false; //IMPORTANT
												
												if (i != tableRow.getRowCount() - 1) //Do not break if it's the last file
												{
													break;
												}
											}
											
										} while (filesCompleted != tableRow.getRowCount());											
									}
									else if (currentProcessedFiles == maxProcesses || cmd.contains("-pass 1") || FFmpeg.error)
									{
										do {	
											
											Thread.sleep(100);	
											Main.lblCurrentEncoding.setText(RenderQueue.filesCompleted + "/" + RenderQueue.tableRow.getRowCount() + " " + Main.language.getProperty("filesEnded"));	
											Main.progressBar.setValue(RenderQueue.filesCompleted);
											
											if (FFmpeg.error || FFmpeg.cancelled)
											{
												filesCompleted++;		
												break;
											}
											
										} while (filesCompleted < sendCommands && FFmpeg.runProcess.isAlive());
										
										if (currentProcessedFiles == maxProcesses)
											currentProcessedFiles = 0;
									}
								}
								
							} catch (InterruptedException e) {}								

							if (caseRunParallel.isSelected() == false) //At then end of the loop
							{
								lastActions(i, fileName, fileInput, fileOut);
							}
							
							if (Main.cancelled)
								break;							
						}
								
						if (caseRunParallel.isSelected())
						{
							for (int i = 0 ; i < tableRow.getRowCount() ; i++)
							{
								String cmd = tableRow.getValueAt(i, 1).toString();
								String fileName = tableRow.getValueAt(i, 0).toString();		
								String c = cmd.substring(0, cmd.length() - 1);	
								File fileInput = new File(c.substring(c.indexOf("\"") + 1).split("\"")[0]);	
								File fileOut = new File(c.substring(c.lastIndexOf("\"") + 1));	
	
								lastActions(i, fileName, fileInput, fileOut);
							}
						}
													
						boolean enableParallel = true;
						for (int i = 0 ; i < tableRow.getRowCount() ; i++)
						{
							String cli[] = tableRow.getValueAt(i, 1).toString().split(" ");
							if (cli[0].toString().equals("ffmpeg") == false)
							{
								enableParallel = false;
							}
						}
						
						if (enableParallel)
						{
							caseRunParallel.setEnabled(true);
							if (caseRunParallel.isSelected())
							{
								parallelValue.setEnabled(true);
							}
						}
						UIController.endOfFunction();
						Main.btnStart.setText(Main.language.getProperty("btnAddToRender"));
						
						if (tableRow.getRowCount() > 0)
							btnStartRender.setEnabled(true);
					}
				});
				render.start();
			}			
		});      		
		
		resizeAll();
	}	

	private void resizeAll() {
		
		title.setBounds(0, 0, frame.getWidth(), 24);
		
		btnStartRender.setBounds(11, frame.getHeight() - 29, frame.getWidth() - 23 - caseRunParallel.getWidth() - 7 - 40 - 5, 21);
		caseRunParallel.setLocation(frame.getWidth() - caseRunParallel.getWidth() - 14 - 40 - 5, btnStartRender.getY() - 1);
		parallelValue.setBounds(caseRunParallel.getX() + caseRunParallel.getWidth() + 7, caseRunParallel.getY() + 1, 40, 20);
		
		scrollPane.setBounds(10, 38, frame.getWidth() - 20, frame.getHeight() - 74);

		quit.setBounds(frame.getSize().width - 20, 4, 15,15);	
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15,15);
		reduce.setBounds(fullscreen.getLocation().x - 20, 4, 15,15);
		topPanel.setBounds(0, 0, frame.getSize().width, 24);		
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
	}
	
	public static void lastActions(int item, String fileName, File fileInput, File fileOut) {
		
		String cli[] = tableRow.getValueAt(item, 1).toString().split(" ");
			
		if (VideoPlayerUI.comboMode.getSelectedItem().toString().equals(Main.language.getProperty("splitMode")))
			fileOut = new File(fileOut.toString().replace("%03d", "000"));
		
		//Erreurs
		if (FFmpeg.error || fileOut.length() == 0 && Main.caseCreateSequence.isSelected() == false)
		{
			FFmpeg.errorList.append(Main.language.getProperty("file") + " N°" + (item + 1) + " - " + fileName);
		    FFmpeg.errorList.append(System.lineSeparator());
			try {
				fileOut.delete();
			} catch (Exception e) {}		
		}		
		
		//Delete file
		if (Main.caseDeleteSourceFile.isSelected() && fileInput != null && Main.cancelled == false && FFmpeg.error == false)
		{
			if (item + 1 < tableRow.getRowCount() && fileName.equals(tableRow.getValueAt(item + 1, 0)))
			{
				//do not delete
			}
			else
				fileInput.delete();
		}
				
		//Concat mode or Image sequence
		if (Settings.btnSetBab.isSelected() || (Main.grpImageSequence.isVisible() && Main.caseEnableSequence.isSelected()))
		{
			String extension = fileName.substring(fileName.lastIndexOf("."));
			
			File concatList = new File(fileOut.getParent().replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
			
			concatList.delete();
		}
		
		//Annulation
		if (Main.cancelled)
		{
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}

		//Fichiers terminés
		if (Main.cancelled == false && FFmpeg.error == false)
		{
			complete++;
			Main.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));
		}
		
		//Suppression fichiers résiduels OP-Atom
		if (item > 0)
		{			
			if (cli[0].toString().equals("bmxtranswrap"))
			{
				String cmd = tableRow.getValueAt(item - 1, 1).toString();			
				String c = cmd.substring(0, cmd.length() - 1);
				fileOut = new File(c.substring(c.lastIndexOf("\"") + 1));	
				try {
					fileOut.delete();
				} catch (Exception e) {}
			}
		}
		
		final File folder = new File(fileOut.getParent());
		
		//Suppression fichiers résiduels
		if (cli[0].toString().equals("ffmpeg") && tableRow.getValueAt(item, 1).toString().contains("pass 2"))
		{
			if (cli[0].toString().equals("ffmpeg"))
			{
				final String extension =  fileName.substring(fileName.lastIndexOf("."));
			    for (final File fileEntry : folder.listFiles()) {
			        if (fileEntry.isFile()) {
			        	if (fileEntry.getName().contains(fileName.replace(extension, "")) && fileEntry.getName().contains("log"))
			        	{
			        		File fileToDelete = new File(fileEntry.getAbsolutePath());
			        		fileToDelete.delete();
			        	}
			        }
			    }
			}
		    
		}
		
		//Suppression fichiers résiduels DVD
		if (cli[0].toString().equals("dvdauthor"))
		{
		   for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isFile()) {
		        	String ext = fileEntry.toString().substring(fileEntry.toString().lastIndexOf("."));
		        	if (ext.equals(".log") || ext.equals(".xml") || ext.equals(".mpg"))
		        	{
		        		File fileToDelete = new File(fileEntry.getAbsolutePath());
		        		fileToDelete.delete();
		        	}
		        }
		    }
		}
		
		//Suppression fichiers résiduels Bluray
		if (cli[0].toString().equals("tsMuxeR"))
		{
		   for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isFile()) {
		        	String ext = fileEntry.toString().substring(fileEntry.toString().lastIndexOf("."));
		        	if (ext.equals(".meta") || ext.equals(".mkv"))
		        	{
		        		File fileToDelete = new File(fileEntry.getAbsolutePath());
		        		fileToDelete.delete();
		        	}
		        }
		    }
		}
		
		//Suppression fihciers résiduels Conform
		if (cli[0].toString().equals("ffmpeg"))
		{					    		
			File tempMKV = new File(Main.dirTemp + "fileToRewrap.mkv");
			if (tempMKV.exists())
				tempMKV.delete();
		}
		
		//Envoi par e-mail et FTP
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		
		if (tableRow.getValueAt(item, 2).toString().contains("|"))
		{
			String s[] = tableRow.getValueAt(item, 2).toString().split("\\|");

			if (s.length > 2)
			{
				Main.caseChangeFolder2.setSelected(true);
				Main.lblDestination2.setText(s[1].substring(1, s[1].length() - 1));
				Main.caseChangeFolder3.setSelected(true);
				Main.lblDestination3.setText(s[2].substring(1, s[2].length()));
			}
			else
			{
				Main.caseChangeFolder2.setSelected(true);
				Main.lblDestination2.setText(s[1].substring(1, s[1].length()));
				Main.caseChangeFolder3.setSelected(false);
				Main.lblDestination3.setText(Main.language.getProperty("aucune"));
			}		
			
			if (s[0].substring(0, s[0].length() - 1).equals(Main.language.getProperty("sameAsSource")))
			{
				Main.lblDestination1.setText(Main.language.getProperty("sameAsSource"));
				Main.caseChangeFolder1.setSelected(false);
			}
			else
			{
				Main.lblDestination1.setText(s[0].substring(0, s[0].length() - 1));
				Main.caseChangeFolder1.setSelected(true);
			}			
		}
		else
		{
			if (tableRow.getValueAt(item, 2).toString().equals(Main.language.getProperty("sameAsSource")))
			{
				Main.lblDestination1.setText(Main.language.getProperty("sameAsSource"));
				Main.caseChangeFolder1.setSelected(false);
			}
			else
			{
				Main.lblDestination1.setText(tableRow.getValueAt(item, 2).toString());
				Main.caseChangeFolder1.setSelected(true);
			}
			
			Main.caseChangeFolder2.setSelected(false);
			Main.lblDestination2.setText(Main.language.getProperty("aucune"));
			Main.caseChangeFolder3.setEnabled(false);
			Main.caseChangeFolder3.setSelected(false);
			Main.lblDestination3.setText(Main.language.getProperty("aucune"));
		}
		
		if (cli[0].toString().equals("ffmpeg") && tableRow.getValueAt(item, 1).toString().contains("pass 1") == false)
			Utils.copyFile(fileOut);
		
	}	
}
	
//Modifications de la liste d'attente
class BoardTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int index, int col) {

	    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, index, col);

	    setHorizontalAlignment(JLabel.CENTER);
	    setFont(new Font("SansSerif", Font.PLAIN, 12));
	    setForeground(Color.LIGHT_GRAY);
		setOpaque(true);
		
		if (isSelected)
		{
			setBackground(new Color(75,75,80));
		}
		else
		{			
			if (index % 2 == 1)
				setBackground(Utils.c35);
			else
				setBackground(new Color(Utils.c35.getRed() + 9, Utils.c35.getGreen() + 9, Utils.c35.getBlue() + 9));
		}
		
	    setBorder(new LineBorder(new Color(0,0,0,0)));	    
	    return c;
	}
}