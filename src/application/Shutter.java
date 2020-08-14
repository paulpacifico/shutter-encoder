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

package application;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
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
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.style.StyleId;
import com.alee.managers.style.XmlSkin;
import com.apple.eawt.Application;

import functions.audio.AAC;
import functions.audio.AC3;
import functions.audio.AIFF;
import functions.audio.AudioNormalization;
import functions.audio.FLAC;
import functions.audio.LoudnessTruePeak;
import functions.audio.MP3;
import functions.audio.OGG;
import functions.audio.OPUS;
import functions.audio.ReplaceAudio;
import functions.audio.WAV;
import functions.other.BlackDetection;
import functions.other.Command;
import functions.other.Conform;
import functions.other.EndToEnd;
import functions.other.Extract;
import functions.other.LosslessCut;
import functions.other.OfflineDetection;
import functions.other.Rewrap;
import functions.other.VideoInserts;
import functions.other.VideoLevels;
import functions.video.AV1;
import functions.video.AVC;
import functions.video.AppleProRes;
import functions.video.Bluray;
import functions.video.CineForm;
import functions.video.DNxHD;
import functions.video.DNxHR;
import functions.video.DVD;
import functions.video.DVDRIP;
import functions.video.DVPAL;
import functions.video.FFV1;
import functions.video.H264;
import functions.video.H265;
import functions.video.HAP;
import functions.video.MJPEG;
import functions.video.MPEG;
import functions.video.OGV;
import functions.video.Picture;
import functions.video.QTAnimation;
import functions.video.UncompressedYUV;
import functions.video.VP9;
import functions.video.WMV;
import functions.video.XAVC;
import functions.video.XDCAM;
import functions.video.Xvid;
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

@SuppressWarnings("serial")
public class Shutter {

	/*
	 * Initialisation
	 */
	public static String actualVersion = "14.1";
	public static String getLanguage = "";
	public static String pathToFont = "JRE/lib/fonts/Montserrat.ttf";
	public static File documents = new File(System.getProperty("user.home") + "/Documents/Shutter Encoder");
	public static String dirTemp = System.getProperty("java.io.tmpdir");
	public static File lutsFolder;
	public static File subtitlesFile;
	public static Properties language = new Properties();
	public static URL soundURL;
	public static URL soundErrorURL;
	public static JFrame frame = new JFrame();
	public static JDialog shadow = new JDialog();
	public static boolean cancelled = false;
	public static float ratioFinal = 0; // Fenêtre CropVideo
	public static String cropFinal = null; // Fenêtre CropImage
	public static String finalEQ = null; // Fenêtre ColorImage
	public static boolean scanIsRunning = false;
	public static boolean sendMailIsRunning = false;
	protected static boolean canScroll = true;
	public static JMenuItem scan;
	static ArrayList<String> droppedFiles = new ArrayList<String>(); // Drop file application
	public static boolean saveCode = false;
	protected static boolean copyFileIsRunning = false;
	protected static boolean subtitlesBurn = true;
	public static StringBuilder errorList = new StringBuilder();

	/*
	 * Animations
	 */
	// private static boolean changeSizeIsRunning = false;
	private static boolean changeGroupes = false;

	/*
	 * Position fenêtre MiniWindow
	 */
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static Rectangle bounds = ge.getMaximumWindowBounds();
	public static int MiniWindowY = bounds.height / 2;

	/*
	 * Composants
	 */
	private static JLabel settings;
	private static JLabel quit;
	private static JLabel reduce;
	private static JLabel help;
	private static JLabel newInstance;

	public static DefaultListModel<String> liste = new DefaultListModel<String>();
	protected static PopupMenu lblDposezVosFichiers;
	public static JList<String> listeDeFichiers;
	static JLabel addToList = new JLabel();
	public static JComboBox<String[]> comboFonctions;

	protected static JButton btnParcourir;
	protected static JButton btnVider;
	protected static JComboBox<Object> comboFilter;
	protected static JComboBox<Object> comboLUTs;
	protected static JComboBox<Object> comboInColormatrix;
	protected static JComboBox<Object> comboOutColormatrix;
	protected static JComboBox<Object> comboColorspace;
	protected static JButton btnStart;
	protected static JButton btnAnnuler;
	protected static JRadioButton caseOpenFolderAtEnd1;
	protected static JRadioButton caseOpenFolderAtEnd2;
	protected static JRadioButton caseOpenFolderAtEnd3;
	protected static JRadioButton caseChangeFolder1;
	protected static JRadioButton caseChangeFolder2;
	protected static JRadioButton caseChangeFolder3;
	protected static JRadioButton caseRunInBackground;
	protected static JRadioButton caseVisualiser;
	protected static JLabel iconTVInterpret;
	protected static JLabel iconTVResolution;
	protected static JLabel iconList;
	protected static JLabel iconPresets;
	protected static JComboBox<String> comboResolution;
	protected static JRadioButton caseRognerImage;
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
	protected static JRadioButton caseActiverSequence;
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
	protected static JRadioButton caseColormatrix;
	protected static JRadioButton caseColorspace;
	protected static JRadioButton caseColor;
	protected static JComboBox<String> caseSequenceFPS;
	protected static JRadioButton caseSetTimecode;
	protected static JRadioButton caseIncrementTimecode;
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
	protected static JRadioButton caseForcerResolution;
	protected static JRadioButton caseForcerDAR;
	protected static JRadioButton caseLimiter;
	protected static JRadioButton caseConform;
	protected static JRadioButton caseCreateTree;
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
	protected static JPanel panelHaut;
	protected static JLabel lblV;
	protected static JLabel topImage;
	protected static WebScrollPane scrollBar;
	protected static JLabel lblTermine;
	protected static JLabel lblFichiers;
	protected static JLabel lblFilter;
	protected static WebTextField lblDestination1;
	protected static WebTextField lblDestination2;
	protected static WebTextField lblDestination3;
	protected static JProgressBar progressBar1;
	public static JLabel lblEncodageEnCours;
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
	protected static JLabel lblPadLeft;
	protected static JLabel lblPadRight;
	protected static JComboBox<String> lblResolution;
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

	/*
	 * Groupes Boxes
	 */
	protected static JPanel grpChoixDesFichiers;
	protected static JPanel grpChoixFonction;
	protected static WebTabbedPane grpDestination;
	protected static JPanel destination1;
	protected static JPanel destination2;
	protected static JPanel destination3;
	protected static JPanel destinationMail;
	protected static JPanel grpProgression;
	protected static JPanel grpResolution;
	protected static JPanel grpSequenceImage;
	protected static JPanel grpFiltreImage;
	protected static JPanel grpLUTs;
	protected static JPanel grpInAndOut;
	protected static JPanel grpSetTimecode;
	protected static JPanel grpOverlay;
	protected static JPanel grpSetAudio;
	protected static JPanel grpAudio;
	protected static JPanel grpCorrections;
	protected static JPanel grpAdvanced;
	protected static JPanel grpH264;
	protected static JPanel grpTransitions;
	protected static JLabel lblFadeInColor;
	protected static JRadioButton caseVideoFadeIn;
	protected static JSpinner spinnerVideoFadeIn;
	protected static JRadioButton caseAudioFadeIn;
	protected static JSpinner spinnerAudioFadeIn;
	protected static JLabel lblFadeOutColor;
	protected static JRadioButton caseVideoFadeOut;
	protected static JSpinner spinnerVideoFadeOut;
	protected static JRadioButton caseAudioFadeOut;
	protected static JSpinner spinnerAudioFadeOut;
	protected static JTextField textH;
	protected static JTextField textMin;
	protected static JTextField textSec;
	protected static JComboBox<String> comboH264Taille;
	protected static JComboBox<String> debitVideo;
	protected static JComboBox<String> debitAudio;
	protected static JPanel h264lines;
	protected static JTextField taille;
	protected static JLabel lock;
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
	protected JPopupMenu popupListe;
	protected static JPopupMenu scanListe;
	protected JPopupMenu popupProgression;
	protected JPopupMenu popupDestination;
	protected static JTextField textMail;
	protected static JRadioButton caseSendMail;

