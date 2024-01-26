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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.BMXTRANSWRAP;
import library.DCRAW;
import library.DVDAUTHOR;
import library.FFMPEG;
import library.FFPROBE;
import library.TSMUXER;
import library.YOUTUBEDL;
import settings.Colorimetry;
import settings.FunctionUtils;

public class Utils extends Shutter {
	
	static String pathToLanguages;
	public static Color themeColor = new Color(71, 163, 236);
	public static Color highlightColor = new Color(129, 198, 253);
	public static boolean yesToAll = false;
	public static boolean noToAll = false;
	public final static String username = "info@shutterencoder.com";
	public final static String password = "";
	
	public static Thread loadEncFile;
	public static String hwaccel = "";
	
	public static void changeFrameVisibility(final JFrame f, final boolean isVisible) {

		if (isVisible == false) {
			f.setVisible(true);
		} else {
			f.setVisible(false);
		}

	}

	public static void changeDialogVisibility(final JDialog f, final boolean isVisible) {

		if (isVisible == false) {
			f.setVisible(true);
		} else {
			f.setVisible(false);
		}

	}

	public static void setLanguage() {
		
		//Language
		InputStream input = null;
		try {
			
			pathToLanguages = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (System.getProperty("os.name").contains("Windows"))
			{
				pathToLanguages = pathToLanguages.substring(1, pathToLanguages.length() - 1);
			}
			else
				pathToLanguages = pathToLanguages.substring(0, pathToLanguages.length() - 1);

			pathToLanguages = pathToLanguages.substring(0, (int) (pathToLanguages.lastIndexOf("/"))).replace("%20", " ")
					+ "/Languages/";		
			
			try {

				File oldDocumentsPath = new File(System.getProperty("user.home") + "/Documents/Shutter Encoder");
				
				// Library/Preferences sur Mac
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{					
					if (new File(System.getProperty("user.home") + "/Library/Preferences/Shutter Encoder").exists())
					{
						Shutter.documents = new File(System.getProperty("user.home") + "/Library/Preferences/Shutter Encoder");
						Shutter.settingsXML = new File(Shutter.documents + "/settings.xml");;
					}
					else if (new File("/Library/Preferences/Shutter Encoder").exists())
					{
						Shutter.documents = new File("/Library/Preferences/Shutter Encoder");
						Shutter.settingsXML = new File(Shutter.documents + "/settings.xml");
					}	
					else if (oldDocumentsPath.exists()) //old path
					{
						try {
							FileUtils.moveDirectory(oldDocumentsPath, Shutter.documents);
						} 
						catch (IOException e)
						{
							if (Shutter.documents.exists() == false)
							{
								Shutter.documents = oldDocumentsPath;
								Shutter.settingsXML = new File(Shutter.documents + "/settings.xml");
							}
						}
					}
				}
				else 
				{
					if (oldDocumentsPath.exists()) //old path
					{
						try {
							FileUtils.moveDirectory(oldDocumentsPath, Shutter.documents);
						} 
						catch (IOException e)
						{
							if (Shutter.documents.exists() == false)
							{
								Shutter.documents = oldDocumentsPath;
								Shutter.settingsXML = new File(Shutter.documents + "/settings.xml");
							}							
						}
					}
				}
				
			} catch (Exception e) {}
						
			// Dossier Temporaire Linux
			if (System.getProperty("os.name").contains("Linux"))
			{
				dirTemp += "/";
			}
			
			if (Shutter.settingsXML.exists())
			{				
				try {
					File fXmlFile = Shutter.settingsXML;
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
						for (String local : Locale.getISOLanguages())
						{												
							String language = Locale.of(local).getDisplayLanguage();

							//With Country
							if (getLanguage.contains("("))
							{
								String c[] = getLanguage.replace("(", "").replace(")", "").split(" ");
								
								if (language.equals(c[0]))
								{
									for (String countries : Locale.getISOCountries())
									{				
										String country = Locale.of(local, countries).getDisplayCountry();
																				
										if (country.equals(c[1]))
										{															
											String loadLanguage = pathToLanguages + local + "_" + countries + ".properties";
																						
											if (new File(loadLanguage).exists())
											{									
												input = new FileInputStream(loadLanguage);
												break;
											}
										}
									}									
															
								}								
							}
							else //Language only
							{
								if (language.equals(getLanguage))
								{
									String loadLanguage = pathToLanguages + local + ".properties";
									
									if (new File(loadLanguage).exists())
									{									
										input = new FileInputStream(loadLanguage);
										break;
									}									
								}
							}
						}
						
						if (input == null)
							input = defaultLanguage(pathToLanguages);				
					}
					else
					{
						input = defaultLanguage(pathToLanguages);
					}
					
				} 
				catch (Exception e) 
				{
					input = defaultLanguage(pathToLanguages);
				}
			}
			else
			{
				input = defaultLanguage(pathToLanguages);
			}
			
			if (getLanguage.contains(Locale.of("zh").getDisplayLanguage()))
			{				
				Shutter.magnetoFont = "Noto Sans SC Medium";
				Shutter.montserratFont = "Noto Sans SC Medium";
				Shutter.freeSansFont = "Noto Sans SC Medium";
			}
			else if (getLanguage.contains(Locale.of("ja").getDisplayLanguage())
			|| getLanguage.equals(Locale.of("ru").getDisplayLanguage())
			|| getLanguage.equals(Locale.of("uk").getDisplayLanguage())) //use system default font
			{
				Shutter.magnetoFont = "";
				Shutter.montserratFont = "";
				Shutter.freeSansFont = "";
			}
			else if (getLanguage.contains(Locale.of("vi").getDisplayLanguage())
			|| getLanguage.contains(Locale.of("pl").getDisplayLanguage())) //use system default font
			{
				Shutter.magnetoFont = "";
				Shutter.montserratFont = "FreeSans";
			}
			else if (getLanguage.equals(Locale.of("sl").getDisplayLanguage()) || getLanguage.equals(Locale.of("cs").getDisplayLanguage()))
			{
				Shutter.montserratFont = "FreeSans";
			}
						
			language.load(input);	
			input.close();														
			
		} catch (IOException ex) {
			System.out.println(ex);
		}	
	}
	
	private static FileInputStream defaultLanguage(String pathToLanguages) throws FileNotFoundException {
							
		String loadLanguage = pathToLanguages + System.getProperty("user.language") + ".properties";		
		
		//Multiple countries
		if (System.getProperty("user.language").equals("pt") || System.getProperty("user.language").equals("zh"))
			loadLanguage = pathToLanguages + System.getProperty("user.language") + "_" + System.getProperty("user.country") + ".properties";

		if (new File(loadLanguage).exists())
		{
			getLanguage = Locale.of(System.getProperty("user.language")).getDisplayLanguage();
			
			//Multiple countries
			if (System.getProperty("user.language").equals("pt") || System.getProperty("user.language").equals("zh"))
				getLanguage = Locale.of(System.getProperty("user.language")).getDisplayLanguage() + " (" + Locale.of(System.getProperty("user.language"), System.getProperty("user.country")).getDisplayCountry() + ")";
						
			return new FileInputStream(loadLanguage);
		}
		else
		{
			getLanguage = "English";
			return new FileInputStream(pathToLanguages + "en.properties");
		}		
	}
	
	public static void copyFile(File file) {		
		//Destination 2
		if (caseChangeFolder2.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(1);
			File filein  = file;
	        File fileout = new File(lblDestination2.getText() + "/" + file.getName());
			try {		
		        long length  = filein.length();
				progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
		
		//Destination 3
		if (caseChangeFolder3.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(2);
			File filein  = file;
	        File fileout = new File(lblDestination3.getText() + "/" + file.getName());
			try {		
		        long length  = filein.length();
		        progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {	                	
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
	}

	@SuppressWarnings("deprecation")
	public static File UNCPath(File file) {		
		
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	try {
			String root[] = file.toString().substring(2, file.toString().length()).split("\\\\");
			String rootFolder = "\\\\" + root[0] + "\\" + root[1];
			
			//Récupération du nom et label des disques connectés
	        for (File path:File.listRoots()) 
	        {
		         FileSystemView view = FileSystemView.getFileSystemView();
		         File dir = new File(path.toString());
		         
		         String name = view.getSystemDisplayName(dir);
		         
		         //Si le disque réseau contient le dossier root du fichier
		         if (name.contains(root[1])) 
			         return new File (file.toString().replace(rootFolder, path.toString().substring(0, path.toString().length() - 1)));
	        }         
	        
	    	//Si le disque n'existe pas alors on le map
			Process process = Runtime.getRuntime().exec("net.exe use * " + '"' + rootFolder + '"');
			
			BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(process.getInputStream()));
			
			String line;
			while ((line = stdInput.readLine()) != null) {
			    if (line.contains(":"))
			    {
			    	String s[] = line.split(":");
			    	String d[] = s[0].split(" ");
					return new File(file.toString().replace(rootFolder, d[d.length - 1] + ":"));
			    }
			}
	         
		} catch (IOException e) {			 
		} finally {			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	
		return file;
	}
	
	public static String filesNumber() {
		
		if (scanIsRunning)
			return "Scan...";

		String labelName;
		int total = liste.getSize();
		if (total > 1 && total < 1000)
			labelName = total + " " + language.getProperty("files");
		else if (total <= 1)
			labelName = total + " " + language.getProperty("file");
		else
			labelName = total / 1000 + "k " + language.getProperty("files");
		
		return labelName;
	}

	public static void findFiles(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			
			if (f.isDirectory()) 
			{
				findFiles(f.getAbsolutePath());
			}
			else
			{
				int s = f.getAbsoluteFile().toString().lastIndexOf('.');
				String ext = f.getAbsoluteFile().toString().substring(s);
				
				if (f.getAbsoluteFile().toString().substring(s).toLowerCase().equals(comboFilter.getSelectedItem().toString())
						|| comboFilter.getSelectedItem().toString().equals(language.getProperty("aucun"))
						|| lblFilter.getText().equals(language.getProperty("lblFilter")) == false)
				{
					if (ext.equals(".enc")) 
					{
						loadSettings(new File (f.getAbsoluteFile().toString()));
					}
					else if (f.isHidden() == false && f.getName().contains("."))
					{			
						if (f.getAbsoluteFile().toString().contains("\"") || f.getAbsoluteFile().toString().contains("\'") || f.getName().contains("/") || f.getName().contains("\\"))
						{
							if (FunctionUtils.allowsInvalidCharacters == false) 
							{
								JOptionPane.showConfirmDialog(Shutter.frame, f.getAbsoluteFile().toString() + System.lineSeparator() + Shutter.language.getProperty("invalidCharacter"), Shutter.language.getProperty("import"),
								JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
								
								FunctionUtils.allowsInvalidCharacters = true;
							}
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
								Shutter.liste.addElement(f.getAbsoluteFile().toString());	
								Shutter.addToList.setVisible(false);
								Shutter.lblFiles.setText(Utils.filesNumber());
							}
						}
						else
						{
							Shutter.liste.addElement(f.getAbsoluteFile().toString());
							Shutter.addToList.setVisible(false);
							Shutter.lblFiles.setText(Utils.filesNumber());
						}
					}
				}
			}
		}
		
		lblFiles.setText(filesNumber());;
	}
	
	public static void findDirectories(String path) {
	
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			
			if (f.isDirectory()) 
			{
				Shutter.liste.addElement(f.getAbsoluteFile().toString());
				Shutter.addToList.setVisible(false);
				Shutter.lblFiles.setText(Utils.filesNumber());
				
				findDirectories(f.getAbsolutePath());
			}
		}	
		
		lblFiles.setText(filesNumber());;
	}
	
	@SuppressWarnings({"rawtypes"})
	public static void saveSettings(boolean update) {
		
		File oldFolder = new File(Functions.functionsFolder.toString().replace("Functions", "Fonctions"));
		if (oldFolder.exists())
		{
			oldFolder.renameTo(Functions.functionsFolder);
		}
		
		if (Functions.functionsFolder.exists() == false)
			Functions.functionsFolder.mkdir();
		
		
		FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveSettings"), FileDialog.SAVE);
		dialog.setDirectory(Functions.functionsFolder.toString()); 
		dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
		dialog.setAlwaysOnTop(true);
	
		if (update == false)
			dialog.setVisible(true);
		else
			dialog.setFile("/" + Functions.listeDeFonctions.getSelectedValue()); //le "/" Contourne un bug							
		
		 if (dialog.getFile() != null)
		 { 
			try {
				
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();
				
				Element root = document.createElement("Shutter");
				document.appendChild(root);
								
				Element settings = document.createElement("Settings");				
				
				//btnExtension
				//Component
				Element component = document.createElement("Component");
				
				//Type
				Element cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JCheckBox"));
				component.appendChild(cType);
				
				//Name
				Element cName = document.createElement("Name");
				cName.appendChild(document.createTextNode(Shutter.btnExtension.getName()));
				component.appendChild(cName);
				
				//Value
				Element cValue = document.createElement("Value");
				cValue.appendChild(document.createTextNode(String.valueOf(Shutter.btnExtension.isSelected())));
				component.appendChild(cValue);
				
				settings.appendChild(component);
				
				//Suffix
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("txtExtension"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");				
				cValue.appendChild(document.createTextNode(Shutter.txtExtension.getText().toString()));
				component.appendChild(cValue);
				
				settings.appendChild(component);
				
				//caseSubFolder
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JCheckBox"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode(Shutter.caseSubFolder.getName()));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");
				cValue.appendChild(document.createTextNode(String.valueOf(Shutter.caseSubFolder.isSelected())));
				component.appendChild(cValue);
				
				settings.appendChild(component);
				
				//SubFolder
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("txtSubFolder"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");				
				cValue.appendChild(document.createTextNode(Shutter.txtSubFolder.getText().toString()));
				component.appendChild(cValue);
				
				settings.appendChild(component);
								
				for (Component c : frame.getContentPane().getComponents())
				{
					if (c instanceof JPanel)
					{						
						for (Component p : ((JPanel) c).getComponents())
						{
							if (p.getName() != "" && p.getName() != null)
							{
								if (p instanceof JButton)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JButton"));
									component.appendChild(cType);
									
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");								
									cValue.appendChild(document.createTextNode(((JButton) p).getForeground().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JLabel)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JLabel"));
									component.appendChild(cType);
									
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									if (p.getName().equals("lock") == false && p.getName().equals("unlock") == false)
									{
										cValue = document.createElement("Value");
										cValue.appendChild(document.createTextNode(((JLabel) p).getText()));
										component.appendChild(cValue);
									}
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JCheckBox)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JCheckBox"));
									component.appendChild(cType);
									
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JCheckBox) p).isSelected())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JComboBox)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JComboBox"));
									component.appendChild(cType);
																		
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JComboBox) p).getSelectedItem().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JTextField)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JTextField"));
									component.appendChild(cType);
									
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JSlider)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JSlider"));
									component.appendChild(cType);
	
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JSlider) p).getValue())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JSpinner)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JSpinner"));
									component.appendChild(cType);
		
									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JSpinner) p).getValue())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JPanel)
								{
									//Component
									component = document.createElement("Component");
									
									//Type
									cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JPanel"));
									component.appendChild(cType);

									//Name
									cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JPanel) p).getBackground())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									root.appendChild(component);
								}
							}
						}
					}
				}
				
				root.appendChild(settings);
								
				//Saving grpAdjustement values	
				Element color = document.createElement("Color");
				
				if (Shutter.caseEnableColorimetry.isSelected())
				{											
					//allR
					Element componentallR = document.createElement("Component");
					
					//Type
					Element cTypeallR = document.createElement("Type");
					cTypeallR.appendChild(document.createTextNode("String"));
					componentallR.appendChild(cTypeallR);
	
					//Name
					Element cNameallR = document.createElement("Name");
					cNameallR.appendChild(document.createTextNode("allR"));
					componentallR.appendChild(cNameallR);
					
					//Value
					Element cValueallR = document.createElement("Value");
					cValueallR.appendChild(document.createTextNode(String.valueOf(Colorimetry.allR)));
					componentallR.appendChild(cValueallR);
	
					color.appendChild(componentallR);
					
					//allG
					Element componentallG = document.createElement("Component");
					
					//Type
					Element cTypeallG = document.createElement("Type");
					cTypeallG.appendChild(document.createTextNode("String"));
					componentallG.appendChild(cTypeallG);
	
					//Name
					Element cNameallG = document.createElement("Name");
					cNameallG.appendChild(document.createTextNode("allG"));
					componentallG.appendChild(cNameallG);
					
					//Value
					Element cValueallG = document.createElement("Value");
					cValueallG.appendChild(document.createTextNode(String.valueOf(Colorimetry.allG)));
					componentallG.appendChild(cValueallG);
	
					color.appendChild(componentallG);
					
					//allB
					Element componentallB = document.createElement("Component");
					
					//Type
					Element cTypeallB = document.createElement("Type");
					cTypeallB.appendChild(document.createTextNode("String"));
					componentallB.appendChild(cTypeallB);
	
					//Name
					Element cNameallB = document.createElement("Name");
					cNameallB.appendChild(document.createTextNode("allB"));
					componentallB.appendChild(cNameallB);
					
					//Value
					Element cValueallB = document.createElement("Value");
					cValueallB.appendChild(document.createTextNode(String.valueOf(Colorimetry.allB)));
					componentallB.appendChild(cValueallB);
	
					color.appendChild(componentallB);
					
					//highR
					Element componenthighR = document.createElement("Component");
					
					//Type
					Element cTypehighR = document.createElement("Type");
					cTypehighR.appendChild(document.createTextNode("String"));
					componenthighR.appendChild(cTypehighR);
	
					//Name
					Element cNamehighR = document.createElement("Name");
					cNamehighR.appendChild(document.createTextNode("highR"));
					componenthighR.appendChild(cNamehighR);
					
					//Value
					Element cValuehighR = document.createElement("Value");
					cValuehighR.appendChild(document.createTextNode(String.valueOf(Colorimetry.highR)));
					componenthighR.appendChild(cValuehighR);
	
					color.appendChild(componenthighR);
					
					//highG
					Element componenthighG = document.createElement("Component");
					
					//Type
					Element cTypehighG = document.createElement("Type");
					cTypehighG.appendChild(document.createTextNode("String"));
					componenthighG.appendChild(cTypehighG);
	
					//Name
					Element cNamehighG = document.createElement("Name");
					cNamehighG.appendChild(document.createTextNode("highG"));
					componenthighG.appendChild(cNamehighG);
					
					//Value
					Element cValuehighG = document.createElement("Value");
					cValuehighG.appendChild(document.createTextNode(String.valueOf(Colorimetry.highG)));
					componenthighG.appendChild(cValuehighG);
	
					color.appendChild(componenthighG);
					
					//highB
					Element componenthighB = document.createElement("Component");
					
					//Type
					Element cTypehighB = document.createElement("Type");
					cTypehighB.appendChild(document.createTextNode("String"));
					componenthighB.appendChild(cTypehighB);
	
					//Name
					Element cNamehighB = document.createElement("Name");
					cNamehighB.appendChild(document.createTextNode("highB"));
					componenthighB.appendChild(cNamehighB);
					
					//Value
					Element cValuehighB = document.createElement("Value");
					cValuehighB.appendChild(document.createTextNode(String.valueOf(Colorimetry.highB)));
					componenthighB.appendChild(cValuehighB);
	
					color.appendChild(componenthighB);
					
					//mediumR
					Element componentmediumR = document.createElement("Component");
					
					//Type
					Element cTypemediumR = document.createElement("Type");
					cTypemediumR.appendChild(document.createTextNode("String"));
					componentmediumR.appendChild(cTypemediumR);
	
					//Name
					Element cNamemediumR = document.createElement("Name");
					cNamemediumR.appendChild(document.createTextNode("mediumR"));
					componentmediumR.appendChild(cNamemediumR);
					
					//Value
					Element cValuemediumR = document.createElement("Value");
					cValuemediumR.appendChild(document.createTextNode(String.valueOf(Colorimetry.mediumR)));
					componentmediumR.appendChild(cValuemediumR);
	
					color.appendChild(componentmediumR);
					
					//mediumG
					Element componentmediumG = document.createElement("Component");
					
					//Type
					Element cTypemediumG = document.createElement("Type");
					cTypemediumG.appendChild(document.createTextNode("String"));
					componentmediumG.appendChild(cTypemediumG);
	
					//Name
					Element cNamemediumG = document.createElement("Name");
					cNamemediumG.appendChild(document.createTextNode("mediumG"));
					componentmediumG.appendChild(cNamemediumG);
					
					//Value
					Element cValuemediumG = document.createElement("Value");
					cValuemediumG.appendChild(document.createTextNode(String.valueOf(Colorimetry.mediumG)));
					componentmediumG.appendChild(cValuemediumG);
	
					color.appendChild(componentmediumG);
					
					//mediumB
					Element componentmediumB = document.createElement("Component");
					
					//Type
					Element cTypemediumB = document.createElement("Type");
					cTypemediumB.appendChild(document.createTextNode("String"));
					componentmediumB.appendChild(cTypemediumB);
	
					//Name
					Element cNamemediumB = document.createElement("Name");
					cNamemediumB.appendChild(document.createTextNode("mediumB"));
					componentmediumB.appendChild(cNamemediumB);
					
					//Value
					Element cValuemediumB = document.createElement("Value");
					cValuemediumB.appendChild(document.createTextNode(String.valueOf(Colorimetry.mediumB)));
					componentmediumB.appendChild(cValuemediumB);
	
					color.appendChild(componentmediumB);										
									
					//lowR
					Element componentlowR = document.createElement("Component");
					
					//Type
					Element cTypelowR = document.createElement("Type");
					cTypelowR.appendChild(document.createTextNode("String"));
					componentlowR.appendChild(cTypelowR);
	
					//Name
					Element cNamelowR = document.createElement("Name");
					cNamelowR.appendChild(document.createTextNode("lowR"));
					componentlowR.appendChild(cNamelowR);
					
					//Value
					Element cValuelowR = document.createElement("Value");
					cValuelowR.appendChild(document.createTextNode(String.valueOf(Colorimetry.lowR)));
					componentlowR.appendChild(cValuelowR);
	
					color.appendChild(componentlowR);
					
					//lowG
					Element componentlowG = document.createElement("Component");
					
					//Type
					Element cTypelowG = document.createElement("Type");
					cTypelowG.appendChild(document.createTextNode("String"));
					componentlowG.appendChild(cTypelowG);
	
					//Name
					Element cNamelowG = document.createElement("Name");
					cNamelowG.appendChild(document.createTextNode("lowG"));
					componentlowG.appendChild(cNamelowG);
					
					//Value
					Element cValuelowG = document.createElement("Value");
					cValuelowG.appendChild(document.createTextNode(String.valueOf(Colorimetry.lowG)));
					componentlowG.appendChild(cValuelowG);
	
					color.appendChild(componentlowG);
					
					//lowB
					Element componentlowB = document.createElement("Component");
					
					//Type
					Element cTypelowB = document.createElement("Type");
					cTypelowB.appendChild(document.createTextNode("String"));
					componentlowB.appendChild(cTypelowB);
	
					//Name
					Element cNamelowB = document.createElement("Name");
					cNamelowB.appendChild(document.createTextNode("lowB"));
					componentlowB.appendChild(cNamelowB);
					
					//Value
					Element cValuelowB = document.createElement("Value");
					cValuelowB.appendChild(document.createTextNode(String.valueOf(Colorimetry.lowB)));
					componentlowB.appendChild(cValuelowB);
	
					color.appendChild(componentlowB);
					
					//vibranceValue
					Element componentvibranceValue = document.createElement("Component");
					
					//Type
					Element cTypevibranceValue = document.createElement("Type");
					cTypevibranceValue.appendChild(document.createTextNode("String"));
					componentvibranceValue.appendChild(cTypevibranceValue);
	
					//Name
					Element cNamevibranceValue = document.createElement("Name");
					cNamevibranceValue.appendChild(document.createTextNode("vibranceValue"));
					componentvibranceValue.appendChild(cNamevibranceValue);
					
					//Value
					Element cValuevibranceValue = document.createElement("Value");
					cValuevibranceValue.appendChild(document.createTextNode(String.valueOf(Colorimetry.vibranceValue)));
					componentvibranceValue.appendChild(cValuevibranceValue);
	
					color.appendChild(componentvibranceValue);
					
					//vibranceR
					Element componentvibranceR = document.createElement("Component");
					
					//Type
					Element cTypevibranceR = document.createElement("Type");
					cTypevibranceR.appendChild(document.createTextNode("String"));
					componentvibranceR.appendChild(cTypevibranceR);
	
					//Name
					Element cNamevibranceR = document.createElement("Name");
					cNamevibranceR.appendChild(document.createTextNode("vibranceR"));
					componentvibranceR.appendChild(cNamevibranceR);
					
					//Value
					Element cValuevibranceR = document.createElement("Value");
					cValuevibranceR.appendChild(document.createTextNode(String.valueOf(Colorimetry.vibranceR)));
					componentvibranceR.appendChild(cValuevibranceR);
	
					color.appendChild(componentvibranceR);
					
					//vibranceG
					Element componentvibranceG = document.createElement("Component");
					
					//Type
					Element cTypevibranceG = document.createElement("Type");
					cTypevibranceG.appendChild(document.createTextNode("String"));
					componentvibranceG.appendChild(cTypevibranceG);
	
					//Name
					Element cNamevibranceG = document.createElement("Name");
					cNamevibranceG.appendChild(document.createTextNode("vibranceG"));
					componentvibranceG.appendChild(cNamevibranceG);
					
					//Value
					Element cValuevibranceG = document.createElement("Value");
					cValuevibranceG.appendChild(document.createTextNode(String.valueOf(Colorimetry.vibranceG)));
					componentvibranceG.appendChild(cValuevibranceG);
	
					color.appendChild(componentvibranceG);
					
					//vibranceB
					Element componentvibranceB = document.createElement("Component");
					
					//Type
					Element cTypevibranceB = document.createElement("Type");
					cTypevibranceB.appendChild(document.createTextNode("String"));
					componentvibranceB.appendChild(cTypevibranceB);
	
					//Name
					Element cNamevibranceB = document.createElement("Name");
					cNamevibranceB.appendChild(document.createTextNode("vibranceB"));
					componentvibranceB.appendChild(cNamevibranceB);
					
					//Value
					Element cValuevibranceB = document.createElement("Value");
					cValuevibranceB.appendChild(document.createTextNode(String.valueOf(Colorimetry.vibranceB)));
					componentvibranceB.appendChild(cValuevibranceB);
	
					color.appendChild(componentvibranceB);
					
					root.appendChild(color);
				}	
																														
				// creation du fichier XML
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new File(dialog.getDirectory() + dialog.getFile().toString().replace(".enc", "")) + ".enc");
	
				transformer.transform(domSource, streamResult);
				
				//Mise à jour de l'affichage
				if (update == false && Functions.frame != null)
				{
					if (Functions.frame.isVisible())					
						changeFrameVisibility(Functions.frame, true);
					
					changeFrameVisibility(Functions.frame, false);
				}
				else if (update == false)
				{
					new Functions();
					Functions.frame.setVisible(true);
					Functions.frame.toFront();
				}
				else
					Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				iconPresets.setVisible(false);
				if (iconList.isVisible())
					btnCancel.setBounds(207, 46, 97, 21);
				else
					btnCancel.setBounds(184, 46, 120, 21);
				
			} catch (ParserConfigurationException | TransformerException e) {}
		 }
	}
		
	@SuppressWarnings("rawtypes")
	public static void loadSettings(File encFile) {
	
		if (liste.getSize() == 0)
		{
			JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),	language.getProperty("noFile"), JOptionPane.ERROR_MESSAGE);
			Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else
		{		
			loadEncFile = new Thread(new Runnable() {
	
				@Override
				public void run() {
	
					try {
						
						doNotLoadImage = true;					
						
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
																
								//Type						
								for (Component c : frame.getContentPane().getComponents())
								{
									if (c instanceof JPanel)
									{
										for (Component p : ((JPanel) c).getComponents())
										{				
											if (p.getName() != "" && p.getName() != null && p.getName().equals("text"))
											{
												continue;
											}
											
											//For lock icon only
											if (p.getName() != "" && p.getName() != null)
											{
												if ((p.getName().equals("lock") || p.getName().equals("unlock")) && eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("lock"))
												{
													lock.setIcon(new FlatSVGIcon("contents/lock.svg", 16, 16));
													isLocked = true;
													lock.setName("lock");
												}
											}
																			
											if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
											{									
												if (p instanceof JLabel)
												{										
													//Value
													if (p.getName().equals("lock") == false && p.getName().equals("unlock") == false)
													{
														((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
													}
																						
													//State
													((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
													
													//Visible
													((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));									
												}
												else if (p instanceof JCheckBox)
												{			
													//State
													((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
													
													//Visible
													((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
													
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
																										
												}
												else if (p instanceof JComboBox)
												{			
													if (p.getName().equals("comboFonctions"))
													{														
														VideoPlayer.frameIsComplete = false;														
														doNotLoadImage = false;
													}
													
													if (p.getName().equals("comboAccel"))
													{																											
														hwaccel = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent();
													}
													
													if (p.getName().equals("comboLUTs"))
													{
														comboLUTs.setSelectedIndex(10);
	
												        for (int i = 0 ; i < comboLUTs.getModel().getSize() ; i++) 
												        { 
												        	if (comboLUTs.getModel().getElementAt(i).toString().equals(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
												        	{
												        		comboLUTs.setSelectedIndex(i);
												        		break;
												        	}
												        }
													}
													else
													{
														//Value
														((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																
														if (p.getName().equals("comboAccel") == false && p.getName().equals("comboGPUDecoding") == false && p.getName().equals("comboGPUFilter") == false)
														{
															//State
															((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
														}														
														
														//Visible
														((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
													}
													
													long time = System.currentTimeMillis();
													
													if (p.getName().equals("comboFonctions"))
													{																												
														do {
															try {
																Thread.sleep(100);
															} catch (InterruptedException er) {}
															
															if (System.currentTimeMillis() - time > 1000)
																VideoPlayer.frameIsComplete = true;
																						
														} while (VideoPlayer.frameIsComplete == false);
																	
														doNotLoadImage = true;
													}
												}
												else if (p instanceof JTextField)
												{
													//Value
													((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																						
													//State
													((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
													
													//Visible
													((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
												
													//Elements position
													if (p.getName().equals("textNamePosX"))
														fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / Shutter.playerRatio), fileName.getLocation().y);	
													
													if (p.getName().equals("textNamePosY"))
														fileName.setLocation(fileName.getLocation().x, (int) Math.round(Integer.valueOf(textNamePosY.getText()) / Shutter.playerRatio));
																						
													if (p.getName().equals("textTcPosY"))
														timecode.setLocation(timecode.getLocation().x, (int) Math.round(Integer.valueOf(textTcPosY.getText()) / Shutter.playerRatio));
													
													if (p.getName().equals("textTcPosX"))
														timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / Shutter.playerRatio), timecode.getLocation().y);
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
												else if (p instanceof JSpinner)
												{							
													//Value
													((JSpinner) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																						
													//State
													((JSpinner) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
													
													//Visible
													((JSpinner) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
												}
												else if (p instanceof JPanel)
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
													
													if (p.getName().equals("panelSubsColor"))
													{
														fontSubsColor = panelSubsColor.getBackground();
													}
													else if (p.getName().equals("panelSubsColor2"))
													{
														backgroundSubsColor = panelSubsColor2.getBackground();
													}
												}
											}
										}
									}
								}
								
								//btnExtension
								if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("btnExtension"))
								{
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == true)
									{
										btnExtension.setSelected(true);
										txtExtension.setEnabled(true);
									}
									else
									{
										btnExtension.setSelected(false);
										txtExtension.setEnabled(false);
									}
								}
									
								//Suffix
								if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("txtExtension"))
								{
									if (eElement.getElementsByTagName("Value").item(0).getFirstChild() != null)
									{
										txtExtension.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
									}
									else
										txtExtension.setText("");
								}
								
								//caseSubFolder
								if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseSubFolder"))
								{
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == true)
									{
										caseSubFolder.setSelected(true);							
										txtSubFolder.setEnabled(true);
									}
									else
									{
										caseSubFolder.setSelected(false);
										txtSubFolder.setEnabled(false);
									}
								}
								
								//SubFolder
								if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("txtSubFolder"))
								{
									if (eElement.getElementsByTagName("Value").item(0).getFirstChild() != null)
									{
										txtSubFolder.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
									}
									else
										txtSubFolder.setText("");
								}
							}
						}
						
						changeFunction(false);	
										
					} catch (Exception e) {
						
						System.out.println(e);
					}
					finally {
						
						doNotLoadImage = false;
						
						//grpCrop
						if (caseEnableCrop.isSelected())
						{
							selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(textCropPosY.getText()) / Shutter.playerRatio));
							int w = (int) Math.round((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
							int h = (int) Math.round((float)  (Integer.valueOf(textCropHeight.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
							
							if (w > VideoPlayer.player.getWidth())
								w = VideoPlayer.player.getWidth();
							
							if (h > VideoPlayer.player.getHeight())
								h = VideoPlayer.player.getHeight();
							
							selection.setSize(w , h);	
							
							frameCropX = VideoPlayer.player.getLocation().x;
							frameCropY = VideoPlayer.player.getLocation().y;
							
							anchorRight = selection.getLocation().x + selection.getWidth();
							anchorBottom = selection.getLocation().y + selection.getHeight();					
							VideoPlayer.checkSelection();
						}
						
						//grpOverlay
						if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
						{
							timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(textTcPosY.getText()) / Shutter.playerRatio));
							tcLocX = timecode.getLocation().x;
							tcLocY = timecode.getLocation().y;	
							timecode.setSize(10,10); //Workaround to not reset the location
						}
						if (caseAddText.isSelected() || caseShowFileName.isSelected())
						{
							fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / Shutter.playerRatio), (int) Math.round(Integer.valueOf(textNamePosY.getText()) / Shutter.playerRatio));
							fileLocX = fileName.getLocation().x;
							fileLocY = fileName.getLocation().y;
							fileName.setSize(10,10); //Workaround to not reset the location
						}
						
						//grpSubtitles
						if (caseAddSubtitles.isSelected())
						{						    		
							if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth)
							{
								subsCanvas.setBounds(0, 0, VideoPlayer.player.getWidth(), (int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));
							}
							else
							{
								subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
							    		(int) (VideoPlayer.player.getHeight() + (float) Integer.parseInt(textSubtitlesPosition.getText()) / ( (float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));	
								
								subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
							}			
						}
						
						//grpWatermark
						if (caseAddWatermark.isSelected())
						{
							VideoPlayer.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
							logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / Shutter.playerRatio), (int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / Shutter.playerRatio));
							//Saving location
							logoLocX = logo.getLocation().x;
							logoLocY = logo.getLocation().y;
						}
						
						VideoPlayer.btnStop.doClick();
						VideoPlayer.resizeAll();
						
						if (lblVBR.getText().equals("CQ"))
						{
							String index = debitVideo.getSelectedItem().toString();
							
							String[] values = new String[53];
							values[0] = language.getProperty("lblBest");
							for (int i = 1 ; i < 52 ; i++)
							{
								values[i] = String.valueOf(i);
							}			
							values[52] = language.getProperty("lblWorst");
							debitVideo.setModel(new DefaultComboBoxModel<String>(values));
							debitVideo.setSelectedItem(index);
						}
						
						if (Functions.frame != null)
						{
							Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

							iconPresets.setVisible(true);
							if (iconList.isVisible())
							{
								iconPresets.setLocation(iconList.getX() + iconList.getWidth() + 2, 45);
								btnCancel.setBounds(207 + iconList.getWidth(), 46, 101 - iconList.getWidth() -  4, 21);
							}
							else
							{
								iconPresets.setBounds(180, 45, 21, 21);
								btnCancel.setBounds(207, 46, 97, 21);
							}
							
							Utils.changeFrameVisibility(Functions.frame, true);
						}
						
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						
						//IMPORTANT
						Shutter.resizeAll(Shutter.frame.getWidth(), 0); 
						Shutter.frame.repaint();						
					}
									
				}
				
			});
			loadEncFile.start();			
			
		}
	}

	public static void loadThemes() {
								
		//Theme
		if (Shutter.settingsXML.exists())
		{				
			try {
				File fXmlFile = Shutter.settingsXML;
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
								
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						
						if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("accentColor"))
						{					
							String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
							String s2[] = s[1].split(",");
							themeColor = new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2]));

						}
					}
				}	

			} catch (Exception e) {}
		}
		
		FlatLaf.setup( new FlatDarkLaf() );
		
		int R = Math.max(0, Math.min(255, themeColor.getRed() + 25));
		int G = Math.max(0, Math.min(255, themeColor.getGreen() + 25));
		int B = Math.max(0, Math.min(255, themeColor.getBlue() + 25));

		highlightColor = new Color(R, G, B);
		
		UIManager.put("Component.focusWidth", 0);
		UIManager.put("Component.innerFocusWidth", 0);
		UIManager.put("Button.innerFocusWidth", 0);
		UIManager.put("ScrollBar.thumbArc", 999);
		UIManager.put("Button.arc", 15);
		UIManager.put("CheckBox.arc", 15);
		UIManager.put("TextField.arc", 10);			
		UIManager.put("ProgressBar.arc", 10);
		UIManager.put("TextComponent.arc", 10);
		UIManager.put("Component.arc", 10);
		UIManager.put("PopupMenu.arc", 15);	
		
		UIManager.put("Component.borderColor", new Color(30,30,35));
		UIManager.put("Component.disabledBorderColor", new Color(30,30,35));
		
		UIManager.put("Button.startBorderColor", new Color(30,30,35));
		UIManager.put("Button.endBorderColor", new Color(30,30,35));
		UIManager.put("Button.startBackground", new Color(42,42,47));
		UIManager.put("Button.endBackground", new Color(42,42,47));
		UIManager.put("Button.disabledBorderColor", new Color(30,30,35));
		UIManager.put("Button.disabledBackground", new Color(42,42,47));
		UIManager.put("Button.foreground", new Color(235,235,240));
		UIManager.put("Button.default.foreground", new Color(235,235,240));
		UIManager.put("Button.default.startBackground", new Color(42,42,47));
		UIManager.put("Button.default.endBackground", new Color(42,42,47));
		UIManager.put("Button.default.borderColor", new Color(30,30,35));
						
		UIManager.put("ComboBox.background", new Color(42,42,47));		
		UIManager.put("ComboBox.foreground", new Color(235,235,240));
		UIManager.put("ComboBox.disabledBackground", new Color(42,42,47));
		UIManager.put("ComboBox.disabledForeground", new Color(120,120,120));
		UIManager.put("ComboBox.buttonBackground", new Color(42,42,47,0));	
		UIManager.put("ComboBox.buttonEditableBackground", new Color(42,42,47));	
		UIManager.put("ComboBox.selectionBackground", themeColor);
		UIManager.put("ComboBox.popupBackground", new Color(42,42,47));
		UIManager.put("ComboBox.buttonSeparatorColor", new Color(30,30,35));	
		UIManager.put("ComboBox.buttonDisabledSeparatorColor", new Color(30,30,35));	
		UIManager.put("ComboBox.buttonArrowColor", themeColor);
		
		UIManager.put("MenuItem.background", new Color(42,42,47));		
		UIManager.put("MenuItem.foreground", new Color(235,235,240));
		UIManager.put("MenuItem.selectionBackground", themeColor);
		
		UIManager.put("CheckBoxMenuItem.background", new Color(42,42,47));		
		UIManager.put("CheckBoxMenuItem.foreground", new Color(235,235,240));
		UIManager.put("CheckBoxMenuItem.selectionBackground", themeColor);
		
		UIManager.put("CheckBox.icon.borderColor", new Color(30,30,35));		
		UIManager.put("CheckBox.icon.background", new Color(42,42,47));
		UIManager.put("CheckBox.icon.disabledBorderColor", new Color(42,42,47));	
		UIManager.put("CheckBox.icon.disabledBackground", new Color(42,42,47));	
		UIManager.put("CheckBox.icon.focusedBorderColor", new Color(30,30,35));
		UIManager.put("CheckBox.icon.focusedBackground", new Color(42,42,47));
		UIManager.put("CheckBox.icon.selectedBorderColor", new Color(30,30,35));
		
		UIManager.put("TableHeader.foreground", new Color(235,235,240));
		UIManager.put("Table.foreground", new Color(235,235,240));
		UIManager.put("Table.selectionBackground", themeColor);
		
		UIManager.put("TableHeader.background", new Color(42,42,47));
					
		UIManager.put("TextField.foreground",new Color(235,235,240));
		UIManager.put("TextField.background", new Color(42,42,47));
		UIManager.put("TextField.selectionBackground", themeColor);
		UIManager.put("TextField.inactiveForeground", new Color(120,120,120));
		
		UIManager.put("TextArea.foreground",new Color(235,235,240));
		UIManager.put("TextArea.background", new Color(42,42,47));
		UIManager.put("TextArea.selectionBackground", themeColor);
		UIManager.put("TextArea.inactiveForeground", new Color(120,120,120));
		
		UIManager.put("PasswordField.foreground", new Color(235,235,240));
		UIManager.put("PasswordField.background", new Color(42,42,47));	
		UIManager.put("PasswordField.selectionBackground", themeColor);			
		UIManager.put("PasswordField.inactiveForeground", new Color(120,120,120));
		
		UIManager.put("Spinner.foreground", new Color(235,235,240));
		UIManager.put("Spinner.disabledBackground", new Color(42,42,47));
		UIManager.put("Spinner.background", new Color(42,42,47));
		UIManager.put("FormattedTextField.selectionBackground", themeColor);
		UIManager.put("Spinner.buttonBackground", new Color(42,42,47));
					
		UIManager.put("ScrollBar.background", new Color(30,30,35));
		UIManager.put("ScrollBar.thumb", new Color(42,42,47));
					
		UIManager.put("MenuBar.foreground", new Color(235,235,240));

		UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(30,30,35)));	
		UIManager.put("PopupMenu.background", new Color(42,42,47));
		
		UIManager.put("Panel.selectionForeground", Color.WHITE);
		UIManager.put("TextPane.selectionForeground", Color.WHITE);
		UIManager.put("FormattedTextField.selectionForeground",  Color.WHITE);
		UIManager.put("TextArea.selectionForeground", Color.WHITE);
		UIManager.put("TextField.selectionForeground", Color.WHITE);		
		UIManager.put("PasswordField.selectionForeground", Color.WHITE);		
		UIManager.put("CheckBoxMenuItem.selectionForeground", Color.WHITE);
		UIManager.put("MenuItem.selectionForeground", Color.WHITE);
		UIManager.put("ComboBox.selectionForeground", Color.WHITE);
		UIManager.put("Menu.selectionForeground", Color.WHITE);
										
		UIManager.put("TabbedPane.focusColor", new Color(30,30,35));
		UIManager.put("TabbedPane.tabHeight", 22);
		UIManager.put("TabbedPane.tabInsets", new Insets(0,5,0,5));
		UIManager.put("TabbedPane.background", new Color(35,35,40));
		UIManager.put("TabbedPane.selectedBackground", new Color(30,30,35));
		UIManager.put("TabbedPane.hoverColor", new Color(30,30,35));
		UIManager.put("TabbedPane.highlight", new Color(30,30,35));
		UIManager.put("TabbedPane.underlineColor", new Color(30,30,35));
		UIManager.put("TabbedPane.inactiveUnderlineColor", new Color(30,30,35));
		UIManager.put("TabbedPane.disabledUnderlineColor", new Color(30,30,35));
		UIManager.put("TabbedPane.contentAreaColor", new Color(30,30,35));
		UIManager.put("TabbedPane.foreground", new Color(235,235,240));	
		
		
		UIManager.put("CheckBox.icon.focusWidth", 0);
		UIManager.put("CheckBox.icon.checkmarkColor", themeColor);	
		UIManager.put("CheckBox.icon.hoverBorderColor", highlightColor);
		UIManager.put("CheckBox.icon.selectedFocusedBorderColor", highlightColor);
		UIManager.put("CheckBox.icon.disabledCheckmarkColor", new Color(100, 100, 100));	
		UIManager.put("CheckBox.foreground" , new Color(235,235,240));
		UIManager.put("CheckBox.background" , new Color(42,42,47,0));
		UIManager.put("CheckBox.icon.focusedBackground", "null");
		UIManager.put("RadioButton.icon.centerDiameter", 9);
		
		UIManager.put("ProgressBar.background" , new Color(35,35,40));
		UIManager.put("ProgressBar.foreground" , themeColor);	
		UIManager.put("ProgressBar.selectionBackground", new Color(235,235,240));
        UIManager.put("ProgressBar.selectionForeground", new Color(235,235,240));
		
		UIManager.put("Slider.thumbColor", themeColor);		
		UIManager.put("Slider.hoverColor", highlightColor);
		UIManager.put("Slider.trackValueColor", themeColor);
		UIManager.put("Slider.trackColor", new Color(25,25,25));
		UIManager.put("Slider.thumbSize", new Dimension(10,10));
		UIManager.put("Slider.trackWidth", 4);
		UIManager.put("Slider.focusWidth", 0);	
		
		UIManager.put("ColorChooser.background", new Color(30,30,35));
		UIManager.put("ColorChooser.foreground", new Color(235,235,240));
		        
		UIManager.put("TextPane.foreground", Color.BLACK);
		UIManager.put("TextPane.background", Color.WHITE);
		UIManager.put("TextPane.selectionBackground", themeColor);		
		
		UIManager.put("Component.arrowType", "triangle");
		UIManager.put("Component.focusColor", new Color(120,120,120));
		UIManager.put("Component.focusedBorderColor", highlightColor);
		
		UIManager.put("Button.hoverBorderColor", highlightColor);		
		UIManager.put("Button.focusedBorderColor", highlightColor);
		UIManager.put("Button.default.focusColor", highlightColor);
		UIManager.put("Button.default.hoverBorderColor", highlightColor);
		UIManager.put("Button.default.focusedBorderColor", highlightColor);
			
		UIManager.put("ComboBox.padding", new Insets(2,2,2,0));
				
		UIManager.put("Spinner.padding", new Insets(2,2,2,0));
		
		UIManager.put("Panel.background", new Color(30,30,35));
		
		UIManager.put("TextField.margin", new Insets(0,0,0,0));
				
		UIManager.put("ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ));
		
		UIManager.put("Label.foreground", new Color(235,235,240));		
		
		UIManager.put("OptionPane.background", new Color(30,30,35));
		
		UIManager.put("TitledBorder.titleColor", new Color(235,235,240));
		
		UIManager.put("ScrollBar.minimumThumbSize", new Dimension(11, 100));
		
		FlatInspector.install("ctrl shift alt X");		
	}
	
	@SuppressWarnings("unused")
	public static void restartApp() {
		
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
				newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/"))).replace(" ",
						"\\ ");
				String[] arguments = new String[] { "/bin/bash", "-c", "open -n " + newShutter };
				Process proc = new ProcessBuilder(arguments).start();
			} else { //Linux	
				String[] arguments = new String[] { "/bin/bash", "-c", "shutter-encoder"};
				Process proc = new ProcessBuilder(arguments).start();
			}

		} catch (Exception error) {}
		
		Utils.killProcesses();
		
		System.exit(0);
		
	}

	public static void killProcesses() {
		
		try {
			if (btnStart.getText().equals(Shutter.language.getProperty("btnResumeFunction")))
				FFMPEG.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le
										// détruire
			FFMPEG.process.destroy();
			
			if (FFPROBE.isRunning)
				FFPROBE.process.destroy();
			
			if (BMXTRANSWRAP.isRunning)
				BMXTRANSWRAP.process.destroy();

			if (DCRAW.isRunning)
				DCRAW.process.destroy();

			if (DVDAUTHOR.isRunning)
				DVDAUTHOR.process.destroy();
			
			if (TSMUXER.isRunning)
				TSMUXER.process.destroy();
			
			if (YOUTUBEDL.isRunning)
				YOUTUBEDL.process.destroy();
			
		} catch (Exception er) {}

		if (SceneDetection.outputFolder != null && SceneDetection.outputFolder.exists())
			SceneDetection.deleteDirectory(SceneDetection.outputFolder);

		// Suppression des SRT temporaires
		String rootPath = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();					
		if (System.getProperty("os.name").contains("Windows"))
		{
			rootPath = rootPath.substring(1,rootPath.length()-1);
			rootPath = rootPath.substring(0,(int) (rootPath.lastIndexOf("/"))).replace("%20", " ");
		}
		else
		{
			rootPath = dirTemp;
		}
		
		for (File subs : new File(rootPath).listFiles())
		{
			if (subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("srt")
			|| subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("vtt")
			|| subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("ssa")
			|| subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("ass")
			|| subs.toString().substring(subs.toString().lastIndexOf(".") + 1).equals("scc"))
				subs.delete();
		}	
		
		//Delete vidstab
		File vidstab;
		if (System.getProperty("os.name").contains("Windows"))
			vidstab = new File("vidstab.trf");
		else							    		
			vidstab = new File(Shutter.dirTemp + "vidstab.trf");
		
		if (vidstab.exists())
			vidstab.delete();
		
		//Delete media offline
		File file = new File(dirTemp + "offline.png");
		if (file.exists())
			file.delete();
				
		//Stats_file
		File stats_file = new File(Shutter.dirTemp + "stats_file");					
		if (System.getProperty("os.name").contains("Windows"))
			stats_file = new File("stats_file");					
		if (stats_file.exists())
			stats_file.delete();
		
		//Image sequence
		File concat = new File(Shutter.dirTemp + "concat.txt");					
		if (concat.exists())
			concat.delete();
		
	}
	
}
