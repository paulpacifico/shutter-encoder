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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;

public class YOUTUBEDL extends Shutter {
	
public static boolean error = false;
static int dureeTotale = 0; 
public static boolean isRunning = false;
public static Thread runProcess;
public static	Process process;
public static File outputFile;
public static StringBuilder formatsOutput;
public static String format = "";

	public static void run(final String cmd, final String options) {
		
        lblCurrentEncoding.setText(language.getProperty("getVideoName"));
		error = false;
		Shutter.cancelled  = false;
		
        outputFile = new File(lblDestination1.getText() + "/%(title)s.%(ext)s");
        
	    Console.consoleYOUTUBEDL.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + format + " " + cmd + options + " --no-continue -o " + '"' + outputFile + '"' + System.lineSeparator());

		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {					
					String PathToYOUTUBEDL;
					ProcessBuilder processYOUTUBEDL;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(1,PathToYOUTUBEDL.length()-1);
						PathToYOUTUBEDL = '"' + PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", " ")  + "/Library/yt-dlp.exe" + '"';
						if (options != "")
						{
							String opts[] = options.split(" ");
							if (opts.length == 3)
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, opts[1], opts[2], "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
							else if (opts.length == 5)
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, opts[1], opts[2], opts[3], opts[4], "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
							else
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, opts[1], opts[2], opts[3], opts[4], opts[5], opts[6], "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
						}
						else
							processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
					}
					else
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,PathToYOUTUBEDL.length()-1);
						
						String youtubedl = "yt-dlp_linux";
						if (System.getProperty("os.name").contains("Mac"))
						{
							String macVersion = System.getProperty("os.version").replace(".", "");
							if (macVersion.length() < 4)
							{
								macVersion = macVersion + "0";
							}
							else if (macVersion.length() > 4)
							{
								macVersion = macVersion.substring(0,4);
							}
							
							youtubedl = "yt-dlp_macos";
							if (arch.equals("x86_64") && Integer.parseInt(macVersion) < 1015)
							{
								youtubedl = "yt-dlp_macos_x86";
							}
						}

						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/" + youtubedl;
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c" , PathToYOUTUBEDL + " " + format + " "+ cmd + options + " --no-continue --ffmpeg-location " + PathToYOUTUBEDL.replace(youtubedl, "ffmpeg") + " --no-part -o " + '"' + outputFile + '"');	
						
					}
									
					isRunning = true;	
					process = processYOUTUBEDL.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String lineOutput;
			        					
					do {
												
						lineOutput = br.readLine();
																		
					    Console.consoleYOUTUBEDL.append(lineOutput + System.lineSeparator());
		                
					    if (lineOutput != null)
					    {
					    	if (lineOutput.contains("Destination"))
					    	{
					    		 lblCurrentEncoding.setText(lineOutput.substring(24).replace(lblDestination1.getText(), "").substring(1));
					    		 outputFile = new File(lblDestination1.getText() + "/" + lblCurrentEncoding.getText());
					    	}
					    		
						    if (lineOutput.contains("ETA") && lineOutput.contains("Unknown ETA") == false && lineOutput.contains("ETA Unknown") == false)
						    {
		                        String[] splitPercent= lineOutput.split("%");
		                        String progress = splitPercent[0].replace("[download]", "");
		                        progressBar1.setValue((int) Math.floor(Double.valueOf(progress)));
		                        
		                        String[] splitETA = lineOutput.split("ETA");
		                        String[] time = splitETA[1].split(":");
		                        tempsRestant.setVisible(true);
		                        String min;
		                        if (time[0].contains("00"))
		                            min = "";
		                        else
		                            min = time[0].replace("TA", "") + "min ";
		                        
		                        tempsRestant.setText(Shutter.language.getProperty("tempsRestant") + " " + min + time[1].replace(" ","") + "s");
		                        tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);
		                        
		                        if (tempsRestant.getX() + tempsRestant.getSize().width > lblArrows.getX())
			       				{
			       					lblArrows.setVisible(false);
			       				}
			       				else
			       					lblArrows.setVisible(true);
						    }
					    }
					    						          						        		
					} while(lineOutput != null && Shutter.cancelled == false);	
					
					process.waitFor();
								     	
                    if (Shutter.cancelled)
                    	outputFile.delete();					
														
					} catch (IOException | InterruptedException e) {
						
	                    JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("downloadError"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	                    error = true;
	                    Shutter.cancelled = true;
	                    
					} finally {
						isRunning = false;
					}
						
			}				
		});		
		runProcess.start();
	}

	public static void update() {
		error = false;
		Shutter.cancelled  = false;
		
	    Console.consoleYOUTUBEDL.append(System.lineSeparator() + Shutter.language.getProperty("command") + " -U " + System.lineSeparator());

		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {					
					String PathToYOUTUBEDL;
					ProcessBuilder processYOUTUBEDL;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(1,PathToYOUTUBEDL.length()-1);
						PathToYOUTUBEDL = '"' + PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", " ")  + "/Library/yt-dlp.exe" + '"';
						processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-U");
					}
					else
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,PathToYOUTUBEDL.length()-1);
						
						String youtubedl = "yt-dlp_linux";
						if (System.getProperty("os.name").contains("Mac"))
						{
							String macVersion = System.getProperty("os.version").replace(".", "");
							if (macVersion.length() < 4)
							{
								macVersion = macVersion + "0";
							}
							else if (macVersion.length() > 4)
							{
								macVersion = macVersion.substring(0,4);
							}
							
							youtubedl = "yt-dlp_macos";
							if (arch.equals("x86_64") && Integer.parseInt(macVersion) < 1015)
							{
								youtubedl = "yt-dlp_macos_x86";
							}
						}

						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/" + youtubedl;
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c", PathToYOUTUBEDL + " -U");
					}
									
					isRunning = true;	
					process = processYOUTUBEDL.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String lineOutput;
			        					
					do {
						lineOutput = br.readLine();		
												
					    Console.consoleYOUTUBEDL.append(lineOutput + System.lineSeparator());		                
						          						        		
					} while(lineOutput != null && Shutter.cancelled == false);	
					process.waitFor();					
														
					} catch (IOException | InterruptedException e) {
	                    JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("downloadError"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	                    error = true;
	                    Shutter.cancelled = true;
					} finally {
						isRunning = false;
					}
						
			}				
		});		
		runProcess.start();
	}

	public static void getAvailableFormats(final String cmd, final String options) {
		
		lblCurrentEncoding.setText(Shutter.language.getProperty("getAvailableFormats"));
		error = false;
		Shutter.cancelled  = false;
		
		Console.consoleYOUTUBEDL.append(System.lineSeparator() + Shutter.language.getProperty("command") + " -F " + cmd + options + System.lineSeparator());
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					String PathToYOUTUBEDL;
					ProcessBuilder processYOUTUBEDL;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(1,PathToYOUTUBEDL.length()-1);
						PathToYOUTUBEDL = '"' + PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", " ")  + "/Library/yt-dlp.exe" + '"';	
						if (options != "")
						{
							String opts[] = options.split(" ");
							if (opts.length == 3)
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-F", cmd, opts[1], opts[2]);
							else if (opts.length == 5)
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-F", cmd, opts[1], opts[2], opts[3], opts[4]);
							else
								processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-F", cmd, opts[1], opts[2], opts[3], opts[4], opts[5], opts[6]);
						}
						else
							processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-F", cmd);
					}
					else
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,PathToYOUTUBEDL.length()-1);
						
						String youtubedl = "yt-dlp_linux";
						if (System.getProperty("os.name").contains("Mac"))
						{
							String macVersion = System.getProperty("os.version").replace(".", "");
							if (macVersion.length() < 4)
							{
								macVersion = macVersion + "0";
							}
							else if (macVersion.length() > 4)
							{
								macVersion = macVersion.substring(0,4);
							}
							
							youtubedl = "yt-dlp_macos";
							if (arch.equals("x86_64") && Integer.parseInt(macVersion) < 1015)
							{
								youtubedl = "yt-dlp_macos_x86";
							}
						}

						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/" + youtubedl;
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c" , PathToYOUTUBEDL + options + " -F " + cmd);
					}					
					
					isRunning = true;	
					process = processYOUTUBEDL.start();
					
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));		

					OutputStream out = process.getOutputStream();
			        out.write("os get /value".getBytes());
			        out.flush();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        formatsOutput = new StringBuilder();		
			        										
					//Erreurs
					while ((line = input.readLine()) != null && Shutter.cancelled == false) {		
					    Console.consoleYOUTUBEDL.append(line  + System.lineSeparator());
					    
				        if (line.contains("Not Found") || line.contains("Invalid URL") || line.contains("ERROR")) {
				             error = true;
				             Shutter.cancelled = true;
				        }
					}	
							
					String lineOutput;
					//Récupération de l'extension du fichier
					do {
						lineOutput = br.readLine();
						formatsOutput.append(lineOutput + System.lineSeparator());															          						        		
					} while(lineOutput != null && Shutter.cancelled == false);	

					process.waitFor();
					
				    Console.consoleYOUTUBEDL.append(formatsOutput + System.lineSeparator() + System.lineSeparator());	
										
				} catch (InterruptedException | IOException e) {
					error = true;
					Shutter.cancelled = true;
				} finally {
					isRunning = false;
				}
			}
		});		
		runProcess.start();
	}	
}