	public static void main(String[] args) {
				
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
		
		/*
		// Copie de la police de Shutter
		if (System.getProperty("os.name").contains("Linux")) 
		{
			try {
				String folderFont = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				folderFont = folderFont.substring(0, folderFont.length() - 1);
				folderFont = folderFont.substring(0, (int) (folderFont.lastIndexOf("/"))).replace("%20", "\\ ") + "/fonts";
				
				File font = new File("/home/" + System.getProperty("user.name") + "/.local/share/fonts/Magneto.ttf");
				if (font.exists() == false)
						FileUtils.copyFile(new File(folderFont + "/Magneto.ttf"), font);
				
				font = new File("/home/" + System.getProperty("user.name") + "/.local/share/fonts/Montserrat.ttf");
				if (font.exists() == false)
						FileUtils.copyFile(new File(folderFont + "/Montserrat.ttf"), font);
				
				font = new File("/home/" + System.getProperty("user.name") + "/.local/share/fonts/Arial.ttf");
				if (font.exists() == false)
						FileUtils.copyFile(new File(folderFont + "/Arial.ttf"), font);
				
				Thread.sleep(2000);
					
			} catch (Exception e1) {}
		}*/

		new Splash();
		Splash.g = Splash.splash.createGraphics();
		Splash.renderSplashFrame(Splash.g, 0);
		Splash.splash.update();

		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				droppedFiles.add(i, args[i]);
			}
		}

		/*
		WebButtonStyle.drawShade = false;
		//WebTextFieldStyle.drawShade = false;
		//WebTextFieldStyle.shadeWidth = 0;
		WebPasswordFieldStyle.drawShade = false;
		WebPasswordFieldStyle.shadeWidth = 0;
		WebRadioButtonStyle.shadeWidth = 0;
		WebRadioButtonStyle.darkBorderColor = new Color(80, 80, 80);
		WebRadioButtonStyle.topSelectedBgColor = new Color(71, 163, 236);
		WebRadioButtonStyle.bottomSelectedBgColor = new Color(71, 163, 236);
		WebRadioButtonStyle.topBgColor = new Color(35, 35, 35);
		WebRadioButtonStyle.bottomBgColor = new Color(45, 45, 45);
		WebRadioButtonStyle.disabledBorderColor = new Color(40, 40, 40);
		WebComboBoxStyle.shadeWidth = 0;
		WebComboBoxStyle.drawBorder = false;
		WebSpinnerStyle.shadeWidth = 0;
		WebTabbedPaneStyle.shadeWidth = 0;
		WebTabbedPaneStyle.selectedTopBg = new Color(50, 50, 50);
		WebTabbedPaneStyle.selectedBottomBg = new Color(50, 50, 50); 
		WebTabbedPaneStyle.tabBorderColor = new Color(50, 50, 50); 
		WebTabbedPaneStyle.topBg = new Color(40, 40, 40); 
		WebTabbedPaneStyle.bottomBg = new Color(40, 40, 40);
		WebTabbedPaneStyle.contentBorderColor = new Color(50, 50, 50);
		WebTabbedPaneStyle.backgroundColor = new Color(80, 80, 80);
		WebTabbedPaneStyle.shadeWidth = 0;
		WebPanelStyle.shadeWidth = 0;
		WebScrollPaneStyle.shadeWidth = 0;
		WebSliderStyle.trackShadeWidth = 0;
		//WebTextFieldStyle.shadeWidth = 0;
		 
		WebProgressBarStyle.shadeWidth = 0;
		WebProgressBarStyle.innerRound = 4;
		WebProgressBarStyle.progressEnabledBorderColor = new Color(80, 80, 80);
		WebProgressBarStyle.progressTopColor = new Color(129, 198, 253);
		WebProgressBarStyle.progressBottomColor = new Color(71, 163, 236);
		WebProgressBarStyle.bgTop = new Color(35, 35, 35);
		WebProgressBarStyle.bgBottom = new Color(45, 45, 45);
		
		WebSliderStyle.progressShadeWidth = 0;
		WebSliderStyle.thumbShadeWidth = 0;
		WebSliderStyle.trackShadeWidth = 0;
		WebSliderStyle.progressRound = 4;
		WebSliderStyle.progressTrackBgTop = new Color(129, 198, 253);
		WebSliderStyle.progressTrackBgBottom = new Color(71, 163, 236);
		WebSliderStyle.thumbBgTop = new Color(80, 80, 80);
		WebSliderStyle.thumbBgBottom = new Color(80, 80, 80);
		WebSliderStyle.trackBgTop = new Color(35, 35, 35);
		WebSliderStyle.trackBgBottom = new Color(45, 45, 45);
		WebTooltipStyle.backgroundColor = new Color(0, 0, 0);
		WebTooltipStyle.textColor = Color.WHITE;
		WebTooltipStyle.trasparency = 0.8f;*/
				
		// Langue
		InputStream input;
		try {
			String pathToLanguages = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (System.getProperty("os.name").contains("Windows"))
				pathToLanguages = pathToLanguages.substring(1, pathToLanguages.length() - 1);
			else
				pathToLanguages = pathToLanguages.substring(0, pathToLanguages.length() - 1);

			pathToLanguages = pathToLanguages.substring(0, (int) (pathToLanguages.lastIndexOf("/"))).replace("%20", " ")
					+ "/Languages/";

			// Library/Preferences sur Mac
			try {
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					if (new File(System.getProperty("user.home") + "/Library/Preferences/Shutter Encoder").exists())
						Shutter.documents = new File(System.getProperty("user.home") + "/Library/Preferences/Shutter Encoder");
					else if (new File("/Library/Preferences/Shutter Encoder").exists())
						Shutter.documents = new File("/Library/Preferences/Shutter Encoder");
				}
			} catch (Exception e) {}
			
			// Dossier Temporaire Linux
			if (System.getProperty("os.name").contains("Linux"))
			{
				dirTemp += "/";
			}
			
			if (new File(Shutter.documents + "/settings.xml").exists())
			{				
				try {
					File fXmlFile = new File(Shutter.documents + "/settings.xml");
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
				
					NodeList nList = doc.getElementsByTagName("Component");
					
					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;

							if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("comboLanguage"))
								getLanguage = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent();							
						}
					}	

					if (getLanguage != null && getLanguage != "")
					{
						if (getLanguage.equals("Français"))
							input = new FileInputStream(pathToLanguages + "fr.properties");
						else if (getLanguage.equals("Italiano"))
							input = new FileInputStream(pathToLanguages + "it.properties");
						else
							input = new FileInputStream(pathToLanguages + "en.properties");
					}
					else
					{
						if (System.getProperty("user.language").equals("fr"))
						{
							getLanguage = "Français";
							input = new FileInputStream(pathToLanguages + "fr.properties");
						}
						else if (System.getProperty("user.language").equals("it"))
						{
							getLanguage = "Italiano";
							input = new FileInputStream(pathToLanguages + "it.properties");
						}
						else
						{
							getLanguage = "English";
							input = new FileInputStream(pathToLanguages + "en.properties");
						}
					}
					
				} catch (Exception e) {
					if (System.getProperty("user.language").equals("fr"))
					{
						getLanguage = "Français";
						input = new FileInputStream(pathToLanguages + "fr.properties");
					}
					else if (System.getProperty("user.language").equals("it"))
					{
						getLanguage = "Italiano";
						input = new FileInputStream(pathToLanguages + "it.properties");
					}
					else
					{
						getLanguage = "English";
						input = new FileInputStream(pathToLanguages + "en.properties");
					}
				}
			}
			else
			{
				if (System.getProperty("user.language").equals("fr"))
				{
					getLanguage = "Français";
					input = new FileInputStream(pathToLanguages + "fr.properties");
				}
				else if (System.getProperty("user.language").equals("it"))
				{
					getLanguage = "Italiano";
					input = new FileInputStream(pathToLanguages + "it.properties");
				}
				else
				{
					getLanguage = "English";
					input = new FileInputStream(pathToLanguages + "en.properties");
				}
			}
			language.load(input);	
			input.close();
			
		} catch (IOException ex) {}
		
		//Theme
		if (new File(Shutter.documents + "/settings.xml").exists())
		{				
			try {
				File fXmlFile = new File(Shutter.documents + "/settings.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
				
				String getTheme = null;
				
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("comboTheme"))
							getTheme = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent();							
					}
				}	
								
				if (getTheme != null && getTheme != "")
				{
					if (getTheme.equals(Shutter.language.getProperty("darkTheme")))
						WebLookAndFeel.install(DarkSkin.class);
					else
						WebLookAndFeel.install(ClearSkin.class);
				}
				else
					WebLookAndFeel.install(ClearSkin.class);
				
			} catch (Exception e) {
				WebLookAndFeel.install(ClearSkin.class);
			}
		}
		else
		{
			WebLookAndFeel.install(ClearSkin.class);
		}

		Splash.increment();

		// Documents Shutter Encoder
		if (Shutter.documents.exists() == false)
			Shutter.documents.mkdirs();

		new Shutter();
	}

	public Shutter() {
			
		frame.getContentPane().setBackground(new Color(50, 50, 50));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Shutter Encoder");
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(332, 669);
		// frame.setSize(980, 669);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100, 100, 100)));
		frame.setBackground(new Color(100, 100, 100));
		frame.getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);

		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
				Settings.saveSettings();
            }
        });
		
		/*/Changement son de fin			
		try {
			for (File wav : new File (documents.toString()).listFiles())
			{			
				if (wav.toString().contains(".") && wav.toString().substring(wav.toString().lastIndexOf(".")).equals(".wav"))
				{					
					if (System.getProperty("os.name").contains("Windows"))
						soundURL = new URL("file:/" + wav.toString().replace(" ", "%20"));	
					else
						soundURL = new URL("file://" + wav.toString().replace("\\ ", "%20"));
					
					break;
				}
				else
					soundURL = this.getClass().getClassLoader().getResource("contents/complete.wav");
			}
		} catch (MalformedURLException e1) {
			soundURL = this.getClass().getClassLoader().getResource("contents/complete.wav");
		}
		finally {
			soundErrorURL = this.getClass().getClassLoader().getResource("contents/error.wav");
		}*/
		
		soundURL = this.getClass().getClassLoader().getResource("contents/complete.wav");
		soundErrorURL = this.getClass().getClassLoader().getResource("contents/error.wav");
		
		// Seulement pour mac
		if (System.getProperty("os.name").contains("Mac"))
			Application.getApplication().setDockIconImage(
					new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());

		frame.getRootPane().setDefaultButton(btnStart);

		frame.addWindowStateListener(new WindowStateListener() {

			@SuppressWarnings("static-access")
			@Override
			public void windowStateChanged(WindowEvent arg0) {
				if (frame.getState() == frame.NORMAL && ReducedWindow.frame != null) {
					ReducedWindow.frame.setVisible(false);
					shadow.setVisible(true);
					frame.toFront();
				} else if (frame.getState() == frame.ICONIFIED)
					shadow.setVisible(false);
			}

		});
		
		//Settings
		Settings.txtThreads.setText("0");
		Settings.txtImageDuration.setText("10");
		Settings.txtBlackDetection.setText("10");
		
		Splash.increment();
		panelHaut();
		Splash.increment();
		StatusBar();
		Splash.increment();
		grpChoixDesFichiers();
		Splash.increment();
		grpChoixFonction();
		Splash.increment();
		grpDestination();
		Splash.increment();
		grpProgression();
		Splash.increment();
		grpResolution();
		Splash.increment();
		grpSequenceImage();
		Splash.increment();
		grpFiltreImage();
		Splash.increment();
		grpLUTs();
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
					changementDeFonction(true);
					changementDeFiltre();
				}
			}
		});
		Splash.increment();

		setShadow();
		Utils.changeFrameVisibility(frame, shadow, false);
		panelHaut.requestFocus();
		
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
				listeDeFichiers.setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));
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
						if ((ke.getKeyCode() == KeyEvent.VK_S) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
						|| (ke.getKeyCode() == KeyEvent.VK_S)
						&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
						{
							if (btnStart.getText().equals(Shutter.language.getProperty("btnStartFunction")) && comboFonctions.getSelectedItem() != "") 
							{
								if (Renamer.frame == null || Renamer.frame != null && Renamer.frame.isVisible() == false) {
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
				
				if (canScroll && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
						&& frame.getWidth() > 332 && mp.getX() > 332
						&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31) 
					{								
						MouseWheelEvent me = (MouseWheelEvent) event;
						int i =  (0 - me.getWheelRotation()) * 20;
						
						//Pré calcul
						if (top.getY() + i >= grpChoixDesFichiers.getY() && i > 0)
						{
							if (i < grpChoixDesFichiers.getY())
								i = grpChoixDesFichiers.getY() - top.getY();	
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
						grpSequenceImage.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getLocation().y + i);
						grpFiltreImage.setLocation(grpFiltreImage.getLocation().x, grpFiltreImage.getLocation().y + i);
						grpLUTs.setLocation(grpLUTs.getLocation().x, grpLUTs.getLocation().y + i);
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
					
					if (ext.equals(".enc")) {
						Utils.loadSettings(new File(droppedFiles.toString()));
					} else if (droppedFiles.isHidden() == false && droppedFiles.getName().contains("."))
						liste.addElement(droppedFiles.toString());
				} else
					Utils.FileFinder(droppedFiles.toString());
			}
			
			lblFichiers.setText(Utils.nombreDeFichiers());
		}
		
		new Settings();
		if (Settings.btnDisableUpdate.isSelected() == false)
			Update.newVersion();
		YOUTUBEDL.update();
		EXIFTOOL.run(""); //Permet de prélancer l'exécutable
	}
	
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}

	private void panelHaut() {

		panelHaut = new JPanel();
		panelHaut.setLayout(null);

		lblV = new JLabel();
		lblV.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblV.setBounds(289, 31, 64, 16);
		lblV.setText("v" + actualVersion);
		panelHaut.add(lblV);

		lblV.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/changelog.txt"));
				} catch (IOException | URISyntaxException e) {
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblV.setFont(new Font("FreeSans", Font.BOLD, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblV.setFont(new Font("FreeSans", Font.PLAIN, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		settings = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/settings.png")));
		settings.setHorizontalAlignment(SwingConstants.CENTER);
		settings.setBounds(0, 0, 21, 21);
		panelHaut.add(settings);
		
		settings.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				settings.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/settings3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept) {
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}
					Settings.frame.setVisible(true);
					frame.setOpacity(1.0f);					
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				settings.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/settings2.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				settings.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/settings.png"))));
				accept = false;
			}

		});

		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24, 0, 21, 21);
		panelHaut.add(quit);
		panelHaut.setBounds(0, 0, 1000, 53);

		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit3.png"))));
				if (FFMPEG.isRunning)
					btnAnnuler.doClick();
				else
					accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept) {					
					Settings.saveSettings();
					
					Utils.changeFrameVisibility(frame, shadow, true);

					try {
						if (btnStart.getText().equals(Shutter.language.getProperty("btnResumeFunction")))
							FFMPEG.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le
													// détruire
						FFMPEG.process.destroy();
						
						if (FFPROBE.isRunning)
							FFPROBE.process.destroy();
						
						if (FFPLAY.isRunning)
							FFPLAY.process.destroy();

						if (DECKLINK.isRunning)
							DECKLINK.process.destroy();

						if (BMXTRANSWRAP.isRunning)
							BMXTRANSWRAP.process.destroy();

						if (DCRAW.isRunning)
							DCRAW.process.destroy();

						if (XPDF.isRunning)
							XPDF.process.destroy();
						
						if (MKVMERGE.isRunning)
							MKVMERGE.process.destroy();
						
						if (DVDAUTHOR.isRunning)
							DVDAUTHOR.process.destroy();
						
						if (TSMUXER.isRunning)
							TSMUXER.process.destroy();
						
						if (XPDF.isRunning)
							XPDF.process.destroy();
						
						if (YOUTUBEDL.isRunning)
							YOUTUBEDL.process.destroy();
					} catch (Exception er) {
					}

					if (SceneDetection.sortieDossier != null && SceneDetection.sortieDossier.exists())
						SceneDetection.deleteDirectory(SceneDetection.sortieDossier);

					// Suppression des SRT temporaires
					String rootPath = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();					
					if (System.getProperty("os.name").contains("Windows"))
					{
						rootPath = rootPath.substring(1,rootPath.length()-1);
						rootPath = rootPath.substring(0,(int) (rootPath.lastIndexOf("/"))).replace("%20", " ");
					}
					else if (System.getProperty("os.name").contains("Mac"))
					{
						rootPath = rootPath.substring(0,rootPath.length()-1);
						rootPath = rootPath.substring(0,(int) (rootPath.lastIndexOf("/"))).replace("%20", " ");
					}
					else //Linux
					{
						rootPath = dirTemp;
					}
					
					for (File subs : new File(rootPath).listFiles())
					{
						if (subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("srt"))
							subs.delete();
					}	
					
					//Suppression de vidstab
					File vidstab;
					if (System.getProperty("os.name").contains("Windows"))
						vidstab = new File("vidstab.trf");
					else							    		
						vidstab = new File(Shutter.dirTemp + "vidstab.trf");
					
					if (vidstab.exists())
						vidstab.delete();
					
					//Suppression du media offline
					File file = new File(dirTemp + "offline.png");
					if (file.exists())
						file.delete();
					
					//Stats_file
					File stats_file = new File(Shutter.dirTemp + "stats_file");					
					if (System.getProperty("os.name").contains("Windows"))
						stats_file = new File("stats_file");					
					if (stats_file.exists())
						stats_file.delete();
					
					System.exit(0);
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

		reduce = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/reduce2.png")));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 21, 0, 21, 21);
		panelHaut.add(reduce);
		panelHaut.setBounds(0, 0, 852, 53);

		reduce.addMouseListener(new MouseListener() {

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
				if (accept) {
					new ReducedWindow();
					shadow.setVisible(false);
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

		help = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/help2.png")));
		help.setHorizontalAlignment(SwingConstants.CENTER);
		help.setBounds(reduce.getLocation().x - 21, 0, 21, 21);
		panelHaut.add(help);
		panelHaut.setBounds(0, 0, 852, 53);

		help.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				help.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/help3.png"))));
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
				help.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/help.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				help.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/help2.png"))));
				accept = false;
			}

		});

		newInstance = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/new2.png")));
		newInstance.setHorizontalAlignment(SwingConstants.CENTER);
		newInstance.setBounds(help.getLocation().x - 21, 0, 21, 21);
		panelHaut.add(newInstance);
		panelHaut.setBounds(0, 0, 852, 53);

		newInstance.addMouseListener(new MouseListener() {	

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				newInstance.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/new3.png"))));
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
				newInstance.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/new.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				newInstance.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/new2.png"))));
				accept = false;
			}

		});

		JLabel panelShutter = new JLabel(language.getProperty("panelShutter"));
		panelShutter.setFont(new Font("Magneto", Font.PLAIN, 26));
		panelShutter.setBounds((320 - panelShutter.getPreferredSize().width) / 2, 0, panelShutter.getPreferredSize().width + 5, 53);
		panelHaut.add(panelShutter);
		
		JLabel panelSettings = new JLabel(language.getProperty("panelSettings"));
		panelSettings.setFont(new Font("Magneto", Font.PLAIN, 26));
		panelSettings.setBounds(328 + ( (650 - 328) - panelSettings.getPreferredSize().width) / 2, 0, panelSettings.getPreferredSize().width + 5, 53);
		panelHaut.add(panelSettings);

		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(panelHaut.getSize().width,
				panelHaut.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);
		topImage.setBounds(0, 0, 1000 ,53);

		panelHaut.add(topImage);
		panelHaut.setBounds(0, 0, 1000, 53);
		frame.getContentPane().add(panelHaut);

		topImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;
				shadow.toFront();
				frame.toFront();
			}
		});

		topImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX,
						MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}

		});

	}

	@SuppressWarnings("unchecked")
	private void grpChoixDesFichiers() {
			
		listeDeFichiers = new JList<String>(liste) {
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
		listeDeFichiers.setForeground(Color.BLACK);
		listeDeFichiers.setCellRenderer(new FilesCellRenderer());
		listeDeFichiers.setFixedCellHeight(17);
		listeDeFichiers.setBounds(10, 50, 292, 255);
		listeDeFichiers.setToolTipText(language.getProperty("rightClick"));
				
		addToList.setText(language.getProperty("dropFilesHere"));
		addToList.setSize(listeDeFichiers.getSize());
		addToList.setForeground(new Color(150,150,150));
		addToList.setBackground(new Color(0,0,0,0));
		addToList.setFont(new Font("FreeSans", Font.PLAIN, 16));
		addToList.setHorizontalAlignment(SwingConstants.CENTER);
		addToList.setVerticalAlignment(SwingConstants.CENTER);
		listeDeFichiers.add(addToList);
				
		grpChoixDesFichiers = new JPanel();
		grpChoixDesFichiers.setLayout(null);
		grpChoixDesFichiers.setBounds(10, 59, 312, 315);
		grpChoixDesFichiers.setBackground(new Color(50, 50, 50));
		grpChoixDesFichiers.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpChoixDesFichiers") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpChoixDesFichiers);

		btnVider = new JButton(language.getProperty("btnVider"));
		btnVider.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnVider.setBounds(122, 19, 86, 25);
		grpChoixDesFichiers.add(btnVider);

		btnVider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Scan
				scan.setText(language.getProperty("menuItemStartScan"));
				scanIsRunning = false;

				liste.clear();
				addToList.setVisible(true);
				lblTermine.setVisible(false);

				// H264 Paramètres
				lblH264.setVisible(false);
				textH.setText("00");
				textMin.setText("00");
				textSec.setText("00");

				// Lecteur
				VideoPlayer.setMedia();

				changementDeFiltre();

				lblFichiers.setText(Utils.nombreDeFichiers());
			}

		});

		scrollBar = new WebScrollPane(StyleId.scrollpaneTransparent);
		scrollBar.getViewport().add(listeDeFichiers);
		scrollBar.setBounds(10, 50, 292, 255);
		scrollBar.setOpaque(false);
		scrollBar.getViewport().setOpaque(false);
		grpChoixDesFichiers.add(scrollBar);

		// Drag & Drop
		listeDeFichiers.setTransferHandler(new ListeFileTransferHandler());

		listeDeFichiers.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (liste.getSize() == 0)
					changementDeFiltre();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (scanIsRunning == false) {
					if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						listeDeFichiers.setSelectionInterval(0, liste.getSize() - 1);

					if (e.getKeyCode() == 127 && liste.getSize() > 0 || e.getKeyCode() == 8 && liste.getSize() > 0) {
						do {
							liste.remove(listeDeFichiers.getSelectedIndex());
						} while (listeDeFichiers.getSelectedIndices().length > 0);
						
						if (liste.getSize() == 0)
							addToList.setVisible(true);
						
						lblFichiers.setText(Utils.nombreDeFichiers());
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

		popupListe = new JPopupMenu();
		final JMenuItem numeriser = new JMenuItem(language.getProperty("menuItemNumeriser"));
		final JMenuItem visualiser = new JMenuItem(language.getProperty("menuItemVisualiser"));
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
		final JMenuItem ouvrirDossier = new JMenuItem(language.getProperty("menuItemOuvrirDossier"));
		final JMenuItem info = new JMenuItem(language.getProperty("menuItemInfo"));
		final JMenuItem rename = new JMenuItem(language.getProperty("menuItemRename"));
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

		visualiser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (listeDeFichiers.getSelectedIndices().length > 1) {
					String input = "";
					String filter = "";
					String hstack = "";
					int n = listeDeFichiers.getSelectedIndices().length;
					int i = 0;
					for (String video : listeDeFichiers.getSelectedValuesList()) {
						input += " -i " + '"' + video + '"';
						filter += "[" + i + ":v]scale=iw/" + n + ":ih/2[v" + i + "];";
						i++;
					}

					for (int v = 0; v < i; v++) {
						hstack += "[v" + v + "]";
					}

					hstack += "hstack=" + n + "[out]";

					FFMPEG.toFFPLAY(input + " -filter_complex " + '"' + filter + hstack + '"' + " -c:v rawvideo -map "
							+ '"' + "[out]" + '"' + " -map a? -f nut pipe:play |");
				} else {
					FFPROBE.Data(listeDeFichiers.getSelectedValue());

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

					String cmd = " -filter_complex " + '"' + videoOutput + audioOutput
							+ " -c:v rawvideo -map a? -f nut pipe:play |";

					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFMPEG.toFFPLAY(" -i " + '"' + listeDeFichiers.getSelectedValue() + '"' + cmd);

					if (FFMPEG.isRunning) {
						do {
							if (FFMPEG.error) {
								JOptionPane.showConfirmDialog(frame, language.getProperty("cantReadFile"),
										language.getProperty("menuItemVisualiser"), JOptionPane.PLAIN_MESSAGE,
										JOptionPane.ERROR_MESSAGE);
								break;
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							}
						} while (FFMPEG.isRunning || FFMPEG.error);
					}

					if (FFMPEG.isRunning)
						FFMPEG.process.destroy();

					enableAll();
					progressBar1.setValue(0);
				}
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

		ouvrirDossier.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.getProperty("os.name").contains("Mac")) 
				{
					try {
						Runtime.getRuntime().exec(new String[]{"/usr/bin/open", "-R", listeDeFichiers.getSelectedValue()});
					} catch (Exception e2){}
				}
				else if (System.getProperty("os.name").contains("Linux"))
				{
					try {
						Desktop.getDesktop().open(new File(listeDeFichiers.getSelectedValue()).getParentFile());
					} catch (Exception e2){}
				}
				else //Windows
				{
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + listeDeFichiers.getSelectedValue());
					} catch (IOException e1) {}
				}
			}
		});

		tempsTotal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int dureeTotale = 0;
				for (String file : listeDeFichiers.getSelectedValuesList()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.dureeTotale == 0 && FFPROBE.isRunning);
					dureeTotale += FFPROBE.dureeTotale;
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
						listeDeFichiers.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + language.getProperty("totalTime")
								+ " " + dureeFinale,
						language.getProperty("totalTimeFiles"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		poids.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int dureeTotale = 0;
				for (String file : listeDeFichiers.getSelectedValuesList()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.dureeTotale == 0 && FFPROBE.isRunning == true);
					dureeTotale += FFPROBE.dureeTotale;
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
						listeDeFichiers.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
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
					Utils.changeFrameVisibility(Informations.frame, Informations.shadow, false);

				for (String item : listeDeFichiers.getSelectedValuesList()) {
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

		arborescence.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File source = null;
				if (System.getProperty("os.name").contains("Mac")) {
					FileDialog dialog = new FileDialog(frame, language.getProperty("chooseFolderToCopy"), FileDialog.LOAD);
					
					if (listeDeFichiers.getSelectedIndices().length > 0)
						dialog.setDirectory(new File(listeDeFichiers.getSelectedValue().toString()).getParent());
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
					if (listeDeFichiers.getSelectedIndices().length > 0)
						dialog.setFilterPath(new File(listeDeFichiers.getSelectedValue().toString()).getParent());
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
					lblEncodageEnCours.setText(language.getProperty("compression"));
					lblDestination1.setText(dialog.getDirectory());
					progressBar1.setIndeterminate(true);

					StringBuilder items = new StringBuilder();
					for (String item : listeDeFichiers.getSelectedValuesList()) {
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
				File fichier = new File(listeDeFichiers.getSelectedValue());
				String path = fichier.getParentFile() + "/"
						+ fichier.getName().substring(0, fichier.getName().toString().lastIndexOf("."));

				lblEncodageEnCours.setText(language.getProperty("decompression"));
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
						if (listeDeFichiers.getSelectedIndices().length > 0)
							destination = new File(new File(listeDeFichiers.getSelectedValue()).getParent());							
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
					btnVider.doClick();
					scanIsRunning = false;
				}
				lblFichiers.setText(Utils.nombreDeFichiers());
			}
		});

		listeDeFichiers.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
						&& TSMUXER.isRunning == false && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
						&& liste.getSize() > 0)
					visualiser.doClick();

				if (e.getButton() == MouseEvent.BUTTON3 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && e.getButton() == MouseEvent.BUTTON1) {
					if (listeDeFichiers.getSelectedIndices().length > 0 && scan.getText().equals(language.getProperty("menuItemStartScan"))) {
						if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
								&& TSMUXER.isRunning == false) {
							// Ajout à la liste
							popupListe.removeAll();
							if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")))
							popupListe.add(silentTrack);
							popupListe.add(visualiser);
							popupListe.add(blackMagic);
							popupListe.add(ouvrirDossier);
							popupListe.add(scan);
							popupListe.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupListe.add(poids);
								break;
							}
							popupListe.add(info);
							popupListe.add(rename);
							popupListe.add(arborescence);
							popupListe.add(gop);
							popupListe.add(ftp);
							popupListe.add(zip);
							popupListe.add(unzip);

							// Décompression d'archives
							String firstElement = listeDeFichiers.getSelectedValue();
							if (firstElement.contains(".zip") || firstElement.contains(".rar")
									|| firstElement.contains(".7z") || firstElement.contains(".iso")) {
								if (listeDeFichiers.getSelectedIndices().length == 1)
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

							File fileOrDirectory = new File(listeDeFichiers.getSelectedValue());

							if (fileOrDirectory.isFile())
								popupListe.show(listeDeFichiers, e.getX() - 30, e.getY());
							else
								scanListe.show(listeDeFichiers, e.getX() - 30, e.getY());
						} else {
							// Ajout à la liste
							popupListe.removeAll();
							popupListe.add(ouvrirDossier);
							popupListe.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupListe.add(poids);
								break;
							}
							popupListe.add(info);
							popupListe.add(arborescence);
							popupListe.add(gop);
							popupListe.show(listeDeFichiers, e.getX() - 30, e.getY());
						}

					} else {
						scanListe.add(numeriser);
						scanListe.add(scan);
						scanListe.add(arborescence);
						scanListe.show(listeDeFichiers, e.getX() - 30, e.getY());
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				listeDeFichiers.setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));
			}

		});

		listeDeFichiers.addMouseMotionListener(new MouseMotionListener() {

			int anchor = -1;

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if (anchor == -1)
					anchor = listeDeFichiers.getSelectedIndex();

				listeDeFichiers.setSelectionInterval(anchor, listeDeFichiers.getSelectedIndex());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				anchor = -1;

			}

		});

		lblTermine = new JLabel(language.getProperty("lblTermine"));
		lblTermine.setFont(new Font("Montserrat", Font.PLAIN, 13));
		lblTermine.setForeground(new Color(71, 163, 236));
		lblTermine.setVisible(false);
		lblTermine.setBounds(213, 15, 94, 16);
		grpChoixDesFichiers.add(lblTermine);

		lblFichiers = new JLabel(Utils.nombreDeFichiers());
		lblFichiers.setForeground(Color.WHITE);
		lblFichiers.setFont(new Font("Montserrat", Font.PLAIN, 13));
		lblFichiers.setBounds(213, 30, 79, 16);
		grpChoixDesFichiers.add(lblFichiers);

		btnParcourir = new JButton(language.getProperty("btnParcourir"));
		btnParcourir.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnParcourir.setBounds(6, 19, 117, 25);
		grpChoixDesFichiers.add(btnParcourir);

		btnParcourir.addActionListener(new ActionListener() {

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
							Utils.loadSettings(new File(droppedFiles.toString()));
						}
						
						File file = files[i];
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						liste.addElement(file.getAbsolutePath());
					}
					lblFichiers.setText(Utils.nombreDeFichiers());
					changementDeFiltre();

					switch (comboFonctions.getSelectedItem().toString()) {
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
						FFPROBE.CalculH264();
						break;
					}
					
					// VideoPlayer
					VideoPlayer.setMedia();
					
					defaultFolder = dialog.getParent().toString();
					addToList.setVisible(false);
				}

			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void grpChoixFonction() {

		grpChoixFonction = new JPanel();
		grpChoixFonction.setLayout(null);
		grpChoixFonction.setBounds(10, 380, 312, 76);
		grpChoixFonction.setBackground(new Color(50, 50, 50));
		grpChoixFonction.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpChoixFonction") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpChoixFonction);

		btnAnnuler = new JButton(language.getProperty("btnAnnuler"));
		btnAnnuler.setEnabled(false);
		btnAnnuler.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnAnnuler.setBounds(205, 44, 101, 25);
		grpChoixFonction.add(btnAnnuler);

		btnAnnuler.addActionListener(new ActionListener() {

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
									Thread.sleep(100);
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
					enableAll();
					scan.setText(language.getProperty("menuItemStartScan"));
					btnVider.doClick();
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

		iconList = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/list.png")));
		iconList.setHorizontalAlignment(SwingConstants.CENTER);
		iconList.setVisible(false);
		iconList.setBounds(180, 46, 21, 21);
		grpChoixFonction.add(iconList);

		iconList.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconList.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/list3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept && btnStart.getText().equals(language.getProperty("btnStartFunction")))
				{
					iconList.setVisible(false);
					btnAnnuler.setBounds(205, 44, 101, 25);
					
					if (iconPresets.isVisible())
					{
						iconPresets.setBounds(180, 46, 21, 21);
						btnAnnuler.setBounds(205, 44, 101, 25);
					}
					else
						btnAnnuler.setBounds(182, 44, 124, 25);
					
					
					btnStart.setText(language.getProperty("btnAddToRender"));
					if (RenderQueue.frame == null)
						new RenderQueue();
					else
						Utils.changeFrameVisibility(RenderQueue.frame, RenderQueue.shadow, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconList.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/list2.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconList.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/list.png"))));
				accept = false;
			}

		});
				
		iconPresets = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/presets.png")));
		iconPresets.setHorizontalAlignment(SwingConstants.CENTER);
		iconPresets.setVisible(true);
		iconPresets.setBounds(180, 46, 21, 21);
		grpChoixFonction.add(iconPresets);

		iconPresets.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconPresets.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/presets3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept)
				{
					iconPresets.setVisible(false);
					if (iconList.isVisible())
						btnAnnuler.setBounds(205, 44, 101, 25);
					else
						btnAnnuler.setBounds(182, 44, 124, 25);
					
					if (Functions.frame == null)
						new Functions();
					else {
						if (Functions.listeDeFonctions.getModel().getSize() > 0) {
							Functions.lblSave.setVisible(false);
							Functions.lblDrop.setVisible(false);
						}

						Functions.frame.setVisible(true);
					}
					iconPresets.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/presets.png"))));
					Utils.changeFrameVisibility(Functions.frame, Functions.shadow, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconPresets.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/presets2.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconPresets.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/presets.png"))));
				accept = false;
			}

		});
			
		
		btnStart = new JButton(language.getProperty("btnStartFunction"));
		btnStart.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnStart.setBounds(6, 44, 172, 25);
		grpChoixFonction.add(btnStart);

		btnStart.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {				
				if ((btnStart.getText().equals(language.getProperty("btnStartFunction"))
						|| btnStart.getText().equals(language.getProperty("btnAddToRender"))) && liste.getSize() > 0) {
					
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
							if (language.getProperty("functionCut").equals(fonction)) {
								if (caseInAndOut.isSelected() || caseSetTimecode.isSelected())
									LosslessCut.main();
								else {
									JOptionPane.showMessageDialog(frame, language.getProperty("chooseInOutPoint"),
											language.getProperty("noInOuPoint"), JOptionPane.INFORMATION_MESSAGE);
									caseInAndOut.setSelected(true);
									new VideoPlayer();									
								}
							} else if ("WAV".equals(fonction)) {
								WAV.main();
							} else if ("MP3".equals(fonction)) {
								MP3.main();
							} else if ("AAC".equals(fonction)) {
								AAC.main();
							} else if ("AC3".equals(fonction)) {
								AC3.main();
							} else if ("OPUS".equals(fonction)) {
								OPUS.main();
							} else if ("OGG".equals(fonction)) {
								OGG.main();
							} else if ("AIFF".equals(fonction)) {
								AIFF.main();
							} else if ("FLAC".equals(fonction)) {
								FLAC.main();
							} else if ("Loudness & True Peak".equals(fonction)) {
								LoudnessTruePeak.main();
							} else if (language.getProperty("functionVideoLevels").equals(fonction)) {
								VideoLevels.main();
							} else if (language.getProperty("functionBab").equals(fonction)) {
								if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									EndToEnd.main();
							} else if (language.getProperty("functionExtract").equals(fonction)) { 
								if (comboFilter.getSelectedItem().toString().equals(language.getProperty("setAll")))
									Extract.extractAll();
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
									Extract.extractAudio();
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
									Extract.extractSubs();
								else
									Extract.main();
							} else if (language.getProperty("functionConform").equals(fonction)) {
									Conform.main();				
							} else if (language.getProperty("functionInsert").equals(fonction)) {
								if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									VideoInserts.main();
							} else if (language.getProperty("functionReplaceAudio").equals(fonction)) {
								if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
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
								if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									caseInAndOut.doClick();
									Utils.changeFrameVisibility(frame, shadow, true);
								}
							} else if (language.getProperty("functionNormalization").equals(fonction)) {
								AudioNormalization.main();
							} else if (language.getProperty("functionSceneDetection").equals(fonction)) {
								if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									if (SceneDetection.frame == null)
										new SceneDetection(true);
									else {
										Utils.changeFrameVisibility(SceneDetection.frame, SceneDetection.shadow, false);
										SceneDetection.btnAnalyse.doClick();
									}
								}
							} else if (language.getProperty("functionBlackDetection").equals(fonction)) {
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
						        
							} else if ("DNxHD".equals(fonction)) {
								DNxHD.main();
							} else if ("DNxHR".equals(fonction)) {
								DNxHR.main();
							} else if ("Apple ProRes".equals(fonction)) {
								AppleProRes.main();
							} else if ("GoPro CineForm".equals(fonction)) {
								CineForm.main();
							} else if ("QT Animation".equals(fonction)) {
								QTAnimation.main();
							}else if ("Uncompressed YUV".equals(fonction)) {
								UncompressedYUV.main();
							} else if ("H.264".equals(fonction)) {
								H264.main(true);
							} else if ("H.265".equals(fonction)) {
								H265.main(true);
							} else if ("WMV".equals(fonction)) {
								WMV.main(true);
							} else if ("MPEG".equals(fonction)) {
								MPEG.main(true);
							} else if ("VP9".equals(fonction)) {
								VP9.main(true);
							} else if ("AV1".equals(fonction)) {
								AV1.main(true);
							} else if ("OGV".equals(fonction)) {
								OGV.main(true);
							} else if ("MJPEG".equals(fonction)) {
								MJPEG.main(true);
							} else if ("Xvid".equals(fonction)) {
								Xvid.main(true);
							} else if ("XDCAM HD422".equals(fonction)) {
								XDCAM.main();
							} else if ("AVC-Intra 100".equals(fonction)) {
								AVC.main();
							} else if ("XAVC".equals(fonction)) {
								XAVC.main();	
							} else if ("HAP".equals(fonction)) {
								HAP.main();
							} else if ("FFV1".equals(fonction)) {
								FFV1.main();
							} else if ("DV PAL".equals(fonction)) {
								DVPAL.main();
							} else if ("DVD".equals(fonction)) {
								DVD.main();
							} else if ("Blu-ray".equals(fonction)) {
								Bluray.main();
							} else if (language.getProperty("functionPicture").equals(fonction) || "JPEG".equals(fonction)) {
								Picture.main();
							} else if (language.getProperty("functionRewrap").equals(fonction)) {
								if (comboFilter.getEditor().getItem().toString().equals(language.getProperty("aucun"))
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
				} else { // Fonctions n'ayant pas de fichiers dans la liste
					if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) {
						if (scanIsRunning)
							JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
									language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
						else {
							try {
								frame.setOpacity(0.5f);
							} catch (Exception er) {}
							new VideoWeb();
							frame.setOpacity(1.0f);
						}
					} else if (comboFonctions.getSelectedItem().equals("DVD RIP")
							&& btnStart.getText().equals(language.getProperty("btnStartFunction"))) {
						if (scanIsRunning)
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

						if (caseVisualiser.isSelected() == false) {
							FFMPEG.suspendProcess();
							btnStart.setText(language.getProperty("btnResumeFunction"));
						} else
							JOptionPane.showConfirmDialog(frame, language.getProperty("useBarSpace"), language.getProperty("btnPauseFunction"), JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);

						tempsRestant.setText(language.getProperty("timePause"));
					} else if (btnStart.getText().equals(language.getProperty("btnResumeFunction"))) {
						caseRunInBackground.setEnabled(true);

						FFMPEG.resumeProcess();

						btnStart.setText(language.getProperty("btnPauseFunction"));
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
				language.getProperty("functionBab"),		
				language.getProperty("functionExtract"),
				language.getProperty("functionSubtitles"),
				language.getProperty("functionInsert"),
				
				language.getProperty("itemAudioConversion"), "WAV", "AIFF", "FLAC", "MP3", "AAC", "AC3", "OPUS", "OGG",
				
				language.getProperty("itemEditingCodecs"), "DNxHD", "DNxHR", "Apple ProRes", "QT Animation", "GoPro CineForm" ,"Uncompressed YUV",
				
				language.getProperty("itemOuputCodecs"), "H.264", "H.265", "VP9", "AV1", "OGV",		
				
				language.getProperty("itemBroadcastCodecs"), "XDCAM HD422", "AVC-Intra 100", "XAVC", "HAP",
				
				language.getProperty("itemOldCodecs"), "DV PAL", "MJPEG", "Xvid", "WMV", "MPEG",
				
				language.getProperty("itemArchiveCodecs"), "FFV1",		
				
				language.getProperty("itemImage"),"JPEG",language.getProperty("functionPicture"), 
				
				language.getProperty("itemBurnRip"), "DVD", "Blu-ray", "DVD RIP",
				
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
		comboFonctions.setForeground(Color.BLACK);
		comboFonctions.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboFonctions.setEditable(true);
		comboFonctions.setMaximumRowCount(Toolkit.getDefaultToolkit().getScreenSize().height / 33);
		comboFonctions.setBounds(8, 19, 168, 22);
		comboFonctions.getModel().setSelectedItem("");
		comboFonctions.setRenderer(new ComboBoxRenderer());
		grpChoixFonction.add(comboFonctions);

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
					newList.add(language.getProperty("functionBab"));	
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
					newList.add("DVD RIP");
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
					changementDeFiltre();
					changementDeFonction(true);
					changeGroupes(false);
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

				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")) {
					changementDeFiltre();
					changeFrameSize(false);

					quit.setLocation(frame.getSize().width - 24, 0);
					reduce.setLocation(quit.getLocation().x - 21, 0);
					help.setLocation(reduce.getLocation().x - 21, 0);
					newInstance.setLocation(help.getLocation().x - 21, 0);
					
					addToList.setText(language.getProperty("dropFilesHere"));
					addToList.setVisible(true);
				} else {
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
							changementDeFiltre();
							changementDeFonction(true);
						}

					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						comboFonctions.setModel(new DefaultComboBoxModel(items));
						comboFonctions.getEditor().setItem("");
						comboFonctions.hidePopup();
						changementDeFiltre();
						changeGroupes(true);
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
		comboFilter.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboFilter.setEditable(true);
		comboFilter.setMaximumRowCount(20);
		comboFilter.setBounds(228, 19, 76, 22);
		grpChoixFonction.add(comboFilter);

		comboFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (lblFilter.getText().equals("Ext." + language.getProperty("colon")))
				{
					if (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov"))
						caseFastStart.setEnabled(true);
					else
						caseFastStart.setEnabled(false);
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
		lblFilter.setFont(new Font("Montserrat", Font.PLAIN, 13));
		lblFilter.setBounds(164, 21, 60, 16);
		grpChoixFonction.add(lblFilter);

	}

	private void grpDestination() {
		
		grpDestination = new WebTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);	
		grpDestination.setBounds(12, 462, 308, 76);
		grpDestination.setBackground(new Color(50, 50, 50));
		grpDestination.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1)));		
		grpDestination.setFont(new Font("Montserrat", Font.PLAIN, 11));	
		frame.getContentPane().add(grpDestination);		

		//DESTINATION 1		
		
		destination1 = new JPanel();
		destination1.setLayout(null);
		destination1.setBackground(new Color(50, 50, 50));
		destination1.setFont(new Font("Montserrat", Font.PLAIN, 12));	
				
		caseOpenFolderAtEnd1 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd1.setName("caseOpenFolderAtEnd1");
		caseOpenFolderAtEnd1.setSelected(true);
		caseOpenFolderAtEnd1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseOpenFolderAtEnd1.setBackground(new Color(50, 50, 50));
		caseOpenFolderAtEnd1.setBounds(6, 23, 207, 23);
		destination1.add(caseOpenFolderAtEnd1);

		caseChangeFolder1 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder1.setName("caseChangeFolder1");
		caseChangeFolder1.setBackground(new Color(50, 50, 50));
		caseChangeFolder1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseChangeFolder1.setBounds(212, 23, 89, 23);
		destination1.add(caseChangeFolder1);

		caseChangeFolder1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scanIsRunning) {
					caseChangeFolder1.setSelected(true);
				} else {

					if (caseChangeFolder1.isSelected()) {
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

						if (destination != null) {
							
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
										changementDeFiltre();
									}
								}
							}							
							
							if (lblDestination1.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(lblDestination1.getText());
							
						} else {
							caseChangeFolder1.setSelected(false);

							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnVider.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD RIP")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
							else
								lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
						} else
							lblDestination1.setText(language.getProperty("sameAsSource"));
					}
				} // End if scan
			}
		});

		lblDestination1 = new WebTextField(StyleId.textfieldTransparent);
		lblDestination1.setName("lblDestination1");
		lblDestination1.setEditable(false);
		lblDestination1.setForeground(new Color(71, 163, 236));
		lblDestination1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination1.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination1.setBackground(new Color(50, 50, 50));
		lblDestination1.setText(language.getProperty("sameAsSource"));
		lblDestination1.setBounds(6, 0, 290, 22);
		lblDestination1.setToolTipText(language.getProperty("rightClick"));
		destination1.add(lblDestination1);
		
		//DESTINATION 2
		
		destination2 = new JPanel();
		destination2.setLayout(null);
		destination2.setBackground(new Color(50, 50, 50));
		destination2.setFont(new Font("Montserrat", Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd2 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd2.setName("caseOpenFolderAtEnd2");
		caseOpenFolderAtEnd2.setSelected(false);
		caseOpenFolderAtEnd2.setEnabled(false);
		caseOpenFolderAtEnd2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseOpenFolderAtEnd2.setBackground(new Color(50, 50, 50));
		caseOpenFolderAtEnd2.setBounds(6, 23, 207, 23);
		destination2.add(caseOpenFolderAtEnd2);

		caseChangeFolder2 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder2.setName("caseChangeFolder2");
		caseChangeFolder2.setBackground(new Color(50, 50, 50));
		caseChangeFolder2.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
										changementDeFiltre();
									}
								}
							}
							
							if (lblDestination2.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(lblDestination2.getText());
							
						} else {
							caseChangeFolder2.setSelected(false);
							
							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnVider.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD RIP")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
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

		lblDestination2 = new WebTextField(StyleId.textfieldTransparent);
		lblDestination2.setName("lblDestination2");
		lblDestination2.setEditable(false);
		lblDestination2.setForeground(new Color(71, 163, 236));
		lblDestination2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination2.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination2.setBackground(new Color(50, 50, 50));
		lblDestination2.setText(language.getProperty("aucune"));
		lblDestination2.setBounds(6, 0, 290, 22);
		lblDestination2.setToolTipText(language.getProperty("rightClick"));
		destination2.add(lblDestination2);
		
		//DESTINATION 3
		
		destination3 = new JPanel();
		destination3.setLayout(null);
		destination3.setBackground(new Color(50, 50, 50));
		destination3.setFont(new Font("Montserrat", Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd3 = new JRadioButton(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd3.setName("caseOpenFolderAtEnd3");
		caseOpenFolderAtEnd3.setSelected(false);
		caseOpenFolderAtEnd3.setEnabled(false);
		caseOpenFolderAtEnd3.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseOpenFolderAtEnd3.setBackground(new Color(50, 50, 50));
		caseOpenFolderAtEnd3.setBounds(6, 23, 207, 23);
		destination3.add(caseOpenFolderAtEnd3);

		caseChangeFolder3 = new JRadioButton(language.getProperty("caseChangeFolder"));
		caseChangeFolder3.setName("caseChangeFolder3");
		caseChangeFolder3.setEnabled(false);
		caseChangeFolder3.setBackground(new Color(50, 50, 50));
		caseChangeFolder3.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
										changementDeFiltre();
									}
								}
							}
							
							if (lblDestination3.getText() != language.getProperty("sameAsSource") && Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(lblDestination3.getText());
							
						} else {
							caseChangeFolder3.setSelected(false);

							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnVider.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD RIP")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
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

		lblDestination3 = new WebTextField(StyleId.textfieldTransparent);
		lblDestination3.setName("lblDestination3");
		lblDestination3.setEditable(false);
		lblDestination3.setForeground(new Color(71, 163, 236));
		lblDestination3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination3.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination3.setBackground(new Color(50, 50, 50));
		lblDestination3.setText(language.getProperty("aucune"));
		lblDestination3.setBounds(6, 0, 290, 22);
		lblDestination3.setToolTipText(language.getProperty("rightClick"));
		destination3.add(lblDestination3);
		
		//MAIL
		
		destinationMail = new JPanel();
		destinationMail.setLayout(null);
		destinationMail.setBackground(new Color(50, 50, 50));
		destinationMail.setFont(new Font("Montserrat", Font.PLAIN, 12));
				
		caseSendMail = new JRadioButton(Shutter.language.getProperty("caseSendMail"));
		caseSendMail.setName("caseSendMail");
		caseSendMail.setSelected(false);
		caseSendMail.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseSendMail.setBackground(new Color(50, 50, 50));
		caseSendMail.setBounds(6, -2, caseSendMail.getPreferredSize().width, 23);
		destinationMail.add(caseSendMail);
		
		caseSendMail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseSendMail.isSelected())
				{
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					textMail.setText(language.getProperty("textMail"));
					textMail.setText(language.getProperty("textMail"));
					textMail.setEnabled(true);	
				}
				else
				{
					textMail.setText("");
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
		textMail.setText("");
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
					if (Settings.comboTheme.getSelectedItem().equals(Shutter.language.getProperty("darkTheme")))
						textMail.setForeground(Color.WHITE);
					else
						textMail.setForeground(Color.BLACK);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					textMail.setText("");
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
		lblDestination1.setTransferHandler(new DestinationFileTransferHandler1());
		lblDestination2.setTransferHandler(new DestinationFileTransferHandler2());
		lblDestination3.setTransferHandler(new DestinationFileTransferHandler3());

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

	private void grpProgression() {
		grpProgression = new JPanel();
		grpProgression.setLayout(null);
		grpProgression.setBounds(10, 544, 312, 94);
		grpProgression.setBackground(new Color(50, 50, 50));
		grpProgression.setToolTipText(language.getProperty("rightClick"));
		grpProgression.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpProgression") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		frame.getContentPane().add(grpProgression);

		caseRunInBackground = new JRadioButton(language.getProperty("caseRunInBackground"));
		caseRunInBackground.setName("caseRunInBackground");
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setBackground(new Color(50, 50, 50));
		caseRunInBackground.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseRunInBackground.setBounds(9, 64, caseRunInBackground.getPreferredSize().width, 23);
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

		caseVisualiser = new JRadioButton(language.getProperty("menuItemVisualiser"));
		caseVisualiser.setName("caseVisualiser");
		caseVisualiser.setEnabled(false);
		caseVisualiser.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseVisualiser.setBackground(new Color(50, 50, 50));
		caseVisualiser.setBounds(215, 64, caseVisualiser.getPreferredSize().width, 23);
		grpProgression.add(caseVisualiser);

		progressBar1 = new JProgressBar();
		progressBar1.setBounds(6, 42, 300, 14);
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

		lblEncodageEnCours = new JLabel(language.getProperty("lblEncodageEnCours"));
		lblEncodageEnCours.setHorizontalAlignment(SwingConstants.CENTER);
		lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
		lblEncodageEnCours.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblEncodageEnCours.setBounds(6, 19, 300, 16);
		grpProgression.add(lblEncodageEnCours);

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
		grpResolution.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpResolution") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpResolution.setBackground(new Color(50, 50, 50));
		grpResolution.setBounds(334, 59, 312, 145);
		frame.getContentPane().add(grpResolution);

		comboResolution = new JComboBox<String>();
		comboResolution.setName("comboResolution");
		comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "2:1", "4:1", "8:1", "16:1",
				"4096:auto", "1920:auto", "1280:auto", "auto:480", "auto:360",
				"4096x2160", "3840x2160", "1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000",
				"854x480", "720x576", "640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
		comboResolution.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboResolution.setEditable(true);
		comboResolution.setBounds(83, 18, 120, 22);
		comboResolution.setMaximumRowCount(21);
		grpResolution.add(comboResolution);

		caseRognerImage = new JRadioButton(language.getProperty("caseCropImage"));
		caseRognerImage.setName("caseRognerImage");
		caseRognerImage.setBackground(new Color(50, 50, 50));
		caseRognerImage.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseRognerImage.setBounds(7, 47, caseRognerImage.getPreferredSize().width, 23);
		grpResolution.add(caseRognerImage);

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
						CropImage.loadImage();
						Utils.changeDialogVisibility(CropImage.frame, CropImage.shadow, false);
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {
	
							@Override
							public void run() {
								do {
									try {
										Thread.sleep(100);
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
		caseRotate.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseRotate.setBackground(new Color(50, 50, 50));
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
		comboRotate.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboRotate.setEditable(false);
		comboRotate.setBounds(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3, 42,
				16);
		comboRotate.setMaximumRowCount(20);
		grpResolution.add(comboRotate);

		caseMiror = new JRadioButton(language.getProperty("caseMiror"));
		caseMiror.setName("caseMiror");
		caseMiror.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseMiror.setBackground(new Color(50, 50, 50));
		caseMiror.setBounds(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y, caseMiror.getPreferredSize().width,	23);
		grpResolution.add(caseMiror);

		caseCreateSequence = new JRadioButton(language.getProperty("caseCreateSequence"));
		caseCreateSequence.setName("caseCreateSequence");
		caseCreateSequence.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseCreateSequence.setBackground(new Color(50, 50, 50));
		caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseCreateSequence.getPreferredSize().width, 23);
		grpResolution.add(caseCreateSequence);

		caseCreateSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseCreateSequence.isSelected())
					comboInterpret.setEnabled(true);
				else
					comboInterpret.setEnabled(false);				
			}
			
		});
		
		lblInterpretation = new JLabel(Shutter.language.getProperty("lblInterpretation"));
		lblInterpretation.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblInterpretation.setBounds(28, caseCreateSequence.getY() + caseCreateSequence.getHeight() + 2, lblInterpretation.getPreferredSize().width, 16);
		grpResolution.add(lblInterpretation);
		
		comboInterpret = new JComboBox<String>();
		comboInterpret.setName("comboInterpret");
		comboInterpret.setEnabled(false);
		comboInterpret.setModel(new DefaultComboBoxModel<String>(
				new String[] { "1", "5", "10", "15","20", "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboInterpret.setSelectedIndex(7);
		comboInterpret.setMaximumRowCount(20);
		comboInterpret.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboInterpret.setEditable(true);
		comboInterpret.setSize(63, 16);
		comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4, lblInterpretation.getLocation().y);
		grpResolution.add(comboInterpret);
		
		lblIsInterpret = new JLabel("i/s");
		lblIsInterpret.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblIsInterpret.setSize(20, 16);
		lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblInterpretation.getLocation().y - 1);
		grpResolution.add(lblIsInterpret);
		
		iconTVInterpret = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVInterpret.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVInterpret.setBounds(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY() + 1, 16, 16);
		iconTVInterpret.setToolTipText(language.getProperty("preview"));
		grpResolution.add(iconTVInterpret);
		
		iconTVInterpret.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Définition de la taille
				if (liste.getSize() > 0) {
					
					String file = ""; 
					// Fichiers sélectionnés ?
					if (listeDeFichiers.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = f.toString();
									break;
								}
							}
						} else
							file = listeDeFichiers.getSelectedValue().toString();

					} else
						file = liste.firstElement();
					
					//Analyse
					FFPROBE.Data(file.toString());	
					do
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					while (FFPROBE.isRunning);
				
					//FFMPEGTOFFPLAY
					FFMPEG.toFFPLAY(" -r " + (float) FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", ".")) + " -i " + '"' + file + '"' + " -vf scale=640:-1 -r 1 -c:v rawvideo -map v -an -f nut pipe:play |");
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
				iconTVInterpret.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVInterpret.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
			}

		});
		
		lblTaille = new JLabel(Shutter.language.getProperty("lblTailleH264"));
		lblTaille.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblTaille.setBounds(42, 20, 42, 16);
		grpResolution.add(lblTaille);

		iconTVResolution = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVResolution.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVResolution.setBounds(14, 21, 16, 16);
		iconTVResolution.setToolTipText(language.getProperty("preview"));
		grpResolution.add(iconTVResolution);

		iconTVResolution.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Définition de la taille
				if (liste.getSize() > 0) {
					
					String file = ""; 
					// Fichiers sélectionnés ?
					if (listeDeFichiers.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = f.toString();
									break;
								}
							}
						} else
							file = listeDeFichiers.getSelectedValue().toString();

					} else
						file = liste.firstElement();
					
					String extension = file.substring(file.lastIndexOf("."));
					
					boolean isRaw = false;
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
					
					 //Analyse
					if  (extension.toLowerCase().equals(".pdf"))
					{
						 XPDF.toFFPROBE(file.toString());	
						 do
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						 while (XPDF.isRunning);
					}
					else if (isRaw)
					{
						 EXIFTOOL.run(file.toString());	
						 do
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						 while (EXIFTOOL.isRunning);
					}
					else
					{
						 FFPROBE.Data(file.toString());	
						 do
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						while (FFPROBE.isRunning);
					}

					String rotate = "";
					if (caseRotate.isSelected()) {
						String transpose = "";
						switch (comboRotate.getSelectedItem().toString()) {
						case "90":
							if (caseMiror.isSelected())
								transpose = "transpose=3";
							else
								transpose = "transpose=1";
							break;
						case "-90":
							if (caseMiror.isSelected())
								transpose = "transpose=0";
							else
								transpose = "transpose=2";
							break;
						case "180":
							if (caseMiror.isSelected())
								transpose = "transpose=1,transpose=1,hflip";
							else
								transpose = "transpose=1,transpose=1";
							break;
						}

						rotate = transpose;
					}

					String miror = "";
					if (caseMiror.isSelected() && caseRotate.isSelected() == false)
						miror = "hflip";

					String filter = "";
					String frameSize = "";
					if (caseRognerImage.isSelected())
						filter = " -vf " + cropFinal;
					
					if (comboResolution.getSelectedItem().toString().contains(":")) {
						
			        	if (comboResolution.getSelectedItem().toString().contains("auto"))
			        	{
			        		String s[] = comboResolution.getSelectedItem().toString().split(":");
			        		if (s[0].toString().equals("auto"))
			        			frameSize = "scale=-1:" + s[1];
			        		else
			        			frameSize = "scale="+s[0]+":-1";
			        	}
			        	else
			        	{
				            String s[] = comboResolution.getSelectedItem().toString().split(":");
				    		float number =  (float) 1 / Integer.parseInt(s[0]);
				    		frameSize = "scale=iw*" + number + ":ih*" + number;
			        	}
						
					} else if (comboResolution.getSelectedItem().toString().contains("x")) {
						String i[] = FFPROBE.imageResolution.split("x");
			        	String o[] = comboResolution.getSelectedItem().toString().split("x");         	
			
			        	int iw = Integer.parseInt(i[0]);
			        	int ih = Integer.parseInt(i[1]);		        	
			        	
			        	int ow = Integer.parseInt(o[0]);
			        	int oh = Integer.parseInt(o[1]);        	
			        	float ir = (float) iw / ih;
			        	
			        	//Original sup. à la sortie
			        	if (iw > ow && ih > oh)
			        	{
			        		//Si la hauteur calculée est > à la hauteur de sortie
			        		if ( (float) ow / ir >= oh)
			        			frameSize = "scale=" + ow + ":-1,crop=" + "'" + ow + ":" + oh + ":0:(ih-oh)*0.5" + "'";
			        		else
			        			frameSize = "scale=-1:" + oh + ",crop=" + "'" + ow + ":" + oh + ":(iw-ow)*0.5:0" + "'";
			        	}
			        	else
			        		frameSize = "scale=" + ow + ":" + oh;
					}
										
					if (frameSize != "")
					{
						if (filter != "")
							filter += "," + frameSize;
						else	
							filter = " -vf " + frameSize;
					}
					
					if (filter != "")
					{
						if (rotate != "")
							filter += "," + rotate;
						if (miror != "")
							filter += "," + miror;
					}
					else
					{
						if (rotate != "" && miror != "")
							filter += " -vf " + rotate + "," + miror;
						else if (rotate != "")
							filter += " -vf " + rotate;
						else if (miror != "")
							filter += " -vf " + miror;
					}

					String compression = "";
					int fileSize = 0;
					if (comboFonctions.getSelectedItem().toString().equals("JPEG") && extension.toLowerCase().equals(".pdf") == false && isRaw == false)
					{
						int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));						
						compression = " -q:v " + q;
														    		
						File fileOut = new File(dirTemp + "fileSize.jpg");
						if (fileOut.exists()) fileOut.delete();
						
						//InOut		
						try {
							FFMPEG.fonctionInOut();
							
							FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + filter + compression + " -vframes 1 " + '"' + fileOut + '"');							
							
							do
								try {
									Thread.sleep(100);
								} catch (InterruptedException e1) {}
							while(FFMPEG.runProcess.isAlive());
							
							enableAll();
							
							if (fileOut.exists())
							{
								fileSize = (int) (float) fileOut.length() / 1024;						
								fileOut.delete();
							}
						} catch (InterruptedException e2) {}
						
						if (filter != "")
							filter += ",drawtext=fontfile=" + pathToFont + ":text='" + fileSize + "Ko" + "':" + '"' + "x=(w-tw)*0.95:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
						else
							filter = " -vf drawtext=fontfile=" + pathToFont + ":text='" + fileSize + "Ko" + "':" + '"' + "x=(w-tw)*0.95:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
					}							
					
					//FFPLAY
					if (extension.toLowerCase().equals(".pdf"))
						XPDF.toFFPLAY(filter);
					else if (isRaw)
						DCRAW.toFFPLAY(filter);
					else if (comboFonctions.getSelectedItem().toString().equals("JPEG"))
					{
						String cmd = filter + " -an -c:v mjpeg" + compression + " -vframes 1 -f nut pipe:play |";
						FFMPEG.toFFPLAY(FFMPEG.inPoint + " -i " + '"' + file + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd);
					}
					else
						FFPLAY.run(" -i " + '"' + file + '"' + filter);
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
				iconTVResolution.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVResolution.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
			}

		});

		JLabel lblPixels = new JLabel("pixels");
		lblPixels.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblPixels.setBounds(206, 21, 32, 16);
		grpResolution.add(lblPixels);

	}

	private void grpSequenceImage() {
		grpSequenceImage = new JPanel();
		grpSequenceImage.setLayout(null);
		grpSequenceImage.setVisible(false);
		grpSequenceImage.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpSequenceImage") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpSequenceImage.setBackground(new Color(50, 50, 50));
		grpSequenceImage.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpSequenceImage);
		
		grpSequenceImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final int sized = 138;
				if (grpSequenceImage.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;

										grpSequenceImage.setSize(312, i);
										grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
										grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										
										grpSequenceImage.setSize(312, i);
										grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
										grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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

		caseActiverSequence = new JRadioButton(language.getProperty("caseActiverSequence"));
		caseActiverSequence.setName("caseActiverSequence");
		caseActiverSequence.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseActiverSequence.setBackground(new Color(50, 50, 50));
		caseActiverSequence.setBounds(7, 16, caseActiverSequence.getPreferredSize().width, 23);
		grpSequenceImage.add(caseActiverSequence);
		
		caseSequenceFPS = new JComboBox<String>();
		caseSequenceFPS.setName("caseSequenceFPS");
		caseSequenceFPS.setEnabled(false);
		caseSequenceFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		caseSequenceFPS.setSelectedIndex(2);
		caseSequenceFPS.setMaximumRowCount(20);
		caseSequenceFPS.setFont(new Font("FreeSans", Font.PLAIN, 11));
		caseSequenceFPS.setEditable(true);
		caseSequenceFPS.setBounds(caseActiverSequence.getLocation().x + caseActiverSequence.getWidth() + 4, caseActiverSequence.getLocation().y + 3, 50, 16);
		grpSequenceImage.add(caseSequenceFPS);

		JLabel lblSequenceFPS = new JLabel("i/s");
		lblSequenceFPS.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblSequenceFPS.setBounds(caseSequenceFPS.getLocation().x + caseSequenceFPS.getWidth() + 4, caseSequenceFPS.getLocation().y, 13, 16);
		grpSequenceImage.add(lblSequenceFPS);

		caseActiverSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseActiverSequence.isSelected())
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
		caseBlend.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseBlend.setBackground(new Color(50, 50, 50));
		caseBlend.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseBlend.getPreferredSize().width + 10, 23);
		grpSequenceImage.add(caseBlend);

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
		sliderBlend.setBounds(163, caseBlend.getLocation().y, 110, 22);
		grpSequenceImage.add(sliderBlend);

		iconTVBlend = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVBlend.setToolTipText(language.getProperty("preview"));
		iconTVBlend.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVBlend.setBounds(289, caseBlend.getLocation().y + 4, 16, 16);
		grpSequenceImage.add(iconTVBlend);

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
				
				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0 && sliderBlend.getValue() > 0) 
				{
					if (scanIsRunning)
					{
						File dir = new File(Shutter.liste.firstElement());
						for (File f : dir.listFiles()) {
							if (f.isHidden() == false && f.isFile()) {
								file = f.toString();
								break;
							}
						}
					} 
					else
						file = liste.firstElement();
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -i " + '"' + fileOut + '"' + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ blend + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
					}
					else
					{
						String fileOut = file;
						if (listeDeFichiers.getSelectedIndices().length > 0)
							fileOut = listeDeFichiers.getSelectedValue().toString();
						
						FFPLAY.run("-fs -i " + '"' + fileOut + '"' + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ blend + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
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
				iconTVBlend.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVBlend.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
		caseMotionBlur.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseMotionBlur.setBackground(new Color(50, 50, 50));
		caseMotionBlur.setBounds(7, caseBlend.getHeight() + caseBlend.getLocation().y, caseMotionBlur.getPreferredSize().width, 23);
		grpSequenceImage.add(caseMotionBlur);
		
	}
	
	private void grpFiltreImage() {
		grpFiltreImage = new JPanel();
		grpFiltreImage.setLayout(null);
		grpFiltreImage.setVisible(false);
		grpFiltreImage.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpFiltreImage") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpFiltreImage.setBackground(new Color(50, 50, 50));
		grpFiltreImage.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpFiltreImage);

		grpFiltreImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (grpFiltreImage.getSize().height < 122) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;
										grpFiltreImage.setSize(312, i);
										btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										grpFiltreImage.setSize(312, i);
										btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}
										
										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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
		caseYear.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseYear.setBackground(new Color(50, 50, 50));
		caseYear.setBounds(7, 16, caseYear.getPreferredSize().width, 23);
		grpFiltreImage.add(caseYear);
		
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
		comboYear.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboYear.setEditable(true);
		comboYear.setBounds(caseYear.getWidth() + caseYear.getLocation().x + 4, caseYear.getLocation().y + 4, 54, 16);
		comboYear.setMaximumRowCount(15);
		grpFiltreImage.add(comboYear);
		
		caseMonth = new JRadioButton(language.getProperty("caseMonth"));
		caseMonth.setName("caseMonth");
		caseMonth.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseMonth.setBackground(new Color(50, 50, 50));
		caseMonth.setBounds(7 , caseYear.getLocation().y + caseYear.getHeight() + 2, caseMonth.getPreferredSize().width, 23);
		grpFiltreImage.add(caseMonth);
		
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
		comboMonth.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboMonth.setEditable(true);
		comboMonth.setBounds(caseMonth.getWidth() + caseMonth.getLocation().x + 4, caseMonth.getLocation().y + 4, 40, 16);
		comboMonth.setMaximumRowCount(15);
		grpFiltreImage.add(comboMonth);
		
		caseDay = new JRadioButton(language.getProperty("caseDay"));
		caseDay.setName("caseDay");
		caseDay.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseDay.setBackground(new Color(50, 50, 50));
		caseDay.setBounds(7 , caseMonth.getLocation().y + caseMonth.getHeight() + 2, caseDay.getPreferredSize().width, 23);
		grpFiltreImage.add(caseDay);
		
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
		comboDay.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboDay.setEditable(true);
		comboDay.setBounds(caseDay.getWidth() + caseDay.getLocation().x + 4, caseDay.getLocation().y + 4, 40, 16);
		comboDay.setMaximumRowCount(15);
		grpFiltreImage.add(comboDay);	
		
		caseFrom = new JRadioButton(language.getProperty("caseFrom"));
		caseFrom.setName("caseFrom");
		caseFrom.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseFrom.setBackground(new Color(50, 50, 50));
		caseFrom.setBounds(7, caseDay.getLocation().y + caseDay.getHeight() + 2, caseFrom.getPreferredSize().width, 23);
		grpFiltreImage.add(caseFrom);
		
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
		comboFrom.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboFrom.setEditable(true);
		comboFrom.setBounds(caseFrom.getWidth() + caseFrom.getLocation().x + 4, caseFrom.getLocation().y + 4, 54, 16);
		comboFrom.setMaximumRowCount(15);
		grpFiltreImage.add(comboFrom);
	
		JLabel h1 = new JLabel(language.getProperty("lblH"));
		h1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		h1.setBounds(comboFrom.getLocation().x + comboFrom.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpFiltreImage.add(h1);
		
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
		comboTo.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboTo.setEditable(true);
		comboTo.setBounds(h1.getWidth() + h1.getLocation().x, comboFrom.getLocation().y, 54, 16);
		comboTo.setMaximumRowCount(15);
		grpFiltreImage.add(comboTo);
		
		JLabel h2 = new JLabel(language.getProperty("lblH"));
		h2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		h2.setBounds(comboTo.getLocation().x + comboTo.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpFiltreImage.add(h2);
		
	}

	private void grpLUTs() {
		grpLUTs = new JPanel();
		grpLUTs.setLayout(null);
		grpLUTs.setVisible(false);
		grpLUTs.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpLUTs") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpLUTs.setBackground(new Color(50, 50, 50));
		grpLUTs.setBounds(334, 199, 312, 17);
		frame.getContentPane().add(grpLUTs);
		
		grpLUTs.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final int sized = 143;
				if (grpLUTs.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;
										
										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpLUTs.setSize(312, i);
											grpFiltreImage.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);										
										}
										else
										{
											grpLUTs.setSize(312, i);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										
										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										
										if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpLUTs.setSize(312, i);
											grpFiltreImage.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);										
										}
										else
										{
											grpLUTs.setSize(312, i);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										
										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
												}
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
		caseColor.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseColor.setBounds(7, 16, caseColor.getPreferredSize().width, 22);
		grpLUTs.add(caseColor);
			
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
				    		FFPROBE.Data(liste.firstElement());
						}
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (FFPROBE.isRunning);
						
						ColorImage.positionVideo.setValue(0);
						ColorImage.positionVideo.setMaximum(FFPROBE.dureeTotale);
						
						ColorImage.loadImage(true);
						
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (new File(dirTemp + "preview.bmp").exists() == false && FFMPEG.error == false && DCRAW.error == false && XPDF.error == false);
						
						Utils.changeFrameVisibility(ColorImage.frame, ColorImage.shadow, false);
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {
	
							@Override
							public void run() {
								do {
									try {
										Thread.sleep(100);
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
						Utils.changeFrameVisibility(ColorImage.frame, ColorImage.shadow, true);
				}
			}
			
		});
		
		caseColormatrix = new JRadioButton(language.getProperty("caseColormatrix"));
		caseColormatrix.setName("caseColormatrix");
		caseColormatrix.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseColormatrix.setBounds(7, caseColor.getLocation().y + caseColor.getHeight(), caseColormatrix.getPreferredSize().width, 22);
		grpLUTs.add(caseColormatrix);
		
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
		comboInColormatrix.setEditable(false);
		comboInColormatrix.setEnabled(false);
		comboInColormatrix.setSelectedIndex(0);
		comboInColormatrix.setMaximumRowCount(20);
		comboInColormatrix.setBounds(grpLUTs.getWidth() - 160 - 7, caseColormatrix.getLocation().y + 4, 70, 16);
		grpLUTs.add(comboInColormatrix);
		
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
		
		JLabel lblTo = new JLabel("->");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblTo.setBounds(comboInColormatrix.getX() + comboInColormatrix.getWidth() + 4, caseColormatrix.getLocation().y + 4, lblTo.getPreferredSize().width, 16);
		grpLUTs.add(lblTo);
		
		comboOutColormatrix = new JComboBox<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "SDR"});		
		comboOutColormatrix.setName("comboOutColormatrix");
		comboOutColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutColormatrix.setEditable(false);
		comboOutColormatrix.setEnabled(false);
		comboOutColormatrix.setSelectedIndex(1);
		comboOutColormatrix.setMaximumRowCount(20);
		comboOutColormatrix.setBounds(grpLUTs.getWidth() - 70 - 7, caseColormatrix.getLocation().y + 4, 70, 16);
		grpLUTs.add(comboOutColormatrix);
		
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
		caseColorspace.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseColorspace.setBounds(7, caseColormatrix.getLocation().y + caseColormatrix.getHeight(), caseColorspace.getPreferredSize().width, 22);
		grpLUTs.add(caseColorspace);
		
		caseColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseColorspace.isSelected()) 
				{
					comboColorspace.setEnabled(true);
					
					if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("422")))
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
					
					if (comboFonctions.getSelectedItem().toString().equals("VP9"))
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
		comboColorspace.setBounds(grpLUTs.getWidth() - 160 - 7, caseColorspace.getLocation().y + 4, 160, 16);
		grpLUTs.add(comboColorspace);
		
		comboColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("422")))
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
						if (comboFonctions.getSelectedItem().toString().equals("VP9"))
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
		caseLUTs.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseLUTs.setBounds(7, caseColorspace.getLocation().y + caseColorspace.getHeight(), caseLUTs.getPreferredSize().width, 22);
		grpLUTs.add(caseLUTs);

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

	        for (int i = 0 ; i < data.length ; i++) {
	        	if (new File(data[i].toString()).isHidden() == false)
	        		LUTs.add(new File(data[i].toString()).getName());
		    }
		}

		JButton btnLUTs = new JButton(language.getProperty("btnManage"));
		btnLUTs.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnLUTs.setBounds(comboColorspace.getX() - 2, caseLUTs.getY(), grpLUTs.getWidth() - comboColorspace.getX() - 2, 25);
		grpLUTs.add(btnLUTs);

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
		comboLUTs.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboLUTs.setEditable(false);
		comboLUTs.setEnabled(false);
		comboLUTs.setMaximumRowCount(20);
		comboLUTs.setBounds(7, caseLUTs.getLocation().y + caseLUTs.getHeight() + 7, grpLUTs.getWidth() - 38, 22);
		grpLUTs.add(comboLUTs);

		iconTVLUTs = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVLUTs.setToolTipText(language.getProperty("preview"));
		iconTVLUTs.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVLUTs.setSize(16, 16);
		iconTVLUTs.setLocation(289, comboLUTs.getLocation().y + 4);
		grpLUTs.add(iconTVLUTs);

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
					String file = "";
	
					// Définition de la taille
					if (liste.getSize() > 0) {
						// Fichiers sélectionnés ?
						if (listeDeFichiers.getSelectedIndices().length > 0) {
							if (scanIsRunning) {
								File dir = new File(Shutter.liste.firstElement());
								for (File f : dir.listFiles()) {
									if (f.isHidden() == false && f.isFile()) {
										file = '"' + f.toString() + '"';
										break;
									}
								}
							} else
								file = '"' + listeDeFichiers.getSelectedValue().toString() + '"';
	
						} else
							file = '"' + liste.firstElement() + '"';
	
						FFPLAY.run("-i " + file + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ lut3d + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
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
				iconTVLUTs.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVLUTs.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
					
			        for (int i = 0 ; i < data.length ; i++) { 
			        	if (new File(data[i].toString()).isHidden() == false)
			        		LUTs.add(new File(data[i].toString()).getName());
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
		grpSetTimecode.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1), "Timecode" + " ",
				0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpSetTimecode.setBackground(new Color(50, 50, 50));
		grpSetTimecode.setBounds(334, 258, 312, 17);
		frame.getContentPane().add(grpSetTimecode);

		caseSetTimecode = new JRadioButton(language.getProperty("caseSetTimecode"));
		caseSetTimecode.setName("caseSetTimecode");
		caseSetTimecode.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseSetTimecode.setBackground(new Color(50, 50, 50));
		caseSetTimecode.setBounds(7, 16, 143, 23);
		grpSetTimecode.add(caseSetTimecode);

		grpSetTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 36;
				for (Component c : grpSetTimecode.getComponents()) {
					if (c instanceof JRadioButton)
						size += 17;
				}

				final int sized = size;
				if (grpSetTimecode.getSize().height < sized) {
					Thread changeSize = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									int i = 17;
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;

											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) 
													|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetTimecode.setSize(312, i);
												grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
												btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut")))
											{
												grpSetTimecode.setSize(312, i);
												btnReset.setLocation(334, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											}
											else
											{
												grpSetTimecode.setSize(312, i);
												grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
										
										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
													|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetTimecode.setSize(312, i);
												grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
												btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut")))
											{
												grpSetTimecode.setSize(312, i);
												btnReset.setLocation(334, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
											}
											else
											{
												grpSetTimecode.setSize(312, i);
												grpSetAudio.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}
										
										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
												}
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
				} else {
					TCset1.setEnabled(false);
					TCset2.setEnabled(false);
					TCset3.setEnabled(false);
					TCset4.setEnabled(false);
					caseIncrementTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);
				}

			}

		});

		TCset1 = new JTextField();
		TCset1.setName("TCset1");
		TCset1.setEnabled(false);
		TCset1.setText("00");
		TCset1.setHorizontalAlignment(SwingConstants.CENTER);
		TCset1.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TCset1.setColumns(10);
		TCset1.setBounds(156, 17, 32, 21);
		grpSetTimecode.add(TCset1);

		TCset2 = new JTextField();
		TCset2.setName("TCset2");
		TCset2.setEnabled(false);
		TCset2.setText("00");
		TCset2.setHorizontalAlignment(SwingConstants.CENTER);
		TCset2.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TCset2.setColumns(10);
		TCset2.setBounds(192, 17, 32, 21);
		grpSetTimecode.add(TCset2);

		TCset3 = new JTextField();
		TCset3.setName("TCset3");
		TCset3.setEnabled(false);
		TCset3.setText("00");
		TCset3.setHorizontalAlignment(SwingConstants.CENTER);
		TCset3.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TCset3.setColumns(10);
		TCset3.setBounds(228, 17, 32, 21);
		grpSetTimecode.add(TCset3);

		TCset4 = new JTextField();
		TCset4.setName("TCset4");
		TCset4.setEnabled(false);
		TCset4.setText("00");
		TCset4.setHorizontalAlignment(SwingConstants.CENTER);
		TCset4.setFont(new Font("FreeSans", Font.PLAIN, 14));
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
		caseIncrementTimecode.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseIncrementTimecode.setBackground(new Color(50, 50, 50));
		caseIncrementTimecode.setBounds(7, caseSetTimecode.getY() + caseSetTimecode.getHeight(), caseIncrementTimecode.getPreferredSize().width, 23);
		grpSetTimecode.add(caseIncrementTimecode);
	}

	private void grpOverlay() {
		grpOverlay = new JPanel();
		grpOverlay.setLayout(null);
		grpOverlay.setVisible(false);
		grpOverlay.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1), language.getProperty("grpOverlay") + " ", 0,
				0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;

										if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
										{
											grpOverlay.setSize(312, i);
											btnReset.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpOverlay.setSize(312, i);
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpFiltreImage.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);											
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray")) 
										{
											grpOverlay.setSize(312, i);
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}										
										else
										{
											grpOverlay.setSize(312, i);
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										
										if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
										{
											grpOverlay.setSize(312, i);
											btnReset.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
										}
										else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
										{
											grpOverlay.setSize(312, i);
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpFiltreImage.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);											
										}
										else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray")) 
										{
											grpOverlay.setSize(312, i);
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}
										else
										{
											grpOverlay.setSize(312, i);
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										}

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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
		caseAddOverlay.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAddOverlay.setBackground(new Color(50, 50, 50));
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
							new OverlayWindow();
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
					    		FFPROBE.Data(liste.firstElement());
							}
							do {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e1) {}
							} while (FFPROBE.isRunning);
							
							OverlayWindow.caseShowTimecode.setEnabled(true);
							OverlayWindow.positionVideo.setValue(0);
							OverlayWindow.positionVideo.setMaximum(FFPROBE.dureeTotale);
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
										Thread.sleep(100);
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
		caseShowDate.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseShowDate.setBackground(new Color(50, 50, 50));
		caseShowDate.setSize(caseShowDate.getPreferredSize().width, 23);
		
		caseShowFileName = new JRadioButton(Shutter.language.getProperty("caseShowFileName"));
		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseShowFileName.setBackground(new Color(50, 50, 50));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
	}

	private void grpInAndOut() {
		grpInAndOut = new JPanel();
		grpInAndOut.setLayout(null);
		grpInAndOut.setVisible(false);
		grpInAndOut.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpInAndOut") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpInAndOut.setBackground(new Color(50, 50, 50));
		grpInAndOut.setBounds(334, 343, 312, 47);
		frame.getContentPane().add(grpInAndOut);
		
		caseInAndOut = new JRadioButton(language.getProperty("changeInOutPoint"));
		caseInAndOut.setName("caseInAndOut");
		caseInAndOut.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseInAndOut.setBackground(new Color(50, 50, 50));
		caseInAndOut.setBounds(7, 16, 291, 23);
		grpInAndOut.add(caseInAndOut);

		caseInAndOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (liste.getSize() == 0 && VideoPlayer.frame != null && VideoPlayer.frame.isVisible() == false)
				{
					caseInAndOut.setSelected(false);
					JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"), language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
				}
				else {
					if (caseInAndOut.isSelected()) {

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						if (VideoPlayer.waveform.exists())
							VideoPlayer.waveform.delete();
						
						new VideoPlayer();														
						
					} else {
						Utils.changeFrameVisibility(VideoPlayer.frame, VideoPlayer.shadow, true);
						if (VideoPlayer.mediaPlayerComponentLeft != null)
							VideoPlayer.mediaPlayerComponentLeft.getMediaPlayer().stop();
						if (VideoPlayer.mediaPlayerComponentRight != null)
							VideoPlayer.mediaPlayerComponentRight.getMediaPlayer().stop();
						VideoPlayer.frame.getContentPane().removeAll();

						switch (Shutter.comboFonctions.getSelectedItem().toString()) {
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
		grpSetAudio.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpAudio") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
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
						|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString()))
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										int sized;
										if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
												|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
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
											i++;
	
											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
													|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetAudio.setSize(312, i);
												btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")) == false) //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
												
	
											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));
	
												start = System.currentTimeMillis();
												fps = 0;
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y - 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y - 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y - 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y - 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y - 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y - 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y - 1);
											}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;
											
											if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
													|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))) 
											{
												grpSetAudio.setSize(312, i);
												btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")) == false) //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
	
											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));
	
												start = System.currentTimeMillis();
												fps = 0;
											}
	
											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
		caseChangeAudioCodec.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseChangeAudioCodec.setBackground(new Color(50, 50, 50));
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
				
				if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
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
		comboAudioCodec.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudioCodec.setEditable(false);
		comboAudioCodec.setSize(78, 16);
		comboAudioCodec.setLocation(caseChangeAudioCodec.getLocation().x + caseChangeAudioCodec.getWidth() + 7, caseChangeAudioCodec.getLocation().y + 3);
		grpSetAudio.add(comboAudioCodec);
					
		comboAudioBitrate = new JComboBox<String>();
		comboAudioBitrate.setName("comboAudioBitrate");
		comboAudioBitrate.setEnabled(false);
		comboAudioBitrate.setMaximumRowCount(20);
		comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "1536"}));
		comboAudioBitrate.setSelectedIndex(0);
		comboAudioBitrate.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudioBitrate.setEditable(false);
		comboAudioBitrate.setSize(47, 16);
		comboAudioBitrate.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		grpSetAudio.add(comboAudioBitrate);
		
		lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblKbs.setBounds(comboAudioBitrate.getLocation().x + comboAudioBitrate.getWidth() + 3, caseChangeAudioCodec.getLocation().y + 3, 33, 16);
		grpSetAudio.add(lblKbs);
		
		caseAudioOffset = new JRadioButton(language.getProperty("caseAudioOffset"));
		caseAudioOffset.setName("caseAudioOffset");
		caseAudioOffset.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAudioOffset.setBackground(new Color(50, 50, 50));
		caseAudioOffset.setBounds(7, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight(), caseChangeAudioCodec.getPreferredSize().width, 23);
		grpSetAudio.add(caseAudioOffset);
		
		caseAudioOffset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseAudioOffset.isSelected())
					txtAudioOffset.setEnabled(true);					
				else
					txtAudioOffset.setEnabled(false);								
			}

		});
		
		txtAudioOffset = new JTextField("0");
		txtAudioOffset.setName("txtAudioOffset");
		txtAudioOffset.setFont(new Font("FreeSans", Font.PLAIN, 11));
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
		
		lblOffsetFPS = new JLabel("i/s");
		lblOffsetFPS.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblOffsetFPS.setBounds(txtAudioOffset.getLocation().x + txtAudioOffset.getWidth() + 3, caseAudioOffset.getLocation().y + 4, 33, 16);
		grpSetAudio.add(lblOffsetFPS);
				
		iconTVOffset = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVOffset.setToolTipText(language.getProperty("preview"));
		iconTVOffset.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVOffset.setSize(16, 16);
		iconTVOffset.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getSize().width - 16, lblOffsetFPS.getLocation().y);
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
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.isRunning);
					
					float offset = (float) ((float) Integer.parseInt(txtAudioOffset.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000;
									
					FFPROBE.Data(audioFile);
							
					do {
						try {
							Thread.sleep(100);
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
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							}
						} while (FFMPEG.isRunning || FFMPEG.error);
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
				iconTVOffset.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVOffset.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		lblAudioMapping = new JLabel(language.getProperty("stereo"));
		lblAudioMapping.setName("lblAudioMapping");
		lblAudioMapping.setBackground(new Color(80, 80, 80));
		lblAudioMapping.setHorizontalAlignment(SwingConstants.CENTER);
		lblAudioMapping.setOpaque(true);
		lblAudioMapping.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblAudioMapping.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		lblAudioMapping.setSize(lblKbs.getLocation().x + lblKbs.getSize().width - 5 - 7 - (comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7) , 16);
		grpSetAudio.add(lblAudioMapping);
		
		lblAudioMapping.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblAudioMapping.getText().equals(language.getProperty("stereo"))) {
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										int sized = 128;
										do {
											i++;
											
											if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
												

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y - 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y - 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y - 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y - 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y - 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y - 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y - 1);
											}
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
					lblAudioMapping.setText(language.getProperty("stereo"));
										
					int sized = 74;
					if (grpSetAudio.getSize().height > sized)
					{
						Thread changeSize = new Thread(new Runnable() {
							@Override
							public void run() {
									try {
										int i = 128;
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
											{
												grpSetAudio.setSize(312, i);
												grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
												grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}
											else //Codecs de sortie
											{
												grpSetAudio.setSize(312, i);
												grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
												grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
												grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
												grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
												grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
												grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
												btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
											}

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
		lbl48k.setFont(new Font("Montserrat", Font.PLAIN, 12));
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
				if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"1536"}));
					comboAudioBitrate.setSelectedIndex(0);
					if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
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
			}
			
		});
		
		//Audio Mapping
		lblAudio1 = new JLabel("Audio 1" + language.getProperty("colon"));
		lblAudio1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio1.setBounds(17, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7, lblAudio1.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio1);
		
		comboAudio1 = new JComboBox<String>();
		comboAudio1.setName("comboAudio1");
		comboAudio1.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio1.setSelectedIndex(0);
		comboAudio1.setMaximumRowCount(20);
		comboAudio1.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio1.setEditable(false);
		comboAudio1.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
		grpSetAudio.add(comboAudio1);
		
		comboAudio1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio1.getSelectedIndex() == 8)
				{
					comboAudio2.setSelectedIndex(8);
					comboAudio3.setSelectedIndex(8);
					comboAudio4.setSelectedIndex(8);
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio2 = new JLabel("Audio 2" + language.getProperty("colon"));
		lblAudio2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio2.setBounds(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y, lblAudio2.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio2);
		
		comboAudio2 = new JComboBox<String>();
		comboAudio2.setName("comboAudio2");
		comboAudio2.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio2.setSelectedIndex(1);
		comboAudio2.setMaximumRowCount(20);
		comboAudio2.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio2.setEditable(false);
		comboAudio2.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
		grpSetAudio.add(comboAudio2);
		
		comboAudio2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio2.getSelectedIndex() == 8)
				{
					comboAudio3.setSelectedIndex(8);
					comboAudio4.setSelectedIndex(8);
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio3 = new JLabel("Audio 3" + language.getProperty("colon"));
		lblAudio3.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio3.setBounds(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2, lblAudio3.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio3);
		
		comboAudio3 = new JComboBox<String>();
		comboAudio3.setName("comboAudio3");
		comboAudio3.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio3.setSelectedIndex(2);
		comboAudio3.setMaximumRowCount(20);
		comboAudio3.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio3.setEditable(false);
		comboAudio3.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
		grpSetAudio.add(comboAudio3);
		
		comboAudio3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio3.getSelectedIndex() == 8)
				{
					comboAudio4.setSelectedIndex(8);
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio4 = new JLabel("Audio 4" + language.getProperty("colon"));
		lblAudio4.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio4.setBounds(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2, lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio4);
		
		comboAudio4 = new JComboBox<String>();
		comboAudio4.setName("comboAudio4");
		comboAudio4.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio4.setSelectedIndex(3);
		comboAudio4.setMaximumRowCount(20);
		comboAudio4.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio4.setEditable(false);
		comboAudio4.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
		grpSetAudio.add(comboAudio4);
		
		comboAudio4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio4.getSelectedIndex() == 8)
				{
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio5 = new JLabel("Audio 5" + language.getProperty("colon"));
		lblAudio5.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio5.setBounds(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2, lblAudio5.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio5);
		
		comboAudio5 = new JComboBox<String>();
		comboAudio5.setName("comboAudio5");
		comboAudio5.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio5.setSelectedIndex(4);
		comboAudio5.setMaximumRowCount(20);
		comboAudio5.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio5.setEditable(false);
		comboAudio5.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
		grpSetAudio.add(comboAudio5);
		
		comboAudio5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio5.getSelectedIndex() == 8)
				{
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio6 = new JLabel("Audio 6" + language.getProperty("colon"));
		lblAudio6.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio6.setBounds(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2, lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio6);
		
		comboAudio6 = new JComboBox<String>();
		comboAudio6.setName("comboAudio6");
		comboAudio6.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio6.setSelectedIndex(5);
		comboAudio6.setMaximumRowCount(20);
		comboAudio6.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio6.setEditable(false);
		comboAudio6.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
		grpSetAudio.add(comboAudio6);
		
		comboAudio6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio6.getSelectedIndex() == 8)
				{
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio7 = new JLabel("Audio 7" + language.getProperty("colon"));
		lblAudio7.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio7.setBounds(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2, lblAudio7.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio7);
		
		comboAudio7 = new JComboBox<String>();
		comboAudio7.setName("comboAudio7");
		comboAudio7.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio7.setSelectedIndex(6);
		comboAudio7.setMaximumRowCount(20);
		comboAudio7.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAudio7.setEditable(false);
		comboAudio7.setSize(comboAudioCodec.getWidth(), 16);
		comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
		grpSetAudio.add(comboAudio7);
		
		comboAudio7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio7.getSelectedIndex() == 8)
				{
					comboAudio8.setSelectedIndex(8);
				}
				
				if (lblAudioMapping.getText().equals("Multi"))
				{
					try {
						FFPROBE.setTailleH264();
					} catch (Exception e1) {}
				}
			}
			
		});
				
		lblAudio8 = new JLabel("Audio 8" + language.getProperty("colon"));
		lblAudio8.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudio8.setBounds(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2, lblAudio8.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio8);
		
		comboAudio8 = new JComboBox<String>();
		comboAudio8.setName("comboAudio8");
		comboAudio8.setModel(new DefaultComboBoxModel<String>(new String[] { "Audio 1", "Audio 2", "Audio 3", "Audio 4", "Audio 5", "Audio 6", "Audio 7", "Audio 8", language.getProperty("noAudio")}));
		comboAudio8.setSelectedIndex(7);
		comboAudio8.setMaximumRowCount(20);
		comboAudio8.setFont(new Font("FreeSans", Font.PLAIN, 10));
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
		grpAudio.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpAudio") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpAudio.setBackground(new Color(50, 50, 50));
		grpAudio.setBounds(334, 343, 312, 116);
		frame.getContentPane().add(grpAudio);
		
		caseMixAudio = new JRadioButton(language.getProperty("caseMixAudio"));
		caseMixAudio.setName("caseMixAudio");
		caseMixAudio.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseMixAudio.setBackground(new Color(50, 50, 50));
		caseMixAudio.setBounds(7, 16, caseMixAudio.getPreferredSize().width, 23);
		caseMixAudio.setToolTipText(language.getProperty("tooltipMixAudio"));
		grpAudio.add(caseMixAudio);

		caseMixAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseMixAudio.isSelected())
				{
					caseSplitAudio.setSelected(false);
					
					if (lblMix.getText().equals("5.1"))
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>SL<br>SR</html>");
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
		lblMix.setFont(new Font("Montserrat", Font.PLAIN, 12));
		lblMix.setBounds(caseMixAudio.getLocation().x + caseMixAudio.getWidth() + 5, caseMixAudio.getY() + 3, 46, 16);
		grpAudio.add(lblMix);
		
		lblMix.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblMix.getText().equals(language.getProperty("stereo")))
				{
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>SL<br>SR</html>");
					
					lblMix.setText("5.1");
				}
				else
				{				
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FL<br>FR<br>...<br>...</html>");
					
					lblMix.setText(language.getProperty("stereo"));
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
		caseSplitAudio.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseSplitAudio.setBackground(new Color(50, 50, 50));
		caseSplitAudio.setBounds(7, caseMixAudio.getY() +  caseMixAudio.getHeight(), caseSplitAudio.getPreferredSize().width, 23);
		grpAudio.add(caseSplitAudio);

		caseSplitAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseSplitAudio.isSelected())
					caseMixAudio.setSelected(false);
			}

		});

		lblSplit = new JLabel("Mono");
		lblSplit.setName("lblSplit");
		lblSplit.setBackground(new Color(80, 80, 80));
		lblSplit.setHorizontalAlignment(SwingConstants.CENTER);
		lblSplit.setOpaque(true);
		lblSplit.setFont(new Font("Montserrat", Font.PLAIN, 12));
		lblSplit.setBounds(caseSplitAudio.getLocation().x + caseSplitAudio.getWidth() + 5, caseSplitAudio.getY() + 3, 46, 16);
		grpAudio.add(lblSplit);

		lblSplit.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblSplit.getText().equals("Mono"))
					lblSplit.setText(language.getProperty("stereo"));
				else
					lblSplit.setText("Mono");
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
		caseSampleRate.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseSampleRate.setBackground(new Color(50, 50, 50));
		caseSampleRate.setBounds(7, caseSplitAudio.getY() +caseSplitAudio.getHeight(), caseSampleRate.getPreferredSize().width, 23);
		grpAudio.add(caseSampleRate);		
		
		caseConvertAudioFramerate = new JRadioButton(language.getProperty("caseConvertAudioFramerate"));
		caseConvertAudioFramerate.setName("caseConvertAudioFramerate");
		caseConvertAudioFramerate.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseConvertAudioFramerate.setBackground(new Color(50, 50, 50));
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
		comboAudioIn.setFont(new Font("FreeSans", Font.PLAIN, 11));
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
		lblFromTo.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblFromTo.setSize(lblFromTo.getPreferredSize().width, 16);
		lblFromTo.setLocation(comboAudioIn.getLocation().x + comboAudioIn.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 3);
		grpAudio.add(lblFromTo);
		
		comboAudioOut = new JComboBox<String>();
		comboAudioOut.setName("comboAudioOut");
		comboAudioOut.setEnabled(false);
		comboAudioOut.setModel(new DefaultComboBoxModel<String>(
				new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboAudioOut.setSelectedIndex(1);
		comboAudioOut.setMaximumRowCount(20);
		comboAudioOut.setFont(new Font("FreeSans", Font.PLAIN, 11));
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
		
		lblAudioIs = new JLabel("i/s");
		lblAudioIs.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblAudioIs.setSize(lblAudioIs.getPreferredSize().width, 16);
		lblAudioIs.setLocation(comboAudioOut.getLocation().x + comboAudioOut.getWidth() + 7, caseConvertAudioFramerate.getLocation().y + 3);
		grpAudio.add(lblAudioIs);
	}

	private void grpCorrections() {
		grpCorrections = new JPanel();
		grpCorrections.setLayout(null);
		grpCorrections.setVisible(false);
		grpCorrections.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1), language.getProperty("grpCorrections") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;
										grpCorrections.setSize(312, i);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										grpCorrections.setSize(312, i);
										grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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
		caseDeflicker.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseDeflicker.setBackground(new Color(50, 50, 50));
		caseDeflicker.setSize(caseDeflicker.getPreferredSize().width, 23);		
		
		caseBanding = new JRadioButton(language.getProperty("caseBanding"));
		caseBanding.setName("caseBanding");
		caseBanding.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseBanding.setBackground(new Color(50, 50, 50));
		caseBanding.setSize(caseBanding.getPreferredSize().width + 10, 23); 
				
		caseDetails = new JRadioButton(language.getProperty("caseDetails"));
		caseDetails.setName("caseDetails");
		caseDetails.setToolTipText(language.getProperty("tooltipDetails"));
		caseDetails.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseDetails.setBackground(new Color(50, 50, 50));
		caseDetails.setSize(caseDetails.getPreferredSize().width + 10, 23);

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
		sliderDetails.setSize(110, 22);

		iconTVDetails = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVDetails.setToolTipText(language.getProperty("preview"));
		iconTVDetails.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVDetails.setSize(16, 16);

		iconTVDetails.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				float value = 0 - (sliderDetails.getValue() / 10);

				String flags = "";
				if (value > 0)
					flags = " -sws_flags lanczos";

				String smartblur = "smartblur=" + "1.0:" + value;

				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0) {
					// Fichiers sélectionnés ?
					if (listeDeFichiers.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = '"' + f.toString() + '"';
									break;
								}
							}
						} else
							file = '"' + listeDeFichiers.getSelectedValue().toString() + '"';

					} else
						file = '"' + liste.firstElement() + '"';
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs" + flags + " -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -i " + fileOut + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ smartblur + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
					}
					else
					{						
						FFPLAY.run("-fs" + flags + " -i " + file + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ smartblur + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
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
				iconTVDetails.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVDetails.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
		caseBruit.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseBruit.setBackground(new Color(50, 50, 50));
		caseBruit.setSize(caseBruit.getPreferredSize().width + 10, 23);

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
		sliderBruit.setSize(110, 22);

		iconTVBruit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVBruit.setToolTipText(language.getProperty("preview"));
		iconTVBruit.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVBruit.setSize(16, 16);

		iconTVBruit.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int value = sliderBruit.getValue();
				String hqdn3d = "hqdn3d=" + value + ":" + value + ":" + value + ":" + value;
				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0) {
					// Fichiers sélectionnés ?
					if (listeDeFichiers.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = '"' + f.toString() + '"';
									break;
								}
							}
						} else
							file = '"' + listeDeFichiers.getSelectedValue().toString() + '"';

					} else
						file = '"' + liste.firstElement() + '"';
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -i " + fileOut + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ hqdn3d + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
					}
					else
					{						
						FFPLAY.run("-fs -i " + file + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ hqdn3d + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
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
				iconTVBruit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVBruit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
		caseExposure.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseExposure.setBackground(new Color(50, 50, 50));
		caseExposure.setSize(caseExposure.getPreferredSize().width + 15, 23);

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
		sliderExposure.setSize(110, 22);

		iconTVExposure = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVExposure.setToolTipText(language.getProperty("preview"));
		iconTVExposure.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVExposure.setSize(16, 16);

		iconTVExposure.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				int value = sliderExposure.getValue();
				String deflicker = "deflicker=s=" + Math.ceil((128 * value) / 100 + 1);
				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0) {
					// Fichiers sélectionnés ?
					if (listeDeFichiers.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = '"' + f.toString() + '"';
									break;
								}
							}
						} else
							file = '"' + listeDeFichiers.getSelectedValue().toString() + '"';

					} else
						file = '"' + liste.firstElement() + '"';
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -i " + fileOut + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ deflicker + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
					}
					else
					{						
						FFPLAY.run("-fs -i " + file + " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"
								+ deflicker + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"');
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
				iconTVExposure.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVExposure.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
		grpTransitions.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1), language.getProperty("grpTransitions") + " ", 0,
				0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;
										grpTransitions.setSize(312, i);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										grpTransitions.setSize(312, i);
										grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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
		caseVideoFadeIn.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseVideoFadeIn.setBackground(new Color(50, 50, 50));
		caseVideoFadeIn.setBounds(7, 16, caseVideoFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeIn);
				
		caseVideoFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseVideoFadeIn.isSelected())
					spinnerVideoFadeIn.setEnabled(true);
				else
					spinnerVideoFadeIn.setEnabled(false);				
			}
			
		});
		
		spinnerVideoFadeIn = new JSpinner(new SpinnerNumberModel(25, 1, 999, 1));
		spinnerVideoFadeIn.setName("spinnerVideoFadeIn");
		spinnerVideoFadeIn.setEnabled(false);
		spinnerVideoFadeIn.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 12, caseVideoFadeIn.getLocation().y + 3, 41, 16);
		grpTransitions.add(spinnerVideoFadeIn);
		
		JLabel videoInFrames = new JLabel(language.getProperty("lblFrames"));
		if (getLanguage.equals("Italiano"))
			videoInFrames.setText("i");
		videoInFrames.setFont(new Font("FreeSans", Font.PLAIN, 12));
		videoInFrames.setBounds(spinnerVideoFadeIn.getLocation().x + spinnerVideoFadeIn.getWidth() + 4, spinnerVideoFadeIn.getY(), videoInFrames.getPreferredSize().width, 16);
		grpTransitions.add(videoInFrames);
				
		lblFadeInColor = new JLabel(language.getProperty("black"));
		lblFadeInColor.setName("lblFadeInColor");
		lblFadeInColor.setBackground(new Color(80, 80, 80));
		lblFadeInColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeInColor.setOpaque(true);
		lblFadeInColor.setFont(new Font("Montserrat", Font.PLAIN, 12));
		lblFadeInColor.setSize(55, 16);
		lblFadeInColor.setLocation(videoInFrames.getLocation().x + videoInFrames.getWidth() + 7, spinnerVideoFadeIn.getY() + 1);
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
		caseAudioFadeIn.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAudioFadeIn.setBackground(new Color(50, 50, 50));
		caseAudioFadeIn.setBounds(7, caseVideoFadeIn.getY() + 17, caseAudioFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeIn);
			
		caseAudioFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAudioFadeIn.isSelected())
					spinnerAudioFadeIn.setEnabled(true);
				else
					spinnerAudioFadeIn.setEnabled(false);				
			}
			
		});
		
		spinnerAudioFadeIn = new JSpinner(new SpinnerNumberModel(25, 1, 999, 1));
		spinnerAudioFadeIn.setName("spinnerAudioFadeIn");
		spinnerAudioFadeIn.setEnabled(false);
		spinnerAudioFadeIn.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerAudioFadeIn.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeIn.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeIn);
		
		JLabel audioInFrames = new JLabel(language.getProperty("lblFrames"));
		if (getLanguage.equals("Italiano"))
			audioInFrames.setText("i");
		audioInFrames.setFont(new Font("FreeSans", Font.PLAIN, 12));
		audioInFrames.setBounds(spinnerAudioFadeIn.getLocation().x + spinnerAudioFadeIn.getWidth() + 4, spinnerAudioFadeIn.getY(), audioInFrames.getPreferredSize().width, 16);
		grpTransitions.add(audioInFrames);
		
		JLabel iconFadeIn = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconFadeIn.setToolTipText(language.getProperty("preview"));
		iconFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		iconFadeIn.setBounds(lblFadeInColor.getX() + lblFadeInColor.getWidth() + 7, lblFadeInColor.getY(), 16, 16);
		grpTransitions.add(iconFadeIn);

		iconFadeIn.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0) 
				{
					if (scanIsRunning)
					{
						File dir = new File(Shutter.liste.firstElement());
						for (File f : dir.listFiles()) {
							if (f.isHidden() == false && f.isFile()) {
								file = f.toString();
								break;
							}
						}
					} 
					else
						file = liste.firstElement();
					
					FFPROBE.Data(file);
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);

					long videoInValue = (long) (Integer.parseInt(((JSpinner.DefaultEditor)spinnerVideoFadeIn.getEditor()).getTextField().getText()) * ((float) 1000 / FFPROBE.currentFPS));
					long audioInValue = (long) (Integer.parseInt(((JSpinner.DefaultEditor)spinnerAudioFadeIn.getEditor()).getTextField().getText()) * ((float) 1000 / FFPROBE.currentFPS));
					
					long duration = (long) videoInValue + 2000;
					if (audioInValue > videoInValue && caseAudioFadeIn.isSelected())
						duration = (long) audioInValue + 2000;
					
					String color = "black";
					if (lblFadeInColor.getText().equals(language.getProperty("white")))
						color = "white";
					
					String videoFade = "";
					if (caseVideoFadeIn.isSelected())
						videoFade = " -vf " + '"' + "fade=in:d=" + videoInValue + "ms:color=" + color + '"';
						
					String audioFade = "";
					if (caseAudioFadeIn.isSelected())
						audioFade = " -af " + '"' + "afade=in:d=" + audioInValue + "ms" + '"';
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs -autoexit -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -i " + '"' + fileOut + '"' + videoFade + audioFade + " -t " + duration + "ms");
					}
					else
					{
						String fileOut = file;
						if (listeDeFichiers.getSelectedIndices().length > 0)
							fileOut = listeDeFichiers.getSelectedValue().toString();
						
						FFPLAY.run("-fs -autoexit -i " + '"' + fileOut + '"' + videoFade + audioFade + " -t " + duration + "ms");
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
				iconFadeIn.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconFadeIn.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		//Output	
		caseVideoFadeOut = new JRadioButton(language.getProperty("lblVideoFadeOut"));
		caseVideoFadeOut.setName("caseVideoFadeOut");
		caseVideoFadeOut.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseVideoFadeOut.setBackground(new Color(50, 50, 50));
		caseVideoFadeOut.setBounds(7, caseAudioFadeIn.getY() + caseAudioFadeIn.getHeight(), caseVideoFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeOut);
		
		caseVideoFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseVideoFadeOut.isSelected())
					spinnerVideoFadeOut.setEnabled(true);
				else
					spinnerVideoFadeOut.setEnabled(false);				
			}
			
		});
				
		spinnerVideoFadeOut = new JSpinner(new SpinnerNumberModel(25, 1, 999, 1));
		spinnerVideoFadeOut.setName("spinnerVideoFadeOut");
		spinnerVideoFadeOut.setEnabled(false);
		spinnerVideoFadeOut.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerVideoFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseVideoFadeOut.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerVideoFadeOut);
				
		JLabel videoOutFrames = new JLabel(language.getProperty("lblFrames"));
		if (getLanguage.equals("Italiano"))
			videoOutFrames.setText("i");
		videoOutFrames.setFont(new Font("FreeSans", Font.PLAIN, 12));
		videoOutFrames.setBounds(spinnerVideoFadeOut.getLocation().x + spinnerVideoFadeOut.getWidth() + 4, spinnerVideoFadeOut.getY(), videoOutFrames.getPreferredSize().width, 16);
		grpTransitions.add(videoOutFrames);
		
		lblFadeOutColor = new JLabel(language.getProperty("black"));
		lblFadeOutColor.setName("lblFadeOutColor");
		lblFadeOutColor.setBackground(new Color(80, 80, 80));
		lblFadeOutColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeOutColor.setOpaque(true);
		lblFadeOutColor.setFont(new Font("Montserrat", Font.PLAIN, 12));
		lblFadeOutColor.setSize(55, 16);
		lblFadeOutColor.setLocation(videoOutFrames.getLocation().x + videoOutFrames.getWidth() + 7, spinnerVideoFadeOut.getY() + 1);
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
		caseAudioFadeOut.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAudioFadeOut.setBackground(new Color(50, 50, 50));
		caseAudioFadeOut.setBounds(7, caseVideoFadeOut.getY() + 17, caseAudioFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeOut);
		
		caseAudioFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAudioFadeOut.isSelected())
					spinnerAudioFadeOut.setEnabled(true);
				else
					spinnerAudioFadeOut.setEnabled(false);				
			}
			
		});
		
		spinnerAudioFadeOut = new JSpinner(new SpinnerNumberModel(25, 1, null, 1));
		spinnerAudioFadeOut.setName("spinnerAudioFadeOut");
		spinnerAudioFadeOut.setEnabled(false);
		spinnerAudioFadeOut.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerAudioFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeOut.getLocation().y + 3, spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeOut);
		
		spinnerAudioFadeOut.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				spinnerAudioFadeOut.setValue(spinnerAudioFadeOut);
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		});
		
		JLabel audioOutFrames = new JLabel(language.getProperty("lblFrames"));
		if (getLanguage.equals("Italiano"))
			audioOutFrames.setText("i");
		audioOutFrames.setFont(new Font("FreeSans", Font.PLAIN, 12));
		audioOutFrames.setBounds(spinnerAudioFadeOut.getLocation().x + spinnerAudioFadeOut.getWidth() + 4, spinnerAudioFadeOut.getY(), audioOutFrames.getPreferredSize().width, 16);
		grpTransitions.add(audioOutFrames);
		
		JLabel iconFadeOut = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconFadeOut.setToolTipText(language.getProperty("preview"));
		iconFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		iconFadeOut.setBounds(lblFadeOutColor.getX() + lblFadeOutColor.getWidth() + 7, lblFadeOutColor.getY(), 16, 16);
		grpTransitions.add(iconFadeOut);
		
		iconFadeOut.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				String file = "";

				// Définition de la taille
				if (liste.getSize() > 0) 
				{
					if (scanIsRunning)
					{
						File dir = new File(Shutter.liste.firstElement());
						for (File f : dir.listFiles()) {
							if (f.isHidden() == false && f.isFile()) {
								file = f.toString();
								break;
							}
						}
					} 
					else
						file = liste.firstElement();
					
					FFPROBE.Data(file);
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);

					
					long videoOutValue = (long) (Integer.parseInt(((JSpinner.DefaultEditor)spinnerVideoFadeOut.getEditor()).getTextField().getText()) * ((float) 1000 / FFPROBE.currentFPS));
					long audioOutValue = (long) (Integer.parseInt(((JSpinner.DefaultEditor)spinnerAudioFadeOut.getEditor()).getTextField().getText()) * ((float) 1000 / FFPROBE.currentFPS));
					
					long forward = (long) FFPROBE.dureeTotale - ((long) videoOutValue + 2000);
					if (audioOutValue > videoOutValue && caseAudioFadeOut.isSelected())
						forward = (long) FFPROBE.dureeTotale - ((long) audioOutValue + 2000);
					
					String color = "black";
					if (lblFadeOutColor.getText().equals(language.getProperty("white")))
						color = "white";
					
					long videoStart = (long) FFPROBE.dureeTotale - videoOutValue;
					long audioStart =  (long) FFPROBE.dureeTotale - audioOutValue;
					
					String videoFade = "";
					if (caseVideoFadeOut.isSelected())
						videoFade = " -vf " + '"' + "fade=out:st=" + videoStart + "ms:d=" + videoOutValue + "ms:color=" + color + '"';
						
					String audioFade = "";
					if (caseAudioFadeOut.isSelected())
						audioFade = " -af " + '"' + "afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms" + '"';
					
					if (caseActiverSequence.isSelected())
					{					
						String extension = file.substring(file.lastIndexOf("."));
						
						int n = 0;
						do {
							n ++;				
						} while (file.substring(file.lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
						
						int nombre = (n - 1);
						String fileOut = file.substring(0, file.lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension;	
						
						FFPLAY.run("-fs -autoexit -start_number " + file.toString().substring(file.lastIndexOf(".") - n + 1).replace(extension, "")
								+ " -ss " + forward + "ms -i " + '"' + fileOut + '"' + videoFade + audioFade);
					}
					else
					{
						String fileOut = file;
						if (listeDeFichiers.getSelectedIndices().length > 0)
							fileOut = listeDeFichiers.getSelectedValue().toString();
						
						FFPLAY.run("-fs -autoexit -ss " + forward + "ms -i " + '"' + fileOut + '"' + videoFade + audioFade);
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
				iconFadeOut.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconFadeOut.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
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
		grpAdvanced.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1), language.getProperty("grpAdvanced") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i++;
										grpAdvanced.setSize(312, i);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
	
										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y - 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y - 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y - 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y - 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y - 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y - 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y - 1);
										}
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
									long start = (System.currentTimeMillis());
									int fps = 0;
									int sleep = 1;
									do {
										i--;
										grpAdvanced.setSize(312, i);
										btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
										
										Thread.sleep(sleep);
										
										// Permet de définir la vitesse
										if ((System.currentTimeMillis() - start) < 350)
											fps += 1;
										else {
											if (fps > 30)
												sleep = (int) (sleep / (fps / 25));
											else if (fps < 20)
												sleep = (int) (sleep * (fps / 25));

											start = System.currentTimeMillis();
											fps = 0;
										}

										if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
												 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
												 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
											grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
													grpSequenceImage.getLocation().y + 1);
											grpLUTs.setLocation(grpLUTs.getLocation().x,
													grpLUTs.getLocation().y + 1);
											grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
													grpFiltreImage.getLocation().y + 1);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + 1);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + 1);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + 1);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + 1);
										}
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

		caseForcerProgressif = new JRadioButton(language.getProperty("caseForcerProgressif"));
		caseForcerProgressif.setName("caseForcerProgressif");
		caseForcerProgressif.setToolTipText(language.getProperty("tooltipProgressif"));
		caseForcerProgressif.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerProgressif.setBackground(new Color(50, 50, 50));
		caseForcerProgressif.setSize(141, 23);

		caseForcerEntrelacement = new JRadioButton(language.getProperty("caseForcerEntrelacement"));
		caseForcerEntrelacement.setName("caseForcerEntrelacement");
		caseForcerEntrelacement.setToolTipText(language.getProperty("tooltipEntrelacement"));
		caseForcerEntrelacement.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerEntrelacement.setBackground(new Color(50, 50, 50));
		caseForcerEntrelacement.setSize(153, 23);
		
		caseForcerInversion = new JRadioButton(language.getProperty("caseForcerInversion"));
		caseForcerInversion.setName("caseForcerInversion");
		caseForcerInversion.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerInversion.setBackground(new Color(50, 50, 50));
		caseForcerInversion.setSize(caseForcerInversion.getPreferredSize().width, 23);

		caseForcerDesentrelacement = new JRadioButton(language.getProperty("caseForcerDesentrelacement"));
		caseForcerDesentrelacement.setName("caseForcerDesentrelacement");
		caseForcerDesentrelacement.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerDesentrelacement.setBackground(new Color(50, 50, 50));
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
		comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "yadif", "bwdif", "detelecine"}));
		comboForcerDesentrelacement.setSelectedIndex(0);
		comboForcerDesentrelacement.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForcerDesentrelacement.setEditable(false);
		comboForcerDesentrelacement.setSize(comboForcerDesentrelacement.getPreferredSize().width, 18);
				
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
		lblTFF.setFont(new Font("Montserrat", Font.PLAIN, 12));
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

		caseForcerResolution = new JRadioButton(language.getProperty("caseForcerResolution"));
		caseForcerResolution.setName("caseForcerResolution");
		caseForcerResolution.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerResolution.setBackground(new Color(50, 50, 50));
		caseForcerResolution.setSize(caseForcerResolution.getPreferredSize().width, 23);
		caseForcerResolution.setToolTipText(language.getProperty("tooltipResolution"));

		caseForcerResolution.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
						|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
						|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100"))
				{
					if (lblResolution.getItemCount() > 2)
					{
						lblResolution.setModel(new DefaultComboBoxModel<String>(new String[] { "1280x720", "1920x1080" }));
						lblResolution.setSelectedIndex(1);
					}			
				}
				else
				{
					if (lblResolution.getItemCount() < 4)
					{
						lblResolution.setModel(new DefaultComboBoxModel<String>(new String[] { "1280x720", "1920x1080", "3840x2160", "4096x2160" }));
						lblResolution.setSelectedIndex(1);
					}
				}
				
				changementDeFiltre();
				
				if (caseForcerResolution.isSelected())
					lblResolution.setEnabled(true);
				else
					lblResolution.setEnabled(false);
			}

		});

		lblResolution = new JComboBox<String>();
		lblResolution.setName("lblResolution");
		lblResolution.setMaximumRowCount(20);
		lblResolution.setModel(new DefaultComboBoxModel<String>(new String[] { "1280x720", "1920x1080", "3840x2160", "4096x2160" }));
		lblResolution.setSelectedIndex(1);
		lblResolution.setFont(new Font("FreeSans", Font.PLAIN, 11));
		lblResolution.setEditable(true);
		lblResolution.setSize(74, 16);

		lblResolution.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				changementDeFiltre();
			}

		});
		
		caseForcerDAR = new JRadioButton(language.getProperty("caseForcerDAR"));
		caseForcerDAR.setName("caseForcerDAR");
		caseForcerDAR.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcerDAR.setBackground(new Color(50, 50, 50));
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
		caseLimiter.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseLimiter.setBackground(new Color(50, 50, 50));
		caseLimiter.setSize(caseLimiter.getPreferredSize().width, 23);

		comboDAR = new JComboBox<String>();
		comboDAR.setName("comboDAR");
		comboDAR.setMaximumRowCount(20);
		comboDAR.setModel(new DefaultComboBoxModel<String>(new String[] { "1:1", "4:3", "16:9", "21:9"}));
		comboDAR.setSelectedIndex(2);
		comboDAR.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboDAR.setEditable(true);
		comboDAR.setSize(48, 16);
		
		caseCreateTree = new JRadioButton(language.getProperty("caseCreateTree"));
		caseCreateTree.setName("caseCreateTree");
		caseCreateTree.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseCreateTree.setBackground(new Color(50, 50, 50));
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
		
		caseCreateOPATOM = new JRadioButton(language.getProperty("caseCreateOPATOM"));
		caseCreateOPATOM.setName("caseCreateOPATOM");
		caseCreateOPATOM.setToolTipText(language.getProperty("tooltipCreateOpatom"));
		caseCreateOPATOM.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseCreateOPATOM.setBackground(new Color(50, 50, 50));
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
		lblCreateOPATOM.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblCreateOPATOM.setBackground(new Color(50, 50, 50));
		lblCreateOPATOM.setSize((int) lblCreateOPATOM.getPreferredSize().getWidth(), 23);
		
		lblOPATOM = new JLabel("OP-Atom");
		lblOPATOM.setName("lblOPATOM");
		lblOPATOM.setBackground(new Color(80, 80, 80));
		lblOPATOM.setHorizontalAlignment(SwingConstants.CENTER);
		lblOPATOM.setOpaque(true);
		lblOPATOM.setFont(new Font("Montserrat", Font.PLAIN, 12));
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
		caseOPATOM.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseOPATOM.setBackground(new Color(50, 50, 50));
		caseOPATOM.setSize(caseOPATOM.getPreferredSize().width, 23);

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
				lblFichiers.setText(Utils.nombreDeFichiers());
			}

		});

		caseSubtitles = new JRadioButton(language.getProperty("caseSubtitles"));
		caseSubtitles.setName("caseSubtitles");
		caseSubtitles.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseSubtitles.setBackground(new Color(50, 50, 50));
		caseSubtitles.setSize(caseSubtitles.getPreferredSize().width, 23);

		caseSubtitles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSubtitles.isSelected()) {
					if (liste.getSize() == 0) {
						JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
								language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseSubtitles.setSelected(false);
					} 
					else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionSubtitles")))
					{
						if (System.getProperty("os.name").contains("Mac")) {
							String PathToSubtitles = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToSubtitles = PathToSubtitles.substring(0, PathToSubtitles.length() - 1);
							PathToSubtitles = PathToSubtitles.substring(0, (int) (PathToSubtitles.lastIndexOf("/"))).replace("%20", " ") + "/"  + Subtitles.srt.getName();
							subtitlesFile = new File(PathToSubtitles);
						} 
						else if (System.getProperty("os.name").contains("Windows"))
							subtitlesFile = new File(Subtitles.srt.getName());
						else //Linux
							subtitlesFile = new File(dirTemp + Subtitles.srt.getName());
									            
						Object[] options = {language.getProperty("subtitlesBurn"), language.getProperty("subtitlesEmbed")};
						
						int sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSubsIntegration"), language.getProperty("caseSubtitles"),
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							    options,
							    options[0]);
			            
						if (sub == 0)
						{
							subtitlesBurn = true;
							SubtitlesWindow.subtitlesFile = Subtitles.srt.toString();				            
				            writeSub(SubtitlesWindow.subtitlesFile);
						}
						else
						{
							subtitlesBurn = false;
							subtitlesFile = new File(Subtitles.srt.toString());

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
										String cmd = " -c copy -c:s mov_text -map v? -map a? -map 1:s -y ";
										
										if (extension.equals(".mkv"))
											cmd = " -c copy -c:s srt -map v? -map a? -map 1:s -y ";							
										
										FFMPEG.run(" -i " + '"' + fileIn + '"' + " -i " + '"' + subtitlesFile + '"' + cmd + '"'  + fileOut + '"');	
										
										lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
										lblEncodageEnCours.setText(fileIn.getName());
										
										//Attente de la fin de FFMPEG
										do {
											Thread.sleep(100);
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
											lblTermine.setText(Utils.fichiersTermines(1));
										
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
										FinDeFonction();
									}
									
								}						
							});
							copySRT.start();	
						}
					}
					else
					{
						FileDialog dialog = new FileDialog(frame, language.getProperty("chooseSubtitles"),
								FileDialog.LOAD);
						dialog.setDirectory(new File(liste.elementAt(0).toString()).getParent());
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						dialog.setMultipleMode(false);
						dialog.setVisible(true);

						if (dialog.getFile() != null) {
							if (dialog.getFile().substring(dialog.getFile().lastIndexOf(".")).equals(".srt"))
							{
									if (System.getProperty("os.name").contains("Mac")) {
										String PathToSubtitles = Shutter.class.getProtectionDomain().getCodeSource().getLocation()
												.getPath();
										PathToSubtitles = PathToSubtitles.substring(0, PathToSubtitles.length() - 1);
										PathToSubtitles = PathToSubtitles.substring(0, (int) (PathToSubtitles.lastIndexOf("/")))
												.replace("%20", " ") + "/"  + dialog.getFile();
										subtitlesFile = new File(PathToSubtitles);
									}
									else if (System.getProperty("os.name").contains("Windows"))
										subtitlesFile = new File(dialog.getFile());
									else //Linux
										subtitlesFile = new File(dirTemp + dialog.getFile());
									
									Object[] options = {language.getProperty("subtitlesBurn"), language.getProperty("subtitlesEmbed")};
									
									int sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSubsIntegration"), language.getProperty("caseSubtitles"),
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										    options,
										    options[0]);
										
									if (sub == 0)
									{
										subtitlesBurn = true;
										SubtitlesWindow.subtitlesFile = dialog.getDirectory() + dialog.getFile().toString();									            
										writeSub(SubtitlesWindow.subtitlesFile);
									}
									else
									{
										subtitlesBurn = false;
										subtitlesFile = new File(dialog.getDirectory() + dialog.getFile().toString());
									}
							}
							else {
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
			
			private void writeSub(String srt) 
			{
				
				try {
					BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(srt),  StandardCharsets.UTF_8);
		            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(subtitlesFile.toString()),  StandardCharsets.UTF_8);

		            String line;
		            //int spinnerValue = 0;
		            boolean stop = false;
		            while((line = bufferedReader.readLine()) != null) {
		            	if (line.matches("[0-9]+"))
		            	{
		            		if (stop == false)
		            		{
			            		bufferedWriter.write(line);
			            		bufferedWriter.newLine();
		            		}
		            		//spinnerValue ++;
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
		            		//Fichier avec BOM
		            		if (line.contains("1") && line.length() == 2)
		            		{
		            			bufferedWriter.write("1");
			            		bufferedWriter.newLine();
		            		}
		            		else
		            		{		            		
			            		if (stop == false)
			            		{
			            			if (SubtitlesWindow.frame != null && SubtitlesWindow.lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
			            				bufferedWriter.write(" \\h" + line + " \\h");
			            			else
			            				bufferedWriter.write(line);
			            			
			            			bufferedWriter.newLine();
			            		}
		            		}
		            	} 
		            	else if (line.isEmpty())						            		
		            		stop = true;

		            }   

		            bufferedReader.close();  
		            bufferedWriter.close();
		            
		            //SubtitlesWindow.spinnerSubtitle.setModel(new SpinnerNumberModel(1, 1, spinnerValue, 1));
		            
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
								Thread.sleep(100);
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
					caseSubtitles.setSelected(false);
				}
			}

		});

		caseStabilisation = new JRadioButton(language.getProperty("caseStabilisation"));
		caseStabilisation.setName("caseStabilisation");
		caseStabilisation.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseStabilisation.setBackground(new Color(50, 50, 50));
		caseStabilisation.setSize(caseStabilisation.getPreferredSize().width, 23);

		caseConform = new JRadioButton(language.getProperty("caseConform"));
		caseConform.setName("caseConform");
		caseConform.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseConform.setBackground(new Color(50, 50, 50));
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

		comboConform = new JComboBox<String>();
		comboConform.setName("comboConform");
		comboConform.setEnabled(false);
		comboConform.setModel(new DefaultComboBoxModel<String>(
				new String[] {language.getProperty("conformByReverse"), language.getProperty("conformBySpeed"), language.getProperty("conformByDrop"), language.getProperty("conformByBlending"), language.getProperty("conformByInterpolation"), language.getProperty("conformBySlowMotion") }));
		comboConform.setSelectedIndex(3);
		comboConform.setMaximumRowCount(20);
		comboConform.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboConform.setEditable(false);
		comboConform.setSize(100, 16);

		lblToConform = new JLabel(language.getProperty("at") + language.getProperty("colon"));
		lblToConform.setFont(new Font("FreeSans", Font.PLAIN, 12));
		if (getLanguage.equals("Français"))
			lblToConform.setSize(13, 16);
		else
			lblToConform.setSize(16, 16);

		comboFPS = new JComboBox<String>();
		comboFPS.setName("comboFPS");
		comboFPS.setEnabled(false);
		comboFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,976", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		comboFPS.setSelectedIndex(2);
		comboFPS.setMaximumRowCount(20);
		comboFPS.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboFPS.setEditable(true);
		comboFPS.setSize(50, 16);
		
		lblIsConform = new JLabel("i/s");
		lblIsConform.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		caseAccel.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAccel.setBackground(new Color(50, 50, 50));
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
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_qsv -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Intel Quick Sync");
							}
							else if (System.getProperty("os.name").contains("Linux"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_vaapi -f null -");
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
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Nvidia NVENC");
		
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_qsv -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Intel Quick Sync");
								
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_amf -f null -" + '"');
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("AMD AMF Encoder");
							}	
							else if (System.getProperty("os.name").contains("Linux"))
							{
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("Nvidia NVENC");
										
								FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_vaapi -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)
									graphicsAccel.add("VAAPI");								
								
								if (comboFonctions.getSelectedItem().toString().equals("H.264"))
								{
									FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_v4l2m2m -f null -");
									do {
										Thread.sleep(10);
									} while (FFMPEG.runProcess.isAlive());
			
									if (FFMPEG.error == false)
										graphicsAccel.add("V4L2 M2M");	
									
									FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_omx -f null -");
									do {
										Thread.sleep(10);
									} while (FFMPEG.runProcess.isAlive());
			
									if (FFMPEG.error == false)
										graphicsAccel.add("OpenMAX");
								}
							}
							else//Accélération graphique Mac
							{
								/*FFMPEG.run("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_videotoolbox -f null -");
								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());
		
								if (FFMPEG.error == false)*/
								
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
							caseForcePreset.setSelected(false);
							caseForcePreset.setEnabled(false);
							comboForcePreset.setEnabled(false);
							caseForceTune.setSelected(false);
							caseForceTune.setEnabled(false);
							comboForceTune.setEnabled(false);
							caseForceOutput.setSelected(false);
							caseForceOutput.setEnabled(false);
							case2pass.setSelected(false);
							case2pass.setEnabled(false);
							
							if (lblVBR.getText().equals("CBR"))
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
			}

		});

		comboAccel = new JComboBox<String>();
		comboAccel.setName("comboAccel");
		comboAccel.setEnabled(false);
		comboAccel.setMaximumRowCount(20);
		comboAccel.setModel(new DefaultComboBoxModel<String>(new String[] { "Nvidia NVENC", "Intel Quick Sync" }));
		comboAccel.setSelectedIndex(0);
		comboAccel.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAccel.setEditable(false);
		comboAccel.setSize(124, 16);
		
		comboAccel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAccel.isSelected()) {
					caseForcerEntrelacement.setSelected(false);
					caseForcerEntrelacement.setEnabled(false);	
					caseForcePreset.setSelected(false);
					caseForcePreset.setEnabled(false);
					comboForcePreset.setEnabled(false);
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
		caseForceOutput.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForceOutput.setBackground(new Color(50, 50, 50));
		caseForceOutput.setSize(caseForceOutput.getPreferredSize().width, 23);

		lblNiveaux = new JLabel("0-255");
		lblNiveaux.setName("lblNiveaux");
		lblNiveaux.setBackground(new Color(80, 80, 80));
		lblNiveaux.setHorizontalAlignment(SwingConstants.CENTER);
		lblNiveaux.setOpaque(true);
		lblNiveaux.setFont(new Font("Montserrat", Font.PLAIN, 12));
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
		caseFastStart.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseFastStart.setBackground(new Color(50, 50, 50));
		caseFastStart.setSize(caseFastStart.getPreferredSize().width, 23);
		
		caseAlpha = new JRadioButton(language.getProperty("caseAlpha"));
		caseAlpha.setName("caseAlpha");
		caseAlpha.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAlpha.setBackground(new Color(50, 50, 50));
		caseAlpha.setSize(caseAlpha.getPreferredSize().width, 23);
		
		caseGOP = new JRadioButton(language.getProperty("caseGOP"));
		caseGOP.setName("caseGOP");
		caseGOP.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseGOP.setBackground(new Color(50, 50, 50));
		caseGOP.setSize(caseGOP.getPreferredSize().width, 23);
		
		caseGOP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseGOP.isSelected())
					gopSize.setEnabled(true);
				else
					gopSize.setEnabled(false);
			}
			
		});
		
		gopSize = new JTextField();
		gopSize.setName("gopSize");
		gopSize.setEnabled(false);
		gopSize.setHorizontalAlignment(SwingConstants.CENTER);
		gopSize.setText("250");
		gopSize.setFont(new Font("FreeSans", Font.PLAIN, 11));
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
		caseForceLevel.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForceLevel.setBackground(new Color(50, 50, 50));
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
		comboForceLevel.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForceLevel.setEditable(false);
		comboForceLevel.setSize(comboForceLevel.getPreferredSize().width, 16);

		comboForceProfile = new JComboBox<String>();
		comboForceProfile.setName("comboForceProfile");
		comboForceProfile.setEnabled(false);
		comboForceProfile.setMaximumRowCount(4);
		comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high" }));
		comboForceProfile.setSelectedIndex(2);
		comboForceProfile.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForceProfile.setEditable(false);
		comboForceProfile.setSize(66, 16);
		
		caseForcePreset = new JRadioButton(language.getProperty("caseForcePreset"));
		caseForcePreset.setName("caseForcePreset");
		caseForcePreset.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForcePreset.setBackground(new Color(50, 50, 50));
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
		comboForcePreset.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForcePreset.setEditable(false);
		comboForcePreset.setSize(comboForcePreset.getPreferredSize().width, 16);
		
		caseForceTune = new JRadioButton(language.getProperty("caseForceTune"));
		caseForceTune.setName("caseForceTune");
		caseForceTune.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForceTune.setBackground(new Color(50, 50, 50));
		caseForceTune.setSize(caseForceTune.getPreferredSize().width, 23);
		
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
		comboForceTune.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForceTune.setEditable(false);
		comboForceTune.setSize(81, 16);
		
		caseForceQuality = new JRadioButton(language.getProperty("caseForceQuality"));
		caseForceQuality.setName("caseForceQuality");
		caseForceQuality.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForceQuality.setBackground(new Color(50, 50, 50));
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
		comboForceQuality.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForceQuality.setEditable(false);
		comboForceQuality.setSize(comboForcePreset.getPreferredSize().width, 16);
		
		caseForceSpeed = new JRadioButton(language.getProperty("caseForceSpeed"));
		caseForceSpeed.setName("caseForceSpeed");
		caseForceSpeed.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseForceSpeed.setBackground(new Color(50, 50, 50));
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
		comboForceSpeed.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboForceSpeed.setEditable(false);
		comboForceSpeed.setSize(comboForceSpeed.getPreferredSize().width, 16);
		
		caseAS10 = new JRadioButton(language.getProperty("caseAS10"));
		caseAS10.setName("caseAS10");
		caseAS10.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseAS10.setBackground(new Color(50, 50, 50));
		caseAS10.setSize(caseAS10.getPreferredSize().width, 23);
		
		caseAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseAS10.isSelected())
				{
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
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
				}
			}

		});
		
		comboAS10 = new JComboBox<String>();
		comboAS10.setName("comboAS10");
		comboAS10.setEnabled(false);
		comboAS10.setMaximumRowCount(4);
		comboAS10.setModel(new DefaultComboBoxModel<String>(new String[] { "HIGH_HD_2014", "NRK_HD_2012"}));
		comboAS10.setSelectedIndex(0);
		comboAS10.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboAS10.setEditable(false);
		comboAS10.setSize(129, 16);

		caseLogo = new JRadioButton(language.getProperty("caseLogo"));
		caseLogo.setName("caseLogo");
		caseLogo.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseLogo.setBackground(new Color(50, 50, 50));
		caseLogo.setSize(caseLogo.getPreferredSize().width, 23);
		caseLogo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseLogo.isSelected()) {
					if (liste.getSize() == 0) {
						JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),
								language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseLogo.setSelected(false);
					} else {
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
								WatermarkWindow.loadImage("0", "0", "0", true, -1, true);
								Utils.changeDialogVisibility(WatermarkWindow.frame, shadow, false);
							}
							
							if (Functions.frame != null && Functions.frame.isVisible())
							{
								Thread t = new Thread (new Runnable() {
			
									@Override
									public void run() {
										do {
											try {
												Thread.sleep(100);
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
						}
					}
				}
			}

		});
	}

	private void grpH264() {
		grpH264 = new JPanel();
		grpH264.setLayout(null);
		grpH264.setVisible(false);
		grpH264.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(80, 80, 80), 1),
				language.getProperty("grpH264") + " ", 0, 0, new Font("Montserrat", Font.PLAIN, 12), Color.WHITE));
		grpH264.setBackground(new Color(50, 50, 50));
		grpH264.setBounds(658, 59, 312, 210);
		frame.getContentPane().add(grpH264);	
		
		lblDureH264 = new JLabel(language.getProperty("lblDureH264"));
		lblDureH264.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblDureH264.setBounds(40, 45, 46, 16);
		grpH264.add(lblDureH264);
	
		textH = new JTextField();
		textH.setName("textH");
		textH.setText("00");
		textH.setHorizontalAlignment(SwingConstants.CENTER);
		textH.setFont(new Font("FreeSans", Font.PLAIN, 14));
		textH.setColumns(10);
		textH.setBounds(86, 43, 32, 21);
		grpH264.add(textH);
		
		lblH = new JLabel(language.getProperty("lblH"));;
		lblH.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblH.setBounds(textH.getX() + textH.getWidth() + 4, 45, lblH.getPreferredSize().width, 16);
		grpH264.add(lblH);

		textMin = new JTextField();
		textMin.setName("textMin");
		textMin.setText("00");
		textMin.setHorizontalAlignment(SwingConstants.CENTER);
		textMin.setFont(new Font("FreeSans", Font.PLAIN, 14));
		textMin.setColumns(10);
		textMin.setBounds(lblH.getX() + lblH.getWidth() + 4, 43, 32, 21);
		grpH264.add(textMin);
		
		lblMin = new JLabel(language.getProperty("lblMin"));
		lblMin.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblMin.setBounds(textMin.getX() + textMin.getWidth() + 4, 45, lblMin.getPreferredSize().width, 16);
		grpH264.add(lblMin);
		
		textSec = new JTextField();
		textSec.setName("textSec");
		textSec.setText("00");
		textSec.setHorizontalAlignment(SwingConstants.CENTER);
		textSec.setFont(new Font("FreeSans", Font.PLAIN, 14));
		textSec.setColumns(10);
		textSec.setBounds(lblMin.getX() + lblMin.getWidth() + 4, 43, 32, 21);
		grpH264.add(textSec);
		
		lblSec = new JLabel(language.getProperty("lblSec"));
		lblSec.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		lblTailleH264.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblTailleH264.setBounds(40, 73, 46, 16);
		grpH264.add(lblTailleH264);

		case2pass = new JRadioButton(language.getProperty("case2pass"));
		case2pass.setName("case2pass");
		case2pass.setFont(new Font("FreeSans", Font.PLAIN, 12));
		case2pass.setBackground(new Color(50, 50, 50));
		case2pass.setBounds(14, 178, 95, 23);
		grpH264.add(case2pass);

		case2pass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
		caseRognage.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseRognage.setBackground(new Color(50, 50, 50));
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
					Object[] options = {language.getProperty("cropSimple"), language.getProperty("cropAdvanced")};
					
					int sub = JOptionPane.showOptionDialog(frame, language.getProperty("chooseCrop"), language.getProperty("frameCropVideo"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						    options,
						    options[0]);
		            
					if (sub == 1)
					{
						caseRognage.setSelected(false);
						
						if (caseRognerImage.isSelected())
							caseRognerImage.setSelected(false);
						
						caseRognerImage.doClick();
						
						if (caseRognerImage.isSelected())
						{
							//Agrandissement de la partie
							final int sized = 138;
							if (grpSequenceImage.getSize().height < sized) {
								Thread changeSize = new Thread(new Runnable() {
									@Override
									public void run() {
										try {
												int i = 17;
												long start = (System.currentTimeMillis());
												int fps = 0;
												int sleep = 1;
												do {
													i++;
		
													grpSequenceImage.setSize(312, i);
													grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
													grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
													grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
													grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
													grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
													btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
		
													Thread.sleep(sleep);
													
													// Permet de définir la vitesse
													if ((System.currentTimeMillis() - start) < 350)
														fps += 1;
													else {
														if (fps > 30)
															sleep = (int) (sleep / (fps / 25));
														else if (fps < 20)
															sleep = (int) (sleep * (fps / 25));
		
														start = System.currentTimeMillis();
														fps = 0;
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
														grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
																grpSequenceImage.getLocation().y - 1);
														grpLUTs.setLocation(grpLUTs.getLocation().x,
																grpLUTs.getLocation().y - 1);
														grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
																grpFiltreImage.getLocation().y - 1);
														grpCorrections.setLocation(grpCorrections.getLocation().x,
																grpCorrections.getLocation().y - 1);
														grpTransitions.setLocation(grpTransitions.getLocation().x,
																grpTransitions.getLocation().y - 1);
														grpAdvanced.setLocation(grpAdvanced.getLocation().x,
																grpAdvanced.getLocation().y - 1);
														btnReset.setLocation(btnReset.getLocation().x,
																btnReset.getLocation().y - 1);
													}
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
				
				if (caseRognage.isSelected()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}
					
					if (CropVideo.frame == null)
						new CropVideo();
					else {
						CropVideo.loadImage("00","00","00");
						Utils.changeDialogVisibility(CropVideo.frame, CropVideo.shadow, false);
					}
					
					if (Functions.frame != null && Functions.frame.isVisible())
					{
						Thread t = new Thread (new Runnable() {

							@Override
							public void run() {
								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException er) {}
								} while (CropVideo.frame.isVisible());
								
								//Largeur
					        	String i[] = FFPROBE.imageResolution.split("x");
								
								if (ratioFinal == 0)
									caseRognage.setSelected(false);
								else if (ratioFinal < (float) Integer.parseInt(i[0]) / Integer.parseInt(i[1])) {
									comboH264Taille.removeAllItems();
									comboH264Taille.addItem(Math.round(Integer.parseInt(i[1]) * ratioFinal) + "x" + i[1]);
									comboH264Taille.addItem(Math.round(1536 * ratioFinal) + "x1536");
									comboH264Taille.addItem(Math.round(1200 * ratioFinal) + "x1200");
									comboH264Taille.addItem(Math.round(768 * ratioFinal) + "x768");
									comboH264Taille.addItem(Math.round(600 * ratioFinal) + "x600");
									comboH264Taille.addItem("720x576");
									comboH264Taille.addItem(Math.round(480 * ratioFinal) + "x480");
									comboH264Taille.addItem(Math.round(360 * ratioFinal) + "x360");
									comboH264Taille.addItem(Math.round(180 * ratioFinal) + "x180");
									comboH264Taille.setSelectedIndex(0);
									comboH264Taille.setEnabled(true);
								} else {
									comboH264Taille.removeAllItems();
									comboH264Taille.addItem("4096x" + Math.round(4096 / ratioFinal));
									comboH264Taille.addItem("3840x" + Math.round(3840 / ratioFinal));
									comboH264Taille.addItem("1920x" + Math.round(1920 / ratioFinal));
									comboH264Taille.addItem("1280x" + Math.round(1280 / ratioFinal));
									comboH264Taille.addItem("854x" + Math.round(854 / ratioFinal));
									comboH264Taille.addItem("640x" + Math.round(640 / ratioFinal));
									comboH264Taille.addItem("320x" + Math.round(320 / ratioFinal));
									comboH264Taille.setSelectedIndex(2);
									comboH264Taille.setEnabled(true);
								}
								
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
						
	
						//Largeur
			        	String i[] = FFPROBE.imageResolution.split("x");
						
						if (ratioFinal == 0)
							caseRognage.setSelected(false);
						else if (ratioFinal < (float) Integer.parseInt(i[0]) / Integer.parseInt(i[1])) {
							comboH264Taille.removeAllItems();
							comboH264Taille.addItem(Math.round(Integer.parseInt(i[1]) * ratioFinal) + "x" + i[1]);
							if (Math.round(2160 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(2160 * ratioFinal) + "x2160");
							if (Math.round(1080 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(1080 * ratioFinal) + "x1080");
							if (Math.round(720 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(720 * ratioFinal) + "x720");
							if (Math.round(480 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(480 * ratioFinal) + "x480");
							if (Math.round(360 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(360 * ratioFinal) + "x360");
							if (Math.round(180 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem(Math.round(180 * ratioFinal) + "x180");
							comboH264Taille.setSelectedIndex(0);
							comboH264Taille.setEnabled(true);
						} else {
							comboH264Taille.removeAllItems();
							comboH264Taille.addItem(i[0] + "x" + Math.round(Integer.parseInt(i[0]) / ratioFinal));
							if (Math.round(4096 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("4096x" + Math.round(4096 / ratioFinal));
							if (Math.round(3840 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("3840x" + Math.round(3840 / ratioFinal));
							if (Math.round(1920 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("1920x" + Math.round(1920 / ratioFinal));
							if (Math.round(1280 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("1280x" + Math.round(1280 / ratioFinal));
							if (Math.round(854 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("854x" + Math.round(854 / ratioFinal));
							if (Math.round(640 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("640x" + Math.round(640 / ratioFinal));
							if (Math.round(320 * ratioFinal) % 2 == 0)
								comboH264Taille.addItem("320x" + Math.round(320 / ratioFinal));
							comboH264Taille.setSelectedIndex(0);
							comboH264Taille.setEnabled(true);
						}
					}
				} else {
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
		caseQMax.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseQMax.setBackground(new Color(50, 50, 50));
		caseQMax.setBounds(215, 178, 90, 23);
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
		comboH264Taille.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboH264Taille.setEditable(true);
		comboH264Taille.setBounds(84, 71, 120, 22);
		grpH264.add(comboH264Taille);

		comboH264Taille.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				lblPad.setVisible(true);
				lblPad.setText(language.getProperty("lblPad"));
				lblPadLeft.setBackground(Color.black);
				lblPadLeft.setVisible(true);
				lblPadRight.setBackground(Color.black);
				lblPadRight.setVisible(true);
				
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

		lblPad = new JLabel(language.getProperty("lblPad"));
		lblPad.setName("lblPad");
		lblPad.setBackground(new Color(80, 80, 80));
		lblPad.setHorizontalAlignment(SwingConstants.CENTER);
		lblPad.setOpaque(true);
		lblPad.setVisible(false);
		lblPad.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 36, comboH264Taille.getLocation().y + 3, 65, 16);
		grpH264.add(lblPad);
		
		lblPadLeft = new JLabel();
		lblPadLeft.setName("lblPadLeft");
		lblPadLeft.setBackground(Color.black);
		lblPadLeft.setOpaque(true);
		lblPadLeft.setBounds(0, 0, 8, 16);
		lblPad.add(lblPadLeft);
		
		lblPadRight = new JLabel();
		lblPadRight.setName("lblPadRight");
		lblPadRight.setBackground(Color.black);
		lblPadRight.setOpaque(true);
		lblPadRight.setBounds(57, 0, 8, 16);
		lblPad.add(lblPadRight);
		
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
							lblPadLeft.setBackground(new Color(50,50,50));
							lblPadLeft.setVisible(true);
							lblPadRight.setBackground(new Color(50,50,50));
							lblPadRight.setVisible(true);
						}
						else
						{
							lblPad.setText(language.getProperty("lblPad"));
							lblPadLeft.setBackground(Color.black);
							lblPadLeft.setVisible(true);
							lblPadRight.setBackground(Color.black);
							lblPadRight.setVisible(true);
						}
					}
					else
					{
						if (lblPad.getText().equals(language.getProperty("lblPad")))
						{
							lblPad.setText(language.getProperty("lblStretch"));
							lblPadLeft.setVisible(false);
							lblPadRight.setVisible(false);
						}
						else if (lblPad.getText().equals(language.getProperty("lblStretch")))
						{
							lblPad.setText(language.getProperty("lblPad"));
							lblPadLeft.setBackground(Color.black);
							lblPadLeft.setVisible(true);
							lblPadRight.setBackground(Color.black);
							lblPadRight.setVisible(true);
						}
					}
				}
				else
				{				
					if (lblPad.getText().equals(language.getProperty("lblPad")))
					{
						lblPad.setText(language.getProperty("lblStretch"));
						lblPadLeft.setVisible(false);
						lblPadRight.setVisible(false);
					}
					else if (lblPad.getText().equals(language.getProperty("lblStretch")))
					{
						lblPad.setText(language.getProperty("lblCrop"));
						lblPadLeft.setBackground(new Color(50,50,50));
						lblPadLeft.setVisible(true);
						lblPadRight.setBackground(new Color(50,50,50));
						lblPadRight.setVisible(true);
					}
					else
					{
						lblPad.setText(language.getProperty("lblPad"));
						lblPadLeft.setBackground(Color.black);
						lblPadLeft.setVisible(true);
						lblPadRight.setBackground(Color.black);
						lblPadRight.setVisible(true);
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
					lblPadLeft.setVisible(false);
					lblPadRight.setVisible(false);
				}
				else
				{
					if (caseRognage.isSelected())
					{
						lblPad.setText(language.getProperty("lblCrop"));
						lblPadLeft.setBackground(new Color(50,50,50));
						lblPadLeft.setVisible(true);
						lblPadRight.setBackground(new Color(50,50,50));
						lblPadRight.setVisible(true);
					}
					else						
					{
						lblPad.setText(language.getProperty("lblPad"));
						lblPadLeft.setBackground(Color.black);
						lblPadLeft.setVisible(true);
						lblPadRight.setBackground(Color.black);
						lblPadRight.setVisible(true);
					}
					
					lblPad.setVisible(true);
				}	
			}
		  }
			
		});
		
		JLabel lblPixelsH264 = new JLabel("pixels");
		lblPixelsH264.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblPixelsH264.setBounds(206, 73, 44, 16);
		grpH264.add(lblPixelsH264);

		lblH264 = new JLabel("");
		lblH264.setVisible(false);
		lblH264.setHorizontalAlignment(SwingConstants.CENTER);
		lblH264.setForeground(new Color(71, 163, 236));
		lblH264.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblH264.setBounds(6, 19, 300, 16);
		grpH264.add(lblH264);

		iconTVH264 = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/TV2.jpg")));
		iconTVH264.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVH264.setBounds(14, 74, 16, 16);
		iconTVH264.setToolTipText(language.getProperty("preview"));
		grpH264.add(iconTVH264);

		iconTVH264.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
					switch (comboFonctions.getSelectedItem().toString()) {
					case "H.264":
						H264.main(false);
						break;
					case "H.265":
						H265.main(false);
						break;
					case "VP9":
						VP9.main(false);
						break;
					case "AV1":
						AV1.main(false);
						break;
					case "WMV":
						WMV.main(false);
						break;
					case "MPEG":
						MPEG.main(false);
						break;
					case "OGV":
						OGV.main(false);
						break;
					case "MJPEG":
						MJPEG.main(false);
						break;
					case "Xvid":
						Xvid.main(false);
						break;
					}			
										
				Thread thread = new Thread(new Runnable(){			
					@Override
					public void run() {	
						try {
							do {								
								Thread.sleep(100);								
							} while (FFMPEG.isRunning == false);	
							
							enableAll();
							lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
							lblEncodageEnCours.setText(language.getProperty("lblEncodageEnCours"));
							
							do {								
								Thread.sleep(100);								
							} while (FFMPEG.isRunning);	
						} catch (InterruptedException e1) {}
												
						if (case2pass.isSelected())
						{						
							File fichier = new File(liste.getElementAt(0));
							File folder = new File(fichier.getParent());
							String ext = fichier.toString().substring(fichier.toString().lastIndexOf("."));
							
							if (caseChangeFolder1.isSelected())
								folder = new File(lblDestination1.getText());					
	
							//Suppression fichiers résiduels	
						    for (final File fileEntry : folder.listFiles()) {							
						        if (fileEntry.isFile()) {
						        	if (fileEntry.getName().contains(fichier.getName().replace(ext, "")) && fileEntry.getName().contains("log"))
						        	{
						        		File fileToDelete = new File(fileEntry.getAbsolutePath());
						        		fileToDelete.delete();
						        	}
						        }
						    }
						}	
					}
				});
				thread.start();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVH264.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVH264.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/TV2.jpg"))));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		
		lblVBR = new JLabel("VBR");
		lblVBR.setName("lblVBR");
		lblVBR.setBackground(new Color(80, 80, 80));
		lblVBR.setHorizontalAlignment(SwingConstants.CENTER);
		lblVBR.setOpaque(true);
		lblVBR.setFont(new Font("Montserrat", Font.PLAIN, 11));
		lblVBR.setBounds(5, 100, 32, 16);
		grpH264.add(lblVBR);

		lblVBR.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblVBR.getText().equals("VBR") && caseAccel.isSelected() == false && comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					lblVBR.setText("CBR");
				}
				else if (lblVBR.getText().equals("CBR") || lblVBR.getText().equals("VBR") && caseAccel.isSelected() && comboFonctions.getSelectedItem().toString().contains("H.26")
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
					if (caseAccel.isSelected() == false || comboFonctions.getSelectedItem().toString().equals("VP9") == false && comboFonctions.getSelectedItem().toString().contains("H.26") == false)
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
		debitVideo.setFont(new Font("FreeSans", Font.PLAIN, 11));
		debitVideo.setEditable(true);
		debitVideo.setBounds(121, 98, 83, 22);
		grpH264.add(debitVideo);

		debitAudio = new JComboBox<String>();
		debitAudio.setName("debitAudio");
		debitAudio.setModel(new DefaultComboBoxModel<String>(new String[] { "320", "256", "192", "128", "96", "64"}));
		debitAudio.setSelectedIndex(1);
		debitAudio.setMaximumRowCount(20);
		debitAudio.setFont(new Font("FreeSans", Font.PLAIN, 11));
		debitAudio.setEditable(true);
		debitAudio.setBounds(121, 125, 83, 22);
		grpH264.add(debitAudio);

		lblKbsH264 = new JLabel("kb/s");
		lblKbsH264.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblKbsH264.setBounds(206, 100, 33, 16);
		grpH264.add(lblKbsH264);
		
		JLabel lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblKbs.setBounds(206, 127, 33, 16);
		grpH264.add(lblKbs);

		JLabel lblMo = new JLabel(language.getProperty("mo"));
		lblMo.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblMo.setBounds(206, 154, 33, 16);
		grpH264.add(lblMo);

		taille = new JTextField();
		taille.setName("taille");
		taille.setHorizontalAlignment(SwingConstants.CENTER);
		taille.setText("2000");
		taille.setFont(new Font("FreeSans", Font.PLAIN, 11));
		taille.setColumns(10);
		taille.setBounds(121, 152, 83, 21);
		grpH264.add(taille);
		
		lock = new JLabel();
		lock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("contents/unlock.png")));
		lock.setHorizontalAlignment(SwingConstants.CENTER);
		lock.setBounds(taille.getX() - 21 - 4, taille.getY() + 1, 21, 21);
		grpH264.add(lock);
		
		lock.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			if (lock.getIcon().toString().substring(lock.getIcon().toString().lastIndexOf("/") + 1).equals("lock.png"))	
				lock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("contents/unlock.png")));
			else
				lock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("contents/lock.png")));
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
		lblSize.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblSize.setBounds(40, 153, lblSize.getPreferredSize().width, 16);
		grpH264.add(lblSize);

		lblDbitVido = new JLabel(language.getProperty("lblDbitVido"));
		lblDbitVido.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblDbitVido.setBounds(40, 100, 76, 16);
		grpH264.add(lblDbitVido);

		lblDbitAudio = new JLabel(language.getProperty("lblDbitAudio"));
		lblDbitAudio.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		btnReset.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnReset.setBounds(334, 567, 312, 25);
		frame.getContentPane().add(btnReset);

		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Resolution
				comboResolution.setSelectedIndex(0);
				caseRognerImage.setSelected(false);
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
				caseIncrementTimecode.setEnabled(false);
				caseIncrementTimecode.setSelected(false);

				// Timecode;
				caseAddOverlay.setSelected(false);
				caseShowDate.setSelected(false);
				caseShowFileName.setSelected(false);

				// H264
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
					comboAudio5.setSelectedIndex(8);
					comboAudio6.setSelectedIndex(8);
					comboAudio7.setSelectedIndex(8);
					comboAudio8.setSelectedIndex(8);
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);											
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
					comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
										} while (i > 74);
									} catch (Exception e1) {
									}									
							}
						});
						changeSize.start();
					}	
				}
				else if ("VP9".equals(comboFonctions.getSelectedItem().toString()))
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpSequenceImage.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
											grpOverlay.setLocation(grpSequenceImage.getLocation().x, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);											
											grpLUTs.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpCorrections.setLocation(grpLUTs.getLocation().x, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);	
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}

											if (grpH264.getLocation().y < grpChoixDesFichiers.getLocation().y && grpH264.isVisible() 
													 || grpInAndOut.getLocation().y < grpChoixDesFichiers.getLocation().y && grpInAndOut.isVisible()
													 || grpResolution.getLocation().y < grpChoixDesFichiers.getLocation().y && grpResolution.isVisible() ) {
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
												grpSequenceImage.setLocation(grpSequenceImage.getLocation().x,
														grpSequenceImage.getLocation().y + 1);
												grpLUTs.setLocation(grpLUTs.getLocation().x,
														grpLUTs.getLocation().y + 1);
												grpFiltreImage.setLocation(grpFiltreImage.getLocation().x,
														grpFiltreImage.getLocation().y + 1);
												grpCorrections.setLocation(grpCorrections.getLocation().x,
														grpCorrections.getLocation().y + 1);
												grpTransitions.setLocation(grpTransitions.getLocation().x,
														grpTransitions.getLocation().y + 1);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x,
														grpAdvanced.getLocation().y + 1);
												btnReset.setLocation(btnReset.getLocation().x,
														btnReset.getLocation().y + 1);
													}
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
										long start = (System.currentTimeMillis());
										int fps = 0;
										int sleep = 1;
										do {
											i--;

											grpSetAudio.setSize(312, i);
											grpOverlay.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);											
											grpCorrections.setLocation(grpOverlay.getLocation().x, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
											grpTransitions.setLocation(grpCorrections.getLocation().x, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
											grpAdvanced.setLocation(grpTransitions.getLocation().x, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
											btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

											Thread.sleep(sleep);
											
											// Permet de définir la vitesse
											if ((System.currentTimeMillis() - start) < 350)
												fps += 1;
											else {
												if (fps > 30)
													sleep = (int) (sleep / (fps / 25));
												else if (fps < 20)
													sleep = (int) (sleep * (fps / 25));

												start = System.currentTimeMillis();
												fps = 0;
											}
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
						if (lblPad.getText().equals(language.getProperty("lblPad")))
						{
							lblPadLeft.setBackground(Color.black);
							lblPadLeft.setVisible(true);
							lblPadRight.setBackground(Color.black);
							lblPadRight.setVisible(true);
						}
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
				caseForcerResolution.setSelected(false);
				lblResolution.setEnabled(false);
				lblResolution.setSelectedIndex(1);
				caseForcerDAR.setSelected(false);
				comboDAR.setEnabled(false);
				comboDAR.setSelectedIndex(2);
				changementDeFiltre();		
				lblPad.setText(language.getProperty("lblPad"));
				lblPadLeft.setBackground(Color.black);
				lblPadLeft.setVisible(true);
				lblPadRight.setBackground(Color.black);
				lblPadRight.setVisible(true);
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
				if (caseCreateOPATOM.isSelected())
					caseCreateOPATOM.doClick();
				lblOPATOM.setText("OP-Atom");
				caseOPATOM.setSelected(false);
				caseSubtitles.setSelected(false);
				caseConform.setSelected(false);
				comboConform.setEnabled(false);
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
				caseActiverSequence.setSelected(false);
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
				
				spinnerVideoFadeIn.setValue(25);
				spinnerVideoFadeOut.setValue(25);
				spinnerAudioFadeIn.setValue(25);
				spinnerAudioFadeOut.setValue(25);
				
				lblFadeInColor.setText(language.getProperty("black"));
				lblFadeOutColor.setText(language.getProperty("black"));
				
				// Important
				panelHaut.repaint();
				statusBar.repaint();
			}

		});

	}

	private void StatusBar() {
		lblCrParPaul = new JLabel(language.getProperty("lblCrParPaul"));
		lblCrParPaul.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		tempsRestant.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
				if (caseVisualiser.isSelected() == false)
				{
					tempsRestant.setVisible(false);
					
					if (tempsEcoule != null && tempsEcoule.getText().equals(language.getProperty("tempsEcoule")) == false)
						tempsEcoule.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {	
				if (caseVisualiser.isSelected() == false)
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
		tempsEcoule.setFont(new Font("FreeSans", Font.PLAIN, 12));
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

		lblYears = new JLabel("2013-2020");
		lblYears.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYears.setForeground(Color.BLACK);
		lblYears.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblYears.setBounds(585, 651, 60, 15);
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
						MousePosition.mouseX = down.getPoint().x;
						MousePosition.mouseY = down.getPoint().y;
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}

				});

				image.addMouseMotionListener(new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent e) {
						oldVersions.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX,
								MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);
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
				.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] { btnParcourir, btnVider,
						listeDeFichiers, comboFonctions, comboFilter, btnStart, btnAnnuler, caseOpenFolderAtEnd1,
						caseChangeFolder1, caseRunInBackground, iconTVResolution, comboResolution, caseRognerImage,
						caseCreateSequence, caseSequenceFPS, caseAddOverlay,
						caseMixAudio, caseInAndOut, textH, textMin, textSec, comboH264Taille, debitVideo, debitAudio,
						taille, case2pass, caseRognage, caseQMax, panelHaut, lblV, quit, reduce, help, topImage,
						grpChoixDesFichiers, scrollBar, lblTermine, lblFichiers, grpChoixFonction, lblFilter,
						grpDestination, lblDestination1, grpProgression, progressBar1, lblEncodageEnCours, grpResolution,
						lblTaille, grpSequenceImage, grpFiltreImage, grpOverlay, grpInAndOut, grpSetAudio, grpAudio,
						grpAdvanced, grpH264, lblDureH264, lblMin, lblH, lblTailleH264, lblH264, iconTVH264, lblSize, lblDbitVido, lblDbitAudio }));

	}

	// Modifications de l'affichage
	public static void changementDeFonction(final boolean anim) {		
		String fonction = comboFonctions.getSelectedItem().toString();
		if (language.getProperty("functionCut").equals(fonction) || language.getProperty("functionRewrap").equals(fonction) 
				|| language.getProperty("functionReplaceAudio").equals(fonction)
				|| "WAV".equals(fonction) || "AIFF".equals(fonction) || "FLAC".equals(fonction)
				|| "MP3".equals(fonction) || "AAC".equals(fonction) || "AC3".equals(fonction) || "OPUS".equals(fonction)
				|| "OGG".equals(fonction) || "Loudness & True Peak".equals(fonction) || language.getProperty("functionVideoLevels").equals(fonction)
				|| language.getProperty("functionBlackDetection").equals(fonction) || language.getProperty("functionOfflineDetection").equals(fonction) 
				|| "DNxHD".equals(fonction)	|| "DNxHR".equals(fonction) || "Apple ProRes".equals(fonction) || "QT Animation".equals(fonction) || ("GoPro CineForm").equals(fonction) || "Uncompressed YUV".equals(fonction)
				|| "H.264".equals(fonction) || "H.265".equals(fonction) || "DV PAL".equals(fonction)
				|| "WMV".equals(fonction) || "MPEG".equals(fonction) || "VP9".equals(fonction) || "AV1".equals(fonction) || "OGV".equals(fonction)
				|| "MJPEG".equals(fonction) || "Xvid".equals(fonction) || "XDCAM HD422".equals(fonction) || "AVC-Intra 100".equals(fonction) || ("XAVC").equals(fonction) || "HAP".equals(fonction) || "FFV1".equals(fonction)
				|| "DVD".equals(fonction) || "Blu-ray".equals(fonction) || "QT JPEG".equals(fonction)
				|| language.getProperty("functionPicture").equals(fonction)
				|| "JPEG".equals(fonction)
				|| language.getProperty("functionNormalization").equals(fonction)) {
			changeFrameSize(true);
			changeGroupes(anim);
		} else if (language.getProperty("functionBab").equals(fonction)
				|| language.getProperty("functionExtract").equals(fonction)
				|| language.getProperty("functionConform").equals(fonction)
				|| language.getProperty("functionInsert").equals(fonction)
				|| "CD RIP".equals(fonction)
				|| "DVD RIP".equals(fonction)				
				|| language.getProperty("functionSceneDetection").equals(fonction)
				|| language.getProperty("functionSubtitles").equals(fonction)
				|| "Synchronisation automatique".equals(fonction)
				|| language.getProperty("functionWeb").equals(fonction)) {
			changeFrameSize(false);
			changeGroupes(anim);
		} else if (language.getProperty("itemMyFunctions").equals(fonction)) {
			changeFrameSize(false);
			changeGroupes(anim);;
			if (Functions.frame == null)
				new Functions();
			else {
				if (Functions.listeDeFonctions.getModel().getSize() > 0) {
					Functions.lblSave.setVisible(false);
					Functions.lblDrop.setVisible(false);
				}

				Functions.frame.setVisible(true);
			}
			Utils.changeFrameVisibility(Functions.frame, Functions.shadow, false);
		}

		//File de rendus
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionBab")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionExtract")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
				&& comboFonctions.getSelectedItem().equals("DVD RIP") == false
				&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionVideoLevels")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("itemMyFunctions")) == false
				&& (RenderQueue.frame == null || RenderQueue.frame != null && RenderQueue.frame.isVisible() == false)) {
			iconList.setVisible(true);
			if (iconPresets.isVisible())
			{
				iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 46);
				btnAnnuler.setBounds(205 + Shutter.iconList.getWidth(), 44, 101 - Shutter.iconList.getWidth(), 25);
			}
			else
			{
				iconPresets.setBounds(180, 46, 21, 21);
				btnAnnuler.setBounds(205, 44, 101, 25);
			}
		}
		else
		{
			iconList.setVisible(false);		
			
			if (iconPresets.isVisible())
			{
				iconPresets.setBounds(180, 46, 21, 21);
				btnAnnuler.setBounds(205, 44, 101, 25);
			}
			else
			{
				btnAnnuler.setBounds(182, 44, 124, 25);
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

		// Désactivation du grpChoixDesFichiers
		Component[] components = grpChoixDesFichiers.getComponents();
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD RIP")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(false);
		} else {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(true);
		}

		// Modification des cases par rapport aux fonctions
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD RIP")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
			if (caseChangeFolder1.isSelected() == false) {
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
				else
					lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
			}			
		} else {
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(language.getProperty("sameAsSource"));
		}
		
		if (grpDestination.getTabCount() < 5)
		{
			setDestinationTabs(6);
		}	
		
		if (((comboFonctions.getSelectedItem().toString().equals("DNxHD") || (comboFonctions.getSelectedItem().toString().equals("DNxHR"))) && caseCreateOPATOM.isSelected()) || caseCreateTree.isSelected()) //OP-Atom
		{
			setDestinationTabs(2);		
		}

		// Modifications du statut des cases
		if (comboFonctions.getSelectedItem().equals("Loudness & True Peak")
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionVideoLevels"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionBab"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles"))
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) {
			caseOpenFolderAtEnd1.setEnabled(false);
			caseChangeFolder1.setEnabled(false);
			lblDestination1.setVisible(false);
			
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionBab")) == false)
			{
				setDestinationTabs(1);		
				
				if (comboFonctions.getSelectedItem().equals("Loudness & True Peak"))
					setDestinationTabs(2);
				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
				{
					caseOpenFolderAtEnd1.setEnabled(true);
					caseChangeFolder1.setEnabled(true);
					lblDestination1.setVisible(true);
					setDestinationTabs(2);
				}
			}
			else
			{
				if (grpDestination.getTabCount() < 5)
				{
					setDestinationTabs(6);
				}
			}
			
		} else {
			caseOpenFolderAtEnd1.setEnabled(true);
			caseChangeFolder1.setEnabled(true);
			lblDestination1.setVisible(true);

		}

		// btnStart text
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
			btnStart.setText(language.getProperty("btnDownload"));
		else if (RenderQueue.frame != null) {
			if (RenderQueue.frame.isVisible()
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBab")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
					&& comboFonctions.getSelectedItem().equals("DVD RIP") == false
					&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
				    && comboFonctions.getSelectedItem().equals(language.getProperty("functionVideoLevels")) == false
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
		
		//CaseForcerResolution
		if (caseForcerResolution.isSelected())
			lblResolution.setEnabled(true);
		else
			lblResolution.setEnabled(false);
		
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
		
				
		if (caseForceTune.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("VP9"))) {
			caseForceTune.setEnabled(true);
			comboForceTune.setEnabled(true);
		} 
		else 
			comboForceTune.setEnabled(false);
		
		if (caseForceQuality.isSelected() && comboFonctions.getSelectedItem().toString().equals("VP9") && caseQMax.isSelected() == false)
		{
			caseForceQuality.setEnabled(true);
			comboForceQuality.setEnabled(true);
		} 
		else 
			comboForceQuality.setEnabled(false);
		
		if (caseForceSpeed.isSelected() && caseQMax.isSelected() == false && (comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1")))
		{
			caseForceSpeed.setEnabled(true);
			comboForceSpeed.setEnabled(true);
		} 
		else 
			comboForceSpeed.setEnabled(false);
		

	}

	private static void changeFrameSize(final boolean bigger) {

		/*
		 * Trop de bugs Thread changeSize = new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * if (changeSizeIsRunning == false) //permet d'attendre la fin de l'action {
		 * try { if (bigger && frame.getSize().width < 657) { do { changeSizeIsRunning =
		 * true; frame.setSize(frame.getSize().width + 20, 669); frame.setShape(new
		 * RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15,
		 * 15)); Thread.sleep(3); quit.setLocation(frame.getSize().width - 24, 0);
		 * reduce.setLocation(quit.getLocation().x - 21,0);
		 * help.setLocation(reduce.getLocation().x - 21,0);
		 * newInstance.setLocation(help.getLocation().x - 21,0); } while
		 * (frame.getSize().width < 650); changeSizeIsRunning = false;
		 * frame.setSize(657, 669); frame.setShape(new RoundRectangle2D.Double(0, 0,
		 * frame.getWidth(), frame.getHeight() + 18, 15, 15));
		 * quit.setLocation(frame.getSize().width - 24, 0);
		 * reduce.setLocation(quit.getLocation().x - 21,0);
		 * help.setLocation(reduce.getLocation().x - 21,0);
		 * newInstance.setLocation(help.getLocation().x - 21,0);
		 * changementDeFonction(true); //une fois l'action terminé on vérifie si la
		 * taille est toujours la même } else if (bigger == false &&
		 * frame.getSize().width > 332) { do { changeSizeIsRunning = true;
		 * frame.setSize(frame.getSize().width - 20, 669); frame.setShape(new
		 * RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15,
		 * 15)); Thread.sleep(3); quit.setLocation(frame.getSize().width - 24, 0);
		 * reduce.setLocation(quit.getLocation().x - 21,0);
		 * help.setLocation(reduce.getLocation().x - 21,0);
		 * newInstance.setLocation(help.getLocation().x - 21,0); } while
		 * (frame.getSize().width > 340); changeSizeIsRunning = false;
		 * frame.setSize(332, 669); frame.setShape(new RoundRectangle2D.Double(0, 0,
		 * frame.getWidth(), frame.getHeight() + 18, 15, 15));
		 * quit.setLocation(frame.getSize().width - 24, 0);
		 * reduce.setLocation(quit.getLocation().x - 21,0);
		 * help.setLocation(reduce.getLocation().x - 21,0);
		 * newInstance.setLocation(help.getLocation().x - 21,0);
		 * changementDeFonction(true); }
		 * 
		 * } catch (Exception e1) {} } } }); changeSize.start();
		 */

		if (bigger && frame.getSize().width < 657) {
			frame.setSize(657, 669);
			frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
			quit.setLocation(frame.getSize().width - 24, 0);
			reduce.setLocation(quit.getLocation().x - 21, 0);
			help.setLocation(reduce.getLocation().x - 21, 0);
			newInstance.setLocation(help.getLocation().x - 21, 0);
		} else if (bigger == false && frame.getSize().width > 332) {
			frame.setSize(332, 669);
			frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
			quit.setLocation(frame.getSize().width - 24, 0);
			reduce.setLocation(quit.getLocation().x - 21, 0);
			help.setLocation(reduce.getLocation().x - 21, 0);
			newInstance.setLocation(help.getLocation().x - 21, 0);
		}

		shadow.repaint();

	}

	private static void changeGroupes(final boolean action) {
		Thread changeSize = new Thread(new Runnable() {
			@Override
			public void run() {

				if (changeGroupes == false) // permet d'attendre la fin de l'action
				{
					try {
						
						if (frame.getSize().width == 657 && action) {
							int i = 334;
							long start = (System.currentTimeMillis());
							int fps = 0;
							int sleep = 1;
							do {
								changeGroupes = true;
								i += 2;
								grpResolution.setLocation(i, grpResolution.getLocation().y);
								grpH264.setLocation(i, grpH264.getLocation().y);
								grpSetTimecode.setLocation(i, grpSetTimecode.getLocation().y);
								grpOverlay.setLocation(i, grpOverlay.getLocation().y);
								grpSetAudio.setLocation(i, grpSetAudio.getLocation().y);
								grpAudio.setLocation(i, grpAudio.getLocation().y);
								grpInAndOut.setLocation(i, grpInAndOut.getLocation().y);
								grpSequenceImage.setLocation(i, grpSequenceImage.getLocation().y);
								grpFiltreImage.setLocation(i, grpFiltreImage.getLocation().y);
								grpLUTs.setLocation(i, grpLUTs.getLocation().y);
								grpCorrections.setLocation(i, grpCorrections.getLocation().y);
								grpTransitions.setLocation(i, grpTransitions.getLocation().y);
								grpAdvanced.setLocation(i, grpAdvanced.getLocation().y);
								btnReset.setLocation(i, btnReset.getLocation().y);

								Thread.sleep(sleep);

								// Permet de définir la vitesse
								if ((System.currentTimeMillis() - start) < 350)
									fps += 1;
								else {
									if (fps > 30)
										sleep = (int) (sleep / (fps / 25));
									else if (fps < 20)
										sleep = (int) (sleep * (fps / 25));

									start = System.currentTimeMillis();
									fps = 0;
								}
							} while (i < 680);
						}

						String fonction = comboFonctions.getSelectedItem().toString();
							
						if (action)
						{
							grpAdvanced.setSize(grpAdvanced.getSize().width, 17);
							grpOverlay.setSize(grpOverlay.getSize().width, 17);
							grpFiltreImage.setSize(grpFiltreImage.getSize().width, 17);
							grpSetAudio.setSize(grpSetAudio.getSize().width, 17);
							grpSequenceImage.setSize(grpSequenceImage.getSize().width, 17);
							grpLUTs.setSize(grpLUTs.getSize().width, 17);
							grpCorrections.setSize(grpCorrections.getSize().width, 17);
							grpTransitions.setSize(grpTransitions.getSize().width, 17);
							grpSetTimecode.setSize(grpSetTimecode.getSize().width, 17);
						}
						
						btnReset.setVisible(true);
				
						if (language.getProperty("functionRewrap").equals(fonction) || language.getProperty("functionCut").equals(fonction)) {
							if (language.getProperty("functionCut").equals(fonction))
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							else
								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));
							caseVisualiser.setEnabled(false);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpOverlay.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, 59);
							grpSetTimecode.setVisible(true);
							grpSetTimecode.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							grpSetAudio.setVisible(true);
							grpSetAudio.setLocation(334, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpAudio.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);	
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);							
							if (comboAudioCodec.getItemCount() != 9)
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
							
							lblAudio1.setLocation(17, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								&& comboAudio5.getSelectedIndex() == 8
								&& comboAudio6.getSelectedIndex() == 8
								&& comboAudio7.getSelectedIndex() == 8
								&& comboAudio8.getSelectedIndex() == 8)
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
							caseVisualiser.setEnabled(false);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetAudio.setVisible(true);
							grpSetAudio.setSize(312, 70);
							grpSetAudio.setLocation(334, 59);
							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							if (comboAudioCodec.getItemCount() != 9)
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
							caseVisualiser.setEnabled(false);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpSetAudio.setVisible(false);														
							grpAudio.setVisible(true);
							grpAudio.setLocation(334, 59);
							grpAudio.add(lbl48k);
							lbl48k.setLocation(caseSampleRate.getLocation().x + caseSampleRate.getWidth() + 3, caseSampleRate.getLocation().y + 3);							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, grpAudio.getSize().height + grpAudio.getLocation().y + 6);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
						} else if ("Loudness & True Peak".equals(fonction)
								|| language.getProperty("functionVideoLevels").equals(fonction)
								|| language.getProperty("functionBlackDetection").equals(fonction)
								|| language.getProperty("functionOfflineDetection").equals(fonction)
								|| language.getProperty("functionInsert").equals(fonction)) {

							if (language.getProperty("functionVideoLevels").equals(fonction)
								|| language.getProperty("functionBlackDetection").equals(fonction)
								|| language.getProperty("functionOfflineDetection").equals(fonction))
								addToList.setText(language.getProperty("filesVideo"));
							else if (language.getProperty("functionInsert").equals(fonction))
								addToList.setText(language.getProperty("fileMaster"));
							else
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							caseVisualiser.setEnabled(false);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							grpOverlay.setVisible(false);
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, 59);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
						} else if ("XDCAM HD422".equals(fonction) || "AVC-Intra 100".equals(fonction) || ("XAVC").equals(fonction) || "HAP".equals(fonction) || "FFV1".equals(fonction)) {
							//grpSetAudio
							grpSetAudio.removeAll();
							lblAudio1.setLocation(17, 18);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								comboAudio5.setSelectedIndex(8);
								comboAudio6.setSelectedIndex(8);
								comboAudio7.setSelectedIndex(8);
								comboAudio8.setSelectedIndex(8);
							}
							else if (comboAudio1.getSelectedIndex() == 0
									&& comboAudio2.getSelectedIndex() == 1
									&& comboAudio3.getSelectedIndex() == 2
									&& comboAudio4.getSelectedIndex() == 3
									&& comboAudio5.getSelectedIndex() == 8
									&& comboAudio6.getSelectedIndex() == 8
									&& comboAudio7.getSelectedIndex() == 8
									&& comboAudio8.getSelectedIndex() == 8 && (fonction.equals("HAP") || fonction.equals("FFV1") || caseAS10.isSelected()))
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
							
							addToList.setText(language.getProperty("filesVideo"));
							caseVisualiser.setEnabled(true);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, 59);
							grpSetTimecode.setVisible(true);
							grpSetTimecode.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							grpAudio.setVisible(false);
							grpSetAudio.setVisible(true);
							if (fonction.equals("HAP") == false && fonction.equals("FFV1") == false)
								grpSetAudio.setSize(312, 100);
							else if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(334, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							grpSetAudio.repaint();
							
							grpSequenceImage.setVisible(true);
							grpSequenceImage.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(334, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);							
							grpLUTs.setVisible(true);
							grpLUTs.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(334, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(334, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);							
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							//grpOverlay
							caseSubtitles.setLocation(7, 16);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
							
							// Ajout case rognage
							caseRognerImage.setLocation(7, caseActiverSequence.getHeight() + caseActiverSequence.getLocation().y);
							grpSequenceImage.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpSequenceImage.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpSequenceImage.add(comboRotate);
							
							// Case Blend location
							caseBlend.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 4);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth() -  4, caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpSequenceImage.add(caseMiror);
							
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
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 4, caseConform.getLocation().y + 4);
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
		
							caseForcerDAR.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
							grpAdvanced.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpAdvanced.add(comboDAR);
							caseForcerResolution.setLocation(7, caseForcerDAR.getLocation().y + 17);
							grpAdvanced.add(caseForcerResolution);
							lblResolution.setLocation(caseForcerResolution.getLocation().x + caseForcerResolution.getWidth() + 4, caseForcerResolution.getLocation().y + 3);
							lblPad.setBounds(lblResolution.getLocation().x + lblResolution.getWidth() + 5, lblResolution.getLocation().y, 65, 16);
							grpAdvanced.add(lblPad);
							lblPad.setVisible(true);
							if (lblPad.getText().equals(language.getProperty("lblPad")))
							{
								lblPadLeft.setBackground(Color.black);
								lblPadLeft.setVisible(true);
								lblPadRight.setBackground(Color.black);
								lblPadRight.setVisible(true);
							}
							grpAdvanced.add(lblResolution);
														
							if (fonction.equals("XDCAM HD422"))
							{
								caseAS10.setText(language.getProperty("caseAS10"));
								caseAS10.setLocation(7, caseForcerResolution.getLocation().y + 17);
								grpAdvanced.add(caseAS10);
								comboAS10.setLocation(caseAS10.getX() + caseAS10.getWidth() + 4, caseAS10.getLocation().y + 4);
								grpAdvanced.add(comboAS10);
							}
							else if (fonction.equals("AVC-Intra 100"))
							{		
								caseAS10.setText(language.getProperty("caseAS10").replace("10" + language.getProperty("colon"), "11").replace("10 format" + language.getProperty("colon"), "11 format"));
								caseAS10.setLocation(7, caseForcerResolution.getLocation().y + 17);
								grpAdvanced.add(caseAS10);
							}

							// grpCorrections
							/*caseLimiter.setLocation(7, 14);
							grpCorrections.add(caseLimiter);*/	
							caseBanding.setLocation(7, 14);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 4);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth() -  4, caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 4);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth() -  4, caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);						
							
						} else if ("DNxHD".equals(fonction) || "DNxHR".equals(fonction)
								|| "Apple ProRes".equals(fonction) || "QT Animation".equals(fonction) || ("GoPro CineForm").equals(fonction) || "Uncompressed YUV".equals(fonction) ) {
							addToList.setText(language.getProperty("filesVideoOrPicture"));			
							
							if (comboFonctions.getSelectedItem().equals("QT Animation"))
								caseVisualiser.setEnabled(false);
							else
								caseVisualiser.setEnabled(true);
							
							if (comboFonctions.getSelectedItem().toString().equals("DNxHD") || comboFonctions.getSelectedItem().toString().equals("DNxHR"))
							{
								caseCreateOPATOM.setEnabled(true);
								lblCreateOPATOM.setForeground(new Color(255, 255, 255));
								if ((caseCreateOPATOM.isSelected() || caseCreateTree.isSelected()) && grpDestination.getTabCount() > 2)
								{
									setDestinationTabs(2);		
								}
							}
							else
							{
								caseCreateOPATOM.setEnabled(false);		
								caseCreateOPATOM.setSelected(false);
								lblCreateOPATOM.setForeground(new Color(35, 35, 35));
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
							
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);							
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, 59);
							grpSetTimecode.setVisible(true);
							grpSetTimecode.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(334, grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
							grpSequenceImage.setVisible(true);
							grpSequenceImage.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(334, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(true);
							grpLUTs.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(334, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(334, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							//grpSetAudio
							grpSetAudio.removeAll();
							lblAudio1.setLocation(17, 18);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								&& comboAudio5.getSelectedIndex() == 8
								&& comboAudio6.getSelectedIndex() == 8
								&& comboAudio7.getSelectedIndex() == 8
								&& comboAudio8.getSelectedIndex() == 8)
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
							
							// Ajout case rognage
							caseRognerImage.setLocation(7, caseActiverSequence.getHeight() + caseActiverSequence.getLocation().y);
							grpSequenceImage.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpSequenceImage.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpSequenceImage.add(comboRotate);
							
							// Case Blend location
							caseBlend.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 4);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth() -  4, caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpSequenceImage.add(caseMiror);

							// Ajout des fonctions avancées
							grpCorrections.removeAll();
							grpAdvanced.removeAll();
							
							caseConform.setLocation(7, 14);
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 4, caseConform.getLocation().y + 4);
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
							
							caseForcerInversion.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
							grpAdvanced.add(caseForcerInversion);	
							
							caseForcerDAR.setLocation(7, caseForcerInversion.getLocation().y + 17);
							grpAdvanced.add(caseForcerDAR);
							comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
							grpAdvanced.add(comboDAR);
							caseForcerResolution.setLocation(7, caseForcerDAR.getLocation().y + 17);
							grpAdvanced.add(caseForcerResolution);
							lblResolution.setLocation(caseForcerResolution.getLocation().x + caseForcerResolution.getWidth() + 4, caseForcerResolution.getLocation().y + 3);
							lblPad.setBounds(lblResolution.getLocation().x + lblResolution.getWidth() + 5, lblResolution.getLocation().y, 65, 16);
							grpAdvanced.add(lblPad);
							lblPad.setVisible(true);
							if (lblPad.getText().equals(language.getProperty("lblPad")))
							{
								lblPadLeft.setBackground(Color.black);
								lblPadLeft.setVisible(true);
								lblPadRight.setBackground(Color.black);
								lblPadRight.setVisible(true);
							}
							
							grpAdvanced.add(lblResolution);							

							caseAlpha.setLocation(7, caseForcerResolution.getY() + 17);
							grpAdvanced.add(caseAlpha);								
							caseCreateTree.setLocation(7, caseAlpha.getLocation().y + 17);
							grpAdvanced.add(caseCreateTree);
							caseCreateOPATOM.setLocation(7, caseCreateTree.getLocation().y + 17);
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
							/*caseLimiter.setLocation(7, caseStabilisation.getLocation().y + 17);
							grpCorrections.add(caseLimiter);*/
							caseDeflicker.setLocation(7, caseStabilisation.getLocation().y + 17);
							grpCorrections.add(caseDeflicker);
							caseBanding.setLocation(7, caseDeflicker.getLocation().y + 17);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 4);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth() -  4, caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 4);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth() -  4, caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
							caseExposure.setLocation(7, caseBruit.getLocation().y + 17);
							grpCorrections.add(caseExposure);						
							iconTVExposure.setLocation(289, caseExposure.getLocation().y + 4);
							grpCorrections.add(iconTVExposure);							
							sliderExposure.setLocation(iconTVExposure.getX() - sliderExposure.getWidth() -  4, caseExposure.getLocation().y);
							grpCorrections.add(sliderExposure);
						} else if ("H.264".equals(fonction) || "H.265".equals(fonction)) {

							addToList.setText(language.getProperty("filesVideoOrPicture"));			
							caseVisualiser.setEnabled(true);

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
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
							}
							else {
								caseForcerEntrelacement.setSelected(false);
								caseForcerEntrelacement.setEnabled(false);		
								caseForcePreset.setSelected(false);
								caseForcePreset.setEnabled(false);
								comboForcePreset.setEnabled(false);								
								caseForceTune.setSelected(false);
								caseForceTune.setEnabled(false);
								comboForceTune.setEnabled(false);
								case2pass.setSelected(false);
								case2pass.setEnabled(false);
							}

							if ("H.264".equals(fonction))
							{															
								if (comboForceProfile.getModel().getSize() != 3)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high"}));
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
								if (comboForceProfile.getModel().getSize() != 1)
								{
									comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main"}));
									comboForceProfile.setSelectedIndex(0);
								}

								
								if (comboForceTune.getModel().getSize() != 6)
								{
									comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "grain", "animation", "fastdecode", "zerolatency", "psnr", "ssim" }));
									comboForceTune.setSelectedIndex(0);
								}
							}

							lblNiveaux.setVisible(true);
							grpResolution.setVisible(false);
							grpH264.setVisible(true);

							if (grpAdvanced.getHeight() > 181)
								grpH264.setLocation(334, 59 - (grpAdvanced.getHeight() - 181));
							else
								grpH264.setLocation(334, 59);
							
							if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
								if (lblPad.getText().equals(language.getProperty("lblPad")))
								{
									lblPadLeft.setBackground(Color.black);
									lblPadLeft.setVisible(true);
									lblPadRight.setBackground(Color.black);
									lblPadRight.setVisible(true);
								}
							}
							lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 36, comboH264Taille.getLocation().y + 3, 65, 16);
							grpH264.add(lblPad);

							grpSetTimecode.setVisible(false);
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, grpH264.getSize().height + grpH264.getLocation().y + 6);
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
							//grpSetAudio
							grpSetAudio.removeAll();
							grpSetAudio.add(caseChangeAudioCodec);
							if (comboAudioCodec.getItemCount() != 4 || comboAudioCodec.getSelectedItem().toString().equals("OPUS") || comboAudioCodec.getSelectedItem().toString().equals("OGG"))
							{
								comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
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
							
							lblAudio1.setLocation(17, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								&& comboAudio5.getSelectedIndex() == 8
								&& comboAudio6.getSelectedIndex() == 8
								&& comboAudio7.getSelectedIndex() == 8
								&& comboAudio8.getSelectedIndex() == 8)
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
							
							grpSequenceImage.setVisible(true);
							grpSequenceImage.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(334, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
														
							grpAudio.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(true);
							grpLUTs.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);			
							
							if ("H.264".equals(fonction))
							{
								if (comboColorspace.getItemCount() != 4)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits"}));
							}
							else
							{
								if (comboColorspace.getItemCount() != 6)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits", "Rec. 2020 HLG 10bits HDR"}));
							}
							
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(334, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(334, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							// CalculH264
							if (liste.getSize() > 0 && FFPROBE.calcul == false)
								FFPROBE.CalculH264();
							// Qualité Max
							caseQMax.setEnabled(true);
							
							/*
							//VBR CBR
							lblVBR.setVisible(true);
							if (lblVBR.getText().equals("CQ"))
							{
								taille.setText("-");
								String[] values = new String[53];
								values[0] = language.getProperty("lblBest");
								for (int i = 1 ; i < 52 ; i++)
								{
									values[i] = String.valueOf(i);
								}			
								values[52] = language.getProperty("lblWorst");	

								if (lblDbitVido.getText().equals(language.getProperty("lblDbitVido")))
								{		 
									debitVideo.setModel(new DefaultComboBoxModel<String>(values));
									debitVideo.setSelectedIndex(23);
								}
								lblDbitVido.setText(language.getProperty("lblValue"));
								lblKbsH264.setVisible(false);
								h264lines.setVisible(false);
							}*/
							
							// Ajout case rognage
							caseRognerImage.setLocation(7,
									caseActiverSequence.getHeight() + caseActiverSequence.getLocation().y);
							grpSequenceImage.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpSequenceImage.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpSequenceImage.add(comboRotate);
							
							// Case Blend location
							caseBlend.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 4);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth() -  4, caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpSequenceImage.add(caseMiror);

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
							lblNiveaux.setLocation(caseForceOutput.getLocation().x + caseForceOutput.getWidth() + 4,
									caseForceOutput.getLocation().y + 4);
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
							
							caseConform.setLocation(7, caseGOP.getLocation().y + 17);
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);
							
							// grpCorrections
							caseBanding.setLocation(7, 14);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 4);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth() -  4, caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 4);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth() -  4, caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
						} else if ("WMV".equals(fonction) || "MPEG".equals(fonction) || "VP9".equals(fonction) || "AV1".equals(fonction) || "OGV".equals(fonction)
								|| "MJPEG".equals(fonction) || "Xvid".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideoOrPicture"));	
							caseVisualiser.setEnabled(true);
							case2pass.setEnabled(true);
							lblNiveaux.setVisible(true);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(true);
							grpH264.setLocation(334, 59);
							
							if ("VP9".equals(fonction) || "AV1".equals(fonction))
							{			
								lblVBR.setVisible(true);
								
								if (lblVBR.getText().equals("CQ"))
								{
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
							}
							else
							{
								lblVBR.setVisible(false);
								lblVBR.setText("VBR");
								case2pass.setEnabled(true);
							}
							
							if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
								lblPad.setVisible(false);
							else
							{
								lblPad.setVisible(true);
								if (lblPad.getText().equals(language.getProperty("lblPad")))
								{
									lblPadLeft.setBackground(Color.black);
									lblPadLeft.setVisible(true);
									lblPadRight.setBackground(Color.black);
									lblPadRight.setVisible(true);
								}
							}
							lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 36, comboH264Taille.getLocation().y + 3, 65, 16);
							grpH264.add(lblPad);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, grpH264.getSize().height + grpH264.getLocation().y + 6);
							
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
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
								else if (comboAudioCodec.getModel().getElementAt(0).equals("OPUS") == false && ("VP9".equals(fonction) || "AV1".equals(fonction)))
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
							
							lblAudio1.setLocation(17, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								&& comboAudio5.getSelectedIndex() == 8
								&& comboAudio6.getSelectedIndex() == 8
								&& comboAudio7.getSelectedIndex() == 8
								&& comboAudio8.getSelectedIndex() == 8)
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
							
							grpSequenceImage.setVisible(true);
							grpSequenceImage.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(334, grpSequenceImage.getSize().height + grpSequenceImage.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);												
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);

							grpAudio.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(true);
							grpLUTs.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							
							if ("VP9".equals(fonction) || "AV1".equals(fonction))
							{
								if (comboColorspace.getItemCount() != 6)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits", "Rec. 2020 HLG 10bits HDR"}));							
							}
							else
							{
								if (comboColorspace.getItemCount() != 3)
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							}
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(334, grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(334, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(334, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
							
							// CalculH264
							if (liste.getSize() > 0 && FFPROBE.calcul == false)
								FFPROBE.CalculH264();
							// Qualité Max
							if (comboFonctions.getSelectedItem().equals("OGV")
									|| comboFonctions.getSelectedItem().equals("MJPEG"))
								caseQMax.setEnabled(false);
							else
								caseQMax.setEnabled(true);
							
							/*
							//VBR CBR
							lblVBR.setVisible(true);
							if (lblVBR.getText().equals("CQ"))
							{
								taille.setText("-");
								String[] values = new String[53];
								values[0] = language.getProperty("lblBest");
								for (int i = 1 ; i < 52 ; i++)
								{
									values[i] = String.valueOf(i);
								}			
								values[52] = language.getProperty("lblWorst");			
								
								if (lblDbitVido.getText().equals(language.getProperty("lblDbitVido")))
								{		 
									debitVideo.setModel(new DefaultComboBoxModel<String>(values));
									debitVideo.setSelectedIndex(23);
								}
								lblDbitVido.setText(language.getProperty("lblValue"));
								lblKbsH264.setVisible(false);
								h264lines.setVisible(false);
							}
							else if (lblDbitVido.getText().equals(language.getProperty("lblValue")))
							{
								debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "2500", "2000", "1500", "1000", "500" }));
								debitVideo.setSelectedIndex(8);
								lblDbitVido.setText(language.getProperty("lblDbitVido"));
								lblKbsH264.setVisible(true);
								h264lines.setVisible(true);
							}*/
							
							// Ajout case rognage
							caseRognerImage.setLocation(7,
									caseActiverSequence.getHeight() + caseActiverSequence.getLocation().y);
							grpSequenceImage.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpSequenceImage.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpSequenceImage.add(comboRotate);

							// Case Blend location
							caseBlend.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
							iconTVBlend.setLocation(289, caseBlend.getLocation().y + 4);
							sliderBlend.setLocation(iconTVBlend.getX() - sliderBlend.getWidth() -  4, caseBlend.getLocation().y);
							
							// Case Motion Blur
							caseMotionBlur.setLocation(7, caseBlend.getHeight() + caseBlend.getLocation().y);
							
							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpSequenceImage.add(caseMiror);
							
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
																						
							if ("VP9".equals(fonction))
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
								caseConform.setLocation(7, caseGOP.getY() + 17);		
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
								caseConform.setLocation(7, caseGOP.getY() + 17);		
							}	
							else
							{
								caseConform.setLocation(7, caseForcerDesentrelacement.getY() + 17);
							}
								
							grpAdvanced.add(caseConform);
							comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboConform);							
							lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y);
							grpAdvanced.add(lblToConform);							
							comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 4, caseConform.getLocation().y + 4);
							grpAdvanced.add(comboFPS);
							lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
							grpAdvanced.add(lblIsConform);

							// grpCorrections
							caseBanding.setLocation(7, 14);
							grpCorrections.add(caseBanding);
							caseDetails.setLocation(7, caseBanding.getLocation().y + 17);
							grpCorrections.add(caseDetails);
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 4);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth() -  4, caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 4);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth() -  4, caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
						} else if ("DV PAL".equals(fonction)) {
							addToList.setText(language.getProperty("filesVideo"));
							caseVisualiser.setEnabled(true);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setVisible(true);
							grpInAndOut.setLocation(334, 59);
							
							// Ajout de la partie Affichage
							grpOverlay.removeAll();
							
							// grpOverlay
							grpOverlay.setVisible(true);
							grpOverlay.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
						} else if ("DVD".equals(fonction) || "Blu-ray".equals(fonction)) {
							
							addToList.setText(language.getProperty("filesVideo"));
							if (comboFonctions.getSelectedItem().equals("DVD"))
								caseVisualiser.setEnabled(false);
							else
								caseVisualiser.setEnabled(true);
							caseForcerProgressif.setEnabled(true);
							grpSequenceImage.setVisible(false);
							grpFiltreImage.setVisible(false);
							grpLUTs.setVisible(false);
							grpResolution.setVisible(false);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							if ("Blu-ray".equals(fonction))
							{
								grpH264.setVisible(true);
								grpH264.setLocation(334, 59);
								
								if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
									lblPad.setVisible(false);
								else
								{
									lblPad.setVisible(true);
									if (lblPad.getText().equals(language.getProperty("lblPad")))
									{
										lblPadLeft.setBackground(Color.black);
										lblPadLeft.setVisible(true);
										lblPadRight.setBackground(Color.black);
										lblPadRight.setVisible(true);
									}
								}
								lblPad.setBounds(comboH264Taille.getLocation().x + comboH264Taille.getWidth() + 36, comboH264Taille.getLocation().y + 3, 65, 16);
								grpH264.add(lblPad);

								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(334, grpH264.getSize().height + grpH264.getLocation().y + 6);
							}
							else
							{
								grpInAndOut.setVisible(true);
								grpInAndOut.setLocation(334, 59);
							}
							
							
							grpSetAudio.setVisible(true);
							if (action)
								grpSetAudio.setSize(312, 17);
							grpSetAudio.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);
							
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
							
							lblAudio1.setLocation(17, caseChangeAudioCodec.getLocation().y + caseChangeAudioCodec.getHeight() + 7);
							comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
							grpSetAudio.add(lblAudio1);
							grpSetAudio.add(comboAudio1);
							lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 17, lblAudio1.getLocation().y);
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
								&& comboAudio5.getSelectedIndex() == 8
								&& comboAudio6.getSelectedIndex() == 8
								&& comboAudio7.getSelectedIndex() == 8
								&& comboAudio8.getSelectedIndex() == 8)
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
							grpOverlay.setLocation(334, grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
							caseAddOverlay.setLocation(7, 16);	
							grpOverlay.add(caseAddOverlay);											
							caseSubtitles.setLocation(7, caseAddOverlay.getHeight() + caseAddOverlay.getLocation().y);
							grpOverlay.add(caseSubtitles);
							caseLogo.setLocation(7, caseSubtitles.getHeight() + caseSubtitles.getLocation().y);
							grpOverlay.add(caseLogo);
							
							grpAudio.setVisible(false);
							grpCorrections.setVisible(true);
							grpCorrections.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							grpTransitions.setVisible(true);
							grpTransitions.setLocation(334, grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
							grpAdvanced.setVisible(true);
							grpAdvanced.setLocation(334, grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
							btnReset.setLocation(334, grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	;

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
							iconTVDetails.setLocation(289, caseDetails.getLocation().y + 4);
							grpCorrections.add(iconTVDetails);
							sliderDetails.setLocation(iconTVDetails.getX() - sliderDetails.getWidth() -  4, caseDetails.getLocation().y);
							grpCorrections.add(sliderDetails);
							caseBruit.setLocation(7, caseDetails.getLocation().y + 17);
							grpCorrections.add(caseBruit);
							iconTVBruit.setLocation(289, caseBruit.getLocation().y + 4);
							grpCorrections.add(iconTVBruit);
							sliderBruit.setLocation(iconTVBruit.getX() - sliderBruit.getWidth() -  4, caseBruit.getLocation().y);
							grpCorrections.add(sliderBruit);
						} else if (language.getProperty("functionPicture").equals(fonction) || "JPEG".equals(fonction)) {
							addToList.setText(language.getProperty("filesVideoOrPicture"));
							caseVisualiser.setEnabled(false);
							grpSequenceImage.setVisible(false);							
							grpResolution.setVisible(true);
							grpResolution.setLocation(334, 59);
							// Ajout case rognage
							caseRognerImage.setLocation(7, 47);
							grpResolution.add(caseRognerImage);

							// Ajout case rotation
							caseRotate.setLocation(7 , caseRognerImage.getLocation().y + caseRognerImage.getHeight());
							grpResolution.add(caseRotate);
							comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
									caseRotate.getLocation().y + 3);
							grpResolution.add(comboRotate);
							
							// lblInterpretation location
							lblInterpretation.setLocation(28, caseCreateSequence.getLocation().y + caseCreateSequence.getHeight());
							comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4, lblInterpretation.getLocation().y);
							lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblInterpretation.getLocation().y - 1);
							iconTVInterpret.setLocation(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY() + 1);

							// Ajout case miroir
							caseMiror.setLocation(comboRotate.getWidth() + comboRotate.getLocation().x + 6,
									caseRotate.getLocation().y);
							grpResolution.add(caseMiror);

							caseCreateSequence.setEnabled(true);
							grpH264.setVisible(false);
							grpSetTimecode.setVisible(false);
							
							grpInAndOut.setLocation(334, grpResolution.getSize().height + grpResolution.getLocation().y + 6);
							
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
							grpOverlay.setLocation(334, grpInAndOut.getSize().height + grpInAndOut.getLocation().y + 6);	
												
							// grpLuts
							grpLUTs.setVisible(true);
							grpLUTs.setLocation(334, grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
							grpLUTs.setSize(grpLUTs.getWidth(), 143);
							
							if (comboColorspace.getItemCount() != 3)
								comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
							
							grpSetAudio.setVisible(false);
							grpAudio.setVisible(false);
							grpInAndOut.setVisible(true);
							grpFiltreImage.setVisible(true);
							grpFiltreImage.setLocation(334,	grpLUTs.getSize().height + grpLUTs.getLocation().y + 6);
							grpCorrections.setVisible(false);
							grpTransitions.setVisible(false);
							grpAdvanced.setVisible(false);
							btnReset.setLocation(334, grpFiltreImage.getSize().height + grpFiltreImage.getLocation().y + 6);
						} else {
							if (language.getProperty("functionConform").equals(fonction) || language.getProperty("functionExtract").equals(fonction)) 
								addToList.setText(language.getProperty("filesVideo"));
							else if (language.getProperty("functionBab").equals(fonction))
								addToList.setText(language.getProperty("filesVideoOrAudio"));
							else if (language.getProperty("functionSubtitles").equals(fonction))
								addToList.setText(language.getProperty("fileVideo"));
							else if (language.getProperty("functionSceneDetection").equals(fonction))
								addToList.setText(language.getProperty("fileVideo"));
							else if (language.getProperty("itemMyFunctions").equals(fonction))
								addToList.setText(language.getProperty("dropFilesHere"));
							else if (comboFonctions.getEditor().getItem().toString().isEmpty())
								addToList.setText(language.getProperty("dropFilesHere"));
							else
								addToList.setText(language.getProperty(""));
							
							caseVisualiser.setEnabled(false);
						}
						
						//Rafrachissement
						grpAdvanced.repaint();
						
						if (action) {
							int i2 = 680;
							long start = (System.currentTimeMillis());
							int fps = 0;
							int sleep = 1;
							do {
								changeGroupes = true;
								i2 -= 2;
								grpResolution.setLocation(i2, grpResolution.getLocation().y);
								grpH264.setLocation(i2, grpH264.getLocation().y);
								grpSetTimecode.setLocation(i2, grpSetTimecode.getLocation().y);
								grpOverlay.setLocation(i2, grpOverlay.getLocation().y);
								grpSetAudio.setLocation(i2, grpSetAudio.getLocation().y);
								grpAudio.setLocation(i2, grpAudio.getLocation().y);
								grpInAndOut.setLocation(i2, grpInAndOut.getLocation().y);
								grpSequenceImage.setLocation(i2, grpSequenceImage.getLocation().y);
								grpFiltreImage.setLocation(i2, grpFiltreImage.getLocation().y);
								grpLUTs.setLocation(i2, grpLUTs.getLocation().y);
								grpCorrections.setLocation(i2, grpCorrections.getLocation().y);
								grpTransitions.setLocation(i2, grpTransitions.getLocation().y);
								grpAdvanced.setLocation(i2, grpAdvanced.getLocation().y);
								btnReset.setLocation(i2, btnReset.getLocation().y);

								Thread.sleep(sleep);

								// Permet de définir la vitesse
								if ((System.currentTimeMillis() - start) < 350)
									fps += 1;
								else {
									if (fps > 30)
										sleep = (int) (sleep / (fps / 25));
									else if (fps < 20)
										sleep = (int) (sleep * (fps / 25));

									start = System.currentTimeMillis();
									fps = 0;
								}
							} while (i2 > 334);
							changeGroupes = false;
							changeGroupes(false); // une fois l'action terminé on vérifie que les groupes correspondent
						}

						// Important
						panelHaut.repaint();
						statusBar.repaint();
						
					} catch (Exception e1) {
					}
				}
			}
		});
		changeSize.start();

	}

	public static void changementDeFiltre() {

		if (liste.getSize() > 0) {
			if (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getEditor().getItem().toString().contains("ffmpeg"))
			{
				lblFilter.setText("Ext." + language.getProperty("colon"));
				
				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg") == false) 
				{
					String[] extensions = new String[] { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts", ".m2ts" };
					
					if (comboFonctions.getSelectedItem().toString().equals("H.265") && caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
						extensions = new String[] {".mkv"};	
					
					DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(0);
					}
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform"))) {
				
				lblFilter.setText(Shutter.language.getProperty("lblTo"));
				final String filtres[] = {"23,976 i/s", "24 i/s", "25 i/s", "29,97 i/s", "30 i/s", "48 i/s", "50 i/s", "59,94 i/s", "60 i/s" };				
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
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHD")) {

				lblFilter.setText("Type" + language.getProperty("colon"));
				DefaultComboBoxModel<Object> model;
				if (caseForcerResolution.isSelected() && lblResolution.getSelectedItem().toString().equals("1280x720")) {
					String filtres[] = { "60", "90", "90 X", "180", "180 X" };
					model = new DefaultComboBoxModel<Object>(filtres);
					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(0);
					}
				} else {
					String filtres[] = { "36", "120", "185", "185 X", "75", "240", "365", "365 X", "80", "115", "175",
							"175 X" };
					model = new DefaultComboBoxModel<Object>(filtres);

					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(1);
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
				String filtres[] = { "Low", "Medium", "High", "Film Scan", "Film Scan 2", "Film Scan 3"};
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
				String filtres[] = { ".png", ".tif", ".tga", ".dpx", ".bmp", ".ico", ".webp" };
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
				
			} else if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg") == false) // Si liste > 0 mais
																									// ne correspond à
																									// aucun d'avant et
																									// qu'elle ne
																									// contient pas
																									// "ffmpeg"
			{
				lblFilter.setText(language.getProperty("lblFilter"));
				final String filtres[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv",
						".mp4", ".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(0);
			}
		} else if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg") == false) // Si la liste = 0 et
																								// qu'elle ne contient
																								// pas "ffmpeg"
		{
			lblFilter.setText(language.getProperty("lblFilter"));
			final String filtres[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
					".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp" };
			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
			comboFilter.setModel(model);
			comboFilter.setSelectedIndex(0);
		}
	}

	private static void setDestinationTabs(int tabs) {	
		
		//Affichage des titres
		Font tabFont = new Font("Montserrat", Font.PLAIN, 11);		
		if (getLanguage.equals("English"))
			tabFont = new Font("Montserrat", Font.PLAIN, 10);		
		
		JLabel output = new JLabel(language.getProperty("output"));
		output.setFont(tabFont);
		JLabel output1 = new JLabel(language.getProperty("output") + "1");
		output1.setFont(tabFont);
		JLabel output2 = new JLabel(language.getProperty("output") + "2");
		output2.setFont(tabFont);
		JLabel output3 = new JLabel(language.getProperty("output") + "3");
		output3.setFont(tabFont);		
		JLabel wetransferTab = new JLabel("WeTransfer");
		wetransferTab.setFont(tabFont);		
		JLabel ftpTab = new JLabel("FTP");
		ftpTab.setFont(tabFont);		
		JLabel mailTab = new JLabel("Mail");
		mailTab.setFont(tabFont);

		if (grpDestination.getTabCount() > 1)
		{
			do {		
				grpDestination.removeTabAt(1);
			} while (grpDestination.getTabCount() > 1);
		}
		
		
		//Ajout des titres
		if (tabs == 1)
		{
			grpDestination.setTabComponentAt(0, output);
		}
		else if (tabs == 2)
		{
			grpDestination.addTab(language.getProperty("output"), destination1);
			grpDestination.addTab("Mail", destinationMail);	
			
			grpDestination.setTabComponentAt(0, output);
			grpDestination.setTabComponentAt(1, mailTab);
		}
		else if (tabs == 6)
		{			
			grpDestination.addTab(language.getProperty("output") + "1", destination1);
			grpDestination.addTab(language.getProperty("output") + "2", destination2);
			grpDestination.addTab(language.getProperty("output") + "3", destination3);	
			grpDestination.addTab("WeTransfer", new JPanel());
			grpDestination.addTab("FTP", new JPanel());
			grpDestination.addTab("Mail", destinationMail);	
			
			grpDestination.setTabComponentAt(0, output1);
			grpDestination.setTabComponentAt(1, output2);
			grpDestination.setTabComponentAt(2, output3);
			grpDestination.setTabComponentAt(3, wetransferTab);
			grpDestination.setTabComponentAt(4, ftpTab);
			grpDestination.setTabComponentAt(5, mailTab);
		}
	}
	
	private void setShadow() {
		shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
		shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
		shadow.setUndecorated(true);
		shadow.setContentPane(new ShutterShadow());
		shadow.setBackground(new Color(255, 255, 255, 0));

		shadow.setFocusableWindowState(false);
		
		shadow.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent down) {
				frame.toFront();
			}
    		
    	});

		frame.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
			}

			public void componentResized(ComponentEvent e2) {
				shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
			}
		});

	}

	public static void disableAll() {

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChoixDesFichiers.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(false);
			}
			listeDeFichiers.setEnabled(false);
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
		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSequenceImage.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpFiltreImage.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpLUTs.getComponents();
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
		lblCreateOPATOM.setForeground(new Color(35, 35, 35));
		
		components = grpH264.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}

		lblFichiers.setEnabled(true);
		lblTermine.setEnabled(true);

		comboFonctions.setEnabled(false);
		comboFilter.setEnabled(false);
		btnReset.setEnabled(false);

		lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
		lblTermine.setVisible(true);
		cancelled = false;
		btnAnnuler.setEnabled(true);

		progressBar1.setValue(0);

		if (FFMPEG.isRunning)
		{		
			if (caseForcerDAR.isSelected() == false && caseAddOverlay.isSelected() == false && caseLUTs.isSelected() == false && comboInColormatrix.getSelectedItem().toString().equals("HDR") == false && caseVisualiser.isSelected() == false && caseDeflicker.isSelected() == false 
					|| grpLUTs.isVisible() == false && grpCorrections.isVisible() == false && grpTransitions.isVisible() == false && grpAdvanced.isVisible() == false
					|| caseVisualiser.isSelected())
			{
				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().toString().equals("JPEG") == false)
					progressBar1.setValue(0);
				if (caseVisualiser.isSelected() == false)
					caseRunInBackground.setEnabled(true);
	
				caseVisualiser.setEnabled(false);
				btnStart.setEnabled(true);
				btnStart.setText(language.getProperty("btnPauseFunction"));
			} 
			else
			{	
				btnStart.setEnabled(false);
				caseVisualiser.setEnabled(false);
			}
		}
		
		frame.repaint();
	}

	public static void enableAll() {

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChoixDesFichiers.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(true);
			}
			listeDeFichiers.setEnabled(true);
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
		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComboBox == false)
				components[i].setEnabled(true);
		}		
		
		if (caseCreateSequence.isSelected())
			comboInterpret.setEnabled(true);
		
		components = grpSequenceImage.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseActiverSequence.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}
		if (caseActiverSequence.isSelected() == false)
			caseSequenceFPS.setEnabled(false);
		
		components = grpFiltreImage.getComponents();
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

		components = grpLUTs.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		if (caseLUTs.isSelected() == false)
			comboLUTs.setEnabled(false);
		if (caseColorspace.isSelected() == false)
			comboColorspace.setEnabled(false);
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
		
		if (caseSetTimecode.isSelected() == false)
			caseIncrementTimecode.setEnabled(false);
			
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
		
		if (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov"))
			caseFastStart.setEnabled(true);
		else
			caseFastStart.setEnabled(false);	
		
		if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("422")))
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
		btnAnnuler.setEnabled(false);
		changementDeFonction(false);

		// Important
		panelHaut.repaint();
		statusBar.repaint();
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public static void FinDeFonction() {		
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
			
			WebScrollPane scrollPane = new WebScrollPane(errorText);  
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false); 
			scrollPane.setPreferredSize( new Dimension( 500, 400 ) );
			
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(errorList.toString()), null);			

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
	            
			errorList.setLength(0);
		}
						
		enableAll();

		if (cancelled == true) {
			lblEncodageEnCours.setForeground(Color.RED);
			lblEncodageEnCours.setText(language.getProperty("processCancelled"));
			progressBar1.setValue(0);
		} else {
			lblEncodageEnCours.setText(language.getProperty("processEnded"));
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
		lastAction();
	}

	public static void lastAction() {
		
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

}// Fin de la classe

