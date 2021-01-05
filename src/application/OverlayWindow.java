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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import library.FFMPEG;
import library.FFPROBE;

public class OverlayWindow {
	public static JDialog frame;
	private static JPanel image = new JPanel();
	private static JLabel changePositions = new JLabel("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>");	
	private static JPanel timecode;
	private static JPanel fileName;
	
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
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;

    public static int containerWidth =  640;	
    public static int containerHeight = 360;
    
	public static String hexTc = "ff";
	public static String hexAlphaTc = "ff";
	public static String hexName = "ff";
	public static String hexAlphaName = "ff";
	public static float imageRatio = 3;
    private int tcPosX = 0;
    private int tcPosY = 0;
    private static int tcLocX = 0;
    private static int tcLocY = 0;    
    private static int filePosX = 0;
    private static int filePosY = 0;
    private static int fileLocX = 0;
    private static int fileLocY = 0;
	public static JTextField textTcPosX;
	public static JTextField textTcPosY;
	public static JTextField textNamePosX;
	public static JTextField textNamePosY;
    
	public static JSlider positionVideo;
	public static JSpinner spinnerSizeTC;
	public static JSpinner spinnerSizeName;
	public static JSpinner spinnerOpacityTC;
	public static JSpinner spinnerOpacityName;
    
	public static JRadioButton caseAddTimecode = new JRadioButton(Shutter.language.getProperty("caseAddTimecode"));//IMPORTANT
	public static JLabel lblTimecode = new JLabel(Shutter.language.getProperty("lblTimecode"));
	public static JTextField TC1 = new JTextField("00");
	public static JTextField TC2 = new JTextField("00");
	public static JTextField TC3 = new JTextField("00");
	public static JTextField TC4 = new JTextField("00");
	public static JTextField text = new JTextField("");
	private static long textTime = System.currentTimeMillis();
	private static Thread changeText;
	public static JRadioButton caseShowTimecode = new JRadioButton(Shutter.language.getProperty("caseShowTimecode"));//IMPORTANT
	public static JRadioButton caseShowFileName = new JRadioButton(Shutter.language.getProperty("caseShowFileName"));//IMPORTANT
	public static JRadioButton caseShowText = new JRadioButton(Shutter.language.getProperty("caseShowText"));//IMPORTANT
	public static JComboBox<String> comboFont;	
	public static JLabel lblBackground; 
	private static JPanel panelColor = new JPanel();
	private static JPanel panelColor2 = new JPanel();
	public static String font;
	public static Color fontColor;
	public static Color backgroundColor;
	public static String hex = "FFFFFF";
	public static String hex2 = "000000";
	public static String alpha = "7F";
	//private Integer screenDPI = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
	//private Float DPIfactor = (float) screenDPI / 96;
	
