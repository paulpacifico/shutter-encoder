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
import java.awt.RenderingHints;
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
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
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
	public static JDialog shadow = new JDialog();
	private static JPanel image = new JPanel();
	private static JRadioButton caseSafeArea;
	public static String logoFile = new String();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel panelHaut;
	private JLabel topImage;	
	private JButton btnOK;
	
	/*
	 * Valeurs
	 */
	
	public static int containerWidth =  640;	
	public static int containerHeight = 360;
	public static float logoRatio = 3;
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;
    private int logoPosX = 0;
    private int logoPosY = 0;
    private static int logoLocX = 0;
    private static int logoLocY = 0;
	private static JSlider positionVideo;
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
		frame.setSize(665, 481);
		frame.setResizable(false);

		if (Functions.frame != null && Functions.frame.isVisible())
			frame.setModal(false);	
		else
			frame.setModal(true);
		
		frame.setAlwaysOnTop(true);
		frame.getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
				
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			setShadow();
		}
					
		panelHaut();
				 
		image.setLayout(null);        
		image.setOpaque(false);  
		frame.getContentPane().add(image);
		
		image.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) (image.getWidth() / 2 - logo.getWidth() / 2), (int) (image.getHeight() / 2 - logo.getHeight() / 2));	
					logoLocX =  logo.getLocation().x;
					logoLocY = logo.getLocation().y;
					textPosX.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
					textPosY.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) );  
				}
			}
			
		});
		
		caseSafeArea = new JRadioButton(Shutter.language.getProperty("caseSafeArea"));
		caseSafeArea.setFont(new Font("Arial", Font.PLAIN, 12));
		caseSafeArea.setBounds(12, 446, 113, 23);
		caseSafeArea.setBackground(new Color(50,50,50));
		frame.getContentPane().add(caseSafeArea);
		
		caseSafeArea.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseSafeArea.isSelected())
				{
		    		ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("contents/safeArea.png"));
		    		logoIcon = new ImageIcon(logoIcon.getImage().getScaledInstance(640, 360, Image.SCALE_DEFAULT));	
		    		JLabel newLogo = new JLabel(logoIcon);
		    		logoIcon.getImage().flush();
		            newLogo.setHorizontalAlignment(SwingConstants.CENTER);
		            newLogo.setSize(640, 360); 
		            image.add(newLogo);
		            frame.setGlassPane(newLogo); 
		            frame.getGlassPane().setVisible(true);
				}
				else
					 frame.getGlassPane().setVisible(false);
			}
			
		});
				 
		logo.setLayout(null);        
		logo.setOpaque(false); 
		logo.setBackground(new Color(0,0,0,50));
		logo.setSize(640, 360);	
		image.add(logo);
		
		logo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) (image.getWidth() / 2 - logo.getWidth() / 2), (int) (image.getHeight() / 2 - logo.getHeight() / 2));	
					textPosX.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
					textPosY.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) );  
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
				textPosX.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
				textPosY.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) );  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
		
		loadImage("0","0","0",true, -1, true);
		
		//On charge la position du logo après sa mise en place dans l'image
		logo.setLocation((int) (image.getWidth() / 2 - logo.getWidth() / 2), (int) (image.getHeight() / 2 - logo.getHeight() / 2));
       	logoLocX = logo.getLocation().x;
       	logoLocY = logo.getLocation().y;
		
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setBounds(170, 446, 480, 25);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
												
				Shutter.cropFinal = "crop=" + textSize.getText() + ":" + textOpacity.getText()  + ":" + textPosX.getText()  + ":" + textPosY.getText() ;
				        
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            Utils.changeDialogVisibility(frame, shadow, true);
	            
	            //Suppression image temporaire
						    		
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();
				
				File file2 = new File(Shutter.dirTemp + "logo.png");
				if (file2.exists()) file2.delete();  
			}
			
		});
		
		valeurs();
		
		//Raccourcis clavier
		Toolkit.getDefaultToolkit().addAWTEventListener(
			    new AWTEventListener(){
			        public void eventDispatched(AWTEvent event){	
			        	
			            KeyEvent ke = (KeyEvent)event;
			  				
			  				switch (ke.getKeyCode())
			  				{
			  					case KeyEvent.VK_UP:
			  						textPosY.setText(String.valueOf(Integer.parseInt(textPosY.getText()) - 1)); 
			  						logo.setLocation(logo.getLocation().x, (int) (Integer.parseInt(textPosY.getText()) / logoRatio));
								break;
			  					
			  					case KeyEvent.VK_DOWN:
									textPosY.setText(String.valueOf(Integer.parseInt(textPosY.getText()) + 1)); 
				  					logo.setLocation(logo.getLocation().x, (int) (Integer.parseInt(textPosY.getText()) / logoRatio));
								break;
			  					
								case KeyEvent.VK_LEFT:
									textPosX.setText(String.valueOf(Integer.parseInt(textPosX.getText()) - 1)); 
				  					logo.setLocation((int) (Integer.parseInt(textPosX.getText()) / logoRatio), logo.getLocation().y);
								break;
			  					
			  					case KeyEvent.VK_RIGHT:
									textPosX.setText(String.valueOf(Integer.parseInt(textPosX.getText()) + 1)); 
				  					logo.setLocation((int) (Integer.parseInt(textPosX.getText()) / logoRatio), logo.getLocation().y);
								break;
			  					
			  					case KeyEvent.VK_ESCAPE:
			  						logo.setLocation((int) (image.getWidth() / 2 - logo.getWidth() / 2), (int) (image.getHeight() / 2 - logo.getHeight() / 2));	
			  						textPosX.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
			  						textPosY.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) ); 
								break;
			  				}			  													              
			        	}	    

			    }, AWTEvent.KEY_EVENT_MASK);
	
		Utils.changeDialogVisibility(frame, shadow, false);		
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
	
	private static class LogoSettings {
		static int size;
		static int opacity;
		static int offset;
	}
	
	private void panelHaut() {
		
		panelHaut = new JPanel();		
		panelHaut.setLayout(null);
			
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 35,0,35, 15);
		panelHaut.add(quit);
		panelHaut.setBounds(0, 0, 1000, 52);
		
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
		            Utils.changeDialogVisibility(frame, shadow, true);
		            
					//Suppression images temporaires
							    		
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();
					
					File file2 = new File(Shutter.dirTemp + "logo.png");
					if (file2.exists()) file2.delete();    

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
		panelHaut.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(panelHaut.getSize().width, panelHaut.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		panelHaut.add(topImage);
		panelHaut.setBounds(0, 0, 1000, 52);
		frame.getContentPane().add(panelHaut);
		
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
	
	private void valeurs() {
		
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
    		FFPROBE.Data(Shutter.liste.firstElement());
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.dureeTotale == 0 && FFPROBE.isRunning);
		
		positionVideo.setMaximum(FFPROBE.dureeTotale);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font("Arial", Font.PLAIN, 11));
		positionVideo.setBounds(12, btnOK.getLocation().y - 26, 146, 22);	
		
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
							if (FFPROBE.dureeTotale > 40)
								loadImage(h,m,s, true, Integer.parseInt(textSize.getText()), true);
							else
								loadImage(h,m,s, false, -1, true);
						}
					});
					runProcess.start();					
			}
			
		});
		
		JLabel posX = new JLabel("Position X :");
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font("Arial", Font.PLAIN, 12));
		posX.setForeground(new Color(71,163,236));
		posX.setBounds(positionVideo.getLocation().x + positionVideo.getWidth() + 12, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
				
		textPosX = new JTextField(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
		textPosX.setName("textPosX");
		textPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosX.setFont(new Font("Arial", Font.PLAIN, 12));
		
		JLabel px1 = new JLabel("px");
		px1.setFont(new Font("Arial", Font.PLAIN, 12));
		px1.setForeground(new Color(71,163,236));
		px1.setBounds(textPosX.getLocation().x + textPosX.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		textPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosX.getText().length() > 0)
					logo.setLocation((int) (Integer.valueOf(textPosX.getText()) / logoRatio), logo.getLocation().y);
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
				
		JLabel posY = new JLabel("Position Y :");
		posY.setFont(new Font("Arial", Font.PLAIN, 12));
		posY.setForeground(new Color(71,163,236));
		posY.setBounds(px1.getLocation().x + 22, posX.getLocation().y, posY.getPreferredSize().width, 16);

		textPosY = new JTextField(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) );
		textPosY.setName("textPosY");
		textPosY.setBounds(posY.getLocation().x + posY.getWidth() + 2, posY.getLocation().y, 34, 16);
		textPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textPosY.setFont(new Font("Arial", Font.PLAIN, 12));
		
		textPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textPosY.getText().length() > 0)
					logo.setLocation(logo.getLocation().x, (int) (Integer.valueOf(textPosY.getText()) / logoRatio));
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
		px2.setFont(new Font("Arial", Font.PLAIN, 12));
		px2.setForeground(new Color(71,163,236));
		px2.setBounds(textPosY.getLocation().x + textPosY.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		JLabel size = new JLabel(Shutter.language.getProperty("lblSize"));
		size.setFont(new Font("Arial", Font.PLAIN, 12));
		size.setForeground(new Color(71,163,236));
		size.setBounds(px2.getLocation().x + 42, posX.getLocation().y, size.getPreferredSize().width, 16);
		
		textSize = new JTextField("100");
		textSize.setName("textSize");
		textSize.setBounds(size.getLocation().x + size.getWidth() + 2, size.getLocation().y, 34, 16);
		textSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textSize.setFont(new Font("Arial", Font.PLAIN, 12));
		
		textSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
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
					LogoSettings.size = e.getY();
					LogoSettings.offset = Integer.parseInt(textSize.getText());
				}
		
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				
			});
			
		textSize.addMouseMotionListener(new MouseMotionListener(){
		
				@Override
				public void mouseDragged(MouseEvent e) {
					if (textSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					{
						textSize.setText(String.valueOf(LogoSettings.offset + (e.getX() - LogoSettings.size)));
						if (Integer.parseInt(textSize.getText()) > 0)
							loadLogo(Integer.parseInt(textSize.getText()));
						else
							textSize.setText("1");
					}
				}
		
				@Override
				public void mouseMoved(MouseEvent e) {		
				}
			});	
		
		JLabel px3 = new JLabel("%");
		px3.setFont(new Font("Arial", Font.PLAIN, 12));
		px3.setForeground(new Color(71,163,236));
		px3.setBounds(textSize.getLocation().x + textSize.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
		JLabel opacity = new JLabel(Shutter.language.getProperty("lblOpacity"));
		opacity.setHorizontalAlignment(SwingConstants.RIGHT);
		opacity.setFont(new Font("Arial", Font.PLAIN, 12));
		opacity.setForeground(new Color(71,163,236));
		opacity.setBounds(btnOK.getLocation().x + btnOK.getWidth() - 104, posX.getLocation().y, opacity.getPreferredSize().width, 16);
		
		textOpacity = new JTextField("100");
		textOpacity.setName("textOpacity");
		textOpacity.setBounds(opacity.getLocation().x + opacity.getWidth() + 2, opacity.getLocation().y, 34, 16);
		textOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textOpacity.setOpaque(false);
		textOpacity.setFont(new Font("Arial", Font.PLAIN, 12));
		
		textOpacity.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				logo.repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textOpacity.getText().length() > 0)
				{
					if (Integer.valueOf(textOpacity.getText()) >= 100)
					{
						textOpacity.setText("100");
						logo.repaint();
					}
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
				LogoSettings.opacity = e.getY();
				LogoSettings.offset = Integer.parseInt(textOpacity.getText());
			}
	
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textOpacity.addMouseMotionListener(new MouseMotionListener(){
		
				@Override
				public void mouseDragged(MouseEvent e) {
					if (textOpacity.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					{
						textOpacity.setText(String.valueOf(LogoSettings.offset + (e.getX() - LogoSettings.opacity)));
						if (Integer.parseInt(textOpacity.getText()) < 0)
							textOpacity.setText("0");
						else if (Integer.parseInt(textOpacity.getText()) > 100)
							textOpacity.setText("100");
						
						loadLogo(Integer.parseInt(textSize.getText()));
					}
				}
		
				@Override
				public void mouseMoved(MouseEvent e) {		
				}
			});	
		
		JLabel px4 = new JLabel("%");
		px4.setFont(new Font("Arial", Font.PLAIN, 12));
		px4.setForeground(new Color(71,163,236));
		px4.setBounds(textOpacity.getLocation().x + textOpacity.getWidth() + 2, btnOK.getLocation().y - 22, posX.getPreferredSize().width, 16);
		
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
	
	public static void loadImage(String h, String m, String s, boolean logo, int size,  boolean loadImage) {
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
        	
        	if (loadImage)
        	{
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();
				
				Console.consoleFFMPEG.append(Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
				
	    	  	//On récupère la taille du logo pour l'adater à l'image vidéo
		  		FFPROBE.Data(fichier);		
				do {
					Thread.sleep(100);
				} while (FFPROBE.isRunning);
							
				//Ratio Widescreen
				if ((float) ImageWidth/ImageHeight >= (float) 640/360)
				{
					containerHeight = (int) Math.floor((float) 640 / ((float) ImageWidth / ImageHeight));
					containerWidth = 640;
				}
				else
				{
					containerWidth = (int) Math.floor((float) ((float) ImageWidth / ImageHeight) * 360);	
					containerHeight = 360;
				}
				
				//Ratio entre l'image d'entrée et celle affichée
				logoRatio = (float) ImageHeight / containerHeight;	
				
				String seek = " ";				
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
					seek = " -ss "+h+":"+m+":"+s+".0";

				FFMPEG.run(seek + " -i " + '"' + fichier + '"' + " -vframes 1 -an -vf scale=" + containerWidth + ":" + containerHeight + " -y " + '"' + Shutter.dirTemp + "preview.bmp" + '"');

		        do
		        {
		        	Thread.sleep(100);  
		        } while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false);
	            
        	}		
        	
        		image.removeAll();  
        		image.add(WatermarkWindow.logo);
        		
	           	//Video
        		Image imageBMP = ImageIO.read(new File(Shutter.dirTemp + "preview.bmp"));
                ImageIcon imageIcon = new ImageIcon(imageBMP);
	    		JLabel newImage = new JLabel(imageIcon);
	    		//Important
	    		if (loadImage)
	    			imageIcon.getImage().flush();
	    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
	    		newImage.setLocation(0, 0);
	    		newImage.setSize(containerWidth,containerHeight);
	    		  			
	    		image.setLocation(12 + ((640 - containerWidth) / 2), 58 + (int) ((float)(360 - containerHeight) / 2));
	    		image.setSize(newImage.getSize());
	    		
	    		//Contourne un bug
	            imageIcon = new ImageIcon(imageBMP);
	    		newImage = new JLabel(imageIcon);
	    		newImage.setSize(containerWidth,containerHeight);
	    		
	    		//Rognage
				JPanel topBorder = new JPanel();
				topBorder.setBackground(Color.BLACK);
				topBorder.setVisible(false);
				topBorder.setOpaque(true);	
				JPanel bottomBorder = new JPanel();
				bottomBorder.setBackground(Color.BLACK);
				bottomBorder.setVisible(false);		
				bottomBorder.setOpaque(true);	
				if (Shutter.caseRognage.isSelected())
				{
					String c[] = Shutter.comboH264Taille.getSelectedItem().toString().split("x");
					int crop = Integer.parseInt(c[1]); 
					
					int borderHeight = (int) Math.floor(((float) containerHeight - ((float) 640 / ((float) ImageWidth / crop))) / 2);
									
					topBorder.setSize(newImage.getWidth(), borderHeight);
					bottomBorder.setSize(newImage.getWidth(), borderHeight);
					bottomBorder.setLocation(0, containerHeight - bottomBorder.getHeight());
					
					topBorder.setVisible(true);
					bottomBorder.setVisible(true);					
				}
				
				newImage.add(topBorder);
				newImage.add(bottomBorder);	
	    		
	    		image.add(newImage); 
	    		image.repaint();    	
	    		
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
	    catch (Exception e)
	    {
 	       	JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	    }
        finally {
        	Shutter.enableAll();       
        }
	}
	
	public static boolean loadLogo(int size) {
		try {
				
			//Logo    		
			FFPROBE.Data(logoFile);	    		
			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			} while (FFPROBE.isRunning);
			
				
			File logoTemp = new File(Shutter.dirTemp + "logo.png");
							
			int logoFinalSizeWidth = (int) ((float) ImageWidth / logoRatio);		
			int logoFinalSizeHeight = (int) ((float) ImageHeight / logoRatio);
	
			//Permet de s'adapter à la taille
			logoFinalSizeWidth = (int) ((float) logoFinalSizeWidth * ((double) size / 100));
			logoFinalSizeHeight = (int) ((float) logoFinalSizeHeight * ((double) size / 100));
			
			//Permet de conserver la position
			int newPosX = (int) ((logo.getWidth() - logoFinalSizeWidth) / 2);
			int newPosY = (int) ((logo.getHeight() - logoFinalSizeHeight) / 2);
			if (logoTemp.exists())
				logoTemp.delete();
			
			//Si le fichier est une vidéo
			String offset = "";			
			if (FFPROBE.dureeTotale > 40 && positionVideo != null 
				&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
			{
				DecimalFormat tc = new DecimalFormat("00");			
				String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
				String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
				String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));			
				offset = " -ss "+h+":"+m+":"+s+".0";
			}
				
			FFMPEG.run(offset + " -i " + '"' + logoFile + '"' + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -y " + '"' + Shutter.dirTemp + "logo.png" + '"');
			
			do {
	        	Thread.sleep(100);  
	        } while (logoTemp.exists() == false && FFMPEG.error == false);		
			
	       	if (FFMPEG.error)
	  	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadLogo"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
			
            logo.removeAll();   
            
    		ImageIcon logoIcon = new ImageIcon(Shutter.dirTemp + "logo.png");
    		JLabel newLogo = new JLabel(logoIcon);
    		//Important
    		logoIcon.getImage().flush();
            newLogo.setHorizontalAlignment(SwingConstants.CENTER);
            newLogo.setLocation(0, 0);
            
            logo.setLocation(logo.getLocation().x + newPosX, logo.getLocation().y + newPosY);
            logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);   			
            
    		//Contourne un bug
            logoIcon = new ImageIcon(Shutter.dirTemp + "logo.png");
            newLogo = new JLabel(logoIcon);
            newLogo.setSize(logoFinalSizeWidth,logoFinalSizeHeight);
            
            //On enregistre la position
			logoLocX = logo.getLocation().x;
			logoLocY = logo.getLocation().y;			
			
			logo.add(newLogo); 
            logo.repaint();
            
			textPosX.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().x * logoRatio) ) ) );
			textPosY.setText(String.valueOf(Integer.valueOf((int) (logo.getLocation().y * logoRatio) ) ) ); 
                        
			Shutter.tempsRestant.setVisible(false);
            Shutter.progressBar1.setValue(0);
            
           	       	
		} catch (Exception e) {
		} finally {
        	Shutter.enableAll();       
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
				
				
				File file = new File(Shutter.dirTemp + "logo.png");
				
				do {
					Thread.sleep(100);
				} while (file.exists() == false);	
				
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
										posX = (int) (Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
									
									if (p.getName().equals("textPosY") && textPosY.getText().length() > 0)
										posY = (int) (Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));

									if (p.getName().equals("textOpacity") && textOpacity.getText().length() > 0)
									{										
										loadLogo(Integer.parseInt(textSize.getText()));		
										
										do {
											Thread.sleep(100);
										} while (file.exists() == false);
										
										//Position des éléments après l'opacity et size
										textPosX.setText(String.valueOf(posX));
										textPosY.setText(String.valueOf(posY));										
										logo.setLocation((int) (posX / logoRatio), logo.getLocation().y);
										logo.setLocation(logo.getLocation().x, (int) (posY / logoRatio));
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
	
 	private void setShadow() {
		shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
    	shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
    	shadow.setUndecorated(true);
    	shadow.setAlwaysOnTop(true);
    	shadow.setContentPane(new LogoShadow());
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
 		});		
	}
}

//Ombre
@SuppressWarnings("serial")
class LogoShadow extends JPanel {
  public void paintComponent(Graphics g){
	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  Graphics2D g1 = (Graphics2D)g.create();
	  g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
	  g1.setRenderingHints(qualityHints);
	  g1.setColor(new Color(0,0,0));
	  g1.fillRect(0,0,WatermarkWindow.frame.getWidth() + 14, WatermarkWindow.frame.getHeight() + 127);
	  
	  for (int i = 0 ; i < 7; i++) 
	  {
		  Graphics2D g2 = (Graphics2D)g.create();
		  g2.setRenderingHints(qualityHints);
		  g2.setColor(new Color(0,0,0, i * 10));
		  g2.drawRoundRect(i, i, WatermarkWindow.frame.getWidth() + 13 - i * 2, WatermarkWindow.frame.getHeight() + 2, 20, 20);
	  }
   }
}