/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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

import application.Shutter;
import application.VideoPlayer;
import functions.VideoEncoders;
import library.FFPROBE;
import library.FFMPEG;

public class Image extends Shutter {

	public static String setCrop(String filterComplex, File file) {		
		
		if (grpResolution.isVisible() || grpImageSequence.isVisible() || comboFonctions.getSelectedItem().toString().equals("Blu-ray"))
		{	    	
	    	if (caseEnableCrop.isSelected())
			{	    		
	    		if (comboPreset.getSelectedIndex() == 1)
	    		{
	    			FFMPEG.setCropDetect(file);	  
	    		}
	    		
				if (filterComplex != "")
					filterComplex += "[w];[w]";

				float imageRatio = 1.0f;
				
				int ow = FFPROBE.imageWidth;  
				
				if (comboFonctions.getSelectedItem().toString().equals("DVD") == false && comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					if (comboResolution.getSelectedItem().toString().contains("%"))
					{
						double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
						
						ow = (int) Math.round(ow * value);
					}					
					else if (comboResolution.getSelectedItem().toString().contains("x"))
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
					}
					else if (comboResolution.getSelectedItem().toString().contains(":"))
					{
						String s[] = comboResolution.getSelectedItem().toString().split(":");
						
						if (s[0].equals("auto"))
						{
							ow = Math.round(FFPROBE.imageHeight / Integer.parseInt(s[1]));
						}
						else if (s[1].equals("auto"))
						{
							ow = Math.round(FFPROBE.imageWidth / Integer.parseInt(s[0]));
						}
						else //ratio like 4:1 etc...
						{
							ow = Integer.parseInt(s[1]);
						}
					}

		        	if (VideoEncoders.setScalingFirst())
					{
		        		imageRatio = (float) FFPROBE.imageWidth / ow;
					}
				}
				
				int cropWidth = Math.round((float) Integer.parseInt(Shutter.textCropWidth.getText()) / imageRatio);
				int cropHeight = Math.round((float) Integer.parseInt(Shutter.textCropHeight.getText()) / imageRatio);
				int cropX = Math.round((float)Integer.parseInt(Shutter.textCropPosX.getText()) / imageRatio);
				int cropY = Math.round((float)Integer.parseInt(Shutter.textCropPosY.getText()) / imageRatio);

	    		filterComplex += "crop=" + cropWidth + ":" +  cropHeight + ":" + cropX + ":" + cropY;
			}
		}
    	