// Modifications de la liste de fichiers
@SuppressWarnings({ "serial", "rawtypes" })
class FilesCellRenderer extends JLabel implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setText(value.toString());

		ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("contents/liste.png"));
		setIcon(imageIcon);

		setFont(new Font("SansSerif", Font.PLAIN, 12));
		setForeground(Color.BLACK);
		
		if (isSelected) {
			setBackground(new Color(215, 215, 215));
			setBorder(new LineBorder(new Color(129, 198, 253)));
			setOpaque(true);
		} else {
			setBorder(new LineBorder(new Color(204,204,204,0)));
			setOpaque(false);
		}
		return this;
	}

}

// Modifications de la liste de fonctions
@SuppressWarnings("serial")
class ComboBoxRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value.toString().contains(":") || value.toString().equals(Shutter.language.getProperty("itemMyFunctions")))
		{
			if (Settings.comboTheme.getSelectedItem().toString().equals(Shutter.language.getProperty("clearTheme")))
				setForeground(Color.BLACK);
			else
				setForeground(Color.WHITE);
			
			setFont(new Font("Montserrat", Font.BOLD, 12));
		}
		else
		{
			if (Settings.comboTheme.getSelectedItem().toString().equals(Shutter.language.getProperty("clearTheme")))
				setForeground(Color.BLACK);
			else
				setForeground(Color.WHITE);
			
			setFont(new Font("FreeSans", Font.PLAIN, 12));
		}

		list.setFixedCellHeight(18);
		
		setOpaque(false);
				
		return this;
	}
}

