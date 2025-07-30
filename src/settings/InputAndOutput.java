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

package settings;

import application.Shutter;
import application.VideoPlayer;
import library.FFPROBE;

public class InputAndOutput extends Shutter {

	public static String inPoint = "";
	public static String outPoint = "";
	public static double savedInPoint = 0;
	public static double savedOutPoint = 0;
	
	public static void getInputAndOutput(boolean setInputAndOutput) {
					
		if (setInputAndOutput && FFPROBE.totalLength > 40)
		{
			double timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.accurateFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
			
			//NTSC timecode
			timeIn = Timecode.getNTSCtimecode(timeIn);
			
			if (timeIn > 0.0f)
	        {		        
				inPoint = " -ss " + Math.floor((double) timeIn * VideoPlayer.inputFramerateMS) + "ms";
		    }
		    else
		        inPoint = "";	
									
			if (VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2 && caseEnableSequence.isSelected() == false)
	        {
				String frames[] = VideoPlayer.lblDuration.getText().split(" ");
				
	        	if ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().contains("JPEG")) && caseCreateSequence.isSelected())
	        	{		        	
	        		double outputFPS = FFPROBE.accurateFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
		    		
		    		outPoint = " -frames:v " + (int) Math.ceil((double) Integer.parseInt(frames[frames.length - 2]) / outputFPS);
	        	}
	        	else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))	        
	        	{
		        	outPoint = " -frames:v " + Integer.parseInt(frames[frames.length - 2]);
	        	}
	        	else
	        	{
	        		outPoint = " -t " + Math.floor((double) Integer.parseInt(frames[frames.length - 2]) * ((float) 1000 / FFPROBE.accurateFPS)) + "ms";
	        	}
	        	
	        }
	        else
	        	outPoint = "";
			
			if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
			{
				outPoint += " -f segment -reset_timestamps 1 -segment_time " + VideoPlayer.splitValue.getText();
			}
		}
		else
		{
			inPoint = "";
			outPoint = "";
		}
	}

}
