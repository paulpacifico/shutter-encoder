package settings;

import application.Shutter;
import library.FFPROBE;

public class BitratesAdjustement extends Shutter {
	
	public static boolean DVD2Pass;
	public static int DVDBitrate;
	
	public static String setResolution() {		
		
		String resolution  = comboH264Taille.getSelectedItem().toString();		
        if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) && caseRognage.isSelected() == false)
        {
        	return "";
        }
        else if (caseRognage.isSelected() && lblPad.getText().equals(language.getProperty("lblPad")))
        {
        	String s[] = comboH264Taille.getSelectedItem().toString().split("x");
        	String i[] = FFPROBE.imageResolution.split("x");
        	int ow = Integer.parseInt(s[0]);
        	int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);        	
        	
        	return " -s " + s[0] + "x" + (int) ((float)ow/((float)iw/ih));	
        }
        else
        	return " -s " + resolution;	
	}
	
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
		
		if (caseRognage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
	
			filterComplex += "crop=" + FFPROBE.cropHeight + ":" + FFPROBE.cropWidth + ":" + FFPROBE.cropPixelsWidth + ":" + FFPROBE.cropPixelsHeight;
		}
    	
    	if (caseRognerImage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
			
    		filterComplex += Shutter.cropFinal;
		}
    	
    	return filterComplex;
	}
	
}
