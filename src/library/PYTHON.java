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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;

import javax.swing.SwingUtilities;

import application.Console;
import application.Shutter;

public class PYTHON extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	public static String PYTHON_DIR = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();;

	public static void getPythonPath() {		
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			PYTHON_DIR = PYTHON_DIR.substring(1,PYTHON_DIR.length()-1);	
			PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python";
		}
		else
		{
			PYTHON_DIR = PYTHON_DIR.substring(0,PYTHON_DIR.length()-1);		
			PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python/bin";
		}	
	
	}
	
	public static void createVirtualEnvironment(String output) throws InterruptedException, IOException {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			//There is no venv module on Windows...
			Files.walk(new File(PYTHON_DIR).toPath()).forEach(path -> {
	        
			 try {
		            Path dest = new File(output).toPath().resolve(new File(PYTHON_DIR).toPath().relativize(path));
		            if (Files.isDirectory(path)) {
		               Files.createDirectories(dest);					
		            } else {
		                Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
		            }
			 	} catch (IOException e) {
			 		e.printStackTrace();
				}
	        });
		}
		else
		{
			ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "venv", output);
			process = processBuilder.start();
			process.waitFor();
		}
	}
		
	public static void installModule(File modulePath, String[] cmd, File module) {
		
		disableAll();
		btnStart.setEnabled(false);
		progressBar1.setIndeterminate(true);
		progressBar1.setStringPainted(false);
		lblCurrentEncoding.setText(language.getProperty("update") + "...");
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
									
				try {	
					
					//Make virtual environment
					if (modulePath.exists() == false)
					{
						modulePath.mkdirs();
						PYTHON.createVirtualEnvironment(modulePath.toString());			
					}
					
					File tempFolder = new File("C:\\temp");
					
					ProcessBuilder processBuilder;
					processBuilder = new ProcessBuilder(cmd);
					
					if (System.getProperty("os.name").contains("Windows"))
					{
						//Avoid long path issue	
						Map<String, String> env = processBuilder.environment();	
			        	env.put("TEMP", tempFolder.toString());
			            env.put("TMP", tempFolder.toString());
			            env.put("TMPDIR", tempFolder.toString());
			            env.put("TEMPDIR", tempFolder.toString());		
			            
			            if (tempFolder.exists() == false)
			            	tempFolder.mkdirs();
					}
					
					Console.consolePYTHON.append(language.getProperty("command") + " " + Arrays.toString(cmd) + " --no-warn-script-location" + System.lineSeparator());
					
		            processBuilder.redirectErrorStream(true);
		            process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            String line;		           
		            while ((line = reader.readLine()) != null)
		            {
		            	if (cancelled)
		            		break;
		            	
		            	lblCurrentEncoding.setText(line);
		            	Console.consolePYTHON.append(line + System.lineSeparator());
		            }
		            process.waitFor();
		            
		            if (System.getProperty("os.name").contains("Windows") && tempFolder.exists())
					{	
		            	tempFolder.delete();
					}
		            		            
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
				finally {
					
					if (module.exists() == false || cancelled)
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
}
