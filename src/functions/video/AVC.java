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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JComboBox;

import application.Ftp;
import application.VideoPlayer;
import application.WatermarkWindow;
import application.Settings;
import application.Shutter;
import application.SubtitlesWindow;
import application.Utils;
import application.Wetransfer;
import library.BMXTRANSWRAP;
import library.FFMPEG;
import library.FFPROBE;

public class AVC extends Shutter {
	
	
	private static int complete;
	private static String silentTrack = "";
	
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
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					lblEncodageEnCours.setText(fichier);
					
					//Analyse des données
					if (analyse(file) == false)
						continue;		 				
					
					String concat = "";
					//Traitement de la file en Bout à bout
					if (Settings.btnSetBab.isSelected())
					{
						file = setBAB(fichier, extension);	
						if (caseActiverSequence.isSelected() == false)
						concat = " -safe 0 -f concat";
					}
					
					//Subtitles
					String subtitles = setSubtitles();
										
					 //Interlace
		            String forceField = setInterlace();	
					
			        //Deinterlace
					String filterComplex = setDeinterlace();	
					
					//Blend
					filterComplex = setBlend(filterComplex);
					
					//MotionBlur
					filterComplex = setMotionBlur(filterComplex);
					
					//LUTs
					filterComplex = setLUT(filterComplex);
					
					//Color
					filterComplex = setColor(filterComplex);
					
					//Interpolation
					filterComplex = setInterpolation(filterComplex);
					
					//Ralenti
					filterComplex = setSlowMotion(filterComplex);
					
			        //PTS
					filterComplex = setPTS(filterComplex);		      		                     	
						       
					//Deband
					filterComplex = setDeband(filterComplex);
							   
					//Détails
	            	filterComplex = setDetails(filterComplex);				
					
		            //Flags
		    		String flags = setFlags();
	            	
					//Bruit
		    		filterComplex = setDenoiser(filterComplex);
		    		
					//Conformisation
		    		filterComplex = setConform(filterComplex);
		    						
			        //Framerate
					String frameRate = setFramerate();
					
					//Limiter
					filterComplex = setLimiter(filterComplex);
		            
					//Logo
			        String logo = setLogo();
					
	            	//Watermak
			        filterComplex = setWatermark(filterComplex);
			        
			    	//Rognage
			        filterComplex = setCrop(filterComplex);
					
					//Rotate
					filterComplex = setRotate(filterComplex);
					
			        //DAR
					filterComplex = setDAR(filterComplex);
					
					//Padding
			        filterComplex = setPad(filterComplex);
			        
					//Interlace50p
					filterComplex = setInterlace50p(filterComplex);	
					
					//Overlay
					filterComplex = setOverlay(filterComplex);		        
					
					//Audio
					String audio = setAudio();	 
					
					//InOut	
					FFMPEG.fonctionInOut();	
					
					//Séquence d'images
					String sequence = setSequence(file, extension);
					
					file = setSequenceName(file, extension);
					
					//Loop image					
					String loop = setLoop(extension);
					
					//Mapping des pistes audios et Filtercomplex
					filterComplex = setFilterComplex(filterComplex, audio);
					
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
						nomExtension =  "_AVC";
					
	            	//Timecode
					String timecode = setTimecode();

