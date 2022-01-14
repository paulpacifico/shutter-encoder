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

import application.Shutter;
import application.Settings;
import application.VideoPlayer;
import library.FFPROBE;

public class Transitions extends Shutter {

	public static String setVideoFade(String filterComplex) {
		
		if (grpTransitions.isVisible())
		{
			//Fade-in
	    	if (caseVideoFadeIn.isSelected())
	    	{ 
	    		if (filterComplex != "") filterComplex += ",";	
	    		
	    		long videoInValue = (long) (Integer.parseInt(spinnerVideoFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
	    		
	    		String color = "black";
				if (lblFadeInColor.getText().equals(language.getProperty("white")))
					color = "white";
	    		
	    		String videoFade = "fade=in:st=0ms:d=" + videoInValue + "ms:color=" + color;
	    		
	        	filterComplex += videoFade;
	    	}
	    	
	    	//Fade-out
	    	if (caseVideoFadeOut.isSelected())
	    	{
	    		if (filterComplex != "") filterComplex += ",";	
	    		
	    		long videoOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
	    		long videoStart = (long) FFPROBE.totalLength - videoOutValue;
	    		
	    		if (caseInAndOut.isSelected())
	    		{
	        		long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
	        		long totalOut = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
	        		 
	        		if (VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
	        		{
		        		videoStart = (totalOut - totalIn) - videoOutValue;
	        		}
	        		else //Remove mode
	        			videoStart = FFPROBE.totalLength - (totalOut - totalIn) - videoOutValue;
	    		}
	    		else if (caseEnableSequence.isSelected())
	    		{
	    			videoOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / Integer.parseInt(caseSequenceFPS.getSelectedItem().toString().replace(",", "."))));
		    		videoStart = (long) ((float) ((float) 1000 / Integer.parseInt(caseSequenceFPS.getSelectedItem().toString().replace(",", "."))) * liste.getSize()) - videoOutValue;
	    		}
	    		else if (Settings.btnSetBab.isSelected())
	    		{
	    			videoOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
		    		videoStart = (long) FunctionUtils.mergeDuration - videoOutValue;
	    		}
	    		
	    		String color = "black";
				if (lblFadeOutColor.getText().equals(language.getProperty("white")))
					color = "white";
	    		
	    		String videoFade = "fade=out:st=" + videoStart + "ms:d=" + videoOutValue + "ms:color=" + color;
	    		
	    		filterComplex += videoFade;
	    	}
		}
		
		return filterComplex;
	}
	
	public static String setAudioFadeIn() {
		
		String audioFilter = "";
		
		//Fade-in
		if (grpTransitions.isVisible() && caseAudioFadeIn.isSelected())
    	{ 
    		long audioInValue = (long) (Integer.parseInt(spinnerAudioFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
			
			audioFilter += "afade=in:st=0ms:d=" + audioInValue + "ms";
    	}    
        
		return audioFilter;
	}
	
	public static String setAudioFadeOut() {
		
		String audioFilter = "";

    	//Fade-out
		if (grpTransitions.isVisible() && caseAudioFadeOut.isSelected())
    	{
    		long audioOutValue = (long) (Integer.parseInt(spinnerAudioFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
    		long audioStart =  (long) FFPROBE.totalLength - audioOutValue;
    		
    		if (caseInAndOut.isSelected())
			{
				long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
				long totalOut = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
				 
				if (VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
				{
					audioStart = (totalOut - totalIn) - audioOutValue;
				}
				else //Remove mode
					audioStart = FFPROBE.totalLength - (totalOut - totalIn) - audioOutValue;
			}
    		else if (Settings.btnSetBab.isSelected())
    		{
    			audioOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
    			audioStart = (long) FunctionUtils.mergeDuration - audioOutValue;
    		}
    		
    		audioFilter += "afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms";
    	}
        
		return audioFilter;
	}
	
	public static String setAudioSpeed() {
		
		String audioFilter = "";
		
		if (grpTransitions.isVisible() || grpAudio.isVisible())
		{
	    	//Audio Speed				        
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed"))
			|| comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse")))
			|| grpImageSequence.isVisible() && caseEnableSequence.isSelected()) 
	        {
	        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
	            
	        	if (caseEnableSequence.isSelected())
	        	{
	        		newFPS = Float.parseFloat((caseSequenceFPS.getSelectedItem().toString()).replace(",", "."));
	        	}
	        	
	        	float value = (float) (newFPS/ FFPROBE.currentFPS);
	        	
	        	if (value < 0.5f || value > 2.0f)
	        	{
	        		return " -an";
	        	}
	        	else
	        	{
	        		audioFilter = "atempo=" + value;      
	        	}
	        }
			else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
			{
				return " -an";
			}
			else if (caseConvertAudioFramerate.isSelected())     
	        {
	        	float AudioFPSIn = Float.parseFloat((comboAudioIn.getSelectedItem().toString()).replace(",", "."));
	        	float AudioFPSOut = Float.parseFloat((comboAudioOut.getSelectedItem().toString()).replace(",", "."));
	        	float value = (float) (AudioFPSOut / AudioFPSIn);
	        	
	        	audioFilter += "atempo=" + value;	
	        }
		}
        
		return audioFilter;
	}
	
}
