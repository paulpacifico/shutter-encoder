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

import java.awt.Color;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import application.Console;
import application.Informations;
import application.Shutter;

public class MEDIAINFO extends Shutter {
	
static int dureeTotale = 0; 
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;

	public static void run(final String file, boolean showInformationsFrame) {
				
		error = false;
		
	    Console.consoleMEDIAINFO.append(Shutter.language.getProperty("command") + " --Output=HTML " + '"' + file.toString() + '"');
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				
				if (showInformationsFrame)
					Informations.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				try {
					
					String PathToMEDIAINFO;
					ProcessBuilder processMEDIAINFO;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToMEDIAINFO = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToMEDIAINFO = PathToMEDIAINFO.substring(1,PathToMEDIAINFO.length()-1);
						PathToMEDIAINFO = '"' + PathToMEDIAINFO.substring(0,(int) (PathToMEDIAINFO.lastIndexOf("/"))).replace("%20", " ")  + "/Library/MediaInfo.exe" + '"';
						processMEDIAINFO = new ProcessBuilder(PathToMEDIAINFO + " --Output=HTML " + '"' + file.toString() + '"');
					}
					else
					{
						PathToMEDIAINFO = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToMEDIAINFO = PathToMEDIAINFO.substring(0,PathToMEDIAINFO.length()-1);
						PathToMEDIAINFO = PathToMEDIAINFO.substring(0,(int) (PathToMEDIAINFO.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/mediainfo";
						processMEDIAINFO = new ProcessBuilder("/bin/bash", "-c" , PathToMEDIAINFO + " --Output=HTML " + '"' + file.toString() + '"');
					}
					
					isRunning = true;	
					Process process = processMEDIAINFO.start();
					 
					InputStreamReader isr = new InputStreamReader(process.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					StringBuilder infoData = new StringBuilder();

					String line;		       
					while ((line = br.readLine()) != null)
					{		
					   infoData.append(line);
					   /*	
					   //Variable Frame Rate
					   if (line.contains("Frame rate mode :"))
					   {						   
						   infoData.append(System.lineSeparator());
						   
						   line = br.readLine();
						   infoData.append(line);
						   
						   String s[] = line.split(">");
						   String s2[] = s[1].split("<");
						   
						   if (s2[0].equals("Variable"))
						   {
							   FFPROBE.currentFPS = FFPROBE.timebaseFPS;
						   }
					   }*/
					   
					   //Timecode
					   if (line.contains("Time code of first frame"))
					   {
						   infoData.append(System.lineSeparator());
						   
						   line = br.readLine();
						   infoData.append(line);
						   			    		   
						   String s[] = line.split(">");
						   String s2[] = s[1].split("<");			    		   
						   String str[]= s2[0].replace(";" , ":").split(":");
							    		   			    		   
			    		   FFPROBE.timecode1 = str[0];
			    		   FFPROBE.timecode2 = str[1];
			    		   FFPROBE.timecode3 = str[2];
			    		   FFPROBE.timecode4 = str[3];
			    	   }
			    	  
					   //Interlaced
					   if (line.contains("Scan type :") && FFPROBE.interlaced == null)
					   {
						   infoData.append(System.lineSeparator());
						   
						   line = br.readLine();
						   infoData.append(line);
						   
						   String s[] = line.split(">");
						   String s2[] = s[1].split("<");	

						   if (s2[0].equals("Interlaced"))
						   {
							   FFPROBE.interlaced = "1";
						   }
						   else
							   FFPROBE.interlaced = "0";						   
					   }					
					   
					   //Field order
					   if (line.contains("Scan order :") && FFPROBE.interlaced.equals("1") && FFPROBE.fieldOrder == null)
					   {
						   infoData.append(System.lineSeparator());
						   
						   line = br.readLine();
						   infoData.append(line);
						   
						   String s[] = line.split(">");
						   String s2[] = s[1].split("<");	

						   if (s2[0].equals("Bottom Field First"))
						   {
							   FFPROBE.fieldOrder = "1";
						   }
						   else
							   FFPROBE.fieldOrder = "0";
					   }					  
					   
			    	   infoData.append(System.lineSeparator());
					}
									    
					process.waitFor();
					
					Console.consoleMEDIAINFO.append(System.lineSeparator());
					
					String StrTotal = "";
					
					int x = infoData.indexOf("<head>");
					x = infoData.indexOf("<head>", x + 1);
					x = infoData.indexOf("<head>", x + 1);
					
					StrTotal = infoData.substring(infoData.indexOf("<head>") + (x + 3));
				  
					Console.consoleMEDIAINFO.append("<html>" + System.lineSeparator() + "<head>" + System.lineSeparator() + StrTotal.toString());	
					
					if (showInformationsFrame)
					{
			           //Adding tab	           	
					   Informations.addTabControl();		           
					   
					   String informationsFrameSize = String.valueOf((int) Informations.frame.getSize().width - 320);
					   String htmlSize = "";
					   for (String l : StrTotal.toString().split(System.lineSeparator()))
					   {
					   		if (l.contains("td width"))
					   		{
					   			String s[] = l.split("\"");
								htmlSize = s[1];
							}
					   			
					   }
					   
					   	JLabel content = new JLabel("<html>" + System.lineSeparator() + "<head>" + System.lineSeparator()
					   	+ StrTotal.toString().replace(htmlSize, informationsFrameSize).replace("<body>", "<body style=background-color:rgb(35,35,35);>").replace("border:1px solid Navy", "border-bottom:1px solid rgb(55,55,55); border-top:1px solid rgb(55,55,55)"));
					   	
				       	content.setBackground(new Color(35,35,35));
				       	content.setForeground(new Color(240,240,240));
				       	content.setOpaque(true);
				       		
				       	JScrollPane scrollPane = new JScrollPane();
						scrollPane.setViewportView(content);
						scrollPane.setBounds(Informations.tabPanel.getBounds());	
						scrollPane.getVerticalScrollBar().setUnitIncrement(16);
						
						Informations.lblWait.setVisible(false);
						Informations.lblArrows.setVisible(true);
							
						Informations.infoTabbedPane.addTab(new File(file).getName(), scrollPane);	    			
					}
			       
				} catch (IOException | InterruptedException e) {
					error = true;
				} finally {
					isRunning = false;
				}
				
				if (showInformationsFrame)
					Informations.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}				
		});		
		runProcess.start();
	}

}
