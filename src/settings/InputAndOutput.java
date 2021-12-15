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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import application.Shutter;
import application.VideoPlayer;
import library.FFPROBE;

public class InputAndOutput extends Shutter {

	public static String inPoint = "";
	public static String outPoint = "";
	
	public static void getInputAndOutput() {
		
		if (grpInAndOut.isVisible())
		{
			if (caseInAndOut.isSelected())
			{		
				//Removing offset
				if (VideoPlayer.caseTcInterne.isSelected())
					VideoPlayer.caseTcInterne.doClick();
		
				int h = Integer.parseInt(VideoPlayer.caseInH.getText());
				int m = Integer.parseInt(VideoPlayer.caseInM.getText());
				int s = Integer.parseInt(VideoPlayer.caseInS.getText());
				int f = (int) (Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));	
				
				NumberFormat formatter = new DecimalFormat("00");
				NumberFormat formatFrame = new DecimalFormat("000");
					
				if (VideoPlayer.sliderIn.getValue() > 0)
		        {		        
					inPoint = " -ss " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f);
			    }
			    else
			        inPoint = "";
		
			        if (VideoPlayer.sliderOut.getValue() != VideoPlayer.sliderOut.getMaximum())
			        {
			        	if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().equals("JPEG"))
			        	{
				        	String frames[] = VideoPlayer.lblDuree.getText().split(" ");
				    		float outputFPS = FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
				    		
				    		outPoint = " -vframes " + Math.ceil(Integer.parseInt(frames[frames.length - 1]) / outputFPS);
			        	}
			        	else
			        		outPoint = " -t " + formatter.format(VideoPlayer.dureeHeures) + ":" + formatter.format(VideoPlayer.dureeMinutes) + ":" + formatter.format(VideoPlayer.dureeSecondes) + "." + formatFrame.format((int) (VideoPlayer.dureeImages * (1000 / FFPROBE.currentFPS)));
			        }
			        else
			        	outPoint = "";
			}
			else
			{
				inPoint = "";
				outPoint = "";
			}
		}
		else
		{
			inPoint = "";
			outPoint = "";
		}
	}

}
