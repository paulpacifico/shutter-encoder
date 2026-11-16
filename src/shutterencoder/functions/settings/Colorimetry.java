/*******************************************************************************************
* Copyright (C) 2027 PACIFICO PAUL
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
import shutterencoder.library.FFprobe;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.videoplayer.VideoPlayerUI;

public class Colorimetry extends Main {
	
	public static String setLevels(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayerUI.fullscreenPlayer) && caseLevels.isSelected())
		{			
			String input = comboInLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");
			String output = comboOutLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");
			
			if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
			{
				filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "range=" + output);
			}
			else
			{
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "scale=in_range=" + input + ":out_range=" + output;	
			}				
		}

		return filterComplex;
	}
	
	public static String setLUT(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayerUI.fullscreenPlayer) && caseLUTs.isSelected())
		{			
			String pathToLuts;
			if (System.getProperty("os.name").contains("Windows"))
			{
				pathToLuts = "LUTs/";
			}
			else
			{
				pathToLuts = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
				pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
			}
			
			if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
			{				
				filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "lut=" + pathToLuts + Main.comboLUTs.getSelectedItem().toString() + ":peak_detect=0");	
			}
			else
			{		
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "lut3d=file=" + pathToLuts + Main.comboLUTs.getSelectedItem().toString();	
			}
		}
		
		return filterComplex;
	}
	
	public static String setColormatrix(String filterComplex) {
		
		if ((grpColorimetry.isVisible() || VideoPlayerUI.fullscreenPlayer) && caseColormatrix.isSelected())
		{
			if (comboInColormatrix.getSelectedItem().equals("HDR"))
			{	
				if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
				{
					filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "tonemapping=hable:colorspace=bt709:color_primaries=bt709:color_trc=bt709");
				}
				else
				{
					if (filterComplex != "") filterComplex += ",";
					
					filterComplex += "setparams=color_primaries=bt2020:color_trc=smpte2084:colorspace=bt2020nc,zscale=t=linear:npl=100,format=gbrpf32le,zscale=p=bt709,tonemap=tonemap=hable:desat=0,zscale=t=bt709:m=bt709:range=tv";	
				}
			}
			else
			{
				if (comboInColormatrix.getSelectedItem().toString().equals("Linear"))
				{
					String transfer = "";
					switch (comboOutColormatrix.getSelectedIndex())
					{
						case 0:
							transfer = "bt470bg";
							break;
						case 1:
							transfer = "bt709";
							break;							
						default:
							transfer = "bt2020-10";
							break;
					}
					
					if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
					{
					    filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "range=full:color_trc=" + transfer);
					}
				    else
				    {
				    	if (filterComplex != "") filterComplex += ",";
				    	
				    	String format = FFprobe.hasAlpha ? "format=gbrapf32le," : "format=gbrpf32le,";
					
				    	filterComplex += format + "zscale=rangein=full:range=full:transferin=linear:transfer=" + transfer;
				    }
				}
				else
				{
					String input = comboInColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("bt601", "smpte170m");
					String output = comboOutColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("bt601", "smpte170m");
					
					if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
					{
					    filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, getLibplaceboColorspaces(comboOutColormatrix.getSelectedItem().toString()));
					}
				    else
				    {
				    	if (filterComplex != "") filterComplex += ",";
				    	
				    	filterComplex += "colorspace=iall=" + input + ":all=" + output;
				    }
				}
			}
		}

		return filterComplex;
	}
	
	private static String getLibplaceboColorspaces(String color)
	{
	    color = color.replace("Rec. ", "");
	    
	    switch (color)
	    {
	        case "601":
	            return "colorspace=smpte170m:color_primaries=smpte170m:color_trc=smpte170m";

	        case "709":
	            return "colorspace=bt709:color_primaries=bt709:color_trc=bt709";

	        case "2020":
	            return "colorspace=bt2020nc:color_primaries=bt2020:color_trc=bt2020-10";

	        default:
	            return "colorspace=bt709:color_primaries=bt709:color_trc=bt709";
	    }
	}
		
	public static String setColorspace() {
		
		if (grpColorimetry.isVisible() && caseColorspace.isSelected())
		{
			if (comboColorspace.getSelectedItem().toString().contains("Rec. 709"))
			{
				return " -color_primaries bt709 -color_trc bt709 -colorspace bt709";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 PQ"))
			{
				return " -color_primaries bt2020 -color_trc smpte2084 -colorspace bt2020nc";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 HLG"))
			{
				return " -color_primaries bt2020 -color_trc arib-std-b67 -colorspace bt2020nc";
			}
		}
		
		return "";
	}
	
	public static String setMetadata(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseColorspace.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
						
			if (comboColorspace.getSelectedItem().toString().contains("Rec. 709"))
			{
				filterComplex += "setparams=color_primaries=bt709:color_trc=bt709:colorspace=bt709";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 PQ"))
			{
				filterComplex += "setparams=color_primaries=bt2020:color_trc=smpte2084:colorspace=bt2020nc";
			}
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 HLG"))
			{
				filterComplex += "setparams=color_primaries=bt2020:color_trc=arib-std-b67:colorspace=bt2020nc";
			}
		}
		
		return filterComplex;
	}
	
	public static String setInputCodec(String extension) {
		
		if (extension.toLowerCase().equals(".exr"))
		{
			return " -apply_trc iec61966_2_1";
		}
		else if (FFprobe.videoCodec != null)
		{
			//Preserve the alpha channel
			if (FFprobe.videoCodec.contains("vp9") && FFprobe.hasAlpha)
			{
				return " -c:v libvpx-vp9";
			}
			else if (FFprobe.videoCodec.contains("vp8"))
			{
				return " -c:v libvpx";
			}
		}
		
		return "";	
	}
}
