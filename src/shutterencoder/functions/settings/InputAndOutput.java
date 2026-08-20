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

package shutterencoder.functions.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts.CutSegment;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;

public class InputAndOutput extends Shutter {

	public static String inPoint = "";
	public static String outPoint = "";
	public static double savedInPoint = 0;
	public static double savedOutPoint = 0;
	public static String segments = "";
		
	public static void getInputAndOutput(boolean setInputAndOutput) {
				
		inPoint = "";
		outPoint = "";
		segments = "";
		
		if (setInputAndOutput && FFPROBE.totalLength > 40)
		{
			//Multi cuts feature
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				setMultiCuts();
			}
			else
			{
				double timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600 + Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60 + Integer.parseInt(VideoPlayerUI.caseInS.getText())) * VideoPlayerUtils.getFPS() + Integer.parseInt(VideoPlayerUI.caseInF.getText());
				
				//NTSC framerate
				timeIn = Timecode.getNTSCtimecode(timeIn);
				timeIn = Timecode.getDropFrameTimecode(timeIn);
									
				if (timeIn > 0.0f)
		        {
					inPoint = " -ss " + (long) ((double) timeIn * VideoPlayerUI.inputFramerateMS) + "ms";
			    }
				
				if (VideoPlayerUI.playerMarkOut < VideoPlayerCore.waveformContainer.getWidth() && caseEnableSequence.isSelected() == false)
		        {				
					String framesText[] = VideoPlayerUI.lblDuration.getText().split(" ");
					Integer frames =  Integer.parseInt(framesText[framesText.length - 2]);
	
		        	if ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().contains("JPEG")) && caseCreateSequence.isSelected())
		        	{		        	
		        		double outputFPS = FFPROBE.accurateFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
			    		
			    		outPoint = " -frames:v " + (int) Math.ceil((double) frames / outputFPS);
		        	}
		        	else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))	        
		        	{
			        	outPoint = " -frames:v " + frames;
		        	}
		        	else
		        		outPoint = " -t " + (long) Math.floor((double) frames * ((float) 1000 / FFPROBE.accurateFPS)) + "ms";		        	
		        }
				
				if (VideoPlayerUI.comboMode.getSelectedItem().toString().equals(language.getProperty("splitMode")))
				{
					outPoint += " -f segment -segment_time " + VideoPlayerUI.splitValue.getText() + " -reset_timestamps 1";
				}
			}
		}
	}

	private static String setMultiCuts() {
			
		//Adding segments
		int i = 0;
		for (i = 0 ; i < VideoPlayerMultiCuts.cutSegments.size() ; i++)
    	{
			//Video segment
			if (FFPROBE.audioOnly == false)
			{
				String videoIndex = i > 0 ? "[" + i + ":v]" : ""; //first [0:v] is already added from FilterComplex.setFilterComplex()
				segments += videoIndex + "setpts=PTS-STARTPTS[v" + i + "];";
			}
			
			//Audio segment
			if (FFPROBE.hasAudio)
			{
				for (int c = 0 ; c < FFPROBE.channels ; c++)
				{
					segments += "[" + i + ":a:" + c + "]asetpts=PTS-STARTPTS[a" + i + "_c" + c + "];";
				}
			}
    	}
		
		//Concat output	
		for (int o = 0 ; o < VideoPlayerMultiCuts.cutSegments.size() ; o++)
    	{
			//Video segment
			if (FFPROBE.audioOnly == false)
			{
				segments += "[v" + o + "]";
			}
			
			//Audio segment
			if (FFPROBE.hasAudio)
			{
				for (int c = 0 ; c < FFPROBE.channels ; c++)
				{
					segments += "[a" + o + "_c" + c + "]";
				}
			}
    	}
		
		//Output streams
		String streams = "";
		if (FFPROBE.audioOnly)
		{
			streams = ":v=0:a=" + FFPROBE.channels;
		}
		else if (FFPROBE.hasAudio == false)
		{
			streams = ":v=1:a=0[video];[video]";
		}
		else //Video + Audio stream
		{
			streams = ":v=1:a=" + FFPROBE.channels + "[video]";
			
			for (int c = 0 ; c < FFPROBE.channels ; c++)
			{
				streams += "[audio_" + c + "]";
			}
			
			streams += ";[video]";
		}
		
		//Final output
		return segments += "concat=n=" + i + streams;
	}
	
	public static String setInputString(String beforeInput, String file, String afterInput) {
		
		String inputFiles = "";
		if (segments != "")
		{
			//Catch lavfi and the second -i inputFile
			String lavfi = "";
			if (afterInput.contains(" -f lavfi"))
			{
				lavfi = " -f lavfi";
			}
				
			Pattern pattern = Pattern.compile("-i\\s+(\"[^\"]*\")");
	        Matcher matcher = pattern.matcher(afterInput);

	        String secondInputFile = "";	        
	        if (matcher.find())
	        {
	        	if (Shutter.caseAddWatermark.isSelected() && Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
	            {
	        		secondInputFile = " -i " + matcher.group(1);
	        		matcher.find();
	        		lavfi += " -i " + matcher.group(1);	        		
	            }
	        	else if (Shutter.caseAddWatermark.isSelected())
	        	{
	        		secondInputFile = " -i " + matcher.group(1);
	        	}
	        	else if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
	        	{
	        		lavfi += " -i " + matcher.group(1);	     
	        	}
	        }
				        
			boolean isFirstInput = true;			
			for (CutSegment seg : VideoPlayerMultiCuts.cutSegments)
        	{
				double in = VideoPlayerMultiCuts.getSegmentTime(seg.inH, seg.inM, seg.inS, seg.inF);
                double out = VideoPlayerMultiCuts.getSegmentTime(seg.outH, seg.outM, seg.outS, seg.outF);
                double total = out - in;
                
                String inputHardware = beforeInput;

                //Add this global flags only once
                if (isFirstInput == false)
                {
                    inputHardware = inputHardware
                        .replaceAll("-init_hw_device\\s+[^\\s]+", "")
                        .replaceAll("-vaapi_device\\s+[^\\s]+", "")
                        .replaceAll("-filter_hw_device\\s+[^\\s]+", "");
                }
                
                //Set input point
                inputFiles += inputHardware + " -ss " + (long) ((double) in * VideoPlayerUI.inputFramerateMS) + "ms";
        				
				//Set output point
	        	if ((comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")) || comboFonctions.getSelectedItem().toString().contains("JPEG")) && caseCreateSequence.isSelected())
	        	{		        	
	        		double outputFPS = FFPROBE.accurateFPS / Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."));  
		    		
	        		inputFiles += " -frames:v " + (int) Math.ceil((double) total / outputFPS) + afterInput;
	        	}
	        	else if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySpeed")))	        
	        	{
	        		inputFiles += " -frames:v " + total + afterInput;
	        	}
	        	else
	        		inputFiles += " -t " + (long) Math.floor((double) total * ((float) 1000 / FFPROBE.accurateFPS)) + "ms" + afterInput;
	        	
	        	inputFiles += file;
	        	isFirstInput = false;
        	}
			
			if (secondInputFile != "" || lavfi != "")
			{			
				return inputFiles.replace(secondInputFile + lavfi, "") + secondInputFile + lavfi;
			}
			else			
				return inputFiles;
		}
		else		
			return beforeInput + file + afterInput;		
	}
}
