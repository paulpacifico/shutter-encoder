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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.VideoWeb;

public class YOUTUBEDL extends Shutter {
	
public static File ytdlp;
private static String PathToYOUTUBEDL;
public static boolean error = false;
static int dureeTotale = 0; 
public static boolean isRunning = false;
public static Thread runProcess;
public static	Process process;
public static File outputFile;
public static StringBuilder formatsOutput;
public static String format = "";

	public static boolean checkYOUTUBEDL() {

		if (System.getProperty("os.name").contains("Windows"))
		{
			ytdlp = new File(Shutter.documents + "/Library/yt-dlp.exe");
		}
		else
		{
			String youtubedl;						
			if (System.getProperty("os.name").contains("Mac"))
			{							
				youtubedl = "yt-dlp_macos";	
			}
			else
				youtubedl = "yt-dlp_linux";
			
			ytdlp = new File(Shutter.documents + "/Library/" + youtubedl);
		}
		
		PathToYOUTUBEDL = '"' + ytdlp.toString() + '"';
		
		if (ytdlp.exists() == false)
		{		
			installYOUTUBEDL();			
			return false;
		}
		
		return true;
	}
	
	private static void installYOUTUBEDL() {
		
		Thread download = new Thread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				try {
					
					isRunning = true;					
										
					String base = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/";
			        String url, dest;
			        
			        if (System.getProperty("os.name").contains("Windows"))
			        {
			            url = base + "yt-dlp.exe";
			        }
			        else if (System.getProperty("os.name").contains("Mac"))
			        {
			            url = base + "yt-dlp_macos";
			        }         
			        else //Linux
			        {
			            url = base + "yt-dlp_linux";
			        }
			        
			        dest = ytdlp.toString();
			        
			        Path p = Paths.get(dest);
			        Path temp = Paths.get(dest + ".tmp");
			        Files.createDirectories(p.getParent());
			        
			        try (FileOutputStream fos = new FileOutputStream(temp.toFile()))
			        {
			            fos.getChannel().transferFrom(Channels.newChannel(new URL(url).openStream()), 0, Long.MAX_VALUE);
			        }
			        
			        Files.move(temp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			        if (System.getProperty("os.name").contains("Windows") == false)
			            Files.setPosixFilePermissions(p, PosixFilePermissions.fromString("rwxr-xr-x"));
					
				} catch (Exception e) {}
				finally {
					isRunning = false;					
				}
			}
			
		});
		download.start();
    }

	public static void run(final String cmd, final String options) {
		
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
        lblCurrentEncoding.setText(language.getProperty("getVideoName"));
		error = false;
		cancelled  = false;
		
        outputFile = new File(lblDestination1.getText() + "/%(title)s.%(ext)s");
        if (caseSubFolder.isSelected() && txtSubFolder.getText().equals("") == false)
		{
        	outputFile = new File(lblDestination1.getText() + "/" + txtSubFolder.getText());
			
			if (outputFile.exists() == false)
			{
				outputFile.mkdirs();
			}
			
			outputFile = new File(lblDestination1.getText() + "/" + txtSubFolder.getText() + "/%(title)s.%(ext)s");
		}
        
	    Console.consoleYOUTUBEDL.append(System.lineSeparator() + language.getProperty("command") + " " + format + " " + cmd + options + " --no-continue -o " + '"' + outputFile + '"' + System.lineSeparator());

		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {					
					ProcessBuilder processYOUTUBEDL;
					if (System.getProperty("os.name").contains("Windows"))
					{
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
							processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, "--restrict-filenames", "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
					}
					else
					{
						String youtubedl;						
						if (System.getProperty("os.name").contains("Mac"))
						{							
							youtubedl = "yt-dlp_macos";	
						}
						else
							youtubedl = "yt-dlp_linux";
						
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c", PathToYOUTUBEDL + " " + format + " "+ cmd + options + " --restrict-filenames --no-continue --ffmpeg-location " + PathToYOUTUBEDL.replace(youtubedl, "ffmpeg") + " --no-part -o " + '"' + outputFile + '"');
					}
									
					isRunning = true;	
					process = processYOUTUBEDL.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        BufferedReader ffmpegOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			        			        
			        String lineOutput;
			        boolean readFFmpeg = false;
			        		
			        Console.consoleYOUTUBEDL.append(System.lineSeparator());
			        
					do {
																		
				        if (VideoWeb.caseTimecode.isSelected() && readFFmpeg)
				        {		
				        	String i[] = VideoWeb.textTimecodeIn.getText().split(":");
				        	int inH = Integer.parseInt(i[0]) * 3600;
				        	int inM = Integer.parseInt(i[1]) * 60;				        	
				        	int inS = Integer.parseInt(i[2]);
				        	int totalIn = inH + inM + inS;
				        	
				        	String o[] = VideoWeb.textTimecodeOut.getText().split(":");
				        	int outH = Integer.parseInt(o[0]) * 3600;
				        	int outM = Integer.parseInt(o[1]) * 60;				        	
				        	int outS = Integer.parseInt(o[2]);
				        	int totalOut = outH + outM + outS;
				        	
				        	int duration = (int) totalOut - totalIn;
				        	
				        	lineOutput = ffmpegOutput.readLine();
				        	
				        	if (lineOutput != null && lineOutput.contains("time=") && lineOutput.contains("time=-") == false && lineOutput.contains("time=N/A") == false)
				        	{
				        		String str = lineOutput.substring(lineOutput.indexOf(":") - 2);
				        		String[] split = str.split("b");	 
				        		String ffmpegTime = split[0].replace(".", ":").replace(" ", "");		
	
			        			progressBar1.setValue((int) Math.floor((float) ((float) FFMPEG.getTimeToSeconds(ffmpegTime) * 100) / duration));
				        	}				        				        	
				        }
				        else
				        	lineOutput = br.readLine();
				        																									
					    Console.consoleYOUTUBEDL.append(lineOutput + System.lineSeparator());
		                
					    if (lineOutput != null)
					    {
					    	if (lineOutput.contains("Destination"))
					    	{
					    		 lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
					    		 lblCurrentEncoding.setText(lineOutput.substring(24).replace(lblDestination1.getText(), "").substring(1));
					    		 outputFile = new File(lblDestination1.getText() + "/" + lblCurrentEncoding.getText());
					    		 
					    		 if (VideoWeb.caseTimecode.isSelected())
					    		 {
					    			 readFFmpeg = true;
					    		 }
					    	}
					    		
						    if (lineOutput.contains("ETA") && lineOutput.contains("Unknown ETA") == false && lineOutput.contains("ETA Unknown") == false && VideoWeb.caseTimecode.isSelected() == false)
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
		                        
		                        lblBy.setVisible(false);
		                        tempsRestant.setText(language.getProperty("tempsRestant") + " " + min + time[1].replace(" ","") + "s");
		                        tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);
		                        
		                        if (tempsRestant.getX() + tempsRestant.getSize().width > lblArrows.getX())
			       				{
			       					lblArrows.setVisible(false);
			       				}
						    }
						    
						    if (lineOutput.contains("Sleeping"))
						    {
						    	Matcher m = Pattern.compile("\\d+(?:\\.\\d+)?").matcher(lineOutput);
						    	
						    	if (m.find())
						    	{
						    	    double value = Double.parseDouble(m.group());
						    	    Thread.sleep((int) Math.ceil(value));
						    	}
						    }
					    }
					    						          						        		
					} while (lineOutput != null && cancelled == false);	
					
					process.waitFor();

					Console.consoleYOUTUBEDL.append(System.lineSeparator());
								     	
                    if (cancelled)
                    	outputFile.delete();					
														
					} catch (IOException | InterruptedException e) {
						
	                    JOptionPane.showMessageDialog(frame, language.getProperty("downloadError"), language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	                    error = true;
	                    cancelled = true;
	                    
					} finally {
						isRunning = false;
					}
						
			}				
		});		
		runProcess.start();
	}

	public static void update() {
			
		if (checkYOUTUBEDL() == false)
			return;
		
		error = false;
		cancelled  = false;
		
	    Console.consoleYOUTUBEDL.append(System.lineSeparator() + language.getProperty("command") + " -U " + System.lineSeparator());

		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {			
					
					ProcessBuilder processYOUTUBEDL;					
					if (System.getProperty("os.name").contains("Windows"))
					{											
						processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, "-U");						
					}
					else
					{													
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c", PathToYOUTUBEDL + " -U");
					}
									
					isRunning = true;	
					process = processYOUTUBEDL.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String lineOutput;
			        	
			        Console.consoleYOUTUBEDL.append(System.lineSeparator());
			        
					do {
						lineOutput = br.readLine();		
												
					    Console.consoleYOUTUBEDL.append(lineOutput + System.lineSeparator());		                
						          						        		
					} while(lineOutput != null && cancelled == false);	
					process.waitFor();			
					
					Console.consoleYOUTUBEDL.append(System.lineSeparator());
														
					} catch (IOException | InterruptedException e) {
	                    JOptionPane.showMessageDialog(frame, language.getProperty("downloadError"), language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	                    error = true;
	                    cancelled = true;
					} finally {
						isRunning = false;
					}
						
			}				
		});		
		runProcess.start();
	}

	public static void getAvailableFormats(final String cmd, final String options) {
		
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		lblCurrentEncoding.setText(language.getProperty("getAvailableFormats"));
		error = false;
		cancelled  = false;
		
		Console.consoleYOUTUBEDL.append(System.lineSeparator() + language.getProperty("command") + " -F " + cmd + options + System.lineSeparator());
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {

					ProcessBuilder processYOUTUBEDL;
					if (System.getProperty("os.name").contains("Windows"))
					{
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
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c", PathToYOUTUBEDL + options + " -F " + cmd);
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
			        	
			        Console.consoleYOUTUBEDL.append(System.lineSeparator());

			        String lineOutput;
					//Récupération de l'extension du fichier
					do {
						lineOutput = br.readLine();
						formatsOutput.append(lineOutput + System.lineSeparator());															          						        		
					} while(lineOutput != null && cancelled == false);	
			        
					while ((line = input.readLine()) != null && cancelled == false)
					{							
					    Console.consoleYOUTUBEDL.append(line  + System.lineSeparator());
					    
				        if (line.contains("Not Found") || line.contains("Invalid URL") || line.contains("ERROR"))
				        {
				             error = true;
				             cancelled = true;
				        }
					}	

					process.waitFor();
					
				    Console.consoleYOUTUBEDL.append(formatsOutput + System.lineSeparator() + System.lineSeparator());	
										
				} catch (InterruptedException | IOException e) {
					error = true;
					cancelled = true;
				} finally {
					isRunning = false;
				}
			}
		});		
		runProcess.start();
	}	
}