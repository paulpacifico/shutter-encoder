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

package shutterencoder.functions.utils;

import shutterencoder.functions.settings.AdvancedFeatures;
import shutterencoder.functions.settings.BitratesAdjustement;
import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.functions.settings.Corrections;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.ui.main.Shutter;

public class Libplacebo extends Shutter {

	public static boolean useLibplaceboFilters = false;
	
	public static String getSetParamsInput(boolean deinterlace, String filterComplex)
	{		
		String params = "";
		String format = "";
		
		//scale filter is replaced to libplacebo scaling filter
		if (filterComplex != "")
		{
			format += ",";
		}
			
		if (deinterlace)
		{
			params += "field_mode=" + lblTFF.getText().toLowerCase(); //assume tff or bff from setparams
		}
		
		if (caseLevels.isSelected())
		{
			if (params != "") params += ":";
			
			params += "range=" + comboInLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");
		}
								
	    if (caseColormatrix.isSelected())
	    {	    	
	    	if (comboInColormatrix.getSelectedItem().toString().equals("Linear"))
	    	{
	    		if (params != "") params += ":";
	    		
	    		params += "color_trc=linear:range=full";
	    	}
	    	else
	    	{
		    	String color = comboInColormatrix.getSelectedItem().toString().replace("Rec. ", "");
		    	
		    	switch (color)
			    {
			        case "601":
			        	if (params != "") params += ":";
			        	params += "colorspace=smpte170m:color_primaries=smpte170m:color_trc=smpte170m";
			        	break;
			        case "709":
			        	if (params != "") params += ":";
			        	params += "colorspace=bt709:color_primaries=bt709:color_trc=bt709";
			        	break;
			        case "2020":
			        	if (params != "") params += ":";
			        	params += "colorspace=bt2020nc:color_primaries=bt2020:color_trc=bt2020-10";
			        	break;
			        case "HDR":
			        	if (params != "") params += ":";
			        	params += "colorspace=bt2020nc:color_primaries=bt2020:color_trc=smpte2084";
			        	break;
			    }
	    	}
	    }
	
	    if (params != "")
	    {
	    	params = "setparams=" + params + ",";
	    }
	    
	    if (caseLUTs.isSelected())
		{
			//Mandatory for correct lut display
			format += FFPROBE.hasAlpha ? "format=rgba64le," : "format=rgb48,";
		}
		
	    return format + params;
	}

