/*******************************************************************************************
* Copyright (C) 2020 PACIFICO PAUL
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
import java.io.InputStreamReader;

import application.Console;
import application.Shutter;

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

}
