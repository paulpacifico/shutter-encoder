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

package shutterencoder.ui.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.Area;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.JTextField;

import shutterencoder.library.Anonymizer;
import shutterencoder.library.BackgroundRemover;
import shutterencoder.library.Demucs;
import shutterencoder.library.Deoldify;
import shutterencoder.library.Exiftool;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.Ncnn;
import shutterencoder.library.Python;
import shutterencoder.library.Whisper;
import shutterencoder.library.Ytdlp;
import shutterencoder.ui.handlers.HardwareListener;
import shutterencoder.ui.others.Functions;
import shutterencoder.ui.others.ReducedWindow;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.others.Update;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.ui.renderers.SplashRenderer;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Main {

	/*
	 * Initialisation
	 */
	public static String actualVersion = "20.4";
	public static String getLanguage = "";
	public static String arch = "x86_64";
	public static long availableMemory;
	public static String magnetoFont = "Magneto";
	public static String boldFont = "Montserrat";
	public static String mainFont = "FreeSans";
	public static File documents = new File(System.getProperty("user.home") + "/Shutter Encoder");
	public static File settingsXML = new File(Main.documents + "/settings.xml");
	public static String dirTemp = System.getProperty("java.io.tmpdir");
	public static File lutsFolder;
	public static File subtitlesFile;
	public static Properties language = new Properties();
	public static URL soundURL;
	public static URL soundErrorURL;
	public static boolean disableEndingSound = false;
	public static JFrame frame = new JFrame();
	public static int minHeight = 731;
	public static int extendedWidth = 1350;
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
	public static boolean subtitlesBurn = true;
	public static boolean autoBurn = false;
	public static boolean autoEmbed = false;
	public static boolean cutKeyframesIsDisplayed = false;
	public static boolean rewrapKeyframesIsDisplayed = false;
	public static boolean conformKeyframesIsDisplayed = false;
	public static StringBuilder errorList = new StringBuilder();
	public static NumberFormat formatter = new DecimalFormat("00");
	public static NumberFormat formatterToMs = new DecimalFormat("000");

	public static int MousePositionX;
	public static int MousePositionY;

	/*
	 * Position ReducedWindow
	 */
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static Rectangle bounds = ge.getMaximumWindowBounds();
	public static int MiniWindowY = bounds.height / 2;

	/*
	 * Components
	 */
	protected static JLabel settingsIcon;
	protected static JLabel quit;
	protected static JLabel expand;
	protected static JLabel minimize;
	protected static JLabel help;
	protected static JLabel newInstance;

	protected static ImageIcon mailIcon;
	protected static ImageIcon streamIcon;

	public static DefaultListModel<String> list = new DefaultListModel<String>();
	protected static PopupMenu dropFiles;
	public static JLabel sortName;
	public static JList<String> fileList;
	public static JLabel addToList = new JLabel();
	public static String loadedFunction = null;
	public static JComboBox<String[]> comboFonctions;
	public static List<String> functionsList;

	protected static JButton btnBrowse;
	protected static JButton btnEmptyList;
	public static JComboBox<Object> comboFilter;
	protected static JComboBox<Object> comboLUTs;
	protected static JComboBox<Object> comboGamma;
	protected static JComboBox<Object> comboInLevels;
	public static JComboBox<Object> comboOutLevels;
	public static JComboBox<Object> comboInColormatrix;
	protected static JComboBox<Object> comboOutColormatrix;
	protected static JComboBox<Object> comboColorspace;
	protected static JLabel lblHDR;
	protected static JComboBox<String> comboHDRvalue;
	protected static JComboBox<String> comboCLLvalue;
	protected static JComboBox<String> comboFALLvalue;
	protected static JButton btnLUTs;
	public static JButton btnStart;
	public static JButton btnCancel;
	public static JCheckBox caseOpenFolderAtEnd1;
	public static JCheckBox caseOpenFolderAtEnd2;
	public static JCheckBox caseOpenFolderAtEnd3;
	public static JCheckBox caseChangeFolder1;
	public static JCheckBox caseChangeFolder2;
	public static JCheckBox caseChangeFolder3;
	public static JCheckBox casePrefix;
	public static JTextField txtPrefix;
	public static JCheckBox btnExtension;
	public static JTextField txtExtension;
	public static JCheckBox caseSubFolder;
	public static JTextField txtSubFolder;
	public static JCheckBox caseDeleteSourceFile;
	public static JCheckBox caseRunInBackground;
	public static JCheckBox caseDisplay;
	protected static JLabel iconTVInterpret;
	public static JLabel iconList;
	public static JLabel iconPresets;
	public static JComboBox<String> comboResolution;
	protected static JLabel lblImageQuality;
	protected static JComboBox<String> comboImageOption;
	public static JCheckBox caseRotate;
	public static JComboBox<String> comboRotate;
	public static JCheckBox caseMiror;
	public static JCheckBox btnNoUpscale;
	public static JCheckBox caseCreateSequence;
	protected static JLabel lblInterpretation;
	protected static JLabel lblIsInterpret;
	public static JComboBox<String> comboInterpret;
	public static JCheckBox case2pass;
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
	public static JCheckBox caseLUTs;
	protected static JCheckBox caseGamma;
	public static JCheckBox caseLevels;
	public static JCheckBox caseColormatrix;
	protected static JCheckBox caseColorspace;
	public static JComboBox<String> caseSequenceFPS;
	public static JCheckBox caseSetTimecode;
	public static JCheckBox caseIncrementTimecode;
	protected static JCheckBox caseGenerateFromDate;
	protected static JCheckBox caseReadAudioTimecode;
	protected static JComboBox<String> comboReadAudioTimecode;
	public static JTextField TCset1;
	public static JTextField TCset2;
	public static JTextField TCset3;
	public static JTextField TCset4;
	protected static JCheckBox caseNormalizeAudio;
	public static JCheckBox caseEqualizer;
	protected static JCheckBox caseChangeAudioCodec;
	public static JCheckBox caseAudioOffset;
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
	protected static JComboBox<String> comboFastDecode;
	protected static JCheckBox caseVarianceBoost;
	protected static JComboBox<String> comboVarianceBoost;
	protected static JCheckBox caseAlpha;
	protected static JComboBox<String> comboAlpha;
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
	protected static JCheckBox caseEncoderParams;
	protected static JLabel lblHWaccel;
	public static JComboBox<String> comboAccel;
	protected static JComboBox<String> comboSelectedGPU;
	protected static JComboBox<String> comboForceProfile;
	protected static JComboBox<String> comboForceLevel;
	protected static JComboBox<String> comboForcePreset;
	protected static JComboBox<String> comboForceTune;
	protected static JComboBox<String> comboForceQuality;
	protected static JComboBox<String> comboForceSpeed;
	protected static JTextField textEncoderParams;
	public static JCheckBox caseForcerDAR;
	protected static JCheckBox caseDecimate;
	protected static JCheckBox caseConform;
	protected static JCheckBox caseCreateTree;
	protected static JComboBox<String> comboCreateTree;
	protected static JCheckBox casePreserveMetadata;
	protected static JCheckBox casePreserveSubs;
	protected static JCheckBox caseCreateOPATOM;
	public static JCheckBox caseOPATOM;
	protected static JCheckBox caseAS10;
	protected static JCheckBox caseChunks;
	protected static JCheckBox caseDRC;
	protected static JCheckBox caseTruePeak;
	protected static JComboBox<Object> comboTruePeak;
	protected static JCheckBox casePeakNormalization;
	protected static JComboBox<Object> comboPeakNormalization;
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
	protected static JComboBox<String> comboLanguage1;
	protected static JComboBox<String> comboLanguage2;
	protected static JComboBox<String> comboLanguage3;
	protected static JComboBox<String> comboLanguage4;
	protected static JComboBox<String> comboLanguage5;
	protected static JComboBox<String> comboLanguage6;
	protected static JComboBox<String> comboLanguage7;
	protected static JComboBox<String> comboLanguage8;
	protected static JComboBox<String> comboAudioCodec1;
	protected static JComboBox<String> comboAudioCodec2;
	protected static JComboBox<String> comboAudioCodec3;
	protected static JComboBox<String> comboAudioCodec4;
	protected static JComboBox<String> comboAudioCodec5;
	protected static JComboBox<String> comboAudioCodec6;
	protected static JComboBox<String> comboAudioCodec7;
	protected static JComboBox<String> comboAudioCodec8;
	protected static JComboBox<String> comboAudioBitrate1;
	protected static JComboBox<String> comboAudioBitrate2;
	protected static JComboBox<String> comboAudioBitrate3;
	protected static JComboBox<String> comboAudioBitrate4;
	protected static JComboBox<String> comboAudioBitrate5;
	protected static JComboBox<String> comboAudioBitrate6;
	protected static JComboBox<String> comboAudioBitrate7;
	protected static JComboBox<String> comboAudioBitrate8;
	protected static JComboBox<String> comboAudioRate1;
	protected static JComboBox<String> comboAudioRate2;
	protected static JComboBox<String> comboAudioRate3;
	protected static JComboBox<String> comboAudioRate4;
	protected static JComboBox<String> comboAudioRate5;
	protected static JComboBox<String> comboAudioRate6;
	protected static JComboBox<String> comboAudioRate7;
	protected static JComboBox<String> comboAudioRate8;
	protected static JComboBox<String> comboAS10;
	public static JTextField txtAudioOffset;
	public static JPanel topPanel;
	protected static JLabel lblShutterEncoder;
	protected static JLabel lblV;
	protected static JLabel topImage;
	protected static JScrollPane scrollBar;
	public static JLabel lblFilesEnded;
	public static JLabel lblFiles;
	protected static JLabel lblFilter;
	public static JTextField lblDestination1;
	public static JTextField lblDestination2;
	public static JTextField lblDestination3;
	public static JProgressBar progressBar;
	public static JLabel lblCurrentEncoding;
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
	public static JLabel lblPad;
	public static JComboBox<String> comboDAR;
	protected static JLabel lblNiveaux;
	protected static JLabel lblOPATOM;
	protected static JLabel lblCreateOPATOM;
	protected static JCheckBox caseBlend;
	protected static JCheckBox caseMotionBlur;
	protected static JSlider sliderBlend;
	public static JButton btnReset;
	public static boolean doNotLoadImage = false;
	public static JPanel statusBar;
	public static boolean windowDrag;
	public static JLabel lblYears;
	public static JLabel lblBy;
	protected static JLabel lblGpuDecoding;
	protected static JLabel lblGpuFiltering;
	public static JComboBox<String> comboGPUDecoding;
	public static JComboBox<String> comboGPUFilter;
	public static JLabel lblRemainingTime;
	public static JLabel lblElapsedTime;
	protected static JComboBox<String> debitVideo;
	protected static JComboBox<String> maximumBitrate;
	protected static JComboBox<String> debitAudio;
	protected static JLabel lblAudioKbs;
	protected static String audioValues[] = new String[] { "1536", "1344", "1152", "960", "768", "640", "512", "448", "384", "320", "256", "192", "160", "128", "96", "64", "32" };
	protected static JPanel h264lines;
	protected static JTextField bitrateSize;
	protected static JLabel lock;
	public static boolean isLocked = false;
	protected static JLabel lblKbsH264;
	protected static JLabel lblMaximumKbs;
	protected static JLabel lblSize;
	protected static JLabel lblFileSizeMo;
	protected static JLabel lblVideoBitrate;
	protected static JLabel lblMaximumBitrate;
	protected static JLabel lblAudioBitrate;
	protected static JLabel lblVBR;
	protected static JPopupMenu popupList;
	protected static JPopupMenu scanListe;
	protected static JPopupMenu popupProgression;
	protected static JPopupMenu popupDestination;
	public static JTextField textMail;
	public static JCheckBox caseSendMail;
	public static JTextField textStream;
	protected static JCheckBox caseStream;
	protected static JCheckBox caseLoop;
	public static boolean setTimecodeEdited = false;

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
	public static JPanel grpFileInformation;
	public static JLabel labelFileInfo;
	protected static JScrollBar settingsScrollBar;
	private static Thread scrollThread;
	private boolean allowScrolling = false;

	// grpImageAdjustement
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

	// grpCorrections
	public static JPanel grpCorrections;
	public static JCheckBox caseStabilisation;
	public static String stabilisation = "";
	public static JCheckBox caseDeflicker;
	public static JCheckBox caseBanding;
	public static JSlider sliderBanding;
	public static JCheckBox caseLimiter;
	public static JCheckBox caseDetails;
	public static JSlider sliderDetails;
	public static JCheckBox caseDeblock;
	public static JSlider sliderDeblock;
	public static JCheckBox caseAddGrain;
	public static JSlider sliderAddGrain;
	public static JCheckBox caseDenoise;
	public static JSlider sliderDenoise;
	public static JCheckBox caseSmoothExposure;
	public static JSlider sliderSmoothExposure;

	// grpTransitions
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

	// grpCrop
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
	public static boolean alt = false;
	public static boolean ctrl = false;
	public static JCheckBox caseEnableCrop;
	public static JComboBox<String> comboPreset;
	public static int mouseCropOffsetX;
	public static int mouseCropOffsetY;
	public static JTextField textCropPosX;
	public static JTextField textCropPosY;
	public static JTextField textCropWidth;
	public static JTextField textCropHeight;
	public static JLabel cropLock;
	public static boolean cropIsLocked = false;

	// grpOverlay
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
	public static String foregroundHex = "ffffff"; // white
	public static String backgroundHex = "000000"; // black
	public static String foregroundTcAlpha = "ff"; // 100%
	public static String foregroundNameAlpha = "ff";
	public static String backgroundTcAlpha = "7f"; // 50%
	public static String backgroundNameAlpha = "7f";

	// grpWatermark
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

	// grpSubtitles
	public static JPanel grpSubtitles;
	public static JCheckBox caseAddSubtitles;
	public static JComboBox<String> comboSubsSource;
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
		
		new SplashRenderer();

		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("apple.awt.textAntialiasing", "on");
		System.setProperty("swing.aatext", "true");		
		
		//Path for AI models
		Ncnn.modelsPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows")) {
			Ncnn.modelsPath = Ncnn.modelsPath.substring(1, Ncnn.modelsPath.length() - 1);
		} else {
			Ncnn.modelsPath = Ncnn.modelsPath.substring(0, Ncnn.modelsPath.length() - 1);
		}
		Ncnn.modelsPath = Ncnn.modelsPath.substring(0, (int) (Ncnn.modelsPath.lastIndexOf("/"))).replace("%20", " ") + "/Library/models";

		//Checking java x86 or arm version
		Utils.getJavaArch();

		//Direct command args
		File presetFile = null;
		boolean autoStart = false;

		if (args != null && args.length > 0)
		{
		    for (int i = 0; i < args.length; i++) 
		    {
		        String arg = args[i];		        
		        if ("--preset".equals(arg))
		        {
		            // Check if there is a next argument for the filename
		            if (i + 1 < args.length)
		            {
		                presetFile = new File(args[i + 1]);
		                i++; // Skip the next argument since we consumed it as the filename
		            }
		            else
		                System.err.println("Error: The --preset option requires a file as an argument.");
		           
		        }
		        else if ("--autostart".equals(arg))
		        {
		            autoStart = true;
		        }
		        else if (new File(arg).getName().contains(".enc") == false) // If it's not a flag, treat it as a dropped file/regular argument
		        {
		            droppedFiles.add(arg);
		        }
		    }
		}
		
		Utils.setLanguage();
		SplashRenderer.increment();
		Utils.loadThemes();
		SplashRenderer.increment();

		//Documents
		if (documents.exists() == false) {
			//Do not create it if the location is different
			if (settingsXML.exists() == false)
				documents.mkdirs();

			if (Functions.functionsFolder.exists() == false)
				documents.mkdirs();
		}
		
		new Main().initializeUI(presetFile != null);

		ImageIO.setUseCache(false); //IMPORTANT use RAM instead of HDD cache
		
		//Load preset
		if (presetFile != null)
		{
			fileList.setSelectedIndex(0);
			
			VideoPlayerUtils.setMedia();
			try {
				while (VideoPlayerUtils.loadMedia == null) //Wait for the process to initialize
					Thread.sleep(1);
				
				VideoPlayerUtils.loadMedia.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			Utils.loadSettings(presetFile);
		}
		
		//Autostart		
		if (autoStart && !droppedFiles.isEmpty() && presetFile != null)
		{
			try {
				while (Utils.loadEncFile == null) //Wait for the process to initialize
					Thread.sleep(1);
				
				Utils.loadEncFile.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			btnStart.doClick();
		}
	}

	public void initializeUI(boolean presetFile) {		
		
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
			desktop.setAboutHandler(e -> {

				Image logo = new ImageIcon(getClass().getClassLoader().getResource("resources/icon.png")).getImage();
				Image newimg = logo.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
				ImageIcon icon = new ImageIcon(newimg);

				JOptionPane.showMessageDialog(null, "Shutter Encoder v" + actualVersion + System.lineSeparator()
						+ language.getProperty("lblCrParPaul"), "About", JOptionPane.PLAIN_MESSAGE, icon);
			});
		}
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		taskBarHeight = (int) (dim.getHeight() - winSize.height);

		frame.getContentPane().setBackground(Utils.background);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Shutter Encoder");
		frame.setBackground(Utils.background);
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		
		if (minHeight > winSize.height)
		{
			minHeight = 650;
			extendedWidth = 1100;
			
			frame.setSize(extendedWidth, minHeight);
			frame.setLocation(dim.width / 2 - frame.getSize().width / 2, (dim.height - taskBarHeight) / 2 - frame.getSize().height / 2);
		}
		else
		{
			frame.setSize(extendedWidth, minHeight);
			frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		}
		
		frame.setMinimumSize(new Dimension(332, 650));
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45, 45, 45)));
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
		shape1.add(shape2);
		frame.setShape(shape1);

		if (System.getProperty("os.name").contains("Mac") == false)
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/icon.png")).getImage());

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
					if (VideoPlayerUI.player != null)
						VideoPlayerUI.resizeAll();
				}
				else
					UIController.resizeAll(frame.getWidth(), 0);
			}
		});

		soundURL = this.getClass().getClassLoader().getResource("resources/complete.wav");
		soundErrorURL = this.getClass().getClassLoader().getResource("resources/error.wav");

		frame.getRootPane().setDefaultButton(btnStart);

		frame.addWindowStateListener(new WindowStateListener() {

			@SuppressWarnings("static-access")
			@Override
			public void windowStateChanged(WindowEvent arg0) {

				if (frame.getState() == frame.NORMAL && ReducedWindow.frame != null) {
					ReducedWindow.frame.setVisible(false);
					frame.toFront();
				}

			}

		});

		//Settings
		Settings.txtThreads.setText("0");
		Settings.txtImageDuration.setText("10");

		//Initialize FFmpeg path
		FFmpeg.getFFmpegPath();
		
		//Initialize Python path
		Python.getPythonPath();
				
		//Check CPU
		LibraryUtils.checkCPUInfo();
		
		//Check GPUs
		LibraryUtils.checkGPUAvailable();
		
		//Get Vulkan compatible GPU
		LibraryUtils.getVulkanGPUIndex();
		
		//Load panels
		TopBar.topBar();
		SplashRenderer.increment();
		
		StatusBar.statusBar();
		SplashRenderer.increment();
		
		ChooseFiles.grpChooseFiles();
		SplashRenderer.increment();
		
		ChooseFunction.grpChooseFunction();
		SplashRenderer.increment();
		
		Destination.grpDestination();
		SplashRenderer.increment();
		
		Progress.grpProgress();
		SplashRenderer.increment();
		
		Scaling.grpScaling();
		SplashRenderer.increment();
		
		ImageFilter.grpImageFilter();
		SplashRenderer.increment();
		
		SetAudio.grpSetAudio();
		SplashRenderer.increment();
		
		Audio.grpAudio();
		SplashRenderer.increment();
		
		Advanced.grpAdvanced();
		SplashRenderer.increment();
		
		ImageAdjustement.grpImageAdjustement();
		SplashRenderer.increment();
		
		Crop.grpCrop();
		SplashRenderer.increment();
		
		SetTimecode.grpSetTimecode();
		SplashRenderer.increment();
		
		Overlay.grpOverlay();
		SplashRenderer.increment();
		
		Subtitles.grpSubtitles();
		SplashRenderer.increment();
		
		Watermark.grpWatermark();
		SplashRenderer.increment();
		
		Colorimetry.grpColorimetry();
		SplashRenderer.increment();
		
		Corrections.grpCorrections();
		SplashRenderer.increment();
		
		Transitions.grpTransitions();
		SplashRenderer.increment();
		
		ImageSequence.grpImageSequence();
		SplashRenderer.increment();
		
		Bitrate.grpBitrate();
		SplashRenderer.increment();
		
		FileInformation.grpFileInformation();
		SplashRenderer.increment();
		
		Reset.btnReset();
		SplashRenderer.increment();
		
		//Load the video player finally
		new VideoPlayerUI();
		SplashRenderer.increment();
		
		settingsScrollBar = new JScrollBar();
		settingsScrollBar.setVisible(false);
		settingsScrollBar.setValue(45);
		settingsScrollBar.setMaximum(100);
		settingsScrollBar.setBackground(Utils.background);
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

		settingsScrollBar.addAdjustmentListener(new AdjustmentListener() {

			public void adjustmentValueChanged(AdjustmentEvent ae) {

				if (scrollThread == null || scrollThread.isAlive() == false)
				{
					scrollThread = new Thread(new Runnable() {

						@Override
						public void run() {

							if (allowScrolling && settingsScrollBar.isVisible()) {
								while (allowScrolling) {
									int i = Math.round((float) (45 - settingsScrollBar.getValue()) / 3);

									// On récupère le groupe qui est le plus haut
									JPanel top;

									if (grpResolution.isVisible()) {
										top = grpResolution;
									} else if (grpSetTimecode.isVisible()) {
										top = grpSetTimecode;
									} else if (grpSetAudio.isVisible()) {
										top = grpSetAudio;
									} else {
										top = grpAudio;
									}

									if (UIController.extendSectionsIsRunning == false)
									{
										if (comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
										&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31
										|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
										&& Settings.btnDisableAnimations.isSelected()
										&& top.getY() < 30)
										{
											// Empêche de faire un scroll vers le bas pour ne pas dépasser la position
											// minimale de top
											if (i < 0 && Settings.btnDisableAnimations.isSelected()
													&& frame.getSize().getHeight()
															- (btnReset.getLocation().y + btnReset.getHeight()) >= 31) {
												i = 0;
											}

											// Pré calcul
											if (top.getY() + i >= grpChooseFiles.getY() && i > 0) {
												i = grpChooseFiles.getY() - top.getY();
											}

											if (frame.getSize().getHeight()
													- (btnReset.getLocation().y + i + btnReset.getHeight()) >= 31
													&& i < 0) {
												if (i < frame.getSize().getHeight()
														- (btnReset.getLocation().y + btnReset.getHeight()))
													i = (int) (frame.getSize().getHeight()
															- (btnReset.getLocation().y + btnReset.getHeight()) - 31);
												else
													i = 0;
											}

											grpResolution.setLocation(grpResolution.getLocation().x,
													grpResolution.getLocation().y + i);
											grpBitrate.setLocation(grpBitrate.getLocation().x,
													grpBitrate.getLocation().y + i);
											grpSetAudio.setLocation(grpSetAudio.getLocation().x,
													grpSetAudio.getLocation().y + i);
											grpAudio.setLocation(grpAudio.getLocation().x,
													grpAudio.getLocation().y + i);
											grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + i);
											grpOverlay.setLocation(grpOverlay.getLocation().x,
													grpOverlay.getLocation().y + i);
											grpSubtitles.setLocation(grpSubtitles.getLocation().x,
													grpSubtitles.getLocation().y + i);
											grpWatermark.setLocation(grpWatermark.getLocation().x,
													grpWatermark.getLocation().y + i);
											grpColorimetry.setLocation(grpColorimetry.getLocation().x,
													grpColorimetry.getLocation().y + i);
											grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x,
													grpImageAdjustement.getLocation().y + i);
											grpCorrections.setLocation(grpCorrections.getLocation().x,
													grpCorrections.getLocation().y + i);
											grpTransitions.setLocation(grpTransitions.getLocation().x,
													grpTransitions.getLocation().y + i);
											grpImageSequence.setLocation(grpImageSequence.getLocation().x,
													grpImageSequence.getLocation().y + i);
											grpImageFilter.setLocation(grpImageFilter.getLocation().x,
													grpImageFilter.getLocation().y + i);
											grpSetTimecode.setLocation(grpSetTimecode.getLocation().x,
													grpSetTimecode.getLocation().y + i);
											grpAdvanced.setLocation(grpAdvanced.getLocation().x,
													grpAdvanced.getLocation().y + i);
											btnReset.setLocation(btnReset.getLocation().x,
													btnReset.getLocation().y + i);
										}
									}

									if ((frame.getSize().getHeight()
											- (btnReset.getLocation().y + btnReset.getHeight()) >= 31)
											&& top.getY() == 30) {
										settingsScrollBar.setVisible(false);
									}

									long startTime = System.nanoTime();

									// Slow down the loop
									while (System.nanoTime() - startTime < 1000000) {
										try {
											Thread.sleep(0);
										} catch (InterruptedException e) {}
									}
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

				if (Settings.modelIsSaving)
					return;
				
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					UIController.changeFunction(true);
					UIController.changeFilters();

					if (Settings.btnDisableVideoPlayer.isSelected() == false) {
						if (Main.comboFonctions.getSelectedItem().toString().equals(
								Main.language.getProperty("functionCut")) && cutKeyframesIsDisplayed == false) {
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"),
									comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							cutKeyframesIsDisplayed = true;
						} else if (Main.comboFonctions.getSelectedItem().toString()
								.equals(Main.language.getProperty("functionRewrap"))
								&& rewrapKeyframesIsDisplayed == false) {
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"),
									comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							rewrapKeyframesIsDisplayed = true;
						} else if (Main.comboFonctions.getSelectedItem().toString()
								.equals(Main.language.getProperty("functionConform"))
								&& conformKeyframesIsDisplayed == false) {
							JOptionPane.showMessageDialog(frame, language.getProperty("cutOnKeyframesOnly"),
									comboFonctions.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
							conformKeyframesIsDisplayed = true;
						}
					}

					if (VideoPlayerUI.btnPlay.isVisible() && list.getSize() > 0) {
						VideoPlayerUI.btnPlay.requestFocus();
					} else {
						frame.requestFocus();
					}
					
					//Install demucs
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionSeparation")))
					{
						Demucs.checkDemucs();						
					}
						
					//Whisper model
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionTranscribe")))
					{		
						new Whisper();
					}
					
					//Shutter Anonymizer
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionBlurFaces")))
					{		
						new Anonymizer();
					}
					
					//Install deoldify
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionColorize")))
					{
						Deoldify.checkDeoldify();						
					}
					
					//Install Background Remover
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionBackgroundRemover")))
					{
						BackgroundRemover.checkBackgroundRemover();
					}
				}
			}
		});
		
		// Global hardware listeners
		HardwareListener.keyboardListener();
		HardwareListener.mouseListener();

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

		// File drop Application
		if (droppedFiles.isEmpty() == false) {
			for (String file : droppedFiles) {
				File droppedFiles = new File(file);
				if (droppedFiles.isFile()) {
					int s = droppedFiles.toString().lastIndexOf('.');
					String ext = droppedFiles.toString().substring(s);

					if (ext.equals(".enc") == false && droppedFiles.isHidden() == false && droppedFiles.getName().contains("."))
						list.addElement(droppedFiles.toString());
				} else
					Utils.findFiles(droppedFiles.toString());
			}

			addToList.setVisible(false);
			lblFiles.setText(Utils.filesNumber());
		}

		//Load settings
		new Settings();
		SplashRenderer.increment();

		Ytdlp.update();
		Exiftool.run('"' + "" + '"'); // Preload the binary

		// Right_to_left
		if (getLanguage.contains(Locale.of("ar").getDisplayLanguage())) {
			// Frame
			for (Component c : frame.getContentPane().getComponents()) {
				if (c instanceof JPanel) {
					for (Component p : ((JPanel) c).getComponents()) {
						if (p instanceof JCheckBox) {
							p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						}
					}
				}
			}

			// Settings
			for (Component c : Settings.frame.getContentPane().getComponents()) {
				if (c instanceof JCheckBox) {
					c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}

			// Destinations
			for (Component c : grpDestination.getComponents()) {
				if (c instanceof JPanel) {
					for (Component p : ((JPanel) c).getComponents()) {
						if (p instanceof JCheckBox) {
							p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
						}
					}
				} else if (c instanceof JCheckBox) {
					c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}
		}
		
		SplashRenderer.instance.dispose();
		Utils.changeFrameVisibility(frame, false);
		btnStart.requestFocus();
		UIController.changeWidth();

		if (Settings.btnLoadPreset.isSelected() && Settings.comboLoadPreset.getItemCount() > 0 && presetFile == false)
		{
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
						
						if (Settings.btnLoadPreset.isSelected() == false)
							break;
						
					} while (list.getSize() == 0 || FFmpeg.isRunning || FFprobe.isRunning);

					if (Settings.btnLoadPreset.isSelected())
					{
						Main.btnReset.doClick();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Utils.loadSettings(new File(Functions.functionsFolder + "/" + Settings.comboLoadPreset.getSelectedItem()));
					}
				}

			});
			t.start();
		}

		if (Settings.btnDisableUpdate.isSelected() == false)
			Update.newVersion();

		availableMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
	}
}
