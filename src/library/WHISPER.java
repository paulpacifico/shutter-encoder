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

package shutterencoder.library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.others.Console;
import shutterencoder.utils.UnlockExternalApps;
import shutterencoder.utils.Utils;

public class WHISPER {
	
	public static boolean error = false;
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static Process process;
	public static File transcriberApp = null;
	public static File whisperFolder = new File(System.getProperty("user.home") + "/Shutter Transcriber/Library/whisper");
	public static String whisperModel;
	public static File LIBRARY_DIR;
	public static JComboBox<String> comboLanguage;
	public static JCheckBox boxVAD;
	public static JCheckBox boxContext;
	public static JTextField textChars;
	public static JTextField textLines;
	public static JCheckBox boxMultilingual;
	public static JLabel lblPrompt;
	public static JTextField textPrompt;
	public static JLabel lblHotwords;
	public static JTextField textHotwords;	
	
	public static String[][] WHISPER_LANGUAGES = {
	    {"af", "Afrikaans"},
	    {"am", "Amharic"},
	    {"ar", "Arabic"},
	    {"as", "Assamese"},
	    {"az", "Azerbaijani"},
	    {"ba", "Bashkir"},
	    {"be", "Belarusian"},
	    {"bg", "Bulgarian"},
	    {"bn", "Bengali"},
	    {"bo", "Tibetan"},
	    {"br", "Breton"},
	    {"bs", "Bosnian"},
	    {"ca", "Catalan"},
	    {"cs", "Czech"},
	    {"cy", "Welsh"},
	    {"da", "Danish"},
	    {"de", "German"},
	    {"el", "Greek"},
	    {"en", "English"},
	    {"es", "Spanish"},
	    {"et", "Estonian"},
	    {"eu", "Basque"},
	    {"fa", "Persian"},
	    {"fi", "Finnish"},
	    {"fo", "Faroese"},
	    {"fr", "French"},
	    {"gl", "Galician"},
	    {"gu", "Gujarati"},
	    {"haw", "Hawaiian"},
	    {"he", "Hebrew"},
	    {"hi", "Hindi"},
	    {"hr", "Croatian"},
	    {"ht", "Haitian Creole"},
	    {"hu", "Hungarian"},
	    {"hy", "Armenian"},
	    {"id", "Indonesian"},
	    {"is", "Icelandic"},
	    {"it", "Italian"},
	    {"ja", "Japanese"},
	    {"jw", "Javanese"},
	    {"ka", "Georgian"},
	    {"kk", "Kazakh"},
	    {"km", "Khmer"},
	    {"kn", "Kannada"},
	    {"ko", "Korean"},
	    {"la", "Latin"},
	    {"lb", "Luxembourgish"},
	    {"ln", "Lingala"},
	    {"lo", "Lao"},
	    {"lt", "Lithuanian"},
	    {"lv", "Latvian"},
	    {"mg", "Malagasy"},
	    {"mi", "Maori"},
	    {"mk", "Macedonian"},
	    {"ml", "Malayalam"},
	    {"mn", "Mongolian"},
	    {"mr", "Marathi"},
	    {"ms", "Malay"},
	    {"mt", "Maltese"},
	    {"my", "Burmese"},
	    {"ne", "Nepali"},
	    {"nl", "Dutch"},
	    {"nn", "Nynorsk"},
	    {"no", "Norwegian"},
	    {"oc", "Occitan"},
	    {"pa", "Punjabi"},
	    {"pl", "Polish"},
	    {"ps", "Pashto"},
	    {"pt", "Portuguese"},
	    {"ro", "Romanian"},
	    {"ru", "Russian"},
	    {"sa", "Sanskrit"},
	    {"sd", "Sindhi"},
	    {"si", "Sinhala"},
	    {"sk", "Slovak"},
	    {"sl", "Slovenian"},
	    {"sn", "Shona"},
	    {"so", "Somali"},
	    {"sq", "Albanian"},
	    {"sr", "Serbian"},
	    {"su", "Sundanese"},
	    {"sv", "Swedish"},
	    {"sw", "Swahili"},
	    {"ta", "Tamil"},
	    {"te", "Telugu"},
	    {"tg", "Tajik"},
	    {"th", "Thai"},
	    {"tk", "Turkmen"},
	    {"tl", "Tagalog"},
	    {"tr", "Turkish"},
	    {"tt", "Tatar"},
	    {"uk", "Ukrainian"},
	    {"ur", "Urdu"},
	    {"uz", "Uzbek"},
	    {"vi", "Vietnamese"},
	    {"yi", "Yiddish"},
	    {"yo", "Yoruba"},
	    {"zh", "Chinese"}
	};

