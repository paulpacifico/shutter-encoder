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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import application.Console;
import application.RenderQueue;
import application.Shutter;

public class TSMUXER extends Shutter {
	
static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;

	public static void run(final String cmd) {
		
	    Console.consoleTSMUXER.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
	        RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "tsMuxeR " + cmd, lblDestination1.getText()});
	        lblEncodageEnCours.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{
		    progressBar1.setValue(0);
		    progressBar1.setMaximum(100);
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToDVDAUTHOR;
						ProcessBuilder processDVDAUTHOR;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToDVDAUTHOR = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToDVDAUTHOR = PathToDVDAUTHOR.substring(1,PathToDVDAUTHOR.length()-1);
							PathToDVDAUTHOR = '"' + PathToDVDAUTHOR.substring(0,(int) (PathToDVDAUTHOR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/tsMuxeR.exe" + '"';
							processDVDAUTHOR = new ProcessBuilder(PathToDVDAUTHOR + " " + cmd);
						}
						else
						{
							PathToDVDAUTHOR = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToDVDAUTHOR = PathToDVDAUTHOR.substring(0,PathToDVDAUTHOR.length()-1);
							PathToDVDAUTHOR = PathToDVDAUTHOR.substring(0,(int) (PathToDVDAUTHOR.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/tsMuxeR";
							processDVDAUTHOR = new ProcessBuilder("/bin/bash", "-c" , PathToDVDAUTHOR + " " + cmd);
						}
						
						isRunning = true;	
						process = processDVDAUTHOR.start();
				         
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);
				        
				        String line;	
						do {
							line = br.readLine();							
							
						    Console.consoleTSMUXER.append(line + System.lineSeparator());		
						    
						    if (line != null)
						    {
								if (line.contains("%"))
								{
									String s[] = line.split("\\.");
									int value = Integer.parseInt(s[0]);		
									progressBar1.setValue(value);
								}
						    }
							          						        		
						} while (line !=null);  
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
