/*******************************************************************************************
* Copyright (C) 2026 PACIFICO PAUL
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
							
							//Set command
							boolean format = false;
							if (comboFilter.getSelectedItem().toString().equals(".srt") || comboFilter.getSelectedItem().toString().equals(".vtt"))
								format = true;								
														
							WHISPER.run(waveFile, fileName);		
															
							do {
								Thread.sleep(100);
							} while (WHISPER.runProcess.isAlive());
							
							File transcribedFile = new File(waveFile.toString().replace(".wav", container));
							
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
										formatSubtitles(output.toPath(), fileOut.toPath());
									}
									else
										formatSubtitles(transcribedFile.toPath(), fileOut.toPath());
								}	
								else
									Files.move(transcribedFile.toPath(), fileOut.toPath());
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
	
	public static void formatSubtitles(Path input, Path output) throws IOException {
		
		List<String> lines = Files.readAllLines(input);
        List<String> finalSub = new ArrayList<>();
        boolean isVtt = input.toString().toLowerCase().endsWith(".vtt");

        if (isVtt) finalSub.add("WEBVTT\n");

        int newIndex = 1;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss" + (isVtt ? "." : ",") + "SSS");

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            
            if (line.contains("-->")) {
                String timestamps = line;
                StringBuilder textCollector = new StringBuilder();
                
                // Collect all text until next empty line or next index
                int j = i + 1;
                while (j < lines.size() && !lines.get(j).trim().isEmpty()) {
                    textCollector.append(lines.get(j).trim()).append(" ");
                    j++;
                }

                List<String> chunks = splitLineChunks(textCollector.toString().trim());
                splitTimeAndAdd(finalSub, timestamps, chunks, newIndex, isVtt, timeFormatter);
                
                newIndex += chunks.size();
                i = j; 
            }
        }
        Files.write(output, finalSub);
    }
	
	private static List<String> splitLineChunks(String text) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        
        StringBuilder currentChunk = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();
        int linesInChunk = 0;

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > Integer.parseInt(WHISPER.textChars.getText())) {
                currentChunk.append(currentLine.toString().trim()).append("\n");
                currentLine.setLength(0);
                linesInChunk++;

                if (linesInChunk == Integer.parseInt(WHISPER.textLines.getText())) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk.setLength(0);
                    linesInChunk = 0;
                }
            }
            currentLine.append(word).append(" ");
        }
        
        if (currentLine.length() > 0 || currentChunk.length() > 0) {
            currentChunk.append(currentLine.toString().trim());
            chunks.add(currentChunk.toString().trim());
        }
        return chunks;
    }

	private static void splitTimeAndAdd(List<String> result, String timestampRange, List<String> chunks, int index, boolean isVtt, DateTimeFormatter formatter) {
        // Normalize separators for parsing
        String normalizedRange = timestampRange.replace(",", ".");
        String[] parts = normalizedRange.split(" --> ");
        
        // Handle VTT short timestamps (mm:ss.SSS) if necessary
        LocalTime start = parseTimestamp(parts[0]);
        LocalTime end = parseTimestamp(parts[1]);
        
        long totalDuration = ChronoUnit.MILLIS.between(start, end);
        int totalChars = chunks.stream().mapToInt(String::length).sum();

        LocalTime currentStart = start;
        for (int i = 0; i < chunks.size(); i++) {
            double ratio = (double) chunks.get(i).length() / Math.max(1, totalChars);
            long chunkDuration = (long) (totalDuration * ratio);
            LocalTime currentEnd = (i == chunks.size() - 1) ? end : currentStart.plus(chunkDuration, ChronoUnit.MILLIS);

            if (!isVtt) result.add(String.valueOf(index + i));
            result.add(currentStart.format(formatter) + " --> " + currentEnd.format(formatter));
            result.add(chunks.get(i));
            result.add("");
            currentStart = currentEnd;
        }
    }

    private static LocalTime parseTimestamp(String ts) {
        // VTT can sometimes omit the hour (00:00.000)
        if (ts.split(":").length == 2) ts = "00:" + ts;
        return LocalTime.parse(ts, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
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
