/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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

package settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import application.Console;
import application.RecordInputDevice;
import application.RenderQueue;
import application.Settings;
import application.Shutter;
import application.SubtitlesEmbed;
import application.Utils;
import application.VideoPlayer;
import library.EXIFTOOL;
import library.FFMPEG;
import library.FFPROBE;
import library.MEDIAINFO;
import library.XPDF;

public class FunctionUtils extends Shutter {

	public static int completed;
	public static boolean yesToAll = false;
	public static boolean noToAll = false;
	public static File OPAtomFolder;
	public static String silentTrack = "";
	public static int mergeDuration = 0;
	private static StringBuilder mailFileList = new StringBuilder();
	
	public static boolean analyze(File file, boolean isRaw) throws InterruptedException {
		
		btnStart.setEnabled(false);	
		
		String extension =  file.toString().substring(file.toString().lastIndexOf("."));
		
		if (caseGenerateFromDate.isSelected()
		|| comboFonctions.getSelectedItem().toString().equals("JPEG")
		|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
		{
			EXIFTOOL.run(file.toString());	
			do
			{
				Thread.sleep(100);
			}						 
			while (EXIFTOOL.isRunning);
			
			if (analyzeError(file.toString()))
				return false;
		}
				
		//inputDeviceIsRunning is already analyzed
		if (inputDeviceIsRunning == false && isRaw == false && extension.toLowerCase().equals(".pdf") == false)
		{
			 FFPROBE.FrameData(file.toString());	
			 do
			 {
			 	Thread.sleep(100);
			 }
			 while (FFPROBE.isRunning);
			 
			//Check GPU
			FFMPEG.checkGPUCapabilities(file.toString());
			 
			 if (analyzeError(file.toString()))
				 return false;
			 
		}	 
		else if (extension.toLowerCase().equals(".pdf"))
		{
			 XPDF.info(file.toString());	
			 do
			 {
				 Thread.sleep(100);						 
			 }
			 while (XPDF.isRunning);
			 
			 if (analyzeError(file.toString()))
				 return false;
			 
			 XPDF.toFFPROBE(file.toString());	
			 do
			 {
				 Thread.sleep(100);						 
			 }
			 while (XPDF.isRunning);
			 
			 if (analyzeError(file.toString()))
				 return false;
		}
		
		if (isRaw == false && extension.toLowerCase().equals(".pdf") == false)
		{
			FFPROBE.Data(file.toString());
				
			do
			{
				Thread.sleep(100);
			}
			while (FFPROBE.isRunning);
			 					 		 
			if (analyzeError(file.toString()))
				return false;
									
			if (FFPROBE.timecode1 == "" || FFPROBE.interlaced == null)
			{
				MEDIAINFO.run(file.toString(), false);
				
				do
				{
					Thread.sleep(100);
				}
				while (MEDIAINFO.isRunning);
				
				if (FFPROBE.interlaced == null)
				{
					FFPROBE.interlaced = "0";
					FFPROBE.fieldOrder = "0";
				}
			}
		}
		else
		{
			if (FFPROBE.interlaced == null)
			{
				FFPROBE.interlaced = "0";
				FFPROBE.fieldOrder = "0";
			}
		}

		return true;
	}

	public  static boolean analyzeError(String file)
	{
		 if (FFMPEG.error)
		 {
			 	errorList = new StringBuilder(file + System.lineSeparator() + FFMPEG.errorLog + System.lineSeparator());
				return true;
		 }
		 return false;
	}
	
	public static boolean waitFileCompleted(File file) {
		
		progressBar1.setIndeterminate(true);
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		lblCurrentEncoding.setText(file.getName());
		tempsRestant.setVisible(false);
		btnStart.setEnabled(false);
		btnCancel.setEnabled(true);
		comboFonctions.setEnabled(false);
		
		long fileSize = 0;
		
		do {
			
			fileSize = file.length();
			try {				
				Thread.sleep(5000);				// Permet d'attendre la nouvelle valeur de la copie					
			} catch (InterruptedException e) {}

		} while ((fileSize != file.length() || FFMPEG.isReadable(file) == false) && cancelled == false && file.exists());
				
		progressBar1.setIndeterminate(false);
		btnCancel.setEnabled(false);
		
		if (cancelled)
		{
			lblCurrentEncoding.setText(language.getProperty("lblEncodageEnCours"));
			btnStart.setEnabled(true);
			comboFonctions.setEnabled(true);
			return false;
		}
				
		return true;
	}
	
	public static File setInputFile(File input) {
		
        if (Shutter.scanIsRunning)
        {
        	input = watchFolder(input.toString());
        	if (input != null)
        		btnStart.setEnabled(true);
        	else
        		return null;
        	
        	Shutter.progressBar1.setIndeterminate(false);		
        }
        else if (Settings.btnWaitFileComplete.isSelected())
        {
			if (waitFileCompleted(input) == false)
				return null;
        }
        
        return input;
	}
	
	public static File watchFolder(String folder) {
		
		progressBar1.setIndeterminate(true);
		lblCurrentEncoding.setText(language.getProperty("waitingFiles"));
		tempsRestant.setVisible(false);

		disableAll();

		File actualScanningFile = null;
		do {
			File dir = new File(folder);
			btnStart.setEnabled(false);

			for (File file : dir.listFiles()) // Récupère chaque fichier du dossier
			{
				if (file.isHidden() || file.isFile() == false)
				{
					continue;
				}
				else if (Settings.btnExclude.isSelected())
				{							
					boolean allowed = true;
					for (String excludeExt : Settings.txtExclude.getText().split("\\*"))
					{
						int s = file.toString().lastIndexOf('.');
						String ext = file.toString().substring(s);
						
						if (excludeExt.contains(".") && ext.toLowerCase().equals(excludeExt.replace(",", "").toLowerCase()))
							allowed = false;
					}
					
					if (allowed == false)
						continue;
				}

				actualScanningFile = file;

				// Lorque un fichier est entrain d'être copié
				progressBar1.setIndeterminate(true);
				
				if (waitFileCompleted(file) == false)
					return null;

				if (actualScanningFile != null)
					return actualScanningFile;
				
			} // End for
		} while (scanIsRunning);

		// Action de fin
		progressBar1.setIndeterminate(false);
		enableAll();
		btnEmptyList.doClick();

		return null;
	}

	public static void moveScannedFiles(String file)
	{
		File folder = new File(liste.getElementAt(0) + "completed");
		
		//Si erreur
		if (FFMPEG.error || cancelled)
			folder = new File(liste.getElementAt(0) + "error");
		
		if (folder.exists() == false)
			folder.mkdir();
		
		File fileToMove = new File(folder + "/" + file);
		
		// Récupère le fichier du dossier
		for (int i = 0 ; i < liste.getSize() ; i++)
		{						
			File getFile = new File(liste.getElementAt(i) + file);
			
			if (getFile.exists()) //Si le fichier correspond on le déplace dans le dossier
			{
					if (fileToMove.exists()) //Nom identique à la source
					{
						int n = 1;
						
						String ext =  file.substring(file.lastIndexOf("."));
						
						do {
							fileToMove = new File(folder + "/" + file.replace(ext, "") + "_" + n + ext);
							n++;
						} while (fileToMove.exists());
					}
					
					//Déplacement du fichier
					getFile.renameTo(fileToMove);
					
					break;	
			}
		}
	}
	
	public static String completedFiles(int number) {
	
		String labelName;
		
		if (number > 1 && number < 1000)
			labelName = number + " " + Shutter.language.getProperty("filesEnded");
		else if( number <= 1)
			labelName = number + " " +  Shutter.language.getProperty("fileEnded");
		else
			labelName = number / 1000 + "k " + Shutter.language.getProperty("filesEnded");		
		
		return labelName;
	}
	
	public static void listFilesForFolder(final String file, final File folder) {
		
		//DVD & Blu-ray
		if (file == null)
		{
			for (final File fileEntry : folder.listFiles())
			{
		        if (fileEntry.isFile())
		        {		        	
		        	String ext = fileEntry.toString().substring(fileEntry.toString().lastIndexOf("."));
		        	if (ext.equals(".log") || ext.equals(".xml") || ext.equals(".mpg") || ext.equals(".meta") || ext.equals(".mkv"))
		        	{
		        		File fileToDelete = new File(fileEntry.getAbsolutePath());
		        		fileToDelete.delete();
		        	}
		        }
		    }
		}
		else
		{
		    for (final File fileEntry : folder.listFiles())
		    {
		        if (fileEntry.isFile()) 
		        {
		        	if (fileEntry.getName().contains(file) && fileEntry.getName().contains(".log"))
		        	{
		        		File fileToDelete = new File(fileEntry.getAbsolutePath());
		        		fileToDelete.delete();
		        	}
		        }
		    }
		}	    
	}
	
	public static String setConcat(File file, String output) 
	{		
		String extension =  file.toString().substring(file.toString().lastIndexOf("."));
		
		File concatFile = new File(output.replace("\\", "/") + "/" + file.getName().replace(extension, ".txt")); 
		
		if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
		{									
			try {								
				PrintWriter writer = new PrintWriter(concatFile.toString(), "UTF-8");
				
				NumberFormat formatter = new DecimalFormat("00");
				NumberFormat formatFrame = new DecimalFormat("000");
				
				int h = Integer.parseInt(VideoPlayer.caseInH.getText());
				int m = Integer.parseInt(VideoPlayer.caseInM.getText());
				int s = Integer.parseInt(VideoPlayer.caseInS.getText());
				int f = (int) (Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.currentFPS));	
				
				writer.println("file " + "'" + file + "'");							
				writer.println("outpoint " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f));
				
				h = Integer.parseInt(VideoPlayer.caseOutH.getText());
				m = Integer.parseInt(VideoPlayer.caseOutM.getText());
				s = Integer.parseInt(VideoPlayer.caseOutS.getText());
				f = (int) (Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.currentFPS));	
				
				writer.println("file " + "'" + file + "'");	
				writer.println("inpoint " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f));
				
