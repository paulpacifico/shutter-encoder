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

package functions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import application.Ftp;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import application.Wetransfer;
import library.DCRAW;
import library.FFMPEG;
import library.FFPROBE;
import library.PDF;
import library.NCNN;
import settings.Colorimetry;
import settings.Corrections;
import settings.Filter;
import settings.FunctionUtils;
import settings.Image;
import settings.InputAndOutput;
import settings.Overlay;

public class Picture extends Shutter {
		
	public static void main(boolean encode, boolean videoPlayerCapture) {

		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{
					//Render queue only accept selected files
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						boolean isSelected = false;
						
						for (String input : Shutter.fileList.getSelectedValuesList())
						{
							if (liste.getElementAt(i).equals(input))
							{
								isSelected = true;
							}
						}	
						
						if (isSelected == false)
						{
							continue;
						}							
					}
					
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
									
					if (file == null)
						break;
		            
					if (videoPlayerCapture)
					{
						settings.FunctionUtils.yesToAll = false;
						screenshotIsRunning = true;
						file =  new File(VideoPlayer.videoPath);
					}
					
					try {
						
						String fileName = file.getName();
						lblCurrentEncoding.setText(fileName);					
						
						String extension = fileName.substring(fileName.lastIndexOf("."));	
							
						//FFPROBE Error with RAW files
						boolean isRaw = false;
						switch (extension.toLowerCase()) { 
						
							case ".3fr":
							case ".arw":
							case ".crw":
							case ".cr2":
							case ".cr3":
							case ".dng":
							case ".kdc":
							case ".mrw":
							case ".nef":
							case ".nrw":
							case ".orf":
							case ".ptx":
							case ".pef":
							case ".raf":
							case ".r3d":
							case ".rw2":
							case ".srw":
							case ".x3f":
								isRaw = true;
						}		
						
						//Data analyze
						if (FunctionUtils.analyze(file, isRaw) == false)
							continue;
				 
						//Date filter
						if (Filter.dateFilter(file) == false)
							continue;
												
				        //Framerate
						String frameRate = setFramerate();
						
			            //Deinterlace
						String filterComplex = setDeinterlace(extension, isRaw);

						//No GPU acceleration when using this function
						FFMPEG.isGPUCompatible = false;
						
						//Scaling									
			        	if (VideoEncoders.setScalingFirst() && comboResolution.getSelectedItem().toString().contains("AI") == false) //Set scaling before or after depending on using a pad or stretch mode			
			        	{
			        		filterComplex = Image.setScale(filterComplex, false);	
			        		filterComplex = Image.setPad(filterComplex, false);		
			        	}
						
						//LUTs
						filterComplex = Colorimetry.setLUT(filterComplex);
						
						//Levels
						filterComplex = Colorimetry.setLevels(filterComplex);
						
						//Colormatrix
						filterComplex = Colorimetry.setColormatrix(filterComplex);	
												
						//Rotate
						filterComplex = Image.setRotate(filterComplex);
						
						//Color
						filterComplex = Colorimetry.setColor(filterComplex);
						
						//Deflicker
						filterComplex = Corrections.setDeflicker(filterComplex);
						
						//Deband
						filterComplex = Corrections.setDeband(filterComplex);
							 
						//Details
		            	filterComplex = Corrections.setDetails(filterComplex);				
														            	
						//Denoise
			    		filterComplex = Corrections.setDenoiser(filterComplex);
			    		
			    		//Exposure
						filterComplex = Corrections.setSmoothExposure(filterComplex);	
						
						//Overlay
						filterComplex = Overlay.setOverlay(filterComplex, false);										
												
						//Logo
				        String logo = Overlay.setLogo();	            			            
										
				    	//Watermark
						filterComplex = Overlay.setWatermark(filterComplex);
						
		            	//Timecode
						filterComplex = Overlay.showTimecode(filterComplex, fileName.replace(extension, ""), videoPlayerCapture);			         
	
						//Crop
				        filterComplex = Image.setCrop(filterComplex);
				        
				        //Scaling
				        if (VideoEncoders.setScalingFirst() == false && comboResolution.getSelectedItem().toString().contains("AI") == false) //Set scaling before or after depending on using a pad or stretch mode		
			        	{
				        	filterComplex = Image.setScale(filterComplex, false);
				        	filterComplex = Image.setPad(filterComplex, false);		
			        	}
				        
				        //GIF Paletteuse
						if (comboFilter.getSelectedItem().toString().equals(".gif"))
						{
							if (filterComplex != "") filterComplex += ",";
							
							filterComplex += "split[a][b];[a]palettegen[p];[b][p]paletteuse,fps=" + comboImageOption.getSelectedItem().toString().replace(",", ".").replace(" " + Shutter.language.getProperty("fps"), "");
						}
						
						//filterComplex
						filterComplex = FunctionUtils.setFilterComplex(filterComplex, "");		
						
						//Hardware decoding
						String hardwareDecoding = " -hwaccel " + Shutter.comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none");
						
						//InOut		
						VideoPlayer.getFileList(file.toString());
						InputAndOutput.getInputAndOutput();
						
						if (videoPlayerCapture && VideoPlayer.waveformContainer.isVisible())
						{
							InputAndOutput.inPoint = " -ss " + (long) (VideoPlayer.playerCurrentFrame * VideoPlayer.inputFramerateMS) + "ms";
						}
						
						//Flags
			    		String flags = setFlags();
						
			    		//Colorspace
			            String colorspace = Colorimetry.setColorspace();
			            
						//EXR gamma
						String inputCodec = Colorimetry.setInputCodec(extension);
						
						//Compression
						String compression = setCompression();
						
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);				
						
						//Container
						String container = setExtension();
											
						//singleFrame
						String singleFrame = setFrame();	
						
						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + fileName.replace(extension, container); 
									
						//File output
						File fileOut = new File(fileOutputName);
						if(fileOut.exists())
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, fileName, extension, "_", container);
							if (fileOut == null)
								continue;	
						}
						
						//Command
						String cmd = filterComplex + singleFrame + colorspace + compression + flags + " -an -y ";
						
						if (extension.toLowerCase().equals(".pdf"))
						{
							for (int page = 0; page < PDF.pagesCount ; ++page)
							{ 	
								if (cancelled)
									break;
								
								disableAll();
								PDF.run(file, page);
								
								do {
									Thread.sleep(10);
								} while(PDF.isRunning);
								
								int n = 1;
								do {
									fileOut = new File(labelOutput + "/" + fileName.replace(extension, "_" + n + container));
									n++;
								} while (fileOut.exists());
								
								FFMPEG.run(" -i " + '"' + VideoPlayer.preview + '"' + logo + cmd + '"' + fileOut + '"');
								
								do {
									Thread.sleep(10);
								}
								while(FFMPEG.isRunning);
							}							
						}
						else if (isRaw)
						{
							btnStart.setEnabled(false);	
							disableAll();
							DCRAW.run(" -v -w -c -q 3 -o 1 -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + logo + cmd + '"' + fileOut + '"');
						}
						else if (inputDeviceIsRunning)
						{	
							String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTime());	
	
							if ((liste.getElementAt(0).equals("Capture.current.screen") || System.getProperty("os.name").contains("Mac")) && RecordInputDevice.audioDeviceIndex > 0)
								cmd = cmd.replace("1:v", "2:v").replace("-map v:0", "-map 1:v").replace("0:v", "1:v");							
							
							FFMPEG.run(" " + RecordInputDevice.setInputDevices() + logo + cmd + '"' + fileOut.toString().replace("Capture.current", timeStamp).replace("Capture.input", timeStamp) + '"');						
							
							fileOut = new File(fileOut.toString().replace("Capture.current", timeStamp).replace("Capture.input", timeStamp));
						}
						else if (comboResolution.getSelectedItem().toString().contains("AI"))
						{		
							File upscaleFolder = new File(lblDestination1.getText() + "/upscale");		
							upscaleFolder.mkdir();				
							
							String ext = fileOut.getName().substring(fileOut.getName().lastIndexOf("."));
							fileOut = new File(upscaleFolder + "/" + fileOut.getName().replace(ext, ".png"));								

							FFMPEG.run(hardwareDecoding + InputAndOutput.inPoint + frameRate + inputCodec + " -i " + '"' + file.toString() + '"' + logo + InputAndOutput.outPoint + filterComplex + singleFrame + colorspace + " -an -y " + '"' + fileOut + '"');
							
							do {
								Thread.sleep(10);
							} while(FFMPEG.runProcess.isAlive());
							
							upscale(fileOut, compression, flags);
						}
						else
						{
							FFMPEG.run(hardwareDecoding + InputAndOutput.inPoint + frameRate + inputCodec + " -i " + '"' + file.toString() + '"' + logo + InputAndOutput.outPoint + cmd + '"' + fileOut + '"');		
						}
	
						if (isRaw)
						{
							do {
								Thread.sleep(100);
							} while (DCRAW.runProcess.isAlive());
							
							btnStart.setEnabled(true);	
						}
						else
						{
							do {
								Thread.sleep(100);
							} while(FFMPEG.runProcess.isAlive());
						}
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{							
							if (lastActions(file, fileName, extension, fileOut, labelOutput) || videoPlayerCapture)
								break;
						}
						
					} catch (Exception e) {
						FFMPEG.error  = true;
					}
				}	
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && encode)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	private static String setFramerate() {
		
		if (caseCreateSequence.isSelected())	            		
	    	return " -r " + (float) FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));          

		return "";
	}
		
	private static String setDeinterlace(String extension, boolean isRaw) {	
	
		if (isRaw || extension.toLowerCase().equals(".pdf"))
		{
			return "";
		}
		else
		{
			if (FFPROBE.interlaced.equals("1"))
			{
				return "yadif=0:" + FFPROBE.fieldOrder + ":0";		
			}
			else
				return "";
		}
	}
			
	private static String setFlags() { 
		
		return " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
	}

	private static String setCompression() {
		
		if (comboFonctions.getSelectedItem().equals("JPEG"))
		{
			int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));
			return " -q:v " + q;
		}
		else if (comboFilter.getSelectedItem().toString().equals(".webp"))
		{
			return " -quality " + comboImageOption.getSelectedItem().toString().replace("%", "");
		}
		else if (comboFilter.getSelectedItem().toString().equals(".avif"))
		{
			return " -crf " +  Math.round((float) 63 - (float) ((float) ((float) Integer.valueOf(comboImageOption.getSelectedItem().toString().replace("%", "")) * 63) / 100));
		}
		else if (comboFilter.getSelectedItem().toString().equals(".tif"))
		{
			return " -compression_algo " + comboImageOption.getSelectedItem().toString();
		}
		else
		{
			return " -q:v 0";
		}
	}
	
	private static String setExtension() {			
	
		if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
		{
			if (caseCreateSequence.isSelected())
				return "_%06d" + comboFilter.getSelectedItem().toString();
			else
				return comboFilter.getSelectedItem().toString();
		}
		else if (comboFonctions.getSelectedItem().equals("JPEG") && caseCreateSequence.isSelected())
		{
			return "_%06d.jpg";
		}
		
		return ".jpg";		
	}

	private static String setFrame() {
	
		if (caseCreateSequence.isSelected())
		{
			return " -r 1";
		}
		else if (comboFilter.getSelectedItem().toString().equals(".gif"))
		{
			return "";
		}
		else if (comboFilter.getSelectedItem().toString().equals(".apng"))
		{
			return " -r " + comboImageOption.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".");
		}
		else
			return " -vframes 1";
	}
	
	private static void upscale(File fileOut, String compression, String flags) throws InterruptedException {
														
		int totalFiles = 0;		
		for (int i = 0 ; i < fileOut.getParentFile().listFiles().length ; i++)
		{
			totalFiles ++;
		}	
		
		progressBar1.setValue(0);
		progressBar1.setMaximum(totalFiles);
		
		String model = "realesr-general-wdn-x4v3";							
		if (Shutter.comboResolution.getSelectedItem().toString().contains("2D"))
		{
			model = "realesrgan-x4plus-anime";
		}	
		
		if (caseCreateSequence.isSelected())
		{			
			NCNN.run(" -v -i " + '"' + fileOut.getParentFile() + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut.getParentFile() + '"');
		}
		else		
			NCNN.run(" -v -i " + '"' + fileOut + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut + '"');
		
		do {
			Thread.sleep(100);
		} while (NCNN.isRunning && cancelled == false);	
		
		Shutter.screenshotIsRunning = true; //Workaround to avoid disableAll();
																										
		progressBar1.setValue(0);
		int progressValue = 0;
		
		File upscaleFolder = new File(lblDestination1.getText() + "/upscale");

		for (File file : upscaleFolder.listFiles()) 
		{			
			if (comboFonctions.getSelectedItem().equals("JPEG"))
			{
				fileOut = new File(lblDestination1.getText() + "/" + file.getName().replace(".png", ".jpg"));
			}
			else
				fileOut = new File(lblDestination1.getText() + "/" + file.getName().replace(".png", comboFilter.getSelectedItem().toString()));

			if (comboFilter.getSelectedItem().toString().equals(".png") == false)
			{									
				String scale = "";								
				if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
				{
					scale = " -vf " + '"' + "scale=iw*0.5:ih*0.5" + '"' + flags;
				}
				
				FFMPEG.run(" -i " + '"' + file + '"' + scale + compression + " -y " + '"' + fileOut + '"');
			
				do {
					Thread.sleep(10);
				} while(FFMPEG.runProcess.isAlive());
			}
			else if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
			{
				FFMPEG.run(" -i " + '"' + file + '"' + " -vf " + '"' + "scale=iw*0.5:ih*0.5" + '"' + flags + " " + '"' + fileOut + '"');
				
				do {
					Thread.sleep(10);
				} while(FFMPEG.runProcess.isAlive());				
			}
			else
			{
				File newName = new File(lblDestination1.getText() + "/" + file.getName());
				file.renameTo(newName);

				do {
					Thread.sleep(10);
				} while (newName.exists() == false && cancelled == false);
			}
			
			progressValue ++;
			progressBar1.setValue(progressValue);
			
			if (cancelled)
				break;
		}
		
		upscaleFolder.delete();		
	}
	
	private static boolean lastActions(File file, String fileName, String extension, File fileOut, String output) {		
		
		if (FunctionUtils.cleanFunction(fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(file);				
			Picture.main(true, false);
			return true;
		}
		return false;
	}

}
