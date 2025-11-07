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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import application.RenderQueue;
import application.Shutter;
import application.VideoPlayer;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;

public class BlackDetection extends Shutter {

	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
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
						
						String levels = "0.1";
						if (FFPROBE.lumaLevel.equals("0-255"))
							levels = "0.0";
						
						//Command
						String cmd;
						if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
							cmd =  " -an -vf blackdetect=d=0.0:pix_th=" + levels + " -f null -";					
						else
							cmd =  " -an -vf blackdetect=d=0.0:pix_th=" + levels + " -f null -" + '"';	
						
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd);		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
						
						//Show detection
						if (cancelled == false)
							showDetection(file);
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							if (lastActions(file, fileName))
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
					endOfFunction();					
				}
			}
			
		});
		thread.start();
		
    }
	
	private static void showDetection(File file) {
		
		if (Shutter.cancelled == false && FFMPEG.error == false)
		{
			if (comboFilter.getSelectedIndex() == 0) // Display
			{
				if (FFMPEG.blackFrame.length() > 0)
				{
					JOptionPane.showMessageDialog(frame, FFMPEG.blackFrame, Shutter.language.getProperty("functionBlackDetection"), JOptionPane.ERROR_MESSAGE);
				}
				else
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("noErrorDetected"), Shutter.language.getProperty("functionBlackDetection"), JOptionPane.INFORMATION_MESSAGE);
			}
			else // Save
			{
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
				String fileOutputName =  FunctionUtils.setOutputDestination("", file).replace("\\", "/") + "/" + prefix + file.getName() + extensionName; 
				
				try {
					PrintWriter writer = new PrintWriter(fileOutputName + ".txt", "UTF-8");
					writer.println(Shutter.language.getProperty("analyzeOf") + " " + file.getName());
					writer.println("");
					if (FFMPEG.blackFrame.length() > 0)
					{
						writer.println(FFMPEG.blackFrame);
					}
					else
						writer.println(Shutter.language.getProperty("noErrorDetected"));
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {}
			}
		}
	}

	private static boolean lastActions(File file, String fileName) {	
		
		//Errors
		if (FFMPEG.error)
		{
			FFMPEG.errorList.append(fileName);
		    FFMPEG.errorList.append(System.lineSeparator());
		}

		//Process cancelled
		if (cancelled)
		{
			return true;
		}
		else
		{
			if(FFMPEG.error == false)
				FunctionUtils.completed++;
			lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
		}
		
		//Sending process
		FunctionUtils.addFileForMail(fileName);		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			BlackDetection.main();
			return true;
		}
		return false;
	}
}
