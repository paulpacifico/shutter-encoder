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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.Console;
import application.Shutter;
import application.UIController;
import application.Utils;

public class ANONYMIZER {
	
	public static boolean error = false;
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static Process process;
	private static File anonymizerApp = null;
	public static File LIBRARY_DIR;
	public static JComboBox<String> comboModels;
	public static JComboBox<String> comboElements;
	public static JCheckBox boxStaticShot;
	public static JSlider sliderMaskSize;
	public static JSlider sliderBlurStrength;
	public static JSlider sliderAccuracy;
	public static JComboBox<String> comboPreview;;
	public static JLabel imgPreview;
	private static boolean mouseIsPressed = false;
	public static BufferedImage previewOriginal = null;
	
	public ANONYMIZER() {
				
		if (System.getProperty("os.name").contains("Windows"))
		{
			anonymizerApp = new File("C:\\Program Files\\Shutter Anonymizer\\Shutter Anonymizer.exe");	
			
			if (anonymizerApp.exists() == false)
				anonymizerApp = null;
		}
		else if (System.getProperty("os.name").contains("Mac"))
		{
			anonymizerApp = new File("/Applications/Shutter Anonymizer.app");
			
			if (anonymizerApp.exists() == false)
				anonymizerApp = null;
		}

		if (anonymizerApp != null)
		{
			if (checkAccount())
			{
				setLibraryDir();
				ANONYMIZER.setComponents();
			}
			else
			{
				Shutter.comboFonctions.setSelectedItem("");
				try {
					Desktop.getDesktop().open(anonymizerApp);
				} catch (IOException e) {}
			}
		}
		else
		{							
			ImageIcon app = new ImageIcon(getClass().getClassLoader().getResource("contents/Shutter Anonymizer.png"));
			Image scaled = app.getImage().getScaledInstance(420, -1, Image.SCALE_SMOOTH); 
	        ImageIcon icon = new ImageIcon(scaled);
	        
	        JLabel background = new JLabel(icon);
	        background.setLayout(new BorderLayout());
	        
	        JLabel text = new JLabel("<html>" + Shutter.language.getProperty("shutterAnonymizerRequired") + "<br>" + Shutter.language.getProperty("wantToDownload") + "</html>", SwingConstants.CENTER);
	        Image anonymizer = new ImageIcon(getClass().getClassLoader().getResource("contents/icon_anonymizer.png")).getImage();
	        Image newimg = anonymizer.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
			ImageIcon logo = new ImageIcon(newimg);
			text.setIcon(logo);
	        text.setOpaque(false);
	        text.setForeground(Color.WHITE);
	        text.setFont(new Font(Shutter.mainFont, Font.BOLD, 14));
	        
	        background.add(text, BorderLayout.CENTER);

			int q = JOptionPane.showConfirmDialog(Shutter.frame, background,
					Shutter.language.getProperty("functionBlurFaces"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (q == JOptionPane.YES_OPTION)
			{
				if (System.getProperty("os.name").contains("Windows"))
				{
					Thread download = new Thread(new Runnable() {

						@Override
						public void run() {
							
							try {
								Desktop.getDesktop().browse(new URI("https://www.paypal.com/ncp/payment/936R65QEVLXCY"));
														
								File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();

								//Waiting the file to exists				
								File fileToUnlock = waitForFile(Paths.get(desktopDir.getAbsolutePath()), Paths.get(checkDownloadLocation()), "Shutter Anonymizer");

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
						Desktop.getDesktop().browse(new URI("https://www.paypal.com/ncp/payment/RGWPG2UKKVRG4"));
					} catch (IOException | URISyntaxException er) {}	
				}															
			}
			else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBlurFaces")))
			{
				Shutter.comboFonctions.removeItem(Shutter.language.getProperty("functionBlurFaces"));
			}
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBlurFaces")))
				Shutter.comboFonctions.setSelectedItem("");
		}
	}
	
	@SuppressWarnings("resource")
	private boolean checkAccount() {
		
		try {
			
			File documents = new File(System.getProperty("user.home") + "/Shutter Anonymizer");
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
				
				File jarFile = anonymizerApp;
				if (System.getProperty("os.name").contains("Mac"))
				{
					jarFile = new File(anonymizerApp.toString() + "/Contents/app/Shutter Anonymizer.jar");
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
			LIBRARY_DIR = new File(anonymizerApp.getParent() + "\\Library");
		}	
		else
			LIBRARY_DIR = new File(anonymizerApp + "/Contents/app/Library");		
	}
	
	public static String getFaceModel() {

		if (System.getProperty("os.name").contains("Windows"))
		{
			return LIBRARY_DIR.toString() + "\\models\\ego_blur_face_gen2.jit";
		}	
		else
			return LIBRARY_DIR.toString() + "/models/ego_blur_face_gen2.jit";
	}
	
	public static String getLicensePlateModel() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			return LIBRARY_DIR.toString() + "\\models\\ego_blur_lp_gen2.jit";
		}	
		else
			return LIBRARY_DIR.toString() + "/models/ego_blur_lp_gen2.jit";
	}

	public static void setComponents() {
		
	    JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
	    mainPanel.setOpaque(false);

	    JPanel leftPanel = new JPanel();
	    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	    leftPanel.setOpaque(false);

	    JLabel lblModel = new JLabel("Blur Presets:");
	    lblModel.setForeground(Utils.c225);
	    lblModel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
	    comboModels = new JComboBox<String>();
	    comboModels.setModel(new DefaultComboBoxModel<>(new String[] {"Soft Blur Mask", "Mosaic Mask", "Opaque Mask"}));
	    comboModels.setSelectedIndex(0);
	    comboModels.setAlignmentX(Component.LEFT_ALIGNMENT);
	    comboModels.setMaximumSize(new Dimension(192, comboModels.getPreferredSize().height));
	    
	    comboModels.addActionListener(e -> Preview.updatePreview());

	    comboElements = new JComboBox<String>();
	    comboElements.setModel(new DefaultComboBoxModel<String>(new String[] {"Faces", "License plates", "Faces + License plates"}));
	    comboElements.setSelectedIndex(0);
	    comboElements.setAlignmentX(Component.LEFT_ALIGNMENT);
	    comboElements.setMaximumSize(new Dimension(192, comboElements.getPreferredSize().height));
	    
	    boxStaticShot = new JCheckBox("Static Shot");
	    boxStaticShot.setForeground(Utils.c225);
	    boxStaticShot.setSelected(false);
	    boxStaticShot.setOpaque(false);
	    boxStaticShot.setAlignmentX(Component.LEFT_ALIGNMENT);

	    leftPanel.add(lblModel);
	    leftPanel.add(Box.createVerticalStrut(5));
	    leftPanel.add(comboModels);
	    leftPanel.add(Box.createVerticalStrut(10));
	    leftPanel.add(comboElements);
	    leftPanel.add(Box.createVerticalStrut(10));
	    leftPanel.add(boxStaticShot);

	    JPanel centerPanel = new JPanel();
	    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	    centerPanel.setOpaque(false);

	    JLabel lblMaskSize = new JLabel("Mask Size:");
	    lblMaskSize.setForeground(Utils.c225);
	    lblMaskSize.setAlignmentX(Component.LEFT_ALIGNMENT);
	    sliderMaskSize = createStyledSlider(0, 10, 5);
	    sliderMaskSize.setAlignmentX(Component.LEFT_ALIGNMENT);

	    JLabel lblBlurStrength = new JLabel("Blur Strength:");
	    lblBlurStrength.setForeground(Utils.c225);
	    lblBlurStrength.setAlignmentX(Component.LEFT_ALIGNMENT);
	    sliderBlurStrength = createStyledSlider(0, 10, 5);
	    sliderBlurStrength.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
	    JLabel lblAccuracy = new JLabel("Accuracy:");
	    lblAccuracy.setForeground(Utils.c225);
	    lblAccuracy.setAlignmentX(Component.LEFT_ALIGNMENT);
	    sliderAccuracy = createStyledSlider(1, 6, 4);
	    sliderAccuracy.setAlignmentX(Component.LEFT_ALIGNMENT);

	    centerPanel.add(lblMaskSize);
	    centerPanel.add(Box.createVerticalStrut(5));
	    centerPanel.add(sliderMaskSize);
	    centerPanel.add(Box.createVerticalStrut(10));
	    centerPanel.add(lblBlurStrength);
	    centerPanel.add(Box.createVerticalStrut(5));
	    centerPanel.add(sliderBlurStrength);
	    centerPanel.add(Box.createVerticalStrut(10));
	    centerPanel.add(lblAccuracy);
	    centerPanel.add(Box.createVerticalStrut(5));
	    centerPanel.add(sliderAccuracy);
	    
	    JPanel rightPanel = new JPanel();
	    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
	    rightPanel.setOpaque(false);

	    comboPreview = new JComboBox<String>();
		comboPreview.setModel(new DefaultComboBoxModel<String>(new String[] {"Female", "Male", "License Plate"}));
		comboPreview.setSelectedIndex(0);	
		comboPreview.setMaximumSize(new Dimension(192, comboPreview.getPreferredSize().height));
		
		comboPreview.addActionListener(e -> {
			
			try {
				
				if (comboPreview.getSelectedIndex() == 0)
				{
					previewOriginal = javax.imageio.ImageIO.read(Shutter.class.getClassLoader().getResource("contents/female.jpg"));
				}
				else if (comboPreview.getSelectedIndex() == 1)
				{
					previewOriginal = javax.imageio.ImageIO.read(Shutter.class.getClassLoader().getResource("contents/male.jpg"));
				}
				else
					previewOriginal = javax.imageio.ImageIO.read(Shutter.class.getClassLoader().getResource("contents/license_plate.jpg"));
				
				Preview.updatePreview();
				
			} catch (Exception ignored) {}
			
		});
		
		imgPreview = new RoundedImageLabel();
		imgPreview.setPreferredSize(new Dimension(192, 192));
		imgPreview.setMaximumSize(new Dimension(192, 192));

		try {
			previewOriginal = javax.imageio.ImageIO.read(Shutter.class.getClassLoader().getResource("contents/female.jpg"));
		} catch (Exception ignored) {}
		Preview.updatePreview();

		JPanel imgWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		imgWrapper.setOpaque(false);
		imgWrapper.setMaximumSize(new Dimension(192, 192));
		imgWrapper.add(imgPreview);
		
		rightPanel.add(comboPreview);
		rightPanel.add(Box.createVerticalStrut(5));
		rightPanel.add(imgWrapper);
		
	    mainPanel.add(leftPanel);
	    mainPanel.add(centerPanel);
	    mainPanel.add(rightPanel);

	    Object[] options = { Shutter.language.getProperty("btnApply") };
	    JOptionPane.showOptionDialog(
	        Shutter.frame, 
	        mainPanel, 
	        Shutter.language.getProperty("functionBlurFaces"), 
	        JOptionPane.DEFAULT_OPTION, 
	        JOptionPane.PLAIN_MESSAGE, 
	        null, 
	        options, 
	        options[0]
	    );
	}
	
	private static JSlider createStyledSlider(int min, int max, int val) {
	    JSlider slider = new JSlider(min, max, val);
	    slider.setMajorTickSpacing(1);
	    slider.setPaintTicks(true);
	    slider.setPaintLabels(true);
	    slider.setOpaque(false);
	    slider.setForeground(Utils.c225);
	    
	    slider.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseIsPressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	mouseIsPressed = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
	    
	    slider.addMouseMotionListener(new MouseMotionAdapter() {

        	int lastValue = -1;
        	
            @Override
            public void mouseDragged(MouseEvent e) {

                if (!mouseIsPressed) return;
                
                BasicSliderUI ui = (BasicSliderUI) sliderMaskSize.getUI();

                int value = ui.valueForXPosition(e.getX());
                value = Math.max(0, Math.min(10, value));

                if (value != lastValue) {
                    Preview.updatePreview();
                    lastValue = value;
                }
            }
        });
	    
	    return slider;
	}
	
	public static void run(String input, File output) {
		
		error = false;
		Shutter.progressBar1.setValue(0);	
		Shutter.progressBar1.setMaximum(100);
		UIController.disableAll();
		Shutter.btnStart.setEnabled(false);
				
		runProcess = new Thread(new Runnable()  {

		@Override
		public void run() {
				
			try {

				ProcessBuilder processBuilder = new ProcessBuilder();
				if (System.getProperty("os.name").contains("Windows"))
				{							
					processBuilder.command().add(LIBRARY_DIR.toString() + "\\python.exe");
					processBuilder.command().add(LIBRARY_DIR.toString() + "\\anonymize.py");
				}	
				else
					processBuilder.command().add(LIBRARY_DIR.toString() + "/anonymize");
				
				// Models
				switch (comboElements.getSelectedIndex())
				{
					case 0: // Faces
						processBuilder.command().add("--face_model");
						processBuilder.command().add(getFaceModel());				
						break;
					case 1: // License plates
						processBuilder.command().add("--lp_model");
						processBuilder.command().add(getLicensePlateModel());	
						break;
					case 2: // Both
						processBuilder.command().add("--face_model");
						processBuilder.command().add(getFaceModel());
						processBuilder.command().add("--lp_model");
						processBuilder.command().add(getLicensePlateModel());
						break;
				}		
				
				// Fixed Shot
				if (boxStaticShot.isSelected())
				{
					processBuilder.command().add("--frame_skip");
					processBuilder.command().add("3");
				}
				
				// Input file
				processBuilder.command().add("--input");
				processBuilder.command().add(input);
				
				// Output folder
				processBuilder.command().add("--output");
				processBuilder.command().add(output.toString());
				
				// Model				
				processBuilder.command().add("--blur_type");
				switch (comboModels.getSelectedIndex())
				{
					case 0: // Smooth
						processBuilder.command().add("smooth");	
						break;
					case 1: // mosaic
						processBuilder.command().add("mosaic");	
						break;
					case 2: // Default
						processBuilder.command().add("default");	
						break;
				}			
		        
				// Size of the mask — scale_factor_detections 1.0..2.0
				processBuilder.command().add("--scale_factor_detections");
				processBuilder.command().add(String.format("%.2f", 1.0 + (sliderMaskSize.getValue() / 10.0) * 1.0));

				// Blur strength — blur_scale 0.10..1.15
				processBuilder.command().add("--blur_scale");
				
				double ratio = 10.0;
				if (comboModels.getSelectedIndex() == 1) // Mosaic
					ratio = 5.0;
				
				processBuilder.command().add(String.format("%.2f", 0.10 + (sliderBlurStrength.getValue() / ratio) * 1.15));
				
				// Accuracy
				switch (sliderAccuracy.getValue())
				{
					case 1:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("100");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("200");
						break;
					case 2:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("200");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("300");
						break;
					case 3:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("400");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("600");				
						break;
					case 4:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("600");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("800");		
						break;
					case 5:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("800");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("1000");		
						break;
					case 6:
						processBuilder.command().add("--resize_min");
						processBuilder.command().add("1200");
						processBuilder.command().add("--resize_max");
						processBuilder.command().add("1200");		
						break;
				}
				
				// GPU
				if (System.getProperty("os.name").contains("Windows") && PYTHON.isTorchCudaInstalled(LIBRARY_DIR.toString()))
					processBuilder.command().add("--fp16");
				
				
				// DeepFace model 
				processBuilder.command().add("--deepface_home");
				processBuilder.command().add(new File(getFaceModel()).getParent());
				
				processBuilder.redirectErrorStream(true);

				Console.consolePYTHON.append(Shutter.language.getProperty("command") + " " + String.join(" ", processBuilder.command()));	
				
				isRunning = true;	
				process = processBuilder.start();
	            
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));	            	    
	            
	            Console.consolePYTHON.append(System.lineSeparator());
	            
	            String line;	           
	            while ((line = reader.readLine()) != null)
	            {
            		Console.consolePYTHON.append(line + System.lineSeparator());

            		if (line.contains("%"))
					{
	            		Pattern pattern = Pattern.compile("(\\d+)%");
	                    Matcher matcher = pattern.matcher(line);
							
	                    if (matcher.find())
	                    {
		                    String percentValue = matcher.group(1);
		                    int percent = Integer.parseInt(percentValue);
		                    
							Shutter.progressBar1.setValue(percent);
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

//Round JLabel corners
@SuppressWarnings("serial")
class RoundedImageLabel extends JLabel {

  private int radius = 20;

  public RoundedImageLabel() {
      setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);

      // Paint rounded background with whatever color is set via setBackground()
      g2.setColor(Utils.c20);
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

      // Clip content (icon/text) to the same rounded shape
      Shape clip = new RoundRectangle2D.Float(
              0, 0, getWidth(), getHeight(),
              radius, radius
      );
      g2.setClip(clip);

      super.paintComponent(g2);

      g2.dispose();
  }
}

//Preview blur
class Preview extends ANONYMIZER {

	public static void updatePreview() {

	    if (previewOriginal == null) return;

	    int modelIdx    = comboModels.getSelectedIndex();  // 0=smooth, 1=mosaic, 2=opaque
	    int sizeVal     = sliderMaskSize.getValue();        // 0..10
	    int strengthVal = sliderBlurStrength.getValue();    // 0..10

	    // 0→0.70, 5→1.0, 10→1.50  (--scale_factor_detections)
	    double expand    = 0.70 + (sizeVal / 10.0) * 0.80;

	    // 0→0.10, 5→1.0, 10→1.15  (--blur_scale)
	    double blurScale = 0.10 + (strengthVal / 10.0) * 1.15;
	    if (modelIdx == 1) // Mosaic
			blurScale = 0.10 + (strengthVal / 5.0) * 1.15;
	    // NOTE: mosaic uses the same blurScale formula as smooth now (was /5.0 before)

	    int W = previewOriginal.getWidth();
	    int H = previewOriginal.getHeight();

	    boolean isWide = comboPreview.getSelectedIndex() == 2;

	    // ── Step 1: raw detection box (simulated, normalised coords) ──────────
	    int x1 = (int)(0.26 * W), y1 = (int)(0.09 * H);
	    int x2 = (int)(0.73 * W), y2 = (int)(0.77 * H);

	    if (isWide) {
	        x1 = (int)(0.22 * W); y1 = (int)(0.38 * H);
	        x2 = (int)(0.78 * W); y2 = (int)(0.61 * H);
	    }

	    // ── Step 2: scale_box() — expand detection ────────────────────────────
	    {
	        double cx = (x1 + x2) / 2.0, cy = (y1 + y2) / 2.0;
	        double hw = (x2 - x1) * expand / 2.0, hh = (y2 - y1) * expand / 2.0;
	        x1 = Math.max(0, (int)(cx - hw));
	        y1 = Math.max(0, (int)(cy - hh));
	        x2 = Math.min(W, (int)(cx + hw));
	        y2 = Math.min(H, (int)(cy + hh));
	    }
	    int bw = x2 - x1, bh = y2 - y1;

	    // ── avg_dim — Python uses average of (width+height)/2 across detections.
	    //    With a single simulated box that is exactly bw×bh:
	    double avgDim = (bw + bh) / 2.0;

	    // ── Step 3: build fg (effect layer) — applied to the WHOLE image ──────
	    // Python: image_fg is the blurred/mosaiced version of the full frame.
	    BufferedImage fg;

	    if (modelIdx == 2) {
	        // "default" — solid black foreground (whole image starts black,
	        //  only the masked region will show through after blending)
	        fg = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
	        // Pixels default to 0 (black) — no fill needed.

	    } else if (modelIdx == 0) {
	        // "smooth" — GaussianBlur on FULL image
	        // Python: k = max(3, int(avg_dim * 0.5 * blur_scale) | 1)
	        int k = Math.max(3, (int)(avgDim * 0.5 * blurScale) | 1);
	        fg = boxBlurRGB(previewOriginal, k);

	    } else {
	        // "mosaic" — pixelate FULL image
	        // Python: block = max(1, int(avg_dim * 0.1 / blur_scale))
	        //         sw = max(1, w // block), sh = max(1, h // block)
	        //         small  = cv2.resize(image, (sw,sh), INTER_LINEAR)
	        //         image_fg = cv2.resize(small, (w,h), INTER_NEAREST)
	        int block = Math.max(1, (int)(avgDim * 0.1 / blurScale));
	        int sw = Math.max(1, W / block), sh = Math.max(1, H / block);
	        fg = mosaicImage(previewOriginal, W, H, sw, sh);
	    }

	    // ── Step 4: build feathered mask ──────────────────────────────────────
	    // Python uses GaussianBlur for feathering (not box blur).
	    // fk multipliers:
	    //   smooth/mosaic + wide (plate):  fk = max(1, int(max(bw,bh)*0.1)  | 1)
	    //   smooth/mosaic + narrow (face): fk = max(1, int(max(bw,bh)*0.3)  | 1)
	    //   default (any shape):           no feather (fk = 0)
	    int cx = (x1 + x2) / 2, cy = (y1 + y2) / 2;
	    float[] mask;

	    if (modelIdx == 2) {
	        // "default" — hard mask, no feather
	        if (isWide) {
	            mask = buildRectMask(W, H, x1, y1, x2, y2, 0);
	        } else {
	            mask = buildEllipseMask(W, H, cx, cy, bw, bh, 0);
	        }

	    } else {
	        // "smooth" or "mosaic" — Gaussian-feathered mask
	        int maxDim = Math.max(bw, bh);
	        if (isWide) {
	            // Python: fk = max(1, int(max(bw,bh)*0.1) | 1)
	            int fk = Math.max(1, (int)(maxDim * 0.1) | 1);
	            mask = buildRectMaskGaussian(W, H, x1, y1, x2, y2, fk);
	        } else {
	            // Python: fk = max(1, int(max(bw,bh)*0.3) | 1)
	            int fk = Math.max(1, (int)(maxDim * 0.3) | 1);
	            mask = buildEllipseMaskGaussian(W, H, cx, cy, bw, bh, fk);
	        }
	    }

	    // ── Step 5: blend — fg*alpha + frame*(1-alpha) ────────────────────────
	    BufferedImage out = blendWithMask(previewOriginal, fg, mask, W, H);
	    setPreviewIcon(out);
	}

	/**
	 * Mosaic (pixelate) the full source image.
	 * Matches Python:
	 *   small    = cv2.resize(image, (sw,sh), INTER_LINEAR)
	 *   image_fg = cv2.resize(small, (w,h),  INTER_NEAREST)
	 */
	private static BufferedImage mosaicImage(BufferedImage src, int W, int H, int sw, int sh) {
	    // Bilinear downscale (INTER_LINEAR)
	    BufferedImage small = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
	    Graphics2D gs = small.createGraphics();
	    gs.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    gs.drawImage(src, 0, 0, sw, sh, null);
	    gs.dispose();

	    // Nearest-neighbour upscale (INTER_NEAREST)
	    BufferedImage result = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
	    Graphics2D gr = result.createGraphics();
	    gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	    gr.drawImage(small, 0, 0, W, H, null);
	    gr.dispose();
	    return result;
	}

	/**
	 * Rectangle mask with Gaussian feathering — matches:
	 *   cv2.rectangle(temp_mask, (x1,y1), (x2,y2), 255, -1)
	 *   cv2.GaussianBlur(temp_mask / 255, (fk, fk), 0)
	 */
	private static float[] buildRectMaskGaussian(int W, int H,
	                                              int x1, int y1, int x2, int y2,
	                                              int fk) {
	    float[] alpha = new float[W * H];
	    for (int py = y1; py < y2; py++)
	        for (int px = x1; px < x2; px++)
	            alpha[py * W + px] = 1f;

	    if (fk > 0)
	        alpha = gaussianBlurMask(alpha, W, H, fk);

	    return alpha;
	}

	/**
	 * Ellipse mask with Gaussian feathering — matches:
	 *   cv2.ellipse(temp_mask, ((cx,cy),(bw,bh),0), 255, -1)
	 *   cv2.GaussianBlur(temp_mask / 255, (fk, fk), 0)
	 */
	private static float[] buildEllipseMaskGaussian(int W, int H,
	                                                 int cx, int cy, int bw, int bh,
	                                                 int fk) {
	    BufferedImage maskImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
	    Graphics2D gm = maskImg.createGraphics();
	    gm.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    gm.setColor(Color.WHITE);
	    gm.fill(new java.awt.geom.Ellipse2D.Double(cx - bw / 2.0, cy - bh / 2.0, bw, bh));
	    gm.dispose();

	    byte[] raw = ((java.awt.image.DataBufferByte) maskImg.getRaster().getDataBuffer()).getData();
	    float[] alpha = new float[W * H];
	    for (int i = 0; i < alpha.length; i++)
	        alpha[i] = (raw[i] & 0xFF) / 255f;

	    if (fk > 0)
	        alpha = gaussianBlurMask(alpha, W, H, fk);

	    return alpha;
	}

	/**
	 * Gaussian blur on a float[] mask — matches cv2.GaussianBlur(mask, (k,k), 0).
	 * OpenCV's GaussianBlur with sigma=0 computes sigma from k as: sigma = 0.3*(k/2 - 1) + 0.8
	 * Implemented as two separable 1-D passes.
	 */
	private static float[] gaussianBlurMask(float[] src, int W, int H, int k) {
	    // sigma = 0.3 * ((k - 1) * 0.5 - 1) + 0.8  — OpenCV's formula for sigma=0
	    double sigma = 0.3 * ((k - 1) * 0.5 - 1) + 0.8;
	    float[] kernel = makeGaussianKernel(k, sigma);

	    int half = k / 2;
	    float[] tmp = new float[W * H];
	    float[] out = new float[W * H];

	    // Horizontal pass
	    for (int y = 0; y < H; y++) {
	        for (int x = 0; x < W; x++) {
	            float v = 0;
	            for (int ki = 0; ki < k; ki++) {
	                int sx = Math.max(0, Math.min(W - 1, x + ki - half));
	                v += src[y * W + sx] * kernel[ki];
	            }
	            tmp[y * W + x] = v;
	        }
	    }
	    // Vertical pass
	    for (int x = 0; x < W; x++) {
	        for (int y = 0; y < H; y++) {
	            float v = 0;
	            for (int ki = 0; ki < k; ki++) {
	                int sy = Math.max(0, Math.min(H - 1, y + ki - half));
	                v += tmp[sy * W + x] * kernel[ki];
	            }
	            out[y * W + x] = v;
	        }
	    }
	    return out;
	}

	/**
	 * Build a normalised 1-D Gaussian kernel of size k with the given sigma.
	 */
	private static float[] makeGaussianKernel(int k, double sigma) {
	    float[] kernel = new float[k];
	    int half = k / 2;
	    double sum = 0;
	    for (int i = 0; i < k; i++) {
	        double x = i - half;
	        kernel[i] = (float) Math.exp(-(x * x) / (2 * sigma * sigma));
	        sum += kernel[i];
	    }
	    for (int i = 0; i < k; i++) kernel[i] /= (float) sum;
	    return kernel;
	}
	
	/**
	 * Draws a filled rectangle mask and optionally box-blurs it — matching:
	 *   cv2.rectangle(temp_mask, (x1,y1), (x2,y2), 255, -1)
	 *   cv2.blur(temp_mask / 255, (fk, fk))   ← only when fk > 0
	 *
	 * Used for wide detections (license plates) where bw > bh * 1.5.
	 */
	private static float[] buildRectMask(int W, int H,
	                                      int x1, int y1, int x2, int y2,
	                                      int fk) {
		float[] alpha = new float[W * H];
		for (int py = y1; py < y2; py++)
			for (int px = x1; px < x2; px++)
				alpha[py * W + px] = 1f;

		if (fk > 0)
			alpha = boxBlurMask(alpha, W, H, fk);

		return alpha;
	}

	/**
	 * Draws a filled ellipse mask and box-blurs it with the given kernel
	 * size fk — directly matching:
	 *   cv2.ellipse(temp_mask, ((cx,cy),(bw,bh),0), 255, -1)
	 *   cv2.blur(temp_mask/255, (fk,fk))
	 *
	 * Note: OpenCV ellipse() takes (cx,cy) as center and (bw,bh) as *full* axes,
	 * whereas Java's Ellipse2D takes top-left corner. We convert here.
	 */
	private static float[] buildEllipseMask(int W, int H,
	                                         int cx, int cy, int bw, int bh,
	                                         int fk) {
		// Hard ellipse — OpenCV's ellipse() center+full-size → Java top-left
		BufferedImage maskImg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D gm = maskImg.createGraphics();
		gm.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gm.setColor(Color.WHITE);
		gm.fill(new java.awt.geom.Ellipse2D.Double(cx - bw / 2.0, cy - bh / 2.0, bw, bh));
		gm.dispose();

		byte[] raw = ((java.awt.image.DataBufferByte) maskImg.getRaster().getDataBuffer()).getData();
		float[] alpha = new float[W * H];
		for (int i = 0; i < alpha.length; i++)
			alpha[i] = (raw[i] & 0xFF) / 255f;

		if (fk > 0)
			alpha = boxBlurMask(alpha, W, H, fk);

		return alpha;
	}

	/**
	 * Box blur on a float[] mask — matches cv2.blur(mask, (k,k)).
	 * Each output pixel is the uniform average of its k×k neighbourhood,
	 * implemented as two separable 1-D passes for efficiency.
	 */
	private static float[] boxBlurMask(float[] src, int W, int H, int k) {
		int half = k / 2;
		float[] tmp = new float[W * H];
		float[] out = new float[W * H];

		// Horizontal pass
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				float v = 0;
				for (int ki = 0; ki < k; ki++) {
					int sx = Math.max(0, Math.min(W - 1, x + ki - half));
					v += src[y * W + sx];
				}
				tmp[y * W + x] = v / k;
			}
		}
		// Vertical pass
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				float v = 0;
				for (int ki = 0; ki < k; ki++) {
					int sy = Math.max(0, Math.min(H - 1, y + ki - half));
					v += tmp[sy * W + x];
				}
				out[y * W + x] = v / k;
			}
		}
		return out;
	}

	/**
	 * Box blur on an RGB BufferedImage — matches cv2.blur(region, (k,k)).
	 * Each output pixel is the uniform average of its k×k neighbourhood,
	 * implemented as two separable 1-D passes for efficiency.
	 */
	private static BufferedImage boxBlurRGB(BufferedImage src, int k) {
		int w = src.getWidth(), h = src.getHeight();
		int half = k / 2;

		int[] px  = src.getRGB(0, 0, w, h, null, 0, w);
		float[] r = new float[w * h], g = new float[w * h], b = new float[w * h];
		for (int i = 0; i < px.length; i++) {
			r[i] = (px[i] >> 16) & 0xFF;
			g[i] = (px[i] >>  8) & 0xFF;
			b[i] =  px[i]        & 0xFF;
		}

		float[] tr = new float[w * h], tg = new float[w * h], tb = new float[w * h];

		// Horizontal pass
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				float vr = 0, vg = 0, vb = 0;
				for (int ki = 0; ki < k; ki++) {
					int sx = Math.max(0, Math.min(w - 1, x + ki - half));
					vr += r[y * w + sx];
					vg += g[y * w + sx];
					vb += b[y * w + sx];
				}
				tr[y * w + x] = vr / k; tg[y * w + x] = vg / k; tb[y * w + x] = vb / k;
			}
		}
		// Vertical pass
		int[] out = new int[w * h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				float vr = 0, vg = 0, vb = 0;
				for (int ki = 0; ki < k; ki++) {
					int sy = Math.max(0, Math.min(h - 1, y + ki - half));
					vr += tr[sy * w + x];
					vg += tg[sy * w + x];
					vb += tb[sy * w + x];
				}
				int ri = Math.min(255, Math.max(0, (int)(vr / k + 0.5f)));
				int gi = Math.min(255, Math.max(0, (int)(vg / k + 0.5f)));
				int bi = Math.min(255, Math.max(0, (int)(vb / k + 0.5f)));
				out[y * w + x] = 0xFF000000 | (ri << 16) | (gi << 8) | bi;
			}
		}
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, w, h, out, 0, w);
		return result;
	}

	/**
	 * Per-pixel blend: out = fg * alpha + bg * (1 - alpha)
	 * Matches: (image_fg * alpha3 + frame * (1.0 - alpha3)).astype(np.uint8)
	 */
	private static BufferedImage blendWithMask(BufferedImage bg, BufferedImage fg,
	                                            float[] alpha, int W, int H) {
		int[] bgPx = bg.getRGB(0, 0, W, H, null, 0, W);
		int[] fgPx = fg.getRGB(0, 0, W, H, null, 0, W);
		int[] out  = new int[W * H];
		for (int i = 0; i < out.length; i++) {
			float a = alpha[i], ia = 1f - a;
			int br = (bgPx[i] >> 16) & 0xFF, bg2 = (bgPx[i] >> 8) & 0xFF, bb = bgPx[i] & 0xFF;
			int fr = (fgPx[i] >> 16) & 0xFF, fg2 = (fgPx[i] >> 8) & 0xFF, fb = fgPx[i] & 0xFF;
			out[i] = 0xFF000000
				| (Math.min(255, (int)(fr * a + br * ia + 0.5f)) << 16)
				| (Math.min(255, (int)(fg2 * a + bg2 * ia + 0.5f)) << 8)
				|  Math.min(255, (int)(fb  * a + bb  * ia + 0.5f));
		}
		BufferedImage result = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, W, H, out, 0, W);
		return result;
	}

	private static void setPreviewIcon(BufferedImage out) {
		int pw = imgPreview.getWidth()  > 0 ? imgPreview.getWidth()  : 192;
		int ph = imgPreview.getHeight() > 0 ? imgPreview.getHeight() : 192;
		int W = out.getWidth(), H = out.getHeight();
		double scale = Math.min((double) pw / W, (double) ph / H);
		int dw = (int)(W * scale), dh = (int)(H * scale);
		Image scaled = out.getScaledInstance(dw, dh, Image.SCALE_SMOOTH);
		imgPreview.setIcon(new ImageIcon(scaled));
	}
}