/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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
import java.io.IOException;

import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class Extract extends Shutter {
	
	private static int complete;	
	
	private static int subStream = 0;	
	private static boolean extractSubsComplete = false;
	
	private static int audioStream = 0;
	private static boolean extractAudioComplete = false;
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				if (scanIsRunning == false)
					complete = 0;
				
				lblTermine.setText(Utils.completedFiles(complete));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{
					File file = new File(liste.getElementAt(i));
					
					//SCANNING
		            if (Shutter.scanIsRunning)
		            {
		            	file = Utils.scanFolder(liste.getElementAt(i));
		            	if (file != null)
		            		btnStart.setEnabled(true);
		            	else
		            		break;
		            	Shutter.progressBar1.setIndeterminate(false);		
		            }
		            else if (Settings.btnWaitFileComplete.isSelected())
		            {
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
		           //SCANNING
		            
					try {		
					
					FFPROBE.Data(file.toString());

					do {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					} while (FFPROBE.isRunning);
						
					String fichier = file.getName();
					lblEncodageEnCours.setText(fichier);
					
					String mapping = setMapping();
					
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					//Fichier de sortie
					String nomExtension;
					if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
						nomExtension =  "_" + Shutter.language.getProperty("video");
					else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
						nomExtension =  "_" + Shutter.language.getProperty("audio") + "_" + (audioStream + 1);
					else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
						nomExtension =  "_" + Shutter.language.getProperty("subtitles") + "_" + (subStream + 1);
					else						
						nomExtension =  "";

					String extension =  fichier.substring(fichier.lastIndexOf("."));
					String newExtension = extension;
					if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")) && FFPROBE.audioCodec.contains("pcm"))
						newExtension = ".wav";						
					else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
						newExtension = ".srt";						
					
					String sortieFichier =  sortie + "/" + fichier.replace(extension, nomExtension + newExtension); 			

					//Si le fichier existe
					File fileOut = new File(sortieFichier);		
								
					if (fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", newExtension);
						if (fileOut == null)
							continue;	
					}
								
					//Envoi de la commande
					String cmd = " -c copy -c:s mov_text -c:s srt" + mapping + " -y ";
					FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
					
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(10);
					while(FFMPEG.runProcess.isAlive());
					
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false
					|| FFMPEG.saveCode && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
					{
						if (actionsDeFin(fichier, fileOut, sortie))
						break;
					}
					
				} catch (InterruptedException e) {
					FFMPEG.error  = true;
				}//End Try
			}//End For	
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}//run
			
		});
		thread.start();
		
    }//main

	public static void extractAll() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
				comboFilter.setSelectedItem(language.getProperty("video"));
				btnStart.doClick();
				
				FFMPEG.isRunning = true;
				
				if (cancelled == false && FFMPEG.error == false)
				{
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
				}
				
				if (FFPROBE.audioStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("audio"));
					extractAudio();
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while (extractAudioComplete == false);	
				}

				if (FFPROBE.subtitleStreams > 0 && cancelled == false && FFMPEG.error == false)
				{
					comboFilter.setSelectedItem(language.getProperty("subtitles"));
					extractSubs();
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while (extractSubsComplete == false);	
				}
				
				comboFilter.setSelectedItem(language.getProperty("setAll"));				
			}										
		});
		wait.start();	
	}
	
	public static void extractAudio() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
								
				audioStream = 0;
				extractAudioComplete = false;
				
				do {
					Extract.main();		
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
					
					audioStream ++;
					
				} while (audioStream < FFPROBE.audioStreams && cancelled == false && cancelled == false && FFMPEG.error == false);
				
				extractAudioComplete = true;				
			}	
			
		});
		wait.start();
	}
	
	public static void extractSubs() 
	{
		Thread wait = new Thread(new Runnable()
		{
			@Override
			public void run() {
								
				subStream = 0;
				extractSubsComplete = false;
				
				do {
					Extract.main();		
					
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {}
					} while ((FFMPEG.isRunning || btnStart.isEnabled() == false) && cancelled == false && FFMPEG.error == false);
					
					subStream ++;
					
				} while (subStream < FFPROBE.subtitleStreams && cancelled == false && cancelled == false && FFMPEG.error == false);
				
				extractSubsComplete = true;				
			}	
			
		});
		wait.start();
	}
	
	protected static String setMapping() {
		if (comboFilter.getSelectedItem().toString().equals(language.getProperty("video")))
			return " -an -map v?";
		else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("audio")))
			return " -vn -map a:" + audioStream + "?";
		else if (comboFilter.getSelectedItem().toString().equals(language.getProperty("subtitles")))
			return " -vn -an -map s:" + subStream + "?";
		
		return " -map v? -map a? -map s?";
	}

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

	private static boolean actionsDeFin(String fichier, File fileOut, String sortie) {
		//Erreurs
		if (FFMPEG.error || fileOut.length() == 0)
		{
			FFMPEG.errorList.append(fichier);
		    FFMPEG.errorList.append(System.lineSeparator());
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
			return true;
		}

		//Fichiers terminés
		if (cancelled == false && FFMPEG.error == false)
		{
			complete++;
			lblTermine.setText(Utils.completedFiles(complete));
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
		
		//Envoi par e-mail et FTP
		Utils.sendMail(fichier);
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			Extract.main();
			return true;
		}
		return false;
	}
}//Class
