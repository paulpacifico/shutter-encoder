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
				if (FFPROBE.dropFrameTC != "")
				{
					dropFrame = FFPROBE.dropFrameTC;
				}
				
				return " -timecode " + '"' + FFPROBE.timecode1 + ":" + FFPROBE.timecode2 + ":" + FFPROBE.timecode3 + dropFrame + FFPROBE.timecode4 + '"';
			}
		}
		
		return "";
	}
	
}
