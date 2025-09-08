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

package library;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
import settings.BitratesAdjustement;
import settings.Colorimetry;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class FFMPEG extends Shutter {
	
public static String PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
public static int fileLength = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static BufferedWriter writer;
public static Thread runProcess = new Thread();
private static Thread displayThread;
public static Process process;
private static Process processAudio;
public static Process waveformProcess;
public static BufferedWriter waveformWriter;
private static InputStream audio = null;	
private static AudioInputStream audioInputStream = null;
private static SourceDataLine line = null;
private static boolean showInputDeviceFrame = false;
private static float directDisplayInputRatio = 1.777777f;
private static Image frameVideo;
public static String analyseLufs;
public static Float mseSensibility = 800f;
public static float newVolume;
public static StringBuilder shortTermValues;
public static StringBuilder blackFrame;
public static StringBuilder mediaOfflineFrame;
public static String VMAFScore;
public static String cropdetect;
private static boolean firstInput = true;
public static int firstScreenIndex = -1;
public static StringBuilder videoDevices;
public static StringBuilder audioDevices;
public static StringBuilder hwaccels = new StringBuilder();
public static boolean isGPUCompatible = false;
public static int GPUCount = 0;
public static boolean hasNvidiaGPU = false;
public static boolean hasAMDGPU = false;
public static boolean hasIntelGPU = false;
public static boolean cudaAvailable = false;
public static boolean amfAvailable = false;
public static boolean qsvAvailable = false;
public static boolean videotoolboxAvailable = false;
public static boolean vulkanAvailable = false;
public static int differenceMax;

//Moyenne de fps		
private static int frame0 = 0;
private static long time = 0;
public static long elapsedTime = 0;
public static int previousElapsedTime = 0;
private static int fps = 0;

private static StringBuilder getAll;
public static StringBuilder errorLog = new StringBuilder();

	public static void getFFmpegPath() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{							
			PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
			PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
		}	
		else
		{
			PathToFFMPEG = PathToFFMPEG.substring(0,PathToFFMPEG.length()-1);
			PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
		}
		
		if (Settings.btnCustomFFmpegPath.isSelected() && Settings.txtCustomFFmpegPath.getText().equals("") == false)
		{
			PathToFFMPEG = Settings.txtCustomFFmpegPath.getText();
		}
	}

	public static void run(String cmd) {
			
		time = 0;
		fps = 0;

		elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
		error = false;	
		firstInput = true;
		
		Console.consoleFFMPEG.append(System.lineSeparator());
	    Console.consoleFFMPEG.append(Shutter.language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + cmd);
	    
	    getAll = new StringBuilder();

		if (saveCode)
		{
			if (cmd.contains("-pass 2") == false)
				saveToXML(cmd);
		}
		else if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled() && cmd.contains("-f rawvideo") == false
		&& cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false && cmd.contains("preview.png") == false && cmd.contains("ebur128=peak=true") == false)
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
	        
			if (caseDisplay.isSelected())
			{
				RenderQueue.caseRunParallel.setSelected(false);
				RenderQueue.caseRunParallel.setEnabled(false);
		        RenderQueue.parallelValue.setEnabled(false);
			}
			
			RenderQueue.frame.toFront();
			
			lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
			
			Console.consoleFFMPEG.append(System.lineSeparator());
		}
		else
		{
			isRunning = true;
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && cmd.contains("-f rawvideo") == false && cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false  && cmd.contains("preview.png") == false && screenshotIsRunning == false)
				disableAll();
			
			runProcess = new Thread(new Runnable()  {
				
				@SuppressWarnings("resource")
				@Override
				public void run() {
					
					try {
						
						ProcessBuilder processFFMPEG;
						
						if (System.getProperty("os.name").contains("Windows"))
						{														
							if (cmd.contains("-f rawvideo") || cmd.contains("pipe:1") || cmd.contains("vidstabdetect") || cmd.contains("60000/1001") || cmd.contains("30000/1001") || cmd.contains("24000/1001")
							|| caseEnableColorimetry.isSelected() && Colorimetry.setEQ(true) != ""
							|| caseLUTs.isSelected() && grpColorimetry.isVisible()
							|| caseForcerDAR.isSelected()
							|| caseColormatrix.isSelected() && comboInColormatrix.getSelectedItem().toString().equals("HDR") && grpColorimetry.isVisible())
							{
								String pipe = "";								
								if (cmd.contains("pipe:1"))
								{
									pipe =  " | " + '"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i pipe:0 -an -c:v bmp -pix_fmt rgb24 -f image2pipe -";
								}
								
								PathToFFMPEG = "Library\\ffmpeg.exe";
								process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG) + pipe});
								
								//Back to default
								if (Settings.btnCustomFFmpegPath.isSelected() && Settings.txtCustomFFmpegPath.getText().equals("") == false)
								{
									PathToFFMPEG = Settings.txtCustomFFmpegPath.getText();
								}
								else
								{
									PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
									PathToFFMPEG = PathToFFMPEG.substring(1,PathToFFMPEG.length()-1);
									PathToFFMPEG = PathToFFMPEG.substring(0,(int) (PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
								}
							}
							else //Allow to suspend FFmpeg process
							{
								processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", '"' + PathToFFMPEG + '"'));								
								process = processFFMPEG.start();	
							}					
						}
						else
						{							
							String pipe = "";								
							if (cmd.contains("pipe:1"))
							{
								pipe =  " | " + PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i pipe:0 -an -c:v bmp -pix_fmt rgb24 -f image2pipe -";
							}
							
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG) + pipe);							
							process = processFFMPEG.start();
						}	

						//IMPORTANT
						if (cmd.contains("cropdetect") == false
						&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction"))|| Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")))
						{
							VideoPlayer.resizeAll();
						}
							
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
						
						InputStream video = process.getInputStream();				
						BufferedInputStream videoInputStream = new BufferedInputStream(video);	
						
						//Allows to write into the stream
						OutputStream stdin = process.getOutputStream();
				        writer = new BufferedWriter(new OutputStreamWriter(stdin));				        
		        
				        if (cmd.contains("pipe:1"))
						{				  				        	
				        	VideoPlayer.playerStop();
					     
				        	Thread playerThread = new Thread(new Runnable() {
	
								@Override
								public void run() {

						            try {
						            	
										do {
											
											if (btnStart.getText().equals(language.getProperty("btnPauseFunction"))
											|| btnStart.getText().equals(language.getProperty("btnStopRecording"))
											|| cancelled) //Empty the buffer
											{	
												VideoPlayer.frameVideo = ImageIO.read(videoInputStream);	
												VideoPlayer.player.repaint();
											}
											
										} while (VideoPlayer.frameVideo != null);
										
									} catch (Exception e) {}
								}
					    		
					    	});
					        playerThread.start();
						}

				        Console.consoleFFMPEG.append(System.lineSeparator());

						while ((line = input.readLine()) != null)
						{			
							getAll.append(line);
							getAll.append(System.lineSeparator());							
							
							Console.consoleFFMPEG.append(line + System.lineSeparator());		
							
							//Errors
							checkForErrors(line);	
																								
							if (cancelled == false)
							{																														
								if (RenderQueue.frame != null && RenderQueue.frame.isVisible() && RenderQueue.caseRunParallel.isSelected())
								{													
									if (line.contains("All streams finished"))
									{
										RenderQueue.filesCompleted++;	
									}
								}
								else
								{
									if (cmd.contains("-pass 2"))	
										setProgress(line, true, cmd);
									else
										setProgress(line, false, cmd);	
								}
							}																		
						}					
						process.waitFor();
											
						if (cancelled == false)
						{							
							postAnalyse();	
						}
					   					     																		
					} catch (IOException io) {//Bug Linux							
					} catch (InterruptedException e) {
						error = true;			
					} finally {
						isRunning = false;
						caseRunInBackground.setEnabled(false);	
					}
					
				}				
			});		
			runProcess.start();
		}
			
	}
			
	public static void runSilently(String cmd) {
		
		error = false;
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {
					
					ProcessBuilder processFFMPEG;
					
					if (System.getProperty("os.name").contains("Windows"))
					{														
						processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd);								
						process = processFFMPEG.start();					
					}
					else
					{													
						processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd);							
						process = processFFMPEG.start();
					}	
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					
					while ((line = input.readLine()) != null)
					{			
						checkForErrors(line);
					}					
					process.waitFor();
									   					     																		
				} catch (IOException io) {//Bug Linux							
				} catch (Exception e) {
					error = true;	
				}
			}
		});		
		runProcess.start();
	}
	
	public static void checkForErrors(String line) {
		
		if (line.contains("No such file or directory")
		|| line.contains("Invalid data found when processing input") && line.contains("unable to decode APP fields") == false //attached picture of an audio file
		|| line.contains("No space left")
		|| line.contains("does not contain any stream")
		|| line.contains("Invalid argument")
		|| line.contains("Error opening filters!")
		|| line.contains("Error reinitializing filters!")
		|| line.contains("Error while opening encoder")
		|| line.contains("unexpected EOF")
		|| line.contains("Decoder (codec none) not found")
		|| line.contains("hwaccel initialisation returned error")
		|| line.contains("Device setup failed for decoder")
		|| line.contains("No device available for decoder")
		|| line.contains("Error while decoding stream")
		|| line.contains("Current pixel format is unsupported")
		|| line.contains("Unknown encoder")
		|| line.contains("Could not set video options")
		|| line.contains("Could not find tag for codec")
		|| line.contains("Input/output error")
		|| line.contains("Operation not permitted")
		|| line.contains("Permission denied")
		|| line.contains("width not divisible by 2")
		|| line.contains("integer multiple of the specified")
		|| line.contains("is not multiple of 4")
		|| line.contains("cannot be smaller than input dimensions"))
		{					
			if (line.contains("error code") == false && line.contains("return code") == false)
			{
				errorLog.append(line + System.lineSeparator());
			}
			
			error = true;
		} 				
	}
	
 	private static String checkList(String cmd) {

		if (cmd.contains("pass 2"))
		{
			return RenderQueue.tableRow.getValueAt(RenderQueue.tableRow.getRowCount() - 1, 1).toString().replace("ffmpeg", "").replace("pass 1", "pass 2");
		}
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

	public static void toSDL(boolean isVideoPlayer) {
		
		if (fileList.getSelectedIndices().length > 1 && isVideoPlayer == false)
		{
			String input = "";
			String filter = "";
			String hstack = "";
			int n = fileList.getSelectedIndices().length;
			int i = 0;
			for (String video : fileList.getSelectedValuesList()) {
				input += " -v quiet -i " + '"' + video + '"';
				filter += "[" + i + ":v]scale=iw/" + n + ":ih/2[v" + i + "];";
				i++;
			}

			for (int v = 0; v < i; v++) {
				hstack += "[v" + v + "]";
			}

			hstack += "hstack=" + n + "[out]";

			FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + input + " -filter_complex " + '"' + filter + hstack + '"' + " -c:v rawvideo -map " + '"' + "[out]" + '"' + " -an -f nut pipe:1");
		}
		else
		{
						
			//File
			File inputFile = null;
			
			if (isVideoPlayer)
			{
				inputFile = new File(VideoPlayer.videoPath);
				InputAndOutput.getInputAndOutput(true);
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
						channels += "[0:a]showvolume=f=0.001:b=4:w=1000:h=12[a0];";
						channels += "[2:a]showvolume=f=0.001:b=4:w=1000:h=12[a2];";
						audioOutput += "[a0]";
						audioOutput += "[a2]";
						
						audioOutput += "vstack=3[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else
					{		
						int i = 0;
						for (i = 0; i < FFPROBE.channels; i++)
						{
							channels += "[0:a:" + i + "]showvolume=f=0.001:b=4:w=1000:h=12[a" + i + "];";
							audioOutput += "[a" + i + "]";
						}
						
						audioOutput += "vstack=" + (i + 1) + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
				} 
				else if (FFPROBE.channels == 1) 
				{
					if (inputDeviceIsRunning && RecordInputDevice.audioDeviceIndex > 0 && overlayDeviceIsRunning && RecordInputDevice.overlayAudioDeviceIndex > 0)
					{
						channels = "[2:a]showvolume=f=0.001:b=4:w=1000:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else if (inputDeviceIsRunning && overlayDeviceIsRunning && RecordInputDevice.overlayAudioDeviceIndex > 0)
					{
						channels = "[1:a]showvolume=f=0.001:b=4:w=1000:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else
					{
						channels = "[0:a:0]showvolume=f=0.001:b=4:w=1000:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
				}

				// On ajoute la vidéo
				videoOutput = "[0:v]scale=1000:-1:sws_flags=fast_bilinear:sws_dither=none[v]" + ";" + channels + "[v]";
				
				if (FFPROBE.channels == 0 || liste.getElementAt(0).equals("Capture.input.device")) {
					videoOutput = "scale=1000:-1:sws_flags=fast_bilinear:sws_dither=none" + '"';
					audioOutput = "";
				}

			}
			
			if (inputDeviceIsRunning && overlayDeviceIsRunning)
			{	     
				if (RecordInputDevice.audioDeviceIndex > 0)
				{
					videoOutput = "[2:v]scale=iw*" + ((float)  Integer.parseInt(Shutter.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(Shutter.textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(Shutter.textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[1:v][scaledwatermark]overlay=" + Shutter.textWatermarkPosX.getText() + ":" + Shutter.textWatermarkPosY.getText() + "[v]";			
				}
				else
				{
					videoOutput = "[1:v]scale=iw*" + ((float)  Integer.parseInt(Shutter.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(Shutter.textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(Shutter.textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + Shutter.textWatermarkPosX.getText() + ":" + Shutter.textWatermarkPosY.getText() + "[v]";	
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
			
			String cmd = " -filter_complex " + '"' + videoOutput + audioOutput	+ " -c:v rawvideo -an -f nut pipe:1";
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			//Loop image					
			String loop = FunctionUtils.setLoop(extension);
			
			if (isVideoPlayer)
			{
				FFMPEG.toFFPLAY(loop + InputAndOutput.inPoint + concat + " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i " + '"' + inputFile + '"' + InputAndOutput.outPoint + cmd);
			}
			else if (inputDeviceIsRunning)
			{
				if (liste.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 || System.getProperty("os.name").contains("Mac") && liste.getElementAt(0).equals("Capture.input.device") && RecordInputDevice.audioDeviceIndex > 0)
					cmd = cmd.replace("0:v", "1:v");	
				
				if (overlayDeviceIsRunning && audioOutput == "")
					cmd = cmd.replace("-an", "-map " + '"' + "[v]" + '"');
					
				if (overlayDeviceIsRunning)
					FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet " + RecordInputDevice.setInputDevices() + " " + RecordInputDevice.setOverlayDevice() + cmd);
				else
					FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet " + RecordInputDevice.setInputDevices() + cmd);
			} 
			else
				FFMPEG.toFFPLAY(loop + " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i " + '"' + inputFile + '"' + cmd);					
						
			progressBar1.setValue(0);
		}
	}
 	
	@SuppressWarnings("resource")
	public static void toFFPLAY(final String cmd) {
		
		error = false;		
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		isRunning = true;
		
		try {
			
			ProcessBuilder processFFMPEG;
			
			//Image sequence
			String fps = " -r " + FFPROBE.currentFPS;
			if (Shutter.caseCreateSequence.isSelected())
			{
				fps = " -r " +  Float.valueOf(Shutter.comboInterpret.getSelectedItem().toString().replace(",", "."));
			}
			else if (inputDeviceIsRunning || RecordInputDevice.frame != null && RecordInputDevice.frame.isVisible())
			{
				fps = "";
			}

			if (System.getProperty("os.name").contains("Windows"))
			{
				//VIDEO STREAM
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c",  '"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd +  " | " + '"' + PathToFFMPEG + '"' + " -v quiet -i pipe:0" + fps + " -c:v bmp -pix_fmt rgb24 -an -f image2pipe -");
				process = pbv.start();
								
				//AUDIO STREAM
				if (FFPROBE.hasAudio)						       
				{						
					File inputFile = new File(fileList.getSelectedValue());
					
					//Concat mode
					String concat = "";
					if (VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
					{					
						String extension = inputFile.toString().substring(inputFile.toString().lastIndexOf("."));
						concat = FunctionUtils.setConcat(inputFile, inputFile.getParent());			
						inputFile = new File(inputFile.getParent().replace("\\", "/") + "/" + inputFile.getName().replace(extension, ".txt"));
					}
					
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + PathToFFMPEG + '"' + concat + " -v quiet "  + InputAndOutput.inPoint + " -i " + '"' + inputFile + '"' + " -vn -c:a pcm_s16le -ar 48k -ac 1 -f wav -");	
					processAudio = pba.start();
				}
			}
			else
			{
				//VIDEO STREAM									
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd + " | " + PathToFFMPEG + " -v quiet -i pipe:0" + fps + " -c:v bmp -pix_fmt rgb24 -an -f image2pipe -");	
				process = processFFMPEG.start();
			
				//AUDIO STREAM
				if (FFPROBE.hasAudio)				       
				{
					File inputFile = new File(fileList.getSelectedValue());
					
					//Concat mode
					String concat = "";
					if (VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
					{					
						String extension = inputFile.toString().substring(inputFile.toString().lastIndexOf("."));
						concat = FunctionUtils.setConcat(inputFile, inputFile.getParent());			
						inputFile = new File(inputFile.getParent().replace("\\", "/") + "/" + inputFile.getName().replace(extension, ".txt"));
					}
					
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + concat + " -v quiet " + InputAndOutput.inPoint + " -i " + '"' + inputFile + '"' + " -vn -c:a pcm_s16le -ar 48k -ac 1 -f wav -");	
					processAudio = pba.start();
				}
			}	
			
			Console.consoleFFPLAY.append(Shutter.language.getProperty("command") + " " + PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd + " | " + PathToFFMPEG + " -v quiet -i pipe:0" + fps + " -c:v bmp -pix_fmt rgb24 -an -f image2pipe -" + System.lineSeparator());
		
			JFrame player = new JFrame();
			player.getContentPane().setBackground(Utils.c42);
			player.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
			player.setBackground(Utils.c42);
			player.getContentPane().setLayout(null);
			
			if (System.getProperty("os.name").contains("Windows"))
				player.setIconImage(frame.getIconImage());
						
			if (RecordInputDevice.frame != null && RecordInputDevice.frame.isVisible())
			{
				RecordInputDevice.frame.setVisible(false);	
				showInputDeviceFrame = true;
				player.setTitle(language.getProperty("preview"));			
			}
			else
			{
				showInputDeviceFrame = false;
				player.setTitle(new File(Shutter.fileList.getSelectedValue()).getName());
			}	
			
			player.addWindowListener(new WindowAdapter() {
	
				@Override
				public void windowClosing(WindowEvent arg0) {
	
					isRunning = false;
					
					process.destroy();
					displayThread.interrupt();

					if (FFPROBE.hasAudio)
					{
						processAudio.destroy();	
					}
					
					if (showInputDeviceFrame)
					{
						RecordInputDevice.frame.setVisible(true);	
					}
				}
				
			});
	
			GraphicsConfiguration config = player.getGraphicsConfiguration();
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
	
			int screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();	
			int screenHeight = allScreens[screenIndex].getDisplayMode().getHeight();	

			@SuppressWarnings("serial")
			JPanel display = new JPanel() {
				
	            @Override
	            protected void paintComponent(Graphics g) {
	            	
	                super.paintComponent(g);
	                
	                Graphics2D g2 = (Graphics2D)g;
	                
	                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	                
	                g2.setColor(Color.BLACK);
	
	                setSize(player.getContentPane().getWidth(), player.getContentPane().getHeight());
	                	                
	                if (frameVideo != null)
	                {
	                	if (player.getHeight() > screenHeight)
		                {
	                		int newWidth = (int) ((float) screenHeight * directDisplayInputRatio);
		                	g2.drawImage(frameVideo, player.getContentPane().getWidth() / 2 - newWidth / 2, 0, newWidth, this.getHeight(), this); 
		                }
		                else
		                	g2.drawImage(frameVideo, player.getContentPane().getWidth() / 2 - this.getWidth() / 2, player.getContentPane().getHeight() / 2 - (int) (this.getWidth() / directDisplayInputRatio) / 2, this.getWidth(), (int) (this.getWidth() / directDisplayInputRatio), this); 
	                }
	                else
	                	g2.fillRect(0, 0, this.getWidth(), this.getHeight()); 
	            }
	        };
	        	        
			display.setLayout(null);
			display.setBackground(Color.BLACK);
			
			player.add(display);
			player.setVisible(true);
			
			// Keyboard shortcuts
			Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
				
				public void eventDispatched(AWTEvent event) {
					
					KeyEvent ke = (KeyEvent) event;
										
					if (ke.getID() == KeyEvent.KEY_PRESSED && ke.getKeyCode() == KeyEvent.VK_ESCAPE) 
					{	
						isRunning = false;
						
						process.destroy();
						displayThread.interrupt();

						if (FFPROBE.hasAudio)
						{
							processAudio.destroy();	
						}
						
						if (showInputDeviceFrame)
						{
							RecordInputDevice.frame.setVisible(true);	
						}
						
						player.dispose();
					}
					
				}
			}, AWTEvent.KEY_EVENT_MASK);
			
			InputStream video = process.getInputStream();				
			BufferedInputStream videoInputStream = new BufferedInputStream(video);
			
			//Allows to write into the stream
			OutputStream stdin = process.getOutputStream();
	        writer = new BufferedWriter(new OutputStreamWriter(stdin));
											
			if (FFPROBE.hasAudio)						       
			{
				audio = processAudio.getInputStream();							
				audioInputStream = AudioSystem.getAudioInputStream(audio);	
			    AudioFormat audioFormat = audioInputStream.getFormat();					    
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		        line = (SourceDataLine) AudioSystem.getLine(info);			        
	            line.open(audioFormat);
	            line.start();	
			}
			
			displayThread = new Thread(new Runnable() {

				@Override
				public void run() {
					        	       										
					try {
						
						byte bytes[] = new byte[(int) Math.ceil(48000*2/FFPROBE.currentFPS)];
			            int bytesRead = 0;
						
				        boolean getRatio = true;
				        
						//Image sequence
						float inputFramerateMS = (float) (1000 / FFPROBE.currentFPS);;
						if (Shutter.caseCreateSequence.isSelected())
						{
							inputFramerateMS = (float) (1000 / (Float.valueOf(Shutter.comboInterpret.getSelectedItem().toString().replace(",", "."))));
						}	
			            
						do {
							
							long startTime = System.nanoTime() + (int) ((float) inputFramerateMS * 1000000);
							
							//Audio volume	
							if (FFPROBE.hasAudio)						       
							{								
								///Read 1 audio frame
								try {
									bytesRead = audioInputStream.read(bytes, 0, bytes.length);
					        		line.write(bytes, 0, bytesRead);
								} catch (Exception e) {}
							}
		
							frameVideo = ImageIO.read(videoInputStream);
			            	display.repaint();
							
							//Getting frame data info once
							if (frameVideo != null && frameVideo.toString().contains("width") && getRatio)
							{
								String info = frameVideo.toString();
															
								String w[] = info.substring(info.indexOf("width")).replace("width ", "").split(" ");
								String h[] = info.substring(info.indexOf("height")).replace("height ", "").split(" ");
															
								directDisplayInputRatio = (float) Integer.parseInt(w[1]) / Integer.parseInt(h[1]);
								
								int borderWidth = player.getWidth() - player.getContentPane().getWidth();
	    						int borderHeight = player.getHeight() - player.getContentPane().getHeight();
								
								player.setSize(1000 + borderWidth, (int) (1000 / directDisplayInputRatio) + borderHeight);					
								player.setLocation(screenWidth / 2 - player.getSize().width / 2, screenHeight / 2 - player.getSize().height / 2);															
								display.setSize(player.getSize());
															
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								
								getRatio = false;
							}						
							
							long delay = startTime - System.nanoTime();
													
			            	if (delay > 0)
			            	{		      
			            		//Because the next loop is very cpu intensive but accurate, this sleep reduce the cpu usage by waiting just less than needed
				            	try {
				            		Thread.sleep((int) (delay / 1500000));
								} catch (InterruptedException e) {}
			            		
				            	delay = startTime - System.nanoTime();
				            	
				            	long time = System.nanoTime();
				            	while (System.nanoTime() - time < delay) {}		
			                }
			            					            	
						} while (process.isAlive());
											
						try {
							video.close();
						} catch (IOException e) {}		
						try {
							videoInputStream.close();
						} catch (IOException e) {}
						
						if (audio != null)	       
						{
							try {
								audio.close();
							} catch (IOException e) {}
							try {
								audioInputStream.close();
							} catch (IOException e) {}
							
							line.close();	
						}
						
						if (player.isVisible())
						{
							player.dispose();	
						}
						
					} catch (Exception e) {
						error = true;
					} finally {
						
						//Mode concat
						if (VideoPlayer.comboMode.getSelectedItem().toString().equals(Shutter.language.getProperty("removeMode")))
						{		
							File inputFile = new File(VideoPlayer.videoPath);
							String extension = inputFile.toString().substring(inputFile.toString().lastIndexOf("."));
							File listeBAB = new File(inputFile.getParent().replace("\\", "/") + "/" + inputFile.getName().replace(extension, ".txt"));			
							listeBAB.delete();
						}		
						
						isRunning = false;
					}
				}
			
	    	});
			displayThread.setPriority(Thread.MAX_PRIORITY);
			displayThread.start();
		   					     																		
		} catch (Exception e) {			
			error = true;
		}								
	}
	
	public static void hwaccel(final String cmd) {
		
		error = false;	
		isRunning = true;

		try {
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd.replace("PathToFFMPEG", PathToFFMPEG));
				process = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd.replace("PathToFFMPEG", PathToFFMPEG));									
				process = processFFMPEG.start();
			}		
			
			String line;

			if (cmd.contains("-hwaccels"))
			{
				InputStreamReader isr = new InputStreamReader(process.getInputStream());
		        BufferedReader br = new BufferedReader(isr);
		        
		        hwaccels.append("auto" + System.lineSeparator());
		        
		        while ((line = br.readLine()) != null) 
		        {				        	
		        	
		        	if (line.contains("Hardware acceleration methods") == false && line.equals("") == false && line != null)
		        	{
		        		hwaccels.append(line + System.lineSeparator());
		        	}
		        }
		        
		        hwaccels.append(language.getProperty("aucun"));				        
		    }
			else
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
				
				while ((line = input.readLine()) != null)
				{						
					//Console.consoleFFMPEG.append(line + System.lineSeparator() );		
															
					//Errors
					checkForErrors(line);																										
				}	
				
				//Console.consoleFFMPEG.append(System.lineSeparator());
			}					
			process.waitFor();	
							     																		
		} catch (IOException io) {//Bug Linux							
		} catch (InterruptedException e) {
			error = true;
		}
		finally {
			isRunning = false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void checkGPUAvailable()
	{
		if (System.getProperty("os.name").contains("Windows"))
		{
			try {
				
				Process process;								
				double version = Double.parseDouble(System.getProperty("os.version"));
				if (version >= 10.0)
				{
					process = Runtime.getRuntime().exec("powershell -Command \"Get-CimInstance Win32_VideoController | Select-Object -ExpandProperty Name\"");
				}
				else
					process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
				
		        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		        String line;
		        while ((line = reader.readLine()) != null)
		        {
		            line = line.trim();
		            if (!line.isEmpty() && !line.toLowerCase().contains("name"))
		            {
		            	Console.consoleFFMPEG.append(line + System.lineSeparator());
		            	
		                if (line.contains("NVIDIA"))
		                {
		                	hasNvidiaGPU = true;	
		                	GPUCount ++;
		                }
		                else if (line.contains("AMD"))
		                {
		                	hasAMDGPU = true;	
		                	GPUCount ++;
		                }
		                else if (line.contains("Intel"))
		                {
		                	hasIntelGPU = true;	
		                	GPUCount ++;
		                }
		            }
		        }
		        
		        Console.consoleFFMPEG.append(System.lineSeparator());
			}
			catch (IOException e) //If the Windows command crashes, set all values to true, then check all GPUs using FFmpeg
			{
				hasNvidiaGPU = true;
				hasAMDGPU = true;
				hasIntelGPU = true;
			}
		}
	}
	
	public static void checkGPUCapabilities(String file, boolean isVideoPlayer) {
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		isGPUCompatible = false;
		cudaAvailable = false;
		amfAvailable = false;
		qsvAvailable = false;
		videotoolboxAvailable = false;
		vulkanAvailable = false;
		
		//Check is GPU can decode				
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
		&& Shutter.comboGPUDecoding.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false
		&& Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false
		|| (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")))
		{
			String vcodec = "";
			if (FFPROBE.videoCodec != null && FFPROBE.totalLength > 40)
			{
				vcodec = FFPROBE.videoCodec.replace("video", "");
				for (String s : Shutter.functionsList)
				{
					if (vcodec.toLowerCase().equals(s.replace(".", "").replace("-", "").toLowerCase())
					|| s.toLowerCase().contains(vcodec.toLowerCase()))
					{
						vcodec = s;
						break;
					}
					else
						vcodec = vcodec.toUpperCase();
				}
			}

			if (vcodec.equals("H.264") || vcodec.equals("HEVC") || vcodec.equals("VP8") || vcodec.equals("VP9") || vcodec.equals("AV1") || vcodec.equals("MPEG-1") || vcodec.equals("MPEG-2"))
			{
				isGPUCompatible = true;
			}
			
			if (FFPROBE.imageDepth > 10)
			{
				isGPUCompatible = false;
			}
						
			if (isGPUCompatible)
			{
				//Scaling
				String bitDepth = "nv12";
				if (FFPROBE.imageDepth == 10)
				{
					bitDepth = "p010";
				}	
				
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false || isVideoPlayer)
				{								
					//Check for Nvidia/AMD or Intel GPU
					if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("auto"))
					{
						if (System.getProperty("os.name").contains("Windows"))
						{			
							if (hasNvidiaGPU)
							{
								//Cuda
								FFMPEG.gpuFilter(" -hwaccel cuda -hwaccel_output_format cuda -i " + '"' + file + '"' + " -vf scale_cuda=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
																
								if (FFMPEG.error == false)
									cudaAvailable = true;
							}
							else if (hasAMDGPU)
							{
								//AMF
								FFMPEG.gpuFilter(" -i " + '"' + file + '"' + " -vf vpp_amf=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
																
								if (FFMPEG.error == false)
									amfAvailable = true;
							}
							
							if (hasIntelGPU)
							{
								//QSV
								FFMPEG.gpuFilter(" -hwaccel qsv -hwaccel_output_format qsv -init_hw_device qsv:hw,child_device_type=dxva2 -i " + '"' + file + '"' + " -vf scale_qsv=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
								
								if (FFMPEG.error == false)
									qsvAvailable = true;
							}
							
							//Vulkan
							if (FFMPEG.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
							{
								FFMPEG.gpuFilter(" -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan=gpu:1  -i " + '"' + file + '"' + " -vf scale_vulkan=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
							}
							else
								FFMPEG.gpuFilter(" -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan=gpu:0  -i " + '"' + file + '"' + " -vf scale_vulkan=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
								
							if (FFMPEG.error == false)
								vulkanAvailable = true;
							
							if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
							{								
								if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use CUDA decoding with AMF or QSV encoding
								{
									cudaAvailable = false;
								}
								else if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use AMF decoding with NVENC or QSV encoding
								{
									amfAvailable = false;
								}
								else if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use QSV decoding with NVENC or AMF encoding
								{
									qsvAvailable = false;
								}
								else if (comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("AMD AMF Encoder")) //Cannot use VULKAN decoding with QSV encoding
								{
									vulkanAvailable = false;
								}
							}
						}
						else //Mac
						{
							//videotoolbox
							FFMPEG.gpuFilter(" -hwaccel videotoolbox -hwaccel_output_format videotoolbox_vld -i " + '"' + file + '"' + " -vf scale_vt=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -");

							if (FFMPEG.error == false)
								videotoolboxAvailable = true;								
						}
						
						//Disable GPU if not available
						if (cudaAvailable == false && amfAvailable == false && qsvAvailable == false && videotoolboxAvailable == false && vulkanAvailable == false)
							isGPUCompatible = false;
					}
					else //Check the current selection
					{		
						String device = "";
						if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("vulkan"))
						{
							if (FFMPEG.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
							{
								device = " -init_hw_device vulkan=gpu:1";
							}
							else
								device = " -init_hw_device vulkan=gpu:0";
							
						}
						else if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("qsv"))
						{
							device = " -init_hw_device qsv:hw,child_device_type=dxva2";
						}
						
						String scaleFilter = "scale_";
						if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("amf"))
						{
							scaleFilter = "vpp_" ;
						}
						
						FFMPEG.gpuFilter(" -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -hwaccel_output_format " + Shutter.comboGPUFilter.getSelectedItem().toString() + device + " -i " + '"' + file + '"' +  " -vf " + scaleFilter + Shutter.comboGPUFilter.getSelectedItem().toString() + "=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
						
						if (FFMPEG.error)
						{								
							isGPUCompatible = false;
							
							if (Shutter.comboGPUDecoding.getSelectedItem().equals("cuda"))
							{
								cudaAvailable = false;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("amf"))
							{
								amfAvailable = false;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("qsv"))
							{
								qsvAvailable = false;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("videotoolbox"))
							{
								videotoolboxAvailable = false;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("vulkan"))
							{
								vulkanAvailable = false;
							}
						}
						else
						{
							if (Shutter.comboGPUDecoding.getSelectedItem().equals("cuda"))
							{
								cudaAvailable = true;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("amf"))
							{
								amfAvailable = true;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("qsv"))
							{
								qsvAvailable = true;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("videotoolbox"))
							{
								videotoolboxAvailable = true;
							}
							else if (Shutter.comboGPUDecoding.getSelectedItem().equals("vulkan"))
							{
								vulkanAvailable = true;
							}
						}
					}
				}						

				FFMPEG.error = false;
				FFMPEG.errorLog.setLength(0);
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			checkGPUDeinterlacing();
			
		}
		
	}

	public static void checkGPUDeinterlacing() {

		boolean limitToFHD = false;
		switch (comboFonctions.getSelectedItem().toString())
		{
			//Limit to Full HD
			case "AVC-Intra 100":
			case "DNxHD":
			case "XDCAM HD422":
			case "XDCAM HD 35":
			case "DVD" : //Needed 16:9 aspect ratio
				
				if (FFPROBE.imageResolution.equals("1440x1080") == false)
				{
					limitToFHD = true;	
				}
				
				break;
		}	
		
		//setScale gives already all the correct settings
		if (settings.Image.setScale("", limitToFHD).contains("cuda"))
		{
			if (comboForcerDesentrelacement.getModel().getSize() != 2 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("yadif") == false)
			{
				comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "yadif", "bwdif" }));
				comboForcerDesentrelacement.setSelectedIndex(0);
			}
		}
		else if (settings.Image.setScale("", limitToFHD).contains("qsv"))
		{
			if (comboForcerDesentrelacement.getModel().getSize() != 2 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("advanced") == false)
			{
				comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "advanced", "bob" }));
				comboForcerDesentrelacement.setSelectedIndex(0);
			}
		}
		else if (settings.Image.setScale("", limitToFHD).contains("vulkan"))
		{
			if (comboForcerDesentrelacement.getModel().getSize() != 1 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("bwdif") == false)
			{
				comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif" }));
				comboForcerDesentrelacement.setSelectedIndex(0);
			}
		}
		else
		{
			if (comboForcerDesentrelacement.getModel().getSize() != 5 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("yadif") == false)
			{
				comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "yadif", "bwdif", "estdif", "w3fdif", "detelecine" }));
				comboForcerDesentrelacement.setSelectedIndex(0);	
			}
		}
	}
	
	public static void setCropDetect(File file) {
	
		cropdetect = "";
		
		String cmd =  " -an -frames:v 5 -vf cropdetect -f null -" + '"';
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
		{
			cmd =  " -an -frames:v 5 -vf cropdetect -f null -";						
		}
				
		//Input point
		String inputPoint = " -ss " + (float) (VideoPlayer.playerCurrentFrame) * VideoPlayer.inputFramerateMS + "ms";
		if (FFPROBE.totalLength <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
			inputPoint = " -loop 1";
		
		screenshotIsRunning = true; //Workaround to not change the frame size
		
		FFMPEG.run(inputPoint + " -i " + '"' + file + '"' + cmd);	
		
		try {
			do {
				Thread.sleep(100);
			} while(FFMPEG.isRunning);
		} catch (Exception er) {}	
		
		screenshotIsRunning = false;
		
		if (cropdetect != "")
		{
			String c[] = FFMPEG.cropdetect.split(":");
			
			textCropPosX.setText(c[2]);						
			textCropWidth.setText(c[0]);
			textCropHeight.setText(c[1]);
			textCropPosY.setText(c[3]);
			
			int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);	
			int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);
			int width = (int) Math.ceil((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayer.player.getHeight()) / FFPROBE.imageHeight);
			int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayer.player.getWidth()) / FFPROBE.imageWidth);
			
			if (width > VideoPlayer.player.getWidth())
				width = VideoPlayer.player.getWidth();
			
			if (height > VideoPlayer.player.getHeight())
				height = VideoPlayer.player.getHeight();
			
			selection.setBounds(x, y, width, height);
		}	
		
	}
	
	public static void gpuFilter(final String cmd) {
		
		error = false;	
		
	    //Console.consoleFFMPEG.append(Shutter.language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + cmd);
	    
		try {
			
			ProcessBuilder processFFMPEG;

			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));
				process = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));									
				process = processFFMPEG.start();
			}	
				
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
			
			//Console.consoleFFMPEG.append(System.lineSeparator());
			
			while ((line = input.readLine()) != null) {
				
				//Console.consoleFFMPEG.append(line + System.lineSeparator());		
				
				//Errors
				checkForErrors(line);					
																
			}					
			process.waitFor();		
			
			//Console.consoleFFMPEG.append(System.lineSeparator());
				
		} catch (IOException io) {//Bug Linux							
		} catch (InterruptedException e) {
			error = true;
		}
	}
	
	public static void devices(final String cmd) {
		
		error = false;		
		isRunning = true;
		
	    Console.consoleFFMPEG.append(Shutter.language.getProperty("command") + cmd);
			
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				
				try {

					ProcessBuilder processFFMPEG;
					if (System.getProperty("os.name").contains("Windows"))
					{													
						processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG));
						process = processFFMPEG.start();
					}
					else
					{
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
					
					Console.consoleFFMPEG.append(System.lineSeparator());
					
					while ((line = input.readLine()) != null) {						
						
						Console.consoleFFMPEG.append(line + System.lineSeparator());		
											
						//Get devices Mac
						if (cmd.contains("avfoundation") && line.contains("]")) 
						{	
							if (isAudioDevices && line.contains("Error") == false)
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

						//Errors
						checkForErrors(line);																		
					}			
					
					process.waitFor();		
					
					Console.consoleFFMPEG.append(System.lineSeparator());
				   					     																		
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
	
	public static void playerWaveform(final String cmd) {
						
		try {
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + cmd + '"');
				waveformProcess = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + cmd);									
				waveformProcess = processFFMPEG.start();
			}	
		
			//Allows to write into the stream
			OutputStream stdin = waveformProcess.getOutputStream();
			waveformWriter = new BufferedWriter(new OutputStreamWriter(stdin));
			
			InputStream is = waveformProcess.getInputStream();				
			BufferedInputStream inputStream = new BufferedInputStream(is);

			VideoPlayer.waveform = ImageIO.read(inputStream);
			
			inputStream.close();
			
			waveformProcess.waitFor();
		   					     																		
		} catch (IOException io) {//Bug Linux							
		} catch (Exception e) {}
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
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -" + '"');
				process = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -");							
				process = processFFMPEG.start();
			}		
						
			Console.consoleFFMPEG.append(Shutter.language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -");
			
			String line;
	
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
								
			Console.consoleFFMPEG.append(System.lineSeparator());
			
			while ((line = input.readLine()) != null)
			{			
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
	   				
			Console.consoleFFMPEG.append(System.lineSeparator());
			
		} catch (IOException io) {//Bug Linux							
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static void suspendProcess()
	{
		try {
		        				
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			{
				if (NCNN.isRunning)
				{
					Runtime.getRuntime().exec("kill -SIGSTOP " + NCNN.process.pid());
				}
				else
				{
					Runtime.getRuntime().exec("kill -SIGSTOP " + process.pid());
				}
			}
			else
			{					           	            
				String pausep = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pausep = pausep.substring(1,pausep.length()-1);
				pausep = '"' + pausep.substring(0,(int) (pausep.lastIndexOf("/"))).replace("%20", " ")  + "/Library/pausep.exe" + '"';	
				
				if (NCNN.isRunning)
				{
					Runtime.getRuntime().exec(pausep + " " + NCNN.process.pid());
				}
				else
				{
					Runtime.getRuntime().exec(pausep + " " + process.pid());
				}
			}
			
			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported())
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.PAUSED);
			
		} catch (SecurityException | IllegalArgumentException | IOException e1) {	}
	}
	
	@SuppressWarnings("deprecation")
	public static void resumeProcess()
	{
		try {	
			
			elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
			
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))        
			{
				if (NCNN.isRunning)
				{
					Runtime.getRuntime().exec("kill -SIGCONT " + NCNN.process.pid());
				}
				else
				{
					Runtime.getRuntime().exec("kill -SIGCONT " + process.pid());
				}
			}
			else
			{				
				String pausep = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pausep = pausep.substring(1,pausep.length()-1);
				pausep = '"' + pausep.substring(0,(int) (pausep.lastIndexOf("/"))).replace("%20", " ")  + "/Library/pausep.exe" + '"';
				
				if (NCNN.isRunning)
				{
					Runtime.getRuntime().exec(pausep + " " + NCNN.process.pid() + " /r");
				}
				else
				{
					Runtime.getRuntime().exec(pausep + " " + process.pid() + " /r");
				}
			}
			
			btnStart.setText(language.getProperty("btnPauseFunction"));
			
			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported())
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.NORMAL);
			
		} catch (SecurityException | IllegalArgumentException | IOException e1) {	}
	}
	
 	private static void setProgress(String line, final boolean pass2, String cmd) {				
									
		if (line.contains("Input #1"))
			firstInput = false;
				
		//Get the duration
	    if (line.contains("Duration") && line.contains("Duration: N/A") == false && line.contains("<Duration>") == false && line.contains("Segment-Durations-Ms") == false && firstInput)
		{	    	    	
			String str = line.substring(line.indexOf(":") + 2);
			String[] split = str.split(",");	 
	
			String ffmpegTime = split[0].replace(".", ":");	  
							
			if (caseEnableSequence.isSelected())
			{
				fileLength = (int) (liste.getSize() / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) );
			}
			else if (FFPROBE.totalLength <= 40) //Image
			{
				fileLength = Integer.parseInt(Settings.txtImageDuration.getText()) * 1000;
			}
			else if (VideoPlayer.playerInMark > 0 || VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
			{
				fileLength = VideoPlayer.durationH * 3600 + VideoPlayer.durationM * 60 + VideoPlayer.durationS;
			}
			else
				fileLength = (getTimeToSeconds(ffmpegTime));
			
			if (caseConform.isSelected())
			{
				float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));	
				if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))
				{
					fileLength = (int) (fileLength * (FFPROBE.currentFPS / newFPS ));	
				}
				else if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
				{
					fileLength = (int) (fileLength * (newFPS / FFPROBE.currentFPS));	
				}
			}
			
			if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform")))
			{
				float newFPS = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".")));		
				fileLength = (int) (fileLength * (FFPROBE.currentFPS / newFPS));
			}
						
			if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) && comboFilter.getSelectedItem().toString().equals(".gif") == false && Shutter.caseCreateSequence.isSelected() == false)
			{
				fileLength = 1;
			}
			
			if ((comboFonctions.getSelectedItem().toString().equals("H.264")
			|| comboFonctions.getSelectedItem().toString().equals("H.265")
			|| comboFonctions.getSelectedItem().toString().equals("H.266")
			|| comboFonctions.getSelectedItem().toString().equals("WMV")
			|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
			|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
			|| comboFonctions.getSelectedItem().toString().equals("WebM")
			|| comboFonctions.getSelectedItem().toString().equals("AV1")
			|| comboFonctions.getSelectedItem().toString().equals("Theora")
			|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
			|| comboFonctions.getSelectedItem().toString().equals("Xvid")
			|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
			&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
			{
				fileLength = (fileLength * 2);
			}	

			if (cmd.contains("-loop"))
			{
				progressBar1.setMaximum(Integer.parseInt(Settings.txtImageDuration.getText()));
			}
			else if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert")) ==  false)			
			{ 
				progressBar1.setMaximum(fileLength);	
			}	
			
		}  	
	    	    	    
    	//Progression
    	if (line.contains("time=") && line.contains("time=N/A") == false && line.contains("ebur128") == false
	  	&& lblCurrentEncoding.getText().equals(language.getProperty("lblEncodageEnCours")) == false 
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
    		    	
    		
    	    if (cmd.contains("vidstabdetect"))
    	    {
    	    	if (line.contains("size=N/A") == false)
    	    		progressBar1.setValue(getTimeToSeconds(ffmpegTime));
    	    }
    	    else
    	    {
				if (pass2)
				{
					progressBar1.setValue((fileLength / 2) + getTimeToSeconds(ffmpegTime));
				}
				else
				{
					progressBar1.setValue(getTimeToSeconds(ffmpegTime));
				}
    	    }	
    	}
		  
		//Elapsed time
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
		tempsEcoule.setSize(tempsEcoule.getPreferredSize().width, 15);
		         
		  //Remaining time
		  if ((line.contains("frame=") || line.contains("time=")) && line.contains("time=N/A") == false && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false)
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
							|| comboFonctions.getSelectedItem().toString().equals("H.266")
							|| comboFonctions.getSelectedItem().toString().equals("WMV")
							|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
							|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
							|| comboFonctions.getSelectedItem().toString().equals("WebM")
							|| comboFonctions.getSelectedItem().toString().equals("AV1")
							|| comboFonctions.getSelectedItem().toString().equals("Theora")
							|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
							|| comboFonctions.getSelectedItem().toString().equals("Xvid")
						 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
						 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
					 total = (int) ((fileLength / 2) * FFPROBE.currentFPS);
				 
				 else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) == false && caseForcerEntrelacement.isSelected() == false)
				 {
					 float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));	
					 total = (int) ((float) (fileLength * FFPROBE.currentFPS) * (newFPS / FFPROBE.currentFPS));
				 }
				 else
					 total = (int) (fileLength * FFPROBE.currentFPS);
				 
				 int restant = ((total - frames) / fps);
		 	 
				 if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")) == false)
				 {
					 String pass = "";
					 if ((comboFonctions.getSelectedItem().toString().equals("H.264")
								|| comboFonctions.getSelectedItem().toString().equals("H.265")
								|| comboFonctions.getSelectedItem().toString().equals("H.266")
								|| comboFonctions.getSelectedItem().toString().equals("WMV")
								|| comboFonctions.getSelectedItem().toString().equals("MPEG-1")
								|| comboFonctions.getSelectedItem().toString().equals("MPEG-2")
								|| comboFonctions.getSelectedItem().toString().equals("WebM")
								|| comboFonctions.getSelectedItem().toString().equals("AV1")
							 	|| comboFonctions.getSelectedItem().toString().equals("Theora")
								|| comboFonctions.getSelectedItem().toString().equals("MJPEG")
								|| comboFonctions.getSelectedItem().toString().equals("Xvid")
							 	|| comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
							 	&& case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && BitratesAdjustement.DVD2Pass)
					 {
						 if (pass2 == false)
							 pass = " - 1/2";
						 else
							 pass = " - 2/2";
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

					lblBy.setVisible(false);
					tempsRestant.setText(Shutter.language.getProperty("tempsRestant") + " " + heures + minutes + secondes + pass + " - " + fps + " " + Shutter.language.getProperty("fps"));
					tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);
					 
					if (heures != "" || minutes != "" || secondes != "")
					{
						tempsEcoule.setVisible(false);
						tempsRestant.setVisible(true);
						
						if (tempsRestant.getX() + tempsRestant.getSize().width > lblArrows.getX())
	       				{
	       					lblArrows.setVisible(false);
	       				}
					}
					else
					{
						tempsRestant.setVisible(false);	
						lblBy.setVisible(true);
					}
				 }
			 }	
			 		 
		  }		
		  
		  //Cut detection
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
		  
		  //autocrop detection
		  if (line.contains("Parsed_cropdetect"))
		  {
			  cropdetect = line.substring(line.indexOf("crop=") + 5);
		  }	  
	}

	private static void postAnalyse() {
		
		 //Loudness & Normalization
	     if (comboFonctions.getSelectedItem().toString().equals("Loudness & True Peak")
	    || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization"))
	    || (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible()))
	     {
               analyseLufs = "";
               
               for (String line : getAll.toString().substring(getAll.toString().lastIndexOf("Summary:") + 12).split(System.lineSeparator()))
               {
            	   if (line.contains("[out#"))
            	   {
            		  break;
            	   }
            	   else
            		   analyseLufs += line + System.lineSeparator();
               }
                                             
               shortTermValues = new StringBuilder();
               
               float momentaryTerm = (float) -1000.0;
               String momentaryTermTC = "";
               float shortTerm = (float) -1000.0;
               String shortTermTC = "";
               
               for (String allValues : getAll.toString().split(System.lineSeparator()))            	   
               {
	    	 		if (allValues.contains("Parsed_ebur128") && allValues.contains("Summary:") == false && allValues.contains("TARGET"))
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
                   if (comboFonctions.getSelectedItem().toString().equals("Loudness & True Peak")  == false && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization")) == false)
                   {
                	   db = comboNormalizeAudio.getSelectedItem().toString().split(" ");
                   }
                   
                   newVolume = Float.parseFloat(db[0]) - Float.parseFloat(lufsFinal[0].replace(" ", ""));
               }
	     }	
	     	     
	     //Black detection
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
	     
	     //Media offline detection
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
	     
	     //VMAF
	     if (comboFonctions.getSelectedItem().toString().equals("VMAF"))
	     {
	    	 	VMAFScore = "";
	    	 	
	    	 	for (String vmafLine : getAll.toString().split(System.lineSeparator()))
	    	 	{
	    	 		if (vmafLine.contains("VMAF score"))
	    	 		{	    	 			
	    	 			String s[] = vmafLine.split("\\]");
	    	 			VMAFScore = s[1].substring(1);
	    	 		}
	    	 	}
	     }
	}
	
	public static int getTimeToSeconds(String time) {
				
		String[] t = time.split(":");

		int heures = Integer.parseInt(t[0]);
		int minutes = Integer.parseInt(t[1]);
		int secondes = Integer.parseInt(t[2]);
		int images = Integer.parseInt(t[3]);
		images = (images / 40);
		
		int totalSecondes = (heures * 3600) + (minutes * 60) +  secondes;  
						
		return totalSecondes;
		
	}

}