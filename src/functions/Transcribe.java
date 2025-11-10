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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.Ftp;
import application.Shutter;
import application.Utils;
import application.VideoPlayer;
import library.FFMPEG;
import library.FFPROBE;
import library.WHISPER;
import settings.FunctionUtils;
import settings.InputAndOutput;
import settings.Timecode;

public class Transcribe extends Shutter {

	public static Thread thread;
	public static File transcriptionFolder;
	private static String currentFile = "";
	
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}|\\d{2}:\\d{2}:\\d{2},\\d{3})");
	
	public static void main() {
		
		thread = new Thread(new Runnable() {	
			
			@Override
			public void run() {
				
				if (scanIsRunning == false)
					FunctionUtils.completed = 0;
				
				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				for (int i = 0 ; i < list.getSize() ; i++)
				{	
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionTranscribe")) == false)
					{
						if (list.getElementAt(i).equals(VideoPlayer.videoPath) == false)
						{
							continue;
						}
						
						currentFile = list.getElementAt(i);
					}					
					
					File file = FunctionUtils.setInputFile(new File(list.getElementAt(i)));	
														
					if (file == null)
						break;
					
					try {
						
						String fileName = file.getName();
						String extension =  fileName.substring(fileName.lastIndexOf("."));
						
						lblCurrentEncoding.setText(fileName);	
						
						//Write the in and out values before getInputAndOutput()
						if (VideoPlayer.caseApplyCutToAll.isSelected())
						{							
							VideoPlayer.videoPath = file.toString();							
							VideoPlayer.updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
							VideoPlayer.updateGrpOut(Timecode.getNTSCtimecode(((double) FFPROBE.totalLength / 1000 * FFPROBE.accurateFPS) - InputAndOutput.savedOutPoint));							
							VideoPlayer.setFileList();	
						}
						
						//InOut	
						InputAndOutput.getInputAndOutput(VideoPlayer.getFileList(file.toString(), FFPROBE.totalLength));
				
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
						if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionTranscribe")) == false)
							container = ".srt";
						
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
						transcriptionFolder = new File(lblDestination1.getText() + "/transcription");
						
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
										
						FFMPEG.run(InputAndOutput.inPoint + " -i " + '"' + file.toString() + '"' + InputAndOutput.outPoint + cmd + '"' + waveFile + '"');		
						
						do {
							Thread.sleep(100);
						} while (FFMPEG.runProcess.isAlive());
						
						if (cancelled == false)
						{
							lblCurrentEncoding.setText(fileName);
							tempsEcoule.setVisible(false);
							
							boolean format = false;
							if (container.equals(".srt"))
							{
								cmd = " --output-srt --max-len 74 --max-context 200 --split-on-word";
								format = true;
							}							
							else if (container.equals(".vtt"))
							{
								cmd = " --output-vtt --max-len 74 --max-context 200 --split-on-word";
								format = true;
							}
							else if (container.equals(".txt"))
							{
								cmd = " --output-txt";
							}
							
							cmd += " --language " + WHISPER.comboLanguage.getSelectedItem().toString();
														
							WHISPER.run(cmd + " --no-prints -f " + '"' + waveFile + '"');		
							
							do {
								Thread.sleep(100);
							} while (WHISPER.runProcess.isAlive());
							
							File transcribedFile = new File(waveFile.toString().replace(".wav", ".wav" + container));
							
							if (transcribedFile.exists())
							{						        
								if (format)
								{
									if (InputAndOutput.inPoint != "")
									{
										String content = Files.readString(transcribedFile.toPath());
										String result = offsetAllTimestamps(content, Long.parseLong(InputAndOutput.inPoint.replace(" -ss ", "").replace("ms","")));
										File output = new File(transcribedFile.toString().replace(container, "") + "_with_offset" + container);
										Files.writeString(output.toPath(), result);
										formatSubtitles(output.toPath(), fileOut.toPath(), container);
									}
									else
										formatSubtitles(transcribedFile.toPath(), fileOut.toPath(), container);
								}	
								else
									formatText(transcribedFile.toPath(), fileOut.toPath());
							}
						}
														
						if (FFMPEG.saveCode == false)
						{
							lastActions(fileOut);
						}						
					}
					catch (Exception e)
					{
						FFMPEG.error  = true;
						e.printStackTrace();
					}	
					finally
					{							
						//Removing temporary files
						if (transcriptionFolder != null)
						{
							for (File f : transcriptionFolder.listFiles()) 
							{
								f.delete();
							}
							
							transcriptionFolder.delete();		
						}
					}
				}

				endOfFunction();	
				
				if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionTranscribe")) == false && FFMPEG.error == false && cancelled == false)
				{
					fileList.setSelectedValue(currentFile, true);						
					comboSubsSource.setSelectedIndex(0);
					caseAddSubtitles.setSelected(true);
					VideoPlayer.setMedia();					
					currentFile = "";
				}
			}
			
		});
		thread.start();
		
    }
	
	public static void formatText(Path input, Path output) throws IOException {
	    List<String> lines = Files.readAllLines(input);
	    List<String> cleaned = new ArrayList<>();

	    for (String line : lines) {
	        // Remove only leading whitespace, preserve internal and trailing
	        cleaned.add(line.replaceFirst("^\\s+", ""));
	    }

	    Files.write(output, cleaned);
	}
	
	public static void formatSubtitles(Path input, Path output, String extension) throws IOException {

		boolean isVtt = extension.equalsIgnoreCase(".vtt");

	    List<String> lines = Files.readAllLines(input);
	    List<String> out = new ArrayList<>();

	    if (isVtt) {
	        // Add mandatory header if missing
	        if (lines.isEmpty() || !lines.get(0).startsWith("WEBVTT")) {
	            out.add("WEBVTT");
	            out.add("");
	        }
	    }

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
	            timing = timing.replace(',', '.'); // ensure correct milliseconds format
	        }

	        // Read full text block until blank line
	        StringBuilder textBlock = new StringBuilder();
	        while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
	            String next = lines.get(i++).trim();
	            // merge speaker lines with spaces
	            if (textBlock.length() > 0) textBlock.append(" ");
	            textBlock.append(next);
	        }

	        // Wrap while preserving "- " speaker markers and 2-line rule
	        String wrapped = wrapText(textBlock.toString().trim(), 37);

	        if (!index.isEmpty()) out.add(index); // SRT numbering
	        out.add(timing);
	        out.addAll(Arrays.asList(wrapped.split("\n")));
	        out.add(""); // blank line separator
	    }

	    Files.write(output, out);
    }

    private static String wrapText(String text, int maxLineLength) {
    	
    	// Split into words
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            // Preserve "-" if it's a speaker prefix
            if (word.equals("-")) {
                if (line.length() > 0) {
                    lines.add(line.toString().trim());
                    line = new StringBuilder();
                }
                line.append("- ");
                continue;
            }

            if (line.length() + word.length() + 1 > maxLineLength) {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }

            line.append(word).append(" ");
        }
        if (line.length() > 0) lines.add(line.toString().trim());

        // Merge extra lines to ensure a max of 2
        if (lines.size() > 2) {
            String second = String.join(" ", lines.subList(1, lines.size()));
            lines = Arrays.asList(lines.get(0), second);
        }

        return String.join("\n", lines);
    }
    
    private static String offsetAllTimestamps(String text, long offsetMs) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = TIME_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            String original = matcher.group(1);
            String adjusted = offsetTimestamp(original, offsetMs);
            result.append(adjusted);
            lastEnd = matcher.end();
        }

        result.append(text.substring(lastEnd));
        return result.toString();
    }

    private static String offsetTimestamp(String timestamp, long offsetMs) {
        boolean isVtt = timestamp.contains(".");
        String[] parts = timestamp.split("[:,\\.]");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        long millis = Long.parseLong(parts[3]);

        long totalMs = (hours * 3600 + minutes * 60 + seconds) * 1000 + millis + offsetMs;
        if (totalMs < 0) totalMs = 0;

        long newH = totalMs / 3600000;
        long newM = (totalMs % 3600000) / 60000;
        long newS = (totalMs % 60000) / 1000;
        long newMs = totalMs % 1000;

        return String.format("%02d:%02d:%02d%s%03d",
                newH, newM, newS,
                isVtt ? "." : ",",
                newMs);
    }
	
	private static void lastActions(File fileOut) {
		
		FunctionUtils.cleanFunction(null, fileOut.toString(), fileOut, "");

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
	}
}
