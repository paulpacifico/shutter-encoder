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

import java.io.File;

import application.ColorImage;
import application.Shutter;
import library.FFMPEG;
import library.FFPROBE;
import library.MKVMERGE;

public class Colorimetry extends Shutter {

	public static String setColor(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseColor.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";
			
			//Important
			ColorImage.setEQ(true);
			
			filterComplex += finalEQ;	
		}

		return filterComplex;
	}
	
	public static String setLevels(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseLevels.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "scale=in_range=" + comboInLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full") + ":out_range=" + comboOutLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");		
		}

		return filterComplex;
	}
	
	public static String setColormatrix(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseColormatrix.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
			
			if (comboInColormatrix.getSelectedItem().equals("HDR"))
			{		
				String pathToLuts;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
					pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/HDR-to-SDR.cube";
				}
				else
					pathToLuts = "LUTs/HDR-to-SDR.cube";

				filterComplex += "lut3d=file=" + pathToLuts;	
			}
			else
				filterComplex += "colorspace=iall=" + Shutter.comboInColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625") + ":all=" + Shutter.comboOutColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625");
		}
		
		return filterComplex;
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

	public static String setEXRGamma(String extension) {
		
		if (extension.toLowerCase().equals(".exr"))
		{
			return " -apply_trc iec61966_2_1";
		}
		
		return "";	
	}
	
	public static String setLUT(String filterComplex) {
		
		if (grpColorimetry.isVisible() && caseLUTs.isSelected())
		{			
			String pathToLuts;
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			{
				pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
				pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
			}
			else
				pathToLuts = "LUTs/";
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "lut3d=file=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString();	
		}
		
		return filterComplex;
	}
	
	public static void setHDR(String fileName, File fileOut) throws InterruptedException {
		
		if (grpColorimetry.isVisible() && caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR") && FFMPEG.error == false)
		{
			lblCurrentEncoding.setText(fileName);
			
			File HDRmkv = fileOut;
			File tempHDR = new File(fileOut.toString().replace(comboFilter.getSelectedItem().toString(), "_HDR" + comboFilter.getSelectedItem().toString()));
			fileOut.renameTo(tempHDR);	
			fileOut = HDRmkv;

			String PQorHLG = "16";
			if (comboColorspace.getSelectedItem().toString().contains("HLG"))
				PQorHLG = "18";
			
			String cmd = " --colour-matrix 0:9 --colour-range 0:1 --colour-transfer-characteristics 0:" + PQorHLG + " --colour-primaries 0:9 --max-luminance 0:" + (int) FFPROBE.HDRmax + " --min-luminance 0:" + FFPROBE.HDRmin + " --chromaticity-coordinates 0:0.68,0.32,0.265,0.690,0.15,0.06 --white-colour-coordinates 0:0.3127,0.3290";
			MKVMERGE.run(cmd + " " + '"' + tempHDR + '"' + " -o " + '"'  + HDRmkv + '"');	
			
			do
			{
				Thread.sleep(100);
			}
			while(MKVMERGE.runProcess.isAlive());
			
			if (MKVMERGE.error == false)
				tempHDR.delete();
			else
				FFMPEG.error = true;
		}
	}
}
