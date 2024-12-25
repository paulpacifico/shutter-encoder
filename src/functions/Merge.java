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

import java.awt.Cursor;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.Timecode;

public class Merge extends Shutter {

	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {

				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
				
				String fileOutputName;
				
				int toExtension = liste.firstElement().toString().lastIndexOf('.');
				String extension =  liste.firstElement().substring(toExtension);		
				
				FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseFileName"), FileDialog.SAVE);
				dialog.setDirectory(new File(liste.elementAt(0).toString()).getParent());

				dialog.setVisible(true);
			    
			    if (dialog.getFile() != null)
			    {
					fileOutputName = dialog.getDirectory() + dialog.getFile().toString().replace(extension, "") + extension;
					File listeBAB = new File(dialog.getDirectory() + dialog.getFile().toString() + ".txt");

					try {
						
						int totalLength = 0;
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
						PrintWriter writer = new PrintWriter(listeBAB, "UTF-8");   
						//Timecode
						String timecode = "";
							
						for (int i = 0 ; i < liste.getSize() ; i++)
						{
							//Wait for file to be ready
							if (Settings.btnWaitFileComplete.isSelected())
				            {
								File file = new File(liste.getElementAt(i));
								
								if (FunctionUtils.waitFileCompleted(file) == false)
									break;
				            }
							
							FFPROBE.Data(liste.getElementAt(i));
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e1) {}
							} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
							
							// IMPORTANT
							do {
								try {
									Thread.sleep(1);
								} catch (InterruptedException e1) {}
							} while (FFPROBE.isRunning);							

							if (i == 0)
							{
								timecode = Timecode.setTimecode();	
							}
							totalLength += FFPROBE.totalLength;
							FFPROBE.totalLength = 0;
							
							writer.println("file '" + liste.getElementAt(i) + "'");
						}				
						writer.close();
									
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
																			
						progressBar1.setMaximum((int) (totalLength / 1000));
						FFPROBE.totalLength = progressBar1.getMaximum();	
						FFMPEG.fileLength = progressBar1.getMaximum();
											
						lblCurrentEncoding.setText(Shutter.language.getProperty("babEncoding"));
				
						//Metadatas
			    		String metadatas = FunctionUtils.setMetadatas();
						
						//Output Name
						File fileOut = new File(fileOutputName);							
						
						//Audio
						String audio = setAudio();
						String audioMapping = setAudioMapping();
						
						String openGOP = "";
						if (Shutter.caseOpenGop.isSelected())
							openGOP = " -copyinkf";
						
						//Command
						String cmd = timecode + openGOP + " -video_track_timescale 90000 -c:v copy -c:s copy" + audio + " -map v:0?" + audioMapping + metadatas + " -map s? -y ";
						FFMPEG.run(" -safe 0 -f concat -i " + '"' + listeBAB.toString() + '"' + cmd + '"'  + fileOutputName + '"');		
				
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
													
						listeBAB.delete();							

						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							lastActions(fileOut);
						}
				
					} catch (InterruptedException | FileNotFoundException | UnsupportedEncodingException e) {
													
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						
						FFMPEG.error  = true;
					}				
			    }
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }
	
	private static String setAudio() {

		if (caseChangeAudioCodec.isSelected())
		{
			if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
			{
				switch (comboAudioCodec.getSelectedIndex()) 
				{
					case 0 :
						return " -c:a pcm_f32le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 1 :
						return " -c:a pcm_s32le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 2 :
						return " -c:a pcm_s24le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
					case 3 :
						return " -c:a pcm_s16le -ar " + lbl48k.getSelectedItem().toString() + " -b:a 1536k";
				}
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
			else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")))
			{
				return " -an";
			}
		}

		return " -c:a copy";		
	}
	
	private static String setAudioMapping() {
	
		String mapping = "";
		if (comboAudio1.getSelectedIndex() == 0
			&& comboAudio2.getSelectedIndex() == 1
			&& comboAudio3.getSelectedIndex() == 2
			&& comboAudio4.getSelectedIndex() == 3
			&& comboAudio5.getSelectedIndex() == 4
			&& comboAudio6.getSelectedIndex() == 5
			&& comboAudio7.getSelectedIndex() == 6
			&& comboAudio8.getSelectedIndex() == 7)
		{
			return " -map a?";	
		}
		else
		{
			if (comboAudio1.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio1.getSelectedIndex()) + "?";
			if (comboAudio2.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio2.getSelectedIndex()) + "?";
			if (comboAudio3.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio3.getSelectedIndex()) + "?";
			if (comboAudio4.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio4.getSelectedIndex()) + "?";
			if (comboAudio5.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio5.getSelectedIndex()) + "?";
			if (comboAudio6.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio6.getSelectedIndex()) + "?";
			if (comboAudio7.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio7.getSelectedIndex()) + "?";
			if (comboAudio8.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio8.getSelectedIndex()) + "?";
		}
		
		return mapping;
	}
	
	private static void lastActions(File fileOut) {
		
		FunctionUtils.cleanFunction(fileOut.toString(), fileOut, "");

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		
	}
}