	public static void getLibplaceboScore(boolean noGPU, boolean firstFilters) {
				
		if (noGPU || LibraryUtils.libplaceboAvailable == false)
		{
			Libplacebo.useLibplaceboFilters = false;			
			return;
		}
		
		Libplacebo.useLibplaceboFilters = true; //Allows to get the output from filters
		
		int score = 0;
	
		boolean progressiveOutput = false;
		if (comboResolution.getSelectedItem().toString().contains("AI") == false) //Deinterlacing is not done before upscaling
		{
			switch (comboFonctions.getSelectedItem().toString())
			{
				case "AV1":
				case "H.264":
				case "H.265":
				case "H.266":
				case "MJPEG":
				case "VP8":
				case "VP9":
				case "Theora":
				case "WMV":
				case "Xvid":
				case "DNxHR":
				case "MPEG-1":
				case "Blu-ray":
				case "DVD":
					
					progressiveOutput = true;
					break;
					
				case "DNxHD":
					
					switch (comboFilter.getSelectedItem().toString())
		            {
		            	case "36":
		            	case "75":
		            	case "240":
		            	case "365":
		            	case "365 X":
		            	case "90":
		            	case "115":
		            	case "175":
		            	case "175 X":
		            		
		            		progressiveOutput = true;          		
	            			break;
		            }
					
					break;
			}
		}
		
		/* For info using libplacebo filters:
		 * Deinterlace is faster only with yadif on Mac!
		 * Rotate is a bit slower but faster in addition on deinterlace
		 * Scale, Crop, Range are much faster on CPU
		 * Colorspace, Lut, Deband are faster with libplacebo
		 * Fps blending like rotate filter, is faster if chained with previous filters
		 */
		
		//Because scale crop range are faster on CPU we only use them if deinterlace is used
		if (firstFilters)
		{
			//Deinterlacing
			if (AdvancedFeatures.setDeinterlace(progressiveOutput, false, "").contains("libplacebo="))
			{
				if (System.getProperty("os.name").contains("Windows"))
				{
					score += 1; //Same speed for both filters
				}
				else
				{
					if (AdvancedFeatures.setDeinterlace(progressiveOutput, false, "").contains("deinterlace=bwdif"))
					{
						score += 1;
					}
					else // yadif deinterlacer which is faster with libplacebo
					{
						score += 3;
					}
				}
			}
	
			//Scale
			if (shutterencoder.functions.settings.Image.setScale("", false, false).contains("libplacebo"))
			{
				score += 0; //Faster on CPU
			}
			
			//Rotate
			if (shutterencoder.functions.settings.Image.setRotate("", false).contains("libplacebo"))
			{
				score += 1;
			}
			
			//Crop
			if (shutterencoder.functions.settings.Image.setCrop("", null).contains("libplacebo")
			|| BitratesAdjustement.setCrop("").contains("libplacebo"))
			{
				score += 0; //Faster on CPU
			}
		}
		else //Reset the score to 0 to re-enable libplacebo if these filters are used
		{
			//Colormatrix
			if (Colorimetry.setColormatrix("").contains("libplacebo") && Colorimetry.setColormatrix("").contains("range"))
			{
				if (System.getProperty("os.name").contains("Windows"))
				{
					score += 1;
				}
				else
					score += 0; //Faster on CPU
			}
			
			if (Colorimetry.setColormatrix("").contains("libplacebo") && Colorimetry.setColormatrix("").contains("colorspace"))
			{
				score += 3;
			}
			
			if (Colorimetry.setColormatrix("").contains("libplacebo") && Colorimetry.setColormatrix("").contains("tonemapping"))
			{
				score += 3;
			}
			
			//LUTs
			if (Colorimetry.setLUT("").contains("libplacebo"))
			{
				score += 3;
			}
			
			//Deband
			if (Corrections.setDeband("").contains("libplacebo"))
			{
				score += 3;
			}
			
			//Frame blending
			if (AdvancedFeatures.setConform("").contains("libplacebo"))
			{
				score += 1;
			}
			
			//Levels
			if (Colorimetry.setLevels("").contains("libplacebo"))
			{
				if (System.getProperty("os.name").contains("Windows"))
				{
					score += 1;
				}
				else
					score += 0; //Faster on CPU
			}
		}
	
		if (score >= 3)
		{
			Libplacebo.useLibplaceboFilters = true;
		}
		else
			Libplacebo.useLibplaceboFilters = false;
	}

	public static boolean checkLibplaceboFilter(String filterComplex) {
		
		String format = FFPROBE.hasAlpha ? ",format=rgba64le" : ",format=rgb48";
	
		if (filterComplex.contains("libplacebo") == false
		|| filterComplex.substring(filterComplex.indexOf("libplacebo")).replace(format, "").contains(",") == false)
		{
			return true;
		}
		
		return false;
	}

	public static String setLibplaceboFilter(String filterComplex, String newLibplaceboFilter) {
				
		if (filterComplex.contains("libplacebo"))
		{
			//Move the format filter forward after all libplacebo filters to avoid CPU fallback
			String format = FFPROBE.hasAlpha ? ",format=rgba64le" : ",format=rgb48";
			
			if (filterComplex.endsWith(format))
			{
				return filterComplex.substring(0, filterComplex.length() - (format.length())) + ":" + newLibplaceboFilter + format;
			}
			else
				return filterComplex + ":" + newLibplaceboFilter;		
		}
		else
		{
			String disableLinear = "disable_linear=1:";
			if (caseColormatrix.isSelected() && (comboInColormatrix.getSelectedItem().equals("HDR") || comboInColormatrix.getSelectedItem().toString().equals("Linear")))
			{
				disableLinear = "";
			}
			
			return filterComplex + getSetParamsInput(newLibplaceboFilter.contains("deinterlace"), filterComplex) + "libplacebo=" + disableLinear + newLibplaceboFilter;
		}
	}
}
