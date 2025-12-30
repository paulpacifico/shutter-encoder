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

package functions;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

import application.Ftp;
import application.Shutter;
import application.Utils;
import library.DEOLDIFY;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;

public class Colorize extends Shutter {
	
	public static File sourceFile = null;
	public static Thread thread;
	
    public static void main() {
				
    	sourceFile = null;
    	
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				DEOLDIFY.patchDeoldify();
				DEOLDIFY.patchFFmpeg();
				DEOLDIFY.downloadModel();
						
				if (new File(DEOLDIFY.deoldifyModel).exists())
				{						
					File tempFolder = null;
					
					for (int i = 0 ; i < list.getSize() ; i++)
					{		
						File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));	
															
						if (file == null)
							break;
						
						try {
							
							String fileName = file.getName();
							
							lblCurrentEncoding.setText(fileName);	
	
							String extension = fileName.substring(fileName.lastIndexOf("."));	
							
							//Output folder
							String labelOutput = FunctionUtils.setOutputDestination("", file);
							
							lblCurrentEncoding.setText(fileName);
							tempsEcoule.setVisible(false);
							
							String model = comboFilter.getSelectedItem().toString();
							
							//File output name
							String prefix = "";	
							if (casePrefix.isSelected())
							{
								prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
							}
							
							String extensionName = "";	
							if (btnExtension.isSelected())
							{
								extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);
							}
							
							//Output name
							String fileOutputName =  labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + extension); 
										
							//File output
							File fileOut = new File(fileOutputName);						
							if (fileOut.exists())
							{						
								fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, "_", extension);
								
								if (fileOut == null)
								{
									cancelled = true;
									break;
								}
								else if (fileOut.toString().equals("skip"))
								{
									continue;
								}
							}	
							
							if (comboFilter.getSelectedItem().equals("video"))
							{
								tempFolder = new File(labelOutput + "/" + fileName.replace(extension, ""));
								tempFolder.mkdirs();
							}
							
							//Run deoldify
							DEOLDIFY.run(file.toString(), model);
							if (comboFilter.getSelectedItem().equals("video"))
							{
								do {								
									Thread.sleep(1000);		
									
									File extractFolder = new File(DEOLDIFY.deoldifyFolder + "/video/bwframes/" + fileName.replace(extension, ""));
									File colorFolder = new File(DEOLDIFY.deoldifyFolder + "/video/colorframes/" + fileName.replace(extension, ""));
									
									//Get progress of extraction
									if (extractFolder.exists() && colorFolder.exists() == false && cancelled == false)
				            		{
			            				int files = extractFolder.listFiles().length;
					            		progressBar1.setValue((int) ((float) files * 100 / ((float) ((float) FFPROBE.totalLength / 1000) * FFPROBE.currentFPS)));
				            		}
									
									//Move the colored frames to output tempFolder
									if (comboFilter.getSelectedItem().equals("video") && colorFolder.exists())
									{									
							            try (DirectoryStream<Path> stream = Files.newDirectoryStream(colorFolder.toPath()))
							            {
							                for (Path f : stream)
							                {
							                    if (Files.isRegularFile(f))
							                    {
							                        Path targetFile = tempFolder.toPath().resolve(f.getFileName());
							                        Files.move(f, targetFile, StandardCopyOption.REPLACE_EXISTING);
							                        
							                        File BWFrame = new File(f.toString().replace("colorframes", "bwframes"));
							                        BWFrame.delete();
							                    }
							                }
							            } catch (Exception e) {}
									}
									
								} while (DEOLDIFY.runProcess.isAlive());	
							}		
							else
							{
								do {								
									Thread.sleep(100);	
								} while (DEOLDIFY.runProcess.isAlive());	
							}

							if (comboFilter.getSelectedItem().equals("video"))		
							{
								//Delete the video folder
								try {
									FileUtils.deleteDirectory(new File(DEOLDIFY.deoldifyFolder + "/video"));
								} catch (Exception e) {}
								
								if (cancelled)
								{	
									if (tempFolder != null && tempFolder.exists())
										FileUtils.deleteDirectory(tempFolder);										
								}
								else //Create the video from image sequence
								{
									sourceFile = file;
									
									if (FFPROBE.videoCodec != null)
									{
										String vcodec = FFPROBE.videoCodec.replace("video", "");
										for (String s : Shutter.functionsList)
										{
											if (vcodec.toLowerCase().equals(s.replace(".", "").replace("-", "").toLowerCase())
											|| s.toLowerCase().contains(vcodec.toLowerCase()))
											{
												comboFonctions.setSelectedItem(s);
												break;
											}
											else
												comboFonctions.setSelectedItem("H.264");
										}
									}
									else
										comboFonctions.setSelectedItem("H.264");
									
									list.clear();
									for (File f : tempFolder.listFiles())
									{
										list.addElement(f.toString());
									}
									
									caseEnableSequence.doClick();
									caseSequenceFPS.setSelectedItem(String.valueOf(FFPROBE.currentFPS));
									
									break;
								}
							}
							else //Move the image colorized from result_images folder
							{
								File tempFile = new File(DEOLDIFY.deoldifyFolder + "/result_images/" + fileName); 								
								if (tempFile.exists())
									tempFile.renameTo(fileOut);	
							}
							
							if (FFMPEG.saveCode == false)
							{
								lastActions(new File(labelOutput));
							}						
						}
						catch (Exception e)
						{
							if (tempFolder != null && tempFolder.exists())
							{
								try {
									FileUtils.deleteDirectory(tempFolder);
								} catch (IOException e1) {}
							}
							
							FFMPEG.error  = true;
							e.printStackTrace();
						}
					}

					endOfFunction();				
				}
			}
			
		});
		thread.start();
    }
	
	private static void lastActions(File fileOut) {
		
		FunctionUtils.cleanFunction(null, fileOut.toString(), fileOut, "");

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
	}
	
}
