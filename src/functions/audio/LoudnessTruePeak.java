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

public class LoudnessTruePeak extends Shutter {
	
	
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
					
					//Analyse des données
					if (analyse(file) == false)
						continue;	
					
					//InOut		
					FFMPEG.fonctionInOut();
					
	            	//Audio
					String audio = setAudio();	
					
					//Envoi de la commande
					String cmd;
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						cmd =  " -vn" + audio + " -f null -";					
					else
						cmd =  " -vn" + audio + " -f null -" + '"';	
					
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

	protected static String setAudio() {
		if (FFPROBE.stereo)
			return " -filter_complex ebur128=peak=true";
	    else if (FFPROBE.channels > 1)	
	    {
	    	if (FFPROBE.channels >= 4)	    		
	    	{
	    		String[] options = {"A1 & A2", "A3 & A4"};
	    		int q = JOptionPane.showOptionDialog(frame, language.getProperty("ChooseMultitrack"), language.getProperty("multitrack"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	    		if (q == 0)
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

	protected static void showDetection(String fichier) {
		if (FFMPEG.analyseLufs != null && Shutter.cancelled == false && FFMPEG.error == false)
		{
			 JOptionPane.showMessageDialog(frame, FFMPEG.analyseLufs, "Loudness & True Peak", JOptionPane.INFORMATION_MESSAGE);
			 int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("saveResult") , Shutter.language.getProperty("analyzeEnded"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			 
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
						writer.println(FFMPEG.analyseLufs + System.lineSeparator() + System.lineSeparator()
									 + FFMPEG.shortTermValues);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e) {}
	
				 }
				
			 }//End Question
			 else
			 {
				 cancelled = false;
			 }
		}	
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
			lblTermine.setText(Utils.completedFiles(complete));
		}
		
		//Envoi par e-mail
		Utils.sendMail(fichier);
		
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			LoudnessTruePeak.main();
			return true;
		}
		return false;
	}
	
}//Class