// Drag & Drop listeDeFichiers
@SuppressWarnings("serial")
class ListeFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.scanIsRunning == false
					&& Shutter.comboFonctions.getSelectedItem().equals("DVD RIP") == false && Shutter.comboFonctions
							.getSelectedItem().equals(Shutter.language.getProperty("functionWeb")) == false) {

				Shutter.listeDeFichiers.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
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
						&& Shutter.comboFonctions.getSelectedItem().equals("DVD RIP") == false && Shutter.comboFonctions
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
							
							if (file != null) {
								if (Shutter.caseChangeFolder1.isSelected()) {
									Shutter.scanIsRunning = true;
									Shutter.changementDeFiltre();
								} 
								else
									JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);				
							}
														
							break;
						}
						else
						{
							if (file.isFile() && file.getName().contains(".")) {
								int s = file.getCanonicalPath().toString().lastIndexOf('.');
								String ext = file.getCanonicalFile().toString().substring(s);
								if (ext.equals(".enc")) {
									Utils.loadSettings(new File (file.getCanonicalPath().toString()));
								} else if (ext.toLowerCase().equals(Shutter.comboFilter.getSelectedItem().toString())
										|| Shutter.comboFilter.getSelectedItem().toString()
												.equals(Shutter.language.getProperty("aucun"))
										|| Shutter.lblFilter.getText()
												.equals(Shutter.language.getProperty("lblFilter")) == false) {
									if (file.isHidden() == false)
										Shutter.liste.addElement(file.getCanonicalPath().toString());
								}
							} else {
								Utils.FileFinder(file.getCanonicalPath().toString());
							}
						}

						Shutter.addToList.setVisible(false);
						Shutter.lblFichiers.setText(Utils.nombreDeFichiers());

					}

					switch (Shutter.comboFonctions.getSelectedItem().toString()) {
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
							Shutter.lblFichiers.setText(Utils.nombreDeFichiers());
						}
						break;
					}

					// VideoPlayer
					VideoPlayer.setMedia();
					
					// Filtre
					Shutter.changementDeFiltre();

					// Border
					Shutter.listeDeFichiers.setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

