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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;
import library.FFPROBE;
import settings.InputAndOutput;

public class VideoPlayer {
	
	public static JFrame frame = new JFrame();
	JLabel title = new JLabel(Shutter.language.getProperty("frameLecteurVideo"));
	ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
	
	private JLabel quit;
	private JLabel fullscreen;
	private JLabel reduce;
	private JPanel topPanel;
	private JLabel topImage;
	private JLabel bottomImage;
			
	public static  JPanel grpOut;
	public static  JTextField caseOutH;
	public static  JTextField caseOutM;
	public static  JTextField caseOutS;
	public static  JTextField caseOutF;

	private static JPanel grpIn;
	public static JTextField caseInH;
	public static JTextField caseInM;
	public static JTextField caseInS;
	public static JTextField caseInF;

	public static JSlider sliderIn;
	public static JSlider sliderOut;
	
	private JButton btnCaptureIn;
	private JLabel btnPreview;
	private JButton btnApply;
	public static JButton leftPrevious;
	public static JButton leftNext;
	private JButton rightPrevious;
	private JButton rightNext;
	public static JRadioButton casePlaySound;
	public static JRadioButton caseTcInterne;

	public static String videoPath = null;
    public static float ratio = 1.777777f;
    public static float inputFramerate = 25f;
    public static float inputFramerateMS = 40.0f;
    
	public static JPanel playerLeft; 
    public static Process playerLeftVideo;
    public static Process playerLeftAudio;
    public static Thread playerLeftThread;
    public static Thread setTimeLeft;
    public static float playerLeftTime = 0;
    public static Image frameLeft;
    public static boolean playerLeftLoop = false;
    public static boolean leftFrameIsComplete = false;
    public static boolean playerLeftPlayVideo = true;
	
	public static JPanel playerRight;
    public static Process playerRightVideo;
    public static Process playerRightAudio;
    public static Thread playerRightThread;
    public static Thread setTimeRight;
    public static float playerRightTime = 0;
    public static Image frameRight;
    public static boolean playerRightLoop = false;
    public static boolean rightFrameIsComplete = false;
    public static boolean playerRightPlayVideo = true;
    
    public static JButton leftStop;
    public static JButton leftPlay;
    private static JButton rightStop;
    public static JButton rightPlay;
    private static JLabel lblVideo;
    
    public static boolean playerHasBeenStopped = false;
    private static boolean drag;
	public static boolean sliderInChange = false;
	static boolean sliderOutChange = false;
	private JLabel lblVolume;
	public static JSlider sliderVolume;
	public static JLabel lblDuree;
	private static JLabel lblMode;
	public static JComboBox<Object> comboMode = new JComboBox<Object>(new String [] {Shutter.language.getProperty("cutUpper"), Shutter.language.getProperty("removeMode")});
	private static JLabel lblSpeed;
	private static JSlider sliderSpeed;
	private static boolean showInfoMessage = true;
	
	//Temps final
	public static float offset = 0;
	public static int dureeHeures = 0;
	public static int dureeMinutes = 0;
	public static int dureeSecondes = 0;
	public static int dureeImages = 0;
		
	//Avance et recul d'une image
	static boolean frameLeftControl = false;
	static boolean frameRightControl = false;
	static boolean seekOnKeyFrames = false;
	
	//Waveform
	public static boolean addWaveformIsRunning = false;
	public static File waveform = new File(Shutter.dirTemp + "waveform.png");
	public static JLabel waveformLeft;
	public static JLabel waveformRight;
	public static JPanel panelWaveformLeft;
	private static JPanel panelWaveformRight;
	
	private static int MousePositionX;
	private static int MousePositionY;
		
