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

public class Conform extends Shutter {
	
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
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;	
						
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
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + extension); 
						
			           	//Audio
						String audio = setAudio();						

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
						
						//File output
						File fileOut = new File(fileOutputName);				
						if (fileOut.exists())		
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, extensionName + "_", extension);
							
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
										
						//Command		    		
				    	float FPSOut = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".")));
				    	float value = (float) (FFPROBE.currentFPS / FPSOut);
						
						String cmd = " -c:v copy -c:s copy" + audio + " -map v:0 -map a? -map s? -y ";
						FFMPEG.run(" -itsscale " + value + InputAndOutput.inPoint + " -i " + '"' + file + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');						
												
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
	
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
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
		
	private static String setAudio() {
		
    	float AudioFPSIn = FFPROBE.currentFPS;
    	float AudioFPSOut = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".")));
    	float value = (float) (AudioFPSOut / AudioFPSIn);
    	if (value < 0.5f || value > 2.0f)
    		return " -an";
    	else
    	{
    		if (FFPROBE.audioCodec != null && FFPROBE.audioCodec != "")
    			return " -c:a " + FFPROBE.audioCodec + " -b:a " + FFPROBE.audioBitrate + "k -af atempo=" + value;	
    		else
    			return " -an";	
    	}
    	
	}
	
	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
				
		if (FunctionUtils.cleanFunction(file, fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			Conform.main();
			return true;
		}
		return false;
	}
	
}