	public static String[][] WHISPER_MODELS = {
		{"tiny.en", "models--Systran--faster-whisper-tiny.en"},
	    {"tiny", "models--Systran--faster-whisper-tiny"},
	    {"base.en", "models--Systran--faster-whisper-base.en"},
	    {"base", "models--Systran--faster-whisper-base"},
	    {"small.en", "models--Systran--faster-whisper-small.en"},
	    {"small", "models--Systran--faster-whisper-small"},
	    {"medium.en", "models--Systran--faster-whisper-medium.en"},
	    {"medium", "models--Systran--faster-whisper-medium"},
	    {"large-v1", "models--Systran--faster-whisper-large-v1"},
	    {"large-v2", "models--Systran--faster-whisper-large-v2"},
	    {"large-v3", "models--Systran--faster-whisper-large-v3"},
	    {"large", "models--Systran--faster-whisper-large-v3"},
	    {"distil-large-v2", "models--Systran--faster-distil-whisper-large-v2"},
	    {"distil-medium.en", "models--Systran--faster-distil-whisper-medium.en"},
	    {"distil-small.en", "models--Systran--faster-distil-whisper-small.en"},
	    {"distil-large-v3", "models--Systran--faster-distil-whisper-large-v3"},
	    {"distil-large-v3.5", "models--distil-whisper--distil-large-v3.5-ct2"},
	    {"large-v3-turbo", "models--mobiuslabsgmbh--faster-whisper-large-v3-turbo"},
	    {"turbo", "models--mobiuslabsgmbh--faster-whisper-large-v3-turbo"}
	};
	
