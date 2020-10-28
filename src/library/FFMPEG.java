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

package library;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Taskbar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.ColorImage;
import application.Console;
import application.SceneDetection;
import application.RenderQueue;
import application.Functions;
import application.VideoPlayer;
import functions.video.DVD;
import application.OverlayWindow;
import application.Settings;
import application.Shutter;

public class FFMPEG extends Shutter {
	
public static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static BufferedWriter writer;
public static Thread runProcess = new Thread();
public static Process process;
public static String analyseLufs;
public static String analyseVideo;
public static Float mseSensibility = 800f;
public static float newVolume;
public static StringBuilder shortTermValues;
public static StringBuilder blackFrame;
public static StringBuilder mediaOfflineFrame;
public static String inPoint = "";
public static String outPoint = "";
public static String postInPoint = "";	
private static boolean firstInput = true;

public static int differenceMax;

//Moyenne de fps		
private static int frame0 = 0;
private static long time = 0;
public static long elapsedTime = 0;
public static int previousElapsedTime = 0;
private static int fps = 0;
private static int signalstatsFrames = 0;
private static boolean brngError = false;
private static int Y = 0;
private static int U = 0;
private static int V = 0;

private static StringBuilder getAll;

	public static void run(final String cmd) {
			
		time = 0;
		fps = 0;
		signalstatsFrames = 0;
		brngError = false;
		Y = 0;
		U = 0;
		V = 0;

		analyseVideo = "";
		elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
		error = false;	
		firstInput = true;
	    Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd.replace("-y", "-threads " + Settings.txtThreads.getText() + " -y") + System.lineSeparator() + System.lineSeparator());
	    
	    getAll = new StringBuilder();

