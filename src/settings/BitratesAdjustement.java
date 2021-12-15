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
import library.FFPROBE;

public class BitratesAdjustement extends Shutter {
	
	public static boolean DVD2Pass;
	public static int DVDBitrate;
	
	public static String setResolution() {		
		
		String resolution  = comboH264Taille.getSelectedItem().toString();		
        if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) && caseRognage.isSelected() == false)
        {
        	return "";
        }
        else if (caseRognage.isSelected() && lblPad.getText().equals(language.getProperty("lblPad")))
        {
        	String s[] = comboH264Taille.getSelectedItem().toString().split("x");
        	String i[] = FFPROBE.imageResolution.split("x");
        	int ow = Integer.parseInt(s[0]);
        	int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);        	
        	
        	return " -s " + s[0] + "x" + (int) ((float)ow/((float)iw/ih));	
        }
        else
        	return " -s " + resolution;	
	}
	
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
		
		if (caseRognage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
	
			filterComplex += "crop=" + FFPROBE.cropHeight + ":" + FFPROBE.cropWidth + ":" + FFPROBE.cropPixelsWidth + ":" + FFPROBE.cropPixelsHeight;
		}
    	
    	if (caseRognerImage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
			
    		filterComplex += Shutter.cropFinal;
		}
    	
    	return filterComplex;
	}
	
}
