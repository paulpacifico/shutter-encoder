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

package settings;

import java.awt.Component;
import java.io.File;

import javax.swing.JComboBox;

import application.Equalizer;
import application.RecordInputDevice;
import application.Shutter;
import application.Utils;
import functions.AudioNormalization;
import functions.Colorize;
import library.FFMPEG;
import library.FFPROBE;

public class AudioSettings extends Shutter {
		
	public static String setAudioFiles(String audioFiles, File file) {
		
    	int channels = 1;
		for (int i2 = 0 ; i2 < list.getSize() ; i2++)
		{
			File audioName = new File(list.getElementAt(i2));
			audioName = new File(audioName.getName().substring(0, audioName.getName().lastIndexOf(".")));							
			File videoName = new File(file.getName().substring(0, file.getName().lastIndexOf(".")));
			
			if (audioName.toString().contains(videoName.toString()) && audioName.toString().equals(videoName.toString()) == false) //L'audio contient le nom du fichier vidéo
				{
					audioFiles += " -i " + '"' + list.getElementAt(i2) + '"' + " ";
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

	public static String setAudioMapping(String filterComplex, String audioCodec, String audioFiles, File file) {
			
		String audioBitrate = "";
		
		boolean isEditingCodec = false;
		boolean isBroadcastCodec = false;
		
		if (grpBitrate.isVisible() || comboFonctions.getSelectedItem().toString().equals("DVD"))
		{
			audioBitrate = " -b:a " + debitAudio.getSelectedItem().toString() + "k";
		}		
		else if (comboFonctions.getSelectedItem().toString().equals("DVD"))
		{
			audioBitrate = " -b:a 320k";
		}
		else if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
		|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
		|| comboFonctions.getSelectedItem().toString().contains("XAVC")) //Broadcast codecs
		{
			isBroadcastCodec = true;
		}
		else //Editing codecs
		{
			isEditingCodec = true;
		}
		
		if (comboAudioCodec.getSelectedItem().equals(language.getProperty(("codecCopy"))))
		{
			String mapping = "";
			
			if (comboAudio1.getSelectedIndex() == 0
			&& comboAudio2.getSelectedIndex() == 1
			&& comboAudio3.getSelectedIndex() == 2
			&& comboAudio4.getSelectedIndex() == 3
			&& comboAudio5.getSelectedIndex() == 4
			&& comboAudio6.getSelectedIndex() == 5
			&& comboAudio7.getSelectedIndex() == 6
			&& comboAudio8.getSelectedIndex() == 7)
			{
    			mapping = " -map a?";	
    			
    			if (inputDeviceIsRunning && list.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
    			{
    				mapping += " -map 2?";
    			}
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
			
			return " -c:a copy" + mapping;
		}
		else if ((debitAudio.getSelectedItem().toString().equals("0") && audioCodec != "FLAC" && isEditingCodec == false && isBroadcastCodec == false)
		|| comboAudioCodec.getSelectedItem().equals(language.getProperty("noAudio"))
		|| (grpImageSequence.isVisible() && caseEnableSequence.isSelected() && Colorize.sourceFile == null))
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
			else if (audioCodec.equals("Opus"))
			{
				audioCodec = "libopus";	
			}
			else if (audioCodec.equals("Vorbis"))
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
				
				audioBitrate = " -compression_level " + debitAudio.getSelectedItem().toString();	
			}
			else if (audioCodec.equals("ALAC 16Bits"))
			{
				audioCodec = "alac";
				audioBitrate = " -sample_fmt s16p";
			}
			else if (audioCodec.equals("ALAC 24Bits"))
			{
				audioCodec = "alac";
				audioBitrate = " -sample_fmt s32p";
			}
			else
			{
				if (System.getProperty("os.name").contains("Mac"))
				{
					audioCodec = "aac_at";
				}
				else
					audioCodec = "aac";
			}
			
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
				//No audio
				if (Transitions.setAudioSpeed().equals(" -an"))
				{
					return " -an";
				}
				
				if (audioFiltering != "") audioFiltering += ",";
				
				audioFiltering += Transitions.setAudioSpeed();
			}
			
			//Audio normalization		
			if (caseNormalizeAudio.isSelected() && caseNormalizeAudio.isVisible())
			{				
	        	AudioNormalization.main(file);
	        							
	        	do {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				} while (AudioNormalization.thread.isAlive());
	        	
	        	lblCurrentEncoding.setText(file.getName());
				
				if (audioFiltering != "") audioFiltering += ",";
							
				audioFiltering += "volume=" + String.valueOf(FFMPEG.newVolume).replace(",", ".") + "dB";				
			}
			
			if (caseOPATOM.isSelected())
	        {
	        	return audioFiles + audio + " -ar " + lbl48k.getSelectedItem().toString();
	        }
			
			if (grpSetAudio.isVisible() && caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")) && isBroadcastCodec == false)
			{
				return setCustomAudio(isBroadcastCodec, audioFiltering);
			}
			else if (FFPROBE.stereo)
		    {
		    	if (FFPROBE.surround && lblAudioMapping.getSelectedItem().toString().equals("Multi") == false)
		    	{			    			    		
		    		if (audioFiltering != "")
			    		audioFiltering = audioFiltering + ",";
			    	
		    		String mono = "";
		    		if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("mono")))
		    		{
		    			mono = " -ac 1";
		    		}
		    		
		    		String channelMix = "";
		    		if (FFPROBE.channelLayout.equals("7.1"))
		    		{
		    			channelMix = "pan=stereo|FL=0.6*FL+0.5*FC+0.3*BL+0.3*SL+0.2*LFE|FR=0.6*FR+0.5*FC+0.3*BR+0.3*SR+0.2*LFE";
		    		}
		    		else if (FFPROBE.channelLayout.equals("6.1"))
		    		{
		    			channelMix = "pan=stereo|FL=0.6*FL+0.5*FC+0.3*BC+0.3*SL+0.2*LFE|FR=0.6*FR+0.5*FC+0.3*BC+0.3*SR+0.2*LFE";
		    		}
		    		else //5.1
		    		{
		    			channelMix = "pan=stereo|FL=0.707*FL+0.707*FC+0.5*BL+0.5*SL|FR=0.707*FR+0.707*FC+0.5*BR+0.5*SR";
		    		}
		    			
	    			audio += " -c:a " + audioCodec + mono + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + " -filter:a " + '"' + audioFiltering + channelMix + '"' + " -map a?";
			    }
		    	else if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
		    	{					    		
				    String mapping = "";
				    				
				    if (comboAudio1.getSelectedIndex() == 0
    				&& comboAudio2.getSelectedIndex() == 1
    				&& comboAudio3.getSelectedIndex() == 2
    				&& comboAudio4.getSelectedIndex() == 3
    				&& comboAudio5.getSelectedIndex() == 4
    				&& comboAudio6.getSelectedIndex() == 5
    				&& comboAudio7.getSelectedIndex() == 6
    				&& comboAudio8.getSelectedIndex() == 7)
    				{
				    	if (isBroadcastCodec == false)
		    			{
		    				mapping = " -map a?";	
		    			}
		    			
		    			if (inputDeviceIsRunning && list.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
		    			{
		    				mapping += " -map 2?";
		    			}
    				}
    				else if (isBroadcastCodec == false)
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

				    if (audioCodec.equals("libopus") && FFPROBE.channelLayout != "")
				    {
				    	mapping += " -channel_layout " + FFPROBE.channelLayout;
				    }
					
				    if (isBroadcastCodec) //Managed from FunctionUtils
		    		{
				    	audioFiltering = "";
		    		}
				    else if (audioFiltering != "")
		    		{
		    			audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
		    		}

		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + audioFiltering + mapping;
		    	}
		    	else if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("mono")))
		    	{
		    		if (audioFiltering != "") 
			    		audioFiltering = "," + audioFiltering;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
		    		
					if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown all tracks to mono
					{
						audio += "[0:a]anull" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
					}
					else
					{
						if (comboAudio1.getSelectedIndex() == 0)
							audio += "[0:a]channelsplit=channel_layout=stereo:channels=FL" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;    	
						else
							audio += "[0:a]channelsplit=channel_layout=stereo:channels=FR" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;    	
					}
		    	}
		    	else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
		    	{
		    		if (audioFiltering != "") 
			    		audioFiltering = "," + audioFiltering;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';
				    
		    		audio += "[0:a]amix=inputs=" + FFPROBE.channels + audioFiltering + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
		    	}
		    	else //Stereo
		    	{		    		
		    		if (audioFiltering != "")
			    		audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
		    		
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + audioFiltering + " -map a:" + comboAudio1.getSelectedIndex();
		    	}		    		
		    }
		    else if (FFPROBE.channels > 1)
		    {
		         if (inputDeviceIsRunning)
		         {
	        	 	if (audioFiltering != "")
			    		audioFiltering = "," + audioFiltering;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
				    if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("stereo")))
				    {
				    	audio += "[0:a][2:a]amix=inputs=2" + audioFiltering + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;   
				    }
				    else if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
			    	{
				    	if (audioFiltering != "")
				    		audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
			    		
				    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + audioFiltering + " -map 0:a? -map 2:a?";
			    	}
				    else if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("mono")))
				    {
				    	if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown all tracks to mono
			    		{
				    		audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a][" + String.valueOf(comboAudio2.getSelectedIndex()).replace("1","2") + ":a]amerge=inputs=2" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
			    		}
			    		else
			    			audio += "[" + String.valueOf(comboAudio1.getSelectedIndex()).replace("1","2") + ":a]anull" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
				    }
				    else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
			    	{
			    		audio += "[0:a]amix=inputs=" + FFPROBE.channels + audioFiltering + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
			    	}
		         }
		    	 else if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("stereo")))
    			 {
			    	if (audioFiltering != "")
			    		audioFiltering = "," + audioFiltering;
			    	
				    if (filterComplex != "")
				    	audio += ";";
				    else
				    	audio += " -filter_complex " + '"';	
				    
			    	audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + audioFiltering + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;    		 
    			 }
		    	 else if (lblAudioMapping.getSelectedItem().toString().equals(language.getProperty("mono")))
		    	 {
		    		 if (audioFiltering != "")
				    	audioFiltering = "," + audioFiltering;
				    	
		    		 if (filterComplex != "")
				    	audio += ";";
		    		 else
				    	audio += " -filter_complex " + '"';	
				    
		    		 if (comboAudio1.getSelectedIndex() != 16 && comboAudio2.getSelectedIndex() != 16) //Mixdown all tracks to mono
		    		 {
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "][0:a:" + comboAudio2.getSelectedIndex() + "]amerge=inputs=2" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
		    		 }
		    		 else
		    			 audio += "[0:a:" + comboAudio1.getSelectedIndex() + "]anull" + audioFiltering + "[a]" + '"' + " -ac 1 -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate; 
		    	 }
		    	 else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
		    	 {
		    		 if (audioFiltering != "") 
		    			 audioFiltering = "," + audioFiltering;
			    	
		    		 if (filterComplex != "")
		    			 audio += ";";
		    		 else
		    			 audio += " -filter_complex " + '"';
				    
		    		 audio += "[0:a]amix=inputs=" + FFPROBE.channels + audioFiltering + "[a]" + '"' + " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate;
		    	 }
		    	 else //Multi
		    	 {
		    		String mapping = "";
		    		
		    		if (comboAudio1.getSelectedIndex() == 0
    				&& comboAudio2.getSelectedIndex() == 1
    				&& comboAudio3.getSelectedIndex() == 2
    				&& comboAudio4.getSelectedIndex() == 3
    				&& comboAudio5.getSelectedIndex() == 4
    				&& comboAudio6.getSelectedIndex() == 5
    				&& comboAudio7.getSelectedIndex() == 6
    				&& comboAudio8.getSelectedIndex() == 7)
    				{
		    			
		    			if (isBroadcastCodec == false)
		    			{
		    				mapping = " -map a?";	
		    			}
		    			
		    			if (inputDeviceIsRunning && list.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 && RecordInputDevice.overlayAudioDeviceIndex > 0)
		    			{
		    				mapping += " -map 2?";
		    			}
    				}
    				else if (isBroadcastCodec == false)
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
		    								
		    		if (isBroadcastCodec) //Managed from FunctionUtils
		    		{
				    	audioFiltering = "";
		    		}
				    else if (audioFiltering != "")
		    		{
		    			audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
		    		}
		    		
		    		audio += " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + audioFiltering + mapping;
		    	 }		    	 
		    }
		    else
		    {
		    	if (isBroadcastCodec) //Managed from FunctionUtils
	    		{
			    	audioFiltering = "";
	    		}
			    else if (audioFiltering != "")
	    		{
	    			audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
	    		}
		    	
		    	audio += " -c:a " + audioCodec + " -ar " + lbl48k.getSelectedItem().toString() + audioBitrate + audioFiltering + " -map a?";
		    }
		    
		    return audio;		   				    
		}
	}

	public static String setEQ(String filter) {
		
		if (Shutter.caseEqualizer.isSelected())
		{					
			if (Equalizer.sliderEQ1.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=60:g=" + Equalizer.sliderEQ1.getValue();		
			}
			if (Equalizer.sliderEQ2.getValue() != 0)
			{	
				if (filter != "") filter += ",";				
				filter += "equalizer=f=170:g=" + Equalizer.sliderEQ2.getValue();
			}
			if (Equalizer.sliderEQ3.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=310:g=" + Equalizer.sliderEQ3.getValue();
			}
			if (Equalizer.sliderEQ4.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=600:g=" + Equalizer.sliderEQ4.getValue();
			}
			if (Equalizer.sliderEQ5.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=1000:g=" + Equalizer.sliderEQ5.getValue();
			}
			if (Equalizer.sliderEQ6.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=3000:g=" + Equalizer.sliderEQ6.getValue();
			}
			if (Equalizer.sliderEQ7.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=6000:g=" + Equalizer.sliderEQ7.getValue();
			}
			if (Equalizer.sliderEQ8.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=12000:g=" + Equalizer.sliderEQ8.getValue();
			}
			if (Equalizer.sliderEQ9.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=14000:g=" + Equalizer.sliderEQ9.getValue();
			}
			if (Equalizer.sliderEQ10.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "equalizer=f=16000:g=" + Equalizer.sliderEQ10.getValue();		
			}	
			if (Equalizer.sliderGain.getValue() != 0)
			{
				if (filter != "") filter += ",";				
				filter += "volume=" + Equalizer.sliderGain.getValue() + "dB";		
			}	
		}
		
		return filter;
	}
	
	@SuppressWarnings("rawtypes")
	public static String setCustomAudio(Boolean isBroadcastCodec, String audioFiltering) {
		
		//Mapping
		String audioMapping = "";
		String audioCodec = "";
		String codecMapping = "";
		String bitrateMapping = "";
		String rateMapping = "";
		String languageMapping = "";
		
		int i = 0;
		for (Component c : grpSetAudio.getComponents())
		{
			if (c instanceof JComboBox && ((JComboBox) c).getName().matches("comboAudio[0-9]+"))
			{		
				if (((JComboBox) c).getSelectedIndex() != 16)
				{
					audioMapping += " -map a:" + (((JComboBox) c).getSelectedIndex()) + "?";
					
					//Codec					
					for (Component a : grpSetAudio.getComponents())
					{
						if (a instanceof JComboBox && ((JComboBox) a).getName().matches("comboAudioCodec[0-9]+") && ((JComboBox) a).getName().endsWith(String.valueOf(i+1)))
						{				
							audioCodec = ((JComboBox) a).getSelectedItem().toString();
							String codec = "";
							
							if (audioCodec.equals("AC3"))
								codec = "ac3";
							else if (audioCodec.equals("Opus"))
								codec = "libopus";	
							else if (audioCodec.equals("Vorbis"))
								codec = "libvorbis";	
							else if (audioCodec.equals("Dolby Digital Plus"))
								codec = "eac3";
							else if (audioCodec.equals("WMA"))
								codec = "wmav2";
							else if (audioCodec.equals("MP3"))
								codec = "libmp3lame";
							else if (audioCodec.equals("MP2"))
								codec = "mp2";
							else if (audioCodec.equals("PCM 16Bits"))
							{
								if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
								{
									codec = "pcm_s16be";
								}
								else
									codec = "pcm_s16le";
							}
							else if (audioCodec.equals("PCM 24Bits"))
							{
								if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
								{
									codec = "pcm_s24be";
								}
								else
									codec = "pcm_s24le";
							}
							else if (audioCodec.equals("PCM 32Bits"))
							{
								if (comboFonctions.getSelectedItem().toString().equals("MJPEG"))
								{
									codec = "pcm_s32be";
								}
								else
									codec = "pcm_s32le";
							}
							else if (audioCodec.equals("PCM 32Float"))
								codec = "pcm_f32be";
							else if (audioCodec.equals("FLAC"))
								codec = "flac";	
							else if (audioCodec.equals("ALAC 16Bits"))
								codec = "alac";
							else if (audioCodec.equals("ALAC 24Bits"))
								codec = "alac";
							else if (audioCodec.equals(language.getProperty("codecCopy")))
							{
								codec = "copy";
							}
							else
							{
								if (System.getProperty("os.name").contains("Mac"))
								{
									codec = "aac_at";
								}
								else
									codec = "aac";
							}
							
							codecMapping += " -c:a:" + i + " " + codec;				
							
							break;
						}
					}
					
					//Bitrate
					for (Component a : grpSetAudio.getComponents())
					{
						if (a instanceof JComboBox && ((JComboBox) a).getName().matches("comboAudioBitrate[0-9]+") && ((JComboBox) a).getName().endsWith(String.valueOf(i+1)))
						{
							if (audioCodec.equals("FLAC"))
							{					
								bitrateMapping += " -compression_level:a:" + i + " " + ((JComboBox) a).getSelectedItem().toString();	
							}
							else if (audioCodec.equals("ALAC 16Bits"))
							{
								bitrateMapping += " -sample_fmt:a:" + i + " s16p";
							}
							else if (audioCodec.equals("ALAC 24Bits"))
							{
								bitrateMapping += " -sample_fmt:a:" + i + " s32p";
							}
							else if (audioCodec.contains("PCM") == false)
							{
								bitrateMapping += " -b:a:" + i + " " + ((JComboBox) a).getSelectedItem() + "k";				
							}

							break;
						}
					}
					
					//Rate
					for (Component a : grpSetAudio.getComponents())
					{
						if (a instanceof JComboBox && ((JComboBox) a).getName().matches("comboAudioRate[0-9]+") && ((JComboBox) a).getName().endsWith(String.valueOf(i+1)))
						{
							rateMapping += " -ar:a:" + i + " " + ((JComboBox) a).getSelectedItem();				
							break;
						}
					}
					
					//Language	
					for (Component a : grpSetAudio.getComponents())
					{
						if (a instanceof JComboBox && ((JComboBox) a).getName().matches("comboLanguage[0-9]+") && ((JComboBox) a).getName().endsWith(String.valueOf(i+1)))
						{
							if (((JComboBox) a).getSelectedItem().toString().equals("default") == false)
							{
								languageMapping += " -metadata:s:a:" + i + " language=" + Utils.ISO_639_2_LANGUAGES[((JComboBox) a).getSelectedIndex() - 1][1];
							}
							break;
						}
					}
				}	
				
			i++;
			}
		}		
		
		if (isBroadcastCodec) //Managed from FunctionUtils
		{
	    	audioFiltering = "";
		}
	    else if (audioFiltering != "")
		{
			audioFiltering = " -filter:a " + '"' + audioFiltering + '"';
		}
			
		return audioFiltering + audioMapping + codecMapping + bitrateMapping + rateMapping + languageMapping;
	}
}
