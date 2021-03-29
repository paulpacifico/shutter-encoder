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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import application.Ftp;
import application.VideoPlayer;
import application.WatermarkWindow;
import application.OverlayWindow;
import application.RecordInputDevice;
import application.Settings;
import application.Shutter;
import application.SubtitlesWindow;
import application.Utils;
import application.Wetransfer;
import library.FFMPEG;
import library.FFPROBE;
import library.MKVMERGE;

public class VP9 extends Shutter {
		
	private static int complete;
	private static String cmd;	
	
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
					
					//InOut	
					FFMPEG.fonctionInOut();	
					 
					//Séquence d'images
					String sequence = setSequence(file, extension);
					
					file = setSequenceName(file, extension);
					
					//Loop image					
					String loop = setLoop(extension);	
					
					//Subtitles
					String subtitles = setSubtitles();
					
					//Resolution
					String gamma = setGamma();						
			        
					//Resolution
					String resolution = setResolution();	

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
					
					//Decimate
					filterComplex = setDecimate(filterComplex);
					
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
		            
	            	//Logo
			        String logo = setLogo();
									
			    	//Watermark
					filterComplex = setWatermark(filterComplex);
					
	            	//Timecode
					filterComplex = showTimecode(filterComplex, fichier.replace(extension, ""));
			        
			    	//Rognage
			        filterComplex = setCrop(filterComplex);
					
					//Rotate
					filterComplex = setRotate(filterComplex);
					
					//Padding
					filterComplex = setPad(filterComplex);
					
					//Overlay
					filterComplex = setOverlay(filterComplex);
			        	            
	            	//Audio
					String audio = setAudio(filterComplex);	
					
					//Fade-in Fade-out
					filterComplex = setFade(filterComplex);
					
	            	//filterComplex
					filterComplex = setFilterComplex(filterComplex, audio);			
					
					//Codec 
					String codec = setCodec();
		            
			       	//Preset
			        String preset = setPreset();
			        
			        //Quality
			        String quality = setQuality();
			        
					//Tune
					String tune = setTune();
			        
					//GOP
					String gop = setGOP();
					
					//Bitrate
					String bitrate = setBitrate();
					
					//Dossier de sortie
					String sortie = setSortie("", file);
																							
					//Fichier de sortie
					String nomExtension;
					if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
						nomExtension = "_VP9_TC";
					else		
						nomExtension =  "_VP9";
					
					if (case2pass.isSelected())
						nomExtension += "_2pass";
					
					if (Settings.btnExtension.isSelected())
						nomExtension = Settings.txtExtension.getText();
					
					String sortieFichier =  sortie.replace("\\", "/") + "/" + fichier.replace(extension, nomExtension + comboFilter.getSelectedItem().toString()); 
					
			        //2pass
			        String pass = setPass(sortieFichier);
					
