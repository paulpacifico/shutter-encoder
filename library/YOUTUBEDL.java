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

package library;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.VideoWeb;

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
		
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
        lblCurrentEncoding.setText(language.getProperty("getVideoName"));
		error = false;
		Shutter.cancelled  = false;
		
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
							processYOUTUBEDL = new ProcessBuilder(PathToYOUTUBEDL, format, cmd, "--restrict-filenames", "--no-continue", "--no-part", "-o","" + '"' + outputFile +'"');
					}
					else
					{
						PathToYOUTUBEDL = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,PathToYOUTUBEDL.length()-1);
						
						String youtubedl;						
						if (System.getProperty("os.name").contains("Mac"))
						{							
							youtubedl = "yt-dlp_macos";	
						}
						else
							youtubedl = "yt-dlp_linux";

						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/" + youtubedl;
						processYOUTUBEDL = new ProcessBuilder("/bin/bash", "-c" , PathToYOUTUBEDL + " " + format + " "+ cmd + options + " --restrict-filenames --no-continue --ffmpeg-location " + PathToYOUTUBEDL.replace(youtubedl, "ffmpeg") + " --no-part -o " + '"' + outputFile + '"');	
						
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
		                        tempsRestant.setText(Shutter.language.getProperty("tempsRestant") + " " + min + time[1].replace(" ","") + "s");
		                        tempsRestant.setSize(tempsRestant.getPreferredSize().width, 15);
		                        
		                        if (tempsRestant.getX() + tempsRestant.getSize().width > lblArrows.getX())
			       				{
			       					lblArrows.setVisible(false);
			       				}
						    }
					    }
					    						          						        		
					} while (lineOutput != null && Shutter.cancelled == false);	
					
					process.waitFor();

					Console.consoleYOUTUBEDL.append(System.lineSeparator());
								     	
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

	@SuppressWarnings("deprecation")
	public static void HTTPDownload(String fileURL, String yt_dlp) {

		try {
			
			URL url = new URL(fileURL);
	        URLConnection connection = url.openConnection();
	        
	        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(yt_dlp);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, bytesRead);;
            }
            
            if (outputStream != null)
            	outputStream.close();
            
            String[] cmd = {
                    "osascript",
                    "-e",
                    "try",
                    "-e",
                    "set thePassword to text returned of (display dialog \"Enter your password to enable this function:\" with hidden answer default answer \"\" with icon caution buttons {\"OK\", \"Cancel\"} default button \"OK\")",
                    "-e",
                    "on error number -128",
                    "-e",
                    "return \"CANCEL\"",
                    "-e",
                    "end try"
                };
                
            String password = null;            
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            password = reader.readLine();  // Read the password entered by the user
            process.waitFor();
            
            ProcessBuilder processBuilder = new ProcessBuilder("sudo", "-S", "chmod", "+x", yt_dlp);
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            // Write the password to the sudo prompt
            try (java.io.OutputStream os = process.getOutputStream()) {
                os.write((password + "\n").getBytes());
                os.flush();
            }

            process.waitFor();     
            
           if (new File(yt_dlp).canExecute() == false)
           {
        	   try {
   					File toDelete = new File(yt_dlp);
   					toDelete.delete();
   				} catch (Exception er) {}	
           }

		} catch (Exception e) {
			
			try {
				File toDelete = new File(yt_dlp);
				toDelete.delete();
			} catch (Exception er) {}	
		}
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
						
						String youtubedl;						
						if (System.getProperty("os.name").contains("Mac"))
						{							
							youtubedl = "yt-dlp_macos";	
						}
						else
							youtubedl = "yt-dlp_linux";
															
						PathToYOUTUBEDL = PathToYOUTUBEDL.substring(0,(int) (PathToYOUTUBEDL.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/" + youtubedl;
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
						          						        		
					} while(lineOutput != null && Shutter.cancelled == false);	
					process.waitFor();			
					
					Console.consoleYOUTUBEDL.append(System.lineSeparator());
														
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
		
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
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
						
						String youtubedl;						
						if (System.getProperty("os.name").contains("Mac"))
						{							
							youtubedl = "yt-dlp_macos";	
						}
						else
							youtubedl = "yt-dlp_linux";

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
			        	
			        Console.consoleYOUTUBEDL.append(System.lineSeparator());

			        String lineOutput;
					//Récupération de l'extension du fichier
					do {
						lineOutput = br.readLine();
						formatsOutput.append(lineOutput + System.lineSeparator());															          						        		
					} while(lineOutput != null && Shutter.cancelled == false);	
			        
					while ((line = input.readLine()) != null && Shutter.cancelled == false)
					{							
					    Console.consoleYOUTUBEDL.append(line  + System.lineSeparator());
					    
				        if (line.contains("Not Found") || line.contains("Invalid URL") || line.contains("ERROR"))
				        {
				             error = true;
				             Shutter.cancelled = true;
				        }
					}	

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