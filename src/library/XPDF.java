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

package library;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import application.Console;
import application.RenderQueue;
import application.Shutter;

public class XPDF extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;
public static int pagesCount = 1;

	public static void run(final String cmd) {
					
		error = false;
		progressBar1.setValue(0);	
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
			String PathToXPDF;
			String PathToFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{
				PathToXPDF = "Library\\pdftoppm.exe";
				PathToFFMPEG = "Library\\ffmpeg.exe";
			}
			else
			{
				PathToXPDF = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToXPDF = PathToXPDF.substring(0,PathToXPDF.length()-1);
				PathToXPDF = PathToXPDF.substring(0,(int) (PathToXPDF.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/pdftoppm";
				PathToFFMPEG = PathToXPDF.replace("pdftoppm", "ffmpeg");
			}
			
	        RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "pdftoppm" + cmd.replace("PathToFFMPEG", PathToFFMPEG), lblDestination1.getText()});
	        lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{
			disableAll();
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToXPDF;
						String PathToFFMPEG;
						ProcessBuilder processToXPDF;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToXPDF = "Library\\pdftoppm.exe";
							PathToFFMPEG = "Library\\ffmpeg.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToXPDF + cmd.replace("PathToFFMPEG", PathToFFMPEG)});
						}
						else
						{
							PathToXPDF = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToXPDF = PathToXPDF.substring(0,PathToXPDF.length()-1);
							PathToXPDF = PathToXPDF.substring(0,(int) (PathToXPDF.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/pdftoppm";
							PathToFFMPEG = PathToXPDF.replace("pdftoppm", "ffmpeg");
							
							processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + cmd.replace("PathToFFMPEG", PathToFFMPEG));								
							process = processToXPDF.start();
						}
						
						Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToXPDF + cmd.replace("PathToFFMPEG", PathToFFMPEG) + System.lineSeparator() + System.lineSeparator());
						
						isRunning = true;
						
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
						
						//Permet d'écrire dans le flux
						OutputStream stdin = process.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));	
						
						while ((line = input.readLine()) != null) {							
						    Console.consoleFFMPEG.append(line + System.lineSeparator());		
						    
						    if (cancelled)
						    {
								writer.write('q');
								writer.flush();
								writer.close();
						    }
						    
						}//While														
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
	
	public static void info(String file) {
		
		error = false;
		progressBar1.setValue(0);	
			
			runProcess = new Thread(new Runnable()  {
				
				@Override
				public void run() {
					
					try {
						String PathToXPDF;
						ProcessBuilder processToXPDF;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToXPDF = "Library\\pdfinfo.exe";							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToXPDF + " " + '"' + file + '"'});
						}
						else
						{
							PathToXPDF = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToXPDF = PathToXPDF.substring(0,PathToXPDF.length()-1);
							PathToXPDF = PathToXPDF.substring(0,(int) (PathToXPDF.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/pdfinfo";
							processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + " " + '"' + file + '"');								
							process = processToXPDF.start();
						}
						
						Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToXPDF + " " + '"' + file + '"' + System.lineSeparator() + System.lineSeparator());
						
						isRunning = true;
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						String line;
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);		
						
						//Permet d'écrire dans le flux
						OutputStream stdin = process.getOutputStream();
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));	
						
						while ((line = br.readLine()) != null) {							
						    Console.consoleFFMPEG.append(line + System.lineSeparator());		
						    
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
						    
						}//While														
						process.waitFor();
										
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;	
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}						
				}				
			});		
			runProcess.start();
	}
	
	public static void toFFPROBE(String file) {
		
		error = false;		
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {
					
					String PathToXPDF;
					ProcessBuilder processToXPDF;
				
					if (System.getProperty("os.name").contains("Windows"))
					{						
						PathToXPDF = "Library\\pdftoppm.exe";						
						process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -"});
					}
					else
					{
						PathToXPDF = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToXPDF = PathToXPDF.substring(0,PathToXPDF.length()-1);
						PathToXPDF = PathToXPDF.substring(0,(int) (PathToXPDF.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/pdftoppm";
						processToXPDF = new ProcessBuilder("/bin/bash", "-c" , PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -");	
						process = processToXPDF.start();
					}
									
					Console.consoleFFPROBE.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToXPDF + " -r 300 -f 1 -l 1 " + '"' + file + '"' + " - | " + PathToXPDF.replace("pdftoppm", "ffprobe") + " -i -"
					+  System.lineSeparator() + System.lineSeparator());
						
					isRunning = true;
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
					
					while ((line = input.readLine()) != null) {
						
						Console.consoleFFPROBE.append(line + System.lineSeparator() );		
					
						//Erreurs
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
							String split[]= line.split(",");
			                int i = 0;
			                do
			                {
			                    i ++;
			                } while (split[i].contains("x") == false || split[i].contains("xyz"));
			                			                
			              	// Crop Image
			                String height = split[i];
			                String splitx[]= height.split("x");
			                String getHeight[] =  splitx[1].split(" ");
			                
			                int imageWidth = Integer.parseInt(splitx[0].replace(" ", ""));
			                FFPROBE.imageResolution = imageWidth + "x" + getHeight[0];
		                	FFPROBE.imageWidth = imageWidth;
		                	FFPROBE.imageHeight = Integer.parseInt(getHeight[0]);
		                	FFPROBE.imageRatio = (float) imageWidth / Integer.parseInt(getHeight[0]);

						 }
							 
						 if (line.contains("frame"))
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
						
																		
					}//While					
					process.waitFor();															
				   					     																		
					} catch (IOException | InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				
			}				
		});		
		runProcess.start();	
	}
}