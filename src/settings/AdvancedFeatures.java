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

import application.Settings;
import application.Shutter;
import library.FFPROBE;

public class AdvancedFeatures extends Shutter {
		
	public static String setDeinterlace(boolean progressiveOutput) {		
		
		if (caseForcerDesentrelacement.isSelected() && comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine"))	
		{
			String detelecineFields = "top";
			if (lblTFF.getText().equals("BFF"))
				detelecineFields = "bottom";
			
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=first_field=" + detelecineFields;
		}
		else if (FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1") && caseForcerEntrelacement.isSelected() == false && progressiveOutput				
		|| FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1") && caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByInterpolation")))
		|| caseForcerDesentrelacement.isSelected())
		{
			int doubler = 0;
			String field = FFPROBE.fieldOrder;
			if (lblTFF.getText().contains("x2") && caseForcerDesentrelacement.isSelected())
			{
				doubler = 1;
				
				if (lblTFF.getText().equals("x2 T"))
				{
					field = "0";
				}
				else if (lblTFF.getText().equals("x2 B"))
				{
					field = "1";
				}
			}
			
			if (comboForcerDesentrelacement.getSelectedItem().toString().equals("bob") || comboForcerDesentrelacement.getSelectedItem().toString().equals("advanced"))
			{
				return comboForcerDesentrelacement.getSelectedItem().toString() + "="; //Because the name of the filter can be replaced, adding the "=" avoid to rename the filename
			}
			else
				return comboForcerDesentrelacement.getSelectedItem().toString() + "=" + doubler + ":" + field + ":0";
		}							
		
		return "";
	}
	
	public static String setPreset() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
				
				 if (caseQMax.isSelected())
				 {
					 if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		        	 {
			        	return " -preset p7 -tune uhq";
	        		 }
					 else
						return " -preset 0";
				 }
				 else if (caseForceSpeed.isSelected())
				 {
					 return " -preset " + Shutter.comboForceSpeed.getSelectedItem().toString();
				 }
				 else
				 {
					 if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
					 {
						 return " -preset 8";
					 }
				 }
			
			case "H.264":
			case "H.265":
				
		        if (caseQMax.isSelected())
		        {
		        	if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		        	{
		        		return " -preset p7";
		        	}
		        	else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
		        	{
		        		return " -quality quality";
		        	}
		        	else if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Vulkan Video"))
		        	{
		        		return " -tune hq";
		        	}
			        else
			        	return " -preset veryslow";
		        }
		        else if (caseForcePreset.isSelected())
		        {
		        	if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		        	{
		        		return " -preset p" + (comboForcePreset.getSelectedIndex() + 1);
		        	}
					else
						return " -preset " + comboForcePreset.getSelectedItem().toString();
		        }
		        
		        break;
		        
			case "H.266":
				
				if (caseQMax.isSelected())
				{
			       return " -preset slower";
				}
				else if (caseForcePreset.isSelected())
				{
					return " -preset " + Shutter.comboForcePreset.getSelectedItem().toString();
				}
				else
				{
					 return " -preset faster";
				}
		        
			case "Blu-ray":
				
				if (caseQMax.isSelected())
				{
					return " -preset veryslow";
				}
				
				break;
		        
			case "MPEG-1":
			case "MPEG-2":
			case "WMV":
				
				if (caseQMax.isSelected())
				{
					return " -trellis 2 -cmp 2 -subcmp 2 -g 300";
				}
				
				break;
		
			case "Xvid":
				
				if (caseQMax.isSelected())
				{
					return " -mbd rd -flags +mv4+aic -trellis 2 -cmp 2 -subcmp 2 -g 300";
				}
				
				break;
				
			case "VP8":
			case "VP9":
				
				if (caseQMax.isSelected())
				{
					return " -speed 0 -quality best";
				}
		        else if (caseForceSpeed.isSelected())
		        {
					if (caseForceQuality.isSelected())
					{
						return " -speed " + Shutter.comboForceSpeed.getSelectedItem().toString() + " -quality " + Shutter.comboForceQuality.getSelectedItem().toString();
					}
					else
		        		return " -speed " + Shutter.comboForceSpeed.getSelectedItem().toString();
		        }
		        else
		        {
		        	if (caseForceQuality.isSelected())
					{
						return " -speed 4 -quality " + Shutter.comboForceQuality.getSelectedItem().toString();
					}
					else
						return " -speed 4";
		        }
		}
		
		return "";	
	}
		
	public static String setProfile() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
		
				if (caseForceLevel.isSelected())
		        	return " -profile:v " + Shutter.comboForceProfile.getSelectedItem().toString() + " -level " + Shutter.comboForceLevel.getSelectedItem().toString();
		    
				break;
					
			case "H.264":
				
				if (caseForceLevel.isSelected())
		        {
		        	String profile = Shutter.comboForceProfile.getSelectedItem().toString().replace("base", "baseline");
		            if (caseColorspace.isSelected() && profile.equals("high") && comboColorspace.getSelectedItem().toString().contains("10bits") && comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
		    			profile = "high10";
		            
		            return " -profile:v " + profile + " -level " + Shutter.comboForceLevel.getSelectedItem().toString();
		        }
		        else
		        {
		        	String profile = "high";
		            if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits") && comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
		    			profile = "high10";
		            
		        	String s[] = FFPROBE.imageResolution.split("x");
		        	if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		        	{
		        		if (comboResolution.getSelectedItem().toString().contains("%"))
						{
							double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
							
							s[0] = String.valueOf((int) (Integer.parseInt(s[0]) * value));
							s[1] = String.valueOf((int) (Integer.parseInt(s[1]) * value));
						}
		        		else if (comboResolution.getSelectedItem().toString().contains("AI"))
						{
							if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
							{
								s[0] = String.valueOf(FFPROBE.imageWidth * 2);
								s[1] = String.valueOf(FFPROBE.imageHeight * 2);
							}
							else
							{
								s[0] = String.valueOf(FFPROBE.imageWidth * 4);
								s[1] = String.valueOf(FFPROBE.imageHeight * 4);
							}
						}
		        		else if (comboResolution.getSelectedItem().toString().contains("x"))
		    			{
		    				if (comboResolution.getSelectedItem().toString().contains("AI"))
		    				{
		    					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
		    					{
		    						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * 2));
		    						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * 2));
		    					}
		    					else
		    					{
		    						s[0] = String.valueOf(Math.round(Integer.parseInt(s[0]) * 4));
		    						s[1] = String.valueOf(Math.round(Integer.parseInt(s[1]) * 4));
		    					}
		    				}
		    				else
		    					s = comboResolution.getSelectedItem().toString().split("x");
		    			}
		        		else if (comboResolution.getSelectedItem().toString().contains(":"))
						{
		        			String i[] = FFPROBE.imageResolution.split("x");
							s = comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
							
							int iw = Integer.parseInt(i[0]);
				        	int ih = Integer.parseInt(i[1]);          	
				        	int ow = Integer.parseInt(s[0]);
				        	int oh = Integer.parseInt(s[1]);        	
				        	float ir = (float) iw / ih;
									        	
							if (s[0].toString().equals("1")) // = auto
							{
								s[0] = String.valueOf((int) Math.round((float) oh * ir));
							}
			        		else
			        		{
			        			s[1] = String.valueOf((int) Math.round((float) ow / ir));
			        		}
						}
		        	}
		        			
		            int width = Integer.parseInt(s[0]);
		            int height = Integer.parseInt(s[1]); 
		
		            if (width > 1920 || height > 1080 || FFPROBE.currentFPS >= 120.0f || FunctionUtils.setVideoBitrate() >= 100000)
		            	return " -profile:v " + profile; //level is auto selected by ffmpeg
		            else
		            	return " -profile:v " + profile + " -level 5.1";
		        }  
			
			case "H.265":
				
				if (caseForceLevel.isSelected())
		        {
		        	String profile = Shutter.comboForceProfile.getSelectedItem().toString();
		    		if (caseColorspace.isSelected() && profile.equals("main") && comboColorspace.getSelectedItem().toString().contains("10bits"))
		    		{
		    			profile = "main10";    
		    		}
		    		else if (caseColorspace.isSelected() && profile.equals("main") && comboColorspace.getSelectedItem().toString().contains("12bits"))
		    		{
		    			profile = "main12";    
		    		}
		    		else if (profile.equals("main422"))
		    		{
		    			if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("12bits"))
		    			{
		    				profile = "main422-12";    
		    			}
		    			else
		    				profile = "main422-10";    
		    		}
		    		else if (profile.equals("main444"))
		    		{
		    			if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("12bits"))
		    			{
		    				profile = "main444-12";  
		    			}
		    			else
		    				profile = "main444-10";    
		    		}
		        	
		    		return " -profile:v " + profile + " -level:v " + Shutter.comboForceLevel.getSelectedItem().toString();
		        }
		        else
		        {
		        	String profile = "main";
		        	if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits"))
		        	{
		        		profile = "main10";
		        	}		        	
		        	else if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("12bits"))
		        	{
		        		profile = "main12";
		        	}
		        	
		        	return " -profile:v " + profile;
		        }
				
		}
		
		return "";
	}
	
	public static String setTune() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
			case "H.265":
				
		        if (caseForceTune.isSelected())
		        {
		        	return " -tune " + Shutter.comboForceTune.getSelectedItem().toString();
		        }
		        
		        break;		
		  
			case "VP8":		  
			case "VP9":
				
				if (caseForceTune.isSelected())
				{
					return " -tune-content " + Shutter.comboForceTune.getSelectedItem().toString();
				}
				
				break;
		}
		
		return "";
	}

	public static String setInterlace() {	
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "MPEG-1":
			case "MPEG-2":
		
				if (FFPROBE.interlaced.equals("1") && caseForcerDesentrelacement.isSelected() == false && comboFilter.getSelectedIndex() == 1)
		        {               
		            if (FFPROBE.interlaced.equals("1") && FFPROBE.fieldOrder.equals("1")) //Invert fields
		            	return " -field_order bt -flags +ildct -top 1";                    	
		            else
		            	return " -flags +ildct -top 1";	
		        }
				
				break;
				
			case "Apple ProRes":
			case "GoPro CineForm":
			case "QT Animation":
			case "Uncompressed":
		
		        if (caseForcerProgressif.isSelected())
				{
		        	return " -field_order progressive";  
		    	}
		        else if (FFPROBE.interlaced.equals("1") || caseForcerEntrelacement.isSelected())
		        {               	
		          if ((FFPROBE.interlaced.equals("1") && FFPROBE.fieldOrder.equals("1")) || caseForcerInversion.isSelected()) //Invert fields
		        	  return " -field_order bt";                    	
		          else
		        	  return " -field_order tt"; 
		        }		
	        
	        	break;
	        		        
			case "DNxHD":
				
				if (FFPROBE.interlaced.equals("1") && caseForcerProgressif.isSelected() == false || caseForcerEntrelacement.isSelected())
		        {        					
		            //Interlacing only for 50i          	 
		            switch (comboFilter.getSelectedItem().toString())
		            {
		            	case "120":
		            	case "185":
		            	case "185 X":	
		                    if ((FFPROBE.interlaced.equals("1") && FFPROBE.fieldOrder.equals("1")) || caseForcerInversion.isSelected()) //Invert fields
		                    	return " -field_order bt -flags +ildct -top 1";                    	
		                    else
		                    	return " -flags +ildct -top 1";			                   	
		            }
		        }
				
				break;
			
			case "AVC-Intra 100":
			case "FFV1":
			case "HAP":
			case "XAVC":
			case "XDCAM HD422":
			case "XDCAM HD 35":				
				
				if (FFPROBE.interlaced.equals("1") && caseForcerProgressif.isSelected() == false || caseForcerEntrelacement.isSelected())
				{
					return " -flags +ildct+ilme -top 1";
				}
				
				break;
			
			case "DVD":
				
				if (caseForcerProgressif.isSelected() == false)
				{
					return " -flags +ildct -top 1";
				}
				
				break;
		}
        
		return "";
	}
		
	public static String setGOP() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
			case "H.264":
			case "H.265":
			case "H.266":
			case "VP8":
			case "VP9":
			case "MPEG-1":
			case "MPEG-2":
			case "Theora":
			case "Blu-ray":
		       
				if (caseGOP.isSelected())
		        {
		        	return " -g " + Shutter.gopSize.getText();
		        }
		        
		        break;
		}
       
        return "";
	}
	
	public static String setCABAC() {
		        
        if (caseCABAC.isSelected())
        {
        	return " -coder:v vlc";
        }
        
        return "";
	}
	
	public static String setDecimate(String filterComplex) {
	
		switch (comboFonctions.getSelectedItem().toString())
		{			
			case "AV1":
			case "H.264":
			case "H.265":
			case "H.266":
			case "VP8":
			case "VP9":						
			case "MPEG-1":
			case "MPEG-2":
			case "MJPEG":
			case "Theora":
			case "WMV":
			case "Xvid":				
				
				if (caseDecimate.isSelected())
				{			
					if (filterComplex != "") filterComplex += ",";	
					
					filterComplex += "mpdecimate";	
				}
				
				break;
		}

		return filterComplex;
	}
	
	public static String setConform(String filterComplex) {		
		
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformByBlending")))
		{
			float newFPS = Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", "."));
			
			float FPS = FFPROBE.currentFPS;
			if (caseEnableSequence.isSelected())
				FPS = Float.valueOf(caseSequenceFPS.getSelectedItem().toString().replace(",", ".").replace(",", "."));
			
			if (FPS != newFPS)
			{	            
				if (filterComplex != "") filterComplex += ",";       
				
				filterComplex += "minterpolate=fps=" + newFPS + ":mi_mode=blend";
			}
		}
		
		return filterComplex;
	}
	
	public static String setInterpolation(String filterComplex) {
		
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformByInterpolation")))
		{		            		
			float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));  
				
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "minterpolate=fps=" + newFPS;            
		}

		return filterComplex;
	}
	
	public static String setSlowMotion(String filterComplex) {
		
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
        {		            		
        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));  
        		
            if (filterComplex != "") filterComplex += ",";
            
            filterComplex += "minterpolate=fps=" + newFPS + ",setpts=" + (newFPS / FFPROBE.currentFPS) + "*PTS";            
        }
		
		return filterComplex;
	}

	public static String setPTS(String filterComplex) {
		
		if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed"))
		|| comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))
        {		            		
        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
                    	
            if (filterComplex != "") filterComplex += ",";
            	
            filterComplex += "setpts=" + (FFPROBE.currentFPS / newFPS) + "*PTS";   

    		if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse")))		            		
    			filterComplex += ",reverse";   			
        }
		
		return filterComplex;
	}	

	public static String setFramerate(boolean mxfCompatible) {
		
		if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("50"))
		{
			return " -r 25";
		}
		else if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("59,94"))
		{
			if (mxfCompatible)
			{
				return " -r 30000/1001";
			}
			else
				return " -r 29.97";
		}
		else if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("60"))
		{
			return " -r 30";
		}
		else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
		{
			return " -r " + FFPROBE.currentFPS;
		}
		else if (caseConform.isSelected())
		{
			if (comboFPS.getSelectedItem().toString().equals("59,94"))
			{
				return " -r 60000/1001";
			}
			else if (comboFPS.getSelectedItem().toString().equals("29,97"))
			{
				return " -r 30000/1001";
			}
			else if (comboFPS.getSelectedItem().toString().equals("23,98"))
			{
				return " -r 24000/1001";
			}
						
			return " -r " + Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));            
		}
		else if (inputDeviceIsRunning)
		{
			return " -fps_mode vfr";
		}
		
		if (caseForcerDesentrelacement.isSelected() && (lblTFF.getText().contains("x2") || comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine")))
		{
			//Null	
		}
		else if (Settings.comboSync.getSelectedItem().toString().equals("auto"))
		{
			if (FFPROBE.currentFPS == 59.94f)
			{
				return " -r 60000/1001";
			}
			else if (FFPROBE.currentFPS == 29.97f)
			{
				return " -r 30000/1001";
			}
			else if (FFPROBE.currentFPS == 23.98f)
			{
				return " -r 24000/1001";
			}
		}
		
		return "";
	}

	public static String setForceTFF(String filterComplex) {
		
		if (caseForcerEntrelacement.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "setfield=tff";
		}
		
		return filterComplex;		
	}	
	
	public static String setInterlace50p(String filterComplex) {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "Apple ProRes":	
			case "AVC-Intra 100":
			case "DNxHD":
			case "XAVC":
			case "XDCAM HD422":
			case "XDCAM HD 35":
			case "FFV1":
			case "GoPro CineForm":
			case "HAP":
			case "QT Animation":
			case "Uncompressed":
			case "Blu-ray":
			case "H.264":
			case "H.265":
			case "H.266":
				
				if (caseForcerEntrelacement.isSelected())
				{			
					if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) == false)
					{
						if (caseConform.isSelected() && Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", ".")) >= 50.0f)
						{
							if (filterComplex != "") filterComplex += ",";
							
							filterComplex += "format=yuv444p,interlace";
						}
					}
					else if (FFPROBE.currentFPS == 50.0f || FFPROBE.currentFPS == 59.94f || FFPROBE.currentFPS == 60.0f)
					{
						if (filterComplex != "") filterComplex += ",";
						
						filterComplex += "format=yuv444p,interlace";
					}						
				}
				
				break;
		}
				
		return filterComplex;
	}
	
	public static String setOPATOM(String audio) {
		
		if (comboFonctions.getSelectedItem().toString().equals("DNxHR")
		&& caseCreateOPATOM.isSelected() && lblOPATOM.getText().equals("OP-Atom"))
		{
			return " -an";
		}
		else if (caseOPATOM.isSelected())
		{
			return audio;
		}
					
		return "";
	}

	public static String setFlags(String fileName) { 
		
		String flags = " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
		
		if (Settings.comboSync.getSelectedItem().equals("auto") == false && caseDecimate.isSelected() == false)
		{
			flags += " -fps_mode " + Settings.comboSync.getSelectedItem();	
		}
		
		if (caseGamma.isSelected())
		{
			flags += " -movflags write_colr+write_gama -mov_gamma " + comboGamma.getSelectedItem().toString();
		}
		
		switch (comboFonctions.getSelectedItem().toString())
		{	
			case "H.265":	
				
				flags += " -tag:v hvc1";
				
				break;
						
			case "AV1":
				
				String av1Flags = "";
				
				if (lblVBR.getText().equals("CQ") == false)
				{
					av1Flags += "enable-force-key-frames=0";
				}
				
				if (caseFastDecode.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ":";
					
					av1Flags += "fast-decode=" + comboFastDecode.getSelectedItem().toString();
				}
				
				if (caseVarianceBoost.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ":";
					
					av1Flags += "enable-variance-boost=1:variance-boost-strength=" + comboVarianceBoost.getSelectedItem().toString();
				}
				
				if (caseForceTune.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ":";
						
					av1Flags += "tune=" + comboForceTune.getSelectedIndex();
				}
				
				if (caseFilmGrain.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ":";
					
					av1Flags += "film-grain=" + comboFilmGrain.getSelectedIndex();
				}
				
				if (caseFilmGrainDenoise.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ":";
					
					av1Flags += "film-grain-denoise=" + comboFilmGrainDenoise.getSelectedIndex();
				}
				
				//HDR
				if (grpColorimetry.isVisible() && caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
				{
					if (av1Flags != "") av1Flags += ":";
					
					String PQorHLG = "16";
					if (comboColorspace.getSelectedItem().toString().contains("HLG"))
						PQorHLG = "18";
					
					if (comboHDRvalue.getSelectedItem().toString().equals("auto") == false)
					{
						FFPROBE.HDRmax = Integer.parseInt(comboHDRvalue.getSelectedItem().toString().replace(" nits", ""));
					}
					
					if (comboCLLvalue.getSelectedItem().toString().equals("auto") == false)
					{
						FFPROBE.maxCLL = Integer.parseInt(comboCLLvalue.getSelectedItem().toString().replace(" nits", ""));
					}
					
					if (comboFALLvalue.getSelectedItem().toString().equals("auto") == false)
					{
						FFPROBE.maxFALL = Integer.parseInt(comboFALLvalue.getSelectedItem().toString().replace(" nits", ""));
					}
					
					av1Flags += "input-depth=10:color-primaries=9:transfer-characteristics=" + PQorHLG + ":matrix-coefficients=9:mastering-display=G(0.265,0.69)B(0.15,0.06)R(0.68,0.32)WP(0.3127,0.329)L(" + (int) FFPROBE.HDRmax + "," + FFPROBE.HDRmin + "):content-light=" + FFPROBE.maxCLL + "," + FFPROBE.maxFALL + ":enable-hdr=1";
				}
				
				if (av1Flags != "")
				{
					flags += " -svtav1-params " + '"' + av1Flags + '"';
				}
				
				break;
				
			case "H.264":
			case "VP8":
			case "VP9":
								
				if (caseFastStart.isSelected() && (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov")))
					flags += " -movflags faststart";
				
				if (caseDecimate.isSelected())		
					flags += " -fps_mode vfr";	

				break;	
			
			case "DVD":
				
				flags += " -f dvd";				
				break;
			
			case "MPEG-1":
			case "MPEG-2":
			case "MJPEG":
			case "Theora":
			case "WMV":
			case "Xvid":
				
				if (caseDecimate.isSelected())		
				{
					flags += " -fps_mode vfr";	
				}
				
				break;
			
			case "Apple ProRes":
				
				flags += " -metadata:s " + '"' + "encoder=" + comboFonctions.getSelectedItem().toString() + " " + comboFilter.getSelectedItem().toString() + '"' + " -vendor apl0 -flags bitexact";		
				break;
				
			case "AVC-Intra 100":
			case "XAVC":
				
				flags += " -f mxf";				
				break;
				
			case "DNxHR":
				
				if (caseCreateOPATOM.isSelected() && lblOPATOM.getText().equals("OP-Atom"))
					flags += " -metadata material_package_name=" + '"' + fileName + '"' + " -f mxf_opatom";
				
				break;				
		}
				
		return flags;
	}
	
	public static String setOptions() {
		
		String options = "";
		
		if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
		{
			int maxrate = FunctionUtils.setVideoBitrate();		
			if (maximumBitrate.getSelectedItem().toString().equals("auto") == false)
			{
				maxrate = Integer.parseInt(maximumBitrate.getSelectedItem().toString());
			}
			
			switch (comboFonctions.getSelectedItem().toString())
			{
				case "H.264":
			
			        if (caseForcerEntrelacement.isSelected())
			        {
			        	options = " -x264opts tff=1";
			            if (lblVBR.getText().equals("CBR"))
			            	options += ":nal-hrd=cbr:force-cfr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + maxrate + "k -bufsize " + Integer.valueOf((int) (maxrate * 2)) + "k";
			        }
			        else if (lblVBR.getText().equals("CBR"))
			        {
			        	options = " -x264opts nal-hrd=cbr:force-cfr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + maxrate + "k -bufsize " + Integer.valueOf((int) (maxrate * 2)) + "k";
			        }
			        
		        	break;
		        
				case "H.265":
							
					//Interlacing
					if (caseForcerEntrelacement.isSelected())
			        {
			        	options += "interlace=1";
			            
			        }
					
					/*Alpha
					if (caseAlpha.isSelected())
					{
						if (options != "") options += ":";
						
						options += "alpha=1";
					}*/
					
					//GOP
					if (caseGOP.isSelected())
			        {
			        	if (options != "") options += ":";
			        	
			    		options += "keyint=" + Shutter.gopSize.getText();
			    		
			        }
			        
					//Bitrate mode
					if (lblVBR.getText().equals("CBR"))
			        {
						if (options != "") options += ":";
						
			        	options += "strict-cbr=1";
			        }		        
			        else if (lblVBR.getText().equals("CQ") && debitVideo.getSelectedItem().toString().equals("0"))
			        {
			        	if (options != "") options += ":";
			        	
			        	options += "lossless=1";
			        }
					
					//HDR
					if (grpColorimetry.isVisible() && caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR"))
					{
						if (options != "") options += ":";
						
						String PQorHLG = "smpte2084";
						if (comboColorspace.getSelectedItem().toString().contains("HLG"))
							PQorHLG = "arib-std-b67";
						
						if (comboHDRvalue.getSelectedItem().toString().equals("auto") == false)
						{
							FFPROBE.HDRmax = Integer.parseInt(comboHDRvalue.getSelectedItem().toString().replace(" nits", ""));
						}
						
						if (comboCLLvalue.getSelectedItem().toString().equals("auto") == false)
						{
							FFPROBE.maxCLL = Integer.parseInt(comboCLLvalue.getSelectedItem().toString().replace(" nits", ""));
						}
						
						if (comboFALLvalue.getSelectedItem().toString().equals("auto") == false)
						{
							FFPROBE.maxFALL = Integer.parseInt(comboFALLvalue.getSelectedItem().toString().replace(" nits", ""));
						}
						
						options += "colorprim=bt2020:transfer=" + PQorHLG + ":colormatrix=bt2020nc:master-display=G(13250,34500)B(7500,3000)R(34000,16000)WP(15635,16450)L(" + (int) FFPROBE.HDRmax * 10000 + "," + FFPROBE.HDRmin * 10000 + "):max-cll=" + FFPROBE.maxCLL + "," + FFPROBE.maxFALL;
					}
					
					//Levels
					if (caseForceLevel.isSelected())
			        {
						if (options != "") options += ":";
						
						options += "level=" + Shutter.comboForceLevel.getSelectedItem().toString();
			        }
					
					if (options != "")
					{
						options = " -x265-params " + '"' + options + '"';
					}
									
					break;
			}
		}
		
		return options;
	}

}
