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

package settings;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import application.VideoPlayer;
import application.RecordInputDevice;
import application.Shutter;
import application.SubtitlesEmbed;
import library.FFPROBE;

public class Overlay extends Shutter {

	public static String showTimecode(String filterComplex, String file) {
		
		if (VideoPlayer.caseAddTimecode.isSelected() || VideoPlayer.caseShowTimecode.isSelected() || VideoPlayer.caseAddText.isSelected() || VideoPlayer.caseShowFileName.isSelected())
		{
			 String tc1 = FFPROBE.timecode1;
			 String tc2 = FFPROBE.timecode2;
			 String tc3 = FFPROBE.timecode3;
			 String tc4 = FFPROBE.timecode4;
			 
			if (VideoPlayer.caseAddTimecode.isSelected())
			{
				 tc1 = VideoPlayer.TC1.getText();
				 tc2 = VideoPlayer.TC2.getText();			
				 tc3 = VideoPlayer.TC3.getText();		    
				 tc4 = VideoPlayer.TC4.getText();
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
						
						if (fontName[0].equals(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].equals(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].equals(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].equals(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].contains(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].contains(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
							
							if (fontName[0].contains(VideoPlayer.comboOverlayFont.getSelectedItem().toString().toLowerCase().replace(" ",  "").replace("_", "")))
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
				overlayFont = "font=" + VideoPlayer.comboOverlayFont.getSelectedItem().toString();
	      
	      	if (VideoPlayer.caseShowFileName.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + file + "':r=" + rate + ":x=" + VideoPlayer.textNamePosX.getText() + ":y=" + VideoPlayer.textNamePosY.getText() + ":fontcolor=0x" + VideoPlayer.foregroundHex + VideoPlayer.foregroundNameAlpha + ":fontsize=" + VideoPlayer.textNameSize.getText() + ":box=1:boxcolor=0x" + VideoPlayer.backgroundHex + VideoPlayer.backgroundNameAlpha;
	      	}
	      	
	      	if (VideoPlayer.caseAddText.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + overlayFont + ":text='" + VideoPlayer.text.getText() + "':r=" + rate + ":x=" + VideoPlayer.textNamePosX.getText() + ":y=" + VideoPlayer.textNamePosY.getText() + ":fontcolor=0x" + VideoPlayer.foregroundHex + VideoPlayer.foregroundNameAlpha + ":fontsize=" + VideoPlayer.textNameSize.getText() + ":box=1:boxcolor=0x" + VideoPlayer.backgroundHex + VideoPlayer.backgroundNameAlpha;
	      	}
	      	
		   	if ((VideoPlayer.caseAddTimecode.isSelected() || VideoPlayer.caseShowTimecode.isSelected()))
		   	{
		   		String dropFrame = ":";
		   		if (caseConform.isSelected() == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || caseConform.isSelected() && (comboFPS.getSelectedItem().toString().equals("29,97") || comboFPS.getSelectedItem().toString().equals("59,94")))
		   			dropFrame = ";";
		   			
		   		if (filterComplex != "") filterComplex += ",";
		   		
		   		if (VideoPlayer.caseAddTimecode.isSelected() && VideoPlayer.lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
		   		{
		   			String startNumber = String.format("%.0f", Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS + Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS + Integer.parseInt(tc3) * FFPROBE.currentFPS + Integer.parseInt(tc4));
		   			filterComplex += "drawtext=" + overlayFont + ":text='%{frame_num}': start_number=" + startNumber + ":x=" + VideoPlayer.textTcPosX.getText() + ":y=" + VideoPlayer.textTcPosY.getText() + ":fontcolor=0x" + VideoPlayer.foregroundHex + VideoPlayer.foregroundTcAlpha + ":fontsize=" + VideoPlayer.textTcSize.getText() + ":box=1:boxcolor=0x" + VideoPlayer.backgroundHex + VideoPlayer.backgroundTcAlpha;
		   		}
		   		else
		   			filterComplex += "drawtext=" + overlayFont + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\" + dropFrame + tc4 + "':r=" + rate + ":x=" + VideoPlayer.textTcPosX.getText() + ":y=" + VideoPlayer.textTcPosY.getText() + ":fontcolor=0x" + VideoPlayer.foregroundHex + VideoPlayer.foregroundTcAlpha + ":fontsize=" + VideoPlayer.textTcSize.getText() + ":box=1:boxcolor=0x" + VideoPlayer.backgroundHex + VideoPlayer.backgroundTcAlpha + ":tc24hmax=1";	      
		   	}   
		}
		
		return filterComplex;
	}

	public static String setSubtitles(JComboBox<String> comboScale, boolean limitToFHD) {
		
    	if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    	
			if (subtitlesFile.toString().substring(subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
    		{	
				String background = "" ;
				if (VideoPlayer.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
					background = ",BorderStyle=4,BackColour=&H" + VideoPlayer.subsAlpha + VideoPlayer.subsHex2 + "&,Outline=0";
				else
					background = ",Outline=" + VideoPlayer.outline + ",OutlineColour=&H" + VideoPlayer.subsAlpha + VideoPlayer.subsHex2 + "&";
				
				//Bold
				if (VideoPlayer.btnG.getForeground() != Color.BLACK)
					background += ",Bold=1";
				
				//Italic
				if (VideoPlayer.btnI.getForeground() != Color.BLACK)
					background += ",Italic=1";
				
				String i[] = FFPROBE.imageResolution.split("x");
				
				//Set the input seeking
				float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());			
				VideoPlayer.writeCurrentSubs(timeIn);
				
				if (limitToFHD)
				{
					String s[] = "1920x1080".split("x");
					if (comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
						s = comboScale.getSelectedItem().toString().split("x");
						
					int iw = Integer.parseInt(i[0]);
					int ih = Integer.parseInt(i[1]);
					int ow = Integer.parseInt(s[0]);
					int oh = Integer.parseInt(s[1]);      

					int width = (int) ((float) Integer.parseInt(VideoPlayer.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
					int height = (int) ((float) (ih + Integer.parseInt(VideoPlayer.textSubtitlesPosition.getText())) / ((float) ih/oh));
					
					if (VideoPlayer.caseEnableCrop.isSelected())
						height -= Integer.parseInt(VideoPlayer.textCropPosY.getText());

					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + VideoPlayer.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + VideoPlayer.textSubsSize.getText() + ",PrimaryColour=&H" + VideoPlayer.subsHex + "&" + background + "'" + '"';
				}		
				else
				{
					int height = Integer.parseInt(VideoPlayer.textSubtitlesPosition.getText());
					
					if (VideoPlayer.caseEnableCrop.isSelected())
						height -= Integer.parseInt(VideoPlayer.textCropPosY.getText());
					
					return " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + VideoPlayer.textSubsWidth.getText() + ":" + i[1] + "+" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + VideoPlayer.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + VideoPlayer.textSubsSize.getText() + ",PrimaryColour=&H" + VideoPlayer.subsHex + "&" + background + "'" + '"';		
				}
			}
			else // ASS or SSA
			{
				String i[] = FFPROBE.imageResolution.split("x");
				VideoPlayer.textSubsWidth.setText(i[0]); //IMPORTANT
				
				if (comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = FFPROBE.imageResolution.split("x");	
					
					if (comboScale.getSelectedItem().toString().contains("%"))
					{
						double value = (double) Integer.parseInt(comboScale.getSelectedItem().toString().replace("%", "")) / 100;
						
						s[0] = String.valueOf((int) (Integer.parseInt(s[0]) * value));
						s[1] = String.valueOf((int) (Integer.parseInt(s[1]) * value));
					}					
					else					
						s = comboScale.getSelectedItem().toString().split("x");
					
		        	int iw = Integer.parseInt(i[0]);
		        	int ih = Integer.parseInt(i[1]);
		        	int ow = Integer.parseInt(s[0]);
		        	int oh = Integer.parseInt(s[1]);        	
		        	
		        	int width = (int) ((float) Integer.parseInt(VideoPlayer.textSubsWidth.getText()) / ((float) iw/ow));	        		        	
		        	int height = (int) ((float) (ih + Integer.parseInt(VideoPlayer.textSubtitlesPosition.getText())) / ((float) ih/oh));
		        	
		        	return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
				}
				else
					return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + i[0] + ":" + i[1] + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
			}
		}
		else if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn == false)
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
		
		if (VideoPlayer.caseAddWatermark.isSelected() && Shutter.overlayDeviceIsRunning)
		{
			return " " + RecordInputDevice.setOverlayDevice(); 
		}
		else if (VideoPlayer.caseAddWatermark.isSelected())
		{
			return " -i " + '"' + VideoPlayer.logoFile + '"'; 
		}
		
		return "";
	}
	
	public static String setWatermark(String filterComplex) {
		
		if (VideoPlayer.caseAddWatermark.isSelected())
        {		        	
        	if (filterComplex != "") 	
        	{
            	filterComplex = "[0:v]" + filterComplex + "[v];[1:v]scale=iw*" + ((float)  Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + VideoPlayer.textWatermarkPosX.getText() + ":" + VideoPlayer.textWatermarkPosY.getText();
        	}
        	else
        	{	
            	filterComplex = "[1:v]scale=iw*" + ((float)  Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(VideoPlayer.textWatermarkOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + VideoPlayer.textWatermarkPosX.getText() + ":" + VideoPlayer.textWatermarkPosY.getText();
        	}
        }
        
		return filterComplex;
	}
	
	public static String setOverlay(String filterComplex, JComboBox<String> comboScale, boolean limitToFHD) {
		
		if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn)
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");

        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = ((int) (ImageWidth - Integer.parseInt(VideoPlayer.textSubsWidth.getText())) / 2);

        	if (VideoPlayer.caseEnableCrop.isSelected())
        	{
        		posX -= Integer.parseInt(VideoPlayer.textCropPosX.getText());
        	}
        	
        	if (limitToFHD)
        	{
        		String s[] = "1920x1080".split("x");
            	if (comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
            		s = comboScale.getSelectedItem().toString().split("x");
            	
            	int iw = Integer.parseInt(i[0]);
            	int ow = Integer.parseInt(s[0]);  
            	posX =  (int) (posX / ((float) iw/ow));
        	}
        	else if (comboScale != null && comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
        	{
        		String s[] = FFPROBE.imageResolution.split("x");	
				
        		if (comboScale.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboScale.getSelectedItem().toString().replace("%", "")) / 100;
					
					s[0] = String.valueOf((int) (Integer.parseInt(s[0]) * value));
					s[1] = String.valueOf((int) (Integer.parseInt(s[1]) * value));
				}					
				else					
					s = comboScale.getSelectedItem().toString().split("x");
	        		        	
	        	int iw = Integer.parseInt(i[0]);
	        	int ow = Integer.parseInt(s[0]);  
	        	posX =  (int) (posX / ((float) iw/ow));
        	}
    		
    		if (VideoPlayer.caseAddWatermark.isSelected())
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
