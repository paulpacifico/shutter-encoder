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
import java.awt.Cursor;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;

public class EndToEnd extends Shutter {
	
	private static int complete;
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				complete = 0;
				lblTermine.setText(Utils.fichiersTermines(complete));
				
				String sortieFichier;
				
				int toExtension = liste.firstElement().toString().lastIndexOf('.');
				String extension =  liste.firstElement().substring(toExtension);		
				
				FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseFileName"), FileDialog.SAVE);
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
				else
					dialog.setDirectory(System.getProperty("user.home") + "\\Desktop");

				dialog.setVisible(true);
			    
			    if (dialog.getFile() != null)
			    {
						sortieFichier = dialog.getDirectory() + dialog.getFile().toString().replace(extension, "") + extension;
						File listeBAB = new File(dialog.getDirectory() + dialog.getFile().toString() + ".txt");
	
						try {
							
							int dureeTotale = 0;
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
							PrintWriter writer = new PrintWriter(listeBAB, "UTF-8");    
								
							for (int i = 0 ; i < liste.getSize() ; i++)
							{
								//Scanning
								if (Settings.btnWaitFileComplete.isSelected())
					            {
									File file = new File(liste.getElementAt(i));
									
									progressBar1.setIndeterminate(true);
									lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
									lblEncodageEnCours.setText(file.getName());
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
								//Scanning
								
								FFPROBE.Data(liste.getElementAt(i));
								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {}
								} while (FFPROBE.isRunning == true);
								dureeTotale += FFPROBE.dureeTotale;
								
								writer.println("file '" + liste.getElementAt(i) + "'");
							}				
							writer.close();
										
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
																				
							progressBar1.setMaximum((int) (dureeTotale / 1000));
							FFPROBE.dureeTotale = progressBar1.getMaximum();	
							FFMPEG.dureeTotale = progressBar1.getMaximum();
												
							lblEncodageEnCours.setText(Shutter.language.getProperty("babEncoding"));
					
							File fileOut = new File(sortieFichier);							
							
							String openGOP = "";
							if (Settings.btnOpenGOP.isSelected())
								openGOP = " -copyinkf";
							
							//Envoi de la commande
							String cmd = " -timecode 00:00:00:00" + openGOP + " -video_track_timescale 90000 -c copy -map v? -map a? -map s? -y ";
							FFMPEG.run(" -safe 0 -f concat -i " + '"' + listeBAB.toString() + '"' + cmd + '"'  + sortieFichier + '"');		
					
							//Attente de la fin de FFMPEG
							do
								Thread.sleep(100);
							while(FFMPEG.runProcess.isAlive());
														
							listeBAB.delete();							
					
							if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
							{
								actionsDeFin(fileOut);
							}
					
						} catch (InterruptedException | FileNotFoundException | UnsupportedEncodingException e) {
							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							FFMPEG.error  = true;
							if (listeBAB.exists())
								listeBAB.delete();
						}//End Try
				
			}//End dialog
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					FinDeFonction();
			}//run
			
		});
		thread.start();
		
    }//main
	
	private static void actionsDeFin(File fileOut) {
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
		
		//Envoi par e-mail et FTP
		Utils.sendMail(fileOut.toString());
		Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		
	}
}//Class
