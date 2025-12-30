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

package library;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import javax.swing.BorderFactory;
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
import javax.swing.filechooser.FileSystemView;

import org.json.JSONObject;

import application.Console;
import application.Shutter;
import application.Utils;

public class WHISPER {
	
	public static boolean error = false;
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static Process process;
	private static File transcriberApp = null;
	private static File whisperFolder = new File(System.getProperty("user.home") + "/Shutter Transcriber/Library/whisper");
	public static String whisperModel;
	public static File whisper;
	public static JComboBox<String> comboLanguage;
	public static JCheckBox boxVAD;
	public static JCheckBox boxContext;
	public static JTextField textChars;
	public static JTextField textLines;
	
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
	
	public WHISPER() {
				
		if (System.getProperty("os.name").contains("Windows"))
		{
			transcriberApp = new File("C:\\Program Files\\Shutter Transcriber\\Shutter Transcriber.exe");							
			if (transcriberApp.exists())
			{						
				whisper = new File(whisperFolder + "/bin/whisper-ctranslate2.exe");
			}
			else
				transcriberApp = null;
		}
		else if (System.getProperty("os.name").contains("Mac"))
		{
			transcriberApp = new File("/Applications/Shutter Transcriber.app");
			if (transcriberApp.exists())
			{
				whisper = new File(whisperFolder + "/bin/whisper-ctranslate2");
			}
			else
				transcriberApp = null;
		}
		else //Linux
		{
			transcriberApp = new File(FFMPEG.PathToFFMPEG);			
			whisper = new File(whisperFolder + "/bin/whisper-ctranslate2");		
		}
		
		if (transcriberApp != null)
		{
			WHISPER.selectModel();
		}
		else
		{							
			ImageIcon app = new ImageIcon(getClass().getClassLoader().getResource("contents/Shutter Transcriber.png"));
			Image scaled = app.getImage().getScaledInstance(420, -1, Image.SCALE_SMOOTH); 
	        ImageIcon icon = new ImageIcon(scaled);
	        
	        JLabel background = new JLabel(icon);
	        background.setLayout(new BorderLayout());
	        
	        JLabel text = new JLabel("<html>" + Shutter.language.getProperty("shutterTranscriberRequired") + "<br>" + Shutter.language.getProperty("wantToDownload") + "</html>", SwingConstants.CENTER);
	        Image transcriber = new ImageIcon(getClass().getClassLoader().getResource("contents/icon_transcriber.png")).getImage();
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
								File fileToUnlock = waitForFile(Paths.get(desktopDir.getAbsolutePath()), Paths.get(checkDownloadLocation()), "Shutter Transcriber");

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
	
	public static String getWhisperModel() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{			
			return transcriberApp + "\\Library\\models";
		}
		else
			return transcriberApp + "/Contents/Resources/Library/models";
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
		
		boxVAD = new JCheckBox("Voice Activation Detection");
		boxVAD.setForeground(Utils.c225);
		boxVAD.setSelected(true);
		boxVAD.setSize(boxVAD.getPreferredSize().width, 16);
		
		boxContext = new JCheckBox("Preserve context");
		boxContext.setForeground(Utils.c225);
		boxContext.setSelected(true);
		boxContext.setSize(boxContext.getPreferredSize().width, 16);
		
		JLabel lblMaxChars = new JLabel("Max. chars:");
		lblMaxChars.setForeground(Utils.c225);
		lblMaxChars.setSize(lblMaxChars.getPreferredSize().width, 16);
		
		textChars = new JTextField("37");
		textChars.setForeground(Utils.c225);
		textChars.setHorizontalAlignment(SwingConstants.CENTER);
		
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
		textLines.setSize(textChars.getSize());
				
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
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(slider);
		
		JPanel rightPanel = new JPanel(new FlowLayout());
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		rightPanel.add(lblLanguage);
		rightPanel.add(comboLanguage);
		panel.add(rightPanel, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel(new FlowLayout());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		bottomPanel.add(boxVAD);
		bottomPanel.add(boxContext);
		bottomPanel.add(lblMaxChars);
		bottomPanel.add(textChars);
		bottomPanel.add(lblMaxLines);
		bottomPanel.add(textLines);
		panel.add(bottomPanel, BorderLayout.SOUTH);
        
        Object[] options = { Shutter.language.getProperty("btnApply") };
        JOptionPane.showOptionDialog(Shutter.frame, panel, Shutter.language.getProperty("functionTranscribe"), 
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        whisperModel = labels.get(slider.getValue()).getText();	
	}
	
	public static void patchFFmpeg() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			try {

				File run = new File(whisperFolder.toString() + "/whisper/audio.py");
				if (run.exists())
				{					
					//Edit the _run.py to add current FFmpeg path
			        String content = Files.readString(run.toPath(), StandardCharsets.UTF_8);		
			        content = content.replace('"' + "ffmpeg" + '"', "r" + '"' + FFMPEG.PathToFFMPEG.replace("/", "\\") + '"');		
			        Files.writeString(run.toPath(), content, StandardCharsets.UTF_8);
			    }
				
			} catch (Exception e) {}
		}
	}
	
	public static void run(String wavFile, String fileName) {
		
		error = false;
		Shutter.progressBar1.setValue(0);		
		Shutter.btnStart.setEnabled(false);
		
		getWhisperModel();
		patchFFmpeg();
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
								
				try {
					
					ProcessBuilder processWHISPER = new ProcessBuilder();
					
					if (System.getProperty("os.name").contains("Windows"))
					{
						processWHISPER.command().add(whisperFolder.toString() + "/python.exe");						
					}
					else				
						processWHISPER.command().add(whisperFolder.toString() + "/bin/python3");
										
					//Run Whisper module				
					processWHISPER.command().add("-u");
					processWHISPER.command().add(whisper.toString());
					
					//Model				
					processWHISPER.command().add("--model_dir");
					processWHISPER.command().add(getWhisperModel());				
					
					processWHISPER.command().add("--model");
					if (whisperModel.equals("Fast"))
					{	
						processWHISPER.command().add("small");
						processWHISPER.command().add("--beam_size");
						processWHISPER.command().add("5");
						processWHISPER.command().add("--best_of");
						processWHISPER.command().add("5");
					}
					else if (whisperModel.equals("Balanced"))
					{	
						processWHISPER.command().add("turbo");
						processWHISPER.command().add("--beam_size");
						processWHISPER.command().add("3");
						processWHISPER.command().add("--best_of");
						processWHISPER.command().add("3");
					}
					else if (whisperModel.equals("Accurate"))
					{	
						processWHISPER.command().add("large-v3");
						processWHISPER.command().add("--beam_size");
						processWHISPER.command().add("1");
						processWHISPER.command().add("--best_of");
						processWHISPER.command().add("1");
					}
					
					//Language
					if (comboLanguage.getSelectedIndex() > 0)
					{
						processWHISPER.command().add("--language");
						processWHISPER.command().add(WHISPER.WHISPER_LANGUAGES[comboLanguage.getSelectedIndex() - 1][0]);
					}
					
					//VAD
					if (boxVAD.isSelected())
					{
						processWHISPER.command().add("--vad_filter");
						processWHISPER.command().add("True");
					}
									
					//Preserve context
					if (boxContext.isSelected() == false)
					{
						processWHISPER.command().add("--condition_on_previous_text");
						processWHISPER.command().add("False");
					}
						
					//Output format
					if (Shutter.comboFilter.getSelectedItem().toString().equals(".srt"))
					{
						processWHISPER.command().add("--output_format");
						processWHISPER.command().add("srt");
						processWHISPER.command().add("--word_timestamps");
						processWHISPER.command().add("True");
						
					}							
					else if (Shutter.comboFilter.getSelectedItem().toString().equals(".vtt"))
					{
						processWHISPER.command().add("--output_format");
						processWHISPER.command().add("vtt");
						processWHISPER.command().add("--word_timestamps");
						processWHISPER.command().add("True");
					}
					else if (Shutter.comboFilter.getSelectedItem().toString().equals(".txt"))
					{
						processWHISPER.command().add("--output_format");
						processWHISPER.command().add("txt");
					}
					
					//Input file
					processWHISPER.command().add(wavFile);
					
					//Output folder
					processWHISPER.command().add("--output_dir");
					processWHISPER.command().add(new File(wavFile).getParent());

					//Adding ffmpeg the the PATH environment						        			        
			        if (System.getProperty("os.name").contains("Mac"))
			        {
			        	Map<String, String> env = processWHISPER.environment();			        	
			        	env.put("PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", "") + ":" + System.getenv("PATH"));
			        	env.put("DYLD_LIBRARY_PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", ""));
			        }
			        
					processWHISPER.redirectErrorStream(true);
					
					Console.consoleWHISPER.append(Shutter.language.getProperty("command") + " " + String.join(" ", processWHISPER.command()));	
						
					isRunning = true;	
					process = processWHISPER.start();
		
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String line;
			        
			        Console.consoleWHISPER.append(System.lineSeparator());
			        
			        Shutter.progressBar1.setMaximum(Math.round(FFPROBE.totalLength / 1000));
					
					do {
						
						if (Shutter.cancelled)
							break;
						
						line = br.readLine();					
						
						if (line != null)							
						{
							Console.consoleWHISPER.append(line + System.lineSeparator());
							
							if (line.contains(" --> "))
							{
								String s[] = line.split("]");
								String s2[] = s[0].split(" ");
								String s3[] = s2[2].split("\\.");
								String s4[] = s3[0].split(":");
								
								int value = Integer.parseInt(s4[0]) * 60 + Integer.parseInt(s4[1]);
								if (s4.length > 2)
									value = Integer.parseInt(s4[0]) * 3600 + Integer.parseInt(s4[1]) * 60 + Integer.parseInt(s4[2]);
								
								Shutter.lblCurrentEncoding.setText(fileName);
								Shutter.progressBar1.setMaximum(FFMPEG.fileLength);
								Shutter.progressBar1.setValue(value);
							}
			            	else if (line.contains("%"))
			            	{
			            		Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("downloadingAIModel"));
			            		Shutter.progressBar1.setMaximum(100);
			            		String s[] = line.split("%");
			            		Shutter.progressBar1.setValue(Integer.valueOf(s[0].replace(" ","")));
			            	} 
			            	else
			            		Shutter.lblCurrentEncoding.setText(fileName);
						}
						
					} while (line != null);		
						
