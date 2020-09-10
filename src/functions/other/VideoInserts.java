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

package functions.other;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.JOptionPane;

import application.Console;
import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class VideoInserts extends Shutter {
	
	private static int complete;
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				complete = 0;
				lblTermine.setText(Utils.fichiersTermines(complete));
				
				int toExtension = liste.firstElement().toString().lastIndexOf('.');
				String extension =  liste.firstElement().substring(toExtension);		
				
				//Liste de fichiers pour le Bout à Bout
				File listeBAB = new File(""); 

				try {
					
					String[] listeFichiers = new String[liste.getSize()];
					
					//On récupère tous les plans avec leurs tc in et out
					for (int i = 0 ; i < liste.getSize() ; i++)
					{
						//Scanning
						if (Settings.btnWaitFileComplete.isSelected())
			            {
							File file = new File(liste.getElementAt(i));
							
							progressBar1.setIndeterminate(true);
							lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
							lblEncodageEnCours.setText(file.getName());
							tempsRestant.setVisible(false);
							btnStart.setEnabled(false);
							btnCancel.setEnabled(true);
							comboFonctions.setEnabled(false);
							
							long fileSize = 0;
							do {
								fileSize = file.length();
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {} // Permet d'attendre la nouvelle valeur de la copie
							} while (fileSize != file.length() && cancelled == false);

							// pour Windows
							while (file.renameTo(file) == false && cancelled == false) {
								if (file.exists() == false) // Dans le cas où on annule la copie en cours
									break;
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
								}
							}
							
							if (cancelled)
							{
								progressBar1.setIndeterminate(false);
								lblEncodageEnCours.setText(language.getProperty("lblEncodageEnCours"));
								btnStart.setEnabled(true);
								btnCancel.setEnabled(false);
								comboFonctions.setEnabled(true);
								break;
							}
							
							progressBar1.setIndeterminate(false);
							btnCancel.setEnabled(false);
			            }
						//Scanning
						
						FFPROBE.Data(liste.getElementAt(i));
						
						//Attente de la fin de FFPROBE
						do
							Thread.sleep(100);
						while(FFPROBE.isRunning);
						
						listeFichiers[i] = tcInMs() + "=" + '"' + liste.getElementAt(i) + '"' + "=" + (int) (tcInMs() + FFPROBE.dureeTotale);
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
						
						if (FFPROBE.dureeTotale > temps)
						{
							fichierMaster = listeFichiers[i];		
							temps = FFPROBE.dureeTotale;
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
					
					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
						nomExtension =  "_Edit";
					
					//Dossier de sortie
					String sortie = setSortie("", new File(master[1].replace("\"", "")));							
					String sortieFichier = sortie + "/" + masterName.replace(extension, nomExtension + extension);
					
					//Dossier Temporaire
					File temp = new File(sortie + "/inserts");	
					temp.mkdir();
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{
						fileOut = Utils.fileReplacement(sortie, masterName, extension, nomExtension + "_", extension);
						if (fileOut == null)
							cancelled = true;	
					}
					
					//Liste de fichiers pour le Bout à Bout
					listeBAB = new File(fileOut.toString().replace(extension, ".txt"));	
										
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
									lblEncodageEnCours.setText(new File(s[1].replace("\"", "")).getName());								
																
									//On intègre le timescale à chaque plan
									FFMPEG.run(" -i " + s[1] + " -video_track_timescale " + FFPROBE.timeBase + " -c copy -map v? -map a? -map s? -y "  + withTempFolder);	
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
					
					PrintWriter writer = new PrintWriter(listeBAB, "UTF-8");	
					
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
								
								writer.println("inpoint " + FFMPEG.inPoint);
								writer.println("outpoint " + FFMPEG.outPoint);
								
								String insert[] = listeFichiersSorted[i + 1].split("=");									
								writer.println("file " + insert[1].replace("\"", "\'"));
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
							}
							else if (In != 0)
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("inpoint " + FFMPEG.inPoint);
								
								String insert[] = listeFichiersSorted[i + 1].split("=");									
								writer.println("file " + insert[1].replace("\"", "\'"));
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + insert[1].replace("\"", "\'"));	
							}
							else if (Out != 0)
							{
								writer.println("file " + master[1].replace("\"", "\'"));	
								Console.consoleFFMPEG.append(System.lineSeparator() + "file " + master[1].replace("\"", "\'"));	
								
								writer.println("outpoint " + FFMPEG.outPoint);
								
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
								
								writer.println("inpoint " + FFMPEG.inPoint);
							}																																													
						}
					}
					writer.close();
						
					String timecode = getTimecode(timelineTC);
					
					//Progressbar
					progressBar1.setMaximum((int) (temps / 1000));
					lblEncodageEnCours.setText(fileOut.getName());
														
					//Envoi de la commande
					if (cancelled == false)
					{						
						String cmd = " -i " + master[1] + timecode + " -c:v copy -c:a copy -c:s copy -map v? -map 1:a? -map s? -y ";
						FFMPEG.run(" -safe 0 -f concat -i " + '"' + listeBAB.toString() + '"' + cmd + '"' + fileOut.toString() + '"');		
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
					}
			
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						actionsDeFin(fileOut, listeBAB, sortie, temp);
			
				} catch (InterruptedException | FileNotFoundException | UnsupportedEncodingException e) {
					FFMPEG.error  = true;
					if (listeBAB.exists())
						listeBAB.delete();
				}//End Try
							
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					FinDeFonction();
			}//run
			
		});
		thread.start();
		
    }//main

	protected static String setSortie(String sortie, File file) {
		if (caseChangeFolder1.isSelected())
			sortie = lblDestination1.getText();
		else
		{
			sortie =  file.getParent();
			lblDestination1.setText(sortie);
		}
		
		return sortie;
	}
	
	public static boolean deleteDirectory(File dir) {
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
        	FFMPEG.inPoint = formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f);
        else
        	FFMPEG.inPoint = "";

        if (tcOut != -1)
        	FFMPEG.outPoint = formatter.format(h2) + ":" + formatter.format(m2) + ":" + formatter.format(s2) + "." + formatFrame.format(f2);
        else
        	FFMPEG.outPoint = "";
	}
	
	private static void actionsDeFin(File fileOut, File listeBAB, String sortie, File temp) {
		//Suppression du dossier temporaire
		deleteDirectory(temp);		
		
		//Erreurs
		if (FFMPEG.error || fileOut.length() == 0)
		{
			JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("babImpossible"), Shutter.language.getProperty("encodingError"), JOptionPane.ERROR_MESSAGE);
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}

		//Annulation
		if (cancelled)
		{
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}
		else
		{
			if(FFMPEG.error == false)
				complete++;
			lblTermine.setText(Utils.fichiersTermines(complete));
		}
		
		//Ouverture du dossier
		if (caseOpenFolderAtEnd1.isSelected() && cancelled == false && FFMPEG.error == false)
		{
			try {
				Desktop.getDesktop().open(new File(sortie));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		listeBAB.delete();
		
		//Envoi par e-mail et FTP
		Utils.sendMail(fileOut.toString());
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);		
		
	}
}//Class
