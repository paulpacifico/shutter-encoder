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

package functions;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import application.Ftp;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import application.VideoPlayer;
import library.BMXTRANSWRAP;
import library.DVDAUTHOR;
import library.FFMPEG;
import library.FFPROBE;
import library.NCNN;
import library.TSMUXER;
import settings.AdvancedFeatures;
import settings.AudioSettings;
import settings.BitratesAdjustement;
import settings.Colorimetry;
import settings.Corrections;
import settings.FunctionUtils;
import settings.Image;
import settings.ImageSequence;
import settings.InputAndOutput;
import settings.Overlay;
import settings.Timecode;
import settings.Transitions;

/*
 * AV1
 * H.264
 * H.265
 * H.266
 * MPEG-1
 * MPEG-2
 * MJPEG
 * VP8
 * VP9
 * Theora
 * WMV
 * Xvid
 * Blu-ray
 * DVD
 * DV PAL
 * AVC-Intra 100
 * Apple ProRes
 * DNxHD
 * DNxHR
 * FFV1
 * GoPro CineForm
 * HAP
 * QT Animation
 * Uncompressed
 * XAVC
 * XDCAM HD422
 * XDCAM HD 35
 */

public class VideoEncoders extends Shutter {
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{	
					//Render queue only accept selected files
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						boolean isSelected = false;
						
						for (String input : Shutter.fileList.getSelectedValuesList())
						{
							if (liste.getElementAt(i).equals(input))
							{
								isSelected = true;
							}
						}	
						
						if (isSelected == false)
						{
							continue;
						}							
					}
					
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
									
					if (file == null)
						break;
					
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
									
						//Audio
						String audio = "";		
						
						//caseOPATOM
						if (caseOPATOM.isSelected())
						{
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
			            	String audioFiles = "";
			            	
							//Finding video file name
				            if (FFPROBE.FindStreams(file.toString()))
				            	audioFiles = AudioSettings.setAudioFiles(audioFiles, file);
				            else
				            	continue;
				            
				            audio = audioFiles;
				            
				            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
						
						lblCurrentEncoding.setText(fileName);
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;	
												
						//InOut	
						VideoPlayer.getFileList(file.toString());
						InputAndOutput.getInputAndOutput();	
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);
															
						//File output name
						String extensionName = "";	
						if (btnExtension.isSelected())
						{
							extensionName = FunctionUtils.setSuffix(txtExtension.getText(), false);
						}												
						
						//Container
						String container = comboFilter.getSelectedItem().toString();						
						switch (comboFonctions.getSelectedItem().toString())
						{	
							case "DNxHD":
							case "DNxHR":
								
								if (caseCreateOPATOM.isSelected())
								{
									container = ".mxf";
								}
								else
									container = ".mov";	
								
								break;
								
							case "XAVC":
								
								container = ".mxf";								
								break;
							
							case "FFV1":
							case "Blu-ray":
								
								container = ".mkv";								
								break;
							
							case "DVD":
								
								container = ".mpg";								
								break;
							
							case "Theora":
								
								container = ".ogv";								
								break;
								
							case "WMV":
								
								container = ".wmv";								
								break;
								
							case "MPEG-1":
								
								container = ".mpg";								
								break;
								
							case "Xvid":
								
								container = ".avi";								
								break;
							
							case "Apple ProRes":
							case "GoPro CineForm":
							case "HAP":
							case "MJPEG":
							case "QT Animation":
							case "Uncompressed":
							case "DV PAL":
								
								container = ".mov";
								break;
								
						}			
						
