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
import java.util.Map;

import javax.swing.SwingUtilities;

import application.Console;
import application.Shutter;

public class DEMUCS extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	private static File demucs;
	public static String PYTHON_DIR;
	public static boolean error = false;
	public static boolean isRunning = false;

	public static void checkDemucs() {
		
		PYTHON_DIR = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows"))
		{
			PYTHON_DIR = PYTHON_DIR.substring(1,PYTHON_DIR.length()-1);	
			demucs = new File("Library/python/bin/demucs.exe");
		}
		else
		{
			PYTHON_DIR = PYTHON_DIR.substring(0,PYTHON_DIR.length()-1);		
			demucs = new File("Library/python/bin/demucs");
		}
			
		PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python/bin";
						
		if (demucs.exists() == false)
		{			
			installDemucs();
		}
	}
	
	public static void installDemucs() {

		btnStart.setEnabled(false);
		
		disableAll();
		progressBar1.setIndeterminate(true);
		progressBar1.setStringPainted(false);
		lblCurrentEncoding.setText("Downloading additional files...");
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {					
					
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3.exe", "-m", "pip", "install" ,"demucs", "torchcodec");
					}
					else
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "pip", "install" ,"demucs", "torchcodec");
					
		            processBuilder.redirectErrorStream(true);
		            process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            String line;		           
		            while ((line = reader.readLine()) != null)
		            {
		            	if (cancelled)
		            		break;
		            	
		            	Console.consoleDEMUCS.append(line + System.lineSeparator());
		            }
		            
		            process.waitFor();
		            		            
		        } catch (Exception e) {}
				finally {
					
					if (demucs.exists() == false || cancelled)
					{
						comboFonctions.setSelectedItem("");						
					}
					
					SwingUtilities.invokeLater(new Runnable()
					{
			           @Override
			           public void run() {
			        	   Shutter.progressBar1.setIndeterminate(false);
			        	   progressBar1.setStringPainted(true);
			        	   lblCurrentEncoding.setText(language.getProperty("lblEncodageEnCours"));
			           }
					});
					 
					enableAll();
				}
			}			
		});
		runProcess.start();
	}
	
	public static void run(String model, String output, String file) {
		
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		disableAll();
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {		
										
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3.exe", "-m", "demucs", "-n", model, "-o", output, "--filename" ,"../{track}/{stem}.{ext}", file);
					}
					else
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "demucs", "-n", model, "-o", output, "--filename" ,"../{track}/{stem}.{ext}", file);
					
					//Adding ffmpeg the the PATH environment
					Map<String, String> env = processBuilder.environment();
			        String currentPath = env.get("PATH");
			        
			        if (System.getProperty("os.name").contains("Windows"))
					{
			        	env.put("PATH", new File(FFMPEG.PathToFFMPEG).getParent() + ";" + currentPath);
					}
			        else
			        {
			        	env.put("DYLD_LIBRARY_PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", ""));
			        }
			        
					processBuilder.redirectErrorStream(true);
					 
					Console.consoleDEMUCS.append(language.getProperty("command") + PYTHON_DIR + "/python3 -m demucs -n " + model + " -o " + output + " " + file);	
					
					isRunning = true;	
					process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            Console.consoleDEMUCS.append(System.lineSeparator());
			        
					progressBar1.setMaximum(100);
		            		            
		            String line;		           
		            while ((line = reader.readLine()) != null)
		            {
		            	if (line.contains("%"))
		            	{
		            		String s[] = line.split("%");
		            		progressBar1.setValue(Integer.valueOf(s[0].replace(" ","")));
		            	} 
		            	
		            	if (cancelled)
		            		break;
		            	
		                Console.consoleDEMUCS.append(line + System.lineSeparator());
		            }
		            
		            process.waitFor();
		            
		            Console.consoleDEMUCS.append(System.lineSeparator());
					
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
		
