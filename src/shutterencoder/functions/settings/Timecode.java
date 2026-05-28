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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.videoplayer.VideoPlayerUI;

public class Timecode extends Shutter {

	public static String setTimecode(File file) {
		
   		String dropFrame = ":";
        if (isDropFrame())
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
   		
   		String function = comboFonctions.getSelectedItem().toString();

		if (caseGenerateFromDate.isSelected())
		{
			try {		
				
	            BasicFileAttributes attrs = Files.readAttributes(Paths.get(file.toString()), BasicFileAttributes.class);
	            Instant creationTime = attrs.creationTime().toInstant();
	            String date = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault()).format(creationTime);
	            
	            String split[] = date.split(":");
	            
	            String audioTimecode = "";
	            if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function))
				{
	            	int h = Integer.parseInt(split[0]) * 3600;
	            	int m = Integer.parseInt(split[1]) * 60;
	            	int s = Integer.parseInt(split[2]);
	            	
	            	int totalSeconds = h + m + s;
	            	int sampleRate = FFPROBE.audioSampleRate;
	        		if (caseSampleRate.isSelected())
	        			sampleRate = (int) (Float.parseFloat(lbl48k.getSelectedItem().toString().replace("k", "")) * 1000);
	        		
	            	audioTimecode = " -metadata time_reference=" + '"' + ((long) totalSeconds * sampleRate) + '"';
				}
				
