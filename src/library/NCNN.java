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

public class NCNN extends Shutter {
	
static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;
public static String modelsPath;

	public static void run(final String cmd) {
		
		error = false;
	    progressBar1.setValue(0);
	    tempsEcoule.setVisible(false);
	    				    
	    Console.consoleNCNN.append(Shutter.language.getProperty("command") + " " + cmd);
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled() && cmd.contains("image2pipe") == false  && cmd.contains("preview.bmp") == false && cmd.contains("preview.png") == false)
		{
	        RenderQueue.tableRow.addRow(new Object[] { lblCurrentEncoding.getText(), "ncnn" + cmd, lblDestination1.getText()});
	        lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{			
			isRunning = true;
			
			runProcess = new Thread(new Runnable()  {
				
				@Override
				public void run() {
										
					try {
						
						String PathToNCNN;
						ProcessBuilder processNCNN;
													
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToNCNN = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToNCNN = PathToNCNN.substring(1,PathToNCNN.length()-1);
							PathToNCNN = '"' + PathToNCNN.substring(0,(int) (PathToNCNN.lastIndexOf("/"))).replace("%20", " ")  + "/Library/realesrgan-ncnn-vulkan.exe" + '"';
							processNCNN = new ProcessBuilder(PathToNCNN + cmd);
						}
						else
						{
							PathToNCNN = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToNCNN = PathToNCNN.substring(0,PathToNCNN.length()-1);
							PathToNCNN = PathToNCNN.substring(0,(int) (PathToNCNN.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/realesrgan-ncnn-vulkan";
							processNCNN = new ProcessBuilder("/bin/bash", "-c" , PathToNCNN + cmd);
						}

						process = processNCNN.start();
				         				        
				        String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
						
						if (caseCreateSequence.isSelected() == false)
						{
							progressBar1.setValue(0);
							progressBar1.setMaximum(100);
						}
						
						Console.consoleNCNN.append(System.lineSeparator());	
						
						int progressValue = 0;
						
						while ((line = input.readLine()) != null)
						{							
						    Console.consoleNCNN.append(line + System.lineSeparator());	

						    if (line.contains("%") && caseCreateSequence.isSelected() == false)
						    {
						    	String s[] = line.split("\\.");
						    	if (System.getProperty("os.name").contains("Windows"))
						    	{
						    		s = line.split(",");
						    	}
						    	
								progressBar1.setValue(Integer.parseInt(s[0]));
						    }
						    else if (line.contains("done"))
						    {
						    	progressValue ++;
								progressBar1.setValue(progressValue);
						    }
						}													
						process.waitFor();	
						
						Console.consoleNCNN.append(System.lineSeparator());
																						
						} catch (IOException | InterruptedException e) {							
							error = true;
						} finally {							
							
							isRunning = false;
							
							if (caseCreateSequence.isSelected() == false)
							{
								if (cancelled == false)
								{
									progressBar1.setValue(100);
								}
								else
									progressBar1.setValue(0);
							}
						}
							
				}				
			});		
			runProcess.start();
		}
	}

}
