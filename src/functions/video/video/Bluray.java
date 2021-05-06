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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

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
import library.FFMPEG;
import library.FFPROBE;
import library.TSMUXER;

public class Bluray extends Shutter {
	
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
						if (Utils.waitFileCompleted(file) == false)
							break;
		            }
					//SCANNING
		            
					try{
					String fichier = file.getName();
					final String extension =  fichier.substring(fichier.lastIndexOf("."));
					lblEncodageEnCours.setText(fichier);
					
					// Analyse des données
					if (analyse(file) == false)
						continue;						

					//InOut		
					FFMPEG.fonctionInOut();
					
					//Subtitles
					String subtitles = setSubtitles();
					
					//Codec
					String codec = setCodec();
					
					 //Interlace
		            String interlace = setInterlace();
		            
		            //Bitrate
			        String bitrate = setBitrate();	
		            
					//Resolution
					String resolution = setResolution();	
			        
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
					
	            	//Logo
			        String logo = setLogo();
	            			            
			    	//Watermark
					filterComplex = setWatermark(filterComplex);
					
	            	//Timecode
					filterComplex = showTimecode(filterComplex, fichier.replace(extension, ""));
					
			    	//Rognage
			        filterComplex = setCrop(filterComplex);
					
					//Padding
					filterComplex = setPad(filterComplex);
					
					//Overlay
					filterComplex = setOverlay(filterComplex);
	            			       
					//Fade-in Fade-out
					filterComplex = setFade(filterComplex);
					
					//Audio
					String audio = setAudio(filterComplex);	
	            						
	            	//filterComplex
					filterComplex = setFilterComplex(filterComplex, audio);	

			       	//Preset
			        String preset = setPreset();
					
					//Dossier de sortie
					String sortie = setSortie("", file);
										
					//Création du répertoire pour les fichiers composants le bluray
					File blurayFolder = new File(sortie + "/" + fichier.replace(extension, ""));
					blurayFolder.mkdir();
					
					String sortieFichier =  blurayFolder.toString().replace("\\", "/") + "/" + fichier.replace(extension, ".mkv") ; 
					
			        //2pass
			        String pass = setPass(sortieFichier);
					
					String output = '"' + sortieFichier + '"';										
					if (caseDisplay.isSelected())
						output = "-flags:v +global_header -f tee " + '"' + sortieFichier + "|[f=matroska]pipe:play" + '"';
						
					//Mode concat
					String concat = FFMPEG.setConcat(file, sortie);					
					if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
						file = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt"));
						
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{
						int q = JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("eraseFile"), Shutter.language.getProperty("theFile") + " " + fichier.replace(extension, ".mkv") + " " + Shutter.language.getProperty("alreadyExist"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						if (q == JOptionPane.NO_OPTION)
							continue;					
						else if (q == JOptionPane.CANCEL_OPTION)
						{
							cancelled = true;
							break;
						}
						
					}
									
					//Envoi de la commande
					String cmd = pass + filterComplex + codec + resolution + preset + " -pix_fmt yuv420p -tune film -level 4.1 -x264opts bluray-compat=1:force-cfr=1:weightp=0:bframes=3:ref=3:nal-hrd=vbr:vbv-maxrate=40000:vbv-bufsize=30000:bitrate=" + bitrate + ":keyint=60:b-pyramid=strict:slices=4" + interlace + ":aud=1:colorprim=bt709:transfer=bt709:colormatrix=bt709 -r " + FFPROBE.currentFPS + flags + " -y ";
					FFMPEG.run(FFMPEG.inPoint + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + output);			

					//Attente de la fin de FFMPEG
					do
							Thread.sleep(100);
					while(FFMPEG.runProcess.isAlive());
					
					if (case2pass.isSelected())
					{						
						if (FFMPEG.cancelled == false)
							FFMPEG.run(FFMPEG.inPoint + concat + " -i " + '"' + file.toString() + '"' + logo + subtitles + FFMPEG.postInPoint + FFMPEG.outPoint + cmd.replace("-pass 1", "-pass 2") + output);	
						
						//Attente de la fin de FFMPEG
						do
								Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
					}
					
					//Création des fichiers VOB
					if (cancelled == false && FFMPEG.error == false)
					{
						lblEncodageEnCours.setText(Shutter.language.getProperty("createBurnFiles"));
						makeBDMVfiles(blurayFolder, sortieFichier);
					}

					//SUPPRESSION DES FICHIERS RESIDUELS
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						listFilesForFolder(fichier.replace(extension, ""),blurayFolder);
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						if (actionsDeFin(fichier, blurayFolder, sortie))
						break;
					}
					
					} catch (Exception e) {
						System.out.println(e);
						FFMPEG.error  = true;
					}//End Try
				}//End For	

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}//run
			
		});
		thread.start();
		
    }//main

	protected static String setBitrate() 
	{		
		return debitVideo.getSelectedItem().toString();
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
        if (FFPROBE.currentFPS == 24.0 || FFPROBE.currentFPS == 23.98 || caseForcerProgressif.isSelected())
        	return "";
        else
        	return ":tff=1";
	}

	protected static String setFlags() {
   
		return " -sws_flags " + Settings.comboScale.getSelectedItem().toString();
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
	
	protected static String setPreset() {
        if (caseQMax.isSelected())
	        return " -preset veryslow";
        else if (caseForcePreset.isSelected())
           	return " -preset " + comboForcePreset.getSelectedItem().toString();
        else
			return "";
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
			String audioCodec = "ac3";
			
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
		    	 if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
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

	protected static String setCodec() {
		return " -c:v libx264";
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
    		
        	if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
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
        	if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && debitAudio.getSelectedItem().toString().equals("0") == false && FFPROBE.stereo == false)
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
        	
        	filterComplex += map + " -c:s srt -metadata:s:s:0 language=" + loc.getISO3Language();
        }
        
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
	
	private static void makeBDMVfiles(File blurayFolder, String fichierMkv) throws IOException, InterruptedException {
			String PathToblurayMeta = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			if (System.getProperty("os.name").contains("Windows"))
				PathToblurayMeta = PathToblurayMeta.substring(1,PathToblurayMeta.length()-1);
			else
				PathToblurayMeta = PathToblurayMeta.substring(0,PathToblurayMeta.length()-1);
			
			PathToblurayMeta = PathToblurayMeta.substring(0,(int) (PathToblurayMeta.lastIndexOf("/"))).replace("%20", " ") + "/Library/bluray.meta"; //Old meta
			String copymeta = blurayFolder.toString() + "/bluray.meta"; //New meta

			FileReader reader = new FileReader(PathToblurayMeta);			
			BufferedReader oldmeta = new BufferedReader(reader);
			
			FileWriter writer = new FileWriter(copymeta);
			
			String line;
			while ((line = oldmeta.readLine()) != null)
			{
				if (line.contains("file"))
					writer.write(line.replace("file", fichierMkv) + System.lineSeparator());
				else					
					writer.write(line + System.lineSeparator());
			}
			
				reader.close();
				oldmeta.close();
				writer.close();		
				
				TSMUXER.run('"' + blurayFolder.toString().replace("\\", "/") + "/bluray.meta" + '"' + " " + '"' + blurayFolder.toString().replace("\\", "/") + '"');	
					
                do {
                	Thread.sleep(100);
                } while (TSMUXER.isRunning);
	}
	
	public static void listFilesForFolder(final String fichier, final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String ext = fileEntry.toString().substring(fileEntry.toString().lastIndexOf("."));
	        	if (ext.equals(".meta") || ext.equals(".mkv") || (fileEntry.getName().contains(fichier) && fileEntry.getName().contains("log")))
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
		
		//Mode concat
		if (Settings.btnSetBab.isSelected() || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
		{		
			final String extension =  fichier.substring(fichier.lastIndexOf("."));
			File listeBAB = new File(sortie.replace("\\", "/") + "/" + fichier.replace(extension, ".txt")); 			
			listeBAB.delete();
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
		
		//Bout à bout
		if (Settings.btnSetBab.isSelected())
			return true;
		
		//Scan
		if (Shutter.scanIsRunning)
		{
			Utils.moveScannedFiles(fichier);
			Bluray.main();
			return true;
		}
		return false;
	}

}//Class
