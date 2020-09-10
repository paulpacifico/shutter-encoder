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

package functions.video;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import application.Ftp;
import application.VideoPlayer;
import application.OverlayWindow;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class DVPAL extends Shutter {
	
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
		            
					try{
					String fichier = file.getName();
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					lblEncodageEnCours.setText(fichier);
					
					//Analyse des données
					if (analyse(file) == false)
						continue;		
					
					//InOut
					FFMPEG.fonctionInOut();					
					
	            	//Timecode
					String filterComplex = setTimecode(fichier);
					
					//Flags
		    		String flags = setFlags();
					 					 			            
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					String nomExtension;
					if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
						nomExtension = "_DV_TC";
					else		
						nomExtension =  "_DV";
					
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					
					String sortieFichier =  sortie.replace("\\", "/") + "/" + fichier.replace(extension, nomExtension + ".mov").replace("\\", "/"); 
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", ".mov");
						if (fileOut == null)
							continue;							
					}
										
					String output = '"' + fileOut.toString() + '"';
					if (caseVisualiser.isSelected())
						output = "-flags:v +global_header -f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:play" + '"';
								
					//Mode concat
					String concat = FFMPEG.setConcat(file, sortie);					
					if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						file = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt"));
								
					//Envoi de la commande
					String cmd = " -aspect " + comboFilter.getSelectedItem().toString().replace("/", ":") + filterComplex + flags + " -c:a pcm_s16le -map v:0 -map a? -ar 48000 -s 720x576 -vcodec dvvideo -b:v 25000 -r 25 -y ";
					
					FFMPEG.run(FFMPEG.inPoint + concat + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + output);		
					
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());

					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false 
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

	protected static String setTimecode(String fichier) {
		
		String timecodeText = "";
		
		 String tc1 = FFPROBE.timecode1;
		 String tc2 = FFPROBE.timecode2;
		 String tc3 = FFPROBE.timecode3;
		 String tc4 = FFPROBE.timecode4;
		 
		if (OverlayWindow.caseAddTimecode.isSelected())
		{
			 tc1 = OverlayWindow.TC1.getText();
			 tc2 = OverlayWindow.TC2.getText();			
			 tc3 = OverlayWindow.TC3.getText();		    
			 tc4 = OverlayWindow.TC4.getText();
		}

		if (caseInAndOut.isSelected() && VideoPlayer.sliderIn.getValue() > VideoPlayer.sliderIn.getMinimum() && OverlayWindow.caseAddTimecode.isSelected())
		{
			 tc1 = String.valueOf(Integer.parseInt(tc1) - Integer.parseInt(VideoPlayer.caseInH.getText()));
	         tc2 = String.valueOf(Integer.parseInt(tc2) - Integer.parseInt(VideoPlayer.caseInM.getText()));
	         tc3 = String.valueOf(Integer.parseInt(tc3) - Integer.parseInt(VideoPlayer.caseInS.getText()));
	         tc4 = String.valueOf(Integer.parseInt(tc4) - Integer.parseInt(VideoPlayer.caseInF.getText()));
		}
		
		String rate = String.valueOf(FFPROBE.currentFPS);
		if (caseConform.isSelected())
			rate = comboFPS.getSelectedItem().toString().replace(",", ".");
     
       	if (OverlayWindow.caseShowFileName.isSelected() && caseAddOverlay.isSelected())
       	{
       		if (timecodeText != "") timecodeText += ",";
       		timecodeText += "drawtext=" + OverlayWindow.font + ":text='" + fichier + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
       	if (OverlayWindow.caseShowText.isSelected() && caseAddOverlay.isSelected())
       	{
       		if (timecodeText != "") timecodeText += ",";
       		timecodeText += "drawtext=" + OverlayWindow.font + ":text='" + OverlayWindow.text.getText() + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
	   	if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
	   	{	   			
	   		if (timecodeText != "") timecodeText += ",";
	   		timecodeText += "drawtext=" + OverlayWindow.font + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\:" + tc4 + "':r=" + rate + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc + ":tc24hmax=1";	      
	   	} 
	   	
	   	if (timecodeText != "") 
	   		timecodeText = " -filter_complex " + '"' + timecodeText + '"';
	   
		return timecodeText;
	}
	
	protected static String setFlags() { 
		
		return " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
	}
	
	protected static boolean analyse(File file) throws InterruptedException {
		 FFPROBE.FrameData(file.toString());	
		 do
		 	Thread.sleep(100);						 
		 while (FFPROBE.isRunning);
		 
		 if (errorAnalyse(file.toString()))
			 return false;
		 						 					 
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
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			DVPAL.main();
			return true;
		}
		return false;
	}
	
}//Class
