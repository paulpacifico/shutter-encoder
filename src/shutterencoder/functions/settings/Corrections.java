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

import java.io.File;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.functions.utils.Libplacebo;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.library.LibraryUtils;
import shutterencoder.ui.main.Main;

public class Corrections extends Main {

	public static File vidstab = null;
	
	public static String setStabilisation(String filterComplex, File file, String fichier, String concat) throws InterruptedException {
				
		if (Main.caseStabilisation.isSelected())
		{
			if (System.getProperty("os.name").contains("Windows"))
			{
				vidstab = new File("vidstab.trf");
			}
			else					    		
				vidstab = new File(Main.dirTemp + "vidstab.trf");		
			
			lblCurrentEncoding.setText(Main.language.getProperty("analyzeOf") + " " + fichier);
			
			//Analyse du fichier
			String cmd;
			if (System.getProperty("os.name").contains("Windows"))
				cmd =  " -an -vf vidstabdetect=result=" + vidstab.toString() + " -y -f null -" + '"';					
			else
				cmd =  " -an -vf vidstabdetect=result=" + vidstab.toString() + " -y -f null -";	
			
			FFmpeg.run(InputAndOutput.inPoint + concat + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd);		
			try {
				FFmpeg.runProcess.join();
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}				
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "vidstabtransform=input=" + vidstab.toString();
			
			lblCurrentEncoding.setText(fichier);
		}
		
		return filterComplex;
	}
	
	public static String setDeflicker(String filterComplex) {
		
		if (Main.caseDeflicker.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
		
			float framerate = FFprobe.currentFPS;
			if (caseEnableSequence.isSelected())
			{
				framerate = Float.parseFloat(caseSequenceFPS.getSelectedItem().toString());
			}
			
			filterComplex += "split[a][b];[b]setpts=PTS-STARTPTS+" + (1 / framerate) + "/TB,format=rgba,colorchannelmixer=aa=0.5[deflicker];[a][deflicker]overlay=shortest=1";		
		}
		
		return filterComplex;
	}
		
	public static String setDeband(String filterComplex) {
		
		if (Main.caseBanding.isSelected())
		{
			if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
			{
				filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "deband=1:deband_radius=32");
			}
			else
			{
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "deband=r=32";
			}
		}	
		
		return filterComplex;
	}
	
	public static String setLimiter(String filterComplex) {
		
		if (Main.caseLimiter.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";

			if (FFprobe.imageDepth == 8)
				filterComplex += "limiter=16:235";
			else if (FFprobe.imageDepth == 10)
				filterComplex += "limiter=64:940";
			else if (FFprobe.imageDepth == 12)
				filterComplex += "limiter=256:3760";
			else if (FFprobe.imageDepth == 16)
				filterComplex += "limiter=4096:60160";
		}	
		
		return filterComplex;
	}
	
	public static String setDetails(String filterComplex) {
		
		if (Main.caseDetails.isSelected())
		{
			float value = (0 - (float) Main.sliderDetails.getValue() / 10);
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "smartblur=1.0:" + value;
		}	
		
		return filterComplex;
	}
	
	public static String setGrain(String filterComplex) {

	    if (Main.caseAddGrain.isSelected())
	    {
	        int value = Main.sliderAddGrain.getValue();

	        if (filterComplex != "") filterComplex += ",";

	        int grainAmount = (int)(value * 0.2);
	        
	        filterComplex += "noise=c0s=" + grainAmount + ":c1s=0:c2s=0:c3s=0:c0f=t+u";
	    }

	    return filterComplex;
	}
	
	public static String setDenoiser(String filterComplex, boolean noGPU) {
		
		//Checking if last filter is GPU accelerated
		boolean filterGPU = FunctionUtils.checkPreviousFilterVulkan(filterComplex);
		
		if (Main.caseDenoise.isSelected())
		{
			int value = Main.sliderDenoise.getValue();
			int r = 2;
			if (value >= 4 && value < 8) r = 4;
			else if (value >= 8) r = 6;
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "nlmeans=s=" + value + ":p=3:r=" + r + ":pc=0";
			
			//Format
			String bitDepth = FFprobe.imageDepth == 10 ? "p010" : "nv12";
						
			//GPU filter
			if (noGPU == false && filterGPU && filterComplex.contains("nlmeans"))
			{
				if (LibraryUtils.autoVULKAN || (LibraryUtils.vulkanAvailable && Main.comboGPUFilter.getSelectedItem().toString().equals("vulkan")))
				{
					filterComplex = filterComplex.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
					
					filterComplex = filterComplex.replace("nlmeans", "nlmeans_vulkan").replace(":pc=0", "") + ",hwdownload,format=" + bitDepth;
				}		
			}
		}
		
		return filterComplex;
	}
	
	public static String setSmoothExposure(String filterComplex) {
		
		if (Main.caseSmoothExposure.isSelected())
		{
			int value = Main.sliderSmoothExposure.getValue();
			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "deflicker=s=" + Math.ceil((128 * value) / 100 + 1);	
		}
		
		return filterComplex;
	}

}
