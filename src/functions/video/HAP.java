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

package functions.video;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import application.Ftp;
import application.RecordInputDevice;
import application.VideoPlayer;
import application.Settings;
import application.Shutter;
import application.SubtitlesWindow;
import application.Utils;
import application.WatermarkWindow;
import application.Wetransfer;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;

public class HAP extends Shutter {
	
	private static int complete;
	
	public static void main(boolean encode) {
		
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
						if (Utils.waitFileCompleted(file) == false)
							break;
		            }
					//SCANNING
		            
	            try {
					String fichier = file.getName();
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					lblEncodageEnCours.setText(fichier);
					
					//Analyse des données
					if (analyse(file) == false)
						continue;		 

					//Subtitles
					String subtitles = setSubtitles();
										
					 //Interlace
		            String forceField = setInterlace();
		            
					//Codec
					String codec = setCodec();
					
		            //Colorspace
		            String colorspace = setColorspace();
					
			        //Deinterlace
					String filterComplex = setDeinterlace();
					
					//Blend
					filterComplex = setBlend(filterComplex);
					
					//MotionBlur
					filterComplex = setMotionBlur(filterComplex);
					
					//LUTs
					filterComplex = setLUT(filterComplex);
						
					//Levels
					filterComplex = setLevels(filterComplex);
									
					//Colormatrix
					filterComplex = setColormatrix(filterComplex);	
					
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
					
	            	//Watermark
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

	            	//Timecode
					String timecode = setTimecode();
					
					//Fade-in Fade-out
					filterComplex = setFade(filterComplex);
					
					//Mapping des pistes audios et Filtercomplex
					filterComplex = setFilterComplex(filterComplex, audio);
					
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					//Fichier de sortie
					String nomExtension;
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					else		
					{
						if (comboFilter.getSelectedItem().equals("Standard"))
							nomExtension =  "_HAP";
						else
							nomExtension =  "_HAP_" + comboFilter.getSelectedItem().toString();
					}

					String sortieFichier =  sortie.replace("\\", "/") + "/" + fichier.replace(extension, nomExtension + ".mov"); 
										
					//Si le fichier existe
					File fileOut = new File(sortieFichier);				
									
					if(fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", extension);
						if (fileOut == null)
							continue;	
					}
									
					String output = '"' + fileOut.toString() + '"';
					if (caseDisplay.isSelected())					
						output = "-f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:play" + '"';
						
					//Mode concat
					String concat = FFMPEG.setConcat(file, sortie);					
					if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						file = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt"));
						
					//Envoi de la commande
					String cmd =  frameRate + colorspace + filterComplex + " -c:v hap" + codec + " -chunks 4" + forceField + timecode + flags + " -y ";
	
					//Screen capture
					if (inputDeviceIsRunning)
					{						
						String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(Calendar.getInstance().getTime());	

						if ((liste.getElementAt(0).equals("Capture.current.screen") || System.getProperty("os.name").contains("Mac")) && RecordInputDevice.audioDeviceIndex > 0)
							cmd = cmd.replace("1:v", "2:v").replace("-map v:0", "-map 1:v").replace("0:v", "1:v");
							
						if (encode)
							FFMPEG.run(" " + RecordInputDevice.setInputDevices() + logo + cmd + output.replace("Capture.current", timeStamp).replace("Capture.input", timeStamp));	
						else
						{
							FFMPEG.toFFPLAY(" " + RecordInputDevice.setInputDevices() + logo + cmd + " -f matroska pipe:play |");							
							break;
						}						
						
						fileOut = new File(fileOut.toString().replace("Capture.current", timeStamp).replace("Capture.input", timeStamp));
					}
					else if (encode) //Encodage
						FFMPEG.run(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.outPoint + cmd + output);	
					else //Preview
					{						
						FFMPEG.toFFPLAY(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.outPoint + cmd + " -f matroska pipe:play |");
						break;
					}
					
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
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false && encode)
					enfOfFunction();
			}//run
			
		});
		thread.start();
		
    }//main
	
	protected static String setLogo() {
		if (caseLogo.isSelected() && Shutter.overlayDeviceIsRunning)
			return " " + RecordInputDevice.setOverlayDevice(); 
		else if (caseLogo.isSelected())
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
		
	private static String setCodec() {
		if (comboFilter.getSelectedItem().equals("Alpha"))
			return " -format hap_alpha";
		else if (comboFilter.getSelectedItem().equals("Q"))
			return " -format hap_q";
		else	
			return "";
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
			
			if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
	        	return " -an";
	        
			return audio + " -ar 48000";
		}
	}
	
	protected static String setAudioFade() {
		String audio = "";
		
		//Fade-in
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
			
    		String audioFade = "afade=in:st=" + audioStart + "ms:d=" + audioInValue + "ms";

    		audio += audioFade + ",";
    	}
    	
    	//Fade-out
    	if (caseAudioFadeOut.isSelected())
    	{
    		long audioOutValue = (long) (Integer.parseInt(spinnerAudioFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
    		long audioStart =  (long) FFPROBE.totalLength - audioOutValue;
    		
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
        			audioStart = FFPROBE.totalLength - (totalOut - totalIn) - audioOutValue;
    		}
    		
    		String audioFade = "afade=out:st=" + audioStart + "ms:d=" + audioOutValue + "ms";
    		
    		audio += audioFade + ",";
    	}
		
    	//Audio Speed				        
		if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))    
        {
        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (newFPS/ FFPROBE.currentFPS);
        	if (value < 0.5f || value > 2.0f)
        		return " -an";
        	else
	    		audio = "atempo=" + value + ",";       			
        }
        
		return audio;
	}
	
	protected static String setFade(String filterComplex) {
		//Fade-in
    	if (caseVideoFadeIn.isSelected())
    	{ 
    		if (filterComplex != "") filterComplex += ",";	
    		
    		long videoInValue = (long) (Integer.parseInt(spinnerVideoFadeIn.getText()) * ((float) 1000 / FFPROBE.currentFPS));
    		long videoStart = 0;
    		
    		if (caseInAndOut.isSelected() && VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
    		{
        		long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));

        		if (totalIn >= 10000)
        			videoStart = 10000;
        		else
        			videoStart = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
    		}
    		
    		String color = "black";
			if (lblFadeInColor.getText().equals(language.getProperty("white")))
				color = "white";
    		
    		String videoFade = "fade=in:st=" + videoStart + "ms:d=" + videoInValue + "ms:color=" + color;
    		
        	filterComplex += videoFade;
    	}
    	
    	//Fade-out
    	if (caseVideoFadeOut.isSelected())
    	{
    		if (filterComplex != "") filterComplex += ",";	
    		
    		long videoOutValue = (long) (Integer.parseInt(spinnerVideoFadeOut.getText()) * ((float) 1000 / FFPROBE.currentFPS));
    		long videoStart = (long) FFPROBE.totalLength - videoOutValue;
    		
			if (caseInAndOut.isSelected())
    		{
        		long totalIn = (long) (Integer.parseInt(VideoPlayer.caseInH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseInM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseInS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));
        		long totalOut = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));
        		 
        		if (VideoPlayer.comboMode.getSelectedItem().toString().contentEquals(Shutter.language.getProperty("cutUpper")))
        		{
	        		if (totalIn >= 10000)
	        			videoStart = 10000 + (totalOut - totalIn) - videoOutValue;
	        		else
	        			videoStart = (long) (Integer.parseInt(VideoPlayer.caseOutH.getText()) * 3600000 + Integer.parseInt(VideoPlayer.caseOutM.getText()) * 60000 + Integer.parseInt(VideoPlayer.caseOutS.getText()) * 1000 + Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS)) - videoOutValue;
        		}
        		else //Remove mode
    	    		videoStart = FFPROBE.totalLength - (totalOut - totalIn) - videoOutValue;
    		}
    		
    		String color = "black";
			if (lblFadeOutColor.getText().equals(language.getProperty("white")))
				color = "white";
    		
    		String videoFade = "fade=out:st=" + videoStart + "ms:d=" + videoOutValue + "ms:color=" + color;
    		
    		filterComplex += videoFade;
    	}
		
		return filterComplex;
	}

	protected static String setFilterComplex(String filterComplex, String audio) {
		
		String mapping = "";
		
		String audioFade = setAudioFade();
		
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
			if (caseLogo.isSelected() || (caseSubtitles.isSelected() && subtitlesBurn))
				mapping += " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			else if (filterComplex != "")
				mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			else
				mapping += " -map v:0" + audio;
			
			//On map les sous-titres que l'on intègre        
			if (caseSubtitles.isSelected() && subtitlesBurn == false)
				mapping += " -map 1:s -c:s mov_text";
			
			return mapping;
		}
		else
		{			
			if (inputDeviceIsRunning)
			{
				if (liste.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
					mapping = " -map a? -map 2?";
				else
					mapping = " -map a?";	
			}
			else if (FFPROBE.channels == 1) //Si le son est stereo alors on split
			{
				if (caseLogo.isSelected() || (caseSubtitles.isSelected() && subtitlesBurn))
					mapping += " -filter_complex " + '"' + filterComplex + "[out];[0:a]" + audioFade + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
				else if (filterComplex != "")
					mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out];[0:a]" + audioFade + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
				else
					mapping += " -map v:0 -filter_complex [0:a]" + audioFade + "channelsplit[a1][a2] -map [a1] -map [a2]" + audio;
			}
			else
			{
				if (comboAudio1.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio1.getSelectedIndex()) + "?";
				if (comboAudio2.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio2.getSelectedIndex()) + "?";
				if (comboAudio3.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio3.getSelectedIndex()) + "?";
				if (comboAudio4.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio4.getSelectedIndex()) + "?";
				if (comboAudio5.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio5.getSelectedIndex()) + "?";
				if (comboAudio6.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio6.getSelectedIndex()) + "?";
				if (comboAudio7.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio7.getSelectedIndex()) + "?";
				if (comboAudio8.getSelectedIndex() != 8)
					mapping += " -map a:" + (comboAudio8.getSelectedIndex()) + "?";
			}
		}			
		
		if (FFPROBE.channels != 1) //On ajoute le filterComplex lorsque il n'y a pas de split des pistes son	
		{
			if (caseLogo.isSelected() || (caseSubtitles.isSelected() && subtitlesBurn))
				mapping = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + mapping + audio;
			else if (filterComplex != "")
				mapping = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + mapping + audio;
			else
				mapping = " -map v:0" + mapping + audio;	
		}		
		
		//On map les sous-titres que l'on intègre        
        if (caseSubtitles.isSelected() && subtitlesBurn == false)
        {
        	String map = " -map 1:s";
        	if (caseLogo.isSelected())
        		map = " -map 2:s";
        	
        	String[] languages = Locale.getISOLanguages();			
			Locale loc = new Locale(languages[comboSubtitles.getSelectedIndex()]);
        	
        	filterComplex += map + " -c:s mov_text -metadata:s:s:0 language=" + loc.getISO3Language();
        }
		
		return mapping;
	}

	protected static boolean analyse(File file) throws InterruptedException {

		if (caseGenerateFromDate.isSelected())
		{
			EXIFTOOL.run(file.toString());	
			do
				Thread.sleep(100);						 
			while (EXIFTOOL.isRunning);
		}
		
		if (inputDeviceIsRunning == false) //Already analyzed
		{
			 FFPROBE.FrameData(file.toString());	
			 do
			 {
			 	Thread.sleep(100);
			 }
			 while (FFPROBE.isRunning);
			 
			 if (errorAnalyse(file.toString()))
				 return false;
		}	 
		
		 FFPROBE.Data(file.toString());

		 do
		 {
			Thread.sleep(100);
		 }
		 while (FFPROBE.isRunning);
		 					 		 
		 if (errorAnalyse(file.toString()))
			return false;

		return true;
	}
	
	protected static File setSequenceName(File file, String extension) {
		if (caseEnableSequence.isSelected())
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
		if (caseEnableSequence.isSelected())
		{
			int n = 0;
			do {
				n ++;				
			} while (file.toString().substring(file.toString().lastIndexOf(".") - n).replace(extension, "").matches("[0-9]+") != false);	
					
			if (caseBlend.isSelected())
				return " -start_number " + file.toString().substring(file.toString().lastIndexOf(".") - n + 1).replace(extension, "");	
			else	
				return " -framerate " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".") + " -start_number " + file.toString().substring(file.toString().lastIndexOf(".") - n + 1).replace(extension, "");		
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
		
		if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		{
			String s[] = comboResolution.getSelectedItem().toString().split("x");
			
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
		if (caseEnableSequence.isSelected() == false)
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
	
	protected static String setColorspace() {
		if (caseColorspace.isSelected())
		{
			if (comboColorspace.getSelectedItem().equals("Rec. 709"))
				return " -color_primaries bt709 -color_trc bt709 -colorspace bt709";
			else if (comboColorspace.getSelectedItem().equals("Rec. 2020 PQ"))
				return " -color_primaries bt2020 -color_trc smpte2084 -colorspace bt2020nc";
			else if (comboColorspace.getSelectedItem().equals("Rec. 2020 HLG"))
				return " -color_primaries bt2020 -color_trc arib-std-b67 -colorspace bt2020nc";
			else
				return "";
		}
		else
			return "";
	}
	
	protected static String setDeinterlace() {		
		if (caseForcerDesentrelacement.isSelected() && comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine"))	
		{
			String detelecineFields = "top";
			if (lblTFF.getText().equals("BFF"))
				detelecineFields = "bottom";
			
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=first_field=" + detelecineFields;
		}			
		else if (caseForcerDesentrelacement.isSelected())
		{
			int doubler = 0;
			if (lblTFF.getText().equals("x2") && caseForcerDesentrelacement.isSelected())
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
			
			blend.append("setpts=" + (float) 25 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) + "*PTS");
			
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
			float fps = Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", ".")) * 2;
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
	
	protected static String setLevels(String filterComplex) {
		
		if (caseLevels.isSelected())
		{			
			if (filterComplex != "") filterComplex += ",";
			
			filterComplex += "scale=in_range=" + comboInLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full") + ":out_range=" + comboOutLevels.getSelectedItem().toString().replace("16-235", "limited").replace("0-255", "full");		
		}

		return filterComplex;
	}
	
	protected static String setColormatrix(String filterComplex) {
		if (caseColormatrix.isSelected())
		{
			if (filterComplex != "") filterComplex += ",";
			
			if (comboInColormatrix.getSelectedItem().equals("HDR"))
			{		
				String pathToLuts;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					pathToLuts = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					pathToLuts = pathToLuts.substring(0,pathToLuts.length()-1);
					pathToLuts = pathToLuts.substring(0,(int) (pathToLuts.lastIndexOf("/"))).replace("%20", "\\ ")  + "/LUTs/HDR-to-SDR.cube";
				}
				else
					pathToLuts = "LUTs/HDR-to-SDR.cube";

				filterComplex += "lut3d=file=" + pathToLuts;	
			}
			else
				filterComplex += "colorspace=iall=" + Shutter.comboInColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625") + ":all=" + Shutter.comboOutColormatrix.getSelectedItem().toString().replace("Rec. ", "bt").replace("601", "601-6-625");
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
		
		return " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
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
    	if (caseSubtitles.isSelected() && subtitlesBurn)
    	{    	
			if (subtitlesFile.toString().substring(subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
    		{	
				String background = "" ;
				if (SubtitlesWindow.lblBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
					background = ",BorderStyle=4,BackColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&,Outline=0";
				else
					background = ",Outline=" + SubtitlesWindow.outline + ",OutlineColour=&H" + SubtitlesWindow.alpha + SubtitlesWindow.hex2 + "&";
				
				//Bold
				if (SubtitlesWindow.btnG.getForeground() != Color.BLACK)
					background += ",Bold=1";
				
				//Italic
				if (SubtitlesWindow.btnI.getForeground() != Color.BLACK)
					background += ",Italic=1";
				
				String i[] = FFPROBE.imageResolution.split("x");
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = comboResolution.getSelectedItem().toString().split("x");
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
			else // ASS or SSA
			{
				String i[] = FFPROBE.imageResolution.split("x");
				SubtitlesWindow.textWidth.setText(i[0]); //IMPORTANT
				
				if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = comboResolution.getSelectedItem().toString().split("x");
		        	int iw = Integer.parseInt(i[0]);
		        	int ih = Integer.parseInt(i[1]);
		        	int ow = Integer.parseInt(s[0]);
		        	int oh = Integer.parseInt(s[1]);        	
		        	
		        	int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
		        	int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));
		        	
		        	return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
				}
				else
					return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + i[0] + ":" + i[1] + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1" + '"';
			}
		}
		else if (caseSubtitles.isSelected() && subtitlesBurn == false)
    	{
    		return FFMPEG.inPoint + " -i " + '"' +  subtitlesFile.toString() + '"';
    	}
    	
    	return "";
	}
	
	protected static String setOverlay(String filterComplex) {
    	if (caseSubtitles.isSelected() && subtitlesBurn)
    	{    		
        	String i[] = FFPROBE.imageResolution.split("x");
        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = ((int) (ImageWidth - Integer.parseInt(SubtitlesWindow.textWidth.getText())) / 2);
        	if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
        	{
	        	String s[] = comboResolution.getSelectedItem().toString().split("x");
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
				filterComplex += "limiter=16:235";
			else if (FFPROBE.imageDepth == 10)
				filterComplex += "limiter=64:940";
			else if (FFPROBE.imageDepth == 12)
				filterComplex += "limiter=256:3760";
			else if (FFPROBE.imageDepth == 16)
				filterComplex += "limiter=4096:60160";
		}	
		return filterComplex;
	}
	
	protected static String setTimecode() {
		
		if (caseGenerateFromDate.isSelected())
		{
			String s[] = EXIFTOOL.creationHours.split(":");
			return " -timecode " + s[0] + ":" + s[1] + ":" + s[2] + ":00";
		}		
		else if (caseSetTimecode.isSelected())
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
			if (caseEnableSequence.isSelected())
				FPS = Float.valueOf(caseSequenceFPS.getSelectedItem().toString().replace(",", ".").replace(",", "."));
			
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
		else if (caseEnableSequence.isSelected())
		{
			if (caseConform.isSelected())
				return " -r " + Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", ".")) + " -frames:v " + liste.getSize();	
			else
				return " -r " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".") + " -frames:v " + liste.getSize();
		}
		else if (inputDeviceIsRunning)
			return " -vsync vfr";
		
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
		if (FFMPEG.error)
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
		
		//Séquence d'images et bout à bout
		if (caseEnableSequence.isSelected() || Settings.btnSetBab.isSelected())
			return true;
		
		//Timecode
		if (caseIncrementTimecode.isSelected())
		{			 
			NumberFormat formatter = new DecimalFormat("00");

			int timecodeToMs = Integer.parseInt(TCset1.getText()) * 3600000 + Integer.parseInt(TCset2.getText()) * 60000 + Integer.parseInt(TCset3.getText()) * 1000 + Integer.parseInt(TCset4.getText()) * (int) (1000 / FFPROBE.currentFPS);
			int millisecondsToTc = timecodeToMs + FFPROBE.totalLength;
			
			if (caseInAndOut.isSelected())
				millisecondsToTc = timecodeToMs + VideoPlayer.dureeHeures * 3600000 + VideoPlayer.dureeMinutes * 60000 + VideoPlayer.dureeSecondes * 1000 + VideoPlayer.dureeImages * (int) (1000 / FFPROBE.currentFPS);
			
			TCset1.setText(formatter.format(millisecondsToTc / 3600000));
			TCset2.setText(formatter.format((millisecondsToTc / 60000) % 60));
			TCset3.setText(formatter.format((millisecondsToTc / 1000) % 60));				        
			TCset4.setText(formatter.format((int) (millisecondsToTc / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
		}		
		
		//Bout à bout
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			HAP.main(true);
			return true;
		}
		return false;
	}
}//Class
