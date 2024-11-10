/*******************************************************************************************
* Copyright (C) 2024 PACIFICO PAUL
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
import java.time.LocalDate;
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
import library.PDF;

public class FunctionUtils extends Shutter {

	public static int completed;
	public static StringBuilder watchFolder = new StringBuilder();
	public static boolean allowsInvalidCharacters = false;
	public static boolean yesToAll = false;
	public static boolean noToAll = false;
	public static File OPAtomFolder;
	public static String silentTrack = "";
	public static int mergeDuration = 0;
	public static boolean bestBitrateMode;
	public static boolean goodBitrateMode;
	public static boolean autoBitrateMode;
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
		}
		
		if (inputDeviceIsRunning)
		{
			btnStart.setEnabled(true);
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
			 			 					
			if (analyzeError(file.toString()))
				return false;
			 
		}	 
		else if (extension.toLowerCase().equals(".pdf"))
		{
			 PDF.info(file.toString());	
			 do
			 {
				 Thread.sleep(100);						 
			 }
			 while (PDF.isRunning);
			 			 
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
			
			//Check GPU
			FFMPEG.checkGPUCapabilities(file.toString());
					
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
		 if (FFMPEG.error)// || EXIFTOOL.error || DCRAW.error || DVDAUTHOR.error || MKVMERGE.error || TSMUXER.error || XPDF.error)
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
        	input = watchFolder();
        	
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
	
	public static File watchFolder() {
			
		progressBar1.setIndeterminate(true);
		lblCurrentEncoding.setText(language.getProperty("waitingFiles"));
		tempsRestant.setVisible(false);

		disableAll();

		File actualScanningFile = null;
		do {

			if (actualScanningFile == null)
			{	
				for (int i = 0 ; i < liste.getSize() ; i ++)
				{							
					File dir = new File(liste.getElementAt(i));
					btnStart.setEnabled(false);

					for (File file : dir.listFiles())
					{		
						if (file.isDirectory() == false && file.isHidden() == false && file.getName().equals("completed") == false && file.getName().equals("error") == false)
						{		
							if (file.getName().contains("."))
							{					
								boolean allowed = true;
								if (Settings.btnExclude.isSelected())
								{
									for (String excludeExt : Settings.txtExclude.getText().split("\\*"))
									{
										int s = file.toString().lastIndexOf('.');
										String ext = file.toString().substring(s);
										
										if (excludeExt.contains(".") && ext.toLowerCase().equals(excludeExt.replace(",", "").toLowerCase()))
											allowed = false;
									}
									
									if (allowed == false)
									{
										continue;//Next
									}
								}
								
								if (getWatchFolderList(file) == false)
								{
									continue;
								}
							}
						}
						else
						{
							continue;
						}

						actualScanningFile = file;

						//While a file is copied
						progressBar1.setIndeterminate(true);
										
						if (waitFileCompleted(file) == false)
							return null;

						if (actualScanningFile != null)
							return actualScanningFile;
						
					}
				}
			}
									
		} while (scanIsRunning);
						
		progressBar1.setIndeterminate(false);
		enableAll();
		btnEmptyList.doClick();

		return null;
	}

	private static void setWatchFolderList(File input) {
		
		try {
			
			StringBuilder stb = new StringBuilder();

			if (watchFolder.length() > 0)
			{			
				for (String file : watchFolder.toString().split(System.lineSeparator()))
				{	
					stb.append(file + System.lineSeparator());
				}

				watchFolder.setLength(0);

				boolean fileExists = false;							
				for (String file : stb.toString().split(System.lineSeparator()))
				{
					if (file.equals(input.toString())) //Replace at the same line
					{						
						watchFolder.append(input.toString() + System.lineSeparator());
						fileExists = true;
					}
					else if (file.equals("null") == false)
					{
						watchFolder.append(file + System.lineSeparator());
					}
				}
				
				if (fileExists == false)
				{
					watchFolder.append(input.toString() + System.lineSeparator());
				}
			}		
			else
			{
				watchFolder.append(input.toString() + System.lineSeparator());
			}
								
		} catch (Exception e) {}		
	}
	
	public static boolean getWatchFolderList(File input) {
		
		try {
				
			if (watchFolder.length() > 0)
			{		
				for (String line : watchFolder.toString().split(System.lineSeparator()))
				{	
					if (line.equals(input.toString()))
					{			
						return false;
					}
				}
			}		
			
		} catch (Exception e) {}		
		
		return true;		
	}
	
	public static void moveScannedFiles(File file)
	{
		//Error
		if (FFMPEG.error)
		{
			File folder = new File(file.getParent() + "/error");

			if (folder.exists() == false)
				folder.mkdir();
			
			File fileToMove = new File(folder + "/" + file.getName());
					
			if (fileToMove.exists())
			{
				int n = 1;
				
				String ext =  file.getName().substring(file.getName().lastIndexOf("."));
				
				do {
					fileToMove = new File(folder + "/" + file.getName().replace(ext, "") + "_" + n + ext);
					n++;
				} while (fileToMove.exists());
			}
			
			//Moving the file to error folder
			file.renameTo(fileToMove);	
		}
		else
		{
			setWatchFolderList(file);
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
		
		if (VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
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
		else if (grpImageSequence.isVisible() && caseEnableSequence.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false) //Image sequence
		{
			setMerge(file.getName(), extension, output);	

			return " -safe 0 -f concat -r " + caseSequenceFPS.getSelectedItem().toString().replace(",", ".");			
		}
		else if (Settings.btnSetBab.isSelected() || (grpImageSequence.isVisible() && caseEnableSequence.isSelected() && comboResolution.getSelectedItem().toString().contains("AI") == false)) //Concat mode
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
			FFMPEG.fileLength = progressBar1.getMaximum();
						
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
				String folderLevel = "";
				
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				{
					String s[] = file.getParent().toString().replace("/Volumes", "").split("/");	
					for (int i = comboCreateTree.getSelectedIndex() + 1 ; i < s.length ; i++)
					{
						folderLevel += ("/" + s[i]);
					}
				}
				else
				{
					String s[] = file.getParent().toString().substring(2).split("\\\\");	
					for (int i = comboCreateTree.getSelectedIndex() + 1 ; i < s.length ; i++)
					{
						folderLevel += ("\\" + s[i]);
					}
				}
				
				pathToFile = new File(lblDestination1.getText() + folderLevel);
				
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
			else if (caseSubFolder.isSelected() && txtSubFolder.getText().equals("") == false)
			{
				output = new File (lblDestination1.getText() + "/" + txtSubFolder.getText()).toString();
				
				if (new File(output).exists() == false)
				{
					new File(output).mkdirs();
				}
			}
		}
		else
		{
			output = file.getParent();
			
			lblDestination1.setText(output);
			
			if (caseSubFolder.isSelected() && txtSubFolder.getText().equals("") == false)
			{
				output = new File (file.getParent() + "/" + txtSubFolder.getText()).toString();
				
				if (new File(output).exists() == false)
				{
					new File(output).mkdirs();
				}
			}
		}
		
		return output;
	}

	public static String setSuffix(String suffix, boolean isOverlay) {

		suffix = suffix.replace("[", "{").replace("]", "}");
		
		if (suffix.contains("{"))
		{			
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
			{
				if (suffix.contains("{resolution}"))
				{
					suffix = suffix.replace("{resolution}", FFPROBE.imageWidth + "x" + FFPROBE.imageHeight);
				}
				else if (suffix.contains("{scale}"))
				{
					suffix = suffix.replace("{scale}", FFPROBE.imageWidth + "x" + FFPROBE.imageHeight);
				}
				
				if (suffix.contains("{width}"))
				{
					suffix = suffix.replace("{width}", String.valueOf(FFPROBE.imageWidth));
				}
				
				if (suffix.contains("{height}"))
				{
					suffix = suffix.replace("{height}", String.valueOf(FFPROBE.imageHeight));
				}
				
				float or = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
				
				if (suffix.contains("{ratio}"))
				{
					suffix = suffix.replace("{ratio}", String.valueOf(or));
				}
				else if (suffix.contains("{aspect}"))
				{
					suffix = suffix.replace("{aspect}", String.valueOf(or));
				}
			}
			else
			{
				String i[] = FFPROBE.imageResolution.split("x");        
				String o[] = FFPROBE.imageResolution.split("x");
							
				if (comboResolution.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
					
					o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * value));
					o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * value));
				}					
				else if (comboResolution.getSelectedItem().toString().contains("x"))
				{
					if (comboResolution.getSelectedItem().toString().contains("AI"))
					{
						if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
						{
							o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 2));
							o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 2));
						}
						else
						{
							o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * 4));
							o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * 4));
						}
					}
					else
						o = comboResolution.getSelectedItem().toString().split("x");
				}
				else if (comboResolution.getSelectedItem().toString().contains(":"))
				{
					o = comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
					
					int iw = Integer.parseInt(i[0]);
		        	int ih = Integer.parseInt(i[1]);          	
		        	int ow = Integer.parseInt(o[0]);
		        	int oh = Integer.parseInt(o[1]);        	
		        	float ir = (float) iw / ih;
							        	
					if (o[0].toString().equals("1")) // = auto
					{
						o[0] = String.valueOf((int) Math.round((float) oh * ir));
					}
	        		else
	        		{
	        			o[1] = String.valueOf((int) Math.round((float) ow / ir));
	        		}
				}
				        	
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);        	
	        	float or = (float) ow / oh;

				if (suffix.contains("{resolution}"))
				{
					suffix = suffix.replace("{resolution}", comboResolution.getSelectedItem().toString());
				}
				else if (suffix.contains("{scale}"))
				{
					suffix = suffix.replace("{scale}", comboResolution.getSelectedItem().toString());
				}
				
				if (suffix.contains("{width}"))
				{
					suffix = suffix.replace("{width}", String.valueOf(ow));
				}
				
				if (suffix.contains("{height}"))
				{
					suffix = suffix.replace("{height}", String.valueOf(oh));
				}
				
				if (suffix.contains("{ratio}"))
				{
					suffix = suffix.replace("{ratio}", String.valueOf(or));
				}
				else if (suffix.contains("{aspect}"))
				{
					suffix = suffix.replace("{aspect}", String.valueOf(or));
				}
			}
			
			if (suffix.contains("{codec}"))
			{
				suffix = suffix.replace("{codec}", comboFonctions.getSelectedItem().toString());
			}
			else if (suffix.contains("{function}"))
			{
				suffix = suffix.replace("{function}", comboFonctions.getSelectedItem().toString());
			}
			
			if (suffix.contains("{date}"))
			{
				LocalDate currentDate = LocalDate.now();
				
				suffix = suffix.replace("{date}", currentDate.toString());
			}
						
			if (suffix.contains("{duration}"))
			{				
				suffix = suffix.replace("{duration}", Shutter.formatter.format(VideoPlayer.durationH) + "." + Shutter.formatter.format(VideoPlayer.durationM) + "."  + Shutter.formatter.format(VideoPlayer.durationS) + "."  + Shutter.formatter.format(VideoPlayer.durationF));
			}	
			else if (suffix.contains("{time}"))
			{				
				suffix = suffix.replace("{time}", Shutter.formatter.format(VideoPlayer.durationH) + "." + Shutter.formatter.format(VideoPlayer.durationM) + "."  + Shutter.formatter.format(VideoPlayer.durationS) + "."  + Shutter.formatter.format(VideoPlayer.durationF));
			}
			
			if (suffix.contains("{framerate}"))
			{				
				if (caseConform.isSelected())
				{
					suffix = suffix.replace("{framerate}", comboFPS.getSelectedItem().toString());
				}
				else
					suffix = suffix.replace("{framerate}", String.valueOf(FFPROBE.currentFPS).replace(".0", ""));
			}
			else if (suffix.contains("{fps}"))
			{				
				if (caseConform.isSelected())
				{
					suffix = suffix.replace("{fps}", comboFPS.getSelectedItem().toString());
				}
				else
					suffix = suffix.replace("{fps}", String.valueOf(FFPROBE.currentFPS).replace(".0", ""));
			}
			
			if (suffix.contains("{preset}"))
			{				
				suffix = suffix.replace("{preset}", Utils.currentPreset.replace(".enc", ""));
			}
			
			if (suffix.contains("{timecode}"))
			{				
				suffix = suffix.replace("{timecode}", FFPROBE.timecode1 + FFPROBE.timecode2 + FFPROBE.timecode3 + FFPROBE.timecode4);
			}
			
			if (suffix.contains("{bitrate}"))
			{				
				if (Shutter.grpBitrate.isVisible())
				{
					suffix = suffix.replace("{bitrate}", FunctionUtils.setVideoBitrate() + "kbps");
				}
				else
					suffix = suffix.replace("{bitrate}", "");
			}
			
		}
		
		return suffix;
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
	
	public static int setVideoBitrate() {
		
		bestBitrateMode = false;
		goodBitrateMode = false;
		autoBitrateMode = false;
				
		if (debitVideo.getSelectedItem().equals(language.getProperty("lblBest").toLowerCase()) || debitVideo.getSelectedItem().equals(language.getProperty("lblGood").toLowerCase()) || debitVideo.getSelectedItem().equals("auto"))
		{			
			//Compression ratio
			//Setup: (1920*1080*25*8*2)/5000kbps = 165888	
			String function = comboFonctions.getSelectedItem().toString();
			Integer compValue = 165888; //5000kbps default
			
			if ("MJPEG".equals(function))
			{
				compValue = 16588; //50000kbps
			}
			else if ("MPEG-1".equals(function))
			{
				compValue = 33177; //250000kbps
			}
			else if ("WMV".equals(function))
			{
				compValue = 41472; //20000kbps
			} 			 
			else if ("MPEG-2".equals(function) || "Xvid".equals(function) || "Theora".equals(function))
			{
				compValue = 103680; //8000kbps
			}
			else if ("H.265".equals(function) || "VP9".equals(function))
			{
				compValue = 331776; //2500kbps
			}
			else if ("AV1".equals(function) || "H.266".equals(function))
			{
				compValue = 414720; //2000kbps
			}		
						
			int pixels = FFPROBE.imageWidth * FFPROBE.imageHeight;
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) == false)
			{  
				String i[] = FFPROBE.imageResolution.split("x");
				String o[] = FFPROBE.imageResolution.split("x");
							
				if (comboResolution.getSelectedItem().toString().contains("%"))
				{
					double value = (double) Integer.parseInt(comboResolution.getSelectedItem().toString().replace("%", "")) / 100;
					
					o[0] = String.valueOf(Math.round(Integer.parseInt(o[0]) * value));
					o[1] = String.valueOf(Math.round(Integer.parseInt(o[1]) * value));
				}		
				else if (comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (Shutter.comboResolution.getSelectedItem().toString().contains("2x"))
					{
						o[0] = String.valueOf(FFPROBE.imageWidth * 2);
						o[1] = String.valueOf(FFPROBE.imageHeight * 2);
					}
					else
					{
						o[0] = String.valueOf(FFPROBE.imageWidth * 4);
						o[1] = String.valueOf(FFPROBE.imageHeight * 4);
					}
				}
				else if (comboResolution.getSelectedItem().toString().contains("x"))
				{
					o = comboResolution.getSelectedItem().toString().split("x");
				}
				else if (comboResolution.getSelectedItem().toString().contains(":"))
				{
					o = comboResolution.getSelectedItem().toString().replace("auto", "1").split(":");
					
					int iw = Integer.parseInt(i[0]);
		        	int ih = Integer.parseInt(i[1]);          	
		        	int ow = Integer.parseInt(o[0]);
		        	int oh = Integer.parseInt(o[1]);        	
		        	float ir = (float) iw / ih;
							        	
					if (o[0].toString().equals("1")) // = auto
					{
						o[0] = String.valueOf((int) Math.round((float) oh * ir));
					}
	        		else
	        		{
	        			o[1] = String.valueOf((int) Math.round((float) ow / ir));
	        		}
				}
				
	        	int ow = Integer.parseInt(o[0]);
	        	int oh = Integer.parseInt(o[1]);	        	
	        		        	
				pixels = ow * oh;					
			}	
			else if (Shutter.grpCrop.isVisible() && caseEnableCrop.isSelected())
			{
				int ow = Integer.parseInt(textCropWidth.getText());
	        	int oh = Integer.parseInt(textCropHeight.getText());	        	
	        		        	
				pixels = ow * oh;	
			}				
			
			float framerate = FFPROBE.currentFPS;
			if (caseConform.isSelected())
			{
				framerate = Float.valueOf(comboFPS.getSelectedItem().toString().replace(",", "."));
			}
			
			int bitDepth = 8;
			if (caseColorspace.isSelected())
			{
				if (comboColorspace.getSelectedItem().toString().contains("10bits"))
				{
					bitDepth = 10;
				}
				else if (comboColorspace.getSelectedItem().toString().contains("12bits"))
				{
					bitDepth = 12;
				}
			}
			
			Integer videoBitrate = (int) Math.round((float) (pixels * framerate * bitDepth * 2) / compValue);
			
			if (debitVideo.getSelectedItem().equals(language.getProperty("lblBest").toLowerCase()))
			{
				bestBitrateMode = true;
				videoBitrate = videoBitrate * 4;
			}
			else if (debitVideo.getSelectedItem().equals(language.getProperty("lblGood").toLowerCase()))
			{
				goodBitrateMode = true;
				videoBitrate = videoBitrate * 2;
			}
			else
				autoBitrateMode = true;				
			
			return videoBitrate;
		}
		else
		{			
			return Integer.parseInt(debitVideo.getSelectedItem().toString());
		}
	}
	
	public static String setStream() {
		
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
				
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
			if (FFPROBE.audioOnly == false)
			{
				metadata = " -map_metadata 0 -map_metadata:s:v 0:s:v";
				
				if (FFPROBE.hasAudio)
				{
					metadata += " -map_metadata:s:a 0:s:a";
				}
			}
			else			
			{
				metadata = " -map_metadata:s:a 0:s:a";
			}
			
			metadata += " -movflags use_metadata_tags";
		}

		return metadata;
	}

	@SuppressWarnings("rawtypes")
	public static String setMapSubtitles() {
		
		int i = 0;
		if (Shutter.caseAddWatermark.isSelected())
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
				Locale loc = Locale.of(languages[((JComboBox) c).getSelectedIndex()]);
				
				if (Shutter.caseAddWatermark.isSelected())
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
	
	public static String setFilterComplex(String filterComplex, String audio, boolean picture) {

		//No audio
		if (picture)
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
			case "H.266":
			case "VP9":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("VAAPI"))			
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
        	if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
        		filterComplex = " -filter_complex " + '"' + filterComplex + "[out]";
        	else
        		filterComplex = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]";
        	
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
        	else
        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        
        }
        else
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
        	else
        		filterComplex = " -map v:0" + audio;
        }
        
		//On map les sous-titres que l'on intègre        
        if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn == false)
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
        		if (FFPROBE.subtitlesCodec != "" && (FFPROBE.subtitlesCodec.equals("hdmv_pgs_subtitle") || FFPROBE.subtitlesCodec.equals("ass")))
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
		String audioFiltering = "";	
		
		if (Transitions.setAudioFadeIn(false) !=  "")
		{
			audioFiltering += Transitions.setAudioFadeIn(false);
		}
		
		if (Transitions.setAudioFadeOut(false) !=  "")
		{
			if (audioFiltering != "")	audioFiltering += ",";
			
			audioFiltering += Transitions.setAudioFadeOut(false);
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			if (audioFiltering != "")	audioFiltering += ",";
			
			audioFiltering += Transitions.setAudioSpeed();
		}		
		
		//Audio normalization		
		if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
		{				
			if (audioFiltering != "") audioFiltering += ",";
			
			audioFiltering += "volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB";				
		}
		
		//No audio
		if (comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio")))
		{
			if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
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
			if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn == false)
			{
				mapping += " -c:s mov_text" + setMapSubtitles();
			}
			
			return mapping;
		}
		else if (comboAudioCodec.getSelectedItem().equals(language.getProperty("codecCopy")) == false)
		{ 
			int channels = 0;
			for (Component c : grpSetAudio.getComponents())
			{
				if (c instanceof JComboBox && c.getName().equals("comboAudioCodec") == false && c.getName().equals("comboNormalizeAudio") == false)
				{
					if (((JComboBox) c).getSelectedIndex() != 16)
						channels ++;
				}
			}

			for (int m = 1 ; m < channels; m++) 
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
						if (audioFiltering != "")
				    		audioFiltering = audioFiltering + ",";
						
						if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
						{
							mapping += " -filter_complex " + '"' + filterComplex + "[out];[0:a]" + audioFiltering + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						}
						else if (filterComplex != "")
						{
							mapping += " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out];[0:a]" + audioFiltering + "channelsplit[a1][a2]" + '"' + " -map " + '"' + "[out]" + '"' + " -map [a1] -map [a2]" + audio;
						}
						else
							mapping += " -map v:0 -filter_complex [0:a]" + audioFiltering + "channelsplit[a1][a2] -map [a1] -map [a2]" + audio;
						
						m ++;
					}
					else
					{
						int i = 1;
						int map = m;
						for (Component c : grpSetAudio.getComponents())
						{							
							if (c.getName() != null && c.getName().contains("comboAudio") && c instanceof JComboBox && c.getName().equals("comboAudioCodec") == false)
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
					silentTrack = " -f lavfi -i anullsrc=r=" + lbl48k.getSelectedItem().toString() + ":cl=mono";
					
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM"))
						silentTrack += " -shortest -map_metadata -1";
					
					if (Shutter.caseAddWatermark.isSelected() && (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
					{
						mapping += " -map 3";	
					}
					else if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
					{
						mapping += " -map 2";
					}
					else
						mapping += " -map 1";	
				}
			}
		}			
		
		if (FFPROBE.channels != 1) //On ajoute le filterComplex lorsque il n'y a pas de split des pistes son	
		{
			if (audioFiltering != "")
	    		audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
			
			if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
			{
				mapping = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audioFiltering + mapping + audio;
			}
			else if (filterComplex != "")
			{
				mapping = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"' + audioFiltering + mapping + audio;
			}
			else
				mapping = " -map v:0" + audioFiltering + mapping + audio;	
		}		
		
		//On map les sous-titres que l'on intègre        
        if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn == false)
        {        				
			mapping += " -c:s mov_text" + setMapSubtitles();
        }
		        
		return mapping;
	}
	
	public static File fileReplacement(String path, String file, String oldExt, String surname, String newExt) {
		
		int n = 1;
		File fileOut = new File(path + "/" + file.replace(oldExt, surname.substring(0, surname.length() - 1) + newExt));
		
		//Nom identique à la source
		if (file.equals(file.replace(oldExt.toLowerCase(), surname.substring(0, surname.length() - 1) + newExt)) && caseChangeFolder1.isSelected() == false)
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

	        if (caseSubFolder.isSelected() && txtSubFolder.getText().equals("") == false)
			{
	        	fileout = new File (lblDestination2.getText() + "/" + txtSubFolder.getText() + "/" + file.getName());
				
				if (new File(lblDestination2.getText() + "/" + txtSubFolder.getText()).exists() == false)
				{
					fileout.mkdirs();
				}
			}
	        
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
	        
	        if (caseSubFolder.isSelected() && txtSubFolder.getText().equals("") == false)
			{
	        	fileout = new File (lblDestination3.getText() + "/" + txtSubFolder.getText() + "/" + file.getName());
				
	        	if (new File(lblDestination3.getText() + "/" + txtSubFolder.getText()).exists() == false)
				{
					fileout.mkdirs();
				}
			}
	        
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
		if (Settings.btnSetBab.isSelected() || (grpImageSequence.isVisible() && caseEnableSequence.isSelected()) || VideoPlayer.comboMode.getSelectedItem().toString().equals(language.getProperty("removeMode")))
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
			
			if (VideoPlayer.playerInMark > 0 || VideoPlayer.playerOutMark < VideoPlayer.waveformContainer.getWidth() - 2)
				millisecondsToTc = timecodeToMs + VideoPlayer.durationH * 3600000 + VideoPlayer.durationM * 60000 + VideoPlayer.durationS * 1000 + VideoPlayer.durationF * (int) (1000 / FFPROBE.currentFPS);
			
			if (caseEnableSequence.isSelected())
				millisecondsToTc = Shutter.liste.getSize() * (int) (1000 / Float.parseFloat(caseSequenceFPS.getSelectedItem().toString()));
			
			TCset1.setText(formatter.format(millisecondsToTc / 3600000));
			TCset2.setText(formatter.format((millisecondsToTc / 60000) % 60));
			TCset3.setText(formatter.format((millisecondsToTc / 1000) % 60));				        
			TCset4.setText(formatter.format((int) (millisecondsToTc / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
		}
		
		//Open the folder
		if (caseOpenFolderAtEnd1.isSelected() && comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false && cancelled == false && FFMPEG.error == false)
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
