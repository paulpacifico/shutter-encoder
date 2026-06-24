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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class DCRAW extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess = new Thread();
public static Process process;

	public static void run(final String cmd) {
					
		error = false;
		progressBar.setValue(0);	
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{			
	        RenderQueue.tableRow.addRow(new Object[] {lblCurrentEncoding.getText(), "dcraw_emu" + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG), lblDestination1.getText()});
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
						ProcessBuilder processDCRAW;
						if (System.getProperty("os.name").contains("Windows"))
						{						
							File workingDir = new File(Utils.getLibraryPath());
							
							PathToDCRAW = "dcraw_emu.exe";	
							PathToFFMPEG = "ffmpeg.exe";
							
							process = Runtime.getRuntime().exec(new String[]{"cmd.exe" , "/c", PathToDCRAW + cmd.replace("PathToFFMPEG", PathToFFMPEG)}, null, workingDir);
						}
						else
						{
							PathToDCRAW = Utils.getLibraryPath() + "/dcraw_emu";
							
							processDCRAW = new ProcessBuilder("/bin/bash", "-c" , PathToDCRAW + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));
							process = processDCRAW.start();
							
							processDCRAW.redirectErrorStream(true); //IMPORTANT AVOID FREEZING
						}
						
						Console.consoleDCRAW.append(Shutter.language.getProperty("command") + " " + PathToDCRAW + cmd.replace("PathToFFMPEG", FFMPEG.PathToFFMPEG));
						
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
						
						VideoPlayerUI.setInfo();
						VideoPlayerUI.resizeAll();
						
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