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
import application.RenderQueue;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;

public class FrameMD5 extends Shutter {
	
	public static Thread thread;
	
	public static void main() {
		
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < list.getSize() ; i++)
				{
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));		

					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						String container = ".framemd5";		
						
						lblCurrentEncoding.setText(fileName);
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;

						//Write the in and out values before getInputAndOutput()
						if (VideoPlayer.caseApplyCutToAll.isSelected())
						{							
							VideoPlayer.videoPath = file.toString();							
							VideoPlayer.updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
							VideoPlayer.updateGrpOut(Timecode.getNTSCtimecode(((double) FFPROBE.totalLength / 1000 * FFPROBE.accurateFPS) - InputAndOutput.savedOutPoint));							
							VideoPlayer.setFileList();	
						}
						
						//InOut	
						InputAndOutput.getInputAndOutput(VideoPlayer.getFileList(file.toString(), FFPROBE.totalLength));	
						
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
						
						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + container); 
		
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
						
						//Concat mode or Image sequence
						String concat = FunctionUtils.setConcat(file, labelOutput);					
						if ((grpImageSequence.isVisible() && caseEnableSequence.isSelected()))
						{
							file = new File(labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
						}											
						
						//Command
						FFMPEG.run(InputAndOutput.inPoint + concat + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + " -f framemd5 -y " + '"'  + fileOut + '"');
	
						do {
							Thread.sleep(100);
						} while(FFMPEG.runProcess.isAlive());
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false
						|| FFMPEG.saveCode == false && caseEnableSequence.isSelected())
						{
							if (lastActions(file, fileName, fileOut, labelOutput))
								break;
						}
						
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}
				}	
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
				{
					//Reset data for the current selected file
					VideoPlayer.videoPath = null;
					VideoPlayer.setMedia();
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (VideoPlayer.loadMedia.isAlive());
					RenderQueue.frame.toFront();
				}
				else
				{
					enfOfFunction();					
				}
			}
			
		});
		thread.start();
		
    }

	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(file, fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Image sequence and merge
		if (caseEnableSequence.isSelected())
			return true;
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			FrameMD5.main();
			return true;
		}
		
		return false;
	}
	
}