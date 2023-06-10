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
import library.FFPROBE;

public class BitratesAdjustement extends Shutter {
	
	public static boolean DVD2Pass;
	public static int DVDBitrate;
	
	public static String setPass(String outputFile) {
						
		if (case2pass.isSelected() || comboFonctions.getSelectedItem().toString().equals("DVD") && DVDBitrate <= 6000)			
		{
			DVD2Pass = true;
			return " -pass 1 -passlogfile " + '"' + outputFile + '"';
		}
		else
			DVD2Pass = false;

		return "";
	}
	
	public static String setCrop(String filterComplex) {		
		    	
    	if (Shutter.caseEnableCrop.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
			
			//IMPORTANT
			float imageRatio = 1.0f;
			
			int ow = FFPROBE.imageWidth;  
			
			if (Shutter.comboFonctions.getSelectedItem().toString().equals("DVD") == false && Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false)
			{
				String s[] = Shutter.comboResolution.getSelectedItem().toString().split("x");
				
	        	ow = Integer.parseInt(s[0]);   
	        	
				imageRatio = (float) FFPROBE.imageWidth / ow;
			}
			
			int cropWidth = Math.round((float) Integer.parseInt(Shutter.textCropWidth.getText()) / imageRatio);
			int cropHeight = Math.round((float) Integer.parseInt(Shutter.textCropHeight.getText()) / imageRatio);
			int cropX = Math.round((float)Integer.parseInt(Shutter.textCropPosX.getText()) / imageRatio);
			int cropY = Math.round((float)Integer.parseInt(Shutter.textCropPosY.getText()) / imageRatio);

    		filterComplex += "crop=" + cropWidth + ":" +  cropHeight + ":" + cropX + ":" + cropY;
		}
    	
    	return filterComplex;
	}
	
}
