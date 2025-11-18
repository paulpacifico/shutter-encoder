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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import application.Console;
import application.RenderQueue;
import application.Shutter;
import application.VideoPlayer;

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
				PathToDCRAW = "Library\\dcraw_emu.exe";
				PathToFFMPEG = "Library\\ffmpeg.exe";
			}
			else
			{
				PathToDCRAW = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToDCRAW = PathToDCRAW.substring(0,PathToDCRAW.length()-1);
				PathToDCRAW = PathToDCRAW.substring(0,(int) (PathToDCRAW.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/dcraw_emu";
				PathToFFMPEG = PathToDCRAW.replace("dcraw_emu", "ffmpeg");
			}
			
	        RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "dcraw_emu" + cmd.replace("PathToFFMPEG", PathToFFMPEG), lblDestination1.getText()});
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
						String PathToDCRAW;
						String PathToFFMPEG;
						ProcessBuilder processDCRAW = null;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToDCRAW = "Library\\dcraw_emu.exe";
							PathToFFMPEG = "Library\\ffmpeg.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG)});
						}
						else
						{
							PathToDCRAW = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToDCRAW = PathToDCRAW.substring(0,PathToDCRAW.length()-1);
							PathToDCRAW = PathToDCRAW.substring(0,(int) (PathToDCRAW.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/dcraw_emu";
							PathToFFMPEG = PathToDCRAW.replace("dcraw_emu", "ffmpeg");
							
							processDCRAW = new ProcessBuilder("/bin/bash", "-c" , PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG));								
							process = processDCRAW.start();
							
							processDCRAW.redirectErrorStream(true); //IMPORTANT AVOID FREEZING
						}
						
						Console.consoleDCRAW.append(Shutter.language.getProperty("command") + " " + PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG));
						
						isRunning = true;
						
						if (cmd.contains("-f rawvideo"))
						{
							InputStream is = process.getInputStream();				
							BufferedInputStream inputStream = new BufferedInputStream(is);
							
							VideoPlayer.readFrame(inputStream, VideoPlayer.player.getWidth(), VideoPlayer.player.getHeight(), true);
							VideoPlayer.preview = VideoPlayer.cloneBufferedImage(VideoPlayer.frameVideo);
							
							inputStream.close();

							if (VideoPlayer.frameVideo != null)
							{
								VideoPlayer.player.repaint();
							}
						}						
						process.waitFor();
						
						VideoPlayer.setInfo();
						VideoPlayer.resizeAll();
						
						Console.consoleDCRAW.append(System.lineSeparator());
										
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
}