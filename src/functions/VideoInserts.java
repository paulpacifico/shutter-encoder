/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import application.Console;
import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class VideoInserts extends Shutter {

	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
				
				int toExtension = liste.firstElement().toString().lastIndexOf('.');
				String extension =  liste.firstElement().substring(toExtension);		
				
				//Liste de fichiers pour le Bout à Bout
				File concatList = new File(""); 

				try {
					
					String[] listeFichiers = new String[liste.getSize()];
					
					//On récupère tous les plans avec leurs tc in et out
					for (int i = 0 ; i < liste.getSize() ; i++)
					{
						//Scanning
						if (Settings.btnWaitFileComplete.isSelected())
			            {
							File file = new File(liste.getElementAt(i));
							
							if (FunctionUtils.waitFileCompleted(file) == false)
								break;
			            }
						
						FFPROBE.Data(liste.getElementAt(i));
						
						do
						{
							Thread.sleep(100);
						}
						while(FFPROBE.isRunning);
						
						listeFichiers[i] = tcInMs() + "=" + '"' + liste.getElementAt(i) + '"' + "=" + (int) (tcInMs() + FFPROBE.totalLength);
					}
																								
					int temps = 0;	
					String fichierMaster = listeFichiers[0];
					//On cherche le fichier Master s'il y a insert au tout début
					for (int i = 0 ; i < liste.getSize() ; i++)
					{
						String fichier[] = listeFichiers[i].split("=");
						FFPROBE.Data(fichier[1].replace("\"", ""));
						
						do {
							Thread.sleep(100);
						} while (FFPROBE.isRunning);	
						
						if (FFPROBE.totalLength > temps)
						{
							fichierMaster = listeFichiers[i];		
							temps = FFPROBE.totalLength;
						}
					}
																			
					//On créé une liste trié par point d'entrée						
					Integer[] array = new Integer [liste.getSize()];
					for (int i = 0 ; i < listeFichiers.length; i++)
					{
						String timeIn[] = listeFichiers[i].split("=");
						array[i] = Integer.parseInt(timeIn[0]);
					}
					
					//Fichier Master
					String[] master = fichierMaster.split("=");
					String masterName = new File(master[1].replace("\"", "")).getName();
					
					//On analyse le fichier Master pour obtenir le timescale
					FFPROBE.FrameData(master[1].replace("\"", ""));					
					do {
						Thread.sleep(100);
					} while (FFPROBE.isRunning);	
					
					//File output name
					String extensionName = "";	
					if (btnExtension.isSelected())
					{
						extensionName = FunctionUtils.setSuffix(txtExtension.getText(), false);
					}
					
					//Output folder		
					String labelOutput = FunctionUtils.setOutputDestination("", new File(master[1].replace("\"", "")));							
					String fileOutputName = labelOutput + "/" + masterName.replace(extension, extensionName + extension);
					
					//Temp. folder
					File temp = new File(labelOutput + "/inserts");	
					temp.mkdir();
					
					//File output
					File fileOut = new File(fileOutputName);
					if(fileOut.exists())
					{
						fileOut = FunctionUtils.fileReplacement(labelOutput, masterName, extension, extensionName + "_", extension);
						
						if (fileOut == null || fileOut.toString().equals("skip"))
						{
							cancelled = true;
						}
					}
					
					//Liste de fichiers pour le Bout à Bout
					concatList = new File(fileOut.toString().replace(extension, ".txt"));	
										
					Arrays.sort(array);
					
					//On tri notre liste de fichier principale grâce à la liste ci-dessus
					String[] listeFichiersSorted = new String[liste.getSize()];
					listeFichiersSorted[0] = fichierMaster;
					int f = 1;
					for (int i = 0 ; i < array.length; i++)
					{
						for (int i2 = 0 ; i2 < listeFichiers.length; i2++)
						{
							String timeIn[] = listeFichiers[i2].split("=");
							if (timeIn[0].equals(array[i].toString()) && listeFichiers[i2].equals(fichierMaster) == false && f < liste.getSize())
							{
								String s[] = listeFichiers[i2].split("=");
								String withTempFolder = '"' + temp.toString() + "/" + new File(s[1].replace("\"", "")).getName() + '"';
								
								if (cancelled == false)
								{
									//Progressbar
									progressBar1.setMaximum((int) ((Integer.parseInt(s[2]) - Integer.parseInt(s[0])) / 1000));
									lblCurrentEncoding.setText(new File(s[1].replace("\"", "")).getName());								
																
									//On intègre le timescale à chaque plan
									FFMPEG.run(" -i " + s[1] + " -video_track_timescale " + FFPROBE.timeBase + " -c copy -map v:0? -map a? -map s? -y "  + withTempFolder);	
									do {
										Thread.sleep(100);
									} while (FFMPEG.isRunning);
								}
								
								listeFichiersSorted[f] = listeFichiers[i2].replace(s[1], withTempFolder);
								f++;
								break;
							}
						}								
					}							
																		
					//Tc de la timeline et de début du master
					int timelineTC = Integer.parseInt(master[0]);
					
					PrintWriter writer = new PrintWriter(concatList, "UTF-8");	
					
					//On créer la liste avec les coupes
					for (int i = 0 ; i < listeFichiersSorted.length; i++)
					{			
						//Rappel : 0 TCIn 1 fichier 2 TCOut
														
						int pointIn;
						if (i == 0) //On Utilise le point In du master 
							pointIn = Integer.parseInt(master[0]);
						else //Puis le point In devient le point Out du fichier suivant
						{
							String coupeIn[] = listeFichiersSorted[i].split("=");
							pointIn = Integer.parseInt(coupeIn[2]);
						}								 
						
						int pointOut = 0;
						if (i < listeFichiersSorted.length - 1) //Le pointOut est le pointIn du plan suivant
						{
							String coupeOut[] = listeFichiersSorted[i + 1].split("=");	
							pointOut = Integer.parseInt(coupeOut[0]);										
						}	
						
						int In = (pointIn - timelineTC);
						int Out;
						if (i == listeFichiersSorted.length - 1) //On ne met pas de point out si c'est le dernier plan
							Out = -1;
						else
							Out = (pointOut - timelineTC);
												
						//IMPORTANT
						fonctionInOutInserts(In, Out);
						
						Console.consoleFFMPEG.append(System.lineSeparator() + listeFichiersSorted[i] + System.lineSeparator());		

						//Si pas de point In ou Out -> Fichier d'insert
						if (In == 0 && Out == 0)
						{
							String insert[] = listeFichiersSorted[i + 1].split("=");									
							writer.println("file " + insert[1].replace("\"", "\'"));
							Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
						}
						else if (Out != -1) //Si l'un ou l'autre -> Master + Insert
						{
							if (In != 0 && Out != 0)
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("inpoint " + InputAndOutput.inPoint);
								writer.println("outpoint " + InputAndOutput.outPoint);
								
								String insert[] = listeFichiersSorted[i + 1].split("=");									
								writer.println("file " + insert[1].replace("\"", "\'"));
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
							}
							else if (In != 0)
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("inpoint " + InputAndOutput.inPoint);
								
								String insert[] = listeFichiersSorted[i + 1].split("=");									
								writer.println("file " + insert[1].replace("\"", "\'"));
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
							}
							else if (Out != 0)
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("outpoint " + InputAndOutput.outPoint);
								
								String insert[] = listeFichiersSorted[i + 1].split("=");									
								writer.println("file " + insert[1].replace("\"", "\'"));
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
							}
						}
						else //Si point Out = Out du fichier
						{ 
							if (In != 0 && In < Integer.parseInt(master[2]) - timelineTC) //Si le point In n'est pas > à la Durée du Master, l'insert reste le dernier Plan
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("inpoint " + InputAndOutput.inPoint);
							}																																													
						}
					}
					writer.close();
						
					String timecode = getTimecode(timelineTC);
					
					//Progressbar
					progressBar1.setMaximum((int) (temps / 1000));
					lblCurrentEncoding.setText(fileOut.getName());
														
					//Command
					if (cancelled == false)
					{						
						String cmd = " -i " + master[1] + timecode + " -c:v copy -c:a copy -c:s copy -map v:0? -map 1:a? -map s? -y ";
						FFMPEG.run(" -safe 0 -f concat -i " + '"' + concatList.toString() + '"' + cmd + '"' + fileOut.toString() + '"');		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
					}
			
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						lastActions(fileOut, concatList, labelOutput, temp);
			
				} catch (InterruptedException | FileNotFoundException | UnsupportedEncodingException e) {					
					FFMPEG.error  = true;
				}
							
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	private static boolean deleteDirectory(File dir) {
		
	    if(! dir.exists() || !dir.isDirectory())    {
	        return false;
	    }

	    String[] files = dir.list();
	    for(int i = 0, len = files.length; i < len; i++)    {
	        File f = new File(dir, files[i]);
	        if(f.isDirectory()) {
	            deleteDirectory(f);
	        }else   {
	            f.delete();
	        }
	    }
	    return dir.delete();
	}
	
	private static int tcInMs() {
		
		int heures = Integer.parseInt(FFPROBE.timecode1);
		int minutes = Integer.parseInt(FFPROBE.timecode2);
		int secondes = Integer.parseInt(FFPROBE.timecode3);
		int images = (int) (Integer.parseInt(FFPROBE.timecode4) * (1000 / FFPROBE.currentFPS));
		
		int totalMiliSecondes = (int) (heures * 3600000) + (minutes * 60000) + (secondes * 1000) + images;  
						
		return totalMiliSecondes;
	}
	
	private static String getTimecode(int timecode) {
		
		int h = timecode / 3600000;
		int m = ((timecode / 60000) % 60);
		int s = (timecode / 1000) % 60;    		
		int f = (int) (timecode / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS);	
				
		NumberFormat formatter = new DecimalFormat("00");
		
		return " -timecode " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + ":" + formatter.format(f);
	}
			
	private static void fonctionInOutInserts(int tcIn, int tcOut) {
		
		NumberFormat formatter = new DecimalFormat("00");
		NumberFormat formatFrame = new DecimalFormat("000");
		
		int h = tcIn / 3600000;
		int m = ((tcIn / 60000) % 60);
		int s = (tcIn / 1000) % 60;    		
		int f = tcIn % 1000;		
		
		//On récupère la durée tcIn - tcOut
		int h2 = (tcOut / 3600000);
		int m2 = ((tcOut / 60000) % 60);
		int s2 = ((tcOut / 1000) % 60);    		
		int f2 = (tcOut % 1000);	

	        
        if (h * 3600000 + m * 60000 + s * 1000 + f > 0)
        	InputAndOutput.inPoint = formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f);
        else
        	InputAndOutput.inPoint = "";

        if (tcOut != -1)
        	InputAndOutput.outPoint = formatter.format(h2) + ":" + formatter.format(m2) + ":" + formatter.format(s2) + "." + formatFrame.format(f2);
        else
        	InputAndOutput.outPoint = "";
	}
	
	private static void lastActions(File fileOut, File listeBAB, String output, File temp) {
	
		//Suppression du dossier temporaire
		deleteDirectory(temp);		
		
		FunctionUtils.cleanFunction(fileOut.toString(), fileOut, output);
		
		listeBAB.delete();
		
		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);			
	}
}