		if (saveCode)
		{
			if (cmd.contains("-pass 2") == false)
					saveToXML(cmd);
		}
		else if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled() && cmd.contains("preview.bmp") == false && cmd.contains("logo.png") == false)
		{
			//On récupère le nom précédent
			if (lblEncodageEnCours.getText().equals(Shutter.language.getProperty("lblEncodageEnCours")))
				lblEncodageEnCours.setText(RenderQueue.tableRow.getValueAt(RenderQueue.tableRow.getRowCount() - 1, 0).toString());
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
				
			if (caseChangeFolder3.isSelected() && caseChangeFolder2.isSelected())
				RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText() + " | " + lblDestination2.getText() + " | " + lblDestination3.getText()});
			else if (caseChangeFolder2.isSelected())
				RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText() + " | " + lblDestination2.getText()});
			else
				RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText()});
	        lblEncodageEnCours.setText(Shutter.language.getProperty("lblEncodageEnCours"));
		}
		else
		{
			isRunning = true;
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
				disableAll();
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToFFMPEG;
						ProcessBuilder processFFMPEG;
						
						if (System.getProperty("os.name").contains("Windows"))
						{							
							PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
							PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
							
							String pipe = "";	
							if (cmd.contains("pipe:play"))
							{
								PathToFFMPEG = "Library\\ffmpeg.exe";
								String codec = "";
								if (comboFonctions.getSelectedItem().equals("QT Animation"))
									codec = " -vcodec qtrle";								
								
									String aspect ="";
									if (caseForcerDAR.isSelected())
										aspect = ",setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
								
									pipe =  " | " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -loglevel quiet -x 320 -y 180 -alwaysontop -autoexit -an -vf setpts=FRAME_RATE" + aspect + " -i " + '"' + "pipe:play" + '"' + codec + " -window_title " + '"' + Shutter.language.getProperty("viewEncoding") + '"';										
									process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFMPEG + FFPROBE.qtref + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG).replace("-y", "-threads " + Settings.txtThreads.getText() + " -y") + pipe});
							}						
							else if (cmd.contains("pipe:stab")
									|| comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG") ||
									(caseForcerDAR.isSelected() && grpAdvanced.isVisible()
									|| caseAddOverlay.isSelected() && grpOverlay.isVisible() 
									|| caseColor.isSelected() && grpLUTs.isVisible()
									|| caseLUTs.isSelected() && grpLUTs.isVisible()
									|| caseColormatrix.isSelected() && comboInColormatrix.getSelectedItem().toString().equals("HDR") && grpLUTs.isVisible()
									|| caseDeflicker.isSelected() && grpCorrections.isVisible()) && caseDisplay.isSelected() == false)
							{
								PathToFFMPEG = "Library\\ffmpeg.exe";
								process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFMPEG + FFPROBE.qtref + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG).replace("-y", "-threads " + Settings.txtThreads.getText() + " -y")});
							}
							else //Permet de mettre en pause FFMPEG
							{		
								processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + FFPROBE.qtref + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG).replace("-y", "-threads " + Settings.txtThreads.getText() + " -y"));
								//processFFMPEG.directory(new File("E:"));
								process = processFFMPEG.start();
							}
						}
						else
						{
							PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
							PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";
							
							String pipe = "";								
							if (cmd.contains("pipe:play"))
							{
								String codec = "";
								if (comboFonctions.getSelectedItem().equals("QT Animation"))
									codec = " -vcodec qtrle";
								
								String aspect ="";
								if (caseForcerDAR.isSelected())
									aspect = ",setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
								
								pipe =  " | " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -loglevel quiet -x 320 -y 180 -alwaysontop -autoexit -an -vf setpts=FRAME_RATE" + aspect + " -i " + '"' + "pipe:play" + '"'  + codec + " -window_title " + '"' + Shutter.language.getProperty("viewEncoding") + '"';
							}
							
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + FFPROBE.qtref + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG).replace("-y", "-threads " + Settings.txtThreads.getText() + " -y") + pipe);									
							process = processFFMPEG.start();
						}	
							
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
						
						//Permet d'écrire dans le flux
						OutputStream stdin = process.getOutputStream();
				        writer = new BufferedWriter(new OutputStreamWriter(stdin));				        
				        
						while ((line = input.readLine()) != null) {
							getAll.append(line);
							getAll.append(System.lineSeparator());							
							
							Console.consoleFFMPEG.append(line + System.lineSeparator() );		
							
							//Erreurs
							if (line.contains("Invalid data found when processing input") 
									|| line.contains("No such file or directory")
									|| line.contains("Invalid data found")
									|| line.contains("No space left")
									|| line.contains("does not contain any stream")
									|| line.contains("Invalid argument")
									|| line.contains("Error opening filters!")
									|| line.contains("matches no streams")
									|| line.contains("Error while opening encoder")
									|| line.contains("Decoder (codec none) not found")
									|| line.contains("Unknown encoder"))
							{
								error = true;
								//break;
							} 
																	
							if (cancelled == false)
							{
								if (cmd.contains("-pass 2"))	
									Progression(line,true);
								else
									Progression(line,false);	
							}
							else
								break;
																			
						}//While							
						process.waitFor();
							
						if (cancelled == false)
							postAnalyse();						
					   					     																		
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;
							caseRunInBackground.setEnabled(false);	
							caseRunInBackground.setSelected(false);
							caseDisplay.setEnabled(true);
						}
					
				}				
			});		
			runProcess.start();
		}
			
	}
			
	private static String checkList(String cmd) {
		
		if (cmd.contains("pass 2"))
			return RenderQueue.tableRow.getValueAt(RenderQueue.tableRow.getRowCount() - 1, 1).toString().replace("ffmpeg", "").replace("pass 1", "pass 2");
		else
		{		
			//On vérifie que le fichier n'existe pas déjà dans le cas contraire on l'incrémente
			String cmdFinale = cmd;		
			String s[] = cmd.split("\"");
			String cmdFile = s[s.length - 1];
			
			int n = 0;
			for (int i = 0 ; i < RenderQueue.tableRow.getRowCount() ; i++)
			{								
				String s2[] = RenderQueue.tableRow.getValueAt(i, 1).toString().split("\"");
				String renduFile = s2[s2.length - 1];
				
				if (cmdFile.equals(renduFile))
				{
					n++;
					String s3[] = cmd.split("\"");
					String ext = cmdFile.substring(cmdFile.lastIndexOf("."), cmdFile.lastIndexOf(".") + 4);
					
					String originalCmdFile = s3[s3.length - 1];			
					cmdFile = originalCmdFile.replace(ext,  "_" + n + ext);	
				}
			}
			
			String s4[] = cmd.split("\"");
			cmdFinale = cmd.replace(s4[s4.length - 1], cmdFile);
	
			return cmdFinale;
		}
	}

	public static void toFFPLAY(final String cmd) {
			error = false;		
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			isRunning = true;
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToFFMPEG;
						ProcessBuilder processFFMPEG;
						File file;
						if (listeDeFichiers.getSelectedIndices().length == 0)
							file = new File(liste.firstElement());
						else							
							file = new File(listeDeFichiers.getSelectedValue());
					
						String fullscreen = "";
						if (ColorImage.frame != null)
						{
							if (ColorImage.frame.isVisible())							
								fullscreen = " -fs";							
						}
							
						
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToFFMPEG = "Library\\ffmpeg.exe";
							processFFMPEG = new ProcessBuilder("cmd.exe" , "/c",  PathToFFMPEG + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + fullscreen + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + file.getName() + '"');
						}
						else
						{
							PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
							PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + fullscreen + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + file.getName() + '"');	
						}		
										
						Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToFFMPEG + " -hwaccel " + Settings.comboGPU.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + fullscreen + " -i " + '"' + "pipe:play" +  '"' + " -window_title " + '"' + file.getName() + '"'
						+  System.lineSeparator() + System.lineSeparator());
						
						process = processFFMPEG.start();
												
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
						
						while ((line = input.readLine()) != null) {
							
							Console.consoleFFPLAY.append(line + System.lineSeparator() );		
						
							//Erreurs
							if (line.contains("Invalid data found when processing input") 
									|| line.contains("No such file or directory")
									|| line.contains("Invalid data found")
									|| line.contains("No space left")
									|| line.contains("does not contain any stream")
									|| line.contains("Invalid argument")
									|| line.contains("Error opening filters!")
									|| line.contains("matches no streams")
									|| line.contains("Error while opening encoder")
									|| line.contains("Decoder (codec none) not found")
									|| line.contains("Unknown encoder"))
							{
								error = true;
								//break;
							} 								
								 
							 if (line.contains("frame"))
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
							
																			
						}//While					
						process.waitFor();															
					   					     																		
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;
						}
					
				}				
			});		
			runProcess.start();	
	}
	
	public static void hwaccel(final String cmd) {
		
		error = false;		
	    Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
			
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					String PathToFFMPEG;
					ProcessBuilder processFFMPEG;
					if (System.getProperty("os.name").contains("Windows"))
					{							
						PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
						PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
														
						processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));
						process = processFFMPEG.start();
					}
					else
					{
						PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
						PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";

						
						processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));									
						process = processFFMPEG.start();
					}		
					
					String line;
						
					if (cmd.contains("-hwaccels"))
					{
						StringBuilder hwaccels = new StringBuilder();
						InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);
				        
				        hwaccels.append("auto" + System.lineSeparator());
				        
				        while ((line = br.readLine()) != null) 
				        {				        	
				        	
				        	if (line.contains("Hardware acceleration methods") == false && line.equals("") == false && line != null)
				        	{
				        		Console.consoleFFMPEG.append(line + System.lineSeparator());
				        		hwaccels.append(line + System.lineSeparator());
				        	}
				        }
				        
				        hwaccels.append(language.getProperty("aucun"));
				        				        				        
				        Settings.comboGPU = new JComboBox<String>( hwaccels.toString().split(System.lineSeparator()) );
					}
					else
					{
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
											
						while ((line = input.readLine()) != null) {						
							
							Console.consoleFFMPEG.append(line + System.lineSeparator() );		
							
							//Erreurs
							if (line.contains("Invalid data found when processing input") 
									|| line.contains("No such file or directory")
									|| line.contains("Invalid data found")
									|| line.contains("No space left")
									|| line.contains("does not contain any stream")
									|| line.contains("Invalid argument")
									|| line.contains("Error opening filters!")
									|| line.contains("matches no streams")
									|| line.contains("Error while opening encoder")
									|| line.contains("Decoder (codec none) not found")
									|| line.contains("Unknown encoder"))
							{
								error = true;
								//break;
							} 
																			
						}//While			
					}
					
					process.waitFor();					
				   					     																		
					} catch (IOException | InterruptedException e) {
						error = true;
					}
				
			}				
		});		
		runProcess.start();
	}
	
	private static void saveToXML(String cmd) {	  
		
		FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveSettings"), FileDialog.SAVE);
		dialog.setDirectory(Functions.fonctionsFolder.toString());
		dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
								
		 if (dialog.getFile() != null)
		 { 
				try {
					DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
					Document document = documentBuilder.newDocument();
					
					Element root = document.createElement("Shutter");
					document.appendChild(root);

					Element settings = document.createElement("settings");
					root.appendChild(settings);

					Attr attr = document.createAttribute("id");
					attr.setValue("10");
					settings.setAttributeNode(attr);

					String split[] = cmd.split("\"");
					String entree = split[1];	
					int i = 0;
					do
					{
						i ++;	
					} while (i < split.length);
					String sortie = split[i - 1];	

					Element firstName = document.createElement("command");
					firstName.appendChild(document.createTextNode("ffmpeg" + cmd.replace(inPoint, "").replace(" -i ", "").replace('"' + entree + '"', "").replace('"' + sortie + '"', "").replace(" -y ","").replace(" -n ", "")));
					settings.appendChild(firstName);

					// point d'entrée
					Element lastname = document.createElement("pointIn");
					lastname.appendChild(document.createTextNode(inPoint));
					settings.appendChild(lastname);

					// extension
					String ext = cmd.substring(cmd.lastIndexOf("."));
					Element email = document.createElement("extension");
					email.appendChild(document.createTextNode(ext.replace("\"", "")));
					settings.appendChild(email);
					
					// creation du fichier XML
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource domSource = new DOMSource(document);
					StreamResult streamResult = new StreamResult(new File(dialog.getDirectory() + dialog.getFile().toString().replace(".enc", "")) + ".enc");

					transformer.transform(domSource, streamResult);
				} catch (ParserConfigurationException | TransformerException e) {}
		 }				
	}
	
	public static void suspendProcess()
	{
		try {
		        				
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				Runtime.getRuntime().exec("kill -SIGSTOP " + process.pid());
			else
			{					           	            
				String pausep = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pausep = pausep.substring(1,pausep.length()-1);
				pausep = '"' + pausep.substring(0,(int) (pausep.lastIndexOf("/"))).replace("%20", " ")  + "/Library/pausep.exe" + '"';	
				Runtime.getRuntime().exec(pausep + " " + process.pid());
			}
			
			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported())
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.PAUSED);
			
		} catch (SecurityException | IllegalArgumentException | IOException e1) {	}
	}
	
	public static void resumeProcess()
	{
		try {	
			elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
			
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))        
		        Runtime.getRuntime().exec("kill -SIGCONT " + process.pid());
			else
			{				
				String pausep = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pausep = pausep.substring(1,pausep.length()-1);
				pausep = '"' + pausep.substring(0,(int) (pausep.lastIndexOf("/"))).replace("%20", " ")  + "/Library/pausep.exe" + '"';
				Runtime.getRuntime().exec(pausep + " " + process.pid() + " /r");
			}
			
			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported())
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.NORMAL);
			
		} catch (SecurityException | IllegalArgumentException | IOException e1) {	}
	}
	
	public static String setConcat(File file, String sortie) 
	{		
		String extension =  file.toString().substring(file.toString().lastIndexOf("."));
		
		File listeBAB = new File(sortie.replace("\\", "/") + "/" + file.getName().replace(extension, ".txt")); 
		
		if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
		{									
			try {								
				PrintWriter writer = new PrintWriter(listeBAB.toString(), "UTF-8");
				
				NumberFormat formatter = new DecimalFormat("00");
				NumberFormat formatFrame = new DecimalFormat("000");
				
				int h = Integer.parseInt(VideoPlayer.caseInH.getText());
				int m = Integer.parseInt(VideoPlayer.caseInM.getText());
				int s = Integer.parseInt(VideoPlayer.caseInS.getText());
				int f = (int) (Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));	
				
				writer.println("file " + "'" + file + "'");							
				writer.println("outpoint " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f));
				
				h = Integer.parseInt(VideoPlayer.caseOutH.getText());
				m = Integer.parseInt(VideoPlayer.caseOutM.getText());
				s = Integer.parseInt(VideoPlayer.caseOutS.getText());
				f = (int) (Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));	
				
				writer.println("file " + "'" + file + "'");	
				writer.println("inpoint " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f));
				
				FFMPEG.inPoint = "";
				FFMPEG.postInPoint = "";
				FFMPEG.outPoint = "";			
				
				writer.close();
				
			} catch (FileNotFoundException | UnsupportedEncodingException e) {				
				FFMPEG.error  = true;
				if (listeBAB.exists())
					listeBAB.delete();
			}	
			
			return " -safe 0 -f concat";
		}
		else if (Settings.btnSetBab.isSelected()) //Mode concat
		{
			setBAB(file.getName(), extension, sortie);	
			
			if (caseEnableSequence.isSelected() == false)
				return " -safe 0 -f concat";
		}
		
		return "";
	}
	
	protected static void setBAB(String fichier, String extension, String sortie) {
					
		File listeBAB = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt")); 
		
		try {			
			int dureeTotale = 0;
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
			PrintWriter writer = new PrintWriter(listeBAB, "UTF-8");      
			
			for (int i = 0 ; i < liste.getSize() ; i++)
			{				
				//Scanning
				if (Settings.btnWaitFileComplete.isSelected())
	            {
					File file = new File(liste.getElementAt(i));
					
					progressBar1.setIndeterminate(true);
					lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
					lblEncodageEnCours.setText(file.getName());
					tempsRestant.setVisible(false);
					btnStart.setEnabled(false);
					btnCancel.setEnabled(true);
					comboFonctions.setEnabled(false);
					
					long fileSize = 0;
					do {
						fileSize = file.length();
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {} // Permet d'attendre la nouvelle valeur de la copie
					} while (fileSize != file.length() && cancelled == false);

					// pour Windows
					while (file.renameTo(file) == false && cancelled == false) {
						if (file.exists() == false) // Dans le cas où on annule la copie en cours
							break;
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
					
					if (cancelled)
					{
						progressBar1.setIndeterminate(false);
						lblEncodageEnCours.setText(language.getProperty("lblEncodageEnCours"));
						btnStart.setEnabled(true);
						btnCancel.setEnabled(false);
						comboFonctions.setEnabled(true);
						break;
					}
					
					progressBar1.setIndeterminate(false);
					btnCancel.setEnabled(false);
	            }
				//Scanning
				
				FFPROBE.Data(liste.getElementAt(i));
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				} while (FFPROBE.isRunning == true);
				dureeTotale += FFPROBE.totalLength;
				
				writer.println("file '" + liste.getElementAt(i) + "'");
			}				
			writer.close();
						
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			progressBar1.setMaximum((int) (dureeTotale / 1000));
			FFPROBE.totalLength = progressBar1.getMaximum();
			FFMPEG.dureeTotale = progressBar1.getMaximum();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			FFMPEG.error  = true;
			if (listeBAB.exists())
				listeBAB.delete();
		}//End Try
	}
	
	public static void fonctionInOut() throws InterruptedException {
		
		if (caseInAndOut.isSelected())
		{	
			
		//On supprime l'offset
		if (VideoPlayer.caseTcInterne.isSelected())
			VideoPlayer.caseTcInterne.doClick();

		int h = Integer.parseInt(VideoPlayer.caseInH.getText());
		int m = Integer.parseInt(VideoPlayer.caseInM.getText());
		int s = Integer.parseInt(VideoPlayer.caseInS.getText());
		int f = (int) (Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));	
		
		NumberFormat formatter = new DecimalFormat("00");
		NumberFormat formatFrame = new DecimalFormat("000");
			
	        if (VideoPlayer.sliderIn.getValue() > VideoPlayer.sliderIn.getMinimum() 
	        	&& Shutter.comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut")) == false
	        	&& Shutter.comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
	        {
	          if (h > 0 && m == 0 && s < 10)
	          	{
	        	  	h -= 1;
	             	m = 59;
	             	s = 50 + Integer.parseInt(VideoPlayer.caseInS.getText());
	             	postInPoint = " -ss 00:00:10.000";
	             	FFPROBE.timecode3 = String.valueOf(Integer.parseInt(OverlayWindow.TC3.getText()) - 10);
	          	}
	          else if (m > 0 && s < 10)
	          	{
	        	  	m -= 1;
	             	s = 50 + Integer.parseInt(VideoPlayer.caseInS.getText());
	             	postInPoint = " -ss 00:00:10.000";
	             	FFPROBE.timecode3 = String.valueOf(Integer.parseInt(OverlayWindow.TC3.getText()) - 10);
	          	}
	          else if (s < 10)
	          	{
	    			postInPoint = " -ss " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f);
	          	}
	          else if (s >= 10)
	            {
	            	s -= 10;
	            	postInPoint = " -ss 00:00:10.000";
	            	FFPROBE.timecode3 = String.valueOf(Integer.parseInt(OverlayWindow.TC3.getText()) - 10);
	            }
	        }
	        else
	        	postInPoint = "";
	        
	        if (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) > 10000
        		|| Shutter.comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))
	        	|| Shutter.comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")))
	        {
	        	inPoint = " -ss " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f);
	        }
	        else
	        	inPoint = "";

	        if (VideoPlayer.sliderOut.getValue() != VideoPlayer.sliderOut.getMaximum())
	        	outPoint = " -t " + formatter.format(VideoPlayer.dureeHeures) + ":" + formatter.format(VideoPlayer.dureeMinutes) + ":" + formatter.format(VideoPlayer.dureeSecondes) + "." + formatFrame.format(VideoPlayer.dureeImages * 1000 / FFPROBE.currentFPS);
	        else
	        	outPoint = "";
		}
		else
		{
			postInPoint = "";
			inPoint = "";
			outPoint = "";
		}
	}
	
	public static void Progression(String line, final boolean pass2) {				
									
		if (line.contains("Input #1"))
			firstInput = false;
				
		//Calcul de la durée
	    if (line.contains("Duration") && line.contains("Duration: N/A") == false && line.contains("<Duration>") == false && firstInput)
		{
			String str = line.substring(line.indexOf(":") + 2);
			String[] split = str.split(",");	 
	
			String ffmpegTime = split[0].replace(".", ":");	  
					
			if (caseEnableSequence.isSelected())
				dureeTotale = (int) (liste.getSize() / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) );
			else if (caseInAndOut.isSelected())
				dureeTotale = VideoPlayer.dureeHeures * 3600 + VideoPlayer.dureeMinutes * 60 + VideoPlayer.dureeSecondes;
			else
				dureeTotale = (CalculTemps(ffmpegTime));
			
			if (caseConform.isSelected())
			{
				float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));	
				if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))
					dureeTotale = (int) (dureeTotale * (FFPROBE.currentFPS / newFPS ));	
				else if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
					dureeTotale = (int) (dureeTotale * (newFPS / FFPROBE.currentFPS));	
			}
			
			if (comboFonctions.getSelectedItem().toString().equals("functionConform"))
			{
				float newFPS = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" i/s", "").replace(",", ".")));		
				dureeTotale = (int) (dureeTotale * (FFPROBE.currentFPS / newFPS ));
			}
						
			if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
				dureeTotale = 1;
			
			if ((comboFonctions.getSelectedItem().toString().equals("H.264")
					|| comboFonctions.getSelectedItem().toString().equals("H.265")
					|| comboFonctions.getSelectedItem().toString().equals("WMV")
					|| comboFonctions.getSelectedItem().toString().equals("MPEG")
					|| comboFonctions.getSelectedItem().toString().equals("WebM")
					|| comboFonctions.getSelectedItem().toString().equals("AV1")
					|| comboFonctions.getSelectedItem().toString().equals("OGV")
					|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
					|| comboFonctions.getSelectedItem().toString().equals("Xvid")
					|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && DVD.multiplesPass)
				dureeTotale = (dureeTotale * 2);
				
			if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert")) ==  false)			
				progressBar1.setMaximum(dureeTotale);	
		}	    	    
	    
	  //Progression
	  if (line.contains("time="))
	  {
		  	//Il arrive que FFmpeg puisse encoder le fichier alors qu'il a detecté une erreur auparavant, dans ce cas on le laisse continuer donc : error = false;
		  	error = false;
		  	
	  		String str = line.substring(line.indexOf(":") - 2);
    		String[] split = str.split("b");	 
    	    
    		String ffmpegTime = split[0].replace(".", ":").replace(" ", "");	    	

    		if (progressBar1.getString().equals("NaN"))
    			progressBar1.setStringPainted(false);
    		else
    			progressBar1.setStringPainted(true);
    		
			if (pass2)
				progressBar1.setValue((dureeTotale / 2) + CalculTemps(ffmpegTime));
			else
				progressBar1.setValue(CalculTemps(ffmpegTime));
	  }
	  
		//Temps écoulé		
		previousElapsedTime = (int) (System.currentTimeMillis() - elapsedTime);

		int timeH = (previousElapsedTime / 3600000) % 60;
		int timeMin =  (previousElapsedTime / 60000) % 60;
		int timeSec = (previousElapsedTime / 1000) % 60;
		
		String heures = "";
		String minutes= "";
		String secondes = "";
		
		if (timeH >= 1)
			heures = timeH + "h ";
		else
			heures = "";
		if (timeMin >= 1)
			minutes = timeMin + "min ";
		else
			minutes = "";
		if (timeSec > 0)
			secondes = timeSec +"sec";
		else
			secondes = "0sec";
		
		tempsEcoule.setText(Shutter.language.getProperty("tempsEcoule") + " " + heures + minutes + secondes);
	         
	  //Temps Restant
	  if ((line.contains("frame=") || line.contains("time=")) && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false)
	  {
		 String[] split = line.split("=");	
		 int frames = 0;
		 
		 if (line.contains("frame="))
		 {
			 frames = Integer.parseInt(split[1].replace("fps", "").replace(" ", ""));			 
		 }
		 else if (line.contains("time="))
		 {
				String[] rawTime = split[2].split(" ");
				String timecode = rawTime[0].replace(".", ":");	  
				String [] time = timecode.split(":");
								
				int h = Integer.parseInt(time[0]);
				int m = Integer.parseInt(time[1]);
				int s = Integer.parseInt(time[2]);
				int fps = Integer.parseInt(time[3]);
				
				frames = (int) ((h * 3600 * FFPROBE.currentFPS) + (m * 60 * FFPROBE.currentFPS) +  (s * FFPROBE.currentFPS) + fps);  			
		 }
				 
		 if (time == 0)
		 {
			frame0 = frames;
			time = System.currentTimeMillis();
		 }
		 
		 if (System.currentTimeMillis() - time >= 1000 && (frames - frame0) > 0)
		 {		
			 if (fps == 0)
				 fps = (frames - frame0);
			 else
			 {
				 if (frames - frame0 < fps - 100 || frames - frame0 > fps + 100)
					 fps = (frames - frame0);
				 else if (frames - frame0 > fps + 1)
					 fps ++;
				 else if (frames - frame0 < fps - 1 && fps > 1)
					 fps --;				 
			 }
			 					 
			 time = 0;
			 int total;
			 if ((comboFonctions.getSelectedItem().toString().equals("H.264")
						|| comboFonctions.getSelectedItem().toString().equals("H.265")
						|| comboFonctions.getSelectedItem().toString().equals("WMV")
						|| comboFonctions.getSelectedItem().toString().equals("MPEG")
						|| comboFonctions.getSelectedItem().toString().equals("WebM")
						|| comboFonctions.getSelectedItem().toString().equals("AV1")
						|| comboFonctions.getSelectedItem().toString().equals("OGV")
						|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
						|| comboFonctions.getSelectedItem().toString().equals("Xvid")
					 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && DVD.multiplesPass)
				 total = (int) ((dureeTotale / 2) * FFPROBE.currentFPS);
			 
			 else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) == false && caseForcerEntrelacement.isSelected() == false)
			 {
				 float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));	
				 total = (int) ((float) (dureeTotale * FFPROBE.currentFPS) * (newFPS / FFPROBE.currentFPS));
			 }
			 else
				 total = (int) (dureeTotale * FFPROBE.currentFPS);
			 
			 int restant = ((total - frames) / fps);
	 	 
			 if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) == false && comboFonctions.getSelectedItem().equals("Synchronisation automatique") == false)
			 {
				 String pass = "";
				 if ((comboFonctions.getSelectedItem().toString().equals("H.264")
							|| comboFonctions.getSelectedItem().toString().equals("H.265")
							|| comboFonctions.getSelectedItem().toString().equals("WMV")
							|| comboFonctions.getSelectedItem().toString().equals("MPEG")
							|| comboFonctions.getSelectedItem().toString().equals("WebM")
							|| comboFonctions.getSelectedItem().toString().equals("AV1")
						 	|| comboFonctions.getSelectedItem().toString().equals("OGV")
							|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
							|| comboFonctions.getSelectedItem().toString().equals("Xvid")
						 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
						 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && DVD.multiplesPass)
				 {
					 if (pass2 == false)
						 pass = " - " + Shutter.language.getProperty("firstPass");
					 else
						 pass = " - " + Shutter.language.getProperty("secondPass");
				 }
				 		
				timeH = (restant / 3600) % 60;
				timeMin =  (restant / 60) % 60;
				timeSec = (restant) % 60;
				 
				if (timeH >= 1)
					heures = timeH + "h ";
				else
					heures = "";
				if (timeMin >= 1)
					minutes = timeMin + "min ";
				else
					minutes = "";
				if (timeSec > 0)
					secondes = timeSec +"sec";
				else
					secondes = "";
				 
				 tempsRestant.setText(Shutter.language.getProperty("tempsRestant") + " " + heures + minutes + secondes + pass);
				 
				 if (heures != "" || minutes != "" || secondes != "")
					 tempsRestant.setVisible(true);
				 else
					 tempsRestant.setVisible(false);	
			 }
		 }	
		 		 
	  }		
	  else if (line.contains("frame=") && caseDisplay.isSelected() == false) //Pour afficher le temps écoulé
		  tempsEcoule.setVisible(true);
	  
	  //Analyse vidéo
	  
	  //int min = 3535; //4096 - ((60160-4095)*1/100)
	  //int max = 61842;//60160 + ((60160-4095)*3/100)
	  //int cmax = 63160;//61440 + ((61440-4095)*3/100)
	  //int satmax = 32768; //128 * 256
	  
	  int min = 14;
	  int max = 242;
	  int cmax = 247;
	  int satmax = 128; //128 * 256
	  
	  if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionVideoLevels")) && line.contains("Parsed_metadata"))
	  {
		  if (line.contains("frame:"))
	      {
			 String[] split = line.split(":");
			 signalstatsFrames = Integer.parseInt(split[1].replace("pts", "").replace(" ", ""));	
		  }	  
	
		  int temps = (int) ((float) signalstatsFrames * ((float) 1000 / FFPROBE.currentFPS));
		  
		  NumberFormat formatter = new DecimalFormat("00");
		  String h = formatter.format(temps / 3600000);
		  String m = formatter.format((temps / 60000) % 60);
		  String s = formatter.format((temps / 1000) % 60);
		  String f = formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS));

		  //BRNG
		  if (line.contains("BRNG"))
		  {
			  float brng = Float.parseFloat(line.substring(line.lastIndexOf("=") + 1));
			  if (String.valueOf(brng).contains("E") == false && brng > 0.01)
				  brngError = true;
			  else
				  brngError = false;
			  
		  }
		  else if (line.contains("YMIN"))
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  if (luma < min && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " luminance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Luminance <16: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }	
		  else if (line.contains("YMAX")) //Y
		  {		  			  			  
			  int luma = Integer.valueOf(line.substring(line.lastIndexOf("=") + 1));
			  
			  Y = luma;
			  
			  if (luma > max && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " luminance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Luminance >235: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }	 
		  else if (line.contains("UMIN"))
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  if (luma < min && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " chrominance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Chrominance <16: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }	
		  else if (line.contains("UMAX")) //U
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  U = luma;
			   
			  if (luma > cmax && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " chrominance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Chrominance >235: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }
		  else if (line.contains("VMIN"))
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  if (luma < min && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " chrominance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Chrominance <16: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }	
		  else if (line.contains("VMAX")) //V
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  V = luma;
			  
			  if (luma > cmax && brngError)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " chrominance : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Chrominance >235: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }
		  else if (line.contains("SATMAX")) //SAT
		  {		  			  			  
			  int luma = Integer.parseInt(line.substring(line.lastIndexOf("=") + 1));
			  
			  if (luma > satmax)
			  {
				  	lblEncodageEnCours.setForeground(Color.RED);
	        		lblEncodageEnCours.setText(language.getProperty("error") + " saturation : " + h + ":" + m + ":" + s + ":" + f);
	        		analyseVideo += System.lineSeparator() + "     Saturation >128: " + h + ":" + m + ":" + s + ":" + f; 
			  }
		  }
		 
		  //RGB
		  //int R = Integer.valueOf((int) ((float) Y + 1.370705*(V-128)));
		  //int G = Integer.valueOf((int) ((float) Y - 0.698001*(U-128) - 0.337633*(V-128)));
		  //int B = Integer.valueOf((int) ((float) Y + 1.732446*(U-128)));
		  
		  int R = Integer.valueOf((int) ((float) Y + 1.4075*(V-128)));
		  int G = Integer.valueOf((int) ((float) Y - 0.3455*(U-128) - 0.7169*(V-128)));
		  int B = Integer.valueOf((int) ((float) Y + 1.7790*(U-128)));
		  
		  //int R = (int) ((float) Y + 2*(V-128)*(1-0.2126));
		  //int G = (int) ((float) Y - 2*(U-128)*(1-0.0722)*(0.0722/0.7152) - 2*(V-128)*(1-0.2126)*(0.2126/0.7152));
		  //int B = (int) ((float) Y + 2*(U-128)*(1-0.0722));
		  		  		  
		  
		  /*float Kr = 0.2126f;
		  float Kg = 0.7152f;
		  float Kb = 0.0722f;
		  int y = Y;
		  int u = U;
		  int v = V;
		  float R = (255/219)*y + (255/112)*v*(1-Kr) - (255*16/219 + 255*128/112*(1-Kr)) ;
		  float G = (255/219)*y - (255/112)*u*(1-Kb)*Kb/Kg - (255/112)*v*(1-Kr)*Kr/Kg - (255*16/219 - 255/112*128*(1-Kb)*Kb/Kg - 255/112*128*(1-Kr)*Kr/Kg);
		  float B = (255/219)*y + (255/112)*u*(1-Kb) - (255*16/219 + 255*128/112*(1-Kb));*/
		  
		  System.out.println("R : " + R);
		  System.out.println("G : " + G);
		  System.out.println("B : " + B);
		  
		  if (Y != 0 && U != 0 && V != 0)
		  {
			  if (R > 248 || R < 3 || G > 248 || G < 3 || B > 248 || B < 3) //RGB
			  {		  			  			  
			  	lblEncodageEnCours.setForeground(Color.RED);
	    		lblEncodageEnCours.setText(language.getProperty("error") + " RGB : " + h + ":" + m + ":" + s + ":" + f);
	    		analyseVideo += System.lineSeparator() + "     RGB : " + h + ":" + m + ":" + s + ":" + f; 	    					
			
			  }			 
		  }

	  	}
	  
	  //Détection de coupe
	  if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) && line.contains("pts"))
	  {
		  
		  NumberFormat formatter = new DecimalFormat("00");
		  String rawline[] = line.split(":");
		  String fullTime[] = rawline[3].split(" ");
		  int rawTime = (int) (Float.valueOf(fullTime[0]) * 1000);	 
          String h = formatter.format(rawTime / 3600000);
          String m = formatter.format((rawTime / 60000) % 60);
          String s = formatter.format((rawTime / 1000) % 60);          
          String f = formatter.format((int) (rawTime / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS));
    
          File imageName = new File(SceneDetection.sortieDossier + "/" + SceneDetection.tableRow.getRowCount() + ".png");
                    
          //Permet d'attendre la création de l'image
          do {
	          try {
				Thread.sleep(100);
	          } catch (InterruptedException e) {}
          } while (imageName.exists() == false);
          
          ImageIcon imageIcon = new ImageIcon(imageName.toString());
          ImageIcon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(142, 80, Image.SCALE_DEFAULT));	         
          SceneDetection.tableRow.addRow(new Object[] {(SceneDetection.tableRow.getRowCount() + 1), icon, h + ":" + m +  ":" + s + ":" + f});

          SceneDetection.scrollPane.getVerticalScrollBar().setValue(SceneDetection.scrollPane.getVerticalScrollBar().getMaximum());
          SceneDetection.table.repaint();
	  }
	  
	}//End Progression

	private static void postAnalyse() {
		
		//Loudness & Normalisation
	     if (comboFonctions.getSelectedItem().toString().equals("Loudness & True Peak") || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization")))
	     {
               analyseLufs = null;
               analyseLufs = getAll.toString().substring(getAll.toString().lastIndexOf("Summary:") + 12);
                              
               shortTermValues = new StringBuilder();
               
               float momentaryTerm = (float) -1000.0;
               String momentaryTermTC = "";
               float shortTerm = (float) -1000.0;
               String shortTermTC = "";
               
               for (String allValues : getAll.toString().split(System.lineSeparator()))            	   
               {
	    	 		if (allValues.contains("Parsed_ebur128") && allValues.contains("Summary:") == false)
	    	 		{	    	 			
	    	 			//Temps
	    			   	String spliter[] = allValues.split(":"); 	    				
	    				java.text.DecimalFormat round = new java.text.DecimalFormat("0.##");
	    				String splitTime[] = spliter[1].split(" ");
	    			  	int temps = (int) (Float.parseFloat(round.format(Double.valueOf(splitTime[1].replace(",", ""))).replace(",", ".")) * 1000);		    			 				 	
	    	 			
	    			 	//Timecode
	    			 	NumberFormat formatter = new DecimalFormat("00");
	    			 	String h = formatter.format(temps / 3600000);
	    			 	String m = formatter.format((temps / 60000) % 60);
	    			 	String s = formatter.format((temps / 1000) % 60);
	    			 	String f = formatter.format((int) (temps / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS));
	    			 	
	    			 	String timecode = h + ":" + m + ":" + s + ":" + f;	    			 	
	    			 	
	    			 	//Momentary et Short-term
	    	 			String values = allValues.substring(allValues.indexOf("M"));
	    	 			String v[] = values.split(":");
	    	 			
	             	   try {
		    	 			float M = Float.parseFloat(v[1].replace(" S", ""));
		    	 			if (M > momentaryTerm)
		    	 			{
		    	 				momentaryTerm = M;
		    	 				momentaryTermTC = timecode;
		    	 			}
		    	 			float S = Float.parseFloat(v[2].replace("     I", ""));
		    	 			if (S > shortTerm)
		    	 			{
		    	 				shortTerm = S;
		    	 				shortTermTC = timecode;
		    	 			}
		    	 			
		    	 			if (S > -16.0)
		    	 				shortTermValues.append(timecode + ": Short-term: " + S + " LUFS"+ System.lineSeparator());
	             	  } catch (Exception e) {}	 		
	    	 		}	    	 		
               }
               
               analyseLufs += System.lineSeparator() + "  Momentary max: " + momentaryTerm + " LUFS";
               analyseLufs += System.lineSeparator() + "    Timecode:     " + momentaryTermTC;
               analyseLufs += System.lineSeparator();
               analyseLufs += System.lineSeparator() + "  Short-term max: " + shortTerm + " LUFS";
               analyseLufs += System.lineSeparator() + "    Timecode:     " + shortTermTC;
               
               if (shortTermValues.length() == 0)
            	   shortTermValues.append(Shutter.language.getProperty("shortTerm"));  
                              
               if (lblEncodageEnCours.getText().contains(Shutter.language.getProperty("analyzing")))
               {
                   String lufs[] = analyseLufs.split(":");
                   String lufsFinal[] = lufs[2].split("L");
                   String db[] = comboFilter.getSelectedItem().toString().split(" ");
                   newVolume = Float.parseFloat(db[0]) - Float.parseFloat(lufsFinal[0].replace(" ", ""));
               }
	     }	
	     	     
	     //Détection de noir
	     if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionBlackDetection")))
	     {
	    	 	blackFrame = new StringBuilder();
	    	 	
	    	 	for (String blackLine : getAll.toString().split(System.lineSeparator()))
	    	 	{
	    	 		if (blackLine.contains("blackdetect"))
	    	 		{
	    	 			String blackdetect = blackLine.substring(blackLine.indexOf("black_start"));
	    	 			String d[] = blackdetect.split(":");
	    	 			
	    	 			float duration = Float.parseFloat(d[3]);
	    	 			float maxDuration = (float) Integer.parseInt(Settings.txtBlackDetection.getText())/FFPROBE.currentFPS;
	    	 			
	    	 			//On limite la durée du noir (en secondes)
	    	 			if (duration <= maxDuration)
	    	 			{					    	 				
	    	 				String blackstart = d[1].replace(" black_end", "");
	    	 				String bsDuree[] = blackstart.split("\\.");
	    	 					    	 				
	    	 				int secondes = Integer.valueOf(bsDuree[0]);
	    	 				int images = 0;

			    			NumberFormat formatter = new DecimalFormat("00");
			    			String tcBlackFrame = (formatter.format(secondes / 3600)) 
			    					+ ":" + (formatter.format((secondes / 60) % 60))
			    					+ ":" + (formatter.format(secondes % 60)); 	
			    			
			    			switch (bsDuree[1].length())
			    			{
			    				case 1:
			    					images = Integer.valueOf(bsDuree[1]) * 100;
			    					break;
			    				case 2:
			    					images = Integer.valueOf(bsDuree[1]) * 10;
			    					break;
			    				case 3:
			    					images = Integer.valueOf(bsDuree[1]);	
			    					break;
			    			}
			    			
			    			tcBlackFrame += ":" + formatter.format((int) (images / (1000 / FFPROBE.currentFPS)));
			    			
	    	 				blackFrame.append(tcBlackFrame + System.lineSeparator());
	    	 			}
	    	 		}
	    	 	}
	     }
	     
	     //Détection de media offline
	     if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionOfflineDetection")))
	     {
	    	 mediaOfflineFrame = new StringBuilder();
	    	 	
				//Stats_file
				File stats_file;
				if (System.getProperty("os.name").contains("Windows"))
					stats_file = new File("stats_file");
				else		    		
					stats_file = new File(Shutter.dirTemp + "stats_file");
	    	 	
	    	 	if (stats_file.exists())	    	 
	    	 	{	    	 		
	    			try {
	    				BufferedReader reader = new BufferedReader(new FileReader(stats_file.toString()));
	    				
	    				boolean offline = false; 
    					Float mseValue = 0f;
    					
	    				String line = reader.readLine();
	    				while (line != null) {
	    						    					
	    					if (line.contains("mse_avg"))
			    	 		{
			    	 			String s[] = line.split(":");
			    	 			String m[] = s[2].split(" ");
			    	 			Float mse = Float.parseFloat(m[0]);	    	 		
			    	 		
			    	 			String f[] = s[1].split(" ");
			    	 			String frame = f[0]; 
			    	 			
		    	 				int frameNumber = (Integer.parseInt(frame) - 2);
		    	 				
		    	 				if (mse <= mseSensibility && offline == false)
		    	 				{			
		    	 					//Pemet de vérifier sur 2 images pour ne pas confondre avec un fondu
		    	 					if ((float) mseValue == (float) mse)
		    	 					{
		    	 						offline = true;
		    	 					
			    	 					NumberFormat formatter = new DecimalFormat("00");
						    			String tcOfflineFrame = (formatter.format(Math.floor(frameNumber / FFPROBE.currentFPS) / 3600)) 
						    					+ ":" + (formatter.format(Math.floor((frameNumber / FFPROBE.currentFPS) / 60) % 60))
						    					+ ":" + (formatter.format(Math.floor(frameNumber / FFPROBE.currentFPS) % 60)
						    					+ ":" + (formatter.format(frameNumber % FFPROBE.currentFPS))); 	
					    			
					    				mediaOfflineFrame.append(tcOfflineFrame + System.lineSeparator());
		    	 					}
		    	 					
		    	 					mseValue = mse;
		    	 				}
		    	 				else if (mse > mseSensibility)
		    	 				{
		    	 					offline = false;
		    	 					mseValue = 0f;
		    	 				}
			    	 		}
	    				
	    					line = reader.readLine();
	    				}
	    				reader.close();
	    			} catch (IOException e) {}
	    			
	    			stats_file.delete();
	    	 	}
	     }
	}
	
	public static int CalculTemps(String temps) {
		
		String[] time = temps.split(":");

		int heures =Integer.parseInt(time[0]);
		int minutes =Integer.parseInt(time[1]);
		int secondes =Integer.parseInt(time[2]);
		int images =Integer.parseInt(time[3]);
		images = (images / 40);
		
		int totalSecondes = (heures * 3600) + (minutes * 60) +  secondes;  
				
		return totalSecondes;
		
	}

}
