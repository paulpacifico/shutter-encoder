/*******************************************************************************************
* Copyright (C) 2022 PACIFICO PAUL
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
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

import application.Console;
import application.Functions;
import application.RecordInputDevice;
import application.RenderQueue;
import application.SceneDetection;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import functions.VideoEncoders;
import settings.BitratesAdjustement;
import settings.Colorimetry;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class FFMPEG extends Shutter {
	
public static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static BufferedWriter writer;
public static Thread runProcess = new Thread();
public static Process process;
public static String analyseLufs;
public static Float mseSensibility = 800f;
public static float newVolume;
public static StringBuilder shortTermValues;
public static StringBuilder blackFrame;
public static StringBuilder mediaOfflineFrame;
private static boolean firstInput = true;
public static int firstScreenIndex = -1;
public static StringBuilder videoDevices;
public static StringBuilder audioDevices;

public static int differenceMax;

//Moyenne de fps		
private static int frame0 = 0;
private static long time = 0;
public static long elapsedTime = 0;
public static int previousElapsedTime = 0;
private static int fps = 0;

private static StringBuilder getAll;

	public static void run(String cmd) {
			
		time = 0;
		fps = 0;

		elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
		error = false;	
		firstInput = true;
	    Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " -threads " + Settings.txtThreads.getText() + cmd + System.lineSeparator() + System.lineSeparator());
	    
	    getAll = new StringBuilder();

		if (saveCode)
		{
			if (cmd.contains("-pass 2") == false)
					saveToXML(cmd);
		}
		else if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled() && cmd.contains("image2pipe") == false && cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false)
		{
			//On récupère le nom précédent
			if (lblCurrentEncoding.getText().equals(Shutter.language.getProperty("lblEncodageEnCours")))
			{
				lblCurrentEncoding.setText(RenderQueue.tableRow.getValueAt(RenderQueue.tableRow.getRowCount() - 1, 0).toString());
			}
			
			if (caseChangeFolder1.isSelected() == false)
			{
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
			}
				
			if (caseChangeFolder3.isSelected() && caseChangeFolder2.isSelected())
			{
				RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText() + " | " + lblDestination2.getText() + " | " + lblDestination3.getText()});
			}
			else if (caseChangeFolder2.isSelected())
			{
				RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText() + " | " + lblDestination2.getText()});
			}
			else
				RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "ffmpeg" + checkList(cmd), lblDestination1.getText()});
	        
			lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
		}
		else
		{
			isRunning = true;
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && cmd.contains("image2pipe") == false && cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false)
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
							if ((cmd.contains("pipe:play") || caseStream.isSelected()) && cmd.contains("image2pipe") == false)
							{								
								PathToFFMPEG = "Library\\ffmpeg.exe";

								String aspect ="";
								if (caseForcerDAR.isSelected())
									aspect = ",setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
							
								pipe =  " | " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -loglevel quiet -x 320 -y 180 -alwaysontop -autoexit -an -vf setpts=FRAME_RATE" + aspect + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + Shutter.language.getProperty("viewEncoding") + '"';										
								process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG) + pipe});
							}						
							else if (cmd.contains("pipe:stab") || cmd.contains("image2pipe") || cmd.contains("60000/1001") || cmd.contains("30000/1001") || cmd.contains("24000/1001")
									|| comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG") ||
									(caseForcerDAR.isSelected() && grpAdvanced.isVisible()
									|| VideoPlayer.caseAddTimecode.isSelected()
									|| VideoPlayer.caseEnableColorimetry.isSelected()
									|| caseLUTs.isSelected() && grpColorimetry.isVisible()
									|| caseColormatrix.isSelected() && comboInColormatrix.getSelectedItem().toString().equals("HDR") && grpColorimetry.isVisible()
									|| VideoPlayer.caseDeflicker.isSelected()) && caseDisplay.isSelected() == false)
							{
								PathToFFMPEG = "Library\\ffmpeg.exe";
								process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG)});
							}
							else //Permet de mettre en pause FFMPEG
							{		
								processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));
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
								String aspect ="";
								if (caseForcerDAR.isSelected())
									aspect = ",setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
								
								pipe =  " | " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -loglevel quiet -x 320 -y 180 -alwaysontop -autoexit -an -vf setpts=FRAME_RATE" + aspect + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + Shutter.language.getProperty("viewEncoding") + '"';
							}
							
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG) + pipe);									
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
									|| line.contains("Unknown encoder")
									|| line.contains("Could not set video options")
									|| line.contains("Input/output error")
									|| line.contains("Operation not permitted"))
							{
								error = true;
							} 
																	
							if (cancelled == false)
							{
								if (cmd.contains("-pass 2"))	
									setProgress(line,true);
								else
									setProgress(line,false);	
							}
							else
								break;
																			
						}//While							
						process.waitFor();	
						
						if (cancelled == false)
							postAnalyse();						
					   					     																		
						} catch (IOException io) {//Bug Linux							
						} catch (InterruptedException e) {
							error = true;			
						} finally {
							isRunning = false;
							caseRunInBackground.setEnabled(false);	
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
						if (fileList.getSelectedIndices().length == 0)
							file = new File(liste.firstElement());
						else							
							file = new File(fileList.getSelectedValue());							
						
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToFFMPEG = "Library\\ffmpeg.exe";
							processFFMPEG = new ProcessBuilder("cmd.exe" , "/c",  PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + file.getName() + '"');
						}
						else
						{
							PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
							PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + file.getName() + '"');	
						}		
										
						Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToFFMPEG + " -threads " + Settings.txtThreads.getText() + " " + cmd + " " + PathToFFMPEG.replace("ffmpeg", "ffplay") + " -i " + '"' + "pipe:play" +  '"' + " -window_title " + '"' + file.getName() + '"'
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
									|| line.contains("Unknown encoder")
									|| line.contains("Could not set video options")
									|| line.contains("Input/output error")
									|| line.contains("Operation not permitted"))
							{
								error = true;
								//break;
							} 								
								 
							 if (line.contains("frame"))
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
							
																			
						}//While					
						process.waitFor();															
					   					     																		
						} catch (IOException io) {//Bug Linux							
						} catch (InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;
						}
					
				}				
			});		
			runProcess.start();	
	}
	
	public static void toSDL(boolean isVideoPlayer) {
		
		if (fileList.getSelectedIndices().length > 1 && isVideoPlayer == false)
		{
			String input = "";
			String filter = "";
			String hstack = "";
			int n = fileList.getSelectedIndices().length;
			int i = 0;
			for (String video : fileList.getSelectedValuesList()) {
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
			
			//File
			File inputFile = null;
			
			if (isVideoPlayer)
			{
				inputFile = new File(VideoPlayer.videoPath);
				InputAndOutput.getInputAndOutput();
			}
			else if (inputDeviceIsRunning == false) //Already analyzed
			{
				inputFile = new File(fileList.getSelectedValue());
				FFPROBE.Data(fileList.getSelectedValue());					
			}
				
			
			do {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {}
			} while (FFPROBE.isRunning);

			String channels = "";
			String videoOutput = "";
			String audioOutput = "";
			
			if (FFPROBE.audioOnly) 
			{
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
			} 
			else
			{
				if (FFPROBE.channels > 1)
				{					
					if (inputDeviceIsRunning)
					{
						channels += "[0:a]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						channels += "[2:a]showvolume=f=0.001:b=4:w=1080:h=12[a2];";
						audioOutput += "[a0]";
						audioOutput += "[a2]";
						
						audioOutput += "vstack=3[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else
					{		
						int i = 0;
						for (i = 0; i < FFPROBE.channels; i++)
						{
							channels += "[0:a:" + i + "]showvolume=f=0.001:b=4:w=1080:h=12[a" + i + "];";
							audioOutput += "[a" + i + "]";
						}
						
						audioOutput += "vstack=" + (i + 1) + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
				} else if (FFPROBE.channels == 1) 
				{
					if (inputDeviceIsRunning && RecordInputDevice.audioDeviceIndex > 0 && overlayDeviceIsRunning && RecordInputDevice.overlayAudioDeviceIndex > 0)
					{
						channels = "[2:a]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else if (inputDeviceIsRunning && overlayDeviceIsRunning && RecordInputDevice.overlayAudioDeviceIndex > 0)
					{
						channels = "[1:a]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else
					{
						channels = "[0:a:0]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
				}

				// On ajoute la vidéo
				videoOutput = "[0:v]scale=1080:-1[v]" + ";" + channels + "[v]";
				
				if (FFPROBE.channels == 0 || liste.getElementAt(0).equals("Capture.input.device")) {
					videoOutput = "scale=1080:-1" + '"';
					audioOutput = "";
				}

			}
			
			if (inputDeviceIsRunning && overlayDeviceIsRunning)
			{	     
				if (RecordInputDevice.audioDeviceIndex > 0)
				{
					videoOutput = "[2:v]scale=iw*" + ((float)  Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[1:v][scaledwatermark]overlay=" + VideoPlayer.textWatermarkPosX.getText() + ":" + VideoPlayer.textWatermarkPosY.getText() + ",scale=1080:-1[v]";			
				}
				else
				{
					videoOutput = "[1:v]scale=iw*" + ((float)  Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + VideoPlayer.textWatermarkPosX.getText() + ":" + VideoPlayer.textWatermarkPosY.getText() + ",scale=1080:-1[v]";	
				}
					
				if (audioOutput != "")
					videoOutput += ";" + channels + "[v]";
				else
					videoOutput += '"';
			}
			
			String extension = "";			
			String output = "";
			
			if (inputDeviceIsRunning == false)
			{
				extension = inputFile.toString().substring(inputFile.toString().lastIndexOf("."));
				output = inputFile.getParent();
			} 
			
			//Concat mode
			String concat = "";
			if (VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
			{
				concat = FunctionUtils.setConcat(inputFile, output);			
				inputFile = new File(output.replace("\\", "/") + "/" + inputFile.getName().replace(extension, ".txt"));
			}
			
			String cmd = " -filter_complex " + '"' + videoOutput + audioOutput	+ " -c:v rawvideo -map a? -f nut pipe:play |";

			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			if (isVideoPlayer)
			{
				FFMPEG.toFFPLAY(InputAndOutput.inPoint + concat + " -i " + '"' + inputFile + '"' + InputAndOutput.outPoint + cmd);
			}
			else if (inputDeviceIsRunning)
			{
				if (liste.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 || System.getProperty("os.name").contains("Mac") && liste.getElementAt(0).equals("Capture.input.device") && RecordInputDevice.audioDeviceIndex > 0)
					cmd = cmd.replace("0:v", "1:v");	
				
				if (overlayDeviceIsRunning && audioOutput == "")
					cmd = cmd.replace("-map a?", "-map " + '"' + "[v]" + '"');
					
				if (overlayDeviceIsRunning)
					FFMPEG.toFFPLAY(RecordInputDevice.setInputDevices() + " " + RecordInputDevice.setOverlayDevice() + cmd);
				else
					FFMPEG.toFFPLAY(RecordInputDevice.setInputDevices() + cmd);
			} 
			else
				FFMPEG.toFFPLAY(" -i " + '"' + inputFile + '"' + cmd);					
			
			if (FFMPEG.isRunning)
			{
				do {
					if (FFMPEG.error)
					{
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
			
			//Mode concat
			if (VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
			{		
				File listeBAB = new File(output.replace("\\", "/") + "/" + inputFile.getName().replace(extension, ".txt"));			
				listeBAB.delete();
			}

			if (FFMPEG.isRunning)
				FFMPEG.process.destroy();

			enableAll();
			progressBar1.setValue(0);
		}
	}

	public static void previewEncoding() {
		
		switch (comboFonctions.getSelectedItem().toString()) 
		{
		case "DNxHD":
		case "DNxHR":
		case "Apple ProRes":
		case "GoPro CineForm":
		case "Uncompressed":
		case "XDCAM HD422":				
		case "AVC-Intra 100":
		case "XAVC":
		case "HAP":
		case "FFV1":
		case "H.264":
		case "H.265":
		case "VP8":
		case "VP9":
		case "AV1":
		case "WMV":
		case "MPEG-1":
		case "MPEG-2":
		case "OGV":
		case "MJPEG":
		case "Xvid":
			VideoEncoders.main(false);
			break;
			
		default:
			
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
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					 while (XPDF.isRunning);
				}
				else if (isRaw)
				{
					 EXIFTOOL.run(file);	
					 do
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					 while (EXIFTOOL.isRunning);
				}
				else
				{
					if (Utils.inputDeviceIsRunning == false)
						FFPROBE.Data(file.toString());	
					
					do
						try {
							Thread.sleep(10);
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
				if (VideoPlayer.caseEnableCrop.isSelected())
					filter = " -vf " + croppingValues;
				
				if (comboResolution.getSelectedItem().toString().contains(":"))
				{					
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
					
				}
				else
				{
					String i[] = FFPROBE.imageResolution.split("x");
		        	String o[] = FFPROBE.imageResolution.split("x");
					
					if (comboResolution.getSelectedItem().toString().contains("%"))
					{
						double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
												
						o[0] = String.valueOf((int) (Integer.parseInt(i[0]) * value));
						o[1] = String.valueOf((int) (Integer.parseInt(i[1]) * value));
					}
					else					
						o = comboResolution.getSelectedItem().toString().split("x");	
		
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

				//EXR gamma
				String EXRGamma = Colorimetry.setInputCodec(extension);
				
				String compression = "";
				int fileSize = 0;
				if (comboFonctions.getSelectedItem().toString().equals("JPEG") && extension.toLowerCase().equals(".pdf") == false && isRaw == false)
				{
					int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));						
					compression = " -q:v " + q;
													    		
					File fileOut = new File(dirTemp + "fileSize.jpg");
					if (fileOut.exists()) fileOut.delete();
					
					//InOut
					InputAndOutput.getInputAndOutput();	
					
					FFMPEG.run(InputAndOutput.inPoint + EXRGamma + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + filter + compression + " -vframes 1 " + '"' + fileOut + '"');							
					
					do
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {}
					while(FFMPEG.runProcess.isAlive());
					
					enableAll();
					
					if (fileOut.exists())
					{
						fileSize = (int) (float) fileOut.length() / 1024;						
						fileOut.delete();
					}					
							
					if (filter != "")
						filter += ",drawtext=fontfile=" + pathToFont + ":text='" + fileSize + "Ko" + "':" + '"' + "x=(w-tw)*0.95:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
					else
						filter = " -vf drawtext=fontfile=" + pathToFont + ":text='" + fileSize + "Ko" + "':" + '"' + "x=(w-tw)*0.95:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
				}							
								
				//FFPLAY
				if (extension.toLowerCase().equals(".pdf"))
				{
					XPDF.toFFPLAY(filter);
				}
				else if (isRaw)
				{
					DCRAW.toFFPLAY(filter);
				}
				else if (comboFonctions.getSelectedItem().toString().equals("JPEG"))
				{
					String cmd = filter + " -an -c:v mjpeg" + compression + " -vframes 1 -f nut pipe:play |";
					FFMPEG.toFFPLAY(InputAndOutput.inPoint + EXRGamma + " -i " + '"' + file + '"' + InputAndOutput.outPoint + cmd);
				}
				else
					FFPLAY.run(EXRGamma + " -i " + '"' + file + '"' + filter);
			}
			
			break;
		}			
							
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {	
				try {
					do {								
						Thread.sleep(10);								
					} while (FFMPEG.isRunning == false);	
					
					enableAll();
					lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
					lblCurrentEncoding.setText(language.getProperty("lblEncodageEnCours"));
					
					do {								
						Thread.sleep(10);								
					} while (FFMPEG.isRunning);	
				} catch (InterruptedException e1) {}	
				
				if (case2pass.isSelected())
				{						
					File fichier = new File(liste.getElementAt(0));
					File folder = new File(fichier.getParent());
					String ext = fichier.toString().substring(fichier.toString().lastIndexOf("."));
					
					if (caseChangeFolder1.isSelected())
						folder = new File(lblDestination1.getText());					

					FunctionUtils.listFilesForFolder(fichier.getName().replace(ext, ""), folder);
				}
			}
		});
		thread.start();	
		
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
									|| line.contains("Unknown encoder")
									|| line.contains("Could not set video options")
									|| line.contains("Input/output error")
									|| line.contains("Operation not permitted"))
							{
								error = true;
								//break;
							} 
																			
						}//While			
					}
					
					process.waitFor();					
				   					     																		
					} catch (IOException io) {//Bug Linux							
					} catch (InterruptedException e) {
						error = true;
					}
				
			}				
		});		
		runProcess.start();
	}

	public static void devices(final String cmd) {
		
		error = false;		
		isRunning = true;
		
	    Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + cmd + System.lineSeparator() + System.lineSeparator());
			
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
					
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		

					boolean isVideoDevices = false;
					boolean isAudioDevices = false;
					if (cmd.contains("openal") == false) //IMPORTANT
					{
						videoDevices = new StringBuilder();
						videoDevices.append(language.getProperty("noVideo"));
					}
					
					audioDevices = new StringBuilder();
					audioDevices.append(language.getProperty("noAudio"));
					
					while ((line = input.readLine()) != null) {						
						
						Console.consoleFFMPEG.append(line + System.lineSeparator());		
											
						//Get devices Mac
						if (cmd.contains("avfoundation") && line.contains("]")) 
						{	
							if (isAudioDevices)
							{								
								String s[] = line.split("\\]");
								
								byte[] bytes = s[2].substring(1, s[2].length()).getBytes(StandardCharsets.ISO_8859_1);
								String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
								
								audioDevices.append(":" + utf8EncodedString);
							}
							
							if (line.contains("AVFoundation audio devices"))
							{
								isAudioDevices = true;
							}
							
							if (isVideoDevices && line.contains("Capture screen") == false && isAudioDevices == false)
							{						
								String s[] = line.split("\\]");
								
								byte[] bytes = s[2].substring(1, s[2].length()).getBytes(StandardCharsets.ISO_8859_1);
								String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
								
								videoDevices.append(":" + utf8EncodedString);
							}
							
							if (line.contains("AVFoundation video devices"))
								isVideoDevices = true;
						}
						
						/*
						if (cmd.contains("openal") && line.contains("]"))
						{
							if (isAudioDevices)
							{								
								String s[] = line.split("\\]");
								audioDevices.append(":" + s[1].substring(3, s[1].length()));
							}
							
							if (line.contains("List of OpenAL capture devices on this system"))
							{
								isAudioDevices = true;
							}
						}*/
						
						//Get current screen index
						if (cmd.contains("avfoundation") && line.contains("Capture screen") && firstScreenIndex == -1) 
						{
							String s[] = line.split("\\[");
							String s2[] = s[2].split("\\]");
							firstScreenIndex = Integer.parseInt(s2[0]);
						}
						
						//Get devices Windows
						if (cmd.contains("dshow"))
						{							
							if (line.contains("audio") && line.contains("\"") && line.contains("Alternative name") == false)
							{
								String s[] = line.split("\"");
								
								byte[] bytes = s[1].getBytes(StandardCharsets.ISO_8859_1);
								String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
								
								audioDevices.append(":" + utf8EncodedString);
							}
							
							if (line.contains("video") && line.contains("\"") && line.contains("Alternative name") == false && isAudioDevices == false)
							{
								String s[] = line.split("\"");
								
								byte[] bytes = s[1].getBytes(StandardCharsets.ISO_8859_1);
								String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
								
								videoDevices.append(":" + utf8EncodedString);
							}
						}

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
								|| line.contains("Unknown encoder")
								|| line.contains("Could not set video options")
								|| line.contains("Input/output error")
								|| line.contains("Operation not permitted"))
						{
							error = true;
						} 
																		
					}//While			
					
					process.waitFor();					
				   					     																		
					} catch (IOException io) {//Bug Linux							
					} catch (InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
					}
				
			}				
		});		
		runProcess.start();
	}
	
	private static void saveToXML(String cmd) {	  
		
		FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveSettings"), FileDialog.SAVE);
		dialog.setDirectory(Functions.functionsFolder.toString());
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
					firstName.appendChild(document.createTextNode("ffmpeg" + cmd.replace(InputAndOutput.inPoint, "").replace(" -i ", "").replace('"' + entree + '"', "").replace('"' + sortie + '"', "").replace(" -y ","").replace(" -n ", "")));
					settings.appendChild(firstName);

					// point d'entrée
					Element lastname = document.createElement("pointIn");
					lastname.appendChild(document.createTextNode(InputAndOutput.inPoint));
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
	
	public static boolean isReadable(File file) {
		
		try {	
			
			String PathToFFMPEG;
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
												
				processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -" + '"');
				process = processFFMPEG.start();
			}
			else
			{
				PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
				PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";
	
				
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -");							
				process = processFFMPEG.start();
			}		
			
			
			Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -" + System.lineSeparator() + System.lineSeparator());
			
			String line;
	
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
								
			while ((line = input.readLine()) != null) {						
				
				Console.consoleFFMPEG.append(line + System.lineSeparator() );		
				
				//Erreurs
				if (line.contains("No such file or directory")
					|| line.contains("Invalid data found")
					|| line.contains("moov atom not found")
					|| line.contains("Operation not permitted")
					|| line.contains("File ended prematurely")
					|| line.contains("Warning MVs not available")
					|| line.contains("broken or empty index")
					|| line.contains("corrupt decoded frame")
					|| line.contains("invalid new backstep")
					|| line.contains("Packet corrupt")
					|| line.contains("ac-tex damaged")
					|| line.contains("Error"))
				{
					return false;
				} 
																		
			}			
	   					     																		
		} catch (IOException io) {//Bug Linux							
		} catch (Exception e) {
			return false;
		}
		
		return true;
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

	private static void setProgress(String line, final boolean pass2) {				
									
		if (line.contains("Input #1"))
			firstInput = false;
				
		//Calcul de la durée
	    if (line.contains("Duration") && line.contains("Duration: N/A") == false && line.contains("<Duration>") == false && firstInput)
		{	    	
			String str = line.substring(line.indexOf(":") + 2);
			String[] split = str.split(",");	 
	
			String ffmpegTime = split[0].replace(".", ":");	  
					
			if (caseEnableSequence.isSelected())
			{
				dureeTotale = (int) (liste.getSize() / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) );
			}
			else if (caseInAndOut.isSelected() && VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
			{
				dureeTotale = VideoPlayer.durationH * 3600 + VideoPlayer.durationM * 60 + VideoPlayer.durationS;
			}
			else
				dureeTotale = (getTimeToSeconds(ffmpegTime));
			
			if (caseConform.isSelected())
			{
				float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));	
				if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))
				{
					dureeTotale = (int) (dureeTotale * (FFPROBE.currentFPS / newFPS ));	
				}
				else if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
				{
					dureeTotale = (int) (dureeTotale * (newFPS / FFPROBE.currentFPS));	
				}
			}
			
			if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform")))
			{
				float newFPS = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".")));		
				dureeTotale = (int) (dureeTotale * (FFPROBE.currentFPS / newFPS));
			}
						
			if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
				dureeTotale = 1;
			
			if ((comboFonctions.getSelectedItem().toString().equals("H.264")
					|| comboFonctions.getSelectedItem().toString().equals("H.265")
					|| comboFonctions.getSelectedItem().toString().equals("WMV")
					|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
					|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
					|| comboFonctions.getSelectedItem().toString().equals("WebM")
					|| comboFonctions.getSelectedItem().toString().equals("AV1")
					|| comboFonctions.getSelectedItem().toString().equals("OGV")
					|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
					|| comboFonctions.getSelectedItem().toString().equals("Xvid")
					|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
				dureeTotale = (dureeTotale * 2);
				
			if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert")) ==  false)			
				progressBar1.setMaximum(dureeTotale);	
		}	    	    
	    
	  //Progression
	  if (line.contains("time=") && lblCurrentEncoding.getText().equals(language.getProperty("lblEncodageEnCours")) == false 
								 && lblCurrentEncoding.getText().equals(language.getProperty("processCancelled")) == false
								 && lblCurrentEncoding.getText().equals(language.getProperty("processEnded")) == false)
	  {		  
		  	//Il arrive que FFmpeg puisse encoder le fichier alors qu'il a detecté une erreur auparavant, dans ce cas on le laisse continuer donc : error = false;
		  	error = false;
		  	
	  		String str = line.substring(line.indexOf(":") - 2);
    		String[] split = str.split("b");	 
    	    
    		String ffmpegTime = split[0].replace(".", ":").replace(" ", "");	    	

    		if (progressBar1.getString().equals("NaN") || inputDeviceIsRunning)
    			progressBar1.setStringPainted(false);
    		else
    			progressBar1.setStringPainted(true);
    		    		
			if (pass2)
			{
				progressBar1.setValue((dureeTotale / 2) + getTimeToSeconds(ffmpegTime));
			}
			else
				progressBar1.setValue(getTimeToSeconds(ffmpegTime));
			
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
						|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
						|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
						|| comboFonctions.getSelectedItem().toString().equals("WebM")
						|| comboFonctions.getSelectedItem().toString().equals("AV1")
						|| comboFonctions.getSelectedItem().toString().equals("OGV")
						|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
						|| comboFonctions.getSelectedItem().toString().equals("Xvid")
					 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
					 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
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
							|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
							|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
							|| comboFonctions.getSelectedItem().toString().equals("WebM")
							|| comboFonctions.getSelectedItem().toString().equals("AV1")
						 	|| comboFonctions.getSelectedItem().toString().equals("OGV")
							|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
							|| comboFonctions.getSelectedItem().toString().equals("Xvid")
						 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
						 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
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
	  
	  //Détection de coupe
	  if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) && line.contains("pts"))
	  {
		  NumberFormat formatter = new DecimalFormat("00");
		  String rawline[] = line.split(":");
		  String fullTime[] = rawline[3].split(" ");
		  int rawTime = (int) (Float.valueOf(fullTime[0]) * 1000);	 
		  long rawFrames = (long) Math.round(rawTime / (1000 / FFPROBE.currentFPS));		  
		  
          String h = formatter.format(Math.round(rawFrames / Math.round(FFPROBE.currentFPS)) / 3600);
          String m = formatter.format(Math.round((rawFrames / Math.round(FFPROBE.currentFPS)) / 60) % 60);
          String s = formatter.format(Math.round((rawFrames / Math.round(FFPROBE.currentFPS)) % 60));          
          String f = formatter.format(rawFrames % Math.round(FFPROBE.currentFPS));
          
          File imageName = new File(SceneDetection.outputFolder + "/" + SceneDetection.tableRow.getRowCount() + ".png");
                    
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
                              
               if (lblCurrentEncoding.getText().contains(Shutter.language.getProperty("analyzing")))
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
	    	 		if (blackLine.contains("blackdetect") && blackLine.contains("black_start:0") == false)
	    	 		{
	    	 			String blackdetect = blackLine.substring(blackLine.indexOf("black_start"));
	    	 			String d[] = blackdetect.split(":");
	    	 						    	 				
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
	
	public static int getTimeToSeconds(String time) {
		
		String[] t = time.split(":");

		int heures =Integer.parseInt(t[0]);
		int minutes =Integer.parseInt(t[1]);
		int secondes =Integer.parseInt(t[2]);
		int images =Integer.parseInt(t[3]);
		images = (images / 40);
		
		int totalSecondes = (heures * 3600) + (minutes * 60) +  secondes;  
				
		return totalSecondes;
		
	}

}