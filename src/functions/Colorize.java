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
						
				if (new File(DEOLDIFY.deoldifyModel).exists())
				{
					File tempFolder = null;
					
					for (int i = 0 ; i < list.getSize() ; i++)
					{		
						File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));	
															
						if (file == null)
							break;
						
						try {
							
							String fileName = file.getName();
							
							lblCurrentEncoding.setText(fileName);	
	
							String extension = fileName.substring(fileName.lastIndexOf("."));	
							
							//Output folder
							String labelOutput = FunctionUtils.setOutputDestination("", file);
							
							lblCurrentEncoding.setText(fileName);
							tempsEcoule.setVisible(false);
							
							String model = comboFilter.getSelectedItem().toString();
							
							//File output name
							String prefix = "";	
							if (casePrefix.isSelected())
							{
								prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
							}
							
							String extensionName = "";	
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);
							}
							
							//Output name
							String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + extension); 
										
							//File output
							File fileOut = new File(fileOutputName);						
							if (fileOut.exists())
							{						
								fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, "_", extension);
								
								if (fileOut == null)
								{
									cancelled = true;
									break;
								}
								else if (fileOut.toString().equals("skip"))
								{
									continue;
								}
							}	
							
							tempFolder = new File(labelOutput + "/" + language.getProperty("functionColorize"));
							tempFolder.mkdirs();
							
							//Run deoldify
							DEOLDIFY.run(file.toString(), model, tempFolder.toString());
							do {
								Thread.sleep(100);								
							} while (DEOLDIFY.runProcess.isAlive());
														
							File tempFile = new File(tempFolder.toString() + "/" + fileName); 
							if (tempFile.exists())
								tempFile.renameTo(fileOut);	
							
							if (tempFolder.exists())
								tempFolder.delete();
							
							if (FFMPEG.saveCode == false)
							{
								lastActions(new File(labelOutput));
							}						
						}
						catch (Exception e)
						{
							if (tempFolder != null && tempFolder.exists())
								tempFolder.delete();
							
							FFMPEG.error  = true;
							e.printStackTrace();
						}
					}
	
					endOfFunction();				
				}
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
