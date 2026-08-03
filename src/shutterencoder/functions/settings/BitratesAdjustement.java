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

import shutterencoder.functions.utils.Libplacebo;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;

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
			//IMPORTANT
			float imageRatio = 1.0f;
			
			int ow = FFPROBE.imageWidth;  
			
			if (Shutter.comboFonctions.getSelectedItem().toString().equals("DVD") == false && Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false)
			{
				if (comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
					{
						ow = (int) Math.round(ow * 2);
					}
					else
					{
						ow = (int) Math.round(ow * 4);
					}
				}
				else
				{
					String s[] = comboResolution.getSelectedItem().toString().split("x");					
		        	ow = Integer.parseInt(s[0]); 
				}
				
				imageRatio = (float) FFPROBE.imageWidth / ow;
			}
			
			int cropWidth = Math.round((float) Integer.parseInt(Shutter.textCropWidth.getText()) / imageRatio);
			int cropHeight = Math.round((float) Integer.parseInt(Shutter.textCropHeight.getText()) / imageRatio);
			int cropX = Math.round((float)Integer.parseInt(Shutter.textCropPosX.getText()) / imageRatio);
			int cropY = Math.round((float)Integer.parseInt(Shutter.textCropPosY.getText()) / imageRatio);

			if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
			{
				String crop_x = "crop_x=" + cropX;
				if (caseMiror.isSelected() && caseRotate.isSelected() == false
				|| caseMiror.isSelected() == false && caseRotate.isSelected() && comboRotate.getSelectedItem().toString().equals("180"))
				{
					crop_x = "crop_x=iw-" + cropWidth + "-" + cropX;
				}
				
				String crop_y = "crop_y=" + cropY;
				if (caseRotate.isSelected() && comboRotate.getSelectedItem().toString().equals("180"))
				{
					crop_y = "crop_y=ih-" + cropHeight + "-" + cropY;
				}
				
				filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "crop_w=" + cropWidth + ":crop_h=" +  cropHeight + ":" + crop_x + ":" + crop_y + ":w=" + cropWidth + ":h=" + cropHeight + ":reset_sar=1");
			}
			else
			{
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "crop=" + cropWidth + ":" +  cropHeight + ":" + cropX + ":" + cropY;
			}
		}
    	
    	return filterComplex;
	}
	
}