						//Split video
						if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
						{
							container = "_%03d" + container;
						}
						
						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, extensionName + container); 
														
						//Authoring folder
						File authoringFolder = null;
						switch (comboFonctions.getSelectedItem().toString())
						{
							case "DVD":
							case "Blu-ray":									
							
								authoringFolder = new File(labelOutput + "/" + fileName.replace(extension, ""));
								authoringFolder.mkdir();
								
								fileOutputName =  authoringFolder.toString().replace("\\", "/") + "/" + fileName.replace(extension, container); 																
								break;
						}	
						
						//File output
						File fileOut = new File(fileOutputName);				
						if (fileOut.exists())		
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, extensionName + "_", container);
							
							if (fileOut == null)
							{
								cancelled = true;
								break;
							}
						}
												
						//Concat mode or Image sequence
						String concat = FunctionUtils.setConcat(file, labelOutput);					
						if (Settings.btnSetBab.isSelected() || (grpImageSequence.isVisible() && caseEnableSequence.isSelected()) || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
						{
							file = new File(labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
						}
						
						//Loop image					
						String loop = FunctionUtils.setLoop(extension);	
						
						//Stream
						String stream = FunctionUtils.setStream();
							
						//Subtitles
						String subtitles = "";
						if (grpBitrate.isVisible())
						{
							subtitles = Overlay.setSubtitles(false, file.toString());
						}
						else if (grpResolution.isVisible())
						{
							switch (comboFonctions.getSelectedItem().toString())
							{
								//Limit to Full HD
								case "AVC-Intra 100":
								case "DNxHD":
								case "XDCAM HD422":
								case "XDCAM HD 35":
									
									subtitles = Overlay.setSubtitles(true, file.toString());
									
									break;
									
								default:
									
									subtitles = Overlay.setSubtitles(false, file.toString());
									
									break;
							}
						}
						else if (comboFonctions.getSelectedItem().toString().equals("DVD"))
						{	
							subtitles = Overlay.setSubtitles(false, file.toString());	
						}
						
						//Interlace
			            String interlace = AdvancedFeatures.setInterlace();	
						
						//Codec
						String codec = setCodec();
						
						//Bitrate
						String bitrate = setBitrate();						

						//Level
						String profile = AdvancedFeatures.setProfile();
						
						//Tune
						String tune = AdvancedFeatures.setTune();
						
						//GOP
						String gop = AdvancedFeatures.setGOP();
						
						//CABAC
						String cabac = AdvancedFeatures.setCABAC();
												
						//Interlace
			            String options = AdvancedFeatures.setOptions();
				        
			            //Used with "options" string
			            if (FunctionUtils.bestBitrateMode == true)
						{
							debitVideo.setSelectedItem(language.getProperty("lblBest").toLowerCase());
						}
			            else if (FunctionUtils.goodBitrateMode == true)
						{
							debitVideo.setSelectedItem(language.getProperty("lblGood").toLowerCase());
						}
			            else if (FunctionUtils.autoBitrateMode == true)
						{
							debitVideo.setSelectedItem("auto");
						}			            
			            
						//Resolution
						String resolution = "";						
						switch (comboFonctions.getSelectedItem().toString())
						{
			            	//Limit to Full HD
							case "AVC-Intra 100":
							case "DNxHD":
							case "XDCAM HD422":
							case "XDCAM HD 35":
								
								resolution = Image.limitToFHD();									
								break;
							
							case "DV PAL":
								
								resolution = " -s 720x576";									
								break;
						}
						
			            //Colorspace
			            String colorspace = Colorimetry.setColorspace();
			            
			            //EXR gamma
						String inputCodec = Colorimetry.setInputCodec(extension);
			        
				        //Deinterlace
						String filterComplex = "";	
						if (comboResolution.getSelectedItem().toString().contains("AI") == false) //Deinterlacing is made before upscaling
						{
							switch (comboFonctions.getSelectedItem().toString())
							{
								case "AV1":
								case "H.264":
								case "H.265":
								case "H.266":
								case "MJPEG":
								case "VP8":
								case "VP9":
								case "Theora":
								case "WMV":
								case "Xvid":
								case "DNxHR":
									
									filterComplex = AdvancedFeatures.setDeinterlace(true);									
									break;
								
								case "MPEG-1":
									
									filterComplex = AdvancedFeatures.setDeinterlace(true);
									break;
									
								case "MPEG-2":
									
									filterComplex = AdvancedFeatures.setDeinterlace(false);								
									break;
								
								case "DNxHD":
									
									switch (comboFilter.getSelectedItem().toString())
						            {
						            	case "36":
						            	case "75":
						            	case "240":
						            	case "365":
						            	case "365 X":
						            	case "90":
						            	case "115":
						            	case "175":
						            	case "175 X":
						            		
						            		filterComplex = AdvancedFeatures.setDeinterlace(true);					            		
					            			break;
					            		
					            		default:
					            			
					            			filterComplex = AdvancedFeatures.setDeinterlace(false);				            			
				            				break;
						            }
									
									break;
									
								case "Apple ProRes":
								case "AVC-Intra 100":
								case "FFV1": 
								case "GoPro CineForm":
								case "HAP":
								case "QT Animation":
								case "Uncompressed":
								case "XAVC":
								case "XDCAM HD422":
								case "XDCAM HD 35":
									
									filterComplex = AdvancedFeatures.setDeinterlace(false);								
									break;
								
								case "Blu-ray":
									
									if (FFPROBE.interlaced.equals("1") && (caseForcerProgressif.isSelected() || comboFilter.getSelectedIndex() == 1)) //H.265
									{
										filterComplex = "yadif=0:" + FFPROBE.fieldOrder + ":0"; 
									}
									break;
									
								case "DVD":
									
									if (FFPROBE.interlaced.equals("1") && caseForcerProgressif.isSelected())
									{
										filterComplex = "yadif=0:" + FFPROBE.fieldOrder + ":0"; 
									}
									
									break;
							}
						}	
												
						//Scaling									
			        	if (setScalingFirst()) //Set scaling before or after depending on using a pad or stretch mode			
			        	{
			        		if (comboResolution.getSelectedItem().toString().contains("AI") && caseEnableCrop.isSelected())
			        		{
			        			//Do nothing
			        		}
			        		else
			        		{
								switch (comboFonctions.getSelectedItem().toString())
								{
									//Limit to Full HD
									case "AVC-Intra 100":
									case "DNxHD":
									case "XDCAM HD422":
									case "XDCAM HD 35":
									case "DVD" : //Needed 16:9 aspect ratio
										
										if (FFPROBE.imageResolution.equals("1440x1080"))
										{
											filterComplex = Image.setScale(filterComplex, false);	
											filterComplex = Image.setPad(filterComplex, false);			
										}
										else
										{
											filterComplex = Image.setScale(filterComplex, true);	
											filterComplex = Image.setPad(filterComplex, true);		
										}
										
										break;
										
									default:
										
										filterComplex = Image.setScale(filterComplex, false);	
										filterComplex = Image.setPad(filterComplex, false);			
										break;
								}	
			        		}
			        	}			        
											
						//Blend
						filterComplex = ImageSequence.setBlend(filterComplex);
						
						//MotionBlur
						filterComplex = ImageSequence.setMotionBlur(filterComplex);
						
						//Stabilisation
						filterComplex = Corrections.setStabilisation(filterComplex, file, fileName, concat);
						
						//LUTs
						filterComplex = Colorimetry.setLUT(filterComplex);
							
						//Levels
						filterComplex = Colorimetry.setLevels(filterComplex);
										
						//Colormatrix
						filterComplex = Colorimetry.setColormatrix(filterComplex);	
						
						//Colorspace metadata
						filterComplex = Colorimetry.setMetadata(filterComplex);

						//Rotate
						filterComplex = Image.setRotate(filterComplex);
						
						//Color
						filterComplex = Colorimetry.setColor(filterComplex);		
						
						//Deflicker
						filterComplex = Corrections.setDeflicker(filterComplex);
						
						//Deband
						filterComplex = Corrections.setDeband(filterComplex);
							 
						//Details
		            	filterComplex = Corrections.setDetails(filterComplex);				
														            	
						//Denoise
			    		filterComplex = Corrections.setDenoiser(filterComplex);
			    		
			    		//Exposure
						filterComplex = Corrections.setSmoothExposure(filterComplex);	
						
						//Decimate
						filterComplex = AdvancedFeatures.setDecimate(filterComplex);
						
						//Interpolation
						filterComplex = AdvancedFeatures.setInterpolation(filterComplex);
						
						//Slow motion
						filterComplex = AdvancedFeatures.setSlowMotion(filterComplex);
											
				        //PTS
						filterComplex = AdvancedFeatures.setPTS(filterComplex);		      		                     	

						//Conform
			    		filterComplex = AdvancedFeatures.setConform(filterComplex);
								
		            	//Logo
				        String logo = Overlay.setLogo();	            			            
										
				    	//Watermark
						filterComplex = Overlay.setWatermark(filterComplex);
						
		            	//Timecode
						filterComplex = Overlay.showTimecode(filterComplex, fileName.replace(extension, ""), false);
				        
				    	//Crop
						if (comboResolution.getSelectedItem().toString().contains("AI") == false) //Cropping is made before upscaling
				        {
							filterComplex = Image.setCrop(filterComplex, file);
				        }
						
						//Zoom
						if (Shutter.sliderZoom.getValue() != 0)
						{		
							filterComplex = Colorimetry.setZoom(filterComplex, true);
						}
												
				        //Scaling									
			        	if (setScalingFirst() == false) //Set scaling before or after depending on using a pad or stretch mode			
			        	{
			        		if (comboResolution.getSelectedItem().toString().contains("AI") && caseEnableCrop.isSelected())
			        		{
			        			//Do nothing
			        		}
			        		else
			        		{
								switch (comboFonctions.getSelectedItem().toString())
								{
									//Limit to Full HD
									case "AVC-Intra 100":
									case "DNxHD":
									case "XDCAM HD422":
									case "XDCAM HD 35":
									case "DVD" : //Needed 16:9 aspect ratio
										
										if (FFPROBE.imageResolution.equals("1440x1080"))
										{
											filterComplex = Image.setScale(filterComplex, false);	
											filterComplex = Image.setPad(filterComplex, false);			
										}
										else
										{
											filterComplex = Image.setScale(filterComplex, true);	
											filterComplex = Image.setPad(filterComplex, true);		
										}
										
										break;
										
									default:
										
										filterComplex = Image.setScale(filterComplex, false);	
										filterComplex = Image.setPad(filterComplex, false);			
										break;
								}	
			        		}
			        	}			    
				        
						//DAR
						filterComplex = Image.setDAR(filterComplex);

						//Overlay
						if (grpBitrate.isVisible())
						{
							filterComplex = Overlay.setOverlay(filterComplex, false);
						}
						else if (grpResolution.isVisible())
						{							
							switch (comboFonctions.getSelectedItem().toString())
							{
								//Limit to Full HD
								case "AVC-Intra 100":
								case "DNxHD":
								case "XDCAM HD422":
								case "XDCAM HD 35":
									
									filterComplex = Overlay.setOverlay(filterComplex, true);									
									break;
									
								default:
									
									filterComplex = Overlay.setOverlay(filterComplex, false);									
									break;
							}							
						}
						else if (comboFonctions.getSelectedItem().toString().equals("DVD"))
						{
							filterComplex = Overlay.setOverlay(filterComplex, false);	 
						}	
						
						//Interlace50p
			            filterComplex = AdvancedFeatures.setInterlace50p(filterComplex);
			            			            
						//Force TFF
						filterComplex = AdvancedFeatures.setForceTFF(filterComplex);																				
						
						//Limiter
						filterComplex = Corrections.setLimiter(filterComplex);
			            
						//Fade-in Fade-out
						filterComplex = Transitions.setVideoFade(filterComplex, false);
										
		            	//Audio
			            if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
			            {
			            	audio = " -c:a pcm_s16le -ar 48000 -map v:0 -map a?";
			            }
			            else
			            	audio = AudioSettings.setAudioMapping(filterComplex, comboAudioCodec.getSelectedItem().toString(), audio, file);
		            
			            //Audio normalization		
			            if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
						{				
				        	if (cancelled)
				        	{
				        		break;
				        	}
				        	else				        	
				        		lblCurrentEncoding.setText(file.getName());										
						}
			            
		            	//filterComplex					
						if (comboFonctions.getSelectedItem().toString().equals("DV PAL"))
						{
							if (filterComplex != "") 
							{
								filterComplex = " -filter_complex " + '"' + filterComplex + '"' + audio;
							}
							else
								filterComplex = audio;
						}
						else 
						{
							switch (comboFonctions.getSelectedItem().toString())
							{
								case "AVC-Intra 100":
								case "XAVC":
								case "XDCAM HD422":
								case "XDCAM HD 35":
									
									filterComplex = FunctionUtils.setFilterComplexBroadcastCodecs(filterComplex, audio);								
									break;
								
								default:
									
									filterComplex = FunctionUtils.setFilterComplex(filterComplex, audio, false);									
									break;
							}
						}	
												
						//Timecode
						String timecode = Timecode.setTimecode();
			            
						//PixelFormat
						String pixelFormat = setPixelFormat(filterComplex);
						
			            //Flags
			    		String flags = AdvancedFeatures.setFlags(fileName);
			    		
						//Metadatas
			    		String metadatas = FunctionUtils.setMetadata();
			    		
			    		//OPATOM
			    		String opatom = AdvancedFeatures.setOPATOM(audio);
						
				       	//Preset
				        String preset = AdvancedFeatures.setPreset();
				        
				        //Framerate
						String frameRate = "";						
						switch (comboFonctions.getSelectedItem().toString())
						{
							case "DNxHD":
							case "DNxHR":
								
								frameRate = AdvancedFeatures.setFramerate(true);								
								break;
							
							case "DV PAL":
								
								frameRate = " -r 25";								
								break;
							
							default:
								
								frameRate = AdvancedFeatures.setFramerate(false);								
								break;							
						}

				        //2pass
				        String pass = BitratesAdjustement.setPass(fileOutputName);
				
						String output = '"' + fileOut.toString() + '"';
						
						if (caseStream.isSelected())
						{
							output = "-flags:v +global_header -f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=flv]" + textStream.getText();
									
							if (caseDisplay.isSelected() && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")) == false)
								output += "|[f=matroska]pipe:1" + '"';
							else
								output += '"';
						}
						else if (caseDisplay.isSelected() && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")) == false)
						{
							switch (comboFonctions.getSelectedItem().toString())
							{
								case "AV1":
								case "H.264":
								case "H.265":
								case "H.266":
								case "MPEG-1":
								case "MPEG-2":
								case "MJPEG":
								case "Theora":
								case "VP8":
								case "VP9":
								case "WMV":
								case "Xvid":
								case "Blu-ray":
								case "DV PAL":
									
									output = "-flags:v +global_header -f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:1" + '"';
									break;
								
								case "AVC-Intra 100":
								case "XAVC":
																	
									output = "-f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=mxf]pipe:1" + '"';
									break;
									
								case "DNxHD":
								case "DNxHR":								
								case "XDCAM HD422":
								case "XDCAM HD 35":
								case "Apple ProRes":
								case "FFV1":
								case "GoPro CineForm":
								case "HAP":
								case "Uncompressed":
									
									output = "-f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:1" + '"';
									break;
							}	
						}
						
						//GPU decoding
						String gpuDecoding = "";						
						if (FFMPEG.isGPUCompatible && (filterComplex.contains("scale_cuda") || filterComplex.contains("scale_qsv") || filterComplex.contains("scale_vt") || filterComplex.contains("scale_vulkan")))
						{
							if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("auto") && Shutter.comboGPUFilter.getSelectedItem().toString().equals("auto"))
							{
								if (FFMPEG.cudaAvailable)
								{
									gpuDecoding = " -hwaccel cuda -hwaccel_output_format cuda";
								}
								else if (FFMPEG.qsvAvailable)
								{
									gpuDecoding = " -hwaccel qsv -hwaccel_output_format qsv";
								}
								else if (FFMPEG.videotoolboxAvailable)
								{
									gpuDecoding = " -hwaccel videotoolbox -hwaccel_output_format videotoolbox_vld";
								}
								else if (FFMPEG.vulkanAvailable)
								{
									gpuDecoding = " -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan";
								}
							}
							else
								gpuDecoding = " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none") + " -hwaccel_output_format " + Shutter.comboGPUFilter.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none");
						}
						else
						{
							gpuDecoding = " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(Shutter.language.getProperty("aucun"), "none");
						}							
						
						//GPU initialization
						if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("VAAPI"))			
						{
							gpuDecoding += " -vaapi_device /dev/dri/renderD128";
						}
						else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Vulkan Video") && (Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan") || FFMPEG.vulkanAvailable == false))			
						{
							gpuDecoding += " -init_hw_device vulkan";
						}						
												
						//GPU filtering
			        	if (filterComplex.contains("hwdownload")) //When GPU scaling is used
			    		{
			    			//Input bitDepth
			    			String bitDepth = "nv12";
			    			if (FFPROBE.imageDepth == 10)
			    			{
			    				bitDepth = "p010";
			    			}	
			    			
			    			//When there is no filter AND it's 8bit only
			    			if (FFPROBE.imageDepth == 8 && filterComplex.contains("format=" + bitDepth + "[out]") && comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && caseColorspace.isSelected() == false)
			    			{
			    				filterComplex = filterComplex.replace(",hwdownload,format=" + bitDepth, "");
			    			}
			    		}
			        	
						//Command
						String cmd = FunctionUtils.silentTrack + opatom + frameRate + resolution + pass + codec + bitrate + preset + profile + tune + gop + cabac + filterComplex + interlace + pixelFormat + colorspace + options + timecode + flags + metadatas + " -y ";
										
						//Screen capture
						if (inputDeviceIsRunning)
						{	
							String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTime());	
		
							if ((liste.getElementAt(0).equals("Capture.current.screen") || System.getProperty("os.name").contains("Mac")) && RecordInputDevice.audioDeviceIndex > 0)
								cmd = cmd.replace("1:v", "2:v").replace("-map v:0", "-map 1:v").replace("0:v", "1:v");	
							
							FFMPEG.run(" " + RecordInputDevice.setInputDevices() + logo + cmd + output.replace("Capture.current", timeStamp).replace("Capture.input", timeStamp));						
							
							fileOut = new File(fileOut.toString().replace("Capture.current", timeStamp).replace("Capture.input", timeStamp));
						}
						else if (comboResolution.getSelectedItem().toString().contains("AI"))
						{		
							File upscaleFolder = new File(lblDestination1.getText() + "/upscale");		
							
							if (upscaleFolder.exists())
							{
								for (File f : upscaleFolder.listFiles()) 
								{
									f.delete();
								}
							}
							else
								upscaleFolder.mkdir();					
							
							String ext = fileOut.getName().substring(fileOut.getName().lastIndexOf("."));
							fileOut = new File(upscaleFolder + "/" + fileOut.getName().replace(ext, "%06d.png"));	
							
							if (grpImageSequence.isVisible() && caseEnableSequence.isSelected())
							{			
								Shutter.btnStart.setText(Shutter.language.getProperty("btnPauseFunction"));
								VideoPlayer.resizeAll();
								
								upscale(new File(liste.getElementAt(0)), fileOut);
							}
							else
							{
								String filter = "";
								
								filter = AdvancedFeatures.setDeinterlace(true);
								filter = Image.setCrop(filter, file);
								
								if (filter != "")
								{
									filter = " -vf " + '"' + filter + '"';
								}
								
								FFMPEG.run(gpuDecoding + InputAndOutput.inPoint + inputCodec + " -i " + '"' + file.toString() + '"' + logo + subtitles + InputAndOutput.outPoint + filter + " -an -y " + '"' + fileOut + '"');
								
								int current = 0;
								do {
	
									if (upscaleFolder.listFiles().length > 0 && current != upscaleFolder.listFiles().length && caseDisplay.isSelected())
									{
										current = upscaleFolder.listFiles().length;
										
										VideoPlayer.frameVideo = ImageIO.read(new File(fileOut.toString().replace("%06d", String.format("%06d", upscaleFolder.listFiles().length))));
										VideoPlayer.player.repaint();
									}
									else
									{
										Thread.sleep(10);
									}
									
								} while (FFMPEG.runProcess.isAlive());
								
								upscale(null, fileOut);
							}
							
							if (grpImageSequence.isVisible() && caseEnableSequence.isSelected())
							{
								PrintWriter writer = new PrintWriter(file, "UTF-8");     
								
								String[] array = new String[upscaleFolder.listFiles().length];
								
								int a = 0;
								for (File f : upscaleFolder.listFiles())
								{
									array[a] = f.toString();
									a++;
								}	
								
								Arrays.sort(array);
								
								for (String s : array)
								{
									writer.println("file '" + s + "'");
								}				
								writer.close();
							}

							cmd = FunctionUtils.silentTrack + opatom + frameRate + resolution + pass + codec + bitrate + preset + profile + tune + gop + cabac + filterComplex.replace("0:a", "1:a").replace("-map a", "-map 1:a") + interlace + pixelFormat + colorspace + options + timecode + flags + metadatas + " -y ";
							
							if (cancelled == false)
							{
								String inputFramerate = " -r " + FFPROBE.currentFPS;
								
								if (FFPROBE.currentFPS == 59.94f)
								{
									inputFramerate = " -r 60000/1001";
								}
								else if (FFPROBE.currentFPS == 29.97f)
								{
									inputFramerate = " -r 30000/1001";
								}
								else if (FFPROBE.currentFPS == 23.98f)
								{
									inputFramerate = " -r 24000/1001";
								}
								
								if (grpImageSequence.isVisible() && caseEnableSequence.isSelected())
								{
									FFMPEG.run(" -safe 0 -f concat -r " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".") + " -i " + '"' + file.toString() + '"' + cmd + output);
								}
								else
									FFMPEG.run(loop + stream + inputFramerate + " -i " + '"' + fileOut + '"' + InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + output);
								
								do {
									Thread.sleep(10);
								} while(FFMPEG.runProcess.isAlive());	
								
								if (grpBitrate.isVisible() && case2pass.isSelected())
								{
									if (grpImageSequence.isVisible() && caseEnableSequence.isSelected())
									{
										FFMPEG.run(" -safe 0 -f concat -r " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".") + " -i " + '"' + file.toString() + '"' + cmd.replace("-pass 1", "-pass 2") + output);
									}
									else
										FFMPEG.run(loop + stream + inputFramerate + " -i " + '"' + fileOut + '"' + InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd.replace("-pass 1", "-pass 2") + output);		
									
									do {
										Thread.sleep(10);
									} while(FFMPEG.runProcess.isAlive());	
								}
							}
							
							for (File f : upscaleFolder.listFiles()) 
							{
								f.delete();
							}
							
							upscaleFolder.delete();	
							
							//IMPORTANT
							fileOut = new File(lblDestination1.getText() + "/" + fileOut.getName().replace("%06d.png", ext));
						}
						else
						{
							FFMPEG.run(gpuDecoding + loop + stream + InputAndOutput.inPoint + inputCodec + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + InputAndOutput.outPoint + cmd + output);		
						}

						do {
							Thread.sleep(100);
						} while (FFMPEG.runProcess.isAlive());
						
						if (grpBitrate.isVisible() && case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && pass !=  "")
						{						
							if (FFMPEG.cancelled == false && comboResolution.getSelectedItem().toString().contains("AI") == false)
							{
								FFMPEG.run(gpuDecoding + loop + stream + InputAndOutput.inPoint + inputCodec + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + InputAndOutput.outPoint + cmd.replace("-pass 1", "-pass 2") + output);	
														
								do {
									Thread.sleep(100);
								} while (FFMPEG.runProcess.isAlive());	
							}
						}
																			
						if (FFMPEG.saveCode == false && cancelled == false && FFMPEG.error == false)
						{
							//HDR
							switch (comboFonctions.getSelectedItem().toString())
							{
								case "DVD":
									
									lblCurrentEncoding.setText(Shutter.language.getProperty("createBurnFiles"));
									makeVOBfiles(authoringFolder, fileOutputName);
									
									break;
									
								case "Blu-ray":									
								
									lblCurrentEncoding.setText(Shutter.language.getProperty("createBurnFiles"));
									makeBDMVfiles(authoringFolder, fileOutputName);		
									
									break;
							}								

							//OPATOM creation
							if (caseCreateOPATOM.isSelected() && lblOPATOM.getText().equals("OP-Atom"))
							{							
								String key = FunctionUtils.getRandomHexString().toUpperCase();
								
								if (btnExtension.isSelected())
									key = txtExtension.getText();
								
								switch (comboFonctions.getSelectedItem().toString())
								{
									case "DNxHD":
									case "XDCAM HD422":
									case "XDCAM HD 35":
										
										lblCurrentEncoding.setText(Shutter.language.getProperty("createOpatomFiles"));
																												
										BMXTRANSWRAP.run("-t avid -p -o " + '"' + labelOutput + "/" + fileName.replace(extension, key) + '"' + " --clip " + '"' + fileName.replace(extension, "") + '"' + " --tape " + '"' + fileName + '"' + " " + '"' + fileOut.toString() + '"');
									
										do
										{
											Thread.sleep(100);
										}
										while (BMXTRANSWRAP.isRunning);
										
										fileOut.delete();
										break;
										
									case "DNxHR":
																		
										fileOut.renameTo(new File(labelOutput + "/" + fileName.replace(extension, key + "_v1.mxf")));			
										break;
								}							
							}
							else if (caseAS10.isSelected())
							{
								switch (comboFonctions.getSelectedItem().toString())
								{
									case "XDCAM HD422":	
									case "XDCAM HD 35":
										
										lblCurrentEncoding.setText(Shutter.language.getProperty("createAS10Format"));
										
										BMXTRANSWRAP.run("-t as10 -p -o " + '"' + labelOutput + "/" + fileName.replace(extension, "_AS10" + comboFilter.getSelectedItem().toString()) + '"' + " --shim-name " + '"' + comboAS10.getSelectedItem().toString() + '"' + " " + '"' + fileOut.toString() + '"');
								
										do
										{
											Thread.sleep(100);
										}
										while(BMXTRANSWRAP.isRunning);
										
										fileOut.delete();
										break;
										
									case "AVC-Intra 100":
										
										lblCurrentEncoding.setText(Shutter.language.getProperty("createAS10Format").replace("10", "11"));
																				
										BMXTRANSWRAP.run("-t as11op1a -p -o " + '"' + labelOutput + "/" + fileName.replace(extension, "_AS11" + comboFilter.getSelectedItem().toString()) + '"' + " " + '"' + fileOut.toString() + '"');
									
										do
										{
											Thread.sleep(100);
										}
										while(BMXTRANSWRAP.isRunning);
										
										fileOut.delete();
										break;
								}
							}
						}
					
						//Removing temporary files
						if (pass != "")
						{						
							final File folder = new File(fileOut.getParent());
							FunctionUtils.listFilesForFolder(fileName.replace(extension, ""), folder);
						}
						
						if (comboFonctions.getSelectedItem().toString().equals("DVD") || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
						{
							final File folder = new File(fileOut.getParent());
							FunctionUtils.listFilesForFolder(null, folder);
							
							if (FFMPEG.saveCode || cancelled || FFMPEG.error)
								FileUtils.deleteDirectory(authoringFolder);	
						}
		
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false 
						|| FFMPEG.saveCode == false && caseEnableSequence.isSelected()
						|| FFMPEG.saveCode == false && Settings.btnSetBab.isSelected()
						|| FFMPEG.saveCode == false && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
						{
							if (lastActions(file, fileName, fileOut, labelOutput))
								break;
						}
					} catch (InterruptedException | IOException e) {
						FFMPEG.error  = true;
					}			
				}
								
				FunctionUtils.OPAtomFolder = null;
				FunctionUtils.silentTrack = "";

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					enfOfFunction();
				}
			}
			
		});
		thread.start();
		
    }

	public static boolean setScalingFirst() {
		
		try {
			
			//Crop need to be before scaling
			if (Shutter.caseEnableCrop.isSelected() || comboResolution.getSelectedItem().toString().contains("AI"))
			{
				FFMPEG.isGPUCompatible = false;
				return false;
			}			
			
			//Set scaling before or after depending on using a pad or stretch mode
			String i[] = FFPROBE.imageResolution.split("x");        
			String o[] = FFPROBE.imageResolution.split("x");
						
			if (comboResolution.getSelectedItem().toString().contains("%"))
			{
				double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
				
				o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * value));
				o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * value));
			}					
			else if (comboResolution.getSelectedItem().toString().contains("x"))
			{
				if (comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 2));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 2));
					}
					else
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 4));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 4));
					}
				}
				else
					o = comboResolution.getSelectedItem().toString().split("x");
			}
			else
				return false;
			
			int iw = Integer.parseInt(i[0]);
	    	int ih = Integer.parseInt(i[1]);          	
	    	int ow = Integer.parseInt(o[0]);
	    	int oh = Integer.parseInt(o[1]);        	
	    	float ir = (float) iw / ih;
	    	float or = (float) ow / oh;
	
	    	//Ratio comparison
	    	if (ir != or 
	    	&& (Shutter.caseAddTimecode.isSelected()
	    	|| Shutter.caseShowTimecode.isSelected()
	    	|| Shutter.caseAddText.isSelected()
	    	|| Shutter.caseShowFileName.isSelected()    	
	    	|| Shutter.caseAddWatermark.isSelected()))
	    	{
	    		FFMPEG.isGPUCompatible = false;
	    		return false;
	    	}
	    	else
	    		return true;
	    	
		}
		catch (Exception e)
    	{
    		FFMPEG.isGPUCompatible = false;
    		return false;
    	}
	
	}
	
	public static String setCodec() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
		
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if (comboAccel.getSelectedItem().equals("Nvidia NVENC"))
					{
						return " -c:v av1_nvenc";	
					}
					else if (comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						return " -c:v av1_qsv";	
					}	
					else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
					{
						return " -c:v av1_amf";
					}
					else if (comboAccel.getSelectedItem().equals("Media Foundation"))
					{
						return " -c:v av1_mf";
					}
					else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						return " -c:v av1_videotoolbox";
					}
				}
		    	else
		        	return " -c:v libsvtav1";
				
			case "Blu-ray":
				
				if (comboFilter.getSelectedIndex() == 0)
				{
					String interlace = "";
					if (FFPROBE.currentFPS != 24.0 && FFPROBE.currentFPS != 23.98 && caseForcerProgressif.isSelected() == false)
			        	interlace = ":tff=1";
					
					String maxrate = "40000";
					
					if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
					{
						maxrate = maximumBitrate.getSelectedItem().toString();
					}
					
					return " -c:v libx264 -pix_fmt yuv420p -tune film -level 4.1 -x264opts bluray-compat=1:force-cfr=1:weightp=0:bframes=3:ref=3:nal-hrd=vbr:vbv-maxrate=" + maxrate + ":vbv-bufsize=30000:bitrate=" + FunctionUtils.setVideoBitrate() + ":keyint=60:b-pyramid=strict:slices=4" + interlace + ":aud=1:colorprim=bt709:transfer=bt709:colormatrix=bt709";		
				}
				else //H.265
				{
					String maxrate = "75000";
					
					if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
					{
						maxrate = maximumBitrate.getSelectedItem().toString();
					}
					
					return " -c:v libx265 -pix_fmt yuv420p10le -profile:v main10 -tune grain -level 5.1 -x265-params keyint=60:bframes=3:vbv-maxrate=" + maxrate + ":vbv-bufsize=75000:bitrate=" + FunctionUtils.setVideoBitrate() + ":colorprim=bt2020:transfer=smpte2084:colormatrix=bt2020nc";		
				
				}

			case "DVD":
				
				if (FFPROBE.currentFPS == 25.0f)
				{
					return " -aspect 16:9 -target pal-dvd -s 720x576";
				}
				else
					return " -aspect 16:9 -target ntsc-dvd -s 720x480";
				
			case "DV PAL":
				
				return " -c:v dvvideo -b:v 25000k -aspect " + comboFilter.getSelectedItem().toString().replace("/", ":");
				
			case "H.264":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if (comboAccel.getSelectedItem().equals("Nvidia NVENC"))
						return " -c:v h264_nvenc -b_ref_mode 0";	
					else if (comboAccel.getSelectedItem().equals("Intel Quick Sync"))
						return " -c:v h264_qsv";	
					else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
						return " -c:v h264_amf";
					else if (comboAccel.getSelectedItem().equals("Vulkan Video"))
						return " -c:v h264_vulkan";
					else if (comboAccel.getSelectedItem().equals("Media Foundation"))
						return " -c:v h264_mf";
					else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
						return " -c:v h264_videotoolbox";
					else if (comboAccel.getSelectedItem().equals("VAAPI"))
						return " -c:v h264_vaapi";	
					else if (comboAccel.getSelectedItem().equals("V4L2 M2M"))
						return " -c:v h264_v4l2m2m";	
					else if (comboAccel.getSelectedItem().equals("OpenMAX"))
						return " -c:v h264_omx";	
				}
				else
					return " -c:v libx264";
				
				break;
			
			case "H.265":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if (comboAccel.getSelectedItem().equals("Nvidia NVENC"))
						return " -c:v hevc_nvenc -b_ref_mode 0";	
					else if (comboAccel.getSelectedItem().equals("Intel Quick Sync"))
						return " -c:v hevc_qsv";	
					else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
						return " -c:v hevc_amf";
					else if (comboAccel.getSelectedItem().equals("Vulkan Video"))
						return " -c:v hevc_vulkan";
					else if (comboAccel.getSelectedItem().equals("Media Foundation"))
						return " -c:v hevc_mf";
					else if (comboAccel.getSelectedItem().equals("D3D12VA"))
						return " -c:v hevc_d3d12va";
					else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						if (caseAlpha.isSelected())
						{
							return " -c:v hevc_videotoolbox -alpha_quality 1";		
						}
						else
							return " -c:v hevc_videotoolbox";
					}
					else if (comboAccel.getSelectedItem().equals("VAAPI"))
						return " -c:v hevc_vaapi";	
				}
				else
					return " -c:v libx265";
				
				break;
				
			case "H.266":
				
				return " -c:v libvvenc";
			
			case "MPEG-1":
				
				return " -c:v mpeg1video";
				
			case "MPEG-2":
				
				return " -c:v mpeg2video";
			
			case "MJPEG":
				
				return " -c:v mjpeg";
				
			case "Theora":
				
				return " -c:v libtheora";
						
			case "VP8":
			
				return " -c:v libvpx -row-mt 1";
			
			case "VP9":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
				{
					return " -c:v vp9_qsv -row-mt 1";
				}
		    	else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("VAAPI"))
		    	{
		    		return " -c:v vp9_vaapi -row-mt 1";
		    	}
		    	else
		        	return " -c:v libvpx-vp9 -row-mt 1";  
				
			case "WMV":
				
				return " -c:v wmv2";
				
			case "Xvid":
				
				return " -c:v libxvid";
				
			case "Apple ProRes":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("OSX VideoToolbox") && System.getProperty("os.name").contains("Mac"))
				{
					switch (comboFilter.getSelectedItem().toString())
					{					
						case "Proxy" :
							return " -c:v prores_videotoolbox -profile:v 0 -pix_fmt yuv422p10";
						case "LT" :
							return " -c:v prores_videotoolbox -profile:v 1 -pix_fmt yuv422p10";
						case "422" :
							return " -c:v prores_videotoolbox -profile:v 2 -pix_fmt yuv422p10";
						case "422 HQ" :
							return " -c:v prores_videotoolbox -profile:v 3 -pix_fmt yuv422p10";
						case "4444" :
							return " -c:v prores_videotoolbox -profile:v 4 -pix_fmt yuv444p10le";
						case "4444 XQ" :
							return " -c:v prores_videotoolbox -profile:v 5 -pix_fmt yuv444p10le";
					}
				}
				else
				{				
					switch (comboFilter.getSelectedItem().toString())
					{					
						case "Proxy" :
							return " -c:v prores -profile:v 0 -pix_fmt yuv422p10";
						case "LT" :
							return " -c:v prores -profile:v 1 -pix_fmt yuv422p10";
						case "422" :
							return " -c:v prores -profile:v 2 -pix_fmt yuv422p10";
						case "422 HQ" :
							return " -c:v prores -profile:v 3 -pix_fmt yuv422p10";
						case "444" :
							return " -c:v prores -profile:v 4 -pix_fmt yuv444p10";
						case "4444" :
							return " -c:v prores_ks -pix_fmt yuva444p10le -alpha_bits 16 -profile:v 4444";
						case "4444 XQ" :
							return " -c:v prores_ks -pix_fmt yuva444p10le -alpha_bits 16 -profile:v 4444xq";
					}
				}
				
				break;

			case "AVC-Intra 100":
				
				return " -shortest -c:v libx264 -coder 0 -g 1 -b:v 100M -tune psnr -preset veryslow -vsync 1 -color_range 2 -avcintra-class 100 -me_method hex -subq 5 -cmp chroma -pix_fmt yuv422p10le";
			
			case "DNxHD":
			case "DNxHR":
				
				return " -c:v dnxhd ";
				
			case "FFV1":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if (comboAccel.getSelectedItem().equals("Vulkan Video"))
						return " -c:v ffv1_vulkan";
				}
				else
					return " -c:v ffv1 -level 3";
				
			case "GoPro CineForm":
				
				String yuv = "yuv422p10";
				if (caseAlpha.isSelected())
					yuv = "gbrap12le";
				
				switch (comboFilter.getSelectedItem().toString())
				{					
					case "Low" :
						return " -c:v cfhd -quality low -pix_fmt " + yuv;
					case "Medium" :
						return " -c:v cfhd -quality medium -pix_fmt " + yuv;
					case "High" :
						return " -c:v cfhd -quality high -pix_fmt " + yuv;
					case "Film Scan" :
						return " -c:v cfhd -quality film1 -pix_fmt " + yuv;
					case "Film Scan 2" :
						return " -c:v cfhd -quality film2 -pix_fmt " + yuv;
					case "Film Scan 3" :
						return " -c:v cfhd -quality film3 -pix_fmt " + yuv;
					case "Film Scan 3+" :
						return " -c:v cfhd -quality film3+ -pix_fmt " + yuv;
				}

				break;
			
			case "HAP":
				
				if (caseChunks.isSelected())
				{
					return " -c:v hap -chunks " + (chunksSize.getSelectedIndex() + 1);
				}
				else
					return " -c:v hap -chunks 4";
				
			case "QT Animation":
				
				return " -c:v qtrle";
				
			case "Uncompressed":
				
				switch (comboFilter.getSelectedItem().toString())
				{		
					case "YUV" :
				
						if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits"))
						{
							return " -c:v v210 -pix_fmt yuv422p10le";
						}
						else						
							return " -c:v rawvideo -pix_fmt uyvy422 -vtag 2vuy";
						
					case "RGB" :						
						
						return " -c:v r210 -pix_fmt gbrp10le";
					
				}
				
				break;
				
			case "XDCAM HD422":
				
				return " -c:v mpeg2video -g 12 -pix_fmt yuv422p -color_range 1 -non_linear_quant 1 -dc 10 -intra_vlc 1 -q:v 2 -qmin 2 -qmax 12 -lmin " + '"' + "1*QP2LAMBDA" + '"' + " -rc_max_vbv_use 1 -rc_min_vbv_use 1 -b:v 50000k -minrate 50000k -maxrate 50000k -bufsize 17825792 -rc_init_occupancy 17825792 -sc_threshold 1000000000 -bf 2";
		
			case "XDCAM HD 35":
				
				return " -c:v mpeg2video -g 12 -pix_fmt yuv420p -color_range 1 -non_linear_quant 1 -dc 10 -intra_vlc 1 -q:v 2 -qmin 2 -qmax 12 -lmin " + '"' + "1*QP2LAMBDA" + '"' + " -rc_max_vbv_use 1 -rc_min_vbv_use 1 -b:v 35000k -minrate 35000k -maxrate 35000k -bufsize 17825792 -rc_init_occupancy 17825792 -sc_threshold 1000000000 -bf 2";
								
			case "XAVC":
				
				return " -shortest -c:v libx264 -me_method tesa -subq 9 -partitions all -direct-pred auto -psy 0 -b:v " + comboFilter.getSelectedItem().toString() + "M -bufsize " + comboFilter.getSelectedItem().toString() + "M -level 5.1 -g 0 -keyint_min 0 -x264opts filler -x264opts colorprim=bt709 -x264opts transfer=bt709 -x264opts colormatrix=bt709 -x264opts force-cfr -preset superfast -tune fastdecode -pix_fmt yuv422p10le";
		}
		
		return "";		
	}

	private static String setPixelFormat(String filterComplex) {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
			case "H.264":
			case "H.265":
			case "H.266":
			case "VP9":
				
				String yuv = "yuv";
				
				if (caseAlpha.isSelected())
		        {
		        	yuv += "a";
		        }
				
		        if (caseForceOutput.isSelected() && lblNiveaux.getText().equals("0-255"))
		        {
		        	yuv += "j";
		        }		

		        if (caseForceLevel.isSelected())
		        {
		        	if (comboForceProfile.getSelectedItem().toString().contains("422"))
		        		yuv += "422p";
		        	else if (comboForceProfile.getSelectedItem().toString().contains("444"))
		        		yuv += "444p";
		        	else
		            	yuv += "420p";
		        }
		        else
		        	yuv += "420p";
		        
				if (caseColorspace.isSelected())
				{
					if (comboColorspace.getSelectedItem().toString().contains("10bits"))
					{
						yuv += "10le";
					}
					else if (comboColorspace.getSelectedItem().toString().contains("12bits"))
					{
						yuv += "12le";
					}
				}
				else
				{
					//Switching to GPU nv12 or p010 to avoid useless pix_fmt conversion
					if (FFMPEG.isGPUCompatible && (filterComplex.contains("scale_cuda") || filterComplex.contains("scale_qsv") || filterComplex.contains("scale_vt") || filterComplex.contains("scale_vulkan")))
					{
						if (filterComplex.contains("format=p010"))
						{
							return " -pix_fmt " + yuv;
						}
						else
							return "";				
					}
					else if ("H.266".equals(comboFonctions.getSelectedItem().toString()))
			        {
						return " -pix_fmt yuv420p10le";
			        }
					else						
						return " -pix_fmt " + yuv;
				}
				
				return " -pix_fmt " + yuv;
			
			case "VP8":

				if (caseAlpha.isSelected())
		        {
					return " -auto-alt-ref 0 -pix_fmt yuva420p";
		        }
				else				
				{
					//Switching to GPU nv12 to avoid useless pix_fmt conversion
					if (caseColorspace.isSelected() == false && FFMPEG.isGPUCompatible && (filterComplex.contains("scale_cuda") || filterComplex.contains("scale_qsv") || filterComplex.contains("scale_vt") || filterComplex.contains("scale_vulkan")))
					{
						return "";				
					}
					else
						return " -pix_fmt yuv420p";
				}
		        
			case "MPEG-2":
				
				if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("4:2:2"))
				{
					return " -pix_fmt yuv422p";
				}
				
			case "MPEG-1":
			case "Theora":
			case "WMV":
			case "Xvid":
				
				//Switching to GPU nv12 to avoid useless pix_fmt conversion
				if (caseColorspace.isSelected() == false && FFMPEG.isGPUCompatible && (filterComplex.contains("scale_cuda") || filterComplex.contains("scale_qsv") || filterComplex.contains("scale_vt") || filterComplex.contains("scale_vulkan")))
				{
					return "";				
				}
				else		        
					return " -pix_fmt yuv420p";
				
			case "MJPEG":
				
				return " -pix_fmt yuvj422p";
				
			//The other cases are managed from setCodec
		}
		
		return "";
	}
	
	public static String setBitrate() {
		
		FunctionUtils.setVideoBitrate();
				
		switch (comboFonctions.getSelectedItem().toString())
		{
		
			case "AV1":
															
				if (lblVBR.getText().equals("CQ"))
		        {
					String limitedBitrate = "";
					
					int maxrate = FunctionUtils.setVideoBitrate();		
					if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
					{
						maxrate = Integer.parseInt(maximumBitrate.getSelectedItem().toString());
						limitedBitrate = " -maxrate " + maxrate + "k -bufsize " + Integer.valueOf((int) (maxrate * 2)) + "k";
					}
					
					String gpu = "";
					if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
					{
						gpu = " -qp " + FunctionUtils.setVideoBitrate();
					}
					else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						gpu = " -global_quality " + FunctionUtils.setVideoBitrate();
					}
						
		    		return " -crf " + FunctionUtils.setVideoBitrate() + gpu + limitedBitrate;         
		        }
		        else
		        	return " -b:v " + FunctionUtils.setVideoBitrate() + "k";

			case "DVD":
				
				float bitrate = (float) ((float) 4000000 / FFPROBE.totalLength) * 8;
				if (VideoPlayer.playerInMark > 0 || VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
				{
					float totalIn =  Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * VideoPlayer.inputFramerateMS;
					float totalOut = Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * VideoPlayer.inputFramerateMS;
					
					float sommeTotal = totalOut - totalIn;
					
					bitrate = (float) ((float) 4000000 / sommeTotal) * 8;
				}
				
				NumberFormat formatter = new DecimalFormat("0000");
				
				if (bitrate > 8)
				{
					BitratesAdjustement.DVDBitrate = 8000;
					return " -b:v 8000k";
				}
				else
				{
					BitratesAdjustement.DVDBitrate = Integer.parseInt(formatter.format(bitrate * 1000));
					return " -b:v " + Integer.parseInt(formatter.format(bitrate * 1000)) + "k";
				}
				
			case "H.264":
			case "H.265":
			case "H.266":
							
				String limitedBitrate = "";
				
				int maxrate = FunctionUtils.setVideoBitrate();		
				if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
				{
					maxrate = Integer.parseInt(maximumBitrate.getSelectedItem().toString());
				}
				
				if (lblVBR.getText().equals("CBR"))
		        {					
					limitedBitrate = " -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + maxrate + "k -bufsize " + Integer.valueOf((int) (maxrate * 2)) + "k";
		        }	
				else if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
				{
					limitedBitrate = " -maxrate " + maxrate + "k -bufsize " + Integer.valueOf((int) (maxrate * 2)) + "k";
				}
				
				if (lblVBR.getText().equals("CQ"))
		        {
		    		String gpu = "";
					if ("H.266".equals(comboFonctions.getSelectedItem().toString()) || (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC")))
					{
						gpu = " -qp " + FunctionUtils.setVideoBitrate();
					}
					else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						gpu = " -global_quality " + FunctionUtils.setVideoBitrate();
					}
					else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
					{
						gpu = " -qp_i " + FunctionUtils.setVideoBitrate() + " -qp_p " + FunctionUtils.setVideoBitrate() + " -qp_b " + FunctionUtils.setVideoBitrate();        			
					}
					else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						gpu = " -q:v " + (31 - (int) Math.ceil((FunctionUtils.setVideoBitrate() * 31) / 51));
					}
						
		    		return " -crf " + FunctionUtils.setVideoBitrate() + gpu + limitedBitrate;          
		        }
				else if (lblVBR.getText().equals("CBR") && comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
		        {
		        	return " -b:v " + FunctionUtils.setVideoBitrate() + "k -rc cbr";
		        }
		        else
		        {		        	
		        	return " -b:v " + FunctionUtils.setVideoBitrate() + "k" + limitedBitrate;
		        }
				
			case "VP8":
			case "VP9":
				
				if (lblVBR.getText().equals("CQ"))
		        {
		        	return " -crf " + FunctionUtils.setVideoBitrate();   
		        }
		        else
		        	return " -b:v " + FunctionUtils.setVideoBitrate() + "k";
				
			case "MPEG-1":
			case "MPEG-2":
			case "MJPEG":
			case "Theora":
			case "WMV":
			case "Xvid":
				
			       return " -b:v " + FunctionUtils.setVideoBitrate() + "k";
				
			case "DNxHD":
				
				if (comboFilter.getSelectedItem().toString().contains("X"))
				{
					return " -b:v " + comboFilter.getSelectedItem().toString().replace(" X", "") + "M -pix_fmt yuv422p10";
				}
				else
					return " -b:v " + comboFilter.getSelectedItem().toString() + "M -pix_fmt yuv422p";
				
			case "DNxHR":	
				
				if (comboFilter.getSelectedItem().toString().equals("HQX"))
				{
					return " -profile:v dnxhr_" + comboFilter.getSelectedItem().toString().toLowerCase() + " -pix_fmt yuv422p10";
				}
				else if (comboFilter.getSelectedItem().toString().equals("444"))
				{
					return " -profile:v dnxhr_" + comboFilter.getSelectedItem().toString().toLowerCase() + " -pix_fmt yuv444p10";
				}
				else
					return " -profile:v dnxhr_" + comboFilter.getSelectedItem().toString().toLowerCase() + " -pix_fmt yuv422p";

			case "HAP":
				
				if (comboFilter.getSelectedItem().equals("Alpha"))
				{
					return " -format hap_alpha";
				}
				else if (comboFilter.getSelectedItem().equals("Q"))
				{
					return " -format hap_q";
				}

				break;
		}
		
		return "";
	}	
				
	private static void makeVOBfiles(File dvdFolder, String MPEGFile) throws IOException, InterruptedException {
		
		String PathToDvdXml = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows"))
			PathToDvdXml = PathToDvdXml.substring(1,PathToDvdXml.length()-1);
		else
			PathToDvdXml = PathToDvdXml.substring(0,PathToDvdXml.length()-1);
		
		PathToDvdXml = PathToDvdXml.substring(0,(int) (PathToDvdXml.lastIndexOf("/"))).replace("%20", " ") + "/Library/dvd.xml"; //Old XML
		String copyXML = dvdFolder.toString() + "/dvd.xml"; //New XML

		FileReader reader = new FileReader(PathToDvdXml);			
		BufferedReader oldXML = new BufferedReader(reader);
		
		FileWriter writer = new FileWriter(copyXML);
		
		String line;
		while ((line = oldXML.readLine()) != null)
		{
			if (FFPROBE.currentFPS != 25.0f && line.contains("pal"))
			{
				writer.write(line.replace("pal", "ntsc").replace("720x576", "720x480") + System.lineSeparator());
			}
			else if (line.contains("path"))
			{
				writer.write(line.replace("path", MPEGFile) + System.lineSeparator());
			}
			else					
				writer.write(line + System.lineSeparator());
		}
		
		reader.close();
		oldXML.close();
		writer.close();		
			
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				DVDAUTHOR.run("-o " + '"' + dvdFolder.toString() + "/" + '"' + " -x " + '"' + dvdFolder.toString() + "/dvd.xml" + '"');
			else
				DVDAUTHOR.run("-o " + '"' + dvdFolder.toString() + '"' + " -x " + '"' + dvdFolder.toString() + "/dvd.xml" + '"');	
				
            do {
            	Thread.sleep(100);
            } while (DVDAUTHOR.isRunning);
	}
		
	private static void makeBDMVfiles(File blurayFolder, String MKVfile) throws IOException, InterruptedException {
		
		String PathToblurayMeta = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows"))
			PathToblurayMeta = PathToblurayMeta.substring(1,PathToblurayMeta.length()-1);
		else
			PathToblurayMeta = PathToblurayMeta.substring(0,PathToblurayMeta.length()-1);
		
		PathToblurayMeta = PathToblurayMeta.substring(0,(int) (PathToblurayMeta.lastIndexOf("/"))).replace("%20", " ") + "/Library/bluray.meta"; //Old meta
		String copymeta = blurayFolder.toString() + "/bluray.meta"; //New meta

		FileReader reader = new FileReader(PathToblurayMeta);			
		BufferedReader oldmeta = new BufferedReader(reader);
		
		FileWriter writer = new FileWriter(copymeta);
		
		String line;
		while ((line = oldmeta.readLine()) != null)
		{
			if (line.contains("file"))
				writer.write(line.replace("file", MKVfile) + System.lineSeparator());
			else					
				writer.write(line + System.lineSeparator());
		}
		
			reader.close();
			oldmeta.close();
			writer.close();		
			
			TSMUXER.run('"' + blurayFolder.toString().replace("\\", "/") + "/bluray.meta" + '"' + " " + '"' + blurayFolder.toString().replace("\\", "/") + '"');	
				
            do {
            	Thread.sleep(100);
            } while (TSMUXER.isRunning);
}
	
	private static void upscale(File inputFile, File fileOut) throws InterruptedException {
				
		progressBar1.setValue(0);
		progressBar1.setMaximum(fileOut.getParentFile().listFiles().length);
		
		String model = "realesr-general-wdn-x4v3";							
		if (Shutter.comboResolution.getSelectedItem().toString().contains("animation"))
		{
			model = "realesrgan-x4plus-anime";
		}	
		
		if (inputFile != null) //Image sequence
		{
			NCNN.run(" -v -i " + '"' + inputFile.getParentFile() + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut.getParentFile() + '"', false);
		}
		else
			NCNN.run(" -v -i " + '"' + fileOut.getParentFile() + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut.getParentFile() + '"', false);
		
		do {
			Thread.sleep(100);
		} while (NCNN.isRunning && cancelled == false);			
	}
	
	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;

		//Sending processes
		FunctionUtils.addFileForMail(fileName);		
		Ftp.sendToFtp(fileOut);
		FunctionUtils.copyFile(fileOut);
		
		//Image sequence and merge
		if (caseEnableSequence.isSelected() || Settings.btnSetBab.isSelected())
			return true;
				
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			VideoEncoders.main();			
			return true;
		}
		
		return false;		
	}

}