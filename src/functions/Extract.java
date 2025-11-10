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

public class Extract extends Shutter {
	
	private static int videoStream = 0;	
	private static int audioStream = 0;
	private static int subStream = 0;	
	private static boolean extractComplete = false;
	
	public static void main() {
		
		extractComplete = false;
		
		Thread thread = new Thread(new Runnable() {			
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				videoStream = 0;
				audioStream = 0;				
				subStream = 0;
				
				for (int i = 0 ; i < list.getSize() ; i++)
				{
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));		
					
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
						String prefix = "";	
						if (casePrefix.isSelected())
						{
							prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
						}
						
						String extensionName = "";
						
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
						{
							extensionName =  "_" + Shutter.language.getProperty("video") + "_" + (videoStream + 1);
							
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false) + "_" + (videoStream + 1);;
							}
						}
						else if (comboFilter.getSelectedItem().toString().contains(language.getProperty("audio")))
						{
							extensionName =  "_" + Shutter.language.getProperty("audio") + "_" + (audioStream + 1);
							
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false) + "_" + (audioStream + 1);
							}
						}
						else if (comboFilter.getSelectedItem().toString().contains(language.getProperty("subtitles")))
						{
							extensionName =  "_" + Shutter.language.getProperty("subtitles") + "_" + (subStream + 1);
							
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false) + "_" + (subStream + 1);
							}
						}
							
						String container = extension;
						if (comboFilter.getSelectedItem().toString().contains(language.getProperty("audio")))
						{
							FFPROBE.audioCodec = FFPROBE.audioCodecs[audioStream];							
							
							if (FFPROBE.audioCodec.contains("pcm"))
							{
								container = ".wav";	
							}
							else if (FFPROBE.audioCodec.contains("aac"))
							{
								container = ".m4a";						
							}
							else if (FFPROBE.audioCodec.contains("ac3"))
							{
								container = ".ac3";	
							}
							else if (FFPROBE.audioCodec.contains("opus"))
							{
								container = ".opus";
							}
							else if (FFPROBE.audioCodec.contains("vorbis"))
							{
								container = ".oga";	
							}
							else if (FFPROBE.audioCodec.contains("eac3"))
							{
								container = ".eac3";
							}
							else if (FFPROBE.audioCodec.contains("wma"))
							{
								container = ".wma";
							}
							else if (FFPROBE.audioCodec.contains("mp3"))
							{
								container = ".mp3";	
							}
							else if (FFPROBE.audioCodec.contains("mp2"))
							{
								container = ".mp2";	
							}
						}						
						else if (comboFilter.getSelectedItem().toString().contains(language.getProperty("subtitles")))
						{
							container = ".srt";						
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
									
						//Command
						String cmd = mapping + " -y ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
																		
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false
						|| FFMPEG.saveCode && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
						{
							if (lastActions(file, fileName, fileOut, labelOutput))
								break;
						}
						
						//Loop the same file until the last videoStream				
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
						{
							if (videoStream < (FFPROBE.videoStreams - 1))
							{
								videoStream ++;
								i --;
							}
							else
							{
								videoStream = 0;
							}
						}
						
						//Loop the same file until the last audioStream				
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
						{
							if (audioStream < (FFPROBE.audioStreams - 1))
							{
								audioStream ++;
								i --;
							}
							else
							{
								audioStream = 0;
							}
						}								
						
						//Loop the same file until the last subStream				
						if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
						{
							if (subStream < (FFPROBE.subtitleStreams - 1))
							{
								subStream ++;
								i --;
							}
							else
							{
								subStream = 0;
							}
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
				
				extractComplete = true;
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
								
				//Extract video
				if (FFPROBE.videoStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("video"));
					Extract.main();	
					
					if (cancelled == false && FFMPEG.error == false)
					{
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (cancelled == false && FFMPEG.error == false && extractComplete == false);
					}
				}
				
				//Extract audio
				if (FFPROBE.audioStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("audio"));
					Extract.main();	
					
					if (cancelled == false && FFMPEG.error == false)
					{
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (cancelled == false && FFMPEG.error == false && extractComplete == false);
					}
				}

				//Extract subs
				if (FFPROBE.subtitleStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("subtitles"));
					Extract.main();

					if (cancelled == false && FFMPEG.error == false)
					{
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (cancelled == false && FFMPEG.error == false && extractComplete == false);
					}
				}	
				
				comboFilter.setSelectedItem(language.getProperty("setAll"));
			}										
		});
		wait.start();	
	}
	
	private static String setMapping() {
		
		if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
		{
			return " -an -sn -c:v copy -map v:" + videoStream + "?";
		}
		else if (comboFilter.getSelectedItem().toString().contains(language.getProperty("audio")))
		{
			if (comboFilter.getSelectedItem().toString().contains("#"))
			{
				String s[] = comboFilter.getSelectedItem().toString().split("#");						
				audioStream = Integer.valueOf(s[1]) - 1;
			}
			
			return " -vn -sn -c:a copy -map a:" + audioStream + "?";
		}
		else if (comboFilter.getSelectedItem().toString().contains(language.getProperty("subtitles")))
		{
			if (comboFilter.getSelectedItem().toString().contains("#"))
			{
				String s[] = comboFilter.getSelectedItem().toString().split("#");						
				subStream = Integer.valueOf(s[1]) - 1;
			}
			
			return " -vn -an -map s:" + subStream + "?";
		}
		
		return " -c copy -map v:0? -map a? -map s?";
	}

	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(file, fileName, fileOut, output))
			return true;

		if (comboFilter.getSelectedItem().toString().contains("#"))
		{
			return true;
		}
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			Extract.main();
			return true;
		}
		return false;
	}
}
