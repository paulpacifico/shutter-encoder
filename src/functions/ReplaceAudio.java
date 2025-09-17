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
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class ReplaceAudio extends Shutter {
	
	private static int shortestLength = 0;
	private static int videoStream = 0;
	
	private static void main(String audioFiles, String audioExt, File videoFile) throws InterruptedException {
		
		if (scanIsRunning == false)
			FunctionUtils.completed = 0;
		
		lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));				

		String fileName = videoFile.getName();
		String extension =  fileName.substring(fileName.lastIndexOf("."));
		
		lblCurrentEncoding.setText(fileName);			
		
		//InOut	
		InputAndOutput.getInputAndOutput(true);		
		
		//Output folder
		String labelOutput = FunctionUtils.setOutputDestination("", videoFile);
		
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
		if(fileOut.exists())
		{
			fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, extensionName + "_", extension);
		}
				
		if (fileOut != null && fileOut.toString().equals("skip") == false)
		{							
			String audio = setAudio(extension, audioExt);
			
			if (caseKeepSourceTracks.isSelected() && audioFiles.contains("-map 0:a") == false)
			{
				audioFiles = audioFiles.replace("-map 0:v", "-map 0");
			}
			
			String shortest = " -t " + shortestLength + "ms";
			if (comboFilter.getSelectedItem().toString().equals(language.getProperty("longest")) || list.getSize() < 2 || videoStream == list.getSize())
				shortest = "";
											
			//Command				
			String cmd = shortest + " -c:v copy -c:s copy" + audio + " -map s? -y ";
			FFMPEG.run(InputAndOutput.outPoint + " -i " + '"' + videoFile.toString() + '"' + audioFiles + cmd + '"'  + fileOut + '"');		
					
			do {
				Thread.sleep(100);
			} while(FFMPEG.runProcess.isAlive());
		
			if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
			{
				lastActions(videoFile, fileName, fileOut, labelOutput);
			}
					
		}
		else if (fileOut == null)
		{
			cancelled = true;
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

	public static void setStreams() {
		
		shortestLength = 0;
		
		Thread thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				try {
				
					String audioFiles = null;	
					String audioExt = "";
					File videoFile = null;
					
					//Batch replace video analyze
					videoStream = 0;							
					if (list.getSize() >= 2)
					{								
						for (int i = 0 ; i < list.getSize() ; i++)
						{
							//Ignore mute tracks
							if (list.getElementAt(i).contains("lavfi") == false)
							{
								if (videoStream <= 1)
								{
									//Allows to get the shortest file duration
									FFPROBE.Data(list.getElementAt(i));
									
									do {
										Thread.sleep(100);
									} while (FFPROBE.isRunning);
									
									if (FFPROBE.totalLength < shortestLength || shortestLength == 0)
									{
										shortestLength = FFPROBE.totalLength;
									}
								}
								
								if (FFPROBE.FindStreams(list.getElementAt(i)))
								{
									videoStream ++;				
								}
							}
							else
							{
								shortestLength = FFPROBE.totalLength;
							}							
						}
						
						//Start batch replace
						if (videoStream > 1)
						{
							for (int i = 0 ; i < list.getSize() ; i++)
							{		
								if (videoStream == list.getSize()) //only video files in the list
								{
									videoFile = new File(list.getElementAt(i));
									audioFiles = " -map 0:v? -map 0:a?";	
								}								
								else if (i % 2 == 0)
								{
									videoFile = new File(list.getElementAt(i));
								
									//Allows to get the shortest file duration
									FFPROBE.Data(list.getElementAt(i+1));

									do {
										Thread.sleep(100);
									} while (FFPROBE.isRunning);
									
									if (FFPROBE.totalLength < shortestLength || shortestLength == 0)
									{
										shortestLength = FFPROBE.totalLength;
									}
								
									if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) && caseChangeAudioCodec.isSelected())
									{
										audioFiles = " -map 0:v?";
									}								
									else if (caseChangeAudioCodec.isSelected())
									{
										audioFiles = " -map 0:v -map 0:a?";
									}
									else
									{
										audioFiles = " -i " + '"' + list.getElementAt(i + 1)  + '"' + " -map 0:v -map 1:a";
										audioExt = list.getElementAt(i + 1).substring(list.getElementAt(i + 1).lastIndexOf("."));
									}
								}	
								else
								{
									if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) && caseChangeAudioCodec.isSelected() || caseChangeAudioCodec.isSelected())
									{
										videoFile = new File(list.getElementAt(i));
									}
									else									
									{
										continue;
									}
								}
																
								//Start replacement
								main(audioFiles, audioExt, videoFile);
								
								//IMPORTANT
								shortestLength = 0;
								
								if (FFMPEG.error || Shutter.cancelled)
								{
									break;
								}
							}
						}
					}
					
					if (list.getSize() <= 2 || videoStream == 1) //Replace one video file
					{
						if (list.getSize() < 2)
						{	
							videoFile = new File(list.getElementAt(0));
							if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) || (list.getSize() == 1 && caseChangeAudioCodec.isSelected() == false))
							{
								audioFiles = " -map 0:v?";
							}
							else
								audioFiles = " -map 0:v? -map a?";
							
							float offset = 0;
							
							if (caseAudioOffset.isSelected())
							{
								FFPROBE.Data(videoFile.toString());
								
								do {
									Thread.sleep(100);
								} while (FFPROBE.isRunning);
								
								if (caseAudioOffset.isSelected())
								{
									offset = (float) ((float) Integer.parseInt(txtAudioOffset.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000;							
								}
								else
									offset = (float) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText()) + ((float) Integer.parseInt(VideoPlayer.caseInF.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000);
								
								audioFiles = " -itsoffset " + offset + " -i " + '"' + list.getElementAt(0)  + '"' + " -map 0:v -map 1:a";
							}
						}	
						else
						{
							videoFile = new File(list.getElementAt(0));
							
							if (list.getElementAt(1).contains("lavfi")) //Mute track
							{
								audioFiles = list.getElementAt(1);
							}
							else
								audioFiles = " -i " + '"' + list.getElementAt(1)  + '"';
							
							//Ignore mute tracks
							if (list.getElementAt(1).contains("lavfi") == false)
							{
								audioExt = list.getElementAt(1).substring(list.getElementAt(1).lastIndexOf("."));								
							}	
							
							float offset = 0;
							
							if (caseAudioOffset.isSelected())
							{
								FFPROBE.Data(videoFile.toString());
								
								do {
									Thread.sleep(100);
								} while (FFPROBE.isRunning);
								
								if (caseAudioOffset.isSelected())
								{
									offset = (float) ((float) Integer.parseInt(txtAudioOffset.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000;							
								}
								else
									offset = (float) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText()) + ((float) Integer.parseInt(VideoPlayer.caseInF.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000);
								
								audioFiles = " -itsoffset " + offset + audioFiles;
							}
							
							audioFiles += " -map 0:v -map 1:a";
							
							if (list.getSize() > 2)
								audioFiles = setMulipleAudioFiles(videoFile, "", offset);
															
							do {
								Thread.sleep(100);
							} while (FFPROBE.isRunning);	
						}
						
						//Start replacement
						main(audioFiles, audioExt, videoFile);
					}
							
				if (Settings.btnEmptyListAtEnd.isSelected() && cancelled == false && FFMPEG.error == false)
					list.clear();
					
				} catch (InterruptedException e1) {					
					FFMPEG.error  = true;
				}
			}
		});
		thread.start();
	}
	
	private static String setMulipleAudioFiles(File videoFile, String audioFiles, Float offset) {
		
		for (int i = 0 ; i < list.getSize() ; i++)
		{
			if (list.getElementAt(i).equals(" -f lavfi -i anullsrc=r=" + lbl48k.getSelectedItem().toString() + ":cl=mono")) //Si le fichier est une piste muette
			{
				audioFiles += list.getElementAt(i) ;
			}
			else if (list.getElementAt(i).equals(videoFile.toString()) == false) //Si le fichier n'est pas le fichier vidéo
			{
				if (caseAudioOffset.isSelected())
				{
					audioFiles += " -itsoffset " + offset + " -i " + '"' + list.getElementAt(i)  + '"';
				}
				else if (VideoPlayer.playerInMark > 0 || VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
				{
					offset = (float) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText()) + ((float) Integer.parseInt(VideoPlayer.caseInF.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000);
					audioFiles += " -itsoffset " + offset + " -i " + '"' + list.getElementAt(i)  + '"';
				}
				else
					audioFiles += " -i " + '"' + list.getElementAt(i)  + '"';
			}
	 	}
							
		audioFiles += " -map 0:v";
		
		for (int i = 1 ; i < list.getSize() ; i++)
		{
			audioFiles +=  " -map " + i + ":a";
		}
		return audioFiles;
	}

	private static String setAudio(String ext, String audioExt) {
		
		if (caseChangeAudioCodec.isSelected())
		{
			if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
			{
				switch (comboAudioCodec.getSelectedIndex()) 
				{
					case 0 :
						return " -c:a pcm_s16le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 1 :
						return " -c:a pcm_s24le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 2 :
						return " -c:a pcm_s32le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 3 :
						return " -c:a pcm_f32le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
				}
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("FLAC"))
			{
				return " -c:a flac -ar " + lbl48k.getSelectedItem().toString() + " -compression_level " + comboAudioBitrate.getSelectedItem().toString();
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
			{
				if (System.getProperty("os.name").contains("Mac"))
				{
					return " -c:a aac_at -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
				}
				else
					return " -c:a aac -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("MP3"))
			{
				return " -c:a libmp3lame -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
			{
				return " -c:a ac3 -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Opus"))
			{
				return " -c:a libopus -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Vorbis"))
			{
				return " -c:a libvorbis -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus"))
			{
				return " -c:a eac3 -ar " + lbl48k.getSelectedItem().toString() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else //No audio
			{
				return " -an";
			}
		}
		else //Auto mode
		{
			if (audioExt.equals(".thd"))
			{
				return  " -c:a copy -strict " + Settings.comboStrict.getSelectedItem();
			}
			
			switch (ext.toLowerCase()) 
			{			
				case ".mp3":
					if (audioExt.equals(".mp3") == false)
						return " -c:a libmp3lame -ar " + lbl48k.getSelectedItem().toString() + " -b:a 256k";
					else
						return  " -c:a copy";
				case ".wmv":
					if (audioExt.equals(".wma") == false)
						return " -c:a wmav2 -ar " + lbl48k.getSelectedItem().toString() + " -b:a 256k";
					else
						return  " -c:a copy";
				case ".mpg":
					if (audioExt.equals(".mp2") == false)
						return " -c:a mp2 -ar " + lbl48k.getSelectedItem().toString() + " -b:a 256k";
					else
						return  " -c:a copy";
				case ".ogv":
				case ".av1":
				case ".webm":
					return " -c:a libopus -ar " + lbl48k.getSelectedItem().toString() + " -b:a 192k";
			}
		}
		
		return  " -c:a copy";
	}
	
	private static void lastActions(File videoFile, String fileName, File fileOut, String output) {
		
		FunctionUtils.cleanFunction(videoFile, fileName, fileOut, output);

		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
	}

}
