package settings;

import java.io.File;

import application.Shutter;
import library.EXIFTOOL;

public class Filter extends Shutter {

	public static boolean dateFilter(File file) throws InterruptedException {
		
		if (caseYear.isSelected() || caseMonth.isSelected() || caseDay.isSelected() || caseFrom.isSelected())
		{			 
			String date[] = EXIFTOOL.exifDate.split(":");

			if (caseYear.isSelected())
			{
				if (comboYear.getSelectedItem().toString().equals(date[0]) == false)
					return false;
			}
			if (caseMonth.isSelected())
			{
				if (comboMonth.getSelectedItem().toString().equals(date[1]) == false)
					return false;
			}
			if (caseDay.isSelected())
			{
				if (comboDay.getSelectedItem().toString().equals(date[2]) == false)
					return false;
			}		
			if (caseFrom.isSelected())
			{
				String exif[] = EXIFTOOL.exifHours.split(":");
				String from[] = comboFrom.getSelectedItem().toString().split(":");
				String to[] = comboTo.getSelectedItem().toString().split(":");
				
				int eH = Integer.parseInt(exif[0]) * 60;
				int eM = Integer.parseInt(exif[1]);
				int exifTime = eH + eM;
				
				int fH = Integer.parseInt(from[0]) * 60;
				int fM = Integer.parseInt(from[1]);
				int fromTime = fH + fM;
				
				int tH = Integer.parseInt(to[0]) * 60;
				int tM = Integer.parseInt(to[1]);
				int toTime = tH + tM;
				
				if ((exifTime >= fromTime && exifTime <= toTime) == false)
					return false;
			}
		}
		return true;
	}
	
}