	public VideoPlayer() {  	
		
		showInfoMessage = true;
		  
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		frame.getContentPane().setLayout(null);
		frame.setVisible(false);
		frame.setSize(1000, 640);
		frame.setTitle(Shutter.language.getProperty("frameLecteurVideo"));
		frame.setForeground(Color.WHITE);
		
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
    		frame.setUndecorated(true);
    		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);	
    		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
            shape1.add(shape2);
    		frame.setShape(shape1);
    		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
        	
		}
		
		topPanel();
		
		//Ces deux boutons définissent la postion des autres objets par la suite ils doivent donc être appelés en amont
    	btnCaptureIn = new JButton(Shutter.language.getProperty("btnCaptureIn"));
    	btnCaptureIn.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnCaptureIn.setMargin(new Insets(0,0,0,0));
		btnCaptureIn.setBounds(8, topPanel.getSize().height + 10, 130, 21);		
		frame.getContentPane().add(btnCaptureIn);
		
		btnCaptureIn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {							
	            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					File file = new File (Shutter.liste.firstElement());
					String ext = file.toString().substring(file.toString().lastIndexOf("."));
					InputAndOutput.getInputAndOutput();
					
					 //Dossier de sortie
					String sortie;					
					if (Shutter.caseChangeFolder1.isSelected())
						sortie = Shutter.lblDestination1.getText();
					else
					{
						sortie =  file.getParent();
						Shutter.lblDestination1.setText(sortie);
					}
					
					//Fichier de sortie
					String newExtension = ".jpg";							
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
						newExtension = Shutter.comboFilter.getSelectedItem().toString();
					
					File fileOut = new File(sortie + "/" + file.getName().replace(ext,  newExtension)); 	
					if(fileOut.exists())
					{
						int n = 1;
						do {							
							fileOut = new File(sortie + "/" + file.getName().replace(ext, "_" + n + newExtension));	
							n++;
						} while (fileOut.exists());
					}
					
					// Analyse des données
					FFPROBE.FrameData(file.toString());	
					do
						Thread.sleep(10);						 
					while (FFPROBE.isRunning);
					
					String filter = "";	
					if (FFPROBE.entrelaced.equals("1"))
						filter = " -filter:v yadif=0:" + FFPROBE.fieldOrder + ":0";		

					if (sliderOut.getValue() != sliderOut.getMaximum())
					{
						int frameIn = (int) (Integer.parseInt(caseInF.getText()) * inputFramerateMS);
						int frameOut = (int) (Integer.parseInt(caseOutF.getText()) * inputFramerateMS);	
						NumberFormat formatFrame = new DecimalFormat("000");
						
						FFMPEG.run( 
						" -ss " + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + "." + formatFrame.format(frameIn) + " -i " + '"' + file.toString() + '"' +
						" -ss " + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + "." + formatFrame.format(frameOut) + " -i " + '"' + file.toString() + '"' + 
					    filter + " -vframes 1 -q:v 0 -an -filter_complex hstack -y " + '"'  + fileOut + '"');	
					}
					else
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + filter + " -vframes 1 -q:v 0 -an -y " + '"'  + fileOut + '"');			
					
					do{
						Thread.sleep(10);
					} while(FFMPEG.isRunning);
					
					FFMPEG.enableAll();
					Shutter.caseRunInBackground.setEnabled(false);	
					Shutter.caseRunInBackground.setSelected(false);
					Shutter.btnCancel.setEnabled(false);
					Shutter.tempsRestant.setVisible(false);
					Shutter.progressBar1.setValue(0);
	            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				} catch (InterruptedException e1) {}	
				
				if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
				
			}        			
		});
		
		btnApply = new JButton(Shutter.language.getProperty("btnApply"));
		btnApply.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnApply.setMargin(new Insets(0,0,0,0));
		btnApply.setBounds(frame.getSize().width - 6 - 130 - 4, topPanel.getSize().height + 10, 130, 21);		
		frame.getContentPane().add(btnApply);
		
		btnApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
	    		{
					Shutter.caseInAndOut.setSelected(false);
	    			SubtitlesTimeline.frame.dispose();
	    		}
				else
					Utils.changeFrameVisibility(frame, true);
			}			
			
		});
		        		
  		lblVideo = new JLabel();    
  		lblVideo.setVisible(false);
		lblVideo.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblVideo.setForeground(Utils.themeColor);
		lblVideo.setHorizontalAlignment(SwingConstants.CENTER);
		lblVideo.setBounds(btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6, topPanel.getSize().height + 12, frame.getSize().width - (btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6 + btnApply.getSize().width + 12), 16);        		
		frame.getContentPane().add(lblVideo);
						
		players();        		
		buttons();
		grpIn();
		grpOut();
		sliders();		
		setMedia();	
				
		if (FFPROBE.audioOnly || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			btnCaptureIn.setEnabled(false);
			caseTcInterne.setVisible(false);
		}
		else
		{
			btnCaptureIn.setEnabled(true);
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
				caseTcInterne.setVisible(true);
		}
		
		lblDuree = new JLabel();
		lblDuree.setHorizontalAlignment(SwingConstants.CENTER);
		lblDuree.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblDuree.setForeground(Utils.themeColor);
		lblDuree.setBounds(0, frame.getSize().height - 16 - 12, frame.getWidth(), 16);   		
		frame.getContentPane().add(lblDuree);
		        		        		
		totalDuration();
				
		frame.addMouseMotionListener(new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (frame.getSize().width >= 636 &&  frame.getSize().height >= 619 && e.getX() >= 636 && e.getY() >= 619 && drag)
				{
					frame.setSize(e.getX() + 20, e.getY() + 20);
					
					resizeAll();        					
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x > frame.getSize().width - 20 || MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y > frame.getSize().height - 20)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				 else 
				{
					if (drag == false)
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
				{
					drag = true;
					
					if (waveform.exists())
					{
						if (waveformLeft != null && FFPROBE.audioOnly == false)
							waveformLeft.setVisible(false);
						
						if (waveformRight != null && FFPROBE.audioOnly == false)
							waveformRight.setVisible(false);
						
						sliderIn.setVisible(true);
						
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
							sliderOut.setVisible(true);
					}
															
    				if (playerLeftVideo != null)
    				{
    					leftPlay.setText(Shutter.language.getProperty("btnResume"));
    					playerLeftStop();
    				}
					
    				if (playerRightVideo != null)
    				{
    					rightPlay.setText(Shutter.language.getProperty("btnResume"));
    					playerRightStop();
    				}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {	

				if (drag)
				{				
					if (frame.getSize().width < 1000 &&  frame.getSize().height < 640)
					{
						frame.setSize(1000, 640);	   
					}			
				}

				drag = false;
				
				resizeAll();	
												
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
		        			
		frame.addWindowListener(new WindowListener(){
			
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {										
				frame.toFront();
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
		
    	frame.addComponentListener(new ComponentAdapter() {
    		
            public void componentResized(ComponentEvent e) {
            	
            	if (drag == false)
            	{					
					resizeAll();					
            	}
            }
        });
				
		KeyListener keyListener = new KeyListener(){

			@Override	
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				//Volume up
				if (e.getKeyCode() == 107)
					sliderVolume.setValue(sliderVolume.getValue() + 2);
					
				//Volume down
				if (e.getKeyCode() == 109)
					sliderVolume.setValue(sliderVolume.getValue() - 2);
				
				
				if (e.getKeyCode() == KeyEvent.VK_K)
					leftPlay.doClick();

				if (e.getKeyCode() == KeyEvent.VK_J)
				{
					playerLeftSetTime((float) (VideoPlayer.playerLeftTime - ((1000 /FFPROBE.currentFPS) * 11)));
  				}
					
				if (e.getKeyCode() == KeyEvent.VK_L)
				{
					playerLeftSetTime((float) (VideoPlayer.playerLeftTime + ((1000 /FFPROBE.currentFPS) * 9)));
				}
				
				if (playerLeftVideo != null)
				{
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
	            		leftPrevious.doClick();	
					if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						leftNext.doClick();
				}
				if (playerRightVideo != null && sliderOut.getValue() < sliderOut.getMaximum())
				{
					if (e.getKeyCode() == KeyEvent.VK_UP)
						rightNext.doClick();	
					if (e.getKeyCode() == KeyEvent.VK_DOWN)
						rightPrevious.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {							
			}
			
		};
		
		for(Component component : frame.getContentPane().getComponents())
		{
			component.addKeyListener(keyListener);
		}
		
    	frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e2)
		    {
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
		    	frame.setShape(shape1);
		    }
 		});
			
		if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
			leftPlay.doClick();
    	
		resizeAll();
				
		Utils.changeFrameVisibility(frame, false);
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			File video = new File(Shutter.liste.firstElement());
			String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
			
			SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
			
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		frame.setLocation(frame.getLocation().x , dim.height/3 - frame.getHeight()/2);
    		    		    		
    		if (SubtitlesTimeline.frame == null)    		
    			new SubtitlesTimeline(dim.width/2-500,frame.getLocation().y + frame.getHeight() + 7);
    		else
    		{        		
    			SubtitlesTimeline.frame.setVisible(true);
    			SubtitlesTimeline.subtitlesNumber();
    		}    	
    					    		
			sliderVolume.setValue(sliderVolume.getMaximum());
		}
	}
	
	
	//Player left
	public static void playerLeft(float inputTime) {
		
		try {			
						
			String PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();			
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe" + '"';	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder(PathToFFMPEG + setVideoCommand(inputTime, playerLeft.getWidth(), playerLeft.getHeight(), playerLeftPlayVideo));
				playerLeftVideo = pbv.start();	
				
				//AUDIO STREAM
				ProcessBuilder pba = new ProcessBuilder(PathToFFMPEG + setAudioCommand(inputTime));
				playerLeftAudio = pba.start();
			}
			else
			{
				PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setVideoCommand(inputTime, playerLeft.getWidth(), playerLeft.getHeight(), playerLeftPlayVideo));
				playerLeftVideo = pbv.start();	
				
				//AUDIO STREAM
				ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setAudioCommand(inputTime));	
				playerLeftAudio = pba.start();
			}			
				
			InputStream video = playerLeftVideo.getInputStream();				
			BufferedInputStream videoInputStream = new BufferedInputStream(video);
					
			InputStream audio = playerLeftAudio.getInputStream();							
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);		    
		    AudioFormat audioFormat = audioInputStream.getFormat();
	        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
	        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
	        
            line.open(audioFormat);
	        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            line.start();	
				        							
			playerLeftThread = new Thread(new Runnable() {

				@Override
				public void run() {

					byte bytes[] = new byte[(int) (FFPROBE.audioSampleRate*4/inputFramerate)];
		            int bytesRead = 0;

					do {	
						
						long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);
						
						if (playerLeftLoop)
						{							
							try {	
								
								//Audio volume	
						        double gain = (double) sliderVolume.getValue() / 100;   
						        if (casePlaySound.isSelected() == false && (sliderInChange || frameLeftControl || drag))
						        	gain = 0.0/100;
						        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
						        gainControl.setValue(dB);
						        
								///Read 1 audio frame
								bytesRead = audioInputStream.read(bytes, 0, bytes.length);
				        		line.write(bytes, 0, bytesRead);
												    
				        		//Read 1 video frame
								frameLeft = ImageIO.read(videoInputStream);
								playerLeftRepaint();
													
								if (sliderSpeed.getValue() != 2)
								{													
									if (sliderSpeed.getValue() != 0)
									{
										playerLeftTime += inputFramerateMS * sliderSpeed.getValue() / 2;
									}
									else
										playerLeftTime += inputFramerateMS * 0.25f;
								}
								else
									playerLeftTime += inputFramerateMS;
																								
							} catch (Exception e) {
								e.printStackTrace();
							}
							finally {
								
								if (frameLeftControl)
								{
									playerLeftLoop = false;
								}
								else if (playerLeftPlayVideo)
								{
					            	long delay = startTime - System.nanoTime();
					                
					            	if (delay > 0)
					            	{		            		
						            	long time = System.nanoTime();
						            	do {
							            	try {
												Thread.sleep(0);
											} catch (InterruptedException e) {}
						            	} while (System.nanoTime() - time < delay);			            	
					                }
								}								
								
								leftFrameIsComplete = true;						
							}
						}	 
						else
						{
							line.flush();	
														
							//IMPORTANT reduce CPU usage
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						}							
						
					} while (playerLeftVideo.isAlive());
										
					try {
						video.close();
					} catch (IOException e) {}		
					try {
						videoInputStream.close();
					} catch (IOException e) {}
					try {
						audio.close();
					} catch (IOException e) {}
					try {
						audioInputStream.close();
					} catch (IOException e) {}
					line.close();	
				}
				
			});
			playerLeftThread.setPriority(Thread.MAX_PRIORITY);
			playerLeftThread.start();	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
			
	public static void playerLeftPlay() {
		
		if (playerLeftVideo == null || playerLeftVideo.isAlive() == false)		
		{		
			playerLeft(playerLeftTime);							
		}		
	}
	
	public static void playerLeftStop() {
				
		playerLeftLoop = false;
		
		try {
			Thread.sleep((long) inputFramerateMS);
		} catch (InterruptedException e1) {}
		
		if (playerLeftVideo != null)
		{
			playerLeftVideo.destroy();
			playerLeftThread.interrupt();
		}
		
		if (playerLeftAudio != null)
			playerLeftAudio.destroy();		
	}

	public static void playerLeftRepaint() {
				
		if (frameLeft != null)
		{
			playerLeft.repaint();
			frame.getContentPane().repaint();	
		}
		
		getTimeInPoint(playerLeftTime); 
	}
	
	public static boolean playerLeftIsPlaying() {
		
		if (playerLeftVideo != null && playerLeftVideo.isAlive() && leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerLeftSetTime(float time) {
		
		if (setTimeLeft == null || setTimeLeft.isAlive() == false)
		{
			setTimeLeft = new Thread(new Runnable() {

				@Override
				public void run() {					

					float t = time;
					
					if (t < 0.0f)
						t = 0;
					
					playerLeftPlayVideo = false;
					
					if (frameLeft != null)
					{
						if (playerLeftIsPlaying())
						{				
							playerLeftStop();
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLeftThread.isAlive());
											
							playerLeftPlayVideo = true;
							playerLeft(t);	
							playerLeftLoop = true;
						}
						else
						{						
							playerLeftStop();
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLeftThread.isAlive());				
							
							playerLeft(t);
							
							playerLeftLoop = true;
							
							do {
								try {
									Thread.sleep(4);
								} catch (InterruptedException e) {}
							} while (frameLeft == null);
							
							playerLeftLoop = false;
						}
						
						playerLeftTime = t;
					}
					
					frameLeftControl = false;
					playerLeftPlayVideo = true;		
				}
				
			});
			setTimeLeft.start();
		}	
	}
		
	public static void playerLeftFreeze() {
		
		frameLeftControl = true;
		playerLeftPlayVideo = false;
		
		if (playerLeftVideo == null || playerLeftVideo.isAlive() == false)		
		{
			playerLeftLoop = true;
			
			if (playerLeftTime > 0)
				playerLeftTime -= 1 * inputFramerateMS;					

			playerLeft(playerLeftTime);	
			
			do {
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {}
			} while (frameLeft == null);
			
			playerLeftLoop = false;
		}
		
		frameLeftControl = false;	
		playerLeftPlayVideo = true;
	}
	
	//Player right
	public static void playerRight(float inputTime) {
		
		try {
			
			String PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe" + '"';	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder(PathToFFMPEG + setVideoCommand(inputTime, playerRight.getWidth(), playerRight.getHeight(), playerRightPlayVideo));
				playerRightVideo = pbv.start();	
				
				//AUDIO STREAM
				ProcessBuilder pba = new ProcessBuilder(PathToFFMPEG + setAudioCommand(inputTime));
				playerRightAudio = pba.start();
			}
			else
			{
				PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setVideoCommand(inputTime, playerRight.getWidth(), playerRight.getHeight(), playerRightPlayVideo));
				playerRightVideo = pbv.start();	
				
				//AUDIO STREAM
				ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setAudioCommand(inputTime));	
				playerRightAudio = pba.start();
			}	
									
			InputStream video = playerRightVideo.getInputStream();				
			BufferedInputStream videoInputStream = new BufferedInputStream(video);
						
			InputStream audio = playerRightAudio.getInputStream();							
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);	         
	        AudioFormat audioFormat = audioInputStream.getFormat();
	        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
	        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(audioFormat);
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            line.start();	
			
            playerRightThread = new Thread(new Runnable() {

				@Override
				public void run() {
		    		           					
					byte bytes[] = new byte[(int) (FFPROBE.audioSampleRate*4/inputFramerate)];
		            int bytesRead = 0;
		            					
					do {
						
						long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);
						
						if (playerRightLoop)
						{
							try {	

								//Audio volume	
						        double gain = (double) sliderVolume.getValue() / 100;  
						        if (casePlaySound.isSelected() == false && (sliderOutChange || frameRightControl || drag))
						        	gain = 0.0/100;
						        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
						        gainControl.setValue(dB);
						        
								///Read 1 audio frame
								bytesRead = audioInputStream.read(bytes, 0, bytes.length);
				        		line.write(bytes, 0, bytesRead);								
					        	
				        		//Read 1 video frame				        		
								frameRight = ImageIO.read(videoInputStream);
								playerRightRepaint();
					
								if (sliderSpeed.getValue() != 2)
								{
									if (sliderSpeed.getValue() != 0)
									{
										playerRightTime += inputFramerateMS * sliderSpeed.getValue() / 2;
									}
									else
										playerRightTime += inputFramerateMS * 0.25f;
								}
								else
									playerRightTime += inputFramerateMS;
																								
							} catch (Exception e) {
								e.printStackTrace();
							}	 
							finally {	
								
								if (frameRightControl)
								{
									playerRightLoop = false;
								}
								else if (playerRightPlayVideo)
								{
					            	long delay = startTime - System.nanoTime();
					                
					            	if (delay > 0 && playerRightPlayVideo)
					            	{		            		
						            	long time = System.nanoTime();
						            	do {
							            	try {
												Thread.sleep(0);
											} catch (InterruptedException e) {}
						            	} while (System.nanoTime() - time < delay);			            	
					                }
								}
								
								rightFrameIsComplete = true;
							}
						}
						else
						{
							line.flush();	
							
							//IMPORTANT reduce CPU usage
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						}	
						
					} while (playerRightVideo.isAlive());
					
					try {
						video.close();
					} catch (IOException e) {}
					try {
						videoInputStream.close();
					} catch (IOException e) {}
					try {
						audio.close();
					} catch (IOException e) {}
					try {
						audioInputStream.close();
					} catch (IOException e) {}
					line.close();
				}
				
			});
            playerRightThread.setPriority(Thread.MAX_PRIORITY);
            playerRightThread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void playerRightPlay() {
		
		if (playerRightVideo == null || playerRightVideo.isAlive() == false)		
		{
			playerRight(playerRightTime);							
		}
	}

	public static void playerRightStop() {
						
		playerRightLoop = false;
		
		try {
			Thread.sleep((long) inputFramerateMS);
		} catch (InterruptedException e1) {}
					
		if (playerRightVideo != null)
		{
			playerRightVideo.destroy();
			playerRightThread.interrupt();
		}
		
		if (playerRightAudio != null)
			playerRightAudio.destroy();
				
	}

	public static void playerRightRepaint() {
					
		if (frameRight != null)	
		{
			playerRight.repaint();
			frame.getContentPane().repaint();			
		}
				
		getTimeOutPoint(playerRightTime); 
	}
	
	public static boolean playerRightIsPlaying() {
		
		if (playerRightVideo != null && playerRightVideo.isAlive() && rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerRightSetTime(float time) {
		
		if (setTimeRight == null || setTimeRight.isAlive() == false)
		{
			setTimeRight = new Thread(new Runnable() {

				@Override
				public void run() {	

					playerRightPlayVideo = false;
							
					if (frameRight != null && time < (FFPROBE.totalLength - inputFramerateMS*2))
					{
						if (playerRightIsPlaying())
						{				
							playerRightStop();
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerRightThread.isAlive());
											
							playerRightPlayVideo = true;
							playerRight(time);	
							playerRightLoop = true;
						}
						else
						{
							playerRightStop();
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerRightThread.isAlive());
							
							playerRight(time);
							
							//Allow to read 1 frame
							playerRightLoop = true;
							
							do {
								try {
									Thread.sleep(4);
								} catch (InterruptedException e) {}
							} while (frameRight == null);
							
							playerRightLoop = false;
						}
						
						playerRightTime = time;
					}
					
					frameRightControl = false;
					playerRightPlayVideo = true;
				}
			});
			setTimeRight.start();
		}
	}
				
	public static void playerRightFreeze() {
				
		frameRightControl = true;
		playerRightPlayVideo = false;
		
		if (playerRightVideo == null || playerRightVideo.isAlive() == false)		
		{
			playerRightLoop = true;
			
			playerRightTime -= 1 * inputFramerateMS;
			
			playerRight(playerRightTime);	
			
			do {
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {}
			} while (frameRight == null);
			
			playerRightLoop = false;
		}	
		
		frameRightControl = false;
		playerRightPlayVideo = true;
	}
	
	
    public static void setMedia() {
    		    	
    	if (Shutter.caseInAndOut.isSelected())
    	{
	    	//Mise à jour du fichier vidéo
			if (Shutter.liste.getSize() != 0)
			{
				videoPath = Shutter.liste.firstElement();
			
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
				if (frame.isVisible() 
				&& lblVideo.getText().equals(new File(videoPath).getName()) == false
				&& panelWaveformLeft.isVisible() && panelWaveformRight.isVisible()
				&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{
					waveformLeft.setVisible(false);
					waveformRight.setVisible(false);
					sliderIn.setVisible(true);
					sliderOut.setVisible(true);		
					
					if (waveform.exists())
						waveform.delete();
					
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
					
					leftStop.doClick();	
				}
								
				if (lblVideo.isVisible() == false || lblVideo.getText().equals(new File(videoPath).getName()) == false)
				{
					if (FFPROBE.isRunning == false)
					{
						FFPROBE.Data(videoPath);
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
						
						FFPROBE.FrameData(videoPath);	
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
						
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
					}
					
					inputFramerate = FFPROBE.currentFPS;
					inputFramerateMS = (float) 1000 / inputFramerate;					
					playerLeftTime = 0;
					playerRightTime = FFPROBE.totalLength;
					
					caseTcInterne.setEnabled(true);	
					
					//Injection du temps 
					long temps = FFPROBE.totalLength;
					NumberFormat formatter = new DecimalFormat("00");
			        caseOutH.setText(formatter.format((temps/1000) / 3600));
			        caseOutM.setText(formatter.format( ((temps/1000) / 60) % 60) );
			        caseOutS.setText(formatter.format((temps/1000) % 60));				        
			        caseOutF.setText(formatter.format((int) (temps / inputFramerateMS % FFPROBE.currentFPS)));
			        
					//On définit les valeurs des sliders
					sliderIn.setMaximum((int) FFPROBE.totalLength);
					sliderOut.setMaximum((int) FFPROBE.totalLength);
					sliderOut.setValue(sliderOut.getMaximum());
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						SubtitlesTimeline.timelineScrollBar.setMaximum(sliderIn.getMaximum());
					
					lblVideo.setText(new File(videoPath).getName());
					lblVideo.setVisible(true);
					
					leftPlay.setEnabled(true);
					leftPrevious.setEnabled(true);
					leftNext.setEnabled(true);
					leftStop.setEnabled(true);
					
					leftStop.doClick();
				}			
			}
			else
			{				
				leftStop.doClick();
				
				videoPath = null;
				lblVideo.setVisible(false);
				playerLeftStop();
				sliderIn.setValue(0);
				
				playerRightStop();
				sliderOut.setValue(sliderOut.getMaximum());

				leftPlay.setText(Shutter.language.getProperty("btnPlay"));
				rightPlay.setText(Shutter.language.getProperty("btnResume"));		
				
				leftPlay.setEnabled(false);
				leftPrevious.setEnabled(false);
				leftNext.setEnabled(false);
				leftStop.setEnabled(false);
				
				caseTcInterne.setEnabled(false);	
				caseTcInterne.setSelected(false);
				
			}
			
			Thread sliders = new Thread(new Runnable()
			{
				@Override
				public void run() {
					
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (frame.isVisible() == false);
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
					{
						grpOut.setVisible(false);
						sliderOut.setVisible(false);
					}
					else
					{
						grpOut.setVisible(true);
						if (sliderIn.isVisible())
							sliderOut.setVisible(true);
						else
							sliderOut.setVisible(false);
					}	
				}				
			});
			sliders.start();			
    	}
	}
 
	public static String setVideoCommand(float inputTime, int width, int height, boolean isPlaying) {
				
		String yadif = "";
		if (FFPROBE.entrelaced.equals("1"))
			yadif = " -vf yadif=0:" + FFPROBE.fieldOrder + ":0";
		
		String speed = "";
		if (sliderSpeed.getValue() != 2)
		{
			if (yadif != "")
				speed = ",";
			else
				speed = " -vf ";
			
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
			
			return " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -v quiet -f lavfi -i " + '"' + "color=c=black:r=25:s="
					+ width + "x" + height + '"' + speed + " -c:v bmp -an -f image2pipe pipe:-";		
		}
		else
		{
			return " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -v quiet -ss " + (long) inputTime + "ms -i " + '"' + videoPath + '"'
					+  " -c:v bmp -an -s " + width + "x" + height + yadif + speed + " -sws_flags fast_bilinear -f image2pipe pipe:-";			
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
				
		if (FFPROBE.hasAudio == false)
		{
			return " -v quiet -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"' + speed +  " -vn -c:a pcm_s16le -ar 48k -ac 2 -f wav pipe:-";				
		}
		else
		{
			return " -v quiet -ss " + (long) inputTime + "ms -i " + '"' + videoPath + '"' + speed + " -vn -c:a pcm_s16le -ac 2 -f wav pipe:-";
		}
	}
    
	public static void addWaveform(boolean newWaveform) {
					
	addWaveformIsRunning = true;
		
	Thread addWaveform = new Thread(new Runnable()
	{
		@Override
		public void run() {
						
			if (FFMPEG.isRunning || playerLeftVideo == null && frame.isVisible() == false)
			{						
				do  {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				} while (FFMPEG.isRunning && playerLeftVideo == null && frame.isVisible() == false);
			}
			
			if (FFMPEG.isRunning == false)
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
							
							long time = (long) (SubtitlesTimeline.setTime(SubtitlesTimeline.timelineScrollBar.getValue()) / SubtitlesTimeline.zoom);
							NumberFormat formatter = new DecimalFormat("00");
							NumberFormat toMs = new DecimalFormat("000");
							String h = formatter.format(time / 3600000);
							String m = formatter.format((time / 60000) % 60);
							String s = formatter.format((time / 1000) % 60);    		
							String f = toMs.format(time % 1000);
							
							start = " -ss " + h + ":" + m + ":" + s + "." + f;
							duration = "atrim=duration=" + (SubtitlesTimeline.frame.getWidth() * 2) / 100 + ",";								
							size = (long) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom);
						}
					
						//IMPORTANT
						if (size > 549944)
							size = 549944;
							
						FFMPEG.run(start + " -i " + '"' + Shutter.liste.firstElement() + '"'
						+ " -filter:a aresample=8000 -filter_complex " + '"' + "[0:a]" + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green" + '"' 
						+ " -pix_fmt rgba -vn -frames:v 1 -y " + '"' + waveform + '"');  									
	
						Shutter.enableAll();							
						
						if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
							Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
						else
							Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
						
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}									
						} while (waveform.exists() == false || FFMPEG.isRunning);					
					}
	
					if (Shutter.caseInAndOut.isSelected()) //Ne charge plus l'image si la fenêtre est fermée entre temps
					{
						try {		
							
							float largeurMax = (float) frame.getSize().width / frame.getSize().height;
							int largeur = (int) ((float) frame.getSize().width / largeurMax);
														
							Image imageBMP = ImageIO.read(waveform);
							ImageIcon resizedWaveform;
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							{
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance((int) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));
							}
							else if (sliderOut.getValue() == sliderOut.getMaximum())
							{
								if (FFPROBE.audioOnly)
									resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / ratio), Image.SCALE_AREA_AVERAGING));	
								else
									resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
							}
							else
							{
								if (FFPROBE.audioOnly)
									resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(frame.getSize().width / 2 - 6, (int) ((float) (frame.getSize().width / 2 - 6) / ratio), Image.SCALE_AREA_AVERAGING));			
								else
									resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
							}
							
							//On attends la creation des waveforms et listeners
							if (waveformLeft == null || waveformRight == null)
							{
								do  {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {}
								} while (waveformLeft == null || waveformRight == null);
							}
		
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							{							
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.setTime(SubtitlesTimeline.timelineScrollBar.getValue()), SubtitlesTimeline.waveform.getY(), (int) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
							}
							else
							{									
								waveformLeft.setIcon(resizedWaveform);
								waveformRight.setIcon(resizedWaveform);	
							}
							
							if (FFPROBE.audioOnly)
							{
								waveformLeft.setBounds(playerLeft.getBounds());
								waveformRight.setBounds(playerRight.getBounds());
							}
							else
							{
								waveformLeft.setSize(sliderIn.getWidth(), grpIn.getHeight());	
								waveformLeft.setLocation(sliderIn.getLocation());
								waveformRight.setSize(sliderOut.getWidth(), grpIn.getHeight());	
								waveformRight.setLocation(sliderOut.getLocation());
							}		
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
							{
								waveformLeft.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));							
								waveformRight.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));
							}
							
							panelWaveformLeft.setBounds((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0, 2, waveformLeft.getSize().height);
							
							if (sliderOut.getValue() != sliderOut.getMaximum() && panelWaveformRight != null)
							{
								panelWaveformRight.setBounds((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0, 2, waveformRight.getSize().height);
							}
							else if (panelWaveformRight != null)
							{
								panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
								sliderOut.setValue(sliderOut.getMaximum());						
							}
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && FFPROBE.audioOnly == false)
							{
								sliderIn.setVisible(false);
								sliderOut.setVisible(false);
							}
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
							{
								waveformLeft.setVisible(true);
								
								if ((FFPROBE.audioOnly == false || FFPROBE.audioOnly && sliderOut.getValue() != sliderOut.getMaximum()) && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
									waveformRight.setVisible(true);
								else
									waveformRight.setVisible(false);
							}				
							
						} catch (Exception e) {
							System.out.println(e);
						}
						finally {
							if (panelWaveformLeft != null)
								panelWaveformLeft.repaint();		
							if (panelWaveformRight != null)
								panelWaveformRight.repaint();
							
							addWaveformIsRunning = false;
							
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			}
		
			}
			
		});
		addWaveform.start();
	}
	
    private void buttons() {		 
    	
		rightPrevious = new JButton("<");
		rightPrevious.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		rightPrevious.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 - 21 - 4, playerRight.getLocation().y + playerRight.getSize().height + 10, 22, 21);		
		rightPrevious.setVisible(false);
		frame.getContentPane().add(rightPrevious);
		
		rightPrevious.addActionListener(new ActionListener(){
			
			int i = 0;
			
			@Override
			public void actionPerformed(ActionEvent e) {

				i ++;
				
				if (frameRight != null && i <= 1)
				{										
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							
							if (sliderSpeed.getValue() != 2)
							{
								sliderSpeed.setValue(2);
								lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
								lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);								
								playerRightSetTime(sliderOut.getValue() - inputFramerateMS);
							}
							
							if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
								leftPlay.doClick();

							frameRightControl = true;				
							
							if (playerRightVideo != null && frameRight != null)
							{
								if (playerRightLoop)
								{
									rightPlay.setText(Shutter.language.getProperty("btnResume"));
									playerRightLoop = false;
								}
		
								rightFrameIsComplete = false;

								playerRightSetTime(playerRightTime - (inputFramerateMS * 2));		
								
								long time = System.currentTimeMillis();
								
								do {									
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (System.currentTimeMillis() - time > 1000)
										rightFrameIsComplete = true;
									
								} while (rightFrameIsComplete == false);
								
								getTimeOutPoint(playerRightTime - inputFramerateMS);
							}			
							
							i = 0;
						}
						
					});
					t.start();
				}
			}			
		});
		
		rightNext = new JButton(">");
		rightNext.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		rightNext.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 + 4, rightPrevious.getLocation().y, 22, 21);	
		rightNext.setVisible(false);
		frame.getContentPane().add(rightNext);
		
		rightNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (sliderSpeed.getValue() != 2)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
					playerRightSetTime(sliderOut.getValue() + inputFramerateMS);
				}
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					leftPlay.doClick();

				frameRightControl = true;
				
				if (playerRightVideo != null)
				{				
					if (playerRightLoop)
					{
						rightPlay.setText(Shutter.language.getProperty("btnResume"));
						playerRightLoop = false;
					}

					//Allow to read 1 frame	
					playerRightLoop = true;
				}				
			}
			
		});
		
		rightPlay = new JButton(Shutter.language.getProperty("btnResume"));
		rightPlay.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		rightPlay.setMargin(new Insets(0,0,0,0));
		rightPlay.setBounds(rightPrevious.getLocation().x - 80 - 4, rightPrevious.getLocation().y, 80, 21);		
		rightPlay.setVisible(false);
		frame.getContentPane().add(rightPlay);
		
		rightPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {	
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					leftPlay.doClick();
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					rightPlay.setText(Shutter.language.getProperty("btnResume"));
					playerRightLoop = false;
					
					if (sliderSpeed.getValue() != 2 && sliderOut.getValue() != sliderOut.getMaximum())
					{
						playerRightSetTime(formatTime(sliderOut.getValue()));	
					}	
				}
				else if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")) && playerRightTime < (FFPROBE.totalLength - inputFramerateMS*2))
				{
					frameRightControl = false;
					rightPlay.setText(Shutter.language.getProperty("btnPause"));
					playerRightLoop = true;
				}
			}
			
		});
		
		rightStop = new JButton(Shutter.language.getProperty("btnCancel"));
		rightStop.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		rightStop.setMargin(new Insets(0,0,0,0));
		rightStop.setBounds(rightNext.getLocation().x + rightNext.getSize().width + 4, rightNext.getLocation().y, 80, 21);	
		rightStop.setVisible(false);
		frame.getContentPane().add(rightStop);		
		
		rightStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (playerRightVideo != null)
				{
					if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					{
						rightPlay.setText(Shutter.language.getProperty("btnResume"));
						playerRightLoop = false;				
					}
									
					if (panelWaveformRight != null)
						panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
					
					sliderOut.setValue(sliderOut.getMaximum());
				}
				rightPlay.setText(Shutter.language.getProperty("btnResume"));
				playerRightLoop = false;
									
				playerRightTime = FFPROBE.totalLength;
				
				resizeAll();		
				
				if (playerRightVideo != null)
				{
					playerRightStop();
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException er) {}
					} while (playerRightThread.isAlive());
				}
				
				playerLeftSetTime(playerLeftTime - inputFramerateMS);
				
				playerRight.setVisible(false);
				playerRight.setVisible(false);
				if (FFPROBE.audioOnly)
				{
					waveformRight.setVisible(false);
					panelWaveformRight.setVisible(false);
				}
				rightPrevious.setVisible(false);
				rightNext.setVisible(false);
				rightPlay.setVisible(false);
				rightStop.setVisible(false);
				
				//Injection du temps 
				long temps = FFPROBE.totalLength;
				NumberFormat formatter = new DecimalFormat("00");
		        caseOutH.setText(formatter.format((temps/1000) / 3600));
		        caseOutM.setText(formatter.format( ((temps/1000) / 60) % 60) );
		        caseOutS.setText(formatter.format((temps/1000) % 60));				        
		        caseOutF.setText(formatter.format((int) (temps / inputFramerateMS % FFPROBE.currentFPS)));	

				playerRightTime = FFPROBE.totalLength;
			}			
			
		});
 	
		leftPrevious = new JButton("<");
		leftPrevious.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		leftPrevious.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 - 21 - 4, playerLeft.getLocation().y + playerLeft.getSize().height + 10, 22, 21);		
		frame.getContentPane().add(leftPrevious);
				
		leftPrevious.addActionListener(new ActionListener(){
			
			int i = 0;
			
			@Override
			public void actionPerformed(ActionEvent e) {

				i ++;
				
				if (frameLeft != null && i <= 1)
				{										
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
									
							if (sliderSpeed.getValue() != 2)
							{
								sliderSpeed.setValue(2);
								lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
								lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
								playerLeftSetTime(sliderIn.getValue() - inputFramerateMS);
							}
							
							if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
								rightPlay.doClick();
			
							frameLeftControl = true;
							
							if (playerLeftVideo != null && frameLeft != null)
							{										
								if (playerLeftLoop)
								{
									leftPlay.setText(Shutter.language.getProperty("btnResume"));
									playerLeftLoop = false;
								}

								leftFrameIsComplete = false;
								
								if (seekOnKeyFrames && FFPROBE.isRunning == false)
								{				
									FFPROBE.Keyframes(videoPath, playerLeftTime - (inputFramerateMS * 2), false);
									
									do {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {}
									} while (FFPROBE.isRunning);
									
									if (FFPROBE.keyFrame > 0)
									{
										playerLeftSetTime(FFPROBE.keyFrame);
									}
								}
								else
									playerLeftSetTime(playerLeftTime - (inputFramerateMS * 2));
								
								long time = System.currentTimeMillis();
								
								do {
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (System.currentTimeMillis() - time > 1000)
										leftFrameIsComplete = true;
																
								} while (leftFrameIsComplete == false);					
								
								if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
									getTimeInPoint(playerLeftTime - inputFramerateMS);
							}	
							
							i = 0;
						}
						
					});
					t.start();
				}
			}			
		});
		
		leftNext = new JButton(">");
		leftNext.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		leftNext.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 + 4, leftPrevious.getLocation().y, 22, 21);		
		frame.getContentPane().add(leftNext);
		
		leftNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sliderSpeed.getValue() != 2)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
					playerLeftSetTime(sliderIn.getValue() + inputFramerateMS);
				}
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					rightPlay.doClick();
				
				frameLeftControl = true;
				
				if (playerLeftVideo != null)
				{
					if (playerLeftLoop)
					{
						leftPlay.setText(Shutter.language.getProperty("btnResume"));
						playerLeftLoop = false;
					}

					if (seekOnKeyFrames && FFPROBE.isRunning == false)
					{									
						FFPROBE.Keyframes(videoPath, playerLeftTime, true);
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException er) {}
						} while (FFPROBE.isRunning);
						
						if (FFPROBE.keyFrame > 0)
						{	
							playerLeftSetTime(FFPROBE.keyFrame);
						}
					}
					else
					{
						//Allow to read 1 frame					
						playerLeftLoop = true;
					}
				}	
			}
			
		});
		
		leftPlay = new JButton(Shutter.language.getProperty("btnPlay"));
		leftPlay.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		leftPlay.setMargin(new Insets(0,0,0,0));
		leftPlay.setBounds(leftPrevious.getLocation().x - 80 - 4, leftPrevious.getLocation().y, 80, 21);				
		frame.getContentPane().add(leftPlay);
		
		leftPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					rightPlay.doClick();
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")) && Shutter.liste.getSize() != 0 || playerLeftVideo.isAlive() == false) 
				{							
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
					
					if (playerHasBeenStopped == false)
					{			
						playerLeftSetTime(0);
					}
					else
					{
						playerLeftPlay();
						leftPlay.setText(Shutter.language.getProperty("btnPause"));
						playerLeftLoop = true;
					}
										
					caseInH.setEnabled(true);
					caseInM.setEnabled(true);
					caseInS.setEnabled(true);
					caseInF.setEnabled(true);				
					
					sliderIn.setEnabled(true);
					
					sliderOut.setEnabled(true);	
					
				}
				else if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					leftPlay.setText(Shutter.language.getProperty("btnResume"));	
					playerLeftLoop = false;
					
					if (sliderSpeed.getValue() != 2)
					{
						playerLeftSetTime(formatTime(sliderIn.getValue()));	
					}							
				}
				else if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{				
					frameLeftControl = false;
					leftPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLeftLoop = true;
				}
								
			}
			
		});
		
		leftStop = new JButton(Shutter.language.getProperty("btnStop"));
		leftStop.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		leftStop.setMargin(new Insets(0,0,0,0));
		leftStop.setBounds(leftNext.getLocation().x + leftNext.getSize().width + 4, leftNext.getLocation().y, 80, 21);		
		frame.getContentPane().add(leftStop);		
		
		leftStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playerLeftVideo != null)
				{					
					playerLeftTime = 0;
					if (playerLeftVideo != null)
					{
						playerLeftStop();
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
						} while (playerLeftVideo.isAlive());
						sliderIn.setValue(0);
					}
					
					playerRightTime = FFPROBE.totalLength;
					if (playerRightVideo != null)
					{
						playerRightStop();
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
						} while (playerRightVideo.isAlive());
						sliderOut.setValue(sliderOut.getMaximum());
					}	
					
					resizeAll();
										
					playerRight.setVisible(false);
					playerRight.setVisible(false);
					if (FFPROBE.audioOnly)
					{
						waveformRight.setVisible(false);
						panelWaveformRight.setVisible(false);
					}
					rightPrevious.setVisible(false);
					rightNext.setVisible(false);
					rightPlay.setVisible(false);
					rightStop.setVisible(false);
					
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
					playerLeftLoop = false;
					
					caseInH.setText("00");
					caseInM.setText("00");
					caseInS.setText("00");
					caseInF.setText("00");
										
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						SubtitlesTimeline.actualSubOut = 0;	
					
					playerHasBeenStopped = true;
					playerLeftTime = 0;				
					playerRightTime = FFPROBE.totalLength;
				}
				
			}
			
			
		});
  
    }

	private void sliders() {
		sliderIn = new JSlider();
		sliderIn.setPaintLabels(true);
		sliderIn.setValue(0);
		sliderIn.setBounds(grpIn.getLocation().x + grpIn.getSize().width + 12, grpIn.getLocation().y, frame.getSize().width - (grpIn.getLocation().x + grpIn.getSize().width + 12) - 12, 60); 
		frame.getContentPane().add(sliderIn);
				
		sliderIn.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					rightPlay.doClick();
				
				if (playerLeftVideo != null && sliderInChange)
				{		
					if (sliderIn.getValue() > 0)
						playerLeftSetTime(formatTime(sliderIn.getValue()));	
					else
						playerLeftSetTime(0);
							
					if (sliderIn.getValue() > sliderOut.getValue())
					{
						try {
							
							sliderOut.setValue(sliderIn.getValue());
							panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
							playerRightSetTime(formatTime(sliderOut.getValue()));
							
						}catch (Exception e1){}
					}							
					
					if (FFPROBE.audioOnly && panelWaveformLeft != null)
					{
		        		panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);	        		
					}
				}
			}
			
		});
		
		sliderIn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {	
				
				sliderInChange = true;
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
					leftPlay.doClick();
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				
				//Allows to wait for the last frame to load
				if (playerLeftLoop == false)
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				}
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (playerLeftLoop);
				}
				
				sliderInChange = false;		
				
				//Then refresh the slider position
				getTimeInPoint(playerLeftTime - inputFramerateMS);
							
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			
			}

			@Override
			public void mouseExited(MouseEvent e) {				
			}
			
		});
		
		sliderOut = new JSlider();
		sliderOut.setPaintLabels(true);
		sliderOut.setValue(100);
		sliderOut.setBounds(grpOut.getLocation().x + grpOut.getSize().width + 12, grpOut.getLocation().y, frame.getSize().width - (grpOut.getLocation().x + grpOut.getSize().width + 12) - 12, 60); 
		frame.getContentPane().add(sliderOut);	
		
		sliderOut.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {				
				
					if (sliderOut.getValue() != sliderOut.getMaximum())
					{
						if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
						{
							if (sliderSpeed.getValue() != 2)
							{
								sliderSpeed.setValue(2);
								lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
							}
								
							leftPlay.doClick();
						}
						
						caseOutH.setEnabled(true);
						caseOutM.setEnabled(true);
						caseOutS.setEnabled(true);
						caseOutF.setEnabled(true);
						
						if (sliderOutChange)
						{							
							rightPrevious.setVisible(true);
							rightNext.setVisible(true);
							rightPlay.setVisible(true);
							rightStop.setVisible(true);							

							if (playerRightTime == 0 || playerRightTime == FFPROBE.totalLength) // Seul moyen pour effectuer l'action seulement quand le playerRight est invisible
							{				
		    					leftPlay.setText(Shutter.language.getProperty("btnResume"));
		    					
		    					playerLeftStop();		    						    					
		    					do {
		    						try {
		    							Thread.sleep(1);
		    						} catch (InterruptedException er) {}
		    					} while (playerLeftThread.isAlive());
		    													
								playerRightTime = formatTime(sliderOut.getValue());	
								
								resizeAll();	
							}
							else if (playerRightVideo != null && sliderOutChange)
							{
								playerRightSetTime(formatTime(sliderOut.getValue()));
							}	
							
							if (FFPROBE.audioOnly && panelWaveformRight != null)
							{
								panelWaveformRight.setVisible(true);
				 	  			panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);        		
							}
														
							if (FFPROBE.audioOnly == false)
								playerRight.setVisible(true);			
							
							if (sliderOut.getValue() < sliderIn.getValue())
							{								
								try {
									
									sliderIn.setValue(sliderOut.getValue());
									panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);		
									
									playerLeftSetTime(formatTime(sliderIn.getValue()));	
									
								}catch (Exception e1){}
							}
							
						}		
					}
					else
					{						
						if (sliderOutChange)
							rightStop.doClick();
						
						caseOutH.setEnabled(false);
						caseOutM.setEnabled(false);
						caseOutS.setEnabled(false);
						caseOutF.setEnabled(false);
					}
					
			}
	
		});

		sliderOut.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {	
			}

			@Override
			public void mousePressed(MouseEvent e) {					
				sliderOutChange = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				//Allows to wait for the last frame to load
				if (playerRightLoop == false)
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				}
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (playerRightLoop);
				}
				
				sliderOutChange = false;		
				
				//Then refresh the slider position
				getTimeOutPoint(playerRightTime - inputFramerateMS);	

			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {		
			}
	
		});
		
		sliderVolume = new JSlider();
		sliderVolume.setName("sliderVolume");
		sliderVolume.setValue(Settings.videoPlayerVolume);
		sliderVolume.setBounds(frame.getSize().width - 12 - 111, sliderIn.getLocation().y - 33, 111, 22);		
		frame.getContentPane().add(sliderVolume);
		
		sliderVolume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Settings.videoPlayerVolume = sliderVolume.getValue();			
			}
			
		});
		
		lblVolume = new JLabel(Shutter.language.getProperty("volume") + " ");
		lblVolume.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVolume.setBounds(sliderVolume.getLocation().x - 69, sliderIn.getLocation().y - 30, 69, 16);		
		frame.getContentPane().add(lblVolume);
				
		addWaveform(true);
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getSize().width, 52);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20 , 3, 15, 15);
		
		quit.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));;
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				int reply = JOptionPane.YES_OPTION;
				
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{
					reply = JOptionPane.showConfirmDialog(frame,
						Shutter.language.getProperty("areYouSure"),
						Shutter.language.getProperty("frameLecteurVideo"), JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);		
				}
				
				if (accept && reply == JOptionPane.YES_OPTION) 
				{			
					if (waveform.exists())
						waveform.delete();
					
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
					
					Shutter.caseInAndOut.setSelected(false);
					
					leftStop.doClick();
					
					videoPath = null;
					lblVideo.setVisible(false);
					playerLeftStop();
					sliderIn.setValue(0);
					
					playerRightStop();
					sliderOut.setValue(sliderOut.getMaximum());

					leftPlay.setText(Shutter.language.getProperty("btnPlay"));
					rightPlay.setText(Shutter.language.getProperty("btnResume"));		
					
					leftPlay.setEnabled(false);
					leftPrevious.setEnabled(false);
					leftNext.setEnabled(false);
					leftStop.setEnabled(false);
					
					caseTcInterne.setEnabled(false);	
					caseTcInterne.setSelected(false);
										
					frame.getContentPane().removeAll();
					Utils.changeFrameVisibility(frame, true);
					
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
							FFPROBE.CalculH264();
							break;
					}
		    		
		    		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		    		{
						Shutter.caseInAndOut.setSelected(false);
		    			SubtitlesTimeline.frame.dispose();
		    		}
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
		
		fullscreen = new JLabel(new FlatSVGIcon("contents/max.svg", 15, 15));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 20, 3, 15, 15);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max_pressed.svg", 15, 15));
				accept = true;
				
				if (playerLeftVideo != null)
				{
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
					playerLeftStop();
				}
				
				if (playerRightVideo != null)
				{
					rightPlay.setText(Shutter.language.getProperty("btnResume"));
					playerRightStop();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				int taskBarHeight = screenSize.height - winSize.height;
        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        		        		
				if (accept && frame.getHeight() < screenSize.height - taskBarHeight)
				{					
					if (playerLeft.getHeight() > playerLeft.getWidth())
					{
						frame.setBounds(0,0, screenSize.width, screenSize.height - taskBarHeight); 	
					}
					else
					{
						int setWidth = (int) ((float) (screenSize.height - topPanel.getHeight() - taskBarHeight - btnCaptureIn.getHeight() - leftPlay.getHeight() - sliderIn.getHeight() * 2 - lblDuree.getHeight() - 40) * ((float) (playerLeft.getWidth() * 2) / playerLeft.getHeight()));
						if (setWidth <= screenSize.width)
							frame.setSize(setWidth, screenSize.height - taskBarHeight); 
						else
							frame.setSize(screenSize.width, screenSize.height - taskBarHeight);						
							
						frame.setLocation(dim.width/2-frame.getSize().width/2,0); 	
					}						
				}
				else if (accept)
				{
	        		frame.setSize(1000, 640);
	        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);		
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {}
				}
				
				if (waveform.exists())
				{
					if (waveformLeft != null && FFPROBE.audioOnly == false)
						waveformLeft.setVisible(false);
					
					if (waveformRight != null && FFPROBE.audioOnly == false)
						waveformRight.setVisible(false);
					
					sliderIn.setVisible(true);
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
						sliderOut.setVisible(true);
				}

				resizeAll();	
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				fullscreen.setIcon(new FlatSVGIcon("contents/max_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max.svg", 15, 15));
				accept = false;
			}
			
			
		});
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(fullscreen.getLocation().x - 20, 3, 15, 15);
			
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce_pressed.svg", 15, 15));
				accept = true;
			}

			@SuppressWarnings("static-access")
			@Override
			public void mouseReleased(MouseEvent e) {		
				
				if (accept)
				{					
					
					frame.setState(frame.ICONIFIED);	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				reduce.setIcon(new FlatSVGIcon("contents/reduce_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce.svg", 15, 15));
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
				if (down.getClickCount() == 2)
				{
					if (playerLeftVideo != null)
    				{
						leftPlay.setText(Shutter.language.getProperty("btnResume"));
    					playerLeftStop();
    				}
					
    				if (playerRightVideo != null)
    				{
    					rightPlay.setText(Shutter.language.getProperty("btnResume"));
    					playerRightStop();
    				}
					
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
					int taskBarHeight = screenSize.height - winSize.height;
	        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        		
					if (frame.getHeight() < screenSize.height - taskBarHeight)
					{						
						if (playerLeft.getHeight() > playerLeft.getWidth())
						{
							frame.setBounds(0,0, screenSize.width, screenSize.height - taskBarHeight); 	
						}
						else
						{
							int setWidth = (int) ((float) (screenSize.height - topPanel.getHeight() - taskBarHeight - btnCaptureIn.getHeight() - leftPlay.getHeight() - sliderIn.getHeight() * 2 - lblDuree.getHeight() - 40) * ((float) (playerLeft.getWidth() * 2) / playerLeft.getHeight()));
							if (setWidth <= screenSize.width)
								frame.setSize(setWidth, screenSize.height - taskBarHeight); 
							else
								frame.setSize(screenSize.width, screenSize.height - taskBarHeight);						
								
							frame.setLocation(dim.width/2-frame.getSize().width/2,0); 	
						}						
					}
					else
					{
		        		frame.setSize(1000, 640);
		        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);		
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					}
					
					if (waveform.exists())
					{
						if (waveformLeft != null && FFPROBE.audioOnly == false)
							waveformLeft.setVisible(false);
						
						if (waveformRight != null && FFPROBE.audioOnly == false)
							waveformRight.setVisible(false);
						
						sliderIn.setVisible(true);
						
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
							sliderOut.setVisible(true);
					}

					resizeAll();
				}
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;	
				
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(reduce);
		topPanel.add(fullscreen);
		topPanel.add(quit);		
		topPanel.add(topImage);
		topPanel.add(bottomImage);
		
		frame.getContentPane().add(topPanel);
	}

	@SuppressWarnings("serial")
	private void players() {		
		
		float largeurMax = (float) frame.getSize().width / frame.getSize().height;
		final int largeur = (int) ((float) frame.getSize().width / largeurMax);
		
		Integer canvasOffset = topPanel.getSize().height + 10 + 21; // Default btnCaptureIn Y position
		if (ratio < 1.77f)
			canvasOffset = topPanel.getSize().height + 10;
		
		Integer canvasHeight = frame.getSize().height - 147 - 13 - canvasOffset; // Default grpIn Y position
		
		playerLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2 = (Graphics2D)g;
                
                g2.setColor(Color.BLACK);
                                
                if (drag)
                {
                	g2.fillRect(0, 0, playerLeft.getWidth(), playerRight.getHeight()); 
                }
                else
                {
                	g2.drawImage(frameLeft, 0, 0, null); 
                }
                
                if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
                {
                	SubtitlesTimeline.refreshData();

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        
                    //On sépare les lignes
                    String text[] = SubtitlesTimeline.txtSubtitles.getText().split("\\r?\\n");                   
                    
                    if (text[0].contains("i>") && text[0].contains("b>"))
                    	g2.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, (int) Math.floor(playerLeft.getHeight()/16))); 
                    else if (text[0].contains("i>"))
                    	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(playerLeft.getHeight()/16))); 
                    else if (text[0].contains("b>"))
                    	g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.floor(playerLeft.getHeight()/16))); 
                    else
                    	g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.floor(playerLeft.getHeight()/16))); 
                    
                    String firstLine = text[0].replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", "");
                                    	
                    FontMetrics metrics = g.getFontMetrics(g2.getFont());
                    
                    int x = (playerLeft.getWidth() - metrics.stringWidth(firstLine)) / 2;                                	
                    int y = playerLeft.getHeight() - (int) (playerLeft.getHeight()/24);
                    
                    if (text.length > 1 && text[1].length() > 0)
                    {                                	                	
                    	y = playerLeft.getHeight() - (int) (playerLeft.getHeight()/9.5);                	
                    	g2.setColor(Color.BLACK);
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftWest(x, 1), ShiftSouth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftNorth(y, 1));
                    	g2.drawString(firstLine, ShiftEast(x, 1), ShiftSouth(y, 1));
                    	g2.setColor(Color.WHITE);
                    	g2.drawString(firstLine, x, y);
                    	
                    	if (text[1].contains("i>") && text[1].contains("b>"))
                        	g2.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, (int) Math.floor(playerLeft.getHeight()/16))); 
                        else if (text[1].contains("i>"))
                        	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(playerLeft.getHeight()/16))); 
                        else if (text[1].contains("b>"))
                        	g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.floor(playerLeft.getHeight()/16))); 
                        else
                        	g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.floor(playerLeft.getHeight()/16))); 
                    	
                        String secondLine = text[1].replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", "");
    	 	            
    	 	            x = (playerLeft.getWidth() - metrics.stringWidth(secondLine)) / 2;
    	 	            y = playerLeft.getHeight() - (int) (playerLeft.getHeight()/24);
                    	
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
		playerLeft.setLayout(null);

		playerLeft.setBackground(Color.BLACK);
		if (ratio < 1.77f)
			playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
		else
			playerLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / ratio));			
		playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, canvasOffset + (canvasHeight - playerLeft.getHeight()) / 2);	
		
		frame.getContentPane().add(playerLeft);
				
		playerRight = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                g.setColor(Color.BLACK);
                
                if (drag)
                {
                	g.fillRect(0, 0, playerLeft.getWidth(), playerRight.getHeight()); 
                }
                else
                {
                	g.drawImage(frameRight, 0, 0, null); 
                }               
            }
        };
		playerRight.setLayout(null);
		playerRight.setBackground(Color.BLACK);
		playerRight.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width, playerLeft.getLocation().y, playerLeft.getSize().width, playerLeft.getSize().height);		
		playerRight.setVisible(false);
		
		frame.getContentPane().add(playerRight);
		
		waveformLeft = new JLabel();
		waveformRight = new JLabel();
								
		panelWaveformLeft = new JPanel();
		panelWaveformLeft.setBackground(Color.RED);
		waveformLeft.add(panelWaveformLeft);		
		
		waveformLeft.addMouseListener(new MouseListener(){

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
				
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
					leftPlay.doClick();			
				
				if (e.getX() >= 0 && e.getX() <= waveformLeft.getWidth() - 2)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(e.getX(), panelWaveformLeft.getLocation().y);	
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}
				else if (e.getX() < 0)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(0, panelWaveformLeft.getLocation().y);	
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}
				else if (e.getX() <= waveformLeft.getWidth() - 2)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(waveformLeft.getWidth() - 2, panelWaveformLeft.getLocation().y);	
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {	

				//Allows to wait for the last frame to load
				if (playerLeftLoop == false)
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				}
					
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (playerLeftLoop);
				}
				
				sliderInChange = false;		
				
				//Then refresh the slider position
				getTimeInPoint(playerLeftTime - inputFramerateMS);
			}
			
		});
		
		waveformLeft.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {
								
				if (e.getX() >= 0 && e.getX() <= waveformLeft.getWidth() - 2)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(e.getX(), panelWaveformLeft.getLocation().y);	
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}
				else if (e.getX() < 0)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(0, panelWaveformLeft.getLocation().y);	
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}
				else if (e.getX() <= waveformLeft.getWidth() - 2)
				{
					sliderInChange = true;					
					panelWaveformLeft.setLocation(waveformLeft.getWidth() - 2, panelWaveformLeft.getLocation().y);		
					sliderIn.setValue((int) ((long) sliderIn.getMaximum() * panelWaveformLeft.getLocation().x / waveformLeft.getSize().width));
				}				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
		
		});

		panelWaveformRight = new JPanel();
		panelWaveformRight.setBackground(Color.RED);
				
		waveformRight.addMouseListener(new MouseListener(){

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

				if (e.getX() >= 0 && e.getX() <= waveformRight.getWidth() - 2)
				{
					sliderOutChange = true;	
					panelWaveformRight.setLocation(e.getX(), panelWaveformRight.getLocation().y);	
					sliderOut.setValue((int) ((long) sliderOut.getMaximum() * panelWaveformRight.getLocation().x / waveformRight.getSize().width));		
				}
				else if (e.getX() < 0)
				{
					sliderOutChange = true;	
					panelWaveformRight.setLocation(0, panelWaveformRight.getLocation().y);	
					sliderOut.setValue((int) ((long) sliderOut.getMaximum() * panelWaveformRight.getLocation().x / waveformRight.getSize().width));	
				}
				else if (e.getX() > waveformRight.getWidth() - 2)
				{
					sliderOutChange = true;	
					panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
					sliderOut.setValue(sliderOut.getMaximum());	
				}
				/*
				if (playerRightVideo == null)
				{
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					} while (playerRightVideo == null);
				}*/
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				//Allows to wait for the last frame to load
				if (playerRightLoop == false)
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				}
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (playerRightLoop);
				}
				
				sliderOutChange = false;		
				
				//Then refresh the slider position
				getTimeOutPoint(playerRightTime - inputFramerateMS);	
				
				if (sliderOut.getValue() == sliderOut.getMaximum())
				{
					panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);
				}
			}
			
		});
		
		waveformRight.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")) == false)
				{
					if (e.getX() >= 0 && e.getX() <= waveformRight.getWidth() - 2)
					{
						sliderOutChange = true;	
						panelWaveformRight.setLocation(e.getX(), panelWaveformRight.getLocation().y);	
						sliderOut.setValue((int) ((long) sliderOut.getMaximum() * panelWaveformRight.getLocation().x / waveformRight.getSize().width));		
					}
					else if (e.getX() < 0)
					{
						sliderOutChange = true;	
						panelWaveformRight.setLocation(0, panelWaveformRight.getLocation().y);	
						sliderOut.setValue((int) ((long) sliderOut.getMaximum() * panelWaveformRight.getLocation().x / waveformRight.getSize().width));	
					}
					else if (e.getX() > waveformRight.getWidth() - 2)
					{
						sliderOutChange = true;	
						panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
						sliderOut.setValue(sliderOut.getMaximum());	
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
		
		});
		
		//Audio seulement
		if (FFPROBE.hasAudio || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
		{
			Thread addWaveform = new Thread(new Runnable() {
				public void run() {		
					try {
						do {
							Thread.sleep(10);
						} while (waveform.exists() == false);			
									
						if (waveform.exists())
						{
							Image imageBMP = ImageIO.read(waveform);
							ImageIcon resizedWaveform;
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));	
							else if (FFPROBE.audioOnly)
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / ratio), Image.SCALE_AREA_AVERAGING));	
							else
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
						
							waveformLeft.setIcon(resizedWaveform);
							waveformRight.setIcon(resizedWaveform);
							
							if (FFPROBE.audioOnly)
							{
								waveformLeft.setBounds(playerLeft.getBounds());
								waveformRight.setBounds(playerRight.getBounds());							
							}
							else
							{
								waveformLeft.setSize(sliderIn.getWidth(), grpIn.getHeight());	
								waveformLeft.setLocation(sliderIn.getLocation());
								waveformRight.setSize(sliderOut.getWidth(), grpIn.getHeight());	
								waveformRight.setLocation(sliderOut.getLocation());
							}	
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
							{
								waveformLeft.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));							
								waveformRight.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));
							}

							frame.getContentPane().add(waveformLeft);  
							frame.getContentPane().add(waveformRight);
							frame.getContentPane().repaint();
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							{
								waveformLeft.setVisible(false);
								waveformRight.setVisible(false);
							}
							else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
							{
								waveformRight.setVisible(false);
							}								
							
							if (sliderOut.getValue() == sliderOut.getMaximum())
								panelWaveformRight.setBounds(waveformRight.getSize().width - 2, 0, 2, waveformRight.getSize().height);
							else
								panelWaveformRight.setBounds((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) , 0, 2, waveformRight.getSize().height);
							
							waveformRight.add(panelWaveformRight);
							
							if (FFPROBE.audioOnly)
							{
								playerLeft.setVisible(false);
								waveformRight.setVisible(false);
								panelWaveformRight.setVisible(false);
							}
							else
								playerLeft.setVisible(true);
							
							panelWaveformLeft.repaint();		
							panelWaveformRight.repaint();	
						}						
					} catch (Exception e) {}
				}
			});
			addWaveform.start();
		}
		
		Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void grpIn(){
		grpIn = new JPanel();
		grpIn.setLayout(null);
		grpIn.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), Shutter.language.getProperty("grpIn") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpIn.setBackground(new Color(50, 50, 50));
		grpIn.setBounds(6, frame.getSize().height - 147, 156, 52);
		frame.getContentPane().add(grpIn);
		
		caseInH = new JTextField();
		caseInH.setName("caseInH");
		caseInH.setText("00");
		caseInH.setHorizontalAlignment(SwingConstants.CENTER);
		caseInH.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInH.setColumns(10);
		caseInH.setBounds(6, 17, 36, 26);
		grpIn.add(caseInH);
		
		caseInM = new JTextField();
		caseInM.setName("caseInM");
		caseInM.setText("00");
		caseInM.setHorizontalAlignment(SwingConstants.CENTER);
		caseInM.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInM.setColumns(10);
		caseInM.setBounds(42, 17, 36, 26);
		grpIn.add(caseInM);
		
		caseInS = new JTextField();
		caseInS.setName("caseInS");
		caseInS.setText("00");
		caseInS.setHorizontalAlignment(SwingConstants.CENTER);
		caseInS.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInS.setColumns(10);
		caseInS.setBounds(78, 17, 36, 26);
		grpIn.add(caseInS);
		
		caseInF = new JTextField();
		caseInF.setName("caseInF");
		caseInF.setText("00");
		caseInF.setHorizontalAlignment(SwingConstants.CENTER);
		caseInF.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseInF.setColumns(10);
		caseInF.setBounds(114, 17, 36, 26);
		grpIn.add(caseInF);
				
		caseTcInterne = new JRadioButton(Shutter.language.getProperty("caseTcInterne"));
		caseTcInterne.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseTcInterne.setBounds(6, frame.getHeight() - 31, 195, 23);
		frame.getContentPane().add(caseTcInterne);	
		
		casePlaySound = new JRadioButton(Shutter.language.getProperty("casePlaySound"));
		casePlaySound.setName("casePlaySound");
		casePlaySound.setBounds(6, grpIn.getLocation().y - 36, 195, 23);	
		casePlaySound.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		casePlaySound.setSelected(Settings.videoPlayerCasePlaySound);
		frame.getContentPane().add(casePlaySound);
		
		casePlaySound.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Settings.videoPlayerCasePlaySound = casePlaySound.isSelected();
			}

		});
		
		btnPreview = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		btnPreview.setHorizontalAlignment(SwingConstants.CENTER);
		btnPreview.setBounds(frame.getSize().width - 28, frame.getSize().height - 26, 16, 16);
		btnPreview.setToolTipText(Shutter.language.getProperty("preview"));
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(btnPreview);
		
		btnPreview.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {	
				
				FFMPEG.toSDL(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				btnPreview.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnPreview.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}        			
		});
		
		comboMode.setName("comboMode");
		comboMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		comboMode.setMaximumRowCount(2);
		comboMode.setSize(76, 22);
		comboMode.setLocation(btnPreview.getX() - 76 - 5, frame.getSize().height - 16 - 12 - 1);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(comboMode);
		
		comboMode.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")) && showInfoMessage)
				{
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("mayNotWorkWithGOP"), Shutter.language.getProperty("mode") + " " + Shutter.language.getProperty("removeMode"), JOptionPane.INFORMATION_MESSAGE);
					showInfoMessage = false;
				}
				
				totalDuration();
			}
	
		});
		
		lblMode = new JLabel(Shutter.language.getProperty("mode"));
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblMode.getPreferredSize().width, 16);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(lblMode);

		sliderSpeed = new JSlider();
		sliderSpeed.setMaximum(4);
		sliderSpeed.setValue(2);
		sliderSpeed.setMinorTickSpacing(1);
		sliderSpeed.setMajorTickSpacing(1);
		sliderSpeed.setSize(80, 22);
		sliderSpeed.setLocation(lblMode.getX() - 80 - 5, frame.getSize().height - 16 - 12);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(sliderSpeed);

		sliderSpeed.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (e.getX() < 20)
				{
					sliderSpeed.setValue(0);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.25");
				}
				else if (e.getX() > 10 && e.getX() < 30)
				{
					sliderSpeed.setValue(1);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.5");
				}
				else if (e.getX() > 30 && e.getX() < 50)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
				}
				else if (e.getX() > 50 && e.getX() < 70)
				{
					sliderSpeed.setValue(3);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1.5");
				}
				else if (e.getX() > 70)
				{
					sliderSpeed.setValue(4);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x2");
				}
				
				lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {	
				
			}
			
		});
		
		sliderSpeed.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() == 2)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
					
					if (sliderIn.getValue() > 0)
					{
						leftFrameIsComplete = false;
									
						playerLeftSetTime(sliderIn.getValue());
						
						long time = System.currentTimeMillis();
						
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException er) {}
							
							if (System.currentTimeMillis() - time > 1000)
								leftFrameIsComplete = true;
														
						} while (leftFrameIsComplete == false);		
					}
					
					if (sliderOut.getValue() != sliderOut.getMaximum())
					{
						rightFrameIsComplete = false;
						
						playerRightSetTime(sliderOut.getValue());
	
						long time = System.currentTimeMillis();
						
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException er) {}
							
							if (System.currentTimeMillis() - time > 1000)
								rightFrameIsComplete = true;
														
						} while (rightFrameIsComplete == false);
					}	
				}
			}	
			
			@Override
			public void mouseReleased(MouseEvent e) {
					
				if (e.getX() < 20)
				{
					sliderSpeed.setValue(0);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.25");
				}
				else if (e.getX() > 10 && e.getX() < 30)
				{
					sliderSpeed.setValue(1);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.5");
				}
				else if (e.getX() > 30 && e.getX() < 50)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
				}
				else if (e.getX() > 50 && e.getX() < 70)
				{
					sliderSpeed.setValue(3);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1.5");
				}
				else if (e.getX() > 70)
				{
					sliderSpeed.setValue(4);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x2");
				}
				
				lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
								
				if (sliderIn.getValue() > 0)
				{
					leftFrameIsComplete = false;
								
					playerLeftSetTime(sliderIn.getValue());
					
					long time = System.currentTimeMillis();
					
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException er) {}
						
						if (System.currentTimeMillis() - time > 1000)
							leftFrameIsComplete = true;
													
					} while (leftFrameIsComplete == false);		
				}
				
				if (sliderOut.getValue() != sliderOut.getMaximum())
				{
					rightFrameIsComplete = false;
					
					playerRightSetTime(sliderOut.getValue());

					long time = System.currentTimeMillis();
					
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException er) {}
						
						if (System.currentTimeMillis() - time > 1000)
							rightFrameIsComplete = true;
													
					} while (rightFrameIsComplete == false);
				}	
			}

		});
				
		lblSpeed = new JLabel(Shutter.language.getProperty("conformBySpeed") + " x1"); //0.25 allow to get max preferred size width
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(lblSpeed);
		
		caseTcInterne.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
    			NumberFormat formatter = new DecimalFormat("00");
    			
    			float timeIn =  Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS;
    			float timeOut = Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS;
    			
				if (caseTcInterne.isSelected())
				{
					FFPROBE.Data(videoPath);
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFPROBE.isRunning);					
					
					if (FFPROBE.timecode1 != "")
					{
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600000
								+ Integer.valueOf(FFPROBE.timecode2) * 60000 
								+ Integer.valueOf(FFPROBE.timecode3) * 1000
								+ Integer.valueOf(FFPROBE.timecode4) * inputFramerateMS;					
					}
				}
				else
				{
					offset = 0;
					
					if (sliderIn.getValue() != 0)
						timeIn = (playerLeftTime - inputFramerateMS);
					else
						timeIn = 0;
					
					if (sliderOut.getValue() < sliderOut.getMaximum())
						timeOut = playerRightTime;
					else
						timeOut = FFPROBE.totalLength;
				}
								
				timeIn += offset;
									    			
    			caseInH.setText(formatter.format(Math.floor(timeIn / 3600000)));
    			caseInM.setText(formatter.format(Math.floor(timeIn / 60000) % 60));
    			caseInS.setText(formatter.format(Math.floor(timeIn / 1000) % 60));    		
    			caseInF.setText(formatter.format((int) Math.floor(timeIn / inputFramerateMS % FFPROBE.currentFPS)));    			

    			timeOut += offset;
    			
    			caseOutH.setText(formatter.format(Math.floor(timeOut / 3600000)));
    			caseOutM.setText(formatter.format(Math.floor(timeOut / 60000) % 60));
    			caseOutS.setText(formatter.format(Math.floor(timeOut / 1000) % 60));    		
    			caseOutF.setText(formatter.format((int) Math.floor(timeOut / inputFramerateMS % FFPROBE.currentFPS)));
			}	
		});
		
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

					playerLeftTime = (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS) - offset;
					playerLeftSetTime(playerLeftTime);
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

					playerLeftTime = (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS) - offset;
					playerLeftSetTime(playerLeftTime);
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

					playerLeftTime = (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS) - offset;
					playerLeftSetTime(playerLeftTime);										
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

					playerLeftTime = (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS) - offset;
					playerLeftSetTime(playerLeftTime);
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
		grpOut = new JPanel();
		grpOut.setLayout(null);
		grpOut.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), Shutter.language.getProperty("grpOut") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpOut.setBackground(new Color(50, 50, 50));
		grpOut.setBounds(6, frame.getSize().height - 86, 156, 52);
		frame.getContentPane().add(grpOut);
		
		caseOutH = new JTextField();
		caseOutH.setName("caseOutH");
		caseOutH.setText("00");
		caseOutH.setEnabled(false);
		caseOutH.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutH.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutH.setColumns(10);
		caseOutH.setBounds(6, 17, 36, 26);
		grpOut.add(caseOutH);
				
		caseOutM = new JTextField();
		caseOutM.setName("caseOutM");
		caseOutM.setText("00");
		caseOutM.setEnabled(false);
		caseOutM.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutM.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutM.setColumns(10);
		caseOutM.setBounds(42, 17, 36, 26);
		grpOut.add(caseOutM);
		
		caseOutS = new JTextField();
		caseOutS.setName("caseOutS");
		caseOutS.setText("00");
		caseOutS.setEnabled(false);
		caseOutS.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutS.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutS.setColumns(10);
		caseOutS.setBounds(78, 17, 36, 26);
		grpOut.add(caseOutS);
		
		caseOutF = new JTextField();
		caseOutF.setName("caseOutF");
		caseOutF.setText("00");
		caseOutF.setEnabled(false);
		caseOutF.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutF.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutF.setColumns(10);
		caseOutF.setBounds(114, 17, 36, 26);
		grpOut.add(caseOutF);
		
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
					
					playerRightTime = (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS) - (int) inputFramerateMS - offset;
					playerRightSetTime(playerRightTime);
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

					playerRightTime = (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS) - (int) inputFramerateMS - offset;
					playerRightSetTime(playerRightTime);
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

					playerRightTime = (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS) - (int) inputFramerateMS - offset;
					playerRightSetTime(playerRightTime);
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

					playerRightTime = (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS) - (int) inputFramerateMS - offset;
					playerRightSetTime(playerRightTime);
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

	private void resizeAll() {
				
		//topPanel
		topPanel.setBounds(0,0,frame.getSize().width, 52);
		topImage.setLocation(frame.getSize().width / 2 - topImage.getSize().width / 2, 0);
		quit.setBounds(frame.getSize().width - 20, 3, 15, 15);
		fullscreen.setBounds(quit.getLocation().x - 20, 3, 15, 15);
		reduce.setBounds(fullscreen.getLocation().x - 20, 3, 15, 15); 		

		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);
		
		bottomImage.setBounds(0 ,0, frame.getSize().width, 52);
		
		//Boutons		
		btnCaptureIn.setBounds(8, topPanel.getSize().height + 10, 130, 21);		
		btnApply.setBounds(frame.getSize().width - 6 - 130 - 4, topPanel.getSize().height + 10, 130, 21);		
		btnPreview.setBounds(frame.getSize().width - 28, frame.getSize().height - 26, 16, 16);
		lblVideo.setBounds(btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6, topPanel.getSize().height + 12, frame.getSize().width - (btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6 + btnApply.getSize().width + 12), 16);     		
		
		//Groupes boxes
		grpIn.setBounds(6, frame.getSize().height - 147, 156, 52);
		grpOut.setBounds(6, frame.getSize().height - 86, 156, 52);
		casePlaySound.setBounds(6, grpIn.getLocation().y - 36, 195, 23);
		caseTcInterne.setBounds(6, frame.getHeight() - 31, 195, 23);		
		comboMode.setLocation(btnPreview.getX() - 76 - 5, frame.getSize().height - 16 - 12 - 1);
		lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblMode.getPreferredSize().width, 16);		
		sliderSpeed.setLocation(lblMode.getX() - 80 - 5, frame.getSize().height - 16 - 12);
		lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblSpeed.getPreferredSize().width, 16);
		
		
		//Sliders
		sliderIn.setBounds(grpIn.getLocation().x + grpIn.getSize().width + 12, grpIn.getLocation().y, frame.getSize().width - (grpIn.getLocation().x + grpIn.getSize().width + 12) - 12, 60); 
		sliderOut.setBounds(grpOut.getLocation().x + grpOut.getSize().width + 12, grpOut.getLocation().y, frame.getSize().width - (grpOut.getLocation().x + grpOut.getSize().width + 12) - 12, 60); 
		sliderVolume.setBounds(frame.getSize().width - 12 - 111, sliderIn.getLocation().y - 33, 111, 22);		
		lblVolume.setBounds(sliderVolume.getLocation().x - 69, sliderIn.getLocation().y - 30, 69, 16);	
		
		//Lecteurs 
		float largeurMax = (float) frame.getSize().width / frame.getSize().height;
		int largeur = (int) ((float) frame.getSize().width / largeurMax);
		
		Integer canvasOffset = btnCaptureIn.getY() + btnCaptureIn.getHeight();
		if (ratio < 1.77f)
			canvasOffset = btnCaptureIn.getY();
		
		Integer canvasHeight = casePlaySound.getY() - canvasOffset;
		
		if (sliderOut.getValue() == sliderOut.getMaximum())
		{						
			if (ratio < 1.77f)
			{
				playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
				playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, playerLeft.getY());	
			}
			else
			{
				playerLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / ratio));			
				playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, canvasOffset + (canvasHeight - playerLeft.getHeight()) / 2);	
			}
		}
		else
		{
			if (ratio < 1.77f)
			{
				playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
				playerLeft.setLocation(frame.getSize().width / 2 - playerLeft.getWidth(), playerLeft.getY());	
			}
			else
			{				
				playerLeft.setSize(frame.getSize().width / 2 - 6, (int) ((float) (frame.getSize().width / 2 - 6) / ratio));	
				playerLeft.setLocation(6, canvasOffset + (canvasHeight - playerLeft.getHeight()) / 2);	
			}
		}
		
		//IMPORTANT video canvas must be a multiple of 4!
		playerLeft.setSize(playerLeft.getWidth() - (playerLeft.getWidth() % 4), playerLeft.getHeight());	
		
		playerRight.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width, playerLeft.getLocation().y, playerLeft.getSize().width, playerLeft.getSize().height);		
						
		//Boutons		
		rightPrevious.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 - 21 - 4, playerRight.getLocation().y + playerRight.getSize().height + 10, 22, 21);		
		rightNext.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 + 4, rightPrevious.getLocation().y, 22, 21);			
		rightPlay.setBounds(rightPrevious.getLocation().x - 80 - 4, rightPrevious.getLocation().y, 80, 21);		
		rightStop.setBounds(rightNext.getLocation().x + rightNext.getSize().width + 4, rightNext.getLocation().y, 80, 21);		
		
		leftPrevious.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 - 21 - 4, playerLeft.getLocation().y + playerLeft.getSize().height + 10, 22, 21);		
		leftNext.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 + 4, leftPrevious.getLocation().y, 22, 21);		
		leftPlay.setBounds(leftPrevious.getLocation().x - 80 - 4, leftPrevious.getLocation().y, 80, 21);				
		leftStop.setBounds(leftNext.getLocation().x + leftNext.getSize().width + 4, leftNext.getLocation().y, 80, 21);	
				
		//Durée
		lblDuree.setBounds(0, frame.getSize().height - 16 - 12, frame.getWidth(), 16);   	   		
		
		title.setBounds(0, 0, frame.getWidth(), 52);
					
		if (drag == false)
		{			
			//Waveforms
			addWaveform(false);	

			if (leftPlay.isEnabled())
				playerLeftFreeze();		
			
			if (sliderOut.getValue() < sliderOut.getMaximum())
				playerRightFreeze();
		}
	}
	
	public static void totalDuration() {	
		
		try {
			
			float totalIn =  Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * inputFramerateMS;
			float totalOut = Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * inputFramerateMS;
			
			float sommeTotal = totalOut - totalIn;
						
			if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
				sommeTotal = FFPROBE.totalLength - sommeTotal;	
			
			//IMPORTANT permet d'arrondir les cadence à virgule (30fps = 33,33ms)
			sommeTotal += 2 - sommeTotal % 2;
						
			dureeHeures = (int) (sommeTotal / 3600000);
			dureeMinutes = (int) (sommeTotal / 60000 % 60);
			dureeSecondes = (int) (sommeTotal / 1000 % 60);
			dureeImages = (int) ((sommeTotal  / inputFramerateMS) % FFPROBE.currentFPS);
			
			lblDuree.setText(Shutter.language.getProperty("lblDuree") + " " + dureeHeures + "h " + dureeMinutes +"min " + dureeSecondes + "sec " + dureeImages + "i" + " | " + Shutter.language.getProperty("lblTotalFrames") + " " + ((int) (sommeTotal  / inputFramerateMS)));
			
			if (sommeTotal <= 0)
	    		lblDuree.setVisible(false);  
			else
			{
	    		lblDuree.setVisible(true);   
	    		
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
			    	   	 NumberFormat formatter = new DecimalFormat("00");
			    	     int secondes = (int) ((sommeTotal / 1000) % 60);
			    	     int minutes =  (int) (((sommeTotal / 1000) / 60) % 60);
			    	     int heures = (int) ((sommeTotal / 1000) / 3600);
			    	
			    	     Shutter.textH.setText(formatter.format(heures));
			    	     Shutter.textMin.setText(formatter.format(minutes));
			    	     Shutter.textSec.setText(formatter.format(secondes));
			    	     
			    	     FFPROBE.setTailleH264();
		    	     break;
	    		}
			}
		
		} catch (Exception e){}
	}
	
	public static float formatTime(float time) {
		
		long seconds = (long) Math.floor(time / 1000) * 1000;
		long frameToMS = (long) Math.ceil((time % 1000) - Math.ceil(time % inputFramerateMS));
		
		return (seconds + frameToMS);	
	}

	public static void getTimeInPoint(float time) {	
		
		if (caseTcInterne.isSelected())
			time += offset;
		
		//IMPORTANT 
		if (time % 2 != 0.0f && inputFramerate % 2 == 0.0f)
			time += inputFramerateMS;
				
    	if (playerLeftVideo != null && time - offset < FFPROBE.totalLength)
    	{    		
    		//Lorsque le sliderIn atteint le sliderOut
    		if (playerLeftTime >= (playerRightTime - inputFramerateMS*2) && sliderOut.getValue() != sliderOut.getMaximum())
    		{
    			if (playerLeftIsPlaying())
    			{
    				leftPlay.setText(Shutter.language.getProperty("btnResume"));
    				playerLeftLoop = false;
    			}
    		}    		
    		    		
			NumberFormat formatter = new DecimalFormat("00");
			caseInH.setText(formatter.format(Math.floor(time / 3600000)));
			caseInM.setText(formatter.format(Math.floor(time / 60000) % 60));
			caseInS.setText(formatter.format(Math.floor(time / 1000) % 60));    		
			caseInF.setText(formatter.format(Math.floor((time / inputFramerateMS) % FFPROBE.currentFPS)));
			
    		if (sliderIn.getValue() == 0)
				caseInF.setText("00");		    		
    		
    		if (sliderOutChange == false && sliderInChange == false && drag == false)
    		{    			
    			sliderIn.setValue((int) playerLeftTime);
    			if (FFPROBE.hasAudio && VideoPlayer.panelWaveformLeft != null)
    				panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);
    			
    			if (sliderIn.getValue() > sliderOut.getValue())
				{
    				sliderInChange = true;
					sliderOut.setValue(sliderIn.getValue());
					panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
					sliderInChange = false;
				}
    		}

    		totalDuration();		
        		        	
    	}
    		
    }

	private static void getTimeOutPoint(float time) {	
				
		time += inputFramerateMS;
		
		//IMPORTANT
		if (time % 2 != 0.0f && inputFramerate % 2 == 0.0f)
			time += inputFramerateMS;
		
		if (caseTcInterne.isSelected())
			time += offset;
		
		if (time - offset >= FFPROBE.totalLength)
		{
			sliderOutChange = true;
			sliderOut.setValue(sliderOut.getMaximum());
			sliderOutChange = false;    		
		}
		
    	if (playerRightVideo != null && playerRightTime != 0)
    	{			    		    		
    		//Lorsque la video atteint le maximum
    		if (time >= (FFPROBE.totalLength - inputFramerateMS*2))
    		{
    			rightPlay.setText(Shutter.language.getProperty("btnResume"));
    			playerRightLoop = false;
    		}    		

			NumberFormat formatter = new DecimalFormat("00");
			caseOutH.setText(formatter.format(Math.floor(time / 3600000)));
			caseOutM.setText(formatter.format(Math.floor(time / 60000) % 60));
			caseOutS.setText(formatter.format(Math.floor(time / 1000) % 60));
			caseOutF.setText(formatter.format(Math.floor((time / inputFramerateMS) % FFPROBE.currentFPS)));
       		
	  		if (sliderInChange == false && sliderOutChange == false && drag == false)
	  		{	  			
	  			sliderOut.setValue((int) playerRightTime);
    			if (FFPROBE.hasAudio && panelWaveformRight != null)
    				panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
    			    			
	  			if (sliderOut.getValue() < sliderIn.getValue())
				{			
	  				sliderOutChange = true;
					sliderIn.setValue(sliderOut.getValue());
					panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);									
					sliderOutChange = false;
				}
	  		}

    		totalDuration();
    	}
	}

	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(10);					
				} while (leftPlay == null);
				
				do {
					Thread.sleep(10);					
				} while (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")));

				boolean changeInPoint = false;
				boolean changeOutPoint = false;

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
						
						for (Component c : frame.getContentPane().getComponents())
						{
							if (c instanceof JPanel)
							{
								for (Component p : ((JPanel) c).getComponents())
								{
									if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
									{
										if (p.getName().contains("caseIn"))
											changeInPoint = true;
										if (p.getName().contains("caseOut"))
											changeOutPoint = true;
										
										if (p instanceof JTextField)
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
					}
				}			
								
				//In
				if (changeInPoint)
				{
					sliderInChange = true;
					sliderIn.setValue((int) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + (Integer.parseInt(caseInF.getText()) * inputFramerateMS) - offset));
					sliderInChange = false;
				}
				
				//Out
				if (changeOutPoint)
				{
					sliderOutChange = true;
					sliderOut.setValue((int) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + (Integer.parseInt(caseOutF.getText()) * inputFramerateMS) - inputFramerateMS - offset));
					sliderOutChange = false;
				}
				
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}
}