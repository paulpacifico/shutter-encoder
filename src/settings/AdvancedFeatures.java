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
		else if (FFPROBE.interlaced.equals("1") && caseForcerEntrelacement.isSelected() == false && progressiveOutput				
		|| FFPROBE.interlaced.equals("1") && caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByInterpolation")))
		|| caseForcerDesentrelacement.isSelected())
		{
			int doubler = 0;
			if (lblTFF.getText().equals("x2") && caseForcerDesentrelacement.isSelected())
				doubler = 1;
			
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=" + doubler + ":" + FFPROBE.fieldOrder + ":0";
		}							
		
		return "";
	}
	
	public static String setPreset() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "AV1":
				
				 if (caseQMax.isSelected())
				 {
					 return " -preset 0";
				 }
				 else if (caseForceSpeed.isSelected())
				 {
					 return " -preset " + Shutter.comboForceSpeed.getSelectedItem().toString();
				 }
				 else
				 {
					 if (caseAccel.isSelected() == false)
					 {
						 return " -preset 8";
					 }
				 }
			
			case "H.264":
			case "H.265":
				
		        if (caseQMax.isSelected())
		        {
		        	if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		        		return " -preset p7";
		        	else if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
		        		return " -quality quality";
			        else
			        	return " -preset veryslow";
		        }
		        else if (caseForcePreset.isSelected())
		        {
		        	if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		        		return " -preset p" + (comboForcePreset.getSelectedIndex() + 1);
					else
						return " -preset " + comboForcePreset.getSelectedItem().toString();
		        }
		        
		        break;
		        
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
		            if (caseColorspace.isSelected() && profile.equals("high") && comboColorspace.getSelectedItem().toString().contains("10bits") && caseAccel.isSelected() == false)
		    			profile = "high10";
		            
		            return " -profile:v " + profile + " -level " + Shutter.comboForceLevel.getSelectedItem().toString();
		        }
		        else
		        {
		        	String profile = "high";
		            if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits") && caseAccel.isSelected() == false)
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
		        		else if (comboResolution.getSelectedItem().toString().contains("x"))		
						{
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
		
		            if (width > 1920 || height > 1080)
		            	return " -profile:v " + profile + " -level 5.2";
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
		    		else if ( profile.equals("main444"))
		    		{
		    			if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("12bits"))
		    			{
		    				profile = "main444-12";  
		    			}
		    			else
		    				profile = "main444-10";    
		    		}
		        	
		            return " -profile:v " + profile + " -level " + Shutter.comboForceLevel.getSelectedItem().toString();
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
		        	
		        	String s[] = FFPROBE.imageResolution.split("x");
		        	if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		        	{
		        		if (comboResolution.getSelectedItem().toString().contains("%"))
						{
							double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
							
							s[0] = String.valueOf((int) (Integer.parseInt(s[0]) * value));
							s[1] = String.valueOf((int) (Integer.parseInt(s[1]) * value));
						}
		        		else if (comboResolution.getSelectedItem().toString().contains("x"))		
						{
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

		            if (width > 1920 || height > 1080 || FFPROBE.currentFPS >= 59.94f)
		            {
		            	if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
		            	{
		            		return " -profile:v " + profile + " -level 6.1";
		            	}
		            	else
		            		return " -profile:v " + profile + " -level 5.2";
		            }
		            else
		            	return " -profile:v " + profile + " -level 5.1";
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
			case "VP8":
			case "VP9":
			case "MPEG-1":
			case "MPEG-2":
		       
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
			case "VP8":
			case "VP9":						
			case "MPEG-1":
			case "MPEG-2":
			case "MJPEG":
			case "OGV":
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
			return " -vsync vfr";
		}
		
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
		
		return "";
	}

	public static String setForceTFF(String filterComplex) {
		
		if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422") && caseForcerEntrelacement.isSelected())
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
			case "FFV1":
			case "GoPro CineForm":
			case "HAP":
			case "QT Animation":
			case "Uncompressed":
			case "Blu-ray":
			case "H.264":
			case "H.265":
				
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
			flags += " -vsync " + Settings.comboSync.getSelectedItem();	
		}
		
		switch (comboFonctions.getSelectedItem().toString())
		{	
			case "H.265":	
				
				flags += " -tag:v hvc1";
						
			case "AV1":
				
				String av1Flags = "";
				
				if (caseFastDecode.isSelected())
				{
					av1Flags += "fast-decode=1";
				}
				
				if (caseForceTune.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ",";
						
					av1Flags += "tune=" + comboForceTune.getSelectedIndex();
				}
				
				if (caseFilmGrain.isSelected())
				{
					if (av1Flags != "")
						av1Flags += ",";
					
					av1Flags += "film-grain=" + comboFilmGrain.getSelectedIndex();
				}
				
				if (av1Flags != "")
				{
					flags += " -svtav1-params " + av1Flags;
				}
				
			case "H.264":
			case "VP8":
			case "VP9":
								
				if (caseFastStart.isSelected() && (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov")))
					flags += " -movflags faststart";
				
				if (caseDecimate.isSelected())		
					flags += " -vsync vfr";	

				break;	
			
			case "DVD":
				
				flags += " -f dvd";				
				break;
			
			case "MPEG-1":
			case "MPEG-2":
			case "MJPEG":
			case "OGV":
			case "WMV":
			case "Xvid":
				
				if (caseDecimate.isSelected())		
				{
					flags += " -vsync vfr";	
				}
				
				break;
			
			case "Apple ProRes":
				
				flags += " -metadata:s " + '"' + "encoder=" + comboFonctions.getSelectedItem().toString() + " " + comboFilter.getSelectedItem().toString() + '"' + " -vendor apl0 -movflags write_colr+write_gama -flags bitexact";		
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
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
		
		        if (caseForcerEntrelacement.isSelected())
		        {
		        	options = " -x264opts tff=1";
		            if (lblVBR.getText().equals("CBR"))
		            	options += ":nal-hrd=cbr:force-cfr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + FunctionUtils.setVideoBitrate() + "k -bufsize " + Integer.valueOf((int) (FunctionUtils.setVideoBitrate() * 2)) + "k";
		        }
		        else if (lblVBR.getText().equals("CBR"))
		        {
		        	options = " -x264opts nal-hrd=cbr:force-cfr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + FunctionUtils.setVideoBitrate() + "k -bufsize " + Integer.valueOf((int) (FunctionUtils.setVideoBitrate() * 2)) + "k";
		        }
		        
	        	break;
	        
			case "H.265":
			
				if (caseForcerEntrelacement.isSelected())
		        {
		        	options = " -x265-params interlace=1";
		        	
		        	if (caseGOP.isSelected())
		        		options += ":keyint=" + Shutter.gopSize.getText();
		        	
		            if (lblVBR.getText().equals("CBR"))
		            	options += ":strict-cbr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + FunctionUtils.setVideoBitrate() + "k -bufsize " + Integer.valueOf((int) (FunctionUtils.setVideoBitrate() * 2)) + "k";
		        }
		        else if (caseGOP.isSelected())
		        {
		    		options = " -x265-params keyint=" + Shutter.gopSize.getText();
		    		
		            if (lblVBR.getText().equals("CBR"))
		            	options += ":strict-cbr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + FunctionUtils.setVideoBitrate() + "k -bufsize " + Integer.valueOf((int) (FunctionUtils.setVideoBitrate() * 2)) + "k";
		        }
		        else if (lblVBR.getText().equals("CBR"))
		        {
		        	options = " -x265-params strict-cbr=1 -minrate " + FunctionUtils.setVideoBitrate() + "k -maxrate " + FunctionUtils.setVideoBitrate() + "k -bufsize " + Integer.valueOf((int) (FunctionUtils.setVideoBitrate() * 2)) + "k";
		        }

				break;
		}
		
		return options;
	}

}
