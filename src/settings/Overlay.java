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

package settings;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JTextField;

import application.VideoPlayer;
import functions.VideoEncoders;
import application.RecordInputDevice;
import application.Shutter;
import application.SubtitlesEmbed;
import library.FFPROBE;

public class Overlay extends Shutter {

	public static String showTimecode(String filterComplex, String file, boolean videoPlayerCapture) {
		
		if ((Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected() || Shutter.caseAddText.isSelected() || Shutter.caseShowFileName.isSelected()))
		{			
			String tc1 = FFPROBE.timecode1;
			String tc2 = FFPROBE.timecode2;
			String tc3 = FFPROBE.timecode3;
			String tc4 = FFPROBE.timecode4;
						 
			if (Shutter.caseAddTimecode.isSelected() && Shutter.TC1.getText().isEmpty() == false && Shutter.TC2.getText().isEmpty() == false && Shutter.TC3.getText().isEmpty() == false && Shutter.TC4.getText().isEmpty() == false)
			{			
				tc1 = Shutter.TC1.getText();
				tc2 = Shutter.TC2.getText();			
				tc3 = Shutter.TC3.getText();		    
				tc4 = Shutter.TC4.getText();			
			}
			else if (Shutter.caseShowTimecode.isSelected() && FFPROBE.timecode1.equals("") == false)
			{							
				float tcH = Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS;
				float tcM = Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS;
				float tcS = Integer.parseInt(tc3) * FFPROBE.currentFPS;
				
				float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());

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
				if (Shutter.caseShowTimecode.isSelected())
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
				
				float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());

				//NTSC framerate
				if (Shutter.caseShowTimecode.isSelected())
				{
					timeIn = 0;
				}
												
				float currentTime = Timecode.setNonDropFrameTC(VideoPlayer.playerCurrentFrame);
				float offset = (currentTime - timeIn) + tcH + tcM + tcS + Integer.parseInt(tc4);

				if (offset < 0)
					offset = 0;
				
				tc1 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 3600));
				tc2 = formatter.format(Math.floor(offset / FFPROBE.currentFPS / 60) % 60);
				tc3 = formatter.format(Math.floor(offset / FFPROBE.currentFPS) % 60);    		
				tc4 = formatter.format(Math.floor(offset % FFPROBE.currentFPS));
			}
			
			String rate = String.valueOf(FFPROBE.currentFPS);
			if (caseConform.isSelected())
				rate = comboFPS.getSelectedItem().toString().replace(",", ".");
			
			String overlayFont = "";
			if (System.getProperty("os.name").contains("Mac"))
			{	
				 //Library
				 File[] fontFolder =  new File("/Library/Fonts").listFiles();
		
				 for (int i = 0; i < fontFolder.length; i++)
				 {				 
					if (fontFolder[i].isFile())
					{
						File fontPath = new File(fontFolder[i].toString());
						String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
						
						if (fontName[0].equals(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
						{
							overlayFont = fontFolder[i].getAbsolutePath();	
							break;
						}
				 	}
				 }
				 		
				 //System Library
				 if (overlayFont == "")
				 {
					 fontFolder = new File("/System/Library/Fonts").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
						 if (fontFolder[i].isFile())
						 {
							File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].equals(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
				 		 }	   
					 }
				 }
				 
				//Supplemental Library
				if (overlayFont == "" && new File("/System/Library/Fonts/Supplemental").exists())
				{
					 fontFolder = new File("/System/Library/Fonts/Supplemental").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
						 if (fontFolder[i].isFile())
						 {
							File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].equals(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
				 		 }	   
					 }
				 }
				 
				 //User Library					 
				 if (overlayFont == "")
				 {
					 fontFolder = new File(System.getProperty("user.home") + "/Library/Fonts").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
					 	if (fontFolder[i].isFile())
						{
					 		File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].equals(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
					 	}   
					 }
				 }	
				 
				 
				 //Library with contains				 
				 if (overlayFont == "")
				 {
					 fontFolder =  new File("/Library/Fonts").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {				 
						if (fontFolder[i].isFile())
						{
							File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].contains(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
					 	}
					 }
				 }
				 		
				 //System Library with contains
				 if (overlayFont == "")
				 {
					 fontFolder = new File("/System/Library/Fonts").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
						 if (fontFolder[i].isFile())
						 {
							File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].contains(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
				 		 }	   
					 }
				 }
				 
				 //User Library	 with contains				 
				 if (overlayFont == "")
				 {
					 fontFolder = new File(System.getProperty("user.home") + "/Library/Fonts").listFiles();
					 
					 for (int i = 0; i < fontFolder.length; i++)
					 {						 
					 	if (fontFolder[i].isFile())
						{
					 		File fontPath = new File(fontFolder[i].toString());
							String fontName[] = fontPath.getName().toLowerCase().replace(" ",  "").replace("_", "").split("\\.");
							
							if (fontName[0].contains(Shutter.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
							{
								overlayFont = fontFolder[i].getAbsolutePath();	
								break;
							}
					 	}   
					 }
				 }
				 
				 if (overlayFont == "")
					 overlayFont = "/Library/Fonts/Arial";
			}
			else
				overlayFont = "font=" + Shutter.comboOverlayFont.getSelectedItem().toString();
	      
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
						if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
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

	      	if (Shutter.caseShowFileName.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + file + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Shutter.textNamePosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Shutter.textNamePosY.getText()) / imageRatio) + ":fontcolor=0x" + Shutter.foregroundHex + Shutter.foregroundNameAlpha + ":fontsize=" + Math.round(Integer.parseInt(Shutter.textNameSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Shutter.backgroundHex + Shutter.backgroundNameAlpha;
	      	}
	      	
	      	if (Shutter.caseAddText.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + Shutter.text.getText() + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Shutter.textNamePosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Shutter.textNamePosY.getText()) / imageRatio) + ":fontcolor=0x" + Shutter.foregroundHex + Shutter.foregroundNameAlpha + ":fontsize=" + Math.round(Integer.parseInt(Shutter.textNameSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Shutter.backgroundHex + Shutter.backgroundNameAlpha;
	      	}
	      	
		   	if ((Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected()))
		   	{
		   		String dropFrame = ":";
		   		if (FFPROBE.dropFrameTC.equals(":") == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f))
		   		{
		   			dropFrame = ";";
		   		}
		        
		   		if (Shutter.caseConform.isSelected())
		   		{
		   			if (Shutter.comboFPS.getSelectedItem().toString().equals("29,97") || Shutter.comboFPS.getSelectedItem().toString().equals("59,94"))
		   			{
		   				dropFrame = ";";
		   			}
		   			else 
		   				dropFrame = ":";
		   		}
		   			
		   		if (filterComplex != "") filterComplex += ",";
		   		
		   		if (Shutter.caseAddTimecode.isSelected() && Shutter.lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
		   		{
		   			String startNumber = String.format("%.0f", Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS + Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS + Integer.parseInt(tc3) * FFPROBE.currentFPS + Integer.parseInt(tc4));
		   			filterComplex += "drawtext=" + overlayFont + ":text='%{frame_num}': start_number=" + startNumber + ":x=" + Math.round(Integer.parseInt(Shutter.textTcPosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Shutter.textTcPosY.getText()) / imageRatio) + ":fontcolor=0x" + Shutter.foregroundHex + Shutter.foregroundTcAlpha + ":fontsize=" + Math.round(Integer.parseInt(Shutter.textTcSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Shutter.backgroundHex + Shutter.backgroundTcAlpha;
		   		}
		   		else
		   			filterComplex += "drawtext=" + overlayFont + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\" + dropFrame + tc4 + "':r=" + rate + ":x=" + Math.round(Integer.parseInt(Shutter.textTcPosX.getText()) / imageRatio) + ":y=" + Math.round(Integer.parseInt(Shutter.textTcPosY.getText()) / imageRatio) + ":fontcolor=0x" + Shutter.foregroundHex + Shutter.foregroundTcAlpha + ":fontsize=" + Math.round(Integer.parseInt(Shutter.textTcSize.getText()) / imageRatio) + ":box=1:boxcolor=0x" + Shutter.backgroundHex + Shutter.backgroundTcAlpha + ":tc24hmax=1";	      
		   	}   
		}
		
		return filterComplex;
	}

	public static String setSubtitles(boolean limitToFHD) {
		
    	if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    	
			if (subtitlesFile.toString().substring(subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
    		{	
				String background = "" ;
				if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
					background = ",BorderStyle=4,BackColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&,Outline=0";
				else
					background = ",Outline=" + Shutter.outline + ",OutlineColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&";
				
				//Bold
				if (Shutter.btnG.getForeground() != Color.BLACK)
					background += ",Bold=1";
				
				//Italic
				if (Shutter.btnI.getForeground() != Color.BLACK)
					background += ",Italic=1";
				
				String i[] = FFPROBE.imageResolution.split("x"); 
				
				//Set the input seeking
				float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());			
				VideoPlayer.writeCurrentSubs(timeIn);
				
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
					
					int width = Math.round((float) Integer.parseInt(Shutter.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
					int height = Math.round((float) (ih + Integer.parseInt(Shutter.textSubtitlesPosition.getText())) / ((float) ih/oh));
															
					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Shutter.textSubsSize.getText() + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"';
				}		
				else
				{					
					int fontSize = Integer.parseInt(Shutter.textSubsSize.getText());
					int height = Integer.parseInt(Shutter.textSubtitlesPosition.getText());
					
					if (Shutter.caseEnableCrop.isSelected())
					{
						int ih = Integer.parseInt(i[1]);
						int oh = Integer.parseInt(Shutter.textCropHeight.getText());
						height -= Integer.parseInt(Shutter.textCropPosY.getText());
						fontSize = (int) ((float) fontSize * ((float) ih/oh));
					}
										
					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + Shutter.textSubsWidth.getText() + ":" + i[1] + "+" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + fontSize + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"';		
				}
			}
			else // ASS or SSA
			{
				String i[] = FFPROBE.imageResolution.split("x");
				Shutter.textSubsWidth.setText(i[0]); //IMPORTANT
				
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
	    					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
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
		        	
		        	int width = Math.round((float) Integer.parseInt(Shutter.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
		        	int height = Math.round((float) (ih + Integer.parseInt(Shutter.textSubtitlesPosition.getText())) / ((float) ih/oh));
		        	
		        	return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
				}
				else
					return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + i[0] + ":" + i[1] + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
			}
		}
		else if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn == false)
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
		
		if (Shutter.caseAddWatermark.isSelected() && Shutter.overlayDeviceIsRunning)
		{
			return " " + RecordInputDevice.setOverlayDevice(); 
		}
		else if (Shutter.caseAddWatermark.isSelected())
		{
			return " -i " + '"' + Shutter.logoFile + '"'; 
		}
		
		return "";
	}
	
	public static String setWatermark(String filterComplex) {
		
		if (Shutter.caseAddWatermark.isSelected())
        {		     
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
						if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
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

			float size = (float) Integer.parseInt(Shutter.textWatermarkSize.getText()) / 100;			

        	if (filterComplex != "") 	
        	{
            	filterComplex = "[0:v]" + filterComplex + "[v];[1:v]scale=iw*" + ((double) 1 / imageRatio) * size + ":ih*" + ((double) 1 / imageRatio) * size +			
        				",lut=a=val*" + ((float) Integer.parseInt(Shutter.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + Math.round(Integer.parseInt(Shutter.textWatermarkPosX.getText()) / imageRatio) + ":" + Math.round(Integer.parseInt(Shutter.textWatermarkPosY.getText()) / imageRatio);
        	}
        	else
        	{	
            	filterComplex = "[1:v]scale=iw*" + ((double) 1 / imageRatio) * size + ":ih*" + ((double) 1 / imageRatio) * size +
        				",lut=a=val*" + ((float) Integer.parseInt(Shutter.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + Math.round(Integer.parseInt(Shutter.textWatermarkPosX.getText()) / imageRatio) + ":" + Math.round(Integer.parseInt(Shutter.textWatermarkPosY.getText()) / imageRatio);
        	}

        }
        
		return filterComplex;
	}
	
	public static String setOverlay(String filterComplex, boolean limitToFHD) {
		
		if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");

        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = (Math.round(ImageWidth - Integer.parseInt(Shutter.textSubsWidth.getText())) / 2);

        	if (Shutter.caseEnableCrop.isSelected())
        	{
        		posX -= Integer.parseInt(Shutter.textCropPosX.getText());
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
    		
    		if (Shutter.caseAddWatermark.isSelected())
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
}
