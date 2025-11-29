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

package functions;

import java.io.File;

import application.Ftp;
import application.Shutter;
import application.Utils;
import library.DEOLDIFY;
import library.FFMPEG;
import settings.FunctionUtils;

public class Colorize extends Shutter {
	
	public static Thread thread;
	
    public static void main() {
				
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				DEOLDIFY.downloadModel();
				
				for (int i = 0 ; i < list.getSize() ; i++)
				{		
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));	
														
					if (file == null)
						break;
					
					try {
						
						String fileName = file.getName();
						
						lblCurrentEncoding.setText(fileName);	

						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file) + "/" + language.getProperty("functionColorize");
						new File(labelOutput).mkdirs();
						
						lblCurrentEncoding.setText(fileName);
						tempsEcoule.setVisible(false);
						
						String model = comboFilter.getSelectedItem().toString();
						
						//Run deoldify
						DEOLDIFY.run(file.toString(), model, labelOutput);
						do {
							Thread.sleep(100);								
						} while (DEOLDIFY.runProcess.isAlive());
														
						if (FFMPEG.saveCode == false)
						{
							lastActions(new File(labelOutput));
						}						
					}
					catch (Exception e)
					{
						FFMPEG.error  = true;
						e.printStackTrace();
					}
				}

				endOfFunction();				
			}
			
		});
		thread.start();
    }
	
	private static void lastActions(File fileOut) {
		
		FunctionUtils.cleanFunction(null, fileOut.toString(), fileOut, "");

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
	}
	
}
