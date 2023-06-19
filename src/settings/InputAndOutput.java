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

import application.Shutter;
import application.VideoPlayer;
import library.FFPROBE;

public class InputAndOutput extends Shutter {

	public static String inPoint = "";
	public static String outPoint = "";
	
	public static void getInputAndOutput() {
					
		float timeIn = (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText())) * FFPROBE.currentFPS + Integer.parseInt(VideoPlayer.caseInF.getText());
		
		//NTSC timecode
		timeIn = Timecode.getNonDropFrameTC(timeIn);
					
		if (timeIn > 0)
        {		        
			inPoint = " -ss " + (long) (timeIn * VideoPlayer.inputFramerateMS) + "ms";
	    }
	    else
	        inPoint = "";	
		
		if (VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
        {
			String frames[] = VideoPlayer.lblDuration.getText().split(" ");
						
        	if ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG")) && caseCreateSequence.isSelected())
        	{		        	
	    		float outputFPS = FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
	    		
	    		outPoint = " -vframes " + (int) Math.ceil(Integer.parseInt(frames[frames.length - 1]) / outputFPS);
        	}
        	else if (FFPROBE.audioOnly || (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) == false) 
        	|| comboFonctions.getSelectedItem().toString().equals("VP8")
        	|| comboFonctions.getSelectedItem().toString().equals("VP9")
        	|| comboFonctions.getSelectedItem().toString().equals("XDCAM HD422")
			|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
			|| comboFonctions.getSelectedItem().toString().equals("XAVC")) //Issue for audio, libvpx & added mute track
        	{
        		outPoint = " -t " + (int) Math.floor(Integer.parseInt(frames[frames.length - 1]) * ((float) 1000 / FFPROBE.currentFPS)) + "ms";
        	}
        	else
        	{
	        	outPoint = " -vframes " + Integer.parseInt(frames[frames.length - 1]);
        	}
        }
        else
        	outPoint = "";
		
		if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
		{
			outPoint += " -f segment -reset_timestamps 1 -segment_time " + VideoPlayer.splitValue.getText();
		}
	}

}
