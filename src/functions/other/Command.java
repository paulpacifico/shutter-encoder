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
import application.Wetransfer;
import functions.video.DNxHD;
import library.BMXTRANSWRAP;
import library.FFMPEG;

public class Command extends Shutter {
	
	
	private static int complete;
	
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
								Thread.sleep(100);
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
					String fichier = file.getName();
					lblEncodageEnCours.setText(fichier);
					
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					final String sortieFichier =  sortie + "/" + fichier.replace(extension, comboFilter.getEditor().getItem().toString()) ; 		
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);					
					if(fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, "_", comboFilter.getEditor().getItem().toString());
						if (fileOut == null)
							continue;						
					}
					
					//Envoi de la commande
					String cmd;
					if (comboFonctions.getEditor().getItem().toString().contains("-passlogfile")) //Passlogfile
					{
						String[] passlogfile = comboFonctions.getEditor().getItem().toString().substring(comboFonctions.getEditor().getItem().toString().indexOf("-passlogfile") + 13).split("\"");				
						cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("ffmpeg", "").replace("-passlogfile " + '"' + passlogfile[1] + '"', "-passlogfile " + '"' + fileOut + '"') + " -y " ;
					}
					else
						cmd =  " " + comboFonctions.getEditor().getItem().toString().replace("ffmpeg", "") + " -y " ;
					
					FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"'  + fileOut + '"');		
					
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
			        if (cmd.contains("-pass"))
         				FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd.replace("-pass 1", "-pass 2") + '"'  + fileOut + '"');		

					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					//Création des fichiers OPATOM
					if (caseCreateOPATOM.isSelected() && FFMPEG.saveCode == false && cancelled == false)
					{
						lblEncodageEnCours.setText(Shutter.language.getProperty("createOpatomFiles"));
						
						String key = DNxHD.getRandomHexString().toUpperCase();
						BMXTRANSWRAP.run("-t avid -p -o " + '"' + sortie + "/" + fichier.replace(extension, key) + '"' + " --clip " + '"' + fichier.replace(extension, "") + '"' + " " + '"' + fileOut.toString() + '"');
					
						//Attente de la fin de BMXTRANSWRAP
						do
							Thread.sleep(100);
						while(BMXTRANSWRAP.isRunning);
					}
					
					//SUPPRESSION DES FICHIERS RESIDUELS
					final File folder = new File(new File(sortieFichier).getParent());
					listFilesForFolder(fichier.replace(extension, ""), folder);

					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
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
	
	public static void listFilesForFolder(final String fichier, final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	if (fileEntry.getName().contains(fichier) && fileEntry.getName().contains("log"))
	        	{
	        		File fileToDelete = new File(fileEntry.getAbsolutePath());
	        		fileToDelete.delete();
	        	}
	        }
	    }
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
			Command.main();
			return true;
		}
		return false;
	}

}//Class
