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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import settings.FunctionUtils;
import settings.ImageSequence;
import settings.InputAndOutput;
import functions.Picture;
import library.DCRAW;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.MEDIAINFO;
import library.XPDF;
import settings.Colorimetry;
import settings.Corrections;
import settings.Timecode;
import settings.Transitions;

public class VideoPlayer {
	
	public static JFrame frame;
	JLabel title = new JLabel(Shutter.language.getProperty("changeInOutPoint"));
	ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
	private static int taskBarHeight;
	
	//Icons
	private JLabel quit;
	private JLabel fullscreen;
	private JLabel reduce;
	private JPanel topPanel;
	private JLabel topImage;
	private JLabel bottomImage;
    
    //Player
	public static JPanel player; 
    public static Process playerVideo;
    public static Process playerAudio;
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
    public static boolean playerLoop = false;
    public static boolean frameIsComplete = false;
    public static boolean playerPlayVideo = true;
    private static boolean windowDrag;
	public static boolean sliderChange = false;
	private static JLabel lblVolume;
	public static JSlider sliderVolume;
	public static JLabel lblPosition;
	public static JLabel lblDuration;
	private static JLabel lblMode;
	public static JComboBox<Object> comboMode = new JComboBox<Object>(new String [] {Shutter.language.getProperty("cutUpper"), Shutter.language.getProperty("removeMode"), Shutter.language.getProperty("splitMode")});
	private static JLabel lblSpeed;
	private static JSlider sliderSpeed;
	private static boolean showInfoMessage = true;
	public static boolean playTransition = false;
		
	//Buttons & Checkboxes
	private static JButton btnCapture;
	private static JLabel btnPreview;
	public static JTextField splitValue;
	private static JLabel lblSplitSec;
	private JButton btnApply;
	public static JButton btnPrevious;
	public static JButton btnNext;
	public static JButton btnStop;
	public static JButton btnPlay;
	public static JButton btnMarkIn;
	public static JButton btnMarkOut;
	public static JButton btnGoToIn;
	public static JButton btnGoToOut;
	public static JButton btnNextFile;
	public static JButton btnPreviousFile;
	public static JSlider slider;
	public static JCheckBox caseGPU;
	public static JCheckBox caseVuMeter;
	public static JCheckBox casePlaySound;
	public static JCheckBox caseInternalTc;
	
	private static NumberFormat formatter = new DecimalFormat("00");
	private static NumberFormat formatterToMs = new DecimalFormat("000");
	public static JLabel lblVideo;	
	public static String videoPath = null;
	public static float inputFramerateMS = 40.0f;
	private static float totalFrames;
	
	//grpIn
	private static JPanel grpIn;
	public static JTextField caseInH;
	public static JTextField caseInM;
	public static JTextField caseInS;
	public static JTextField caseInF;
	
	//grpOut
	public static  JPanel grpOut;
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
	
	//grpColorimetry
	public static File preview = new File(Shutter.dirTemp + "preview.bmp");
	private static Thread runProcess = new Thread();
	public static JPanel grpColorimetry;
	public static JPanel panelColorimetryComponents;
	private JScrollBar scrollBarColorimetry = new JScrollBar();
	int scrollColorimetryValue = 0;
	private static JButton btnReset;
	public static JCheckBox caseEnableColorimetry = new JCheckBox(Shutter.language.getProperty("enable"));
	public static JComboBox<String> comboRGB = new JComboBox<String>();
	public static JSlider sliderExposure = new JSlider();
	public static JSlider sliderGamma = new JSlider();
	public static JSlider sliderContrast = new JSlider();
	public static JSlider sliderHighlights = new JSlider();
	public static JSlider sliderMediums = new JSlider();
	public static JSlider sliderShadows = new JSlider();
	public static JSlider sliderWhite = new JSlider();
	public static JSlider sliderBlack = new JSlider();
	public static JSlider sliderBalance = new JSlider();
	public static JSlider sliderHUE = new JSlider();
	public static JSlider sliderRED = new JSlider();
	public static JSlider sliderGREEN = new JSlider();
	public static JSlider sliderBLUE = new JSlider();		
	public static JSlider sliderVibrance = new JSlider();
	public static JComboBox<String> comboVibrance = new JComboBox<String>();
	public static JSlider sliderSaturation = new JSlider();
	public static JSlider sliderGrain = new JSlider();
	public static JSlider sliderVignette = new JSlider();
	public static JSlider sliderAngle = new JSlider();
	
	//grpCorrections
	public static JPanel grpCorrections;
	public static JCheckBox caseStabilisation = new JCheckBox(Shutter.language.getProperty("caseStabilisation"));
	private static String stabilisation = "";
	public static JCheckBox caseDeflicker = new JCheckBox(Shutter.language.getProperty("caseDeflicker"));
	public static JCheckBox caseBanding = new JCheckBox(Shutter.language.getProperty("caseBanding"));
	public static JCheckBox caseLimiter = new JCheckBox(Shutter.language.getProperty("caseLimiter"));
	public static JCheckBox caseDetails = new JCheckBox(Shutter.language.getProperty("caseDetails"));
	public static JSlider sliderDetails;
	public static JCheckBox caseDenoise = new JCheckBox(Shutter.language.getProperty("caseBruit"));
	public static JSlider sliderDenoise;
	public static JCheckBox caseSmoothExposure = new JCheckBox(Shutter.language.getProperty("caseExposure"));
	public static JSlider sliderSmoothExposure;
	
	//grpTransitions
	public static JPanel grpTransitions;
	public static JLabel lblFadeInColor;
	public static JCheckBox caseVideoFadeIn;
	public static JTextField spinnerVideoFadeIn = new JTextField("25");
	public static JCheckBox caseAudioFadeIn;
	public static JTextField spinnerAudioFadeIn = new JTextField("25");
	public static JLabel lblFadeOutColor;
	public static JCheckBox caseVideoFadeOut;
	public static JTextField spinnerVideoFadeOut = new JTextField("25");;
	public static JCheckBox caseAudioFadeOut;
	public static JTextField spinnerAudioFadeOut = new JTextField("25");;
	
	//grpCrop
	public static JPanel grpCrop;
	private static JPanel selection;
	private static JPanel overImage;
	private static boolean selectionDrag;
	private static int anchorRight;
	private static int anchorBottom;
	private static int startCropX = 0;
	private static int startCropY = 0;
	private static int frameCropX;
    private static int frameCropY;
	private static boolean shift = false;
    private static boolean ctrl = false;
	public static JCheckBox caseEnableCrop = new JCheckBox(Shutter.language.getProperty("enable"));
	private static JComboBox<String> comboPreset = new JComboBox<String>();
	private static int mouseCropOffsetX;
	private static int mouseCropOffsetY;
	public static JTextField textCropPosX;
    public static JTextField textCropPosY;
    public static JTextField textCropWidth;
    public static JTextField textCropHeight;
		
    //grpOverlay
    public static JPanel grpOverlay;	
	public static float imageRatio = 3;
	private static boolean ratioChanged = false;
    private int tcPosX = 0;
    private int tcPosY = 0;
    private static int tcLocX = 0;
    private static int tcLocY = 0;    
    private static int filePosX = 0;
    private static int filePosY = 0;
    private static int fileLocX = 0;
    private static int fileLocY = 0;
    private static JPanel timecode;
	private static JPanel fileName;
	public static JTextField textTcPosX;
	public static JTextField textTcPosY;
	public static JTextField textNamePosX;
	public static JTextField textNamePosY;    
	public static JTextField textTcSize;
	public static JTextField textNameSize;
	public static JTextField textTcOpacity;
	public static JTextField textNameOpacity;    
	public static JCheckBox caseAddTimecode = new JCheckBox(Shutter.language.getProperty("caseAddTimecode"));//IMPORTANT
	public static JLabel lblTimecode = new JLabel(Shutter.language.getProperty("lblTimecode"));
	public static JTextField TC1 = new JTextField("00");
	public static JTextField TC2 = new JTextField("00");
	public static JTextField TC3 = new JTextField("00");
	public static JTextField TC4 = new JTextField("00");
	public static JTextField text = new JTextField("");
	private static long textTime = System.currentTimeMillis();
	private static Thread changeText;
	public static JCheckBox caseShowTimecode = new JCheckBox(Shutter.language.getProperty("caseShowTimecode"));
	public static JCheckBox caseShowFileName = new JCheckBox(Shutter.language.getProperty("caseShowFileName"));
	public static JCheckBox caseAddText = new JCheckBox(Shutter.language.getProperty("caseShowText"));
	public static JComboBox<String> comboOverlayFont;	
	public static JLabel lblTcBackground; 
	private static JPanel panelTcColor = new JPanel();
	private static JPanel panelTcColor2 = new JPanel();
	public static Color foregroundColor = Color.WHITE;
	public static Color backgroundColor = Color.BLACK;
	public static String foregroundHex = "ffffff"; //white
	public static String backgroundHex = "000000"; //black
	public static String foregroundTcAlpha = "ff"; //100%
	public static String foregroundNameAlpha = "ff";
	public static String backgroundTcAlpha = "7f"; //50%
	public static String backgroundNameAlpha = "7f";
	
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
	
	//grpSubtitles
	public static JPanel grpSubtitles;
	public static JCheckBox caseAddSubtitles = new JCheckBox(Shutter.language.getProperty("caseSubtitles"));
	public static String outline = "1";
	public static JComboBox<String> comboSubsFont;	
	public static Color fontSubsColor = Color.WHITE;
	public static Color backgroundSubsColor = Color.BLACK;
	public static String subsHex = "FFFFFF";
	public static String subsHex2 = "000000";
	public static String subsAlpha = "7F";
	private static int alphaHeight;	
	private static JPanel panelSubsColor = new JPanel();
	private static JLabel lblSubsOutline = new JLabel();
	private static JPanel panelSubsColor2 = new JPanel();
	public static JButton btnI;
	public static JButton btnG;
	public static JTextField textSubsSize;
	public static JTextField textSubsOutline;
	public static JTextField textSubtitlesPosition;
	public static JTextField textSubsWidth = new JTextField();
	public static JLabel lblSubsBackground; 
	public static File subtitlesFile;
	private static JPanel subsCanvas;
	
	private static class MouseSubSize {
		static int mouseX;
		static int offsetX;
	}
	
  	private static class MouseSubsPosition {
  		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
  	}
	
	//grpWatermark
	public static JPanel grpWatermark;
	public static JPanel logo; 
	public static Image logoPNG;
	public static JCheckBox caseAddWatermark = new JCheckBox(Shutter.language.getProperty("frameLogo"));
	public static JTextField textWatermarkPosX;
	public static JTextField textWatermarkPosY;
    public static JTextField textWatermarkSize;
    public static JTextField textWatermarkOpacity;
    private static JCheckBox caseSafeArea;
	public static String logoFile = new String();
    private int logoPosX = 0;
    private int logoPosY = 0;
    private static int logoLocX = 0;
    private static int logoLocY = 0;
	
  	private static class MouseLogoPosition {
		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
	}
  	  	
	private static int MousePositionX;
	private static int MousePositionY;
		
