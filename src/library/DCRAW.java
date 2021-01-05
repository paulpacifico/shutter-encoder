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

/*
   dcraw.c -- Dave Coffin's raw photo decoder
   Copyright 1997-2016 by Dave Coffin, dcoffin a cybercom o net

   This is a command-line ANSI C program to convert raw photos from
   any digital camera on any computer running any operating system.

   No license is required to download and use dcraw.c.  However,
   to lawfully redistribute dcraw, you must either (a) offer, at
   no extra charge, full source code* for all executable files
   containing RESTRICTED functions, (b) distribute this code under
   the GPL Version 2 or later, (c) remove all RESTRICTED functions,
   re-implement them, or copy them from an earlier, unrestricted
   Revision of dcraw.c, or (d) purchase a license from the author.

   The functions that process Foveon images have been RESTRICTED
   since Revision 1.237.  All other code remains free for all uses.

   *If you have not modified dcraw.c in any way, a link to my
   homepage qualifies as "full source code".

   $Revision: 1.477 $
   $Date: 2016/05/10 21:30:43 $
 */

package library;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import application.ColorImage;
import application.Console;
import application.RenderQueue;
import application.Shutter;

public class DCRAW extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess = new Thread();
public static Process process;

	public static void run(final String cmd) {
					
		error = false;
		progressBar1.setValue(0);	
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
			String PathToDCRAW;
			String PathToFFMPEG;
			if (System.getProperty("os.name").contains("Windows"))
			{
				PathToDCRAW = "Library\\dcraw.exe";
				PathToFFMPEG = "Library\\ffmpeg.exe";
			}
			else
			{
				PathToDCRAW = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToDCRAW = PathToDCRAW.substring(0,PathToDCRAW.length()-1);
				PathToDCRAW = PathToDCRAW.substring(0,(int) (PathToDCRAW.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/dcraw";
				PathToFFMPEG = PathToDCRAW.replace("dcraw", "ffmpeg");
			}
			
	        RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "dcraw" + cmd.replace("PathToFFMPEG", PathToFFMPEG), lblDestination1.getText()});
	        lblEncodageEnCours.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
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
						String PathToDCRAW;
						String PathToFFMPEG;
						ProcessBuilder processDCRAW;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToDCRAW = "Library\\dcraw.exe";
							PathToFFMPEG = "Library\\ffmpeg.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG)});
						}
						else
						{
							PathToDCRAW = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToDCRAW = PathToDCRAW.substring(0,PathToDCRAW.length()-1);
							PathToDCRAW = PathToDCRAW.substring(0,(int) (PathToDCRAW.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/dcraw";
							PathToFFMPEG = PathToDCRAW.replace("dcraw", "ffmpeg");
							
							processDCRAW = new ProcessBuilder("/bin/bash", "-c" , PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG));								
							process = processDCRAW.start();
						}
						
						Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG) + System.lineSeparator() + System.lineSeparator());
						
						isRunning = true;
						
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
						
						while ((line = input.readLine()) != null) {							
						    Console.consoleFFMPEG.append(line + System.lineSeparator());																		
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
	
	public static void toFFPLAY(final String filter) {
		error = false;		
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				try {
					
					String PathToDCRAW;
					ProcessBuilder processDCRAW;
					File file;
					if (fileList.getSelectedIndices().length == 0)
						file = new File(liste.firstElement());
					else							
						file = new File(fileList.getSelectedValue());
				
					String fullscreen = "";
					if (ColorImage.frame != null)
					{
						if (ColorImage.frame.isVisible())							
							fullscreen = " -fs";							
					}
						
					
					if (System.getProperty("os.name").contains("Windows"))
					{						
						PathToDCRAW = "Library\\dcraw.exe";						
						process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c",  PathToDCRAW + " -v -w -c -q 0 -6 " + '"' + file.toString() + '"' + " | " + PathToDCRAW.replace("dcraw", "ffplay") + fullscreen + " -i -" + filter + " -window_title " + '"' + file + '"'});
					}
					else
					{
						PathToDCRAW = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToDCRAW = PathToDCRAW.substring(0,PathToDCRAW.length()-1);
						PathToDCRAW = PathToDCRAW.substring(0,(int) (PathToDCRAW.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/dcraw";
						processDCRAW = new ProcessBuilder("/bin/bash", "-c" , PathToDCRAW + " -v -w -c -q 0 -6 " + '"' + file.toString() + '"' + " | " + PathToDCRAW.replace("dcraw", "ffplay") + fullscreen + " -i -" + filter + " -window_title " + '"' + file + '"');	
						process = processDCRAW.start();
					}
									
					Console.consoleFFPLAY.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + PathToDCRAW + " -v -w -c -q 0 -6 " + '"' + file.toString() + '"' + " | " + PathToDCRAW.replace("dcraw", "ffplay") + fullscreen + " -i -" + filter + " -window_title " + '"' + file + '"'
					+  System.lineSeparator() + System.lineSeparator());
						
					isRunning = true;
					
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
					}
				
			}				
		});		
		runProcess.start();	
	}
	
}
