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
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import functions.VideoEncoders;
import library.DCRAW;
import library.FFMPEG;
import library.FFPROBE;
import library.MEDIAINFO;
import library.PDF;
import library.NCNN;
import settings.AdvancedFeatures;
import settings.AudioSettings;
import settings.Colorimetry;
import settings.Corrections;
import settings.FunctionUtils;
import settings.ImageSequence;
import settings.InputAndOutput;
import settings.Timecode;
import settings.Transitions;

public class VideoPlayer {
	
    //Player
	public static JPanel player; 
    public static Process playerVideo;
    public static Process bufferVideo;
    public static Process playerAudio;	
    private static BufferedInputStream videoInputStream;
    private static InputStream audio = null;	
    private static AudioInputStream audioInputStream = null;
    private static SourceDataLine line;
    private static FloatControl gainControl;
    private static float offsetVideo = 0f;
    private static float offsetAudio = 0f;
    private static Thread playerThread;
    public static Thread setTime;
    private static String setEQ = "";	
	public static float playerCurrentFrame = 0;
	public static float bufferCurrentFrame = 0;
    private static long fpsTime = 0;
    private static int fps = 0;
    private static int displayCurrentFPS = 0;
    private static JLabel showFPS;
    public static JLabel showScale;
    public static JComboBox<String> comboAudioTrack;
    public static int playerInMark = 0;
    public static int playerOutMark = 0;
    public static ArrayList<Image> bufferedFrames = new ArrayList<Image>();
    public static int maxBufferedFrames = 500;
    public static BufferedImage frameVideo;
    private static BufferedImage fullSizeWatermark;
	public static double screenRefreshRate = 16.7; //Vsync in ms
	private static long lastEvTime = 0;
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
    private static boolean closeAudioStream = false;
    public static boolean isPiping = false;
    private static boolean previewUpscale = false;    
	public static boolean fullscreenPlayer = false;
	private static Thread mouseClickThread;
	private static String freezeFrame = "";
	private static float fileDuration = 0;
	private static int waveformZoom = 1;
	
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
	public static JSlider slider;
	public static JCheckBox caseApplyCutToAll = new JCheckBox(Shutter.language.getProperty("caseApplyToAll"));
	public static JCheckBox caseShowWaveform = new JCheckBox(Shutter.language.getProperty("caseShowWaveform"));
	public static JCheckBox caseVuMeter = new JCheckBox(Shutter.language.getProperty("caseVuMeter"));;
	public static JCheckBox casePlaySound = new JCheckBox(Shutter.language.getProperty("casePlaySound"));;
	public static JCheckBox caseInternalTc;
	
	public static String videoPath = null;
	public static float inputFramerateMS = 40.0f;
	private static float totalFrames;
	
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
	public static float offset = 0;
	public static int durationH = 0;
	public static int durationM = 0;
	public static int durationS = 0;
	public static int durationF = 0;
		
	//Frame by frame forward/backward
	public static boolean frameControl = false;
	public static boolean seekOnKeyFrames = false;
	
	//Waveform
	private static Thread addWaveform = new Thread();
	public static boolean addWaveformIsRunning = false;
	public static BufferedImage waveform = null;
	public static JLabel waveformIcon;
	public static JLabel waveformContainer;
	private static JScrollPane waveformScrollPane;
	public static JPanel cursorHead;
	public static JPanel cursorWaveform;
	public static JPanel cursorCurrentFrame;
	public static boolean mouseIsPressed = false;
	
	//Preview
	public static BufferedImage preview = null;
	public static Thread runProcess = new Thread();
	
	//FileList
	public static StringBuilder fileList = new StringBuilder();

	public static int MousePositionX;
	public static int MousePositionY;
		
