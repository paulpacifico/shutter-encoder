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

	public static void run(final String cmd, final String fichier) {
				
	    Console.consoleMEDIAINFO.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
		
		runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
				Informations.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
	
					//Analyse
					FFPROBE.Data(fichier);
					
					do
					{
						Thread.sleep(100);
					} while (FFPROBE.isRunning);
					
					String PathToMEDIAINFO;
					ProcessBuilder processMEDIAINFO;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToMEDIAINFO = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToMEDIAINFO = PathToMEDIAINFO.substring(1,PathToMEDIAINFO.length()-1);
						PathToMEDIAINFO = '"' + PathToMEDIAINFO.substring(0,(int) (PathToMEDIAINFO.lastIndexOf("/"))).replace("%20", " ")  + "/Library/MediaInfo.exe" + '"';
						processMEDIAINFO = new ProcessBuilder(PathToMEDIAINFO + " " + cmd);
					}
					else
					{
						PathToMEDIAINFO = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToMEDIAINFO = PathToMEDIAINFO.substring(0,PathToMEDIAINFO.length()-1);
						PathToMEDIAINFO = PathToMEDIAINFO.substring(0,(int) (PathToMEDIAINFO.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/mediainfo";
						processMEDIAINFO = new ProcessBuilder("/bin/bash", "-c" , PathToMEDIAINFO + " " + cmd);
					}
				
					isRunning = true;	
					Process process = processMEDIAINFO.start();
			         
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        StringBuilder infoData = new StringBuilder();

			       int stopMediaInfo = 0; //Seul moyen de tout récupérer et stopper le flux à la fois
			        while (stopMediaInfo < 300)
			        	{
			        	
			        	stopMediaInfo++;
			        	infoData.append(br.readLine());
			        	infoData.append(System.lineSeparator());
			        	
					    };
			        
					if (stopMediaInfo >= 300)
						process.destroy();
					    
					process.waitFor();
   
					String StrTotal = "";
					
		            int x = infoData.indexOf("<head>");
		            x = infoData.indexOf("<head>", x + 1);
		            x = infoData.indexOf("<head>", x + 1);
		            
		            StrTotal = infoData.substring(infoData.indexOf("<head>") + (x + 3));
		            StringBuilder FinalXML = new StringBuilder();
		            String lines[] = StrTotal.split(System.lineSeparator());
		            
		            int i = 0;
		            
		           for (String line : lines)
		           {
		                if (i >= 1)
		                {
		                    i ++;
		                    if (i == 4)
		                        FinalXML.append(System.lineSeparator() + "<tr>" + System.lineSeparator() + "<td><i>Luminance level :</i></td>" + System.lineSeparator() + "<td colspan=" + '"' + "3" + '"' + ">" + FFPROBE.lumaLevel + "</td>" + System.lineSeparator() + "</tr>");
		                }
		                if (line.contains("Color space :"))
		                    i ++;
		                
		                FinalXML.append(line);
		                FinalXML.append(System.lineSeparator());
		           }			        
									
		           //Ajout de la Tab	           	
		           Informations.addTabControl();		           
		           
		           String informationsFrameSize = String.valueOf((int) Informations.frame.getSize().width - 320);
		           String htmlSize = "";
		           for (String line : FinalXML.toString().split(System.lineSeparator()))
		        	   {
		        	   		if (line.contains("td width"))
		        	   			{
		        	   				String s[] = line.split("\"");
		        	   				htmlSize = s[1];
		        	   			}
		        	   			
		        	   }
		           
		           	JLabel content = new JLabel("<html>" + System.lineSeparator() + "<head>" + System.lineSeparator() + FinalXML.toString().replace(htmlSize, informationsFrameSize));
		           	content.setBackground(new Color(245,245,245));
		           	content.setForeground(Color.BLACK);
		           	content.setOpaque(true);
		           		
		           	JScrollPane scrollPane = new JScrollPane();
		   			scrollPane.setViewportView(content);
		   			scrollPane.setBounds(Informations.tabPanel.getBounds());	
		   			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		   			
		   			Informations.lblWait.setVisible(false);
		   			Informations.lblFlecheBas.setVisible(true);
		   				
		   			Informations.infoTabbedPane.addTab(new File(fichier).getName(), scrollPane);		    
									
					} catch (IOException | InterruptedException e) {
						error = true;
					} finally {
						isRunning = false;
					}
				
				Informations.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}				
		});		
		runProcess.start();
	}

}
