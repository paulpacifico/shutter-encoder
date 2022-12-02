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

import application.Settings;
import application.Shutter;
import application.VideoPlayer;
import library.FFPROBE;
import library.FFMPEG;

public class Image extends Shutter {

	public static String setCrop(String filterComplex) {		
		
		if (grpResolution.isVisible() || grpImageSequence.isVisible() || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
		{	    	
	    	if (caseInAndOut.isSelected() && VideoPlayer.caseEnableCrop.isSelected())
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
				
				int cropWidth = Math.round((float) Integer.parseInt(VideoPlayer.textCropWidth.getText()) / imageRatio);
				int cropHeight = Math.round((float) Integer.parseInt(VideoPlayer.textCropHeight.getText()) / imageRatio);
				int cropX = Math.round((float)Integer.parseInt(VideoPlayer.textCropPosX.getText()) / imageRatio);
				int cropY = Math.round((float)Integer.parseInt(VideoPlayer.textCropPosY.getText()) / imageRatio);

	    		filterComplex += "crop=" + cropWidth + ":" +  cropHeight + ":" + cropX + ":" + cropY;
			}
		}
    	
    	return filterComplex;
	}
	
	public static String setRotate(String filterComplex) {
		
		if (grpResolution.isVisible() || grpImageSequence.isVisible())
		{
			String rotate = "";
			if (caseRotate.isSelected()) 
			{
				String transpose = "";
				switch (comboRotate.getSelectedItem().toString()) {
				case "90":
					if (caseMiror.isSelected())
						transpose = "transpose=3";
					else
						transpose = "transpose=1";
					break;
				case "-90":
					if (caseMiror.isSelected())
						transpose = "transpose=0";
					else
						transpose = "transpose=2";
					break;
				case "180":
					if (caseMiror.isSelected())
						transpose = "transpose=1,transpose=1,hflip";
					else
						transpose = "transpose=1,transpose=1";
					break;
				}
	
				rotate = transpose;
			}
			else if (caseMiror.isSelected())
			{
				rotate = "hflip";
			}
						
			if (rotate != "")
			{
				if (filterComplex != "") filterComplex += ",";
		
				filterComplex += rotate;	
			}
		}

		return filterComplex;
	}
	
	public static String limitToFHD() {
		
		if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		{
			return " -s "+ comboResolution.getSelectedItem().toString();
		}
		else
			return " -s 1920x1080";
	}
	
	public static String setPad(String filterComplex, boolean limitToFHD) {	
		
		if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		{
			String i[] = FFPROBE.imageResolution.split("x");        
			String o[] = FFPROBE.imageResolution.split("x");
						
			if (comboResolution.getSelectedItem().toString().contains("%"))
			{
				double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
				
				o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * value));
				o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * value));
			}					
			else
				o = comboResolution.getSelectedItem().toString().split("x");
			
			int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);          	
        	int ow = Integer.parseInt(o[0]);
        	int oh = Integer.parseInt(o[1]);        	
        	float ir = (float) iw / ih;
        	float or = (float) ow / oh;
			
        	if (filterComplex != "") filterComplex += ",";
			
			if (lblPad.getText().equals(language.getProperty("lblCrop"))
			|| comboFonctions.getSelectedItem().toString().equals("JPEG")
			|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
			{
				if (comboResolution.getSelectedItem().toString().contains(":"))
		        {
		        	if (comboResolution.getSelectedItem().toString().contains("auto"))
		        	{
		        		o = comboResolution.getSelectedItem().toString().split(":");
		        		if (o[0].toString().equals("auto"))
		        			filterComplex = "scale=-1:" + o[1];
		        		else
		        			filterComplex = "scale="+o[0]+":-1";
		        	}
		        	else
		        	{
			            o = comboResolution.getSelectedItem().toString().split(":");
			    		float number =  (float) 1 / Integer.parseInt(o[0]);
			    		filterComplex = "scale=iw*" + number + ":ih*" + number;
		        	}
		        }
				else
				{					       	
		        	//Original sup. à la sortie
		        	if (iw > ow || ih > oh)
		        	{
		        		//Si la hauteur calculée est > à la hauteur de sortie
		        		if ( (float) ow / ir >= oh)
		        			filterComplex += "scale=" + ow + ":-1,crop=" + "'" + ow + ":" + oh + ":0:(ih-oh)*0.5" + "'";
		        		else
		        			filterComplex += "scale=-1:" + oh + ",crop=" + "'" + ow + ":" + oh + ":(iw-ow)*0.5:0" + "'";
		        	}
		        	else
		        		filterComplex += "scale=" + ow + ":" + oh;
				}
			}
			else
			{
				if (lblPad.getText().equals(language.getProperty("lblPad")) && ir != or)
				{
					filterComplex += "scale="+o[0]+":"+o[1]+":force_original_aspect_ratio=decrease,pad=" +o[0]+":"+o[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
				}
				else
					filterComplex += "scale="+o[0]+":"+o[1];	
			}
			
		}
		else if (limitToFHD)
		{
			String i[] = FFPROBE.imageResolution.split("x");    
			String o[] = "1920x1080".split("x");
			
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
			{
				o = comboResolution.getSelectedItem().toString().split("x");
			}
			
			if (filterComplex != "") filterComplex += ",";
			
			int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);          	
        	int ow = Integer.parseInt(o[0]);
        	int oh = Integer.parseInt(o[1]);        	
        	float ir = (float) iw / ih;
        	float or = (float) ow / oh;
			
			if (ir != or)
			{
				filterComplex += "scale="+o[0]+":"+o[1]+":force_original_aspect_ratio=decrease,pad=" +o[0]+":"+o[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
			}
			else
				filterComplex += "scale="+o[0]+":"+o[1];			
			
		}
		else if (comboFilter.getSelectedItem().toString().equals(".ico"))
		{
			filterComplex = "scale=256x256";
		}
		
		//GPU Scaling
		if (FFMPEG.isGPUCompatible && filterComplex.contains("scale"))
		{
			//Scaling
			String bitDepth = "nv12";
			if (FFPROBE.imageDepth == 10)
			{
				bitDepth = "p010";
			}			
			
			//Auto GPU selection
			boolean autoQSV = false;
			boolean autoCUDA = false;
			if (Settings.comboGPU.getSelectedItem().toString().equals("auto") && Settings.comboGPUFilter.getSelectedItem().toString().equals("auto"))
			{
				if (FFMPEG.cudaAvailable)
				{
					autoCUDA = true;
				}
				else if (FFMPEG.qsvAvailable)
				{
					autoQSV = true;
				}
			}
			
			if ((autoQSV || Settings.comboGPUFilter.getSelectedItem().toString().equals("qsv") && FFMPEG.isGPUCompatible) && filterComplex.contains("yadif") == false && filterComplex.contains("force_original_aspect_ratio") == false)
			{
				filterComplex = filterComplex.replace("scale", "scale_qsv");
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
			else if (autoCUDA || Settings.comboGPUFilter.getSelectedItem().toString().equals("cuda") && FFMPEG.isGPUCompatible)
			{
				filterComplex = filterComplex.replace("yadif", "yadif_cuda");			
				filterComplex = filterComplex.replace("scale", "scale_cuda");
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
		}
		
		return filterComplex;
	}
	
	public static String setDAR(String filterComplex) {
		
		if (grpResolution.isVisible() || grpImageSequence.isVisible())
		{
			if (caseForcerDAR.isSelected())
			{
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
			}
		}
    	
    	return filterComplex;
	}	
	
}