					process.waitFor();		
					
					Console.consoleWHISPER.append(System.lineSeparator());
					
					isRunning = false;	        
					
					} catch (IOException | InterruptedException e) {
						error = true;
					}
									 
				}				
		});		
		runProcess.start();
	}

	public static String checkBrowser() throws Exception {
        // Registry key for default HTTP handler
        String key = "HKCU\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice";

        Process process = new ProcessBuilder("reg", "query", key, "/v", "ProgId")
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            String progId = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ProgId")) {
                    // Example output:    ProgId    REG_SZ    ChromeHTML
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3) {
                        progId = parts[parts.length - 1].trim();
                    }
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0 && progId != null)
            {
                return resolveBrowser(progId);
            }
        }
        
		return null;
    }

    private static String resolveBrowser(String progId) {
        switch (progId) {
            case "ChromeHTML": return "Google Chrome";
            case "MSEdgeHTM":
            case "MSEdgeHTMHTML": return "Microsoft Edge";
            case "FirefoxURL": return "Mozilla Firefox";
            case "IE.HTTP": return "Internet Explorer";
            default: return "Unknown (" + progId + ")";
        }
    }
    
    public static String checkDownloadLocation() throws Exception {
    	String defaultBrowser = getDefaultBrowserProgId();

        String downloadFolder = switch (defaultBrowser) {
            case "ChromeHTML" -> getChromeOrEdgeDownloadFolder(
                    Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome", "User Data", "Default", "Preferences"));
            case "MSEdgeHTM", "MSEdgeHTMHTML" -> getChromeOrEdgeDownloadFolder(
                    Paths.get(System.getenv("LOCALAPPDATA"), "Microsoft", "Edge", "User Data", "Default", "Preferences"));
            case "FirefoxURL" -> getFirefoxDownloadFolder();
            case "IE.HTTP" -> getIEDownloadFolder();
            default -> System.getProperty("user.home") + "\\Downloads";
        };

        return downloadFolder;
    }

    private static String getDefaultBrowserProgId() throws Exception {
        Process p = new ProcessBuilder("reg", "query",
                "HKCU\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice",
                "/v", "ProgId").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("ProgId")) {
                String[] parts = line.split("\\s{2,}");
                if (parts.length >= 3) return parts[2];
            }
        }
        p.waitFor();
        return "";
    }

    private static String getChromeOrEdgeDownloadFolder(Path prefsPath) {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            if (!Files.exists(prefsPath)) return fallback;
            String content = Files.readString(prefsPath, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);
            JSONObject download = json.optJSONObject("download");
            if (download != null) {
                String dir = download.optString("default_directory", "").trim();
                if (!dir.isEmpty()) return dir;
            }
        } catch (Exception ignored) { }
        return fallback;
    }

    private static String getFirefoxDownloadFolder() {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            Path appData = Paths.get(System.getenv("APPDATA"), "Mozilla", "Firefox", "Profiles");
            Optional<Path> profile = Files.list(appData).filter(Files::isDirectory).findFirst();
            if (profile.isEmpty()) return fallback;
            Path prefsJs = profile.get().resolve("prefs.js");
            if (!Files.exists(prefsJs)) return fallback;
            for (String line : Files.readAllLines(prefsJs, StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.startsWith("user_pref(\"browser.download.dir\"")) {
                    int firstQuote = line.indexOf('"', 27);
                    int secondQuote = line.indexOf('"', firstQuote + 1);
                    if (firstQuote > 0 && secondQuote > firstQuote) {
                        return line.substring(firstQuote + 1, secondQuote);
                    }
                }
            }
        } catch (Exception ignored) { }
        return fallback;
    }

    private static String getIEDownloadFolder() {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            Process p = new ProcessBuilder("reg", "query",
                    "HKCU\\Software\\Microsoft\\Internet Explorer\\Main",
                    "/v", "Download Directory").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Download Directory")) {
                    String[] parts = line.split("\\s{2,}");
                    if (parts.length >= 3) return parts[2];
                }
            }
            p.waitFor();
        } catch (Exception ignored) { }
        return fallback;
    }    
    
    private static File waitForFile(Path desktop, Path downloads, String keyword) throws InterruptedException {
      
    	while (true) {
    		
            // Check Desktop
            File f = findMatch(desktop, keyword);
            if (f != null) return f;

            // Check Downloads
            f = findMatch(downloads, keyword);
            if (f != null)
            	return f;

            Thread.sleep(1000);
        }
    }

    private static File findMatch(Path folder, String keyword) {
        File dir = folder.toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] matches = dir.listFiles((d, name) -> 
                name.toLowerCase().contains(keyword.toLowerCase()) &&
                name.toLowerCase().endsWith(".exe")
            );
            if (matches != null && matches.length > 0) {
                return matches[0];
            }
        }
        return null;
    }
}