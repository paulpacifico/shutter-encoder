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

package functions;

import java.io.File;

import application.Ftp;
import application.Shutter;
import library.EXIFTOOL;
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
					//Render queue only accept selected files
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						boolean isSelected = false;
						
						for (String input : Shutter.fileList.getSelectedValuesList())
						{
							if (liste.getElementAt(i).equals(input))
							{
								isSelected = true;
							}
						}	
						
						if (isSelected == false)
						{
							continue;
						}							
					}
					
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
					
					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						final String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);

						//File output name
						String extensionName = "";	
						if (btnExtension.isSelected())
						{
							extensionName = FunctionUtils.setSuffix(txtExtension.getText(), false);
						}	
						
						//Output name
						String fileOutputName =  labelOutput + "/" + fileName.replace(extension, extensionName + comboFilter.getEditor().getItem().toString()) ; 		

						//File output
						File fileOut = new File(fileOutputName);					
						if(fileOut.exists())
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, "_", comboFilter.getEditor().getItem().toString());
							
							if (fileOut == null)
							{
								cancelled = true;
								break;
							}					
						}
						
						//Command
						String cmd;
						if (comboFonctions.getEditor().getItem().toString().contains("-passlogfile")) //Passlogfile
						{
							String[] passlogfile = comboFonctions.getEditor().getItem().toString().substring(comboFonctions.getEditor().getItem().toString().indexOf("-passlogfile") + 13).split("\"");				
							cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("ffmpeg", "").replace("-passlogfile " + '"' + passlogfile[1] + '"', "-passlogfile " + '"' + fileOut + '"') + " -y " ;
						}
						else
							cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("exiftool", "").replace("ffmpeg", "");
						
						if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg"))
						{
							cmd += " -y ";
							
							FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
							
							do
							{
								Thread.sleep(100);
							} while(FFMPEG.runProcess.isAlive());
							
					        if (cmd.contains("-pass"))
		         			{
					        	FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd.replace("-pass 1", "-pass 2") + '"'  + fileOut + '"');		
		         			}
		
					        do
							{
								Thread.sleep(100);
							} while(FFMPEG.runProcess.isAlive());
						}
						else if (comboFonctions.getEditor().getItem().toString().contains("exiftool"))
						{
							btnStart.setEnabled(false);
							
							EXIFTOOL.run(" -tagsfromfile " + '"'  + file.toString() + '"' + cmd  + " " + '"'  + file.toString() + '"');		
							
							do
							{
								Thread.sleep(100);
							} while (EXIFTOOL.isRunning);
							
							btnStart.setEnabled(true);
						}
												
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
