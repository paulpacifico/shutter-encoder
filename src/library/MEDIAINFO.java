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
import application.Utils;

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

					Console.consoleMEDIAINFO.append(System.lineSeparator());
					
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
					   if (line.contains("Time code of first frame") && FFPROBE.timecode1 == "")
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

					Console.consoleMEDIAINFO.append("<html>" + System.lineSeparator() + "<head>" + System.lineSeparator() + formatHTMLOutput(infoData));	
					
					if (showInformationsFrame)
					{
						//Adding tab	           	
						Informations.addTabControl();			   
					   
						// Strip any leftover head/body wrapper tags from StrTotal
						String cleanContent = formatHTMLOutput(infoData)
						    .replaceAll("(?s)^.*?<body[^>]*>", "")  // remove everything up to and including <body>
						    .replaceAll("(?s)</body>.*?$", "")        // remove </body> and everything after
						    .replaceAll("(?s)</html>", "")
						    .trim();

						String labelHtml = "<html><body style=\"background-color:rgb(" + Utils.c30.getRed() + "," + Utils.c30.getGreen() + "," + Utils.c30.getBlue() + "); color:rgb(235,235,240);\">"
						    + cleanContent
						        .replace("border:1px solid Navy", "border-top:1px solid rgb(55,55,55)")
						    + "</body></html>";

						JLabel content = new JLabel(labelHtml);
					   	
						content.setBackground(Utils.c30);
				       	content.setForeground(new Color(235,235,240));
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

	protected static String formatHTMLOutput(StringBuilder infoData) {
		
		String htmlOutput = infoData.toString();

		// Convert new MediaInfo HTML format to legacy format
		StringBuilder legacyHtml = new StringBuilder();
		legacyHtml.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n</head>\n<body>\n");

		// Parse each <table> block from the new format
		java.util.regex.Pattern tablePattern = java.util.regex.Pattern.compile(
		    "<table>(.*?)</table>", 
		    java.util.regex.Pattern.DOTALL
		);
		java.util.regex.Matcher tableMatcher = tablePattern.matcher(htmlOutput);

		while (tableMatcher.find()) {
		    String tableContent = tableMatcher.group(1);
		    
		    legacyHtml.append("<table width=\"100%\" border=\"0\" cellpadding=\"1\" cellspacing=\"2\" style=\"border:1px solid Navy\">\n");
		    
		    // Extract section header (h2)
		    java.util.regex.Pattern h2Pattern = java.util.regex.Pattern.compile("<h2>(.*?)</h2>");
		    java.util.regex.Matcher h2Matcher = h2Pattern.matcher(tableContent);
		    if (h2Matcher.find()) {
		        legacyHtml.append("<tr>\n  <td width=\"150\"><h2>").append(h2Matcher.group(1)).append("</h2></td>\n</tr>\n");
		    }
		    
		    // Extract data rows: <td class="Prefix">label</td><td>value</td>
		    java.util.regex.Pattern rowPattern = java.util.regex.Pattern.compile(
		        "<td class=\"Prefix\">(.*?)</td>\\s*<td>(.*?)</td>",
		        java.util.regex.Pattern.DOTALL
		    );
		    java.util.regex.Matcher rowMatcher = rowPattern.matcher(tableContent);
		    while (rowMatcher.find()) {
		        String label = rowMatcher.group(1).trim();
		        String value = rowMatcher.group(2).trim();
		        legacyHtml.append("<tr>\n  <td><i>").append(label).append("</i></td>\n  <td colspan=\"3\">").append(value).append("</td>\n</tr>\n");
		    }
		    
		    legacyHtml.append("</table>\n<br />\n");
		}

		legacyHtml.append("</body>\n</html>");

		String StrTotal = legacyHtml.toString();
		
		// Remove the "<html>\n<head>\n" prefix since it gets prepended later
		return StrTotal.substring("<html>\n<head>\n".length());
		
	}

}
