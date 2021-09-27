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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.io.FileUtils;

import library.FFPROBE;

public class SubtitlesTimeline {

	public static JFrame frame;
	public static JTextPane txtSubtitles;
	private static JButton lblHelp;
	public static File srt;
	public static int number = 0;
	public static long actualSubIn = 0;
	public static long actualSubOut = 0;
	private static int previousSub = number;
	private static ArrayList<Integer> selectedSubs = new ArrayList<Integer>();
	public static Color fontColor;
	public static int fontSize;
	public static long timeIn;
	private static long keyboardTime;
	private static boolean keyboardLoop = false;
	private static boolean isSaving = false;
	private static int currentFrameHeight;	
	public static double currentCursorPosition = 1;
	public static long currentTime = System.currentTimeMillis();
	
	public static JScrollBar timelineScrollBar = new JScrollBar();
	private static int currentScrollBarValue;
	public static double zoom = (double) 0.1;
	boolean enableZoom = false;
	private static boolean enableAutoScroll = true;
	private static boolean enableTimelineScroll = false;	
	private static boolean control = false;
	private static boolean shift = false;
	private static boolean controlRight = false;
	private static boolean txtSubtitlesHasFocus = false;
	private static boolean mouseSrolling = false;
		
	public static JButton btnAjouter;	
	public static JButton btnSupprimer;	
	private static JButton btnEditAll = new JButton(Shutter.language.getProperty("btnModify"));
	public static JButton btnDebut; 
	public static JButton btnFin;
	private JButton btnI = new JButton("I");
	private JButton btnG = new JButton(Shutter.language.getProperty("btnG"));
	private static JRadioButton caseShowWaveform = new JRadioButton(Shutter.language.getProperty("caseShowWaveform"));
	private static JLabel lblOffset = new JLabel(Shutter.language.getProperty("lblOffset"));
	public static JTextField textOffset = new JTextField("0");
	public final static JPanel timeline = new JPanel();
	public final static JPanel cursor = new JPanel();
	public static JLabel waveform = null;
	private static Thread waveformReload;
	
	private static File dirTemp = new File(Shutter.dirTemp + "subtitles");
	
	private static int MousePositionX;
	private static int MouseTextWidth;
	private static int MouseTextLocationX;
	
	public SubtitlesTimeline(int positionX, int positionY) {
    	frame = new JFrame();
    	frame.setResizable(true);
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
    	frame.getContentPane().setBackground(new Color(50,50,50));
    	frame.getContentPane().setLayout(null);
    	frame.setAlwaysOnTop(true);
    	frame.setSize(1000, 270);
    	currentFrameHeight = 270;
    	frame.setMinimumSize(new Dimension (1000,270));
    	frame.setLocation(positionX, positionY);
		frame.setForeground(Color.WHITE);
		
		//IMPORTANT
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
    	    	
    	frame.setTitle(Shutter.language.getProperty("frameSubtitles") + " - 0 " + Shutter.language.getProperty("subtitlesLower"));   
    	
    	frame.addWindowListener(new WindowListener()
    	{
			@Override
			public void windowActivated(WindowEvent arg0) {				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {				

			Utils.changeFrameVisibility(VideoPlayer.frame, true);
			
			if (VideoPlayer.playerLeftVideo != null)
				VideoPlayer.playerLeftStop();
			
			if (VideoPlayer.playerRightVideo != null)
				VideoPlayer.playerRightStop();
			
			VideoPlayer.frame.getContentPane().removeAll();
			Shutter.caseInAndOut.setSelected(false);
			
			Utils.changeFrameVisibility(Shutter.frame, false);						
			Utils.changeFrameVisibility(frame, true);
			
    		if (Shutter.comboFonctions.getSelectedItem().equals("H.264") || Shutter.comboFonctions.getSelectedItem().equals("H.264"))
    			FFPROBE.CalculH264();
    		
    		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
    		{
				Shutter.caseInAndOut.setSelected(false);
    			SubtitlesTimeline.frame.dispose();
    		}
    		
    		if (srt.exists())
			{
			 int q = JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("integrateSRT"), Shutter.language.getProperty("subtitles"), JOptionPane.YES_NO_OPTION);
						 
			 if (q == 0)
			 {
				 if (Shutter.caseSubtitles.isSelected())
					 Shutter.caseSubtitles.doClick();
				 
				 Shutter.caseSubtitles.doClick();
			 }
			 else
			 {
					if (Shutter.caseOpenFolderAtEnd1.isSelected())
					{						
						if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux")) 
						{
							try {
								Runtime.getRuntime().exec(new String[]{"/usr/bin/open", "-R", srt.toString()});
							} catch (Exception e2){}
						}
						else if (System.getProperty("os.name").contains("Linux"))
						{
							try {
								Desktop.getDesktop().open(srt.getParentFile());
							} catch (Exception e2){}
						}
						else //Windows
						{
							try {
								Runtime.getRuntime().exec("explorer.exe /select," + srt.toString());
							} catch (IOException e1) {}
						}
					}
			 }
			}
			
			if (dirTemp.exists())
			{
				try {
					FileUtils.deleteDirectory(dirTemp);
				} catch (IOException e) {}
			}
			
			if (VideoPlayer.waveform.exists())
				VideoPlayer.waveform.delete();
			
			timeline.removeAll();
		}

			@Override
			public void windowClosing(WindowEvent arg0) {	

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {	
				if (dirTemp.exists())
				{
					try {
						FileUtils.deleteDirectory(dirTemp);
					} catch (IOException e) {}
				}
				
				subtitlesNumber();		
				setSubtitles(srt); //Permet de réarranger l'ordre des subs
			}
    		
    	});
    	    	
    	frame.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {	
				
				if (e.getNewState() == JFrame.MAXIMIZED_BOTH)
					frame.setExtendedState(JFrame.NORMAL);
				
				frame.setBounds(0, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - frame.getHeight(),Toolkit.getDefaultToolkit().getScreenSize().width, 270);		

				//Waveform
				VideoPlayer.addWaveform(true);
			}    		
    	});
    	    	
    	frame.addComponentListener(new ComponentAdapter() {
    		
            public void componentResized(ComponentEvent e) {
            	
            	//On arrondi pour être sur une seconde complète
            	frame.setSize(Math.round(frame.getWidth() / 100) * 100, frame.getHeight());
            	
            	//Waveform
        		VideoPlayer.addWaveform(true);
            }
        });
    	
