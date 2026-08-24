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
import shutterencoder.ui.videoplayer.VideoPlayerUI;

public class Colorimetry extends Shutter {

	public static int allR = 0;
    public static int allG = 0;
    public static int allB = 0;
    public static int highR = 0;
    public static int highG = 0;
    public static int highB = 0;
    public static int mediumR = 0;
    public static int mediumG = 0;
    public static int mediumB = 0;
    public static int lowR = 0;
    public static int lowG = 0;
    public static int lowB = 0;
    public static String balanceAll = "";
    public static String balanceHigh = "";
    public static String balanceMedium = "";
    public static String balanceLow = "";
    public static int vibranceValue = 0;
    public static int vibranceR = 0;
    public static int vibranceG = 0;
    public static int vibranceB = 0;
	
	public static String setColor(String filterComplex) {
				
		if (grpColorimetry.isVisible() && Shutter.caseEnableColorimetry.isSelected() && setEQ(true).equals("") == false)
		{			
			if (filterComplex != "") filterComplex += ",";
			
			//Important
			setEQ(true);
						
			filterComplex += colorimetryValues;	
		}

		return filterComplex;
	}
	
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
				pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
				pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
			}
			
			if (Libplacebo.useLibplaceboFilters && Libplacebo.checkLibplaceboFilter(filterComplex))
			{				
				filterComplex = Libplacebo.setLibplaceboFilter(filterComplex, "lut=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString() + ":peak_detect=0");	
			}
			else
			{		
				if (filterComplex != "") filterComplex += ",";
				
				filterComplex += "lut3d=file=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString();	
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
				    	
				    	String format = FFPROBE.hasAlpha ? "format=gbrapf32le," : "format=gbrpf32le,";
					
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
		else if (FFPROBE.videoCodec != null)
		{
			//Preserve the alpha channel
			if (FFPROBE.videoCodec.contains("vp9") && FFPROBE.hasAlpha)
			{
				return " -c:v libvpx-vp9";
			}
			else if (FFPROBE.videoCodec.contains("vp8"))
			{
				return " -c:v libvpx";
			}
		}
		
		return "";	
	}

	public static String setSharpness(String eq) {
	
		if (Shutter.sliderGrain.getValue() != 0)
		{
		    if (!eq.isEmpty())
		        eq += ",";

		    float slider = Shutter.sliderGrain.getValue() / 100.0f;
		    float amount;

		    if (Shutter.sliderGrain.getValue() > 0)
		    {
		        amount = 1.0f * slider * slider;
		        eq += "unsharp=7:7:" + String.format("%.3f", amount) + ":5:5:0";
		    }
		    else
		    {
		        amount = -0.80f * slider * slider;
		        eq += "unsharp=5:5:" + String.format("%.3f", amount) + ":5:5:0";
		    }
		}

		return eq;
	}
		
	public static String setAngle(String eq) {

		if (Shutter.sliderAngle.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			float angle;
			if (Shutter.sliderAngle.getValue() > 0)
				angle = (float) ((float) ((float) Shutter.sliderAngle.getValue() / 10) * Math.PI) / 180;
			else
				angle = (float) ((float) (0 - (float) Shutter.sliderAngle.getValue() / 10) * Math.PI) / 180;
			
			float ratio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
			float h = (float) ( (float) FFPROBE.imageHeight / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
			float w = (float) h * ratio;
			if (ratio < 1)
			{
				ratio = (float) FFPROBE.imageHeight / FFPROBE.imageWidth;
				w = (float) ( (float) FFPROBE.imageWidth / ( ( (float) ratio * Math.sin(angle) ) + Math.cos(angle) ) );
				h = (float) w * ratio;
			}
			
			w = (float) (2 - ((float) FFPROBE.imageWidth / w));
			h = (float) (2 - ((float) FFPROBE.imageHeight / h));			
						
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
			{
				eq += "rotate=" + ((float) Shutter.sliderAngle.getValue() / 10) + "*PI/180:ow=iw*" + w + ":oh=ih*" + h + ",scale=" + FFPROBE.imageWidth + ":" + FFPROBE.imageHeight; 
			}
			else
				eq += "rotate=" + ((float) Shutter.sliderAngle.getValue() / 10) + "*PI/180:ow=iw*" + w + ":oh=ih*" + h; 
		}
		
		return eq;
	}
	
	public static String setZoom(String eq) {

		if (sliderZoom.getValue() != 0)
		{			
			if (eq != "")
				eq += ",";
			
			float zoomValue = (float) 1 - ((float) sliderZoom.getValue() / 2 / 100);
			
			eq += "crop=iw*" + zoomValue + ":ih*" + zoomValue; 
		}
		
		return eq;
	}
	
	public static String setVignette(String eq) {

	    if (Shutter.sliderVignette.getValue() != 0)
	    {
	        if (eq != "")
	            eq += ",";

	        float padFactor = 1.5f;
	        String pre = "pad=trunc(iw*" + padFactor + "):trunc(ih*" + padFactor
	                    + "):(ow-iw)/2:(oh-ih)/2:black,";
	        String post = ",crop=iw/" + padFactor + ":ih/" + padFactor;

	        int sliderVal = Shutter.sliderVignette.getValue(); // -100..100
	        float maxAngle = (float) (Math.PI / 2);
	        float angle = maxAngle * (Math.abs(sliderVal) / 100f);

	        if (sliderVal > 0)
	            eq += pre + "vignette=" + angle + ":mode=backward:aspect=" + FFPROBE.imageRatio + post;
	        else
	            eq += pre + "vignette=" + angle + ":aspect=" + FFPROBE.imageRatio + post;
	    }

	    return eq;
	}

	public static String setVibrance(String eq) {

	    if (vibranceValue != 0) {

	        if (!eq.isEmpty())
	            eq += ",";

	        float v = vibranceValue / 100.0f;

	        float amount = (float) Math.copySign(
	            Math.pow(Math.abs(v), 0.75),
	            v
	        ) * 0.75f;

	        eq += "vibrance=" + amount
	            + ":rbal=" + ((100.0f + vibranceR) / 100.0f)
	            + ":gbal=" + ((100.0f + vibranceG) / 100.0f)
	            + ":bbal=" + ((100.0f + vibranceB) / 100.0f);
	    }

	    return eq;
	}
	
	public static String setSaturation(String eq) {

	    int value = Shutter.sliderSaturation.getValue();

	    if (value != 0) {

	        if (!eq.isEmpty())
	            eq += ",";

	        float v = value / 100.0f;
	        float saturation;

	        if (v >= 0.0f)
	        {
	            saturation = 1.0f + 0.65f * v;
	        }
	        else
	        {
	            saturation = 1.0f + 0.85f * v;

	            if (v <= -1.0f)
	                saturation = 0.0f;
	        }

	        eq += "eq=saturation=" + saturation;
	    }

	    return eq;
	}

	public static String setBalance(String eq) {		
		
		float r = (float) Shutter.sliderRED.getValue() / 400;
		float g = (float) Shutter.sliderGREEN.getValue() / 400;
		float b = (float) Shutter.sliderBLUE.getValue() / 400;
		
		if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setAll")))
			balanceAll = "rs="+r+":gs="+g+":bs="+b+":rm="+r+":gm="+g+":bm="+b+":rh="+r+":gh="+g+":bh="+b;			
			
		if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setLow")))	
			balanceLow = "rs="+r+":gs="+g+":bs="+b;	
		
		else if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setMedium")))		
			balanceMedium = "rm="+r+":gm="+g+":bm="+b;	
		
		else if (Shutter.comboRGB.getSelectedItem().equals(Shutter.language.getProperty("setHigh")))
			balanceHigh = "rh="+r+":gh="+g+":bh="+b;
		
		if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
		{
			if (eq != "")
				eq += ",";
			
			eq += "colorbalance=" + balanceAll;
		}
		
		//Permet de compléter tout l'eq à chaque fois
		if (balanceLow != "" || balanceMedium != "" || balanceHigh != "")
		{
			if (balanceAll != "" && balanceAll.equals("rs=0.0:gs=0.0:bs=0.0:rm=0.0:gm=0.0:bm=0.0:rh=0.0:gh=0.0:bh=0.0") == false)
				eq += ",colorbalance=";
			else if (eq != "")
				eq += ",colorbalance=";
			else
				eq = "colorbalance=";
			
			if (balanceLow == "")
				balanceLow = "rs=0:gs=0:bs=0";
			
			if (balanceMedium == "")
				balanceMedium = "rm=0:gm=0:bm=0";
			
			if (balanceHigh == "")
				balanceHigh = "rh=0:gh=0:bh=0";
			
			eq += balanceLow + ":" + balanceMedium + ":" + balanceHigh;
		}


		return eq;
	}

	public static String setContrast(String eq) {
		
		if (Shutter.sliderContrast.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "eq=contrast=" + (1 + (float) Shutter.sliderContrast.getValue() / 300); 
		}
		
		return eq;
	}
	
	public static String setWB(String eq) {

		if (Shutter.sliderBalance.getValue() != 6500)
		{
			if (eq != "")
				eq += ",";

			eq += "colortemperature=" + Shutter.sliderBalance.getValue() + ":pl=1"; 
		}
		
		return eq;
	}
	
	public static String setHUE(String eq) {

		if (Shutter.sliderHUE.getValue() != 0)
		{
			if (eq != "")
				eq += ",";

			eq += "hue=h=" + (0 - Shutter.sliderHUE.getValue()); 
		}
		
		return eq;
	}
	
	public static String setWhite(String eq) {

		if (Shutter.sliderWhite.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
				
			if (Shutter.sliderWhite.getValue() > 0)
			{
				float value = 1 - (float) Shutter.sliderWhite.getValue() / 200;				
				eq += "colorlevels=rimax=" + value + ":gimax=" + value + ":bimax=" + value; 
			}
			else
			{
				float value = 1 + (float) Shutter.sliderWhite.getValue() / 200;
				eq += "colorlevels=romax=" + value + ":gomax=" + value + ":bomax=" + value; 
			}
		}
		
		return eq;
	}
	
	public static String setBlack(String eq) {

		if (Shutter.sliderBlack.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
				
			if (Shutter.sliderBlack.getValue() > 0)
			{
				float value = (float) Shutter.sliderBlack.getValue() / 400;				
				eq += "colorlevels=romin=" + value + ":gomin=" + value + ":bomin=" + value; 				 
			}
			else
			{
				float value = 0 - (float) Shutter.sliderBlack.getValue() / 400;
				eq += "colorlevels=rimin=" + value + ":gimin=" + value + ":bimin=" + value;
			}
				
		}
		
		return eq;
	}

	public static String setShadows(String eq) {

	    int value = Shutter.sliderShadows.getValue();

	    if (value != 0) {

	        if (!eq.isEmpty())
	            eq += ",";

	        float amount = 0.15f * value / 100.0f;

	        eq += "curves=master='"
	            + "0/0 "
	            + "0.0625/" + (0.0625f + amount * 0.45f) + " "
	            + "0.125/"  + (0.125f  + amount * 0.85f) + " "
	            + "0.20/"   + (0.20f   + amount) + " "
	            + "0.30/"   + (0.30f   + amount * 0.55f) + " "
	            + "0.40/0.40 "
	            + "0.50/0.50 "
	            + "0.75/0.75 "
	            + "1/1'";
	    }

	    return eq;
	}

	public static String setMediums(String eq) {

	    int value = Shutter.sliderMediums.getValue();

	    if (value != 0) {

	        if (!eq.isEmpty())
	            eq += ",";

	        float amount = 0.16f * value / 100.0f;

	        eq += "curves=master='"
	            + "0/0 "
	            + "0.25/" + (0.25f + amount * 0.35f) + " "
	            + "0.40/" + (0.40f + amount * 0.75f) + " "
	            + "0.50/" + (0.50f + amount) + " "
	            + "0.60/" + (0.60f + amount * 0.75f) + " "
	            + "0.75/" + (0.75f + amount * 0.35f) + " "
	            + "1/1'";
	    }

	    return eq;
	}

	public static String setHighlights(String eq) {

	    int value = Shutter.sliderHighlights.getValue();

	    if (value != 0) {

	        if (!eq.isEmpty())
	            eq += ",";

	        float amount = 0.15f * value / 100.0f;

	        eq += "curves=master='"
	            + "0/0 "
	            + "0.25/0.25 "
	            + "0.50/0.50 "
	            + "0.625/" + (0.625f + amount * 0.15f) + " "
	            + "0.75/"  + (0.75f  + amount * 0.45f) + " "
	            + "0.875/" + (0.875f + amount * 0.90f) + " "
	            + "0.95/"  + (0.95f  + amount) + " "
	            + "1/1'";
	    }

	    return eq;
	}

	public static String setExposure(String eq) {

		if (Shutter.sliderExposure.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "exposure=" + (float) ((float) Shutter.sliderExposure.getValue() / 100) * 3; 
		}
		
		return eq;
	}
	
	public static String setGamma(String eq) {		

		if (Shutter.sliderGamma.getValue() != 0)
		{
			if (eq != "")
				eq += ",";
			
			eq += "eq=gamma=" + (1 + (float) Shutter.sliderGamma.getValue() / 100); 
		}
		
		return eq;
	}
	
	public static String setEQ(boolean finalEQ) {
		
		String eq = "";
		
		//Highlights 
		eq = setHighlights(eq);
		
		//Mediums 
		eq = setMediums(eq);
		
		//Shadows 
		eq = setShadows(eq);
		
		//Exposure
		eq = setExposure(eq);
		
		//Gamma
		eq = setGamma(eq);
		
		//Contrast
		eq = setContrast(eq);
		
		//White
		eq = setWhite(eq);

		//Black
		eq = setBlack(eq);
				
		//White Balance 
		eq = setWB(eq);
		
		//Hue
		eq = setHUE(eq);
				
		//Balance
		eq = setBalance(eq);
		
		//Saturation
		eq = setSaturation(eq);
		
		//Vibrance
		eq = setVibrance(eq);
				
		//Grain
		eq = setSharpness(eq);
				
		//Angle
		eq = setAngle(eq);
		
		//Vignette
		eq = setVignette(eq);
		
		//FinalEQ
		Shutter.colorimetryValues = eq.replace("\"", "'");
		
		return eq;
	}
	

}
