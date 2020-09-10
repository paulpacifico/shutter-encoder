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
import application.VideoPlayer;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class OGG extends Shutter {
	
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
					String fichier = file.getName();
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
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
						nomExtension =  "_MIX";
					
					String sortieFichier;
					if (caseMixAudio.isSelected())						
						sortieFichier =  sortie + "/" + fichier.replace(extension, nomExtension + ".ogg");
					else
						sortieFichier =  sortie + "/" + fichier.replace(extension, ".ogg");		
					
		           	//Audio
					String audio = setAudio("");			    	
					
					//InOut		
					FFMPEG.fonctionInOut();
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists() && caseSplitAudio.isSelected() == false)
					{						
						if (caseMixAudio.isSelected())						
							fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", ".ogg");
						else
							fileOut = Utils.fileReplacement(sortie, fichier, extension, "_", ".ogg");
						if (fileOut == null)
							continue;	
					}
					
					//Mode concat
					String concat = FFMPEG.setConcat(file, sortie);					
					if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						file = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt"));

									
					//Envoi de la commande					
					if (caseSplitAudio.isSelected()) //Permet de créer la boucle de chaque canal audio						
						splitAudio(fichier, extension, file, sortie);
					else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
					{
						String cmd = " " + audio + "-vn -y ";
						FFMPEG.run(FFMPEG.inPoint + concat +
								" -i " + '"' + liste.getElementAt(0) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								" -i " + '"' + liste.getElementAt(1) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								" -i " + '"' + liste.getElementAt(2) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								" -i " + '"' + liste.getElementAt(3) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								" -i " + '"' + liste.getElementAt(4) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								" -i " + '"' + liste.getElementAt(5) + '"' + FFMPEG.postInPoint + FFMPEG.outPoint +
								cmd + '"'  + fileOut + '"');
					}
					else
					{
						String cmd = " " + audio + "-vn -y ";
						FFMPEG.run(FFMPEG.inPoint + concat + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + '"'  + fileOut + '"');
					}	
					
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());					
				
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && caseSplitAudio.isSelected() == false
					|| FFMPEG.saveCode == false && Settings.btnSetBab.isSelected() || FFMPEG.saveCode == false && VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
					{
						if (actionsDeFin(fichier, fileOut, sortie))
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

	private static String setAudio(String audio) {
		
        //Fade-in
		String audioFilter = ""; 		
		if (caseAudioFadeIn.isSelected())
		{ 
			long audioInValue = (long) (Integer.parseInt(spinnerAudioFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
			long audioStart = 0;
			
			if (caseInAndOut.isSelected() && VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
			{
				long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
				
				if (totalIn >= 10000)
					audioStart = 10000;
				else
					audioStart = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
			}

			audioFilter += ",afade=in:st=" + audioStart + "ms:d=" + audioInValue + "ms";
		}
		
		//Fade-out
		if (caseAudioFadeOut.isSelected())
		{
			long audioOutValue = (long) (Integer.parseInt(spinnerAudioFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
			long audioStart = (long) FFPROBE.dureeTotale - audioOutValue;
			
			if (caseInAndOut.isSelected())
			{
				long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
				long totalOut = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
				 
				if (VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
				{
					if (totalIn >= 10000)
						audioStart = 10000 + (totalOut - totalIn) - audioOutValue;
					else
						audioStart = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - audioOutValue;
				}
				else //Remove mode
					audioStart = FFPROBE.dureeTotale - (totalOut - totalIn) - audioOutValue;
			}
			
			audioFilter += ",afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms";
		}
		
    	//Audio Speed
		if (caseConvertAudioFramerate.isSelected())     
        {
        	float AudioFPSIn = Float.parseFloat((comboAudioIn.getSelectedItem().toString()).replace(",", "."));
        	float AudioFPSOut = Float.parseFloat((comboAudioOut.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (AudioFPSOut / AudioFPSIn);
        	audioFilter += ",atempo=" + value;	
        }
		
		if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("stereo")))						
		{
			for (int n = 1 ; n < liste.size() ; n++)
			{
				audio += "-i " + '"' + liste.elementAt(n) + '"' + " ";
			}
			
			if (FFPROBE.stereo)
				audio += "-filter_complex amerge=inputs=" + liste.size() + audioFilter + " -ac 2 ";
			else
			{
				audio += "-filter_complex " + '"';
				String left = "";
				int cl = 0;
				String right = "";
				int cr = 0;
				for (int n = 0 ; n < liste.size() ; n++)
				{
					if (n % 2 == 0) //les chiffres paires à gauche
					{
						left += "[" + n + ":0]";
						cl++;
					}
					else			//les chiffres impaires à droite
					{
						right += "[" + n + ":0]";
						cr++;
					}
				}
				audio += left + "amerge=inputs=" + cl + ",channelmap=map=FL[left];" + right + "amerge=inputs=" + cr + ",channelmap=map=FR[right];";
						
				audio += "[left][right]amerge=inputs=2" + audioFilter + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + " -ac 2 ";
			}							
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals("5.1"))
		{
			audio = "-filter_complex " + '"' + "[0:a][1:a][2:a][3:a][4:a][5:a]join=inputs=6:channel_layout=5.1" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";
		}
		else if (caseMixAudio.isSelected() && lblMix.getText().equals(language.getProperty("mono")))						
		{
			for (int n = 1 ; n < liste.size() ; n++)
			{
				audio += "-i " + '"' + liste.elementAt(n) + '"' + " ";
			}
			
			audio += "-filter_complex amerge=inputs=" + liste.size() + audioFilter + " -ac 1 ";
			
		}
		else if (FFPROBE.stereo)
		{
			audio = "-map a:0 ";
			if (audioFilter != "")     
				audio += audioFilter.replaceFirst(",", " -filter_complex ") + " ";
		}
		else if (FFPROBE.channels > 1)
	    	audio = "-filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " ";	
		else //Fichier Mono
		{
			if (audioFilter != "")     
				audio += audioFilter.replaceFirst(",", " -filter_complex ") + " ";
		}
			
		//Bitrate
		audio += "-acodec libvorbis -b:a "+ comboFilter.getSelectedItem().toString() + "k ";								
		
		//Frequence d'échantillonnage
		if (caseSampleRate.isSelected())
			audio += "-ar " + lbl48k.getText() + " ";
		
		return audio;
	}
	
	private static void splitAudio(String fichier, String extension, File file, String sortie) throws InterruptedException {
		
        //Fade-in
		String audioFilter = ""; 		
		if (caseAudioFadeIn.isSelected())
		{ 
			long audioInValue = (long) (Integer.parseInt(spinnerAudioFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
			long audioStart = 0;
			
			if (caseInAndOut.isSelected() && VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
			{
				long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
				
				if (totalIn >= 10000)
					audioStart = 10000;
				else
					audioStart = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
			}

			audioFilter += ",afade=in:st=" + audioStart + "ms:d=" + audioInValue + "ms";
		}
		
		//Fade-out
		if (caseAudioFadeOut.isSelected())
		{
			long audioOutValue = (long) (Integer.parseInt(spinnerAudioFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
			long audioStart = (long) FFPROBE.dureeTotale - audioOutValue;
			
			if (caseInAndOut.isSelected())
			{
				long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
				long totalOut = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
				 
				if (VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
				{
					if (totalIn >= 10000)
						audioStart = 10000 + (totalOut - totalIn) - audioOutValue;
					else
						audioStart = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - audioOutValue;
				}
				else //Remove mode
					audioStart = FFPROBE.dureeTotale - (totalOut - totalIn) - audioOutValue;
			}
			
			audioFilter += ",afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms";
		}
		
    	//Audio Speed
		if (caseConvertAudioFramerate.isSelected())     
        {
        	float AudioFPSIn = Float.parseFloat((comboAudioIn.getSelectedItem().toString()).replace(",", "."));
        	float AudioFPSOut = Float.parseFloat((comboAudioOut.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (AudioFPSOut / AudioFPSIn);
        	audioFilter += ",atempo=" + value;	
        }
		
		if (FFPROBE.channels == 1 && lblSplit.getText().equals(language.getProperty("mono")))
		{			
			
			for (int i = 1 ; i < 3; i ++)
			{
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(sortie + "/" + fichier.replace(extension, "_Audio_" + i + ".wav"));
				if(fileOut.exists())
				{										
					fileOut = Utils.fileReplacement(sortie, fichier, extension, "_Audio_" + i + "_", ".wav");
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[a:0]pan=1c|c0=c" + (i - 1) + audioFilter + "[a" + (i - 1) + "]" + '"' + " -map " + '"'+ "[a" + (i - 1) + "]" + '"' + " -acodec libvorbis -b:a "+ comboFilter.getSelectedItem().toString() + "k -vn" + yesno;
				FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (actionsDeFin(fichier, fileOut, sortie))
						break;
				}
			}
			
		}
		else if (FFPROBE.channels == 1 && lblSplit.getText().equals(Shutter.language.getProperty("stereo"))) //Si le fichier est stéréo et demandé en stéréo on ne split rien
		{
			JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("theFile") + " " + fichier + " " + Shutter.language.getProperty("isAlreadyStereo"), Shutter.language.getProperty("cantSplitAudio"), JOptionPane.ERROR_MESSAGE);
		}
		else if (FFPROBE.channels > 1 && lblSplit.getText().equals(language.getProperty("mono")))
		{
			for (int i = 1 ; i < FFPROBE.channels + 1; i ++)
			{
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(sortie + "/" + fichier.replace(extension, "_Audio_" + i + ".wav"));
				if(fileOut.exists())
				{										
					fileOut = Utils.fileReplacement(sortie, fichier, extension, "_Audio_" + i + "_", ".wav");
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				if (audioFilter != "")     
					audioFilter = audioFilter.replaceFirst(",", " -filter_complex ");
				
				String cmd = audioFilter + " -map a:" + (i - 1) + " -acodec libvorbis -b:a "+ comboFilter.getSelectedItem().toString() + "k -vn" + yesno;
				FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (actionsDeFin(fichier, fileOut, sortie))
						break;
				}
			}
		}
		else if (FFPROBE.channels > 1 && lblSplit.getText().equals(Shutter.language.getProperty("stereo")))
		{
			
			int number = 1;
			
			for (int i = 1 ; i < FFPROBE.channels + 1; i +=2)
			{
				
				//Si le fichier existe
				String yesno = " -y ";
				File fileOut = new File(sortie + "/" + fichier.replace(extension, "_Audio_" + number + ".wav"));
				if(fileOut.exists())
				{										
					fileOut = Utils.fileReplacement(sortie, fichier, extension, "_Audio_" + number + "_", ".wav");
					if (fileOut == null)
						yesno = " -n ";	
				}
				
				String cmd = " -filter_complex " + '"' + "[0:a:" + (i - 1) + "][0:a:" + i + "]amerge=inputs=2" + audioFilter + "[a]" + '"' + " -map " + '"' + "[a]" + '"' + " -acodec libvorbis -b:a "+ comboFilter.getSelectedItem().toString() + "k -vn" + yesno;
				FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + '"'  + fileOut + '"');	
				
				do
					Thread.sleep(100);
				while(FFMPEG.runProcess.isAlive());	
				
				if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
				{
					if (actionsDeFin(fichier, fileOut, sortie))
						break;
				}
				
				number ++;
			}
		}
		else
		{
			FFMPEG.errorList.append(fichier);
		    FFMPEG.errorList.append(System.lineSeparator());
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
		
		//Mode concat
		if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
		{		
			final String extension =  fichier.substring(fichier.lastIndexOf("."));
			File listeBAB = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt")); 			
			listeBAB.delete();
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
		
		//Bout à bout
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//MixAudio
		if (caseMixAudio.isSelected())
			return true;
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			OGG.main();
			return true;
		}
		return false;
	}

}//Class