				InputAndOutput.inPoint = "";
				InputAndOutput.outPoint = "";			
				
				writer.close();
				
			} catch (FileNotFoundException | UnsupportedEncodingException e) {	
				
				FFMPEG.error  = true;
				
				if (concatFile.exists())
					concatFile.delete();
			}	
			
			return " -safe 0 -f concat";
		}
		else if (grpImageSequence.isVisible() && caseEnableSequence.isSelected()) //Image sequence
		{
			setMerge(file.getName(), extension, output);	

			return " -safe 0 -f concat -r " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".");			
		}
		else if (Settings.btnSetBab.isSelected() || (grpImageSequence.isVisible() && caseEnableSequence.isSelected())) //Concat mode
		{
			setMerge(file.getName(), extension, output);	

			return " -safe 0 -f concat";
		}
		
		return "";
	}
	
	public static void setMerge(String fileName, String extension, String output) {
					
		File concatFile = new File(output.replace("\\", "/") + "/" + fileName.replace(extension, ".txt")); 
				
		try {			
			mergeDuration = 0;
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
			PrintWriter writer = new PrintWriter(concatFile, "UTF-8");      
			
			for (int i = 0 ; i < liste.getSize() ; i++)
			{				
				//Scanning
				if (Settings.btnWaitFileComplete.isSelected())
	            {
					File file = new File(liste.getElementAt(i));
					
					if (waitFileCompleted(file) == false)
						break;
	            }
				//Scanning
				
				if (Settings.btnSetBab.isSelected())
				{
					FFPROBE.Data(liste.getElementAt(i));
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
					
					// IMPORTANT
					do {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {}
					} while (FFPROBE.isRunning);
					
					mergeDuration += FFPROBE.totalLength;
					FFPROBE.totalLength = 0;
				}
				else if (grpImageSequence.isVisible() && caseEnableSequence.isSelected())
				{
					FFPROBE.currentFPS = 25.0f; //Important
					mergeDuration = (int) (Shutter.liste.getSize() * ((float) 1000 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString().replace(",", "."))));
				}
				
				writer.println("file '" + liste.getElementAt(i) + "'");
			}				
			writer.close();
									
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));			
			progressBar1.setMaximum((int) (mergeDuration / 1000));
			
			FFPROBE.totalLength = mergeDuration;
			FFMPEG.dureeTotale = progressBar1.getMaximum();
						
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			FFMPEG.error  = true;
			if (concatFile.exists())
				concatFile.delete();
			
		}
	}

	public static String setOutputDestination(String output, File file) {	
		
		if (caseChangeFolder1.isSelected())
		{
			output = lblDestination1.getText();
			
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
				
				output = pathToFile.toString();
			}
			else if (caseCreateOPATOM.isSelected() && lblOPATOM.getText().equals("OP-Atom")
			&& (comboFonctions.getSelectedItem().toString().equals("DNxHD") || comboFonctions.getSelectedItem().toString().equals("DNxHR")))
			{ 						
				String number = new File(output).getName(); 
								
				if (number.matches("^[0-9]+$") ) //Si la création de fichiers OPatom est activée et que le dossier est un nombre 
				{				
					if (OPAtomFolder == null)
					{
						//Si le nom du fichier contient le nom du dossier on update avec le dossier supérieur
						if (file.getName().contains(file.getParentFile().getName()))
							OPAtomFolder = new File(file.getParentFile().getParent());
						else
							OPAtomFolder = new File(file.getParent());
					}
					else
					{			
						//Si le nom du fichier contient le nom du dossier
						if (file.getName().contains(file.getParentFile().getName()))
						{
							if (file.getParentFile().getParent().toString().equals(OPAtomFolder.toString()) == false) //Si le fichier est dans un dossier différent du précédent
							{
								File newFolder; //Tant que le dossier existe on incrément le numéro de dossier
								int n = (Integer.parseInt(number) + 1);
								do	{									
									newFolder = new File(new File(output).getParent() + "/" + n);
									n++;
								} while (newFolder.exists());
								
								newFolder.mkdir();
								
								lblDestination1.setText(newFolder.toString());
								output = lblDestination1.getText();
								
								OPAtomFolder = new File(file.getParentFile().getParent());
							}
						}
						else if (file.getParent().toString().equals(OPAtomFolder.toString()) == false) //Si le fichier est dans un dossier différent du précédent
						{
							File newFolder; //Tant que le dossier existe on incrément le numéro de dossier
							int n = (Integer.parseInt(number) + 1);
							do	{									
								newFolder = new File(new File(output).getParent() + "/" + n);
								n++;
							} while (newFolder.exists());
							
							newFolder.mkdir();
							
							lblDestination1.setText(newFolder.toString());
							output = lblDestination1.getText();
							
							OPAtomFolder = new File(file.getParent());
						}
					}	
				}
			}
		}
		else
		{
			output = file.getParent();
			lblDestination1.setText(output);
		}
		
		return output;
	}
		
	public static String getRandomHexString() {
		
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < 10){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, 10);
    }
	
	public static String setLoop(String extension) {	
		
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
					
					return " -loop 1 -t " + Settings.txtImageDuration.getText();
			}
		}
		
		return "";
	}
	
	public static String setStream() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
			case "H.265":
				
				if (caseStream.isSelected())
				{
					if (caseLoop.isSelected())						
						return " -stream_loop -1 -re";
					else
						return " -re";
				}
				
			break;							
		}
		
		return "";
	}
	
	public static String setMetadatas() { 
		
		String metadata = " -metadata creation_time=" + '"' + java.time.Clock.systemUTC().instant() + '"';
				
		if (casePreserveMetadata.isSelected())
		{
			if (FFPROBE.creationTime != "") metadata = " -metadata creation_time=" + '"' + FFPROBE.creationTime + '"';
			
			metadata += " -map_metadata 0 -map_metadata:s:v 0:s:v";
			
			if (FFPROBE.hasAudio) metadata += " -map_metadata:s:a 0:s:a";
			
			metadata += " -movflags use_metadata_tags";
		}

		return metadata;
	}

	@SuppressWarnings("rawtypes")
	public static String setMapSubtitles() {
		
		int i = 0;
		if (VideoPlayer.caseAddWatermark.isSelected())
			i = 1;
		
		String subsMapping = "";
		boolean addSub = false;
		
		for (Component c : SubtitlesEmbed.frame.getContentPane().getComponents())
		{			
			if (c instanceof JTextField)
			{
				if (((JTextField) c).getText().equals(language.getProperty("aucun")) == false)
				{
					i++;
					addSub = true;
				}
				else
					break;
			}
			else if (c instanceof JComboBox && addSub)
			{
	        	String[] languages = Locale.getISOLanguages();			
				Locale loc = new Locale(languages[((JComboBox) c).getSelectedIndex()]);
				
				if (VideoPlayer.caseAddWatermark.isSelected())
				{
					subsMapping += " -map " + i + ":s -metadata:s:s:" + (i - 2) + " language=" + loc.getISO3Language().replace("zho", "chi"); //For chinese compatibility			
				}
				else
					subsMapping += " -map " + i + ":s -metadata:s:s:" + (i - 1) + " language=" + loc.getISO3Language().replace("zho", "chi"); //For chinese compatibility
					
				addSub = false;
			}
		}
		
		return subsMapping;
	}
	
	public static String setFilterComplex(String filterComplex, boolean isOutputCodec, String audio) {

		//No audio
		if (comboFonctions.getSelectedItem().toString().equals("JPEG") || comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
		{
			if (filterComplex != "")   	   
	        	filterComplex = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"';
	        
	        return filterComplex;
		}
		
		//VAAPI Hardware encoding
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
			case "H.265":
			case "VP9":
				
				if (caseAccel.isSelected() && comboAccel.getSelectedItem().equals("VAAPI"))			
				{						
					if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits"))
					{
						filterComplex += ",format=p010,hwupload";
					}
					else
						filterComplex += ",format=nv12,hwupload";
				}
				
				break;
		}
		
		if (caseOPATOM.isSelected())
			audio = "";
		
        if (filterComplex != "")
        {	          	
        	//Si une des cases est sélectionnée alors il y a déjà [0:v]
        	if (VideoPlayer.caseAddWatermark.isSelected() || (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
        		filterComplex = " -filter_complex " + '"' + filterComplex + "[out]";
        	else
        		filterComplex = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]";
        	
        	if (isOutputCodec)
        	{
				float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
	        	float value = (float) (newFPS/ FFPROBE.currentFPS);
	        	
				if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))) && (value < 0.5f || value > 2.0f))    
				{
					filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
				}
				else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
				{
					filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
				}
	        	else if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")) && FFPROBE.stereo == false)
	        	{
	        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
	        	}
	        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals(language.getProperty("mono")) && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")) && FFPROBE.surround == false)
	        	{
	        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
	        	}
	        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")))
	        	{
	        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' + "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
	        	}
	        	else
	        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        	}
        	else
        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        
        }
        else if (isOutputCodec)
        {        
			float newFPS = Float.parseFloat((comboFPS.getSelectedItem().toString()).replace(",", "."));
        	float value = (float) (newFPS/ FFPROBE.currentFPS);
        	
			if (caseConform.isSelected() && (comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")) || comboConform.getSelectedItem().toString().equals(language.getProperty("conformByReverse"))) && (value < 0.5f || value > 2.0f))    
			{
				filterComplex = " -map v:0" + audio;
			}
			else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
			{
				filterComplex = " -map v:0" + audio;
			}
        	else if (FFPROBE.channels > 1 && (lblAudioMapping.getText().equals(language.getProperty("stereo")) || lblAudioMapping.getText().equals(language.getProperty("mono"))) && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")) && FFPROBE.stereo == false)
        	{
        		filterComplex = audio + " -map v:0 -map " + '"' +  "[a]" + '"';
        	}
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals(language.getProperty("mono")) && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")) && FFPROBE.surround == false)
        	{
        		filterComplex = audio + " -map v:0 -map " + '"' +  "[a]" + '"';
        	}
        	else if (FFPROBE.stereo && lblAudioMapping.getText().equals("Multi") && (debitAudio.getSelectedItem().toString().equals("0") == false || comboAudioCodec.getSelectedItem().equals("FLAC")))
        	{
        		filterComplex = audio + " -map v:0 -map " + '"'+ "[a1]" + '"' + " -map " + '"'+ "[a2]" + '"';
        	}
        	else
        		filterComplex = " -map v:0" + audio;
        }
        else
        {
        	filterComplex = " -map v:0" + audio;
        }
        
		//On map les sous-titres que l'on intègre        
        if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn == false)
        {			        	
        	if (comboFilter.getSelectedItem().toString().equals(".mkv"))
        		filterComplex += " -c:s srt" + setMapSubtitles();
        	else
        		filterComplex += " -c:s mov_text" + setMapSubtitles();    	
        }
        else if (casePreserveSubs.isSelected())
        {
        	if (FFPROBE.subtitlesCodec != "" && FFPROBE.subtitlesCodec.equals("dvb_subtitle"))
        	{
    			switch (comboFilter.getSelectedItem().toString())
    			{
    				case ".mp4":
    				case ".mkv":
    				case ".ts":
    					
    					filterComplex += " -c:s dvbsub -map s?";
    					break;
    					
    				default:
    					
    					filterComplex += " -c:s copy -map s?";
    					break;
    			}
        	}
        	else if (comboFilter.getSelectedItem().toString().equals(".mkv"))
        	{
        		if (FFPROBE.subtitlesCodec != "" && FFPROBE.subtitlesCodec.equals("hdmv_pgs_subtitle"))
        		{
        			filterComplex += " -c:s copy -map s?";
        		}
        		else
        			filterComplex += " -c:s srt -map s?";
        	}
        	else
        		filterComplex += " -c:s mov_text -map s?";
        }
        
        return filterComplex;
	}
	
	@SuppressWarnings("rawtypes")
	public static String setFilterComplexBroadcastCodecs(String filterComplex, String audio) {
		
		String mapping = "";		
		String transitions = "";	
		
		if (Transitions.setAudioFadeIn() !=  "")
		{
			transitions += Transitions.setAudioFadeIn();
		}
		
		if (Transitions.setAudioFadeOut() !=  "")
		{
			if (transitions != "")	transitions += ",";
			
			transitions += Transitions.setAudioFadeOut();
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			if (transitions != "")	transitions += ",";
			
			transitions += Transitions.setAudioSpeed();
		}
		
		//No audio
		if (comboAudio1.getSelectedIndex() == 16
		&& comboAudio2.getSelectedIndex() == 16
		&& comboAudio3.getSelectedIndex() == 16
		&& comboAudio4.getSelectedIndex() == 16
		&& comboAudio5.getSelectedIndex() == 16
		&& comboAudio6.getSelectedIndex() == 16
		&& comboAudio7.getSelectedIndex() == 16
		&& comboAudio8.getSelectedIndex() == 16)
		{
			if (VideoPlayer.caseAddWatermark.isSelected() || (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
			{
				mapping += " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			}
			else if (filterComplex != "")
			{
				mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audio;
			}
			else
				mapping += " -map v:0" + audio;
			
			//On map les sous-titres que l'on intègre        
			if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn == false)
			{
				mapping += " -c:s mov_text" + setMapSubtitles();
			}
			
			return mapping;
		}
		else
		{
			int channels = 0;
			for (Component c : grpSetAudio.getComponents())
			{
				if (c instanceof JComboBox)
				{
					if (((JComboBox) c).getSelectedIndex() != 16)
						channels ++;
				}
			}
			
			for (int m = 1 ; m < channels + 1; m++) 
			{	
				//On map les pistes existantes
				if (m <= FFPROBE.channels)
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
						if (transitions != "")
				    		transitions = transitions + ",";
						
						if (VideoPlayer.caseAddWatermark.isSelected() || (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
							mapping += " -filter_complex " + '"' + filterComplex + "[out];[0:a]" + transitions + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						else if (filterComplex != "")
							mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out];[0:a]" + transitions + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						else
							mapping += " -map v:0 -filter_complex [0:a]" + transitions + "channelsplit[a1][a2] -map [a1] -map [a2]" + audio;
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
					
					if (comboFonctions.getSelectedItem().toString().equals("XDCAM HD422"))
						silentTrack += " -shortest -map_metadata -1";
					
					if (VideoPlayer.caseAddWatermark.isSelected() && (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
						mapping += " -map 3";	
					else if (VideoPlayer.caseAddWatermark.isSelected() || (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
						mapping += " -map 2";
					else
						mapping += " -map 1";	
				}
			}
		}			
		
		if (FFPROBE.channels != 1) //On ajoute le filterComplex lorsque il n'y a pas de split des pistes son	
		{
			if (transitions != "")
	    		transitions = " -filter:a " + '"' + transitions + '"';
			
			if (VideoPlayer.caseAddWatermark.isSelected() || (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn))
				mapping = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + transitions + mapping + audio;
			else if (filterComplex != "")
				mapping = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + transitions + mapping + audio;
			else
				mapping = " -map v:0" + transitions + mapping + audio;	
		}		
		
		//On map les sous-titres que l'on intègre        
        if (VideoPlayer.caseAddSubtitles.isSelected() && subtitlesBurn == false)
        {        				
			mapping += " -c:s mov_text" + setMapSubtitles();
        }
		
		return mapping;
	}
	
	public static File fileReplacement(String path, String file, String oldExt, String surname, String newExt) {
		
		int n = 1;
		File fileOut = new File(path + "/" + file.replace(oldExt, surname.substring(0, surname.length() - 1) + newExt));
		
		//Nom identique à la source
		if (file.equals(file.replace(oldExt, surname.substring(0, surname.length() - 1) + newExt)) && caseChangeFolder1.isSelected() == false)
		{
			do {
				fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
				n++;
			} while (fileOut.exists());
		}
		else
		{
			
			int q = 0;
			if (yesToAll == false && noToAll == false)
			{				
				Object[] options = { language.getProperty("yes"), language.getProperty("yesToAll"), language.getProperty("no"), language.getProperty("noToAll"), language.getProperty("btnCancel") };
				
				q = JOptionPane.showOptionDialog(frame, language.getProperty("eraseFile"),
						Shutter.language.getProperty("File") + " " + fileOut.getName() + " " + Shutter.language.getProperty("alreadyExist"), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
			}

			if (q == 3) //No to all
			{
				noToAll = true;
			}
			
			if (q == 1) //Yes to all
			{
				yesToAll = true;
			}
			
			if (q == 2 || noToAll) //No
			{
				do {
					fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
					n++;
				} while (fileOut.exists());
			}
			else if (q == 4) //Cancel
			{
				if (caseChangeFolder1.isSelected() == false)
				{
					lblDestination1.setText(language.getProperty("sameAsSource"));
				}
				
				return null;	
			}
		}
			
		return fileOut;				
	}

	public static void addFileForMail(final String file)
	{		
		String text = Shutter.language.getProperty("isEncoded");
		if (FFMPEG.error)
			text = Shutter.language.getProperty("notEncoded");
		
		if (caseChangeFolder3.isSelected())
		{
			mailFileList.append(file + " " + text + " " + lblDestination1.getText() + " | " + lblDestination2.getText() + " | " + lblDestination3.getText()  + System.lineSeparator());
		}
		else if (caseChangeFolder2.isSelected())
		{
			mailFileList.append(file + " " + text + " " + lblDestination1.getText() + " | " + lblDestination2.getText()  + System.lineSeparator());
		}
		else
			mailFileList.append(file + " " + text + " " + lblDestination1.getText()  + System.lineSeparator());
	}
	
	public static void sendMail() {
		
		if (caseSendMail.isSelected() && mailFileList.length() != 0) 
		{
			sendMailIsRunning = true;

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "auth.smtp.1and1.fr");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(Utils.username, Utils.password);
				}
			});
			
			try {
				
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(Utils.username));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(textMail.getText()));			
				if (FFMPEG.error)
				{
					message.setSubject(Shutter.language.getProperty("shutterEncodingError"));
				}
				else
					message.setSubject(Shutter.language.getProperty("shutterEncodingCompleted"));
				
				message.setText(mailFileList.toString());

				Transport.send(message);						
				
			    Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		        Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("mailSuccessful"));
		        
			} catch (MessagingException e) {
				
				Console.consoleFFMPEG.append(System.lineSeparator() + e + System.lineSeparator());
				
				Shutter.lblCurrentEncoding.setForeground(Color.RED);
	        	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("mailFailed"));
				Shutter.progressBar1.setValue(0);
				
			} finally {
				sendMailIsRunning = false;
				mailFileList.setLength(0);
			}

		}
		else
		{
			mailFileList.setLength(0);
		}
	}

	public static void copyFile(File file) {		
		//Destination 2
		if (caseChangeFolder2.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(1);
			File filein  = file;
	        File fileout = new File(lblDestination2.getText() + "/" + file.getName());
			try {		
		        long length  = filein.length();
				progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
		
		//Destination 3
		if (caseChangeFolder3.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(2);
			File filein  = file;
	        File fileout = new File(lblDestination3.getText() + "/" + file.getName());
			try {		
		        long length  = filein.length();
		        progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {	                	
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
	}

	public static boolean cleanFunction(String fileName, File fileOut, String output) {
		
		String extension = "";
		
		if (fileName != null && fileName != "" && fileName.contains("."))
		{
			extension = fileName.substring(fileName.lastIndexOf("."));
		}
		
		//Errors
		if (FFMPEG.error || fileOut.exists() && fileOut.length() == 0 && caseCreateSequence.isSelected() == false && extension.equals(".pdf") == false)
		{
			errorList.append(fileName + System.lineSeparator() + FFMPEG.errorLog + System.lineSeparator());
			FFMPEG.errorLog.setLength(0);
			
			try {
				fileOut.delete();
			} catch (Exception e) {}
		}
		
		//Stabilisation
		if (Corrections.vidstab != null)
		{
			if (Corrections.vidstab.exists())
				Corrections.vidstab.delete();
			
			Corrections.vidstab = null;
		}
		
		//Concat mode or Image sequence
		if (Settings.btnSetBab.isSelected() || (grpImageSequence.isVisible() && caseEnableSequence.isSelected()) || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")) && caseInAndOut.isSelected())
		{
			File concatList = new File(output.replace("\\", "/") + "/" + fileName.replace(extension, ".txt")); 			
			
			if (RenderQueue.frame == null || RenderQueue.frame.isVisible() == false)
				concatList.delete();
		}
		
		//Process cancelled
		if (cancelled && caseCreateSequence.isSelected() == false && extension.equals(".pdf") == false)
		{
			try {
				fileOut.delete();
			} catch (Exception e) {}
			return true;
		}
	
		//Ended files
		if (cancelled == false && FFMPEG.error == false)
		{
			completed++;
			lblFilesEnded.setText(completedFiles(completed));
		}
		
		//Timecode
		if (caseIncrementTimecode.isSelected())
		{			 
			NumberFormat formatter = new DecimalFormat("00");

			int timecodeToMs = Integer.parseInt(TCset1.getText()) * 3600000 + Integer.parseInt(TCset2.getText()) * 60000 + Integer.parseInt(TCset3.getText()) * 1000 + Integer.parseInt(TCset4.getText()) * (int) (1000 / FFPROBE.currentFPS);
			int millisecondsToTc = timecodeToMs + FFPROBE.totalLength;
			
			if (caseInAndOut.isSelected())
				millisecondsToTc = timecodeToMs + VideoPlayer.durationH * 3600000 + VideoPlayer.durationM * 60000 + VideoPlayer.durationS * 1000 + VideoPlayer.durationF * (int) (1000 / FFPROBE.currentFPS);
			
			if (caseEnableSequence.isSelected())
				millisecondsToTc = Shutter.liste.getSize() * (int) (1000 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString()));
			
			TCset1.setText(formatter.format(millisecondsToTc / 3600000));
			TCset2.setText(formatter.format((millisecondsToTc / 60000) % 60));
			TCset3.setText(formatter.format((millisecondsToTc / 1000) % 60));				        
			TCset4.setText(formatter.format((int) (millisecondsToTc / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
		}
		
		//Open the folder
		if (caseOpenFolderAtEnd1.isSelected() && cancelled == false && FFMPEG.error == false)
		{
			try {
				Desktop.getDesktop().open(new File(output));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
				
		return false;
		
	}

}
