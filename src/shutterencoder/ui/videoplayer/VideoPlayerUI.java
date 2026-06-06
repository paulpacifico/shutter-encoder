/*******************************************************************************************
* Copyright (C) 2026 PACIFICO PAUL
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

package shutterencoder.ui.videoplayer;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.MEDIAINFO;
import shutterencoder.library.NCNN;
import shutterencoder.ui.handlers.ListFileTransferHandler;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.subtitling.SubtitlesTimeline;
import shutterencoder.utils.Utils;

public class VideoPlayerUI {
	
    //Player
	public static JPanel player;    
    private static JFrame fullscreenFrame = new JFrame();
    private static GraphicsDevice graphicsDevice;
    private static long fpsTime = 0;
    public static int fps = 0;
    public static boolean previewUpscale = false; 
    private static int displayCurrentFPS = 0;
    public static JLabel showScale;
    public static JComboBox<Object> comboPlayerQuality = new JComboBox<Object>(new String [] {"1:1", "1:2", "1:4", "auto"});
    private static JLabel showFPS;
    public static JComboBox<String> comboAudioTrack;
    public static int playerInMark = 0;
    public static int playerOutMark = 0;    
    public static double screenRefreshRate = 16.7; //Vsync in ms	
    public static boolean playerLoop = false;
    public static boolean frameIsComplete = false;
    public static boolean playerPlayVideo = true;
    public static boolean audioSetTimeIsRunning = false;
	public static boolean sliderChange = false;
	public static JLabel lblVolume;
	public static JSlider sliderVolume = new JSlider();
	public static JLabel lblPosition;
	public static JLabel lblDuration;
	private static JLabel lblMode;
	public static JComboBox<Object> comboMode = new JComboBox<Object>(new String [] {Shutter.language.getProperty("cutUpper"), Shutter.language.getProperty("removeMode"), Shutter.language.getProperty("splitMode")});
	public static JLabel lblSpeed;
	public static JSlider sliderSpeed;
	private static boolean showInfoMessage = true;
	public static boolean playTransition = false;    
    public static boolean isPiping = false;
  	public static boolean mouseIsPressed = false;
	public static boolean fullscreenPlayer = false;
	private static Thread mouseClickThread;	
	public static double fileDuration = 0;
	public static double inputFramerateMS = 40.0f;
	public static double totalFrames;
		
	//Buttons & Checkboxes
	public static JLabel btnPreview;
	public static JTextField splitValue;
	private static JLabel lblSplitSec;
	public static JButton btnPrevious;
	public static boolean previousFrame = false;
	public static JButton btnNext;
	public static JButton btnStop;
	public static JButton btnPlay;
	public static JButton btnMarkIn;
	public static JButton btnMarkOut;
	public static JButton btnGoToIn;
	public static JButton btnGoToOut;
	private static JPanel panelForButtons;
	public static JCheckBox caseApplyCutToAll = new JCheckBox(Shutter.language.getProperty("caseApplyToAll"));
	public static JCheckBox caseShowWaveform = new JCheckBox(Shutter.language.getProperty("caseShowWaveform"));
	public static JCheckBox caseVuMeter = new JCheckBox(Shutter.language.getProperty("caseVuMeter"));;
	public static JCheckBox casePlaySound = new JCheckBox(Shutter.language.getProperty("casePlaySound"));;
	public static JCheckBox caseInternalTc;
	
	//Waveform
  	public static JLabel waveformIcon;
  	public static JLabel waveformContainer;
  	public static boolean waveformContainerHasMouse = false;
  	public static JScrollPane waveformScrollPane;
  	public static JPanel cursorHead;
  	public static JPanel cursorWaveform;
  	public static JPanel cursorCurrentFrame;
  	public static int waveformZoom = 1;
		
	//grpIn
	public static JTextField caseInH;
	public static JTextField caseInM;
	public static JTextField caseInS;
	public static JTextField caseInF;
	
	//grpOut
	public static  JTextField caseOutH;
	public static  JTextField caseOutM;
	public static  JTextField caseOutS;
	public static  JTextField caseOutF;
	
	//Final time
	public static double offset = 0;
	public static int durationH = 0;
	public static int durationM = 0;
	public static int durationS = 0;
	public static int durationF = 0;
		
	//Frame by frame forward/backward
	public static boolean frameControl = false;
	public static boolean seekOnKeyFrames = false;	

	public static int MousePositionX;
	public static int MousePositionY;
		
	public VideoPlayerUI() {  	
		
		showInfoMessage = true;
		
		GraphicsConfiguration config = Shutter.frame.getGraphicsConfiguration();
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

		DisplayMode dm = allScreens[screenIndex].getDisplayMode();
		
		graphicsDevice = allScreens[screenIndex];

	    int refreshRate = dm.getRefreshRate();
	    if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN)
	    {
	    	screenRefreshRate = 16.666666;
	    }
	    else 
	    {
	    	screenRefreshRate = (double) 1000 / refreshRate;
	    }
	        						
		player();        		
		buttons();		
		sliders();	
		grpIn();
		grpOut();
		playerOptions();
						
		lblPosition = new JLabel();
		lblPosition.setHorizontalAlignment(SwingConstants.LEFT);
		lblPosition.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		lblPosition.setForeground(Utils.red);
		Shutter.frame.getContentPane().add(lblPosition);
		
		caseApplyCutToAll.setName("caseApplyCutToAll");	
		caseApplyCutToAll.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));	
		caseApplyCutToAll.setSelected(false);
		caseApplyCutToAll.setSize(caseApplyCutToAll.getPreferredSize().width, 23);
		Shutter.frame.getContentPane().add(caseApplyCutToAll);
		
		caseApplyCutToAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseApplyCutToAll.isSelected())
				{
					double timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
							
					InputAndOutput.savedInPoint = (double) Math.ceil(timeIn);	
					
					if (VideoPlayerUI.playerOutMark < VideoPlayerCore.waveformContainer.getWidth() - 2)
					{
						double totalOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
						double total = (totalFrames - totalOut); //Get how much frames to remove from totalFrames					
												
						InputAndOutput.savedOutPoint = total;					
					}
					
					if (caseInH.isVisible())
					{
						caseInH.setEnabled(false);
						caseInM.setEnabled(false);
						caseInS.setEnabled(false);
						caseInF.setEnabled(false);
					}
					if (caseOutH.isVisible())
					{
						caseOutH.setEnabled(false);
						caseOutM.setEnabled(false);
						caseOutS.setEnabled(false);
						caseOutF.setEnabled(false);
					}
				}
				else
				{
					InputAndOutput.savedInPoint = 0;		
					InputAndOutput.savedOutPoint = 0;
					
					if (caseInH.isVisible())
					{
						caseInH.setEnabled(true);
						caseInM.setEnabled(true);
						caseInS.setEnabled(true);
						caseInF.setEnabled(true);
					}
					if (caseOutH.isVisible())
					{
						caseOutH.setEnabled(true);
						caseOutM.setEnabled(true);
						caseOutS.setEnabled(true);
						caseOutF.setEnabled(true);
					}
				}
				
				waveformContainer.repaint();
			}
			
		});;
		
		lblDuration = new JLabel();
		lblDuration.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDuration.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		lblDuration.setForeground(Utils.themeColor);
		Shutter.frame.getContentPane().add(lblDuration);
		        		 
		VideoPlayerCore.setMedia();	
		
		VideoPlayerCore.totalDuration();
							
		//Arrows control
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
					
	        public void eventDispatched(AWTEvent event)
	        {				        	
	        	KeyEvent e = (KeyEvent) event;
	        		        	
	        	if (Shutter.caseAddWatermark.isSelected())
	        	{
	        		if (e.getID() == KeyEvent.KEY_PRESSED)
	        		{          	  	
	        			boolean update = false;
	        			
						if (e.getKeyCode() == KeyEvent.VK_UP)
						{
							Shutter.logo.setLocation(Shutter.logo.getLocation().x, Shutter.logo.getLocation().y - 1);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_DOWN)
						{
							Shutter.logo.setLocation(Shutter.logo.getLocation().x, Shutter.logo.getLocation().y + 1);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_LEFT)
						{
							Shutter.logo.setLocation(Shutter.logo.getLocation().x - 1, Shutter.logo.getLocation().y);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						{
							Shutter.logo.setLocation(Shutter.logo.getLocation().x + 1, Shutter.logo.getLocation().y);
							update = true;
						} 

						if (update)
						{
							Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.playerRatio) ) ) );
							Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.playerRatio) ) ) );  
							Shutter.logoLocX = Shutter.logo.getLocation().x;
							Shutter.logoLocY = Shutter.logo.getLocation().y;
							
							Shutter.watermarkPreset = null;
						}
	        		}
	        	}
	        	else
	        	{
					int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - Shutter.frame.getLocation().x - Shutter.frameCropX;
					int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - Shutter.frame.getLocation().y - Shutter.frameCropY;
									
		        	if (mouseInPictureX > 0 && mouseInPictureX < player.getWidth() && mouseInPictureY > 0 && mouseInPictureY < player.getHeight())
		        	{
		        		if (e.getID() == KeyEvent.KEY_PRESSED)
		        		{           	  
							if (e.getKeyCode() == KeyEvent.VK_SHIFT)
								Shutter.shift = true;
							
							if (e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH)
								Shutter.alt = true;
							
							if (e.getKeyCode() == KeyEvent.VK_CONTROL)
								Shutter.ctrl = true;
							
							if (e.getKeyCode() == KeyEvent.VK_UP)
							{
								Shutter.selection.setLocation(Shutter.selection.getLocation().x, Shutter.selection.getLocation().y - 1);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_DOWN)
							{
								Shutter.selection.setLocation(Shutter.selection.getLocation().x, Shutter.selection.getLocation().y + 1);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_LEFT)
							{
								Shutter.selection.setLocation(Shutter.selection.getLocation().x - 1, Shutter.selection.getLocation().y);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_RIGHT)
							{
								Shutter.selection.setLocation(Shutter.selection.getLocation().x + 1, Shutter.selection.getLocation().y);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
							{
								Shutter.selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);
								Shutter.anchorRight = Shutter.selection.getLocation().x + Shutter.selection.getWidth();
								Shutter.anchorBottom = Shutter.selection.getLocation().y + Shutter.selection.getHeight();	
							}	 
							
							VideoPlayerOverlay.checkSelection();
		        		}
		              
		        	}
		        	
		        	if (e.getID() == KeyEvent.KEY_RELEASED)
		        	{
		        		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		        			Shutter.shift = false;  
		        		
		        		if (e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH)
							Shutter.alt = false;
		        		
		        		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		        			Shutter.ctrl = false;
		        	}
	        	}
	        }

	    }, AWTEvent.KEY_EVENT_MASK);
    	
		KeyListener keyListener = new KeyListener(){

			@Override	
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
									
				//Volume
				if (e.getKeyCode() == 109 || e.getKeyCode() == 107)
				{
					boolean resfreshSliderSpeed = false;
					
					//Volume up
					if (e.getKeyCode() == 107 && sliderSpeed.getValue() < 4)
					{
						sliderSpeed.setValue(sliderSpeed.getValue() + 1);
						resfreshSliderSpeed = true;
					}
					
					//Volume down
					if (e.getKeyCode() == 109 && sliderSpeed.getValue() > 0)
					{
						sliderSpeed.setValue(sliderSpeed.getValue() - 1);			
						resfreshSliderSpeed = true;
					}
					
					if (resfreshSliderSpeed)
					{
						if (sliderSpeed.getValue() == 0)
						{
							lblSpeed.setText("x0.25");
						}
						else if (sliderSpeed.getValue() == 1)
						{
							lblSpeed.setText("x0.5");
						}
						else if (sliderSpeed.getValue() == 2)
						{
							lblSpeed.setText("x1");
						}
						else if (sliderSpeed.getValue() == 3)
						{
							lblSpeed.setText("x1.5");
						}
						else if (sliderSpeed.getValue() == 4)
						{
							lblSpeed.setText("x2");
						}
						
						lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
										
						if (VideoPlayerCore.playerCurrentFrame > 0)
						{
							frameIsComplete = false;
										
							VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
						}	
					}
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_K || e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					e.consume();
					btnPlay.doClick();
				}

				if (Shutter.shift)
				{				
					if (e.getKeyCode() == KeyEvent.VK_NUMPAD0)
					{
						e.consume();
						VideoPlayerCore.playerSetTime(0);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD1)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 1);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 2);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 3);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD4)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 4);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD5)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 5);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD6)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 6);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD7)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 7);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 8);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD9)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) (totalFrames / 10) * 9);
					}
				}
				
				if (e.getKeyCode() == KeyEvent.VK_J)
				{
					previousFrame = true;
					VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame - 10));
  				}
					
				if (e.getKeyCode() == KeyEvent.VK_L)
				{
					previousFrame = true;
					VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame + 10));
				}
								
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{											
					if (e.getID() == KeyEvent.KEY_PRESSED)
	        		{           	  
						if (e.getKeyCode() == KeyEvent.VK_SHIFT)
							Shutter.shift = true;
						
						if (e.getKeyCode() == KeyEvent.VK_ALT)
							Shutter.alt = true;
	        		}
					
					if (e.getKeyCode() == KeyEvent.VK_HOME)
					{
						e.consume();
						VideoPlayerCore.playerSetTime(0);
					}
						
					if (e.getKeyCode() == KeyEvent.VK_END)
					{
						e.consume();
						VideoPlayerCore.playerSetTime((double) totalFrames - 2);
					}
					
					if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
					{
						btnGoToIn.doClick();
					}
					
					if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN )
					{
						btnGoToOut.doClick();
					}
					
					if (e.getKeyCode() == KeyEvent.VK_I)
					{
						if (Shutter.shift)
						{						
							btnGoToIn.doClick();
						}
						else
						{							
							btnMarkIn.doClick();
						}
					}
										
					if (e.getKeyCode() == KeyEvent.VK_O)
					{
						if (Shutter.shift)
						{
							btnGoToOut.doClick();
						}
						else if (cursorWaveform.getX() > playerInMark)
						{
							btnMarkOut.doClick();
						}
					}
				}
				
				if (VideoPlayerCore.playerVideo != null)
				{					
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
					{					
						if (Shutter.shift)
						{
							previousFrame = true;
							VideoPlayerCore.playerSetTime((double) VideoPlayerCore.playerCurrentFrame - Math.ceil(FFPROBE.accurateFPS));
						}
						else if (Shutter.alt)
						{
							previousFrame = true;
							VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame - Math.ceil(FFPROBE.accurateFPS) * 10));
						}
						else
						{
							waveformContainer.requestFocus();
							btnPrevious.doClick();							
							waveformScrollPane.getHorizontalScrollBar().setValue(waveformScrollPane.getHorizontalScrollBar().getValue() + 10);
						}
	  				}
					
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					{						
						if (Shutter.shift)
						{
							previousFrame = true;
							VideoPlayerCore.playerSetTime((double) VideoPlayerCore.playerCurrentFrame + Math.ceil(FFPROBE.accurateFPS));
						}
						else if (Shutter.alt)
						{
							previousFrame = true;
							VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame + Math.ceil(FFPROBE.accurateFPS) * 10));
						}
						else
						{
							waveformContainer.requestFocus();
							btnNext.doClick();
							waveformScrollPane.getHorizontalScrollBar().setValue(waveformScrollPane.getHorizontalScrollBar().getValue() - 10);
						}
	  				}
					
					if (e.getKeyCode() == KeyEvent.VK_UP)
						btnGoToOut.doClick();	
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN)
						btnGoToIn.doClick();					
				}				
				      
			}

			@Override
			public void keyReleased(KeyEvent e) {			
				
				if (e.getID() == KeyEvent.KEY_RELEASED)
	        	{
					if (e.getKeyCode() == KeyEvent.VK_SHIFT)
						Shutter.shift = false;
					
					if (e.getKeyCode() == KeyEvent.VK_ALT)
						Shutter.alt = false;
	        	}
			}
			
		};
		
		for (Component c : Shutter.frame.getContentPane().getComponents())
		{
			c.addKeyListener(keyListener);
		}
		
		//IMPORTANT
		waveformContainer.addKeyListener(keyListener);
						
    	Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	   
    public static void setInfo() {
    	    	
    	String tff = "";
		if (FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
		{
			if (FFPROBE.fieldOrder.equals("0"))
			{
				tff = " TFF";
			}
			else
				tff = " BFF";
		}

		if (FFPROBE.videoCodec != null && fileDuration > 40 && Shutter.inputDeviceIsRunning == false)
		{
			String vcodec = FFPROBE.videoCodec.replace("video", "");
			for (String s : Shutter.functionsList)
			{
				if (vcodec.toLowerCase().equals(s.replace(".", "").replace("-", "").toLowerCase())
				|| s.toLowerCase().contains(vcodec.toLowerCase()))
				{
					vcodec = s;
					break;
				}
				else
					vcodec = vcodec.toUpperCase();
			}

			showScale.setText(FFPROBE.imageResolution + " " + vcodec + tff + " " + FFPROBE.imageDepth + "-bit");
		}
		else
			showScale.setText(FFPROBE.imageResolution + tff);
		
		showScale.repaint();
		showFPS.repaint();
    }
    
    public static void setPlayerButtons(boolean enable) {

		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			waveformScrollPane.setVisible(true);
			waveformIcon.setVisible(true);
			cursorHead.setVisible(true);
			caseInH.setVisible(false);
			caseInM.setVisible(false);
			caseInS.setVisible(false);
			caseInF.setVisible(false);
			caseOutH.setVisible(false);
			caseOutM.setVisible(false);
			caseOutS.setVisible(false);
			caseOutF.setVisible(false);
			lblDuration.setVisible(false);
			
			if (Shutter.list.getSize() > 0)
			{
				lblPosition.setVisible(true);
			}
			else
				lblPosition.setVisible(false);
						
			lblVolume.setVisible(true);
			sliderVolume.setVisible(true);
			lblSpeed.setVisible(true);
			sliderSpeed.setVisible(true);
			lblMode.setVisible(false);
			comboMode.setVisible(false);
			btnPreview.setVisible(false);
			splitValue.setVisible(false);
			lblSplitSec.setVisible(false);
			btnGoToIn.setVisible(false);
			btnMarkIn.setVisible(false);
			btnPlay.setVisible(true);
			btnPrevious.setVisible(true);
			btnNext.setVisible(true);
			btnStop.setVisible(true);
			btnMarkOut.setVisible(false);
			btnGoToOut.setVisible(false);
			panelForButtons.setVisible(true);
			caseApplyCutToAll.setVisible(false);
			caseInternalTc.setVisible(false);
			caseShowWaveform.setVisible(false);
			caseVuMeter.setVisible(true);
			casePlaySound.setVisible(true);
			showScale.setVisible(false);
			comboPlayerQuality.setVisible(true);
			comboAudioTrack.setVisible(false);
		}
		else if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected() || enable == false) //Image or disableAll()
		{			
			waveformScrollPane.setVisible(false);
			waveformIcon.setVisible(false);
			cursorHead.setVisible(false);
			caseInH.setVisible(false);
			caseInM.setVisible(false);
			caseInS.setVisible(false);
			caseInF.setVisible(false);
			caseOutH.setVisible(false);
			caseOutM.setVisible(false);
			caseOutS.setVisible(false);
			caseOutF.setVisible(false);
			lblDuration.setVisible(false);
			lblPosition.setVisible(false);
			lblVolume.setVisible(false);
			sliderVolume.setVisible(false);
			lblSpeed.setVisible(false);
			sliderSpeed.setVisible(false);
			lblMode.setVisible(false);
			comboMode.setVisible(false);
			btnPreview.setVisible(false);
			splitValue.setVisible(false);
			lblSplitSec.setVisible(false);
			btnGoToIn.setVisible(false);
			btnMarkIn.setVisible(false);
			
			if (Shutter.list.getSize() > 0 && VideoPlayerCore.videoPath != null && fullscreenPlayer == false && isPiping == false && Settings.btnDisableVideoPlayer.isSelected() == false)
			{
				showScale.setVisible(true);
			}
			else
				showScale.setVisible(false);
			
			comboPlayerQuality.setVisible(false);
			showFPS.setVisible(false);
						
			if (Shutter.caseEnableSequence.isSelected() && enable)
				btnPlay.setVisible(true);
			else
				btnPlay.setVisible(false);
			
			btnPrevious.setVisible(false);
			btnNext.setVisible(false);
			
			if (Shutter.caseEnableSequence.isSelected() && enable)
				btnStop.setVisible(true);
			else
				btnStop.setVisible(false);
			
			btnMarkOut.setVisible(false);
			btnGoToOut.setVisible(false);
			panelForButtons.setVisible(false);
			caseApplyCutToAll.setVisible(false);
			caseInternalTc.setVisible(false);
			caseShowWaveform.setVisible(false);
			caseVuMeter.setVisible(false);
			casePlaySound.setVisible(false);
			comboAudioTrack.setVisible(false);
			
			if (Shutter.caseEnableSequence.isSelected() && enable)
			{	
				Shutter.caseAddSubtitles.setEnabled(true);
			}
		}
		else if (Shutter.frame.getSize().width > 654)
		{	
			if (FFPROBE.audioOnly)
			{
				Shutter.caseVideoFadeIn.setEnabled(false);
				Shutter.caseVideoFadeOut.setEnabled(false);
			}
			else
			{
				Shutter.caseVideoFadeIn.setEnabled(true);
				if (Shutter.caseVideoFadeIn.isSelected())
					Shutter.spinnerVideoFadeIn.setEnabled(true);
				Shutter.caseVideoFadeOut.setEnabled(true);
				if (Shutter.caseVideoFadeOut.isSelected())
					Shutter.spinnerVideoFadeOut.setEnabled(true);
			}
													
			caseInH.setVisible(true);
			caseInM.setVisible(true);
			caseInS.setVisible(true);
			caseInF.setVisible(true);
			caseOutH.setVisible(true);
			caseOutM.setVisible(true);
			caseOutS.setVisible(true);
			caseOutF.setVisible(true);
			
			if (Shutter.list.getSize() > 0)
			{
				lblDuration.setVisible(true);
				lblPosition.setVisible(true);
			}
			else
			{
				lblDuration.setVisible(false);
				lblPosition.setVisible(false);
			}
			
			if (Shutter.frame.getWidth() >= 1320)
			{
				lblVolume.setVisible(true);
				sliderVolume.setVisible(true);
				lblSpeed.setVisible(true);
				sliderSpeed.setVisible(true);
			}
			
			lblMode.setVisible(true);
			comboMode.setVisible(true);
			
			if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
			{
				btnPreview.setVisible(true);
				splitValue.setVisible(false);
				lblSplitSec.setVisible(false);
			}
			else if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("splitMode")))
			{
				btnPreview.setVisible(false);
				splitValue.setVisible(true);
				lblSplitSec.setVisible(true);
			}
			else
			{
				btnPreview.setVisible(true);
				splitValue.setVisible(false);
				lblSplitSec.setVisible(false);
			}
			
			btnGoToIn.setVisible(true);
			btnMarkIn.setVisible(true);
			btnPlay.setVisible(true);
			btnPrevious.setVisible(true);
			btnNext.setVisible(true);
			btnStop.setVisible(true);
			btnMarkOut.setVisible(true);
			btnGoToOut.setVisible(true);
			panelForButtons.setVisible(true);
			
			waveformScrollPane.setVisible(true);
			cursorHead.setVisible(true);
			
			if (Shutter.caseEnableSequence.isSelected())
			{
				caseApplyCutToAll.setVisible(false);
			}
			else
				caseApplyCutToAll.setVisible(true);
			
			if (FFPROBE.hasAudio)
			{
				caseShowWaveform.setVisible(true);
				caseVuMeter.setVisible(true);												
				casePlaySound.setVisible(true);
				
				if (caseShowWaveform.isSelected())
					waveformIcon.setVisible(true);				
				
				//Add Audio tracks
				comboAudioTrack.removeAllItems();
				if (FFPROBE.channels > 1 && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
				{	
					for (int i = 0 ; i < FFPROBE.channels ; i++)
					{
						comboAudioTrack.addItem(Shutter.language.getProperty("audio").toUpperCase().substring(0, 1) + "" + (i + 1));
					}
					comboAudioTrack.addItem("Mix");
					comboAudioTrack.setVisible(true);
				}
				else
					comboAudioTrack.setVisible(false);
			}
			else
			{
				caseShowWaveform.setVisible(false);
				caseVuMeter.setVisible(false);												
				casePlaySound.setVisible(false);
				waveformIcon.setVisible(false);
				comboAudioTrack.setVisible(false);
			}
			
			if (FFPROBE.audioOnly || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
			{
				caseInternalTc.setVisible(false);
				casePlaySound.setBounds(caseInternalTc.getX(), caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);	
				
				showScale.setVisible(false);
				comboPlayerQuality.setVisible(false);
				showFPS.setVisible(false);
			}
			else
			{
				caseInternalTc.setVisible(true);
				caseInternalTc.setBounds(caseInH.getX() - 2, btnPrevious.getY() + btnPrevious.getHeight() + 6, caseInternalTc.getPreferredSize().width, 23);	
				casePlaySound.setBounds(caseInternalTc.getX() + caseInternalTc.getWidth() + 4, caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);
								
				if (Shutter.list.getSize() > 0 && Settings.btnDisableVideoPlayer.isSelected() == false)
				{
					showScale.setVisible(true);
				}
				
				comboPlayerQuality.setVisible(true);
			}
			
			Shutter.caseAddSubtitles.setEnabled(true);
							
			//Timecode			
			if (Shutter.caseShowTimecode.isSelected() || caseInternalTc.isSelected())
			{
				if (FFPROBE.timecode1.equals(""))
				{
					if (Shutter.caseShowTimecode.isSelected())
					{
						Shutter.caseShowTimecode.setSelected(false);
						Shutter.caseAddTimecode.doClick();
					}
					
					if (caseInternalTc.isSelected())
					{
						caseInternalTc.setSelected(false);
						offset = 0;
					}
				}
				else
				{
					if (Shutter.caseShowTimecode.isSelected())
					{
						Shutter.TC1.setEnabled(false);
						Shutter.TC2.setEnabled(false);
						Shutter.TC3.setEnabled(false);
						Shutter.TC4.setEnabled(false);
						Shutter.caseAddTimecode.setSelected(false);					
						player.add(Shutter.timecode);
						
						//Shutter.overImage need to be the last component added
						if (Shutter.caseEnableCrop.isSelected())
						{
							player.remove(Shutter.selection);
							player.remove(Shutter.overImage);
							player.add(Shutter.selection);
							player.add(Shutter.overImage);
						}
					}
					
					if (caseInternalTc.isSelected())
					{
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600 * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode2) * 60 * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode3) * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode4);				
					}
				}
			}
		}		
    }
    
	private void buttons() {		 
    	
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));
		btnPrevious.setBackground(new Color(30,30,35, 0));
		btnPrevious.setBorder(null);
		Shutter.frame.getContentPane().add(btnPrevious);
				
		btnPrevious.addActionListener(new ActionListener(){
			
			int i = 0;
			
			@Override
			public void actionPerformed(ActionEvent e) {

				previousFrame = true;
				
				i ++;

				if (i <= 1)
				{
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
									
							if (sliderSpeed.getValue() != 2)
							{
								sliderSpeed.setValue(2);
								lblSpeed.setText("x1");
								lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
								VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame - 1);
							}
										
							frameControl = true;
														
							if (VideoPlayerCore.playerVideo != null)
							{										
								if (playerLoop)
								{
									btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));
									btnPlay.setName("play");
									playerLoop = false;
								}

								frameIsComplete = false;
								
								if (seekOnKeyFrames && FFPROBE.isRunning == false)
								{				
									FFPROBE.Keyframes(VideoPlayerCore.videoPath, (VideoPlayerCore.playerCurrentFrame - 2) * inputFramerateMS, false);
									
									do {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {}
									} while (FFPROBE.isRunning);
									
									if (FFPROBE.keyFrame > 0)
									{
										VideoPlayerCore.playerSetTime(FFPROBE.keyFrame);
									}
								}
								else if (VideoPlayerCore.bufferedFrames.size() > 0)
								{
									VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame - 2);	
								}
								else
								{									
									VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame - 1);	
									
									while (VideoPlayerCore.setTime.isAlive())
									{
										try {
											Thread.sleep(1);
										} catch (InterruptedException e) {}
									}
								}
								
							}	
							
							i = 0;
						}
						
					});
					t.start();
				}
			}	
			
		});
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));	
		btnNext.setBackground(new Color(30,30,35, 0));
		btnNext.setBorder(null);
		Shutter.frame.getContentPane().add(btnNext);
		
		btnNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sliderSpeed.getValue() != 2)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText("x1");
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame + 1);
				}
				
				if (VideoPlayerCore.preview != null || Shutter.caseAddSubtitles.isSelected())
				{												
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame + 1);
				}

				frameControl = true;
				
				if (VideoPlayerCore.playerVideo != null)
				{
					if (playerLoop)
					{
						btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));
						btnPlay.setName("play");
						playerLoop = false;
					}
					
					if (seekOnKeyFrames && FFPROBE.isRunning == false)
					{									
						FFPROBE.Keyframes(VideoPlayerCore.videoPath, (VideoPlayerCore.playerCurrentFrame + 1) * inputFramerateMS, true);
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException er) {}
						} while (FFPROBE.isRunning);
		
						if (FFPROBE.keyFrame > 0)
						{	
							VideoPlayerCore.playerSetTime(FFPROBE.keyFrame);
						}
					}
					else
					{					
						//Allow to read 1 frame							
						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame + 1);
					}
				}	
			}
			
		});
		
		btnPlay = new JButton(new FlatSVGIcon("resources/play.svg", 15, 15));
		btnPlay.setMargin(new Insets(0,0,0,0));		
		btnPlay.setBackground(new Color(30,30,35, 0));
		btnPlay.setBorder(null);
		Shutter.frame.getContentPane().add(btnPlay);
			
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//Allows to wait for the last frame to load
				if (VideoPlayerCore.setTime != null)
				{
					while (VideoPlayerCore.setTime.isAlive())
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}						
					}			
				}
				
				if (btnPlay.getName().equals("pause"))
				{
					btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));	
					btnPlay.setName("play");
					playerLoop = false;
					showFPS.setVisible(false);
					
					if (sliderSpeed.getValue() != 2)
					{						
						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);	
					}		
					else
					{
						//IMPORTANT Sync to the correct timecode
						if (Timecode.isDropFrame() == false)
						{
							VideoPlayerCore.getTimePoint(VideoPlayerCore.playerCurrentFrame); 
						}
					}
					
				}
				else if (btnPlay.getName().equals("play"))
				{		
					if (VideoPlayerCore.bufferedFrames.size() > 0 || VideoPlayerCore.preview != null || previousFrame || Shutter.caseAddSubtitles.isSelected())
					{				
						if (VideoPlayerCore.bufferedFrames.size() > 0 || VideoPlayerCore.preview != null)
						{	
							//Clear the buffer
							VideoPlayerCore.bufferedFrames.clear();		
							waveformContainer.repaint();
						}
						
						if (previousFrame)
						{
							//IMPORTANT enable GPU decoding after using btnPrevious
							previousFrame = false;
						}
												
						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
					}
					
					//Loop the player
					if (VideoPlayerCore.playerCurrentFrame >= totalFrames - 2)
					{
						VideoPlayerCore.playerSetTime(0);
						btnPlay.doClick();
					}
					
					frameControl = false;
					btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					btnPlay.setName("pause");
					
					if (VideoPlayerCore.preview == null)
						playerLoop = true;
					
		            fpsTime = System.nanoTime();
		            displayCurrentFPS = 0;
				}
								
			}
			
		});
		
		btnStop = new JButton(new FlatSVGIcon("resources/stop.svg", 15, 15));		
		btnStop.setMargin(new Insets(0,0,0,0));	
		btnStop.setBackground(new Color(30,30,35, 0));
		btnStop.setBorder(null);
		Shutter.frame.getContentPane().add(btnStop);		
		
		btnStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (VideoPlayerCore.playerVideo != null)
				{				
					VideoPlayerCore.playerCurrentFrame = 0;
					
					long time = System.currentTimeMillis();
					
					if (VideoPlayerCore.playerVideo != null)
					{				
						VideoPlayerCore.playerStop();						
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
							
							if (System.currentTimeMillis() - time > 5000)
								break;
							
						} while (VideoPlayerCore.playerVideo.isAlive());
												
						VideoPlayerCore.playerSetTime(0);						
					}
										
					resizeAll();

					btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));
					btnPlay.setName("play");
					playerLoop = false;
										
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						SubtitlesTimeline.actualSubOut = 0;	

					VideoPlayerCore.playerCurrentFrame = 0;		
					
					waveformScrollPane.getHorizontalScrollBar().setValue(0);
				}
				else if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
				{
					resizeAll();
				}
			}			
			
		});

		btnMarkIn = new JButton("[");
		btnMarkIn.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));	
		btnMarkIn.setBackground(new Color(30,30,35, 0));
		btnMarkIn.setBorder(null);
		Shutter.frame.getContentPane().add(btnMarkIn);
		
		btnMarkIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
					
				if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
				{
					playerInMark = cursorWaveform.getX();
					waveformContainer.repaint();			
					
					if (VideoPlayerCore.bufferCurrentFrame > 0)
					{
						VideoPlayerCore.updateGrpIn(VideoPlayerCore.bufferCurrentFrame);		
					}
					else
						VideoPlayerCore.updateGrpIn(VideoPlayerCore.playerCurrentFrame);
					
					Shutter.timecode.repaint();
					
					//FileList
					VideoPlayerCore.setFileList();
				}
			}
			
		});
		
		btnGoToIn = new JButton("[<");
		btnGoToIn.setMargin(new Insets(0,0,0,0));
		btnGoToIn.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));	
		btnGoToIn.setBackground(new Color(30,30,35, 0));
		btnGoToIn.setBorder(null);
		Shutter.frame.getContentPane().add(btnGoToIn);
		
		btnGoToIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {		

				playTransition = true;

				//Clear the buffer
				if (VideoPlayerCore.bufferedFrames.size() > 0)
				{		
					VideoPlayerCore.bufferedFrames.clear();
					VideoPlayerCore.bufferCurrentFrame = 0;
					waveformContainer.repaint();
				}
				
				VideoPlayerCore.playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * VideoPlayerCore.getFPS() + Integer.parseInt(caseInF.getText());
				
				//NTSC framerate
				VideoPlayerCore.playerCurrentFrame = Timecode.getNTSCtimecode(VideoPlayerCore.playerCurrentFrame);
				VideoPlayerCore.playerCurrentFrame = Timecode.getDropFrameTimecode(VideoPlayerCore.playerCurrentFrame);
				
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
			}
			
		});
		
		btnMarkOut = new JButton("]");
		btnMarkOut.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));		
		btnMarkOut.setBackground(new Color(30,30,35, 0));
		btnMarkOut.setBorder(null);
		Shutter.frame.getContentPane().add(btnMarkOut);
		
		btnMarkOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
				{
					playerOutMark = cursorWaveform.getX();
					waveformContainer.repaint();
					
					if (VideoPlayerCore.bufferCurrentFrame > 0)
					{
						VideoPlayerCore.updateGrpOut(VideoPlayerCore.bufferCurrentFrame + 1);		
					}
					else
						VideoPlayerCore.updateGrpOut(VideoPlayerCore.playerCurrentFrame + 1);	
					
					VideoPlayerCore.setMarkers();

					//FileList
					VideoPlayerCore.setFileList();
				}
			}
			
		});
				
		btnGoToOut = new JButton(">]");
		btnGoToOut.setMargin(new Insets(0,0,0,0));
		btnGoToOut.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));			
		btnGoToOut.setBackground(new Color(30,30,35, 0));
		btnGoToOut.setBorder(null);
		Shutter.frame.getContentPane().add(btnGoToOut);
		
		btnGoToOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Clear the buffer
				if (VideoPlayerCore.bufferedFrames.size() > 0)
				{		
					VideoPlayerCore.bufferedFrames.clear();
					VideoPlayerCore.bufferCurrentFrame = 0;
					waveformContainer.repaint();
				}
				
				VideoPlayerCore.playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * VideoPlayerCore.getFPS() + Integer.parseInt(caseOutF.getText())  - 1;

				//NTSC framerate
				VideoPlayerCore.playerCurrentFrame = Timecode.getNTSCtimecode(VideoPlayerCore.playerCurrentFrame);
				VideoPlayerCore.playerCurrentFrame = Timecode.getDropFrameTimecode(VideoPlayerCore.playerCurrentFrame);
								
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
			}
			
		});
   
		panelForButtons = new JPanel() {
		
				@Override
				public void paintComponent(Graphics g) {
					
					Graphics2D g2d = (Graphics2D) g;
												
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			        
					g2d.setColor(Utils.c42);					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					{
						g2d.fillRoundRect(0, 0, (btnStop.getX() + btnStop.getWidth()) - btnPlay.getX() - 4, 21, 15, 15);
						
						g2d.setColor(Utils.c50);
						g2d.drawRoundRect(0, 0, (btnStop.getX() + btnStop.getWidth()) - btnPlay.getX() - 5, 20, 15, 15);
					}
					else
					{
						g2d.fillRoundRect(0, 0, (btnGoToOut.getX() + btnGoToOut.getWidth()) - btnGoToIn.getX() - 4, 21, 15, 15);
						
						g2d.setColor(Utils.c50);
						g2d.drawRoundRect(0, 0, (btnGoToOut.getX() + btnGoToOut.getWidth()) - btnGoToIn.getX() - 5, 20, 15, 15);
					}
									
					g2d.setColor(new Color(25,25,25));
					g2d.drawLine(this.getWidth() / 2, 5, this.getWidth() / 2, this.getHeight() - 6);
				}
				
		};
		Shutter.frame.getContentPane().add(panelForButtons);

		showFPS = new JLabel("25 fps");
		showFPS.setVisible(false);
		showFPS.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));
		showFPS.setHorizontalAlignment(SwingConstants.RIGHT);
		Shutter.frame.getContentPane().add(showFPS);
		
		comboPlayerQuality.setName("comboPlayerQuality");
		comboPlayerQuality.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		comboPlayerQuality.setMaximumRowCount(4);
		comboPlayerQuality.setSize(50, 16);
		Shutter.frame.getContentPane().add(comboPlayerQuality);
		
		comboPlayerQuality.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
			}
			
		});
		
		showScale = new JLabel("1920x1080");
		showScale.setVisible(false);
		showScale.setEnabled(false);
		showScale.setFont(new Font(Shutter.mainFont, Font.BOLD, 12));
		showScale.setHorizontalAlignment(SwingConstants.LEFT);
		Shutter.frame.getContentPane().add(showScale);
		
		comboAudioTrack = new JComboBox<String>(new String[] { (Shutter.language.getProperty("audio").toUpperCase().substring(0, 1) + "1") });
		comboAudioTrack.setName("comboAudioTrack");
		comboAudioTrack.setOpaque(false);
		comboAudioTrack.setVisible(false);
		comboAudioTrack.setEditable(false);
		comboAudioTrack.putClientProperty("FlatLaf.style", "borderWidth: 0; focusWidth: 0;");
		comboAudioTrack.setBackground(new Color(comboAudioTrack.getBackground().getRed(),comboAudioTrack.getBackground().getGreen(),comboAudioTrack.getBackground().getBlue(), 100));	
		comboAudioTrack.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		comboAudioTrack.setMaximumRowCount(16);		

		comboAudioTrack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				VideoPlayerCore.addWaveform(true);
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
			}
			
		});		
	}
	
	private void player() {		

		player = new JPanel() {

		    // Cached resources
		    private int cachedWidth = -1;
		    private int cachedHeight = -1;

		    private Font cachedFontPlain;
		    private Font cachedFontItalic;
		    private Font cachedFontBold;
		    private Font cachedFontItalicBold;
		    private int cachedFontHeight = -1;
		    
		    private final Color colorC1 = new Color(200, 200, 200);
		    private final Color colorC2 = new Color(255, 255, 255);
		    private TexturePaint checkerPaint;
		    
		    private BufferedImage mask;
		    private int lastW, lastH;

		    @Override
		    protected void paintComponent(Graphics g) {

		        super.paintComponent(g);

		        int w = getWidth();
		        int h = getHeight();
		        
		        // Values for rounded mask
		        if (w != lastW || h != lastH) {
		            roundedMask(w, h);
		            lastW = w;
		            lastH = h;
		        }

		        if (w <= 0 || h <= 0) return;

		        Graphics2D g2 = (Graphics2D) g;

		        // Recreate cached images only on resize
		        if (w != cachedWidth || h != cachedHeight) {
		            cachedWidth  = w;
		            cachedHeight = h;
		        }

		        // Draw into buffer
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_SPEED);
		        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		        // Checkerboard drawn into buffer, not directly to screen
		        if (FFPROBE.hasAlpha)
		            drawCheckerboard(g2);

		        g2.setColor(Color.BLACK);

		        // Frame display
		        if (VideoPlayerCore.frameVideo == null
		        || Shutter.list.getSize() == 0
		        || (Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction"))
		        && Shutter.caseDisplay.isSelected() == false))
		        {
		            g2.fillRect(0, 0, w, h);
		        }
		        else
		        {		
		            g2.drawImage(VideoPlayerCore.frameVideo, 0, 0, w, h, this);

		            if (VideoPlayerCore.playerIsPlaying()) //Handled directly from setTime
		            {
		            	cursorCurrentFrame.setLocation((int) Math.floor((double) (waveformContainer.getWidth() * Timecode.setNTSCtimecode(VideoPlayerCore.playerCurrentFrame)) / totalFrames), 0);
		            }
		        }
		        
		        // Mask display
		        if (!fullscreenPlayer) {
		            g2.drawImage(mask, 0, 0, null);
		        }

		        // FPS display
		        if (FFPROBE.audioOnly == false && fileDuration > 40)
		        {
		            if (System.nanoTime() - fpsTime >= 1_000_000_000L) {
		                displayCurrentFPS = fps;
		                fpsTime = System.nanoTime();
		                fps = 0;
		            }

		            if (displayCurrentFPS > 0
                    && playerLoop
                    && sliderSpeed.getValue() == 2
                    && fullscreenPlayer == false
                    && mouseIsPressed == false
                    && Shutter.inputDeviceIsRunning == false)
		            {
		                showFPS.setVisible(true);
		                if ((double) displayCurrentFPS >= FFPROBE.currentFPS)
		                {
		                    showFPS.setForeground(Color.GREEN);
		                    String[] fpsParts = String.valueOf(FFPROBE.currentFPS).split("\\.");
		                    showFPS.setText(
		                        (fpsParts[1].equals("0")
		                            ? fpsParts[0]
		                            : String.valueOf(FFPROBE.currentFPS))
		                        + " " + Shutter.language.getProperty("fps")
		                    );
		                }
		                else
		                {
		                    showFPS.setForeground(Color.RED);
		                    showFPS.setText(displayCurrentFPS + " " + Shutter.language.getProperty("fps"));
		                }
		            }
		            else
		            {
		                showFPS.setVisible(false);
		            }
		        }

		        // Preview upscale label
		        if (previewUpscale && VideoPlayerCore.frameVideo != null && VideoPlayerCore.preview != null && fileDuration > 40) {
		            Font f = getCachedFont(Font.ITALIC, h);
		            g2.setFont(f);
		            FontMetrics metrics = g2.getFontMetrics(f);
		            String label = Shutter.language.getProperty("preview");
		            int x = (w - metrics.stringWidth(label)) / 2;
		            int y = h - (h / 24);
		            g2.setColor(Color.WHITE);
		            g2.drawString(label, x, y);
		        }

		        // Subtitles
		        if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		        {
		            SubtitlesTimeline.refreshData();		            
		            String rawSub = SubtitlesTimeline.txtSubtitles.getText();
		            
	                if (rawSub != null && !rawSub.isEmpty())
	                {
			            String[] text = SubtitlesTimeline.txtSubtitles.getText().split("\\r?\\n");
	
			            Font firstFont = getFontForLine(text[0], h);
			            g2.setFont(firstFont);
			            String firstLine = stripTags(text[0]);
	
			            FontMetrics metrics = g2.getFontMetrics(firstFont);
			            int x = (w - metrics.stringWidth(firstLine)) / 2;
			            int y;
	
			            if (text.length > 1 && text[1].length() > 0) {
			                y = h - (int) (h / 9.5);
			                drawSubtitleString(g2, firstLine, x, y);
	
			                Font secondFont = getFontForLine(text[1], h);
			                g2.setFont(secondFont);
			                String secondLine = stripTags(text[1]);
			                FontMetrics metrics2 = g2.getFontMetrics(secondFont);
			                x = (w - metrics2.stringWidth(secondLine)) / 2;
			                y = h - (h / 24);
			                drawSubtitleString(g2, secondLine, x, y);
			            } else if (firstLine.length() > 0) {
			                y = h - (h / 24);
			                drawSubtitleString(g2, firstLine, x, y);
			            }
	                }
		        }
		    }
		    
		    /** Mask for rounded borders */
		    private void roundedMask(int w, int h) {
		        mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2 = mask.createGraphics();

		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
		        
		        g2.setColor(Utils.background); 
		        g2.fillRect(0, 0, w, h);
		        
		        g2.setComposite(AlphaComposite.Clear);
		        g2.fillRoundRect(0, 0, w, h, 10, 10);
		        
		        g2.dispose();
		    }

		    /** Draws a subtitle string with a 1px black outline then white fill. */
		    private void drawSubtitleString(Graphics2D g2, String text, int x, int y) {
		        g2.setColor(Color.BLACK);
		        g2.drawString(text, x - 1, y - 1);
		        g2.drawString(text, x - 1, y + 1);
		        g2.drawString(text, x + 1, y - 1);
		        g2.drawString(text, x + 1, y + 1);
		        g2.setColor(Color.WHITE);
		        g2.drawString(text, x, y);
		    }

		    /** Strips basic HTML italic/bold tags from a subtitle line. */
		    private String stripTags(String line) {
		        return line.replace("<i>", "").replace("</i>", "")
		                   .replace("<b>", "").replace("</b>", "");
		    }

		    /** Returns the appropriate cached font for a subtitle line based on its tags. */
		    private Font getFontForLine(String line, int panelHeight) {
		        boolean italic = line.contains("i>");
		        boolean bold   = line.contains("b>");
		        if (italic && bold) return getCachedFont(Font.ITALIC | Font.BOLD, panelHeight);
		        if (italic)         return getCachedFont(Font.ITALIC,             panelHeight);
		        if (bold)           return getCachedFont(Font.BOLD,               panelHeight);
		        return                     getCachedFont(Font.PLAIN,              panelHeight);
		    }

		    /** Returns a cached Font, rebuilding only when panel height changes. */
		    private Font getCachedFont(int style, int panelHeight) {
		        int size = (int) Math.floor(panelHeight / 16.0);
		        if (size != cachedFontHeight) {
		            cachedFontHeight  = size;
		            cachedFontPlain    = new Font("SansSerif", Font.PLAIN,              size);
		            cachedFontItalic   = new Font("SansSerif", Font.ITALIC,             size);
		            cachedFontBold     = new Font("SansSerif", Font.BOLD,               size);
		            cachedFontItalicBold = new Font("SansSerif", Font.ITALIC | Font.BOLD, size);
		        }
		        switch (style) {
		            case Font.ITALIC:             return cachedFontItalic;
		            case Font.BOLD:               return cachedFontBold;
		            case Font.ITALIC | Font.BOLD: return cachedFontItalicBold;
		            default:                      return cachedFontPlain;
		        }
		    }

		    /** Draws a grey/white checkerboard pattern to indicate transparency. */
		    private void drawCheckerboard(Graphics2D g2) {
		    	if (checkerPaint == null) {
	                BufferedImage anchor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
	                Graphics2D gr = anchor.createGraphics();
	                gr.setColor(colorC1); gr.fillRect(0, 0, 8, 8); gr.fillRect(8, 8, 8, 8);
	                gr.setColor(colorC2); gr.fillRect(8, 0, 8, 8); gr.fillRect(0, 8, 8, 8);
	                gr.dispose();
	                checkerPaint = new TexturePaint(anchor, new Rectangle(0, 0, 16, 16));
	            }
	            g2.setPaint(checkerPaint);
	            g2.fillRect(0, 0, getWidth(), getHeight());
		    }
		};

        // Drag & Drop
 		player.setTransferHandler(new ListFileTransferHandler());
        
 		player.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2)
				{					
					toggleFullscreen();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {		
				
				if (fullscreenPlayer && isPiping == false)
				{
					mouseIsPressed = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (fullscreenPlayer && isPiping == false)
				{					
					if (e.getClickCount() < 2)
					{						
						mouseClickThread = new Thread(new Runnable() {
	
							@Override
							public void run() {

								try {
									
									//Wait to check simple or double click
									Thread.sleep(500);
									
									mouseIsPressed = false;
									
									sliderChange = false;								
									
									//Reload the frame to apply bicubic filter			
									if (VideoPlayerCore.setTime != null)
									{
										do {
											Thread.sleep(1);
										} while (VideoPlayerCore.setTime.isAlive());
									}
										
									VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
									
								} catch (InterruptedException e) {}
							}
							
						});
						mouseClickThread.start();	
					}
					else
					{
						if (mouseClickThread != null && mouseClickThread.isAlive())
						{
							mouseClickThread.interrupt();
						}
					}
				}
			}
 			
 		});

 		player.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (fullscreenPlayer && isPiping == false)
				{					
					double value = (double) totalFrames * e.getX() / player.getWidth();
					
					sliderChange = true;					
					cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
					
					if (value < totalFrames)
						VideoPlayerCore.playerSetTime(value);
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
 			
 		});
 		
 		player.setOpaque(false);
		player.setLayout(null);
		player.setBackground(Color.BLACK);
		Shutter.frame.getContentPane().add(player);		
	}
	
	public static void toggleFullscreen() {
				
		//Avoid glitch when resizing while playing
		if (VideoPlayerCore.playerIsPlaying())
			playerLoop = false;
		
		Shutter.windowDrag = true;
		
		if (fullscreenPlayer == false)
	    {			
			fullscreenPlayer = true;

			fullscreenFrame.getContentPane().setLayout(null);
	        fullscreenFrame.setFocusableWindowState(true);	   
	        fullscreenFrame.getContentPane().setBackground(Color.BLACK);
	        fullscreenFrame.setTitle(new File(VideoPlayerCore.videoPath).getName());		
	        fullscreenFrame.setUndecorated(true);
	        fullscreenFrame.add(player);
	        
	        Shutter.frame.setVisible(false);
	        graphicsDevice.setFullScreenWindow(fullscreenFrame);
	        	        
	        fullscreenFrame.setVisible(true);
	        		
	        resizeAll();
	        
			if (isPiping == false)
			{
				if (fileDuration <= 40 || Shutter.comboResolution.getSelectedItem().toString().contains("AI"))
				{	
					if (VideoPlayerCore.preview != null)
						VideoPlayerCore.preview = null;
					
					VideoPlayerCore.loadImage(true);
				}
				else						
				{
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
				}
			}
	    }
	    else
	    {
	    	fullscreenPlayer = false;
	    	
	        graphicsDevice.setFullScreenWindow(null);
	        fullscreenFrame.dispose();
	        fullscreenFrame.setVisible(false);
	        Shutter.frame.getContentPane().add(player);
	        Shutter.frame.getContentPane().revalidate();
	        Shutter.frame.setVisible(true);
	        
	        if (isPiping == false)
			{							
	    		mouseIsPressed = false;
	    		
	    		if (fileDuration <= 40 || Shutter.comboResolution.getSelectedItem().toString().contains("AI"))
				{	
					if (VideoPlayerCore.preview != null)
						VideoPlayerCore.preview = null;
					
					VideoPlayerCore.loadImage(true);
				}
				else
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame			
			}
	        
	        resizeAll();
	        
	        if (isPiping == false)
	    		btnPlay.requestFocus();
	    }
		
		Shutter.windowDrag = false;
	}

	private void sliders() {
				
		waveformContainer = new JLabel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {		
	        	Graphics2D g2 = (Graphics2D)g;
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            
	            //Background
	            if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
	            {
	            	g2.setColor(Utils.darkenColor);
	            	
	            	//Mask in                               	
	                g2.fillRoundRect(0, 0, playerInMark + 1, getHeight() - 1, 5, 5);
	                
	                //Mask out     
	                g2.fillRoundRect(playerOutMark + 1, 0, getWidth() - playerOutMark - 1, getHeight() - 1, 5, 5);
	            }
	            else
	            {
	            	g2.setColor(new Color(25, 27, 30));
	            	g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
	            }

	            //Borders
                g2.setColor(Utils.c25);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);	
                                
                if (Shutter.list.getSize() > 0 && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
                {
	                //Mark in & out
                	if (caseApplyCutToAll.isSelected())
	                {
                		g2.setColor(Color.GRAY);          
	                }
                	else
                		g2.setColor(Utils.darkenColor);
	                	                
	                if (playerOutMark > waveformContainer.getWidth() - 2)
	                	playerOutMark = waveformContainer.getWidth() - 2;	 
	                
	                if (playerInMark < 0)
	                	playerInMark = 0;
	                
	               	if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")) == false)
	               	{
	               		g2.fillRoundRect(playerInMark + 1, 0, playerOutMark - playerInMark - 1, getHeight() - 1, 5, 5);	
	               	}
	                
	                //Splitters
	                if (comboMode.isVisible() && comboMode.getSelectedItem().equals(Shutter.language.getProperty("splitMode")))
	                {
		                g2.setColor(new Color(25, 27, 30));
		                int alpha = 255;
		                int splitTime = (int) (playerInMark + Math.floor((double) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.accurateFPS / totalFrames)));
		                do {
		                	
		                	g2.fillRect(splitTime + 1, 0, 1, getHeight() - 1);
		                	
		                	splitTime += Math.floor((double) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.accurateFPS / totalFrames));
		                	
		                	g2.setColor(new Color(25, 27, 30, alpha));
		                	
		                	alpha -= 10;
		                	
		                	if (alpha < 0)
		                		break;		             
           	
		                } while (splitTime < playerOutMark);
	                }
	                	
	                //Waveform
	                if (waveformIcon.getIcon() != null && waveformIcon.isVisible()) {
	                    waveformIcon.getIcon().paintIcon(this, g2, 0, 0);
	                }
	                
	                //Masks 
	                if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
	                {
	                	g2.setColor(new Color(24, 26, 29, 200));
	                	g2.fillRoundRect(playerInMark + 1, 1, playerOutMark - playerInMark, getHeight() - 2, 5, 5);
	                }
	                else
	                {	     
	                	g2.setColor(new Color(26, 27, 31, 200));
	                	
		                //Mask in                               	
		                g2.fillRoundRect(0, 0, playerInMark + 1, getHeight() - 1, 5, 5);
		                
		                //Mask out     
		                g2.fillRoundRect(playerOutMark + 1, 0, getWidth() - playerOutMark - 1, getHeight() - 1, 5, 5);
	                }
	                
	                //Buffer
	                if (VideoPlayerCore.bufferedFrames.size() > 0)	                	
	                {		              	                	
	                	g2.setColor(Color.GREEN);
	                	
			            double bufferPosition = (VideoPlayerCore.playerCurrentFrame - VideoPlayerCore.bufferedFrames.size() + 1);       
			            
			            int x = (int) Math.floor((double) (waveformContainer.getSize().width * bufferPosition) / totalFrames);
			            int width = (int) Math.floor((double) (waveformContainer.getSize().width * VideoPlayerCore.bufferedFrames.size()) / totalFrames);
			            
			            g2.drawLine(x, 1, x + width, 1);
	                }
	                
	                VideoPlayerCore.totalDuration();
                }			
		    }
		};
		
		if (Shutter.noSettings)
		{
			waveformContainer.setSize((Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth()) * waveformZoom, 40);
		}
		else
			waveformContainer.setSize((Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2) * waveformZoom, 40);
		
		waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));		
		waveformContainer.setLayout(null);
		Shutter.frame.getContentPane().add(waveformContainer);
		
		waveformContainer.add(comboAudioTrack);
						
		cursorHead = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics grphcs) {
		        super.paintComponent(grphcs);
		        Graphics2D g2d = (Graphics2D) grphcs;
		        
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		        
		        int[] xPoints = {0, getWidth(), getWidth(), getWidth() / 2, 0};
		        int[] yPoints = {0, 0, getHeight() - 6, getHeight(), getHeight() - 6}; 
		        
		        //Fill
		        g2d.setColor(Utils.red);
		        g2d.fillPolygon(xPoints, yPoints, 5);

		    }
		};

		cursorHead.setOpaque(false);
		cursorHead.setSize(10, 9); 
		waveformContainer.add(cursorHead);
		
		cursorWaveform = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics grphcs) {
	            super.paintComponent(grphcs);
	            Graphics2D g2d = (Graphics2D) grphcs;
	            
	            g2d.setColor(Utils.red);
	            g2d.drawLine(0, getWidth(), 0, getHeight());	
	        }
		};
	
		cursorWaveform.setBounds(0, 0, 1, waveformContainer.getSize().height - 1);		
		waveformContainer.add(cursorWaveform);	
			   
		waveformIcon = new JLabel();
		waveformIcon.setOpaque(false);
		waveformIcon.setLayout(null);
		waveformIcon.setSize(waveformContainer.getSize());

		//Important
		playerOutMark = waveformContainer.getWidth() - 2;
				
		waveformContainer.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {

				waveformContainerHasMouse = true;			
			}

			@Override
			public void mouseExited(MouseEvent e) {					
				waveformContainerHasMouse = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				mouseIsPressed = true;
				
				if (NCNN.isRunning)
				{
					NCNN.process.destroy();
					
					if (VideoPlayerCore.preview != null)
						VideoPlayerCore.preview = null;
				}
								
				if (Shutter.list.getSize() > 0)
                {
					//IMPORTANT
					waveformContainer.requestFocus();
					
					sliderChange = true;
					
					if (VideoPlayerCore.playerIsPlaying())
					{
						btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));	
						btnPlay.setName("pause");
					}
					
					if (e.getX() >= 0 && e.getX() <= waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
					}
					else if (e.getX() < 0)
					{				
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);							
					}
					
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
					
					double value = (double) totalFrames * cursorWaveform.getLocation().x / waveformContainer.getSize().width;

					//NTSC framerate
					value = Timecode.getNTSCtimecode(value);
					
					VideoPlayerCore.playerSetTime(value);
					
					//Allows to wait for the last frame to load					
					while (VideoPlayerCore.setTime.isAlive())
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}						
					}	
                }
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				mouseIsPressed = false;

				if (Shutter.list.getSize() > 0)
                {						
					sliderChange = false;								

					//Reload the frame to apply bicubic filter					
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (VideoPlayerCore.setTime.isAlive());

					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);	
					
					//Allows to wait for the last frame to load
					while (VideoPlayerCore.setTime.isAlive())
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}						
					}
					
					VideoPlayerCore.setMarkers();
					
					if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && cursorWaveform.getX() < playerOutMark && mouseIsPressed)
					{							
						cursorWaveform.setLocation(playerInMark, 0);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						if (VideoPlayerCore.bufferCurrentFrame > 0)
						{
							VideoPlayerCore.updateGrpIn(VideoPlayerCore.bufferCurrentFrame);		
						}
						else
							VideoPlayerCore.updateGrpIn(VideoPlayerCore.playerCurrentFrame);
					}
					else if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && cursorWaveform.getX() > playerInMark && mouseIsPressed)
					{			
						cursorWaveform.setLocation(playerOutMark, 0);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						if (VideoPlayerCore.bufferCurrentFrame > 0)
						{
							VideoPlayerCore.updateGrpOut(VideoPlayerCore.bufferCurrentFrame);		
						}
						else
							VideoPlayerCore.updateGrpOut(VideoPlayerCore.playerCurrentFrame);
					}	
					
					waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					//FileList
					VideoPlayerCore.setFileList();
                }								
			}
			
		});		

		waveformContainer.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {
									
				if (Shutter.list.getSize() > 0)
                {					
					if (e.getX() > 0 && e.getX() <= waveformContainer.getWidth() - 2)
					{
						sliderChange = true;					
						cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());

						double value = (double) totalFrames * cursorWaveform.getLocation().x / waveformContainer.getSize().width;

						//NTSC framerate
						value = Timecode.getNTSCtimecode(value);
						
						if (value < totalFrames)
							VideoPlayerCore.playerSetTime(value);
					}
					else if (e.getX() <= 0)
					{					
						sliderChange = true;					
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);	
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						VideoPlayerCore.playerSetTime(0);
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{
						sliderChange = true;					
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						VideoPlayerCore.playerSetTime(totalFrames);
					}
					
					if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && cursorWaveform.getX() < playerOutMark && mouseIsPressed)
					{
						playerInMark = cursorWaveform.getX();
						waveformContainer.repaint();
					}
					else if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && cursorWaveform.getX() > playerInMark && mouseIsPressed)
					{
						playerOutMark = cursorWaveform.getX();
						waveformContainer.repaint();
					}

					if (cursorWaveform.getX() > waveformScrollPane.getWidth() + waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX() - (waveformContainer.getWidth() / waveformZoom) + 1);
					}
					else if (cursorWaveform.getX() < waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX());
					}
					
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
                }
			}

			@Override
			public void mouseMoved(MouseEvent e) {	
				
				if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
				{
					if (e.getX() >= playerInMark - 5 && e.getX() <= playerInMark + 5)
					{
						waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));						
					}
					else if (e.getX() >= playerOutMark - 5 && e.getX() <= playerOutMark + 5)
					{
						waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					}
					else
						waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		
		});
		
		waveformContainer.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
								
				if (e.getPreciseWheelRotation() < 0)
				{
		            if (waveformZoom < 100)
		            {
		            	if (waveformZoom >= 20)
		            	{
		            		waveformZoom += 10;
		            	}
		            	else
		            		waveformZoom += 1;
		            }
		        }
				else
				{
					if (waveformZoom > 1)
		            {
						if (waveformZoom >= 20)
		            	{
		            		waveformZoom -= 10;
		            	}
		            	else
		            	{
		            		waveformZoom -= 1;
		            	}
		            }
		        }
				
				waveformContainer.setSize(waveformScrollPane.getWidth() * waveformZoom, waveformScrollPane.getHeight() - waveformScrollPane.getHorizontalScrollBar().getHeight());
				waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));	
				
				VideoPlayerCore.setMarkers();
				
				if (VideoPlayerCore.bufferCurrentFrame > 0)
				{
					cursorWaveform.setLocation((int) Math.floor((double) (waveformContainer.getWidth() * Timecode.setNTSCtimecode(VideoPlayerCore.bufferCurrentFrame)) / totalFrames), 0);
				}
				else
					cursorWaveform.setLocation((int) Math.floor((double) (waveformContainer.getWidth() * Timecode.setNTSCtimecode(VideoPlayerCore.playerCurrentFrame)) / totalFrames), 0);
					
				cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
				
				cursorCurrentFrame.setLocation(cursorWaveform.getX(), 0);
			
				if (waveformIcon.isVisible() && waveformIcon.getIcon() != null)
				{
					waveformIcon.setSize(waveformContainer.getSize());
					
					ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(VideoPlayerCore.waveform).getImage().getScaledInstance(waveformContainer.getWidth(), waveformContainer.getHeight(), Image.SCALE_AREA_AVERAGING));
					waveformIcon.setIcon(resizedWaveform);
					waveformIcon.repaint();
				}
				
				//Center cursor
				int viewportWidth = waveformScrollPane.getViewport().getWidth();
				int newViewPosX = cursorWaveform.getX() - (viewportWidth / 2);
				int maxViewPosX = waveformContainer.getWidth() - viewportWidth;
				newViewPosX = Math.max(0, Math.min(newViewPosX, maxViewPosX));
				waveformScrollPane.getViewport().setViewPosition(new Point(newViewPosX, 0));
			}
			
		});
					
		cursorCurrentFrame = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics grphcs) {
	            super.paintComponent(grphcs);
	            Graphics2D g2d = (Graphics2D) grphcs;
	            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));	            
	            
	            g2d.setColor(Utils.red);
	            g2d.drawLine(0, 0, 0, waveformContainer.getHeight());
	        }
		};
		cursorCurrentFrame.setOpaque(false);
		waveformContainer.add(cursorCurrentFrame);
				
		waveformScrollPane = new JScrollPane();
		waveformScrollPane.getViewport().add(waveformContainer);
		waveformScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		waveformScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		waveformScrollPane.getViewport().setOpaque(false);
		waveformScrollPane.setBorder(null);
	    Shutter.frame.getContentPane().add(waveformScrollPane, BorderLayout.CENTER);
		
		sliderVolume.setName("sliderVolume");	
		sliderVolume.setVisible(false);
		sliderVolume.setValue(50);
		Shutter.frame.getContentPane().add(sliderVolume);
				
		sliderVolume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if (VideoPlayerCore.playerAudio != null && VideoPlayerCore.playerAudio.isAlive())
				{
					float gain = (float) sliderVolume.getValue() / 100;   
					float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
			        if (VideoPlayerCore.gainControl != null)
			        	VideoPlayerCore.gainControl.setValue(dB);
				}
			}
			
		});
		
		lblVolume = new JLabel(new FlatSVGIcon("resources/volume.svg", 15, 15));
		lblVolume.setFont(new Font("", Font.PLAIN, 12));
		lblVolume.setVisible(false);
		lblVolume.setSize(lblVolume.getPreferredSize().width + 3, 16);			
		lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, sliderVolume.getY() + 2);	
		
		lblVolume.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1)
				{
					if (sliderVolume.getValue() > 0)
					{
						sliderVolume.setValue(0);
					}
					else
						sliderVolume.setValue(50);
				}
				else if (e.getButton() == MouseEvent.BUTTON3)
				{
					AudioFormat format = new AudioFormat(48000, 16, 2, true, false);
			        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			        JPopupMenu popupList = new JPopupMenu();
			        			                
			        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
			        for (int i = 0; i < mixers.length; i++) {
			            Mixer mixer = AudioSystem.getMixer(mixers[i]);
			            if (mixer.isLineSupported(info)) {
			            	
			            	Mixer.Info currentMixerInfo = mixers[i];
			                
			                JMenuItem item = new JMenuItem(currentMixerInfo.getName());
			                item.addActionListener(ev -> {
			                    Mixer selected = AudioSystem.getMixer(currentMixerInfo);
			                    VideoPlayerCore.audioHardwareOutput = selected;
			                    VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
			                });
			            	
			            	popupList.add(item);
			            }
			        }
			        
			        popupList.show(lblVolume, e.getX() - 30, e.getY());
				}
				
			}
			
		});	
		
		Shutter.frame.getContentPane().add(lblVolume);
	}
	
	private void grpIn(){
				
		caseInH = new JTextField();
		caseInH.setName("caseInH");
		caseInH.setText("00");
		caseInH.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInH.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseInH.setColumns(10);
		Shutter.frame.getContentPane().add(caseInH);
		
		caseInM = new JTextField();
		caseInM.setName("caseInM");
		caseInM.setText("00");
		caseInM.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInM.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseInM.setColumns(10);
		Shutter.frame.getContentPane().add(caseInM);
		
		caseInS = new JTextField();
		caseInS.setName("caseInS");
		caseInS.setText("00");
		caseInS.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInS.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseInS.setColumns(10);
		caseInS.setBounds(caseInM.getX() + caseInM.getWidth() + 2, 17, 21, 21);
		Shutter.frame.getContentPane().add(caseInS);
		
		caseInF = new JTextField();
		caseInF.setName("caseInF");
		caseInF.setText("00");
		caseInF.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInF.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseInF.setColumns(10);
		Shutter.frame.getContentPane().add(caseInF);
				
		caseInH.setBounds(waveformScrollPane.getX() - 2, btnPrevious.getY(), 21, 21);
		caseInM.setBounds(caseInH.getX() + caseInH.getWidth(), caseInH.getY(), 21, 21);
		caseInS.setBounds(caseInM.getX() + caseInM.getWidth(), caseInH.getY(), 21, 21);
		caseInF.setBounds(caseInS.getX() + caseInS.getWidth(), caseInH.getY(), 21, 21);	
		
		sliderSpeed = new JSlider();
		sliderSpeed.setMaximum(4);
		sliderSpeed.setValue(2);
		sliderSpeed.setVisible(false);
		sliderSpeed.setMinorTickSpacing(1);
		sliderSpeed.setMajorTickSpacing(1);
		sliderSpeed.setSize(80, 22);
	
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(sliderSpeed);

		sliderSpeed.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (sliderSpeed.isEnabled())
				{
					if (e.getX() < 20)
					{
						sliderSpeed.setValue(0);
						lblSpeed.setText("x0.25");
					}
					else if (e.getX() > 10 && e.getX() < 30)
					{
						sliderSpeed.setValue(1);
						lblSpeed.setText("x0.5");
					}
					else if (e.getX() > 30 && e.getX() < 50)
					{
						sliderSpeed.setValue(2);
						lblSpeed.setText("x1");
					}
					else if (e.getX() > 50 && e.getX() < 70)
					{
						sliderSpeed.setValue(3);
						lblSpeed.setText("x1.5");
					}
					else if (e.getX() > 70)
					{
						sliderSpeed.setValue(4);
						lblSpeed.setText("x2");
					}
					
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {	
				
			}
			
		});
		
		sliderSpeed.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2 && sliderSpeed.isEnabled())
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText("x1");
					
					if (VideoPlayerCore.playerCurrentFrame > 0)
					{
						frameIsComplete = false;
									
						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);	
					}					
				}
			}	
			
			@Override
			public void mouseReleased(MouseEvent e) {
					
				if (sliderSpeed.isEnabled())
				{
					if (e.getX() < 20)
					{
						sliderSpeed.setValue(0);
						lblSpeed.setText("x0.25");
					}
					else if (e.getX() > 10 && e.getX() < 30)
					{
						sliderSpeed.setValue(1);
						lblSpeed.setText("x0.5");
					}
					else if (e.getX() > 30 && e.getX() < 50)
					{
						sliderSpeed.setValue(2);
						lblSpeed.setText("x1");
					}
					else if (e.getX() > 50 && e.getX() < 70)
					{
						sliderSpeed.setValue(3);
						lblSpeed.setText("x1.5");
					}
					else if (e.getX() > 70)
					{
						sliderSpeed.setValue(4);
						lblSpeed.setText("x2");
					}
					
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
									
					if (VideoPlayerCore.playerCurrentFrame > 0)
					{
						frameIsComplete = false;
									
						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);	
					}	
				}
			}

		});
				
		lblSpeed = new JLabel("x1"); //0.25 allow to get max preferred size width
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		lblSpeed.setVisible(false);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(lblSpeed);
		
		caseInH.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (caseInH.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseInH.setText(String.valueOf(Integer.parseInt(caseInH.getText()) + 1));
						
						if (caseInH.getText().length() == 1)
							caseInH.setText("0" + caseInH.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseInH.setText(String.valueOf(Integer.parseInt(caseInH.getText()) - 1));
						
						if (caseInH.getText().length() == 1)
							caseInH.setText("0" + caseInH.getText());
					}
				}
				
				if (caseInH.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseInH.getText().length() == 1)
						caseInH.setText("0" + caseInH.getText());

					VideoPlayerCore.updateTimeIn();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (caseInH.getText().length() >= 2)
					caseInH.setText("");				
			}			
			
		});
		
		caseInH.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseInH.getText().length() == 1)
					caseInH.setText("0" + caseInH.getText());	
			}
			
		});
		
		caseInM.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseInM.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseInM.setText(String.valueOf(Integer.parseInt(caseInM.getText()) + 1));
						
						if (caseInM.getText().length() == 1)
							caseInM.setText("0" + caseInM.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseInM.setText(String.valueOf(Integer.parseInt(caseInM.getText()) - 1));
						
						if (caseInM.getText().length() == 1)
							caseInM.setText("0" + caseInM.getText());
					}
				}
				
				if (caseInM.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseInM.getText().length() == 1)
						caseInM.setText("0" + caseInM.getText());

					VideoPlayerCore.updateTimeIn();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (caseInM.getText().length() >= 2)
					caseInM.setText("");				
			}
			
		});
		
		caseInM.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseInM.getText().length() == 1)
					caseInM.setText("0" + caseInM.getText());		
			}
			
		});
		
		caseInS.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseInS.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseInS.setText(String.valueOf(Integer.parseInt(caseInS.getText()) + 1));
						
						if (caseInS.getText().length() == 1)
							caseInS.setText("0" + caseInS.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseInS.setText(String.valueOf(Integer.parseInt(caseInS.getText()) - 1));
						
						if (caseInS.getText().length() == 1)
							caseInS.setText("0" + caseInS.getText());
					}
				}
				
				if (caseInS.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseInS.getText().length() == 1)
						caseInS.setText("0" + caseInS.getText());				

					VideoPlayerCore.updateTimeIn();
				}						
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (caseInS.getText().length() >= 2)
					caseInS.setText("");
			}
			
		});
		
		caseInS.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseInS.getText().length() == 1)
					caseInS.setText("0" + caseInS.getText());
			}
			
		});
		
		caseInF.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseInF.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseInF.setText(String.valueOf(Integer.parseInt(caseInF.getText()) + 1));
						
						if (caseInF.getText().length() == 1)
							caseInF.setText("0" + caseInF.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseInF.setText(String.valueOf(Integer.parseInt(caseInF.getText()) - 1));
						
						if (caseInF.getText().length() == 1)
							caseInF.setText("0" + caseInF.getText());
					}
				}
				
				if (caseInF.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseInF.getText().length() == 1)
						caseInF.setText("0" + caseInF.getText());

					VideoPlayerCore.updateTimeIn();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume();
				else if (caseInF.getText().length() >= 2)
					caseInF.setText("");	
			}
			
		});
	
		caseInF.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseInF.getText().length() == 1)
					caseInF.setText("0" + caseInF.getText());		
			}
			
		});
	
	}
	
	private void grpOut(){
		
		caseOutH = new JTextField();
		caseOutH.setName("caseOutH");
		caseOutH.setText("00");
		caseOutH.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutH.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseOutH.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutH);
				
		caseOutM = new JTextField();
		caseOutM.setName("caseOutM");
		caseOutM.setText("00");
		caseOutM.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutM.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseOutM.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutM);
		
		caseOutS = new JTextField();
		caseOutS.setName("caseOutS");
		caseOutS.setText("00");
		caseOutS.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutS.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseOutS.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutS);
		
		caseOutF = new JTextField();
		caseOutF.setName("caseOutF");
		caseOutF.setText("00");
		caseOutF.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutF.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		caseOutF.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutF);
				
		caseOutH.setBounds(waveformScrollPane.getX() + waveformScrollPane.getWidth() - 21 * 4, caseInH.getY(), 21, 21);
		caseOutM.setBounds(caseOutH.getX() + caseOutH.getWidth(), caseOutH.getY(), 21, 21);
		caseOutS.setBounds(caseOutM.getX() + caseOutM.getWidth(), caseOutH.getY(), 21, 21);
		caseOutF.setBounds(caseOutS.getX() + caseOutS.getWidth(), caseOutH.getY(), 21, 21);
		
		caseOutH.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (caseOutH.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseOutH.setText(String.valueOf(Integer.parseInt(caseOutH.getText()) + 1));
						
						if (caseOutH.getText().length() == 1)
							caseOutH.setText("0" + caseOutH.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseOutH.setText(String.valueOf(Integer.parseInt(caseOutH.getText()) - 1));
						
						if (caseOutH.getText().length() == 1)
							caseOutH.setText("0" + caseOutH.getText());
					}
				}

				if (caseOutH.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseOutH.getText().length() == 1)
						caseOutH.setText("0" + caseOutH.getText());
					
					VideoPlayerCore.updateTimeOut();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 	
				else if (caseOutH.getText().length() >= 2)
					caseOutH.setText("");	
			}
			
		});
		
		caseOutH.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseOutH.getText().length() == 1)
					caseOutH.setText("0" + caseOutH.getText());	
			}
			
		});
		
		caseOutM.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseOutM.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseOutM.setText(String.valueOf(Integer.parseInt(caseOutM.getText()) + 1));
						
						if (caseOutM.getText().length() == 1)
							caseOutM.setText("0" + caseOutM.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseOutM.setText(String.valueOf(Integer.parseInt(caseOutM.getText()) - 1));
						
						if (caseOutM.getText().length() == 1)
							caseOutM.setText("0" + caseOutM.getText());
					}
				}
				
				if (caseOutM.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseOutM.getText().length() == 1)
						caseOutM.setText("0" + caseOutM.getText());

					VideoPlayerCore.updateTimeOut();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (caseOutM.getText().length() >= 2)
					caseOutM.setText("");				
			}
			
		});
		
		caseOutM.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseOutM.getText().length() == 1)
					caseOutM.setText("0" + caseOutM.getText());		
			}
			
		});
		
		caseOutS.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseOutS.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseOutS.setText(String.valueOf(Integer.parseInt(caseOutS.getText()) + 1));
						
						if (caseOutS.getText().length() == 1)
							caseOutS.setText("0" + caseOutS.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseOutS.setText(String.valueOf(Integer.parseInt(caseOutS.getText()) - 1));
						
						if (caseOutS.getText().length() == 1)
							caseOutS.setText("0" + caseOutS.getText());
					}
				}
				
				if (caseOutS.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseOutS.getText().length() == 1)
						caseOutS.setText("0" + caseOutS.getText());

					VideoPlayerCore.updateTimeOut();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (caseOutS.getText().length() >= 2)
					caseOutS.setText("");
			}
			
		});
		
		caseOutS.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseOutS.getText().length() == 1)
					caseOutS.setText("0" + caseOutS.getText());	
			}
			
		});
		
		caseOutF.addKeyListener(new KeyListener(){
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {	
				
				if (caseOutF.getText().isEmpty() == false)
				{
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						caseOutF.setText(String.valueOf(Integer.parseInt(caseOutF.getText()) + 1));
						
						if (caseOutF.getText().length() == 1)
							caseOutF.setText("0" + caseOutF.getText());
					}
					
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						caseOutF.setText(String.valueOf(Integer.parseInt(caseOutF.getText()) - 1));
						
						if (caseOutF.getText().length() == 1)
							caseOutF.setText("0" + caseOutF.getText());
					}
				}

				if (caseOutF.getText().length() > 1 || e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (caseOutF.getText().length() == 1)
						caseOutF.setText("0" + caseOutF.getText());

					VideoPlayerCore.updateTimeOut();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (caseOutF.getText().length() >= 2)
					caseOutF.setText("");
			}			
		});
		
		caseOutF.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (caseOutF.getText().length() == 1)
					caseOutF.setText("0" + caseOutF.getText());	
			}
			
		});	
	
	}
		
	private void playerOptions() {
		
		caseShowWaveform.setName("caseShowWaveform");	
		caseShowWaveform.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));	
		caseShowWaveform.setSelected(true);
		Shutter.frame.getContentPane().add(caseShowWaveform);
		
		caseShowWaveform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseShowWaveform.isSelected())
				{
					if (VideoPlayerCore.waveform != null)
					{
						VideoPlayerCore.addWaveform(false);
					}
					else
						VideoPlayerCore.addWaveform(true);
				}
				else
				{
					if (VideoPlayerCore.addWaveformIsRunning)
					{									
						try {
							FFMPEG.waveformWriter.write('q');
							FFMPEG.waveformWriter.flush();
							FFMPEG.waveformWriter.close();
						} catch (IOException er) {}
						
						FFMPEG.waveformProcess.destroy();
					}
					
					waveformIcon.setVisible(false);
					waveformContainer.repaint();
				}
			}
		});
				
		caseVuMeter.setName("caseVuMeter");	
		caseVuMeter.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));	
		caseVuMeter.setSelected(true);
		Shutter.frame.getContentPane().add(caseVuMeter);
		
		caseVuMeter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (VideoPlayerCore.bufferedFrames.size() > 0)
				{				
					//Clear the buffer
					VideoPlayerCore.bufferedFrames.clear();					
					waveformContainer.repaint();
				}
				
				frameIsComplete = false;
							
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
			}

		});
					
		casePlaySound.setName("casePlaySound");	
		casePlaySound.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		casePlaySound.setSelected(true);
		Shutter.frame.getContentPane().add(casePlaySound);
					
		caseInternalTc = new JCheckBox(Shutter.language.getProperty("caseTcInterne"));
		caseInternalTc.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		Shutter.frame.getContentPane().add(caseInternalTc);	
		

		btnPreview = new JLabel(new FlatSVGIcon("resources/preview.svg", 16, 16));
		btnPreview.setHorizontalAlignment(SwingConstants.CENTER);
		btnPreview.setToolTipText(Shutter.language.getProperty("preview"));
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(btnPreview);
		
		btnPreview.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {	
				
				FFMPEG.toSDL(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				btnPreview.setIcon(new FlatSVGIcon("resources/preview.svg", 16, 16).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnPreview.setIcon(new FlatSVGIcon("resources/preview.svg", 16, 16));
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}        			
		});
		
		splitValue = new JTextField();
		splitValue.setName("splitValue");
		splitValue.setText("10");
		splitValue.setVisible(false);
		splitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		splitValue.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		splitValue.setColumns(10);
		Shutter.frame.getContentPane().add(splitValue);
		
		splitValue.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (splitValue.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					waveformContainer.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (splitValue.getText().length() >= 4)
					splitValue.setText("");					
			}
			
		});
		
		lblSplitSec = new JLabel(Shutter.language.getProperty("lblSec"));
		lblSplitSec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSplitSec.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		lblSplitSec.setVisible(false);
		lblSplitSec.setBounds(splitValue.getX() + splitValue.getWidth() + 2, splitValue.getY(), lblSplitSec.getPreferredSize().width, 16);
		Shutter.frame.getContentPane().add(lblSplitSec);
		
		comboMode.setName("comboMode");
		comboMode.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		comboMode.setMaximumRowCount(3);
		comboMode.setSize(76, 22);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(comboMode);
		
		comboMode.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				btnPreview.setBounds(waveformScrollPane.getX() + waveformScrollPane.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
				lblSplitSec.setBounds(btnPreview.getX() + 10, caseInternalTc.getY() + 2, lblSplitSec.getPreferredSize().width, 16);
				splitValue.setBounds(lblSplitSec.getX() - splitValue.getWidth() - 2, caseInternalTc.getY() + 2, 34, 16);
				
				if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
				{
					if (showInfoMessage)
					{
						JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("mayNotWorkWithGOP"), Shutter.language.getProperty("mode") + " " + Shutter.language.getProperty("removeMode"), JOptionPane.INFORMATION_MESSAGE);
						showInfoMessage = false;
					}
					
					btnPreview.setVisible(true);
					splitValue.setVisible(false);
					lblSplitSec.setVisible(false);
					comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);	
					
					caseApplyCutToAll.setEnabled(false);
					caseApplyCutToAll.setSelected(false);
				}
				else if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("splitMode")))
				{
					btnPreview.setVisible(false);
					splitValue.setVisible(true);
					lblSplitSec.setVisible(true);
					comboMode.setLocation(splitValue.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);
					
					caseApplyCutToAll.setEnabled(true);
				}
				else
				{
					btnPreview.setVisible(true);
					splitValue.setVisible(false);
					lblSplitSec.setVisible(false);
					comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);	
					
					caseApplyCutToAll.setEnabled(true);
				}			
								
				lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, caseInternalTc.getY() + 3, lblMode.getPreferredSize().width, 16);			
				caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, caseInternalTc.getY(), caseVuMeter.getPreferredSize().width, 23);	
				caseShowWaveform.setBounds(caseVuMeter.getX() - caseShowWaveform.getPreferredSize().width - 5, caseVuMeter.getY(), caseShowWaveform.getPreferredSize().width, 23);
				
				waveformContainer.repaint();
			}
	
		});
		
		lblMode = new JLabel(Shutter.language.getProperty("mode"));
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(lblMode);
		
		caseInternalTc.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
    			
				if (caseInternalTc.isSelected())
				{
					FFPROBE.Data(VideoPlayerCore.videoPath);
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFPROBE.isRunning);
							
					
					if (FFPROBE.timecode1.equals(""))
	    			{
	    				MEDIAINFO.run(VideoPlayerCore.videoPath, false);
	    				
	    				do
	    				{
	    					try {
		        				Thread.sleep(100);
		        			} catch (InterruptedException e1) {}
	    				}
	    				while (MEDIAINFO.isRunning);		    				
	    			}
					
					if (FFPROBE.timecode1.equals(""))
					{
						caseInternalTc.setSelected(false);
						offset = 0;
					}
					else
					{
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600 * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode2) * 60 * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode3) * FFPROBE.accurateFPS
								+ Integer.valueOf(FFPROBE.timecode4);				
					}						
				}
				else
				{					
					offset = 0;
				}
				
    			//Update lblTimecode
				sliderChange = true;
    			VideoPlayerCore.getTimePoint(VideoPlayerCore.playerCurrentFrame);
				sliderChange = false;
			}	
		});
		
	}

	public static void resizeAll() {
								
		if (Shutter.frame.getWidth() > 332 && Shutter.doNotLoadImage == false)	
		{						
			//Clear the buffer
			if (VideoPlayerCore.bufferedFrames.size() > 0)
			{			
				VideoPlayerCore.bufferedFrames.clear();
				waveformContainer.repaint();
			}
			
			isPiping = false;
			
			if (Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction"))
			|| Shutter.btnStart.getText().equals(Shutter.language.getProperty("resume"))
			|| Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")))
			{
				isPiping = true;				
				setPlayerButtons(false);
			}
			
			//Players 		
			double ratio = FFPROBE.imageRatio;
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
			{
				ratio = (double) FFPROBE.imageHeight / FFPROBE.imageWidth;
			}
							
			//CaseForceDAR
			if (Shutter.caseForcerDAR.isSelected())
			{
				if (Shutter.comboDAR.getSelectedItem().toString().contains(":"))
				{
					String s[] = Shutter.comboDAR.getSelectedItem().toString().split(":");
					
					ratio = (double) Integer.parseInt(s[0]) / Integer.parseInt(s[1]);
				}
				else
					ratio = Float.parseFloat(Shutter.comboDAR.getSelectedItem().toString());
			}
			else if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false && Shutter.btnNoUpscale.isSelected() == false
			&& Shutter.comboResolution.getSelectedItem().toString().contains("x")
			&& Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false)
			{
				String s[] = Shutter.comboResolution.getSelectedItem().toString().split("x");		
				ratio = (double) Integer.parseInt(s[0]) / Integer.parseInt(s[1]);
			}
			else if (Shutter.caseEnableCrop.isSelected() && isPiping)
			{			
				ratio = (double) Integer.parseInt(Shutter.textCropWidth.getText()) / Integer.parseInt(Shutter.textCropHeight.getText());
			}
			
			int maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2;
			if (Shutter.noSettings)
			{
				maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth();
			}	
			
			if (fullscreenPlayer)
			{
				maxWidth = graphicsDevice.getFullScreenWindow().getWidth();
			}
			
			int maxHeight = (int) (Shutter.frame.getHeight() - (Shutter.topPanel.getHeight() + Shutter.statusBar.getHeight()) - 40);
			if (fileDuration <= 40 && Shutter.caseEnableSequence.isSelected() == false || isPiping) //Image
			{
				maxHeight = Shutter.frame.getHeight() - (Shutter.grpChooseFiles.getY() + 8) - (Shutter.frame.getHeight() - (Shutter.grpProgression.getY() + Shutter.grpProgression.getHeight()));
			}			
			
			if (fullscreenPlayer)
			{
				maxHeight = graphicsDevice.getFullScreenWindow().getHeight();
			}
			
			int width = (int) (maxHeight * ratio);
			int height = (int) (maxWidth / ratio);	
						
			if (width <= maxWidth)
			{								
				player.setSize(width, maxHeight);
			}
			else							
				player.setSize(maxWidth, height);			
			
			if (fullscreenPlayer == false && Shutter.frame.getHeight() - player.getHeight() < 220 && (fileDuration > 40 || Shutter.caseEnableSequence.isSelected()))
			{
				int p = 220 - (Shutter.frame.getHeight() - player.getHeight());				
				player.setSize((int) (player.getWidth() - (double) p * ratio), player.getHeight() - p);
			}
			
			int y = Shutter.frame.getHeight() / 2 - player.getHeight() / 2 - 58;
			
			if (fileDuration <= 40 && Shutter.caseEnableSequence.isSelected() == false || isPiping || Shutter.inputDeviceIsRunning) //Image
			{			
				y = Shutter.frame.getHeight() / 2 - player.getHeight() / 2;
			}
			
			if (Shutter.noSettings)
			{
				player.setLocation((1350 - player.getSize().width) / 2, y);
			}
			else if (fullscreenPlayer)
			{
				player.setLocation(graphicsDevice.getFullScreenWindow().getWidth() / 2 - player.getWidth() / 2, graphicsDevice.getFullScreenWindow().getHeight() / 2 - player.getHeight() / 2);
			}
			else
			{
				player.setLocation((Shutter.frame.getWidth() - player.getSize().width) / 2, y);
			}

			//IMPORTANT video canvas must be a multiple of 4!
			player.setSize(player.getWidth() - (player.getWidth() % 4), player.getHeight() - (player.getHeight() % 4));
								
			//Define bufferSize
			if (player.getWidth() != 0 && player.getHeight() != 0)
			{
				VideoPlayerCore.maxBufferedFrames = (int) ((double) (Shutter.availableMemory / 3) / (player.getWidth() * player.getHeight() * 3));
			}
			
			Shutter.playerRatio = (float) FFPROBE.imageWidth / player.getWidth();
			
			//Sliders
			if (Shutter.noSettings)
			{
				waveformScrollPane.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth(), 40 + waveformScrollPane.getHorizontalScrollBar().getHeight());
			}
			else
				waveformScrollPane.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2, 40 + waveformScrollPane.getHorizontalScrollBar().getHeight());
						
			waveformContainer.setSize(waveformScrollPane.getWidth() * waveformZoom, waveformScrollPane.getHeight() - waveformScrollPane.getHorizontalScrollBar().getHeight());
			waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));
			waveformIcon.setSize(waveformContainer.getSize());
						
			//Waveforms
			if (fullscreenPlayer == false && isPiping == false && Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false && Shutter.list.getSize() > 0 && VideoPlayerCore.addWaveform.isAlive() == false)
			{	
				VideoPlayerCore.addWaveform(false);				 					
			}
			
			if (VideoPlayerCore.playerCurrentFrame <= 1)
			{
				cursorWaveform.setLocation(0, 0);
				cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
			}
			else
			{
				if (cursorWaveform.getX() > waveformContainer.getWidth() - 2)
				{
					cursorWaveform.setLocation(waveformContainer.getWidth() - 2, 0);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
				}
				else
				{
					cursorWaveform.setLocation((int) Math.floor((double) (waveformContainer.getSize().width * VideoPlayerCore.playerCurrentFrame) / totalFrames), 0);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
				}
			}

			if (isPiping == false)
			{
				try { //Might fail loading
					
					VideoPlayerCore.setMarkers();
					
				} catch (Exception e) {}
			}
			
			//lblTimecode & lblDuration
			lblPosition.setBounds(waveformScrollPane.getX(), waveformScrollPane.getY() - 22, waveformScrollPane.getWidth(), 16);
			lblDuration.setBounds(waveformScrollPane.getX(), lblPosition.getY(), waveformScrollPane.getWidth(), 16); 
									
			//grpSubtitles
			if (Shutter.caseAddSubtitles.isSelected())
			{						    		
				if (Integer.parseInt(Shutter.textSubsWidth.getText()) >= FFPROBE.imageWidth)
				{
					Shutter.subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (double) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (double) FFPROBE.imageHeight / player.getHeight())));
				}
				else
				{
					Shutter.subsCanvas.setSize((int) ((double) Integer.parseInt(Shutter.textSubsWidth.getText()) / ( (double) FFPROBE.imageHeight / player.getHeight())),
				    		(int) (player.getHeight() + (double) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (double) FFPROBE.imageHeight / player.getHeight())));	
					
					Shutter.subsCanvas.setLocation((player.getWidth() - Shutter.subsCanvas.getWidth()) / 2, 0);
				}			
			}
			
			if (RecordInputDevice.comboInputVideo != null && RecordInputDevice.comboInputVideo.getSelectedIndex() > 0)
			{
				Shutter.caseAddWatermark.setSelected(true);
			}
			
			//grpWatermark
			if (Shutter.caseAddWatermark.isSelected()
			&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false
			&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")) == false)
			{				
				VideoPlayerOverlay.loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
				if (Shutter.watermarkPreset != null)
				{
					Shutter.logo.setLocation((int) Math.round(Integer.valueOf(Shutter.textWatermarkPosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(Shutter.textWatermarkPosY.getText()) / Shutter.playerRatio));
				}
			}
			
			//grpCrop
			Shutter.frameCropX = player.getLocation().x;
			Shutter.frameCropY = player.getLocation().y;
			if (Shutter.caseEnableCrop.isSelected())
			{
				Shutter.selection.setLocation((int) Math.round(Integer.valueOf(Shutter.textCropPosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(Shutter.textCropPosY.getText()) / Shutter.playerRatio));
				int w = (int) Math.round((double)  (Integer.valueOf(Shutter.textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
				int h = (int) Math.round((double)  (Integer.valueOf(Shutter.textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
				
				if (w > player.getWidth())
					w = player.getWidth();
				
				if (h > player.getHeight())
					h = player.getHeight();
				
				Shutter.selection.setSize(w , h);	
			}
			else
			{
				//Important
				Shutter.selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);	
				Shutter.anchorRight = Shutter.selection.getLocation().x + Shutter.selection.getWidth();
				Shutter.anchorBottom = Shutter.selection.getLocation().y + Shutter.selection.getHeight();
			}
			
			//grpOverlay
			if (Shutter.windowDrag)
			{				
				if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected())
				{
					Shutter.timecode.setLocation((int) Math.round(Integer.valueOf(Shutter.textTcPosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(Shutter.textTcPosY.getText()) / Shutter.playerRatio));
					Shutter.tcLocX = Shutter.timecode.getLocation().x;
					Shutter.tcLocY = Shutter.timecode.getLocation().y;			
				}
				if (Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
				{
					Shutter.fileName.setLocation((int) Math.round(Integer.valueOf(Shutter.textNamePosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(Shutter.textNamePosY.getText()) / Shutter.playerRatio));
					Shutter.fileLocX = Shutter.fileName.getLocation().x;
					Shutter.fileLocY = Shutter.fileName.getLocation().y;
				}
			}
						
			btnPrevious.setBounds(player.getLocation().x + player.getSize().width / 2 - 21 - 4, waveformScrollPane.getY() + waveformContainer.getHeight() + 10, 22, 21);		
			btnNext.setBounds(player.getLocation().x + player.getSize().width / 2 + 4, btnPrevious.getLocation().y, 22, 21);		
			btnPlay.setBounds(btnPrevious.getLocation().x - 40 - 4, btnPrevious.getLocation().y, 40, 21);				
			btnStop.setBounds(btnNext.getLocation().x + btnNext.getSize().width + 4, btnNext.getLocation().y, 40, 21);	
			btnMarkIn.setBounds(btnPlay.getLocation().x - 22 - 4, btnPlay.getLocation().y, 22, 21);				
			btnGoToIn.setBounds(btnMarkIn.getLocation().x - 40 - 4, btnMarkIn.getLocation().y, 40, 21);				
			btnMarkOut.setBounds(btnStop.getLocation().x + btnStop.getSize().width + 4, btnStop.getLocation().y, 22, 21);				
			btnGoToOut.setBounds(btnMarkOut.getLocation().x + btnMarkOut.getSize().width + 4, btnMarkOut.getLocation().y, 40, 21);		
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
			{
				panelForButtons.setBounds(btnPlay.getX() + 2, btnPlay.getY(), (btnStop.getX() + btnStop.getWidth()) - btnPlay.getX() - 4, 21);
			}
			else
				panelForButtons.setBounds(btnGoToIn.getX() + 2, btnPlay.getY(), (btnGoToOut.getX() + btnGoToOut.getWidth()) - btnGoToIn.getX() - 4, 21);
			
			showFPS.setBounds(player.getX() + player.getWidth() / 2, player.getY() - 18, player.getWidth() / 2, showFPS.getPreferredSize().height);
			comboPlayerQuality.setLocation(player.getX() + (player.getWidth() - comboPlayerQuality.getWidth()) / 2, player.getY() - 18);
			showScale.setBounds(player.getX(), showFPS.getY(), (comboPlayerQuality.getX() - player.getX()), showScale.getPreferredSize().height);
			comboAudioTrack.setBounds(7, (waveformContainer.getHeight() / 2) - 8, 40, 16);

			if (showScale.getY() < Shutter.topPanel.getHeight() || FFPROBE.audioOnly || VideoPlayerCore.videoPath == null || Shutter.frame.getSize().width <= 654 || fullscreenPlayer)
			{
				showScale.setVisible(false);
			}
			else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.list.getSize() > 0 && isPiping == false && Settings.btnDisableVideoPlayer.isSelected() == false)
			{
				showScale.setVisible(true);
			}
			
			showScale.repaint();
			showFPS.repaint();
			
			//Group boxes
			caseInH.setBounds(waveformScrollPane.getX() - 2, btnPrevious.getY(), 21, 21);
			caseInM.setBounds(caseInH.getX() + caseInH.getWidth(), caseInH.getY(), 21, 21);
			caseInS.setBounds(caseInM.getX() + caseInM.getWidth(), caseInH.getY(), 21, 21);
			caseInF.setBounds(caseInS.getX() + caseInS.getWidth(), caseInH.getY(), 21, 21);		
			caseOutH.setBounds(waveformScrollPane.getX() + waveformScrollPane.getWidth() - 21 * 4, caseInH.getY(), 21, 21);
			caseOutM.setBounds(caseOutH.getX() + caseOutH.getWidth(), caseOutH.getY(), 21, 21);
			caseOutS.setBounds(caseOutM.getX() + caseOutM.getWidth(), caseOutH.getY(), 21, 21);
			caseOutF.setBounds(caseOutS.getX() + caseOutS.getWidth(), caseOutH.getY(), 21, 21);
		
			caseInternalTc.setBounds(caseInH.getX() - 2, btnPrevious.getY() + btnPrevious.getHeight() + 6, caseInternalTc.getPreferredSize().width, 23);	
			
			if (caseInternalTc.isVisible())
			{
				casePlaySound.setBounds(caseInternalTc.getX() + caseInternalTc.getWidth() + 4, caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);
			}
			else
				casePlaySound.setBounds(caseInternalTc.getX(), caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);
			
			btnPreview.setBounds(waveformScrollPane.getX() + waveformScrollPane.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
			lblSplitSec.setBounds(btnPreview.getX() + 10, caseInternalTc.getY() + 2, lblSplitSec.getPreferredSize().width, 16);
			splitValue.setBounds(lblSplitSec.getX() - splitValue.getWidth() - 2, caseInternalTc.getY() + 2, 34, 16);		
			
			if (splitValue.isVisible())
			{
				comboMode.setLocation(splitValue.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);
			}
			else
				comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);		
			
			lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, caseInternalTc.getY() + 3, lblMode.getPreferredSize().width, 16);			

			//Sliders
			sliderSpeed.setLocation(btnGoToIn.getX() -  sliderSpeed.getWidth() - 4, btnPrevious.getY() + 1);
			lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);			
			lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, lblSpeed.getY());	
			
			if (Shutter.frame.getWidth() < 1320 && Shutter.noSettings == false)
			{
				caseShowWaveform.setBounds(caseInternalTc.getX(), caseInternalTc.getY() + caseInternalTc.getHeight(), caseShowWaveform.getPreferredSize().width, 23);
				caseVuMeter.setBounds(caseShowWaveform.getX() + caseShowWaveform.getWidth() + 4, caseShowWaveform.getY(), caseVuMeter.getPreferredSize().width, 23);
				
				sliderVolume.setBounds(lblVolume.getX() + lblVolume.getWidth() - lblVolume.getWidth(), sliderSpeed.getY(), sliderSpeed.getWidth(), 22);
				
				caseApplyCutToAll.setLocation(lblPosition.getX() + waveformScrollPane.getWidth() / 2 - caseApplyCutToAll.getWidth(), lblPosition.getY() - 3);
			}
			else
			{
				caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, caseInternalTc.getY(), caseVuMeter.getPreferredSize().width, 23);
				caseShowWaveform.setBounds(caseVuMeter.getX() - caseShowWaveform.getPreferredSize().width - 5, caseVuMeter.getY(), caseShowWaveform.getPreferredSize().width, 23);

				sliderVolume.setBounds(lblVolume.getX() + lblVolume.getWidth() + 1, sliderSpeed.getY(), sliderSpeed.getWidth(), 22);
				
				caseApplyCutToAll.setLocation(lblPosition.getX() + waveformScrollPane.getWidth() / 2 - caseApplyCutToAll.getWidth() / 2, lblPosition.getY() - 3);
			}
			
			if (Shutter.windowDrag == false && VideoPlayerCore.videoPath != null && isPiping == false)
			{					
				if (VideoPlayerCore.preview != null && fileDuration > 40)
					VideoPlayerCore.preview = null;
				
				if (Shutter.inputDeviceIsRunning)
				{
					VideoPlayerCore.playerFreeze();
				}
				else if (fileDuration <= 40)
				{
					VideoPlayerCore.loadImage(false);
					waveformIcon.setVisible(false);
				}
				else if (btnPlay.isEnabled())
				{			
					VideoPlayerCore.playerFreeze();
				}
			}	
			
			if (Shutter.list.getSize() == 0 || VideoPlayerCore.videoPath == null)
			{
				VideoPlayerUI.setPlayerButtons(false);
			}
			
		}
		
		if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected() || Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
		{
			VideoPlayerOverlay.refreshTimecodeAndText();
		}
				
		Shutter.statusBar.setBounds(0, Shutter.frame.getHeight() - 23, Shutter.frame.getWidth(), 22);
		
		if (Shutter.frame.getWidth() >= 1130)
		{
			Shutter.lblArrows.setLocation(Shutter.statusBar.getWidth() / 2 - Shutter.lblArrows.getWidth() / 2, Shutter.lblArrows.getY());			
			Shutter.lblYears.setVisible(true);
		}
		else
		{
			Shutter.lblArrows.setLocation(Shutter.frame.getWidth() - Shutter.lblArrows.getWidth() - 7, Shutter.lblArrows.getY());
			Shutter.lblYears.setVisible(false);
		}
		
		Shutter.lblYears.setLocation(Shutter.frame.getWidth() - Shutter.lblYears.getWidth() - 8, Shutter.lblBy.getY());
		
	}
}
