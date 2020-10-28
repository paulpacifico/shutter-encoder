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
/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009, 2010, 2011, 2012, 2013 Caprica Software Limited.
 */

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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
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
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jna.NativeLibrary;

import library.FFMPEG;
import library.FFPLAY;
import library.FFPROBE;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class VideoPlayer {
	
	public static JFrame frame = new JFrame();
	JLabel title = new JLabel(Shutter.language.getProperty("frameLecteurVideo"));
	ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
	private boolean isFullScreen = false;
	
	private JLabel quit;
	private JLabel fullscreen;
	private JLabel reduce;
	private JPanel topPanel;
	private JLabel topImage;
	private JLabel bottomImage;
	public static JPanel playerLeft;
	public static JPanel playerRight;
			
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
	private JButton btnPreview;
	public static JButton leftPrevious;
	public static JButton leftNext;
	private JButton rightPrevious;
	private JButton rightNext;
	private JRadioButton casePlaySound;//framebyframe
	public static JRadioButton caseTcInterne;
	
	//Lecteur VLC duree Heures
	private static String videoPath = null;
    private static int width = 640;
    private static int height = 360;
    public static float ratio = 1.777777f;
    
    private static JPanel videoSurfaceLeft;
    private static JPanel videoSurfaceRight;
    private static BufferedImage imageLeft;
    private static BufferedImage imageRight;
    public static DirectMediaPlayerComponent mediaPlayerComponentLeft;
    public static DirectMediaPlayerComponent mediaPlayerComponentRight;
    public static JButton leftStop;
    public static JButton leftPlay;
    private static JButton rightStop;
    public static JButton rightPlay;
    private static JLabel lblVideo;
    
    private static boolean drag;
	public static boolean sliderInChange = false;
	static boolean sliderOutChange = false;
	private JLabel lblVolume;
	public static JSlider sliderVolume;
	private static JLabel lblDuree;
	private static JLabel lblMode;
	public static JComboBox<Object> comboMode = new JComboBox<Object>(new String [] {Shutter.language.getProperty("cutUpper"), Shutter.language.getProperty("removeMode")});
	private static boolean showInfoMessage = true;
	
	//Temps final
	public static int offset = 0;
	public static int dureeHeures = 0;
	public static int dureeMinutes = 0;
	public static int dureeSecondes = 0;
	public static int dureeImages = 0;
	
	//Stop le calcul
	private static boolean calculIn = true;
	private static boolean calculOut = true;
	
	//Avance et recul d'une image
	public static long timeIn;
	static long timeOut;
	static boolean frameControl = false;
	
	//Waveform
	public static File waveform = new File(Shutter.dirTemp + "waveform.png");
	public static  JLabel waveformLeft;
	public static  JLabel waveformRight;
	public static JPanel panelWaveformLeft;
	private static JPanel panelWaveformRight;
		
	/**
	 * @wbp.parser.entryPoint
	 */
	public VideoPlayer() {  	
		
		showInfoMessage = true;
		
		//Récupération du dossier de VLC
		//new NativeDiscovery().discover();
		String NATIVE_LIBRARY_SEARCH_PATH;
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			NATIVE_LIBRARY_SEARCH_PATH = "Library/vlc/";			
		}
		else
		{			
			NATIVE_LIBRARY_SEARCH_PATH = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			NATIVE_LIBRARY_SEARCH_PATH = NATIVE_LIBRARY_SEARCH_PATH.substring(0,NATIVE_LIBRARY_SEARCH_PATH.length()-1);
			NATIVE_LIBRARY_SEARCH_PATH = NATIVE_LIBRARY_SEARCH_PATH.substring(0,(int) (NATIVE_LIBRARY_SEARCH_PATH.lastIndexOf("/"))).replace("%20", " ")  + "/Library/vlc/lib";

			uk.co.caprica.vlcj.binding.LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", NATIVE_LIBRARY_SEARCH_PATH.replace("lib", "plugins"), 1);
		}
		
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
  
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		frame.getContentPane().setLayout(null);
		frame.setVisible(false);
		frame.setSize(646, 629);
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
    	btnCaptureIn.setFont(new Font("Montserrat", Font.PLAIN, 12));
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
					FFMPEG.fonctionInOut();	
					
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
					 	Thread.sleep(100);						 
					 while (FFPROBE.isRunning);
					
					String filter = "";	
					if (FFPROBE.entrelaced.equals("1"))
						filter = " -filter:v yadif=0:" + FFPROBE.fieldOrder + ":0";		

					if (sliderOut.getValue() != sliderOut.getMaximum())
					{
						int frameIn = (int) (Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS));
						int frameOut = (int) (Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS));	
						NumberFormat formatFrame = new DecimalFormat("000");
						
						FFMPEG.run( 
						" -ss " + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + "." + formatFrame.format(frameIn) + " -i " + '"' + file.toString() + '"' +
						" -ss " + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + "." + formatFrame.format(frameOut) + " -i " + '"' + file.toString() + '"' + 
					    filter + " -vframes 1 -q:v 0 -an -filter_complex hstack -y " + '"'  + fileOut + '"');	
					}
					else
						FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + filter + " -vframes 1 -q:v 0 -an -y " + '"'  + fileOut + '"');			
					
					do{
						Thread.sleep(100);
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
		
		btnPreview = new JButton(Shutter.language.getProperty("preview"));
		btnPreview.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnPreview.setMargin(new Insets(0,0,0,0));
		btnPreview.setBounds(frame.getSize().width - 6 - 130 - 4, topPanel.getSize().height + 10, 130, 21);		
		frame.getContentPane().add(btnPreview);
		
		btnPreview.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {	
				
				try {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFMPEG.fonctionInOut();
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.isRunning);

					String channels = "";
					String videoOutput = "";
					String audioOutput = "";
					if (FFPROBE.audioOnly) {
						if (FFPROBE.channels > 1) {
							int i;
							for (i = 0; i < FFPROBE.channels; i++) {
								channels += "[0:a:" + i + "]showvolume=f=0.001:b=4:w=720:h=12[a" + i + "];";
								audioOutput += "[a" + i + "]";
							}
							audioOutput = channels + audioOutput + "vstack=" + i + "[volume]" + '"' + " -map " + '"'
									+ "[volume]" + '"';

						} else if (FFPROBE.channels <= 1)
							audioOutput = "[0:a:0]showvolume=f=0.001:b=4:w=720:h=12[volume]" + '"' + " -map " + '"'
									+ "[volume]" + '"';
					} else {
						if (FFPROBE.channels > 1) {
							int i;
							for (i = 0; i < FFPROBE.channels; i++) {
								channels += "[0:a:" + i + "]showvolume=f=0.001:b=4:w=1080:h=12[a" + i + "];";
								audioOutput += "[a" + i + "]";
							}
							audioOutput += "vstack=" + (i + 1) + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
						} else if (FFPROBE.channels == 1) {
							channels = "[0:a:0]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
							audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
						}

						// On ajoute la vidéo
						videoOutput = "[0:v]scale=1080:-1[v]" + ";" + channels + "[v]";

						if (FFPROBE.channels == 0) {
							videoOutput = "scale=1080:-1" + '"';
							audioOutput = "";
						}

					}
					
					//Fichier
					File fichier = new File(videoPath);
					
					final String extension =  videoPath.substring(videoPath.lastIndexOf("."));
					String sortie = new File(videoPath).getParent();
					
					//Mode concat
					String concat = "";
					if (comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
					{
						concat = FFMPEG.setConcat(fichier, sortie);			
						fichier = new File(sortie.replace("\\", "/") + "/" + fichier.getName().replace(extension, ".txt"));
					}

					String cmd = " -filter_complex " + '"' + videoOutput + audioOutput + " -c:v rawvideo -map a? -f nut pipe:play |";

					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));						
					FFMPEG.toFFPLAY(FFMPEG.inPoint + concat + " -i " + '"' + fichier + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd);

					if (FFMPEG.isRunning) {
						do {
							if (FFMPEG.error) {
								JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("cantReadFile"),
										Shutter.language.getProperty("menuItemVisualiser"), JOptionPane.PLAIN_MESSAGE,
										JOptionPane.ERROR_MESSAGE);
								break;
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							}
						} while (FFMPEG.isRunning || FFMPEG.error);
					}
					
					//Mode concat
					if (comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
					{		
						File listeBAB = new File(sortie.replace("\\", "/") + "/" + fichier.getName().replace(extension, ".txt"));			
						listeBAB.delete();
					}

					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();

					Shutter.enableAll();
					Shutter.progressBar1.setValue(0);
									
				} catch (InterruptedException e1) {}
			}        			
		});
		        		
  		lblVideo = new JLabel();    
  		lblVideo.setVisible(false);
		lblVideo.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblVideo.setForeground(Utils.themeColor);
		lblVideo.setHorizontalAlignment(SwingConstants.CENTER);
		lblVideo.setBounds(btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6, topPanel.getSize().height + 12, frame.getSize().width - (btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6 + btnPreview.getSize().width + 12), 16);        		
		frame.getContentPane().add(lblVideo);
		
		
		setMedia();	
		
		do{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		}while (FFPROBE.isRunning);
		
		Lecteurs();        		
		boutons();
		grpIn();
		grpOut();
		sliders();
				
		if (FFPROBE.audioOnly)
			btnCaptureIn.setEnabled(false);
		else
			btnCaptureIn.setEnabled(true);
		
		lblDuree = new JLabel();
		lblDuree.setHorizontalAlignment(SwingConstants.CENTER);
		lblDuree.setFont(new Font("Montserrat", Font.PLAIN, 13));
		lblDuree.setForeground(Utils.themeColor);
		lblDuree.setBounds(0, frame.getSize().height - 16 - 12, frame.getWidth(), 16);   		
		frame.getContentPane().add(lblDuree);
		        		        		
		dureeTotale();
		
		drag = false;
		
		frame.addMouseMotionListener(new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (frame.getSize().width >= 636 &&  frame.getSize().height >= 619 && e.getX() >= 636 && e.getY() >= 619 && drag)
				{
					frame.setExtendedState(JFrame.NORMAL);
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
					
    				if (mediaPlayerComponentLeft != null)
    				{
    					mediaPlayerComponentLeft.getMediaPlayer().stop();
    					playerLeft.remove(videoSurfaceLeft);
    					playerLeft.revalidate();
    					playerLeft.repaint();
    				}
					
    				if (mediaPlayerComponentRight != null)
    				{
    					mediaPlayerComponentRight.getMediaPlayer().stop();
    					playerRight.remove(videoSurfaceRight);
    					playerRight.revalidate();
    					playerRight.repaint();
    				}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {	

				if (drag)
				{
					if (frame.getSize().width < 646 &&  frame.getSize().height < 629)
					{
						frame.setExtendedState(JFrame.NORMAL);
						frame.setSize(646, 629);	
						resizeAll();    
					}
					
					if (mediaPlayerComponentLeft != null)
					{
						videoSurfaceLeft.setBounds(0,0, width,height);
						if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null)
							updateVideoSurfaceLeft();
					}
				
					if (mediaPlayerComponentRight != null)
					{
						videoSurfaceRight.setBounds(0,0, width,height);
						if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null)
							updateVideoSurfaceRight();
						
    					if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() == null)
    					{
    						mediaPlayerComponentRight.getMediaPlayer().playMedia(videoPath);						
    						do {
    							try {
									Thread.sleep(10);
								} catch (InterruptedException e1) {}
    						} while(mediaPlayerComponentRight.getMediaPlayer().isPlaying() == false);					
    						mediaPlayerComponentRight.getMediaPlayer().pause();
						
    						mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(sliderOut.getValue()));
						
    						rightPlay.setText(Shutter.language.getProperty("btnResume"));		
    					}	
					}       				

				}
				
				drag = false;
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
				if (isFullScreen) {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}   		
				}
				
				
				frame.toFront();
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			File video = new File(Shutter.liste.firstElement());
			String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
			
			Subtitles.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
			
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		frame.setLocation(frame.getLocation().x , dim.height/3 - frame.getHeight()/2);

    		/*//On défini le zoom par defaut	
    		while ((int) (FFPROBE.dureeTotale - (float)1000/Subtitles.zoom) > 0 && Subtitles.zoom > (double) 0.02) {
    			 Subtitles.zoom -=  (double) 0.01;
    		 } 
    		
			DecimalFormat numberFormat = new DecimalFormat("0.0");
			if (Subtitles.zoom < 0.1)
				numberFormat = new DecimalFormat("0.00");	
			
			Subtitles.zoom = Double.parseDouble(numberFormat.format(Subtitles.zoom).replace(",", "."));*/

			new Subtitles(dim.width/2-500,frame.getLocation().y + frame.getHeight() + 7);
			
			sliderVolume.setValue(sliderVolume.getMaximum());
		}
		
		width = playerLeft.getSize().width;
		height = playerLeft.getSize().height;
		
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
				
				
				if (mediaPlayerComponentLeft != null)
				{
					if(mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null)
					{
						if (e.getKeyCode() == KeyEvent.VK_LEFT)
		            		leftPrevious.doClick();	
						if (e.getKeyCode() == KeyEvent.VK_RIGHT)
							leftNext.doClick();	
					}
				}
				if (mediaPlayerComponentRight != null)
				{
					if(mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null)
					{
						if (e.getKeyCode() == KeyEvent.VK_UP)
							rightNext.doClick();	
						if (e.getKeyCode() == KeyEvent.VK_DOWN)
							rightPrevious.doClick();
					}
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
		
		leftPlay.doClick();	
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
								Thread.sleep(100);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
					}
					
					lblVideo.setText(new File(videoPath).getName());
					lblVideo.setVisible(true);

					Thread addWaveform = new Thread(new Runnable()
					{
						@Override
						public void run() {
							do {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while (mediaPlayerComponentLeft == null && frame.isVisible() == false);
							
							//Si fichier audio seulement ou Sous titrage								
							if (FFPROBE.hasAudio)
							{
								String border = "";
								long size = (long) (FFPROBE.totalLength / 10);
								if (size > 549944)
									size = 549944;
								if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
									border = ",drawbox=c=0x505050";
									
								FFMPEG.run(" -i " + '"' + Shutter.liste.firstElement() + '"'
								+ " -f lavfi -i color=s=" + size + "x360:c=0x323232" + border
								+ " -filter:a aresample=8000 -filter_complex " + '"' + "[0:a]aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=green|green[w];[1:v][w]overlay" + '"' 
								+ " -pix_fmt rgb24 -frames:v 1 -y " + '"' + waveform + '"');  									
			
								Shutter.enableAll();							
								
								if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
									Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
								else
									Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
								
								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {}									
								} while (waveform.exists() == false);

								frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
								try {		
									waveformLeft.setVisible(false);
									waveformRight.setVisible(false);
									
									float largeurMax = (float) frame.getSize().width / frame.getSize().height;
									int largeur = (int) ((float) frame.getSize().width / largeurMax);
									
									Image imageBMP = ImageIO.read(waveform);
									ImageIcon resizedWaveform;
									if (sliderOut.getValue() == sliderOut.getMaximum())
									{
										if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));	
										else if (FFPROBE.audioOnly)
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / ratio), Image.SCALE_AREA_AVERAGING));	
										else
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
									}
									else
									{
										if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));
										else if (FFPROBE.audioOnly)
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(frame.getSize().width / 2 - 6, (int) ((float) (frame.getSize().width / 2 - 6) / ratio), Image.SCALE_AREA_AVERAGING));			
										else
											resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
									}
									
									waveformLeft.setIcon(resizedWaveform);
									waveformRight.setIcon(resizedWaveform);
									
									if (FFPROBE.audioOnly)
									{
										waveformLeft.setBounds(playerLeft.getBounds());
										waveformRight.setBounds(playerRight.getBounds());
									}
									else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
									{
										waveformLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / 12));	
										waveformLeft.setLocation((int) (float) (frame.getSize().width - waveformLeft.getSize().width) / 2, frame.getSize().height - 95);
									}
									else
									{
										waveformLeft.setSize(sliderIn.getWidth(), grpIn.getHeight());	
										waveformLeft.setLocation(sliderIn.getLocation());
										waveformRight.setSize(sliderOut.getWidth(), grpIn.getHeight());	
										waveformRight.setLocation(sliderOut.getLocation());
									}		
									
									panelWaveformLeft.setBounds((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0, 2, waveformLeft.getSize().height);
									if (sliderOut.getValue() != sliderOut.getMaximum() && panelWaveformRight != null)
										panelWaveformRight.setBounds((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0, 2, waveformRight.getSize().height);
									else if (panelWaveformRight != null)
									{
										panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
										sliderOut.setValue(sliderOut.getMaximum());
										
										//Injection du temps 
										long temps = FFPROBE.totalLength;
										NumberFormat formatter = new DecimalFormat("00");
								        caseOutH.setText(formatter.format((temps/1000) / 3600));
								        caseOutM.setText(formatter.format( ((temps/1000) / 60) % 60) );
								        caseOutS.setText(formatter.format((temps/1000) % 60));				        
								        caseOutF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
									}
									
									waveformLeft.setVisible(true);
									waveformRight.setVisible(true);
									
									if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && FFPROBE.audioOnly == false)
									{
										sliderIn.setVisible(false);
										sliderOut.setVisible(false);
									}
									
								} catch (Exception e) {}
								finally {
									frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							}
						}
						
					});
					addWaveform.start();				
				}			
			}
			else
			{
				
				leftStop.doClick();
				
				videoPath = null;
				lblVideo.setVisible(false);
				mediaPlayerComponentLeft.getMediaPlayer().stop();
				sliderIn.setValue(0);
				playerLeft.remove(videoSurfaceLeft);
				playerLeft.revalidate();
				playerLeft.repaint();
				
				mediaPlayerComponentRight.getMediaPlayer().stop();
				sliderOut.setValue(sliderOut.getMaximum());
				playerRight.remove(videoSurfaceRight);
				playerRight.revalidate();
				playerRight.repaint();  						
				
				leftPlay.setText(Shutter.language.getProperty("btnPlay"));
				rightPlay.setText(Shutter.language.getProperty("btnPlay"));				
			}
			
			Thread sliders = new Thread(new Runnable()
			{
				@Override
				public void run() {
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (frame.isVisible() == false);
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
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

	private void addVideo() {
    	    	
		videoSurfaceLeft = new VideoSurfacePanelLeft();
		videoSurfaceLeft.setBounds(0,0, width,height);
		    		
		playerLeft.add(videoSurfaceLeft);
		playerLeft.revalidate();
		playerLeft.repaint();
		
		videoSurfaceRight = new VideoSurfacePanelRight();
		videoSurfaceRight.setBounds(0,0, width,height);
		
		playerRight.add(videoSurfaceRight);
		playerRight.revalidate();
		playerRight.repaint();
	   			
		imageLeft = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration()
        .createCompatibleImage(width, height);
		
		imageRight = GraphicsEnvironment
	            .getLocalGraphicsEnvironment()
	            .getDefaultScreenDevice()
	            .getDefaultConfiguration()
	            .createCompatibleImage(width, height);
            
        BufferFormatCallback bufferFormatCallbackLeft = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(width, height);
            }
        };
        
        BufferFormatCallback bufferFormatCallbackRight = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(width, height);
            }
        };
        
        mediaPlayerComponentLeft = new DirectMediaPlayerComponent(bufferFormatCallbackLeft) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new RenderCallbackAdapterLeft();
            }
            
            @Override
            public void playing(MediaPlayer mediaPlayer) {   
            	if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))	
            		VideoPlayer.mediaPlayerComponentLeft.getMediaPlayer().setSubTitleFile(Subtitles.srt);
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {            	
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());  
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            	calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
            }
        };
        
        mediaPlayerComponentRight = new DirectMediaPlayerComponent(bufferFormatCallbackRight) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new RenderCallbackAdapterRight();
            }
            
            @Override
            public void playing(MediaPlayer mediaPlayer) {       
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
            	calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());  
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            	calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
            }

        };

		mediaPlayerComponentLeft.getMediaPlayer().setVolume(50);	
		mediaPlayerComponentRight.getMediaPlayer().setVolume(50);	
    }
 
    /*
     * Affichage Video 
     */
    
	//Affichage de la vidéo de gauche 
	@SuppressWarnings("serial")
	private class VideoSurfacePanelLeft extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(imageLeft, null, 0, 0);  
            
            
            if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
            	Subtitles.refreshData();
            		
            if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && mediaPlayerComponentLeft.getMediaPlayer().isPlaying() == false)
            {          	            	
        		//Enlève les autosubs de VLC
        		try {
        			VideoPlayer.mediaPlayerComponentLeft.getMediaPlayer().setSpu(VideoPlayer.mediaPlayerComponentLeft.getMediaPlayer().getSpuDescriptions().get(0).id());
        		} catch (Exception er) {}           	
            	
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    
                //On sépare les lignes
                String text[] = Subtitles.txtSubtitles.getText().replace("<i>", "").replace("</i>", "").replace("<b>", "").replace("</b>", "").split("\\r?\\n");
                   
                
                if (Subtitles.txtSubtitles.getText().contains("<i>") && Subtitles.txtSubtitles.getText().contains("<b>"))
                	g2.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, (int) Math.floor(height/16))); 
                else if (Subtitles.txtSubtitles.getText().contains("<i>"))
                	g2.setFont(new Font("SansSerif", Font.ITALIC, (int) Math.floor(height/16))); 
                else if (Subtitles.txtSubtitles.getText().contains("<b>"))
                	g2.setFont(new Font("SansSerif", Font.BOLD, (int) Math.floor(height/16))); 
                else
                	g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.floor(height/16))); 
                	
                FontMetrics metrics = g.getFontMetrics(g2.getFont());
                
                int x = videoSurfaceLeft.getX() + (videoSurfaceLeft.getWidth() - metrics.stringWidth(text[0])) / 2;                                	
                int y = height - (int) (height/24);
                
                if (text.length > 1 && text[1].length() > 0)
                {                                	                	
                	y = height - (int) (height/9.5);                	
                	g2.setColor(Color.BLACK);
                	g2.drawString(text[0], ShiftWest(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[0], ShiftWest(x, 1), ShiftSouth(y, 1));
                	g2.drawString(text[0], ShiftEast(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[0], ShiftEast(x, 1), ShiftSouth(y, 1));
                	g2.setColor(Color.WHITE);
                	g2.drawString(text[0], x, y);
	 	            
	 	            x = videoSurfaceLeft.getX() + (videoSurfaceLeft.getWidth() - metrics.stringWidth(text[1])) / 2;
	 	            y = height - (int) (height/24);
                	
                	g2.setColor(Color.BLACK);
                	g2.drawString(text[1], ShiftWest(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[1], ShiftWest(x, 1), ShiftSouth(y, 1));
                	g2.drawString(text[1], ShiftEast(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[1], ShiftEast(x, 1), ShiftSouth(y, 1));
                	g2.setColor(Color.WHITE);
                	g2.drawString(text[1], x, y);
                }
                else if (text[0].length() > 0)
                {
                	g2.setColor(Color.BLACK);
                	g2.drawString(text[0], ShiftWest(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[0], ShiftWest(x, 1), ShiftSouth(y, 1));
                	g2.drawString(text[0], ShiftEast(x, 1), ShiftNorth(y, 1));
                	g2.drawString(text[0], ShiftEast(x, 1), ShiftSouth(y, 1));
                	g2.setColor(Color.WHITE);
                	g2.drawString(text[0], x, y);
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
    }

    private class RenderCallbackAdapterLeft extends RenderCallbackAdapter {

        private RenderCallbackAdapterLeft() {
            super(new int[width * height]);
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
        	imageLeft.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            videoSurfaceLeft.repaint();
        }
    }
    
	//Affichage de la vidéo de droite 
	@SuppressWarnings("serial")
	private class VideoSurfacePanelRight extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(imageRight, null, 0, 0);
         }
    }

    private class RenderCallbackAdapterRight extends RenderCallbackAdapter {

        private RenderCallbackAdapterRight() {
            super(new int[width * height]);
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
        	imageRight.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            videoSurfaceRight.repaint();
        }
    }
    
    /*
     * Affichage Video 
     */
    
    private void boutons() {		    	
		rightPrevious = new JButton("<");
		rightPrevious.setFont(new Font("Montserrat", Font.PLAIN, 12));
		rightPrevious.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 - 21 - 4, playerRight.getLocation().y + playerRight.getSize().height + 10, 22, 21);		
		rightPrevious.setVisible(false);
		frame.getContentPane().add(rightPrevious);
		
		rightPrevious.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				calculOut = true;
				frameControl = true;
				if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null)
				{
					if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
						rightPlay.doClick();
					timeOut -= 1000 / FFPROBE.currentFPS;
					mediaPlayerComponentRight.getMediaPlayer().setTime(timeOut);								
					rightPlay.setText(Shutter.language.getProperty("btnResume"));
					
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();					
				}				
			}
			
		});
		
		rightNext = new JButton(">");
		rightNext.setFont(new Font("Montserrat", Font.PLAIN, 12));
		rightNext.setBounds(playerRight.getLocation().x + playerRight.getSize().width / 2 + 4, rightPrevious.getLocation().y, 22, 21);	
		rightNext.setVisible(false);
		frame.getContentPane().add(rightNext);
		
		rightNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				calculOut = true;
				frameControl = true;
				if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null)
				{
					if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
						rightPlay.doClick();
					timeOut += 1000 / FFPROBE.currentFPS;
					mediaPlayerComponentRight.getMediaPlayer().setTime(timeOut);
					rightPlay.setText(Shutter.language.getProperty("btnResume"));
										
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();
				}				
			}
			
		});
		
		rightPlay = new JButton(Shutter.language.getProperty("btnPlay"));
		rightPlay.setFont(new Font("Montserrat", Font.PLAIN, 12));
		rightPlay.setMargin(new Insets(0,0,0,0));
		rightPlay.setBounds(rightPrevious.getLocation().x - 80 - 4, rightPrevious.getLocation().y, 80, 21);		
		rightPlay.setVisible(false);
		frame.getContentPane().add(rightPlay);
		
		rightPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {	
				calculOut = true;
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					{
					mediaPlayerComponentRight.getMediaPlayer().pause();
						rightPlay.setText(Shutter.language.getProperty("btnResume"));
					}
				else if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{
					mediaPlayerComponentRight.getMediaPlayer().play();
					rightPlay.setText(Shutter.language.getProperty("btnPause"));
				}
			}
			
		});
		
		rightStop = new JButton(Shutter.language.getProperty("btnCancel"));
		rightStop.setFont(new Font("Montserrat", Font.PLAIN, 12));
		rightStop.setMargin(new Insets(0,0,0,0));
		rightStop.setBounds(rightNext.getLocation().x + rightNext.getSize().width + 4, rightNext.getLocation().y, 80, 21);	
		rightStop.setVisible(false);
		frame.getContentPane().add(rightStop);		
		
		rightStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null)
				{
					if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
					{
						rightPlay.doClick();
						
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (mediaPlayerComponentRight.getMediaPlayer().isPlaying());
					}

					
					mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(FFPROBE.totalLength));
					if (panelWaveformRight != null)
						panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
					sliderOut.setValue(sliderOut.getMaximum());
				}
				rightPlay.setText(Shutter.language.getProperty("btnPlay"));
							
				mediaPlayerComponentLeft.getMediaPlayer().stop();
				playerLeft.remove(videoSurfaceLeft);
				playerLeft.revalidate();
				playerLeft.repaint();
				
				mediaPlayerComponentRight.getMediaPlayer().stop();
				playerRight.remove(videoSurfaceRight);
				playerRight.revalidate();
				playerRight.repaint();

				resizeAll();	
				updateVideoSurfaceLeft();
				updateVideoSurfaceRight();
				
				videoSurfaceRight.setVisible(false);
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
		        caseOutF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));				
			}			
			
		});
 	
		leftPrevious = new JButton("<");
		leftPrevious.setFont(new Font("Montserrat", Font.PLAIN, 12));
		leftPrevious.setEnabled(false);
		leftPrevious.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 - 21 - 4, playerLeft.getLocation().y + playerLeft.getSize().height + 10, 22, 21);		
		frame.getContentPane().add(leftPrevious);
				
		leftPrevious.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				calculIn = true;
				frameControl = true;
				if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null)
				{
					if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
						leftPlay.doClick();
					timeIn -= 1000 / FFPROBE.currentFPS;
					
					mediaPlayerComponentLeft.getMediaPlayer().setTime(timeIn);
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
				}		
								
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());  
				playSoundIn();
			}
			
		});
		
		leftNext = new JButton(">");
		leftNext.setFont(new Font("Montserrat", Font.PLAIN, 12));
		leftNext.setEnabled(false);
		leftNext.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width / 2 + 4, leftPrevious.getLocation().y, 22, 21);		
		frame.getContentPane().add(leftNext);
		
		leftNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				calculIn = true;
				frameControl = true;
				if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null)
				{
					if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
						leftPlay.doClick();
					
					timeIn += 1000 / FFPROBE.currentFPS;
					
					mediaPlayerComponentLeft.getMediaPlayer().setTime(timeIn);
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
					
					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime()); 
					playSoundIn();
				}	
			}
			
		});
		
		leftPlay = new JButton(Shutter.language.getProperty("btnPlay"));
		leftPlay.setFont(new Font("Montserrat", Font.PLAIN, 12));
		leftPlay.setMargin(new Insets(0,0,0,0));
		leftPlay.setBounds(leftPrevious.getLocation().x - 80 - 4, leftPrevious.getLocation().y, 80, 21);				
		frame.getContentPane().add(leftPlay);
		
		leftPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				calculIn = true;
				calculOut = true;
								
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")) && Shutter.liste.getSize() != 0) 
				{
					try {
						addVideo();	
						
						caseTcInterne.setEnabled(true);							
						leftPrevious.setEnabled(true);
						leftNext.setEnabled(true);
					} catch(Exception er) {
						Console.consoleFFMPEG.append(er.toString() + System.lineSeparator() + System.lineSeparator());
					}						
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					mediaPlayerComponentLeft.getMediaPlayer().setVolume(sliderVolume.getValue());
					
					mediaPlayerComponentLeft.getMediaPlayer().playMedia(videoPath);				   			
											
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
						
						if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState().toString().equals("libvlc_NothingSpecial"))
							break;
						
					} while(mediaPlayerComponentLeft.getMediaPlayer().isPlaying() == false || FFPROBE.isRunning);
					
					caseInH.setEnabled(true);
					caseInM.setEnabled(true);
					caseInS.setEnabled(true);
					caseInF.setEnabled(true);
					
					Utils.changeFrameVisibility(frame, false);
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					leftPlay.setText(Shutter.language.getProperty("btnPause"));
					sliderIn.setEnabled(true);
					
					sliderOut.setEnabled(true);
											
					//Injection du temps 
					long temps = formatTemps(FFPROBE.totalLength);
					NumberFormat formatter = new DecimalFormat("00");
			        caseOutH.setText(formatter.format((temps/1000) / 3600));
			        caseOutM.setText(formatter.format( ((temps/1000) / 60) % 60) );
			        caseOutS.setText(formatter.format((temps/1000) % 60));				        
			        caseOutF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
			        
					//On définit les valeurs des sliders
					sliderIn.setMaximum((int) formatTemps(FFPROBE.totalLength));
					sliderOut.setMaximum((int) formatTemps(FFPROBE.totalLength));
					sliderOut.setValue(sliderOut.getMaximum());
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							Subtitles.timelineScrollBar.setMaximum(sliderIn.getMaximum());
					
				}
				else if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					mediaPlayerComponentLeft.getMediaPlayer().pause();
					leftPlay.setText(Shutter.language.getProperty("btnResume"));
				}
				else if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))
				{
					mediaPlayerComponentLeft.getMediaPlayer().play();
					leftPlay.setText(Shutter.language.getProperty("btnPause"));
				}
								
			}
			
		});
		
		leftStop = new JButton(Shutter.language.getProperty("btnStop"));
		leftStop.setFont(new Font("Montserrat", Font.PLAIN, 12));
		leftStop.setMargin(new Insets(0,0,0,0));
		leftStop.setBounds(leftNext.getLocation().x + leftNext.getSize().width + 4, leftNext.getLocation().y, 80, 21);		
		frame.getContentPane().add(leftStop);		
		
		leftStop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null)
				{
					caseInH.setEnabled(false);
					caseInM.setEnabled(false);
					caseInS.setEnabled(false);
					caseInF.setEnabled(false);
					
					mediaPlayerComponentLeft.getMediaPlayer().stop();
					sliderIn.setValue(0);
					playerLeft.remove(videoSurfaceLeft);
					playerLeft.revalidate();
					playerLeft.repaint();
					
					mediaPlayerComponentRight.getMediaPlayer().stop();
					sliderOut.setValue(sliderOut.getMaximum());
					playerRight.remove(videoSurfaceRight);
					playerRight.revalidate();
					playerRight.repaint();
					
					resizeAll();
					
					videoSurfaceRight.setVisible(false);
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
					
					leftPlay.setText(Shutter.language.getProperty("btnPlay"));
					
					caseInH.setText("00");
					caseInM.setText("00");
					caseInS.setText("00");
					caseInF.setText("00");
					caseOutH.setText("00");
					caseOutH.setText("00");
					caseOutH.setText("00");
					caseOutH.setText("00");
					
					caseTcInterne.setEnabled(false);	
					caseTcInterne.setSelected(false);
					leftPrevious.setEnabled(false);
					leftNext.setEnabled(false);
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						Subtitles.actualSubOut = 0;	
				}
				
			}
			
			
		});
  
    }
    
	protected void playSoundIn() {
		if (casePlaySound.isSelected())
		{
			boolean tcInterne = false;
			try {
				if (caseTcInterne.isSelected())
				{
					caseTcInterne.doClick();
					tcInterne = true;
				}
				
				File file = new File (Shutter.liste.firstElement());
				FFMPEG.fonctionInOut();	
				NumberFormat toMs = new DecimalFormat("000");
				
				if (FFPLAY.isRunning)
					FFPLAY.process.destroy();
				
				String Ms; 
				if (caseInF.getText().equals("24"))
					Ms = "000";
				else
					Ms = toMs.format((Integer.parseInt(caseInF.getText())) * (1000 / FFPROBE.currentFPS));
				
				FFPLAY.audioOnly("-ss " + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + "." + Ms + " -i " + '"' + file.toString() + '"' + " -t 00:00:00.080 -vn -c:a pcm_s16le -af volume=" + (double) sliderVolume.getValue() / 100 + " -f nut pipe:play");
				
				//FFPLAY.run("-ss " + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + "." + Ms + " -nodisp -autoexit -vn -af volume=" + (double) sliderVolume.getValue() / 100 + " -t 00:00:00.080" + " -i " + '"' + file.toString() + '"');		
				
				if (tcInterne)
					caseTcInterne.doClick();
				
			} catch (Exception e1) {}	
		}
	}
	
	protected void playSoundOut() {
		if (casePlaySound.isSelected())
		{
			boolean tcInterne = false;
			try {
				if (caseTcInterne.isSelected())
				{
					caseTcInterne.doClick();
					tcInterne = true;
				}
				
				File file = new File (Shutter.liste.firstElement());
				FFMPEG.fonctionInOut();	
				NumberFormat toMs = new DecimalFormat("000");
				
				if (FFPLAY.isRunning)
					FFPLAY.process.destroy();
				
				String Ms; 
				if (caseOutF.getText().equals("24"))
					Ms = "000";
				else
					Ms = toMs.format((Integer.parseInt(caseOutF.getText())) * (1000 / FFPROBE.currentFPS));
				
				FFPLAY.audioOnly("-ss " + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + "." + Ms + " -i " + '"' + file.toString() + '"' + " -t 00:00:00.080 -vn -c:a pcm_s16le -af volume=" + (double) sliderVolume.getValue() / 100 + " -f nut pipe:play");
				
				//FFPLAY.run("-ss " + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + "." + Ms + " -nodisp -autoexit -vn -af volume=" + (double) sliderVolume.getValue() / 100 + " -t 00:00:00.080" + " -i " + '"' + file.toString() + '"');		
				
				if (tcInterne)
					caseTcInterne.doClick();
				
			} catch (InterruptedException e1) {}	
		}
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
			calculIn = true;
				if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null && sliderInChange)
				{		
					if (sliderIn.getValue() > 0)
						mediaPlayerComponentLeft.getMediaPlayer().setTime(formatTemps(sliderIn.getValue()));	
					else	
						mediaPlayerComponentLeft.getMediaPlayer().setTime(0);	
							
					if (sliderIn.getValue() > sliderOut.getValue())
					{
						try {
							sliderOut.setValue(sliderIn.getValue());
							panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
							mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(sliderOut.getValue()));
						}catch (Exception e1){}
					}							
						
					if (FFPROBE.hasAudio && panelWaveformLeft != null)
					{
		        		panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);	        		
					}						
				}
				
				//Evite de causer un bug pendant la lecture	
				if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))		
					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime()); 
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
				sliderInChange = false;
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					leftPlay.doClick();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {}
					leftPlay.doClick();
				}
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
				calculOut = true;
				
				if (rightPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
					rightPlay.doClick();
				
					if (sliderOut.getValue() != sliderOut.getMaximum())
					{
						if (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
						{
							leftPlay.setText(Shutter.language.getProperty("btnResume"));
							caseInH.setEnabled(true);
							caseInM.setEnabled(true);
							caseInS.setEnabled(true);
							caseInF.setEnabled(true);
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

							if (mediaPlayerComponentRight.getMediaPlayer().getTime() == -1 || mediaPlayerComponentRight.getMediaPlayer().getTime() == FFPROBE.totalLength) // Seul moyen pour effectuer l'action seulement quand le playerRight est invisible
							{
								
								if (mediaPlayerComponentLeft != null)
								{
									mediaPlayerComponentLeft.getMediaPlayer().stop();
									playerLeft.remove(videoSurfaceLeft);
									playerLeft.revalidate();
									playerLeft.repaint();
								}
						
								if (mediaPlayerComponentRight != null)
								{
									mediaPlayerComponentRight.getMediaPlayer().stop();
									playerRight.remove(videoSurfaceRight);
									playerRight.revalidate();
									playerRight.repaint();
								}

								resizeAll();	
								if (mediaPlayerComponentLeft != null)
									updateVideoSurfaceLeft();
								if (mediaPlayerComponentRight != null)
									updateVideoSurfaceRight();
								
							}//End if visible
							
							if (FFPROBE.hasAudio && waveform.exists())
							{
								if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
									panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);
								else
								{
									waveformRight.setVisible(true);
									panelWaveformRight.setVisible(true);
									panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);
					 	  			panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
								}								
							}
							
							if (FFPROBE.audioOnly == false)
								playerRight.setVisible(true);
						
							if (mediaPlayerComponentRight != null) {
								if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null && sliderOutChange)
									mediaPlayerComponentRight.getMediaPlayer().setTime(sliderOut.getValue());	
							}
							
							if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() == null)
							{
								mediaPlayerComponentRight.getMediaPlayer().playMedia(videoPath);						
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e1) {}
								} while(mediaPlayerComponentRight.getMediaPlayer().isPlaying() == false);					
								mediaPlayerComponentRight.getMediaPlayer().pause();
								
								mediaPlayerComponentRight.getMediaPlayer().setTime(mediaPlayerComponentLeft.getMediaPlayer().getTime());
								
								rightPlay.setText(Shutter.language.getProperty("btnResume"));		
								videoSurfaceRight.setVisible(true);
							}	
							
							if (sliderOut.getValue() < sliderIn.getValue())
							{								
								try {
									sliderIn.setValue(sliderOut.getValue());
									panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);								
									mediaPlayerComponentLeft.getMediaPlayer().setTime(formatTemps(sliderIn.getValue()));	
								}catch (Exception e1){}
							}
							
						}
												
						//Evite de causer un bug pendant la lecture	
						if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")))	
							calculDuTempsCasesOut(sliderOut.getValue());
						
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
				sliderOutChange = false;
				if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					rightPlay.doClick();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {}
					rightPlay.doClick();
				}
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
				if (mediaPlayerComponentLeft != null)
					mediaPlayerComponentLeft.getMediaPlayer().setVolume(sliderVolume.getValue());
				
				if (mediaPlayerComponentRight != null)
					mediaPlayerComponentRight.getMediaPlayer().setVolume(sliderVolume.getValue());
				
				Settings.videoPlayerVolume = sliderVolume.getValue();			
			}
			
		});
		
		lblVolume = new JLabel(Shutter.language.getProperty("volume") + " ");
		lblVolume.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblVolume.setBounds(sliderVolume.getLocation().x - 61, sliderIn.getLocation().y - 30, 61, 16);		
		frame.getContentPane().add(lblVolume);
		
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getSize().width, 52);
		
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		
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
				int reply = JOptionPane.showConfirmDialog(frame,
						Shutter.language.getProperty("areYouSure"),
						Shutter.language.getProperty("frameLecteurVideo"), JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);					
				if (accept && reply == JOptionPane.YES_OPTION) 
				{			
					if (waveform.exists())
						waveform.delete();
					
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
					
					Shutter.caseInAndOut.setSelected(false);
					if (mediaPlayerComponentLeft != null)
					mediaPlayerComponentLeft.getMediaPlayer().stop();
					if (mediaPlayerComponentRight != null)
					mediaPlayerComponentRight.getMediaPlayer().stop();
					frame.getContentPane().removeAll();
					Utils.changeFrameVisibility(frame, true);
					
					switch (Shutter.comboFonctions.getSelectedItem().toString())
					{
						case "H.264":
						case "H.265":
						case "WMV":
						case "MPEG":
						case "WebM":
						case "OGV":
						case "MJPEG":
						case "Xvid":
							FFPROBE.CalculH264();
							break;
					}
		    		
		    		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		    		{
						Shutter.caseInAndOut.setSelected(false);
		    			Subtitles.frame.dispose();
		    		}
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
		
		fullscreen = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/max2.png")));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 21,0,21, 21);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {			
				if (accept && frame.getExtendedState() == JFrame.NORMAL) {
					isFullScreen = true;
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}   		
				}
				else if (accept)
				{
					isFullScreen = false;
					frame.setExtendedState(JFrame.NORMAL);
	        		frame.setSize(646, 629);
	        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);		
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
					
				}
				
				if (mediaPlayerComponentLeft != null)
				{
					mediaPlayerComponentLeft.getMediaPlayer().stop();
					playerLeft.remove(videoSurfaceLeft);
					playerLeft.revalidate();
					playerLeft.repaint();
				}
				
				if (mediaPlayerComponentRight != null)
				{
					mediaPlayerComponentRight.getMediaPlayer().stop();
					playerRight.remove(videoSurfaceRight);
					playerRight.revalidate();
					playerRight.repaint();
				}

				resizeAll();	
				if (mediaPlayerComponentLeft != null)
						updateVideoSurfaceLeft();
				if (mediaPlayerComponentRight != null)
						updateVideoSurfaceRight();
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				fullscreen.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/max2.png"))));
				accept = false;
			}
			
			
		});
		
		reduce = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/reduce2.png")));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(fullscreen.getLocation().x - 21,0,21, 21);
			
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce3.png"))));
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
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce2.png"))));
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
					if (frame.getExtendedState() == JFrame.NORMAL) {
						frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}   		
					}
					else
					{
						frame.setExtendedState(JFrame.NORMAL);
		        		frame.setSize(646, 629);
		        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		        		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);		
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
						
					}
					
					if (mediaPlayerComponentLeft != null)
					{
						mediaPlayerComponentLeft.getMediaPlayer().stop();
						playerLeft.remove(videoSurfaceLeft);
						playerLeft.revalidate();
						playerLeft.repaint();
					}
					
					if (mediaPlayerComponentRight != null)
					{
						mediaPlayerComponentRight.getMediaPlayer().stop();
						playerRight.remove(videoSurfaceRight);
						playerRight.revalidate();
						playerRight.repaint();
					}

					resizeAll();	
					if (mediaPlayerComponentLeft != null)
							updateVideoSurfaceLeft();
					if (mediaPlayerComponentRight != null)
							updateVideoSurfaceRight();
				}
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;	
				
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX, MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
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

	private void Lecteurs() {		
		float largeurMax = (float) frame.getSize().width / frame.getSize().height;
		final int largeur = (int) ((float) frame.getSize().width / largeurMax);
		
		playerLeft = new JPanel();
		playerLeft.setLayout(null);

		playerLeft.setBackground(Color.BLACK);
		if (ratio < 1.77f)
			playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
		else
			playerLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / ratio));			
		playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, (int) (frame.getSize().height / 2 - (float) playerLeft.getSize().height / 1.55));	
		
		frame.getContentPane().add(playerLeft);
				
		playerRight = new JPanel();
		playerRight.setLayout(null);
		playerRight.setBackground(Color.BLACK);
		playerRight.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width, playerLeft.getLocation().y, playerLeft.getSize().width, playerLeft.getSize().height);		
		playerRight.setVisible(false);
		
		frame.getContentPane().add(playerRight);
				
		//Audio seulement
		if (FFPROBE.hasAudio || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
		{
			Thread addWaveform = new Thread(new Runnable() {
				public void run() {		
					try {
						do {
							Thread.sleep(100);
						}while (waveform.exists() == false);			
						
						Image imageBMP = ImageIO.read(waveform);
						ImageIcon resizedWaveform;
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));	
						else if (FFPROBE.audioOnly)
							resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / ratio), Image.SCALE_AREA_AVERAGING));	
						else
							resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
						
						waveformLeft = new JLabel(resizedWaveform);
						waveformRight = new JLabel(resizedWaveform);
						
						if (FFPROBE.audioOnly)
						{
							waveformLeft.setBounds(playerLeft.getBounds());
							waveformRight.setBounds(playerRight.getBounds());
						}
						else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						{
							waveformLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / 12));	
							waveformLeft.setLocation((int) (float) (frame.getSize().width - waveformLeft.getSize().width) / 2, frame.getSize().height - 95);
						}
						else
						{
							waveformLeft.setSize(sliderIn.getWidth(), grpIn.getHeight());	
							waveformLeft.setLocation(sliderIn.getLocation());
							waveformRight.setSize(sliderOut.getWidth(), grpIn.getHeight());	
							waveformRight.setLocation(sliderOut.getLocation());
						}	
						
						frame.getContentPane().add(waveformLeft);  
						frame.getContentPane().add(waveformRight);
						frame.getContentPane().repaint();
						
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
							waveformRight.setVisible(false);
										
						panelWaveformLeft = new JPanel();
						panelWaveformLeft.setBackground(Color.RED);
						panelWaveformLeft.setBounds((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) , 0, 2, waveformLeft.getSize().height);
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
							public void mouseReleased(MouseEvent arg0) {
								sliderInChange = false;
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
						
						if (sliderOut.getValue() == sliderOut.getMaximum())
							panelWaveformRight.setBounds(waveformRight.getSize().width - 2, 0, 2, waveformRight.getSize().height);
						else
							panelWaveformRight.setBounds((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) , 0, 2, waveformRight.getSize().height);
						waveformRight.add(panelWaveformRight);
						
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
								if (rightPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
									rightPlay.doClick();								

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
			
							@Override
							public void mouseReleased(MouseEvent arg0) {
								sliderOutChange = false;
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
				
						if (FFPROBE.audioOnly)
						{
							playerLeft.setVisible(false);
							waveformRight.setVisible(false);
							panelWaveformRight.setVisible(false);
						}
						else
							playerLeft.setVisible(true);
						
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && FFPROBE.audioOnly == false)
						{
							sliderIn.setVisible(false);
							sliderOut.setVisible(false);
						}
						
						panelWaveformLeft.repaint();		
						panelWaveformRight.repaint();	
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
		grpIn.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color (80,80,80), 1), Shutter.language.getProperty("grpIn") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpIn.setBackground(new Color(50, 50, 50));
		grpIn.setBounds(6, frame.getSize().height - 147, 156, 52);
		frame.getContentPane().add(grpIn);
		
		caseInH = new JTextField();
		caseInH.setName("caseInH");
		caseInH.setText("00");
		caseInH.setHorizontalAlignment(SwingConstants.CENTER);
		caseInH.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseInH.setColumns(10);
		caseInH.setBounds(6, 17, 36, 26);
		grpIn.add(caseInH);
		
		caseInM = new JTextField();
		caseInM.setName("caseInM");
		caseInM.setText("00");
		caseInM.setHorizontalAlignment(SwingConstants.CENTER);
		caseInM.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseInM.setColumns(10);
		caseInM.setBounds(42, 17, 36, 26);
		grpIn.add(caseInM);
		
		caseInS = new JTextField();
		caseInS.setName("caseInS");
		caseInS.setText("00");
		caseInS.setHorizontalAlignment(SwingConstants.CENTER);
		caseInS.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseInS.setColumns(10);
		caseInS.setBounds(78, 17, 36, 26);
		grpIn.add(caseInS);
		
		caseInF = new JTextField();
		caseInF.setName("caseInF");
		caseInF.setText("00");
		caseInF.setHorizontalAlignment(SwingConstants.CENTER);
		caseInF.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseInF.setColumns(10);
		caseInF.setBounds(114, 17, 36, 26);
		grpIn.add(caseInF);
		
		casePlaySound = new JRadioButton(Shutter.language.getProperty("casePlaySound"));
		casePlaySound.setBounds(14, grpIn.getLocation().y - 36, 195, 23);	
		casePlaySound.setFont(new Font("FreeSans", Font.PLAIN, 12));
		casePlaySound.setSelected(false);
		frame.getContentPane().add(casePlaySound);
		
		caseTcInterne = new JRadioButton(Shutter.language.getProperty("caseTcInterne"));
		caseTcInterne.setEnabled(false);
		caseTcInterne.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseTcInterne.setBounds(14, frame.getHeight() - 31, 195, 23);
		if (FFPROBE.audioOnly || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
			caseTcInterne.setVisible(false);
		else
			caseTcInterne.setVisible(true);
		frame.getContentPane().add(caseTcInterne);				
		
		comboMode.setName("comboMode");
		comboMode.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboMode.setMaximumRowCount(20);
		comboMode.setSize(76, 22);
		comboMode.setLocation(frame.getWidth() - comboMode.getWidth() - 12, frame.getSize().height - 16 - 12 - 1);
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
				
				dureeTotale();
			}
	
		});
		
		lblMode = new JLabel(Shutter.language.getProperty("mode"));
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setFont(new Font("FreeSans", Font.PLAIN, 13));
		lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblMode.getPreferredSize().width, 16);
		frame.getContentPane().add(lblMode);
				
		caseTcInterne.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseTcInterne.isSelected())
				{
					FFPROBE.Data(videoPath);
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (FFPROBE.isRunning);
					
					if (FFPROBE.timecode1 != "")
					{
						offset = Integer.valueOf(FFPROBE.timecode1) * 3600000
								+ Integer.valueOf(FFPROBE.timecode2) * 60000 
								+ Integer.valueOf(FFPROBE.timecode3) * 1000
								+ (Integer.valueOf(FFPROBE.timecode4) * (int) (1000 / FFPROBE.currentFPS));	
					}
				}
				else
					offset = 0;
				
				//On actualise les cases
    			NumberFormat formatter = new DecimalFormat("00");
    			
				long tempsIn = (long) (mediaPlayerComponentLeft.getMediaPlayer().getTime());
    			
    			if (tempsIn % (1000 / FFPROBE.currentFPS) != 0)
    				tempsIn = (long) (tempsIn + (1000 / FFPROBE.currentFPS)); //permet d'éviter le décalage d'une image
    			
				tempsIn += offset;
					    			
    			caseInH.setText(formatter.format(tempsIn / 3600000));
    			caseInM.setText(formatter.format((tempsIn / 60000) % 60));
    			caseInS.setText(formatter.format((tempsIn / 1000) % 60));    		
    			caseInF.setText(formatter.format((int) (tempsIn / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));    			

				long tempOut = (long) (mediaPlayerComponentLeft.getMediaPlayer().getTime());
    			
    			if (tempOut % (1000 / FFPROBE.currentFPS) != 0)
    				tempOut = (long) (tempOut + (1000 / FFPROBE.currentFPS)); //permet d'éviter le décalage d'une image
    			
    			tempOut += offset;

    			if (sliderOut.getValue() < sliderOut.getMaximum())
    				tempOut = (long) (mediaPlayerComponentRight.getMediaPlayer().getTime());
    			else
    				tempOut = (long) (FFPROBE.totalLength);
    			
    			tempOut += offset;

    			caseOutH.setText(formatter.format(tempOut / 3600000));
    			caseOutM.setText(formatter.format((tempOut / 60000) % 60));
    			caseOutS.setText(formatter.format((tempOut / 1000) % 60));    		
    			caseOutF.setText(formatter.format((int) (tempOut / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
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
					
					calculIn = true;

					timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
					mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
					playSoundIn();	
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
				
				calculIn = true;

				timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
				mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
				playSoundIn();	
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
					
					calculIn = true;

					timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
					mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));

					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
					playSoundIn();
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
				
				calculIn = true;

				timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
				mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
				playSoundIn();	
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
					
					calculIn = true;

					timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
					mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));

					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());					
					playSoundIn();					
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
				
				calculIn = true;

				timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
				mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
				playSoundIn();	
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
					
					calculIn = true;

					timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
					mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
					playSoundIn();	
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
				
				calculIn = true;

				timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
				mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());
				playSoundIn();	
			}
			
		});
	
	}
	
	private void grpOut(){
		grpOut = new JPanel();
		grpOut.setLayout(null);
		grpOut.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color (80,80,80), 1), Shutter.language.getProperty("grpOut") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpOut.setBackground(new Color(50, 50, 50));
		grpOut.setBounds(6, frame.getSize().height - 86, 156, 52);
		frame.getContentPane().add(grpOut);
		
		caseOutH = new JTextField();
		caseOutH.setName("caseOutH");
		caseOutH.setText("00");
		caseOutH.setEnabled(false);
		caseOutH.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutH.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseOutH.setColumns(10);
		caseOutH.setBounds(6, 17, 36, 26);
		grpOut.add(caseOutH);
				
		caseOutM = new JTextField();
		caseOutM.setName("caseOutM");
		caseOutM.setText("00");
		caseOutM.setEnabled(false);
		caseOutM.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutM.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseOutM.setColumns(10);
		caseOutM.setBounds(42, 17, 36, 26);
		grpOut.add(caseOutM);
		
		caseOutS = new JTextField();
		caseOutS.setName("caseOutS");
		caseOutS.setText("00");
		caseOutS.setEnabled(false);
		caseOutS.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutS.setFont(new Font("FreeSans", Font.PLAIN, 14));
		caseOutS.setColumns(10);
		caseOutS.setBounds(78, 17, 36, 26);
		grpOut.add(caseOutS);
		
		caseOutF = new JTextField();
		caseOutF.setName("caseOutF");
		caseOutF.setText("00");
		caseOutF.setEnabled(false);
		caseOutF.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutF.setFont(new Font("FreeSans", Font.PLAIN, 14));
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
					
					calculOut = true;

					timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
					mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();	
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
				
				calculOut = true;

				timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
				mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
				playSoundOut();	
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
					
					calculOut = true;

					timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
					mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();	
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
				
				calculOut = true;

				timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
				mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
				playSoundOut();	
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
					
					calculOut = true;

					timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
					mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();	
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
				
				calculOut = true;

				timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
				mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
				playSoundOut();	
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
					
					calculOut = true;

					timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
					mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
					
					calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
					playSoundOut();	
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
				
				calculOut = true;

				timeOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) (1000 / FFPROBE.currentFPS) - offset;
				mediaPlayerComponentRight.getMediaPlayer().setTime((long) (timeOut - (FFPROBE.currentFPS / 1000)));
				
				calculDuTempsCasesOut(mediaPlayerComponentRight.getMediaPlayer().getTime());
				playSoundOut();	
			}
			
		});	
	}

	//Resize
	private void resizeAll() {
		//topPanel
		topPanel.setBounds(0,0,frame.getSize().width, 52);
		topImage.setLocation(frame.getSize().width / 2 - topImage.getSize().width / 2, 0);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		fullscreen.setBounds(quit.getLocation().x - 21,0,21, 21);
		reduce.setBounds(fullscreen.getLocation().x - 21,0,21, 21); 		

		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);
		
		bottomImage.setBounds(0 ,0, frame.getSize().width, 52);
		
		//Boutons		
		btnCaptureIn.setBounds(8, topPanel.getSize().height + 10, 130, 21);		
		btnPreview.setBounds(frame.getSize().width - 6 - 130 - 4, topPanel.getSize().height + 10, 130, 21);		
		lblVideo.setBounds(btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6, topPanel.getSize().height + 12, frame.getSize().width - (btnCaptureIn.getLocation().x + btnCaptureIn.getSize().width + 6 + btnPreview.getSize().width + 12), 16);     		
		
		//Groupes boxes
		grpIn.setBounds(6, frame.getSize().height - 147, 156, 52);
		grpOut.setBounds(6, frame.getSize().height - 86, 156, 52);
		casePlaySound.setBounds(14, grpIn.getLocation().y - 36, 195, 23);
		caseTcInterne.setBounds(14, frame.getHeight() - 31, 195, 23);		
		comboMode.setLocation(frame.getWidth() - comboMode.getWidth() - 12, frame.getSize().height - 16 - 12 - 1);
		lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblMode.getPreferredSize().width, 16);
		
		//Sliders
		sliderIn.setBounds(grpIn.getLocation().x + grpIn.getSize().width + 12, grpIn.getLocation().y, frame.getSize().width - (grpIn.getLocation().x + grpIn.getSize().width + 12) - 12, 60); 
		sliderOut.setBounds(grpOut.getLocation().x + grpOut.getSize().width + 12, grpOut.getLocation().y, frame.getSize().width - (grpOut.getLocation().x + grpOut.getSize().width + 12) - 12, 60); 
		sliderVolume.setBounds(frame.getSize().width - 12 - 111, sliderIn.getLocation().y - 33, 111, 22);		
		lblVolume.setBounds(sliderVolume.getLocation().x - 61, sliderIn.getLocation().y - 30, 61, 16);	
		
		//Lecteurs 
		if (sliderOut.getValue() == sliderOut.getMaximum())
		{
			float largeurMax = (float) frame.getSize().width / frame.getSize().height;
			int largeur = (int) ((float) frame.getSize().width / largeurMax);
			if (ratio < 1.77f)
			{
				playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
				playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, playerLeft.getY());	
			}
			else
			{
				playerLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / ratio));			
				playerLeft.setLocation((int) (float) (frame.getSize().width - playerLeft.getSize().width) / 2, (int) (frame.getSize().height / 2 - (float) playerLeft.getSize().height / 1.55));	
			}
		}
		else
		{
			float largeurMax = (float) frame.getSize().width / frame.getSize().height;
			int positionY = (int) ((float) frame.getSize().height / (2.5 + largeurMax));
			if (ratio < 1.77f)
			{
				playerLeft.setSize((int) ((float) (frame.getHeight() - 269 - 12) * ratio), frame.getHeight() - 269 - 12);	
				playerLeft.setLocation(frame.getSize().width / 2 - playerLeft.getWidth(), playerLeft.getY());	
			}
			else
				playerLeft.setBounds(6, positionY, frame.getSize().width / 2 - 6, (int) ((float) (frame.getSize().width / 2 - 6) / ratio));	
		}
		
		playerRight.setBounds(playerLeft.getLocation().x + playerLeft.getSize().width, playerLeft.getLocation().y, playerLeft.getSize().width, playerLeft.getSize().height);
				
		//Waveforms
		if (FFPROBE.hasAudio && waveform.exists())
		{
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Thread addWaveform = new Thread(new Runnable() {
				public void run() {	
					try {		
						waveformLeft.setVisible(false);
						waveformRight.setVisible(false);
						
						float largeurMax = (float) frame.getSize().width / frame.getSize().height;
						int largeur = (int) ((float) frame.getSize().width / largeurMax);
						
						Image imageBMP = ImageIO.read(waveform);
						ImageIcon resizedWaveform;
						if (sliderOut.getValue() == sliderOut.getMaximum())
						{
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));	
							else if (FFPROBE.audioOnly)
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / ratio), Image.SCALE_AREA_AVERAGING));	
							else
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
						}
						else
						{
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(largeur - 12, (int) ((float) (largeur - 12) / 12), Image.SCALE_AREA_AVERAGING));
							else if (FFPROBE.audioOnly)
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(frame.getSize().width / 2 - 6, (int) ((float) (frame.getSize().width / 2 - 6) / ratio), Image.SCALE_AREA_AVERAGING));			
							else
								resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance(sliderIn.getWidth(), grpIn.getHeight(), Image.SCALE_AREA_AVERAGING));
						}
						
						waveformLeft.setIcon(resizedWaveform);
						waveformRight.setIcon(resizedWaveform);
						
						if (FFPROBE.audioOnly)
						{
							waveformLeft.setBounds(playerLeft.getBounds());
							waveformRight.setBounds(playerRight.getBounds());
						}
						else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						{
							waveformLeft.setSize(largeur - 12, (int) ((float) (largeur - 12) / 12));	
							waveformLeft.setLocation((int) (float) (frame.getSize().width - waveformLeft.getSize().width) / 2, frame.getSize().height - 95);
						}
						else
						{
							waveformLeft.setSize(sliderIn.getWidth(), grpIn.getHeight());	
							waveformLeft.setLocation(sliderIn.getLocation());
							waveformRight.setSize(sliderOut.getWidth(), grpIn.getHeight());	
							waveformRight.setLocation(sliderOut.getLocation());
						}		
						
						panelWaveformLeft.setBounds((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0, 2, waveformLeft.getSize().height);
						if (sliderOut.getValue() != sliderOut.getMaximum() && panelWaveformRight != null)
							panelWaveformRight.setBounds((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0, 2, waveformRight.getSize().height);
						else if (panelWaveformRight != null)
						{
							panelWaveformRight.setLocation(waveformRight.getWidth() - 2, panelWaveformRight.getLocation().y);	
							sliderOut.setValue(sliderOut.getMaximum());
							
							//Injection du temps 
							long temps = FFPROBE.totalLength;
							NumberFormat formatter = new DecimalFormat("00");
					        caseOutH.setText(formatter.format((temps/1000) / 3600));
					        caseOutM.setText(formatter.format( ((temps/1000) / 60) % 60) );
					        caseOutS.setText(formatter.format((temps/1000) % 60));				        
					        caseOutF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
						}
						
						waveformLeft.setVisible(true);
						waveformRight.setVisible(true);
						
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && FFPROBE.audioOnly == false)
						{
							sliderIn.setVisible(false);
							sliderOut.setVisible(false);
						}
						
					} catch (Exception e) {}
					finally {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			});
			addWaveform.start();
		}
		
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
		
		width = playerLeft.getSize().width;
		height = playerLeft.getSize().height;
		
		title.setBounds(0, 0, frame.getWidth(), 52);
						  
	}
	
	public static void dureeTotale() {	
		long totalIn =  (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS));
		long totalOut = (long) (Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
		
		long sommeTotal = totalOut - totalIn;
		
		if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
			sommeTotal = FFPROBE.totalLength - sommeTotal;	
		
		dureeHeures = (int) (sommeTotal / 3600000);
		dureeMinutes = (int) (sommeTotal / 60000 % 60);
		dureeSecondes = (int) (sommeTotal / 1000 % 60);
		dureeImages = (int) (sommeTotal  / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS);
		
		lblDuree.setText(Shutter.language.getProperty("lblDuree") + " " + dureeHeures + "h " + dureeMinutes +"min " + dureeSecondes + "sec " + dureeImages + "i" + " | " + Shutter.language.getProperty("lblTotalFrames") + " " + ((int) (sommeTotal  / (1000 / FFPROBE.currentFPS))));
		
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
			case "MPEG":
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
	}
	
	private static long formatTemps(long value) {		
		if (FFPROBE.isRunning) { //Dans le cas contraire erreur lors d'un encodage
			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			} while (FFPROBE.isRunning);
		}

		float framerate = 1000 / FFPROBE.currentFPS;
		long frameToMs = (long) (((value / framerate) % FFPROBE.currentFPS) * framerate) ;
						
		long time = Math.round(value / 1000) * 1000 + +frameToMs;	
		return time;
	}
	
	private void updateVideoSurfaceLeft(){
		addVideo();
		
		mediaPlayerComponentLeft.getMediaPlayer().playMedia(videoPath);
		
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		} while(mediaPlayerComponentLeft.getMediaPlayer().isPlaying() == false);
		
		mediaPlayerComponentLeft.getMediaPlayer().pause();		
		
		mediaPlayerComponentLeft.getMediaPlayer().setTime(formatTemps(sliderIn.getValue()));		
				
		if (leftPlay.getText().equals(Shutter.language.getProperty("btnPause")))
			mediaPlayerComponentLeft.getMediaPlayer().play();			
	}
	
	private void updateVideoSurfaceRight(){
		if (rightPlay.getText().equals(Shutter.language.getProperty("btnPlay")) == false)
		{
			mediaPlayerComponentRight.getMediaPlayer().playMedia(videoPath);	
			
			do {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
			} while(mediaPlayerComponentRight.getMediaPlayer().isPlaying() == false);	
			
			mediaPlayerComponentRight.getMediaPlayer().pause();				
			
			mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(sliderOut.getValue()));	
			
			if (rightPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				mediaPlayerComponentRight.getMediaPlayer().play();	
		}
	}
	
	public static void calculDuTempsCasesIn(long temps) {	
		if (temps % (1000 / FFPROBE.currentFPS) != 0)
			temps = (long) (temps + (1000 / FFPROBE.currentFPS)); //permet d'éviter le décalage d'une image
				
		if (caseTcInterne.isSelected())
			temps += offset;

    	if (mediaPlayerComponentLeft.getMediaPlayer().getMediaState() != null && calculIn && temps < FFPROBE.totalLength)
    	{    		
			mediaPlayerComponentLeft.getMediaPlayer().setVolume(sliderVolume.getValue());
    		
    		//Lorsque le sliderIn atteint le sliderOut
    		if (mediaPlayerComponentLeft.getMediaPlayer().getTime() >= mediaPlayerComponentRight.getMediaPlayer().getTime() && sliderOut.getValue() != sliderOut.getMaximum())
    		{
    			if (mediaPlayerComponentLeft.getMediaPlayer().isPlaying())
    				mediaPlayerComponentLeft.getMediaPlayer().setTime(0);
    		}    		

			NumberFormat formatter = new DecimalFormat("00");
			caseInH.setText(formatter.format(temps / 3600000));
			caseInM.setText(formatter.format((temps / 60000) % 60));
			caseInS.setText(formatter.format((temps / 1000) % 60));    		
			caseInF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
    		
    		if (sliderIn.getValue() == 0)
				caseInF.setText("00");		    		
    		
    		if (sliderOutChange == false && sliderInChange == false && drag == false)
    		{    			
    			sliderIn.setValue((int) formatTemps(mediaPlayerComponentLeft.getMediaPlayer().getTime()));
    			if (FFPROBE.hasAudio && VideoPlayer.panelWaveformLeft != null)
    				panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);
    			
    			if (sliderIn.getValue() > sliderOut.getValue())
				{
    				sliderInChange = true;
					try {
						sliderOut.setValue(sliderIn.getValue());
						panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
					} catch (Exception e1){}
					sliderInChange = false;
				}
    		}
    		        		
    		if (leftPlay.getText().equals(Shutter.language.getProperty("btnResume")))
    		{
        		timeIn = formatTemps(mediaPlayerComponentLeft.getMediaPlayer().getTime());
    			calculIn = false;
    			
    			if (frameControl == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
    				mediaPlayerComponentLeft.getMediaPlayer().setTime(sliderIn.getValue());

				frameControl = false;
    		}
    		        		
    		dureeTotale();		
        		        	
    	}
    		
    }

	private static void calculDuTempsCasesOut(long temps) {	
		if (temps % (1000 / FFPROBE.currentFPS) != 0)
			temps = (long) (temps + (1000 / FFPROBE.currentFPS) * 2); //permet d'éviter le décalage d'une image		
		else
			temps = (long) (temps + (1000 / FFPROBE.currentFPS)); //permet d'éviter le décalage d'une image
		
		if (caseTcInterne.isSelected())
			temps += offset;
		
		if (temps >= FFPROBE.totalLength)
		{
			sliderOutChange = true;
			sliderOut.setValue(sliderOut.getMaximum());
			sliderOutChange = false;    		
		}
		
    	if (mediaPlayerComponentRight.getMediaPlayer().getMediaState() != null && calculOut && mediaPlayerComponentRight.getMediaPlayer().getTime() != -1)
    	{			    		
			mediaPlayerComponentRight.getMediaPlayer().setVolume(sliderVolume.getValue());
    		
    		//Lorsque la video atteint le maximum
    		if (mediaPlayerComponentRight.getMediaPlayer().getTime() >= FFPROBE.totalLength)
    		{
    			if (mediaPlayerComponentRight.getMediaPlayer().isPlaying())
    				mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(sliderIn.getValue()));    				
    		}    		

			NumberFormat formatter = new DecimalFormat("00");
			caseOutH.setText(formatter.format(temps / 3600000));
			caseOutM.setText(formatter.format((temps / 60000) % 60));
			caseOutS.setText(formatter.format((temps / 1000) % 60));
			caseOutF.setText(formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
       		
	  		if (sliderInChange == false && sliderOutChange == false && drag == false)
	  		{	  			
	  			sliderOut.setValue((int) formatTemps(mediaPlayerComponentRight.getMediaPlayer().getTime()));
    			if (FFPROBE.hasAudio && panelWaveformRight != null)
    				panelWaveformRight.setLocation((int) ((long) ((long) waveformRight.getSize().width * sliderOut.getValue()) / sliderOut.getMaximum()) ,0);
    			    			
	  			if (sliderOut.getValue() < sliderIn.getValue())
				{			
	  				sliderOutChange = true;
					try {
						sliderIn.setValue(sliderOut.getValue());
						panelWaveformLeft.setLocation((int) ((long) ((long) waveformLeft.getSize().width * sliderIn.getValue()) / sliderIn.getMaximum()) ,0);									
					} catch (Exception e1){}
					sliderOutChange = false;
				}
	  		}
	  		
    		if (rightPlay.getText().equals(Shutter.language.getProperty("btnResume")))
    		{
                timeOut = formatTemps(mediaPlayerComponentRight.getMediaPlayer().getTime());
        		calculOut = false;
        		
    			if (frameControl == false)
    				mediaPlayerComponentRight.getMediaPlayer().setTime(sliderOut.getValue());
    			
				frameControl = false;
    		}
	  		
    		dureeTotale();
    	}
	}

	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(100);					
				} while (leftPlay == null);
				
				do {
					Thread.sleep(100);					
				} while (leftPlay.getText().equals(Shutter.language.getProperty("btnPlay")));
				
				VideoPlayer.leftPlay.doClick();
				
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
										
										do {
											Thread.sleep(100);
										} while (mediaPlayerComponentLeft.getMediaPlayer().isPlaying());
										
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
				
				//Position des sliders
				if (changeInPoint)
				{
					calculIn = true;
					timeIn = (long) (Integer.parseInt(caseInH.getText()) * 3600000 + Integer.parseInt(caseInM.getText()) * 60000 + Integer.parseInt(caseInS.getText()) * 1000 + Integer.parseInt(caseInF.getText()) * (1000 / FFPROBE.currentFPS)) - offset;
					mediaPlayerComponentLeft.getMediaPlayer().setTime((long) (timeIn - (FFPROBE.currentFPS / 1000)));
					calculDuTempsCasesIn(mediaPlayerComponentLeft.getMediaPlayer().getTime());					
				}
				
				//Out
				if (changeOutPoint)
				{
					sliderOutChange = true;
					sliderOut.setValue((int) ((Integer.parseInt(caseOutH.getText()) * 3600000 + Integer.parseInt(caseOutM.getText()) * 60000 + Integer.parseInt(caseOutS.getText()) * 1000 + Integer.parseInt(caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - (int) ((1000 / FFPROBE.currentFPS) * 1.5) - offset));
					sliderOutChange = false;
					mediaPlayerComponentRight.getMediaPlayer().setTime(formatTemps(sliderOut.getValue()));
				}
								
				//Deuxième chargement
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
										do {
											Thread.sleep(100);
										} while (mediaPlayerComponentLeft.getMediaPlayer().isPlaying());
										
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
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}
}