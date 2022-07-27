/*******************************************************************************************
* Copyright (C) 2022 PACIFICO PAUL
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
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Transitions;

/*
 * AAC
 * AC3
 * AIFF
 * FLAC
 * OGG
 * Dolby Digital Plus
 * Dolby TrueHD
 * OPUS
 * MP3
 * WAV
 */

public class AudioEncoders extends Shutter {
	
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
						String extension =  fileName.substring(fileName.lastIndexOf("."));	
						
						lblCurrentEncoding.setText(fileName);
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;	
						
						//InOut	
						InputAndOutput.getInputAndOutput();	
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);
													
						//File output name
						String extensionName = "";	
						if (Settings.btnExtension.isSelected())
						{
							extensionName = Settings.txtExtension.getText();
						}	
						else if (caseMixAudio.isSelected())	
							extensionName +=  "_MIX";
						
						//Audio codec
						String audioCodec = "";
						String container = "";
						boolean stereoOutput = false;
						switch (comboFonctions.getSelectedItem().toString())
						{	
							case "AAC":
								
								audioCodec = "aac -b:a " + comboFilter.getSelectedItem().toString() + "k";		
								container = ".m4a";	
								stereoOutput = false;
								break;
							
							case "AC3":
								
								audioCodec = "ac3 -b:a " + comboFilter.getSelectedItem().toString() + "k";		
								container = ".ac3";	
								stereoOutput = true;
								break;
						
							case "AIFF":
								
								if (comboFilter.getSelectedItem().toString().contains("Float"))
								{
									audioCodec = "pcm_f" + comboFilter.getSelectedItem().toString().replace(" Float", "") + "be";
								}
								else
									audioCodec = "pcm_s" + comboFilter.getSelectedItem().toString().replace(" Bits", "") + "be";	
								
								stereoOutput = true;
								container = ".aif";								
								break;
								
							case "FLAC":
								
								audioCodec = "flac -compression_level " + comboFilter.getSelectedItem().toString();									
								container = ".flac";
								stereoOutput = true;
								break;
								
							case "OGG":	
								
								audioCodec = "libvorbis -b:a " + comboFilter.getSelectedItem().toString() + "k";									
								container = ".ogg";
								stereoOutput = true;
								break;
								
							case "Dolby Digital Plus":
								
								audioCodec = "eac3 -b:a " + comboFilter.getSelectedItem().toString() + "k";									
								container = ".eac3";
								stereoOutput = true;
								break;
								
							case "Dolby TrueHD":
								
								audioCodec = "truehd -strict -2";									
								container = ".thd";
								stereoOutput = true;
								break;
								
							case "OPUS":
								
								audioCodec = "libopus -b:a " + comboFilter.getSelectedItem().toString() + "k";									
								container = ".opus";
								stereoOutput = false;
								break;
							
							case "MP3":
								
								audioCodec = "libmp3lame -b:a " + comboFilter.getSelectedItem().toString() + "k";		
								container = ".mp3";	
								stereoOutput = true;
								break;
								
							case "WAV":
								
								if (comboFilter.getSelectedItem().toString().contains("Float"))
								{
									audioCodec = "pcm_f" + comboFilter.getSelectedItem().toString().replace(" Float", "") + "le" + " -write_bext 1";
								}
								else
									audioCodec = "pcm_s" + comboFilter.getSelectedItem().toString().replace(" Bits", "") + "le" + " -write_bext 1";		
								
								container = ".wav";
								stereoOutput = true;
								break;
						}
						
						//Split video
						if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
						{
							container = "_%03d" + container;
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
						
						//Concat mode
						String concat = FunctionUtils.setConcat(file, labelOutput);					
						if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
							file = new File(labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
																						
						//Audio
						String audio = setAudio(audioCodec, stereoOutput);
													
						//Command				
						if (caseSplitAudio.isSelected()) //Permet de créer la boucle de chaque canal audio
						{
							if (FFPROBE.surround)
							{
								if (lblSplit.getText().equals(language.getProperty("mono")))
								{								
									String cmd = " -filter_complex " + '"' + "channelsplit=channel_layout=5.1[FL][FR][FC][LFE][BL][BR]" + '"' + " -vn -y ";
									FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd
									+ " -map " + '"' + "[FL]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_FL" + container) + '"'
									+ " -map " + '"' + "[FR]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_FR" + container) + '"'
									+ " -map " + '"' + "[FC]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_FC" + container) + '"'
									+ " -map " + '"' + "[LFE]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_LFE" + container) + '"'
									+ " -map " + '"' + "[BL]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_BL" + container) + '"'
									+ " -map " + '"' + "[BR]" + '"' + " " + '"'  + fileOut.toString().replace(container, "_BR" + container) + '"');
								}
								else if (lblSplit.getText().equals(language.getProperty("stereo")))
								{		
									String cmd = " -af " + '"' + "pan=stereo|c0=FL|c1=FR" + '"' + " -vn -y ";
									FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');
								}
							}
							else 
							{
								splitAudio(audioCodec, fileName, extension, file, labelOutput, container);		
							}
						}
						else if (caseMixAudio.isSelected() && lblMix.getText().equals("2.1"))
						{
							String cmd = " " + audio + "-vn -y ";
							FFMPEG.run(InputAndOutput.inPoint + concat +
									" -i " + '"' + liste.getElementAt(0) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(1) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(2) + '"' + InputAndOutput.outPoint +
									cmd + '"'  + fileOut + '"');
						}
						else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
						{
							String cmd = " " + audio + "-vn -y ";
							FFMPEG.run(InputAndOutput.inPoint + concat +
									" -i " + '"' + liste.getElementAt(0) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(1) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(2) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(3) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(4) + '"' + InputAndOutput.outPoint +
									" -i " + '"' + liste.getElementAt(5) + '"' + InputAndOutput.outPoint +
									cmd + '"'  + fileOut + '"');
						}
						else
						{
							String cmd = " " + audio + "-vn -y ";
							FFMPEG.run(InputAndOutput.inPoint + concat + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');
						}								
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
	
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && caseSplitAudio.isSelected() == false
						|| FFMPEG.saveCode == false && Settings.btnSetBab.isSelected()
						|| FFMPEG.saveCode == false && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
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

	private static String setAudio(String codec, boolean stereoOutput) {        
		
		String audio = "";	
		String audioFilter = "";	
		
		if (Transitions.setAudioFadeIn() !=  "")
		{
			audioFilter += "," + Transitions.setAudioFadeIn();
		}
		
		if (Transitions.setAudioFadeOut() !=  "")
		{
			audioFilter += "," + Transitions.setAudioFadeOut();
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			audioFilter += "," + Transitions.setAudioSpeed();
		}
		
		if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("stereo")))						
		{
			for (int n = 1 ; n < liste.size() ; n++)
			{
				audio += "-i " + '"' + liste.elementAt(n) + '"' + " ";
			}
			
			if (FFPROBE.stereo)
				audio += "-filter_complex amerge=inputs=" + liste.size() + audioFilter + " -ac 2 ";
			else
			{
				audio += "-filter_complex " + '"';
				String left = "";
				int cl = 0;
				String right = "";
				int cr = 0;
				for (int n = 0 ; n < liste.size() ; n++)
				{
					if (n % 2 == 0) //les chiffres paires à gauche
					{
						left += "[" + n + ":0]";
						cl++;
					}
					else			//les chiffres impaires à droite
					{
						right += "[" + n + ":0]";
						cr++;
					}
				}
				audio += left + "amerge=inputs=" + cl + ",channelmap=map=FL[left];" + right + "amerge=inputs=" + cr + ",channelmap=map=FR[right];";
						
				audio += "[left][right]amerge=inputs=2" + audioFilter + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + " -ac 2 ";
			}							
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals("2.1"))
		{
			audio = "-filter_complex " + '"' + "[0:a][1:a][2:a]join=inputs=3:channel_layout=2.1" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
		{
			audio = "-filter_complex " + '"' + "[0:a][1:a][2:a][3:a][4:a][5:a]join=inputs=6:channel_layout=5.1" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("mono")))						
		{
			for (int n = 1 ; n < liste.size() ; n++)
			{
				audio += "-i " + '"' + liste.elementAt(n) + '"' + " ";
			}
			
			audio += "-filter_complex amerge=inputs=" + liste.size() + audioFilter + " -ac 1 ";
			
		}
		else if (FFPROBE.stereo)
		{
			audio = "-map a:0 ";
			if (audioFilter != "")     
				audio += audioFilter.replaceFirst(",", " -filter_complex ") + " ";
		}
		else if (FFPROBE.channels > 1)
		{
			if (stereoOutput)
			{
				audio = "-filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";	
			}
			else
			{
				audio = audioFilter.replaceFirst(",", " -filter_complex ") + " -map a? ";
			}
		}
		else //Fichier Mono
		{
			if (audioFilter != "")     
				audio += audioFilter.replaceFirst(",", " -filter_complex ") + " ";
		}
			
		//Quantization
		audio += "-c:a " + codec + " ";							
		
		//Frequence d'échantillonnage
		if (caseSampleRate.isSelected())
			audio += "-ar " + lbl48k.getText() + " ";
		
		return audio;
	}
	
	private static void splitAudio(String codec, String fileName, String extension, File file, String output, String container) throws InterruptedException {
		
		String audioFilter = "";	
		
		if (Transitions.setAudioFadeIn() !=  "")
		{
			audioFilter += "," + Transitions.setAudioFadeIn();
		}
		
		if (Transitions.setAudioFadeOut() !=  "")
		{
			audioFilter += "," + Transitions.setAudioFadeOut();
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			audioFilter += "," + Transitions.setAudioSpeed();
		}
		
		if (FFPROBE.channels == 1 && lblSplit.getText().equals(language.getProperty("mono")))
		{			
			
			for (int i = 1 ; i < 3; i ++)
			{
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(output + "/" + fileName.replace(extension, "_Audio_" + i + container));
				if(fileOut.exists())
				{										
					fileOut = FunctionUtils.fileReplacement(output, fileName, extension, "_Audio_" + i + "_", container);
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[a:0]pan=1c|c0=c" + (i - 1) + audioFilter + "[a" + (i - 1) + "]" + '"' + " -map " + '"'+ "[a" + (i - 1) + "]" + '"' + " -c:a " + codec + " -vn" + yesno;
				FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(fileName, fileOut, output))
						break;
				}
			}
			
		}
		else if (FFPROBE.channels == 1 && lblSplit.getText().equals(Shutter.language.getProperty("stereo"))) //Si le fichier est stéréo et demandé en stéréo on ne split rien
		{
			JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("theFile") + " " + fileName + " " + Shutter.language.getProperty("isAlreadyStereo"), Shutter.language.getProperty("cantSplitAudio"), JOptionPane.ERROR_MESSAGE);
		}
		else if (FFPROBE.channels > 1 && lblSplit.getText().equals(language.getProperty("mono")))
		{
			for (int i = 1 ; i < FFPROBE.channels + 1; i ++)
			{
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(output + "/" + fileName.replace(extension, "_Audio_" + i + container));
				if(fileOut.exists())
				{										
					fileOut = FunctionUtils.fileReplacement(output, fileName, extension, "_Audio_" + i + "_", container);
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				if (audioFilter != "")     
					audioFilter = audioFilter.replaceFirst(",", " -filter_complex ");
				
				String cmd = audioFilter + " -map a:" + (i - 1) + " -c:a " + codec + " -vn" + yesno;
				FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(fileName, fileOut, output))
						break;
				}
			}
		}
		else if (FFPROBE.channels > 1 && lblSplit.getText().equals(Shutter.language.getProperty("stereo")))
		{
			
			int number = 1;
			
			for (int i = 1 ; i < FFPROBE.channels + 1; i +=2)
			{
				
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(output + "/" + fileName.replace(extension, "_Audio_" + number + container));
				if(fileOut.exists())
				{										
					fileOut = FunctionUtils.fileReplacement(output, fileName, extension, "_Audio_" + number + "_", container);
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[0:a:" + (i - 1) + "][0:a:" + i + "]amerge=inputs=2" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " -c:a " + codec + " -vn" + yesno;
				FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(fileName, fileOut, output))
						break;
				}
				
				number ++;
			}
		}
		else
		{
			FFMPEG.errorList.append(fileName);
		    FFMPEG.errorList.append(System.lineSeparator());
		}
		
	}
	
	private static boolean lastActions(String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Merge
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//MixAudio
		if (caseMixAudio.isSelected())
			return true;
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(fileName);			
			AudioEncoders.main();
			return true;
		}
		
		return false;
	}
	
}