// Drag & Drop lblDestination1
@SuppressWarnings("serial")
class DestinationFileTransferHandler1 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				Shutter.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
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
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							Shutter.lblDestination1.setText(file.getParent());
						} else {
							Shutter.lblDestination1.setText(file.getAbsolutePath());
						}

						//Si destination identique à l'une des autres
						if (Shutter.lblDestination1.getText().equals(Shutter.lblDestination2.getText()) || Shutter.lblDestination1.getText().equals(Shutter.lblDestination3.getText())) 
						{
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
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
							if (Shutter.liste.firstElement().substring(0, Shutter.liste.firstElement().length() - 1).equals(Shutter.lblDestination1.getText())) {
								JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"),Shutter.language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
								Shutter.caseChangeFolder1.setSelected(false);
							} else {
								Shutter.scanIsRunning = true;
							}
						}
						
						if (Shutter.lblDestination1.getText() != Shutter.language.getProperty("sameAsSource") && Settings.lastUsedOutput1.isSelected())
							Settings.lblDestination1.setText(Shutter.lblDestination1.getText());
					}

					// Border
					Shutter.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

//Drag & Drop lblDestination2
@SuppressWarnings("serial")
class DestinationFileTransferHandler2 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				Shutter.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
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
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							Shutter.lblDestination2.setText(file.getParent());
						} else {
							Shutter.lblDestination2.setText(file.getAbsolutePath());
						}
						
						//Si destination identique à l'une des autres
						if (Shutter.lblDestination2.getText().equals(Shutter.lblDestination1.getText()) || Shutter.lblDestination2.getText().equals(Shutter.lblDestination3.getText())) 
						{
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
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

					// Border
					Shutter.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

//Drag & Drop lblDestination3
@SuppressWarnings("serial")
class DestinationFileTransferHandler3 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.caseChangeFolder3.isEnabled()) {
				Shutter.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
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
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							Shutter.lblDestination3.setText(file.getParent());
						} else {
							Shutter.lblDestination3.setText(file.getAbsolutePath());
						}
						
						//Si destination identique à l'une des autres
						if (Shutter.lblDestination3.getText().equals(Shutter.lblDestination1.getText()) || Shutter.lblDestination3.getText().equals(Shutter.lblDestination2.getText())) 
						{
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
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

					// Border
					Shutter.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

// Ombre
@SuppressWarnings("serial")
class ShutterShadow extends JPanel {
	public void paintComponent(Graphics g) {
		RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Graphics2D g1 = (Graphics2D) g.create();
		g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
		g1.setRenderingHints(qualityHints);
		g1.setColor(new Color(0, 0, 0));
		g1.fillRect(0, 0, Shutter.frame.getWidth() + 14, Shutter.frame.getHeight() + 7);

		for (int i = 0; i < 7; i++) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHints(qualityHints);
			g2.setColor(new Color(0, 0, 0, i * 10));
			g2.drawRoundRect(i, i, Shutter.frame.getWidth() + 13 - i * 2, Shutter.frame.getHeight() + 7, 20, 20);
		}
	}
}

//Skin
class ClearSkin extends XmlSkin
{
    public ClearSkin ()
    {
        super (ClearSkin.class, "ClearSkin.xml");
    }
}

//DarkSkin
class DarkSkin extends XmlSkin
{
  public DarkSkin ()
  {
	  super (DarkSkin.class, "DarkSkin.xml");
  }
}