	public VideoPlayer() {  	
		
		showInfoMessage = true;
		  
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(45, 45, 45));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		frame.getContentPane().setLayout(null);
		frame.setVisible(false);
		frame.setSize(1300, 624);
		frame.setMinimumSize(new Dimension(1300, 624));
		frame.setTitle(Shutter.language.getProperty("changeInOutPoint"));
		frame.setForeground(Color.WHITE);
				
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
    		frame.setUndecorated(true);
    		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);	
    		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    		taskBarHeight = (int) (dim.getHeight() - winSize.height);
    		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
            shape1.add(shape2);
    		frame.setShape(shape1);
    		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
		}
		
		topPanel();
		
		//Ces deux boutons définissent la postion des autres objets par la suite ils doivent donc être appelés en amont
    	btnCapture = new JButton(Shutter.language.getProperty("btnCaptureIn"));
    	btnCapture.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
    	btnCapture.setMargin(new Insets(0,0,0,0));
    	btnCapture.setBounds(9, topPanel.getSize().height + 10, 310, 21);		
		frame.getContentPane().add(btnCapture);
		
		btnCapture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
            	if (FFPROBE.totalLength <= 40 || Shutter.inputDeviceIsRunning)
            	{
            		Picture.main(true, true);
            	}
            	else
            		Picture.main(true, true);
            	
            	frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
				
			}        			
		});
		
		btnApply = new JButton(Shutter.language.getProperty("btnApply"));
		btnApply.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnApply.setMargin(new Insets(0,0,0,0));
		btnApply.setBounds(frame.getSize().width - 8 - 310 - 2, topPanel.getSize().height + 10, 310, 21);		
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
		frame.getContentPane().add(lblVideo);
				
		player();        		
		buttons();
		grpColorimetry();
		grpCorrections();
		grpTransitions();
		grpCrop();
		grpOverlay();
		grpSubtitles();
		grpWatermark();
		grpIn();
		sliders();	
		grpOut();
						
		lblPosition = new JLabel();
		lblPosition.setHorizontalAlignment(SwingConstants.CENTER);
		lblPosition.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblPosition.setForeground(Utils.themeColor);   		
		frame.getContentPane().add(lblPosition);
				
		lblDuration = new JLabel();
		lblDuration.setHorizontalAlignment(SwingConstants.CENTER);
		lblDuration.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblDuration.setForeground(Utils.themeColor);   		
		frame.getContentPane().add(lblDuration);
		        		 
		setMedia();	
		
		totalDuration();
				
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
				Settings.saveSettings();
            }
        });
		
		frame.addMouseMotionListener(new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {

				if (windowDrag)
				{					
					frame.setSize(e.getX() + 10, e.getY() + 10);										
					resizeAll();        					
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if (MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x > frame.getSize().width - 20 || MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y > frame.getSize().height - 20)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				}
				else 
				{
					if (windowDrag == false && frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) == false)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
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
					windowDrag = true;
																				
    				if (playerVideo != null)
    				{
    					btnPlay.setText(Shutter.language.getProperty("btnPlay"));
    					playerStop();
    				}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {	

				if (Shutter.liste.getSize() > 0 && Shutter.fileList.getSelectedValue().equals(videoPath) == false)
				{					
					setMedia();
				}	

				windowDrag = false;
				
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
            	
            	if (windowDrag == false)
            	{					
					resizeAll();					
            	}
            }
        });
				
		//Arrows control
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
					
	        public void eventDispatched(AWTEvent event)
	        {				        	
	        	KeyEvent e = (KeyEvent) event;
	        	
	        	if (caseAddWatermark.isSelected())
	        	{
	        		if (e.getID() == KeyEvent.KEY_PRESSED)
	        		{          	  	
	        			boolean update = false;
	        			
						if (e.getKeyCode() == KeyEvent.VK_UP)
						{
							logo.setLocation(logo.getLocation().x, logo.getLocation().y - 1);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_DOWN)
						{
							logo.setLocation(logo.getLocation().x, logo.getLocation().y + 1);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_LEFT)
						{
							logo.setLocation(logo.getLocation().x - 1, logo.getLocation().y);
							update = true;
						}
						
						if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						{
							logo.setLocation(logo.getLocation().x + 1, logo.getLocation().y);
							update = true;
						} 

						if (update)
						{
							textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
							textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
							logoLocX = logo.getLocation().x;
							logoLocY = logo.getLocation().y;
						}
	        		}
	        	}
	        	else
	        	{
					int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - frameCropX;
					int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - frameCropY;
									
		        	if (mouseInPictureX > 0 && mouseInPictureX < player.getWidth() && mouseInPictureY > 0 && mouseInPictureY < player.getHeight())
		        	{
		        		if (e.getID() == KeyEvent.KEY_PRESSED)
		        		{           	  
							if (e.getKeyCode() == KeyEvent.VK_SHIFT)
								shift = true;
							
							if (e.getKeyCode() == KeyEvent.VK_CONTROL)
								ctrl = true;
							
							if (e.getKeyCode() == KeyEvent.VK_UP)
							{
								selection.setLocation(selection.getLocation().x, selection.getLocation().y - 1);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_DOWN)
							{
								selection.setLocation(selection.getLocation().x, selection.getLocation().y + 1);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_LEFT)
							{
								selection.setLocation(selection.getLocation().x - 1, selection.getLocation().y);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_RIGHT)
							{
								selection.setLocation(selection.getLocation().x + 1, selection.getLocation().y);
							}
							
							if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
							{
								selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);
								anchorRight = selection.getLocation().x + selection.getWidth();
								anchorBottom = selection.getLocation().y + selection.getHeight();	
							}	 
							
							checkSelection();
		        		}
		              
		        	}
		        	
		        	if (e.getID() == KeyEvent.KEY_RELEASED)
		        	{
		        		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
							shift = false;  
		        		
		        		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
							ctrl = false;
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
							lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.25");
						}
						else if (sliderSpeed.getValue() == 1)
						{
							lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x0.5");
						}
						else if (sliderSpeed.getValue() == 2)
						{
							lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
						}
						else if (sliderSpeed.getValue() == 3)
						{
							lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1.5");
						}
						else if (sliderSpeed.getValue() == 4)
						{
							lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x2");
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
							shift = true;
	        		}
					
					if (e.getKeyCode() == KeyEvent.VK_I)
					{
						if (shift)
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
						if (shift)
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
						shift = false;		
	        	}
			}
			
		};
		
		for (Component component : frame.getContentPane().getComponents())
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
		
		Utils.changeFrameVisibility(frame, false);
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		resizeAll(); //IMPORTANT
	}
	
	public static void playerProcess(float inputTime) {

		try {			
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				String PathToFFMPEG = "Library\\ffmpeg.exe";
				
				//VIDEO STREAM
				playerVideo = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToFFMPEG + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo)});
	
				//AUDIO STREAM
				playerAudio = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToFFMPEG + setAudioCommand(inputTime)});
			}
			else
			{
				String PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
				
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
				
				//AUDIO STREAM
				ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + setAudioCommand(inputTime));	
				playerAudio = pba.start();
			}			
				
			InputStream video = playerVideo.getInputStream();				
			BufferedInputStream videoInputStream = new BufferedInputStream(video);

			InputStream audio = playerAudio.getInputStream();							
		    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);		    
		    AudioFormat audioFormat = audioInputStream.getFormat();
	        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
	        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
	        
            line.open(audioFormat);
	        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            line.start();	
				        					
			playerThread = new Thread(new Runnable() {

				@Override
				public void run() {

					byte bytes[] = new byte[(int) (FFPROBE.audioSampleRate*4/FFPROBE.currentFPS)];
		            int bytesRead = 0;
		            
					do {	
						
						long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);

						if (playerLoop)
						{							
							try {	
								
								//Audio volume	
								if (casePlaySound.isSelected() || (sliderChange == false && frameControl == false && windowDrag == false))						       
								{
									double gain = (double) sliderVolume.getValue() / 100;   
							        float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
							        gainControl.setValue(dB);
	
									///Read 1 audio frame
									bytesRead = audioInputStream.read(bytes, 0, bytes.length);
					        		line.write(bytes, 0, bytesRead);
								}
												 				        		
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
						            	long time = System.nanoTime();
						            	while (System.nanoTime() - time < delay) {}		
					                }
								}								
								
								frameIsComplete = true;		
							}
						}	 
						else
						{
							if (casePlaySound.isSelected() || (sliderChange == false && frameControl == false && windowDrag == false))				       
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
					
					if (casePlaySound.isSelected() || (sliderChange == false && frameControl == false && windowDrag == false))	       
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
		
		playerLoop = false;
		/*
		try {
			Thread.sleep((long) inputFramerateMS);
		} catch (InterruptedException e1) {}*/
		
		if (playerVideo != null)
		{
			playerVideo.destroy();
			playerThread.interrupt();
		}
		
		if (playerAudio != null)
			playerAudio.destroy();		
	}

	public static void playerRepaint() {
				
		if (frameVideo != null)
		{
			player.repaint();
		}

		getTimePoint(playerCurrentFrame); 		
	}
	
	public static boolean playerIsPlaying() {
		
		if (playerVideo != null && playerVideo.isAlive() && btnPlay.getText().equals(Shutter.language.getProperty("btnPause")))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerSetTime(float time) {
			
		if (setTime == null || setTime.isAlive() == false && frameVideo != null)
		{			
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {					

					int t = (int) Math.ceil(time);
					
					if (t < 0)
						t = 0;
					
					writeCurrentSubs(t);
					
					playerPlayVideo = false;
					
					if (frameVideo != null)
					{
						if (playerIsPlaying())
						{				
							playerStop();
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerThread.isAlive());
											
							playerPlayVideo = true;
							playerLoop = true;
							playerProcess(t);								
						}
						else
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
							
							playerLoop = false;
						}
														
						playerCurrentFrame = t;
						getTimePoint(playerCurrentFrame); 
						timecode.repaint();
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
    		    	
    	if (Shutter.caseInAndOut.isSelected())
    	{
	    	//Updating video file
			if (Shutter.liste.getSize() != 0)
			{
				if (Shutter.fileList.getSelectedIndices().length == 0)
	      		{
					Shutter.fileList.setSelectedIndex(0);
	      		}
				
				videoPath = Shutter.fileList.getSelectedValue();
								
				//set timecode & filename locations
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
				if (frame.isVisible() 
				&& lblVideo.getText().equals(new File(videoPath).getName()) == false
				&& cursorWaveform.isVisible()
				&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{
					if (waveform.exists())
						waveform.delete();
										
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
				}
												
				if (lblVideo.isVisible() == false || lblVideo.getText().equals(new File(videoPath).getName()) == false)
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

							if (FFPROBE.imageRatio < 1.77f)
							{
								frame.setMinimumSize(new Dimension(1300, 724));								
							}
							else
								frame.setMinimumSize(new Dimension(1300, 624));
							
				    		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
				            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
				            shape1.add(shape2);
				    		frame.setShape(shape1);
				    		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
						}
						
					} catch (InterruptedException e) {}

					//Displaying section 
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					{
						btnPreviousFile.setVisible(false);
						btnNextFile.setVisible(false);
						grpColorimetry.setVisible(false);
						grpCorrections.setVisible(false);
						grpTransitions.setVisible(false);
						grpCrop.setVisible(false);
						grpOverlay.setVisible(false);
						grpSubtitles.setVisible(false);
						grpWatermark.setVisible(false);
						waveformContainer.setVisible(true);
						grpIn.setVisible(false);
						grpOut.setVisible(false);
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
						btnCapture.setEnabled(false);
						showScale.setVisible(false);
					}
					else if (FFPROBE.totalLength <= 40) //Image
					{
						btnPreviousFile.setVisible(true);
						btnNextFile.setVisible(true);
						grpColorimetry.setVisible(true);
						grpCorrections.setVisible(true);
						grpTransitions.setVisible(true);
						grpTransitions.setEnabled(false);
						grpCrop.setVisible(true);
						grpOverlay.setVisible(true);
						grpSubtitles.setVisible(true);
						grpWatermark.setVisible(true);
						waveformContainer.setVisible(false);
						grpIn.setVisible(false);
						grpOut.setVisible(false);
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
						btnCapture.setEnabled(true);
						
						if (Shutter.caseEnableSequence.isSelected())
						{
							grpSubtitles.setEnabled(true);		
							caseAddSubtitles.setEnabled(true);
						}
						else
						{
							grpSubtitles.setEnabled(false);		
							
							for (Component c : grpSubtitles.getComponents())
							{
								c.setEnabled(false);
							}						
						}
					}
					else
					{						
						btnPreviousFile.setVisible(true);
						btnNextFile.setVisible(true);
						if (FFPROBE.audioOnly)
						{
							grpColorimetry.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(true);
							grpTransitions.setEnabled(true);
							caseVideoFadeIn.setEnabled(false);
							caseVideoFadeOut.setEnabled(false);
							grpCrop.setVisible(false);
							grpOverlay.setVisible(false);
							grpSubtitles.setVisible(false);
							grpWatermark.setVisible(false);
						}
						else
						{
							grpColorimetry.setVisible(true);
							grpCorrections.setVisible(true);
							grpTransitions.setVisible(true);
							grpTransitions.setEnabled(true);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);
							grpCrop.setVisible(true);
							grpOverlay.setVisible(true);
							grpSubtitles.setVisible(true);
							grpWatermark.setVisible(true);
						}
													
						waveformContainer.setVisible(true);
						grpIn.setVisible(true);
						grpOut.setVisible(true);
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
							btnCapture.setEnabled(false);
							caseInternalTc.setVisible(false);
							showScale.setVisible(false);
						}
						else
						{
							btnCapture.setEnabled(true);
							caseInternalTc.setVisible(true);
							showScale.setVisible(true);
						}
						
						grpSubtitles.setEnabled(true);	
						caseAddSubtitles.setEnabled(true);
										
						//Timecode			
						if (caseShowTimecode.isSelected() || caseInternalTc.isSelected())
						{
							if (FFPROBE.timecode1.equals(""))
							{
								if (caseShowTimecode.isSelected())
								{
									caseShowTimecode.setSelected(false);
									caseAddTimecode.doClick();
								}
								
								if (caseInternalTc.isSelected())
								{
									caseInternalTc.setSelected(false);
									offset = 0;
								}
							}
							else
							{
								if (caseShowTimecode.isSelected())
								{
									TC1.setEnabled(false);
									TC2.setEnabled(false);
									TC3.setEnabled(false);
									TC4.setEnabled(false);
									caseAddTimecode.setSelected(false);					
									player.add(timecode);
									
									//Overimage need to be the last component added
									if (caseEnableCrop.isSelected())
									{
										player.remove(selection);
										player.remove(overImage);
										player.add(selection);
										player.add(overImage);
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
						
						grpCrop.setEnabled(false);
						
						for (Component c : grpCrop.getComponents())
						{
							c.setEnabled(false);
						}
						
						grpOverlay.setEnabled(false);
						
						for (Component c : grpOverlay.getComponents())
						{
							c.setEnabled(false);
						}
						
						grpWatermark.setEnabled(false);
						
						for (Component c : grpWatermark.getComponents())
						{
							c.setEnabled(false);
						}
					}
					else
					{
						grpCrop.setEnabled(true);
						caseEnableCrop.setEnabled(true);
						
						grpOverlay.setEnabled(true);
						
						for (Component c : grpOverlay.getComponents())
						{
							c.setEnabled(true);
						}
						
						grpWatermark.setEnabled(true);
						caseAddWatermark.setEnabled(true);
						caseSafeArea.setEnabled(true);
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
					caseShowTimecode.setEnabled(true);
									
					textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
					lblVideo.setText(new File(videoPath).getName());
					lblVideo.setVisible(true);
					
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
				lblVideo.setVisible(false);
				showScale.setVisible(false);
				playerStop();
				slider.setValue(0);

				btnPlay.setText(Shutter.language.getProperty("btnPlay"));		
				
				btnPlay.setEnabled(false);
				btnPrevious.setEnabled(false);
				btnNext.setEnabled(false);
				btnStop.setEnabled(false);
				btnMarkIn.setEnabled(false);
				btnMarkOut.setEnabled(false);
				btnGoToIn.setEnabled(false);
				btnGoToOut.setEnabled(false);
				
				caseInternalTc.setEnabled(false);	
				caseInternalTc.setSelected(false);				
			}
			
			if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("processEnded")))
			{
				Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
			}
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) || Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
			{
				grpOut.setVisible(false);
			}
			else if (waveformContainer.isVisible())
			{
				grpOut.setVisible(true);
			}				
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
			{
				File video = new File(videoPath);
				String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
				
				SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
				
	    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    		frame.setLocation(frame.getLocation().x , dim.height/3 - frame.getHeight()/2);
	    		
	    		if (caseAddSubtitles.isSelected())
	    		{
		    		player.remove(subsCanvas);
					caseAddSubtitles.setSelected(false);	    	
	    		}
	    		
	    		if (SubtitlesTimeline.frame == null)    		
	    		{
	    			new SubtitlesTimeline(dim.width/2-500,frame.getLocation().y + frame.getHeight() + 7);
	    		}
	    		else
	    		{        		
	    			SubtitlesTimeline.frame.setVisible(true);
	    			SubtitlesTimeline.subtitlesNumber();
	    		}    	
			}
			
			waveformContainer.requestFocus();
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
					//Auto GPU selection
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
		if (caseAudioFadeIn.isSelected() || caseAudioFadeOut.isSelected())
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
		waveformIcon.setVisible(false);
			
		Thread addWaveform = new Thread(new Runnable()
		{
			@Override
			public void run() {
							
				if (FFMPEG.isRunning || playerVideo == null && frame.isVisible() == false)
				{						
					do  {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFMPEG.isRunning && playerVideo == null && frame.isVisible() == false);
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
								String h = formatter.format(time / 3600);
								String m = formatter.format((time / 60) % 60);
								String s = formatter.format(time % 60);    		
								String f = formatterToMs.format(time % 1000);
								
								start = " -ss " + h + ":" + m + ":" + s + "." + f;
								duration = "atrim=duration=" + (SubtitlesTimeline.frame.getWidth() * 2) / 100 + ",";								
								size = (long) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom);
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
							
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && Shutter.caseInAndOut.isSelected()) //Ne charge plus l'image si la fenêtre est fermée entre temps
							{
								Image imageBMP = ImageIO.read(waveform);
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(imageBMP).getImage().getScaledInstance((int) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));						
								
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) ((SubtitlesTimeline.frame.getWidth() * 2) * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
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
		btnPrevious.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnPrevious.setBounds(player.getLocation().x + player.getSize().width / 2 - 21 - 4, player.getLocation().y + player.getSize().height + 10, 22, 21);		
		frame.getContentPane().add(btnPrevious);
				
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
								lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
								lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
								playerSetTime(playerCurrentFrame - 1);
							}
			
							frameControl = true;
							
							if (playerVideo != null && frameVideo != null)
							{										
								if (playerLoop)
								{
									btnPlay.setText(Shutter.language.getProperty("btnPlay"));
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
		btnNext.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnNext.setBounds(player.getLocation().x + player.getSize().width / 2 + 4, btnPrevious.getLocation().y, 22, 21);		
		frame.getContentPane().add(btnNext);
		
		btnNext.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sliderSpeed.getValue() != 2)
				{
					sliderSpeed.setValue(2);
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
					playerSetTime(playerCurrentFrame + 1);
				}
				
				if (preview.exists() || caseAddSubtitles.isSelected())
				{
					preview.delete();												
					playerSetTime(playerCurrentFrame + 1);
				}

				frameControl = true;
				
				if (playerVideo != null)
				{
					if (playerLoop)
					{
						btnPlay.setText(Shutter.language.getProperty("btnPlay"));
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
		
		btnPlay = new JButton(Shutter.language.getProperty("btnPlay"));
		btnPlay.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnPlay.setMargin(new Insets(0,0,0,0));
		btnPlay.setBounds(btnPrevious.getLocation().x - 80 - 4, btnPrevious.getLocation().y, 80, 21);				
		frame.getContentPane().add(btnPlay);
		
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (btnPlay.getText().equals(Shutter.language.getProperty("btnPause")))
				{
					btnPlay.setText(Shutter.language.getProperty("btnPlay"));	
					playerLoop = false;
					showFPS.setVisible(false);
					
					if (sliderSpeed.getValue() != 2)
					{						
						playerSetTime(slider.getValue());	
					}							
				}
				else if (btnPlay.getText().equals(Shutter.language.getProperty("btnPlay")))
				{									
					if (preview.exists() || caseAddSubtitles.isSelected())
					{
						preview.delete();												
						playerSetTime(playerCurrentFrame);
					}
					
					frameControl = false;
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
		            fpsTime = System.nanoTime();
		            displayCurrentFPS = 0;
				}
								
			}
			
		});
		
		btnStop = new JButton(Shutter.language.getProperty("btnStop"));
		btnStop.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnStop.setMargin(new Insets(0,0,0,0));
		btnStop.setBounds(btnNext.getLocation().x + btnNext.getSize().width + 4, btnNext.getLocation().y, 80, 21);		
		frame.getContentPane().add(btnStop);		
		
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
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
						} while (playerVideo.isAlive());
						slider.setValue(0);
					}
					
					resizeAll();

					btnPlay.setText(Shutter.language.getProperty("btnPlay"));
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
 		
		btnPreviousFile = new JButton(Shutter.language.getProperty("btnPrevious"));
		btnPreviousFile.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));	
		btnPreviousFile.setMargin(new Insets(0,0,0,0));
		btnPreviousFile.setBounds(player.getX(), btnCapture.getY(), 84, 21);	
		frame.getContentPane().add(btnPreviousFile);
		
		btnPreviousFile.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent e) {
		  		
				if (Shutter.fileList.getSelectedIndex() > 0)
		  		{     			
					Shutter.fileList.setSelectedIndex(Shutter.fileList.getSelectedIndex() - 1);
					setMedia();
		  		}	
			}
			
		});
		
		btnNextFile = new JButton(Shutter.language.getProperty("btnNext"));
		btnNextFile.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnNextFile.setMargin(new Insets(0,0,0,0));
		btnNextFile.setBounds(player.getX() + player.getWidth() - 84, btnPreviousFile.getY(), btnPreviousFile.getWidth(), 21);			
		frame.getContentPane().add(btnNextFile);
		
		btnNextFile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
	      		
				if (Shutter.fileList.getSelectedIndex() < Shutter.liste.getSize())
	      		{      			
					Shutter.fileList.setSelectedIndex(Shutter.fileList.getSelectedIndex() + 1);
					if (playerLoop)
					{
						btnPlay.setText(Shutter.language.getProperty("btnPlay"));
						playerLoop = false;
					}
					setMedia();
	      		}
			}
			
		});
		
		btnMarkIn = new JButton("⬥");
		btnMarkIn.setFont(new Font("", Font.PLAIN, 12));
		btnMarkIn.setBounds(btnPlay.getLocation().x - 22 - 4, btnPlay.getLocation().y, 22, 21);				
		frame.getContentPane().add(btnMarkIn);
		
		btnMarkIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
						
				playerInMark = cursorWaveform.getX();
				waveformContainer.repaint();							
				updateGrpIn(playerCurrentFrame);
				timecode.repaint();
			}
			
		});
		
		btnGoToIn = new JButton("⬥<");
		btnGoToIn.setMargin(new Insets(0,0,0,0));
		btnGoToIn.setFont(new Font("", Font.PLAIN, 12));
		btnGoToIn.setBounds(btnMarkIn.getLocation().x - 40 - 4, btnMarkIn.getLocation().y, 40, 21);				
		frame.getContentPane().add(btnGoToIn);
		
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
		btnMarkOut.setBounds(btnStop.getLocation().x + btnStop.getSize().width + 4, btnStop.getLocation().y, 22, 21);				
		frame.getContentPane().add(btnMarkOut);
		
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
		btnGoToOut.setBounds(btnMarkOut.getLocation().x + btnMarkOut.getSize().width + 4, btnMarkOut.getLocation().y, 40, 21);				
		frame.getContentPane().add(btnGoToOut);
		
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
		showFPS.setBounds(player.getX() + player.getWidth() / 2, player.getY() - 18, player.getWidth() / 2, showFPS.getPreferredSize().height);
		frame.getContentPane().add(showFPS);
		
		showScale = new JLabel("1920x1080");
		showScale.setVisible(false);
		showScale.setEnabled(false);
		showScale.setFont(new Font(Shutter.freeSansFont, Font.BOLD, 12));
		showScale.setHorizontalAlignment(SwingConstants.LEFT);
		showScale.setBounds(player.getX(), showFPS.getY(), player.getWidth() / 2, showScale.getPreferredSize().height);
		frame.getContentPane().add(showScale);
		
    }
	
    @SuppressWarnings("serial")
	private void player() {		
    	
		player = new JPanel() {
			
            @Override
            protected void paintComponent(Graphics g) {
            	
                super.paintComponent(g);
                
                Graphics2D g2 = (Graphics2D)g;
                
                g2.setColor(Color.BLACK);
                                
                if (windowDrag || Shutter.liste.getSize() == 0)
                {
                	g2.fillRect(0, 0, player.getWidth(), player.getHeight()); 
                }
                else
                {
                	g2.drawImage(frameVideo, 0, 0, null); 
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
                                
                if (stabilisation != "")
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
		frame.getContentPane().add(player);
		
	}
	    
	@SuppressWarnings("serial")
	private void sliders() {
		
		slider = new JSlider();
		slider.setPaintLabels(true);
		slider.setValue(0);
		slider.setVisible(false);
		slider.setBounds(grpIn.getLocation().x + grpIn.getSize().width + 6, grpIn.getLocation().y, frame.getSize().width - (grpIn.getLocation().x + grpIn.getSize().width + 6) * 2 - 6, 60); 
		frame.getContentPane().add(slider);
				
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
		waveformContainer.setBounds(slider.getX(), slider.getY() + 7, slider.getWidth(), grpIn.getHeight() - 9);
		frame.getContentPane().add(waveformContainer);
		
		waveformIcon = new JLabel();
		waveformIcon.setOpaque(false);
		waveformIcon.setBounds(waveformContainer.getBounds());
		frame.getContentPane().add(waveformIcon);
		
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
						btnPlay.setText(Shutter.language.getProperty("btnPause"));		
					
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
		sliderVolume.setValue(Settings.videoPlayerVolume);			
		frame.getContentPane().add(sliderVolume);
		
		sliderVolume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Settings.videoPlayerVolume = sliderVolume.getValue();			
			}
			
		});
		
		lblVolume = new JLabel(Shutter.language.getProperty("volume") + " ");
		lblVolume.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVolume.setSize(lblVolume.getPreferredSize().width + 3, 16);			
		lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, lblSpeed.getY());	
		
		frame.getContentPane().add(lblVolume);
				
		addWaveform(true);
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20 , 4, 15, 15);
		
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
								
				if (accept) 
				{			
					if (preview.exists())
						preview.delete();
					
					if (waveform.exists())
						waveform.delete();
					
					//Image sequence
					File concat = new File(Shutter.dirTemp + "concat.txt");					
					if (concat.exists())
						concat.delete();
					
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
					
					Shutter.caseInAndOut.setSelected(false);
					
					btnStop.doClick();					
					
					videoPath = null;
					lblVideo.setVisible(false);
					playerStop();
					slider.setValue(0);

					btnPlay.setText(Shutter.language.getProperty("btnPlay"));
					
					btnPlay.setEnabled(false);
					btnPrevious.setEnabled(false);
					btnNext.setEnabled(false);
					btnStop.setEnabled(false);
					
					caseInternalTc.setEnabled(false);	
					caseInternalTc.setSelected(false);

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
							FFPROBE.setLength();
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
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15, 15);
			
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max_pressed.svg", 15, 15));
				accept = true;
				
				if (playerVideo != null)
				{
					btnPlay.setText(Shutter.language.getProperty("btnPlay"));
					playerStop();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				
				GraphicsConfiguration config = frame.getGraphicsConfiguration();
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

				int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	
				int screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();
        		
				if (accept && frame.getHeight() < screenHeight - taskBarHeight)
				{					
					if (player.getHeight() > player.getWidth())
					{
						frame.setBounds(0,0, screenWidth, screenHeight - taskBarHeight); 	
					}
					else
					{
						int setWidth = (int) ((float) (screenHeight - topPanel.getHeight() - taskBarHeight - btnCapture.getHeight() - btnPlay.getHeight() - slider.getHeight() * 2 - lblDuration.getHeight() - 40) * ((float) (player.getWidth() * 2) / player.getHeight()));
						if (setWidth <= screenWidth)
							frame.setSize(setWidth, screenHeight - taskBarHeight); 
						else
							frame.setSize(screenWidth, screenHeight - taskBarHeight);						
							
						if (System.getProperty("os.name").contains("Windows"))
						{
							frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width,
		        		   					  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);		        		
						}
						else
		        		{
		        			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width,
		        							  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);	
		        		}
		        			
					}						
				}
				else if (accept)
				{
	        		frame.setSize(1300, 624);
	        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
				
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {}
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
		reduce.setBounds(fullscreen.getLocation().x - 20, 4, 15, 15);
			
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
		bottomImage.setBounds(0 ,0, frame.getSize().width, 28);
		
		bottomImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
				
				if (down.getClickCount() == 2)
				{
					if (playerVideo != null)
    				{
						btnPlay.setText(Shutter.language.getProperty("btnPlay"));
    					playerStop();
    					
    					//Bug workaround
    					do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e1) {};
						} while (playerVideo.isAlive());
    				}
					
    				GraphicsConfiguration config = frame.getGraphicsConfiguration();
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

    				int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	
    				int screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();
    				
					if (frame.getHeight() < screenHeight - taskBarHeight)
					{						
						if (player.getHeight() > player.getWidth())
						{
							frame.setBounds(0,0, screenWidth, screenHeight - taskBarHeight); 	
						}
						else
						{
							int setWidth = (int) ((float) (screenHeight - topPanel.getHeight() - taskBarHeight - btnCapture.getHeight() - btnPlay.getHeight() - slider.getHeight() * 2 - lblDuration.getHeight() - 40) * ((float) (player.getWidth() * 2) / player.getHeight()));
							if (setWidth <= screenWidth)
								frame.setSize(setWidth, screenHeight - taskBarHeight); 
							else
								frame.setSize(screenWidth, screenHeight - taskBarHeight);						
								
							if (System.getProperty("os.name").contains("Windows"))
							{
								frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width,
			        		   					  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);		        		
							}
							else
			        		{
			        			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width,
			        							  allScreens[screenIndex].getDefaultConfiguration().getBounds().y);	
			        		}
								
						}						
					}
					else
					{
		        		frame.setSize(1300, 624);
		        		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		    			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		        		
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
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
				
				if (Shutter.liste.getSize() > 0 && Shutter.fileList.getSelectedValue().equals(videoPath) == false)
				{					
					setMedia();
				}	
				
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
		title.setBounds(0, 0, frame.getWidth(), 28);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
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

	private void grpIn(){
		
		grpIn = new JPanel();
		grpIn.setLayout(null);
		grpIn.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("grpIn") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpIn.setBackground(new Color(45, 45, 45));
		grpIn.setBounds(6, frame.getSize().height - 84, 156, 52);
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

		btnPreview = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		btnPreview.setHorizontalAlignment(SwingConstants.CENTER);
		btnPreview.setBounds(frame.getSize().width - 36, frame.getSize().height - 26, 16, 16);
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
		
		splitValue = new JTextField();
		splitValue.setName("splitValue");
		splitValue.setText("10");
		splitValue.setVisible(false);
		splitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		splitValue.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		splitValue.setColumns(10);
		splitValue.setBounds(frame.getSize().width - 48, frame.getSize().height - 26, 34, 16);
		frame.getContentPane().add(splitValue);
		
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
		frame.getContentPane().add(lblSplitSec);
		
		comboMode.setName("comboMode");
		comboMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		comboMode.setMaximumRowCount(3);
		comboMode.setSize(76, 22);
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
				
				waveformContainer.repaint();
			}
	
		});
		
		lblMode = new JLabel(Shutter.language.getProperty("mode"));
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(lblMode);
		
		sliderSpeed = new JSlider();
		sliderSpeed.setMaximum(4);
		sliderSpeed.setValue(2);
		sliderSpeed.setMinorTickSpacing(1);
		sliderSpeed.setMajorTickSpacing(1);
		sliderSpeed.setSize(80, 22);
	
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(sliderSpeed);

		sliderSpeed.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (sliderSpeed.isEnabled())
				{
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
					lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
					
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
					
					lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
									
					if (slider.getValue() > 0)
					{
						frameIsComplete = false;
									
						playerSetTime(slider.getValue());	
					}	
				}
			}

		});
				
		lblSpeed = new JLabel(Shutter.language.getProperty("conformBySpeed") + " x1"); //0.25 allow to get max preferred size width
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false)
			frame.getContentPane().add(lblSpeed);
		
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
		
		caseInH.setText(formatter.format(Math.floor(timeIn / FFPROBE.currentFPS / 3600)));
		caseInM.setText(formatter.format(Math.floor(timeIn / FFPROBE.currentFPS / 60) % 60));
		caseInS.setText(formatter.format(Math.floor(timeIn / FFPROBE.currentFPS) % 60));    		
		caseInF.setText(formatter.format(Math.floor(timeIn % FFPROBE.currentFPS)));
		
	}
	
	private void grpOut(){
		
		grpOut = new JPanel();
		grpOut.setLayout(null);
		grpOut.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("grpOut") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpOut.setBackground(new Color(45, 45, 45));
		grpOut.setBounds(frame.getWidth() - grpIn.getWidth() - 12, grpIn.getY(), grpIn.getWidth(), grpIn.getHeight());
		frame.getContentPane().add(grpOut);

		caseVuMeter = new JCheckBox(Shutter.language.getProperty("caseVuMeter"));
		caseVuMeter.setName("caseVuMeter");	
		caseVuMeter.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseVuMeter.setSelected(Settings.videoPlayerCaseVuMeter);
		frame.getContentPane().add(caseVuMeter);
		
		caseVuMeter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frameIsComplete = false;
							
				playerSetTime(slider.getValue());

				Settings.videoPlayerCaseVuMeter = caseVuMeter.isSelected();
			}

		});
		
		caseGPU = new JCheckBox(Shutter.language.getProperty("caseGPU"));
		caseGPU.setName("caseGPU");	
		caseGPU.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseGPU.setSelected(Settings.videoPlayerCaseGPUDecoding);
		frame.getContentPane().add(caseGPU);
		
		caseGPU.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frameIsComplete = false;
							
				playerSetTime(slider.getValue());

				Settings.videoPlayerCaseGPUDecoding = caseGPU.isSelected();
			}

		});
		
		casePlaySound = new JCheckBox(Shutter.language.getProperty("casePlaySound"));
		casePlaySound.setName("casePlaySound");	
		casePlaySound.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		casePlaySound.setSelected(Settings.videoPlayerCasePlaySound);
		frame.getContentPane().add(casePlaySound);
		
		casePlaySound.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Settings.videoPlayerCasePlaySound = casePlaySound.isSelected();
			}

		});
			
		caseInternalTc = new JCheckBox(Shutter.language.getProperty("caseTcInterne"));
		caseInternalTc.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		frame.getContentPane().add(caseInternalTc);	
		
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
				
		caseOutH = new JTextField();
		caseOutH.setName("caseOutH");
		caseOutH.setText("00");
		caseOutH.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutH.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutH.setColumns(10);
		caseOutH.setBounds(6, 17, 36, 26);
		grpOut.add(caseOutH);
				
		caseOutM = new JTextField();
		caseOutM.setName("caseOutM");
		caseOutM.setText("00");
		caseOutM.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutM.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutM.setColumns(10);
		caseOutM.setBounds(42, 17, 36, 26);
		grpOut.add(caseOutM);
		
		caseOutS = new JTextField();
		caseOutS.setName("caseOutS");
		caseOutS.setText("00");
		caseOutS.setHorizontalAlignment(SwingConstants.CENTER);
		caseOutS.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		caseOutS.setColumns(10);
		caseOutS.setBounds(78, 17, 36, 26);
		grpOut.add(caseOutS);
		
		caseOutF = new JTextField();
		caseOutF.setName("caseOutF");
		caseOutF.setText("00");
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

			caseOutH.setText(formatter.format(Math.floor(timeOut / FFPROBE.currentFPS / 3600)));
			caseOutM.setText(formatter.format(Math.floor(timeOut / FFPROBE.currentFPS / 60) % 60));
			caseOutS.setText(formatter.format(Math.floor(timeOut / FFPROBE.currentFPS) % 60));    		
			caseOutF.setText(formatter.format(Math.floor(timeOut % FFPROBE.currentFPS)));
		}
		else
		{			
			caseOutH.setText(formatter.format((FFPROBE.totalLength) / 3600000));
	        caseOutM.setText(formatter.format(((FFPROBE.totalLength) / 60000) % 60) );
	        caseOutS.setText(formatter.format((FFPROBE.totalLength / 1000) % 60));				        
	        caseOutF.setText(formatter.format(((int) Math.floor((float) FFPROBE.totalLength / ((float) 1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS))));
		}
	}
	
	private void grpColorimetry() {
		
		grpColorimetry = new JPanel();
		grpColorimetry.setLayout(null);
		grpColorimetry.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("frameColorImage") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpColorimetry.setBackground(new Color(45, 45, 45));
		grpColorimetry.setBounds(6, btnCapture.getY() + btnCapture.getHeight() + 6, 314, 17);
		frame.getContentPane().add(grpColorimetry);		
		
		grpColorimetry.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				int grpInSize = (frame.getHeight() - grpIn.getY());
				if (waveformContainer.isVisible() == false)
				{
					grpInSize = 0;
				}
				
				final int sized = frame.getHeight() - grpInSize - grpColorimetry.getY() - 40 - 12;
								
				if (grpColorimetry.getSize().height > 17)
				{
					Thread changeSize = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
								try {
									int i = sized;									
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i -= 2;
										
										grpColorimetry.setSize(grpColorimetry.getWidth(), i);
										scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
										
										grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
										grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
									
									if (grpColorimetry.getHeight() < 17)
									{
										grpColorimetry.setSize(grpColorimetry.getWidth(), 17);
									}
									
									scrollBarColorimetry.setValue(0);
									scrollBarColorimetry.setVisible(false);
									
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							try {
								
								int i = 17;
								do {
									
									long startTime = System.currentTimeMillis() + 1;
									
									if (Settings.btnDisableAnimations.isSelected())
									{
										i = sized;
									}
									else
										i += 2;

									if (grpColorimetry.getHeight() < btnReset.getY() + btnReset.getHeight() + 30)
									{
										grpColorimetry.setSize(grpColorimetry.getWidth(), i);
										panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);	
										scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
										grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
										grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
																				
										int maxHeight = grpIn.getY();										
										if (grpIn.isVisible() == false)
										{
											maxHeight = frame.getHeight();
										}
										
										if (grpCorrections.getY() + grpCorrections.getHeight() >= maxHeight - 17 - 12 && grpCorrections.getHeight() > 17)
										{
											grpCorrections.setSize(grpCorrections.getWidth(), grpCorrections.getHeight() - 2);
											grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										}
										
										if (grpTransitions.getY() + grpTransitions.getHeight() >= maxHeight - 6 && grpTransitions.getHeight() > 17)
										{
											grpTransitions.setSize(grpTransitions.getWidth(), grpTransitions.getHeight() - 2);
										}
									}
									
									if (Settings.btnDisableAnimations.isSelected())
									{
										if (grpColorimetry.getHeight() > 17)
										{
											int grpInSize = (frame.getHeight() - grpIn.getY());
											if (waveformContainer.isVisible() == false)
											{
												grpInSize = 0;
											}
											
											grpColorimetry.setSize(grpColorimetry.getWidth(), frame.getHeight() - grpInSize - grpColorimetry.getY() - grpCorrections.getHeight() - 12);	
											
											if (grpColorimetry.getHeight() > btnReset.getY() + btnReset.getHeight() + 30)
											{
												grpColorimetry.setSize(grpColorimetry.getWidth(), btnReset.getY() + btnReset.getHeight() + 30);
											}
											panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);
											
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										}
									}
									
									//Animate size
									Shutter.animateSections(startTime);	
									
								} while (i < sized);

								if (grpColorimetry.getHeight() < btnReset.getY() + btnReset.getHeight() + 30)
								{
									scrollBarColorimetry.setMaximum((btnReset.getY() + btnReset.getHeight() + 15) - panelColorimetryComponents.getHeight());
									scrollBarColorimetry.setVisible(true);									
								}
								else
									scrollBarColorimetry.setVisible(false);
								
								if (grpCorrections.getHeight() > 17 && grpCorrections.getHeight() < 144)
								{
									i = grpCorrections.getHeight();			
									
									do {
										
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpCorrections.setSize(grpCorrections.getWidth(), i);
										grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
	
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								}
								else if (grpCorrections.getHeight() < 17)
								{
									grpCorrections.setSize(grpCorrections.getWidth(), 17);
									grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								}
								
								if (grpTransitions.getHeight() > 17 && grpTransitions.getHeight() < 104)
								{
									i = grpTransitions.getHeight();			
									
									do {
										
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpTransitions.setSize(grpTransitions.getWidth(), i);
	
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								}
								else if (grpTransitions.getHeight() < 17)
								{
									grpTransitions.setSize(grpTransitions.getWidth(), 17);
								}
								
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
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
		
		panelColorimetryComponents = new JPanel();
		panelColorimetryComponents.setBackground(new Color(45, 45, 45));
		panelColorimetryComponents.setOpaque(true);
		panelColorimetryComponents.setLayout(null);
		panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);
		panelColorimetryComponents.setLocation(4, 15);	
		grpColorimetry.add(panelColorimetryComponents);
				
		scrollBarColorimetry = new JScrollBar();
		scrollBarColorimetry.setBackground(new Color(45, 45, 45));
		scrollBarColorimetry.setOrientation(JScrollBar.VERTICAL);
		scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
		scrollBarColorimetry.setLocation(grpColorimetry.getWidth() - scrollBarColorimetry.getWidth() - 3, 8);
		scrollBarColorimetry.setVisible(false);
		
		scrollBarColorimetry.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				
					int scrollIncrement = scrollBarColorimetry.getValue() - scrollColorimetryValue;
					for (Component c : panelColorimetryComponents.getComponents())
					{
						if (c instanceof JLabel || c instanceof JSlider && c.getName() != null || c instanceof JComboBox || c instanceof JButton || c instanceof JCheckBox)
						{
							c.setLocation(c.getLocation().x, c.getLocation().y - scrollIncrement);
						}
					}
					scrollColorimetryValue = scrollBarColorimetry.getValue();
		      }			
			
		});
		
		grpColorimetry.add(scrollBarColorimetry);
		
		panelColorimetryComponents.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				if (scrollBarColorimetry.isVisible())
					scrollBarColorimetry.setValue(scrollBarColorimetry.getValue() + e.getWheelRotation() * 10);				
			}
			
		});			
				
		caseEnableColorimetry.setName("caseEnableColorimetry");
		caseEnableColorimetry.setBounds(4, 2, 90, 23);	
		caseEnableColorimetry.setForeground(Color.WHITE);
		caseEnableColorimetry.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseEnableColorimetry.setSelected(true);
		panelColorimetryComponents.add(caseEnableColorimetry);
		
		caseEnableColorimetry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				loadImage(true);			
			}

		});
		
		JLabel lblExposure = new JLabel(Shutter.language.getProperty("lblExposure"));
		lblExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblExposure.setBounds(6, caseEnableColorimetry.getY() + caseEnableColorimetry.getHeight() + 4, 250, 16);		
		panelColorimetryComponents.add(lblExposure);

		panelColorimetryComponents.add(lblExposure);
		
		sliderExposure.setName("sliderExposure");
		sliderExposure.setMaximum(100);
		sliderExposure.setMinimum(-100);
		sliderExposure.setValue(0);		
		sliderExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderExposure.setBounds(4, lblExposure.getY() + lblExposure.getHeight(), 284, 22);	
		
		sliderExposure.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderExposure.setValue(0);	
					lblExposure.setText(Shutter.language.getProperty("lblExposure"));
				}
			}		

		});
		
		sliderExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if (sliderExposure.getValue() == 0)
				{
					lblExposure.setText(Shutter.language.getProperty("lblExposure"));
				}
				else
				{
					lblExposure.setText(Shutter.language.getProperty("lblExposure") + " " + sliderExposure.getValue());
				}	
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderExposure);
		
		JLabel lblGamma = new JLabel(Shutter.language.getProperty("lblGamma"));
		lblGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGamma.setBounds(6, sliderExposure.getY() + sliderExposure.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblGamma);

		panelColorimetryComponents.add(lblGamma);
		
		sliderGamma.setName("sliderGamma");
		sliderGamma.setMaximum(90);
		sliderGamma.setMinimum(-90);
		sliderGamma.setValue(0);		
		sliderGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGamma.setBounds(4, lblGamma.getY() + lblGamma.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderGamma.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGamma.setValue(0);	
					lblGamma.setText(Shutter.language.getProperty("lblGamma"));
				}
			}		

		});
	
		sliderGamma.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGamma.getValue() == 0)
				{
					lblGamma.setText(Shutter.language.getProperty("lblGamma"));
				}
				else
				{
					lblGamma.setText(Shutter.language.getProperty("lblGamma") + " " + sliderGamma.getValue());
				}	
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderGamma);
				
		JLabel lblContrast = new JLabel(Shutter.language.getProperty("lblContrast"));
		lblContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblContrast.setBounds(6, sliderGamma.getY() + sliderGamma.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblContrast);
		
		panelColorimetryComponents.add(lblContrast);
		
		sliderContrast.setName("sliderContrast");
		sliderContrast.setMaximum(100);
		sliderContrast.setMinimum(-100);
		sliderContrast.setValue(0);		
		sliderContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderContrast.setBounds(4, lblContrast.getY() + lblContrast.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderContrast.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderContrast.setValue(0);	
					lblContrast.setText(Shutter.language.getProperty("lblContrast"));
				}
			}

		});
		
		sliderContrast.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderContrast.getValue() == 0)
				{
					lblContrast.setText(Shutter.language.getProperty("lblContrast"));
				}
				else
				{
					lblContrast.setText(Shutter.language.getProperty("lblContrast") + " " + sliderContrast.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderContrast);
		
		JLabel lblWhite = new JLabel(Shutter.language.getProperty("lblWhite"));
		lblWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblWhite.setBounds(6, sliderContrast.getY() + sliderContrast.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblWhite);
		
		panelColorimetryComponents.add(lblWhite);
		
		sliderWhite.setName("sliderWhite");
		sliderWhite.setMaximum(100);
		sliderWhite.setMinimum(-100);
		sliderWhite.setValue(0);		
		sliderWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderWhite.setBounds(4, lblWhite.getY() + lblWhite.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderWhite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderWhite.setValue(0);	
					lblWhite.setText(Shutter.language.getProperty("lblWhite"));
				}
			}

		});
		
		sliderWhite.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderWhite.getValue() == 0)
				{
					lblWhite.setText(Shutter.language.getProperty("lblWhite"));
				}
				else
				{
					lblWhite.setText(Shutter.language.getProperty("lblWhite") + " " + sliderWhite.getValue());
				}

				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderWhite);
		
		JLabel lblBlack = new JLabel(Shutter.language.getProperty("lblBlack"));
		lblBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBlack.setBounds(6, sliderWhite.getY() + sliderWhite.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblBlack);
		
		panelColorimetryComponents.add(lblBlack);
		
		sliderBlack.setName("sliderBlack");
		sliderBlack.setMaximum(100);
		sliderBlack.setMinimum(-100);
		sliderBlack.setValue(0);		
		sliderBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBlack.setBounds(4, lblBlack.getY() + lblBlack.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBlack.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBlack.setValue(0);	
					lblBlack.setText(Shutter.language.getProperty("lblBlack"));
				}
			}

		});
		
		sliderBlack.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBlack.getValue() == 0)
				{
					lblBlack.setText(Shutter.language.getProperty("lblBlack"));
				}
				else
				{
					lblBlack.setText(Shutter.language.getProperty("lblBlack") + " " + sliderBlack.getValue());
				}

				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderBlack);
		
		JLabel lblHighlights = new JLabel(Shutter.language.getProperty("lblHighlights"));
		lblHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHighlights.setBounds(6, sliderBlack.getY() + sliderBlack.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblHighlights);
		
		panelColorimetryComponents.add(lblHighlights);
		
		sliderHighlights.setName("sliderHighlights");
		sliderHighlights.setMaximum(100);
		sliderHighlights.setMinimum(-100);
		sliderHighlights.setValue(0);		
		sliderHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHighlights.setBounds(4, lblHighlights.getY() + lblHighlights.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderHighlights.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderHighlights.setValue(0);	
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights"));
				}
			}

		});
		
		sliderHighlights.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHighlights.getValue() == 0)
				{
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights"));
				}
				else
				{
					lblHighlights.setText(Shutter.language.getProperty("lblHighlights") + " " + sliderHighlights.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderHighlights);
		
		JLabel lblMediums = new JLabel(Shutter.language.getProperty("lblMediums"));
		lblMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblMediums.setBounds(6, sliderHighlights.getY() + sliderHighlights.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblMediums);
		
		panelColorimetryComponents.add(lblMediums);
		
		sliderMediums.setName("sliderMediums");
		sliderMediums.setMaximum(100);
		sliderMediums.setMinimum(-100);
		sliderMediums.setValue(0);		
		sliderMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderMediums.setBounds(4, lblMediums.getY() + lblMediums.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderMediums.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderMediums.setValue(0);	
					lblMediums.setText(Shutter.language.getProperty("lblMediums"));
				}
			}

		});
		
		sliderMediums.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderMediums.getValue() == 0)
				{
					lblMediums.setText(Shutter.language.getProperty("lblMediums"));
				}
				else
				{
					lblMediums.setText(Shutter.language.getProperty("lblMediums") + " " + sliderMediums.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderMediums);
		
		JLabel lblShadows = new JLabel(Shutter.language.getProperty("lblShadows"));
		lblShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblShadows.setBounds(6, sliderMediums.getY() + sliderMediums.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblShadows);
		
		panelColorimetryComponents.add(lblShadows);
		
		sliderShadows.setName("sliderShadows");
		sliderShadows.setMaximum(100);
		sliderShadows.setMinimum(-100);
		sliderShadows.setValue(0);		
		sliderShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderShadows.setBounds(4, lblShadows.getY() + lblShadows.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderShadows.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderShadows.setValue(0);	
					lblShadows.setText(Shutter.language.getProperty("lblShadows"));
				}
			}

		});
		
		sliderShadows.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderShadows.getValue() == 0)
				{
					lblShadows.setText(Shutter.language.getProperty("lblShadows"));
				}
				else
				{
					lblShadows.setText(Shutter.language.getProperty("lblShadows") + " " + sliderShadows.getValue());
				}

				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderShadows);
						
		JLabel lblBalance = new JLabel(Shutter.language.getProperty("lblBalance"));
		lblBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBalance.setBounds(6, sliderShadows.getY() + sliderShadows.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblBalance);
		
		panelColorimetryComponents.add(lblBalance);
		
		sliderBalance.setName("sliderBalance");
		sliderBalance.setMaximum(12000);
		sliderBalance.setMinimum(1000);
		sliderBalance.setValue(6500);		
		sliderBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBalance.setBounds(4, lblBalance.getY() + lblBalance.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBalance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBalance.setValue(6500);	
					lblBalance.setText(Shutter.language.getProperty("lblBalance"));
				}
			}

		});
		
		sliderBalance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBalance.getValue() == 6500)
				{
					lblBalance.setText(Shutter.language.getProperty("lblBalance"));
				}
				else
				{
					lblBalance.setText(Shutter.language.getProperty("lblBalance") + " " + sliderBalance.getValue() + "k");
				}

				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderBalance);
		
		JLabel lblHUE = new JLabel(Shutter.language.getProperty("lblHUE"));
		lblHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHUE.setBounds(6, sliderBalance.getY() + sliderBalance.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblHUE);
		
		panelColorimetryComponents.add(lblHUE);
		
		sliderHUE.setName("sliderHUE");
		sliderHUE.setMaximum(100);
		sliderHUE.setMinimum(-100);
		sliderHUE.setValue(0);		
		sliderHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHUE.setBounds(4, lblHUE.getY() + lblHUE.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderHUE.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderHUE.setValue(0);	
					lblHUE.setText(Shutter.language.getProperty("lblHUE"));
				}
			}

		});
		
		sliderHUE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHUE.getValue() == 0)
				{
					lblHUE.setText(Shutter.language.getProperty("lblHUE"));
				}
				else
				{
					lblHUE.setText(Shutter.language.getProperty("lblHUE") + " " + sliderHUE.getValue());
				}

				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderHUE);
		
		JLabel lblRGB = new JLabel(Shutter.language.getProperty("lblRGB"));
		lblRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblRGB.setBounds(6, sliderHUE.getY() + sliderHUE.getHeight() + 6, lblRGB.getPreferredSize().width + 4, 16);		
		panelColorimetryComponents.add(lblRGB);
		
		panelColorimetryComponents.add(lblRGB);
		
		comboRGB.setName("comboRGB");
		comboRGB.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("setAll"), Shutter.language.getProperty("setHigh"), Shutter.language.getProperty("setMedium"), Shutter.language.getProperty("setLow")}));
		comboRGB.setMaximumRowCount(10);
		comboRGB.setEditable(false);
		comboRGB.setSelectedIndex(0);
		comboRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboRGB.setBounds(lblRGB.getX() + lblRGB.getWidth() + 3, lblRGB.getY() - 3, 100, 22);		
		panelColorimetryComponents.add(comboRGB);
		
		comboRGB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
				{
					sliderRED.setValue(Colorimetry.allR);
					sliderGREEN.setValue(Colorimetry.allG);
					sliderBLUE.setValue(Colorimetry.allB);	
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
				{
					sliderRED.setValue(Colorimetry.lowR);
					sliderGREEN.setValue(Colorimetry.lowG);
					sliderBLUE.setValue(Colorimetry.lowB);						
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
				{
					sliderRED.setValue(Colorimetry.mediumR);
					sliderGREEN.setValue(Colorimetry.mediumG);
					sliderBLUE.setValue(Colorimetry.mediumB);					
				}
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
				{
					sliderRED.setValue(Colorimetry.highR);
					sliderGREEN.setValue(Colorimetry.highG);
					sliderBLUE.setValue(Colorimetry.highB);	
				}				
			}
			
		});
		
		JLabel lblR = new JLabel(Shutter.language.getProperty("lblRED"));
		lblR.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblR.setBounds(6, comboRGB.getY() + comboRGB.getHeight() + 3, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblR);
		
		panelColorimetryComponents.add(lblR);
				
		sliderRED.setName("sliderRED");
		sliderRED.setMaximum(100);
		sliderRED.setMinimum(-100);
		sliderRED.setValue(0);		
		sliderRED.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderRED.setBounds(4, lblR.getY() + lblR.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderRED.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderRED.setValue(0);
					lblR.setText(Shutter.language.getProperty("lblRED"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						Colorimetry.allR = sliderRED.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						Colorimetry.lowR = sliderRED.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						Colorimetry.mediumR = sliderRED.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						Colorimetry.highR = sliderRED.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allR = sliderRED.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowR = sliderRED.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumR = sliderRED.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highR = sliderRED.getValue();
			}
		
		});
		
		sliderRED.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderRED.getValue() == 0)
				{
					lblR.setText(Shutter.language.getProperty("lblRED"));
				}
				else
				{
					lblR.setText(Shutter.language.getProperty("lblRED") + " " + sliderRED.getValue());
				}
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allR = sliderRED.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowR = sliderRED.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumR = sliderRED.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highR = sliderRED.getValue();
														
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderRED);
		
		JLabel lblG = new JLabel(Shutter.language.getProperty("lblGREEN"));
		lblG.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblG.setBounds(6, sliderRED.getY() + sliderRED.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblG);
		
		panelColorimetryComponents.add(lblG);
		
		sliderGREEN.setName("sliderGREEN");
		sliderGREEN.setMaximum(100);
		sliderGREEN.setMinimum(-100);
		sliderGREEN.setValue(0);		
		sliderGREEN.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGREEN.setBounds(4, lblG.getY() + lblG.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderGREEN.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGREEN.setValue(0);
					lblG.setText(Shutter.language.getProperty("lblGREEN"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						Colorimetry.allG = sliderGREEN.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						Colorimetry.lowG = sliderGREEN.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						Colorimetry.mediumG = sliderGREEN.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						Colorimetry.highG = sliderGREEN.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allG = sliderGREEN.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowG = sliderGREEN.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumG = sliderGREEN.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highG = sliderGREEN.getValue();
			}
		
		});
		
		sliderGREEN.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGREEN.getValue() == 0)
				{
					lblG.setText(Shutter.language.getProperty("lblGREEN"));
				}
				else
				{
					lblG.setText(Shutter.language.getProperty("lblGREEN") + " " + sliderGREEN.getValue());
				}
				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allG = sliderGREEN.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowG = sliderGREEN.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumG = sliderGREEN.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highG = sliderGREEN.getValue();
														
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderGREEN);
		
		JLabel lblB = new JLabel(Shutter.language.getProperty("lblBLUE"));
		lblB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblB.setBounds(6, sliderGREEN.getY() + sliderGREEN.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblB);
		
		panelColorimetryComponents.add(lblB);
		
		sliderBLUE.setName("sliderBLUE");
		sliderBLUE.setMaximum(100);
		sliderBLUE.setMinimum(-100);
		sliderBLUE.setValue(0);		
		sliderBLUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBLUE.setBounds(4, lblB.getY() + lblB.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderBLUE.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderBLUE.setValue(0);
					lblB.setText(Shutter.language.getProperty("lblBLUE"));
					
					if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
						Colorimetry.allB = sliderBLUE.getValue();	
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
						Colorimetry.lowB = sliderBLUE.getValue();					
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
						Colorimetry.mediumB = sliderBLUE.getValue();				
					else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						Colorimetry.highB = sliderBLUE.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allB = sliderBLUE.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowB = sliderBLUE.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumB = sliderBLUE.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highB = sliderBLUE.getValue();
			}
		
		});
		
		sliderBLUE.addChangeListener(new ChangeListener() {
		
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBLUE.getValue() == 0)
				{
					lblB.setText(Shutter.language.getProperty("lblBLUE"));
				}
				else
				{
					lblB.setText(Shutter.language.getProperty("lblBLUE") + " " + sliderBLUE.getValue());
				}
							
				if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
					Colorimetry.allB = sliderBLUE.getValue();	
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
					Colorimetry.lowB = sliderBLUE.getValue();					
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
					Colorimetry.mediumB = sliderBLUE.getValue();				
				else if (comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
					Colorimetry.highB = sliderBLUE.getValue();
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderBLUE);
		
		JLabel lblSaturation = new JLabel(Shutter.language.getProperty("lblSaturation"));
		lblSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblSaturation.setBounds(6, sliderBLUE.getY() + sliderBLUE.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblSaturation);
		
		panelColorimetryComponents.add(lblSaturation);
		
		sliderSaturation.setName("sliderSaturation");
		sliderSaturation.setMaximum(100);
		sliderSaturation.setMinimum(-100);
		sliderSaturation.setValue(0);		
		sliderSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderSaturation.setBounds(4, lblSaturation.getY() + lblSaturation.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderSaturation.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderSaturation.setValue(0);	
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation"));
				}
			}

		});
		
		sliderSaturation.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderSaturation.getValue() == 0)
				{
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation"));
				}
				else
				{
					lblSaturation.setText(Shutter.language.getProperty("lblSaturation") + " " + sliderSaturation.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderSaturation);
			
		JLabel lblVibrance = new JLabel(Shutter.language.getProperty("lblVibrance"));
		lblVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVibrance.setBounds(6, sliderSaturation.getY() + sliderSaturation.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblVibrance);
		
		panelColorimetryComponents.add(lblVibrance);
		
		sliderVibrance.setName("sliderVibrance");
		sliderVibrance.setMaximum(100);
		sliderVibrance.setMinimum(-100);
		sliderVibrance.setValue(0);		
		sliderVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVibrance.setBounds(4, lblVibrance.getY() + lblVibrance.getHeight(), sliderExposure.getWidth(), 22);	
				
		panelColorimetryComponents.add(sliderVibrance);			
		
		comboVibrance.setName("comboVibrance");
		comboVibrance.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("intensity"), Shutter.language.getProperty("red"), Shutter.language.getProperty("green"), Shutter.language.getProperty("blue")}));
		comboVibrance.setMaximumRowCount(10);
		comboVibrance.setEditable(false);
		comboVibrance.setSelectedIndex(0);
		comboVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboVibrance.setBounds(scrollBarColorimetry.getX() - scrollBarColorimetry.getWidth() - 94, lblVibrance.getY() - 3, 100, 22);		
		panelColorimetryComponents.add(comboVibrance);
		
		comboVibrance.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
				{
					sliderVibrance.setValue(Colorimetry.vibranceValue);
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
				{
					sliderVibrance.setValue(Colorimetry.vibranceR);					
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
				{
					sliderVibrance.setValue(Colorimetry.vibranceG);					
				}
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
				{
					sliderVibrance.setValue(Colorimetry.vibranceB);
				}				
			}
			
		});
		
		sliderVibrance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderVibrance.setValue(0);	
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance"));
				
					if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
						Colorimetry.vibranceValue = sliderVibrance.getValue();	
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
						Colorimetry.vibranceR = sliderVibrance.getValue();					
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
						Colorimetry.vibranceG = sliderVibrance.getValue();				
					else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
						Colorimetry.vibranceB = sliderVibrance.getValue();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
					Colorimetry.vibranceValue = sliderVibrance.getValue();	
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
					Colorimetry.vibranceR = sliderVibrance.getValue();					
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
					Colorimetry.vibranceG = sliderVibrance.getValue();				
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
					Colorimetry.vibranceB = sliderVibrance.getValue();
			}			

		});
		
		sliderVibrance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVibrance.getValue() == 0)
				{
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance"));
				}
				else
				{
					lblVibrance.setText(Shutter.language.getProperty("lblVibrance") + " " + sliderVibrance.getValue());
				}
				
				if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("intensity")))
					Colorimetry.vibranceValue = sliderVibrance.getValue();	
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("red")))	
					Colorimetry.vibranceR = sliderVibrance.getValue();					
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("green")))		
					Colorimetry.vibranceG = sliderVibrance.getValue();				
				else if (comboVibrance.getSelectedItem().equals(Shutter.language.getProperty("blue")))
					Colorimetry.vibranceB = sliderVibrance.getValue();
				
				loadImage(false);
			}
			
		});
				
		JLabel lblGrain = new JLabel(Shutter.language.getProperty("lblGrain"));
		lblGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGrain.setBounds(6, sliderVibrance.getY() + sliderVibrance.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblGrain);
		
		panelColorimetryComponents.add(lblGrain);
		
		sliderGrain.setName("sliderGrain");
		sliderGrain.setMaximum(100);
		sliderGrain.setMinimum(-100);
		sliderGrain.setValue(0);		
		sliderGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGrain.setBounds(4, lblGrain.getY() + lblGrain.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderGrain.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderGrain.setValue(0);	
					lblGrain.setText(Shutter.language.getProperty("lblGrain"));
				}
			}

		});
		
		sliderGrain.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGrain.getValue() == 0)
				{
					lblGrain.setText(Shutter.language.getProperty("lblGrain"));
				}
				else
				{
					lblGrain.setText(Shutter.language.getProperty("lblGrain") + " " + sliderGrain.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderGrain);	
				
		JLabel lblVignette = new JLabel(Shutter.language.getProperty("lblVignette"));
		lblVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVignette.setBounds(6, sliderGrain.getY() + sliderGrain.getHeight() + 4, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblVignette);
		
		panelColorimetryComponents.add(lblVignette);
		
		sliderVignette.setName("sliderVignette");
		sliderVignette.setMaximum(100);
		sliderVignette.setMinimum(-100);
		sliderVignette.setValue(0);		
		sliderVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVignette.setBounds(4, lblVignette.getY() + lblVignette.getHeight(), sliderExposure.getWidth(), 22);	
		
		sliderVignette.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderVignette.setValue(0);	
					lblVignette.setText(Shutter.language.getProperty("lblVignette"));
				}
			}

		});
		
		sliderVignette.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVignette.getValue() == 0)
				{
					lblVignette.setText(Shutter.language.getProperty("lblVignette"));
				}
				else
				{
					lblVignette.setText(Shutter.language.getProperty("lblVignette") + " " + sliderVignette.getValue());
				}
				
				loadImage(false);
			}
			
		});
		
		panelColorimetryComponents.add(sliderVignette);		

		JLabel lblAngle = new JLabel(Shutter.language.getProperty("caseAngle"));
		lblAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblAngle.setBounds(6, sliderVignette.getY() + sliderVignette.getHeight() + 3, lblExposure.getSize().width, 16);		
		panelColorimetryComponents.add(lblAngle);
		
		sliderAngle.setName("sliderAngle");
		sliderAngle.setMaximum(100);
		sliderAngle.setMinimum(-100);
		sliderAngle.setValue(0);		
		sliderAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderAngle.setBounds(4, lblAngle.getY() + lblAngle.getHeight(), sliderExposure.getWidth(), 22);	
		panelColorimetryComponents.add(sliderAngle);	
		
		sliderAngle.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderAngle.setValue(0);	
					lblAngle.setText(Shutter.language.getProperty("caseAngle"));
				}
			}

		});
		
		sliderAngle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if (sliderAngle.getValue() == 0)
				{
					lblAngle.setText(Shutter.language.getProperty("caseAngle"));
				}
				else
				{
					lblAngle.setText(Shutter.language.getProperty("caseAngle") + " " + sliderAngle.getValue());
				}
				
				loadImage(false);
			}
			
		});

		btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(6, sliderAngle.getY() + sliderAngle.getHeight() + 6, sliderAngle.getWidth(), 21);
		panelColorimetryComponents.add(btnReset);		
		
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Colorimetry.allR = 0;
				Colorimetry.allG = 0;
				Colorimetry.allB = 0;
				Colorimetry.highR = 0;
				Colorimetry.highG = 0;
				Colorimetry.highB = 0;
				Colorimetry.mediumR = 0;
				Colorimetry.mediumG = 0;
				Colorimetry.mediumB = 0;
				Colorimetry.lowR = 0;
				Colorimetry.lowG = 0;
				Colorimetry.lowB = 0;
				Colorimetry.vibranceValue = 0;
				Colorimetry.vibranceR = 0;
				Colorimetry.vibranceG = 0;
				Colorimetry.vibranceB = 0;
				Colorimetry.balanceAll = "";
				Colorimetry.balanceHigh = "";
				Colorimetry.balanceMedium = "";
				Colorimetry.balanceLow = "";
				sliderExposure.setValue(0);
				sliderGamma.setValue(0);
				sliderContrast.setValue(0);
				sliderHighlights.setValue(0);
				sliderMediums.setValue(0);
				sliderShadows.setValue(0);
				sliderWhite.setValue(0);
				sliderBlack.setValue(0);
				sliderBalance.setValue(6500);
				sliderHUE.setValue(0);
				sliderRED.setValue(0);
				sliderGREEN.setValue(0);
				sliderBLUE.setValue(0);
				sliderVibrance.setValue(0);	
				sliderSaturation.setValue(0);
				sliderGrain.setValue(0);
				sliderVignette.setValue(0);
				sliderAngle.setValue(0);			
					
				//important
				comboRGB.setSelectedIndex(0);
				comboVibrance.setSelectedIndex(0);
			}
			
		});
			
	}
	
	private void grpCorrections() {
		
		grpCorrections = new JPanel();
		grpCorrections.setLayout(null);
		grpCorrections.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("grpCorrections") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpCorrections.setBackground(new Color(45, 45, 45));
		grpCorrections.setBounds(grpColorimetry.getX(), grpColorimetry.getY() + grpColorimetry.getHeight() + 6, grpColorimetry.getWidth(), 17);
		frame.getContentPane().add(grpCorrections);

		grpCorrections.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				int size = 25;
				for (Component c : grpCorrections.getComponents()) {
					if (c instanceof JCheckBox)
						size += 17;
				}

				final int sized = size;
				if (grpCorrections.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpCorrections.setSize(grpCorrections.getWidth(), i);
										grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										
										int maxHeight = grpIn.getY();										
										if (grpIn.isVisible() == false)
										{
											maxHeight = frame.getHeight();
										}
										
										if (grpTransitions.getY() + grpTransitions.getHeight() >= maxHeight - 6)
										{
											grpColorimetry.setSize(grpColorimetry.getWidth(), grpColorimetry.getHeight() - 1);
											panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);
											if (scrollBarColorimetry.isVisible() == false)
											{
												scrollBarColorimetry.setVisible(true);
											}													
											scrollBarColorimetry.setValue(0);	
											scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
											scrollBarColorimetry.setMaximum((btnReset.getY() + btnReset.getHeight() + 15) - panelColorimetryComponents.getHeight());
											grpCorrections.setLocation(grpCorrections.getX(), grpCorrections.getY() - 1);
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									//Avoid overlapping
									if (Settings.btnDisableAnimations.isSelected())
									{
										resizeAll();
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpCorrections.setSize(grpCorrections.getWidth(), i);
										grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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

		caseStabilisation.setName("caseStabilisation");
		caseStabilisation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseStabilisation.setSize(caseStabilisation.getPreferredSize().width, 23);
		
		caseStabilisation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				//Avoid undesired selection
				if (grpCorrections.getHeight() <= 17)
				{
					caseStabilisation.setSelected(false);
				}
				
				if (caseStabilisation.isSelected() && Shutter.inputDeviceIsRunning == false && FFPROBE.totalLength > 40)
				{
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							frame.setVisible(false);
							
							File file = new File(videoPath);
							try {
								
								//InOut	
								InputAndOutput.getInputAndOutput();	
								
								stabilisation = Corrections.setStabilisation("", file, file.getName(), "");
								
								do {
									Thread.sleep(100);
								} while (FFMPEG.isRunning);
								
								Shutter.enableAll();
								Shutter.progressBar1.setValue(0);
								Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
	
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								
								frame.setVisible(true);	
								
								if (FFMPEG.cancelled)
								{
									stabilisation = "";
								}
								else
								{				
									btnPlay.setText(Shutter.language.getProperty("btnPause"));
									
									float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
									
									//NTSC framerate
									timeIn = Timecode.getNonDropFrameTC(timeIn);
									
									playerSetTime(timeIn);															
									
									do {
										Thread.sleep(100);
									} while (playerIsPlaying() == false);
									
									do {
										Thread.sleep(100);
									} while (playerIsPlaying());
									
									stabilisation = "";
								}
							
							} catch (InterruptedException e) {}		
														
						}
						
					});
					t.start();
				}
				else
				{
					stabilisation = "";
					loadImage(false);
				}
								
				
			}
			
		});
		
		caseDeflicker.setName("caseDeflicker");
		caseDeflicker.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseDeflicker.setSize(caseDeflicker.getPreferredSize().width, 23);		
		
		caseDeflicker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				loadImage(false);
			}
			
		});
		
		caseBanding.setName("caseBanding");
		caseBanding.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseBanding.setSize(caseBanding.getPreferredSize().width + 10, 23); 
				
		caseBanding.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				loadImage(false);
			}
			
		});
		
		caseLimiter.setName("caseLimiter");
		caseLimiter.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseLimiter.setSize(caseLimiter.getPreferredSize().width, 23);
		
		caseLimiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				loadImage(false);
			}
			
		});
		
		caseDetails.setName("caseDetails");
		caseDetails.setToolTipText(Shutter.language.getProperty("tooltipDetails"));
		caseDetails.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseDetails.setSize(caseDetails.getPreferredSize().width + 14, 23);

		caseDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseDetails.isSelected() == false)
				{
					sliderDetails.setValue(0);
					loadImage(false);
				}
			}

		});

		sliderDetails = new JSlider();
		sliderDetails.setName("sliderDetails");
		sliderDetails.setMinorTickSpacing(1);
		sliderDetails.setMaximum(10);
		sliderDetails.setMinimum(-10);
		sliderDetails.setValue(0);
		sliderDetails.setSize(120, 22);

		sliderDetails.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				float value = (float) sliderDetails.getValue() / 10;

				if (value != 0)
					caseDetails.setSelected(true);
				else
					caseDetails.setSelected(false);

				caseDetails.setText(Shutter.language.getProperty("details") + " " + value);
				
				loadImage(false);
			}

		});

		sliderDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDetails.setSelected(true);
				sliderDetails.setEnabled(true);
			}

		});

		caseDenoise.setName("caseDenoise");
		caseDenoise.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseDenoise.setSize(caseDenoise.getPreferredSize().width + 14, 23);

		caseDenoise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseDenoise.isSelected() == false)
				{
					sliderDenoise.setValue(0);
					loadImage(false);
				}
			}

		});

		sliderDenoise = new JSlider();
		sliderDenoise.setName("sliderDenoise");
		sliderDenoise.setMinorTickSpacing(1);
		sliderDenoise.setMaximum(10);
		sliderDenoise.setMinimum(0);
		sliderDenoise.setValue(0);
		sliderDenoise.setSize(sliderDetails.getWidth(), 22);

		sliderDenoise.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				int value = sliderDenoise.getValue();

				if (value != 0)
					caseDenoise.setSelected(true);
				else
					caseDenoise.setSelected(false);

				caseDenoise.setText(Shutter.language.getProperty("noiseSuppression") + " " + value);
				
				loadImage(false);
			}

		});

		sliderDenoise.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDenoise.setSelected(true);
				sliderDenoise.setEnabled(true);
			}

		});

		caseSmoothExposure.setName("caseSmoothExposure");
		caseSmoothExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseSmoothExposure.setSize(caseSmoothExposure.getPreferredSize().width + 20, 23);

		caseSmoothExposure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSmoothExposure.isSelected() == false)
				{
					sliderSmoothExposure.setValue(0);
					loadImage(false);
				}
			}

		});

		sliderSmoothExposure = new JSlider();
		sliderSmoothExposure.setName("sliderSmoothExposure");
		sliderSmoothExposure.setMinorTickSpacing(1);
		sliderSmoothExposure.setMaximum(100);
		sliderSmoothExposure.setMinimum(0);
		sliderSmoothExposure.setValue(0);
		sliderSmoothExposure.setSize(sliderDetails.getWidth(), 22);

		sliderSmoothExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				int value = sliderSmoothExposure.getValue();

				if (value != 0)
					caseSmoothExposure.setSelected(true);
				else
					caseSmoothExposure.setSelected(false);

				caseSmoothExposure.setText(Shutter.language.getProperty("smoothExposure") + " " + value);
				
				loadImage(false);
			}

		});

		sliderSmoothExposure.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseSmoothExposure.setSelected(true);
				sliderSmoothExposure.setEnabled(true);
			}

		});

		caseStabilisation.setLocation(7, 14);
		grpCorrections.add(caseStabilisation);
		caseDeflicker.setLocation(7, caseStabilisation.getLocation().y + 17);
		grpCorrections.add(caseDeflicker);
		caseBanding.setLocation(7, caseDeflicker.getLocation().y + 17);
		grpCorrections.add(caseBanding);
		caseLimiter.setLocation(7, caseBanding.getLocation().y + 17);
		grpCorrections.add(caseLimiter);
		caseDetails.setLocation(7, caseLimiter.getLocation().y + 17);
		grpCorrections.add(caseDetails);
		sliderDetails.setLocation(grpCorrections.getWidth() - sliderDetails.getWidth() - 14, caseDetails.getLocation().y);
		grpCorrections.add(sliderDetails);
		caseDenoise.setLocation(7, caseDetails.getLocation().y + 17);
		grpCorrections.add(caseDenoise);
		sliderDenoise.setLocation(sliderDetails.getX(), caseDenoise.getLocation().y);
		grpCorrections.add(sliderDenoise);
		caseSmoothExposure.setLocation(7, caseDenoise.getLocation().y + 17);
		grpCorrections.add(caseSmoothExposure);						
		sliderSmoothExposure.setLocation(sliderDetails.getX(), caseSmoothExposure.getLocation().y);
		grpCorrections.add(sliderSmoothExposure);
	}
	
	private void grpTransitions() {
		
		grpTransitions = new JPanel();
		grpTransitions.setLayout(null);
		grpTransitions.setVisible(true);
		grpTransitions.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("grpTransitions") + " ", 0,
				0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpTransitions.setBackground(new Color(45, 45, 45));
		grpTransitions.setBounds(grpCorrections.getX(), grpCorrections.getY() + grpCorrections.getHeight() + 6, grpCorrections.getWidth(), 17);
		frame.getContentPane().add(grpTransitions);
		
		grpTransitions.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				int size = 104;

				final int sized = size;
				if (grpTransitions.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpTransitions.setSize(grpTransitions.getWidth(), i);
										
										int maxHeight = grpIn.getY();										
										if (grpIn.isVisible() == false)
										{
											maxHeight = frame.getHeight();
										}
										
										if (grpTransitions.getY() + grpTransitions.getHeight() >= maxHeight - 6 && grpTransitions.getHeight() > 17)
										{
											grpColorimetry.setSize(grpColorimetry.getWidth(), grpColorimetry.getHeight() - 1);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);;
											
											panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);
											if (scrollBarColorimetry.isVisible() == false)
											{
												scrollBarColorimetry.setVisible(true);
											}													
											scrollBarColorimetry.setValue(0);	
											scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
											scrollBarColorimetry.setMaximum((btnReset.getY() + btnReset.getHeight() + 15) - panelColorimetryComponents.getHeight());
											grpTransitions.setLocation(grpTransitions.getX(), grpTransitions.getY() - 1);
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									//Avoid overlapping
									if (Settings.btnDisableAnimations.isSelected())
									{
										resizeAll();
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpTransitions.setSize(grpTransitions.getWidth(), i);
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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

		//Input
		caseVideoFadeIn = new JCheckBox(Shutter.language.getProperty("lblVideoFadeIn"));
		caseVideoFadeIn.setName("caseVideoFadeIn");
		caseVideoFadeIn.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseVideoFadeIn.setBounds(7, 16, caseVideoFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeIn);
				
		caseVideoFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseVideoFadeIn.isSelected())
				{
					spinnerVideoFadeIn.setEnabled(true);
										
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
					
					playTransition = true;
					playerSetTime(timeIn);
					
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
				}
				else
				{
					spinnerVideoFadeIn.setEnabled(false);		
				}		
				
				Utils.textFieldBackground();
			}
			
		});

		spinnerVideoFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeIn.setName("spinnerVideoFadeIn");
		spinnerVideoFadeIn.setEnabled(false);
		spinnerVideoFadeIn.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		if (Shutter.getLanguage.equals(Locale.of("en").getDisplayLanguage()))
			spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 12, caseVideoFadeIn.getLocation().y + 3, 41, 16);
		else
			spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 6, caseVideoFadeIn.getLocation().y + 3, 41, 16);
		grpTransitions.add(spinnerVideoFadeIn);
		
		spinnerVideoFadeIn.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
		});
				
		JLabel videoInFrames = new JLabel(Shutter.language.getProperty("lblFrames"));
		videoInFrames.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		videoInFrames.setBounds(spinnerVideoFadeIn.getLocation().x + spinnerVideoFadeIn.getWidth() + 4, spinnerVideoFadeIn.getY(), videoInFrames.getPreferredSize().width + 4, 16);
		grpTransitions.add(videoInFrames);
				
		lblFadeInColor = new JLabel(Shutter.language.getProperty("black"));
		lblFadeInColor.setName("lblFadeInColor");
		lblFadeInColor.setBackground(new Color(60, 60, 60));
		lblFadeInColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeInColor.setOpaque(true);
		lblFadeInColor.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		lblFadeInColor.setSize(55, 16);
		lblFadeInColor.setLocation(videoInFrames.getLocation().x + videoInFrames.getWidth() + 3, spinnerVideoFadeIn.getY() + 1);
		grpTransitions.add(lblFadeInColor);
		
		lblFadeInColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (lblFadeInColor.getText().equals(Shutter.language.getProperty("black")))
					lblFadeInColor.setText(Shutter.language.getProperty("white"));
				else
					lblFadeInColor.setText(Shutter.language.getProperty("black"));

				if (caseVideoFadeIn.isSelected())
				{
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
					
					playTransition = true;
					playerSetTime(timeIn);
					
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		caseAudioFadeIn = new JCheckBox(Shutter.language.getProperty("lblAudioFadeIn"));
		caseAudioFadeIn.setName("caseAudioFadeIn");
		caseAudioFadeIn.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAudioFadeIn.setBounds(7, caseVideoFadeIn.getY() + 17, caseAudioFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeIn);
			
		caseAudioFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAudioFadeIn.isSelected())
				{
					spinnerAudioFadeIn.setEnabled(true);
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
					
					playTransition = true;
					playerSetTime(timeIn);
					
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;	
				}
				else
				{
					spinnerAudioFadeIn.setEnabled(false);		
				}	
				
				Utils.textFieldBackground();
			}
			
		});
		
		spinnerAudioFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeIn.setName("spinnerAudioFadeIn");
		spinnerAudioFadeIn.setEnabled(false);
		spinnerAudioFadeIn.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		spinnerAudioFadeIn.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeIn.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeIn);
		
		spinnerAudioFadeIn.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
		});
				
		JLabel audioInFrames = new JLabel(videoInFrames.getText());
		audioInFrames.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		audioInFrames.setBounds(spinnerAudioFadeIn.getLocation().x + spinnerAudioFadeIn.getWidth() + 4, spinnerAudioFadeIn.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioInFrames);
		
		//Output	
		caseVideoFadeOut = new JCheckBox(Shutter.language.getProperty("lblVideoFadeOut"));
		caseVideoFadeOut.setName("caseVideoFadeOut");
		caseVideoFadeOut.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseVideoFadeOut.setBounds(7, caseAudioFadeIn.getY() + caseAudioFadeIn.getHeight(), caseVideoFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeOut);
		
		caseVideoFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (Shutter.inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("incompatibleInputDevice"), Shutter.language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseVideoFadeOut.setSelected(false);
				}
				
				if (caseVideoFadeOut.isSelected())
				{
					spinnerVideoFadeOut.setEnabled(true);
					
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					playerCurrentFrame = timeOut - spinnerValue * 2;
					playerSetTime(playerCurrentFrame);	
					
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
							
						} while (setTime.isAlive());
					}	

					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
				}
				else
				{
					spinnerVideoFadeOut.setEnabled(false);		
				}
				
				Utils.textFieldBackground();
			}
			
		});
				
		spinnerVideoFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeOut.setName("spinnerVideoFadeOut");
		spinnerVideoFadeOut.setEnabled(false);
		spinnerVideoFadeOut.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		spinnerVideoFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseVideoFadeOut.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerVideoFadeOut);
				
		spinnerVideoFadeOut.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
		});
		
		JLabel videoOutFrames = new JLabel(videoInFrames.getText());
		videoOutFrames.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		videoOutFrames.setBounds(spinnerVideoFadeOut.getLocation().x + spinnerVideoFadeOut.getWidth() + 4, spinnerVideoFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(videoOutFrames);
		
		lblFadeOutColor = new JLabel(Shutter.language.getProperty("black"));
		lblFadeOutColor.setName("lblFadeOutColor");
		lblFadeOutColor.setBackground(new Color(60, 60, 60));
		lblFadeOutColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeOutColor.setOpaque(true);
		lblFadeOutColor.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		lblFadeOutColor.setSize(55, 16);
		lblFadeOutColor.setLocation(lblFadeInColor.getX(), spinnerVideoFadeOut.getY() + 1);
		grpTransitions.add(lblFadeOutColor);
		
		lblFadeOutColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e)	{	
				
				if (lblFadeOutColor.getText().equals(Shutter.language.getProperty("black")))
					lblFadeOutColor.setText(Shutter.language.getProperty("white"));
				else
					lblFadeOutColor.setText(Shutter.language.getProperty("black"));
				
				if (caseVideoFadeOut.isSelected())
				{
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					playerCurrentFrame = timeOut - spinnerValue * 2;
					playerSetTime(playerCurrentFrame);	
					
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
							
						} while (setTime.isAlive());
					}	
					
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		caseAudioFadeOut = new JCheckBox(Shutter.language.getProperty("lblAudioFadeOut"));
		caseAudioFadeOut.setName("caseAudioFadeOut");
		caseAudioFadeOut.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAudioFadeOut.setBounds(7, caseVideoFadeOut.getY() + 17, caseAudioFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeOut);
		
		caseAudioFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (Shutter.inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("incompatibleInputDevice"), Shutter.language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseAudioFadeOut.setSelected(false);
				}
				
				if (caseAudioFadeOut.isSelected())
				{
					spinnerAudioFadeOut.setEnabled(true);
					
					float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					playerCurrentFrame = timeOut - spinnerValue * 2;
					playerSetTime(playerCurrentFrame);	
					
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
							
						} while (setTime.isAlive());
					}	
					
					btnPlay.setText(Shutter.language.getProperty("btnPause"));
					playerLoop = true;
				}
				else
				{
					spinnerAudioFadeOut.setEnabled(false);		
				}	

				Utils.textFieldBackground();				
			}
			
		});
				
		spinnerAudioFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeOut.setName("spinnerAudioFadeOut");
		spinnerAudioFadeOut.setEnabled(false);
		spinnerAudioFadeOut.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		spinnerAudioFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeOut.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeOut);
		
		spinnerAudioFadeOut.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
		});
				
		JLabel audioOutFrames = new JLabel(videoInFrames.getText());
		audioOutFrames.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		audioOutFrames.setBounds(spinnerAudioFadeOut.getLocation().x + spinnerAudioFadeOut.getWidth() + 4, spinnerAudioFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioOutFrames);
				
	}
	
	@SuppressWarnings("serial")
	private void grpCrop() {
				
		grpCrop = new JPanel();
		grpCrop.setLayout(null);
		grpCrop.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("frameCropImage") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpCrop.setBackground(new Color(45, 45, 45));
		grpCrop.setSize(grpColorimetry.getWidth(), 17);
		grpCrop.setLocation(frame.getWidth() - grpCrop.getWidth() - 6, grpColorimetry.getY());
		frame.getContentPane().add(grpCrop);		
		
		grpCrop.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				final int sized = 90;
								
				if (grpCrop.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpCrop.setSize(grpCrop.getWidth(), i);									
										grpOverlay.setLocation(grpCrop.getLocation().x, grpCrop.getSize().height + grpCrop.getLocation().y + 6);
										grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										
										if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
										{		
											grpOverlay.setSize(grpOverlay.getWidth(), grpOverlay.getHeight() - 1);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									if (grpOverlay.getHeight() > 17 && grpOverlay.getHeight() < 278)
									{
										i = grpOverlay.getHeight();									
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											grpOverlay.setSize(grpOverlay.getWidth(), i);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
											
											//Animate size
											Shutter.animateSections(startTime);	
											
										} while (i > 17);
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;									
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpCrop.setSize(grpCrop.getWidth(), i);
										grpOverlay.setLocation(grpCrop.getLocation().x, grpCrop.getSize().height + grpCrop.getLocation().y + 6);
										grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
											
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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
	
		caseEnableCrop.setName("caseEnableCrop");
		caseEnableCrop.setBounds(8, 16, 90, 23);	
		caseEnableCrop.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseEnableCrop.setSelected(false);
		grpCrop.add(caseEnableCrop);
		
		caseEnableCrop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				if (caseEnableCrop.isSelected())
				{
					//Make sure the crop is accurate to pixel resolution
					float squareRatio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
					if (Shutter.inputDeviceIsRunning == false && FFPROBE.imageRatio != squareRatio)
					{
						ratioChanged = true;
						FFPROBE.imageRatio = squareRatio;  
						resizeAll();
						
						frameIsComplete = false;
						
						playerSetTime(playerCurrentFrame);
					}
					
					//Important
					selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);	
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();
					
					for (Component c : grpCrop.getComponents())
					{
						c.setEnabled(true);
					}
					
					frameCropX = player.getLocation().x;
					frameCropY = player.getLocation().y;
					
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();					
					checkSelection();
					
					player.add(selection);
					player.add(overImage);
				}
				else
				{			
					//Come back to original DAR
					if (Shutter.inputDeviceIsRunning == false && ratioChanged)
					{
						FFPROBE.Data(videoPath);					
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
						resizeAll();
						
						frameIsComplete = false;
						
						playerSetTime(playerCurrentFrame);
					}
					ratioChanged = false;	
					
					for (Component c : grpCrop.getComponents())
					{
						if (c instanceof JCheckBox == false)
						{
							c.setEnabled(false);
						}
					}
					
					player.remove(selection);
					player.remove(overImage);	
					
					comboPreset.setSelectedIndex(0);
				}		

				if (frameVideo != null)
				{
					player.repaint();
				}
			}

		});
		
		JLabel lblPresets = new JLabel(Shutter.language.getProperty("lblPresets"));
		lblPresets.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblPresets.setEnabled(false);
		lblPresets.setBounds(caseEnableCrop.getX() + caseEnableCrop.getWidth() + 7, caseEnableCrop.getY() + 3, lblPresets.getPreferredSize().width, 16);		
		grpCrop.add(lblPresets);
		
		final String presetsList[] = { Shutter.language.getProperty("aucun"), "auto", "2.75", "2.55", "2.39", "2.35", "2.33", "1.91", "1.85", "16/9", "4/3", "1", "9/16"};
		
		comboPreset.setName("comboPreset");
		comboPreset.setModel(new DefaultComboBoxModel<String>(presetsList));
		comboPreset.setMaximumRowCount(10);
		comboPreset.setEnabled(false);
		comboPreset.setEditable(true);
		comboPreset.setSelectedIndex(0);
		comboPreset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		comboPreset.setBounds(lblPresets.getX() + lblPresets.getWidth() + 4, lblPresets.getY() - 2, 82, 22);		
		grpCrop.add(comboPreset);
		
		comboPreset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboPreset.getSelectedIndex() == 0)
				{	
					selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();	
					
					checkSelection();
				}	
				else if (comboPreset.getSelectedIndex() == 1) //Auto
				{	
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {							
							
							File file = new File(videoPath);
							
							String cmd;
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								cmd =  " -an -t 1 -vf cropdetect -f null -";					
							else
								cmd =  " -an -t 1 -vf cropdetect -f null -" + '"';	
							
							FFMPEG.cropdetect = "";
							
							//Input point
							String inputPoint = " -ss " + (float) (playerCurrentFrame) * inputFramerateMS + "ms";
							if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
								inputPoint = "";
							
							FFMPEG.run(inputPoint + " -i " + '"' + file + '"' + cmd);	
							
							try {
								do {
									Thread.sleep(100);
								} while(FFMPEG.runProcess.isAlive());
							} catch (Exception er) {}	
								
							Shutter.enableAll();
													
							if (FFMPEG.cropdetect != "")
							{
								String c[] = FFMPEG.cropdetect.split(":");
								
								textCropPosX.setText(c[2]);						
								textCropWidth.setText(c[0]);
								textCropHeight.setText(c[1]);
								textCropPosY.setText(c[3]);
								
								int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * player.getHeight()) / FFPROBE.imageHeight);	
								int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * player.getWidth()) / FFPROBE.imageWidth);
								int width = (int) Math.ceil((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
								int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * player.getWidth()) / FFPROBE.imageWidth);
								
								if (width > player.getWidth())
									width = player.getWidth();
								
								if (height > player.getHeight())
									height = player.getHeight();
								
								selection.setBounds(x, y, width, height);
							}	
							
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}						
					});
					t.start();							
				}
				else if (comboPreset.getSelectedItem().toString().isEmpty() == false)
				{
					try {
						
						String s[] = FFPROBE.imageResolution.split("x");
						int w = Integer.parseInt(s[0]);
						int h = Integer.parseInt(s[1]);		
						
						String inputRatio = String.valueOf((float) Integer.parseInt(s[0]) / Integer.parseInt(s[1]));
						
						if (inputRatio.length() > 4)
							inputRatio = inputRatio.substring(0,4);		
						
						float outputRatio;
						if (comboPreset.getSelectedItem().toString().contains("/"))
						{
							String d[] = comboPreset.getSelectedItem().toString().split("/");
							outputRatio = (float) Integer.parseInt(d[0]) / Integer.parseInt(d[1]);
						}
						else
							outputRatio = Float.parseFloat(comboPreset.getSelectedItem().toString());
						
						if (outputRatio == 1.33f)
						{
							outputRatio = 4/3f;
						}
						else if (outputRatio == 0.8f)
						{
							outputRatio = 4/5f;
						}
						else if (outputRatio == 1.77f)
						{
							outputRatio = 16/9f;					
						}
						else if (outputRatio == 2.33f)
						{
							outputRatio = 21/9f;
						}
						
						if (outputRatio < Float.parseFloat(inputRatio))
						{		
							textCropPosY.setText("0");
							textCropHeight.setText(s[1]);
							textCropWidth.setText(String.valueOf(Math.round(h * outputRatio)));							
							textCropPosX.setText(String.valueOf((w - Integer.parseInt(textCropWidth.getText())) / 2));				
						}
						else
						{	
							textCropPosX.setText("0");						
							textCropWidth.setText(s[0]);
							textCropHeight.setText(String.valueOf(Math.round(w / outputRatio)));
							textCropPosY.setText(String.valueOf((h - Integer.parseInt(textCropHeight.getText())) / 2));			
						}
						
						int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * player.getHeight()) / FFPROBE.imageHeight);	
						int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * player.getWidth()) / FFPROBE.imageWidth);
						int width = (int) Math.ceil((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
						int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * player.getWidth()) / FFPROBE.imageWidth);
						
						if (width > player.getWidth())
							width = player.getWidth();
						
						if (height > player.getHeight())
							height = player.getHeight();

						selection.setBounds(x, y, width, height);
						
			        } catch (Exception er) {			        	
	
			        	if (comboPreset.getSelectedItem().toString() != "")
			        	{
			        		JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("wrongValue"), Shutter.language.getProperty("wrongFormat"), JOptionPane.ERROR_MESSAGE);
			        	}
			        }	
				}
			}
			
		});

		//Selection
		selection = new JPanel();
		selection.setOpaque(false);
		selection.setBorder(BorderFactory.createDashedBorder(Utils.themeColor, 4, 4));
		
		selection.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {		
				
				selectionDrag = true;
				
				//Mouse position from click mouse
				int mouseX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - startCropX - frameCropX;				
				int mouseY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - startCropY - frameCropY;
				
				if (mouseX < 0)
					mouseX = 0;
				
				if (mouseY < 0)
					mouseY = 0;
				
				int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - frameCropX;
				int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - frameCropY;
				
				if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
				{
					if (mouseInPictureX >= 0 && mouseInPictureY >= 0)
					{
						selection.setLocation(mouseX, mouseY);						
						
						if (shift)
						{
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), 2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
							selection.setSize(anchorRight - mouseX, anchorBottom - mouseY);
					}
					else
					{
						if (mouseInPictureX < 0)
						{
							selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
						}
						else if (mouseInPictureY < 0)
						{
							selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());
						}							
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
				{
					if (mouseInPictureY >= 0)
					{
						selection.setLocation(selection.getLocation().x, mouseY);
						
						if (shift)
						{
							selection.setSize(selection.getSize().width, 2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
							selection.setSize(selection.getSize().width, anchorBottom - mouseY);
					}
					else
					{
						selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
				{		
					if (mouseInPictureY >= 0 && mouseInPictureX <= player.getWidth())
					{
						if (shift)
						{						
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), mouseY);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), 2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
						{
							selection.setLocation(selection.getLocation().x, mouseY);
							selection.setSize(e.getX(), anchorBottom - mouseY);
						}
					}
					else
					{
						if (mouseInPictureY < 0)
						{
							selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());
						}
						else if (mouseInPictureX > player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), player.getWidth() - selection.getX(), selection.getHeight());
						}		
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
				{										
					if (mouseInPictureX <= player.getWidth())
					{
						if (shift)
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), selection.getSize().height);
						}
						else
							selection.setSize(e.getX(), selection.getSize().height);
					}
					else
					{
						selection.setBounds(selection.getX(), selection.getY(), player.getWidth() - selection.getX(), selection.getHeight());
					}
															
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
				{
					if (mouseInPictureY <= player.getHeight() && mouseInPictureX <= player.getWidth())
					{
						if (shift)						
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(e.getX(), e.getY());
						}
					else
					{
						if (mouseInPictureY > player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), player.getHeight() - selection.getY());
						}
						else if (mouseInPictureX > player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), player.getWidth() - selection.getX(), selection.getHeight());
						}	
					}
					
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
				{		
					if (mouseInPictureY < player.getHeight())
					{
						if (shift)
						{						
							textCropPosY.setText("0");
							selection.setLocation(selection.getLocation().x, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(selection.getSize().width, e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(selection.getSize().width, e.getY());
					}
					else
					{
						selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), player.getHeight() - selection.getY());
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
				{					
					if (mouseInPictureY <= player.getHeight() && mouseInPictureX >= 0)
					{
						if (shift)
						{						
							selection.setLocation(mouseX, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
						{
							selection.setLocation(mouseX, selection.getLocation().y);
							selection.setSize(anchorRight - mouseX, e.getY());	
						}
					}
					else
					{
						if (mouseInPictureY > player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), player.getHeight() - selection.getY());
						}
						else if (mouseInPictureX < 0)
						{
							selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
						}	
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))
				{					
					if (mouseInPictureX >= 0)
					{												
						selection.setLocation(mouseX, selection.getLocation().y);	
						
						if (shift)
						{
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), selection.getSize().height);
						}
						else
							selection.setSize(anchorRight - mouseX, selection.getSize().height);
					}
					else
					{
						selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					if (shift && ctrl)
					{
						selection.setLocation(mouseX, selection.getLocation().y);
					}
					else if (shift)
					{
						selection.setLocation(selection.getLocation().x, mouseY);
					}
					else if (ctrl)
					{
						selection.setLocation(mouseX, selection.getLocation().y);
					}
					else					
						selection.setLocation(mouseX, mouseY);
				}
					
				//Location limits
				if (selection.getX() < 0)
				{
					selection.setLocation(0, selection.getY());
				}
				
				if (selection.getY() < 0)
				{
					selection.setLocation(selection.getX(), 0);
				}
								
				if (selection.getX() + selection.getWidth() > player.getWidth())
				{
					selection.setLocation(player.getWidth() - selection.getWidth(), selection.getY());
				}
				
				if (selection.getY() + selection.getHeight() > player.getHeight())
				{
					selection.setLocation(selection.getX(), player.getHeight() - selection.getHeight());
				}
			
				//Anchor points
				anchorRight = selection.getLocation().x + selection.getWidth();
				anchorBottom = selection.getLocation().y + selection.getHeight();
				
				checkSelection();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if (selectionDrag == false)
				{
					if (e.getX() <= 10 && e.getY() <= 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					}
					else if (e.getX() <= selection.getWidth() - 10 && e.getY() <= 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					}
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10 && e.getY() <= 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					}
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10 && e.getY() >= 10 && e.getY() <= selection.getHeight() - 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					}
					else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10 && e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					}
					else if (e.getX() <= selection.getWidth() - 10 && e.getX() >= 10 && e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					}
					else if (e.getX() <= 10 && e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					}
					else if (e.getX() <= 10 && e.getY() >= 10 && e.getY() <= selection.getHeight() - 10)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					}
					else		
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}
			
		});
		
		selection.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {;
				startCropX = e.getPoint().x;
				startCropY = e.getPoint().y;
 			}
			
			@Override 
			public void mouseReleased(MouseEvent e) {
				
				selectionDrag = false;
				
				if (selection.getLocation().x <= 0 && selection.getLocation().y <= 0) 
				{
					selection.setLocation(0, 0);
				}
				else if (selection.getLocation().x + selection.getWidth() > player.getWidth() && selection.getLocation().y <= 0)
				{
					selection.setLocation(player.getWidth() - selection.getWidth(), 0);
				}
				else if (selection.getLocation().x <= 0 && selection.getLocation().y + selection.getHeight() > player.getHeight())
				{
					selection.setLocation(0, player.getHeight() - selection.getHeight());
				}
				else if (selection.getLocation().x + selection.getWidth() > player.getWidth() && selection.getLocation().y + selection.getHeight() > player.getHeight())
				{
					selection.setLocation(player.getWidth() - selection.getWidth(), player.getHeight() - selection.getHeight());
				}
				else if (selection.getLocation().x + selection.getWidth() > player.getWidth())
				{
					selection.setLocation(player.getWidth() - selection.getWidth(), selection.getLocation().y);
				}
				else if (selection.getLocation().y + selection.getHeight() > player.getHeight())
				{
					selection.setLocation(selection.getLocation().x, player.getHeight() - selection.getHeight());
				}
				else if (selection.getLocation().x <= 0)
				{
					selection.setLocation(0, selection.getLocation().y);
				}
				else if (selection.getLocation().y <= 0)
				{
					selection.setLocation(selection.getLocation().x, 0);
				}				
			}
			
		});		
				
		//Outside of selection
		overImage = new JPanel() 
		{
			public void paintComponent(Graphics g) 
			{				
				Graphics2D g2d = (Graphics2D) g;

				Area outter = new Area(new Rectangle(0, 0, player.getWidth(), player.getHeight()));
                Rectangle inner = new Rectangle(selection.getLocation().x, selection.getLocation().y, selection.getWidth(), selection.getHeight());
                outter.subtract(new Area(inner));
                
                g2d.setColor(new Color(0,0,0,180));
                g2d.fill(outter);
				
			}
		};
		overImage.setBounds(0,0, player.getWidth(), player.getHeight());
		overImage.setOpaque(false);
		overImage.setLayout(null);   
		
		overImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();	
					checkSelection();					
					comboPreset.setSelectedIndex(0);
				}
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {

				if (selectionDrag == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
				
			}
			
		});

		JLabel cropPosX = new JLabel(Shutter.language.getProperty("posX"));
		cropPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		cropPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPosX.setForeground(Utils.themeColor);
		cropPosX.setEnabled(false);
		cropPosX.setBounds(24, caseEnableCrop.getY() + caseEnableCrop.getHeight() + 4, cropPosX.getPreferredSize().width, 16);
		
		textCropPosX = new JTextField("0");
		textCropPosX.setName("textCropPosX");
		textCropPosX.setBounds(cropPosX.getLocation().x + cropPosX.getWidth() + 2, cropPosX.getLocation().y, 34, 16);
		textCropPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropPosX.setEnabled(false);
		textCropPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		JLabel cropPx1 = new JLabel("px");
		cropPx1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPx1.setForeground(Utils.themeColor);
		cropPx1.setEnabled(false);
		cropPx1.setBounds(textCropPosX.getLocation().x + textCropPosX.getWidth() + 2, cropPosX.getLocation().y, cropPx1.getPreferredSize().width, 16);
		
		textCropPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textCropPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int value = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * player.getHeight()) / FFPROBE.imageHeight);	
					selection.setLocation(value, selection.getLocation().y);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textCropPosX.getText().length() >= 4)
					textCropPosX.setText("");				
			}			
			
		});
				
		textCropPosX.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionX = e.getX();
				mouseCropOffsetX = Integer.parseInt(textCropPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textCropPosX.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (textCropPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textCropPosX.setText(String.valueOf(mouseCropOffsetX + (e.getX() - MousePositionX)));
					selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / imageRatio), selection.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});
		
		JLabel cropPosY = new JLabel(Shutter.language.getProperty("posY"));
		cropPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		cropPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPosY.setForeground(Utils.themeColor);
		cropPosY.setEnabled(false);
		cropPosY.setBounds(cropPx1.getX() + cropPx1.getWidth() + 30, cropPosX.getLocation().y, cropPosX.getWidth(), 16);

		textCropPosY = new JTextField("0");
		textCropPosY.setName("textCropPosY");
		textCropPosY.setBounds(cropPosY.getLocation().x + cropPosY.getWidth() + 2, cropPosY.getLocation().y, textCropPosX.getWidth(), 16);
		textCropPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropPosY.setEnabled(false);
		textCropPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textCropPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textCropPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int value = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * player.getWidth()) / FFPROBE.imageWidth);	
					selection.setLocation(selection.getLocation().x, value);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textCropPosY.getText().length() >= 4)
					textCropPosY.setText("");				
			}			
			
		});
		
		textCropPosY.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionY = e.getY();
				mouseCropOffsetY = Integer.parseInt(textCropPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textCropPosY.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textCropPosY.setText(String.valueOf(mouseCropOffsetY + (e.getY() - MousePositionY)));
					selection.setLocation(selection.getLocation().x, (int) Math.round(Integer.valueOf(textCropPosY.getText()) / imageRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel cropPx2 = new JLabel("px");
		cropPx2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPx2.setForeground(Utils.themeColor);
		cropPx2.setEnabled(false);
		cropPx2.setBounds(textCropPosY.getLocation().x + textCropPosY.getWidth() + 2, cropPosY.getLocation().y, cropPosX.getPreferredSize().width, 16);
		
		JLabel lblWidth = new JLabel(Shutter.language.getProperty("lblWidth"));
		lblWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWidth.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblWidth.setForeground(Utils.themeColor);
		lblWidth.setEnabled(false);
		lblWidth.setBounds(textCropPosX.getX() - lblWidth.getPreferredSize().width - 2, cropPosX.getY() + cropPosX.getHeight() + 4, lblWidth.getPreferredSize().width, 16);
		
		textCropWidth = new JTextField("0");
		textCropWidth.setName("textCropWidth");
		textCropWidth.setBounds(textCropPosX.getX(), textCropPosX.getY() + textCropPosX.getHeight() + 4, textCropPosX.getWidth(), 16);
		textCropWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropWidth.setEnabled(false);
		textCropWidth.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textCropWidth.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textCropWidth.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int value = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(value, selection.getHeight());
					checkSelection();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textCropWidth.getText().length() >= 4)
					textCropWidth.setText("");				
			}			
			
		});
		
		textCropWidth.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionX = e.getX();
				mouseCropOffsetX = Integer.parseInt(textCropWidth.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textCropWidth.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropWidth.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textCropWidth.setText(String.valueOf(mouseCropOffsetX + (e.getX() - MousePositionX)));
					int value = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(value , selection.getHeight());
					checkSelection();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});
		
		JLabel cropPx3 = new JLabel("px");
		cropPx3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPx3.setForeground(Utils.themeColor);
		cropPx3.setEnabled(false);
		cropPx3.setBounds(cropPx1.getX(), lblWidth.getY(), cropPosX.getPreferredSize().width, 16);
		
		JLabel lblHeight = new JLabel(Shutter.language.getProperty("lblHeight"));
		lblHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHeight.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblHeight.setForeground(Utils.themeColor);
		lblHeight.setEnabled(false);
		lblHeight.setBounds(textCropPosY.getX() - lblHeight.getPreferredSize().width - 2, cropPosY.getY() + cropPosY.getHeight() + 4, lblHeight.getPreferredSize().width, 16);
		
		textCropHeight = new JTextField("0");
		textCropHeight.setName("textCropHeight");
		textCropHeight.setBounds(lblHeight.getLocation().x + lblHeight.getWidth() + 2, textCropWidth.getY(), textCropPosX.getWidth(), 16);
		textCropHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropHeight.setEnabled(false);
		textCropHeight.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textCropHeight.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textCropHeight.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int value = (int) Math.round((float) (Integer.valueOf(textCropHeight.getText()) * player.getWidth()) / FFPROBE.imageWidth);
					selection.setSize(selection.getWidth(), value);
					checkSelection();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textCropHeight.getText().length() >= 4)
					textCropHeight.setText("");				
			}			
			
		});
		
		textCropHeight.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionY = e.getY();
				mouseCropOffsetY = Integer.parseInt(textCropHeight.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textCropHeight.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropHeight.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textCropHeight.setText(String.valueOf(mouseCropOffsetY + (e.getY() - MousePositionY)));
					int value = (int) Math.round((float)  (Integer.valueOf(textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(selection.getWidth(), value);
					checkSelection();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});
		
		JLabel cropPx4 = new JLabel("px");
		cropPx4.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		cropPx4.setForeground(Utils.themeColor);
		cropPx4.setEnabled(false);
		cropPx4.setBounds(cropPx2.getX(), lblHeight.getY(), cropPosX.getPreferredSize().width, 16);
		
		grpCrop.add(cropPosX);	
		grpCrop.add(textCropPosX);
		grpCrop.add(cropPx1);
		grpCrop.add(cropPosY);	
		grpCrop.add(textCropPosY);
		grpCrop.add(cropPx2);
		grpCrop.add(lblWidth);	
		grpCrop.add(textCropWidth);
		grpCrop.add(cropPx3);
		grpCrop.add(lblHeight);
		grpCrop.add(textCropHeight);	
		grpCrop.add(cropPx4);
		
		for (Component c : grpCrop.getComponents())
		{
			if (c instanceof JCheckBox == false)
			{
				c.setEnabled(false);
			}
		}
	}

	public static void checkSelection() {
		
		float ratioW = (float) FFPROBE.imageWidth / player.getWidth();
		float ratioH = (float) FFPROBE.imageHeight / player.getHeight();
				
		int outW = (int) Math.round(selection.getWidth() * ratioW);
		int outH = (int) Math.round(selection.getHeight() * ratioH);
					
		int Px = (int) Math.round(selection.getLocation().x * ratioW);
		int Py = (int) Math.round(selection.getLocation().y * ratioH);
					
		if (textCropWidth.getText().matches("[0-9]+") && textCropHeight.getText().matches("[0-9]+"))
		{
			if (Px + Integer.valueOf(textCropWidth.getText()) > FFPROBE.imageWidth)
			{
				Px = Px + (FFPROBE.imageWidth - (Px + Integer.valueOf(textCropWidth.getText())));
			}
			
			if (Py + Integer.valueOf(textCropHeight.getText()) > FFPROBE.imageHeight)
			{
				Py = Py + (FFPROBE.imageHeight - (Py + Integer.valueOf(textCropHeight.getText())));
			}
						
			if (Integer.valueOf(textCropWidth.getText()) != FFPROBE.imageWidth)
			{
				textCropPosX.setText(String.valueOf(Px));
			}
			if (Integer.valueOf(textCropHeight.getText()) != FFPROBE.imageHeight)
			{
				textCropPosY.setText(String.valueOf(Py));
			}
		}
		else //First launch
		{
			textCropPosX.setText(String.valueOf(Px));
			textCropPosY.setText(String.valueOf(Py));
		}
				
		if (frame.getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
		{
			textCropWidth.setText(String.valueOf(outW));
			textCropHeight.setText(String.valueOf(outH));
		}	
		
		if (caseEnableCrop.isSelected())
		{
			comboPreset.getEditor().setItem((double) Math.round((double) ((double) outW / outH) * 100.0) / 100.0);		
		}
		else
		{
			comboPreset.getEditor().setItem(Shutter.language.getProperty("aucun"));
		}
	}
		
	@SuppressWarnings("serial")
	private void grpOverlay() {
		
		grpOverlay = new JPanel();
		grpOverlay.setLayout(null);
		grpOverlay.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("caseAddOverlay") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpOverlay.setBackground(new Color(45, 45, 45));
		grpOverlay.setSize(grpCrop.getWidth(), 17);
		grpOverlay.setLocation(frame.getWidth() - grpOverlay.getWidth() - 6, grpCrop.getY() + grpCrop.getHeight() + 6);
		frame.getContentPane().add(grpOverlay);		
		
		grpOverlay.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				final int sized = 278;
								
				if (grpOverlay.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpOverlay.setSize(grpOverlay.getWidth(), i);	
										grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										
										if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
										{		
											if (grpSubtitles.getHeight() > 17)
											{
												grpSubtitles.setSize(grpSubtitles.getWidth(), grpSubtitles.getHeight() - 1);
												grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);												
											}
											
											if (grpWatermark.getHeight() > 17)
											{
												grpWatermark.setSize(grpWatermark.getWidth(), grpWatermark.getHeight() - 1);
											}
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									if (grpSubtitles.getHeight() > 17 && grpSubtitles.getHeight() < 131)
									{
										i = grpSubtitles.getHeight();									
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											grpSubtitles.setSize(grpSubtitles.getWidth(), i);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
											
											//Animate size
											Shutter.animateSections(startTime);	
											
										} while (i > 17);
									}
									
									if (grpWatermark.getHeight() > 17 && grpWatermark.getHeight() < 90)
									{
										i = grpWatermark.getHeight();									
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											grpWatermark.setSize(grpWatermark.getWidth(), i);

											//Animate size
											Shutter.animateSections(startTime);	
											
										} while (i > 17);
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;									
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpOverlay.setSize(grpOverlay.getWidth(), i);
										grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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

		JLabel lblFont = new JLabel(Shutter.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setForeground(Utils.themeColor);
		lblFont.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblFont.setBounds(12, 24, lblFont.getPreferredSize().width, 16);
		grpOverlay.add(lblFont);
		
		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		comboOverlayFont = new JComboBox<String>(Fonts);		
		comboOverlayFont.setName("comboOverlayFont");
		comboOverlayFont.setSelectedItem("Arial");
		comboOverlayFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboOverlayFont.setRenderer(new ComboRendererOverlay(comboOverlayFont));
		comboOverlayFont.setEditable(true);
		comboOverlayFont.setLocation(lblFont.getX() + lblFont.getWidth() + 7, lblFont.getY() - 4);
		comboOverlayFont.setSize(grpOverlay.getWidth() - comboOverlayFont.getX() - 10, 22);
		grpOverlay.add(comboOverlayFont);
				
		comboOverlayFont.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
	    {
			String text = "";
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void keyReleased(KeyEvent e) {
				
					if (comboOverlayFont.getEditor().toString().length() <= 1)
						text = String.valueOf(e.getKeyChar());

					if (Character.isLetterOrDigit(e.getKeyChar())) {
						comboOverlayFont.setModel(new DefaultComboBoxModel(Fonts));
						text += e.getKeyChar();

						ArrayList<String> newList = new ArrayList<String>();
						for (int i = 0; i < comboOverlayFont.getItemCount(); i++) {
							if (Fonts[i].toString().length() >= text.length()) {
								if (Fonts[i].toString().toLowerCase().substring(0, text.length()).contains(text)
										&& Fonts[i].toString().contains(":") == false) {
									newList.add(Fonts[i].toString());
								}
							}
						}

						// Pour éviter d'afficher le premier item
						comboOverlayFont.getEditor().setItem(text);

						if (newList.isEmpty() == false) {
							comboOverlayFont.setModel(new DefaultComboBoxModel(newList.toArray()));
							comboOverlayFont.showPopup();
						}

					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboOverlayFont.setModel(new DefaultComboBoxModel(Fonts));
						comboOverlayFont.getEditor().setItem("");
						comboOverlayFont.hidePopup();
						text = "";
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						e.consume();// Contournement pour éviter le listeDrop
					else
						comboOverlayFont.hidePopup();
			}
					
	    });	
		
		comboOverlayFont.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				comboOverlayFont.setFont(new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, 11));
				timecode.repaint();	
				fileName.repaint();
			}
			
		});
		
		JLabel lblColor = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setForeground(Utils.themeColor);
		lblColor.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblColor.setBounds(lblFont.getX(), lblFont.getY() + lblFont.getHeight() + 11, lblColor.getPreferredSize().width + 4, 16);
		grpOverlay.add(lblColor);
		
		panelTcColor = new JPanel();
		panelTcColor.setName("panelTcColor");
		panelTcColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelTcColor.setBackground(foregroundColor);
		panelTcColor.setBounds(lblColor.getLocation().x + lblColor.getWidth(), lblColor.getY() - 4, 41, 22);
		grpOverlay.add(panelTcColor);
		
		panelTcColor.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
				foregroundColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				
				if (foregroundColor != null)
				{
					panelTcColor.setBackground(foregroundColor);	
					timecode.repaint();
					fileName.repaint();
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
		
		lblTcBackground = new JLabel(Shutter.language.getProperty("lblBackgroundOn"));
		lblTcBackground.setName("lblTcBackground");
		lblTcBackground.setBackground(new Color(60, 60, 60));
		lblTcBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblTcBackground.setOpaque(true);
		lblTcBackground.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 11));
		lblTcBackground.setBounds(panelTcColor.getLocation().x + panelTcColor.getWidth() + 7, lblColor.getLocation().y - 1, 80, 16);
		grpOverlay.add(lblTcBackground);
		
		lblTcBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				{
					lblTcBackground.setText(Shutter.language.getProperty("aucun"));
					textTcOpacity.setText("100");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					textNameOpacity.setText("100");
				}
				else
				{
					lblTcBackground.setText(Shutter.language.getProperty("lblBackgroundOn"));
					textTcOpacity.setText("50");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					textNameOpacity.setText("50");
				}
								
				timecode.repaint();
				fileName.repaint();
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
		lblColor2.setForeground(Utils.themeColor);
		lblColor2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblColor2.setBounds(lblTcBackground.getLocation().x + lblTcBackground.getWidth() + 7, lblColor.getY(), lblColor2.getPreferredSize().width + 4, 16);
		grpOverlay.add(lblColor2);
		
		panelTcColor2 = new JPanel();
		panelTcColor2.setName("panelTcColor2");
		panelTcColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelTcColor2.setBackground(backgroundColor);
		panelTcColor2.setBounds(lblColor2.getLocation().x + lblColor2.getWidth(), panelTcColor.getY(), 41, 22);
		grpOverlay.add(panelTcColor2);
		
		panelTcColor2.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
				backgroundColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				
				if (backgroundColor != null)
				{
					panelTcColor2.setBackground(backgroundColor);	
					timecode.repaint();
					fileName.repaint();
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
		caseAddTimecode.setSelected(false);
		caseAddTimecode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddTimecode.setSize(caseAddTimecode.getPreferredSize().width, 23);
		caseAddTimecode.setLocation(8, lblColor.getY() + lblColor.getHeight() + 9);
		grpOverlay.add(caseAddTimecode);
		
		caseAddTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseAddTimecode.isSelected())
				{
					timecode.repaint();
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					TC4.setEnabled(true);	
					caseShowTimecode.setSelected(false);					
					player.add(timecode);
					
					//Overimage need to be the last component added
					if (caseEnableCrop.isSelected())
					{
						player.remove(selection);
						player.remove(overImage);
						player.add(selection);
						player.add(overImage);
					}					
				} 
				else
				{
					FFPROBE.timecode1 = "";
					FFPROBE.timecode2 = "";
					FFPROBE.timecode3 = "";
					FFPROBE.timecode4 = "";
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					TC4.setEnabled(false);					
					player.remove(timecode);
				}
				
				refreshTimecodeAndText();
				
				player.repaint();
			}

		});
		
		timecode = new JPanel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {				 
		        super.paintComponent(g);
		
		        Graphics2D g2 = (Graphics2D) g;
		
		        //First initialisation
		        boolean resetLocation = false;
		        if (getWidth() == 1)
		        {
		        	resetLocation = true;
		        }
		        
		        //Saving height for spinnerSize
				int width = getWidth();
				int height = getHeight();
		        
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        
				Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(textTcSize.getText()) / imageRatio));
		        font.deriveFont((float) Integer.parseInt(textTcSize.getText()) / imageRatio);
		        g2.setFont(font);
		        		        
		        String dropFrame = ":";
		        if (FFPROBE.dropFrameTC.equals(":") == false  && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f))
	        	{
		        	dropFrame = ";";
	        	}
		        
		   		if (Shutter.caseConform.isSelected())
		   		{
		   			if (Shutter.comboFPS.getSelectedItem().toString().equals("29,97") || Shutter.comboFPS.getSelectedItem().toString().equals("59,94"))
		   			{
		   				dropFrame = ";";
		   			}
		   			else 
		   				dropFrame = ":";
		   		}
		        
		        String str = "00:00:00" + dropFrame + "00";
		        
				if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected()) 
				{												
					float tcH = 0;				
					float tcM = 0;
					float tcS = 0;
					float tcF = 0;
					
					if (caseAddTimecode.isSelected() && TC1.getText().isEmpty() == false && TC2.getText().isEmpty() == false && TC3.getText().isEmpty() == false && TC4.getText().isEmpty() == false)
					{
						tcH = Integer.valueOf(TC1.getText());
						tcM = Integer.valueOf(TC2.getText());
						tcS = Integer.valueOf(TC3.getText());
						tcF = Integer.valueOf(TC4.getText());
					}
					else if (caseShowTimecode.isSelected() && FFPROBE.timecode1.equals("") == false)
					{
						tcH = Integer.valueOf(FFPROBE.timecode1);
						tcM = Integer.valueOf(FFPROBE.timecode2);
						tcS = Integer.valueOf(FFPROBE.timecode3);
						tcF = Integer.valueOf(FFPROBE.timecode4);
					}
					
					tcH = tcH * 3600 * FFPROBE.currentFPS;
					tcM = tcM * 60 * FFPROBE.currentFPS;
					tcS = tcS * FFPROBE.currentFPS;
					
					float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());

					if (caseShowTimecode.isSelected())
					{
						timeIn = 0;
					}
					
					float currentTime = Timecode.setNonDropFrameTC(playerCurrentFrame);
					float offset = (currentTime - timeIn) + tcH + tcM + tcS + tcF;
					
					if (offset < 0)
						offset = 0;
					
			        String h = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 3600));
					String m = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 60) % 60);
					String s = formatter.format(Math.floor(offset / FFPROBE.currentFPS) % 60);    		
					String f = formatter.format(Math.floor(offset % FFPROBE.currentFPS));
			        
			        if (caseAddTimecode.isSelected() && lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
			        {
			        	str = String.format("%.0f", offset);
			        }
			        else
			        	str = h+":"+m+":"+s+ dropFrame +f;	
				}
									
		        Rectangle bounds = getStringBounds(g2, str, 0 ,0);
				
				if (lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
					g2.setColor(new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textTcOpacity.getText()) * 255) /  100)));
				else
					g2.setColor(new Color(0,0,0,0));
									
				GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		        AffineTransform transform = gfxConfig.getDefaultTransform();
		        
				if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
			        g2.fillRect(0, 0, bounds.width, bounds.height / 2);
				else
					g2.fillRect(0, 0, bounds.width, bounds.height);
				
				if (lblTcBackground.getText().equals(Shutter.language.getProperty("aucun")))
					g2.setColor(new Color(foregroundColor.getRed(),foregroundColor.getGreen(),foregroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textNameOpacity.getText()) * 255) /  100)));
				else				
					g2.setColor(foregroundColor);
				
				if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
		        {
		        	Integer offset = bounds.height + (int) bounds.getY();						
					g2.drawString(str, -2, bounds.height / 2 - offset / 2);	
		        }
				else
				{
					Integer offset = bounds.height + (int) bounds.getY();						
					g2.drawString(str, -2, bounds.height - offset);						
				}
				
				setSize(bounds.width, bounds.height);
									
				//Define center position after the size is correct
				if (resetLocation)
				{
					timecode.setLocation(player.getWidth() / 2 - timecode.getWidth() / 2, timecode.getHeight());
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;
				}		
				else if (textTcSize.hasFocus())
				{					
					timecode.setLocation(timecode.getX() + (width - timecode.getWidth()) / 2, timecode.getY() + (height - timecode.getHeight()) / 2);	
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;
				}
				
				refreshTimecodeAndText();
		    }
		 
			private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
			{
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
				return gv.getPixelBounds(null, x, y);
			}
		};
				
		timecode.setSize(1,1);
		timecode.setLayout(null);
		timecode.setOpaque(false);
		timecode.setBackground(new Color(0,0,0,0));
				
		timecode.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getClickCount() == 2)
				{
					timecode.setLocation(player.getWidth() / 2 - timecode.getWidth() / 2, timecode.getHeight());
					
					refreshTimecodeAndText();
					
					//Saving the location
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;
				}
				
				tcPosX = e.getLocationOnScreen().x;
				tcPosY = e.getLocationOnScreen().y;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {				
				tcLocX = timecode.getLocation().x;
				tcLocY = timecode.getLocation().y;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
				
		lblTimecode.setName("lblTimecode");
		lblTimecode.setBackground(new Color(60, 60, 60));
		lblTimecode.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimecode.setOpaque(true);
		lblTimecode.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 11));
		lblTimecode.setBounds(caseAddTimecode.getLocation().x + caseAddTimecode.getWidth() + 2, caseAddTimecode.getLocation().y + 3, 70, 16);
		grpOverlay.add(lblTimecode);
		
		lblTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (lblTimecode.getText().equals(Shutter.language.getProperty("lblTimecode")))
					lblTimecode.setText(Shutter.language.getProperty("lblFrameNumber"));
				else
					lblTimecode.setText(Shutter.language.getProperty("lblTimecode"));
				
				if (frameVideo != null)
				{
					player.repaint();
				}
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
		TC1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC1.setColumns(10);
		TC1.setBounds(lblTimecode.getX() + lblTimecode.getWidth() + 7, caseAddTimecode.getY(), 32, 21);
		grpOverlay.add(TC1);
		
		TC2.setName("TC2");
		TC2.setEnabled(false);
		TC2.setText("00");
		TC2.setHorizontalAlignment(SwingConstants.CENTER);
		TC2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC2.setColumns(10);
		TC2.setBounds(TC1.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC2);
		
		TC3.setName("TC3");
		TC3.setEnabled(false);
		TC3.setText("00");
		TC3.setHorizontalAlignment(SwingConstants.CENTER);
		TC3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC3.setColumns(10);
		TC3.setBounds(TC2.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC3);

		TC4.setName("TC4");
		TC4.setEnabled(false);
		TC4.setText("00");
		TC4.setHorizontalAlignment(SwingConstants.CENTER);
		TC4.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC4.setColumns(10);
		TC4.setBounds(TC3.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC4);
		
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
				timecode.repaint();
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
				timecode.repaint();
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
				timecode.repaint();
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
				timecode.repaint();
			}
		});
				
		caseShowTimecode.setName("caseShowTimecode");
		caseShowTimecode.setSelected(false);
		caseShowTimecode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseShowTimecode.setEnabled(true);
		caseShowTimecode.setSize(caseShowTimecode.getPreferredSize().width, 23);
		caseShowTimecode.setLocation(caseAddTimecode.getX(), caseAddTimecode.getY() + caseAddTimecode.getHeight());
		grpOverlay.add(caseShowTimecode);
				
		caseShowTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseShowTimecode.isSelected())
				{
					//Timecode info
					if (Utils.inputDeviceIsRunning == false)
					{						
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
							caseShowTimecode.setSelected(false);
							caseAddTimecode.doClick();
						}
						else
						{
							TC1.setEnabled(false);
							TC2.setEnabled(false);
							TC3.setEnabled(false);
							TC4.setEnabled(false);
							caseAddTimecode.setSelected(false);					
							player.add(timecode);
							
							//Overimage need to be the last component added
							if (caseEnableCrop.isSelected())
							{
								player.remove(selection);
								player.remove(overImage);
								player.add(selection);
								player.add(overImage);
							}
						}						
					}					
				}				
				else
				{
					player.remove(timecode);
				}
				
				refreshTimecodeAndText();
				
				player.repaint();
			}

		});

		JLabel posX = new JLabel(Shutter.language.getProperty("posX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setAlignmentX(SwingConstants.RIGHT);
		posX.setBounds(24,  caseShowTimecode.getY() + caseShowTimecode.getHeight() + 6, posX.getPreferredSize().width, 16);
		grpOverlay.add(posX);
		
		textTcPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().x * imageRatio) ) ) );
		textTcPosX.setName("textTcPosX");
		textTcPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textTcPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		grpOverlay.add(textTcPosX);
		
		textTcPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textTcPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), timecode.getLocation().y);
				
				tcLocX = timecode.getLocation().x;
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
				tcLocX = timecode.getLocation().x;
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
		
		JLabel px1 = new JLabel("px");
		px1.setEnabled(false);
		px1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textTcPosX.getLocation().x + textTcPosX.getWidth() + 2, posX.getY(), px1.getPreferredSize().width, 16);
		grpOverlay.add(px1);		
		
		JLabel posY = new JLabel(Shutter.language.getProperty("posY"));
		posY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setAlignmentX(SwingConstants.RIGHT);
		posY.setBounds(px1.getX() + px1.getWidth() + 30, posX.getLocation().y, posY.getPreferredSize().width, 16);
		grpOverlay.add(posY);

		textTcPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().y * imageRatio) ) ) );
		textTcPosY.setName("textTcPosY");
		textTcPosY.setBounds(posY.getLocation().x + posY.getWidth() + 2, posY.getLocation().y, 34, 16);
		textTcPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		grpOverlay.add(textTcPosY);
		
		textTcPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textTcPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
				
				tcLocY = timecode.getLocation().y;
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
				
				tcLocY = timecode.getLocation().y;
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
				
		JLabel px2 = new JLabel("px");
		px2.setEnabled(false);
		px2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textTcPosY.getLocation().x + textTcPosY.getWidth() + 2, posX.getY(), px1.getPreferredSize().width, 16);
		grpOverlay.add(px2);	
					
		JLabel lblSizeTC = new JLabel(Shutter.language.getProperty("lblSize"));
		lblSizeTC.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSizeTC.setAlignmentX(SwingConstants.RIGHT);
		lblSizeTC.setForeground(Utils.themeColor);
		lblSizeTC.setBounds(textTcPosX.getX() - lblSizeTC.getPreferredSize().width - 2, posX.getY() + posX.getHeight() + 6, lblSizeTC.getPreferredSize().width + 2, 16);				
		grpOverlay.add(lblSizeTC);
		
		textTcSize = new JTextField(String.valueOf(Math.round((float) 27 * imageRatio)));
		textTcSize.setName("textTcSize");
		textTcSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textTcSize.setBounds(textTcPosX.getLocation().x, lblSizeTC.getLocation().y, 34, 16);
		grpOverlay.add(textTcSize);
		
		textTcSize.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (textTcSize.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
					frame.requestFocus();
			}
			
		});
		
		textTcSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				
				textTcSize.requestFocus();
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textTcSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{			
					if (Integer.parseInt(textTcSize.getText()) < 5)
					{
						textTcSize.setText("5");
					}
					
					timecode.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	

				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textTcSize.getText().length() >= 3)
					textTcSize.setText("");						
			}			
			
		});
		
		textTcSize.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					
				if (textTcSize.getText().length() > 0)
				{	
					int value = MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX);
					
					if (value < 5)
					{
						textTcSize.setText("5");
					}
					else
						textTcSize.setText(String.valueOf(value));
								
					timecode.repaint();					
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});			
		
		JLabel percent1 = new JLabel("%");
		percent1.setEnabled(false);
		percent1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent1.setForeground(Utils.themeColor);
		percent1.setBounds(textTcSize.getLocation().x + textTcSize.getWidth() + 2, lblSizeTC.getY(), percent1.getPreferredSize().width + 4, 16);
		grpOverlay.add(percent1);
		
		JLabel lblOpacityTC = new JLabel(Shutter.language.getProperty("lblOpacity"));
		lblOpacityTC.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblOpacityTC.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityTC.setForeground(Utils.themeColor);
		lblOpacityTC.setBounds(textTcPosY.getX() - lblOpacityTC.getPreferredSize().width - 2, lblSizeTC.getLocation().y, lblOpacityTC.getPreferredSize().width, 16);	
		grpOverlay.add(lblOpacityTC);
		
		textTcOpacity = new JTextField("50");
		textTcOpacity.setName("textTcOpacity");
		textTcOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcOpacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textTcOpacity.setBounds(textTcPosY.getLocation().x, lblSizeTC.getLocation().y, textTcSize.getWidth(), 16);
		grpOverlay.add(textTcOpacity);
		
		textTcOpacity.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textTcOpacity.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textTcOpacity.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textTcOpacity.getText().length() > 0)
				{
					if (Integer.valueOf(textTcOpacity.getText()) > 100)
					{
						textTcOpacity.setText("100");
					}	
					else if (Integer.valueOf(textTcOpacity.getText()) < 1)
					{
						textTcOpacity.setText("1");
					}		
					
					timecode.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	

				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textTcOpacity.getText().length() >= 3)
					textTcOpacity.setText("");	
			}			
			
		});
		
		textTcOpacity.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (textTcOpacity.getText().length() > 0)
				{
					int value = MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX);
					
					if (value > 100)
					{
						textTcOpacity.setText("100");						
					}
					else if (value < 1)
					{
						textTcOpacity.setText("1");
					}	
					else
						textTcOpacity.setText(String.valueOf(value));
					
					timecode.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel percent2 = new JLabel("%");
		percent2.setEnabled(false);
		percent2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent2.setForeground(Utils.themeColor);
		percent2.setBounds(textTcOpacity.getLocation().x + textTcOpacity.getWidth() + 2, lblOpacityTC.getY(), percent1.getPreferredSize().width + 4, 16);
		grpOverlay.add(percent2);
		
		caseAddText.setName("caseAddText");
		caseAddText.setSelected(false);
		caseAddText.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddText.setSize(caseAddText.getPreferredSize().width + 4, 23);
		caseAddText.setLocation(caseAddTimecode.getX(), lblSizeTC.getY() + lblSizeTC.getHeight() + 8);
		grpOverlay.add(caseAddText);
		
		caseAddText.addActionListener(new ActionListener()	{

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAddText.isSelected())
				{
					caseShowFileName.setSelected(false);
					text.setEnabled(true);
					
					if (text.getText().length() > 0)
					{
						player.add(fileName);
						
						//Overimage need to be the last component added
						if (caseEnableCrop.isSelected())
						{
							player.remove(selection);
							player.remove(overImage);
							player.add(selection);
							player.add(overImage);
						}
					}	
				}
				else
				{
					text.setEnabled(false);
					player.remove(fileName);
				}
				
				refreshTimecodeAndText();
				
				player.repaint();	
		
			}
		});
		
		text.setName("text");
		text.setEnabled(false);
		text.setLocation(caseAddText.getLocation().x + caseAddText.getWidth() + 7, caseAddText.getLocation().y + 1);
		text.setSize(grpOverlay.getWidth() - text.getX() - 10, 21);
		text.setHorizontalAlignment(SwingConstants.LEFT);
		text.setFont(new Font("SansSerif", Font.PLAIN, 12));
		grpOverlay.add(text);

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
								
				if (e.getKeyChar() == ':' || e.getKeyChar() == '\"' || e.getKeyChar() == '\'' || e.getKeyChar() == ';' || e.getKeyChar() == ',')	
				{
					e.consume();
				}
				else
				{
					if (changeText == null || changeText.isAlive() == false)
					{
						changeText = new Thread(new Runnable() {
							
							@Override
							public void run() {
																
								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {}
								} while (System.currentTimeMillis() - textTime < 500);
													
								player.add(fileName);
								
								//Overimage need to be the last component added
								if (caseEnableCrop.isSelected())
								{
									player.remove(selection);
									player.remove(overImage);
									player.add(selection);
									player.add(overImage);
								}		
								
								fileName.repaint();
							}
						});
						changeText.start();
					}
				}
			}		
			
		});

		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setSelected(false);
		caseShowFileName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
		caseShowFileName.setLocation(caseAddText.getX(), caseAddText.getY() + caseAddText.getHeight());
		grpOverlay.add(caseShowFileName);
		
		caseShowFileName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseShowFileName.isSelected())
				{
					caseAddText.setSelected(false);
					text.setEnabled(false);
					player.add(fileName);
										 
					//Overimage need to be the last component added
					if (caseEnableCrop.isSelected())
					{
						player.remove(selection);
						player.remove(overImage);
						player.add(selection);
						player.add(overImage);
					}
				}
				else
				{
					player.remove(fileName);
				}
				
				refreshTimecodeAndText();

				player.repaint();
			}
			
		});
			
		fileName = new JPanel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {			
				if (caseShowFileName.isSelected() || caseAddText.isSelected() && text.getText().length() > 0)
				{
			        super.paintComponent(g);		
					
			        Graphics2D g2 = (Graphics2D) g;
	
			        //First initialisation
			        boolean resetLocation = false;
			        if (getWidth() == 1)
			        {
			        	resetLocation = true;
			        }
			        
			        //Saving height for spinnerSize
					int width = getWidth();
					int height = getHeight();				
			        
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			        
			        Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(textNameSize.getText()) / imageRatio));
			        font.deriveFont((float) Integer.parseInt(textNameSize.getText()) / imageRatio);
			        g2.setFont(font);
	
			        String file = videoPath;
					if (Shutter.scanIsRunning)
					{
			            File dir = new File(Shutter.liste.firstElement());
			            for (File f : dir.listFiles())
			            {
			            	if (f.isHidden() == false && f.isFile())
			            	{    	
			            		file = f.toString();
			            		break;
			            	}
			            }
					}
			        
					String ext = file.substring(file.lastIndexOf("."));
			        String str = new File(file).getName().replace(ext, "");
			        
					if (caseAddText.isSelected())
						str = text.getText();
					
			        Rectangle bounds = getStringBounds(g2, str, 0 ,0);
			        		        								
					if (lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
						g2.setColor(new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textNameOpacity.getText()) * 255) /  100)));
					else
						g2.setColor(new Color(0,0,0,0));
		
					GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			        AffineTransform transform = gfxConfig.getDefaultTransform();
					
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
				        g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					else
						g2.fillRect(0, 0, bounds.width, bounds.height);
					
					if (lblTcBackground.getText().equals(Shutter.language.getProperty("aucun")))
						g2.setColor(new Color(foregroundColor.getRed(),foregroundColor.getGreen(),foregroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textNameOpacity.getText()) * 255) /  100)));
					else				
						g2.setColor(foregroundColor);				
					
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
			        {
			        	Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2, bounds.height / 2 - offset / 2);	
			        }
					else
					{
						Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2, bounds.height - offset);	
					}	
					
					fileName.setSize(bounds.width, bounds.height);
					
					//Define center position after the size is correct
					if (resetLocation)
					{
						fileName.setLocation(player.getWidth() / 2 - fileName.getWidth() / 2, player.getHeight() -  fileName.getHeight() - fileName.getHeight() / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					}
					else if (textNameSize.hasFocus())
					{
						fileName.setLocation(fileName.getX() + (width - fileName.getWidth()) / 2, fileName.getY() + (height - fileName.getHeight()) / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					}
					
					refreshTimecodeAndText();
				}
		    }
		 
			private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
			{
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
				return gv.getPixelBounds(null, x, y);
			}
		};
			
		fileName.setSize(1,1);
		fileName.setLayout(null);
		fileName.setOpaque(false);
		fileName.setBackground(new Color(0,0,0,0));
		
		fileName.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2)
				{
					fileName.setLocation(player.getWidth() / 2 - fileName.getWidth() / 2, player.getHeight() -  fileName.getHeight() - fileName.getHeight() / 2);
					
					refreshTimecodeAndText();
					
					//Saving the location
					fileLocX = fileName.getLocation().x;
					fileLocY = fileName.getLocation().y;
				}
				
				filePosX = e.getLocationOnScreen().x;
				filePosY = e.getLocationOnScreen().y;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				fileLocX = fileName.getLocation().x;
				fileLocY = fileName.getLocation().y;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		
		JLabel posX2 = new JLabel(Shutter.language.getProperty("posX"));
		posX2.setHorizontalAlignment(SwingConstants.LEFT);
		posX2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posX2.setForeground(Utils.themeColor);
		posX2.setAlignmentX(SwingConstants.RIGHT);
		posX2.setBounds(posX.getX(), caseShowFileName.getY() + caseShowFileName.getHeight() + 6, posX.getWidth(), 16);
		grpOverlay.add(posX2);
		
		textNamePosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().x * imageRatio) ) ) );
		textNamePosX.setName("textNamePosX");
		textNamePosX.setBounds(posX2.getLocation().x + posX2.getWidth() + 2, posX2.getLocation().y, 34, 16);
		textNamePosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		grpOverlay.add(textNamePosX);
		
		textNamePosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNamePosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), fileName.getLocation().y);
				
				fileLocX = fileName.getLocation().x;
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

				fileLocX = fileName.getLocation().x;
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
		
		JLabel px3 = new JLabel("px");
		px3.setEnabled(false);
		px3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px3.setForeground(Utils.themeColor);
		px3.setBounds(textNamePosX.getLocation().x + textNamePosX.getWidth() + 2, posX2.getY(), px1.getPreferredSize().width, 16);
		grpOverlay.add(px3);				
		
		JLabel posY2 = new JLabel(Shutter.language.getProperty("posY"));
		posY2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posY2.setForeground(Utils.themeColor);
		posY2.setAlignmentX(SwingConstants.RIGHT);
		posY2.setBounds(px3.getX() + px3.getWidth() + 30, posX2.getLocation().y, posX.getWidth(), 16);
		grpOverlay.add(posY2);
		
		textNamePosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().y * imageRatio) ) ) );
		textNamePosY.setName("textNamePosY");
		textNamePosY.setBounds(posY2.getLocation().x + posY2.getWidth() + 2, posY2.getLocation().y, 34, 16);
		textNamePosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		grpOverlay.add(textNamePosY);
		
		textNamePosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNamePosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
				
				fileLocY = fileName.getLocation().y;
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

				fileLocY = fileName.getLocation().y;
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
	
		JLabel px4 = new JLabel("px");
		px4.setEnabled(false);
		px4.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px4.setForeground(Utils.themeColor);
		px4.setBounds(textNamePosY.getLocation().x + textNamePosY.getWidth() + 2, posY2.getY(), px1.getPreferredSize().width, 16);		
		grpOverlay.add(px4);
		
		JLabel lblSizeName = new JLabel(Shutter.language.getProperty("lblSize"));
		lblSizeName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSizeName.setAlignmentX(SwingConstants.RIGHT);
		lblSizeName.setForeground(Utils.themeColor);
		lblSizeName.setBounds(lblSizeTC.getX(), posX2.getY() + posX2.getHeight() + 6, lblSizeTC.getWidth(), 16);		
		grpOverlay.add(lblSizeName);
		
		textNameSize = new JTextField(String.valueOf(Math.round((float) 27 * imageRatio )));
		textNameSize.setName("textNameSize");
		textNameSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textNameSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textNameSize.setBounds(textNamePosX.getLocation().x, lblSizeName.getLocation().y, textNamePosX.getWidth(), 16);
		grpOverlay.add(textNameSize);
		
		textNameSize.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNameSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (textNameSize.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
					frame.requestFocus();
			}
			
		});
		
		textNameSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				
				textNameSize.requestFocus();	
			}

			@Override
			public void keyReleased(KeyEvent e) {
								
				if (textNameSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{			
					if (Integer.parseInt(textNameSize.getText()) < 5)
					{
						textNameSize.setText("5");
					}
					
					fileName.repaint();
				}
								
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textNameSize.getText().length() >= 3)
					textNameSize.setText("");	
			}			
			
		});
		
		textNameSize.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					
				if (textNameSize.getText().length() > 0)
				{	
					int value = MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX);
					
					if (value < 5)
					{
						textNameSize.setText("5");
					}
					else
						textNameSize.setText(String.valueOf(value));
								
					fileName.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});			
				
		JLabel percent3 = new JLabel("%");
		percent3.setEnabled(false);
		percent3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent3.setForeground(Utils.themeColor);
		percent3.setBounds(textNameSize.getLocation().x + textNameSize.getWidth() + 2, lblSizeName.getY(), percent1.getPreferredSize().width, 16);
		grpOverlay.add(percent3);
		
		JLabel lblOpacityName = new JLabel(Shutter.language.getProperty("lblOpacity"));
		lblOpacityName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblOpacityName.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityName.setForeground(Utils.themeColor);
		lblOpacityName.setBounds(lblOpacityTC.getX(), lblSizeName.getLocation().y, lblOpacityTC.getWidth(), 16);		
		grpOverlay.add(lblOpacityName);
		
		textNameOpacity = new JTextField("50");
		textNameOpacity.setName("spinnerNameOpacity");
		textNameOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textNameOpacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textNameOpacity.setBounds(textNamePosY.getX(), lblSizeName.getLocation().y, textNameSize.getWidth(), 16);
		grpOverlay.add(textNameOpacity);
		
		textNameOpacity.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNameOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textNameOpacity.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textNameOpacity.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textNameOpacity.getText().length() > 0)
				{
					if (Integer.valueOf(textNameOpacity.getText()) > 100)
					{
						textNameOpacity.setText("100");
					}	
					else if (Integer.valueOf(textNameOpacity.getText()) < 1)
					{
						textNameOpacity.setText("1");
					}		
					
					fileName.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textNameOpacity.getText().length() >= 3)
					textNameOpacity.setText("");	
			}			
			
		});
		
		textNameOpacity.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (textNameOpacity.getText().length() > 0)
				{
					int value = MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX);
					
					if (value > 100)
					{
						textNameOpacity.setText("100");						
					}
					else if (value < 1)
					{
						textNameOpacity.setText("1");
					}	
					else
						textNameOpacity.setText(String.valueOf(value));
					
					fileName.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});			
		
		JLabel percent4 = new JLabel("%");
		percent4.setEnabled(false);
		percent4.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent4.setForeground(Utils.themeColor);
		percent4.setBounds(textNameOpacity.getLocation().x + textNameOpacity.getWidth() + 2, lblOpacityName.getY(), percent1.getPreferredSize().width, 16);
		grpOverlay.add(percent4);
				
	}
	
	private static void refreshTimecodeAndText() {
				
		//Colors	
		if (foregroundColor != null)
		{
			 String c = Integer.toHexString(foregroundColor.getRGB()).substring(2);
			 foregroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}
		else
			foregroundColor = new Color(255,255,255);
					
		if (backgroundColor != null)
		{
			 String c = Integer.toHexString(backgroundColor.getRGB()).substring(2);
			 backgroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}	
		else
			backgroundColor = new Color(0,0,0);

		if (lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
		{	
			backgroundTcAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(textTcOpacity.getText()) * 255) / 100);
			foregroundTcAlpha = "ff";
			backgroundNameAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(textNameOpacity.getText()) * 255) / 100);
		 	foregroundNameAlpha = "ff";
		}
		else
		{
			backgroundTcAlpha = "0";
			foregroundTcAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(textTcOpacity.getText()) * 255) / 100);
		 	backgroundNameAlpha = "0";
			foregroundNameAlpha = Integer.toHexString((int) (float) ((int) Integer.parseInt(textNameOpacity.getText()) * 255) / 100);
		}	
		
		if (backgroundTcAlpha.length() < 2)
		{
			backgroundTcAlpha = "0" + backgroundTcAlpha;
		}
		if (foregroundTcAlpha.length() < 2)	
		{
			foregroundTcAlpha = "0" + foregroundTcAlpha;
		}
		if (backgroundNameAlpha.length() < 2)
		{
			backgroundNameAlpha = "0" + backgroundNameAlpha;
		}		
		if (foregroundNameAlpha.length() < 2)
		{
			foregroundNameAlpha = "0" + foregroundNameAlpha;	
		}

		if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
		{	
			textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * imageRatio)));
			textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * imageRatio)));  
		}
		else
		{
			textTcPosX.setText("0");
			textTcPosY.setText("0"); 
		}
		
		if (caseShowFileName.isSelected() || caseAddText.isSelected())
		{						
			textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * imageRatio)));
			textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * imageRatio)));  
		}
		else
		{	textNamePosX.setText("0");
			textNamePosY.setText("0"); 

		}			

	}
	
	private void grpSubtitles() {
		
		grpSubtitles = new JPanel();
		grpSubtitles.setLayout(null);
		grpSubtitles.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("caseSubtitles") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpSubtitles.setBackground(new Color(45, 45, 45));
		grpSubtitles.setSize(grpCrop.getWidth(), 17);
		grpSubtitles.setLocation(frame.getWidth() - grpSubtitles.getWidth() - 6, grpOverlay.getY() + grpOverlay.getHeight() + 6);
		frame.getContentPane().add(grpSubtitles);		
		
		grpSubtitles.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				final int sized = 131;
								
				if (grpSubtitles.getSize().height < sized)
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpSubtitles.setSize(grpSubtitles.getWidth(), i);	
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										
										if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
										{		
											grpOverlay.setSize(grpOverlay.getWidth(), grpOverlay.getHeight() - 1);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									if (grpOverlay.getHeight() > 17 && grpOverlay.getHeight() < 278)
									{
										i = grpOverlay.getHeight();									
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											grpOverlay.setSize(grpOverlay.getWidth(), i);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
											
											//Animate size
											Shutter.animateSections(startTime);	
											
										} while (i > 17);
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;									
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpSubtitles.setSize(grpSubtitles.getWidth(), i);
										grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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
			
		caseAddSubtitles.setName("caseAddSubtitles");
		caseAddSubtitles.setBounds(8, 16, caseAddSubtitles.getPreferredSize().width + 8, 23);	
		caseAddSubtitles.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddSubtitles.setSelected(false);
		grpSubtitles.add(caseAddSubtitles);
				
		caseAddSubtitles.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent arg0) {			
				
				if (caseAddSubtitles.isSelected())
				{	
					if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")))
					{
						Shutter.casePreserveSubs.setSelected(false);
					}					
					
					if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionSubtitles")))
					{
						if (System.getProperty("os.name").contains("Windows"))
							Shutter.subtitlesFile = new File(SubtitlesTimeline.srt.getName());
						else
							Shutter.subtitlesFile = new File(Shutter.dirTemp + SubtitlesTimeline.srt.getName());
									            
						Object[] options = {Shutter.language.getProperty("subtitlesBurn"), Shutter.language.getProperty("subtitlesEmbed")};
						
						int sub = JOptionPane.showOptionDialog(frame, Shutter.language.getProperty("chooseSubsIntegration"), Shutter.language.getProperty("caseSubtitles"),
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							    options,
							    options[0]);
			            
						if (sub == 0) //Burn
						{
							Shutter.comboFonctions.setModel(new DefaultComboBoxModel(Shutter.functionsList));
							Shutter.comboFonctions.setSelectedItem("H.264");
							lblVideo.setText("");//Needed to refresh sections
							setMedia();
							
							Shutter.caseInAndOut.doClick();
							caseAddSubtitles.setSelected(true);
							
							Shutter.subtitlesBurn = true;		
							subtitlesFile = SubtitlesTimeline.srt;
							writeSub(subtitlesFile.toString(), StandardCharsets.UTF_8);	
													
							subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
						    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
							
							subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);				
							player.add(subsCanvas);
							
							grpSubtitles.setSize(grpSubtitles.getWidth(), 131);	
							grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
							
							if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
							{		
								grpOverlay.setSize(grpOverlay.getWidth(), grpOverlay.getHeight() - 1);
								grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
							}
							
							for (Component c : grpSubtitles.getComponents())
							{
								c.setEnabled(true);
							}
						}
						else
						{
							Shutter.subtitlesBurn = false;
							subtitlesFile = new File(SubtitlesTimeline.srt.toString());
							Shutter.changeSections(false);
							Shutter.caseDisplay.setSelected(false);

							//On copy le .srt dans le fichier
							Thread copySRT = new Thread(new Runnable()
							{
								@SuppressWarnings("deprecation")
								@Override
								public void run() {
									
									try {						
										
										Shutter.disableAll();
										
										File fileIn = new File(videoPath);
										String extension = videoPath.toString().substring(fileIn.toString().lastIndexOf("."));
										File fileOut = new File(fileIn.toString().replace(extension, "_subs" + extension));
										
										//Envoi de la commande
										String cmd = " -c copy -c:s mov_text -map v:0? -map a? -map 1:s -y ";
										
										if (extension.equals(".mkv"))
											cmd = " -c copy -c:s srt -map v:0? -map a? -map 1:s -y ";							
										
										FFMPEG.run(" -i " + '"' + fileIn + '"' + " -i " + '"' + subtitlesFile + '"' + cmd + '"'  + fileOut + '"');	
										
										Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
										Shutter.lblCurrentEncoding.setText(fileIn.getName());
										
										//Attente de la fin de FFMPEG
										do {
											Thread.sleep(10);
										} while(FFMPEG.runProcess.isAlive());
										
										//Erreurs
										if (FFMPEG.error || fileOut.length() == 0)
										{
											FFMPEG.errorList.append(fileIn.getName());
										    FFMPEG.errorList.append(System.lineSeparator());
											fileOut.delete();
										}
										
										//Annulation
										if (Shutter.cancelled)
											fileOut.delete();
										
										//Fichiers terminés
										if (Shutter.cancelled == false && FFMPEG.error == false)
											Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(1));
										
										//Ouverture du dossier
										if (Shutter.caseOpenFolderAtEnd1.isSelected() && Shutter.cancelled == false && FFMPEG.error == false)
										{
											if (System.getProperty("os.name").contains("Mac")) 
											{
												try {
													Runtime.getRuntime().exec(new String[]{"/usr/bin/open", "-R", fileOut.toString()});
												} catch (Exception e2){}
											}
											else if (System.getProperty("os.name").contains("Linux"))
											{
												try {
													Desktop.getDesktop().open(fileOut);
												} catch (Exception e2){}
											}
											else //Windows
											{
												try {
													Runtime.getRuntime().exec("explorer.exe /select," + fileOut.toString());
												} catch (IOException e1) {}
											}
										}
										
									} catch (Exception e) {										
									}	
									finally
									{
										Shutter.enfOfFunction();
									}
									
								}						
							});
							copySRT.start();	
						}
					}
					else
					{
					
						File video = new File(Shutter.liste.elementAt(0).toString());
						String ext = video.toString().substring(video.toString().lastIndexOf("."));
						
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseSubtitles"),	FileDialog.LOAD);
						
						char slash = '/';
						if (System.getProperty("os.name").contains("Windows"))
							slash = '\\';
						
						if (new File (video.toString().replace(ext, ".srt")).exists())
						{
							dialog.setDirectory(video.getParent() + slash);
							dialog.setFile(video.getName().replace(ext, ".srt"));
						}
						else if (new File (video.toString().replace(ext, ".vtt")).exists())
						{
							dialog.setDirectory(video.getParent() + slash);
							dialog.setFile(video.getName().replace(ext, ".vtt"));
						}
						else if (new File (video.toString().replace(ext, ".ass")).exists())
						{
							dialog.setDirectory(video.getParent() + slash);
							dialog.setFile(video.getName().replace(ext, ".ass"));
						}
						else if (new File (video.toString().replace(ext, ".ssa")).exists())
						{
							dialog.setDirectory(video.getParent() + slash);
							dialog.setFile(video.getName().replace(ext, ".ssa"));
						}
						else if (new File (video.toString().replace(ext, ".scc")).exists())
						{
							dialog.setDirectory(video.getParent() + slash);
							dialog.setFile(video.getName().replace(ext, ".scc"));
						}
						else
						{
							dialog.setDirectory(new File(videoPath).getParent());
							dialog.setFile("*.srt;*.vtt;*.ass;*.ssa;*.scc");
							dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
							dialog.setAlwaysOnTop(true);
							dialog.setMultipleMode(false);
							dialog.setVisible(true);
						}						
	
						if (dialog.getFile() != null) 
						{
							String input = dialog.getFile().substring(dialog.getFile().lastIndexOf("."));
							if (input.equals(".srt") || input.equals(".vtt") || input.equals(".ssa") || input.equals(".ass") || input.equals(".scc"))
							{
								if (System.getProperty("os.name").contains("Windows"))
									Shutter.subtitlesFile = new File(dialog.getFile());
								else
									Shutter.subtitlesFile = new File(Shutter.dirTemp + dialog.getFile());
								
								if (input.equals(".srt") || input.equals(".vtt"))
								{
									Object[] options = {Shutter.language.getProperty("subtitlesBurn"), Shutter.language.getProperty("subtitlesEmbed")};
									
									int sub = 0;
									if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")))
										sub = 1;
									
									if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
									&& Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC") == false
									&& Shutter.caseCreateOPATOM.isSelected() == false
									&& Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")) == false)
									{
										sub = JOptionPane.showOptionDialog(frame, Shutter.language.getProperty("chooseSubsIntegration"), Shutter.language.getProperty("caseAddSubtitles"),
												JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											    options,
											    options[0]);
									}
										
									if (sub == 0) //Burn
									{
										Shutter.subtitlesBurn = true;							
																
										//Conversion du .vtt en .srt
										if (input.equals(".vtt"))
										{	
											Shutter.subtitlesFile = new File(subtitlesFile.toString().replace(".vtt", ".srt"));
											
											FFMPEG.run(" -i " + '"' + dialog.getDirectory() + dialog.getFile().toString() + '"' + " -y " + '"' + subtitlesFile.toString().replace(".srt", "_vtt.srt") + '"');
											
											//Attente de la fin de FFMPEG
											do
											{
												try {
													Thread.sleep(10);
												} catch (InterruptedException e) {}
											}
											while(FFMPEG.runProcess.isAlive());
											
											Shutter.enableAll();
											
											subtitlesFile = new File(subtitlesFile.toString().replace(".srt", "_vtt.srt"));
										}
										else											
											subtitlesFile = new File(dialog.getDirectory() + dialog.getFile().toString());			
										
										writeSub(subtitlesFile.toString(), StandardCharsets.UTF_8);		
										
										subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
									    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
										
										subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);				
										player.add(subsCanvas);
										
										for (Component c : grpSubtitles.getComponents())
										{
											c.setEnabled(true);
										}
									}
									else
									{											
										SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
	
										if (SubtitlesEmbed.frame == null)
											new SubtitlesEmbed();
										else
											Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);
										
										Shutter.subtitlesBurn = false;
										Shutter.changeSections(false);
										Shutter.caseDisplay.setSelected(false);
										
										if (caseAddSubtitles.isSelected())
										{
											for (Component c : grpSubtitles.getComponents())
											{
												if (c instanceof JCheckBox == false)
												{
													c.setEnabled(false);
												}
											}
										}
										
										JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("previewNotAvailable"), Shutter.language.getProperty("caseSubtitles"), JOptionPane.INFORMATION_MESSAGE);
									}	
									
									//Important
									sliderSpeed.setEnabled(false);
									sliderSpeed.setValue(2);
									lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
									lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
								}
								else //SSA or ASS or SCC
								{									
									Object[] options = {Shutter.language.getProperty("subtitlesBurn"), Shutter.language.getProperty("subtitlesEmbed")};
									
									int sub = 0;
									
									if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
									&& Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC") == false
									&& Shutter.caseCreateOPATOM.isSelected() == false)
									{
										sub = JOptionPane.showOptionDialog(frame, Shutter.language.getProperty("chooseSubsIntegration"), Shutter.language.getProperty("caseAddSubtitles"),
												JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											    options,
											    options[0]);
									}
										
									if (sub == 0) //Burn
									{
										Shutter.subtitlesBurn = true;
										
										try {
											FileUtils.copyFile(new File(dialog.getDirectory() + dialog.getFile().toString()), Shutter.subtitlesFile);
										} catch (IOException e) {}
									}
									else
									{
										SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
										
										if (SubtitlesEmbed.frame == null)
											new SubtitlesEmbed();
										else
											Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);															
										
										Shutter.subtitlesBurn = false;
										Shutter.changeSections(false);
										Shutter.caseDisplay.setSelected(false);
									}
									
									for (Component c : grpSubtitles.getComponents())
									{
										if (c instanceof JCheckBox == false)
										{
											c.setEnabled(false);
										}
									}
									
									JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("previewNotAvailable"), Shutter.language.getProperty("caseSubtitles"), JOptionPane.INFORMATION_MESSAGE);
								}	
								
								//Important
								sliderSpeed.setEnabled(false);
								sliderSpeed.setValue(2);
								lblSpeed.setText(Shutter.language.getProperty("conformBySpeed") + " x1");
								lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
							}
							else 
							{
								JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("invalidSubtitles"), Shutter.language.getProperty("subtitlesFileError"), JOptionPane.PLAIN_MESSAGE);
								caseAddSubtitles.setSelected(false);
							}
						}					
						else
							caseAddSubtitles.setSelected(false);							
					}
				} 
				else 
				{
					player.remove(subsCanvas);
					caseAddSubtitles.setSelected(false);					
					loadImage(true);					

					for (Component c : grpSubtitles.getComponents())
					{
						if (c instanceof JCheckBox == false)
						{
							c.setEnabled(false);
						}
					}
					
					sliderSpeed.setEnabled(true);
				}
			}
			
			private void writeSub(String srt, Charset encoding) 
			{
				
				try {

					writeCurrentSubs(playerCurrentFrame);
					loadImage(true);

				} catch (Exception e) {
					
					if (encoding == StandardCharsets.UTF_8)
					{						
						writeSub(srt, StandardCharsets.ISO_8859_1);
					}
					else					
					{		
						caseAddSubtitles.setSelected(false);
						player.remove(subsCanvas);
					}
				}
			}

		});	
		
		JLabel lblFont = new JLabel(Shutter.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setForeground(Utils.themeColor);
		lblFont.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblFont.setBounds(12, caseAddSubtitles.getY() + caseAddSubtitles.getHeight() + 7, lblFont.getPreferredSize().width, 16);
		grpSubtitles.add(lblFont);
		
		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		comboSubsFont = new JComboBox<String>(Fonts);
		comboSubsFont.setName("comboSubsFont");
		comboSubsFont.setSelectedItem("Arial");
		comboSubsFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboSubsFont.setRenderer(new ComboRenderer(comboSubsFont));
		comboSubsFont.setEditable(true);
		comboSubsFont.setBounds(lblFont.getX() + lblFont.getWidth() + 7, lblFont.getY() - 4, 110, 22);
		grpSubtitles.add(comboSubsFont);
		
		comboSubsFont.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
	    {
			String text = "";
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void keyReleased(KeyEvent e) {
				
					if (comboSubsFont.getEditor().toString().length() <= 1)
						text = String.valueOf(e.getKeyChar());

					if (Character.isLetterOrDigit(e.getKeyChar())) {
						comboSubsFont.setModel(new DefaultComboBoxModel(Fonts));
						text += e.getKeyChar();

						ArrayList<String> newList = new ArrayList<String>();
						for (int i = 0; i < comboSubsFont.getItemCount(); i++) {
							if (Fonts[i].toString().length() >= text.length()) {
								if (Fonts[i].toString().toLowerCase().substring(0, text.length()).contains(text)
										&& Fonts[i].toString().contains(":") == false) {
									newList.add(Fonts[i].toString());
								}
							}
						}

						// Pour éviter d'afficher le premier item
						comboSubsFont.getEditor().setItem(text);

						if (newList.isEmpty() == false) {
							comboSubsFont.setModel(new DefaultComboBoxModel(newList.toArray()));
							comboSubsFont.showPopup();
						}

					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboSubsFont.setModel(new DefaultComboBoxModel(Fonts));
						comboSubsFont.getEditor().setItem("");
						comboSubsFont.hidePopup();
						text = "";
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						e.consume();// Contournement pour éviter le listeDrop
					else
						comboSubsFont.hidePopup();
			}
					
	    });	
		
		comboSubsFont.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				comboSubsFont.setFont(new Font(comboSubsFont.getSelectedItem().toString(), Font.PLAIN, 11));				
				loadImage(false);				
			}
			
		});
			
		btnG = new JButton(Shutter.language.getProperty("btnG"));
    	btnG.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
    	btnG.setForeground(Color.BLACK);
    	btnG.setName("btnG");
    	btnG.setBounds(comboSubsFont.getLocation().x + comboSubsFont.getWidth() + 4, comboSubsFont.getY(), 22, 22);    	
    	grpSubtitles.add(btnG);
    	
    	btnG.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (btnG.getForeground() == Color.BLACK)
					btnG.setForeground(Utils.themeColor);
				else					
					btnG.setForeground(Color.BLACK);
				
				loadImage(true);
			}
    		
    	});

		btnI = new JButton("I");
    	btnI.setFont(new Font("Courier New", Font.ITALIC, 13));
    	btnI.setForeground(Color.BLACK);
    	btnI.setName("btnI");
    	btnI.setBounds(btnG.getLocation().x + btnG.getWidth() + 2, comboSubsFont.getY(), 22, 22);    	
    	grpSubtitles.add(btnI);
    	
    	btnI.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (btnI.getForeground() == Color.BLACK)
					btnI.setForeground(Utils.themeColor);
				else
					btnI.setForeground(Color.BLACK);
				
				loadImage(true);
			}
    		
    	});
		
		JLabel lblSubtitlesPosition = new JLabel(Shutter.language.getProperty("lblSubtitlesPosition"));
		lblSubtitlesPosition.setAlignmentX(SwingConstants.RIGHT);
		lblSubtitlesPosition.setForeground(Utils.themeColor);
		lblSubtitlesPosition.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSubtitlesPosition.setBounds(btnI.getLocation().x + btnI.getWidth() + 6, comboSubsFont.getY() + 3, lblSubtitlesPosition.getPreferredSize().width, 16);
		grpSubtitles.add(lblSubtitlesPosition);
		
		textSubtitlesPosition = new JTextField("0");
		textSubtitlesPosition.setName("textSubtitlesPosition");
		textSubtitlesPosition.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textSubtitlesPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubtitlesPosition.setBounds(lblSubtitlesPosition.getLocation().x + lblSubtitlesPosition.getWidth() + 2, lblSubtitlesPosition.getLocation().y, 34, 16);
		grpSubtitles.add(textSubtitlesPosition);
		
		textSubtitlesPosition.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				MouseSubsPosition.mouseY = e.getY();
				MouseSubsPosition.offsetY = Integer.parseInt(textSubtitlesPosition.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textSubtitlesPosition.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				refreshSubtitles();
			}

			@Override
			public void keyTyped(KeyEvent e) {					
			}			
			
		});
		
		textSubtitlesPosition.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubtitlesPosition.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textSubtitlesPosition.getText().length() > 0)
				{
					textSubtitlesPosition.setText(String.valueOf(MouseSubsPosition.offsetY + (e.getY() - MouseSubsPosition.mouseY)));
					refreshSubtitles();
				}				
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
			
		});			
			
		JLabel px1 = new JLabel("px");
		px1.setEnabled(false);
		px1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textSubtitlesPosition.getLocation().x + textSubtitlesPosition.getWidth() + 2, lblSubtitlesPosition.getY(), px1.getPreferredSize().width, 16);
		grpSubtitles.add(px1);	
		
		JLabel lblWidth = new JLabel(Shutter.language.getProperty("lblWidth"));
		lblWidth.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblWidth.setAlignmentX(SwingConstants.RIGHT);
		lblWidth.setForeground(Utils.themeColor);
		lblWidth.setBounds(lblFont.getX(), lblFont.getY() + lblFont.getHeight() + 11, lblWidth.getPreferredSize().width, 16);
		grpSubtitles.add(lblWidth);

		textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
		textSubsWidth.setName("textSubsWidth");
		textSubsWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsWidth.setBounds(lblWidth.getX() + lblWidth.getWidth() + 5, lblWidth.getLocation().y, 34, 16);
		textSubsWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsWidth.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		grpSubtitles.add(textSubsWidth);
		
		textSubsWidth.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textSubsWidth.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
					{
						textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
						subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
					}
					else
					{
						subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
					    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
						
						subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);
					}	
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textSubsWidth.getText().length() >= 4)
					textSubsWidth.setText("");				
			}			
			
		});
		
		textSubsWidth.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseSubSize.mouseX = e.getX();
				MouseSubSize.offsetX = Integer.parseInt(textSubsWidth.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				loadImage(true);
			}
			
		});
		
		textSubsWidth.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (textSubsWidth.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					textSubsWidth.setText(String.valueOf(MouseSubSize.offsetX + (e.getX() - MouseSubSize.mouseX)));
				
				if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
				{
					textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
					subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
				}
				else
				{
					subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
				    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
					
					subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);
				}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel widthPx = new JLabel("px");
		widthPx.setEnabled(false);
		widthPx.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		widthPx.setForeground(Utils.themeColor);
		widthPx.setBounds(textSubsWidth.getLocation().x + textSubsWidth.getWidth() + 2, lblWidth.getY(), px1.getPreferredSize().width, 16);
		grpSubtitles.add(widthPx);				
		
		JLabel lblColor = new JLabel(Shutter.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setForeground(Utils.themeColor);
		lblColor.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblColor.setBounds(widthPx.getLocation().x + widthPx.getWidth() + 7, lblWidth.getY(), lblColor.getPreferredSize().width + 4, 16);
		grpSubtitles.add(lblColor);
		
		panelSubsColor = new JPanel();
		panelSubsColor.setName("panelSubsColor");
		panelSubsColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelSubsColor.setBackground(fontSubsColor);
		panelSubsColor.setBounds(lblColor.getLocation().x + lblColor.getWidth(), lblColor.getY() - 4, 41, 22);
		grpSubtitles.add(panelSubsColor);
		
		panelSubsColor.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
				fontSubsColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				
				if (fontSubsColor != null)
				{
					panelSubsColor.setBackground(fontSubsColor);	
					loadImage(true);
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
		lblSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSize.setAlignmentX(SwingConstants.RIGHT);
		lblSize.setForeground(Utils.themeColor);
		lblSize.setBounds(panelSubsColor.getLocation().x + panelSubsColor.getWidth() + 7, lblColor.getLocation().y, lblSize.getPreferredSize().width, 16);		
		grpSubtitles.add(lblSize);
		
		textSubsSize = new JTextField("18");
		textSubsSize.setName("textSubsSize");
		textSubsSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textSubsSize.setBounds(lblSize.getLocation().x + lblSize.getWidth() + 5, lblColor.getLocation().y, 34, 16);
		grpSubtitles.add(textSubsSize);
		
		textSubsSize.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				MouseSubsPosition.mouseX = e.getX();
				MouseSubsPosition.offsetX = Integer.parseInt(textSubsSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textSubsSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				loadImage(false);
			}

			@Override
			public void keyTyped(KeyEvent e) {	

				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textSubsSize.getText().length() >= 3)
					textSubsSize.setText("");						
			}			
			
		});
		
		textSubsSize.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubsSize.getText().length() > 0 && textSubsSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					
					int value = MouseSubsPosition.offsetX + (e.getX() - MouseSubsPosition.mouseX);
					
					if (value < 1)
					{
						textSubsSize.setText("1");
					}
					else
						textSubsSize.setText(String.valueOf(value));

					loadImage(false);	
				}				
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
			
		});	
		
		JLabel percent1 = new JLabel("%");
		percent1.setEnabled(false);
		percent1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent1.setForeground(Utils.themeColor);
		percent1.setBounds(textSubsSize.getLocation().x + textSubsSize.getWidth() + 2, lblSize.getY(), px1.getPreferredSize().width, 16);		
		if (Shutter.getLanguage.equals(Locale.of("pl").getDisplayLanguage()) == false)
			grpSubtitles.add(percent1);	
		
		lblSubsBackground = new JLabel(Shutter.language.getProperty("lblBackgroundOff"));
		lblSubsBackground.setName("lblSubsBackground");
		lblSubsBackground.setBackground(new Color(60, 60, 60));
		lblSubsBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubsBackground.setOpaque(true);
		lblSubsBackground.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 11));
		lblSubsBackground.setBounds(lblWidth.getX(), lblColor.getY() + lblColor.getHeight() + 13, 80, 16);
		grpSubtitles.add(lblSubsBackground);
		
		lblSubsBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOff")))
				{
					
					lblSubsBackground.setText(Shutter.language.getProperty("lblBackgroundOn"));
					
					lblSubsOutline.setText(Shutter.language.getProperty("lblOpacity"));
				}
				else
				{
					lblSubsBackground.setText(Shutter.language.getProperty("lblBackgroundOff"));
					
					lblSubsOutline.setText(Shutter.language.getProperty("lblSize"));
				}
				
				loadImage(true);					
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
		lblColor2.setForeground(Utils.themeColor);
		lblColor2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblColor2.setBounds(lblSubsBackground.getLocation().x + lblSubsBackground.getWidth() + 7, lblSubsBackground.getY(), lblColor2.getPreferredSize().width + 4, 16);
		grpSubtitles.add(lblColor2);
		
		panelSubsColor2 = new JPanel();
		panelSubsColor2.setName("panelSubsColor2");
		panelSubsColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelSubsColor2.setBackground(backgroundSubsColor);
		panelSubsColor2.setBounds(lblColor2.getLocation().x + lblColor2.getWidth(), lblColor2.getY() - 4, 41, 22);
		grpSubtitles.add(panelSubsColor2);
		
		panelSubsColor2.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
				backgroundSubsColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				
				if (backgroundSubsColor != null)
				{
					panelSubsColor2.setBackground(backgroundSubsColor);	
					loadImage(true);
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
			
		if (Shutter.getLanguage.equals(Locale.of("pl").getDisplayLanguage()))
		{
			lblSubsOutline = new JLabel(Shutter.language.getProperty("lblSize"));
		}
		else
			lblSubsOutline = new JLabel(Shutter.language.getProperty("lblOpacity"));
		
		lblSubsOutline.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSubsOutline.setName("lblSubsOutline");
		lblSubsOutline.setForeground(Utils.themeColor);
		lblSubsOutline.setAlignmentX(SwingConstants.RIGHT);
		lblSubsOutline.setBounds(panelSubsColor2.getLocation().x + panelSubsColor2.getWidth() + 7, lblColor2.getLocation().y, lblSubsOutline.getPreferredSize().width + 2, 16);		
		lblSubsOutline.setText(Shutter.language.getProperty("lblSize"));
		grpSubtitles.add(lblSubsOutline);
				
		textSubsOutline = new JTextField("50");
		textSubsOutline.setName("textSubsOutline");
		textSubsOutline.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsOutline.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		textSubsOutline.setBounds(lblSubsOutline.getLocation().x + lblSubsOutline.getWidth() + 5, lblColor2.getLocation().y, 34, 16);
		grpSubtitles.add(textSubsOutline);
		
		textSubsOutline.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				MouseSubsPosition.mouseX = e.getX();
				MouseSubsPosition.offsetX = Integer.parseInt(textSubsOutline.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textSubsOutline.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				loadImage(false);
			}

			@Override
			public void keyTyped(KeyEvent e) {	

				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textSubsOutline.getText().length() >= 3)
					textSubsOutline.setText("");						
			}			
			
		});
		
		textSubsOutline.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubsOutline.getText().length() >  0 && textSubsOutline.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					int value = MouseSubsPosition.offsetX + (e.getX() - MouseSubsPosition.mouseX);
					
					if (value < 1)
					{
						textSubsOutline.setText("1");
					}
					else if (value > 100)
					{
						textSubsOutline.setText("100");
					}
					else
						textSubsOutline.setText(String.valueOf(value));

					loadImage(false);	
				}				
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
			
		});

		JLabel percent2 = new JLabel("%");
		percent2.setEnabled(false);
		percent2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent2.setForeground(Utils.themeColor);
		percent2.setBounds(textSubsOutline.getLocation().x + textSubsOutline.getWidth() + 2, lblSubsOutline.getY(), px1.getPreferredSize().width, 16);
		grpSubtitles.add(percent2);	
		
		//Border
		subsCanvas = new JPanel();	        	
		subsCanvas.setBorder(BorderFactory.createDashedBorder(Color.WHITE, 4, 4));	
		subsCanvas.setOpaque(false);
		subsCanvas.setBounds(0, 0, player.getWidth(), player.getHeight());	
		subsCanvas.setBackground(new Color(0,0,0,0));			
	
		for (Component c : grpSubtitles.getComponents())
		{
			if (c instanceof JCheckBox == false)
			{
				c.setEnabled(false);
			}
		}		
	}

	private static void refreshSubtitles() {
		
		//Initialisation
		if (alphaHeight == 0)
		{
			alphaHeight = FFPROBE.imageHeight;
		}
		
		int v = Integer.parseInt(textSubtitlesPosition.getText());
		int sz = Integer.parseInt(textSubsSize.getText());
		int newValue = Math.round((float)sz*((float)alphaHeight/(FFPROBE.imageHeight+v)));
		
		if (newValue > 0)
			textSubsSize.setText(String.valueOf(newValue));
		
		alphaHeight = (int) (FFPROBE.imageHeight + v);
		
		if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
		{
			subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
		}
		else
		{
			subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
		    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
			
			subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);
		}	
		
		loadImage(false);
	}
	
	public static void writeCurrentSubs(float inputTime) {	
				
		if (caseAddSubtitles.isSelected() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{
			try {
	
				BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(subtitlesFile.toString()),  StandardCharsets.UTF_8);
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
						
						String iH = formatter.format(Math.floor(inOffset / FFPROBE.currentFPS / 3600));
						String iM = formatter.format(Math.floor(inOffset / FFPROBE.currentFPS / 60) % 60);
						String iS = formatter.format(Math.floor(inOffset / FFPROBE.currentFPS) % 60);    		
						String iF = formatterToMs.format(Math.floor(inOffset % FFPROBE.currentFPS * inputFramerateMS));
						
						String oH = formatter.format(Math.floor(outOffset / FFPROBE.currentFPS / 3600));
						String oM = formatter.format(Math.floor(outOffset / FFPROBE.currentFPS / 60) % 60);
						String oS = formatter.format(Math.floor(outOffset / FFPROBE.currentFPS) % 60);    		
						String oF = formatterToMs.format(Math.floor(outOffset % FFPROBE.currentFPS * inputFramerateMS));
						
						bufferedWriter.write(iH + ":" + iM + ":" + iS + "," + iF + " --> " + oH + ":" + oM + ":" + oS + "," + oF);
	            		bufferedWriter.newLine();
	        		}
	        		else if (line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
	        		{           			
	        			if (lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
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
	
	@SuppressWarnings("serial")
	private void grpWatermark() {
				
		grpWatermark = new JPanel();
		grpWatermark.setLayout(null);
		grpWatermark.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true), Shutter.language.getProperty("caseLogo") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpWatermark.setBackground(new Color(45, 45, 45));
		grpWatermark.setSize(grpCrop.getWidth(), 17);
		grpWatermark.setLocation(frame.getWidth() - grpWatermark.getWidth() - 6, grpSubtitles.getY() + grpSubtitles.getHeight() + 6);
		frame.getContentPane().add(grpWatermark);
		
		grpWatermark.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				final int sized = 90;
								
				if (grpWatermark.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = sized;
										else
											i ++;
										
										grpWatermark.setSize(grpWatermark.getWidth(), i);									
										
										if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
										{		
											grpOverlay.setSize(grpOverlay.getWidth(), grpOverlay.getHeight() - 1);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
										}
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i < sized);
									
									if (grpOverlay.getHeight() > 17 && grpOverlay.getHeight() < 278)
									{
										i = grpOverlay.getHeight();									
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											grpOverlay.setSize(grpOverlay.getWidth(), i);
											grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
											
											//Animate size
											Shutter.animateSections(startTime);	
											
										} while (i > 17);
									}
									
							} catch (Exception e1) {
							}
						}
					});
					changeSize.start();
				}
				else
				{
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
								try {
									int i = sized;									
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpWatermark.setSize(grpWatermark.getWidth(), i);
	
										
										//Animate size
										Shutter.animateSections(startTime);	
										
									} while (i > 17);
								} catch (Exception e1) {
								}
						}
					});
					changeSize.start();
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
	
	  	logo = new JPanel(){
	  		
	  		protected void paintComponent(Graphics g) {
	  			
	  			Graphics2D g2d = (Graphics2D) g;
	  			
	  			if (textWatermarkOpacity.getText().length() > 0 && Integer.parseInt(textWatermarkOpacity.getText()) <= 100)
	  			{
	  				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Integer.valueOf(textWatermarkOpacity.getText()) / 100));
	  			}
	  			
	  			if (logoPNG != null)
	  				g2d.drawImage(logoPNG, 0, 0, null);
	  		}
	  	};
		logo.setLayout(null);        
		logo.setOpaque(false); 
		logo.setBackground(new Color(0,0,0,50));
		logo.setSize(player.getWidth(), player.getHeight());	
		
		logo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) Math.floor(player.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(player.getHeight() / 2 - logo.getHeight() / 2));	
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
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
				textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
				textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
			
		caseAddWatermark.setName("caseAddWatermark");
		caseAddWatermark.setBounds(8, 16, caseAddWatermark.getPreferredSize().width + 8, 23);	
		caseAddWatermark.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddWatermark.setSelected(false);
		grpWatermark.add(caseAddWatermark);
				
		caseAddWatermark.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAddWatermark.isSelected())
				{
					boolean addDevice = false;
					if (RecordInputDevice.comboInputVideo != null && RecordInputDevice.comboInputVideo.getSelectedIndex() > 0)
						addDevice = true;
					
					if (Shutter.inputDeviceIsRunning && Shutter.liste.getElementAt(0).equals("Capture.current.screen") && System.getProperty("os.name").contains("Windows") && addDevice == false)
					{
						int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("addInputDevice"),
								Shutter.language.getProperty("menuItemInputDevice"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						
						if (reply == JOptionPane.YES_OPTION)
						{
							addDevice = true;
							caseAddWatermark.doClick();
							Shutter.caseInAndOut.doClick();
							Shutter.inputDevice.doClick();		
							Shutter.inputDeviceIsRunning = false;
						}
						else
						{
							Shutter.overlayDeviceIsRunning = false;
						}
					}
					
					if (Shutter.liste.getSize() == 0)
					{
						JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("addFileToList"),Shutter.language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);						
						caseAddWatermark.setSelected(false);
					}
					else if (addDevice)
					{
						for (Component c : grpWatermark.getComponents())
						{
							c.setEnabled(true);							
						}
						
						loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
			    		player.add(logo);
			    		
			    		textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
						textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
			    		
			    		//Overimage need to be the last component added
						if (caseEnableCrop.isSelected())
						{
							player.remove(selection);
							player.remove(overImage);
							player.add(selection);
							player.add(overImage);
						}
						
						resizeAll();
					}
					else
					{
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseLogo"), FileDialog.LOAD);
						dialog.setDirectory(new File(videoPath).getParent());
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						dialog.setMultipleMode(false);
						dialog.setVisible(true);
	
						if (dialog.getFile() != null)
						{
							logoFile = dialog.getDirectory() + dialog.getFile().toString();
							
							for (Component c : grpWatermark.getComponents())
							{
								c.setEnabled(true);							
							}
							
							loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
				    		player.add(logo);
				    		
				    		textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
							textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
				    		
				    		//Overimage need to be the last component added
							if (caseEnableCrop.isSelected())
							{
								player.remove(selection);
								player.remove(overImage);
								player.add(selection);
								player.add(overImage);
							}
						}
						else
						{
							if (Shutter.overlayDeviceIsRunning)
								Shutter.overlayDeviceIsRunning = false;
							
							caseAddWatermark.setSelected(false);
						}
					}
					
				}
				else
				{
					for (Component c : grpWatermark.getComponents())
					{
						if (c instanceof JCheckBox == false)
						{
							c.setEnabled(false);
						}
					}
					
					player.remove(logo);
					logoPNG = null;
				}	
				
				if (frameVideo != null)
				{
					player.repaint();
				}
			}

		});
		
       	caseSafeArea = new JCheckBox(Shutter.language.getProperty("caseSafeArea"));
       	caseSafeArea.setName("caseSafeArea");
		caseSafeArea.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseSafeArea.setSize(caseSafeArea.getPreferredSize().width, 23);
		caseSafeArea.setLocation(caseAddWatermark.getX() + caseAddWatermark.getWidth() + 4, caseAddWatermark.getY());
		grpWatermark.add(caseSafeArea);
	
		JPanel safeArea = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				
				super.paintComponent(g);
				g.setColor(new Color(200,200,200));					
				
				//Action safe
				g.drawRect(player.getX() + (int) ((float) ((float) player.getWidth() * 0.07) / 2), player.getY() +  (int) ((float) ((float) player.getHeight() * 0.07) / 2), (int) ((float) player.getWidth() * 0.93), (int) ((float) player.getHeight() * 0.93));
				
				//Title safe
				g.drawRect(player.getX() + (int) ((float) ((float) player.getWidth() * 0.1) / 2), player.getY() +  (int) ((float) ((float) player.getHeight() * 0.1) / 2), (int) ((float) player.getWidth() * 0.9), (int) ((float) player.getHeight() * 0.9));
			}
		};
		
		safeArea.setOpaque(false);
		safeArea.setBackground(new Color(0,0,0,0));
		safeArea.setSize(player.getWidth(), player.getHeight());
		frame.getGlassPane().setVisible(false);

		caseSafeArea.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSafeArea.isSelected())
				{
					frame.setGlassPane(safeArea); 
			        frame.getGlassPane().setVisible(true);
				}
				else
				{
					frame.getGlassPane().setVisible(false);
				}
			}
			
		});
			
		JLabel watermarkPosX = new JLabel(Shutter.language.getProperty("posX"));
		watermarkPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkPosX.setEnabled(false);
		watermarkPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkPosX.setForeground(Utils.themeColor);
		watermarkPosX.setBounds(24, caseAddWatermark.getY() + caseAddWatermark.getHeight() + 4, watermarkPosX.getPreferredSize().width, 16);

		textWatermarkPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
		textWatermarkPosX.setName("textWatermarkPosX");
		textWatermarkPosX.setEnabled(false);
		textWatermarkPosX.setBounds(watermarkPosX.getLocation().x + watermarkPosX.getWidth() + 2, watermarkPosX.getLocation().y, 34, 16);
		textWatermarkPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		JLabel px1 = new JLabel("px");
		px1.setEnabled(false);
		px1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textWatermarkPosX.getLocation().x + textWatermarkPosX.getWidth() + 2, watermarkPosX.getY(), px1.getPreferredSize().width, 16);
		
		textWatermarkPosX.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textWatermarkPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / imageRatio), logo.getLocation().y);
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textWatermarkPosX.getText().length() >= 4)
					textWatermarkPosX.setText("");				
			}			
			
		});
		
		textWatermarkPosX.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}
			
		});
		
		textWatermarkPosX.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textWatermarkPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textWatermarkPosX.setText(String.valueOf(MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX)));
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / imageRatio), logo.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
				
		JLabel watermarkPosY = new JLabel(Shutter.language.getProperty("posY"));
		watermarkPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkPosY.setEnabled(false);
		watermarkPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkPosY.setForeground(Utils.themeColor);
		watermarkPosY.setBounds(px1.getX() + px1.getWidth() + 30, watermarkPosX.getLocation().y, watermarkPosX.getWidth(), 16);

		textWatermarkPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );
		textWatermarkPosY.setName("textWatermarkPosY");
		textWatermarkPosY.setEnabled(false);
		textWatermarkPosY.setBounds(watermarkPosY.getLocation().x + watermarkPosY.getWidth() + 2, watermarkPosX.getY(), 34, 16);
		textWatermarkPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkPosY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textWatermarkPosY.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textWatermarkPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / imageRatio));
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textWatermarkPosY.getText().length() >= 4)
					textWatermarkPosY.setText("");				
			}			
			
		});
		
		textWatermarkPosY.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseY = e.getY();
				MouseLogoPosition.offsetY = Integer.parseInt(textWatermarkPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}
			
		});
		
		textWatermarkPosY.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textWatermarkPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					textWatermarkPosY.setText(String.valueOf(MouseLogoPosition.offsetY + (e.getY() - MouseLogoPosition.mouseY)));
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / imageRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel px2 = new JLabel("px");
		px2.setEnabled(false);
		px2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textWatermarkPosY.getLocation().x + textWatermarkPosY.getWidth() + 2, watermarkPosX.getY(), watermarkPosX.getPreferredSize().width, 16);
		
		JLabel watermarkSize = new JLabel(Shutter.language.getProperty("lblSize"));
		watermarkSize.setEnabled(false);
		watermarkSize.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkSize.setForeground(Utils.themeColor);
		watermarkSize.setBounds(watermarkPosX.getX(), watermarkPosX.getY() + watermarkPosX.getHeight() + 4, watermarkSize.getPreferredSize().width, 16);
		
		textWatermarkSize = new JTextField("100");
		textWatermarkSize.setName("textWatermarkSize");
		textWatermarkSize.setEnabled(false);
		textWatermarkSize.setBounds(textWatermarkPosX.getLocation().x, watermarkSize.getY(), 34, 16);
		textWatermarkSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textWatermarkSize.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
				textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}
			
		});
		
		textWatermarkSize.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textWatermarkSize.getText().length() > 0)
				{	
					loadWatermark(Integer.parseInt(textWatermarkSize.getText()));	
					
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textWatermarkSize.getText().length() >= 3)
					textWatermarkSize.setText("");				
			}			
			
		});
		
		textWatermarkSize.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					
				if (textWatermarkSize.getText().length() > 0)
				{	
					int value = MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX);
					
					if (value < 1)
					{
						textWatermarkSize.setText("1");
					}
					else
						textWatermarkSize.setText(String.valueOf(value));
					
					loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * imageRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * imageRatio) ) ) );  
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;				
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel percent1 = new JLabel("%");
		percent1.setEnabled(false);
		percent1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent1.setForeground(Utils.themeColor);
		percent1.setBounds(textWatermarkSize.getLocation().x + textWatermarkSize.getWidth() + 2, watermarkSize.getY(), percent1.getPreferredSize().width, 16);
		
		JLabel watermarkOpacity = new JLabel(Shutter.language.getProperty("lblOpacity"));
		watermarkOpacity.setEnabled(false);
		watermarkOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkOpacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkOpacity.setForeground(Utils.themeColor);
		watermarkOpacity.setBounds(textWatermarkPosY.getX() - watermarkOpacity.getPreferredSize().width - 2, textWatermarkPosY.getY() + textWatermarkPosY.getHeight() + 4, watermarkOpacity.getPreferredSize().width, 16);
		
		textWatermarkOpacity = new JTextField("100");
		textWatermarkOpacity.setEnabled(false);
		textWatermarkOpacity.setName("textWatermarkOpacity");
		textWatermarkOpacity.setBounds(textWatermarkPosY.getLocation().x, watermarkOpacity.getY(), 34, 16);
		textWatermarkOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkOpacity.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		textWatermarkOpacity.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {	
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
		
		textWatermarkOpacity.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				logo.repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (textWatermarkSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textWatermarkOpacity.getText().length() > 0)
				{
					if (Integer.valueOf(textWatermarkOpacity.getText()) > 100)
					{
						textWatermarkOpacity.setText("100");
					}	
					else if (Integer.valueOf(textWatermarkOpacity.getText()) < 1)
					{
						textWatermarkOpacity.setText("1");
					}		
					
					logo.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (textWatermarkOpacity.getText().length() >= 3)
					textWatermarkOpacity.setText("");					
			}			
			
		});
		
		textWatermarkOpacity.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (textWatermarkOpacity.getText().length() > 0)
				{
					int value = MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX);
					
					if (value > 100)
					{
						textWatermarkOpacity.setText("100");						
					}
					else if (value < 1)
					{
						textWatermarkOpacity.setText("1");
					}	
					else
						textWatermarkOpacity.setText(String.valueOf(value));
					
					logo.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
		
		JLabel percent2 = new JLabel("%");
		percent2.setEnabled(false);
		percent2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		percent2.setForeground(Utils.themeColor);
		percent2.setBounds(textWatermarkOpacity.getLocation().x + textWatermarkOpacity.getWidth() + 2, watermarkOpacity.getY(), percent2.getPreferredSize().width, 16);
		
		grpWatermark.add(watermarkPosX);	
		grpWatermark.add(textWatermarkPosX);
		grpWatermark.add(px1);
		grpWatermark.add(watermarkPosY);	
		grpWatermark.add(textWatermarkPosY);
		grpWatermark.add(px2);
		grpWatermark.add(watermarkSize);	
		grpWatermark.add(textWatermarkSize);
		grpWatermark.add(percent1);
		grpWatermark.add(watermarkOpacity);
		grpWatermark.add(textWatermarkOpacity);	
		grpWatermark.add(percent2);
	
		for (Component c : grpWatermark.getComponents())
		{
			if (c instanceof JCheckBox == false)
			{
				c.setEnabled(false);
			}
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
				FFPROBE.Data(logoFile);					
				do {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				} while (FFPROBE.isRunning);
			}
									
			int logoFinalSizeWidth = (int) Math.floor((float) FFPROBE.imageWidth / imageRatio);		
			int logoFinalSizeHeight = (int) Math.floor((float) FFPROBE.imageHeight / imageRatio);
	
			//Adapt to size
			logoFinalSizeWidth = (int) Math.floor((float) logoFinalSizeWidth * ((double) size / 100));
			logoFinalSizeHeight = (int) Math.floor((float) logoFinalSizeHeight * ((double) size / 100));

			//Preserve location
			int newPosX = (int) Math.floor((logo.getWidth() - logoFinalSizeWidth) / 2);
			int newPosY = (int) Math.floor((logo.getHeight() - logoFinalSizeHeight) / 2);
			
			if (Shutter.overlayDeviceIsRunning)
			{
				FFMPEG.run(" -v quiet " + RecordInputDevice.setOverlayDevice() + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bicubic -f image2pipe pipe:-");
			}
			else
				FFMPEG.run(" -v quiet -i " + '"' + logoFile + '"' + " -vframes 1 -an -vf scale=" + logoFinalSizeWidth + ":" + logoFinalSizeHeight + " -c:v png -pix_fmt bgra -sws_flags bicubic -f image2pipe pipe:-");

			do {
				Thread.sleep(10);
			} while (FFMPEG.process.isAlive() == false);

			InputStream videoInput = FFMPEG.process.getInputStream(); 
			InputStream is = new BufferedInputStream(videoInput);
			logoPNG = ImageIO.read(is);
	       	
			if (logo.getWidth() == 0)
				logo.setLocation((int) Math.floor(player.getWidth() / 2 - logoFinalSizeWidth / 2), (int) Math.floor(player.getHeight() / 2 - logoFinalSizeHeight / 2));	
			else
				logo.setLocation(logo.getLocation().x + newPosX, logo.getLocation().y + newPosY);
			
            logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);
            
            //Saving location
			logoLocX = logo.getLocation().x;
			logoLocY = logo.getLocation().y;	
			
            logo.repaint();        
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadLogo"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
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
			
		if (forceRefresh)
		{
			Thread waitProcess = new Thread (new Runnable() {
				
				@Override
				public void run() {
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (runProcess.isAlive());
				}
			});
			waitProcess.start();
		}
		
		if (forceRefresh || runProcess.isAlive() == false)
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
						
						if (caseShowTimecode.isSelected() && FFPROBE.timecode1.equals(""))
						{
							caseShowTimecode.setSelected(false);
							caseShowTimecode.setEnabled(false);
							caseAddTimecode.setSelected(true);
							TC1.setEnabled(true);
							TC2.setEnabled(true);
							TC3.setEnabled(true);
							TC4.setEnabled(true);	
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
						
						if (new File(Shutter.dirTemp + "preview.bmp").exists() == false && caseAddSubtitles.isSelected() == false)
						{											   		
							if (extension.toLowerCase().equals(".pdf"))
							{
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								XPDF.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (isRaw)
							{	
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								DCRAW.run(" -v -w -c -q 0 -o 1 -h -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + '"' + preview + '"');
							}
							else if (Shutter.inputDeviceIsRunning) //Screen capture		
							{					
								showFPS.setVisible(false);
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								frame.setVisible(false);
								FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');								
							}
							else									
							{
								FFMPEG.run(EXRGamma + inputPoint + " -i " + '"' + file.toString() + '"' + cmd + '"' + preview + '"');			
							}
							
				            do {
				            	Thread.sleep(10);  
				            } while ((FFMPEG.isRunning && FFMPEG.error == false) || (XPDF.isRunning && XPDF.error == false) || (DCRAW.isRunning && DCRAW.error == false));
				        
				            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				            
						}	
						
						//Screen capture
						if (Shutter.inputDeviceIsRunning && preview.exists() == false)
						{
							cmd = " -vframes 1 -an " + setFilter("", "", true) + " ";	
														
							frame.setVisible(false);
							FFMPEG.run(" " +  RecordInputDevice.setInputDevices() + cmd + '"' + preview + '"');
						}				
						else
						{							
							//Subtitles are visible only from a video file
							if (caseAddSubtitles.isSelected())
							{				
								FFMPEG.run(EXRGamma + " -v quiet" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter("","", true) + " -vframes 1 -c:v bmp -sws_flags bicubic -f image2pipe pipe:-"); 
							}
							else
							{
								FFMPEG.run(EXRGamma + " -v quiet -i " + '"' + preview + '"' + setFilter("","", true) + " -vframes 1 -c:v bmp -sws_flags bicubic -f image2pipe pipe:-"); 							    	
							}
						}

						do {
	    					Thread.sleep(10);
	    				} while (FFMPEG.process.isAlive() == false);
						
						//IMPORTANT
						if (Shutter.caseInAndOut.isSelected())
						{
							frame.setVisible(true);
						}
	
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
		if (caseAddSubtitles.isSelected() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{			
			//Color	
			if (fontSubsColor != null)
			{
				 String c = Integer.toHexString(fontSubsColor.getRGB()).substring(2);
				 subsHex = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}
			
			if (backgroundSubsColor != null)
			{
				 String c = Integer.toHexString(backgroundSubsColor.getRGB()).substring(2);
				 subsHex2 = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}		
			
			
			subsAlpha = "00";
			outline = "1";
			if (lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
			{
				int o = (int) (255 - (float) ((int) Integer.valueOf(textSubsOutline.getText()) * 255) / 100);
				subsAlpha = Integer.toHexString(o);
			}
			else
			{
				outline = String.valueOf((float) ((float) ((int) Integer.valueOf(textSubsOutline.getText())) * 2) / 100);
			}
			
			//Fond sous-titres							
			if (lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				background = ",BorderStyle=4,BackColour=&H" + subsAlpha + subsHex2 + "&,Outline=0";
			else
				background = ",Outline=" + outline + ",OutlineColour=&H" + subsAlpha + subsHex2 + "&";
				
			//Bold
			if (btnG.getForeground() != Color.BLACK)
				background += ",Bold=1";
			
			//Italic
			if (btnI.getForeground() != Color.BLACK)
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
		if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 0 || Shutter.comboRotate.getSelectedIndex() == 1))
		{
			width = player.getHeight();
			height = player.getWidth();
		}
		
		if (FFMPEG.isGPUCompatible && caseGPU.isSelected() && noGPU == false)
		{
			String bitDepth = "nv12";
			if (FFPROBE.imageDepth == 10)
			{
				bitDepth = "p010";
			}			
			
			//Auto GPU selection	
			if (FFMPEG.cudaAvailable)
			{
				filter = filter.replace("yadif", "yadif_cuda");			
				filter += "scale_cuda=" + width + ":" + height + ":interp_algo=nearest,hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.qsvAvailable && yadif == "")
			{
				filter += "scale_qsv=" + width + ":" + height + ":mode=low_power,hwdownload,format=" + bitDepth;
			}	
			else
			{
				filter += "scale=" + width + ":" + height + ":flags=neighbor";
			}
		}
		else
		{
			filter += "scale=" + width + ":" + height + ":flags=neighbor";
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
		if (stabilisation != "")
			eq = stabilisation;
		
		//Blend
		if (preview.exists() == false) //Show only on playing
			eq = ImageSequence.setBlend(eq);
		
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
		if (caseEnableColorimetry.isSelected())
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
			
			if (sliderAngle.getValue() != 0)
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
		if (caseVideoFadeIn.isSelected() || caseVideoFadeOut.isSelected())
		{
			eq = Transitions.setVideoFade(eq, true);
		}
								
		if (eq.isEmpty() == false)
		{
			filter += "," + eq;
		}
		
		if (caseVuMeter.isSelected() && FFPROBE.hasAudio && caseAddSubtitles.isSelected() == false && preview.exists() == false)
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
		
		if (caseAddSubtitles.isSelected() && Shutter.subtitlesBurn && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{						
			caseVuMeter.setEnabled(false);
			
			int subsWidth = (int) ((float) (Integer.parseInt(textSubsWidth.getText()) / imageRatio));
			int subsPosY = (int) ((float) Integer.parseInt(textSubtitlesPosition.getText()) / imageRatio);
			
			return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + subsWidth + ":" + player.getHeight() + "+" + subsPosY
          			+ ",subtitles='" + Shutter.subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + comboSubsFont.getSelectedItem().toString() + ",FontSize=" + textSubsSize.getText() + ",PrimaryColour=&H" + subsHex + "&" + background + "'" + '"'
          			+ " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];[v][1:v]overlay=x=" + (int) ((player.getWidth() - subsWidth) / 2) + ",scale=" + player.getWidth() + ":" + player.getHeight() + '"'; 
		}
		else
		{
			caseVuMeter.setEnabled(true);	
			return filter;
		}

	}
	
	private void resizeAll() {
						
		if (preview.exists())
			preview.delete();
		
		//topPanel
		topPanel.setBounds(0,0,frame.getSize().width, 28);
		topImage.setLocation(frame.getSize().width / 2 - topImage.getSize().width / 2, 0);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15, 15);
		reduce.setBounds(fullscreen.getLocation().x - 20, 4, 15, 15); 		

		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_AREA_AVERAGING));
		bottomImage.setIcon(imageIcon);
		
		bottomImage.setBounds(0 ,0, frame.getSize().width, 28);
		
		//Buttons		
		btnCapture.setBounds(9, topPanel.getSize().height + 10, 310, 21);		
		btnApply.setBounds(frame.getSize().width - 8 - 308 - 2, topPanel.getSize().height + 10, 310, 21);		
		btnPreview.setBounds(frame.getSize().width - 36, frame.getSize().height - 26, 16, 16);
		splitValue.setBounds(frame.getSize().width - 48, frame.getSize().height - 26, 34, 16);
		lblSplitSec.setBounds(splitValue.getX() + splitValue.getWidth() + 2, splitValue.getY(), lblSplitSec.getPreferredSize().width, 16);
		
		//Group boxes
		grpIn.setBounds(6, frame.getSize().height - 84, 156, 52);
		grpOut.setBounds(frame.getWidth() - grpIn.getWidth() - 12, grpIn.getY(), grpIn.getWidth(), grpIn.getHeight());

		//grpColorimetry
		if (grpColorimetry.getHeight() > 17)
		{
			int grpInSize = (frame.getHeight() - grpIn.getY());
			if (waveformContainer.isVisible() == false)
			{
				grpInSize = 0;
			}
			
			grpColorimetry.setSize(grpColorimetry.getWidth(), frame.getHeight() - grpInSize - grpColorimetry.getY() - grpCorrections.getHeight() - grpTransitions.getHeight() - 18);	
			
			if (grpColorimetry.getHeight() > btnReset.getY() + btnReset.getHeight() + 30)
			{
				grpColorimetry.setSize(grpColorimetry.getWidth(), btnReset.getY() + btnReset.getHeight() + 30);
			}
			panelColorimetryComponents.setSize(grpColorimetry.getWidth() - 18, grpColorimetry.getHeight() - 25);
			
			grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
			grpTransitions.setLocation(grpColorimetry.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);	
		}
		
		scrollBarColorimetry.setValue(0);
		
		if (grpColorimetry.getHeight() < btnReset.getY() + btnReset.getHeight() + 30)
		{			
			scrollBarColorimetry.setSize(11, grpColorimetry.getHeight() - 11);
			scrollBarColorimetry.setMaximum((btnReset.getY() + btnReset.getHeight() + 15) - panelColorimetryComponents.getHeight());
			scrollBarColorimetry.setVisible(true);	
		}
		else
			scrollBarColorimetry.setVisible(false);	
								
		comboMode.setLocation(splitValue.getX() - 74 - 5, frame.getSize().height - 16 - 12 - 1);
		lblMode.setBounds(comboMode.getX() - lblMode.getPreferredSize().width - 4, frame.getSize().height - 16 - 12 + 2, lblMode.getPreferredSize().width, 16);		

		caseVuMeter.setBounds(lblMode.getX() - caseVuMeter.getPreferredSize().width - 5, comboMode.getY(), caseVuMeter.getPreferredSize().width, 23);
		caseGPU.setBounds(caseVuMeter.getX() - caseGPU.getPreferredSize().width - 5, comboMode.getY(), caseGPU.getPreferredSize().width, 23);
		
		caseInternalTc.setBounds(grpIn.getX(), frame.getSize().height - 16 - 12, caseInternalTc.getPreferredSize().width, 23);	
		casePlaySound.setBounds(caseInternalTc.getX() + caseInternalTc.getWidth() + 4, caseInternalTc.getY(), casePlaySound.getPreferredSize().width, 23);

		float timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseInF.getText());
		float timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(caseOutF.getText());

		//Sliders
		if (waveformContainer.isVisible())
		{
			slider.setBounds(grpIn.getLocation().x + grpIn.getSize().width + 6, grpIn.getLocation().y, frame.getSize().width - (grpIn.getLocation().x + grpIn.getSize().width + 6) * 2 - 6, 60); 
			waveformContainer.setBounds(slider.getX(), slider.getY() + 7, slider.getWidth(), grpIn.getHeight() - 9);
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
		}
				
		//lblTimecode & lblDuration
		lblPosition.setBounds(slider.getX(), slider.getY() - 16, slider.getWidth(), 16);
		lblDuration.setBounds(slider.getX(), slider.getY() + slider.getHeight() - 2, slider.getWidth(), 16);   
		
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
			int maxHeight = frame.getHeight() - (frame.getHeight() - lblPosition.getY()) - btnCapture.getY() - btnCapture.getHeight() - 64;
			
			if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
			{
				maxHeight = frame.getHeight() - btnCapture.getY() - btnCapture.getHeight() - 24;
			}
			
			player.setSize((int) (maxHeight * ratio), maxHeight);
		}
		else
		{
			int maxWidth = frame.getWidth() - grpCrop.getWidth() * 2 - 24;
			
			player.setSize(maxWidth, (int) (maxWidth / ratio));			
		}	
				
		if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
		{
			player.setLocation((frame.getSize().width - player.getSize().width) / 2, frame.getHeight() / 2 - player.getHeight() / 2 + 28);
		}
		else
			player.setLocation((frame.getSize().width - player.getSize().width) / 2, frame.getHeight() / 2 - player.getHeight() / 2 - 32);
		
		//IMPORTANT video canvas must be a multiple of 4!
		player.setSize(player.getWidth() - (player.getWidth() % 4), player.getHeight());	

		imageRatio = (float) FFPROBE.imageWidth / player.getWidth();
						
		//grpOverlay
		grpOverlay.setLocation(frame.getWidth() - grpOverlay.getWidth() - 6, grpCrop.getY() + grpCrop.getHeight() + 6);
		
		//grpSubtitles
		grpSubtitles.setLocation(frame.getWidth() - grpSubtitles.getWidth() - 6, grpOverlay.getY() + grpOverlay.getHeight() + 6);
		if (caseAddSubtitles.isSelected())
		{						    		
			if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
			{
				subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
			}
			else
			{
				subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
			    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
				
				subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);
			}			
		}
		
		//grpWatermark
		grpWatermark.setLocation(frame.getWidth() - grpWatermark.getWidth() - 6, grpSubtitles.getY() + grpSubtitles.getHeight() + 6);
		
		if (caseAddWatermark.isSelected())
		{
			loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
			logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / imageRatio), (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / imageRatio));
		}
					
		if (grpWatermark.getY() + grpWatermark.getHeight() >= grpOut.getY() - 6)
		{									
			grpOverlay.setSize(grpOverlay.getWidth(), 17);
			grpSubtitles.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
			grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);	
		}
		
		//grpCrop
		grpCrop.setLocation(frame.getWidth() - grpCrop.getWidth() - 6, grpColorimetry.getY());
		overImage.setBounds(0,0, player.getWidth(), player.getHeight());
		
		frameCropX = player.getLocation().x;
		frameCropY = player.getLocation().y;
		if (caseEnableCrop.isSelected())
		{
			selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textCropPosY.getText()) / imageRatio));
			int w = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
			int h = (int) Math.round((float)  (Integer.valueOf(textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
			
			if (w > player.getWidth())
				w = player.getWidth();
			
			if (h > player.getHeight())
				h = player.getHeight();
			
			selection.setSize(w , h);	
		}
		else
		{
			//Important
			selection.setBounds(player.getWidth() / 4, player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);	
			anchorRight = selection.getLocation().x + selection.getWidth();
			anchorBottom = selection.getLocation().y + selection.getHeight();
		}
		
		//grpOverlay
		if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
		{
			timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
			tcLocX = timecode.getLocation().x;
			tcLocY = timecode.getLocation().y;			
		}
		if (caseAddText.isSelected() || caseShowFileName.isSelected())
		{
			fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
			fileLocX = fileName.getLocation().x;
			fileLocY = fileName.getLocation().y;
		}
		
		btnPrevious.setBounds(player.getLocation().x + player.getSize().width / 2 - 21 - 4, player.getLocation().y + player.getSize().height + 10, 22, 21);		
		btnNext.setBounds(player.getLocation().x + player.getSize().width / 2 + 4, btnPrevious.getLocation().y, 22, 21);		
		btnPlay.setBounds(btnPrevious.getLocation().x - 80 - 4, btnPrevious.getLocation().y, 80, 21);				
		btnStop.setBounds(btnNext.getLocation().x + btnNext.getSize().width + 4, btnNext.getLocation().y, 80, 21);	
		btnMarkIn.setBounds(btnPlay.getLocation().x - 22 - 4, btnPlay.getLocation().y, 22, 21);				
		btnGoToIn.setBounds(btnMarkIn.getLocation().x - 40 - 4, btnMarkIn.getLocation().y, 40, 21);				
		btnMarkOut.setBounds(btnStop.getLocation().x + btnStop.getSize().width + 4, btnStop.getLocation().y, 22, 21);				
		btnGoToOut.setBounds(btnMarkOut.getLocation().x + btnMarkOut.getSize().width + 4, btnMarkOut.getLocation().y, 40, 21);		
		showFPS.setBounds(player.getX() + player.getWidth() / 2, player.getY() - 18, player.getWidth() / 2, showFPS.getPreferredSize().height);
		showScale.setBounds(player.getX(), showFPS.getY(), player.getWidth() / 2, showScale.getPreferredSize().height);
		btnPreviousFile.setBounds(player.getX(), btnCapture.getY(), 84, 21);	
		btnNextFile.setBounds(player.getX() + player.getWidth() - 84, btnPreviousFile.getY(), btnPreviousFile.getWidth(), 21);
		lblVideo.setBounds(btnPreviousFile.getX() + btnPreviousFile.getWidth() + 3, topPanel.getSize().height + 12, btnNextFile.getX() - btnPreviousFile.getX() + btnPreviousFile.getWidth() - 174, 16);
		
		sliderSpeed.setLocation(btnGoToIn.getX() -  sliderSpeed.getWidth() - 4, btnGoToIn.getY() + 1);
		lblSpeed.setBounds(sliderSpeed.getX() - lblSpeed.getPreferredSize().width - 2, sliderSpeed.getY() + 2, lblSpeed.getPreferredSize().width, 16);
					
		lblVolume.setLocation(btnGoToOut.getX() + btnGoToOut.getWidth() + 7, lblSpeed.getY());	
		sliderVolume.setBounds(lblVolume.getX() + lblVolume.getWidth() + 1, sliderSpeed.getY(), sliderSpeed.getWidth(), 22);	
		
		title.setBounds(0, 0, frame.getWidth(), 28);
				
		if (windowDrag == false)
		{	
			if (Shutter.inputDeviceIsRunning || FFPROBE.totalLength <= 40)
			{
				loadImage(false);
				waveformIcon.setVisible(false);
			}
			else
			{		
				//Waveforms
				addWaveform(false);	
	
				if (btnPlay.isEnabled())
					playerFreeze();	
			}
		}
		else
			waveformIcon.setVisible(false);
		
		refreshTimecodeAndText();
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

						Shutter.textH.setText(formatter.format(Math.floor(total / FFPROBE.currentFPS / 3600)));
						Shutter.textM.setText(formatter.format(Math.floor(total / FFPROBE.currentFPS / 60) % 60));
						Shutter.textS.setText(formatter.format(Math.floor(total / FFPROBE.currentFPS) % 60));    		
						Shutter.textF.setText(formatter.format(Math.round(total % FFPROBE.currentFPS)));
			    	     
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
			String h = formatter.format(Math.floor(time / FFPROBE.currentFPS / 3600));
			String m =  formatter.format(Math.floor(time / FFPROBE.currentFPS / 60) % 60);
			String s = formatter.format(Math.floor(time / FFPROBE.currentFPS) % 60);   
			String f = formatter.format(Math.floor(time % FFPROBE.currentFPS));
					    	
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
			
    		if (sliderChange == false && windowDrag == false)
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
    		btnPlay.setText(Shutter.language.getProperty("btnPlay"));
    	}
    		
    }

	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@SuppressWarnings("rawtypes")
			@Override
			public void run() {
				
			try {
			
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
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
						for (Component p : frame.getContentPane().getComponents())
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
						
						//grpColorimetry
						for (Component p : panelColorimetryComponents.getComponents())
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
				if (caseEnableCrop.isSelected())
				{
					selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textCropPosY.getText()) / imageRatio));
					int w = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					int h = (int) Math.round((float)  (Integer.valueOf(textCropHeight.getText()) * player.getHeight()) / FFPROBE.imageHeight);
					
					if (w > player.getWidth())
						w = player.getWidth();
					
					if (h > player.getHeight())
						h = player.getHeight();
					
					selection.setSize(w , h);	
					
					frameCropX = player.getLocation().x;
					frameCropY = player.getLocation().y;
					
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();					
					checkSelection();
				}
				
				//grpOverlay
				if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
				{
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textTcPosY.getText()) / imageRatio));
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;			
				}
				if (caseAddText.isSelected() || caseShowFileName.isSelected())
				{
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / imageRatio), (int) Math.round(Integer.valueOf(textNamePosY.getText()) / imageRatio));
					fileLocX = fileName.getLocation().x;
					fileLocY = fileName.getLocation().y;
				}
				
				//grpSubtitles
				if (caseAddSubtitles.isSelected())
				{						    		
					if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
					{
						subsCanvas.setBounds(0, 0, player.getWidth(), (int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));
					}
					else
					{
						subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())),
					    		(int) (player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / player.getHeight())));	
						
						subsCanvas.setLocation((player.getWidth() - subsCanvas.getWidth()) / 2, 0);
					}			
				}
				
				//grpWatermark
				if (caseAddWatermark.isSelected())
				{
					loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / imageRatio), (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / imageRatio));
					//Saving location
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
				
				timecode.repaint();
				fileName.repaint();
				selection.repaint();
				overImage.repaint();
				
				} catch (Exception e) {
				} finally {
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		t.start();	
	}
	
	private class ComboRenderer extends BasicComboBoxRenderer {

        private static final long serialVersionUID = 1L;
		@SuppressWarnings("rawtypes")
		private JComboBox comboBox;
        final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private int row;

		@SuppressWarnings("rawtypes")
		private ComboRenderer(JComboBox fontsBox) {
            comboBox = fontsBox;
        }
        
		@SuppressWarnings({ "rawtypes", "unused" })
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