    	txtSubtitles = new JTextPane();
    	SimpleAttributeSet attribs = new SimpleAttributeSet();  
    	StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_CENTER);  
    	txtSubtitles.setCaretColor(Color.BLACK);
    	txtSubtitles.setParagraphAttributes(attribs,true);
    	txtSubtitles.setSelectionColor(new Color(71,163,236,127));
    	txtSubtitles.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12)); 
    	
    	if (System.getProperty("os.name").contains("Windows"))
    		txtSubtitles.setBounds(10, 36, frame.getWidth() - 36, 36); 
    	else
    		txtSubtitles.setBounds(10, 36, frame.getWidth() - 24, 36); 
    	
    	txtSubtitles.addKeyListener(new KeyListener()
    	{
    		
			@Override
			public void keyPressed(KeyEvent e) {
				String text[] = txtSubtitles.getText().split("\\r?\\n");
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{					
					if (text.length > 1)
						e.consume();
				}	
				
				keyboardTime = System.currentTimeMillis();
				
				//Important pour la touche maj. enfoncée et ctrl avec une touche
				if (txtSubtitles.getText().equals("Title") && e.getKeyCode() != KeyEvent.VK_SPACE && (e.getModifiersEx() & KeyEvent.META_DOWN_MASK) == 0 &&  (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == 0)										
					txtSubtitles.setText("");								
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) && e.getKeyCode() == KeyEvent.VK_A)
				{
					txtSubtitles.setSelectionStart(0);
					txtSubtitles.setSelectionEnd(txtSubtitles.getText().length());
				}
				
				if (e.getKeyCode() != KeyEvent.VK_HOME && e.getKeyCode() != KeyEvent.VK_END && e.getKeyCode() != KeyEvent.VK_TAB)
				{			
					boolean newSubtitle = true;
										
					//Lorsque txtSubtitles est vide
					if (txtSubtitles.getText().isEmpty() || txtSubtitles.getText() == null || txtSubtitles.getText() == "")
					{
						if (KeyEvent.getKeyText(e.getKeyCode()).length() == 1)										
							txtSubtitles.setText(String.valueOf(e.getKeyChar()));						
					}
					else if (txtSubtitlesHasFocus == false)
					{					
						if (KeyEvent.getKeyText(e.getKeyCode()).length() == 1)	
						{
							if (txtSubtitles.getText().equals("Title") && e.getKeyCode() != KeyEvent.VK_SPACE && (e.getModifiersEx() & KeyEvent.META_DOWN_MASK) == 0 &&  (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == 0 && e.getKeyCode() != KeyEvent.VK_TAB)
							{
								txtSubtitles.setText("");	
								txtSubtitles.setText(txtSubtitles.getText() + e.getKeyChar());
							}
						}
					}
						
					
					for (Component c : timeline.getComponents())
					{
						if (c instanceof JTextPane)
						{
							if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
							{		
								((JTextPane) c).setText(txtSubtitles.getText());
								repaintTimeline();
								newSubtitle = false;
								break;
							}
						}
					}	
					
					//Si on est en dehors d'un JTextPane
					if (newSubtitle)										
						addSubtitles(false);	
					else if (txtSubtitles.getText().length() > 0)
						keyboardSaveTime();		
				}
			}

			private void keyboardSaveTime() {
				if (keyboardLoop == false)
				{
					Thread t = new Thread(new Runnable() {
						public void run() 
						{
							do {
								keyboardLoop = true;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while ((keyboardTime + 1000) > System.currentTimeMillis());
							
							saveSubtitles(true, false);	
							
							keyboardLoop = false;
						}					
					});
					t.start();	
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
    		
    	});
    	
    	frame.getContentPane().add(txtSubtitles);
    	    	    	    	
    	JLabel lblTexte = new JLabel(Shutter.language.getProperty("lblTexte"));
    	lblTexte.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
    	lblTexte.setBounds(10, 12, lblTexte.getPreferredSize().width, 14);
    	frame.getContentPane().add(lblTexte);	
    	
    	btnI.setFont(new Font("Courier New", Font.ITALIC, 13));
    	btnI.setBounds(lblTexte.getLocation().x + lblTexte.getWidth() + 7, 8, 22, 22);    	
    	frame.getContentPane().add(btnI);
    	
    	btnI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String text[] = txtSubtitles.getText().split("\\r?\\n");
				
				if (txtSubtitles.getText().contains("<i>"))
				{
					txtSubtitles.setText(txtSubtitles.getText().replace("<i>", "").replace("</i>", ""));
				}
				else if (txtSubtitles.getSelectedText() != null && text[0].contains(txtSubtitles.getSelectedText()))
				{
					txtSubtitles.setText("<i>" + text[0] + "</i>" + System.lineSeparator() + text[1]);
				}
				else if (txtSubtitles.getSelectedText() != null && text[1].contains(txtSubtitles.getSelectedText()))
				{
					txtSubtitles.setText(text[0] + System.lineSeparator() + "<i>" + text[1] + "</i>");
				}
				else				
					txtSubtitles.setText("<i>" + txtSubtitles.getText() + "</i>" );

				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
						{
							((JTextPane) c).setText(txtSubtitles.getText());	
							repaintTimeline();
							break;
						}
					}
				}
				
				if (txtSubtitles.getText().length() > 0)
					saveSubtitles(true, false);
			}
    		
    	});
    	
    	btnG.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
    	btnG.setBounds(btnI.getLocation().x + btnI.getWidth() + 4, 8, 22, 22);    	
    	frame.getContentPane().add(btnG);
    	
    	btnG.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String text[] = txtSubtitles.getText().split("\\r?\\n");
				
				if (txtSubtitles.getText().contains("<b>"))
				{
					txtSubtitles.setText(txtSubtitles.getText().replace("<b>", "").replace("</b>", ""));
				}
				else if (txtSubtitles.getSelectedText() != null && text[0].contains(txtSubtitles.getSelectedText()))
				{
					txtSubtitles.setText("<b>" + text[0] + "</b>" + System.lineSeparator() + text[1]);
				}
				else if (txtSubtitles.getSelectedText() != null && text[1].contains(txtSubtitles.getSelectedText()))
				{
					txtSubtitles.setText(text[0] + System.lineSeparator() + "<b>" + text[1] + "</b>");
				}
				else				
					txtSubtitles.setText("<b>" + txtSubtitles.getText() + "</b>" );
				
				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
						{
							((JTextPane) c).setText(txtSubtitles.getText());	
							repaintTimeline();
							break;
						}
					}
				}
				
				if (txtSubtitles.getText().length() > 0)
					saveSubtitles(true, false);
			}
    		
    	});
    	    	
    	lblHelp = new JButton(Shutter.language.getProperty("lblHelp"));
    	lblHelp.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	lblHelp.setBounds(btnG.getX() + btnG.getWidth() + 4, 8, lblHelp.getPreferredSize().width, 22);
    	frame.getContentPane().add(lblHelp);    
    	
    	lblHelp.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (SubtitlesHelp.frame != null)
				{
					if (SubtitlesHelp.frame.isVisible() == false)
						new SubtitlesHelp();
				} 
				else
					new SubtitlesHelp();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
    		
    	});
    	
    	btnEditAll.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnEditAll.setSize(btnEditAll.getPreferredSize().width, 21);
    	if (System.getProperty("os.name").contains("Windows"))    		
    		btnEditAll.setLocation(frame.getWidth() - btnEditAll.getWidth() - 22, 8);
    	else
    		btnEditAll.setLocation(frame.getWidth() - btnEditAll.getWidth() - 10, 8);
    	frame.getContentPane().add(btnEditAll);
    	
    	btnEditAll.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				new SubtitlesEdit(number);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
    		
    	});
    	  	
    	btnAjouter = new JButton(Shutter.language.getProperty("btnAdd"));
    	btnAjouter.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnAjouter.setBounds(btnEditAll.getLocation().x - btnAjouter.getWidth() - 2, 8, btnAjouter.getPreferredSize().width, 21);
    	frame.getContentPane().add(btnAjouter);
    	    	
    	btnAjouter.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addSubtitles(true);									
		}
    		
    	});

    	btnSupprimer = new JButton(Shutter.language.getProperty("btnDelete"));
    	btnSupprimer.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnSupprimer.setMargin(new Insets(0,0,0,0));
    	btnSupprimer.setBounds(btnAjouter.getLocation().x - btnSupprimer.getWidth() - 2, 8, 80, 21);
    	btnSupprimer.setEnabled(false);
    	frame.getContentPane().add(btnSupprimer);
    	
    	btnSupprimer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
					int q = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("deleteConfirmation"), Shutter.language.getProperty("subtitlesDelete"), JOptionPane.YES_NO_OPTION);
					
					if (q == 0)		
					{
						if (selectedSubs.size() != 0)
						{
							int supp = 0;
							for (int index = 0 ; index < selectedSubs.size() ; index++)
							{
								timeline.remove(selectedSubs.get(index) - supp);
								supp ++;
							}
							
							selectedSubs.clear();
							
							saveSubtitles(true, false);
				
							//Permet de relancer la boucle
							timeIn = 0;
						}					
						else
							deleteSubtitles();
					}
			}    		
    		
    	});
     	    	 
    	btnFin = new JButton(Shutter.language.getProperty("btnFin"));
    	btnFin.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnFin.setBounds(btnSupprimer.getLocation().x - btnFin.getWidth() - 2, 8, btnFin.getPreferredSize().width, 21);
    	btnFin.setEnabled(false);
    	frame.getContentPane().add(btnFin);
    	
    	btnFin.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int sub = 0;
				boolean outside = true;

				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						if (c.getX() + c.getWidth() < cursor.getX())
							sub++;

						if (cursor.getLocation().x > c.getLocation().x && cursor.getLocation().x <= (c.getLocation().x + c.getWidth()))
						{
							c.setBounds(c.getX(), c.getY(), c.getWidth() - ((c.getX() + c.getWidth()) - cursor.getX()), c.getHeight());
							outside = false;
							break;
						}
					}
				}
				
				//Si on est en dehors d'un sous-titre
				if (outside)
					timeline.getComponent(sub).setSize(cursor.getX() - timeline.getComponent(sub).getX(),timeline.getComponent(sub).getHeight());
				
				saveSubtitles(true, false);
				
			}
    		
    	});
    	  	    	
     	btnDebut = new JButton(Shutter.language.getProperty("btnDebut"));
    	btnDebut.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnDebut.setBounds(btnFin.getLocation().x - btnDebut.getWidth() - 2, 8, btnDebut.getPreferredSize().width, 21);
    	btnDebut.setEnabled(false);
    	frame.getContentPane().add(btnDebut);
    	
    	btnDebut.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int sub = 1; //On enlève le curseur
				boolean outside = true;
				
				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						if (c.getX() < cursor.getX())
							sub ++;
						
						if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
						{
							c.setBounds(cursor.getX(), c.getY(), c.getWidth() - (cursor.getX() -  c.getX()), c.getHeight());
							outside = false;
							break;
						}
					}
					
					//Permet de relancer la boucle
					timeIn = 0;
				}
				
				//Si on est en dehors d'un sous-titre
				if (outside)
				{
					int subX = timeline.getComponent(sub).getX();
					int subW = timeline.getComponent(sub).getWidth();
					timeline.getComponent(sub).setLocation(cursor.getX(), timeline.getComponent(sub).getY());
					timeline.getComponent(sub).setSize(subW + (subX - cursor.getX()),timeline.getComponent(sub).getHeight());
				}
				
				saveSubtitles(true, false);
			}
    		
    	});
    	
    	caseShowWaveform.setSelected(true);
    	caseShowWaveform.setEnabled(false);
    	caseShowWaveform.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
    	caseShowWaveform.setBounds(btnDebut.getX() - caseShowWaveform.getWidth() - 20, 6, caseShowWaveform.getPreferredSize().width, 23);
    	frame.getContentPane().add(caseShowWaveform);
    	
    	caseShowWaveform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseShowWaveform.isSelected())
				{
					timeline.removeAll();
					setSubtitles(srt);	
				}
        		else
        		{
        			if (waveform != null)
        			{
	        			timeline.remove(waveform);
	        			repaintTimeline();
        			}
        		}
	        			
			}
    		
    	});
   					
    	JLabel images = new JLabel("i");
    	images.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	images.setBounds(caseShowWaveform.getX() - images.getWidth() - 7, 9, 10, 16);
    	frame.getContentPane().add(images);	
    	
		textOffset.setBounds(images.getX() - images.getWidth() - 27, lblOffset.getLocation().y, 34, 16);
		textOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		textOffset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		frame.getContentPane().add(textOffset);
		
		textOffset.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				textOffset.setFocusable(true);
				textOffset.requestFocus();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (textOffset.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					for (Component c : timeline.getComponents())
					{
						if (c instanceof JTextPane)
						{
							c.setLocation((int) (c.getX() + Math.ceil((float) Integer.parseInt(textOffset.getText()) * ((float) (1000 / FFPROBE.currentFPS))*zoom)), c.getY());					
						}
					}
					
					saveSubtitles(true, false);
					
					textOffset.setText("0");
					textOffset.setFocusable(false);
					frame.requestFocus();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					textOffset.setText("0");
					textOffset.setFocusable(false);
					frame.requestFocus();
				}
				else
				{
					textOffset.setFocusable(true);
					textOffset.requestFocus();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9\\-]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textOffset.getText().length() >= 4)
					textOffset.setText("");				
			}		
			
		});
		
		textOffset.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textOffset.setFocusable(true);
				textOffset.requestFocus();
				textOffset.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textOffset.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textOffset.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Offset.mouseX = e.getX();
				Offset.offset = Integer.parseInt(textOffset.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (textOffset.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					for (Component c : timeline.getComponents())
					{
						if (c instanceof JTextPane)
						{
							c.setLocation((int) (c.getX() + Math.ceil((float) Integer.parseInt(textOffset.getText()) * ((float) (1000 / FFPROBE.currentFPS))*zoom)), c.getY());					
						}
					}
					
					saveSubtitles(true, false);
					
					textOffset.setText("0");
					textOffset.setFocusable(false);
					frame.requestFocus();
				}
			}
			
		});
		
		textOffset.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textOffset.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textOffset.setText(String.valueOf(Offset.offset + (e.getX() - Offset.mouseX)));					
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		lblOffset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblOffset.setAlignmentX(SwingConstants.RIGHT);
		lblOffset.setBounds(textOffset.getX() - lblOffset.getWidth() - 7, 9, lblOffset.getPreferredSize().width, 16);
		frame.getContentPane().add(lblOffset);
    	
    	refreshData();
		
		KeyListener keyListener = new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {				
					
				if ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)
				{								        
					control = true;
					enableZoom = true;	
					
					if (e.getKeyCode() == KeyEvent.VK_SHIFT)
						shift = true;	
					
					if (e.getKeyCode() == KeyEvent.VK_ALT)
						controlRight = true;
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_TAB)
				{
					e.consume();
					for (Component c : frame.getContentPane().getComponents())
					{
						c.setFocusable(false);
					}

					txtSubtitles.setFocusable(true);
					
					if (txtSubtitles.hasFocus())
					{
						frame.requestFocus();
					}
					else
					{
						txtSubtitles.requestFocus();
					}
					
				}					
												
				if (control)
				{										
					if (e.getKeyCode() == KeyEvent.VK_I)
						btnI.doClick();
					
					if (e.getKeyCode() == KeyEvent.VK_B)
						btnG.doClick();
					
					//Copie clipboard
					if ((e.getKeyCode() == KeyEvent.VK_X) && txtSubtitles.getSelectionStart() != txtSubtitles.getSelectionEnd())
					{					
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtSubtitles.getText().substring(txtSubtitles.getSelectionStart(), txtSubtitles.getSelectionEnd())), null);
						txtSubtitles.setText(txtSubtitles.getText().replace(txtSubtitles.getText().substring(txtSubtitles.getSelectionStart(), txtSubtitles.getSelectionEnd()), ""));
						
						for (Component c : timeline.getComponents())
						{
							if (c instanceof JTextPane)
							{
								if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
								{		
									((JTextPane) c).setText(txtSubtitles.getText());
									repaintTimeline();
									break;
								}
							}
						}
					}
					
					if (frame.hasFocus())
					{
						 if (e.getKeyCode() == KeyEvent.VK_A) 
						 {
							 	int index = 0;
								selectedSubs.clear();
								for (Component c : timeline.getComponents())
								{
									if (c instanceof JTextPane)
									{
										index ++;
										((JComponent) c).setBorder(new RoundedBorder(5, Color.RED));		
										c.setForeground(Color.RED);
										selectedSubs.add(index);
										repaintTimeline();
									}
								}
						 }
					}
					
					if (e.getKeyCode() == KeyEvent.VK_C) 
					{
						if (txtSubtitles.getSelectionStart() != txtSubtitles.getSelectionEnd())
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtSubtitles.getText().substring(txtSubtitles.getSelectionStart(), txtSubtitles.getSelectionEnd())), null);
						else
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtSubtitles.getText()), null);
					}
					
			        if (e.getKeyCode() == KeyEvent.VK_V) 
			        	PasteFromClipBoard();	
			        
			        if (e.getKeyCode() == KeyEvent.VK_Z && shift == false) 
			        {
			        	frame.requestFocus();
			        	loadBackupSubtitles();		
			        }
			        else if (e.getKeyCode() == KeyEvent.VK_Z && shift || e.getKeyCode() == KeyEvent.VK_Y)
			        {
			        	frame.requestFocus();
			        	loadRestoreSubtitles();	
			        }							
					
					//Volume up
					if (e.getKeyCode() == 107)
						VideoPlayer.sliderVolume.setValue(VideoPlayer.sliderVolume.getValue() + 2);
						
					//Volume down
					if (e.getKeyCode() == 109)
						VideoPlayer.sliderVolume.setValue(VideoPlayer.sliderVolume.getValue() - 2);
				}				
				
				
				if (frame.hasFocus())
				{
					if (e.getKeyCode() == KeyEvent.VK_HOME)
					{
						enableAutoScroll = false;	
						cursor.setLocation(0, cursor.getY());
						setVideoPosition(0);
						timelineScrollBar.setValue(0);	

	  					enableAutoScroll = true;
					}
					
					if (e.getKeyCode() == KeyEvent.VK_END)
					{
	  					int time = (int) ((timeline.getComponent(number).getX() + timeline.getComponent(number).getWidth())/zoom);  					
	  					if (time > 0)
	  						setVideoPosition(time);
					}
					
					//WaveForm
					if ((e.getKeyCode() == KeyEvent.VK_W)) 
					{
						if (caseShowWaveform.isEnabled())
							caseShowWaveform.doClick();
					}
					
					if (e.getKeyCode() == KeyEvent.VK_I && control == false)
						btnDebut.doClick();
					if (e.getKeyCode() == KeyEvent.VK_O)
					{
						btnFin.doClick();
						//Permet de relancer la boucle
						timeIn = 0;
					}
					
					if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_K)
						VideoPlayer.leftPlay.doClick();
	
					if (e.getKeyCode() == KeyEvent.VK_J)
					{
						setVideoPosition((int) (VideoPlayer.playerLeftGetTime() - ((1000 /FFPROBE.currentFPS) * 10)));
	  				}
						
					if (e.getKeyCode() == KeyEvent.VK_L)
					{
						setVideoPosition((int) (VideoPlayer.playerLeftGetTime() + ((1000 /FFPROBE.currentFPS) * 10)));
					}
					
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
						btnAjouter.doClick();
					
					if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE)
						btnSupprimer.doClick();
					
					if (e.getKeyCode() == KeyEvent.VK_UP)
						nextSubtitle();			
					else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						previousSubtitle();	
					
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
						VideoPlayer.leftPrevious.doClick();			
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						VideoPlayer.leftNext.doClick();					
					}
				}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_META || e.getKeyCode() == KeyEvent.VK_CONTROL)
				{
					enableZoom = false;
					control = false;			
				}
				
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)
					shift = false;	
				
				if (e.getKeyCode() == KeyEvent.VK_ALT)
					controlRight = false;		
				
				VideoPlayer.playerLeft.repaint();
			}

			@Override
			public void keyTyped(KeyEvent e) {				
			}

		};	
		
		MouseWheelListener mouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (enableZoom)
				{			
					enableAutoScroll = false;		
					double actualZoom = zoom;
					
					if (zoom >= 0.1)
					{
						zoom -= (double) e.getWheelRotation() / 10;
						
						if (caseShowWaveform.isEnabled() && caseShowWaveform.isSelected())
						{
							VideoPlayer.addWaveform(true);
							waveform.setVisible(true);
						}
					}
					else
					{
						zoom -= (double) e.getWheelRotation() / 100;
						
						if (caseShowWaveform.isEnabled() && caseShowWaveform.isSelected())
							waveform.setVisible(false);
					}
					
					if (zoom < 0.01)
						zoom = (double) 0.01;

					DecimalFormat numberFormat = new DecimalFormat("0.0");
					if (zoom < 0.1)
						numberFormat = new DecimalFormat("0.00");
					
					if ((int) (VideoPlayer.sliderIn.getMaximum() - (float)frame.getWidth()/zoom) > 0)
						zoom = Double.parseDouble(numberFormat.format(zoom).replace(",", "."));		
					else
						zoom = actualZoom;												

					timeline.setSize((int) ((VideoPlayer.sliderIn.getMaximum()-(1000/FFPROBE.currentFPS)*2)*zoom), timeline.getHeight());
					timelineScrollBar.setMaximum(timeline.getWidth() - frame.getWidth());
										
					cursor.setLocation((int) (setTime(VideoPlayer.sliderIn.getValue())*zoom), cursor.getY());
					
					enableAutoScroll = true;
					
					timeline.removeAll();
					setSubtitles(srt);	
				}
				else
				{			
					if (mouseSrolling == false)
					{
						mouseSrolling = true;
						
						Thread t = new Thread(new Runnable() {
	
							@Override
							public void run() {
																
								if (VideoPlayer.frameLeft != null)
								{
									int newValue = timelineScrollBar.getValue() + e.getWheelRotation() * 100;
									if (zoom < 0.1)
										newValue = timelineScrollBar.getValue() + e.getWheelRotation() * 10;
									
									enableTimelineScroll = true;
									enableAutoScroll = false;
									timelineScrollBar.setValue(newValue);	
									enableTimelineScroll = false;
									enableAutoScroll = true;	
								}
								
								mouseSrolling = false;
							}						
						});		
						t.start();
					}
				}							
			}			
		};
		
		frame.addKeyListener(keyListener);
		frame.addMouseWheelListener(mouseWheelListener);
		txtSubtitles.addKeyListener(keyListener);	
		
		Toolkit.getDefaultToolkit().addAWTEventListener(
			    new AWTEventListener(){
			        public void eventDispatched(AWTEvent event){
			              KeyEvent ke = (KeyEvent)event;
			              if(ke.getID() == KeyEvent.KEY_PRESSED && txtSubtitles.hasFocus() == false){
			            	  frame.requestFocus();
			        	}
			        }
			     }, AWTEvent.KEY_EVENT_MASK);	
		
		Utils.changeFrameVisibility(frame, false);
		
		JPanel timelineBackround = new JPanel();
		timelineBackround.setBackground(new Color(50,50,50));
		timelineBackround.setLayout(null);
		timelineBackround.setBorder(BorderFactory.createTitledBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY), Shutter.language.getProperty("lblTimeline") + " ", TitledBorder.CENTER, TitledBorder.TOP, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		timelineBackround.setBounds(0, 80, frame.getWidth(), frame.getContentPane().getHeight() - 97);
		frame.getContentPane().add(timelineBackround);
		
		timeline.setBackground(new Color(50,50,50));
		timeline.setLayout(null);
		timeline.setBounds(0, 15, (int) ((VideoPlayer.sliderIn.getMaximum()-(1000/FFPROBE.currentFPS)*2)*zoom), timelineBackround.getHeight() - 20);
		timelineBackround.add(timeline);
				
		timeline.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {	
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				frame.requestFocus();
				
				VideoPlayer.sliderInChange = true;
				
				cursor.setLocation(e.getX(), cursor.getLocation().y);
				
				VideoPlayer.sliderIn.setValue((int) ((e.getX()-2)/zoom));	
				
				if (selectedSubs.size() != 0)
				{
					for (int index = 0 ; index < selectedSubs.size() ; index++)
					{
						((JComponent) timeline.getComponent(selectedSubs.get(index))).setBorder(new RoundedBorder(5, Utils.themeColor));
						((JComponent) timeline.getComponent(selectedSubs.get(index))).setForeground(Color.WHITE);
					}
				}
				
				selectedSubs.clear();
				repaintTimeline();
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				VideoPlayer.sliderInChange = false;						
				VideoPlayer.getTimeInPoint(VideoPlayer.playerLeftGetTime());
			}				
			
		});
		
		timeline.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				VideoPlayer.sliderInChange = true;	

				if (e.getX() <= 0)
				{
					cursor.setLocation(0, cursor.getLocation().y);
					VideoPlayer.sliderIn.setValue(0);	
				}
				else
				{
					cursor.setLocation(e.getX(), cursor.getLocation().y);
					VideoPlayer.sliderIn.setValue((int) ((e.getX()-2)/zoom));	
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {	
			}
			
		});		
		
		timelineScrollBar.setVisible(true);
		timelineScrollBar.setValue(0);
		timelineScrollBar.setBackground(new Color(50,50,50));
		timelineScrollBar.setOrientation(JScrollBar.HORIZONTAL);	
		timelineScrollBar.setBounds(0, 0, frame.getContentPane().getWidth(), 17);
		
		JPanel scrollBarPanel = new JPanel();
		scrollBarPanel.setBackground(new Color(50,50,50));
		scrollBarPanel.setLayout(null);
		scrollBarPanel.setBounds(0, frame.getContentPane().getHeight() - 17, frame.getContentPane().getWidth(), 17);
		frame.getContentPane().add(scrollBarPanel);	
		
		scrollBarPanel.add(timelineScrollBar);
		
		timelineScrollBar.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				enableTimelineScroll = true;
				enableAutoScroll = false;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	
				enableTimelineScroll = false;
				enableAutoScroll = true;
			}
			
		});
		
		timelineScrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {	
									
					timeline.setLocation(0 - timelineScrollBar.getValue(), timeline.getLocation().y);

					if (cursor.getLocation().x - timelineScrollBar.getValue() >= frame.getContentPane().getWidth() && enableTimelineScroll)
					{
						cursor.setLocation((int) (timelineScrollBar.getValue() + frame.getWidth() - (1000/FFPROBE.currentFPS)), cursor.getY());
						setVideoPosition((int) (cursor.getX()/zoom));
					}
					else if (cursor.getLocation().x - timelineScrollBar.getValue() < 0 && enableTimelineScroll)
					{
						cursor.setLocation(timelineScrollBar.getValue(), cursor.getY());
						setVideoPosition((int) (cursor.getX()/zoom));
					}
		      }			
			
		});
		
		cursor.setBackground(Color.RED);
		cursor.setBounds(0, 0, 2, timeline.getHeight());
		timeline.add(cursor);				
				
    	frame.addComponentListener(new ComponentAdapter() 
    	{  
            public void componentResized(ComponentEvent evt) {   
            	            	            	
            	if (System.getProperty("os.name").contains("Windows"))
            		txtSubtitles.setBounds(10, 36, frame.getWidth() - 36, 36); 
            	else
            		txtSubtitles.setBounds(10, 36, frame.getWidth() - 24, 36); 
            	
            	timelineBackround.setBounds(0, 80, frame.getWidth(), frame.getContentPane().getHeight() - 97);
            	timeline.setBounds(0, 15, (int) ((VideoPlayer.sliderIn.getMaximum()-(1000/FFPROBE.currentFPS)*2)*zoom), timelineBackround.getHeight() - 20);
            	timelineScrollBar.setMaximum(timeline.getWidth() - frame.getWidth());
            	        		            	
            	scrollBarPanel.setBounds(0, frame.getContentPane().getHeight() - 17, frame.getContentPane().getWidth(), 17);
            	timelineScrollBar.setBounds(0, 0, frame.getContentPane().getWidth(), 17);
            	cursor.setBounds(0, 0, 2, timeline.getHeight());            	
            	
            	//Buttons
            	if (System.getProperty("os.name").contains("Windows"))    		
            		btnEditAll.setLocation(frame.getWidth() - btnEditAll.getWidth() - 24, 8);
            	else
            		btnEditAll.setLocation(frame.getWidth() - btnEditAll.getWidth() - 12, 8);
            	btnAjouter.setBounds(btnEditAll.getLocation().x - btnAjouter.getWidth() - 2, 8, btnAjouter.getPreferredSize().width, 21);
            	btnSupprimer.setBounds(btnAjouter.getLocation().x - btnSupprimer.getWidth() - 2, 8, 80, 21);
            	btnFin.setBounds(btnSupprimer.getLocation().x - btnFin.getWidth() - 2, 8, btnFin.getPreferredSize().width, 21);
            	btnDebut.setBounds(btnFin.getLocation().x - btnDebut.getWidth() - 2, 8, btnDebut.getPreferredSize().width, 21);  
               	caseShowWaveform.setBounds(btnDebut.getX() - caseShowWaveform.getWidth() - 20, 6, caseShowWaveform.getPreferredSize().width, 23);
               	images.setBounds(caseShowWaveform.getX() - images.getWidth() - 7, 9, 10, 16);
               	textOffset.setBounds(images.getX() - images.getWidth() - 27, lblOffset.getLocation().y, 34, 16);
               	lblOffset.setBounds(textOffset.getX() - lblOffset.getWidth() - 7, 9, lblOffset.getPreferredSize().width, 16);           	
               	
               	lblHelp.setBounds(btnG.getX() + btnG.getWidth() + 4, 8, lblHelp.getPreferredSize().width, 22);
            	           
            	if (frame.getHeight() != currentFrameHeight)
            	{           		
            		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            							
	            	currentFrameHeight = frame.getHeight();
	            	timeline.removeAll(); //Force resizing subs and refresh all
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            	}
            	
            	if (srt.exists())
            		setSubtitles(srt);
            }
	    });    	

    	
		for (Component c : frame.getContentPane().getComponents())
		{
			c.setFocusable(false);
		}
		txtSubtitles.setFocusable(true);
		
    	frame.requestFocus();
	}
	
	public static void refreshData() {
					
		try
		{	
			if (enableAutoScroll)
			{	
				int posX = VideoPlayer.sliderIn.getValue();
				cursor.setLocation((int) (setTime(posX)*zoom), cursor.getY());
			}
			
			//Permet d'afficher le sub en cours dès que timeIn change
			if (txtSubtitles.hasFocus() == false)
			{				
				timeIn = (long) VideoPlayer.playerLeftTime;
				txtSubtitles.setText("");
				btnSupprimer.setEnabled(false);
				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{ 
						//Si le curseur ce situe entre un JTextPane
						if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
						{
							txtSubtitles.setText(((JTextPane) c).getText()); 
							actualSubIn = c.getLocation().x;				   //Point d'entrée permettant de relancer la boucle dès que le curseur sort du sous-titre
							actualSubOut = (c.getLocation().x + c.getWidth()); //Point de sortie permettant de relancer la boucle dès que le curseur sort du sous-titre
							btnSupprimer.setEnabled(true);
							break;
						}
					}			
				}
			}
		
			if (enableAutoScroll)
			{
				if (cursor.getLocation().x - timelineScrollBar.getValue() >= frame.getContentPane().getWidth())
				{
					timelineScrollBar.setValue(cursor.getLocation().x - frame.getWidth()/2);
				}
				else if (cursor.getLocation().x + 2 - timelineScrollBar.getValue() < 0)
				{
					timelineScrollBar.setValue(cursor.getLocation().x - frame.getWidth()/2);
				}
									
				timelineScrollBar.setValue(setTime(timelineScrollBar.getValue()));
				
				if (currentScrollBarValue != timelineScrollBar.getValue() || waveformReload == null)
				{					
					currentScrollBarValue = timelineScrollBar.getValue();
					
					if (waveformReload == null || waveformReload.isAlive() == false)
					{
						waveformReload = new Thread(new Runnable() {
	
							@Override
							public void run() {
								do
								{
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {}
								} while (VideoPlayer.addWaveformIsRunning);
								
								//Waveform
								VideoPlayer.addWaveform(true);
							}
							
						});
						waveformReload.start();		
					}
				}
			}
			
		}catch (Exception e) {}

	}
	
	private static class Offset {
		static int mouseX;
		static int offset;
	}
	
	private static class RoundedBorder implements Border {
        
        private int radius;
        private Color color;
        
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        	g.setColor(color);
            g.drawRoundRect(x,y,width-1,height-1,radius,radius);
        }
    }
	
	private static JTextPane addText(String subContent, int x, int size) {
		JTextPane text = new JTextPane();
		text.setBackground(new Color(50,50,50, 120));
		text.setForeground(Color.WHITE);
		text.setText(subContent);	
		text.setBorder(new RoundedBorder(5, Utils.themeColor));		
		text.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		text.setHighlighter(null);
		text.setEditable(false);
		text.setBounds((int) (x - (float)timelineScrollBar.getValue()*zoom), 20, size, timeline.getHeight() - 30);			
		
		text.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {				
				if (down.getClickCount() == 2)
				{
					setVideoPosition((int) ((text.getX())/zoom));					
				}			
				
			}

			@Override
			public void mousePressed(MouseEvent down) {
				
				if (control)
				{
					if (text.getForeground() == Color.RED) //Si on veut déselectionner un sub
					{
						text.setBorder(new RoundedBorder(5, Utils.themeColor));
						text.setForeground(Color.WHITE);
					}
					else
					{
						text.setBorder(new RoundedBorder(5, Color.RED));		
						text.setForeground(Color.RED);
						
						if (shift)
						{
							int index = 0;
							boolean setColor = false;
							selectedSubs.clear();
							for (Component c : timeline.getComponents())
							{
								if (c instanceof JTextPane)
								{
									index ++;
									if (c.getForeground() == Color.RED)
									{
										if (setColor == false)
											setColor = true;
										else
											setColor = false;
										selectedSubs.add(index);
									}
									else if (setColor) //Si le sub est bleu
									{
										((JComponent) c).setBorder(new RoundedBorder(5, Color.RED));		
										c.setForeground(Color.RED);
										selectedSubs.add(index);
									}
								}
							}
						}
					}
				}
				
				int index = 0;
				selectedSubs.clear();
				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						index ++;
						if (c.getForeground() == Color.RED)
							selectedSubs.add(index);
					}
				}
				
				MousePositionX = down.getPoint().x;	
				MouseTextWidth = text.getWidth();
				MouseTextLocationX = down.getXOnScreen();	
				
				if (zoom < 0.1)
				{
					enableAutoScroll = false;	
					
					setVideoPosition((int) ((down.getXOnScreen() - frame.getLocation().x - MousePositionX + timelineScrollBar.getValue())/zoom));
					
					zoom = (double) 0.1;	
					
					timeline.setSize((int) ((VideoPlayer.sliderIn.getMaximum()-(1000/FFPROBE.currentFPS)*2)*zoom), timeline.getHeight());		
					timelineScrollBar.setMaximum(timeline.getWidth() - frame.getWidth());
					
					cursor.setLocation((int) (setTime(VideoPlayer.sliderIn.getValue())*zoom), cursor.getY());
					
					enableAutoScroll = true;
					
					timeline.removeAll();
					setSubtitles(srt);			
					
					if (caseShowWaveform.isEnabled() && caseShowWaveform.isSelected())
					{
						VideoPlayer.addWaveform(true);
						waveform.setVisible(true);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (e.getClickCount() < 2 && control == false)
				{
					saveSubtitles(true, false);
				}
				
				frame.requestFocus();
				
				//Permet de relancer la boucle
				timeIn = 0;		
				
				if (control == false)
				{
					if (selectedSubs.size() != 0)
					{
						for (int index = 0 ; index < selectedSubs.size() ; index++)
						{
							((JComponent) timeline.getComponent(selectedSubs.get(index))).setBorder(new RoundedBorder(5, Utils.themeColor));
							((JComponent) timeline.getComponent(selectedSubs.get(index))).setForeground(Color.WHITE);
						}
					}
					
					selectedSubs.clear();
				}
				
				enableTimelineScroll = false;
				enableAutoScroll = true;
				
				repaintTimeline();
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
			}

			@Override
			public void mouseExited(MouseEvent e) {				
			}		

		 });
				
		text.addMouseMotionListener(new MouseMotionListener()
		{

			@Override
			public void mouseDragged(MouseEvent e) {
				int previousSubX = 0;
				int previousSubW = 0;
				int nextSub = 0;
				int nextSubX = 0;
				int nextSubW = 0;
								
				//On récupère le sous-titre précédent pour le magnet
				if (selectedSubs.size() == 0)
				{
					previousSub = number;
					for (Component c : timeline.getComponents())
					{
						if (c instanceof JTextPane)
						{
							previousSub--;
							if (c.getLocation().x + c.getWidth() <= text.getLocation().x)
							{
								previousSubX = c.getLocation().x;	
								previousSubW = c.getWidth();
								previousSub++;
							}
						}
					}
					
					//On récupère le sous-titre suivant pour lecontrolt	
					nextSub = previousSub + 2;			
					
					if (nextSub <= number)
					{
						nextSubX = timeline.getComponent(nextSub).getLocation().x;
						nextSubW = timeline.getComponent(nextSub).getWidth();
					}
				}
				else
				{
					for (int index = 0 ; index < selectedSubs.size() ; index++)
					{
						if (text.getLocation().x == timeline.getComponent(selectedSubs.get(index)).getLocation().x)
						{
							previousSub = (selectedSubs.get(index) - 1);
							previousSubX = timeline.getComponent(previousSub).getLocation().x;	
							previousSubW = timeline.getComponent(previousSub).getWidth();
							break ;
						}
					}

					if ((selectedSubs.get(selectedSubs.size() - 1) + 1) <= (timeline.getComponents().length - 1))
					{
						nextSub = (selectedSubs.get(selectedSubs.size() - 1) + 1);
						nextSubX = timeline.getComponent(nextSub).getLocation().x;	
						nextSubW = timeline.getComponent(nextSub).getWidth();	
					}
					
				}
				
				//-8 = Offset de la souris
				int mouseOffset = 8;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					mouseOffset = 0;
				
				int oldPosX = 0;
				if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{								
					if (selectedSubs.size() != 0)
						oldPosX = text.getX();								
						
					if (control && text.getX() >= cursor.getX() && cursor.getX() > previousSubX + previousSubW && controlRight == false)
						text.setLocation(cursor.getX() + mouseOffset, text.getLocation().y);
					else if (control && text.getX() <= cursor.getX() && cursor.getX() < nextSubX + nextSubW && controlRight)
						text.setLocation(cursor.getX() + mouseOffset - text.getWidth(), text.getLocation().y);
					else if (control && controlRight && text.getX() <= nextSubX)
						text.setLocation(nextSubX + mouseOffset - text.getWidth(), text.getLocation().y);
					else if (control && text.getX() >= previousSubX && text.getX() != cursor.getX())
						text.setLocation(previousSubX + previousSubW + mouseOffset, text.getLocation().y);
					else
						text.setLocation(e.getXOnScreen() - frame.getLocation().x - MousePositionX + timelineScrollBar.getValue(), text.getLocation().y);					
				}
				else if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) && text.getWidth() > 0)
				{
					text.setSize(MouseTextWidth - (e.getXOnScreen() - MouseTextLocationX), text.getHeight());
					text.setLocation(e.getXOnScreen() - frame.getLocation().x - MousePositionX + timelineScrollBar.getValue() - mouseOffset, text.getLocation().y);
						
				}
				else if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) && text.getWidth() > 0)
						text.setSize(e.getX() - mouseOffset, text.getHeight());
							
				//IMPORTANT arrondi à la bonne frame
				if (text.getCursor() != Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
				{
					int posX = (int) ((float) (text.getLocation().x - mouseOffset + (float)timelineScrollBar.getValue()*zoom)/zoom);						
					text.setLocation((int) (setTime(posX)*zoom - (float)timelineScrollBar.getValue()*zoom), text.getY());
				}
					
				if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) || text.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
				{
					int size = (int) ((float) (text.getLocation().x + text.getWidth() + mouseOffset + (float)timelineScrollBar.getValue()*zoom)/zoom);
					text.setSize((int) (setTimeFloor(size)*zoom - (float)timelineScrollBar.getValue()*zoom) - text.getX(), text.getHeight());
				}
				
				//Multisubs
				if (selectedSubs.size() != 0)
				{
					for (int index = 0 ; index < selectedSubs.size() ; index++)
					{
						if (selectedSubs.get(index) != previousSub + 1)
						{
							int offset = timeline.getComponent(previousSub + 1).getX() - oldPosX;	
							timeline.getComponent(selectedSubs.get(index)).setLocation(timeline.getComponent(selectedSubs.get(index)).getX() + offset, text.getLocation().y);
						}
					}	
				}
				else
				{						
					//Collisions
					if (text.getX() <= previousSubX + previousSubW && text.getX() > 0 - (float)timelineScrollBar.getValue()*zoom)
					{
						 timeline.getComponent(previousSub).setSize(text.getX() - previousSubX, text.getHeight());
						 if (timeline.getComponent(previousSub).getWidth() <= 0)
						 {
							 timeline.remove(previousSub);
							 number --;
						 }
					}
										
					if (text.getX() + text.getWidth() >= nextSubX && nextSubX != 0 && nextSub <= number)
					{					
						 timeline.getComponent(nextSub).setLocation(text.getX() + text.getWidth(), text.getY());
	
						 if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
						 {
							 timeline.getComponent(nextSub).setLocation(text.getX() + text.getWidth(), text.getY());
							 timeline.getComponent(nextSub).setSize(nextSubW - (timeline.getComponent(nextSub).getX() - nextSubX), text.getHeight());
						 }
					     else if (text.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
					     {
					    	 timeline.getComponent(nextSub).setLocation(text.getX() + text.getWidth(), text.getY());
					    	 timeline.getComponent(nextSub).setSize(timeline.getComponent(nextSub).getWidth() - (timeline.getComponent(nextSub).getX() - nextSubX), text.getHeight());
					     }
						 
						 if (timeline.getComponent(nextSub).getWidth() < 0)
						 {
							 timeline.remove(nextSub);
							 number --;
						 }
					}
				}
				
				if (text.getLocation().x < 0)
					text.setLocation(0,text.getLocation().y);	
				
				if ((text.getLocation().x + text.getWidth()) > timeline.getWidth())
					text.setLocation(timeline.getWidth() - text.getWidth(),text.getLocation().y);
				
				//Débordement timeline
				if (e.getXOnScreen() > (frame.getX() + frame.getWidth()))
				{
					enableTimelineScroll = true;
					enableAutoScroll = false;
					timelineScrollBar.setValue(timelineScrollBar.getValue() + (e.getXOnScreen() - (frame.getX() + frame.getWidth())));
				}
				else if (e.getXOnScreen() < frame.getX() && timelineScrollBar.getValue() > 0)
				{
					enableTimelineScroll = true;
					enableAutoScroll = false;
					timelineScrollBar.setValue(timelineScrollBar.getValue() - (frame.getX() - e.getXOnScreen()));
				}				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (selectedSubs.size() != 0)
				{
					text.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
				else
				{
					if (e.getX() > - 10 && e.getX() < 10)
						text.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					else if (e.getX() > text.getWidth() - 10 && e.getX() < text.getWidth() + 10)
						text.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					else
						text.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}
			
		});
	    return text;
	}
	
	public static void setSubtitles(File srt) {		

		//Premier lancement
		if (srt.exists() && timeline.getComponents().length <= 1)
		{
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			timeline.add(cursor, 0);
			
			int index = 1;
			
			BufferedReader reader = null;		
			
			try {
				if (srt.exists())
				{				
					reader = Files.newBufferedReader(Paths.get(srt.toString()),  StandardCharsets.UTF_8);
									
					String line;					
					while((line = reader.readLine()) != null)
					{							
						if (line.isEmpty() == false && line.matches("[0-9]+") == false)
						{
							String[] s = line.replace(",", ":").split(" ");
							
							String tcInPoint[] = s[0].split(":");
							String tcOutPoint[] = s[2].split(":");
							
							int inH = Integer.parseInt(tcInPoint[0]) * 3600000;
		    				int inM = Integer.parseInt(tcInPoint[1]) * 60000;
		    				int inS = Integer.parseInt(tcInPoint[2]) * 1000;
		    				int inF = Integer.parseInt(tcInPoint[3]);
		            		
		    				int outH = Integer.parseInt(tcOutPoint[0]) * 3600000;
		    				int outM = Integer.parseInt(tcOutPoint[1]) * 60000;
		    				int outS = Integer.parseInt(tcOutPoint[2]) * 1000;
		    				int outF = Integer.parseInt(tcOutPoint[3]);
							
							int inPoint = (int) ((float) (inH + inM + inS + inF) * zoom);
							int outPoint = (int) ((float) (outH + outM + outS + outF) * zoom);
							
							StringBuilder subContent = new StringBuilder();
							
							int i = 0;
							while ((line = reader.readLine()) != null && line.isEmpty() == false)
							{
								i++;
								if (i == 1)
									subContent.append(line);
								else
									subContent.append(System.lineSeparator() + line);
							}
							 
							if (inPoint < outPoint)							
								timeline.add(addText(subContent.toString(), (int) (inPoint + (timelineScrollBar.getValue()*zoom)), outPoint - inPoint), index);	
							else
							 	index --;
							
							if (index > 1)
							{	//Si le sous titre précédent dépasse le sous titre actuel
								if ((timeline.getComponent(index-1).getX() + timeline.getComponent(index-1).getWidth()) > inPoint)
									timeline.getComponent(index-1).setSize(inPoint - timeline.getComponent(index-1).getX(), timeline.getComponent(index-1).getHeight());
							}
							
							index ++;
						}
					}	
					
					repaintTimeline();
				}	
				} catch (Exception e) {
					System.out.println(e);
				}
				finally 
				{
					try {
						reader.close();
					} catch (IOException e) {}	
					
					//On ajoute la waveform en dernier pour quelle soit visible						
					caseShowWaveform.setEnabled(true);
					if (caseShowWaveform.isSelected())
					{
						Thread addWaveform = new Thread(new Runnable() {
							public void run() {	
								
								if (VideoPlayer.waveform.exists() == false)
								{
									while (VideoPlayer.waveform.exists() == false)
									{
										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {}
									}
									caseShowWaveform.setEnabled(true);
								}
								
								try { 	
					        		timeline.add(waveform);
					        		repaintTimeline();
								} catch (Exception e) {}
							}
						});
						addWaveform.start();
					}
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}		
		}
		else //Mise à jour du placement des subs
		{
			if (VideoPlayer.waveform.exists() && caseShowWaveform.isSelected() && waveform != null)
				timeline.remove(waveform);
						
			for (int index = number; index > 1 ; index--)
			{					
				//Si le sub actuel a un point d'entrée < au point d'entrée du sub précédent
				if (index <= timeline.getComponentCount() - 1 && timeline.getComponent(index).getX() < timeline.getComponent(index-1).getX())
				{				
					//On conserve le numéro d'index qui n'a pas la bonne référence de placement
					int newIndex = index;								
					
					//On loop jusqu'à connaitre le sub qui le précède				
					do {
						index --;
						if (index == 0)
							break;					
					} while (timeline.getComponent(index).getX() >= timeline.getComponent(newIndex).getX());

					Component toAdd = timeline.getComponent(newIndex);
					
					//Puis on définit l'index qu'il doit remplacer
					timeline.remove(newIndex);								
					timeline.add(toAdd, (index+1));	
					
					break;
				}
			}
			
			//On décale aussi la waveform
			if (VideoPlayer.waveform.exists() && caseShowWaveform.isSelected() && waveform != null)
			{
				timeline.add(waveform);
			}

			repaintTimeline();
		}
	}
	
	private static void previousSubtitle() {
		
		int time = 0;
		for (Component c : timeline.getComponents())
		{
			if (c instanceof JTextPane)
			{				
				if (c.getLocation().x < cursor.getLocation().x || c.getLocation().x + c.getWidth() < cursor.getLocation().x)
				{
					time = (int) (c.getLocation().x/zoom);
					
					if (c.getLocation().x + c.getWidth() < cursor.getLocation().x)
						time = (int) ((c.getLocation().x + c.getWidth())/zoom);
					 
				}
			}
		}
		
		setVideoPosition(time);
	}
	
	private static void nextSubtitle() {
		
		int time = 0;
		for (Component c : timeline.getComponents())
		{
			if (c instanceof JTextPane)
			{
				if (c.getLocation().x > cursor.getLocation().x)
				{
					time = (int) (c.getLocation().x/zoom);
					break;
				}
				
				if (c.getLocation().x + c.getWidth() > cursor.getLocation().x)
				{
					time = (int) ((c.getLocation().x + c.getWidth())/zoom);
					break;
				}
			}
		}
		
		if (time > 0)
		{
			setVideoPosition(time);				
		}
	}
	
	private static void addSubtitles(boolean empty) {	
		int previousSub = 0;
		int previousSubX = 0;
		int previousSubW = 0;
		int nextSubX = 0;
		boolean changeSize = false;
		
		//On récupère le sous-titre précédent pour lecontrolt
		for (Component c : timeline.getComponents())
		{
			if (c instanceof JTextPane)
			{
				previousSub++;
				if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
				{
					previousSubX = c.getLocation().x;	
					previousSubW = c.getWidth();
					break;
				}
			}
		}
		
		//On récupère le sous-titre suivant pour le magnétisme
		for (Component c : timeline.getComponents())
		{
			if (c instanceof JTextPane)
			{
				if ((int) (c.getLocation().x + (float)timelineScrollBar.getValue()*zoom) >= (int) (cursor.getLocation().x + (float)timelineScrollBar.getValue()*zoom))
				{
					nextSubX =  (int) (c.getLocation().x + (float)timelineScrollBar.getValue()*zoom);
					changeSize = true;
					break;
				}		
			}
		}
		
		int size = (int) (1000*zoom);
		if (changeSize && (int) (nextSubX - (cursor.getLocation().x + (float)timelineScrollBar.getValue()*zoom)) < size)
			size = (int) (nextSubX - (cursor.getLocation().x + (float)timelineScrollBar.getValue()*zoom));
		
		//Collision sous-titre précédent
		if (cursor.getX() != previousSubX || previousSubX == 0) //le nouveau sous-titre ne peut pas être le début d'un autre
		{
			if (cursor.getX() <= previousSubX + previousSubW && cursor.getX() > 0 - (float)timelineScrollBar.getValue()*zoom)
			{			
				timeline.getComponent(previousSub).setSize(cursor.getX() - previousSubX, timeline.getComponent(previousSub).getHeight());
				if (timeline.getComponent(previousSub).getWidth() <= 0)
					 timeline.remove(previousSub);
			}		

			if (empty)
				timeline.add(addText("Title", (int) (cursor.getLocation().x + (float)timelineScrollBar.getValue()*zoom), size));
			else
				timeline.add(addText(txtSubtitles.getText(), (int) (cursor.getLocation().x + (float)timelineScrollBar.getValue()*zoom), size));
			
			
			saveSubtitles(true, false);		
			
			//Permet de relancer la boucle
			timeIn = 0;
		}
	}
	
	private static void deleteSubtitles() {
		int n = 0;
		for (Component c : timeline.getComponents())
		{
			if (c instanceof JTextPane)
			{
				n++;
				if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
				{
					timeline.remove(n);
					saveSubtitles(true, false);
					break;
				}
			}
		}	
		
		//Permet de relancer la boucle
		timeIn = 0;
	}
	
	private static void loadBackupSubtitles() {		
		//Load backup File
		File backupFile = new File(dirTemp + "/backup_1.srt");
		
		//Restore pour CTRL + Y
		if (srt.exists() && backupFile.exists())
		{		
			if (dirTemp.exists() == false)
				dirTemp.mkdir();
			
			File restoreFile = new File(dirTemp + "/restore_1.srt");
			
			if (restoreFile.exists())
			{
				int n = 1;
				do {
					n++;
					restoreFile = new File(dirTemp + "/restore_"+ n + ".srt");
				} while (restoreFile.exists());
			}
			
			try {
				FileUtils.copyFile(srt, restoreFile);
			} catch (IOException e) {}
		}
		
		if (dirTemp.exists() && backupFile.exists())
		{			
			int n = 1;
			while (backupFile.exists())
			{
				n++;
				backupFile = new File(dirTemp + "/backup_"+ n + ".srt");
			}
			
			File fileToRestore = new File(dirTemp + "/backup_" + (n - 1) + ".srt");
			
			timeline.removeAll();
			setSubtitles(fileToRestore);
			fileToRestore.delete();
			
			saveSubtitles(false, true);
			
			//Permet de relancer la boucle
			timeIn = 0;
		}
	}
	
	private static void loadRestoreSubtitles() {
		
		File restoreFile = new File(dirTemp + "/restore_1.srt");
		
		if (dirTemp.exists() && restoreFile.exists())
		{			
			int n = 1;
			while (restoreFile.exists())
			{
				n++;
				restoreFile = new File(dirTemp + "/restore_"+ n + ".srt");
			}
			
			File fileToRestore = new File(dirTemp + "/restore_" + (n - 1) + ".srt");
			
			timeline.removeAll();
			setSubtitles(fileToRestore);
			fileToRestore.delete();
			
			saveSubtitles(true, true);
			
			//Permet de relancer la boucle
			timeIn = 0;
		}
	}
	
	private static void saveSubtitles(boolean backup, boolean restore) {
		
	if (isSaving == false)	
	{
		isSaving = true;	
		
		//Backup pour CTRL + Z
		if (srt.exists() && backup)
		{		
			if (dirTemp.exists() == false)
				dirTemp.mkdir();
						
			File backupFile = new File(dirTemp + "/backup_1.srt");
			
			if (backupFile.exists())
			{
				int n = 1;
				do {
					n++;
					backupFile = new File(dirTemp + "/backup_"+ n + ".srt");
				} while (backupFile.exists());
			}
			
			try {
				FileUtils.copyFile(srt, backupFile);
			} catch (IOException e) {}
		}
		
		//Suppression des restores
		if (dirTemp.exists() && restore == false)
		{
			File restoreFile = new File(dirTemp + "/restore_1.srt");
			
			if (restoreFile.exists())
			{
				int n = 1;
				do {
					restoreFile.delete();
					n++;
					restoreFile = new File(dirTemp + "/restore_"+ n + ".srt");
				} while (restoreFile.exists());
			}
		}			
			
		BufferedWriter writer = null;
	
		try {
				writer = Files.newBufferedWriter(Paths.get(srt.toString()),  StandardCharsets.UTF_8);
				
				int n = 0;	
				
				//On tri les sous-titres dans l'ordre de la timeline
				ArrayList<Integer> subtitles = new ArrayList<Integer>();
				for (Component c : timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						subtitles.add(c.getX());
					}
				}
				
				Collections.sort(subtitles);
				
				ArrayList<String> sortedSubtitles = new ArrayList<String>();
				for (Integer subtitle : subtitles)
				{
					for (Component c : timeline.getComponents())
					{
						if (c instanceof JTextPane)
						{
							if (c.getX() == subtitle)
							{
								if (((JTextPane) c).getText().isEmpty() == false && ((JTextPane) c).getText() != null && ((JTextPane) c).getText() != "")
									sortedSubtitles.add(c.getX() + "|" + (c.getX() + c.getWidth()) + "|" + ((JTextPane) c).getText());
								break;
							}
						}
					}
				}
	
				for (String subtitle : sortedSubtitles)
				{
					
					 	String s[] = subtitle.split("\\|");
						n++;
						//Numéro
						if (n == 1)
							writer.write(n + System.lineSeparator());
						else
							writer.write(System.lineSeparator()  + System.lineSeparator() + n + System.lineSeparator());
							
						//Timecode
						NumberFormat f = new DecimalFormat("00");
						NumberFormat f2 = new DecimalFormat("000");
						
						int inPoint = (int) (Integer.parseInt(s[0])/zoom);
						int outPoint = (int) (Integer.parseInt(s[1])/zoom);
													
						int inH = (int) (((float) inPoint / 3600000) % 60);
	    				int inM = (int) (((float) inPoint / 60000) % 60);
	    				int inS = (int) (((float) inPoint / 1000) % 60);
	    				int inF = inPoint % 1000;
	    				inF = (int) (Math.ceil((float) inF / (1000 / FFPROBE.currentFPS)) * (1000 / FFPROBE.currentFPS)); //IMPORTANT arrondi à la bonne frame
	    						            				    				
	    				int outH = (int) (((float) outPoint / 3600000) % 60);
	    				int outM = (int) (((float) outPoint / 60000) % 60);
	    				int outS = (int) (((float) outPoint / 1000) % 60);
	    				int outF = outPoint % 1000;
	    				outF = (int) (Math.ceil((float) outF / (1000 / FFPROBE.currentFPS)) * (1000 / FFPROBE.currentFPS)); //IMPORTANT arrondi à la bonne frame
	
						writer.write(//In
								f.format(inH) + ":" + f.format(inM) + ":" + f.format(inS) + "," + f2.format(inF) 
								+	//Out
								" --> " + f.format(outH) + ":" + f.format(outM) + ":" + f.format(outS) + "," + f2.format(outF) + System.lineSeparator());
						
						//Texte
						writer.write(s[2]);
				  }			
				
			} catch (IOException e1) {}
			finally {
				try {
					writer.close();
				} catch (IOException e) {}
				finally {
					subtitlesNumber();
					setSubtitles(srt); //Permet de réarranger l'ordre des subs
					isSaving = false;
					VideoPlayer.playerLeft.repaint();
				}
			}
		}
	}

	private static void setVideoPosition(int time) {
		
		VideoPlayer.playerLeftSetTime(time);						
	}
	
	public static int setTime(int rawTime) {
		//IMPORTANT arrondi à la bonne frame				
		int frames = rawTime % 1000; 
		int time = rawTime - frames;
		int F = (int) (Math.ceil((float) frames / (1000 / FFPROBE.currentFPS)) * (1000 / FFPROBE.currentFPS)); 
		return (time+F);
	}
	
	public static int setTimeFloor(int rawTime) {
		//IMPORTANT arrondi à la bonne frame				
		int frames = rawTime % 1000; 
		int time = rawTime - frames;
		int F = (int) (Math.floor((float) frames / (1000 / FFPROBE.currentFPS)) * (1000 / FFPROBE.currentFPS)); 
		return (time+F);
	}
	
	public static void subtitlesNumber() {
		BufferedReader reader = null;
		
		try {
				if (srt.exists() == false)
				{
					srt.createNewFile();
					timeline.removeAll();
					timeline.add(cursor, 0);
				}
			
    			number = 0;
				reader = Files.newBufferedReader(Paths.get(srt.toString()),  StandardCharsets.UTF_8);
				
				String line;					
				while((line = reader.readLine()) != null)
				{
					if (line.matches("[0-9]+") && Integer.parseInt(line) == (number + 1)) //permet de ne pas prendre en compte un sous titre avec des chiffres
						number = Integer.parseInt(line);					
				}
				
				frame.setTitle(Shutter.language.getProperty("frameSubtitles") + " - " + number + " " + Shutter.language.getProperty("subtitlesLower"));			
			

		}catch (Exception e){}
		finally {
			try {
				reader.close();
			}catch (Exception e) {}
		}		
		
		if (number > 0)
		{
			btnDebut.setEnabled(true);
			btnFin.setEnabled(true);
		}
	}
		
	private static void repaintTimeline() {
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				timeline.invalidate();
				timeline.validate();
				timeline.repaint();
		    }});
		
	}
	
	private void PasteFromClipBoard(){				
    	   Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
           Transferable clipTf = sysClip.getContents(null);
           if (clipTf != null) {
               if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                   try {
                	   
                	   boolean newSubtitle = true;
                	   
                	   if (txtSubtitles.getText().equals("Title"))
                		   txtSubtitles.setText((String) clipTf.getTransferData(DataFlavor.stringFlavor));
                	   else
                	   {               		  
        				   if (frame.hasFocus())
        					   txtSubtitles.setText(txtSubtitles.getText() + (String) clipTf.getTransferData(DataFlavor.stringFlavor));
                	   }
                	   	
    					for (Component c : timeline.getComponents())
    					{
    						if (c instanceof JTextPane)
    						{
    							if (cursor.getLocation().x >= c.getLocation().x && cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
    							{
    								((JTextPane) c).setText(txtSubtitles.getText());	
    								repaintTimeline();
    								newSubtitle = false;
    								break;
    							}
    						}
    					}
    					
    					if (newSubtitle)
    						addSubtitles(false);	
    					
    					if (txtSubtitles.getText().length() > 0)
    						saveSubtitles(true, false);
                   } catch (Exception er) {}
               }
           }		
		}
	}