	public WHISPER() {
				
		if (System.getProperty("os.name").contains("Windows"))
		{
			transcriberApp = new File("C:\\Program Files\\Shutter Transcriber\\Shutter Transcriber.exe");	
			
			if (transcriberApp.exists() == false)
				transcriberApp = null;
		}
		else if (System.getProperty("os.name").contains("Mac"))
		{
			transcriberApp = new File("/Applications/Shutter Transcriber.app");
			
			if (transcriberApp.exists() == false)
				transcriberApp = null;
		}
		
		if (transcriberApp != null)
		{
			String version = getAppVersion(); // Uses the 'run' method from previous example
	        
	        if (isVersionAtLeast(version, "1.5") == false)
	        {
	        	JOptionPane.showMessageDialog(Shutter.frame, "Shutter Transcriber >=1.5 is required." + System.lineSeparator() + "Please update your app!",
	        	"Shutter Transcriber", JOptionPane.ERROR_MESSAGE);
	        	
	        	if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranscribe")))
					Shutter.comboFonctions.setSelectedItem("");
	        	
	        	UIController.changeWidth(false);
	        }
	        else if (checkAccount())
			{
				setLibraryDir();
				WHISPER.selectModel();
				deleteOldInstall();
			}
			else
			{
				Shutter.comboFonctions.setSelectedItem("");
				try {
					Desktop.getDesktop().open(transcriberApp);
				} catch (IOException e) {}
			}	
		}
		else
		{							
			ImageIcon app = new ImageIcon(getClass().getClassLoader().getResource("resources/Shutter Transcriber.png"));
			Image scaled = app.getImage().getScaledInstance(420, -1, Image.SCALE_SMOOTH); 
	        ImageIcon icon = new ImageIcon(scaled);
	        
	        JLabel background = new JLabel(icon);
	        background.setLayout(new BorderLayout());
	        
	        JLabel text = new JLabel("<html>" + Shutter.language.getProperty("shutterTranscriberRequired") + "<br>" + Shutter.language.getProperty("wantToDownload") + "</html>", SwingConstants.CENTER);
	        Image transcriber = new ImageIcon(getClass().getClassLoader().getResource("resources/icon_transcriber.png")).getImage();
	        Image newimg = transcriber.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
			ImageIcon logo = new ImageIcon(newimg);
			text.setIcon(logo);
	        text.setOpaque(false);
	        text.setForeground(Color.WHITE);
	        text.setFont(new Font(Shutter.mainFont, Font.BOLD, 14));
	        
	        background.add(text, BorderLayout.CENTER);

			int q = JOptionPane.showConfirmDialog(Shutter.frame, background,
					Shutter.language.getProperty("functionTranscribe"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (q == JOptionPane.YES_OPTION)
			{
				if (System.getProperty("os.name").contains("Windows"))
				{
					Thread download = new Thread(new Runnable() {

						@Override
						public void run() {
							
							try {
								Desktop.getDesktop().browse(new URI("https://www.paypal.com/ncp/payment/8BT2G3JWLLZPU"));
														
								File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();

								//Waiting the file to exists				
								File fileToUnlock = UnlockExternalApps.waitForFile(Paths.get(desktopDir.getAbsolutePath()), Paths.get(UnlockExternalApps.checkDownloadLocation()), "Shutter Transcriber");

								//Unlock file
						        ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", "Unblock-File -Path '" + fileToUnlock + "'");
						        pb.start();
						        
						        //Open the installer
								Desktop.getDesktop().open(fileToUnlock);
								 
							} catch (Exception e) {}	
						}				
					});				
					download.start();
				}
				else
				{
					try {
						Desktop.getDesktop().browse(new URI("https://www.paypal.com/ncp/payment/WG4KV7R49DMY6"));
					} catch (IOException | URISyntaxException er) {}	
				}															
			}
			else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranscribe")))
			{
				Shutter.comboFonctions.removeItem(Shutter.language.getProperty("functionTranscribe"));
			}
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranscribe")))
				Shutter.comboFonctions.setSelectedItem("");
		}
	}
	
	@SuppressWarnings("resource")
	private boolean checkAccount() {
		
		try {
			
			File documents = new File(System.getProperty("user.home") + "/Shutter Transcriber");
			File settingsXML = new File(documents + "/settings.xml");
			String account = null;
			
			if (settingsXML.exists())
			{
				File fXmlFile = settingsXML;
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
				
				nList = doc.getElementsByTagName("Account");
				
				for (int temp = 0; temp < nList.getLength(); temp++)
				{								
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element eElement = (Element) nNode;
						
						//Retrieving e-mail
						if (eElement.getElementsByTagName("Email").item(0).getFirstChild() != null)
							account = eElement.getElementsByTagName("Email").item(0).getFirstChild().getTextContent();
					}
				}
				
				File jarFile = transcriberApp;
				if (System.getProperty("os.name").contains("Mac"))
				{
					jarFile = new File(transcriberApp.toString() + "/Contents/app/Shutter Transcriber.jar");
				}
				
				URLClassLoader loader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, getClass().getClassLoader());
		
				Class<?> authClass = loader.loadClass("security.AuthService");
				Object authService = authClass.getDeclaredConstructor().newInstance();
		
				// Call the login method via reflection
				Method loginMethod = authClass.getMethod("login", String.class);
				Object result = loginMethod.invoke(authService, account);
				
				if (result.toString().equals("SUCCESS"))
				{
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
		
	public static void setLibraryDir() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			LIBRARY_DIR = new File(transcriberApp.getParent() + "\\Library");
		}	
		else
			LIBRARY_DIR = new File(transcriberApp + "/Contents/app/Library");		
	}
	
	private String getAppVersion() {
		
		try {	        	
             	
        	if (System.getProperty("os.name").contains("Windows"))
        	{
        		return checkCurrentVersion("powershell", "-command", "$v = (Get-Item '" + transcriberApp + "').VersionInfo; " + "'{0}.{1}' -f $v.FileMajorPart, $v.FileMinorPart");
            }
        	else if (System.getProperty("os.name").contains("Mac"))
        	{
                return checkCurrentVersion("defaults", "read", transcriberApp + "/Contents/Info.plist", "CFBundleShortVersionString");
            }
        	
        } catch (Exception e) {}
		
		return "Unknown";		
	}
	
	private void deleteOldInstall() {
		
		if (whisperFolder.exists() && whisperFolder.isDirectory())
		{
		    for (File file : whisperFolder.listFiles()) {

		        if (file.isDirectory() && file.getName().equals("models"))
		        	continue;

		        FileUtils.deleteQuietly(file);
		    }
		}
	}
	
	private static String checkCurrentVersion(String... cmd) throws Exception {
        Process p = new ProcessBuilder(cmd).start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = r.readLine();
            return (line != null) ? line.trim() : "Unknown";
        }
    }
	
	public static boolean isVersionAtLeast(String current, String required) {
        if (current == null || current.equals("Unknown")) return false;

        String[] curParts = current.split("\\.");
        String[] reqParts = required.split("\\.");
        int length = Math.max(curParts.length, reqParts.length);

        for (int i = 0; i < length; i++) {
            int cur = i < curParts.length ? Integer.parseInt(curParts[i].replaceAll("[^0-9]", "")) : 0;
            int req = i < reqParts.length ? Integer.parseInt(reqParts[i].replaceAll("[^0-9]", "")) : 0;

            if (cur < req) return false;
            if (cur > req) return true;
        }
        return true; // They are equal
    }

	public static String getWhisperModel() {
		
		File modelFolder = new File(whisperFolder + "/models");
		if (modelFolder.exists() == false)
		{
			modelFolder.mkdir();
		}
		
		if (System.getProperty("os.name").contains("Windows"))
		{			
			//Moving old models path
			try {
				moveModels(Paths.get("Library/models"), Paths.get(modelFolder.toString()));
			} catch (IOException e) {}
			
			return modelFolder.toString().replace("/", "\\");
		}
		else
			return modelFolder.toString();
	}
	
	public static void moveModels(Path src, Path dst) throws IOException {

        if (Files.exists(src) == false)
        	return;
        
        Files.createDirectories(dst);

        Files.walkFileTree(src, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes a)
                    throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes a)
                    throws IOException {
                Path t = dst.resolve(src.relativize(file));
                try {
                    Files.move(file, t, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.copy(file, t, StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
	
	public static void selectModel() {
		
        JSlider slider = new JSlider(0, 2, 1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPreferredSize(new Dimension(300, 50));

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("Fast"));
        labels.put(1, new JLabel("Balanced"));
        labels.put(2, new JLabel("Accurate"));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        
		DefaultComboBoxModel<String> languages = new DefaultComboBoxModel<>();
		languages.addElement("auto");
        for (String[] lang : WHISPER.WHISPER_LANGUAGES) {
        	languages.addElement(lang[1].toLowerCase());
        }
		
        JLabel lblLanguage = new JLabel("Language:");
        lblLanguage.setForeground(Utils.c225);
        lblLanguage.setSize(lblLanguage.getPreferredSize().width, 16);
		
		comboLanguage = new JComboBox<String>(languages);
		comboLanguage.setSelectedIndex(0);
		comboLanguage.setSize(120,26);
		
		boxVAD = new JCheckBox("Transcribe speech only");
		boxVAD.setForeground(Utils.c225);
		boxVAD.setSelected(true);
		boxVAD.setSize(boxVAD.getPreferredSize().width, 16);
		
		boxContext = new JCheckBox("Keep context");
		boxContext.setForeground(Utils.c225);
		boxContext.setSelected(true);
		boxContext.setSize(boxContext.getPreferredSize().width, 16);
		
		JLabel lblMaxChars = new JLabel("Max. characters:");
		lblMaxChars.setForeground(Utils.c225);
		lblMaxChars.setSize(lblMaxChars.getPreferredSize().width, 16);
		
		textChars = new JTextField("37");
		textChars.setForeground(Utils.c225);
		textChars.setHorizontalAlignment(SwingConstants.CENTER);
		textChars.setPreferredSize(new Dimension(40, 16));
		
		textChars.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textChars.getText().length() >= 3)
					textChars.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		
		JLabel lblMaxLines = new JLabel("Max. lines:");
		lblMaxLines.setForeground(Utils.c225);
		
		textLines = new JTextField("2");
		textLines.setForeground(Utils.c225);
		textLines.setHorizontalAlignment(SwingConstants.CENTER);
		textLines.setPreferredSize(new Dimension(40, 16));
				
		textLines.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textLines.getText().length() >= 1)
					textLines.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		
		boxMultilingual = new JCheckBox("Multilingual");
		boxMultilingual.setForeground(Utils.c225);
		boxMultilingual.setSelected(false);
		boxMultilingual.setSize(boxMultilingual.getPreferredSize().width + 7, 16);

		JLabel lblPrompt = new JLabel("Transcription context:");
		lblPrompt.setForeground(Utils.c225);
		
		textPrompt = new JTextField("");
		textPrompt.setForeground(Utils.c225);
		textPrompt.setHorizontalAlignment(SwingConstants.CENTER);	
		textPrompt.setPreferredSize(new Dimension(100, 16));		
				
		JLabel lblHotwords = new JLabel("Keywords / Names:");
		lblHotwords.setForeground(Utils.c225);
		
		textHotwords = new JTextField("");
		textHotwords.setName("textHotwords");
		textHotwords.setForeground(Utils.c225);
		textHotwords.setHorizontalAlignment(SwingConstants.CENTER);
		textHotwords.setPreferredSize(new Dimension(100, 16));		
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(slider);
		
		JPanel rightPanel = new JPanel(new FlowLayout());
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		rightPanel.add(lblLanguage);
		rightPanel.add(comboLanguage);
		panel.add(rightPanel, BorderLayout.EAST);

		JPanel optionsFirstLine = new JPanel(new FlowLayout());
		optionsFirstLine.add(boxVAD);
		optionsFirstLine.add(boxContext);
		optionsFirstLine.add(lblMaxChars);
		optionsFirstLine.add(textChars);
		optionsFirstLine.add(lblMaxLines);
		optionsFirstLine.add(textLines);

		JPanel optionsSecondLine = new JPanel(new FlowLayout());
		optionsSecondLine.add(boxMultilingual);
		optionsSecondLine.add(lblPrompt);
		optionsSecondLine.add(textPrompt);
		optionsSecondLine.add(lblHotwords);
		optionsSecondLine.add(textHotwords);

		JPanel optionsContainer = new JPanel();
		optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
		optionsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		optionsContainer.add(optionsFirstLine);
		optionsContainer.add(optionsSecondLine);

		panel.add(optionsContainer, BorderLayout.SOUTH);
        
        Object[] options = { Shutter.language.getProperty("btnApply") };
        JOptionPane.showOptionDialog(Shutter.frame, panel, Shutter.language.getProperty("functionTranscribe"), 
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (labels.get(slider.getValue()).getText().equals("Fast"))
		{	
			whisperModel = "small";
		}
		else if (labels.get(slider.getValue()).getText().equals("Balanced"))
		{	
			whisperModel = "turbo";
		}
		else if (labels.get(slider.getValue()).getText().equals("Accurate"))
		{	
			whisperModel = "large-v3";
		}
	}
	
	public static File checkIfModelExists() {
		
        for (String[] model : WHISPER_MODELS) 
		{
			if (whisperModel.equals(model[0]))
			{
				File file = new File(getWhisperModel() + File.separator + model[1]);
				
				if (file.exists())
					return file;				
			}							    
		}
        
		return null;
	}   
	
	public static void deleteIncompleteModel() {
		
		//AI model incomplete
		if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("downloadingAIModel")))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
	           @Override
	           public void run() {
	        	   
	        	   try {
	   					if (checkIfModelExists() != null)			
	   						FileUtils.deleteDirectory(checkIfModelExists());		
	   				} catch (Exception e) {}
	        	   
	        	   Shutter.progressBar.setIndeterminate(false);
	        	   Shutter.progressBar.setStringPainted(true);			        	   
	           }
			});
		}
	
	}
	
	@SuppressWarnings("deprecation")
	public static String getComputeType() {
		
	    String computeType = "float32"; // Default fallback

	    if (System.getProperty("os.name").contains("Windows")) {
	        try {
	            Process process;
	            // Handle version parsing carefully (String to Double can be locale-sensitive)
	            String osVerStr = System.getProperty("os.version");
	            double version = 0;
	            try {
	                version = Double.parseDouble(osVerStr);
	            } catch (NumberFormatException e) {
	                version = 10.0; // Assume modern if parsing fails
	            }

	            if (version >= 10.0) {
	                process = Runtime.getRuntime().exec("powershell -Command \"Get-CimInstance Win32_VideoController | Select-Object -ExpandProperty Name\"");
	            } else {
	                process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
	            }

	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                line = line.trim();
	                if (!line.isEmpty() && !line.toLowerCase().contains("name")) {
	                    String gpuName = line.toUpperCase();

	                    if (gpuName.contains("NVIDIA") || gpuName.contains("GEFORCE") || gpuName.contains("RTX")) {
	                        // Extract the first 4-digit or 3-digit number found in the name
	                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d{3,4}").matcher(gpuName);
	                        
	                        if (m.find()) {
	                            int modelNumber = Integer.parseInt(m.group());

	                            // Logic based on Architecture:
	                            // RTX 50xx, 40xx, 30xx, 20xx -> float16
	                            // GTX 16xx, 10xx and below -> float32
	                            if (modelNumber >= 2000 || gpuName.contains("RTX")) {
	                                computeType = "float16";
	                            } else {
	                                computeType = "float32";
	                            }
	                        } else if (gpuName.contains("RTX")) {
	                            // Catch-all for RTX cards without a detected number
	                            computeType = "float16";
	                        } else {
	                            computeType = "float32";
	                        }
	                        
	                        // We found our primary GPU, we can break
	                        break; 
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "float32";
	        }
	    }
	    return computeType;
	}
	
	public static void run(String wavFile, String fileName) {
		
		error = false;
		Shutter.progressBar.setValue(0);		
		Shutter.btnStart.setEnabled(false);
				
		runProcess = new Thread(new Runnable()  {

		@Override
		public void run() {
				
			try {

				ProcessBuilder processBuilder = new ProcessBuilder();
				if (System.getProperty("os.name").contains("Windows"))
				{							
					processBuilder.command().add(LIBRARY_DIR.toString() + "\\python.exe");
					processBuilder.command().add("-u");
					processBuilder.command().add(LIBRARY_DIR.toString() + "\\bin\\whisper-ctranslate2.exe");
				}	
				else
					processBuilder.command().add(LIBRARY_DIR.toString() + "/whisper-ctranslate2");

				//Verbose
				processBuilder.command().add("--verbose");
				processBuilder.command().add("True");
				
				//Check is GPU is available
				if (System.getProperty("os.name").contains("Windows") && PYTHON.isCudaInstalled())
				{
					if (PYTHON.isTorchCudaInstalled(WHISPER.whisperFolder.toString()) == false)
					{
			        	processBuilder.command().add("--device");
			        	processBuilder.command().add("cpu");			        
					}
					else
					{
						processBuilder.command().add("--compute_type");
						processBuilder.command().add(getComputeType());
					}
				}
				
				//Model				
				processBuilder.command().add("--model_dir");
				processBuilder.command().add(getWhisperModel());				
				
				processBuilder.command().add("--model");
				processBuilder.command().add(whisperModel);
				if (whisperModel.equals("small"))
				{	
					processBuilder.command().add("--beam_size");
					processBuilder.command().add("5");
					processBuilder.command().add("--best_of");
					processBuilder.command().add("5");
				}
				else if (whisperModel.equals("turbo"))
				{	
					processBuilder.command().add("--beam_size");
					processBuilder.command().add("3");
					processBuilder.command().add("--best_of");
					processBuilder.command().add("3");
				}
				else if (whisperModel.equals("large-v3"))
				{	
					processBuilder.command().add("--beam_size");
					processBuilder.command().add("1");
					processBuilder.command().add("--best_of");
					processBuilder.command().add("1");
				}
				
				//Language
				if (comboLanguage.getSelectedIndex() > 0)
				{
					processBuilder.command().add("--language");
					processBuilder.command().add(WHISPER_LANGUAGES[comboLanguage.getSelectedIndex() - 1][0]);
				}
				
				//VAD
				if (boxVAD.isSelected())
				{
					processBuilder.command().add("--vad_filter");
					processBuilder.command().add("True");
					
					if (boxMultilingual.isSelected())
					{
						processBuilder.command().add("--vad_max_speech_duration_s");
						processBuilder.command().add("7");
					}
				}
								
				//Preserve context
				if (boxContext.isSelected() == false)
				{
					processBuilder.command().add("--condition_on_previous_text");
					processBuilder.command().add("False");
				}
				
				//Enable multilingual
				if (boxMultilingual.isSelected())
				{
					processBuilder.command().add("--multilingual");
					processBuilder.command().add("True");
				}
				
				//Initial prompt
				if (textPrompt.getText().trim().isEmpty() == false)
				{
					processBuilder.command().add("--initial_prompt");
					processBuilder.command().add('"' + textPrompt.getText() + '"');
				}
				
				//Hotwords
				if (textHotwords.getText().trim().isEmpty() == false)
				{
					processBuilder.command().add("--hotwords");
					processBuilder.command().add('"' + textHotwords.getText() + '"');
				}
					
				//Output format
				if (Shutter.comboFilter.getSelectedItem().toString().equals(".srt"))
				{
					processBuilder.command().add("--output_format");
					processBuilder.command().add("srt");
					processBuilder.command().add("--word_timestamps");
					processBuilder.command().add("True");
					
				}							
				else if (Shutter.comboFilter.getSelectedItem().toString().equals(".vtt"))
				{
					processBuilder.command().add("--output_format");
					processBuilder.command().add("vtt");
					processBuilder.command().add("--word_timestamps");
					processBuilder.command().add("True");
				}
				else if (Shutter.comboFilter.getSelectedItem().toString().equals(".txt"))
				{
					processBuilder.command().add("--output_format");
					processBuilder.command().add("txt");
				}
				
				//Input file
				processBuilder.command().add(wavFile);
				
				//Output folder
				processBuilder.command().add("--output_dir");
				processBuilder.command().add(new File(wavFile).getParent());
		        
				processBuilder.redirectErrorStream(true);

				Console.consolePYTHON.append(Shutter.language.getProperty("command") + " " + String.join(" ", processBuilder.command()));	
				
				isRunning = true;	
				process = processBuilder.start();
	            
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));	            	    
	            
	            File model = checkIfModelExists();
	            if (model == null)
            	{
            		Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("downloadingAIModel"));
            		Shutter.progressBar.setIndeterminate(true);
            		Shutter.progressBar.setStringPainted(false);
            	}
	            
	            Console.consolePYTHON.append(System.lineSeparator());
	            
	            String line;	
	            boolean stopReading = false;
	            while ((line = reader.readLine()) != null)
	            {
	            	if (line != null)
	            	{	            		
	            		//Removes warnings due to pyinstaller
	            		if (line.contains("whisper-ctranslate2: error: unrecognized arguments: -B -S -I -c"))
	            		{
	            			stopReading = false;
	            			continue;
	            		}
	            		else if (line.contains("usage: whisper-ctranslate2 [-h]")
	            		|| line.contains("multiprocessing/resource_tracker"))
	            		{
	            			stopReading = true;	            			
	            		}
	            		
	            		if (stopReading)
	            			continue;
	            		
	            		Console.consolePYTHON.append(line + System.lineSeparator());

	            		if (line.contains("Detected language") && line.contains("with probability"))
		            	{
		            		if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("downloadingAIModel")))
		            		{
		            			SwingUtilities.invokeLater(new Runnable()
		    					{
		    			           @Override
		    			           public void run() {
		    			        	   Shutter.progressBar.setIndeterminate(false);
		    			        	   Shutter.progressBar.setStringPainted(true);		
		    			        	   Shutter.lblCurrentEncoding.setText("Transcribing " + fileName);	
		    			           }
		    					});
		            		}
		            	}
	            		else if (line.contains(" --> "))
						{
	            			String s[] = line.split("]");
							String s2[] = s[0].split(" ");
							String s3[] = s2[2].split("\\.");
							String s4[] = s3[0].split(":");
							
							int value = Integer.parseInt(s4[0]) * 60 + Integer.parseInt(s4[1]);
							if (s4.length > 2)
								value = Integer.parseInt(s4[0]) * 3600 + Integer.parseInt(s4[1]) * 60 + Integer.parseInt(s4[2]);
																				
							Shutter.progressBar.setValue(value);
						}		            	
	            	}
	            	
	            	if (Shutter.cancelled)
	            		break;	 
	            }
	            
	            process.waitFor();
	            
	            Console.consolePYTHON.append(System.lineSeparator());	

				isRunning = false;	        
				
				} catch (IOException | InterruptedException e) {
					error = true;
				}
				finally {					
					deleteIncompleteModel();
				}
								 
			}				
		});		
		runProcess.start();
	}
}