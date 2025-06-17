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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;

import library.FFMPEG;
import library.YOUTUBEDL;
import settings.FunctionUtils;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

/** ATTENTION pour windows les commandes sont sous forme de tableau ["a","b"], sous mac en forme de String... **/

public class VideoWeb {
	public static JDialog frame;
	private static int complete;
		
	/*
	 * Composants
	 */
	private JLabel quit;
	private JLabel help;
	private JPanel topPanel;
	private JLabel topImage;	
	private JPanel grpURL;
	private JLabel lblURL;
	private JTextArea textURL;
	private JTextField textVideoPass;
	private JTextField textUser;
	private JPasswordField textPass;
	private JLabel lblQualit;
	private JButton btnOK;
	private JCheckBox caseMP3;
	private JCheckBox caseWAV;
	private JCheckBox caseAuto;
	public static JCheckBox caseMetadata;
	public static JCheckBox caseTimecode;
	public static JTextField textTimecodeIn;
	public static JTextField textTimecodeOut;
	public static JCheckBox caseCookies;
	private JComboBox<String> comboCookies;
	private JCheckBox caseUser;
	private JCheckBox casePass;
	private JCheckBox caseVideoPass;
	private JComboBox<String> comboFormats;
	
	private static int MousePositionX;
	private static int MousePositionY;

