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

package functions;

import java.io.File;

import application.Ftp;
import application.Shutter;
import application.Wetransfer;
import library.FFMPEG;
import settings.FunctionUtils;

public class Command extends Shutter {
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
					
					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						final String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);

						//Output name
						String fileOutputName =  labelOutput + "/" + fileName.replace(extension, comboFilter.getEditor().getItem().toString()) ; 		
						
						//File output
						File fileOut = new File(fileOutputName);					
						if(fileOut.exists())
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, "_", comboFilter.getEditor().getItem().toString());
							if (fileOut == null)
								continue;						
						}
						
						//Command
						String cmd;
						if (comboFonctions.getEditor().getItem().toString().contains("-passlogfile")) //Passlogfile
						{
							String[] passlogfile = comboFonctions.getEditor().getItem().toString().substring(comboFonctions.getEditor().getItem().toString().indexOf("-passlogfile") + 13).split("\"");				
							cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("ffmpeg", "").replace("-passlogfile " + '"' + passlogfile[1] + '"', "-passlogfile " + '"' + fileOut + '"') + " -y " ;
						}
						else
							cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("ffmpeg", "") + " -y " ;
						
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
						
				        if (cmd.contains("-pass"))
	         			{
				        	FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd.replace("-pass 1", "-pass 2") + '"'  + fileOut + '"');		
	         			}
	
				        do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
												
				        //Removing temporary files
						final File folder = new File(fileOut.getParent());
						FunctionUtils.listFilesForFolder(fileName.replace(extension, ""), folder);
	
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							if (lastActions(file, fileName, fileOut, labelOutput))
							break;
						}
					
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}
				}	

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }
		
	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);	
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		FunctionUtils.copyFile(fileOut);
		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			Command.main();
			return true;
		}
		return false;
	}

}
