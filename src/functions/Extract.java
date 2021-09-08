/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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
import application.VideoPlayer;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;

public class Extract extends Shutter {
	
	private static int subStream = 0;	
	private static boolean extractSubsComplete = false;
	
	private static int audioStream = 0;
	private static boolean extractAudioComplete = false;
	
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
					
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;	

						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);
						
						//Mapping
						String mapping = setMapping();
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);

						//File output name
						String extensionName = "";
						
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
						{
							extensionName =  "_" + Shutter.language.getProperty("video");
						}
						else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
						{
							extensionName =  "_" + Shutter.language.getProperty("audio") + "_" + (audioStream + 1);
						}
						else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
						{
							extensionName =  "_" + Shutter.language.getProperty("subtitles") + "_" + (subStream + 1);
						}
							
						String container = extension;
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")) && FFPROBE.audioCodec.contains("pcm"))
						{
							container = ".wav";						
						}
						else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
						{
							container = ".srt";						
						}

						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, extensionName + container); 
												
						//File output
						File fileOut = new File(fileOutputName);				
						if (fileOut.exists())		
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, extensionName + "_", container);
							
							if (fileOut == null)
								continue;						
						}		
									
						//Command
						String cmd = " -c copy -c:s mov_text -c:s srt" + mapping + " -y ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
						
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false
						|| FFMPEG.saveCode && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						{
							if (lastActions(fileName, fileOut, labelOutput))
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

	public static void extractAll() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
				comboFilter.setSelectedItem(language.getProperty("video"));
				btnStart.doClick();
				
				FFMPEG.isRunning = true;
				
				if (cancelled == false && FFMPEG.error == false)
				{
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
				}
				
				if (FFPROBE.audioStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("audio"));
					extractAudio();
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while (extractAudioComplete == false);	
				}

				if (FFPROBE.subtitleStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("subtitles"));
					extractSubs();
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while (extractSubsComplete == false);	
				}
				
				comboFilter.setSelectedItem(language.getProperty("setAll"));				
			}										
		});
		wait.start();	
	}
	
	public static void extractAudio() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
								
				audioStream = 0;
				extractAudioComplete = false;
				
				do {
					Extract.main();		
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
					
					audioStream ++;
					
				} while (audioStream < FFPROBE.audioStreams && cancelled == false && cancelled == false && FFMPEG.error == false);
				
				extractAudioComplete = true;				
			}	
			
		});
		wait.start();
	}
	
	public static void extractSubs() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
								
				subStream = 0;
				extractSubsComplete = false;
				
				do {
					Extract.main();		
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
					
					subStream ++;
					
				} while (subStream < FFPROBE.subtitleStreams && cancelled == false && cancelled == false && FFMPEG.error == false);
				
				extractSubsComplete = true;				
			}	
			
		});
		wait.start();
	}
	
	private static String setMapping() {
		
		if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
		{
			return " -an -map v:0?";
		}
		else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
		{
			return " -vn -map a:" + audioStream + "?";
		}
		else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
		{
			return " -vn -an -map s:" + subStream + "?";
		}
		
		return " -map v:0? -map a? -map s?";
	}

	private static boolean lastActions(String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;

		//Sending processes
		Utils.sendMail(fileName);
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(fileName);
			Extract.main();
			return true;
		}
		return false;
	}
}
