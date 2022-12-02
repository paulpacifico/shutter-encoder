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

public class ImageSequence extends Shutter {
		
	public static String setBlend(String filterComplex) {
		
		if (grpImageSequence.isVisible() && caseBlend.isSelected())
		{			
			int value = sliderBlend.getValue();
			StringBuilder blend = new StringBuilder();
			for (int i = 0 ; i < value ; i++)
			{
				blend.append("tblend=all_mode=average,");
			}	
			
			blend.append("setpts=" + (float) 25 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) + "*PTS");
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += blend;	
		}
		
		return filterComplex;
	}
	
	public static String setMotionBlur(String filterComplex) {
		
		if (grpImageSequence.isVisible() && caseMotionBlur.isSelected())
		{			
			float fps = Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) * 2;
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "minterpolate=fps=" + fps + ",tblend=all_mode=average,framestep=2";	
		}
		
		return filterComplex;
	}
	
}
