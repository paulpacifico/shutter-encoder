/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import application.Console;
import application.RecordInputDevice;
import application.RenderQueue;
import application.Settings;
import application.Shutter;
import application.SubtitlesEmbed;
import application.SubtitlesTimeline;
import application.Utils;
import application.VideoPlayer;
import application.fileOverwriteWindow;
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
	public static boolean skipToAll = false;
	public static File OPAtomFolder;
	public static String silentTrack = "";
	public static int mergeDuration = 0;
	public static boolean bestBitrateMode;
	public static boolean goodBitrateMode;
	public static boolean autoBitrateMode;
	public static boolean deleteSRT = false;
	private static StringBuilder mailFileList = new StringBuilder();
	
	public static boolean analyze(File file, boolean isRaw) throws InterruptedException {
		
		btnStart.setEnabled(false);	
		
		String extension =  file.toString().substring(file.toString().lastIndexOf("."));
						
		if (caseGenerateFromDate.isSelected()
		|| comboFonctions.getSelectedItem().toString().contains("JPEG")
		|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
		{
			EXIFTOOL.run('"' + file.toString() + '"');	
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
			FFMPEG.checkGPUCapabilities(file.toString(), false);
					
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
				
		SwingUtilities.invokeLater(new Runnable()
		{
           @Override
           public void run() {
        	   progressBar1.setIndeterminate(false);
           }
		});
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
        	
        	SwingUtilities.invokeLater(new Runnable()
			{
	           @Override
	           public void run() {
	        	   progressBar1.setIndeterminate(false);
	           }
			});		
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
							if (file.isDirectory() && file.isHidden() == false && file.getName().equals("completed") == false && file.getName().equals("error") == false)
							{
								boolean addFolder = true;
								for (int f = 0 ; f < liste.getSize() ; f++)
								{
									if (liste.getElementAt(f).equals(file.toString()))
									{
										addFolder = false;
									}									
								}
								
								if (addFolder)
									liste.addElement(file.toString());
							}
							
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
						
		SwingUtilities.invokeLater(new Runnable()
		{
           @Override
           public void run() {
        	   progressBar1.setIndeterminate(false);
           }
		});
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
				int f = (int) Math.floor(Integer.parseInt(VideoPlayer.caseInF.getText()) * (1000 / FFPROBE.accurateFPS));	
				
				writer.println("file " + "'" + file + "'");							
				writer.println("outpoint " + formatter.format(h) + ":" + formatter.format(m) + ":" + formatter.format(s) + "." + formatFrame.format(f));
				
				h = Integer.parseInt(VideoPlayer.caseOutH.getText());
				m = Integer.parseInt(VideoPlayer.caseOutM.getText());
				s = Integer.parseInt(VideoPlayer.caseOutS.getText());
				f = (int) Math.floor(Integer.parseInt(VideoPlayer.caseOutF.getText()) * (1000 / FFPROBE.accurateFPS));	
				
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
			VideoPlayer.fileDuration = FunctionUtils.mergeDuration;			
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

	public static String setPrefixSuffix(String text, boolean isOverlay) {

		text = text.replace("[", "{").replace("]", "}");
		
		if (text.contains("{"))
		{			
			if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")))
			{
				if (text.contains("{resolution}"))
				{
					text = text.replace("{resolution}", FFPROBE.imageWidth + "x" + FFPROBE.imageHeight);
				}
				else if (text.contains("{scale}"))
				{
					text = text.replace("{scale}", FFPROBE.imageWidth + "x" + FFPROBE.imageHeight);
				}
				
				if (text.contains("{width}"))
				{
					text = text.replace("{width}", String.valueOf(FFPROBE.imageWidth));
				}
				
				if (text.contains("{height}"))
				{
					text = text.replace("{height}", String.valueOf(FFPROBE.imageHeight));
				}
				
				float or = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
				
				if (text.contains("{ratio}"))
				{
					text = text.replace("{ratio}", String.valueOf(or));
				}
				else if (text.contains("{aspect}"))
				{
					text = text.replace("{aspect}", String.valueOf(or));
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

				if (text.contains("{resolution}"))
				{
					text = text.replace("{resolution}", comboResolution.getSelectedItem().toString());
				}
				else if (text.contains("{scale}"))
				{
					text = text.replace("{scale}", comboResolution.getSelectedItem().toString());
				}
				
				if (text.contains("{width}"))
				{
					text = text.replace("{width}", String.valueOf(ow));
				}
				
				if (text.contains("{height}"))
				{
					text = text.replace("{height}", String.valueOf(oh));
				}
				
				if (text.contains("{ratio}"))
				{
					text = text.replace("{ratio}", String.valueOf(or));
				}
				else if (text.contains("{aspect}"))
				{
					text = text.replace("{aspect}", String.valueOf(or));
				}
			}
			
			if (text.contains("{codec}"))
			{
				text = text.replace("{codec}", comboFonctions.getSelectedItem().toString());
			}
			else if (text.contains("{function}"))
			{
				text = text.replace("{function}", comboFonctions.getSelectedItem().toString());
			}
			
			if (text.contains("{date}"))
			{
				LocalDate currentDate = LocalDate.now();
				
				text = text.replace("{date}", currentDate.toString());
			}
						
			if (text.contains("{duration}"))
			{				
				text = text.replace("{duration}", Shutter.formatter.format(VideoPlayer.durationH) + "." + Shutter.formatter.format(VideoPlayer.durationM) + "."  + Shutter.formatter.format(VideoPlayer.durationS) + "."  + Shutter.formatter.format(VideoPlayer.durationF));
			}	
			else if (text.contains("{time}"))
			{				
				text = text.replace("{time}", Shutter.formatter.format(VideoPlayer.durationH) + "." + Shutter.formatter.format(VideoPlayer.durationM) + "."  + Shutter.formatter.format(VideoPlayer.durationS) + "."  + Shutter.formatter.format(VideoPlayer.durationF));
			}
			
			if (text.contains("{framerate}"))
			{				
				if (caseConform.isSelected())
				{
					text = text.replace("{framerate}", comboFPS.getSelectedItem().toString());
				}
				else
					text = text.replace("{framerate}", String.valueOf(FFPROBE.currentFPS).replace(".0", ""));
			}
			else if (text.contains("{fps}"))
			{				
				if (caseConform.isSelected())
				{
					text = text.replace("{fps}", comboFPS.getSelectedItem().toString());
				}
				else
					text = text.replace("{fps}", String.valueOf(FFPROBE.currentFPS).replace(".0", ""));
			}
			
			if (text.contains("{preset}"))
			{				
				text = text.replace("{preset}", Utils.currentPreset.replace(".enc", ""));
			}
			
			if (text.contains("{timecode}"))
			{				
				text = text.replace("{timecode}", FFPROBE.timecode1 + FFPROBE.timecode2 + FFPROBE.timecode3 + FFPROBE.timecode4);
			}
			
			if (text.contains("{bitrate}"))
			{				
				if (Shutter.grpBitrate.isVisible())
				{
					text = text.replace("{bitrate}", FunctionUtils.setVideoBitrate() + "kbps");
				}
				else
					text = text.replace("{bitrate}", "");
			}
			
		}
		
		return text;
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
			if (Shutter.isLocked)
			{
				FFPROBE.setFilesize();
			}
			
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
	
	public static String setMetadata() { 
		
		String metadata = " -metadata creation_time=" + '"' + java.time.Clock.systemUTC().instant() + '"';
				
		if (casePreserveMetadata.isSelected())
		{		
			if (FFPROBE.audioOnly == false)
			{
				metadata = " -map_metadata 0";
				
				if (casePreserveSubs.isSelected() == false)
				{
					metadata += " -map_metadata:s:v 0:s:v";
				}
				
				if (FFPROBE.hasAudio && casePreserveSubs.isSelected() == false)
				{
					metadata += " -map_metadata:s:a 0:s:a";
				}
			}
			else if (casePreserveSubs.isSelected() == false)
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
		
		//Hardware encoding
		switch (comboFonctions.getSelectedItem().toString())
		{
			case "H.264":
			case "H.265":
			case "H.266":
			case "AV1":
			case "VP9":
			case "FFV1":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
				&& comboAccel.getSelectedItem().equals("VAAPI") || comboAccel.getSelectedItem().equals("Vulkan Video"))			
				{		
					if (filterComplex != "")
						filterComplex += ",";
					
					if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits"))
					{
						filterComplex += "format=p010,hwupload";
					}
					else
						filterComplex += "format=nv12,hwupload";
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

        	if (audio.contains("[a]"))
        	{
        		filterComplex += audio + " -map " + '"' + "[out]" + '"' + " -map " + '"' +  "[a]" + '"';
        	}
        	else
        		filterComplex += '"' + " -map " + '"' + "[out]" + '"' +  audio;
        }
        else
        {        
        	if (audio.contains("[a]"))
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
        	if (FFPROBE.subtitlesCodec != "" && (FFPROBE.subtitlesCodec.equals("dvb_subtitle") || FFPROBE.subtitlesCodec.equals("dvd_subtitle")))
        	{        		
    			switch (comboFilter.getSelectedItem().toString())
    			{
    				case ".mp4":
    				case ".mkv":
    				case ".ts":
    					
    					if (FFPROBE.subtitlesCodec.equals("dvb_subtitle"))
    					{
    						filterComplex += " -c:s dvbsub -map s?";
    					}
    					else if (FFPROBE.subtitlesCodec.equals("dvd_subtitle"))
    					{
    						filterComplex += " -c:s dvdsub -map s?";
    					}
    					
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
        	
        	filterComplex += " -disposition:s 0";
        }
        
        return filterComplex;
	}
	
	@SuppressWarnings("rawtypes")
	public static String setFilterComplexBroadcastCodecs(String filterComplex, String audio) {
		
		String mapping = "";		
		String audioFiltering = "";	
	
		//EQ
		audioFiltering = AudioSettings.setEQ(audioFiltering);
		
		if (Transitions.setAudioFadeIn(false) !=  "")
		{
			if (audioFiltering != "") audioFiltering += ",";
			
			audioFiltering += Transitions.setAudioFadeIn(false);
		}
		
		if (Transitions.setAudioFadeOut(false) !=  "")
		{
			if (audioFiltering != "") audioFiltering += ",";
			
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
		
		if (comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio"))) //No audio
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
				if (c instanceof JComboBox && c.getName().equals("comboAudioCodec") == false && c.getName().equals("lblAudioMapping") == false && c.getName().equals("comboNormalizeAudio") == false)
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
		
		//Same name as source
		if (caseChangeFolder1.isSelected() == false && file.equals(file.replace(oldExt.toLowerCase(), surname.substring(0, surname.length() - 1) + newExt))
		|| caseChangeFolder1.isSelected() && path.equals(new File(FFPROBE.analyzedMedia).getParent()))
		{
			do {
				fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
				n++;
			} while (fileOut.exists());
		}
		else
		{
			if (yesToAll == false && noToAll == false && skipToAll == false)
			{			
				new fileOverwriteWindow(fileOut.getName());
			}

			if (fileOverwriteWindow.value.equals("skip") && fileOverwriteWindow.caseApplyToAll.isSelected()) //Skip to all
			{
				skipToAll = true;
			}
			
			if (fileOverwriteWindow.value.equals("keep") && fileOverwriteWindow.caseApplyToAll.isSelected()) //No to all
			{
				noToAll = true;
			}			
			
			if (fileOverwriteWindow.value.equals("overwrite") && fileOverwriteWindow.caseApplyToAll.isSelected()) //Yes to all
			{
				yesToAll = true;
			}		
									
			if (fileOverwriteWindow.value.equals("keep") || noToAll) //No
			{
				do {
					fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
					n++;
				} while (fileOut.exists());
			}
			else if (fileOverwriteWindow.value.equals("skip") || skipToAll) //Skip
			{
				fileOut = new File("skip");
			}
			else if (fileOverwriteWindow.value.equals("cancel")) //Cancel
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addSubtitles(boolean add) {
		
		if (VideoPlayer.videoPath != null && add)
		{
			deleteSRT = false;
			
			if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionSubtitles")))
			{
				if (System.getProperty("os.name").contains("Windows"))
					Shutter.subtitlesFile = new File(SubtitlesTimeline.srt.getName());
				else
					Shutter.subtitlesFile = new File(Shutter.dirTemp + SubtitlesTimeline.srt.getName());

				Object[] options = { Shutter.language.getProperty("subtitlesBurn"),
						Shutter.language.getProperty("subtitlesEmbed") };

				int sub = JOptionPane.showOptionDialog(frame,
						Shutter.language.getProperty("chooseSubsIntegration"),
						Shutter.language.getProperty("caseSubtitles"), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (sub == 0) // Burn
				{
					Shutter.comboFonctions.setModel(new DefaultComboBoxModel(Shutter.functionsList));
					Shutter.comboFonctions.setSelectedItem("H.264");
					VideoPlayer.setMedia();

					caseAddSubtitles.setSelected(true);

					Shutter.subtitlesBurn = true;
					subtitlesFilePath = SubtitlesTimeline.srt;
					VideoPlayer.writeSub(subtitlesFilePath.toString(), StandardCharsets.UTF_8);

					subsCanvas.setSize(
							(int) ((float) Integer.parseInt(textSubsWidth.getText())
									/ ((float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
							(int) (VideoPlayer.player.getHeight()
									+ (float) Integer.parseInt(textSubtitlesPosition.getText())
											/ ((float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())));

					subsCanvas.setLocation((VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
					VideoPlayer.player.add(subsCanvas);

					grpSubtitles.setSize(grpSubtitles.getWidth(), 131);
					grpWatermark.setLocation(grpSubtitles.getLocation().x,
							grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);

					if (grpWatermark.getY() + grpWatermark.getHeight() >= 156 - 6) {
						grpOverlay.setSize(grpOverlay.getWidth(), grpOverlay.getHeight() - 1);
						grpSubtitles.setLocation(grpOverlay.getLocation().x,
								grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
						grpWatermark.setLocation(grpSubtitles.getLocation().x,
								grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
					}

					for (Component c : grpSubtitles.getComponents()) {
						c.setEnabled(true);
					}
				} else {
					Shutter.subtitlesBurn = false;
					subtitlesFilePath = new File(SubtitlesTimeline.srt.toString());
					Shutter.caseDisplay.setSelected(false);

					// On copy le .srt dans le fichier
					Thread copySRT = new Thread(new Runnable() {
						@SuppressWarnings("deprecation")
						@Override
						public void run() {

							try {

								Shutter.disableAll();

								File fileIn = new File(VideoPlayer.videoPath);
								String extension = VideoPlayer.videoPath.toString()
										.substring(fileIn.toString().lastIndexOf("."));
								File fileOut = new File(
										fileIn.toString().replace(extension, "_subs" + extension));

								// Envoi de la commande
								String cmd = " -c copy -c:s mov_text -map v:0? -map a? -map 1:s -y ";

								if (extension.equals(".mkv"))
									cmd = " -c copy -c:s srt -map v:0? -map a? -map 1:s -y ";

								FFMPEG.run(" -i " + '"' + fileIn + '"' + " -i " + '"' + subtitlesFilePath + '"' + cmd + '"' + fileOut + '"');

								Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
								Shutter.lblCurrentEncoding.setText(fileIn.getName());

								do {
									Thread.sleep(10);
								} while (FFMPEG.runProcess.isAlive());

								if (FFMPEG.error || fileOut.length() == 0) {
									FFMPEG.errorList.append(fileIn.getName());
									FFMPEG.errorList.append(System.lineSeparator());
									fileOut.delete();
								}

								// Annulation
								if (Shutter.cancelled)
									fileOut.delete();

								// Fichiers terminés
								if (Shutter.cancelled == false && FFMPEG.error == false)
									Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(1));

								// Ouverture du dossier
								if (Shutter.caseOpenFolderAtEnd1.isSelected() && Shutter.cancelled == false
										&& FFMPEG.error == false) {
									if (System.getProperty("os.name").contains("Mac")) {
										try {
											Runtime.getRuntime().exec(
													new String[] { "/usr/bin/open", "-R", fileOut.toString() });
										} catch (Exception e2) {
										}
									} else if (System.getProperty("os.name").contains("Linux")) {
										try {
											Desktop.getDesktop().open(fileOut);
										} catch (Exception e2) {
										}
									} else // Windows
									{
										try {
											Runtime.getRuntime().exec("explorer.exe /select," + fileOut.toString());
										} catch (IOException e1) {
										}
									}
								}

							} catch (Exception e) {
							} finally {
								Shutter.enfOfFunction();
							}

						}
					});
					copySRT.start();
				}
			}
			else
			{
				File video = new File(fileList.getSelectedValue().toString());
				String ext = video.toString().substring(video.toString().lastIndexOf("."));

				char slash = '/';
				if (System.getProperty("os.name").contains("Windows"))
					slash = '\\';
				
				FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseSubtitles"), FileDialog.LOAD);
				if (comboSubsSource.getSelectedIndex() == 0)
				{
					if (new File(video.toString().replace(ext, ".srt")).exists()) {
						dialog.setDirectory(video.getParent() + slash);
						dialog.setFile(video.getName().replace(ext, ".srt"));
					} else if (new File(video.toString().replace(ext, ".vtt")).exists()) {
						dialog.setDirectory(video.getParent() + slash);
						dialog.setFile(video.getName().replace(ext, ".vtt"));
					} else if (new File(video.toString().replace(ext, ".ass")).exists()) {
						dialog.setDirectory(video.getParent() + slash);
						dialog.setFile(video.getName().replace(ext, ".ass"));
					} else if (new File(video.toString().replace(ext, ".ssa")).exists()) {
						dialog.setDirectory(video.getParent() + slash);
						dialog.setFile(video.getName().replace(ext, ".ssa"));
					} else if (new File(video.toString().replace(ext, ".scc")).exists()) {
						dialog.setDirectory(video.getParent() + slash);
						dialog.setFile(video.getName().replace(ext, ".scc"));
					} else {
						dialog.setDirectory(new File(VideoPlayer.videoPath).getParent());
						dialog.setFile("*.srt;*.vtt;*.ass;*.ssa;*.scc");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						dialog.setMultipleMode(false);
						dialog.setVisible(true);
					}							
				}
				else
				{	
					try {
						
						File srt = new File(video.toString().replace(ext, ".srt"));
						
						//Command
						FFMPEG.runSilently(" -i " + '"' + video.toString() + '"' + " -vn -an -map s:" + (comboSubsSource.getSelectedIndex() - 1) + "? -y " + '"'  + video.toString().replace(ext, ".srt") + '"');	

						do {
							Thread.sleep(100);
						} while (FFMPEG.process.isAlive() && FFMPEG.error == false);
						
						if (srt.exists())
						{
							deleteSRT = true;
						}	
																						
					} catch (InterruptedException e) {}					
					
					dialog.setDirectory(video.getParent() + slash);
					dialog.setFile(video.getName().replace(ext, ".srt"));
				}						
				
				if (dialog.getFile() != null)
				{
					String input = dialog.getFile().substring(dialog.getFile().lastIndexOf("."));
												
					if (input.equals(".srt") || input.equals(".vtt") || input.equals(".ssa") || input.equals(".ass") || input.equals(".scc"))
					{
						if (System.getProperty("os.name").contains("Windows")) {
							Shutter.subtitlesFile = new File(dialog.getFile());
						} else
							Shutter.subtitlesFile = new File(Shutter.dirTemp + dialog.getFile());
						
						if (input.equals(".srt") || input.equals(".vtt"))
						{
							int sub = 0;
							if (autoBurn == false && autoEmbed == false && comboSubsSource.getSelectedIndex() == 0)
							{
								Object[] options = { Shutter.language.getProperty("subtitlesBurn"),
										Shutter.language.getProperty("subtitlesEmbed") };

								if (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))
								|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut")))
								{
									sub = 1;
								}

								if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
								&& Shutter.comboFonctions.getSelectedItem().toString().equals("XAVC") == false
								&& Shutter.caseCreateOPATOM.isSelected() == false
								&& Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap")) == false
								&& Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut")) == false)
								{
									sub = JOptionPane.showOptionDialog(frame,
										Shutter.language.getProperty("chooseSubsIntegration"),
										Shutter.language.getProperty("caseAddSubtitles"),
										JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
										null, options, options[0]);
								}										
							}
							else if (autoEmbed)
							{
								sub = 1;
							}
							
							if (sub == 0) // Burn
							{
								Shutter.subtitlesBurn = true;

								// Conversion du .vtt en .srt
								if (input.equals(".vtt"))
								{
									subtitlesFilePath = new File(Shutter.subtitlesFile.toString().replace(".vtt", ".srt"));

									try {
										
										FFMPEG.runSilently(" -i " + '"' + dialog.getDirectory() + dialog.getFile().toString() + '"' + " -y " + '"' + subtitlesFilePath.toString().replace(".srt", "_vtt.srt") + '"');

										do {
											Thread.sleep(100);													
										} while (FFMPEG.process.isAlive() && FFMPEG.error == false);
										
									} catch (InterruptedException e) {}

									Shutter.subtitlesFile = new File(subtitlesFilePath.toString().replace(".srt", "_vtt.srt"));
								} else
									subtitlesFilePath = new File(dialog.getDirectory() + dialog.getFile().toString());

								VideoPlayer.writeSub(subtitlesFilePath.toString(), StandardCharsets.UTF_8);

								subsCanvas.setSize((int) ((float) Integer.parseInt(textSubsWidth.getText())
										/ ((float) FFPROBE.imageHeight / VideoPlayer.player.getHeight())),
										(int) (VideoPlayer.player.getHeight()
												+ (float) Integer.parseInt(textSubtitlesPosition.getText())
														/ ((float) FFPROBE.imageHeight
																/ VideoPlayer.player.getHeight())));

								subsCanvas.setLocation(
										(VideoPlayer.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
								VideoPlayer.player.add(subsCanvas);

								for (Component c : grpSubtitles.getComponents()) {
									c.setEnabled(true);
								}
							} else {
								SubtitlesEmbed.subtitlesFile1.setText(dialog.getDirectory() + dialog.getFile().toString());

								if (SubtitlesEmbed.frame == null)
									new SubtitlesEmbed();
								else
									Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

								Shutter.subtitlesBurn = false;
								Shutter.changeSections(false);
								Shutter.caseDisplay.setSelected(false);

								if (caseAddSubtitles.isSelected())
								{
									for (Component c : grpSubtitles.getComponents()) {
										if (c instanceof JCheckBox == false) {
											c.setEnabled(false);
										}
									}
									if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
										comboSubsSource.setEnabled(true);
								}

								if (autoEmbed == false)
								{
									if (caseAddSubtitles.isSelected())
									{
										JOptionPane.showMessageDialog(frame,
												Shutter.language.getProperty("previewNotAvailable"),
												Shutter.language.getProperty("caseSubtitles"),
												JOptionPane.INFORMATION_MESSAGE);
									}
								}
							}

							// Important
							VideoPlayer.sliderSpeed.setEnabled(false);
							VideoPlayer.sliderSpeed.setValue(2);
							VideoPlayer.lblSpeed.setText("x1");
							VideoPlayer.lblSpeed.setBounds(
							VideoPlayer.sliderSpeed.getX() - VideoPlayer.lblSpeed.getPreferredSize().width - 2, VideoPlayer.sliderSpeed.getY() + 2, VideoPlayer.lblSpeed.getPreferredSize().width, 16);
						}
						else // SSA or ASS or SCC
						{
							Object[] options = { Shutter.language.getProperty("subtitlesBurn"),
									Shutter.language.getProperty("subtitlesEmbed") };

							int sub = 0;
							if (autoBurn == false && autoEmbed == false) {
								if (Shutter.comboFilter.getSelectedItem().toString().equals(".mxf") == false
										&& Shutter.comboFonctions.getSelectedItem().toString()
												.equals("XAVC") == false
										&& Shutter.caseCreateOPATOM.isSelected() == false) {
									sub = JOptionPane.showOptionDialog(frame,
											Shutter.language.getProperty("chooseSubsIntegration"),
											Shutter.language.getProperty("caseAddSubtitles"),
											JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
											null, options, options[0]);
								}
							} else if (autoEmbed) {
								sub = 1;
							}

							if (sub == 0) // Burn
							{
								Shutter.subtitlesBurn = true;

								try {
									FileUtils.copyFile(
											new File(dialog.getDirectory() + dialog.getFile().toString()),
											Shutter.subtitlesFile);
								} catch (IOException e) {
								}
							} else {
								SubtitlesEmbed.subtitlesFile1
										.setText(dialog.getDirectory() + dialog.getFile().toString());

								if (SubtitlesEmbed.frame == null)
									new SubtitlesEmbed();
								else
									Utils.changeDialogVisibility(SubtitlesEmbed.frame, false);

								Shutter.subtitlesBurn = false;
								Shutter.changeSections(false);
								Shutter.caseDisplay.setSelected(false);
							}

							for (Component c : grpSubtitles.getComponents())
							{
								if (c instanceof JCheckBox == false) {
									c.setEnabled(false);
								}										
							}
							if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
								comboSubsSource.setEnabled(true);

							if (autoEmbed == false)
							{
								if (caseAddSubtitles.isSelected())
								{
									JOptionPane.showMessageDialog(frame,
											Shutter.language.getProperty("previewNotAvailable"),
											Shutter.language.getProperty("caseSubtitles"),
											JOptionPane.INFORMATION_MESSAGE);
								}
							}
						}

						// Important
						VideoPlayer.sliderSpeed.setEnabled(false);
						VideoPlayer.sliderSpeed.setValue(2);
						VideoPlayer.lblSpeed.setText("x1");
						VideoPlayer.lblSpeed.setBounds(
								VideoPlayer.sliderSpeed.getX() - VideoPlayer.lblSpeed.getPreferredSize().width
										- 2,
								VideoPlayer.sliderSpeed.getY() + 2,
								VideoPlayer.lblSpeed.getPreferredSize().width, 16);
					} else {
						JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("invalidSubtitles"),
								Shutter.language.getProperty("subtitlesFileError"), JOptionPane.PLAIN_MESSAGE);
						caseAddSubtitles.setSelected(false);
					}
				} else
					caseAddSubtitles.setSelected(false);
			}

			if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
					|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionCut"))
					|| subtitlesBurn == false) {
				Shutter.casePreserveSubs.setSelected(false);
			}
			
		}
		else if (add == false)
		{			
			if (deleteSRT && subtitlesFilePath != null)
			{
				subtitlesFilePath.delete();
			}

			// IMPORTANT Enable caseDisplay
			Shutter.subtitlesBurn = true;
			changeSections(false);

			VideoPlayer.player.remove(subsCanvas);

			if (autoBurn == false && autoEmbed == false) {
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); // Use VideoPlayer.resizeAll and
																			// reload the frame
			}

			for (Component c : grpSubtitles.getComponents()) {
				if (c instanceof JCheckBox == false) {
					c.setEnabled(false);
				}
			}
			if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
				comboSubsSource.setEnabled(true);

			VideoPlayer.sliderSpeed.setEnabled(true);
		}
		
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

	public static boolean cleanFunction(File file, String fileName, File fileOut, String output) {
		
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
		
		//Delete file
		if (caseDeleteSourceFile.isSelected() && file != null && cancelled == false && FFMPEG.error == false)
		{
			file.delete();
		}
				
		return false;
		
	}

}
