/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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

import application.BlackMagicOutput;
import application.ColorImage;
import application.Console;
import application.CropImage;
import application.CropVideo;
import application.GOP;
import application.OverlayWindow;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import application.SubtitlesWindow;
import application.VideoPlayer;
import application.WatermarkWindow;

public class FFPROBE extends Shutter {
	
public static Process process;
public static boolean isRunning = false;
public static boolean calcul = false;
public static boolean audioOnly = true;
public static boolean hasAudio = false;
public static Thread processData;
public static Thread processFrameData;
public static Thread processVideoLevels;
public static Thread processGOP;
public static Thread processFindStreams;
public static Thread processCalculH264;
public static int subtitleStreams = 0;
public static int audioStreams = 0;
public static int totalLength;
public static String getVideoLengthTC;
public static String lumaLevel;
public static float currentFPS = 25.0f; //Utilisé pour lire l'audio avec le lecteur vidéo
public static String entrelaced;
public static String fieldOrder;
private static boolean videoStream = false;
public static String pixelformat = "";
public static int imageDepth = 8;
public static String imageResolution;
public static int cropWidth;
public static int cropHeight;
public static int cropPixelsWidth;
public static int cropPixelsHeight;
public static String timecode1 = "";
public static String timecode2 = "";
public static String timecode3 = "";
public static String timecode4 = "";
public static int audioSampleRate = 48000;
public static boolean stereo = true;	
public static boolean surround = false;
public static int channels;	
public static int qantization;
public static String videoCodec;
public static String audioCodec;
public static String audioBitrate;
public static String timeBase = "";
public static String creationTime = "";
public static float HDRmin = 0;
public static float HDRmax = 0;

public static int gopCount = 0;
public static int gopSpace = 124;

	public static void Data(final String fichier) {	
		
		getVideoLengthTC = null;
		
		if (inputDeviceIsRunning == false)
		{
			channels = 0;
			stereo = false;	
		}
		
		surround = false;
		totalLength = 0;
		qantization = 16;
 		subtitleStreams = 0;
 		audioSampleRate = 48000;
 		audioStreams = 0;
		if (calcul == false) //pour ne pas réactive audioOnly lors de l'analyse calculH264
			audioOnly = true;
		creationTime = "";
		lumaLevel = "unavailable";
		videoCodec = null;
		audioCodec = null;
		audioBitrate = null;
		FFMPEG.error = false;
		hasAudio = false; 		
		btnStart.setEnabled(false);
		
		VideoPlayer.ratio = 1.777777f;
		
		if (OverlayWindow.caseAddTimecode.isSelected())
		{
			timecode1 = OverlayWindow.TC1.getText();
			timecode2 = OverlayWindow.TC2.getText();			
        	timecode3 = OverlayWindow.TC3.getText();		    
			timecode4 = OverlayWindow.TC4.getText();
		}
		else
		{			
			timecode1 = "";
			timecode2 = "";
			timecode3 = "";
			timecode4 = "";
		}
		
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
						
						if (inputDeviceIsRunning && (fichier.equals("Capture.current.screen") || fichier.equals("Capture.input.device")))
						{
							if (fichier.equals("Capture.input.device") && RecordInputDevice.inputDeviceResolution == "")
							{		
								String s[] = RecordInputDevice.setInputDevices().split("-f ");
								if (overlayDeviceIsRunning)
									s = RecordInputDevice.setOverlayDevice().split("-f ");
								
								String id[] = s[1].split("\"");
								String inputDevice = id[0] + '"' + id[1] + '"';

								processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -f " + inputDevice);
							}
							else if (fichier.equals("Capture.input.device") && RecordInputDevice.videoDeviceIndex > 0)
							{
								String[] deviceSize = RecordInputDevice.inputDeviceResolution.split("x");
								processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -f lavfi -i nullsrc=s=" + deviceSize[0] + "x" + deviceSize[1] + ":d=0:r=" + currentFPS + '"');	
							}
							else
								processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -f lavfi -i nullsrc=s=" + RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth + ":d=0" + '"');	
						}	
						else
							processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -i " + '"' + fichier + '"');
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";						
						
						if (inputDeviceIsRunning && (fichier.equals("Capture.current.screen") || fichier.equals("Capture.input.device")))
						{
							if (fichier.equals("Capture.input.device") && RecordInputDevice.inputDeviceResolution == "")
							{
								String s[] = RecordInputDevice.setInputDevices().split("-f ");
								if (overlayDeviceIsRunning)
									s = RecordInputDevice.setOverlayDevice().split("-f ");
								
								String id[] = s[1].split("\"");
								if (RecordInputDevice.audioDeviceIndex > 0 && overlayDeviceIsRunning == false)
									id =  s[2].split("\"");;

								String inputDevice = id[0] + '"' + id[1] + '"';
								
								processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -f " + inputDevice);
							}
							else if (fichier.equals("Capture.input.device") && RecordInputDevice.videoDeviceIndex > 0)
							{
								String[] deviceSize = RecordInputDevice.inputDeviceResolution.split("x");
								processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -f lavfi -i nullsrc=s=" + deviceSize[0] + "x" + deviceSize[1] + ":d=0:r=" + currentFPS + '"');	
							}
							else
								processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -f lavfi -i nullsrc=s=" + RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth + ":d=0");	
						}
						else
							processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -i " + '"' + fichier + '"');
					}
					
