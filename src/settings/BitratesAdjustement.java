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
import application.VideoPlayer;
import library.FFPROBE;

public class BitratesAdjustement extends Shutter {
	
	public static boolean DVD2Pass;
	public static int DVDBitrate;
	
	public static String setResolution() {		
				
        if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")))
        {
        	return "";
        }
        else
        {
        	String s[] = FFPROBE.imageResolution.split("x");
    		
        	if (comboH264Taille.getSelectedItem().toString().contains("%"))
			{
				double value = (double) Integer.parseInt(comboH264Taille.getSelectedItem().toString().replace("%", "")) / 100;
				
				return " -s " + (int) (Integer.parseInt(s[0]) * value) + "x" + (int) (Integer.parseInt(s[1]) * value);
			}
			else										
				return " -s " + comboH264Taille.getSelectedItem().toString();
        }
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
		    	
    	if (VideoPlayer.caseEnableCrop.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
			
    		filterComplex += Shutter.croppingValues;
		}
    	
    	return filterComplex;
	}
	
}
