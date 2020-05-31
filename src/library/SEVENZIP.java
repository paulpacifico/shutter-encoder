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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import application.Console;
import application.Shutter;

public class SEVENZIP extends Shutter {
	
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static boolean error = false;
	
	public static void run(final String cmd, final boolean notUpdate){
		
	runProcess = new Thread(new Runnable()  {
		@Override
		public void run() {
			Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator());			
			try {
			String PathTo7za;
			ProcessBuilder process7za;
			if (System.getProperty("os.name").contains("Windows"))
			{
				PathTo7za = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathTo7za = PathTo7za.substring(1,PathTo7za.length()-1);
				PathTo7za = '"' + PathTo7za.substring(0,(int) (PathTo7za.lastIndexOf("/"))).replace("%20", " ")  + "/Library/7za.exe" + '"';
				process7za = new ProcessBuilder(PathTo7za + " " + cmd);
			}
			else
			{
				PathTo7za = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathTo7za = PathTo7za.substring(0,PathTo7za.length()-1);
				PathTo7za = PathTo7za.substring(0,(int) (PathTo7za.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/7za";
				process7za = new ProcessBuilder("/bin/bash", "-c" , PathTo7za + " " + cmd);
			}
				
			isRunning = true;	
			Process process = process7za.start();

			OutputStream out = process.getOutputStream();
	        out.write("os get /value".getBytes());
	        out.flush();
	         
	        InputStreamReader isr = new InputStreamReader(process.getInputStream());
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        
			//Analyse des données	
			do {
				line = br.readLine();
				Console.consoleFFMPEG.append(line + System.lineSeparator() );			
			}while(line !=null);		
				
			process.waitFor();		
			
			isRunning = false;	        
			
			} catch (IOException | InterruptedException e) {
				System.out.println(cmd);
				error = true;
			}
			
			 if (notUpdate)
			 {
				 if (error == false)
				 {
			       	try {
			       		Desktop.getDesktop().open(new File(lblDestination1.getText()));
			        } catch (IOException e1) {}
				 }
				 else
					 FFMPEG.cancelled = true;
			        	
				progressBar1.setIndeterminate(false);		
								
				FFMPEG.FinDeFonction();
				FFMPEG.enableAll();
			 }
			 
		}				
	});		
	runProcess.start();
	}

}


