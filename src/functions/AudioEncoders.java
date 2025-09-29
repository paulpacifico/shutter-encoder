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

import javax.swing.JOptionPane;

import application.Ftp;
import application.RenderQueue;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.FFMPEG;
import library.FFPROBE;
import settings.AudioSettings;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;
import settings.Transitions;

/*
 * AAC
 * ALAC
 * AC3
 * AIFF
 * FLAC
 * Vorbis
 * Dolby Digital Plus
 * Dolby TrueHD
 * Opus
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

				for (int i = 0 ; i < list.getSize() ; i++)
				{
					//Render queue only accept selected files
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						boolean isSelected = false;
						
						for (String input : Shutter.fileList.getSelectedValuesList())
						{
							if (list.getElementAt(i).equals(input))
							{
								isSelected = true;
							}
						}	
						
						if (isSelected == false)
						{
							continue;
						}							
					}
										
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));		
					
					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));	
						
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
						
						//DRC
						String DRC = "";
						if (FFPROBE.audioCodec !=  null && FFPROBE.audioCodec.equals("ac3") && caseDRC.isSelected() == false)
						{
							DRC = " -drc_scale 0";
						}

						//Audio codec
						String audioCodec = "";
						String container = "";
						String noVideo = " -vn";
						if (FFPROBE.attachedPic)
						{
							noVideo = "";
						}
						
						boolean stereoOutput = false;
						switch (comboFonctions.getSelectedItem().toString())
						{	
							case "AAC":
								
								if (System.getProperty("os.name").contains("Mac"))
								{
									audioCodec = "aac_at -b:a " + comboFilter.getSelectedItem().toString() + "k" + " -vn -write_id3v2 1";	
								}
								else
									audioCodec = "aac -b:a " + comboFilter.getSelectedItem().toString() + "k" + " -vn -write_id3v2 1";
								
								container = ".m4a";	
								stereoOutput = false;
								break;
							
							case "AC3":
								
								audioCodec = "ac3 -b:a " + comboFilter.getSelectedItem().toString() + "k" + " -write_id3v2 1";		
								container = ".ac3";	
								stereoOutput = true;
								break;
						
							case "AIFF":
								
								if (comboFilter.getSelectedItem().toString().contains("Float"))
								{
									audioCodec = "pcm_f" + comboFilter.getSelectedItem().toString().replace(" Float", "") + "be" + noVideo + " -write_id3v2 1";
								}
								else
									audioCodec = "pcm_s" + comboFilter.getSelectedItem().toString().replace(" Bits", "") + "be" + noVideo + " -write_id3v2 1";	
								
								stereoOutput = true;
								container = ".aif";								
								break;
								
							case "FLAC":
								
								audioCodec = "flac -compression_level " + comboFilter.getSelectedItem().toString() + noVideo + " -write_id3v2 1";									
								container = ".flac";
								stereoOutput = true;
								break;
								
							case "ALAC":
								
								audioCodec = "alac -sample_fmt s" + comboFilter.getSelectedItem().toString().replace(" Bits", "").replace("24", "32") + "p" + " -vn -write_id3v2 1";	
								container = ".m4a";
								stereoOutput = true;
								break;
								
							case "Vorbis":	
								
								audioCodec = "libvorbis -b:a " + comboFilter.getSelectedItem().toString() + "k" + noVideo + " -write_id3v2 1";									
								container = ".oga";
								stereoOutput = true;
								break;
								
							case "Dolby Digital Plus":
								
								audioCodec = "eac3 -b:a " + comboFilter.getSelectedItem().toString() + "k" + noVideo + " -write_id3v2 1";									
								container = ".eac3";
								stereoOutput = true;
								break;
								
							case "Dolby TrueHD":
								
								audioCodec = "truehd -strict " + Settings.comboStrict.getSelectedItem();									
								container = ".thd";
								stereoOutput = true;
								break;
								
							case "Opus":
								
								audioCodec = "libopus -b:a " + comboFilter.getSelectedItem().toString() + "k" + noVideo + " -write_id3v2 1";		
								
								if (FFPROBE.surround && FFPROBE.channelLayout != "")
								{
									audioCodec += " -channel_layout " + FFPROBE.channelLayout;
								}
								
								container = ".opus";
								stereoOutput = false;
								break;
							
							case "MP3":
								
								audioCodec = "libmp3lame -b:a " + comboFilter.getSelectedItem().toString() + "k" + noVideo + " -write_id3v2 1";		
								container = ".mp3";	
								stereoOutput = true;
								break;
								
							case "WAV":
								
								if (comboFilter.getSelectedItem().toString().contains("Float"))
								{
									audioCodec = "pcm_f" + comboFilter.getSelectedItem().toString().replace(" Float", "") + "le" + noVideo + " -write_bext 1 -write_id3v2 1";
								}
								else
									audioCodec = "pcm_s" + comboFilter.getSelectedItem().toString().replace(" Bits", "") + "le" + noVideo + " -write_bext 1 -write_id3v2 1";		
								
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
						
						//Concat mode
						String concat = FunctionUtils.setConcat(file, labelOutput);					
						if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
							file = new File(labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, ".txt"));
																						
						//Audio
						String audio = setAudio(audioCodec, stereoOutput, file);
										
						//Audio normalization		
			            if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
						{				
				        	if (cancelled)
				        	{
				        		break;
				        	}
				        	else				        	
				        	{
				        		//Allows to add the file to the queue
				        		if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				        		{
				        			enableAll();
				        		}
				        		
				        		lblCurrentEncoding.setText(file.getName());										
				        	}
						}
						
						//Command				
						if (caseSplitAudio.isSelected()) //Permet de créer la boucle de chaque canal audio
						{
							splitAudio(audioCodec, prefix, fileName, extension, file, labelOutput, fileOut.toString(), container, DRC);
						}
						else if (caseMixAudio.isSelected() && lblMix.getText().equals("2.1"))
						{
							String cmd = " " + audio + "-y ";
							FFMPEG.run(InputAndOutput.inPoint + concat + DRC +
									" -i " + '"' + list.getElementAt(0) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(1) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(2) + '"' + InputAndOutput.outPoint +
									cmd + '"'  + fileOut + '"');
						}
						else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
						{
							String cmd = " " + audio + "-y ";
							FFMPEG.run(InputAndOutput.inPoint + concat + DRC +
									" -i " + '"' + list.getElementAt(0) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(1) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(2) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(3) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(4) + '"' + InputAndOutput.outPoint + DRC +
									" -i " + '"' + list.getElementAt(5) + '"' + InputAndOutput.outPoint +
									cmd + '"'  + fileOut + '"');
						}
						else
						{
							String cmd = " " + audio + "-y ";
							FFMPEG.run(InputAndOutput.inPoint + concat + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');
						}								
						
						do {
							Thread.sleep(100);
						} while(FFMPEG.runProcess.isAlive());
							
						//MixAudio
						if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && caseMixAudio.isSelected() && FFPROBE.surround == false)
						{
							break;
						}
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && caseSplitAudio.isSelected() == false
						|| FFMPEG.saveCode == false && Settings.btnSetBab.isSelected()
						|| FFMPEG.saveCode == false && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
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

	private static String setAudio(String codec, boolean stereoOutput, File file) {        
		
		String audio = "";	
		String audioFiltering = "";	
		
		//EQ
		if (AudioSettings.setEQ(audioFiltering) != "")
		{
			audioFiltering = "," + AudioSettings.setEQ(audioFiltering);
		}
		
		if (Transitions.setAudioFadeIn(false) !=  "")
		{
			audioFiltering += "," + Transitions.setAudioFadeIn(false);
		}
		
		if (Transitions.setAudioFadeOut(false) !=  "")
		{
			audioFiltering += "," + Transitions.setAudioFadeOut(false);
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			audioFiltering += "," + Transitions.setAudioSpeed();
		}
				
		//Audio normalization		
		if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
		{				
        	AudioNormalization.main(file);
        							
        	do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			} while (AudioNormalization.thread.isAlive());
        	
        	lblCurrentEncoding.setText(file.getName());
						
			audioFiltering += ",volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB";				
		}
		
		if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("stereo")) && FFPROBE.surround)		
		{
			audio += "-af " + '"' + "pan=stereo|FL=FC+0.30*FL+0.30*BL|FR=FC+0.30*FR+0.30*BR" + audioFiltering + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("stereo")))						
		{
			for (int n = 1 ; n < list.size() ; n++)
			{
				audio += "-i " + '"' + list.elementAt(n) + '"' + " ";
			}
			
			if (FFPROBE.stereo)
			{
				audio += "-filter_complex amerge=inputs=" + list.size() + audioFiltering + " -ac 2 ";
			}
			else
			{
				audio += "-filter_complex " + '"';
				
				if (list.size() > 2)
				{
					String left = "";
					int cl = 0;
					String right = "";
					int cr = 0;
					for (int n = 0 ; n < list.size() ; n++)
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
					audio += left + "amix=inputs=" + cl + "[left];" + right + "amix=inputs=" + cr + "[right];";
							
					audio += "[left][right]amerge=inputs=2" + audioFiltering + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + " -ac 2 ";
				}
				else
					audio += "[0:a][1:a]amerge=inputs=2" + audioFiltering + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + " -ac 2 ";
			}							
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals("2.1"))
		{
			audio = "-filter_complex " + '"' + "[0:a][1:a][2:a]join=inputs=3:channel_layout=2.1" + audioFiltering + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
		{
			audio = "-filter_complex " + '"' + "[0:a][1:a][2:a][3:a][4:a][5:a]join=inputs=6:channel_layout=5.1" + audioFiltering + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("mono")))						
		{
			for (int n = 1 ; n < list.size() ; n++)
			{
				audio += "-i " + '"' + list.elementAt(n) + '"' + " ";
			}
			
			audio += "-filter_complex amerge=inputs=" + list.size() + audioFiltering + " -ac 1 ";
			
		}		
		else if (FFPROBE.stereo)
		{
			if (audioFiltering != "")     
				audio = audioFiltering.replaceFirst(",", " -filter_complex ") + " ";
		}
		else if (FFPROBE.channels > 1)
		{
			if (stereoOutput)
			{
				audio = "-filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2" + audioFiltering + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";	
			}
			else
			{
				audio = audioFiltering.replaceFirst(",", " -filter_complex ") + " -map a? ";
			}
		}
		else //Fichier Mono
		{
			if (audioFiltering != "")     
				audio += audioFiltering.replaceFirst(",", " -filter_complex ") + " ";
		}
			
		//Quantization
		audio += "-c:a " + codec + " ";							
		
		//Frequence d'échantillonnage
		if (caseSampleRate.isSelected())
			audio += "-ar " + lbl48k.getSelectedItem().toString() + " ";
		
		return audio;
	}
	
	private static void splitAudio(String codec, String prefix, String fileName, String extension, File file, String output, String fileOut, String container, String DRC) throws InterruptedException {
		
		String audioFiltering = "";	
		
		//EQ
		if (AudioSettings.setEQ(audioFiltering) != "")
		{
			audioFiltering = "," + AudioSettings.setEQ(audioFiltering);
		}
		
		if (Transitions.setAudioFadeIn(false) !=  "")
		{
			audioFiltering += "," + Transitions.setAudioFadeIn(false);
		}
		
		if (Transitions.setAudioFadeOut(false) !=  "")
		{
			audioFiltering += "," + Transitions.setAudioFadeOut(false);
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			audioFiltering += "," + Transitions.setAudioSpeed();
		}
		
		//Audio normalization		
		if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
		{				
			audioFiltering += ",volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB";				
		}
		
		//Frequence d'échantillonnage
		String sampleRate = "";
		if (caseSampleRate.isSelected())
			sampleRate = " -ar " + lbl48k.getSelectedItem().toString();
		
		if (FFPROBE.surround)
		{		
			if (audioFiltering != "")
			{
				audioFiltering = audioFiltering.substring(1, audioFiltering.length()) + ",";
			}
			
			if (lblSplit.getText().equals(language.getProperty("mono")))
			{								
				String cmd = " -filter_complex " + '"' + audioFiltering + "channelsplit=channel_layout=5.1[FL][FR][FC][LFE][BL][BR]" + '"' + " -y ";
				FFMPEG.run(InputAndOutput.inPoint + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd
				+ " -map " + '"' + "[FL]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_FL" + container) + '"'
				+ " -map " + '"' + "[FR]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_FR" + container) + '"'
				+ " -map " + '"' + "[FC]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_FC" + container) + '"'
				+ " -map " + '"' + "[LFE]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_LFE" + container) + '"'
				+ " -map " + '"' + "[BL]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_BL" + container) + '"'
				+ " -map " + '"' + "[BR]" + '"' + " -c:a " + codec + sampleRate + " " + '"'  + fileOut.replace(container, "_BR" + container) + '"');
			}
			else if (lblSplit.getText().equals(language.getProperty("stereo")))
			{		
				String cmd = " -af " + '"' + audioFiltering + "pan=stereo|c0=FL|c1=FR" + '"' + " -c:a " + codec + sampleRate + " -y ";
				FFMPEG.run(InputAndOutput.inPoint + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOut + '"');
			}
		}
		else if (FFPROBE.channels == 1 && lblSplit.getText().equals(language.getProperty("mono")))
		{		
			for (int i = 1 ; i < 3; i ++)
			{
				//Si le fichier existe
				String yesno = " -y ";
				File fileOutput = new File(output + "/" + prefix + fileName.replace(extension, "_Audio_" + i + container));
				if(fileOutput.exists())
				{										
					fileOutput = FunctionUtils.fileReplacement(output, prefix + fileName, extension, "_Audio_" + i + "_", container);
					if (fileOutput == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[a:0]pan=1c|c0=c" + (i - 1) + audioFiltering + "[a" + (i - 1) + "]" + '"' + " -map " + '"'+ "[a" + (i - 1) + "]" + '"' + " -c:a " + codec + sampleRate + yesno;
				FFMPEG.run(InputAndOutput.inPoint + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOutput + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(file, fileName, fileOutput, output))
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
				File fileOutput = new File(output + "/" + prefix + fileName.replace(extension, "_Audio_" + i + container));
				if(fileOutput.exists())
				{										
					fileOutput = FunctionUtils.fileReplacement(output, prefix + fileName, extension, "_Audio_" + i + "_", container);
					if (fileOutput == null)
						yesno = " -n ";	
				}
				
				if (audioFiltering != "")     
					audioFiltering = audioFiltering.replaceFirst(",", " -filter_complex ");
				
				String cmd = audioFiltering + " -map a:" + (i - 1) + " -c:a " + codec + sampleRate + yesno;
				FFMPEG.run(InputAndOutput.inPoint + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOutput + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(file, fileName, fileOutput, output))
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
				File fileOutput = new File(output + "/" + prefix + fileName.replace(extension, "_Audio_" + number + container));
				if(fileOutput.exists())
				{										
					fileOutput = FunctionUtils.fileReplacement(output, prefix + fileName, extension, "_Audio_" + number + "_", container);
					if (fileOutput == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[0:a:" + (i - 1) + "][0:a:" + i + "]amerge=inputs=2" + audioFiltering + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " -c:a " + codec + sampleRate + yesno;
				FFMPEG.run(InputAndOutput.inPoint + DRC + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"'  + fileOutput + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (lastActions(file, fileName, fileOutput, output))
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
	
	private static boolean lastActions(File file, String fileName, File fileOut, String output) {
		
		if (FunctionUtils.cleanFunction(file, fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Merge
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//MixAudio
		if (caseMixAudio.isSelected() && FFPROBE.surround == false)
			return true;
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);			
			AudioEncoders.main();
			return true;
		}
		
		return false;
	}
	
}