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

import javax.swing.JOptionPane;
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
			PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python";
			demucs = new File(PYTHON_DIR + "/demucs/demucs.py");
		}
		else
		{
			PYTHON_DIR = PYTHON_DIR.substring(0,PYTHON_DIR.length()-1);		
			PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python/bin";
			demucs = new File(PYTHON_DIR + "/demucs");
		}		
		
		if (System.getProperty("os.name").contains("Linux"))
		{
			checkFFmpegForLinux();			
		}
							
		if (demucs.exists() == false)
		{		
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("additionalFiles") + System.lineSeparator() + Shutter.language.getProperty("wantToDownload"), Shutter.language.getProperty("functionSeparation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{	    									
				installDemucs();    							 	
			}
			else
				comboFonctions.setSelectedItem("");
			
		}
	}

	@SuppressWarnings("unused")
	public static void checkFFmpegForLinux() {
		
	    try {
	        Process process = new ProcessBuilder("ffmpeg", "-version").start();
	    } catch (Exception e) {	    	
	    	try {		    		
	    		Process process = new ProcessBuilder("pkexec", "apt-get", "install", "-y", "ffmpeg").start();		        
	    	 } catch (Exception e1) {
	    		 try {	
	    			 Process process = new ProcessBuilder("pkexec", "dnf", "install", "-y", "ffmpeg").start();		   
	 	    	 } catch (Exception e2) {}
	    	 }
	    }
	}
	
	public static void installDemucs() {

		disableAll();
		btnStart.setEnabled(false);
		progressBar1.setIndeterminate(true);
		progressBar1.setStringPainted(false);
		lblCurrentEncoding.setText(language.getProperty("update") + "...");
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {	
					
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{						
						//Install pip
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python.exe", PYTHON_DIR + "/get-pip.py", "--target", PYTHON_DIR);
						processBuilder.redirectErrorStream(true);
						
						process = processBuilder.start();
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						 
						String line;		           
			            while ((line = reader.readLine()) != null)
			            {
			            	if (cancelled)
			            		break;
			            	
			            	lblCurrentEncoding.setText(line);
			            	Console.consoleDEMUCS.append(line + System.lineSeparator());
			            }
			            process.waitFor();
						
						//Then install demucs
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python.exe", "-m", "pip", "install" ,"demucs", "torchcodec", "--target", PYTHON_DIR, "--no-warn-script-location");
					}
					else
					{
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "pip", "install" ,"demucs", "torchcodec", "--no-warn-script-location");
					}
					
		            processBuilder.redirectErrorStream(true);
		            process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            String line;		           
		            while ((line = reader.readLine()) != null)
		            {
		            	if (cancelled)
		            		break;
		            	
		            	lblCurrentEncoding.setText(line);
		            	Console.consoleDEMUCS.append(line + System.lineSeparator());
		            }
		            process.waitFor();
		            		            
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
				finally {
					
					if (demucs.exists() == false || cancelled)
					{
						comboFonctions.setSelectedItem("");	
						lblCurrentEncoding.setText(language.getProperty("lblEncodageEnCours"));
					}
					else
					{
						lblCurrentEncoding.setText(language.getProperty("processEnded"));
						Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
					}
					
					SwingUtilities.invokeLater(new Runnable()
					{
			           @Override
			           public void run() {
			        	   Shutter.progressBar1.setIndeterminate(false);
			        	   progressBar1.setStringPainted(true);			        	   
			           }
					});
					 
					enableAll();
				}
			}			
		});
		runProcess.start();
	}
	
	public static void run(String model, String output, String file) {
		
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
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python.exe", "-c", "import os; os.add_dll_directory(r'" + new File(FFMPEG.PathToFFMPEG).getParent().replace("/", "\\") + "'); " +
						"import runpy; runpy.run_module('demucs', run_name='__main__')", "-n", model, "-o", output, "--filename" ,"../{track}/{stem}.{ext}", file);
					}
					else
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "demucs", "-n", model, "-o", output, "--filename" ,"../{track}/{stem}.{ext}", file);
					
					//Adding ffmpeg the the PATH environment						        			        
			        if (System.getProperty("os.name").contains("Mac"))
			        {
			        	Map<String, String> env = processBuilder.environment();	
			        	env.put("DYLD_LIBRARY_PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", ""));
			        }
			        
					processBuilder.redirectErrorStream(true);
					 
					Console.consoleDEMUCS.append(language.getProperty("command") + " " + PYTHON_DIR + "/python3 -m demucs -n " + model + " -o " + output + " --filename ../{track}/{stem}.{ext} " + file);	
					
					isRunning = true;	
					process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            Console.consoleDEMUCS.append(System.lineSeparator());
			        
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