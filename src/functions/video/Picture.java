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

import application.Wetransfer;
import library.DCRAW;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.XPDF;
import application.Ftp;
import application.Settings;
import application.Shutter;
import application.Utils;
import application.WatermarkWindow;

public class Picture extends Shutter {
	
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
					lblEncodageEnCours.setText(fichier);					
					
					String extension =  fichier.substring(fichier.lastIndexOf("."));	
						
					//Erreur FFPROBE avec les fichiers RAW
					boolean isRaw = false;
					switch (extension.toLowerCase()) { 
						case ".3fr":
						case ".arw":
						case ".crw":
						case ".cr2":
						case ".cr3":
						case ".dng":
						case ".kdc":
						case ".mrw":
						case ".nef":
						case ".nrw":
						case ".orf":
						case ".ptx":
						case ".pef":
						case ".raf":
						case ".r3d":
						case ".rw2":
						case ".srw":
						case ".x3f":
							isRaw = true;
					}		
					
					//Analyse des données
					if (analyse(file, extension, isRaw) == false)
						continue;
			 
					//Filtre dates
					if (imageFilter(file) == false)
						continue;
					
			        //Framerate
					String frameRate = setFramerate();
					
		            //Deinterlace
					String filter = setDeinterlace(extension, isRaw);
					
	            	//Logo
			        String logo = setLogo();	
					
			    	//Watermak
					filter = setWatermark(filter);
					
					//Framesize
					filter = setCrop(filter);	
					
					//Framesize
					filter = setFramesize(filter);	           				

					//Rotate
					filter = setRotate(filter);
					
					//Display
					filter = setDisplay(filter, fichier);
					
					//LUTs
					filter = setLUT(filter);
					
					//Color
					filter = setColor(filter);
					
	            	//filterComplex
					filter = setFilterComplex(filter);		
					
					//Colorspace
		            String colorspace = setColorspace();
					
					//Compression
					String compression = setCompression();
					
					//Dossier de sortie
					String sortie = setSortie("", file);				
					
					//Extension
					String newExtension = setExtension();
										
					//singleFrame
					String singleFrame = setFrame();					
					
					final String sortieFichier =  sortie + "/" + fichier.replace(extension, newExtension); 		
					
					//InOut		
					FFMPEG.fonctionInOut();
					
					//Si le fichier existe
					File fileOut = new File(sortieFichier);
					if(fileOut.exists())
					{						
						fileOut = Utils.fileReplacement(sortie, fichier, extension, "_", newExtension);
						if (fileOut == null)
							continue;	
					}
					
					//Envoi de la commande
					String cmd = filter + singleFrame + colorspace + compression + " -an -y ";
					if (extension.toLowerCase().equals(".pdf"))
					{
						for (int p = 1 ; p < XPDF.pagesCount + 1 ; p++)
						{
							int n = 1;
							do {
								fileOut = new File(sortie + "/" + fichier.replace(extension, "_" + n + newExtension));
								n++;
							} while (fileOut.exists());
							
							if (cancelled == false)
								XPDF.run(" -r 300 -f " + p + " -l " + p + " " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + logo + cmd + '"' + fileOut + '"');
							
							//Attente de la fin de FFMPEG
							do
								Thread.sleep(100);
							while(XPDF.runProcess.isAlive());
						}						
						btnStart.setEnabled(true);	
					}
					else if (isRaw)
					{
						btnStart.setEnabled(false);	
						DCRAW.run(" -v -w -c -q 0 -6 -g 2.4 12.92 " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + logo + cmd + '"' + fileOut + '"');
					}
					else
						FFMPEG.run(FFMPEG.inPoint + frameRate + " -i " + '"' + file.toString() + '"' + logo + FFMPEG.postInPoint + FFMPEG.outPoint + cmd + '"' + fileOut + '"');		
					
