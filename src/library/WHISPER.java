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

package library;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Locale;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.Update;
import application.Utils;

public class WHISPER extends Shutter {
	
	public static boolean error = false;
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static String PathToWHISPER;
	public static Process process;
	public static String whisperModel;
	public static String modelLink = "";	
	public static String modelName;
	public static long modelSize;

	public static void getWhisperModel() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			whisperModel = PathToWHISPER.replace("whisper-cli.exe", "models/" + modelName);
		}
		else
			whisperModel = PathToWHISPER.replace("whisper-cli", "models/" + modelName);
	}
	
	public static void run(final String cmd) {
		
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
								
				try {
					
					ProcessBuilder processWHISPER;
					if (System.getProperty("os.name").contains("Windows"))
					{						
						processWHISPER = new ProcessBuilder('"' + PathToWHISPER + '"' + " -m " + '"' + whisperModel + '"' + cmd);
					}
					else
					{
						processWHISPER = new ProcessBuilder("/bin/bash", "-c" , PathToWHISPER.replace(" ", "\\ ") + " -m " + '"' + whisperModel + '"' + cmd);
					}
					
					processWHISPER.redirectErrorStream(true);
					
					Console.consoleWHISPER.append(Shutter.language.getProperty("command") + " " + " -m " + '"' + whisperModel + '"' + cmd);	
						
					isRunning = true;	
					process = processWHISPER.start();
		
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String line;
			        
			        Console.consoleWHISPER.append(System.lineSeparator());
			        
					progressBar1.setMaximum(Math.round(FFPROBE.totalLength / 1000));
					
					do {
						
						if (cancelled)
							break;
						
						line = br.readLine();					
						
						if (line != null && line.contains(" --> "))
						{
							Console.consoleWHISPER.append(line + System.lineSeparator());
							
							String s[] = line.split("]");
							String s2[] = s[0].split(" ");
							String s3[] = s2[2].split("\\.");
							String s4[] = s3[0].split(":");
							
							int value = Integer.parseInt(s4[0]) * 3600 + Integer.parseInt(s4[1]) * 60 + Integer.parseInt(s4[2]);
							progressBar1.setValue(value);
						}
						
					} while (line != null);		
						
					process.waitFor();		
					
					Console.consoleWHISPER.append(System.lineSeparator());
					
					isRunning = false;	        
					
					} catch (IOException | InterruptedException e) {
						error = true;
					}
									 
				}				
			});		
			runProcess.start();
	}

	public static void downloadModel() {
				
		Object[] options = { "Accurate", "Balanced", "Fast" };
		int m = JOptionPane.showOptionDialog(frame, language.getProperty("downloadModel") + language.getProperty("colon"),
				language.getProperty("functionTranscribe"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
		
		switch (m)
		{
			case 0: //Accurate
				
				modelLink = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-large-v3.bin?download=true";
				modelName = "ggml-large-v3-q5_0.bin";
				modelSize = 1081140203L;
				break;			
				
			case 1: //Balanced
				
				modelLink = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-large-v3-turbo-q8_0.bin?download=true";
				modelName = "ggml-large-v3-turbo-q8_0.bin";
				modelSize = 874188075L;				
				break;
				
			case 2: //Fast
				
				modelLink = "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-medium-q5_0.bin?download=true";
				modelName = "ggml-medium-q5_0.bin";
				modelSize = 539212467L;		
				break;
			
		}
		
		getWhisperModel();
		
		File model = new File(whisperModel);
		
		try {
			if (model.exists() && Files.size(model.toPath()) != modelSize)				
			{
				model.delete();
			}
		} catch (IOException e1) {}
		
		if (model.exists() == false)
		{
			new Update();
			
			if (getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
			{
				Update.lblNewVersion.setText(language.getProperty("update"));
			}
			else
				Update.lblNewVersion.setText(language.getProperty("update") + "...");
			
			//Download
			Thread download = new Thread(new Runnable() {
				
				public void run() {		
			
					Utils.changeFrameVisibility(frame, true);
					
					Update.HTTPDownload(modelLink, whisperModel);	
					
					Update.frame.dispose();
					
					Utils.changeFrameVisibility(frame, false);
					frame.toFront();
					
					if (model.exists() == false)
					{
						comboFonctions.setSelectedItem("");;
					}
				}
			});
			download.start();
		}
		
	}
   
}