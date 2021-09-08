package settings;

import application.Shutter;
import library.EXIFTOOL;
import library.FFPROBE;

public class Timecode extends Shutter {

	public static String setTimecode() {
		
		if (grpSetTimecode.isVisible())
		{
			String dropFrame = ":";
	   		if (caseConform.isSelected() == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || caseConform.isSelected() && (comboFPS.getSelectedItem().toString().equals("29,97") || comboFPS.getSelectedItem().toString().equals("59,94")))
	   			dropFrame = ";";
			
			if (caseGenerateFromDate.isSelected())
			{
				String s[] = EXIFTOOL.creationHours.split(":");
				return " -timecode " + '"' + s[0] + ":" + s[1] + ":" + s[2] + dropFrame + "00" + '"';
			}		
			else if (caseSetTimecode.isSelected())
			{
				return " -timecode " + '"' + TCset1.getText() + ":" + TCset2.getText() + ":" + TCset3.getText() + dropFrame + TCset4.getText() + '"';
			}
			else if (FFPROBE.timecode1 != "")
			{
				return " -timecode " + '"' + FFPROBE.timecode1 + ":" + FFPROBE.timecode2 + ":" + FFPROBE.timecode3 + dropFrame + FFPROBE.timecode4 + '"';
			}
		}
		
		return "";
	}
	
}
