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

import java.awt.Color;
import java.io.File;
import java.util.Set;

import application.Ftp;
import application.Shutter;
import application.UIController;
import application.Utils;
import library.ANONYMIZER;
import library.FFMPEG;
import settings.FunctionUtils;

public class BlurFaces extends Shutter {

	public static Thread thread;

	public static void main() {
		
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
				lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);

				for (int i = 0 ; i < list.getSize() ; i++)
				{	
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));	
														
					if (file == null)
						break;
					
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						//Check images extension
						Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".wbmp", ".tiff", ".webp", ".heic");
						boolean isImage = IMAGE_EXTENSIONS.contains(extension.toLowerCase()) ? true : false;
						
						lblCurrentEncoding.setText(fileName);	
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);
									
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
						
						//Container
						String container = ".mov";
						if (isImage)
							container = extension;
							
						//Output name
						String fileOutputName = labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + container); 
						
						//File output
						File fileOut = new File(fileOutputName);
						if (fileOut.exists())		
						{
							fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, extensionName + "_", container);
							
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

						tempsEcoule.setVisible(false);
						
						//Command		
						ANONYMIZER.run(file.toString(), fileOut);		
														
						do {
							Thread.sleep(100);
						} while (ANONYMIZER.runProcess.isAlive());
														
						if (FFMPEG.saveCode == false)
						{
							if (lastActions(fileOut))
								break;
						}						
					}
					catch (Exception e)
					{
						FFMPEG.error  = true;
						e.printStackTrace();
					}
				}

				UIController.endOfFunction();	
			}
			
		});
		thread.start();
		
    }
	
	private static boolean lastActions(File fileOut) {
		
		if (FunctionUtils.cleanFunction(null, fileOut.toString(), fileOut, ""))
			return true;

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		return false;
	}
}
