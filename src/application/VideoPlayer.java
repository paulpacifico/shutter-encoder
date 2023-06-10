/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.DCRAW;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.MEDIAINFO;
import library.XPDF;
import settings.Colorimetry;
import settings.Corrections;
import settings.FunctionUtils;
import settings.ImageSequence;
import settings.Timecode;
import settings.Transitions;

public class VideoPlayer {
	
    //Player
	public static JPanel player; 
    public static Process playerVideo;
    public static Process playerAudio;
    private static InputStream audio;							
    private static AudioInputStream audioInputStream;
    private static SourceDataLine line;
    private static FloatControl gainControl;
    public static Thread playerThread;	
    public static Thread setTime;
	public static float playerCurrentFrame = 0;
    private static long fpsTime = 0;
    private static int fps = 0;
    private static int displayCurrentFPS = 0;
    private static JLabel showFPS;
    private static JLabel showScale;
    public static int playerInMark = 0;
    public static int playerOutMark = 0;
    public static Image frameVideo;
	private static double screenRefreshRate = 16.7; //Vsync in ms
	private static long lastEvTime = 0;
    public static boolean playerLoop = false;
    public static boolean frameIsComplete = false;
    public static boolean playerPlayVideo = true;
	public static boolean sliderChange = false;
	private static JLabel lblVolume;
	public static JSlider sliderVolume;
	public static JLabel lblPosition;
	public static JLabel lblDuration;
	private static JLabel lblMode;
	public static JComboBox<Object> comboMode = new JComboBox<Object>(new String [] {Shutter.language.getProperty("cutUpper"), Shutter.language.getProperty("removeMode"), Shutter.language.getProperty("splitMode")});
	public static JLabel lblSpeed;
	public static JSlider sliderSpeed;
	private static boolean showInfoMessage = true;
	public static boolean playTransition = false;
    private static boolean closeAudioStream = false;
		
	//Buttons & Checkboxes
	public static JLabel btnPreview;
	public static JTextField splitValue;
	private static JLabel lblSplitSec;
	public static JButton btnPrevious;
	public static JButton btnNext;
	public static JButton btnStop;
	public static JButton btnPlay;
	public static JButton btnMarkIn;
	public static JButton btnMarkOut;
	public static JButton btnGoToIn;
	public static JButton btnGoToOut;
	public static JSlider slider;
	public static JCheckBox caseGPU;
	public static JCheckBox caseVuMeter;
	public static JCheckBox casePlaySound;
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
	static boolean frameControl = false;
	static boolean seekOnKeyFrames = false;
	
	//Waveform
	public static boolean addWaveformIsRunning = false;
	public static File waveform = new File(Shutter.dirTemp + "waveform.png");
	public static JLabel waveformIcon;
	public static JLabel waveformContainer;
	public static JPanel cursorWaveform;
	private static boolean mouseIsPressed = false;
	
	public static File preview = new File(Shutter.dirTemp + "preview.bmp");
	private static Thread runProcess = new Thread();
		
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
		lblPosition.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblPosition.setForeground(Utils.themeColor);   		
		Shutter.frame.getContentPane().add(lblPosition);
				
