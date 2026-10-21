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

package shutterencoder.functions.settings;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import shutterencoder.functions.VideoEncoders;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.subtitling.SubtitlesEmbed;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;

public class Overlay extends Main {

	public static String showTimecode(String filterComplex, String file, boolean videoPlayerCapture) {
				
		if ((Main.caseAddTimecode.isSelected() || Main.caseShowTimecode.isSelected() || Main.caseAddText.isSelected() || Main.caseShowFileName.isSelected()))
		{			
			//IMPORTANT RESET SCALING
			FFPROBE.analyzedMedia = null;
			
			if (VideoPlayerCore.videoPath == null || lblCurrentEncoding.getText().equals(new File(VideoPlayerCore.videoPath).getName()) == false) //Do not process scaling if it's the first item
			{		
				//IMPORTANT RESET SCALING
				VideoPlayerCore.videoPath = null;
				
				float scale = 0.0f;
				if (FFPROBE.previousImageWidth > 0)	
				{
					scale = ((float) FFPROBE.imageWidth / FFPROBE.previousImageWidth);

					if (scale != 0.0f)
					{
						//Display timecode
						if (Main.caseShowTimecode.isSelected() || Main.caseAddTimecode.isSelected())
						{
							Main.textTcSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textTcSize.getText()) * scale)));							
						}
						
						//Display text
						if (Main.caseShowFileName.isSelected() || Main.caseAddText.isSelected())
						{
							Main.textNameSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textNameSize.getText()) * scale)));
						}

						VideoPlayerUI.resizeAll();
					}
				}
			}
			
			String tc1 = FFPROBE.timecode1;
			String tc2 = FFPROBE.timecode2;
			String tc3 = FFPROBE.timecode3;
			String tc4 = FFPROBE.timecode4;
						 
			if (Main.caseAddTimecode.isSelected() && Main.TC1.getText().isEmpty() == false && Main.TC2.getText().isEmpty() == false && Main.TC3.getText().isEmpty() == false && Main.TC4.getText().isEmpty() == false)
			{			
				tc1 = Main.TC1.getText();
				tc2 = Main.TC2.getText();			
				tc3 = Main.TC3.getText();		    
				tc4 = Main.TC4.getText();			
			}
			else if (Main.caseShowTimecode.isSelected() && FFPROBE.timecode1.equals("") == false)
			{							
				float tcH = Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS;
				float tcM = Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS;
				float tcS = Integer.parseInt(tc3) * FFPROBE.currentFPS;
				
				float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayerUI.caseInF.getText());

				float offset = timeIn + tcH + tcM + tcS + Integer.parseInt(tc4);

				if (offset < 0)
					offset = 0;
				
				DecimalFormat formatter = new DecimalFormat("00");	
				
				tc1 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 3600));
				tc2 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 60) % 60);
				tc3 = formatter.format(Math.floor(offset / FFPROBE.currentFPS) % 60);    		
				tc4 = formatter.format(Math.round(offset % FFPROBE.currentFPS));
			}	
			
			if (videoPlayerCapture)
			{		
				if (Main.caseShowTimecode.isSelected())
				{
					tc1 = FFPROBE.timecode1;
					tc2 = FFPROBE.timecode2;
					tc3 = FFPROBE.timecode3;
					tc4 = FFPROBE.timecode4;
				}
				
				DecimalFormat formatter = new DecimalFormat("00");					
												
				float tcH = Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS;
				float tcM = Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS;
				float tcS = Integer.parseInt(tc3) * FFPROBE.currentFPS;
				
				float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayerUI.caseInF.getText());

				//NTSC framerate
				if (Main.caseShowTimecode.isSelected())
				{
					timeIn = 0;
				}
												
				double currentTime = Timecode.setNTSCtimecode(VideoPlayerCore.playerCurrentFrame);
				double offset = (currentTime - timeIn) + tcH + tcM + tcS + Integer.parseInt(tc4);

				if (offset < 0)
					offset = 0;
				
				tc1 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 3600));
				tc2 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 60) % 60);
				tc3 = formatter.format(Math.floor(offset / FFPROBE.currentFPS) % 60);    		
				tc4 = formatter.format(Math.floor(offset % FFPROBE.currentFPS));
			}
			
			String rate = String.valueOf(FFPROBE.currentFPS);
			if (caseConform.isSelected())
			{
				rate = comboFPS.getSelectedItem().toString().replace(",", ".");
			}
			else if (caseForcerDesentrelacement.isSelected() && lblTFF.getText().contains("x2"))
			{
				rate = String.valueOf(FFPROBE.currentFPS * 2);
			}
			
			String overlayFont = "fontfile='" + findFont(comboOverlayFont.getSelectedItem().toString()) + "'";
	      
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
			    overlayFont = overlayFont.replace('\\', '/').replace(":", "\\:");
			}
			
			float imageRatio = 1.0f;
			
			int ow = FFPROBE.imageWidth;  
			
			if (comboFonctions.getSelectedItem().toString().equals("DVD") == false && comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
			{
				if (comboResolution.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
					
					ow = (int) Math.round(ow * value);
				}					
				else if (comboResolution.getSelectedItem().toString().contains("x"))
				{
					if (comboResolution.getSelectedItem().toString().contains("AI"))
					{
						if (Main.comboResolution.getSelectedItem().toString().contains("2x"))
						{
							ow = (int) Math.round(ow * 2);
						}
						else
						{
							ow = (int) Math.round(ow * 4);
						}
					}
					else
					{
						String s[] = comboResolution.getSelectedItem().toString().split("x");					
			        	ow = Integer.parseInt(s[0]); 
					}
				}
				else if (comboResolution.getSelectedItem().toString().contains(":"))
				{
					String s[] = comboResolution.getSelectedItem().toString().split(":");
					
					if (s[0].equals("auto"))
					{
						ow = Math.round(FFPROBE.imageHeight / Integer.parseInt(s[1]));
					}
					else if (s[1].equals("auto"))
					{
						ow = Math.round(FFPROBE.imageWidth / Integer.parseInt(s[0]));
					}
					else //ratio like 1:4 etc...
					{
						ow = Integer.parseInt(s[1]);
					}
				}

	        	if (VideoEncoders.setScalingFirst())
				{
	        		imageRatio = (float) FFPROBE.imageWidth / ow;
				}
			}
			
	      	if (Main.caseShowFileName.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + file + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Main.textNamePosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Main.textNamePosY.getText()) / imageRatio) + ":fontcolor=0x" + Main.foregroundHex + Main.foregroundNameAlpha + ":fontsize=" + Math.round(Integer.parseInt(Main.textNameSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Main.backgroundHex + Main.backgroundNameAlpha;
	      	}
	      	
	      	if (Main.caseAddText.isSelected())
	      	{
	      		String text = FunctionUtils.setPrefixSuffix(Main.overlayText.getText(), true);						
					      		
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + text + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Main.textNamePosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Main.textNamePosY.getText()) / imageRatio) + ":fontcolor=0x" + Main.foregroundHex + Main.foregroundNameAlpha + ":fontsize=" + Math.round(Integer.parseInt(Main.textNameSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Main.backgroundHex + Main.backgroundNameAlpha;
	      	}
	      	
		   	if ((Main.caseAddTimecode.isSelected() || Main.caseShowTimecode.isSelected()))
		   	{
		   		String dropFrame = ":";
		   		if (FFPROBE.dropFrameTC.equals(":") == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f))
		   		{
		   			dropFrame = ";";
		   		}
		        
		   		if (Main.caseConform.isSelected())
		   		{
		   			if (Main.comboFPS.getSelectedItem().toString().equals("29,97") || Main.comboFPS.getSelectedItem().toString().equals("59,94"))
		   			{
		   				dropFrame = ";";
		   			}
		   			else 
		   				dropFrame = ":";
		   		}
		   			
		   		if (filterComplex != "") filterComplex += ",";
		   		
		   		if (Main.caseAddTimecode.isSelected() && Main.lblTimecode.getText().equals(Main.language.getProperty("lblFrameNumber")))
		   		{
		   			String startNumber = String.format("%.0f", Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS + Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS + Integer.parseInt(tc3) * FFPROBE.currentFPS + Integer.parseInt(tc4));
		   			filterComplex += "drawtext=" + overlayFont + ":text='%{frame_num}': start_number=" + startNumber + ":x=" + Math.round(Integer.parseInt(Main.textTcPosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Main.textTcPosY.getText()) / imageRatio) + ":fontcolor=0x" + Main.foregroundHex + Main.foregroundTcAlpha + ":fontsize=" + Math.round(Integer.parseInt(Main.textTcSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Main.backgroundHex + Main.backgroundTcAlpha;
		   		}
		   		else
		   			filterComplex += "drawtext=" + overlayFont + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\" + dropFrame + tc4 + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Main.textTcPosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Main.textTcPosY.getText()) / imageRatio) + ":fontcolor=0x" + Main.foregroundHex + Main.foregroundTcAlpha + ":fontsize=" + Math.round(Integer.parseInt(Main.textTcSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Main.backgroundHex + Main.backgroundTcAlpha + ":tc24hmax=1";	      
		   	}   
		}
		
		return filterComplex;
	}

	public static String setSubtitles(boolean limitToFHD, String file) {
				
		//Auto add subs
		if (caseAddSubtitles.isSelected())
		{
			caseAddSubtitles.setEnabled(true);
			Main.fileList.setSelectedValue(file, true);
			
			if (Main.subtitlesBurn)
			{
				autoBurn = true;
			}
			else
				autoEmbed = true;
			
			String ext = file.substring(file.lastIndexOf("."));
											
			if (new File(file.replace(ext, ".srt")).exists()
			|| new File (file.replace(ext, ".vtt")).exists()
			|| new File (file.replace(ext, ".ass")).exists()
			|| new File (file.replace(ext, ".ssa")).exists()
			|| new File (file.replace(ext, ".scc")).exists()
			|| comboSubsSource.getSelectedIndex() != 0)
			{
				FunctionUtils.addSubtitles(false);
				if (VideoPlayerUtils.loadImageThread != null)
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException e) {
					    Thread.currentThread().interrupt();
					}
				}
				FunctionUtils.addSubtitles(true);
			}
									
			autoBurn = false;
			autoEmbed = false;
			caseAddSubtitles.setEnabled(false);
		
			try {
				FFMPEG.runProcess.join();
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
		}
		
    	if (caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    	
    		String subtitlesPath = Main.subtitlesFile.toString();
			if (System.getProperty("os.name").contains("Windows"))
			{
				subtitlesPath = Main.subtitlesFile.getName(); //Only use the name because of the workingDir from the process
			}
    		
			if (subtitlesFile.toString().substring(subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
    		{	
				String background = "" ;
				if (Main.lblSubsBackground.getText().equals(Main.language.getProperty("lblBackgroundOn")))
					background = ",BorderStyle=4,BackColour=&H" + Main.subsAlpha + Main.subsHex2 + "&,Outline=0";
				else
					background = ",Outline=" + Main.outline + ",OutlineColour=&H" + Main.subsAlpha + Main.subsHex2 + "&";
				
				//Bold
				if (Main.btnG.getForeground() != Color.BLACK)
					background += ",Bold=1";
				
				//Italic
				if (Main.btnI.getForeground() != Color.BLACK)
					background += ",Italic=1";
				
				String i[] = FFPROBE.imageResolution.split("x"); 
				
				//Set the input seeking
				float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayerUI.caseInF.getText());			
				VideoPlayerOverlay.writeCurrentSubs(timeIn, false);
				
	            //Delete created .srt file
	            if (comboSubsSource.getSelectedIndex() != 0)
	            {
	            	subtitlesFilePath.delete();
	            }
				
				if (limitToFHD || comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = "1920x1080".split("x");					
					if (comboResolution.getSelectedItem().toString().contains("%"))
					{
						double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
						
						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * value));
						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * value));
					}					
					else if (comboResolution.getSelectedItem().toString().contains("x"))				
					{
						s = comboResolution.getSelectedItem().toString().split("x");
					}
					
					int iw = Integer.parseInt(i[0]);
					int ih = Integer.parseInt(i[1]);
					int ow = Integer.parseInt(s[0]);
					int oh = Integer.parseInt(s[1]);  
					
					int width = Math.round((float) Integer.parseInt(Main.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
					int height = Math.round((float) (ih + Integer.parseInt(Main.textSubtitlesPosition.getText())) / ((float) ih/oh));
															
					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesPath + "':alpha=1:force_style='FontName=" + Main.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Main.textSubsSize.getText() + ",PrimaryColour=&H" + Main.subsHex + "&" + background + "'" + '"';
				}		
				else
				{					
					int fontSize = Integer.parseInt(Main.textSubsSize.getText());
					int height = Integer.parseInt(Main.textSubtitlesPosition.getText());
					
					if (Main.caseEnableCrop.isSelected())
					{
						int ih = Integer.parseInt(i[1]);
						int oh = Integer.parseInt(Main.textCropHeight.getText());
						height -= Integer.parseInt(Main.textCropPosY.getText());
						fontSize = (int) ((float) fontSize * ((float) ih/oh));
					}
										
					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + Main.textSubsWidth.getText() + ":" + i[1] + "+" + height + ",subtitles=" + "'" + subtitlesPath + "':alpha=1:force_style='FontName=" + Main.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + fontSize + ",PrimaryColour=&H" + Main.subsHex + "&" + background + "'" + '"';		
				}
			}
			else // ASS or SSA
			{
				String i[] = FFPROBE.imageResolution.split("x");
				Main.textSubsWidth.setText(i[0]); //IMPORTANT
				
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = FFPROBE.imageResolution.split("x");	
					
					if (comboResolution.getSelectedItem().toString().contains("%"))
					{
						double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
						
						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * value));
						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * value));
					}					
					else					
					{
						if (comboResolution.getSelectedItem().toString().contains("AI"))
	    				{
	    					if (Main.comboResolution.getSelectedItem().toString().contains("2x"))
	    					{
	    						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * 2));
	    						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * 2));
	    					}
	    					else
	    					{
	    						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * 4));
	    						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * 4));
	    					}
	    				}
	    				else
	    					s = comboResolution.getSelectedItem().toString().split("x");
					}
					
		        	int iw = Integer.parseInt(i[0]);
		        	int ih = Integer.parseInt(i[1]);
		        	int ow = Integer.parseInt(s[0]);
		        	int oh = Integer.parseInt(s[1]);        	
		        	
		        	int width = Math.round((float) Integer.parseInt(Main.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
		        	int height = Math.round((float) (ih + Integer.parseInt(Main.textSubtitlesPosition.getText())) / ((float) ih/oh));
		        	
		        	return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesPath + "':alpha=1" + '"';
				}
				else
					return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + i[0] + ":" + i[1] + ",subtitles=" + "'" + subtitlesPath + "':alpha=1" + '"';
			}
		}
		else if (caseAddSubtitles.isSelected() && subtitlesBurn == false)
    	{	
			String subsFiles = "";
			for (Component c : SubtitlesEmbed.frame.getContentPane().getComponents())
			{			
				if (c instanceof JTextField)
				{
					if (((JTextField) c).getText().equals(language.getProperty("aucun")) == false)
					{
						subsFiles += InputAndOutput.inPoint + " -i " + '"' +  ((JTextField) c).getText() + '"';
					}
				}
			}
			
    		return subsFiles;
    	}
	
    	return "";
	}

 	public static String setLogo() {
		
		if (Main.caseAddWatermark.isSelected() && Main.overlayDeviceIsRunning)
		{
			return " " + RecordInputDevice.setOverlayDevice(); 
		}
		else if (Main.caseAddWatermark.isSelected())
		{
			return " -i " + '"' + Main.logoFile + '"'; 
		}
		
		return "";
	}
	
	public static String setWatermark(String filterComplex) {
		
		if (Main.caseAddWatermark.isSelected())
	    {			
			//IMPORTANT RESET SCALING
			FFPROBE.analyzedMedia = null;
			
			if (VideoPlayerCore.videoPath == null || lblCurrentEncoding.getText().equals(new File(VideoPlayerCore.videoPath).getName()) == false) //Do not process scaling if it's the first item
			{		
				//IMPORTANT RESET SCALING
				VideoPlayerCore.videoPath = null;
								
				float scale = 0.0f;
				if (FFPROBE.previousImageWidth > 0)	
				{
					scale = ((float) FFPROBE.imageWidth / FFPROBE.previousImageWidth);
					
					if (scale != 0.0f)
					{			
						Main.textWatermarkSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textWatermarkSize.getText()) * scale)));
					}
				}
				
				VideoPlayerUI.resizeAll();
				
				VideoPlayerOverlay.loadWatermark(Integer.parseInt(Main.textWatermarkSize.getText()));
				if (Main.watermarkPreset != null)
				{
					Main.logo.setLocation((int) Math.floor(Integer.valueOf(Main.textWatermarkPosX.getText()) / Main.playerRatio), (int) Math.floor(Integer.valueOf(Main.textWatermarkPosY.getText()) / Main.playerRatio));
				}
			}
			
			float imageRatio = 1.0f;
			
			int ow = FFPROBE.imageWidth;  
			
			if (comboFonctions.getSelectedItem().toString().equals("DVD") == false && comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
			{
				if (comboResolution.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
					
					ow = (int) Math.round(ow * value);
				}					
				else if (comboResolution.getSelectedItem().toString().contains("x"))
				{
					if (comboResolution.getSelectedItem().toString().contains("AI"))
					{
						if (Main.comboResolution.getSelectedItem().toString().contains("2x"))
						{
							ow = (int) Math.round(ow * 2);
						}
						else
						{
							ow = (int) Math.round(ow * 4);
						}
					}
					else
					{
						String s[] = comboResolution.getSelectedItem().toString().split("x");					
			        	ow = Integer.parseInt(s[0]); 
					}
				}
				else if (comboResolution.getSelectedItem().toString().contains(":"))
				{
					String s[] = comboResolution.getSelectedItem().toString().split(":");
					
					if (s[0].equals("auto"))
					{
						ow = Math.round(FFPROBE.imageHeight / Integer.parseInt(s[1]));
					}
					else if (s[1].equals("auto"))
					{
						ow = Math.round(FFPROBE.imageWidth / Integer.parseInt(s[0]));
					}
					else //ratio like 4:1 etc...
					{
						ow = Integer.parseInt(s[1]);
					}
				}

	        	if (VideoEncoders.setScalingFirst())
				{
	        		imageRatio = (float) FFPROBE.imageWidth / ow;
				}
			}

			float size = (float) Integer.parseInt(Main.textWatermarkSize.getText()) / 100;			

        	if (filterComplex != "") 	
        	{
            	filterComplex = "[0:v]" + filterComplex + "[v];[1:v]scale=iw*" + ((double) 1 / imageRatio) * size + ":ih*" + ((double) 1 / imageRatio) * size +			
        				",lut=a=val*" + ((float) Integer.parseInt(Main.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + Math.round(Integer.parseInt(Main.textWatermarkPosX.getText()) / imageRatio) + ":" + Math.round(Integer.parseInt(Main.textWatermarkPosY.getText()) / imageRatio);
        	}
        	else
        	{	
            	filterComplex = "[1:v]scale=iw*" + ((double) 1 / imageRatio) * size + ":ih*" + ((double) 1 / imageRatio) * size +
        				",lut=a=val*" + ((float) Integer.parseInt(Main.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + Math.round(Integer.parseInt(Main.textWatermarkPosX.getText()) / imageRatio) + ":" + Math.round(Integer.parseInt(Main.textWatermarkPosY.getText()) / imageRatio);
        	}

        }
        
		return filterComplex;
	}
	
	public static String setOverlay(String filterComplex, boolean limitToFHD) {
		
		if (Main.caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");

        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = (Math.round(ImageWidth - Integer.parseInt(Main.textSubsWidth.getText())) / 2);

        	if (Main.caseEnableCrop.isSelected())
        	{
        		posX -= Integer.parseInt(Main.textCropPosX.getText());
        	}
        	
        	if (limitToFHD)
        	{
        		String s[] = "1920x1080".split("x");
            	if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
            		s = comboResolution.getSelectedItem().toString().split("x");
            	
            	int iw = Integer.parseInt(i[0]);
            	int ow = Integer.parseInt(s[0]);  
            	
            	float imageRatio = (float) iw/ow;
            	
            	posX =  Math.round(posX / imageRatio);
        	}
        	else if (comboFonctions.getSelectedItem().toString().equals("DVD") == false && comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
        	{
        		String s[] = FFPROBE.imageResolution.split("x");	
				
        		if (comboResolution.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
					
					s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * value));
					s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * value));
				}					
				else					
					s = comboResolution.getSelectedItem().toString().split("x");
	        		        	
	        	int iw = Integer.parseInt(i[0]);
	        	int ow = Integer.parseInt(s[0]);  
	        		   
	        	float imageRatio = (float) iw/ow;
	        		        		        	
	        	posX = Math.round(posX / imageRatio);
        	}
    		
    		if (Main.caseAddWatermark.isSelected())
			{
    			filterComplex += "[p];[p][2:v]overlay=shortest=1:x=" + posX;
			}
    		else
    		{
        		if (filterComplex != "")
        			filterComplex += "[p];[p][1:v]overlay=shortest=1:x=" + posX;
        		else
        			filterComplex = "[0:v][1:v]overlay=shortest=1:x=" + posX;
    		}
      	}
    	
    	return filterComplex;
	}	
	
	private static String findFont(String fontName) {

	    if (fontName == null || fontName.isBlank()) {
	        return "";
	    }

	    // Normalize the font name used for comparison
	    String wanted = fontName
	            .toLowerCase()
	            .replace(" ", "")
	            .replace("_", "");

	    String os = System.getProperty("os.name").toLowerCase();

	    List<String> fontFolders = new ArrayList<>();
	    	    
	    if (os.contains("mac"))
	    {
	        fontFolders.add("/Library/Fonts");
	        fontFolders.add("/System/Library/Fonts");

	        File supplemental = new File("/System/Library/Fonts/Supplemental");
	        if (supplemental.exists()) {
	            fontFolders.add(supplemental.getAbsolutePath());
	        }

	        fontFolders.add(System.getProperty("user.home") + "/Library/Fonts");
	    }
	    else if (os.contains("win"))
	    {
	        // System fonts
	        fontFolders.add(System.getenv("WINDIR") + "\\Fonts");

	        // User fonts (Windows 10/11)
	        String localAppData = System.getenv("LOCALAPPDATA");

	        if (localAppData != null) {
	            fontFolders.add(
	                localAppData + "\\Microsoft\\Windows\\Fonts"
	            );
	        }
	    }    
	    else // Linux
	    {
	        // System fonts
	        fontFolders.add("/usr/share/fonts");
	        fontFolders.add("/usr/local/share/fonts");

	        // User fonts
	        String home = System.getProperty("user.home");

	        fontFolders.add(home + "/.fonts");
	        fontFolders.add(home + "/.local/share/fonts");
	    }

	    
	    // First pass: exact filename match
	    String result = findFontInFolders(fontFolders, wanted, false);

	    if (!result.isEmpty()) {
	        return result;
	    }

	    
	    // Second pass: filename contains font name  
	    result = findFontInFolders(fontFolders, wanted, true);

	    return result;
	}
	
	private static String findFontInFolders(List<String> folders, String wanted, boolean contains) {

	    for (String folderPath : folders)
	    {
	        File folder = new File(folderPath);

	        if (!folder.exists() || !folder.isDirectory()) {
	            continue;
	        }

	        File[] files = folder.listFiles();

	        if (files == null) {
	            continue;
	        }

	        for (File file : files) {

	            if (!file.isFile()) {
	                continue;
	            }

	            String fileName = file.getName().toLowerCase();

	            // Only actual font files
	            if (!fileName.endsWith(".ttf") &&
	                !fileName.endsWith(".otf") &&
	                !fileName.endsWith(".ttc"))
	            {
	                continue;
	            }

	            // Remove extension
	            int dot = fileName.lastIndexOf('.');

	            if (dot > 0) {
	                fileName = fileName.substring(0, dot);
	            }

	            // Normalize
	            String normalized = fileName
	                    .replace(" ", "")
	                    .replace("_", "");

	            boolean match;

	            if (contains) {
	                match = normalized.contains(wanted);
	            } else {
	                match = normalized.equals(wanted);
	            }

	            if (match) {
	                return file.getAbsolutePath();
	            }
	        }
	    }

	    return "";
	}
}