	public VideoWeb() {	
		
		frame = new JDialog();
		frame.getContentPane().setBackground(Utils.bg32);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameVideoWeb"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(420, 338 + 22);
		frame.setResizable(false);
		frame.setModal(true);
		frame.setAlwaysOnTop(true);
				
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45,45,45)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);
		}
				
		if (System.getProperty("os.name").contains("Mac"))
		{
			Thread download = new Thread(new Runnable() {

				@Override
				public void run() {
					
					String PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,PathToYOUTUBEDL.length()-1);
					PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", " ")  + "/Library/yt-dlp_macos";
					
					if (new File(PathToYOUTUBEDL).exists() == false)
					{							
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));						
						YOUTUBEDL.HTTPDownload("https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_macos", PathToYOUTUBEDL);
						
						if (new File(PathToYOUTUBEDL).exists() == false)
						{
							Utils.changeDialogVisibility(frame, true);
						}
						
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}					
				}  
			});
			download.start();
		}
						
		topPanel();
		grpURL();
					
		//Right_to_left
		if (Shutter.getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
		{
			//Frame
			for (Component c : frame.getContentPane().getComponents())
			{
				if (c instanceof JPanel)
				{						
					for (Component p : ((JPanel) c).getComponents())
					{
						if (p instanceof JCheckBox)
						{
							p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						}
					}
				}
			}		
		}
		
		Utils.changeDialogVisibility(frame, false);	
		
	}
			
	private void topPanel() {	
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBackground(Utils.bg32);
		topPanel.setBounds(0, 0, 420, 28);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		
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
					if (YOUTUBEDL.runProcess != null)
					{
						if (YOUTUBEDL.runProcess.isAlive())
						{
							if (System.getProperty("os.name").contains("Windows"))
							{						
								try {
									@SuppressWarnings("unused")
									Process processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", "yt-dlp.exe").start();
								} catch (Exception e1) {}
							}
							else
							{
								YOUTUBEDL.process.destroy();
							}
						}
					}
					
					Shutter.lblCurrentEncoding.setForeground(Color.RED);
					Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("processCancelled"));
					Shutter.progressBar1.setValue(0);		        
					
					Utils.changeDialogVisibility(frame, true);
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
	
		help = new JLabel(new FlatSVGIcon("contents/help.svg", 15, 15));
		help.setHorizontalAlignment(SwingConstants.CENTER);
		help.setBounds(quit.getLocation().x - 20, 4, 15, 15);
		topPanel.add(help);
		
		help.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				help.setIcon(new FlatSVGIcon("contents/help_pressed.svg", 15, 15));
				accept = true;
			}

			@SuppressWarnings("deprecation")
			@Override
			public void mouseReleased(MouseEvent e) {			
				if (accept)
				{
					try {
					    Desktop.getDesktop().browse(new URL( "https://rg3.github.io/youtube-dl/supportedsites.html").toURI());
					} catch (Exception error) {}	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				help.setIcon(new FlatSVGIcon("contents/help_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				help.setIcon(new FlatSVGIcon("contents/help.svg", 15, 15));
				accept = false;
			}
		
	});
		
		JLabel title = new JLabel(Shutter.language.getProperty("panelWebVideo"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45,45,45)));	
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);
		
		frame.getContentPane().add(topPanel);
		
		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;					
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	@SuppressWarnings("serial")
	private void grpURL() {
		
		grpURL = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D) g.create();
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.setColor(Utils.c25);
		        g2.fillRoundRect(2, 9, getWidth() - 4, getHeight() - 11, 10, 10);      
		        g2.dispose();
		        super.paintComponent(g);
		    }
		};
		grpURL.setLayout(null);
		grpURL.setLocation(6, 28);
		grpURL.setSize(408, frame.getHeight() - 34);
		grpURL.setBackground(Utils.c30);
		grpURL.setOpaque(false);
		grpURL.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), Utils.c42, 1, 10), Shutter.language.getProperty("videoUrl") + " ", 0, 0, new Font(Shutter.boldFont, Font.PLAIN, 13), new Color(235,235,240)));
		
		lblURL = new JLabel(Shutter.language.getProperty("lblURL"));
		lblURL.setHorizontalAlignment(SwingConstants.RIGHT);
		lblURL.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		lblURL.setBounds(26, 25, 35, 16);			
		grpURL.add(lblURL);
		
		textURL = new JTextArea();
		textURL.setForeground(Color.LIGHT_GRAY);
		textURL.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textURL.setText(Shutter.language.getProperty("textURL"));
		textURL.setBounds(66, 22, grpURL.getSize().width - (lblURL.getLocation().x + lblURL.getSize().width) - 17, 80);
				
        final JPopupMenu menu = new JPopupMenu();
        JMenuItem coller = new JMenuItem(Shutter.language.getProperty("MenuItemPaste"));
        coller.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				PasteFromClipBoard(true);
			}
        	
        });
        menu.add(coller);
        
        textURL.setComponentPopupMenu(menu);		
      
		textURL.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				
				if (textURL.getText().equals("") || textURL.getText().equals(Shutter.language.getProperty("textURL")))
				{
					btnOK.setEnabled(false);
					if (caseAuto.isSelected() == false)
						caseAuto.doClick();
					caseAuto.setEnabled(false);
				}
				else
				{
					textURL.setForeground(Color.WHITE);
					textURL.setFont(new Font("SansSerif", Font.PLAIN, 12));
		       		btnOK.setEnabled(true);
					
		       		if (textURL.getLineCount() == 1)
					{
						caseAuto.setEnabled(true);
					}
					else
					{
						caseAuto.setEnabled(false);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
					//CTRL + V coller
			        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
			        	PasteFromClipBoard(false);
			           
			        //CTRL + A
			        if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
			        	textURL.selectAll();
					
				}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});
        
		JScrollPane scrollBar = new JScrollPane(textURL);
		scrollBar.setBounds(textURL.getBounds());
		grpURL.add(scrollBar, BorderLayout.CENTER);
								
		caseAuto = new JCheckBox("Auto");
		caseAuto.setSelected(true);
		caseAuto.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseAuto.setBounds(66, 111, caseAuto.getPreferredSize().width, 16);	
		caseAuto.setEnabled(false);
		grpURL.add(caseAuto);
				
		caseAuto.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				comboFormats.removeAllItems();
				
				if (caseAuto.isSelected())
				{
					if (YOUTUBEDL.runProcess != null)
					{
						if (YOUTUBEDL.runProcess.isAlive())
						{
							if (System.getProperty("os.name").contains("Windows"))
							{						
								try {
									@SuppressWarnings("unused")
									Process processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", "yt-dlp.exe").start();
								} catch (Exception e1) {}
							}
							else
							{
								YOUTUBEDL.process.destroy();
							}
						}
						
						Shutter.lblCurrentEncoding.setForeground(Color.RED);
						Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("processCancelled"));
						Shutter.progressBar1.setValue(0);	
					}
															
					comboFormats.addItem("default");
					comboFormats.addItem("bestvideo+bestaudio");					
					comboFormats.addItem("up to 4K");
					comboFormats.addItem("up to 1440p");
					comboFormats.addItem("up to 1080p");
					comboFormats.addItem("up to 720p");
					comboFormats.addItem("up to 480p");
					comboFormats.addItem("up to 360p");
					comboFormats.addItem("up to 240p");
					comboFormats.addItem("bestvideo");
					comboFormats.addItem("bestaudio");
					
					btnOK.setEnabled(true);
				}
				else
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
					Thread getFormats = new Thread(new Runnable() {

						@Override
						public void run() {
							
							btnOK.setEnabled(false);
							comboFormats.addItem(Shutter.language.getProperty("getAvailableFormats") + "...");
							
							YOUTUBEDL.getAvailableFormats(textURL.getText(), options());
							
							do {
								try {
									Thread.sleep(100);
								} catch (Exception er){}
							} while (YOUTUBEDL.runProcess.isAlive() && Shutter.cancelled == false);
							
							if (caseAuto.isSelected() == false && frame.isVisible())
							{
								if (YOUTUBEDL.error == false)
								{
									comboFormats.removeAllItems();
									
									String allFormats = YOUTUBEDL.formatsOutput.substring(YOUTUBEDL.formatsOutput.lastIndexOf(":") + 2).replace("null", "").replace("DASH audio", "").replace("DASH video", "");
									
									for (String format : allFormats.split(System.lineSeparator()))
									{
										if (format.contains("format") == false)
											comboFormats.addItem(format);
									}
								}
								else
								{
									if (caseAuto.isSelected() == false)
										caseAuto.doClick();
									
									JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("invalidUrl"), Shutter.language.getProperty("downloadError"), JOptionPane.ERROR_MESSAGE);
								}
							}
												
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							btnOK.setEnabled(true);
						}
						
					});
					getFormats.start();
				}
			}
			
		});
			
		caseMetadata = new JCheckBox(Shutter.language.getProperty("casePreserveMetadata"));
		caseMetadata.setName("caseMetadata");
		if (Settings.videoWebCaseMetadata)
		{
			caseMetadata.setSelected(true);
		}
		else
		{
			caseMetadata.setSelected(false);
		}
		caseMetadata.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseMetadata.setBounds(66, caseAuto.getY() + 22, caseMetadata.getPreferredSize().width, 16);	
		grpURL.add(caseMetadata);
		
		caseTimecode = new JCheckBox(Shutter.language.getProperty("caseSetTimecode"));
		caseTimecode.setName("caseTimecode");
		caseTimecode.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseTimecode.setBounds(66, caseMetadata.getY() + 22, caseTimecode.getPreferredSize().width, 16);	
		grpURL.add(caseTimecode);
		
		caseTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
					
				if (caseTimecode.isSelected())
				{
					textTimecodeIn.setEnabled(true);
					textTimecodeOut.setEnabled(true);
				}
				else
				{
					textTimecodeIn.setEnabled(false);
					textTimecodeOut.setEnabled(false);
				}
			}
			
		});
 		
		textTimecodeIn = new JTextField("00:00:00");
		textTimecodeIn.setHorizontalAlignment(SwingConstants.CENTER);
		textTimecodeIn.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textTimecodeIn.setBounds(caseTimecode.getLocation().x + caseTimecode.getWidth() + 4, caseTimecode.getY() - 4, 70, 21);
		textTimecodeIn.setEnabled(false);	
		grpURL.add(textTimecodeIn);
		
		textTimecodeIn.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e)
			{
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != ':' && caracter != '￿')
				{
					e.consume();
				}
				else if (textTimecodeIn.getText().length() >= 8)
				{
					textTimecodeIn.setText("");
				}
				else if ((textTimecodeIn.getText().length() == 2 || textTimecodeIn.getText().length() == 5) && caracter != ':')
				{
					textTimecodeIn.setText(textTimecodeIn.getText() + ":");
				}
			}

		});
		
		textTimecodeOut = new JTextField("00:00:00");
		textTimecodeOut.setHorizontalAlignment(SwingConstants.CENTER);
		textTimecodeOut.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textTimecodeOut.setBounds(textTimecodeIn.getLocation().x + textTimecodeIn.getWidth() + 4, textTimecodeIn.getY(), textTimecodeIn.getWidth(), 21);
		textTimecodeOut.setEnabled(false);	
		grpURL.add(textTimecodeOut);
		
		textTimecodeOut.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e)
			{
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != ':' && caracter != '￿')
				{
					e.consume();
				}
				else if (textTimecodeOut.getText().length() >= 8)
				{
					textTimecodeOut.setText("");
				}
				else if ((textTimecodeOut.getText().length() == 2 || textTimecodeOut.getText().length() == 5) && caracter != ':')
				{
					textTimecodeOut.setText(textTimecodeOut.getText() + ":");
				}
			}

		});
		
		caseCookies = new JCheckBox(Shutter.language.getProperty("caseCookies"));
		caseCookies.setName("caseCookies");
		caseCookies.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseCookies.setBounds(66, caseTimecode.getY() + 22, caseCookies.getPreferredSize().width, 16);	
		grpURL.add(caseCookies);
		
		caseCookies.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseCookies.isSelected())
				{
					comboCookies.setEnabled(true);
				}
				else
				{
					comboCookies.setEnabled(false);
				}
			}
			
		});
		
		comboCookies = new JComboBox<String>();
		comboCookies.setEnabled(false);
		comboCookies.addItem("chrome");	
		comboCookies.addItem("firefox");
		comboCookies.addItem("safari");
		comboCookies.addItem("brave");						
		comboCookies.addItem("chromium");
		comboCookies.addItem("edge");		
		comboCookies.addItem("opera");		
		comboCookies.addItem("vivaldi");
		comboCookies.addItem("whale");
		comboCookies.setLocation(caseCookies.getX() + caseCookies.getWidth() + 4, caseCookies.getY() - 4);	
		comboCookies.setSize(grpURL.getWidth() - comboCookies.getX() - 14, 22);
		comboCookies.setMaximumRowCount(10);
		grpURL.add(comboCookies);
		
		comboFormats = new JComboBox<String>();
		comboFormats.addItem("default");
		comboFormats.addItem("bestvideo+bestaudio");					
		comboFormats.addItem("up to 4K");
		comboFormats.addItem("up to 1440p");
		comboFormats.addItem("up to 1080p");
		comboFormats.addItem("up to 720p");
		comboFormats.addItem("up to 480p");
		comboFormats.addItem("up to 360p");
		comboFormats.addItem("up to 240p");
		comboFormats.addItem("bestvideo");
		comboFormats.addItem("bestaudio");
		comboFormats.setLocation(16, 246 + 22);	
		comboFormats.setSize(380, 22);
		comboFormats.setMaximumRowCount(10);
		grpURL.add(comboFormats);
		
		comboFormats.addPopupMenuListener(new PopupMenuListener(){

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
				if (comboFormats.getPreferredSize().getWidth() > 380)
				{
					comboFormats.setSize(comboFormats.getPreferredSize());				
				}
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				comboFormats.setSize(380,22);	
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
			
		});
		
		lblQualit = new JLabel(Shutter.language.getProperty("lblQualit"));
		lblQualit.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQualit.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		lblQualit.setSize(lblQualit.getPreferredSize().width, 16);
		lblQualit.setLocation(textURL.getX() - lblQualit.getWidth() - 5, 110);		
		grpURL.add(lblQualit);
		
		btnOK = new JButton(Shutter.language.getProperty("btnStartFunction"));
		btnOK.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		btnOK.setBounds(comboFormats.getX(), comboFormats.getY() + comboFormats.getHeight() + 4, comboFormats.getWidth(), 21);		
		btnOK.setEnabled(false);
		grpURL.add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				startDownload();
			}
		});
		
		caseMP3 = new JCheckBox(Shutter.language.getProperty("caseMP3"));
		caseMP3.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseMP3.setBounds(121, caseAuto.getY(), caseMP3.getPreferredSize().width + 4, 16);		
		grpURL.add(caseMP3);
		
		caseMP3.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseMP3.isSelected())
				{
					caseWAV.setSelected(false);
					
					if (caseAuto.isSelected())
						comboFormats.setSelectedItem("bestaudio");
				}
				else
				{
					if (caseAuto.isSelected())
						comboFormats.setSelectedIndex(0);
				}
			}
			
		});
		
		caseWAV = new JCheckBox(Shutter.language.getProperty("caseWAV"));
		caseWAV.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseWAV.setBounds(caseMP3.getX() + caseMP3.getWidth(), caseAuto.getY(), caseWAV.getPreferredSize().width + 4, 16);		
		grpURL.add(caseWAV);
		
		caseWAV.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseWAV.isSelected())
				{
					caseMP3.setSelected(false);
					
					if (caseAuto.isSelected())
						comboFormats.setSelectedItem("bestaudio");
				}
				else
				{
					if (caseAuto.isSelected())
						comboFormats.setSelectedIndex(0);
				}
			}
			
		});
		
		frame.getContentPane().add(grpURL);			
		
		caseVideoPass = new JCheckBox(Shutter.language.getProperty("caseVideoPass"));
		caseVideoPass.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseVideoPass.setSize(caseVideoPass.getPreferredSize().width, 16);	
		caseVideoPass.setLocation(66, 221 + 22);
		grpURL.add(caseVideoPass);
			
		caseVideoPass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseVideoPass.isSelected())
					textVideoPass.setEnabled(true);
				else
				{
					textVideoPass.setText("");
					textVideoPass.setEnabled(false);
				}
			}
		
		});		
		
		textVideoPass = new JTextField();
		textVideoPass.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textVideoPass.setBounds(caseVideoPass.getLocation().x + caseVideoPass.getWidth() + 4, caseVideoPass.getY() - 3, grpURL.getSize().width - (caseVideoPass.getLocation().x + caseVideoPass.getSize().width) - 17, 21);
		textVideoPass.setEnabled(false);
		textVideoPass.setColumns(10);	
		grpURL.add(textVideoPass);
				
		caseUser = new JCheckBox(Shutter.language.getProperty("caseUser"));
		caseUser.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		caseUser.setBounds(caseVideoPass.getX(), caseCookies.getY() + 22, caseUser.getPreferredSize().width, 16);
		grpURL.add(caseUser);
		
		caseUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseUser.isSelected())
					textUser.setEnabled(true);
				else
				{
					textUser.setText("");
					textUser.setEnabled(false);
				}
			}
		
		});
		
		textUser = new JTextField();
		textUser.setEnabled(false);
		textUser.setText((String) null);
		textUser.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textUser.setColumns(10);
		textUser.setBounds(caseVideoPass.getLocation().x + caseVideoPass.getWidth() + 4, caseUser.getY() - 3, grpURL.getSize().width - (caseVideoPass.getLocation().x + caseVideoPass.getSize().width) - 17, 21);
		grpURL.add(textUser);
				
		casePass = new JCheckBox(Shutter.language.getProperty("casePass"));
		casePass.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		casePass.setBounds(caseVideoPass.getX(), caseUser.getY() + 22, casePass.getPreferredSize().width, 16);
		grpURL.add(casePass);
		
		casePass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (casePass.isSelected())
					textPass.setEnabled(true);
				else
				{
					textPass.setText("");
					textPass.setEnabled(false);
				}
			}
		
		});
		
		textPass = new JPasswordField();
		textPass.setEnabled(false);
		textPass.setText((String) null);
		textPass.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textPass.setColumns(10);
		textPass.setEchoChar('•');
		textPass.setBounds(caseVideoPass.getLocation().x + caseVideoPass.getWidth() + 4, casePass.getY() - 3, grpURL.getSize().width - (caseVideoPass.getLocation().x + caseVideoPass.getSize().width) - 17, 21);
		grpURL.add(textPass);
	
	}

	private String options() {
		
		String options = "";
		
		if (caseMetadata.isSelected())
		{
			options += " --embed-thumbnail --embed-metadata";
		}
		
		if (caseTimecode.isSelected())
		{
			options += " --download-sections " + '"' + "*" + textTimecodeIn.getText() + "-" + textTimecodeOut.getText() + '"';
		}
		
		if (caseCookies.isSelected())
		{
			options += " --cookies-from-browser " + comboCookies.getSelectedItem().toString();
		}
		
		if (caseUser.isSelected())
		{
			options += " --username " + '"' + textUser.getText() + '"';
		}
		
		if (casePass.isSelected())
		{
			options += " --password " + '"' + new String(textPass.getPassword()) + '"';
		}
		
		if (caseVideoPass.isSelected())
		{
			options += " --video-password " + '"' + textVideoPass.getText() + '"';
		}
		
		return options;		
	}
	
	private void startDownload() {
		
			Thread downloadProcess = new Thread(new Runnable() {
				
				public void run(){ 
						
				complete = 0;
				Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));
						
		        try {
		        	
	        		if (textURL.getText().toLowerCase().equals("update"))
	        		{
	        			FFMPEG.disableAll();
	        			Shutter.btnStart.setEnabled(false);
	        			Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("update"));
	        			YOUTUBEDL.update();
	        			Shutter.progressBar1.setIndeterminate(true);
	        			        			
	        			do {
	        				Thread.sleep(100);
	        			} while(YOUTUBEDL.isRunning);
	        			
	        			SwingUtilities.invokeLater(new Runnable()
	        			{
	        	           @Override
	        	           public void run() {
	        	        	   Shutter.progressBar1.setIndeterminate(false);
	        	           }
	        			});
	        			FFMPEG.enableAll();	 
	        			FFMPEG.enfOfFunction();	     
	        		}
	        		else 
	        		{	
	        			//Loop each line
	        			for (String line : textURL.getText().split("\n"))
	        			{	  
			        		//Format checking
							if (caseAuto.isSelected()) 
							{				
								if (comboFormats.getSelectedItem().toString().equals("default"))
								{
									YOUTUBEDL.format = "";
								}
								else if (comboFormats.getSelectedItem().toString().contains("up to "))
								{
									switch (comboFormats.getSelectedItem().toString().replace("up to ", ""))
									{
										case "4K": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?4096][height<=?2160]+bestaudio/best[width<=?4096][height<=?2160]" + '"'; break;
										case "1440p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?2560][height<=?1440]+bestaudio/best[width<=?2560][height<=?1440]" + '"'; break;
										case "1080p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?1920][height<=?1080]+bestaudio/best[width<=?1920][height<=?1080]" + '"'; break;
										case "720p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?1280][height<=?720]+bestaudio/best[width<=?1280][height<=?720]" + '"'; break;
										case "480p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?854][height<=?480]+bestaudio/best[width<=?854][height<=?480]" + '"'; break;
										case "360p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?640][height<=?360]+bestaudio/best[width<=?640][height<=?360]" + '"'; break;
										case "240p": YOUTUBEDL.format = "-f " + '"' + "bestvideo[width<=?426][height<=?240]+bestaudio/best[width<=?426][height<=?240]" + '"'; break;
									}
								}	
								else
									YOUTUBEDL.format = "-f " + comboFormats.getSelectedItem().toString();
								
							}							
							else
							{
								String[] f = comboFormats.getSelectedItem().toString().split(" ");		
								
								YOUTUBEDL.format = "-f " + f[0];								
							}
	
							FFMPEG.disableAll();
							Shutter.btnStart.setEnabled(false);
						
						    //Download	    
							YOUTUBEDL.run('"' + line + '"', options());
							
						       do { 
						    	   Thread.sleep(100);		
						       }while (YOUTUBEDL.runProcess.isAlive() && FFMPEG.cancelled == false);
						       
						       if (Shutter.cancelled)
						       {
							    	 if (YOUTUBEDL.outputFile.exists()) 
							    		 YOUTUBEDL.outputFile.delete();
						       }
						       else //Audio conversion
						       {
						    	   Shutter.tempsRestant.setVisible(false);
						    	   String ext = YOUTUBEDL.outputFile.toString().substring(YOUTUBEDL.outputFile.toString().lastIndexOf("."));
						    	   if (caseWAV.isSelected())
						    	   {		
						    		   	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("convertToWAV")); 
										String cmd = " -vn -y ";
										FFMPEG.run(" -i " + '"' + YOUTUBEDL.outputFile.toString() + '"' + cmd + '"'  + YOUTUBEDL.outputFile.toString().replace(ext, ".wav") + '"');	
									
									       do { 
												Thread.sleep(100);		
								       }while (FFMPEG.isRunning && FFMPEG.cancelled == false);			
										
								       //Suppression du fichier audio si processus annulé
								       if (Shutter.cancelled)
								       {
								    	   File audioFile = new File (YOUTUBEDL.outputFile.toString().replace(ext, ".wav"));
								    	   audioFile.delete();
								       }
						    	   }
						    	   else if (caseMP3.isSelected())
						    	   {
						    		   Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("convertToMP3"));  
						    		   String cmd = " -vn -c:a mp3 -b:a 256k -y ";
						    		   FFMPEG.run(" -i " + '"' + YOUTUBEDL.outputFile.toString() + '"' + cmd + '"'  + YOUTUBEDL.outputFile.toString().replace(ext, ".mp3") + '"');	
									
								       do { 
												Thread.sleep(100);		
								       }while (FFMPEG.isRunning && FFMPEG.cancelled == false);			
								       
								       //Suppression du fichier audio si processus annulé
								       if (Shutter.cancelled)
								       {
								    	   File audioFile = new File (YOUTUBEDL.outputFile.toString().replace(ext, ".mp3"));
								    	   audioFile.delete();
								       }
						    	   }		    	   			       
						       }
						       					       
						       if (Shutter.cancelled == false)
						       {
									complete++;
									Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));
									
									//Ouverture du dossier
									if (Shutter.caseOpenFolderAtEnd1.isSelected())
									{
										try {
											Desktop.getDesktop().open(new File(Shutter.lblDestination1.getText()));
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
						       }
								FFMPEG.enableAll();
								FFMPEG.enfOfFunction();							
			        			FunctionUtils.addFileForMail(YOUTUBEDL.outputFile.toString());		       
	        				}
	        			}
					       				        
					} catch (InterruptedException e1) {}
		        }
			});
			downloadProcess.start();
					
			Utils.changeDialogVisibility(frame, true);				
	}

	private void PasteFromClipBoard(boolean mouse){
		
		if (textURL.getText().equals(Shutter.language.getProperty("textURL")))
			textURL.setText("");
		
		textURL.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textURL.setForeground(Color.WHITE);
   		btnOK.setEnabled(true);
   		
   		if (caseAuto.isSelected() == false)
			caseAuto.doClick();
   		
   		if (textURL.getLineCount() == 1)
		{
			caseAuto.setEnabled(true);
		}
		else
		{
			caseAuto.setEnabled(false);
		}
		
		if (mouse)
		{
    	   Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
           Transferable clipTf = sysClip.getContents(null);
           if (clipTf != null) {
               if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                   try {
                	   	textURL.setText(textURL.getText() + (String) clipTf.getTransferData(DataFlavor.stringFlavor));
                   } catch (Exception er) {}
               }
           }
		}       
		

	}
}