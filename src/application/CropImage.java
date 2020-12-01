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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import library.DCRAW;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.XPDF;

import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;

import javax.swing.JTextField;

public class CropImage {
	public static JDialog frame;
	private static JPanel overImage;
	private static JPanel image = new JPanel();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JLabel fullscreen;
	private static JPanel topPanel;
	private static JLabel title = new JLabel(Shutter.language.getProperty("frameCropImage"));
	ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
	private JLabel topImage;
	private JLabel bottomImage;
	private static JPanel selection;
	public static JButton btnOK;
	private static int startX = 0;
	private static int startY = 0;
	private static int ancrageDroit;
	private static int ancrageBas;
	public static JSlider positionVideo;
	
	/*
	 * Valeurs
	 */
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;
    private static boolean dragWindow;
    private static boolean drag = false;
    private static boolean shift = false;
    private static boolean ctrl = false;
    private static int frameX;
    private static int frameY;
    public static JTextField textPosX;
    public static JLabel posX;
    public static JTextField textPosY;
    public static JLabel posY;
    public static JTextField textWidth;
    public static JLabel width;
    public static JTextField textHeight;
    public static JLabel height;
    public static JLabel px1;
    public static JLabel px2;
    public static JLabel px3;
    public static JLabel px4;
    
    
	public CropImage() {
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameCropImage"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(665, 665);
		frame.setResizable(false);
		
		if (Functions.frame != null && Functions.frame.isVisible())
			frame.setModal(false);	
		else
			frame.setModal(true);
		
		frame.setAlwaysOnTop(true);
		
		
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
				if (e.getX() >= 625 && e.getY() >= 625 && dragWindow)
				{
			        frame.setSize(e.getX() + 20, e.getY() + 20);	
				}
				
				image();
				
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
					if (frame.getSize().width < 665 || frame.getSize().height < 665)
					{
						frame.setSize(665, 665);	
						
						resizeAll();	
					}
					
					loadImage();	
					
					int value = (int) Math.floor((float) (Integer.valueOf(textPosX.getText()) * image.getHeight()) / ImageHeight);	
					selection.setLocation(value, selection.getLocation().y);	

					value = (int) Math.floor((float) (Integer.valueOf(textPosY.getText()) * image.getWidth()) / ImageWidth);	
					selection.setLocation(selection.getLocation().x, value);
				
					value = (int) Math.floor((float)  (Integer.valueOf(textWidth.getText()) * image.getHeight()) / ImageHeight);
					selection.setSize(value, selection.getHeight());

					value = (int) Math.floor((float) (Integer.valueOf(textHeight.getText()) * image.getWidth()) / ImageWidth);
					selection.setSize(selection.getWidth(), value);
				}
				
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
		
		topPanel();

