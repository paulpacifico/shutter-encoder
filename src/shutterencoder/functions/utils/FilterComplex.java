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

package shutterencoder.functions.utils;

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import shutterencoder.functions.settings.AudioSettings;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Transitions;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts;

public class FilterComplex extends Shutter {

	public static String setFilterComplex(String filterComplex, String audio, boolean picture) {
	
		//No audio
		if (picture)
		{
			if (filterComplex != "")   	   
	        	filterComplex = " -filter_complex " + '"' + filterComplex + "[out]" + '"' + " -map " + '"' + "[out]" + '"';
	        
	        return filterComplex;
		}
		
		//Hardware encoding Vulkan
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
					if (FunctionUtils.checkPreviousFilterVulkan(filterComplex) == false || Shutter.caseLUTs.isSelected())
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
				}
				else if (filterComplex.contains("format=rgb"))
				{
					if (filterComplex != "")
						filterComplex += ",";
					
					if (caseColorspace.isSelected() && comboColorspace.getSelectedItem().toString().contains("10bits"))
					{
						filterComplex += "format=p010";
					}
					else
						filterComplex += "format=nv12";
				}
				
			break;		
				
			case "Apple ProRes":
				
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboAccel.getSelectedItem().equals("Vulkan Video"))			
				{		
					if (filterComplex != "")
						filterComplex += ",";
					
					if (comboFilter.getSelectedItem().toString().contains("444"))
					{
						filterComplex += "format=yuv444p10le,hwupload";
					}
					else
						filterComplex += "format=yuv422p10,hwupload";
				}
				
			break;
		}
		
		if (caseOPATOM.isSelected())
			audio = "";
		
		//Multiple cuts
		filterComplex = setMultipleCutFilter(filterComplex);
		
	    if (filterComplex != "")
	    {	          	
	    	//Si une des cases est sélectionnée alors il y a déjà [0:v]
	    	if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
	    	{
	    		filterComplex = " -filter_complex " + '"' + filterComplex + "[out]";
	    	}
	    	else
	    		filterComplex = " -filter_complex " + '"' + "[0:v]" + filterComplex + "[out]";
	    	
	    	//Multiple cuts
	    	multipleCutsMapping multiCuts = setMultipleCutsMapping(filterComplex, audio);
	    	filterComplex = multiCuts.filterComplex();
	    	audio = multiCuts.audio();
	
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
	    		filterComplex += " -c:s srt" + FunctionUtils.setMapSubtitles();
	    	else
	    		filterComplex += " -c:s mov_text" + FunctionUtils.setMapSubtitles();    	
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
	    }
	    
	    return filterComplex;
	}

	public static String setMultipleCutFilter(String filterComplex) {
		
		//Multiple cuts
		if (InputAndOutput.segments != "")
		{
			String filter = filterComplex != "" ? filterComplex : "null";
			
			filterComplex = InputAndOutput.segments + filter;
			
			if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
			{
				//Move [0:v] before the segments
				if (Shutter.caseAddWatermark.isSelected())
				{
					filterComplex = "[0:v]" + filterComplex.replace("[0:v]", "[video]"); 
				}
				else
					filterComplex = "[0:v]" + filterComplex.replace("[0:v]", ""); 
				
				//Replace [1:v] to the correct value
				if (Shutter.caseAddWatermark.isSelected() && Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
				{
					filterComplex = filterComplex.replace(";[video][1:v]", ";[" + VideoPlayerMultiCuts.cutSegments.size() + ":v]");
					filterComplex = filterComplex.replace("[2:v]overlay", "[" + (VideoPlayerMultiCuts.cutSegments.size() + 1) + ":v]overlay");
				}
				else if (Shutter.caseAddWatermark.isSelected())		
				{
					filterComplex = filterComplex.replace(";[video][1:v]", ";[" + VideoPlayerMultiCuts.cutSegments.size() + ":v]");
				}
				else if (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn)
				{
					filterComplex = filterComplex.replace("[1:v]overlay", "[" + VideoPlayerMultiCuts.cutSegments.size() + ":v]overlay");
				}
			}
		}
		
		return filterComplex;
	}
	
 	public record multipleCutsMapping(String filterComplex, String audio) {}
	
	public static multipleCutsMapping setMultipleCutsMapping(String filterComplex, String audio) {
	
		//Multiple cuts
	    if (InputAndOutput.segments != "" && InputAndOutput.segments.contains("[audio_"))
	    {  	    
	    	//Moving filter:a to filterComplex
	    	String audioOutputName = "audio_";
	    	if (audio.contains("filter:a")) 
	    	{
	    		audioOutputName = "a";
	    		
	    		String audioFilter = audio.substring(audio.indexOf("-filter:a"));
	    		String s[] = audioFilter.split("\"");
	    		audioFilter = s[1];
	
	    		audio = audio.replace(" -filter:a " + '"' + audioFilter + '"', ""); //Fetch only audio codec  	
	    		
	    		for (int c = 0 ; c < FFPROBE.channels ; c++)
	    		{
	    			filterComplex += ";[audio_" + c + "]" + audioFilter + "[a" + c + "]"; 	    			
	    		}
	    	}
	    	
	        //No audio
	        if (audio.contains("-an"))
	        {
	            for (int c = 0 ; c < FFPROBE.channels ; c++)
	            {
	                filterComplex += ";[" + audioOutputName + c +"]anullsink";
	            }   
	        }
	        else if (audio.contains("-map a?")) //Add non mapped track to anullsink
	        {
	        	String tracks = "";
	            for (int c = 0 ; c < FFPROBE.channels ; c++)
	            {
	                tracks += " -map " + '"' + "[" + audioOutputName + c +"]" + '"';
	            }
	            
	            audio = audio.replace(" -map a?", tracks);
	        }
	        else if (audio.contains("[0:a]")) //Add non mapped track to anullsink
	        {
	            if (audio.contains("amix=inputs=" + FFPROBE.channels))
	            {
	                String tracks = "";
	                for (int c = 0 ; c < FFPROBE.channels ; c++)
	                {
	                    tracks += "[" + audioOutputName + c +"]";
	                }
	                
	                audio = audio.replace("[0:a]", tracks);
	            }
	            else
	            {
	                audio = audio.replace("[0:a]", "[" + audioOutputName + "0]");
	                
	                if (FFPROBE.channels > 1)
	                {
	                    for (int c = 1 ; c < FFPROBE.channels ; c++)
	                    {
	                        filterComplex += ";[" + audioOutputName + c +"]anullsink"; //null output
	                    }
	                }
	            }
	        }
	        else if (audio.contains("[0:a:")) //Add non mapped track to anullsink
	        {
	            Matcher m = Pattern.compile("0:a:(\\d+)").matcher(audio);
	
	            //Add anullsink to non-mapped audio tracks
	            for (int c = 0 ; c < FFPROBE.channels ; c++)
	            {
	                boolean map = false;
	                m.reset();
	                while (m.find())
	                {
	                    int channel = Integer.parseInt(m.group(1));
	                    
	                    if (c == channel)
	                    {
	                        map = true;
	                        break;
	                    }
	                }
	                
	                if (map == false)
	                {
	                    filterComplex += ";[" + audioOutputName + c +"]anullsink"; //null output
	                }
	            }   			
	            
	            //Convert [0:a:x] to [audio_x]
	            audio = audio.replaceAll("\\[0:a:(\\d+)\\]", "[" + audioOutputName + "$1]");  			
	        }
	        else if (audio.contains("-map a:")) //Add non mapped track to anullsink
	        {
	            Matcher m = Pattern.compile("a:(\\d+)").matcher(audio);
	
	            //Add anullsink to non-mapped audio tracks
	            for (int c = 0 ; c < FFPROBE.channels ; c++)
	            {
	                boolean map = false;
	                m.reset();
	                while (m.find())
	                {
	                    int channel = Integer.parseInt(m.group(1));
	                    
	                    if (c == channel)
	                    {
	                        map = true;
	                        break;
	                    }
	                }
	                
	                if (map == false)
	                {
	                    filterComplex += ";[" + audioOutputName + c +"]anullsink"; //null output
	                    audio = audio.replace("-map a:" + c + "?", ""); //Remove the mapping
	                }
	            } 
	            
	            //Removes non existing sources with -map a:x?
	            m.reset();
	            while (m.find())
	            {
	                int channel = Integer.parseInt(m.group(1));
	                if ((channel + 1) > FFPROBE.channels)
	                {
	                    audio = audio.replace(" -map a:" + channel + "?", "");     
	                }
	            }
	            
	            //Convert -map a:x? to [audio_x]
	            audio = audio.replaceAll("-map\\s+a:(\\d+)\\??", "-map \"[" + audioOutputName + "$1]\"");
	        }
	    }
	    
	    return new multipleCutsMapping(filterComplex, audio);
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
		
		//Multiple cuts
		filterComplex = setMultipleCutFilter(filterComplex);
		
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
				mapping += " -c:s mov_text" + FunctionUtils.setMapSubtitles();
			}
			
			return setMultipleCutsMappingBroadcastCodecs(mapping);
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
						if (list.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
						{
							mapping = " -map a? -map 2?";
						}
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
	
						mapping += " -map a:" + (map - 1);						
					}					
				}
				else //On ajoute une piste silencieuse
				{
					FunctionUtils.silentTrack = " -f lavfi -i anullsrc=r=" + lbl48k.getSelectedItem().toString() + ":cl=mono";
					
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM"))
						FunctionUtils.silentTrack += " -shortest -map_metadata -1";
					
					int map = 1;
					if (InputAndOutput.segments != "")
					{
						map = VideoPlayerMultiCuts.cutSegments.size();
					}
					
					if (Shutter.caseAddWatermark.isSelected() && (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
					{
						mapping += " -map " + (map + 2);	
					}
					else if (Shutter.caseAddWatermark.isSelected() || (Shutter.caseAddSubtitles.isSelected() && subtitlesBurn))
					{
						mapping += " -map " + (map + 1);
					}
					else
						mapping += " -map " + map;	
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
			mapping += " -c:s mov_text" + FunctionUtils.setMapSubtitles();
	    }
	    
		return setMultipleCutsMappingBroadcastCodecs(mapping);
	}

	public static String setMultipleCutsMappingBroadcastCodecs(String mapping) {
	
		//Multiple cuts
		if (InputAndOutput.segments != "" && InputAndOutput.segments.contains("[audio_"))
	    {        	
			//Moving filter:a to filterComplex
	    	String audioOutputName = "audio_";
	    	if (mapping.contains("filter:a")) 
	    	{
	    		audioOutputName = "a";
	    		
	    		String audioFilter = mapping.substring(mapping.indexOf("-filter:a"));
	    		String s[] = audioFilter.split("\"");
	    		audioFilter = s[1];
	
	    		mapping = mapping.replace(" -filter:a " + '"' + audioFilter + '"', ""); //Fetch only audio codec  	
	    		
	    		String tracks = "";
	    		for (int c = 0 ; c < FFPROBE.channels ; c++)
	    		{
	    			tracks += ";[audio_" + c + "]" + audioFilter + "[a" + c + "]"; 	    			
	    		}
	    		
	    		mapping = mapping.replaceFirst("\\[out\\]", "[out]" + tracks);
	    	}
	    	
	    	mapping = mapping.replace("0:a", audioOutputName + "0");
			mapping = mapping.replace(" -map a?", " -map " + '"' + "[" + audioOutputName + "0]" + '"' );
			
			//No audio
			if (mapping.contains("-an"))
			{
	    		for (int c = 0 ; c < FFPROBE.channels ; c++)
	    		{
	    			mapping = mapping.replaceFirst("\\[out\\]", "[out];[" + audioOutputName + c +"]anullsink");
	    		}   
			}
			else if (mapping.contains("-map a:")) //Add non mapped track to anullsink
			{
				Matcher m = Pattern.compile("a:(\\d+)").matcher(mapping);
	
				//Add anullsink to non-mapped audio tracks
				for (int c = 0 ; c < FFPROBE.channels ; c++)
	    		{
					boolean map = false;
					m.reset();
					while (m.find())
	    			{
	    			    int channel = Integer.parseInt(m.group(1));
	    			    
	    			    if (c == channel)
	    			    {
	    			    	map = true;
	    			    	break;
	    			    }
	    			}
					
					if (map == false)
					{
						mapping = mapping.replaceFirst("\\[out\\]", "[out];[" + audioOutputName + c +"]anullsink"); //null output
						mapping = mapping.replace("-map a:" + c, ""); //Remove the mapping
					}
	    		} 
			
				//Removes non existing sources with -map 0:x
				m.reset();
				while (m.find())
				{
					int channel = Integer.parseInt(m.group(1));
					if ((channel + 1) > FFPROBE.channels)
					{
						mapping = mapping.replace(" -map a:" + channel, "");     
					}
				}
				
				//Convert -map a:x to [audio_x]
				mapping = mapping.replaceAll("-map\\s+a:(\\d+)\\??", "-map \"[" + audioOutputName + "$1]\"");      			
			}
	    }
		
		return mapping;
	}
}
