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

import application.Shutter;
import application.VideoPlayer;
import library.DEMUCS;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;

public class AudioSeparation extends Shutter {

	public static Thread thread;
	public static File separationFolder;
	
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
						
						lblCurrentEncoding.setText(fileName);	
						
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
						
						//Working folder
						separationFolder = new File(labelOutput + "/" + fileName.replace(extension, ""));
						
						if (separationFolder.exists())
						{
							for (File f : separationFolder.listFiles()) 
							{
								f.delete();
							}
						}
						else
							separationFolder.mkdir();

						//Wave output
						File waveFile = new File(separationFolder.toString() + "/" + fileName.replace(extension, ".wav"));
						
						//Command
						String cmd = " -c:a pcm_s16le -vn -y ";
										
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"' + waveFile.toString() + '"');		
						
						do {
							Thread.sleep(100);
						} while (FFMPEG.runProcess.isAlive());
						
						String model = "htdemucs_6s";
						if (cancelled == false)
						{
							lblCurrentEncoding.setText(fileName);
							tempsEcoule.setVisible(false);
														
							//Run demucs
							DEMUCS.run(model, separationFolder.toString(), waveFile.toString());
																			
							do {		
								Thread.sleep(100);								
							} while (DEMUCS.runProcess.isAlive());
							
							waveFile.delete();
						}
						
						if (cancelled || FFMPEG.error)
						{
							separationFolder.delete();
						}
						else
						{
							File modelFolder = new File(separationFolder.toString() + "/" + model);		
							if (modelFolder.exists())
								modelFolder.delete();
						}
														
						if (FFMPEG.saveCode == false)
						{
							lastActions(separationFolder);
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
	}
	
}
