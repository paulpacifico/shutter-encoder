/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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
import java.util.ArrayList;

import application.BlackMagicInput;
import application.BlackMagicOutput;
import application.Console;
import application.Shutter;

public class DECKLINK extends Shutter {

public static int dureeTotale = 0;
public static boolean error = false;
public static boolean isRunning = false;
public static boolean asBlackMagic = false;
public static BufferedWriter writer;
public static Thread runProcess;
public static Process process;
public static String getBlackMagic = "";
public static ArrayList<String> formatsList;

	public static void run(final String cmd) {
		error = false;		
	    Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
	    
			isRunning = true;
			
			formatsList = new ArrayList<String>();
			
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToDECKLINK;
						ProcessBuilder processDECKLINK;
						if (System.getProperty("os.name").contains("Windows"))
							{
								PathToDECKLINK = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
								PathToDECKLINK = PathToDECKLINK.substring(1,PathToDECKLINK.length()-1);
								PathToDECKLINK = '"' + PathToDECKLINK.substring(0,(int) (PathToDECKLINK.lastIndexOf("/"))).replace("%20", " ")  + "/Library/decklink.exe" + '"';
								processDECKLINK = new ProcessBuilder(PathToDECKLINK + " -hide_banner " + cmd);	
								process = Runtime.getRuntime().exec(PathToDECKLINK + " -hide_banner " + cmd);
							}
						else
							{
								PathToDECKLINK = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
								PathToDECKLINK = PathToDECKLINK.substring(0,PathToDECKLINK.length()-1);
								PathToDECKLINK = PathToDECKLINK.substring(0,(int) (PathToDECKLINK.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/decklink";
								processDECKLINK = new ProcessBuilder("/bin/bash", "-c" , PathToDECKLINK + " -hide_banner " + cmd);
								process = processDECKLINK.start();
							}
											
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));	
						
						//Permet d'écrire dans le flux
						OutputStream stdin = process.getOutputStream();
				        writer = new BufferedWriter(new OutputStreamWriter(stdin));
						
						while ((line = input.readLine()) != null) {
							
								//Erreurs
								if (line.contains("Invalid data found when processing input") 
										|| line.contains("No such file or directory")
										|| line.contains("Invalid data found")
										|| line.contains("No space left")
										|| line.contains("does not contain any stream")
										|| line.contains("Invalid argument")
										|| line.contains("Error opening filters!")
										|| line.contains("matches no streams"))
								{
									error = true;
									break;
								} 
								
							Progression(line);	
							
							if (line.contains("Could not create DeckLink iterator"))
							{
								asBlackMagic = false;
								break;
							}							
							
							if (cmd.contains("-list_devices") && line.contains("\'"))
							{
								String s[] = line.split("\'");
								getBlackMagic = s[1];
								asBlackMagic = true;		
							}
							
							if (cmd.contains("-list_formats") && line.contains("fps") )
							{		
								int i = 0;
								do {
									i++;									
								} while (line.substring(i, 1).equals(" ") || line.substring(i, 1).matches("^[0-9]+$"));
															
								formatsList.add(line.substring(i));
							}	
							
							Console.consoleFFPLAY.append(line + System.lineSeparator());
																			
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

	public static void toFFPLAY(final String cmd) {
		error = false;		
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					String PathToDECKLINK;
					ProcessBuilder processDECKLINK;
				
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToDECKLINK = "Library\\decklink.exe";
						processDECKLINK = new ProcessBuilder("cmd.exe" , "/c",  PathToDECKLINK + " " + cmd + " " + PathToDECKLINK.replace("decklink", "ffplay") + " -i " + '"' + "pipe:play" + '"' + " -autoexit -x 640 -y 360 -window_title " + '"' + Shutter.language.getProperty("digit") + '"');
					}
					else
					{
						PathToDECKLINK = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToDECKLINK = PathToDECKLINK.substring(0,PathToDECKLINK.length()-1);
						PathToDECKLINK = PathToDECKLINK.substring(0,(int) (PathToDECKLINK.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/decklink";
						processDECKLINK = new ProcessBuilder("/bin/bash", "-c" , PathToDECKLINK + " " + cmd + " " + PathToDECKLINK.replace("decklink", "ffplay") + " -i " + '"' + "pipe:play" + '"' + " -autoexit -x 640 -y 360 -window_title " + '"' + Shutter.language.getProperty("digit") + '"');	
					}
									
					Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToDECKLINK + " " + cmd + " " + PathToDECKLINK.replace("decklink", "ffplay") + " -i " + '"' + "pipe:play" +  '"' + " -autoexit -x 640 -y 360 -window_title " + '"' + Shutter.language.getProperty("digit") + '"'
					+  System.lineSeparator() + System.lineSeparator());
					
					process = processDECKLINK.start();
					
					BlackMagicInput.comboInput.setEnabled(false);
											
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));			
					
					while ((line = input.readLine()) != null) {
						
						Console.consoleFFPLAY.append(line + System.lineSeparator() );		
					
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
							 
						 if (line.contains("frame"))
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
						
																		
					}//While					
					process.waitFor();															
				   					     																		
					} catch (IOException | InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
						BlackMagicInput.comboInput.setEnabled(true);
					}
				
			}				
		});		
		runProcess.start();	
}
	
	public static void toFFMPEG(final String cmd) {
						
		//Permet le bon démarrage
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		
		error = false;		
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					String PathToDECKLINK;
					ProcessBuilder processDECKLINK;
						
					String pipe = "";
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToDECKLINK = "Library\\decklink.exe";
						pipe =  " | " + PathToDECKLINK.replace("decklink", "ffplay") + " -loglevel quiet -autoexit -vf setpts=FRAME_RATE,scale=640:-1 -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + Shutter.language.getProperty("digitIsRunning") + '"';
						processDECKLINK = new ProcessBuilder("cmd.exe" , "/c",  PathToDECKLINK + " " + cmd + pipe);
					}
					else
					{
						PathToDECKLINK = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToDECKLINK = PathToDECKLINK.substring(0,PathToDECKLINK.length()-1);
						PathToDECKLINK = PathToDECKLINK.substring(0,(int) (PathToDECKLINK.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/decklink";
						pipe =  " | " + PathToDECKLINK.replace("decklink", "ffplay") + " -loglevel quiet -autoexit -vf setpts=FRAME_RATE,scale=640:-1 -i " + '"' + "pipe:play" + '"' + " -window_title " + '"' + Shutter.language.getProperty("digitIsRunning") + '"';
						processDECKLINK = new ProcessBuilder("/bin/bash", "-c" , PathToDECKLINK + " " + cmd + pipe);	
					}
									
					Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToDECKLINK + " " + cmd + pipe + System.lineSeparator() + System.lineSeparator());
					
					process = processDECKLINK.start();
					
					BlackMagicInput.comboInput.setEnabled(false);
											
					String line;
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));			
					
					//Permet d'écrire dans le flux
					OutputStream stdin = process.getOutputStream();
			        writer = new BufferedWriter(new OutputStreamWriter(stdin));		
					
					while ((line = input.readLine()) != null) {
						
						Console.consoleFFMPEG.append(line + System.lineSeparator() );		
					
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
							 
						if (line.contains("time="))
						{
							 String s[] = line.split("time=");
							 String time = s[1].replace(" bitrate", "");
							 String s2[] = time.split("\\.");
							 BlackMagicInput.lblTimecode.setText(s2[0]);
							 
							 if (BlackMagicInput.caseStopAt.isSelected())
							 {
								 if (s2[0].equals(BlackMagicInput.TC1.getText() + ":" + BlackMagicInput.TC2.getText() + ":" + BlackMagicInput.TC3.getText()) &&  BlackMagicInput.btnRecord.getText().equals(Shutter.language.getProperty("btnStopRecording")))
									 BlackMagicInput.btnRecord.doClick();
							 }
						}
						
																		
					}//While					
					process.waitFor();															
				   					     																		
					} catch (IOException | InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
						BlackMagicInput.comboInput.setEnabled(true);
					}
				
			}				
		});		
		runProcess.start();	
}	
	
	public static void Progression(String line) {				
		
	  //Calcul de la durée
	  if (line.contains("Duration") && line.contains("Duration: N/A") == false)
	  {
		String str = line.substring(line.indexOf(":") + 2);
		String[] split = str.split(",");	 
	
		String ffmpegTime = split[0].replace(".", ":");	  
				
		dureeTotale = (FFMPEG.CalculTemps(ffmpegTime));
		
		BlackMagicOutput.slider.setMaximum(dureeTotale);
	  		
	  }
	    
	  //Progression
	  if (line.contains("time="))
	  {
  		String str = line.substring(line.indexOf(":") - 2);
		String[] split = str.split("b");	 
	    
		String ffmpegTime = split[0].replace(".", ":").replace(" ", "");	
		if (BlackMagicOutput.sliderChange == false && BlackMagicOutput.btnLire.getText().equals(Shutter.language.getProperty("btnArret")))
			BlackMagicOutput.slider.setValue(BlackMagicOutput.sliderValue + FFMPEG.CalculTemps(ffmpegTime));	
	  }         
	  
	}

}
