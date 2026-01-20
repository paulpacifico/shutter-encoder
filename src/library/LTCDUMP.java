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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.Console;
import application.Settings;
import application.Shutter;

public class LTCDUMP extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static Process process;

	public static void run(String file) {
				
		error = false;
		
		FFPROBE.timecode1 = "";
		FFPROBE.timecode2 = "";
		FFPROBE.timecode3 = "";
		FFPROBE.timecode4 = "";
	    				    		
	    try {
			
			ProcessBuilder processLTCDUMP;
										
			if (System.getProperty("os.name").contains("Windows"))
			{								
				process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " -nostats -loglevel 0 -i " + '"' + file + '"' + " -map a:" + comboReadAudioTimecode.getSelectedIndex() + "? -c:a pcm_s16le -vn -sn -f wav -t 1 - | " + '"' + FFMPEG.PathToFFMPEG.replace("ffmpeg", "ltcdump") + '"' + " -"});
			}
			else
			{				
				processLTCDUMP = new ProcessBuilder("/bin/bash", "-c" , FFMPEG.PathToFFMPEG + " -strict " + Settings.comboStrict.getSelectedItem() + " -hide_banner -threads " + Settings.txtThreads.getText() + " -nostats -loglevel 0 -i " + '"' + file + '"' + " -map a:" + comboReadAudioTimecode.getSelectedIndex() + "? -c:a pcm_s16le -vn -sn -f wav -t 1 - | " + FFMPEG.PathToFFMPEG.replace("ffmpeg", "ltcdump") + " -");							
				process = processLTCDUMP.start();
			}					

			isRunning = true;
						         				        
	        String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));				

			Console.consoleFFPROBE.append(System.lineSeparator());	
									
			while ((line = input.readLine()) != null)
			{		
				Console.consoleFFPROBE.append(line + System.lineSeparator());		

				Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}[.:]\\d{2})");
		        Matcher matcher = pattern.matcher(line);
		        
		        if (matcher.find())
		        {	      
		            String str[] = matcher.group(1).replace("." , ":").split(":");
		            
		            FFPROBE.timecode1 = str[0];
                	FFPROBE.timecode2 = str[1];
                	FFPROBE.timecode3 = str[2];
                	FFPROBE.timecode4 = str[3];
                	
                	process.destroy();
                	break;
		        }
			}													
			process.waitFor();	
			
			Console.consoleFFPROBE.append(System.lineSeparator());
																			
			} catch (IOException | InterruptedException e) {							
				error = true;
			} finally {	
				isRunning = false;
			}
	}

}
