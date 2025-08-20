/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
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
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.BMXTRANSWRAP;
import library.DCRAW;
import library.DVDAUTHOR;
import library.FFMPEG;
import library.PDF;
import library.TSMUXER;
import settings.FunctionUtils;

	public class RenderQueue {
	public static JFrame frame;

	/*
	 * Composants
	 */
	private static JPanel topPanel;
	private static JLabel title = new JLabel(Shutter.language.getProperty("frameFileDeRendus"));
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
	private boolean drag = false;
	public static int filesCompleted = 0;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	public RenderQueue() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Utils.bg32);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameFileDeRendus"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 332);
		frame.setResizable(true);
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45,45,45)));
			
			if (System.getProperty("os.name").contains("Mac") == false)
				frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			
			frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);
		}
				
		topPanel();
		table();				
		
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we)
			{
				
				frame.toFront();
		    }
			
			public void windowClosed(WindowEvent arg0) {
				
				if (FFMPEG.isRunning || DCRAW.isRunning || PDF.isRunning || DVDAUTHOR.isRunning || TSMUXER.isRunning || BMXTRANSWRAP.isRunning)
					Shutter.btnCancel.doClick();
				
				if (Shutter.btnCancel.isEnabled() == false)		
				{
					//File de rendus
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionMerge")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false
							&& Shutter.comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionVideoLevels")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionNormalization")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBlackDetection")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionOfflineDetection")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionWeb")) == false
							&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("itemMyFunctions")) == false)
					{
						Shutter.iconList.setVisible(true);
						if (Shutter.iconPresets.isVisible())
						{
							Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
							Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
						}
						else
						{
							Shutter.iconPresets.setBounds(180, 45, 21, 21);
							Shutter.btnCancel.setBounds(207, 46, 97, 21);
						}						
					}
					else
					{
						Shutter.iconList.setVisible(false);
						
						if (Shutter.iconPresets.isVisible())
						{
							Shutter.iconPresets.setBounds(180, 45, 21, 21);
							Shutter.btnCancel.setBounds(207, 46, 97, 21);
						}
						else
						{
							Shutter.btnCancel.setBounds(184, 46, 120, 21);
						}
					}
					
					Utils.changeFrameVisibility(frame, true);
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
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
		            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
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
	            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
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
		topPanel.setBackground(Utils.bg32);
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
				
		fullscreen = new JLabel(new FlatSVGIcon("contents/max.svg", 15, 15));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15, 15);
			
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

					if (accept && frame.getHeight() < screenHeight - Shutter.taskBarHeight)
					{		
						frame.setSize(screenWidth, screenHeight - Shutter.taskBarHeight);
						
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
						frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);
					}
					
					resizeAll();
				}
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
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45,45,45)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(fullscreen.getLocation().x - 20, 4, 15, 15);
		
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
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				if (accept)		
				{		  
					if (FFMPEG.isRunning || DCRAW.isRunning || PDF.isRunning || DVDAUTHOR.isRunning || TSMUXER.isRunning || BMXTRANSWRAP.isRunning)
						Shutter.btnCancel.doClick();
					
					if (Shutter.btnCancel.isEnabled() == false)		
					{						
						//File de rendus
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionMerge")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false
								&& Shutter.comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionVideoLevels")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionNormalization")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBlackDetection")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionOfflineDetection")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionWeb")) == false
								&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("itemMyFunctions")) == false)
						{
							Shutter.iconList.setVisible(true);
							
							if (Shutter.iconPresets.isVisible())
							{
								Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
								Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
							}
							else
							{
								Shutter.iconPresets.setBounds(180, 45, 21, 21);
								Shutter.btnCancel.setBounds(207, 46, 97, 21);
							}
							
						}
						else
						{
							Shutter.iconList.setVisible(false);
							
							if (Shutter.iconPresets.isVisible())
							{
								Shutter.iconPresets.setBounds(180, 45, 21, 21);
								Shutter.btnCancel.setBounds(207, 46, 97, 21);
							}
							else
							{
								Shutter.btnCancel.setBounds(184, 46, 120, 21);
							}
						}
						
						Utils.changeFrameVisibility(frame, true);
						Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
					}
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
		
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		frame.getContentPane().add(topPanel);						

		topImage.addMouseListener(new MouseListener() {

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

					double dpiScaleFactor = 1.0;
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			        	dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
			        }

			        int screenHeight = (int) (allScreens[screenIndex].getDisplayMode().getHeight() * dpiScaleFactor);	
					int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);		
			        
					if (frame.getHeight() < screenHeight - Shutter.taskBarHeight)
					{
						frame.setSize(screenWidth, screenHeight - Shutter.taskBarHeight);
						
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
						frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);			
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

	@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
	private void table() {
		
		JLabel columnFile = new JLabel(Shutter.language.getProperty("columnFile"));
		columnFile.setFont(new Font("SansSerif", Font.PLAIN, 11));
		
		tableRow = new DefaultTableModel(new Object[][] {}, new String[] {Shutter.language.getProperty("columnFile"), Shutter.language.getProperty("columnCommand"), Shutter.language.getProperty("destination")});
		
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
		    	  
					Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));					
					if (Shutter.caseChangeFolder1.isSelected() == false)
						Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		      }

		    });
		
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add(table);
		scrollPane.setBackground(Utils.c42);
		scrollPane.setOpaque(true);
		scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setValue(RenderQueue.scrollPane.getVerticalScrollBar().getMaximum());
		frame.getContentPane().add(scrollPane);
		
		caseRunParallel = new JCheckBox(Shutter.language.getProperty("caseRunParallel"));
		caseRunParallel.setName("caseRunParallel");
		caseRunParallel.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
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
		parallelValue.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		parallelValue.setEditable(false);	
		parallelValue.setEnabled(false);
		frame.getContentPane().add(parallelValue);
		
		btnStartRender = new JButton(Shutter.language.getProperty("btnStartRender"));
		btnStartRender.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		btnStartRender.setEnabled(false);
		frame.getContentPane().add(btnStartRender);
				
		btnStartRender.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//Temps écoulé
				FFMPEG.tempsEcoule.setVisible(false);
				FFMPEG.elapsedTime = 0;
				FFMPEG.previousElapsedTime = 0;
				
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
							
							if (cmd.contains("pipe:1"))
							{
								Shutter.caseDisplay.setSelected(true);
							}
							else
								Shutter.caseDisplay.setSelected(false);
							
							switch (cli[0].toString())
							{
								case "ffmpeg" :
									if (caseRunParallel.isSelected())
									{
										FFMPEG.run(cmd.toString().replace("ffmpeg"," -v verbose"));
										
										//IMPORTANT avoid a bug
										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {}
									}
									else											
										FFMPEG.run(cmd.toString().replace("ffmpeg",""));
									
									break;
								case "bmxtranswrap" :
									BMXTRANSWRAP.run(cmd.toString().replace("bmxtranswrap",""));
									break;
								case "dvdauthor" :
									DVDAUTHOR.run(cmd.toString().replace("dvdauthor",""));
									break;
								case "tsMuxeR" :
									TSMUXER.run(cmd.toString().replace("tsMuxeR",""));
									break;
								case "dcraw" :
									DCRAW.run(cmd.toString().replace("dcraw",""));
									break;
							}	
							
							currentProcessedFiles += 1;
							sendCommands += 1;
							Shutter.disableAll();
							
							if (caseRunParallel.isSelected())
							{
								Shutter.btnStart.setEnabled(false);
								table.setRowSelectionInterval(0, i);
								Shutter.lblCurrentEncoding.setText(filesCompleted + "/" + tableRow.getRowCount() + " " + Shutter.language.getProperty("filesEnded")); 
								Shutter.progressBar1.setMaximum(tableRow.getRowCount());
								Shutter.progressBar1.setIndeterminate(true);
							}
							else
							{
								table.setRowSelectionInterval(i, i);
								Shutter.lblCurrentEncoding.setText(fileName); 	
							}
							
							try {	
							
								if (caseRunParallel.isSelected() == false)
								{									
									do {										
										Thread.sleep(100);											
									} while (FFMPEG.runProcess.isAlive() || BMXTRANSWRAP.isRunning || DCRAW.isRunning || PDF.isRunning || DVDAUTHOR.isRunning || TSMUXER.isRunning);
																	
									//Permet d'attendre si un autre processus se lance
									Thread.sleep(1000);
									
									do {
										Thread.sleep(100);										
									} while (FFMPEG.runProcess.isAlive() || BMXTRANSWRAP.isRunning || DCRAW.isRunning || PDF.isRunning || DVDAUTHOR.isRunning || TSMUXER.isRunning);	
								}
								else
								{									
									int maxProcesses = Integer.parseInt(parallelValue.getSelectedItem().toString());
									
									if (i == tableRow.getRowCount() - 1 && filesCompleted < tableRow.getRowCount()) //Waiting all files to complete
									{		
										do {
											
											Thread.sleep(100);		
											Shutter.lblCurrentEncoding.setText(RenderQueue.filesCompleted + "/" + RenderQueue.tableRow.getRowCount() + " " + Shutter.language.getProperty("filesEnded"));	
											Shutter.progressBar1.setValue(RenderQueue.filesCompleted);
											
											if (FFMPEG.error || FFMPEG.cancelled)
											{															
												filesCompleted++;
												FFMPEG.error = false; //IMPORTANT
												
												if (i != tableRow.getRowCount() - 1) //Do not break if it's the last file
												{
													break;
												}
											}
											
										} while (filesCompleted != tableRow.getRowCount());											
									}
									else if (currentProcessedFiles == maxProcesses || cmd.contains("-pass 1") || FFMPEG.error)
									{
										do {	
											
											Thread.sleep(100);	
											Shutter.lblCurrentEncoding.setText(RenderQueue.filesCompleted + "/" + RenderQueue.tableRow.getRowCount() + " " + Shutter.language.getProperty("filesEnded"));	
											Shutter.progressBar1.setValue(RenderQueue.filesCompleted);
											
											if (FFMPEG.error || FFMPEG.cancelled)
											{
												filesCompleted++;		
												break;
											}
											
										} while (filesCompleted < sendCommands && FFMPEG.runProcess.isAlive());
										
										if (currentProcessedFiles == maxProcesses)
											currentProcessedFiles = 0;
									}
								}
								
							} catch (InterruptedException e) {}								

							if (caseRunParallel.isSelected() == false) //At then end of the loop
							{
								lastActions(i, fileName, fileInput, fileOut);
							}
							
							if (Shutter.cancelled)
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
						Shutter.enfOfFunction();
						Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
						
						if (tableRow.getRowCount() > 0)
							btnStartRender.setEnabled(true);
						
					}//End Run
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
				
		//Erreurs
		if (FFMPEG.error || fileOut.length() == 0 && Shutter.caseCreateSequence.isSelected() == false)
		{
			FFMPEG.errorList.append(Shutter.language.getProperty("file") + " N°" + (item + 1) + " - " + fileName);
		    FFMPEG.errorList.append(System.lineSeparator());
			try {
				fileOut.delete();
			} catch (Exception e) {}		
		}		
		
		//Delete file
		if (Shutter.caseDeleteSourceFile.isSelected() && fileInput != null && Shutter.cancelled == false && FFMPEG.error == false)
		{
			if (item + 1 < tableRow.getRowCount() && fileName.equals(tableRow.getValueAt(item + 1, 0)))
			{
				//do not delete
			}
			else
				fileInput.delete();
		}
				
		//Concat mode or Image sequence
		if (Settings.btnSetBab.isSelected() || (Shutter.grpImageSequence.isVisible() && Shutter.caseEnableSequence.isSelected()) || VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
		{
			String extension = fileName.substring(fileName.lastIndexOf("."));
			
			File concatList = new File(fileOut.getParent().replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
			
			concatList.delete();
		}
		
		//Annulation
		if (Shutter.cancelled)
		{
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}

		//Fichiers terminés
		if (Shutter.cancelled == false && FFMPEG.error == false)
		{
			complete++;
			Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));
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
			File tempMKV = new File(Shutter.dirTemp + "fileToRewrap.mkv");
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
				Shutter.caseChangeFolder2.setSelected(true);
				Shutter.lblDestination2.setText(s[1].substring(1, s[1].length() - 1));
				Shutter.caseChangeFolder3.setSelected(true);
				Shutter.lblDestination3.setText(s[2].substring(1, s[2].length()));
			}
			else
			{
				Shutter.caseChangeFolder2.setSelected(true);
				Shutter.lblDestination2.setText(s[1].substring(1, s[1].length()));
				Shutter.caseChangeFolder3.setSelected(false);
				Shutter.lblDestination3.setText(Shutter.language.getProperty("aucune"));
			}		
			
			if (s[0].substring(0, s[0].length() - 1).equals(Shutter.language.getProperty("sameAsSource")))
			{
				Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
				Shutter.caseChangeFolder1.setSelected(false);
			}
			else
			{
				Shutter.lblDestination1.setText(s[0].substring(0, s[0].length() - 1));
				Shutter.caseChangeFolder1.setSelected(true);
			}			
		}
		else
		{
			if (tableRow.getValueAt(item, 2).toString().equals(Shutter.language.getProperty("sameAsSource")))
			{
				Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
				Shutter.caseChangeFolder1.setSelected(false);
			}
			else
			{
				Shutter.lblDestination1.setText(tableRow.getValueAt(item, 2).toString());
				Shutter.caseChangeFolder1.setSelected(true);
			}
			
			Shutter.caseChangeFolder2.setSelected(false);
			Shutter.lblDestination2.setText(Shutter.language.getProperty("aucune"));
			Shutter.caseChangeFolder3.setEnabled(false);
			Shutter.caseChangeFolder3.setSelected(false);
			Shutter.lblDestination3.setText(Shutter.language.getProperty("aucune"));
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