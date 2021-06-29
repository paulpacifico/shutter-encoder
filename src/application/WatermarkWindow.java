/*******************************************************************************************

* Copyright (C) 2021 PACIFICO PAUL
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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import library.FFMPEG;
import library.FFPROBE;

import javax.swing.JRadioButton;

public class WatermarkWindow {
	public static JDialog frame;
	private static JPanel image = new JPanel();
	private static JRadioButton caseSafeArea;
	public static String logoFile = new String();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	private JButton btnOK;
	
	/*
	 * Valeurs
	 */
	
	public static int containerWidth =  854;	
	public static int containerHeight = 480;
	public static float logoRatio = 3;
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;
    private int logoPosX = 0;
    private int logoPosY = 0;
    private static int logoLocX = 0;
    private static int logoLocY = 0;
	public static JSlider positionVideo;
	public static JTextField textPosX;
	public static JTextField textPosY;
    public static JTextField textSize;
    public static JTextField textOpacity;
    
    //Logo
	@SuppressWarnings("serial")
	public static JPanel logo = new JPanel(){
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			if (textOpacity.getText().length() > 0 && Integer.parseInt(textOpacity.getText()) <= 100)
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) Integer.valueOf(textOpacity.getText()) / 100));	        
		}
	};

	public WatermarkWindow() {		
				
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameLogo"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(878, 578);
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
					
		topPanel();
				 
		image.setLayout(null);        
		image.setOpaque(false);  
		frame.getContentPane().add(image);
		
		image.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) Math.floor(image.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(image.getHeight() / 2 - logo.getHeight() / 2));	
					logoLocX =  logo.getLocation().x;
					logoLocY = logo.getLocation().y;
					textPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * logoRatio) ) ) );
					textPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * logoRatio) ) ) );  
				}
			}
			
		});
		
		logo.setLayout(null);        
		logo.setOpaque(false); 
		logo.setBackground(new Color(0,0,0,50));
		logo.setSize(854, 480);	
		image.add(logo);
		
		logo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) Math.floor(image.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(image.getHeight() / 2 - logo.getHeight() / 2));	
					textPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * logoRatio) ) ) );
					textPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * logoRatio) ) ) );  
				}
				else
		     	{
					logoPosX = e.getLocationOnScreen().x;
					logoPosY = e.getLocationOnScreen().y;
		     	}

			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				logoLocX =  logo.getLocation().x;
				logoLocY = logo.getLocation().y;

			}
			
			
		});
		
		logo.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				logo.setLocation(MouseInfo.getPointerInfo().getLocation().x - logoPosX + logoLocX, MouseInfo.getPointerInfo().getLocation().y - logoPosY + logoLocY);		
				textPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * logoRatio) ) ) );
				textPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * logoRatio) ) ) );  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
		
		loadImage("0","0","0",true, -1);
		
		//On charge la position du logo après sa mise en place dans l'image
		logo.setLocation((int) Math.floor(image.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(image.getHeight() / 2 - logo.getHeight() / 2));
       	logoLocX = logo.getLocation().x;
       	logoLocY = logo.getLocation().y;
		
		values();
		
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOK.setBounds(caseSafeArea.getX() + caseSafeArea.getWidth() + 6, frame.getHeight() - 32, frame.getWidth() - (caseSafeArea.getX() + caseSafeArea.getWidth()) - 12 - 6, 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
												
				Shutter.cropFinal = "crop=" + textSize.getText() + ":" + textOpacity.getText()  + ":" + textPosX.getText()  + ":" + textPosY.getText() ;
				        
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            Utils.changeDialogVisibility(frame, true);
			}
			
		});
		
		//Raccourcis clavier
		Toolkit.getDefaultToolkit().addAWTEventListener(
			    new AWTEventListener(){
			        public void eventDispatched(AWTEvent event){	
			        	
			            KeyEvent ke = (KeyEvent)event;
			  				
			  				switch (ke.getKeyCode())
			  				{
			  					case KeyEvent.VK_UP:
			  						textPosY.setText(String.valueOf(Integer.parseInt(textPosY.getText()) - 1)); 
			  						logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.parseInt(textPosY.getText()) / logoRatio));
								break;
			  					
			  					case KeyEvent.VK_DOWN:
									textPosY.setText(String.valueOf(Integer.parseInt(textPosY.getText()) + 1)); 
				  					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.parseInt(textPosY.getText()) / logoRatio));
								break;
			  					
								case KeyEvent.VK_LEFT:
									textPosX.setText(String.valueOf(Integer.parseInt(textPosX.getText()) - 1)); 
				  					logo.setLocation((int) Math.floor(Integer.parseInt(textPosX.getText()) / logoRatio), logo.getLocation().y);
								break;
			  					
			  					case KeyEvent.VK_RIGHT:
									textPosX.setText(String.valueOf(Integer.parseInt(textPosX.getText()) + 1)); 
				  					logo.setLocation((int) Math.floor(Integer.parseInt(textPosX.getText()) / logoRatio), logo.getLocation().y);
								break;
			  					
			  					case KeyEvent.VK_ESCAPE:
			  						logo.setLocation((int) Math.floor(image.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(image.getHeight() / 2 - logo.getHeight() / 2));	
			  						textPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * logoRatio) ) ) );
			  						textPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * logoRatio) ) ) ); 
								break;
			  				}			  													              
			        	}	    

			    }, AWTEvent.KEY_EVENT_MASK);
	
		Utils.changeDialogVisibility(frame, false);		
		frame.repaint();
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private static class MouseLogoPosition {
		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
	}
		
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
			
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 1000, 52);
		
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
					Shutter.caseLogo.setSelected(false);
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("frameLogo"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);
		topPanel.setBounds(0, 0, 1000, 52);
		frame.getContentPane().add(topPanel);
		
		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;					
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
		
		topImage.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX, MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}
	
	private void values() {
		
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
				FFPROBE.Data(Shutter.liste.firstElement());
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
		
		positionVideo.setMaximum(FFPROBE.totalLength);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		positionVideo.setBounds(12, frame.getHeight() - 32, 122, 22);	
		
		//Contournement d'un bug
		Component[] components = frame.getContentPane().getComponents();		    
		boolean addToFrame = true;
	    for(int i = 0; i < components.length; i++) {
	    	if (components[i] instanceof JSlider)
	    		addToFrame = false;
	    }	    
	    if (addToFrame)
	    	frame.getContentPane().add(positionVideo);
		
		positionVideo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {			
					//On attends que le slider soit relaché pour faire le changement
					Thread runProcess = new Thread(new Runnable()  {
						@Override
						public void run() {
							DecimalFormat tc = new DecimalFormat("00");			
							String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
							String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
							String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
							
							//Logo    		
							FFPROBE.Data(logoFile);	    		
							do {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while (FFPROBE.isRunning);
							
							//Si le fichier est une vidéo on recharge le logo
							if (FFPROBE.totalLength > 40)
								loadImage(h,m,s, true, Integer.parseInt(textSize.getText()));
							else
								loadImage(h,m,s, false, -1);
						}
					});
					runProcess.start();					
			}
			
		});
		
		JLabel posX = new JLabel(Shutter.language.getProperty("posX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setBounds(positionVideo.getLocation().x + positionVideo.getWidth() + 12, positionVideo.getLocation().y + 3, posX.getPreferredSize().width, 16);
				
		textPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * logoRatio) ) ) );
		textPosX.setName("textPosX");
		textPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		JLabel px1 = new JLabel("px");
		px1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textPosX.getLocation().x + textPosX.getWidth() + 2, posX.getY(), posX.getPreferredSize().width, 16);
		
		textPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosX.getText().length() > 0)
					logo.setLocation((int) Math.floor(Integer.valueOf(textPosX.getText()) / logoRatio), logo.getLocation().y);
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
		
		textPosX.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textPosX.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textPosX.setText(String.valueOf(MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX)));
					logo.setLocation((int) Math.floor(Integer.valueOf(textPosX.getText()) / logoRatio), logo.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
				
		JLabel posY = new JLabel(Shutter.language.getProperty("posY"));
		posY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setBounds(px1.getLocation().x + 22, posX.getY(), posY.getPreferredSize().width, 16);

		textPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * logoRatio) ) ) );
		textPosY.setName("textPosY");
		textPosY.setBounds(posY.getLocation().x + posY.getWidth() + 2, posX.getY(), 34, 16);
		textPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosY.getText().length() > 0)
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textPosY.getText()) / logoRatio));
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
	
		textPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosY.getText().length() > 0)
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textPosY.getText()) / logoRatio));
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
		
		textPosY.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseY = e.getY();
				MouseLogoPosition.offsetY = Integer.parseInt(textPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textPosY.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textPosY.setText(String.valueOf(MouseLogoPosition.offsetY + (e.getY() - MouseLogoPosition.mouseY)));
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textPosY.getText()) / logoRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel px2 = new JLabel("px");
		px2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textPosY.getLocation().x + textPosY.getWidth() + 2, posX.getY(), posX.getPreferredSize().width, 16);
		
		JLabel size = new JLabel(Shutter.language.getProperty("lblSize"));
		size.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		size.setForeground(Utils.themeColor);
		size.setBounds(px2.getLocation().x + 22, posX.getY(), size.getPreferredSize().width + 4, 16);
		
		textSize = new JTextField("100");
		textSize.setName("textSize");
		textSize.setBounds(size.getLocation().x + size.getWidth() - 2, posX.getY(), 34, 16);
		textSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textSize.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{	
					loadLogo(Integer.parseInt(textSize.getText()));					
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textSize.getText().length() >= 3)
					textSize.setText("");				
			}			
			
		});
		
		textSize.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textSize.getText().length() > 0)
				{	
					textSize.setText(String.valueOf(MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX)));
					loadLogo(Integer.parseInt(textSize.getText()));					
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel px3 = new JLabel("%");
		px3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px3.setForeground(Utils.themeColor);
		px3.setBounds(textSize.getLocation().x + textSize.getWidth() + 2, posX.getY(), posX.getPreferredSize().width, 16);
		
		JLabel opacity = new JLabel(Shutter.language.getProperty("lblOpacity"));
		opacity.setHorizontalAlignment(SwingConstants.RIGHT);
		opacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		opacity.setForeground(Utils.themeColor);
		opacity.setBounds(px3.getLocation().x + 22, posX.getY(), opacity.getPreferredSize().width, 16);
		
		textOpacity = new JTextField("100");
		textOpacity.setName("textOpacity");
		textOpacity.setBounds(opacity.getLocation().x + opacity.getWidth() + 2, posX.getY(), 34, 16);
		textOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textOpacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textOpacity.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textOpacity.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				logo.repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textOpacity.getText().length() > 0)
				{
					if (Integer.valueOf(textOpacity.getText()) > 100)
					{
						textOpacity.setText("100");
					}	
					else if (Integer.valueOf(textOpacity.getText()) < 1)
					{
						textOpacity.setText("1");
					}		
					
					logo.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textOpacity.getText().length() >= 3)
					textOpacity.setText("");					
			}			
			
		});
		
		textOpacity.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textOpacity.getText().length() > 0)
				{
					int value = MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX);
					
					if (value > 100)
					{
						textOpacity.setText("100");						
					}
					else if (value < 1)
					{
						textOpacity.setText("1");
					}	
					else
						textOpacity.setText(String.valueOf(value));
					
					logo.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel px4 = new JLabel("%");
		px4.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px4.setForeground(Utils.themeColor);
		px4.setBounds(textOpacity.getLocation().x + textOpacity.getWidth() + 2, posX.getY(), posX.getPreferredSize().width, 16);
		
		
       	caseSafeArea = new JRadioButton(Shutter.language.getProperty("caseSafeArea"));
		caseSafeArea.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseSafeArea.setSize(caseSafeArea.getPreferredSize().width, 23);
		caseSafeArea.setLocation(px4.getLocation().x + 18, frame.getHeight() - 32);
		frame.getContentPane().add(caseSafeArea);
		
		@SuppressWarnings("serial")
		JPanel safeArea = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);					
				
				g.drawRect(image.getX() + (int) ((float) ((float) image.getWidth() * 0.1) / 2), image.getY() +  (int) ((float) ((float) image.getHeight() * 0.1) / 2), (int) ((float) image.getWidth() * 0.9), (int) ((float) image.getHeight() * 0.9));
				g.drawRect(image.getX() + (int) ((float) ((float) image.getWidth() * 0.2) / 2), image.getY() +  (int) ((float) ((float) image.getHeight() * 0.2) / 2), (int) ((float) image.getWidth() * 0.8), (int) ((float) image.getHeight() * 0.8));
			}
		};
		
		safeArea.setOpaque(false);
		safeArea.setBackground(new Color(0,0,0,0));
		safeArea.setSize(854, 480);
			
		caseSafeArea.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseSafeArea.isSelected())
				{
					frame.setGlassPane(safeArea); 
			        frame.getGlassPane().setVisible(true);
				}
				else
				{
					frame.getGlassPane().setVisible(false);
				}
			}
			
		});
		
		frame.getContentPane().add(posX);	
		frame.getContentPane().add(textPosX);
		frame.getContentPane().add(px1);
		frame.getContentPane().add(posY);	
		frame.getContentPane().add(textPosY);
		frame.getContentPane().add(px2);
		frame.getContentPane().add(size);	
		frame.getContentPane().add(textSize);
		frame.getContentPane().add(px3);
		frame.getContentPane().add(opacity);
		frame.getContentPane().add(textOpacity);	
		frame.getContentPane().add(px4);
}
	
	public static void loadImage(String h, String m, String s, boolean logo, int size) {
       
		try
        {
        	String fichier = Shutter.liste.firstElement();
			if (Shutter.scanIsRunning)
			{
	            File dir = new File(Shutter.liste.firstElement());
	            for (File f : dir.listFiles())
	            {
	            	if (f.isHidden() == false && f.isFile())
	            	{    	
	            		fichier = f.toString();
	            		break;
	            	}
	            }
			}
		
			Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
			
    	  	//On récupère la taille du logo pour l'adater à l'image vidéo
			if (Utils.inputDeviceIsRunning)
				RecordInputDevice.setInputDevices();
			else
			{
				FFPROBE.Data(fichier);		
				
				do {
					Thread.sleep(100);
				} while (FFPROBE.isRunning);
			}
						
			//Ratio Widescreen
			if ((float) ImageWidth/ImageHeight >= (float) 854/480)
			{
				containerHeight = (int) Math.floor((float) 854 / ((float) ImageWidth / ImageHeight));
				containerWidth = 854;
			}
			else
			{
				containerWidth = (int) Math.floor((float) ((float) ImageWidth / ImageHeight) * 480);	
				containerHeight = 480;
			}
			
			//Ratio entre l'image d'entrée et celle affichée
			logoRatio = (float) ImageHeight / containerHeight;				
			
			String seek = " ";				
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
				seek = " -ss "+h+":"+m+":"+s+".0";

			//Envoi de la commande
			String cmd = " -vframes 1 -an -vf scale=" + containerWidth +":" + containerHeight + " -c:v bmp -sws_flags bilinear -f image2pipe pipe:-";
			
			if (Shutter.inputDeviceIsRunning) //Screen capture			
			{
				FFMPEG.run(" -v quiet " +  RecordInputDevice.setInputDevices() + cmd);
			}
			else					
			{					
      			FFMPEG.run(seek + " -v quiet -i " + '"' + fichier + '"' + cmd);
     		}	
			
			do {
				Thread.sleep(10);
			} while (FFMPEG.process.isAlive() == false);

			InputStream videoInput = FFMPEG.process.getInputStream();
 
			InputStream is = new BufferedInputStream(videoInput);
			Image imageBMP = ImageIO.read(is);	
        	
        	if (FFMPEG.error == false)
            {	
        		image.removeAll();  
        		image.add(WatermarkWindow.logo);
        		
	           	//Video
        		ImageIcon imageIcon = new ImageIcon(imageBMP);
	    		JLabel newImage = new JLabel(imageIcon);
	            imageIcon.getImage().flush();	

	            newImage.setHorizontalAlignment(SwingConstants.CENTER);
	    		newImage.setBounds(0, 0, containerWidth, containerHeight);
	    		  			
	    		image.setLocation(12 + ((854 - containerWidth) / 2), 58 + (int) Math.floor((float)(480 - containerHeight) / 2));
	    		image.setSize(newImage.getSize());
	    		
	    		//Rognage
	    		Color c = new Color(50,50,50);
	    		if (Shutter.lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
	    			c = Color.BLACK;
	    		
				JPanel topBorder = new JPanel();
				topBorder.setBackground(c);
				topBorder.setVisible(false);
				topBorder.setOpaque(true);	
				
				JPanel bottomBorder = new JPanel();
				bottomBorder.setBackground(c);
				bottomBorder.setVisible(false);		
				bottomBorder.setOpaque(true);					
				
				JPanel leftBorder = new JPanel();
				leftBorder.setBackground(c);
				leftBorder.setVisible(false);
				leftBorder.setOpaque(true);	
				
				JPanel rightBorder = new JPanel();
				rightBorder.setBackground(c);
				rightBorder.setVisible(false);
				rightBorder.setOpaque(true);	
				
				if (Shutter.caseRognage.isSelected())
				{						
					String i[] = FFPROBE.imageResolution.split("x");
					String original = String.valueOf((float) Integer.parseInt(i[0]) / Integer.parseInt(i[1]));
					
					String o[] = Shutter.comboH264Taille.getSelectedItem().toString().split("x");
					String output = String.valueOf((float) Integer.parseInt(o[0]) / Integer.parseInt(o[1]));

					if (original.length() > 4)
						original = original.substring(0,4);					

					//On affiche les côtés
					if (Float.parseFloat(output) < Float.parseFloat(original))
					{
						int crop = Integer.parseInt(o[0]); 
			        	
						int borderWidth = (int) Math.floor(((float) containerWidth - ((float) newImage.getHeight() / ((float) ImageHeight / crop))) / 2);
						
						leftBorder.setSize(borderWidth, newImage.getHeight());
						rightBorder.setSize(borderWidth, newImage.getHeight());
						rightBorder.setLocation(containerWidth - rightBorder.getWidth(), 0);

			    		leftBorder.setVisible(true);
			    		rightBorder.setVisible(true);	
			    		
						newImage.add(leftBorder);
						newImage.add(rightBorder);	
			    	}
					else
					{
			    		int crop = Integer.parseInt(o[1]); 
						
						int borderHeight = (int) Math.floor(((float) containerHeight - ((float) newImage.getWidth() / ((float) ImageWidth / crop))) / 2);
										
						topBorder.setSize(newImage.getWidth(), borderHeight);
						bottomBorder.setSize(newImage.getWidth(), borderHeight);
						bottomBorder.setLocation(0, containerHeight - bottomBorder.getHeight());
			    		
			    		topBorder.setVisible(true);
						bottomBorder.setVisible(true);	
						
						newImage.add(topBorder);
						newImage.add(bottomBorder);	
					}		        	
					
				}
					    		
	    		image.add(newImage); 
	    		image.repaint(); 
				frame.getContentPane().repaint();	    		
	    		
	    		//Logo 
	    		if (logo && size == -1) //Premier chargement
	    		{
	    			if (textSize != null)
	    				textSize.setText("100");
	    			loadLogo(100);	
	    		}
	    		else if (logo)
	    			loadLogo(size);
				
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
            }
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
	
	public static boolean loadLogo(int size) {
		try {
							
			//Logo    		
			if (Shutter.overlayDeviceIsRunning)
				RecordInputDevice.setOverlayDevice();
			else
			{
				FFPROBE.Data(logoFile);							

				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				} while (FFPROBE.isRunning);
			}
							
			int logoFinalSizeWidth = (int) Math.floor((float) ImageWidth / logoRatio);		
			int logoFinalSizeHeight = (int) Math.floor((float) ImageHeight / logoRatio);
	
			//Permet de s'adapter à la taille
			logoFinalSizeWidth = (int) Math.floor((float) logoFinalSizeWidth * ((double) size / 100));
			logoFinalSizeHeight = (int) Math.floor((float) logoFinalSizeHeight * ((double) size / 100));
			
			//Permet de conserver la position
			int newPosX = (int) Math.floor((logo.getWidth() - logoFinalSizeWidth) / 2);
			int newPosY = (int) Math.floor((logo.getHeight() - logoFinalSizeHeight) / 2);
			
			//Si le fichier est une vidéo
			String offset = "";			
			if (FFPROBE.totalLength > 40 && positionVideo != null 
				&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
			{
				DecimalFormat tc = new DecimalFormat("00");			
				String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
				String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
				String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));			
				offset = " -ss "+h+":"+m+":"+s+".0";
			}
			
			if (Shutter.overlayDeviceIsRunning)
				FFMPEG.run(" -v quiet " + RecordInputDevice.setOverlayDevice() + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bilinear -f image2pipe pipe:-");
			else
				FFMPEG.run(offset + " -v quiet -i " + '"' + logoFile + '"' + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bilinear -f image2pipe pipe:-");
			
			do {
				Thread.sleep(10);
			} while (FFMPEG.process.isAlive() == false);
			
			InputStream videoInput = FFMPEG.process.getInputStream();
 
			InputStream is = new BufferedInputStream(videoInput);
			Image logoPNG = ImageIO.read(is);
			
	       	if (FFMPEG.error == false)	  	       
	       	{
	            logo.removeAll();   
	                        	            
	            ImageIcon logoIcon = new ImageIcon(logoPNG);
	    		JLabel newLogo = new JLabel(logoIcon);
	    		logoIcon.getImage().flush();
	            newLogo.setHorizontalAlignment(SwingConstants.CENTER);
	            newLogo.setBounds(0, 0, logoFinalSizeWidth,logoFinalSizeHeight);
	            
	            logo.setLocation(logo.getLocation().x + newPosX, logo.getLocation().y + newPosY);
	            logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);
	            
	            //On enregistre la position
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;			
				
				logo.add(newLogo); 
	            logo.repaint();
	            frame.getContentPane().repaint();

				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	       	}            
           	       	
		} catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadLogo"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
		} 
		finally {
			
        	Shutter.enableAll();  
        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
			else
				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
        }
	return true;
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
				
				int posX = 0;
				int posY = 0;
				
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
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
									//Position des éléments
									if (p.getName().equals("textPosX") && textPosX.getText().length() > 0)
										posX = (int) (Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
									
									if (p.getName().equals("textPosY") && textPosY.getText().length() > 0)
										posY = (int) (Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));

									if (p.getName().equals("textOpacity") && textOpacity.getText().length() > 0)
									{										
										loadLogo(Integer.parseInt(textSize.getText()));		

										//Position des éléments après l'opacity et size
										textPosX.setText(String.valueOf(posX));
										textPosY.setText(String.valueOf(posY));										
										logo.setLocation((int) Math.floor(posX / logoRatio), logo.getLocation().y);
										logo.setLocation(logo.getLocation().x, (int) Math.floor(posY / logoRatio));
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
}