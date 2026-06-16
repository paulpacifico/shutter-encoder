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

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;

public class InputAndOutput extends Shutter {

	public static String inPoint = "";
	public static String outPoint = "";
	public static double savedInPoint = 0;
	public static double savedOutPoint = 0;
	
	public static void getInputAndOutput(boolean setInputAndOutput) {
					
		if (setInputAndOutput && FFPROBE.totalLength > 40)
		{			
			double timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayerUI.caseInS.getText())) * VideoPlayerCore.getFPS() + Integer.parseInt(VideoPlayerUI.caseInF.getText());
			
			//NTSC framerate
			timeIn = Timecode.getNTSCtimecode(timeIn);
			timeIn = Timecode.getDropFrameTimecode(timeIn);
					
			if (timeIn > 0.0f)
	        {		        
				inPoint = " -ss " + (long) ((double) timeIn * VideoPlayerUI.inputFramerateMS) + "ms";
		    }
		    else
		        inPoint = "";	
			
			if (VideoPlayerUI.playerOutMark < VideoPlayerCore.waveformContainer.getWidth() - 2 && caseEnableSequence.isSelected() == false)
	        {				
				String framesText[] = VideoPlayerUI.lblDuration.getText().split(" ");
				Integer frames =  Integer.parseInt(framesText[framesText.length - 2]);

	        	if ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().contains("JPEG")) && caseCreateSequence.isSelected())
	        	{		        	
	        		double outputFPS = FFPROBE.accurateFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
		    		
		    		outPoint = " -frames:v " + (int) Math.ceil((double) frames / outputFPS);
	        	}
	        	else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))	        
	        	{
		        	outPoint = " -frames:v " + frames;
	        	}
	        	else
	        	{
	        		outPoint = " -t " + (long) Math.floor((double) frames * ((float) 1000 / FFPROBE.accurateFPS)) + "ms";
	        	}
	        	
	        }
	        else
	        	outPoint = "";
			
			if (VideoPlayerUI.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
			{
				outPoint += " -f segment -segment_time " + VideoPlayerUI.splitValue.getText() + " -reset_timestamps 1";
			}
		}
		else
		{
			inPoint = "";
			outPoint = "";
		}
	}

}