				return " -timecode " + '"' + split[0] + ":" + split[1] + ":" + split[2] + dropFrame + "00" + '"' + audioTimecode;
				
	        } catch (IOException e) {}		
		}		
		else if (caseSetTimecode.isSelected())
		{
			String audioTimecode = "";
            if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function))
			{
            	int h = Integer.parseInt(TCset1.getText()) * 3600;
            	int m = Integer.parseInt(TCset2.getText()) * 60;
            	int s = Integer.parseInt(TCset3.getText());
            	
            	int totalSeconds = h + m + s;
            	int sampleRate = FFPROBE.audioSampleRate;
        		if (caseSampleRate.isSelected())
        			sampleRate = (int) (Float.parseFloat(lbl48k.getSelectedItem().toString().replace("k", "")) * 1000);
        		
            	audioTimecode = " -metadata time_reference=" + '"' + ((long) totalSeconds * sampleRate) + '"';
			}
                       
            return " -timecode " + '"' + TCset1.getText() + ":" + TCset2.getText() + ":" + TCset3.getText() + dropFrame + TCset4.getText() + '"' + audioTimecode;
		}
		else if (FFPROBE.timecode1 != "")
		{						
			if (InputAndOutput.inPoint != "")
            {
				double time = (Integer.valueOf(FFPROBE.timecode1) + Integer.valueOf(VideoPlayerUI.caseInH.getText())) * 3600000
							+ (Integer.valueOf(FFPROBE.timecode2) + Integer.valueOf(VideoPlayerUI.caseInM.getText())) * 60000
							+ (Integer.valueOf(FFPROBE.timecode3) + Integer.valueOf(VideoPlayerUI.caseInS.getText())) * 1000
							+ (Integer.valueOf(FFPROBE.timecode4) + Integer.valueOf(VideoPlayerUI.caseInF.getText())) * VideoPlayerUI.inputFramerateMS;	
				
				String h = Shutter.formatter.format(Math.floor(time / 1000) / 3600);
				String m = Shutter.formatter.format((Math.floor(time / 1000) / 60) % 60);
				String s = Shutter.formatter.format(Math.floor(time / 1000) % 60);    		
				String f = Shutter.formatter.format((time % 1000) / VideoPlayerUI.inputFramerateMS);
				
            	return " -timecode " + '"' + h + ":" + m + ":" + s + dropFrame + f + '"';
            }
			else
				return " -timecode " + '"' + FFPROBE.timecode1 + ":" + FFPROBE.timecode2 + ":" + FFPROBE.timecode3 + dropFrame + FFPROBE.timecode4 + '"';
		}
		
		return "";
	}
	
	public static boolean isDropFrame() {
		
		if (FFPROBE.dropFrameTC.equals(";") && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f))
     	{
			return true;
     	}
		else
			return false;
	}
	
	public static boolean isNonDropFrame() {
		
		if (FFPROBE.dropFrameTC.equals(";") == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || FFPROBE.currentFPS == 23.98f)
     	{
			return true;
     	}
		else
			return false;
	}
	
	public static double setNTSCtimecode(double currentFrame) {
						
		if (currentFrame <= 0) return 0;
		
		//NTSC framerates => remove a frame to reach round framerate		
		if (isNonDropFrame())
		{
			double currentTime = currentFrame * ((double) 1000 / FFPROBE.currentFPS);
			
			if (FFPROBE.currentFPS == 23.98f)
			{
				currentFrame -= (currentTime * 0.024 / 1000) - 1;
			}
			else if (FFPROBE.currentFPS == 29.97f)
			{
				currentFrame -= (currentTime * 0.03 / 1000) - 1;
			}
			else if (FFPROBE.currentFPS == 59.94f)
			{					
				currentFrame -= (currentTime * 0.06 / 1000) - 1;
			}	
			
			return Math.floor(currentFrame);
		}	
		else if (isDropFrame())
		{
			return Math.round(currentFrame);
		}			
				
		return currentFrame;
	}
	
	public static double getNTSCtimecode(double currentFrame) {

		//Allows to set the current seeking values		
		if (isNonDropFrame())
		{
			double currentTime = currentFrame * ((double) 1000 / FFPROBE.currentFPS);
				
			if (FFPROBE.currentFPS == 23.98f)
			{
				currentFrame += (currentTime * 0.024 / 1000);
			}
			else if (FFPROBE.currentFPS == 29.97f)
			{
				currentFrame += (currentTime * 0.03 / 1000);
			}
			else if (FFPROBE.currentFPS == 59.94f)
			{
				currentFrame += (currentTime * 0.06 / 1000);
			}
			
			return Math.floor(currentFrame);
		}
		else if (isDropFrame())
		{
			return Math.round(currentFrame);
		}
		
		return currentFrame;
	}
	
	public static double setDropFrameTimecode(double currentFrame) {

		if (isDropFrame())
		{
			int step = (FFPROBE.currentFPS == 29.97f) ? 2 : 4;

			// Standard timecode counts per 10 minutes and 1 minute (including skipped numbers)
			int framesPer10Min = (FFPROBE.currentFPS == 29.97f) ? 17982 : 35964; 
			int framesPerMin   = (FFPROBE.currentFPS == 29.97f) ? 1798 : 3596;

			int frameCountInt = (int) Math.round(currentFrame);

			int d = frameCountInt / framesPer10Min;
			int m = frameCountInt % framesPer10Min;

			// Calculate frames to add based on exactly where the frame falls
			int framesToAdd;
			if (m > step)
			    framesToAdd = (step * 9 * d) + step * ((m - step) / framesPerMin);
			else
			    framesToAdd = (step * 9 * d);			

			currentFrame += framesToAdd;
		}
		
		return currentFrame;		
	}
	
	public static double getDropFrameTimecode(double currentFrame) {

		if (isDropFrame())
		{
		    int step = (FFPROBE.currentFPS == 29.97f) ? 2 : 4;

		    // Standard timecode counts per 10 minutes and 1 minute (including skipped numbers)
		    int timecodePer10Min = (FFPROBE.currentFPS == 29.97f) ? 18000 : 36000; 
		    int timecodePerMin   = (FFPROBE.currentFPS == 29.97f) ? 1800 : 3600;

		    int frameCountInt = (int) Math.round(currentFrame);

		    int d = frameCountInt / timecodePer10Min;
		    int m = frameCountInt % timecodePer10Min;

		    // Calculate how many frames *were* added during the forward calculation
		    int framesToSubtract = (step * 9 * d);
		    
		    if (m >= step) {
		        framesToSubtract += step * ((m - step) / timecodePerMin);
		    }

		    currentFrame -= framesToSubtract;
		}
		
		return currentFrame;		
	}
	
}
