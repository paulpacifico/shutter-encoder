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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import application.Ftp;
import application.VideoPlayer;
import application.WatermarkWindow;
import application.OverlayWindow;
import application.Settings;
import application.Shutter;
import application.SubtitlesWindow;
import application.Utils;
import application.Wetransfer;
import library.DVDAUTHOR;
import library.FFMPEG;
import library.FFPROBE;

public class DVD extends Shutter {
	
	
	
	
	private static int complete;
	public static boolean multiplesPass;
	
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
		            
					try{
					String fichier = file.getName();
					lblEncodageEnCours.setText(fichier);
					
					//Analyse des données
					if (analyse(file) == false)
						continue;			
					 		           
					//Dossier de sortie
					String sortie = setSortie("", file);
					
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					
					//Création du répertoire pour les fichiers composants le dvd
					File dvdFolder = new File(sortie + "/" + fichier.replace(extension, ""));
					dvdFolder.mkdir();
	
					String sortieFichier =  dvdFolder.toString().replace("\\", "/") + "/" + fichier.replace(extension, ".mpg") ; 
					
					//InOut		
					FFMPEG.fonctionInOut();
					
					//Subtitles
					String subtitles = setSubtitles();
					
					//Interlace
			        String interlace = setInterlace();		
			        
					//Bitrate
			        Integer bitrate = setBitrate(file.toString());	
			        
			        //2Pass
			        String pass = setPass(bitrate, sortieFichier);
			        
	            	//Desentrelacement
		            String filterComplex = setDeinterlace("");

					//Deband
					filterComplex = setDeband(filterComplex);		            
					
		            //Détails
	            	filterComplex = setDetails(filterComplex);	            	
	            	
		            //Flags
		    		String flags = setFlags();
	            	
					//Bruit
	            	filterComplex = setDenoiser(filterComplex);
		            
	            	//Timecode
					filterComplex = setTimecode(filterComplex, fichier);
					
	            	//Logo
			        String logo = setLogo();
	            			            
			    	//Watermak
					filterComplex = setWatermark(filterComplex);
					
					//Overlay
					filterComplex = setOverlay(filterComplex);
	            			            
					//Audio
					String audio = setAudio(filterComplex);	
					
	            	//filterComplex
					filterComplex = setFilterComplex(filterComplex, audio);	
	            			            							
					String output = '"' + sortieFichier + '"';										
					/*if (caseVisualiser.isSelected())
						output = "-f tee " + '"' + sortieFichier + "|[f=dvd]pipe:play" + '"';*/
										
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{
						int q = JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("eraseFile"), Shutter.language.getProperty("theFile") + " " + fichier.replace(extension, ".mpg") + " " + Shutter.language.getProperty("alreadyExist"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (q == JOptionPane.NO_OPTION)
							continue;					
						else if (q == JOptionPane.CANCEL_OPTION)
						{
							cancelled = true;
							break;
						}
						
					}
									
					//Envoi de la commande
					String cmd = pass + " " + filterComplex + " -aspect 16:9 -target pal-dvd -b:v " + bitrate + "k -f dvd" + interlace + flags + " -y ";
					FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + output);			
				
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					if (FFMPEG.cancelled == false && pass != "")
						FFMPEG.run(FFMPEG.inPoint + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd.replace("-pass 1", "-pass 2") + output);	
					
					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					//Création des fichiers VOB
					if (cancelled == false && FFMPEG.error == false)
					{
						lblEncodageEnCours.setText(Shutter.language.getProperty("createBurnFiles"));
						makeVOBfiles(dvdFolder, sortieFichier);
					}

					//SUPPRESSION DES FICHIERS RESIDUELS
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						listFilesForFolder(dvdFolder);
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						if (actionsDeFin(fichier, dvdFolder, sortie))
						break;
					}
					
					} catch (InterruptedException | IOException e) {
						FFMPEG.error  = true;
					}//End Try
				}//End For	

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					FinDeFonction();
			}//run
			
		});
		thread.start();
		
    }//main

	protected static int setBitrate(String file) throws InterruptedException {
		FFPROBE.Data(file);
		do {
			Thread.sleep(100);
		} while (FFPROBE.dureeTotale == 0 && FFPROBE.isRunning);
		float debit = (float) ((float) 4000000 / FFPROBE.dureeTotale) * 8;
		
		NumberFormat formatter = new DecimalFormat("0000");
		
		if (debit > 8)
			return 8000;
		else
			return Integer.parseInt(formatter.format(debit * 1000));
	}
	
	protected static String setPass(Integer bitrate, String sortieFichier) {
		if (bitrate <= 6000)
		{
			multiplesPass = true;
			return " -pass 1 -passlogfile " + '"' + sortieFichier + '"';
		}
		else
		{
			multiplesPass = false;
			return "";
		}
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

	protected static String setInterlace() {
        if (caseForcerProgressif.isSelected())
        	return "";
        else
        	return " -flags +ildct -top 1";
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
	
	protected static String setDetails(String filterComplex) {
    	
    	if (caseDetails.isSelected())
    	{
			float value = (0 - (float) sliderDetails.getValue() / 10);
			
    		if (filterComplex != "") filterComplex += ",";
    			filterComplex += "smartblur=1.0:" + value; 
    	}
		return filterComplex;
	}
	
	protected static String setDeinterlace(String filterComplex) {
    	if (FFPROBE.entrelaced.equals("1") && caseForcerProgressif.isSelected())
    		filterComplex += "yadif=0:" + FFPROBE.fieldOrder + ":0"; 
    	
    	return filterComplex;
	}

	protected static String setDenoiser(String filterComplex) {
		if (caseBruit.isSelected())
		{
			int value = sliderBruit.getValue();
			
			if (filterComplex != "") filterComplex += ",";
				filterComplex += "hqdn3d=" + value + ":" + value + ":" + value + ":" + value;
		}
		return filterComplex;
	}

	protected static String setTimecode(String filterComplex, String fichier) {
		
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
       
       	if (OverlayWindow.caseShowFileName.isSelected() && caseAddOverlay.isSelected())
       	{
       		if (filterComplex != "") filterComplex += ",";
       		filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + fichier + "':r=" + FFPROBE.currentFPS + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
       	if (OverlayWindow.caseShowText.isSelected() && caseAddOverlay.isSelected())
       	{
       		if (filterComplex != "") filterComplex += ",";
       		filterComplex += "drawtext=" + OverlayWindow.font + ":text='" + OverlayWindow.text.getText() + "':r=" + FFPROBE.currentFPS + ":x=" + OverlayWindow.textNamePosX.getText() + ":y=" + OverlayWindow.textNamePosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaName + ":fontsize=" + OverlayWindow.spinnerSizeName.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexName;
       	}
       	
	   	if ((OverlayWindow.caseAddTimecode.isSelected() || OverlayWindow.caseShowTimecode.isSelected()) && caseAddOverlay.isSelected())
	   	{
	   		if (filterComplex != "") filterComplex += ",";
	   		filterComplex += "drawtext=" + OverlayWindow.font + ":timecode='" + tc1 + "\\:" + tc2 + "\\:" + tc3 + "\\:" + tc4 + "':r=" + FFPROBE.currentFPS + ":x=" + OverlayWindow.textTcPosX.getText() + ":y=" + OverlayWindow.textTcPosY.getText() + ":fontcolor=0x" + OverlayWindow.hex + OverlayWindow.hexAlphaTc + ":fontsize=" + OverlayWindow.spinnerSizeTC.getValue() + ":box=1:boxcolor=0x" + OverlayWindow.hex2 + OverlayWindow.hexTc + ":tc24hmax=1";	      
	   	}  
	   
		return filterComplex;
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
	
	protected static String setSubtitles() {
    	if (caseSubtitles.isSelected() && subtitlesBurn)
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
			return " -f lavfi" + FFMPEG.inPoint + " -i " + '"' + "color=black@0.0,format=rgba,scale=" + SubtitlesWindow.textWidth.getText() + ":" + i[1] + "+" + SubtitlesWindow.spinnerSubtitlesPosition.getValue() + ",subtitles=" + "'" + subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + SubtitlesWindow.comboFont.getSelectedItem().toString() + ",FontSize=" + SubtitlesWindow.spinnerSize.getValue() + ",PrimaryColour=&H" + SubtitlesWindow.hex + "&" + background + "'" + '"';
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
	
	protected static String setAudio(String filterComplex) {
		if (debitAudio.getSelectedItem().toString().equals("0") || comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio")))
			return " -an";
		else
		{
			String audio = "";
			String audioCodec = "ac3";
			String newAudio = "";	        
	        if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))))     
	        {
	        	float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
	        	float value = (float) (newFPS/ FFPROBE.currentFPS);
	        	if (value < 0.5f || value > 2.0f)
	        		return " -an";
	        	else
	        		newAudio = "atempo=" + value;	
	        }
			
		    if (FFPROBE.stereo)
		    {
		    	if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
		    	
		    	if (FFPROBE.surround)
		    	{			    	
			    	if (lblAudioMapping.getText().equals("Multi"))
			    	{
				    	FFPROBE.stereo = false; //permet de contourner le split audio				    	
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k" + newAudio + " -map a:0";
			    	}
				    else	
				    {
				    	if (newAudio != "") 
				    		newAudio = newAudio.replace(" -filter:a", "").replace("\"", "") + ",";
				    	
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k -filter:a " + '"' + newAudio + "pan=stereo|FL=FC+0.30*FL+0.30*BL|FR=FC+0.30*FR+0.30*BR" + '"' + " -map a?";
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
				    
				    audio += "[a:0]pan=1c|c0=c" + comboAudio1.getSelectedIndex() + newAudio + "[a1];[a:0]pan=1c|c0=c" + comboAudio2.getSelectedIndex() + newAudio + "[a2]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k";
		    	}
		    	else
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k" + newAudio + " -map a:0";
		    }
		    else if (FFPROBE.channels > 1)
		    {
		    	 if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
    			 {
			    	if (newAudio != "")
			    		newAudio = "," + newAudio;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
			    	audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + newAudio + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k";    		 
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
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k" + newAudio + mapping;
		    	 }		    	 
		    }
		    else
		    {
		    	if (newAudio != "") newAudio = " -filter:a " + '"' + newAudio + '"';
		    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a 320k" + newAudio + " -map a?";
		    }
		    
		    return audio;		   				    
		}
	}

	protected static String setFilterComplex(String filterComplex, String audio) {
		
        if (filterComplex != "")
        {	   	   
        	//Si une des cases est sélectionnée alors il y a déjà [0:v]
        	if (caseLogo.isSelected() || (caseSubtitles.isSelected() && subtitlesBurn))
        		filterComplex = " -filter_complex " + '"' + filterComplex + "[out]";
        	else
        		filterComplex = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]";
    		
        	if (FFPROBE.channels > 1 && lblAudioMapping.getText().equals(language.getProperty("stereo")) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && debitAudio.getSelectedItem().toString().equals("0") == false)
        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"'+ "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
        	else
        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        }
        else
        {        	        	
        	if (FFPROBE.channels > 1 && lblAudioMapping.getText().equals(language.getProperty("stereo")) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
        		filterComplex = audio + " -map v -map " + '"' +  "[a]" + '"';
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && debitAudio.getSelectedItem().toString().equals("0") == false)
        		filterComplex = audio + " -map v -map " + '"'+ "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
        	else
        		filterComplex = " -map v" + audio;
        }
        
		//On map les sous-titres que l'on intègre        
        if (caseSubtitles.isSelected() && subtitlesBurn == false)
        	filterComplex += " -map 1:s -c:s mov_text";
		
        return filterComplex;
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
	
	private static void makeVOBfiles(File dvdFolder, String fichierMpeg) throws IOException, InterruptedException {
			String PathToDvdXml = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (System.getProperty("os.name").contains("Windows"))
				PathToDvdXml = PathToDvdXml.substring(1,PathToDvdXml.length()-1);
			else
				PathToDvdXml = PathToDvdXml.substring(0,PathToDvdXml.length()-1);
			
			PathToDvdXml = PathToDvdXml.substring(0,(int) (PathToDvdXml.lastIndexOf("/"))).replace("%20", " ") + "/Library/dvd.xml"; //Old XML
			String copyXML = dvdFolder.toString() + "/dvd.xml"; //New XML

			FileReader reader = new FileReader(PathToDvdXml);			
			BufferedReader oldXML = new BufferedReader(reader);
			
			FileWriter writer = new FileWriter(copyXML);
			
			String line;
			while ((line = oldXML.readLine()) != null)
			{
				if (line.contains("path"))
					writer.write(line.replace("path", fichierMpeg) + System.lineSeparator());
				else					
					writer.write(line + System.lineSeparator());
			}
			
				reader.close();
				oldXML.close();
				writer.close();		
				
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					DVDAUTHOR.run("-o " + '"' + dvdFolder.toString() + "/" + '"' + " -x " + '"' + dvdFolder.toString() + "/dvd.xml" + '"');
				else
					DVDAUTHOR.run("-o " + '"' + dvdFolder.toString() + '"' + " -x " + '"' + dvdFolder.toString() + "/dvd.xml" + '"');	
					
                do {
                	Thread.sleep(100);
                } while (DVDAUTHOR.isRunning);
	}
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String ext = fileEntry.toString().substring(fileEntry.toString().lastIndexOf("."));
	        	if (ext.equals(".log") || ext.equals(".xml") || ext.equals(".mpg"))
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
		if (FFMPEG.error)
		{
			FFMPEG.errorList.append(fichier);
		    FFMPEG.errorList.append(System.lineSeparator());
		}

		//Annulation
		if (cancelled || FFMPEG.error)
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
		
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			DVD.main();
			return true;
		}
		return false;
	}
}//Class
