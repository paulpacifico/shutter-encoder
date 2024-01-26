/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JFrame;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.MEDIAINFO;

	public class Informations {
	public static JFrame frame;
	
	/*
	 * Composants
	 */
	private static JPanel topPanel;
	private boolean drag = false;
	private JLabel quit;
	private JLabel reduce;
	private JLabel topImage;
	private JLabel bottomImage;
	public static JPanel tabPanel;
	public static JLabel lblWait;
	public static JLabel lblArrows;
	public static JTabbedPane infoTabbedPane = new JTabbedPane(JTabbedPane.TOP);

	private static int MousePositionX;
	private static int MousePositionY;
	
	public Informations() {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameInformations"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 670);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Shutter.taskBarHeight = (int) (dim.getHeight() - winSize.height);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);		
				
		lblWait = new JLabel(Shutter.language.getProperty("lblWait"));
		lblWait.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 20));
		lblWait.setForeground(Color.WHITE);
		lblWait.setSize(lblWait.getPreferredSize().width, 40);
		lblWait.setLocation(frame.getSize().width / 2 - lblWait.getSize().width / 2, frame.getSize().height / 2);
		lblWait.setVisible(true);
		frame.getContentPane().add(lblWait);
		
		lblArrows = new JLabel("▲▼");
		lblArrows.setHorizontalAlignment(SwingConstants.CENTER);
		lblArrows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 20));
		lblArrows.setSize(new Dimension(frame.getSize().width, 20));
		lblArrows.setLocation(0, frame.getSize().height - lblArrows.getSize().height);
		lblArrows.setVisible(false);
		
		frame.getContentPane().add(lblArrows);
				
		topPanel();	
				
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (drag && frame.getSize().height > 90 && MEDIAINFO.isRunning == false)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);		
			    	lblArrows.setLocation(0, frame.getSize().height - lblArrows.getSize().height);
			    	tabPanel.setBounds(0, topPanel.getSize().height, frame.getSize().width, frame.getSize().height - topPanel.getSize().height - 20);	
			    	infoTabbedPane.setBounds(tabPanel.getBounds());	
		       	}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height - 20 && infoTabbedPane.getTabCount() > 0 && MEDIAINFO.isRunning == false)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				 else 
				{
					if (drag == false && infoTabbedPane.getTabCount() > 0 && MEDIAINFO.isRunning == false)
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
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
					drag = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				drag = false;
				if (MEDIAINFO.isRunning == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
				
				Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	            shape1.add(shape2);
	    		frame.setShape(shape1);				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (frame.getSize().height <= 90)
				{
					frame.setSize(frame.getSize().width, 100);
		    		lblArrows.setLocation(0, frame.getSize().height - lblArrows.getSize().height);
		    		tabPanel.setBounds(0, topPanel.getSize().height, frame.getSize().width, frame.getSize().height - topPanel.getSize().height - 20);	
		    		infoTabbedPane.setBounds(tabPanel.getBounds());	
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});		
			
		KeyListener keyListener = new KeyListener(){

			@Override	
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {	
				
				if (infoTabbedPane.getTabCount() > 1)
				{
					if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
							infoTabbedPane.remove(infoTabbedPane.getSelectedIndex());		
				}	
				else
				{
					if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
					{
						infoTabbedPane.removeAll();
						Utils.changeFrameVisibility(frame, true);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		};

		infoTabbedPane.addKeyListener(keyListener);
		frame.addKeyListener(keyListener);
	
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
		
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we) {
		       
			   frame.toFront();
		    }
		});
        	
	}
	
	public static void addTabControl() {	
		
		tabPanel = new JPanel();			
		tabPanel.setBackground(new Color(35,255,35));
		tabPanel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tabPanel.setLayout(null);
		tabPanel.setBounds(0, topPanel.getSize().height, frame.getSize().width, frame.getSize().height - topPanel.getSize().height - 20);				
		
		infoTabbedPane.setBounds(tabPanel.getBounds());	
		infoTabbedPane.setForeground(Color.WHITE);
		
		frame.getContentPane().add(infoTabbedPane);			
		
	}
		
	private void topPanel() {	
		
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 20, 4, 15, 15);
			
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
		bottomImage.setBackground(new Color(35,35,40));
		bottomImage.setOpaque(true);
		bottomImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));
		bottomImage.setBounds(0 , 1, frame.getSize().width, 24);
			
		JLabel title = new JLabel(Shutter.language.getProperty("frameInformations"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));		
		topImage.setBounds(title.getBounds());
				
		topPanel.add(quit);	
		topPanel.add(reduce);	
		topPanel.add(topImage);
		topPanel.add(bottomImage);
		
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
					infoTabbedPane.removeAll();
					Utils.changeFrameVisibility(frame, true);	
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
		
		bottomImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
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
		 		
		bottomImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}
}