/*******************************************************************************************
* Copyright (C) 2024 PACIFICO PAUL
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

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import application.Shutter;
import application.VideoPlayer;
import library.FFMPEG;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class OfflineDetection extends Shutter {
		
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
						
						lblCurrentEncoding.setText(fileName);	
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;		         
						
						//InOut		
						VideoPlayer.getFileList(file.toString());
						InputAndOutput.getInputAndOutput();
						
						//Stats_file
						String stats_file;
						if (System.getProperty("os.name").contains("Windows"))
							stats_file = "stats_file";
						else		    		
							stats_file = Shutter.dirTemp + "stats_file";
						
						//Command
						String cmd =  " -i " + '"' + Shutter.dirTemp + "offline.png" + '"' + " -lavfi " + '"' + "[0:v]scale=1920x1080[source];[1:v]scale=1920x1080[reference];[source][reference]psnr=" + stats_file + '"' + " -an -f null -";
						
						if (System.getProperty("os.name").contains("Windows"))						
							cmd += '"';					
														
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd);		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
						
						//Show detection
						if (cancelled == false)
							showDetection(fileName);
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							if (lastActions(file, fileName))
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
	
	private static void showDetection(String fileName) {
		
		if (FFMPEG.mediaOfflineFrame.length() > 0 && Shutter.cancelled == false && FFMPEG.error == false)
		{
			 JOptionPane.showMessageDialog(frame, FFMPEG.mediaOfflineFrame, Shutter.language.getProperty("functionOfflineDetection"), JOptionPane.ERROR_MESSAGE);
			 int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("saveResult"), Shutter.language.getProperty("analyzeEnded"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			 
			 if (q == JOptionPane.YES_OPTION)
				{
					FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveResult"), FileDialog.SAVE);
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
					dialog.setVisible(true);

					 if (dialog.getFile() != null)
					 { 
						try {
							PrintWriter writer = new PrintWriter(dialog.getDirectory() + dialog.getFile().replace(".txt", "") + ".txt", "UTF-8");
							writer.println(Shutter.language.getProperty("analyzeOf") + " " + fileName);
							writer.println("");
							writer.println(FFMPEG.mediaOfflineFrame);
							writer.close();
						} catch (FileNotFoundException | UnsupportedEncodingException e) {}

					 }
					
				}//End Question
			 else
			 {
				 cancelled = false;
			 }
		}
		else
			JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("noErrorDetected"), Shutter.language.getProperty("functionOfflineDetection"), JOptionPane.INFORMATION_MESSAGE);
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
			OfflineDetection.main();
			return true;
		}
		return false;
	}
}