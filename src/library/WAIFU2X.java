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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import application.Console;
import application.RenderQueue;
import application.Shutter;

public class WAIFU2X extends Shutter {
	
static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;

	public static void run(final String cmd) {
		
		error = false;
	    progressBar1.setValue(0);
		
	    Console.consoleWAIFU2X.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
	        RenderQueue.tableRow.addRow(new Object[] { lblCurrentEncoding.getText(), "waifu2x " + cmd, lblDestination1.getText()});
	        lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{			
			runProcess = new Thread(new Runnable()  {
				
				@Override
				public void run() {
					
					try {
						
						String PathToWAIFU2X;
						ProcessBuilder processWAIFU2X;
						
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToWAIFU2X = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToWAIFU2X = PathToWAIFU2X.substring(1,PathToWAIFU2X.length()-1);
							PathToWAIFU2X = '"' + PathToWAIFU2X.substring(0,(int) (PathToWAIFU2X.lastIndexOf("/"))).replace("%20", " ")  + "/Library/waifu2x.exe" + '"';
							processWAIFU2X = new ProcessBuilder(PathToWAIFU2X + " " + cmd);
						}
						else
						{
							PathToWAIFU2X = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToWAIFU2X = PathToWAIFU2X.substring(0,PathToWAIFU2X.length()-1);
							PathToWAIFU2X = PathToWAIFU2X.substring(0,(int) (PathToWAIFU2X.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/waifu2x";
							processWAIFU2X = new ProcessBuilder("/bin/bash", "-c" , PathToWAIFU2X + " " + cmd);
						}
						
						isRunning = true;	
						process = processWAIFU2X.start();
				         				        
				        String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
						
						while ((line = input.readLine()) != null)
						{							
						    Console.consoleWAIFU2X.append(line + System.lineSeparator());	
						}													
						process.waitFor();
										
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;
						}
							
				}				
			});		
			runProcess.start();
		}
	}

}