		lblDuration = new JLabel();
		lblDuration.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDuration.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
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
							Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.imageRatio) ) ) );
							Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.imageRatio) ) ) );  
							Shutter.logoLocX = Shutter.logo.getLocation().x;
							Shutter.logoLocY = Shutter.logo.getLocation().y;
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
										
							playerSetTime(slider.getValue());
						}	
					}
				}
				
				
				if (e.getKeyCode() == KeyEvent.VK_K || e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					e.consume();
					btnPlay.doClick();
				}
				
				if (e.getKeyCode() == KeyEvent.VK_J)
				{
					playerSetTime((float) (VideoPlayer.playerCurrentFrame - 10));
  				}
					
				if (e.getKeyCode() == KeyEvent.VK_L)
				{
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
	            		btnPrevious.doClick();	
					
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						btnNext.doClick();
					
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
		
		for (Component component : Shutter.frame.getContentPane().getComponents())
		{
			component.addKeyListener(keyListener);
		}
						
    	Shutter.frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e2)
		    {
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, Shutter.frame.getWidth(), Shutter.frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, Shutter.frame.getHeight()-15, Shutter.frame.getWidth(), 15));
		        shape1.add(shape2);
		    	Shutter.frame.setShape(shape1);
		    }
 		});

    	Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	public static void playerProcess(float inputTime) {

		try {			
						
			String PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c", '"' + PathToFFMPEG + '"' + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
				
				//AUDIO STREAM
				if (casePlaySound.isSelected() || mouseIsPressed == false || (sliderChange == false && frameControl == false))						       
				{
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + PathToFFMPEG + '"' + setAudioCommand(inputTime));	
					playerAudio = pba.start();
				}
			}	
			else
			{
				PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
				
				//AUDIO STREAM
				if (casePlaySound.isSelected() || mouseIsPressed == false || (sliderChange == false && frameControl == false))						       
				{
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setAudioCommand(inputTime));	
					playerAudio = pba.start();
				}
			}			
				
			InputStream video = playerVideo.getInputStream();				
			BufferedInputStream videoInputStream = new BufferedInputStream(video);

			if (casePlaySound.isSelected() || mouseIsPressed == false || (sliderChange == false && frameControl == false))						       
			{
				audio = playerAudio.getInputStream();							
				audioInputStream = AudioSystem.getAudioInputStream(audio);		    
			    AudioFormat audioFormat = audioInputStream.getFormat();
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		        line = (SourceDataLine) AudioSystem.getLine(info);
		        
	            line.open(audioFormat);
	            gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
	            line.start();	
			}
			
			playerThread = new Thread(new Runnable() {

				@Override
				public void run() {

					byte bytes[] = new byte[(int) Math.ceil(FFPROBE.audioSampleRate*4/FFPROBE.currentFPS)];
		            int bytesRead = 0;
		            		            
					do {	
						
						long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);
						
						if (playerLoop)
						{							
							try {	
								
								//Audio volume	
								if (casePlaySound.isSelected() || (sliderChange == false && frameControl == false))						       
								{
									closeAudioStream = true;
									double gain = (double) sliderVolume.getValue() / 100;   
							        float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
							        gainControl.setValue(dB);
	
									///Read 1 audio frame
									bytesRead = audioInputStream.read(bytes, 0, bytes.length);
					        		line.write(bytes, 0, bytesRead);
								}
								else
									closeAudioStream = false;
												 				        		
				        		//Read 1 video frame				        		
								frameVideo = ImageIO.read(videoInputStream);
								playerRepaint();
						    	fps ++;	

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
								
															
							} catch (Exception e) {}
							finally {
								
								if (frameControl)
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
											Thread.sleep((int) (delay / 1000000 / 2));
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
							if (line!= null && closeAudioStream)				       
							{
								line.flush();	
							}
														
							//IMPORTANT reduce CPU usage
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						}							
						
					} while (playerVideo.isAlive());
													
					try {
						video.close();
					} catch (IOException e) {}		
					try {
						videoInputStream.close();
					} catch (IOException e) {}
					
					if (audio != null && closeAudioStream)	       
					{
						try {
							audio.close();
						} catch (IOException e) {}
						try {
							audioInputStream.close();
						} catch (IOException e) {}
						line.close();	
					}
				}
				
			});
			playerThread.setPriority(Thread.MAX_PRIORITY);
			playerThread.start();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
			
	public static void playerPlay() {
		
		if (playerVideo == null || playerVideo.isAlive() == false)		
		{		
			playerProcess(playerCurrentFrame);							
		}		
	}
	
	public static void playerStop() {
		
		closeAudioStream = true;
		playerLoop = false;
				
		if (playerVideo != null)
		{
			playerVideo.destroy();
			playerThread.interrupt();
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
		
		if (playerVideo != null && playerVideo.isAlive() && btnPlay.getText().equals("⏸"))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerSetTime(float time) {
			
		if ((setTime == null || setTime.isAlive() == false && frameVideo != null) && playerThread != null)
		{			
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {					

					int t = (int) Math.ceil(time);
					
					if (t < 0)
						t = 0;
					
					writeCurrentSubs(t);
					
					playerPlayVideo = false;
					
					boolean playback;
					if (playerIsPlaying())
					{
						playback = true;
					}
					else
						playback = false;					
										
					if (frameVideo != null)
					{
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

							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
							
							if (System.currentTimeMillis() - time > 1000)
								frameIsComplete = true;
														
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
					}
					
					frameControl = false;
					playerPlayVideo = true;		
				}
				
			});
			setTime.start();			
		}	
	}
		
	public static void playerFreeze() {
				
		frameControl = true;
		playerPlayVideo = false;

		if (playerVideo == null || playerVideo.isAlive() == false)		
		{				
			frameControl = true; //IMPORTANT to stop the player loop
			frameIsComplete = false;		
			playerLoop = true;
			playerProcess(playerCurrentFrame);
			
			long time = System.currentTimeMillis();
			
			do {
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}

				if (System.currentTimeMillis() - time > 1000)
					frameIsComplete = true;
											
			} while (frameIsComplete == false);
			
			if (playerCurrentFrame > 0)
				playerCurrentFrame -= 1;
			
			getTimePoint(playerCurrentFrame);
		}
		
		frameControl = false;	
		playerPlayVideo = true;
	}
	
    public static void setMedia() {
    		    	
    	//Updating video file
		if (Shutter.liste.getSize() != 0)
		{
			if (Shutter.fileList.getSelectedIndices().length == 0)
      		{
				Shutter.fileList.setSelectedIndex(0);
      		}
			
			videoPath = Shutter.fileList.getSelectedValue();
							
			//set timecode & Shutter.fileName locations
			refreshTimecodeAndText();
			
			if (preview.exists())
				preview.delete();
			
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
			
			//On Reset si on change de fichier
			if (Shutter.fileList.getSelectedValue().equals(new File(videoPath).getName()) == false
			&& cursorWaveform.isVisible()
			&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
			{
				if (waveform.exists())
					waveform.delete();
									
				if (FFMPEG.isRunning)
					FFMPEG.process.destroy();
			}
											
			if (videoPath != null || Shutter.fileList.getSelectedValue().equals(new File(videoPath).getName()) == false)
			{
				String extension = videoPath.substring(videoPath.lastIndexOf("."));	
				
				try {
					
					if (extension.toLowerCase().equals(".pdf"))
					{
						 XPDF.info(videoPath);	
						 do
						 {
							 Thread.sleep(100);						 
						 }
						 while (XPDF.isRunning);

						 XPDF.toFFPROBE(videoPath);	
						 do
						 {
							 Thread.sleep(100);						 
						 }
						 while (XPDF.isRunning);								 
					}				
					else
					{						
						FFPROBE.Data(videoPath);
						do 
						{								
							Thread.sleep(100);								
						} 
						while (FFPROBE.isRunning);	
					
						FFPROBE.FrameData(videoPath);	
						do 
						{								
							Thread.sleep(100);								
						} 
						while (FFPROBE.isRunning);
						
						FFMPEG.checkGPUCapabilities(videoPath, true);
						
						if (FFPROBE.interlaced == null)
						{
							MEDIAINFO.run(videoPath, false);
							
							do
							{
								Thread.sleep(100);
							}
							while (MEDIAINFO.isRunning);
							
							if (FFPROBE.interlaced == null)
							{
								FFPROBE.interlaced = "0";
								FFPROBE.fieldOrder = "0";
							}
						}

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
						
						if (extension.toLowerCase().equals(".pdf"))
						{
							 XPDF.toFFPROBE(videoPath);	
							 do
							 {
								 Thread.sleep(100);
							 }
							 while (XPDF.isRunning);								 
						}				
						else if (isRaw || Shutter.comboFonctions.getSelectedItem().toString().equals("JPEG")
									   || Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionPicture")))
						{
							 EXIFTOOL.run(videoPath);	
							 do
							 {
								 Thread.sleep(100);						 
							 }
							 while (EXIFTOOL.isRunning);
						}
					}
					
				} catch (InterruptedException e) {}

				setPlayerButtons(true);	
				
				seekOnKeyFrames = false;
				
				if (FFPROBE.audioOnly == false
				&& (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut"))
				|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))))
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
				
				//Image sequence
				if (Shutter.caseEnableSequence.isSelected())
				{	
					//Create the concat text file
					FunctionUtils.setConcat(new File("concat.txt"), Shutter.dirTemp);						
					inputFramerateMS = Float.parseFloat(Shutter.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
				}
				else					
					inputFramerateMS = (float) (1000 / FFPROBE.currentFPS);		
				
				totalFrames = (float) Math.round(FFPROBE.totalLength / inputFramerateMS);
				playerCurrentFrame = 0;

				caseInternalTc.setEnabled(true);	
				Shutter.caseShowTimecode.setEnabled(true);
				
				Shutter.textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
				
				if (FFPROBE.videoCodec != null && FFPROBE.totalLength > 40)
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
					
					showScale.setText(FFPROBE.imageResolution + " " + vcodec);
				}
				else
					showScale.setText(FFPROBE.imageResolution);
				
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
				
				if (FFPROBE.totalLength > 40)
				{
					lblPosition.setVisible(true);
					lblDuration.setVisible(true);
				}
				
				btnStop.doClick();
				
				playerInMark = 0;
				playerOutMark = waveformContainer.getWidth() - 2;
				
				waveformContainer.repaint();
				
				//Reset boxes
				updateGrpIn(0);
				updateGrpOut(FFPROBE.totalLength);
				slider.setMaximum((int) (totalFrames));
				
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					SubtitlesTimeline.timelineScrollBar.setMaximum(slider.getMaximum());
			}		
		}
		else
		{				
			btnStop.doClick();
			
			videoPath = null;
			showScale.setVisible(false);
			playerStop();
			slider.setValue(0);

			btnPlay.setText("▶");		
			
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
		}
		
		if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("processEnded")))
		{
			Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
		}
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
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
		else if (waveformContainer.isVisible())
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
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			File video = new File(videoPath);
			String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
			
			SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
			
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    		if (Shutter.caseAddSubtitles.isSelected())
    		{
	    		player.remove(Shutter.subsCanvas);
				Shutter.caseAddSubtitles.setSelected(false);	    	
    		}
    		
    		if (SubtitlesTimeline.frame == null)    		
    		{
    			new SubtitlesTimeline(dim.width/2-500,Shutter.frame.getLocation().y + Shutter.frame.getHeight() + 7);
    		}
    		else
    		{        		
    			SubtitlesTimeline.frame.setVisible(true);
    			SubtitlesTimeline.subtitlesNumber();
    		}    	
		}
		
		resizeAll();
		
		if (Shutter.fileList.hasFocus() == false)
		{
			waveformContainer.requestFocus();
		}
	}
     
    public static void setPlayerButtons(boolean enable) {
    	
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			waveformContainer.setVisible(true);
			waveformIcon.setVisible(true);
			caseInH.setVisible(false);
			caseInM.setVisible(false);
			caseInS.setVisible(false);
			caseInF.setVisible(false);
			caseOutH.setVisible(false);
			caseOutM.setVisible(false);
			caseOutS.setVisible(false);
			caseOutF.setVisible(false);
			lblDuration.setVisible(false);
			lblPosition.setVisible(true);
			lblVolume.setVisible(true);
			sliderVolume.setVisible(true);
			lblSpeed.setVisible(false);
			sliderSpeed.setVisible(false);
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
			caseInternalTc.setVisible(false);
			caseGPU.setVisible(true);
			caseVuMeter.setVisible(true);
			casePlaySound.setVisible(true);
			showScale.setVisible(false);
		}
		else if (FFPROBE.totalLength <= 40 || enable == false) //Image or disableAll()
		{
			waveformContainer.setVisible(false);
			waveformIcon.setVisible(false);
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
			
			float ratio = FFPROBE.imageRatio;
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
			{
				ratio = (float) FFPROBE.imageHeight / FFPROBE.imageWidth;
			}

			if (ratio > 0.8f)
				showScale.setVisible(true);
			else
				showScale.setVisible(false);
			
			showFPS.setVisible(false);
			
			if (Shutter.caseEnableSequence.isSelected())
				btnPlay.setVisible(true);
			else
				btnPlay.setVisible(false);
			
			btnPrevious.setVisible(false);
			btnNext.setVisible(false);
			
			if (Shutter.caseEnableSequence.isSelected())
				btnStop.setVisible(true);
			else
				btnStop.setVisible(false);
			
			btnMarkOut.setVisible(false);
			btnGoToOut.setVisible(false);
			caseInternalTc.setVisible(false);
			caseGPU.setVisible(false);
			caseVuMeter.setVisible(false);
			casePlaySound.setVisible(false);
			
			if (Shutter.caseEnableSequence.isSelected())
			{	
				Shutter.caseAddSubtitles.setEnabled(true);
			}
		}
		else
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
										
			waveformContainer.setVisible(true);
			waveformIcon.setVisible(true);
			caseInH.setVisible(true);
			caseInM.setVisible(true);
			caseInS.setVisible(true);
			caseInF.setVisible(true);
			caseOutH.setVisible(true);
			caseOutM.setVisible(true);
			caseOutS.setVisible(true);
			caseOutF.setVisible(true);
			lblDuration.setVisible(true);
			lblPosition.setVisible(true);
			lblVolume.setVisible(true);
			sliderVolume.setVisible(true);
			lblSpeed.setVisible(true);
			sliderSpeed.setVisible(true);
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
			caseGPU.setVisible(true);
			caseVuMeter.setVisible(true);												
			casePlaySound.setVisible(true);
			
			if (FFPROBE.audioOnly || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
			{
				caseInternalTc.setVisible(false);
				showScale.setVisible(false);
				showFPS.setVisible(false);
			}
			else
			{
				caseInternalTc.setVisible(true);
				showScale.setVisible(true);
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
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600 * FFPROBE.currentFPS
								+ Integer.valueOf(FFPROBE.timecode2) * 60 * FFPROBE.currentFPS
								+ Integer.valueOf(FFPROBE.timecode3) * FFPROBE.currentFPS
								+ Integer.valueOf(FFPROBE.timecode4);				
					}
				}
			}
		}
    }
    
	public static String setVideoCommand(float inputTime, int width, int height, boolean isPlaying) throws InterruptedException {

		//Deinterlacer		
		String yadif = "";
		if (FFPROBE.interlaced.equals("1"))
			yadif = "yadif=0:" + FFPROBE.fieldOrder + ":0";
		
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
			FFPROBE.currentFPS = 25.0f;
			
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
			
			return " -v quiet -ss " + (long) (inputTime * inputFramerateMS) + "ms -i " + '"' + videoPath + '"' + " -f lavfi -i " + '"' + "color=c=black:r=25:s=" + width + "x" + height + '"' + filter + " -c:v bmp -an -f image2pipe pipe:-";
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
			
			if (caseGPU.isSelected())
			{
				if (FFMPEG.isGPUCompatible)
				{
					//Auto GPU Shutter.selection
					if (FFMPEG.cudaAvailable)
					{
						gpuDecoding = " -hwaccel cuda -hwaccel_output_format cuda";
					}
					else if (FFMPEG.qsvAvailable)
					{
						gpuDecoding = " -hwaccel qsv -hwaccel_output_format qsv";
					}					
					else
						gpuDecoding = " -hwaccel auto";
				}
				else if (System.getProperty("os.name").contains("Mac"))
				{
					gpuDecoding = " -hwaccel auto";
				}
			}
			
			String cmd = gpuDecoding + " -v quiet -ss " + (long) (inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + setFilter(yadif, speed, false) + " -c:v bmp -an -f image2pipe pipe:-";
						
			Console.consoleFFMPEG.append(System.lineSeparator() + cmd + System.lineSeparator());

			return cmd;			
		}
	}
	
	public static String setAudioCommand(float inputTime) {
				
		String speed = "";					
		if (sliderSpeed.getValue() != 2)
		{
			if (sliderSpeed.getValue() != 0)
				speed = " -af atempo=" + (float) sliderSpeed.getValue() / 2;
			else
				speed = " -af atempo=0.5,atempo=0.5";
		}
		
		String audioFade = "";
		if (Shutter.caseAudioFadeIn.isSelected() || Shutter.caseAudioFadeOut.isSelected())
		{
			if (speed != "")
			{
				audioFade += ",";
			}
			else
				audioFade += " -af ";
			
			audioFade += Transitions.setAudioFadeIn(true);
			
			if (Transitions.setAudioFadeIn(true) != "" && Transitions.setAudioFadeOut(true) != "") audioFade += ",";
			
			audioFade += Transitions.setAudioFadeOut(true);
		
		}
		
		if (playTransition)
		{
			playTransition = false;
		}
		
		if (FFPROBE.hasAudio == false)
		{
			return " -v quiet -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"' + speed + audioFade +  " -vn -c:a pcm_s16le -ar 48k -ac 2 -f wav pipe:-";				
		}
		else
		{
			return " -v quiet -ss " + (long) (inputTime * inputFramerateMS) + "ms -i " + '"' + videoPath + '"' + speed + audioFade + " -vn -c:a pcm_s16le -ac 2 -f wav pipe:-";
		}		
		
	}
    
	public static void addWaveform(boolean newWaveform) {
					
		addWaveformIsRunning = true;
		if (newWaveform || waveform.exists() == false)
		{
			waveformIcon.setVisible(false);
			
			if (newWaveform)
			{
				waveform.delete();
			}
		}
			
		Thread addWaveform = new Thread(new Runnable()
		{
			@Override
			public void run() {
							
				if (FFMPEG.isRunning || playerVideo == null)
				{						
					do  {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFMPEG.isRunning && playerVideo == null);
				}
				
				if (FFMPEG.isRunning == false || Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")))
				{								
					//Si fichier audio seulement ou Sous titrage								
					if (FFPROBE.hasAudio)
					{
						if (newWaveform || waveform.exists() == false)
						{
							long size = 1920;
							
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
							
							FFMPEG.run(start + " -i " + '"' + videoPath + '"'
							+ " -filter:a aresample=8000 -filter_complex " + '"' + "[0:a]" + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green" + '"' 
							+ " -pix_fmt rgba -vn -frames:v 1 -y " + '"' + waveform + '"');  									
		
							Shutter.enableAll();							
							
							if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
								Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
							else
								Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
							
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}										
							} while (waveform.exists() == false && FFMPEG.isRunning);								
						}
						
						//add Waveform		
						try {
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles"))) //Ne charge plus l'image si la fenêtre est fermée entre temps
							{
								Image imageBMP = ImageIO.read(waveform);
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance((int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));						
								
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
								SubtitlesTimeline.waveform.repaint();
							}
							else
							{	    
								Image imageBMP = ImageIO.read(waveform);						
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(waveformContainer.getWidth(), waveformContainer.getHeight(), Image.SCALE_AREA_AVERAGING));
								 						
								waveformIcon.setIcon(resizedWaveform);
								waveformIcon.repaint();
								waveformIcon.setVisible(true);
							} 						
						}
						catch (Exception e) {						
						}
						finally
						{
							addWaveformIsRunning = false;
						}
					}
				}			
			}
			
		});
		addWaveform.start();
	}
	
	private void buttons() {		 
    	
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font("", Font.PLAIN, 12));
		Shutter.frame.getContentPane().add(btnPrevious);
				
		btnPrevious.addActionListener(new ActionListener(){
			
			int i = 0;
			
			@Override
			public void actionPerformed(ActionEvent e) {

				i ++;
				
				if (frameVideo != null && i <= 1)
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
							
							if (playerVideo != null && frameVideo != null)
							{										
								if (playerLoop)
								{
									btnPlay.setText("▶");
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
								else
									playerSetTime(playerCurrentFrame - 1);					
															
								long time = System.currentTimeMillis();
								
								do {

									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (System.currentTimeMillis() - time > 1000)
										frameIsComplete = true;
																
								} while (frameIsComplete == false);
								
							}	
							
							i = 0;
						}
						
					});
					t.start();
				}
			}	
			
		});
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font("", Font.PLAIN, 12));		
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
				
				if (preview.exists() || Shutter.caseAddSubtitles.isSelected())
				{
					preview.delete();												
					playerSetTime(playerCurrentFrame + 1);
				}

				frameControl = true;
				
				if (playerVideo != null)
				{
					if (playerLoop)
					{
						btnPlay.setText("▶");
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
						playerLoop = true;
					}
				}	
			}
			
		});
		
		btnPlay = new JButton("▶");		
		btnPlay.setFont(new Font("", Font.PLAIN, 12));
		btnPlay.setMargin(new Insets(0,0,0,0));			
		Shutter.frame.getContentPane().add(btnPlay);
		
		btnPlay.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				if (btnPlay.getText().equals("⏸") && System.getProperty("os.name").contains("Windows") == false)
				{
					btnPlay.setFont(new Font("", Font.PLAIN, 16));					
				}
				else
					btnPlay.setFont(new Font("", Font.PLAIN, 12));				
			}
			
		});
		
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//Allows to wait for the last frame to load					
				long time = System.currentTimeMillis();

				if (playerIsPlaying() == false)
				{
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
						
						if (System.currentTimeMillis() - time > 1000)
							break;
						
					} while (playerLoop);
				}
				
				if (btnPlay.getText().equals("⏸"))
				{
					btnPlay.setFont(new Font("Arial", Font.PLAIN, 12));
					btnPlay.setText("▶");	
					playerLoop = false;
					showFPS.setVisible(false);
					
					if (sliderSpeed.getValue() != 2)
					{						
						playerSetTime(slider.getValue());	
					}							
				}
				else if (btnPlay.getText().equals("▶"))
				{									
					if (preview.exists() || Shutter.caseAddSubtitles.isSelected())
					{
						preview.delete();												
						playerSetTime(playerCurrentFrame);
					}
					
					frameControl = false;
					btnPlay.setText("⏸");
					playerLoop = true;
		            fpsTime = System.nanoTime();
		            displayCurrentFPS = 0;
				}
								
			}
			
		});
		
		btnStop = new JButton("⏹");
		if (System.getProperty("os.name").contains("Windows"))
		{
			btnStop.setFont(new Font("", Font.PLAIN, 12));
		}
		else
			btnStop.setFont(new Font("", Font.PLAIN, 16));
		
		btnStop.setMargin(new Insets(0,0,0,0));	
		Shutter.frame.getContentPane().add(btnStop);		
		
		btnStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (playerVideo != null)
				{				
					playerCurrentFrame = 0;
					if (playerVideo != null)
					{
						playerStop();						
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {};
						} while (playerVideo.isAlive());
												
						slider.setValue(0);
					}
					
					resizeAll();

					btnPlay.setText("▶");
					playerLoop = false;
										
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						SubtitlesTimeline.actualSubOut = 0;	

					playerCurrentFrame = 0;				
				}
				else if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
				{
					resizeAll();
				}
			}			
			
		});

		btnMarkIn = new JButton("⬥");
		btnMarkIn.setFont(new Font("", Font.PLAIN, 12));			
		Shutter.frame.getContentPane().add(btnMarkIn);
		
		btnMarkIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
						
				playerInMark = cursorWaveform.getX();
				waveformContainer.repaint();							
				updateGrpIn(playerCurrentFrame);
				Shutter.timecode.repaint();
			}
			
		});
		
		btnGoToIn = new JButton("⬥<");
		btnGoToIn.setMargin(new Insets(0,0,0,0));
		btnGoToIn.setFont(new Font("", Font.PLAIN, 12));			
		Shutter.frame.getContentPane().add(btnGoToIn);
		
		btnGoToIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {		

				playTransition = true;
				
				playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
				
				//NTSC framerate
				playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
				
				playerSetTime(playerCurrentFrame);
			}
			
		});
		
		btnMarkOut = new JButton("⬥");
		btnMarkOut.setFont(new Font("", Font.PLAIN, 12));		
		Shutter.frame.getContentPane().add(btnMarkOut);
		
		btnMarkOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				playerOutMark = cursorWaveform.getX();
				waveformContainer.repaint();
				updateGrpOut(playerCurrentFrame + 1);
			}
			
		});
				
		btnGoToOut = new JButton(">⬥");
		btnGoToOut.setMargin(new Insets(0,0,0,0));
		btnGoToOut.setFont(new Font("", Font.PLAIN, 12));				
		Shutter.frame.getContentPane().add(btnGoToOut);
		
		btnGoToOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());

				//NTSC framerate
				playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
				
				playerSetTime(playerCurrentFrame - 1);
			}
			
		});
   
		showFPS = new JLabel("25 fps");
		showFPS.setVisible(false);
		showFPS.setFont(new Font(Shutter.freeSansFont, Font.BOLD, 12));
		showFPS.setHorizontalAlignment(SwingConstants.RIGHT);
		Shutter.frame.getContentPane().add(showFPS);
		
		showScale = new JLabel("1920x1080");
		showScale.setVisible(false);
		showScale.setEnabled(false);
		showScale.setFont(new Font(Shutter.freeSansFont, Font.BOLD, 12));
		showScale.setHorizontalAlignment(SwingConstants.LEFT);
		Shutter.frame.getContentPane().add(showScale);
		
    }
	
    @SuppressWarnings("serial")
	private void player() {		
    	
		player = new JPanel() {
			
            @Override
            protected void paintComponent(Graphics g) {
            	
                super.paintComponent(g);
                
                Graphics2D g2 = (Graphics2D)g;
                
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                g2.setColor(Color.BLACK);

                if (Shutter.liste.getSize() == 0)
                {
                	g2.fillRect(0, 0, player.getWidth(), player.getHeight()); 
                }
                else
                {
                	g2.drawImage(frameVideo, 0, 0, player.getWidth(), player.getHeight(), this); 
                }
                
                //Get the current fps
                if (FFPROBE.audioOnly == false && FFPROBE.totalLength > 40)
                {
	                if (System.nanoTime() - fpsTime >= 1000000000)
					{          	
	                	displayCurrentFPS = fps;
						fpsTime = System.nanoTime();
						fps = 0;
					}	              
	                	                
	                //Display current fps
		            if (displayCurrentFPS > 0 && playerLoop && sliderSpeed.getValue() == 2)
		            {
		            	showFPS.setVisible(true);		            	
		            	if ((float) displayCurrentFPS >= FFPROBE.currentFPS)
		            	{
		            		showFPS.setForeground(Color.GREEN);
		            		
		            		String fps[] = String.valueOf(FFPROBE.currentFPS).split("\\.");
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
                                
                if (Shutter.stabilisation != "")
                {
                	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                	g2.setColor(Color.WHITE);
                	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(player.getHeight()/16))); 
                	FontMetrics metrics = g.getFontMetrics(g2.getFont());
                     
                    int x = (player.getWidth() - metrics.stringWidth(Shutter.language.getProperty("preview"))) / 2;                                	
                    int y = player.getHeight() - (int) (player.getHeight()/24);
                     
                	g2.drawString(Shutter.language.getProperty("preview"), x, y);
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
					if (preview.exists())
						preview.delete();
					
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
                g2.setColor(new Color(65, 65, 65));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);	
                                
                if (Shutter.liste.getSize() > 0 && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
                {
	                //Mark in & out
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
		                int splitTime = playerInMark + Math.round((float) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.currentFPS / totalFrames));
		                do {
		                	
		                	g2.fillRect(splitTime + 1, 0, 1, getHeight() - 1);
		                	
		                	splitTime += Math.round((float) (waveformContainer.getSize().width * Integer.parseInt(splitValue.getText()) * FFPROBE.currentFPS / totalFrames));
		                	
		                	g2.setColor(new Color(Utils.themeColor.getRed(), Utils.themeColor.getGreen(), Utils.themeColor.getBlue(), alpha));
		                	
		                	alpha -= 10;
		                	
		                	if (alpha < 0)
		                		break;		             
           	
		                } while (splitTime < playerOutMark);
	                }
	                	                
	                //Masks
	                g2.setColor(new Color(45,45,45, 120)); 
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
		waveformContainer.setBounds(slider.getX(), slider.getY(), slider.getWidth(), slider.getHeight());
		Shutter.frame.getContentPane().add(waveformContainer);
		
		waveformIcon = new JLabel();
		waveformIcon.setOpaque(false);
		waveformIcon.setBounds(waveformContainer.getBounds());
		Shutter.frame.getContentPane().add(waveformIcon);
		
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
								
				if (Shutter.liste.getSize() > 0)
                {
					//IMPORTANT
					waveformContainer.requestFocus();
					
					sliderChange = true;
					
					if (playerIsPlaying())
						btnPlay.setText("⏸");		
					
					if (e.getX() >= 0 && e.getX() <= waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(e.getX(), cursorWaveform.getLocation().y);	
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
					else if (e.getX() < 0)
					{				
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);	
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{				
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);	
						slider.setValue((int) ((long) slider.getMaximum() * cursorWaveform.getLocation().x / waveformContainer.getSize().width));
					}
                }
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				mouseIsPressed = false;
				
				if (Shutter.liste.getSize() > 0)
                {	
					//Allows to wait for the last frame to load					
					long time = System.currentTimeMillis();

					if (playerIsPlaying() == false)
					{
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
							
							if (System.currentTimeMillis() - time > 1000)
								break;
							
						} while (playerLoop);
					}		
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
					
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
						updateGrpIn(playerCurrentFrame);
					}
					else if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && cursorWaveform.getX() > playerInMark && mouseIsPressed)
					{			
						cursorWaveform.setLocation(playerOutMark, 0);
						updateGrpOut(playerCurrentFrame + 1);
					}		
					
					sliderChange = false;								
					
					if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && mouseIsPressed)
					{
						playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
											
						//NTSC framerate
						playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
						
						playerSetTime(playerCurrentFrame);
					}
					else if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && mouseIsPressed)
					{
						playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText()) - 1;
						
						//NTSC framerate
						playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
						
						playerSetTime(playerCurrentFrame);
					}	
					else
					{
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {}
						} while (setTime.isAlive());
						
						//NTSC framerate
						playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
						
						playerSetTime(playerCurrentFrame);
					}					
										
					waveformContainer.repaint();
					
					waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
						
						//Make sure the value is not more than file length less one frame
						if (value < (totalFrames))
							slider.setValue(value);
					}
					else if (e.getX() <= 0)
					{					
						sliderChange = true;					
						cursorWaveform.setLocation(0, cursorWaveform.getLocation().y);	
						slider.setValue(0);
						playerSetTime(0);
					}
					else if (e.getX() > waveformContainer.getWidth() - 2)
					{
						sliderChange = true;					
						cursorWaveform.setLocation(waveformContainer.getWidth() - 2, cursorWaveform.getLocation().y);	
						slider.setValue((int) (totalFrames - 2));
						playerSetTime(totalFrames - 2);
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
					
                }
			}

			@Override
			public void mouseMoved(MouseEvent e) {	
											
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
		
		});
			
		cursorWaveform = new JPanel();
		cursorWaveform.setBackground(Color.RED);
		cursorWaveform.setBounds(0, 0, 2, waveformContainer.getSize().height);
		waveformContainer.add(cursorWaveform);		
				
		sliderVolume = new JSlider();
		sliderVolume.setName("sliderVolume");
		sliderVolume.setValue(50);			
		Shutter.frame.getContentPane().add(sliderVolume);
				
		lblVolume = new JLabel("🔊");
		if (System.getProperty("os.name").contains("Windows"))
		{
			lblVolume.setFont(new Font("", Font.PLAIN, 16));
		}
		else
			lblVolume.setFont(new Font("", Font.PLAIN, 12));
		lblVolume.setSize(lblVolume.getPreferredSize().width + 3, 16);			
		lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, sliderVolume.getY() + 2);	
		
		Shutter.frame.getContentPane().add(lblVolume);
				
		addWaveform(true);
	}
	
	private void grpIn(){
				
		caseInH = new JTextField();
		caseInH.setName("caseInH");
		caseInH.setText("00");
		caseInH.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInH.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInH.setColumns(10);
		Shutter.frame.getContentPane().add(caseInH);
		
		caseInM = new JTextField();
		caseInM.setName("caseInM");
		caseInM.setText("00");
		caseInM.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInM.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInM.setColumns(10);
		Shutter.frame.getContentPane().add(caseInM);
		
		caseInS = new JTextField();
		caseInS.setName("caseInS");
		caseInS.setText("00");
		caseInS.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInS.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInS.setColumns(10);
		caseInS.setBounds(caseInM.getX() + caseInM.getWidth() + 2, 17, 21, 21);
		Shutter.frame.getContentPane().add(caseInS);
		
		caseInF = new JTextField();
		caseInF.setName("caseInF");
		caseInF.setText("00");
		caseInF.setHorizontalAlignment(SwingConstants.RIGHT);
		caseInF.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
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
									
						playerSetTime(slider.getValue());	
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
									
						playerSetTime(slider.getValue());	
					}	
				}
			}

		});
				
		lblSpeed = new JLabel("x1"); //0.25 allow to get max preferred size width
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);	
					
					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);

					playerInMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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
	
	private static void updateGrpIn(float timeIn) {
	
		//NTSC framerate
		if (timeIn > 0)
			timeIn = Timecode.setNonDropFrameTC(timeIn);
		
		caseInH.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.currentFPS / 3600)));
		caseInM.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.currentFPS / 60) % 60));
		caseInS.setText(Shutter.formatter.format(Math.floor(timeIn / FFPROBE.currentFPS) % 60));    		
		caseInF.setText(Shutter.formatter.format(Math.floor(timeIn % FFPROBE.currentFPS)));
		
	}
	
	private void grpOut(){
		
		caseOutH = new JTextField();
		caseOutH.setName("caseOutH");
		caseOutH.setText("00");
		caseOutH.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutH.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutH.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutH);
				
		caseOutM = new JTextField();
		caseOutM.setName("caseOutM");
		caseOutM.setText("00");
		caseOutM.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutM.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutM.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutM);
		
		caseOutS = new JTextField();
		caseOutS.setName("caseOutS");
		caseOutS.setText("00");
		caseOutS.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutS.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutS.setColumns(10);
		Shutter.frame.getContentPane().add(caseOutS);
		
		caseOutF = new JTextField();
		caseOutF.setName("caseOutF");
		caseOutF.setText("00");
		caseOutF.setHorizontalAlignment(SwingConstants.RIGHT);
		caseOutF.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
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
					
					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
										
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

					playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText()) - 1;

					//NTSC framerate
					playerCurrentFrame = Timecode.getNonDropFrameTC(playerCurrentFrame);
					
					playerSetTime(playerCurrentFrame);
					
					playerOutMark = Math.round((float) (waveformContainer.getSize().width * playerCurrentFrame) / slider.getMaximum());
					waveformContainer.repaint();
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

	private static void updateGrpOut(float timeOut) {

		if (playerOutMark < waveformContainer.getWidth() - 2)
		{
			//NTSC framerate
			timeOut = Timecode.setNonDropFrameTC(timeOut);

			caseOutH.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.currentFPS / 3600)));
			caseOutM.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.currentFPS / 60) % 60));
			caseOutS.setText(Shutter.formatter.format(Math.floor(timeOut / FFPROBE.currentFPS) % 60));    		
			caseOutF.setText(Shutter.formatter.format(Math.floor(timeOut % FFPROBE.currentFPS)));
		}
		else
		{			
			caseOutH.setText(Shutter.formatter.format((FFPROBE.totalLength) / 3600000));
	        caseOutM.setText(Shutter.formatter.format(((FFPROBE.totalLength) / 60000) % 60) );
	        caseOutS.setText(Shutter.formatter.format((FFPROBE.totalLength / 1000) % 60));				        
	        caseOutF.setText(Shutter.formatter.format(((int) Math.floor((float) FFPROBE.totalLength / ((float) 1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS))));
		}
	}
	
	private void playerOptions() {
		
		caseVuMeter = new JCheckBox(Shutter.language.getProperty("caseVuMeter"));
		caseVuMeter.setName("caseVuMeter");	
		caseVuMeter.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseVuMeter.setSelected(true);		
		Shutter.frame.getContentPane().add(caseVuMeter);
		
		caseVuMeter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frameIsComplete = false;
							
				playerSetTime(slider.getValue());
			}

		});
		
		caseGPU = new JCheckBox(Shutter.language.getProperty("caseGPU"));
		caseGPU.setName("caseGPU");	
		caseGPU.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseGPU.setSelected(false);
		Shutter.frame.getContentPane().add(caseGPU);
		
		caseGPU.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frameIsComplete = false;
							
				playerSetTime(slider.getValue());
			}

		});
		
		casePlaySound = new JCheckBox(Shutter.language.getProperty("casePlaySound"));
		casePlaySound.setName("casePlaySound");	
		casePlaySound.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		casePlaySound.setSelected(true);
		Shutter.frame.getContentPane().add(casePlaySound);
					
		caseInternalTc = new JCheckBox(Shutter.language.getProperty("caseTcInterne"));
		caseInternalTc.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
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
		splitValue.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
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
		lblSplitSec.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSplitSec.setVisible(false);
		lblSplitSec.setBounds(splitValue.getX() + splitValue.getWidth() + 2, splitValue.getY(), lblSplitSec.getPreferredSize().width, 16);
		Shutter.frame.getContentPane().add(lblSplitSec);
		
		comboMode.setName("comboMode");
		comboMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		comboMode.setMaximumRowCount(3);
		comboMode.setSize(76, 22);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			Shutter.frame.getContentPane().add(comboMode);
		
		comboMode.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				btnPreview.setBounds(slider.getX() + slider.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
				lblSplitSec.setBounds(btnPreview.getX() + 10, caseGPU.getY() + 2, lblSplitSec.getPreferredSize().width, 16);
				splitValue.setBounds(lblSplitSec.getX() - splitValue.getWidth() - 2, caseGPU.getY() + 2, 34, 16);
				
				if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")) && showInfoMessage)
				{
					JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("mayNotWorkWithGOP"), Shutter.language.getProperty("mode") + " " + Shutter.language.getProperty("removeMode"), JOptionPane.INFORMATION_MESSAGE);
					showInfoMessage = false;
					
					btnPreview.setVisible(true);
					splitValue.setVisible(false);
					lblSplitSec.setVisible(false);
					comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);	
				}
				else if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("splitMode")))
				{
					btnPreview.setVisible(false);
					splitValue.setVisible(true);
					lblSplitSec.setVisible(true);
					comboMode.setLocation(splitValue.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);
				}
				else
				{
					btnPreview.setVisible(true);
					splitValue.setVisible(false);
					lblSplitSec.setVisible(false);
					comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);	
				}			
								
				lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, caseInternalTc.getY() + 3, lblMode.getPreferredSize().width, 16);			
				caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, caseInternalTc.getY(), caseVuMeter.getPreferredSize().width, 23);
				caseGPU.setBounds(caseVuMeter.getX() - caseGPU.getPreferredSize().width - 5, caseInternalTc.getY(), caseGPU.getPreferredSize().width, 23);
				
				waveformContainer.repaint();
			}
	
		});
		
		lblMode = new JLabel(Shutter.language.getProperty("mode"));
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
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
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600 * FFPROBE.currentFPS
								+ Integer.valueOf(FFPROBE.timecode2) * 60 * FFPROBE.currentFPS
								+ Integer.valueOf(FFPROBE.timecode3) * FFPROBE.currentFPS
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
		
		if (Shutter.caseEnableCrop.isSelected())
		{
			Shutter.comboPreset.getEditor().setItem((double) Math.round((double) ((double) outW / outH) * 100.0) / 100.0);		
		}
		else
		{
			Shutter.comboPreset.getEditor().setItem(Shutter.language.getProperty("aucun"));
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
			Shutter.textTcPosX.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().x * Shutter.imageRatio)));
			Shutter.textTcPosY.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().y * Shutter.imageRatio)));  
		}
		else
		{
			Shutter.textTcPosX.setText("0");
			Shutter.textTcPosY.setText("0"); 
		}
		
		if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected())
		{						
			Shutter.textNamePosX.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().x * Shutter.imageRatio)));
			Shutter.textNamePosY.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().y * Shutter.imageRatio)));  
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
	
	public static void writeCurrentSubs(float inputTime) {	
				
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{
			try {
	
				BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(Shutter.subtitlesFilePath.toString()),  StandardCharsets.UTF_8);
	            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Shutter.subtitlesFile.toString()),  StandardCharsets.UTF_8);
	
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
	            		String split[] = line.split("-->");				
	            		String inTimecode[] = split[0].replace(",", ":").replace(" ","").split(":");
	            		String outTimecode[] = split[1].replace(",", ":").replace(" ","").split(":");
		            		
	    				int inH = Integer.parseInt(inTimecode[0]) * 3600;
	    				int inM = Integer.parseInt(inTimecode[1]) * 60;
	    				int inS = Integer.parseInt(inTimecode[2]);
	    				int inF = Integer.parseInt(inTimecode[3]);
	    				float subsInTime = (inH + inM + inS) * FFPROBE.currentFPS + inF / inputFramerateMS;
	    				
	    				if (subNumber == 1)
	            		{
	    					sliderChange = true;
	    					slider.setValue((int) subsInTime);
	    					sliderChange = false;
	    					waveformContainer.repaint();
	            		}
	    				
	    				int outH = Integer.parseInt(outTimecode[0]) * 3600;
	    				int outM = Integer.parseInt(outTimecode[1]) * 60;
	    				int outS = Integer.parseInt(outTimecode[2]);
	    				int outF = Integer.parseInt(outTimecode[3]);
	    				float subsOuTime = (outH + outM + outS) * FFPROBE.currentFPS + outF / inputFramerateMS;
	
	    				long inOffset = (long) (subsInTime - inputTime);
						long outOffset = (long) (subsOuTime - inputTime);
						
						String iH = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.currentFPS / 3600));
						String iM = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.currentFPS / 60) % 60);
						String iS = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.currentFPS) % 60);    		
						String iF = Shutter.formatterToMs.format(Math.floor(inOffset % FFPROBE.currentFPS * inputFramerateMS));
						
						String oH = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.currentFPS / 3600));
						String oM = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.currentFPS / 60) % 60);
						String oS = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.currentFPS) % 60);    		
						String oF = Shutter.formatterToMs.format(Math.floor(outOffset % FFPROBE.currentFPS * inputFramerateMS));
						
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
	
			} catch (Exception e) {}
		}
	}
	
	public static boolean loadWatermark(int size) {
		
		try {

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
									
			int logoFinalSizeWidth = (int) Math.floor((float) FFPROBE.imageWidth / Shutter.imageRatio);		
			int logoFinalSizeHeight = (int) Math.floor((float) FFPROBE.imageHeight / Shutter.imageRatio);
	
			//Adapt to size
			logoFinalSizeWidth = (int) Math.floor((float) logoFinalSizeWidth * ((double) size / 100));
			logoFinalSizeHeight = (int) Math.floor((float) logoFinalSizeHeight * ((double) size / 100));

			//Preserve location
			int newPosX = (int) Math.floor((Shutter.logo.getWidth() - logoFinalSizeWidth) / 2);
			int newPosY = (int) Math.floor((Shutter.logo.getHeight() - logoFinalSizeHeight) / 2);
			
			if (Shutter.overlayDeviceIsRunning)
			{
				FFMPEG.run(" -v quiet " + RecordInputDevice.setOverlayDevice() + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bicubic -f image2pipe pipe:-");
			}
			else
				FFMPEG.run(" -v quiet -i " + '"' + Shutter.logoFile + '"' + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bicubic -f image2pipe pipe:-");

			do {
				Thread.sleep(10);
			} while (FFMPEG.process.isAlive() == false);

			InputStream videoInput = FFMPEG.process.getInputStream(); 
			InputStream is = new BufferedInputStream(videoInput);
			Shutter.logoPNG = ImageIO.read(is);
	       	
			if (Shutter.logo.getWidth() == 0)
				Shutter.logo.setLocation((int) Math.floor(player.getWidth() / 2 - logoFinalSizeWidth / 2), (int) Math.floor(player.getHeight() / 2 - logoFinalSizeHeight / 2));	
			else
				Shutter.logo.setLocation(Shutter.logo.getLocation().x + newPosX, Shutter.logo.getLocation().y + newPosY);
			
            Shutter.logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);
            
            //Saving location
			Shutter.logoLocX = Shutter.logo.getLocation().x;
			Shutter.logoLocY = Shutter.logo.getLocation().y;	
			
            Shutter.logo.repaint();        
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("cantLoadShutter.logo"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
		} 
		finally {
			
			//IMPORTANT keeps original source file data			
			try {					
				FFPROBE.Data(videoPath);
				do {								
					Thread.sleep(10);								
				} while (FFPROBE.isRunning);
				
			} catch (InterruptedException e) {}
			
        	Shutter.enableAll();  
        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
			else
				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
        }
	return true;
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
		
		if ((forceRefresh || runProcess.isAlive() == false) && videoPath != null)
		{			
			
			runProcess = new Thread (new Runnable() {
			
			@Override
			public void run() {

					//Important permet de lancer le runtime process dans FFMPEG
					boolean display = false;
					if (Shutter.caseDisplay.isSelected())
					{
						display = true;
						Shutter.caseDisplay.setSelected(false);
					}
					
					//Stop player
					if (playerIsPlaying())
					{
						btnPlay.doClick();
					}
				
			        try
			        {	 			        	
			        	File file = new File(videoPath);
			        			        						
						String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
						boolean isRaw = false;
			    		
						if (preview.exists() == false)
						{
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
							
							if (extension.toLowerCase().equals(".pdf"))
							{
								 XPDF.toFFPROBE(file.toString());	
								 do
								 {
									 Thread.sleep(100);
								 }
								 while (XPDF.isRunning);								 
							}				
							else if (isRaw)
							{
								 EXIFTOOL.run(file.toString());	
								 do
								 {
									 Thread.sleep(100);						 
								 }
								 while (EXIFTOOL.isRunning);
							}
	       	        						
							Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder")+ " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
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
												
						String deinterlace = "";
						
						if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
							deinterlace = " -vf yadif=0:" + FFPROBE.fieldOrder + ":0";		
	
						//Input point
						String inputPoint = " -ss " + (float) (playerCurrentFrame) * inputFramerateMS + "ms";
						if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
							inputPoint = "";
												
						//Creating preview file																
						String cmd = deinterlace + " -vframes 1 -an -s " + player.getWidth() + "x" + player.getHeight() + " -sws_flags bicubic -y ";	
						if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
						{
							cmd = deinterlace + " -vframes 1 -an -s " + player.getHeight() + "x" + player.getWidth() + " -sws_flags bicubic -y ";
						}
	
						//EXR gamma
						String EXRGamma = "";						
						if (extension.toLowerCase().equals(".exr"))
						{
							EXRGamma = Colorimetry.setInputCodec(extension);
						}
						
						if (new File(Shutter.dirTemp + "preview.bmp").exists() == false && Shutter.caseAddSubtitles.isSelected() == false)
						{											   		
							if (extension.toLowerCase().equals(".pdf"))
							{
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								XPDF.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (isRaw)
							{	
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								DCRAW.run(" -v -w -c -q 0 -o 1 -h -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (Shutter.inputDeviceIsRunning) //Screen capture		
							{					
								showFPS.setVisible(false);
								Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								Shutter.frame.setVisible(false);
								FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');								
							}
							else									
							{
								FFMPEG.run(EXRGamma + inputPoint + " -i " + '"' + file.toString() + '"' + cmd + '"' + preview + '"');			
							}
							
				            do {
				            	Thread.sleep(10);  
				            } while ((FFMPEG.isRunning && FFMPEG.error == false) || (XPDF.isRunning && XPDF.error == false) || (DCRAW.isRunning && DCRAW.error == false));
				        
				            Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				            
						}	
						
						//Screen capture
						if (Shutter.inputDeviceIsRunning && preview.exists() == false)
						{
							cmd = " -vframes 1 -an " + setFilter("", "", true) + " ";	
														
							Shutter.frame.setVisible(false);
							FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');
						}				
						else
						{							
							//Subtitles are visible only from a video file
							if (Shutter.caseAddSubtitles.isSelected())
							{				
								FFMPEG.run(EXRGamma + " -v quiet" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter("","", true) + " -vframes 1 -c:v bmp -f image2pipe pipe:-"); 
							}
							else
							{
								FFMPEG.run(EXRGamma + " -v quiet -i " + '"' + preview + '"' + setFilter("","", true) + " -vframes 1 -c:v bmp -f image2pipe pipe:-"); 							    	
							}
						}

						do {
	    					Thread.sleep(10);
	    				} while (FFMPEG.process.isAlive() == false);
						
						InputStream videoInput = FFMPEG.process.getInputStream();		
						InputStream is = new BufferedInputStream(videoInput);
						frameVideo = ImageIO.read(is);	
				
						if (frameVideo != null)
						{
							player.repaint();
						}
			        }
				    catch (Exception e)
				    {				
				    	e.printStackTrace();
			 	       	//JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
				    }
			        finally 
			        {
			        	Shutter.enableAll();
	          			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
	        			else
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
	          			
			    		if (display)
			    			Shutter.caseDisplay.setSelected(true);
			        }
				}
			});
			runProcess.start();
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
				background = ",BorderStyle=4,BackColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&,Shutter.outline=0";
			else
				background = ",Shutter.outline=" + Shutter.outline + ",Shutter.outlineColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&";
				
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

		//Scaling
		int width = player.getWidth();
		int height = player.getHeight();
				
		if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false)
		{
			String i[] = FFPROBE.imageResolution.split("x");        
			String o[] = FFPROBE.imageResolution.split("x");
						
			if (Shutter.comboResolution.getSelectedItem().toString().contains("%"))
			{
				double value = (double) Integer.parseInt(Shutter.comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
				
				o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * value));
				o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * value));
			}					
			else if (Shutter.comboResolution.getSelectedItem().toString().contains("x"))
			{
				o = Shutter.comboResolution.getSelectedItem().toString().split("x");
			}
			else if (Shutter.comboResolution.getSelectedItem().toString().contains(":"))
			{
				o = Shutter.comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
				
				int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);          	
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);        	
	        	float ir = (float) iw / ih;
						        	
				if (o[0].toString().equals("1")) // = auto
				{
					o[0] = String.valueOf((int) Math.round((float) oh * ir));
				}
        		else
        		{
        			o[1] = String.valueOf((int) Math.round((float) ow / ir));
        		}
			}
			
			if (Integer.parseInt(o[0]) < width || Integer.parseInt(o[1]) < height)
			{
				if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
				{
					width = Integer.parseInt(o[1]);
					height = Integer.parseInt(o[0]);
				}
				else
				{
					width = Integer.parseInt(o[0]);
					height = Integer.parseInt(o[1]);
				}
			}			
		}
		else			
		{
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
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
		
		if (FFMPEG.isGPUCompatible && caseGPU.isSelected() && noGPU == false)
		{
			String bitDepth = "nv12";
			if (FFPROBE.imageDepth == 10)
			{
				bitDepth = "p010";
			}			
			
			//Auto GPU Shutter.selection	
			if (FFMPEG.cudaAvailable)
			{
				filter = filter.replace("yadif", "yadif_cuda");			
				filter += "scale_cuda=" + width + ":" + height + ":interp_algo=" + algorithm.replace("neighbor", "nearest").replace("bilinear", "bicubic") + ",hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.qsvAvailable && yadif == "")
			{
				filter += "scale_qsv=" + width + ":" + height + ":mode=low_power,hwdownload,format=" + bitDepth;
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
				
		//Alpha channel
		if (FFPROBE.pixelformat.contains("a"))
		{
			filter += ",split=2[bg][fg];[bg]drawbox=c=0x2d2d2d:replace=1:t=fill[bg];[bg][fg]overlay=0:0,format=rgb24";		
		}
		
		//Speed
		if (speed != "")
		{
			filter += "," + speed;
		}			
		
		//EQ
		String eq = "";	
		
		//Stabilisation
		if (Shutter.stabilisation != "")
			eq = Shutter.stabilisation;
		
		//Blend
		if (preview.exists() == false) //Show only on playing
		{
			eq = ImageSequence.setBlend(eq);
			eq = ImageSequence.setMotionBlur(eq);
		}
		
		//LUTs
		eq = Colorimetry.setLUT(eq);	
		
		//Levels
		eq = Colorimetry.setLevels(eq);
		
		//Colormatrix
		eq = Colorimetry.setColormatrix(eq);
		
		//Rotate
		if (Shutter.caseRotate.isSelected() || Shutter.caseMiror.isSelected())
			eq = settings.Image.setRotate(eq);
		
		//Colorimetry
		if (Shutter.caseEnableColorimetry.isSelected())
		{			
			String color = Colorimetry.setEQ(false);
						
			if (eq != "" && color != "")
			{
				eq += "," + color;
			}
			else if (color != "")
			{
				eq += color;
			}
			
			if (Shutter.sliderAngle.getValue() != 0)
			{
				if (eq.contains("scale"))
				{
					eq = eq.replace("scale=" + FFPROBE.imageWidth + ":" + FFPROBE.imageHeight,  "scale=" + player.getWidth() + ":" + player.getHeight());
				}
				else
				{
					eq += ",scale=" + player.getWidth() + ":" + player.getHeight();
				}
			}
		}
				
		//Deflicker			
		eq = Corrections.setDeflicker(eq);
			
		//Deband			
		eq = Corrections.setDeband(eq);
				 
		//Details			
		eq = Corrections.setDetails(eq);				
											            	
		//Denoise			
		eq = Corrections.setDenoiser(eq);
		
		//Exposure
		if (preview.exists() == false) //Show only on playing
			eq = Corrections.setSmoothExposure(eq);	
		
		//Limiter
		eq = Corrections.setLimiter(eq);

		//Fade-in Fade-out
		if (Shutter.caseVideoFadeIn.isSelected() || Shutter.caseVideoFadeOut.isSelected())
		{
			eq = Transitions.setVideoFade(eq, true);
		}
								
		if (eq.isEmpty() == false)
		{
			filter += "," + eq;
		}
		
		if (caseVuMeter.isSelected() && FFPROBE.hasAudio && Shutter.caseAddSubtitles.isSelected() == false && preview.exists() == false)
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
			
			int subsWidth = (int) ((float) (Integer.parseInt(Shutter.textSubsWidth.getText()) / Shutter.imageRatio));
			int subsPosY = (int) ((float) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / Shutter.imageRatio);
			
			return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + subsWidth + ":" + player.getHeight() + "+" + subsPosY
          			+ ",subtitles='" + Shutter.subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Shutter.textSubsSize.getText() + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"'
          			+ " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];[v][1:v]overlay=x=" + (int) ((player.getWidth() - subsWidth) / 2) + ",scale=" + player.getWidth() + ":" + player.getHeight() + '"'; 
		}
		else
		{
			caseVuMeter.setEnabled(true);	
			return filter;
		}

	}
	
	public static void resizeAll() {
		
		if (Shutter.frame.getWidth() > 332)	
		{		
			if (preview.exists())
				preview.delete();
					
			float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
			float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
	
			//Waveforms
			addWaveform(false);				 					
			
			//Players 		
			float ratio = FFPROBE.imageRatio;
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
			{
				ratio = (float) FFPROBE.imageHeight / FFPROBE.imageWidth;
			}
			
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
			
			if (ratio < 1.3f)
			{
				int maxHeight = (int) (Shutter.frame.getHeight() / 1.6f);
				
				if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
				{
					maxHeight = Shutter.frame.getHeight() - (Shutter.grpChooseFiles.getY() + 8) - (Shutter.frame.getHeight() - (Shutter.grpProgression.getY() + Shutter.grpProgression.getHeight()));
				}
				
				player.setSize((int) (maxHeight * ratio), maxHeight);
			}
			else
			{
				int maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2;
				
				if (Shutter.frame.getWidth() == (1350 - 312))
				{
					maxWidth = Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth();
				}
				
				player.setSize(maxWidth, (int) (maxWidth / ratio));		
			}	
				
			int y = Shutter.frame.getHeight() / 2 - player.getHeight() / 2 - 58;
			if (ratio < 1.3f)
			{
				y = Shutter.grpChooseFiles.getY() + 50;
			}	
			
			if ((FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) && videoPath != null) //Image
			{
				y = Shutter.frame.getHeight() / 2 - player.getHeight() / 2;
			}
			
			if (Shutter.frame.getWidth() < 1350)
			{
				player.setLocation((1350 - player.getSize().width) / 2, y);
			}
			else
			{
				player.setLocation((Shutter.frame.getSize().width - player.getSize().width) / 2, y);
			}
			
			//IMPORTANT video canvas must be a multiple of 4!
			player.setSize(player.getWidth() - (player.getWidth() % 4), player.getHeight());
			
			Shutter.imageRatio = (float) FFPROBE.imageWidth / player.getWidth();
			
			//Sliders
			if (Shutter.frame.getWidth() == (1350 - 312))
			{
				slider.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth(), 40);
			}
			else
				slider.setBounds(Shutter.grpChooseFiles.getWidth() + 20, player.getY() + player.getHeight() + 26, Shutter.frame.getWidth() - 40 - Shutter.grpChooseFiles.getWidth() * 2, 40);
			
			waveformContainer.setBounds(slider.getX(), slider.getY(), slider.getWidth(), slider.getHeight());
			waveformIcon.setBounds(waveformContainer.getBounds());
			
			if (playerCurrentFrame <= 1)
			{
				cursorWaveform.setBounds(0, 0, 2, waveformContainer.getSize().height);
			}
			else
			{
				if (cursorWaveform.getX() > waveformContainer.getWidth() - 2)
				{
					cursorWaveform.setLocation(waveformContainer.getWidth() - 2, 0);
				}
				else
					cursorWaveform.setBounds(Math.round((float) (waveformContainer.getSize().width * slider.getValue()) / slider.getMaximum()), 0, 2, waveformContainer.getSize().height);
			}
			
			playerInMark = Math.round((float) (waveformContainer.getSize().width * timeIn) / totalFrames);
						
			if ((int) Math.ceil(timeOut) < totalFrames)
				playerOutMark = Math.round((float) (waveformContainer.getSize().width * timeOut - 1) / totalFrames);
			else
				playerOutMark = waveformContainer.getWidth() - 2;	
			
			waveformContainer.repaint();
			
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
			
			//grpWatermark
			if (Shutter.caseAddWatermark.isSelected())
			{
				loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
				Shutter.logo.setLocation((int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosX.getText()) / Shutter.imageRatio), (int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosY.getText()) / Shutter.imageRatio));
			}
			
			//grpCrop
			Shutter.frameCropX = player.getLocation().x;
			Shutter.frameCropY = player.getLocation().y;
			if (Shutter.caseEnableCrop.isSelected())
			{
				Shutter.selection.setLocation((int) Math.round(Integer.valueOf(Shutter.textCropPosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textCropPosY.getText()) / Shutter.imageRatio));
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
			if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected())
			{
				Shutter.timecode.setLocation((int) Math.round(Integer.valueOf(Shutter.textTcPosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textTcPosY.getText()) / Shutter.imageRatio));
				Shutter.tcLocX = Shutter.timecode.getLocation().x;
				Shutter.tcLocY = Shutter.timecode.getLocation().y;			
			}
			if (Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
			{
				Shutter.fileName.setLocation((int) Math.round(Integer.valueOf(Shutter.textNamePosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textNamePosY.getText()) / Shutter.imageRatio));
				Shutter.fileLocX = Shutter.fileName.getLocation().x;
				Shutter.fileLocY = Shutter.fileName.getLocation().y;
			}
			
			btnPrevious.setBounds(player.getLocation().x + player.getSize().width / 2 - 21 - 4, slider.getY() + slider.getHeight() + 6, 22, 21);		
			btnNext.setBounds(player.getLocation().x + player.getSize().width / 2 + 4, btnPrevious.getLocation().y, 22, 21);		
			btnPlay.setBounds(btnPrevious.getLocation().x - 40 - 4, btnPrevious.getLocation().y, 40, 21);				
			btnStop.setBounds(btnNext.getLocation().x + btnNext.getSize().width + 4, btnNext.getLocation().y, 40, 21);	
			btnMarkIn.setBounds(btnPlay.getLocation().x - 22 - 4, btnPlay.getLocation().y, 22, 21);				
			btnGoToIn.setBounds(btnMarkIn.getLocation().x - 40 - 4, btnMarkIn.getLocation().y, 40, 21);				
			btnMarkOut.setBounds(btnStop.getLocation().x + btnStop.getSize().width + 4, btnStop.getLocation().y, 22, 21);				
			btnGoToOut.setBounds(btnMarkOut.getLocation().x + btnMarkOut.getSize().width + 4, btnMarkOut.getLocation().y, 40, 21);		
			showFPS.setBounds(player.getX() + player.getWidth() / 2, player.getY() - 18, player.getWidth() / 2, showFPS.getPreferredSize().height);
			showScale.setBounds(player.getX(), showFPS.getY(), player.getWidth() / 2, showScale.getPreferredSize().height);
			
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
			casePlaySound.setBounds(caseInternalTc.getX() + caseInternalTc.getWidth() + 4, caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);
	
			btnPreview.setBounds(slider.getX() + slider.getWidth() - 16, caseInternalTc.getY() + 2, 16, 16);
			lblSplitSec.setBounds(btnPreview.getX() + 10, caseGPU.getY() + 2, lblSplitSec.getPreferredSize().width, 16);
			splitValue.setBounds(lblSplitSec.getX() - splitValue.getWidth() - 2, caseGPU.getY() + 2, 34, 16);		
			
			if (splitValue.isVisible())
			{
				comboMode.setLocation(splitValue.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);
			}
			else
				comboMode.setLocation(btnPreview.getX() - comboMode.getWidth() - 4, caseInternalTc.getY() - 1);		
			
			lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, caseInternalTc.getY() + 3, lblMode.getPreferredSize().width, 16);			
			caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, caseInternalTc.getY(), caseVuMeter.getPreferredSize().width, 23);
			caseGPU.setBounds(caseVuMeter.getX() - caseGPU.getPreferredSize().width - 5, caseInternalTc.getY(), caseGPU.getPreferredSize().width, 23);
			
			//Sliders
			sliderSpeed.setLocation(btnGoToIn.getX() -  sliderSpeed.getWidth() - 4, btnPrevious.getY() + 1);
			lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
						
			lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, lblSpeed.getY());	
			sliderVolume.setBounds(lblVolume.getX() + lblVolume.getWidth() + 1, sliderSpeed.getY(), sliderSpeed.getWidth(), 22);	
			
			if (Shutter.windowDrag == false && videoPath != null)
			{	
				if (Shutter.inputDeviceIsRunning || FFPROBE.totalLength <= 40)
				{
					loadImage(false);
					waveformIcon.setVisible(false);
				}
				else
				{			
					if (btnPlay.isEnabled())
						playerFreeze();	
				}
			}			
		}
		
		if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected() || Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
		{
			refreshTimecodeAndText();
		}
		
		Shutter.statusBar.setBounds(0, Shutter.frame.getHeight() - 23, Shutter.frame.getWidth(), 22);
		
		if (Shutter.frame.getWidth() >= 1350)
		{
			Shutter.lblArrows.setLocation(Shutter.statusBar.getWidth() / 2 - Shutter.lblArrows.getWidth() / 2, Shutter.lblArrows.getY());			
			Shutter.lblYears.setVisible(true);
		}
		else
		{
			Shutter.lblArrows.setLocation(Shutter.frame.getWidth() - Shutter.lblArrows.getWidth() - 7, Shutter.lblArrows.getY());
			Shutter.lblYears.setVisible(false);
		}
		
		Shutter.lblYears.setLocation(Shutter.frame.getWidth() - Shutter.lblYears.getPreferredSize().width - 6, Shutter.lblBy.getY());
	}
	
	public static void totalDuration() {	
		
		try {
						
			float totalIn =  (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
			float totalOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
			
			float total = (totalOut - totalIn);
						
			//NTSC timecode
			total = Timecode.getNonDropFrameTC(total);

			if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
				total = totalFrames - total;	

			durationH = (int) Math.floor(total / FFPROBE.currentFPS / 3600);
			durationM = (int) Math.floor((total / FFPROBE.currentFPS / 60) % 60);
			durationS = (int) Math.floor((total / FFPROBE.currentFPS) % 60);
			durationF = (int) Math.round(total % FFPROBE.currentFPS);
						
			lblDuration.setText(Shutter.language.getProperty("lblDuree") + " " + durationH + "h " + durationM +"min " + durationS + "sec " + durationF + "i" + " | " + Shutter.language.getProperty("lblTotalFrames") + " " + (int) Math.ceil(total));
			
			if (total <= 0)
			{
				lblDuration.setVisible(false);  
			}
			else if (waveformContainer.isVisible() && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
			{
	    		lblDuration.setVisible(true);   
	    		
	    		//Durée H264
	    		switch (Shutter.comboFonctions.getSelectedItem().toString())
	    		{
					case "H.264":
					case "H.265":
					case "WMV":
					case "MPEG-1":
					case "MPEG-2":
					case "VP8":
					case "VP9":
					case "AV1":
					case "OGV":
					case "MJPEG":
					case "Xvid":
					case "Blu-ray":

						Shutter.textH.setText(Shutter.formatter.format(Math.floor(total / FFPROBE.currentFPS / 3600)));
						Shutter.textM.setText(Shutter.formatter.format(Math.floor(total / FFPROBE.currentFPS / 60) % 60));
						Shutter.textS.setText(Shutter.formatter.format(Math.floor(total / FFPROBE.currentFPS) % 60));    		
						Shutter.textF.setText(Shutter.formatter.format(Math.round(total % FFPROBE.currentFPS)));
			    	     
			    	    FFPROBE.setFilesize();
			    	    
		    	     break;
	    		}
			}
		
		} catch (Exception e){}
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

    	if (playerVideo != null && time - offset < totalFrames - 1)
    	{    					    			 		
			String h = Shutter.formatter.format(Math.floor(time / FFPROBE.currentFPS / 3600));
			String m = Shutter.formatter.format(Math.floor(time / FFPROBE.currentFPS / 60) % 60);
			String s = Shutter.formatter.format(Math.floor(time / FFPROBE.currentFPS) % 60);   
			String f = Shutter.formatter.format(Math.floor(time % FFPROBE.currentFPS));
					    	
			String dropFrame = ":";
	        if (Timecode.isDropFrame())
        	{
	        	dropFrame = ";";
        	}
	        
			lblPosition.setText(Shutter.language.getProperty("grpTimecode") + Shutter.language.getProperty("colon") + " " + h + ":" + m + ":" + s + dropFrame + f);	    
			
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
					}
    				else
    				{
    					if (cursorWaveform.getX() > waveformContainer.getWidth() - 2)
						{
							cursorWaveform.setLocation(waveformContainer.getWidth() - 2, 0);
						}
						else if (newValue != cursorWaveform.getX()) //Only refresh when the value is different
						{					
							cursorWaveform.setLocation(newValue, 0);
						}
    				}
    			}
    		}    
    	}
    	
    	if (time - offset >= totalFrames - 2)
    	{
    		btnPlay.setText("▶");
    	}
    		
    }

	public static void loadSettings(File encFile) {
		/*
		Thread t = new Thread (new Runnable() 
		{
			@Override
			public void run() {
				
			try {
			
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				do {
					Thread.sleep(10);					
				} while (btnPlay == null);

				File fXmlFile = encFile;
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
								
				for (int temp = 0; temp < nList.getLength(); temp++) 
				{
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) nNode;
						
						//Player						
						for (Component p : Shutter.frame.getContentPane().getComponents())
						{
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
																		
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}										
							}							
						}	
						
						//grpImageAdjustement
						for (Component p : grpImageAdjustement.getComponents())
						{				
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{								
								if (p instanceof JSlider)
								{
									//Value
									((JSlider) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																		
									//State
									((JSlider) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JSlider) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}							
						}	
						
						//grpCorrections
						for (Component p : grpCorrections.getComponents())
						{				
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{	
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
																		
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}		
								else if (p instanceof JSlider)
								{
									//Value
									((JSlider) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																		
									//State
									((JSlider) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JSlider) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}							
						}
						
						//grpCrop
						for (Component p : grpCrop.getComponents())
						{				
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{			
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
																		
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}		
								else if (p instanceof JTextField)
								{										
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								
									//Position des éléments
									if (p.getName().equals("textPosX") && textCropPosX.getText().length() > 0)
									{
										int value = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * player.getHeight()) / FFPROBE.imageHeight);	
										selection.setLocation(value, selection.getLocation().y);	
									}
									
									if (p.getName().equals("textPosY") && textCropPosY.getText().length() > 0)
									{
										int value = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * player.getWidth()) / FFPROBE.imageWidth);	
										selection.setLocation(selection.getLocation().x, value);
									}
									
									if (p.getName().equals("textWidth") && textCropWidth.getText().length() > 0)
									{
										int value = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
										selection.setSize(value, selection.getHeight());
									}
									
									if (p.getName().equals("textHeight") && textCropHeight.getText().length() > 0)
									{
										int value = (int) Math.round((float) (Integer.valueOf(textCropHeight.getText()) * player.getWidth()) / FFPROBE.imageWidth);
										selection.setSize(selection.getWidth(), value);
									}
								}
							}
						}
						
						//grpOverlay
						for (Component p : grpOverlay.getComponents())
						{
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{
								if (p instanceof JPanel)
								{									
									//Value
									String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
									String s2[] = s[1].split(",");
									((JPanel) p).setBackground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));
									
									if (p.getName().equals("panelTcColor"))
									{
										foregroundColor = panelTcColor.getBackground();
									}
									else if (p.getName().equals("panelTcColor2"))
									{
										backgroundColor = panelTcColor2.getBackground();
									}
								}
								
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
									
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
								else if (p instanceof JLabel)
								{									
									//Value
									((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));																			
								}
								else if (p instanceof JComboBox)
								{									
									//Value
									((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
								}
								else if (p instanceof JTextField)
								{			
									long time = System.currentTimeMillis();
									
									do {
										try {
											Thread.sleep(100);
										} catch (InterruptedException er) {}
										
										if (System.currentTimeMillis() - time > 1000)
											frameIsComplete = true;
																	
									} while (frameIsComplete == false);
									
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
									//Elements position
									if (p.getName().equals("textNamePosX"))
										fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), fileName.getLocation().y);	
									
									if (p.getName().equals("textNamePosY"))
										fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
																		
									if (p.getName().equals("textTcPosY"))
										timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
									
									if (p.getName().equals("textTcPosX"))
										timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), timecode.getLocation().y);
								}
							}
						}
						
						//grpSubtitles
						for (Component p : grpSubtitles.getComponents())
						{
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
																		
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}		
								else if (p instanceof JPanel)
								{
									//Value
									String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
									String s2[] = s[1].split(",");
									((JPanel) p).setBackground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));
									
									if (p.getName().equals("panelSubsColor"))
									{
										fontSubsColor = panelSubsColor.getBackground();
									}
									else if (p.getName().equals("panelSubsColor2"))
									{
										backgroundSubsColor = panelSubsColor2.getBackground();
									}

								}
								else if (p instanceof JButton)
								{									
									//Value
									String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
									String s2[] = s[1].split(",");
									
									if (Integer.valueOf(s2[0]) == 0 && Integer.valueOf(s2[1]) == 0 && Integer.valueOf(s2[2]) == 0)								
										((JButton) p).setForeground(Color.BLACK);	
									else
										((JButton) p).setForeground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));	
								}
								else if (p instanceof JLabel)
								{									
									//Value
									((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));

								}
								else if (p instanceof JComboBox)
								{
									//Value
									((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									
								}
								else if (p instanceof JTextField)
								{									
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}
						}					
						
						//grpWatermark
						for (Component p : grpWatermark.getComponents())
						{			
							if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
							{								
								if (p instanceof JCheckBox)
								{									
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JCheckBox) p).isSelected() == false)
											((JCheckBox) p).doClick();
									}
									else
									{
										if (((JCheckBox) p).isSelected())
											((JCheckBox) p).doClick();
									}
																		
									//State
									((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}		
								else if (p instanceof JTextField)
								{											
									//Value
									((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}
						}						
					}
				}			
						
				loadImage(false);
				
				if (frameVideo != null)
				{
					player.repaint();	
				}
				
				//grpCrop
				if (Shutter.caseEnableCrop.isSelected())
				{
					Shutter.selection.setLocation((int) Math.round(Integer.valueOf(Shutter.textCropPosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textCropPosY.getText()) / Shutter.imageRatio));
					int w = (int) Math.round((float)  (Integer.valueOf(Shutter.textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					int h = (int) Math.round((float)  (Integer.valueOf(Shutter.textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					
					if (w > player.getWidth())
						w = player.getWidth();
					
					if (h > player.getHeight())
						h = player.getHeight();
					
					Shutter.selection.setSize(w , h);	
					
					Shutter.frameCropX = player.getLocation().x;
					Shutter.frameCropY = player.getLocation().y;
					
					Shutter.anchorRight = Shutter.selection.getLocation().x + Shutter.selection.getWidth();
					Shutter.anchorBottom = Shutter.selection.getLocation().y + Shutter.selection.getHeight();					
					checkSelection();
				}
				
				//grpOverlay
				if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected())
				{
					Shutter.timecode.setLocation((int) Math.round(Integer.valueOf(Shutter.textTcPosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textTcPosY.getText()) / Shutter.imageRatio));
					Shutter.tcLocX = Shutter.timecode.getLocation().x;
					Shutter.tcLocY = Shutter.timecode.getLocation().y;			
				}
				if (Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected())
				{
					Shutter.fileName.setLocation((int) Math.round(Integer.valueOf(Shutter.textNamePosX.getText()) / Shutter.imageRatio), (int) Math.round(Integer.valueOf(Shutter.textNamePosY.getText()) / Shutter.imageRatio));
					Shutter.fileLocX = Shutter.fileName.getLocation().x;
					Shutter.fileLocY = Shutter.fileName.getLocation().y;
				}
				
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
				
				//grpWatermark
				if (Shutter.caseAddWatermark.isSelected())
				{
					loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
					Shutter.logo.setLocation((int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosX.getText()) / Shutter.imageRatio), (int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosY.getText()) / Shutter.imageRatio));
					//Saving location
					Shutter.logoLocX = Shutter.logo.getLocation().x;
					Shutter.logoLocY = Shutter.logo.getLocation().y;
				}
				
				Shutter.timecode.repaint();
				Shutter.fileName.repaint();
				Shutter.selection.repaint();
				Shutter.overImage.repaint();
				
				} catch (Exception e) {
				} finally {
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		t.start();	*/
	}

}
