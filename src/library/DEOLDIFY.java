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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import application.Console;
import application.Shutter;
import application.Update;
import application.Utils;

public class DEOLDIFY extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	private static File deoldify;
	public static String PYTHON_DIR;
	public static boolean error = false;
	public static boolean isRunning = false;
	
	public static String deoldifyModel;
	public static String modelLink = "";	
	public static String modelName;
	public static long modelSize;

	public static void checkDeoldify() {
		
		PYTHON_DIR = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
			
		//Find correct python folder
		Path lib = Paths.get(PYTHON_DIR.replace("/bin", "") + "/lib");
		try (DirectoryStream<Path> dirs = Files.newDirectoryStream(lib, "python*"))
		{
            for (Path p : dirs)
            {
                Path target = p.resolve("site-packages/deoldify");
                if (Files.isDirectory(target))
                {
                	deoldify = target.toFile();
                	break;
                }
            }
        } catch (IOException e) {}
		
		if (deoldify.exists() == false)
		{		
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("additionalFiles") + System.lineSeparator() + Shutter.language.getProperty("wantToDownload"), Shutter.language.getProperty("functionColorize"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{	    									
				installDeoldify();    							 	
			}
			else
				comboFonctions.setSelectedItem("");
			
		}
	}
	
	public static void installDeoldify() {

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
			            	Console.consolePYTHON.append(line + System.lineSeparator());
			            }
			            process.waitFor();
						
						//Then install deoldify
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python.exe", "-m", "pip", "install" ,"deoldify", "matplotlib", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--target", PYTHON_DIR, "--no-warn-script-location");
					}
					else
					{
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", "-m", "pip", "install" ,"deoldify", "matplotlib", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--no-warn-script-location");
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
		            	Console.consolePYTHON.append(line + System.lineSeparator());
		            }
		            process.waitFor();
		            		            
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
				finally {
					
					if (deoldify.exists() == false || cancelled)
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
	
	public static void getDeoldifyModel() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{			
			deoldifyModel = new File(PYTHON_DIR).getParentFile() + "/models/" + modelName;
		}
		else
			deoldifyModel = new File(PYTHON_DIR).getParentFile().getParentFile() + "/models/" + modelName;
	}
	
    public static void downloadModel() {
		        
    	switch (comboFilter.getSelectedIndex())
		{	
			case 0: //Artistic
				
				modelLink = "https://huggingface.co/databuzzword/deoldify-artistic/resolve/aae6daa766bab0496224bf01a4b7959941703bce/ColorizeArtistic_gen.pth?download=true";
				modelName = "ColorizeArtistic_gen.pth";
				modelSize = 255144681L;	
				break;
			
			case 1: //Stable
				
				modelLink = "https://huggingface.co/spensercai/DeOldify/resolve/main/ColorizeStable_gen.pth?download=true";
				modelName = "ColorizeStable_gen.pth";
				modelSize = 874066230L;			
				break;
				
			case 2: //Video
				
				modelLink = "https://huggingface.co/spensercai/DeOldify/resolve/main/ColorizeVideo_gen.pth?download=true";
				modelName = "ColorizeVideo_gen.pth";
				modelSize = 874066230L;
				break;
			
		}      
    	
    	getDeoldifyModel();
				
		File model = new File(deoldifyModel);
		File modelPath = new File(model.getParent());
		
		if (modelPath.exists() == false)
		{
			modelPath.mkdir();
		}
		
		try {
			if (model.exists() && Files.size(model.toPath()) != modelSize)				
			{
				model.delete();
			}
		} catch (IOException e1) {}
		
		if (model.exists() == false)
		{
			new Update();
			
			if (Shutter.getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
			{
				Update.lblNewVersion.setText(Shutter.language.getProperty("downloadingAIModel"));
			}
			else
				Update.lblNewVersion.setText(Shutter.language.getProperty("downloadingAIModel") + "...");
			
			//Download
			Thread download = new Thread(new Runnable() {
				
				public void run() {		
			
					Utils.changeFrameVisibility(Shutter.frame, true);
					
					Update.HTTPDownload(modelLink, deoldifyModel);	

					Utils.changeFrameVisibility(Shutter.frame, false);
					Shutter.frame.toFront();
					
					Update.frame.dispose();
					
					if (model.exists() == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionColorize")))
					{
						Shutter.comboFonctions.setSelectedItem("");;
					}
				}
			});
			download.start();
		}
		
	}   
	
	public static void run(String file, String model, String output) {
		
		disableAll();
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {		
					
					String colorizePath = FFMPEG.PathToFFMPEG.replace("\\", "").replace("ffmpeg", "colorize.py");
										
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python.exe", colorizePath, file, "-m", model, "-o", output);
					}
					else
						processBuilder = new ProcessBuilder(PYTHON_DIR + "/python3", colorizePath, file, "-m", model, "-o", output);
					
					processBuilder.directory(new File("Library"));
					processBuilder.redirectErrorStream(true);
					 
					Console.consolePYTHON.append(language.getProperty("command") + " " + PYTHON_DIR + "/python3 " + colorizePath + " " + file + " -m " + model + " -o " + output);	
					
					isRunning = true;	
					process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            Console.consolePYTHON.append(System.lineSeparator());
			        
		            progressBar1.setValue(0);
					progressBar1.setMaximum(fileList.getModel().getSize());
							            		            
		            String line;
		            while ((line = reader.readLine()) != null)
		            {		            			            	
		            	if (line.contains("RuntimeError"))
		            		error = true;		
		            	
		            	if (line.contains("Done!"))
		            		progressBar1.setValue(progressBar1.getValue() + 1);
		            	
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