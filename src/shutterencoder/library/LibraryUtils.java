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

package shutterencoder.library;

import java.awt.Cursor;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.utils.Libplacebo;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.Functions;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class LibraryUtils extends Shutter {

	public static boolean showInputDeviceFrame = false;
	public static StringBuilder videoDevices;
	public static StringBuilder audioDevices;
	public static StringBuilder hwaccels = new StringBuilder();
	public static boolean isGPUCompatible = false;
	public static int GPUCount = 0;
	public static int multiGPU = 0;
	public static String cpuName;
	public static boolean hasNvidiaGPU = false;
	public static boolean hasAMDGPU = false;
	public static boolean hasIntelGPU = false;
	public static boolean isIntelArc = false;
	public static boolean cudaAvailable = false;
	public static boolean amfAvailable = false;
	public static boolean qsvAvailable = false;
	public static boolean videotoolboxAvailable = false;
	public static boolean libplaceboAvailable = false;
	public static boolean vulkanAvailable = false;
	public static boolean autoQSV = false;
	public static boolean autoCUDA = false;
	public static boolean autoAMF = false;
	public static boolean autoVIDEOTOOLBOX = false;
	public static boolean autoVULKAN = false;
	public static Process waveformProcess;
	public static BufferedWriter waveformWriter;
	
	public static void detectHardwareAcceleration(final String function) {
	    
		Thread hwaccel = new Thread(new Runnable() {
	        @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
	        public void run() {
	            comboAccel.setEnabled(false);
	            comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	
	            try {
	                List<String> graphicsAccel = new ArrayList<>();
	                graphicsAccel.add(language.getProperty("aucune").toLowerCase());
	
	                String os = System.getProperty("os.name");
	                boolean isWindows = os.contains("Windows");
	                boolean isMac     = os.contains("Mac");
	                boolean isLinux   = os.contains("Linux");
	
	                switch (function) {
	
	                    case "H.264":
	                    case "H.265":
	                    case "H.266": {
	                        String codec = "H.264".equals(function) ? "h264"
	                                     : "H.265".equals(function) ? "hevc"
	                                     : "vvc";
	
	                        if (isWindows) {
	                            if (arch.equals("arm64")) {
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                        + "_mf -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Media Foundation");
	                            } else {
	                                if (LibraryUtils.hasNvidiaGPU) {
	                                    checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                            + "_nvenc -b:v 5000k -b_ref_mode 0 -s 640x360 -f null -\"");
	                                    if (!FFMPEG.error) graphicsAccel.add("Nvidia NVENC");
	                                }
	                                if (LibraryUtils.hasIntelGPU) {
	                                    checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                            + "_qsv -b:v 5000k -s 640x360 -f null -\"");
	                                    if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	                                }
	                                if (LibraryUtils.hasAMDGPU) {
	                                    checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                            + "_amf -b:v 5000k -s 640x360 -f null -\"");
	                                    if (!FFMPEG.error) graphicsAccel.add("AMD AMF Encoder");
	                                }
	                                String gpuIndex = LibraryUtils.GPUCount > 1 ? "1" : "0";
	                                checkHWaccel("-init_hw_device vulkan=gpu:" + gpuIndex
	                                        + " -f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                        + "_vulkan -b:v 5000k -vf format=nv12,hwupload -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Vulkan Video");
	                            }
	                        } else if (isLinux) {
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                    + "_nvenc -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("Nvidia NVENC");
	                            
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                    + "_qsv -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                    + "_vaapi -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("VAAPI");
	
	                            if ("H.264".equals(function)) {
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                        + "_v4l2m2m -b:v 5000k -s 640x360 -f null -");
	                                if (!FFMPEG.error) graphicsAccel.add("V4L2 M2M");
	
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                        + "_omx -b:v 5000k -s 640x360 -f null -");
	                                if (!FFMPEG.error) graphicsAccel.add("OpenMAX");
	                            }
	                        } else { // Mac
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v " + codec
	                                    + "_videotoolbox -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("OSX VideoToolbox");  
	                        }
	                        break;
	                    }
	
	                    case "Apple ProRes": {
	                    	
	                    	if (isWindows)
	                    	{
	                    		String gpuIndex = LibraryUtils.GPUCount > 1 ? "1" : "0";
	                    		checkHWaccel("-init_hw_device vulkan=gpu:" + gpuIndex
	                    				+ " -f lavfi -i nullsrc -frames:v 1 -c:v prores_ks_vulkan -s 640x360"
	                    				+ " -vf format=nv12,hwupload -pix_fmt yuv422p10 -f null -\"");
                                if (!FFMPEG.error) graphicsAccel.add("Vulkan Video");
	                    	}
	                    	else if (isMac && arch.equals("arm64"))
	                        {
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v prores_videotoolbox -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("OSX VideoToolbox");
	                        }
	                        break;
	                    }
	
	                    case "FFV1": {
	                        if (isWindows) {
	                            String gpuIndex = LibraryUtils.GPUCount > 1 ? "1" : "0";
	                            checkHWaccel("-init_hw_device vulkan=gpu:" + gpuIndex
	                                    + " -f lavfi -i nullsrc -frames:v 1 -c:v ffv1_vulkan -level 3"
	                                    + " -vf format=nv12,hwupload -f null -\"");
	                            if (!FFMPEG.error) graphicsAccel.add("Vulkan Video");
	                        }
	                        break;
	                    }
	
	                    case "VP9": {
	                        if (isWindows) {
	                            if (LibraryUtils.hasIntelGPU) {
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v vp9_qsv"
	                                        + " -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	                            }
	                        } else if (isLinux) {
	                        	checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v vp9_qsv"
	                        			+ " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	                            
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v vp9_vaapi"
	                                    + " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("VAAPI");
	                        }
	                        break;
	                    }
	
	                    case "AV1": {
	                        if (isWindows) {
	                            if (arch.equals("arm64")) {
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_mf"
	                                        + " -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Media Foundation");
	                            } else {
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_nvenc"
	                                        + " -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Nvidia NVENC");
	
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_qsv"
	                                        + " -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	
	                                checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_amf"
	                                        + " -b:v 5000k -s 640x360 -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("AMD AMF Encoder");
	
	                                String gpuIndex = LibraryUtils.GPUCount > 1 ? "1" : "0";
	                                checkHWaccel("-init_hw_device vulkan=gpu:" + gpuIndex
	                                        + " -f lavfi -i nullsrc -frames:v 1 -c:v av1_vulkan"
	                                        + " -b:v 5000k -vf format=nv12,hwupload -f null -\"");
	                                if (!FFMPEG.error) graphicsAccel.add("Vulkan Video");
	                            }
	                        } else if (isLinux) {
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_nvenc"
	                            			+ " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("Nvidia NVENC");
	                            
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_qsv"
	                            		+ " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("Intel Quick Sync");
	
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_vaapi"
	                            			+ " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("VAAPI");                        
	                        } else if (isMac) {
	                            checkHWaccel("-f lavfi -i nullsrc -frames:v 1 -c:v av1_videotoolbox"
	                                    	+ " -b:v 5000k -s 640x360 -f null -");
	                            if (!FFMPEG.error) graphicsAccel.add("OSX VideoToolbox");
	                        }
	                        break;
	                    }
	                }
	
	                int previousIndex = comboAccel.getSelectedIndex();
	                comboAccel.setModel(new DefaultComboBoxModel(graphicsAccel.toArray()));
	                
	                if (previousIndex <= comboAccel.getModel().getSize())
	                    comboAccel.setSelectedIndex(previousIndex);
	
	                if (Utils.loadEncFile != null && !Utils.hwaccel.isEmpty()) {
	                    comboAccel.setSelectedItem(Utils.hwaccel);
	                    Utils.hwaccel = "";
	                }
	
	            } catch (Exception e) {
	            } finally {
	                if (comboAccel.getItemCount() > 1)
	                    comboAccel.setEnabled(true);
	                comboAccel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	            }
	        }
	    });
	    hwaccel.start();
	}

	public static void checkHWaccel(final String cmd) {
		
		FFMPEG.error = false;	
		FFMPEG.isRunning = true;
	
		try {
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));
				FFMPEG.process = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));									
				FFMPEG.process = processFFMPEG.start();
			}		
			
			String line;
	
			if (cmd.contains("-hwaccels"))
			{
				try (InputStreamReader isr = new InputStreamReader(FFMPEG.process.getInputStream()))
				{
			        BufferedReader br = new BufferedReader(isr);
			        
			        hwaccels.append("auto" + System.lineSeparator());
			        
			        while ((line = br.readLine()) != null) 
			        {	
			        	if (line.contains("Hardware acceleration methods") == false && line.equals("") == false && line != null)
			        	{
			        		hwaccels.append(line + System.lineSeparator());
			        	}
			        }
			        
			        hwaccels.append(language.getProperty("aucun"));		
				}
		    }
			else
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(FFMPEG.process.getErrorStream()));		
				
				while ((line = input.readLine()) != null)
				{						
					//Console.consoleFFMPEG.append(line + System.lineSeparator() );		
															
					//Errors
					FFMPEG.checkForErrors(line);																										
				}	
				
				//Console.consoleFFMPEG.append(System.lineSeparator());
			}					
			FFMPEG.process.waitFor();	
							     																		
		} catch (IOException io) {//Bug Linux							
		} catch (InterruptedException e) {
			FFMPEG.error = true;
		}
		finally {
			FFMPEG.isRunning = false;
		}
	}

	public static void checkCPUInfo() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			try {
				Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "Name"});
		        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		        String line;
		        while ((line = reader.readLine()) != null)
		        {
		            if (line.contains("Intel"))
		            {
		            	Console.consoleFFMPEG.append(line + System.lineSeparator());
		            	cpuName = line;
		            }
		        }
			} catch (Exception e) {}
		}
	}

	public static int detectIntelGen() {
		
		if (cpuName != null)
		{
		    int dash = cpuName.indexOf('-');
		    if (dash == -1) return -1;
	
		    String model = cpuName.substring(dash + 1).trim();
		    Pattern MODEL_PATTERN = Pattern.compile("\\b(\\d{3,5})([A-Za-z0-9]{0,3})\\b");
		    Matcher version = MODEL_PATTERN.matcher(model);
	
		    version.find();
	        String digits = version.group(1);
	        int gen = Character.getNumericValue(digits.charAt(0));
		    
		    if (digits.length() == 4 && gen <= 9)
	        {
	            gen = Character.getNumericValue(digits.charAt(0)); // 1st–9th gen
	        }
	        else if (model.length() >= 4)
	        {
	            gen = Integer.parseInt(digits.substring(0, 2));   // 10th gen and later           
	        }
		    else
		    	return -1;
	
		    return gen;
		}
		
		return -1;
	}

	@SuppressWarnings("deprecation")
	public static void checkGPUAvailable()
	{
		if (System.getProperty("os.name").contains("Windows"))
		{
			try {
				
				Process process;								
				double version = Double.parseDouble(System.getProperty("os.version"));
				if (version >= 10.0)
				{
					process = Runtime.getRuntime().exec("powershell -Command \"Get-CimInstance Win32_VideoController | Select-Object -ExpandProperty Name\"");
				}
				else
					process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
				
		        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		        String line;
		        while ((line = reader.readLine()) != null)
		        {
		            line = line.trim();
		            if (!line.isEmpty() && !line.toLowerCase().contains("name"))
		            {
		            	Console.consoleFFMPEG.append(line + System.lineSeparator());
		            	
		                if (line.contains("NVIDIA") || line.contains("GeForce"))
		                {
		                	if (LibraryUtils.hasNvidiaGPU) //If it's already true there is more than 1 Nvidia GPU
		                		LibraryUtils.multiGPU ++;
		                	
		                	LibraryUtils.hasNvidiaGPU = true;	
		                	LibraryUtils.GPUCount ++;
		                }
		                else if (line.contains("AMD") || line.contains("Radeon"))
		                {		                	
		                	LibraryUtils.hasAMDGPU = true;	
		                	LibraryUtils.GPUCount ++;
		                }
		                else if (line.contains("Intel"))
		                {
		                	LibraryUtils.hasIntelGPU = true;
		                	LibraryUtils.GPUCount ++;
		                	
		                	if (line.contains("Arc"))
		                		LibraryUtils.isIntelArc = true;
		                }
		            }
		        }
		        
		        Console.consoleFFMPEG.append(System.lineSeparator());
			}
			catch (IOException e) //If the Windows command crashes, set all values to true, then check all GPUs using FFmpeg
			{
				LibraryUtils.hasNvidiaGPU = true;
				LibraryUtils.hasAMDGPU = true;
				LibraryUtils.hasIntelGPU = true;
			}
		}
	}

	public static void checkGPUCapabilities(String file) {
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		LibraryUtils.isGPUCompatible = false;
		LibraryUtils.cudaAvailable = false;
		LibraryUtils.amfAvailable = false;
		LibraryUtils.qsvAvailable = false;
		LibraryUtils.videotoolboxAvailable = false;
		LibraryUtils.libplaceboAvailable = false;
		LibraryUtils.vulkanAvailable = false;
				
		//Check is GPU can decode				
		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
		&& comboGPUDecoding.getSelectedItem().toString().equals(language.getProperty("aucun")) == false
		&& comboGPUFilter.getSelectedItem().toString().equals(language.getProperty("aucun")) == false)
		{
			String vcodec = "";
			if (FFPROBE.videoCodec != null && FFPROBE.totalLength > 40)
			{
				vcodec = FFPROBE.videoCodec.replace("video", "");
				for (String s : functionsList)
				{
					if (vcodec.toLowerCase().equals(s.replace(".", "").replace("-", "").toLowerCase())
					|| s.toLowerCase().contains(vcodec.toLowerCase()))
					{
						vcodec = s;
						break;
					}
					else
						vcodec = vcodec.toUpperCase();
				}
			}
			
			if (vcodec.equals("H.264") || vcodec.equals("HEVC") || (vcodec.equals("VP9") && FFPROBE.hasAlpha == false) || vcodec.equals("AV1") || vcodec.equals("MPEG-1") || vcodec.equals("MPEG-2") || vcodec.equals("MPEG-4") || (vcodec.equals("PRORES_RAW") && System.getProperty("os.name").contains("Mac")))
			{
				LibraryUtils.isGPUCompatible = true;
			}
				
			String selectedGPU = "";
			if (LibraryUtils.multiGPU > 0)
				selectedGPU = " -hwaccel_device " + comboSelectedGPU.getSelectedIndex();
					
			//Scaling
			String bitDepth = FFPROBE.imageDepth == 10 ? "p010" : "nv12";
			
			if (LibraryUtils.isGPUCompatible)
			{				
				//Check for Nvidia/AMD or Intel GPU
				if (comboGPUDecoding.getSelectedItem().toString().equals("auto"))
				{
					if (System.getProperty("os.name").contains("Windows"))
					{			
						if (LibraryUtils.hasNvidiaGPU)
						{
							//Cuda
							gpuFilter(" -hwaccel cuda -hwaccel_output_format cuda" + selectedGPU + " -i " + '"' + file + '"' + " -vf scale_cuda=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
															
							if (FFMPEG.error == false)
								LibraryUtils.cudaAvailable = true;
						}
						else if (LibraryUtils.hasAMDGPU)
						{
							//AMF
							gpuFilter(" -hwaccel d3d11va -hwaccel_output_format d3d11 -i " + '"' + file + '"' + " -vf vpp_amf=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
															
							if (FFMPEG.error == false)
								LibraryUtils.amfAvailable = true;
						}
						
						if (LibraryUtils.hasIntelGPU)
						{
							String child = "dxva2";
							if (detectIntelGen() >= 9 || LibraryUtils.isIntelArc)
							{
								child = "d3d11va";
							}					
														
							//QSV
							gpuFilter(" -hwaccel qsv -hwaccel_output_format qsv -init_hw_device qsv:hw,child_device_type=" + child + " -i " + '"' + file + '"' + " -vf scale_qsv=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
							
							if (FFMPEG.error == false)
								LibraryUtils.qsvAvailable = true;
						}
						
						//Vulkan
						if (LibraryUtils.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
						{
							gpuFilter(" -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan=gpu:1  -i " + '"' + file + '"' + " -vf scale_vulkan=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
						}
						else
							gpuFilter(" -hwaccel vulkan -hwaccel_output_format vulkan -init_hw_device vulkan=gpu:0  -i " + '"' + file + '"' + " -vf scale_vulkan=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
							
						if (FFMPEG.error == false)
							LibraryUtils.vulkanAvailable = true;
						
						if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
						{								
							if (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use CUDA decoding with AMF or QSV encoding
							{
								LibraryUtils.cudaAvailable = false;
							}
							else if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use AMF decoding with NVENC or QSV encoding
							{
								LibraryUtils.amfAvailable = false;
							}
							else if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals("Vulkan Video")) //Cannot use QSV decoding with NVENC or AMF encoding
							{
								LibraryUtils.qsvAvailable = false;
							}
							else if (comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("AMD AMF Encoder")) //Cannot use VULKAN decoding with QSV encoding
							{
								LibraryUtils.vulkanAvailable = false;
							}
						}
					}
					else //Mac
					{
						//videotoolbox
						gpuFilter(" -hwaccel videotoolbox -hwaccel_output_format videotoolbox_vld -i " + '"' + file + '"' + " -vf scale_vt=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -");
	
						if (FFMPEG.error == false)
							LibraryUtils.videotoolboxAvailable = true;
					}
					
					//Disable GPU if not available
					if (LibraryUtils.cudaAvailable == false && LibraryUtils.amfAvailable == false && LibraryUtils.qsvAvailable == false && LibraryUtils.videotoolboxAvailable == false && LibraryUtils.vulkanAvailable == false)
						LibraryUtils.isGPUCompatible = false;
				}
				else //Check the current selection
				{		
					String device = "";
					if (comboGPUDecoding.getSelectedItem().toString().equals("vulkan")
					|| comboGPUFilter.getSelectedItem().toString().equals("vulkan")) //Always need to choose the GPU
					{
						if (LibraryUtils.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
						{
							device = " -init_hw_device vulkan=gpu:1";
						}
						else
							device = " -init_hw_device vulkan=gpu:0";
						
					}
					else if (comboGPUDecoding.getSelectedItem().toString().equals("qsv"))
					{
						String child = "dxva2";
						if (detectIntelGen() >= 9 || LibraryUtils.isIntelArc)
						{
							child = "d3d11va";
						}					
						
						device = " -init_hw_device qsv:hw,child_device_type=" + child;
					}
					else if (comboAccel.getSelectedItem().equals("Nvidia NVENC"))
					{
						device = " -init_hw_device cuda";
					}
					else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						device = " -init_hw_device videotoolbox";
					}
					
					String scaleFilter = "scale_";
					if (comboGPUDecoding.getSelectedItem().toString().equals("amf"))
					{
						scaleFilter = "vpp_" ;
					}
					
					if (System.getProperty("os.name").contains("Windows"))
					{
						gpuFilter(" -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " -hwaccel_output_format " + comboGPUFilter.getSelectedItem().toString() + device + selectedGPU + " -i " + '"' + file + '"' +  " -vf " + scaleFilter + comboGPUFilter.getSelectedItem().toString() + "=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -" + '"');
					}
					else
						gpuFilter(" -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " -hwaccel_output_format " + comboGPUFilter.getSelectedItem().toString().replace("videotoolbox", "videotoolbox_vld") + device + " -i " + '"' + file + '"' +  " -vf " + scaleFilter + comboGPUFilter.getSelectedItem().toString().replace("videotoolbox", "vt") + "=640:360,hwdownload,format=" + bitDepth + " -an -frames:v 1 -f null -");
	
					if (FFMPEG.error)
					{								
						LibraryUtils.isGPUCompatible = false;
						
						if (comboGPUDecoding.getSelectedItem().equals("cuda"))
						{
							LibraryUtils.cudaAvailable = false;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("amf"))
						{
							LibraryUtils.amfAvailable = false;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("qsv"))
						{
							LibraryUtils.qsvAvailable = false;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("videotoolbox"))
						{
							LibraryUtils.videotoolboxAvailable = false;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("vulkan"))
						{
							LibraryUtils.vulkanAvailable = false;
						}
					}
					else
					{
						if (comboGPUDecoding.getSelectedItem().equals("cuda"))
						{
							LibraryUtils.cudaAvailable = true;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("amf"))
						{
							LibraryUtils.amfAvailable = true;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("qsv"))
						{
							LibraryUtils.qsvAvailable = true;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("videotoolbox"))
						{
							LibraryUtils.videotoolboxAvailable = true;
						}
						else if (comboGPUDecoding.getSelectedItem().equals("vulkan"))
						{
							LibraryUtils.vulkanAvailable = true;
						}
					}
				}									
	
				FFMPEG.error = false;
				FFMPEG.errorLog.setLength(0);
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
			}
			
			//libplacebo do not need to be true for isGPUcompatible
			if (System.getProperty("os.name").contains("Windows"))
			{
				if (LibraryUtils.hasNvidiaGPU || LibraryUtils.hasAMDGPU || LibraryUtils.hasIntelGPU && LibraryUtils.GPUCount > 1) //Make sure integrated GPU is not used because it's slower
				{
					String device = "";
					if (LibraryUtils.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
					{
						device = " -init_hw_device vulkan=gpu:1";
					}
					else
						device = " -init_hw_device vulkan=gpu:0";
					
					gpuFilter(device + selectedGPU + " -i " + '"' + file + '"' + " -vf libplacebo=w=640:h=360 -an -frames:v 1 -f null -" + '"');
	
					if (FFMPEG.error == false)
						LibraryUtils.libplaceboAvailable = true;
				}
			}
			else if (System.getProperty("os.name").contains("Mac") && arch.equals("arm64"))
			{
				gpuFilter(" -i " + '"' + file + '"' + " -vf libplacebo=w=640:h=360 -an -frames:v 1 -f null -");
	
				if (FFMPEG.error == false)
					LibraryUtils.libplaceboAvailable = true;
			}
			
			LibraryUtils.checkGPUFiltering();
			LibraryUtils.checkGPUDeinterlacing();			
		}
		
	}

	public static void checkGPUFiltering() {
		
		//Auto GPU selection
		LibraryUtils.autoQSV = false;
		LibraryUtils.autoCUDA = false;
		LibraryUtils.autoAMF = false;
		LibraryUtils.autoVIDEOTOOLBOX = false;
		LibraryUtils.autoVULKAN = false;
			
		if (comboGPUDecoding.getSelectedItem().toString().equals("auto") && comboGPUFilter.getSelectedItem().toString().equals("auto"))
		{
			if (LibraryUtils.cudaAvailable && (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
			{
				LibraryUtils.autoCUDA = true;
			}
			else if (LibraryUtils.amfAvailable && (comboAccel.getSelectedItem().equals("AMD AMF Encoder") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
			{
				LibraryUtils.autoAMF = true;
			}
			else if (LibraryUtils.qsvAvailable && (comboAccel.getSelectedItem().equals("Intel Quick Sync") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
			{
				LibraryUtils.autoQSV = true;
			}
			else if (LibraryUtils.videotoolboxAvailable && (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
			{
				LibraryUtils.autoVIDEOTOOLBOX = true;
			}
			else if (LibraryUtils.vulkanAvailable && (comboAccel.getSelectedItem().equals("Vulkan Video") || comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())))
			{
				LibraryUtils.autoVULKAN = true;
			}
		}
	}

	public static void checkGPUDeinterlacing() {
	
		boolean limitToFHD = false;
		switch (comboFonctions.getSelectedItem().toString())
		{
			//Limit to Full HD
			case "AVC-Intra 100":
			case "DNxHD":
			case "XDCAM HD422":
			case "XDCAM HD 35":
			case "DVD" : //Needed 16:9 aspect ratio
				
				if (FFPROBE.imageResolution != null && FFPROBE.imageResolution.equals("1440x1080") == false)
				{
					limitToFHD = true;	
				}
				
				break;
		}	
		
		if (caseEnableCrop.isSelected() == false
		&& comboResolution.getSelectedItem().toString().contains("AI") == false
		&& caseStabilisation.isSelected() == false)
		{
			//setScale gives already all the correct settings
			if (shutterencoder.functions.settings.Image.setScale("", limitToFHD, false).contains("cuda"))
			{
				if (comboForcerDesentrelacement.getModel().getSize() != 2 || comboForcerDesentrelacement.getModel().getElementAt(1).equals("yadif") == false)
				{
					comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "yadif" }));
					comboForcerDesentrelacement.setSelectedIndex(0);
				}
			}
			else if (shutterencoder.functions.settings.Image.setScale("", limitToFHD, false).contains("qsv"))
			{
				if (comboForcerDesentrelacement.getModel().getSize() != 2 || comboForcerDesentrelacement.getModel().getElementAt(1).equals("advanced") == false)
				{
					comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "advanced" }));
					comboForcerDesentrelacement.setSelectedIndex(0);
				}
			}
			else if (shutterencoder.functions.settings.Image.setScale("", limitToFHD, false).contains("vulkan"))
			{
				if (comboForcerDesentrelacement.getModel().getSize() != 1 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("bwdif") == false)
				{
					comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif" }));
					comboForcerDesentrelacement.setSelectedIndex(0);
				}
			}
			else if (shutterencoder.functions.settings.Image.setScale("", limitToFHD, false).contains("libplacebo"))
			{
				if (comboForcerDesentrelacement.getModel().getSize() != 2 || comboForcerDesentrelacement.getModel().getElementAt(1).equals("yadif") == false)
				{
					comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "yadif" }));
					comboForcerDesentrelacement.setSelectedIndex(0);
				}
			}
			else
			{
				if (comboForcerDesentrelacement.getModel().getSize() != 5 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("bwdif") == false)
				{
					comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "yadif", "estdif", "w3fdif", "detelecine" }));
					comboForcerDesentrelacement.setSelectedIndex(0);	
				}
			}
		}
		else
		{
			if (comboForcerDesentrelacement.getModel().getSize() != 5 || comboForcerDesentrelacement.getModel().getElementAt(0).equals("bwdif") == false)
			{
				comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "yadif", "estdif", "w3fdif", "detelecine" }));
				comboForcerDesentrelacement.setSelectedIndex(0);	
			}
		}	
	}

	public static String setGPUDevice(String filterComplex) {
				
		String selectedGPU = "";
		if (LibraryUtils.multiGPU > 0)
			selectedGPU = " -hwaccel_device " + comboSelectedGPU.getSelectedIndex();
				
		//GPU decoding
		String gpuDecoding = "";						
		if (LibraryUtils.isGPUCompatible && (filterComplex.contains("_cuda") || filterComplex.contains("_amf") || filterComplex.contains("_qsv") || filterComplex.contains("_vt") || filterComplex.contains("_vulkan")))
		{
			if (LibraryUtils.autoCUDA || (LibraryUtils.cudaAvailable && comboGPUFilter.getSelectedItem().toString().equals("cuda")))
			{			
				gpuDecoding = " -hwaccel cuda -hwaccel_output_format cuda -init_hw_device cuda" + selectedGPU;
			}
			else if (LibraryUtils.autoAMF || (LibraryUtils.amfAvailable && comboGPUFilter.getSelectedItem().toString().equals("amf")))
			{
				gpuDecoding = " -hwaccel d3d11va -hwaccel_output_format d3d11"; //Works differently
			}
			else if (LibraryUtils.autoQSV || (LibraryUtils.qsvAvailable && comboGPUFilter.getSelectedItem().toString().equals("qsv")))
			{
				String child = "dxva2";
				if (detectIntelGen() >= 9 || LibraryUtils.isIntelArc)
				{
					child = "d3d11va";
				}					
	
				gpuDecoding = " -hwaccel qsv -hwaccel_output_format qsv -init_hw_device qsv:hw,child_device_type=" + child;
			}
			else if (LibraryUtils.autoVIDEOTOOLBOX || (LibraryUtils.videotoolboxAvailable && comboGPUFilter.getSelectedItem().toString().equals("videotoolbox")))
			{
				gpuDecoding = " -hwaccel videotoolbox -hwaccel_output_format videotoolbox_vld -init_hw_device videotoolbox";
				
			}
			else if (LibraryUtils.autoVULKAN || (LibraryUtils.vulkanAvailable && comboGPUFilter.getSelectedItem().toString().equals("vulkan")))
			{
				gpuDecoding = " -hwaccel vulkan -hwaccel_output_format vulkan";
			}
			else
				gpuDecoding = " -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + " -hwaccel_output_format " + comboGPUFilter.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + selectedGPU;
		}
		else
		{
			gpuDecoding = " -hwaccel " + comboGPUDecoding.getSelectedItem().toString().replace(language.getProperty("aucun"), "none") + selectedGPU;
		}	
	
		if (comboAccel.getSelectedItem().equals("Vulkan Video")
		|| comboGPUDecoding.getSelectedItem().toString().equals("vulkan")
		|| comboGPUFilter.getSelectedItem().toString().equals("vulkan")
		|| Libplacebo.useLibplaceboFilters && filterComplex.contains("libplacebo")) //Always need to choose the GPU
		{
			if (LibraryUtils.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
			{
				gpuDecoding += " -init_hw_device vulkan=gpu:1";
			}
			else
				gpuDecoding += " -init_hw_device vulkan=gpu:0";
		}	
		else if (comboAccel.getSelectedItem().equals("VAAPI"))			
		{
			gpuDecoding += " -vaapi_device /dev/dri/renderD128";
		}
		
		return gpuDecoding;
	}

	public static void setCropDetect(File file) {
	
		FFMPEG.cropdetect = "";
		
		String cmd =  " -an -frames:v 5 -vf cropdetect -f null -" + '"';
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
		{
			cmd =  " -an -frames:v 5 -vf cropdetect -f null -";						
		}
				
		//Input point
		String inputPoint = " -ss " + (float) (VideoPlayerCore.playerCurrentFrame) * VideoPlayerUI.inputFramerateMS + "ms";
		if (FFPROBE.totalLength <= 40 || caseEnableSequence.isSelected()) //Image
			inputPoint = " -loop 1";
		
		screenshotIsRunning = true; //Workaround to not change the frame size
		
		FFMPEG.run(inputPoint + " -i " + '"' + file + '"' + cmd);	
		
		try {
			do {
				Thread.sleep(100);
			} while(FFMPEG.isRunning);
		} catch (Exception er) {}	
		
		screenshotIsRunning = false;
		
		if (FFMPEG.cropdetect != "")
		{
			String c[] = FFMPEG.cropdetect.split(":");
			
			textCropPosX.setText(c[2]);						
			textCropWidth.setText(c[0]);
			textCropHeight.setText(c[1]);
			textCropPosY.setText(c[3]);
			
			int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayerUI.player.getHeight()) / FFPROBE.imageHeight);	
			int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayerUI.player.getWidth()) / FFPROBE.imageWidth);
			int width = (int) Math.ceil((float)  (Integer.valueOf(textCropWidth.getText()) * VideoPlayerUI.player.getHeight()) / FFPROBE.imageHeight);
			int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayerUI.player.getWidth()) / FFPROBE.imageWidth);
			
			if (width > VideoPlayerUI.player.getWidth())
				width = VideoPlayerUI.player.getWidth();
			
			if (height > VideoPlayerUI.player.getHeight())
				height = VideoPlayerUI.player.getHeight();
			
			selection.setBounds(x, y, width, height);
		}	
		
	}

	public static void gpuFilter(final String cmd) {
		
		FFMPEG.error = false;	
		
	    //Console.consoleFFMPEG.append(language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + cmd);
	    
		try {
			
			ProcessBuilder processFFMPEG;
	
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + " " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + " " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));	
				
				FFMPEG.setEnvironment(processFFMPEG);
			}
			
			processFFMPEG.redirectErrorStream(true);
			
			FFMPEG.process = processFFMPEG.start();
				
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(FFMPEG.process.getInputStream()));		
			
			//Console.consoleFFMPEG.append(System.lineSeparator());
	
			while ((line = input.readLine()) != null)
			{				
				//Console.consoleFFMPEG.append(line + System.lineSeparator());		
				
				//Errors
				FFMPEG.checkForErrors(line);	
				
				if (FFMPEG.error)
				{
					FFMPEG.process.destroy();
					break;
				}
			}					
			int exitCode = FFMPEG.process.waitFor();
			
			if (exitCode != 0)
				FFMPEG.error = true;
			
			//Console.consoleFFMPEG.append(System.lineSeparator());
				
		} catch (IOException io) {//Bug Linux							
		} catch (InterruptedException e) {
			FFMPEG.error = true;
			e.printStackTrace();
		}
	}

	public static void devices(final String cmd) {
		
		FFMPEG.error = false;		
		FFMPEG.isRunning = true;
		
	    Console.consoleFFMPEG.append(language.getProperty("command") + cmd);
			
		FFMPEG.runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				
				try {
	
					ProcessBuilder processFFMPEG;
					if (System.getProperty("os.name").contains("Windows"))
					{													
						processFFMPEG = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + " " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));
						FFMPEG.process = processFFMPEG.start();
					}
					else
					{
						processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + " " + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));									
						FFMPEG.process = processFFMPEG.start();
					}		
					
					String line;
					
					BufferedReader input = new BufferedReader(new InputStreamReader(FFMPEG.process.getErrorStream()));		
	
					boolean isVideoDevices = false;
					boolean isAudioDevices = false;
					if (cmd.contains("openal") == false) //IMPORTANT
					{
						videoDevices = new StringBuilder();
						videoDevices.append(language.getProperty("noVideo"));
					}
					
					audioDevices = new StringBuilder();
					audioDevices.append(language.getProperty("noAudio"));
					
					Console.consoleFFMPEG.append(System.lineSeparator());
					
					Pattern devicePattern = Pattern.compile("\\[\\d+\\]\\s*(.*?)(?=\\s*\\[uid:|$)", Pattern.CASE_INSENSITIVE);
					
					while ((line = input.readLine()) != null)
					{					
						Console.consoleFFMPEG.append(line + System.lineSeparator());		
											
						//Get devices Mac
						if (cmd.contains("avfoundation") && line.contains("]")) 
						{	
						    if (line.contains("AVFoundation audio devices"))
						    {
						        isAudioDevices = true;
						        isVideoDevices = false;
						    }
						    else if (line.contains("AVFoundation video devices"))
						    {
						        isVideoDevices = true;
						        isAudioDevices = false;
						    }

						    Matcher matcher = devicePattern.matcher(line);
						    if (matcher.find()) 
						    {
						        String rawName = matcher.group(1).trim();
						        
						        byte[] bytes = rawName.getBytes(StandardCharsets.ISO_8859_1);
						        String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);

						        if (isAudioDevices && !line.contains("Error"))
						        {								
						            audioDevices.append(":" + utf8EncodedString);
						        }
						        else if (isVideoDevices && !line.contains("Capture screen"))
						        {						
						            videoDevices.append(":" + utf8EncodedString);
						        }
						    }
						}
						
						//Get current screen index
						if (cmd.contains("avfoundation") && line.contains("Capture screen") && FFMPEG.firstScreenIndex == -1) 
						{
							String s[] = line.split("\\[");
							String s2[] = s[2].split("\\]");
							FFMPEG.firstScreenIndex = Integer.parseInt(s2[0]);
						}
						
						//Get devices Windows
						if (cmd.contains("dshow"))
						{							
							if (line.contains("audio") && line.contains("\"") && line.contains("Alternative name") == false)
							{
								String s[] = line.split("\"");								
								audioDevices.append(":" + s[1]);
							}
							
							if (line.contains("video") && line.contains("\"") && line.contains("Alternative name") == false && isAudioDevices == false)
							{
								String s[] = line.split("\"");
								videoDevices.append(":" + s[1]);
							}
						}
	
						//Errors
						FFMPEG.checkForErrors(line);																		
					}			
					
					FFMPEG.process.waitFor();		
					
					Console.consoleFFMPEG.append(System.lineSeparator());
				   					     																		
					} catch (IOException io) {//Bug Linux							
					} catch (InterruptedException e) {
						FFMPEG.error = true;
					} finally {
						FFMPEG.isRunning = false;
					}
				
			}				
		});		
		FFMPEG.runProcess.start();
	}

	public static void playerWaveform(final String cmd) {
						
		try {
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + cmd + '"');
				waveformProcess = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + cmd);									
				waveformProcess = processFFMPEG.start();
			}	
		
			//Allows to write into the stream
			OutputStream stdin = waveformProcess.getOutputStream();
			waveformWriter = new BufferedWriter(new OutputStreamWriter(stdin));
			
			InputStream is = waveformProcess.getInputStream();				
			BufferedInputStream inputStream = new BufferedInputStream(is);
	
			VideoPlayerCore.waveform = ImageIO.read(inputStream);
			
			inputStream.close();
			
			waveformProcess.waitFor();
		   					     																		
		} catch (IOException io) {//Bug Linux							
		} catch (Exception e) {}
	}

	public static void saveToXML(String cmd) {	  
		
		File savedFilePath = Utils.saveDialog(Functions.functionsFolder);
								
		if (savedFilePath != null)
		{
				try {
					DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
					Document document = documentBuilder.newDocument();
					
					Element root = document.createElement("Shutter");
					document.appendChild(root);
	
					Element settings = document.createElement("settings");
					root.appendChild(settings);
	
					Attr attr = document.createAttribute("id");
					attr.setValue("10");
					settings.setAttributeNode(attr);
	
					String split[] = cmd.split("\"");
					String entree = split[1];	
					int i = 0;
					do
					{
						i ++;	
					} while (i < split.length);
					String sortie = split[i - 1];	
	
					Element firstName = document.createElement("command");
					firstName.appendChild(document.createTextNode("ffmpeg" + cmd.replace(InputAndOutput.inPoint, "").replace(" -i ", "").replace('"' + entree + '"', "").replace('"' + sortie + '"', "").replace(" -y ","").replace(" -n ", "")));
					settings.appendChild(firstName);
	
					// point d'entrée
					Element lastname = document.createElement("pointIn");
					lastname.appendChild(document.createTextNode(InputAndOutput.inPoint));
					settings.appendChild(lastname);
	
					// extension
					String ext = cmd.substring(cmd.lastIndexOf("."));
					Element email = document.createElement("extension");
					email.appendChild(document.createTextNode(ext.replace("\"", "")));
					settings.appendChild(email);
					
					// creation du fichier XML
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource domSource = new DOMSource(document);
					StreamResult streamResult = new StreamResult(new File(savedFilePath.toString().replace(".enc", "")) + ".enc");
	
					transformer.transform(domSource, streamResult);
				} catch (ParserConfigurationException | TransformerException e) {}
		 }				
	}

	public static boolean isReadable(File file) {
		
		try {	
			
			ProcessBuilder processFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{							
				processFFMPEG = new ProcessBuilder('"' + FFMPEG.PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -" + '"');
				FFMPEG.process = processFFMPEG.start();
			}
			else
			{
				processFFMPEG = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -");							
				FFMPEG.process = processFFMPEG.start();
			}		
						
			Console.consoleFFMPEG.append(language.getProperty("command") + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -i " + '"' + file + '"' + " -t 5 -f null -");
			
			String line;
	
			BufferedReader input = new BufferedReader(new InputStreamReader(FFMPEG.process.getErrorStream()));		
								
			Console.consoleFFMPEG.append(System.lineSeparator());
			
			while ((line = input.readLine()) != null)
			{			
				Console.consoleFFMPEG.append(line + System.lineSeparator() );		
				
				//Erreurs
				if (line.contains("No such file or directory")
					|| line.contains("Invalid data found")
					|| line.contains("moov atom not found")
					|| line.contains("Operation not permitted")
					|| line.contains("File ended prematurely")
					|| line.contains("Warning MVs not available")
					|| line.contains("broken or empty index")
					|| line.contains("corrupt decoded frame")
					|| line.contains("invalid new backstep")
					|| line.contains("Packet corrupt")
					|| line.contains("ac-tex damaged")
					|| line.contains("Error"))
				{
					return false;
				} 																		
			}			
	   				
			Console.consoleFFMPEG.append(System.lineSeparator());
			
		} catch (IOException io) {//Bug Linux							
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
}