    	return filterComplex;
	}
	
	public static String setRotate(String filterComplex) {
		
		if (grpResolution.isVisible() || grpImageSequence.isVisible() || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) || VideoPlayer.fullscreenPlayer)
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
	
	public static String setScale(String filterComplex, boolean limitToFHD) {	
		
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
			else if (comboResolution.getSelectedItem().toString().contains("x"))
			{
				if (comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 2));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 2));
					}
					else
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 4));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 4));
					}
				}
				else
					o = comboResolution.getSelectedItem().toString().split("x");
			}
			else if (comboResolution.getSelectedItem().toString().contains(":"))
			{
				o = comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
				
				int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);          	
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);        	
	        	float ir = (float) iw / ih;
						        	
				if (o[0].toString().equals("1")) // = auto
				{
					o[0] = String.valueOf((int) Math.round((float) oh * ir));
				}
				else if (!(o[0].toString().contains("-") || o[1].toString().contains("-")))
        		{
        			o[1] = String.valueOf((int) Math.round((float) ow / ir));
        		}
			}
			
			int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);          	
        	int ow = Integer.parseInt(o[0]);
        	int oh = Integer.parseInt(o[1]);        	
        	float ir = (float) iw / ih;
        	float or = (float) ow / oh;
        	
        	boolean upscale = true;
    		if (btnNoUpscale.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false && comboResolution.getSelectedItem().toString().contains("%") == false)
    		{
    			if (iw < ow || ih < oh)
    			{
    				upscale = false;
    			}
    		}
    		
			if (upscale)
			{
	        	if (filterComplex != "") filterComplex += ",";
				
				if (lblPad.getText().equals(language.getProperty("lblCrop")) && lblPad.isVisible()
				|| ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().contains("JPEG")) && comboResolution.getSelectedItem().toString().contains(":") && comboResolution.getSelectedItem().toString().contains("auto") == false))
				{
					if (comboResolution.getSelectedItem().toString().contains(":"))
			        {
			        	if (comboResolution.getSelectedItem().toString().contains("auto"))
			        	{
			        		o = comboResolution.getSelectedItem().toString().split(":");
			        		if (o[0].toString().equals("auto"))
			        			filterComplex += "scale=-1:" + o[1];
			        		else
			        			filterComplex += "scale="+o[0]+":-1";
			        	}
			        	// Negative scale makes sure that the auto set dimension is divisible, fixes "Crop"
						// ex: -2:480 means 480h with the closes width matching the aspect
						else if (comboResolution.getSelectedItem().toString().contains("-")) {
							o = comboResolution.getSelectedItem().toString().split(":");
							filterComplex += "scale=" + o[0] + ":" + o[1];
						}
			        	else
			        	{
				            o = comboResolution.getSelectedItem().toString().split(":");
				    		float number =  (float) 1 / Integer.parseInt(o[1]);
				    		filterComplex += "scale=iw*" + number + ":ih*" + number;
			        	}
			        }
					else
					{					       	
			        	//Source > to output
			        	if (iw > ow || ih > oh)
			        	{
			        		//Si la hauteur calculée est > à la hauteur de sortie
			        		if ( (float) ow / ir >= oh)
			        			filterComplex += "scale=" + ow + ":-1";
			        		else
			        			filterComplex += "scale=-1:" + oh;
			        	}
			        	else
			        		filterComplex += "scale=" + ow + ":" + oh;
					}
				}
				else
				{
					if (lblPad.getText().equals(language.getProperty("lblPad")) && ir != or && lblPad.isVisible())
					{
						filterComplex += "scale="+o[0]+":"+o[1]+":force_original_aspect_ratio=decrease";
					}
					else
						filterComplex += "scale="+o[0]+":"+o[1];	
				}
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
        	
        	boolean upscale = true;
    		if (btnNoUpscale.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false && comboResolution.getSelectedItem().toString().contains("%") == false)
    		{
    			if (iw < ow || ih < oh)
    			{
    				upscale = false;
    			}
    		}
    		
			if (upscale)
			{
				if (ir != or)
				{
					filterComplex += "scale="+o[0]+":"+o[1]+":force_original_aspect_ratio=decrease";
				}
				else
					filterComplex += "scale="+o[0]+":"+o[1];
			}
			
		}
		else if (comboFilter.getSelectedItem().toString().equals(".ico"))
		{
			filterComplex += "scale=256:256";
		}
				
		//GPU Scaling
		if (FFMPEG.isGPUCompatible && filterComplex.contains("scale=")
		&& comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false
		&& (FFPROBE.videoCodec.contains("vp9") == false || (FFPROBE.videoCodec.contains("vp9") && FFPROBE.hasAlpha == false))
		&& FFPROBE.videoCodec.contains("vp8") == false
		&& VideoPlayer.mouseIsPressed == false)
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
			boolean autoAMF = false;
			boolean autoVIDEOTOOLBOX = false;
			boolean autoVULKAN = false;			
			if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals("auto") && Shutter.comboGPUFilter.getSelectedItem().toString().equals("auto"))
			{
				if (FFMPEG.cudaAvailable && (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					autoCUDA = true;
				}
				else if (FFMPEG.amfAvailable && (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					autoAMF = true;
				}
				else if (FFMPEG.qsvAvailable && (comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					autoQSV = true;
				}
				else if (FFMPEG.videotoolboxAvailable && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					autoVIDEOTOOLBOX = true;
				}
				else if (FFMPEG.vulkanAvailable && (comboAccel.getSelectedItem().equals("Vulkan Video") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
				{
					autoVULKAN = true;
				}
			}
			
			if (autoCUDA || Shutter.comboGPUFilter.getSelectedItem().toString().equals("cuda"))
			{
				if (caseForcerDesentrelacement.isSelected())
				{
					filterComplex = filterComplex.replace("yadif=", "yadif_cuda=").replace("bwdif=", "bwdif_cuda=");						
				}
				
				filterComplex = filterComplex.replace("scale=", "scale_cuda=");	
				
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
			else if ((autoAMF || Shutter.comboGPUFilter.getSelectedItem().toString().equals("amf")) && caseForcerDesentrelacement.isSelected() == false && lblPad.getText().equals(language.getProperty("lblCrop")) == false)
			{
				filterComplex = filterComplex.replace("scale=", "vpp_amf=");
				
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
			else if ((autoQSV || Shutter.comboGPUFilter.getSelectedItem().toString().equals("qsv")) && filterComplex.contains("force_original_aspect_ratio") == false)
			{
				if (caseForcerDesentrelacement.isSelected())
				{
					filterComplex = filterComplex.replace("bob=", "vpp_qsv=deinterlace=1").replace("advanced=", "vpp_qsv=deinterlace=2");						
				}
				
				filterComplex = filterComplex.replace("scale=", "scale_qsv=");
				
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
			else if ((autoVIDEOTOOLBOX || Shutter.comboGPUFilter.getSelectedItem().toString().equals("videotoolbox")) && caseForcerDesentrelacement.isSelected() == false && filterComplex.contains("force_original_aspect_ratio") == false && lblPad.getText().equals(language.getProperty("lblCrop")) == false)
			{
				filterComplex = filterComplex.replace("scale=", "scale_vt=");
				
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
			else if ((autoVULKAN || Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan")) && filterComplex.contains("force_original_aspect_ratio") == false && lblPad.getText().equals(language.getProperty("lblCrop")) == false)
			{
				if (caseForcerDesentrelacement.isSelected())
				{
					filterComplex = filterComplex.replace("bwdif=", "bwdif_vulkan=");						
				}
				
				filterComplex = filterComplex.replace("scale=", "scale_vulkan=");
				
				filterComplex += ",hwdownload,format=" + bitDepth;
			}
		}
				
		return filterComplex;
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
			else if (comboResolution.getSelectedItem().toString().contains("x"))
			{
				if (comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 2));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 2));
					}
					else
					{
						o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 4));
						o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 4));
					}
				}
				else
					o = comboResolution.getSelectedItem().toString().split("x");
			}
			else if (comboResolution.getSelectedItem().toString().contains(":"))
			{
				o = comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
				
				int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);          	
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);        	
	        	float ir = (float) iw / ih;
						        	
				if (o[0].toString().equals("1")) // = auto
				{
					o[0] = String.valueOf((int) Math.round((float) oh * ir));
				}
        		else
        		{
        			o[1] = String.valueOf((int) Math.round((float) ow / ir));
        		}
			}
			
			int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);          	
        	int ow = Integer.parseInt(o[0]);
        	int oh = Integer.parseInt(o[1]);        	
        	float ir = (float) iw / ih;
        	float or = (float) ow / oh;
        	
        	boolean upscale = true;
    		if (btnNoUpscale.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false && comboResolution.getSelectedItem().toString().contains("%") == false)
    		{
    			if (iw < ow || ih < oh)
    			{
    				upscale = false;
    			}
    		}
    		
    		if (upscale)
    		{
				if (lblPad.getText().equals(language.getProperty("lblCrop")) && lblPad.isVisible())
				{
					if (comboResolution.getSelectedItem().toString().contains(":") == false)		       
					{					       	
			        	//Original sup. à la sortie
			        	if (iw > ow || ih > oh)
			        	{
			        		//Si la hauteur calculée est > à la hauteur de sortie
			        		if ( (float) ow / ir >= oh)
			        			filterComplex += ",crop=" + "'" + ow + ":" + oh + ":0:(ih-oh)*0.5" + "'";
			        		else
			        			filterComplex += ",crop=" + "'" + ow + ":" + oh + ":(iw-ow)*0.5:0" + "'";
			        	}
					}
				}
				else
				{
					if (lblPad.getText().equals(language.getProperty("lblPad")) && ir != or && lblPad.isVisible())
					{
						filterComplex += ",pad=" +o[0]+":"+o[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
					}
				}
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

			int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);          	
        	int ow = Integer.parseInt(o[0]);
        	int oh = Integer.parseInt(o[1]);        	
        	float ir = (float) iw / ih;
        	float or = (float) ow / oh;
			
        	boolean upscale = true;
    		if (btnNoUpscale.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false && comboResolution.getSelectedItem().toString().contains("%") == false)
    		{
    			if (iw < ow || ih < oh)
    			{
    				upscale = false;
    			}
    		}
    		
    		if (upscale)
    		{
				if (ir != or)
				{
					filterComplex += ",pad=" +o[0]+":"+o[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
				}	
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
