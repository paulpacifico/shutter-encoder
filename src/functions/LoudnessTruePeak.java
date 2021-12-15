/*******************************************************************************************
* Copyright (C) 2022 PACIFICO PAUL
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

package functions;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import application.Shutter;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;
import settings.InputAndOutput;

public class LoudnessTruePeak extends Shutter {
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {

				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
					
					if (file == null)
						break;
		            
					try {
						
						String fileName = file.getName();
						lblCurrentEncoding.setText(fileName);			
						
						//Data analyze
						if (FunctionUtils.analyze(file, false) == false)
							continue;
						
						//InOut		
						InputAndOutput.getInputAndOutput();	
						
		            	//Audio
						String audio = setAudio();	
						
						//Envoi de la commande
						String cmd;
						if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
							cmd =  " -vn" + audio + " -f null -";					
						else
							cmd =  " -vn" + audio + " -f null -" + '"';	
						
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd);		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());
						
						//Show detection
						if (cancelled == false)
							showDetection(fileName);				
						
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							if (lastActions(fileName))
								break;
						}
					
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}
				}	
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	private static String setAudio() {
		
		if (FFPROBE.stereo)
		{
			return " -filter_complex ebur128=peak=true";
		}
	    else if (FFPROBE.channels > 1)	
	    {
	    	if (FFPROBE.channels >= 4)	    		
	    	{
	    		String[] options = {"A1 & A2", "A3 & A4"};
	    		int q = JOptionPane.showOptionDialog(frame, language.getProperty("ChooseMultitrack"), language.getProperty("multitrack"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	    		if (q == 0)
		    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
		    	else
		    		return " -filter_complex " + '"' + "[0:a:2][0:a:3]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    	}
	    	else
	    		return " -filter_complex " + '"' + "[0:a:0][0:a:1]amerge=inputs=2[a];[a]ebur128=peak=true" + '"';
	    }
	    else
	    	return " -filter_complex ebur128=peak=true";
	}

	private static void showDetection(String fichier) {
		
		if (FFMPEG.analyseLufs != null && Shutter.cancelled == false && FFMPEG.error == false)
		{
			 JOptionPane.showMessageDialog(frame, FFMPEG.analyseLufs, "Loudness & True Peak", JOptionPane.INFORMATION_MESSAGE);
			 int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("saveResult") , Shutter.language.getProperty("analyzeEnded"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			 
			 if (q == JOptionPane.YES_OPTION)
			 {
				FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveResult"), FileDialog.SAVE);
				dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
				dialog.setVisible(true);
	
				 if (dialog.getFile() != null)
				 { 
					try {
						PrintWriter writer = new PrintWriter(dialog.getDirectory() + dialog.getFile().replace(".txt", "") + ".txt", "UTF-8");
						writer.println(Shutter.language.getProperty("analyzeOf") + " " + fichier);
						writer.println("");
						writer.println(FFMPEG.analyseLufs + System.lineSeparator() + System.lineSeparator()
									 + FFMPEG.shortTermValues);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e) {}
	
				 }
				
			 }//End Question
			 else
			 {
				 cancelled = false;
			 }
		}	
	}
	
	private static boolean lastActions(String fileName) {		
		
		//Errors
		if (FFMPEG.error)
		{
			FFMPEG.errorList.append(fileName);
		    FFMPEG.errorList.append(System.lineSeparator());
		}

		//Process cancelled
		if (cancelled)
		{
			return true;
		}
		else
		{
			if(FFMPEG.error == false)
				FunctionUtils.completed++;
			lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));
		}
		
		//Sending process
		FunctionUtils.addFileForMail(fileName);		
		
		//Watch folder
		if (Shutter.scanIsRunning)
		{
			FunctionUtils.moveScannedFiles(fileName);
			LoudnessTruePeak.main();
			return true;
		}
		return false;
	}
	
}