					File fileOut = new File(sortieFichier);				
					//Si le fichier existe
					if(fileOut.exists())		
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, nomExtension + "_", comboFilter.getSelectedItem().toString());
						if (fileOut == null)
							continue;						
					}					
					
					String output = '"' + fileOut.toString() + '"';
					if (caseDisplay.isSelected())
						output = "-flags:v +global_header -f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:play" + '"';
								
					//Mode concat
					String concat = FFMPEG.setConcat(file, sortie);					
					if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						file = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt"));
								
					//Envoi de la commande
					cmd = frameRate + resolution + colorspace + pass + filterComplex + " -vcodec" + codec + preset + quality + tune + gop + bitrate + gamma + flags + " -y ";
										
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
						FFMPEG.run(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + output);		
					else //Preview
					{						
						FFMPEG.toFFPLAY(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + " -f matroska pipe:play |");
						break;
					}
					
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					if (case2pass.isSelected())
					{						
						if (FFMPEG.cancelled == false)
							FFMPEG.run(loop + FFMPEG.inPoint + sequence + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd.replace("-pass 1", "-pass 2") + output);	
						
						//Attente de la fin de FFMPEG
						do
								Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
						
						//SUPPRESSION DES FICHIERS RESIDUELS
						final File folder = new File(new File(sortieFichier).getParent());
						listFilesForFolder(fichier.replace(extension, ""), folder);
					}
					
					//HDR
					if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("HDR") && FFMPEG.error == false)
					{
						lblEncodageEnCours.setText(fichier);
						
						File HDRmkv = fileOut;
						File tempHDR = new File(fileOut.toString().replace(comboFilter.getSelectedItem().toString(), "_HDR" + comboFilter.getSelectedItem().toString()));
						fileOut.renameTo(tempHDR);	
						fileOut = HDRmkv;

						String PQorHLG = "16";
						if (comboColorspace.getSelectedItem().toString().contains("HLG"))
							PQorHLG = "18";
						
						String cmd = " --colour-matrix 0:9 --colour-range 0:1 --colour-transfer-characteristics 0:" + PQorHLG + " --colour-primaries 0:9 --max-luminance 0:" + (int) FFPROBE.HDRmax + " --min-luminance 0:" + FFPROBE.HDRmin + " --chromaticity-coordinates 0:0.68,0.32,0.265,0.690,0.15,0.06 --white-colour-coordinates 0:0.3127,0.3290";
						MKVMERGE.run(cmd + " " + '"' + tempHDR + '"' + " -o " + '"'  + HDRmkv + '"');		
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(MKVMERGE.runProcess.isAlive());
						
						if (MKVMERGE.error == false)
							tempHDR.delete();
						else
							FFMPEG.error = true;
					}

					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false 
					|| FFMPEG.saveCode == false && caseEnableSequence.isSelected()
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

	protected static String setCodec() {
    	if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
    		return " vp9_qsv";
    	else if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("VAAPI"))
    		return " vp9_vaapi";
    	else
        	return " libvpx-vp9";        
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
		
        if (filterComplex != "")
        {	   	   
        	//Si une des cases est sélectionnée alors il y a déjà [0:v]
        	if (caseLogo.isSelected() || (caseSubtitles.isSelected() && subtitlesBurn))
        		filterComplex = " -filter_complex " + '"' + filterComplex + "[out]";
        	else
        		filterComplex = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]";
        	
        	
			float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (newFPS/ FFPROBE.currentFPS);
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))) && (value < 0.5f || value > 2.0f))    
	        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
			else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
				filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        	else if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
			else if (FFPROBE.stereo && lblAudioMapping.getText().equals(language.getProperty("mono")) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.surround == false)
        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && debitAudio.getSelectedItem().toString().equals("0") == false)
        		filterComplex += audio + '"' + " -map " + '"' + "[out]" + '"' + " -map " + '"'+ "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
        	else
        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        }
        else
        {        
			float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (newFPS/ FFPROBE.currentFPS);
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))) && (value < 0.5f || value > 2.0f))    
	        		filterComplex = " -map v:0" + audio;
			else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
				filterComplex = " -map v:0" + audio;
        	else if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
        		filterComplex = audio + " -map v:0 -map " + '"' +  "[a]" + '"';
			else if (FFPROBE.stereo && lblAudioMapping.getText().equals(language.getProperty("mono")) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.surround == false)
        		filterComplex = audio + " -map v:0 -map " + '"' +  "[a]" + '"';
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && debitAudio.getSelectedItem().toString().equals("0") == false)
        		filterComplex = audio + " -map v:0 -map " + '"'+ "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
        	else
        		filterComplex = " -map v:0" + audio;
        }
		
		//On map les sous-titres que l'on intègre        
        if (caseSubtitles.isSelected() && subtitlesBurn == false)
        {
        	String map = " -map 1:s";
        	if (caseLogo.isSelected())
        		map = " -map 2:s";
        	
        	String[] languages = Locale.getISOLanguages();			
			Locale loc = new Locale(languages[comboSubtitles.getSelectedIndex()]);
        	
        	if (comboFilter.getSelectedItem().toString().equals(".mkv"))
        		filterComplex += map + " -c:s srt -metadata:s:s:0 language=" + loc.getISO3Language();
        	else
        		filterComplex += map + " -c:s mov_text -metadata:s:s:0 language=" + loc.getISO3Language();
        }
		else if (casePreserveSubs.isSelected())
        {
        	if (comboFilter.getSelectedItem().toString().equals(".mkv"))
        		filterComplex += " -c:s srt -map s?";
        	else
        		filterComplex += " -c:s mov_text -map s?";
        }
        
        return filterComplex;
	}
	
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

	protected static String setPreset() {
        if (caseQMax.isSelected())
        	return " -speed 0";
        else if (caseForceSpeed.isSelected())
        	return " -speed " + Shutter.comboForceSpeed.getSelectedItem().toString();
        else
        	return " -speed 4";
	}
	
	protected static String setQuality() {
		if (caseQMax.isSelected())
        	return " -quality best";
        else if (caseForceQuality.isSelected())
            return " -quality " + Shutter.comboForceQuality.getSelectedItem().toString();
        else
        	return "";
	}
	
	protected static String setTune() {
        if (caseForceTune.isSelected())
            return " -tune-content " + Shutter.comboForceTune.getSelectedItem().toString();
        else
        	return "";
	}
	
	protected static String setGOP() {
        if (caseGOP.isSelected())
            return " -g " + Shutter.gopSize.getText();
        else
        	return "";
	}
	
	protected static String setBitrate() {
        if (lblVBR.getText().equals("CQ"))
        	return " -crf " + debitVideo.getSelectedItem().toString();   
        else
        	return " -b:v " + debitVideo.getSelectedItem().toString() + "k";
	}	

	protected static String setPass(String sortieFichier) {
		if (case2pass.isSelected())			
			return " -pass 1 -passlogfile " + '"' + sortieFichier + '"';
		else		
			return "";
	}
	
	protected static String setAudio(String filterComplex) {
		if (comboAudioCodec.getSelectedItem().equals(language.getProperty(("codecCopy"))))
		{
    		String mapping = "";
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
			
			return " -c:a copy" + mapping;
		}
		else if (debitAudio.getSelectedItem().toString().equals("0") || comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio")))
			return " -an";
		else
		{
			String audio = "";
			String audioCodec = "libopus";
			if (comboAudioCodec.getSelectedItem().toString().equals("OGG"))
				audioCodec = "libvorbis";			
			
			String newAudio = "";
			
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

	    		if (newAudio != "")
	    			newAudio += "," + audioFade;
	    		else
	    			newAudio += audioFade;
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
	    		
	    		if (newAudio != "")
	    			newAudio += "," + audioFade;
	    		else
	    			newAudio += audioFade;
	    	}
			
	    	//Audio Speed				        
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))    
	        {
	        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
	        	float value = (float) (newFPS/ FFPROBE.currentFPS);
	        	if (value < 0.5f || value > 2.0f)
	        		return " -an";
	        	else
	        	{
		    		if (newAudio != "")
		    			newAudio = ",atempo=" + value;
		    		else
		    			newAudio = "atempo=" + value;
	        	}	        			
	        }
			else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
	        	return " -an";
			
		    if (FFPROBE.stereo)
		    {
		    	if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
		    	
		    	if (FFPROBE.surround)
		    	{			    	
			    	if (lblAudioMapping.getText().equals("Multi"))
			    	{
				    	FFPROBE.stereo = false; //permet de contourner le split audio				    	
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k" + newAudio + " -map a:0";
			    	}
					else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
			    	{
			    		if (newAudio != "") 
				    		newAudio = newAudio.replace(" -filter:a", "").replace("\"", "") + ",";
				    	
				    	audio += " -c:a " + audioCodec + " -ac 1 -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k -filter:a " + '"' + newAudio + "pan=stereo|FL=FC+0.30*FL+0.30*BL|FR=FC+0.30*FR+0.30*BR" + '"' + " -map a?";
			    	}
				    else	
				    {
				    	if (newAudio != "") 
				    		newAudio = newAudio.replace(" -filter:a", "").replace("\"", "") + ",";
				    	
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k -filter:a " + '"' + newAudio + "pan=stereo|FL=FC+0.30*FL+0.30*BL|FR=FC+0.30*FR+0.30*BR" + '"' + " -map a?";
				    }
			    }
		    	else if (lblAudioMapping.getText().equals("Multi"))
		    	{
		    		if (newAudio != "")
			    		newAudio = "," + newAudio;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
				    audio += "[a:0]pan=1c|c0=c" + comboAudio1.getSelectedIndex() + newAudio + "[a1];[a:0]pan=1c|c0=c" + comboAudio2.getSelectedIndex() + newAudio + "[a2]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";
		    	}
				else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
		    	{
					if (newAudio != "")
			    		newAudio = "," + newAudio;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
		    		
					if (comboAudio1.getSelectedIndex() != 8 && comboAudio2.getSelectedIndex() != 8) //Mixdown des pistes en mono
						audio += "[0:a]anull" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";
					else
		    		{
		    			if (comboAudio1.getSelectedIndex() == 0)
		    				audio += "[0:a]channelsplit=channel_layout=stereo:channels=FL" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";    	
		    			else
		    				audio += "[0:a]channelsplit=channel_layout=stereo:channels=FR" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";    	
		    		}
				}
		    	else
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k" + newAudio + " -map a:0";
		    }
		    else if (FFPROBE.channels > 1)
		    {
		    	 if (inputDeviceIsRunning)
		         {
		        	 if (newAudio != "")
				    		newAudio = "," + newAudio;
				    	
					    if (filterComplex != "")
					    	audio += ";";
					    else
					    	audio += " -filter_complex " + '"';	
					    
					    if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
					    	audio += "[0:a][2:a]amix=inputs=2" + newAudio + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";   
					    else if (lblAudioMapping.getText().equals("Multi"))
				    	{
					    	if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
				    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k" + newAudio + " -map 0:a? -map 2:a?";
				    	}
					    else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
					    {
					    	if (comboAudio1.getSelectedIndex() != 8 && comboAudio2.getSelectedIndex() != 8) //Mixdown des pistes en mono
				    			 audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a][" + String.valueOf(comboAudio2.getSelectedIndex()).replace("1","2") + ":a]amerge=inputs=2" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";
				    		 else
				    			 audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a]anull" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";
					    }
		         }
		    	 else if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
    			 {
			    	if (newAudio != "")
			    		newAudio = "," + newAudio;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
			    	audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + newAudio + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";    		 
    			 }
				 else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
		    	 {
		    		 if (newAudio != "")
				    		newAudio = "," + newAudio;
				    	
		    		 if (filterComplex != "")
				    	audio += ";";
		    		 else
				    	audio += " -filter_complex " + '"';	
				    
		    		 if (comboAudio1.getSelectedIndex() != 8 && comboAudio2.getSelectedIndex() != 8) //Mixdown des pistes en mono
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k";
		    		 else
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "]anull" + newAudio + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k"; 
		    	 }
		    	 else
		    	 {
		    		String mapping = "";
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
					
		    		if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k" + newAudio + mapping;
		    	 }		    	 
		    }
		    else
		    {
		    	if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
		    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + debitAudio.getSelectedItem().toString() + "k" + newAudio + " -map a?";
		    }
		    
		    return audio;		   				    
		}
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
				
		if (caseRognage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
	
			filterComplex += "crop=" + FFPROBE.cropHeight + ":" + FFPROBE.cropWidth + ":" + FFPROBE.cropPixelsWidth + ":" + FFPROBE.cropPixelsHeight;
		}
    	
    	if (caseRognerImage.isSelected())
		{
			if (filterComplex != "")
				filterComplex += "[w];[w]";
			
    		filterComplex += Shutter.cropFinal;
		}
    	
    	return filterComplex;
	}
	
	protected static String setPad(String filterComplex) {	
		
		if (lblPad.getText().equals(language.getProperty("lblPad")) && comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		{
			if (filterComplex != "")
				filterComplex += "[c];[c]";
			
			String s[] = FFPROBE.imageResolution.split("x");
			if (caseRognage.isSelected())
				filterComplex += "pad=" + FFPROBE.imageResolution.replace("x", ":") + ":(ow-iw)*0.5:(oh-ih)*0.5";
			else
			{
				s = comboH264Taille.getSelectedItem().toString().split("x");
				filterComplex += "scale="+s[0]+":"+s[1]+":force_original_aspect_ratio=decrease,pad="+s[0]+":"+s[1]+":(ow-iw)*0.5:(oh-ih)*0.5";
			}
		}
		
		return filterComplex;
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

	protected static String setGamma() {		
		String yuv = "yuv";
		
        if (caseAlpha.isSelected())
        {
        	yuv += "a";
        }		

		if (caseColorspace.isSelected())
		{
        	if (comboColorspace.getSelectedItem().toString().contains("422"))
        		yuv += "422p";
        	else
            	yuv += "420p";
        	
			if (comboColorspace.getSelectedItem().toString().contains("10bits"))
				yuv += "10le";
		}
        else
        	yuv += "420p";
        
		return " -pix_fmt " + yuv;
	}

	protected static boolean analyse(File file) throws InterruptedException {
		
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
	
	protected static String setResolution() {		
		String resolution  = comboH264Taille.getSelectedItem().toString();		
        if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) && caseRognage.isSelected() == false)
            return "";
        else if (caseRognage.isSelected() && lblPad.getText().equals(language.getProperty("lblPad")))
        {
        	String s[] = comboH264Taille.getSelectedItem().toString().split("x");
        	String i[] = FFPROBE.imageResolution.split("x");
        	int ow = Integer.parseInt(s[0]);
        	int iw = Integer.parseInt(i[0]);
        	int ih = Integer.parseInt(i[1]);        	
        	
        	return " -s " + s[0] + "x" + (int) ((float)ow/((float)iw/ih));	
        }
        else
        	return " -s " + resolution;	
	}
	
	protected static String setColorspace() {
		if (caseColorspace.isSelected())
		{
			if (comboColorspace.getSelectedItem().toString().contains("Rec. 709"))
				return " -color_primaries bt709 -color_trc bt709 -colorspace bt709";
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 PQ"))
				return " -color_primaries bt2020 -color_trc smpte2084 -colorspace bt2020nc -profile:v 2";
			else if (comboColorspace.getSelectedItem().toString().contains("Rec. 2020 HLG"))
				return " -color_primaries bt2020 -color_trc arib-std-b67 -colorspace bt2020nc -profile:v 2";
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
		else if (caseForcerDesentrelacement.isSelected() || FFPROBE.entrelaced.equals("1"))
		{
			int doubler = 0;
			if (lblTFF.getText().equals("x2") && caseForcerDesentrelacement.isSelected())
				doubler = 1;
			
			return comboForcerDesentrelacement.getSelectedItem().toString() + "=" + doubler + ":" + FFPROBE.fieldOrder + ":0";
		}	
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
		
		String flags = " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
		
		if (caseFastStart.isSelected() && (comboFilter.getSelectedItem().toString().equals(".mp4") || comboFilter.getSelectedItem().toString().equals(".mov")))
			flags += " -movflags faststart";
		
		if (caseDecimate.isSelected())		
			flags += " -vsync vfr";	
		
		return flags;
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
		
	protected static String setDecimate(String filterComplex) {
		if (caseDecimate.isSelected())
		{			
			if (filterComplex != "")
				filterComplex += ",mpdecimate";	
			else
				filterComplex = "mpdecimate";	
		}

		return filterComplex;
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
				if (caseRognage.isSelected() && lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
					return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + SubtitlesWindow.textWidth.getText() + ":" + FFPROBE.cropWidth + "+" + SubtitlesWindow.spinnerSubtitlesPosition.getValue() + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
				else if (caseRognage.isSelected() == false && lblPad.getText().equals(language.getProperty("lblPad")) && comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = comboH264Taille.getSelectedItem().toString().split("x");
					int iw = Integer.parseInt(i[0]);
					int ih = Integer.parseInt(i[1]);
					int ow = Integer.parseInt(s[0]);
					int oh = Integer.parseInt(s[1]);        	
					
					int width = (int) ((float) Integer.parseInt(SubtitlesWindow.textWidth.getText()) / ((float) iw/ow));	        		        	
					int height = (int) ((float) (ih + Integer.parseInt(SubtitlesWindow.spinnerSubtitlesPosition.getValue().toString())) / ((float) ih/oh));
					
					return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + width + ":" + height + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
				}
				else
				{
					return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + SubtitlesWindow.textWidth.getText() + ":" + i[1] + "+" + SubtitlesWindow.spinnerSubtitlesPosition.getValue() + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
				}
			}
			else // ASS or SSA
			{
				String i[] = FFPROBE.imageResolution.split("x");
				SubtitlesWindow.textWidth.setText(i[0]); //IMPORTANT
				
				if (comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) == false)
				{
					String s[] = comboH264Taille.getSelectedItem().toString().split("x");
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
		
			//IMPORTANT ratio inf. à celui d'origine
        	if (caseRognage.isSelected() && lblPad.getText().equals(Shutter.language.getProperty("lblCrop")) && FFMPEG.ratioFinal < (float) Integer.parseInt(i[0]) / Integer.parseInt(i[1]))
        		i = String.valueOf(((int) (Integer.parseInt(i[1])*FFMPEG.ratioFinal) + "x" + i[1])).split("x");		
			
        	int ImageWidth = Integer.parseInt(i[0]);
        	
        	int posX = ((int) (ImageWidth - Integer.parseInt(SubtitlesWindow.textWidth.getText())) / 2);
        	if (caseRognage.isSelected() == false && lblPad.getText().equals(language.getProperty("lblPad")) && comboH264Taille.getSelectedItem().toString().equals(language.getProperty("source")) == false)
        	{
	        	String s[] = comboH264Taille.getSelectedItem().toString().split("x");
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

	protected static String showTimecode(String filterComplex, String fichier) {
		
		 String tc1 = FFPROBE.timecode1;
		 String tc2 = FFPROBE.timecode2;
		 String tc3 = FFPROBE.timecode3;
		 String tc4 = FFPROBE.timecode4;

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
       		if (filterComplex != "") filterComplex += ",";
       		filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + fichier + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
       	if (OverlayWindow.caseShowText.isSelected() && caseAddOverlay.isSelected())
       	{
       		if (filterComplex != "") filterComplex += ",";
       		filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + OverlayWindow.text.getText() + "':r=" + rate + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
	   	if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
	   	{
	   		String dropFrame = ":";
	   		if (caseConform.isSelected() == false && (FFPROBE.currentFPS == 29.97f || FFPROBE.currentFPS == 59.94f) || caseConform.isSelected() && (comboFPS.getSelectedItem().toString().equals("29,97") || comboFPS.getSelectedItem().toString().equals("59,94")))
	   			dropFrame = ";";
	   			
	   		if (filterComplex != "") filterComplex += ",";
	   		
	   		if (OverlayWindow.caseAddTimecode.isSelected() && OverlayWindow.lblTimecode.getText().equals(Shutter.language.getProperty("lblFrameNumber")))
	   		{
	   			String startNumber = String.format("%.0f", Integer.parseInt(tc1) * 3600 * FFPROBE.currentFPS + Integer.parseInt(tc2) * 60 * FFPROBE.currentFPS + Integer.parseInt(tc3) * FFPROBE.currentFPS + Integer.parseInt(tc4));
	   			filterComplex += "drawtext=" + OverlayWindow.font + ":text='%{frame_num}': start_number=" + startNumber + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc;
	   		}
	   		else
				filterComplex += "drawtext=" + OverlayWindow.font + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\" + dropFrame + tc4 + "':r=" + rate + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc + ":tc24hmax=1";	      
	   	}   
	   
		return filterComplex;
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
		if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
			return " -r " + FFPROBE.currentFPS;
		else if (caseConform.isSelected())
	    	return " -r " + Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));            
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
		{
			sortie = lblDestination1.getText();
			
			if (caseCreateTree.isSelected())
			{ 	
				File pathToFile = null;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					String s[] = file.getParent().toString().split("/");
					pathToFile = new File(lblDestination1.getText() + file.getParent().toString().replace("/Volumes", "").replace(s[2], ""));	
				}
				else
					pathToFile = new File (lblDestination1.getText() + file.getParent().toString().substring(2));
				
				if (pathToFile.exists() == false)
					pathToFile.mkdirs();
				
				sortie = pathToFile.toString();
			}
		}
		else
		{
			sortie =  file.getParent();
			lblDestination1.setText(sortie);
		}
		
		return sortie;
	}
	
	public static void listFilesForFolder(final String fichier, final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	if (fileEntry.getName().contains(fichier) && fileEntry.getName().contains("log"))
	        	{
	        		File fileToDelete = new File(fileEntry.getAbsolutePath());
	        		fileToDelete.delete();
	        	}
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
		
		//Bout à bout
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			VP9.main(true);
			return true;
		}
		return false;
	}

}//Class