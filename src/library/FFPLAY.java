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

			@SuppressWarnings("deprecation")
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

	public static void previewFilters(String filter) {

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

			
			if (inputDeviceIsRunning)
			{		
				filter = " -vf " + '"' + filter + ",scale=iw/2:ih/2" + '"';
								
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
				filter = " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]"	+ filter + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"';				
				
				File f = new File(liste.firstElement());
				
				String extension =  f.getName().substring(f.getName().lastIndexOf("."));
				
				//Output folder
				String output = FunctionUtils.setOutputDestination("", f);
				
				//Concat file
				FunctionUtils.setConcat(f, output);		
				
				File concatList = new File(output.replace("\\", "/") + "/" + f.getName().replace(extension, ".txt"));				
	
				FFPLAY.run(option + flags + " -safe 0 -f concat -i " + '"' + concatList + '"' + filter);
				
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
				filter = " -vf " + '"' + "[in]split=2[v0][v1];[v0]scale=iw/2:ih/2[video1];[v1]" + filter + ",scale=iw/2:ih/2[video2];[video1][video2]hstack" + '"';
							
				FFPLAY.run(option + seek + flags + " -i " + file + filter);
			}			
		}
	}
	
}
