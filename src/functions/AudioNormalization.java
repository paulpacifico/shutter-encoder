/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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

import javax.swing.JOptionPane;

import application.Ftp;
import application.Shutter;
import application.Utils;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;

public class AudioNormalization extends Shutter {
	
	public static Thread thread;
	private static int audioTracks;
	
	public static void main(File input) {
		
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
					
					//Audio normalization from a video codec
					if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
					{
						file = input;
					}
					
					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(Shutter.language.getProperty("analyzing") + " " + fileName);	
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;
						
		            	//filterComplex
						String filterComplex = setFilterComplex();	
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);	
	
						//File output name
						String extensionName = "";	
						if (btnExtension.isSelected())
						{
							extensionName = txtExtension.getText();
						}
						
						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, extensionName + extension); 
						
						//Audio
						String audio = setAudio(extension);
						
						//File output
						File fileOut = new File(fileOutputName);				
						if (fileOut.exists())		
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, extensionName + "_", extension);
							
							if (fileOut == null)
							{
								cancelled = true;
								break;
							}				
						}
								
						//Loudness analysis
						String cmd;
						if (caseTruePeak.isSelected() == false && caseLRA.isSelected() == false)
						{							
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
							{
								cmd =  " -vn" + filterComplex + " -f null -";					
							}
							else
								cmd =  " -vn" + filterComplex + " -f null -" + '"';	
							
							FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd);		
							
							do {
								Thread.sleep(100);
							} while(FFMPEG.runProcess.isAlive());
						}
						
						//Audio normalization from a video codec
						if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
						{
							break;
						}
						
						lblCurrentEncoding.setText(fileName);	
						
						if (cancelled == false)
						{
							//Simple volume compensation
							String normalization = "volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB";		
							
							//Using LoudNorm filter
							if (caseTruePeak.isSelected() && caseLRA.isSelected())
							{
								normalization = "loudnorm=i=" + comboFilter.getSelectedItem().toString().replace(" LUFS", "") + ":tp=" + comboTruePeak.getSelectedItem().toString().replace(" dBFS", "") + ":lra=" + comboLRA.getSelectedItem().toString().replace(" LU", "");
							}
							else if (caseTruePeak.isSelected())
							{
								normalization = "loudnorm=i=" + comboFilter.getSelectedItem().toString().replace(" LUFS", "") + ":tp=" + comboTruePeak.getSelectedItem().toString().replace(" dBFS", "");
							}
							else if (caseLRA.isSelected())
							{
								normalization = "loudnorm=i=" + comboFilter.getSelectedItem().toString().replace(" LUFS", "") + ":lra=" + comboLRA.getSelectedItem().toString().replace(" LU", "");
							}
							
							//Command
							if (FFPROBE.stereo)
							{
								cmd = " -af " + normalization + " -c:v copy -c:s copy" + audio + " -y ";
							}
						    else if (FFPROBE.channels > 1)	
						    {
						    	if (FFPROBE.channels >= 4)	    		
						    	{
									if (audioTracks == 0)
										cmd = " -filter_complex " + '"' + "[0:a:0]" + normalization + "[a1];[0:a:1]" + normalization + "[a2]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
							    	else
							    		cmd = " -filter_complex " + '"' + "[0:a:2]" + normalization + "[a3];[0:a:3]" + normalization + "[a4]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map 0:a:0 -map 0:a:1 -map [a3] -map [a4] -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
						    	}
						    	else
						    		cmd = " -filter_complex " + '"' + "[0:a:0]" + normalization + "[a1];[0:a:1]" + normalization + "[a2]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
						    }	
						    else
						    	cmd = " -af " + normalization + " -c:v copy -c:s copy" + audio + " -y ";
	
							FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"' + fileOut + '"');							
							
							do {
								Thread.sleep(100);
							} while (FFMPEG.runProcess.isAlive());
							
							if (FFMPEG.error)
							{
								String AACencoder = "aac";								
								if (System.getProperty("os.name").contains("Mac"))
								{
									AACencoder = "aac_at";		
								}

								if (FFPROBE.stereo)
								{
									cmd = " -af " + normalization + " -c:v copy -c:s copy -c:a " + AACencoder + " -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map a? -map s? -y ";
								}
							    else if (FFPROBE.channels > 1)	
							    {
							    	if (FFPROBE.channels >= 4)	    		
							    	{
										if (audioTracks == 0)
											cmd = " -filter_complex " + '"' + "[0:a:0]" + normalization + "[a1];[0:a:1]" + normalization + "[a2]" + '"' + " -c:v copy -c:s copy -c:a " + AACencoder + " -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
								    	else
								    		cmd = " -filter_complex " + '"' + "[0:a:2]" + normalization + "[a3];[0:a:3]" + normalization + "[a4]" + '"' + " -c:v copy -c:s copy -c:a " + AACencoder + " -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map 0:a:0 -map 0:a:1 -map [a3] -map [a4] -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
							    	}
							    	else
							    		cmd = " -filter_complex " + '"' + "[0:a:0]" + normalization + "[a1];[0:a:1]" + normalization + "[a2]" + '"' + " -c:v copy -c:s copy -c:a " + AACencoder + " -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
							    }	
							    else
							    	cmd = " -af " + normalization + " -c:v copy -c:s copy -c:a " + AACencoder + " -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map a? -map s? -y ";
								
								FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"' + fileOut + '"');	
							}
							
							do
							{
								Thread.sleep(100);
							}
							while(FFMPEG.runProcess.isAlive());
						}
	
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							if (lastActions(file, fileName, fileOut, labelOutput))
							break;
						}
						
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}
				}	
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	private static String setFilterComplex() {
	
		if (FFPROBE.stereo)
		{
			return " -af ebur128=peak=true";
		}
	    else if (FFPROBE.channels > 1)	
	    {
	    	if (FFPROBE.channels >= 4)	    		
	    	{
	    		String[] options = {"A1 & A2", "A3 & A4"};
	    		audioTracks = JOptionPane.showOptionDialog(frame, language.getProperty("ChooseMultitrack"), language.getProperty("multitrack"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	    		if (audioTracks == 0)
		    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
		    	else
		    		return " -filter_complex " + '"' + "[0:a:2][0:a:3]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    	}
	    	else
	    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    }
	    else
	    	return " -af ebur128=peak=true";
	}

	private static String setAudio(String ext) {		
		
		if (caseChangeAudioCodec.isSelected())
		{
			if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
			{
				switch (comboAudioCodec.getSelectedIndex()) 
				{
					case 0 :
						return " -c:a pcm_f32le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 1 :
						return " -c:a pcm_s32le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 2 :
						return " -c:a pcm_s24le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 3 :
						return " -c:a pcm_s16le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
				}
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
			{
				if (System.getProperty("os.name").contains("Mac"))
				{
					return " -c:a aac_at -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
				}
				else
					return " -c:a aac -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("MP3"))
			{
				return " -c:a libmp3lame -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
			{
				return " -c:a ac3 -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Opus"))
			{
				return " -c:a libopus -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Vorbis"))
			{
				return " -c:a libvorbis -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus"))
			{
				return " -c:a eac3 -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
		}
		else //Mode Auto
		{
			switch (ext.toLowerCase())
			{			
				case ".mp4":
					
					if (System.getProperty("os.name").contains("Mac"))
					{
						return " -c:a aac_at -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
					}
					else
						return " -c:a aac -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";

				case ".mp3":
					
					return " -c:a mp3 -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
					
				case ".wmv":
					
					return " -c:a wmav2 -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
					
				case ".mpg":
					
					return " -c:a mp2 -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
					
				case ".ogv":
				case ".av1":
				case ".webm":
					
					return " -c:a libopus -ar " + lbl48k.getText() + " -b:a 192k -map v:0? -map a? -map s?";
			}
		}
		
		if (FFPROBE.qantization == 24)
		{
			return " -c:a pcm_s24le -ar " + FFPROBE.audioSampleRate + " -map v:0? -map a? -map s?";
		}
		else if (FFPROBE.qantization == 32)
		{
			return " -c:a pcm_s32le -ar " + FFPROBE.audioSampleRate + " -map v:0? -map a? -map s?";
		}
		else
			return " -c:a pcm_s16le -ar " + FFPROBE.audioSampleRate + " -map v:0? -map a? -map s?";
	}
	
	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);
			AudioNormalization.main(new File(""));
			return true;
		}
		return false;
	}
	
}