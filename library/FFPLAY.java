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

package library;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import application.Console;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import settings.FunctionUtils;

public class FFPLAY extends Shutter {
	
static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;

	public static void run(final String cmd) {				
		
	    Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
	    
	    error = false;
	    
		runProcess = new Thread(new Runnable()  {

			public void run() {
				try {
					String PathToFFPLAY;
					ProcessBuilder processFFPLAY;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToFFPLAY = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPLAY = PathToFFPLAY.substring(1,PathToFFPLAY.length()-1);
						PathToFFPLAY = '"' + PathToFFPLAY.substring(0,(int) (PathToFFPLAY.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffplay.exe" + '"';							
						process = Runtime.getRuntime().exec(PathToFFPLAY + " " + cmd);							
					}
					else
					{
						PathToFFPLAY = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPLAY = PathToFFPLAY.substring(0,PathToFFPLAY.length()-1);
						PathToFFPLAY = PathToFFPLAY.substring(0,(int) (PathToFFPLAY.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffplay";
						processFFPLAY = new ProcessBuilder("/bin/bash", "-c" , PathToFFPLAY + " " + cmd);
						process = processFFPLAY.start();
					}	
					
					isRunning = true;						
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
					
					while ((line = input.readLine()) != null) {
												 
						 if (line.contains("nan") == false)
						 {
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							Console.consoleFFPLAY.append(line + System.lineSeparator() );	
						 }
						 
					}		
					
					process.waitFor();
					
					} catch (Exception e) {
						error = true;
					} finally {
						isRunning = false;
					}						
			}				
		});		
		runProcess.start();
	}
	
	public static void audioOnly(final String cmd) {				
	    Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
	    
	    error = false;
	    
		runProcess = new Thread(new Runnable()  {

			public void run() {
				try {
					String PathToFFPLAY;
					ProcessBuilder processFFPLAY;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToFFPLAY = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPLAY = PathToFFPLAY.substring(1,PathToFFPLAY.length()-1);
						PathToFFPLAY = '"' + PathToFFPLAY.substring(0,(int) (PathToFFPLAY.lastIndexOf("/"))).replace("%20", " ")  + "/Library/ffplay.exe" + '"';
						
						String pipe =  " | " + PathToFFPLAY + " -i " + '"' + "pipe:play" + '"' + " -nodisp -autoexit";										
						process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToFFPLAY.replace("ffplay", "ffmpeg") + " " + cmd + pipe});			
					}
					else
					{
						PathToFFPLAY = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToFFPLAY = PathToFFPLAY.substring(0,PathToFFPLAY.length()-1);
						PathToFFPLAY = PathToFFPLAY.substring(0,(int) (PathToFFPLAY.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffplay";
						
						String pipe =  " | " + PathToFFPLAY + " -i " + '"' + "pipe:play" + '"' + " -nodisp -autoexit";		
						processFFPLAY = new ProcessBuilder("/bin/bash", "-c" , PathToFFPLAY.replace("ffplay", "ffmpeg") + " " + cmd + pipe);	
						process = processFFPLAY.start();
					}	
					
					isRunning = true;						
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
					
					while ((line = input.readLine()) != null) {
												 
						 if (line.contains("nan") == false)
						 {
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							Console.consoleFFPLAY.append(line + System.lineSeparator() );	
						 }
						 
					}//While			
					
					process.waitFor();
					
					} catch (Exception e) {
						error = true;
					} finally {
						isRunning = false;
					}						
			}				
		});		
		runProcess.start();
	}

	public static void previewFilters(String filter, boolean transitions) {

		String seek = "";
		String flags = " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
		
		String option = "-fs";
		
		if (liste.getSize() > 0)
		{
			String file = "";
						
			//Selected file?
			if (fileList.getSelectedIndices().length > 0)
			{
				if (scanIsRunning)
				{
					File dir = new File(Shutter.liste.firstElement());
					for (File f : dir.listFiles()) {
						if (f.isHidden() == false && f.isFile()) {
							file = '"' + f.toString() + '"';
							break;
						}
					}
				} 
				else
					file = '"' + fileList.getSelectedValue().toString() + '"';

			} 
			else
				file = '"' + liste.firstElement() + '"';
			
			if (transitions)
			{				
				option = "-autoexit";
				
				if (inputDeviceIsRunning == false)
					FFPROBE.Data(file);
				
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				} while (FFPROBE.isRunning);
				
				if (filter.equals("fadeIn"))
				{
					long videoInValue = (long) (Integer.parseInt(spinnerVideoFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
					long audioInValue = (long) (Integer.parseInt(spinnerAudioFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
					
					long duration = (long) videoInValue + 2000;
					if (audioInValue > videoInValue && caseAudioFadeIn.isSelected())
						duration = (long) audioInValue + 2000;
					
					String color = "black";
					if (lblFadeInColor.getText().equals(language.getProperty("white")))
						color = "white";
					
					String videoFade = "";
					if (caseVideoFadeIn.isSelected())
						videoFade = " -vf " + '"' + "scale=1080:-1,fade=in:d=" + videoInValue + "ms:color=" + color + '"';
						
					String audioFade = "";
					if (caseAudioFadeIn.isSelected())
						audioFade = " -af " + '"' + "afade=in:d=" + audioInValue + "ms" + '"';
					
					filter = videoFade + audioFade + " -t " + duration + "ms";
				}
				else if (filter.equals("fadeOut"))
				{
					long videoOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
					long audioOutValue = (long) (Integer.parseInt(spinnerAudioFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
					
					long forward = (long) FFPROBE.totalLength - ((long) videoOutValue + 2000);
					if (audioOutValue > videoOutValue && caseAudioFadeOut.isSelected())
						forward = (long) FFPROBE.totalLength - ((long) audioOutValue + 2000);
					
					String color = "black";
					if (lblFadeOutColor.getText().equals(language.getProperty("white")))
						color = "white";
					
					long videoStart = (long) FFPROBE.totalLength - videoOutValue;
					long audioStart =  (long) FFPROBE.totalLength - audioOutValue;
					
					String videoFade = "";
					if (caseVideoFadeOut.isSelected())
						videoFade = " -vf " + '"' + "scale=1080:-1,fade=out:st=" + videoStart + "ms:d=" + videoOutValue + "ms:color=" + color + '"';
						
					String audioFade = "";
					if (caseAudioFadeOut.isSelected())
						audioFade = " -af " + '"' + "afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms" + '"';
					
					seek = " -ss " + forward + "ms ";
					filter = videoFade + audioFade;
				}
			}
			
			if (inputDeviceIsRunning)
			{		
				if (transitions == false)
				{
					filter = " -vf " + '"' + filter + ",scale=iw/2:ih/2" + '"';
				}
				
				String device[] = RecordInputDevice.setInputDevices().replace("-thread_queue_size 4096", " ").split("-f ");
				
				if ((liste.getElementAt(0).equals("Capture.current.screen") || System.getProperty("os.name").contains("Mac")) && RecordInputDevice.audioDeviceIndex > 0)
				{
					FFPLAY.run(flags + " -f " + device[2] + filter);
				}
				else
					FFPLAY.run(flags + " -f " + device[1] + filter);
			}				
			else if (caseEnableSequence.isSelected())
			{		
				if (transitions == false)
				{
					filter = " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"	+ filter + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"';
				}
				
				File f = new File(liste.firstElement());
				
				String extension =  f.getName().substring(f.getName().lastIndexOf("."));
				
				//Output folder
				String output = FunctionUtils.setOutputDestination("", f);
				
				//Concat file
				String concat = FunctionUtils.setConcat(f, output);		
				
				File concatList = new File(output.replace("\\", "/") + "/" + f.getName().replace(extension, ".txt"));				
	
				FFPLAY.run(option + flags + concat + " -i " + '"' + concatList + '"' + filter);
				
				do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				} while (FFPLAY.isRunning);
				
				if (caseChangeFolder1.isSelected() == false)
				{
					lblDestination1.setText(language.getProperty("sameAsSource"));
				}
				
				concatList.delete();
			}
			else
			{	
				if (transitions == false)
				{
					filter = " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]" + filter + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"';
				}
				
				FFPLAY.run(option + seek + flags + " -i " + file + filter);
			}			
		}
	}
	
}
