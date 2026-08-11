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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import shutterencoder.functions.settings.BitratesAdjustement;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.SceneDetection;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class FFMPEG extends Shutter {
	
public static String PathToFFMPEG;
public static int fileLength = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static BufferedWriter writer;
public static Thread runProcess = new Thread();
private static Thread displayThread;
public static Process process;
private static Process processAudio;
private static InputStream audio = null;	
private static AudioInputStream audioInputStream = null;
private static SourceDataLine line = null;
private static float directDisplayInputRatio = 1.777777f;
private static Image frameVideo;
public static String analyseLufs;
public static String integrated;
public static String truePeak;
public static String LRA;
public static String Threshold;
public static Float mseSensibility = 800f;
public static float newVolume;
public static StringBuilder shortTermValues;
public static StringBuilder blackFrame;
public static StringBuilder mediaOfflineFrame;
public static String VMAFScore;
public static String cropdetect;
private static boolean firstInput = true;
public static int firstScreenIndex = -1;

//Encoding average FPS	
private static int frame0 = 0;
private static long time = 0;
public static long elapsedTime = 0;
public static int previousElapsedTime = 0;
private static int fps = 0;

private static StringBuilder getOutputLog;
public static StringBuilder errorLog = new StringBuilder();

	public static void getFFmpegPath() {
		
		boolean isWindows = System.getProperty("os.name").contains("Windows");
		
		PathToFFMPEG = isWindows ? Utils.getLibraryPath() + "\\ffmpeg.exe" : Utils.getLibraryPath() + "/ffmpeg"; 
		
		if (Settings.btnCustomFFmpegPath.isSelected() && Settings.txtCustomFFmpegPath.getText().equals("") == false)
		{
			PathToFFMPEG = Settings.txtCustomFFmpegPath.getText();
		}
	}

	public static void setEnvironment(ProcessBuilder pb) {
		
		Map<String, String> env = pb.environment();

		String var = new File(FFMPEG.PathToFFMPEG).getParent();
		
		if (System.getProperty("os.name").contains("Windows") == false)
		{
			var = var.replace("\\ ", " ");
		}
		
        // Set your environment variables
        env.put("DYLD_LIBRARY_PATH", var);
        env.put("VK_ICD_FILENAMES", var + "/MoltenVK_icd.json");
        env.put("MVK_CONFIG_LOG_LEVEL", "1");		
	}
	
	public static void run(String cmd) {
			
		time = 0;
		fps = 0;

		elapsedTime = (System.currentTimeMillis() - previousElapsedTime);
		error = false;	
		firstInput = true;
		
		Console.consoleFFMPEG.append(System.lineSeparator());
	    Console.consoleFFMPEG.append(language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + cmd);
	    
	    getOutputLog = new StringBuilder();

		if (saveCode)
		{
			if (cmd.contains("-pass 2") == false)
				LibraryUtils.saveToXML(cmd);
		}
		else if (btnStart.getText().equals(language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled() && cmd.contains("-f rawvideo") == false
		&& cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false && cmd.contains("preview.png") == false && cmd.contains("ebur128=peak=true") == false)
		{			
			//On récupère le nom précédent
			if (lblCurrentEncoding.getText().equals(language.getProperty("lblEncodageEnCours")))
			{
				lblCurrentEncoding.setText(RenderQueue.tableRow.getValueAt(RenderQueue.tableRow.getRowCount() - 1, 0).toString());
			}
			
			if (caseChangeFolder1.isSelected() == false)
			{
				lblDestination1.setText(language.getProperty("sameAsSource"));
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
			
			lblCurrentEncoding.setText(language.getProperty("lblEncodageEnCours"));
			
			Console.consoleFFMPEG.append(System.lineSeparator());
		}
		else
		{
			isRunning = true;
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false && cmd.contains("-f rawvideo") == false && cmd.contains("waveform.png") == false && cmd.contains("preview.bmp") == false  && cmd.contains("preview.png") == false && screenshotIsRunning == false)
				UIController.disableAll();
			
			runProcess = new Thread(new Runnable()  {
				
				@SuppressWarnings("resource")
				@Override
				public void run() {
					
					try {
						
						ProcessBuilder processFFMPEG;
						OutputStream stdin;
						BufferedReader input;
						
						//Command args
						String args = PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd.replace("PathToFFMPEG", PathToFFMPEG);

						//Display output
						if (cmd.contains("pipe:1"))
						{
							args += " | " + PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i pipe:0 -an -c:v bmp -pix_fmt rgb24 -f image2pipe -";
						}
						
						//Splitting pipe char
						if (System.getProperty("os.name").contains("Windows"))
						{
							List<String> tokens = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(args).results()
							        .map(m -> m.group(1) != null ? m.group(1) : m.group(2))
							        .collect(Collectors.toList());
		
							int pipeIndex = tokens.indexOf("|");
							File workingDir = new File(Utils.getLibraryPath()).getParentFile();
		
							if (pipeIndex == -1)
							{
							    processFFMPEG = new ProcessBuilder(tokens);
							    processFFMPEG.directory(workingDir);
							    process = processFFMPEG.start();	
							    
							    stdin = process.getOutputStream();
							    input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
							}
							else
							{
							    ProcessBuilder pb1 = new ProcessBuilder(tokens.subList(0, pipeIndex));
							    ProcessBuilder pb2 = new ProcessBuilder(tokens.subList(pipeIndex + 1, tokens.size()));
							    pb1.directory(workingDir);
							    pb2.directory(workingDir);
		
							    List<Process> processes = ProcessBuilder.startPipeline(List.of(pb1, pb2));
							    process = processes.get(processes.size() - 1);

							    //Select the first process
								stdin = processes.get(0).getOutputStream();
								input = new BufferedReader(new InputStreamReader(processes.get(0).getErrorStream()));
							}
						}
						else //Mac & Linux
						{														
							processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , args);
							
							if (LibraryUtils.libplaceboAvailable)
							{
								FFMPEG.setEnvironment(processFFMPEG);
							}
													
							process = processFFMPEG.start();
							
							stdin = process.getOutputStream();
							input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						}

						//IMPORTANT
						if (cmd.contains("cropdetect") == false
						&& btnStart.getText().equals(language.getProperty("btnPauseFunction"))|| btnStart.getText().equals(language.getProperty("btnStopRecording")))
						{
							VideoPlayerUI.resizeAll();
						}
							
						String line;
						InputStream video = process.getInputStream();				
						BufferedInputStream videoInputStream = new BufferedInputStream(video);	
								        
						//Allows to write into the stream						
				        writer = new BufferedWriter(new OutputStreamWriter(stdin));	
						
				        if (cmd.contains("pipe:1"))
						{				  				        	
				        	VideoPlayerCore.playerStop();
					     
				        	Thread playerThread = new Thread(new Runnable() {
	
								@Override
								public void run() {

						            try {
						            	
										do {
											
											if (btnStart.getText().equals(language.getProperty("btnPauseFunction"))
											|| btnStart.getText().equals(language.getProperty("btnStopRecording"))
											|| cancelled) //Empty the buffer
											{	
												VideoPlayerCore.frameVideo = ImageIO.read(videoInputStream);	
												VideoPlayerUI.player.repaint();
											}
											
										} while (VideoPlayerCore.frameVideo != null);
										
									} catch (Exception e) {}
								}
					    		
					    	});
					        playerThread.start();
						}

				        Console.consoleFFMPEG.append(System.lineSeparator());

						while ((line = input.readLine()) != null)
						{			
							Console.consoleFFMPEG.append(line + System.lineSeparator());

							getOutputLog.append(line + System.lineSeparator());
																															
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
									{
										setProgress(line, true, cmd);
									}
									else
										setProgress(line, false, cmd);	
								}
							}	
						}					
						int exitCode = process.waitFor();
						
						if (exitCode != 0)
						{
							if (cancelled == false)
								error = true;
							
							//Errors
							checkForErrors(getOutputLog.toString());	
						}
						
						if (cancelled == false)
						{							
							postAnalyse();	
						}
					   					     																		
					} catch (IOException io) {//Bug Linux							
					} catch (InterruptedException e) {
						if (cancelled == false)
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
						getOutputLog.append(line + System.lineSeparator());
					}
					int exitCode = process.waitFor();
					
					if (exitCode != 0)
					{
						if (cancelled == false)
							error = true;
					}
									   					     																		
				} catch (IOException io) {//Bug Linux							
				} catch (Exception e) {
					if (cancelled == false)
						error = true;	
				}
			}
		});		
		runProcess.start();
	}
	
	public static void checkForErrors(String output) {

	    String[] lines = output.split("\\R");

	    for (String line : lines)
	    {
	        line = line.trim();
	        
	        if (line.isEmpty())
	            continue;

	        if (line.contains("unable to decode APP fields"))
	            continue; // Ignore this known harmless case

	        if (line.contains("No such file or directory")
            || line.contains("Invalid data found when processing input")
            || line.contains("No space left")
            || line.contains("does not contain any stream")
            || line.contains("Error opening filters!")
            || line.contains("Error reinitializing filters!")
            || line.contains("Error initializing filters")
            || line.contains("Error while opening encoder")
            || line.contains("unexpected EOF")
            || line.contains("Decoder (codec none) not found")
            || line.contains("hwaccel initialisation returned error")
            || line.contains("Device setup failed for decoder")
            || line.contains("No device available for decoder")
            || line.contains("no decoder found for")
            || line.contains("Error while decoding stream")
            || line.contains("Current pixel format is unsupported")
            || line.contains("Unknown encoder")
            || line.contains("Could not set video options")
            || line.contains("Could not find tag for codec")
            || line.contains("Input/output error")
            || line.contains("Operation not permitted")
            || line.contains("Permission denied")
            || line.contains("not divisible by 2")
            || line.contains("integer multiple of the specified")
            || line.contains("is not multiple of 4")
            || line.contains("cannot be smaller than input dimensions")
            || line.contains("Failed setup for format")
            || line.contains("Failed to get pixel format")
            || line.contains("hardware accelerator failed to decode picture")
            || line.contains("Your platform doesn't support hardware accelerated"))
	        {
	            if (line.contains("error code") == false
	            && line.contains("return code") == false)
	            {
	            	//Removes text with [...]
	            	line = line.replaceFirst("^(?:\\[[^\\]]*\\]\\s*)+", "");
	            	
	                errorLog.append(line + System.lineSeparator());
	            }
	        }
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

			FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + input + " -filter_complex " + '"' + filter + hstack + '"' + " -c:v rawvideo -map " + '"' + "[out]" + '"' + " -an -f nut pipe:1");
		}
		else
		{
						
			//File
			File inputFile = null;
			
			if (isVideoPlayer)
			{
				inputFile = new File(VideoPlayerCore.videoPath);
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
				videoOutput = "[0:v]scale=1000:-1:scaler=bilinear:sws_dither=none[v]" + ";" + channels + "[v]";
				
				if (FFPROBE.channels == 0 || list.getElementAt(0).equals("Capture.input.device")) {
					videoOutput = "scale=1000:-1:scaler=bilinear:sws_dither=none" + '"';
					audioOutput = "";
				}

			}
			
			if (inputDeviceIsRunning && overlayDeviceIsRunning)
			{	     
				if (RecordInputDevice.audioDeviceIndex > 0)
				{
					videoOutput = "[2:v]scale=iw*" + ((float)  Integer.parseInt(textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[1:v][scaledwatermark]overlay=" + textWatermarkPosX.getText() + ":" + textWatermarkPosY.getText() + "[v]";			
				}
				else
				{
					videoOutput = "[1:v]scale=iw*" + ((float)  Integer.parseInt(textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(textWatermarkSize.getText()) / 100) +			
	        				",lut=a=val*" + ((float) Integer.parseInt(textWatermarkOpacity.getText()) / 100) + 
	        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + textWatermarkPosX.getText() + ":" + textWatermarkPosY.getText() + "[v]";	
				}
					
				if (audioOutput != "")
					videoOutput += ";" + channels + "[v]";
				else
					videoOutput += '"';
			}
			
			String extension = "";			
			if (inputDeviceIsRunning == false)
			{
				extension = inputFile.toString().substring(inputFile.toString().lastIndexOf("."));
			} 
						
			String cmd = " -filter_complex " + '"' + videoOutput + audioOutput	+ " -c:v rawvideo -an -f nut pipe:1";
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			//Loop image					
			String loop = FunctionUtils.setLoop(extension);
			
			if (isVideoPlayer)
			{
				FFMPEG.toFFPLAY(loop + InputAndOutput.inPoint + " -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i " + '"' + inputFile + '"' + InputAndOutput.outPoint + cmd);
			}
			else if (inputDeviceIsRunning)
			{
				if (list.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 || System.getProperty("os.name").contains("Mac") && list.getElementAt(0).equals("Capture.input.device") && RecordInputDevice.audioDeviceIndex > 0)
					cmd = cmd.replace("0:v", "1:v");	
				
				if (overlayDeviceIsRunning && audioOutput == "")
					cmd = cmd.replace("-an", "-map " + '"' + "[v]" + '"');
					
				if (overlayDeviceIsRunning)
					FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet " + RecordInputDevice.setInputDevices() + " " + RecordInputDevice.setOverlayDevice() + cmd);
				else
					FFMPEG.toFFPLAY(" -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet " + RecordInputDevice.setInputDevices() + cmd);
			} 
			else
				FFMPEG.toFFPLAY(loop + " -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -i " + '"' + inputFile + '"' + cmd);					
						
			progressBar.setValue(0);
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
			if (caseCreateSequence.isSelected())
			{
				fps = " -r " +  Float.valueOf(comboInterpret.getSelectedItem().toString().replace(",", "."));
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
										
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + PathToFFMPEG + '"' + " -v quiet "  + InputAndOutput.inPoint + " -i " + '"' + inputFile + '"' + " -vn -c:a pcm_s16le -ar 48k -ac 1 -f wav -");	
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

					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", PathToFFMPEG + " -v quiet " + InputAndOutput.inPoint + " -i " + '"' + inputFile + '"' + " -vn -c:a pcm_s16le -ar 48k -ac 1 -f wav -");	
					processAudio = pba.start();
				}
			}	
			
			Console.consoleFFMPEG.append(System.lineSeparator() + language.getProperty("command") + " " + PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " " + cmd + " | " + PathToFFMPEG + " -v quiet -i pipe:0" + fps + " -c:v bmp -pix_fmt rgb24 -an -f image2pipe -" + System.lineSeparator());
		
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
				LibraryUtils.showInputDeviceFrame = true;
				player.setTitle(language.getProperty("preview"));			
			}
			else
			{
				LibraryUtils.showInputDeviceFrame = false;
				player.setTitle(new File(fileList.getSelectedValue()).getName());
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
					
					if (LibraryUtils.showInputDeviceFrame)
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
						
						if (LibraryUtils.showInputDeviceFrame)
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
						float inputFramerateMS = (float) (1000 / FFPROBE.currentFPS);
						if (caseCreateSequence.isSelected())
						{
							inputFramerateMS = (float) (1000 / (Float.valueOf(comboInterpret.getSelectedItem().toString().replace(",", "."))));
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
				String pausep = '"' + Utils.getLibraryPath() + "\\pausep.exe" + '"';	
				
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
				String pausep = '"' + Utils.getLibraryPath() + "\\pausep.exe" + '"';
				
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
				fileLength = (int) (list.getSize() / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) );
			}
			else if (FFPROBE.totalLength <= 40) //Image
			{
				fileLength = Integer.parseInt(Settings.txtImageDuration.getText()) * 1000;
			}
			else if (InputAndOutput.segments != "")
			{		
				fileLength = 0;
				for (VideoPlayerMultiCuts.CutSegment seg : VideoPlayerMultiCuts.cutSegments)
				{
					double totalIn =  (seg.inH * 3600 + seg.inM * 60 + seg.inS);
					double totalOut = (seg.outH * 3600 + seg.outM * 60 + seg.outS);
					fileLength += totalOut - totalIn;
				}
			}
			else if (VideoPlayerUI.playerMarkIn > 0 || VideoPlayerUI.playerMarkOut < VideoPlayerCore.waveformContainer.getWidth())
			{
				fileLength = VideoPlayerUI.durationH * 3600 + VideoPlayerUI.durationM * 60 + VideoPlayerUI.durationS;
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
				float newFPS = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" " + language.getProperty("fps"), "").replace(",", ".")));		
				fileLength = (int) (fileLength * (FFPROBE.currentFPS / newFPS));
			}
						
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) && comboFilter.getSelectedItem().toString().equals(".gif") == false && caseCreateSequence.isSelected() == false)
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
				progressBar.setMaximum(Integer.parseInt(Settings.txtImageDuration.getText()));
			}
			else if (comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) ==  false)			
			{ 
				progressBar.setMaximum(fileLength);	
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

    		if (progressBar.getString().equals("NaN") || inputDeviceIsRunning)
    			progressBar.setStringPainted(false);
    		else
    			progressBar.setStringPainted(true);    		    	
    		
    		if (pass2)
			{
				progressBar.setValue((fileLength / 2) + getTimeToSeconds(ffmpegTime));
			}
			else
			{
				progressBar.setValue(getTimeToSeconds(ffmpegTime));
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
		
		tempsEcoule.setText(language.getProperty("tempsEcoule") + " " + heures + minutes + secondes);
		tempsEcoule.setSize(tempsEcoule.getPreferredSize().width, 15);
		         
		  //Remaining time
		  if ((line.contains("frame=") || line.contains("time=")) && line.contains("time=N/A") == false && comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false)
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
		 	 
				 if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false && comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false)
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
					tempsRestant.setText(language.getProperty("tempsRestant") + " " + heures + minutes + secondes + pass + " - " + fps + " " + language.getProperty("fps"));
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
		  if (comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) && line.contains("pts"))
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
               integrated = extractLoudnormValue(getOutputLog.toString(), "Input Integrated:\\s*([-+]?[\\d.]+)\\s*LUFS");
               truePeak = extractLoudnormValue(getOutputLog.toString(), "Input True Peak:\\s*([\\-+]?[\\d.]+)\\s*dBTP");
               LRA = extractLoudnormValue(getOutputLog.toString(), "Input LRA:\\s*([\\-+]?[\\d.]+)\\s*LU");
               Threshold = extractLoudnormValue(getOutputLog.toString(), "Input Threshold:\\s*([\\-+]?[\\d.]+)\\s*LUFS");             
               
               for (String line : getOutputLog.toString().substring(getOutputLog.toString().lastIndexOf("Summary:") + 12).split(System.lineSeparator()))
               {
            	   if (line.contains("[out#"))
            	   {
            		  break;
            	   }
            	   else
            	   {
            		   analyseLufs += line + System.lineSeparator();
            	   }
               }
                                             
               shortTermValues = new StringBuilder();
               
               float momentaryTerm = (float) -1000.0;
               String momentaryTermTC = "";
               float shortTerm = (float) -1000.0;
               String shortTermTC = "";
               
               for (String allValues : getOutputLog.toString().split(System.lineSeparator()))            	   
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
            	   shortTermValues.append(language.getProperty("shortTerm"));  
                              
               if (lblCurrentEncoding.getText().contains(language.getProperty("analyzing")))
               {
                   String lufs = extractLoudnormValue(analyseLufs, "I:\\s*([-+]?[\\d.]+)\\s*LUFS");	
                   
                   if (caseTruePeak.isSelected() || caseLRA.isSelected())
                	   lufs = extractLoudnormValue(getOutputLog.toString(), "Input Integrated:\\s*([-+]?[\\d.]+)\\s*LUFS");
                   
                   String db[] = comboFilter.getSelectedItem().toString().split(" ");
                   if (comboFonctions.getSelectedItem().toString().equals("Loudness & True Peak")  == false && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization")) == false)
                   {
                	   db = comboNormalizeAudio.getSelectedItem().toString().split(" ");
                   }

                   newVolume = Float.parseFloat(db[0]) - Float.parseFloat(lufs);
               }
	     }	
	     	     
	     //Black detection
	     if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionBlackDetection")))
	     {
	    	 	blackFrame = new StringBuilder();
	    	 	
	    	 	for (String blackLine : getOutputLog.toString().split(System.lineSeparator()))
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
	     if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionOfflineDetection")))
	     {
	    	 mediaOfflineFrame = new StringBuilder();
	    	 	
			//Stats_file
			File stats_file;
			if (System.getProperty("os.name").contains("Windows"))
				stats_file = new File("stats_file");
			else		    		
				stats_file = new File(dirTemp + "stats_file");
    	 	
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
	    	 	
	    	 	for (String vmafLine : getOutputLog.toString().split(System.lineSeparator()))
	    	 	{
	    	 		if (vmafLine.contains("VMAF score"))
	    	 		{	    	 			
	    	 			String s[] = vmafLine.split("\\]");
	    	 			VMAFScore = s[1].substring(1);
	    	 		}
	    	 	}
	     }
	}
	
	private static String extractLoudnormValue(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            return m.group(1);
        }
		return "";
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