					isRunning = true;
					process = processFFPROBE.start();
										
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
					
					Console.consoleFFPROBE.append(System.lineSeparator());
					
					while ((line = input.readLine()) != null) {
						//// Analyse des données	
						
						Console.consoleFFPROBE.append(line + System.lineSeparator());		
																
						//Erreurs
						if (line.contains("Invalid data found when processing input") 
								|| line.contains("No such file or directory")
								|| line.contains("Invalid data found")
								|| line.contains("No space left")
								|| line.contains("does not contain any stream")
								|| line.contains("Invalid argument"))
						{
							FFMPEG.error = true;
						}
												
						//Entrelacement
						if (line.contains("top first") || line.contains("top coded first"))
						{
							entrelaced = "1";
							if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
								fieldOrder = "0";
						}
						else if (line.contains("bottom first") || line.contains("bottom coded first"))
						{
							entrelaced = "1";
							if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
								fieldOrder = "1";
						}
						
		                // Durée
			            if (line.contains("Duration:") && line.contains("Duration: N/A") == false && line.contains("<Duration>") == false) {
				    		String str = line.substring(line.indexOf(":") + 2);
				    		String s[] = str.split(",");	 
				    						    		
				    		String ffmpegTime = s[0].replace(".", ":");	  
				    					    		
				    		getVideoLengthTC = ffmpegTime;
				    		totalLength = (CalculTemps(ffmpegTime));
				    		
				         	if (grpH264.isVisible() && totalLength != 0)
							{     						         		
					        	 NumberFormat formatter = new DecimalFormat("00");
					             int secondes = ((totalLength) / 1000) % 60;
					             int minutes =  ((totalLength) / 60000) % 60;
					             int heures = ((totalLength) / 3600000);
					             
					             textH.setText(formatter.format(heures));
					             textMin.setText(formatter.format(minutes));
					             textSec.setText(formatter.format(secondes));
					             
					      		if (caseInAndOut.isSelected() && VideoPlayer.playerLeftVideo != null)	
					     			VideoPlayer.totalDuration();

					             setTailleH264();
							}
			            }
 
			            // Détection YUVJ
						 if (line.contains("Video:"))
						 {							

							 //Codec vidéo
							String[] splitVCodec = line.substring(line.indexOf("Video:")).split(" ");							
							videoCodec = splitVCodec[1];
							 
							//Création de la waveform pour le lecteur vidéo
				            audioOnly = false;
							 
							// Niveaux					 
			                if (line.contains("tv"))
			                   lumaLevel = "16-235";
			                else if (line.contains("(pc)"))
			                   lumaLevel = "0-255";
			                			                
			                // Lecture
						 	String ligne = line;
			                ligne = line.substring(ligne.indexOf("Video:"));

			                // Timecode Size
			                String split[]= ligne.split(",");
			                int i = 0;
			                do
			                {
			                    i ++;
			                } while (split[i].contains("x") == false || split[i].contains("xyz"));
			                
			                String resolution = split[i].substring(split[i].indexOf("x") + 1);
			              	String splitr[] = resolution.split(" ");
			                
			              	// Crop Image
			                String height = split[i];
			                String splitx[]= height.split("x");
			                String getHeight[] =  splitx[1].split(" ");

				            int imageWidth = Integer.parseInt(splitx[0].replace(" ", ""));
				            int imageHeight = Integer.parseInt(splitr[0]);
			                imageResolution = imageWidth + "x" + getHeight[0];
			                
			                if (inputDeviceIsRunning && fichier.equals("Capture.current.screen"))
			                {
			                	imageResolution = RecordInputDevice.screenWidth + "x" + RecordInputDevice.screenHeigth;
			                	imageWidth = RecordInputDevice.screenWidth;
			                	imageHeight = RecordInputDevice.screenHeigth;
			                }
			                
			                if (caseRognage.isSelected() || Shutter.inputDeviceIsRunning)
			                {
			                    CropVideo.ImageWidth = imageWidth;
			                    CropVideo.ImageHeight = Integer.parseInt(getHeight[0]);
			                }	
			                if (caseRognerImage.isSelected() || Shutter.inputDeviceIsRunning)
			                {
			                    CropImage.ImageWidth = imageWidth;
			                    CropImage.ImageHeight = Integer.parseInt(getHeight[0]);
			                }					                
			                if (caseLogo.isSelected() || Shutter.inputDeviceIsRunning)
			                {
			                	WatermarkWindow.ImageWidth = imageWidth;
			                	WatermarkWindow.ImageHeight = Integer.parseInt(getHeight[0]);
			                }
			                if (caseAddOverlay.isSelected() || Shutter.inputDeviceIsRunning)
			                {
			                	OverlayWindow.ImageWidth = imageWidth;
			                	OverlayWindow.ImageHeight = Integer.parseInt(getHeight[0]);
			                }
			                if (caseSubtitles.isSelected())
			                {
			                	SubtitlesWindow.ImageWidth = imageWidth;
			                	SubtitlesWindow.ImageHeight = Integer.parseInt(getHeight[0]);
			                }
			                if (caseColor.isSelected() || Shutter.inputDeviceIsRunning)
			                {
			                	ColorImage.ImageWidth = imageWidth;
			                	ColorImage.ImageHeight = Integer.parseInt(getHeight[0]);
			                }
			                			               			                
			                //Ratio du lecteur
			                if (line.contains("DAR"))
			                {
			                	String[] splitDAR = line.split("DAR");
			                	String[] splitDAR2 = splitDAR[1].split(",");
			                	String[] splitDAR3 = splitDAR2[0].replace(" ", "").replace("]", "").split(":");
			                	int ratioLargeur = Integer.parseInt(splitDAR3[0]);
			                	int ratioHauteur = Integer.parseInt(splitDAR3[1]);
			                	VideoPlayer.ratio = (float) ratioLargeur / ratioHauteur;
			                }
			                else
			                	VideoPlayer.ratio = (float) Integer.parseInt(splitx[0].replace(" ", "")) / Integer.parseInt(getHeight[0]);
			              	
			                /*
			              	if (VideoPlayer.ratio < 1.76f)
			              		VideoPlayer.ratio = 1.777777f;*/		         
			                
			                // Crop Form
			                int largeur = 0;
			                int hauteur = 0;
		                	if (caseRognage.isSelected())
		                	{
		                		if (ratioFinal < ((float) imageWidth / imageHeight))
		                		{
		                			hauteur = (int) ((float) imageHeight * ratioFinal);
		                			cropWidth = imageHeight;	
		                			cropHeight = hauteur;
		                		}
		                		else
		                		{
		                			largeur = (int) ((float) imageWidth / ratioFinal);
		                			cropWidth = largeur;	
		                			cropHeight = imageWidth;
		                		}
		                	}				               
		                	
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
			            		if (fichier.equals("Capture.current.screen"))
			            			currentFPS = Float.parseFloat(Settings.txtScreenRecord.getText());
			            		else
			            			currentFPS = Float.parseFloat(Settings.txtInputDevice.getText());
			            	}
			                else
			                {
					            if (line.contains("fps")) 
					            {
					                String str[]= line.split("fps");
					                currentFPS = Float.parseFloat(str[0].substring(str[0].lastIndexOf(",")).replace("s,", "").replace(", ", ""));
					                
					                if (currentFPS == 23.98f)
					                	currentFPS = 23.976f;
					            } 
			                }
						 }
						 
