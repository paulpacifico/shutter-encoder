/*******************************************************************************************
* Copyright (C) 2020 PACIFICO PAUL
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
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
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JFrame;

import java.awt.Image;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import library.MEDIAINFO;

	public class Informations {
	public static JFrame frame;
	public static JDialog shadow = new JDialog();
	
	/*
	 * Composants
	 */
	private static JPanel panelHaut;
	private boolean drag;
	private JLabel quit;
	private JLabel reduce;
	private JLabel topImage;
	private JLabel bottomImage;
	public static JPanel tabPanel;
	public static JLabel lblWait;
	public static JLabel lblFlecheBas;
	public static JTabbedPane infoTabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public Informations() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameInformations"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 600);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		frame.setLocation(Shutter.frame.getLocation().x - frame.getSize().width -20, Shutter.frame.getLocation().y);	
		frame.getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
				
		lblWait = new JLabel(Shutter.language.getProperty("lblWait"));
		lblWait.setFont(new Font("Arial", Font.PLAIN, 20));
		lblWait.setSize(lblWait.getPreferredSize().width, 40);
		lblWait.setLocation(frame.getSize().width / 2 - lblWait.getSize().width / 2, frame.getSize().height / 2);
		lblWait.setVisible(true);
		frame.getContentPane().add(lblWait);
		
		lblFlecheBas = new JLabel("▲▼");
		lblFlecheBas.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlecheBas.setFont(new Font("Arial", Font.PLAIN, 20));
		lblFlecheBas.setSize(new Dimension(frame.getSize().width, 20));
		lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
		lblFlecheBas.setVisible(false);
		
		frame.getContentPane().add(lblFlecheBas);
				
		panelHaut();
		
		drag = false;		
				
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag && frame.getSize().height > 90 && MEDIAINFO.isRunning == false)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);		
			    	lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
			    	tabPanel.setBounds(0, panelHaut.getSize().height, frame.getSize().width, frame.getSize().height - panelHaut.getSize().height - 20);	
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
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (frame.getSize().height <= 90)
				{
					frame.setSize(frame.getSize().width, 100);
		    		lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
		    		tabPanel.setBounds(0, panelHaut.getSize().height, frame.getSize().width, frame.getSize().height - panelHaut.getSize().height - 20);	
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
						Utils.changeFrameVisibility(frame, shadow, true);
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
		    	frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
		    }
 		});
		
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we) {
		       shadow.setVisible(true);
			   frame.toFront();
		    }
		});
    	
    	setShadow();
	}
	
	public static void addTabControl() {		
		tabPanel = new JPanel();			
		tabPanel.setBackground(new Color(50,50,50));
		tabPanel.setLayout(null);
		tabPanel.setBounds(0, panelHaut.getSize().height, frame.getSize().width, frame.getSize().height - panelHaut.getSize().height - 20);				
		
		infoTabbedPane.setBounds(tabPanel.getBounds());
				
		frame.getContentPane().add(infoTabbedPane);			
		
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private void panelHaut() {	
		panelHaut	= new JPanel();
		panelHaut.setLayout(null);
		panelHaut.setBounds(0, 0, frame.getSize().width, 51);
		
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setBounds(frame.getSize().width - 35,0,35, 15);
		
		reduce = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/reduce2.png")));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 21,0,21, 15);
			
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce3.png"))));
				accept = true;
			}

			@SuppressWarnings("static-access")
			@Override
			public void mouseReleased(MouseEvent e) {		
				
				if (accept)
				{							
					shadow.setVisible(false);
					frame.setState(frame.ICONIFIED);	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce2.png"))));
				accept = false;
			}
			
			
		});
				
		ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("contents/FondNeutre.png"));
		Image scaledImage = image.getImage().getScaledInstance(panelHaut.getSize().width, panelHaut.getSize().height, Image.SCALE_SMOOTH);
		ImageIcon fondNeutre = new ImageIcon(scaledImage);
		bottomImage = new JLabel(fondNeutre);
		bottomImage.setBounds(0 ,0, frame.getSize().width, 51);
			
		JLabel title = new JLabel(Shutter.language.getProperty("frameInformations"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		panelHaut.add(title);
		
		topImage = new JLabel();
		ImageIcon imageIcon = new ImageIcon(fondNeutre.getImage().getScaledInstance(panelHaut.getSize().width, panelHaut.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
				
		panelHaut.add(quit);	
		panelHaut.add(reduce);	
		panelHaut.add(topImage);
		panelHaut.add(bottomImage);
		
		quit.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				if (accept)		
				{		  
					infoTabbedPane.removeAll();
					Utils.changeFrameVisibility(frame, shadow, true);	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit2.png"))));
				accept = false;
			}

						
		});
		panelHaut.setBounds(0, 0, frame.getSize().width, 51);
		frame.getContentPane().add(panelHaut);						
		
		bottomImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;	
				shadow.toFront();
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX, MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	private void setShadow() {
		shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
    	shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
    	shadow.setUndecorated(true);
    	shadow.setContentPane(new InformationsShadow());
    	shadow.setBackground(new Color(255,255,255,0));
    	
    	shadow.setFocusableWindowState(false);
		
		shadow.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent down) {
				frame.toFront();
			}
    		
    	});
   		
    	frame.addComponentListener(new ComponentAdapter() {
		    public void componentMoved(ComponentEvent e) {
		        shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
		    }
		    public void componentResized(ComponentEvent e2)
		    {
		    	shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
		    }
 		});
	}
}

//Ombre
@SuppressWarnings("serial")
class InformationsShadow extends JPanel {
    public void paintComponent(Graphics g){
    	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  	  Graphics2D g1 = (Graphics2D)g.create();
	  	  g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
	  	  g1.setRenderingHints(qualityHints);
	  	  g1.setColor(new Color(0,0,0));
	  	  g1.fillRect(0,0,Informations.frame.getWidth() + 14, Informations.frame.getHeight() + 7);
	  	  
	 	  for (int i = 0 ; i < 7; i++) 
	 	  {
	 		  Graphics2D g2 = (Graphics2D)g.create();
	 		  g2.setRenderingHints(qualityHints);
	 		  g2.setColor(new Color(0,0,0, i * 10));
	 		  g2.drawRoundRect(i, i, Informations.frame.getWidth() + 13 - i * 2, Informations.frame.getHeight() + 7, 20, 20);
	 	  }
     }
 }