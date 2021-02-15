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
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
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
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

public class SubtitlesWindow {
	public static JDialog frame;
	private static JPanel image = new JPanel();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	private JButton btnOK;
    public static int ImageWidth;
    public static int ImageHeight;
    public static int finalWidth;
    public static int finalHeight;
    public static int containerWidth =  640;
    public static int containerHeight = 360;
	private static JPanel panelColor = new JPanel();
	private static JPanel panelColor2 = new JPanel();
	public static Color fontColor;
	public static Color backgroundColor;
	public static String hex = "FFFFFF";
	public static String hex2 = "000000";
	public static String alpha = "7F";
	public static JComboBox<String> comboFont;		
	public static JButton btnI;
	public static JButton btnG;
	public static JSlider positionVideo = new JSlider();
	public static JSpinner spinnerSize;
	public static JSpinner spinnerOpacity;
	public static JSpinner spinnerSubtitle = new JSpinner();
	public static JSpinner spinnerSubtitlesPosition = new JSpinner(new SpinnerNumberModel(0,null,null,-1));
	public static JTextField textWidth = new JTextField();
	public static JLabel lblBackground; 
	public static String subtitlesFile;

	public SubtitlesWindow() {	
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameAddSubtitles"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(665, 500);
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
		
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private static class MouseSubSize {
		static int mouseX;
		static int offsetX;
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
					//Suppression image temporaire							    		
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();
					
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);
		            Shutter.caseSubtitles.setSelected(false);
		            
		            Utils.changeDialogVisibility(frame, true);
	            	Shutter.frame.setOpacity(1.0f);
	            	Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	            	
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("frameAddSubtitles"));
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

