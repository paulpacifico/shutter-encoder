package settings;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import application.OverlayWindow;
import application.RecordInputDevice;
import application.Shutter;
import application.SubtitlesEmbed;
import application.SubtitlesWindow;
import application.WatermarkWindow;
import library.FFMPEG;
import library.FFPROBE;

public class Overlay extends Shutter {

	public static String showTimecode(String filterComplex, String file) {
		
		if (grpOverlay.isVisible())
		{
			 String tc1 = FFPROBE.timecode1;
			 String tc2 = FFPROBE.timecode2;
			 String tc3 = FFPROBE.timecode3;
			 String tc4 = FFPROBE.timecode4;
			 
			if (OverlayWindow.caseAddTimecode.isSelected())
			{
				 tc1 = OverlayWindow.TC1.getText();
				 tc2 = OverlayWindow.TC2.getText();			
				 tc3 = OverlayWindow.TC3.getText();		    
				 tc4 = OverlayWindow.TC4.getText();
			}
			
			String rate = String.valueOf(FFPROBE.currentFPS);
			if (caseConform.isSelected())
				rate = comboFPS.getSelectedItem().toString().replace(",", ".");
	      
	      	if (OverlayWindow.caseShowFileName.isSelected() && caseAddOverlay.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + file + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
	      	}
	      	
	      	if (OverlayWindow.caseShowText.isSelected() && caseAddOverlay.isSelected())
	      	{
	      		if (filterComplex != "") filterComplex += ",";
	      			filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + OverlayWindow.text.getText() + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
	      	}
	      	
		   	if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
		   	{
		   		String dropFrame = ":";
		   		if (caseConform.isSelected() == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || caseConform.isSelected() && (comboFPS.getSelectedItem().toString().equals("29,97") || comboFPS.getSelectedItem().toString().equals("59,94")))
		   			dropFrame = ";";
		   			
		   		if (filterComplex != "") filterComplex += ",";
		   		
		   		if (OverlayWindow.caseAddTimecode.isSelected() && OverlayWindow.lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
		   		{
		   			String startNumber = String.format("%.0f", Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS + Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS + Integer.parseInt(tc3) * FFPROBE.currentFPS + Integer.parseInt(tc4));
		   			filterComplex += "drawtext=" + OverlayWindow.font + ":text='%{frame_num}': start_number=" + startNumber + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc;
		   		}
		   		else
		   			filterComplex += "drawtext=" + OverlayWindow.font + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\" + dropFrame + tc4 + "':r=" + rate + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc + ":tc24hmax=1";	      
		   	}   
		}
		
		return filterComplex;
	}

	public static String setSubtitles(JComboBox<String> comboScale, boolean limitToFHD) {
		
		if (grpOverlay.isVisible() || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")))
		{
	    	if (caseSubtitles.isSelected() && subtitlesBurn)
	    	{    	
				if (subtitlesFile.toString().substring(subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
	    		{	
					String background = "" ;
					if (SubtitlesWindow.lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
						background = ",BorderStyle=4,BackColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&,Outline=0";
					else
						background = ",Outline=" + SubtitlesWindow.outline + ",OutlineColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&";
					
					//Bold
					if (SubtitlesWindow.btnG.getForeground() != Color.BLACK)
						background += ",Bold=1";
					
					//Italic
					if (SubtitlesWindow.btnI.getForeground() != Color.BLACK)
						background += ",Italic=1";
					
					String i[] = FFPROBE.imageResolution.split("x");
					
					if (limitToFHD)
					{
						String s[] = "1920x1080".split("x");
						if (comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
							s = comboScale.getSelectedItem().toString().split("x");
							
						int iw = Integer.parseInt(i[0]);
						int ih = Integer.parseInt(i[1]);
						int ow = Integer.parseInt(s[0]);
						int oh = Integer.parseInt(s[1]);      
	
						int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
						int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));
	
						return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
					}		
					else if (comboScale != null && comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false && lblPad.getText().equals(language.getProperty("lblStretch")) == false
					&& (caseRognage.isSelected() == false || caseRognage.isSelected() && lblPad.getText().equals(Shutter.language.getProperty("lblCrop"))))
					{
						String s[] = comboScale.getSelectedItem().toString().split("x");
						
						int iw = Integer.parseInt(i[0]);
						int ih = Integer.parseInt(i[1]);
						int ow = Integer.parseInt(s[0]);
						int oh = Integer.parseInt(s[1]);      
						
						int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
						int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));
						
						return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
					}
					else
					{
						return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + SubtitlesWindow.textWidth.getText() + ":" + i[1] + "+" + SubtitlesWindow.spinnerSubtitlesPosition.getValue() + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';		
					}
				}
				else // ASS or SSA
				{
					String i[] = FFPROBE.imageResolution.split("x");
					SubtitlesWindow.textWidth.setText(i[0]); //IMPORTANT
					
					if (comboScale.getSelectedItem().toString().equals(language.getProperty("source")) == false)
					{
						String s[] = comboScale.getSelectedItem().toString().split("x");
			        	int iw = Integer.parseInt(i[0]);
			        	int ih = Integer.parseInt(i[1]);
			        	int ow = Integer.parseInt(s[0]);
			        	int oh = Integer.parseInt(s[1]);        	
			        	
			        	int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
			        	int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));
			        	
			        	return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
					}
					else
						return " -f lavfi" + InputAndOutput.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + i[0] + ":" + i[1] + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
				}
			}
			else if (caseSubtitles.isSelected() && subtitlesBurn == false)
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
		}
    	
    	return "";
	}
	
	public static String setLogo() {
		
		if (grpOverlay.isVisible())
		{
			if (caseLogo.isSelected() && Shutter.overlayDeviceIsRunning)
			{
				return " " + RecordInputDevice.setOverlayDevice(); 
			}
			else if (caseLogo.isSelected())
			{
				return " -i " + '"' + WatermarkWindow.logoFile + '"'; 
			}	
		}
		
		return "";
	}
	
	public static String setWatermark(String filterComplex) {
		
		if (grpOverlay.isVisible() && caseLogo.isSelected())
        {		        	
        	if (filterComplex != "") 	
        	{
            	filterComplex = "[0:v]" + filterComplex + "[v];[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        	else
        	{	
            	filterComplex = "[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        }
        
		return filterComplex;
	}
	
	public static String setOverlay(String filterComplex, JComboBox<String> comboScale, boolean limitToFHD) {
		
		if (grpOverlay.isVisible() && caseSubtitles.isSelected() && subtitlesBurn)
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");
        	
        	//IMPORTANT ratio inf. à celui d'origine
        	if (caseRognage.isSelected() && lblPad.getText().equals(Shutter.language.getProperty("lblCrop")) && FFMPEG.ratioFinal < (float) Integer.parseInt(i[0]) / Integer.parseInt(i[1]))
        		i = String.valueOf(((int) (Integer.parseInt(i[1])*FFMPEG.ratioFinal) + "x" + i[1])).split("x");
        	
        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = ((int) (ImageWidth - Integer.parseInt(SubtitlesWindow.textWidth.getText())) / 2);
        	        	
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
	        	String s[] = comboScale.getSelectedItem().toString().split("x");
	        		        	
	        	int iw = Integer.parseInt(i[0]);
	        	int ow = Integer.parseInt(s[0]);  
	        	posX =  (int) (posX / ((float) iw/ow));
        	}
    		
    		if (caseLogo.isSelected())
    			filterComplex += "[p];[p][2:v]overlay=shortest=1:x=" + posX;
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