			        	 if (line.contains("Audio:"))
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
			        		 else if  (line.contains("5.1") || line.contains("6.1") || line.contains("7.1"))
			        		 {
			        			 stereo = true;//permet de l'utiliser tel quel avec les codecs audio car il est embedded
			        			 surround = true;
			        		 }
			        		 
			        		 //Codec audio
							 String[] splitACodec = line.substring(line.indexOf("Audio:")).split(" ");							
							 audioCodec = splitACodec[1].replace(",", "");
							 
							 String[] splitBitrate = line.substring(line.indexOf("Audio:")).split(" kb/s");	
							 String[] bitrate = splitBitrate[0].split(" ");
							 audioBitrate =	bitrate[bitrate.length - 1];		
			        		 
			        	 }
			        	 
			        	 //Extract Audio	
			        	 if (line.contains("Audio:"))
			        		 audioStreams ++;
			        	 
			        	 //Extract Subtitles			     		
			        	 if (line.contains("Subtitle:"))
			        		 subtitleStreams ++;
			        							 
						//Timecode
			            if (line.contains("timecode") && (OverlayWindow.caseShowTimecode.isSelected()
			            		|| comboFonctions.getSelectedItem().equals("XDCAM HD422")
			            		|| comboFonctions.getSelectedItem().equals("XAVC")
			            		|| comboFonctions.getSelectedItem().equals("AVC-Intra 100")			            		
			            		|| comboFonctions.getSelectedItem().equals("DNxHD")
			            		|| comboFonctions.getSelectedItem().equals("DNxHR")
			            		|| comboFonctions.getSelectedItem().equals("Apple ProRes")
			            		|| comboFonctions.getSelectedItem().equals("QT Animation")
								|| comboFonctions.getSelectedItem().equals("GoPro CineForm")
			            		|| comboFonctions.getSelectedItem().equals("Uncompressed YUV")
			            		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection"))
			            		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionInsert"))))
			            {
			                String str[]= line.replace(";" , ":").split(":");
		                	timecode1 = str[1].replace(" ", "");
		                	timecode2 = str[2].replace(" ", "");
		                	timecode3 = str[3].replace(" ", "");
		                	timecode4 = str[4].replace(" ", "");
			            }
			            
			            if (line.contains("timecode") && BlackMagicOutput.frame != null) {
							if (BlackMagicOutput.frame.isVisible())
							{
				                String str[]= line.replace(";" , ":").split(":");
				                BlackMagicOutput.timecode1 = Integer.parseInt(str[1].replace(" ", ""));
				                BlackMagicOutput.timecode2 = Integer.parseInt(str[2].replace(" ", ""));
				                BlackMagicOutput.timecode3 = Integer.parseInt(str[3].replace(" ", ""));
				                BlackMagicOutput.timecode4 = Integer.parseInt(str[4].replace(" ", ""));
							}
			            }
			            
			            if (line.contains("timecode") && caseInAndOut.isSelected())
			            {
			            	if (VideoPlayer.caseTcInterne != null) //contourne un bug
			            	{
				            	if (VideoPlayer.caseTcInterne.isSelected())
				            	{
					                String str[]= line.split(":");
				                	timecode1 = str[1].replace(" ", "");
				                	timecode2 = str[2].replace(" ", "");
				                	timecode3 = str[3].replace(" ", "");
				                	timecode4 = str[4].replace(" ", "");
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
			            
				}//While			
				process.waitFor();		
					
				} catch (IOException | InterruptedException e) {
					FFMPEG.error = true;
				} finally {
					isRunning = false;
					btnStart.setEnabled(true);
				}
						
			}//RUN				
		});//THREAD		
		processData.start();
		
	}

	public static void FrameData(final String fichier) {	
		
		if (caseForcerEntrelacement.isSelected() == false)
			entrelaced = null;
		if (caseForcerDesentrelacement.isSelected() == false || caseForcerDesentrelacement.isSelected() && lblTFF.getText().equals("x2"))
			fieldOrder = null;
		
		videoStream = false;
		pixelformat = "";
		imageDepth = 8;		
		timeBase = "";
		HDRmin = (float) 0.01;
		HDRmax = 1000;

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
						processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -show_frames -show_streams -read_intervals %+#1 -loglevel warning -i " + '"' + fichier + '"');
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
						processFFPROBE = new ProcessBuilder("/bin/bash", "-c", PathToFFPROBE + " -i " + '"' + fichier + '"' + " -show_frames -show_streams -read_intervals %+#1 -loglevel warning");
					}	
					
					isRunning = true;	
					Process process = processFFPROBE.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        			        
			        String line;
			        			        
			        Console.consoleFFPROBE.append(System.lineSeparator());
			        
					//Analyse des données	
			        while ((line = br.readLine()) != null) {		
			        	
						Console.consoleFFPROBE.append(line + System.lineSeparator());	
						
						//Erreurs
						if (line.contains("Invalid data found when processing input") 
								|| line.contains("No such file or directory")
								|| line.contains("Invalid data found")
								|| line.contains("No space left")
								|| line.contains("does not contain any stream")
								|| line.contains("Invalid argument"))
						{
							FFMPEG.error = true;
						}
																		
					  if (line.contains("interlaced_frame"))
					  {
						  String interlace = line.substring(line.indexOf("interlaced_frame") + 17);
						  entrelaced = interlace;
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
						          						        		
					}
					
					process.waitFor();					
					
					 if (entrelaced == null)
					  	entrelaced = "0";
					 
					 if (fieldOrder == null)
						 fieldOrder = "0";
								
					} catch (Exception e) {		
						FFMPEG.error = true;
					} finally {
						isRunning = false;
						btnStart.setEnabled(true);
					}
						
			}//RUN				
		});//THREAD		
		processFrameData.start();
	}
	
	public static void AnalyseGOP(final String fichier) {
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
						processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -show_frames -select_streams v:0 -i " + '"' + fichier + '"');
					}
					else
					{
						PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
						PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
						processFFPROBE = new ProcessBuilder("/bin/bash", "-c" , PathToFFPROBE + " -i " + '"' + fichier + '"' + " -select_streams v:0 -show_frames");
					}	
					
					isRunning = true;	
					
					//Attente de l'ouverture de la fenêtre
					do {
						Thread.sleep(100);
					} while(GOP.frame == null);
					
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
				            if (i > 10000 || gopCount > 500)
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
									GOP.newImage('I', gopSpace);		
								    gopSpace += 112;
								    gopCount += 1;
								}
							}
							if (line.equals("pict_type=P")) {
								GOP.newImage('P', gopSpace);
							    gopSpace += 112;
							    gopCount += 1;
							}
							if (line.equals("pict_type=B")) {
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
					}		
						
			}//RUN				

		});//THREAD		
		processGOP.start();
	}
	
	public static boolean FindStreams(final String fichier) {			
		
		try {		
			String PathToFFPROBE;
			ProcessBuilder processFFPROBE;
			if (System.getProperty("os.name").contains("Windows"))
			{						
				PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFPROBE = PathToFFPROBE.substring(1,PathToFFPROBE.length()-1);
				PathToFFPROBE = '"' + PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffprobe.exe" + '"';
				processFFPROBE = new ProcessBuilder(PathToFFPROBE + " -show_streams" + " -i " + '"' + fichier + '"');
			}
			else
			{
				PathToFFPROBE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToFFPROBE = PathToFFPROBE.substring(0,PathToFFPROBE.length()-1);
				PathToFFPROBE = PathToFFPROBE.substring(0,(int) (PathToFFPROBE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffprobe";
				processFFPROBE = new ProcessBuilder("/bin/bash", "-c" , PathToFFPROBE + " -i " + '"' + fichier + '"' + " -show_streams");
			}				
			
			isRunning = true;	
			Process process = processFFPROBE.start();
			
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
							
			//Analyse
			while ((line = input.readLine()) != null) {
		         if (line.contains("Stream"))
		         {
		        	 if (line.contains("Video"))
		        		 return true;					        		 
		        	 else if (line.contains("Audio"))
		        		 return false;
		         }
			}									
			process.waitFor();
			
			} catch (Exception e) {}
			finally{
				isRunning = false;
			}
		
		return false;			

	}
	
	public static void CalculH264() {
		
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
	processCalculH264 = new Thread(new Runnable()  {
		@Override
		public void run() { 
			try {
				calcul = true;
				lblH264.setVisible(true);
				
				if (Shutter.scanIsRunning)
				{
		            File dir = new File(Shutter.liste.firstElement());
		            for (File f : dir.listFiles())
		            {
		            	if (f.isHidden() == false && f.isFile())
		            	{    
		                 	lblH264.setText(f.getName());
		                 	
		    				//Envoi dans Data()
		                 	FFPROBE.Data(f.toString());
		            		break;
		            	}
		            }
				}
				else
				{
					lblH264.setText(new File(liste.getElementAt(0).toString()).getName());
					
					//Envoi dans Data()
					if (inputDeviceIsRunning == false)					
						FFPROBE.Data(liste.getElementAt(0).toString());
				}
				
				//Attente d'analyse
		         do {
						Thread.sleep(100);
		         } while(processData.isAlive());         
		         
         	if (totalLength != 0)
			{           		
    		    if (Shutter.comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
    		    {
    				float debit = (float) ((float) 23000000 / FFPROBE.totalLength) * 8;
    				
    				if (debit > 38)
    					Shutter.debitVideo.setSelectedItem(38000);
    				else
    					Shutter.debitVideo.setSelectedItem((int) debit * 1000);
    		    }	
         		
	        	 NumberFormat formatter = new DecimalFormat("00");
	             int secondes = ((totalLength) / 1000) % 60;
	             int minutes =  ((totalLength) / 60000) % 60;
	             int heures = ((totalLength) / 3600000);
	             
	             textH.setText(formatter.format(heures));
	             textMin.setText(formatter.format(minutes));
	             textSec.setText(formatter.format(secondes));
	             
	             if (caseInAndOut.isSelected() && VideoPlayer.playerLeftVideo != null)	
		     			VideoPlayer.totalDuration();
	             
	             setTailleH264();
			}
         	
		 } catch (Exception e) {}
			finally {
				calcul = false;
				frame.setCursor(Cursor.getDefaultCursor());
			}
		}//Run
	});
	processCalculH264.start();
	
	}
	
	public static void setTailleH264() {
		
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
				
		if (lblVBR.getText().equals("CQ") == false || lblVBR.isVisible() == false)
		{
			if (lock.getIcon().toString().substring(lock.getIcon().toString().lastIndexOf("/") + 1).equals("lock.png"))
			{
				 //Injection du débit
				int h = Integer.parseInt(textH.getText());
				int min = Integer.parseInt(textMin.getText());
				int sec = Integer.parseInt(textSec.getText());
				int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
				int tailleFinale = Integer.parseInt(taille.getText());
				float result = (float) tailleFinale / ((h * 3600) + (min * 60) + sec);
				float resultAudio = (float) (audio*multi) / 8 / 1024;
				float resultatdebit = (result - resultAudio) * 8 * 1024;
				debitVideo.getModel().setSelectedItem((int) resultatdebit);
			}
			else
			{
		        //Injection de la taille
				int h = Integer.parseInt(textH.getText());
				int min = Integer.parseInt(textMin.getText());
				int sec = Integer.parseInt(textSec.getText());
				int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
				int video =  Integer.parseInt(debitVideo.getSelectedItem().toString());
				float resultVideo = (float) video / 8 / 1024;
				float resultAudio =  (float) (audio*multi) / 8 / 1024;
				float resultatdebit = (resultVideo  + resultAudio) * ( (h * 3600)+(min * 60)+sec);
				taille.setText(String.valueOf((int)resultatdebit));	
			}
		}
		else
			taille.setText("-");
		
	}
	
	private static int CalculTemps(String temps) {
		
		String[] time = temps.split(":");
		
		int heures = Integer.parseInt(time[0]);
		int minutes = Integer.parseInt(time[1]);
		int secondes = Integer.parseInt(time[2]);
		int images = (int) (Integer.parseInt(time[3]) * 10);
		
		int totalMiliSecondes = (heures * 3600000) + (minutes * 60000) + (secondes * 1000) + images;  
				
		return totalMiliSecondes;
		
	}
}