	public OverlayWindow() {		
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("grpOverlay"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(665, 550);
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
				
		boutons();
		
		image.setLayout(null); 
		image.setOpaque(false); 

		loadImage("0","0","0", true);	
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private static class MouseTcPosition {
		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
	}
	
	private static class MouseNamePosition {
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
		            
					//Suppression images temporaires
							    		
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();

					Shutter.caseAddOverlay.setSelected(false);
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("grpOverlay"));
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
		
		image.setBounds(12, 58, 640, 360);		
		frame.getContentPane().add(image);
		
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
	
	@SuppressWarnings("serial")
	private void boutons() {	
		
		timecode = new JPanel() {
			 @Override
			    protected void paintComponent(Graphics g)
			    {				 
			        super.paintComponent(g);
			
			        Graphics2D g2 = (Graphics2D) g;
			
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			        
					String sp[] = FFPROBE.imageResolution.split("x");
					imageRatio = (float) Integer.parseInt(sp[0])/containerWidth;
			        g2.setFont(new Font(comboFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(spinnerSizeTC.getValue().toString()) / imageRatio)));
						        
			        String str = "00:00:00:00";
			        
					if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected()) 
					{						
						if (FFPROBE.isRunning) //Contourne un bug incompréhensible
						{
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}
							} while (FFPROBE.isRunning);
						}
						
						long tcH = 0;				
						long tcM = 0;
						long tcS = 0;
						long tcI = 0;
						
						if (caseAddTimecode.isSelected() && TC1.getText().isEmpty() == false && TC2.getText().isEmpty() == false && TC3.getText().isEmpty() == false && TC4.getText().isEmpty() == false)
						{
							tcH = Integer.valueOf(TC1.getText());
							tcM = Integer.valueOf(TC2.getText());
							tcS = Integer.valueOf(TC3.getText());
							tcI = Integer.valueOf(TC4.getText());
						}
						else if (caseShowTimecode.isSelected())
						{
							tcH = Integer.valueOf(FFPROBE.timecode1);
							tcM = Integer.valueOf(FFPROBE.timecode2);
							tcS = Integer.valueOf(FFPROBE.timecode3);
							tcI = Integer.valueOf(FFPROBE.timecode4);
						}
						
						tcH = tcH * 3600000;
						tcM = tcM * 60000;
						tcS = tcS * 1000;
						
						long offset =  positionVideo.getValue() + tcH + tcM + tcS;
						NumberFormat formatter = new DecimalFormat("00");
				        String heures =  (formatter.format((offset/1000) / 3600));
				        String minutes = (formatter.format( ((offset/1000) / 60) % 60) );
				        String secondes = (formatter.format((offset/1000) % 60));				        
				        String images = (formatter.format(tcI));	
				        
				        if (caseAddTimecode.isSelected() && lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
				        {
				        	str = String.format("%.0f",
				        	Integer.parseInt(heures) * 3600 * FFPROBE.currentFPS +
				        	Integer.parseInt(minutes) * 60 * FFPROBE.currentFPS +
				        	Integer.parseInt(secondes) * FFPROBE.currentFPS +
				        	Integer.parseInt(images));
				        }
				        else
				        	str = heures+":"+minutes+":"+secondes+":"+images;	
					}
					
			        Rectangle bounds = getStringBounds(g2, str, 0 ,0);
					
					if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
						g2.setColor(new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(), (int) ( (float) (Integer.parseInt(spinnerOpacityTC.getValue().toString()) * 255) /  100)));
					else
						g2.setColor(new Color(0,0,0,0));
										
					GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			        AffineTransform transform = gfxConfig.getDefaultTransform();
			        
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = Retina
				        g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					else
						g2.fillRect(0, 0, bounds.width, bounds.height);
					
					if (lblBackground.getText().equals(Shutter.language.getProperty("aucun")))
						g2.setColor(new Color(fontColor.getRed(),fontColor.getGreen(),fontColor.getBlue(), (int) ( (float) (Integer.parseInt(spinnerOpacityTC.getValue().toString()) * 255) /  100)));
					else				
						g2.setColor(fontColor);
					
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = Retina
			        {
			        	Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, 0, bounds.height / 2 - offset / 2);	
			        }
					else
					{
						Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, 0, bounds.height - offset);	
					}
			        
			        image.repaint();
					timecode.repaint();
			    }
			 
				private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
				{
					FontRenderContext frc = g2.getFontRenderContext();
					GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
					return gv.getPixelBounds(null, x, y);
				}
			};
					
		timecode.setLayout(null);			
		
		timecode.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				tcPosX = e.getLocationOnScreen().x;
				tcPosY = e.getLocationOnScreen().y;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {	
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					if (changePositions.getText().equals("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>"))
					{
						tcLocX = (int) Math.round(image.getWidth()/2 - timecode.getWidth()/2);
						tcLocY = timecode.getHeight();	
						timecode.setLocation(MouseInfo.getPointerInfo().getLocation().x - tcPosX + tcLocX, MouseInfo.getPointerInfo().getLocation().y - tcPosY + tcLocY);
					}						
					else
					{
						tcLocX = (int) Math.round(image.getWidth()/2 - timecode.getWidth()/2);
						tcLocY = containerHeight - timecode.getHeight() * 2;
						timecode.setLocation(MouseInfo.getPointerInfo().getLocation().x - tcPosX + tcLocX, MouseInfo.getPointerInfo().getLocation().y - tcPosY + tcLocY);
					}
										
					textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * imageRatio)));
					textTcPosY.setText(String.valueOf((int) Math.round (timecode.getLocation().y * imageRatio)));  
				}
				else
				{
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;
				}
			}
			
			
		});
		
		timecode.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {					
				timecode.setLocation(MouseInfo.getPointerInfo().getLocation().x - tcPosX + tcLocX, MouseInfo.getPointerInfo().getLocation().y - tcPosY + tcLocY);		
				textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * imageRatio)));
				textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * imageRatio)));  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
			
		fileName = new JPanel() {
		 @Override
		    protected void paintComponent(Graphics g)
		    {
			 
		        super.paintComponent(g);
		
		        Graphics2D g2 = (Graphics2D) g;
		
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        
				String sp[] = FFPROBE.imageResolution.split("x");
				imageRatio = (float) Integer.parseInt(sp[0])/containerWidth;
		        g2.setFont(new Font(comboFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(spinnerSizeName.getValue().toString()) / imageRatio)));
		
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
		        
		        String str = new File(fichier).getName();
		        
				if (caseShowText.isSelected())
					str = text.getText();
				
		        Rectangle bounds = getStringBounds(g2, str, 0 ,0);
		        		        								
				if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
					g2.setColor(new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(), (int) ( (float) (Integer.parseInt(spinnerOpacityName.getValue().toString()) * 255) /  100)));
				else
					g2.setColor(new Color(0,0,0,0));

				GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		        AffineTransform transform = gfxConfig.getDefaultTransform();
				
				if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = Retina
			        g2.fillRect(0, 0, bounds.width, bounds.height / 2);
				else
					g2.fillRect(0, 0, bounds.width, bounds.height);
				
				if (lblBackground.getText().equals(Shutter.language.getProperty("aucun")))
					g2.setColor(new Color(fontColor.getRed(),fontColor.getGreen(),fontColor.getBlue(), (int) ( (float) (Integer.parseInt(spinnerOpacityName.getValue().toString()) * 255) /  100)));
				else				
					g2.setColor(fontColor);				
				
				if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = Retina
		        {
		        	Integer offset = bounds.height + (int) bounds.getY();						
					g2.drawString(str, 0, bounds.height / 2 - offset / 2);	
		        }
				else
				{
					Integer offset = bounds.height + (int) bounds.getY();						
					g2.drawString(str, 0, bounds.height - offset);	
				}	
		        
		        image.repaint();
				fileName.repaint();
		    }
		 
			private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
			{
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
				return gv.getPixelBounds(null, x, y);
			}
		};
				
		fileName.setLayout(null);
		
		fileName.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				filePosX = e.getLocationOnScreen().x;
				filePosY = e.getLocationOnScreen().y;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {			
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					if (changePositions.getText().equals("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>"))
					{
						fileLocX = (int) Math.round(image.getWidth()/2 - fileName.getWidth()/2);
						fileLocY = containerHeight - fileName.getHeight() * 2;
						fileName.setLocation(MouseInfo.getPointerInfo().getLocation().x - filePosX + fileLocX, MouseInfo.getPointerInfo().getLocation().y - filePosY + fileLocY);
					}
					else
					{
						fileLocX = (int) Math.round(image.getWidth()/2 - fileName.getWidth()/2);
						fileLocY = fileName.getHeight();	
						fileName.setLocation(MouseInfo.getPointerInfo().getLocation().x - filePosX + fileLocX, MouseInfo.getPointerInfo().getLocation().y - filePosY + fileLocY);;
					}
					
					textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * imageRatio)));
					textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * imageRatio)));  
				}
				else
				{
					fileLocX = fileName.getLocation().x;
					fileLocY = fileName.getLocation().y;
				}
			}
			
			
		});
		
		fileName.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				fileName.setLocation(MouseInfo.getPointerInfo().getLocation().x - filePosX + fileLocX, MouseInfo.getPointerInfo().getLocation().y - filePosY + fileLocY);		
				textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * imageRatio)));
				textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * imageRatio)));  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
			
		timecode.setBackground(new Color(0,0,0,0));
		fileName.setBackground(new Color(0,0,0,0));
		
		JLabel lblFont = new JLabel(Shutter.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblFont.setBounds(12, frame.getHeight() - 124, lblFont.getPreferredSize().width, 16);
		frame.getContentPane().add(lblFont);
		
		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		comboFont = new JComboBox<String>(Fonts);		
		comboFont.setName("comboFont");
		comboFont.setSelectedItem("Arial");
		comboFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboFont.setRenderer(new ComboRendererOverlay(comboFont));
		comboFont.setEditable(true);
		comboFont.setBounds(lblFont.getX() + lblFont.getWidth() + 7, lblFont.getY() - 4, 153, 22);
		frame.getContentPane().add(comboFont);
				
		comboFont.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
	    {
			String text = "";
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void keyReleased(KeyEvent e) {
				
					if (comboFont.getEditor().toString().length() <= 1)
						text = String.valueOf(e.getKeyChar());

					if (Character.isLetterOrDigit(e.getKeyChar())) {
						comboFont.setModel(new DefaultComboBoxModel(Fonts));
						text += e.getKeyChar();

						ArrayList<String> newList = new ArrayList<String>();
						for (int i = 0; i < comboFont.getItemCount(); i++) {
							if (Fonts[i].toString().length() >= text.length()) {
								if (Fonts[i].toString().toLowerCase().substring(0, text.length()).contains(text)
										&& Fonts[i].toString().contains(":") == false) {
									newList.add(Fonts[i].toString());
								}
							}
						}

						// Pour éviter d'afficher le premier item
						comboFont.getEditor().setItem(text);

						if (newList.isEmpty() == false) {
							comboFont.setModel(new DefaultComboBoxModel(newList.toArray()));
							comboFont.showPopup();
						}

					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboFont.setModel(new DefaultComboBoxModel(Fonts));
						comboFont.getEditor().setItem("");
						comboFont.hidePopup();
						text = "";
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						e.consume();// Contournement pour éviter le listeDrop
					else
						comboFont.hidePopup();
			}
					
	    });	
		
		comboFont.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				comboFont.setFont(new Font(comboFont.getSelectedItem().toString(), Font.PLAIN, 11));
				sliderChange(false);	
			}
			
		});
		
		JLabel lblColor = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblColor.setBounds(comboFont.getX() + comboFont.getWidth() + 7, lblFont.getY(), lblColor.getPreferredSize().width + 4, 16);
		frame.getContentPane().add(lblColor);
		
		panelColor = new JPanel();
		panelColor.setName("panelColor");
		panelColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelColor.setBackground(Color.WHITE);
		panelColor.setBounds(lblColor.getLocation().x + lblColor.getWidth() + 7, comboFont.getLocation().y, 41, 22);
		frame.getContentPane().add(panelColor);
		
		panelColor.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				fontColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				if (fontColor != null)
				{
					panelColor.setBackground(fontColor);	
					sliderChange(false);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {		
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
		
		lblBackground = new JLabel(Shutter.language.getProperty("lblBackgroundOn"));
		lblBackground.setName("lblBackground");
		lblBackground.setBackground(new Color(80, 80, 80));
		lblBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblBackground.setOpaque(true);
		lblBackground.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblBackground.setBounds(panelColor.getLocation().x + panelColor.getWidth() + 11, lblColor.getLocation().y, 70, 16);
		frame.getContentPane().add(lblBackground);
		
		lblBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				{
					lblBackground.setText(Shutter.language.getProperty("aucun"));
					spinnerOpacityTC.setValue(100);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
					spinnerOpacityName.setValue(100);
				}
				else
				{
					lblBackground.setText(Shutter.language.getProperty("lblBackgroundOn"));
					spinnerOpacityTC.setValue(50);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
					spinnerOpacityName.setValue(50);
				}
								
				//sliderChange(false);	
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
				
		JLabel lblColor2 = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor2.setAlignmentX(SwingConstants.RIGHT);
		lblColor2.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblColor2.setBounds(lblBackground.getLocation().x + lblBackground.getWidth() + 11, lblFont.getY(), lblColor2.getPreferredSize().width + 4, 16);
		frame.getContentPane().add(lblColor2);
		
		panelColor2 = new JPanel();
		panelColor2.setName("panelColor2");
		panelColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelColor2.setBackground(Color.BLACK);
		panelColor2.setBounds(lblColor2.getLocation().x + lblColor2.getWidth() + 7, comboFont.getLocation().y, 41, 22);
		frame.getContentPane().add(panelColor2);
		
		panelColor2.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				backgroundColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				if (backgroundColor != null)
				{
					panelColor2.setBackground(backgroundColor);	
					sliderChange(false);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {		
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
				
		caseAddTimecode.setName("caseAddTimecode");				
		caseAddTimecode.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAddTimecode.setSize(caseAddTimecode.getPreferredSize().width, 23);
		caseAddTimecode.setLocation(12, frame.getHeight() - 102);
		frame.add(caseAddTimecode);
		
		caseAddTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseAddTimecode.isSelected()) {
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					TC4.setEnabled(true);	
					caseShowTimecode.setSelected(false);
				} else {
					FFPROBE.timecode1 = "";
					FFPROBE.timecode2 = "";
					FFPROBE.timecode3 = "";
					FFPROBE.timecode4 = "";
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					TC4.setEnabled(false);
				}
				
				sliderChange(false);
			}

		});

		lblTimecode.setName("lblTimecode");
		lblTimecode.setBackground(new Color(80, 80, 80));
		lblTimecode.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimecode.setOpaque(true);
		lblTimecode.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblTimecode.setBounds(caseAddTimecode.getLocation().x + caseAddTimecode.getWidth() + 2, caseAddTimecode.getLocation().y + 3, 70, 16);
		frame.add(lblTimecode);
		
		lblTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblTimecode.getText().equals(Shutter.language.getProperty("lblTimecode")))
					lblTimecode.setText(Shutter.language.getProperty("lblFrameNumber"));
				else
					lblTimecode.setText(Shutter.language.getProperty("lblTimecode"));
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		
		TC1.setName("TC1");
		TC1.setEnabled(false);
		TC1.setText("00");
		TC1.setHorizontalAlignment(SwingConstants.CENTER);
		TC1.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC1.setColumns(10);
		TC1.setBounds(lblTimecode.getX() + lblTimecode.getWidth() + 7, caseAddTimecode.getY(), 32, 21);
		frame.add(TC1);
		
		TC2.setName("TC2");
		TC2.setEnabled(false);
		TC2.setText("00");
		TC2.setHorizontalAlignment(SwingConstants.CENTER);
		TC2.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC2.setColumns(10);
		TC2.setBounds(TC1.getX() + 36, TC1.getY(), 32, 21);
		frame.add(TC2);
		
		TC3.setName("TC3");
		TC3.setEnabled(false);
		TC3.setText("00");
		TC3.setHorizontalAlignment(SwingConstants.CENTER);
		TC3.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC3.setColumns(10);
		TC3.setBounds(TC2.getX() + 36, TC1.getY(), 32, 21);
		frame.add(TC3);

		TC4.setName("TC4");
		TC4.setEnabled(false);
		TC4.setText("00");
		TC4.setHorizontalAlignment(SwingConstants.CENTER);
		TC4.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC4.setColumns(10);
		TC4.setBounds(TC3.getX() + 36, TC1.getY(), 32, 21);
		frame.add(TC4);
		
		TC1.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC1.getText().length() >= 2)
					TC1.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sliderChange(false);
			}
		});

		TC2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC2.getText().length() >= 2)
					TC2.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sliderChange(false);
			}
		});

		TC3.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC3.getText().length() >= 2)
					TC3.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sliderChange(false);
			}
		});

		TC4.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC4.getText().length() >= 2)
					TC4.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sliderChange(false);
			}
		});
				
		caseShowTimecode.setName("caseShowTimecode");
		caseShowTimecode.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseShowTimecode.setEnabled(true);
		caseShowTimecode.setSize(caseShowTimecode.getPreferredSize().width, 23);
		caseShowTimecode.setLocation(caseAddTimecode.getX(), caseAddTimecode.getY() + caseAddTimecode.getHeight());
		frame.add(caseShowTimecode);
				
		caseShowTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseShowTimecode.isSelected()) {
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					TC4.setEnabled(false);
					caseAddTimecode.setSelected(false);
				}					
				sliderChange(true);
			}

		});
						
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
				Thread.sleep(10);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
		
		String sp[] = FFPROBE.imageResolution.split("x");
		imageRatio = (float) Integer.parseInt(sp[0])/containerWidth;
		
		positionVideo.setMaximum(FFPROBE.totalLength);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font("FreeSans", Font.PLAIN, 11));
		positionVideo.setLocation(panelColor2.getX() + panelColor2.getWidth() + 6, panelColor2.getY());
		positionVideo.setSize(frame.getWidth() - positionVideo.getX() - 12, 22);	
		
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
					sliderChange(true);
			}
			
		});
		
		JLabel lblSizeTC = new JLabel(Shutter.language.getProperty("lblSize"));
		lblSizeTC.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblSizeTC.setAlignmentX(SwingConstants.RIGHT);
		lblSizeTC.setBounds(12,  caseShowTimecode.getY() + caseShowTimecode.getHeight() + 11, lblSizeTC.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblSizeTC);
		
		spinnerSizeTC = new JSpinner(new SpinnerNumberModel(Math.round((float) 27 * imageRatio ), 1, 999, Math.round(imageRatio)));
		spinnerSizeTC.setName("spinnerSizeTC");
		spinnerSizeTC.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerSizeTC.setBounds(lblSizeTC.getLocation().x + lblSizeTC.getWidth() + 11, lblSizeTC.getLocation().y - 3, 54, 22);
		frame.getContentPane().add(spinnerSizeTC);
		
		spinnerSizeTC.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				sliderChange(false);
			}
			
		});
		
		JLabel lblOpacityTC = new JLabel(Shutter.language.getProperty("lblOpacity"));
		lblOpacityTC.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblOpacityTC.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityTC.setBounds(spinnerSizeTC.getLocation().x + spinnerSizeTC.getWidth() + 11, lblSizeTC.getLocation().y, lblOpacityTC.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblOpacityTC);

		spinnerOpacityTC = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
		spinnerOpacityTC.setName("spinnerOpacityTC");
		spinnerOpacityTC.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerOpacityTC.setBounds(lblOpacityTC.getLocation().x + lblOpacityTC.getWidth() + 11, lblSizeTC.getLocation().y - 3, spinnerSizeTC.getWidth(), 22);
		frame.getContentPane().add(spinnerOpacityTC);
		
		spinnerOpacityTC.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {		
				sliderChange(false);		
			}
			
		});
		
		JLabel posX = new JLabel(Shutter.language.getProperty("pX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setAlignmentX(SwingConstants.RIGHT);
		posX.setBounds(lblSizeTC.getX(), lblSizeTC.getY() + lblSizeTC.getHeight() + 6, lblOpacityTC.getPreferredSize().width + 2, 16);
		frame.getContentPane().add(posX);
				
		textTcPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().x * imageRatio) ) ) );
		textTcPosX.setName("textTcPosX");
		textTcPosX.setBounds(spinnerSizeTC.getLocation().x, posX.getLocation().y, spinnerSizeTC.getWidth(), 16);
		textTcPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosX.setFont(new Font("FreeSans", Font.PLAIN, 12));
		frame.getContentPane().add(textTcPosX);
		
		textTcPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textTcPosX.getText().length() > 0)
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), timecode.getLocation().y);
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textTcPosX.getText().length() >= 4)
					textTcPosX.setText("");				
			}		
			
		});
		
		textTcPosX.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textTcPosX.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textTcPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textTcPosX.setText(String.valueOf(MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX)));
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), timecode.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel posY = new JLabel(Shutter.language.getProperty("pY"));
		posY.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setAlignmentX(SwingConstants.RIGHT);
		posY.setBounds(lblOpacityTC.getX(), posX.getLocation().y, lblOpacityTC.getPreferredSize().width, 16);
		frame.getContentPane().add(posY);

		textTcPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().y * imageRatio) ) ) );
		textTcPosY.setName("textTcPosY");
		textTcPosY.setBounds(spinnerOpacityTC.getLocation().x, posY.getLocation().y, spinnerOpacityTC.getWidth(), 16);
		textTcPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosY.setFont(new Font("FreeSans", Font.PLAIN, 12));
		frame.getContentPane().add(textTcPosY);
		
		textTcPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textTcPosY.getText().length() > 0)
					timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textTcPosY.getText().length() >= 4)
					textTcPosY.setText("");				
			}			
			
		});
		
		textTcPosY.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseY = e.getY();
				MouseTcPosition.offsetY = Integer.parseInt(textTcPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textTcPosY.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textTcPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textTcPosY.setText(String.valueOf(MouseTcPosition.offsetY + (e.getY() - MouseTcPosition.mouseY)));
					timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		textNamePosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().y * imageRatio) ) ) );
		textNamePosY.setName("textNamePosY");
		textNamePosY.setBounds(frame.getWidth() - spinnerOpacityTC.getWidth() - 13, posY.getLocation().y, spinnerOpacityTC.getWidth(), 16);
		textNamePosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosY.setFont(new Font("FreeSans", Font.PLAIN, 12));
		frame.getContentPane().add(textNamePosY);
		
		textNamePosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textNamePosY.getText().length() > 0)
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textNamePosY.getText().length() >= 4)
					textNamePosY.setText("");				
			}			
			
		});
		
		textNamePosY.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseY = e.getY();
				MouseNamePosition.offsetY = Integer.parseInt(textNamePosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textNamePosY.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textNamePosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textNamePosY.setText(String.valueOf(MouseNamePosition.offsetY + (e.getY() - MouseNamePosition.mouseY)));
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
					
		JLabel posY2 = new JLabel(Shutter.language.getProperty("pY"));
		posY2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posY2.setForeground(Utils.themeColor);
		posY2.setAlignmentX(SwingConstants.RIGHT);
		posY2.setBounds(textNamePosY.getX() - lblOpacityTC.getPreferredSize().width - 11, posY.getLocation().y, lblOpacityTC.getPreferredSize().width, 16);
		frame.getContentPane().add(posY2);
		
		spinnerOpacityName = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
		spinnerOpacityName.setName("spinnerOpacityName");
		spinnerOpacityName.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerOpacityName.setBounds(textNamePosY.getX(), spinnerOpacityTC.getLocation().y, spinnerOpacityTC.getWidth(), 22);
		frame.getContentPane().add(spinnerOpacityName);
		
		spinnerOpacityName.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textNamePosY.getText().length() > 0)
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textNamePosY.getText().length() >= 4)
					textNamePosY.setText("");				
			}			
			
		});
		
		spinnerOpacityName.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {		
				sliderChange(false);		
			}
			
		});
		
		JLabel lblOpacityName = new JLabel(Shutter.language.getProperty("lblOpacity"));
		lblOpacityName.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblOpacityName.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityName.setBounds(spinnerOpacityName.getLocation().x - lblOpacityTC.getPreferredSize().width - 11, lblOpacityTC.getLocation().y, lblOpacityName.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblOpacityName);
				
		spinnerSizeName = new JSpinner(new SpinnerNumberModel(Math.round((float) 27 * imageRatio ), 1, 999, Math.round(imageRatio)));
		spinnerSizeName.setName("spinnerSizeName");
		spinnerSizeName.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerSizeName.setBounds(lblOpacityName.getLocation().x - spinnerSizeTC.getWidth() - 15, lblOpacityTC.getLocation().y - 3, spinnerSizeTC.getWidth(), 22);
		frame.getContentPane().add(spinnerSizeName);
		
		spinnerSizeName.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {							

				sliderChange(false);
			}
			
		});
		
		JLabel lblSizeName = new JLabel(Shutter.language.getProperty("lblSize"));
		lblSizeName.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblSizeName.setAlignmentX(SwingConstants.RIGHT);
		lblSizeName.setBounds(spinnerSizeName.getX() - lblSizeTC.getWidth() - 11, lblSizeTC.getY(), lblSizeName.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblSizeName);
						
		textNamePosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().x * imageRatio) ) ) );
		textNamePosX.setName("textNamePosX");
		textNamePosX.setBounds(posY2.getLocation().x - textNamePosY.getWidth() - 15, posY2.getLocation().y, textNamePosY.getWidth(), 16);
		textNamePosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosX.setFont(new Font("FreeSans", Font.PLAIN, 12));
		frame.getContentPane().add(textNamePosX);
		
		textNamePosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textNamePosX.getText().length() > 0)
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), fileName.getLocation().y);
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textNamePosX.getText().length() >= 4)
					textNamePosX.setText("");				
			}			
			
		});
		
		textNamePosX.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNamePosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textNamePosX.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textNamePosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textNamePosX.setText(String.valueOf(MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX)));
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), fileName.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel posX2 = new JLabel(Shutter.language.getProperty("pX"));
		posX2.setHorizontalAlignment(SwingConstants.LEFT);
		posX2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		posX2.setForeground(Utils.themeColor);
		posX2.setAlignmentX(SwingConstants.RIGHT);
		posX2.setBounds(lblSizeName.getX(), posX.getY(), posX.getPreferredSize().width, 16);
		frame.getContentPane().add(posX2);
		
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setBounds(textTcPosY.getX() + textTcPosY.getWidth() + 9, textTcPosY.getY() - 4, frame.getWidth() - (textTcPosY.getX() + textTcPosY.getWidth()) - (frame.getWidth() - posX2.getX()) - 18, 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            Utils.changeDialogVisibility(frame, true);
	            
	            //Suppression image temporaire
						    		
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();
				
				if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				{	
					hexTc = Integer.toHexString((int) (float) ((int) spinnerOpacityTC.getValue() * 255) / 100);
					hexAlphaTc = "ff";
					hexName = Integer.toHexString((int) (float) ((int) spinnerOpacityName.getValue() * 255) / 100);
					hexAlphaName = "ff";
				}
				else
				{
					hexTc = "0";
					hexAlphaTc = Integer.toHexString((int) (float) ((int) spinnerOpacityTC.getValue() * 255) / 100);
					hexName = "0";
					hexAlphaName = Integer.toHexString((int) (float) ((int) spinnerOpacityName.getValue() * 255) / 100);
				}
				
				 if (hexTc.length() < 2)
					 hexTc = "0" + hexTc;
				 if (hexName.length() < 2)	
					 hexName = "0" + hexName;
				 if (hexAlphaTc.length() < 2)
					 hexAlphaTc = "0" + hexAlphaTc;
				 if (hexAlphaName.length() < 2)
					 hexAlphaName = "0" + hexAlphaName;				 
				 
				 if (System.getProperty("os.name").contains("Mac"))
				 {	
					 font = "";

					 //Library
					 File[] fontFolder =  new File("/Library/Fonts").listFiles();

					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
					   if (fontFolder[i].isFile() && fontFolder[i].toString().toLowerCase().replace(" ",  "").contains(OverlayWindow.comboFont.getSelectedItem().toString().toLowerCase().replace(" ",  "")))
						   font = fontFolder[i].getAbsolutePath();	   
					 }
					 		
					 //System Library
					 if (font == "")
					 {
						 fontFolder = new File("/System/Library/Fonts").listFiles();
						 
						 for (int i = 0; i < fontFolder.length; i++)
						 {						 
						   if (fontFolder[i].isFile() && fontFolder[i].toString().toLowerCase().replace(" ",  "").contains(OverlayWindow.comboFont.getSelectedItem().toString().toLowerCase().replace(" ",  "")))
							   font = fontFolder[i].getAbsolutePath();	   
						 }
					 }
					 
					 //User Library					 
					 if (font == "")
					 {
						 fontFolder = new File(System.getProperty("user.home") + "/Library/Fonts").listFiles();
						 
						 for (int i = 0; i < fontFolder.length; i++)
						 {						 
						   if (fontFolder[i].isFile() && fontFolder[i].toString().toLowerCase().replace(" ",  "").contains(OverlayWindow.comboFont.getSelectedItem().toString().toLowerCase().replace(" ",  "")))
							   font = fontFolder[i].getAbsolutePath();	   
						 }
					 }			 		 
				 }
				 else
					 font = "font=" + OverlayWindow.comboFont.getSelectedItem().toString();

			}
			
		});
	
		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
		caseShowFileName.setLocation(lblSizeName.getX(), caseShowTimecode.getY());
		frame.add(caseShowFileName);
		
		caseShowFileName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseShowFileName.isSelected())
				{
					caseShowText.setSelected(false);
					text.setEnabled(false);
				}
				
				sliderChange(false);
			}
			
		});
		
		caseShowText.setName("caseShowText");
		caseShowText.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseShowText.setSize(caseShowText.getPreferredSize().width + 4, 23);
		caseShowText.setLocation(caseShowFileName.getX(), caseAddTimecode.getY());
		frame.add(caseShowText);
		
		caseShowText.addActionListener(new ActionListener()	{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseShowText.isSelected())
				{
					caseShowFileName.setSelected(false);
					text.setEnabled(true);
				}
				else
					text.setEnabled(false);
				
				sliderChange(false);
			}
		});
		
		text.setName("text");
		text.setEnabled(false);
		text.setLocation(caseShowText.getLocation().x + caseShowText.getWidth() + 7, caseShowText.getLocation().y + 1);
		text.setSize(frame.getWidth() - text.getX() - 13, 21);
		text.setHorizontalAlignment(SwingConstants.LEFT);
		text.setFont(new Font("SansSerif", Font.PLAIN, 12));
		frame.getContentPane().add(text);

		text.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				textTime = System.currentTimeMillis();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				
				if (changeText == null || changeText.isAlive() == false)
				{
					changeText = new Thread(new Runnable() {
						@Override
						public void run() {
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e1) {}
							} while (System.currentTimeMillis() - textTime < 500);
							
							sliderChange(false);
						}
					});
					changeText.start();
				}
			}		
			
		});
		
		
		changePositions.setBackground(new Color(80, 80, 80));
		changePositions.setOpaque(true);
		changePositions.setHorizontalAlignment(JLabel.CENTER);
		changePositions.setFont(new Font("Montserrat", Font.PLAIN, 12));
		changePositions.setSize(changePositions.getPreferredSize());
		changePositions.setLocation((btnOK.getX() + btnOK.getWidth()/2) - changePositions.getWidth()/2, btnOK.getY() - changePositions.getHeight() - 14);
		changePositions.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (changePositions.getText().equals("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>")) {
					changePositions.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;Text<br>Timecode</html>");
					
					fileName.setLocation(image.getWidth()/2 - fileName.getWidth()/2, fileName.getHeight());	
					timecode.setLocation(image.getWidth() / 2 - timecode.getWidth()/2, containerHeight - timecode.getHeight() * 2);	
				} else {
					changePositions.setText("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>");
					
					timecode.setLocation(image.getWidth()/2 - timecode.getWidth()/2, timecode.getHeight());	
					fileName.setLocation(image.getWidth() / 2 - fileName.getWidth()/2, containerHeight - fileName.getHeight() * 2);	
				}
				
				fileLocX = fileName.getLocation().x;
				fileLocY = fileName.getLocation().y;
				
				tcLocX = timecode.getLocation().x;
				tcLocY = timecode.getLocation().y;	
				
				textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * imageRatio)));
				textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * imageRatio)));  
				
				textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * imageRatio)));
				textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * imageRatio))); 
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});
		
		frame.getContentPane().add(changePositions);
		
	}

	public static void sliderChange(boolean loadImage) {		
		DecimalFormat tc = new DecimalFormat("00");			
		String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
		String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
		String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
		
		loadImage(h,m,s,loadImage);
	}
	
	public static void loadImage(String h, String m, String s, boolean loadImage) {
       
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
				
				Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());		
					
	    	  	//On récupère la taille du logo pour l'adater à l'image vidéo
				if (Utils.inputDeviceIsRunning == false)
					FFPROBE.Data(fichier);		
				
				do {
					Thread.sleep(10);
				} while (FFPROBE.isRunning);	
				
				if (caseShowTimecode.isSelected() && FFPROBE.timecode1 == "")
				{
					caseShowTimecode.setSelected(false);
					caseShowTimecode.setEnabled(false);
					caseAddTimecode.setSelected(true);
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					TC4.setEnabled(true);	
				}
								
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
				
				//Screen capture
				if (Shutter.inputDeviceIsRunning)				
					FFMPEG.run(" " +  Utils.setInputDevices() + " -vframes 1 -an -vf scale=" + containerWidth +":" + containerHeight + " -y " + '"' + Shutter.dirTemp + "preview.bmp" + '"');
				else
					FFMPEG.run(" -ss "+h+":"+m+":"+s+".0 -i " + '"' + fichier + '"' + " -vframes 1 -an -vf scale=" + containerWidth +":" + containerHeight + " -y " + '"' + Shutter.dirTemp + "preview.bmp" + '"');
	        	
		        do
		        {
		        	Thread.sleep(10);  
		        } while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false);
		        
        	}
        	
	        image.removeAll();  	        
	        
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
			
			String sp[] = FFPROBE.imageResolution.split("x");
			imageRatio = (float) Integer.parseInt(sp[0])/containerWidth;
			
			//Permet de s'adapter à la taille
			int tcPreviousSizeWidth = timecode.getWidth();
			int tcPreviousSizeHeight = timecode.getHeight();
			int namePreviousSizeWidth = fileName.getWidth();
			int namePreviousSizeHeight = fileName.getHeight();
			
			//Couleurs	
			if (fontColor != null)
			{
				 String c = Integer.toHexString(fontColor.getRGB()).substring(2);
				 hex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
			}
			else
				fontColor = new Color(255,255,255);
						
			if (backgroundColor != null)
			{
				 String c = Integer.toHexString(backgroundColor.getRGB()).substring(2);
				 hex2 = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
			}	
			else
				backgroundColor = new Color(0,0,0);
						
			fileName.removeAll();
			
			JLabel addText = new JLabel(new File(fichier).getName());
			
			if (caseShowText.isSelected() && text.getText().isEmpty() == false)
				addText.setText(text.getText());
			
			fileName.add(addText);
			
			addText.setAlignmentX(SwingConstants.CENTER);
			addText.setAlignmentY(SwingConstants.TOP);
			addText.setFont(new Font(comboFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(spinnerSizeName.getValue().toString()) / imageRatio)));
						
			fileName.setSize(addText.getPreferredSize().width, addText.getPreferredSize().height);
			
			fileLocX = fileName.getLocation().x;
			fileLocY = fileName.getLocation().y;
			if (caseShowFileName.isSelected() || caseShowText.isSelected())
			{				
				image.add(fileName);
				
				//Permet de conserver la position
				int newPosX = (int) Math.round(fileName.getWidth() - namePreviousSizeWidth) / 2;
				int newPosY = (int) Math.round(fileName.getHeight() - namePreviousSizeHeight) / 2;
				
				fileName.setLocation(fileName.getLocation().x - newPosX, fileName.getLocation().y - newPosY);
	            
	            //On enregistre la position
				fileLocX = fileName.getLocation().x;
				fileLocY = fileName.getLocation().y;
				
				textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * imageRatio)));
				textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * imageRatio)));  
			}
			else
			{
				if (changePositions.getText().equals("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>"))
					fileName.setLocation(image.getWidth() / 2 - fileName.getWidth()/2, containerHeight - fileName.getHeight() * 2);	
				else
					fileName.setLocation(image.getWidth()/2 - fileName.getWidth()/2, fileName.getHeight());	
				
				textNamePosX.setText("0");
				textNamePosY.setText("0");  
			}			
			
			timecode.removeAll();			
			
			JLabel addTimecode = new JLabel("00:00:00:00");
						
			timecode.add(addTimecode);
			
			addTimecode.setAlignmentX(SwingConstants.CENTER);
			addTimecode.setAlignmentY(SwingConstants.TOP);
			addTimecode.setFont(new Font(comboFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(spinnerSizeTC.getValue().toString()) / imageRatio)));
						
			timecode.setSize(addTimecode.getPreferredSize().width, addTimecode.getPreferredSize().height);
			
			tcLocX = timecode.getLocation().x;
			tcLocY = timecode.getLocation().y;
			if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
			{				
				if (FFPROBE.isRunning) //Contourne un bug incompréhensible
				{
					do {
						Thread.sleep(10);
					} while (FFPROBE.isRunning);
				}
				
				long tcH = 0;				
				long tcM = 0;
				long tcS = 0;
				long tcI = 0;
				
				if (caseAddTimecode.isSelected() && TC1.getText().isEmpty() == false && TC2.getText().isEmpty() == false && TC3.getText().isEmpty() == false && TC4.getText().isEmpty() == false)
				{
					tcH = Integer.valueOf(TC1.getText());
					tcM = Integer.valueOf(TC2.getText());
					tcS = Integer.valueOf(TC3.getText());
					tcI = Integer.valueOf(TC4.getText());
				}
				else if (caseShowTimecode.isSelected())
				{
					tcH = Integer.valueOf(FFPROBE.timecode1);
					tcM = Integer.valueOf(FFPROBE.timecode2);
					tcS = Integer.valueOf(FFPROBE.timecode3);
					tcI = Integer.valueOf(FFPROBE.timecode4);
				}
				
				tcH = tcH * 3600000;
				tcM = tcM * 60000;
				tcS = tcS * 1000;
				
				long offset =  positionVideo.getValue() + tcH + tcM + tcS;
				NumberFormat formatter = new DecimalFormat("00");
		        String heures = (formatter.format((offset/1000) / 3600));
		        String minutes = (formatter.format( ((offset/1000) / 60) % 60) );
		        String secondes = (formatter.format((offset/1000) % 60));				        
		        String images = (formatter.format(tcI));							
		        
		        if (caseAddTimecode.isSelected() && lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
		        {
		        	addTimecode.setText(String.format("%.0f",
		        	Integer.parseInt(heures) * 3600 * FFPROBE.currentFPS +
		        	Integer.parseInt(minutes) * 60 * FFPROBE.currentFPS +
		        	Integer.parseInt(secondes) * FFPROBE.currentFPS +
		        	Integer.parseInt(images)));
		        }
		        else
		        	addTimecode.setText(heures+":"+minutes+":"+secondes+":"+images);	
				
				image.add(timecode);
				
				//Permet de conserver la position
				int newPosX = (int) Math.round(timecode.getWidth() - tcPreviousSizeWidth) / 2;
				int newPosY = (int) Math.round(timecode.getHeight() - tcPreviousSizeHeight) / 2;

				timecode.setLocation(timecode.getLocation().x - newPosX, timecode.getLocation().y - newPosY);
	            
	            //On enregistre la position
				tcLocX = timecode.getLocation().x;
				tcLocY = timecode.getLocation().y;
				
				textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * imageRatio)));
				textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * imageRatio)));  
			}
			else
			{
				if (changePositions.getText().equals("<html>Timecode<br>&nbsp;&nbsp;&nbsp;&nbsp;Text</html>"))
					timecode.setLocation(image.getWidth()/2 - timecode.getWidth()/2, timecode.getHeight());	
				else
					timecode.setLocation(image.getWidth() / 2 - timecode.getWidth()/2, containerHeight - timecode.getHeight() * 2);						
				
				textTcPosX.setText("0");
				textTcPosY.setText("0");  
			}		
					
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
			
			JPanel leftBorder = new JPanel();
			leftBorder.setBackground(Color.BLACK);
			leftBorder.setVisible(false);
			leftBorder.setOpaque(true);	
			
			JPanel rightBorder = new JPanel();
			rightBorder.setBackground(Color.BLACK);
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
			
			fileName.repaint();
			timecode.repaint();
									
    		if (frame.isVisible() == false)
    		{
    			Utils.changeDialogVisibility(frame, false);
    		}

			Shutter.tempsRestant.setVisible(false);
	        Shutter.progressBar1.setValue(0);
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
			@SuppressWarnings("rawtypes")
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(10);
				} while (OverlayWindow.frame == null && OverlayWindow.frame.isVisible() == false);
				
				
				File file = new File(Shutter.dirTemp + "preview.bmp");
				
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
						
						for (Component p : OverlayWindow.frame.getContentPane().getComponents())
						{
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{
								if (p instanceof JPanel)
								{
									do {
										Thread.sleep(10);
									} while (file.exists() == false);
									
									//Value
									String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
									String s2[] = s[1].split(",");
									((JPanel) p).setBackground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));
									
									if (p.getName().equals("panelColor"))
										fontColor = panelColor.getBackground();
									else if (p.getName().equals("panelColor2"))
										backgroundColor = panelColor2.getBackground();
									
									sliderChange(false);
								}
								
								if (p instanceof JRadioButton)
								{
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JRadioButton) p).isSelected() == false)
											((JRadioButton) p).doClick();
									}
									else
									{
										if (((JRadioButton) p).isSelected())
											((JRadioButton) p).doClick();
									}
																		
									//State
									((JRadioButton) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JRadioButton) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
								else if (p instanceof JLabel)
								{
									do {
										Thread.sleep(10);
									} while (file.exists() == false);
									
									//Value
									((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));																			
								}
								else if (p instanceof JComboBox)
								{
									do {
										Thread.sleep(10);
									} while (file.exists() == false);
									
									//Value
									((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
								}
								else if (p instanceof JTextField)
								{											
									do {
										Thread.sleep(10);
									} while (file.exists() == false);
									
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
									//Position des éléments
									if (p.getName().equals("textNamePosX") && textNamePosX.getText().length() > 0)
										fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), fileName.getLocation().y);	
									
									if (p.getName().equals("textNamePosY") && textNamePosY.getText().length() > 0)
										fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
									
									if (p.getName().equals("textTcPosY") && textTcPosY.getText().length() > 0)
										timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
									
									if (p.getName().equals("textTcPosX") && textTcPosX.getText().length() > 0)
										timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), timecode.getLocation().y);
								}
								else if (p instanceof JSpinner)
								{									
									do {
										Thread.sleep(10);
									} while (file.exists() == false);
									
									//Value
									((JSpinner) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																		
									//State
									((JSpinner) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JSpinner) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}
						}
					}
				}			
				
				sliderChange(false);
				
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private class ComboRendererOverlay extends BasicComboBoxRenderer {
	
		private static final long serialVersionUID = 1L;
		private JComboBox comboBox;
		final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		private int row;
	
		private ComboRendererOverlay(JComboBox fontsBox) {
			comboBox = fontsBox;
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (list.getModel().getSize() > 0) {
				 final Object comp = comboBox.getUI().getAccessibleChild(comboBox, 0);
			}
			final JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);
			final Object fntObj = value;
			final String fontFamilyName = (String) fntObj;
			
			setFont(new Font(fontFamilyName, Font.PLAIN, 16));	            
			
			return this;
		}
	}
}