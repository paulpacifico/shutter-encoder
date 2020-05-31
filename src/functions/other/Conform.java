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
import java.io.IOException;
import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;
import library.MKVMERGE;

public class Conform extends Shutter {
	
	
	private static int complete;
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				if (scanIsRunning == false)
					complete = 0;
				
				lblTermine.setText(Utils.fichiersTermines(complete));

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
						btnAnnuler.setEnabled(true);
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
							btnAnnuler.setEnabled(false);
							comboFonctions.setEnabled(true);
							break;
						}
						
						progressBar1.setIndeterminate(false);
						btnAnnuler.setEnabled(false);
		            }
		           //SCANNING
		            
					try {
					String fichier = file.getName();
					lblEncodageEnCours.setText(fichier);
					
					//Analyse des données
					if (analyse(file) == false)
						continue;		
					
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
						nomExtension =  "_Conform";
					
					final String extension =  fichier.substring(fichier.lastIndexOf("."));	
					
					String sortieFichier = sortie + "/" + fichier.replace(extension, nomExtension + extension);
					
		           	//Audio
					String audio = setAudio();						
		           	
					//InOut		
					FFMPEG.fonctionInOut();
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists() && caseSplitAudio.isSelected() == false)
					{											
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", extension);
						if (fileOut == null)
							continue;	
					}
									
					//Envoi de la commande			    		
					File tempMKV = new File(sortie + "/" + fichier.replace(extension, "_Conform.mkv"));

					String cmd = " --default-duration 0:" + comboFilter.getSelectedItem().toString().replace(" i/s", "").replace(",", ".") + "fps -A -S -T -M -B --fix-bitstream-timing-information 0 ";
					MKVMERGE.run(cmd + '"' + file.toString() + '"' + " -o " + '"'  + tempMKV + '"');	
					
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(MKVMERGE.runProcess.isAlive());					
					
					if (tempMKV.exists() && cancelled == false || btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						cmd = " -c:v copy -c:s copy" + audio + " -map v:0 -map 1:a? -map s? -y ";
						FFMPEG.run(" -i " + '"' + tempMKV + '"' + " -i " + '"' + file + '"' + cmd + '"'  + fileOut + '"');						
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
						
						btnStart.setEnabled(true);	
					}
					else if (tempMKV.exists() == false)
						FFMPEG.error = true;

					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						if (actionsDeFin(fichier, tempMKV, fileOut, sortie))
							break;
					}
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}//End Try
				}//End For	

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					FinDeFonction();
			}//run
			
		});
		thread.start();
		
    }//main
		
	private static String setAudio() {
		
    	float AudioFPSIn = FFPROBE.currentFPS;
    	float AudioFPSOut = Float.parseFloat((comboFilter.getSelectedItem().toString().replace(" i/s", "").replace(",", ".")));
    	float value = (float) (AudioFPSOut / AudioFPSIn);
    	if (value < 0.5f || value > 2.0f)
    		return " -an";
    	else
    	{
    		if (FFPROBE.audioCodec != null && FFPROBE.audioCodec != "")
    			return " -c:a " + FFPROBE.audioCodec + " -b:a " + FFPROBE.audioBitrate + "k -af atempo=" + value;	
    		else
    			return " -an";	
    	}
    	
	}
	
	protected static boolean analyse(File file) throws InterruptedException {
		 FFPROBE.Data(file.toString());

		 do
			Thread.sleep(100);
		 while (FFPROBE.isRunning);
		 					 
		 if (errorAnalyse(file.toString()))
			return false;
		 
		 return true;
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
	
	private static boolean errorAnalyse (String fichier)
	{
		 if (FFMPEG.error)
		 {
				FFMPEG.errorList.append(fichier);
			    FFMPEG.errorList.append(System.lineSeparator());
				return true;
		 }
		 return false;
	}
	
	private static boolean actionsDeFin(String fichier, File tempMKV, File fileOut, String sortie) {
		
		//Fichier temporaire
		if (tempMKV.exists())
			tempMKV.delete();		
		
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
		if (cancelled && fileOut.exists())
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
		
		//Envoi par e-mail et FTP
		Utils.sendMail(fichier);
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		
		//MixAudio
		if (caseMixAudio.isSelected())
			return true;
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			Conform.main();
			return true;
		}
		return false;
	}
	
}//Class