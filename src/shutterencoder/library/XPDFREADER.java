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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class XPDFREADER extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;
public static int pagesCount = 1;

	public static void run(final String cmd) {
					
		error = false;
		progressBar.setValue(0);	
		
		if (btnStart.getText().equals(language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{			
	        RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "pdftoppm" + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG), lblDestination1.getText()});
	        RenderQueue.caseRunParallel.setSelected(false);
	        RenderQueue.caseRunParallel.setEnabled(false);
	        RenderQueue.parallelValue.setEnabled(false);
	        
	        lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					
					try {
						
						String PathToXPDF;
						String PathToFFMPEG;
						ProcessBuilder processToXPDF;
						if (System.getProperty("os.name").contains("Windows"))
						{
							File workingDir = new File(Utils.getLibraryPath());
							
							PathToXPDF = "pdftoppm.exe";	
							PathToFFMPEG = "ffmpeg.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToXPDF + cmd.replace("PathToFFMPEG", PathToFFMPEG)}, null, workingDir);
						}
						else
						{
							PathToXPDF = Utils.getLibraryPath() + "/pdftoppm";							
							processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));	
							
							process = processToXPDF.start();
						}
						
						Console.consoleXPDFREADER.append(Shutter.language.getProperty("command") + " " + PathToXPDF + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG) + System.lineSeparator());
						
						isRunning = true;
						
						if (cmd.contains("-f rawvideo"))
						{
							InputStream is = process.getInputStream();				
							BufferedInputStream inputStream = new BufferedInputStream(is);
							
							VideoPlayerCore.readFrame(inputStream, VideoPlayerUI.player.getWidth(), VideoPlayerUI.player.getHeight(), true, false);
							VideoPlayerCore.preview = VideoPlayerCore.cloneBufferedImage(VideoPlayerCore.frameVideo);
							
							inputStream.close();

							if (VideoPlayerCore.frameVideo != null)
							{
								VideoPlayerUI.player.repaint();
							}
						}											
						process.waitFor();
										
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;	
						}						
				}				
			});		
			runProcess.start();
		}
	}
	
	public static void getPagesCount(String file) {
		
		error = false;
		progressBar.setValue(0);	
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						
						String PathToXPDF;
						ProcessBuilder processToXPDF;
						if (System.getProperty("os.name").contains("Windows"))
						{							
							File workingDir = new File(Utils.getLibraryPath());
							
							PathToXPDF = "pdfinfo.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToXPDF + " " + '"' + file + '"'}, null, workingDir);
						}
						else
						{							
							PathToXPDF = Utils.getLibraryPath() + "/pdfinfo";
							processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + " " + '"' + file + '"');
							
							process = processToXPDF.start();
						}
												
						isRunning = true;
						
						String line;
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);		
						
						OutputStream stdin = process.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));	
						
						while ((line = br.readLine()) != null)
						{							
						    if (line.contains("Pages"))
						    {
						    	String s[] = line.split(":");
						    	pagesCount = Integer.parseInt(s[1].replace(" ", ""));
						    }
						    						    
						    if (cancelled)
						    {
								writer.write('q');
								writer.flush();
								writer.close();
						    }
						    
						}												
						process.waitFor();
										
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;	
						}						
				}				
			});		
			runProcess.start();
	}

	public static void toFFPROBE(String file) {
		
		error = false;		
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					
					String PathToXPDF;
					ProcessBuilder processToXPDF;
					if (System.getProperty("os.name").contains("Windows"))
					{
						File workingDir = new File(Utils.getLibraryPath());
						
						PathToXPDF = "pdftoppm.exe";						
						process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -"}, null, workingDir);
					}
					else
					{						
						PathToXPDF = Utils.getLibraryPath() + "/pdftoppm";
						processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -");		
					
						process = processToXPDF.start();
					}					
									
					Console.consoleFFPROBE.append(Shutter.language.getProperty("command") + " " + PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -" +  System.lineSeparator());
						
					isRunning = true;
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
					
					while ((line = input.readLine()) != null)
					{						
						//Errors
						if (line.contains("Invalid data found when processing input") 
						|| line.contains("No such file or directory")
						|| line.contains("Invalid data found")
						|| line.contains("No space left")
						|| line.contains("does not contain any stream")
						|| line.contains("Invalid argument")
						|| line.contains("Error opening filters!")
						|| line.contains("matches no streams")
						|| line.contains("Error while opening encoder"))
						{
							error = true;
							break;
						} 
						
						if (line.contains("Video:"))
					    {	
							// Image resolution
			                Pattern resolutionPattern = Pattern.compile("(\\d{2,5})x(\\d{2,5})");
			                Matcher matcher = resolutionPattern.matcher(line);
			                
			                if (matcher.find())
			                {
			                	FFPROBE.imageWidth = Integer.parseInt(matcher.group(1));
			                	FFPROBE.imageHeight = Integer.parseInt(matcher.group(2));
			                	FFPROBE.imageResolution = FFPROBE.imageWidth + "x" + FFPROBE.imageHeight;
			                	FFPROBE.imageRatio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;    
			                }	                	

						 }
							 
						 if (line.contains("frame"))
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));						
																		
					}				
					process.waitFor();															
				   					     																		
					} catch (IOException | InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
					}
				
			}				
		});		
		runProcess.start();	
	}
}
