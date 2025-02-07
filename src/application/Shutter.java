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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
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
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;

import functions.AudioEncoders;
import functions.AudioNormalization;
import functions.BlackDetection;
import functions.Command;
import functions.Conform;
import functions.DVDRIP;
import functions.Extract;
import functions.FrameMD5;
import functions.LoudnessTruePeak;
import functions.Merge;
import functions.OfflineDetection;
import functions.Picture;
import functions.ReplaceAudio;
import functions.Rewrap;
import functions.VMAF;
import functions.VideoEncoders;
import functions.VideoInserts;
import library.BMXTRANSWRAP;
import library.DCRAW;
import library.DVDAUTHOR;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.MEDIAINFO;
import library.NCNN;
import library.SEVENZIP;
import library.TSMUXER;
import library.YOUTUBEDL;
import settings.Colorimetry;
import settings.Corrections;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;

@SuppressWarnings("serial")
public class Shutter {

	/*
	 * Initialisation
	 */
	public static String actualVersion = "18.7";
	public static String getLanguage = "";
	public static String arch = "x86_64";
	public static long availableMemory;
	public static String pathToFont = "JRE/lib/fonts/Montserrat.ttf";
	public static String magnetoFont = "Magneto";
	public static String montserratFont = "Montserrat";
	public static String freeSansFont = "FreeSans";
	public static File documents = new File(System.getProperty("user.home") + "/Shutter Encoder");
	public static File settingsXML = new File(Shutter.documents + "/settings.xml");
	public static String dirTemp = System.getProperty("java.io.tmpdir");
	public static File lutsFolder;
	public static File subtitlesFile;
	public static Properties language = new Properties();
	public static URL soundURL;
	public static URL soundErrorURL;
	public static JFrame frame = new JFrame();
	public static int extendedWidth = 1350;
	public static boolean noSettings = true;
	public static boolean showDonateWindow = false;
	public static int taskBarHeight;
	public static boolean cancelled = false;
	public static float ratioFinal = 0; // CropVideo
	public static String colorimetryValues = null; // ColorImage
	public static boolean scanIsRunning = false;
	public static JMenuItem menuDisplay;
	public static JMenuItem inputDevice;
	public static JMenuItem informations;
	public static boolean inputDeviceIsRunning = false;
	public static boolean overlayDeviceIsRunning = false;
	public static boolean sendMailIsRunning = false;
	protected static boolean canScroll = true;
	public static JMenuItem scan;
	static ArrayList<String> droppedFiles = new ArrayList<String>(); // Drop file application
	public static boolean saveCode = false;
	protected static boolean copyFileIsRunning = false;
	protected static boolean subtitlesBurn = true;
    public static boolean autoBurn = false;
    public static boolean autoEmbed = false;
    public static boolean cutKeyframesIsDisplayed = false;
    public static boolean rewrapKeyframesIsDisplayed = false;
    public static boolean conformKeyframesIsDisplayed = false;
	public static StringBuilder errorList = new StringBuilder();
	public static NumberFormat formatter = new DecimalFormat("00");
	public static NumberFormat formatterToMs = new DecimalFormat("000");

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
	private static JLabel fullscreen;
	private static JLabel reduce;
	private static JLabel help;
	private static JLabel newInstance;
	
	private static ImageIcon mailIcon;
	private static ImageIcon streamIcon;

	public static DefaultListModel<String> liste = new DefaultListModel<String>();
	protected static PopupMenu dropFiles;
	public static JList<String> fileList;
	static JLabel addToList = new JLabel();
	public static JComboBox<String[]> comboFonctions;
	public static String[] functionsList;

	protected static JButton btnBrowse;
	protected static JButton btnEmptyList;
	protected static JComboBox<Object> comboFilter;
	protected static JComboBox<Object> comboLUTs;
	protected static JComboBox<Object> comboGamma;
	protected static JComboBox<Object> comboInLevels;
	protected static JComboBox<Object> comboOutLevels;
	protected static JComboBox<Object> comboInColormatrix;
	protected static JComboBox<Object> comboOutColormatrix;
	protected static JComboBox<Object> comboColorspace;
	protected static JLabel lblHDR;
	protected static JComboBox<String> comboHDRvalue;
	protected static JComboBox<String> comboCLLvalue;
	protected static JComboBox<String> comboFALLvalue;
	protected static JButton btnLUTs;
	protected static JButton btnStart;
	protected static JButton btnCancel;
	protected static JCheckBox caseOpenFolderAtEnd1;
	protected static JCheckBox caseOpenFolderAtEnd2;
	protected static JCheckBox caseOpenFolderAtEnd3;
	protected static JCheckBox caseChangeFolder1;
	protected static JCheckBox caseChangeFolder2;
	protected static JCheckBox caseChangeFolder3;
	protected static JCheckBox btnExtension;
	protected static JTextField txtExtension;
	protected static JCheckBox caseSubFolder;
	protected static JTextField txtSubFolder;
	protected static JCheckBox caseDeleteSourceFile;
	protected static JCheckBox caseRunInBackground;
	protected static JCheckBox caseDisplay;
	protected static JLabel iconTVInterpret;
	protected static JLabel iconList;
	protected static JLabel iconPresets;
	protected static JComboBox<String> comboResolution;
	protected static JLabel lblImageQuality;
	protected static JComboBox<String> comboImageOption;
	protected static JCheckBox caseRotate;
	protected static JComboBox<String> comboRotate;
	protected static JCheckBox caseMiror;
	protected static JCheckBox btnNoUpscale;
	public static JCheckBox caseCreateSequence;
	protected static JLabel lblInterpretation;
	protected static JLabel lblIsInterpret;
	public static JComboBox<String> comboInterpret;
	protected static JCheckBox case2pass;
	protected static JCheckBox caseQMax;
	public static JCheckBox caseEnableSequence;
	protected static JCheckBox caseYear;
	protected static JCheckBox caseMonth;
	protected static JCheckBox caseDay;
	protected static JCheckBox caseFrom;
	protected static JComboBox<String> comboYear;
	protected static JComboBox<String> comboMonth;
	protected static JComboBox<String> comboDay;
	protected static JComboBox<String> comboFrom;
	protected static JComboBox<String> comboTo;
	protected static JCheckBox caseLUTs;
	protected static JCheckBox caseGamma;
	protected static JCheckBox caseLevels;
	protected static JCheckBox caseColormatrix;
	protected static JCheckBox caseColorspace;
	public static JComboBox<String> caseSequenceFPS;
	protected static JCheckBox caseSetTimecode;
	protected static JCheckBox caseIncrementTimecode;
	protected static JCheckBox caseGenerateFromDate;
	protected static JTextField TCset1;
	protected static JTextField TCset2;
	protected static JTextField TCset3;
	protected static JTextField TCset4;
	protected static JCheckBox caseNormalizeAudio;
	protected static JCheckBox caseChangeAudioCodec;
	protected static JCheckBox caseAudioOffset;
	protected static JCheckBox caseKeepSourceTracks;
	protected static JCheckBox caseSampleRate;
	protected static JCheckBox caseMixAudio;
	protected static JCheckBox caseSplitAudio;
	protected static JCheckBox caseConvertAudioFramerate;
	protected static JLabel lblFromTo;
	protected static JLabel lblAudioIs;
	protected static JLabel lblSplit;
	protected static JLabel lblMix;
	protected static JCheckBox caseOpenGop;
	protected static JCheckBox caseForcerProgressif;
	protected static JCheckBox caseForcerEntrelacement;
	protected static JCheckBox caseForcerInversion;
	protected static JCheckBox caseForcerDesentrelacement;
	protected static JComboBox<String> comboForcerDesentrelacement;
	protected static JCheckBox caseForceOutput;
	protected static JCheckBox caseFastStart;
	protected static JCheckBox caseFastDecode;
	protected static JCheckBox caseAlpha;
	protected static JCheckBox caseGOP;
	protected static JTextField gopSize;
	protected static JCheckBox caseFilmGrain;
	protected static JComboBox<String> comboFilmGrain;
	protected static JCheckBox caseFilmGrainDenoise;
	protected static JComboBox<String> comboFilmGrainDenoise;
	protected static JCheckBox caseCABAC;
	protected static JCheckBox caseForceLevel;
	protected static JCheckBox caseForcePreset;
	protected static JCheckBox caseForceTune;
	protected static JCheckBox caseForceQuality;	
	protected static JCheckBox caseForceSpeed;
	protected static JLabel lblHWaccel;
	protected static JComboBox<String> comboAccel;
	protected static JComboBox<String> comboForceProfile;
	protected static JComboBox<String> comboForceLevel;
	protected static JComboBox<String> comboForcePreset;
	protected static JComboBox<String> comboForceTune;
	protected static JComboBox<String> comboForceQuality;	
	protected static JComboBox<String> comboForceSpeed;
	protected static JCheckBox caseForcerDAR;
	protected static JCheckBox caseDecimate;
	protected static JCheckBox caseConform;
	protected static JCheckBox caseCreateTree;
	protected static JComboBox<String> comboCreateTree;
	protected static JCheckBox casePreserveMetadata;
	protected static JCheckBox casePreserveSubs;
	protected static JCheckBox caseCreateOPATOM;
	protected static JCheckBox caseOPATOM;
	protected static JCheckBox caseAS10;
	protected static JCheckBox caseChunks;
	protected static JCheckBox caseDRC;
	protected static JCheckBox caseTruePeak;
	protected static JComboBox<Object> comboTruePeak;
	protected static JCheckBox caseLRA;
	protected static JComboBox<Object> comboLRA;
	protected static JComboBox<String> chunksSize;
	protected static JComboBox<String> comboConform;
	protected static JComboBox<String> comboFPS;
	protected static JComboBox<String> comboAudioIn;
	protected static JComboBox<String> comboAudioOut;
	protected static JComboBox<String> comboAudioCodec;
	protected static JComboBox<Object> comboNormalizeAudio;
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
	protected static JLabel lblShutterEncoder;
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
	protected static JLabel lblImageSize;
	protected static JLabel lblScreenshot;
	public static boolean screenshotIsRunning = false;
	protected static JLabel lblToConform;
	protected static JLabel lblIsConform;
	protected static JLabel lblTFF;
	protected static JComboBox<String> lbl48k;
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
	protected static JComboBox<String> lblAudioMapping;
	protected static JLabel lblPad;
	protected static JComboBox<String> comboDAR;
	protected static JLabel lblNiveaux;
	protected static JLabel lblOPATOM;
	protected static JLabel lblCreateOPATOM;
	protected static JCheckBox caseBlend;
	protected static JCheckBox caseMotionBlur;
	protected static JSlider sliderBlend;
	protected static JButton btnReset;
	protected static boolean doNotLoadImage = false;
	protected static JPanel statusBar;
	protected static JLabel lblArrows;
	protected static boolean windowDrag;
	protected static JLabel lblYears;
	protected static JLabel lblBy;
	protected static JLabel lblGpuDecoding;
	protected static JLabel lblGpuFiltering;
	public static JComboBox<String> comboGPUDecoding;
	public static JComboBox<String> comboGPUFilter;
	protected static JLabel tempsRestant;
	protected static JLabel tempsEcoule;
	protected static JTextField textH;
	protected static JTextField textM;
	protected static JTextField textS;
	protected static JTextField textF;
	protected static JComboBox<String> debitVideo;
	protected static JComboBox<String> maximumBitrate;
	protected static JComboBox<String> debitAudio;
	protected static JLabel lblAudioKbs;
	protected static String audioValues[] = new String[] { "1536","1344","1152","960","768","640","512","448","384","320","256","192","160","128","96","64","32"};
	protected static JPanel h264lines;
	protected static JTextField bitrateSize;
	protected static JLabel lock;
	public static boolean isLocked = false;
	protected static JLabel lblBitrateTimecode;
	protected static JLabel lblH264;
	protected static JLabel lblKbsH264;
	protected static JLabel lblMaximumKbs;
	protected static JLabel lblSize;
	protected static JLabel lblFileSizeMo;
	protected static JLabel lblVideoBitrate;
	protected static JLabel lblMaximumBitrate;
	protected static JLabel lblAudioBitrate;
	protected static JLabel lblVBR;
	protected JPopupMenu popupList;
	protected static JPopupMenu scanListe;
	protected JPopupMenu popupProgression;
	protected JPopupMenu popupDestination;
	protected static JTextField textMail;
	protected static JCheckBox caseSendMail;
	protected static JTextField textStream;
	protected static JCheckBox caseStream;
	protected static JCheckBox caseLoop;

	/*
	 * Group Boxes
	 */
	public static JPanel grpChooseFiles;
	public static JPanel grpChooseFunction;
	public static JTabbedPane grpDestination;
	public static JPanel destination1;
	public static JPanel destination2;
	public static JPanel destination3;
	public static JPanel destinationMail;
	public static JPanel destinationStream;
	public static JPanel optionsPanel;
	public static JPanel grpProgression;
	public static JPanel grpResolution;
	public static JPanel grpImageSequence;
	public static JPanel grpImageFilter;
	public static JPanel grpColorimetry;
	public static JPanel grpSetTimecode;
	public static JPanel grpSetAudio;
	public static JPanel grpAudio;
	public static JPanel grpAdvanced;
	public static JPanel grpBitrate;	
	private static boolean extendSectionsIsRunning = false;
	private static Thread scrollThread;
	private static JScrollBar settingsScrollBar;
	private boolean allowScrolling = false;
	
	//grpImageAdjustement
	public static JPanel grpImageAdjustement;
	public static JButton btnResetColor;
	public static JCheckBox caseEnableColorimetry;
	public static JComboBox<String> comboRGB;
	public static JSlider sliderExposure;
	public static JSlider sliderGamma;
	public static JSlider sliderContrast;
	public static JSlider sliderHighlights;
	public static JSlider sliderMediums;
	public static JSlider sliderShadows;
	public static JSlider sliderWhite;
	public static JSlider sliderBlack;
	public static JSlider sliderBalance;
	public static JSlider sliderHUE;
	public static JSlider sliderRED;
	public static JSlider sliderGREEN;
	public static JSlider sliderBLUE;		
	public static JSlider sliderVibrance;
	public static JComboBox<String> comboVibrance;
	public static JSlider sliderSaturation;
	public static JSlider sliderGrain;
	public static JSlider sliderVignette;
	public static JSlider sliderAngle;
	public static JSlider sliderZoom;
	
	//grpCorrections
	public static JPanel grpCorrections;
	public static JCheckBox caseStabilisation;
	public static String stabilisation = "";
	public static JCheckBox caseDeflicker;
	public static JCheckBox caseBanding;
	public static JCheckBox caseLimiter;
	public static JCheckBox caseDetails;
	public static JSlider sliderDetails;
	public static JCheckBox caseDenoise;
	public static JSlider sliderDenoise;
	public static JCheckBox caseSmoothExposure;
	public static JSlider sliderSmoothExposure;
	
	//grpTransitions
	public static JPanel grpTransitions;
	public static JLabel lblFadeInColor;
	public static JCheckBox caseVideoFadeIn;
	public static JTextField spinnerVideoFadeIn;
	public static JCheckBox caseAudioFadeIn;
	public static JTextField spinnerAudioFadeIn;
	public static JLabel lblFadeOutColor;
	public static JCheckBox caseVideoFadeOut;
	public static JTextField spinnerVideoFadeOut;
	public static JCheckBox caseAudioFadeOut;
	public static JTextField spinnerAudioFadeOut;
	
	//grpCrop
	public static JPanel grpCrop;
	public static JPanel selection;
	public static JPanel overImage;
	public static boolean selectionDrag;
	public static int anchorRight;
	public static int anchorBottom;
	public static int startCropX = 0;
	public static int startCropY = 0;
	public static int frameCropX;
	public static int frameCropY;
	public static boolean shift = false;
    public static boolean ctrl = false;
	public static JCheckBox caseEnableCrop;
	public static JComboBox<String> comboPreset;
	public static int mouseCropOffsetX;
	public static int mouseCropOffsetY;
	public static JTextField textCropPosX;
    public static JTextField textCropPosY;
    public static JTextField textCropWidth;
    public static JTextField textCropHeight;
		
    //grpOverlay
    public static JPanel grpOverlay;
	public static float playerRatio = 3;
	public static boolean ratioChanged = false;
    public static int tcPosX = 0;
    public static int tcPosY = 0;
    public static int tcLocX = 0;
    public static int tcLocY = 0;    
    public static int filePosX = 0;
    public static int filePosY = 0;
    public static int fileLocX = 0;
    public static int fileLocY = 0;
    public static JPanel timecode;
	public static JPanel fileName;
	public static JTextField textTcPosX;
	public static JTextField textTcPosY;
	public static JTextField textNamePosX;
	public static JTextField textNamePosY;    
	public static JTextField textTcSize;
	public static JTextField textNameSize;
	public static JTextField textTcOpacity;
	public static JTextField textNameOpacity;    
	public static JCheckBox caseAddTimecode;
	public static JLabel lblTimecode;
	public static JTextField TC1;
	public static JTextField TC2;
	public static JTextField TC3;
	public static JTextField TC4;
	public static JTextField overlayText;
	public static long textTime = System.currentTimeMillis();
	public static Thread changeText;
	public static JCheckBox caseShowTimecode;
	public static JCheckBox caseShowFileName;
	public static JCheckBox caseAddText;
	public static JComboBox<String> comboOverlayFont;	
	public static JLabel lblTcBackground; 
	public static JPanel panelTcColor;
	public static JPanel panelTcColor2;
	public static Color foregroundColor = Color.WHITE;
	public static Color backgroundColor = Color.BLACK;
	public static String foregroundHex = "ffffff"; //white
	public static String backgroundHex = "000000"; //black
	public static String foregroundTcAlpha = "ff"; //100%
	public static String foregroundNameAlpha = "ff";
	public static String backgroundTcAlpha = "7f"; //50%
	public static String backgroundNameAlpha = "7f";

	//grpWatermark
	public static JPanel grpWatermark;
	public static JPanel logo; 
	public static Image logoPNG;
	public static int logoWidth;
	public static int logoHeight;
	public static JCheckBox caseAddWatermark;
	public static JTextField textWatermarkPosX;
	public static JTextField textWatermarkPosY;
    public static JTextField textWatermarkSize;
    public static JTextField textWatermarkOpacity;
    public static String watermarkPreset = null;
    public static JCheckBox caseSafeArea;
	public static String logoFile = null;
    public static int logoPosX = 0;
    public static int logoPosY = 0;
    public static int logoLocX = 0;
    public static int logoLocY = 0;
    
	//grpSubtitles
	public static JPanel grpSubtitles;
	public static JCheckBox caseAddSubtitles;
	public static String outline = "1";
	public static JComboBox<String> comboSubsFont;	
	public static Color fontSubsColor = Color.WHITE;
	public static Color backgroundSubsColor = Color.BLACK;
	public static String subsHex = "FFFFFF";
	public static String subsHex2 = "000000";
	public static String subsAlpha = "7F";
	public static int alphaHeight;	
	public static JPanel panelSubsColor;
	public static JLabel lblSubsOutline;
	public static JPanel panelSubsColor2;
	public static JButton btnI;
	public static JButton btnG;
	public static JTextField textSubsSize;
	public static JTextField textSubsOutline;
	public static JTextField textSubtitlesPosition;
	public static JTextField textSubsWidth;
	public static JLabel lblSubsBackground; 
	public static File subtitlesFilePath;
	public static JPanel subsCanvas;
	
  	public static class MouseTcPosition {
  		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;		
  	}	
	
  	public static class MouseNamePosition {
  		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
  	}
		
	public static class MouseSubSize {
		static int mouseX;
		static int offsetX;
	}
	
	public static class MouseSubsPosition {
  		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
  	}
		
  	public static class MouseLogoPosition {
		static int mouseX;
		static int offsetX;
		static int mouseY;
		static int offsetY;
	}
  	  
	public static void main(String[] args) {
						
		//Splashscreen
		new Splash();
				
		//Accès à la police Montserrat pour drawtext
		if (System.getProperty("os.name").contains("Mac"))
		{
			pathToFont = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			pathToFont = pathToFont.substring(0,pathToFont.length()-1);
			pathToFont = pathToFont.substring(0,(int) (pathToFont.lastIndexOf("/"))).replace("%20", " ");
			pathToFont = "'" + pathToFont + "/JRE/lib/fonts/Montserrat.ttf" + "'";
		}
		else if (System.getProperty("os.name").contains("Linux"))
		{
			pathToFont = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			pathToFont = pathToFont.substring(0,pathToFont.length()-1);
			pathToFont = pathToFont.substring(0,(int) (pathToFont.lastIndexOf("/"))).replace("%20", " ");
			pathToFont = "'" + pathToFont + "fonts/Montserrat.ttf" + "'";
		}
		
		//Path for AI models
		NCNN.modelsPath = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows"))
		{
			NCNN.modelsPath = NCNN.modelsPath.substring(1,NCNN.modelsPath.length()-1);
		}
		else
		{
			NCNN.modelsPath = NCNN.modelsPath.substring(0,NCNN.modelsPath.length()-1);
		}		
		NCNN.modelsPath = NCNN.modelsPath.substring(0,(int) (NCNN.modelsPath.lastIndexOf("/"))).replace("%20", " ")  + "/Library/models";		
		
		//Checking java x86 or arm version
		try {
			
			String PathToJAVA = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			PathToJAVA = PathToJAVA.substring(0,PathToJAVA.length()-1);
			PathToJAVA = PathToJAVA.substring(0,(int) (PathToJAVA.lastIndexOf("/"))).replace("%20", " ")  + "/JRE/bin/java";
			
			ProcessBuilder processJAVA = new ProcessBuilder("file", PathToJAVA);	            				
			Process proc = processJAVA.start();	            		         				

	        BufferedReader reader =  new BufferedReader(new InputStreamReader(proc.getInputStream()));	           		       
	        
	        String s[] = reader.readLine().split(" ");     		       
	        
	        arch = s[s.length - 1];        
	        
		} catch (Exception e) {}     
		
		//Drop files
		if (args.length != 0) 
		{
			for (int i = 0; i < args.length; i++)
			{
				droppedFiles.add(i, args[i]);
			}
		}

		Utils.setLanguage();
		Utils.loadThemes();		
		Splash.increment();
						
		// Documents Shutter Encoder
		if (documents.exists() == false)
		{
			// Do not create it if the location is different
			if (new File("settings.xml").exists() == false)
				documents.mkdirs();			
			
			if (new File("Functions").exists() == false)
				documents.mkdirs();
		}
		
		new Shutter();

		ImageIO.setUseCache(false); //IMPORTANT use RAM instead of HDD cache
	}

	public Shutter() {
				
		Desktop desktop = Desktop.getDesktop();
		if( desktop.isSupported( Desktop.Action.APP_ABOUT ) ) {
		    desktop.setAboutHandler( e -> {
		    	
		    	Image logo = new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage();
		    	Image newimg = logo.getScaledInstance(64, 64,  java.awt.Image.SCALE_SMOOTH);
		    	ImageIcon icon = new ImageIcon(newimg);
		    	
		    	JOptionPane.showMessageDialog(null, "Shutter Encoder v" + actualVersion + System.lineSeparator()
		    	+ language.getProperty("lblCrParPaul"), "About", JOptionPane.PLAIN_MESSAGE, icon);
		    } );
		}
		
		frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Shutter Encoder");
		frame.setBackground(new Color(30,30,35));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(332, 708);
		frame.setMinimumSize(new Dimension(332, 708));
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100, 100, 100)));
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Shutter.taskBarHeight = (int) (dim.getHeight() - winSize.height);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		
		frame.addWindowListener(new WindowAdapter() {
			
            @Override
            public void windowClosing(WindowEvent e) {
				Settings.saveSettings();
            }
            
        });

    	frame.addComponentListener(new ComponentAdapter() {
    		
            public void componentResized(ComponentEvent e) {
            	
            	if (windowDrag == false)
            	{					            		
					VideoPlayer.resizeAll();					
            	}
            	else
            	{
            		resizeAll(frame.getWidth(), 0);
            	}
            }
        });
				
		soundURL = this.getClass().getClassLoader().getResource("contents/complete.wav");
		soundErrorURL = this.getClass().getClassLoader().getResource("contents/error.wav");

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
				
		//Initialize FFmpeg path
		FFMPEG.getFFmpegPath();
		
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
		grpImageFilter();
		Splash.increment();
		grpSetAudio();
		Splash.increment();
		grpAudio();
		Splash.increment();
		grpAdvanced();
		Splash.increment();			
		grpImageAdjustement();
		Splash.increment();
		grpCrop();
		Splash.increment();
		grpSetTimecode();
		Splash.increment();
		grpOverlay();
		Splash.increment();
		grpSubtitles();
		Splash.increment();
		grpWatermark();
		Splash.increment();
		grpColorimetry();
		Splash.increment();
		grpCorrections();
		Splash.increment();		
		grpTransitions();
		Splash.increment();
		grpImageSequence();
		Splash.increment();
		grpBitrate();
		Splash.increment();
		Reset();
		Splash.increment();
		new VideoPlayer();
		Splash.increment();	

		settingsScrollBar = new JScrollBar();
		settingsScrollBar.setVisible(false);
		settingsScrollBar.setValue(45);
		settingsScrollBar.setMaximum(100);
		settingsScrollBar.setBackground(new Color(30,30,35));
		settingsScrollBar.setOrientation(JScrollBar.VERTICAL);
		settingsScrollBar.setBounds(extendedWidth - settingsScrollBar.getWidth() - 2, topPanel.getHeight() - 4, 11, frame.getHeight() - topPanel.getHeight() - statusBar.getHeight() + 4);
		frame.getContentPane().add(settingsScrollBar);
		
		settingsScrollBar.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				
				allowScrolling = true;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				allowScrolling = false;
				settingsScrollBar.setValue(45);
			}
			
		});
		
		settingsScrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {			
				
				if (scrollThread == null || scrollThread.isAlive() == false)
				{				
					scrollThread = new Thread(new Runnable() {
	
						@Override
						public void run() {
							
							if (allowScrolling && settingsScrollBar.isVisible())
							{	
								while (allowScrolling)
								{										
									int i = Math.round((float) (45 - settingsScrollBar.getValue()) / 3);
																		
									//On récupère le groupe qui est le plus haut
									JPanel top;
									
									if (grpResolution.isVisible())
									{
										top = grpResolution;
									}
									else if (grpSetTimecode.isVisible())
									{
										top = grpSetTimecode;
									}
									else if (grpSetAudio.isVisible())
									{
										top = grpSetAudio;
									}
									else
									{
										top = grpAudio;
									}
									
									if (extendSectionsIsRunning == false)
									{
										if (canScroll 
											&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
											&& frame.getWidth() > 332
											&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31
											|| 
											canScroll 
											&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
											&& frame.getWidth() > 332
											&& Settings.btnDisableAnimations.isSelected()
											&& top.getY() < 30) 
											{							
												//Empêche de faire un scroll vers le bas pour ne pas dépasser la position minimale de top
												if (i < 0 && Settings.btnDisableAnimations.isSelected() && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
												{
													i = 0;
												}
												
												//Pré calcul
												if (top.getY() + i >= grpChooseFiles.getY() && i > 0)
												{
													i = grpChooseFiles.getY() - top.getY();	
												}
												
												if (frame.getSize().getHeight() - (btnReset.getLocation().y + i + btnReset.getHeight()) >= 31 && i < 0)						
												{
													if (i < frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()))
														i = (int) (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) - 31);
													else
														i = 0;	
												}
												
												grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + i);
												grpBitrate.setLocation(grpBitrate.getLocation().x, grpBitrate.getLocation().y + i);								
												grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + i);
												grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + i);								
												grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + i);								
												grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + i);
												grpSubtitles.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getLocation().y + i);
												grpWatermark.setLocation(grpWatermark.getLocation().x, grpWatermark.getLocation().y + i);
												grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + i);								
												grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x, grpImageAdjustement.getLocation().y + i);								
												grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + i);	
												grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + i);	
												grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + i);
												grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + i);
												grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + i);
												grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + i);
												btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + i);				
											}
									}
									
									if ((frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31) && top.getY() == 30)
									{
										settingsScrollBar.setVisible(false);
									}
									
									long startTime = System.nanoTime();
									
									//Animate size
									animateSections(startTime, true);	
								}
								
							}
						}
						
					});
					scrollThread.start();
					
				}
	      }		

		});
		
		comboFonctions.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					changeFunction(true);
					changeFilters();
					
					if (Settings.btnDisableVideoPlayer.isSelected() == false)
					{					
						if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut")) && cutKeyframesIsDisplayed == false)
						{							
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"), comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							cutKeyframesIsDisplayed = true;
						}
						else if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")) && rewrapKeyframesIsDisplayed == false)
						{
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"), comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							rewrapKeyframesIsDisplayed = true;
						}
						else if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionConform")) && conformKeyframesIsDisplayed == false)
						{
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"), comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							conformKeyframesIsDisplayed = true;
						}
					}
					
					if (VideoPlayer.btnPlay.isVisible() && liste.getSize() > 0)
					{
						VideoPlayer.btnPlay.requestFocus();
					}
					else
					{
						frame.requestFocus();
					}
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
						//Use ESCAPE only for FFPLAY
						if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
						{
							frame.requestFocus();
						}
						
						if (VideoPlayer.fullscreenPlayer)
						{
							if (ke.getKeyCode() == KeyEvent.VK_K || ke.getKeyCode() == KeyEvent.VK_SPACE)
							{
								ke.consume();
								VideoPlayer.btnPlay.doClick();
							}
							
							if (ke.getKeyCode() == KeyEvent.VK_J)
							{
								VideoPlayer.previousFrame = true;
								VideoPlayer.playerSetTime((float) (VideoPlayer.playerCurrentFrame - 10));
			  				}
								
							if (ke.getKeyCode() == KeyEvent.VK_L)
							{
								VideoPlayer.previousFrame = true;
								VideoPlayer.playerSetTime((float) (VideoPlayer.playerCurrentFrame + 10));
							}
							
							if (ke.getID() == KeyEvent.KEY_PRESSED)
			        		{           	  
								if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
									shift = true;
			        		}
							
							if (ke.getKeyCode() == KeyEvent.VK_I)
							{
								if (shift)
								{						
									VideoPlayer.btnGoToIn.doClick();
								}
								else
								{							
									VideoPlayer.btnMarkIn.doClick();
								}
							}
												
							if (ke.getKeyCode() == KeyEvent.VK_O)
							{
								if (shift)
								{
									VideoPlayer.btnGoToOut.doClick();
								}
								else
								{
									VideoPlayer.btnMarkOut.doClick();
								}
							}	

							if (ke.getKeyCode() == KeyEvent.VK_LEFT)
								VideoPlayer.btnPrevious.doClick();	
							
							if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
								VideoPlayer.btnNext.doClick();
							
							if (ke.getKeyCode() == KeyEvent.VK_UP)
								VideoPlayer.btnGoToOut.doClick();	
							
							if (ke.getKeyCode() == KeyEvent.VK_DOWN)
								VideoPlayer.btnGoToIn.doClick();					
							
							//Volume up
							if (ke.getKeyCode() == 107 && VideoPlayer.sliderVolume.getValue() < VideoPlayer.sliderVolume.getMaximum())
							{
								VideoPlayer.sliderVolume.setValue(VideoPlayer.sliderVolume.getValue() + 10);
							}
							
							//Volume down
							if (ke.getKeyCode() == 109 && VideoPlayer.sliderVolume.getValue() > 0)
							{
								VideoPlayer.sliderVolume.setValue(VideoPlayer.sliderVolume.getValue() - 10);			
							}
							if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
							{
								VideoPlayer.fullscreenPlayer = false;						
								
								topPanel.setVisible(true);
								grpChooseFiles.setVisible(true);
								grpChooseFunction.setVisible(true);
								grpDestination.setVisible(true);
								grpProgression.setVisible(true);
								statusBar.setVisible(true);
								
								frame.getContentPane().setBackground(new Color(30,30,35));
														
								changeSections(false);
								
								if (VideoPlayer.isPiping == false)
								{			
									VideoPlayer.setPlayerButtons(true);
									
									VideoPlayer.mouseIsPressed = false;
						    		
						    		VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame			
								}
								
								VideoPlayer.resizeAll();
								
								Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, Shutter.frame.getWidth(), Shutter.frame.getHeight(), 15, 15));
					            Area shape2 = new Area(new Rectangle(0, Shutter.frame.getHeight()-15, Shutter.frame.getWidth(), 15));
					            shape1.add(shape2);
					    		frame.setShape(shape1);
					    		
					    		if (VideoPlayer.isPiping == false)
					    			VideoPlayer.btnPlay.requestFocus();
							}
						}
						
						//CMD + Q
						if (System.getProperty("os.name").contains("Mac") && (ke.getKeyCode() == KeyEvent.VK_Q) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						{							
							Runtime.getRuntime().addShutdownHook(new Thread()
					        {
					            @Override
					            public void run()
					            {
					            	Settings.saveSettings();
									
									Utils.killProcesses();
																		
									if (VideoPlayer.waveform != null)
									{
										VideoPlayer.waveform = null;
									}
					            }
					        });
						}
						
						//Save settings
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
						
						//btnStart
						if ((ke.getKeyCode() == KeyEvent.VK_ENTER) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
						|| (ke.getKeyCode() == KeyEvent.VK_ENTER)
						&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
						{
							btnStart.doClick();
						}
						
						//Informations
						if ((ke.getKeyCode() == KeyEvent.VK_I) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
						|| (ke.getKeyCode() == KeyEvent.VK_I)
						&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
						{
							if (fileList.getSelectedIndices().length > 0) 
							{
								informations.doClick();
							}
						}
						
					}	
					else if (ke.getID() == KeyEvent.KEY_RELEASED)
		        	{
						if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
							shift = false;		
		        	}
				}	
			}
		}, AWTEvent.KEY_EVENT_MASK);

		// Mouse wheel
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			public void eventDispatched(AWTEvent event) {
				
				//Mouse position
				MouseEvent mp = (MouseEvent) event;
								
				//On récupère le groupe qui est le plus haut
				JPanel top;
				
				if (grpResolution.isVisible())
				{
					top = grpResolution;
				}
				else if (grpSetTimecode.isVisible())
				{
					top = grpSetTimecode;
				}
				else if (grpSetAudio.isVisible())
				{
					top = grpSetAudio;
				}
				else
				{
					top = grpAudio;
				}
				
				if (extendSectionsIsRunning == false)
				{
					if (canScroll 
						&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
						&& frame.getWidth() > 332 && mp.getX() > 332
						&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31
						|| 
						canScroll 
						&& comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
						&& frame.getWidth() > 332 && mp.getX() > 332
						&& Settings.btnDisableAnimations.isSelected()
						&& top.getY() < 30) 
						{								
							MouseWheelEvent me = (MouseWheelEvent) event;
							int i =  (0 - me.getWheelRotation()) * 20;
							
							//Empêche de faire un scroll vers le bas pour ne pas dépasser la position minimale de top
							if (i < 0 && Settings.btnDisableAnimations.isSelected() && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
							{
								i = 0;
							}
							
							//Pré calcul
							if (top.getY() + i >= grpChooseFiles.getY() && i > 0)
							{
								i = grpChooseFiles.getY() - top.getY();	
							}
							
							if (frame.getSize().getHeight() - (btnReset.getLocation().y + i + btnReset.getHeight()) >= 31 && i < 0)						
							{
								if (i < frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()))
									i = (int) (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) - 31);
								else
									i = 0;	
							}
							
							grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + i);
							grpBitrate.setLocation(grpBitrate.getLocation().x, grpBitrate.getLocation().y + i);								
							grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + i);
							grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + i);								
							grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + i);								
							grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + i);
							grpSubtitles.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getLocation().y + i);
							grpWatermark.setLocation(grpWatermark.getLocation().x, grpWatermark.getLocation().y + i);
							grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + i);								
							grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x, grpImageAdjustement.getLocation().y + i);								
							grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + i);	
							grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + i);	
							grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + i);
							grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + i);
							grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + i);
							grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + i);
							btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + i);				
						}
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
				} 
				else
					Utils.findFiles(droppedFiles.toString());
			}
			
			addToList.setVisible(false);			
			lblFiles.setText(Utils.filesNumber());
		}
								
		new Settings();
		Splash.increment();
				
		YOUTUBEDL.update();
		EXIFTOOL.run('"' + "" + '"'); //Preload the binary
		Splash.increment();
				
		//Right_to_left
		if (getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
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
			
			//Settings
			for (Component c : Settings.frame.getContentPane().getComponents())
			{		
				if (c instanceof JCheckBox)
				{
					c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}	
			
			//Destinations
			for (Component c : grpDestination.getComponents())
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
				else if (c instanceof JCheckBox)
				{
					c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}
		}
		
		Utils.changeFrameVisibility(frame, false);
		btnStart.requestFocus();
		
		if (Settings.btnLoadPreset.isSelected() && Settings.comboLoadPreset.getItemCount() > 0)
		{
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (liste.getSize() == 0 || FFMPEG.isRunning || FFPROBE.isRunning);
					
					Shutter.btnReset.doClick();
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Utils.loadSettings(new File(Functions.functionsFolder + "/" + Settings.comboLoadPreset.getSelectedItem()));					
				}
				
			});
			t.start();		
		}
			
		if (Settings.btnDisableUpdate.isSelected() == false)
			Update.newVersion();	
		
		availableMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
	}
	
	private void topPanel() {

		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setBounds(0, 0, extendedWidth, 28);

		settings = new JLabel(new FlatSVGIcon("contents/settings.svg", 13, 13));
		settings.setHorizontalAlignment(SwingConstants.CENTER);
		settings.setBounds(4, 4, 15, 15);
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
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		
		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));
				
				if (FFMPEG.isRunning && btnCancel.isEnabled())
				{
					btnCancel.doClick();
				}
				else
					accept = true;
			}
		 	
			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (accept)
				{					
					Settings.saveSettings();
					
					Utils.changeFrameVisibility(frame, true);

					Utils.killProcesses();
										
					if (VideoPlayer.waveform != null)
					{
						VideoPlayer.waveform = null;
					}				
						
					if (showDonateWindow)
					{
						new Donate();
					}
					else						
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
		
		fullscreen = new JLabel(new FlatSVGIcon("contents/max.svg", 15, 15));
		fullscreen.setHorizontalAlignment(SwingConstants.CENTER);
		fullscreen.setBounds(quit.getLocation().x - 20, 4, 15, 15);			
		topPanel.add(fullscreen);
		
		fullscreen.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				fullscreen.setIcon(new FlatSVGIcon("contents/max_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				
				if (accept)
				{
					toggleFullscreen();
				}
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
					if (inputDeviceIsRunning == false && Settings.btnDisableMinimizedWindow.isSelected() == false
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
		help.setBounds(reduce.getLocation().x - 20, 4, 15, 15);
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
					
					try {
						Desktop.getDesktop().browse(new URI("https://www.shutterencoder.com/documentation/"));
					} catch (IOException | URISyntaxException er) {}
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
		newInstance.setBounds(help.getLocation().x - 20, 4, 15, 15);
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
				
				if (accept)
				{
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					
					if (frame.getLocation().x == dim.width / 2 - frame.getSize().width / 2
					&& frame.getLocation().y == dim.height / 2 - frame.getSize().height / 2)
					{
						frame.setLocation(frame.getLocation().x + new Random().nextInt(50), frame.getLocation().y + new Random().nextInt(50));
					}
					
					try {
						String newShutter;
						
						if (System.getProperty("os.name").contains("Windows"))
						{
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							newShutter = '"' + newShutter.substring(1, newShutter.length()).replace("%20", " ") + '"';
							String[] arguments = new String[] { newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						}
						else if (System.getProperty("os.name").contains("Mac"))
						{
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
							newShutter = newShutter.substring(0, newShutter.length() - 1);
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/"))).replace(" ","\\ ");
							String[] arguments = new String[] { "/bin/bash", "-c", "open -n " + newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						}
						else //Linux
						{ 
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

		lblShutterEncoder = new JLabel(language.getProperty("panelShutter"));
		lblShutterEncoder.setFont(new Font("Magneto", Font.PLAIN, 17));
		lblShutterEncoder.setBounds((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1, lblShutterEncoder.getPreferredSize().width + 10, 24);
		topPanel.add(lblShutterEncoder);
		
		lblV = new JLabel("v" + actualVersion);
		lblV.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblV.setVisible(false);
		lblV.setSize(lblV.getPreferredSize().width + 4, 16);
		topPanel.add(lblV);

		lblV.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.shutterencoder.com/changelog/"));
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
	
		topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
		
		topPanel.add(topImage);
		frame.getContentPane().add(topPanel);
		
		topImage.addMouseListener(new MouseAdapter() {
			
			int screenIndex = 0;
			
			@Override
			public void mouseClicked(MouseEvent down) {
				
				if (down.getClickCount() == 2)
				{
					toggleFullscreen();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent down) {
				
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;
				
				frame.toFront();
			}

			@Override
			public void mouseReleased(MouseEvent down) {

				if (System.getProperty("os.name").contains("Windows"))
		        {
		        	GraphicsConfiguration config = frame.getGraphicsConfiguration();
					GraphicsDevice myScreen = config.getDevice();
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] allScreens = env.getScreenDevices();
					int newScreenIndex = -1;
					for (int i = 0; i < allScreens.length; i++) {
					    if (allScreens[i].equals(myScreen))
					    {
					    	newScreenIndex = i;
					        break;
					    }
					}
		        	
		            if (screenIndex != newScreenIndex)
		            {		            	
		            	screenIndex = newScreenIndex; 

		            	/*
				        if (comboFonctions.getSelectedItem().toString().isEmpty() == false)
				        {
				        	changeFunction(false);
				        }
				        */
				        
		            	Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		                Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		                shape1.add(shape2);
		        		frame.setShape(shape1);
		        		
		        		if (FFMPEG.isRunning)
		        		{
		        			VideoPlayer.setPlayerButtons(false);
		        		}
		            }
		        }		
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
							
		grpChooseFiles = new JPanel();
		grpChooseFiles.setLayout(null);
		grpChooseFiles.setBounds(10, 30, 312, frame.getHeight() - 400);
		grpChooseFiles.setBackground(new Color(30,30,35));
		grpChooseFiles.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpChooseFiles") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		frame.getContentPane().add(grpChooseFiles);

		fileList = new JList<String>(liste);
		fileList.setBackground(new Color(42,42,47));
		fileList.setCellRenderer(new FilesCellRenderer());
		fileList.setFixedCellHeight(17);
		fileList.setBounds(10, 50, 292, frame.getHeight() - 460);
		fileList.setToolTipText(language.getProperty("rightClick"));
	
		addToList.setIcon(new FlatSVGIcon("contents/drop.svg", 40, 40));
		addToList.setText(language.getProperty("dropFilesHere"));
		addToList.setVerticalTextPosition(JLabel.BOTTOM);
		addToList.setHorizontalTextPosition(JLabel.CENTER);
		addToList.setSize(fileList.getSize());
		addToList.setForeground(new Color(120,120,120));
		addToList.setBackground(new Color(0,0,0,0));
		addToList.setFont(new Font(freeSansFont, Font.PLAIN, 16));
		addToList.setHorizontalAlignment(SwingConstants.CENTER);
		addToList.setVerticalAlignment(SwingConstants.CENTER);
		fileList.add(addToList);
		
		btnEmptyList = new JButton(language.getProperty("btnEmptyList"));
		btnEmptyList.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnEmptyList.setBounds(124, 21, 82, 21);
		grpChooseFiles.add(btnEmptyList);

		btnEmptyList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (liste.getSize() > 0 && comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				{
					// Screen record
					if (inputDeviceIsRunning)
						caseDisplay.setSelected(false);
										
					inputDeviceIsRunning = false;					
					grpImageAdjustement.setEnabled(true);
					Component[] components = Shutter.grpImageAdjustement.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(true);
					}
					
					if (overlayDeviceIsRunning)
					{
						caseAddWatermark.setSelected(false);
						overlayDeviceIsRunning = false;
					}
					
					// Scan
					scan.setText(language.getProperty("menuItemStartScan"));
					scanIsRunning = false;		
					
					FunctionUtils.watchFolder.setLength(0);	
	
					liste.clear();
					addToList.setVisible(true);
					lblFilesEnded.setVisible(false);
					
					// H264 Settings
					lblH264.setVisible(false);
					textH.setText("00");
					textM.setText("00");
					textS.setText("00");
					textF.setText("00");
					if (isLocked == false)
					{
						bitrateSize.setText("-");
					}
					
					// Lecteur
					if (VideoPlayer.waveform != null)
					{
						VideoPlayer.waveform = null;
						VideoPlayer.waveformIcon.setIcon(null);
						VideoPlayer.waveformIcon.repaint();
					}
					VideoPlayer.playerStop();	
					VideoPlayer.setPlayerButtons(false);	
					VideoPlayer.playerRepaint();
										
					changeFilters();
	
					lblFiles.setText(Utils.filesNumber());
					
					FFPROBE.analyzedMedia = null;
					VideoPlayer.videoPath = null;
				}
			}

		});

		scrollBar = new JScrollPane();
		scrollBar.getViewport().add(fileList);
		scrollBar.setBounds(10, 50, 292, fileList.getHeight());
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
					grpImageAdjustement.setEnabled(true);
					Component[] components = Shutter.grpImageAdjustement.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(true);
					}
					
					if (overlayDeviceIsRunning)
					{
						caseAddWatermark.setSelected(false);
						overlayDeviceIsRunning = false;
					}
					changeFilters();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
				if (scanIsRunning == false)
				{					
					if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						fileList.setSelectionInterval(0, liste.getSize() - 1);

					if (e.getKeyCode() == 127 && liste.getSize() > 0 || e.getKeyCode() == 8 && liste.getSize() > 0)
					{
						do {
							liste.remove(fileList.getSelectedIndex());
						} while (fileList.getSelectedIndices().length > 0);
						
						if (liste.getSize() == 0)
							addToList.setVisible(true);
						
						lblFiles.setText(Utils.filesNumber());
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {	
				
				// VideoPlayer.player
				VideoPlayer.setMedia();
			}

		});

		popupList = new JPopupMenu();
		menuDisplay = new JMenuItem(language.getProperty("menuItemVisualiser"));
		final JMenuItem silentTrack = new JMenuItem(language.getProperty("menuItemSilentTrack"));
		final JMenuItem menuOpenFolder = new JMenuItem(language.getProperty("menuItemOuvrirDossier"));
		informations = new JMenuItem(language.getProperty("menuItemInfo"));
		final JMenuItem rename = new JMenuItem(language.getProperty("menuItemRename"));
		inputDevice = new JMenuItem(Shutter.language.getProperty("menuItemInputDevice"));
		final JMenuItem arborescence = new JMenuItem(language.getProperty("menuItemArborescence"));
		final JMenuItem hash = new JMenuItem(Shutter.language.getProperty("menuItemHash"));
		final JMenuItem tempsTotal = new JMenuItem(language.getProperty("menuItemTempsTotal"));
		final JMenuItem poids = new JMenuItem(language.getProperty("menuItemPoids"));
		final JMenuItem gop = new JMenuItem(language.getProperty("menuItemGop"));
		final JMenuItem ftp = new JMenuItem(language.getProperty("menuItemFtp"));
		final JMenuItem zip = new JMenuItem(language.getProperty("menuItemZip"));
		final JMenuItem unzip = new JMenuItem(language.getProperty("menuItemUnzip"));

		silentTrack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				liste.addElement(" -f lavfi -i anullsrc=r=" + lbl48k.getSelectedItem().toString() + ":cl=mono");
			}
		});

		menuDisplay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (FFPROBE.isRunning)
				{
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
				}
				
				FFMPEG.toSDL(false);
			}
		});

		menuOpenFolder.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
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
				
				int totalLength = 0;
				FFPROBE.totalLength = 0;
				FFPROBE.analyzedMedia = null;
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	
				
				for (String file : fileList.getSelectedValuesList())
				{			
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
					
					//IMPORTANT
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
					
					totalLength += FFPROBE.totalLength;
					FFPROBE.totalLength = 0;
				}
				
				//IMPORTANT
				if (VideoPlayer.videoPath != null)
				{
					FFPROBE.Data(VideoPlayer.videoPath);
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
					
					//IMPORTANT
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
				}				
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				// Formatage
				int h = (totalLength / 3600000);
				int m = (totalLength / 60000) % 60;
				int s = (totalLength) / 1000 % 60;
				int f = (int) (totalLength / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS);

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
				
				int totalLength = 0;
				FFPROBE.totalLength = 0;
				FFPROBE.analyzedMedia = null;
				
				for (String file : fileList.getSelectedValuesList()) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					FFPROBE.Data(file);
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {
						}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
					
					// IMPORTANT
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
					
					totalLength += FFPROBE.totalLength;
					FFPROBE.totalLength = 0;
				}
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				int finalSize = 0;
				String codec = "";
				int bitrate = 0;
				switch (comboFonctions.getSelectedItem().toString()) {
				case "DNxHD":
					codec = "DNxHD " + comboFilter.getSelectedItem().toString();
					bitrate = Integer.parseInt(comboFilter.getSelectedItem().toString().replace(" X", ""));
					break;
				case "Apple ProRes":
					codec = "Apple ProRes " + comboFilter.getSelectedItem().toString();
					switch (comboFilter.getSelectedItem().toString()) {
					case "Proxy":
						bitrate = (int) ((float) 1.52 * FFPROBE.currentFPS);
						break;
					case "LT":
						bitrate = (int) ((float) 3.4 * FFPROBE.currentFPS);
						break;
					case "422":
						bitrate = (int) ((float) 4.88 * FFPROBE.currentFPS);
						break;
					case "422 HQ":
						bitrate = (int) ((float) 7.4 * FFPROBE.currentFPS);
						break;
					case "444":
						bitrate = (int) ((float) 11 * FFPROBE.currentFPS);
						break;
					case "4444":
						bitrate = (int) ((float) 11 * FFPROBE.currentFPS);
						break;
					case "4444 XQ":
						bitrate = (int) ((float) 16.5 * FFPROBE.currentFPS);
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
							bitrate = (int) ((float) 0.7616 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 0.7148 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.1916 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.1796 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "SQ":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 2.4492 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 2.2968 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.6132 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.5744 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "HQ":
					case "HQX":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 3.7072 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 3.4728 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.9256 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.8672 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					case "444":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 7.41 * 8 * FFPROBE.currentFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 6.9492 * 8 * FFPROBE.currentFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 1.8516 * 8 * FFPROBE.currentFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 1.7384 * 8 * FFPROBE.currentFPS);
							break;
						}
						break;
					}
					break;
				}

				finalSize = (int) (((float) totalLength / 1000) * ((float) bitrate / 8));
				
				String fileSize;
				if (finalSize >= 1000)
					fileSize = String.valueOf(Math.round((float) finalSize / 1024)) + " Go";
				else
					fileSize = finalSize + " Mo";

				JOptionPane.showMessageDialog(frame,
						fileList.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + fileSize + " "
								+ language.getProperty("to") + " " + codec,
						language.getProperty("approximativeWeight"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		informations.addActionListener(new ActionListener() {
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
					MEDIAINFO.run(item, true);					
				}
			}
		});

		rename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				//Unlock the file to be deletable		
				if (VideoPlayer.videoPath != null)
				{					
					VideoPlayer.videoPath = null;	
					VideoPlayer.frameVideo = null;	
					VideoPlayer.playerRepaint();
					
					// Lecteur
					if (VideoPlayer.waveform != null)
					{
						VideoPlayer.waveform = null;
						VideoPlayer.waveformIcon.setIcon(null);
						VideoPlayer.waveformIcon.repaint();
					}
					
					VideoPlayer.playerStop();
				}
				
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
			@SuppressWarnings("deprecation")
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

		hash.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
					
				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {}
				new HashGenerator();
			}
			
		});
		
		gop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new GOP();
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
				
				if (scan.getText().equals(language.getProperty("menuItemStartScan")))
				{					
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
					
				}
				else
				{
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;
					
					FunctionUtils.watchFolder.setLength(0);	
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
				{
					menuDisplay.doClick();
				}

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
							popupList.add(informations);
							popupList.add(rename);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(hash);
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
							popupList.add(informations);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(hash);
							popupList.add(gop);
							popupList.show(fileList, e.getX() - 30, e.getY());
						}

					} else {
						scanListe.removeAll();
						scanListe.add(scan);
						scanListe.add(inputDevice);
						scanListe.add(arborescence);
						scanListe.add(hash);
						scanListe.show(fileList, e.getX() - 30, e.getY());
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 0));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (e.getButton() == MouseEvent.BUTTON1 && liste.getSize() > 0 && fileList.getSelectedValue().equals(VideoPlayer.videoPath) == false)
				{
					VideoPlayer.setMedia();
				}
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

				if (dialog.getFiles() != null)
				{
					File[] files = dialog.getFiles();
					
					for (int i = 0; i < files.length; i++)
					{						
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
							if (FunctionUtils.allowsInvalidCharacters == false) 
							{
								JOptionPane.showConfirmDialog(Shutter.frame, file.getAbsoluteFile().toString() + System.lineSeparator() + Shutter.language.getProperty("invalidCharacter"), Shutter.language.getProperty("import"),
								JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
								
								FunctionUtils.allowsInvalidCharacters = true;
							}														
						}		
						
						liste.addElement(file.getAbsolutePath());
						addToList.setVisible(false);
						lblFiles.setText(Utils.filesNumber());
					}

					changeFilters();

					switch (comboFonctions.getSelectedItem().toString()) {
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
						FFPROBE.setLength();
						break;
					}
					
					// VideoPlayer.player
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
		grpChooseFunction.setBounds(10, grpChooseFiles.getY() + grpChooseFiles.getHeight() + 4, 312, 76);
		grpChooseFunction.setBackground(new Color(30,30,35));
		grpChooseFunction.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpChooseFunction") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		
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
				
				cancelled = true;
				
				if (FFMPEG.runProcess != null && FFMPEG.runProcess.isAlive()) 
				{
					FFMPEG.isRunning = false;

					if (btnStart.getText().equals(language.getProperty("resume")))
					{
						FFMPEG.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le détruire
					}

					try {
						FFMPEG.writer.write('q');
						FFMPEG.writer.flush();
						FFMPEG.writer.close();
					} catch (IOException er) {}
					
					Thread wait = new Thread(new Runnable() {

						@Override
						public void run() {
							
							do {
								try {
									frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									btnStart.setEnabled(false);
									Thread.sleep(10);									
								} catch (InterruptedException e1) {}
							} while (FFMPEG.runProcess.isAlive());
							
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}	
					});
					wait.start();
				}
				
				if (DCRAW.runProcess != null)
				{
					if (DCRAW.runProcess.isAlive())
					{
						DCRAW.process.destroy();
					}
				}
				
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
				
				if (BMXTRANSWRAP.runProcess != null)
				{
					if (BMXTRANSWRAP.runProcess.isAlive())
					{
						BMXTRANSWRAP.process.destroy();
					}
				}
				
				if (NCNN.runProcess != null)
				{
					if (NCNN.runProcess.isAlive())
					{
						NCNN.process.destroy();
					}
				}
				
				if (scanIsRunning)
				{
					enableAll();
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;
					
					FunctionUtils.watchFolder.setLength(0);			
				}
				
				if (Ftp.isRunning)
				{					
					try {
						Ftp.ftp.abort();
					} catch (Exception e1) {}
				}


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
					{
						if (frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)))
						{			
							long time = System.currentTimeMillis();
							
							do {
								
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {}
								
								if (System.currentTimeMillis() - time > 3000)
									break;
								
							} while (frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
						}
						new Functions();
					}
					else
					{
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
				
				if (caseDisplay.isSelected() && caseDisplay.isEnabled())
				{
					VideoPlayer.frameVideo = null;
				}
				
				if (VideoPlayer.addWaveformIsRunning)
				{
					try {
						FFMPEG.waveformWriter.write('q');
						FFMPEG.waveformWriter.flush();
						FFMPEG.waveformWriter.close();
					} catch (IOException er) {}
					
					FFMPEG.waveformProcess.destroy();
				}
				
				FunctionUtils.yesToAll = false;
				FunctionUtils.noToAll = false;
				FunctionUtils.skipToAll = false;
				
				if ((btnStart.getText().equals(language.getProperty("btnStartFunction"))
				|| btnStart.getText().equals(language.getProperty("btnAddToRender"))) && liste.getSize() > 0)
				{					
					grpDestination.setSelectedIndex(0);
					FFMPEG.error = false;
					FFMPEG.errorLog.setLength(0);
					errorList.setLength(0);		
					
					//Temps écoulé
					tempsEcoule.setVisible(false);
					FFMPEG.elapsedTime = 0;
					FFMPEG.previousElapsedTime = 0;

					if (btnStart.getText().equals(language.getProperty("btnAddToRender")))
						RenderQueue.btnStartRender.setEnabled(true);					
					
					// Command directe FFMPEG
					if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg"))
					{
						if (comboFilter.getEditor().getItem().toString().equals(language.getProperty("aucun"))
						|| comboFilter.getEditor().getItem().toString().equals("")
						|| comboFilter.getEditor().getItem().toString().equals(" ")
						|| comboFilter.getEditor().getItem().toString().contains(".") == false)
						{
							JOptionPane.showMessageDialog(frame, language.getProperty("chooseExtension"),
									language.getProperty("extensionError"), JOptionPane.INFORMATION_MESSAGE);
						}
						else
							Command.main();
						
					}
					else if (comboFonctions.getEditor().getItem().toString().contains("exiftool"))
					{
						Command.main();
					}
					else
					{
						//Scan Folder
						if (scan.getText().equals(language.getProperty("menuItemStopScan")) && scanIsRunning == false)
						{
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);
						}
						else
						{
							if (inputDeviceIsRunning)
							{
								VideoPlayer.playerStop();
							}
							
							String function = comboFonctions.getSelectedItem().toString();
							if (language.getProperty("functionCut").equals(function)) 
							{
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									Rewrap.main();
							}
							else if ("WAV".equals(function)
									|| "MP3".equals(function)
									|| "AC3".equals(function)
									|| "Opus".equals(function)
									|| "Vorbis".equals(function)
									|| "AIFF".equals(function)
									|| "FLAC".equals(function)
									|| "ALAC".equals(function)
									|| "AAC".equals(function)
									|| "Dolby Digital Plus".equals(function)
									|| "Dolby TrueHD".equals(function))
							{
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									AudioEncoders.main();
								
							} else if ("Loudness & True Peak".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									LoudnessTruePeak.main();
							} else if (language.getProperty("functionMerge").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									Merge.main();
							} else if (language.getProperty("functionExtract").equals(function)) { 
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("setAll")))
									Extract.extractAll();
								else
									Extract.main();
							} else if (language.getProperty("functionConform").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									Conform.main();				
							} else if (language.getProperty("functionInsert").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									VideoInserts.main();
							} else if (language.getProperty("functionReplaceAudio").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
								{
									ReplaceAudio.setStreams();
								}
							
							} else if (language.getProperty("functionNormalization").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									AudioNormalization.main(new File(""));
							} else if (language.getProperty("functionSceneDetection").equals(function)) {
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
										SceneDetection.btnAnalyze.doClick();
									}
								}
							} else if (language.getProperty("functionBlackDetection").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									BlackDetection.main();
							} else if (language.getProperty("functionOfflineDetection").equals(function)) {
								
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
						        
							} else if ("VMAF".equals(function)) {
								
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									VMAF.main();
								
							} else if ("FrameMD5".equals(function)) {
								
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									FrameMD5.main();
								
							} else if ("DNxHD".equals(function)
									|| "DNxHR".equals(function)
									|| "Apple ProRes".equals(function)
									|| "GoPro CineForm".equals(function)
									|| "QT Animation".equals(function)
									|| "Uncompressed".equals(function)
									|| "H.264".equals(function)
									|| "H.265".equals(function)
									|| "H.266".equals(function)
									|| "WMV".equals(function)
									|| "MPEG-1".equals(function)
									|| "MPEG-2".equals(function)
									|| "VP8".equals(function)
									|| "VP9".equals(function)
									|| "AV1".equals(function)
									|| "Theora".equals(function)
									|| "MJPEG".equals(function)
									|| "Xvid".equals(function)
									|| "XDCAM HD422".equals(function)
									|| "XDCAM HD 35".equals(function)
									|| "AVC-Intra 100".equals(function)
									|| "XAVC".equals(function)
									|| "HAP".equals(function)
									|| "FFV1".equals(function)
									|| "DV PAL".equals(function))
							{
									VideoEncoders.main();
									
							} else if ("DVD".equals(function)) {								
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									VideoEncoders.main();
							} else if ("Blu-ray".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else 
									VideoEncoders.main();
							} else if (language.getProperty("functionPicture").equals(function) || "JPEG".equals(function) || "JPEG XL".equals(function)) {
									Picture.main(true, false);
							}
							else if (language.getProperty("functionRewrap").equals(function))
							{
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									Rewrap.main();
							}
						}
					}
				}
				else // Fonctions n'ayant pas de fichiers dans la liste
				{ 	
					if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
					{
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						
						new VideoWeb();		
						frame.setOpacity(1.0f);
					}
					else if (comboFonctions.getSelectedItem().equals("DVD Rip") && btnStart.getText().equals(language.getProperty("btnStartFunction")))
					{						
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
					
					if (btnStart.getText().equals(language.getProperty("btnPauseFunction")))
					{
						caseRunInBackground.setEnabled(false);
						caseRunInBackground.setSelected(false);
						
						FFMPEG.suspendProcess();
						
						btnStart.setText(language.getProperty("btnResumeFunction"));
						tempsRestant.setText(language.getProperty("timePause"));
						tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);
					} 
					else if (btnStart.getText().equals(language.getProperty("btnResumeFunction")))
					{
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
		
		functionsList = new String[]{ 
				
				language.getProperty("itemNoConversion"),
				language.getProperty("functionCut"),
				language.getProperty("functionReplaceAudio"),
				language.getProperty("functionRewrap"),	
				language.getProperty("functionConform"),			
				language.getProperty("functionMerge"),		
				language.getProperty("functionExtract"),
				language.getProperty("functionSubtitles"),
				language.getProperty("functionInsert"),
				
				language.getProperty("itemAudioConversion"), "WAV", "AIFF", "FLAC", "ALAC", "MP3", "AAC", "AC3", "Opus", "Vorbis", "Dolby Digital Plus", "Dolby TrueHD",
				
				language.getProperty("itemEditingCodecs"), "DNxHD", "DNxHR", "Apple ProRes", "QT Animation", "GoPro CineForm" ,"Uncompressed",
				
				language.getProperty("itemOuputCodecs"), "H.264", "H.265", "H.266", "VP8", "VP9", "AV1",		
				
				language.getProperty("itemBroadcastCodecs"), "XDCAM HD422", "XDCAM HD 35", "AVC-Intra 100", "XAVC", "HAP",
				
				language.getProperty("itemOldCodecs"), "Theora", "MPEG-2", "MJPEG", "Xvid", "DV PAL", "WMV", "MPEG-1",
				
				language.getProperty("itemArchiveCodecs"), "FFV1",		
				
				language.getProperty("itemImage"),"JPEG","JPEG XL",language.getProperty("functionPicture"), 
				
				language.getProperty("itemBurnRip"), "DVD", "Blu-ray", "DVD Rip",
				
				language.getProperty("itemAnalyze"), "Loudness & True Peak",
				language.getProperty("functionNormalization"),
				language.getProperty("functionSceneDetection"),
				language.getProperty("functionBlackDetection"),
				language.getProperty("functionOfflineDetection"),
				"VMAF", "FrameMD5",
				
				language.getProperty("itemDownload"),
				language.getProperty("functionWeb")
				
		};
			
		comboFonctions = new JComboBox<String[]>();
		comboFonctions.setName("comboFonctions");
		comboFonctions.setModel(new DefaultComboBoxModel(functionsList));	
		comboFonctions.setSelectedItem(null);		
		comboFonctions.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFonctions.setEditable(true);
		comboFonctions.setMaximumRowCount(Toolkit.getDefaultToolkit().getScreenSize().height / 33);
		comboFonctions.setBounds(8, 19, 168, 22);
		comboFonctions.getModel().setSelectedItem("");
		comboFonctions.setRenderer(new ComboBoxRenderer());
		grpChooseFunction.add(comboFonctions);
		
		if (System.getProperty("os.name").contains("Mac") && arch.equals("x86_64"))
		{
			comboFonctions.removeItem("H.266");				
		}
		
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
					newList.add("ALAC");
					newList.add("MP3");
					newList.add("AAC");
					newList.add("AC3");
					newList.add("Opus");
					newList.add("Vorbis");
					newList.add("Dolby Digital Plus");
					newList.add("Dolby TrueHD");					
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemEditingCodecs")))
				{
					newList.clear();
					
					newList.add("DNxHD");
					newList.add("DNxHR");
					newList.add("Apple ProRes");
					newList.add("QT Animation");
					newList.add("GoPro CineForm");
					newList.add("Uncompressed");
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemOuputCodecs")))
				{
					newList.clear();
					
					newList.add("H.264");
					newList.add("H.265");
					
					if (System.getProperty("os.name").contains("Mac") == false || (System.getProperty("os.name").contains("Mac") && arch.equals("arm64")))
					{
						newList.add("H.266");				
					}
					
					newList.add("VP8");
					newList.add("VP9");
					newList.add("AV1");
					newList.add("Theora");

				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("itemBroadcastCodecs")))
				{
					newList.clear();
					
					newList.add("XDCAM HD422");
					newList.add("XDCAM HD 35");
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
					newList.add("MPEG-1");
					newList.add("MPEG-2");
					
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
					newList.add("JPEG XL");
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
					newList.add(("VMAF"));
					newList.add(("FrameMD5"));
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
				
				if (frame.getWidth() > 654)
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				}
			}
			
		});
		
		comboFonctions.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			
			String text = "";
			
			@Override
			public void keyReleased(KeyEvent e) {

				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg") || comboFonctions.getEditor().getItem().toString().contains("exiftool")) 
				{
					changeFilters();
					changeWidth(false);

					topPanel.setBounds(0, 0, frame.getWidth(), 28);
					topImage.setBounds(0, 0, topPanel.getWidth(), 24);
					quit.setLocation(frame.getSize().width - 20, 4);
					fullscreen.setLocation(quit.getLocation().x - 20, 4);
					reduce.setLocation(fullscreen.getLocation().x - 20, 4);
					help.setLocation(reduce.getLocation().x - 20, 4);
					newInstance.setLocation(help.getLocation().x - 20, 4);
					
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

					if (Character.isLetterOrDigit(e.getKeyChar()))
					{						
						comboFonctions.setModel(new DefaultComboBoxModel(functionsList));
						text += String.valueOf(e.getKeyChar()).toLowerCase();

						ArrayList<String> newList = new ArrayList<String>();
						for (int i = 0; i < comboFonctions.getItemCount(); i++) {
							if (functionsList[i].toString().length() >= text.length()) {
								if (functionsList[i].toString().toLowerCase().substring(0, text.length()).contains(text)
										&& functionsList[i].toString().contains(":") == false) {
									newList.add(functionsList[i].toString());
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

					}
					else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						comboFonctions.setModel(new DefaultComboBoxModel(functionsList));
						comboFonctions.getEditor().setItem("");
						comboFonctions.hidePopup();
						changeFilters();
						changeSections(true);
						changeWidth(false);
						text = "";
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					{
						e.consume();// Contournement pour éviter le listeDrop
					}
					else
						comboFonctions.hidePopup();					
				}
				
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (VideoPlayer.btnPlay.isVisible() && liste.getSize() > 0)
					{
						VideoPlayer.btnPlay.requestFocus();
					}
					else
					{
						frame.requestFocus();
					}
				}
			}
		});

		String AllFilters[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4", ".mov",
				".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp", ".avif" };
		comboFilter = new JComboBox<Object>(AllFilters);
		comboFilter.setVisible(false);
		comboFilter.setName("comboFilter");
		comboFilter.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFilter.setEditable(true);
		comboFilter.setMaximumRowCount(20);
		comboFilter.setBounds(228, 19, 76, 22);
		grpChooseFunction.add(comboFilter);

		comboFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
								
				if (Shutter.lblFilter.isVisible())
				{
					if (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("AV1"))
					{
						if (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov"))
						{
							caseFastStart.setEnabled(true);
						}
						else
							caseFastStart.setEnabled(false);
					}
										
					// Add quality selection
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
					{
						setGPUOptions();
						
						if (comboFilter.getSelectedItem().toString().equals(".avif"))
						{
							List<String> graphicsAccel = new ArrayList<String>();
							graphicsAccel.add(language.getProperty("aucune").toLowerCase());
							
							Thread hwaccel = new Thread(new Runnable() {

								@Override
								public void run() {
									
									comboAccel.setEnabled(false);
									comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
									
									try {
										
										if (System.getProperty("os.name").contains("Windows"))
										{
											FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_nvenc -s 640x360 -f null -" + '"');
					
											if (FFMPEG.error == false)
												graphicsAccel.add("Nvidia NVENC");
					
											FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_qsv -s 640x360 -f null -" + '"');
					
											if (FFMPEG.error == false)
												graphicsAccel.add("Intel Quick Sync");
	
											FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_amf -s 640x360 -f null -" + '"');
					
											if (FFMPEG.error == false)
												graphicsAccel.add("AMD AMF Encoder");
										}
										else if (System.getProperty("os.name").contains("Mac"))
										{
											FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_videotoolbox -s 640x360 -f null -");
					
											if (FFMPEG.error == false)
												graphicsAccel.add("OSX VideoToolbox");	
										}
										
										int index = comboAccel.getSelectedIndex();
										
										comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
										
										if (index <= comboAccel.getModel().getSize())
											comboAccel.setSelectedIndex(index);	
										
										//load hwaccel value after checking gpu capabilities
										if (Utils.loadEncFile != null && Utils.hwaccel != "")
										{
											comboAccel.setSelectedItem(Utils.hwaccel); 
											Utils.hwaccel = "";
										}
										
									} catch (Exception e) {}
									
									if (comboAccel.getItemCount() > 1)
										comboAccel.setEnabled(true);
									
									comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
								
							});
							hwaccel.start();
						}
						
						if (comboFilter.getSelectedItem().toString().equals(".png"))
						{							
							if (comboImageOption.getItemAt(0).equals("0%") == false)
							{
								comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "0%","5%","10%","15%","20%","25%","30%","35%","40%","45%","50%","55%","60%","65","70%","75%","80%","85%","90%","95%","100%" }));	
							}
							comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
							comboImageOption.setSize(50, 16);
							comboImageOption.setEditable(false);
							lblImageQuality.setText("Comp.:");
							grpResolution.add(lblImageQuality);											
							grpResolution.add(comboImageOption);
							lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
							comboImageOption.repaint();
						}
						else if (comboFilter.getSelectedItem().toString().equals(".webp") || comboFilter.getSelectedItem().toString().equals(".avif"))
						{							
							if (comboImageOption.getItemAt(0).equals("100%") == false)
							{
								comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%" }));	
							}
							comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
							comboImageOption.setSize(50, 16);
							comboImageOption.setEditable(false);
							lblImageQuality.setText(language.getProperty("lblQualit"));
							grpResolution.add(lblImageQuality);											
							grpResolution.add(comboImageOption);
							lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
							comboImageOption.repaint();
						}
						else if (comboFilter.getSelectedItem().toString().equals(".tif"))
						{
							if (comboImageOption.getItemAt(0).equals("packbits") == false)
							{
								comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "packbits", "raw", "lzw", "deflate" }));
							}
							comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6, lblImageQuality.getLocation().y);
							comboImageOption.setSize(90, 16);
							comboImageOption.setEditable(false);
							lblImageQuality.setText(language.getProperty("lblQualit"));
							grpResolution.remove(lblImageQuality);
							grpResolution.add(comboImageOption);
							lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
							comboImageOption.repaint();
						}
						else if (comboFilter.getSelectedItem().toString().equals(".gif") || comboFilter.getSelectedItem().toString().equals(".apng"))
						{
							if (comboImageOption.getItemAt(0).equals("15 " + Shutter.language.getProperty("fps")) == false)
							{
								String fps[] = new String[17];
								int a = 0;
								for (int f = 15 ; f < 24 ; f++)
								{
									fps[a] = f + " " + Shutter.language.getProperty("fps");
									a++; 
								}

								fps[a] = "23,98 " + Shutter.language.getProperty("fps");
								fps[a+1] = "24 " + Shutter.language.getProperty("fps");
								fps[a+2] = "25 " + Shutter.language.getProperty("fps");
								fps[a+3] = "29,97 " + Shutter.language.getProperty("fps");
								fps[a+4] = "30 " + Shutter.language.getProperty("fps");
								fps[a+5] = "50 " + Shutter.language.getProperty("fps");
								fps[a+6] = "59,94 " + Shutter.language.getProperty("fps");
								fps[a+7] = "60 " + Shutter.language.getProperty("fps");
								
								comboImageOption.setModel(new DefaultComboBoxModel<String>(fps));
							}
							comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6, lblImageQuality.getLocation().y);
							comboImageOption.setSize(90, 16);
							comboImageOption.setEditable(false);
							grpResolution.remove(lblImageQuality);
							grpResolution.add(comboImageOption);
							lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
							comboImageOption.repaint();
						}
						else
						{
							grpResolution.remove(lblImageQuality);
							grpResolution.remove(comboImageOption);
							lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
						}
						
						grpResolution.repaint();
					}
					
					if (comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					{
						if (comboFilter.getSelectedIndex() == 0) //H.264
						{
							debitVideo.setSelectedItem(38000);
						}
						else //H.265
							debitVideo.setSelectedItem(50000);
					}

					if (comboFonctions.getSelectedItem().toString().equals("DNxHD") 
					|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
					|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
					|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
					|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
					|| comboFonctions.getSelectedItem().toString().equals("Uncompressed")) 
					{
						if (comboFilter.getSelectedItem().toString().equals("36"))
						{
							caseForcerEntrelacement.setEnabled(false);
							caseForcerInversion.setEnabled(false);
							caseForcerEntrelacement.setSelected(false);
							caseForcerInversion.setSelected(false);
						}
						else
						{
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

		lblFilter = new JLabel();
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(new Font(montserratFont, Font.PLAIN, 13));
		lblFilter.setBounds(164, 21, 60, 16);
		grpChooseFunction.add(lblFilter);

	}

	private void grpDestination() {
		
		grpDestination = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);	
		grpDestination.setBounds(12, grpChooseFunction.getY() + grpChooseFunction.getHeight() + 10, 308, 145);
		grpDestination.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(1,1,1,1), new Color(45,45,45), 1, 5)));		
		grpDestination.setFont(new Font(montserratFont, Font.PLAIN, 11));	
		frame.getContentPane().add(grpDestination);		

		//DESTINATION 1		
		
		destination1 = new JPanel();
		destination1.setLayout(null);
		destination1.setFont(new Font(montserratFont, Font.PLAIN, 12));	
				
		caseOpenFolderAtEnd1 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd1.setName("caseOpenFolderAtEnd1");
		caseOpenFolderAtEnd1.setSelected(true);
		caseOpenFolderAtEnd1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd1.setBounds(6, 23, caseOpenFolderAtEnd1.getPreferredSize().width, 23);
		destination1.add(caseOpenFolderAtEnd1);

		caseChangeFolder1 = new JCheckBox(language.getProperty("caseChangeFolder"));
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
					GradientPaint gp = new GradientPaint(0, 0, new Color(30,30,35), fill, lblDestination1.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination1.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(30,30,35), lblDestination1.getWidth(), lblDestination1.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				   	g2d.drawRoundRect(0, 0, lblDestination1.getWidth() - 1, lblDestination1.getHeight() - 1, 10,10);
				    
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
		lblDestination1.setBackground(new Color(30,30,35));
		lblDestination1.setText(language.getProperty("sameAsSource"));
		lblDestination1.setBounds(6, 0, 290, 22);
		destination1.add(lblDestination1);
				
		//DESTINATION 2
		
		destination2 = new JPanel();
		destination2.setLayout(null);
		destination2.setFont(new Font(montserratFont, Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd2 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd2.setName("caseOpenFolderAtEnd2");
		caseOpenFolderAtEnd2.setSelected(false);
		caseOpenFolderAtEnd2.setEnabled(false);
		caseOpenFolderAtEnd2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd2.setBounds(6, 23, caseOpenFolderAtEnd2.getPreferredSize().width, 23);
		destination2.add(caseOpenFolderAtEnd2);

		caseChangeFolder2 = new JCheckBox(language.getProperty("caseChangeFolder"));
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
					GradientPaint gp = new GradientPaint(0, 0, new Color(30,30,35), fill, lblDestination2.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination2.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(30,30,35), lblDestination2.getWidth(), lblDestination2.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				    g2d.drawRoundRect(0, 0, lblDestination2.getWidth() - 1, lblDestination2.getHeight() - 1, 10,10);
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
		lblDestination2.setBackground(new Color(30,30,35));
		lblDestination2.setText(language.getProperty("aucune"));
		lblDestination2.setBounds(6, 0, 290, 22);
		destination2.add(lblDestination2);
		
		//DESTINATION 3
		
		destination3 = new JPanel();
		destination3.setLayout(null);
		destination3.setFont(new Font(montserratFont, Font.PLAIN, 12));	
		
		caseOpenFolderAtEnd3 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd3.setName("caseOpenFolderAtEnd3");
		caseOpenFolderAtEnd3.setSelected(false);
		caseOpenFolderAtEnd3.setEnabled(false);
		caseOpenFolderAtEnd3.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd3.setBounds(6, 23, caseOpenFolderAtEnd3.getPreferredSize().width, 23);
		destination3.add(caseOpenFolderAtEnd3);

		caseChangeFolder3 = new JCheckBox(language.getProperty("caseChangeFolder"));
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
					GradientPaint gp = new GradientPaint(0, 0, new Color(30,30,35), fill, lblDestination3.getHeight(),  c);
				    g2d.setPaint(gp);
				    g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination3.getHeight()));	
				    
				    // Borders
				    GradientPaint gp2 = new GradientPaint(0, 0, new Color(30,30,35), lblDestination3.getWidth(), lblDestination3.getHeight(),  new Color(75,75,75));
				    g2d.setPaint(gp2);
				    g2d.drawRoundRect(0, 0, lblDestination3.getWidth() - 1, lblDestination3.getHeight() - 1, 10,10);
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
		lblDestination3.setBackground(new Color(30,30,35));
		lblDestination3.setText(language.getProperty("aucune"));
		lblDestination3.setBounds(6, 0, 290, 22);
		destination3.add(lblDestination3);
		
		//MAIL		
		destinationMail = new JPanel();
		destinationMail.setLayout(null);
		destinationMail.setFont(new Font(montserratFont, Font.PLAIN, 12));
				
		caseSendMail = new JCheckBox(Shutter.language.getProperty("caseSendMail"));
		caseSendMail.setName("caseSendMail");
		caseSendMail.setSelected(false);
		caseSendMail.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSendMail.setBounds(6, 1, caseSendMail.getPreferredSize().width, 23);
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
						textMail.setForeground(Color.WHITE);
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
		textMail.setBounds(6, 24, 290, 21);
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
					textMail.setForeground(Color.WHITE);
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
				
		caseStream = new JCheckBox(Shutter.language.getProperty("caseStream"));
		caseStream.setName("caseStream");
		caseStream.setSelected(false);
		caseStream.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseStream.setBounds(6, 1, caseStream.getPreferredSize().width, 23);
		destinationStream.add(caseStream);
		
		caseLoop = new JCheckBox(Shutter.language.getProperty("caseLoop"));
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
						textStream.setForeground(Color.WHITE);
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
		textStream.setBounds(6, 24, 290, 21);
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
					textStream.setForeground(Color.WHITE);
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
		mailIcon = new FlatSVGIcon("contents/mail.svg", 18, 18);
		streamIcon = new FlatSVGIcon("contents/stream.svg", 18, 18);
		
		//Ajout des tabs	
		setDestinationTabs(5);
			
		btnExtension = new JCheckBox(Shutter.language.getProperty("btnExtension"));
		btnExtension.setName("btnExtension");
		btnExtension.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnExtension.setBounds(6, caseOpenFolderAtEnd1.getLocation().y + caseOpenFolderAtEnd1.getHeight(), btnExtension.getPreferredSize().width, 23);
		destination1.add(btnExtension);
		
		txtExtension = new JTextField();
		txtExtension.setName("txtExtension");
		txtExtension.setColumns(10);
		txtExtension.setEnabled(false);
		txtExtension.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtExtension.setBounds(btnExtension.getLocation().x + btnExtension.getWidth() + 6, btnExtension.getLocation().y + 1, grpDestination.getWidth() - (btnExtension.getLocation().x + btnExtension.getWidth()) - 18, 21);
		txtExtension.setToolTipText("<html>{codec/function}<br>{preset}<br>{resolution/scale}<br>{width}<br>{height}<br>{ratio/aspect}<br>{framerate/fps}<br>{bitrate}<br>{timecode}<br>{duration/time}<br>{date}</html>");
		destination1.add(txtExtension);	
				
		btnExtension.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (btnExtension.isSelected())
				{
					txtExtension.setEnabled(true);		
					
					ToolTipManager.sharedInstance().setInitialDelay(0);
		            ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(txtExtension, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false));
		            ToolTipManager.sharedInstance().setInitialDelay(750); // Reset to default delay
		            
					if (txtExtension.getText().isEmpty())
					{
						String extensionName = "_" + comboFonctions.getSelectedItem().toString().replace(" ","_");	
						
						if (comboFilter.getSelectedItem().toString().contains(".") == false
						&& comboFilter.getSelectedItem().toString().equals(language.getProperty("aucun")) == false
						&& comboFilter.getSelectedItem().toString().contains("/") == false)
						{
							extensionName += "_" + comboFilter.getSelectedItem().toString().replace(" ","_");
						}
						
						if ((Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected()))
						{
							extensionName += "_TC";
						}
						
						txtExtension.setText(extensionName);
					}
				}
				else
				{
					txtExtension.setEnabled(false);					
					
					if (txtExtension.getText().contains(comboFonctions.getSelectedItem().toString().replace(" ","_")))
					{
						txtExtension.setText("");
					}
				}
			}
			
		});
				
		caseSubFolder = new JCheckBox(Shutter.language.getProperty("caseSubFolder"));
		caseSubFolder.setName("caseSubFolder");
		caseSubFolder.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSubFolder.setBounds(6, btnExtension.getY() + btnExtension.getHeight(), caseSubFolder.getPreferredSize().width, 23);
		destination1.add(caseSubFolder);
		
		txtSubFolder = new JTextField();
		txtSubFolder.setName("txtSubFolder");
		txtSubFolder.setColumns(10);
		txtSubFolder.setEnabled(false);
		txtSubFolder.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtSubFolder.setBounds(caseSubFolder.getLocation().x + caseSubFolder.getWidth() + 6, caseSubFolder.getLocation().y + 1, grpDestination.getWidth() - (caseSubFolder.getLocation().x + caseSubFolder.getWidth()) - 18, 21);
		destination1.add(txtSubFolder);	
		
		caseSubFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSubFolder.isSelected())
				{
					txtSubFolder.setEnabled(true);
				}
				else
				{
					txtSubFolder.setEnabled(false);
				}
			}
			
		});	
		
		caseDeleteSourceFile = new JCheckBox(Shutter.language.getProperty("caseDeleteSourceFile"));
		caseDeleteSourceFile.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDeleteSourceFile.setBounds(6, caseSubFolder.getY() + caseSubFolder.getHeight(), caseDeleteSourceFile.getPreferredSize().width, 23);
		destination1.add(caseDeleteSourceFile);
		
		caseDeleteSourceFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseDeleteSourceFile.isSelected())
				{
					int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("areYouSure"), Shutter.language.getProperty("caseDeleteSourceFile"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
					
					if (reply == JOptionPane.NO_OPTION)
					{
						caseDeleteSourceFile.setSelected(false);
						caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
					}
					else
						caseDeleteSourceFile.setForeground(Color.RED);
				}
				else
				{
					caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
				}
				
			}
			
		});
		
		grpDestination.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {	
				
				try {	
					
					if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals(language.getProperty("output")))
					{
						destination1.add(btnExtension);
						destination1.add(txtExtension);	
						destination1.add(caseSubFolder);
						destination1.add(txtSubFolder);
						destination1.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals(language.getProperty("output") + "1"))
					{
						destination1.add(btnExtension);
						destination1.add(txtExtension);	
						destination1.add(caseSubFolder);
						destination1.add(txtSubFolder);
						destination1.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals(language.getProperty("output") + "2"))
					{
						destination2.add(btnExtension);
						destination2.add(txtExtension);	
						destination2.add(caseSubFolder);
						destination2.add(txtSubFolder);
						destination2.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals(language.getProperty("output") + "3"))
					{
						destination3.add(btnExtension);
						destination3.add(txtExtension);	
						destination3.add(caseSubFolder);
						destination3.add(txtSubFolder);
						destination3.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("FTP"))
					{
						grpDestination.setSelectedComponent(destinationMail);
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}
						new Ftp();
						frame.setOpacity(1.0f);
						
						destinationMail.add(btnExtension);
						destinationMail.add(txtExtension);	
						destinationMail.add(caseSubFolder);
						destinationMail.add(txtSubFolder);
						destinationMail.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("Mail"))
					{
						destinationMail.add(btnExtension);
						destinationMail.add(txtExtension);
						destinationMail.add(caseSubFolder);	
						destinationMail.add(txtSubFolder);
						destinationMail.add(caseDeleteSourceFile);
					}
					else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("Stream"))
					{
						destinationStream.add(btnExtension);
						destinationStream.add(txtExtension);
						destinationStream.add(caseSubFolder);
						destinationStream.add(txtSubFolder);
						destinationStream.add(caseDeleteSourceFile);
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
		grpProgression.setBounds(10, grpDestination.getY() + grpDestination.getHeight() + 6, 312, 94);
		grpProgression.setBackground(new Color(30,30,35));
		grpProgression.setToolTipText(language.getProperty("rightClick"));
		grpProgression.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpProgression") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		frame.getContentPane().add(grpProgression);

		caseRunInBackground = new JCheckBox(language.getProperty("caseRunInBackground"));
		caseRunInBackground.setName("caseRunInBackground");
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRunInBackground.setBounds(9, 64, caseRunInBackground.getPreferredSize().width, 23);
		grpProgression.add(caseRunInBackground);

		// Inactivité
		caseRunInBackground.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseRunInBackground.isSelected()) {
					
					Thread thread = new Thread(new Runnable()
					{
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
									tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);							
									
									if (running == true)
										FFMPEG.suspendProcess();
									running = false;
								}

								if (caseRunInBackground.isSelected() == false)
									FFMPEG.resumeProcess();

							} while (caseRunInBackground.isSelected()
									&& btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")));

						}

					});
					thread.start();
				}
			}

		});

		caseDisplay = new JCheckBox(language.getProperty("menuItemVisualiser"));
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
					else
						Taskbar.getTaskbar().setWindowProgressValue(frame, 0);
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
		grpResolution.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpResolution") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpResolution.setBackground(new Color(30,30,35));
		grpResolution.setBounds(frame.getWidth(), 30, 312, 121);
		frame.getContentPane().add(grpResolution);

		comboResolution = new JComboBox<String>();
		comboResolution.setName("comboResolution");
		comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "1:2", "1:4", "1:8", "1:16",
				"3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720",
				"4096x2160", "3840x2160", "2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000",
				"854x480", "720x576", "640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
		comboResolution.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboResolution.setEditable(true);
		comboResolution.setBounds(58, 18, 118, 22);
		comboResolution.setMaximumRowCount(30);
		grpResolution.add(comboResolution);

		comboResolution.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{     	
				if (comboResolution.getItemCount() > 0) //Contourne un bug lors de l'action sur le btnReset
				{
					if (comboFonctions.getSelectedItem().toString().contains("JPEG") || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
					{
						changeFilters();
						
						if (comboFonctions.getSelectedItem().toString().contains("JPEG") || comboFilter.getSelectedItem().toString().equals(".png"))
						{
							grpResolution.remove(lblImageQuality);
							grpResolution.remove(comboImageOption);
							lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
						}
					}
					else
					{
						if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
						{
							lblPad.setVisible(false);
						}
						else
						{				
							lblPad.setVisible(true);
						}	
		
						changeFilters();
						FFPROBE.setFilesize();	
					}
				}		
				
				if (VideoPlayer.videoPath != null)
				{
					FFMPEG.checkGPUCapabilities(VideoPlayer.videoPath);
				}

				if (NCNN.isRunning && NCNN.process != null)
				{
					NCNN.process.destroy();
					
					do {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					} while (NCNN.isRunning);
					
					if (VideoPlayer.preview != null)
						VideoPlayer.preview = null;
				}
								
				if (FFPROBE.totalLength <= 40 || comboResolution.getSelectedItem().toString().contains("AI"))
				{	
					if (VideoPlayer.preview != null)
						VideoPlayer.preview = null;
					
					VideoPlayer.loadImage(true);
				}
				else
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
					VideoPlayer.resizeAll();		
				}
				
				if (comboResolution.getSelectedItem().toString().contains("AI"))
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
					
					if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					{
						Utils.changeFrameVisibility(RenderQueue.frame, true);
						btnStart.setText(language.getProperty("btnStartFunction"));
					}
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionExtract")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
				&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
				&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
				&& comboFonctions.getSelectedItem().equals("VMAF") == false
				&& comboFonctions.getSelectedItem().equals("FrameMD5") == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("itemMyFunctions")) == false
				&& (RenderQueue.frame == null || RenderQueue.frame != null && RenderQueue.frame.isVisible() == false)
				&& comboResolution.getSelectedItem().toString().contains("AI") == false)
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
			}
			
		});
		
		comboResolution.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
				if (comboFonctions.getSelectedItem().toString().contains("JPEG") == false && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) == false)
				{
					lblPad.setVisible(true);
				}
				
				char caracter = e.getKeyChar();
				if (caracter != '-' && caracter != 'x' && caracter != '%' && caracter != ':' && caracter != 'a' && caracter != 'u' && caracter != 't' && caracter != 'o')
				{
					if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
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
		
		caseRotate = new JCheckBox(language.getProperty("caseRotate"));
		caseRotate.setName("caseRotate");
		caseRotate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseRotate.setBounds(7, 47, caseRotate.getPreferredSize().width, 23);
		grpResolution.add(caseRotate);

		caseRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseRotate.isSelected())
				{
					comboRotate.setEnabled(true);
				}
				else
					comboRotate.setEnabled(false);
				
				VideoPlayer.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame		
			}

		});

		comboRotate = new JComboBox<String>();
		comboRotate.setName("comboRotate");
		comboRotate.setEnabled(false);
		comboRotate.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "90", "-90", "180" }));
		comboRotate.setSelectedIndex(3);
		comboRotate.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboRotate.setEditable(false);
		comboRotate.setBounds(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3, 46, 16);
		comboRotate.setMaximumRowCount(20);
		grpResolution.add(comboRotate);

		comboRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame				
			}

			
		});
		
		caseMiror = new JCheckBox(language.getProperty("caseMiror"));
		caseMiror.setName("caseMiror");
		caseMiror.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMiror.setBounds(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y, caseMiror.getPreferredSize().width,	23);
		grpResolution.add(caseMiror);
		
		caseMiror.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
			}

		});

		caseForcerDAR = new JCheckBox(language.getProperty("caseForcerDAR"));
		caseForcerDAR.setName("caseForcerDAR");
		caseForcerDAR.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerDAR.setSize(caseForcerDAR.getPreferredSize().width, 23);
		caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
		grpResolution.add(caseForcerDAR);
		
		caseForcerDAR.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (caseForcerDAR.isSelected())
					comboDAR.setEnabled(true);
				else
					comboDAR.setEnabled(false);
				
				VideoPlayer.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame				
			}

		});
		
		comboDAR = new JComboBox<String>();
		comboDAR.setName("comboDAR");
		comboDAR.setMaximumRowCount(20);
		comboDAR.setModel(new DefaultComboBoxModel<String>(new String[] { "1:1", "4:3", "9:16", "16:9", "21:9", "1.85", "2.35", "2.39"}));
		comboDAR.setSelectedIndex(3);
		comboDAR.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboDAR.setEditable(true);
		comboDAR.setSize(54, 16);
		comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
		grpResolution.add(comboDAR);
			
		comboDAR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VideoPlayer.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame				
			}
			
		});
		
		btnNoUpscale = new JCheckBox(Shutter.language.getProperty("btnNoUpscale"));
		btnNoUpscale.setName("btnNoUpscale");
		btnNoUpscale.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnNoUpscale.setBounds(12, caseRotate.getLocation().y + caseRotate.getHeight(), btnNoUpscale.getPreferredSize().width, 16);
		grpResolution.add(btnNoUpscale);
			
		btnNoUpscale.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				VideoPlayer.resizeAll();				
			}
			
		});
				
		caseCreateSequence = new JCheckBox(language.getProperty("caseCreateSequence"));
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
				{
					if (liste.getSize() > 0 && inputDeviceIsRunning == false)
					{
						//Analyse
						FFPROBE.Data(liste.firstElement().toString());	
						do
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
						while (FFPROBE.isRunning);
						
						comboInterpret.setSelectedItem(String.valueOf(FFPROBE.currentFPS).replace(".0", "").replace(".", ","));
					}
					
					comboInterpret.setEnabled(true);
				}
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
		comboInterpret.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "5", "10", "15","20", "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
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
		if (getLanguage.equals(Locale.of("ru").getDisplayLanguage()) || getLanguage.equals(Locale.of("uk").getDisplayLanguage()) || getLanguage.equals(Locale.of("vi").getDisplayLanguage()))
		{
			lblIsInterpret.setVisible(false);
		}				
		
		iconTVInterpret = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
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
					FFMPEG.toFFPLAY(" -r " + (float) FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", ".")) + " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -v quiet -i " + '"' + file + '"' + " -vf scale=1000:-1:sws_flags=fast_bilinear:sws_dither=none -r 1 -c:v rawvideo -map v:0 -an -f nut pipe:1");
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
		
		lblImageSize = new JLabel(Shutter.language.getProperty("lblBitrateSize"));
		lblImageSize.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblImageSize.setBounds(12, 20, 48, 16);
		grpResolution.add(lblImageSize);
		
		lblScreenshot = new JLabel(new FlatSVGIcon("contents/screenshot.svg", 16, 16));
		lblScreenshot.setHorizontalAlignment(SwingConstants.CENTER);
		lblScreenshot.setBounds(comboResolution.getX() + comboResolution.getWidth() + 9, 21, 16, 16);
		grpResolution.add(lblScreenshot);

		lblScreenshot.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			
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

		lblImageQuality = new JLabel(language.getProperty("lblQualit"));
		lblImageQuality.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblImageQuality.setBounds(comboResolution.getX() + comboResolution.getWidth() + 6, comboResolution.getY() + 3, lblImageQuality.getPreferredSize().width + 4, 16);
		grpResolution.add(lblImageQuality);
		
		comboImageOption = new JComboBox<String>();
		comboImageOption.setName("comboImageOption");
		comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%" }));
		comboImageOption.setSelectedIndex(0);
		comboImageOption.setMaximumRowCount(20);
		comboImageOption.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboImageOption.setEditable(false);
		comboImageOption.setSize(90, 16);
		comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
		grpResolution.add(comboImageOption);
	}

	private void grpImageFilter() {
	
		grpImageFilter = new JPanel();
		grpImageFilter.setLayout(null);
		grpImageFilter.setVisible(false);
		grpImageFilter.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpFiltreImage") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpImageFilter.setBackground(new Color(30,30,35));
		grpImageFilter.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpImageFilter);

		grpImageFilter.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpImageFilter, 122);
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
		
		caseYear = new JCheckBox(language.getProperty("caseYear"));
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
		
		caseMonth = new JCheckBox(language.getProperty("caseMonth"));
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
		
		caseDay = new JCheckBox(language.getProperty("caseDay"));
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
		
		caseFrom = new JCheckBox(language.getProperty("caseFrom"));
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

	private void grpSetTimecode() {
		
		grpSetTimecode = new JPanel();
		grpSetTimecode.setLayout(null);
		grpSetTimecode.setVisible(false);
		grpSetTimecode.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), language.getProperty("grpTimecode") + " ",
				0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpSetTimecode.setBackground(new Color(30,30,35));
		grpSetTimecode.setBounds(frame.getWidth(), 258, 312, 17);
		frame.getContentPane().add(grpSetTimecode);

		caseSetTimecode = new JCheckBox(language.getProperty("caseSetTimecode"));
		caseSetTimecode.setName("caseSetTimecode");
		caseSetTimecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSetTimecode.setBounds(7, 16, 150, 23);
		grpSetTimecode.add(caseSetTimecode);

		grpSetTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpSetTimecode, 93);
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
					
					TCset1.setText(VideoPlayer.caseInH.getText());
					TCset2.setText(VideoPlayer.caseInM.getText());
					TCset3.setText(VideoPlayer.caseInS.getText());
					TCset4.setText(VideoPlayer.caseInF.getText());					
					
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

		caseIncrementTimecode = new JCheckBox(language.getProperty("caseIncrementTimecode"));
		caseIncrementTimecode.setName("caseIncrementTimecode");
		caseIncrementTimecode.setEnabled(false);
		caseIncrementTimecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseIncrementTimecode.setBounds(7, caseSetTimecode.getY() + caseSetTimecode.getHeight(), caseIncrementTimecode.getPreferredSize().width, 23);
		grpSetTimecode.add(caseIncrementTimecode);
		
		caseGenerateFromDate = new JCheckBox(language.getProperty("caseGenerateFromDate"));
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

	private void grpSetAudio() {
		
		grpSetAudio = new JPanel();
		grpSetAudio.setLayout(null);
		grpSetAudio.setVisible(false);
		grpSetAudio.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpAudio") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpSetAudio.setBackground(new Color(30,30,35));
		grpSetAudio.setBounds(frame.getWidth(), 343, 312, 47);
		frame.getContentPane().add(grpSetAudio);
		
		grpSetAudio.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {		
				
				if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false) 
				{
					int size;
					if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
					|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()))
					{
						size = 146;
					}				
					else //Codecs de sortie
					{
						if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
						{
							size = 146;
						}
						else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
						{
							size = 68;
						}
						else
							size = 92;
					}
					
					extendSections(grpSetAudio, size);			
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
		
		caseChangeAudioCodec = new JCheckBox(language.getProperty("caseAudioCodec"));
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
					lbl48k.setEnabled(true);
					
					if (language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()) == false)
					{
						caseNormalizeAudio.setEnabled(true);
					}
				}
				else
				{
					comboAudioCodec.setEnabled(false);					
					comboAudioBitrate.setEnabled(false);
					lbl48k.setEnabled(false);
					caseNormalizeAudio.setEnabled(false);
					caseNormalizeAudio.setSelected(false);
					comboNormalizeAudio.setEnabled(false);
				}
				
				if (comboAudioCodec.getSelectedItem().toString().contains("PCM") || comboAudioCodec.getSelectedItem().toString().contains("FLAC") || comboAudioCodec.getSelectedItem().toString().contains("ALAC"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"1536"}));
					comboAudioBitrate.setSelectedIndex(0);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AAC") || comboAudioCodec.getSelectedItem().toString().equals("MP3"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AC3") || comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus"))
				{
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));					
					comboAudioBitrate.setSelectedIndex(8);
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
		comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "MP3", "AC3", "Opus", "Vorbis", "Dolby Digital Plus", language.getProperty("noAudio") }));
		comboAudioCodec.setSelectedIndex(3);
		comboAudioCodec.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAudioCodec.setEditable(false);
		comboAudioCodec.setSize(80, 16);
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
		comboAudioBitrate.setSize(44, 16);
		comboAudioBitrate.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		grpSetAudio.add(comboAudioBitrate);
		
		lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblKbs.setBounds(comboAudioBitrate.getLocation().x + comboAudioBitrate.getWidth() + 3, caseChangeAudioCodec.getLocation().y + 3, 33, 16);
		grpSetAudio.add(lblKbs);
		
		lblAudioMapping = new JComboBox<String>();
		lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
		lblAudioMapping.setName("lblAudioMapping");
		lblAudioMapping.setBackground(new Color(42,42,47));
		lblAudioMapping.setEditable(false);
		lblAudioMapping.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		lblAudioMapping.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7, comboAudioCodec.getLocation().y);
		lblAudioMapping.setSize(lblKbs.getLocation().x + lblKbs.getSize().width - 5 - 7 - (comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7) , 16);
		grpSetAudio.add(lblAudioMapping);

		lblAudioMapping.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				if (lblAudioMapping.isFocusOwner())
				{				
					if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
					{
						grpSetAudio.add(lblAudio1);
						grpSetAudio.add(comboAudio1);
						grpSetAudio.add(lblAudio2);
						grpSetAudio.add(comboAudio2);
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
	
						if (grpSetAudio.getHeight() != 146)
							extendSections(grpSetAudio, 146);
					}
					else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
					{
						if (grpSetAudio.getHeight() != 68)
							extendSections(grpSetAudio, 68);
						
						grpSetAudio.remove(lblAudio1);
						grpSetAudio.remove(comboAudio1);
						grpSetAudio.remove(lblAudio2);
						grpSetAudio.remove(comboAudio2);
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
					}
					else
					{
						if (grpSetAudio.getHeight() != 92)
							extendSections(grpSetAudio, 92);
					
						grpSetAudio.add(lblAudio1);
						grpSetAudio.add(comboAudio1);
						grpSetAudio.add(lblAudio2);
						grpSetAudio.add(comboAudio2);
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
					}
					
					grpSetAudio.repaint();
					
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {}
				}
			}

		});
				
		lbl48k = new JComboBox<String>();
		lbl48k.setMaximumRowCount(20);
		lbl48k.setModel(new DefaultComboBoxModel<String>(new String[] { "192k","96k","48k","44.1k","16k","8k" }));
		lbl48k.setSelectedIndex(2);		
		lbl48k.setName("lbl48k");
		lbl48k.setEditable(false);
		lbl48k.setEnabled(false);
		lbl48k.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		lbl48k.setSize(46, 16);
		lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
		grpSetAudio.add(lbl48k);
		
		comboAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboAudioCodec.getSelectedItem().toString().equals("FLAC"))
				{
					lblAudioBitrate.setText(language.getProperty("lblValue"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12"}));
					comboAudioBitrate.setSelectedIndex(5);	
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(5);
				}
				else if (comboAudioCodec.getSelectedItem().toString().contains("PCM") || comboAudioCodec.getSelectedItem().toString().contains("ALAC"))
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"1536"}));
					comboAudioBitrate.setSelectedIndex(0);
					if (comboFonctions.getSelectedItem().toString().equals("MJPEG") || comboFonctions.getSelectedItem().toString().contains("H.26"))
					{
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(0);
					}			
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AAC") || comboAudioCodec.getSelectedItem().toString().equals("MP3"))
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(10);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals("AC3") || comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus"))
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));					
					comboAudioBitrate.setSelectedIndex(8);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(8);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] {"0"}));					
					comboAudioBitrate.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(0);
				}	
				else if (comboAudioCodec.getSelectedItem().toString().equals("Opus"))
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(11);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(11);
				}
				else //Codecs de sortie
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(10);				
				}
				
				if (comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					if (comboAudioCodec.getSelectedItem().toString().contains("FLAC"))
					{
						comboFilter.setSelectedIndex(2);
						lblAudioKbs.setVisible(false);
					}
					else
						lblAudioKbs.setVisible(true);
				}
			}
			
		});
		
		caseNormalizeAudio = new JCheckBox(language.getProperty("functionNormalization") + language.getProperty("colon"));
		caseNormalizeAudio.setName("caseNormalizeAudio");
		caseNormalizeAudio.setEnabled(false);
		caseNormalizeAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseNormalizeAudio.setBounds(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight(), caseNormalizeAudio.getPreferredSize().width, 23);
		grpSetAudio.add(caseNormalizeAudio);
		
		caseNormalizeAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseNormalizeAudio.isSelected())
				{
					comboNormalizeAudio.setEnabled(true);
				}
				else
				{
					comboNormalizeAudio.setEnabled(false);
				}
			}
			
		});
		
		String types[] = new String[31];

		types[0] = "0 LUFS";
		int i = 1;
		do {
			types[i] = ("-" + i + " LUFS");
			i++;
		} while (i < 31);

		final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
		comboNormalizeAudio = new JComboBox<Object>(model);		
		comboNormalizeAudio.setName("comboNormalizeAudio");
		comboNormalizeAudio.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboNormalizeAudio.setEnabled(false);
		comboNormalizeAudio.setSelectedIndex(23);
		comboNormalizeAudio.setMaximumRowCount(20);
		comboNormalizeAudio.setBounds(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3, 82, 16);
		grpSetAudio.add(comboNormalizeAudio);
		
		caseAudioOffset = new JCheckBox(language.getProperty("caseAudioOffset"));
		caseAudioOffset.setName("caseAudioOffset");
		caseAudioOffset.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAudioOffset.setBounds(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight(), caseAudioOffset.getPreferredSize().width + 4, 23);
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
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
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
				
				if (txtAudioOffset.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				}
			}
		});
			
		lblOffsetFPS = new JLabel(Shutter.language.getProperty("lblFrames"));
		lblOffsetFPS.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblOffsetFPS.setBounds(txtAudioOffset.getLocation().x + txtAudioOffset.getWidth() + 3, caseAudioOffset.getLocation().y + 4, lblOffsetFPS.getPreferredSize().width, 16);
		grpSetAudio.add(lblOffsetFPS);
				
		caseKeepSourceTracks = new JCheckBox(language.getProperty("caseKeepSourceTracks"));
		caseKeepSourceTracks.setName("caseKeepSourceTracks");
		caseKeepSourceTracks.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseKeepSourceTracks.setBounds(7, caseAudioOffset.getY() + caseAudioOffset.getHeight(), caseKeepSourceTracks.getPreferredSize().width + 4, 23);
		grpSetAudio.add(caseKeepSourceTracks);
				
		//Audio Mapping
		lblAudio1 = new JLabel(language.getProperty("audio") + " 1" + language.getProperty("colon"));
		lblAudio1.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio1.setBounds(12, caseNormalizeAudio.getLocation().y + caseNormalizeAudio.getHeight() + 2, lblAudio1.getPreferredSize().width, 16);
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {}
				}
			}
			
		});
		
		lblAudio2 = new JLabel(language.getProperty("audio") + " 2" + language.getProperty("colon"));
		lblAudio2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudio2.setBounds(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y, lblAudio2.getPreferredSize().width, 16);
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
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
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {}
				}
			}
			
		});
	}
	
	private void grpAudio() {
	
		grpAudio = new JPanel();
		grpAudio.setLayout(null);
		grpAudio.setVisible(false);
		grpAudio.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpAudio") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpAudio.setBackground(new Color(30,30,35));
		grpAudio.setBounds(frame.getWidth(), 343, 312, 139);
		frame.getContentPane().add(grpAudio);
				
		caseMixAudio = new JCheckBox(language.getProperty("caseMixAudio"));
		caseMixAudio.setName("caseMixAudio");
		caseMixAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMixAudio.setBounds(7, 39, caseMixAudio.getPreferredSize().width, 23);
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
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>BL<br>BR</html>");
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
		lblMix.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblMix.setBackground(new Color(42,42,47));
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
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>BL<br>BR</html>");
					
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

		caseSplitAudio = new JCheckBox(language.getProperty("caseSplitAudio"));
		caseSplitAudio.setName("caseSplitAudio");
		caseSplitAudio.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSplitAudio.setBounds(7, caseMixAudio.getY() + caseMixAudio.getHeight(), caseSplitAudio.getPreferredSize().width, 23);
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
		lblSplit.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblSplit.setBackground(new Color(42,42,47));
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
		
		caseSampleRate = new JCheckBox(language.getProperty("caseSampleRate"));
		caseSampleRate.setName("caseSampleRate");
		caseSampleRate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseSampleRate.setBounds(7, caseSplitAudio.getY() +caseSplitAudio.getHeight(), caseSampleRate.getPreferredSize().width, 23);
		grpAudio.add(caseSampleRate);		
		
		caseSampleRate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSampleRate.isSelected())
				{
					lbl48k.setEnabled(true);
				}
				else
					lbl48k.setEnabled(false);
			}
			
		});
		
		caseConvertAudioFramerate = new JCheckBox(language.getProperty("caseConvertAudioFramerate"));
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
				new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
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
				new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
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
		
		if (getLanguage.equals(Locale.of("nl").getDisplayLanguage()) == false && getLanguage.equals(Locale.of("ru").getDisplayLanguage()) == false && getLanguage.equals(Locale.of("uk").getDisplayLanguage()) == false)		
			grpAudio.add(lblAudioIs);
	}
	
	private void grpCrop() {
		
		grpCrop = new JPanel();
		grpCrop.setLayout(null);
		grpCrop.setVisible(false);
		grpCrop.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("frameCropImage") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpCrop.setBackground(new Color(30,30,35));
		grpCrop.setSize(312, 17);
		grpCrop.setLocation(frame.getWidth(), grpImageAdjustement.getY());
		frame.getContentPane().add(grpCrop);		
		
		grpCrop.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				extendSections(grpCrop, 90);	
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
	
		caseEnableCrop = new JCheckBox(Shutter.language.getProperty("enable"));
		caseEnableCrop.setName("caseEnableCrop");
		caseEnableCrop.setBounds(8, 16, caseEnableCrop.getPreferredSize().width + 4, 23);	
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
						VideoPlayer.resizeAll();
						
						VideoPlayer.frameIsComplete = false;
						
						VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame);
					}
					
					//Important
					selection.setBounds(VideoPlayer.player.getWidth() / 4, VideoPlayer.player.getHeight() / 4, VideoPlayer.player.getWidth() / 2, VideoPlayer.player.getHeight() / 2);	
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();
					
					for (Component c : grpCrop.getComponents())
					{
						c.setEnabled(true);
					}
					
					frameCropX = VideoPlayer.player.getLocation().x;
					frameCropY = VideoPlayer.player.getLocation().y;
					
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();					
					VideoPlayer.checkSelection();
					
					VideoPlayer.player.add(selection);
					VideoPlayer.player.add(overImage);
				}
				else
				{			
					//Come back to original DAR
					if (Shutter.inputDeviceIsRunning == false && ratioChanged && VideoPlayer.videoPath != null)
					{
						FFPROBE.Data(VideoPlayer.videoPath);					
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
						VideoPlayer.resizeAll();
						
						VideoPlayer.frameIsComplete = false;
						
						VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame);
					}
					ratioChanged = false;	
					
					for (Component c : grpCrop.getComponents())
					{
						if (c instanceof JCheckBox == false)
						{
							c.setEnabled(false);
						}
					}
					
					VideoPlayer.player.remove(selection);
					VideoPlayer.player.remove(overImage);	
					
					comboPreset.setSelectedIndex(0);
				}		

				if (VideoPlayer.frameVideo != null)
				{
					VideoPlayer.player.repaint();
					FFPROBE.setFilesize();
				}
			}

		});
		
		JLabel lblPresets = new JLabel(Shutter.language.getProperty("lblPresets"));
		lblPresets.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblPresets.setEnabled(false);
		lblPresets.setBounds(caseEnableCrop.getX() + caseEnableCrop.getWidth() + 7, caseEnableCrop.getY() + 3, lblPresets.getPreferredSize().width, 16);		
		grpCrop.add(lblPresets);
		
		final String presetsList[] = { language.getProperty("aucun"), "auto", "2.75", "2.55", "2.39", "2.35", "2.33", "1.91", "1.85", "16/9", "4/3", "4/5", "1", "9/16"};
		
		comboPreset = new JComboBox<String>();
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
					selection.setBounds(VideoPlayer.player.getWidth() / 4, VideoPlayer.player.getHeight() / 4, VideoPlayer.player.getWidth() / 2, VideoPlayer.player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();	
					
					VideoPlayer.checkSelection();
				}	
				else if (comboPreset.getSelectedIndex() == 1) //Auto
				{	
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {							
							
							File file = new File(VideoPlayer.videoPath);
							
							FFMPEG.setCropDetect(file);
							
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
						
						int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);	
						int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);
						int width = (int) Math.ceil((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
						int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);
						
						if (width > VideoPlayer.player.getWidth())
							width = VideoPlayer.player.getWidth();
						
						if (height > VideoPlayer.player.getHeight())
							height = VideoPlayer.player.getHeight();

						selection.setBounds(x, y, width, height);

			        } catch (Exception er) {}	
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
					if (mouseInPictureY >= 0 && mouseInPictureX <= VideoPlayer.player.getWidth())
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
						else if (mouseInPictureX > VideoPlayer.player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), VideoPlayer.player.getWidth() - selection.getX(), selection.getHeight());
						}		
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
				{										
					if (mouseInPictureX <= VideoPlayer.player.getWidth())
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
						selection.setBounds(selection.getX(), selection.getY(), VideoPlayer.player.getWidth() - selection.getX(), selection.getHeight());
					}
															
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
				{
					if (mouseInPictureY <= VideoPlayer.player.getHeight() && mouseInPictureX <= VideoPlayer.player.getWidth())
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
						if (mouseInPictureY > VideoPlayer.player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayer.player.getHeight() - selection.getY());
						}
						else if (mouseInPictureX > VideoPlayer.player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), VideoPlayer.player.getWidth() - selection.getX(), selection.getHeight());
						}	
					}
					
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
				{		
					if (mouseInPictureY < VideoPlayer.player.getHeight())
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
						selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayer.player.getHeight() - selection.getY());
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
				{					
					if (mouseInPictureY <= VideoPlayer.player.getHeight() && mouseInPictureX >= 0)
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
						if (mouseInPictureY > VideoPlayer.player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayer.player.getHeight() - selection.getY());
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
								
				if (selection.getX() + selection.getWidth() > VideoPlayer.player.getWidth())
				{
					selection.setLocation(VideoPlayer.player.getWidth() - selection.getWidth(), selection.getY());
				}
				
				if (selection.getY() + selection.getHeight() > VideoPlayer.player.getHeight())
				{
					selection.setLocation(selection.getX(), VideoPlayer.player.getHeight() - selection.getHeight());
				}
			
				//Anchor points
				anchorRight = selection.getLocation().x + selection.getWidth();
				anchorBottom = selection.getLocation().y + selection.getHeight();
				
				VideoPlayer.checkSelection();
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
				else if (selection.getLocation().x + selection.getWidth() > VideoPlayer.player.getWidth() && selection.getLocation().y <= 0)
				{
					selection.setLocation(VideoPlayer.player.getWidth() - selection.getWidth(), 0);
				}
				else if (selection.getLocation().x <= 0 && selection.getLocation().y + selection.getHeight() > VideoPlayer.player.getHeight())
				{
					selection.setLocation(0, VideoPlayer.player.getHeight() - selection.getHeight());
				}
				else if (selection.getLocation().x + selection.getWidth() > VideoPlayer.player.getWidth() && selection.getLocation().y + selection.getHeight() > VideoPlayer.player.getHeight())
				{
					selection.setLocation(VideoPlayer.player.getWidth() - selection.getWidth(), VideoPlayer.player.getHeight() - selection.getHeight());
				}
				else if (selection.getLocation().x + selection.getWidth() > VideoPlayer.player.getWidth())
				{
					selection.setLocation(VideoPlayer.player.getWidth() - selection.getWidth(), selection.getLocation().y);
				}
				else if (selection.getLocation().y + selection.getHeight() > VideoPlayer.player.getHeight())
				{
					selection.setLocation(selection.getLocation().x, VideoPlayer.player.getHeight() - selection.getHeight());
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

				this.setBounds(0,0,VideoPlayer.player.getWidth(), VideoPlayer.player.getHeight());
				
				Area outter = new Area(new Rectangle(0, 0, VideoPlayer.player.getWidth(), VideoPlayer.player.getHeight()));
                Rectangle inner = new Rectangle(selection.getLocation().x, selection.getLocation().y, selection.getWidth(), selection.getHeight());
                outter.subtract(new Area(inner));
                 
                g2d.setColor(new Color(0,0,0,180));
                g2d.fill(outter);
                
                FFPROBE.setFilesize();				
			}
		};		
		overImage.setSize(640,360);
		overImage.setOpaque(false);
		overImage.setLayout(null);   
		
		overImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					selection.setBounds(VideoPlayer.player.getWidth() / 4, VideoPlayer.player.getHeight() / 4, VideoPlayer.player.getWidth() / 2, VideoPlayer.player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();	
					VideoPlayer.checkSelection();					
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
					int value = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);	
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
					selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / playerRatio), selection.getLocation().y);
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
					int value = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);	
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
					selection.setLocation(selection.getLocation().x, (int) Math.round(Integer.valueOf(textCropPosY.getText()) / playerRatio));
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
					int value = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(value, selection.getHeight());
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
					int value = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(value , selection.getHeight());
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
					int value = (int) Math.round((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);
					selection.setSize(selection.getWidth(), value);
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
					int value = (int) Math.round((float)  (Integer.valueOf(textCropHeight.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
					selection.setSize(selection.getWidth(), value);
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
	
	private void grpOverlay() {
	
		grpOverlay = new JPanel();
		grpOverlay.setLayout(null);
		grpOverlay.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("caseAddOverlay") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpOverlay.setBackground(new Color(30,30,35));
		grpOverlay.setSize(312, 17);
		grpOverlay.setLocation(frame.getWidth(), grpCrop.getY() + grpCrop.getHeight() + 6);
		grpOverlay.setVisible(false);
		frame.getContentPane().add(grpOverlay);		
		
		grpOverlay.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpOverlay, 278);
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
		lblTcBackground.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblTcBackground.setBackground(new Color(42,42,47));
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
					lblTcBackground.setText(language.getProperty("aucun"));
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
				
				Shutter.backgroundColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), Color.WHITE);
				
				if (Shutter.backgroundColor != null)
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
				
		caseAddTimecode = new JCheckBox(Shutter.language.getProperty("caseAddTimecode"));
		caseAddTimecode.setName("caseAddTimecode");		
		caseAddTimecode.setSelected(false);
		caseAddTimecode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddTimecode.setSize(caseAddTimecode.getPreferredSize().width, 23);
		caseAddTimecode.setLocation(8, lblColor.getY() + lblColor.getHeight() + 9);
		grpOverlay.add(caseAddTimecode);
		
		timecode = new JPanel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {		
				
				if ((caseAddTimecode.isSelected() || caseShowTimecode.isSelected()) && liste.getSize() > 0 && VideoPlayer.videoPath != null)
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
			        
					Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(textTcSize.getText()) / playerRatio));
			        font.deriveFont((float) Integer.parseInt(textTcSize.getText()) / playerRatio);
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
						else if (caseShowTimecode.isSelected() && FFPROBE.timecode1 == "" == false)
						{
							tcH = Integer.valueOf(FFPROBE.timecode1);
							tcM = Integer.valueOf(FFPROBE.timecode2);
							tcS = Integer.valueOf(FFPROBE.timecode3);
							tcF = Integer.valueOf(FFPROBE.timecode4);
						}
						
						tcH = tcH * 3600 * FFPROBE.currentFPS;
						tcM = tcM * 60 * FFPROBE.currentFPS;
						tcS = tcS * FFPROBE.currentFPS;
						
						float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
		
						if (caseShowTimecode.isSelected())
						{
							timeIn = 0;
						}
						
						float currentTime = Timecode.setNonDropFrameTC(VideoPlayer.playerCurrentFrame);
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
			        
			        //Checking screen DPI
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
					
					float dpiScaleFactor = 1.0f;
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			        	dpiScaleFactor = (float) (trueHorizontalLines / scaledHorizontalLines);
			        }
			        
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
					{
						g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					}
					else if (System.getProperty("os.name").contains("Windows"))
					{
						g2.fillRect(0, 0, (int) Math.round(bounds.width * dpiScaleFactor), (int) Math.round(bounds.height * dpiScaleFactor));
					}
					else
						g2.fillRect(0, 0, bounds.width, bounds.height);
					
					if (lblTcBackground.getText().equals(language.getProperty("aucun")))
					{
						g2.setColor(new Color(foregroundColor.getRed(),foregroundColor.getGreen(),foregroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textTcOpacity.getText()) * 255) /  100)));
					}
					else				
						g2.setColor(foregroundColor);
					
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
			        {
			        	Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2, bounds.height / 2 - offset / 2);	
			        }
					else if (System.getProperty("os.name").contains("Windows"))
					{
						Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2 * dpiScaleFactor, (bounds.height - offset) * dpiScaleFactor);		
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
						timecode.setLocation(VideoPlayer.player.getWidth() / 2 - timecode.getWidth() / 2, timecode.getHeight());
						tcLocX = timecode.getLocation().x;
						tcLocY = timecode.getLocation().y;
					}		
					else if (textTcSize.hasFocus())
					{					
						timecode.setLocation(timecode.getX() + (width - timecode.getWidth()) / 2, timecode.getY() + (height - timecode.getHeight()) / 2);	
						tcLocX = timecode.getLocation().x;
						tcLocY = timecode.getLocation().y;
					}
					
					VideoPlayer.refreshTimecodeAndText();
				}
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
					timecode.setLocation(VideoPlayer.player.getWidth() / 2 - timecode.getWidth() / 2, timecode.getHeight());
					
					VideoPlayer.refreshTimecodeAndText();
					
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
				textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * playerRatio)));
				textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * playerRatio)));  
			}
	
			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
				
		lblTimecode = new JLabel(Shutter.language.getProperty("lblTimecode"));
		lblTimecode.setName("lblTimecode");
		lblTimecode.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblTimecode.setBackground(new Color(42,42,47));
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
				
				if (VideoPlayer.frameVideo != null)
				{
					VideoPlayer.player.repaint();
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
		
		TC1	= new JTextField("00");
		TC1.setName("TC1");
		TC1.setEnabled(false);
		TC1.setText("00");
		TC1.setHorizontalAlignment(SwingConstants.CENTER);
		TC1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC1.setColumns(10);
		TC1.setBounds(lblTimecode.getX() + lblTimecode.getWidth() + 7, caseAddTimecode.getY(), 32, 21);
		grpOverlay.add(TC1);
		
		TC2	= new JTextField("00");
		TC2.setName("TC2");
		TC2.setEnabled(false);
		TC2.setText("00");
		TC2.setHorizontalAlignment(SwingConstants.CENTER);
		TC2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC2.setColumns(10);
		TC2.setBounds(TC1.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC2);
		
		TC3	= new JTextField("00");
		TC3.setName("TC3");
		TC3.setEnabled(false);
		TC3.setText("00");
		TC3.setHorizontalAlignment(SwingConstants.CENTER);
		TC3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 14));
		TC3.setColumns(10);
		TC3.setBounds(TC2.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC3);
		
		TC4	= new JTextField("00");
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
				
		caseShowTimecode = new JCheckBox(Shutter.language.getProperty("caseShowTimecode"));
		caseShowTimecode.setName("caseShowTimecode");
		caseShowTimecode.setSelected(false);
		caseShowTimecode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseShowTimecode.setEnabled(true);
		caseShowTimecode.setSize(caseShowTimecode.getPreferredSize().width, 23);
		caseShowTimecode.setLocation(caseAddTimecode.getX(), caseAddTimecode.getY() + caseAddTimecode.getHeight());
		grpOverlay.add(caseShowTimecode);
				
		JLabel posX = new JLabel(Shutter.language.getProperty("posX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setAlignmentX(SwingConstants.RIGHT);
		posX.setEnabled(false);
		posX.setBounds(24,  caseShowTimecode.getY() + caseShowTimecode.getHeight() + 6, posX.getPreferredSize().width, 16);
		grpOverlay.add(posX);
		
		textTcPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().x * playerRatio) ) ) );
		textTcPosX.setName("textTcPosX");
		textTcPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textTcPosX.setEnabled(false);
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
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / playerRatio), timecode.getLocation().y);
				
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
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / playerRatio), timecode.getLocation().y);
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
		posY.setEnabled(false);
		posY.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setAlignmentX(SwingConstants.RIGHT);
		posY.setBounds(px1.getX() + px1.getWidth() + 30, posX.getLocation().y, posY.getPreferredSize().width, 16);
		grpOverlay.add(posY);
	
		textTcPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().y * playerRatio) ) ) );
		textTcPosY.setName("textTcPosY");
		textTcPosY.setEnabled(false);
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
					timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / playerRatio));
				
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
					timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / playerRatio));
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
		lblSizeTC.setEnabled(false);
		lblSizeTC.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSizeTC.setAlignmentX(SwingConstants.RIGHT);
		lblSizeTC.setForeground(Utils.themeColor);
		lblSizeTC.setBounds(textTcPosX.getX() - lblSizeTC.getPreferredSize().width - 2, posX.getY() + posX.getHeight() + 6, lblSizeTC.getPreferredSize().width + 2, 16);				
		grpOverlay.add(lblSizeTC);
		
		textTcSize = new JTextField(String.valueOf(Math.round((float) 27 * playerRatio)));
		textTcSize.setEnabled(false);
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
		lblOpacityTC.setEnabled(false);
		lblOpacityTC.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblOpacityTC.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityTC.setForeground(Utils.themeColor);
		lblOpacityTC.setBounds(textTcPosY.getX() - lblOpacityTC.getPreferredSize().width - 2, lblSizeTC.getLocation().y, lblOpacityTC.getPreferredSize().width, 16);	
		grpOverlay.add(lblOpacityTC);
		
		textTcOpacity = new JTextField("50");
		textTcOpacity.setName("textTcOpacity");
		textTcOpacity.setEnabled(false);
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
		
		caseAddText = new JCheckBox(Shutter.language.getProperty("caseShowText"));
		caseAddText.setName("caseAddText");
		caseAddText.setSelected(false);
		caseAddText.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddText.setSize(caseAddText.getPreferredSize().width + 4, 23);
		caseAddText.setLocation(caseAddTimecode.getX(), lblSizeTC.getY() + lblSizeTC.getHeight() + 8);
		grpOverlay.add(caseAddText);
		
		overlayText = new JTextField("");
		overlayText.setName("overlayText");
		overlayText.setEnabled(false);
		overlayText.setLocation(caseAddText.getLocation().x + caseAddText.getWidth() + 7, caseAddText.getLocation().y + 1);
		overlayText.setSize(grpOverlay.getWidth() - overlayText.getX() - 10, 21);
		overlayText.setHorizontalAlignment(SwingConstants.LEFT);
		overlayText.setFont(new Font("SansSerif", Font.PLAIN, 12));
		overlayText.setToolTipText("<html>{codec/function}<br>{preset}<br>{resolution/scale}<br>{width}<br>{height}<br>{ratio/aspect}<br>{framerate/fps}<br>{bitrate}<br>{timecode}<br>{duration/time}<br>{date}</html>");
		grpOverlay.add(overlayText);
	
		overlayText.addKeyListener(new KeyListener(){
			
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
													
								VideoPlayer.player.add(fileName);
								
								//Overimage need to be the last component added
								if (caseEnableCrop.isSelected())
								{
									VideoPlayer.player.remove(selection);
									VideoPlayer.player.remove(overImage);
									VideoPlayer.player.add(selection);
									VideoPlayer.player.add(overImage);
								}		
								
								fileName.repaint();
							}
						});
						changeText.start();
					}
				}
			}		
			
		});
	
		caseShowFileName = new JCheckBox(Shutter.language.getProperty("caseShowFileName"));
		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setSelected(false);
		caseShowFileName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
		caseShowFileName.setLocation(caseAddText.getX(), caseAddText.getY() + caseAddText.getHeight());
		grpOverlay.add(caseShowFileName);
		
		fileName = new JPanel() {
			
			@Override
		    protected void paintComponent(Graphics g)
		    {			
				if ((caseShowFileName.isSelected() || caseAddText.isSelected() && overlayText.getText().length() > 0) && liste.getSize() > 0 && VideoPlayer.videoPath != null)
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
			        
			        Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, (int) Math.round((float) Integer.parseInt(textNameSize.getText()) / playerRatio));
			        font.deriveFont((float) Integer.parseInt(textNameSize.getText()) / playerRatio);
			        g2.setFont(font);
	
			        String file = VideoPlayer.videoPath;
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
					{
						str = FunctionUtils.setSuffix(overlayText.getText(), true);						
					}
					
			        Rectangle bounds = getStringBounds(g2, str, 0 ,0);
			        		        								
					if (lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
						g2.setColor(new Color(backgroundColor.getRed(),backgroundColor.getGreen(),backgroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textNameOpacity.getText()) * 255) /  100)));
					else
						g2.setColor(new Color(0,0,0,0));
		
					GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			        AffineTransform transform = gfxConfig.getDefaultTransform();
					
			        //Checking screen DPI
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
					
					float dpiScaleFactor = 1.0f;
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			        	dpiScaleFactor = (float) (trueHorizontalLines / scaledHorizontalLines);
			        }
			        
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
					{
						g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					}
					else if (System.getProperty("os.name").contains("Windows"))
					{
						g2.fillRect(0, 0, (int) Math.round(bounds.width * dpiScaleFactor), (int) Math.round(bounds.height * dpiScaleFactor));
					}
					else
						g2.fillRect(0, 0, bounds.width, bounds.height);
					
					if (lblTcBackground.getText().equals(language.getProperty("aucun")))
						g2.setColor(new Color(foregroundColor.getRed(),foregroundColor.getGreen(),foregroundColor.getBlue(), (int) ( (float) (Integer.parseInt(textNameOpacity.getText()) * 255) /  100)));
					else				
						g2.setColor(foregroundColor);				
					
					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = RetinaScreen
			        {
			        	Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2, bounds.height / 2 - offset / 2);	
			        }
					else if (System.getProperty("os.name").contains("Windows"))
					{
						Integer offset = bounds.height + (int) bounds.getY();						
						g2.drawString(str, -2 * dpiScaleFactor, (bounds.height - offset) * dpiScaleFactor);
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
						fileName.setLocation(VideoPlayer.player.getWidth() / 2 - fileName.getWidth() / 2, VideoPlayer.player.getHeight() -  fileName.getHeight() - fileName.getHeight() / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					}
					else if (textNameSize.hasFocus())
					{
						fileName.setLocation(fileName.getX() + (width - fileName.getWidth()) / 2, fileName.getY() + (height - fileName.getHeight()) / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					}
					
					VideoPlayer.refreshTimecodeAndText();
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
					fileName.setLocation(VideoPlayer.player.getWidth() / 2 - fileName.getWidth() / 2, VideoPlayer.player.getHeight() -  fileName.getHeight() - fileName.getHeight() / 2);
					
					VideoPlayer.refreshTimecodeAndText();
					
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
				textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * playerRatio)));
				textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * playerRatio)));  
			}
	
			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
		
		JLabel posX2 = new JLabel(Shutter.language.getProperty("posX"));
		posX2.setEnabled(false);
		posX2.setHorizontalAlignment(SwingConstants.LEFT);
		posX2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posX2.setForeground(Utils.themeColor);
		posX2.setAlignmentX(SwingConstants.RIGHT);
		posX2.setBounds(posX.getX(), caseShowFileName.getY() + caseShowFileName.getHeight() + 6, posX.getWidth(), 16);
		grpOverlay.add(posX2);
		
		textNamePosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().x * playerRatio) ) ) );
		textNamePosX.setName("textNamePosX");
		textNamePosX.setEnabled(false);
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
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / playerRatio), fileName.getLocation().y);
				
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
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / playerRatio), fileName.getLocation().y);
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
		posY2.setEnabled(false);
		posY2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		posY2.setForeground(Utils.themeColor);
		posY2.setAlignmentX(SwingConstants.RIGHT);
		posY2.setBounds(px3.getX() + px3.getWidth() + 30, posX2.getLocation().y, posX.getWidth(), 16);
		grpOverlay.add(posY2);
		
		textNamePosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().y * playerRatio) ) ) );
		textNamePosY.setEnabled(false);
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
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / playerRatio));
				
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
					fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / playerRatio));
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
		lblSizeName.setEnabled(false);
		lblSizeName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSizeName.setAlignmentX(SwingConstants.RIGHT);
		lblSizeName.setForeground(Utils.themeColor);
		lblSizeName.setBounds(lblSizeTC.getX(), posX2.getY() + posX2.getHeight() + 6, lblSizeTC.getWidth(), 16);		
		grpOverlay.add(lblSizeName);
		
		textNameSize = new JTextField(String.valueOf(Math.round((float) 27 * playerRatio )));
		textNameSize.setEnabled(false);
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
		lblOpacityName.setEnabled(false);
		lblOpacityName.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblOpacityName.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityName.setForeground(Utils.themeColor);
		lblOpacityName.setBounds(lblOpacityTC.getX(), lblSizeName.getLocation().y, lblOpacityTC.getWidth(), 16);		
		grpOverlay.add(lblOpacityName);
		
		textNameOpacity = new JTextField("50");
		textNameOpacity.setEnabled(false);
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

		caseAddTimecode.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseAddTimecode.isSelected())
				{
					posX.setEnabled(true);
					textTcPosX.setEnabled(true);
					px1.setEnabled(true);
					posY.setEnabled(true);
					textTcPosY.setEnabled(true);
					px2.setEnabled(true);
					lblSizeTC.setEnabled(true);
					textTcSize.setEnabled(true);
					percent1.setEnabled(true);
					lblOpacityTC.setEnabled(true);
					textTcOpacity.setEnabled(true);
					percent2.setEnabled(true);
					
					timecode.repaint();
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					TC4.setEnabled(true);	
					caseShowTimecode.setSelected(false);					
					VideoPlayer.player.add(timecode);
					
					//Overimage need to be the last component added
					if (caseEnableCrop.isSelected())
					{
						VideoPlayer.player.remove(selection);
						VideoPlayer.player.remove(overImage);
						VideoPlayer.player.add(selection);
						VideoPlayer.player.add(overImage);
					}			
					
				} 
				else
				{
					posX.setEnabled(false);
					textTcPosX.setEnabled(false);
					px1.setEnabled(false);
					posY.setEnabled(false);
					textTcPosY.setEnabled(false);
					px2.setEnabled(false);
					lblSizeTC.setEnabled(false);
					textTcSize.setEnabled(false);
					percent1.setEnabled(false);
					lblOpacityTC.setEnabled(false);
					textTcOpacity.setEnabled(false);
					percent2.setEnabled(false);
					
					FFPROBE.timecode1 = "";
					FFPROBE.timecode2 = "";
					FFPROBE.timecode3 = "";
					FFPROBE.timecode4 = "";
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					TC4.setEnabled(false);					
					VideoPlayer.player.remove(timecode);
				}
				
				VideoPlayer.refreshTimecodeAndText();
				
				VideoPlayer.player.repaint();
			}
	
		});
		
		caseShowTimecode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseShowTimecode.isSelected() && liste.getSize() > 0)
				{
					posX.setEnabled(true);
					textTcPosX.setEnabled(true);
					px1.setEnabled(true);
					posY.setEnabled(true);
					textTcPosY.setEnabled(true);
					px2.setEnabled(true);
					lblSizeTC.setEnabled(true);
					textTcSize.setEnabled(true);
					percent1.setEnabled(true);
					lblOpacityTC.setEnabled(true);
					textTcOpacity.setEnabled(true);
					percent2.setEnabled(true);
					
					//Timecode info
					if (Utils.inputDeviceIsRunning == false)
					{			
						FFPROBE.analyzedMedia = null;
						FFPROBE.Data(VideoPlayer.videoPath);
						
						do
						{
							try {
		        				Thread.sleep(100);
		        			} catch (InterruptedException e1) {}
						}
						while (FFPROBE.isRunning);
						
						if (FFPROBE.timecode1 == "")
		    			{
							MEDIAINFO.run(VideoPlayer.videoPath, false);
		    				
		    				do {
		    					try {
			        				Thread.sleep(100);
			        			} catch (InterruptedException e1) {}
		    				} while (MEDIAINFO.isRunning);		    				
		    			}
						
						if (FFPROBE.timecode1 == "")
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
							VideoPlayer.player.add(timecode);
							
							//Overimage need to be the last component added
							if (caseEnableCrop.isSelected())
							{
								VideoPlayer.player.remove(selection);
								VideoPlayer.player.remove(overImage);
								VideoPlayer.player.add(selection);
								VideoPlayer.player.add(overImage);
							}
						}						
					}					
				}				
				else
				{
					posX.setEnabled(false);
					textTcPosX.setEnabled(false);
					px1.setEnabled(false);
					posY.setEnabled(false);
					textTcPosY.setEnabled(false);
					px2.setEnabled(false);
					lblSizeTC.setEnabled(false);
					textTcSize.setEnabled(false);
					percent1.setEnabled(false);
					lblOpacityTC.setEnabled(false);
					textTcOpacity.setEnabled(false);
					percent2.setEnabled(false);
					
					VideoPlayer.player.remove(timecode);
				}
				
				VideoPlayer.refreshTimecodeAndText();
				
				VideoPlayer.player.repaint();
			}
	
		});
			
		caseAddText.addActionListener(new ActionListener()	{
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAddText.isSelected())
				{
					posX2.setEnabled(true);
					textNamePosX.setEnabled(true);
					px3.setEnabled(true);
					posY2.setEnabled(true);
					textNamePosY.setEnabled(true);
					px4.setEnabled(true);
					lblSizeName.setEnabled(true);
					textNameSize.setEnabled(true);
					percent3.setEnabled(true);
					lblOpacityName.setEnabled(true);
					textNameOpacity.setEnabled(true);
					percent4.setEnabled(true);
					
					caseShowFileName.setSelected(false);
					overlayText.setEnabled(true);
					
					if (overlayText.getText().length() > 0)
					{
						VideoPlayer.player.add(fileName);
						
						//Overimage need to be the last component added
						if (caseEnableCrop.isSelected())
						{
							VideoPlayer.player.remove(selection);
							VideoPlayer.player.remove(overImage);
							VideoPlayer.player.add(selection);
							VideoPlayer.player.add(overImage);
						}
					}	
				}
				else
				{
					posX2.setEnabled(false);
					textNamePosX.setEnabled(false);
					px3.setEnabled(false);
					posY2.setEnabled(false);
					textNamePosY.setEnabled(false);
					px4.setEnabled(false);
					lblSizeName.setEnabled(false);
					textNameSize.setEnabled(false);
					percent3.setEnabled(false);
					lblOpacityName.setEnabled(false);
					textNameOpacity.setEnabled(false);
					percent4.setEnabled(false);
					
					overlayText.setEnabled(false);
					VideoPlayer.player.remove(fileName);
				}
				
				VideoPlayer.refreshTimecodeAndText();
				
				VideoPlayer.player.repaint();	
		
			}
		});
		
		caseShowFileName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseShowFileName.isSelected())
				{
					posX2.setEnabled(true);
					textNamePosX.setEnabled(true);
					px3.setEnabled(true);
					posY2.setEnabled(true);
					textNamePosY.setEnabled(true);
					px4.setEnabled(true);
					lblSizeName.setEnabled(true);
					textNameSize.setEnabled(true);
					percent3.setEnabled(true);
					lblOpacityName.setEnabled(true);
					textNameOpacity.setEnabled(true);
					percent4.setEnabled(true);
					
					caseAddText.setSelected(false);
					overlayText.setEnabled(false);
					VideoPlayer.player.add(fileName);
										 
					//Overimage need to be the last component added
					if (caseEnableCrop.isSelected())
					{
						VideoPlayer.player.remove(selection);
						VideoPlayer.player.remove(overImage);
						VideoPlayer.player.add(selection);
						VideoPlayer.player.add(overImage);
					}
				}
				else
				{
					posX2.setEnabled(false);
					textNamePosX.setEnabled(false);
					px3.setEnabled(false);
					posY2.setEnabled(false);
					textNamePosY.setEnabled(false);
					px4.setEnabled(false);
					lblSizeName.setEnabled(false);
					textNameSize.setEnabled(false);
					percent3.setEnabled(false);
					lblOpacityName.setEnabled(false);
					textNameOpacity.setEnabled(false);
					percent4.setEnabled(false);
					
					VideoPlayer.player.remove(fileName);
				}
				
				VideoPlayer.refreshTimecodeAndText();
	
				VideoPlayer.player.repaint();
			}
			
		});
			
	}

	private void grpSubtitles() {
	
		grpSubtitles = new JPanel();
		grpSubtitles.setLayout(null);
		grpSubtitles.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("caseSubtitles") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpSubtitles.setBackground(new Color(30,30,35));
		grpSubtitles.setSize(312, 17);
		grpSubtitles.setLocation(frame.getWidth(), grpOverlay.getY() + grpOverlay.getHeight() + 6);
		grpSubtitles.setVisible(false);
		frame.getContentPane().add(grpSubtitles);		
		
		grpSubtitles.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpSubtitles, 131);
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
			
		caseAddSubtitles = new JCheckBox(Shutter.language.getProperty("caseSubtitles"));
		caseAddSubtitles.setName("caseAddSubtitles");
		caseAddSubtitles.setBounds(8, 16, caseAddSubtitles.getPreferredSize().width + 8, 23);	
		caseAddSubtitles.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseAddSubtitles.setSelected(false);
		grpSubtitles.add(caseAddSubtitles);
				
		caseAddSubtitles.addActionListener(new ActionListener() {
	
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent arg0) {			
				
				if (caseAddSubtitles.isSelected() && VideoPlayer.videoPath != null)
				{											
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
							VideoPlayer.setMedia();
							
							caseAddSubtitles.setSelected(true);
							
							Shutter.subtitlesBurn = true;		
							subtitlesFilePath = SubtitlesTimeline.srt;
							VideoPlayer.writeSub(subtitlesFilePath.toString(), StandardCharsets.UTF_8);	
													
							subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
						    		(int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));	
							
							subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);				
							VideoPlayer.player.add(subsCanvas);
							
							grpSubtitles.setSize(grpSubtitles.getWidth(), 131);	
							grpWatermark.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
							
							if (grpWatermark.getY() + grpWatermark.getHeight() >= 156 - 6)
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
							subtitlesFilePath = new File(SubtitlesTimeline.srt.toString());
							Shutter.caseDisplay.setSelected(false);
	
							//On copy le .srt dans le fichier
							Thread copySRT = new Thread(new Runnable()
							{
								@SuppressWarnings("deprecation")
								@Override
								public void run() {
									
									try {						
										
										Shutter.disableAll();
										
										File fileIn = new File(VideoPlayer.videoPath);
										String extension = VideoPlayer.videoPath.toString().substring(fileIn.toString().lastIndexOf("."));
										File fileOut = new File(fileIn.toString().replace(extension, "_subs" + extension));
										
										//Envoi de la commande
										String cmd = " -c copy -c:s mov_text -map v:0? -map a? -map 1:s -y ";
										
										if (extension.equals(".mkv"))
											cmd = " -c copy -c:s srt -map v:0? -map a? -map 1:s -y ";							
										
										FFMPEG.run(" -i " + '"' + fileIn + '"' + " -i " + '"' + subtitlesFilePath + '"' + cmd + '"'  + fileOut + '"');	
										
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
						File video = new File(fileList.getSelectedValue().toString());
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
							dialog.setDirectory(new File(VideoPlayer.videoPath).getParent());
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
								{
									Shutter.subtitlesFile = new File(dialog.getFile());
								}
								else
									Shutter.subtitlesFile = new File(Shutter.dirTemp + dialog.getFile());
								
								if (input.equals(".srt") || input.equals(".vtt"))
								{
									int sub = 0;									
									if (autoBurn == false && autoEmbed == false)
									{
										Object[] options = {Shutter.language.getProperty("subtitlesBurn"), Shutter.language.getProperty("subtitlesEmbed")};
										
										if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")) || Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut")))
											sub = 1;
										
										if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
										&& Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC") == false
										&& Shutter.caseCreateOPATOM.isSelected() == false
										&& Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")) == false
										&& Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut")) == false)
										{
											sub = JOptionPane.showOptionDialog(frame, Shutter.language.getProperty("chooseSubsIntegration"), Shutter.language.getProperty("caseAddSubtitles"),
													JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												    options,
												    options[0]);
										}
									}
									else if (autoEmbed)
									{
										sub = 1;
									}
										
									if (sub == 0) //Burn
									{
										Shutter.subtitlesBurn = true;							
																
										//Conversion du .vtt en .srt
										if (input.equals(".vtt"))
										{
											subtitlesFilePath = new File(Shutter.subtitlesFile.toString().replace(".vtt", ".srt"));
											
											FFMPEG.run(" -i " + '"' + dialog.getDirectory() + dialog.getFile().toString() + '"' + " -y " + '"' + subtitlesFilePath.toString().replace(".srt", "_vtt.srt") + '"');
											
											do {
												try {
													Thread.sleep(10);
												} catch (InterruptedException e) {}
											} while(FFMPEG.runProcess.isAlive());
											
											Shutter.enableAll();
											
											Shutter.subtitlesFile = new File(subtitlesFilePath.toString().replace(".srt", "_vtt.srt"));
										}
										else											
											subtitlesFilePath = new File(dialog.getDirectory() + dialog.getFile().toString());			
										
										VideoPlayer.writeSub(subtitlesFilePath.toString(), StandardCharsets.UTF_8);		
										
										subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
									    		(int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));	
										
										subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);				
										VideoPlayer.player.add(subsCanvas);
										
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
										
										if (autoEmbed == false)
										{
											JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("previewNotAvailable"), Shutter.language.getProperty("caseSubtitles"), JOptionPane.INFORMATION_MESSAGE);
										}
									}	
									
									//Important
									VideoPlayer.sliderSpeed.setEnabled(false);
									VideoPlayer.sliderSpeed.setValue(2);
									VideoPlayer.lblSpeed.setText("x1");
									VideoPlayer.lblSpeed.setBounds(VideoPlayer.sliderSpeed.getX() - VideoPlayer.lblSpeed.getPreferredSize().width - 2, VideoPlayer.sliderSpeed.getY() + 2, VideoPlayer.lblSpeed.getPreferredSize().width, 16);
								}
								else //SSA or ASS or SCC
								{									
									Object[] options = {Shutter.language.getProperty("subtitlesBurn"), Shutter.language.getProperty("subtitlesEmbed")};
									
									int sub = 0;
									if (autoBurn == false && autoEmbed == false)
									{
										if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
										&& Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC") == false
										&& Shutter.caseCreateOPATOM.isSelected() == false)
										{
											sub = JOptionPane.showOptionDialog(frame, Shutter.language.getProperty("chooseSubsIntegration"), Shutter.language.getProperty("caseAddSubtitles"),
													JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												    options,
												    options[0]);
										}
									}
									else if (autoEmbed)
									{
										sub = 1;
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
									
									if (autoEmbed == false)
									{
										JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("previewNotAvailable"), Shutter.language.getProperty("caseSubtitles"), JOptionPane.INFORMATION_MESSAGE);
									}	
								}
								
								//Important
								VideoPlayer.sliderSpeed.setEnabled(false);
								VideoPlayer.sliderSpeed.setValue(2);
								VideoPlayer.lblSpeed.setText("x1");
								VideoPlayer.lblSpeed.setBounds(VideoPlayer.sliderSpeed.getX() - VideoPlayer.lblSpeed.getPreferredSize().width - 2, VideoPlayer.sliderSpeed.getY() + 2, VideoPlayer.lblSpeed.getPreferredSize().width, 16);
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
					
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
					|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))
					|| subtitlesBurn == false)
					{
						Shutter.casePreserveSubs.setSelected(false);
					}
				} 
				else 
				{
					caseAddSubtitles.setSelected(false);

					//IMPORTANT Enable caseDisplay		
					Shutter.subtitlesBurn = true; 			
					changeSections(false);
					
					VideoPlayer.player.remove(subsCanvas);
					
					if (autoBurn == false && autoEmbed == false)
					{
						VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
					}
	
					for (Component c : grpSubtitles.getComponents())
					{
						if (c instanceof JCheckBox == false)
						{
							c.setEnabled(false);
						}
					}
					
					VideoPlayer.sliderSpeed.setEnabled(true);
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
				VideoPlayer.loadImage(false);				
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
				
				VideoPlayer.loadImage(true);
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
				
				VideoPlayer.loadImage(true);
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
				
				VideoPlayer.refreshSubtitles();
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
					VideoPlayer.refreshSubtitles();
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
	
		textSubsWidth = new JTextField();
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
						subsCanvas.setBounds(0, 0, VideoPlayer.player.getWidth(), (int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));
					}
					else
					{
						subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
					    		(int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));	
						
						subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
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
				VideoPlayer.loadImage(true);
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
					subsCanvas.setBounds(0, 0, VideoPlayer.player.getWidth(), (int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));
				}
				else
				{
					subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
				    		(int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));	
					
					subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
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
					VideoPlayer.loadImage(true);
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
				
				VideoPlayer.loadImage(false);
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
	
					VideoPlayer.loadImage(false);	
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
		lblSubsBackground.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblSubsBackground.setBackground(new Color(42,42,47));
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
					
				VideoPlayer.writeCurrentSubs(VideoPlayer.playerCurrentFrame, false);
				VideoPlayer.loadImage(true);					
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
					VideoPlayer.loadImage(true);
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
				
				VideoPlayer.loadImage(false);
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
	
					VideoPlayer.loadImage(false);	
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
		subsCanvas.setSize(640,360);
		subsCanvas.setBackground(new Color(0,0,0,0));			
	
		for (Component c : grpSubtitles.getComponents())
		{
			if (c instanceof JCheckBox == false)
			{
				c.setEnabled(false);
			}
		}		
	}

	private void grpWatermark() {
			
		grpWatermark = new JPanel();
		grpWatermark.setLayout(null);
		grpWatermark.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("caseLogo") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpWatermark.setBackground(new Color(30,30,35));
		grpWatermark.setSize(312, 17);
		grpWatermark.setLocation(frame.getWidth(), grpSubtitles.getY() + grpSubtitles.getHeight() + 6);
		grpWatermark.setVisible(false);
		frame.getContentPane().add(grpWatermark);
		
		grpWatermark.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpWatermark, 113);
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
	  			
	  			if (logoPNG != null && VideoPlayer.videoPath != null)
	  			{
	  				g2d.drawImage(logoPNG, 0, 0, null);
	  			}
	  		}
	  	};
		logo.setLayout(null);        
		logo.setOpaque(false); 
		logo.setBackground(new Color(0,0,0,50));
		
		logo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					logo.setLocation((int) Math.floor(VideoPlayer.player.getWidth() / 2 - logo.getWidth() / 2), (int) Math.floor(VideoPlayer.player.getHeight() / 2 - logo.getHeight() / 2));	
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
				}
				else
		     	{
					logoPosX = e.getLocationOnScreen().x;
					logoPosY = e.getLocationOnScreen().y;
		     	}	
				
				watermarkPreset = null;
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
				textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
				textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
				
				watermarkPreset = null;
			}
	
			@Override
			public void mouseMoved(MouseEvent arg0) {				
			}
			
		});
			
		caseAddWatermark = new JCheckBox(Shutter.language.getProperty("frameLogo"));
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
					//Initiate location
					if (logo.getWidth() == 0)
					{
						logo.setSize(VideoPlayer.player.getWidth(), VideoPlayer.player.getHeight());
					}
					
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
							inputDevice.doClick();		
							inputDeviceIsRunning = false;
							grpImageAdjustement.setEnabled(true);
							Component[] components = Shutter.grpImageAdjustement.getComponents();
							for (int i = 0; i < components.length; i++) {
								components[i].setEnabled(true);
							}
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
							if (c instanceof JComboBox == false)
							{
								c.setEnabled(true);							
							}						
						}
						
						VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
			    		VideoPlayer.player.add(logo);
			    		
			    		textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
						textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
			    		
			    		//Overimage need to be the last component added
						if (caseEnableCrop.isSelected())
						{
							VideoPlayer.player.remove(selection);
							VideoPlayer.player.remove(overImage);
							VideoPlayer.player.add(selection);
							VideoPlayer.player.add(overImage);
						}
						
						VideoPlayer.resizeAll();
					}
					else
					{
						//Preset loaded
						if (logoFile != null)
						{
							for (Component c : grpWatermark.getComponents())
							{
								if (c instanceof JComboBox == false)
								{
									c.setEnabled(true);							
								}
							}
							
							VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
				    		VideoPlayer.player.add(logo);
				    		
				    		textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
							textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
				    		
				    		//Overimage need to be the last component added
							if (caseEnableCrop.isSelected())
							{
								VideoPlayer.player.remove(selection);
								VideoPlayer.player.remove(overImage);
								VideoPlayer.player.add(selection);
								VideoPlayer.player.add(overImage);
							}
						}
						else
						{						
							FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseLogo"), FileDialog.LOAD);
							dialog.setDirectory(new File(VideoPlayer.videoPath).getParent());
							dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
							dialog.setAlwaysOnTop(true);
							dialog.setMultipleMode(false);
							dialog.setVisible(true);
		
							if (dialog.getFile() != null)
							{
								logoFile = dialog.getDirectory() + dialog.getFile().toString();
								
								for (Component c : grpWatermark.getComponents())
								{
									if (c instanceof JComboBox == false)
									{
										c.setEnabled(true);							
									}
								}
								
								VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
					    		VideoPlayer.player.add(logo);
					    		
					    		textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
								textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
					    		
					    		//Overimage need to be the last component added
								if (caseEnableCrop.isSelected())
								{
									VideoPlayer.player.remove(selection);
									VideoPlayer.player.remove(overImage);
									VideoPlayer.player.add(selection);
									VideoPlayer.player.add(overImage);
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
					
					VideoPlayer.player.remove(logo);
					logoFile = null;
					logoPNG = null;
				}	

				VideoPlayer.player.repaint();
				
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
				g.drawRect(VideoPlayer.player.getX() + (int) ((float) ((float) VideoPlayer.player.getWidth() * 0.07) / 2), VideoPlayer.player.getY() +  (int) ((float) ((float) VideoPlayer.player.getHeight() * 0.07) / 2), (int) ((float) VideoPlayer.player.getWidth() * 0.93), (int) ((float) VideoPlayer.player.getHeight() * 0.93));
				
				//Title safe
				g.drawRect(VideoPlayer.player.getX() + (int) ((float) ((float) VideoPlayer.player.getWidth() * 0.1) / 2), VideoPlayer.player.getY() +  (int) ((float) ((float) VideoPlayer.player.getHeight() * 0.1) / 2), (int) ((float) VideoPlayer.player.getWidth() * 0.9), (int) ((float) VideoPlayer.player.getHeight() * 0.9));
			}
		};
		
		safeArea.setOpaque(false);
		safeArea.setBackground(new Color(0,0,0,0));
		safeArea.setSize(640,360);
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
					
		JLabel watermarkTopLeft = new JLabel("\u2196");
		watermarkTopLeft.setName("watermarkTopLeft");
		watermarkTopLeft.setBackground(new Color(42,42,47,0));
		watermarkTopLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTopLeft.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkTopLeft.setSize(28, 16);
		watermarkTopLeft.setLocation(grpWatermark.getWidth() / 2 - 270 / 2, caseAddWatermark.getY() + caseAddWatermark.getHeight() + 2);
		grpWatermark.add(watermarkTopLeft);
		
		JLabel watermarkLeft = new JLabel("\u2190");
		watermarkLeft.setName("watermarkLeft");
		watermarkLeft.setBackground(new Color(42,42,47,0));
		watermarkLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkLeft.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkLeft.setSize(watermarkTopLeft.getSize());
		watermarkLeft.setLocation(watermarkTopLeft.getX() + watermarkTopLeft.getWidth() + 2, watermarkTopLeft.getY() - 2);
		grpWatermark.add(watermarkLeft);
		
		JLabel watermarkBottomLeft = new JLabel("\u2199");
		watermarkBottomLeft.setName("watermarkBottomLeft");
		watermarkBottomLeft.setBackground(new Color(42,42,47,0));
		watermarkBottomLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottomLeft.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkBottomLeft.setSize(watermarkTopLeft.getSize());
		watermarkBottomLeft.setLocation(watermarkLeft.getX() + watermarkLeft.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottomLeft);
		
		JLabel watermarkTop = new JLabel("\u2191");
		watermarkTop.setName("watermarkTop");
		watermarkTop.setBackground(new Color(42,42,47,0));
		watermarkTop.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTop.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkTop.setSize(watermarkTopLeft.getSize());
		watermarkTop.setLocation(watermarkBottomLeft.getX() + watermarkBottomLeft.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkTop);
		
		JLabel watermarkCenter = new JLabel("\u2500");
		watermarkCenter.setName("watermarkCenter");
		watermarkCenter.setBackground(new Color(42,42,47,0));
		watermarkCenter.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkCenter.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkCenter.setSize(watermarkTopLeft.getSize());
		watermarkCenter.setLocation(watermarkTop.getX() + watermarkTop.getWidth() + 2, watermarkTopLeft.getY() - 1);
		grpWatermark.add(watermarkCenter);
		
		JLabel watermarkBottom = new JLabel("\u2193");
		watermarkBottom.setName("watermarkBottom");
		watermarkBottom.setBackground(new Color(42,42,47,0));
		watermarkBottom.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottom.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkBottom.setSize(watermarkTopLeft.getSize());
		watermarkBottom.setLocation(watermarkCenter.getX() + watermarkCenter.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottom);
		
		JLabel watermarkTopRight = new JLabel("\u2197");
		watermarkTopRight.setName("watermarkTopRight");
		watermarkTopRight.setBackground(new Color(42,42,47,0));
		watermarkTopRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTopRight.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkTopRight.setSize(watermarkTopLeft.getSize());
		watermarkTopRight.setLocation(watermarkBottom.getX() + watermarkBottom.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkTopRight);
		
		JLabel watermarkRight = new JLabel("\u2192");
		watermarkRight.setName("watermarkRight");
		watermarkRight.setBackground(new Color(42,42,47,0));
		watermarkRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkRight.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkRight.setSize(watermarkTopLeft.getSize());
		watermarkRight.setLocation(watermarkTopRight.getX() + watermarkTopRight.getWidth() + 2, watermarkTopLeft.getY() - 2);
		grpWatermark.add(watermarkRight);
		
		JLabel watermarkBottomRight = new JLabel("\u2198");
		watermarkBottomRight.setName("watermarkBottomRight");
		watermarkBottomRight.setBackground(new Color(42,42,47,0));
		watermarkBottomRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottomRight.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		watermarkBottomRight.setSize(watermarkTopLeft.getSize());
		watermarkBottomRight.setLocation(watermarkRight.getX() + watermarkRight.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottomRight);
		
		MouseListener watermarkPositions = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
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
			public void mousePressed(MouseEvent e) {
				VideoPlayer.watermarkPositions(e.getComponent().getName());
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {				
			}
			
		};
		
		watermarkTopLeft.addMouseListener(watermarkPositions);
		watermarkLeft.addMouseListener(watermarkPositions);
		watermarkBottomLeft.addMouseListener(watermarkPositions);
		watermarkTop.addMouseListener(watermarkPositions);
		watermarkCenter.addMouseListener(watermarkPositions);
		watermarkBottom.addMouseListener(watermarkPositions);
		watermarkTopRight.addMouseListener(watermarkPositions);
		watermarkRight.addMouseListener(watermarkPositions);
		watermarkBottomRight.addMouseListener(watermarkPositions);		
		
		JPanel panelForButtons = new JPanel() {
			
			@Override
			public void paintComponent(Graphics g) {
				
				Graphics2D g2d = (Graphics2D) g;
											
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        
				g2d.setColor(new Color(42,42,47));
				g2d.fillRoundRect(0, 0, (watermarkBottomRight.getX() + watermarkBottomRight.getWidth()) - watermarkTopLeft.getX() + 4, 18, 15, 15);
				
				watermarkTopLeft.repaint();
				watermarkLeft.repaint();
				watermarkBottomLeft.repaint();
				watermarkTop.repaint();
				watermarkCenter.repaint();
				watermarkBottom.repaint();
				watermarkTopRight.repaint();
				watermarkRight.repaint();
				watermarkBottomRight.repaint();;		
			}
			
		};
		panelForButtons.setBounds(watermarkTopLeft.getX() - 2, watermarkTopLeft.getY() - 2, (watermarkBottomRight.getX() + watermarkBottomRight.getWidth()) - watermarkTopLeft.getX() + 4, 18);
		grpWatermark.add(panelForButtons);
		
		JLabel watermarkPosX = new JLabel(Shutter.language.getProperty("posX"));
		watermarkPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkPosX.setEnabled(false);
		watermarkPosX.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkPosX.setForeground(Utils.themeColor);
		watermarkPosX.setBounds(24, watermarkBottomRight.getY() + watermarkBottomRight.getHeight() + 6, watermarkPosX.getPreferredSize().width, 16);
	
		textWatermarkPosX = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
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
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / playerRatio), logo.getLocation().y);
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
					
					watermarkPreset = null;
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
								
				watermarkPreset = null;
				
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
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / playerRatio), logo.getLocation().y);
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
	
		textWatermarkPosY = new JTextField(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );
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
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / playerRatio));
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
					
					watermarkPreset = null;
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
								
				watermarkPreset = null;
				
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
					logo.setLocation(logo.getLocation().x, (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / playerRatio));
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
			
		textWatermarkSize = new JTextField("100");
		textWatermarkSize.setName("textWatermarkSize");
		textWatermarkSize.setEnabled(false);
		textWatermarkSize.setBounds(textWatermarkPosX.getLocation().x, watermarkPosX.getY() + watermarkPosX.getHeight() + 4, 34, 16);
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

				watermarkPreset = null;
				
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkSize.getText());
			}
	
			@Override
			public void mouseReleased(MouseEvent e) {
				
				textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
				textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
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
					VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));	
					
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
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
					
					if (value < 2)
					{
						textWatermarkSize.setText("2");
					}
					else
						textWatermarkSize.setText(String.valueOf(value));

					VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
					textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio) ) ) );
					textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio) ) ) );  
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;				
				}
			}
	
			@Override
			public void mouseMoved(MouseEvent e) {		
			}
		});	
	
		JLabel watermarkSize = new JLabel(Shutter.language.getProperty("lblSize"));
		watermarkSize.setEnabled(false);
		watermarkSize.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkSize.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		watermarkSize.setForeground(Utils.themeColor);
		watermarkSize.setBounds(textWatermarkSize.getX() - watermarkSize.getPreferredSize().width - 2, watermarkPosX.getY() + watermarkPosX.getHeight() + 4, watermarkSize.getPreferredSize().width, 16);
			
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
		
	private void grpColorimetry() {
	
		grpColorimetry = new JPanel();
		grpColorimetry.setLayout(null);
		grpColorimetry.setVisible(false);
		grpColorimetry.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpColorimetry") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpColorimetry.setBackground(new Color(30,30,35));
		grpColorimetry.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpColorimetry);
		
		grpColorimetry.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpColorimetry, 165);
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
	
		caseGamma = new JCheckBox(language.getProperty("caseGamma"));
		caseGamma.setName("caseGamma");
		caseGamma.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseGamma.setBounds(7, 16, caseGamma.getPreferredSize().width, 22);
		grpColorimetry.add(caseGamma);
		
		caseGamma.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseGamma.isSelected()) 
				{
					comboGamma.setEnabled(true);
				}
				else
				{
					comboGamma.setEnabled(false);
				}
				
				//VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		comboGamma = new JComboBox<Object>(new String[] {"1.8","2.0","2.2","2.4","2.6"});		
		comboGamma.setName("comboGamma");
		comboGamma.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboGamma.setEditable(false);
		comboGamma.setEnabled(false);
		comboGamma.setSelectedIndex(3);
		comboGamma.setMaximumRowCount(20);
		comboGamma.setBounds(grpColorimetry.getWidth() - 130 - 7, caseGamma.getLocation().y + 4, 42, 16);
		grpColorimetry.add(comboGamma);
		
		comboGamma.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		caseLevels = new JCheckBox(language.getProperty("caseLevels"));
		caseLevels.setName("caseLevels");
		caseLevels.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLevels.setBounds(7, caseGamma.getLocation().y + caseGamma.getHeight(), caseLevels.getPreferredSize().width, 22);
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
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		comboInLevels = new JComboBox<Object>(new String[] {"16-235", "0-255"});		
		comboInLevels.setName("comboInLevels");
		comboInLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboInLevels.setEditable(false);
		comboInLevels.setEnabled(false);
		comboInLevels.setSelectedIndex(0);
		comboInLevels.setMaximumRowCount(20);
		comboInLevels.setBounds(grpColorimetry.getWidth() - 150 - 7, caseLevels.getLocation().y + 4, 62, 16);
		grpColorimetry.add(comboInLevels);
		
		comboInLevels.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				if (comboInLevels.getSelectedIndex() == 1)
					comboOutLevels.setSelectedIndex(0);
				else 
					comboOutLevels.setSelectedIndex(1);
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});	
		
		JLabel lblTo = new JLabel(">");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTo.setBounds(comboInLevels.getX() + comboInLevels.getWidth() + 5, caseLevels.getLocation().y + 4, lblTo.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo);
		
		comboOutLevels = new JComboBox<Object>(new String[] {"16-235", "0-255"});		
		comboOutLevels.setName("comboOutLevels");
		comboOutLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutLevels.setEditable(false);
		comboOutLevels.setEnabled(false);
		comboOutLevels.setSelectedIndex(1);
		comboOutLevels.setMaximumRowCount(20);
		comboOutLevels.setBounds(lblTo.getX() + lblTo.getWidth() + 4, caseLevels.getLocation().y + 4, comboInLevels.getWidth(), 16);
		grpColorimetry.add(comboOutLevels);
		
		comboOutLevels.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				if (comboOutLevels.getSelectedIndex() == 1)
					comboInLevels.setSelectedIndex(0);
				else 
					comboInLevels.setSelectedIndex(1);
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});	
		
		caseColormatrix = new JCheckBox(language.getProperty("caseColormatrix"));
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
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		comboInColormatrix = new JComboBox<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "HDR"});		
		comboInColormatrix.setName("comboInColormatrix");
		comboInColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboInColormatrix.setEditable(false);
		comboInColormatrix.setEnabled(false);
		comboInColormatrix.setSelectedIndex(0);
		comboInColormatrix.setMaximumRowCount(20);
		comboInColormatrix.setBounds(grpColorimetry.getWidth() - 160 - 7, caseColormatrix.getLocation().y + 4, 72, 16);
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
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});	
		
		JLabel lblTo2 = new JLabel(">");
		lblTo2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo2.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblTo2.setBounds(lblTo.getX(), caseColormatrix.getLocation().y + 2, lblTo2.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo2);
		
		comboOutColormatrix = new JComboBox<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "SDR"});		
		comboOutColormatrix.setName("comboOutColormatrix");
		comboOutColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutColormatrix.setEditable(false);
		comboOutColormatrix.setEnabled(false);
		comboOutColormatrix.setSelectedIndex(1);
		comboOutColormatrix.setMaximumRowCount(20);
		comboOutColormatrix.setBounds(grpColorimetry.getWidth() - 70 - 9, caseColormatrix.getLocation().y + 4, comboInColormatrix.getWidth(), 16);
		grpColorimetry.add(comboOutColormatrix);
		
		comboOutColormatrix.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				if (comboOutColormatrix.getSelectedItem().toString().equals("SDR"))
				{
					comboInColormatrix.setSelectedIndex(3);
				}
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});	
				
		caseColorspace = new JCheckBox(language.getProperty("caseColorspace"));
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
										
					if (comboColorspace.getSelectedItem().toString().contains("HDR"))
					{
						comboHDRvalue.setVisible(true);
						lblHDR.setVisible(true);
					}
					else
					{
						comboHDRvalue.setVisible(false);
						lblHDR.setVisible(false);
					}
				} 
				else
				{
					comboColorspace.setEnabled(false);	
					
					if (comboFonctions.getSelectedItem().toString().equals("VP9")
					|| comboFonctions.getSelectedItem().toString().equals("H.265") && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
					{
						caseAlpha.setEnabled(true);			
					}
					
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
							String filtres[] = {".mp4", ".mkv", ".webm"};	
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
					
					comboHDRvalue.setVisible(false);
					lblHDR.setVisible(false);
				}
				
				FFPROBE.setFilesize();
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
				
		comboHDRvalue = new JComboBox<String>(new String[] {"auto", "400 nits", "500 nits", "600 nits", "1000 nits", "1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits"} );
		comboHDRvalue.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboHDRvalue.setEditable(true);
		comboHDRvalue.setVisible(false);
		comboHDRvalue.setSelectedIndex(0);
		comboHDRvalue.setBounds(comboColorspace.getX() + comboColorspace.getWidth() - 80, comboColorspace.getLocation().y + comboColorspace.getHeight() + 2, 80, 16);
		comboHDRvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboHDRvalue);
				
		comboCLLvalue = new JComboBox<String>(new String[] {"auto", "400 nits", "500 nits", "600 nits", "1000 nits", "1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits"} );
		comboCLLvalue.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboCLLvalue.setEditable(true);
		comboCLLvalue.setVisible(false);
		comboCLLvalue.setSelectedIndex(0);
		comboCLLvalue.setBounds(comboHDRvalue.getX(), comboHDRvalue.getLocation().y + comboHDRvalue.getHeight() + 2, 80, 16);
		comboCLLvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboCLLvalue);
		
		JLabel lblMaxCLL = new JLabel("MaxCLL" + language.getProperty("colon"));
		lblMaxCLL.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblMaxCLL.setBounds(comboCLLvalue.getX() - lblMaxCLL.getPreferredSize().width - 4, comboCLLvalue.getY(), lblMaxCLL.getPreferredSize().width, lblMaxCLL.getPreferredSize().height);
		lblMaxCLL.setVisible(false);
		grpColorimetry.add(lblMaxCLL);
				
		comboFALLvalue = new JComboBox<String>(new String[] {"auto", "400 nits", "500 nits", "600 nits", "1000 nits", "1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits"} );
		comboFALLvalue.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboFALLvalue.setEditable(true);
		comboFALLvalue.setVisible(false);
		comboFALLvalue.setSelectedIndex(0);
		comboFALLvalue.setBounds(comboHDRvalue.getX(), comboCLLvalue.getLocation().y + comboCLLvalue.getHeight() + 2, 80, 16);
		comboFALLvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboFALLvalue);
				
		JLabel lblMaxFALL = new JLabel("MaxFALL" + language.getProperty("colon"));
		lblMaxFALL.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblMaxFALL.setBounds(comboFALLvalue.getX() - lblMaxFALL.getPreferredSize().width - 4, comboFALLvalue.getY(), lblMaxFALL.getPreferredSize().width, lblMaxFALL.getPreferredSize().height);
		lblMaxFALL.setVisible(false);
		grpColorimetry.add(lblMaxFALL);
		
		lblHDR = new JLabel("luminance" + language.getProperty("colon"));
		lblHDR.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblHDR.setBounds(comboHDRvalue.getX() - lblHDR.getPreferredSize().width - 4, comboHDRvalue.getY(), lblHDR.getPreferredSize().width, lblHDR.getPreferredSize().height);
		lblHDR.setVisible(false);
		grpColorimetry.add(lblHDR);
		
		lblHDR.addComponentListener(new ComponentAdapter ()
	    {
	        public void componentShown(ComponentEvent e)
	        {
	        	lblMaxCLL.setVisible(true);
				comboCLLvalue.setVisible(true);
				lblMaxFALL.setVisible(true);
				comboFALLvalue.setVisible(true);
				
				btnLUTs.setVisible(false);
				caseLUTs.setVisible(false);
				comboLUTs.setVisible(false);
	        }

	        public void componentHidden(ComponentEvent e)
	        {
	        	lblMaxCLL.setVisible(false);
				comboCLLvalue.setVisible(false);
				lblMaxFALL.setVisible(false);
				comboFALLvalue.setVisible(false);
				
				btnLUTs.setVisible(true);
				caseLUTs.setVisible(true);
				comboLUTs.setVisible(true);
	        }
	    });
		
		comboColorspace.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits") || comboColorspace.getSelectedItem().toString().contains("12bits") || comboColorspace.getSelectedItem().toString().contains("422")))
				{
					caseAlpha.setSelected(false);
					caseAlpha.setEnabled(false);	
				}				
				else if (comboFonctions.getSelectedItem().toString().equals("VP9")
				|| comboFonctions.getSelectedItem().toString().equals("H.265") && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					caseAlpha.setEnabled(true);					
				}
				
				if (comboColorspace.getSelectedItem().toString().contains("HDR"))
				{
					comboHDRvalue.setVisible(true);
					lblHDR.setVisible(true);
				}
				else
				{
					comboHDRvalue.setVisible(false);
					lblHDR.setVisible(false);
				}
	
				FFPROBE.setFilesize();
			}
		
		});
				
		caseLUTs = new JCheckBox(language.getProperty("caseLUTs"));
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
	
		btnLUTs = new JButton(language.getProperty("btnManage"));
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
		comboLUTs.setBounds(7, caseLUTs.getLocation().y + caseLUTs.getHeight() + 7, grpColorimetry.getWidth() - 14, 22);
		grpColorimetry.add(comboLUTs);
	
		comboLUTs.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}			
			
		});
		
		caseLUTs.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int index = comboLUTs.getSelectedIndex();
								
				File[] luts = lutsFolder.listFiles();
				String[] data = new String[luts.length]; 
	
		        for (int i = 0 ; i < luts.length ; i++) { 
		           data[i] = (String) luts[i].toString(); 
		        }
	
		        Arrays.sort(data); 
		        LUTs.clear();
	
				if (caseLUTs.isSelected())
				{					
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
					comboLUTs.setSelectedIndex(index);
				} 
				else
				{
					comboLUTs.setEnabled(false);
				}
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
	
		});
	}
	
	private void grpImageAdjustement() {
		
		grpImageAdjustement = new JPanel();
		grpImageAdjustement.setName("grpImageAdjustement");
		grpImageAdjustement.setLayout(null);
		grpImageAdjustement.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("frameColorImage") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpImageAdjustement.setBackground(new Color(30,30,35));
		grpImageAdjustement.setBounds(frame.getWidth(), 258, 312, 17);
		grpImageAdjustement.setVisible(false);
		
		frame.getContentPane().add(grpImageAdjustement);		
		
		grpImageAdjustement.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpImageAdjustement, 792);
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

		caseEnableColorimetry = new JCheckBox(Shutter.language.getProperty("enable"));				
		caseEnableColorimetry.setName("caseEnableColorimetry");
		caseEnableColorimetry.setBounds(7, 16, caseEnableColorimetry.getPreferredSize().width + 4, 23);	
		caseEnableColorimetry.setForeground(Color.WHITE);
		caseEnableColorimetry.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseEnableColorimetry.setSelected(true);
		grpImageAdjustement.add(caseEnableColorimetry);
		
		caseEnableColorimetry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VideoPlayer.loadImage(true);			
			}

		});
		
		JLabel lblExposure = new JLabel(Shutter.language.getProperty("lblExposure"));
		lblExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblExposure.setBounds(13, caseEnableColorimetry.getY() + caseEnableColorimetry.getHeight() + 4, 250, 16);		
		grpImageAdjustement.add(lblExposure);

		sliderExposure = new JSlider();
		sliderExposure.setName("sliderExposure");
		sliderExposure.setMaximum(100);
		sliderExposure.setMinimum(-100);
		sliderExposure.setValue(0);		
		sliderExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderExposure.setBounds(11, lblExposure.getY() + lblExposure.getHeight() - 2, 284, 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderExposure);
		
		JLabel lblGamma = new JLabel(Shutter.language.getProperty("lblGamma"));
		lblGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGamma.setBounds(lblExposure.getX(), sliderExposure.getY() + sliderExposure.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblGamma);

		grpImageAdjustement.add(lblGamma);
		
		sliderGamma = new JSlider();
		sliderGamma.setName("sliderGamma");
		sliderGamma.setMaximum(90);
		sliderGamma.setMinimum(-90);
		sliderGamma.setValue(0);		
		sliderGamma.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGamma.setBounds(sliderExposure.getX(), lblGamma.getY() + lblGamma.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderGamma);
				
		JLabel lblContrast = new JLabel(Shutter.language.getProperty("lblContrast"));
		lblContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblContrast.setBounds(lblExposure.getX(), sliderGamma.getY() + sliderGamma.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblContrast);
		
		grpImageAdjustement.add(lblContrast);
		
		sliderContrast = new JSlider();
		sliderContrast.setName("sliderContrast");
		sliderContrast.setMaximum(100);
		sliderContrast.setMinimum(-100);
		sliderContrast.setValue(0);		
		sliderContrast.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderContrast.setBounds(sliderExposure.getX(), lblContrast.getY() + lblContrast.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderContrast);
		
		JLabel lblWhite = new JLabel(Shutter.language.getProperty("lblWhite"));
		lblWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblWhite.setBounds(lblExposure.getX(), sliderContrast.getY() + sliderContrast.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblWhite);
		
		grpImageAdjustement.add(lblWhite);
		
		sliderWhite = new JSlider();
		sliderWhite.setName("sliderWhite");
		sliderWhite.setMaximum(100);
		sliderWhite.setMinimum(-100);
		sliderWhite.setValue(0);		
		sliderWhite.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderWhite.setBounds(sliderExposure.getX(), lblWhite.getY() + lblWhite.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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

				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderWhite);
		
		JLabel lblBlack = new JLabel(Shutter.language.getProperty("lblBlack"));
		lblBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBlack.setBounds(lblExposure.getX(), sliderWhite.getY() + sliderWhite.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblBlack);
		
		grpImageAdjustement.add(lblBlack);
		
		sliderBlack = new JSlider();
		sliderBlack.setName("sliderBlack");
		sliderBlack.setMaximum(100);
		sliderBlack.setMinimum(-100);
		sliderBlack.setValue(0);		
		sliderBlack.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBlack.setBounds(sliderExposure.getX(), lblBlack.getY() + lblBlack.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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

				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderBlack);
		
		JLabel lblHighlights = new JLabel(Shutter.language.getProperty("lblHighlights"));
		lblHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHighlights.setBounds(lblExposure.getX(), sliderBlack.getY() + sliderBlack.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblHighlights);
		
		grpImageAdjustement.add(lblHighlights);
		
		sliderHighlights = new JSlider();
		sliderHighlights.setName("sliderHighlights");
		sliderHighlights.setMaximum(100);
		sliderHighlights.setMinimum(-100);
		sliderHighlights.setValue(0);		
		sliderHighlights.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHighlights.setBounds(sliderExposure.getX(), lblHighlights.getY() + lblHighlights.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderHighlights);
		
		JLabel lblMediums = new JLabel(Shutter.language.getProperty("lblMediums"));
		lblMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblMediums.setBounds(lblExposure.getX(), sliderHighlights.getY() + sliderHighlights.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblMediums);
		
		grpImageAdjustement.add(lblMediums);
		
		sliderMediums = new JSlider();
		sliderMediums.setName("sliderMediums");
		sliderMediums.setMaximum(100);
		sliderMediums.setMinimum(-100);
		sliderMediums.setValue(0);		
		sliderMediums.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderMediums.setBounds(sliderExposure.getX(), lblMediums.getY() + lblMediums.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderMediums);
		
		JLabel lblShadows = new JLabel(Shutter.language.getProperty("lblShadows"));
		lblShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblShadows.setBounds(lblExposure.getX(), sliderMediums.getY() + sliderMediums.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblShadows);
		
		grpImageAdjustement.add(lblShadows);
		
		sliderShadows = new JSlider();
		sliderShadows.setName("sliderShadows");
		sliderShadows.setMaximum(100);
		sliderShadows.setMinimum(-100);
		sliderShadows.setValue(0);		
		sliderShadows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderShadows.setBounds(sliderExposure.getX(), lblShadows.getY() + lblShadows.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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

				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderShadows);
						
		JLabel lblBalance = new JLabel(Shutter.language.getProperty("lblBalance"));
		lblBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblBalance.setBounds(lblExposure.getX(), sliderShadows.getY() + sliderShadows.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblBalance);
		
		grpImageAdjustement.add(lblBalance);
		
		sliderBalance = new JSlider();
		sliderBalance.setName("sliderBalance");
		sliderBalance.setMaximum(12000);
		sliderBalance.setMinimum(1000);
		sliderBalance.setValue(6500);		
		sliderBalance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBalance.setBounds(sliderExposure.getX(), lblBalance.getY() + lblBalance.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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

				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderBalance);
		
		JLabel lblHUE = new JLabel(Shutter.language.getProperty("lblHUE"));
		lblHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblHUE.setBounds(lblExposure.getX(), sliderBalance.getY() + sliderBalance.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblHUE);
		
		grpImageAdjustement.add(lblHUE);
		
		sliderHUE = new JSlider();
		sliderHUE.setName("sliderHUE");
		sliderHUE.setMaximum(100);
		sliderHUE.setMinimum(-100);
		sliderHUE.setValue(0);		
		sliderHUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderHUE.setBounds(sliderExposure.getX(), lblHUE.getY() + lblHUE.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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

				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderHUE);
		
		JLabel lblRGB = new JLabel(Shutter.language.getProperty("lblRGB"));
		lblRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblRGB.setBounds(lblExposure.getX(), sliderHUE.getY() + sliderHUE.getHeight() + 6, lblRGB.getPreferredSize().width + 4, 16);		
		grpImageAdjustement.add(lblRGB);
		
		grpImageAdjustement.add(lblRGB);
		
		comboRGB = new JComboBox<String>();
		comboRGB.setName("comboRGB");
		comboRGB.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("setAll"), Shutter.language.getProperty("setHigh"), Shutter.language.getProperty("setMedium"), Shutter.language.getProperty("setLow")}));
		comboRGB.setMaximumRowCount(10);
		comboRGB.setEditable(false);
		comboRGB.setSelectedIndex(0);
		comboRGB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboRGB.setBounds(lblRGB.getX() + lblRGB.getWidth() + 3, lblRGB.getY() - 3, 100, 22);		
		grpImageAdjustement.add(comboRGB);
		
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
		lblR.setBounds(lblExposure.getX(), comboRGB.getY() + comboRGB.getHeight() + 3, lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblR);
		
		grpImageAdjustement.add(lblR);
				
		sliderRED = new JSlider();
		sliderRED.setName("sliderRED");
		sliderRED.setMaximum(100);
		sliderRED.setMinimum(-100);
		sliderRED.setValue(0);		
		sliderRED.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderRED.setBounds(sliderExposure.getX(), lblR.getY() + lblR.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
														
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderRED);
		
		JLabel lblG = new JLabel(Shutter.language.getProperty("lblGREEN"));
		lblG.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblG.setBounds(lblExposure.getX(), sliderRED.getY() + sliderRED.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblG);
		
		grpImageAdjustement.add(lblG);
		
		sliderGREEN = new JSlider();
		sliderGREEN.setName("sliderGREEN");
		sliderGREEN.setMaximum(100);
		sliderGREEN.setMinimum(-100);
		sliderGREEN.setValue(0);		
		sliderGREEN.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGREEN.setBounds(sliderExposure.getX(), lblG.getY() + lblG.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
														
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderGREEN);
		
		JLabel lblB = new JLabel(Shutter.language.getProperty("lblBLUE"));
		lblB.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblB.setBounds(lblExposure.getX(), sliderGREEN.getY() + sliderGREEN.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblB);
		
		grpImageAdjustement.add(lblB);
		
		sliderBLUE = new JSlider();
		sliderBLUE.setName("sliderBLUE");
		sliderBLUE.setMaximum(100);
		sliderBLUE.setMinimum(-100);
		sliderBLUE.setValue(0);		
		sliderBLUE.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderBLUE.setBounds(sliderExposure.getX(), lblB.getY() + lblB.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderBLUE);
		
		JLabel lblSaturation = new JLabel(Shutter.language.getProperty("lblSaturation"));
		lblSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblSaturation.setBounds(lblExposure.getX(), sliderBLUE.getY() + sliderBLUE.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblSaturation);
		
		grpImageAdjustement.add(lblSaturation);
		
		sliderSaturation = new JSlider();
		sliderSaturation.setName("sliderSaturation");
		sliderSaturation.setMaximum(100);
		sliderSaturation.setMinimum(-100);
		sliderSaturation.setValue(0);		
		sliderSaturation.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderSaturation.setBounds(sliderExposure.getX(), lblSaturation.getY() + lblSaturation.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderSaturation);
			
		JLabel lblVibrance = new JLabel(Shutter.language.getProperty("lblVibrance"));
		lblVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVibrance.setBounds(lblExposure.getX(), sliderSaturation.getY() + sliderSaturation.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblVibrance);
		
		grpImageAdjustement.add(lblVibrance);
		
		sliderVibrance = new JSlider();
		sliderVibrance.setName("sliderVibrance");
		sliderVibrance.setMaximum(100);
		sliderVibrance.setMinimum(-100);
		sliderVibrance.setValue(0);		
		sliderVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVibrance.setBounds(sliderExposure.getX(), lblVibrance.getY() + lblVibrance.getHeight() - 2, sliderExposure.getWidth(), 22);	
				
		grpImageAdjustement.add(sliderVibrance);			
		
		comboVibrance = new JComboBox<String>();
		comboVibrance.setName("comboVibrance");
		comboVibrance.setModel(new DefaultComboBoxModel<String>(new String[] {Shutter.language.getProperty("intensity"), Shutter.language.getProperty("red"), Shutter.language.getProperty("green"), Shutter.language.getProperty("blue")}));
		comboVibrance.setMaximumRowCount(10);
		comboVibrance.setEditable(false);
		comboVibrance.setSelectedIndex(0);
		comboVibrance.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboVibrance.setBounds(grpImageAdjustement.getWidth() - 108, lblVibrance.getY() - 5, 100, 22);		
		grpImageAdjustement.add(comboVibrance);
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
				
		JLabel lblGrain = new JLabel(Shutter.language.getProperty("lblGrain"));
		lblGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblGrain.setBounds(lblExposure.getX(), sliderVibrance.getY() + sliderVibrance.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblGrain);
		
		grpImageAdjustement.add(lblGrain);
		
		sliderGrain = new JSlider();
		sliderGrain.setName("sliderGrain");
		sliderGrain.setMaximum(100);
		sliderGrain.setMinimum(-100);
		sliderGrain.setValue(0);		
		sliderGrain.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderGrain.setBounds(sliderExposure.getX(), lblGrain.getY() + lblGrain.getHeight() - 2, sliderExposure.getWidth(), 22);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		grpImageAdjustement.add(sliderGrain);	
				
		JLabel lblVignette = new JLabel(Shutter.language.getProperty("lblVignette"));
		lblVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblVignette.setBounds(lblExposure.getX(), sliderGrain.getY() + sliderGrain.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblVignette);
		
		sliderVignette = new JSlider();
		sliderVignette.setName("sliderVignette");
		sliderVignette.setMaximum(100);
		sliderVignette.setMinimum(-100);
		sliderVignette.setValue(0);		
		sliderVignette.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderVignette.setBounds(sliderExposure.getX(), lblVignette.getY() + lblVignette.getHeight() - 2, sliderExposure.getWidth(), 22);	
		grpImageAdjustement.add(sliderVignette);
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		JLabel lblAngle = new JLabel(Shutter.language.getProperty("caseAngle"));
		lblAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblAngle.setBounds(lblExposure.getX(), sliderVignette.getY() + sliderVignette.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblAngle);
		
		sliderAngle = new JSlider();
		sliderAngle.setName("sliderAngle");
		sliderAngle.setMaximum(100);
		sliderAngle.setMinimum(-100);
		sliderAngle.setValue(0);		
		sliderAngle.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderAngle.setBounds(sliderExposure.getX(), lblAngle.getY() + lblAngle.getHeight() - 2, sliderExposure.getWidth(), 22);	
		grpImageAdjustement.add(sliderAngle);	
		
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
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		JLabel lblZoom = new JLabel(Shutter.language.getProperty("lblZoom"));
		lblZoom.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblZoom.setBounds(lblExposure.getX(), sliderAngle.getY() + sliderAngle.getHeight(), lblExposure.getSize().width, 16);		
		grpImageAdjustement.add(lblZoom);
		
		sliderZoom = new JSlider();
		sliderZoom.setName("sliderZoom");
		sliderZoom.setMaximum(100);
		sliderZoom.setMinimum(0);
		sliderZoom.setValue(0);		
		sliderZoom.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		sliderZoom.setBounds(sliderExposure.getX(), lblZoom.getY() + lblZoom.getHeight() - 2, sliderExposure.getWidth(), 22);	
		grpImageAdjustement.add(sliderZoom);	
		
		sliderZoom.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					sliderZoom.setValue(0);	
					lblZoom.setText(Shutter.language.getProperty("lblZoom"));
				}
			}

		});
		
		sliderZoom.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if (sliderZoom.getValue() == 0)
				{
					lblZoom.setText(Shutter.language.getProperty("lblZoom"));
				}
				else
				{
					lblZoom.setText(Shutter.language.getProperty("lblZoom") + " " + sliderZoom.getValue());
				}
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		
		btnResetColor = new JButton(Shutter.language.getProperty("btnReset"));
		btnResetColor.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnResetColor.setBounds(lblExposure.getX(), sliderZoom.getY() + sliderZoom.getHeight() + 2, sliderZoom.getWidth(), 21);
		grpImageAdjustement.add(btnResetColor);		
		
		btnResetColor.addActionListener(new ActionListener() {

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
				sliderVignette.setValue(0);
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
				sliderZoom.setValue(0);
					
				//important
				comboRGB.setSelectedIndex(0);
				comboVibrance.setSelectedIndex(0);
			}
			
		});
			
	}
	
	private void grpCorrections() {
		
		grpCorrections = new JPanel();
		grpCorrections.setLayout(null);
		grpCorrections.setVisible(false);
		grpCorrections.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("grpCorrections") + " ", 0, 0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpCorrections.setBackground(new Color(30,30,35));
		grpCorrections.setBounds(frame.getWidth(), grpImageAdjustement.getY() + grpImageAdjustement.getHeight() + 6, 312, 17);
		frame.getContentPane().add(grpCorrections);

		grpCorrections.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				int size = 25;
				for (Component c : grpCorrections.getComponents()) {
					if (c instanceof JCheckBox)
						size += 17;
				}
				
				extendSections(grpCorrections, size);
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

		caseStabilisation = new JCheckBox(Shutter.language.getProperty("caseStabilisation"));
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
				
				if (caseStabilisation.isSelected() && Shutter.inputDeviceIsRunning == false && FFPROBE.totalLength > 40 && VideoPlayer.videoPath != null)
				{
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
							File file = new File(VideoPlayer.videoPath);
							try {
								
								//InOut	
								InputAndOutput.getInputAndOutput();	
								
								stabilisation = Corrections.setStabilisation("", file, file.getName(), "");

								Shutter.enableAll();
								Shutter.progressBar1.setValue(0);
								Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
	
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

								if (FFMPEG.cancelled)
								{
									stabilisation = "";
								}
								else
								{	
									VideoPlayer.setPlayerButtons(true);
									
									VideoPlayer.resizeAll();
									
									float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
									
									//NTSC framerate
									timeIn = Timecode.getNonDropFrameTC(timeIn);
									
									VideoPlayer.playerSetTime(timeIn);	
									
									VideoPlayer.btnPlay.doClick();
									
									do {
										Thread.sleep(100);
									} while (VideoPlayer.playerIsPlaying() == false);
									
									do {
										Thread.sleep(100);
									} while (VideoPlayer.playerIsPlaying());
									
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
					VideoPlayer.loadImage(false);
				}
								
				
			}
			
		});
		
		caseDeflicker = new JCheckBox(Shutter.language.getProperty("caseDeflicker"));
		caseDeflicker.setName("caseDeflicker");
		caseDeflicker.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseDeflicker.setSize(caseDeflicker.getPreferredSize().width, 23);		
		
		caseDeflicker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		caseBanding = new JCheckBox(Shutter.language.getProperty("caseBanding"));
		caseBanding.setName("caseBanding");
		caseBanding.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseBanding.setSize(caseBanding.getPreferredSize().width + 10, 23); 
				
		caseBanding.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		caseLimiter = new JCheckBox(Shutter.language.getProperty("caseLimiter"));
		caseLimiter.setName("caseLimiter");
		caseLimiter.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseLimiter.setSize(caseLimiter.getPreferredSize().width, 23);
		
		caseLimiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.loadImage(false);
			}
			
		});
		
		caseDetails = new JCheckBox(Shutter.language.getProperty("caseDetails"));
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
					VideoPlayer.loadImage(false);
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
				
				VideoPlayer.loadImage(false);
			}

		});

		sliderDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDetails.setSelected(true);
				sliderDetails.setEnabled(true);
			}

		});

		caseDenoise = new JCheckBox(Shutter.language.getProperty("caseBruit"));
		caseDenoise.setName("caseDenoise");
		caseDenoise.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseDenoise.setSize(caseDenoise.getPreferredSize().width + 14, 23);

		caseDenoise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseDenoise.isSelected() == false)
				{
					sliderDenoise.setValue(0);
					VideoPlayer.loadImage(false);
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
				
				VideoPlayer.loadImage(false);
			}

		});

		sliderDenoise.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDenoise.setSelected(true);
				sliderDenoise.setEnabled(true);
			}

		});

		caseSmoothExposure = new JCheckBox(Shutter.language.getProperty("caseExposure"));
		caseSmoothExposure.setName("caseSmoothExposure");
		caseSmoothExposure.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseSmoothExposure.setSize(caseSmoothExposure.getPreferredSize().width + 20, 23);

		caseSmoothExposure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseSmoothExposure.isSelected() == false)
				{
					sliderSmoothExposure.setValue(0);
					VideoPlayer.loadImage(false);
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
				
				VideoPlayer.loadImage(false);
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
		grpTransitions.setVisible(false);
		grpTransitions.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), Shutter.language.getProperty("grpTransitions") + " ", 0,
				0, new Font(Shutter.montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpTransitions.setBackground(new Color(30,30,35));
		grpTransitions.setBounds(frame.getWidth(), grpCorrections.getY() + grpCorrections.getHeight() + 6, 312, 17);
		frame.getContentPane().add(grpTransitions);
		
		grpTransitions.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				extendSections(grpTransitions, 104);
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
										
					float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
					
					VideoPlayer.playTransition = true;
					VideoPlayer.playerSetTime(timeIn);
					
					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;
				}
				else
				{
					spinnerVideoFadeIn.setEnabled(false);		
				}		
			}
			
		});

		spinnerVideoFadeIn = new JTextField("25");
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
		lblFadeInColor.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblFadeInColor.setBackground(new Color(42,42,47));
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
					float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
					
					VideoPlayer.playTransition = true;
					VideoPlayer.playerSetTime(timeIn);
					
					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;
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
					
					float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
					
					VideoPlayer.playTransition = true;
					VideoPlayer.playerSetTime(timeIn);
					
					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;	
				}
				else
				{
					spinnerAudioFadeIn.setEnabled(false);		
				}	

			}
			
		});
		
		spinnerAudioFadeIn = new JTextField("25");
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
				
				if (caseVideoFadeOut.isSelected() && liste.getSize() > 0)
				{
					spinnerVideoFadeOut.setEnabled(true);
					
					float timeOut = (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					VideoPlayer.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame);	
					
					//Allows to wait for the last frame to load					
					long time = System.currentTimeMillis();

					if (VideoPlayer.playerIsPlaying() == false)
					{
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
							
							if (System.currentTimeMillis() - time > 1000)
								break;
							
						} while (VideoPlayer.setTime.isAlive());
					}	

					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;
				}
				else
				{
					spinnerVideoFadeOut.setEnabled(false);		
				}
			}
			
		});
				
		spinnerVideoFadeOut = new JTextField("25");
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
		lblFadeOutColor.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblFadeOutColor.setBackground(new Color(42,42,47));
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
					float timeOut = (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					VideoPlayer.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame);
					
					//Allows to wait for the last frame to load					
					long time = System.currentTimeMillis();

					if (VideoPlayer.playerIsPlaying() == false)
					{
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
							
							if (System.currentTimeMillis() - time > 1000)
								break;
							
						} while (VideoPlayer.setTime.isAlive());
					}	
					
					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;
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
				
				if (caseAudioFadeOut.isSelected() && liste.getSize() > 0)
				{
					spinnerAudioFadeOut.setEnabled(true);
					
					float timeOut = (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseOutS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseOutF.getText());
					
					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue)
					{
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}
							
					VideoPlayer.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame);
					
					//Allows to wait for the last frame to load					
					long time = System.currentTimeMillis();

					if (VideoPlayer.playerIsPlaying() == false)
					{
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {}
							
							if (System.currentTimeMillis() - time > 1000)
								break;
							
						} while (VideoPlayer.setTime.isAlive());
					}	
					
					VideoPlayer.btnPlay.setIcon(new FlatSVGIcon("contents/pause.svg", 15, 15));
					VideoPlayer.btnPlay.setName("pause");
					VideoPlayer.playerLoop = true;
				}
				else
				{
					spinnerAudioFadeOut.setEnabled(false);		
				}	
				
			}
			
		});
				
		spinnerAudioFadeOut = new JTextField("25");
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
	
	private void grpImageSequence() {
		
		grpImageSequence = new JPanel();
		grpImageSequence.setLayout(null);
		grpImageSequence.setVisible(false);
		grpImageSequence.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpSequenceImage") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpImageSequence.setBackground(new Color(30,30,35));
		grpImageSequence.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpImageSequence);
		
		grpImageSequence.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {	
				
				extendSections(grpImageSequence, 93);
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

		caseEnableSequence = new JCheckBox(language.getProperty("caseActiverSequence"));
		caseEnableSequence.setName("caseActiverSequence");
		caseEnableSequence.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseEnableSequence.setBounds(7, 16, caseEnableSequence.getPreferredSize().width, 23);
		grpImageSequence.add(caseEnableSequence);
		
		caseSequenceFPS = new JComboBox<String>();
		caseSequenceFPS.setName("caseSequenceFPS");
		caseSequenceFPS.setEnabled(false);
		caseSequenceFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
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
				
				VideoPlayer.setMedia();	
				VideoPlayer.setPlayerButtons(true);
			}

		});

		caseBlend = new JCheckBox(language.getProperty("caseBlend"));
		caseBlend.setName("caseBlend");
		caseBlend.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseBlend.setBounds(7, caseEnableSequence.getLocation().y + caseEnableSequence.getHeight(), caseBlend.getPreferredSize().width + 14, 23);
		grpImageSequence.add(caseBlend);

		caseBlend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseBlend.isSelected() == false)
					sliderBlend.setValue(0);
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}

		});

		sliderBlend = new JSlider();
		sliderBlend.setName("sliderBlend");
		sliderBlend.setMinorTickSpacing(1);
		sliderBlend.setMaximum(16);
		sliderBlend.setMinimum(0);
		sliderBlend.setValue(0);
		sliderBlend.setBounds(grpImageSequence.getWidth() - 120 - 14, caseBlend.getLocation().y, 120, 22);
		grpImageSequence.add(sliderBlend);

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


			@Override
			public void mouseReleased(MouseEvent e) {
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}

		});
		
		caseMotionBlur = new JCheckBox(language.getProperty("caseMotionBlur"));
		caseMotionBlur.setName("caseMotionBlur");
		caseMotionBlur.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseMotionBlur.setBounds(7, caseBlend.getHeight() + caseBlend.getLocation().y, caseMotionBlur.getPreferredSize().width, 23);
		grpImageSequence.add(caseMotionBlur);
		
		caseMotionBlur.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
								
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}

		});
		
	}
		
	private void grpAdvanced() {

		grpAdvanced = new JPanel();
		grpAdvanced.setLayout(null);
		grpAdvanced.setVisible(false);
		grpAdvanced.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5), language.getProperty("grpAdvanced") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpAdvanced.setBackground(new Color(30,30,35));
		grpAdvanced.setBounds(frame.getWidth(), 396, 312, 17);
		frame.getContentPane().add(grpAdvanced);

		grpAdvanced.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int size = 25;
				for (Component c : grpAdvanced.getComponents()) {
					if (c instanceof JCheckBox)
						size += 17;
				}

				extendSections(grpAdvanced, size);
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

		caseOpenGop = new JCheckBox(Shutter.language.getProperty("btnOpenGOP"));
		caseOpenGop.setName("caseOpenGop");
		caseOpenGop.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseOpenGop.setSize(caseOpenGop.getPreferredSize().width, 23);
		
		caseForcerProgressif = new JCheckBox(language.getProperty("caseForcerProgressif"));
		caseForcerProgressif.setName("caseForcerProgressif");
		caseForcerProgressif.setToolTipText(language.getProperty("tooltipProgressif"));
		caseForcerProgressif.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerProgressif.setSize(caseForcerProgressif.getPreferredSize().width + 4, 23);

		caseForcerEntrelacement = new JCheckBox(language.getProperty("caseForcerEntrelacement"));
		caseForcerEntrelacement.setName("caseForcerEntrelacement");
		caseForcerEntrelacement.setToolTipText(language.getProperty("tooltipEntrelacement"));
		caseForcerEntrelacement.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerEntrelacement.setSize(caseForcerEntrelacement.getPreferredSize().width + 4, 23);
		
		caseForcerInversion = new JCheckBox(language.getProperty("caseForcerInversion"));
		caseForcerInversion.setName("caseForcerInversion");
		caseForcerInversion.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForcerInversion.setSize(caseForcerInversion.getPreferredSize().width, 23);

		caseForcerDesentrelacement = new JCheckBox(language.getProperty("caseForcerDesentrelacement"));
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
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame	
				VideoPlayer.setInfo();
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
				
				if (comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") && lblTFF.getText().contains("x2"))
				{
					lblTFF.setText("TFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}				
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		lblTFF = new JLabel("TFF");
		lblTFF.setName("lblTFF");
		lblTFF.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblTFF.setBackground(new Color(42,42,47));
		lblTFF.setHorizontalAlignment(SwingConstants.CENTER);
		lblTFF.setOpaque(true);
		lblTFF.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblTFF.setSize(32, 16);

		lblTFF.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (lblTFF.getText().equals("TFF"))
				{
					lblTFF.setText("BFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "1";
				}
				else if (lblTFF.getText().equals("BFF") && comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") == false)
				{
					lblTFF.setText("x2 T");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}
				else if (lblTFF.getText().equals("x2 T") && comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") == false)
				{
					lblTFF.setText("x2 B");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}
				else 
				{
					lblTFF.setText("TFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				VideoPlayer.setInfo();
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
		
		caseCreateTree = new JCheckBox(language.getProperty("caseCreateTree"));
		caseCreateTree.setName("caseCreateTree");
		caseCreateTree.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseCreateTree.setSize(caseCreateTree.getPreferredSize().width, 23);
		
		caseCreateTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseCreateTree.isSelected())
				{
					setDestinationTabs(2);	
					comboCreateTree.setEnabled(true);
				}
				else
				{
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));
					
					if (caseCreateOPATOM.isSelected() == false)
					{
						if (comboFonctions.getSelectedItem().toString().equals("H.264"))
						{
							setDestinationTabs(6);						
						}
						else
							setDestinationTabs(5);	
					}
					
					comboCreateTree.setEnabled(false);
				}	
			}	
		});
		
		comboCreateTree = new JComboBox<String>();
		comboCreateTree.setName("comboCreateTree");
		comboCreateTree.setEnabled(false);
		comboCreateTree.setMaximumRowCount(15);
		
		String[] levels	= new String[11];
		for (int i = 0 ; i < 11 ; i++)
		{
			levels[i] = String.valueOf(i);
		}
		
		comboCreateTree.setModel(new DefaultComboBoxModel<String>(levels));
		comboCreateTree.setSelectedIndex(0);
		comboCreateTree.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboCreateTree.setEditable(false);
		comboCreateTree.setSize(35, 16);
		
		casePreserveMetadata = new JCheckBox(language.getProperty("casePreserveMetadata"));
		casePreserveMetadata.setName("casePreserveMetadata");
		casePreserveMetadata.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		casePreserveMetadata.setSize(casePreserveMetadata.getPreferredSize().width, 23);
		
		casePreserveSubs = new JCheckBox(language.getProperty("casePreserveSubs"));
		casePreserveSubs.setName("casePreserveSubs");
		casePreserveSubs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		casePreserveSubs.setSize(casePreserveSubs.getPreferredSize().width, 23);
				
		casePreserveSubs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (casePreserveSubs.isSelected()
				&& (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
				|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))
				|| subtitlesBurn == false))
				{
					caseAddSubtitles.setSelected(false);
				}
			}			
			
		});		
		
		caseCreateOPATOM = new JCheckBox(language.getProperty("caseCreateOPATOM"));
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
					
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")))
					{
						comboFilter.setSelectedItem(".mxf");
					}
					
					if (caseAS10.isSelected())
					{
						caseAS10.doClick();
					}
				} 
				else 
				{
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));
					
					if (caseCreateTree.isSelected() == false)
					{
						setDestinationTabs(5);		
					}
					
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")))
					{
						comboFilter.setSelectedItem(language.getProperty("aucun"));
					}
				}		
			}	
		});
		
		lblCreateOPATOM = new JLabel(language.getProperty("lblCreateOPATOM"));
		lblCreateOPATOM.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblCreateOPATOM.setBackground(new Color(30,30,35));
		lblCreateOPATOM.setSize((int) lblCreateOPATOM.getPreferredSize().getWidth(), 23);
		
		lblOPATOM = new JLabel("OP-Atom");
		lblOPATOM.setName("lblOPATOM");
		lblOPATOM.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblOPATOM.setBackground(new Color(42,42,47));
		lblOPATOM.setHorizontalAlignment(SwingConstants.CENTER);
		lblOPATOM.setOpaque(true);
		lblOPATOM.setFont(new Font(montserratFont, Font.PLAIN, 12));
		lblOPATOM.setSize(65, 16);

		lblOPATOM.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (lblOPATOM.getText().equals("OP-Atom") && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
				{
					lblOPATOM.setText("OP1a");
				}
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
		
		caseOPATOM = new JCheckBox(language.getProperty("caseOPATOM"));
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

		caseConform = new JCheckBox(language.getProperty("caseConform"));
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
				
				FFPROBE.setFilesize();
				
				//VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			}

		});

		caseDecimate = new JCheckBox(language.getProperty("caseDecimate"));
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

		comboConform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				String newFPS = AdvancedFeatures.setFramerate(false);
				if (newFPS.contains("/") == false && newFPS.contains("vfr") == false)
				{
					VideoPlayer.inputFramerateMS = (float) (1000 / Float.parseFloat(newFPS.replace(" -r ", "")));
				}
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame*/
			}
			
		});
		
		lblToConform = new JLabel(">");
		lblToConform.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblToConform.setSize(lblToConform.getPreferredSize().width + 1, 16);

		comboFPS = new JComboBox<String>();
		comboFPS.setName("comboFPS");
		comboFPS.setEnabled(false);
		comboFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		comboFPS.setSelectedIndex(2);
		comboFPS.setMaximumRowCount(20);
		comboFPS.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		comboFPS.setEditable(true);
		comboFPS.setSize(50, 16);
		
		comboFPS.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				FFPROBE.setFilesize();				
			}
			
		});
		
		lblIsConform = new JLabel(Shutter.language.getProperty("fps"));
		lblIsConform.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblIsConform.setSize(20, 16);
		if (getLanguage.equals(Locale.of("ru").getDisplayLanguage())
		|| getLanguage.equals(Locale.of("uk").getDisplayLanguage())
		|| getLanguage.equals(Locale.of("vi").getDisplayLanguage())
		|| getLanguage.equals(Locale.of("cs").getDisplayLanguage()))
		{
			lblIsConform.setVisible(false);
		}
		
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

		caseForceOutput = new JCheckBox(language.getProperty("caseForceOutput"));
		caseForceOutput.setName("caseForceOutput");
		caseForceOutput.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceOutput.setSize(caseForceOutput.getPreferredSize().width, 23);

		lblNiveaux = new JLabel("0-255");
		lblNiveaux.setName("lblNiveaux");
		lblNiveaux.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblNiveaux.setBackground(new Color(42,42,47));
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

		caseFastStart = new JCheckBox(language.getProperty("caseFastStart"));
		caseFastStart.setName("caseFastStart");
		caseFastStart.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFastStart.setSize(caseFastStart.getPreferredSize().width, 23);
		
		caseFastDecode = new JCheckBox(language.getProperty("caseFastDecode"));
		caseFastDecode.setName("caseFastDecode");
		caseFastDecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFastDecode.setSize(caseFastDecode.getPreferredSize().width, 23);
		
		caseAlpha = new JCheckBox(language.getProperty("caseAlpha"));
		caseAlpha.setName("caseAlpha");
		caseAlpha.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAlpha.setSize(caseAlpha.getPreferredSize().width, 23);
		
		caseGOP = new JCheckBox(language.getProperty("caseGOP"));
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
		
		caseFilmGrain = new JCheckBox(language.getProperty("caseFilmGrain"));
		caseFilmGrain.setName("caseFilmGrain");
		caseFilmGrain.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFilmGrain.setSize(caseFilmGrain.getPreferredSize().width + 4, 23);
		
		caseFilmGrain.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseFilmGrain.isSelected())
				{
					comboFilmGrain.setEnabled(true);
				}
				else
				{
					comboFilmGrain.setEnabled(false);		
				}	
			}
			
		});
					
		comboFilmGrain = new JComboBox<String>();
		comboFilmGrain.setName("comboFilmGrain");
		comboFilmGrain.setEnabled(false);
		comboFilmGrain.setMaximumRowCount(15);
		String[] grainValues = new String[51];
		for (int i = 0 ; i < 51 ; i++)
		{
			grainValues[i] = String.valueOf(i);
		}	
		comboFilmGrain.setModel(new DefaultComboBoxModel<String>(grainValues));
		comboFilmGrain.setSelectedIndex(6);
		comboFilmGrain.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboFilmGrain.setEditable(false);
		comboFilmGrain.setSize(40, 16);
		
		caseFilmGrainDenoise = new JCheckBox(language.getProperty("caseFilmGrainDenoise"));
		caseFilmGrainDenoise.setName("caseFilmGrainDenoise");
		caseFilmGrainDenoise.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseFilmGrainDenoise.setSize(caseFilmGrainDenoise.getPreferredSize().width + 4, 23);
		
		caseFilmGrainDenoise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseFilmGrainDenoise.isSelected())
				{
					comboFilmGrainDenoise.setEnabled(true);
				}
				else
				{
					comboFilmGrainDenoise.setEnabled(false);		
				}	
			}
			
		});
		
		comboFilmGrainDenoise = new JComboBox<String>();
		comboFilmGrainDenoise.setName("comboFilmGrainDenoise");
		comboFilmGrainDenoise.setEnabled(false);
		comboFilmGrainDenoise.setMaximumRowCount(15);
		comboFilmGrainDenoise.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1"} ));
		comboFilmGrainDenoise.setSelectedIndex(1);
		comboFilmGrainDenoise.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboFilmGrainDenoise.setEditable(false);
		comboFilmGrainDenoise.setSize(40, 16);
		
		caseCABAC = new JCheckBox(language.getProperty("caseCABAC"));
		caseCABAC.setName("caseCABAC");
		caseCABAC.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseCABAC.setSize(caseCABAC.getPreferredSize().width + 4, 23);
		
		caseForceLevel = new JCheckBox(language.getProperty("caseForceLevel"));
		caseForceLevel.setName("caseForceLevel");
		caseForceLevel.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseForceLevel.setSize(caseForceLevel.getPreferredSize().width, 23);

		caseForceLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseForceLevel.isSelected())
				{
					comboForceProfile.setEnabled(true);
					comboForceLevel.setEnabled(true);
				}
				else
				{
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
		
		caseForcePreset = new JCheckBox(language.getProperty("caseForcePreset"));
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
		
		caseForceTune = new JCheckBox(language.getProperty("caseForceTune"));
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
		
		caseForceQuality = new JCheckBox(language.getProperty("caseForceQuality"));
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
		
		caseForceSpeed = new JCheckBox(language.getProperty("caseForceSpeed"));
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
		comboForceSpeed.setModel(new DefaultComboBoxModel<String>(new String[] { "0","1","2","3","4","5","6","7","8","10","11","12","13"}));
		comboForceSpeed.setSelectedIndex(4);
		comboForceSpeed.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboForceSpeed.setEditable(false);
		comboForceSpeed.setSize(40, 16);
		
		caseAS10 = new JCheckBox(language.getProperty("caseAS10"));
		caseAS10.setName("caseAS10");
		caseAS10.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseAS10.setSize(caseAS10.getPreferredSize().width, 23);
		
		caseAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAS10.isSelected())
				{
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM"))
					{
						final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mxf"});						
						comboFilter.setModel(model);
						
						if (comboAS10.getSelectedItem().toString().equals("NRK_HD_2012"))
						{
							comboAudioCodec.setSelectedIndex(0);
						}
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
					
					caseCreateOPATOM.setSelected(false);					
				}
				else
				{
					comboAudioCodec.setSelectedIndex(1);
					
					comboAS10.setEnabled(false);
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);				
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
					
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM"))
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
		
		comboAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseAS10.isSelected() && comboFonctions.getSelectedItem().toString().contains("XDCAM") && comboAS10.getSelectedItem().toString().equals("NRK_HD_2012"))
				{
					comboAudioCodec.setSelectedIndex(0);
				}
				else
					comboAudioCodec.setSelectedIndex(1);
			}
			
		});

		caseChunks = new JCheckBox(language.getProperty("caseChunks"));
		caseChunks.setName("caseChunks");
		caseChunks.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseChunks.setSize(caseChunks.getPreferredSize().width + 4, 23);
		
		caseChunks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseChunks.isSelected())
				{
					chunksSize.setEnabled(true);
				}
				else
				{
					chunksSize.setEnabled(false);		
				}	
			}
			
		});
			
		chunksSize = new JComboBox<String>();
		chunksSize.setName("chunksSize");
		chunksSize.setEnabled(false);
		chunksSize.setMaximumRowCount(15);
		String[] values = new String[64];
		for (int i = 0 ; i < 64 ; i++)
		{
			values[i] = String.valueOf(i + 1);
		}			
		chunksSize.setModel(new DefaultComboBoxModel<String>(values));
		chunksSize.setSelectedIndex(3);
		chunksSize.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		chunksSize.setEditable(false);
		chunksSize.setSize(40, 16);
		
		caseDRC = new JCheckBox(language.getProperty("caseDRC"));
		caseDRC.setName("caseDRC");
		caseDRC.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseDRC.setSize(caseDRC.getPreferredSize().width + 4, 23);
		
		caseTruePeak = new JCheckBox("True Peak" + language.getProperty("colon"));
		caseTruePeak.setName("caseTruePeak");
		caseTruePeak.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseTruePeak.setSize(caseTruePeak.getPreferredSize().width + 4, 23);
		
		caseTruePeak.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseTruePeak.isSelected())
					comboTruePeak.setEnabled(true);
				else
					comboTruePeak.setEnabled(false);
			}

		});
		
		comboTruePeak = new JComboBox<Object>();
		comboTruePeak.setName("comboTruePeak");
		comboTruePeak.setEnabled(false);
		comboTruePeak.setMaximumRowCount(15);

		String truePeakValues[] = new String[10];

		truePeakValues[0] = "0.0 dBFS";
		int i = 1;
		do {
			truePeakValues[i] = ("-" + i + ".0 dBFS");
			i++;
		} while (i < 10);

		final ComboBoxModel<Object> truePeakModel = new DefaultComboBoxModel<Object>(truePeakValues);
		comboTruePeak.setModel(truePeakModel);
		comboTruePeak.setSelectedIndex(3);
		comboTruePeak.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboTruePeak.setEditable(true);
		comboTruePeak.setSize(75, 16);
		
		caseLRA = new JCheckBox("LRA" + language.getProperty("colon"));
		caseLRA.setName("caseLRA");
		caseLRA.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseLRA.setSize(caseLRA.getPreferredSize().width + 4, 23);
		
		caseLRA.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseLRA.isSelected())
					comboLRA.setEnabled(true);
				else
					comboLRA.setEnabled(false);
			}

		});
		
		comboLRA = new JComboBox<Object>();
		comboLRA.setName("comboLRA");
		comboLRA.setEnabled(false);
		comboLRA.setMaximumRowCount(15);

		String LRAValues[] = new String[21];
		LRAValues[0] = "1.0 LU";
		int o = 1;
		do {
			LRAValues[o] = (o + " LU");
			o++;
		} while (o < 21);

		final ComboBoxModel<Object> LRAModel = new DefaultComboBoxModel<Object>(LRAValues);
		comboLRA.setModel(LRAModel);
		comboLRA.setSelectedIndex(15);
		comboLRA.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboLRA.setEditable(true);
		comboLRA.setSize(55, 16);

	}

	private void grpBitrate() {
		
		grpBitrate = new JPanel();
		grpBitrate.setLayout(null);
		grpBitrate.setVisible(false);
		grpBitrate.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(45,45,45), 1, 5),
				language.getProperty("grpBitrate") + " ", 0, 0, new Font(montserratFont, Font.PLAIN, 12), new Color(235,235,240)));
		grpBitrate.setBackground(new Color(30,30,35));
		grpBitrate.setBounds(658, 30, 312, 208);
		frame.getContentPane().add(grpBitrate);	
		
		JLabel lblFile = new JLabel(language.getProperty("file") + language.getProperty("colon"));
		lblFile.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblFile.setBounds(12, 20, lblFile.getPreferredSize().width + 4, 16);
		grpBitrate.add(lblFile);
		
		lblH264 = new JLabel("");
		lblH264.setVisible(false);
		lblH264.setHorizontalAlignment(SwingConstants.LEFT);
		lblH264.setForeground(Utils.themeColor);
		lblH264.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblH264.setBounds(lblFile.getX() + lblFile.getWidth() + 2, lblFile.getY() - 1, 302 - (lblFile.getX() + lblFile.getWidth()), 16);
		grpBitrate.add(lblH264);
		
		lblBitrateTimecode = new JLabel(language.getProperty("lblBitrateTimecode"));
		lblBitrateTimecode.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblBitrateTimecode.setBounds(lblFile.getX(), lblFile.getY() + lblFile.getHeight() + 9, lblBitrateTimecode.getPreferredSize().width, 16);
		grpBitrate.add(lblBitrateTimecode);
	
		textH = new JTextField();
		textH.setName("textH");
		textH.setText("00");
		textH.setHorizontalAlignment(SwingConstants.CENTER);
		textH.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textH.setColumns(10);
		textH.setBounds(58, 43, 32, 21);
		grpBitrate.add(textH);
		
		textM = new JTextField();
		textM.setName("textM");
		textM.setText("00");
		textM.setHorizontalAlignment(SwingConstants.CENTER);
		textM.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textM.setColumns(10);
		textM.setBounds(textH.getX() + textH.getWidth(), 43, 32, 21);
		grpBitrate.add(textM);

		textS = new JTextField();
		textS.setName("textS");
		textS.setText("00");
		textS.setHorizontalAlignment(SwingConstants.CENTER);
		textS.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textS.setColumns(10);
		textS.setBounds(textM.getX() + textM.getWidth(), 43, 32, 21);
		grpBitrate.add(textS);
		
		textF = new JTextField();
		textF.setName("textF");
		textF.setText("00");
		textF.setHorizontalAlignment(SwingConstants.CENTER);
		textF.setFont(new Font(freeSansFont, Font.PLAIN, 14));
		textF.setColumns(10);
		textF.setBounds(textS.getX() + textS.getWidth(), 43, 32, 21);
		grpBitrate.add(textF);

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
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}

				}
			}
		});

		textM.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textM.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}

				}
			}
		});

		textS.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textS.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}

				}
			}
		});
		
		textF.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| textF.getText().length() >= 2 || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}

				}
			}
		});

		lblPad = new JLabel() 
		{			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")) || lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
				{
					if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
						g.setColor(Color.BLACK);
					else
						g.setColor(new Color(30,30,35));
					
					g.fillRect(0, 0, 8, 16);
					g.fillRect((70 - 8), 0, (70 - 8), 16);
				}
			}
		};
		lblPad.setText(language.getProperty("lblPad"));
		lblPad.setName("lblPad");
		lblPad.setBackground(new Color(42,42,47));
		lblPad.setHorizontalAlignment(SwingConstants.CENTER);
		lblPad.setOpaque(true);
		lblPad.setVisible(false);
		lblPad.setFont(new Font(montserratFont, Font.PLAIN, 11));
		lblPad.setBounds(comboResolution.getLocation().x + comboResolution.getWidth() + 8, comboResolution.getLocation().y + 3, 70, 16);
		grpBitrate.add(lblPad);
				
		lblPad.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (caseEnableCrop.isSelected())
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
						lblPad.setText(language.getProperty("lblCrop"));
					}
					else
					{
						lblPad.setText(language.getProperty("lblPad"));
					}
				}
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				VideoPlayer.resizeAll();		
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
		
		lblPad.addComponentListener(new ComponentAdapter() {
			
			 public void componentShown ( ComponentEvent e )
			 {
				 lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
			 }

			 public void componentHidden ( ComponentEvent e )
			 {
				 lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
			 }
			
		});
		
		lblVideoBitrate = new JLabel(language.getProperty("lblVideoBitrate"));
		lblVideoBitrate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblVideoBitrate.setBounds(lblBitrateTimecode.getX(), lblBitrateTimecode.getY() + lblBitrateTimecode.getHeight() + 11, 80, 16);
		grpBitrate.add(lblVideoBitrate);
		
		debitVideo = new JComboBox<String>();
		debitVideo.setName("debitVideo");
		debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(), language.getProperty("lblGood").toLowerCase(), "auto" }));
		debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
		debitVideo.setMaximumRowCount(20);
		debitVideo.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		debitVideo.setEditable(true);
		debitVideo.setBounds(92, textH.getY() + textH.getHeight() + 5, 93, 22);
		grpBitrate.add(debitVideo);
		
		debitVideo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ac) {
				
				if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != "")
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
					VideoPlayer.resizeAll();		
				}
			}
			
		});
		
		lblKbsH264 = new JLabel("kb/s");
		lblKbsH264.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblKbsH264.setBounds(188, debitVideo.getY() + 3, 33, 16);
		grpBitrate.add(lblKbsH264);
		
		lblMaximumBitrate = new JLabel(language.getProperty("lblMaximumBitrate"));
		lblMaximumBitrate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblMaximumBitrate.setBounds(lblBitrateTimecode.getX(), lblVideoBitrate.getY() + lblVideoBitrate.getHeight() + 11, 80, 16);
		grpBitrate.add(lblMaximumBitrate);
		
		maximumBitrate = new JComboBox<String>();
		maximumBitrate.setName("maximumBitrate");
		maximumBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", "auto" }));
		maximumBitrate.setSelectedIndex(maximumBitrate.getModel().getSize() - 1);
		maximumBitrate.setMaximumRowCount(20);
		maximumBitrate.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		maximumBitrate.setEditable(true);
		maximumBitrate.setBounds(debitVideo.getX(), debitVideo.getY() + debitVideo.getHeight() + 5, debitVideo.getWidth(), 22);
		grpBitrate.add(maximumBitrate);
		
		lblMaximumKbs = new JLabel("kb/s");
		lblMaximumKbs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblMaximumKbs.setBounds(lblKbsH264.getX(), maximumBitrate.getY() + 3, 33, 16);
		grpBitrate.add(lblMaximumKbs);
		
		lblAudioBitrate = new JLabel(language.getProperty("lblAudioBitrate"));
		lblAudioBitrate.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudioBitrate.setBounds(lblBitrateTimecode.getX(), lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11, 80, 16);
		grpBitrate.add(lblAudioBitrate);
		
		debitAudio = new JComboBox<String>();
		debitAudio.setName("debitAudio");			
		debitAudio.setModel(new DefaultComboBoxModel<String>(audioValues));
		debitAudio.setSelectedIndex(10);
		debitAudio.setMaximumRowCount(20);
		debitAudio.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		debitAudio.setEditable(true);
		debitAudio.setBounds(debitVideo.getX(), maximumBitrate.getY() + maximumBitrate.getHeight() + 5, debitVideo.getWidth(), 22);
		grpBitrate.add(debitAudio);
		
		lblAudioKbs = new JLabel("kb/s");
		lblAudioKbs.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblAudioKbs.setBounds(lblKbsH264.getX(), debitAudio.getY() + 3, 33, 16);
		grpBitrate.add(lblAudioKbs);

		lblSize = new JLabel(language.getProperty("size"));
		lblSize.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblSize.setBounds(lblBitrateTimecode.getX(), lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11, lblSize.getPreferredSize().width, 16);
		grpBitrate.add(lblSize);
		
		bitrateSize = new JTextField();
		bitrateSize.setName("bitrateSize");
		bitrateSize.setHorizontalAlignment(SwingConstants.CENTER);
		bitrateSize.setText("2000");
		bitrateSize.setFont(new Font(freeSansFont, Font.PLAIN, 11));
		bitrateSize.setColumns(10);
		bitrateSize.setBounds(debitVideo.getX(), debitAudio.getY() + debitAudio.getHeight() + 5, debitVideo.getWidth(), 21);
		grpBitrate.add(bitrateSize);
				
		bitrateSize.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '.' && caracter != ',' && caracter != '￿'
				|| bitrateSize.getText().length() >= 5
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
					if (liste.getSize() > 0 && fileList.getSelectedIndex() == -1)
					{
						fileList.setSelectedIndex(0);
						VideoPlayer.setMedia();
					}			

					try {
						if (e.getKeyCode() != KeyEvent.VK_DELETE) {
							int h = Integer.parseInt(textH.getText());
							int min = Integer.parseInt(textM.getText());
							int sec = Integer.parseInt(textS.getText());
							int frames = Integer.parseInt(textF.getText());
							int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
							float tailleFinale = Float.parseFloat(bitrateSize.getText().replace(",", "."));
							float result = (float) tailleFinale / ((h * 3600) + (min * 60) + sec + (frames * ((float) 1 / FFPROBE.currentFPS)));
							float resultAudio = (float) audio / 8 / 1024;
							float resultatdebit = (result - resultAudio) * 8 * 1024;
							debitVideo.getModel().setSelectedItem((int) resultatdebit);
						}
					} catch (Exception e1) {}
				}
			}
		});

		lblFileSizeMo = new JLabel(language.getProperty("mo"));
		lblFileSizeMo.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblFileSizeMo.setBounds(lblKbsH264.getX(), bitrateSize.getY() + 3, 33, 16);
		grpBitrate.add(lblFileSizeMo);
		
		lock = new JLabel(new FlatSVGIcon("contents/unlock.svg", 16, 16));
		lock.setName("unlock");
		lock.setHorizontalAlignment(SwingConstants.CENTER);
		lock.setBounds(bitrateSize.getX() - 21 - 3, bitrateSize.getY(), 21, 21);
		grpBitrate.add(lock);
		
		lock.addMouseListener(new MouseListener() {
						
			@Override
			public void mouseClicked(MouseEvent arg0) {
								
				if (isLocked)	
				{
					lock.setIcon(new FlatSVGIcon("contents/unlock.svg", 16, 16));
					isLocked = false;
					lock.setName("unlock");
				}
				else
				{
					lock.setIcon(new FlatSVGIcon("contents/lock.svg", 16, 16));
					isLocked = true;
					lock.setName("lock");
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
					if (bitrateSize.isFocusOwner() == false)
						FFPROBE.setFilesize();
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
					if (bitrateSize.isFocusOwner() == false)
						FFPROBE.setFilesize();
					
					if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblBest")))
						debitVideo.setSelectedIndex(1);
					else if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblWorst")))
						debitVideo.setSelectedIndex(51);
						
				} catch (Exception e1) {}

			}

		};
		debitVideo.addActionListener(actionListener);
		debitAudio.addActionListener(actionListener);

		case2pass = new JCheckBox(language.getProperty("case2pass"));
		case2pass.setName("case2pass");
		case2pass.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		case2pass.setSize(case2pass.getPreferredSize().width, 23);
		case2pass.setLocation(7, grpBitrate.getHeight() - 32);
		grpBitrate.add(case2pass);

		case2pass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (inputDeviceIsRunning)
				{
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					case2pass.setSelected(false);
				}
				
				if (case2pass.isSelected()) {
					comboResolution.setEnabled(true);
					debitVideo.setEnabled(true);
					bitrateSize.setEnabled(true);
					textH.setEnabled(true);
					textM.setEnabled(true);
					textS.setEnabled(true);
					textF.setEnabled(true);
				}
			}
		});

		caseQMax = new JCheckBox(language.getProperty("caseQMax"));
		caseQMax.setName("caseQMax");
		caseQMax.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		caseQMax.setSize(caseQMax.getPreferredSize().width, 23);
		caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());		
		grpBitrate.add(caseQMax);
		
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
					
					if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync")))
					{
						caseForcePreset.setEnabled(true);
					}
				}

				if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != "")
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
					VideoPlayer.resizeAll();		
				}
			}
			
		});
	
		// Links
		h264lines = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, null, 0.0f));
				
				if (comboFonctions.getSelectedItem().toString().contains("H.26"))
				{
					g2.draw(new Line2D.Float(41, 15, 41, 95));// Grande ligne
					g2.draw(new Line2D.Float(6, 15, 40, 15)); // 1
					g2.draw(new Line2D.Float(6, 69, 40, 69)); // 2
					g2.draw(new Line2D.Float(6, 95, 40, 95)); // 3
				}
				else					
				{
					g2.draw(new Line2D.Float(41, 15, 41, 69));// Grande ligne
					g2.draw(new Line2D.Float(6, 15, 40, 15)); // 1
					g2.draw(new Line2D.Float(6, 42, 40, 42)); // 2
					g2.draw(new Line2D.Float(6, 69, 40, 69)); // 3
				}
			}
		};
		h264lines.setBackground(new Color(30,30,35));
		h264lines.setBounds(212, lblKbsH264.getY() - 7, 53, 105);
		grpBitrate.add(h264lines);
		grpBitrate.validate();
		grpBitrate.repaint();
		
		h264lines.addComponentListener(new ComponentAdapter ()
	    {
	        public void componentShown(ComponentEvent e)
	        {
	        	lblVBR.setLocation(h264lines.getX() + h264lines.getWidth(), debitVideo.getY() + 3);
	        }

	        public void componentHidden(ComponentEvent e)
	        {
	        	lblVBR.setLocation(debitVideo.getX() + debitVideo.getWidth() + 3, debitVideo.getY() + 3);
	        }
	        
	    });
				
		lblVBR = new JLabel("VBR");
		lblVBR.setName("lblVBR");
		lblVBR.setBorder(new FlatLineBorder(new Insets(0,0,0,0), new Color(30,30,35), 1, 10));
		lblVBR.setBackground(new Color(42,42,47));
		lblVBR.setHorizontalAlignment(SwingConstants.CENTER);
		lblVBR.setFont(new Font(montserratFont, Font.PLAIN, 11));
		lblVBR.setBounds(h264lines.getX() + h264lines.getWidth(), debitVideo.getY() + 3, 32, 16);
		grpBitrate.add(lblVBR);
		
		lblVBR.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (lblVBR.getText().equals("VBR")
				&& (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())
				|| comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC")
				|| comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
				&& (comboFonctions.getSelectedItem().toString().equals("H.264") || comboFonctions.getSelectedItem().toString().equals("H.265")))
				{
					lblVBR.setText("CBR");
				}
				else if (lblVBR.getText().equals("CBR")
				|| lblVBR.getText().equals("VBR") && comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
				&& (comboAccel.getSelectedItem().equals("OSX VideoToolbox") == false || System.getProperty("os.arch").equals("aarch64"))
				&& comboFonctions.getSelectedItem().toString().contains("H.26") && comboAccel.getSelectedItem().equals("Vulkan Video") == false
				|| (lblVBR.getText().equals("VBR") && (comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().equals("AV1") || comboFonctions.getSelectedItem().toString().equals("H.266"))))
				{
					lblVBR.setText("CQ");
					bitrateSize.setText("-");
					String[] values = new String[53];
					values[0] = language.getProperty("lblBest");
					for (int i = 1 ; i < 52 ; i++)
					{
						values[i] = String.valueOf(i);
					}			
					values[52] = language.getProperty("lblWorst");
					debitVideo.setModel(new DefaultComboBoxModel<String>(values));
					debitVideo.setSelectedIndex(23);
					lblVideoBitrate.setText(language.getProperty("lblValue"));
					lblKbsH264.setVisible(false);
					h264lines.setVisible(false);					
					case2pass.setSelected(false);
					case2pass.setEnabled(false);
				}
				else
				{
					lblVBR.setText("VBR");
					debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(), language.getProperty("lblGood").toLowerCase(), "auto" }));
					debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
					lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
					lblKbsH264.setVisible(true);
					h264lines.setVisible(true);
					if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) || comboFonctions.getSelectedItem().toString().equals("VP8") == false && comboFonctions.getSelectedItem().toString().equals("VP9") == false && comboFonctions.getSelectedItem().toString().contains("H.26") == false)
						case2pass.setEnabled(true);
					FFPROBE.setLength();
				}
				
				if (comboFonctions.getSelectedItem().toString().equals("AV1"))
				{
					changeSections(false);
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
		
	}

	private void Reset() {

		btnReset = new JButton(language.getProperty("btnReset"));
		btnReset.setVisible(false);
		btnReset.setFont(new Font(montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(frame.getWidth(), 569, 309, 21);
		frame.getContentPane().add(btnReset);

		btnReset.addMouseListener(new MouseListener() {

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

				doNotLoadImage = true;
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {

				if (btnReset.isEnabled())
				{				
					// Resolution
					comboResolution.setSelectedIndex(0);
					comboImageOption.setSelectedIndex(0);
					caseCreateSequence.setSelected(false);
					comboInterpret.setEnabled(false);
					comboInterpret.setSelectedIndex(7);
					caseRotate.setSelected(false);
					comboRotate.setSelectedIndex(3);
					comboRotate.setEnabled(false);
					caseMiror.setSelected(false);
					btnNoUpscale.setSelected(false);
	
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
	
					// grpH264
					lock.setIcon(new FlatSVGIcon("contents/unlock.svg", 16, 16));
					isLocked = false;
					textH.setEnabled(true);
					textM.setEnabled(true);
					textS.setEnabled(true);
					textF.setEnabled(true);
					comboResolution.setEnabled(true);
					debitVideo.setEnabled(true);
					debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(), language.getProperty("lblGood").toLowerCase(), "auto" }));
					debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);				
					maximumBitrate.setEnabled(true);
					maximumBitrate.setSelectedIndex(maximumBitrate.getModel().getSize() - 1);
					bitrateSize.setEnabled(true);
					bitrateSize.setText("2000");
					lblVBR.setText("VBR");
					lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
					lblKbsH264.setVisible(true);
					h264lines.setVisible(true);
					
					if (comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
						FFPROBE.setLength();
					
					case2pass.setSelected(false);
					case2pass.setEnabled(true);
					caseQMax.setSelected(false);
					
					// grpSetAudio	
					grpSetAudio.removeAll();
					
					if (caseNormalizeAudio.isSelected())
					{
						caseNormalizeAudio.doClick();
						comboNormalizeAudio.setSelectedIndex(23);
					}
					
					if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed") )
					{
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
						lblAudioMapping.setSelectedItem("Multi");						
					}
					else if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
					|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
					|| comboFonctions.getSelectedItem().toString().equals("XAVC")
					|| comboFonctions.getSelectedItem().toString().equals("HAP")
					|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
					{
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { "Multi" }));
						lblAudioMapping.setSelectedItem("Multi");						
					}
					else
					{
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
						lblAudioMapping.setSelectedItem(language.getProperty("stereo"));					
					}				
					lbl48k.setSelectedIndex(2);
					
					caseAudioOffset.setSelected(false);
					txtAudioOffset.setEnabled(false);
					caseKeepSourceTracks.setSelected(false);
					
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);					
					comboAudio4.setSelectedIndex(3);	
					
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
					|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
					|| comboFonctions.getSelectedItem().toString().equals("XAVC")
					|| comboFonctions.getSelectedItem().toString().equals("HAP")
					|| comboFonctions.getSelectedItem().toString().equals("FFV1"))
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
					}
													
					if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString()) 
					|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "MP3", "AC3", "Opus", "Vorbis", "Dolby Digital Plus", language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(3);
						caseNormalizeAudio.setEnabled(false);
						caseNormalizeAudio.setSelected(false);
						comboNormalizeAudio.setEnabled(false);
						caseChangeAudioCodec.setSelected(false);
						comboAudioCodec.setEnabled(false);
						comboAudioBitrate.setEnabled(false);
						lbl48k.setEnabled(false);
						
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
						
						if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false
						&& language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
						{
							grpSetAudio.add(caseNormalizeAudio);
							grpSetAudio.add(comboNormalizeAudio);
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
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);						
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(0);
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if (comboFonctions.getSelectedItem().toString().contains("H.26"))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "MP3", "AC3", "Opus", "FLAC", "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", "ALAC 16Bits", "ALAC 24Bits", "Dolby Digital Plus", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);						
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("WMV".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);	
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("MPEG-1".equals(comboFonctions.getSelectedItem().toString()) || "MPEG-2".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);	
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("VP8".equals(comboFonctions.getSelectedItem().toString()) || "VP9".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus", "AAC", "Vorbis", "FLAC", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(11);	
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("AV1".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus", "AAC", "Vorbis", "FLAC", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(11);	
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("Theora".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Vorbis", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);	
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if ("Xvid".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}	
					else if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					{
						if (comboFonctions.getSelectedItem().toString().equals("DVD"))
						{
							comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						}
						else
							comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", "Dolby Digital Plus", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
						
						comboAudioCodec.setSelectedIndex(0);						
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(5);
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
						if (grpSetAudio.getHeight() > 92 || grpSetAudio.getHeight() == 68)
							extendSections(grpSetAudio, 92);
					}
					else if (comboFonctions.getSelectedItem().toString().equals("WAV")
					|| comboFonctions.getSelectedItem().toString().equals("AIFF")
					|| comboFonctions.getSelectedItem().toString().equals("FLAC")
					|| comboFonctions.getSelectedItem().toString().equals("ALAC")
					|| comboFonctions.getSelectedItem().toString().equals("MP3")
					|| comboFonctions.getSelectedItem().toString().equals("AAC")
					|| comboFonctions.getSelectedItem().toString().equals("AC3")
					|| comboFonctions.getSelectedItem().toString().equals("Opus")
					|| comboFonctions.getSelectedItem().toString().equals("Vorbis")
					|| comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus")
					|| comboFonctions.getSelectedItem().toString().equals("Dolby TrueHD"))
					{
						//Don't change anything
					}
					else //Editing codecs
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
	
						if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
						|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
						|| comboFonctions.getSelectedItem().toString().equals("XAVC"))
						{
							comboAudioCodec.setSelectedIndex(1);
						}
						else
						{
							comboAudioCodec.setSelectedIndex(0);
						}
						
						caseNormalizeAudio.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);
						
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
						
						if (grpSetAudio.getHeight() < 146 && grpSetAudio.getHeight() > 17)
							extendSections(grpSetAudio, 146);
					}
					
					grpSetAudio.repaint();
					
					if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed") 	
							|| comboFonctions.getSelectedItem().toString().contains("XDCAM")
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
					
					//grpCrop		
					if (caseEnableCrop.isSelected())
						caseEnableCrop.doClick();
					
					//grpOverlay
					if (caseShowTimecode.isSelected())
						caseShowTimecode.doClick();
					
					if (caseAddTimecode.isSelected())
						caseAddTimecode.doClick();
					
					if (caseShowFileName.isSelected())
						caseShowFileName.doClick();
					
					if (caseAddText.isSelected())
						caseAddText.doClick();
					
					//grpSubtitles
					if (caseAddSubtitles.isSelected())
						caseAddSubtitles.doClick();
					
					//grpWatermark
					if (caseAddWatermark.isSelected())
						caseAddWatermark.doClick();
					
					//grpCorrections
					for (Component c : grpCorrections.getComponents())
					{
						if (c != null && c instanceof JCheckBox && ((JCheckBox) c).isSelected())
						{
							((JCheckBox) c).doClick();
						}
					}
					
					//grpTransitions
					for (Component c : grpTransitions.getComponents())
					{
						if (c != null && c instanceof JCheckBox && ((JCheckBox) c).isSelected())
						{
							((JCheckBox) c).doClick();
						}
					}
					
	
					comboAccel.setSelectedIndex(0);
					
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
					
					if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
					{
						lblPad.setVisible(false);
					}
					else
					{
						lblPad.setVisible(true);
					}
					lblPad.setText(language.getProperty("lblPad"));
					
					caseOpenGop.setSelected(false);
					caseForceOutput.setSelected(false);
					caseFastStart.setSelected(false);
					caseFastDecode.setSelected(false);
					caseAlpha.setSelected(false);
					caseGOP.setSelected(false);
					gopSize.setEnabled(false);
					caseFilmGrain.setSelected(false);
					comboFilmGrain.setEnabled(false);
					caseFilmGrainDenoise.setSelected(false);
					comboFilmGrainDenoise.setEnabled(false);
					caseCABAC.setSelected(false);
					gopSize.setText("250");
					caseChunks.setSelected(false);
					chunksSize.setEnabled(false);
					chunksSize.setSelectedIndex(3);
					caseDRC.setSelected(false);				
					caseTruePeak.setSelected(false);
					comboTruePeak.setEnabled(false);
					comboTruePeak.setSelectedIndex(3);
					caseLRA.setSelected(false);
					comboLRA.setEnabled(false);
					comboLRA.setSelectedIndex(15);				
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
					if (caseAddSubtitles != null)
						caseAddSubtitles.setSelected(false);
					caseConform.setSelected(false);
					comboConform.setEnabled(false);
					caseDecimate.setSelected(false);
					comboConform.setSelectedIndex(3);
					comboFPS.setEnabled(false);
					comboFPS.setSelectedIndex(2);
					lblTFF.setText("TFF");
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
					comboColorspace.setSelectedIndex(0);
					
					if (caseLevels.isSelected())
						caseLevels.doClick();
					
					if (caseGamma.isSelected())
						caseGamma.doClick();
					
					if (caseColormatrix.isSelected())
						caseColormatrix.doClick();
					
					comboInColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "HDR"}));
					comboInColormatrix.setSelectedIndex(0);
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 601", "Rec. 709", "Rec. 2020", "SDR"}));
					comboOutColormatrix.setSelectedIndex(1);
					
					topPanel.repaint();
					statusBar.repaint();
					topImage.repaint();
					
					doNotLoadImage = false;
					
					VideoPlayer.resizeAll();
				}
			}
			
		});

	}

	private void StatusBar() {
		
		statusBar = new JPanel();
		statusBar.setName("statusBar");
		statusBar.setBackground(new Color(35,35,40));
		statusBar.setOpaque(true);
		statusBar.setLayout(null);
		statusBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(65, 65, 65)));
		statusBar.setBounds(0, frame.getHeight() - 23, extendedWidth, 22);
		
		statusBar.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
			}

			@Override
			public void mousePressed(MouseEvent e) {
				windowDrag = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				if (frame.getWidth() > 332 && VideoPlayer.setTime != null && VideoPlayer.isPiping == false)
				{
					VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
					
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					} while (VideoPlayer.setTime.isAlive());
				}
				
				windowDrag = false;
				
				//IMPORTANT
				if (FFPROBE.totalLength <= 40 && VideoPlayer.preview != null)
					VideoPlayer.preview = null;
				
				resizeAll(frame.getWidth(), 0);
			}
			
		});
		
		statusBar.addMouseMotionListener(new MouseMotionListener() {
	 			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				int i = e.getY() - 10;
				
				if (frame.getSize().getHeight() + i < 708)
					i = 0;
				
				int width = e.getX() + 10;
				
				if (windowDrag)
		       	{	
					resizeAll(width, i);     			
		       	}	
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
							
				if (MouseInfo.getPointerInfo().getLocation().x - Shutter.frame.getLocation().x > Shutter.frame.getSize().width - 20)
				{
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				}
				else if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height - 20)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				}
				else 
				{
					if (Shutter.windowDrag == false && Shutter.frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) == false)
					{
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}			
		});
		
		lblArrows = new JLabel("▲▼");
		lblArrows.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 15));
		lblArrows.setSize(lblArrows.getPreferredSize().width + 4, 20);
		lblArrows.setHorizontalAlignment(SwingConstants.CENTER);
		lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, statusBar.getSize().height - lblArrows.getSize().height);
		statusBar.add(lblArrows);
		
		lblBy = new JLabel(language.getProperty("lblCrParPaul"));
		lblBy.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblBy.setForeground(Color.WHITE);
		lblBy.setBounds(6, 4, lblBy.getPreferredSize().width, 15);
		statusBar.add(lblBy);

		lblBy.addMouseListener(new MouseListener() {

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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});
			
		lblBy.addComponentListener (new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
	        	tempsEcoule.setLocation(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY());
	    		tempsRestant.setLocation(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY());
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	tempsEcoule.setLocation(lblBy.getLocation());
	    		tempsRestant.setLocation(lblBy.getLocation());	        	
	        }
	    } );

		lblGpuDecoding = new JLabel(Shutter.language.getProperty("lblGpuDecoding"));
		lblGpuDecoding.setVisible(false);
		lblGpuDecoding.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblGpuDecoding.setBounds(750, lblBy.getY(), lblGpuDecoding.getPreferredSize().width, 15);
		statusBar.add(lblGpuDecoding);
								
		//GPU decoding
		if (System.getProperty("os.name").contains("Windows"))
			FFMPEG.hwaccel("-hwaccels" + '"');
		else
			FFMPEG.hwaccel("-hwaccels");
				
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFMPEG.runProcess.isAlive());
		
        comboGPUDecoding = new JComboBox<String>( FFMPEG.hwaccels.toString().split(System.lineSeparator()) );
		comboGPUDecoding.setName("comboGPUDecoding");
		comboGPUDecoding.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboGPUDecoding.setEditable(false);
		comboGPUDecoding.setSelectedItem(language.getProperty("aucun"));
		comboGPUDecoding.setBounds(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6, lblGpuDecoding.getLocation().y - 1, comboGPUDecoding.getPreferredSize().width, 16);
		comboGPUDecoding.setMaximumRowCount(10);
		comboGPUDecoding.setVisible(false);
		statusBar.add(comboGPUDecoding);
		
		comboGPUDecoding.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
				{
					if (comboGPUDecoding.getSelectedItem().equals("auto"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "auto", language.getProperty("aucun") }));
						comboGPUFilter.setSelectedIndex(0);
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("qsv"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "qsv", language.getProperty("aucun") }));
						comboGPUFilter.setSelectedIndex(0);
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("cuda"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "cuda", language.getProperty("aucun") }));
						comboGPUFilter.setSelectedIndex(0);
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("vulkan"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "vulkan", language.getProperty("aucun") }));
						comboGPUFilter.setSelectedIndex(0);
						comboGPUFilter.setEnabled(true);
					}
					else
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("aucun") }));
						comboGPUFilter.setSelectedIndex(0);
						comboGPUFilter.setEnabled(false);
					}
				}				
				
				if (VideoPlayer.videoPath != null)
				{					
					//FFMPEG.checkGPUCapabilities(VideoPlayer.videoPath);			
				}
								
				VideoPlayer.frameIsComplete = false;
				
				VideoPlayer.playerSetTime(VideoPlayer.slider.getValue());	
			}
			
		});
				
		lblGpuFiltering = new JLabel(Shutter.language.getProperty("lblGpuFiltering"));
		lblGpuFiltering.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblGpuFiltering.setBounds(comboGPUDecoding.getLocation().x + comboGPUDecoding.getWidth() + 6, lblBy.getY(), lblGpuFiltering.getPreferredSize().width, 15);
		lblGpuFiltering.setVisible(false);
		statusBar.add(lblGpuFiltering);
		
		comboGPUFilter = new JComboBox<String>(new String[] {"auto", language.getProperty("aucun")} );
		comboGPUFilter.setName("comboGPUFilter");		
		comboGPUFilter.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboGPUFilter.setEditable(false);
		comboGPUFilter.setVisible(false);
		comboGPUFilter.setBounds(lblGpuFiltering.getX() + lblGpuFiltering.getWidth() + 6, comboGPUDecoding.getY(), comboGPUFilter.getPreferredSize().width, 16);
		comboGPUFilter.setMaximumRowCount(10);
				
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) && comboGPUDecoding.getSelectedItem().equals(language.getProperty("aucun")) == false)
		{
			comboGPUFilter.setSelectedItem("auto");
			comboGPUFilter.setEnabled(true);
		}
		else
		{
			comboGPUFilter.setSelectedItem(language.getProperty("aucun"));
			comboGPUFilter.setEnabled(false);
		}		
		statusBar.add(comboGPUFilter);
		
		comboGPUFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
								
				VideoPlayer.frameIsComplete = false;
				
				VideoPlayer.playerSetTime(VideoPlayer.slider.getValue());
			}
			
		});
		
		lblHWaccel = new JLabel(language.getProperty("caseAccel"));
		lblHWaccel.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblHWaccel.setVisible(false);
		lblHWaccel.setBounds(comboGPUFilter.getX() + comboGPUFilter.getWidth() + 6, lblBy.getY(), lblHWaccel.getPreferredSize().width, 15);
		statusBar.add(lblHWaccel);

		comboAccel = new JComboBox<String>();
		comboAccel.setName("comboAccel");
		comboAccel.setMaximumRowCount(20);
		comboAccel.setVisible(false);
		comboAccel.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("aucune").toLowerCase() } ));	
		comboAccel.setSelectedIndex(0);
		comboAccel.setFont(new Font(freeSansFont, Font.PLAIN, 10));
		comboAccel.setEditable(false);
		comboAccel.setBounds(lblHWaccel.getLocation().x + lblHWaccel.getWidth() + 4, comboGPUDecoding.getY(), 115, 16);
		statusBar.add(comboAccel);
		
		comboAccel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
						
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if ("H.264".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high"}));
						comboForceProfile.setSelectedIndex(2);
					}
					else 
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] {"main"}));
						comboForceProfile.setSelectedIndex(0);
						
						if (comboFonctions.getSelectedItem().equals("H.265") && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
						{
							comboForceLevel.setVisible(false);
						}
					}

					if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						if (comboForcePreset.getModel().getSize() != 7)
						{
							comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow"}));
							comboForcePreset.setSelectedIndex(3);
						}
					}
	    			else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals("Vulkan Video"))
	    			{
    					caseForcePreset.setSelected(false);
    					caseForcePreset.setEnabled(false);
    					comboForcePreset.setEnabled(false);
	    			}
					
					if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						caseQMax.setEnabled(false);
						caseQMax.setSelected(false);
					}
					else
					{
						caseAlpha.setEnabled(false);
						caseAlpha.setSelected(false);
					}
						
					if (lblVBR.getText().equals("CBR") || lblVBR.getText().equals("CQ") && comboAccel.getSelectedItem().equals("OSX VideoToolbox") && System.getProperty("os.arch").equals("amd64"))
					{								
						lblVBR.setText("VBR");
						debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(), language.getProperty("lblGood").toLowerCase(), "auto" }));
						debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
						lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
						lblKbsH264.setVisible(true);
						h264lines.setVisible(true);
						if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
							case2pass.setEnabled(true);
						
						FFPROBE.setLength();								
					}
										
					caseAlpha.setEnabled(true);
					caseForcerEntrelacement.setSelected(false);
					caseForcerEntrelacement.setEnabled(false);
					caseForceTune.setSelected(false);
					caseForceTune.setEnabled(false);
					comboForceTune.setEnabled(false);
					caseForceOutput.setSelected(false);
					caseForceOutput.setEnabled(false);
					case2pass.setSelected(false);
					case2pass.setEnabled(false);					
				} 
				else
				{
					comboForceLevel.setVisible(true);
					
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
    				
					if (("H.264".equals(comboFonctions.getSelectedItem().toString()) || "H.265".equals(comboFonctions.getSelectedItem().toString())) && comboForcePreset.getModel().getSize() != 10)
    				{
	    				comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow", "placebo"}));
	    				comboForcePreset.setSelectedIndex(5);
    				} 
					else if ("H.266".equals(comboFonctions.getSelectedItem().toString()) && comboForcePreset.getModel().getSize() != 5)
					{				    
						comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "faster", "fast",  "medium",  "slow", "slower"}));
	    				comboForcePreset.setSelectedIndex(2);
					}
					
					caseForcerEntrelacement.setEnabled(true);					

					lblVBR.setVisible(true);
					
					caseQMax.setEnabled(true);
					
					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);
					
					caseForceTune.setEnabled(true);
					
					if (lblVBR.getText().equals("CQ") == false)
						case2pass.setEnabled(true);
									
					caseForceOutput.setEnabled(true);									
					caseForcerEntrelacement.setEnabled(true);
					
					lblVBR.setVisible(true);
					
					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);
					
					caseForceTune.setEnabled(true);
				}
				
				changeSections(false);
				
				if ("Apple ProRes".equals(comboFonctions.getSelectedItem().toString()) && System.getProperty("os.name").contains("Mac") && arch.equals("arm64"))
				{
					changeFilters();
				}
			}

		});
				
		tempsRestant = new JLabel(language.getProperty("tempsRestant"));
		tempsRestant.setVisible(false);
		tempsRestant.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		tempsRestant.setForeground(Color.WHITE);
		tempsRestant.setBounds(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY(), tempsRestant.getPreferredSize().width, 15);
		statusBar.add(tempsRestant);
		
		tempsRestant.addComponentListener (new ComponentAdapter ()
	    {
	        public void componentShown ( ComponentEvent e )
	        {
				tempsEcoule.setVisible(false);
	        }

	        public void componentHidden ( ComponentEvent e )
	        {
	        	if (tempsEcoule != null && tempsEcoule.getText().equals(language.getProperty("tempsEcoule")) == false)
	        	{
	        		if (FFMPEG.isRunning == false && YOUTUBEDL.isRunning == false)
	        		{
	        			lblBy.setVisible(true);
	        		}
	        		tempsEcoule.setVisible(true);
	        	}
	        }
	    } );
		
		tempsRestant.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				tempsRestant.setVisible(false);
				
				if (tempsEcoule != null && tempsEcoule.getText().equals(language.getProperty("tempsEcoule")) == false)
				{
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
		tempsEcoule.setForeground(Color.WHITE);
		tempsEcoule.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		tempsEcoule.setBounds(tempsRestant.getX(), lblBy.getY(), tempsEcoule.getPreferredSize().width, 15);
		statusBar.add(tempsEcoule);
		
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

		lblYears = new JLabel("2013-2025");
		lblYears.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYears.setForeground(Color.WHITE);
		lblYears.setFont(new Font(freeSansFont, Font.PLAIN, 12));
		lblYears.setBounds(extendedWidth - lblYears.getPreferredSize().width - 10, lblBy.getY(), lblYears.getPreferredSize().width + 4, 15);
		statusBar.add(lblYears);

		lblYears.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				final JFrame oldVersions = new JFrame();
				oldVersions.setUndecorated(true);
				oldVersions.getContentPane().setBackground(new Color(30,30,35));
				oldVersions.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f));
				oldVersions.setSize(1600, 423);
				oldVersions.setShape(new AntiAliasedRoundRectangle(0, 0, oldVersions.getWidth() + 18,
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}

		});
								
		frame.getContentPane().add(statusBar);

	}

	public static void changeFunction(final boolean anim) {		
		
		String function = comboFonctions.getSelectedItem().toString();
		if (language.getProperty("functionConform").equals(function)
				|| language.getProperty("functionSubtitles").equals(function)
				|| language.getProperty("functionCut").equals(function) || language.getProperty("functionRewrap").equals(function) 
				|| language.getProperty("functionMerge").equals(function)
				|| language.getProperty("functionReplaceAudio").equals(function)
				|| "WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function) || "ALAC".equals(function)
				|| "MP3".equals(function) || "AAC".equals(function) || "AC3".equals(function) || "Opus".equals(function)
				|| "Vorbis".equals(function) || "Dolby Digital Plus".equals(function) || "Dolby TrueHD".equals(function) || "Loudness & True Peak".equals(function)
				|| language.getProperty("functionBlackDetection").equals(function) || language.getProperty("functionOfflineDetection").equals(function) || "VMAF".equals(function) || "FrameMD5".equals(function)
				|| "DNxHD".equals(function)	|| "DNxHR".equals(function) || "Apple ProRes".equals(function) || "QT Animation".equals(function) || ("GoPro CineForm").equals(function) || "Uncompressed".equals(function)
				|| "H.264".equals(function) || "H.265".equals(function) || "H.266".equals(function) || "DV PAL".equals(function)
				|| "WMV".equals(function) || "MPEG-1".equals(function) || "MPEG-2".equals(function) || "VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function) || "Theora".equals(function)
				|| "MJPEG".equals(function) || "Xvid".equals(function) || "XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function) || "AVC-Intra 100".equals(function) || ("XAVC").equals(function) || "HAP".equals(function) || "FFV1".equals(function)
				|| "DVD".equals(function) || "Blu-ray".equals(function) || "QT JPEG".equals(function)
				|| language.getProperty("functionPicture").equals(function)
				|| "JPEG".equals(function) || "JPEG XL".equals(function)
				|| language.getProperty("functionNormalization").equals(function))
		{
			changeWidth(true);
			changeSections(anim);
			
		} else if (language.getProperty("functionExtract").equals(function)				
				|| language.getProperty("functionInsert").equals(function)
				|| "CD RIP".equals(function)
				|| "DVD Rip".equals(function)				
				|| language.getProperty("functionSceneDetection").equals(function)
				|| language.getProperty("functionWeb").equals(function))
		{
			changeWidth(false);
			changeSections(anim);
			
		} 
		else if (language.getProperty("itemMyFunctions").equals(function))
		{
			changeWidth(false);
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
		
		//Render queue
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
		&& comboFonctions.getSelectedItem().equals("VMAF") == false
		&& comboFonctions.getSelectedItem().equals("FrameMD5") == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("itemMyFunctions")) == false
		&& (RenderQueue.frame == null || RenderQueue.frame != null && RenderQueue.frame.isVisible() == false)
		&& comboResolution.getSelectedItem().toString().contains("AI") == false)
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
		|| comboFonctions.getSelectedItem().equals("VMAF")
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) 
		{
			
			caseOpenFolderAtEnd1.setEnabled(false);
			caseChangeFolder1.setEnabled(false);
			lblDestination1.setVisible(false);
			
			btnExtension.setEnabled(false);
			txtExtension.setEnabled(false);
			caseSubFolder.setEnabled(false);
			txtSubFolder.setEnabled(false);
			caseDeleteSourceFile.setEnabled(false);
			caseDeleteSourceFile.setSelected(false);
			caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
			
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")))
			{
				setDestinationTabs(5);
			}
			else
			{			
				if (comboFonctions.getSelectedItem().equals("Loudness & True Peak")
				|| comboFonctions.getSelectedItem().equals("VMAF"))
				{
					setDestinationTabs(2);
				}
				else if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
				{
					caseOpenFolderAtEnd1.setEnabled(true);
					caseChangeFolder1.setEnabled(true);
					lblDestination1.setVisible(true);
					
					btnExtension.setEnabled(true);
					txtExtension.setEnabled(true);
					caseSubFolder.setEnabled(true);
					txtSubFolder.setEnabled(true);
					
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
				setDestinationTabs(6);
			}
			else
				setDestinationTabs(5);
			
			caseOpenFolderAtEnd1.setEnabled(true);
			caseChangeFolder1.setEnabled(true);
			lblDestination1.setVisible(true);

			if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
			{
				btnExtension.setEnabled(false);
				txtExtension.setEnabled(false);
				caseSubFolder.setEnabled(false);
				txtSubFolder.setEnabled(false);
				caseDeleteSourceFile.setEnabled(false);
				caseDeleteSourceFile.setSelected(false);
				caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
			}
			else
			{
				btnExtension.setEnabled(true);
				txtExtension.setEnabled(true);
				caseSubFolder.setEnabled(true);
				txtSubFolder.setEnabled(true);
				caseDeleteSourceFile.setEnabled(true);
			}
		}
		
		if (btnExtension.isSelected() == false)
			txtExtension.setEnabled(false);
		
		if (caseSubFolder.isSelected() == false)
			txtSubFolder.setEnabled(false);

		// btnStart text
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
		{
			btnStart.setText(language.getProperty("btnDownload"));
		}
		else if (RenderQueue.frame != null)
		{
			if (RenderQueue.frame.isVisible()
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
					&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
					&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
					&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
					&& comboFonctions.getSelectedItem().equals("VMAF") == false
					&& comboFonctions.getSelectedItem().equals("FrameMD5") == false)
			{
				btnStart.setText(language.getProperty("btnAddToRender"));
			}
			else
			{
				if (FFMPEG.isRunning == false)
					btnStart.setText(language.getProperty("btnStartFunction"));
			}
		}
		else
		{
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
		if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("Apple ProRes")))
		{
			//Nothing
		} 
		else
		{
			caseForcerEntrelacement.setEnabled(true);
		}

		if (caseForceLevel.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("AV1")))
		{
			caseForceLevel.setEnabled(true);
			comboForceProfile.setEnabled(true);
			comboForceLevel.setEnabled(true);
		} 
		else
		{
			comboForceProfile.setEnabled(false);
			comboForceLevel.setEnabled(false);
		}
		
		if (caseForcePreset.isSelected() && comboFonctions.getSelectedItem().toString().contains("H.26") && caseQMax.isSelected() == false)
		{
			caseForcePreset.setEnabled(true);
			comboForcePreset.setEnabled(true);
		} 
		else
			comboForcePreset.setEnabled(false);
		
				
		if (caseForceTune.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26") || comboFonctions.getSelectedItem().toString().equals("VP8") || comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem().toString().contains("AV1")))
		{
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

	public static void changeWidth(final boolean bigger) {
							
		String function = comboFonctions.getSelectedItem().toString();
		
		noSettings = false;
		
		boolean noVideoPlayer = false;
		if (Settings.btnDisableVideoPlayer.isSelected() && function.isEmpty() == false && bigger)
			noVideoPlayer = true;

		boolean forceFullSize = false;
		if (caseAddWatermark.isSelected() || caseAddTimecode.isSelected() || caseShowTimecode.isSelected() || caseAddText.isSelected() || caseShowFileName.isSelected())
		{
			forceFullSize = true;
		}
		
		if (bigger == false && FFMPEG.isRunning && caseDisplay.isSelected() == false && forceFullSize == false)
		{		
			noSettings = true;
			
			frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2, frame.getY() + (frame.getHeight() - 708) / 2, 332, 708);	
			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);
			
		    lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
		    lblYears.setVisible(false);
		}
		else if (language.getProperty("functionConform").equals(function)
		|| "DV PAL".equals(function)
		|| language.getProperty("functionSubtitles").equals(function)
		|| "Loudness & True Peak".equals(function)
		|| language.getProperty("functionBlackDetection").equals(function)
		|| language.getProperty("functionOfflineDetection").equals(function)
		|| "VMAF".equals(function))
		{
			noSettings = true;
											
			if (Settings.btnDisableVideoPlayer.isSelected())
			{			
				frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2, frame.getY() + (frame.getHeight() - 708) / 2, 332, 708);	
				lblArrows.setVisible(true);
				lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
				lblGpuDecoding.setVisible(false);
				comboGPUDecoding.setVisible(false);
				lblGpuFiltering.setVisible(false);
				comboGPUFilter.setVisible(false);
				
			    lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
			    lblYears.setVisible(false);
			}
			else
			{	
				if (frame.getSize().width == 332)
				{
					frame.setBounds(frame.getX() - (1350 - 312 - 332) / 2, frame.getY(), 1350 - 312, frame.getHeight());
				}
				else if (frame.getSize().width == 654)
				{
					frame.setBounds(frame.getX() - (1350 - 312 - 654) / 2, frame.getY(), 1350 - 312, frame.getHeight());
				}
				else if (frame.getSize().width != (1350 - 312))
				{
					frame.setBounds(frame.getX() - (1350 - 312 - extendedWidth) / 2, frame.getY(), 1350 - 312, frame.getHeight());
				}
			}
				
			VideoPlayer.setPlayerButtons(true);
			VideoPlayer.player.setVisible(true);

			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);			
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
			{
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}
			
			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);	
			lblYears.setLocation(frame.getWidth()  - lblYears.getWidth() - 8, lblBy.getY());
		    lblYears.setVisible(false);
		}
		else if (language.getProperty("functionMerge").equals(function) || language.getProperty("functionNormalization").equals(function) || noVideoPlayer)
		{						
			noSettings = true;
			
			if (frame.getSize().width == 332)
			{
				frame.setBounds(frame.getX() - (654 - 332) / 2, frame.getY(), 654, frame.getHeight());
			}
			else if (frame.getSize().width == 1350 - 312)
			{
				frame.setBounds(frame.getX() - (654 - (1350 - 312)) / 2, frame.getY(), 654, frame.getHeight());
			}
			else if (frame.getSize().width != 654)
			{
				frame.setBounds(frame.getX() + (extendedWidth - 654) / 2, frame.getY(), 654, frame.getHeight());
			}
			
			VideoPlayer.setPlayerButtons(false);
			VideoPlayer.player.setVisible(false);
			
			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);			
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);			
			
			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);	
			lblYears.setLocation(frame.getWidth()  - lblYears.getWidth() - 8, lblBy.getY());
		    lblYears.setVisible(false);
		}
		else if (bigger && frame.getSize().width < 1350)
		{			
			if (frame.getSize().width == (1350 - 312))
			{
				frame.setBounds(frame.getX() - (extendedWidth - (1350 - 312)) / 2, frame.getY(), extendedWidth, frame.getHeight());
			}
			else if (frame.getSize().width == 654)
			{
				frame.setBounds(frame.getX() - (extendedWidth - 654) / 2, frame.getY(), extendedWidth, frame.getHeight());
			}
			else if (frame.getSize().width == 332)
			{
				frame.setBounds(frame.getX() - (extendedWidth - 332) / 2, frame.getY(), extendedWidth, frame.getHeight());
			}
			
			VideoPlayer.setPlayerButtons(true);
			VideoPlayer.player.setVisible(true);
			
			lblArrows.setVisible(false);
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
			{
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}
			
			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);	
			lblYears.setLocation(frame.getWidth()  - lblYears.getWidth() - 8, lblBy.getY());
		    lblYears.setVisible(true);
		}
		else if (bigger == false && frame.getSize().width > 332 && forceFullSize == false)
		{			
			noSettings = true;
			
			frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2, frame.getY() + (frame.getHeight() - 708) / 2, 332, 708);	
			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);
			
		    lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
		    lblYears.setVisible(false);
		}
		else if (bigger && frame.getSize().width > 332)
		{
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
			{
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}
		}
		
		//Checking maximum screen width
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
		
		double dpiScaleFactor = 1.0;
        if (System.getProperty("os.name").contains("Windows"))
        {
        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
        	dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
        }
        
		int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);		

		if (frame.getWidth() > screenWidth)
		{
			extendedWidth = screenWidth;
			toggleFullscreen();
		}
		
		resizeAll(frame.getWidth(), 0);		
	}
	
	private static void toggleFullscreen() {
		
		//IMPORTANT
		if (FFPROBE.totalLength <= 40 && VideoPlayer.preview != null)
			VideoPlayer.preview = null;
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
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
		
		double dpiScaleFactor = 1.0;
        if (System.getProperty("os.name").contains("Windows"))
        {
        	double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
            double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
        	dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
        }
        
        int screenHeight = (int) (allScreens[screenIndex].getDisplayMode().getHeight() * dpiScaleFactor);	
		int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);			    
		int screenX = (int) allScreens[screenIndex].getDefaultConfiguration().getBounds().getX();
		
		int height = 0;
		int screenOffset = allScreens[screenIndex].getDefaultConfiguration().getBounds().y;
		
		if ((frame.getHeight() < screenHeight - taskBarHeight || frame.getWidth() < screenWidth) && frame.getWidth() > 332 && frame.getWidth() != 654 && frame.getWidth() != (extendedWidth - 312))
		{		
			height = screenHeight - taskBarHeight - frame.getHeight();
			resizeAll(screenWidth, height);
			frame.setLocation(screenX, screenOffset);	        		
		}
		else if ((frame.getHeight() == screenHeight - taskBarHeight && frame.getWidth() == screenWidth))
		{		
			height = 708 - frame.getHeight();
			resizeAll(extendedWidth, height);
			frame.setLocation(screenX + dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2 + screenOffset);
		}
		else if (frame.getHeight() >= screenHeight - taskBarHeight)
		{
			height = 708 - frame.getHeight();
			resizeAll(frame.getWidth(), height);
			frame.setLocation(frame.getX(), dim.height/2-frame.getSize().height/2 + screenOffset);	
		}
		else
		{
			height = screenHeight - taskBarHeight - frame.getHeight();
			resizeAll(frame.getWidth(), height);
			frame.setLocation(frame.getX(), screenOffset);	
		}				
		
		if (frame.getWidth() > 332 && VideoPlayer.setTime != null && VideoPlayer.isPiping == false)
		{
			VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
			
			do {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {}
			} while (VideoPlayer.setTime.isAlive());
			
			//grpWatermark
			if (caseAddWatermark.isSelected()
			&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false
			&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")) == false)
			{
				VideoPlayer.loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
				Shutter.logo.setLocation((int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosX.getText()) / Shutter.playerRatio), (int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosY.getText()) / Shutter.playerRatio));
			}
		}
	}
	
	public static void resizeAll(int width, int height) {
					
		if (frame.getWidth() >= 1130 && width >= 1130)
		{
			frame.setSize(width, frame.getHeight() + height);					
		}
		else
		    frame.setSize(frame.getSize().width, frame.getHeight() + height);

		if (frame.getWidth() < 1350 && frame.getWidth() >= 1130)
		{
			extendedWidth = frame.getWidth();
		}
		
		if (frame.getWidth() < 1320 && noSettings == false)
		{
			VideoPlayer.lblSpeed.setVisible(false);
			VideoPlayer.lblVolume.setVisible(false);
			
			if (frame.getWidth() < 1300)
			{
				VideoPlayer.sliderSpeed.setVisible(false);
				VideoPlayer.sliderVolume.setVisible(false);
			}
			else if (Shutter.frame.getSize().width > 654 && FFPROBE.totalLength > 40 && Shutter.caseEnableSequence.isSelected() == false && VideoPlayer.isPiping == false)
			{
				VideoPlayer.sliderSpeed.setVisible(true);
				VideoPlayer.sliderVolume.setVisible(true);
			}
		}	
		else if (Shutter.frame.getSize().width > 654 && FFPROBE.totalLength > 40 && Shutter.caseEnableSequence.isSelected() == false && VideoPlayer.isPiping == false)
		{
			VideoPlayer.lblSpeed.setVisible(true);
			VideoPlayer.lblVolume.setVisible(true);
			VideoPlayer.sliderSpeed.setVisible(true);
			VideoPlayer.sliderVolume.setVisible(true);
		}
				
		if (frame.getWidth() > 332)
		{
			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);	
			lblV.setVisible(true);
		}
		else
		{
			lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);		
			lblV.setVisible(false);
		}
		
		setGPUOptions();
		
		lblV.setLocation(lblShutterEncoder.getX() + lblShutterEncoder.getWidth(), 5);
		
		if (frame.getWidth() > 332)
		{
			int grpX = frame.getWidth() - 312 - 12;
			grpResolution.setLocation(grpX, grpResolution.getLocation().y);
			grpBitrate.setLocation(grpX, grpBitrate.getLocation().y);								
			grpSetAudio.setLocation(grpX, grpSetAudio.getLocation().y);
			grpAudio.setLocation(grpX, grpAudio.getLocation().y);								
			grpCrop.setLocation(grpX, grpCrop.getLocation().y);								
			grpOverlay.setLocation(grpX, grpOverlay.getLocation().y);
			grpSubtitles.setLocation(grpX, grpSubtitles.getLocation().y);
			grpWatermark.setLocation(grpX, grpWatermark.getLocation().y);
			grpColorimetry.setLocation(grpX, grpColorimetry.getLocation().y);								
			grpImageAdjustement.setLocation(grpX, grpImageAdjustement.getLocation().y);								
			grpCorrections.setLocation(grpX, grpCorrections.getLocation().y);	
			grpTransitions.setLocation(grpX, grpTransitions.getLocation().y);	
			grpImageSequence.setLocation(grpX, grpImageSequence.getLocation().y);
			grpImageFilter.setLocation(grpX, grpImageFilter.getLocation().y);
			grpSetTimecode.setLocation(grpX, grpSetTimecode.getLocation().y);
			grpAdvanced.setLocation(grpX, grpAdvanced.getLocation().y);
			btnReset.setLocation((grpX + 2), btnReset.getLocation().y);
		}
		
		topPanel.setBounds(0, 0, frame.getWidth(), 28);
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
		quit.setLocation(frame.getSize().width - 20, 4);
		fullscreen.setLocation(quit.getLocation().x - 20, 4);
		reduce.setLocation(fullscreen.getLocation().x - 20, 4);
		help.setLocation(reduce.getLocation().x - 20, 4);
		newInstance.setLocation(help.getLocation().x - 20, 4);
  		
		grpChooseFiles.setSize(grpChooseFiles.getWidth(), frame.getHeight() - 400);
		fileList.setSize(292, frame.getHeight() - 460);
		addToList.setSize(fileList.getSize());					
		scrollBar.setSize(292, fileList.getHeight());
		grpChooseFunction.setLocation(grpChooseFunction.getX(), grpChooseFiles.getY() + grpChooseFiles.getHeight() + 4);
		grpDestination.setLocation(grpDestination.getX(), grpChooseFunction.getY() + grpChooseFunction.getHeight() + 10);
		grpProgression.setLocation(grpProgression.getX(), grpDestination.getY() + grpDestination.getHeight() + 6);
        
        //On récupère le groupe qui est le plus haut
		JPanel top;
		
		if (grpResolution.isVisible())
		{
			top = grpResolution;
		}
		else if (grpSetTimecode.isVisible())
		{
			top = grpSetTimecode;
		}
		else if (grpSetAudio.isVisible())
		{
			top = grpSetAudio;
		}
		else
		{
			top = grpAudio;
		}
							
		//Empêche de faire dépasser la position minimale de top
		if (height < 0 && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
		{
			height = 0;
		}
		
		//Pré calcul
		if (top.getY() + height >= grpChooseFiles.getY() && height > 0)
		{
			height = grpChooseFiles.getY() - top.getY();	
		}
		
		if (frame.getWidth() > 332 && top.getY() < 30) 
		{		
			grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + height);
			grpBitrate.setLocation(grpBitrate.getLocation().x, grpBitrate.getLocation().y + height);								
			grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + height);
			grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + height);								
			grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + height);								
			grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + height);
			grpSubtitles.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getLocation().y + height);
			grpWatermark.setLocation(grpWatermark.getLocation().x, grpWatermark.getLocation().y + height);
			grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + height);								
			grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x, grpImageAdjustement.getLocation().y + height);								
			grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + height);	
			grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + height);	
			grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + height);
			grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + height);
			grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + height);
			grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + height);
			btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + height);		
		}
		
		if (noSettings == false && (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31 || top.getY() < 30))
		{
			settingsScrollBar.setVisible(true);
		}
		else
			settingsScrollBar.setVisible(false);
							
		if (System.getProperty("os.name").contains("Mac") && windowDrag)
		{
			frame.setShape(null);
		}
		else
		{
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
            shape1.add(shape2);
    		frame.setShape(shape1);
		}
		
		settingsScrollBar.setBounds(frame.getWidth() - settingsScrollBar.getWidth() - 2, topPanel.getHeight() - 4, 11, frame.getHeight() - topPanel.getHeight() - statusBar.getHeight() + 4);

		//For grpOverlay resizing
		if (windowDrag == false && (caseAddTimecode.isSelected() || caseShowTimecode.isSelected() || caseAddText.isSelected() || caseShowFileName.isSelected()))
		{
			windowDrag = true;
			VideoPlayer.resizeAll();
			windowDrag = false;
		}
		else if (frame.getWidth() > 332)
		{
			VideoPlayer.resizeAll();
		}
		else if (frame.getWidth() == 332)
		{
			statusBar.setBounds(0, frame.getHeight() - 23, frame.getWidth(), 22);
		}
		
	}
		
	private static void setGPUOptions() {
		
		String function = comboFonctions.getSelectedItem().toString();

		if ("Apple ProRes".equals(function) && System.getProperty("os.name").contains("Mac") && arch.equals("arm64")
		|| "H.264".equals(function) || "H.265".equals(function) || "H.266".equals(function) || "AV1".equals(function)
		|| System.getProperty("os.name").contains("Windows") && "VP9".equals(function)
		|| System.getProperty("os.name").contains("Windows") && language.getProperty("functionPicture").equals(function) && comboFilter.getSelectedItem().toString().equals(".avif"))
		{
			lblHWaccel.setVisible(true);
			comboAccel.setVisible(true);
		}
		else
		{
			lblHWaccel.setVisible(false);
			comboAccel.setVisible(false);
		}		
		
		if (frame.getWidth() > 332)
		{
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
			{
				if (lblHWaccel.isVisible())
				{
					lblGpuDecoding.setLocation(frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth() + 6 + lblGpuFiltering.getWidth() + 6 + comboGPUFilter.getWidth() + 6 + lblHWaccel.getWidth() + 6 + comboAccel.getWidth()) / 2, lblGpuDecoding.getY());
				}
				else
					lblGpuDecoding.setLocation(frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth() + 6 + lblGpuFiltering.getWidth() + 6 + comboGPUFilter.getWidth()) / 2, lblGpuDecoding.getY());
				
				comboGPUDecoding.setLocation(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6, lblGpuDecoding.getLocation().y - 1);
				lblGpuFiltering.setLocation(comboGPUDecoding.getLocation().x + comboGPUDecoding.getWidth() + 6, lblBy.getY());
				comboGPUFilter.setLocation(lblGpuFiltering.getX() + lblGpuFiltering.getWidth() + 6, comboGPUDecoding.getY());
				lblHWaccel.setLocation(comboGPUFilter.getX() + comboGPUFilter.getWidth() + 6, lblBy.getY());
			}
			else
			{
				if (lblHWaccel.isVisible())
				{
					lblGpuDecoding.setLocation(frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth() + 6 + lblHWaccel.getWidth() + 6 + comboAccel.getWidth()) / 2, lblGpuDecoding.getY());
				}
				else
					lblGpuDecoding.setLocation(frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth()) / 2, lblGpuDecoding.getY());
				
				comboGPUDecoding.setLocation(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6, lblGpuDecoding.getLocation().y - 1);
				lblHWaccel.setLocation(comboGPUDecoding.getX() + comboGPUDecoding.getWidth() + 6, lblBy.getY());
			}
			comboAccel.setLocation(lblHWaccel.getLocation().x + lblHWaccel.getWidth() + 4, comboGPUDecoding.getY());
		}
	}
	
	public static void changeSections(final boolean action) {
				
		if (frame.getWidth() > 332)
		{
			Thread changeSize = new Thread(new Runnable() {
				
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void run() {
	
					if (changeGroupes == false) // permet d'attendre la fin de l'action
					{
						try {
							
							if (frame.getSize().width >= 1130 && action)
							{							
								int i = frame.getWidth() - 312 - 12;
								
								do {
									
									long startTime = System.nanoTime();
									
									changeGroupes = true;
									if (Settings.btnDisableAnimations.isSelected())
										i = frame.getWidth();
									else
										i += 4;
									
									grpResolution.setLocation(i, grpResolution.getLocation().y);
									grpBitrate.setLocation(i, grpBitrate.getLocation().y);								
									grpSetAudio.setLocation(i, grpSetAudio.getLocation().y);
									grpAudio.setLocation(i, grpAudio.getLocation().y);								
									grpCrop.setLocation(i, grpCrop.getLocation().y);								
									grpOverlay.setLocation(i, grpOverlay.getLocation().y);
									grpSubtitles.setLocation(i, grpSubtitles.getLocation().y);
									grpWatermark.setLocation(i, grpWatermark.getLocation().y);
									grpColorimetry.setLocation(i, grpColorimetry.getLocation().y);								
									grpImageAdjustement.setLocation(i, grpImageAdjustement.getLocation().y);								
									grpCorrections.setLocation(i, grpCorrections.getLocation().y);	
									grpTransitions.setLocation(i, grpTransitions.getLocation().y);	
									grpImageSequence.setLocation(i, grpImageSequence.getLocation().y);
									grpImageFilter.setLocation(i, grpImageFilter.getLocation().y);
									grpSetTimecode.setLocation(i, grpSetTimecode.getLocation().y);
									grpAdvanced.setLocation(i, grpAdvanced.getLocation().y);
									btnReset.setLocation((i + 2), btnReset.getLocation().y);
	
									//Animate size
									animateSections(startTime, true);	
									
								} while (i < frame.getWidth());
							}
							
							VideoPlayer.seekOnKeyFrames = false;
	
							String function = comboFonctions.getSelectedItem().toString();
								
							List<String> graphicsAccel = new ArrayList<String>();
							graphicsAccel.add(language.getProperty("aucune").toLowerCase());
									
							if (noSettings == false && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31)
							{
								settingsScrollBar.setVisible(true);
							}
							else
								settingsScrollBar.setVisible(false);
							
							if (action)
							{															
								grpSetTimecode.setSize(grpSetTimecode.getSize().width, 17);
								grpSetAudio.setSize(grpSetAudio.getSize().width, 17);								
								grpCrop.setSize(grpCrop.getSize().width, 17);
								grpOverlay.setSize(grpOverlay.getSize().width, 17);
								grpSubtitles.setSize(grpSubtitles.getSize().width, 17);
								grpWatermark.setSize(grpWatermark.getSize().width, 17);					
								grpColorimetry.setSize(grpColorimetry.getSize().width, 17);						
								grpImageAdjustement.setSize(grpImageAdjustement.getSize().width, 17);
								grpCorrections.setSize(grpCorrections.getSize().width, 17);
								grpTransitions.setSize(grpTransitions.getSize().width, 17);						
								grpImageSequence.setSize(grpImageSequence.getSize().width, 17);
								grpImageFilter.setSize(grpImageFilter.getSize().width, 17);
								grpAdvanced.setSize(grpAdvanced.getSize().width, 17);
																
								//Reset Screenshot icon
								if (grpResolution.isVisible())
								{
									if (lblPad.isVisible())
									{
										lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
									}
									else
										lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
							}
							
							btnStart.setEnabled(true);
							btnReset.setVisible(true);
												
							if (language.getProperty("functionConform").equals(function) || language.getProperty("functionSubtitles").equals(function))
							{
								addToList.setText(language.getProperty("filesVideo"));
								
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);							
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);
															
								if (language.getProperty("functionSubtitles").equals(function))
								{				
									btnStart.setEnabled(false);
									
									if (inputDeviceIsRunning)
									{
										JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"), language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
									}
									else if (scanIsRunning)
									{	
										JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"), language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
									}
								}
								else
									VideoPlayer.seekOnKeyFrames = true;
								
							}
							else if (language.getProperty("functionRewrap").equals(function) || language.getProperty("functionCut").equals(function) || language.getProperty("functionMerge").equals(function))
							{				
								VideoPlayer.seekOnKeyFrames = true;
								
								if (language.getProperty("functionCut").equals(function) || language.getProperty("functionMerge").equals(function))
								{
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								}
								else
									addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));
															
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);							
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);	
								
								if (language.getProperty("functionRewrap").equals(function) || language.getProperty("functionCut").equals(function))
								{	
									grpSetTimecode.setVisible(true);
									grpSetTimecode.setLocation(grpSetTimecode.getX(), 30);
									grpSetAudio.setLocation(grpSetAudio.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
																		
									if (caseChangeAudioCodec.isSelected() == false && caseNormalizeAudio.isEnabled())
									{
										caseNormalizeAudio.setSelected(false);
										caseNormalizeAudio.setEnabled(false);
										comboNormalizeAudio.setEnabled(false);
									}
								}
								else
								{
									grpSetTimecode.setVisible(false);
									grpSetAudio.setLocation(grpSetAudio.getX(), 30);							
								}
								
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);							
								if (action)
									grpSetAudio.setSize(312, 17);
								grpAudio.setVisible(false);
								
								if (language.getProperty("functionRewrap").equals(function) || language.getProperty("functionCut").equals(function))	
								{										
									grpAdvanced.removeAll();
									
									grpSubtitles.setVisible(true);
									grpSubtitles.setLocation(grpSubtitles.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									grpAdvanced.setVisible(true);
									grpAdvanced.setLocation(grpAdvanced.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
									casePreserveSubs.setLocation(7, 14);
									grpAdvanced.add(casePreserveSubs);	
									caseCreateTree.setLocation(7, casePreserveSubs.getLocation().y + 17);
									grpAdvanced.add(caseCreateTree);
									comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
									grpAdvanced.add(comboCreateTree);
									casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);	
									
									if (language.getProperty("functionRewrap").equals(function))	
									{
										grpAdvanced.add(caseForcerDAR);
										caseForcerDAR.setLocation(7, casePreserveMetadata.getLocation().y + 17);
										grpAdvanced.add(comboDAR);							
										comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
										grpAdvanced.add(caseRotate);
										caseRotate.setLocation(7, caseForcerDAR.getLocation().y + 17);								
										grpAdvanced.add(comboRotate);
										comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
										caseCreateOPATOM.setLocation(7, caseRotate.getLocation().y + 17);
										caseCreateOPATOM.setEnabled(true);
										grpAdvanced.add(caseCreateOPATOM);						
										lblOPATOM.setLocation(caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y + 3);
										grpAdvanced.add(lblOPATOM);
										lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y);
										lblCreateOPATOM.setEnabled(true);
										grpAdvanced.add(lblCreateOPATOM);	
									}
									
									btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
								}
								else if (language.getProperty("functionMerge").equals(function))
								{
									grpAdvanced.removeAll();
	
									grpAdvanced.setVisible(true);
									grpAdvanced.setLocation(grpAdvanced.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									casePreserveMetadata.setLocation(7, 14);
									grpAdvanced.add(casePreserveMetadata);	
									caseOpenGop.setLocation(7, casePreserveMetadata.getLocation().y + 17);													
									grpAdvanced.add(caseOpenGop);	
									btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
								}
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);	
								
								if (language.getProperty("functionMerge").equals(function))
								{
									caseNormalizeAudio.setSelected(false);
									caseNormalizeAudio.setEnabled(false);
									comboNormalizeAudio.setEnabled(false);
								}
								
								if ((comboAudioCodec.getItemCount() != 11 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 32Float") == false) && action)
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
									}	
									lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "MP3", "AC3", "Opus", "Vorbis", "Dolby Digital Plus", language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(3);	
									caseNormalizeAudio.setEnabled(false);
									caseNormalizeAudio.setSelected(false);
									comboNormalizeAudio.setEnabled(false);
									caseChangeAudioCodec.setSelected(false);								
									comboAudioCodec.setEnabled(false);
									comboAudioBitrate.setEnabled(false);
									lbl48k.setEnabled(false);
								}
								caseChangeAudioCodec.setEnabled(true);	
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(comboAudioBitrate);
								grpSetAudio.add(lblKbs);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								grpSetAudio.add(lblAudio1);
								grpSetAudio.add(comboAudio1);
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
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
	
							} else if (language.getProperty("functionReplaceAudio").equals(function) || language.getProperty("functionNormalization").equals(function)) {
															
								if (language.getProperty("functionReplaceAudio").equals(function))
									addToList.setText(language.getProperty("fileVideoAndAudio"));
								else
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.setLocation(grpSetAudio.getX(), 30);
								
								if (language.getProperty("functionReplaceAudio").equals(function))
								{
									grpSetAudio.setSize(312, 93);
								}
								else
									grpSetAudio.setSize(312, 70);								
								
								grpAudio.setVisible(false);
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseChangeAudioCodec);
								
								if ((comboAudioCodec.getItemCount() != 11 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 32Float") == false) && action)
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
									}		
									lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 32Float", "PCM 32Bits", "PCM 24Bits", "PCM 16Bits", "AAC", "MP3", "AC3", "Opus", "Vorbis", "Dolby Digital Plus", language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(3);
									caseNormalizeAudio.setEnabled(false);
									caseNormalizeAudio.setSelected(false);
									comboNormalizeAudio.setEnabled(false);
									caseChangeAudioCodec.setSelected(false);
									comboAudioCodec.setEnabled(false);
									comboAudioBitrate.setEnabled(false);
									lbl48k.setEnabled(false);
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
								if (language.getProperty("functionReplaceAudio").equals(function))
								{
									grpSetAudio.add(caseKeepSourceTracks);
								}
								grpSetAudio.repaint();								
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);	
								grpSetTimecode.setVisible(false);
								
								if (language.getProperty("functionNormalization").equals(function))
								{
									// grpAdvanced
									grpAdvanced.removeAll();
									grpAdvanced.setVisible(true);
									caseTruePeak.setLocation(7, 14);
									grpAdvanced.add(caseTruePeak);
									comboTruePeak.setLocation(caseTruePeak.getLocation().x + caseTruePeak.getWidth() + 4, caseTruePeak.getLocation().y + 4);
									grpAdvanced.add(comboTruePeak);	
									caseLRA.setLocation(7, caseTruePeak.getLocation().y + 17);
									grpAdvanced.add(caseLRA);	
									comboLRA.setLocation(caseLRA.getLocation().x + caseLRA.getWidth() + 4, caseLRA.getLocation().y + 4);
									grpAdvanced.add(comboLRA);	
									grpAdvanced.setLocation(grpAdvanced.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									caseCreateTree.setLocation(7, caseLRA.getLocation().y + 17);
									grpAdvanced.add(caseCreateTree);
									comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
									grpAdvanced.add(comboCreateTree);
									btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
								}
								else
								{
									grpAdvanced.setVisible(false);
									btnReset.setLocation(btnReset.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								}
								
							} else if ("FrameMD5".equals(function)) {
								
								addToList.setText(language.getProperty("filesVideo"));
								
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);			
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), 30);;
								grpImageFilter.setVisible(false);	
								grpSetTimecode.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setLocation(btnReset.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								
							} else if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function) || "ALAC".equals(function) || "MP3".equals(function) || "AAC".equals(function) || "AC3".equals(function) || "Opus".equals(function) || "Vorbis".equals(function) || "Dolby Digital Plus".equals(function) || "Dolby TrueHD".equals(function)) {
											
								if (action)
								{
									if (comboFonctions.getSelectedItem().toString().equals("MP3") || comboFonctions.getSelectedItem().toString().equals("AAC") || comboFonctions.getSelectedItem().toString().equals("Vorbis"))
									{
										comboFilter.setSelectedIndex(9);
									}
									else if (comboFonctions.getSelectedItem().toString().equals("AC3") || comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus"))
									{
										comboFilter.setSelectedIndex(7);
									}
									else if (comboFonctions.getSelectedItem().toString().equals("Opus"))
									{						
										comboFilter.setSelectedIndex(11);
									}
								}
								
								addToList.setText(language.getProperty("filesVideoOrAudio"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);														
								grpAudio.setVisible(true);
								grpAudio.setLocation(grpAudio.getX(), 30);
								caseNormalizeAudio.setLocation(7, 16);
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								grpAudio.add(caseNormalizeAudio);
								caseNormalizeAudio.setEnabled(true);
								grpAudio.add(comboNormalizeAudio);
								grpAudio.add(lbl48k);
								lbl48k.setLocation(caseSampleRate.getLocation().x + caseSampleRate.getWidth() + 3, caseSampleRate.getLocation().y + 3);	
								if (caseSampleRate.isSelected() == false)
								{
									lbl48k.setEnabled(false);
								}
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);	
								grpTransitions.setVisible(true);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);	
								grpSetTimecode.setVisible(false);									
								grpTransitions.setLocation(grpTransitions.getX(), grpAudio.getSize().height + grpAudio.getLocation().y + 6);
								grpAdvanced.removeAll();
								grpAdvanced.setVisible(true);
								caseCreateTree.setLocation(7, 14);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
								grpAdvanced.add(comboCreateTree);
								caseDRC.setLocation(7, caseCreateTree.getLocation().y + 17);								
								grpAdvanced.add(caseDRC);						
								grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
															
							} else if ("Loudness & True Peak".equals(function)
							|| language.getProperty("functionBlackDetection").equals(function)
							|| language.getProperty("functionOfflineDetection").equals(function)
							|| "VMAF".equals(function)
							|| "FrameMD5".equals(function)
							|| language.getProperty("functionInsert").equals(function))
							{
	
								if (language.getProperty("functionBlackDetection").equals(function)
								|| language.getProperty("functionOfflineDetection").equals(function)
								|| "VMAF".equals(function)
								|| "FrameMD5".equals(function))
								{
									addToList.setText(language.getProperty("filesVideo"));
								}
								else if (language.getProperty("functionInsert").equals(function))
								{
									addToList.setText(language.getProperty("fileMaster"));
								}
								else
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								
								caseDisplay.setEnabled(false);		
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);							
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);	
								grpSetTimecode.setVisible(false);							
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);
								
							} else if ("XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function) || "AVC-Intra 100".equals(function) || ("XAVC").equals(function) || "HAP".equals(function) || "FFV1".equals(function)) {
								
								if (comboFonctions.getSelectedItem().toString().contains("XDCAM") && caseAS10.isSelected())
								{
									final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(new String[] {".mxf"});						
									comboFilter.setModel(model);
								}
								
								//HWaccel	
								/*
								if (action && System.getProperty("os.name").contains("Windows") && "FFV1".equals(function))
								{
									Thread hwaccel = new Thread(new Runnable() {

										@Override
										public void run() {
											
											comboAccel.setEnabled(false);
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
											
											try {												
												
												FFMPEG.hwaccel("-init_hw_device vulkan -f lavfi -i nullsrc -t 1 -c:v ffv1_vulkan -vf format=nv12,hwupload -f null -" + '"');
						
												if (FFMPEG.error == false)
													graphicsAccel.add("Vulkan Video");
												
												int index = comboAccel.getSelectedIndex();
												
												comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
												
												if (index <= comboAccel.getModel().getSize())
													comboAccel.setSelectedIndex(index);	
												
												//load hwaccel value after checking gpu capabilities
												if (Utils.loadEncFile != null && Utils.hwaccel != "")
												{
													comboAccel.setSelectedItem(Utils.hwaccel); 
													Utils.hwaccel = "";
												}
												
											} catch (Exception e) {}
											
											if (comboAccel.getItemCount() > 1)
												comboAccel.setEnabled(true);
												
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										}
										
									});
									hwaccel.start();
																		
								}*/
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 5 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false)
								{
									if (lblAudioMapping.getItemCount() > 1)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] {"Multi"}));										
									}	
									lblAudioMapping.setSelectedItem("Multi");
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));								
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									lbl48k.setEnabled(true);
									
									if ("XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function) || "AVC-Intra 100".equals(function) || ("XAVC").equals(function))
									{
										comboAudioCodec.setSelectedIndex(1);
									}
									else
									{
										comboAudioCodec.setSelectedIndex(0);
									}
								}
														
								caseNormalizeAudio.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								grpSetAudio.add(lblAudio1);
								grpSetAudio.add(comboAudio1);
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
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
								
								if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
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
									&& comboAudio5.getSelectedIndex() == 4
									&& comboAudio6.getSelectedIndex() == 5
									&& comboAudio7.getSelectedIndex() == 6
									&& comboAudio8.getSelectedIndex() == 7 && function.equals("HAP") == false && function.equals("FFV1") == false && caseAS10.isSelected() == false)
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
										&& comboAudio8.getSelectedIndex() == 16 && (function.equals("HAP") || function.equals("FFV1") || caseAS10.isSelected()))
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
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);								
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);								
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);							
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
								
								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
									lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
								else
								{
									lblPad.setVisible(true);
									lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
								}
								
								grpResolution.add(comboResolution);
								
								if (comboFonctions.getSelectedItem().toString().contains("XDCAM") || comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100"))
								{
									if (comboResolution.getItemCount() > 3)
									{
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "1920x1080", "1280x720" }));	
									}
								}
								else
								{
									if (comboResolution.getItemCount() != 24)
									{
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440", "1920x1080",
												"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
								}							
																
								addToList.setText(language.getProperty("filesVideo"));
								if (subtitlesBurn)
									caseDisplay.setEnabled(true);
								else
								{
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpBitrate.setVisible(false);						
								grpAudio.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								if (function.equals("HAP") == false && function.equals("FFV1") == false)
								{
									if (lblAudioMapping.getItemCount() > 1)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] {"Multi"}));
									}
									lblAudioMapping.setSelectedItem("Multi");
									
									grpSetAudio.setSize(312, 146);
								}
								else if (action)
								{
									grpSetAudio.setSize(312, 17);
								}
								grpSetAudio.setLocation(grpSetAudio.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);
								grpSetAudio.repaint();
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(), grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);							
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(), grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);							
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(), grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);							
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);	
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);	
								
								if (comboColorspace.getItemCount() != 3)
								{
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
									
									comboHDRvalue.setVisible(false);
									lblHDR.setVisible(false);
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);							
										
								// Ajout des fonctions avancées
								grpAdvanced.removeAll();
	
								// grpAdvanced
								caseConform.setLocation(7, 14);
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);								
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y - 2);
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
															
								if (function.contains("XDCAM"))
								{															
									casePreserveMetadata.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);	
										
									caseAS10.setText(language.getProperty("caseAS10"));
									caseAS10.setLocation(7, casePreserveMetadata.getLocation().y + 17);
									grpAdvanced.add(caseAS10);
									comboAS10.setLocation(caseAS10.getX() + caseAS10.getWidth() + 4, caseAS10.getLocation().y + 4);
									grpAdvanced.add(comboAS10);	
									
									caseCreateOPATOM.setEnabled(true);
									lblCreateOPATOM.setEnabled(true);
									if ((caseCreateOPATOM.isSelected() || caseCreateTree.isSelected()) && grpDestination.getTabCount() > 2)
									{
										setDestinationTabs(2);		
									}
									
									caseCreateOPATOM.setLocation(7, caseAS10.getLocation().y + 17);
									grpAdvanced.add(caseCreateOPATOM);						
									lblOPATOM.setLocation(caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y + 3);
									grpAdvanced.add(lblOPATOM);
									lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4, caseCreateOPATOM.getLocation().y);
									grpAdvanced.add(lblCreateOPATOM);	
									caseOPATOM.setLocation(7, caseCreateOPATOM.getLocation().y + 17);
									grpAdvanced.add(caseOPATOM);
								}
								else if (function.equals("AVC-Intra 100"))
								{		
									caseAS10.setText(language.getProperty("caseAS10").replace("10" + language.getProperty("colon"), "11").replace("10 format" + language.getProperty("colon"), "11 format"));
									caseAS10.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseAS10);
									casePreserveMetadata.setLocation(7, caseAS10.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);	
								}
								else if (comboFonctions.getSelectedItem().toString().equals("HAP"))
								{
									caseChunks.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseChunks);
									chunksSize.setLocation(caseChunks.getX() + caseChunks.getWidth() + 3, caseChunks.getY() + 3);
									grpAdvanced.add(chunksSize);
									casePreserveMetadata.setLocation(7, caseChunks.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);		
								}
								else
								{
									casePreserveMetadata.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);	
								}						
								
							} else if ("DNxHD".equals(function) || "DNxHR".equals(function) || "Apple ProRes".equals(function) || "QT Animation".equals(function) || ("GoPro CineForm").equals(function) || "Uncompressed".equals(function) ) {
								
								addToList.setText(language.getProperty("filesVideoOrPicture"));			

								//HWaccel
								if (action)
								{
									Thread hwaccel = new Thread(new Runnable() {

										@Override
										public void run() {
											
											comboAccel.setEnabled(false);
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
											
											if ("Apple ProRes".equals(comboFonctions.getSelectedItem().toString()) && System.getProperty("os.name").contains("Mac") && arch.equals("arm64"))
											{
												FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v prores_videotoolbox -s 640x360 -f null -");
						
												if (FFMPEG.error == false)
													graphicsAccel.add("OSX VideoToolbox");	
												
												int index = comboAccel.getSelectedIndex();
												
												comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
												
												if (index <= comboAccel.getModel().getSize())
													comboAccel.setSelectedIndex(index);	
																								
												//load hwaccel value after checking gpu capabilities
												if (Utils.loadEncFile != null && Utils.hwaccel != "")
												{
													comboAccel.setSelectedItem(Utils.hwaccel); 
													Utils.hwaccel = "";
												}												
											}
											
											if (comboAccel.getItemCount() > 1)
												comboAccel.setEnabled(true);
											
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										}
										
									});
									hwaccel.start();									

								}
	
								if (comboFonctions.getSelectedItem().equals("QT Animation") || subtitlesBurn == false)
								{
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
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
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);								
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);								
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);							
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
	
								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
									lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
								else
								{
									lblPad.setVisible(true);
									lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
								}
								
								grpResolution.add(comboResolution);
								
								if (comboFonctions.getSelectedItem().toString().equals("DNxHD"))
								{
									if (comboResolution.getItemCount() > 3)
									{
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "1920x1080", "1280x720" }));		
									}
								}
								else
								{
									if (comboResolution.getItemCount() != 24)
									{
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440", "1920x1080",
												"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
								}
	
								grpBitrate.setVisible(false);
								grpSetTimecode.setVisible(false);							
								grpAudio.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								if (action)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);						
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(), grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);							
								grpAudio.setVisible(false);
								grpImageFilter.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(), grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);							
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(), grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);							
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								
								if ("Uncompressed".equals(function))
								{
									if (comboColorspace.getItemCount() != 4)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits"}));
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								else
								{
									if (comboColorspace.getItemCount() != 3)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 5 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false)
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));										
									}									
									lblAudioMapping.setSelectedItem("Multi");
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									comboAudioCodec.setSelectedIndex(0);
									lbl48k.setEnabled(true);
								}							
								
								caseNormalizeAudio.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}
								
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
								
								if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
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
																			
								// Ajout des fonctions avancées
								grpAdvanced.removeAll();
								
								// grpAdvanced				
								caseConform.setLocation(7, 14);							
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);							
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y - 2);
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
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
								grpAdvanced.add(comboCreateTree);
								
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
								
							} else if ("H.264".equals(function) || "H.265".equals(function) || "H.266".equals(function)) {
								
								addToList.setText(language.getProperty("filesVideoOrPicture"));	
								
								//HWaccel
								if (action)
								{
									Thread hwaccel = new Thread(new Runnable() {

										@Override
										public void run() {

											comboAccel.setEnabled(false);
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));;
											
											String codec = "h264";
											if ("H.265".equals(comboFonctions.getSelectedItem().toString()))
											{
												codec = "hevc";
											}
											else if ("H.266".equals(comboFonctions.getSelectedItem().toString()))
											{
												codec = "vvc";
											}
											
											try {			
												
												//Accélération graphique Windows
												if (System.getProperty("os.name").contains("Windows"))
												{
													
													if (arch.equals("arm64"))
													{
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_mf -s 640x360 -f null -" + '"');
														
														if (FFMPEG.error == false)
															graphicsAccel.add("Media Foundation");
													}
													else
													{													
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -b_ref_mode 0 -s 640x360 -f null -" + '"');
								
														if (FFMPEG.error == false)
															graphicsAccel.add("Nvidia NVENC");
								
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_qsv -s 640x360 -f null -" + '"');
														
														if (FFMPEG.error == false)
															graphicsAccel.add("Intel Quick Sync");
														
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_amf -s 640x360 -f null -" + '"');
								
														if (FFMPEG.error == false)
															graphicsAccel.add("AMD AMF Encoder");
														
														FFMPEG.hwaccel("-init_hw_device vulkan -f lavfi -i nullsrc -t 1 -c:v " + codec + "_vulkan -vf format=nv12,hwupload -f null -" + '"');
								
														if (FFMPEG.error == false)
															graphicsAccel.add("Vulkan Video");
														
														/*
														if (codec == "hevc")
														{
															FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_d3d12va -s 640x360 -f null -" + '"');
									
															if (FFMPEG.error == false)
																graphicsAccel.add("D3D12VA");
														}*/
													}
												}	
												else if (System.getProperty("os.name").contains("Linux"))
												{
													FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_nvenc -s 640x360 -f null -");
							
													if (FFMPEG.error == false)
														graphicsAccel.add("Nvidia NVENC");
															
													FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_vaapi -s 640x360 -f null -");
							
													if (FFMPEG.error == false)
														graphicsAccel.add("VAAPI");								
													
													if (comboFonctions.getSelectedItem().toString().equals("H.264"))
													{
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_v4l2m2m -s 640x360 -f null -");
								
														if (FFMPEG.error == false)
															graphicsAccel.add("V4L2 M2M");	
														
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_omx -s 640x360 -f null -");
								
														if (FFMPEG.error == false)
															graphicsAccel.add("OpenMAX");
													}
												}
												else //Accélération graphique Mac
												{
													FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v " + codec + "_videotoolbox -s 640x360 -f null -");
													
													if (FFMPEG.error == false)								
														graphicsAccel.add("OSX VideoToolbox");										
												}
												
												int index = comboAccel.getSelectedIndex();
																								
												comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
												
												if (index <= comboAccel.getModel().getSize())
													comboAccel.setSelectedIndex(index);											
												
												//load hwaccel value after checking gpu capabilities
												if (Utils.loadEncFile != null && Utils.hwaccel != "")
												{
													comboAccel.setSelectedItem(Utils.hwaccel); 
													Utils.hwaccel = "";
												}
												
											} catch (Exception e) {}
	
											if (comboAccel.getItemCount() > 1)
												comboAccel.setEnabled(true);
											
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										}
										
									});
									hwaccel.start();									
								}
								
								if (comboFonctions.getSelectedItem().equals("H.266") || subtitlesBurn == false)
								{
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
								else
									caseDisplay.setEnabled(true);
	
								if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
								{
									caseForcerEntrelacement.setEnabled(true);								
									lblVBR.setVisible(true);
									
									if (caseQMax.isSelected() == false)
										caseForcePreset.setEnabled(true);
									caseForceTune.setEnabled(true);
									caseForceOutput.setEnabled(true);
									
									if (lblVBR.getText().equals("CQ"))
									{
										bitrateSize.setText("-");
										lblVideoBitrate.setText(language.getProperty("lblValue"));
										lblKbsH264.setVisible(false);
										h264lines.setVisible(false);					
										case2pass.setSelected(false);
										case2pass.setEnabled(false);
									}
								}
								else
								{
									caseForcerEntrelacement.setSelected(false);
									caseForcerEntrelacement.setEnabled(false);									
									caseForceTune.setSelected(false);
									caseForceTune.setEnabled(false);
									comboForceTune.setEnabled(false);
									case2pass.setSelected(false);
									case2pass.setEnabled(false);
								}
									
								if ("H.264".equals(function))
								{															
	
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboForceProfile.getModel().getSize() != 3 || comboForceProfile.getModel().getElementAt(0).toString().equals("base") == false)
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
								else if ("H.265".equals(function))
								{
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboForceProfile.getModel().getSize() != 1)
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
								if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
								{
									if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync"))
									{
										if (comboForcePreset.getModel().getSize() != 7)
										{
											comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow"}));
											comboForcePreset.setSelectedIndex(3);
										}
									}
					    			else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals("Vulkan Video"))
					    			{
					    				caseForcePreset.setSelected(false);
				    					caseForcePreset.setEnabled(false);
				    					comboForcePreset.setEnabled(false);
					    			}	
								}
				    			else 
				    			{
				    				
									if (("H.264".equals(function) || "H.265".equals(function)) && comboForcePreset.getModel().getSize() != 10)
				    				{
					    				comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",  "medium",  "slow", "slower", "veryslow", "placebo"}));
					    				comboForcePreset.setSelectedIndex(5);
				    				} 
									else if ("H.266".equals(function) && comboForcePreset.getModel().getSize() != 5)
									{				    
										comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "faster", "fast",  "medium",  "slow", "slower"}));
					    				comboForcePreset.setSelectedIndex(2);
									}
				    			}
	
								lblNiveaux.setVisible(true);
								
								// Ajout partie résolution
								grpResolution.removeAll();
								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);								
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);								
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);							
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
								
								if (comboResolution.getItemCount() != 24)
								{
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440", "1920x1080",
											"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
								}
								
								grpBitrate.setVisible(true);
								grpBitrate.setBounds(grpBitrate.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312, 208);
								lblMaximumBitrate.setVisible(true);
								maximumBitrate.setVisible(true);
								lblMaximumKbs.setVisible(true);		
								lblAudioBitrate.setLocation(lblBitrateTimecode.getX(), lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
								debitAudio.setLocation(debitVideo.getX(), maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
								lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
								lblSize.setLocation(lblBitrateTimecode.getX(), lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
								bitrateSize.setLocation(debitVideo.getX(), debitAudio.getY() + debitAudio.getHeight() + 5);
								lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
								lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
								case2pass.setLocation(7, grpBitrate.getHeight() - 32);
								caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());
								
								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
									lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
								else
								{
									lblPad.setVisible(true);
									lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
								}
								
								grpSetTimecode.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								if (action)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(), grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 13 || comboAudioCodec.getModel().getElementAt(0).equals("AAC") == false)
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));										
									}	
									lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AAC", "MP3", "AC3", "Opus", "FLAC", "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", "ALAC 16Bits", "ALAC 24Bits", "Dolby Digital Plus", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									lbl48k.setEnabled(true);
									
									debitAudio.setModel(comboAudioBitrate.getModel());
									debitAudio.setSelectedIndex(10);
								}
								
								caseNormalizeAudio.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);							
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}
								
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
								
								if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
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
	
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(), grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);							
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(), grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);							
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(), grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);							
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);
								
								if ("H.264".equals(function))
								{
									if (comboColorspace.getItemCount() != 4)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits"}));
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								else if ("H.265".equals(function))
								{
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
									{
										if (comboColorspace.getItemCount() != 6)
										{
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));							
		
											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
									else
									{
										if (comboColorspace.getItemCount() != 8)
										{
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits", "Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));
											
											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
								}
								else if ("H.266".equals(function))
								{
									if (comboColorspace.getItemAt(0).equals("Rec. 709 10bits") == false)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits"}));
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
								
								// CalculH264
								if (liste.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();
								
								// Qualité Max
								if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
								{
									caseQMax.setEnabled(false);
								}
								else
									caseQMax.setEnabled(true);
								
								// Ajout des fonctions avancées
								grpAdvanced.removeAll();
	
								// grpAdvanced				
								caseForcerDesentrelacement.setLocation(7, 14);
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);								
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);
								
								if ("H.266".equals(function))
								{					
									caseForcePreset.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcePreset);									
									comboForcePreset.setLocation(caseForcePreset.getLocation().x + caseForcePreset.getWidth() + 4, caseForcePreset.getLocation().y + 4);
									grpAdvanced.add(comboForcePreset);	
									
									caseGOP.setLocation(7, caseForcePreset.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
								}
								else
								{
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
		
									if (comboFonctions.getSelectedItem().equals("H.265"))
									{									
										caseAlpha.setLocation(7, caseForceTune.getY() + 17);
										grpAdvanced.add(caseAlpha);	
										
										if (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
										{
											caseAlpha.setEnabled(true);
										}
										else
											caseAlpha.setEnabled(false);
										
										caseFastStart.setLocation(7, caseAlpha.getY() + 17);
									}
									else
										caseFastStart.setLocation(7, caseForceTune.getY() + 17);							
										
									grpAdvanced.add(caseFastStart);
									
									caseGOP.setLocation(7, caseFastStart.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
								}								
								
								gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
								grpAdvanced.add(gopSize);
								
								if ("H.264".equals(function))
								{
									caseCABAC.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseCABAC);
									
									caseDecimate.setLocation(7, caseCABAC.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);
								}
								else
								{
									caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);
								}
										
								caseConform.setLocation(7, caseDecimate.getLocation().y + 17);
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);							
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);							
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);
								caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
								grpAdvanced.add(comboCreateTree);
								casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);
								casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
								grpAdvanced.add(casePreserveSubs);							
								
							} else if ("WMV".equals(function) || "MPEG-1".equals(function) || "MPEG-2".equals(function) || "VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function) || "Theora".equals(function)
									|| "MJPEG".equals(function) || "Xvid".equals(function)) {
															
								addToList.setText(language.getProperty("filesVideoOrPicture"));	
								
								//HWaccel		
								if (action)
								{
									Thread hwaccel = new Thread(new Runnable() {

										@Override
										public void run() {
											
											comboAccel.setEnabled(false);
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
											
											try {
												
												if ("VP9".equals(comboFonctions.getSelectedItem().toString()))
												{
													if (System.getProperty("os.name").contains("Windows"))
													{
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_qsv -s 640x360 -f null -" + '"');
								
														if (FFMPEG.error == false)
															graphicsAccel.add("Intel Quick Sync");
													}
													else if (System.getProperty("os.name").contains("Linux"))
													{
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v vp9_vaapi -s 640x360 -f null -");
								
														if (FFMPEG.error == false)
															graphicsAccel.add("VAAPI");
													}
												}
												else if ("AV1".equals(comboFonctions.getSelectedItem().toString()))
												{
													if (System.getProperty("os.name").contains("Windows"))
													{
														if (arch.equals("arm64"))
														{
															FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_mf -s 640x360 -f null -" + '"');
															
															if (FFMPEG.error == false)
																graphicsAccel.add("Media Foundation");
														}
														else
														{	
															FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_nvenc -s 640x360 -f null -" + '"');
									
															if (FFMPEG.error == false)
																graphicsAccel.add("Nvidia NVENC");
									
															FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_qsv -s 640x360 -f null -" + '"');
									
															if (FFMPEG.error == false)
																graphicsAccel.add("Intel Quick Sync");
					
															FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_amf -s 640x360 -f null -" + '"');
									
															if (FFMPEG.error == false)
																graphicsAccel.add("AMD AMF Encoder");
														}
													}
													else if (System.getProperty("os.name").contains("Mac"))
													{
														FFMPEG.hwaccel("-f lavfi -i nullsrc -t 1 -c:v av1_videotoolbox -s 640x360 -f null -");
								
														if (FFMPEG.error == false)
															graphicsAccel.add("OSX VideoToolbox");	
													}
												}
												
												int index = comboAccel.getSelectedIndex();
												
												comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
												
												if (index <= comboAccel.getModel().getSize())
													comboAccel.setSelectedIndex(index);		
												
												//load hwaccel value after checking gpu capabilities
												if (Utils.loadEncFile != null && Utils.hwaccel != "")
												{
													comboAccel.setSelectedItem(Utils.hwaccel); 
													Utils.hwaccel = "";
												}
												
											} catch (Exception e) {}
											
											if (comboAccel.getItemCount() > 1)
												comboAccel.setEnabled(true);
											
											comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										}
										
									});
									hwaccel.start();
																		
								}
								
								if (subtitlesBurn)
								{
									caseDisplay.setEnabled(true);
								}
								else
								{
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
								
								case2pass.setEnabled(true);
								lblNiveaux.setVisible(true);
								grpColorimetry.setVisible(false);
								
								// Ajout partie résolution
								grpResolution.removeAll();
								
								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);								
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);								
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);							
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
								
								if (comboResolution.getItemCount() != 24)
								{
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440", "1920x1080",
											"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
								}
								
								grpBitrate.setVisible(true);								
								if ("AV1".equals(function) && lblVBR.getText().equals("CQ"))
								{
									grpBitrate.setBounds(grpBitrate.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312, 208);
									lblMaximumBitrate.setVisible(true);
									maximumBitrate.setVisible(true);
									lblMaximumKbs.setVisible(true);
									lblAudioBitrate.setLocation(lblBitrateTimecode.getX(), lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(), maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
								}
								else
								{
									grpBitrate.setBounds(grpBitrate.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312, 182);
									lblMaximumBitrate.setVisible(false);
									maximumBitrate.setVisible(false);
									lblMaximumKbs.setVisible(false);			
									lblAudioBitrate.setLocation(lblBitrateTimecode.getX(), lblVideoBitrate.getY() + lblVideoBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(), debitVideo.getY() + debitVideo.getHeight() + 5);
								}	
								lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
								lblSize.setLocation(lblBitrateTimecode.getX(), lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
								bitrateSize.setLocation(debitVideo.getX(), debitAudio.getY() + debitAudio.getHeight() + 5);
								lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
								lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
								case2pass.setLocation(7, grpBitrate.getHeight() - 32);
								caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());
								
								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
									lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
								else
								{
									lblPad.setVisible(true);
									lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
								}
								
								if ("VP9".equals(function) || "AV1".equals(function))
								{			
									lblVBR.setVisible(true);
									
									if (lblVBR.getText().equals("CQ"))
									{
										bitrateSize.setText("-");
										lblVideoBitrate.setText(language.getProperty("lblValue"));
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
										debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(), language.getProperty("lblGood").toLowerCase(), "auto" }));
										debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
										lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
										lblKbsH264.setVisible(true);
										h264lines.setVisible(true);
										FFPROBE.setLength();
									}
									lblVBR.setText("VBR");
								}
								
								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
								}
								else
								{
									lblPad.setVisible(true);
								}
								grpSetTimecode.setVisible(false);							
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);							
								if (action)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(), grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);
								
								if (comboAudioCodec.getItemCount() != 5 && "MJPEG".equals(function))
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
									}
									lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									comboAudioCodec.setSelectedIndex(0);								
									debitAudio.setModel(comboAudioBitrate.getModel());
									debitAudio.setSelectedIndex(0);
								}
								else if ("MJPEG".equals(function) == false)
								{
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
									}
									lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									
									if (comboAudioCodec.getModel().getElementAt(0).equals("WMA") == false && "WMV".equals(function))
									{
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);
									}
									else if (comboAudioCodec.getModel().getElementAt(0).equals("MP2") == false && ("MPEG-1".equals(function) || "MPEG-2".equals(function)))
									{
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);
									}
									else if (comboAudioCodec.getModel().getElementAt(0).equals("Opus") == false && ("VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function)))
									{
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus", "AAC", "Vorbis", "FLAC", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);
									}
									else if (comboAudioCodec.getModel().getElementAt(0).equals("Vorbis") == false && "Theora".equals(function))
									{
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Vorbis", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);
									}
									else if (comboAudioCodec.getModel().getElementAt(0).equals("MP3") == false && "Xvid".equals(function))
									{
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);
									}
								}
								
								caseNormalizeAudio.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);	
								caseChangeAudioCodec.setSelected(true);
								comboAudioCodec.setEnabled(true);	
								lbl48k.setEnabled(true);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}
								
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
								
								if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
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
															
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(), grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);							
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(), grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);							
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(), grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(), grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);							
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);
								
								if ("AV1".equals(function))
								{
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
									{
										if (comboColorspace.getItemCount() != 6)
										{
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));							
		
											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
									else
									{									
										if (comboColorspace.getItemCount() != 8)
										{
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits", "Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits", "Rec. 2020 HLG 12bits" }));							
		
											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
								}
								else if ("MPEG-2".equals(function))
								{
									if (comboColorspace.getItemCount() != 6)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 709 4:2:2", "Rec. 2020 PQ", "Rec. 2020 PQ 4:2:2", "Rec. 2020 HLG", "Rec. 2020 HLG 4:2:2"}));	
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								else
								{
									if (comboColorspace.getItemCount() != 3)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
	
										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	
								
								// CalculH264
								if (liste.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();
								// Qualité Max
								if (comboFonctions.getSelectedItem().equals("Theora") || comboFonctions.getSelectedItem().equals("MJPEG"))
									caseQMax.setEnabled(false);
								else
									caseQMax.setEnabled(true);
	
								
								// Ajout des fonctions avancées
								grpAdvanced.removeAll();
	
								// grpAdvanced	
								if (System.getProperty("os.name").contains("Windows") && ("VP9".equals(function) || "AV1".equals(function)))
								{
									lblVBR.setVisible(true);
									
									if (lblVBR.getText().equals("CQ") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
									{
										case2pass.setSelected(false);
										case2pass.setEnabled(false);
									}
								}
	
								caseForcerDesentrelacement.setLocation(7, 14);							
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(caseForcerDesentrelacement.getLocation().x + caseForcerDesentrelacement.getWidth() + 4, caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);								
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4, lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);
																							
								if ("VP8".equals(function) || "VP9".equals(function))
								{																													
									if (caseQMax.isSelected() == false)
									{
										caseForceQuality.setEnabled(true);
										caseForcePreset.setEnabled(true);
									}
									
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
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
									comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "film", "animation", "grain", "stillimage", "fastdecode", "zerolatency", "psnr", "ssim" }));
									if (caseColorspace.isSelected() == false || caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().equals("Rec. 709"))
									{
										caseAlpha.setEnabled(true);			
									}
									caseAlpha.setLocation(7, caseForceTune.getY() + 17);
									grpAdvanced.add(caseAlpha);								
									caseGOP.setLocation(7, caseAlpha.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);								
									caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);	
								}
								else if ("AV1".equals(function))
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
									caseForceTune.setLocation(7, caseForceSpeed.getLocation().y + 17);
									grpAdvanced.add(caseForceTune);
									comboForceTune.setLocation(caseForceTune.getLocation().x + caseForceTune.getWidth() + 4, caseForceTune.getLocation().y + 4);
									grpAdvanced.add(comboForceTune);									
									if (comboForceTune.getModel().getSize() != 2)
									{
										comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "visual quality", "psnr"}));
										comboForceTune.setSelectedIndex(0);
									}								
									caseFastStart.setLocation(7, caseForceTune.getLocation().y + 17);
									grpAdvanced.add(caseFastStart);								
									caseFastDecode.setLocation(7, caseFastStart.getLocation().y + 17);
									grpAdvanced.add(caseFastDecode);
									caseGOP.setLocation(7, caseFastDecode.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth() + 3, caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);									
									caseFilmGrain.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseFilmGrain);
									comboFilmGrain.setLocation(caseFilmGrain.getX() + caseFilmGrain.getWidth() + 3, caseFilmGrain.getY() + 3);
									grpAdvanced.add(comboFilmGrain);
									caseFilmGrainDenoise.setLocation(7, caseFilmGrain.getLocation().y + 17);
									grpAdvanced.add(caseFilmGrainDenoise);	
									comboFilmGrainDenoise.setLocation(caseFilmGrainDenoise.getX() + caseFilmGrainDenoise.getWidth() + 3, caseFilmGrainDenoise.getY() + 3);
									grpAdvanced.add(comboFilmGrainDenoise);									
									caseDecimate.setLocation(7, caseFilmGrainDenoise.getLocation().y + 17);
								}	
								else if ("MPEG-1".equals(function) || "MPEG-2".equals(function))
								{
									caseGOP.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
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
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4, comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);							
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4, comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);
								caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
								grpAdvanced.add(comboCreateTree);
								casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);
								casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
								grpAdvanced.add(casePreserveSubs);	
								
							} else if ("DV PAL".equals(function)) {
								
								addToList.setText(language.getProperty("filesVideo"));
								caseDisplay.setEnabled(true);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);							
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);
								
							} else if ("DVD".equals(function) || "Blu-ray".equals(function)) {
															
								addToList.setText(language.getProperty("filesVideo"));
								
								if (comboFonctions.getSelectedItem().equals("DVD") || subtitlesBurn == false)
								{
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
								else
									caseDisplay.setEnabled(true);
															
								caseForcerProgressif.setEnabled(true);
								caseForcerEntrelacement.setEnabled(true);
								grpImageSequence.setVisible(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								
								if ("Blu-ray".equals(function))
								{
									if (action)
									{
										if (comboFilter.getSelectedIndex() == 0) //H.264
										{
											debitVideo.setSelectedItem(38000);
										}
										else //H.265
											debitVideo.setSelectedItem(50000);
									}
									
									// Ajout partie résolution
									grpResolution.removeAll();
									
									grpResolution.setVisible(true);	
									grpResolution.setLocation(grpResolution.getX(), 30);
									grpResolution.add(lblImageSize);
									grpResolution.add(comboResolution);
									grpResolution.add(lblPad);
									grpResolution.add(lblScreenshot);
									grpResolution.add(btnNoUpscale);
									btnNoUpscale.setLocation(7, 47);																		
									grpResolution.add(caseRotate);
									caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);								
									grpResolution.add(comboRotate);
									comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
									grpResolution.add(caseMiror);
									caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
									grpResolution.add(caseForcerDAR);
									caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
									grpResolution.add(comboDAR);							
									comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4, caseForcerDAR.getLocation().y + 3);
																		
									if (comboResolution.getItemCount() != 24)
									{
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440", "1920x1080",
												"1440x1080", "1280x720", "1024x768", "1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
									
									grpBitrate.setVisible(true);
									grpBitrate.setBounds(grpBitrate.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312, 208);
									lblMaximumBitrate.setVisible(true);
									maximumBitrate.setVisible(true);
									lblMaximumKbs.setVisible(true);	
									lblAudioBitrate.setLocation(lblBitrateTimecode.getX(), lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(), maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
									lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
									lblSize.setLocation(lblBitrateTimecode.getX(), lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
									bitrateSize.setLocation(debitVideo.getX(), debitAudio.getY() + debitAudio.getHeight() + 5);
									lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
									lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
									case2pass.setLocation(7, grpBitrate.getHeight() - 32);
									caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());
									
									if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
									{
										lblPad.setVisible(false);
										lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
									}
									else
									{
										lblPad.setVisible(true);
										lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
									}
								}								
								
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);								
								if (action)
									grpSetAudio.setSize(312, 17);
								
								if ("Blu-ray".equals(function))
								{
									grpSetAudio.setLocation(grpSetAudio.getX(), grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);
								}
								else
									grpSetAudio.setLocation(grpSetAudio.getX(), 30);
								
								//grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseChangeAudioCodec);
								
								caseChangeAudioCodec.setSelected(true);
								comboAudioCodec.setEnabled(true);
								lbl48k.setEnabled(true);
												
								if ("Blu-ray".equals(function))
								{
									if (comboAudioCodec.getItemCount() != 4 || comboAudioCodec.getItemAt(0).equals("AC3") == false)
									{
										if (lblAudioMapping.getItemCount() != 4)
										{
											lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
										}
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
										
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", "Dolby Digital Plus", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);						
										debitAudio.setModel(comboAudioBitrate.getModel());								
										debitAudio.setSelectedIndex(5);
									}								
								}
								else
								{
									if (comboAudioCodec.getItemCount() != 3 || comboAudioCodec.getItemAt(0).equals("AC3") == false)
									{
										if (lblAudioMapping.getItemCount() != 4)
										{
											lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
										}
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
										
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {"AC3", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
										comboAudioCodec.setSelectedIndex(0);						
										debitAudio.setModel(comboAudioBitrate.getModel());								
										debitAudio.setSelectedIndex(5);
									}
								}
								
								caseNormalizeAudio.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
			
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
								
								lblAudio1.setLocation(12, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false)
								{
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}
								
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
								
								if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
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
																						
								grpAudio.setVisible(false);							
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpSubtitles.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpWatermark.setVisible(false);					
								grpColorimetry.setVisible(false);						
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(false);							
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);	;
	
								// CalculH264
								if (liste.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();
								// Qualité Max
								caseQMax.setEnabled(true);
								
								
								// Ajout des fonctions avancées
								grpAdvanced.removeAll();
	
								// grpAdvanced
								caseForcerProgressif.setLocation(7, 14);													
								grpAdvanced.add(caseForcerProgressif);
								
								if ("Blu-ray".equals(function))
								{
									caseForcerEntrelacement.setLocation(caseForcerProgressif.getX(), caseForcerProgressif.getY() + 17);
									grpAdvanced.add(caseForcerEntrelacement);
								}
								
							} else if (language.getProperty("functionPicture").equals(function) || "JPEG".equals(function) || "JPEG XL".equals(function)) {
								
								addToList.setText(language.getProperty("filesVideoOrPicture"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpImageSequence.setVisible(false);	
								
								// Ajout partie résolution
								grpResolution.removeAll();
								
								grpResolution.setVisible(true);		
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, 47);								
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(iconTVInterpret);
								grpResolution.add(lblScreenshot);
								lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								
								grpResolution.add(comboResolution);
								
								if (comboResolution.getItemCount() != 31)
								{
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"), "AI real-life 4x", "AI real-life 2x", "AI animation 4x", "AI animation 2x", "1:2", "1:4", "1:8", "1:16",
											"3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720",
											"4096x2160", "3840x2160", "2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000",
											"854x480", "720x576", "640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
								}
								
								if (comboFilter.getSelectedItem().toString().equals(".png"))
								{
									if (comboImageOption.getItemAt(0).equals("0%") == false)
									{
										comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "0%","5%","10%","15%","20%","25%","30%","35%","40%","45%","50%","55%","60%","65","70%","75%","80%","85%","90%","95%","100%" }));	
									}
									comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
									comboImageOption.setSize(50, 16);
									comboImageOption.setEditable(false);
									lblImageQuality.setText("Comp.:");
									grpResolution.add(lblImageQuality);											
									grpResolution.add(comboImageOption);
									lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
									comboImageOption.repaint();
								}
								else if (comboFilter.getSelectedItem().toString().equals(".webp") || comboFilter.getSelectedItem().toString().equals(".avif")) // Ajout de la quality pour l'extension .webp & .avif & .tif
								{							
									if (comboImageOption.getItemAt(0).equals("100%") == false)
									{
										comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%" }));	
									}
									comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(), lblImageQuality.getLocation().y);
									comboImageOption.setSize(50, 16);
									comboImageOption.setEditable(false);
									grpResolution.add(lblImageQuality);											
									grpResolution.add(comboImageOption);
									lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
									comboImageOption.repaint();
								}
								else if (comboFilter.getSelectedItem().toString().equals(".tif"))
								{
									if (comboImageOption.getItemAt(0).equals("packbits") == false)
									{
										comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "packbits", "raw", "lzw", "deflate" }));
									}
									comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6, lblImageQuality.getLocation().y);
									comboImageOption.setSize(90, 16);
									comboImageOption.setEditable(false);
									grpResolution.remove(lblImageQuality);
									grpResolution.add(comboImageOption);
									lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
									comboImageOption.repaint();
								}
								else if (comboFilter.getSelectedItem().toString().equals(".gif") || comboFilter.getSelectedItem().toString().equals(".apng"))
								{
									if (comboImageOption.getItemAt(0).equals("15 " + Shutter.language.getProperty("fps")) == false)
									{
										String fps[] = new String[17];
										int a = 0;
										for (int f = 15 ; f < 24 ; f++)
										{
											fps[a] = f + " " + Shutter.language.getProperty("fps");
											a++; 
										}
	
										fps[a] = "23,98 " + Shutter.language.getProperty("fps");
										fps[a+1] = "24 " + Shutter.language.getProperty("fps");
										fps[a+2] = "25 " + Shutter.language.getProperty("fps");
										fps[a+3] = "29,97 " + Shutter.language.getProperty("fps");
										fps[a+4] = "30 " + Shutter.language.getProperty("fps");
										fps[a+5] = "50 " + Shutter.language.getProperty("fps");
										fps[a+6] = "59,94 " + Shutter.language.getProperty("fps");
										fps[a+7] = "60 " + Shutter.language.getProperty("fps");
										
										comboImageOption.setModel(new DefaultComboBoxModel<String>(fps));
									}								
									comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6, lblImageQuality.getLocation().y);
									comboImageOption.setSize(90, 16);
									comboImageOption.setEditable(false);
									grpResolution.remove(lblImageQuality);
									grpResolution.add(comboImageOption);
									lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
									comboImageOption.repaint();
								}								
								else
								{
									grpResolution.remove(lblImageQuality);
									grpResolution.remove(comboImageOption);
									lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);									
								}
								
								if (comboResolution.getSelectedItem().toString().contains("AI"))
								{	
									if (VideoPlayer.preview != null)
										VideoPlayer.preview = null;
									
									VideoPlayer.loadImage(true);
								}
								
								grpResolution.repaint();							
								
								// lblInterpretation location
								lblInterpretation.setLocation(30, caseCreateSequence.getLocation().y + caseCreateSequence.getHeight());
								grpResolution.add(lblInterpretation);							
								comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4, lblInterpretation.getLocation().y);
								grpResolution.add(comboInterpret);							
								lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblInterpretation.getLocation().y - 1);
								grpResolution.add(lblIsInterpret);	
								if (getLanguage.equals(Locale.of("ru").getDisplayLanguage()) || getLanguage.equals(Locale.of("uk").getDisplayLanguage()) || getLanguage.equals(Locale.of("vi").getDisplayLanguage()))
								{
									iconTVInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5, lblIsInterpret.getY() + 1);
								}
								else
									iconTVInterpret.setLocation(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY() + 1);
								grpResolution.add(iconTVInterpret);
								
								caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(), caseCreateSequence.getPreferredSize().width, 23);							
								grpResolution.add(caseCreateSequence);
								
								grpBitrate.setVisible(false);
	
								if (comboColorspace.getItemCount() != 3)
								{
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {"Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG"}));	
	
									comboHDRvalue.setVisible(false);
									lblHDR.setVisible(false);
								}
								
								grpSetAudio.setVisible(false);							
								grpAudio.setVisible(false);
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(), grpResolution.getSize().height + grpResolution.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(), grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(), grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);							
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(), grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);							
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(), grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(), grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(false);						
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								
								grpImageFilter.setVisible(true);
								grpImageFilter.setLocation(grpImageFilter.getX(),grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpSetTimecode.setVisible(false);

								grpAdvanced.removeAll();
								grpAdvanced.setVisible(true);
								caseCreateTree.setLocation(7, 14);
								grpAdvanced.add(caseCreateTree);			
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 3);
								grpAdvanced.add(comboCreateTree);
								grpAdvanced.setLocation(grpAdvanced.getX(), grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);
								
								btnReset.setLocation(btnReset.getX(), grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
								
							} else {
								
								if (language.getProperty("functionExtract").equals(function)) 
								{
									addToList.setText(language.getProperty("filesVideo"));
								}
								else if (language.getProperty("functionMerge").equals(function))
								{
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								}
								else if (language.getProperty("functionSubtitles").equals(function))
								{
									addToList.setText(language.getProperty("fileVideo"));
								}
								else if (language.getProperty("functionSceneDetection").equals(function))
								{
									addToList.setText(language.getProperty("fileVideo"));
								}
								else if (language.getProperty("itemMyFunctions").equals(function))
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
								caseDisplay.setSelected(false);
							}
																					
							grpAdvanced.repaint();
							
							if (action && frame.getWidth() > 332) {
								
								int i2 = frame.getWidth();
								
								do {
									long startTime = System.nanoTime();
									
									changeGroupes = true;
									if (Settings.btnDisableAnimations.isSelected())
										i2 = frame.getWidth() - 312 - 12;
									else
										i2 -= 4;
									
									grpResolution.setLocation(i2, grpResolution.getLocation().y);
									grpBitrate.setLocation(i2, grpBitrate.getLocation().y);								
									grpSetAudio.setLocation(i2, grpSetAudio.getLocation().y);
									grpAudio.setLocation(i2, grpAudio.getLocation().y);								
									grpCrop.setLocation(i2, grpCrop.getLocation().y);								
									grpOverlay.setLocation(i2, grpOverlay.getLocation().y);
									grpSubtitles.setLocation(i2, grpSubtitles.getLocation().y);
									grpWatermark.setLocation(i2, grpWatermark.getLocation().y);
									grpColorimetry.setLocation(i2, grpColorimetry.getLocation().y);								
									grpImageAdjustement.setLocation(i2, grpImageAdjustement.getLocation().y);								
									grpCorrections.setLocation(i2, grpCorrections.getLocation().y);	
									grpTransitions.setLocation(i2, grpTransitions.getLocation().y);	
									grpImageSequence.setLocation(i2, grpImageSequence.getLocation().y);
									grpImageFilter.setLocation(i2, grpImageFilter.getLocation().y);
									grpSetTimecode.setLocation(i2, grpSetTimecode.getLocation().y);
									grpAdvanced.setLocation(i2, grpAdvanced.getLocation().y);
									btnReset.setLocation((i2 + 2), btnReset.getLocation().y);
	
									//Animate size
									animateSections(startTime, true);	
									
								} while (i2 > frame.getWidth() - 312 - 12);
								
								changeGroupes = false;
								changeSections(false); // une fois l'action terminé on vérifie que les groupes correspondent
							}
	
							//Right_to_left
							if (getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
							{
								//destinationStream
								for (Component c : destinationStream.getComponents())
								{		
									if (c instanceof JCheckBox)
									{
										c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
									}
								}	
								
								//grpAdvanced
								for (Component c : grpAdvanced.getComponents())
								{		
									if (c instanceof JCheckBox)
									{
										c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
									}
								}
							}
							
							// Important
							grpResolution.repaint();
							topPanel.repaint();
							statusBar.repaint();
							
							if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && action) 
							{
								VideoPlayer.videoPath = null;
					    		VideoPlayer.setMedia();
							}
																			
						} catch (Exception e1) {}
					}
				}
			});
			changeSize.start();
		}
	}

	public static void extendSections(Component grpPanel, int maxSize) {
						
		if (extendSectionsIsRunning == false) //Avoid double-click bug
		{	
			Thread extend = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						
						extendSectionsIsRunning = true;
						
						//Getting the first & last grpPanel
						int i = 0;
						int l = 1;
						Component firstComponent = null;
						Component lastComponent = grpPanel;
						
						for (Component a : frame.getContentPane().getComponents())
						{
							if (a instanceof JPanel && a.isVisible() && a.getX() == grpPanel.getX())
							{
								if (firstComponent == null)
								{
									firstComponent = a;
								}

								if (a.getY() > i)
								{
									i = a.getY();
									lastComponent = a;
								}
							}
						}

						i = 17;
						
						if (grpPanel.getHeight() < maxSize)
						{									
							//Used for grpAudio
							if (grpPanel.getHeight() > 17)
							{
								i = grpPanel.getHeight();
							}
							
							do {
								
								long startTime = System.nanoTime();
				
								if (Settings.btnDisableAnimations.isSelected())
								{	
									l = maxSize - grpPanel.getHeight();
																										
									for (Component c : frame.getContentPane().getComponents())
									{
										if (c instanceof JPanel && c.isVisible() && c.getX() == grpPanel.getX() && c.getY() > grpPanel.getY() + grpPanel.getHeight())
										{
											c.setLocation(c.getLocation().x, c.getY() + l);
										}
									}
									
									grpPanel.setSize(312, maxSize);									
									btnReset.setLocation(btnReset.getX(), lastComponent.getY() + lastComponent.getHeight() + 6);
									
									if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31)
									{
										settingsScrollBar.setVisible(true);
									}
									else
										settingsScrollBar.setVisible(false);

									break;
								}
								else
									i ++;	
																	
								for (Component c : frame.getContentPane().getComponents())
								{
									if (c instanceof JPanel && c.isVisible() && c.getX() == grpPanel.getX() && c.getY() > grpPanel.getY() + grpPanel.getHeight())
									{
										c.setLocation(c.getLocation().x, c.getY() + l);
									}
								}
								
								grpPanel.setSize(312, i);
								
								if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31)
								{
									settingsScrollBar.setVisible(true);
									
									if (grpPanel.getName() == null || grpPanel.getName().equals("grpImageAdjustement") == false)
									{
										for (Component c2 : frame.getContentPane().getComponents())
										{
											if (c2 instanceof JPanel && c2.isVisible() && c2.getX() == grpPanel.getX())
											{
												c2.setLocation(c2.getLocation().x, c2.getY() - l);
											}
										}
									}									
								}
								else
									settingsScrollBar.setVisible(false);
		
								btnReset.setLocation(btnReset.getX(), lastComponent.getY() + lastComponent.getHeight() + 6);
																
								//Animate size
								animateSections(startTime, false);	
																		
							} while (i < maxSize);
						}
						else
						{											
							int minSize = 17;
							
							//Used for grpAudio
							if (grpPanel.getHeight() > maxSize)
							{
								minSize = maxSize;
								i = grpPanel.getHeight();
							}
							else
								i = maxSize;
														
							do {
								
								long startTime = System.nanoTime();
				
								if (Settings.btnDisableAnimations.isSelected())
								{									
									for (Component c : frame.getContentPane().getComponents())
									{
										if (c instanceof JPanel && c.isVisible() && c.getX() == grpPanel.getX() && c.getY() > grpPanel.getY() + grpPanel.getHeight())
										{			
											//Used for grpAudio
											if (grpPanel.getHeight() > maxSize)
											{
												c.setLocation(c.getLocation().x, c.getY() - (i - minSize));
											}
											else
												c.setLocation(c.getLocation().x, c.getY() - (maxSize - minSize));
										}
									}
									
									grpPanel.setSize(312, minSize);									
									btnReset.setLocation(btnReset.getX(), lastComponent.getY() + lastComponent.getHeight() + 6);	

									if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31 && firstComponent.getY() == 30)
									{
										settingsScrollBar.setVisible(false);
									}
									
									break;
								}
								else
									i --;
																
								for (Component c : frame.getContentPane().getComponents())
								{
									if (c instanceof JPanel && c.isVisible() && c.getX() == grpPanel.getX() && c.getY() > grpPanel.getY() + grpPanel.getHeight())
									{										
										c.setLocation(c.getLocation().x, c.getY() - l);
									}	
								}
								
								grpPanel.setSize(312, i);
								
								if (firstComponent.getY() < grpChooseFiles.getLocation().y && firstComponent.isVisible())
								{
									for (Component c2 : frame.getContentPane().getComponents())
									{
										if (c2 instanceof JPanel && c2.isVisible() && c2.getX() == grpPanel.getX())
										{
											c2.setLocation(c2.getLocation().x, c2.getY() + l);
										}
									}
								}	
								
								btnReset.setLocation(btnReset.getX(), lastComponent.getY() + lastComponent.getHeight() + 6);
									
								if (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
								{
									settingsScrollBar.setVisible(false);
								}
								
								//Animate size
								animateSections(startTime, false);	
																		
							} while (i > minSize);
							
						}
						
					} catch (Exception e1) {						
					}
					finally {
						extendSectionsIsRunning = false;
					}
			
				}
			});
			extend.start();
			extend.setPriority(Thread.MAX_PRIORITY);
		}
		
	}
	
	public static void animateSections(long startTime, boolean horizontal) {

		int time = 200000;
		if (horizontal)
		{
			time = 1000000;
		}
						
		while (System.nanoTime() - startTime < time)
		{
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {}
		}  
	}
	
	public static void changeFilters() {
				
		if (comboFonctions.getEditor().getItem().toString().length() == 0)
		{
			lblFilter.setVisible(false);
			comboFilter.setVisible(false);
			lblFilter.setLocation(164, 21);
			lblFilter.setIcon(null);
			
			final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
					".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp", ".avif" };
			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
			comboFilter.setModel(model);
			comboFilter.setSelectedIndex(0);
		}		
		else
		{
			if (comboFonctions.getSelectedItem().toString().contains("H.26"))
			{
				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String[] extensions = new String[] { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts", ".m2ts" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform"))) {
				
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				final String types[] = {"23,98 " + Shutter.language.getProperty("fps"), "24 " + Shutter.language.getProperty("fps"), "25 " + Shutter.language.getProperty("fps"), "29,97 " + Shutter.language.getProperty("fps"), "30 " + Shutter.language.getProperty("fps"), "48 " + Shutter.language.getProperty("fps"), "50 " + Shutter.language.getProperty("fps"), "59,94 " + Shutter.language.getProperty("fps"), "60 " + Shutter.language.getProperty("fps") };				
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(2);
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio"))) {
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				final String types[] = {	language.getProperty("shortest"), language.getProperty("longest") };				
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi",
						".mp4", ".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".webm"};
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionExtract"))) {

				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				final String types[] = { language.getProperty("setAll"), language.getProperty("video"), language.getProperty("audio"), language.getProperty("subtitles")};
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionBlackDetection"))) {

				lblFilter.setText(Shutter.language.getProperty("levels"));
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);
				
				String types[] = { "16-235", "0-255" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DV PAL")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "16/9", "4/3" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("AV1")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String[] extensions = new String[] {".mp4", ".mkv", ".webm" };
								
				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("Blu-ray")) {

				/*
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));*/
				
				lblFilter.setVisible(false);
				comboFilter.setVisible(false);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);
				
				String[] extensions = new String[] { "H.264", "H.265" };
								
				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP8")) {

				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
							
				String[] extensions = new String[] { ".webm", ".mkv" };			
				
				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP9")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
							
				String[] extensions = new String[] { ".webm", ".mkv", ".mp4" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("WAV")
					|| comboFonctions.getSelectedItem().toString().equals("AIFF")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "16 Bits", "24 Bits", "32 Bits", "32 Float" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false || comboFilter.getModel().getSize() != 4) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("FLAC")) {

				lblFilter.setText("Comp.:");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);
				
				String types[] = { "0","1","2","3","4","5","6","7","8","9","10","11","12" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(5);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("ALAC")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "16 Bits", "24 Bits" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false || comboFilter.getModel().getSize() != 2) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			}  else if (comboFonctions.getSelectedItem().toString().equals("MP3")
					|| comboFonctions.getSelectedItem().toString().equals("AAC")
					|| comboFonctions.getSelectedItem().toString().equals("AC3")
					|| comboFonctions.getSelectedItem().toString().equals("Opus")
					|| comboFonctions.getSelectedItem().toString().equals("Vorbis")					
					|| comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus")) {
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(audioValues);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);

					if (comboFonctions.getSelectedItem().toString().equals("MP3") || comboFonctions.getSelectedItem().toString().equals("AAC") || comboFonctions.getSelectedItem().toString().equals("Vorbis"))
					{
						comboFilter.setSelectedIndex(9);
					}
					else if (comboFonctions.getSelectedItem().toString().equals("AC3") || comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboFilter.setSelectedIndex(7);
					}
					else if (comboFonctions.getSelectedItem().toString().equals("Opus"))
					{						
						comboFilter.setSelectedIndex(11);
					}
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("HAP")) {		
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				DefaultComboBoxModel<Object> model;
				String types[] = { "Standard", "Alpha", "Q"};
				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().contains("XDCAM") || comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")) {				

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				DefaultComboBoxModel<Object> model;
				String types[] = { ".mxf", ".mov"};
								
				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHD")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				DefaultComboBoxModel<Object> model;
				if (comboResolution.getSelectedItem().toString().equals("1280x720")) {
					String types[] = { "60", "90", "90 X", "75", "110", "145", "220", "220 X" };
					model = new DefaultComboBoxModel<Object>(types);
					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(0);
					}
				} else {
					String types[] = { "36", "115", "175", "175 X", "36", "120", "185", "185 X", "45", "145", "220", "220 X", "75", "240", "365", "365 X", "90", "290", "440", "440 X"};
					model = new DefaultComboBoxModel<Object>(types);

					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(5);
					}
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHR")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				DefaultComboBoxModel<Object> model;
				String types[] = { "LB", "SQ", "HQ", "HQX", "444" };
				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("Apple ProRes")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
								
				String types[] = new String[] { "Proxy", "LT", "422", "422 HQ", "444", "4444", "4444 XQ" };
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
				{
					types = new String[] { "Proxy", "LT", "422", "422 HQ", "4444", "4444 XQ" };
				}
				
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(4).equals(comboFilter.getModel().getElementAt(4)) == false || model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")) {

				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "Low", "Medium", "High", "Film Scan", "Film Scan 2", "Film Scan 3", "Film Scan 3+"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(3);
				}			

			} else if (comboFonctions.getSelectedItem().toString().equals("Uncompressed")) {
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "YUV", "RGB" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("XAVC")) {
				
				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "300", "480", "960"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(1);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("MPEG-2")) {
				
				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { ".mpg", ".ts" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization"))) {
				
				lblFilter.setText(" ");		
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = new String[31];

				types[0] = "0 LUFS";
				int i = 1;
				do {
					types[i] = ("-" + i + " LUFS");
					i++;
				} while (i < 31);

				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(23);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture"))) {
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { ".png", ".tif", ".tga", ".dpx", ".j2k", ".exr", ".webp", ".avif",".bmp", ".ico", ".gif", ".apng" };
				
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false || comboFilter.getItemCount() != model.getSize())
				{
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().contains("JPEG")) {
				
				lblFilter.setText(" ");	
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				
				String types[] = { "100%","95%","90%","85%","80%","75%","70%","65%","60%","55%","50%","45%","40%","35%","30%","25%","20%","15%","10%","5%","0%"};
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}	
			}
			else
			{
				lblFilter.setVisible(false);
				comboFilter.setVisible(false);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);
				
				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg"))
				{
					lblFilter.setText(" ");			
					lblFilter.setVisible(true);
					comboFilter.setVisible(true);
					lblFilter.setLocation(165, 23);
					lblFilter.setIcon(new FlatSVGIcon("contents/arrow.svg", 30, 30));
				}
				
				final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
						".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm", ".webp", ".avif" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);				
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false)
				{
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			}
		}
	}

	private static void setDestinationTabs(int tabs) {	
		
		try {
			//Affichage des titres
			Font tabFont = new Font(montserratFont, Font.PLAIN, 11);		
			if (getLanguage.equals(Locale.of("en").getDisplayLanguage()))
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
			JLabel mailTab = new JLabel(mailIcon);
			mailTab.setFont(tabFont);
			JLabel streamTab = new JLabel(streamIcon);	
			streamTab.setFont(tabFont);					
							
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
				else if (tabs == 5)
				{			
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);	
					grpDestination.addTab("FTP", new JPanel());									
					grpDestination.addTab("Mail", destinationMail);;
					
					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					grpDestination.setTabComponentAt(4, mailTab);					
				}
				else if (tabs == 6)
				{			
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);	
					grpDestination.addTab("FTP", new JPanel());
					grpDestination.addTab("Mail", destinationMail);
					grpDestination.addTab("Stream", destinationStream);
					
					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					grpDestination.setTabComponentAt(4, mailTab);
					grpDestination.setTabComponentAt(5, streamTab);
				}
			}
		} catch (Exception e) {}
	}
	
	public static void disableAll() {
		
		Utils.disableSleepMode = false;
		Utils.disableSleepMode();
		
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
		components = grpBitrate.getComponents();
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
		components = grpCrop.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSubtitles.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpWatermark.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpColorimetry.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageAdjustement.getComponents();
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
		components = grpImageSequence.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageFilter.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSetTimecode.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		
		//Status bar GPU
		comboGPUDecoding.setEnabled(false);
		comboGPUFilter.setEnabled(false);
		comboAccel.setEnabled(false);
		
		//Disable buttons
		VideoPlayer.setPlayerButtons(false);
		VideoPlayer.player.remove(selection);	
		VideoPlayer.player.remove(overImage);	
		VideoPlayer.player.remove(timecode);
		VideoPlayer.player.remove(fileName);
		VideoPlayer.player.remove(subsCanvas);
		VideoPlayer.player.remove(logo);
		VideoPlayer.showScale.setVisible(false);		
		VideoPlayer.playerStop();

		lblFiles.setEnabled(true);
		lblFilesEnded.setEnabled(true);

		comboFonctions.setEnabled(false);
		comboFilter.setEnabled(false);
		btnReset.setEnabled(false);

		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		if (FunctionUtils.completed > 0)
		{
			lblFilesEnded.setVisible(true);
		}
		cancelled = false;
		btnCancel.setEnabled(true);

		if (inputDeviceIsRunning)
			progressBar1.setIndeterminate(true);
		
		progressBar1.setValue(0);

		if (FFMPEG.isRunning)
		{		
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().toString().contains("JPEG") == false)
				progressBar1.setValue(0);
			
			if (caseDisplay.isSelected() == false)
			{
				changeWidth(false);
				caseRunInBackground.setEnabled(true);
			}

			caseDisplay.setEnabled(false);
			btnStart.setEnabled(true);
						
			if (inputDeviceIsRunning || caseStream.isSelected())
			{
				btnStart.setText(language.getProperty("btnStopRecording"));
			}
			else
				btnStart.setText(language.getProperty("btnPauseFunction"));
		}
		
		// Important
		topPanel.repaint();
		statusBar.repaint();
		topImage.repaint();				
		frame.repaint();
	}

	public static void enableAll() {
		
		Utils.disableSleepMode = true;
		
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
		
		//.webp .avif
		comboImageOption.setEnabled(true);
		
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
		
		if (caseGamma.isSelected() == false)
		{
			comboGamma.setEnabled(false);
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
			lbl48k.setEnabled(false);
		}
		
		if (caseAudioOffset.isSelected() == false)
			txtAudioOffset.setEnabled(false);
		
		components = grpAudio.getComponents();
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
		
		if (caseNormalizeAudio.isSelected() == false)
		{
			comboNormalizeAudio.setEnabled(false);	
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
			
		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		
		if (caseCreateTree.isSelected() == false)
		{
			comboCreateTree.setEnabled(false);
		}
		
		components = grpBitrate.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		
		components = grpCrop.getComponents();
		for (int i = 0; i < components.length; i++)
		{
			if (caseEnableCrop.isSelected() == false && components[i] instanceof JCheckBox == false)
			{
				components[i].setEnabled(false);
			}
			else
				components[i].setEnabled(true);
		}
		
		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++)
		{
			components[i].setEnabled(true);
		}
		
		if (caseAddTimecode.isSelected() == false && caseShowTimecode.isSelected() == false)
		{
			components = grpOverlay.getComponents();
			for (int i = 0; i < components.length; i++)
			{
				if (components[i].getY() > caseShowTimecode.getY() && components[i].getY() < caseAddText.getY())
					components[i].setEnabled(false);
			}
		}
		
		if (caseAddTimecode.isSelected() == false)
		{
			TC1.setEnabled(false);
			TC2.setEnabled(false);
			TC3.setEnabled(false);
			TC4.setEnabled(false);		
		}
		
		if (caseAddText.isSelected() == false && caseShowFileName.isSelected() == false)
		{
			components = grpOverlay.getComponents();
			for (int i = 0; i < components.length; i++)
			{
				if (components[i].getY() > caseShowFileName.getY())
					components[i].setEnabled(false);
			}			
		}
		
		if (caseAddText.isSelected() == false)
		{
			overlayText.setEnabled(false);
		}
		
		
		components = grpSubtitles.getComponents();
		for (int i = 0; i < components.length; i++)
		{			
			if (caseAddSubtitles.isSelected() == false && components[i] instanceof JCheckBox == false)
			{
				components[i].setEnabled(false);
			}
			else
				components[i].setEnabled(true);
		}
		
		if (caseAddSubtitles.isSelected() && subtitlesBurn == false)
		{
			for (Component c : grpSubtitles.getComponents())
			{
				if (c instanceof JCheckBox == false)
				{
					c.setEnabled(false);
				}
			}
		}
		
		components = grpWatermark.getComponents();
		for (int i = 0; i < components.length; i++)
		{			
			if (caseAddWatermark.isSelected() == false && components[i] instanceof JCheckBox == false)
			{
				components[i].setEnabled(false);
			}
			else
				components[i].setEnabled(true);
		}
						
		if (inputDeviceIsRunning == false)
		{
			components = grpImageAdjustement.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(true);
			}
		}
		
		components = grpCorrections.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		
		components = grpTransitions.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
				
		//Status bar GPU
		comboGPUDecoding.setEnabled(true);
		if (comboGPUDecoding.getSelectedItem().equals(language.getProperty("aucun")) == false)
				comboGPUFilter.setEnabled(true);
		
		if (comboAccel.getItemCount() > 1)
			comboAccel.setEnabled(true);
		
		//Enable buttons
		VideoPlayer.setPlayerButtons(true);	
		
		if (caseAddWatermark.isSelected())
		{
			VideoPlayer.player.add(logo);
		}
		
		if (caseAddSubtitles.isSelected() && subtitlesBurn)
		{
			VideoPlayer.player.add(subsCanvas);
		}
		
		if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected()) 
		{				
			VideoPlayer.player.add(timecode);			
		}
		
		if (caseShowFileName.isSelected() || caseAddText.isSelected())
		{				
			VideoPlayer.player.add(fileName);			
		}		
		
		if (caseEnableCrop.isSelected())
		{
			//Shutter.overImage need to be the last component added
			VideoPlayer.player.add(selection);
			VideoPlayer.player.add(overImage);
		}
		
		if (FFPROBE.audioOnly)
		{
			caseVideoFadeIn.setEnabled(false);
			caseVideoFadeOut.setEnabled(false);
		}
		
		if (caseVideoFadeIn.isSelected() == false)
			spinnerVideoFadeIn.setEnabled(false);
		
		if (caseAudioFadeIn.isSelected() == false)
			spinnerAudioFadeIn.setEnabled(false);
		
		if (caseVideoFadeOut.isSelected() == false)
			spinnerVideoFadeOut.setEnabled(false);
		
		if (caseAudioFadeOut.isSelected() == false)
			spinnerAudioFadeOut.setEnabled(false);

		if (caseAS10.isSelected() == false)
			comboAS10.setEnabled(false);
		
		if (caseGOP.isSelected() == false)
			gopSize.setEnabled(false);
		
		if (caseFilmGrain.isSelected() == false)
			comboFilmGrain.setEnabled(false);
		
		if (caseFilmGrainDenoise.isSelected() == false)
			comboFilmGrainDenoise.setEnabled(false);
		
		if (caseChunks.isSelected() == false)
			chunksSize.setEnabled(false);
		
		if ((comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false || lblVBR.getText().equals("CQ")))
		{
			if (lblVBR.getText().equals("CQ") == false)
				caseForceOutput.setEnabled(false);
			
			case2pass.setEnabled(false);
		}
		
		if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals("Vulkan Video")))
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
		else if (comboFonctions.getSelectedItem().toString().equals("VP9")
		|| comboFonctions.getSelectedItem().toString().equals("H.265") && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
		{
			caseAlpha.setEnabled(true);			
		}
		
		if (caseQMax.isSelected())
		{
			caseForcePreset.setEnabled(false);
			comboForcePreset.setEnabled(false);
			caseForceSpeed.setEnabled(false);
			comboForceSpeed.setEnabled(false);			
			caseForceQuality.setEnabled(false);
			comboForceQuality.setEnabled(false);	
		}

		if (caseTruePeak.isSelected() == false)
		{
			comboTruePeak.setEnabled(false);
		}
			
		if (caseLRA.isSelected() == false)
		{
			comboLRA.setEnabled(false);
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
		
		// Important
		topPanel.repaint();
		statusBar.repaint();
		topImage.repaint();
		frame.repaint();
				
		if (inputDeviceIsRunning)
			progressBar1.setIndeterminate(false);
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public static void enfOfFunction() {	

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
				Taskbar.getTaskbar().setWindowProgressValue(frame, 100);
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.ERROR);
			} 	
			
			JTextArea errorText = new JTextArea(errorList.toString());  
			errorText.setWrapStyleWord(true);
			
			JScrollPane scrollPane = new JScrollPane(errorText);  
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false); 
			scrollPane.setPreferredSize( new Dimension( 500, 400 ) );

			if (scanIsRunning == false)
			{
				Object[] moreInfo = {"OK", language.getProperty("menuItemConsole")};
		        
				int result =  JOptionPane.showOptionDialog(Shutter.frame, scrollPane, Shutter.language.getProperty("notProcessedFiles"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, moreInfo, null);
				
				if (result == JOptionPane.NO_OPTION)
				{
					if (Console.frmConsole != null)
					{
						if (Console.frmConsole.isVisible())
						{
							Console.frmConsole.toFront();
						}
						else
							new Console();
					} 
					else
						new Console();
				}
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
			
			FFMPEG.errorLog.setLength(0);
			errorList.setLength(0);
		}
		else if (RenderQueue.frame != null && RenderQueue.frame.isVisible() && Shutter.cancelled == false)
		{
			RenderQueue.tableRow.setRowCount(0);
		}
		
		if (VideoPlayer.fullscreenPlayer)
		{
			VideoPlayer.fullscreenPlayer = false;						
			
			topPanel.setVisible(true);
			grpChooseFiles.setVisible(true);
			grpChooseFunction.setVisible(true);
			grpDestination.setVisible(true);
			grpProgression.setVisible(true);
			statusBar.setVisible(true);
			
			frame.getContentPane().setBackground(new Color(30,30,35));
									
			changeSections(false);
					
			VideoPlayer.setPlayerButtons(true);
			
			VideoPlayer.mouseIsPressed = false;
    		
    		VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame			
			
			VideoPlayer.resizeAll();
			
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, Shutter.frame.getWidth(), Shutter.frame.getHeight(), 15, 15));
            Area shape2 = new Area(new Rectangle(0, Shutter.frame.getHeight()-15, Shutter.frame.getWidth(), 15));
            shape1.add(shape2);
    		frame.setShape(shape1);
		}
		
		//Unlock the file to be deletable
		if (scanIsRunning == false && screenshotIsRunning == false)
		{
			VideoPlayer.videoPath = null;	
			fileList.clearSelection();
			VideoPlayer.frameVideo = null;	
			VideoPlayer.playerRepaint();
			
			// Lecteur
			if (VideoPlayer.waveform != null)
			{
				VideoPlayer.waveform = null;
				VideoPlayer.waveformIcon.setIcon(null);
				VideoPlayer.waveformIcon.repaint();
			}
		}
		else if (screenshotIsRunning)
		{
			VideoPlayer.addWaveform(false);
		}
		
		if (scanIsRunning == false)
		{
			enableAll();
		}

		if (cancelled == true)
		{
			lblCurrentEncoding.setForeground(Color.RED);
			lblCurrentEncoding.setText(language.getProperty("processCancelled"));
			progressBar1.setValue(0);
		}
		else
		{
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
						
		//IMPORTANT
		screenshotIsRunning = false;
		
		FunctionUtils.sendMail();
		lastActions();
	}

	public static void lastActions() {
		
		if (Settings.btnEmptyListAtEnd.isSelected() && cancelled == false && FFMPEG.error == false)
			liste.clear();
		
		Thread thread = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				
				do {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {}
				} while (Ftp.isRunning || sendMailIsRunning);
		
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
		
		//Show only file name
		if (Settings.btnHidePath.isSelected() && Shutter.scanIsRunning == false)
		{
			setText(new File(value.toString()).getName());
		}
		else
			setText(value.toString());
		
		setIcon(new FlatSVGIcon("contents/item.svg", 10, 10));

		setToolTipText(value.toString());
		
		setFont(new Font("SansSerif", Font.PLAIN, 12));
		setForeground(Color.LIGHT_GRAY);
		setOpaque(true);
		
		if (isSelected)
		{
			setBackground(new Color(75,75,80));
		}
		else
		{			
			if (index % 2 == 1)
				setBackground(new Color(42,42,47));
			else
				setBackground(new Color(51,51,56));
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
		
		if (isSelected)
		{
			setBackground(Utils.themeColor);
		}
		else
		{	
			setBackground(new Color(42,42,47));
		}
		
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
			if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.inputDeviceIsRunning == false
			&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false)
			{
				Shutter.fileList.setBorder(BorderFactory.createLineBorder(Utils.themeColor, 1));
				return true;
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
				
				if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false)
				{
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext())
					{						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
						{
							if (file.isDirectory())
							{								
								if (file.toString().contains("completed") == false && file.toString().contains("error") == false)
								{
									boolean folderExists = false;
									for (int f = 0 ; f < Shutter.liste.getSize() ; f++)
									{							
										if (Shutter.liste.getElementAt(f).equals(file.toString()))
										{
											folderExists = true;
										}
									}
									
									if (folderExists == false)
									{
										Utils.findDirectories(file.toString());						
									}
								}
							}
							else 
								file = new File(file.getParent());
								
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								Shutter.liste.addElement(file + "/");
							else
								Shutter.liste.addElement(file + "\\");
									
							Shutter.addToList.setVisible(false);
							Shutter.lblFiles.setText(Utils.filesNumber());
							
							if (file != null) 
							{
								if (Shutter.caseChangeFolder1.isSelected()) 
								{
									Shutter.scanIsRunning = true;
									Shutter.changeFilters();
								} 
								else
									JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("dragFolderToDestination"), Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.INFORMATION_MESSAGE);				
							}

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
								else 
								{
									if (file.isHidden() == false)
									{			
										boolean allowed = true;
										if (Settings.btnExclude.isSelected())
										{	
											for (String excludeExt : Settings.txtExclude.getText().replace(" ", "").split("\\*"))
											{					
												if (excludeExt.contains(".") && ext.toLowerCase().equals(excludeExt.replace(",", "").toLowerCase()))
												{
													allowed = false;
													break;
												}
											}	
											
											if (allowed == false)
											{
												continue;//Next
											}
										}	
										
										if (file.getCanonicalPath().toString().contains("\"") || file.getCanonicalPath().toString().contains("\'") || file.getName().contains("/") || file.getName().contains("\\"))
										{
											if (FunctionUtils.allowsInvalidCharacters == false) 
											{
												JOptionPane.showConfirmDialog(Shutter.frame, file.getAbsoluteFile().toString() + System.lineSeparator() + Shutter.language.getProperty("invalidCharacter"), Shutter.language.getProperty("import"),
												JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
												
												FunctionUtils.allowsInvalidCharacters = true;
											}
										}
										
										Shutter.liste.addElement(file.getCanonicalPath().toString());	
										Shutter.addToList.setVisible(false);
										Shutter.lblFiles.setText(Utils.filesNumber());
									}
								}
							}
							else
							{
								Utils.findFiles(file.getCanonicalPath().toString());
							}
						}
					}

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
							FFPROBE.setLength();
							break;
					}

					// CaseOPATOM
					switch (Shutter.comboFonctions.getSelectedItem().toString())
					{
						case "DNxHD":
						case "DNxHR":
						case "Apple ProRes":
						case "GoPro CineForm":
						case "QT Animation":
						case "Uncompressed":
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
					
					// VideoPlayer.player					
					Shutter.fileList.setSelectedIndex(Shutter.liste.getSize() - 1);					
						
					VideoPlayer.setMedia();					
					
					// Filter
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

// Overlay fonts
class ComboRenderer extends BasicComboBoxRenderer {

    private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
    final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    private int row;

	@SuppressWarnings("rawtypes") ComboRenderer(JComboBox fontsBox) {
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
class ComboRendererOverlay extends BasicComboBoxRenderer {

	private static final long serialVersionUID = 1L;
	private JComboBox comboBox;
	final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private int row;

	ComboRendererOverlay(JComboBox fontsBox) {
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

@SuppressWarnings("serial")
class AntiAliasedRoundRectangle extends RoundRectangle2D.Double {

    public AntiAliasedRoundRectangle(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        super(x, y, w, h, arcWidth, arcHeight);
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);        
        g2d.draw(this);
    }
    
    public void fill(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);        
        g2d.fill(this);
    }
}
	