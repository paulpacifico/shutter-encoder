/*******************************************************************************************
* Copyright (C) 2026 PACIFICO PAUL
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
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;

public class BACKGROUNDREMOVER extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	private static File backgroundRemoverFolder = new File(documents.toString() + "/Library/backgroundremover");
	private static File backgroundRemover;
	public static boolean error = false;
	public static boolean isRunning = false;

	public static void checkBackgroundRemover() {

		if (System.getProperty("os.name").contains("Windows"))
		{
			backgroundRemover = new File(backgroundRemoverFolder + "/backgroundRemover/backgroundRemover.py");
		}
		else
			backgroundRemover = new File(backgroundRemoverFolder + "/bin/backgroundRemover");
								
		if (backgroundRemover.exists() == false)
		{		
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("additionalFiles") + System.lineSeparator() + Shutter.language.getProperty("wantToDownload"), Shutter.language.getProperty("functionBackgroundRemover"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{	    	
				String[] cmd = { backgroundRemoverFolder.toString() + "/bin/python3", "-m", "pip", "install", "backgroundremover", "--no-warn-script-location" };
				if (System.getProperty("os.name").contains("Windows"))
				{
					cmd = new String[] { backgroundRemoverFolder.toString() + "/python.exe", "-m", "pip", "install", "backgroundremover", "--no-warn-script-location" };
											 	
				}
				
				PYTHON.installModule(backgroundRemoverFolder, cmd, backgroundRemover);
			}
			else
				comboFonctions.setSelectedItem("");			
		}
	}

	public static void run(String file, String output) {
		
		disableAll();
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {		
										
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder = new ProcessBuilder(backgroundRemoverFolder.toString() + "/python.exe", "-m", "backgroundremover", "-i", file, "-o", output);
					}
					else
						processBuilder = new ProcessBuilder(backgroundRemoverFolder.toString() + "/bin/backgroundremover", "-i", file, "-o", output);

					processBuilder.redirectErrorStream(true);
					 
					Console.consolePYTHON.append(language.getProperty("command") + " backgroundremover -i " + file + " -o " + output);	
					
					isRunning = true;	
					process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            Console.consolePYTHON.append(System.lineSeparator());
			        
					progressBar1.setMaximum(100);
		            		            
		            String line;		
		            boolean downloadModel = false;
		            while ((line = reader.readLine()) != null)
		            {
		            	if (line.contains("Downloading"))
		            		downloadModel = true;
		            	
		            	if (downloadModel)
		            	{
		            		if (line.contains("Selected model"))
		            		{
		            			downloadModel = false;
		            		}
		            		else
		            			lblCurrentEncoding.setText(language.getProperty("downloadingAIModel"));
		            	}
		            	else
		            		lblCurrentEncoding.setText(new File(file).getName());
		            	
		            	if (line.contains("%"))
		            	{
		            		String s[] = line.split("%");
		            		progressBar1.setValue(Integer.valueOf(s[0].replace(" ","")));
		            	} 
		            	
		            	if (line.contains("RuntimeError"))
		            		error = true;		            	
		            	
		            	if (cancelled)
		            		break;
		            	
		                Console.consolePYTHON.append(line + System.lineSeparator());
		            }
		            
		            process.waitFor();
		            
		            Console.consolePYTHON.append(System.lineSeparator());
					
					isRunning = false;	    
		            
				} catch (Exception e) {
					error = true;
					e.printStackTrace();
				}
				finally {
					enableAll();
				}
			}
			
		});
		runProcess.start();
	}
}