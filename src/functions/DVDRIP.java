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

package functions;

import java.io.File;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import application.Console;
import application.Ftp;
import application.Shutter;
import application.Utils;
import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;

public class DVDRIP extends Shutter {
	
	public static void main() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {
				
				FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));			
				
				//Scan VIDEO_TS folder
				File [] volumes;
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					volumes = new File("/Volumes").listFiles();		
				else
					volumes = File.listRoots();					
				
				//DVD folder
				File dvdFolder = setDVDFolder(volumes);	
				
				if (dvdFolder == null)
				{
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("noDVD"), Shutter.language.getProperty("DVDMissing"), JOptionPane.ERROR_MESSAGE);
					cancelled = true;
				}
				else
				{	  		
					Console.consoleFFMPEG.append(System.lineSeparator() + dvdFolder + System.lineSeparator() );
					
					//Récupération des fichiers vidéo .VOB
					String[] vobArray = new String[dvdFolder.listFiles().length];
					int i = 0;
					for (File VOB : dvdFolder.listFiles())
					{
						if (VOB.isHidden() == false)
						{
							String ext = VOB.toString().substring(VOB.toString().lastIndexOf("."));
							
							if (VOB.getName().contains("VTS") && ext.equals(".VOB")) //Filtre des .VOB par VTS et extension
							{
								String [] s = VOB.getName().split("_");
								if (s[2].replace(ext, "").equals("0") == false)
								{
									vobArray[i] = VOB.toString();
									i ++;
								}				
							}
						}
					}	
					
					//Découpe des différentes vidéos
					File concatFile = new File(lblDestination1.getText() + "/DVD_RIP.txt");
					
					if (concatFile.exists())
						concatFile.delete();

					//Création du fichier texte pour la liste
					PrintWriter writer = null;
					
					int vtsNumber = 1;
					String VOB = null;
					int dureeTotale = 0;
					for (i = 0; i < vobArray.length ; i++)
					{		
						VOB  = vobArray[i];
						
						if (VOB != null)
						{
							String[] s = new File(VOB).getName().split("_");	
							int actualVOB = Integer.valueOf(s[1]);
							
							if (actualVOB == vtsNumber)//Par exemple si le VTS_01 contient VTS_01_00, VTS_01_02, VTS_01_03... on ajoute le fichier à la liste						
							{								
								if (concatFile.exists() == false)
								{
									try {
										writer = new PrintWriter(concatFile, "UTF-8");
									} catch (Exception e1) {}
								}
								writer.println("file '" + VOB + "'");	
								
								FFPROBE.Data(VOB);
								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {}
								} while (FFPROBE.isRunning == true);
								dureeTotale += FFPROBE.totalLength;
								
								Console.consoleFFMPEG.append(VOB + System.lineSeparator());					
							}
							else //Si le fichier VTS passe de VTS_01 à VTS_02 on execute le bout à bout existant avant de le recréer ou si il n'y a pas de VTS_02								
							{				
								progressBar1.setMaximum((int) (dureeTotale / 1000));
								
								//On enregistre le fichier de la liste des VOB
								writer.close();
								
								if (runRIP(vtsNumber, concatFile, VOB))
									break;								
								
								//Après exécution on passe on VTS suivant et on scan le nombre de fichiers existant sous le même nom dans la condition if suivante
								vtsNumber = actualVOB;	
								i--;
								 dureeTotale = 0;
							}
							
						}
					}
					
					//On execute la dernière liste de VTS
					if (cancelled == false)
					{
						writer.close();	
						progressBar1.setMaximum((int) (dureeTotale / 1000));				
						runRIP(vtsNumber, concatFile, VOB);
					}
					
				}
				
				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
					enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	protected static File setDVDFolder(File[] volumes) {
	
		for (File folder : volumes)
		{
			Console.consoleFFMPEG.append(String.valueOf(folder) + System.lineSeparator());
			try{
				for (File video_ts_folder : folder.listFiles())
				{
					if (video_ts_folder.getName().equals("VIDEO_TS"))
						return video_ts_folder;
				}
			} catch (Exception e) {}
		}		
		return null;
	}

	private static boolean runRIP(int vtsNumber, File concatFile, String VOB) {		
		
		try {
			String videoName = "VIDEO_" + vtsNumber + ".VOB";
			lblCurrentEncoding.setText(videoName);
								
			String labelOutput = lblDestination1.getText();
			lblDestination1.setText(labelOutput);
			
			//File output
			File fileOut = new File(labelOutput + "/" + videoName);
			if(fileOut.exists())
			{
				int q = JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("eraseFile"), Shutter.language.getProperty("theFile") + " " + videoName + " " + Shutter.language.getProperty("alreadyExist"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (q == JOptionPane.NO_OPTION)
					cancelled = true;	
			}
				
			if (cancelled == false)
			{
				//Command
				String cmd = " -c:v copy -c:a copy -c:s copy -err_detect ignore_err -map v:0? -map a? -map s? -y ";
				FFMPEG.run(" -safe 0 -f concat -i " + '"' + concatFile.toString() + '"' + cmd + '"'  + fileOut + '"');	
				
				do
				{
					Thread.sleep(100);
				}
				while(FFMPEG.runProcess.isAlive());
			}
			else
				return true;
			
			if (cancelled == true)
			{
				lastActions(VOB, fileOut, labelOutput);
				concatFile.delete();	
				return true;
			}

			if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
			{
				lastActions(VOB, fileOut, labelOutput);
			}
			
			} catch (InterruptedException e) {
				FFMPEG.error  = true;
			}
			finally { //Dans tous les cas on vide la liste de fichiers VOB
				concatFile.delete();										
			}
		return false;
	}
	
	private static void lastActions(String fileName, File fileOut, String output) {
	
		FunctionUtils.cleanFunction(null, fileName, fileOut, output);
		
		//Sending processes
		FunctionUtils.addFileForMail(fileName);
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
		
	}
}