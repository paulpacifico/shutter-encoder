/*******************************************************************************************
* Copyright (C) 2020 PACIFICO PAUL
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

import application.Console;
import application.RenderQueue;
import application.Shutter;

public class MKVMERGE extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess = new Thread();
public static Process process;

	public static void run(final String cmd) {				
	    Console.consoleMKVMERGE.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
	    
		error = false;
		btnStart.setEnabled(false);	
		
		if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) && RenderQueue.btnStartRender.isEnabled())
		{
			btnStart.setEnabled(true);	
	        RenderQueue.tableRow.addRow(new Object[] {lblEncodageEnCours.getText(), "mkvmerge " + checkList(cmd), lblDestination1.getText()});
	        lblEncodageEnCours.setText(Shutter.language.getProperty("lblEncodageEnCours"));	        
			
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
		}
		else
		{
			disableAll();
			
		    progressBar1.setValue(0);
		    progressBar1.setMaximum(100);
		    
			runProcess = new Thread(new Runnable()  {
				@Override
				public void run() {
					try {
						String PathToMKVMERGE;
						ProcessBuilder processMKVMERGE;
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToMKVMERGE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToMKVMERGE = PathToMKVMERGE.substring(1,PathToMKVMERGE.length()-1);
							PathToMKVMERGE = '"' + PathToMKVMERGE.substring(0,(int) (PathToMKVMERGE.lastIndexOf("/"))).replace("%20", " ")  + "/Library/mkvmerge.exe" + '"';
							processMKVMERGE = new ProcessBuilder(PathToMKVMERGE + " " + cmd);
						}
						else
						{
							PathToMKVMERGE = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToMKVMERGE = PathToMKVMERGE.substring(0,PathToMKVMERGE.length()-1);
							PathToMKVMERGE = PathToMKVMERGE.substring(0,(int) (PathToMKVMERGE.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/mkvmerge";
							processMKVMERGE = new ProcessBuilder("/bin/bash", "-c" , PathToMKVMERGE + " " + cmd);
						}
						
						isRunning = true;	
						process = processMKVMERGE.start();																		
						
						String line;
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);				
						
						while ((line = br.readLine()) != null) {							
						    Console.consoleMKVMERGE.append(line + System.lineSeparator());	
						    
						    if (line.contains("Progress"))
						    {
						    	String s[] = line.split(": ");
						    	progressBar1.setValue(Integer.parseInt(s[1].replace("%", "")));
						    }
						    
						    if (line.contains("Error"))
						    {
						    	FFMPEG.error = true;
						    	process.destroy();
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

	private static String checkList(String cmd) {
		
		//On vérifie que le fichier n'existe pas déjà dans le cas contraire on l'incrémente
		String cmdFinale = cmd;		
		String s[] = cmd.split("\"");
		String cmdFile = s[s.length - 1];
		String cmdFileHDR = s[s.length - 3];
		
		int n = 0;
		for (int i = 0 ; i < RenderQueue.tableRow.getRowCount() ; i++)
		{								
			String s2[] = RenderQueue.tableRow.getValueAt(i, 1).toString().split("\"");
			String renduFile = s2[s2.length - 1];
			
			if (cmdFile.equals(renduFile) && RenderQueue.tableRow.getValueAt(i, 1).toString().contains("mkvmerge"))
			{
				n++;
				String s3[] = cmd.split("\"");
				String ext = cmdFile.substring(cmdFile.lastIndexOf("."), cmdFile.lastIndexOf(".") + 4);
				
				String originalCmdFile = s3[s3.length - 1];			
				cmdFile = originalCmdFile.replace(ext,  "_" + n + ext);	
				
				String originalCmdFileHDR = s3[s3.length - 3];	
				cmdFileHDR = originalCmdFileHDR.replace("_HDR" + ext, "_" + n + "_HDR" + ext);
			}
		}
		
		String s4[] = cmd.split("\"");
		cmdFinale = cmd.replace(s4[s4.length - 1], cmdFile).replace(s4[s4.length - 3], cmdFileHDR);

		return cmdFinale;
	}
}
