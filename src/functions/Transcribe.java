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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.Ftp;
import application.RenderQueue;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.FFMPEG;
import library.WHISPER;
import settings.FunctionUtils;

public class Transcribe extends Shutter {

	public static void main() {
		
		Thread thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < liste.getSize() ; i++)
				{	
					//Render queue only accept selected files
					if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
					{
						boolean isSelected = false;
						
						for (String input : Shutter.fileList.getSelectedValuesList())
						{
							if (liste.getElementAt(i).equals(input))
							{
								isSelected = true;
							}
						}	
												
						if (isSelected == false)
						{
							continue;
						}							
					}
					
					File file = FunctionUtils.setInputFile(new File(liste.getElementAt(i)));		
									
					if (file == null)
						break;
					
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);						
				
						//Output folder
						String labelOutput = FunctionUtils.setOutputDestination("", file);
															
						//File output name
						String prefix = "";	
						if (casePrefix.isSelected())
						{
							prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);
						}
						
						String extensionName = "";	
						if (btnExtension.isSelected())
						{
							extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);
						}												
						
						//Container
						String container = comboFilter.getSelectedItem().toString();								
						
						//Output name
						String fileOutputName = labelOutput.replace("\\", "/") + "/" + prefix + fileName.replace(extension, extensionName + container); 
						
						//File output
						File fileOut = new File(fileOutputName);				
						if (fileOut.exists())		
						{						
							fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + fileName, extension, extensionName + "_", container);
							
							if (fileOut == null)
							{
								cancelled = true;
								break;
							}
							else if (fileOut.toString().equals("skip"))
							{
								continue;
							}
						}
						
						//Transcription folder
						File transcriptionFolder = new File(lblDestination1.getText() + "/transcription");		
						
						if (transcriptionFolder.exists())
						{
							for (File f : transcriptionFolder.listFiles()) 
							{
								f.delete();
							}
						}
						else
							transcriptionFolder.mkdir();

						//Wave output
						String waveFile = transcriptionFolder.toString() + "/" + fileName.replace(extension, ".wav");
						
						//Command
						String cmd = " -c:a pcm_s16le -ac 1 -ar 16000 -vn -y ";
										
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + '"' + waveFile + '"');		
						
						do {
							Thread.sleep(100);
						} while (FFMPEG.runProcess.isAlive());
						
						if (cancelled == false)
						{
							lblCurrentEncoding.setText(fileName);
							tempsEcoule.setVisible(false);
							
							boolean format = false;
							if (comboFilter.getSelectedItem().toString().equals(".srt"))
							{
								cmd = " --output-srt --max-len 74 --max-context 200 --split-on-word";
								format = true;
							}							
							else if (comboFilter.getSelectedItem().toString().equals(".vtt"))
							{
								cmd = " --output-vtt --max-len 74 --max-context 200 --split-on-word";
								format = true;
							}
							else if (comboFilter.getSelectedItem().toString().equals(".txt"))
							{
								cmd = " --output-txt";
							}
							
							WHISPER.run(" --language auto" + cmd + " --no-prints -f " + '"' + waveFile + '"');		
							
							do {
								Thread.sleep(100);
							} while (WHISPER.runProcess.isAlive());
							
							File transcribedFile = new File(waveFile.toString().replace(".wav", ".wav" + container));
							
							if (transcribedFile.exists())
							{
								if (format)
								{
									formatSubtitles(transcribedFile.toPath(), fileOut.toPath());
								}	
								else
									transcribedFile.renameTo(fileOut);
							}
						}
														
						if (FFMPEG.saveCode == false && btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")) == false)
						{
							//Removing temporary files
							for (File f : transcriptionFolder.listFiles()) 
							{
								f.delete();
							}
							
							transcriptionFolder.delete();
							
							lastActions(fileOut);
						}
						
					} catch (Exception e) {
						FFMPEG.error  = true;
					}			
				}

				if (btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
				{
					if (fileList.getSelectedValuesList().size() > 1)
					{
						//Reset data for the current selected file
						VideoPlayer.videoPath = null;
						VideoPlayer.setMedia();
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (VideoPlayer.loadMedia.isAlive());
						RenderQueue.frame.toFront();
					}
				}
				else
				{
					enfOfFunction();					
				}
			}
			
		});
		thread.start();
		
    }
	
	public static void formatSubtitles(Path input, Path output) throws IOException {

        String extension = comboFilter.getSelectedItem().toString();
        boolean isVtt = extension.equals(".vtt");

        List<String> lines = Files.readAllLines(input);
        List<String> out = new ArrayList<>();

        for (int i = 0; i < lines.size();) {
            String line = lines.get(i++).trim();
            if (line.isEmpty()) continue;

            String index = "";
            if (!isVtt && line.matches("\\d+")) { // SRT index
                index = line;
                line = lines.get(i++).trim();
            }

            String timing = line;
            if (isVtt) {
                timing = timing.replace(',', '.'); // VTT uses dots
            }

            StringBuilder textBlock = new StringBuilder();
            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                textBlock.append(" ").append(lines.get(i++).trim());
            }
            i++; // skip blank line

            String wrapped = wrapText(textBlock.toString().trim(), 37);

            if (!index.isEmpty()) out.add(index); // SRT index
            out.add(timing);
            out.addAll(Arrays.asList(wrapped.split("\n")));
            out.add(""); // blank separator
        }

        Files.write(output, out);
    }

    private static String wrapText(String text, int maxLineLength) {
    	
    	String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (!line.isEmpty()) lines.add(line.toString().trim());

        if (lines.size() > 2) { // max 2 lines
            String second = String.join(" ", lines.subList(1, lines.size()));
            lines = Arrays.asList(lines.get(0), second);
        }
        return String.join("\n", lines);
    }
	
	private static void lastActions(File fileOut) {
		
		FunctionUtils.cleanFunction(null, fileOut.toString(), fileOut, "");

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
	}
}
