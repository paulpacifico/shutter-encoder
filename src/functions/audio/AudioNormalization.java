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

package functions.audio;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class AudioNormalization extends Shutter {
	
	
	private static int complete;
	private static int audioTracks;
	
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
					lblEncodageEnCours.setText(Shutter.language.getProperty("analyzing") + " " + fichier);	
					
					//Analyse des données
					if (analyse(file) == false)
						continue;	
					
	            	//filterComplex
					String filterComplex = setFilterComplex();	
					
					//Dossier de sortie
					String sortie = setSortie("", file);

					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
						nomExtension =  "_Norm";
					
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					final String sortieFichier =  sortie + "/" + fichier.replace(extension, nomExtension + extension) ; 
					
					//Audio
					String audio = setAudio(extension);
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", extension);
						if (fileOut == null)
							continue;						
					}
														
					//Envoi de la commande
					String cmd;
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						cmd =  " -vn" + filterComplex + " -f null -";					
					else
						cmd =  " -vn" + filterComplex + " -f null -" + '"';	
					
					FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd);		
					
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					lblEncodageEnCours.setText(fichier);	
					
					if (cancelled == false)
					{
						//Envoi de la commande
						if (FFPROBE.stereo)
							cmd = " -filter_complex volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB -c:v copy -c:s copy" + audio + " -y ";
					    else if (FFPROBE.channels > 1)	
					    {
					    	if (FFPROBE.channels >= 4)	    		
					    	{
								if (audioTracks == 0)
									cmd = " -filter_complex " + '"' + "[0:a:0]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a1];[0:a:1]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a2]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
						    	else
						    		cmd = " -filter_complex " + '"' + "[0:a:2]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a3];[0:a:3]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a4]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map 0:a:0 -map 0:a:1 -map [a3] -map [a4] -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
					    	}
					    	else
					    		cmd = " -filter_complex " + '"' + "[0:a:0]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a1];[0:a:1]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a2]" + '"' + " -c:v copy -c:s copy" + audio.replace("-map a?", "-map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7?") + " -y ";
					    }				

						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"' + fileOut + '"');							
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
						
						if (FFMPEG.error)
						{
							if (FFPROBE.stereo)
								cmd = " -filter_complex volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB -c:v copy -c:s copy -acodec aac -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map a? -map s? -y ";
						    else if (FFPROBE.channels > 1)	
						    {
						    	if (FFPROBE.channels >= 4)	    		
						    	{
									if (audioTracks == 0)
										cmd = " -filter_complex " + '"' + "[0:a:0]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a1];[0:a:1]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a2]" + '"' + " -c:v copy -c:s copy -acodec aac -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
							    	else
							    		cmd = " -filter_complex " + '"' + "[0:a:2]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a3];[0:a:3]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a4]" + '"' + " -c:v copy -c:s copy -acodec aac -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map 0:a:0 -map 0:a:1 -map [a3] -map [a4] -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
						    	}
						    	else
						    		cmd = " -filter_complex " + '"' + "[0:a:0]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a1];[0:a:1]volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB[a2]" + '"' + " -c:v copy -c:s copy -acodec aac -ar " + lbl48k.getText() + " -b:a 320k -map v:0? -map [a1] -map [a2] -map 0:a:2? -map 0:a:3? -map 0:a:4? -map 0:a:5? -map 0:a:6? -map 0:a:7? -map s? -y ";
						    }	
							
							FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"' + fileOut + '"');	
						}
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
					}

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

	protected static boolean analyse(File file) throws InterruptedException {
		 FFPROBE.Data(file.toString());
		 do
			Thread.sleep(100);
		 while (FFPROBE.isRunning);
		 					 
		 if (errorAnalyse(file.toString()))
			return false;
		 
		 return true;
	}

	protected static String setFilterComplex() {
		if (FFPROBE.stereo)
			return " -filter_complex ebur128=peak=true";
	    else if (FFPROBE.channels > 1)	
	    {
	    	if (FFPROBE.channels >= 4)	    		
	    	{
	    		String[] options = {"A1 & A2", "A3 & A4"};
	    		audioTracks = JOptionPane.showOptionDialog(frame, language.getProperty("ChooseMultitrack"), language.getProperty("multitrack"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	    		if (audioTracks == 0)
		    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
		    	else
		    		return " -filter_complex " + '"' + "[0:a:2][0:a:3]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    	}
	    	else
	    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    }
	    else
	    	return " -filter_complex ebur128=peak=true";
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

	protected static String setAudio(String ext) {		
		if (caseChangeAudioCodec.isSelected())
		{
			if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
			{
				switch (comboAudioCodec.getSelectedIndex()) 
				{
					case 0 :
						return " -c:a pcm_f32le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 1 :
						return " -c:a pcm_s32le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 2 :
						return " -c:a pcm_s24le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
					case 3 :
						return " -c:a pcm_s16le -ar " + lbl48k.getText() + " -b:a 1536k -map v:0? -map a? -map s?";
				}
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
			{
				return " -c:a aac -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
			{
				return " -c:a ac3 -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k -map v:0? -map a? -map s?";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("OPUS"))
			{
				return " -c:a libopus -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("OGG"))
			{
				return " -c:a libvorbis -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
		}
		else //Mode Auto
		{
			switch (ext.toLowerCase()) {
				case ".mp4":
					return " -c:a aac -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
				case ".wmv":
					return " -c:a wmav2 -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
				case ".mpg":
					return " -c:a mp2 -ar " + lbl48k.getText() + " -b:a 256k -map v:0? -map a? -map s?";
				case ".ogv":
				case ".av1":
				case ".webm":
					return " -c:a libopus -ar " + lbl48k.getText() + " -b:a 192k -map v:0? -map a? -map s?";
			}
		}
		
		if (FFPROBE.qantization == 24)
			return " -c:a pcm_s24le -map v:0? -map a? -map s?";
		else if (FFPROBE.qantization == 32)
			return " -c:a pcm_s32le -map v:0? -map a? -map s?";
		else
			return " -c:a pcm_s16le -map v:0? -map a? -map s?";
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
			AudioNormalization.main();
			return true;
		}
		return false;
	}
	
}//Class
