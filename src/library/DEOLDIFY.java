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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.Update;
import application.Utils;

public class DEOLDIFY extends Shutter {
	
	public static Thread runProcess;
	public static Process process;
	public static File deoldifyFolder = new File(documents.toString() + "/Library/deoldify");
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
					String[] cmd = { deoldifyFolder.toString() + "/python.exe", "-m", "pip", "install", "deoldify", "--only-binary=opencv-python", "matplotlib", "numpy==1.25.2", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--target", deoldifyFolder.toString(), "--no-warn-script-location" };
					PYTHON.installModule(deoldifyFolder, cmd, deoldify);						 	
				}
				else
				{
					String[] cmd = { deoldifyFolder.toString() + "/bin/python3", "-m", "pip", "install", "deoldify", "--only-binary=opencv-python", "matplotlib", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--no-warn-script-location" };
					
					if (System.getProperty("os.name").contains("Mac") && arch.equals("x86_64"))
					{
						cmd = new String[] { deoldifyFolder.toString() + "/bin/python3", "-m", "pip", "install", "deoldify", "--only-binary=opencv-python", "matplotlib", "numpy==1.25.2", "pandas", "scipy", "fastprogress", "torch==2.3.0", "torchvision", "torchaudio", "--no-warn-script-location" };
					}
					
					PYTHON.installModule(deoldifyFolder, cmd, deoldify);					
				}
			}
			else
				comboFonctions.setSelectedItem("");			
		}			
		
	}
	
	public static void patchDeoldify() {
		
		try {
			
			File visualize = new File(deoldify + "/visualize.py");
		
			if (visualize.exists())
			{
				//Edit the visualize.py file to get .png output
		        String content = Files.readString(visualize.toPath(), StandardCharsets.UTF_8);
	
		        content = content.replace("mjpeg", "png");
		        content = content.replace(".jpg", ".png");
	
		        Files.writeString(visualize.toPath(), content, StandardCharsets.UTF_8);
		        
		        //Remove the build_video function -> handled manually
		        List<String> lines = Files.readAllLines(visualize.toPath(), StandardCharsets.UTF_8);
		        for (int i = 0; i < lines.size(); i++) {
		            String line = lines.get(i).trim();

		            if (line.equals("return self._build_video(source_path)")) {
		                lines.set(i,"        return source_path");
		                break;
		            }
		        }

		        Files.write(visualize.toPath(), lines, StandardCharsets.UTF_8);
			}
			
		} catch (Exception e) {}
		
	}
	
    public static void downloadModel() {
		        
    	switch (comboFilter.getSelectedIndex())
		{	
			case 0: //Artistic
				
				modelLink = "https://huggingface.co/spensercai/DeOldify/resolve/main/ColorizeArtistic_gen.pth?download=true";
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
    	
    	deoldifyModel = deoldifyFolder.toString() + "/models/" + modelName;
    	
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
				Update.lblNewVersion.setText(Shutter.language.getProperty("downloadingAIModel") + " 1/2");
			}
			else
				Update.lblNewVersion.setText(Shutter.language.getProperty("downloadingAIModel") + " 1/2");
			
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
		
	}   
	
	public static void run(String file, String model) {
		
		disableAll();
		error = false;
		progressBar1.setValue(0);
		progressBar1.setMaximum(100);
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable() {	
			
			@Override
			public void run() {
					
				try {		
					
					//Copy colorize.py to allow writing into models folder
					File colorizeSource = new File(new File(FFMPEG.PathToFFMPEG.replace("\\ ", " ")).getParent() + "/colorize.py");
					File colorizePath = new File(deoldifyFolder.toString() + "/colorize.py");
					if (colorizePath.exists() == false)
					{
						Files.copy(colorizeSource.toPath(), colorizePath.toPath());
					}
					
					String quality = "35";
					if (comboFilter.getSelectedItem().equals("video"))
					{
						quality = "21";
					}
										
					ProcessBuilder processBuilder;
					if (System.getProperty("os.name").contains("Windows"))
					{
						processBuilder = new ProcessBuilder(deoldifyFolder.toString() + "/python.exe", colorizePath.toString(), file, "--model", model, "--render-factor", quality, "--no-watermark");
					}
					else
						processBuilder = new ProcessBuilder(deoldifyFolder.toString() + "/bin/python3", colorizePath.toString(), file, "--model", model, "--render-factor", quality, "--no-watermark");
					
					//Adding ffmpeg the the PATH environment						        			        
			        if (System.getProperty("os.name").contains("Mac"))
			        {
			        	Map<String, String> env = processBuilder.environment();			        	
			        	env.put("PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", "") + ":" + System.getenv("PATH"));
			        	env.put("DYLD_LIBRARY_PATH", new File(FFMPEG.PathToFFMPEG).getParent().replace("\\", ""));
			        }
					
					processBuilder.directory(deoldifyFolder);
					processBuilder.redirectErrorStream(true);
					 
					Console.consolePYTHON.append(language.getProperty("command") + " python3 " + colorizePath + " " + file + " --model " + model + " --render-factor " + quality + " --no-watermark");	
					
					isRunning = true;	
					process = processBuilder.start();
		            
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            
		            Console.consolePYTHON.append(System.lineSeparator());
							            		            
		            String line;
		            boolean downloadModel = false;
		            while ((line = reader.readLine()) != null)
		            {		            			            	
		            	if (line.contains("RuntimeError") || line.contains("cannot identify image file"))
		            		error = true;		
		            	
		            	if (line.contains("Downloading:"))
		            		downloadModel = true;		            	
		            	
		            	if (downloadModel)
		            	{
		            		if (progressBar1.getValue() == progressBar1.getMaximum())
		            		{
		            			downloadModel = false;
		            			progressBar1.setValue(0);
		            			lblCurrentEncoding.setText(new File(file).getName());
		            		}
		            		else
		            			lblCurrentEncoding.setText(language.getProperty("downloadingAIModel") + " 2/2");		
		            	}
		            	
		            	//Retrieve current progress output
		            	if ((downloadModel || comboFilter.getSelectedItem().equals("video")) && line.contains("%"))
		            	{
		            		String s[] = line.split("\\.");
		            		if (comboFilter.getSelectedItem().equals("video"))
		            		{
		            			String s2[] = s[0].split("\\|");
		            			progressBar1.setValue(Integer.valueOf(s2[2].replace(" ","")));
		            		}
		            		else
		            			progressBar1.setValue(Integer.valueOf(s[0].replace(" ","")));
		            	}		            	
		            	else if (line.contains("Done!"))
		            	{
		            		progressBar1.setValue(100);
		            	}
		            	
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