					final String sortieFichier =  sortie.replace("\\", "/") + "/" + fichier.replace(extension, nomExtension + ".mxf") ; 				
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", ".mxf");
						if (fileOut == null)
							continue;						
					}
					
					String output = '"' + fileOut.toString() + '"';
					if (caseVisualiser.isSelected())						
						output = "-f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=mxf]pipe:play" + '"';
									
					//Envoi de la commande
					String cmd = silentTrack + frameRate + filterComplex + " -shortest -c:v libx264 -coder 0 -g 1 -b:v 100M -tune psnr -preset veryslow -vsync 1 -color_range 2 -avcintra-class 100 -color_primaries bt709 -color_trc bt709 -colorspace bt709 -me_method hex -subq 5 -cmp chroma -pix_fmt yuv422p10le -f mxf" + forceField + timecode + flags + " -y ";
					FFMPEG.run(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + output);		
					
					//Attente de la fin de FFMPEG
					do
						Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					//Création des fichiers OPATOM
					if (caseAS10.isSelected() && FFMPEG.saveCode == false && cancelled == false && FFMPEG.error == false)
					{
						lblEncodageEnCours.setText(Shutter.language.getProperty("createAS10Format").replace("10", "11"));

						BMXTRANSWRAP.run("-t as11op1a -p -o " + '"' + sortie + "/" + fichier.replace(extension, "_AS11.mxf") + '"' + " " + '"' + fileOut.toString() + '"');
					
						//Attente de la fin de BMXTRANSWRAP
						do
							Thread.sleep(100);
						while(BMXTRANSWRAP.isRunning);
					}

					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false 
					|| FFMPEG.saveCode == false && Settings.btnSetBab.isSelected())
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

	protected static File setBAB(String fichier, String extension) {
		
		String sortie =  new File(liste.getElementAt(0)).getParent();
		
		if (caseChangeFolder1.isSelected())
			sortie = lblDestination1.getText();
			
		File listeBAB = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt")); 
		
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
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			FFMPEG.error  = true;
			if (listeBAB.exists())
				listeBAB.delete();
		}//End Try
		
		return listeBAB;
	}

	protected static String setLogo() {
		if (caseLogo.isSelected())	        	
			return " -i " + '"' + WatermarkWindow.logoFile + '"'; 
		else
			return "";

	}
	
	protected static String setWatermark(String filterComplex) {
        if (caseLogo.isSelected())
        {		        	        	
        	if (filterComplex != "") 	
        	{
            	filterComplex = "[0:v]" + filterComplex + "[v];[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        	else
        	{
            	filterComplex = "[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        }
        
		return filterComplex;        
	}
	
	protected static String setAudio() {
		
		//Pas d'audio
		if (comboAudio1.getSelectedIndex() == 8
			&& comboAudio2.getSelectedIndex() == 8
			&& comboAudio3.getSelectedIndex() == 8
			&& comboAudio4.getSelectedIndex() == 8
			&& comboAudio5.getSelectedIndex() == 8
			&& comboAudio6.getSelectedIndex() == 8
			&& comboAudio7.getSelectedIndex() == 8
			&& comboAudio8.getSelectedIndex() == 8)
		{
			return " -an";
		}
		else
		{			
			String audio = " -c:a pcm_s24le";
			
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))     
	        {
	        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
	        	float value = (float) (newFPS/ FFPROBE.currentFPS);
	        	if (value < 0.5f || value > 2.0f)
	        		return " -an";
	        	else
	        		audio += " -filter:a " + ("atempo=" + value);	
	        }
				
	        if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
	        	return " -an";
	        
			return audio + " -ar 48000";
		}
	}

	@SuppressWarnings("rawtypes")
	protected static String setFilterComplex(String filterComplex, String audio) {
		
		String mapping = "";
		
		//Pas d'audio
		if (comboAudio1.getSelectedIndex() == 8
			&& comboAudio2.getSelectedIndex() == 8
			&& comboAudio3.getSelectedIndex() == 8
			&& comboAudio4.getSelectedIndex() == 8
			&& comboAudio5.getSelectedIndex() == 8
			&& comboAudio6.getSelectedIndex() == 8
			&& comboAudio7.getSelectedIndex() == 8
			&& comboAudio8.getSelectedIndex() == 8)
		{
			if (caseLogo.isSelected() || caseSubtitles.isSelected())
				mapping += " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			else if (filterComplex != "")
				mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			else
				mapping += " -map v" + audio;
			
			return mapping;
		}
		else
		{
			int channels = 0;
			for (Component c : grpSetAudio.getComponents())
			{
				if (c instanceof JComboBox)
				{
					if (((JComboBox) c).getSelectedIndex() != 8)
						channels ++;
				}
			}
			
			for (int m = 1 ; m < channels + 1; m++) 
			{	
				//On map les pistes existantes
				if (m <= FFPROBE.channels)
				{ 
					if (FFPROBE.channels == 1) //Si le son est stereo alors on split
					{
						if (caseLogo.isSelected() || caseSubtitles.isSelected())
							mapping += " -filter_complex " + '"' + filterComplex + "[out];[0:a]channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						else if (filterComplex != "")
							mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out];[0:a]channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						else
							mapping += " -map v -filter_complex [0:a]channelsplit[a1][a2] -map [a1] -map [a2]" + audio;
						m ++;
					}
					else
					{
						int i = 1;
						int map = m;
						for (Component c : grpSetAudio.getComponents())
						{
							if (c instanceof JComboBox)
							{								
								if (i == m)
								{
									map = (((JComboBox) c).getSelectedIndex() + 1);	
									break;
								}
								i++;
							}						
						}
						
						mapping += " -map 0:" + map;
					}
				}
				else //On ajoute une piste silencieuse
				{
					silentTrack = " -f lavfi -i anullsrc=r=48000:cl=mono";
					if (caseLogo.isSelected() && caseSubtitles.isSelected())
						mapping += " -map 3";	
					else if (caseLogo.isSelected() || caseSubtitles.isSelected())
						mapping += " -map 2";
					else
						mapping += " -map 1";	
				}
			}
		}			
		
		if (FFPROBE.channels != 1) //On ajoute le filterComplex lorsque il n'y a pas de split des pistes son	
		{
			if (caseLogo.isSelected() || caseSubtitles.isSelected())
				mapping = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + mapping + audio;
			else if (filterComplex != "")
				mapping = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + mapping + audio;
			else
				mapping = " -map v" + mapping + audio;	
		}		
		
		return mapping;
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
	
	protected static File setSequenceName(File file, String extension) {
		if (caseActiverSequence.isSelected())
		{
			int n = 0;
			do {
				n ++;
			} while (file.toString().substring(file.toString().lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
			
			int nombre = (n - 1);
			file = new File(file.toString().substring(0, file.toString().lastIndexOf(".") - nombre) + "%0" + nombre + "d" + extension);				
		}
		return file;
	}

	protected static String setSequence(File file, String extension) {
		if (caseActiverSequence.isSelected())
		{
			int n = 0;
			do {
				n ++;				
			} while (file.toString().substring(file.toString().lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
					
			if (caseBlend.isSelected())
				return " -start_number " + file.toString().substring(file.toString().lastIndexOf(".") - n + 1).replace(extension, "");	
			else	
				return " -framerate " + caseSequenceFPS.getText() + " -start_number " + file.toString().substring(file.toString().lastIndexOf(".") - n + 1).replace(extension, "");		
		}
		
		return "";
	}
	
	protected static String setCrop(String filterComplex) {		
		
		if (caseRognerImage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "," + Shutter.cropFinal;
			else
				filterComplex = Shutter.cropFinal;
		}
    	
    	return filterComplex;
	}
	
	protected static String setPad(String filterComplex) {	
		
		String s[] = lblResolution.getSelectedItem().toString().split("x");
		
		if (caseForcerResolution.isSelected())
		{			
			if (filterComplex != "") filterComplex += "[c];[c]";
			
			if (lblPad.getText().equals(language.getProperty("lblPad")))
					filterComplex += "scale="+s[0]+":"+s[1]+":force_original_aspect_ratio=decrease,pad=" +s[0]+":"+s[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
			else if (lblPad.getText().equals(language.getProperty("lblStretch")))
					filterComplex += "scale="+s[0]+":"+s[1];				
			else 
			{
				String i[] = FFPROBE.imageResolution.split("x");        	
	
	        	int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);          	
	        	int ow = Integer.parseInt(s[0]);
	        	int oh = Integer.parseInt(s[1]);        	
	        	float ir = (float) iw / ih;
	        	
	        	//Original sup. à la sortie
	        	if (iw > ow || ih > oh)
	        	{
	        		//Si la hauteur calculée est > à la hauteur de sortie
	        		if ( (float) ow / ir >= oh)
	        			filterComplex += "scale=" + ow + ":-1,crop=" + "'" + ow + ":" + oh + ":0:(ih-oh)*0.5" + "'";
	        		else
	        			filterComplex += "scale=-1:" + oh + ",crop=" + "'" + ow + ":" + oh + ":(iw-ow)*0.5:0" + "'";
	        	}
	        	else
	        		filterComplex += "scale=" + ow + ":" + oh;
			}			
		}
		else
		{
			if (filterComplex != "") filterComplex += "[c];[c]";
				filterComplex += "scale=1920:1080:force_original_aspect_ratio=decrease,pad=1920:1080:(ow-iw)*0.5:(oh-ih)*0.5";
		}
		
		return filterComplex;
	}
	
	protected static String setDAR(String filterComplex) {
		if (caseForcerDAR.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
				filterComplex += "setdar=" + comboDAR.getSelectedItem().toString().replace(":", "/");
		}
    	
    	return filterComplex;
	}
	
	protected static String setLoop(String extension) {
		if (caseActiverSequence.isSelected() == false)
		{
			switch (extension)
			{
				case ".jpg":
				case ".jpeg":
				case ".png":
				case ".tif":
				case ".tiff":
				case ".tga":
				case ".bmp":
				case ".psd":
					Shutter.progressBar1.setMaximum(10);
					return " -loop 1 -t " + Settings.txtImageDuration.getText();	
			}
		}
		
		return "";
	}
	
	protected static String setDeinterlace() {		
		if (caseForcerDesentrelacement.isSelected())
		{
			int doubler = 0;
			if (lblTFF.getText().equals("x2"))
				doubler = 1;
			
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=" + doubler + ":" + FFPROBE.fieldOrder + ":0";
		}	
		else if (FFPROBE.entrelaced.equals("1") && caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByInterpolation"))))
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=0:" + FFPROBE.fieldOrder + ":0";
		else
			return "";
	}
	
	protected static String setBlend(String videoFilter) {
		if (caseBlend.isSelected())
		{			
			int value = sliderBlend.getValue();
			StringBuilder blend = new StringBuilder();
			for (int i = 0 ; i < value ; i++)
			{
				blend.append("tblend=all_mode=average,");
			}
			
			blend.append("setpts=" + (float) 25 / Float.parseFloat(caseSequenceFPS.getText()) + "*PTS");
			
			if (videoFilter != "")
				videoFilter += "," + blend;	
			else
				videoFilter = blend.toString();	
		}
		return videoFilter;
	}
	
	protected static String setMotionBlur(String videoFilter) {
		if (caseMotionBlur.isSelected())
		{			
			float fps = Float.parseFloat(caseSequenceFPS.getText()) * 2;
			if (videoFilter != "")
				videoFilter += ",minterpolate=fps=" + fps + ",tblend=all_mode=average,framestep=2";	
			else
				videoFilter = "minterpolate=fps=" + fps + ",tblend=all_mode=average,framestep=2";	
		}
		return videoFilter;
	}
	
	protected static String setLUT(String filterComplex) {
		if (caseLUTs.isSelected())
		{			
			String pathToLuts;
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			{
				pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
				pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/";
			}
			else
				pathToLuts = "LUTs/";
			
			if (filterComplex != "")
				filterComplex += ",lut3d=file=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString();	
			else
				filterComplex = "lut3d=file=" + pathToLuts + Shutter.comboLUTs.getSelectedItem().toString();	
		}
		return filterComplex;
	}
	
	protected static String setColor(String filterComplex) {
		if (caseColor.isSelected())
		{			
			if (filterComplex != "")
				filterComplex += "," + finalEQ;	
			else
				filterComplex = finalEQ;	
		}

		return filterComplex;
	}
	
	protected static String setInterlace() {		
		if (FFPROBE.entrelaced.equals("1") && caseForcerProgressif.isSelected() == false || caseForcerEntrelacement.isSelected())
        	return " -top 1 -flags +ildct+ilme";
        
		return "";
	}
	
	protected static String setInterlace50p(String videoFilter) {
		if (caseForcerEntrelacement.isSelected())
		{			
			if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")) == false)
			{
				if (caseConform.isSelected() && Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", ".")) >= 50.0f)
				{
					if (videoFilter != "")
						videoFilter += ",format=yuv444p,interlace";
					else
						videoFilter = "format=yuv444p,interlace";
				}
			}
			else if (FFPROBE.currentFPS == 50.0f || FFPROBE.currentFPS == 59.94f || FFPROBE.currentFPS == 60.0f)
			{
				if (videoFilter != "")
					videoFilter += ",format=yuv444p,interlace";
				else
					videoFilter = "format=yuv444p,interlace";
			}						
		}
		return videoFilter;
	}
		
	protected static String setRotate(String filterComplex) {
		String rotate = "";
		if (caseRotate.isSelected()) {
			String transpose = "";
			switch (comboRotate.getSelectedItem().toString()) {
			case "90":
				if (caseMiror.isSelected())
					transpose = "transpose=3";
				else
					transpose = "transpose=1";
				break;
			case "-90":
				if (caseMiror.isSelected())
					transpose = "transpose=0";
				else
					transpose = "transpose=2";
				break;
			case "180":
				if (caseMiror.isSelected())
					transpose = "transpose=1,transpose=1,hflip";
				else
					transpose = "transpose=1,transpose=1";
				break;
			}

			rotate = transpose;
		}
		else if (caseMiror.isSelected())
			rotate = "hflip";
					
		if (rotate != "")
		{
	    	if (filterComplex != "") filterComplex += ",";
	    		filterComplex += rotate;	
		}		
		
		return filterComplex;
	}
	
	protected static String setDeband(String filterComplex) {
		
		if (caseBanding.isSelected())
		{
			if (filterComplex != "")
				filterComplex += ",deband=r=32";
			else
				filterComplex = "deband=r=32";
		}	
		return filterComplex;
	}
	
	protected static String setDetails(String videoFilter) {
		
		if (caseDetails.isSelected())
		{
			float value = (0 - (float) sliderDetails.getValue() / 10);
			
			if (videoFilter != "")
				videoFilter += ",smartblur=1.0:" + value;
			else
				videoFilter = "smartblur=1.0:" + value;
		}	
		return videoFilter;
	}
	
	protected static String setFlags() { 
		
		if (caseDetails.isSelected())
    	{
			float value = (0 - (float) sliderDetails.getValue() / 10);
			
			if (value > 0)
				return " -sws_flags lanczos";
    	}
		return "";
	}
	
	protected static String setDenoiser(String videoFilter) {
		if (caseBruit.isSelected())
		{
			int value = sliderBruit.getValue();
			
			if (videoFilter != "")
				videoFilter += ",hqdn3d=" + value + ":" + value + ":" + value + ":" + value;
			else
				videoFilter = "hqdn3d=" + value + ":" + value + ":" + value + ":" + value;
		}
		
		return videoFilter;
	}
	
	protected static String setInterpolation(String filterComplex) {
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformByInterpolation")))
		{		            		
			float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));  
				
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "minterpolate=fps=" + newFPS;            
		}

		return filterComplex;
	}
	
	protected static String setSlowMotion(String filterComplex) {
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
        {		            		
        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));  
        		
            if (filterComplex != "") filterComplex += ",";
            
            filterComplex += "minterpolate=fps=" + newFPS + ",setpts=" + (newFPS / FFPROBE.currentFPS) + "*PTS";            
        }
		
		return filterComplex;
	}

	protected static String setPTS(String videoFilter) {
		if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))
        {		            		
        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
            
            if (videoFilter != "") videoFilter += ",";
            	
            videoFilter += "setpts=" + (FFPROBE.currentFPS / newFPS) + "*PTS";    

    		if (comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse")))		            		
                videoFilter += ",reverse";   			
        }
		
		return videoFilter;
	}
	
	protected static String setSubtitles() {
    	if (caseSubtitles.isSelected())
    	{    		
    		String background = "" ;
			if (SubtitlesWindow.lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				background = ",BorderStyle=4,BackColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&,Outline=0";
			else
				background = ",OutlineColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&";
			
			//Bold
			if (SubtitlesWindow.btnG.getForeground() != Color.BLACK)
				background += ",Bold=1";
			
			//Italic
			if (SubtitlesWindow.btnI.getForeground() != Color.BLACK)
				background += ",Italic=1";
    		
			String i[] = FFPROBE.imageResolution.split("x");
			if (caseForcerResolution.isSelected())
			{
	   			String s[] = lblResolution.getSelectedItem().toString().split("x");
	        	int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);
	        	int ow = Integer.parseInt(s[0]);
	        	int oh = Integer.parseInt(s[1]);      
	        		        	
	        	int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
	        	int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));

				return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
			}
			else
				return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + SubtitlesWindow.textWidth.getText() + ":" + i[1] + "+" + SubtitlesWindow.spinnerSubtitlesPosition.getValue() + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';		
			 
		}
    	
    	return "";
	}
	
	protected static String setOverlay(String filterComplex) {
    	if (caseSubtitles.isSelected())
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");
        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = ((int) (ImageWidth - Integer.parseInt(SubtitlesWindow.textWidth.getText())) / 2);
        	if (caseForcerResolution.isSelected())
        	{
	        	String s[] = lblResolution.getSelectedItem().toString().split("x");
	        	int iw = Integer.parseInt(i[0]);
	        	int ow = Integer.parseInt(s[0]);  
	        	posX =  (int) (posX / ((float) iw/ow));
        	}
    		
    		if (caseLogo.isSelected())
    			filterComplex += "[p];[p][2:v]overlay=shortest=1:x=" + posX;
    		else
    		{    			
        		if (filterComplex != "")
        			filterComplex = "[0:v]" + filterComplex + "[p];[p][1:v]overlay=shortest=1:x=" + posX;
        		else
        			filterComplex = "[0:v][1:v]overlay=shortest=1:x=" + posX;
    		}
      	}
    	
    	return filterComplex;
	}
	
	protected static String setLimiter(String filterComplex) {
		if (caseLimiter.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";

			if (FFPROBE.imageDepth == 8)
				filterComplex = "limiter=16:235";
			else if (FFPROBE.imageDepth == 10)
				filterComplex = "limiter=64:940";
			else if (FFPROBE.imageDepth == 12)
				filterComplex = "limiter=256:3760";
			else if (FFPROBE.imageDepth == 16)
				filterComplex = "limiter=4096:60160";
		}	
		return filterComplex;
	}
	
	protected static String setTimecode() {
		if (caseSetTimecode.isSelected())
			return " -timecode " + TCset1.getText() + ":" + TCset2.getText() + ":" + TCset3.getText() + ":" + TCset4.getText();
		else if (FFPROBE.timecode1 != "")
			return " -timecode " + FFPROBE.timecode1 + ":" + FFPROBE.timecode2 + ":" + FFPROBE.timecode3 + ":" + FFPROBE.timecode4;
	
		return "";
	}

	protected static String setConform(String filterComplex) {				
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformByBlending")))
		{
			float newFPS = Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", "."));
			
			float FPS = FFPROBE.currentFPS;
			if (caseActiverSequence.isSelected())
				FPS = Float.valueOf(caseSequenceFPS.getText().replace(",", "."));
			
			if (FPS != newFPS)
			{	            
				if (filterComplex != "") filterComplex += ",";       
				
				filterComplex += "minterpolate=fps=" + newFPS + ":mi_mode=blend";
			}
		}
		
		return filterComplex;
	}
	
	protected static String setFramerate() {
		if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("50"))
			return " -r 25";
		else if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("59,94"))
			return " -r 29.97";
		else if (caseForcerEntrelacement.isSelected() && caseConform.isSelected() && comboFPS.getSelectedItem().toString().equals("60"))
			return " -r 30";
		else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
			return " -r " + FFPROBE.currentFPS;
		else if (caseConform.isSelected())
			return " -r " + Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", "."));
		else if (caseActiverSequence.isSelected())
		{
			if (caseConform.isSelected())
				return " -r " + Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", ".")) + " -frames:v " + liste.getSize();	
			else
				return " -r " + caseSequenceFPS.getText() + " -frames:v " + liste.getSize();
		}
		
		return "";
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
		if (FFMPEG.error || fileOut.length() == 0 || caseAS10.isSelected())
		{
			if (FFMPEG.error)
			{
				FFMPEG.errorList.append(fichier);
				FFMPEG.errorList.append(System.lineSeparator());
		    }
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}
		
		//Traitement de la file en Bout à bout
		if (Settings.btnSetBab.isSelected())
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
		if (caseAS10.isSelected())
			Wetransfer.addFile(new File(sortie + "/" + fichier.replace(fichier.substring(fichier.lastIndexOf(".")), "_AS11.mxf")));
		else
			Wetransfer.addFile(fileOut);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
		//Séquence d'images et bout à bout
		if (caseActiverSequence.isSelected() || Settings.btnSetBab.isSelected())
			return true;
		
		//Timecode
		if (caseIncrementTimecode.isSelected())
		{			 
			NumberFormat formatter = new DecimalFormat("00");

			int timecodeToMs = Integer.parseInt(TCset1.getText()) * 3600000 + Integer.parseInt(TCset2.getText()) * 60000 + Integer.parseInt(TCset3.getText()) * 1000 + Integer.parseInt(TCset4.getText()) * (int) (1000 / FFPROBE.currentFPS);
			int millisecondsToTc = timecodeToMs + FFPROBE.dureeTotale;
			
			if (caseInAndOut.isSelected())
				millisecondsToTc = timecodeToMs + VideoPlayer.dureeHeures * 3600000 + VideoPlayer.dureeMinutes * 60000 + VideoPlayer.dureeSecondes * 1000 + VideoPlayer.dureeImages * (int) (1000 / FFPROBE.currentFPS);
			
			TCset1.setText(formatter.format(millisecondsToTc / 3600000));
			TCset2.setText(formatter.format((millisecondsToTc / 60000) % 60));
			TCset3.setText(formatter.format((millisecondsToTc / 1000) % 60));				        
			TCset4.setText(formatter.format((int) (millisecondsToTc / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
		}
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			AVC.main();
			return true;
		}
		return false;
	}
	
}//Class