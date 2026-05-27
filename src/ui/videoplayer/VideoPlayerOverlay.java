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

package shutterencoder.ui.videoplayer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import shutterencoder.functions.settings.FunctionUtils;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.RenderQueue;

public class VideoPlayerOverlay extends Shutter {

	public static void checkSelection() {
		
		double ratioW = (double) FFPROBE.imageWidth / VideoPlayerUI.player.getWidth();
		double ratioH = (double) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight();
				
		int outW = (int) Math.round(Shutter.selection.getWidth() * ratioW);
		int outH = (int) Math.round(Shutter.selection.getHeight() * ratioH);
					
		int Px = (int) Math.round(Shutter.selection.getLocation().x * ratioW);
		int Py = (int) Math.round(Shutter.selection.getLocation().y * ratioH);
					
		if (Shutter.textCropWidth.getText().matches("[0-9]+") && Shutter.textCropHeight.getText().matches("[0-9]+"))
		{
			if (Px + Integer.valueOf(Shutter.textCropWidth.getText()) > FFPROBE.imageWidth)
			{
				Px = Px + (FFPROBE.imageWidth - (Px + Integer.valueOf(Shutter.textCropWidth.getText())));
			}
			
			if (Py + Integer.valueOf(Shutter.textCropHeight.getText()) > FFPROBE.imageHeight)
			{
				Py = Py + (FFPROBE.imageHeight - (Py + Integer.valueOf(Shutter.textCropHeight.getText())));
			}
						
			if (Integer.valueOf(Shutter.textCropWidth.getText()) != FFPROBE.imageWidth)
			{
				Shutter.textCropPosX.setText(String.valueOf(Px));
			}
			if (Integer.valueOf(Shutter.textCropHeight.getText()) != FFPROBE.imageHeight)
			{
				Shutter.textCropPosY.setText(String.valueOf(Py));
			}
		}
		else //First launch
		{
			Shutter.textCropPosX.setText(String.valueOf(Px));
			Shutter.textCropPosY.setText(String.valueOf(Py));
		}
				
		if (Shutter.frame.getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
		{
			Shutter.textCropWidth.setText(String.valueOf(outW));
			Shutter.textCropHeight.setText(String.valueOf(outH));
		}
	}
	
	public static void refreshTimecodeAndText() {
						
		//Colors	
		if (Shutter.foregroundColor != null)
		{
			 String c = Integer.toHexString(Shutter.foregroundColor.getRGB()).substring(2);
			 Shutter.foregroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}
		else
			Shutter.foregroundColor = new Color(255,255,255);
					
		if (Shutter.backgroundColor != null)
		{
			 String c = Integer.toHexString(Shutter.backgroundColor.getRGB()).substring(2);
			 Shutter.backgroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}	
		else
			Shutter.backgroundColor = new Color(0,0,0);

		if (Shutter.lblTcBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
		{	
			Shutter.backgroundTcAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Shutter.textTcOpacity.getText()) * 255) / 100);
			Shutter.foregroundTcAlpha = "ff";
			Shutter.backgroundNameAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Shutter.textNameOpacity.getText()) * 255) / 100);
		 	Shutter.foregroundNameAlpha = "ff";
		}
		else
		{
			Shutter.backgroundTcAlpha = "0";
			Shutter.foregroundTcAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Shutter.textTcOpacity.getText()) * 255) / 100);
		 	Shutter.backgroundNameAlpha = "0";
			Shutter.foregroundNameAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Shutter.textNameOpacity.getText()) * 255) / 100);
		}	
		
		if (Shutter.backgroundTcAlpha.length() < 2)
		{
			Shutter.backgroundTcAlpha = "0" + Shutter.backgroundTcAlpha;
		}
		if (Shutter.foregroundTcAlpha.length() < 2)	
		{
			Shutter.foregroundTcAlpha = "0" + Shutter.foregroundTcAlpha;
		}
		if (Shutter.backgroundNameAlpha.length() < 2)
		{
			Shutter.backgroundNameAlpha = "0" + Shutter.backgroundNameAlpha;
		}		
		if (Shutter.foregroundNameAlpha.length() < 2)
		{
			Shutter.foregroundNameAlpha = "0" + Shutter.foregroundNameAlpha;	
		}

		if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected())
		{				
			Shutter.textTcPosX.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().x * Shutter.playerRatio)));
			Shutter.textTcPosY.setText(String.valueOf((int) Math.round(Shutter.timecode.getLocation().y * Shutter.playerRatio)));  
		}
		else
		{
			Shutter.textTcPosX.setText("0");
			Shutter.textTcPosY.setText("0"); 
		}
		
		if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected())
		{						
			Shutter.textNamePosX.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().x * Shutter.playerRatio)));
			Shutter.textNamePosY.setText(String.valueOf((int) Math.round(Shutter.fileName.getLocation().y * Shutter.playerRatio)));  
		}
		else
		{	Shutter.textNamePosX.setText("0");
			Shutter.textNamePosY.setText("0"); 

		}			

	}

	public static void refreshSubtitles() {
		
		//Initialisation
		if (Shutter.alphaHeight == 0)
		{
			Shutter.alphaHeight = FFPROBE.imageHeight;
		}
		
		int v = Integer.parseInt(Shutter.textSubtitlesPosition.getText());
		int sz = Integer.parseInt(Shutter.textSubsSize.getText());
		int newValue = (int) Math.round((double)sz*((double)Shutter.alphaHeight/(FFPROBE.imageHeight+v)));
		
		if (newValue > 0)
			Shutter.textSubsSize.setText(String.valueOf(newValue));
		
		Shutter.alphaHeight = (int) (FFPROBE.imageHeight + v);
		
		if (Integer.parseInt(Shutter.textSubsWidth.getText()) >= FFPROBE.imageWidth)
		{
			Shutter.subsCanvas.setBounds(0, 0, VideoPlayerUI.player.getWidth(), (int) (VideoPlayerUI.player.getHeight() + (double) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (double) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));
		}
		else
		{
			Shutter.subsCanvas.setSize((int) ((double) Integer.parseInt(Shutter.textSubsWidth.getText()) / ( (double) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())),
		    		(int) (VideoPlayerUI.player.getHeight() + (double) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / ( (double) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));	
			
			Shutter.subsCanvas.setLocation((VideoPlayerUI.player.getWidth() - Shutter.subsCanvas.getWidth()) / 2, 0);
		}	
		
		VideoPlayerCore.loadImage(false);
	}
	
	public static void writeSub(String srt, Charset encoding) 
	{
		try {
			
			VideoPlayerOverlay.writeCurrentSubs(VideoPlayerCore.playerCurrentFrame, true);

		} catch (Exception e) {
			
			if (encoding == StandardCharsets.UTF_8)
			{						
				writeSub(srt, StandardCharsets.ISO_8859_1);
			}
			else					
			{		
				Shutter.caseAddSubtitles.setSelected(false);
				VideoPlayerUI.player.remove(Shutter.subsCanvas);
			}
		}
	}
	
	public static void writeCurrentSubs(double inputTime, boolean firstSub) {	
		
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesFilePath != null && Shutter.subtitlesFilePath.exists() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{
			try {
	
				BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(Shutter.subtitlesFilePath.toString()),  StandardCharsets.UTF_8);
	            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Shutter.subtitlesFile.toString()),  StandardCharsets.UTF_8);
	            
	            String line;
	            int subNumber = 0;
	            boolean startWriting = false;
	            
	            while ((line = bufferedReader.readLine()) != null)
	            {	            	
	            	//Removes UTF-8 with BOM
	            	line = line.replace("\uFEFF", "");
	            	
	        		if (line.contains("-->") )
	        		{ 	     	        			
	            		String split[] = line.split("-->");				
	            		String inTimecode[] = split[0].replace(",", ":").replace(" ","").split(":");
	            		String outTimecode[] = split[1].replace(",", ":").replace(" ","").split(":");
		            		
	    				int inH = (Integer.parseInt(inTimecode[0])) * 3600;
	    				int inM = Integer.parseInt(inTimecode[1]) * 60;
	    				int inS = Integer.parseInt(inTimecode[2]);
	    				int inF = Integer.parseInt(inTimecode[3]);
	    				 					
	    				double subsInTime = (inH + inM + inS) * FFPROBE.accurateFPS + inF / VideoPlayerUI.inputFramerateMS;

	    				//Reset player position to the first sub
	    				if (subNumber == 0 && firstSub)
	            		{    		
	    					while (VideoPlayerCore.setTime.isAlive())
							{
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							}
	    					VideoPlayerCore.playerSetTime(subsInTime);
	    					break;
	            		}
	    				else
	    				{
		    				int outH = (Integer.parseInt(outTimecode[0])) * 3600;
		    				int outM = Integer.parseInt(outTimecode[1]) * 60;
		    				int outS = Integer.parseInt(outTimecode[2]);
		    				int outF = Integer.parseInt(outTimecode[3]);
		    				double subsOuTime = (outH + outM + outS) * FFPROBE.accurateFPS + outF / VideoPlayerUI.inputFramerateMS;
		
		    				long inOffset = (long) (subsInTime - inputTime);
							long outOffset = (long) (subsOuTime - inputTime);							    					
							
							if (outOffset > 0)
							{
			    				if (inOffset < 0)
			    				{
			    					inOffset = 0;
			    				}
			    				
								startWriting = true;							
								subNumber ++;
		            			
			             		bufferedWriter.write(String.valueOf(subNumber));
			             		bufferedWriter.newLine();
								
								String iH = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS / 3600));
								String iM = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS / 60) % 60);
								String iS = Shutter.formatter.format(Math.floor(inOffset / FFPROBE.accurateFPS) % 60);    		
								String iF = Shutter.formatterToMs.format(Math.floor(inOffset % FFPROBE.accurateFPS * VideoPlayerUI.inputFramerateMS));
								
								String oH = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS / 3600));
								String oM = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS / 60) % 60);
								String oS = Shutter.formatter.format(Math.floor(outOffset / FFPROBE.accurateFPS) % 60);    		
								String oF = Shutter.formatterToMs.format(Math.floor(outOffset % FFPROBE.accurateFPS * VideoPlayerUI.inputFramerateMS));
												
								bufferedWriter.write(iH + ":" + iM + ":" + iS + "," + iF + " --> " + oH + ":" + oM + ":" + oS + "," + oF);
			            		bufferedWriter.newLine();
							}
	    				}
	        		}
	        		else if (firstSub == false && startWriting && line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
	        		{           			
	        			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
	    					bufferedWriter.write("\\h" + line + "\\h");
	        			else
	        				bufferedWriter.write(line);
	    			
	        			bufferedWriter.newLine();
	        		}
	        		else if (firstSub == false && startWriting && line.isEmpty())
	        		{
	        			bufferedWriter.newLine();
	        		}
	
	            }   
	
	            bufferedReader.close();  
	            bufferedWriter.close();
	            	            
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public static boolean loadWatermark(int size) {
				
		if (FFPROBE.imageWidth != FFPROBE.previousImageWidth || FFPROBE.imageHeight != FFPROBE.previousImageHeight || Shutter.logoPNG == null)	
		{			
			try {
			
				if (Shutter.logoPNG == null)
				{				
					int previousImageWidth = FFPROBE.previousImageWidth;
					
					//IMPORTANT
					if (FFPROBE.isRunning)
					{
						do {								
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {}	
						} while (FFPROBE.isRunning);
					}
					
					//Keep media size
					FFPROBE.previousImageWidth = previousImageWidth;
					
					if (Shutter.overlayDeviceIsRunning)
					{
						RecordInputDevice.setOverlayDevice();
					}
					else 
					{
						FFPROBE.Data(Shutter.logoFile);					
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (FFPROBE.isRunning);
					}
					
					Shutter.logoWidth = FFPROBE.imageWidth;
					Shutter.logoHeight = FFPROBE.imageHeight;
					
					//IMPORTANT keeps original source file data			
					FunctionUtils.analyze(new File(VideoPlayerCore.videoPath), false, true);
				}
										
				int logoFinalSizeWidth = (int) Math.floor((double) Shutter.logoWidth / Shutter.playerRatio);		
				int logoFinalSizeHeight = (int) Math.floor((double) Shutter.logoHeight / Shutter.playerRatio);
						
				//Adapt to size
				logoFinalSizeWidth = (int) Math.floor((double) logoFinalSizeWidth * ((double) size / 100));
				logoFinalSizeHeight = (int) Math.floor((double) logoFinalSizeHeight * ((double) size / 100));
				
				if (logoFinalSizeWidth < 1)
					logoFinalSizeWidth = 1;
				
				if (logoFinalSizeHeight < 1)
					logoFinalSizeHeight = 1;
					
				//Preserve location
				int newPosX = (int) Math.floor((Shutter.logo.getWidth() - logoFinalSizeWidth) / 2);
				int newPosY = (int) Math.floor((Shutter.logo.getHeight() - logoFinalSizeHeight) / 2);
				
				if (Shutter.logoPNG == null)
				{
					if (Shutter.overlayDeviceIsRunning)
					{
						FFMPEG.run(" -v quiet -hide_banner " + RecordInputDevice.setOverlayDevice() + " -frames:v 1 -an -sn -c:v rawvideo -pix_fmt rgba -sws_flags fast_bilinear -f rawvideo -");
					}
					else if (Shutter.logoPNG == null)
					{
						FFMPEG.run(" -v quiet -hide_banner -i " + '"' + Shutter.logoFile + '"' + " -frames:v 1 -an -sn -c:v rawvideo -pix_fmt rgba -sws_flags fast_bilinear -f rawvideo -");
					}
					
					do {
						Thread.sleep(10);
					} while (FFMPEG.process.isAlive() == false);
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					InputStream videoInput = FFMPEG.process.getInputStream(); 
					InputStream is = new BufferedInputStream(videoInput);		
					
					int frameSize = Shutter.logoWidth * Shutter.logoHeight * 4;
					byte[] frameData = new byte[frameSize];

			        int read = is.readNBytes(frameData, 0, frameData.length);
			        if (read == frameData.length)
			        {
			        	BufferedImage frame = new BufferedImage(Shutter.logoWidth, Shutter.logoHeight, BufferedImage.TYPE_4BYTE_ABGR);
			            frame.getRaster().setDataElements(0, 0, Shutter.logoWidth, Shutter.logoHeight, frameData);
			            
			            VideoPlayerCore.fullSizeWatermark = frame;
			        }       
				}
				
				Shutter.logoPNG = new ImageIcon(VideoPlayerCore.fullSizeWatermark).getImage().getScaledInstance(logoFinalSizeWidth, logoFinalSizeHeight, Image.SCALE_AREA_AVERAGING);
							
				if (Shutter.logo.getWidth() == 0)
				{
					Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - logoFinalSizeWidth / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - logoFinalSizeHeight / 2));	
				}
				else
					Shutter.logo.setLocation(Shutter.logo.getLocation().x + newPosX, Shutter.logo.getLocation().y + newPosY);
	
				Shutter.logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);        
	            			
				Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.playerRatio) ) ) );
				Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.playerRatio) ) ) );  
				
	            //Saving location
				Shutter.logoLocX = Shutter.logo.getLocation().x;
				Shutter.logoLocY = Shutter.logo.getLocation().y;	
	            Shutter.logo.repaint();        
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
			} 
			finally {
				
				if (Shutter.watermarkPreset != null)
				{
					watermarkPositions(Shutter.watermarkPreset);
				}
				
				Shutter.btnStart.setEnabled(true);
				
	        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
	        }
		}
		return true;
	}
	
	public static void watermarkPositions(String preset) {

		Shutter.watermarkPreset = preset;
		
		int offsetX = (int) ((double) VideoPlayerUI.player.getWidth() * 0.036);
		int offsetY = (int) ((double) VideoPlayerUI.player.getHeight() * 0.036);
		
		if (preset.equals("watermarkTopLeft"))
		{
			Shutter.logo.setLocation(offsetX, offsetY);	
		}
		else if (preset.equals("watermarkLeft"))
		{
			Shutter.logo.setLocation(offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));	
		}
		else if (preset.equals("watermarkBottomLeft"))
		{
			Shutter.logo.setLocation(offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() - Shutter.logo.getHeight()) - offsetY);	
		}
		else if (preset.equals("watermarkTop"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), offsetY);	
		}
		else if (preset.equals("watermarkCenter"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottom"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Shutter.logo.getWidth() / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() - Shutter.logo.getHeight()) - offsetY);
		}
		else if (preset.equals("watermarkTopRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Shutter.logo.getWidth()) - offsetX, offsetY);
		}
		else if (preset.equals("watermarkRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Shutter.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Shutter.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottomRight"))
		{
			Shutter.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Shutter.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() - Shutter.logo.getHeight()) - offsetY);
		}
				
		Shutter.logoLocX = Shutter.logo.getLocation().x;
		Shutter.logoLocY = Shutter.logo.getLocation().y;
		Shutter.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().x * Shutter.playerRatio) ) ) );
		Shutter.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Shutter.logo.getLocation().y * Shutter.playerRatio) ) ) );
	}
	
}
