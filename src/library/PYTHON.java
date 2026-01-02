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
		}
		else		
			PYTHON_DIR = PYTHON_DIR.substring(0,PYTHON_DIR.length()-1);	
		
		PYTHON_DIR = PYTHON_DIR.substring(0,(int) (PYTHON_DIR.lastIndexOf("/"))).replace("%20", " ")  + "/Library/python.7z";	
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
	
	public static boolean isCudaInstalled() {
    	
        try {
            ProcessBuilder pb = new ProcessBuilder("nvidia-smi");
            process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Look for CUDA Version in nvidia-smi output
                if (line.contains("CUDA Version")) {
                    // Extract version number (e.g., "CUDA Version: 12.1")
                    // The line format is like: "| CUDA Version: 12.1     |"
                    String version = extractCudaVersion(line);
                    
                    if (version != null) {
                        System.out.println("Found CUDA Version: " + version);
                        
                        // Check if it's CUDA 12 or higher
                        try {
                            String majorVersion = version.split("\\.")[0];
                            int major = Integer.parseInt(majorVersion);
                            if (major >= 12) {
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Could not parse CUDA version number");
                        }
                    }
                }
            }
            
            process.waitFor();
            return false;
            
        } catch (Exception e) {
            System.out.println("nvidia-smi not found or error occurred: " + e.getMessage());
            return false;
        }
    }
    
    private static String extractCudaVersion(String line) {
        // Split by "CUDA Version:" and take the part after it
        String[] parts = line.split("CUDA Version:");
        if (parts.length > 1) {
            String versionPart = parts[1].trim();
            
            // Remove any trailing characters (like pipe |, spaces, etc.)
            // Extract just the version number (e.g., "12.1")
            String[] tokens = versionPart.split("\\s+");
            if (tokens.length > 0) {
                String version = tokens[0].trim();
                // Remove any non-numeric trailing characters like |
                version = version.replaceAll("[^0-9.]", "");
                return version;
            }
        }
        return null;
    }

    public static boolean isTorchCudaInstalled() {
    	
        try {
        	
        	 ProcessBuilder processBuilder = new ProcessBuilder();
 			
 			if (System.getProperty("os.name").contains("Windows"))
 			{
 				processBuilder.command().add(WHISPER.whisperFolder.toString() + "/python.exe");
 			}
 			else
 				processBuilder.command().add(WHISPER.whisperFolder.toString() + "/bin/python3");
 			
 			processBuilder.command().add("-m");
 			processBuilder.command().add("pip");
 			processBuilder.command().add("show");
 			processBuilder.command().add("torch");
 			
            process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String line;            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Version:")) {
                    // Check if version contains CUDA indicator (e.g., +cu121)
                    if (line.contains("+cu12")) {
                        System.out.println("Found PyTorch: " + line);
                        return true;
                    }
                }
            }
            
            process.waitFor();            
            return false;
            
        } catch (Exception e) {
            System.err.println("Error checking PyTorch installation: " + e.getMessage());
            return false;
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
						SEVENZIP.run("x " + '"' + PYTHON_DIR + '"' + " -o" + '"' + modulePath + '"', false);	
						
						do {
							Thread.sleep(10);
						} while (SEVENZIP.runProcess.isAlive());
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