		loadImage();
						
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setBounds(14, frame.getHeight() - 31, (frame.getWidth() - 28), 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
												
				Shutter.cropFinal = "crop=" + textWidth.getText() + ":" + textHeight.getText()  + ":" + textPosX.getText()  + ":" + textPosY.getText() ;
				        
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            Utils.changeDialogVisibility(frame, true);
	            
	            //Suppression image temporaire
						    		
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();				
			}
			
		});

		valeurs();
				
		//Raccourcis clavier
		Toolkit.getDefaultToolkit().addAWTEventListener(
			    new AWTEventListener(){
			        public void eventDispatched(AWTEvent event){	
			        	
			            KeyEvent ke = (KeyEvent)event;
			              
						int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - frameX;
						int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - frameY;
						
			        	if (mouseInPictureX > 0 && mouseInPictureX < image.getWidth() && mouseInPictureY > 0 && mouseInPictureY < image.getHeight())
			        	{
			              if(ke.getID() == KeyEvent.KEY_PRESSED){
			            	  
			  				if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
			  					shift = true;
			  				
			  				if (ke.getKeyCode() == KeyEvent.VK_CONTROL)
			  					ctrl = true;
			  				
			  				if (ke.getKeyCode() == KeyEvent.VK_UP)
			  					selection.setLocation(selection.getLocation().x, selection.getLocation().y - 1);
			  				if (ke.getKeyCode() == KeyEvent.VK_DOWN)
			  					selection.setLocation(selection.getLocation().x, selection.getLocation().y + 1);
			  				if (ke.getKeyCode() == KeyEvent.VK_LEFT)
			  					selection.setLocation(selection.getLocation().x - 1, selection.getLocation().y);
			  				if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
			  					selection.setLocation(selection.getLocation().x + 1, selection.getLocation().y);
			  				
			  				if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
			  				{
								selection.setBounds(image.getWidth() / 4, image.getHeight() / 4, image.getWidth() / 2, image.getHeight() / 2);
								ancrageDroit = selection.getLocation().x + selection.getWidth();
								ancrageBas = selection.getLocation().y + selection.getHeight();	
								selection.repaint();
								image.repaint();
			              	}
			  				
			  				checkSelection();
			  				
			              }
			              
			              
			        	}
			        	
		  				
		              if(ke.getID() == KeyEvent.KEY_RELEASED){
		  				if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
		  					shift = false;
		  					ctrl = false;
		              }
			        }
	
			    }, AWTEvent.KEY_EVENT_MASK);
		
		checkSelection();
		
		Utils.changeDialogVisibility(frame, false);		
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
			
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getWidth(), 52);
			
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		
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
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);		            
		            Utils.changeDialogVisibility(frame, true);
		            Shutter.cropFinal = null;
		            
					//Suppression image temporaire
							    		
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();
									
					Shutter.caseRognerImage.setSelected(false);
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
		
		fullscreen = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/max2.png")));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 21,0,21, 21);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {			
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				int taskBarHeight = screenSize.height - winSize.height;
        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        		
				if (accept && frame.getHeight() < screenSize.height - taskBarHeight)
				{
					if (ImageHeight > ImageWidth)
					{
						frame.setBounds(0,0, screenSize.width, screenSize.height - taskBarHeight); 	
					}
					else
					{
						int setWidth = (int) ((float) (screenSize.height - topPanel.getHeight() - 35 - 22 - 17 - taskBarHeight) * ((float) ImageWidth / ImageHeight));
						if (setWidth <= screenSize.width)
							frame.setSize(setWidth, screenSize.height - taskBarHeight); 
						else
							frame.setSize(screenSize.width, screenSize.height - taskBarHeight);						
							
						frame.setLocation(dim.width/2-frame.getSize().width/2,0); 	
					}						
				}
				else if (accept)
				{
					frame.setSize(665, 665);
	        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);						
				}

				image();
				
				resizeAll();	
				
				loadImage();
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max2.png"))));
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
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
					int taskBarHeight = screenSize.height - winSize.height;
	        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        		
					if (frame.getHeight() < screenSize.height - taskBarHeight)
					{
						if (ImageHeight > ImageWidth)
						{
							frame.setBounds(0,0, screenSize.width, screenSize.height - taskBarHeight); 	
						}
						else
						{
							int setWidth = (int) ((float) (screenSize.height - topPanel.getHeight() - 35 - 22 - 17 - taskBarHeight) * ((float) ImageWidth / ImageHeight));
							if (setWidth <= screenSize.width)
								frame.setSize(setWidth, screenSize.height - taskBarHeight); 
							else
								frame.setSize(screenSize.width, screenSize.height - taskBarHeight);						
								
							frame.setLocation(dim.width/2-frame.getSize().width/2,0); 	
						}						
					}
					else
					{
						frame.setSize(665, 665);
		        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);						
					}

					image();
					
					resizeAll();	
					
					loadImage();
				}
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;	
				
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
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());

		topPanel.add(quit);	
		topPanel.add(fullscreen);
		topPanel.add(bottomImage);
		topPanel.add(topImage);
		topPanel.add(bottomImage);
		
		frame.getContentPane().add(topPanel);
	}

	protected static void checkSelection() {
		//Si la sélection est trop grande			
		if (selection.getLocation().x < 0)
			selection.setLocation(0, selection.getLocation().y);
		
		if (selection.getLocation().y < 0)
			selection.setLocation(selection.getLocation().x, 0);
		
		if (selection.getLocation().x + selection.getWidth() > image.getWidth())
		{
			if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				selection.setLocation(image.getWidth() - selection.getWidth(), selection.getLocation().y);
			else
				selection.setSize(image.getWidth() - selection.getLocation().x, selection.getHeight());
		}
		
		if (selection.getLocation().y + selection.getHeight() > image.getHeight())
		{
			if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				selection.setLocation(selection.getLocation().x, image.getHeight() - selection.getHeight());
			else
				selection.setSize(selection.getWidth(), image.getHeight() - selection.getLocation().y);
		}
		
		if (selection.getWidth() > image.getWidth())
			selection.setSize(image.getWidth(), selection.getHeight());	
		/*
		if (textWidth.getText().matches("[0-9]+") && textHeight.getText().matches("[0-9]+"))
		{
			if (Integer.valueOf(textWidth.getText()) == ImageWidth)
				selection.setSize(image.getWidth(), selection.getHeight());	
			if (Integer.valueOf(textHeight.getText()) == ImageHeight)
				selection.setSize(selection.getWidth(), image.getHeight());	
		}*/
		
		//Texte 
		int borderW = selection.getWidth();
		int borderH =  selection.getHeight();
		
		int outW;
		int outH;
		
		if (ImageHeight > ImageWidth)
		{
			outW = (int) Math.round((float) ImageWidth / ((float) finalWidth / borderW));				
			outH = (int) Math.round((float) ImageHeight / ((float) (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17) / borderH));
		}
		else
		{
			outW =  (int) Math.round((float) ImageWidth / ((float) (frame.getWidth() - 24) / borderW));				
			outH =  (int) Math.round((float) ImageHeight / ((float) finalHeight / borderH));
		}
		
		int Px =  (int) Math.round((float) ImageWidth / ((float) image.getSize().width / (selection.getLocation().x)));						
		int Py =  (int) Math.round((float) ImageHeight / ((float) image.getSize().height / selection.getLocation().y));
					
		if (textWidth.getText().matches("[0-9]+") && textHeight.getText().matches("[0-9]+"))
		{
			if (Px + Integer.valueOf(textWidth.getText()) > ImageWidth)
				Px = Px + (ImageWidth - (Px + Integer.valueOf(textWidth.getText())));
			
			if (Py + Integer.valueOf(textHeight.getText()) > ImageHeight)
				Py = Py + (ImageHeight - (Py + Integer.valueOf(textHeight.getText())));
			
			if (Integer.valueOf(textWidth.getText()) != ImageWidth)
				textPosX.setText(String.valueOf(Px));
			if (Integer.valueOf(textHeight.getText()) != ImageHeight)
				textPosY.setText(String.valueOf(Py));
		}
		else //Premiere ouverture
		{
			textPosX.setText(String.valueOf(Px));
			textPosY.setText(String.valueOf(Py));
		}
				
		if (frame.getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
		{
			textWidth.setText(String.valueOf(outW));
			textHeight.setText(String.valueOf(outH));
		}
	}
	
	public static void valeurs() {
		posX = new JLabel(Shutter.language.getProperty("posX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setBounds(14, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		textPosX = new JTextField();
		textPosX.setName("textPosX");
		textPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosX.setFont(new Font("FreeSans", Font.PLAIN, 12));
		
		px1 = new JLabel("px");
		px1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textPosX.getLocation().x + textPosX.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		textPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosX.getText().length() > 0)
				{
					int value = (int) Math.floor((float) (Integer.valueOf(textPosX.getText()) * image.getHeight()) / ImageHeight);	
					selection.setLocation(value, selection.getLocation().y);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textPosX.getText().length() >= 4)
					textPosX.setText("");				
			}			
			
		});
		
		posY = new JLabel(Shutter.language.getProperty("posY"));
		posY.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setBounds((int) (btnOK.getWidth() / 2) - 120, posX.getLocation().y, posY.getPreferredSize().width, 16);

		textPosY = new JTextField();
		textPosY.setName("textPosY");
		textPosY.setBounds(posY.getLocation().x + posY.getWidth() + 2, posY.getLocation().y, 34, 16);
		textPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosY.setFont(new Font("FreeSans", Font.PLAIN, 12));
		
		textPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosY.getText().length() > 0)
				{
					int value = (int) Math.floor((float) (Integer.valueOf(textPosY.getText()) * image.getWidth()) / ImageWidth);	
					selection.setLocation(selection.getLocation().x, value);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textPosY.getText().length() >= 4)
					textPosY.setText("");				
			}			
			
		});
		
		px2 = new JLabel("px");
		px2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textPosY.getLocation().x + textPosY.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		width = new JLabel(Shutter.language.getProperty("lblWidth"));
		width.setFont(new Font("FreeSans", Font.PLAIN, 12));
		width.setForeground(Utils.themeColor);
		width.setBounds((int) (btnOK.getWidth() / 2) + 45, posX.getLocation().y, width.getPreferredSize().width, 16);
		
		textWidth = new JTextField();
		textWidth.setName("textWidth");
		textWidth.setBounds(width.getLocation().x + width.getWidth() + 2, width.getLocation().y, 34, 16);
		textWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textWidth.setFont(new Font("FreeSans", Font.PLAIN, 12));
		
		textWidth.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textWidth.getText().length() > 0)
				{
					int value = (int) Math.floor((float)  (Integer.valueOf(textWidth.getText()) * image.getHeight()) / ImageHeight);
					selection.setSize(value, selection.getHeight());
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textWidth.getText().length() >= 4)
					textWidth.setText("");				
			}			
			
		});
		
		px3 = new JLabel("px");
		px3.setFont(new Font("FreeSans", Font.PLAIN, 12));
		px3.setForeground(Utils.themeColor);
		px3.setBounds(textWidth.getLocation().x + textWidth.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		height = new JLabel(Shutter.language.getProperty("lblHeight"));
		height.setHorizontalAlignment(SwingConstants.RIGHT);
		height.setFont(new Font("FreeSans", Font.PLAIN, 12));
		height.setForeground(Utils.themeColor);
		height.setBounds(btnOK.getLocation().x + btnOK.getWidth() - 104, posX.getLocation().y, height.getPreferredSize().width, 16);
		
		textHeight = new JTextField();
		textHeight.setName("textHeight");
		textHeight.setBounds(height.getLocation().x + height.getWidth() + 2, height.getLocation().y, 34, 16);
		textHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		textHeight.setFont(new Font("FreeSans", Font.PLAIN, 12));
		
		textHeight.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textHeight.getText().length() > 0)
				{
					int value = (int) Math.floor((float) (Integer.valueOf(textHeight.getText()) * image.getWidth()) / ImageWidth);
					selection.setSize(selection.getWidth(), value);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textHeight.getText().length() >= 4)
					textHeight.setText("");				
			}			
			
		});
		
		px4 = new JLabel("px");
		px4.setFont(new Font("FreeSans", Font.PLAIN, 12));
		px4.setForeground(Utils.themeColor);
		px4.setBounds(textHeight.getLocation().x + textHeight.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		frame.getContentPane().add(posX);	
		frame.getContentPane().add(textPosX);
		frame.getContentPane().add(px1);
		frame.getContentPane().add(posY);	
		frame.getContentPane().add(textPosY);
		frame.getContentPane().add(px2);
		frame.getContentPane().add(width);	
		frame.getContentPane().add(textWidth);
		frame.getContentPane().add(px3);
		frame.getContentPane().add(height);
		frame.getContentPane().add(textHeight);	
		frame.getContentPane().add(px4);
		
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
    		FFPROBE.Data(Shutter.listeDeFichiers.getSelectedValue().toString());
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);

		if (FFPROBE.totalLength > 100) //Plus d'une image
			positionVideo.setVisible(true);
		else
			positionVideo.setVisible(false);
		
		positionVideo.setMaximum(FFPROBE.totalLength);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font("FreeSans", Font.PLAIN, 11));
		positionVideo.setBounds(14, posX.getY() - 22, (frame.getWidth() - 28), 22);	
		frame.getContentPane().add(positionVideo); 
		
		positionVideo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {								
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();	
						
					loadImage();
						
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
			}
			
		});
	}
	
	@SuppressWarnings("serial")
	private static void image()
	{		
		image.removeAll(); 
		
		final int containerWidth;
		final int containerHeight;
		if (ImageHeight > ImageWidth)
		{
			containerHeight = (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17);
			containerWidth =  (int) Math.floor((float) (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17) * ImageWidth / ImageHeight);
		}
		else
		{
			containerHeight =  (int) Math.floor((float) (frame.getWidth() - 24) * ImageHeight / ImageWidth);
			containerWidth = (frame.getWidth() - 24);	
		}
		
		image.setSize(containerWidth, containerHeight);
		
		//frame.setSize(frame.getWidth(), image.getHeight() + 120);
		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		
		if (ImageHeight > ImageWidth)
			image.setLocation((int) Math.floor((float) (frame.getWidth() - containerWidth) / 2) + 10, 62);
		else
			image.setLocation(12,  (int) Math.floor((float) (frame.getHeight() - containerHeight) / 2));
		
		frameX = image.getLocation().x;
		frameY = image.getLocation().y;
		
		image.setLayout(null);        
		image.setOpaque(false);

		selection = new JPanel() {
			public void paintComponent(Graphics g) {
				g.setColor(Utils.themeColor);
				g.fillRect(0, 0, 6, 6); //NW
				g.fillRect(selection.getWidth() / 2 - 3,0, 6, 6); //N
				g.fillRect(selection.getWidth() - 6, 0, 6, 6); //NE
				g.fillRect(selection.getWidth() - 6,selection.getHeight() / 2 - 3, 6, 6); //E
				g.fillRect(selection.getWidth() - 6,selection.getHeight() - 6, 6, 6); //SE
				g.fillRect(selection.getWidth() / 2 - 3,selection.getHeight() -6, 6, 6); //S
				g.fillRect(0,selection.getHeight() - 6, 6, 6); //SW
				g.fillRect(0,selection.getHeight() / 2 -3, 6, 6); //W					
			}
		};
		selection.setBorder(BorderFactory.createDashedBorder(Color.WHITE, 4, 4));
		selection.setBounds(image.getWidth() / 4, image.getHeight() / 4, image.getWidth() / 2, image.getHeight() / 2);
		image.add(selection);
		
		ancrageDroit = selection.getLocation().x + selection.getWidth();
		ancrageBas = selection.getLocation().y + selection.getHeight();
				 							
		selection.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {		
				
				drag = true;
				
				//Position de la souris depuis le point d'appui start
				int mouseX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - startX - frameX;				
				int mouseY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - startY - frameY;
				
				int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - frameX;
				int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - frameY;
						
				//Permet d'éviter d'avoir une position négative de la sélection
				if (mouseInPictureX > -10 && mouseInPictureX < image.getWidth() + 10 && mouseInPictureY > -10 && mouseInPictureY < image.getHeight() + 10)
				{
					if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
					{
						selection.setLocation(mouseX, mouseY);						
						
						if (shift)
							selection.setSize(2 * ancrageDroit - 2 * mouseX - selection.getWidth(), 2 * ancrageBas - 2 * mouseY - selection.getHeight());
						else
							selection.setSize(ancrageDroit - mouseX, ancrageBas - mouseY);
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
					{
						selection.setLocation(selection.getLocation().x, mouseY);
						
						if (shift)
							selection.setSize(selection.getSize().width, 2 * ancrageBas - 2 * mouseY - selection.getHeight());
						else
							selection.setSize(selection.getSize().width, ancrageBas - mouseY);
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
					{			
						if (shift)
						{						
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), mouseY);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), 2 * ancrageBas - 2 * mouseY - selection.getHeight());
						}
						else
						{
							selection.setLocation(selection.getLocation().x, mouseY);
							selection.setSize(e.getX(), ancrageBas - mouseY);
						}
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
					{								
						if (shift)
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), selection.getSize().height);
						}
						else
							selection.setSize(e.getX(), selection.getSize().height);
																
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
					{
						if (shift)						
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(e.getX(), e.getY());
						
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
					{				
						
						if (shift)
						{						
							selection.setLocation(selection.getLocation().x, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(selection.getSize().width, e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(selection.getSize().width, e.getY());
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
					{
						
						if (shift)
						{						
							selection.setLocation(mouseX, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(2 * ancrageDroit - 2 * mouseX - selection.getWidth(), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
						{
							selection.setLocation(mouseX, selection.getLocation().y);
							selection.setSize(ancrageDroit - mouseX, e.getY());	
						}
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))
					{
						selection.setLocation(mouseX, selection.getLocation().y);	
											
						if (shift)
							selection.setSize(2 * ancrageDroit - 2 * mouseX - selection.getWidth(), selection.getSize().height);
						else
							selection.setSize(ancrageDroit - mouseX, selection.getSize().height);
					}
					else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					{
						if (shift && ctrl)
							selection.setLocation(mouseX, selection.getLocation().y);
						else if (shift)
							selection.setLocation(selection.getLocation().x, mouseY);
						else if (ctrl)
							selection.setLocation(mouseX, selection.getLocation().y);
						else					
							selection.setLocation(mouseX, mouseY);
					}
				}
									
					//Point d'ancrage
					ancrageDroit = selection.getLocation().x + selection.getWidth();
					ancrageBas = selection.getLocation().y + selection.getHeight();
					
					checkSelection();									
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if (drag == false)
				{
					if (e.getX() <= 10 && e.getY() <= 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					else if (e.getX() <= selection.getWidth() / 2 + 5 && e.getX() >= selection.getWidth() / 2 - 5
							&& e.getY() <= 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10 && e.getY() <= 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10
							&& e.getY() <= selection.getHeight() / 2 + 5 && e.getY() >= selection.getHeight() / 2 - 5)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10
							&& e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					else if (e.getX() <= selection.getWidth() / 2 + 5 && e.getX() >= selection.getWidth() / 2 - 5
							&& e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					else if (e.getX() <= 10 && e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					else if (e.getX() <= 6 && e.getY() <= selection.getHeight() / 2 + 5 && e.getY() >= selection.getHeight() / 2 - 5)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					else		
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}
			
		});
		
		selection.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {;
				startX = e.getPoint().x;
				startY = e.getPoint().y;
 			}
			
			@Override public void mouseReleased(MouseEvent e) {
				
				drag = false;
				
				if (selection.getLocation().x <= 0 && selection.getLocation().y <= 0) 
					selection.setLocation(0, 0);
				else if (selection.getLocation().x + selection.getWidth() > image.getWidth() && selection.getLocation().y <= 0)
					selection.setLocation(image.getWidth() - selection.getWidth(), 0);
				else if (selection.getLocation().x <= 0 && selection.getLocation().y + selection.getHeight() > image.getHeight())
					selection.setLocation(0, image.getHeight() - selection.getHeight());
				else if (selection.getLocation().x + selection.getWidth() > image.getWidth() && selection.getLocation().y + selection.getHeight() > image.getHeight())
					selection.setLocation(image.getWidth() - selection.getWidth(), image.getHeight() - selection.getHeight());
				else if (selection.getLocation().x + selection.getWidth() > image.getWidth())
					selection.setLocation(image.getWidth() - selection.getWidth(), selection.getLocation().y);
				else if (selection.getLocation().y + selection.getHeight() > image.getHeight())
					selection.setLocation(selection.getLocation().x, image.getHeight() - selection.getHeight());
				else if (selection.getLocation().x <= 0)
					selection.setLocation(0, selection.getLocation().y);
				else if (selection.getLocation().y <= 0)
					selection.setLocation(selection.getLocation().x, 0);
			}
			
		});		
		
		image.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {	
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					selection.setBounds(image.getWidth() / 4, image.getHeight() / 4, image.getWidth() / 2, image.getHeight() / 2);
					ancrageDroit = selection.getLocation().x + selection.getWidth();
					ancrageBas = selection.getLocation().y + selection.getHeight();	
					selection.repaint();
					image.repaint();
					checkSelection();
				}
 			}
		});
		
		image.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent down) {	
				if (drag == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
 			}
		});

		if (overImage == null)
		{
			overImage = new JPanel() {
				public void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setColor(new Color(255,255,255,0));
					g2d.fillRect(0, 0, containerWidth, containerHeight);	
					
					Area outter = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
	                Rectangle inner = new Rectangle(selection.getLocation().x, selection.getLocation().y, selection.getWidth(), selection.getHeight());
	                outter.subtract(new Area(inner));
	                
	                g2d.setColor(new Color(0,0,0,180));
	                g2d.fill(outter);
					
				}
			};
		}
		overImage.setBounds(image.getBounds());
		overImage.setLayout(null);    
			
		frame.getContentPane().add(overImage);
		frame.getContentPane().add(image);
		
	}
	
	public static void loadImage() {
        try
        {		
        	
        	File file = new File (Shutter.liste.firstElement());
        
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
						
			//Erreur FFPROBE avec les fichiers RAW
			boolean isRaw = false;
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
				FFPROBE.Data(file.toString());
	        	do
	            	Thread.sleep(100);   
	        	while (FFPROBE.isRunning);
			}			

        	
        		finalWidth = (int) Math.floor(((frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17) * ImageWidth) / ImageHeight);
        			        	
        		finalHeight = (int) Math.floor(((frame.getWidth() - 24) * ImageHeight) / ImageWidth);
        	                   	        		
	    		
				File fileOut = new File(Shutter.dirTemp + "preview.bmp");
				if (fileOut.exists()) fileOut.delete();
				
				Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder")+ " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
				
				//InOut		
				FFMPEG.fonctionInOut();
				
				//Slider
				if (positionVideo != null && positionVideo.getValue() > 0 && positionVideo.getValue() < positionVideo.getMaximum() && FFPROBE.totalLength > 100)
				{
					DecimalFormat tc = new DecimalFormat("00");			
					String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
					String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
					String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
					
					FFMPEG.inPoint = " -ss " + h + ":" + m + ":" + s + ".0";
				}
				
				//Envoi de la commande
				String cmd;
				if (ImageHeight > ImageWidth)
					cmd = " -vframes 1 -an -s " + finalWidth + "x" + (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17) + " -y ";
				else
					cmd = " -vframes 1 -an -s " + (frame.getWidth() - 24) + "x" + finalHeight + " -y ";
					
				if (extension.toLowerCase().equals(".pdf"))
					XPDF.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + '"' + fileOut + '"');
				else if (isRaw)
					DCRAW.run(" -v -w -c -q 0 -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + '"' + fileOut + '"');
				else
          			FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + cmd + '"' + fileOut + '"');
					         	            
	            do
	            {
	            	Thread.sleep(100);  
	            } while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false);

	           	if (FFMPEG.error)
	      	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);	           	
	           		       
	           	image.removeAll();  
	           	
	           	//On charge l'image après la création du fichier pour avoir le bon ratio
	    		image();	         
	    		
	    		Image imageBMP = ImageIO.read(new File(Shutter.dirTemp + "preview.bmp"));
	            ImageIcon imageIcon = new ImageIcon(imageBMP);
	    		JLabel newImage = new JLabel(imageIcon);
	            imageIcon.getImage().flush();
	    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
	            if (ImageHeight > ImageWidth)
	            	newImage.setBounds(0, 0,  (int) Math.floor((float) (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17) * ImageWidth / ImageHeight), (frame.getHeight() - topPanel.getHeight() - 35 - 22 - 17));  
	            else
	            	newImage.setBounds(0, 0, (frame.getWidth() - 24),  (int) Math.floor((float) (frame.getWidth() - 24) * ImageHeight / ImageWidth)); 
	            
	    		//Contourne un bug
	            imageIcon = new ImageIcon(imageBMP);
	    		newImage = new JLabel(imageIcon);
	    		newImage.setSize(image.getSize());
	    		
	    		image.add(newImage);
	    		image.repaint();
	    		selection.repaint();
	    		frame.getContentPane().repaint();
	    		
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);	   
	            
	            if (btnOK != null) 
	            	checkSelection();
        }
	    catch (Exception e)
	    {
 	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	    }
        finally {
        	Shutter.enableAll();     
        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
			else
				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
        }
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
				
				
				File file = new File(Shutter.dirTemp + "preview.bmp");
				
				do {
					Thread.sleep(100);
				} while (file.exists() == false);				
				
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
								if (p instanceof JTextField)
								{											
									do {
										Thread.sleep(100);
									} while (file.exists() == false);
									
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								
									//Position des éléments
									if (p.getName().equals("textPosX") && textPosX.getText().length() > 0)
									{
										int value = (int) Math.floor((float) (Integer.valueOf(textPosX.getText()) * image.getHeight()) / ImageHeight);	
										selection.setLocation(value, selection.getLocation().y);	
									}
									
									if (p.getName().equals("textPosY") && textPosY.getText().length() > 0)
									{
										int value = (int) Math.floor((float) (Integer.valueOf(textPosY.getText()) * image.getWidth()) / ImageWidth);	
										selection.setLocation(selection.getLocation().x, value);
									}
									
									if (p.getName().equals("textWidth") && textWidth.getText().length() > 0)
									{
										int value = (int) Math.floor((float)  (Integer.valueOf(textWidth.getText()) * image.getHeight()) / ImageHeight);
										selection.setSize(value, selection.getHeight());
									}
									
									if (p.getName().equals("textHeight") && textHeight.getText().length() > 0)
									{
										int value = (int) Math.floor((float) (Integer.valueOf(textHeight.getText()) * image.getWidth()) / ImageWidth);
										selection.setSize(selection.getWidth(), value);
									}
								}
							}
						}
					}
				}				
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}
		
	private void resizeAll() {		
		topPanel.setBounds(0,0,frame.getSize().width, 52);
		
		topImage.setLocation(frame.getSize().width / 2 - topImage.getSize().width / 2, 0);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);	
		fullscreen.setBounds(quit.getLocation().x - 21,0,21, 21);
		
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);					
		bottomImage.setBounds(0 ,0, frame.getSize().width, 52);
		
		title.setBounds(0, 0, frame.getWidth(), 52);
				
		btnOK.setBounds(14, frame.getHeight() - 31, (frame.getWidth() - 28), 21);	
		
	   	posX.setLocation(14, btnOK.getLocation().y - 22);
		textPosX.setLocation(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y);
		px1.setLocation(textPosX.getLocation().x + textPosX.getWidth() + 2, btnOK.getLocation().y - 22);
		
	    posY.setLocation((int) (btnOK.getWidth() / 4) + posY.getWidth(), posX.getLocation().y);
	    textPosY.setLocation(posY.getLocation().x + posY.getWidth() + 2, posY.getLocation().y);
	    px2.setLocation(textPosY.getLocation().x + textPosY.getWidth() + 2, btnOK.getLocation().y - 22);
	    
	    width.setLocation((int) (btnOK.getWidth() / 4) * 2 + width.getWidth(), posX.getLocation().y);
	    textWidth.setLocation(width.getLocation().x + width.getWidth() + 2, width.getLocation().y);
	    px3.setLocation(textWidth.getLocation().x + textWidth.getWidth() + 2, btnOK.getLocation().y - 22);
	    
	    height.setLocation(btnOK.getLocation().x + btnOK.getWidth() - 104, posX.getLocation().y);
	    textHeight.setLocation(height.getLocation().x + height.getWidth() + 2, height.getLocation().y);		    					   
	    px4.setLocation(textHeight.getLocation().x + textHeight.getWidth() + 2, btnOK.getLocation().y - 22);
	    
		positionVideo.setBounds(14, posX.getY() - 22, (frame.getWidth() - 28), 22);
	}
}