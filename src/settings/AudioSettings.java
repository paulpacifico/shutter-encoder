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

package settings;

import java.io.File;

import application.RecordInputDevice;
import application.Shutter;
import library.FFPROBE;

public class AudioSettings extends Shutter {
		
	public static String setAudioFiles(String audioFiles, File file) {
		
    	int channels = 1;
		for (int i2 = 0 ; i2 < liste.getSize() ; i2++)
		{
			File audioName = new File(liste.getElementAt(i2));
			audioName = new File(audioName.getName().substring(0, audioName.getName().lastIndexOf(".")));							
			File videoName = new File(file.getName().substring(0, file.getName().lastIndexOf(".")));
			
			if (audioName.toString().contains(videoName.toString()) && audioName.toString().equals(videoName.toString()) == false) //L'audio contient le nom du fichier vidéo
				{
					audioFiles += " -i " + '"' + liste.getElementAt(i2) + '"' + " ";
					channels ++;
				}
		}
		
		for (int map = 0 ; map < channels ; map++)
		{
			if (map > 0)
				audioFiles += "-map " + map + ":a ";
		}
		
		return audioFiles;	
	}

	public static String setAudioEditingCodecs(String audioFiles) {

		//No audio
		if (comboAudio1.getSelectedIndex() == 16
		&& comboAudio2.getSelectedIndex() == 16
		&& comboAudio3.getSelectedIndex() == 16
		&& comboAudio4.getSelectedIndex() == 16
		&& comboAudio5.getSelectedIndex() == 16
		&& comboAudio6.getSelectedIndex() == 16
		&& comboAudio7.getSelectedIndex() == 16
		&& comboAudio8.getSelectedIndex() == 16)
		{
			return " -an";
		}
		
		String audio = " -c:a pcm_s16le";
		if (FFPROBE.qantization == 24)
			audio = " -c:a pcm_s24le";
		else if (FFPROBE.qantization == 32)
			audio = " -c:a pcm_s32le"; 
		
		if (Transitions.setAudioFadeIn() !=  "")
		{
			audio += " -filter:a " + Transitions.setAudioFadeIn();
		}
		
		if (Transitions.setAudioFadeOut() !=  "")
		{
			if (audio.contains("-filter:a"))
			{
				audio += ",";
			}
			else
				audio += " -filter:a ";
			
			audio += Transitions.setAudioFadeOut();
		}
		
		if (Transitions.setAudioSpeed() !=  "")
		{
			//No audio
			if (Transitions.setAudioSpeed().equals(" -an"))
			{
				return " -an";
			}
			
			if (audio.contains("-filter:a"))
			{
				audio += ",";
			}
			else
				audio += " -filter:a ";
			
			audio += Transitions.setAudioSpeed();
		}
			
        if (caseOPATOM.isSelected())
        {
        	return audioFiles + audio + " -ar 48000";
        }
		else
		{
			String mapping = "";
			if (inputDeviceIsRunning)
			{
				if (liste.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
					mapping = " -map a? -map 2?";
				else
					mapping = " -map a?";	
			}
			else if (comboAudio1.getSelectedIndex() == 0
				&& comboAudio2.getSelectedIndex() == 1
				&& comboAudio3.getSelectedIndex() == 2
				&& comboAudio4.getSelectedIndex() == 3
				&& comboAudio5.getSelectedIndex() == 4
				&& comboAudio6.getSelectedIndex() == 5
				&& comboAudio7.getSelectedIndex() == 6
				&& comboAudio8.getSelectedIndex() == 7)
			{
				return audio + " -ar 48000 -map a?";	
			}
			else
			{
				if (comboAudio1.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio1.getSelectedIndex()) + "?";
				if (comboAudio2.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio2.getSelectedIndex()) + "?";
				if (comboAudio3.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio3.getSelectedIndex()) + "?";
				if (comboAudio4.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio4.getSelectedIndex()) + "?";
				if (comboAudio5.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio5.getSelectedIndex()) + "?";
				if (comboAudio6.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio6.getSelectedIndex()) + "?";
				if (comboAudio7.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio7.getSelectedIndex()) + "?";
				if (comboAudio8.getSelectedIndex() != 16)
					mapping += " -map a:" + (comboAudio8.getSelectedIndex()) + "?";
			}
			
			return audio + " -ar 48000" + mapping;	
		}
	}
	
	public static String setAudioOutputCodecs(String filterComplex, String audioCodec) {
			
		String audioBitrate = debitAudio.getSelectedItem().toString();
		
		if (comboFonctions.getSelectedItem().toString().equals("DVD"))
			audioBitrate = "320";
		
		if (comboAudioCodec.getSelectedItem().equals(language.getProperty(("codecCopy"))))
		{
    		String mapping = "";
    		if (comboAudio1.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio1.getSelectedIndex()) + "?";
			if (comboAudio2.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio2.getSelectedIndex()) + "?";
			if (comboAudio3.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio3.getSelectedIndex()) + "?";
			if (comboAudio4.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio4.getSelectedIndex()) + "?";
			if (comboAudio5.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio5.getSelectedIndex()) + "?";
			if (comboAudio6.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio6.getSelectedIndex()) + "?";
			if (comboAudio7.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio7.getSelectedIndex()) + "?";
			if (comboAudio8.getSelectedIndex() != 16)
				mapping += " -map a:" + (comboAudio8.getSelectedIndex()) + "?";
			
			return " -c:a copy" + mapping;
		}
		else if (audioBitrate.equals("0") || comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio")))
		{
			return " -an";
		}
		else
		{
			String audio = "";
			if (audioCodec.equals("AC3"))
			{
				audioCodec = "ac3";
			}
			else if (audioCodec.equals("OPUS"))
			{
				audioCodec = "libopus";	
			}
			else if (audioCodec.equals("OGG"))
			{
				audioCodec = "libvorbis";	
			}
			else if (audioCodec.equals("Dolby Digital Plus"))
			{
				audioCodec = "eac3";
			}
			else if (audioCodec.equals("WMA"))
			{
				audioCodec = "wmav2";
			}
			else if (audioCodec.equals("MP3"))
			{
				audioCodec = "libmp3lame";
			}
			else if (audioCodec.equals("MP2"))
			{
				audioCodec = "mp2";
			}
			else if (audioCodec.equals("PCM 16Bits"))
			{
				if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
				{
					audioCodec = "pcm_s16be";
				}
				else
					audioCodec = "pcm_s16le";
			}
			else if (audioCodec.equals("PCM 24Bits"))
			{
				if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
				{
					audioCodec = "pcm_s24be";
				}
				else
					audioCodec = "pcm_s24le";
			}
			else if (audioCodec.equals("PCM 32Bits"))
			{
				if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
				{
					audioCodec = "pcm_s32be";
				}
				else
					audioCodec = "pcm_s32le";
			}
			else if (comboAudioCodec.getSelectedItem().toString().equals("PCM 32Float"))
			{
				audioCodec = "pcm_f32be";
			}
			else if (audioCodec.equals("FLAC"))
			{
				audioCodec = "flac";
			}
			else
				audioCodec = "aac";
			
			String transitions = "";
			
			if (Transitions.setAudioFadeIn() !=  "")
			{
				transitions += Transitions.setAudioFadeIn();
			}
			
			if (Transitions.setAudioFadeOut() !=  "")
			{
				if (transitions != "")	transitions += ",";
				
				transitions += Transitions.setAudioFadeOut();
			}
			
			if (Transitions.setAudioSpeed() !=  "")
			{
				//No audio
				if (Transitions.setAudioSpeed().equals(" -an"))
				{
					return " -an";
				}
				
				if (transitions != "")	transitions += ",";
				
				transitions += Transitions.setAudioSpeed();
			}
			
		    if (FFPROBE.stereo)
		    {
		    	if (FFPROBE.surround)
		    	{			    	
			    	if (lblAudioMapping.getText().equals("Multi"))
			    	{
			    		if (transitions != "")
				    		transitions = " -filter:a " + '"' + transitions + '"';
			    		
				    	FFPROBE.stereo = false; //permet de contourner le split audio				    	
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k" + transitions + " -map a:0";
			    	}
			    	else
			    	{			    		
			    		if (transitions != "")
				    		transitions = transitions + ",";
				    	
			    		String mono = "";
			    		if (lblAudioMapping.getText().equals(language.getProperty("mono")))
			    		{
			    			mono = " -ac1";
			    		}
			    		
				    	audio += " -c:a " + audioCodec + mono + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k -filter:a " + '"' + transitions + "pan=stereo|FL=FC+0.30*FL+0.30*BL|FR=FC+0.30*FR+0.30*BR" + '"' + " -map a?";
			    	}
			    }
		    	else if (lblAudioMapping.getText().equals("Multi"))
		    	{
		    		if (transitions != "") 
			    		transitions = "," + transitions;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
				    audio += "[a:0]pan=1c|c0=c" + comboAudio1.getSelectedIndex() + transitions + "[a1];[a:0]pan=1c|c0=c" + comboAudio2.getSelectedIndex() + transitions + "[a2]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";
		    	}
		    	else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
		    	{
		    		if (transitions != "") 
			    		transitions = "," + transitions;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
		    		
					if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown des pistes en mono
						audio += "[0:a]anull" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";
					else
					{
						if (comboAudio1.getSelectedIndex() == 0)
							audio += "[0:a]channelsplit=channel_layout=stereo:channels=FL" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";    	
						else
							audio += "[0:a]channelsplit=channel_layout=stereo:channels=FR" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";    	
					}
		    	}
		    	else //Stereo
		    	{		    		
		    		if (transitions != "")
			    		transitions = " -filter:a " + '"' + transitions + '"';
		    		
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k" + transitions + " -map a:" + comboAudio1.getSelectedIndex();
		    	}		    		
		    }
		    else if (FFPROBE.channels > 1)
		    {
		         if (inputDeviceIsRunning)
		         {
	        	 	if (transitions != "")
			    		transitions = "," + transitions;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
				    if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
				    {
				    	audio += "[0:a][2:a]amix=inputs=2" + transitions + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";   
				    }
				    else if (lblAudioMapping.getText().equals("Multi"))
			    	{
				    	if (transitions != "")
				    		transitions = " -filter:a " + '"' + transitions + '"';
			    		
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k" + transitions + " -map 0:a? -map 2:a?";
			    	}
				    else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
				    {
				    	if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown des pistes en mono
			    		{
				    		audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a][" + String.valueOf(comboAudio2.getSelectedIndex()).replace("1","2") + ":a]amerge=inputs=2" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";
			    		}
			    		else
			    			audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a]anull" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";
				    }
		         }
		    	 else if (lblAudioMapping.getText().equals(language.getProperty("stereo")))
    			 {
			    	if (transitions != "")
			    		transitions = "," + transitions;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
			    	audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + transitions + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";    		 
    			 }
		    	 else if (lblAudioMapping.getText().equals(language.getProperty("mono")))
		    	 {
		    		 if (transitions != "")
				    	transitions = "," + transitions;
				    	
		    		 if (filterComplex != "")
				    	audio += ";";
		    		 else
				    	audio += " -filter_complex " + '"';	
				    
		    		 if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown des pistes en mono
		    		 {
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k";
		    		 }
		    		 else
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "]anull" + transitions + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k"; 
		    	 }
		    	 else
		    	 {
		    		String mapping = "";
		    		if (comboAudio1.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio1.getSelectedIndex()) + "?";
					if (comboAudio2.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio2.getSelectedIndex()) + "?";
					if (comboAudio3.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio3.getSelectedIndex()) + "?";
					if (comboAudio4.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio4.getSelectedIndex()) + "?";
					if (comboAudio5.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio5.getSelectedIndex()) + "?";
					if (comboAudio6.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio6.getSelectedIndex()) + "?";
					if (comboAudio7.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio7.getSelectedIndex()) + "?";
					if (comboAudio8.getSelectedIndex() != 16)
						mapping += " -map a:" + (comboAudio8.getSelectedIndex()) + "?";
					
		    		if (transitions != "")
		    			transitions = " -filter:a " + '"' + transitions + '"';
		    		
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k" + transitions + mapping;
		    	 }		    	 
		    }
		    else
		    {
		    	if (transitions != "")
		    		transitions = " -filter:a " + '"' + transitions + '"';
		    	
		    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getText() + " -b:a " + audioBitrate + "k" + transitions + " -map a?";
		    }
		    
		    return audio;		   				    
		}
	}

	public static String setAudioBroadcastCodecs(String filterComplex) {
		
		//No audio
		if (comboAudio1.getSelectedIndex() == 16
			&& comboAudio2.getSelectedIndex() == 16
			&& comboAudio3.getSelectedIndex() == 16
			&& comboAudio4.getSelectedIndex() == 16
			&& comboAudio5.getSelectedIndex() == 16
			&& comboAudio6.getSelectedIndex() == 16
			&& comboAudio7.getSelectedIndex() == 16
			&& comboAudio8.getSelectedIndex() == 16)
		{
			return " -an";
		}
		else
		{			
			String audio = " -c:a pcm_s24le";
			if (caseAS10.isSelected() && comboAS10.getSelectedItem().toString().equals("NRK_HD_2012"))
				audio = " -c:a pcm_s16le";
				
	        if (caseConform.isSelected() && comboConform.getSelectedItem().toString().equals(language.getProperty("conformBySlowMotion")))
	        	return " -an";
	        
			return audio + " -ar 48000";
		}
	}
}
