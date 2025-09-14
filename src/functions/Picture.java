/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import application.Ftp;
import application.RecordInputDevice;
import application.RenderQueue;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.DCRAW;
import library.FFMPEG;
import library.FFPROBE;
import library.NCNN;
import library.PDF;
import settings.Colorimetry;
import settings.Corrections;
import settings.Filter;
import settings.FunctionUtils;
import settings.Image;
import settings.InputAndOutput;
import settings.Overlay;
import settings.Timecode;

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
																		
			            //Deinterlace
						String filterComplex = setDeinterlace(extension, isRaw);

						//No GPU acceleration when using this function
						FFMPEG.isGPUCompatible = false;
						
						//LUTs
						filterComplex = Colorimetry.setLUT(filterComplex);
						
						//Levels
						filterComplex = Colorimetry.setLevels(filterComplex);
						
						//Colormatrix
						filterComplex = Colorimetry.setColormatrix(filterComplex);	
												
						//Rotate
						filterComplex = Image.setRotate(filterComplex, false);
						
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
	
						 //Framerate
						filterComplex = setFramerate(filterComplex);
						
						//Crop
						filterComplex = Image.setCrop(filterComplex, file);
				        
				        //Zoom
						if (Shutter.sliderZoom.getValue() != 0)
						{		
							filterComplex = Colorimetry.setZoom(filterComplex, true);
						}
				        
				        //Scaling
				        if (comboResolution.getSelectedItem().toString().contains("AI") == false) //Set scaling before or after depending on using a pad or stretch mode		
			        	{
				        	filterComplex = Image.setScale(filterComplex, false);
				        	filterComplex = Image.setPad(filterComplex, false);		
			        	}
				        
				        //GIF Paletteuse
						if (comboFilter.getSelectedItem().toString().equals(".gif"))
						{
							if (filterComplex != "") filterComplex += ",";
							
							filterComplex += "split[a][b];[a]palettegen[p];[b][p]paletteuse";
						}
						
						//Color format
						filterComplex = setColorFormat(filterComplex);
						
						//filterComplex
						filterComplex = FunctionUtils.setFilterComplex(filterComplex, "", true);		

						//InOut	
						if (FFPROBE.totalLength > 40)
						{
							//Write the in and out values before getInputAndOutput()
							if (VideoPlayer.caseApplyCutToAll.isSelected())
							{							
								VideoPlayer.videoPath = file.toString();							
								VideoPlayer.updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
								VideoPlayer.updateGrpOut(Timecode.getNTSCtimecode(((double) FFPROBE.totalLength / 1000 * FFPROBE.accurateFPS) - InputAndOutput.savedOutPoint));							
								VideoPlayer.setFileList();	
							}
							
							//InOut	
							InputAndOutput.getInputAndOutput(VideoPlayer.getFileList(file.toString(), FFPROBE.totalLength));	
							
							if (videoPlayerCapture && VideoPlayer.waveformContainer.isVisible())
							{
								InputAndOutput.inPoint = " -ss " + (long) (VideoPlayer.playerCurrentFrame * VideoPlayer.inputFramerateMS) + "ms";
							}
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
						
						//File output name
						String prefix = "";	
						if (casePrefix.isSelected())
						{
							prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
						}
						
						String extensionName = "";	
						if (btnExtension.isSelected())
						{
							extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);
						}
						
						//Container
						String container = setExtension();
											
						//singleFrame
						String singleFrame = setFrame();	
						
						//Output name
						String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + container); 
									
						//File output
						File fileOut = new File(fileOutputName);						
						if(fileOut.exists())
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, "_", container);
							
							if (fileOut == null)
							{
								cancelled = true;
								break;
							}
							else if (fileOut.toString().equals("skip"))
							{
								continue;
							}
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
									fileOut = new File(labelOutput + "/" + prefix + fileName.replace(extension, "_" + n + container));
									n++;
								} while (fileOut.exists());

								Process process;
								if (System.getProperty("os.name").contains("Windows"))
								{							
									ProcessBuilder pbv = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + " -v quiet -hide_banner -i pipe:0" + logo + cmd + '"' + fileOut + '"');
									process = pbv.start();	
								}	
								else
								{
									ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + " -v quiet -hide_banner -i pipe:0" + logo + cmd + '"' + fileOut + '"');
									process = pbv.start();	
								}	
																			
								if (VideoPlayer.preview != null)
								{
							        OutputStream outputStream = process.getOutputStream();
							        
							        ImageIO.write(VideoPlayer.preview, "bmp", outputStream);		        
							        outputStream.close();
								}	
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
							
							if (upscaleFolder.exists())
							{
								for (File f : upscaleFolder.listFiles()) 
								{
									f.delete();
								}
							}
							else
								upscaleFolder.mkdir();				
							
							String ext = fileOut.getName().substring(fileOut.getName().lastIndexOf("."));
							fileOut = new File(upscaleFolder + "/" + fileOut.getName().replace(ext, ".png"));								

							FFMPEG.run(InputAndOutput.inPoint + inputCodec + " -i " + '"' + file.toString() + '"' + logo + InputAndOutput.outPoint + filterComplex + singleFrame + colorspace + " -an -y " + '"' + fileOut + '"');
							
							do {
								Thread.sleep(10);
							} while(FFMPEG.runProcess.isAlive());
							
							if (NCNN.isRunning && NCNN.process != null)
							{
								NCNN.process.destroy();
								
								do {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {}
								} while (NCNN.isRunning);
								
								lblCurrentEncoding.setText(fileName);
							}
							
							upscale(fileOut, compression, flags);
						}
						else
						{				
							FFMPEG.run(InputAndOutput.inPoint + inputCodec + " -i " + '"' + file.toString() + '"' + logo + InputAndOutput.outPoint + cmd + '"' + fileOut + '"');		
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
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && encode)
				{
					//Reset data for the current selected file
					VideoPlayer.videoPath = null;
					VideoPlayer.setMedia();
					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (VideoPlayer.loadMedia.isAlive());
					RenderQueue.frame.toFront();
				}
				else
				{
					enfOfFunction();					
				}
			}
			
		});
		thread.start();
		
    }

	private static String setFramerate(String filterComplex) {
		
		if (caseCreateSequence.isSelected())	            		
		{					
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "fps=" + Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));	
		}
		
		return filterComplex;
	}
	
	private static String setColorFormat(String filterComplex) {
				
		if (comboFilter.getSelectedItem().toString().equals(".tif"))           		
		{					
			if (filterComplex != "") filterComplex += ",";	
			
			if (FFPROBE.hasAlpha)
			{
				filterComplex += "format=rgba";
				
				if (FFPROBE.imageDepth > 8)
					filterComplex += "64le";
			}
			else
			{
				filterComplex += "format=rgb";
				
				if (FFPROBE.imageDepth > 8)
				{
					filterComplex += "48le";
				}
				else
					filterComplex += "24";
			}
		}
		
		return filterComplex;
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

		if (comboFonctions.getSelectedItem().toString().equals("JPEG"))
		{
			int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));			
			return " -q:v " + q;
		}
		else if (comboFonctions.getSelectedItem().toString().equals("JPEG XL"))
		{			
			return " -c:v libjxl -q:v " + Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", ""));
		}
		else if (comboFilter.getSelectedItem().toString().equals(".png"))
		{
			return " -compression_level " + comboImageOption.getSelectedItem().toString().replace("%", "");
		}
		else if (comboFilter.getSelectedItem().toString().equals(".webp"))
		{
			return " -quality " + comboImageOption.getSelectedItem().toString().replace("%", "");
		}
		else if (comboFilter.getSelectedItem().toString().equals(".avif"))
		{
			String encoder = " -c:v libsvtav1";
			
			if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
			{
				if (comboAccel.getSelectedItem().equals("Nvidia NVENC"))
				{
					encoder = " -c:v av1_nvenc";	
				}
				else if (comboAccel.getSelectedItem().equals("Intel Quick Sync"))
				{
					encoder = " -c:v av1_qsv";	
				}	
				else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
				{
					encoder = " -c:v av1_amf";
				}
				else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
				{
					encoder = " -c:v av1_videotoolbox";
				}
			}
			
			return encoder + " -crf " +  Math.round((float) 63 - (float) ((float) ((float) Integer.valueOf(comboImageOption.getSelectedItem().toString().replace("%", "")) * 63) / 100));
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
			{
				return "_%06d" + comboFilter.getSelectedItem().toString();
			}
			else
				return comboFilter.getSelectedItem().toString();
		}
		else if (comboFonctions.getSelectedItem().toString().equals("JPEG"))
		{
			if (caseCreateSequence.isSelected())
			{
				return "_%06d.jpg";
			}
			else
				return ".jpg";
		}
		else if (comboFonctions.getSelectedItem().toString().equals("JPEG XL"))
		{
			if (caseCreateSequence.isSelected())
			{
				return "_%06d.jxl";
			}
			else
				return ".jxl";
		}
		
		return ".jpg";		
	}

	private static String setFrame() {
	
		if (caseCreateSequence.isSelected())
		{			
			return "";
		}
		else if (comboFilter.getSelectedItem().toString().equals(".gif"))
		{
			return " -loop 0 -r " + comboImageOption.getSelectedItem().toString().replace(",", ".").replace(" " + Shutter.language.getProperty("fps"), "");
		}
		else if (comboFilter.getSelectedItem().toString().equals(".apng"))
		{
			return " -r " + comboImageOption.getSelectedItem().toString().replace(" " + Shutter.language.getProperty("fps"), "").replace(",", ".");
		}
		else if (comboFilter.getSelectedItem().toString().equals(".webp") && FFPROBE.totalLength > 40)
		{
			return " -loop 0";
		}
		else
		{
			return " -frames:v 1";
		}
	}
	
	private static void upscale(File fileOut, String compression, String flags) throws InterruptedException {
							
		progressBar1.setValue(0);
		progressBar1.setMaximum(fileOut.getParentFile().listFiles().length);
		
		String model = "realesr-general-wdn-x4v3";							
		if (Shutter.comboResolution.getSelectedItem().toString().contains("animation"))
		{
			model = "realesrgan-x4plus-anime";
		}	
		
		if (caseCreateSequence.isSelected())
		{			
			NCNN.run(" -v -i " + '"' + fileOut.getParentFile() + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut.getParentFile() + '"', false);
		}
		else		
			NCNN.run(" -v -i " + '"' + fileOut + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + fileOut + '"', false);
		
		do {
			Thread.sleep(100);
		} while (NCNN.isRunning && cancelled == false);	
																				
		progressBar1.setValue(0);
		int progressValue = 0;
		
		File upscaleFolder = new File(lblDestination1.getText() + "/upscale");

		for (File file : upscaleFolder.listFiles()) 
		{			
			if (comboFonctions.getSelectedItem().toString().contains("JPEG") || (comboFonctions.getSelectedItem().toString().contains("JPEG") == false && comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false)) //Video codec
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
				FFMPEG.run(" -i " + '"' + file + '"' + " -vf " + '"' + "scale=iw*0.5:ih*0.5" + '"' + flags + " -y " + '"' + fileOut + '"');
				
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
		
		for (File f : upscaleFolder.listFiles()) 
		{
			f.delete();
		}
		
		upscaleFolder.delete();		
	}
	
	private static boolean lastActions(File file, String fileName, String extension, File fileOut, String output) {		
		
		if (FunctionUtils.cleanFunction(file, fileName, fileOut, output))
			return true;
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
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
