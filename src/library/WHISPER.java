package library;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import application.Console;
import application.Shutter;
import application.Update;
import application.Utils;

public class WHISPER extends Shutter {
	
	public static boolean error = false;
	public static boolean isRunning = false;
	public static Thread runProcess;
	public static Process process;
	public static String whisperModel = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	public static void getWhisperModel() {
		
		if (System.getProperty("os.name").contains("Windows"))
		{
			whisperModel = whisperModel.substring(1,whisperModel.length()-1);
		}
		else
			whisperModel = whisperModel.substring(0,whisperModel.length()-1);	
								
		whisperModel = whisperModel.substring(0,(int) (whisperModel.lastIndexOf("/"))).replace("%20", " ")  + "/Library/models/ggml-large-v3-q5_0.bin";
	}
	
	public static void run(final String cmd) {
		
		error = false;
		progressBar1.setValue(0);		
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
								
				try {
					String PathToWHISPER;
					ProcessBuilder processWHISPER;
					if (System.getProperty("os.name").contains("Windows"))
					{
						PathToWHISPER = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToWHISPER = PathToWHISPER.substring(1,PathToWHISPER.length()-1);
						PathToWHISPER = '"' + PathToWHISPER.substring(0,(int) (PathToWHISPER.lastIndexOf("/"))).replace("%20", " ")  + "/Library/whisper-cli.exe" + '"';
						
						processWHISPER = new ProcessBuilder(PathToWHISPER + " -m " + '"' + whisperModel + '"' + cmd);
					}
					else
					{
						PathToWHISPER = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						PathToWHISPER = PathToWHISPER.substring(0,PathToWHISPER.length()-1);
						PathToWHISPER = PathToWHISPER.substring(0,(int) (PathToWHISPER.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/whisper-cli";
						
						processWHISPER = new ProcessBuilder("/bin/bash", "-c" , PathToWHISPER + " -m " + '"' + whisperModel + '"' + cmd);
					}
					
					processWHISPER.redirectErrorStream(true);
					
					Console.consoleWHISPER.append(Shutter.language.getProperty("command") + " " + " -m " + '"' + whisperModel + '"' + cmd);	
						
					isRunning = true;	
					process = processWHISPER.start();
		
			        InputStreamReader isr = new InputStreamReader(process.getInputStream());
			        BufferedReader br = new BufferedReader(isr);
			        String line;
			        
			        Console.consoleWHISPER.append(System.lineSeparator());
			        
					progressBar1.setMaximum(Math.round(FFPROBE.totalLength / 1000));
					
					do {
						
						if (cancelled)
							break;
						
						line = br.readLine();					
						
						if (line != null && line.contains(" --> "))
						{
							Console.consoleWHISPER.append(line + System.lineSeparator());
							
							String s[] = line.split("]");
							String s2[] = s[0].split(" ");
							String s3[] = s2[2].split("\\.");
							String s4[] = s3[0].split(":");
							
							int value = Integer.parseInt(s4[0]) * 3600 + Integer.parseInt(s4[1]) * 60 + Integer.parseInt(s4[2]);
							progressBar1.setValue(value);
						}
						
					} while (line != null);		
						
					process.waitFor();		
					
					Console.consoleWHISPER.append(System.lineSeparator());
					
					isRunning = false;	        
					
					} catch (IOException | InterruptedException e) {
						error = true;
					}
									 
				}				
			});		
			runProcess.start();
	}

	public static void downloadModel() {
				
		File model = new File(whisperModel);
		
		try {
			if (model.exists() && Files.size(model.toPath()) != 1081140203L)				
			{
				model.delete();
			}
		} catch (IOException e1) {}
		
		if (model.exists() == false)
		{
			int q =  JOptionPane.showConfirmDialog(Shutter.frame, language.getProperty("downloadModel"), language.getProperty("functionTranscribe"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			if (q == JOptionPane.YES_OPTION)
			{
				new Update();
				
				Update.lblNewVersion.setText("AI model...");
				
				//Download
				Thread download = new Thread(new Runnable() {
					
					public void run() {		
				
						Utils.changeFrameVisibility(frame, true);
						
						Update.HTTPDownload("https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-large-v3-q5_0.bin?download=true", whisperModel);	
						
						Update.frame.dispose();
						
						Utils.changeFrameVisibility(frame, false);
						frame.toFront();
						
						if (model.exists() == false)
						{
							comboFonctions.removeItem(language.getProperty("functionTranscribe"));
						}
					}
				});
				download.start();
			}	
			else
			{
				comboFonctions.setSelectedItem("");
			}
		}
		
	}
   
}