	public VideoPlayer() {  	
		
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
		lblPosition.setForeground(new Color(230,75,60));
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
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
							
					InputAndOutput.savedInPoint = (float) Math.ceil(timeIn);	
					
					if (VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
					{
						float totalOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
						float total = (totalFrames - totalOut); //Get how much frames to remove from totalFrames					
												
						InputAndOutput.savedOutPoint = total;					
					}
				}
				else
				{
					InputAndOutput.savedInPoint = 0;		
					InputAndOutput.savedOutPoint = 0;
				}
				
				waveformContainer.repaint();
			}
			
		});;
		
		lblDuration = new JLabel();
		lblDuration.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDuration.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
		lblDuration.setForeground(Utils.themeColor);
		Shutter.frame.getContentPane().add(lblDuration);
		        		 
		setMedia();	
		
		totalDuration();
							
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
							
							checkSelection();
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
										
						if (slider.getValue() > 0)
						{
							frameIsComplete = false;
										
							playerSetTime(playerCurrentFrame);
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
						playerSetTime(0);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD1)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 1);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 2);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 3);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD4)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 4);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD5)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 5);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD6)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 6);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD7)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 7);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 8);
					}
					else if (e.getKeyCode() == KeyEvent.VK_NUMPAD9)
					{
						e.consume();
						playerSetTime((float) (totalFrames / 10) * 9);
					}
				}
				
				if (e.getKeyCode() == KeyEvent.VK_J)
				{
					previousFrame = true;
					playerSetTime((float) (VideoPlayer.playerCurrentFrame - 10));
  				}
					
				if (e.getKeyCode() == KeyEvent.VK_L)
				{
					previousFrame = true;
					playerSetTime((float) (VideoPlayer.playerCurrentFrame + 10));
				}
				
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{											
					if (e.getID() == KeyEvent.KEY_PRESSED)
	        		{           	  
						if (e.getKeyCode() == KeyEvent.VK_SHIFT)
							Shutter.shift = true;
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
				
				if (playerVideo != null)
				{
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						btnPrevious.doClick();							
						waveformScrollPane.getHorizontalScrollBar().setValue(waveformScrollPane.getHorizontalScrollBar().getValue() + 10);
					}
					
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						btnNext.doClick();
						waveformScrollPane.getHorizontalScrollBar().setValue(waveformScrollPane.getHorizontalScrollBar().getValue() - 10);
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
	
	public static void playerProcess(float inputTime) {
		
		try {	
			
			//VIDEO STREAM
			if (System.getProperty("os.name").contains("Windows"))
			{					
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
			}	
			else
			{
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
			}		
			
			InputStream video = playerVideo.getInputStream();				
			videoInputStream = new BufferedInputStream(video);
			
			//AUDIO STREAM
			if ((casePlaySound.isSelected() && (mouseIsPressed == false || FFPROBE.audioOnly)) || mouseIsPressed == false)						       
			{					
				if (System.getProperty("os.name").contains("Windows"))
				{							
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, false));	
					playerAudio = pba.start();					
				}	
				else
				{
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, false));	
					playerAudio = pba.start();					
				}
				
				//Avoid a crashing issue
				try {
					audio = playerAudio.getInputStream();	
					audioInputStream = null;
					audioInputStream = AudioSystem.getAudioInputStream(audio);		    
				    AudioFormat audioFormat = audioInputStream.getFormat();
			        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
			        line = (SourceDataLine) AudioSystem.getLine(info);
			        
		            line.open(audioFormat);
		            gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		            
		            double gain = (double) sliderVolume.getValue() / 100;   
			        float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
			        gainControl.setValue(dB);
			        
		            line.start();	
				} catch (Exception e) {}
			}
								
			//Video thread
			playerThread = new Thread(new Runnable() {

				@Override
				public void run() {																			
					
					do {
												
						long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);
						
						if (playerLoop)
						{			
							try {
												 				        		
					    		//Read 1 video frame	
								if (playerCurrentFrame >= offsetVideo || Shutter.caseAudioOffset.isSelected() == false)
								{		
									if (Shutter.inputDeviceIsRunning)
									{
										frameVideo = readFrame(videoInputStream, FFPROBE.imageWidth, FFPROBE.imageHeight);	
									}
									else
									{
										if (Shutter.windowDrag && fullscreenPlayer == false)
										{
											frameVideo = readFrame(videoInputStream, frameVideo.getWidth(), frameVideo.getHeight());
										}
										else
											frameVideo = readFrame(videoInputStream, player.getWidth(), player.getHeight());															
									}
									
									playerRepaint();
							    	fps ++;	
								}

								updateCurrentFrame();								
															
							} catch (Exception e) {}
							finally {

								if (frameControl && Shutter.inputDeviceIsRunning == false)
								{
									playerLoop = false;
									getTimePoint(playerCurrentFrame);
								}
								else if (playerPlayVideo)
								{										
					            	long delay = startTime - System.nanoTime();
					            						                			
					            	if (delay > 0)
					            	{							            		
					            		//Because the next loop is very cpu intensive but accurate, this sleep reduce the cpu usage by waiting just less than needed
						            	try {
						            		Thread.sleep((int) (delay / 1500000));
										} catch (InterruptedException e) {}

						            	delay = startTime - System.nanoTime();
						            	
						            	long time = System.nanoTime();
						            	while (System.nanoTime() - time < delay) {}		
					                }
								}								
								
								frameIsComplete = true;		
							}
						}   
						else
						{																							
							//IMPORTANT reduce CPU usage
							do {
								try {
								Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLoop == false && playerVideo.isAlive());
						}
					} while (playerVideo.isAlive());
					
					try {
						video.close();
					} catch (IOException e) {}		
					try {
						videoInputStream.close();
					} catch (IOException e) {}
					
					if (audio != null && audioInputStream != null && closeAudioStream)	       
					{						
						try {
							audio.close();
						} catch (IOException e) {}
						try {
							audioInputStream.close();
						} catch (IOException e) {}
						line.flush();
					}
				}
				
			});
			playerThread.setPriority(Thread.MAX_PRIORITY);
			playerThread.start();	
						
			//Audio thread
			Thread playerAudiothread = new Thread(new Runnable() {

				@Override
				public void run() {
					
					byte buffer[] = new byte[4096]; //(int) Math.ceil(48000*2/FFPROBE.accurateFPS)
		            int bytesRead = 0;

		            boolean forceLoop = frameControl; //Allow to read only 1 frame
		            boolean inputAudioStreamIsDone = false;
		            		         
		            //Replace audio offset		    		
					if (Shutter.caseAudioOffset.isSelected())
					{
						offsetVideo = (long) inputTime - Integer.parseInt(Shutter.txtAudioOffset.getText());
						offsetAudio = (long) inputTime + Integer.parseInt(Shutter.txtAudioOffset.getText());
					}	
					
					float inputVideoFrameToSeconds = (float) inputTime / FFPROBE.accurateFPS;
						
					do {
						
						if (playerLoop && (forceLoop || playerIsPlaying()))
						{		
							if (playerIsPlaying())
							{
								long time = System.currentTimeMillis();
								
								while (frameIsComplete == false)
								{	
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (frameVideo == null || System.currentTimeMillis() - time > 5000)
									{
										frameIsComplete = true;
									}						
								}
							}
							
							//Audio volume	
							if (audioInputStream != null && audioSetTimeIsRunning == false && ((casePlaySound.isSelected() || playerIsPlaying()) && (mouseIsPressed == false || FFPROBE.audioOnly)))					       
							{										
								closeAudioStream = true;
		
								///Read 1 audio frame
								if (playerCurrentFrame >= offsetAudio)
								{
									if (inputAudioStreamIsDone == false)
									{
										try {
											
											bytesRead = audioInputStream.read(buffer, 0, buffer.length);
							        		line.write(buffer, 0, bytesRead);
							        									        		
											if (playerPlayVideo && FFPROBE.audioOnly == false)
											{
												if (audioSetTimeIsRunning)
													inputVideoFrameToSeconds = (float) playerCurrentFrame / FFPROBE.accurateFPS - (float) line.getLongFramePosition() / 48000;
												
												float videoClock = (float) ((float) playerCurrentFrame / FFPROBE.accurateFPS) * 1000;
												float audioClock = (float) ((float) line.getLongFramePosition() / 48000 + inputVideoFrameToSeconds) * 1000;
												float delay = (audioClock - videoClock);
																							
												if (delay >= 50) //When the unsync is more than 50ms
												{	
									            	try {
														Thread.sleep(Math.round(delay));
													} catch (InterruptedException e) {}	
												}
											}
							        		
										} catch (Exception e) {
											
											if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio"))
											&& Shutter.comboFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("longest"))) //When the audio is empty
											{	
												inputAudioStreamIsDone = true;
											}											
										}
									}
								}
							}
							else
								closeAudioStream = false;	
														
							forceLoop = false;
						}
						else
						{									
							if (line != null && closeAudioStream && sliderChange == false && frameControl == false)		       
							{
								line.flush();	
							}
														
							//IMPORTANT reduce CPU usage
							do {
								try {
								Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLoop == false && playerVideo.isAlive());
						}
												
					} while (playerThread.isAlive());					
				}
				
			});
			playerAudiothread.setPriority(Thread.MAX_PRIORITY);
			playerAudiothread.start();				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static BufferedImage readFrame(BufferedInputStream is, int width, int height) throws IOException
	{
        int frameSize = width * height * 3;
        if (FFPROBE.hasAlpha)
        	frameSize = width * height * 4;
        
		byte[] frameData = new byte[frameSize];
		
        int read = is.readNBytes(frameData, 0, frameData.length);
        if (read == frameData.length)
        {
        	BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        	if (FFPROBE.hasAlpha)
        		frame = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        	
            frame.getRaster().setDataElements(0, 0, width, height, frameData);
            
            return frame;
        }
        
        return null;
	}
	
	private static void playerPlayAudioOnly(float inputTime) {
		
		if (casePlaySound.isSelected() && FFPROBE.hasAudio && mouseIsPressed == false)
		{		
			if (line != null)
				line.flush();
			
			try {	
				
				Process playerAudio;
				
				//AUDIO STREAM
				if (System.getProperty("os.name").contains("Windows"))
				{						
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, true));	
					playerAudio = pba.start();
				}	
				else
				{		
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, true));	
					playerAudio = pba.start();
				}			
					
				InputStream audio = playerAudio.getInputStream();							
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);		    
			    AudioFormat audioFormat = audioInputStream.getFormat();
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		        
	            line.open(audioFormat);
	            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
	            line.start();	
	            
	            byte bytes[] = new byte[4096];
	            int bytesRead = 0;
	            
				double gain = (double) sliderVolume.getValue() / 100;   
		        float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
		        
		        gainControl.setValue(dB);		        
		        bytesRead = audioInputStream.read(bytes, 0, bytes.length);
        		line.write(bytes, 0, bytesRead);

				try {
					audio.close();
				} catch (IOException e) {}
				try {
					audioInputStream.close();
				} catch (IOException e) {}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public static void playerPlay() {

		if (playerVideo == null || playerVideo.isAlive() == false)		
		{		
			playerProcess(playerCurrentFrame);							
		}		
	}
	
	public static void playerStop() {
		
		playerLoop = false;
				
		if (playerVideo != null)
		{
			playerVideo.destroy();
			try {
				playerThread.interrupt();
			} catch(Exception e) {}
		}
		
		if (playerAudio != null)
		{
			playerAudio.destroy();	
		}
	}

	public static void playerRepaint() {
				
		if (frameVideo != null)
		{			  
		    long time = System.currentTimeMillis();
		    
		    if (time > (lastEvTime + screenRefreshRate)) //Vsync
		    {			    	
		    	lastEvTime = time;		      
		    	player.repaint();
		    	getTimePoint(playerCurrentFrame); 
		    }			
		}			
	}
	
	public static boolean playerIsPlaying() {

		if (btnPlay.getName().equals("pause"))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerSetTime(float time) {
					
		if ((setTime == null || setTime.isAlive() == false) && (frameVideo != null || playerCurrentFrame > 0) && playerThread != null && Shutter.doNotLoadImage == false && time < totalFrames && videoPath != null)
		{			
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {					

					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					int t = (int) Math.ceil(time);
					
					if (t < 0)
						t = 0;
					
					boolean useBuffer = false;
					if (preview != null || Shutter.caseAddSubtitles.isSelected())
					{
						preview = null;
					}					
					else if (FFPROBE.audioOnly == false && (mouseIsPressed || frameControl) && playerIsPlaying() == false && playerCurrentFrame != time && freezeFrame == "")
					{
						useBuffer = true;
					}
							
					int framesToSkip = (int) ((float) time - playerCurrentFrame);
					int framesToSkipBackward = (int) ((float) time - bufferCurrentFrame);

					//Read buffered frames if they exists
					if (bufferedFrames.size() > 0 && time < bufferCurrentFrame + 1 && framesToSkip >= 0 && useBuffer)
					{
						//System.out.println("Read buffered frames");
						
						int framesToRead = (int) ((float) bufferCurrentFrame - playerCurrentFrame - framesToSkip + 1);
							
						frameVideo = (BufferedImage) bufferedFrames.get(bufferedFrames.size() - framesToRead);	
						playerCurrentFrame += framesToSkip;
						
						//Read 1 audio frame
						playerPlayAudioOnly(time);
						
						getTimePoint(playerCurrentFrame); 						
						player.repaint();	
					}					
					else if (framesToSkip < 60 && framesToSkip >= 0 && useBuffer) //Read forward is faster until 60 frames than recreating the process
					{									
						//System.out.println("Read frames");
						
						try {

							int i = 0;
							
							do {
								
								i++;
								
								frameVideo = readFrame(videoInputStream, player.getWidth(), player.getHeight());
								updateCurrentFrame();				
								
								//Limit the buffer size into memory
								if (bufferedFrames.size() > maxBufferedFrames) 
								{
									bufferedFrames.remove(0);
								}
								
								//Read frames and add them into the bufferedFrames
								bufferedFrames.add(frameVideo);
								
								if (i == framesToSkip)
								{
									//Read 1 audio frame
									playerPlayAudioOnly(time);
									
									playerRepaint();
									bufferCurrentFrame = playerCurrentFrame;
								}
								
							} while (i < framesToSkip);

						} catch (Exception er) {}					
					}
					else if (bufferedFrames.size() > 1 && 0 - framesToSkipBackward < bufferedFrames.size() && framesToSkip < 0 && useBuffer) //Read available buffered frames backward
					{			
						//System.out.println("Read buffered frames backward");
						
						frameVideo = (BufferedImage) bufferedFrames.get(bufferedFrames.size() + framesToSkipBackward);							
						playerCurrentFrame += framesToSkip + 1;
						
						//Read 1 audio frame
						playerPlayAudioOnly(time + 1);
						
						getTimePoint(playerCurrentFrame); 						
						player.repaint();
					}
					else if (framesToSkip != 0 || (framesToSkip == 0 && mouseIsPressed == false)) //Do not use if there is no time difference and user is currently scrolling
					{	
						//Clear the buffer
						if (bufferedFrames.size() > 0 && playerCurrentFrame != time && (framesToSkip >= 60 || 0 - framesToSkipBackward >= bufferedFrames.size() || useBuffer == false))
						{		
							//System.out.println("CLEARED");
							bufferedFrames.clear();
							
							//IMPORTANT
							t += 1;
						}
						else
						{
							//System.out.println("Set Time");
							
							//Remove all buffered frames after the playerCurrentFrame
							if (bufferedFrames.size() > 0)
							{
								while (bufferCurrentFrame > playerCurrentFrame)
								{									
									bufferedFrames.remove(bufferedFrames.size() - 1);
									bufferCurrentFrame -= 1;
									
								}
							}
						}
						
						writeCurrentSubs(t, false);
						
						playerPlayVideo = false;
						
						boolean playback;
						if (playerIsPlaying())
						{
							playback = true;
						}
						else
							playback = false;
												
						playerStop();
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						} while (playerThread.isAlive());				
						
						frameControl = true; //IMPORTANT to stop the player loop
						frameIsComplete = false;		
						playerLoop = true;
						playerProcess(t);							
						
						long time = System.currentTimeMillis();
												
						do {

							//IMPORTANT
							playerLoop = true;
							
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
							
							if (frameVideo == null || System.currentTimeMillis() - time > 5000)
							{
								frameIsComplete = true;
							}
							
						} while (frameIsComplete == false);
													
						if (playback && mouseIsPressed == false)
						{						
							playerLoop = true;
						}
						else
							playerLoop = false;
																				
						playerCurrentFrame = t;
						getTimePoint(playerCurrentFrame); 
						Shutter.timecode.repaint();						
						
						frameControl = false;
						playerPlayVideo = true;	
												
					}
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					Shutter.windowDrag = false;
				}
				
			});
			setTime.start();			
		}			
	}

	public static void playerAudioSetTime(float inputTime) {
		
		if (playerAudio != null)
		{			
			playerAudio.destroy();
			
			audioSetTimeIsRunning = true;
			
			if (line != null)
				line.flush();
						
			try {

				if ((casePlaySound.isSelected() && (mouseIsPressed == false || FFPROBE.audioOnly)) || mouseIsPressed == false)						       
				{	
					//AUDIO STREAM
					if (System.getProperty("os.name").contains("Windows"))
					{							
						ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, false));	
						playerAudio = pba.start();					
					}	
					else
					{
						ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, false));	
						playerAudio = pba.start();					
					}
					
					//Avoid a crashing issue
					try {
						audio = playerAudio.getInputStream();	
						audioInputStream = null;
						audioInputStream = AudioSystem.getAudioInputStream(audio);		    
					} catch (Exception e) {}	
				}
				
				if (playerIsPlaying())
					playerLoop = true;
				
			} catch (Exception e) {
				playerAudio.destroy();					
			}
			
			audioSetTimeIsRunning = false;
		}	
	}
		
	public static void playerFreeze() {
							
		if ((setTime == null || setTime.isAlive() == false) && (playerVideo == null || playerVideo.isAlive() == false))
		{		
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {		
					
					frameVideo = null;
					
					playerPlayVideo = false;
					
					writeCurrentSubs(0, false);
					
					if (playerThread != null)
					{						
						playerStop();
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (playerThread.isAlive());	
					}
										
					frameControl = true; //IMPORTANT to stop the player loop
					frameIsComplete = false;						
					playerLoop = true;
					playerProcess(playerCurrentFrame);							
												
					long time = System.currentTimeMillis();
					
					do {

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
						
						if (System.currentTimeMillis() - time > 5000)
							frameIsComplete = true;
						
					} while (frameIsComplete == false);
											
					if (playerCurrentFrame > 0)
						playerCurrentFrame -= 1;

					getTimePoint(playerCurrentFrame); 

					frameControl = false;
					playerPlayVideo = true;	
				}
			});
			setTime.start();
		}			
	}
		
    public static void setMedia() {
    	
    	Thread loadMedia = new Thread(new Runnable()
		{
    		@Override
    		public void run()
    		{    			
    			if (FFMPEG.isRunning == false
		    	|| (Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false
		    	&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("resume")) == false
		    	&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")) == false))
		    	{    				
		   	    	//Updating video file
					if (Shutter.liste.getSize() > 0)
					{				
						if (Shutter.fileList.getSelectedIndices().length == 0)
			      		{
							Shutter.fileList.setSelectedIndex(0);
			      		}
														
						//set timecode & Shutter.fileName locations
						refreshTimecodeAndText();				
						
						if (Shutter.scanIsRunning)
						{
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									videoPath = f.toString();
									break;
								}
							}
						} 
						else if (Shutter.inputDeviceIsRunning)
						{
							videoPath = Shutter.liste.firstElement();
							setInfo();
						}
						
						//Reset when changing file													
						if (Shutter.fileList.getSelectedValue().equals(videoPath) == false && (new File(Shutter.fileList.getSelectedValue()).isFile() || Shutter.scanIsRunning))
						{										
							//Clear the buffer
							if (bufferedFrames.size() > 0)
							{			
								bufferedFrames.clear();
							}
															
							//IMPORTANT
							if (FFPROBE.isRunning)
							{
								do {								
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {}								
								} 
								while (FFPROBE.isRunning);
							}
							
							if (Shutter.scanIsRunning == false)
								videoPath = Shutter.fileList.getSelectedValue();
							
							if (frameVideo != null)
								frameVideo = null;
							
							if (preview != null)
								preview = null;
							
							if (waveform != null)
							{
								waveform = null;
								waveformIcon.setIcon(null);
								waveformIcon.repaint();
							}
							
							waveformZoom = 1;
												
							if (addWaveformIsRunning && FFMPEG.waveformWriter != null)
							{
								try {
									FFMPEG.waveformWriter.write('q');
									FFMPEG.waveformWriter.flush();
									FFMPEG.waveformWriter.close();
								} catch (IOException er) {}
								
								FFMPEG.waveformProcess.destroy();
							}
													
							String extension = videoPath.substring(videoPath.lastIndexOf("."));	
								
							boolean isRaw = false;
				    		
							//FFprobe with RAW files
							switch (extension.toLowerCase())
							{ 
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
									FFPROBE.totalLength = 0;
							}
				
							try {
								
								FunctionUtils.analyze(new File(videoPath), isRaw);

							} catch (InterruptedException e) {}
							
							//IMPORTANT
							btnStop.doClick();
							Shutter.fileList.repaint();
							fileDuration = FFPROBE.totalLength; //Avoid a bug when totalLength is loader somewhere else
							
							if (isRaw)
							{
								Shutter.btnStart.setEnabled(true);
							}
																					
							cursorCurrentFrame.setBounds(0, 0, 1, waveformContainer.getHeight());
							setPlayerButtons(true);	
							
							seekOnKeyFrames = false;
							
							if (FFPROBE.audioOnly == false
							&& (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionConform"))))
							{
								FFPROBE.AnalyzeGOP(videoPath, false);
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {}
									
									if (FFPROBE.gopCount > 2)
									{
										seekOnKeyFrames = true;
										FFPROBE.process.destroy();
										break;
									}
								} while (FFPROBE.isRunning);	
							}
							else
							{
								Shutter.caseEnableCrop.setEnabled(true);
								Shutter.caseAddWatermark.setEnabled(true);
								Shutter.caseSafeArea.setEnabled(true);
							}
							
							//Autocrop
							if (Shutter.caseEnableCrop.isSelected() && Shutter.comboPreset.getSelectedIndex() == 1)
				    		{
				    			FFMPEG.setCropDetect(new File(videoPath));	  
				    		}
							
							//Burn subtitles
							if (Shutter.caseAddSubtitles.isSelected())
							{	
								if (Shutter.subtitlesBurn)
								{
									Shutter.autoBurn = true;
								}
								else
									Shutter.autoEmbed = true;
								
								String ext = videoPath.substring(videoPath.lastIndexOf("."));
																
								if (new File(videoPath.replace(ext, ".srt")).exists()
								|| new File (videoPath.replace(ext, ".vtt")).exists()
								|| new File (videoPath.replace(ext, ".ass")).exists()
								|| new File (videoPath.replace(ext, ".ssa")).exists()
								|| new File (videoPath.replace(ext, ".scc")).exists()
								|| Shutter.comboSubsSource.getSelectedIndex() != 0)
								{
									FunctionUtils.addSubtitles(false);
									if (VideoPlayer.runProcess != null)
									{
										do {
											try {
												Thread.sleep(100);
											} catch (InterruptedException e) {}
										} while (VideoPlayer.runProcess.isAlive());
									}
									FunctionUtils.addSubtitles(true);
								}
								
								Shutter.autoBurn = false;
								Shutter.autoEmbed = false;
								
								try {
									do {
										Thread.sleep(100);
									} while (FFMPEG.isRunning);
								} catch (InterruptedException e) {}		
							}
							
							if (FFPROBE.subtitleStreams != Shutter.comboSubsSource.getItemCount() - 1)
							{
								Shutter.comboSubsSource.removeAllItems();
								Shutter.comboSubsSource.addItem(Shutter.language.getProperty("file"));
								for (int i = 0 ; i < FFPROBE.subtitleStreams ; i++)
								{
									Shutter.comboSubsSource.addItem(Shutter.language.getProperty("source") + " #" + (i + 1));
								}
							}
							
							//Image sequence
							if (Shutter.caseEnableSequence.isSelected())
							{	
								//Create the concat text file
								FunctionUtils.setConcat(new File("concat.txt"), Shutter.dirTemp);						
								inputFramerateMS = Float.parseFloat(Shutter.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
							}
							else					
								inputFramerateMS = (float) (1000 / FFPROBE.accurateFPS);		
							
																				
							playerCurrentFrame = 0;
			
							caseInternalTc.setEnabled(true);	
							Shutter.caseShowTimecode.setEnabled(true);
							
							Shutter.textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
							
							setInfo();
							
							btnPlay.setEnabled(true);
							btnPrevious.setEnabled(true);
							btnNext.setEnabled(true);
							btnStop.setEnabled(true);
							btnMarkIn.setEnabled(true);
							btnMarkOut.setEnabled(true);
							btnGoToIn.setEnabled(true);
							btnGoToOut.setEnabled(true);	
							
							caseInH.setEnabled(true);
							caseInM.setEnabled(true);
							caseInS.setEnabled(true);
							caseInF.setEnabled(true);
							caseOutH.setEnabled(true);
							caseOutM.setEnabled(true);
							caseOutS.setEnabled(true);
							caseOutF.setEnabled(true);
							
							if (fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false && Shutter.frame.getSize().width > 654)
							{
								lblPosition.setVisible(true);
								lblDuration.setVisible(true);
							}
																					
							totalFrames = ((float) fileDuration / 1000 * FFPROBE.accurateFPS);
							slider.setMaximum((int) (totalFrames));
							
							//Reset boxes
							if (caseApplyCutToAll.isVisible() && caseApplyCutToAll.isSelected())
							{
								updateGrpIn(Timecode.getNonDropFrameTC(InputAndOutput.savedInPoint));
								updateGrpOut(Timecode.getNonDropFrameTC(totalFrames - InputAndOutput.savedOutPoint));
							}
							else
							{
								updateGrpIn(0);
								updateGrpOut(totalFrames);	
								
								playerInMark = 0;
								playerOutMark = waveformContainer.getWidth() - 2;
							}
							
							waveformContainer.repaint();
							
							//Setup fileList
							if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
							{
								getFileList(videoPath);
							}
							
							setFileList();	
							
							//Scaling text & logo
							float scale = 0.0f;
							if (FFPROBE.previousImageWidth > 0)	
							{
								scale = ((float) FFPROBE.imageWidth / FFPROBE.previousImageWidth);
								
								if (scale != 0.0f)
								{
									//Display timecode
									if (Shutter.caseShowTimecode.isSelected() || Shutter.caseAddTimecode.isSelected())
									{
										Shutter.textTcSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textTcSize.getText()) * scale)));		
									}
									
									//Display text
									if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected())
									{
										Shutter.textNameSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textNameSize.getText()) * scale)));
									}
									
									//Watermark
									if (Shutter.caseAddWatermark.isSelected() && (FFPROBE.imageWidth != FFPROBE.previousImageWidth || FFPROBE.imageHeight != FFPROBE.previousImageHeight))				
									{	
										Shutter.textWatermarkSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textWatermarkSize.getText()) * scale)));	
									}
									
									resizeAll();
								}
							}
							
						}	
						
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					else
					{				
						btnStop.doClick();
						
						videoPath = null;
						showScale.setVisible(false);
						playerStop();
						slider.setValue(0);
			
						btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));	
						btnPlay.setName("play");
						
						btnPlay.setEnabled(false);
						btnPrevious.setEnabled(false);
						btnNext.setEnabled(false);
						btnStop.setEnabled(false);
						btnMarkIn.setEnabled(false);
						btnMarkOut.setEnabled(false);
						btnGoToIn.setEnabled(false);
						btnGoToOut.setEnabled(false);
						
						caseInH.setEnabled(false);
						caseInM.setEnabled(false);
						caseInS.setEnabled(false);
						caseInF.setEnabled(false);
						caseOutH.setEnabled(false);
						caseOutM.setEnabled(false);
						caseOutS.setEnabled(false);
						caseOutF.setEnabled(false);
						
						caseInternalTc.setEnabled(false);	
						caseInternalTc.setSelected(false);		
						
						lblPosition.setVisible(false);
						lblDuration.setVisible(false);	
						
						if (waveform != null)
						{
							waveform = null;
							waveformIcon.setIcon(null);
							waveformIcon.repaint();
						}
					}
					
					if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("processEnded")))
					{
						Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
					}
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					{
						caseInH.setVisible(false);
						caseInM.setVisible(false);
						caseInS.setVisible(false);
						caseInF.setVisible(false);
						caseOutH.setVisible(false);
						caseOutM.setVisible(false);
						caseOutS.setVisible(false);
						caseOutF.setVisible(false);
					}
					else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
					{
						if (Settings.btnDisableVideoPlayer.isSelected() == false)
						{
							caseInH.setVisible(true);
							caseInM.setVisible(true);
							caseInS.setVisible(true);
							caseInF.setVisible(true);
						}
						caseOutH.setVisible(false);
						caseOutM.setVisible(false);
						caseOutS.setVisible(false);
						caseOutF.setVisible(false);
					}
					else if (waveformScrollPane.isVisible())
					{
						caseInH.setVisible(true);
						caseInM.setVisible(true);
						caseInS.setVisible(true);
						caseInF.setVisible(true);
						caseOutH.setVisible(true);
						caseOutM.setVisible(true);
						caseOutS.setVisible(true);
						caseOutF.setVisible(true);
					}		
						
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && videoPath != null)
					{				
						File video = new File(videoPath);
						String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
						
						SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
						SubtitlesTimeline.timelineScrollBar.setMaximum(slider.getMaximum());
									
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			    		Shutter.frame.setLocation(Shutter.frame.getLocation().x , dim.height/3 - Shutter.frame.getHeight()/2);
			
			    		if (Shutter.caseAddSubtitles.isSelected())
			    		{
			    			VideoPlayer.player.remove(Shutter.subsCanvas);
							Shutter.caseAddSubtitles.setSelected(false);	    	
			    		}
							    		    	
			    		if (SubtitlesTimeline.frame == null) 
			    		{	    	
			    			new SubtitlesTimeline();		
			    		}
			    		else
			    		{
			    			SubtitlesTimeline.frame.setVisible(true);
			    			SubtitlesTimeline.frame.setLocation((Shutter.frame.getLocation().x + Shutter.frame.getWidth() / 2) - SubtitlesTimeline.frame.getWidth() / 2, Shutter.frame.getLocation().y + Shutter.frame.getHeight() + 7);
			    	    	
							SubtitlesTimeline.subtitlesNumber();					
							SubtitlesTimeline.timeline.remove(SubtitlesTimeline.waveform);
							SubtitlesTimeline.repaintTimeline();
							SubtitlesTimeline.timeline.removeAll();
							SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);	
			    		}
			    		
			    		playerFreeze();	
			    		
						Shutter.btnStart.setEnabled(false);						    		
						Shutter.comboFonctions.setEnabled(false);	
						
						//IMPORTANT Correct focus bug on Mac
						Shutter.frame.setVisible(false);
						Shutter.frame.setVisible(true);
					}
					else		
						resizeAll();
							
					if (Shutter.fileList.hasFocus() == false)
					{
						waveformContainer.requestFocus();
					}
		    	}
    		}    		
    		
		});
    	loadMedia.start();
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
			
			if (Shutter.liste.getSize() > 0)
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
			
			if (Shutter.liste.getSize() > 0 && videoPath != null && fullscreenPlayer == false && isPiping == false && Settings.btnDisableVideoPlayer.isSelected() == false)
			{
				showScale.setVisible(true);
			}
			else
				showScale.setVisible(false);
			
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
			
			if (Shutter.liste.getSize() > 0)
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
				showFPS.setVisible(false);
			}
			else
			{
				caseInternalTc.setVisible(true);
				caseInternalTc.setBounds(caseInH.getX() - 2, btnPrevious.getY() + btnPrevious.getHeight() + 6, caseInternalTc.getPreferredSize().width, 23);	
				casePlaySound.setBounds(caseInternalTc.getX() + caseInternalTc.getWidth() + 4, caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);
								
				if (Shutter.liste.getSize() > 0 && Settings.btnDisableVideoPlayer.isSelected() == false)
				{
					showScale.setVisible(true);
				}
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
    
	public static String setVideoCommand(float inputTime, int width, int height, boolean isPlaying) throws InterruptedException {

		//Deinterlacer		
		String yadif = AdvancedFeatures.setDeinterlace(true);		
		if (mouseIsPressed)
		{
			yadif = "";
		}
				
		//Speed slider
		String speed = "";
		if (sliderSpeed.getValue() != 2)
		{
			if (sliderSpeed.getValue() != 0)
			{
				speed += "setpts=" + (float) 1 / ((float) sliderSpeed.getValue() / 2) + "*PTS";
			}
			else
				speed += "setpts=4*PTS";				
		}	
				
		if (FFPROBE.audioOnly)
		{			
			//Important
			FFPROBE.accurateFPS = 25.0f;
			
			String filter = "";
			
			if (caseVuMeter.isSelected())
			{		
				String aspeed = "";
				
				if (sliderSpeed.getValue() != 2)
				{
					if (sliderSpeed.getValue() != 0)
					{
						aspeed += "atempo=" + ((float) sliderSpeed.getValue() / 2) + ",";
					}
					else
						aspeed += "atempo=0.5,atempo=0.5,";				
				}	
				
				String channels = "";
				String audioOutput = "";
				int i;
				for (i = 0; i < FFPROBE.channels; i++) {
					channels += "[0:a:" + i + "]" + aspeed + "showvolume=f=0:w=" + width + ":h=" + (int) Math.round(height / 30) + ":b=4:s=0[a" + i + "];";
					audioOutput += "[a" + i + "]";
				}
				
				if (FFPROBE.channels > 1)
				{
					audioOutput += "vstack=" + i + "[volume];";
					filter = " -filter_complex " + '"' + channels + audioOutput + "[1:v][volume]overlay=W*0.5-w*0.5:H*0.5-h*0.5" + '"';
				}
				else
				{
					audioOutput = audioOutput.replace("[a0]", "");
					filter = " -filter_complex " + '"' + channels + audioOutput + "[1:v][a0]overlay=W*0.5-w*0.5:H*0.5-h*0.5" + '"';
				}
			}
			
			return " -v quiet -hide_banner -ss " + (long) (inputTime * inputFramerateMS) + "ms -i " + '"' + videoPath + '"' + " -f lavfi -i " + '"' + "color=c=black:r=25:s=" + width + "x" + height + '"' + filter + " -c:v rawvideo -pix_fmt rgb24 -an -f rawvideo -";
		}
		else
		{
			String video = videoPath;
			String concat = "";
			
			//Image sequence
			if (Shutter.caseEnableSequence.isSelected())
			{		
				concat = FunctionUtils.setConcat(new File("concat.txt"), Shutter.dirTemp);					
				video = Shutter.dirTemp + "concat.txt";
			}	

			String gpuDecoding = "";
			
			if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && mouseIsPressed == false && previousFrame == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
			{
				if (FFMPEG.isGPUCompatible)
				{
					//Auto GPU Shutter.selection
					if (FFMPEG.cudaAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && setFilter(yadif, speed, false).contains("scale_cuda"))
					{
						gpuDecoding = " -hwaccel cuda -hwaccel_output_format cuda";
					}
					else if (FFMPEG.qsvAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && setFilter(yadif, speed, false).contains("scale_qsv"))
					{
						gpuDecoding = " -hwaccel qsv -hwaccel_output_format qsv";
					}	
					else if (FFMPEG.videotoolboxAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && setFilter(yadif, speed, false).contains("scale_vt"))
					{
						gpuDecoding = " -hwaccel videotoolbox -hwaccel_output_format videotoolbox_vld";
					}
					else if (FFMPEG.vulkanAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && setFilter(yadif, speed, false).contains("scale_vulkan"))
					{
						gpuDecoding = " -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan";
					}
					else
						gpuDecoding = " -hwaccel auto";
				}
				else if (System.getProperty("os.name").contains("Mac"))
				{
					gpuDecoding = " -hwaccel auto";
				}
			}

			String extension = videoPath.substring(videoPath.lastIndexOf("."));	
					
			int framesToSkip = (int) ((float) slider.getValue() - playerCurrentFrame);
			
			if (mouseIsPressed && (framesToSkip > 60 || framesToSkip < 0))
			{	
				freezeFrame = " -analyzeduration 0 -probesize 32 -frames:v 1";	
			}
			else
				freezeFrame = "";
			
			//Alpha
			String colorFormat = "rgb24";
			if (FFPROBE.hasAlpha)
				colorFormat = "rgba";
			
			String cmd = gpuDecoding + Colorimetry.setInputCodec(extension) + " -strict -2 -v quiet -hide_banner -ss " + (long) (inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + setFilter(yadif, speed, false) + " -r " + FFPROBE.accurateFPS + freezeFrame + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -f rawvideo -";
									
			String codec = "";
			if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != ""
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("DNxHD") == false
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("DVD") == false
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("QT Animation") == false
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("GoPro CineForm") == false
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100") == false)
			{
				String format = "matroska";
				
				if (Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC"))
				{
					format = "mxf";
				}	
				
				//Deinterlacer
				if (yadif != "")
				{
					yadif = " -vf " + yadif;
				}
				
				if (System.getProperty("os.name").contains("Windows"))
				{	
					codec = VideoEncoders.setCodec() + VideoEncoders.setBitrate() + AdvancedFeatures.setPreset() + yadif + freezeFrame + " -an -f " + format + " pipe:1 | " + '"' + FFMPEG.PathToFFMPEG + '"' + " -v quiet -hide_banner -i pipe:0" + setFilter("", speed, false);
				}
				else
					codec = VideoEncoders.setCodec() + VideoEncoders.setBitrate() + AdvancedFeatures.setPreset() + yadif + freezeFrame + " -an -f " + format + " pipe:1 | " + FFMPEG.PathToFFMPEG + " -v quiet -hide_banner -i pipe:0" + setFilter("", speed, false);	
								
				cmd = gpuDecoding + Colorimetry.setInputCodec(extension) + " -strict -2 -v quiet -hide_banner -ss " + (long) (inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + " -r " + FFPROBE.accurateFPS + codec + freezeFrame + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -f rawvideo -";
			}
									
			if (Shutter.inputDeviceIsRunning)
			{
				cmd = " -strict -2 -v quiet -hide_banner " + RecordInputDevice.setInputDevices() + setFilter(yadif, speed, false) + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -f rawvideo -";
			}

			Console.consoleFFMPEG.append(cmd + System.lineSeparator());

			return cmd;			
		}
	}
	
	public static String setAudioCommand(float inputTime, boolean frameByFrame) {
					
		String duration = "";
		if (frameByFrame)
		{
			duration = " -t " + (int) inputFramerateMS + "ms";
		}
		
		if (playTransition)
		{
			playTransition = false;
		}
		
		if (FFPROBE.hasAudio == false && (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false || Shutter.liste.getSize() == 1))
		{
			return " -v quiet -hide_banner -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"' + setAudioFilter() + duration +  " -vn -c:a pcm_s16le -ar 48k -ac 1 -f wav -";				
		}
		else
		{
			String input = " -i " + '"' + videoPath + '"';
			String mapping = "";
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) && Shutter.fileList.getSelectedIndex() + 1 < Shutter.liste.getSize())
			{
				if (Shutter.liste.getElementAt(Shutter.fileList.getSelectedIndex() + 1).contains("lavfi"))
				{
					input =  " -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"';
				}
				else
				{
					input = " -i " + '"' + Shutter.liste.getElementAt(Shutter.fileList.getSelectedIndex() + 1) + '"';
				}
			}
			else
			{	
				if (FFPROBE.channels > 0 && comboAudioTrack.isVisible())
				{
					if (comboAudioTrack.getSelectedItem() != null && comboAudioTrack.getSelectedItem().equals("Mix"))
					{						
						mapping = " -filter_complex amerge=inputs=" + FFPROBE.channels + setAudioFilter().replace(" -filter:a ", ",");
					}
					else
						mapping = " -map a:" + comboAudioTrack.getSelectedIndex() + setAudioFilter();
				}
				else
					mapping = setAudioFilter();
			}

			return " -v quiet -hide_banner -ss " + (long) (inputTime * inputFramerateMS) + "ms" + input + duration + " -vn -c:a pcm_s16le -ar 48k -ac 1" + mapping + " -f wav -";
		}		
		
	}
    
	private static void updateCurrentFrame() {
				
		if (sliderSpeed.getValue() != 2)
		{													
			if (sliderSpeed.getValue() != 0)
			{
				playerCurrentFrame += 1 * ((float) sliderSpeed.getValue() / 2);
			}
			else
				playerCurrentFrame += 1 * 0.25f;
		}
		else
			playerCurrentFrame += 1;	
	}
	        
	public static void addWaveform(boolean newWaveform) {
		
		if (caseShowWaveform.isSelected() && FFPROBE.hasAudio && addWaveformIsRunning == false && Shutter.frame.getSize().width > 654 && Settings.btnDisableVideoPlayer.isSelected() == false)
		{			
			addWaveformIsRunning = true;
			
			if (newWaveform || waveform == null)
			{
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				waveformIcon.setVisible(false);
				
				if (newWaveform)
				{					
					waveform = null;
				}
			}
						
			addWaveform = new Thread(new Runnable()
			{
				@Override
				public void run() {
					
					if (newWaveform || waveform == null)
					{							
						long size = 2000;

						String start = "";
						String duration = "";
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						{	
							do {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while (SubtitlesTimeline.frame == null);
							
							if (SubtitlesTimeline.waveform == null)
								SubtitlesTimeline.waveform = new JLabel();	
							
							long time = (long) (SubtitlesTimeline.timelineScrollBar.getValue() / SubtitlesTimeline.zoom);

							String h = Shutter.formatter.format(Math.floor(time / 1000) / 3600);
							String m = Shutter.formatter.format((Math.floor(time / 1000) / 60) % 60);
							String s = Shutter.formatter.format(Math.floor(time / 1000) % 60);    		
							String f = Shutter.formatterToMs.format(time % 1000);
							
							start = " -ss " + h + ":" + m + ":" + s + "." + f;
							duration = "atrim=duration=" + (SubtitlesTimeline.frame.getWidth() / 100) + ",";								
							size = (long) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom);
						}
						
						//IMPORTANT
						if (size > 549944)
							size = 549944;
						
						if (FFPROBE.channels > 1 && comboAudioTrack.isVisible())
						{		
							if (comboAudioTrack.getSelectedItem() != null && comboAudioTrack.getSelectedItem().equals("Mix"))
							{
								FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]aresample=1000,amerge=inputs=" + FFPROBE.channels + "," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green,format=rgba,colorkey=black:0.01" + '"'  + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
							else
							{
								FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a:" + comboAudioTrack.getSelectedIndex() + "]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
						}
						else
						{
							FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -");  																
						}
						
						if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
						{
							Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
						}
						else
							Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
					}
					
					//add Waveform		
					try {
						
						if (Shutter.liste.getSize() > 0 && isPiping == false && waveform != null)
						{
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles"))) //Ne charge plus l'image si la fenêtre est fermée entre temps
							{
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance((int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));						
								
								waveformIcon.setIcon(null);
								waveformIcon.repaint();
								
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
								SubtitlesTimeline.waveform.repaint();
							}
							else
							{	    						
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance(slider.getWidth() * waveformZoom, waveformContainer.getHeight(), Image.SCALE_AREA_AVERAGING));
								
								waveformIcon.setIcon(resizedWaveform);
								waveformIcon.repaint();

								if ((RenderQueue.frame != null && RenderQueue.frame.isVisible() && FFMPEG.isRunning) || isPiping || videoPath == null)
								{
									waveformIcon.setVisible(false);
								}
								else
									waveformIcon.setVisible(true);
							} 	
						}
					}
					catch (Exception e) {}
					finally
					{					
						addWaveformIsRunning = false;
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}	
				}				
			});
			addWaveform.start();
		}
	}
	
	@SuppressWarnings("serial")
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
								playerSetTime(playerCurrentFrame - 1);
							}
										
							frameControl = true;
														
							if (playerVideo != null)
							{										
								if (playerLoop)
								{
									btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));
									btnPlay.setName("play");
									playerLoop = false;
								}

								frameIsComplete = false;
								
								if (seekOnKeyFrames && FFPROBE.isRunning == false)
								{				
									FFPROBE.Keyframes(videoPath, (playerCurrentFrame - 2) * inputFramerateMS, false);
									
									do {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {}
									} while (FFPROBE.isRunning);
									
									if (FFPROBE.keyFrame > 0)
									{
										playerSetTime(FFPROBE.keyFrame);
									}
								}
								else if (bufferedFrames.size() > 0)
								{
									playerSetTime(playerCurrentFrame - 2);	
								}
								else
								{									
									playerSetTime(playerCurrentFrame - 1);	
									
									while (setTime.isAlive())
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
					playerSetTime(playerCurrentFrame + 1);
				}
				
				if (preview != null || Shutter.caseAddSubtitles.isSelected())
				{												
					playerSetTime(playerCurrentFrame + 1);
				}

				frameControl = true;
				
				if (playerVideo != null)
				{
					if (playerLoop)
					{
						btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));
						btnPlay.setName("play");
						playerLoop = false;
					}
					
					if (seekOnKeyFrames && FFPROBE.isRunning == false)
					{									
						FFPROBE.Keyframes(videoPath, playerCurrentFrame * inputFramerateMS, true);
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException er) {}
						} while (FFPROBE.isRunning);
		
						if (FFPROBE.keyFrame > 0)
						{	
							playerSetTime(FFPROBE.keyFrame);
						}
					}
					else
					{					
						//Allow to read 1 frame							
						playerSetTime(playerCurrentFrame + 1);
					}
				}	
			}
			
		});
		
		btnPlay = new JButton(new FlatSVGIcon("contents/play.svg", 15, 15));
		btnPlay.setMargin(new Insets(0,0,0,0));		
		btnPlay.setBackground(new Color(30,30,35, 0));
		btnPlay.setBorder(null);
		Shutter.frame.getContentPane().add(btnPlay);
			
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//Allows to wait for the last frame to load					
				while (setTime.isAlive())
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {}						
				} 
				
				if (btnPlay.getName().equals("pause"))
				{
					btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));	
					btnPlay.setName("play");
					playerLoop = false;
					showFPS.setVisible(false);
					
					if (sliderSpeed.getValue() != 2)
					{						
						playerSetTime(playerCurrentFrame);	
					}		
					else
					{
						//IMPORTANT Sync to the correct timecode
						if (Timecode.isDropFrame() == false)
						{
							getTimePoint(playerCurrentFrame); 
						}
					}
					
				}
				else if (btnPlay.getName().equals("play"))
				{						
					if (bufferedFrames.size() > 0 || preview != null || Shutter.caseAddSubtitles.isSelected() || previousFrame)
					{				
						if (bufferedFrames.size() > 0 || preview != null || Shutter.caseAddSubtitles.isSelected())
						{	
							//Clear the buffer
							bufferedFrames.clear();			
						}
						
						if (previousFrame)
						{
							//IMPORTANT enable GPU decoding after using btnPrevious
							previousFrame = false;
						}
												
						playerSetTime(playerCurrentFrame);
					}
					
					
					//Loop the player
					if (playerCurrentFrame >= totalFrames - 2)
					{
						playerSetTime(0);
						btnPlay.doClick();
					}
					
					frameControl = false;
					btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					btnPlay.setName("pause");
					playerLoop = true;
		            fpsTime = System.nanoTime();
		            displayCurrentFPS = 0;
				}
								
			}
			
		});
		
		btnStop = new JButton(new FlatSVGIcon("contents/stop.svg", 15, 15));		
		btnStop.setMargin(new Insets(0,0,0,0));	
		btnStop.setBackground(new Color(30,30,35, 0));
		btnStop.setBorder(null);
		Shutter.frame.getContentPane().add(btnStop);		
		
		btnStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (playerVideo != null)
				{				
					playerCurrentFrame = 0;
					
					long time = System.currentTimeMillis();
					
					if (playerVideo != null)
					{				
						playerStop();						
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
							
							if (System.currentTimeMillis() - time > 5000)
								break;
							
						} while (playerVideo.isAlive());
												
						slider.setValue(0);						
					}
										
					resizeAll();

					btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));
					btnPlay.setName("play");
					playerLoop = false;
										
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						SubtitlesTimeline.actualSubOut = 0;	

					playerCurrentFrame = 0;		
					
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
					updateGrpIn(playerCurrentFrame);
					Shutter.timecode.repaint();
					
					//FileList
					setFileList();
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
				
				playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
				
				//NTSC framerate
				playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
				
				playerSetTime(playerCurrentFrame);
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
					updateGrpOut(playerCurrentFrame + 1);
					
					//FileList
					setFileList();
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

				playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());

				//NTSC framerate
				playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
				
				playerSetTime(playerCurrentFrame - 1);
			}
			
		});
   
		panelForButtons = new JPanel() {
		
				@Override
				public void paintComponent(Graphics g) {
					
					Graphics2D g2d = (Graphics2D) g;
												
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			        
					g2d.setColor(Utils.c42);
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					{
						g2d.fillRoundRect(0, 0, (btnStop.getX() + btnStop.getWidth()) - btnPlay.getX() - 4, 21, 15, 15);
					}
					else
						g2d.fillRoundRect(0, 0, (btnGoToOut.getX() + btnGoToOut.getWidth()) - btnGoToIn.getX() - 4, 21, 15, 15);
									
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
		comboAudioTrack.setBorder(null);
		comboAudioTrack.setBackground(new Color(comboAudioTrack.getBackground().getRed(),comboAudioTrack.getBackground().getGreen(),comboAudioTrack.getBackground().getBlue(), 0));	
		comboAudioTrack.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		comboAudioTrack.setMaximumRowCount(16);		
		Shutter.frame.getContentPane().add(comboAudioTrack);

		comboAudioTrack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				addWaveform(true);
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
			}
			
		});		
	}
	
    @SuppressWarnings("serial")
	private void player() {		
    	
		player = new JPanel() {
			
            @Override
            protected void paintComponent(Graphics g) {
            	
                super.paintComponent(g);
                
                Graphics2D g2 = (Graphics2D)g;
                
                if (FFPROBE.hasAlpha)
                	drawCheckerboard((Graphics2D) g);
                               
                g2.setColor(Color.BLACK);
                
                if (playerIsPlaying() && frameVideo == null)
                {
                	long time = System.currentTimeMillis();
                	do {

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
						
						if (System.currentTimeMillis() - time > 5000)
							break;
						
					} while (frameVideo == null && playerIsPlaying());
                }
                
                if (frameVideo == null || Shutter.liste.getSize() == 0 || Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) && Shutter.caseDisplay.isSelected() == false)
                {
                	g2.fillRect(0, 0, player.getWidth(), player.getHeight()); 
                }
                else
                {
                	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            		g2.drawImage(frameVideo, 0, 0, player.getWidth(), player.getHeight(), this); 
                	
                	cursorCurrentFrame.setBounds(Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum()), 0, 1, waveformContainer.getHeight());
                }
                
                //Get the current fps
                if (FFPROBE.audioOnly == false && fileDuration > 40)
                {
	                if (System.nanoTime() - fpsTime >= 1000000000)
					{          	
	                	displayCurrentFPS = fps;
						fpsTime = System.nanoTime();
						fps = 0;
					}	              
	                	                
	                //Display current fps
		            if (displayCurrentFPS > 0 && playerLoop && sliderSpeed.getValue() == 2 && fullscreenPlayer == false && mouseIsPressed == false && Shutter.inputDeviceIsRunning == false)
		            {
		            	showFPS.setVisible(true);		            	
		            	if ((float) displayCurrentFPS >= FFPROBE.currentFPS)
		            	{
		            		showFPS.setForeground(Color.GREEN);
		            		
		            		String fps[] = String.valueOf(FFPROBE.accurateFPS).split("\\.");
		            		if (fps[1].equals("0"))
		            			showFPS.setText(String.valueOf(FFPROBE.currentFPS).replace(".0", "") + " " + Shutter.language.getProperty("fps"));
		            		else
		            			showFPS.setText(String.valueOf(FFPROBE.currentFPS) + " " + Shutter.language.getProperty("fps"));
		            	}
		            	else
		            	{
		            		showFPS.setForeground(Color.RED);
		            		showFPS.setText(String.valueOf(displayCurrentFPS) + " " + Shutter.language.getProperty("fps"));
		            	}
		            }
		            else
		            	showFPS.setVisible(false);
                }
                                         
                if (previewUpscale && preview != null && fileDuration > 40)
                {
                	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                	g2.setColor(Color.WHITE);
                	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(player.getHeight()/16))); 
                	FontMetrics metrics = g.getFontMetrics(g2.getFont());
                     
                    int x = (player.getWidth() - metrics.stringWidth(Shutter.language.getProperty("preview"))) / 2;                                	
                    int y = player.getHeight() - (int) (player.getHeight()/24);
                     
                	g2.drawString(Shutter.language.getProperty("preview"), x, y);
                	
                	previewUpscale = false;
                }
                                
                if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
                {
                	SubtitlesTimeline.refreshData();

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        
                    //On sépare les lignes
                    String text[] = SubtitlesTimeline.txtSubtitles.getText().split("\\r?\\n");                   
                    
                    if (text[0].contains("i>") && text[0].contains("b>"))
                    	g2.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, (int) Math.floor(player.getHeight()/16))); 
                    else if (text[0].contains("i>"))
                    	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(player.getHeight()/16))); 
                    else if (text[0].contains("b>"))
                    	g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.floor(player.getHeight()/16))); 
                    else
                    	g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.floor(player.getHeight()/16))); 
                    
                    String firstLine = text[0].replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", "");
                                    	
                    FontMetrics metrics = g.getFontMetrics(g2.getFont());
                    
                    int x = (player.getWidth() - metrics.stringWidth(firstLine)) / 2;                                	
                    int y = player.getHeight() - (int) (player.getHeight()/24);
                    
                    if (text.length > 1 && text[1].length() > 0)
                    {                                	                	
                    	y = player.getHeight() - (int) (player.getHeight()/9.5);                	
                    	g2.setColor(Color.BLACK);
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftSouth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftSouth(y, 1));
                    	g2.setColor(Color.WHITE);
                    	g2.drawString(firstLine, x, y);
                    	
                    	if (text[1].contains("i>") && text[1].contains("b>"))
                        	g2.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, (int) Math.floor(player.getHeight()/16))); 
                        else if (text[1].contains("i>"))
                        	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(player.getHeight()/16))); 
                        else if (text[1].contains("b>"))
                        	g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.floor(player.getHeight()/16))); 
                        else
                        	g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.floor(player.getHeight()/16))); 
                    	
                        String secondLine = text[1].replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", "");
    	 	            
    	 	            x = (player.getWidth() - metrics.stringWidth(secondLine)) / 2;
    	 	            y = player.getHeight() - (int) (player.getHeight()/24);
                    	
                    	g2.setColor(Color.BLACK);
                    	g2.drawString(secondLine, ShiftWest(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(secondLine, ShiftWest(x, 1), ShiftSouth(y, 1));
                    	g2.drawString(secondLine, ShiftEast(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(secondLine, ShiftEast(x, 1), ShiftSouth(y, 1));
                    	g2.setColor(Color.WHITE);
                    	g2.drawString(secondLine, x, y);
                    }
                    else if (firstLine.length() > 0)
                    {
                    	g2.setColor(Color.BLACK);
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftSouth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftSouth(y, 1));
                    	g2.setColor(Color.WHITE);
                    	g2.drawString(firstLine, x, y);
                    }                           		     
                }
            }
            
            private void drawCheckerboard(Graphics2D g2d) {
                int tileSize = 8;
                Color c1 = new Color(200, 200, 200);
                Color c2 = new Color(255, 255, 255);
                for (int y = 0; y < getHeight(); y += tileSize) {
                    for (int x = 0; x < getWidth(); x += tileSize) {
                        g2d.setColor(((x / tileSize + y / tileSize) % 2 == 0) ? c1 : c2);
                        g2d.fillRect(x, y, tileSize, tileSize);
                    }
                }
            }
            
            int ShiftNorth(int p, int distance) {
         	   return (p - distance);
         	   }
         	int ShiftSouth(int p, int distance) {
         	   return (p + distance);
         	   }
         	int ShiftEast(int p, int distance) {
         	   return (p + distance);
         	   }
         	int ShiftWest(int p, int distance) {
         	   return (p - distance);
         }
        };
        
        // Drag & Drop
 		player.setTransferHandler(new ListeFileTransferHandler());
        
 		player.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2 && Shutter.noSettings == false)
				{
					Shutter.windowDrag = true;
					
					if (fullscreenPlayer)
					{
						fullscreenPlayer = false;						
						
						Shutter.topPanel.setVisible(true);
						Shutter.grpChooseFiles.setVisible(true);
						Shutter.grpChooseFunction.setVisible(true);
						Shutter.grpDestination.setVisible(true);
						Shutter.grpProgression.setVisible(true);
						Shutter.statusBar.setVisible(true);
						
						Shutter.frame.getContentPane().setBackground(Utils.bg32);
												
						Shutter.changeSections(false);
						
						if (isPiping == false)
						{			
							setPlayerButtons(true);
							
				    		mouseIsPressed = false;
				    		
							playerSetTime(playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame			
						}
						
						resizeAll();
						
						Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, Shutter.frame.getWidth(), Shutter.frame.getHeight(), 15, 15));
			            Area shape2 = new Area(new Rectangle(0, Shutter.frame.getHeight()-15, Shutter.frame.getWidth(), 15));
			            shape1.add(shape2);
			    		Shutter.frame.setShape(shape1);
			    		
			    		if (isPiping == false)
			    			btnPlay.requestFocus();
					}
					else
					{
						fullscreenPlayer = true;	
												
						resizeAll();
						
						Shutter.frame.setShape(null);
						
						if (isPiping == false)
						{
							if (fileDuration <= 40 || Shutter.comboResolution.getSelectedItem().toString().contains("AI"))
							{	
								if (preview != null)
									preview = null;
								
								loadImage(true);
							}
							else						
								playerSetTime(playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
							
							//Load filter before removing groups								
							if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
							{
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException er) {}
								} while (runProcess.isAlive());
							}
							else
							{
								do {
									try {
										Thread.sleep(1);
									} catch (InterruptedException e1) {}
								} while (setTime.isAlive());
							}
						}
												
						Shutter.frame.requestFocus();
						
						Shutter.topPanel.setVisible(false);
						Shutter.grpChooseFiles.setVisible(false);
						Shutter.grpChooseFunction.setVisible(false);
						Shutter.grpDestination.setVisible(false);
						Shutter.grpProgression.setVisible(false);
						Shutter.statusBar.setVisible(false);
						
						Shutter.frame.getContentPane().setBackground(new Color(0,0,0));
						
						setPlayerButtons(false);
						
						Shutter.grpResolution.setVisible(false);
						Shutter.grpBitrate.setVisible(false);
						Shutter.grpSetAudio.setVisible(false);
						Shutter.grpAudio.setVisible(false);							
						Shutter.grpCrop.setVisible(false);
						Shutter.grpOverlay.setVisible(false);
						Shutter.grpSubtitles.setVisible(false);
						Shutter.grpWatermark.setVisible(false);					
						Shutter.grpAudio.setVisible(false);
						Shutter.grpColorimetry.setVisible(false);						
						Shutter.grpImageAdjustement.setVisible(false);
						Shutter.grpCorrections.setVisible(false);
						Shutter.grpTransitions.setVisible(false);						
						Shutter.grpImageSequence.setVisible(false);
						Shutter.grpImageFilter.setVisible(false);	
						Shutter.grpSetTimecode.setVisible(false);							
						Shutter.grpAdvanced.setVisible(false);
						Shutter.btnReset.setVisible(false);
					}
					
					Shutter.windowDrag = false;
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
									if (setTime != null)
									{
										do {
											Thread.sleep(1);
										} while (setTime.isAlive());
									}
										
									playerSetTime(playerCurrentFrame);
									
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
					int value = (int) ((long) slider.getMaximum() * e.getX() / player.getSize().width);
					sliderChange = true;					
					cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
					
					//Make sure the value is not more than file length less one frame
					if (value < (totalFrames))
						slider.setValue(value);
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
 			
 		});
 		
		player.setLayout(null);
		player.setBackground(Color.BLACK);
		Shutter.frame.getContentPane().add(player);		
	}
	    
	@SuppressWarnings("serial")
	private void sliders() {
		
		slider = new JSlider();
		slider.setPaintLabels(true);
		slider.setValue(0);
		slider.setVisible(false);
		slider.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2, 40);		
		Shutter.frame.getContentPane().add(slider);
						
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {

				if (playerVideo != null && sliderChange)
				{							
					if (slider.getValue() > 0)
					{							
						playerSetTime(slider.getValue());						
					}
					else
					{
						playerSetTime(0);												
					}											
				}
			}
			
		});
		
		waveformContainer = new JLabel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {		
	        	Graphics2D g2 = (Graphics2D)g; 
	        	
	            //Borders
                g2.setColor(new Color(55, 55, 55));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);	
                                
                if (Shutter.liste.getSize() > 0 && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
                {
	                //Mark in & out
                	if (caseApplyCutToAll.isSelected())
	                {
                		g2.setColor(Color.GRAY);          
	                }
                	else
                		g2.setColor(Utils.themeColor);
	                	                
	                if (playerOutMark > waveformContainer.getWidth() - 2)
	                	playerOutMark = waveformContainer.getWidth() - 2;	 
	                
	               if (playerInMark < 0)
	            	   playerInMark = 0;
	                
	                g2.drawRoundRect(playerInMark + 1, 0, playerOutMark - playerInMark, getHeight() - 1, 5, 5);	
	                
	                //Splitters
	                if (comboMode.isVisible() && comboMode.getSelectedItem().equals(Shutter.language.getProperty("splitMode")))
	                {
		                g2.setColor(Utils.themeColor);
		                int alpha = 255;
		                int splitTime = playerInMark + Math.round((float) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.accurateFPS / totalFrames));
		                do {
		                	
		                	g2.fillRect(splitTime + 1, 0, 1, getHeight() - 1);
		                	
		                	splitTime += Math.round((float) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.accurateFPS / totalFrames));
		                	
		                	g2.setColor(new Color(Utils.themeColor.getRed(), Utils.themeColor.getGreen(), Utils.themeColor.getBlue(), alpha));
		                	
		                	alpha -= 10;
		                	
		                	if (alpha < 0)
		                		break;		             
           	
		                } while (splitTime < playerOutMark);
	                }
	                	                
	                //Masks
	                g2.setColor(new Color(35,35,40,120)); 
	                if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
	                {
	                	g2.fillRoundRect(playerInMark + 2, 1, playerOutMark - playerInMark - 1, getHeight() - 2, 5, 5);
	                }
	                else
	                {
		                //Mask in                               	
		                g2.fillRoundRect(0, 0, playerInMark + 1, getHeight() - 1, 5, 5);
		                
		                //Mask out     
		                g2.fillRoundRect(playerOutMark + 2, 0, getWidth() - playerOutMark - 2, getHeight() - 1, 5, 5);
	                }
	                
	                totalDuration();
                }			
		    }
		};
		waveformContainer.setSize(slider.getWidth() * waveformZoom, slider.getHeight());
		waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));		
		waveformContainer.setLayout(null);	
		Shutter.frame.getContentPane().add(waveformContainer);
				
		cursorHead = new JPanel()
		{
	        @Override
	        protected void paintComponent(Graphics grphcs) {
	            super.paintComponent(grphcs);
	            Graphics2D g2d = (Graphics2D) grphcs;
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            
	            g2d.setColor(new Color(230,75,60));
	            
	            g2d.fillPolygon(new int[] {0, 5, 10}, new int[] {0, 8, 0}, 3);	            
	        }
		};

		cursorHead.setOpaque(false);
		cursorHead.setSize(10, 10);
		waveformContainer.add(cursorHead);
		
		cursorWaveform = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics grphcs) {
	            super.paintComponent(grphcs);
	            Graphics2D g2d = (Graphics2D) grphcs;
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            
	            g2d.setColor(new Color(230,75,60));
	            g2d.drawLine(0, 1, 0, waveformContainer.getHeight());	
	        }
		};
	
		cursorWaveform.setBounds(0, 0, 1, waveformContainer.getSize().height);		
		waveformContainer.add(cursorWaveform);	
			   
		waveformIcon = new JLabel();
		waveformIcon.setOpaque(false);
		waveformIcon.setLayout(null);
		waveformIcon.setSize(waveformContainer.getSize());
		waveformContainer.add(waveformIcon);

		//Important
		playerOutMark = waveformContainer.getWidth() - 2;
				
		waveformContainer.addMouseListener(new MouseListener(){

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
				
				mouseIsPressed = true;
				
				if (NCNN.isRunning)
				{
					NCNN.process.destroy();
					
					if (preview != null)
						preview = null;
				}
								
				if (Shutter.liste.getSize() > 0)
                {
					//IMPORTANT
					waveformContainer.requestFocus();
					
					sliderChange = true;
					
					if (playerIsPlaying())
					{
						btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));	
						btnPlay.setName("pause");
					}
					
					if (e.getX() >= 0 && e.getX() <= waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
					else if (e.getX() < 0)
					{				
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);	
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);	
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
					
					//Allows to wait for the last frame to load					
					while (setTime.isAlive())
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

				if (Shutter.liste.getSize() > 0)
                {	
					//Allows to wait for the last frame to load					
					while (setTime.isAlive())
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}						
					}	
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
					
					//NTSC framerate
					timeIn = Timecode.getNonDropFrameTC(timeIn);
					timeOut = Timecode.getNonDropFrameTC(timeOut);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * timeIn) / totalFrames);
					if ((int) Math.ceil(timeOut) < totalFrames)
					{
						playerOutMark = Math.round((float) (waveformContainer.getSize().width * timeOut - 1) / totalFrames);
					}
					else
						playerOutMark = waveformContainer.getWidth();

					if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && cursorWaveform.getX() < playerOutMark && mouseIsPressed)
					{							
						cursorWaveform.setLocation(playerInMark, 0);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						updateGrpIn(playerCurrentFrame);
					}
					else if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && cursorWaveform.getX() > playerInMark && mouseIsPressed)
					{			
						cursorWaveform.setLocation(playerOutMark, 0);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						updateGrpOut(playerCurrentFrame);
					}		
					
					sliderChange = false;								

					//Reload the frame to apply bicubic filter					
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (setTime.isAlive());

					if (Timecode.isNonDropFrame())
					{
						playerSetTime(playerCurrentFrame - 1);		
					}
					else
						playerSetTime(playerCurrentFrame);		
					
					waveformContainer.repaint();
					
					waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					//FileList
					setFileList();
                }								
			}
			
		});		

		waveformContainer.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {
									
				if (Shutter.liste.getSize() > 0)
                {
					if (e.getX() > 0 && e.getX() <= waveformContainer.getWidth() - 2)
					{
						int value = (int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width);
						sliderChange = true;					
						cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						//Make sure the value is not more than file length less one frame
						if (value < (totalFrames))
							slider.setValue(value);
					}
					else if (e.getX() <= 0)
					{					
						sliderChange = true;					
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);	
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						slider.setValue(0);
						playerSetTime(0);
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{
						sliderChange = true;					
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						
						slider.setValue((int) (totalFrames));
						playerSetTime(totalFrames);
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
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX() - slider.getWidth() + 1);
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
				
				if (waveformIcon.isVisible() && waveformIcon.getIcon() != null)
				{
					if (e.getWheelRotation() < 0)
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
					
					waveformContainer.setSize(slider.getWidth() * waveformZoom, slider.getHeight());
					waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));	
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
									
					//NTSC framerate
					timeIn = Timecode.getNonDropFrameTC(timeIn);
					timeOut = Timecode.getNonDropFrameTC(timeOut);
										
					playerInMark = Math.round((float) (waveformContainer.getSize().width * timeIn) / totalFrames);			
					if ((int) Math.ceil(timeOut) < totalFrames)
					{
						playerOutMark = Math.round((float) (waveformContainer.getSize().width * timeOut - 1) / totalFrames);
					}
					else
						playerOutMark = waveformContainer.getWidth();
					
					waveformContainer.repaint();
					
					cursorWaveform.setBounds(Math.round((float) (waveformContainer.getSize().width * slider.getValue()) / slider.getMaximum()), 0, 1, waveformContainer.getSize().height);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
					
					cursorCurrentFrame.setBounds((int) ((float) playerCurrentFrame * waveformContainer.getWidth() / totalFrames), 0, 1, waveformContainer.getHeight());
					
					waveformIcon.setSize(waveformContainer.getSize());
					
					ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance(waveformContainer.getWidth(), waveformContainer.getHeight(), Image.SCALE_AREA_AVERAGING));
					waveformIcon.setIcon(resizedWaveform);
					waveformIcon.repaint();
					
					//Center cursor
					int viewportWidth = waveformScrollPane.getViewport().getWidth();
					int newViewPosX = cursorWaveform.getX() - (viewportWidth / 2);
					int maxViewPosX = waveformContainer.getWidth() - viewportWidth;
					newViewPosX = Math.max(0, Math.min(newViewPosX, maxViewPosX));
					waveformScrollPane.getViewport().setViewPosition(new Point(newViewPosX, 0));
				}
			}
			
		});
					
		cursorCurrentFrame = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics grphcs) {
	            super.paintComponent(grphcs);
	            Graphics2D g2d = (Graphics2D) grphcs;
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));	            
	            
	            g2d.setColor(Color.WHITE);
	            g2d.drawLine(0, 1, 0, waveformContainer.getHeight());
	        }
		};
		waveformContainer.add(cursorCurrentFrame);
		
		waveformScrollPane = new JScrollPane();
		waveformScrollPane.getViewport().add(waveformContainer);
		waveformScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		waveformScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		waveformScrollPane.getViewport().setOpaque(false);
		waveformScrollPane.setBorder(null);
	    Shutter.frame.getContentPane().add(waveformScrollPane, BorderLayout.CENTER);
		
		sliderVolume.setName("sliderVolume");		
		sliderVolume.setValue(50);			
		Shutter.frame.getContentPane().add(sliderVolume);
				
		sliderVolume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if (playerAudio != null && playerAudio.isAlive())
				{
					double gain = (double) sliderVolume.getValue() / 100;   
			        float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
			        if (gainControl != null)
			        	gainControl.setValue(dB);
				}
			}
			
		});
		
		lblVolume = new JLabel(new FlatSVGIcon("contents/volume.svg", 15, 15));
		lblVolume.setFont(new Font("", Font.PLAIN, 12));
		lblVolume.setSize(lblVolume.getPreferredSize().width + 3, 16);			
		lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, sliderVolume.getY() + 2);	
		
		lblVolume.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (sliderVolume.getValue() > 0)
				{
					sliderVolume.setValue(0);
				}
				else
					sliderVolume.setValue(50);
				
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
				
		caseInH.setBounds(slider.getX() - 2, btnPrevious.getY(), 21, 21);
		caseInM.setBounds(caseInH.getX() + caseInH.getWidth(), caseInH.getY(), 21, 21);
		caseInS.setBounds(caseInM.getX() + caseInM.getWidth(), caseInH.getY(), 21, 21);
		caseInF.setBounds(caseInS.getX() + caseInS.getWidth(), caseInH.getY(), 21, 21);	
		
		sliderSpeed = new JSlider();
		sliderSpeed.setMaximum(4);
		sliderSpeed.setValue(2);
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
					
					if (slider.getValue() > 0)
					{
						frameIsComplete = false;
									
						playerSetTime(playerCurrentFrame);	
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
									
					if (slider.getValue() > 0)
					{
						frameIsComplete = false;
									
						playerSetTime(playerCurrentFrame);	
					}	
				}
			}

		});
				
		lblSpeed = new JLabel("x1"); //0.25 allow to get max preferred size width
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setFont(new Font(Shutter.mainFont, Font.PLAIN, 13));
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
					
					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);	
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);

					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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
	
	public static void updateGrpIn(float timeIn) {
			
		//NTSC framerate
		if (timeIn > 0)
			timeIn = Timecode.setNonDropFrameTC(timeIn);
				
		caseInH.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.accurateFPS / 3600)));
		caseInM.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.accurateFPS / 60) % 60));
		caseInS.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.accurateFPS) % 60));    		
		caseInF.setText(Shutter.formatter.format(Math.floor(timeIn % FFPROBE.accurateFPS)));
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
		{
			Shutter.txtAudioOffset.setText(String.valueOf((int) timeIn));
			
			if (timeIn > 0)
			{
				if (Shutter.caseAudioOffset.isSelected() == false)
				{
					Shutter.caseAudioOffset.doClick();
				}
				
			}
			else
			{
				if (Shutter.caseAudioOffset.isSelected())
				{
					Shutter.caseAudioOffset.doClick();
				}
			}
		}
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
				
		caseOutH.setBounds(slider.getX() + slider.getWidth() - (21) * 4, caseInH.getY(), 21, 21);
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
					
					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
										
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();

					//FileList
					setFileList();
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

	public static void updateGrpOut(float timeOut) {

		if (playerOutMark < waveformContainer.getWidth() - 2)
		{
			//NTSC framerate
			timeOut = Timecode.setNonDropFrameTC(timeOut);
		}	
		else
		{
			//NTSC framerate
			timeOut = Timecode.setNonDropFrameTC(totalFrames);
		}

		caseOutH.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.accurateFPS / 3600)));
		caseOutM.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.accurateFPS / 60) % 60));
		caseOutS.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.accurateFPS) % 60));    		
		caseOutF.setText(Shutter.formatter.format(Math.floor(timeOut % FFPROBE.accurateFPS)));
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
					if (waveform != null)
					{
						addWaveform(false);
					}
					else
						addWaveform(true);
				}
				else
				{
					if (addWaveformIsRunning)
					{									
						try {
							FFMPEG.waveformWriter.write('q');
							FFMPEG.waveformWriter.flush();
							FFMPEG.waveformWriter.close();
						} catch (IOException er) {}
						
						FFMPEG.waveformProcess.destroy();
					}
					
					waveformIcon.setVisible(false);
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
				
				if (bufferedFrames.size() > 0)
				{				
					//Clear the buffer
					bufferedFrames.clear();						
				}
				
				frameIsComplete = false;
							
				playerSetTime(playerCurrentFrame);
			}

		});
					
		casePlaySound.setName("casePlaySound");	
		casePlaySound.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		casePlaySound.setSelected(true);
		Shutter.frame.getContentPane().add(casePlaySound);
					
		caseInternalTc = new JCheckBox(Shutter.language.getProperty("caseTcInterne"));
		caseInternalTc.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		Shutter.frame.getContentPane().add(caseInternalTc);	
		

		btnPreview = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
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
				btnPreview.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnPreview.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
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
				
				btnPreview.setBounds(slider.getX() + slider.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
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
					FFPROBE.Data(videoPath);
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFPROBE.isRunning);
							
					
					if (FFPROBE.timecode1.equals(""))
	    			{
	    				MEDIAINFO.run(videoPath, false);
	    				
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
    			getTimePoint(playerCurrentFrame);
				sliderChange = false;
			}	
		});
		
	}
	
	public static void checkSelection() {
		
		float ratioW = (float) FFPROBE.imageWidth / player.getWidth();
		float ratioH = (float) FFPROBE.imageHeight / player.getHeight();
				
		int outW = (int) Math.round(Shutter.selection.getWidth() * ratioW);
		int outH = (int) Math.round(Shutter.selection.getHeight() * ratioH);
					
		int Px = (int) Math.round(Shutter.selection.getLocation().x * ratioW);
		int Py = (int) Math.round(Shutter.selection.getLocation().y * ratioH);
					
		if (Shutter.textCropWidth.getText().matches("[0-9]+") && Shutter.textCropHeight.getText().matches("[0-9]+"))
		{
			if (Px + Integer.valueOf(Shutter.textCropWidth.getText()) > FFPROBE.imageWidth)
			{
				Px = Px + (FFPROBE.imageWidth - (Px + Integer.valueOf(Shutter.textCropWidth.getText())));
			}
			
			if (Py + Integer.valueOf(Shutter.textCropHeight.getText()) > FFPROBE.imageHeight)
			{
				Py = Py + (FFPROBE.imageHeight - (Py + Integer.valueOf(Shutter.textCropHeight.getText())));
			}
						
			if (Integer.valueOf(Shutter.textCropWidth.getText()) != FFPROBE.imageWidth)
			{
				Shutter.textCropPosX.setText(String.valueOf(Px));
			}
			if (Integer.valueOf(Shutter.textCropHeight.getText()) != FFPROBE.imageHeight)
			{
				Shutter.textCropPosY.setText(String.valueOf(Py));
			}
		}
		else //First launch
		{
			Shutter.textCropPosX.setText(String.valueOf(Px));
			Shutter.textCropPosY.setText(String.valueOf(Py));
		}
				
		if (Shutter.frame.getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
		{
			Shutter.textCropWidth.setText(String.valueOf(outW));
			Shutter.textCropHeight.setText(String.valueOf(outH));
		}
	}
	
	public static void refreshTimecodeAndText() {
						
		//Colors	
		if (Shutter.foregroundColor != null)
		{
			 String c = Integer.toHexString(Shutter.foregroundColor.getRGB()).substring(2);
			 Shutter.foregroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}
		else
			Shutter.foregroundColor = new Color(255,255,255);
					
		if (Shutter.backgroundColor != null)
		{
			 String c = Integer.toHexString(Shutter.backgroundColor.getRGB()).substring(2);
			 Shutter.backgroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}	
		else
			Shutter.backgroundColor = new Color(0,0,0);

		if (Shutter.lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
		{	
			Shutter.backgroundTcAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(Shutter.textTcOpacity.getText()) * 255) / 100);
			Shutter.foregroundTcAlpha = "ff";
			Shutter.backgroundNameAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(Shutter.textNameOpacity.getText()) * 255) / 100);
		 	Shutter.foregroundNameAlpha = "ff";
		}
		else
		{
			Shutter.backgroundTcAlpha = "0";
			Shutter.foregroundTcAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(Shutter.textTcOpacity.getText()) * 255) / 100);
		 	Shutter.backgroundNameAlpha = "0";
			Shutter.foregroundNameAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(Shutter.textNameOpacity.getText()) * 255) / 100);
		}	
		
		if (Shutter.backgroundTcAlpha.length() < 2)
		{
			Shutter.backgroundTcAlpha = "0" + Shutter.backgroundTcAlpha;
		}
		if (Shutter.foregroundTcAlpha.length() < 2)	
		{
			Shutter.foregroundTcAlpha = "0" + Shutter.foregroundTcAlpha;
		}
		if (Shutter.backgroundNameAlpha.length() < 2)
		{
			Shutter.backgroundNameAlpha = "0" + Shutter.backgroundNameAlpha;
		}		
		if (Shutter.foregroundNameAlpha.length() < 2)
		{
			Shutter.foregroundNameAlpha = "0" + Shutter.foregroundNameAlpha;	
		}

		if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected())
		{				
			Shutter.textTcPosX.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().x * Shutter.playerRatio)));
			Shutter.textTcPosY.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().y * Shutter.playerRatio)));  
		}
		else
		{
			Shutter.textTcPosX.setText("0");
			Shutter.textTcPosY.setText("0"); 
		}
		
		if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected())
		{						
			Shutter.textNamePosX.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().x * Shutter.playerRatio)));
			Shutter.textNamePosY.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().y * Shutter.playerRatio)));  
		}
		else
		{	Shutter.textNamePosX.setText("0");
			Shutter.textNamePosY.setText("0"); 

		}			

	}

	public static void refreshSubtitles() {
		
		//Initialisation
		if (Shutter.alphaHeight == 0)
		{
			Shutter.alphaHeight = FFPROBE.imageHeight;
		}
		
		int v = Integer.parseInt(Shutter.textSubtitlesPosition.getText());
		int sz = Integer.parseInt(Shutter.textSubsSize.getText());
		int newValue = Math.round((float)sz*((float)Shutter.alphaHeight/(FFPROBE.imageHeight+v)));
		
		if (newValue > 0)
			Shutter.textSubsSize.setText(String.valueOf(newValue));
		
		Shutter.alphaHeight = (int) (FFPROBE.imageHeight + v);
		
		if (Integer.parseInt(Shutter.textSubsWidth.getText()) >= FFPROBE.imageWidth)
		{
			Shutter.subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
		}
		else
		{
			Shutter.subsCanvas.setSize((int) ((float) Integer.parseInt(Shutter.textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
		    		(int) (player.getHeight() + (float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
			
			Shutter.subsCanvas.setLocation((player.getWidth() - Shutter.subsCanvas.getWidth()) / 2, 0);
		}	
		
		loadImage(false);
	}
	
	public static void writeCurrentSubs(float inputTime, boolean firstSub) {	
				
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesFilePath.exists() &&  Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{
			try {
	
				BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(Shutter.subtitlesFilePath.toString()),  StandardCharsets.UTF_8);
	            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Shutter.subtitlesFile.toString()),  StandardCharsets.UTF_8);
	
    			Integer subsOffset = 0;	            
	            String line;
	            int subNumber = 0;
	            while ((line = bufferedReader.readLine()) != null)
	            {
	            	//Removes UTF-8 with BOM
	            	line = line.replace("\uFEFF", "");
		            	
	            	if (line.matches("[0-9]+"))
	             	{
	            		subNumber = Integer.parseInt(line);
	             		bufferedWriter.write(line);
	             		bufferedWriter.newLine();
	             	}
	            	
	        		if (line.contains("-->") )
	        		{ 	     
	        			//Offset
	        			if (subNumber == 1)
	        			{
	        				String s[] = line.split(":");
	        				subsOffset = Integer.parseInt(s[0]);
	        			}
	        			
	            		String split[] = line.split("-->");				
	            		String inTimecode[] = split[0].replace(",", ":").replace(" ","").split(":");
	            		String outTimecode[] = split[1].replace(",", ":").replace(" ","").split(":");
		            		
	    				int inH = (Integer.parseInt(inTimecode[0]) - subsOffset) * 3600;
	    				int inM = Integer.parseInt(inTimecode[1]) * 60;
	    				int inS = Integer.parseInt(inTimecode[2]);
	    				int inF = Integer.parseInt(inTimecode[3]);
	    				float subsInTime = (inH + inM + inS) * FFPROBE.accurateFPS + inF / inputFramerateMS;
	    				
	    				if (subNumber == 1 && firstSub)
	            		{
	    					sliderChange = true;
	    					slider.setValue((int) subsInTime);
	    					sliderChange = false;
	    					waveformContainer.repaint();
	            		}
	    				
	    				int outH = (Integer.parseInt(outTimecode[0]) - subsOffset) * 3600;
	    				int outM = Integer.parseInt(outTimecode[1]) * 60;
	    				int outS = Integer.parseInt(outTimecode[2]);
	    				int outF = Integer.parseInt(outTimecode[3]);
	    				float subsOuTime = (outH + outM + outS) * FFPROBE.accurateFPS + outF / inputFramerateMS;
	
	    				long inOffset = (long) (subsInTime - inputTime);
						long outOffset = (long) (subsOuTime - inputTime);
						
						String iH = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS / 3600));
						String iM = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS / 60) % 60);
						String iS = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS) % 60);    		
						String iF = Shutter.formatterToMs.format(Math.floor(inOffset % FFPROBE.accurateFPS * inputFramerateMS));
						
						String oH = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS / 3600));
						String oM = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS / 60) % 60);
						String oS = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS) % 60);    		
						String oF = Shutter.formatterToMs.format(Math.floor(outOffset % FFPROBE.accurateFPS * inputFramerateMS));
						
						bufferedWriter.write(iH + ":" + iM + ":" + iS + "," + iF + " --> " + oH + ":" + oM + ":" + oS + "," + oF);
	            		bufferedWriter.newLine();
	        		}
	        		else if (line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
	        		{           			
	        			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
	    					bufferedWriter.write("\\h" + line + "\\h");
	        			else
	        				bufferedWriter.write(line);
	    			
	        			bufferedWriter.newLine();
	        		}
	        		else if (line.isEmpty())
	        		{
	        			bufferedWriter.newLine();
	        		}
	
	            }   
	
	            bufferedReader.close();  
	            bufferedWriter.close();
	            	            
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean loadWatermark(int size) {
				
		if (FFPROBE.imageWidth != FFPROBE.previousImageWidth || FFPROBE.imageHeight != FFPROBE.previousImageHeight || Shutter.logoPNG == null)	
		{			
			try {
			
				if (Shutter.logoPNG == null)
				{				
					int previousImageWidth = FFPROBE.previousImageWidth;
					
					//IMPORTANT
					if (FFPROBE.isRunning)
					{
						do {								
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {}	
						} while (FFPROBE.isRunning);
					}
					
					//Keep media size
					FFPROBE.previousImageWidth = previousImageWidth;
					
					if (Shutter.overlayDeviceIsRunning)
					{
						RecordInputDevice.setOverlayDevice();
					}
					else 
					{
						FFPROBE.Data(Shutter.logoFile);					
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
					}
					
					Shutter.logoWidth = FFPROBE.imageWidth;
					Shutter.logoHeight = FFPROBE.imageHeight;
					
					//IMPORTANT keeps original source file data			
					FunctionUtils.analyze(new File(videoPath), false);
				}
										
				int logoFinalSizeWidth = (int) Math.floor((float) Shutter.logoWidth / Shutter.playerRatio);		
				int logoFinalSizeHeight = (int) Math.floor((float) Shutter.logoHeight / Shutter.playerRatio);
						
				//Adapt to size
				logoFinalSizeWidth = (int) Math.floor((float) logoFinalSizeWidth * ((double) size / 100));
				logoFinalSizeHeight = (int) Math.floor((float) logoFinalSizeHeight * ((double) size / 100));
				
				if (logoFinalSizeWidth < 1)
					logoFinalSizeWidth = 1;
				
				if (logoFinalSizeHeight < 1)
					logoFinalSizeHeight = 1;
					
				//Preserve location
				int newPosX = (int) Math.floor((Shutter.logo.getWidth() - logoFinalSizeWidth) / 2);
				int newPosY = (int) Math.floor((Shutter.logo.getHeight() - logoFinalSizeHeight) / 2);
				
				if (Shutter.logoPNG == null)
				{
					if (Shutter.overlayDeviceIsRunning)
					{
						FFMPEG.run(" -v quiet -hide_banner " + RecordInputDevice.setOverlayDevice() + " -frames:v 1 -an -c:v rawvideo -pix_fmt rgba -sws_flags fast_bilinear -f rawvideo -");
					}
					else if (Shutter.logoPNG == null)
					{
						FFMPEG.run(" -v quiet -hide_banner -i " + '"' + Shutter.logoFile + '"' + " -frames:v 1 -an -c:v rawvideo -pix_fmt rgba -sws_flags fast_bilinear -f rawvideo -");
					}
					
					do {
						Thread.sleep(10);
					} while (FFMPEG.process.isAlive() == false);
					
					InputStream videoInput = FFMPEG.process.getInputStream(); 
					InputStream is = new BufferedInputStream(videoInput);		
					
					int frameSize = Shutter.logoWidth * Shutter.logoHeight * 4;
					byte[] frameData = new byte[frameSize];

			        int read = is.readNBytes(frameData, 0, frameData.length);
			        if (read == frameData.length)
			        {
			        	BufferedImage frame = new BufferedImage(Shutter.logoWidth, Shutter.logoHeight, BufferedImage.TYPE_4BYTE_ABGR);
			            frame.getRaster().setDataElements(0, 0, Shutter.logoWidth, Shutter.logoHeight, frameData);
			            
			            fullSizeWatermark = frame;
			        }       
				}
				
				Shutter.logoPNG = new ImageIcon(fullSizeWatermark).getImage().getScaledInstance(logoFinalSizeWidth, logoFinalSizeHeight, Image.SCALE_AREA_AVERAGING);
							
				if (Shutter.logo.getWidth() == 0)
				{
					Shutter.logo.setLocation((int) Math.floor(player.getWidth() / 2 - logoFinalSizeWidth / 2), (int) Math.floor(player.getHeight() / 2 - logoFinalSizeHeight / 2));	
				}
				else
					Shutter.logo.setLocation(Shutter.logo.getLocation().x + newPosX, Shutter.logo.getLocation().y + newPosY);
	
				Shutter.logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);        
	            			
				Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.playerRatio) ) ) );
				Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.playerRatio) ) ) );  
				
	            //Saving location
				Shutter.logoLocX = Shutter.logo.getLocation().x;
				Shutter.logoLocY = Shutter.logo.getLocation().y;	
	            Shutter.logo.repaint();        
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
			} 
			finally {
				
				if (Shutter.watermarkPreset != null)
				{
					watermarkPositions(Shutter.watermarkPreset);
				}
				
				Shutter.btnStart.setEnabled(true);
				
	        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
	        }
		}
		return true;
	}
	
	public static void watermarkPositions(String preset) {

		Shutter.watermarkPreset = preset;
		
		int offsetX = (int) ((float) player.getWidth() * 0.036);
		int offsetY = (int) ((float) player.getHeight() * 0.036);
		
		if (preset.equals("watermarkTopLeft"))
		{
			Shutter.logo.setLocation(offsetX, offsetY);	
		}
		else if (preset.equals("watermarkLeft"))
		{
			Shutter.logo.setLocation(offsetX, (int) Math.floor(VideoPlayer.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));	
		}
		else if (preset.equals("watermarkBottomLeft"))
		{
			Shutter.logo.setLocation(offsetX, (int) Math.floor(VideoPlayer.player.getHeight() - Shutter.logo.getHeight()) - offsetY);	
		}
		else if (preset.equals("watermarkTop"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), offsetY);	
		}
		else if (preset.equals("watermarkCenter"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), (int) Math.floor(VideoPlayer.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottom"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), (int) Math.floor(VideoPlayer.player.getHeight() - Shutter.logo.getHeight()) - offsetY);
		}
		else if (preset.equals("watermarkTopRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() - Shutter.logo.getWidth()) - offsetX, offsetY);
		}
		else if (preset.equals("watermarkRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() - Shutter.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayer.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottomRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() - Shutter.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayer.player.getHeight() - Shutter.logo.getHeight()) - offsetY);
		}
				
		Shutter.logoLocX = Shutter.logo.getLocation().x;
		Shutter.logoLocY = Shutter.logo.getLocation().y;
		Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.playerRatio) ) ) );
		Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.playerRatio) ) ) );
	}
	
	public static void loadImage(boolean forceRefresh) {

		if (forceRefresh && videoPath != null)
		{
			Thread waitProcess = new Thread (new Runnable() {
				
				@Override
				public void run() {
										
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (runProcess.isAlive());
				}
			});
			waitProcess.start();
		}
		
		if ((forceRefresh || runProcess.isAlive() == false) && videoPath != null && Shutter.liste.getSize() >  0 && Shutter.doNotLoadImage == false)
		{				
			runProcess = new Thread (new Runnable() {

			@Override
			public void run() {
												
					//Clear the buffer
					if (bufferedFrames.size() > 0)
					{				
						bufferedFrames.clear();
					}
								
					//Stop player
					if (playerIsPlaying())
					{
						btnPlay.doClick();
					}
				
			        try
			        {	
			        	do {
			        		Thread.sleep(10);
			        	} while (videoPath == null);
			        		
			        	File file = new File(videoPath);
			        			        						
						String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
						boolean isRaw = false;
						
						//FFprobe with RAW files
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
						
						if (Shutter.caseShowTimecode.isSelected() && FFPROBE.timecode1.equals(""))
						{
							Shutter.caseShowTimecode.setSelected(false);
							Shutter.caseShowTimecode.setEnabled(false);
							Shutter.caseAddTimecode.setSelected(true);
							Shutter.TC1.setEnabled(true);
							Shutter.TC2.setEnabled(true);
							Shutter.TC3.setEnabled(true);
							Shutter.TC4.setEnabled(true);	
						}			
								
						//Deinterlace
						String deinterlace = "";
						
						//Alpha
						String colorFormat = "rgb24";
						if (FFPROBE.hasAlpha)
							colorFormat = "rgba";
						
						if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
							deinterlace = " -vf yadif=0:" + FFPROBE.fieldOrder + ":0";		
	
						//Input point
						String inputPoint = " -ss " + (float) (playerCurrentFrame) * inputFramerateMS + "ms";
						if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
							inputPoint = "";
															
						//Creating preview file																
						String cmd = deinterlace + " -frames:v 1 -an -s " + player.getWidth() + "x" + player.getHeight() + " -sws_flags bicubic -y ";	
						if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
						{
							cmd = deinterlace + " -frames:v 1 -an -s " + player.getHeight() + "x" + player.getWidth() + " -sws_flags bicubic -y ";
						}
																		
						if (preview == null && Shutter.caseAddSubtitles.isSelected() == false)
						{
							if (extension.toLowerCase().equals(".pdf"))
							{
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								PDF.run(file, 0);
								
					            do {
					            	Thread.sleep(10);  
					            } while (PDF.isRunning && PDF.error == false);
							}
							else if (isRaw)
							{									
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								DCRAW.run(" -v -w -c -q 0 -o 1 -h -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
								
					            do {
					            	Thread.sleep(10);  					            	
					            } while (DCRAW.isRunning && DCRAW.error == false);	
							}
							else if (Shutter.comboResolution.getSelectedItem().toString().contains("AI"))							
							{													
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								
								File preview = new File(Shutter.dirTemp + "preview.png");
								
								FFMPEG.run(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + deinterlace + " -frames:v 1 -an " + '"' + preview + '"');		
								
								do {
					            	Thread.sleep(10);  
					            } while (FFMPEG.isRunning && FFMPEG.error == false);
								
								String model = "realesr-general-wdn-x4v3";							
								if (Shutter.comboResolution.getSelectedItem().toString().contains("animation"))
								{
									model = "realesrgan-x4plus-anime";
								}	

								Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
								Shutter.lblCurrentEncoding.setText(new File(videoPath).getName());
																								
								NCNN.run(" -v -i " + '"' + preview + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + preview + '"', true);

								do {									
									Thread.sleep(10);
								} while (NCNN.isRunning);
															
								Shutter.progressBar1.setValue(0);
								Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
																
								if (preview.exists())
								{									
									generatePreview(" -v quiet -hide_banner -i " + '"' + preview + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -"); 

									if (mouseIsPressed == false)
									{
										previewUpscale = true;
									}
								}
								else
								{
									generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + '"' + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
								}
								
								do {
									preview.delete();
								} while (preview.exists());
							}		
							else									
							{	
								generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -f rawvideo -");
							}		
				            
				            Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				            
						}	
						
						if (Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false && (preview != null || Shutter.caseAddSubtitles.isSelected()))
						{						
							//Subtitles are visible only from a video file
							if (Shutter.caseAddSubtitles.isSelected())
							{				
								generatePreview(Colorimetry.setInputCodec(extension) + " -v quiet -hide_banner" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter("","", true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + colorFormat + " -an -f rawvideo -"); 
							}
							else
							{		
								generatePreview(" -v quiet -hide_banner -i pipe:0" + setFilter("","", true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
							}
						}
			        }
				    catch (Exception e)
				    {				
				    	e.printStackTrace();
			 	       	//JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
				    }
			        finally 
			        {	
			        	while (FFMPEG.isRunning)
			        	{
			        		try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
			        	} 
						
	          			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
	        			else
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
						
			        }
				}
			});
			runProcess.start();
		}
	}

	private static void generatePreview(String cmd) {
		
		try {		
						
			Process process;
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + cmd);
				process = pbv.start();	
			}	
			else
			{
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + cmd);
				process = pbv.start();	
			}	
						
			Console.consoleFFMPEG.append(cmd + System.lineSeparator());
			
			if (preview != null)
			{
		        OutputStream outputStream = process.getOutputStream();
		        
		        if (FFPROBE.hasAlpha)
		        {
		        	ImageIO.write(preview, "png", outputStream);		        
		        }
		        else
		        	ImageIO.write(preview, "bmp", outputStream);		   
		        
		        outputStream.close();
			}				     	
	        
	        InputStream is = process.getInputStream();				
			BufferedInputStream inputStream = new BufferedInputStream(is);

			if (preview == null && Shutter.caseAddSubtitles.isSelected() == false)
			{
				preview = (BufferedImage) readFrame(inputStream, player.getWidth(), player.getHeight());
				frameVideo = preview;
			}
			else
				frameVideo = readFrame(inputStream, player.getWidth(), player.getHeight());
			
			inputStream.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (frameVideo != null)
		{
			player.repaint();
		}
		
	}
	
	public static void writeSub(String srt, Charset encoding) 
	{
		try {
			
			writeCurrentSubs(playerCurrentFrame, true);
			loadImage(true);

		} catch (Exception e) {
			
			if (encoding == StandardCharsets.UTF_8)
			{						
				writeSub(srt, StandardCharsets.ISO_8859_1);
			}
			else					
			{		
				Shutter.caseAddSubtitles.setSelected(false);
				player.remove(Shutter.subsCanvas);
			}
		}
	}
	
	private static String setFilter(String yadif, String speed, boolean noGPU) {
				
		//Subtitles
		String background = "" ;
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{			
			//Color	
			if (Shutter.fontSubsColor != null)
			{
				 String c = Integer.toHexString(Shutter.fontSubsColor.getRGB()).substring(2);
				 Shutter.subsHex = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}
			
			if (Shutter.backgroundSubsColor != null)
			{
				 String c = Integer.toHexString(Shutter.backgroundSubsColor.getRGB()).substring(2);
				 Shutter.subsHex2 = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}		
			
			
			Shutter.subsAlpha = "00";
			Shutter.outline = "1";
			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
			{
				int o = (int) (255 - (float) ((int) Integer.valueOf(Shutter.textSubsOutline.getText()) * 255) / 100);
				Shutter.subsAlpha = Integer.toHexString(o);
			}
			else
			{
				Shutter.outline = String.valueOf((float) ((float) ((int) Integer.valueOf(Shutter.textSubsOutline.getText())) * 2) / 100);
			}
			
			//Fond sous-titres							
			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				background = ",BorderStyle=4,BackColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&,outline=0";
			else
				background = ",outline=" + Shutter.outline + ",outlineColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&";
				
			//Bold
			if (Shutter.btnG.getForeground() != Color.BLACK)
				background += ",Bold=1";
			
			//Italic
			if (Shutter.btnI.getForeground() != Color.BLACK)
				background += ",Italic=1";
		}
		
		//Global Filter
		String filter = " -vf " + '"';

		//Deinterlacer
		if (yadif != "")
		{
			filter += yadif + ",";
		}	
		
		//Zoom
		if (Shutter.sliderZoom.getValue() != 0)
		{		
			filter = Colorimetry.setZoom(filter, false) + ",";
		}		

		//Scaling
		int width = player.getWidth();
		int height = player.getHeight();

		//Crop & Pad
		if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false && Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false && noGPU == false && Shutter.inputDeviceIsRunning == false)
		{			
			filter += settings.Image.setScale("", false);
			
			if (filter.contains("scale"))
			{
				filter += settings.Image.setPad("", false) + ",";
			}
		}
		else			
		{
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
			{
				width = player.getHeight();
				height = player.getWidth();		
			}
		}
		
		if (Shutter.grpColorimetry.isVisible() && Shutter.caseColormatrix.isSelected() && Shutter.comboInColormatrix.getSelectedItem().equals("HDR") == false)
		{
			//IMPORTANT scaling must be a multiple of 4!
			width = (width - (width % 4));
			height = (height - (height % 4));
		}
				
		String algorithm = "bilinear";
		if (mouseIsPressed)
		{
			algorithm = "neighbor";
		}

		String extension = videoPath.substring(videoPath.lastIndexOf("."));	
		
		if (Shutter.inputDeviceIsRunning)
		{
			filter += "null";
		}
		else if (FFMPEG.isGPUCompatible && FFPROBE.isRotated == false
		&& Shutter.comboGPUDecoding.getSelectedItem().toString().equals("auto")
		&& Shutter.comboGPUFilter.getSelectedItem().toString().equals("auto")
		&& noGPU == false && previousFrame == false
		&& mouseIsPressed == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
		&& Colorimetry.setInputCodec(extension) == ""
		&& Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")))
		{
			String bitDepth = "nv12";
			if (FFPROBE.imageDepth == 10)
			{
				bitDepth = "p010";
			}			
			
			//Auto GPU
			if (FFMPEG.cudaAvailable)
			{
				filter = filter.replace("yadif", "yadif_cuda");			
				filter += "scale_cuda=" + width + ":" + height + ":interp_algo=" + algorithm.replace("neighbor", "nearest").replace("bilinear", "bicubic") + ",hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.qsvAvailable && yadif == "")
			{
				filter += "scale_qsv=" + width + ":" + height + ":mode=low_power,hwdownload,format=" + bitDepth;
			}	
			else if (FFMPEG.videotoolboxAvailable && yadif == "")
			{
				filter += "scale_vt=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.vulkanAvailable && yadif == "")
			{
				filter += "scale_vulkan=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}
			else
			{
				filter += "scale=" + width + ":" + height + ":sws_flags=" + algorithm + ":sws_dither=none";
			}
		}
		else
		{
			filter += "scale=" + width + ":" + height + ":sws_flags=" + algorithm + ":sws_dither=none";		
		}				
		
		//Speed
		if (speed != "")
		{
			filter += "," + speed;
		}			
		
		//EQ
		setEQ = "";	
		
		//Stabilisation
		if (Shutter.stabilisation != "")
			setEQ = Shutter.stabilisation;
		
		//Blend
		if (preview == null) //Show only on playing
		{
			setEQ = ImageSequence.setBlend(setEQ);
			setEQ = ImageSequence.setMotionBlur(setEQ);
		}
		
		//LUTs
		setEQ = Colorimetry.setLUT(setEQ);	
		
		//Levels
		setEQ = Colorimetry.setLevels(setEQ);
		
		//Colormatrix
		setEQ = Colorimetry.setColormatrix(setEQ);
		
		//Rotate
		if (Shutter.caseRotate.isSelected() || Shutter.caseMiror.isSelected())
			setEQ = settings.Image.setRotate(setEQ);
		
		//Colorimetry
		if (Shutter.caseEnableColorimetry.isSelected())
		{			
			String color = Colorimetry.setEQ(false);
						
			if (setEQ != "" && color != "")
			{
				setEQ += "," + color;
			}
			else if (color != "")
			{
				setEQ += color;
			}
			
			if (Shutter.sliderAngle.getValue() != 0)
			{
				if (setEQ.contains("scale"))
				{
					setEQ = setEQ.replace("scale=" + FFPROBE.imageWidth + ":" + FFPROBE.imageHeight,  "scale=" + player.getWidth() + ":" + player.getHeight());
				}
				else
				{
					setEQ += ",scale=" + player.getWidth() + ":" + player.getHeight();
				}
			}
		}
				
		//Deflicker			
		setEQ = Corrections.setDeflicker(setEQ);
			
		//Deband			
		setEQ = Corrections.setDeband(setEQ);
				 
		//Details			
		setEQ = Corrections.setDetails(setEQ);				
											            	
		//Denoise			
		setEQ = Corrections.setDenoiser(setEQ);
		
		//Exposure
		if (preview == null) //Show only on playing
			setEQ = Corrections.setSmoothExposure(setEQ);	
		
		//Limiter
		setEQ = Corrections.setLimiter(setEQ);

		//Fade-in Fade-out
		if (Shutter.caseVideoFadeIn.isSelected() || Shutter.caseVideoFadeOut.isSelected())
		{
			setEQ = Transitions.setVideoFade(setEQ, true);
		}
		
		/*
		//Interpolation
		setEQ = AdvancedFeatures.setInterpolation(setEQ);
		
		//Slow motion
		setEQ = AdvancedFeatures.setSlowMotion(setEQ);
							
        //PTS
		setEQ = AdvancedFeatures.setPTS(setEQ);		      		                     	

		//Conform
		setEQ = AdvancedFeatures.setConform(setEQ);
		*/
								
		if (setEQ.isEmpty() == false)
		{
			filter += "," + setEQ;
		}
		
		if (caseVuMeter.isSelected() && FFPROBE.hasAudio && Shutter.caseAddSubtitles.isSelected() == false && preview == null)
		{
			String aspeed = "";
						
			if (sliderSpeed.getValue() != 2)
			{
				if (sliderSpeed.getValue() != 0)
				{
					aspeed += "atempo=" + ((float) sliderSpeed.getValue() / 2) + ",";
				}
				else
					aspeed += "atempo=0.5,atempo=0.5,";				
			}	
			
			String channels = "";
			String audioOutput = "";
			int i;
			for (i = 0; i < FFPROBE.channels; i++) {
				channels += "[0:a:" + i + "]" + aspeed + "showvolume=f=0:w=" + player.getWidth() + ":h=" + (int) Math.round(player.getHeight() / 90) + ":t=0:b=0:v=0:o=v:s=0:p=0.5[a" + i + "];";
				audioOutput += "[a" + i + "]";
			}
			
			if (FFPROBE.channels > 1)
			{
				audioOutput += "hstack=" + i + "[volume];";
			}
			else
			{
				audioOutput = audioOutput.replace("[a0]", "");
				channels = channels.replace("[a0]", "[volume]");
			}
			
			filter = " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];" + channels + audioOutput + "[v][volume]overlay=W-w:H-h";
		}
		
		//Close filter
		filter += '"';		
		
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesBurn && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{						
			caseVuMeter.setEnabled(false);
			
			int subsWidth = (int) ((float) (Integer.parseInt(Shutter.textSubsWidth.getText()) / Shutter.playerRatio));
			int subsPosY = (int) ((float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / Shutter.playerRatio);
			
			filter = " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + subsWidth + ":" + player.getHeight() + "+" + subsPosY
          			+ ",subtitles='" + Shutter.subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Shutter.textSubsSize.getText() + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"'
          			+ " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];[v][1:v]overlay=x=" + (int) ((player.getWidth() - subsWidth) / 2) + ",scale=" + player.getWidth() + ":" + player.getHeight() + '"';	
		}
		else
		{
			caseVuMeter.setEnabled(true);				
		}
		
		return filter;
	}

	private static String setAudioFilter() {
		
		String filter = "";	
		
		//EQ
		filter = AudioSettings.setEQ(filter);
		
		if (sliderSpeed.getValue() != 2)
		{
			if (filter != "") filter += ",";
			
			if (sliderSpeed.getValue() != 0)
				filter += "atempo=" + (float) sliderSpeed.getValue() / 2;
			else
				filter += "atempo=0.5,atempo=0.5";
		}
				
		if (Shutter.caseAudioFadeIn.isSelected() || Shutter.caseAudioFadeOut.isSelected())
		{
			if (filter != "") filter += ",";	
			
			filter += Transitions.setAudioFadeIn(true);
			
			if (Transitions.setAudioFadeIn(true) != "" && Transitions.setAudioFadeOut(true) != "")
			{
				filter += ",";
			}
			
			filter += Transitions.setAudioFadeOut(true);
		
		}
		
		if (filter != "")
		{
			filter = " -filter:a " + filter;
		}

		return filter;
	}
	
	public static boolean getFileList(String file) {
		
		try {
			
			if (fileList.length() > 0 && fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
			{
				for (String line : fileList.toString().split(System.lineSeparator()))
				{	
					String s[] = line.split("\\|");
					String in[] = s[1].split(":");
					String out[] = s[2].split(":");
					
					if (s[0].equals(file))
					{			
						caseInH.setText(in[0]);
						caseInM.setText(in[1]);
						caseInS.setText(in[2]);
						caseInF.setText(in[3]);
						
						caseOutH.setText(out[0]);
						caseOutM.setText(out[1]);
						caseOutS.setText(out[2]);
						caseOutF.setText(out[3]);

						float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
						float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
						
						//NTSC framerate
						timeIn = Timecode.getNonDropFrameTC(timeIn);
						timeOut = Timecode.getNonDropFrameTC(timeOut);
						
						//Used for encoding
						if (Shutter.caseEnableSequence.isSelected())
						{						
							inputFramerateMS = Float.parseFloat(Shutter.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
						}
						else			
							inputFramerateMS = (float) (1000 / FFPROBE.accurateFPS);	
						
						totalFrames = ((float) fileDuration / 1000 * FFPROBE.accurateFPS);
						
						playerInMark = Math.round((float) (waveformContainer.getSize().width * timeIn) / totalFrames);
						if ((int) Math.ceil(timeOut) < totalFrames)
						{
							playerOutMark = Math.round((float) (waveformContainer.getSize().width * timeOut - 1) / totalFrames);
						}
						else
							playerOutMark = waveformContainer.getWidth();
						
						slider.setMaximum((int) (totalFrames));							
						waveformContainer.repaint();	
						totalDuration();
						
						return true;
					}
				}
			}		
			
		} catch (Exception e) {}		
		
		return false;
	}
	
	public static void setFileList() {
		
		try {
			
			StringBuilder stb = new StringBuilder();
			
			if (fileList.length() > 0 && fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
			{
				for (String file : fileList.toString().split(System.lineSeparator()))
				{
					stb.append(file + System.lineSeparator());
				}

				fileList.setLength(0);
				
				boolean fileExists = false;							
				for (String file : stb.toString().split(System.lineSeparator()))
				{
					String s[] = file.split("\\|");
					if (s[0].equals(videoPath)) //Replace at the same line
					{						
						fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
						fileExists = true;
					}
					else if (file.equals("null") == false)
					{
						fileList.append(file + System.lineSeparator());
					}
				}
				
				if (fileExists == false)
				{
					fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
				}
			}		
			else if (fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
			{
				fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
			}
			
		} catch (Exception e) {}		
	}
	
	public static void resizeAll() {
								
		if (Shutter.frame.getWidth() > 332 && Shutter.doNotLoadImage == false)	
		{						
			//Clear the buffer
			if (bufferedFrames.size() > 0)
			{			
				bufferedFrames.clear();
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
			float ratio = FFPROBE.imageRatio;
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
			{
				ratio = (float) FFPROBE.imageHeight / FFPROBE.imageWidth;
			}
							
			//CaseForceDAR
			if (Shutter.caseForcerDAR.isSelected())
			{
				if (Shutter.comboDAR.getSelectedItem().toString().contains(":"))
				{
					String s[] = Shutter.comboDAR.getSelectedItem().toString().split(":");
					
					ratio = (float) Integer.parseInt(s[0]) / Integer.parseInt(s[1]);
				}
				else
					ratio = Float.parseFloat(Shutter.comboDAR.getSelectedItem().toString());
			}
			else if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false && Shutter.btnNoUpscale.isSelected() == false
			&& Shutter.comboResolution.getSelectedItem().toString().contains("x")
			&& Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false)
			{
				String s[] = Shutter.comboResolution.getSelectedItem().toString().split("x");		
				ratio = (float) Integer.parseInt(s[0]) / Integer.parseInt(s[1]);
			}
			else if (Shutter.caseEnableCrop.isSelected() && isPiping)
			{			
				ratio = (float) Integer.parseInt(Shutter.textCropWidth.getText()) / Integer.parseInt(Shutter.textCropHeight.getText());
			}
						
			if (ratio < 1.3f)
			{
				int maxHeight = (int) (Shutter.frame.getHeight() / 1.6f);
				
				if (fileDuration <= 40 && Shutter.caseEnableSequence.isSelected() == false || isPiping) //Image
				{
					maxHeight = Shutter.frame.getHeight() - (Shutter.grpChooseFiles.getY() + 8) - (Shutter.frame.getHeight() - (Shutter.grpProgression.getY() + Shutter.grpProgression.getHeight()));
				}
				
				if (fullscreenPlayer)
				{
					maxHeight = Shutter.frame.getHeight();
				}
				
				player.setSize((int) (maxHeight * ratio), maxHeight);
			}
			else
			{
				int maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2;
				
				if (Shutter.noSettings)
				{
					maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth();
				}
				
				if (fullscreenPlayer)
				{
					maxWidth = Shutter.frame.getWidth();
				}
				
				player.setSize(maxWidth, (int) (maxWidth / ratio));		
			}	
			
			if (fullscreenPlayer == false && Shutter.frame.getHeight() - player.getHeight() < 220 && (fileDuration > 40 || Shutter.caseEnableSequence.isSelected()))
			{
				int p = 220 - (Shutter.frame.getHeight() - player.getHeight());				
				player.setSize((int) (player.getWidth() - (float) p * ratio), player.getHeight() - p);
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
				player.setLocation(Shutter.frame.getWidth() / 2 - player.getWidth() / 2, Shutter.frame.getHeight() / 2 - player.getHeight() / 2);
			}
			else
			{
				player.setLocation((Shutter.frame.getWidth() - player.getSize().width) / 2, y);
			}

			//IMPORTANT video canvas must be a multiple of 4!
			player.setSize(player.getWidth() - (player.getWidth() % 4), player.getHeight() - (player.getHeight() % 4));
								
			//Define bufferSize
			maxBufferedFrames = (int) ((float) (Shutter.availableMemory / 3) / (player.getWidth() * player.getHeight() * 3));
			
			Shutter.playerRatio = (float) FFPROBE.imageWidth / player.getWidth();
			
			//Sliders
			if (Shutter.noSettings)
			{
				slider.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth(), 40);
			}
			else
				slider.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2, 40);
			
			waveformContainer.setSize(slider.getWidth() * waveformZoom, slider.getHeight());
			waveformContainer.setPreferredSize(new Dimension(waveformContainer.getWidth(), waveformContainer.getHeight()));
			waveformIcon.setSize(waveformContainer.getSize());
			waveformScrollPane.setBounds(slider.getX(), slider.getY(), slider.getWidth(), slider.getHeight() + waveformScrollPane.getHorizontalScrollBar().getHeight());
	
			//Waveforms
			if (fullscreenPlayer == false && isPiping == false && Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false && Shutter.liste.getSize() > 0 && addWaveform.isAlive() == false)
			{	
				addWaveform(false);				 					
			}
			
			if (playerCurrentFrame <= 1)
			{
				cursorWaveform.setBounds(0, 0, 1, waveformContainer.getSize().height);
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
					cursorWaveform.setBounds(Math.round((float) (waveformContainer.getSize().width * slider.getValue()) / slider.getMaximum()), 0, 1, waveformContainer.getSize().height);
					cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
				}
			}

			if (isPiping == false)
			{
				try { //Might fail loading
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseInF.getText());
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(caseOutF.getText());
						
					//NTSC framerate
					timeIn = Timecode.getNonDropFrameTC(timeIn);
					timeOut = Timecode.getNonDropFrameTC(timeOut);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * timeIn) / totalFrames);			
					if ((int) Math.ceil(timeOut) < totalFrames)
					{
						playerOutMark = Math.round((float) (waveformContainer.getSize().width * timeOut - 1) / totalFrames);
					}
					else
						playerOutMark = waveformContainer.getWidth();	
					
				} catch (Exception e) {}
				
				waveformContainer.repaint();
			}
			
			//lblTimecode & lblDuration
			lblPosition.setBounds(slider.getX(), slider.getY() - 22, slider.getWidth(), 16);
			lblDuration.setBounds(slider.getX(), lblPosition.getY(), slider.getWidth(), 16); 
									
			//grpSubtitles
			if (Shutter.caseAddSubtitles.isSelected())
			{						    		
				if (Integer.parseInt(Shutter.textSubsWidth.getText()) >= FFPROBE.imageWidth)
				{
					Shutter.subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
				}
				else
				{
					Shutter.subsCanvas.setSize((int) ((float) Integer.parseInt(Shutter.textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
				    		(int) (player.getHeight() + (float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
					
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
				loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
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
				int w = (int) Math.round((float)  (Integer.valueOf(Shutter.textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
				int h = (int) Math.round((float)  (Integer.valueOf(Shutter.textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
				
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
						
			btnPrevious.setBounds(player.getLocation().x + player.getSize().width / 2 - 21 - 4, slider.getY() + slider.getHeight() + 10, 22, 21);		
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
			showScale.setBounds(player.getX(), showFPS.getY(), player.getWidth() / 2, showScale.getPreferredSize().height);
			comboAudioTrack.setBounds(waveformContainer.getX() + 7, waveformContainer.getY() + (waveformContainer.getHeight() / 2) - 8, 40, 16);
						
			if (showScale.getY() < Shutter.topPanel.getHeight() || FFPROBE.audioOnly || videoPath == null || Shutter.frame.getSize().width <= 654 || fullscreenPlayer)
			{
				showScale.setVisible(false);
			}
			else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.liste.getSize() > 0 && isPiping == false && Settings.btnDisableVideoPlayer.isSelected() == false)
			{
				showScale.setVisible(true);
			}
			
			showScale.repaint();
			showFPS.repaint();
			
			//Group boxes
			caseInH.setBounds(slider.getX() - 2, btnPrevious.getY(), 21, 21);
			caseInM.setBounds(caseInH.getX() + caseInH.getWidth(), caseInH.getY(), 21, 21);
			caseInS.setBounds(caseInM.getX() + caseInM.getWidth(), caseInH.getY(), 21, 21);
			caseInF.setBounds(caseInS.getX() + caseInS.getWidth(), caseInH.getY(), 21, 21);		
			caseOutH.setBounds(slider.getX() + slider.getWidth() - (21) * 4, caseInH.getY(), 21, 21);
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
			
			btnPreview.setBounds(slider.getX() + slider.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
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
				
				caseApplyCutToAll.setLocation(lblPosition.getX() + slider.getWidth() / 2 - caseApplyCutToAll.getWidth(), lblPosition.getY() - 3);
			}
			else
			{
				caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, caseInternalTc.getY(), caseVuMeter.getPreferredSize().width, 23);
				caseShowWaveform.setBounds(caseVuMeter.getX() - caseShowWaveform.getPreferredSize().width - 5, caseVuMeter.getY(), caseShowWaveform.getPreferredSize().width, 23);

				sliderVolume.setBounds(lblVolume.getX() + lblVolume.getWidth() + 1, sliderSpeed.getY(), sliderSpeed.getWidth(), 22);
				
				caseApplyCutToAll.setLocation(lblPosition.getX() + slider.getWidth() / 2 - caseApplyCutToAll.getWidth() / 2, lblPosition.getY() - 3);
			}
			
			if (Shutter.windowDrag == false && videoPath != null && isPiping == false)
			{					
				if (preview != null && fileDuration > 40)
					preview = null;
				
				if (Shutter.inputDeviceIsRunning)
				{
					if (setEQ.isEmpty() == false)
					{
						loadImage(false);
						waveformIcon.setVisible(false);
					}
					else
						playerFreeze();
				}
				else if (fileDuration <= 40)
				{
					loadImage(false);
					waveformIcon.setVisible(false);
				}
				else if (btnPlay.isEnabled())
				{			
					playerFreeze();
				}
			}	
			
			if (Shutter.liste.getSize() == 0 || videoPath == null)
			{
				VideoPlayer.setPlayerButtons(false);
			}
			
		}
		
		if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected() || Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
		{
			refreshTimecodeAndText();
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
	
	public static void getTimePoint(float time) {	
		
		if (caseInternalTc.isSelected())
			time += offset;
		
		if (time - offset >= totalFrames)
		{
			sliderChange = true;
			slider.setValue(slider.getMaximum());
			sliderChange = false;    		
		}

		//NTSC framerate
		time = Timecode.setNonDropFrameTC(time);
		
    	if (playerVideo != null && time - offset < totalFrames)
    	{    					    			 		
			String h = Shutter.formatter.format(Math.floor(time / FFPROBE.accurateFPS / 3600));
			String m = Shutter.formatter.format(Math.floor(time / FFPROBE.accurateFPS / 60) % 60);
			String s = Shutter.formatter.format(Math.floor(time / FFPROBE.accurateFPS) % 60);   
			String f = Shutter.formatter.format(Math.floor(time % FFPROBE.accurateFPS));
					    	
			String dropFrame = ":";
	        if (Timecode.isDropFrame())
        	{
	        	dropFrame = ";";
        	}
	        
			lblPosition.setText(h + ":" + m + ":" + s + dropFrame + f + " | " + Math.round(playerCurrentFrame));	    
			
			if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && mouseIsPressed)
			{
				updateGrpIn(time - offset);
			}			
			
			if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && mouseIsPressed)
			{
				updateGrpOut(time - offset + 1);
			}
			
    		if (sliderChange == false && Shutter.windowDrag == false)
    		{   		    			    			
    			slider.setValue((int) playerCurrentFrame);
    			
    			int newValue = Math.round((float) (waveformContainer.getSize().width * slider.getValue()) / slider.getMaximum());
    			
    			if (cursorWaveform != null)
    			{
    				if (playerCurrentFrame <= 1)
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
						else if (newValue != cursorWaveform.getX()) //Only refresh when the value is different
						{					
							cursorWaveform.setLocation(newValue, 0);
							cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						}
    				}
    				
    				if (cursorWaveform.getX() > waveformScrollPane.getWidth() + waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX() - slider.getWidth() + 1);
					}
					else if (cursorWaveform.getX() < waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX());
					}				
    			}
    		}    
    	}
    	
    	if (time - offset >= totalFrames - 2)
    	{
    		btnPlay.setIcon(new FlatSVGIcon("contents/play.svg", 15, 15));
    		btnPlay.setName("play");
    	}
    		
    }

	public static void totalDuration() {	
		
		try {
						
			int inH = Integer.parseInt(caseInH.getText());
			int inM = Integer.parseInt(caseInM.getText());
			int inS = Integer.parseInt(caseInS.getText());
			int inF = Integer.parseInt(caseInF.getText());
			
			int outH = Integer.parseInt(caseOutH.getText());
			int outM = Integer.parseInt(caseOutM.getText());
			int outS = Integer.parseInt(caseOutS.getText());
			int outF = Integer.parseInt(caseOutF.getText());
			
			float totalIn =  (inH * 3600 + inM * 60 + inS) * FFPROBE.accurateFPS + inF;
			float totalOut = (outH * 3600 + outM * 60 + outS) * FFPROBE.accurateFPS + outF;
			
			float total = (float) Math.ceil(totalOut - totalIn);
							
			if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
				total = totalFrames - total;
		
			durationH = (int) Math.floor(total / FFPROBE.accurateFPS / 3600);
			durationM = (int) Math.floor(total / FFPROBE.accurateFPS / 60) % 60;
			durationS = (int) Math.floor(total / FFPROBE.accurateFPS) % 60;
			durationF = (int) Math.floor(total % FFPROBE.accurateFPS);
									
			lblDuration.setText(Shutter.language.getProperty("lblBitrateTimecode") + " " + Shutter.formatter.format(durationH) + ":" + Shutter.formatter.format(durationM) + ":" + Shutter.formatter.format(durationS) + ":" + Shutter.formatter.format(durationF) + " | " + (int) Timecode.getNonDropFrameTC(total) + " " + Shutter.language.getProperty("lblTotalFrames"));
			
			if (total <= 0)
			{
				lblDuration.setVisible(false);  
			}
			else if (waveformScrollPane.isVisible() && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.caseEnableSequence.isSelected() == false)
			{
	    		lblDuration.setVisible(true);   
	    		
	    		//Durée H264
	    		switch (Shutter.comboFonctions.getSelectedItem().toString())
	    		{
					case "H.264":
					case "H.265":
					case "H.266":
					case "WMV":
					case "MPEG-1":
					case "MPEG-2":
					case "VP8":
					case "VP9":
					case "AV1":
					case "Theora":
					case "MJPEG":
					case "Xvid":
					case "Blu-ray":

			    	    FFPROBE.setFilesize();
			    	    
		    	     break;
	    		}
			}
		
		} catch (Exception e){}
	}
	
}
