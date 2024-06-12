/*******************************************************************************************
* Copyright (C) 2024 PACIFICO PAUL
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

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import application.Console;
import application.GOP;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import application.VideoPlayer;
import settings.FunctionUtils;

public class FFPROBE extends Shutter {
	
public static Process process;
public static boolean isRunning = false;
public static boolean calcul = false;
public static boolean audioOnly = true;
public static boolean hasAudio = false;
public static boolean attachedPic = false;
public static Thread processData;
public static Thread processFrameData;
public static Thread processVideoLevels;
public static Thread processGOP;
public static Thread processFindStreams;
public static Thread processSetLength;
public static String analyzedMedia = null;
public static String subtitlesCodec = "";
public static int subtitleStreams = 0;
public static int audioStreams = 0;
public static int totalLength;
public static String getVideoLengthTC;
public static String lumaLevel;
public static float currentFPS;
public static String interlaced;
public static String fieldOrder;
private static boolean videoStream = false;
public static String pixelformat = "";
public static int imageDepth = 8;
public static String imageResolution;
public static int previousImageWidth;
public static int imageWidth;
public static int imageHeight;
public static float imageRatio = 1.777777f;
public static int cropPixelsWidth;
public static int cropPixelsHeight;
public static String dropFrameTC = "";
public static String timecode1 = "";
public static String timecode2 = "";
public static String timecode3 = "";
public static String timecode4 = "";
public static int audioSampleRate = 48000;
public static boolean stereo = true;	
public static boolean surround = false;
public static String channelLayout = "";
public static int channels;	
public static int qantization;
public static String videoCodec;
public static String audioCodec;
public static String[] audioCodecs;
public static String audioBitrate;
public static String timeBase = "";
public static String creationTime = "";
public static float HDRmin = 0;
public static float HDRmax = 0;
public static int maxCLL = 0;
public static int maxFALL = 0;
public static float keyFrame = 0;
public static int gopCount = 0;
public static int gopSpace = 124;
public static boolean hasAlpha = false;
public static boolean isRotated = false;

	public static void Data(final String file) {	
			
		if (file.equals(analyzedMedia) == false || scanIsRunning || Settings.btnWaitFileComplete.isSelected())
		{
			analyzedMedia = file;

			getVideoLengthTC = null;
			
			if (inputDeviceIsRunning == false)
			{
				channels = 0;
				stereo = false;	
			}
			
			interlaced = null;
			if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
			{
				fieldOrder = null;
			}
			
			currentFPS = 25.0f; //Used to play audio with the Video Player
			dropFrameTC = "";
			surround = false;
			channelLayout = "";
			totalLength = 0;
			qantization = 16;
			subtitlesCodec = "";
	 		subtitleStreams = 0;
	 		audioSampleRate = 48000;
	 		audioStreams = 0;
			if (calcul == false) //pour ne pas réactive audioOnly lors de l'analyse calculH264
				audioOnly = true;
			creationTime = "";
			lumaLevel = "unavailable";
			videoCodec = null;
			audioCodec = null;
			audioCodecs = new String[50];
			audioBitrate = null;
			FFMPEG.error = false;
			hasAudio = false; 		
			attachedPic = false;
			btnStart.setEnabled(false);
			
			imageRatio = 1.777777f;
						
			//Watermark scaling
			String extension =  file.toString().substring(file.toString().lastIndexOf("."));
			
			if (caseGenerateFromDate.isSelected() == false
			&& comboFonctions.getSelectedItem().toString().equals("JPEG") == false
			&& comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) == false
			&& extension.toLowerCase().equals(".pdf") == false)
			{
				previousImageWidth = imageWidth;
			}			
						
			timecode1 = "";
			timecode2 = "";
			timecode3 = "";
			timecode4 = "";
			
			processData = new Thread(new Runnable() {
	
				@Override
				public void run() {
					
					try {	
								
						String PathToFFPROBE;
						ProcessBuilder processFFPROBE;
											
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
							PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';						
							
							if (inputDeviceIsRunning && (file.equals("Capture.current.screen") || file.equals("Capture.input.device")))
							{
								if (file.equals("Capture.input.device") && RecordInputDevice.inputDeviceResolution == "")
								{		
									String s[] = RecordInputDevice.setInputDevices().split("-f ");
									if (overlayDeviceIsRunning)
										s = RecordInputDevice.setOverlayDevice().split("-f ");
									
									String id[] = s[1].split("\"");
									String inputDevice = id[0] + '"' + id[1] + '"';
	
									processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -f " + inputDevice);
								}
								else if (file.equals("Capture.input.device") && RecordInputDevice.videoDeviceIndex > 0)
								{
									String[] deviceSize = RecordInputDevice.inputDeviceResolution.split("x");
									processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -f lavfi -i nullsrc=s=" + deviceSize[0] + "x" + deviceSize[1] + ":d=0:r=" + currentFPS + '"');	
								}
								else
									processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -f lavfi -i nullsrc=s=" + RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth + ":d=0" + '"');	
							}	
							else
								processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -i " + '"' + file + '"');
						}
						else
						{
							PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
							PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";						
							
							if (inputDeviceIsRunning && (file.equals("Capture.current.screen") || file.equals("Capture.input.device")))
							{
								if (file.equals("Capture.input.device") && RecordInputDevice.inputDeviceResolution == "")
								{
									String s[] = RecordInputDevice.setInputDevices().split("-f ");
									if (overlayDeviceIsRunning)
										s = RecordInputDevice.setOverlayDevice().split("-f ");
									
									String id[] = s[1].split("\"");
									if (RecordInputDevice.audioDeviceIndex > 0 && overlayDeviceIsRunning == false)
										id =  s[2].split("\"");;
	
									String inputDevice = id[0] + '"' + id[1] + '"';
									
									processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -f " + inputDevice);
								}
								else if (file.equals("Capture.input.device") && RecordInputDevice.videoDeviceIndex > 0)
								{
									String[] deviceSize = RecordInputDevice.inputDeviceResolution.split("x");
									processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -f lavfi -i nullsrc=s=" + deviceSize[0] + "x" + deviceSize[1] + ":d=0:r=" + currentFPS + '"');	
								}
								else
									processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -f lavfi -i nullsrc=s=" + RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth + ":d=0");	
							}
							else
								processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -i " + '"' + file + '"');
						}
						
						isRunning = true;
						process = processFFPROBE.start();
											
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
	
						Console.consoleFFPROBE.append(System.lineSeparator());
						
						while ((line = input.readLine()) != null)
						{						
							Console.consoleFFPROBE.append(line + System.lineSeparator());		
										
							//Errors
							FFMPEG.checkForErrors(line);
													
							//Entrelacement
							if (line.contains("top first") || line.contains("top coded first"))
							{								
								interlaced = "1";
								if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
									fieldOrder = "0";
							}
							else if (line.contains("bottom first") || line.contains("bottom coded first"))
							{								
								interlaced = "1";
								if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
									fieldOrder = "1";
							}
							
			                // Durée
				            if (line.contains("Duration:") && line.contains("Duration: N/A") == false && line.contains("<Duration>") == false)
				            {			            	 
					    		String str = line.substring(line.indexOf(":") + 2);
					    		String s[] = str.split(",");	 
					    		
					    		String ffmpegTime = s[0].replace(".", ":");	  
					    					    							    		
					    		getVideoLengthTC = ffmpegTime;
					    		totalLength = (getTimeToMS(ffmpegTime));
					    		    		
					         	if (grpBitrate.isVisible() && totalLength != 0)
								{     				
					         		lblH264.setText(new File(file).getName());
						            lblH264.setVisible(true);
					         		
						        	NumberFormat formatter = new DecimalFormat("00");
						        	int hours = ((totalLength) / 3600000);
						        	int min =  ((totalLength) / 60000) % 60;
						            int sec = ((totalLength) / 1000) % 60;
						            int frames = (int) Math.floor((float) totalLength / ((float) 1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS);
						            
						            textH.setText(formatter.format(hours));
						            textM.setText(formatter.format(min));
						            textS.setText(formatter.format(sec));
						            textF.setText(formatter.format(frames));
						             
						      		if (VideoPlayer.playerVideo != null)	
						     			VideoPlayer.totalDuration();
	
						            setFilesize();
								}
				            }
	 			            
				            // Détection YUVJ
							if (line.contains("Video:") && line.contains("attached pic") == false)
							{						
								//Codec vidéo
								String[] splitVideo = line.substring(line.indexOf("Video:")).split(" ");							
								videoCodec = splitVideo[1].replace(",", "");
							
								if (videoCodec.equals("dnxhd") && line.toLowerCase().contains("dnxhr"))
								{
									videoCodec = "dnxhr";
								}
								
								if (videoCodec.equals("qtrle"))
								{
									videoCodec = "qt animation";
								}
								
								//Création de la waveform pour le lecteur vidéo
					            audioOnly = false;
								 
								// Niveaux					 
				                if (line.contains("tv"))
				                   lumaLevel = "16-235";
				                else if (line.contains("(pc)"))
				                   lumaLevel = "0-255";
				                			                
				                // Lecture
							 	String data = line;
				                data = line.substring(data.indexOf("Video:"));
	
				                // Timecode Size
				                String split[] = data.split(",");
				                int i = 0;
				                do {
				                    i ++;
				                } while ((split[i].contains("x") == false || split[i].contains("xyz")) && i < split.length - 1);
				                
				                if (split[i].contains("["))
				                {
				                	String s[] = split[i].split("\\[");
				                	split[i] = s[0].replace(" ","");
				                }
			
				                //Bug workaround when it shows "unspecified size" && is a scale form like 1920x1080
				                if (split[i].contains("unspecified size") == false && split[i].contains("x"))
				                {		
					                String resolution = split[i].substring(split[i].indexOf("x") + 1);
					              	String splitr[] = resolution.split(" ");
		
					              	// Crop Image
					                String height = split[i];
					                
					                String splitx[]= height.split("x");
					                
					                String getHeight[] = splitx[1].split(" ");
		
						            imageWidth = Integer.parseInt(splitx[0].replace(" ", ""));
						            imageHeight = Integer.parseInt(splitr[0]);
					                imageResolution = imageWidth + "x" + getHeight[0];
		              			               
					                if (inputDeviceIsRunning && file.equals("Capture.current.screen"))
					                {
					                	imageResolution = RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth;
					                	imageWidth = RecordInputDevice.screenWidth;
					                	imageHeight = RecordInputDevice.screenHeigth;
					                }
					                			               			                
					                //Ratio du lecteur
					                if (line.contains("DAR"))
					                {
					                	String[] splitDAR = line.split("DAR");
					                	String[] splitDAR2 = splitDAR[1].split(",");
					                	String[] splitDAR3 = splitDAR2[0].replace(" ", "").replace("]", "").split(":");
					                	int ratioWidth = Integer.parseInt(splitDAR3[0]);
					                	int ratioHeight = Integer.parseInt(splitDAR3[1]);
					                	imageRatio = (float) ratioWidth / ratioHeight;
					                }
					                else
					                	imageRatio = (float) Integer.parseInt(splitx[0].replace(" ", "")) / Integer.parseInt(getHeight[0]);      
					                
									
									if (isRotated)
									{	
										Integer h = imageHeight;
										Integer w = imageWidth;
										  
										imageWidth =  h;
										imageHeight = w;
										imageRatio = (float) imageWidth / imageHeight;
									}
									
					                // Crop Form
					                int largeur = 0;
					                int hauteur = 0;			               
				                	
					                // Pixels cropés
					                if (ratioFinal < ((float) imageWidth / imageHeight))
					                {
						                int pixelsWidth = (imageWidth - hauteur);
						                cropPixelsWidth =  (int) (float) pixelsWidth / 2;
						                cropPixelsHeight = 0;
					                }
					                else 
					                {
						                int pixelsHeight = (imageHeight - largeur);
						                cropPixelsHeight = (int) (float) pixelsHeight / 2;
						                cropPixelsWidth = 0;
					                }	
					                
					                // FPS
					                if (inputDeviceIsRunning)
					            	{
					            		if (file.equals("Capture.current.screen"))
					            			currentFPS = Float.parseFloat(RecordInputDevice.txtScreenRecord.getText());
					            		else
					            			currentFPS = Float.parseFloat(RecordInputDevice.txtInputDevice.getText());
					            	}
					                else
					                {
							            if (line.contains("fps")) 
							            {						            	
							                String str[] = line.split("fps");
							                							                
							                str = str[0].substring(str[0].lastIndexOf(",")).split(" ");
							                
							                //For DV format
							                if (str[1].contains("k"))
							                {
							                	str = line.split("tbr");
								                str = str[0].substring(str[0].lastIndexOf(",")).split(" ");
							                }
							               
							                currentFPS = Float.parseFloat(str[1]);
							                
							                //Used for VFR						      
							                str = String.valueOf(currentFPS).split("\\.");	
	
							                if (str[1].length() == 2 && str[1].equals("00") == false)
							                {
							                	if (str[1].equals("88") == false //119.88
							                	&& str[1].equals("94") == false //59.94
							                	&& str[1].equals("97") == false //29.97
							                	&& str[1].equals("98") == false) //23.98
							                	{						                	
								                	str = line.split("tbr");
									                str = str[0].substring(str[0].lastIndexOf(",")).split(" ");
									                currentFPS = Float.parseFloat(str[1]);
							                	}
							                }
							            } 
							            else if (line.contains("tbr"))
							            {
							            	String str[] = line.split("tbr");
						                	
							                str = str[0].substring(str[0].lastIndexOf(",")).split(" ");
							                
							                currentFPS = Float.parseFloat(str[1]);
							                
							                //Used for VFR						      
							                str = String.valueOf(currentFPS).split("\\.");	
	
							                if (str[1].length() == 2 && str[1].equals("00") == false)
							                {
							                	if (str[1].equals("88") == false //119.88
							                	&& str[1].equals("94") == false //59.94
							                	&& str[1].equals("97") == false //29.97
							                	&& str[1].equals("98") == false) //23.98
							                	{						                	
								                	str = line.split("tbr");
									                str = str[0].substring(str[0].lastIndexOf(",")).split(" ");
									                currentFPS = Float.parseFloat(str[1]);
							                	}
							                }
							            }
					                }
				                }
							 }
							 
							 if (line.contains("attached pic"))
							 {
								 attachedPic = true;
							 }
							
				        	 if (line.contains("Audio:") && (line.contains("0 channels")) == false)
				        	 {
				        		 hasAudio = true;
				        		 
				        		 if (line.contains("pcm_s24"))
				        			 qantization = 24;
				        		 
				        		 if (line.contains("pcm_s32"))
				        			 qantization = 32;
				        			 
				        		 channels ++;
						        		 
				        		 if (line.contains("Hz"))
				        		 {
				        			 String s[] = line.split(",");
				        			 
				        			 int i = 0;
				        			 do {			        						 
				        				 i++;
				        			 } while (s[i].contains("Hz") == false);
				        			 audioSampleRate = Integer.parseInt(s[i].replace(" ", "").replace("Hz", ""));
				        		 }
				        		 
				        		 if (line.contains("2 channels") || line.contains("stereo"))
				        		 {
				        			 stereo = true;
				        			 surround = false;
				        		 }
				        		 else if (line.contains("1 channels") || line.contains("mono"))
				        		 {
				        			 stereo = false;
				        			 surround = false;
				        		 }
				        		 else if (line.contains("5.1") || line.contains("6.1") || line.contains("7.1"))
				        		 {
				        			 if (line.contains("5.1"))
				        			 {
				        				 channelLayout = "5.1";
				        			 }
				        			 else if (line.contains("6.1"))
				        			 {
				        				 channelLayout = "6.1";
				        			 }
				        			 else if (line.contains("7.1"))
				        			 {
				        				 channelLayout = "7.1";
				        			 }
				        			 				        			 
				        			 stereo = true;//permet de l'utiliser tel quel avec les codecs audio car il est embedded
				        			 surround = true;
				        		 }
				        		 
				        		 //Codec audio
								 String[] splitAudio = line.substring(line.indexOf("Audio:")).split(" ");							
								 audioCodec = splitAudio[1].replace(",", "");
								 
								 audioCodecs[channels - 1] = audioCodec;
								 														 
								 String[] splitBitrate = line.substring(line.indexOf("Audio:")).split(" kb/s");	
								 String[] bitrate = splitBitrate[0].split(" ");
								 audioBitrate =	bitrate[bitrate.length - 1];		
				        		 
				        	 }
				        	 
				        	 //Extract Audio	
				        	 if (line.contains("Audio:"))
				        		 audioStreams ++;
				        	 
				        	 //Extract Subtitles			     		
				        	 if (line.contains("Subtitle:"))
				        	 {
				        		 subtitleStreams ++;
				        		 
				        		 String s[] = line.substring(line.lastIndexOf(":") + 1).split(" ");	
				        		 subtitlesCodec = s[1].replace(",", "");			        	 }
				        	 
				        	//Timecode from XML
				        	if (line.contains("StartTimecode"))
				        	{
				        		String s1[] = line.split(">");
				        		String s2[] = s1[1].split("<");
				        					        		
				        		String str[] = s2[0].replace(";" , ":").split(":");
			                	timecode1 = str[0].replace(" ", "");
			                	timecode2 = str[1].replace(" ", "");
			                	timecode3 = str[2].replace(" ", "");
			                	timecode4 = str[3].replace(" ", "");
				        	}

			        	 	//Timecode
				            if (line.contains("timecode") && line.contains("timecode is invalid") == false && line.contains("Input") == false) //Avoid "timecode" in the filename
				            {		
				            	//Drop frame / non drop frame
				            	if (line.contains(";"))
				            	{
				            		dropFrameTC = ";";
				            	}
				            	else
				            		dropFrameTC = ":";

				            	if (Shutter.caseShowTimecode.isSelected()
				            	|| comboFonctions.getSelectedItem().equals("") //IMPORTANT allow first scan without selecting a function
			            		|| comboFonctions.getSelectedItem().toString().contains("XDCAM")
			            		|| comboFonctions.getSelectedItem().equals("XAVC")
			            		|| comboFonctions.getSelectedItem().equals("AVC-Intra 100")			            		
			            		|| comboFonctions.getSelectedItem().equals("DNxHD")
			            		|| comboFonctions.getSelectedItem().equals("DNxHR")
			            		|| comboFonctions.getSelectedItem().equals("Apple ProRes")
			            		|| comboFonctions.getSelectedItem().equals("QT Animation")
								|| comboFonctions.getSelectedItem().equals("GoPro CineForm")
			            		|| comboFonctions.getSelectedItem().equals("Uncompressed")
			            		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection"))
			            		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert"))			            			
		            			|| (VideoPlayer.caseInternalTc != null && VideoPlayer.caseInternalTc.isSelected()))
					            {			
				            		if (FFPROBE.timecode1 == "")
					                {			            					            			
				            			String str[] = line.replace(" ", "").replace(";" , ":").split(":");
				            			
					                	timecode1 = str[1];
					                	timecode2 = str[2];
					                	timecode3 = str[3];
					                	timecode4 = str[4];
					                }
					            }
				            }
				            
			                // Creation time
			                if (line.contains("creation_time") && creationTime.equals(""))
			                {
			                	//Example   : 2021-05-20T09:55:22.000000Z
			                	String s[] =  line.substring(line.indexOf(":") + 1).replace(" ", "").replace("T", " ").split("\\.");
			                	
			                	creationTime = s[0];
			                }
				            
					}		
					process.waitFor();	
	
					Console.consoleFFPROBE.append(System.lineSeparator());
								
					} catch (Exception e) {	
						FFMPEG.error = true;
					} finally {
						isRunning = false;
						btnStart.setEnabled(true);
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
							
				}			
			});
			processData.start();
		}		
	}

	public static void FrameData(final String file) {	
						
		videoStream = false;
		pixelformat = "";
		imageDepth = 8;		
		timeBase = "";
		HDRmin = (float) 0.01;
		HDRmax = 1000;		
		maxCLL = 1000;
		maxFALL = 400;
		hasAlpha = false;
		isRotated = false;

		FFMPEG.error = false;
		btnStart.setEnabled(false);
		
		processFrameData = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {		
					
					String PathToFFPROBE;
					ProcessBuilder processFFPROBE;
					if (System.getProperty("os.name").contains("Windows"))
					{						
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
						PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';
						processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -show_frames -show_streams -read_intervals %+#1 -loglevel warning -i " + '"' + file + '"');
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
						processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -i " + '"' + file + '"' + " -show_frames -show_streams -read_intervals %+#1 -loglevel warning");
					}	
					
					isRunning = true;	
					Process process = processFFPROBE.start();			         			       

			        //IMPORTANT avoid process hanging
			        Thread stream = new Thread(new Runnable() {
				      		        	
						@Override
						public void run() {
							
					        InputStreamReader esr = new InputStreamReader(process.getErrorStream());
					        BufferedReader bre = new BufferedReader(esr);
							
					        Console.consoleFFPROBE.append(System.lineSeparator());
					        
							String line;
							try {
								
								while ((line = bre.readLine()) != null)
								{
									Console.consoleFFPROBE.append(line + System.lineSeparator());
								}
								
							} catch (IOException e) {}
							
							Console.consoleFFPROBE.append(System.lineSeparator());
						}
			        	
			        });
			        stream.start();
			        
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);

			        String line;			        		      		      
			        			        
			        Console.consoleFFPROBE.append(System.lineSeparator());
			        
					//Analyse des données	
			        while ((line = br.readLine()) != null)
			        {		
						Console.consoleFFPROBE.append(line + System.lineSeparator());	
						
						//Errors
						FFMPEG.checkForErrors(line);
																		
						  if (line.contains("interlaced_frame"))
						  {
							  String interlace = line.substring(line.indexOf("interlaced_frame") + 17);
							  interlaced = interlace;
						  }
						  
						  if (line.contains("top_field_first")) 
						  {
							  String field = line.substring(line.indexOf("top_field_first") + 16);
							  
							  if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
							  {
								  if (field.equals("1"))
									  fieldOrder = "0";
								  else
									  fieldOrder = "1";
							  }
						  }
						  
			              if (line.contains("rotation"))
			              {			            	  
			            	  String s[] = line.split("=");
			                	
			            	  if (s[1].contains("90"))
			            	  {
				            	  isRotated = true;
			            	  }			            	  
			              }
			              
						  if (line.contains("codec_type=video"))
							  videoStream = true;
	
						  if (line.contains("bits_per_raw_sample") && videoStream)
						  {
							  String depth = line.substring(line.indexOf("bits_per_raw_sample") + 20);
							  
							  if (depth.equals("N/A") == false)
							  {
								  imageDepth = Integer.parseInt(depth);						  
								  videoStream = false;
							  }
						  }
						  
						  if (line.contains("pix_fmt") && videoStream)
						  {
							  String depth = line.substring(line.indexOf("pix_fmt") + 8);
							  
							  pixelformat = depth;
									  
							  if (depth.equals("N/A") == false)
							  {
								  if (depth.contains("p10"))
									  imageDepth = 10;	
								  else if (depth.contains("p16"))
									 imageDepth = 16;
								  
								  videoStream = false;
							  }						  
						  }	
	
						  if (line.contains("time_base"))
						  {
							  String s[] = line.split("=");
							  timeBase = s[1];
							  if (timeBase.contains("1/"))
								  timeBase = s[1].replace("1/", ""); 
						  }
						  
						  if (line.contains("min_luminance"))
						  {
							  String s[] = line.split("=");
							  String s2[] = s[1].split("/");								  
							  HDRmin = (float) Integer.parseInt(s2[0]) / Integer.parseInt(s2[1]);						  
						  }
						  
						  if (line.contains("max_luminance"))
						  {
							  String s[] = line.split("=");
							  String s2[] = s[1].split("/");								  
							  HDRmax = (float) Integer.parseInt(s2[0]) / Integer.parseInt(s2[1]);	
						  }
							       
						  if (line.contains("max_content"))
						  {
							  String s[] = line.split("=");
							  maxCLL = Integer.parseInt(s[1]);
						  }
						  
						  if (line.contains("max_average"))
						  {
							  String s[] = line.split("=");
							  maxFALL = Integer.parseInt(s[1]);
						  }
						  
						  if (line.contains("alpha_mode=1") || FFPROBE.pixelformat.contains("a"))
						  {
							  hasAlpha = true;
						  }
						  
					}										
													
				} catch (Exception e) {	
					FFMPEG.error = true;
				} finally {
					isRunning = false;
					btnStart.setEnabled(true);
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				
			}			
		});
		processFrameData.start();
	}
	
	public static void AnalyzeGOP(final String file, boolean isGOPWindow) {
		
		gopCount = 0;
		gopSpace = 124;
				
		processGOP = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {		
					
					String PathToFFPROBE;
					ProcessBuilder processFFPROBE;
					if (System.getProperty("os.name").contains("Windows"))
					{						
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
						PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';
						processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -show_frames -select_streams v:0 -i " + '"' + file + '"');						
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
						processFFPROBE = new ProcessBuilder("/bin/bash", "-c" , PathToFFPROBE + " -hide_banner -i " + '"' + file + '"' + " -select_streams v:0 -show_frames");
					}	
					processFFPROBE.redirectErrorStream(true); //IMPORTANT AVOID FREEZING
					
					isRunning = true;	
					
					//Attente de l'ouverture de la fenêtre
					if (isGOPWindow)
					{
						do {
							Thread.sleep(100);
						} while(GOP.frame == null);
					}
					
					Process process = processFFPROBE.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String line;
			        
					//Analyse des données	
			        int i = 0; //stop la boucle si elle est interminable
					int intra = 0;
					
					do {
						
						line = br.readLine();
						
						Console.consoleFFPROBE.append(line + System.lineSeparator());
					
						 if (line == null || intra == 2 || i > 10000 || gopCount > 500)
						 {
							isRunning = false;
				            process.destroy();
				            if ((i > 10000 || gopCount > 500) && isGOPWindow)
				            {
				            	GOP.frame.dispose();
				            	JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantAnalyzeGop"), Shutter.language.getProperty("analyzeError"), JOptionPane.ERROR_MESSAGE);			
				            }
				            break;
						 }
						 else
						 {
							if (line.equals("pict_type=I")) {
								
								intra ++;
								
								if (intra == 2)
								{
									if (isGOPWindow)
										GOP.newImage('I', gopSpace);	
									
								    gopSpace += 112;
								    gopCount += 1;
								}
							}
							if (line.equals("pict_type=P")) {
								
								if (isGOPWindow)
									GOP.newImage('P', gopSpace);
								
							    gopSpace += 112;
							    gopCount += 1;
							}
							if (line.equals("pict_type=B")) {
								
								if (isGOPWindow)
									GOP.newImage('B', gopSpace);
								
							    gopSpace += 112;
							    gopCount += 1;
							 }						   				    
							 i ++;  
						 }
					} while(line != null);	
					
					process.wait();
								
					} catch (Exception e) {						
					} finally {
						isRunning = false;
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}		
						
			}			

		});	
		processGOP.start();
	}
	
	public static void Keyframes(final String file, float inputTime, boolean getTheNextKey) {

		keyFrame = 0;
		FFMPEG.error = false;	
				
		processFrameData = new Thread(new Runnable()  {
			
			float seekTime = inputTime;
			
			@Override
			public void run() {
				
				if (getTheNextKey == false)
				{
					seekTime = inputTime - 20000; //Rewind of 20sec
					
					if (seekTime < 0)
						seekTime = 0;
				}

				try {		
					
					String PathToFFPROBE;
					ProcessBuilder processFFPROBE;
					if (System.getProperty("os.name").contains("Windows"))
					{						
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
						PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';
						processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -v quiet -read_intervals " + (long) seekTime + "ms -show_entries frame=pict_type,pts_time,flags -select_streams v:0 -skip_frame nokey -print_format csv=print_section=0 -i " + '"' + file + '"');
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
						processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -hide_banner -i " + '"' + file + '"' + " -v quiet -read_intervals " + (long) seekTime + "ms -show_entries frame=pict_type,pts_time,flags -select_streams v:0 -skip_frame nokey -print_format csv=print_section=0");
					}					
				
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					isRunning = true;	
					Process process = processFFPROBE.start();

			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        			        
			        String line;
			        
			        Console.consoleFFPROBE.append(System.lineSeparator());
			        			        
					//Analyse des données	
			        while ((line = br.readLine()) != null) 
			        {	
						Console.consoleFFPROBE.append(line + System.lineSeparator());	
						
						//Errors
						FFMPEG.checkForErrors(line);

						if (line.equals("") == false && line.contains("I"))
						{						
							String s[] =  line.split(",");
							float keyPTS = Float.parseFloat(s[0]) * 1000;
							
							if (getTheNextKey)
							{
								if (keyPTS > inputTime)
								{
									keyFrame = keyPTS / VideoPlayer.inputFramerateMS;	
									process.destroy();
									break;
								}
							}
							else 
							{				
								if (keyPTS < inputTime && keyPTS > keyFrame)
								{
									keyFrame = keyPTS / VideoPlayer.inputFramerateMS;	
								}
								else if (keyPTS >= inputTime)
								{
									process.destroy();
									break;
								}
							} 
						}
			        }
				
			        process.waitFor();		
			        
			        Console.consoleFFPROBE.append(System.lineSeparator());
							
				} catch (Exception e) {				
					FFMPEG.error = true;					
				} finally {
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					isRunning = false;
				}						
			}			
		});	
		processFrameData.start();
		
	}
	
	public static boolean FindStreams(final String file) {			
		
		try {		
			String PathToFFPROBE;
			ProcessBuilder processFFPROBE;
			if (System.getProperty("os.name").contains("Windows"))
			{						
				PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
				PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';
				processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -hide_banner -show_streams" + " -i " + '"' + file + '"');
			}
			else
			{
				PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
				PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
				processFFPROBE = new ProcessBuilder("/bin/bash", "-c" , PathToFFPROBE + " -hide_banner -i " + '"' + file + '"' + " -show_streams");
			}				
			
			isRunning = true;	
			Process process = processFFPROBE.start();
			
			String line;
			InputStreamReader input = new InputStreamReader(process.getInputStream());
	        BufferedReader br = new BufferedReader(input);
							
			//Analyse
	        while ((line = br.readLine()) != null) 
			{	        	
	        	if (line.contains("codec_type=video"))
	        	{
	        		return true;					        		 
	        	}
	        	else if (line.contains("codec_type=audio"))
		        {
	        		return false;
		        }
			}									
			process.waitFor();
			
			} catch (Exception e) {}
			finally {
				isRunning = false;
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		
		return false;			

	}
	
	public static void setLength() {

		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		processSetLength = new Thread(new Runnable()  {
			
			@Override
			public void run() { 
				
				try {
					
					calcul = true;
										
					//Updating video file
					if (Shutter.liste.getSize() != 0)
					{
						if (Shutter.scanIsRunning)
						{
							File dir = new File(Shutter.liste.firstElement());
							for (File f : dir.listFiles())
							{
								if (f.isHidden() == false && f.isFile())
								{
									//Sending to Data()
									FFPROBE.Data(f.toString());
									lblH264.setText(new File(f.toString()).getName());
									
							        do {
										Thread.sleep(100);
									} while (processData.isAlive()); 
							        
									break;
								}
							}
						} 
						else if (Shutter.fileList.getSelectedIndices().length != 0)
						{
							lblH264.setText(new File(Shutter.fileList.getSelectedValue()).getName());
				            lblH264.setVisible(true);
							
							//Sending to Data()
							if (inputDeviceIsRunning == false)					
								FFPROBE.Data(Shutter.fileList.getSelectedValue());
							
							do {
								Thread.sleep(100);
							} while (processData.isAlive()); 
							
							if (totalLength != 0 && inputDeviceIsRunning == false)
							{           		
				    		    if (Shutter.comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
				    		    {
				    				float debit = (float) ((float) 23000000 / FFPROBE.totalLength) * 8;
				    				
				    				if (comboFilter.getSelectedIndex() == 0) //H.264
									{
				    					if (debit > 38)
					    					Shutter.debitVideo.setSelectedItem(38000);
					    				else
					    					Shutter.debitVideo.setSelectedItem((int) debit * 1000);
									}
									else //H.265
									{
										if (debit > 50)
					    					Shutter.debitVideo.setSelectedItem(50000);
					    				else
					    					Shutter.debitVideo.setSelectedItem((int) debit * 1000);
									}
			    		    	}	
				         		
					        	NumberFormat formatter = new DecimalFormat("00");
				
					            textH.setText(formatter.format((totalLength) / 3600000));
					            textM.setText(formatter.format(((totalLength) / 60000) % 60) );
					            textS.setText(formatter.format((totalLength / 1000) % 60));				        
					            textF.setText(formatter.format(((int) Math.floor((float) totalLength / ((float) 1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS))));
					             			             
					            if (VideoPlayer.playerVideo != null)	
						     		VideoPlayer.totalDuration();
					             
					            setFilesize();
							}
						}
						else
						{
							lblH264.setVisible(false);
							textH.setText("00");
							textM.setText("00");
							textS.setText("00");
							textF.setText("00");
							bitrateSize.setText("-");
						}
					}
					
				} catch (Exception e) {}
				finally {
					calcul = false;
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		processSetLength.start();
	
	}
	
	public static void setFilesize() {

		if (grpBitrate.isVisible())
        {
			int multi = 0;
			if (lblAudioMapping.getText().equals("Multi"))
			{
				if (comboAudio1.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio2.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio3.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio4.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio5.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio6.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio7.getSelectedIndex() != 16)
					multi += 1;
				if (comboAudio8.getSelectedIndex() != 16)
					multi += 1;
			}
			else
				multi = 1;
					
			if (Shutter.liste.getSize() > 0 && imageResolution != null && (lblVBR.getText().equals("CQ") == false || lblVBR.isVisible() == false))
			{
				if (isLocked)
				{
					//Set Bitrate
					int h = Integer.parseInt(textH.getText());
					int min = Integer.parseInt(textM.getText());
					int sec = Integer.parseInt(textS.getText());
					int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
					int tailleFinale = Integer.parseInt(bitrateSize.getText());
					float result = (float) tailleFinale / ((h * 3600) + (min * 60) + sec);
					float resultAudio = (float) (audio*multi) / 8 / 1024;
					float resultatdebit = (result - resultAudio) * 8 * 1024;
					debitVideo.getModel().setSelectedItem((int) resultatdebit);
				}
				else
				{
			        //Set Filesize
					int h = Integer.parseInt(textH.getText());
					int min = Integer.parseInt(textM.getText());
					int sec = Integer.parseInt(textS.getText());
					int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
					
					if (comboAudioCodec.getSelectedItem().toString().equals("FLAC"))
						audio = 1536;
					
					Integer videoBitrate = FunctionUtils.setVideoBitrate();
												
					float resultVideo = (float) videoBitrate / 8 / 1024;
					float resultAudio =  (float) (audio*multi) / 8 / 1024;
					float resultatdebit = (resultVideo  + resultAudio) * ( (h * 3600)+(min * 60)+sec);
					bitrateSize.setText(String.valueOf((int)resultatdebit));	
				}
			}
			else
				bitrateSize.setText("-");
		
        }
		
	}
	
	private static int getTimeToMS(String time) {
						
		String[] t = time.split(":");
		
		int heures = Integer.parseInt(t[0]);
		int minutes = Integer.parseInt(t[1]);
		int secondes = Integer.parseInt(t[2]);
		int images = (int) (Integer.parseInt(t[3]) * 10);
		
		int totalMiliSecondes = (heures * 3600000) + (minutes * 60000) + (secondes * 1000) + images;  
		
		return totalMiliSecondes;
		
	}
}