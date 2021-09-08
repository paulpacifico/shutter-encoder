/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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
import java.io.OutputStream;

import application.Console;
import application.RenderQueue;
import application.Shutter;

public class BMXTRANSWRAP extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;

	public static void run(final String cmd) {				
	    Console.consoleBMXTRANSWRAP.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
	    
		error = false;
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
	        RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "bmxtranswrap " + cmd, lblDestination1.getText()});
	        lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{
			disableAll();
			
		    progressBar1.setValue(0);
		    progressBar1.setMaximum(100);
		    
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToBMX;
						ProcessBuilder processBMX;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToBMX = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToBMX = PathToBMX.substring(1,PathToBMX.length()-1);
							PathToBMX = '"' + PathToBMX.substring(0,(int) (PathToBMX.lastIndexOf("/"))).replace("%20", " ")  + "/Library/bmxtranswrap.exe" + '"';
							processBMX = new ProcessBuilder(PathToBMX + " " + cmd);
						}
						else
						{
							PathToBMX = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToBMX = PathToBMX.substring(0,PathToBMX.length()-1);
							PathToBMX = PathToBMX.substring(0,(int) (PathToBMX.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/bmxtranswrap";
							processBMX = new ProcessBuilder("/bin/bash", "-c" , PathToBMX + " " + cmd);
						}
						
						isRunning = true;	
						process = processBMX.start();
																		
						OutputStream out = process.getOutputStream();
				        out.write("os get /value".getBytes());
				        out.flush();
				         
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);
				        String line;		
						
				        //Analyse des données	
						do {
							line = br.readLine();
							
							if (line != null)
							{
								if (line.contains("%"))
								{
									String s[] = line.split("\\.");
									int value = Integer.valueOf(s[0].replace(" ", ""));
									progressBar1.setValue(value);
								}
							}
																			
						} while(line !=null);					
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
