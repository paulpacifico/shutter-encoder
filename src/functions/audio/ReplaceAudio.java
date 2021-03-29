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

public class ReplaceAudio extends Shutter {
	
	private static int complete;

	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				String audioFiles;	
				String audioExt = "";
				File videoFile;
				complete = 0;	
				lblTermine.setText(Utils.completedFiles(complete));				

			try {
					
				if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio")) == false)
				{
					//Analyse du stream		
					if (FFPROBE.FindStreams(liste.getElementAt(1)))
					{
						videoFile = new File(liste.getElementAt(1));
						audioFiles = " -i " + '"' + liste.getElementAt(0)  + '"';
						audioExt = liste.getElementAt(0).substring(liste.getElementAt(0).lastIndexOf("."));
						FFPROBE.FindStreams(liste.getElementAt(0));
					}
					else
					{
						videoFile = new File(liste.getElementAt(0));		
						audioFiles = " -i " + '"' + liste.getElementAt(1)  + '"';
						audioExt = liste.getElementAt(1).substring(liste.getElementAt(1).lastIndexOf("."));
					}			
					
					float offset = 0;
					
					if (caseAudioOffset.isSelected() || caseInAndOut.isSelected())
					{
						FFPROBE.Data(videoFile.toString());
						
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							}
						} while (FFPROBE.isRunning);
						
						if (caseAudioOffset.isSelected())
							offset = (float) ((float) Integer.parseInt(txtAudioOffset.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000;							
						else
							offset = (float) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText()) + ((float) Integer.parseInt(VideoPlayer.caseInF.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000);
						
						audioFiles = " -itsoffset " + offset + audioFiles;
					}
					
					audioFiles += " -map 0:v -map 1:a";
					
					if (liste.getSize() > 2)
						audioFiles = setMulipleAudioFiles(videoFile, "", offset);
													
					do {
						Thread.sleep(100);
					} while (FFPROBE.isRunning);	
				}
				else
				{
					videoFile = new File(liste.getElementAt(0));
					audioFiles = " -map v:0?";
				}				
					String fichier = videoFile.getName();					
					lblEncodageEnCours.setText(videoFile.getName());			
					
					//InOut	
					FFMPEG.fonctionInOut();	
					
					//Dossier de sortie
					String sortie = setSortie("", videoFile);	
					
					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
						nomExtension =  "_MIX";

					final String ext =  fichier.substring(fichier.lastIndexOf("."));
					final String sortieFichier =  sortie + "/" + fichier.replace(ext, nomExtension + ext) ; 		
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
						fileOut = Utils.fileReplacement(sortie, fichier, ext, nomExtension + "_", ext);
							
					String audio = setAudio(ext, audioExt);
					String shortest = " -shortest";
					if (comboFilter.getSelectedItem().toString().equals(language.getProperty("longest")))
						shortest = "";
										
					//Envoi de la commande					
					String cmd = shortest + " -c:v copy -c:s copy" + audio + " -map s? -y ";
					FFMPEG.run(" -i " + '"' + videoFile.toString() + '"' + audioFiles + FFMPEG.outPoint + cmd + '"'  + fileOut + '"');		
							
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						actionsDeFin(fichier, fileOut, sortie);
					}
					
				} catch (InterruptedException e) {
					FFMPEG.error  = true;
				}//End Try
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}//run
			
		});
		thread.start();
		
    }//main

	protected static String setMulipleAudioFiles(File videoFile, String audioFiles, Float offset) {
		for (int i = 0 ; i < liste.getSize() ; i++)
		{
			if (liste.getElementAt(i).equals(" -f lavfi -i anullsrc=r=" + lbl48k.getText() + ":cl=mono")) //Si le fichier est une piste muette
				audioFiles += liste.getElementAt(i) ;
			else if (liste.getElementAt(i).equals(videoFile.toString()) == false) //Si le fichier n'est pas le fichier vidéo
			{
				if (caseAudioOffset.isSelected())
					audioFiles += " -itsoffset " + offset + " -i " + '"' + liste.getElementAt(i)  + '"';
				else if (caseInAndOut.isSelected())
				{
					offset = (float) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayer.caseInS.getText()) + ((float) Integer.parseInt(VideoPlayer.caseInF.getText()) * ((float) 1000 / FFPROBE.currentFPS)) / 1000);
					audioFiles += " -itsoffset " + offset + " -i " + '"' + liste.getElementAt(i)  + '"';
				}
				else
					audioFiles += " -i " + '"' + liste.getElementAt(i)  + '"';
			}
	 	}
							
		audioFiles += " -map 0:v";
		
		for (int i = 1 ; i < liste.getSize() ; i++)
		{
			audioFiles +=  " -map " + i + ":a";
		}
		return audioFiles;
	}

	protected static String setAudio(String ext, String audioExt) {
		
		if (caseChangeAudioCodec.isSelected())
		{
			if (comboAudioCodec.getSelectedItem().toString().contains("PCM"))
			{
				switch (comboAudioCodec.getSelectedIndex()) 
				{
					case 0 :
						return " -c:a pcm_f32le -ar " + lbl48k.getText() + " -b:a 1536k";
					case 1 :
						return " -c:a pcm_s32le -ar " + lbl48k.getText() + " -b:a 1536k";
					case 2 :
						return " -c:a pcm_s24le -ar " + lbl48k.getText() + " -b:a 1536k";
					case 3 :
						return " -c:a pcm_s16le -ar " + lbl48k.getText() + " -b:a 1536k";
				}
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AAC"))
			{
				return " -c:a aac -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("AC3"))
			{
				return " -c:a ac3 -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("OPUS"))
			{
				return " -c:a libopus -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("OGG"))
			{
				return " -c:a libvorbis -ar " + lbl48k.getText() + " -b:a " + comboAudioBitrate.getSelectedItem().toString() + "k";
			}
			else //Sans audio
			{
				return " -an";
			}
		}
		else //Mode Auto
		{
			switch (ext.toLowerCase()) {
				case ".mp4":
				if (audioExt.equals(".m4a") == false)
					return " -c:a aac -ar " + lbl48k.getText() + " -b:a 256k";
				else
					return  " -c:a copy";
				case ".wmv":
				if (audioExt.equals(".wma") == false)
					return " -c:a wmav2 -ar " + lbl48k.getText() + " -b:a 256k";
				else
					return  " -c:a copy";
				case ".mpg":
				if (audioExt.equals(".mp2") == false)
					return " -c:a mp2 -ar " + lbl48k.getText() + " -b:a 256k";
				else
					return  " -c:a copy";
				case ".ogv":
				case ".av1":
				case ".webm":
					return " -c:a libopus -ar " + lbl48k.getText() + " -b:a 192k -map v:0? -map a? -map s?";
			}
		}
		
		return  " -c:a copy";
	}
	
	protected static String setSortie(String sortie, File videoFile) {
		if (caseChangeFolder1.isSelected())
			sortie = lblDestination1.getText();
		else
		{
			sortie =  videoFile.getParent();
			lblDestination1.setText(sortie);
		}		
		return sortie;
	}

	private static void actionsDeFin(String fichier, File fileOut, String sortie) {
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
		
	}

}//Class
