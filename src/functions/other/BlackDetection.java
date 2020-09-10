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
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import application.Settings;
import application.Shutter;
import application.Utils;
import library.FFMPEG;
import library.FFPROBE;

public class BlackDetection extends Shutter {
	
	
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
					lblEncodageEnCours.setText(fichier);		
					
		            
					//Analyse des données
					if (analyse(file) == false)
						continue;		         
					
					//InOut		
					FFMPEG.fonctionInOut();
					
					//Envoi de la commande
					String cmd;
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						cmd =  " -an -vf blackdetect=d=0.0:pix_th=.1 -f null -";					
					else
						cmd =  " -an -vf blackdetect=d=0.0:pix_th=.1 -f null -" + '"';	
					
					FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + FFMPEG.postInPoint + FFMPEG.outPoint + cmd);		
					
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					//On Affiche la détection
					if (cancelled == false)
						showDetection(fichier);
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						if (actionsDeFin(fichier))
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
	
	protected static void showDetection(String fichier) {
		if (FFMPEG.blackFrame.length() > 0 && Shutter.cancelled == false && FFMPEG.error == false)
		{
			 JOptionPane.showMessageDialog(frame, FFMPEG.blackFrame, Shutter.language.getProperty("functionBlackDetection"), JOptionPane.ERROR_MESSAGE);
			 int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("saveResult"), Shutter.language.getProperty("analyzeEnded"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			 
			 if (q == JOptionPane.YES_OPTION)
				{
					FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveResult"), FileDialog.SAVE);
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
					dialog.setVisible(true);

					 if (dialog.getFile() != null)
					 { 
						try {
							PrintWriter writer = new PrintWriter(dialog.getDirectory() + dialog.getFile().replace(".txt", "") + ".txt", "UTF-8");
							writer.println(Shutter.language.getProperty("analyzeOf") + " " + fichier);
							writer.println("");
							writer.println(FFMPEG.blackFrame);
							writer.close();
						} catch (FileNotFoundException | UnsupportedEncodingException e) {}

					 }
					
				}//End Question
			 else
			 {
				 cancelled = false;
			 }
		}
		else
			JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("noErrorDetected"), Shutter.language.getProperty("functionBlackDetection"), JOptionPane.INFORMATION_MESSAGE);
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
	
	private static boolean actionsDeFin(String fichier) {		
		//Erreurs
		if (FFMPEG.error)
		{
			FFMPEG.errorList.append(fichier);
		    FFMPEG.errorList.append(System.lineSeparator());
		}

		//Annulation
		if (cancelled)
		{
			return true;
		}
		else
		{
			if(FFMPEG.error == false)
				complete++;
			lblTermine.setText(Utils.fichiersTermines(complete));
		}
		
		//Envoi par e-mail
		Utils.sendMail(fichier);
		
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			BlackDetection.main();
			return true;
		}
		return false;
	}
}//Class