	private void boutons()	{
		
		JLabel lblFont = new JLabel(Shutter.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblFont.setBounds(12, 435, lblFont.getPreferredSize().width, 16);
		frame.getContentPane().add(lblFont);
		
		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		comboFont = new JComboBox<String>(Fonts);
		comboFont.setName("comboFont");
		comboFont.setSelectedItem("Arial");
		comboFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboFont.setRenderer(new ComboRenderer(comboFont));
		comboFont.setEditable(true);
		comboFont.setBounds(65, 431, 120, 22);
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
			
		btnG = new JButton(Shutter.language.getProperty("btnG"));
    	btnG.setFont(new Font("Montserrat", Font.PLAIN, 13));
    	btnG.setForeground(Color.BLACK);
    	btnG.setName("btnG");
    	btnG.setBounds(comboFont.getLocation().x + comboFont.getWidth() + 4, 431, 22, 22);    	
    	frame.getContentPane().add(btnG);
    	
    	btnG.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnG.getForeground() == Color.BLACK)
					btnG.setForeground(Utils.themeColor);
				else					
					btnG.setForeground(Color.BLACK);
				
				sliderChange(false);
			}
    		
    	});

		btnI = new JButton("I");
    	btnI.setFont(new Font("Courier New", Font.ITALIC, 13));
    	btnI.setForeground(Color.BLACK);
    	btnI.setName("btnI");
    	btnI.setBounds(btnG.getLocation().x + btnG.getWidth() + 2, 431, 22, 22);    	
    	frame.getContentPane().add(btnI);
    	
    	btnI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnI.getForeground() == Color.BLACK)
					btnI.setForeground(Utils.themeColor);
				else
					btnI.setForeground(Color.BLACK);
				
				sliderChange(false);
			}
    		
    	});
		
		JLabel lblSubtitlesPosition = new JLabel(Shutter.language.getProperty("lblSubtitlesPosition"));
		lblSubtitlesPosition.setAlignmentX(SwingConstants.RIGHT);
		lblSubtitlesPosition.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblSubtitlesPosition.setBounds(btnI.getLocation().x + btnI.getWidth() + 6, 434, lblSubtitlesPosition.getPreferredSize().width, 16);
		frame.getContentPane().add(lblSubtitlesPosition);
		
		spinnerSubtitlesPosition.setName("spinnerSubtitlesPosition");
		spinnerSubtitlesPosition.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerSubtitlesPosition.setValue(0);
		spinnerSubtitlesPosition.setBounds(lblSubtitlesPosition.getLocation().x + lblSubtitlesPosition.getWidth() + 5, lblSubtitlesPosition.getLocation().y - 3, 58, 22);
		frame.getContentPane().add(spinnerSubtitlesPosition);
		
		spinnerSubtitlesPosition.addChangeListener(new ChangeListener() {

			String s[] = FFPROBE.imageResolution.split("x");
			int h = Integer.parseInt(s[1]);
			int alphaHeight = h;
			
			@Override
			public void stateChanged(ChangeEvent arg0) {				
				int v = Integer.parseInt(spinnerSubtitlesPosition.getValue().toString());
				int sz = Integer.parseInt(spinnerSize.getValue().toString());
				
				spinnerSize.setValue(Math.round((float)sz*((float)alphaHeight/(h+v))));
				changeSubtitle();		
				
				alphaHeight = (int) (h + v);
			}
			
		});
			
		JLabel lblWidth = new JLabel(Shutter.language.getProperty("lblWidth"));
		lblWidth.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblWidth.setAlignmentX(SwingConstants.RIGHT);
		lblWidth.setBounds(spinnerSubtitlesPosition.getLocation().x + spinnerSubtitlesPosition.getWidth() + 9, 434, lblWidth.getPreferredSize().width, 16);
		frame.getContentPane().add(lblWidth);

		textWidth.setText(String.valueOf(ImageWidth));
		textWidth.setName("textWidth");
		textWidth.setBounds(lblWidth.getX() + lblWidth.getWidth() + 5, lblWidth.getLocation().y, 34, 16);
		textWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textWidth.setFont(new Font("FreeSans", Font.PLAIN, 12));
		frame.getContentPane().add(textWidth);
		
		textWidth.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textWidth.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					sliderChange(false);
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
		
		textWidth.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseSubSize.mouseX = e.getX();
				MouseSubSize.offsetX = Integer.parseInt(textWidth.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				sliderChange(false);
			}
			
		});
		
		textWidth.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textWidth.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					textWidth.setText(String.valueOf(MouseSubSize.offsetX + (e.getX() - MouseSubSize.mouseX)));
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel lblsubtitleNumber = new JLabel(Shutter.language.getProperty("lblsubtitleNumber"));
		lblsubtitleNumber.setAlignmentX(SwingConstants.RIGHT);
		lblsubtitleNumber.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblsubtitleNumber.setBounds(textWidth.getLocation().x + textWidth.getWidth() + 9, 434, lblsubtitleNumber.getPreferredSize().width, 16);
		frame.getContentPane().add(lblsubtitleNumber);
		
		spinnerSubtitle.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerSubtitle.setValue(1);
		spinnerSubtitle.setBounds(lblsubtitleNumber.getLocation().x + lblsubtitleNumber.getWidth() + 5, lblsubtitleNumber.getLocation().y - 3, 50, 22);
		frame.getContentPane().add(spinnerSubtitle);
		
		spinnerSubtitle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if ((int) spinnerSubtitle.getValue() < 1)
					spinnerSubtitle.setValue(1);
				else
					changeSubtitle();			
			}
			
		});
			
		positionVideo.setFont(new Font("FreeSans", Font.PLAIN, 11));
		positionVideo.setBounds(spinnerSubtitle.getLocation().x + spinnerSubtitle.getWidth() + 9, comboFont.getLocation().y, frame.getWidth() - (spinnerSubtitle.getLocation().x + spinnerSubtitle.getWidth() + 7) - 12, 22);	
		frame.getContentPane().add(positionVideo);
		
		positionVideo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {			
				sliderChange(false);				
			}
			
		});
			
		JLabel lblColor = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblColor.setBounds(12, 470, lblColor.getPreferredSize().width + 4, 16);
		frame.getContentPane().add(lblColor);
		
		panelColor = new JPanel();
		panelColor.setName("panelColor");
		panelColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelColor.setBackground(Color.WHITE);
		panelColor.setBounds(lblColor.getLocation().x + 62, lblColor.getLocation().y - 7, 41, 29);
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

		JLabel lblSize = new JLabel(Shutter.language.getProperty("lblSize"));
		lblSize.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblSize.setAlignmentX(SwingConstants.RIGHT);
		lblSize.setBounds(lblColor.getLocation().x + 113, lblColor.getLocation().y, lblSize.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblSize);
		
		spinnerSize = new JSpinner(new SpinnerNumberModel(18, 1, 999, 1));
		spinnerSize.setName("spinnerSize");
		spinnerSize.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerSize.setBounds(panelColor.getLocation().x + 103, lblColor.getLocation().y - 3, 50, 22);
		frame.getContentPane().add(spinnerSize);
		
		spinnerSize.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				sliderChange(false);				
			}
			
		});
		
		JLabel lblColor2 = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor2.setAlignmentX(SwingConstants.RIGHT);
		lblColor2.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblColor2.setBounds(spinnerSize.getLocation().x + spinnerSize.getWidth() + 92, 470, lblColor2.getPreferredSize().width + 4, 16);
		frame.getContentPane().add(lblColor2);
		
		panelColor2 = new JPanel();
		panelColor2.setName("panelColor2");
		panelColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelColor2.setBackground(Color.BLACK);
		panelColor2.setBounds(lblColor2.getLocation().x + 62, lblColor2.getLocation().y - 7, 41, 29);
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
			
		JLabel lblOpacity = new JLabel(Shutter.language.getProperty("lblOpacity"));
		lblOpacity.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblOpacity.setAlignmentX(SwingConstants.RIGHT);
		lblOpacity.setBounds(lblColor2.getLocation().x + 113, lblColor2.getLocation().y, lblOpacity.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblOpacity);
		
		spinnerOpacity = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
		spinnerOpacity.setName("spinnerOpacity");
		spinnerOpacity.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerOpacity.setBounds(lblOpacity.getLocation().x + lblOpacity.getWidth() + 11, lblColor2.getLocation().y - 3, 54, 22);
		frame.getContentPane().add(spinnerOpacity);
		
		spinnerOpacity.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				sliderChange(false);				
			}
			
		});
		
		lblBackground = new JLabel(Shutter.language.getProperty("lblBackgroundOff"));
		lblBackground.setName("lblBackground");
		lblBackground.setBackground(new Color(80, 80, 80));
		lblBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblBackground.setOpaque(true);
		lblBackground.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblBackground.setBounds(spinnerSize.getLocation().x + spinnerSize.getWidth() + 11, spinnerSize.getLocation().y + 3, 70, 16);
		frame.getContentPane().add(lblBackground);
		
		lblBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOff")))
				{
					lblBackground.setText(Shutter.language.getProperty("lblBackgroundOn"));
					spinnerOpacity.setValue(50);
				}
				else
				{
					lblBackground.setText(Shutter.language.getProperty("lblBackgroundOff"));
					spinnerOpacity.setValue(100);
				}
				
				//changeSubtitle();				
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
		
		btnOK = new JButton("OK");
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setBounds(spinnerOpacity.getLocation().x + spinnerOpacity.getWidth() + 13, 468, frame.getWidth() - (spinnerOpacity.getLocation().x + spinnerOpacity.getWidth() + 7) - 20, 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
	            
				//Suppression image temporaire						    		
				File file = new File(Shutter.dirTemp + "preview.bmp");
				if (file.exists()) file.delete();
				
				try {
		            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(subtitlesFile.toString()),  StandardCharsets.UTF_8);
		            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Shutter.subtitlesFile.toString()),  StandardCharsets.UTF_8);

		            String line;
		            while((line = bufferedReader.readLine()) != null) {
		            	if (line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
		            	{
		            		//Fichier avec BOM
		            		if (line.contains("1") && line.length() == 2)
		            			bufferedWriter.write("1");
		            		else
		            		{
		            			if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
		            				bufferedWriter.write(" \\h" + line + " \\h");
		            			else
		            				bufferedWriter.write(line);
		            		}
		            		
		            		bufferedWriter.newLine();
		            	} 
		            	else
		            	{
		            		bufferedWriter.write(line);
		            		bufferedWriter.newLine();
		            	}

		            }   

		            bufferedReader.close();  
		            bufferedWriter.close();
				} catch (Exception e1) {}
					            
				Utils.changeDialogVisibility(frame, true);
				
            	Shutter.frame.setOpacity(1.0f);
            	Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            	
            	if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionSubtitles")))
            		JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("chooseFunction"), Shutter.language.getProperty("subtitles"), JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
			
		sliderChange(true);
	}

	public static void changeSubtitle() {	
		
		try {
			
            BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(subtitlesFile.toString()),  StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Shutter.subtitlesFile.toString()),  StandardCharsets.UTF_8);

            String line;
            boolean start = false;
            while((line = bufferedReader.readLine()) != null) {
            	            	
            	if (line.matches("[0-9]+") && line.equals(spinnerSubtitle.getValue().toString()))
            	{
            		bufferedWriter.write(line);
            		bufferedWriter.newLine();
            		start = true;
            	}
            		
            	if (start)
            	{
            		if (line.contains("-->"))
            		{
	            		bufferedWriter.write("00:00:00,000 --> 10:00:00,000");
	            		bufferedWriter.newLine();
	            		
	            		String split[] = line.split("-->");				
	            		String inTimecode[] = split[0].replace(",", ":").replace(" ","").split(":");
	            		String outTimecode[] = split[1].replace(",", ":").replace(" ","").split(":");
	            		
	    				int inH = Integer.parseInt(inTimecode[0]) * 3600000;
	    				int inM = Integer.parseInt(inTimecode[1]) * 60000;
	    				int inS = Integer.parseInt(inTimecode[2]) * 1000;
	    				int inF = Integer.parseInt(inTimecode[3]);
	            		
	    				int outH = Integer.parseInt(outTimecode[0]) * 3600000;
	    				int outM = Integer.parseInt(outTimecode[1]) * 60000;
	    				int outS = Integer.parseInt(outTimecode[2]) * 1000;
	    				int outF = Integer.parseInt(outTimecode[3]);
	    				
	    				positionVideo.setMinimum(inH+inM+inS+inF);
	    				positionVideo.setMaximum(outH+outM+outS+outF);
	            		positionVideo.setValue(positionVideo.getMinimum());		
            		}
            		else if (line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
            		{
            			if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
        					bufferedWriter.write(" \\h" + line + " \\h");
	        			else
	        				bufferedWriter.write(line);
            			
                		bufferedWriter.newLine();
            		}
            		
            		if (line.isEmpty())
            			break;
            	}
            }   

            bufferedReader.close();  
            bufferedWriter.close();
            
            if (start)
            	sliderChange(false);		
            else if ((int) spinnerSubtitle.getValue() > 1)
            	spinnerSubtitle.setValue((int) spinnerSubtitle.getValue() - 1);	

		} catch (Exception e) {}
	}
	
	public static void sliderChange(boolean analyze) {
		Thread runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				DecimalFormat tc = new DecimalFormat("00");	
				NumberFormat toMs = new DecimalFormat("000");
				String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
				String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
				String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
				String f = String.valueOf(toMs.format(positionVideo.getValue() % 1000));
								
				loadImage(h,m,s,f,analyze);
			}
		});
		runProcess.start();	
	}
	
	public static void loadImage(String h, String m, String s, String f, boolean analyze) {
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run() {
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
	
					File file = new File(Shutter.dirTemp + "preview.bmp");
					if (file.exists()) file.delete();
					
					Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
	
					//Couleurs	
					if (fontColor != null)
					{
						 String c = Integer.toHexString(fontColor.getRGB()).substring(2);
						 hex = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
					}
					
					if (backgroundColor != null)
					{
						 String c = Integer.toHexString(backgroundColor.getRGB()).substring(2);
						 hex2 = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
					}		
					
					int o = (int) (255 - (float) ((int) spinnerOpacity.getValue() * 255) / 100);
					alpha = Integer.toHexString(o);
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
					//Fond sous-titres
					String background = "" ;
					if (lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
						background = ",BorderStyle=4,BackColour=&H" + alpha + hex2 + "&,Outline=0";
					else
						background = ",OutlineColour=&H" + alpha + hex2 + "&";
						
					//Bold
					if (btnG.getForeground() != Color.BLACK)
						background += ",Bold=1";
					
					//Italic
					if (btnI.getForeground() != Color.BLACK)
						background += ",Italic=1";
					
					String size[] = FFPROBE.imageResolution.split("x");
					
		        	
		        	if (analyze)
		        	{	
			    	  	//On récupère la taille du logo pour l'adater à l'image vidéo
				  		FFPROBE.Data(fichier);		
						do {
							Thread.sleep(100);
						} while (FFPROBE.isRunning);
		        	}
		        	
		        	//Rognage
		        	String crop = "";
		        	String pad = "";
		        	if (Shutter.caseRognage.isSelected())
					{
		        		if (Shutter.lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
		        		{
							String c[] = Shutter.comboH264Taille.getSelectedItem().toString().split("x");
							ImageWidth = Integer.parseInt(c[0]); 
							ImageHeight = Integer.parseInt(c[1]); 
														
							size[0] = String.valueOf(FFPROBE.cropHeight);
							size[1] = String.valueOf(FFPROBE.cropWidth);	
		        		}
		        							
						crop = "crop=" + FFPROBE.cropHeight + ":" + FFPROBE.cropWidth + ":" + FFPROBE.cropPixelsWidth + ":" + FFPROBE.cropPixelsHeight + "[c];[c]";						
						
						if (Shutter.lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
			    			pad = "pad=" + FFPROBE.imageResolution.replace("x", ":") + ":(ow-iw)*0.5:(oh-ih)*0.5[p];[p]";
					}
		        			        	
		        	//Largeur
		        	String i[] = FFPROBE.imageResolution.split("x");	
		        	
		        	//Permet d'appliquer les paramètres lorsque le ratio de rognage est inf. à celui d'origine
		        	boolean infRatio = false;
		        	
		        	if (Shutter.caseRognage.isSelected() && Shutter.lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
		        	{	
		        		i = Shutter.comboH264Taille.getSelectedItem().toString().split("x");
		        		infRatio = true;
		        	}
		        	
		        	if (analyze)
		        		textWidth.setText(i[0]);	
		        			        			        	
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
					
		          	FFMPEG.run(" -ss "+h+":"+m+":"+s+"."+f+" -i " + '"' + fichier + '"' + " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + textWidth.getText() + ":" + size[1] + "+" + spinnerSubtitlesPosition.getValue() + ",subtitles='" + Shutter.subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + comboFont.getSelectedItem().toString() + ",FontSize=" + spinnerSize.getValue() + ",PrimaryColour=&H" + hex + "&" + background + "'" + '"' + " -vframes 1 -filter_complex [0:v]" + crop + pad + "[1:v]overlay=x=" + ((int) (Integer.parseInt(i[0]) - Integer.parseInt(textWidth.getText()))/2) + ",scale=" + containerWidth + ":" + containerHeight + " -an -y " + '"' + Shutter.dirTemp + "preview.bmp" + '"');
				      
		            do
		            {
		            	Thread.sleep(100);  
		            } while (new File(Shutter.dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false);
	     
		            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		            
		           	if (FFMPEG.error)
		      	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
		           	
		           	image.removeAll();  
		           	
		    		Image imageBMP = ImageIO.read(new File(Shutter.dirTemp + "preview.bmp"));
		            ImageIcon imageIcon = new ImageIcon(imageBMP);
		    		JLabel newImage = new JLabel(imageIcon);
		            imageIcon.getImage().flush();
		    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
						    		
		    		image.setLocation(12 + ((640 - containerWidth) / 2), 58 + (int) ((float)(360 - containerHeight) / 2));
		    		image.setSize(containerWidth,containerHeight);
		    		
		    		//Contourne un bug
		            imageIcon = new ImageIcon(imageBMP);
		    		newImage = new JLabel(imageIcon);
		    		newImage.setSize(containerWidth,containerHeight);
		    							
		    		//Border
		    		JPanel borderWidth = new JPanel();

		    		int outputHeight = Integer.parseInt(i[1]);
		    		
    				if (infRatio)
    				{
    		        	String out[] = Shutter.comboH264Taille.getSelectedItem().toString().split("x");
    		        	outputHeight = Integer.parseInt(out[1]);
    				}
    		
		    		borderWidth.setSize((int) ((float) Integer.parseInt(textWidth.getText()) / ( (float) outputHeight / newImage.getHeight())),
		    		(int) (newImage.getHeight() + (float) Integer.parseInt(spinnerSubtitlesPosition.getValue().toString()) / ( (float) outputHeight / newImage.getHeight())) );

		    		borderWidth.setLocation(newImage.getX() + (newImage.getWidth() - borderWidth.getWidth()) / 2, newImage.getY());
		        	
		    		borderWidth.setBorder(BorderFactory.createDashedBorder(Color.WHITE, 4, 4));	
		    		borderWidth.setOpaque(true);
		    		borderWidth.setBackground(new Color(0,0,0,0));	
		    		
		    		image.add(borderWidth);
		    		
		    		image.add(newImage);
		    		image.repaint(); 
		    		
		    		if (frame.isVisible() ==  false)
		    			Utils.changeDialogVisibility(frame, false);
	
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);
			        }
				    catch (Exception e)
				    {
			 	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
				    }
			        finally {
			        	Shutter.tempsRestant.setVisible(false);
			        	Shutter.enableAll();         
		            	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
		    				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
		    			else
		    				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
			        }
				
			}
		});
		thread.start();
	}

	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@SuppressWarnings("rawtypes")
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(100);
				} while (frame == null && frame.isVisible() == false);
				
				
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
						
						for (Component p : frame.getContentPane().getComponents())
						{
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{
								if (p instanceof JPanel)
								{
									do {
										Thread.sleep(100);
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
								
								if (p instanceof JButton)
								{
									do {
										Thread.sleep(100);
									} while (file.exists() == false);
									
									//Value
									String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
									String s2[] = s[1].split(",");
									((JButton) p).setForeground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));	
								}
								else if (p instanceof JLabel)
								{
									do {
										Thread.sleep(100);
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
										Thread.sleep(100);
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
										Thread.sleep(100);
									} while (file.exists() == false);
									
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
								else if (p instanceof JSpinner)
								{
									do {
										Thread.sleep(100);
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
	private class ComboRenderer extends BasicComboBoxRenderer {

        private static final long serialVersionUID = 1L;
		private JComboBox comboBox;
        final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private int row;

		private ComboRenderer(JComboBox fontsBox) {
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