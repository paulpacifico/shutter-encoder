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
import java.util.Locale;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.Update;
import application.Utils;

public class DEOLDIFY extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	private static File deoldifyFolder = new File("Library/deoldify");
	private static File deoldify;
	public static boolean error = false;
	public static boolean isRunning = false;
	
	public static String deoldifyModel;
	public static String modelLink = "";	
	public static String modelName;
	public static long modelSize;

	public static void checkDeoldify() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			deoldify = new File(deoldifyFolder.toString() + "/deoldify");
		}
		else
			deoldify = new File(deoldifyFolder.toString() + "/lib/python3.10/site-packages/deoldify");	
			
		if (deoldify.exists() == false)
		{		
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("additionalFiles") + System.lineSeparator() + Shutter.language.getProperty("wantToDownload"), Shutter.language.getProperty("functionSeparation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{	    									
				if (System.getProperty("os.name").contains("Windows"))
				{
					String[] cmd = { deoldifyFolder.toString() + "/Scripts/python.exe", "-m", "pip", "install", "deoldify", "matplotlib", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--target", deoldifyFolder.toString(), "--no-warn-script-location" };
					PYTHON.installModule(deoldifyFolder, cmd, deoldify);						 	
				}
				else
				{
					String[] cmd = { deoldifyFolder.toString() + "/bin/python3", "-m", "pip", "install", "deoldify", "matplotlib", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--no-warn-script-location" };
					PYTHON.installModule(deoldifyFolder, cmd, deoldify);			
				}
			}
			else
				comboFonctions.setSelectedItem("");			
		}
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
    	
    	deoldifyModel = deoldifyFolder.getParent() + "/models/" + modelName;
				
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
						colorizePath = FFMPEG.PathToFFMPEG.replace("ffmpeg.exe", "colorize.py");
						processBuilder = new ProcessBuilder("deoldify/Scripts/python.exe", colorizePath, file, "-m", model, "-o", output);
					}
					else
						processBuilder = new ProcessBuilder("deoldify/bin/python3", colorizePath, file, "-m", model, "-o", output);
					
					processBuilder.directory(new File("Library"));
					processBuilder.redirectErrorStream(true);
					 
					Console.consolePYTHON.append(language.getProperty("command") + " python3 " + colorizePath + " " + file + " -m " + model + " -o " + output);	
					
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