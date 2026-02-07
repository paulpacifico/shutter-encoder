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

import application.Console;
import application.Shutter;

public class DEMUCS extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	private static File demucsFolder = new File(documents.toString() + "/Library/demucs");
	private static File demucs;
	public static boolean error = false;
	public static boolean isRunning = false;

	public static void checkDemucs() {

		if (System.getProperty("os.name").contains("Windows"))
		{
			demucs = new File(demucsFolder + "/demucs/demucs.py");
		}
		else
			demucs = new File(demucsFolder + "/bin/demucs");
		
		//Download ffmpeg for Linux because it need shared libs
		if (System.getProperty("os.name").contains("Linux"))
		{
			PYTHON.checkFFmpegForLinux();			
		}
							
		if (demucs.exists() == false)
		{		
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("additionalFiles") + System.lineSeparator() + Shutter.language.getProperty("wantToDownload"), Shutter.language.getProperty("functionSeparation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{	    									
				if (System.getProperty("os.name").contains("Windows"))
				{
					String[] cmd = { demucsFolder.toString() + "/python.exe", "-m", "pip", "install", "demucs", "torchcodec", "--target", demucsFolder.toString(), "--no-warn-script-location" };
					PYTHON.installModule(demucsFolder, cmd, demucs);						 	
				}
				else
				{
					String[] cmd = { demucsFolder.toString() + "/bin/python3", "-m", "pip", "install", "demucs", "torchcodec", "--no-warn-script-location" };
					
					if (System.getProperty("os.name").contains("Mac") && arch.equals("x86_64"))
					{
						cmd = new String[] { demucsFolder.toString() + "/bin/python3", "-m", "pip", "install", "demucs", "torchcodec", "numpy==1.25.2", "--no-warn-script-location" };
					}
					
					PYTHON.installModule(demucsFolder, cmd, demucs);			
				}
			}
			else
				comboFonctions.setSelectedItem("");			
		}
	}
		
	public static void run(String model, String output, String file, String selection) {
		
		disableAll();
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {		
										
					ProcessBuilder processBuilder = new ProcessBuilder();
					
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder.command().add(demucsFolder.toString() + "/python.exe");
						processBuilder.command().add("-c");
						processBuilder.command().add(
						    "import os; os.add_dll_directory(r'" +
						    new File(FFMPEG.PathToFFMPEG).getParent().replace("/", "\\") +
						    "'); import runpy; runpy.run_module('demucs', run_name='__main__')"
						);							
					}
					else
					{
						processBuilder.command().add(demucsFolder.toString() + "/bin/python3");
						processBuilder.command().add("-m");
						processBuilder.command().add("demucs");
					}
					
					processBuilder.command().add("-n");
					processBuilder.command().add(model);
					
					//Separate all tracks
					if (comboFilter.getSelectedIndex() != 0)
						processBuilder.command().add("--two-stems=" + selection);
					
					//Audio bit depth
					if (FFPROBE.audioCodec.contains("pcm_s24") || FFPROBE.audioCodec.contains("pcm_f32"))
					{
						processBuilder.command().add("--int24");
						processBuilder.command().add("--flac");
					}
					
					processBuilder.command().add("-o");
					processBuilder.command().add(output);
					processBuilder.command().add("--filename");
					processBuilder.command().add("../{stem}.{ext}");
					processBuilder.command().add(file);
				
					Console.consolePYTHON.append(language.getProperty("command") + String.join(" ", processBuilder.command()));	
					
					//Adding ffmpeg the the PATH environment						        			        
			        if (System.getProperty("os.name").contains("Mac"))
			        {
			        	Map<String, String> env = processBuilder.environment();			        	
			        	env.put("PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", "") + ":" + System.getenv("PATH"));
			        	env.put("DYLD_LIBRARY_PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", ""));
			        }
			        
			        processBuilder.environment().put("PYTHONUNBUFFERED", "1");
			        processBuilder.environment().put("TERM", "xterm"); // Tricks tqdm into thinking it's a real terminal
					processBuilder.redirectErrorStream(true);

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