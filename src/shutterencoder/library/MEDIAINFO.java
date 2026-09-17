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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.Informations;
import shutterencoder.utils.Utils;

public class MEDIAINFO extends Shutter {
	
	public static int totalDuration = 0; 
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
						PathToMEDIAINFO = '"' + Utils.getLibraryPath() + "\\MediaInfo.exe" + '"';
						processMEDIAINFO = new ProcessBuilder(PathToMEDIAINFO + " --Output=HTML " + '"' + file.toString() + '"');
					}
					else
					{						
						PathToMEDIAINFO = Utils.getLibraryPath() + "/mediainfo";
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
					   
					   //Timecode
					   if (line.contains("Time code of first frame") && FFPROBE.timecode1 == "")
					   {
						   infoData.append(System.lineSeparator());
						   
						   line = br.readLine();
						   infoData.append(line);

						   Matcher matcher = Pattern .compile("\\b(\\d{2}):(\\d{2}):(\\d{2})([:;])(\\d{2})\\b").matcher(line);

			                if (matcher.find() && FFPROBE.timecode1.isEmpty())
			                {
			                    FFPROBE.timecode1 = matcher.group(1);
			                    FFPROBE.timecode2 = matcher.group(2);
			                    FFPROBE.timecode3 = matcher.group(3);
			                    FFPROBE.timecode4 = matcher.group(5);

			                    FFPROBE.dropFrameTC = matcher.group(4); //Drop frame / non drop frame
			                }
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
					Console.consoleMEDIAINFO.append("<html>" + System.lineSeparator() + "<head>" + System.lineSeparator() + formatHTMLOutput(infoData, showInformationsFrame));	
					
					if (showInformationsFrame)
					{
						//Adding tab	           	
						Informations.addTabControl();			   
					}
										
					//Add to the component
					if (showInformationsFrame)
					{
						// Strip any leftover head/body wrapper tags from StrTotal
						String cleanContent = formatHTMLOutput(infoData, true)
						    .replaceAll("(?s)^.*?<body[^>]*>", "")  // remove everything up to and including <body>
						    .replaceAll("(?s)</body>.*?$", "")        // remove </body> and everything after
						    .replaceAll("(?s)</html>", "")
						    .trim();
							
						String labelHtml = "<html><body style=\"background-color:rgb(" + Utils.c25.getRed() + "," + Utils.c25.getGreen() + "," + Utils.c25.getBlue() + "); color:rgb(235,235,240);\">"
							    + cleanContent.replace("border:1px solid Navy", "border-top:1px solid rgb(55,55,55)")
							    + "</body></html>";
						
						JLabel content = new JLabel(labelHtml);					   	
						content.setBackground(Utils.c25);
				       	content.setForeground(new Color(235,235,240));
				       	content.setOpaque(true);
				       	
						JScrollPane scrollPane = new JScrollPane();
						scrollPane.setBackground(Utils.c25);
				       	scrollPane.setBorder(null);
						scrollPane.setViewportView(content);							
						scrollPane.getVerticalScrollBar().setUnitIncrement(16);						
						scrollPane.setBounds(Informations.tabPanel.getBounds());
						
						JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
						verticalBar.setBackground(Utils.c25);

						JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
						horizontalBar.setBackground(Utils.c25);
	
						Informations.lblWait.setVisible(false);
						Informations.lblArrows.setVisible(true);							
						Informations.infoTabbedPane.addTab(new File(file).getName(), scrollPane);	
					}
					
					// Strip any leftover head/body wrapper tags from StrTotal
					String cleanContent = formatHTMLOutput(infoData, false)
					    .replaceAll("(?s)^.*?<body[^>]*>", "")  // remove everything up to and including <body>
					    .replaceAll("(?s)</body>.*?$", "")        // remove </body> and everything after
					    .replaceAll("(?s)</html>", "")
					    .trim();
						
					String labelHtml = "<html><body style=\"background-color:rgb(" + Utils.c25.getRed() + "," + Utils.c25.getGreen() + "," + Utils.c25.getBlue() + "); color:rgb(235,235,240);\">"
						    + cleanContent.replace("border:1px solid Navy", "border-top:1px solid rgb(55,55,55)")
						    + "</body></html>";
					
					JLabel content = new JLabel(labelHtml);					   	
					content.setBackground(Utils.c25);
			       	content.setForeground(new Color(235,235,240));
			       	content.setOpaque(true);
					
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setBackground(Utils.c25);
			       	scrollPane.setBorder(null);
					scrollPane.setViewportView(content);							
					scrollPane.getVerticalScrollBar().setUnitIncrement(16);
					
					JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
					verticalBar.setBackground(Utils.c25);

					JScrollBar horizontalBar = scrollPane.getHorizontalScrollBar();
					horizontalBar.setBackground(Utils.c25);
					
				    grpFileInformation.removeAll();
				    grpFileInformation.setLayout(new BorderLayout());
				    grpFileInformation.setBorder(BorderFactory.createEmptyBorder(14, 7, 4, 2));
				    grpFileInformation.add(scrollPane, BorderLayout.CENTER);	
				    grpFileInformation.revalidate();
				    grpFileInformation.repaint();			
			       
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

	protected static String formatHTMLOutput(StringBuilder infoData, boolean showInformationsFrame) {
	    
	    String htmlOutput = infoData.toString();

	    // Convert new MediaInfo HTML format to legacy format
	    StringBuilder legacyHtml = new StringBuilder();
	    
	    legacyHtml.append("<html>\n<head>\n");
	    legacyHtml.append("<body style=\"font-family: FreeSans, sans-serif;\">\n");

	    // Parse each <table> block from the new format
	    Pattern tablePattern = Pattern.compile(
	        "<table>(.*?)</table>", 
	        Pattern.DOTALL
	    );
	    Matcher tableMatcher = tablePattern.matcher(htmlOutput);

	    boolean isFirstTable = true;
	    while (tableMatcher.find()) {
	        String tableContent = tableMatcher.group(1);
	        
	        String tableBase = "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: FreeSans, sans-serif; color: rgb(225,225,225); font-size: 10px; margin-left: 4px; ";
	        if (showInformationsFrame)
	        {
	        	tableBase = "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: FreeSans, sans-serif; color: rgb(225,225,225); font-size: 10px; margin-left: 7px; ";
	        }
	        
	        if (isFirstTable)
	        {
	            legacyHtml.append(tableBase + "border-top:0px solid rgb(" + Utils.c42.getRed() + "," + Utils.c42.getGreen() + "," + Utils.c42.getBlue() + ")\">\n");
	            isFirstTable = false;
	        }
	        else
	            legacyHtml.append(tableBase + "border-top:1px solid rgb(" + Utils.c42.getRed() + "," + Utils.c42.getGreen() + "," + Utils.c42.getBlue() + ")\">\n");
	        	        
	        // Extract section header (h2) - Inline Montserrat
	        Pattern h2Pattern = Pattern.compile("<h2>(.*?)</h2>");
	        Matcher h2Matcher = h2Pattern.matcher(tableContent);
	        if (h2Matcher.find()) {
	            legacyHtml.append("<tr>\n  <td width=\"150\"><h2 style=\"font-family: Montserrat, sans-serif; font-weight: bold;\">")
	                      .append(h2Matcher.group(1))
	                      .append("</h2></td>\n</tr>\n");
	        }
	        
	        // Extract data rows: <td class="Prefix">label</td><td>value</td> - Inline FreeSans
	        Pattern rowPattern = Pattern.compile(
	        	    "<td class=\"Prefix\">(.*?)</td>\\s*<td>(.*?)</td>",
	        	    Pattern.DOTALL
	        	);
	        	Matcher rowMatcher = rowPattern.matcher(tableContent);
	        	while (rowMatcher.find()) {
	        	    String label = rowMatcher.group(1).trim();
	        	    String value = rowMatcher.group(2).trim();
	        	    
	        	    // Skip "Complete name" row	        	    
	        	    if (label.contains("Complete name"))
	        	    {
	        	        continue;
	        	    }
	        	    
	        	    // Removed <i> and </i> tags around label
	        	    legacyHtml.append("<tr>\n  <td style=\"font-family: FreeSans, sans-serif; font-style: normal;\">")
	        	              .append(label)
	        	              .append("</td>\n  <td colspan=\"3\" style=\"font-family: FreeSans, sans-serif;\">")
	        	              .append(value)
	        	              .append("</td>\n</tr>\n");
	        	}
	        
	        legacyHtml.append("</table>\n<br />\n");
	    }

	    legacyHtml.append("</body>\n</html>");

	    String StrTotal = legacyHtml.toString();
	    
	    // Remove the exact "<html>\n<head>\n" prefix without eating the content
	    return StrTotal.substring("<html>\n<head>\n".length());
	}

}
