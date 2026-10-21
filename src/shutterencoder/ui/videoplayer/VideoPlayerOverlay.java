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

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.RenderQueue;

public class VideoPlayerOverlay extends Main {

	public static void checkSelection() {
		
		double ratioW = (double) FFprobe.imageWidth / VideoPlayerUI.player.getWidth();
		double ratioH = (double) FFprobe.imageHeight / VideoPlayerUI.player.getHeight();
				
		int outW = (int) Math.round(Main.selection.getWidth() * ratioW);
		int outH = (int) Math.round(Main.selection.getHeight() * ratioH);
					
		int Px = (int) Math.round(Main.selection.getLocation().x * ratioW);
		int Py = (int) Math.round(Main.selection.getLocation().y * ratioH);
					
		if (Main.textCropWidth.getText().matches("[0-9]+") && Main.textCropHeight.getText().matches("[0-9]+"))
		{
			if (Px + Integer.valueOf(Main.textCropWidth.getText()) > FFprobe.imageWidth)
			{
				Px = Px + (FFprobe.imageWidth - (Px + Integer.valueOf(Main.textCropWidth.getText())));
			}
			
			if (Py + Integer.valueOf(Main.textCropHeight.getText()) > FFprobe.imageHeight)
			{
				Py = Py + (FFprobe.imageHeight - (Py + Integer.valueOf(Main.textCropHeight.getText())));
			}
						
			if (Integer.valueOf(Main.textCropWidth.getText()) != FFprobe.imageWidth)
			{
				Main.textCropPosX.setText(String.valueOf(Px));
			}
			if (Integer.valueOf(Main.textCropHeight.getText()) != FFprobe.imageHeight)
			{
				Main.textCropPosY.setText(String.valueOf(Py));
			}
		}
		else //First launch
		{
			Main.textCropPosX.setText(String.valueOf(Px));
			Main.textCropPosY.setText(String.valueOf(Py));
		}
				
		if (Main.frame.getCursor() != Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
		{
			Main.textCropWidth.setText(String.valueOf(outW));
			Main.textCropHeight.setText(String.valueOf(outH));
		}
	}
	
	public static void refreshTimecodeAndText() {
						
		//Colors	
		if (Main.foregroundColor != null)
		{
			 String c = Integer.toHexString(Main.foregroundColor.getRGB()).substring(2);
			 Main.foregroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}
		else
			Main.foregroundColor = new Color(255,255,255);
					
		if (Main.backgroundColor != null)
		{
			 String c = Integer.toHexString(Main.backgroundColor.getRGB()).substring(2);
			 Main.backgroundHex = c.substring(0, 2) + c.substring(2, 4) + c.substring(4, 6);
		}	
		else
			Main.backgroundColor = new Color(0,0,0);

		if (Main.lblTcBackground.getText().equals(Main.language.getProperty("lblBackgroundOn")))
		{	
			Main.backgroundTcAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Main.textTcOpacity.getText()) * 255) / 100);
			Main.foregroundTcAlpha = "ff";
			Main.backgroundNameAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Main.textNameOpacity.getText()) * 255) / 100);
		 	Main.foregroundNameAlpha = "ff";
		}
		else
		{
			Main.backgroundTcAlpha = "0";
			Main.foregroundTcAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Main.textTcOpacity.getText()) * 255) / 100);
		 	Main.backgroundNameAlpha = "0";
			Main.foregroundNameAlpha = Integer.toHexString((int) (double) ((int) Integer.parseInt(Main.textNameOpacity.getText()) * 255) / 100);
		}	
		
		if (Main.backgroundTcAlpha.length() < 2)
		{
			Main.backgroundTcAlpha = "0" + Main.backgroundTcAlpha;
		}
		if (Main.foregroundTcAlpha.length() < 2)	
		{
			Main.foregroundTcAlpha = "0" + Main.foregroundTcAlpha;
		}
		if (Main.backgroundNameAlpha.length() < 2)
		{
			Main.backgroundNameAlpha = "0" + Main.backgroundNameAlpha;
		}		
		if (Main.foregroundNameAlpha.length() < 2)
		{
			Main.foregroundNameAlpha = "0" + Main.foregroundNameAlpha;	
		}

		if (Main.caseAddTimecode.isSelected() || Main.caseShowTimecode.isSelected())
		{				
			Main.textTcPosX.setText(String.valueOf((int) Math.round(Main.timecode.getLocation().x * Main.playerRatio)));
			Main.textTcPosY.setText(String.valueOf((int) Math.round(Main.timecode.getLocation().y * Main.playerRatio)));  
		}
		else
		{
			Main.textTcPosX.setText("0");
			Main.textTcPosY.setText("0"); 
		}
		
		if (Main.caseShowFileName.isSelected() || Main.caseAddText.isSelected())
		{						
			Main.textNamePosX.setText(String.valueOf((int) Math.round(Main.fileName.getLocation().x * Main.playerRatio)));
			Main.textNamePosY.setText(String.valueOf((int) Math.round(Main.fileName.getLocation().y * Main.playerRatio)));  
		}
		else
		{	Main.textNamePosX.setText("0");
			Main.textNamePosY.setText("0"); 

		}			

	}

	public static void refreshSubtitles() {
		
		//Initialisation
		if (Main.alphaHeight == 0)
		{
			Main.alphaHeight = FFprobe.imageHeight;
		}
		
		int v = Integer.parseInt(Main.textSubtitlesPosition.getText());
		int sz = Integer.parseInt(Main.textSubsSize.getText());
		int newValue = (int) Math.round((double)sz*((double)Main.alphaHeight/(FFprobe.imageHeight+v)));
		
		if (newValue > 0)
			Main.textSubsSize.setText(String.valueOf(newValue));
		
		Main.alphaHeight = (int) (FFprobe.imageHeight + v);
		
		if (Integer.parseInt(Main.textSubsWidth.getText()) >= FFprobe.imageWidth)
		{
			Main.subsCanvas.setBounds(0, 0, VideoPlayerUI.player.getWidth(), (int) (VideoPlayerUI.player.getHeight() + (double) Integer.parseInt(Main.textSubtitlesPosition.getText()) / ( (double) FFprobe.imageHeight / VideoPlayerUI.player.getHeight())));
		}
		else
		{
			Main.subsCanvas.setSize((int) ((double) Integer.parseInt(Main.textSubsWidth.getText()) / ( (double) FFprobe.imageHeight / VideoPlayerUI.player.getHeight())),
		    		(int) (VideoPlayerUI.player.getHeight() + (double) Integer.parseInt(Main.textSubtitlesPosition.getText()) / ( (double) FFprobe.imageHeight / VideoPlayerUI.player.getHeight())));	
			
			Main.subsCanvas.setLocation((VideoPlayerUI.player.getWidth() - Main.subsCanvas.getWidth()) / 2, 0);
		}	
		
		VideoPlayerUtils.loadImage();
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
				Main.caseAddSubtitles.setSelected(false);
				VideoPlayerUI.player.remove(Main.subsCanvas);
			}
		}
	}
	
	public static void writeCurrentSubs(double inputTime, boolean firstSub) {	
		
		if (Main.caseAddSubtitles.isSelected() && Main.subtitlesFilePath != null && Main.subtitlesFilePath.exists() && Main.subtitlesFile.toString().substring(Main.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{
			try {
	
				BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(Main.subtitlesFilePath.toString()),  StandardCharsets.UTF_8);
	            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Main.subtitlesFile.toString()),  StandardCharsets.UTF_8);
	            
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
	    				 					
	    				double subsInTime = (inH + inM + inS) * FFprobe.accurateFPS + inF / VideoPlayerUI.inputFramerateMS;

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
		    				double subsOuTime = (outH + outM + outS) * FFprobe.accurateFPS + outF / VideoPlayerUI.inputFramerateMS;
		
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
								
								String iH = Main.formatter.format(Math.floor(inOffset / FFprobe.accurateFPS / 3600));
								String iM = Main.formatter.format(Math.floor(inOffset / FFprobe.accurateFPS / 60) % 60);
								String iS = Main.formatter.format(Math.floor(inOffset / FFprobe.accurateFPS) % 60);    		
								String iF = Main.formatterToMs.format(Math.floor(inOffset % FFprobe.accurateFPS * VideoPlayerUI.inputFramerateMS));
								
								String oH = Main.formatter.format(Math.floor(outOffset / FFprobe.accurateFPS / 3600));
								String oM = Main.formatter.format(Math.floor(outOffset / FFprobe.accurateFPS / 60) % 60);
								String oS = Main.formatter.format(Math.floor(outOffset / FFprobe.accurateFPS) % 60);    		
								String oF = Main.formatterToMs.format(Math.floor(outOffset % FFprobe.accurateFPS * VideoPlayerUI.inputFramerateMS));
												
								bufferedWriter.write(iH + ":" + iM + ":" + iS + "," + iF + " --> " + oH + ":" + oM + ":" + oS + "," + oF);
			            		bufferedWriter.newLine();
							}
	    				}
	        		}
	        		else if (firstSub == false && startWriting && line.contains("-->") == false && line.matches("[0-9]+") == false && line.isEmpty() == false)
	        		{           			
	        			if (Main.lblSubsBackground.getText().equals(Main.language.getProperty("lblBackgroundOn")))
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
				
		if (FFprobe.imageWidth != FFprobe.previousImageWidth || FFprobe.imageHeight != FFprobe.previousImageHeight || Main.logoPNG == null)	
		{			
			try {
			
				if (Main.logoPNG == null)
				{				
					int previousImageWidth = FFprobe.previousImageWidth;
					
					//IMPORTANT
					if (FFprobe.isRunning)
					{
						try {
							FFprobe.processData.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}
					}
					
					//Keep media size
					FFprobe.previousImageWidth = previousImageWidth;
					
					if (Main.overlayDeviceIsRunning)
					{
						RecordInputDevice.setOverlayDevice();
					}
					else 
					{
						FFprobe.Data(Main.logoFile);					
						try {
							FFprobe.processData.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}
					}
					
					Main.logoWidth = FFprobe.imageWidth;
					Main.logoHeight = FFprobe.imageHeight;
					
					//IMPORTANT keeps original source file data			
					FunctionUtils.analyze(new File(VideoPlayerCore.videoPath), false, true);
				}
										
				int logoFinalSizeWidth = (int) Math.floor((double) Main.logoWidth / Main.playerRatio);		
				int logoFinalSizeHeight = (int) Math.floor((double) Main.logoHeight / Main.playerRatio);
						
				//Adapt to size
				logoFinalSizeWidth = (int) Math.floor((double) logoFinalSizeWidth * ((double) size / 100));
				logoFinalSizeHeight = (int) Math.floor((double) logoFinalSizeHeight * ((double) size / 100));
				
				if (logoFinalSizeWidth < 1)
					logoFinalSizeWidth = 1;
				
				if (logoFinalSizeHeight < 1)
					logoFinalSizeHeight = 1;
					
				//Preserve location
				int newPosX = (int) Math.floor((Main.logo.getWidth() - logoFinalSizeWidth) / 2);
				int newPosY = (int) Math.floor((Main.logo.getHeight() - logoFinalSizeHeight) / 2);
				
				if (Main.logoPNG == null)
				{
					if (Main.overlayDeviceIsRunning)
					{
						FFmpeg.run(" -v quiet -hide_banner " + RecordInputDevice.setOverlayDevice() + " -frames:v 1 -an -sn -c:v rawvideo -pix_fmt rgba -scaler bilinear -f rawvideo -");
					}
					else if (Main.logoPNG == null)
					{
						FFmpeg.run(" -v quiet -hide_banner -i " + '"' + Main.logoFile + '"' + " -frames:v 1 -an -sn -c:v rawvideo -pix_fmt rgba -scaler bilinear -f rawvideo -");
					}
					
					do {
						Thread.sleep(10);
					} while (FFmpeg.process.isAlive() == false);
					
					Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					InputStream videoInput = FFmpeg.process.getInputStream(); 
					InputStream is = new BufferedInputStream(videoInput);		
					
					int frameSize = Main.logoWidth * Main.logoHeight * 4;
					byte[] frameData = new byte[frameSize];

			        int read = is.readNBytes(frameData, 0, frameData.length);
			        if (read == frameData.length)
			        {
			        	BufferedImage frame = new BufferedImage(Main.logoWidth, Main.logoHeight, BufferedImage.TYPE_4BYTE_ABGR);
			            frame.getRaster().setDataElements(0, 0, Main.logoWidth, Main.logoHeight, frameData);
			            
			            VideoPlayerCore.fullSizeWatermark = frame;
			        }       
				}
				
				Main.logoPNG = new ImageIcon(VideoPlayerCore.fullSizeWatermark).getImage().getScaledInstance(logoFinalSizeWidth, logoFinalSizeHeight, Image.SCALE_AREA_AVERAGING);
							
				if (Main.logo.getWidth() == 0)
				{
					Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - logoFinalSizeWidth / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - logoFinalSizeHeight / 2));	
				}
				else
					Main.logo.setLocation(Main.logo.getLocation().x + newPosX, Main.logo.getLocation().y + newPosY);
	
				Main.logo.setSize(logoFinalSizeWidth, logoFinalSizeHeight);        
	            			
				Main.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Main.logo.getLocation().x * Main.playerRatio) ) ) );
				Main.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Main.logo.getLocation().y * Main.playerRatio) ) ) );  
				
	            //Saving location
				Main.logoLocX = Main.logo.getLocation().x;
				Main.logoLocY = Main.logo.getLocation().y;	
	            Main.logo.repaint();        
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.frame, Main.language.getProperty("cantLoadFile"), Main.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
			} 
			finally {
				
				if (Main.watermarkPreset != null)
				{
					watermarkPositions(Main.watermarkPreset);
				}
				
				Main.btnStart.setEnabled(true);
				
	        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Main.btnStart.setText(Main.language.getProperty("btnAddToRender"));
				else
					Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
	        }
		}
		return true;
	}
	
	public static void watermarkPositions(String preset) {

		Main.watermarkPreset = preset;
		
		int offsetX = (int) ((double) VideoPlayerUI.player.getWidth() * 0.036);
		int offsetY = (int) ((double) VideoPlayerUI.player.getHeight() * 0.036);
		
		if (preset.equals("watermarkTopLeft"))
		{
			Main.logo.setLocation(offsetX, offsetY);	
		}
		else if (preset.equals("watermarkLeft"))
		{
			Main.logo.setLocation(offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Main.logo.getHeight() / 2));	
		}
		else if (preset.equals("watermarkBottomLeft"))
		{
			Main.logo.setLocation(offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() - Main.logo.getHeight()) - offsetY);	
		}
		else if (preset.equals("watermarkTop"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Main.logo.getWidth() / 2), offsetY);	
		}
		else if (preset.equals("watermarkCenter"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Main.logo.getWidth() / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Main.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottom"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - Main.logo.getWidth() / 2), (int) Math.floor(VideoPlayerUI.player.getHeight() - Main.logo.getHeight()) - offsetY);
		}
		else if (preset.equals("watermarkTopRight"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Main.logo.getWidth()) - offsetX, offsetY);
		}
		else if (preset.equals("watermarkRight"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Main.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - Main.logo.getHeight() / 2));
		}
		else if (preset.equals("watermarkBottomRight"))
		{
			Main.logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() - Main.logo.getWidth()) - offsetX, (int) Math.floor(VideoPlayerUI.player.getHeight() - Main.logo.getHeight()) - offsetY);
		}
				
		Main.logoLocX = Main.logo.getLocation().x;
		Main.logoLocY = Main.logo.getLocation().y;
		Main.textWatermarkPosX.setText(String.valueOf(Integer.valueOf((int) Math.floor(Main.logo.getLocation().x * Main.playerRatio) ) ) );
		Main.textWatermarkPosY.setText(String.valueOf(Integer.valueOf((int) Math.floor(Main.logo.getLocation().y * Main.playerRatio) ) ) );
	}
	
}