					//Attente de la fin de FFMPEG
					if (isRaw)
					{
						do
							Thread.sleep(100);
						while(DCRAW.runProcess.isAlive());
						
						btnStart.setEnabled(true);	
					}
					else
					{
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());
					}
					
					if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					{
						if (actionsDeFin(fichier, extension, fileOut, sortie))
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
	
	protected static boolean analyse(File file, String extension, boolean isRaw) throws InterruptedException {
		
		btnStart.setEnabled(false);	

		 EXIFTOOL.run(file.toString());	
		 do
		 	Thread.sleep(100);						 
		 while (EXIFTOOL.isRunning);
		 
		 if (errorAnalyse(file.toString()))
			 return false;
			 
		if (isRaw == false && extension.toLowerCase().equals(".pdf") == false)
		{
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
		}
		else if (extension.toLowerCase().equals(".pdf"))
		{
			 XPDF.info(file.toString());	
			 do
			 	Thread.sleep(100);						 
			 while (XPDF.isRunning);
			 
			 if (errorAnalyse(file.toString()))
				 return false;
			 
			 XPDF.toFFPROBE(file.toString());	
			 do
			 	Thread.sleep(100);						 
			 while (XPDF.isRunning);
			 
			 if (errorAnalyse(file.toString()))
				 return false;
		}
		 
		 return true;
	}

	protected static String setFramerate() {
		if (caseCreateSequence.isSelected())	            		
	    	return " -r " + (float) FFPROBE.currentFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));          

		return "";
	}
	
	protected static boolean imageFilter(File file) throws InterruptedException {
	
		if (caseYear.isSelected() || caseMonth.isSelected() || caseDay.isSelected() || caseFrom.isSelected())
		{
			 EXIFTOOL.run(file.toString());	
			 do
			 	Thread.sleep(100);						 
			 while (EXIFTOOL.isRunning);
			 
			String date[] = EXIFTOOL.exifDate.split(":");
			if (caseYear.isSelected())
			{
				if (comboYear.getSelectedItem().toString().equals(date[0]) == false)
					return false;
			}
			if (caseMonth.isSelected())
			{
				if (comboMonth.getSelectedItem().toString().equals(date[1]) == false)
					return false;
			}
			if (caseDay.isSelected())
			{
				if (comboDay.getSelectedItem().toString().equals(date[2]) == false)
					return false;
			}		
			if (caseFrom.isSelected())
			{
				String exif[] = EXIFTOOL.exifHours.split(":");
				String from[] = comboFrom.getSelectedItem().toString().split(":");
				String to[] = comboTo.getSelectedItem().toString().split(":");
				
				int eH = Integer.parseInt(exif[0]) * 60;
				int eM = Integer.parseInt(exif[1]);
				int exifTime = eH + eM;
				
				int fH = Integer.parseInt(from[0]) * 60;
				int fM = Integer.parseInt(from[1]);
				int fromTime = fH + fM;
				
				int tH = Integer.parseInt(to[0]) * 60;
				int tM = Integer.parseInt(to[1]);
				int toTime = tH + tM;
				
				if ((exifTime >= fromTime && exifTime <= toTime) == false)
					return false;
			}
		}
		return true;
	}
	
	protected static String setDeinterlace(String extension, boolean isRaw) {	
		if (isRaw || extension.toLowerCase().equals(".pdf"))
			return "";
		else
		{
			if (FFPROBE.entrelaced.equals("1"))
				return "yadif=0:" + FFPROBE.fieldOrder + ":0";		
			else
				return "";
		}
	}
	
	protected static String setLogo() {
		if (caseLogo.isSelected())	        	
			return " -i " + '"' + WatermarkWindow.logoFile + '"'; 
		else
			return "";

	}
	
	protected static String setWatermark(String filter) {
        if (caseLogo.isSelected())
        {		        	
        	if (filter != "") 	
        	{
        		filter += "[v];[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        	else
        	{
        		filter = "[1:v]scale=iw*" + ((float)  Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) + ":ih*" + ((float) Integer.parseInt(WatermarkWindow.textSize.getText()) / 100) +			
        				",lut=a=val*" + ((float) Integer.parseInt(WatermarkWindow.textOpacity.getText()) / 100) + 
        				"[scaledwatermark];[0:v][scaledwatermark]overlay=" + WatermarkWindow.textPosX.getText() + ":" + WatermarkWindow.textPosY.getText();
        	}
        }
        
		return filter;
	}
	
	protected static String setCrop(String filter) {
		
		if (caseRognerImage.isSelected())
		{
			if (filter != "")
				filter += "," + cropFinal;
			else	
				filter = cropFinal;
		}		
		return filter;
	}
	
	protected static String setFramesize(String filter) {
		
		String frameSize = "";
		
		if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
		{
	        if (comboResolution.getSelectedItem().toString().contains(":"))
	        {
	        	if (comboResolution.getSelectedItem().toString().contains("auto"))
	        	{
	        		String s[] = comboResolution.getSelectedItem().toString().split(":");
	        		if (s[0].toString().equals("auto"))
	        			frameSize = "scale=-1:" + s[1];
	        		else
	        			frameSize = "scale="+s[0]+":-1";
	        	}
	        	else
	        	{
		            String s[] = comboResolution.getSelectedItem().toString().split(":");
		    		float number =  (float) 1 / Integer.parseInt(s[0]);
		    		frameSize = "scale=iw*" + number + ":ih*" + number;
	        	}
	        }
	        else
	        {
	        	String i[] = FFPROBE.imageResolution.split("x");
	        	String o[] = comboResolution.getSelectedItem().toString().split("x");         	
	
	        	int iw = Integer.parseInt(i[0]);
	        	int ih = Integer.parseInt(i[1]);          	
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);        	
	        	float ir = (float) iw / ih;
	        	
	        	//Original sup. à la sortie
	        	if (iw > ow && ih > oh)
	        	{
	        		//Si la hauteur calculée est > à la hauteur de sortie
	        		if ( (float) ow / ir >= oh)
	        			frameSize = "scale=" + ow + ":-1,crop=" + "'" + ow + ":" + oh + ":0:(ih-oh)*0.5" + "'";
	        		else
	        			frameSize = "scale=-1:" + oh + ",crop=" + "'" + ow + ":" + oh + ":(iw-ow)*0.5:0" + "'";
	        	}
	        	else
	        		frameSize = "scale=" + ow + ":" + oh;
	        }
			
		}
		else
		{
			if (comboFilter.getSelectedItem().toString().equals(".ico"))
				frameSize = "scale=256x256";
		}
		
		if (frameSize != "")
		{
			if (filter != "")
				filter += "," + frameSize;
			else	
				filter = frameSize;
		}
		
		return filter;
	}
	
	protected static String setRotate(String filter) {
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
			if (filter != "")
				filter += "," + rotate;
			else	
				filter = rotate;	
		}

		return filter;
	}
	
	protected static String setDisplay(String filterComplex, String fichier) {
		
      	if (caseShowDate.isSelected())
      	{
      		if (filterComplex != "")
      			filterComplex += ",drawtext=fontfile=" + Shutter.pathToFont + ":text='" + EXIFTOOL.exifDate.replace(":", "-") + "':r=" + FFPROBE.currentFPS + ":" + '"' + "x=(w-tw)*0.5:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
      		else
      			filterComplex = "drawtext=fontfile=" + Shutter.pathToFont + ":text='" + EXIFTOOL.exifDate.replace(":", "-") + "':r=" + FFPROBE.currentFPS + ":" + '"' + "x=(w-tw)*0.5:y=h-(2*lh)" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
      	}
      	
	   	if (caseShowFileName.isSelected())
	   	{
      		if (filterComplex != "") 
      			filterComplex += ",drawtext=fontfile=" + Shutter.pathToFont + ":text='" + fichier + "':r=" + FFPROBE.currentFPS + ":" + '"' + "x=(w-tw)*0.5:y=lh" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
      		else
      			filterComplex = "drawtext=fontfile=" + Shutter.pathToFont + ":text='" + fichier + "':r=" + FFPROBE.currentFPS + ":" + '"' + "x=(w-tw)*0.5:y=lh" + '"' + ":fontcolor=white:fontsize=w*0.0422:box=1:boxcolor=0x00000099";
      	}
	   
		return filterComplex;
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
	
	protected static String setFilterComplex(String filterComplex) {
        if (filterComplex != "")   	   
        	filterComplex = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"';
        
        return filterComplex;
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
	
	protected static String setCompression() {
		
		if (comboFonctions.getSelectedItem().equals("JPEG"))
		{
			int q = Math.round((float) 31 - (float) ((float) ((float) Integer.valueOf(comboFilter.getSelectedItem().toString().replace("%", "")) * 31) / 100));
			return " -q:v " + q;
		}
		else if (comboFilter.getSelectedItem().toString().equals(".webp"))
			return " -quality 100";
		else
			return " -q:v 0";
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
	
	protected static String setExtension() {			
		if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionPicture")))
		{
			if (caseCreateSequence.isSelected())
				return "_%06d" + comboFilter.getSelectedItem().toString();
			else
				return comboFilter.getSelectedItem().toString();
		}
		else if (comboFonctions.getSelectedItem().equals("JPEG") && caseCreateSequence.isSelected())
				return "_%06d.jpg";
		
		return ".jpg";		
	}

	protected static String setFrame() {
		if (caseCreateSequence.isSelected())
			return " -r 1";
		else
			return " -vframes 1";
	}

	private static boolean errorAnalyse(String fichier)
	{
		 if (FFMPEG.error)
		 {
				FFMPEG.errorList.append(fichier);
			    FFMPEG.errorList.append(System.lineSeparator());
				return true;
		 }
		 return false;
	}
	
	private static boolean actionsDeFin(String fichier, String extension, File fileOut, String sortie) {		
		//Erreurs
		if (FFMPEG.error || fileOut.length() == 0 && caseCreateSequence.isSelected() == false && extension.toLowerCase().equals(".pdf") == false)
		{
			FFMPEG.errorList.append(fichier);
		    FFMPEG.errorList.append(System.lineSeparator());
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}

		//Annulation
		if (cancelled && caseCreateSequence.isSelected() == false && extension.toLowerCase().equals(".pdf") == false)
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
			Picture.main();
			return true;
		}
		return false;
	}

}//Class
