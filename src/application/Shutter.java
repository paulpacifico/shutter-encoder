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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import java.awt.event.WindowStateListener;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import functions.AudioEncoders;
import functions.AudioNormalization;
import functions.BlackDetection;
import functions.Command;
import functions.Conform;
import functions.DVDRIP;
import functions.Extract;
import functions.LoudnessTruePeak;
import functions.Rewrap;
import functions.Merge;
import functions.OfflineDetection;
import functions.Picture;
import functions.ReplaceAudio;
import functions.VideoEncoders;
import functions.VideoInserts;
import library.BMXTRANSWRAP;
import library.DCRAW;
import library.DECKLINK;
import library.DVDAUTHOR;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPLAY;
import library.FFPROBE;
import library.MEDIAINFO;
import library.MKVMERGE;
import library.SEVENZIP;
import library.TSMUXER;
import library.XPDF;
import library.YOUTUBEDL;
import settings.FunctionUtils;

@SuppressWarnings("serial")
public class Shutter {

	/*
	 * Initialisation
	 */
	public static String actualVersion = "15.5";
	public static String getLanguage = "";
	public static String pathToFont = "JRE/lib/fonts/Montserrat.ttf";
	public static String magnetoFont = "Magneto";
	public static String montserratFont = "Montserrat";
	public static String freeSansFont = "FreeSans";
	public static File documents = new File(System.getProperty("user.home") + "/Documents/Shutter Encoder");
	public static String dirTemp = System.getProperty("java.io.tmpdir");
	public static File lutsFolder;
	public static File subtitlesFile;
	public static Properties language = new Properties();
	protected static final String fullscreen = null;
	public static URL soundURL;
	public static URL soundErrorURL;
	public static JFrame frame = new JFrame();
	public static boolean cancelled = false;
	public static float ratioFinal = 0; // CropVideo
	public static String cropFinal = null; // CropImage
	public static String finalEQ = null; // ColorImage
	public static boolean scanIsRunning = false;
	public static JMenuItem menuDisplay;
	public static JMenuItem inputDevice;
	public static boolean inputDeviceIsRunning = false;
	public static boolean overlayDeviceIsRunning = false;
	public static boolean sendMailIsRunning = false;
	protected static boolean canScroll = true;
	public static JMenuItem scan;
	static ArrayList<String> droppedFiles = new ArrayList<String>(); // Drop file application
	public static boolean saveCode = false;
	protected static boolean copyFileIsRunning = false;
	protected static boolean subtitlesBurn = true;
	public static StringBuilder errorList = new StringBuilder();

	private static int MousePositionX;
	private static int MousePositionY;
	
	/*
	 * Animations
	 */
	private static boolean changeGroupes = false;

	/*
	 * Position ReducedWindow
	 */
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static Rectangle bounds = ge.getMaximumWindowBounds();
	public static int MiniWindowY = bounds.height / 2;

	/*
	 * Components
	 */
	private static JLabel settings;
	private static JLabel quit;
	private static JLabel reduce;
	private static JLabel help;
	private static JLabel newInstance;
	
	private static ImageIcon wetransferIcon;
	private static ImageIcon mailIcon;
	private static ImageIcon streamIcon;

	public static DefaultListModel<String> liste = new DefaultListModel<String>();
	protected static PopupMenu dropFiles;
	public static JList<String> fileList;
	static JLabel addToList = new JLabel();
	public static JComboBox<String[]> comboFonctions;

	protected static JButton btnBrowse;
	protected static JButton btnEmptyList;
	protected static JComboBox<Object> comboFilter;
	protected static JComboBox<Object> comboLUTs;
	protected static JComboBox<Object> comboInLevels;
	protected static JComboBox<Object> comboOutLevels;
	protected static JComboBox<Object> comboInColormatrix;
	protected static JComboBox<Object> comboOutColormatrix;
	protected static JComboBox<Object> comboColorspace;
	protected static JButton btnStart;
	protected static JButton btnCancel;
	protected static JRadioButton caseOpenFolderAtEnd1;
	protected static JRadioButton caseOpenFolderAtEnd2;
	protected static JRadioButton caseOpenFolderAtEnd3;
	protected static JRadioButton caseChangeFolder1;
	protected static JRadioButton caseChangeFolder2;
	protected static JRadioButton caseChangeFolder3;
	protected static JRadioButton caseRunInBackground;
	protected static JRadioButton caseDisplay;
	protected static JLabel iconTVInterpret;
	protected static JLabel iconTVResolution;
	protected static JLabel iconList;
	protected static JLabel iconPresets;
	protected static JComboBox<String> comboResolution;
	protected static JRadioButton caseRognerImage;
	protected static JLabel lblImageQuality;
	protected static JComboBox<String> comboImageQuality;
	protected static JRadioButton caseRotate;
	protected static JComboBox<String> comboRotate;
	protected static JRadioButton caseMiror;
	protected static JRadioButton caseCreateSequence;
	protected static JLabel lblInterpretation;
	protected static JLabel lblIsInterpret;
	protected static JComboBox<String> comboInterpret;
	protected static JRadioButton case2pass;
	protected static JRadioButton caseRognage;
	protected static JRadioButton caseQMax;
	protected static JRadioButton caseEnableSequence;
	protected static JRadioButton caseYear;
	protected static JRadioButton caseMonth;
	protected static JRadioButton caseDay;
	protected static JRadioButton caseFrom;
	protected static JComboBox<String> comboYear;
	protected static JComboBox<String> comboMonth;
	protected static JComboBox<String> comboDay;
	protected static JComboBox<String> comboFrom;
	protected static JComboBox<String> comboTo;
	protected static JRadioButton caseLUTs;
	protected static JRadioButton caseLevels;
	protected static JRadioButton caseColormatrix;
	protected static JRadioButton caseColorspace;
	protected static JRadioButton caseColor;
	protected static JComboBox<String> caseSequenceFPS;
	protected static JRadioButton caseSetTimecode;
	protected static JRadioButton caseIncrementTimecode;
	protected static JRadioButton caseGenerateFromDate;
	protected static JTextField TCset1;
	protected static JTextField TCset2;
	protected static JTextField TCset3;
	protected static JTextField TCset4;
	protected static JRadioButton caseAddOverlay;
	protected static JRadioButton caseShowDate;
	protected static JRadioButton caseShowFileName;
	protected static JRadioButton caseChangeAudioCodec;
	protected static JRadioButton caseAudioOffset;
	protected static JRadioButton caseSampleRate;
	protected static JRadioButton caseMixAudio;
	protected static JRadioButton caseSplitAudio;
	protected static JRadioButton caseConvertAudioFramerate;
	protected static JLabel lblFromTo;
	protected static JLabel lblAudioIs;
	protected static JLabel lblSplit;
	protected static JLabel lblMix;
	protected static JRadioButton caseInAndOut;
	protected static JRadioButton caseOpenGop;
	protected static JRadioButton caseForcerProgressif;
	protected static JRadioButton caseForcerEntrelacement;
	protected static JRadioButton caseForcerInversion;
	protected static JRadioButton caseForcerDesentrelacement;
	protected static JComboBox<String> comboForcerDesentrelacement;
	protected static JRadioButton caseForceOutput;
	protected static JRadioButton caseFastStart;
	protected static JRadioButton caseAlpha;
	protected static JRadioButton caseGOP;
	protected static JTextField gopSize;
	protected static JRadioButton caseForceLevel;
	protected static JRadioButton caseForcePreset;
	protected static JRadioButton caseForceTune;
	protected static JRadioButton caseForceQuality;	
	protected static JRadioButton caseForceSpeed;
	protected static JRadioButton caseLogo;
	protected static JRadioButton caseAccel;
	protected static JComboBox<String> comboAccel;
	protected static JComboBox<String> comboForceProfile;
	protected static JComboBox<String> comboForceLevel;
	protected static JComboBox<String> comboForcePreset;
	protected static JComboBox<String> comboForceTune;
	protected static JComboBox<String> comboForceQuality;	
	protected static JComboBox<String> comboForceSpeed;
	protected static JRadioButton caseForcerDAR;
	protected static JRadioButton caseLimiter;
	protected static JRadioButton caseDecimate;
	protected static JRadioButton caseConform;
	protected static JRadioButton caseCreateTree;
	protected static JRadioButton casePreserveMetadata;
	protected static JRadioButton casePreserveSubs;
	protected static JRadioButton caseCreateOPATOM;
	protected static JRadioButton caseOPATOM;
	protected static JRadioButton caseSubtitles;
	protected static JRadioButton caseAS10;
	protected static JComboBox<String> comboConform;
	protected static JComboBox<String> comboFPS;
	protected static JComboBox<String> comboAudioIn;
	protected static JComboBox<String> comboAudioOut;
	protected static JComboBox<String> comboAudioCodec;
	protected static JComboBox<String> comboAudioBitrate;
	protected static JComboBox<String> comboAudio1;
	protected static JComboBox<String> comboAudio2;
	protected static JComboBox<String> comboAudio3;
	protected static JComboBox<String> comboAudio4;
	protected static JComboBox<String> comboAudio5;
	protected static JComboBox<String> comboAudio6;
	protected static JComboBox<String> comboAudio7;
	protected static JComboBox<String> comboAudio8;
	protected static JComboBox<String> comboAS10;
	protected static JTextField txtAudioOffset;
	protected static JPanel topPanel;
	protected static JLabel lblV;
	protected static JLabel topImage;
	protected static JScrollPane scrollBar;
	protected static JLabel lblFilesEnded;
	protected static JLabel lblFiles;
	protected static JLabel lblFilter;
	protected static JTextField lblDestination1;
	protected static JTextField lblDestination2;
	protected static JTextField lblDestination3;
	protected static JProgressBar progressBar1;
	protected static JLabel lblCurrentEncoding;
	protected static JLabel lblTaille;
	protected static JLabel lblToConform;
	protected static JLabel lblIsConform;
	protected static JLabel lblTFF;
	protected static JLabel lbl48k;
	protected static JLabel lblKbs;
	protected static JLabel lblOffsetFPS;
	protected static JLabel lblAudio1;
	protected static JLabel lblAudio2;
	protected static JLabel lblAudio3;
	protected static JLabel lblAudio4;
	protected static JLabel lblAudio5;
	protected static JLabel lblAudio6;
	protected static JLabel lblAudio7;
	protected static JLabel lblAudio8;
	protected static JLabel lblAudioMapping;
	protected static JLabel lblPad;
	protected static JComboBox<String> comboDAR;
	protected static JLabel lblNiveaux;
	protected static JLabel lblOPATOM;
	protected static JLabel lblCreateOPATOM;
	protected static JRadioButton caseStabilisation;
	protected static JRadioButton caseDeflicker;
	protected static JLabel iconTVLUTs;
	protected static JLabel iconTVDetails;
	protected static JLabel iconTVOffset;
	protected static JRadioButton caseBanding;
	protected static JRadioButton caseDetails;
	protected static JSlider sliderDetails;
	protected static JLabel iconTVBruit;
	protected static JRadioButton caseBruit;
	protected static JSlider sliderBruit;
	protected static JLabel iconTVBlend;
	protected static JRadioButton caseBlend;
	protected static JRadioButton caseMotionBlur;
	protected static JSlider sliderBlend;
	protected static JLabel iconTVExposure;
	protected static JRadioButton caseExposure;
	protected static JSlider sliderExposure;
	protected static JButton btnReset;
	protected static JLabel statusBar;
	protected static JLabel lblYears;
	protected static JLabel lblCrParPaul;
	protected static JLabel tempsRestant;
	protected static JLabel tempsEcoule;
	protected static JLabel lblFadeInColor;
	protected static JRadioButton caseVideoFadeIn;
	protected static JTextField spinnerVideoFadeIn;
	protected static JRadioButton caseAudioFadeIn;
	protected static JTextField spinnerAudioFadeIn;
	protected static JLabel lblFadeOutColor;
	protected static JRadioButton caseVideoFadeOut;
	protected static JTextField spinnerVideoFadeOut;
	protected static JRadioButton caseAudioFadeOut;
	protected static JTextField spinnerAudioFadeOut;
	protected static JTextField textH;
	protected static JTextField textMin;
	protected static JTextField textSec;
	protected static JComboBox<String> comboH264Taille;
	protected static JComboBox<String> debitVideo;
	protected static JComboBox<String> debitAudio;
	protected static JPanel h264lines;
	protected static JTextField taille;
	protected static JLabel lock;
	public static boolean isLocked = false;
	protected static JLabel lblDureH264;
	protected static JLabel lblSec;
	protected static JLabel lblMin;
	protected static JLabel lblH;
	protected static JLabel lblTailleH264;
	protected static JLabel lblH264;
	protected static JLabel iconTVH264;
	protected static JLabel lblKbsH264;
	protected static JLabel lblSize;
	protected static JLabel lblDbitVido;
	protected static JLabel lblDbitAudio;
	protected static JLabel lblVBR;
	protected JPopupMenu popupList;
	protected static JPopupMenu scanListe;
	protected JPopupMenu popupProgression;
	protected JPopupMenu popupDestination;
	protected static JTextField textMail;
	protected static JRadioButton caseSendMail;
	protected static JTextField textStream;
	protected static JRadioButton caseStream;
	protected static JRadioButton caseLoop;

	/*
	 * Groupes Boxes
	 */
	protected static JPanel grpChooseFiles;
	protected static JPanel grpChooseFunction;
	protected static JTabbedPane grpDestination;
	protected static JPanel destination1;
	protected static JPanel destination2;
	protected static JPanel destination3;
	protected static JPanel destinationMail;
	protected static JPanel destinationStream;
	protected static JPanel grpProgression;
	protected static JPanel grpResolution;
	protected static JPanel grpImageSequence;
	protected static JPanel grpImageFilter;
	protected static JPanel grpColorimetry;
	protected static JPanel grpInAndOut;
	protected static JPanel grpSetTimecode;
	protected static JPanel grpOverlay;
	protected static JPanel grpSetAudio;
	protected static JPanel grpAudio;
	protected static JPanel grpCorrections;
	protected static JPanel grpAdvanced;
	protected static JPanel grpH264;
	protected static JPanel grpTransitions;

	public static void main(String[] args) {
		
		//Splashscreen
		new Splash();
		
		//Accès à la police Montserrat pour drawtext
		if (System.getProperty("os.name").contains("Mac"))
		{
			pathToFont = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			pathToFont = pathToFont.substring(0,pathToFont.length()-1);
			pathToFont = pathToFont.substring(0,(int) (pathToFont.lastIndexOf("/"))).replace("%20", " ");
			pathToFont = "'" + pathToFont + "/JRE/Contents/Home/lib/fonts/Montserrat.ttf" + "'";
		}
		else if (System.getProperty("os.name").contains("Linux"))
		{
			pathToFont = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			pathToFont = pathToFont.substring(0,pathToFont.length()-1);
			pathToFont = pathToFont.substring(0,(int) (pathToFont.lastIndexOf("/"))).replace("%20", " ");
			pathToFont = "'" + pathToFont + "fonts/Montserrat.ttf" + "'";
		}

		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				droppedFiles.add(i, args[i]);
			}
		}
		
		Utils.setLanguage();
	
		Utils.loadThemes();		
		Splash.increment();
		
		// Documents Shutter Encoder
		if (Shutter.documents.exists() == false)
			Shutter.documents.mkdirs();

		new Shutter();
		
		Utils.textFieldBackground();
	}

	public Shutter() {
					
		frame.getContentPane().setBackground(new Color(50, 50, 50));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Shutter Encoder");
		frame.setBackground(new Color(50,50,50));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(332, 670);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
        frame.setShape(shape1);
		//frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100, 100, 100)));
		
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
				Settings.saveSettings();
            }
        });
		
		soundURL = this.getClass().getClassLoader().getResource("contents/complete.wav");
		soundErrorURL = this.getClass().getClassLoader().getResource("contents/error.wav");
		
		// Seulement pour mac
		//if (System.getProperty("os.name").contains("Mac"))
			//Application.getApplication().setDockIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());

		frame.getRootPane().setDefaultButton(btnStart);

		frame.addWindowStateListener(new WindowStateListener() {

			@SuppressWarnings("static-access")
			@Override
			public void windowStateChanged(WindowEvent arg0) {
				if (frame.getState() == frame.NORMAL && ReducedWindow.frame != null)
				{
					ReducedWindow.frame.setVisible(false);					
					frame.toFront();
				}
					
			}

		});
		
		//Settings
		Settings.txtThreads.setText("0");
		Settings.txtImageDuration.setText("10");
				
		Splash.increment();
		topPanel();
		Splash.increment();
		StatusBar();
		Splash.increment();
		grpChooseFiles();
		Splash.increment();
		grpChooseFunction();
		Splash.increment();
		grpDestination();
		Splash.increment();
		grpProgress();
		Splash.increment();
		grpResolution();
		Splash.increment();
		grpImageSequence();
		Splash.increment();
		grpImageFilter();
		Splash.increment();
		grpColorimetry();
		Splash.increment();
		grpSetTimecode();
		Splash.increment();
		grpOverlay();
		Splash.increment();
		grpInAndOut();
		Splash.increment();
		grpSetAudio();
		Splash.increment();
		grpAudio();
		Splash.increment();
		grpCorrections();
		Splash.increment();
		grpAdvanced();
		Splash.increment();
		grpH264();
		Splash.increment();
		grpTransitions();
		Splash.increment();
		Reset();
		Splash.increment();
		
		comboFonctions.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					changeFunction(true);
					changeFilters();
				}
			}
		});
		Splash.increment();
		
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
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
				fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 0));
				lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));					
			}

		});
		
		frame.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getX() < 332)
					canScroll = false;
				else
					canScroll = true;
			}
			
		});

		// Keyboard shortcuts
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			public void eventDispatched(AWTEvent event) {
				
				if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false) 
				{					
					KeyEvent ke = (KeyEvent) event;
										
					if (ke.getID() == KeyEvent.KEY_PRESSED) 
					{					
						//CMD + Q
						if (System.getProperty("os.name").contains("Mac") && (ke.getKeyCode() == KeyEvent.VK_Q) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						{							
							Runtime.getRuntime().addShutdownHook(new Thread()
					        {
					            @Override
					            public void run()
					            {
					            	if (FFMPEG.runProcess != null && FFMPEG.runProcess.isAlive()) 
					            	{
										if (btnStart.getText().equals(language.getProperty("resume")))
											FFMPEG.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le
																	// détruire

										try {
											FFMPEG.writer.write('q');
											FFMPEG.writer.flush();
											FFMPEG.writer.close();
										} catch (IOException er) {
										}
										
										if (comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false)
											FFMPEG.process.destroy();
									}					            	
									if (DCRAW.runProcess != null && DCRAW.runProcess.isAlive())
									{
										DCRAW.process.destroy();
									}									
									if (XPDF.runProcess != null && XPDF.runProcess.isAlive())
									{
										XPDF.process.destroy();
									}									
									if (MKVMERGE.runProcess != null && MKVMERGE.runProcess.isAlive())
									{
										MKVMERGE.process.destroy();
									}									
									if (YOUTUBEDL.runProcess != null && YOUTUBEDL.runProcess.isAlive())
									{
										YOUTUBEDL.process.destroy();
									}
									if (BMXTRANSWRAP.runProcess != null && BMXTRANSWRAP.runProcess.isAlive())
									{
										BMXTRANSWRAP.process.destroy();
									}
									if (Ftp.isRunning) {
										try {
											Ftp.ftp.abort();
										} catch (IOException e1) {
											System.out.println(e1);
										}
									}
									if (Wetransfer.isRunning) {
										try {
											Wetransfer.process.destroy();
										} catch (Exception e1) {
											System.out.println(e1);
										}
									}
									
									Settings.saveSettings();
					            }
					        });
						}
						
						if ((ke.getKeyCode() == KeyEvent.VK_S) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
						|| (ke.getKeyCode() == KeyEvent.VK_S)
						&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
						{
							if ((btnStart.getText().equals(Shutter.language.getProperty("btnStartFunction")) || btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender"))) && comboFonctions.getSelectedItem() != "") 
							{
								if (Renamer.frame == null || Renamer.frame != null && Renamer.frame.isVisible() == false)
								{
									Utils.saveSettings(false);
								}
							}
						}
						
					}
					
				} // End sous titres
				
			}
		}, AWTEvent.KEY_EVENT_MASK);

		// Mouse wheel
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			public void eventDispatched(AWTEvent event) {
				
				//Mouse position
				MouseEvent mp = (MouseEvent) event;
								
				//On récupère le groupe qui est le plus haut
				JPanel top;
				
				if (grpH264.isVisible())
					top = grpH264;
				else if (grpResolution.isVisible())
					top = grpResolution;
				else 
					top = grpInAndOut;
				
				if (canScroll 
					&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
					&& frame.getWidth() > 332 && mp.getX() > 332
					&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31
					|| 
					canScroll 
					&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
					&& frame.getWidth() > 332 && mp.getX() > 332
					&& Settings.btnDisableAnimations.isSelected()
					&& top.getY() < 59) 
					{								
						MouseWheelEvent me = (MouseWheelEvent) event;
						int i =  (0 - me.getWheelRotation()) * 20;
						
						//Empêche de faire un scroll vers le bas pour ne pas dépasser la position minimale de top
						if (i < 0 && Settings.btnDisableAnimations.isSelected() && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
							i = 0;
						
						//Pré calcul
						if (top.getY() + i >= grpChooseFiles.getY() && i > 0)
						{
							if (i < grpChooseFiles.getY())
								i = grpChooseFiles.getY() - top.getY();	
							else
								i = 0;
						}

						if (frame.getSize().getHeight() - (btnReset.getLocation().y + i + btnReset.getHeight()) >= 31 && i < 0)						
						{
							if (i < frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()))
								i = (int) (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) - 31);
							else
								i = 0;						
						}
						
						grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + i);
						grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y + i);
						grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + i);
						grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + i);
						grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + i);
						grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + i);
						grpInAndOut.setLocation(grpInAndOut.getLocation().x, grpInAndOut.getLocation().y + i);
						grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + i);
						grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + i);
						grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + i);
						grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + i);
						grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + i);
						grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + i);
						btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + i);						

					}
				
			}
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		// File drop Application
		if (droppedFiles.isEmpty() == false) 
		{			
			for (String file : droppedFiles) 
			{
				File droppedFiles = new File(file);
				if (droppedFiles.isFile())
				{
					int s = droppedFiles.toString().lastIndexOf('.');
					String ext = droppedFiles.toString().substring(s);
					
					if (ext.equals(".enc") == false && droppedFiles.isHidden() == false && droppedFiles.getName().contains("."))
						liste.addElement(droppedFiles.toString());
				} else
					Utils.findFiles(droppedFiles.toString());
			}
			
			addToList.setVisible(false);			
			lblFiles.setText(Utils.filesNumber());
		}
		
		//GPU decoding
		if (System.getProperty("os.name").contains("Windows"))
			FFMPEG.hwaccel("-hwaccels" + '"');
		else
			FFMPEG.hwaccel("-hwaccels");
				
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {}
		} while (FFMPEG.runProcess.isAlive());
						
		new Settings();
		Splash.increment();
				
		YOUTUBEDL.update();
		EXIFTOOL.run(""); //Permet de prélancer l'exécutable
		
		Splash.increment();
		
		Utils.changeFrameVisibility(frame, false);
		topPanel.requestFocus();
		
		if (Settings.btnDisableUpdate.isSelected() == false)
			Update.newVersion();
	}
	
	private void topPanel() {

		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, 1000, 53);

		lblV = new JLabel();
		lblV.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblV.setBounds(289, 31, 64, 16);
		lblV.setText("v" + actualVersion);
		topPanel.add(lblV);

		lblV.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/changelog.html"));
				} catch (IOException | URISyntaxException e) {
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblV.setFont(new Font(freeSansFont, Font.BOLD, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblV.setFont(new Font(freeSansFont, Font.PLAIN, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		settings = new JLabel(new FlatSVGIcon("contents/settings.svg", 13, 13));
		settings.setHorizontalAlignment(SwingConstants.CENTER);
		settings.setBounds(4, 4, 13, 13);
		topPanel.add(settings);
		
		settings.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				settings.setIcon(new FlatSVGIcon("contents/settings_pressed.svg", 13, 13));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept)
				{
					Settings.frame.setVisible(true);							
					Settings.frame.setLocation(Shutter.frame.getLocation().x - Settings.frame.getSize().width -20, Shutter.frame.getLocation().y);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				settings.setIcon(new FlatSVGIcon("contents/settings_hover.svg", 13, 13));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				settings.setIcon(new FlatSVGIcon("contents/settings.svg", 13, 13));
				accept = false;
			}

		});

		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);	
		quit.setBounds(frame.getSize().width - 20, 3, 15, 15);
		topPanel.add(quit);
		
		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));
				if (FFMPEG.isRunning)
					btnCancel.doClick();
				else
					accept = true;
			}
		 	
			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept) {					
					Settings.saveSettings();
					
					Utils.changeFrameVisibility(frame, true);

					Utils.killProcesses();
					
					System.exit(0);
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
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 20, 3, 15, 15);
		topPanel.add(reduce);

		reduce.addMouseListener(new MouseListener() {

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
					if (inputDeviceIsRunning == false 
					&& lblCurrentEncoding.getText().equals(language.getProperty("lblEncodageEnCours")) == false 
					&& lblCurrentEncoding.getText().equals(language.getProperty("processCancelled")) == false
					&& lblCurrentEncoding.getText().equals(language.getProperty("processEnded")) == false)
					{
						new ReducedWindow();
					}
					
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

		help = new JLabel(new FlatSVGIcon("contents/help.svg", 15, 15));
		help.setHorizontalAlignment(SwingConstants.CENTER);
		help.setBounds(reduce.getLocation().x - 20, 3, 15, 15);
		topPanel.add(help);

		help.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("contents/help_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept) {
					if (Help.frame != null) {
						if (Help.frame.isVisible() == false)
							new Help();
					} else
						new Help();
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

		newInstance = new JLabel(new FlatSVGIcon("contents/new.svg", 15, 15));
		newInstance.setHorizontalAlignment(SwingConstants.CENTER);
		newInstance.setBounds(help.getLocation().x - 20, 3, 15, 15);
		newInstance.setToolTipText(language.getProperty("newInstance"));
		topPanel.add(newInstance);

		newInstance.addMouseListener(new MouseListener() {	

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			@SuppressWarnings("unused")
			public void mouseReleased(MouseEvent e) {
				if (accept) {
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					if (frame.getLocation().x == dim.width / 2 - frame.getSize().width / 2
							&& frame.getLocation().y == dim.height / 2 - frame.getSize().height / 2)
						frame.setLocation(frame.getLocation().x - frame.getSize().width - 20, frame.getLocation().y);
					try {
						String newShutter;
						if (System.getProperty("os.name").contains("Windows")) {
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							newShutter = '"' + newShutter.substring(1, newShutter.length()).replace("%20", " ") + '"';
							String[] arguments = new String[] { newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						} else if (System.getProperty("os.name").contains("Mac")) {
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
							newShutter = newShutter.substring(0, newShutter.length() - 1);
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/"))).replace(" ","\\ ");
							String[] arguments = new String[] { "/bin/bash", "-c", "open -n " + newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						} else { //Linux	
							String[] arguments = new String[] { "/bin/bash", "-c", "shutter-encoder"};
							Process proc = new ProcessBuilder(arguments).start();
						}

					} catch (Exception error) {
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new.svg", 15, 15));
				accept = false;
			}

		});

		JLabel panelShutter = new JLabel(language.getProperty("panelShutter"));
		panelShutter.setFont(new Font("Magneto", Font.PLAIN, 26));
		panelShutter.setBounds((320 - panelShutter.getPreferredSize().width) / 2, 0, panelShutter.getPreferredSize().width + 5, 53);
		topPanel.add(panelShutter);
		
		JLabel panelSettings = new JLabel(language.getProperty("panelSettings"));
		panelSettings.setFont(new Font(magnetoFont, Font.PLAIN, 26));
		panelSettings.setBounds(328 + ( (650 - 328) - panelSettings.getPreferredSize().width) / 2, 0, panelSettings.getPreferredSize().width + 5, 53);
		topPanel.add(panelSettings);

		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width,
				topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);
		topImage.setBounds(0, 0, 1000 ,53);

		topPanel.add(topImage);
		frame.getContentPane().add(topPanel);

		topImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent down) {
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;
				
				frame.toFront();
			}
		});

		topImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX,
						MouseInfo.getPointerInfo().getLocation().y - MousePositionY);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}

		});

	}

	@SuppressWarnings("unchecked")
	private void grpChooseFiles() {
			
		fileList = new JList<String>(liste) {
			Image image = new ImageIcon(getClass().getClassLoader().getResource("contents/zebra.jpg")).getImage();
			{
				setOpaque(false);
			}
			
			public void paintComponent(Graphics g) {

				super.paintComponent(g);
				int iw = image.getWidth(this);
				int ih = image.getHeight(this);
				if (iw > 0 && ih > 0) {
					for (int x = 0; x < getWidth(); x += iw) {
						for (int y = 0; y < getHeight(); y += ih) {
							g.drawImage(image, x, y, iw, ih, this);
						}
					}
				}
				super.paintComponent(g);
			}
		};
		fileList.setForeground(Color.BLACK);
		fileList.setCellRenderer(new FilesCellRenderer());
		fileList.setFixedCellHeight(17);
		fileList.setBounds(10, 50, 292, 255);
		fileList.setToolTipText(language.getProperty("rightClick"));
				
		addToList.setText(language.getProperty("dropFilesHere"));
		addToList.setSize(fileList.getSize());
		addToList.setForeground(new Color(150,150,150));
		addToList.setBackground(new Color(0,0,0,0));
		addToList.setFont(new Font(freeSansFont, Font.PLAIN, 16));
		addToList.setHorizontalAlignment(SwingConstants.CENTER);
		addToList.setVerticalAlignment(SwingConstants.CENTER);
		fileList.add(addToList);
				
		grpChooseFiles = new JPanel();
		grpChooseFiles.setLayout(null);
		grpChooseFiles.setBounds(10, 59, 312, 315);
		grpChooseFiles.setBackground(new Color(50, 50, 50));
		grpChooseFiles.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpChooseFiles") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpChooseFiles);

		btnEmptyList = new JButton(language.getProperty("btnEmptyList"));
		btnEmptyList.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnEmptyList.setBounds(124, 21, 82, 21);
		grpChooseFiles.add(btnEmptyList);

		btnEmptyList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Screen record
				if (inputDeviceIsRunning)
					caseDisplay.setSelected(false);
				inputDeviceIsRunning = false;
				
				if (overlayDeviceIsRunning)
				{
					caseLogo.setSelected(false);
					overlayDeviceIsRunning = false;
				}
				
				// Scan
				scan.setText(language.getProperty("menuItemStartScan"));
				scanIsRunning = false;				

				liste.clear();
				addToList.setVisible(true);
				lblFilesEnded.setVisible(false);

				// H264 Paramètres
				lblH264.setVisible(false);
				textH.setText("00");
				textMin.setText("00");
				textSec.setText("00");

				// Lecteur
				VideoPlayer.setMedia();
				
				changeFilters();

				lblFiles.setText(Utils.filesNumber());
			}

		});

		scrollBar = new JScrollPane();
		scrollBar.getViewport().add(fileList);
		scrollBar.setBounds(10, 50, 292, 255);
		scrollBar.setOpaque(false);
		scrollBar.getViewport().setOpaque(false);
		grpChooseFiles.add(scrollBar);

		// Drag & Drop
		fileList.setTransferHandler(new ListeFileTransferHandler());

		fileList.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (liste.getSize() == 0)
				{
					if (inputDeviceIsRunning)
						caseDisplay.setSelected(false);
					inputDeviceIsRunning = false;
					if (overlayDeviceIsRunning)
					{
						caseLogo.setSelected(false);
						overlayDeviceIsRunning = false;
					}
					changeFilters();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (scanIsRunning == false) {
					if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						fileList.setSelectionInterval(0, liste.getSize() - 1);

					if (e.getKeyCode() == 127 && liste.getSize() > 0 || e.getKeyCode() == 8 && liste.getSize() > 0) {
						do {
							liste.remove(fileList.getSelectedIndex());
						} while (fileList.getSelectedIndices().length > 0);
						
						if (liste.getSize() == 0)
							addToList.setVisible(true);
						
						lblFiles.setText(Utils.filesNumber());
						FFPROBE.CalculH264();

						// VideoPlayer
						VideoPlayer.setMedia();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		popupList = new JPopupMenu();
		final JMenuItem numeriser = new JMenuItem(language.getProperty("menuItemNumeriser"));
		menuDisplay = new JMenuItem(language.getProperty("menuItemVisualiser"));
		final JMenuItem blackMagic = new JMenuItem(language.getProperty("menuItemBlackMagic"));

		numeriser.setVisible(false);
		blackMagic.setVisible(false);
				
		// Affichage BlackMagic
		if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
		{
			DECKLINK.run("-f decklink -list_devices 1 -i dummy");
			do {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
				}
			} while (DECKLINK.isRunning);
	
			if (DECKLINK.asBlackMagic) {
				numeriser.setVisible(true);
				blackMagic.setVisible(true);
				DECKLINK.run("-f decklink -list_formats 1 -i " + '"' + DECKLINK.getBlackMagic + '"');
			}
		}

		final JMenuItem silentTrack = new JMenuItem(language.getProperty("menuItemSilentTrack"));
		final JMenuItem menuOpenFolder = new JMenuItem(language.getProperty("menuItemOuvrirDossier"));
		final JMenuItem info = new JMenuItem(language.getProperty("menuItemInfo"));
		final JMenuItem rename = new JMenuItem(language.getProperty("menuItemRename"));
		inputDevice = new JMenuItem(Shutter.language.getProperty("menuItemInputDevice"));
		final JMenuItem arborescence = new JMenuItem(language.getProperty("menuItemArborescence"));
		final JMenuItem tempsTotal = new JMenuItem(language.getProperty("menuItemTempsTotal"));
		final JMenuItem poids = new JMenuItem(language.getProperty("menuItemPoids"));
		final JMenuItem gop = new JMenuItem(language.getProperty("menuItemGop"));
		final JMenuItem ftp = new JMenuItem(language.getProperty("menuItemFtp"));
		final JMenuItem zip = new JMenuItem(language.getProperty("menuItemZip"));
		final JMenuItem unzip = new JMenuItem(language.getProperty("menuItemUnzip"));

		silentTrack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				liste.addElement(" -f lavfi -i anullsrc=r=" + lbl48k.getText() + ":cl=mono");
			}
		});

		numeriser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (BlackMagicInput.frame != null) {
					if (BlackMagicInput.frame.isVisible() == false)
						new BlackMagicInput();
				} else
					new BlackMagicInput();
			}
		});

		menuDisplay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				FFMPEG.toSDL(false);
			}
		});

		blackMagic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (BlackMagicOutput.frame != null) {
					if (BlackMagicOutput.frame.isVisible() == false)
						new BlackMagicOutput();
				} else
					new BlackMagicOutput();
			}
		});

		menuOpenFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.getProperty("os.name").contains("Mac")) 
				{
					try {
						Runtime.getRuntime().exec(new String[]{"/usr/bin/open", "-R", fileList.getSelectedValue()});
					} catch (Exception e2){}
				}
				else if (System.getProperty("os.name").contains("Linux"))
				{
					try {
						Desktop.getDesktop().open(new File(fileList.getSelectedValue()).getParentFile());
					} catch (Exception e2){}
				}
				else //Windows
				{
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + fileList.getSelectedValue());
					} catch (IOException e1) {}
				}
			}
		});

		tempsTotal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int dureeTotale = 0;
				for (String file : fileList.getSelectedValuesList()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
					dureeTotale += FFPROBE.totalLength;
				}
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				// Formatage
				int h = (dureeTotale / 3600000);
				int m = (dureeTotale / 60000) % 60;
				int s = (dureeTotale) / 1000 % 60;
				int f = (int) (dureeTotale / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS);

				String dureeFinale;
				if (h > 0)
					dureeFinale = h + "h " + m + "min " + s + "sec " + f + "i";
				else if (m > 0)
					dureeFinale = m + "min " + s + "sec " + f + "i";
				else
					dureeFinale = s + "sec " + f + "i";

				JOptionPane.showMessageDialog(frame,
						fileList.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + language.getProperty("totalTime")
								+ " " + dureeFinale,
						language.getProperty("totalTimeFiles"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		poids.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int dureeTotale = 0;
				for (String file : fileList.getSelectedValuesList()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning == true);
					dureeTotale += FFPROBE.totalLength;
				}
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				int poidsFinal = 0;
				String codec = "";
				int debit = 0;
				switch (comboFonctions.getSelectedItem().toString()) {
				case "DNxHD":
					codec = "DNxHD " + comboFilter.getSelectedItem().toString();
					debit = Integer.parseInt(comboFilter.getSelectedItem().toString().replace(" X", ""));
					break;
				case "Apple ProRes":
					codec = "Apple ProRes " + comboFilter.getSelectedItem().toString();
					switch (comboFilter.getSelectedItem().toString()) {
					case "Proxy":
						debit = (int) ((float) 1.52 * FFPROBE.currentFPS);
						break;
					case "LT":
						debit = (int) ((float) 3.4 * FFPROBE.currentFPS);
						break;
					case "422":
						debit = (int) ((float) 4.88 * FFPROBE.currentFPS);
						break;
					case "422 HQ":
						debit = (int) ((float) 7.4 * FFPROBE.currentFPS);
						break;
					case "444":
						debit = (int) ((float) 11 * FFPROBE.currentFPS);
						break;
					case "4444":
						debit = (int) ((float) 11 * FFPROBE.currentFPS);
						break;
					case "4444 XQ":
						debit = (int) ((float) 16.5 * FFPROBE.currentFPS);
						break;
					}
					break;
				case "DNxHR":
					Object[] options = { "4K", "UHD", "2K", "HD" };
					int q = JOptionPane.showOptionDialog(frame, language.getProperty("chooseResolution"),
							language.getProperty("resolution"), JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					int resolution = 4096;
					switch (q) {
					case 1:
						resolution = 3840;
						break;
					case 2:
						resolution = 2048;
						break;
					case 3:
						resolution = 1920;
						break;
					}

					codec = "DNxHR " + comboFilter.getSelectedItem().toString();
					switch (comboFilter.getSelectedItem().toString()) {
					case "LB":
						switch (resolution) {
						case 4096:
							debit = (int) ((float) 0.7616 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							debit = (int) ((float) 0.7148 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							debit = (int) ((float) 0.1916 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							debit = (int) ((float) 0.1796 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "SQ":
						switch (resolution) {
						case 4096:
							debit = (int) ((float) 2.4492 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							debit = (int) ((float) 2.2968 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							debit = (int) ((float) 0.6132 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							debit = (int) ((float) 0.5744 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "HQ":
					case "HQX":
						switch (resolution) {
						case 4096:
							debit = (int) ((float) 3.7072 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							debit = (int) ((float) 3.4728 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							debit = (int) ((float) 0.9256 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							debit = (int) ((float) 0.8672 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "444":
						switch (resolution) {
						case 4096:
							debit = (int) ((float) 7.41 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							debit = (int) ((float) 6.9492 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							debit = (int) ((float) 1.8516 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							debit = (int) ((float) 1.7384 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					}
					break;
				}

				poidsFinal = (int) (((float) dureeTotale / 1000) * ((float) debit / 8));

				String taille;
				if (poidsFinal >= 1000)
					taille = String.valueOf(((float) poidsFinal / 1024)).substring(0, 4) + " Go";
				else
					taille = poidsFinal + " Mo";

				JOptionPane.showMessageDialog(frame,
						fileList.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + taille + " "
								+ language.getProperty("to") + " " + codec,
						language.getProperty("approximativeWeight"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Informations.frame == null)
					new Informations();
				else
					Informations.frame.setVisible(true);
				if (Informations.infoTabbedPane.getTabCount() == 0)
					Utils.changeFrameVisibility(Informations.frame, false);

				for (String item : fileList.getSelectedValuesList()) 
				{
					MEDIAINFO.run("--Output=HTML " + '"' + item + '"', item);					
				}
			}
		});

		rename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {}
				new Renamer();
				frame.setOpacity(1.0f);

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
		
		inputDevice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {		
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				Thread checkDevices = new Thread(new Runnable() {

					@Override
					public void run() {
					//list devices		
						if (System.getProperty("os.name").contains("Mac"))
						{				
							FFMPEG.devices("-f avfoundation -list_devices true -i dummy");	
						}
						else if (System.getProperty("os.name").contains("Windows"))
						{
							FFMPEG.devices("-f dshow -list_devices true -i dummy" + '"');
						}
						else //Linux
						{
							FFMPEG.videoDevices = new StringBuilder();
							FFMPEG.videoDevices.append(language.getProperty("noVideo"));
							
							FFMPEG.audioDevices = new StringBuilder();
							FFMPEG.audioDevices.append(language.getProperty("noAudio"));
						}
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFMPEG.isRunning);
						
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
						new RecordInputDevice();	
					}
				});
				checkDevices.start();

			}			
		});
		
		arborescence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File source = null;
				if (System.getProperty("os.name").contains("Mac")) {
					FileDialog dialog = new FileDialog(frame, language.getProperty("chooseFolderToCopy"), FileDialog.LOAD);
					
					if (fileList.getSelectedIndices().length > 0)
						dialog.setDirectory(new File(fileList.getSelectedValue().toString()).getParent());
					else	
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
					
					dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
					dialog.setAlwaysOnTop(true);
					System.setProperty("apple.awt.fileDialogForDirectories", "true");
					dialog.setVisible(true);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					if (dialog.getDirectory() != null)
						source = new File(dialog.getDirectory() + dialog.getFile());
				} else if (System.getProperty("os.name").contains("Linux")) {
					JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
					dialog.setDialogTitle(language.getProperty("chooseDestinationFolder"));
					dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
						dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
					else
						dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

					int result = dialog.showOpenDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION) 
		               source = new File(dialog.getSelectedFile().toString());				   
				} else {
					Shell shell = new Shell(SWT.ON_TOP);

					shell.setSize(frame.getSize().width, frame.getSize().height);
					shell.setLocation(frame.getLocation().x, frame.getLocation().y);
					shell.setAlpha(0);
					shell.open();

					DirectoryDialog dialog = new DirectoryDialog(shell);
					dialog.setText(language.getProperty("chooseFolderToCopy"));
					if (fileList.getSelectedIndices().length > 0)
						dialog.setFilterPath(new File(fileList.getSelectedValue().toString()).getParent());
					else						
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

					try {
						source = new File(dialog.open());
					} catch (Exception e1) {}

					shell.dispose();
				}

				File destination = null;
				if (source != null) {
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);

						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();

						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(language.getProperty("chooseDestinationFolder"));
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}

						shell.dispose();
					}
										
					if (destination != null) {
						if (source.toString().equals(destination.toString()))
							JOptionPane.showMessageDialog(frame, language.getProperty("sameFolders"),
									language.getProperty("copyError"), JOptionPane.ERROR_MESSAGE);
						else {
							try {
								if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux")) {
									ProcessBuilder rsync = new ProcessBuilder("/bin/bash", "-c",
											"rsync -a  " + source.toString().replace(" ", "\\ ") + "/ "
													+ destination.toString().replace(" ", "\\ ")
													+ " --include \\*/ --exclude \\*");
									rsync.start();
								} else
									Runtime.getRuntime().exec("cmd /c xcopy /t /e " + '"' + source.toString() + '"'
											+ " " + '"' + destination.toString() + '"');
							} catch (IOException e1) {
							}

							JOptionPane.showMessageDialog(frame, language.getProperty("copyFinished"),
									language.getProperty("treeCopy"), JOptionPane.INFORMATION_MESSAGE);
						}
					}

				}
			}
		});

		gop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {}
				new GOP();
				frame.setOpacity(1.0f);
			}

		});

		ftp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {}
				new Ftp();				
				frame.setOpacity(1.0f);
			}
		});

		zip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				FileDialog dialog = new FileDialog(frame, language.getProperty("saveZip"), FileDialog.SAVE);
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
				else
					dialog.setDirectory(System.getProperty("user.home") + "\\Desktop");
				dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);

				if (dialog.getFile() != null) {
					lblCurrentEncoding.setText(language.getProperty("compression"));
					lblDestination1.setText(dialog.getDirectory());
					progressBar1.setIndeterminate(true);

					StringBuilder items = new StringBuilder();
					for (String item : fileList.getSelectedValuesList()) {
						items.append(" " + '"' + item + '"');
					}

					disableAll();
					SEVENZIP.run("a " + '"' + dialog.getDirectory() + dialog.getFile().replace(".zip", "") + ".zip"
							+ '"' + items, true);
				}

			}
		});

		unzip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fichier = new File(fileList.getSelectedValue());
				String path = fichier.getParentFile() + "/"
						+ fichier.getName().substring(0, fichier.getName().toString().lastIndexOf("."));

				lblCurrentEncoding.setText(language.getProperty("decompression"));
				lblDestination1.setText(path);
				progressBar1.setIndeterminate(true);

				disableAll();
				SEVENZIP.run("e " + '"' + fichier.toString() + '"' + " -y -o" + '"' + path + '"', true);
			}
		});

		scanListe = new JPopupMenu();
		scan = new JMenuItem(language.getProperty("menuItemStartScan"));

		scan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scan.getText().equals(language.getProperty("menuItemStartScan"))) {
					
					File destination = null;
					if (liste.getSize() > 0)
					{
						if (fileList.getSelectedIndices().length > 0)
							destination = new File(new File(fileList.getSelectedValue()).getParent());							
						else
							destination = new File(new File(liste.getElementAt(0)).getParent());
									
						liste.clear();
						
						if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
							liste.add(0, destination + "/");
						else
							liste.add(0, destination + "\\");
						
						scan.setText(language.getProperty("menuItemStopScan"));
						if (lblDestination1.getText().equals(language.getProperty("sameAsSource")))
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);
						else
							scanIsRunning = true;
					}
					else
					{
						scan.setText(language.getProperty("menuItemStopScan"));
						JOptionPane.showMessageDialog(frame, language.getProperty("dragFolderToList"), language.getProperty("chooseScanFolder"), JOptionPane.INFORMATION_MESSAGE);						
					}
					
				} else {
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;
				}
				lblFiles.setText(Utils.filesNumber());
			}
		});

		fileList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
						&& TSMUXER.isRunning == false && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
						&& liste.getSize() > 0)
					menuDisplay.doClick();

				if (e.getButton() == MouseEvent.BUTTON3 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getButton() == MouseEvent.BUTTON1)
				{
					if (inputDeviceIsRunning)
					{
						popupList.removeAll();
						popupList.add(menuDisplay);
						popupList.show(fileList, e.getX() - 30, e.getY());
					}
					else if (fileList.getSelectedIndices().length > 0 && scan.getText().equals(language.getProperty("menuItemStartScan")))
					{
						if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
								&& TSMUXER.isRunning == false) {
							// Ajout à la liste
							popupList.removeAll();
							if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")))
							popupList.add(silentTrack);
							popupList.add(menuDisplay);
							popupList.add(blackMagic);
							popupList.add(menuOpenFolder);
							popupList.add(scan);
							popupList.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupList.add(poids);
								break;
							}
							popupList.add(info);
							popupList.add(rename);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(gop);
							popupList.add(ftp);
							popupList.add(zip);
							popupList.add(unzip);

							// Décompression d'archives
							String firstElement = fileList.getSelectedValue();
							if (firstElement.contains(".zip") || firstElement.contains(".rar") || firstElement.contains(".7z") || firstElement.contains(".iso")
							 || firstElement.contains(".tar") || firstElement.contains(".xz") || firstElement.contains(".bz2") || firstElement.contains(".bzip2")
							 || firstElement.contains(".tbz2") || firstElement.contains(".tbz") || firstElement.contains(".gz") || firstElement.contains(".gzip")
							 || firstElement.contains(".tgz") || firstElement.contains(".lzma") || firstElement.contains(".xar") || firstElement.contains(".taz")) {
								if (fileList.getSelectedIndices().length == 1)
									unzip.setVisible(true);
								else
									unzip.setVisible(false);

								tempsTotal.setVisible(false);
								gop.setVisible(false);
							} else {
								tempsTotal.setVisible(true);
								gop.setVisible(true);
								unzip.setVisible(false);
							}

							File fileOrDirectory = new File(fileList.getSelectedValue());

							if (fileOrDirectory.isFile())
								popupList.show(fileList, e.getX() - 30, e.getY());
							else
								scanListe.show(fileList, e.getX() - 30, e.getY());
						} else {
							// Ajout à la liste
							popupList.removeAll();
							popupList.add(menuOpenFolder);
							popupList.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupList.add(poids);
								break;
							}
							popupList.add(info);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(gop);
							popupList.show(fileList, e.getX() - 30, e.getY());
						}

					} else {
						scanListe.removeAll();
						scanListe.add(numeriser);
						scanListe.add(scan);
						scanListe.add(inputDevice);
						scanListe.add(arborescence);
						scanListe.show(fileList, e.getX() - 30, e.getY());
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 0));
			}

		});

		fileList.addMouseMotionListener(new MouseMotionListener() {

			int anchor = -1;

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if (anchor == -1)
					anchor = fileList.getSelectedIndex();

				fileList.setSelectionInterval(anchor, fileList.getSelectedIndex());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				anchor = -1;

			}

		});

		lblFilesEnded = new JLabel(language.getProperty("lblTermine"));
		lblFilesEnded.setFont(new Font(montserratFont, Font.PLAIN, 13));
		lblFilesEnded.setForeground(Utils.themeColor);
		lblFilesEnded.setVisible(false);
		lblFilesEnded.setBounds(213, 15, 94, 16);
		grpChooseFiles.add(lblFilesEnded);

		lblFiles = new JLabel(Utils.filesNumber());
		lblFiles.setForeground(Color.WHITE);
		lblFiles.setFont(new Font(montserratFont, Font.PLAIN, 13));
		lblFiles.setBounds(213, 30, 83, 16);
		grpChooseFiles.add(lblFiles);

		btnBrowse = new JButton(language.getProperty("btnBrowse"));
		btnBrowse.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnBrowse.setBounds(8, 21, 113, 21);
		grpChooseFiles.add(btnBrowse);

		btnBrowse.addActionListener(new ActionListener() {

			String defaultFolder = "";
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog dialog = new FileDialog(frame, language.getProperty("import"), FileDialog.LOAD);
				if (defaultFolder == "")
				{
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
					else
						dialog.setDirectory(System.getProperty("user.home") + "\\Desktop");
				}
				dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
				dialog.setAlwaysOnTop(true);
				dialog.setMultipleMode(true);
				dialog.setVisible(true);

				if (dialog.getFiles() != null) {
					File[] files = dialog.getFiles();
					for (int i = 0; i < files.length; i++) {
						int s = files[i].getAbsolutePath().toString().lastIndexOf('.');
						String ext = files[i].getAbsolutePath().toString().substring(s);
						if (ext.equals(".enc")) {
							Utils.loadSettings(files[i]);
						}
						
						File file = files[i];
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getAbsolutePath().toString().contains("\"") || file.getAbsolutePath().toString().contains("\'") || file.getName().contains("/") || file.getName().contains("\\"))
						{
							Object[] options = { Shutter.language.getProperty("btnAdd"), Shutter.language.getProperty("btnNext"), Shutter.language.getProperty("btnCancel") };
							
							int q = JOptionPane.showOptionDialog(Shutter.frame, file.getAbsoluteFile().toString() + System.lineSeparator() + Shutter.language.getProperty("invalidCharacter"), Shutter.language.getProperty("import"),
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
												
							if (q == 0) //OK
							{
								liste.addElement(file.getAbsolutePath());
								addToList.setVisible(false);
								lblFiles.setText(Utils.filesNumber());
							}
							else if (q == 1) //Next
								continue;
							else if (q == 2) //Cancel
								break;
						}
						else
						{
							liste.addElement(file.getAbsolutePath());
							addToList.setVisible(false);
							lblFiles.setText(Utils.filesNumber());
						}
					}

					changeFilters();

					switch (comboFonctions.getSelectedItem().toString()) {
					case "H.264":
					case "H.265":
					case "WMV":
					case "MPEG":
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
					
					// VideoPlayer
					VideoPlayer.setMedia();
					
					defaultFolder = dialog.getParent().toString();
				}

			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void grpChooseFunction() {

		grpChooseFunction = new JPanel();
		grpChooseFunction.setLayout(null);
		grpChooseFunction.setBounds(10, 380, 312, 76);
		grpChooseFunction.setBackground(new Color(50, 50, 50));
		grpChooseFunction.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpChooseFunction") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpChooseFunction);

		btnCancel = new JButton(language.getProperty("btnCancel"));
		btnCancel.setEnabled(false);
		btnCancel.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnCancel.setMargin(new Insets(0,0,0,0));
		btnCancel.setBounds(207, 46, 97, 21);
		grpChooseFunction.add(btnCancel);

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (FFMPEG.runProcess != null) {
					if (FFMPEG.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							FFMPEG.isRunning = false;

							if (btnStart.getText().equals(language.getProperty("resume")))
								FFMPEG.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le
														// détruire

							try {
								FFMPEG.writer.write('q');
								FFMPEG.writer.flush();
								FFMPEG.writer.close();
							} catch (IOException er) {
							}
							
							if (comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false)
								FFMPEG.process.destroy();

							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e1) {
								}
							} while (FFMPEG.runProcess.isAlive());
						}
					}
				}
				if (DCRAW.runProcess != null) {
					if (DCRAW.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							DCRAW.process.destroy();
						}
					} // End if
				}
				if (XPDF.runProcess != null) {
					if (XPDF.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							XPDF.process.destroyForcibly();
						}
					} // End if
				}
				if (MKVMERGE.runProcess != null) {
					if (MKVMERGE.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							MKVMERGE.process.destroyForcibly();
						}
					} // End if
				}
				if (YOUTUBEDL.runProcess != null) {
					if (YOUTUBEDL.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							YOUTUBEDL.process.destroy();
						}
					}
				}
				if (BMXTRANSWRAP.runProcess != null) {
					if (BMXTRANSWRAP.runProcess.isAlive()) {
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
								language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION) {
							cancelled = true;
							BMXTRANSWRAP.process.destroy();
						}
					}
				}
				if (copyFileIsRunning)
				{
					int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("areYouSure"),
							language.getProperty("stopProcess"), JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (reply == JOptionPane.YES_OPTION)
						cancelled = true;
				}
				if (scanIsRunning) {
					cancelled = true;
					enableAll();
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;
				}
				if (Ftp.isRunning) {
					cancelled = true;
					try {
						Ftp.ftp.abort();
					} catch (IOException e1) {
						System.out.println(e1);
					}
				}
				if (Wetransfer.isRunning) {
					cancelled = true;
					try {
						Wetransfer.process.destroy();
					} catch (Exception e1) {
						System.out.println(e1);
					}
				}
				if (Settings.btnWaitFileComplete.isSelected())
					cancelled = true;

				progressBar1.setValue(0);
			}
		});

		iconList = new JLabel(new FlatSVGIcon("contents/list.svg", 15, 15));
		iconList.setHorizontalAlignment(SwingConstants.CENTER);
		iconList.setVisible(false);
		iconList.setBounds(180, 46, 21, 21);
		grpChooseFunction.add(iconList);

		iconList.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("contents/list_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept && btnStart.getText().equals(language.getProperty("btnStartFunction")))
				{
					iconList.setVisible(false);
					btnCancel.setBounds(207, 46, 97, 21);
					
					if (iconPresets.isVisible())
					{
						iconPresets.setBounds(180, 45, 21, 21);
						btnCancel.setBounds(207, 46, 97, 21);
					}
					else
						btnCancel.setBounds(184, 46, 120, 21);
					
					
					btnStart.setText(language.getProperty("btnAddToRender"));
					if (RenderQueue.frame == null)
						new RenderQueue();
					else
						Utils.changeFrameVisibility(RenderQueue.frame, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("contents/list_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("contents/list.svg", 15, 15));
				accept = false;
			}

		});
				
		iconPresets = new JLabel(new FlatSVGIcon("contents/presets.svg", 15, 15));
		iconPresets.setHorizontalAlignment(SwingConstants.CENTER);
		iconPresets.setVisible(true);
		iconPresets.setBounds(180, 45, 21, 21);
		grpChooseFunction.add(iconPresets);

		iconPresets.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("contents/presets_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept)
				{
					iconPresets.setVisible(false);
					if (iconList.isVisible())
						btnCancel.setBounds(207, 46, 97, 21);
					else
						btnCancel.setBounds(184, 46, 120, 21);
					
					if (Functions.frame == null)
						new Functions();
					else {
						if (Functions.listeDeFonctions.getModel().getSize() > 0) {
							Functions.lblSave.setVisible(false);
							Functions.lblDrop.setVisible(false);
						}

						Functions.frame.setVisible(true);
					}
					iconPresets.setIcon(new FlatSVGIcon("contents/presets.svg", 15, 15));
					Utils.changeFrameVisibility(Functions.frame, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("contents/presets_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("contents/presets.svg", 15, 15));
				accept = false;
			}

		});
			
		
		btnStart = new JButton(language.getProperty("btnStartFunction"));
		btnStart.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnStart.setMargin(new Insets(0,0,0,0));
		btnStart.setBounds(8, 46, 168, 21);
		grpChooseFunction.add(btnStart);

		btnStart.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				
				FunctionUtils.yesToAll = false;
				FunctionUtils.noToAll = false;
				
				if ((btnStart.getText().equals(language.getProperty("btnStartFunction"))
				|| btnStart.getText().equals(language.getProperty("btnAddToRender"))) && liste.getSize() > 0)
				{					
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
					
					grpDestination.setSelectedIndex(0);
					FFMPEG.error = false;
					
					//Temps écoulé
					tempsEcoule.setVisible(false);
					FFMPEG.elapsedTime = 0;
					FFMPEG.previousElapsedTime = 0;

					if (btnStart.getText().equals(language.getProperty("btnAddToRender")))
						RenderQueue.btnStartRender.setEnabled(true);					
					
					// Command directe FFMPEG
					if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")) {
						if (comboFilter.getEditor().getItem().toString().equals(language.getProperty("aucun"))
								|| comboFilter.getEditor().getItem().toString().equals("")
								|| comboFilter.getEditor().getItem().toString().equals(" ")
								|| comboFilter.getEditor().getItem().toString().contains(".") == false)
							JOptionPane.showMessageDialog(frame, language.getProperty("chooseExtension"),
									language.getProperty("extensionError"), JOptionPane.INFORMATION_MESSAGE);
						else
							Command.main();
					} else {

						//Scan dossier
						if(scan.getText().equals(language.getProperty("menuItemStopScan")) && scanIsRunning == false)
								JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);
						else
						{
							String fonction = comboFonctions.getSelectedItem().toString();
							if (language.getProperty("functionCut").equals(fonction)) 
							{
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (caseInAndOut.isSelected() || caseSetTimecode.isSelected())
									Rewrap.main();
								else {
									JOptionPane.showMessageDialog(frame, language.getProperty("chooseInOutPoint"),
											language.getProperty("noInOuPoint"), JOptionPane.INFORMATION_MESSAGE);
									caseInAndOut.setSelected(true);
									new VideoPlayer();									
								}	
							}
							else if ("WAV".equals(fonction)
									|| "MP3".equals(fonction)
									|| "AC3".equals(fonction)
									|| "OPUS".equals(fonction)
									|| "OGG".equals(fonction)
									|| "AIFF".equals(fonction)
									|| "FLAC".equals(fonction)
									|| "AAC".equals(fonction))
							{
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									AudioEncoders.main();
								
							} else if ("Loudness & True Peak".equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									LoudnessTruePeak.main();
							} else if (language.getProperty("functionMerge").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									Merge.main();
							} else if (language.getProperty("functionExtract").equals(fonction)) { 
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("setAll")))
									Extract.extractAll();
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
									Extract.extractAudio();
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
									Extract.extractSubs();
								else
									Extract.main();
							} else if (language.getProperty("functionConform").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									Conform.main();				
							} else if (language.getProperty("functionInsert").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									VideoInserts.main();
							} else if (language.getProperty("functionReplaceAudio").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
								{
									if (liste.getSize() < 2)
									{
										if (caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")))
											ReplaceAudio.main();
										else
											JOptionPane.showMessageDialog(frame, language.getProperty("replaceAudioMissing"), language.getProperty("missingElement"), JOptionPane.ERROR_MESSAGE);
									}
									else
										ReplaceAudio.main();
								}
							} else if (language.getProperty("functionSubtitles").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									caseInAndOut.doClick();
									Utils.changeFrameVisibility(frame, true);
								}
							} else if (language.getProperty("functionNormalization").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									AudioNormalization.main();
							} else if (language.getProperty("functionSceneDetection").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									if (SceneDetection.frame == null)
										new SceneDetection(true);
									else {
										Utils.changeFrameVisibility(SceneDetection.frame, false);
										SceneDetection.btnAnalyse.doClick();
									}
								}
							} else if (language.getProperty("functionBlackDetection").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									BlackDetection.main();
							} else if (language.getProperty("functionOfflineDetection").equals(fonction)) {
								
								Object[] options = {"Avid", "Davinci", "Premiere", "Custom"};
								
								int NLE = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSoftware"), language.getProperty("functionOfflineDetection"),
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
								    options,
								    options[0]);
								
								URL sourceFile = getClass().getResource("/contents/avid.png");
						        File destinationFile = new File(dirTemp + "offline.png");
						        			
								switch (NLE) 
								{
									case 0:
										FFMPEG.mseSensibility = 150f;
										break;
									case 1:
										FFMPEG.mseSensibility = 150f;
										sourceFile = getClass().getResource("/contents/davinci.png");
										break;
									case 2:
										FFMPEG.mseSensibility = 800f;
										sourceFile = getClass().getResource("/contents/premiere.png");
										break;
									case 3:
										FFMPEG.mseSensibility = 800f;
										FileDialog dialog = new FileDialog(frame, "Custom", FileDialog.LOAD);
										dialog.setDirectory(new File(liste.elementAt(0).toString()).getParent());
										dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
										dialog.setAlwaysOnTop(true);
										dialog.setMultipleMode(false);
										dialog.setVisible(true);

										if (dialog.getFile() != null)
										{
											try {
												sourceFile = new File(dialog.getDirectory() + dialog.getFile().toString()).toURL();
											} catch (MalformedURLException e2) {}
										}

										break;
								}
								
								if (destinationFile.exists())
									destinationFile.delete();
								
						        try {
						            FileUtils.copyURLToFile(sourceFile, destinationFile);
						        } catch (Exception e1) {}

						        if (destinationFile.exists())
						        	OfflineDetection.main();
						        
							} else if ("DNxHD".equals(fonction)
									|| "DNxHR".equals(fonction)
									|| "Apple ProRes".equals(fonction)
									|| "GoPro CineForm".equals(fonction)
									|| "QT Animation".equals(fonction)
									|| "Uncompressed YUV".equals(fonction)
									|| "H.264".equals(fonction)
									|| "H.265".equals(fonction)
									|| "WMV".equals(fonction)
									|| "MPEG".equals(fonction)
									|| "VP8".equals(fonction)
									|| "VP9".equals(fonction)
									|| "AV1".equals(fonction)
									|| "OGV".equals(fonction)
									|| "MJPEG".equals(fonction)
									|| "Xvid".equals(fonction)
									|| "XDCAM HD422".equals(fonction)
									|| "AVC-Intra 100".equals(fonction)
									|| "XAVC".equals(fonction)
									|| "HAP".equals(fonction)
									|| "FFV1".equals(fonction)
									|| "DV PAL".equals(fonction))
							{
									VideoEncoders.main(true);
									
							} else if ("DVD".equals(fonction)) {								
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									VideoEncoders.main(true);
							} else if ("Blu-ray".equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									VideoEncoders.main(true);
							} else if (language.getProperty("functionPicture").equals(fonction) || "JPEG".equals(fonction)) {
									Picture.main(true);
							} else if (language.getProperty("functionRewrap").equals(fonction)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (comboFilter.getEditor().getItem().toString().equals(language.getProperty("aucun"))
										|| comboFilter.getEditor().getItem().toString().equals("")
										|| comboFilter.getEditor().getItem().toString().equals(" ")
										|| comboFilter.getEditor().getItem().toString().contains(".") == false)
									JOptionPane.showMessageDialog(frame, language.getProperty("chooseExtension"),
											language.getProperty("extensionError"), JOptionPane.INFORMATION_MESSAGE);
								else
									Rewrap.main();
							}
						}
					}
				}
				else 
				{ // Fonctions n'ayant pas de fichiers dans la liste
					if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) {
							try {
								frame.setOpacity(0.5f);
							} catch (Exception er) {}
							new VideoWeb();
							frame.setOpacity(1.0f);
					} else if (comboFonctions.getSelectedItem().equals("DVD Rip")
							&& btnStart.getText().equals(language.getProperty("btnStartFunction"))) {
						if (inputDeviceIsRunning)
							JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
						else if (scanIsRunning)
							JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
									language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
						else {
							disableAll();
							DVDRIP.main();
						}
					}
					if (btnStart.getText().equals(language.getProperty("btnPauseFunction"))) {
						caseRunInBackground.setEnabled(false);
						caseRunInBackground.setSelected(false);

						if (caseDisplay.isSelected() == false) {
							FFMPEG.suspendProcess();
							btnStart.setText(language.getProperty("btnResumeFunction"));
						} else
							JOptionPane.showConfirmDialog(frame, language.getProperty("useBarSpace"), language.getProperty("btnPauseFunction"), JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);

						tempsRestant.setText(language.getProperty("timePause"));
					} 
					else if (btnStart.getText().equals(language.getProperty("btnResumeFunction"))) {
						caseRunInBackground.setEnabled(true);

						FFMPEG.resumeProcess();

						btnStart.setText(language.getProperty("btnPauseFunction"));
					}
					else if (btnStart.getText().equals(language.getProperty("btnStopRecording")))
					{
						if (FFMPEG.runProcess != null)
						{							
							if (FFMPEG.runProcess.isAlive()) {

								try {
									FFMPEG.writer.write('q');
									FFMPEG.writer.flush();
									FFMPEG.writer.close();
								} catch (IOException er) {
								}
								
								FFMPEG.process.destroy();

								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e1) {
									}
								} while (FFMPEG.runProcess.isAlive());
							}
						}
						
						btnStart.setText(language.getProperty("btnStartFunction"));
					}
				}
			}
		});
		
		String items[] = { 
				language.getProperty("itemNoConversion"),
				language.getProperty("functionCut"),
				language.getProperty("functionReplaceAudio"),
				language.getProperty("functionRewrap"),	
				language.getProperty("functionConform"),			
				language.getProperty("functionMerge"),		
				language.getProperty("functionExtract"),
				language.getProperty("functionSubtitles"),
				language.getProperty("functionInsert"),
				
				language.getProperty("itemAudioConversion"), "WAV", "AIFF", "FLAC", "MP3", "AAC", "AC3", "OPUS", "OGG",
				
				language.getProperty("itemEditingCodecs"), "DNxHD", "DNxHR", "Apple ProRes", "QT Animation", "GoPro CineForm" ,"Uncompressed YUV",
				
				language.getProperty("itemOuputCodecs"), "H.264", "H.265", "VP8", "VP9", "AV1", "OGV",		
				
				language.getProperty("itemBroadcastCodecs"), "XDCAM HD422", "AVC-Intra 100", "XAVC", "HAP",
				
				language.getProperty("itemOldCodecs"), "DV PAL", "MJPEG", "Xvid", "WMV", "MPEG",
				
				language.getProperty("itemArchiveCodecs"), "FFV1",		
				
				language.getProperty("itemImage"),"JPEG",language.getProperty("functionPicture"), 
				
				language.getProperty("itemBurnRip"), "DVD", "Blu-ray", "DVD Rip",
				
				language.getProperty("itemAnalyze"), "Loudness & True Peak",
				language.getProperty("functionNormalization"),
				language.getProperty("functionSceneDetection"),
				language.getProperty("functionBlackDetection"),
				language.getProperty("functionOfflineDetection"),
				
				language.getProperty("itemDownload"),
				language.getProperty("functionWeb")
				
		};
		
		/*
		int itemsLength = fonctions.length;
		if (System.getProperty("os.name").contains("Linux") == false || new File("/usr/local/bin/youtube-dl").exists())
			itemsLength += 2;
			
		final String[] items = new String[itemsLength];
		
		int i = 0;
		for (String item : fonctions)
		{
			items[i] = item;
			i++;
		}
				
		if (System.getProperty("os.name").contains("Linux") == false || new File("/usr/local/bin/youtube-dl").exists())
		{
			items[i] = language.getProperty("itemDownload");
			items[i+1] = language.getProperty("functionWeb");		
		}*/

		comboFonctions = new JComboBox<String[]>();
		comboFonctions.setName("comboFonctions");
		comboFonctions.setModel(new DefaultComboBoxModel(items));				
		comboFonctions.setSelectedItem(null);		
		comboFonctions.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFonctions.setEditable(true);
		comboFonctions.setMaximumRowCount(Toolkit.getDefaultToolkit().getScreenSize().height / 33);
		comboFonctions.setBounds(8, 19, 168, 22);
		comboFonctions.getModel().setSelectedItem("");
		comboFonctions.setRenderer(new ComboBoxRenderer());
		grpChooseFunction.add(comboFonctions);

		comboFonctions.addActionListener(new ActionListener() {
			
			ArrayList<String> newList = new ArrayList<String>();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboFonctions.getSelectedItem().equals(language.getProperty("itemNoConversion")))
				{
					newList.clear();
					
					newList.add(language.getProperty("functionCut"));
					newList.add(language.getProperty("functionReplaceAudio"));
					newList.add(language.getProperty("functionRewrap"));
					newList.add(language.getProperty("functionConform"));			
					newList.add(language.getProperty("functionMerge"));	
					newList.add(language.getProperty("functionExtract"));
					newList.add(language.getProperty("functionSubtitles"));
					newList.add(language.getProperty("functionInsert"));
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemAudioConversion")))
				{
					newList.clear();

					newList.add("WAV");
					newList.add("AIFF");
					newList.add("FLAC");
					newList.add("MP3");
					newList.add("AAC");
					newList.add("AC3");
					newList.add("OPUS");
					newList.add("OGG");
					
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemEditingCodecs")))
				{
					newList.clear();
					
					newList.add("DNxHD");
					newList.add("DNxHR");
					newList.add("Apple ProRes");
					newList.add("QT Animation");
					newList.add("GoPro CineForm");
					newList.add("Uncompressed YUV");
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemOuputCodecs")))
				{
					newList.clear();
					
					newList.add("H.264");
					newList.add("H.265");
					newList.add("VP8");
					newList.add("VP9");
					newList.add("AV1");
					newList.add("OGV");

				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemBroadcastCodecs")))
				{
					newList.clear();
					
					newList.add("XDCAM HD422");
					newList.add("AVC-Intra 100");
					newList.add("XAVC");
					newList.add("HAP");

				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemOldCodecs")))
				{
					newList.clear();
					
					newList.add("DV PAL");
					newList.add("MJPEG");
					newList.add("Xvid");
					newList.add("WMV");
					newList.add("MPEG");
					
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemArchiveCodecs")))
				{
					newList.clear();
					
					newList.add("FFV1");
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemImage")))
				{
					newList.clear();
					
					newList.add("JPEG");
					newList.add(language.getProperty("functionPicture")); 
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemBurnRip")))
				{
					newList.clear();
					
					newList.add("DVD");
					newList.add("Blu-ray");
					newList.add("DVD Rip");
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemAnalyze")))
				{
					newList.clear();
					
					newList.add("Loudness & True Peak");
					newList.add(language.getProperty("functionNormalization"));
					newList.add(language.getProperty("functionSceneDetection"));
					newList.add(language.getProperty("functionBlackDetection"));
					newList.add(language.getProperty("functionOfflineDetection"));
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemDownload")))
				{
					newList.clear();
					
					newList.add(language.getProperty("functionWeb"));
				}
				
				if (newList.isEmpty() == false)
				{
					comboFonctions.hidePopup();
					comboFonctions.setModel(new DefaultComboBoxModel(newList.toArray()));
					changeFilters();
					changeFunction(true);
					changeSections(false);
					comboFonctions.showPopup();
					
					newList.clear();
				}
								
				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
				{
					if (scanIsRunning == false)
					{
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						
						new VideoWeb();
						frame.setOpacity(1.0f);
					}
				}
			}
			
		});
		
		comboFonctions.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			String text = "";

			@Override
			public void keyReleased(KeyEvent e) {

				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")) 
				{
					changeFilters();
					changeFrameSize(false);

					quit.setLocation(frame.getSize().width - 20, 3);
					reduce.setLocation(quit.getLocation().x - 20, 3);
					help.setLocation(reduce.getLocation().x - 20, 3);
					newInstance.setLocation(help.getLocation().x - 20, 3);
					
					addToList.setText(language.getProperty("dropFilesHere"));
					
					if (liste.getSize() == 0)
						addToList.setVisible(true);				
					else
						addToList.setVisible(false);		
				}
				else 
				{
					if (comboFonctions.getEditor().toString().length() <= 1)
						text = String.valueOf(e.getKeyChar()).toLowerCase();

					if (Character.isLetterOrDigit(e.getKeyChar())) {
						comboFonctions.setModel(new DefaultComboBoxModel(items));
						text += String.valueOf(e.getKeyChar()).toLowerCase();

						ArrayList<String> newList = new ArrayList<String>();
						for (int i = 0; i < comboFonctions.getItemCount(); i++) {
							if (items[i].toString().length() >= text.length()) {
								if (items[i].toString().toLowerCase().substring(0, text.length()).contains(text)
										&& items[i].toString().contains(":") == false) {
									newList.add(items[i].toString());
								}
							}
						}

						// Pour éviter d'afficher le premier item
						comboFonctions.getEditor().setItem(text);

						if (newList.isEmpty() == false) {
							comboFonctions.setModel(new DefaultComboBoxModel(newList.toArray()));
							comboFonctions.showPopup();
							changeFilters();
							changeFunction(true);
						}

					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboFonctions.setModel(new DefaultComboBoxModel(items));
						comboFonctions.getEditor().setItem("");
						comboFonctions.hidePopup();
						changeFilters();
						changeSections(true);
						changeFrameSize(false);
						text = "";
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						e.consume();// Contournement pour éviter le listeDrop
					else
						comboFonctions.hidePopup();
				}
			}
		});

		String AllFilters[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4", ".mov",
				".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp"};
		comboFilter = new JComboBox<Object>(AllFilters);
		comboFilter.setName("comboFilter");
		comboFilter.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFilter.setEditable(true);
		comboFilter.setMaximumRowCount(20);
		comboFilter.setBounds(228, 19, 76, 22);
		grpChooseFunction.add(comboFilter);

		comboFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (lblFilter.getText().equals("Ext." + language.getProperty("colon")))
				{
					if (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov"))
						caseFastStart.setEnabled(true);
					else
						caseFastStart.setEnabled(false);
										
					// Ajout de la quality pour l'extension .webp
					if (comboFilter.getSelectedItem().toString().equals(".webp"))
					{
						grpResolution.add(lblImageQuality);
						grpResolution.add(comboImageQuality);
						grpResolution.repaint();
					}
					else
					{
						grpResolution.remove(lblImageQuality);
						grpResolution.remove(comboImageQuality);
						grpResolution.repaint();
					}
				}
				else if (lblFilter.getText().equals("Type" + language.getProperty("colon")))
				{
					if (comboFonctions.getSelectedItem().toString().equals("DNxHD") 
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV")) 
					{
						if (comboFilter.getSelectedItem().toString().equals("36")) {
							caseForcerEntrelacement.setEnabled(false);
							caseForcerInversion.setEnabled(false);
							caseForcerEntrelacement.setSelected(false);
							caseForcerInversion.setSelected(false);
						} else {
							caseForcerEntrelacement.setEnabled(true);
							caseForcerInversion.setEnabled(true);
						}
					}
				}
			}

		});

		comboFilter.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboFilter.getSelectedIndex();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				newitem = newitem + e.getWheelRotation();
				if (newitem >= 0 && newitem < comboFilter.getItemCount())
					comboFilter.setSelectedIndex(newitem);
				else
					newitem = comboFilter.getSelectedIndex();
			}
		});

		lblFilter = new JLabel(language.getProperty("lblFilter"));
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(new Font(montserratFont, Font.PLAIN, 13));
		lblFilter.setBounds(164, 21, 60, 16);
		grpChooseFunction.add(lblFilter);

	}

	private void grpDestination() {
		
		grpDestination = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);	
		grpDestination.setBounds(12, 462, 308, 76);
		grpDestination.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true)));		
		grpDestination.setFont(new Font(montserratFont, Font.PLAIN, 11));	
		frame.getContentPane().add(grpDestination);		

		//DESTINATION 1		
		
		destination1 = new JPanel();
		destination1.setLayout(null);
		destination1.setFont(new Font(montserratFont, Font.PLAIN, 12));	
				
		caseOpenFolderAtEnd1 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd1.setName("caseOpenFolderAtEnd1");
		caseOpenFolderAtEnd1.setSelected(true);
		caseOpenFolderAtEnd1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd1.setBounds(6, 23, caseOpenFolderAtEnd1.getPreferredSize().width, 23);
		destination1.add(caseOpenFolderAtEnd1);

		caseChangeFolder1 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder1.setName("caseChangeFolder1");
		caseChangeFolder1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseChangeFolder1.setBounds(212, 23, 89, 23);
		destination1.add(caseChangeFolder1);

		caseChangeFolder1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (scanIsRunning)
				{
					caseChangeFolder1.setSelected(true);
				}
				else 
				{
					if (caseChangeFolder1.isSelected() || inputDeviceIsRunning) 
					{
						File destination = null;
						if (System.getProperty("os.name").contains("Mac")) {
							FileDialog dialog = new FileDialog(frame, language.getProperty("chooseDestinationFolder"),
									FileDialog.LOAD);
							
							if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
								dialog.setDirectory(Settings.lblDestination1.getText());
							else
								dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
							dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
							dialog.setAlwaysOnTop(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "true");
							dialog.setVisible(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "false");
							if (dialog.getDirectory() != null)
								destination = new File(dialog.getDirectory() + dialog.getFile());
						} else if (System.getProperty("os.name").contains("Linux")) {			
							/*
							File dialog = WebDirectoryChooser.showDialog(frame, System.getProperty("user.home") + "/Desktop");

							if (dialog != null)
								destination = dialog;	
							*/					
							
							JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
							dialog.setDialogTitle(language.getProperty("chooseDestinationFolder"));
							dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							
							if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
								dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
							else
								dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

							int result = dialog.showOpenDialog(frame);
							if (result == JFileChooser.APPROVE_OPTION) 
				               destination = new File(dialog.getSelectedFile().toString());	
						} else {
							Shell shell = new Shell(SWT.ON_TOP);

							shell.setSize(frame.getSize().width, frame.getSize().height);
							shell.setLocation(frame.getLocation().x, frame.getLocation().y);
							shell.setAlpha(0);
							shell.open();

							DirectoryDialog dialog = new DirectoryDialog(shell);
							dialog.setText(language.getProperty("chooseDestinationFolder"));	
							
							if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
								dialog.setFilterPath(Settings.lblDestination1.getText());
							else
								dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

							try {
								destination = new File(dialog.open());
							} catch (Exception e1) {}

							shell.dispose();
						}

						if (destination != null)
						{							
							//Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);
							
							if (destination.isFile())
								lblDestination1.setText(destination.getParent());
							else
								lblDestination1.setText(destination.toString());
							
							//Si destination identique à l'une des autres
							if (lblDestination1.getText().equals(lblDestination2.getText()) || lblDestination1.getText().equals(lblDestination3.getText())) 
							{
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination1.setText(language.getProperty("sameAsSource"));
								caseChangeFolder1.setSelected(false);
							}
							else
							{
								caseOpenFolderAtEnd1.setSelected(false);
								
								if (scan.getText().equals(language.getProperty("menuItemStopScan")) && liste.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (liste.firstElement().substring(0, liste.firstElement().length() - 1)
											.equals(lblDestination1.getText())) {
										JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination1.setText(language.getProperty("sameAsSource"));
										caseChangeFolder1.setSelected(false);
										caseChangeFolder1.doClick();
									} else {
										scanIsRunning = true;
										changeFilters();
									}
								}
							}							
							
							if (lblDestination1.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(lblDestination1.getText());
							
						} else {
							
							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();
							
							if (inputDeviceIsRunning == false)
								caseChangeFolder1.setSelected(false);
						}
					} else {
						
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
								|| inputDeviceIsRunning)
						{
							if (System.getProperty("os.name").contains("Windows"))
							{
								if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
									lblDestination1.setText(Settings.lblDestination1.getText());
								else
									lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
							}
							else
							{
								if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
									lblDestination1.setText(Settings.lblDestination1.getText());
								else
									lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
							}
						} else
							lblDestination1.setText(language.getProperty("sameAsSource"));
					}
					
					if (inputDeviceIsRunning)
						caseChangeFolder1.setSelected(true);
					
				} // End if scan
			}
		});

		lblDestination1 = new JTextField() {
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				
				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			    qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			    
			    g2d.setRenderingHints(qualityHints);
			        
			    if (lblDestination1.getText().equals(language.getProperty("sameAsSource")) == false && lblDestination1.getText().equals(language.getProperty("aucune")) == false)
			    {
			    	File drive = new File(lblDestination1.getText());
			    				    	
			    	Integer fill = lblDestination1.getWidth() - ((int) ((float) ((float) drive.getFreeSpace() * lblDestination1.getWidth()) / drive.getTotalSpace()));
			    	
			    	Color c = new Color(75,75,75);
			    	if (fill > lblDestination1.getWidth() * 0.9) // Only 10% space left
			    		c = new Color(150,75,75);			    		
			    		
			    	// Fill
					GradientPaint gp = new GradientPaint(0, 0, new Color(50,50,50), fill, lblDestination1.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination1.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(50,50,50), lblDestination1.getWidth(), lblDestination1.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				    g2d.drawRect(0, 0, lblDestination1.getWidth() - 1, lblDestination1.getHeight() - 1);
			    }
			    
			    g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination1.getText(), 0, 15);
				
				lblDestination1.setToolTipText(lblDestination1.getText());
			}
			
		};
		
		lblDestination1.setName("lblDestination1");
		lblDestination1.setEditable(false);
		lblDestination1.setForeground(Utils.themeColor);
		lblDestination1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination1.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination1.setBackground(new Color(50, 50, 50));
		lblDestination1.setText(language.getProperty("sameAsSource"));
		lblDestination1.setBounds(6, 0, 290, 22);
		destination1.add(lblDestination1);
		
		//DESTINATION 2
		
		destination2 = new JPanel();
		destination2.setLayout(null);
		destination2.setFont(new Font(montserratFont, Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd2 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd2.setName("caseOpenFolderAtEnd2");
		caseOpenFolderAtEnd2.setSelected(false);
		caseOpenFolderAtEnd2.setEnabled(false);
		caseOpenFolderAtEnd2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd2.setBounds(6, 23, caseOpenFolderAtEnd2.getPreferredSize().width, 23);
		destination2.add(caseOpenFolderAtEnd2);

		caseChangeFolder2 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder2.setName("caseChangeFolder2");
		caseChangeFolder2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseChangeFolder2.setBounds(212, 23, 89, 23);
		destination2.add(caseChangeFolder2);

		caseChangeFolder2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scanIsRunning) {
					caseChangeFolder2.setSelected(true);
				} else {

					if (caseChangeFolder2.isSelected()) {
						File destination = null;
						if (System.getProperty("os.name").contains("Mac")) {
							FileDialog dialog = new FileDialog(frame, language.getProperty("chooseDestinationFolder"),
									FileDialog.LOAD);
							
							if (Settings.lblDestination2.getText() != "" && new File(Settings.lblDestination2.getText()).exists())
								dialog.setDirectory(Settings.lblDestination2.getText());
							else
								dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
							dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
							dialog.setAlwaysOnTop(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "true");
							dialog.setVisible(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "false");
							if (dialog.getDirectory() != null)
								destination = new File(dialog.getDirectory() + dialog.getFile());
						} else if (System.getProperty("os.name").contains("Linux")) {
							JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
							dialog.setDialogTitle(language.getProperty("chooseDestinationFolder"));
							dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							
							if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
								dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
							else
								dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

							int result = dialog.showOpenDialog(frame);
							if (result == JFileChooser.APPROVE_OPTION) 
				               destination = new File(dialog.getSelectedFile().toString());				   
						} else {
							Shell shell = new Shell(SWT.ON_TOP);

							shell.setSize(frame.getSize().width, frame.getSize().height);
							shell.setLocation(frame.getLocation().x, frame.getLocation().y);
							shell.setAlpha(0);
							shell.open();

							DirectoryDialog dialog = new DirectoryDialog(shell);
							dialog.setText(language.getProperty("chooseDestinationFolder"));	
							
							if (Settings.lblDestination2.getText() != "" && new File(Settings.lblDestination2.getText()).exists())
								dialog.setFilterPath(Settings.lblDestination2.getText());
							else
								dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

							try {
								destination = new File(dialog.open());
							} catch (Exception e1) {}

							shell.dispose();
						}

						if (destination != null) {
							
							//Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);
							
							if (destination.isFile())
								lblDestination2.setText(destination.getParent());
							else
								lblDestination2.setText(destination.toString());
							
							//Si destination identique à l'une des autres
							if (lblDestination2.getText().equals(lblDestination1.getText()) || lblDestination2.getText().equals(lblDestination3.getText())) 
							{
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination2.setText(language.getProperty("aucune"));
								caseChangeFolder2.setSelected(false);
								
								lblDestination3.setText(language.getProperty("aucune"));
								caseOpenFolderAtEnd3.setSelected(false);
								caseOpenFolderAtEnd3.setEnabled(false);
								caseChangeFolder3.setSelected(false);	
								caseChangeFolder3.setEnabled(false);														
							}
							else
							{
								caseOpenFolderAtEnd2.setSelected(false);
								caseOpenFolderAtEnd2.setEnabled(true);
								
								caseChangeFolder3.setEnabled(true);
								
								if (scan.getText().equals(language.getProperty("menuItemStopScan")) && liste.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (liste.firstElement().substring(0, liste.firstElement().length() - 1)
											.equals(lblDestination2.getText())) {
										JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination2.setText(language.getProperty("aucune"));
										caseChangeFolder2.setSelected(false);
										caseChangeFolder2.doClick();
									} else {
										scanIsRunning = true;
										changeFilters();
									}
								}
							}
							
							if (lblDestination2.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(lblDestination2.getText());
							
						} else {
							caseChangeFolder2.setSelected(false);
							
							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
								|| inputDeviceIsRunning) {
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								lblDestination2.setText(System.getProperty("user.home") + "/Desktop");
							else
								lblDestination2.setText(System.getProperty("user.home") + "\\Desktop");
						} else
							lblDestination2.setText(language.getProperty("aucune"));
						
						caseOpenFolderAtEnd2.setSelected(false);
						caseOpenFolderAtEnd2.setEnabled(false);
						
						lblDestination3.setText(language.getProperty("aucune"));
						caseOpenFolderAtEnd3.setSelected(false);
						caseOpenFolderAtEnd3.setEnabled(false);
						caseChangeFolder3.setSelected(false);	
						caseChangeFolder3.setEnabled(false);
					}
				} // End if scan
			}
		});

		lblDestination2 = new JTextField() {
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				
				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			    qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			    
			    g2d.setRenderingHints(qualityHints);
			        
			    if (lblDestination2.getText().equals(language.getProperty("sameAsSource")) == false && lblDestination2.getText().equals(language.getProperty("aucune")) == false)
			    {
			    	File drive = new File(lblDestination2.getText());
			    				    	
			    	Integer fill = lblDestination2.getWidth() - ((int) ((float) ((float) drive.getFreeSpace() * lblDestination2.getWidth()) / drive.getTotalSpace()));
			    	
			    	Color c = new Color(75,75,75);
			    	if (fill > lblDestination2.getWidth() * 0.9) // Only 10% space left
			    		c = new Color(150,75,75);			    		
			    		
			    	// Fill
					GradientPaint gp = new GradientPaint(0, 0, new Color(50,50,50), fill, lblDestination2.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination2.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(50,50,50), lblDestination2.getWidth(), lblDestination2.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				    g2d.drawRect(0, 0, lblDestination2.getWidth() - 1, lblDestination2.getHeight() - 1);
			    }
			    
			    g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination2.getText(), 0, 15);
				
				lblDestination2.setToolTipText(lblDestination2.getText());
			}
			
		};

		lblDestination2.setName("lblDestination2");
		lblDestination2.setEditable(false);
		lblDestination2.setForeground(Utils.themeColor);
		lblDestination2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination2.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination2.setBackground(new Color(50, 50, 50));
		lblDestination2.setText(language.getProperty("aucune"));
		lblDestination2.setBounds(6, 0, 290, 22);
		destination2.add(lblDestination2);
		
		//DESTINATION 3
		
		destination3 = new JPanel();
		destination3.setLayout(null);
		destination3.setFont(new Font(montserratFont, Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd3 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd3.setName("caseOpenFolderAtEnd3");
		caseOpenFolderAtEnd3.setSelected(false);
		caseOpenFolderAtEnd3.setEnabled(false);
		caseOpenFolderAtEnd3.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd3.setBounds(6, 23, caseOpenFolderAtEnd3.getPreferredSize().width, 23);
		destination3.add(caseOpenFolderAtEnd3);

		caseChangeFolder3 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder3.setName("caseChangeFolder3");
		caseChangeFolder3.setEnabled(false);
		caseChangeFolder3.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseChangeFolder3.setBounds(212, 23, 89, 23);
		destination3.add(caseChangeFolder3);

		caseChangeFolder3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scanIsRunning) {
					caseChangeFolder3.setSelected(true);
				} else {

					if (caseChangeFolder3.isSelected()) {
						File destination = null;
						if (System.getProperty("os.name").contains("Mac")) {
							FileDialog dialog = new FileDialog(frame, language.getProperty("chooseDestinationFolder"),
									FileDialog.LOAD);
							
							if (Settings.lblDestination3.getText() != "" && new File(Settings.lblDestination3.getText()).exists())
								dialog.setDirectory(Settings.lblDestination3.getText());
							else
								dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
							dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
							dialog.setAlwaysOnTop(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "true");
							dialog.setVisible(true);
							System.setProperty("apple.awt.fileDialogForDirectories", "false");
							if (dialog.getDirectory() != null)
								destination = new File(dialog.getDirectory() + dialog.getFile());
						} else if (System.getProperty("os.name").contains("Linux")) {
							JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
							dialog.setDialogTitle(language.getProperty("chooseDestinationFolder"));
							dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							
							if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
								dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
							else
								dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

							int result = dialog.showOpenDialog(frame);
							if (result == JFileChooser.APPROVE_OPTION) 
				               destination = new File(dialog.getSelectedFile().toString());				   
						} else {
							Shell shell = new Shell(SWT.ON_TOP);

							shell.setSize(frame.getSize().width, frame.getSize().height);
							shell.setLocation(frame.getLocation().x, frame.getLocation().y);
							shell.setAlpha(0);
							shell.open();

							DirectoryDialog dialog = new DirectoryDialog(shell);
							dialog.setText(language.getProperty("chooseDestinationFolder"));	
							
							if (Settings.lblDestination3.getText() != "" && new File(Settings.lblDestination3.getText()).exists())
								dialog.setFilterPath(Settings.lblDestination3.getText());
							else
								dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

							try {
								destination = new File(dialog.open());
							} catch (Exception e1) {}

							shell.dispose();
						}

						if (destination != null) {
							
							//Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);
							
							if (destination.isFile())
								lblDestination3.setText(destination.getParent());
							else
								lblDestination3.setText(destination.toString());
									
							//Si destination identique à l'une des autres
							if (lblDestination3.getText().equals(lblDestination1.getText()) || lblDestination3.getText().equals(lblDestination2.getText())) 
							{
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination3.setText(language.getProperty("aucune"));
								caseChangeFolder3.setSelected(false);
							}
							else
							{
								caseOpenFolderAtEnd3.setSelected(false);
								caseOpenFolderAtEnd3.setEnabled(true);
								
								if (scan.getText().equals(language.getProperty("menuItemStopScan")) && liste.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (liste.firstElement().substring(0, liste.firstElement().length() - 1)
											.equals(lblDestination3.getText())) {
										JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination3.setText(language.getProperty("aucune"));
										caseChangeFolder3.setSelected(false);
										caseChangeFolder3.doClick();
									} else {
										scanIsRunning = true;
										changeFilters();
									}
								}
							}
							
							if (lblDestination3.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(lblDestination3.getText());
							
						} else {
							caseChangeFolder3.setSelected(false);

							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
								|| inputDeviceIsRunning) {
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								lblDestination3.setText(System.getProperty("user.home") + "/Desktop");
							else
								lblDestination3.setText(System.getProperty("user.home") + "\\Desktop");
						} else
							lblDestination3.setText(language.getProperty("aucune"));
						
						caseOpenFolderAtEnd3.setSelected(false);
						caseOpenFolderAtEnd3.setEnabled(false);
					}
				} // End if scan
			}
		});

		lblDestination3 = new JTextField() {
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				
				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			    qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
			    
			    g2d.setRenderingHints(qualityHints);
			        
			    if (lblDestination3.getText().equals(language.getProperty("sameAsSource")) == false && lblDestination3.getText().equals(language.getProperty("aucune")) == false)
			    {
			    	File drive = new File(lblDestination3.getText());
			    				    	
			    	Integer fill = lblDestination3.getWidth() - ((int) ((float) ((float) drive.getFreeSpace() * lblDestination3.getWidth()) / drive.getTotalSpace()));
			    	
			    	Color c = new Color(75,75,75);
			    	if (fill > lblDestination3.getWidth() * 0.9) // Only 10% space left
			    		c = new Color(150,75,75);			    		
			    		
			    	// Fill
					GradientPaint gp = new GradientPaint(0, 0, new Color(50,50,50), fill, lblDestination3.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination3.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(50,50,50), lblDestination3.getWidth(), lblDestination3.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				    g2d.drawRect(0, 0, lblDestination3.getWidth() - 1, lblDestination3.getHeight() - 1);
			    }
			    
			    g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination3.getText(), 0, 15);
				
				lblDestination3.setToolTipText(lblDestination3.getText());
			}
			
		};
		
		lblDestination3.setName("lblDestination3");
		lblDestination3.setEditable(false);
		lblDestination3.setForeground(Utils.themeColor);
		lblDestination3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination3.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination3.setBackground(new Color(50, 50, 50));
		lblDestination3.setText(language.getProperty("aucune"));
		lblDestination3.setBounds(6, 0, 290, 22);
		destination3.add(lblDestination3);
		
		//MAIL		
		destinationMail = new JPanel();
		destinationMail.setLayout(null);
		destinationMail.setFont(new Font(montserratFont, Font.PLAIN, 12));
				
		caseSendMail = new JRadioButton(Shutter.language.getProperty("caseSendMail"));
		caseSendMail.setName("caseSendMail");
		caseSendMail.setSelected(false);
		caseSendMail.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSendMail.setBounds(6, -2, caseSendMail.getPreferredSize().width, 23);
		destinationMail.add(caseSendMail);
		
		caseSendMail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseSendMail.isSelected())
				{
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					if (textMail.getText().length() <= 0)
						textMail.setText(language.getProperty("textMail"));
					else if (textMail.getText().equals(language.getProperty("textMail")) == false)
					{
						textMail.setFont(new Font("SansSerif", Font.PLAIN, 12));
						if (Utils.getTheme.equals(Shutter.language.getProperty("darkTheme")))
							textMail.setForeground(Color.WHITE);
						else
							textMail.setForeground(Color.BLACK);
					}
					textMail.setEnabled(true);	
				}
				else
				{
					textMail.setEnabled(false);
				}				
			}			
		});
		
		textMail = new JTextField();
		textMail.setName("textMail");
		textMail.setEnabled(false);
		textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textMail.setForeground(Color.LIGHT_GRAY);
		textMail.setBounds(6, 23, 290, 21);
		textMail.setColumns(1);
		textMail.setText(language.getProperty("textMail"));
		destinationMail.add(textMail);
		
		textMail.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (textMail.getText().equals(language.getProperty("textMail"))) {
					textMail.setText("");
					textMail.setFont(new Font("SansSerif", Font.PLAIN, 12));
					if (Utils.getTheme.equals(Shutter.language.getProperty("darkTheme")))
						textMail.setForeground(Color.WHITE);
					else
						textMail.setForeground(Color.BLACK);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					textMail.setText(language.getProperty("textMail"));
					caseSendMail.setSelected(false);
					textMail.setEnabled(false);
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER)
					grpDestination.setSelectedIndex(0);

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});
		
		//Stream		
		destinationStream = new JPanel();
		destinationStream.setLayout(null);
		destinationStream.setFont(new Font(montserratFont, Font.PLAIN, 12));
				
		caseStream = new JRadioButton(Shutter.language.getProperty("caseStream"));
		caseStream.setName("caseStream");
		caseStream.setSelected(false);
		caseStream.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseStream.setBounds(6, -2, caseStream.getPreferredSize().width, 23);
		destinationStream.add(caseStream);
		
		caseLoop = new JRadioButton(Shutter.language.getProperty("caseLoop"));
		caseLoop.setName("caseLoop");
		caseLoop.setEnabled(false);
		caseLoop.setSelected(false);
		caseLoop.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLoop.setBounds(caseStream.getX() + caseStream.getWidth() + 7, -2, caseLoop.getPreferredSize().width, 23);
		destinationStream.add(caseLoop);
		
		caseStream.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseStream.isSelected())
				{
					textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textStream.setForeground(Color.LIGHT_GRAY);
					if (textStream.getText().length() <= 0)
						textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
					else if (textStream.getText().equals("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx") == false) 
					{
						textStream.setFont(new Font("SansSerif", Font.PLAIN, 12));
						if (Utils.getTheme.equals(Shutter.language.getProperty("darkTheme")))
							textStream.setForeground(Color.WHITE);
						else
							textStream.setForeground(Color.BLACK);
					}
						
					textStream.setEnabled(true);	
					caseLoop.setEnabled(true);
				}
				else
				{
					textStream.setEnabled(false);
					caseLoop.setEnabled(false);
				}				
			}			
		});
		
		textStream = new JTextField();
		textStream.setName("textStream");
		textStream.setEnabled(false);
		textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textStream.setForeground(Color.LIGHT_GRAY);
		textStream.setBounds(6, 23, 290, 21);
		textStream.setColumns(1);
		textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
		destinationStream.add(textStream);
		
		textStream.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (textStream.getText().equals("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx")) {
					textStream.setText("");
					textStream.setFont(new Font("SansSerif", Font.PLAIN, 12));
					if (Utils.getTheme.equals(Shutter.language.getProperty("darkTheme")))
						textStream.setForeground(Color.WHITE);
					else
						textStream.setForeground(Color.BLACK);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textStream.setForeground(Color.LIGHT_GRAY);
					textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
					caseStream.setSelected(false);
					textStream.setEnabled(false);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});
		
		
		//Destinations icons
		wetransferIcon = new FlatSVGIcon("contents/wetransfer.svg", 18, 18);
		mailIcon = new FlatSVGIcon("contents/mail.svg", 18, 18);
		streamIcon = new FlatSVGIcon("contents/stream.svg", 18, 18);
		
		//Ajout des tabs	
		setDestinationTabs(6);
		
		grpDestination.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {	
				try {				
					if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("FTP"))
					{
						grpDestination.setSelectedComponent(destinationMail);
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						new Ftp();
						frame.setOpacity(1.0f);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("WeTransfer"))
					{
						grpDestination.setSelectedIndex(0);
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						new Wetransfer();
						frame.setOpacity(1.0f);
					}
				} catch (Exception e) {}				
			}

			
		});
		
		popupDestination = new JPopupMenu();
		final JMenuItem openFolder = new JMenuItem(language.getProperty("menuItemOpenFolder"));
		popupDestination.add(openFolder);

		openFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					switch (grpDestination.getSelectedIndex())
					{
						case 0:
							Desktop.getDesktop().open(new File(lblDestination1.getText()));
							break;
						case 1:
							Desktop.getDesktop().open(new File(lblDestination2.getText()));
							break;
						case 2:
							Desktop.getDesktop().open(new File(lblDestination3.getText()));
							break;
					}
				} catch (IOException e1) {
				}
			}
		});

		// Drag & Drop
		lblDestination1.setTransferHandler(new DestinationFileTransferHandler());
		lblDestination2.setTransferHandler(new DestinationFileTransferHandler());
		lblDestination3.setTransferHandler(new DestinationFileTransferHandler());

		MouseListener mouseListener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3
						|| (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0
								&& e.getButton() == MouseEvent.BUTTON1) {
					
					switch (grpDestination.getSelectedIndex())
					{
						case 0:
							if (lblDestination1.getText().equals(language.getProperty("sameAsSource")) == false)
								openFolder.setVisible(true);
							else
								openFolder.setVisible(false);						

							popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
							break;
						case 1:
							if (lblDestination2.getText().equals(language.getProperty("aucune")) == false)
							{
								openFolder.setVisible(true);
								popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
							}
							else
								openFolder.setVisible(false);
							
							break;
						case 2:
							if (lblDestination3.getText().equals(language.getProperty("aucune")) == false)
							{
								openFolder.setVisible(true);
								popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
							}
							else
								openFolder.setVisible(false);
							
							break;
					}					

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
				lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
			}

		};

		destination1.addMouseListener(mouseListener);

		for (Component component : destination1.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination1.setToolTipText(language.getProperty("rightClick"));

		destination2.addMouseListener(mouseListener);

		for (Component component : destination2.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination2.setToolTipText(language.getProperty("rightClick"));
		
		destination3.addMouseListener(mouseListener);

		for (Component component : destination3.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination3.setToolTipText(language.getProperty("rightClick"));
				

	}

	private void grpProgress() {
		grpProgression = new JPanel();
		grpProgression.setLayout(null);
		grpProgression.setBounds(10, 544, 312, 94);
		grpProgression.setBackground(new Color(50, 50, 50));
		grpProgression.setToolTipText(language.getProperty("rightClick"));
		grpProgression.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpProgression") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpProgression);

		caseRunInBackground = new JRadioButton(language.getProperty("caseRunInBackground"));
		caseRunInBackground.setName("caseRunInBackground");
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRunInBackground.setBounds(9, 64, caseRunInBackground.getPreferredSize().width, 23);

		if (System.getProperty("os.arch").equals("amd64")) //Not compatible with Mac ARM
			grpProgression.add(caseRunInBackground);

		// Inactivité
		caseRunInBackground.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseRunInBackground.isSelected()) {
					Thread thread = new Thread(new Runnable() {
						boolean running = true;

						@Override
						public void run() {

							// Appuie clavier en dehors de l'application
							GlobalKeyListener.main(null);

							int x = MouseInfo.getPointerInfo().getLocation().x;
							int y = MouseInfo.getPointerInfo().getLocation().y;
							long lastInputTime = System.currentTimeMillis();
							do {
								long timeSinceLastInput = System.currentTimeMillis() - lastInputTime;

								if (x != MouseInfo.getPointerInfo().getLocation().x
										|| y != MouseInfo.getPointerInfo().getLocation().y
										|| GlobalKeyListener.keyIsPressed) {
									lastInputTime = System.currentTimeMillis();
									x = MouseInfo.getPointerInfo().getLocation().x;
									y = MouseInfo.getPointerInfo().getLocation().y;
								}
								if (timeSinceLastInput >= 1000) {
									if (running == false)
										FFMPEG.resumeProcess();
									running = true;
								} else {
									tempsRestant.setText(Shutter.language.getProperty("timePause"));
									if (running == true)
										FFMPEG.suspendProcess();
									running = false;
								}

								if (caseRunInBackground.isSelected() == false)
									FFMPEG.resumeProcess();

							} while (caseRunInBackground.isSelected()
									&& btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")));

						}// End Run

					});// End Thread
					thread.start();
				}
			}

		});

		caseDisplay = new JRadioButton(language.getProperty("menuItemVisualiser"));
		caseDisplay.setName("caseDisplay");
		caseDisplay.setEnabled(false);
		caseDisplay.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDisplay.setBounds(215, 64, caseDisplay.getPreferredSize().width, 23);
		grpProgression.add(caseDisplay);

		progressBar1 = new JProgressBar();
		progressBar1.setBounds(6, 42, 300, 14);
		progressBar1.setFont(new Font(montserratFont, Font.PLAIN, 12));
		progressBar1.setStringPainted(true);
		grpProgression.add(progressBar1);
		
		progressBar1.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported()) 
				{ 
					if (progressBar1.isIndeterminate())
						Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.INDETERMINATE);
					else
						Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.NORMAL);
					
					if (progressBar1.getValue() > 0)
						Taskbar.getTaskbar().setWindowProgressValue(frame, (100 * progressBar1.getValue()) / progressBar1.getMaximum());
				} 				
			}
			
		});

		lblCurrentEncoding = new JLabel(language.getProperty("lblEncodageEnCours"));
		lblCurrentEncoding.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		lblCurrentEncoding.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblCurrentEncoding.setBounds(6, 19, 300, 16);
		grpProgression.add(lblCurrentEncoding);

		popupProgression = new JPopupMenu();
		JMenuItem console = new JMenuItem(language.getProperty("menuItemConsole"));
		popupProgression.add(console);

		console.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Console.frmConsole != null) {
					if (Console.frmConsole.isVisible())
						Console.frmConsole.toFront();
					else
						new Console();
				} else
					new Console();
			}
		});

		grpProgression.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3
						|| (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0
								&& e.getButton() == MouseEvent.BUTTON1)
					popupProgression.show(grpProgression, e.getX() - 30, e.getY());
			}

		});
	}

	private void grpResolution() {
		grpResolution = new JPanel();
		grpResolution.setLayout(null);
		grpResolution.setVisible(false);
		grpResolution.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpResolution") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpResolution.setBackground(new Color(50, 50, 50));
		grpResolution.setBounds(334, 59, 312, 145);
		frame.getContentPane().add(grpResolution);

		comboResolution = new JComboBox<String>();
		comboResolution.setName("comboResolution");
		comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "2:1", "4:1", "8:1", "16:1",
				"4096:auto", "1920:auto", "1280:auto", "auto:480", "auto:360",
				"4096x2160", "3840x2160", "1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000",
				"854x480", "720x576", "640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
		comboResolution.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboResolution.setEditable(true);
		comboResolution.setBounds(83, 18, 117, 22);
		comboResolution.setMaximumRowCount(21);
		grpResolution.add(comboResolution);

		comboResolution.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {                      
			if (comboResolution.getItemCount() > 0 //Contourne un bug lors de l'action sur le btnReset
			&& comboFonctions.getSelectedItem().toString().equals("JPEG") == false
			&& comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) == false)  
			{
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
				{
					lblPad.setVisible(false);
				}
				else
				{
					lblPad.setText(language.getProperty("lblPad"));					
					lblPad.setVisible(true);
				}	
				
				changeFilters();
			}
		  }
			
		});
		
		comboResolution.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
				if (comboFonctions.getSelectedItem().toString().equals("JPEG") == false && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) == false)
				{
					lblPad.setVisible(true);
					lblPad.setText(language.getProperty("lblPad"));
				}
				
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' && caracter != 'x'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});
		
		caseRognerImage = new JRadioButton(language.getProperty("caseCropImage"));
		caseRognerImage.setName("caseRognerImage");
		caseRognerImage.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRognerImage.setBounds(7, 47, caseRognerImage.getPreferredSize().width, 23);
		grpResolution.add(caseRognerImage);
		
		lblImageQuality = new JLabel(language.getProperty("lblQualit"));
		lblImageQuality.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblImageQuality.setBounds(comboResolution.getWidth() + comboResolution.getLocation().x + 5, comboResolution.getY() + 3, lblImageQuality.getPreferredSize().width + 4, 16);
		grpResolution.add(lblImageQuality);
		
		comboImageQuality = new JComboBox<String>();
		comboImageQuality.setName("comboImageQuality");
		comboImageQuality.setModel(new DefaultComboBoxModel<String>(
				new String[] { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%" }));
		comboImageQuality.setSelectedIndex(0);
		comboImageQuality.setMaximumRowCount(20);
		comboImageQuality.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboImageQuality.setEditable(true);
		comboImageQuality.setSize(50, 16);
		comboImageQuality.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
		grpResolution.add(comboImageQuality);

		caseRognerImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (liste.getSize() == 0 && caseRognerImage.isSelected()) {
					JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
							language.getProperty("noFile"), JOptionPane.ERROR_MESSAGE);
					caseRognerImage.setSelected(false);
				}
				if (caseRognerImage.isSelected()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}

					if (CropImage.frame == null)
						new CropImage();
					else {
						
						if (scanIsRunning)
						{
							File dir = new File(liste.firstElement());
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
								FFPROBE.Data(liste.firstElement());
						}
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						} while (FFPROBE.isRunning);
						
						if (FFPROBE.totalLength > 100) //Plus d'une image
							CropImage.positionVideo.setVisible(true);
						else
							CropImage.positionVideo.setVisible(false);
						
						CropImage.positionVideo.setValue(0);
						CropImage.positionVideo.setMaximum(FFPROBE.totalLength);

						CropImage.loadImage();
						Utils.changeDialogVisibility(CropImage.frame, false);
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {
	
							@Override
							public void run() {
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException er) {}
								} while (CropImage.frame.isVisible());
								
								frame.setOpacity(1.0f);
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
								
								if (cropFinal == null)
									caseRognerImage.setSelected(false);
							}
						});
						t.start();
					}
					else
					{
						frame.setOpacity(1.0f);
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
						
						if (cropFinal == null)
							caseRognerImage.setSelected(false);
					}
					
					enableAll();
				} 
			}

		});

		caseRotate = new JRadioButton(language.getProperty("caseRotate"));
		caseRotate.setName("caseRotate");
		caseRotate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRotate.setBounds(7, caseRognerImage.getLocation().y + caseRognerImage.getHeight(), caseRotate.getPreferredSize().width, 23);
		grpResolution.add(caseRotate);

		caseRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseRotate.isSelected())
					comboRotate.setEnabled(true);
				else
					comboRotate.setEnabled(false);
			}

		});

		comboRotate = new JComboBox<String>();
		comboRotate.setName("comboRotate");
		comboRotate.setEnabled(false);
		comboRotate.setModel(new DefaultComboBoxModel<String>(new String[] { "90", "-90", "180" }));
		comboRotate.setSelectedIndex(2);
		comboRotate.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboRotate.setEditable(false);
		comboRotate.setBounds(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3, 46, 16);
		comboRotate.setMaximumRowCount(20);
		grpResolution.add(comboRotate);

		caseMiror = new JRadioButton(language.getProperty("caseMiror"));
		caseMiror.setName("caseMiror");
		caseMiror.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMiror.setBounds(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y, caseMiror.getPreferredSize().width,	23);
		grpResolution.add(caseMiror);

		caseCreateSequence = new JRadioButton(language.getProperty("caseCreateSequence"));
		caseCreateSequence.setName("caseCreateSequence");
		caseCreateSequence.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseCreateSequence.getPreferredSize().width, 23);
		grpResolution.add(caseCreateSequence);

		caseCreateSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseCreateSequence.setSelected(false);
				}
				
				if (caseCreateSequence.isSelected())
					comboInterpret.setEnabled(true);
				else
					comboInterpret.setEnabled(false);				
			}
			
		});
		
		lblInterpretation = new JLabel(Shutter.language.getProperty("lblInterpretation"));
		lblInterpretation.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblInterpretation.setBounds(28, caseCreateSequence.getY() + caseCreateSequence.getHeight() + 2, lblInterpretation.getPreferredSize().width, 16);
		grpResolution.add(lblInterpretation);
		
		comboInterpret = new JComboBox<String>();
		comboInterpret.setName("comboInterpret");
		comboInterpret.setEnabled(false);
		comboInterpret.setModel(new DefaultComboBoxModel<String>(
				new String[] { "1", "5", "10", "15","20", "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboInterpret.setSelectedIndex(7);
		comboInterpret.setMaximumRowCount(20);
		comboInterpret.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboInterpret.setEditable(true);
		comboInterpret.setSize(63, 16);
		comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4, lblInterpretation.getLocation().y);
		grpResolution.add(comboInterpret);
		
		lblIsInterpret = new JLabel(Shutter.language.getProperty("fps"));
		lblIsInterpret.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblIsInterpret.setSize(20, 16);
		lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblInterpretation.getLocation().y - 1);
		grpResolution.add(lblIsInterpret);
		
		iconTVInterpret = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVInterpret.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVInterpret.setBounds(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY(), 16, 16);
		iconTVInterpret.setToolTipText(language.getProperty("preview"));
		grpResolution.add(iconTVInterpret);
		
		iconTVInterpret.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Définition de la taille
				if (liste.getSize() > 0) {
					
					String file = ""; 
					// Fichiers sélectionnés ?
					if (fileList.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = f.toString();
									break;
								}
							}
						} else
							file = fileList.getSelectedValue().toString();

					} else
						file = liste.firstElement();
					
					//Analyse
					FFPROBE.Data(file.toString());	
					do
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					while (FFPROBE.isRunning);
				
					//FFMPEGTOFFPLAY
					FFMPEG.toFFPLAY(" -r " + (float) FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", ".")) + " -i " + '"' + file + '"' + " -vf scale=1080:-1 -r 1 -c:v rawvideo -map v:0 -an -f nut pipe:play |");
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
				iconTVInterpret.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVInterpret.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		lblTaille = new JLabel(Shutter.language.getProperty("lblTailleH264"));
		lblTaille.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTaille.setBounds(42, 20, 42, 16);
		grpResolution.add(lblTaille);

		iconTVResolution = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVResolution.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVResolution.setBounds(14, 20, 16, 16);
		iconTVResolution.setToolTipText(language.getProperty("preview"));
		grpResolution.add(iconTVResolution);

		iconTVResolution.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
								
				FFMPEG.previewEncoding();		
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVResolution.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVResolution.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

	}

	private void grpImageSequence() {
		grpImageSequence = new JPanel();
		grpImageSequence.setLayout(null);
		grpImageSequence.setVisible(false);
		grpImageSequence.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpSequenceImage") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpImageSequence.setBackground(new Color(50, 50, 50));
		grpImageSequence.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpImageSequence);
		
		grpImageSequence.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {			
				int grpSize = 161;
				
				String fonction = comboFonctions.getSelectedItem().toString();
				if ("DNxHD".equals(fonction) || "DNxHR".equals(fonction) || "Apple ProRes".equals(fonction) || "QT Animation".equals(fonction) || ("GoPro CineForm").equals(fonction) || "Uncompressed YUV".equals(fonction)
				|| "XDCAM HD422".equals(fonction) || "AVC-Intra 100".equals(fonction) || ("XAVC").equals(fonction) || "HAP".equals(fonction) || "FFV1".equals(fonction))
					grpSize = 93;
				
				final int sized = grpSize;
				
				if (grpImageSequence.getSize().height < sized) {
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

										grpImageSequence.setSize(312, i);
										grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
										grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
										
										grpImageSequence.setSize(312, i);
										grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
										grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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

		caseEnableSequence = new JRadioButton(language.getProperty("caseActiverSequence"));
		caseEnableSequence.setName("caseActiverSequence");
		caseEnableSequence.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseEnableSequence.setBounds(7, 16, caseEnableSequence.getPreferredSize().width, 23);
		grpImageSequence.add(caseEnableSequence);
		
		caseSequenceFPS = new JComboBox<String>();
		caseSequenceFPS.setName("caseSequenceFPS");
		caseSequenceFPS.setEnabled(false);
		caseSequenceFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		caseSequenceFPS.setSelectedIndex(2);
		caseSequenceFPS.setMaximumRowCount(20);
		caseSequenceFPS.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		caseSequenceFPS.setEditable(true);
		caseSequenceFPS.setBounds(caseEnableSequence.getLocation().x + caseEnableSequence.getWidth() + 4, caseEnableSequence.getLocation().y + 3, 56, 16);
		grpImageSequence.add(caseSequenceFPS);

		JLabel lblSequenceFPS = new JLabel(Shutter.language.getProperty("fps"));
		lblSequenceFPS.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblSequenceFPS.setBounds(caseSequenceFPS.getLocation().x + caseSequenceFPS.getWidth() + 4, caseSequenceFPS.getLocation().y, lblSequenceFPS.getPreferredSize().width, 16);
		grpImageSequence.add(lblSequenceFPS);

		caseEnableSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseEnableSequence.isSelected())
				{
			        String[] data = new String[liste.getSize()]; 

			        for (int i = 0 ; i < liste.getSize() ; i++) { 
			           data[i] = (String) liste.getElementAt(i); 
			        }

			        Arrays.sort(data); 
			        liste.clear();
			        
			        for (int i = 0 ; i < data.length ; i++) { 
						liste.addElement(data[i].toString());
				    }
					
					caseSequenceFPS.setEnabled(true);
				}
				else
					caseSequenceFPS.setEnabled(false);
			}

		});

		caseBlend = new JRadioButton(language.getProperty("caseBlend"));
		caseBlend.setName("caseBlend");
		caseBlend.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseBlend.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseBlend.getPreferredSize().width + 14, 23);
		grpImageSequence.add(caseBlend);

		caseBlend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseBlend.isSelected() == false)
					sliderBlend.setValue(0);
			}

		});

		sliderBlend = new JSlider();
		sliderBlend.setName("sliderBlend");
		sliderBlend.setMinorTickSpacing(1);
		sliderBlend.setMaximum(16);
		sliderBlend.setMinimum(0);
		sliderBlend.setValue(0);
		sliderBlend.setBounds(163, caseBlend.getLocation().y, 106, 22);
		grpImageSequence.add(sliderBlend);

		iconTVBlend = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVBlend.setToolTipText(language.getProperty("preview"));
		iconTVBlend.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVBlend.setBounds(289, caseBlend.getLocation().y + 3, 16, 16);
		grpImageSequence.add(iconTVBlend);

		iconTVBlend.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int value = sliderBlend.getValue();
				StringBuilder blend = new StringBuilder();
				for (int i = 0 ; i < value ; i++)
				{
					blend.append("tblend=all_mode=average,");
				}
				
				blend.append("setpts=" + (float) 25 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) + "*PTS");
				
				FFPLAY.previewFilters(blend.toString(), false);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVBlend.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVBlend.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		sliderBlend.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = sliderBlend.getValue();

				if (value != 0)
					caseBlend.setSelected(true);
				else
					caseBlend.setSelected(false);

				caseBlend.setText(language.getProperty("blend") + " x" + value);
			}

		});

		sliderBlend.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseBlend.setSelected(true);
				sliderBlend.setEnabled(true);
			}

		});
		
		caseMotionBlur = new JRadioButton(language.getProperty("caseMotionBlur"));
		caseMotionBlur.setName("caseMotionBlur");
		caseMotionBlur.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMotionBlur.setBounds(7, caseBlend.getHeight() + caseBlend.getLocation().y, caseMotionBlur.getPreferredSize().width, 23);
		grpImageSequence.add(caseMotionBlur);
		
	}
	
	private void grpImageFilter() {
		grpImageFilter = new JPanel();
		grpImageFilter.setLayout(null);
		grpImageFilter.setVisible(false);
		grpImageFilter.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpFiltreImage") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpImageFilter.setBackground(new Color(50, 50, 50));
		grpImageFilter.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpImageFilter);

		grpImageFilter.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (grpImageFilter.getSize().height < 122) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 122;
										else
											i ++;
										
										grpImageFilter.setSize(312, i);
										btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);
										
										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < 122);
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
									int i = 122;
									do {
										long startTime = System.currentTimeMillis() + 1;
										
										if (Settings.btnDisableAnimations.isSelected())
											i = 17;
										else
											i --;
										
										grpImageFilter.setSize(312, i);
										btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);
										
										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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
		
		caseYear = new JRadioButton(language.getProperty("caseYear"));
		caseYear.setName("caseYear");
		caseYear.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseYear.setBounds(7, 16, caseYear.getPreferredSize().width, 23);
		grpImageFilter.add(caseYear);
		
		caseYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseYear.isSelected())
					comboYear.setEnabled(true);
				else
					comboYear.setEnabled(false);			
			}			
		});
		
		comboYear = new JComboBox<String>();
		comboYear.setName("comboYear");
		comboYear.setEnabled(false);
		
		int actualYear = Year.now().getValue();
		String[] years = new String[actualYear + 1 - 2000];
		
		int y = 0;
		for (int i = actualYear ; i > 1999 ; i--)
		{
			years[y] = String.valueOf(i);
			y++;
		}
		
		comboYear.setModel(new DefaultComboBoxModel<String>(years));
		comboYear.setSelectedIndex(0);
		comboYear.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboYear.setEditable(true);
		comboYear.setBounds(caseYear.getWidth() + caseYear.getLocation().x + 4, caseYear.getLocation().y + 4, 54, 16);
		comboYear.setMaximumRowCount(15);
		grpImageFilter.add(comboYear);
		
		caseMonth = new JRadioButton(language.getProperty("caseMonth"));
		caseMonth.setName("caseMonth");
		caseMonth.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMonth.setBounds(7 , caseYear.getLocation().y + caseYear.getHeight() + 2, caseMonth.getPreferredSize().width, 23);
		grpImageFilter.add(caseMonth);
		
		caseMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseMonth.isSelected())
					comboMonth.setEnabled(true);
				else
					comboMonth.setEnabled(false);			
			}			
		});
		
		comboMonth = new JComboBox<String>();
		comboMonth.setName("comboMonth");
		comboMonth.setEnabled(false);

		String[] months = new String[12];	
		int m = 0;
		for (int i = 1 ; i < 13 ; i++)
		{
			months[m] = String.valueOf(i);
			if (i < 10)
				months[m] = "0" + months[m];
			
			m++;
		}
		
		comboMonth.setModel(new DefaultComboBoxModel<String>(months));
		comboMonth.setSelectedIndex(0);
		comboMonth.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboMonth.setEditable(true);
		comboMonth.setBounds(caseMonth.getWidth() + caseMonth.getLocation().x + 4, caseMonth.getLocation().y + 4, 40, 16);
		comboMonth.setMaximumRowCount(15);
		grpImageFilter.add(comboMonth);
		
		caseDay = new JRadioButton(language.getProperty("caseDay"));
		caseDay.setName("caseDay");
		caseDay.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDay.setBounds(7 , caseMonth.getLocation().y + caseMonth.getHeight() + 2, caseDay.getPreferredSize().width, 23);
		grpImageFilter.add(caseDay);
		
		caseDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseDay.isSelected())
					comboDay.setEnabled(true);
				else
					comboDay.setEnabled(false);			
			}			
		});
		
		comboDay = new JComboBox<String>();
		comboDay.setName("comboDay");
		comboDay.setEnabled(false);

		String[] days = new String[31];	
		int d = 0;
		for (int i = 1 ; i < 32 ; i++)
		{
			days[d] = String.valueOf(i);
			if (i < 10)
				days[d] = "0" + days[d];
			
			d++;
		}	
		
		comboDay.setModel(new DefaultComboBoxModel<String>(days));
		comboDay.setSelectedIndex(0);
		comboDay.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboDay.setEditable(true);
		comboDay.setBounds(caseDay.getWidth() + caseDay.getLocation().x + 4, caseDay.getLocation().y + 4, 40, 16);
		comboDay.setMaximumRowCount(15);
		grpImageFilter.add(comboDay);	
		
		caseFrom = new JRadioButton(language.getProperty("caseFrom"));
		caseFrom.setName("caseFrom");
		caseFrom.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFrom.setBounds(7, caseDay.getLocation().y + caseDay.getHeight() + 2, caseFrom.getPreferredSize().width, 23);
		grpImageFilter.add(caseFrom);
		
		caseFrom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseFrom.isSelected())
				{
					comboFrom.setEnabled(true);
					comboTo.setEnabled(true);
				}
				else
				{
					comboFrom.setEnabled(false);
					comboTo.setEnabled(false);
				}				
			}			
		});
		
		comboFrom = new JComboBox<String>();
		comboFrom.setName("comboFrom");
		comboFrom.setEnabled(false);
		
		String[] from = new String[49];
		int h = 0;
		for (int i = 0 ; i < 49 ; i++)
		{
			if( (i % 2) == 0 )
				from[i] = h + ":00";
			else
			{
				from[i] = h + ":30";
				h++;
			}
			
			if (i < 20)
				from[i] = "0" + from[i];
			
		}
		comboFrom.setModel(new DefaultComboBoxModel<String>(from));
		comboFrom.setSelectedIndex(0);
		comboFrom.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFrom.setEditable(true);
		comboFrom.setBounds(caseFrom.getWidth() + caseFrom.getLocation().x + 4, caseFrom.getLocation().y + 4, 54, 16);
		comboFrom.setMaximumRowCount(15);
		grpImageFilter.add(comboFrom);
	
		JLabel h1 = new JLabel(language.getProperty("lblH"));
		h1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		h1.setBounds(comboFrom.getLocation().x + comboFrom.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpImageFilter.add(h1);
		
		comboTo = new JComboBox<String>();
		comboTo.setName("comboTo");
		comboTo.setEnabled(false);
		
		String[] to = new String[49];
		h = 0;
		for (int i = 0 ; i < 49 ; i++)
		{
			if( (i % 2) == 0 )
				to[i] = h + ":00";
			else
			{
				to[i] = h + ":30";
				h++;
			}
			
			if (i < 20)
				to[i] = "0" + to[i];
			
		}	

		comboTo.setModel(new DefaultComboBoxModel<String>(to));
		comboTo.setSelectedIndex(48);
		comboTo.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboTo.setEditable(true);
		comboTo.setBounds(h1.getWidth() + h1.getLocation().x, comboFrom.getLocation().y, 54, 16);
		comboTo.setMaximumRowCount(15);
		grpImageFilter.add(comboTo);
		
		JLabel h2 = new JLabel(language.getProperty("lblH"));
		h2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		h2.setBounds(comboTo.getLocation().x + comboTo.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpImageFilter.add(h2);
		
	}

	private void grpColorimetry() {
		
		grpColorimetry = new JPanel();
		grpColorimetry.setLayout(null);
		grpColorimetry.setVisible(false);
		grpColorimetry.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpColorimetry") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpColorimetry.setBackground(new Color(50, 50, 50));
		grpColorimetry.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpColorimetry);
		
		grpColorimetry.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final int sized = 165;
				if (grpColorimetry.getSize().height < sized) {
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
										
										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpColorimetry.setSize(312, i);
											grpImageFilter.setLocation(334, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);										
										}
										else
										{
											grpColorimetry.setSize(312, i);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
										
										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpColorimetry.setSize(312, i);
											grpImageFilter.setLocation(334, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);										
										}
										else
										{
											grpColorimetry.setSize(312, i);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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

		caseColor = new JRadioButton(language.getProperty("caseColor"));
		caseColor.setName("caseColor");
		caseColor.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseColor.setBounds(7, 16, caseColor.getPreferredSize().width, 22);
		grpColorimetry.add(caseColor);
			
		caseColor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (liste.getSize() == 0 && caseColor.isSelected()) {
					JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
							language.getProperty("noFile"), JOptionPane.ERROR_MESSAGE);
					caseColor.setSelected(false);
				}
				
				if (caseColor.isSelected()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					File file = new File(dirTemp + "preview.bmp");
					if (file.exists())
						file.delete();
					
					if (ColorImage.frame == null)
						new ColorImage();
					else {
						
						if (scanIsRunning)
						{
							File dir = new File(liste.firstElement());
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
								FFPROBE.Data(liste.firstElement());
						}
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						} while (FFPROBE.isRunning);
						
						if (FFPROBE.totalLength > 100) //Plus d'une image
							ColorImage.positionVideo.setEnabled(true);
						else
							ColorImage.positionVideo.setEnabled(false);
						
						ColorImage.positionVideo.setValue(0);
						ColorImage.positionVideo.setMaximum(FFPROBE.totalLength);
						
						ColorImage.loadImage(true);
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						} while (new File(dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
						Utils.changeFrameVisibility(ColorImage.frame, false);
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {
	
							@Override
							public void run() {
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException er) {}
								} while (ColorImage.frame.isVisible());
								
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));									
							}
						});
						t.start();
					}
					else
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
					
					enableAll();
				}		
				else
				{
					if (ColorImage.frame != null)
						Utils.changeFrameVisibility(ColorImage.frame, true);
				}
			}
			
		});
		
		caseLevels = new JRadioButton(language.getProperty("caseLevels"));
		caseLevels.setName("caseLevels");
		caseLevels.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLevels.setBounds(7, caseColor.getLocation().y + caseColor.getHeight(), caseLevels.getPreferredSize().width, 22);
		grpColorimetry.add(caseLevels);
		
		caseLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseLevels.isSelected()) 
				{
					comboInLevels.setEnabled(true);
					comboOutLevels.setEnabled(true);
				}
				else
				{
					comboInLevels.setEnabled(false);
					comboOutLevels.setEnabled(false);
				}
			}
			
		});
		
		comboInLevels = new JComboBox<Object>(new String[] {"16-235", "0-255"});		
		comboInLevels.setName("comboInLevels");
		comboInLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboInLevels.setEditable(false);
		comboInLevels.setEnabled(false);
		comboInLevels.setSelectedIndex(0);
		comboInLevels.setMaximumRowCount(20);
		comboInLevels.setBounds(grpColorimetry.getWidth() - 150 - 7, caseLevels.getLocation().y + 4, 60, 16);
		grpColorimetry.add(comboInLevels);
		
		comboInLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboInLevels.getSelectedIndex() == 1)
					comboOutLevels.setSelectedIndex(0);
				else 
					comboOutLevels.setSelectedIndex(1);
			}
			
		});	
		
		JLabel lblTo = new JLabel("->");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTo.setBounds(comboInLevels.getX() + comboInLevels.getWidth() + 4, caseLevels.getLocation().y + 4, lblTo.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo);
		
		comboOutLevels = new JComboBox<Object>(new String[] {"16-235", "0-255"});		
		comboOutLevels.setName("comboOutLevels");
		comboOutLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutLevels.setEditable(false);
		comboOutLevels.setEnabled(false);
		comboOutLevels.setSelectedIndex(1);
		comboOutLevels.setMaximumRowCount(20);
		comboOutLevels.setBounds(lblTo.getX() + lblTo.getWidth() + 4, caseLevels.getLocation().y + 4, 60, 16);
		grpColorimetry.add(comboOutLevels);
		
		comboOutLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboOutLevels.getSelectedIndex() == 1)
					comboInLevels.setSelectedIndex(0);
				else 
					comboInLevels.setSelectedIndex(1);
			}
			
		});	
		
		caseColormatrix = new JRadioButton(language.getProperty("caseColormatrix"));
		caseColormatrix.setName("caseColormatrix");
		caseColormatrix.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseColormatrix.setBounds(7, caseLevels.getLocation().y + caseLevels.getHeight(), caseColormatrix.getPreferredSize().width, 22);
		grpColorimetry.add(caseColormatrix);
		
		caseColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseColormatrix.isSelected()) 
				{
					comboInColormatrix.setEnabled(true);
					comboOutColormatrix.setEnabled(true);
				}
				else
				{
					comboInColormatrix.setEnabled(false);
					comboOutColormatrix.setEnabled(false);	
				}
			}
			
		});
		
		comboInColormatrix = new JComboBox<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "HDR"});		
		comboInColormatrix.setName("comboInColormatrix");
		comboInColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		//comboInColormatrix.setLayout(null);
		comboInColormatrix.setEditable(false);
		comboInColormatrix.setEnabled(false);
		comboInColormatrix.setSelectedIndex(0);
		comboInColormatrix.setMaximumRowCount(20);
		comboInColormatrix.setBounds(grpColorimetry.getWidth() - 160 - 7, caseColormatrix.getLocation().y + 4, 70, 16);
		grpColorimetry.add(comboInColormatrix);
		
		comboInColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 601"))
				{
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020", "SDR"}));
				}
				else if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 709"))
				{
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 2020", "SDR"}));
					comboOutColormatrix.setSelectedIndex(1);
				}
				else if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 2020"))
				{
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 709", "SDR"}));
					comboOutColormatrix.setSelectedIndex(1);
				}				
				else if (comboInColormatrix.getSelectedItem().toString().equals("HDR"))
				{
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"SDR"}));
					comboOutColormatrix.setSelectedIndex(0);
				}
			}
			
		});	
		
		JLabel lblTo2 = new JLabel("->");
		lblTo2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTo2.setBounds(comboInColormatrix.getX() + comboInColormatrix.getWidth() + 4, caseColormatrix.getLocation().y + 4, lblTo2.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo2);
		
		comboOutColormatrix = new JComboBox<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "SDR"});		
		comboOutColormatrix.setName("comboOutColormatrix");
		comboOutColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		//comboOutColormatrix.setLayout(null);
		comboOutColormatrix.setEditable(false);
		comboOutColormatrix.setEnabled(false);
		comboOutColormatrix.setSelectedIndex(1);
		comboOutColormatrix.setMaximumRowCount(20);
		comboOutColormatrix.setBounds(grpColorimetry.getWidth() - 70 - 7, caseColormatrix.getLocation().y + 4, 70, 16);
		grpColorimetry.add(comboOutColormatrix);
		
		comboOutColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboOutColormatrix.getSelectedItem().toString().equals("SDR"))
				{
					comboInColormatrix.setSelectedIndex(3);
				}
			}
			
		});	
		
		caseColorspace = new JRadioButton(language.getProperty("caseColorspace"));
		caseColorspace.setName("caseColorspace");
		caseColorspace.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseColorspace.setBounds(7, caseColormatrix.getLocation().y + caseColormatrix.getHeight(), caseColorspace.getPreferredSize().width, 22);
		grpColorimetry.add(caseColorspace);
		
		caseColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseColorspace.isSelected()) 
				{
					comboColorspace.setEnabled(true);
					
					if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("12bits") || comboColorspace.getSelectedItem().toString().contains("422")))
					{
						caseAlpha.setSelected(false);
						caseAlpha.setEnabled(false);			
					}
					
					if (comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1") || comboFonctions.getSelectedItem().toString().equals("H.265") )
					{
						if (comboColorspace.getSelectedItem().toString().contains("HDR"))
						{
							if (comboFilter.getModel().getSize() != 1)
							{
								final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mkv"});						
								comboFilter.setModel(model);
							}
						}
					}
				} 
				else
				{
					comboColorspace.setEnabled(false);	
					caseAlpha.setEnabled(true);
					
					if (comboFonctions.getSelectedItem().toString().equals("VP8"))
					{
						if (comboFilter.getModel().getSize() != 2)
						{
							String filtres[] = {".webm", ".mkv" };
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					}
					else if (comboFonctions.getSelectedItem().toString().equals("VP9"))
					{
						if (comboFilter.getModel().getSize() != 3)
						{
							String filtres[] = {".webm", ".mkv", ".mp4" };
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					}
					else if (comboFonctions.getSelectedItem().toString().equals("AV1"))
					{
						if (comboFilter.getModel().getSize() != 3)
						{
							String filtres[] = {".mkv", ".mp4", ".webm"};	
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					}
					else if (comboFonctions.getSelectedItem().toString().equals("H.265"))
					{
						if (comboFilter.getModel().getSize() != 9)
						{
							String filtres[] = { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts", ".m2ts" };
							DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
								comboFilter.setModel(model);
								comboFilter.setSelectedIndex(0);
							}
						}
					}
				}
			}
			
		});
		
		comboColorspace = new JComboBox<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"});		
		comboColorspace.setName("comboColorspace");
		comboColorspace.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboColorspace.setEditable(false);
		comboColorspace.setEnabled(false);
		comboColorspace.setSelectedIndex(0);
		comboColorspace.setMaximumRowCount(20);
		comboColorspace.setBounds(grpColorimetry.getWidth() - 160 - 7, caseColorspace.getLocation().y + 4, 160, 16);
		grpColorimetry.add(comboColorspace);
		
		comboColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("12bits") || comboColorspace.getSelectedItem().toString().contains("422")))
				{
					caseAlpha.setSelected(false);
					caseAlpha.setEnabled(false);	
				}				
				else
					caseAlpha.setEnabled(true);
				
				if ((comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1") || comboFonctions.getSelectedItem().toString().equals("H.265")) && caseColorspace.isSelected())
				{
					if (comboColorspace.getSelectedItem().toString().contains("HDR"))
					{
						if (comboFilter.getModel().getSize() != 1)
						{
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mkv"});						
							comboFilter.setModel(model);
						}
					}
					else
					{
						if (comboFonctions.getSelectedItem().toString().equals("VP8"))
						{
							if (comboFilter.getModel().getSize() != 2)
							{
								String filtres[] = {".webm", ".mkv" };
								final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
								comboFilter.setModel(model);
								comboFilter.setSelectedIndex(0);
							}
						}
						else if (comboFonctions.getSelectedItem().toString().equals("VP9"))
						{
							if (comboFilter.getModel().getSize() != 3)
							{
								String filtres[] = {".webm", ".mkv", ".mp4" };
								final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
								comboFilter.setModel(model);
								comboFilter.setSelectedIndex(0);
							}
						}
						else if (comboFonctions.getSelectedItem().toString().equals("AV1"))
						{
							if (comboFilter.getModel().getSize() != 3)
							{
								String filtres[] = {".mkv", ".mp4", ".webm"};	
								final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
								comboFilter.setModel(model);
								comboFilter.setSelectedIndex(0);
							}
						}
						else if (comboFonctions.getSelectedItem().toString().equals("H.265"))
						{
							if (comboFilter.getModel().getSize() != 9)
							{
								String filtres[] = { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts", ".m2ts" };
								DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
								if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
									comboFilter.setModel(model);
									comboFilter.setSelectedIndex(0);
								}
							}
						}
					}					
					
				}
			}
		
		});
		
		caseLUTs = new JRadioButton(language.getProperty("caseLUTs"));
		caseLUTs.setName("caseLUTs");
		caseLUTs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLUTs.setBounds(7, caseColorspace.getLocation().y + caseColorspace.getHeight(), caseLUTs.getPreferredSize().width, 22);
		grpColorimetry.add(caseLUTs);

		ArrayList<Object> LUTs = new ArrayList<Object>();

		String PathToLUTs = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			PathToLUTs = PathToLUTs.substring(0, PathToLUTs.length() - 1);
		else
			PathToLUTs = PathToLUTs.substring(1,PathToLUTs.length() - 1);
			
		PathToLUTs = PathToLUTs.substring(0, (int) (PathToLUTs.lastIndexOf("/"))).replace("%20", " ") + "/LUTs";
		lutsFolder = new File(PathToLUTs);

		if (lutsFolder.exists() == false)
			lutsFolder.mkdir();
		else {
			File[] luts = lutsFolder.listFiles();
			String[] data = new String[luts.length]; 

	        for (int i = 0 ; i < luts.length ; i++) { 
	           data[i] = (String) luts[i].toString(); 
	        }

	        Arrays.sort(data); 
	        LUTs.clear();

	        for (int i = 0 ; i < data.length ; i++) 
	        {
	        	if (new File(data[i].toString()).isHidden() == false && new File(data[i].toString()).getName().equals("HDR-to-SDR.cube") == false)
	        		LUTs.add(new File(data[i].toString()).getName());
		    }
		}

		JButton btnLUTs = new JButton(language.getProperty("btnManage"));
		btnLUTs.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnLUTs.setBounds(comboColorspace.getX(), caseLUTs.getY() + 1, grpColorimetry.getWidth() - comboColorspace.getX() - 6, 21);
		grpColorimetry.add(btnLUTs);

		btnLUTs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(lutsFolder);
				} catch (IOException e1) {
				}

				if (caseLUTs.isSelected())
					caseLUTs.doClick();
			}

		});

		comboLUTs = new JComboBox<Object>(LUTs.toArray());
		comboLUTs.setName("comboLUTs");
		comboLUTs.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboLUTs.setEditable(false);
		comboLUTs.setEnabled(false);
		comboLUTs.setMaximumRowCount(20);
		comboLUTs.setBounds(7, caseLUTs.getLocation().y + caseLUTs.getHeight() + 7, grpColorimetry.getWidth() - 38, 22);
		grpColorimetry.add(comboLUTs);

		iconTVLUTs = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVLUTs.setToolTipText(language.getProperty("preview"));
		iconTVLUTs.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVLUTs.setSize(16, 16);
		iconTVLUTs.setLocation(287, comboLUTs.getLocation().y + 3);
		grpColorimetry.add(iconTVLUTs);

		iconTVLUTs.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (caseLUTs.isSelected())
				{
					String pathToLuts;
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					{
						pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
						pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
					}
					else
						pathToLuts = "LUTs/";
	
					String lut3d = "lut3d=file=" + '"' + pathToLuts + comboLUTs.getSelectedItem().toString() + '"';				
					
					FFPLAY.previewFilters(lut3d, false);
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
				iconTVLUTs.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVLUTs.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		caseLUTs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				File[] luts = lutsFolder.listFiles();
				String[] data = new String[luts.length]; 

		        for (int i = 0 ; i < luts.length ; i++) { 
		           data[i] = (String) luts[i].toString(); 
		        }

		        Arrays.sort(data); 
		        LUTs.clear();

				if (caseLUTs.isSelected()) {
					
			        for (int i = 0 ; i < data.length ; i++)
			        { 
			        	File lut = new File(data[i].toString());
			        	
			        	if (lut.isHidden() == false && new File(data[i].toString()).getName().equals("HDR-to-SDR.cube") == false)
			        	{			        		
			        		if (lut.getName().contains(" "))
			        		{	
			        			File newLutName = new File(lutsFolder + "/" + lut.getName().replace(" ", "-"));
			        			lut.renameTo(newLutName);			
			        			
			        			if (newLutName.exists())
			        				LUTs.add(newLutName.getName());
			        			else
			        				LUTs.add(lut.getName());
			        		}
			        		else
			        			LUTs.add(lut.getName());
			        	}
				    }
					
					comboLUTs.setModel(new DefaultComboBoxModel<Object>(LUTs.toArray()));
					comboLUTs.setEnabled(true);
				} else
					comboLUTs.setEnabled(false);
			}

		});
	}
	
	private void grpSetTimecode() {
		grpSetTimecode = new JPanel();
		grpSetTimecode.setLayout(null);
		grpSetTimecode.setVisible(false);
		grpSetTimecode.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), language.getProperty("grpTimecode") + " ",
				0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpSetTimecode.setBackground(new Color(50, 50, 50));
		grpSetTimecode.setBounds(334, 258, 312, 17);
		frame.getContentPane().add(grpSetTimecode);

		caseSetTimecode = new JRadioButton(language.getProperty("caseSetTimecode"));
		caseSetTimecode.setName("caseSetTimecode");
		caseSetTimecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSetTimecode.setBounds(7, 16, 143, 23);
		grpSetTimecode.add(caseSetTimecode);

		grpSetTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 93;

				final int sized = size;
				if (grpSetTimecode.getSize().height < sized) {
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

										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))) 
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											grpAdvanced.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											btnReset.setLocation(336, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut")))
										{
											grpSetTimecode.setSize(312, i);
											btnReset.setLocation(336, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
										}
										else
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
										
										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))) 
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											grpAdvanced.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											btnReset.setLocation(336, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
										}
										else
										{
											grpSetTimecode.setSize(312, i);
											grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										
										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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
		
		caseSetTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseSetTimecode.isSelected()) {
					TCset1.setEnabled(true);
					TCset2.setEnabled(true);
					TCset3.setEnabled(true);
					TCset4.setEnabled(true);
					if (caseInAndOut.isSelected()) {
						TCset1.setText(VideoPlayer.caseInH.getText());
						TCset2.setText(VideoPlayer.caseInM.getText());
						TCset3.setText(VideoPlayer.caseInS.getText());
						TCset4.setText(VideoPlayer.caseInF.getText());
					}
					
					caseIncrementTimecode.setEnabled(true);
					caseGenerateFromDate.setSelected(false);
					caseGenerateFromDate.setEnabled(false);
					
				} else {
					TCset1.setEnabled(false);
					TCset2.setEnabled(false);
					TCset3.setEnabled(false);
					TCset4.setEnabled(false);
					caseIncrementTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);					
					caseGenerateFromDate.setEnabled(true);
				}

			}

		});

		TCset1 = new JTextField();
		TCset1.setName("TCset1");
		TCset1.setEnabled(false);
		TCset1.setText("00");
		TCset1.setHorizontalAlignment(SwingConstants.CENTER);
		TCset1.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		TCset1.setColumns(10);
		TCset1.setBounds(156, 17, 32, 21);
		grpSetTimecode.add(TCset1);

		TCset2 = new JTextField();
		TCset2.setName("TCset2");
		TCset2.setEnabled(false);
		TCset2.setText("00");
		TCset2.setHorizontalAlignment(SwingConstants.CENTER);
		TCset2.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		TCset2.setColumns(10);
		TCset2.setBounds(192, 17, 32, 21);
		grpSetTimecode.add(TCset2);

		TCset3 = new JTextField();
		TCset3.setName("TCset3");
		TCset3.setEnabled(false);
		TCset3.setText("00");
		TCset3.setHorizontalAlignment(SwingConstants.CENTER);
		TCset3.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		TCset3.setColumns(10);
		TCset3.setBounds(228, 17, 32, 21);
		grpSetTimecode.add(TCset3);

		TCset4 = new JTextField();
		TCset4.setName("TCset4");
		TCset4.setEnabled(false);
		TCset4.setText("00");
		TCset4.setHorizontalAlignment(SwingConstants.CENTER);
		TCset4.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		TCset4.setColumns(10);
		TCset4.setBounds(264, 17, 32, 21);
		grpSetTimecode.add(TCset4);

		TCset1.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset1.getText().length() >= 2)
					TCset1.setText("");

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		TCset2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset2.getText().length() >= 2)
					TCset2.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		TCset3.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset3.getText().length() >= 2)
					TCset3.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		TCset4.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset4.getText().length() >= 2)
					TCset4.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		caseIncrementTimecode = new JRadioButton(language.getProperty("caseIncrementTimecode"));
		caseIncrementTimecode.setName("caseIncrementTimecode");
		caseIncrementTimecode.setEnabled(false);
		caseIncrementTimecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseIncrementTimecode.setBounds(7, caseSetTimecode.getY() + caseSetTimecode.getHeight(), caseIncrementTimecode.getPreferredSize().width, 23);
		grpSetTimecode.add(caseIncrementTimecode);
		
		caseGenerateFromDate = new JRadioButton(language.getProperty("caseGenerateFromDate"));
		caseGenerateFromDate.setName("caseGenerateFromDate");
		caseGenerateFromDate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseGenerateFromDate.setBounds(7, caseIncrementTimecode.getY() + caseIncrementTimecode.getHeight(), caseGenerateFromDate.getPreferredSize().width + 4, 23);
		grpSetTimecode.add(caseGenerateFromDate);
		
		caseGenerateFromDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				 if (caseGenerateFromDate.isSelected())
				 {
					 caseSetTimecode.setSelected(false);
					 caseSetTimecode.setEnabled(false);
					 caseIncrementTimecode.setSelected(false);
					 caseIncrementTimecode.setEnabled(false);
				 }			
				 else
				 {
					 caseSetTimecode.setEnabled(true);
				 }
			}
			
		});
	}

	private void grpOverlay() {
		grpOverlay = new JPanel();
		grpOverlay.setLayout(null);
		grpOverlay.setVisible(false);
		grpOverlay.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), language.getProperty("grpOverlay") + " ", 0,
				0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpOverlay.setBackground(new Color(50, 50, 50));
		grpOverlay.setBounds(334, 258, 312, 17);
		frame.getContentPane().add(grpOverlay);

		grpOverlay.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 93;
				if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
						|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
						|| comboFonctions.getSelectedItem().toString().equals("XAVC")
						|| comboFonctions.getSelectedItem().toString().equals("HAP")
						|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
				{
					size = 69;
				}
				else if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
					size = 47;
				
				final int sized = size;	
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

										if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
										{
											grpOverlay.setSize(312, i);
											btnReset.setLocation(336, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpOverlay.setSize(312, i);
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpImageFilter.setLocation(334, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);											
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray")) 
										{
											grpOverlay.setSize(312, i);
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}										
										else
										{
											grpOverlay.setSize(312, i);
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
										
										if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
										{
											grpOverlay.setSize(312, i);
											btnReset.setLocation(336, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpOverlay.setSize(312, i);
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpImageFilter.setLocation(334, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											btnReset.setLocation(336, grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);											
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray")) 
										{
											grpOverlay.setSize(312, i);
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										else
										{
											grpOverlay.setSize(312, i);
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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
		
		caseAddOverlay = new JRadioButton(language.getProperty("caseAddOverlay"));
		caseAddOverlay.setName("caseAddOverlay");
		caseAddOverlay.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAddOverlay.setSize(caseAddOverlay.getPreferredSize().width, 23);

		caseAddOverlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseAddOverlay.isSelected()) {
					
					if (liste.getSize() == 0) 
					{
						JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
								language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseAddOverlay.setSelected(false);
					}
					else
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						
						if (OverlayWindow.frame == null)
						{
							new OverlayWindow();
						}
						else
						{
							if (scanIsRunning)
							{
								File dir = new File(liste.firstElement());
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
									FFPROBE.Data(liste.firstElement());
							}
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e1) {}
							} while (FFPROBE.isRunning);
							
							OverlayWindow.caseShowTimecode.setEnabled(true);
							OverlayWindow.positionVideo.setValue(0);
							OverlayWindow.positionVideo.setMaximum(FFPROBE.totalLength);
							OverlayWindow.loadImage("0","0","0", true);	
						}
							
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {
	
							@Override
							public void run() {
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException er) {}
								} while (OverlayWindow.frame.isVisible());
								
								frame.setOpacity(1.0f);
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
							}
						});
						t.start();
					}
					else
					{
						frame.setOpacity(1.0f);
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
					}
							
				}
			}

		});
		
		caseShowDate = new JRadioButton(language.getProperty("caseShowDate"));
		caseShowDate.setName("caseShowDate");
		caseShowDate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseShowDate.setSize(caseShowDate.getPreferredSize().width, 23);
		
		caseShowFileName = new JRadioButton(Shutter.language.getProperty("caseShowFileName"));
		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
		
		caseSubtitles = new JRadioButton(language.getProperty("caseSubtitles"));
		caseSubtitles.setName("caseSubtitles");
		caseSubtitles.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSubtitles.setSize(caseSubtitles.getPreferredSize().width + 4, 23);

		caseSubtitles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseSubtitles.setSelected(false);
				}
				
				if (caseSubtitles.isSelected())
				{
					if (liste.getSize() == 0) {
						JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
								language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseSubtitles.setSelected(false);
					} 
					else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionSubtitles")))
					{
						if (System.getProperty("os.name").contains("Windows"))
							subtitlesFile = new File(SubtitlesTimeline.srt.getName());
						else
							subtitlesFile = new File(dirTemp + SubtitlesTimeline.srt.getName());
									            
						Object[] options = {language.getProperty("subtitlesBurn"), language.getProperty("subtitlesEmbed")};
						
						int sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSubsIntegration"), language.getProperty("caseSubtitles"),
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							    options,
							    options[0]);
			            
						if (sub == 0)
						{
							subtitlesBurn = true;
							SubtitlesWindow.subtitlesFile = SubtitlesTimeline.srt.toString();				            
				            writeSub(SubtitlesWindow.subtitlesFile, StandardCharsets.UTF_8);
						}
						else
						{
							subtitlesBurn = false;
							subtitlesFile = new File(SubtitlesTimeline.srt.toString());
							changeSections(false);
				        	caseDisplay.setSelected(false);

							//On copy le .srt dans le fichier
							Thread copySRT = new Thread(new Runnable()
							{
								@Override
								public void run() {
									
									try {						
										
										disableAll();
										
										File fileIn = new File(liste.firstElement());
										String extension = fileIn.toString().substring(fileIn.toString().lastIndexOf("."));
										File fileOut = new File(fileIn.toString().replace(extension, "_subs" + extension));
										
										//Envoi de la commande
										String cmd = " -c copy -c:s mov_text -map v:0? -map a? -map 1:s -y ";
										
										if (extension.equals(".mkv"))
											cmd = " -c copy -c:s srt -map v:0? -map a? -map 1:s -y ";							
										
										FFMPEG.run(" -i " + '"' + fileIn + '"' + " -i " + '"' + subtitlesFile + '"' + cmd + '"'  + fileOut + '"');	
										
										lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
										lblCurrentEncoding.setText(fileIn.getName());
										
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
										if (cancelled)
											fileOut.delete();
										
										//Fichiers terminés
										if (cancelled == false && FFMPEG.error == false)
											lblFilesEnded.setText(FunctionUtils.completedFiles(1));
										
										//Ouverture du dossier
										if (caseOpenFolderAtEnd1.isSelected() && cancelled == false && FFMPEG.error == false)
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
										
									} catch (Exception e) {}	
									finally {
										enfOfFunction();
									}
									
								}						
							});
							copySRT.start();	
						}
					}
					else
					{
						File video = new File(liste.elementAt(0).toString());
						String ext = video.toString().substring(video.toString().lastIndexOf("."));
						
						FileDialog dialog = new FileDialog(frame, language.getProperty("chooseSubtitles"),	FileDialog.LOAD);
						
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
							dialog.setDirectory(new File(liste.elementAt(0).toString()).getParent());
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
									subtitlesFile = new File(dialog.getFile());
								else
									subtitlesFile = new File(dirTemp + dialog.getFile());
								
								if (input.equals(".srt") || input.equals(".vtt"))
								{
									if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())) 
									{
										SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
										
										try {
											frame.setOpacity(0.5f);
										} catch (Exception er) {}
										
										if (SubtitlesEmbed.frame == null)
											new SubtitlesEmbed();
										else
											Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

										frame.setOpacity(1.0f);											
										subtitlesBurn = false;
										changeSections(false);
							        	caseDisplay.setSelected(false);
									}
									else
									{
										Object[] options = {language.getProperty("subtitlesBurn"), language.getProperty("subtitlesEmbed")};
										
										int sub = 0;
										
										if (comboFilter.getSelectedItem().toString().equals(".mxf") == false
										&& comboFonctions.getSelectedItem().toString().equals("XAVC") == false
										&& caseCreateOPATOM.isSelected() == false)
										{
											sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSubsIntegration"), language.getProperty("caseSubtitles"),
													JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												    options,
												    options[0]);
										}
											
										if (sub == 0) //Burn
										{
											subtitlesBurn = true;							
																	
											//Conversion du .vtt en .srt
											if (input.equals(".vtt"))
											{	
												subtitlesFile = new File(subtitlesFile.toString().replace(".vtt", ".srt"));
												
												FFMPEG.run(" -i " + '"' + dialog.getDirectory() + dialog.getFile().toString() + '"' + " -y " + '"' + subtitlesFile.toString().replace(".srt", "_vtt.srt") + '"');
												
												//Attente de la fin de FFMPEG
												do
												{
													try {
														Thread.sleep(10);
													} catch (InterruptedException e) {}
												}
												while(FFMPEG.runProcess.isAlive());
												
												enableAll();
												
												SubtitlesWindow.subtitlesFile = subtitlesFile.toString().replace(".srt", "_vtt.srt");
											}
											else											
												SubtitlesWindow.subtitlesFile = dialog.getDirectory() + dialog.getFile().toString();			
											
											writeSub(SubtitlesWindow.subtitlesFile, StandardCharsets.UTF_8);										
										}
										else
										{											
											SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
											
											try {
												frame.setOpacity(0.5f);
											} catch (Exception er) {}
											
											if (SubtitlesEmbed.frame == null)
												new SubtitlesEmbed();
											else
												Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

											frame.setOpacity(1.0f);											
											subtitlesBurn = false;
											changeSections(false);
								        	caseDisplay.setSelected(false);
										}
									}
								}
								else //SSA or ASS or SCC
								{
									
									if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())) 
									{
										SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
										
										try {
											frame.setOpacity(0.5f);
										} catch (Exception er) {}
										
										if (SubtitlesEmbed.frame == null)
											new SubtitlesEmbed();
										else
											Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

										frame.setOpacity(1.0f);											
										subtitlesBurn = false;
										changeSections(false);
							        	caseDisplay.setSelected(false);
									}
									else
									{
										Object[] options = {language.getProperty("subtitlesBurn"), language.getProperty("subtitlesEmbed")};
										
										int sub = 0;
										
										if (comboFilter.getSelectedItem().toString().equals(".mxf") == false
										&& comboFonctions.getSelectedItem().toString().equals("XAVC") == false
										&& caseCreateOPATOM.isSelected() == false)
										{
											sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSubsIntegration"), language.getProperty("caseSubtitles"),
													JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												    options,
												    options[0]);
										}
											
										if (sub == 0) //Burn
										{
											subtitlesBurn = true;
											
											try {
												FileUtils.copyFile(new File(dialog.getDirectory() + dialog.getFile().toString()), subtitlesFile);
											} catch (IOException e) {}
										}
										else
										{
											SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());
											
											try {
												frame.setOpacity(0.5f);
											} catch (Exception er) {}
											
											if (SubtitlesEmbed.frame == null)
												new SubtitlesEmbed();
											else
												Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

											frame.setOpacity(1.0f);											
											subtitlesBurn = false;
											changeSections(false);
								        	caseDisplay.setSelected(false);
										}
									}
								}
							}
							else 
							{
								JOptionPane.showConfirmDialog(frame, language.getProperty("invalidSubtitles"),
										language.getProperty("subtitlesFileError"), JOptionPane.PLAIN_MESSAGE);
								caseSubtitles.setSelected(false);
							}
						} else
							caseSubtitles.setSelected(false);
					}
				} else {
					caseSubtitles.setSelected(false);
				}
			}
			
			private void writeSub(String srt, Charset encoding) 
			{
				
				try {

					BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(srt),  encoding);
		            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(subtitlesFile.toString()),  StandardCharsets.UTF_8);

		            String line;
		            boolean stop = false;
		            while((line = bufferedReader.readLine()) != null)
		            {
		            	//Removes UTF-8 with BOM
		            	line = line.replace("\uFEFF", "");
		            	
		            	if (line.matches("[0-9]+"))
		            	{
		            		if (stop == false)
		            		{
			            		bufferedWriter.write(line);
			            		bufferedWriter.newLine();
		            		}
		            	}
		            	else if (line.contains("-->"))
		            	{
		            		if (stop == false)
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
			    				
			    				SubtitlesWindow.positionVideo.setMinimum(inH+inM+inS+inF);
			    				SubtitlesWindow.positionVideo.setMaximum(outH+outM+outS+outF);
			    				SubtitlesWindow.positionVideo.setValue(SubtitlesWindow.positionVideo.getMinimum());				
		            		}
		            	}
		            	else if (line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
		            	{	            		
		            		if (stop == false)
		            		{
		            			if (SubtitlesWindow.frame != null && SubtitlesWindow.lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
		            				bufferedWriter.write("\\h" + line + "\\h");
		            			else
		            				bufferedWriter.write(line);
		            			
		            			bufferedWriter.newLine();
		            		}
		            	} 
		            	else if (line.isEmpty())						            		
		            		stop = true;

		            }   

		            bufferedReader.close();  
		            bufferedWriter.close();

					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}

					if (SubtitlesWindow.frame == null)
					{
						if (scanIsRunning)
						{
							File dir = new File(liste.firstElement());
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
				    		FFPROBE.Data(liste.firstElement());
						}
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						} while (FFPROBE.isRunning);
						
						new SubtitlesWindow();					
					}
					else
					{
						SubtitlesWindow.positionVideo.setValue(0);
						SubtitlesWindow.spinnerSubtitle.setValue(1);
						SubtitlesWindow.sliderChange(true);
					}
						
				} catch (Exception e) {
					
					if (encoding == StandardCharsets.UTF_8)
					{						
						writeSub(srt, StandardCharsets.ISO_8859_1);
					}
					else					
						caseSubtitles.setSelected(false);
				}
			}

		});

		String[] languages = Locale.getISOLanguages();
		String[] allLanguages = new String[languages.length];
		
		for (int i = 0; i < languages.length; i++)
		{
		    Locale loc = new Locale(languages[i]);
		    allLanguages[i] = loc.getDisplayLanguage();
		}
		
		caseLogo = new JRadioButton(language.getProperty("caseLogo"));
		caseLogo.setName("caseLogo");
		caseLogo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLogo.setSize(caseLogo.getPreferredSize().width, 23);
		
		caseLogo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				if (caseLogo.isSelected())
				{					
					boolean addDevice = false;
					if (inputDeviceIsRunning && liste.getElementAt(0).equals("Capture.current.screen") && System.getProperty("os.name").contains("Windows"))
					{
						int reply = JOptionPane.showConfirmDialog(frame, language.getProperty("addInputDevice"),
								language.getProperty("menuItemInputDevice"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);
						if (reply == JOptionPane.YES_OPTION)
						{
							addDevice = true;
							inputDevice.doClick();
						}						
					}
					
					if (liste.getSize() == 0) {
						JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
								language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseLogo.setSelected(false);
					}
					else if (addDevice)
					{
						//Nothing
					}
					else
					{
						FileDialog dialog = new FileDialog(frame, language.getProperty("chooseLogo"), FileDialog.LOAD);
						dialog.setDirectory(new File(liste.elementAt(0).toString()).getParent());
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						dialog.setMultipleMode(false);
						dialog.setVisible(true);

						if (dialog.getFile() != null) {
							WatermarkWindow.logoFile = dialog.getDirectory() + dialog.getFile().toString();
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							try {
								frame.setOpacity(0.5f);
							} catch (Exception er) {}

							if (WatermarkWindow.frame == null)
								new WatermarkWindow();
							else {
								WatermarkWindow.positionVideo.setValue(0);
								WatermarkWindow.loadImage("0", "0", "0", true, -1);
								Utils.changeDialogVisibility(WatermarkWindow.frame, false);
							}
							
							if (Functions.frame != null && Functions.frame.isVisible())
							{
								Thread t = new Thread (new Runnable() {
			
									@Override
									public void run() {
										do {
											try {
												Thread.sleep(10);
											} catch (InterruptedException er) {}
										} while (WatermarkWindow.frame.isVisible());
										
										frame.setOpacity(1.0f);
										frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
									}
								});
								t.start();
							}
							else
							{
								frame.setOpacity(1.0f);
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
							}
							
						} else {
							caseLogo.setSelected(false);
							
							if (overlayDeviceIsRunning)
								overlayDeviceIsRunning = false;
						}
					}
				}
			}

		});
	
	}

	private void grpInAndOut() {
		grpInAndOut = new JPanel();
		grpInAndOut.setLayout(null);
		grpInAndOut.setVisible(false);
		grpInAndOut.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpInAndOut") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpInAndOut.setBackground(new Color(50, 50, 50));
		grpInAndOut.setBounds(334, 343, 312, 47);
		frame.getContentPane().add(grpInAndOut);
		
		caseInAndOut = new JRadioButton(language.getProperty("changeInOutPoint"));
		caseInAndOut.setName("caseInAndOut");
		caseInAndOut.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseInAndOut.setBounds(7, 16, 291, 23);
		grpInAndOut.add(caseInAndOut);

		caseInAndOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseInAndOut.setSelected(false);
				}				
				
				if (liste.getSize() == 0 && VideoPlayer.frame != null && VideoPlayer.frame.isVisible() == false)
				{
					caseInAndOut.setSelected(false);
					JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"), language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
				}
				else {
					if (caseInAndOut.isSelected()) {
						
						if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
						|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString()))
						{
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"), comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
						}

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						if (VideoPlayer.waveform.exists())
							VideoPlayer.waveform.delete();
						
						VideoPlayer.playerHasBeenStopped = false;
						
						FFPROBE.Data(liste.firstElement());
						
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						} while (FFPROBE.isRunning);
						
						new VideoPlayer();														
						
					} else {
						Utils.changeFrameVisibility(VideoPlayer.frame, true);
						
						if (VideoPlayer.playerLeftVideo != null)
							VideoPlayer.playerLeftStop();
						
						if (VideoPlayer.playerRightVideo != null)
							VideoPlayer.playerRightStop();
						
						VideoPlayer.frame.getContentPane().removeAll();

						switch (Shutter.comboFonctions.getSelectedItem().toString()) {
						case "H.264":
						case "H.265":
						case "WMV":
						case "MPEG":
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
						
						if (FFMPEG.isRunning)
							FFMPEG.process.destroy();
						
						// VideoPlayer
						VideoPlayer.setMedia();
					}
				}
			}
		});

	}

	private void grpSetAudio() {
		grpSetAudio = new JPanel();
		grpSetAudio.setLayout(null);
		grpSetAudio.setVisible(false);
		grpSetAudio.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpAudio") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpSetAudio.setBackground(new Color(50, 50, 50));
		grpSetAudio.setBounds(334, 343, 312, 47);
		frame.getContentPane().add(grpSetAudio);
		
		grpSetAudio.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {		
				
				if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false) 
				{
					int sized;
					if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
					|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()))
					{
						sized = 128;
					}
					else if (comboFonctions.getSelectedItem().toString().equals("DNxHD") 
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV")
							|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
							|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
							|| comboFonctions.getSelectedItem().toString().equals("XAVC")
							|| comboFonctions.getSelectedItem().toString().equals("HAP")
							|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
					{
						sized = 100;
					}
					else //Codecs de sortie
					{
						if (lblAudioMapping.getText().equals("Multi"))
							sized = 128;
						else
							sized = 74;
					}
					if (grpSetAudio.getSize().height < sized) {
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
										int i = 17;
										int sized;
										if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
										|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
										|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString())
										|| language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
										|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
										{
											sized = 128;
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DNxHD") 
												|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
												|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
												|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
												|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
												|| comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV")
												|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
												|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
												|| comboFonctions.getSelectedItem().toString().equals("XAVC")
												|| comboFonctions.getSelectedItem().toString().equals("HAP")
												|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
										{
											sized = 100;
										}
										else //Codecs de sortie
										{
											if (lblAudioMapping.getText().equals("Multi"))
												sized = 128;
											else
												sized = 74;
										}
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = sized;
											else
												i ++;
	
											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
											|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionMerge"))) 
											{
												grpSetAudio.setSize(312, i);
												grpAdvanced.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetAudio.setSize(312, i);
												btnReset.setLocation(336, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")) == false) //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
												grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
	
											if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
												grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y - 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y - 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y - 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y - 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y - 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y - 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y - 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y - 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y - 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y - 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y - 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i < sized);
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
										int i;
										if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
											|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
											|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString())
											|| language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
											|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
										{
											i = 128;
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DNxHD") 
												|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
												|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
												|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
												|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
												|| comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV")
												|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
												|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
												|| comboFonctions.getSelectedItem().toString().equals("XAVC")
												|| comboFonctions.getSelectedItem().toString().equals("HAP")
												|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
										{
											i = 100;
										}
										else //Codecs de sortie
										{
											if (lblAudioMapping.getText().equals("Multi"))
												i = 128;
											else
												i = 74;
										}
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 17;
											else
												i --;
											
											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
											|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionMerge"))) 
											{
												grpSetAudio.setSize(312, i);
												grpAdvanced.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetAudio.setSize(312, i);
												btnReset.setLocation(336, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")) == false) //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
												grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
	
											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 17);
									} catch (Exception e1) {
									}
							}
						});
						changeSize.start();
					}					
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
		
		caseChangeAudioCodec = new JRadioButton(language.getProperty("caseAudioCodec"));
		caseChangeAudioCodec.setName("caseChangeAudioCodec");
		caseChangeAudioCodec.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseChangeAudioCodec.setBounds(7, 16, caseChangeAudioCodec.getPreferredSize().width, 23);
		grpSetAudio.add(caseChangeAudioCodec);
		
		caseChangeAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseChangeAudioCodec.isSelected())
				{
					comboAudioCodec.setEnabled(true);					
					comboAudioBitrate.setEnabled(true);
				}
				else
				{
					comboAudioCodec.setEnabled(false);					
					comboAudioBitrate.setEnabled(false);
				}
				
				if (comboAudioCodec.getSelectedItem().toString().contains("PCM") || comboAudioCodec.getSelectedItem().toString().contains("FLAC"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"1536"}));
					comboAudioBitrate.setSelectedIndex(0);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "320", "256", "192", "128", "96", "64"}));
					comboAudioBitrate.setSelectedIndex(1);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "640", "448", "384", "320", "256", "192", "128", "96", "64"}));					
					comboAudioBitrate.setSelectedIndex(2);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"0"}));					
					comboAudioBitrate.setSelectedIndex(0);
				}			
			}

		});
		
		comboAudioCodec = new JComboBox<String>();
		comboAudioCodec.setName("comboAudioCodec");
		comboAudioCodec.setEnabled(false);
		comboAudioCodec.setMaximumRowCount(20);
		comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "AC3", "OPUS", "OGG", language.getProperty("noAudio") }));
		comboAudioCodec.setSelectedIndex(3);
		comboAudioCodec.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudioCodec.setEditable(false);
		comboAudioCodec.setSize(82, 16);
		comboAudioCodec.setLocation(caseChangeAudioCodec.getLocation().x + caseChangeAudioCodec.getWidth() + 7, caseChangeAudioCodec.getLocation().y + 3);
		grpSetAudio.add(comboAudioCodec);
					
		comboAudioBitrate = new JComboBox<String>();
		comboAudioBitrate.setName("comboAudioBitrate");
		comboAudioBitrate.setEnabled(false);
		comboAudioBitrate.setMaximumRowCount(20);
		comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "1536"}));
		comboAudioBitrate.setSelectedIndex(0);
		comboAudioBitrate.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudioBitrate.setEditable(false);
		comboAudioBitrate.setSize(53, 16);
		comboAudioBitrate.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		grpSetAudio.add(comboAudioBitrate);
		
		lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblKbs.setBounds(comboAudioBitrate.getLocation().x + comboAudioBitrate.getWidth() + 3, caseChangeAudioCodec.getLocation().y + 3, 33, 16);
		grpSetAudio.add(lblKbs);
		
		caseAudioOffset = new JRadioButton(language.getProperty("caseAudioOffset"));
		caseAudioOffset.setName("caseAudioOffset");
		caseAudioOffset.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAudioOffset.setBounds(7, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight(), caseAudioOffset.getPreferredSize().width + 4, 23);
		grpSetAudio.add(caseAudioOffset);
		
		caseAudioOffset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseAudioOffset.isSelected())
				{
					txtAudioOffset.setEnabled(true);	
				}
				else
				{
					txtAudioOffset.setEnabled(false);								
				}
				
				Utils.textFieldBackground();
			}

		});
				
		txtAudioOffset = new JTextField("0");
		txtAudioOffset.setName("txtAudioOffset");
		txtAudioOffset.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		txtAudioOffset.setSize(32, 16);
		txtAudioOffset.setEnabled(false);
		txtAudioOffset.setLocation(caseAudioOffset.getLocation().x + caseAudioOffset.getWidth() + 7, caseAudioOffset.getLocation().y + 4);
		grpSetAudio.add(txtAudioOffset);

		txtAudioOffset.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' && caracter != '-' || txtAudioOffset.getText().length() >= 4)
				{
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
			
		lblOffsetFPS = new JLabel(Shutter.language.getProperty("lblFrames"));
		lblOffsetFPS.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblOffsetFPS.setBounds(txtAudioOffset.getLocation().x + txtAudioOffset.getWidth() + 3, caseAudioOffset.getLocation().y + 4, lblOffsetFPS.getPreferredSize().width, 16);
		grpSetAudio.add(lblOffsetFPS);
				
		iconTVOffset = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVOffset.setToolTipText(language.getProperty("preview"));
		iconTVOffset.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVOffset.setSize(16, 16);
		iconTVOffset.setLocation(lblOffsetFPS.getX() + lblOffsetFPS.getWidth() + 6, lblOffsetFPS.getLocation().y);
		grpSetAudio.add(iconTVOffset);
		
		iconTVOffset.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
								
				if (liste.getSize() >= 2)
				{					
					String videoFile = "";
					String audioFile = "";
					
					//Analyse du stream		
					if (FFPROBE.FindStreams(liste.getElementAt(1)))
					{
						videoFile = liste.getElementAt(1);
						audioFile = liste.getElementAt(0);
					}
					else
					{
						videoFile = liste.getElementAt(0);		
						audioFile = liste.getElementAt(1);
					}		
					
					FFPROBE.Data(videoFile);
					
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.isRunning);
					
					float offset = (float) ((float) Integer.parseInt(txtAudioOffset.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000;
									
					FFPROBE.Data(audioFile);
							
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.isRunning);
	
					String channels = "";
					String videoOutput = "";
					String audioOutput = "";
					if (FFPROBE.channels > 1) {
						int i;
						for (i = 0; i < FFPROBE.channels; i++) {
							channels += "[1:a:" + i + "]showvolume=f=0.001:b=4:w=1080:h=12[a" + i + "];";
							audioOutput += "[a" + i + "]";
						}
						audioOutput += "vstack=" + (i + 1) + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					} else if (FFPROBE.channels == 1) {
						channels = "[1:a:0]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}

					// On ajoute la vidéo
					videoOutput = "[0:v]scale=1080:-1[v]" + ";" + channels + "[v]";
	
					String cmd = " -filter_complex " + '"' + videoOutput + audioOutput
							+ " -c:v rawvideo -map 1:a -f nut pipe:play |";
	
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
										
					FFMPEG.toFFPLAY(" -i " + '"' + videoFile + '"' + " -itsoffset " + offset + " -i " + '"' + audioFile + '"' + cmd);
	
					if (FFMPEG.isRunning) {
						do {
							if (FFMPEG.error) {
								JOptionPane.showConfirmDialog(frame, language.getProperty("cantReadFile"),
										language.getProperty("menuItemVisualiser"), JOptionPane.PLAIN_MESSAGE,
										JOptionPane.ERROR_MESSAGE);
								break;
							}
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {
							}
						} while (FFMPEG.isRunning && FFMPEG.error == false);
					}
	
					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();
	
					enableAll();
					progressBar1.setValue(0);
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
				iconTVOffset.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVOffset.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		lblAudioMapping = new JLabel(language.getProperty("stereo"));
		lblAudioMapping.setName("lblAudioMapping");
		lblAudioMapping.setBackground(new Color(80, 80, 80));
		lblAudioMapping.setHorizontalAlignment(SwingConstants.CENTER);
		lblAudioMapping.setOpaque(true);
		lblAudioMapping.setFont(new Font(montserratFont, Font.PLAIN, 11));
		lblAudioMapping.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		lblAudioMapping.setSize(lblKbs.getLocation().x + lblKbs.getSize().width - 5 - 7 - (comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7) , 16);
		grpSetAudio.add(lblAudioMapping);
		
		lblAudioMapping.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
				{
					lblAudioMapping.setText("Multi");
					
					grpSetAudio.add(lblAudio3);
					grpSetAudio.add(comboAudio3);
					grpSetAudio.add(lblAudio4);
					grpSetAudio.add(comboAudio4);
					grpSetAudio.add(lblAudio5);
					grpSetAudio.add(comboAudio5);
					grpSetAudio.add(lblAudio6);
					grpSetAudio.add(comboAudio6);
					grpSetAudio.add(lblAudio7);
					grpSetAudio.add(comboAudio7);
					grpSetAudio.add(lblAudio8);
					grpSetAudio.add(comboAudio8);
					
					grpSetAudio.repaint();
					
					int	sized = 128;
					if (grpSetAudio.getSize().height < sized) {
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
										int i = 74;
										int sized = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = sized;
											else
												i ++;
											
											if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
												grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
												
											if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
												grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y - 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y - 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y - 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y - 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y - 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y - 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y - 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y - 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y - 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y - 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y - 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i < sized);
								} catch (Exception e1) {
								}
							}
						});
						changeSize.start();
					}
				}
				else
				{
					if (lblAudioMapping.getText().equals("Multi"))
						lblAudioMapping.setText(language.getProperty("mono"));
					else
						lblAudioMapping.setText(language.getProperty("stereo"));
										
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
												grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											
											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}
									grpSetAudio.remove(lblAudio3);
									grpSetAudio.remove(comboAudio3);
									grpSetAudio.remove(lblAudio4);
									grpSetAudio.remove(comboAudio4);
									grpSetAudio.remove(lblAudio5);
									grpSetAudio.remove(comboAudio5);
									grpSetAudio.remove(lblAudio6);
									grpSetAudio.remove(comboAudio6);
									grpSetAudio.remove(lblAudio7);
									grpSetAudio.remove(comboAudio7);
									grpSetAudio.remove(lblAudio8);
									grpSetAudio.remove(comboAudio8);
									
									grpSetAudio.repaint();
							}
						});
						changeSize.start();
					}					
				}
				
				try {
					FFPROBE.setTailleH264();
				} catch (Exception e1) {}
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
				
		lbl48k = new JLabel("48k");
		lbl48k.setName("lbl48k");
		lbl48k.setBackground(new Color(80, 80, 80));
		lbl48k.setHorizontalAlignment(SwingConstants.CENTER);
		lbl48k.setOpaque(true);
		lbl48k.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lbl48k.setSize(36, 16);
		lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
		grpSetAudio.add(lbl48k);
		
		lbl48k.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				switch (lbl48k.getText())
				{
					case "8k" :
						lbl48k.setText("16k");
						break;
					case "16k" :
						lbl48k.setText("44.1k");
						break;
					case "44.1k" :
						lbl48k.setText("48k");
						break;
					case "48k" :
						lbl48k.setText("96k");
						break;
					case "96k" :
						lbl48k.setText("192k");
						break;
					case "192k" :
						lbl48k.setText("8k");
						break;
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

		comboAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboAudioCodec.getSelectedItem().toString().contains("PCM") || comboAudioCodec.getSelectedItem().toString().equals("FLAC"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"1536"}));
					comboAudioBitrate.setSelectedIndex(0);
					if (comboFonctions.getSelectedItem().toString().equals("MJPEG") || comboFonctions.getSelectedItem().toString().contains("H.26"))
					{
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(0);
					}			
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "320", "256", "192", "128", "96", "64"}));
					comboAudioBitrate.setSelectedIndex(1);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "640", "448", "384", "320", "256", "192", "128", "96", "64"}));					
					comboAudioBitrate.setSelectedIndex(2);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(2);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"0"}));					
					comboAudioBitrate.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(0);
				}	
				else if (comboAudioCodec.getSelectedItem().toString().equals("OPUS"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "256", "192", "128", "96", "64"}));
					comboAudioBitrate.setSelectedIndex(1);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);
				}
				else //Codecs de sortie
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "320", "256", "192", "128", "96", "64"}));
					comboAudioBitrate.setSelectedIndex(1);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);				
				}
				
				if (comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
						comboFilter.setSelectedIndex(1);
				}
			}
			
		});
		
		//Audio Mapping
		lblAudio1 = new JLabel(language.getProperty("audio") + " 1" + language.getProperty("colon"));
		lblAudio1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio1.setBounds(13, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7, lblAudio1.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio1);
		
		comboAudio1 = new JComboBox<String>();
		comboAudio1.setName("comboAudio1");
		comboAudio1.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio1.setSelectedIndex(0);
		comboAudio1.setMaximumRowCount(20);
		comboAudio1.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio1.setEditable(false);
		comboAudio1.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
		grpSetAudio.add(comboAudio1);
		
		comboAudio1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboAudio1.getSelectedIndex() == 16)
				{
					comboAudio2.setSelectedIndex(16);
					comboAudio3.setSelectedIndex(16);
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
					
					comboAudioCodec.setSelectedIndex(comboAudioCodec.getItemCount() - 1);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio2 = new JLabel(language.getProperty("audio") + " 2" + language.getProperty("colon"));
		lblAudio2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio2.setBounds(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y, lblAudio2.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio2);
		
		comboAudio2 = new JComboBox<String>();
		comboAudio2.setName("comboAudio2");
		comboAudio2.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio2.setSelectedIndex(1);
		comboAudio2.setMaximumRowCount(20);
		comboAudio2.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio2.setEditable(false);
		comboAudio2.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
		grpSetAudio.add(comboAudio2);
		
		comboAudio2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio2.getSelectedIndex() == 16)
				{
					comboAudio3.setSelectedIndex(16);
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio3 = new JLabel(language.getProperty("audio") + " 3" + language.getProperty("colon"));
		lblAudio3.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio3.setBounds(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2, lblAudio3.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio3);
		
		comboAudio3 = new JComboBox<String>();
		comboAudio3.setName("comboAudio3");
		comboAudio3.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio3.setSelectedIndex(2);
		comboAudio3.setMaximumRowCount(20);
		comboAudio3.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio3.setEditable(false);
		comboAudio3.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
		grpSetAudio.add(comboAudio3);
		
		comboAudio3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio3.getSelectedIndex() == 16)
				{
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio4 = new JLabel(language.getProperty("audio") + " 4" + language.getProperty("colon"));
		lblAudio4.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio4.setBounds(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2, lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio4);
		
		comboAudio4 = new JComboBox<String>();
		comboAudio4.setName("comboAudio4");
		comboAudio4.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio4.setSelectedIndex(3);
		comboAudio4.setMaximumRowCount(20);
		comboAudio4.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio4.setEditable(false);
		comboAudio4.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
		grpSetAudio.add(comboAudio4);
		
		comboAudio4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio4.getSelectedIndex() == 16)
				{
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio5 = new JLabel(language.getProperty("audio") + " 5" + language.getProperty("colon"));
		lblAudio5.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio5.setBounds(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2, lblAudio5.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio5);
		
		comboAudio5 = new JComboBox<String>();
		comboAudio5.setName("comboAudio5");
		comboAudio5.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio5.setSelectedIndex(4);
		comboAudio5.setMaximumRowCount(20);
		comboAudio5.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio5.setEditable(false);
		comboAudio5.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
		grpSetAudio.add(comboAudio5);
		
		comboAudio5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio5.getSelectedIndex() == 16)
				{
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio6 = new JLabel(language.getProperty("audio") + " 6" + language.getProperty("colon"));
		lblAudio6.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio6.setBounds(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2, lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio6);
		
		comboAudio6 = new JComboBox<String>();
		comboAudio6.setName("comboAudio6");
		comboAudio6.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio6.setSelectedIndex(5);
		comboAudio6.setMaximumRowCount(20);
		comboAudio6.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio6.setEditable(false);
		comboAudio6.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
		grpSetAudio.add(comboAudio6);
		
		comboAudio6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio6.getSelectedIndex() == 16)
				{
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio7 = new JLabel(language.getProperty("audio") + " 7" + language.getProperty("colon"));
		lblAudio7.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio7.setBounds(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2, lblAudio7.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio7);
		
		comboAudio7 = new JComboBox<String>();
		comboAudio7.setName("comboAudio7");
		comboAudio7.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio7.setSelectedIndex(6);
		comboAudio7.setMaximumRowCount(20);
		comboAudio7.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio7.setEditable(false);
		comboAudio7.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
		grpSetAudio.add(comboAudio7);
		
		comboAudio7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio7.getSelectedIndex() == 16)
				{
					comboAudio8.setSelectedIndex(16);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
				
		lblAudio8 = new JLabel(language.getProperty("audio") + " 8" + language.getProperty("colon"));
		lblAudio8.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio8.setBounds(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2, lblAudio8.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio8);
		
		comboAudio8 = new JComboBox<String>();
		comboAudio8.setName("comboAudio8");
		comboAudio8.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1", language.getProperty("audio") + " 2", language.getProperty("audio") + " 3", language.getProperty("audio") + " 4", language.getProperty("audio") + " 5", language.getProperty("audio") + " 6", language.getProperty("audio") + " 7", language.getProperty("audio") + " 8", language.getProperty("audio") + " 9", language.getProperty("audio") + " 10", language.getProperty("audio") + " 11", language.getProperty("audio") + " 12", language.getProperty("audio") + " 13", language.getProperty("audio") + " 14", language.getProperty("audio") + " 15", language.getProperty("audio") + " 16", language.getProperty("noAudio")}));
		comboAudio8.setSelectedIndex(7);
		comboAudio8.setMaximumRowCount(20);
		comboAudio8.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudio8.setEditable(false);
		comboAudio8.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
		grpSetAudio.add(comboAudio8);
		
		comboAudio8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
	}
	
	private void grpAudio() {
		grpAudio = new JPanel();
		grpAudio.setLayout(null);
		grpAudio.setVisible(false);
		grpAudio.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpAudio") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpAudio.setBackground(new Color(50, 50, 50));
		grpAudio.setBounds(334, 343, 312, 116);
		frame.getContentPane().add(grpAudio);
		
		caseMixAudio = new JRadioButton(language.getProperty("caseMixAudio"));
		caseMixAudio.setName("caseMixAudio");
		caseMixAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMixAudio.setBounds(7, 16, caseMixAudio.getPreferredSize().width, 23);
		caseMixAudio.setToolTipText(language.getProperty("tooltipMixAudio"));
		grpAudio.add(caseMixAudio);

		caseMixAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseMixAudio.isSelected())
				{
					caseSplitAudio.setSelected(false);
					
					if (lblMix.getText().equals("2.1"))
					{
						addToList.setText("<html>FL<br>FR<br>LFE</html>");
					}
					else if (lblMix.getText().equals("5.1"))
					{
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>SL<br>SR</html>");
					}
					else
						addToList.setText("<html>FL<br>FR<br>FL<br>FR<br>...<br>...</html>");						
				}
				else
					addToList.setText(language.getProperty("filesVideoOrAudio"));	
			}

		});
		
		lblMix = new JLabel(language.getProperty("stereo"));
		lblMix.setName("lblMix");
		lblMix.setBackground(new Color(80, 80, 80));
		lblMix.setHorizontalAlignment(SwingConstants.CENTER);
		lblMix.setOpaque(true);
		lblMix.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblMix.setBounds(caseMixAudio.getLocation().x + caseMixAudio.getWidth() + 5, caseMixAudio.getY() + 3, 46, 16);
		grpAudio.add(lblMix);
		
		lblMix.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblMix.getText().equals(language.getProperty("mono")))
				{
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FL<br>FR<br>...<br>...</html>");
					
					lblMix.setText(language.getProperty("stereo"));
				}
				else if (lblMix.getText().equals(language.getProperty("stereo")))
				{
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>LFE</html>");						
					
					lblMix.setText("2.1");
				} 
				else if (lblMix.getText().equals("2.1"))
				{
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>SL<br>SR</html>");
					
					lblMix.setText("5.1");
				} 
				else if (lblMix.getText().equals("5.1"))
				{				
					if (caseMixAudio.isSelected())
						addToList.setText(language.getProperty("filesVideoOrAudio"));
					
					lblMix.setText(language.getProperty("mono"));
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

		caseSplitAudio = new JRadioButton(language.getProperty("caseSplitAudio"));
		caseSplitAudio.setName("caseSplitAudio");
		caseSplitAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSplitAudio.setBounds(7, caseMixAudio.getY() +  caseMixAudio.getHeight(), caseSplitAudio.getPreferredSize().width, 23);
		grpAudio.add(caseSplitAudio);

		caseSplitAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseSplitAudio.isSelected())
					caseMixAudio.setSelected(false);
			}

		});

		lblSplit = new JLabel(language.getProperty("mono"));
		lblSplit.setName("lblSplit");
		lblSplit.setBackground(new Color(80, 80, 80));
		lblSplit.setHorizontalAlignment(SwingConstants.CENTER);
		lblSplit.setOpaque(true);
		lblSplit.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblSplit.setBounds(caseSplitAudio.getLocation().x + caseSplitAudio.getWidth() + 5, caseSplitAudio.getY() + 3, 46, 16);
		grpAudio.add(lblSplit);

		lblSplit.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblSplit.getText().equals(language.getProperty("mono")))
					lblSplit.setText(language.getProperty("stereo"));
				else
					lblSplit.setText(language.getProperty("mono"));
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
		
		caseSampleRate = new JRadioButton(language.getProperty("caseSampleRate"));
		caseSampleRate.setName("caseSampleRate");
		caseSampleRate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSampleRate.setBounds(7, caseSplitAudio.getY() +caseSplitAudio.getHeight(), caseSampleRate.getPreferredSize().width, 23);
		grpAudio.add(caseSampleRate);		
		
		caseConvertAudioFramerate = new JRadioButton(language.getProperty("caseConvertAudioFramerate"));
		caseConvertAudioFramerate.setName("caseConvertAudioFramerate");
		caseConvertAudioFramerate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseConvertAudioFramerate.setBounds(7, caseSampleRate.getY() +  caseSampleRate.getHeight(), caseConvertAudioFramerate.getPreferredSize().width, 23);
		grpAudio.add(caseConvertAudioFramerate);
		
		caseConvertAudioFramerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseConvertAudioFramerate.isSelected())
				{
					comboAudioIn.setEnabled(true);
					comboAudioOut.setEnabled(true);
				}
				else
				{
					comboAudioIn.setEnabled(false);
					comboAudioOut.setEnabled(false);
				}
			}

		});

		comboAudioIn = new JComboBox<String>();
		comboAudioIn.setName("comboAudioIn");
		comboAudioIn.setEnabled(false);
		comboAudioIn.setModel(new DefaultComboBoxModel<String>(
				new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboAudioIn.setSelectedIndex(2);
		comboAudioIn.setMaximumRowCount(20);
		comboAudioIn.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboAudioIn.setEditable(true);
		comboAudioIn.setSize(63, 16);
		comboAudioIn.setLocation(caseConvertAudioFramerate.getLocation().x + caseConvertAudioFramerate.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 4);
		grpAudio.add(comboAudioIn);		

		comboAudioIn.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboAudioIn.getSelectedIndex();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (caseConvertAudioFramerate.isSelected()) {
					newitem = newitem + e.getWheelRotation();
					if (newitem >= 0 && newitem < comboAudioIn.getItemCount())
						comboAudioIn.setSelectedIndex(newitem);
					else
						newitem = comboAudioIn.getSelectedIndex();
				}
			}
		});
		
		lblFromTo = new JLabel(language.getProperty("lblFromTo"));
		lblFromTo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblFromTo.setSize(lblFromTo.getPreferredSize().width + 4, 16);
		lblFromTo.setLocation(comboAudioIn.getLocation().x + comboAudioIn.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 3);
		grpAudio.add(lblFromTo);
		
		comboAudioOut = new JComboBox<String>();
		comboAudioOut.setName("comboAudioOut");
		comboAudioOut.setEnabled(false);
		comboAudioOut.setModel(new DefaultComboBoxModel<String>(
				new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboAudioOut.setSelectedIndex(1);
		comboAudioOut.setMaximumRowCount(20);
		comboAudioOut.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboAudioOut.setEditable(true);
		comboAudioOut.setSize(63, 16);
		comboAudioOut.setLocation(lblFromTo.getLocation().x + lblFromTo.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 4);
		grpAudio.add(comboAudioOut);		

		comboAudioOut.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboAudioOut.getSelectedIndex();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (caseConvertAudioFramerate.isSelected()) {
					newitem = newitem + e.getWheelRotation();
					if (newitem >= 0 && newitem < comboAudioOut.getItemCount())
						comboAudioOut.setSelectedIndex(newitem);
					else
						newitem = comboAudioOut.getSelectedIndex();
				}
			}
		});
		
		lblAudioIs = new JLabel(Shutter.language.getProperty("fps"));
		lblAudioIs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudioIs.setSize(lblAudioIs.getPreferredSize().width, 16);
		lblAudioIs.setLocation(comboAudioOut.getLocation().x + comboAudioOut.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 3);
		
		if (getLanguage.equals("Dutch") == false)		
			grpAudio.add(lblAudioIs);
	}

	private void grpCorrections() {
		grpCorrections = new JPanel();
		grpCorrections.setLayout(null);
		grpCorrections.setVisible(false);
		grpCorrections.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), language.getProperty("grpCorrections") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpCorrections.setBackground(new Color(50, 50, 50));
		grpCorrections.setBounds(334, 396, 312, 17);
		frame.getContentPane().add(grpCorrections);

		grpCorrections.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 25;
				for (Component c : grpCorrections.getComponents()) {
					if (c instanceof JRadioButton)
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
										
										grpCorrections.setSize(312, i);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
										
										grpCorrections.setSize(312, i);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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

		caseDeflicker = new JRadioButton(language.getProperty("caseDeflicker"));
		caseDeflicker.setName("caseDeflicker");
		caseDeflicker.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDeflicker.setSize(caseDeflicker.getPreferredSize().width, 23);		
		
		caseBanding = new JRadioButton(language.getProperty("caseBanding"));
		caseBanding.setName("caseBanding");
		caseBanding.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseBanding.setSize(caseBanding.getPreferredSize().width + 10, 23); 
				
		caseDetails = new JRadioButton(language.getProperty("caseDetails"));
		caseDetails.setName("caseDetails");
		caseDetails.setToolTipText(language.getProperty("tooltipDetails"));
		caseDetails.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDetails.setSize(caseDetails.getPreferredSize().width + 14, 23);

		caseDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseDetails.isSelected() == false)
					sliderDetails.setValue(0);
			}

		});

		sliderDetails = new JSlider();
		sliderDetails.setName("sliderDetails");
		sliderDetails.setMinorTickSpacing(1);
		sliderDetails.setMaximum(10);
		sliderDetails.setMinimum(-10);
		sliderDetails.setValue(0);
		sliderDetails.setSize(106, 22);

		iconTVDetails = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVDetails.setToolTipText(language.getProperty("preview"));
		iconTVDetails.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVDetails.setSize(16, 16);

		iconTVDetails.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				float value = 0 - (sliderDetails.getValue() / 10);
				String smartblur = "smartblur=" + "1.0:" + value;
				
				FFPLAY.previewFilters(smartblur, false);

			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVDetails.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVDetails.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		sliderDetails.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				float value = (float) sliderDetails.getValue() / 10;

				if (value != 0)
					caseDetails.setSelected(true);
				else
					caseDetails.setSelected(false);

				caseDetails.setText(language.getProperty("details") + " " + value);
			}

		});

		sliderDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDetails.setSelected(true);
				sliderDetails.setEnabled(true);
			}

		});

		caseBruit = new JRadioButton(language.getProperty("caseBruit"));
		caseBruit.setName("caseBruit");
		caseBruit.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseBruit.setSize(caseBruit.getPreferredSize().width + 14, 23);

		caseBruit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseBruit.isSelected() == false)
					sliderBruit.setValue(0);
			}

		});

		sliderBruit = new JSlider();
		sliderBruit.setName("sliderBruit");
		sliderBruit.setMinorTickSpacing(1);
		sliderBruit.setMaximum(10);
		sliderBruit.setMinimum(0);
		sliderBruit.setValue(0);
		sliderBruit.setSize(106, 22);

		iconTVBruit = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVBruit.setToolTipText(language.getProperty("preview"));
		iconTVBruit.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVBruit.setSize(16, 16);

		iconTVBruit.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int value = sliderBruit.getValue();
				String hqdn3d = "hqdn3d=" + value + ":" + value + ":" + value + ":" + value;

				FFPLAY.previewFilters(hqdn3d, false);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVBruit.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVBruit.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		sliderBruit.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = sliderBruit.getValue();

				if (value != 0)
					caseBruit.setSelected(true);
				else
					caseBruit.setSelected(false);

				caseBruit.setText(language.getProperty("noiseSuppression") + " " + value);
			}

		});

		sliderBruit.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseBruit.setSelected(true);
				sliderBruit.setEnabled(true);
			}

		});

		caseExposure = new JRadioButton(language.getProperty("caseExposure"));
		caseExposure.setName("caseExposure");
		caseExposure.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseExposure.setSize(caseExposure.getPreferredSize().width + 20, 23);

		caseExposure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseExposure.isSelected() == false)
					sliderExposure.setValue(0);
			}

		});

		sliderExposure = new JSlider();
		sliderExposure.setName("sliderExposure");
		sliderExposure.setMinorTickSpacing(1);
		sliderExposure.setMaximum(100);
		sliderExposure.setMinimum(0);
		sliderExposure.setValue(0);
		sliderExposure.setSize(106, 22);

		iconTVExposure = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVExposure.setToolTipText(language.getProperty("preview"));
		iconTVExposure.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVExposure.setSize(16, 16);

		iconTVExposure.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int value = sliderExposure.getValue();
				String deflicker = "deflicker=s=" + Math.ceil((128 * value) / 100 + 1);
				
				FFPLAY.previewFilters(deflicker, false);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVExposure.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVExposure.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		sliderExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = sliderExposure.getValue();

				if (value != 0)
					caseExposure.setSelected(true);
				else
					caseExposure.setSelected(false);

				caseExposure.setText(language.getProperty("smoothExposure") + " " + value);
			}

		});

		sliderExposure.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseExposure.setSelected(true);
				sliderExposure.setEnabled(true);
			}

		});

	}

	private void grpTransitions() {
		grpTransitions = new JPanel();
		grpTransitions.setLayout(null);
		grpTransitions.setVisible(false);
		grpTransitions.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), language.getProperty("grpTransitions") + " ", 0,
				0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpTransitions.setBackground(new Color(50, 50, 50));
		grpTransitions.setBounds(334, 258, 312, 17);
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
										
										grpTransitions.setSize(312, i);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										
										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
									} while (i < sized);
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
																				
										grpTransitions.setSize(312, i);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										
										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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
		caseVideoFadeIn = new JRadioButton(language.getProperty("lblVideoFadeIn"));
		caseVideoFadeIn.setName("caseVideoFadeIn");
		caseVideoFadeIn.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseVideoFadeIn.setBounds(7, 16, caseVideoFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeIn);
				
		caseVideoFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseVideoFadeIn.isSelected())
				{
					spinnerVideoFadeIn.setEnabled(true);
				}
				else
				{
					spinnerVideoFadeIn.setEnabled(false);		
				}		
				
				Utils.textFieldBackground();
			}
			
		});
					
		spinnerVideoFadeIn = new JTextField("25");
		spinnerVideoFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeIn.setName("spinnerVideoFadeIn");
		spinnerVideoFadeIn.setEnabled(false);
		spinnerVideoFadeIn.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		if (getLanguage.equals("English"))
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
				
		JLabel videoInFrames = new JLabel(language.getProperty("lblFrames"));
		videoInFrames.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		videoInFrames.setBounds(spinnerVideoFadeIn.getLocation().x + spinnerVideoFadeIn.getWidth() + 4, spinnerVideoFadeIn.getY(), videoInFrames.getPreferredSize().width + 4, 16);
		if (videoInFrames.getWidth() > 44)
		{
			videoInFrames.setText(videoInFrames.getText().substring(0, 1));
			videoInFrames.setSize(videoInFrames.getPreferredSize().width + 4, 16);
		}
		grpTransitions.add(videoInFrames);
				
		lblFadeInColor = new JLabel(language.getProperty("black"));
		lblFadeInColor.setName("lblFadeInColor");
		lblFadeInColor.setBackground(new Color(80, 80, 80));
		lblFadeInColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeInColor.setOpaque(true);
		lblFadeInColor.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblFadeInColor.setSize(55, 16);
		lblFadeInColor.setLocation(videoInFrames.getLocation().x + videoInFrames.getWidth() + 3, spinnerVideoFadeIn.getY() + 1);
		grpTransitions.add(lblFadeInColor);
		
		lblFadeInColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblFadeInColor.getText().equals(language.getProperty("black")))
					lblFadeInColor.setText(language.getProperty("white"));
				else
					lblFadeInColor.setText(language.getProperty("black"));
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
		
		caseAudioFadeIn = new JRadioButton(language.getProperty("lblAudioFadeIn"));
		caseAudioFadeIn.setName("caseAudioFadeIn");
		caseAudioFadeIn.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAudioFadeIn.setBounds(7, caseVideoFadeIn.getY() + 17, caseAudioFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeIn);
			
		caseAudioFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAudioFadeIn.isSelected())
				{
					spinnerAudioFadeIn.setEnabled(true);
				}
				else
				{
					spinnerAudioFadeIn.setEnabled(false);		
				}	
				
				Utils.textFieldBackground();
			}
			
		});
		
		spinnerAudioFadeIn = new JTextField("25");
		spinnerAudioFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeIn.setName("spinnerAudioFadeIn");
		spinnerAudioFadeIn.setEnabled(false);
		spinnerAudioFadeIn.setFont(new Font(freeSansFont, Font.PLAIN, 11));
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
		audioInFrames.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		audioInFrames.setBounds(spinnerAudioFadeIn.getLocation().x + spinnerAudioFadeIn.getWidth() + 4, spinnerAudioFadeIn.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioInFrames);
		
		JLabel iconFadeIn = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconFadeIn.setToolTipText(language.getProperty("preview"));
		iconFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		iconFadeIn.setBounds(lblFadeInColor.getX() + lblFadeInColor.getWidth() + 8, lblFadeInColor.getY(), 16, 16);
		grpTransitions.add(iconFadeIn);

		iconFadeIn.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
						
				FFPLAY.previewFilters("fadeIn", true);
					
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconFadeIn.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconFadeIn.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		//Output	
		caseVideoFadeOut = new JRadioButton(language.getProperty("lblVideoFadeOut"));
		caseVideoFadeOut.setName("caseVideoFadeOut");
		caseVideoFadeOut.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseVideoFadeOut.setBounds(7, caseAudioFadeIn.getY() + caseAudioFadeIn.getHeight(), caseVideoFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeOut);
		
		caseVideoFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseVideoFadeOut.setSelected(false);
				}
				
				if (caseVideoFadeOut.isSelected())
				{
					spinnerVideoFadeOut.setEnabled(true);
				}
				else
				{
					spinnerVideoFadeOut.setEnabled(false);		
				}
				
				Utils.textFieldBackground();
			}
			
		});
				
		spinnerVideoFadeOut = new JTextField("25");
		spinnerVideoFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeOut.setName("spinnerVideoFadeOut");
		spinnerVideoFadeOut.setEnabled(false);
		spinnerVideoFadeOut.setFont(new Font(freeSansFont, Font.PLAIN, 11));
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
		videoOutFrames.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		videoOutFrames.setBounds(spinnerVideoFadeOut.getLocation().x + spinnerVideoFadeOut.getWidth() + 4, spinnerVideoFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(videoOutFrames);
		
		lblFadeOutColor = new JLabel(language.getProperty("black"));
		lblFadeOutColor.setName("lblFadeOutColor");
		lblFadeOutColor.setBackground(new Color(80, 80, 80));
		lblFadeOutColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeOutColor.setOpaque(true);
		lblFadeOutColor.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblFadeOutColor.setSize(55, 16);
		lblFadeOutColor.setLocation(lblFadeInColor.getX(), spinnerVideoFadeOut.getY() + 1);
		grpTransitions.add(lblFadeOutColor);
		
		lblFadeOutColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblFadeOutColor.getText().equals(language.getProperty("black")))
					lblFadeOutColor.setText(language.getProperty("white"));
				else
					lblFadeOutColor.setText(language.getProperty("black"));
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
		
		caseAudioFadeOut = new JRadioButton(language.getProperty("lblAudioFadeOut"));
		caseAudioFadeOut.setName("caseAudioFadeOut");
		caseAudioFadeOut.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAudioFadeOut.setBounds(7, caseVideoFadeOut.getY() + 17, caseAudioFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeOut);
		
		caseAudioFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseAudioFadeOut.setSelected(false);
				}
				
				if (caseAudioFadeOut.isSelected())
				{
					spinnerAudioFadeOut.setEnabled(true);
				}
				else
				{
					spinnerAudioFadeOut.setEnabled(false);		
				}	

				Utils.textFieldBackground();
			}
			
		});
				
		spinnerAudioFadeOut = new JTextField("25");
		spinnerAudioFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeOut.setName("spinnerAudioFadeOut");
		spinnerAudioFadeOut.setEnabled(false);
		spinnerAudioFadeOut.setFont(new Font(freeSansFont, Font.PLAIN, 11));
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
		audioOutFrames.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		audioOutFrames.setBounds(spinnerAudioFadeOut.getLocation().x + spinnerAudioFadeOut.getWidth() + 4, spinnerAudioFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioOutFrames);
		
		JLabel iconFadeOut = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconFadeOut.setToolTipText(language.getProperty("preview"));
		iconFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		iconFadeOut.setBounds(lblFadeOutColor.getX() + lblFadeOutColor.getWidth() + 8, lblFadeOutColor.getY(), 16, 16);
		grpTransitions.add(iconFadeOut);
		
		iconFadeOut.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
																					FFPLAY.previewFilters("fadeOut", true);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconFadeOut.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconFadeOut.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		JPanel linkFadeIn = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				float dash[] = { 5.0f };
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f));
				g2.draw(new Line2D.Float(0, getHeight() - 2, getWidth() - 1, getHeight() - 2));// Grande ligne
				g2.draw(new Line2D.Float(getWidth() - 2, 2, getWidth() - 2, getHeight()));
			}
		};	
		linkFadeIn.setLocation(lblFadeInColor.getX(), iconFadeIn.getY() + iconFadeIn.getHeight());
		linkFadeIn.setSize(lblFadeInColor.getWidth() + iconFadeIn.getWidth() + 1, 10);
		linkFadeIn.setBackground(new Color(50,50,50));
		grpTransitions.add(linkFadeIn);
		
		JPanel linkFadeOut = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				float dash[] = { 5.0f };
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f));
				g2.draw(new Line2D.Float(0, getHeight() - 2, getWidth() - 1, getHeight() - 2));// Grande ligne
				g2.draw(new Line2D.Float(getWidth() - 2, 2, getWidth() - 2, getHeight()));
			}
		};	
		linkFadeOut.setLocation(lblFadeOutColor.getX(), iconFadeOut.getY() + iconFadeOut.getHeight());
		linkFadeOut.setSize(lblFadeOutColor.getWidth() + iconFadeOut.getWidth() + 1, 10);
		linkFadeOut.setBackground(new Color(50,50,50));
		grpTransitions.add(linkFadeOut);
		
	}
	
	private void grpAdvanced() {

		grpAdvanced = new JPanel();
		grpAdvanced.setLayout(null);
		grpAdvanced.setVisible(false);
		grpAdvanced.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true), language.getProperty("grpAdvanced") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpAdvanced.setBackground(new Color(50, 50, 50));
		grpAdvanced.setBounds(334, 396, 312, 17);
		frame.getContentPane().add(grpAdvanced);

		grpAdvanced.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 25;
				for (Component c : grpAdvanced.getComponents()) {
					if (c instanceof JRadioButton)
						size += 17;
				}

				final int sized = size;
				if (grpAdvanced.getSize().height < sized) {
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
										
										grpAdvanced.setSize(312, i);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
											grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y - 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y - 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y - 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y - 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y - 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y - 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
										
										//Animate size
										animateSections(startTime);									
										
									} while (i < sized);
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
										
										grpAdvanced.setSize(312, i);
										btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										
										if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
											grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
											grpH264.setLocation(grpH264.getLocation().x,
													grpH264.getLocation().y + 1);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + 1);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + 1);
											grpInAndOut.setLocation(grpInAndOut.getLocation().x,
													grpInAndOut.getLocation().y + 1);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + 1);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + 1);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + 1);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
										
										//Animate size
										animateSections(startTime);	
										
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

		caseOpenGop = new JRadioButton(Shutter.language.getProperty("btnOpenGOP"));
		caseOpenGop.setName("caseOpenGop");
		caseOpenGop.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenGop.setSize(caseOpenGop.getPreferredSize().width, 23);
		
		caseForcerProgressif = new JRadioButton(language.getProperty("caseForcerProgressif"));
		caseForcerProgressif.setName("caseForcerProgressif");
		caseForcerProgressif.setToolTipText(language.getProperty("tooltipProgressif"));
		caseForcerProgressif.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerProgressif.setSize(caseForcerProgressif.getPreferredSize().width + 4, 23);

		caseForcerEntrelacement = new JRadioButton(language.getProperty("caseForcerEntrelacement"));
		caseForcerEntrelacement.setName("caseForcerEntrelacement");
		caseForcerEntrelacement.setToolTipText(language.getProperty("tooltipEntrelacement"));
		caseForcerEntrelacement.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerEntrelacement.setSize(153, 23);
		
		caseForcerInversion = new JRadioButton(language.getProperty("caseForcerInversion"));
		caseForcerInversion.setName("caseForcerInversion");
		caseForcerInversion.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerInversion.setSize(caseForcerInversion.getPreferredSize().width, 23);

		caseForcerDesentrelacement = new JRadioButton(language.getProperty("caseForcerDesentrelacement"));
		caseForcerDesentrelacement.setName("caseForcerDesentrelacement");
		caseForcerDesentrelacement.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerDesentrelacement.setSize(caseForcerDesentrelacement.getPreferredSize().width, 23);

		caseForcerDesentrelacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lblTFF.getText().equals("TFF"))
					FFPROBE.fieldOrder = "0";
				else if (lblTFF.getText().equals("BFF"))
					FFPROBE.fieldOrder = "1";
				else
					FFPROBE.fieldOrder = "0";
				
				if (caseForcerDesentrelacement.isSelected())
				{
					comboForcerDesentrelacement.setEnabled(true);
				}
				else
				{
					comboForcerDesentrelacement.setSelectedItem("yadif");
					comboForcerDesentrelacement.setEnabled(false);
				}
			}
		});

		comboForcerDesentrelacement = new JComboBox<String>();
		comboForcerDesentrelacement.setName("comboForcerDesentrelacement");
		comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "yadif", "bwdif", "estdif", "w3fdif", "detelecine"}));
		comboForcerDesentrelacement.setSelectedIndex(0);
		comboForcerDesentrelacement.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForcerDesentrelacement.setEditable(false);
		comboForcerDesentrelacement.setSize(78, 18);
				
		comboForcerDesentrelacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") && lblTFF.getText().equals("x2"))
				{
					lblTFF.setText("TFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}				
			}
			
		});
		
		lblTFF = new JLabel("TFF");
		lblTFF.setName("lblTFF");
		lblTFF.setBackground(new Color(80, 80, 80));
		lblTFF.setHorizontalAlignment(SwingConstants.CENTER);
		lblTFF.setOpaque(true);
		lblTFF.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblTFF.setSize(32, 16);

		lblTFF.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblTFF.getText().equals("TFF")) {
					lblTFF.setText("BFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "1";
				} else if (lblTFF.getText().equals("BFF") && comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") == false) {
					lblTFF.setText("x2");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}
				else 
				{
					lblTFF.setText("TFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
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
		
		caseForcerDAR = new JRadioButton(language.getProperty("caseForcerDAR"));
		caseForcerDAR.setName("caseForcerDAR");
		caseForcerDAR.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerDAR.setSize(caseForcerDAR.getPreferredSize().width, 23);
		
		caseForcerDAR.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (caseForcerDAR.isSelected())
					comboDAR.setEnabled(true);
				else
					comboDAR.setEnabled(false);
			}

		});
		
		caseLimiter = new JRadioButton(language.getProperty("caseLimiter"));
		caseLimiter.setName("caseLimiter");
		caseLimiter.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLimiter.setSize(caseLimiter.getPreferredSize().width, 23);

		comboDAR = new JComboBox<String>();
		comboDAR.setName("comboDAR");
		comboDAR.setMaximumRowCount(20);
		comboDAR.setModel(new DefaultComboBoxModel<String>(new String[] { "1:1", "4:3", "9:16", "16:9", "21:9", "1.85", "2.35", "2.39"}));
		comboDAR.setSelectedIndex(3);
		comboDAR.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboDAR.setEditable(true);
		comboDAR.setSize(54, 16);
		
		caseCreateTree = new JRadioButton(language.getProperty("caseCreateTree"));
		caseCreateTree.setName("caseCreateTree");
		caseCreateTree.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseCreateTree.setSize(caseCreateTree.getPreferredSize().width, 23);
		
		caseCreateTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseCreateTree.isSelected())
				{
					setDestinationTabs(2);					
				} else {
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));
					
					if (caseCreateOPATOM.isSelected() == false)
						setDestinationTabs(6);		
				}	
			}	
		});
		
		casePreserveMetadata = new JRadioButton(language.getProperty("casePreserveMetadata"));
		casePreserveMetadata.setName("casePreserveMetadata");
		casePreserveMetadata.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		casePreserveMetadata.setSize(casePreserveMetadata.getPreferredSize().width, 23);
		
		casePreserveSubs = new JRadioButton(language.getProperty("casePreserveSubs"));
		casePreserveSubs.setName("casePreserveSubs");
		casePreserveSubs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		casePreserveSubs.setSize(casePreserveSubs.getPreferredSize().width, 23);
				
		caseCreateOPATOM = new JRadioButton(language.getProperty("caseCreateOPATOM"));
		caseCreateOPATOM.setName("caseCreateOPATOM");
		caseCreateOPATOM.setToolTipText(language.getProperty("tooltipCreateOpatom"));
		caseCreateOPATOM.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseCreateOPATOM.setSize((int) caseCreateOPATOM.getPreferredSize().getWidth(), 23);

		caseCreateOPATOM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseCreateOPATOM.isSelected())
				{
					setDestinationTabs(2);		
					
					if (comboFonctions.getSelectedItem().toString().equals("DNxHR") && lblOPATOM.getText().equals("OP-Atom"))
					{
						JOptionPane.showMessageDialog(frame, language.getProperty("opatomNoSound"), caseCreateOPATOM.getText(), JOptionPane.WARNING_MESSAGE);
					}
					
				} else {
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));
					
					if (caseCreateTree.isSelected() == false)
						setDestinationTabs(6);		
				}		
			}	
		});
		
		lblCreateOPATOM = new JLabel(language.getProperty("lblCreateOPATOM"));
		lblCreateOPATOM.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblCreateOPATOM.setBackground(new Color(50, 50, 50));
		lblCreateOPATOM.setSize((int) lblCreateOPATOM.getPreferredSize().getWidth(), 23);
		
		lblOPATOM = new JLabel("OP-Atom");
		lblOPATOM.setName("lblOPATOM");
		lblOPATOM.setBackground(new Color(80, 80, 80));
		lblOPATOM.setHorizontalAlignment(SwingConstants.CENTER);
		lblOPATOM.setOpaque(true);
		lblOPATOM.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblOPATOM.setSize(65, 16);

		lblOPATOM.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblOPATOM.getText().equals("OP-Atom"))
					lblOPATOM.setText("OP1a");
				else 
					lblOPATOM.setText("OP-Atom");
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
		
		caseOPATOM = new JRadioButton(language.getProperty("caseOPATOM"));
		caseOPATOM.setName("caseOPATOM");
		caseOPATOM.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOPATOM.setSize(caseOPATOM.getPreferredSize().width + 4, 23);

		caseOPATOM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {			
				for (int m = 0; m < liste.getSize(); m++) {
					int s = liste.getElementAt(m).toString().lastIndexOf('.');
					if (liste.getElementAt(m).toString().substring(s).toLowerCase().equals(".mxf") == false) {
						liste.remove(m);
						m = -1;
					}
					
			        String[] data = new String[liste.getSize()]; 

			        for (int i = 0 ; i < liste.getSize() ; i++) { 
			           data[i] = (String) liste.getElementAt(i); 
			        }

			        Arrays.sort(data); 
			        liste.clear();
			        
			        for (int i = 0 ; i < data.length ; i++) { 
						liste.addElement(data[i].toString());
				    }
				}
				lblFiles.setText(Utils.filesNumber());
			}

		});
		
		caseStabilisation = new JRadioButton(language.getProperty("caseStabilisation"));
		caseStabilisation.setName("caseStabilisation");
		caseStabilisation.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseStabilisation.setSize(caseStabilisation.getPreferredSize().width, 23);

		caseConform = new JRadioButton(language.getProperty("caseConform"));
		caseConform.setName("caseConform");
		caseConform.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseConform.setSize(caseConform.getPreferredSize().width, 23);
		caseConform.setToolTipText(language.getProperty("tooltipConform"));

		caseConform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseConform.isSelected())
				{
					comboConform.setEnabled(true);
					comboFPS.setEnabled(true);
				}
				else
				{
					comboConform.setEnabled(false);
					comboFPS.setEnabled(false);
				}
			}

		});

		caseDecimate = new JRadioButton(language.getProperty("caseDecimate"));
		caseDecimate.setName("caseDecimate");
		caseDecimate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDecimate.setSize(caseDecimate.getPreferredSize().width, 23);
		
		comboConform = new JComboBox<String>();
		comboConform.setName("comboConform");
		comboConform.setEnabled(false);
		comboConform.setModel(new DefaultComboBoxModel<String>(
				new String[] {language.getProperty("conformByReverse"), language.getProperty("conformBySpeed"), language.getProperty("conformByDrop"), language.getProperty("conformByBlending"), language.getProperty("conformByInterpolation"), language.getProperty("conformBySlowMotion") }));
		comboConform.setSelectedIndex(3);
		comboConform.setMaximumRowCount(20);
		comboConform.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboConform.setEditable(false);
		comboConform.setSize(100, 16);

		lblToConform = new JLabel(language.getProperty("at") + language.getProperty("colon"));
		lblToConform.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblToConform.setSize(lblToConform.getPreferredSize().width, 16);

		comboFPS = new JComboBox<String>();
		comboFPS.setName("comboFPS");
		comboFPS.setEnabled(false);
		comboFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		comboFPS.setSelectedIndex(2);
		comboFPS.setMaximumRowCount(20);
		comboFPS.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFPS.setEditable(true);
		comboFPS.setSize(50, 16);
		
		lblIsConform = new JLabel(Shutter.language.getProperty("fps"));
		lblIsConform.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblIsConform.setSize(20, 16);

		caseForcerProgressif.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerProgressif.isSelected())
				{
					caseForcerEntrelacement.setSelected(false);
					caseForcerInversion.setSelected(false);
				}
				else if (caseForcerDesentrelacement.isSelected())
				{
					caseForcerDesentrelacement.setSelected(false);
				}
			}
		});
		caseForcerEntrelacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerEntrelacement.isSelected()) {
					caseForcerDesentrelacement.setSelected(false);
					caseForcerProgressif.setSelected(false);
				}
				else
					caseForcerInversion.setSelected(false);
			}
		});
		caseForcerInversion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerInversion.isSelected()) {
					caseForcerDesentrelacement.setSelected(false);
					caseForcerProgressif.setSelected(false);
					caseForcerEntrelacement.setSelected(true);
				}
			}
		});
		caseForcerDesentrelacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerDesentrelacement.isSelected()) {
					caseForcerEntrelacement.setSelected(false);
					caseForcerInversion.setSelected(false);
					caseForcerProgressif.setSelected(true);
				}
			}
		});

		caseAccel = new JRadioButton(language.getProperty("caseAccel"));
		caseAccel.setName("caseAccel");
		caseAccel.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAccel.setSize(caseAccel.getPreferredSize().width, 23);

		caseAccel.addActionListener(new ActionListener() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseAccel.isSelected()) {

					if ("H.264".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high"}));
						comboForceProfile.setSelectedIndex(2);
					}
					else 
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main"}));
						comboForceProfile.setSelectedIndex(0);
					}
										
					try {
						comboAccel.setEnabled(true);						
						List<String> graphicsAccel = new ArrayList<String>(); 

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						if ("VP9".equals(comboFonctions.getSelectedItem().toString()))
						{
							if (System.getProperty("os.name").contains("Windows"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_qsv -s 640x360 -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Intel Quick Sync");
							}
							else if (System.getProperty("os.name").contains("Linux"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_vaapi -s 640x360 -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("VAAPI");
							}
						}
						else
						{							
							String codec = "h264";
							if ("H.265".equals(comboFonctions.getSelectedItem().toString()))
								codec = "hevc";
							
							//Accélération graphique Windows
							if (System.getProperty("os.name").contains("Windows"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -s 640x360 -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Nvidia NVENC");
		
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_qsv -s 640x360 -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Intel Quick Sync");
								
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_amf -s 640x360 -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("AMD AMF Encoder");
							}	
							else if (System.getProperty("os.name").contains("Linux"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -s 640x360 -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Nvidia NVENC");
										
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_vaapi -s 640x360 -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("VAAPI");								
								
								if (comboFonctions.getSelectedItem().toString().equals("H.264"))
								{
									FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_v4l2m2m -s 640x360 -f null -");
									do {
										Thread.sleep(10);
									} while (FFMPEG.runProcess.isAlive());
			
									if (FFMPEG.error == false)
										graphicsAccel.add("V4L2 M2M");	
									
									FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_omx -s 640x360 -f null -");
									do {
										Thread.sleep(10);
									} while (FFMPEG.runProcess.isAlive());
			
									if (FFMPEG.error == false)
										graphicsAccel.add("OpenMAX");
								}
							}
							else//Accélération graphique Mac
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_videotoolbox -s 640x360 -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)								
									graphicsAccel.add("OSX VideoToolbox");
							}
						}

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

						if (graphicsAccel.isEmpty())
						{
							comboAccel.setEnabled(false);
							caseAccel.setSelected(false);
							JOptionPane.showMessageDialog(frame, language.getProperty("noAccel"),
									language.getProperty("accel"), JOptionPane.ERROR_MESSAGE);
						}
						else {
							comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
							caseForcerEntrelacement.setSelected(false);
							caseForcerEntrelacement.setEnabled(false);
							caseForceTune.setSelected(false);
							caseForceTune.setEnabled(false);
							comboForceTune.setEnabled(false);
							caseForceOutput.setSelected(false);
							caseForceOutput.setEnabled(false);
							case2pass.setSelected(false);
							case2pass.setEnabled(false);
							
							if (lblVBR.getText().equals("CBR") || lblVBR.getText().equals("CQ") && comboAccel.getSelectedItem().equals("OSX VideoToolbox") && System.getProperty("os.arch").equals("amd64"))
							{								
								lblVBR.setText("VBR");
								debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
								debitVideo.setSelectedIndex(8);
								lblDbitVido.setText(language.getProperty("lblDbitVido"));
								lblKbsH264.setVisible(true);
								h264lines.setVisible(true);
								if (caseAccel.isSelected() == false)
									case2pass.setEnabled(true);
								FFPROBE.CalculH264();
							}
						}
					} catch (InterruptedException e1) {
					}
				} else {					
					
					caseForcerEntrelacement.setEnabled(true);					

					lblVBR.setVisible(true);
					
					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);
					
					caseForceTune.setEnabled(true);
					
					if (lblVBR.getText().equals("CQ") == false)
						case2pass.setEnabled(true);
									
					caseForceOutput.setEnabled(true);
					comboAccel.setEnabled(false);
				}
				
				//Presets
				if (caseAccel.isSelected())
				{
					if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						if (comboForcePreset.getModel().getSize() != 7)
						{
							comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow"}));
							comboForcePreset.setSelectedIndex(3);
						}
					}
	    			else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
	    			{
    					caseForcePreset.setSelected(false);
    					caseForcePreset.setEnabled(false);
    					comboForcePreset.setEnabled(false);
	    			}	
				}
    			else 
    			{
					if ("H.264".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high", "high422", "high444"}));
						comboForceProfile.setSelectedIndex(2);
					}
					else 
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main", "main422", "main444"}));
						comboForceProfile.setSelectedIndex(0);
					}
    				
    				if (comboForcePreset.getModel().getSize() != 10)
    				{
	    				comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow", "placebo"}));
	    				comboForcePreset.setSelectedIndex(5);
    				}
    			}
			}

		});

		comboAccel = new JComboBox<String>();
		comboAccel.setName("comboAccel");
		comboAccel.setEnabled(false);
		comboAccel.setMaximumRowCount(20);
		comboAccel.setModel(new DefaultComboBoxModel<String>(new String[] { "Nvidia NVENC", "Intel Quick Sync" }));
		comboAccel.setSelectedIndex(0);
		comboAccel.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAccel.setEditable(false);
		comboAccel.setSize(124, 16);
		
		comboAccel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAccel.isSelected()) {
					caseForcerEntrelacement.setSelected(false);
					caseForcerEntrelacement.setEnabled(false);	
					caseForceTune.setSelected(false);
					caseForceTune.setEnabled(false);
					comboForceTune.setEnabled(false);
					case2pass.setSelected(false);
					case2pass.setEnabled(false);
				} 
				else
				{
					caseForcerEntrelacement.setEnabled(true);
					
					lblVBR.setVisible(true);
					
					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);
					
					caseForceTune.setEnabled(true);
					case2pass.setEnabled(true);
				}
			}

		});

		caseForceOutput = new JRadioButton(language.getProperty("caseForceOutput"));
		caseForceOutput.setName("caseForceOutput");
		caseForceOutput.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceOutput.setSize(caseForceOutput.getPreferredSize().width, 23);

		lblNiveaux = new JLabel("0-255");
		lblNiveaux.setName("lblNiveaux");
		lblNiveaux.setBackground(new Color(80, 80, 80));
		lblNiveaux.setHorizontalAlignment(SwingConstants.CENTER);
		lblNiveaux.setOpaque(true);
		lblNiveaux.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblNiveaux.setSize(45, 16);

		lblNiveaux.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblNiveaux.getText().equals("0-255")) {
					lblNiveaux.setText("16-235");
				} else {
					lblNiveaux.setText("0-255");
				}
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

		caseFastStart = new JRadioButton(language.getProperty("caseFastStart"));
		caseFastStart.setName("caseFastStart");
		caseFastStart.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFastStart.setSize(caseFastStart.getPreferredSize().width, 23);
		
		caseAlpha = new JRadioButton(language.getProperty("caseAlpha"));
		caseAlpha.setName("caseAlpha");
		caseAlpha.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAlpha.setSize(caseAlpha.getPreferredSize().width, 23);
		
		caseGOP = new JRadioButton(language.getProperty("caseGOP"));
		caseGOP.setName("caseGOP");
		caseGOP.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseGOP.setSize(caseGOP.getPreferredSize().width + 4, 23);
		
		caseGOP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseGOP.isSelected())
				{
					gopSize.setEnabled(true);
				}
				else
				{
					gopSize.setEnabled(false);		
				}	
				
				Utils.textFieldBackground();
			}
			
		});
				
		gopSize = new JTextField();
		gopSize.setName("gopSize");
		gopSize.setEnabled(false);
		gopSize.setHorizontalAlignment(SwingConstants.CENTER);
		gopSize.setText("250");
		gopSize.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		gopSize.setColumns(10);
		gopSize.setSize(35, 16);
		
		gopSize.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| gopSize.getText().length() >= 3 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		
		caseForceLevel = new JRadioButton(language.getProperty("caseForceLevel"));
		caseForceLevel.setName("caseForceLevel");
		caseForceLevel.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceLevel.setSize(caseForceLevel.getPreferredSize().width, 23);

		caseForceLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceLevel.isSelected()) {
					comboForceProfile.setEnabled(true);
					comboForceLevel.setEnabled(true);
				} else {
					comboForceProfile.setEnabled(false);
					comboForceLevel.setEnabled(false);
				}
			}

		});

		comboForceLevel = new JComboBox<String>();
		comboForceLevel.setName("comboForceLevel");
		comboForceLevel.setEnabled(false);
		comboForceLevel.setMaximumRowCount(15);
		comboForceLevel.setModel(new DefaultComboBoxModel<String>(new String[] { "1.0", "1.1", "1.2", "1.3", "2.0",
				"2.1", "2.2", "3.0", "3.1", "3.2", "4.0", "4.1", "4.2", "5.0", "5.1", "5.2", "6", "6.1", "6.2" }));
		comboForceLevel.setSelectedIndex(14);
		comboForceLevel.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceLevel.setEditable(false);
		comboForceLevel.setSize(50, 16);

		comboForceProfile = new JComboBox<String>();
		comboForceProfile.setName("comboForceProfile");
		comboForceProfile.setEnabled(false);
		comboForceProfile.setMaximumRowCount(4);
		comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high" }));
		comboForceProfile.setSelectedIndex(2);
		comboForceProfile.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceProfile.setEditable(false);
		comboForceProfile.setSize(66, 16);
		
		caseForcePreset = new JRadioButton(language.getProperty("caseForcePreset"));
		caseForcePreset.setName("caseForcePreset");
		caseForcePreset.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcePreset.setSize(caseForcePreset.getPreferredSize().width, 23);

		caseForcePreset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForcePreset.isSelected()) {
					comboForcePreset.setEnabled(true);
				} else {
					comboForcePreset.setEnabled(false);
				}
			}

		});

		comboForcePreset = new JComboBox<String>();
		comboForcePreset.setName("comboForcePreset");
		comboForcePreset.setEnabled(false);
		comboForcePreset.setMaximumRowCount(15);
		comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow", "placebo"}));
		comboForcePreset.setSelectedIndex(5);
		comboForcePreset.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForcePreset.setEditable(false);
		comboForcePreset.setSize(comboForcePreset.getPreferredSize().width, 16);
		
		caseForceTune = new JRadioButton(language.getProperty("caseForceTune"));
		caseForceTune.setName("caseForceTune");
		caseForceTune.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceTune.setSize(caseForceTune.getPreferredSize().width + 4, 23);
		
		caseForceTune.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceTune.isSelected())
					comboForceTune.setEnabled(true);
				else
					comboForceTune.setEnabled(false);
			}

		});
		
		comboForceTune = new JComboBox<String>();
		comboForceTune.setName("comboForceTune");
		comboForceTune.setEnabled(false);
		comboForceTune.setMaximumRowCount(15);
		comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "film", "animation", "grain", "stillimage", "fastdecode", "zerolatency", "psnr", "ssim" }));
		comboForceTune.setSelectedIndex(0);
		comboForceTune.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceTune.setEditable(false);
		comboForceTune.setSize(81, 16);
		
		caseForceQuality = new JRadioButton(language.getProperty("caseForceQuality"));
		caseForceQuality.setName("caseForceQuality");
		caseForceQuality.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceQuality.setSize(caseForceQuality.getPreferredSize().width, 23);
		
		caseForceQuality.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceQuality.isSelected())
					comboForceQuality.setEnabled(true);
				else
					comboForceQuality.setEnabled(false);
			}

		});
		
		comboForceQuality = new JComboBox<String>();
		comboForceQuality.setName("comboForceQuality");
		comboForceQuality.setEnabled(false);
		comboForceQuality.setMaximumRowCount(15);
		comboForceQuality.setModel(new DefaultComboBoxModel<String>(new String[] { "best", "good", "realtime"}));
		comboForceQuality.setSelectedIndex(1);
		comboForceQuality.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceQuality.setEditable(false);
		comboForceQuality.setSize(comboForcePreset.getPreferredSize().width, 16);
		
		caseForceSpeed = new JRadioButton(language.getProperty("caseForceSpeed"));
		caseForceSpeed.setName("caseForceSpeed");
		caseForceSpeed.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceSpeed.setSize(caseForceSpeed.getPreferredSize().width, 23);
		
		caseForceSpeed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceSpeed.isSelected())
					comboForceSpeed.setEnabled(true);
				else
					comboForceSpeed.setEnabled(false);
			}

		});
		
		comboForceSpeed = new JComboBox<String>();
		comboForceSpeed.setName("comboForceSpeed");
		comboForceSpeed.setEnabled(false);
		comboForceSpeed.setMaximumRowCount(15);
		comboForceSpeed.setModel(new DefaultComboBoxModel<String>(new String[] { "0","1","2","3","4","5","6","7","8"}));
		comboForceSpeed.setSelectedIndex(4);
		comboForceSpeed.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceSpeed.setEditable(false);
		comboForceSpeed.setSize(40, 16);
		
		caseAS10 = new JRadioButton(language.getProperty("caseAS10"));
		caseAS10.setName("caseAS10");
		caseAS10.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAS10.setSize(caseAS10.getPreferredSize().width, 23);
		
		caseAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAS10.isSelected())
				{
					if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422"))
					{
						final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mxf"});						
						comboFilter.setModel(model);
					}
					
					comboAS10.setEnabled(true);
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);				
					comboAudio5.setSelectedIndex(4);
					comboAudio6.setSelectedIndex(5);
					comboAudio7.setSelectedIndex(6);
					comboAudio8.setSelectedIndex(7);
				}
				else
				{
					comboAS10.setEnabled(false);
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);				
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
					
					if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422"))
					{
						final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] { ".mxf", ".mov" });						
						comboFilter.setModel(model);
					}
				}
			}

		});
		
		comboAS10 = new JComboBox<String>();
		comboAS10.setName("comboAS10");
		comboAS10.setEnabled(false);
		comboAS10.setMaximumRowCount(4);
		comboAS10.setModel(new DefaultComboBoxModel<String>(new String[] { "HIGH_HD_2014", "NRK_HD_2012"}));
		comboAS10.setSelectedIndex(0);
		comboAS10.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAS10.setEditable(false);
		comboAS10.setSize(129, 16);

	}

	private void grpH264() {
		grpH264 = new JPanel();
		grpH264.setLayout(null);
		grpH264.setVisible(false);
		grpH264.setBorder(BorderFactory.createTitledBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true),
				language.getProperty("grpH264") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), Color.WHITE));
		grpH264.setBackground(new Color(50, 50, 50));
		grpH264.setBounds(658, 59, 312, 210);
		frame.getContentPane().add(grpH264);	
		
		lblDureH264 = new JLabel(language.getProperty("lblDureH264"));
		lblDureH264.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblDureH264.setBounds(40, 45, 46, 16);
		grpH264.add(lblDureH264);
	
		textH = new JTextField();
		textH.setName("textH");
		textH.setText("00");
		textH.setHorizontalAlignment(SwingConstants.CENTER);
		textH.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textH.setColumns(10);
		textH.setBounds(86, 43, 32, 21);
		grpH264.add(textH);
		
		lblH = new JLabel(language.getProperty("lblH"));;
		lblH.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblH.setBounds(textH.getX() + textH.getWidth() + 4, 45, lblH.getPreferredSize().width, 16);
		grpH264.add(lblH);

		textMin = new JTextField();
		textMin.setName("textMin");
		textMin.setText("00");
		textMin.setHorizontalAlignment(SwingConstants.CENTER);
		textMin.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textMin.setColumns(10);
		textMin.setBounds(lblH.getX() + lblH.getWidth() + 4, 43, 32, 21);
		grpH264.add(textMin);
				
		lblMin = new JLabel(language.getProperty("lblMin"));
		lblMin.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblMin.setBounds(textMin.getX() + textMin.getWidth() + 4, 45, lblMin.getPreferredSize().width, 16);
		grpH264.add(lblMin);
		
		textSec = new JTextField();
		textSec.setName("textSec");
		textSec.setText("00");
		textSec.setHorizontalAlignment(SwingConstants.CENTER);
		textSec.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textSec.setColumns(10);
		textSec.setBounds(lblMin.getX() + lblMin.getWidth() + 4, 43, 32, 21);
		grpH264.add(textSec);
		
		lblSec = new JLabel(language.getProperty("lblSec"));
		lblSec.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblSec.setBounds(textSec.getX() + textSec.getWidth() + 4, 45, lblSec.getPreferredSize().width, 16);
		grpH264.add(lblSec);

		textH.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textH.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {
					}

				}
			}
		});

		textMin.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textMin.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {
					}

				}
			}
		});

		textSec.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textSec.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {
					}

				}
			}
		});

		lblTailleH264 = new JLabel(language.getProperty("lblTailleH264"));
		lblTailleH264.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTailleH264.setBounds(40, 73, 46, 16);
		grpH264.add(lblTailleH264);

		case2pass = new JRadioButton(language.getProperty("case2pass"));
		case2pass.setName("case2pass");
		case2pass.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		case2pass.setBounds(14, 178, 95, 23);
		grpH264.add(case2pass);

		case2pass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					case2pass.setSelected(false);
				}
				
				if (case2pass.isSelected()) {
					comboH264Taille.setEnabled(true);
					debitVideo.setEnabled(true);
					taille.setEnabled(true);
					textH.setEnabled(true);
					textMin.setEnabled(true);
					textSec.setEnabled(true);
				}
			}
		});

		caseRognage = new JRadioButton(language.getProperty("caseRognage"));
		caseRognage.setName("caseRognage");
		caseRognage.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRognage.setBounds(118, 178, 95, 23);
		grpH264.add(caseRognage);

		caseRognage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (liste.getSize() == 0 && caseRognage.isSelected()) {
					JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
							language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
					caseRognage.setSelected(false);
				}
				
				if (caseRognage.isSelected())
				{
					int sub = 0;
							
					if (comboFonctions.getSelectedItem().toString().equals("Blu-ray") ==  false)					
					{
						Object[] options = { language.getProperty("cropSimple"), language.getProperty("cropAdvanced") };
					
						sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseCrop"), language.getProperty("frameCropVideo"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						    options,
						    options[0]);
					}
					
					if (sub == 1)
					{
						caseRognage.setSelected(false);
						
						if (caseRognerImage.isSelected())
							caseRognerImage.setSelected(false);
						
						caseRognerImage.doClick();
						
						if (caseRognerImage.isSelected())
						{
							//Agrandissement de la partie
							final int sized = 161;
							if (grpImageSequence.getSize().height < sized) {
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
		
													grpImageSequence.setSize(312, i);
													grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
													grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
													grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
													grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
													grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
													btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
				
													if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
														grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y - 1);
														grpH264.setLocation(grpH264.getLocation().x, grpH264.getLocation().y - 1);
														grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
																grpSetTimecode.getLocation().y - 1);
														grpOverlay.setLocation(grpOverlay.getLocation().x,
																grpOverlay.getLocation().y - 1);
														grpInAndOut.setLocation(grpInAndOut.getLocation().x,
																grpInAndOut.getLocation().y - 1);
														grpSetAudio.setLocation(grpSetAudio.getLocation().x,
																grpSetAudio.getLocation().y - 1);
														grpImageSequence.setLocation(grpImageSequence.getLocation().x,
																grpImageSequence.getLocation().y - 1);
														grpColorimetry.setLocation(grpColorimetry.getLocation().x,
																grpColorimetry.getLocation().y - 1);
														grpImageFilter.setLocation(grpImageFilter.getLocation().x,
																grpImageFilter.getLocation().y - 1);
														grpCorrections.setLocation(grpCorrections.getLocation().x,
																grpCorrections.getLocation().y - 1);
														grpTransitions.setLocation(grpTransitions.getLocation().x,
																grpTransitions.getLocation().y - 1);
														grpAdvanced.setLocation(grpAdvanced.getLocation().x,
																grpAdvanced.getLocation().y - 1);
														btnReset.setLocation(btnReset.getLocation().x,
																btnReset.getLocation().y - 1);
													}
													
													//Animate size
													animateSections(startTime);	
													
												} while (i < sized);
										} catch (Exception e1) {
										}
									}
								});
								changeSize.start();
							}
						}
					}
				}
				
				if (caseRognage.isSelected())
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}
					
					if (CropVideo.frame == null)
						new CropVideo();
					else {
						CropVideo.loadImage("00","00","00");
						Utils.changeDialogVisibility(CropVideo.frame, false);
					}
							
					Thread t = new Thread (new Runnable() {

						@Override
						public void run() {
								
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException er) {}
							} while (CropVideo.frame.isVisible());

					
							frame.setOpacity(1.0f);
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));						
		
							//Largeur
				        	String i[] = FFPROBE.imageResolution.split("x");
							
							comboH264Taille.removeAllItems();
				        	
							if (ratioFinal == 0)
								caseRognage.setSelected(false);
							else if (ratioFinal < (float) Integer.parseInt(i[0]) / Integer.parseInt(i[1]))
							{
								Integer cropValue = (int) (Math.floor(Integer.parseInt(i[1]) * ratioFinal));
																
								if (cropValue % 2 == 0)
									comboH264Taille.addItem(cropValue + "x" + i[1]);
								else
									comboH264Taille.addItem((cropValue + 1) + "x" + i[1]);
								
								comboH264Taille.addItem((int) (Math.floor(2160 * ratioFinal) + (Math.floor(2160 * ratioFinal) % 2)) + "x2160");
								comboH264Taille.addItem((int) (Math.floor(1080 * ratioFinal) + (Math.floor(1080 * ratioFinal) % 2)) + "x1080");
								comboH264Taille.addItem((int) (Math.floor(720 * ratioFinal) + (Math.floor(720 * ratioFinal) % 2)) + "x720");
								comboH264Taille.addItem("720x576");
								comboH264Taille.addItem((int) (Math.floor(480 * ratioFinal) + (Math.floor(480 * ratioFinal) % 2)) + "x480");
								comboH264Taille.addItem((int) (Math.floor(360 * ratioFinal) + (Math.floor(360 * ratioFinal) % 2)) + "x360");
								comboH264Taille.addItem((int) (Math.floor(180 * ratioFinal) + (Math.floor(180 * ratioFinal) % 2)) + "x180");
							} 
							else 
							{
								Integer cropValue = (int) (Math.floor(Integer.parseInt(i[0]) / ratioFinal));
								
								if (cropValue % 2 == 0)
									comboH264Taille.addItem(i[0] + "x" + cropValue);
								else
									comboH264Taille.addItem(i[0] + "x" + (cropValue + 1));								
								
								comboH264Taille.addItem("4096x" + (int) (Math.floor(4096 / ratioFinal) + (Math.floor(4096 / ratioFinal)) % 2));
								comboH264Taille.addItem("3840x" + (int) (Math.floor(3840 / ratioFinal) + (Math.floor(3840 / ratioFinal)) % 2));
								comboH264Taille.addItem("1920x" + (int) (Math.floor(1920 / ratioFinal) + (Math.floor(1920 / ratioFinal)) % 2));
								comboH264Taille.addItem("1280x" + (int) (Math.floor(1280 / ratioFinal) + (Math.floor(1280 / ratioFinal)) % 2));
								comboH264Taille.addItem("854x" + (int) (Math.floor(854 / ratioFinal) + (Math.floor(854 / ratioFinal)) % 2));
								comboH264Taille.addItem("640x" + (int) (Math.floor(640 / ratioFinal) + (Math.floor(640 / ratioFinal)) % 2));
								comboH264Taille.addItem("320x" + (int) (Math.floor(320 / ratioFinal) + (Math.floor(320 / ratioFinal)) % 2));
							}
							
							comboH264Taille.setEnabled(true);
						}
					});
					t.start();
				} 
				else
				{
					comboH264Taille.removeAllItems();
					comboH264Taille.addItem(language.getProperty("source"));
					comboH264Taille.addItem("4096x2160");
					comboH264Taille.addItem("3840x2160");
					comboH264Taille.addItem("1920x1080");
					comboH264Taille.addItem("1440x1080");
					comboH264Taille.addItem("1280x720");
					comboH264Taille.addItem("1024x768");
					comboH264Taille.addItem("854x480");
					comboH264Taille.addItem("720x576");
					comboH264Taille.addItem("640x360");
					comboH264Taille.addItem("320x180");
					comboH264Taille.setSelectedIndex(0);
					comboH264Taille.setEnabled(true);
				}
			}

		});

		caseQMax = new JRadioButton(language.getProperty("caseQMax"));
		caseQMax.setName("caseQMax");
		caseQMax.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseQMax.setBounds(215, 178, caseQMax.getPreferredSize().width, 23);
		grpH264.add(caseQMax);
		
		caseQMax.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseQMax.isSelected())
				{
					caseForcePreset.setSelected(false);
					caseForcePreset.setEnabled(false);
					comboForcePreset.setEnabled(false);
					
					caseForceSpeed.setSelected(false);
					caseForceSpeed.setEnabled(false);
					comboForceSpeed.setEnabled(false);
					
					caseForceQuality.setSelected(false);
					caseForceQuality.setEnabled(false);
					comboForceQuality.setEnabled(false);
				}
				else
				{
					caseForceSpeed.setEnabled(true);
					caseForceQuality.setEnabled(true);
					
					if (caseAccel.isSelected() == false)
						caseForcePreset.setEnabled(true);
				}
			}
			
		});

		comboH264Taille = new JComboBox<String>();
		comboH264Taille.setName("comboH264Taille");
		comboH264Taille.setModel(
				new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "4096x2160", "3840x2160", "1920x1080",
						"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180" }));
		comboH264Taille.setMaximumRowCount(20);
		comboH264Taille.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboH264Taille.setEditable(true);
		comboH264Taille.setBounds(84, 71, 120, 22);
		grpH264.add(comboH264Taille);

		comboH264Taille.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				lblPad.setVisible(true);
				lblPad.setText(language.getProperty("lblPad"));
				
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' && caracter != 'x'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		lblPad = new JLabel() {			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")) || lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
				{
					if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
						g.setColor(Color.BLACK);
					else
						g.setColor(new Color(50,50,50));
					
					g.fillRect(0, 0, 8, 16);
					g.fillRect((70 - 8), 0, (70 - 8), 16);
				}
			}
		};
		lblPad.setText(language.getProperty("lblPad"));
		lblPad.setName("lblPad");
		lblPad.setBackground(new Color(80, 80, 80));
		lblPad.setHorizontalAlignment(SwingConstants.CENTER);
		lblPad.setOpaque(true);
		lblPad.setVisible(false);
		lblPad.setFont(new Font(montserratFont, Font.PLAIN, 11));
		lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 7, comboH264Taille.getLocation().y + 3, 70, 16);
		grpH264.add(lblPad);
				
		lblPad.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				//codecs de sortie
				if (grpH264.isVisible())
				{
					if (caseRognage.isSelected())
					{
						if (lblPad.getText().equals(language.getProperty("lblPad")))
						{
							lblPad.setText(language.getProperty("lblCrop"));
						}
						else
						{
							lblPad.setText(language.getProperty("lblPad"));
						}
					}
					else
					{
						if (lblPad.getText().equals(language.getProperty("lblPad")))
						{
							lblPad.setText(language.getProperty("lblStretch"));
						}
						else if (lblPad.getText().equals(language.getProperty("lblStretch")))
						{
							lblPad.setText(language.getProperty("lblPad"));
						}
					}
				}
				else
				{				
					if (lblPad.getText().equals(language.getProperty("lblPad")))
					{
						lblPad.setText(language.getProperty("lblStretch"));
					}
					else if (lblPad.getText().equals(language.getProperty("lblStretch")))
					{
						lblPad.setText(language.getProperty("lblCrop"));
					}
					else
					{
						lblPad.setText(language.getProperty("lblPad"));
					}
				}
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
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		
		comboH264Taille.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {                      
			if (comboH264Taille.getItemCount() > 0)  //Contourne un bug lors de l'action sur le btnReset
			{
				if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
				{
					lblPad.setVisible(false);
				}
				else
				{
					if (caseRognage.isSelected())
					{
						lblPad.setText(CropVideo.lblPad.getText());
					}
					else						
					{
						lblPad.setText(language.getProperty("lblPad"));
					}
					
					lblPad.setVisible(true);
				}	
			}
		  }
			
		});

		lblH264 = new JLabel("");
		lblH264.setVisible(false);
		lblH264.setHorizontalAlignment(SwingConstants.CENTER);
		lblH264.setForeground(Utils.themeColor);
		lblH264.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblH264.setBounds(6, 19, 300, 16);
		grpH264.add(lblH264);

		iconTVH264 = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconTVH264.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVH264.setBounds(14, 73, 16, 16);
		iconTVH264.setToolTipText(language.getProperty("preview"));
		grpH264.add(iconTVH264);

		iconTVH264.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
					
				FFMPEG.previewEncoding();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVH264.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVH264.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		lblVBR = new JLabel("VBR");
		lblVBR.setName("lblVBR");
		lblVBR.setBackground(new Color(80, 80, 80));
		lblVBR.setHorizontalAlignment(SwingConstants.CENTER);
		lblVBR.setOpaque(true);
		lblVBR.setFont(new Font(montserratFont, Font.PLAIN, 11));
		lblVBR.setBounds(5, 100, 32, 16);
		grpH264.add(lblVBR);

		lblVBR.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblVBR.getText().equals("VBR") && caseAccel.isSelected() == false && comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					lblVBR.setText("CBR");
				}
				else if (lblVBR.getText().equals("CBR")
						|| lblVBR.getText().equals("VBR") && caseAccel.isSelected() && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") == false || System.getProperty("os.arch").equals("aarch64")) && comboFonctions.getSelectedItem().toString().contains("H.26")
						|| (comboFonctions.getSelectedItem().toString().contains("H.26") == false && (comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1")) && lblVBR.getText().equals("VBR")))
				{
					lblVBR.setText("CQ");
					taille.setText("-");
					String[] values = new String[53];
					values[0] = language.getProperty("lblBest");
					for (int i = 1 ; i < 52 ; i++)
					{
						values[i] = String.valueOf(i);
					}			
					values[52] = language.getProperty("lblWorst");
					debitVideo.setModel(new DefaultComboBoxModel<String>(values));
					debitVideo.setSelectedIndex(23);
					lblDbitVido.setText(language.getProperty("lblValue"));
					lblKbsH264.setVisible(false);
					h264lines.setVisible(false);					
					case2pass.setSelected(false);
					case2pass.setEnabled(false);
				}
				else
				{
					lblVBR.setText("VBR");
					debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
					debitVideo.setSelectedIndex(8);
					lblDbitVido.setText(language.getProperty("lblDbitVido"));
					lblKbsH264.setVisible(true);
					h264lines.setVisible(true);
					if (caseAccel.isSelected() == false || comboFonctions.getSelectedItem().toString().equals("VP8") == false && comboFonctions.getSelectedItem().toString().equals("VP9") == false && comboFonctions.getSelectedItem().toString().contains("H.26") == false)
						case2pass.setEnabled(true);
					FFPROBE.CalculH264();
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
		
		debitVideo = new JComboBox<String>();
		debitVideo.setName("debitVideo");
		debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000",
				"15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
		debitVideo.setSelectedIndex(8);
		debitVideo.setMaximumRowCount(20);
		debitVideo.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		debitVideo.setEditable(true);
		debitVideo.setBounds(121, 98, 83, 22);
		grpH264.add(debitVideo);

		debitAudio = new JComboBox<String>();
		debitAudio.setName("debitAudio");
		debitAudio.setModel(new DefaultComboBoxModel<String>(new String[] { "320", "256", "192", "128", "96", "64"}));
		debitAudio.setSelectedIndex(1);
		debitAudio.setMaximumRowCount(20);
		debitAudio.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		debitAudio.setEditable(true);
		debitAudio.setBounds(121, 125, 83, 22);
		grpH264.add(debitAudio);

		lblKbsH264 = new JLabel("kb/s");
		lblKbsH264.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblKbsH264.setBounds(206, 100, 33, 16);
		grpH264.add(lblKbsH264);
		
		JLabel lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblKbs.setBounds(206, 127, 33, 16);
		grpH264.add(lblKbs);

		JLabel lblMo = new JLabel(language.getProperty("mo"));
		lblMo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblMo.setBounds(206, 154, 33, 16);
		grpH264.add(lblMo);

		taille = new JTextField();
		taille.setName("taille");
		taille.setHorizontalAlignment(SwingConstants.CENTER);
		taille.setText("2000");
		taille.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		taille.setColumns(10);
		taille.setBounds(121, 152, 83, 21);
		grpH264.add(taille);
				
		lock = new JLabel(new FlatSVGIcon("contents/unlock.svg", 16, 16));
		lock.setHorizontalAlignment(SwingConstants.CENTER);
		lock.setBounds(taille.getX() - 21 - 3, taille.getY(), 21, 21);
		grpH264.add(lock);
		
		lock.addMouseListener(new MouseListener() {
						
			@Override
			public void mouseClicked(MouseEvent arg0) {
								
				if (isLocked)	
				{
					lock.setIcon(new FlatSVGIcon("contents/unlock.svg", 16, 16));
					isLocked = false;
				}
				else
				{
					lock.setIcon(new FlatSVGIcon("contents/lock.svg", 16, 16));
					isLocked = true;
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

		taille.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
					|| taille.getText().length() >= 5
					|| String.valueOf(caracter).matches("[éèçàù]")
					|| lblVBR.getText().equals("CQ") && lblVBR.isVisible())
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (lblVBR.getText().equals("CQ") == false || lblVBR.isVisible() == false)
				{
				try {
					if (e.getKeyCode() != KeyEvent.VK_DELETE) {
						int h = Integer.parseInt(textH.getText());
						int min = Integer.parseInt(textMin.getText());
						int sec = Integer.parseInt(textSec.getText());
						int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
						int tailleFinale = Integer.parseInt(taille.getText());
						float result = (float) tailleFinale / ((h * 3600) + (min * 60) + sec);
						float resultAudio = (float) audio / 8 / 1024;
						float resultatdebit = (result - resultAudio) * 8 * 1024;
						debitVideo.getModel().setSelectedItem((int) resultatdebit);
					}
				} catch (Exception e1) {}
				}
			}
		});

		lblSize = new JLabel(language.getProperty("size"));
		lblSize.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblSize.setBounds(40, 153, lblSize.getPreferredSize().width, 16);
		grpH264.add(lblSize);

		lblDbitVido = new JLabel(language.getProperty("lblDbitVido"));
		lblDbitVido.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblDbitVido.setBounds(40, 100, 76, 16);
		grpH264.add(lblDbitVido);

		lblDbitAudio = new JLabel(language.getProperty("lblDbitAudio"));
		lblDbitAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblDbitAudio.setBounds(40, 127, 76, 16);
		grpH264.add(lblDbitAudio);

		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (taille.isFocusOwner() == false)
						FFPROBE.setTailleH264();
				} catch (Exception e1) {
				}
			}
		};

		debitVideo.getEditor().getEditorComponent().addKeyListener(keyListener);
		debitAudio.getEditor().getEditorComponent().addKeyListener(keyListener);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (taille.isFocusOwner() == false)
						FFPROBE.setTailleH264();
					
					if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblBest")))
						debitVideo.setSelectedIndex(1);
					else if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblWorst")))
						debitVideo.setSelectedIndex(51);
						
				} catch (Exception e1) {}

			}

		};
		debitVideo.addActionListener(actionListener);
		debitAudio.addActionListener(actionListener);

		// Traits pour bouton okH264
		h264lines = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				float dash[] = { 5.0f };
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f));
				g2.draw(new Line2D.Float(41, 15, 41, 70));// Grande ligne
				g2.draw(new Line2D.Float(6, 15, 40, 15)); // 1
				g2.draw(new Line2D.Float(6, 42, 40, 42)); // 2
				g2.draw(new Line2D.Float(6, 70, 40, 70)); // 3
			}
		};
		h264lines.setBackground(new Color(50, 50, 50));
		h264lines.setBounds(230, 92, 66, 82);
		grpH264.add(h264lines);
		grpH264.validate();
		grpH264.repaint();

	}

	private void Reset() {

		btnReset = new JButton(language.getProperty("btnReset"));
		btnReset.setVisible(false);
		btnReset.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(336, 569, 308, 21);
		frame.getContentPane().add(btnReset);

		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Resolution
				comboResolution.setSelectedIndex(0);
				caseRognerImage.setSelected(false);
				comboImageQuality.setSelectedIndex(0);
				caseCreateSequence.setSelected(false);
				comboInterpret.setEnabled(false);
				comboInterpret.setSelectedIndex(7);
				caseRotate.setSelected(false);
				comboRotate.setSelectedIndex(2);
				comboRotate.setEnabled(false);
				caseMiror.setSelected(false);

				// SetTimecode
				TCset1.setEnabled(false);
				TCset1.setText("00");
				TCset2.setEnabled(false);
				TCset2.setText("00");
				TCset3.setEnabled(false);
				TCset3.setText("00");
				TCset4.setEnabled(false);
				TCset4.setText("00");
				caseSetTimecode.setSelected(false);
				caseSetTimecode.setEnabled(true);
				caseIncrementTimecode.setEnabled(false);
				caseIncrementTimecode.setSelected(false);
				caseGenerateFromDate.setSelected(false);
				caseGenerateFromDate.setEnabled(true);

				// Timecode;
				caseAddOverlay.setSelected(false);
				caseShowDate.setSelected(false);
				caseShowFileName.setSelected(false);

				// H264
				lock.setIcon(new FlatSVGIcon("contents/unlock.svg", 16, 16));
				isLocked = false;
				textH.setEnabled(true);
				textMin.setEnabled(true);
				textSec.setEnabled(true);
				comboH264Taille.setEnabled(true);
				debitVideo.setEnabled(true);
				debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
				debitVideo.setSelectedIndex(8);
				taille.setEnabled(true);
				taille.setText("2000");
				lblVBR.setText("VBR");
				lblDbitVido.setText(language.getProperty("lblDbitVido"));
				lblKbsH264.setVisible(true);
				h264lines.setVisible(true);
				
				if (comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
					FFPROBE.CalculH264();
				
				case2pass.setSelected(false);
				case2pass.setEnabled(true);
				caseRognage.setSelected(false);
				caseQMax.setSelected(false);
				comboH264Taille.removeAllItems();
				comboH264Taille.addItem(language.getProperty("source"));
				comboH264Taille.addItem("4096x2160");
				comboH264Taille.addItem("3840x2160");
				comboH264Taille.addItem("1920x1080");
				comboH264Taille.addItem("1440x1080");
				comboH264Taille.addItem("1280x720");
				comboH264Taille.addItem("1024x768");
				comboH264Taille.addItem("854x480");
				comboH264Taille.addItem("720x576");
				comboH264Taille.addItem("640x360");
				comboH264Taille.addItem("320x180");
				comboH264Taille.setSelectedIndex(0);
				
				// grpSetAudio	
				grpSetAudio.removeAll();
				lblAudioMapping.setText(language.getProperty("stereo"));
				lbl48k.setText("48k");
				
				caseAudioOffset.setSelected(false);
				txtAudioOffset.setEnabled(false);
				
				comboAudio1.setSelectedIndex(0);
				comboAudio2.setSelectedIndex(1);
				comboAudio3.setSelectedIndex(2);
				comboAudio4.setSelectedIndex(3);				
				if (comboFonctions.getSelectedItem().equals("XDCAM HD422") || comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100") || comboFonctions.getSelectedItem().toString().equals("XAVC"))
				{
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}
				else
				{
					comboAudio5.setSelectedIndex(4);
					comboAudio6.setSelectedIndex(5);
					comboAudio7.setSelectedIndex(6);
					comboAudio8.setSelectedIndex(7);
				}
				
				if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
				{
					grpSetAudio.add(lblAudio1);
					grpSetAudio.add(comboAudio1);
					grpSetAudio.add(lblAudio2);
					grpSetAudio.add(comboAudio2);
				}
				else
				{
					grpSetAudio.add(caseAudioOffset);
					grpSetAudio.add(txtAudioOffset);
					txtAudioOffset.setText("0");
					grpSetAudio.add(lblOffsetFPS);
					grpSetAudio.add(iconTVOffset);
				}
												
				if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
				|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
				|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString())
				|| language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
				|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "AC3", "OPUS", "OGG", language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(3);
					caseChangeAudioCodec.setSelected(false);
					comboAudioCodec.setEnabled(false);
					comboAudioBitrate.setEnabled(false);
					
					if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
					{
						grpSetAudio.add(lblAudio3);
						grpSetAudio.add(comboAudio3);
						grpSetAudio.add(lblAudio4);
						grpSetAudio.add(comboAudio4);
						grpSetAudio.add(lblAudio5);
						grpSetAudio.add(comboAudio5);
						grpSetAudio.add(lblAudio6);
						grpSetAudio.add(comboAudio6);
						grpSetAudio.add(lblAudio7);
						grpSetAudio.add(comboAudio7);
						grpSetAudio.add(lblAudio8);
						grpSetAudio.add(comboAudio8);
					}
					
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(comboAudioBitrate);
					grpSetAudio.add(lblKbs);

				}
				else if ("MJPEG".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"PCM 32Bits", "PCM 24Bits", "PCM 16Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(2);						
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(0);
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);											
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if (comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "AC3", "OPUS", "FLAC", "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);						
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("WMV".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("MPEG".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
												}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("VP8".equals(comboFonctions.getSelectedItem().toString()) || "VP9".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "OPUS", "OGG", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 91;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("AV1".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "OPUS", "OGG", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("OGV".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "OGG", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("Xvid".equals(comboFonctions.getSelectedItem().toString()))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(1);	
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpImageSequence.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);											
											grpColorimetry.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											if (grpH264.getLocation().y < grpChooseFiles.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChooseFiles.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChooseFiles.getLocation().y && grpResolution.isVisible() ) {
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + 1);
												grpH264.setLocation(grpH264.getLocation().x,
														grpH264.getLocation().y + 1);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
														grpSetTimecode.getLocation().y + 1);
												grpOverlay.setLocation(grpOverlay.getLocation().x,
														grpOverlay.getLocation().y + 1);
												grpInAndOut.setLocation(grpInAndOut.getLocation().x,
														grpInAndOut.getLocation().y + 1);
												grpSetAudio.setLocation(grpSetAudio.getLocation().x,
														grpSetAudio.getLocation().y + 1);
												grpImageSequence.setLocation(grpImageSequence.getLocation().x,
														grpImageSequence.getLocation().y + 1);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x,
														grpColorimetry.getLocation().y + 1);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x,
														grpImageFilter.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
											}
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}	
				else if (comboFonctions.getSelectedItem().toString().contains("DVD") || comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
				{
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
					comboAudioCodec.setSelectedIndex(0);						
					debitAudio.setModel(comboAudioBitrate.getModel());
					if (comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
						debitAudio.setSelectedIndex(3);
					else
						debitAudio.setSelectedIndex(1);
					caseChangeAudioCodec.setEnabled(false);
					grpSetAudio.add(caseChangeAudioCodec);
					grpSetAudio.add(comboAudioCodec);
					grpSetAudio.add(lbl48k);
					lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
					grpSetAudio.add(lblAudioMapping);
					
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										do {
											long startTime = System.currentTimeMillis() + 1;
											
											if (Settings.btnDisableAnimations.isSelected())
												i = 74;
											else
												i --;

											grpSetAudio.setSize(312, i);
											grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(336, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											
											//Animate size
											animateSections(startTime);	
											
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else //Codecs de montage
				{
					grpSetAudio.add(lblAudio3);
					grpSetAudio.add(comboAudio3);
					grpSetAudio.add(lblAudio4);
					grpSetAudio.add(comboAudio4);
					grpSetAudio.add(lblAudio5);
					grpSetAudio.add(comboAudio5);
					grpSetAudio.add(lblAudio6);
					grpSetAudio.add(comboAudio6);
					grpSetAudio.add(lblAudio7);
					grpSetAudio.add(comboAudio7);
					grpSetAudio.add(lblAudio8);
					grpSetAudio.add(comboAudio8);
				}
				
				grpSetAudio.repaint();
				
				if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
						|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
						|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
						|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
						|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
						|| comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV") 	
						|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
						|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
						|| comboFonctions.getSelectedItem().toString().equals("XAVC")
						|| comboFonctions.getSelectedItem().toString().equals("HAP")
						|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
				{
						lblPad.setVisible(true);
				}
						
				
				// grpAudio
				caseSampleRate.setSelected(false);				
				if (caseMixAudio.isSelected())
					caseMixAudio.doClick();
				caseSplitAudio.setSelected(false);
				caseConvertAudioFramerate.setSelected(false);
				comboAudioIn.setEnabled(false);
				comboAudioOut.setEnabled(false);
				
				// InAndOut
				if (caseInAndOut.isSelected())
					caseInAndOut.doClick();

				// grpAdvanced
				if (caseAccel.isSelected())
					caseAccel.doClick();

				caseAccel.setSelected(false);
				comboAccel.setEnabled(false);
				caseForcerProgressif.setSelected(false);
				caseForcerEntrelacement.setSelected(false);
				caseForcerInversion.setSelected(false);
				caseForcerDesentrelacement.setSelected(false);
				comboForcerDesentrelacement.setEnabled(false);
				comboForcerDesentrelacement.setSelectedIndex(0);
				caseForcerDAR.setSelected(false);
				comboDAR.setEnabled(false);
				comboDAR.setSelectedIndex(3);
				changeFilters();		
				
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
					lblPad.setVisible(false);
				else
				{
					lblPad.setVisible(true);
				}
				
				caseOpenGop.setSelected(false);
				caseLimiter.setSelected(false);
				caseForceOutput.setSelected(false);
				caseFastStart.setSelected(false);
				caseAlpha.setSelected(false);
				caseGOP.setSelected(false);
				gopSize.setEnabled(false);
				gopSize.setText("250");
				caseForceLevel.setSelected(false);
				comboForceProfile.setEnabled(false);
				comboForceLevel.setEnabled(false);
				caseForcePreset.setSelected(false);
				caseForcePreset.setEnabled(true);
				comboForcePreset.setEnabled(false);
				caseForceTune.setSelected(false);
				comboForceTune.setEnabled(false);
				caseForceQuality.setSelected(false);
				caseForceQuality.setEnabled(true);
				comboForceQuality.setEnabled(false);
				caseForceSpeed.setSelected(false);
				caseForceSpeed.setEnabled(true);
				comboForceSpeed.setEnabled(false);				
				caseLogo.setSelected(false);
				if (caseCreateTree.isSelected())
					caseCreateTree.doClick();
				if (casePreserveMetadata.isSelected())
					casePreserveMetadata.doClick();
				if (casePreserveSubs.isSelected())
					casePreserveSubs.doClick();
				if (caseCreateOPATOM.isSelected())
					caseCreateOPATOM.doClick();
				lblOPATOM.setText("OP-Atom");
				caseOPATOM.setSelected(false);
				caseSubtitles.setSelected(false);
				caseConform.setSelected(false);
				comboConform.setEnabled(false);
				caseDecimate.setSelected(false);
				comboConform.setSelectedIndex(3);
				comboFPS.setEnabled(false);
				comboFPS.setSelectedIndex(2);
				lblTFF.setText("TFF");
				caseStabilisation.setSelected(false);
				caseDeflicker.setSelected(false);
				caseBanding.setSelected(false);
				if (caseDetails.isSelected())
					caseDetails.doClick();
				if (caseBruit.isSelected())
					caseBruit.doClick();
				if (caseExposure.isSelected())
					caseExposure.doClick();
				if (caseAS10.isSelected())
					caseAS10.doClick();
				
				// grpSequenceImages
				caseEnableSequence.setSelected(false);
				caseSequenceFPS.setSelectedIndex(2);
				caseSequenceFPS.setEnabled(false);
				caseMotionBlur.setSelected(false);
				if (caseBlend.isSelected())
					caseBlend.doClick();
				
				// grpFiltreImage
				caseYear.setSelected(false);
				comboYear.setSelectedIndex(0);
				comboYear.setEnabled(false);
				caseMonth.setSelected(false);
				comboMonth.setSelectedIndex(0);
				comboMonth.setEnabled(false);
				caseDay.setSelected(false);
				comboDay.setSelectedIndex(0);
				comboDay.setEnabled(false);
				caseFrom.setSelected(false);
				comboFrom.setEnabled(false);
				comboFrom.setSelectedIndex(0);
				comboTo.setEnabled(false);
				comboTo.setSelectedIndex(48);

				// grpLUTs
				if (caseLUTs.isSelected())
					caseLUTs.doClick();
				if (caseColorspace.isSelected())
					caseColorspace.doClick();
				if (caseColor.isSelected())
					caseColor.doClick();
				comboColorspace.setSelectedIndex(0);
				
				if (caseLevels.isSelected())
					caseLevels.doClick();
				
				if (caseColormatrix.isSelected())
					caseColormatrix.doClick();
				
				comboInColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "HDR"}));
				comboInColormatrix.setSelectedIndex(0);
				comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "SDR"}));
				comboOutColormatrix.setSelectedIndex(1);
				
				// grpTransition
				if (caseVideoFadeIn.isSelected())
					caseVideoFadeIn.doClick();
				if (caseVideoFadeOut.isSelected())
					caseVideoFadeOut.doClick();
				if (caseAudioFadeIn.isSelected())
					caseAudioFadeIn.doClick();
				if (caseAudioFadeOut.isSelected())
					caseAudioFadeOut.doClick();
				
				spinnerVideoFadeIn.setText("25");
				spinnerVideoFadeOut.setText("25");
				spinnerAudioFadeIn.setText("25");
				spinnerAudioFadeOut.setText("25");
				
				lblFadeInColor.setText(language.getProperty("black"));
				lblFadeOutColor.setText(language.getProperty("black"));
				
				Utils.textFieldBackground();
				
				// Important
				topPanel.repaint();
				statusBar.repaint();
			}

		});

	}

	private void StatusBar() {
		lblCrParPaul = new JLabel(language.getProperty("lblCrParPaul"));
		lblCrParPaul.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblCrParPaul.setForeground(Color.BLACK);
		lblCrParPaul.setBounds(6, 651, 101, 15);
		frame.getContentPane().add(lblCrParPaul);

		lblCrParPaul.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				String PathToGame;
				PathToGame = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				if (System.getProperty("os.name").contains("Windows")) {
					PathToGame = PathToGame.substring(1, PathToGame.length() - 1);
				} else {
					PathToGame = PathToGame.substring(0, PathToGame.length() - 1);
				}
				
				PathToGame = PathToGame.substring(0, (int) (PathToGame.lastIndexOf("/"))).replace("%20", " ") + "/Game.jar";

				try {
					String PathToJRE;
					ProcessBuilder processGame;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToJRE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToJRE = PathToJRE.substring(1, PathToJRE.length() - 1);
						PathToJRE = '"' + PathToJRE.substring(0, (int) (PathToJRE.lastIndexOf("/"))).replace("%20", " ")
								+ "/JRE/bin/java.exe" + '"';
						processGame = new ProcessBuilder(PathToJRE + " -jar " + '"' + PathToGame + '"');
					} 
					else if (System.getProperty("os.name").contains("Mac"))
					{
						PathToJRE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToJRE = PathToJRE.substring(0, PathToJRE.length() - 1);
						PathToJRE = PathToJRE.substring(0, (int) (PathToJRE.lastIndexOf("/"))).replace("%20", " ")
								+ "/JRE/Contents/Home/bin/java";
						processGame = new ProcessBuilder("/bin/bash", "-c" , '"' + PathToJRE + '"' + " -jar " + '"' + PathToGame + '"');	
					}
					else
					{
						PathToJRE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToJRE = PathToJRE.substring(0, PathToJRE.length() - 1);
						PathToJRE = PathToJRE.substring(0, (int) (PathToJRE.lastIndexOf("/"))).replace("%20", " ")
								+ "/JRE/bin/java";
						processGame = new ProcessBuilder("/bin/bash", "-c" , '"' + PathToJRE + '"' + " -jar " + '"' + PathToGame + '"');	
					}

					processGame.start();
				} catch (Exception e) {				
					System.out.println(e);
				}

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblCrParPaul.setForeground(Color.BLACK);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		tempsRestant = new JLabel(language.getProperty("tempsRestant"));
		tempsRestant.setVisible(false);
		tempsRestant.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		tempsRestant.setForeground(Color.BLACK);
		tempsRestant.setBounds(117, 651, 456, 15);
		frame.getContentPane().add(tempsRestant);
		
		tempsRestant.addComponentListener (new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
				tempsEcoule.setVisible(false);
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	if (tempsEcoule != null && tempsEcoule.getText().equals(language.getProperty("tempsEcoule")) == false)
	        		tempsEcoule.setVisible(true);
	        }
	    } );
		
		tempsRestant.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (caseDisplay.isSelected() == false)
				{
					tempsRestant.setVisible(false);
					
					if (tempsEcoule != null && tempsEcoule.getText().equals(language.getProperty("tempsEcoule")) == false)
						tempsEcoule.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {	
				if (caseDisplay.isSelected() == false)
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		tempsEcoule = new JLabel(language.getProperty("tempsEcoule"));
		tempsEcoule.setVisible(false);
		tempsEcoule.setForeground(Color.BLACK);
		tempsEcoule.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		tempsEcoule.setBounds(117, 651, 456, 15);
		frame.getContentPane().add(tempsEcoule);
		
		tempsEcoule.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (FFMPEG.isRunning || BMXTRANSWRAP.isRunning)
				{
					tempsEcoule.setVisible(false);
					tempsRestant.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {	
				if (FFMPEG.isRunning  || BMXTRANSWRAP.isRunning)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
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

		ImageIcon imageIcon = new ImageIcon(
				new ImageIcon(getClass().getClassLoader().getResource("contents/bouton.jpg")).getImage()
						.getScaledInstance(1000, 22, Image.SCALE_DEFAULT));

		lblYears = new JLabel("2013-2021");
		lblYears.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYears.setForeground(Color.BLACK);
		lblYears.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblYears.setBounds(585, 651, lblYears.getPreferredSize().width, 15);
		frame.getContentPane().add(lblYears);

		lblYears.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				final JFrame oldVersions = new JFrame();
				oldVersions.setUndecorated(true);
				oldVersions.getContentPane().setBackground(new Color(50, 50, 50));
				oldVersions.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f));
				oldVersions.setSize(1600, 423);
				oldVersions.setShape(new RoundRectangle2D.Double(0, 0, oldVersions.getWidth() + 18,
						oldVersions.getHeight(), 15, 15));
				oldVersions.setIconImage(
						new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				oldVersions.setLocation(dim.width / 2 - oldVersions.getSize().width / 2,
						dim.height / 2 - oldVersions.getSize().height / 2);
				oldVersions.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				oldVersions.setTitle("2013-2017");
				oldVersions.getContentPane().setLayout(null);
				oldVersions.setResizable(false);
				oldVersions.setVisible(true);
				oldVersions.setAlwaysOnTop(true);

				oldVersions.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
							oldVersions.dispose();
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
					}

					@Override
					public void keyTyped(KeyEvent e) {
					}

				});

				JLabel image = new JLabel(
						new ImageIcon(getClass().getClassLoader().getResource("contents/2013-2016.jpg")));
				image.setSize(1600, 423);
				image.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						oldVersions.dispose();
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
					}

					@Override
					public void mousePressed(MouseEvent down) {
						MousePositionX = down.getPoint().x;
						MousePositionY = down.getPoint().y;
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}

				});

				image.addMouseMotionListener(new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent e) {
						oldVersions.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX,
								MouseInfo.getPointerInfo().getLocation().y - MousePositionY);
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
					}

				});

				oldVersions.getContentPane().add(image);
			}

			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent arg0) {
				lblYears.setForeground(Color.BLACK);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}

		});

		statusBar = new JLabel();
		statusBar.setIcon(imageIcon);
		statusBar.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.setBounds(0, 647, 1000, 22);
		frame.getContentPane().add(statusBar);
		frame.getContentPane()
				.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { btnBrowse, btnEmptyList,
						fileList, comboFonctions, comboFilter, btnStart, btnCancel, caseOpenFolderAtEnd1,
						caseChangeFolder1, caseRunInBackground, iconTVResolution, comboResolution, caseRognerImage,
						caseCreateSequence, caseSequenceFPS, caseAddOverlay,
						caseMixAudio, caseInAndOut, textH, textMin, textSec, comboH264Taille, debitVideo, debitAudio,
						taille, case2pass, caseRognage, caseQMax, topPanel, lblV, quit, reduce, help, topImage,
						grpChooseFiles, scrollBar, lblFilesEnded, lblFiles, grpChooseFunction, lblFilter,
						grpDestination, lblDestination1, grpProgression, progressBar1, lblCurrentEncoding, grpResolution,
						lblTaille, grpImageSequence, grpImageFilter, grpOverlay, grpInAndOut, grpSetAudio, grpAudio,
						grpAdvanced, grpH264, lblDureH264, lblMin, lblH, lblTailleH264, lblH264, iconTVH264, lblSize, lblDbitVido, lblDbitAudio }));

	}

	public static void changeFunction(final boolean anim) {		
		
		String fonction = comboFonctions.getSelectedItem().toString();
		if (language.getProperty("functionCut").equals(fonction) || language.getProperty("functionRewrap").equals(fonction) 
				|| language.getProperty("functionMerge").equals(fonction)
				|| language.getProperty("functionReplaceAudio").equals(fonction)
				|| "WAV".equals(fonction) || "AIFF".equals(fonction) || "FLAC".equals(fonction)
				|| "MP3".equals(fonction) || "AAC".equals(fonction) || "AC3".equals(fonction) || "OPUS".equals(fonction)
				|| "OGG".equals(fonction) || "Loudness & True Peak".equals(fonction)
				|| language.getProperty("functionBlackDetection").equals(fonction) || language.getProperty("functionOfflineDetection").equals(fonction) 
				|| "DNxHD".equals(fonction)	|| "DNxHR".equals(fonction) || "Apple ProRes".equals(fonction) || "QT Animation".equals(fonction) || ("GoPro CineForm").equals(fonction) || "Uncompressed YUV".equals(fonction)
				|| "H.264".equals(fonction) || "H.265".equals(fonction) || "DV PAL".equals(fonction)
				|| "WMV".equals(fonction) || "MPEG".equals(fonction) || "VP8".equals(fonction) || "VP9".equals(fonction) || "AV1".equals(fonction) || "OGV".equals(fonction)
				|| "MJPEG".equals(fonction) || "Xvid".equals(fonction) || "XDCAM HD422".equals(fonction) || "AVC-Intra 100".equals(fonction) || ("XAVC").equals(fonction) || "HAP".equals(fonction) || "FFV1".equals(fonction)
				|| "DVD".equals(fonction) || "Blu-ray".equals(fonction) || "QT JPEG".equals(fonction)
				|| language.getProperty("functionPicture").equals(fonction)
				|| "JPEG".equals(fonction)
				|| language.getProperty("functionNormalization").equals(fonction))
		{
			changeFrameSize(true);
			changeSections(anim);
			
		} else if (language.getProperty("functionExtract").equals(fonction)
				|| language.getProperty("functionConform").equals(fonction)
				|| language.getProperty("functionInsert").equals(fonction)
				|| "CD RIP".equals(fonction)
				|| "DVD Rip".equals(fonction)				
				|| language.getProperty("functionSceneDetection").equals(fonction)
				|| language.getProperty("functionSubtitles").equals(fonction)
				|| language.getProperty("functionWeb").equals(fonction))
		{
			changeFrameSize(false);
			changeSections(anim);
			
		} 
		else if (language.getProperty("itemMyFunctions").equals(fonction))
		{
			changeFrameSize(false);
			changeSections(anim);
		
			if (Functions.frame == null)
			{
				new Functions();
			}
			else {
				if (Functions.listeDeFonctions.getModel().getSize() > 0) {
					Functions.lblSave.setVisible(false);
					Functions.lblDrop.setVisible(false);
				}

				Functions.frame.setVisible(true);
			}
			Utils.changeFrameVisibility(Functions.frame, false);
		}

		//File de rendus
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionExtract")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
				&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
				&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("itemMyFunctions")) == false
				&& (RenderQueue.frame == null || RenderQueue.frame != null && RenderQueue.frame.isVisible() == false))
		{
			iconList.setVisible(true);			
			
			if (iconPresets.isVisible())
			{
				iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
				btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
			}
			else
			{
				iconPresets.setBounds(180, 45, 21, 21);
				btnCancel.setBounds(207, 46, 97, 21);
			}
		}
		else
		{
			iconList.setVisible(false);		
			
			if (iconPresets.isVisible())
			{
				iconPresets.setBounds(180, 45, 21, 21);
				btnCancel.setBounds(207, 46, 97, 21);
			}
			else
			{
				btnCancel.setBounds(184, 46, 120, 21);
			}	
		}
		
		// Case Conform
		if (caseConform.isSelected())
		{
			comboConform.setEnabled(true);
			comboFPS.setEnabled(true);
		}
		else
		{
			comboConform.setEnabled(false);
			comboFPS.setEnabled(false);
		}	
		
		//Case Convert FPS Audio
		if (caseConvertAudioFramerate.isSelected())
		{
			comboAudioIn.setEnabled(true);
			comboAudioOut.setEnabled(true);		
		}
		else
		{
			comboAudioIn.setEnabled(false);
			comboAudioOut.setEnabled(false);	
		}

		// Désactivation du grpChooseFiles
		Component[] components = grpChooseFiles.getComponents();
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(false);
		} else {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(true);
		}

		// Modification des cases par rapport aux fonctions
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
				|| inputDeviceIsRunning) {
			if (caseChangeFolder1.isSelected() == false) {
				
				if (System.getProperty("os.name").contains("Windows"))
				{
					if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
						lblDestination1.setText(Settings.lblDestination1.getText());
					else
						lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
				}
				else
				{
					if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
						lblDestination1.setText(Settings.lblDestination1.getText());
					else
						lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
				}
				
			}			
		} else {
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(language.getProperty("sameAsSource"));
		}
				
		// Modifications du statut des cases
		if (comboFonctions.getSelectedItem().equals("Loudness & True Peak")
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) 
		{
			
			caseOpenFolderAtEnd1.setEnabled(false);
			caseChangeFolder1.setEnabled(false);
			lblDestination1.setVisible(false);
			
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")))
			{
				setDestinationTabs(6);
			}
			else
			{			
				if (comboFonctions.getSelectedItem().equals("Loudness & True Peak"))
					setDestinationTabs(2);
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
				{
					caseOpenFolderAtEnd1.setEnabled(true);
					caseChangeFolder1.setEnabled(true);
					lblDestination1.setVisible(true);
					setDestinationTabs(2);
				}
				else
					setDestinationTabs(1);
			}
			
		} 
		else
		{
			if (((comboFonctions.getSelectedItem().toString().equals("DNxHD") || (comboFonctions.getSelectedItem().toString().equals("DNxHR"))) && caseCreateOPATOM.isSelected()) || caseCreateTree.isSelected()) //OP-Atom
			{
				setDestinationTabs(2);		
			}
			else if (comboFonctions.getSelectedItem().toString().equals("H.264"))
			{
				setDestinationTabs(7);
			}
			else
				setDestinationTabs(6);
			
			caseOpenFolderAtEnd1.setEnabled(true);
			caseChangeFolder1.setEnabled(true);
			lblDestination1.setVisible(true);

		}

		// btnStart text
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
			btnStart.setText(language.getProperty("btnDownload"));
		else if (RenderQueue.frame != null) {
			if (RenderQueue.frame.isVisible()
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
					&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
					&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false) {
				btnStart.setText(language.getProperty("btnAddToRender"));
			} else {
				if (FFMPEG.isRunning == false)
					btnStart.setText(language.getProperty("btnStartFunction"));
			}
		} else {
			if (FFMPEG.isRunning == false)
				btnStart.setText(language.getProperty("btnStartFunction"));
		}
		
		//caseForcerDesentrelacement
		if (caseForcerDesentrelacement.isSelected())
			comboForcerDesentrelacement.setEnabled(true);
		else
			comboForcerDesentrelacement.setEnabled(false);
				
		//caseForceDar
		if (caseForcerDAR.isSelected())
			comboDAR.setEnabled(true);
		else
			comboDAR.setEnabled(false);		
		
		// Case Accélération
		if (caseAccel.isSelected() && comboFonctions.getSelectedItem().toString().contains("H.26")) {
			comboAccel.setEnabled(true);
		} 
		else
		{
			comboAccel.setEnabled(false);
			caseForcerEntrelacement.setEnabled(true);
		}

		if (caseForceLevel.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("AV1"))) {
			caseForceLevel.setEnabled(true);
			comboForceProfile.setEnabled(true);
			comboForceLevel.setEnabled(true);
		} else {
			comboForceProfile.setEnabled(false);
			comboForceLevel.setEnabled(false);
		}
		
		if (caseForcePreset.isSelected() && comboFonctions.getSelectedItem().toString().contains("H.26") && caseQMax.isSelected() == false) {
			caseForcePreset.setEnabled(true);
			comboForcePreset.setEnabled(true);
		} 
		else
			comboForcePreset.setEnabled(false);
		
				
		if (caseForceTune.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("VP8") || comboFonctions.getSelectedItem().toString().equals("VP9"))) {
			caseForceTune.setEnabled(true);
			comboForceTune.setEnabled(true);
		} 
		else 
			comboForceTune.setEnabled(false);
		
		if (caseForceQuality.isSelected() && (comboFonctions.getSelectedItem().toString().equals("VP8") || comboFonctions.getSelectedItem().toString().equals("VP9")) && caseQMax.isSelected() == false)
		{
			caseForceQuality.setEnabled(true);
			comboForceQuality.setEnabled(true);
		} 
		else 
			comboForceQuality.setEnabled(false);
		
		if (caseForceSpeed.isSelected() && caseQMax.isSelected() == false && (comboFonctions.getSelectedItem().toString().equals("VP8") || comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1")))
		{
			caseForceSpeed.setEnabled(true);
			comboForceSpeed.setEnabled(true);
		} 
		else 
			comboForceSpeed.setEnabled(false);

	}

	private static void changeFrameSize(final boolean bigger) {
		
		 if (bigger && frame.getSize().width < 660) {
				frame.setSize(660, 670);
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
				frame.setShape(shape1);
				quit.setLocation(frame.getSize().width - 20, 3);
				reduce.setLocation(quit.getLocation().x - 20, 3);
				help.setLocation(reduce.getLocation().x - 20, 3);
				newInstance.setLocation(help.getLocation().x - 20, 3);
			} else if (bigger == false && frame.getSize().width > 332) {
				frame.setSize(332, 670);
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
				frame.setShape(shape1);
				quit.setLocation(frame.getSize().width - 20, 3);
				reduce.setLocation(quit.getLocation().x - 20, 3);
				help.setLocation(reduce.getLocation().x - 20, 3);
				newInstance.setLocation(help.getLocation().x - 20, 3);
			}
		 
	}

	private static void changeSections(final boolean action) {
		
		Thread changeSize = new Thread(new Runnable() {
			@Override
			public void run() {

				if (changeGroupes == false) // permet d'attendre la fin de l'action
				{
					try {
						
						if (frame.getSize().width == 660 && action) {
							int i = 334;
							do {
								long startTime = System.currentTimeMillis() + 2;
								
								changeGroupes = true;
								if (Settings.btnDisableAnimations.isSelected())
									i = 680;
								else
									i += 4;
								grpResolution.setLocation(i, grpResolution.getLocation().y);
								grpH264.setLocation(i, grpH264.getLocation().y);
								grpSetTimecode.setLocation(i, grpSetTimecode.getLocation().y);
								grpOverlay.setLocation(i, grpOverlay.getLocation().y);
								grpSetAudio.setLocation(i, grpSetAudio.getLocation().y);
								grpAudio.setLocation(i, grpAudio.getLocation().y);
								grpInAndOut.setLocation(i, grpInAndOut.getLocation().y);
								grpImageSequence.setLocation(i, grpImageSequence.getLocation().y);
								grpImageFilter.setLocation(i, grpImageFilter.getLocation().y);
								grpColorimetry.setLocation(i, grpColorimetry.getLocation().y);
								grpCorrections.setLocation(i, grpCorrections.getLocation().y);
								grpTransitions.setLocation(i, grpTransitions.getLocation().y);
								grpAdvanced.setLocation(i, grpAdvanced.getLocation().y);
								btnReset.setLocation((i + 2), btnReset.getLocation().y);

								//Animate size
								animateSections(startTime);	
								
							} while (i < 680);
						}

						String fonction = comboFonctions.getSelectedItem().toString();
							
						if (action)
						{
							grpAdvanced.setSize(grpAdvanced.getSize().width, 17);
							grpOverlay.setSize(grpOverlay.getSize().width, 17);
							grpImageFilter.setSize(grpImageFilter.getSize().width, 17);
							grpSetAudio.setSize(grpSetAudio.getSize().width, 17);
							grpImageSequence.setSize(grpImageSequence.getSize().width, 17);
							grpColorimetry.setSize(grpColorimetry.getSize().width, 17);
							grpCorrections.setSize(grpCorrections.getSize().width, 17);
							grpTransitions.setSize(grpTransitions.getSize().width, 17);
							grpSetTimecode.setSize(grpSetTimecode.getSize().width, 17);
						}
						
						btnReset.setVisible(true);
				
						if (language.getProperty("functionRewrap").equals(fonction) || language.getProperty("functionCut").equals(fonction) || language.getProperty("functionMerge").equals(fonction)) {
							
							if (language.getProperty("functionCut").equals(fonction) || language.getProperty("functionMerge").equals(fonction))
							{
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							}
							else
								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));
							
							caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpOverlay.setVisible(false);
							
							if (language.getProperty("functionRewrap").equals(fonction) || language.getProperty("functionCut").equals(fonction))
							{	
								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(grpInAndOut.getX(), 59);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
								grpSetAudio.setLocation(grpSetAudio.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							}
							else
							{
								grpInAndOut.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpSetAudio.setLocation(grpSetAudio.getX(), 59);							
							}
							
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpAudio.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							
							if (language.getProperty("functionRewrap").equals(fonction))	
							{
								grpAdvanced.removeAll();
								
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								caseSubtitles.setLocation(7, 16);
								grpAdvanced.add(caseSubtitles);								
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							}
							else if (language.getProperty("functionMerge").equals(fonction))
							{
								grpAdvanced.removeAll();

								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								caseOpenGop.setLocation(7, 14);													
								grpAdvanced.add(caseOpenGop);	
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							}
							else
							{
								grpAdvanced.setVisible(false);
								btnReset.setLocation(btnReset.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);	
							}
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);							
							if (comboAudioCodec.getItemCount() != 9 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 32Float") == false)
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "AC3", "OPUS", "OGG", language.getProperty("noAudio") }));
								comboAudioCodec.setSelectedIndex(3);
								caseChangeAudioCodec.setSelected(false);
								comboAudioCodec.setEnabled(false);
								comboAudioBitrate.setEnabled(false);
							}
							caseChangeAudioCodec.setEnabled(true);	
							grpSetAudio.add(comboAudioCodec);
							grpSetAudio.add(comboAudioBitrate);
							grpSetAudio.add(lblKbs);
							grpSetAudio.add(lbl48k);
							lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
							
							lblAudio1.setLocation(13, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							grpSetAudio.add(lblAudio3);
							grpSetAudio.add(comboAudio3);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							grpSetAudio.add(lblAudio4);
							grpSetAudio.add(comboAudio4);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							grpSetAudio.add(lblAudio5);
							grpSetAudio.add(comboAudio5);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							grpSetAudio.add(lblAudio6);
							grpSetAudio.add(comboAudio6);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							grpSetAudio.add(lblAudio7);
							grpSetAudio.add(comboAudio7);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							grpSetAudio.add(lblAudio8);
							grpSetAudio.add(comboAudio8);
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 16
								&& comboAudio6.getSelectedIndex() == 16
								&& comboAudio7.getSelectedIndex() == 16
								&& comboAudio8.getSelectedIndex() == 16)
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}							

						} else if (language.getProperty("functionReplaceAudio").equals(fonction) || language.getProperty("functionNormalization").equals(fonction)) {
							
							if (language.getProperty("functionReplaceAudio").equals(fonction))
								addToList.setText(language.getProperty("fileVideoAndAudio"));
							else
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetAudio.setVisible(true);
							
							if (language.getProperty("functionReplaceAudio").equals(fonction))
							{
								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(grpInAndOut.getX(), 59);
								
								grpSetAudio.setLocation(grpSetAudio.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							}
							else
							{
								grpInAndOut.setVisible(false);
								grpSetAudio.setLocation(grpSetAudio.getX(), 59);
							}
							
							grpSetAudio.setSize(312, 70);

							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpAudio.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(btnReset.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							if (comboAudioCodec.getItemCount() != 9 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 32Float") == false)
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "AC3", "OPUS", "OGG", language.getProperty("noAudio") }));
								comboAudioCodec.setSelectedIndex(3);
								caseChangeAudioCodec.setSelected(false);
								comboAudioCodec.setEnabled(false);
								comboAudioBitrate.setEnabled(false);
							}
							caseChangeAudioCodec.setEnabled(true);	
							grpSetAudio.add(comboAudioCodec);
							grpSetAudio.add(comboAudioBitrate);
							grpSetAudio.add(lblKbs);
							grpSetAudio.add(lbl48k);
							lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
							grpSetAudio.add(caseAudioOffset);
							grpSetAudio.add(txtAudioOffset);
							grpSetAudio.add(lblOffsetFPS);
							grpSetAudio.add(iconTVOffset);
							
							grpSetAudio.repaint();
							
						} else if ("WAV".equals(fonction) || "AIFF".equals(fonction) || "FLAC".equals(fonction) || "MP3".equals(fonction) || "AAC".equals(fonction) || "AC3".equals(fonction) || "OPUS".equals(fonction) || "OGG".equals(fonction)) {
						
							addToList.setText(language.getProperty("filesVideoOrAudio"));
							caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpSetAudio.setVisible(false);														
							grpAudio.setVisible(true);
							grpAudio.setLocation(grpAudio.getX(), 59);
							grpAudio.add(lbl48k);
							lbl48k.setLocation(caseSampleRate.getLocation().x + caseSampleRate.getWidth() + 3, caseSampleRate.getLocation().y + 3);							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), grpAudio.getSize().height + grpAudio.getLocation().y + 6);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(false);
							spinnerVideoFadeIn.setEnabled(false);
							caseVideoFadeOut.setEnabled(false);
							spinnerVideoFadeOut.setEnabled(false);	
							
							// grpAdvanced
							grpAdvanced.removeAll();
							grpAdvanced.setVisible(true);
							caseCreateTree.setLocation(7, 14);
							grpAdvanced.add(caseCreateTree);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
							
						} else if ("Loudness & True Peak".equals(fonction)
						|| language.getProperty("functionBlackDetection").equals(fonction)
						|| language.getProperty("functionOfflineDetection").equals(fonction)
						|| language.getProperty("functionInsert").equals(fonction))
						{

							if (language.getProperty("functionBlackDetection").equals(fonction)	|| language.getProperty("functionOfflineDetection").equals(fonction))
							{
								addToList.setText(language.getProperty("filesVideo"));
							}
							else if (language.getProperty("functionInsert").equals(fonction))
							{
								addToList.setText(language.getProperty("fileMaster"));
							}
							else
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							
							caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), 59);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(btnReset.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
						} else if ("XDCAM HD422".equals(fonction) || "AVC-Intra 100".equals(fonction) || ("XAVC").equals(fonction) || "HAP".equals(fonction) || "FFV1".equals(fonction)) {
							
							if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422") && caseAS10.isSelected())
							{
								final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mxf"});						
								comboFilter.setModel(model);
							}
							
							//grpSetAudio
							grpSetAudio.removeAll();
							lblAudio1.setLocation(13, 18);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							grpSetAudio.add(lblAudio3);
							grpSetAudio.add(comboAudio3);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							grpSetAudio.add(lblAudio4);
							grpSetAudio.add(comboAudio4);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							grpSetAudio.add(lblAudio5);
							grpSetAudio.add(comboAudio5);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							grpSetAudio.add(lblAudio6);
							grpSetAudio.add(comboAudio6);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							grpSetAudio.add(lblAudio7);
							grpSetAudio.add(comboAudio7);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							grpSetAudio.add(lblAudio8);
							grpSetAudio.add(comboAudio8);
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 4
								&& comboAudio6.getSelectedIndex() == 5
								&& comboAudio7.getSelectedIndex() == 6
								&& comboAudio8.getSelectedIndex() == 7 && fonction.equals("HAP") == false && fonction.equals("FFV1") == false && caseAS10.isSelected() == false)
							{
								comboAudio5.setSelectedIndex(16);
								comboAudio6.setSelectedIndex(16);
								comboAudio7.setSelectedIndex(16);
								comboAudio8.setSelectedIndex(16);
							}
							else if (comboAudio1.getSelectedIndex() == 0
									&& comboAudio2.getSelectedIndex() == 1
									&& comboAudio3.getSelectedIndex() == 2
									&& comboAudio4.getSelectedIndex() == 3
									&& comboAudio5.getSelectedIndex() == 16
									&& comboAudio6.getSelectedIndex() == 16
									&& comboAudio7.getSelectedIndex() == 16
									&& comboAudio8.getSelectedIndex() == 16 && (fonction.equals("HAP") || fonction.equals("FFV1") || caseAS10.isSelected()))
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}
							
							// Ajout partie résolution
							grpResolution.removeAll();
							
							grpResolution.setVisible(true);
							grpResolution.setBounds(grpResolution.getX(), 59, 312, 125);
							
							grpResolution.add(lblTaille);
							grpResolution.add(iconTVResolution);
							
							lblPad.setBounds(comboResolution.getLocation().x + comboResolution.getWidth() + 5, comboResolution.getLocation().y + 3, 70, 16);
							grpResolution.add(lblPad);
							
							if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
							}
							
							grpResolution.add(comboResolution);
							
							if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422") || comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100"))
							{
								if (comboResolution.getItemCount() > 3)
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "1920x1080", "1280x720" }));			
							}
							else
							{
								if (comboResolution.getItemCount() != 12)
								{
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "4096x2160", "3840x2160", "1920x1080",
											"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180" }));
								}
							}
							
							// Ajout case rognage
							caseRognerImage.setLocation(7, 47);
							grpResolution.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpResolution.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,	caseRotate.getLocation().y + 3);
							grpResolution.add(comboRotate);
							
							// Ajout case force display
							caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							grpResolution.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpResolution.add(comboDAR);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y);
							grpResolution.add(caseMiror);			
							
							addToList.setText(language.getProperty("filesVideo"));
							if (subtitlesBurn)
								caseDisplay.setEnabled(true);
							else
								caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpH264.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);
							grpSetTimecode.setVisible(true);
							grpSetTimecode.setLocation(grpSetTimecode.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);							
							grpAudio.setVisible(false);
							grpSetAudio.setVisible(true);
							if (fonction.equals("HAP") == false && fonction.equals("FFV1") == false)
								grpSetAudio.setSize(312, 100);
							else if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(grpSetAudio.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							grpSetAudio.repaint();
							
							grpImageSequence.setVisible(true);
							grpImageSequence.setLocation(grpImageSequence.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);							
							grpColorimetry.setVisible(true);
							grpColorimetry.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(grpCorrections.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);	
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);							
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							//grpOverlay
							caseSubtitles.setLocation(7, 16);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
														
							// Case Blend location
							caseBlend.setLocation(7, caseEnableSequence.getLocation().y + caseEnableSequence.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 2);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth(), caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);
																				
							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();

							// grpAdvanced
							caseConform.setLocation(7, 14);
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);								
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);
														
							caseForcerProgressif.setLocation(7, caseConform.getLocation().y + 17);
							grpAdvanced.add(caseForcerProgressif);
							
							caseForcerDesentrelacement.setLocation(7, caseForcerProgressif.getLocation().y + 17);
							grpAdvanced.add(caseForcerDesentrelacement);
							lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
							grpAdvanced.add(lblTFF);								
							comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
							grpAdvanced.add(comboForcerDesentrelacement);				
							
							caseForcerEntrelacement.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
							grpAdvanced.add(caseForcerEntrelacement);						
														
							if (fonction.equals("XDCAM HD422"))
							{
								caseAS10.setText(language.getProperty("caseAS10"));
								caseAS10.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
								grpAdvanced.add(caseAS10);
								comboAS10.setLocation(caseAS10.getX() + caseAS10.getWidth() + 4, caseAS10.getLocation().y + 4);
								grpAdvanced.add(comboAS10);
								casePreserveMetadata.setLocation(7, caseAS10.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);	
							}
							else if (fonction.equals("AVC-Intra 100"))
							{		
								caseAS10.setText(language.getProperty("caseAS10").replace("10" + language.getProperty("colon"), "11").replace("10 format" + language.getProperty("colon"), "11 format"));
								caseAS10.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
								grpAdvanced.add(caseAS10);
								casePreserveMetadata.setLocation(7, caseAS10.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);	
							}
							else
							{
								casePreserveMetadata.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);	
							}

							// grpCorrections
							caseBanding.setLocation(7, 14);
							grpCorrections.add(caseBanding);
							caseLimiter.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseLimiter);
							caseDetails.setLocation(7, caseLimiter.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 2);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth(), caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 2);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth(), caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);						
							
						} else if ("DNxHD".equals(fonction) || "DNxHR".equals(fonction) || "Apple ProRes".equals(fonction) || "QT Animation".equals(fonction) || ("GoPro CineForm").equals(fonction) || "Uncompressed YUV".equals(fonction) ) {
							
							addToList.setText(language.getProperty("filesVideoOrPicture"));			
														
							if (comboFonctions.getSelectedItem().equals("QT Animation") || subtitlesBurn == false)
								caseDisplay.setEnabled(false);
							else
								caseDisplay.setEnabled(true);
							
							if (comboFonctions.getSelectedItem().toString().equals("DNxHD") || comboFonctions.getSelectedItem().toString().equals("DNxHR"))
							{
								caseCreateOPATOM.setEnabled(true);
								lblCreateOPATOM.setEnabled(true);
								if ((caseCreateOPATOM.isSelected() || caseCreateTree.isSelected()) && grpDestination.getTabCount() > 2)
								{
									setDestinationTabs(2);		
								}
							}
							else
							{
								caseCreateOPATOM.setEnabled(false);		
								caseCreateOPATOM.setSelected(false);
								lblCreateOPATOM.setEnabled(false);	
							}
							
							if (comboFilter.getSelectedItem().toString().equals("36")) {
								caseForcerEntrelacement.setEnabled(false);
								caseForcerInversion.setEnabled(false);
								caseForcerEntrelacement.setSelected(false);
								caseForcerInversion.setSelected(false);
							} else {
								caseForcerEntrelacement.setEnabled(true);
								caseForcerInversion.setEnabled(true);
							}
							
							// Ajout partie résolution
							grpResolution.removeAll();
							
							grpResolution.setVisible(true);
							grpResolution.setBounds(grpResolution.getX(), 59, 312, 125);
							
							grpResolution.add(lblTaille);
							grpResolution.add(iconTVResolution);
							
							lblPad.setBounds(comboResolution.getLocation().x + comboResolution.getWidth() + 5, comboResolution.getLocation().y + 3, 70, 16);
							grpResolution.add(lblPad);

							if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
							}
							
							grpResolution.add(comboResolution);
							
							if (comboFonctions.getSelectedItem().toString().equals("DNxHD"))
							{
								if (comboResolution.getItemCount() > 3)
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "1920x1080", "1280x720" }));			
							}
							else
							{
								if (comboResolution.getItemCount() != 12)
								{
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "4096x2160", "3840x2160", "1920x1080",
											"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180" }));
								}
							}
							
							// Ajout case rognage
							caseRognerImage.setLocation(7, 47);
							grpResolution.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpResolution.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,	caseRotate.getLocation().y + 3);
							grpResolution.add(comboRotate);
							
							// Ajout case force display
							caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							grpResolution.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpResolution.add(comboDAR);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y);
							grpResolution.add(caseMiror);

							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);							
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);
							grpSetTimecode.setVisible(true);
							grpSetTimecode.setLocation(grpSetTimecode.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(grpSetAudio.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							grpImageSequence.setVisible(true);
							grpImageSequence.setLocation(grpImageSequence.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(true);
							grpColorimetry.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(grpCorrections.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);	
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							//grpSetAudio
							grpSetAudio.removeAll();
							lblAudio1.setLocation(13, 18);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							grpSetAudio.add(lblAudio3);
							grpSetAudio.add(comboAudio3);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							grpSetAudio.add(lblAudio4);
							grpSetAudio.add(comboAudio4);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							grpSetAudio.add(lblAudio5);
							grpSetAudio.add(comboAudio5);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							grpSetAudio.add(lblAudio6);
							grpSetAudio.add(comboAudio6);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							grpSetAudio.add(lblAudio7);
							grpSetAudio.add(comboAudio7);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							grpSetAudio.add(lblAudio8);
							grpSetAudio.add(comboAudio8);	
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 16
								&& comboAudio6.getSelectedIndex() == 16
								&& comboAudio7.getSelectedIndex() == 16
								&& comboAudio8.getSelectedIndex() == 16)
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}
				
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							//grpOverlay
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);	
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
														
							// Case Blend location
							caseBlend.setLocation(7, caseEnableSequence.getLocation().y + caseEnableSequence.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 2);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth(), caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);

							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();
							
							caseConform.setLocation(7, 14);
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);
							
							caseForcerProgressif.setLocation(7, caseConform.getLocation().y + 17);
							grpAdvanced.add(caseForcerProgressif);
														
							caseForcerDesentrelacement.setLocation(7, caseForcerProgressif.getLocation().y + 17);
							grpAdvanced.add(caseForcerDesentrelacement);
							lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
							grpAdvanced.add(lblTFF);								
							comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
							grpAdvanced.add(comboForcerDesentrelacement);	
														
							if (comboFonctions.getSelectedItem().toString().equals("DNxHR") == false)
							{
								caseForcerEntrelacement.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
								grpAdvanced.add(caseForcerEntrelacement);
								
								caseForcerInversion.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
								grpAdvanced.add(caseForcerInversion);
							}

							if (comboFonctions.getSelectedItem().equals("GoPro CineForm"))
							{
								caseAlpha.setLocation(7, caseForcerInversion.getY() + 17);
								grpAdvanced.add(caseAlpha);	
								caseCreateTree.setLocation(7, caseAlpha.getLocation().y + 17);
							}
							else
							{
								if (comboFonctions.getSelectedItem().toString().equals("DNxHR"))
									caseCreateTree.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
								else
									caseCreateTree.setLocation(7, caseForcerInversion.getLocation().y + 17);
							}							
							grpAdvanced.add(caseCreateTree);
							
							casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
							grpAdvanced.add(casePreserveMetadata);							
							caseCreateOPATOM.setLocation(7, casePreserveMetadata.getLocation().y + 17);
							grpAdvanced.add(caseCreateOPATOM);						
							lblOPATOM.setLocation(caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y + 3);
							grpAdvanced.add(lblOPATOM);
							lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y);
							grpAdvanced.add(lblCreateOPATOM);	
							caseOPATOM.setLocation(7, caseCreateOPATOM.getLocation().y + 17);
							grpAdvanced.add(caseOPATOM);

							// grpCorrections
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
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 2);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth(), caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 2);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth(), caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
							caseExposure.setLocation(7, caseBruit.getLocation().y + 17);
							grpCorrections.add(caseExposure);						
							iconTVExposure.setLocation(289, caseExposure.getLocation().y + 2);
							grpCorrections.add(iconTVExposure);							
							sliderExposure.setLocation(iconTVExposure.getX() - sliderExposure.getWidth(), caseExposure.getLocation().y);
							grpCorrections.add(sliderExposure);
							
						} else if ("H.264".equals(fonction) || "H.265".equals(fonction)) {

							addToList.setText(language.getProperty("filesVideoOrPicture"));			
							if (subtitlesBurn)
								caseDisplay.setEnabled(true);
							else
								caseDisplay.setEnabled(false);

							if (caseAccel.isSelected() == false)
							{
								caseForcerEntrelacement.setEnabled(true);								
								lblVBR.setVisible(true);
								if (caseQMax.isSelected() == false)
									caseForcePreset.setEnabled(true);
								caseForceTune.setEnabled(true);
								caseForceOutput.setEnabled(true);
								
								if (lblVBR.getText().equals("CQ"))
								{
									taille.setText("-");
									lblDbitVido.setText(language.getProperty("lblValue"));
									lblKbsH264.setVisible(false);
									h264lines.setVisible(false);					
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
							}
							else {
								caseForcerEntrelacement.setSelected(false);
								caseForcerEntrelacement.setEnabled(false);									
								caseForceTune.setSelected(false);
								caseForceTune.setEnabled(false);
								comboForceTune.setEnabled(false);
								case2pass.setSelected(false);
								case2pass.setEnabled(false);
							}

							if ("H.264".equals(fonction))
							{															

								if (caseAccel.isSelected() && comboForceProfile.getModel().getSize() != 3 || comboForceProfile.getModel().getElementAt(0).toString().equals("base") == false)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high"}));
									comboForceProfile.setSelectedIndex(2);	
								}
								else if (comboForceProfile.getModel().getSize() != 5)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high", "high422", "high444"}));																		
									comboForceProfile.setSelectedIndex(2);		
								}
															
								if (comboForceTune.getModel().getSize() != 8)
								{
									comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "film", "animation", "grain", "stillimage", "fastdecode", "zerolatency", "psnr", "ssim" }));
									comboForceTune.setSelectedIndex(0);
								}
							}
							else 
							{
								if (caseAccel.isSelected() && comboForceProfile.getModel().getSize() != 1)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main"}));
									comboForceProfile.setSelectedIndex(0);
								}
								else if (comboForceProfile.getModel().getSize() != 3 || comboForceProfile.getModel().getElementAt(0).toString().equals("main") == false)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main", "main422", "main444"}));
									comboForceProfile.setSelectedIndex(0);
								}																		
								
								if (comboForceTune.getModel().getSize() != 6)
								{
									comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "grain", "animation", "fastdecode", "zerolatency", "psnr", "ssim" }));
									comboForceTune.setSelectedIndex(0);
								}
							}
							
							//Presets
							if (caseAccel.isSelected())
							{
								if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync"))
								{
									if (comboForcePreset.getModel().getSize() != 7)
									{
										comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow"}));
										comboForcePreset.setSelectedIndex(3);
									}
								}
				    			else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
				    			{
				    				caseForcePreset.setSelected(false);
			    					caseForcePreset.setEnabled(false);
			    					comboForcePreset.setEnabled(false);
				    			}	
							}
			    			else 
			    			{
			    				if (comboForcePreset.getModel().getSize() != 10)
			    				{
				    				comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow", "placebo"}));
				    				comboForcePreset.setSelectedIndex(5);
			    				}
			    			}

							lblNiveaux.setVisible(true);
							grpResolution.setVisible(false);
							grpH264.setVisible(true);

							if (grpAdvanced.getHeight() > 181)
								grpH264.setLocation(grpH264.getX(), 59 - (grpAdvanced.getHeight() - 181));
							else
								grpH264.setLocation(grpH264.getX(), 59);
							
							if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
							}
							lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 5, comboH264Taille.getLocation().y + 3, 70, 16);
							grpH264.add(lblPad);

							grpSetTimecode.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), grpH264.getSize().height + grpH264.getLocation().y + 6);
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(grpSetAudio.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							if (comboAudioCodec.getItemCount() != 9 || comboAudioCodec.getModel().getElementAt(0).equals("AAC") == false)
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "AC3", "OPUS", "FLAC", "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
								comboAudioCodec.setSelectedIndex(0);
								caseChangeAudioCodec.setSelected(true);
								comboAudioCodec.setEnabled(true);
								
								debitAudio.setModel(comboAudioBitrate.getModel());
								debitAudio.setSelectedIndex(1);
							}
							caseChangeAudioCodec.setEnabled(false);
							grpSetAudio.add(comboAudioCodec);
							grpSetAudio.add(lblAudioMapping);
							grpSetAudio.add(lbl48k);
							lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);							
							
							lblAudio1.setLocation(13, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							
							if (lblAudioMapping.getText().equals("Multi"))
							{	
								grpSetAudio.add(lblAudio3);
								grpSetAudio.add(comboAudio3);
								grpSetAudio.add(lblAudio4);
								grpSetAudio.add(comboAudio4);
								grpSetAudio.add(lblAudio5);
								grpSetAudio.add(comboAudio5);
								grpSetAudio.add(lblAudio6);
								grpSetAudio.add(comboAudio6);
								grpSetAudio.add(lblAudio7);
								grpSetAudio.add(comboAudio7);
								grpSetAudio.add(lblAudio8);
								grpSetAudio.add(comboAudio8);
							}
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 16
								&& comboAudio6.getSelectedIndex() == 16
								&& comboAudio7.getSelectedIndex() == 16
								&& comboAudio8.getSelectedIndex() == 16)
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							grpImageSequence.setVisible(true);
							grpImageSequence.setLocation(grpImageSequence.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
														
							grpAudio.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(true);
							grpColorimetry.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);			
							
							if ("H.264".equals(fonction))
							{
								if (comboColorspace.getItemCount() != 4)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits"}));
							}
							else
							{
								if (comboColorspace.getItemCount() != 7)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));
							}
							
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(grpCorrections.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);	
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							// CalculH264
							if (liste.getSize() > 0 && FFPROBE.calcul == false)
								FFPROBE.CalculH264();
							// Qualité Max
							caseQMax.setEnabled(true);
							
							// Ajout case rognage
							caseRognerImage.setLocation(7,
									caseEnableSequence.getHeight() + caseEnableSequence.getLocation().y);
							grpImageSequence.add(caseRognerImage);
							
							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpImageSequence.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpImageSequence.add(comboRotate);
							
							// Ajout case force display
							caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							grpImageSequence.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpImageSequence.add(comboDAR);
							
							// Case Blend location
							caseBlend.setLocation(7, caseForcerDAR.getLocation().y + caseForcerDAR.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 2);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth(), caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y);
							grpImageSequence.add(caseMiror);

							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();

							// grpAdvanced
							caseAccel.setLocation(7, 14);
							grpAdvanced.add(caseAccel);
							comboAccel.setLocation(caseAccel.getLocation().x + caseAccel.getWidth() + 4, caseAccel.getLocation().y + 4);
							grpAdvanced.add(comboAccel);
																					
							caseForcerDesentrelacement.setLocation(7, caseAccel.getLocation().y + 17);
							grpAdvanced.add(caseForcerDesentrelacement);
							lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
							grpAdvanced.add(lblTFF);								
							comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
							grpAdvanced.add(comboForcerDesentrelacement);
							
							caseForcerEntrelacement.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
							grpAdvanced.add(caseForcerEntrelacement);
							
							caseForceOutput.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
							grpAdvanced.add(caseForceOutput);
							lblNiveaux.setLocation(caseForceOutput.getLocation().x + caseForceOutput.getWidth() + 4, caseForceOutput.getLocation().y + 4);
							grpAdvanced.add(lblNiveaux);							
							caseForceLevel.setLocation(7, caseForceOutput.getLocation().y + 17);
							grpAdvanced.add(caseForceLevel);
							comboForceProfile.setLocation(caseForceLevel.getLocation().x + caseForceLevel.getWidth() + 4, caseForceLevel.getLocation().y + 4);
							grpAdvanced.add(comboForceProfile);
							comboForceLevel.setLocation(comboForceProfile.getLocation().x + comboForceProfile.getWidth() + 4,comboForceProfile.getLocation().y);
							grpAdvanced.add(comboForceLevel);								
							caseForcePreset.setLocation(7, caseForceLevel.getLocation().y + 17);
							grpAdvanced.add(caseForcePreset);
							comboForcePreset.setLocation(caseForcePreset.getLocation().x + caseForcePreset.getWidth() + 4, caseForcePreset.getLocation().y + 4);
							grpAdvanced.add(comboForcePreset);								
							caseForceTune.setLocation(7, caseForcePreset.getLocation().y + 17);
							grpAdvanced.add(caseForceTune);
							comboForceTune.setLocation(caseForceTune.getLocation().x + caseForceTune.getWidth() + 4, caseForceTune.getLocation().y + 4);
							grpAdvanced.add(comboForceTune);									
							caseFastStart.setLocation(7, caseForceTune.getLocation().y + 17);
							grpAdvanced.add(caseFastStart);
							caseGOP.setLocation(7, caseFastStart.getLocation().y + 17);
							grpAdvanced.add(caseGOP);
							gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
							grpAdvanced.add(gopSize);
							
							caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);
							grpAdvanced.add(caseDecimate);
									
							caseConform.setLocation(7, caseDecimate.getLocation().y + 17);
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);
							caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
							grpAdvanced.add(caseCreateTree);
							casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
							grpAdvanced.add(casePreserveMetadata);
							casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
							grpAdvanced.add(casePreserveSubs);							
							
							// grpCorrections
							caseStabilisation.setLocation(7, 14);
							grpCorrections.add(caseStabilisation);
							caseDeflicker.setLocation(7, caseStabilisation.getLocation().y + 17);
							grpCorrections.add(caseDeflicker);
							caseBanding.setLocation(7, caseDeflicker.getLocation().y + 17);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 2);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth(), caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 2);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth(), caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
							
						} else if ("WMV".equals(fonction) || "MPEG".equals(fonction) || "VP8".equals(fonction) || "VP9".equals(fonction) || "AV1".equals(fonction) || "OGV".equals(fonction)
								|| "MJPEG".equals(fonction) || "Xvid".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideoOrPicture"));	
							
							if (subtitlesBurn)
							{
								caseDisplay.setEnabled(true);
							}
							else
								caseDisplay.setEnabled(false);
							
							case2pass.setEnabled(true);
							lblNiveaux.setVisible(true);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(true);
							grpH264.setLocation(grpH264.getX(), 59);
							
							if ("VP9".equals(fonction) || "AV1".equals(fonction))
							{			
								lblVBR.setVisible(true);
								
								if (lblVBR.getText().equals("CQ"))
								{
									taille.setText("-");
									lblDbitVido.setText(language.getProperty("lblValue"));
									lblKbsH264.setVisible(false);
									h264lines.setVisible(false);					
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
							}
							else
							{
								lblVBR.setVisible(false);
								case2pass.setEnabled(true);								
								
								if (lblVBR.getText().equals("CQ")) //Si la fonction ne prend pas en charge CQ
								{
									debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
									debitVideo.setSelectedIndex(8);
									lblDbitVido.setText(language.getProperty("lblDbitVido"));
									lblKbsH264.setVisible(true);
									h264lines.setVisible(true);
									FFPROBE.CalculH264();
								}
								lblVBR.setText("VBR");
							}
							
							if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
							}
							lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 7, comboH264Taille.getLocation().y + 3, 70, 16);
							grpH264.add(lblPad);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), grpH264.getSize().height + grpH264.getLocation().y + 6);
							
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(grpSetAudio.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							
							if (comboAudioCodec.getItemCount() != 5 && "MJPEG".equals(fonction))
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
								comboAudioCodec.setSelectedIndex(2);
								
								debitAudio.setModel(comboAudioBitrate.getModel());
								debitAudio.setSelectedIndex(0);
							}
							else if ("MJPEG".equals(fonction) == false)
							{
								if (comboAudioCodec.getModel().getElementAt(0).equals("WMA") == false && "WMV".equals(fonction))
								{
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
								}
								else if (comboAudioCodec.getModel().getElementAt(0).equals("MP2") == false && "MPEG".equals(fonction))
								{
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
								}
								else if (comboAudioCodec.getModel().getElementAt(0).equals("OPUS") == false && ("VP8".equals(fonction) || "VP9".equals(fonction) || "AV1".equals(fonction)))
								{
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "OPUS", "OGG", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
								}
								else if (comboAudioCodec.getModel().getElementAt(0).equals("OGG") == false && "OGV".equals(fonction))
								{
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "OGG", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
								}
								else if (comboAudioCodec.getModel().getElementAt(0).equals("MP3") == false && "Xvid".equals(fonction))
								{
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
								}
							}
							caseChangeAudioCodec.setEnabled(false);	
							caseChangeAudioCodec.setSelected(true);
							comboAudioCodec.setEnabled(true);	
							grpSetAudio.add(comboAudioCodec);
							grpSetAudio.add(lblAudioMapping);
							grpSetAudio.add(lbl48k);
							lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
							
							lblAudio1.setLocation(13, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							
							if (lblAudioMapping.getText().equals("Multi"))
							{	
								grpSetAudio.add(lblAudio3);
								grpSetAudio.add(comboAudio3);
								grpSetAudio.add(lblAudio4);
								grpSetAudio.add(comboAudio4);
								grpSetAudio.add(lblAudio5);
								grpSetAudio.add(comboAudio5);
								grpSetAudio.add(lblAudio6);
								grpSetAudio.add(comboAudio6);
								grpSetAudio.add(lblAudio7);
								grpSetAudio.add(comboAudio7);
								grpSetAudio.add(lblAudio8);
								grpSetAudio.add(comboAudio8);
							}
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 16
								&& comboAudio6.getSelectedIndex() == 16
								&& comboAudio7.getSelectedIndex() == 16
								&& comboAudio8.getSelectedIndex() == 16)
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							grpImageSequence.setVisible(true);
							grpImageSequence.setLocation(grpImageSequence.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);												
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);

							grpAudio.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(true);
							grpColorimetry.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							
							if ("VP9".equals(fonction) || "AV1".equals(fonction))
							{
								if (comboColorspace.getItemCount() != 7)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));							
							}
							else
							{
								if (comboColorspace.getItemCount() != 3)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							}
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(grpCorrections.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);	
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							// CalculH264
							if (liste.getSize() > 0 && FFPROBE.calcul == false)
								FFPROBE.CalculH264();
							// Qualité Max
							if (comboFonctions.getSelectedItem().equals("OGV")
									|| comboFonctions.getSelectedItem().equals("MJPEG"))
								caseQMax.setEnabled(false);
							else
								caseQMax.setEnabled(true);
							
							// Ajout case rognage
							caseRognerImage.setLocation(7,
									caseEnableSequence.getHeight() + caseEnableSequence.getLocation().y);
							grpImageSequence.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpImageSequence.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpImageSequence.add(comboRotate);
							
							// Ajout case force display
							caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							grpImageSequence.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpImageSequence.add(comboDAR);

							// Case Blend location
							caseBlend.setLocation(7, caseForcerDAR.getLocation().y + caseForcerDAR.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 2);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth(), caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);
							
							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpImageSequence.add(caseMiror);
							
							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();

							// grpAdvanced	
							if ("VP9".equals(fonction))
							{
								caseAccel.setLocation(7, 14);
								grpAdvanced.add(caseAccel);
								comboAccel.setLocation(caseAccel.getLocation().x + caseAccel.getWidth() + 4, caseAccel.getLocation().y + 4);
								grpAdvanced.add(comboAccel);
								
								caseForcerDesentrelacement.setLocation(7, caseAccel.getY() + 17);	
								
								lblVBR.setVisible(true);
								
								if (lblVBR.getText().equals("CQ") || caseAccel.isSelected())
								{
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
							}
							else
								caseForcerDesentrelacement.setLocation(7, 14);
							
							grpAdvanced.add(caseForcerDesentrelacement);
							lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
							grpAdvanced.add(lblTFF);								
							comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
							grpAdvanced.add(comboForcerDesentrelacement);
																						
							if ("VP8".equals(fonction) || "VP9".equals(fonction))
							{																													
								if (caseQMax.isSelected() == false)
								{
									caseForceQuality.setEnabled(true);
									caseForcePreset.setEnabled(true);
								}
								
								if (caseAccel.isSelected() == false)
								{
									caseForceTune.setEnabled(true);
								}
								
								if (comboForceTune.getModel().getSize() != 3)
								{
									comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "default", "screen", "film" }));
									comboForceTune.setSelectedIndex(0);
								}
																
								caseForceQuality.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
								grpAdvanced.add(caseForceQuality);
								comboForceQuality.setLocation(caseForceQuality.getLocation().x + caseForceQuality.getWidth() + 4, caseForceQuality.getLocation().y + 4);
								grpAdvanced.add(comboForceQuality);																
								caseForceSpeed.setLocation(7, caseForceQuality.getLocation().y + 17);
								grpAdvanced.add(caseForceSpeed);
								comboForceSpeed.setLocation(caseForceSpeed.getLocation().x + caseForceSpeed.getWidth() + 4, caseForceSpeed.getLocation().y + 4);
								grpAdvanced.add(comboForceSpeed);								
								caseForceTune.setLocation(7, caseForceSpeed.getLocation().y + 17);
								grpAdvanced.add(caseForceTune);
								comboForceTune.setLocation(caseForceTune.getLocation().x + caseForceTune.getWidth() + 4, caseForceTune.getLocation().y + 4);
								grpAdvanced.add(comboForceTune);								
								caseAlpha.setLocation(7, caseForceTune.getY() + 17);
								grpAdvanced.add(caseAlpha);								
								caseFastStart.setLocation(7, caseAlpha.getLocation().y + 17);
								grpAdvanced.add(caseFastStart);
								caseGOP.setLocation(7, caseFastStart.getLocation().y + 17);
								grpAdvanced.add(caseGOP);
								gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
								grpAdvanced.add(gopSize);								
								caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);	
							}
							else if ("AV1".equals(fonction))
							{
								if (caseQMax.isSelected() == false)
									caseForceLevel.setEnabled(true);
								
								if (comboForceProfile.getModel().getSize() != 1)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main"}));
									comboForceProfile.setSelectedIndex(0);
								}								
								
								caseForceLevel.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
								grpAdvanced.add(caseForceLevel);								
								comboForceProfile.setLocation(caseForceLevel.getLocation().x + caseForceLevel.getWidth() + 4, caseForceLevel.getLocation().y + 4);
								grpAdvanced.add(comboForceProfile);									
								comboForceLevel.setLocation(comboForceProfile.getLocation().x + comboForceProfile.getWidth() + 4,comboForceProfile.getLocation().y);
								grpAdvanced.add(comboForceLevel);									
								caseForceSpeed.setLocation(7, caseForceLevel.getLocation().y + 17);
								grpAdvanced.add(caseForceSpeed);
								comboForceSpeed.setLocation(caseForceSpeed.getLocation().x + caseForceSpeed.getWidth() + 4, caseForceSpeed.getLocation().y + 4);
								grpAdvanced.add(comboForceSpeed);
								caseFastStart.setLocation(7, caseForceSpeed.getLocation().y + 17);
								grpAdvanced.add(caseFastStart);
								caseGOP.setLocation(7, caseFastStart.getLocation().y + 17);
								grpAdvanced.add(caseGOP);
								gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
								grpAdvanced.add(gopSize);								
								caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);
							}	
							else
							{
								caseDecimate.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
							}							
							grpAdvanced.add(caseDecimate);
							
							caseConform.setLocation(7, caseDecimate.getY() + 17);								
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);
							caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
							grpAdvanced.add(caseCreateTree);
							casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
							grpAdvanced.add(casePreserveMetadata);
							casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
							grpAdvanced.add(casePreserveSubs);	

							// grpCorrections
							caseStabilisation.setLocation(7, 14);
							grpCorrections.add(caseStabilisation);
							caseDeflicker.setLocation(7, caseStabilisation.getLocation().y + 17);
							grpCorrections.add(caseDeflicker);
							caseBanding.setLocation(7, caseDeflicker.getLocation().y + 17);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 2);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth(), caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 2);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth(), caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
							
						} else if ("DV PAL".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideo"));
							caseDisplay.setEnabled(true);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(grpInAndOut.getX(), 59);
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(btnReset.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							
						} else if ("DVD".equals(fonction) || "Blu-ray".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideo"));
							if (comboFonctions.getSelectedItem().equals("DVD") || subtitlesBurn == false)
								caseDisplay.setEnabled(false);
							else
								caseDisplay.setEnabled(true);
														
							caseForcerProgressif.setEnabled(true);
							grpImageSequence.setVisible(false);
							grpImageFilter.setVisible(false);
							grpColorimetry.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							if ("Blu-ray".equals(fonction))
							{
								grpH264.setVisible(true);
								grpH264.setLocation(grpH264.getX(), 59);
								
								if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
									lblPad.setVisible(false);
								else
								{
									lblPad.setVisible(true);
								}
								lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 7, comboH264Taille.getLocation().y + 3, 70, 16);
								grpH264.add(lblPad);

								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(grpInAndOut.getX(), grpH264.getSize().height + grpH264.getLocation().y + 6);
							}
							else
							{
								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(grpInAndOut.getX(), 59);
							}
							
							
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(grpSetAudio.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							
							caseChangeAudioCodec.setSelected(true);
							comboAudioCodec.setEnabled(true);
														
							if (comboAudioCodec.getItemCount() != 3)
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
								comboAudioCodec.setSelectedIndex(0);						
								debitAudio.setModel(comboAudioBitrate.getModel());
								
								if (comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
									debitAudio.setSelectedIndex(3);
								else
									debitAudio.setSelectedIndex(1);
							}
							caseChangeAudioCodec.setEnabled(false);
		
							grpSetAudio.add(comboAudioCodec);
							grpSetAudio.add(lblAudioMapping);
							grpSetAudio.add(lbl48k);
							lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
							
							lblAudio1.setLocation(13, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 13, lblAudio1.getLocation().y);
							comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
							grpSetAudio.add(lblAudio2);
							grpSetAudio.add(comboAudio2);
							
							lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
							comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
							lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
							comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
							lblAudio5.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
							comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
							lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
							comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
							lblAudio7.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
							comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
							lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
							comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
							
							if (lblAudioMapping.getText().equals("Multi"))
							{	
								grpSetAudio.add(lblAudio3);
								grpSetAudio.add(comboAudio3);
								grpSetAudio.add(lblAudio4);
								grpSetAudio.add(comboAudio4);
								grpSetAudio.add(lblAudio5);
								grpSetAudio.add(comboAudio5);
								grpSetAudio.add(lblAudio6);
								grpSetAudio.add(comboAudio6);
								grpSetAudio.add(lblAudio7);
								grpSetAudio.add(comboAudio7);
								grpSetAudio.add(lblAudio8);
								grpSetAudio.add(comboAudio8);
							}
							
							if (comboAudio1.getSelectedIndex() == 0
								&& comboAudio2.getSelectedIndex() == 1
								&& comboAudio3.getSelectedIndex() == 2
								&& comboAudio4.getSelectedIndex() == 3
								&& comboAudio5.getSelectedIndex() == 16
								&& comboAudio6.getSelectedIndex() == 16
								&& comboAudio7.getSelectedIndex() == 16
								&& comboAudio8.getSelectedIndex() == 16)
							{
								comboAudio1.setSelectedIndex(0);
								comboAudio2.setSelectedIndex(1);
								comboAudio3.setSelectedIndex(2);
								comboAudio4.setSelectedIndex(3);
								comboAudio5.setSelectedIndex(4);
								comboAudio6.setSelectedIndex(5);
								comboAudio7.setSelectedIndex(6);
								comboAudio8.setSelectedIndex(7);
							}
														
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(grpOverlay.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
							
							grpAudio.setVisible(false);
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(grpCorrections.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							caseVideoFadeIn.setEnabled(true);
							if (caseVideoFadeIn.isSelected())
								spinnerVideoFadeIn.setEnabled(true);
							caseVideoFadeOut.setEnabled(true);
							if (caseVideoFadeOut.isSelected())
								spinnerVideoFadeOut.setEnabled(true);	
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	;

							// CalculH264
							if (liste.getSize() > 0 && FFPROBE.calcul == false)
								FFPROBE.CalculH264();
							// Qualité Max
							caseQMax.setEnabled(true);
							
							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();

							// grpAdvanced
							caseForcerProgressif.setLocation(7, 14);													
							grpAdvanced.add(caseForcerProgressif);

							// grpCorrections
							caseBanding.setLocation(7, 14);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 2);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth(), caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 2);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth(), caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
							
						} else if (language.getProperty("functionPicture").equals(fonction) || "JPEG".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideoOrPicture"));
							caseDisplay.setEnabled(false);
							grpImageSequence.setVisible(false);	
							
							// Ajout partie résolution
							grpResolution.removeAll();
							
							grpResolution.setVisible(true);
							grpResolution.setBounds(grpResolution.getX(), 59, 312, 145);
							
							grpResolution.add(iconTVInterpret);							
							grpResolution.add(lblTaille);
							grpResolution.add(iconTVResolution);
							
							grpResolution.add(comboResolution);
							
							if (comboResolution.getItemCount() != 26)
							{
								comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "2:1", "4:1", "8:1", "16:1",
										"4096:auto", "1920:auto", "1280:auto", "auto:480", "auto:360",
										"4096x2160", "3840x2160", "1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000",
										"854x480", "720x576", "640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
							}

							// Ajout case rognage
							caseRognerImage.setLocation(7, 47);
							grpResolution.add(caseRognerImage);
							
							// Ajout de la quality pour l'extension .webp
							if (comboFilter.getSelectedItem().toString().equals(".webp"))
							{
								grpResolution.add(lblImageQuality);
								grpResolution.add(comboImageQuality);
							}
							else
							{
								grpResolution.remove(lblImageQuality);
								grpResolution.remove(comboImageQuality);
							}
							
							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpResolution.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,	caseRotate.getLocation().y + 3);
							grpResolution.add(comboRotate);
							
							// lblInterpretation location
							lblInterpretation.setLocation(28, caseCreateSequence.getLocation().y + caseCreateSequence.getHeight());
							grpResolution.add(lblInterpretation);							
							comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4, lblInterpretation.getLocation().y);
							grpResolution.add(comboInterpret);							
							lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblInterpretation.getLocation().y - 1);
							grpResolution.add(lblIsInterpret);							
							iconTVInterpret.setLocation(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY());
							grpResolution.add(iconTVInterpret);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpResolution.add(caseMiror);
							
							caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseCreateSequence.getPreferredSize().width, 23);
							caseCreateSequence.setEnabled(true);
							grpResolution.add(caseCreateSequence);
							
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setLocation(grpInAndOut.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							// grpOverlay
							grpOverlay.setVisible(true);
							caseShowDate.setLocation(7, 16);
							grpOverlay.add(caseShowDate);
							caseShowFileName.setLocation(7, caseShowDate.getHeight() + caseShowDate.getLocation().y);
							grpOverlay.add(caseShowFileName);
							caseLogo.setLocation(7, caseShowFileName.getHeight() + caseShowFileName.getLocation().y);
							grpOverlay.add(caseLogo);
							grpOverlay.setLocation(grpOverlay.getX(), grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);	
												
							// grpLuts
							grpColorimetry.setVisible(true);
							grpColorimetry.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							grpColorimetry.setSize(grpColorimetry.getWidth(), 165);
							
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpImageFilter.setVisible(true);
							grpImageFilter.setLocation(grpImageFilter.getX(),	grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(btnReset.getX(), grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);
							
						} else {
							
							if (language.getProperty("functionConform").equals(fonction) || language.getProperty("functionExtract").equals(fonction)) 
							{
								addToList.setText(language.getProperty("filesVideo"));
							}
							else if (language.getProperty("functionMerge").equals(fonction))
							{
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							}
							else if (language.getProperty("functionSubtitles").equals(fonction))
							{
								addToList.setText(language.getProperty("fileVideo"));
							}
							else if (language.getProperty("functionSceneDetection").equals(fonction))
							{
								addToList.setText(language.getProperty("fileVideo"));
							}
							else if (language.getProperty("itemMyFunctions").equals(fonction))
							{
								addToList.setText(language.getProperty("dropFilesHere"));
							}
							else if (comboFonctions.getEditor().getItem().toString().isEmpty())
							{
								addToList.setText(language.getProperty("dropFilesHere"));
							}
							else
								addToList.setText(language.getProperty(""));
							
							caseDisplay.setEnabled(false);
						}
						
						//Rafrachissement
						grpAdvanced.repaint();
						
						if (action) {
							int i2 = 680;
							do {
								long startTime = System.currentTimeMillis() + 2;
								
								changeGroupes = true;
								if (Settings.btnDisableAnimations.isSelected())
									i2 = 334;
								else
									i2 -= 4;
								grpResolution.setLocation(i2, grpResolution.getLocation().y);
								grpH264.setLocation(i2, grpH264.getLocation().y);
								grpSetTimecode.setLocation(i2, grpSetTimecode.getLocation().y);
								grpOverlay.setLocation(i2, grpOverlay.getLocation().y);
								grpSetAudio.setLocation(i2, grpSetAudio.getLocation().y);
								grpAudio.setLocation(i2, grpAudio.getLocation().y);
								grpInAndOut.setLocation(i2, grpInAndOut.getLocation().y);
								grpImageSequence.setLocation(i2, grpImageSequence.getLocation().y);
								grpImageFilter.setLocation(i2, grpImageFilter.getLocation().y);
								grpColorimetry.setLocation(i2, grpColorimetry.getLocation().y);
								grpCorrections.setLocation(i2, grpCorrections.getLocation().y);
								grpTransitions.setLocation(i2, grpTransitions.getLocation().y);
								grpAdvanced.setLocation(i2, grpAdvanced.getLocation().y);
								btnReset.setLocation((i2 + 2), btnReset.getLocation().y);

								//Animate size
								animateSections(startTime);	
								
							} while (i2 > 334);
							changeGroupes = false;
							changeSections(false); // une fois l'action terminé on vérifie que les groupes correspondent
						}

						// Important
						grpResolution.repaint();
						topPanel.repaint();
						statusBar.repaint();
						
					} catch (Exception e1) {
					}
				}
			}
		});
		changeSize.start();

	}

	private static void animateSections(long startTime) {
		
		long delay = startTime - System.currentTimeMillis();
        
    	if (delay > 0)
    	{		            		
        	long time = System.currentTimeMillis();
        	do {
            	try {
					Thread.sleep(0);
				} catch (InterruptedException e) {}
        	} while (System.currentTimeMillis() - time < delay);			            	
        }	
	}
	
	public static void changeFilters() {
		
		if (comboFonctions.getEditor().getItem().toString().length() == 0)
		{
			lblFilter.setText(language.getProperty("lblFilter"));			
			final String filtres[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
					".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp" };
			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
			comboFilter.setModel(model);
			comboFilter.setSelectedIndex(0);
		}		
		else
		{
			if (comboFonctions.getSelectedItem().toString().contains("H.26"))
			{
				lblFilter.setText("Ext." + language.getProperty("colon"));

				String[] extensions = new String[] { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts", ".m2ts" };
				
				if (comboFonctions.getSelectedItem().toString().equals("H.265") && caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
					extensions = new String[] {".mkv"};	
				
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform"))) {
				
				lblFilter.setText(Shutter.language.getProperty("lblTo"));
				final String filtres[] = {"23,976 " + Shutter.language.getProperty("fps"), "24 " + Shutter.language.getProperty("fps"), "25 " + Shutter.language.getProperty("fps"), "29,97 " + Shutter.language.getProperty("fps"), "30 " + Shutter.language.getProperty("fps"), "48 " + Shutter.language.getProperty("fps"), "50 " + Shutter.language.getProperty("fps"), "59,94 " + Shutter.language.getProperty("fps"), "60 " + Shutter.language.getProperty("fps") };				
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(2);
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio"))) {
				
				lblFilter.setText(Shutter.language.getProperty("lblAt"));
				final String filtres[] = {	language.getProperty("shortest"), language.getProperty("longest") };				
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);

			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))) {

				lblFilter.setText(Shutter.language.getProperty("lblTo"));
				final String filtres[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi",
						".mp4", ".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".webm"};
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
			
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionExtract"))) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				final String filtres[] = { language.getProperty("setAll"), language.getProperty("video"), language.getProperty("audio"), language.getProperty("subtitles")};
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
				
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionBlackDetection"))) {

				lblFilter.setText(Shutter.language.getProperty("levels"));
				String filtres[] = { "16-235", "0-255" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DV PAL")) {

				lblFilter.setText("Format");
				String filtres[] = { "16/9", "4/3" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("AV1")) {
				lblFilter.setText("Ext." + language.getProperty("colon"));
				
				String[] extensions = new String[] {".mkv", ".mp4", ".webm" };
				if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
					extensions = new String[] {".mkv"};				
				
				String filtres[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP8")) {
				lblFilter.setText("Ext." + language.getProperty("colon"));
							
				String[] extensions = new String[] { ".webm", ".mkv" };			
				
				String filtres[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP9")) {
				lblFilter.setText("Ext." + language.getProperty("colon"));
							
				String[] extensions = new String[] { ".webm", ".mkv", ".mp4" };
				if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
					extensions = new String[] {".mkv"};				
				
				String filtres[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("WAV")
					|| comboFonctions.getSelectedItem().toString().equals("AIFF")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				String filtres[] = { "16 Bits", "24 Bits", "32 Bits", "32 Float" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("FLAC")) {

				lblFilter.setText("Comp.:");
				String filtres[] = { "0","1","2","3","4","5","6","7","8","9","10","11","12" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(5);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("MP3")
					|| comboFonctions.getSelectedItem().toString().equals("AAC")
					|| comboFonctions.getSelectedItem().toString().equals("OGG")) {

				lblFilter.setText(language.getProperty("lblBitrate"));
				String filtres[] = { "320", "256", "192", "128", "96", "64" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("AC3")) {
				lblFilter.setText(language.getProperty("lblBitrate"));
				String filtres[] = { "640", "448", "384", "320", "256", "192", "128", "96", "64" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(1);
				
			} else if (comboFonctions.getSelectedItem().toString().equals("OPUS")) {
				lblFilter.setText(language.getProperty("lblBitrate"));
				String filtres[] = { "256", "192", "128", "96", "64" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
				
			} else if (comboFonctions.getSelectedItem().toString().equals("HAP")) {				
				lblFilter.setText("Type" + language.getProperty("colon"));
				DefaultComboBoxModel<Object> model;
				String filtres[] = { "Standard", "Alpha", "Q"};
				model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422") || comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")) {				
				lblFilter.setText("Ext." + language.getProperty("colon"));
				DefaultComboBoxModel<Object> model;
				String filtres[] = { ".mxf", ".mov"};
								
				model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHD")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				DefaultComboBoxModel<Object> model;
				if (comboResolution.getSelectedItem().toString().equals("1280x720")) {
					String filtres[] = { "60", "90", "90 X", "75", "110", "145", "220", "220 X" };
					model = new DefaultComboBoxModel<Object>(filtres);
					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(0);
					}
				} else {
					String filtres[] = { "36", "115", "175", "175 X", "36", "120", "185", "185 X", "45", "145", "220", "220 X", "75", "240", "365", "365 X", "90", "290", "440", "440 X"};
					model = new DefaultComboBoxModel<Object>(filtres);

					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(5);
					}
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHR")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				DefaultComboBoxModel<Object> model;
				String filtres[] = { "LB", "SQ", "HQ", "HQX", "444" };
				model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("Apple ProRes")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				String filtres[] = { "Proxy", "LT", "422", "422 HQ", "444", "4444", "4444 XQ" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				String filtres[] = { "Low", "Medium", "High", "Film Scan", "Film Scan 2", "Film Scan 3", "Film Scan 3+"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(3);
				}			

			} else if (comboFonctions.getSelectedItem().toString().equals("Uncompressed YUV")) {
				
				lblFilter.setText("Type" + language.getProperty("colon"));
				String filtres[] = { "8 Bits 422", "10 Bits 422" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("XAVC")) {
				lblFilter.setText(language.getProperty("lblBitrate"));
				String filtres[] = { "480", "960"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
				
			} else if (comboFonctions.getSelectedItem().toString().equals("MPEG")) {
				
				lblFilter.setText("Type" + language.getProperty("colon"));
				String filtres[] = { "version 1", "version 2" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString()
					.equals(language.getProperty("functionNormalization"))) {
				lblFilter.setText(" " + language.getProperty("at") + language.getProperty("colon"));
				String filtres[] = new String[31];

				filtres[0] = "0 LUFS";
				int i = 1;
				do {
					filtres[i] = ("-" + i + " LUFS");
					i++;
				} while (i < 31);

				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(23);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture"))) {
				lblFilter.setText("Ext." + language.getProperty("colon"));
				String filtres[] = { ".png", ".tif", ".tga", ".dpx", ".exr", ".bmp", ".ico", ".webp" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("JPEG")) {
				
				lblFilter.setText("Qual.:");
				String filtres[] = { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}	
			}
			else
			{
				lblFilter.setText(language.getProperty("lblFilter"));
				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg"))
				{
					lblFilter.setText("Ext." + language.getProperty("colon"));
				}
				
				final String filtres[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
						".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
			}
		}
	}

	private static void setDestinationTabs(int tabs) {	
		
		try {
			//Affichage des titres
			Font tabFont = new Font(montserratFont, Font.PLAIN, 11);		
			if (getLanguage.equals("English"))
				tabFont = new Font(montserratFont, Font.PLAIN, 10);		
			
			JLabel output = new JLabel(language.getProperty("output"));
			output.setFont(tabFont);
			JLabel output1 = new JLabel(language.getProperty("output") + "1");
			output1.setFont(tabFont);
			JLabel output2 = new JLabel(language.getProperty("output") + "2");
			output2.setFont(tabFont);
			JLabel output3 = new JLabel(language.getProperty("output") + "3");
			output3.setFont(tabFont);			
			JLabel ftpTab = new JLabel("FTP");
			ftpTab.setFont(tabFont);		
			JLabel wetransferTab = new JLabel(wetransferIcon);
			wetransferTab.setFont(tabFont);	
			JLabel mailTab = new JLabel(mailIcon);
			mailTab.setFont(tabFont);
			JLabel streamTab = new JLabel(streamIcon);	
			streamTab.setFont(tabFont);					
			
			boolean addWetransfer = true;
			
			String PathToWTCLIENT = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			PathToWTCLIENT = PathToWTCLIENT.substring(0,PathToWTCLIENT.length()-1);
			PathToWTCLIENT = PathToWTCLIENT.substring(0,(int) (PathToWTCLIENT.lastIndexOf("/"))).replace("%20", " ")  + "/Library/wtclient";
				
			if (System.getProperty("os.name").contains("Mac") && new File (PathToWTCLIENT).exists() == false)
			{
				addWetransfer = false;
				if (tabs > 2)
					tabs = tabs - 1;
			}
				
			if (tabs != grpDestination.getTabCount())
			{
				grpDestination.removeAll();
				
				//Ajout des titres
				if (tabs == 1)
				{
					grpDestination.addTab(language.getProperty("output"), null);
				}
				else if (tabs == 2)
				{
					grpDestination.addTab(language.getProperty("output"), destination1);
					grpDestination.addTab("Mail", destinationMail);	
					
					grpDestination.setTabComponentAt(0, output);
					grpDestination.setTabComponentAt(1, mailTab);
				}
				else if (addWetransfer && tabs == 6 || addWetransfer == false && tabs == 5)
				{			
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);	
					grpDestination.addTab("FTP", new JPanel());
					
					if (addWetransfer)
						grpDestination.addTab("WeTransfer", new JPanel());
					
					grpDestination.addTab("Mail", destinationMail);;
					
					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					
					if (addWetransfer)
					{	
						grpDestination.setTabComponentAt(4, wetransferTab);			
						grpDestination.setTabComponentAt(5, mailTab);
					}
					else
					{
						grpDestination.setTabComponentAt(4, mailTab);
					}
				}
				else if (addWetransfer && tabs == 7 || addWetransfer == false && tabs == 6)
				{			
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);	
					grpDestination.addTab("FTP", new JPanel());
					
					if (addWetransfer)
						grpDestination.addTab("WeTransfer", new JPanel());
					
					grpDestination.addTab("Mail", destinationMail);
					grpDestination.addTab("Stream", destinationStream);
					
					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					
					if (addWetransfer)
					{		
						grpDestination.setTabComponentAt(4, wetransferTab);			
						grpDestination.setTabComponentAt(5, mailTab);
						grpDestination.setTabComponentAt(6, streamTab);
					}
					else
					{
						grpDestination.setTabComponentAt(4, mailTab);
						grpDestination.setTabComponentAt(5, streamTab);
					}
				}
			}
		} catch (Exception e) {}
	}
	
	public static void disableAll() {

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChooseFiles.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(false);
			}
			fileList.setEnabled(false);
		}

		components = grpDestination.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination1.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination2.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination3.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destinationMail.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destinationStream.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageSequence.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageFilter.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpColorimetry.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSetTimecode.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSetAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpInAndOut.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpCorrections.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpTransitions.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		
		components = grpH264.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}

		lblFiles.setEnabled(true);
		lblFilesEnded.setEnabled(true);

		comboFonctions.setEnabled(false);
		comboFilter.setEnabled(false);
		btnReset.setEnabled(false);

		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		lblFilesEnded.setVisible(true);
		cancelled = false;
		btnCancel.setEnabled(true);

		if (inputDeviceIsRunning)
			progressBar1.setIndeterminate(true);
		
		progressBar1.setValue(0);

		if (FFMPEG.isRunning)
		{		
			if (caseForcerDAR.isSelected() == false && caseAddOverlay.isSelected() == false && caseLUTs.isSelected() == false && comboInColormatrix.getSelectedItem().toString().equals("HDR") == false && caseDisplay.isSelected() == false && caseDeflicker.isSelected() == false 
					|| grpColorimetry.isVisible() == false && grpCorrections.isVisible() == false && grpTransitions.isVisible() == false && grpAdvanced.isVisible() == false
					|| caseDisplay.isSelected())
			{
				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
					progressBar1.setValue(0);
				if (caseDisplay.isSelected() == false)
					caseRunInBackground.setEnabled(true);
	
				caseDisplay.setEnabled(false);
				btnStart.setEnabled(true);
				
				if (inputDeviceIsRunning || caseStream.isSelected())
					btnStart.setText(language.getProperty("btnStopRecording"));
				else
					btnStart.setText(language.getProperty("btnPauseFunction"));
			} 
			else
			{	
				btnStart.setEnabled(false);
				caseDisplay.setEnabled(false);
			}
		}
		
		Utils.textFieldBackground();
		
		frame.repaint();
	}

	public static void enableAll() {

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChooseFiles.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(true);
			}
			fileList.setEnabled(true);
		}

		components = grpDestination.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = destination1.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = destination2.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (lblDestination2.getText().equals(language.getProperty("aucune")))
				caseOpenFolderAtEnd2.setEnabled(false);					
		}
		components = destination3.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (lblDestination3.getText().equals(language.getProperty("aucune")))
				caseOpenFolderAtEnd3.setEnabled(false);		
			if (caseChangeFolder2.isSelected() == false)
				caseChangeFolder3.setEnabled(false);
				
		}
		components = destinationMail.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (caseSendMail.isSelected() == false)
				textMail.setEnabled(false);						
		}
		components = destinationStream.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (caseStream.isSelected() == false)
			{
				textStream.setEnabled(false);		
			}
		}
		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComboBox == false)
				components[i].setEnabled(true);
		}		
		
		//.webp
		comboImageQuality.setEnabled(true);
		
		if (caseCreateSequence.isSelected())
			comboInterpret.setEnabled(true);
		
		components = grpImageSequence.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseEnableSequence.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}
		if (caseEnableSequence.isSelected() == false)
			caseSequenceFPS.setEnabled(false);
		
		components = grpImageFilter.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}		
		
		if (caseYear.isSelected() == false)
			comboYear.setEnabled(false);
		
		if (caseMonth.isSelected() == false)
			comboMonth.setEnabled(false);

		if (caseYear.isSelected() == false)
			comboYear.setEnabled(false);	
		
		if (caseDay.isSelected() == false)
			comboDay.setEnabled(false);
		
		if (caseFrom.isSelected() == false)
		{
			comboFrom.setEnabled(false);
			comboTo.setEnabled(false);
		}
			
		comboResolution.setEnabled(true);

		if (caseRotate.isSelected())
			comboRotate.setEnabled(true);
		else
			comboRotate.setEnabled(false);			
		
		if (caseConvertAudioFramerate.isSelected())
		{
			comboAudioIn.setEnabled(true);
			comboAudioOut.setEnabled(true);		
		}
		else
		{
			comboAudioIn.setEnabled(false);
			comboAudioOut.setEnabled(false);	
		}

		components = grpColorimetry.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		if (caseLUTs.isSelected() == false)
			comboLUTs.setEnabled(false);
		
		if (caseColorspace.isSelected() == false)
			comboColorspace.setEnabled(false);
		
		if (caseLevels.isSelected() == false)
		{
			comboInLevels.setEnabled(false);
			comboOutLevels.setEnabled(false);
		}
		
		if (caseColormatrix.isSelected() == false)
		{
			comboInColormatrix.setEnabled(false);
			comboOutColormatrix.setEnabled(false);
		}
		
		components = grpSetAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		
		if (caseChangeAudioCodec.isSelected() == false)
		{
			comboAudioCodec.setEnabled(false);					
			comboAudioBitrate.setEnabled(false);
		}
		if (caseAudioOffset.isSelected() == false)
			txtAudioOffset.setEnabled(false);
		
		components = grpAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = grpInAndOut.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = grpSetTimecode.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseSetTimecode.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}
		
		if (caseGenerateFromDate.isSelected())
		{
			caseSetTimecode.setSelected(false);
			caseSetTimecode.setEnabled(false);
		}
			
		if (caseSetTimecode.isSelected() == false)
			caseIncrementTimecode.setEnabled(false);
		else
			caseGenerateFromDate.setEnabled(false);
			
		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseAddOverlay.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}
		components = grpCorrections.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = grpTransitions.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		
		if (caseVideoFadeIn.isSelected() == false)
			spinnerVideoFadeIn.setEnabled(false);
		if (caseVideoFadeOut.isSelected() == false)
			spinnerVideoFadeOut.setEnabled(false);
		if (caseAudioFadeIn.isSelected() == false)
			spinnerAudioFadeIn.setEnabled(false);
		if (caseAudioFadeOut.isSelected() == false)
			spinnerAudioFadeOut.setEnabled(false);
		
		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = grpH264.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		if (caseAS10.isSelected() == false)
			comboAS10.setEnabled(false);
		
		if (caseGOP.isSelected() == false)
			gopSize.setEnabled(false);
		
		if ((caseAccel.isSelected() || lblVBR.getText().equals("CQ")))
		{
			if (lblVBR.getText().equals("CQ") == false)
				caseForceOutput.setEnabled(false);
			
			case2pass.setEnabled(false);
		}
		
		 if (caseAccel.isSelected() && (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox")))
		 {
			caseForcePreset.setSelected(false);
			caseForcePreset.setEnabled(false);
			comboForcePreset.setEnabled(false);
		 }
		
		if (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov"))
			caseFastStart.setEnabled(true);
		else
			caseFastStart.setEnabled(false);	
		
		if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("12bits") || comboColorspace.getSelectedItem().toString().contains("422")))
		{
			caseAlpha.setSelected(false);
			caseAlpha.setEnabled(false);			
		}
		else
			caseAlpha.setEnabled(true);
		
		if (caseQMax.isSelected())
		{
			caseForcePreset.setEnabled(false);
			comboForcePreset.setEnabled(false);
			caseForceSpeed.setEnabled(false);
			comboForceSpeed.setEnabled(false);			
			caseForceQuality.setEnabled(false);
			comboForceQuality.setEnabled(false);	
		}

			
		// Dans tous les cas
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setSelected(false);
		tempsRestant.setVisible(false);
		comboFonctions.setEnabled(true);
		comboFilter.setEnabled(true);
		btnReset.setEnabled(true);
		btnStart.setEnabled(true);
		btnCancel.setEnabled(false);
		changeFunction(false);

		Utils.textFieldBackground();
		
		// Important
		topPanel.repaint();
		statusBar.repaint();
		
		if (inputDeviceIsRunning)
			progressBar1.setIndeterminate(false);
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public static void enfOfFunction() {		
		//Affichage des erreurs
		String[] FFPROBESplit = Console.consoleFFPROBE.getText().split(System.lineSeparator());
		String[] FFMPEGSplit = Console.consoleFFMPEG.getText().split(System.lineSeparator());
		
		if (errorList.length() != 0)
		{
			if (Settings.btnDisableSound.isSelected() == false) {
				try {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundErrorURL);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				}
			}
			
			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported()) 
			{ 
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.ERROR);
			} 	
			
			JTextArea errorText = new JTextArea(errorList.toString() + '\n' +
					Shutter.language.getProperty("ffprobe") + " " + FFPROBESplit[FFPROBESplit.length - 1] + '\n' +
					Shutter.language.getProperty("ffmpeg") + " " + FFMPEGSplit[FFMPEGSplit.length - 1]);  
			errorText.setWrapStyleWord(true);
			
			JScrollPane scrollPane = new JScrollPane(errorText);  
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false); 
			scrollPane.setPreferredSize( new Dimension( 500, 400 ) );

			Object[] moreInfo = {"OK", language.getProperty("menuItemConsole")};
	        
			int result =  JOptionPane.showOptionDialog(Shutter.frame, scrollPane, Shutter.language.getProperty("notProcessedFiles"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, moreInfo, null);
			
			if (result == JOptionPane.NO_OPTION)
			{
				if (Console.frmConsole != null) {
					if (Console.frmConsole.isVisible())
						Console.frmConsole.toFront();
					else
						new Console();
				} else
					new Console();
			}
			
			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
			{
				String s[] = errorList.toString().split(System.lineSeparator());
				
				int startRowCount = 0;
				int removedRows = 0;
				
				for (String line : s)
				{
					if (line.contains(Shutter.language.getProperty("file") + " N°"))
					{
						String item[] = line.split("-");
						String clearItemNumber = item[0].replace(Shutter.language.getProperty("file") + " N°", "");
						Integer getItemNumber = (int) (Integer.parseInt(clearItemNumber.replace(" ", "")) - removedRows - 1);

						for (int r = startRowCount ; r < RenderQueue.tableRow.getRowCount() ; r++)
						{
							if (r == getItemNumber)
							{
								startRowCount = r + 1;									
								break;
							}
							else
							{
								RenderQueue.tableRow.removeRow(r);
								getItemNumber --;
								r --;
								removedRows ++;
							}
						}
					}
				}	
				
				//Une fois terminé on supprime les fichiers au delà du dernier fichier qui contient l'erreur					
				for (int r = startRowCount ; r < RenderQueue.tableRow.getRowCount() ; r++)
				{
					RenderQueue.tableRow.removeRow(r);
					r --;
				}
			}
			
			errorList.setLength(0);
		}
		else if (RenderQueue.frame != null && RenderQueue.frame.isVisible() && Shutter.cancelled == false)
		{
			RenderQueue.tableRow.setRowCount(0);
		}
						
		enableAll();
		Utils.yesToAll = false;
		Utils.noToAll = false;

		if (cancelled == true) {
			lblCurrentEncoding.setForeground(Color.RED);
			lblCurrentEncoding.setText(language.getProperty("processCancelled"));
			progressBar1.setValue(0);
		} else {
			lblCurrentEncoding.setText(language.getProperty("processEnded"));
			if (progressBar1.getMaximum() == 0)
				progressBar1.setMaximum(1);
			progressBar1.setValue(progressBar1.getMaximum());

			if (Settings.btnDisableSound.isSelected() == false) {
				try {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				}
			}
		}
		
		if (grpDestination.isEnabled())
			grpDestination.setSelectedIndex(0);
		
		Wetransfer.sendToWeTransfer();
		lastActions();
	}

	public static void lastActions() {
		
		if (Settings.btnEmptyListAtEnd.isSelected() && cancelled == false && FFMPEG.error == false)
			liste.clear();
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				
				do
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
				while (Wetransfer.isRunning || Ftp.isRunning || sendMailIsRunning);
		
				if (Settings.btnEndingAction.isSelected() && cancelled == false)
				{	
					final JOptionPane msg = new JOptionPane(language.getProperty("shutdown"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				    final JDialog dlg = msg.createDialog(frame, Settings.comboAction.getSelectedItem().toString());

				    msg.setInitialSelectionValue(JOptionPane.CANCEL_OPTION);
				    dlg.setAlwaysOnTop(true);
				    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				    dlg.addComponentListener(new ComponentAdapter() {
				        @Override
				        public void componentShown(ComponentEvent e) {
				            super.componentShown(e);
				            final Timer t = new Timer(60000, new ActionListener() {
				                @Override
				                public void actionPerformed(ActionEvent e) {
				                    dlg.setVisible(false);
				                }
				            });
				            t.start();
				        }
				    });
				    dlg.setVisible(true);

				    Object selectedvalue = msg.getValue();
				    if (selectedvalue.equals(JOptionPane.CANCEL_OPTION) == false) {				    	
						switch (Settings.comboAction.getSelectedIndex())
						{		
							
								case 0:
									System.exit(0);
									break;
								case 1:
								    if (System.getProperty("os.name").contains("Mac"))
								    {
								        try {
											Runtime.getRuntime().exec(new String[]{"osascript", "-e", "tell application \"System Events\" to shut down"});
										} catch (IOException e) {}
								    }
								    else if (System.getProperty("os.name").contains("Linux"))
								    {
									    try {
									    	Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "shutdown -h now"});
											} catch (IOException e) {}
								    }
								    else //Windows
								    {
									    try {
											Runtime.getRuntime().exec("shutdown.exe -s -t 0");
										} catch (IOException e) {}
								    }				
								    break;
						}
				    }
				}
				
				saveCode = false;
				cancelled = false;
			}
		});
		thread.start();
	}

}

// Editing file list
@SuppressWarnings({ "serial", "rawtypes" })
class FilesCellRenderer extends JLabel implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		setText(value.toString());
		
		setIcon(new FlatSVGIcon("contents/item.svg", 10, 10));

		setToolTipText(value.toString());
		
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		setForeground(Color.BLACK);
		
		if (isSelected) {
			setBackground(new Color(215, 215, 215));
			setBorder(new LineBorder(Utils.highlightColor));
			setOpaque(true);
		} else {
			setBorder(new LineBorder(new Color(204,204,204,0)));
			setOpaque(false);
		}
		return this;
	}

}

// Editing functions list
@SuppressWarnings("serial")
class ComboBoxRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value.toString().contains(":") || value.toString().equals(Shutter.language.getProperty("itemMyFunctions")))
		{			
			setFont(new Font(Shutter.montserratFont, Font.BOLD, 12));
		}
		else
		{		
			setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		}
		
		list.setFixedCellHeight(18);
						
		return this;
	}
}

// Drag & Drop file list
@SuppressWarnings("serial")
class ListeFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		
		for (int i = 0; i < arg1.length; i++)
		{
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.scanIsRunning == false && Shutter.inputDeviceIsRunning == false
			&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionWeb")) == false)
			{

				Shutter.fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 1));
				return true;
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
		
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.scanIsRunning == false
						&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false && Shutter.comboFonctions
								.getSelectedItem().equals(Shutter.language.getProperty("functionWeb")) == false) {

					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
						{
							if (file.isFile())
								file = new File(file.getParent());
								
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								Shutter.liste.addElement(file + "/");
							else
								Shutter.liste.addElement(file + "\\");
									
							Shutter.addToList.setVisible(false);
							Shutter.lblFiles.setText(Utils.filesNumber());
							
							if (file != null) {
								if (Shutter.caseChangeFolder1.isSelected()) {
									Shutter.scanIsRunning = true;
									Shutter.changeFilters();
								} 
								else
									JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);				
							}
														
							break;
						}
						else
						{
							if (file.isFile() && file.getName().contains("."))
							{
								int s = file.getCanonicalPath().toString().lastIndexOf('.');
								String ext = file.getCanonicalFile().toString().substring(s);
								
								if (ext.equals(".enc")) 
								{
									Utils.loadSettings(new File (file.getCanonicalPath().toString()));
								} 
								else if (ext.toLowerCase().equals(Shutter.comboFilter.getSelectedItem().toString())
										|| Shutter.comboFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun"))
										|| Shutter.lblFilter.getText().equals(Shutter.language.getProperty("lblFilter")) == false) 
								{
									if (file.isHidden() == false)
									{			
										
										if (file.getCanonicalPath().toString().contains("\"") || file.getCanonicalPath().toString().contains("\'") || file.getName().contains("/") || file.getName().contains("\\"))
										{
											Object[] options = { Shutter.language.getProperty("btnAdd"), Shutter.language.getProperty("btnNext"), Shutter.language.getProperty("btnCancel") };
											
											int q = JOptionPane.showOptionDialog(Shutter.frame, file.getCanonicalPath().toString() + System.lineSeparator() + Shutter.language.getProperty("invalidCharacter"), Shutter.language.getProperty("import"),
													JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
										
											if (q == 1) //Next
												continue;
											else if (q == 2) //Cancel
												break;
										}
																				
										if (Settings.btnExclude.isSelected())
										{		
											boolean allowed = true;
											for (String excludeExt : Settings.txtExclude.getText().split("\\*"))
											{
												if (excludeExt.contains(".") && ext.toLowerCase().equals(excludeExt.replace(",", "").toLowerCase()))
													allowed = false;
											}
											
											if (allowed)
											{
												Shutter.liste.addElement(file.getCanonicalPath().toString());	
												Shutter.addToList.setVisible(false);
												Shutter.lblFiles.setText(Utils.filesNumber());
											}
										}
										else
										{
											Shutter.liste.addElement(file.getCanonicalPath().toString());
											Shutter.addToList.setVisible(false);
											Shutter.lblFiles.setText(Utils.filesNumber());
										}
									}
								}
							}
							else
							{
								Utils.findFiles(file.getCanonicalPath().toString());
							}
						}
					}

					switch (Shutter.comboFonctions.getSelectedItem().toString()) {
					case "H.264":
					case "H.265":
					case "WMV":
					case "MPEG":
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

					// CaseOPATOM
					switch (Shutter.comboFonctions.getSelectedItem().toString()) {
					case "DNxHD":
					case "DNxHR":
					case "Apple ProRes":
					case "GoPro CineForm":
					case "QT Animation":
					case "Uncompressed YUV":
						if (Shutter.caseOPATOM.isSelected()) {
							for (int item = 0; item < Shutter.liste.getSize(); item++) {
								int s = Shutter.liste.getElementAt(item).toString().lastIndexOf('.');
								if (Shutter.liste.getElementAt(item).toString().substring(s).toLowerCase()
										.equals(".mxf") == false) {
									Shutter.liste.remove(item);
									item = -1;
								}
							}
							Shutter.lblFiles.setText(Utils.filesNumber());
						}
						break;
					}

					// VideoPlayer
					VideoPlayer.setMedia();
					
					// Filtre
					Shutter.changeFilters();

					// Border
					Shutter.fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

// Drag & Drop lblDestination
@SuppressWarnings("serial")
class DestinationFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent comp, DataFlavor[] arg1) {
		
		for (int i = 0; i < arg1.length; i++)
		{
			DataFlavor flavor = arg1[i];
			
			if (flavor.equals(DataFlavor.javaFileListFlavor))
			{
				if (comp.getName().equals("lblDestination3") && Shutter.caseChangeFolder2.isSelected() == false)
				{
					return false;
				}
				else
				{
					comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
					return true;
				}
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
				
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++)
		{
			DataFlavor flavor = flavors[i];
			
			try {
				
				if (flavor.equals(DataFlavor.javaFileListFlavor))
				{
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					
					while (iter.hasNext())
					{						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getName().contains("."))
						{
							((JTextField) comp).setText(file.getParent());
						} 
						else 
							((JTextField) comp).setText(file.getAbsolutePath());

						if (comp.getName().equals("lblDestination1"))
						{							
							//Si destination identique à l'une des autres
							if (Shutter.lblDestination1.getText().equals(Shutter.lblDestination2.getText()) || Shutter.lblDestination1.getText().equals(Shutter.lblDestination3.getText())) 
							{
								JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
								Shutter.caseChangeFolder1.setSelected(false);
							}
							else
							{							
								Shutter.caseChangeFolder1.setSelected(true);
								Shutter.caseOpenFolderAtEnd1.setSelected(false);
							}						
							
							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
							{
								// Si le dossier d'entrée et de sortie est identique
								if (Shutter.liste.firstElement().substring(0, Shutter.liste.firstElement().length() - 1).equals(Shutter.lblDestination1.getText()))
								{
									JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"),Shutter.language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
									Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
									Shutter.caseChangeFolder1.setSelected(false);
								}
								else
								{
									Shutter.scanIsRunning = true;
								}
							}
							
							if (Shutter.lblDestination1.getText() != Shutter.language.getProperty("sameAsSource") && Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(Shutter.lblDestination1.getText());
						}
						else if (comp.getName().equals("lblDestination2"))
						{
							//Si destination identique à l'une des autres
							if (Shutter.lblDestination2.getText().equals(Shutter.lblDestination1.getText()) || Shutter.lblDestination2.getText().equals(Shutter.lblDestination3.getText())) 
							{
								JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination2.setText(Shutter.language.getProperty("aucune"));
								Shutter.caseChangeFolder2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setEnabled(false);
							}
							else
							{							
								Shutter.caseChangeFolder2.setSelected(true);
								Shutter.caseOpenFolderAtEnd2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setEnabled(true);
								Shutter.caseChangeFolder3.setEnabled(true);
							}							
							
							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
								Shutter.scanIsRunning = true;
							
							if (Shutter.lblDestination2.getText() != Shutter.language.getProperty("sameAsSource") && Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(Shutter.lblDestination2.getText());
						}
						else if (comp.getName().equals("lblDestination3"))
						{
							//Si destination identique à l'une des autres
							if (Shutter.lblDestination3.getText().equals(Shutter.lblDestination1.getText()) || Shutter.lblDestination3.getText().equals(Shutter.lblDestination2.getText())) 
							{
								JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination3.setText(Shutter.language.getProperty("aucune"));
								Shutter.caseChangeFolder3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setEnabled(false);
							}
							else
							{							
								Shutter.caseChangeFolder3.setSelected(true);
								Shutter.caseOpenFolderAtEnd3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setEnabled(true);
							}	
							
							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
								Shutter.scanIsRunning = true;
							
							if (Shutter.lblDestination3.getText() != Shutter.language.getProperty("sameAsSource") && Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(Shutter.lblDestination3.getText());
						}
					}

					// Border
					comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}
	