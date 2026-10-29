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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.Dcraw;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.Ncnn;
import shutterencoder.library.Xpdfreader;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.subtitling.SubtitlesTimeline;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts.CutSegment;

public class VideoPlayerUtils extends VideoPlayerCore {

	//FileList
	public static StringBuilder fileList = new StringBuilder();
	
    //Waveform
    public static Thread addWaveform = new Thread();
  	public static boolean addWaveformIsRunning = false;
  	public static BufferedImage waveform = null;  	
  	
  	//Thumbnails
  	private static Thread thumbnailThread;
  	private static ExecutorService thumbnailExecutor;
  	private static final Set<Process> activeFFmpegProcesses = Collections.newSetFromMap(new ConcurrentHashMap<>());
  	public static BufferedImage[] allThumbnails;
  		
	//Preview
	public static byte[] preview = null;
	public static Thread loadImageThread = new Thread();
	private static volatile boolean loadImagePending = false;
	public static Process processLoadImage;
	
	public static void setMedia() {
		
		loadMedia = new Thread(new Runnable()
		{
			@Override
			public void run()
			{    			
				if (FFmpeg.isRunning == false
		    	|| (Main.btnStart.getText().equals(Main.language.getProperty("btnPauseFunction")) == false
		    	&& Main.btnStart.getText().equals(Main.language.getProperty("resume")) == false
		    	&& Main.btnStart.getText().equals(Main.language.getProperty("btnStopRecording")) == false))
		    	{
		   	    	//Updating video file
					if (Main.list.getSize() > 0)
					{				
						if (Main.fileList.getSelectedIndices().length == 0)
			      		{
							Main.fileList.setSelectedIndex(0);
			      		}
														
						//set timecode & Shutter.fileName locations
						VideoPlayerOverlay.refreshTimecodeAndText();				
						
						if (Main.scanIsRunning)
						{
							File dir = new File(Main.list.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									videoPath = f.toString();
									break;
								}
							}
						} 
						else if (Main.inputDeviceIsRunning)
						{
							videoPath = Main.list.firstElement();
							setInfo();
						}
						
						//Reset when changing file													
						if (Main.fileList.getSelectedValue().equals(videoPath) == false && (new File(Main.fileList.getSelectedValue()).isFile() || Main.scanIsRunning) || Main.inputDeviceIsRunning)
						{				
							//Stop player
							if (playerIsPlaying())
							{
								btnPlay.doClick();
							}
							
							//Stop generating image from loadImage()
							if (processLoadImage != null && processLoadImage.isAlive())
					        {
					            processLoadImage.destroyForcibly();
					            
					            try {
									loadImageThread.join();
								} catch (InterruptedException ex) {
									ex.printStackTrace();
									Thread.currentThread().interrupt();
								}
					        }
							
							//Clear the buffer
							if (bufferedFrames.size() > 0)
							{											
								bufferedFrames.clear();
								waveformContainer.repaint();
							}
									
							//Clear the segment list
							activeSegmentIndex = -1;
							if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
							{					
								VideoPlayerMultiCuts.clearCutHistory();
								VideoPlayerMultiCuts.cutSegments.clear();
							}
							
							//IMPORTANT
							if (FFprobe.isRunning)
							{								
								try {
									while (FFprobe.processData == null) //Wait for the process to initialize
										Thread.sleep(1);
									
									FFprobe.processData.join();
								} catch (InterruptedException er) {
								    Thread.currentThread().interrupt();
								}
							}
							
							if (Main.scanIsRunning == false)
								videoPath = Main.fileList.getSelectedValue();
							
							if (frameVideo != null)
								frameVideo = null;
							
							if (preview != null)
								preview = null;
							
							if (waveform != null)
								waveform = null;
							
							waveformZoom = 1;
							
							// Waveform							
							if (addWaveformIsRunning && LibraryUtils.waveformWriter != null)
							{
								stopWaveformCreation();
							}
							
							// Thumbnails
							if (thumbnails != null)
							{			
								allThumbnails = null;
								stopThumbnailsCreation();
							}
													
							String extension = videoPath.substring(videoPath.lastIndexOf("."));	
								
							boolean isRaw = false;
				    		
							//FFprobe with RAW files
							switch (extension.toLowerCase())
							{ 
								case ".3fr":
								case ".arw":
								case ".crw":
								case ".cr2":
								case ".cr3":
								case ".dng":
								case ".kdc":
								case ".mrw":
								case ".nef":
								case ".nrw":
								case ".orf":
								case ".ptx":
								case ".pef":
								case ".raf":
								case ".r3d":
								case ".rw2":
								case ".srw":
								case ".x3f":
									isRaw = true;
									FFprobe.totalLength = 0;
							}
				
							try {
								FunctionUtils.analyze(new File(videoPath), isRaw, true);
							} catch (InterruptedException e) {}
							
							//IMPORTANT							
							btnStop.doClick();							
							Main.fileList.repaint();							
							fileDuration = FFprobe.totalLength; //Avoid a bug when totalLength is loader somewhere else

							if (isRaw)
							{
								Main.btnStart.setEnabled(true);
							}
																					
							cursorCurrentFrame.setBounds(0, 0, 1, waveformContainer.getHeight() - 1);
							setPlayerButtons(true);	

							//Generate filmstrip							
							if (caseShowThumbnails.isSelected())
								generateThumbnails();
							
							//Add layers
							if (Main.caseAddWatermark.isSelected()) {
								player.add(Main.logo);
							}
	
							if (Main.caseAddSubtitles.isSelected() && Main.subtitlesBurn) {
								player.add(Main.subsCanvas);
							}
	
							if (Main.caseAddTimecode.isSelected() || Main.caseShowTimecode.isSelected()) {
								player.add(Main.timecode);
							}
	
							if (Main.caseShowFileName.isSelected() || Main.caseAddText.isSelected()) {
								player.add(Main.fileName);
							}
	
							if (Main.caseEnableCrop.isSelected()) {
								// Shutter.overImage need to be the last component added
								player.add(Main.selection);
								player.add(Main.overImage);
							}
														
							//Get GOP size
							seekOnKeyFrames = false;
							if (FFprobe.audioOnly == false)
							{							
								FFprobe.AnalyzeGOP(videoPath, false);
								do {
									//Slow down the loop
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (FFprobe.gopCount > 2)
									{										
										FFprobe.process.destroyForcibly();
										break;
									}
								} while (FFprobe.processGOP.isAlive());								
							}
							
							//Jump on key frames only
							if (FFprobe.audioOnly == false
							&& (Main.comboFonctions.getSelectedItem().toString().equals(Main.language.getProperty("functionCut"))
							|| Main.comboFonctions.getSelectedItem().toString().equals(Main.language.getProperty("functionRewrap"))
							|| Main.comboFonctions.getSelectedItem().toString().equals(Main.language.getProperty("functionConform"))))
							{
								if (FFprobe.gopCount > 2)
								{
									seekOnKeyFrames = true;
								}
							}
							
							//Autocrop
							if (Main.caseEnableCrop.isSelected() && Main.comboPreset.getSelectedIndex() == 1)
				    		{
				    			LibraryUtils.setCropDetect(new File(videoPath));	  
				    		}
							
							//Burn subtitles
							if (Main.caseAddSubtitles.isSelected())
							{	
								if (Main.subtitlesBurn)
								{
									Main.autoBurn = true;
								}
								else
									Main.autoEmbed = true;
								
								String ext = videoPath.substring(videoPath.lastIndexOf("."));
																
								if (new File(videoPath.replace(ext, ".srt")).exists()
								|| new File (videoPath.replace(ext, ".vtt")).exists()
								|| new File (videoPath.replace(ext, ".ass")).exists()
								|| new File (videoPath.replace(ext, ".ssa")).exists()
								|| new File (videoPath.replace(ext, ".scc")).exists()
								|| Main.comboSubsSource.getSelectedIndex() != 0)
								{
									FunctionUtils.addSubtitles(false);
									if (loadImageThread != null)
									{
										try {
											loadImageThread.join();
										} catch (InterruptedException e) {
										    Thread.currentThread().interrupt();
										}
									}
									FunctionUtils.addSubtitles(true);
								}
								
								Main.autoBurn = false;
								Main.autoEmbed = false;
								
								try {
									FFmpeg.runProcess.join();
								} catch (InterruptedException e) {
								    Thread.currentThread().interrupt();
								}
							}
														
							if ((System.getProperty("os.name").contains("Mac") && Main.arch.equals("x86_64")) || System.getProperty("os.name").contains("Linux"))
							{
								if (FFprobe.subtitleStreams != Main.comboSubsSource.getItemCount() - 1)
								{
									Main.comboSubsSource.removeAllItems();
									Main.comboSubsSource.addItem(Main.language.getProperty("file"));
									for (int i = 0 ; i < FFprobe.subtitleStreams ; i++)
									{
										Main.comboSubsSource.addItem(Main.language.getProperty("source") + " #" + (i + 1));
									}
								}
							}
							else
							{
								if (FFprobe.subtitleStreams != Main.comboSubsSource.getItemCount() - 2)
								{
									Main.comboSubsSource.removeAllItems();
									Main.comboSubsSource.addItem(Main.language.getProperty("file"));								
									for (int i = 0 ; i < FFprobe.subtitleStreams ; i++)
									{
										Main.comboSubsSource.addItem(Main.language.getProperty("source") + " #" + (i + 1));
									}
									Main.comboSubsSource.addItem(Main.language.getProperty("functionTranscribe"));
								}
							}
							
							//Image sequence
							if (Main.caseEnableSequence.isSelected())
							{	
								//Create the concat text file
								FunctionUtils.setConcat(new File("concat.txt"), Main.dirTemp);						
								inputFramerateMS = Float.parseFloat(Main.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
							}
							else					
								inputFramerateMS = (double) (1000 / FFprobe.accurateFPS);		
									
							bufferCurrentFrame = 0;
							playerCurrentFrame = 0;
			
							caseInternalTc.setEnabled(true);	
							Main.caseShowTimecode.setEnabled(true);
							
							Main.textSubsWidth.setText(String.valueOf(FFprobe.imageWidth));
							
							setInfo();
							
							btnPlay.setEnabled(true);
							btnPrevious.setEnabled(true);
							btnNext.setEnabled(true);
							btnStop.setEnabled(true);
							btnMarkIn.setEnabled(true);
							btnMarkOut.setEnabled(true);
							btnGoToIn.setEnabled(true);
							btnGoToOut.setEnabled(true);	
							
							if ((caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false) && UIController.noVideoPlayer == false)
							{
								caseInH.setEnabled(true);
								caseInM.setEnabled(true);
								caseInS.setEnabled(true);
								caseInF.setEnabled(true);
								caseOutH.setEnabled(true);
								caseOutM.setEnabled(true);
								caseOutS.setEnabled(true);
								caseOutF.setEnabled(true);
							}
							
							if (fileDuration > 40 && Main.caseEnableSequence.isSelected() == false && Main.frame.getSize().width > 654)
							{
								lblPosition.setVisible(true);
								lblDuration.setVisible(true);
							}
																					
							totalFrames = Math.round((double) fileDuration / 1000 * FFprobe.accurateFPS);
							
							//Reset boxes
							if (caseApplyCutToAll.isVisible() && caseApplyCutToAll.isSelected())
							{
								updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
								updateGrpOut(Timecode.getNTSCtimecode(totalFrames - InputAndOutput.savedOutPoint));
							}
							else
							{
								updateGrpIn(0);
								updateGrpOut(totalFrames);
								
								playerMarkIn = 0;
								playerMarkOut = waveformContainer.getWidth();
							}
	
							waveformContainer.repaint();
							
							//Setup fileList
							if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
							{
								getFileList(videoPath, fileDuration);
							}
								
							setFileList();	
							
							//Scaling text & logo
							double scale = 0.0f;
							if (FFprobe.previousImageWidth > 0)	
							{
								scale = ((double) FFprobe.imageWidth / FFprobe.previousImageWidth);
								
								if (scale != 0.0f)
								{
									//Display timecode
									if (Main.caseShowTimecode.isSelected() || Main.caseAddTimecode.isSelected())
									{
										Main.textTcSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textTcSize.getText()) * scale)));		
									}
									
									//Display text
									if (Main.caseShowFileName.isSelected() || Main.caseAddText.isSelected())
									{
										Main.textNameSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textNameSize.getText()) * scale)));
									}
									
									//Watermark
									if (Main.caseAddWatermark.isSelected() && (FFprobe.imageWidth != FFprobe.previousImageWidth || FFprobe.imageHeight != FFprobe.previousImageHeight))				
									{	
										Main.textWatermarkSize.setText(String.valueOf(Math.round(Integer.parseInt(Main.textWatermarkSize.getText()) * scale)));	
									}
									
									resizeAll();
								}
							}						
						}	
						
						Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					else
					{				
						btnStop.doClick();
						
						videoPath = null;
						showScale.setVisible(false);
						playerStop();
						playerSetTime(0);
			
						btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));	
						btnPlay.setName("play");
						
						btnPlay.setEnabled(false);
						btnPrevious.setEnabled(false);
						btnNext.setEnabled(false);
						btnStop.setEnabled(false);
						btnMarkIn.setEnabled(false);
						btnMarkOut.setEnabled(false);
						btnGoToIn.setEnabled(false);
						btnGoToOut.setEnabled(false);
						
						caseInH.setEnabled(false);
						caseInM.setEnabled(false);
						caseInS.setEnabled(false);
						caseInF.setEnabled(false);
						caseOutH.setEnabled(false);
						caseOutM.setEnabled(false);
						caseOutS.setEnabled(false);
						caseOutF.setEnabled(false);
						
						caseInternalTc.setEnabled(false);	
						caseInternalTc.setSelected(false);		
						
						lblPosition.setVisible(false);
						lblDuration.setVisible(false);	
						
						if (waveform != null)
						{
							waveform = null;
							waveformContainer.repaint();
						}
						
						// Thumbnails
						if (thumbnails != null)
						{			
							allThumbnails = null;
							stopThumbnailsCreation();
						}
					}
					
					if (Main.lblCurrentEncoding.getText().equals(Main.language.getProperty("processEnded")))
					{
						Main.progressBar.setValue(Main.progressBar.getMaximum());
					}
					
					if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")))
					{
						caseInH.setVisible(false);
						caseInM.setVisible(false);
						caseInS.setVisible(false);
						caseInF.setVisible(false);
						caseOutH.setVisible(false);
						caseOutM.setVisible(false);
						caseOutS.setVisible(false);
						caseOutF.setVisible(false);
					}
					else if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionReplaceAudio")))
					{
						if (Settings.btnDisableVideoPlayer.isSelected() == false)
						{
							caseInH.setVisible(true);
							caseInM.setVisible(true);
							caseInS.setVisible(true);
							caseInF.setVisible(true);
						}
						caseOutH.setVisible(false);
						caseOutM.setVisible(false);
						caseOutS.setVisible(false);
						caseOutF.setVisible(false);
					}
					else if (waveformScrollPane.isVisible())
					{
						caseInH.setVisible(true);
						caseInM.setVisible(true);
						caseInS.setVisible(true);
						caseInF.setVisible(true);
						caseOutH.setVisible(true);
						caseOutM.setVisible(true);
						caseOutS.setVisible(true);
						caseOutF.setVisible(true);
					}	
					
					if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")))
					{						
						File video = new File(videoPath);
						String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
						
						SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");		
						SubtitlesTimeline.timelineScrollBar.setMaximum((int) (((totalFrames - 1) * inputFramerateMS) * SubtitlesTimeline.zoom));
									
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			    		Main.frame.setLocation(Main.frame.getLocation().x , dim.height/3 - Main.frame.getHeight()/2);
			
			    		if (Main.caseAddSubtitles.isSelected())
			    		{
			    			player.remove(Main.subsCanvas);
							Main.caseAddSubtitles.setSelected(false);	    	
			    		}
							    		    	
			    		if (SubtitlesTimeline.frame == null) 
			    		{	    	
			    			new SubtitlesTimeline();		
			    		}
			    		else
			    		{
			    			SubtitlesTimeline.frame.setVisible(true);
			    			SubtitlesTimeline.frame.setLocation((Main.frame.getLocation().x + Main.frame.getWidth() / 2) - SubtitlesTimeline.frame.getWidth() / 2, Main.frame.getLocation().y + Main.frame.getHeight() + 7);
			    	    	
							SubtitlesTimeline.subtitlesNumber();					
							SubtitlesTimeline.timeline.remove(SubtitlesTimeline.waveform);
							SubtitlesTimeline.repaintTimeline();
							SubtitlesTimeline.timeline.removeAll();
							SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);	
			    		}
			    		
			    		playerFreeze();	
			    		
						Main.btnStart.setEnabled(false);						    		
						Main.comboFonctions.setEnabled(false);	
						
						//IMPORTANT Correct focus bug on Mac
						Main.frame.setVisible(false);
						Main.frame.setVisible(true);
					}
					else		
						resizeAll();
							
					if (Main.fileList.hasFocus() == false)
					{
						waveformContainer.requestFocus();
					}
		    	}
			}    		
			
		});
		loadMedia.start();
	}

	public static void setInfo() {
		    	
		String tff = "";
		if (FFprobe.interlaced != null && FFprobe.interlaced.equals("1"))
		{
			if (FFprobe.fieldOrder.equals("0"))
			{
				tff = " TFF";
			}
			else
				tff = " BFF";
		}
	
		if (FFprobe.videoCodec != null && FFprobe.imageResolution != null
		&& fileDuration > 40 && Main.inputDeviceIsRunning == false)
		{
			String vcodec = FFprobe.videoCodec.replace("video", "");
			for (String s : Main.functionsList)
			{
				if (vcodec.toLowerCase().equals(s.replace(".", "").replace("-", "").toLowerCase())
				|| s.toLowerCase().contains(vcodec.toLowerCase()))
				{
					vcodec = s;
					break;
				}
				else
					vcodec = vcodec.toUpperCase();
			}

			showScale.setText(FFprobe.imageResolution + " " + vcodec + tff + " " + FFprobe.imageDepth + "-bit");
		}
		else if (FFprobe.imageResolution != null)
		{
			showScale.setText(FFprobe.imageResolution + tff);
		}
		
		showScale.repaint();
		showFPS.repaint();
	}

	public static double getFPS() {
		
		if (Timecode.isDropFrame())
		{		
			if (FFprobe.currentFPS == 29.97f)
			{
				return 30;
			}
			else if (FFprobe.currentFPS == 59.94f)
			{
				return 60;
			}
		}
		
		return FFprobe.accurateFPS;
	}

	public static boolean getFileList(String file, double fileDuration) {
		
		try {
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				VideoPlayerMultiCuts.clearCutHistory();
				VideoPlayerMultiCuts.cutSegments.clear();
			}
			
			if (fileList.length() > 0 && fileDuration > 40 && Main.caseEnableSequence.isSelected() == false)
			{
				boolean fileExists = false;
				for (String line : fileList.toString().split(System.lineSeparator()))
				{	
					String s[] = line.split("\\|");
					String in[] = s[1].split(":");
					String out[] = s[2].split(":");					
					
					totalFrames = Math.round((double) fileDuration / 1000 * FFprobe.accurateFPS);
					
					if (s[0].equals(file))
					{											
						caseInH.setText(in[0]);
						caseInM.setText(in[1]);
						caseInS.setText(in[2]);
						caseInF.setText(in[3]);
						
						if (Main.caseSetTimecode.isSelected() && Main.caseIncrementTimecode.isSelected() == false && Main.setTimecodeEdited == false)
						{
							Main.TCset1.setText(caseInH.getText());
							Main.TCset2.setText(caseInM.getText());
							Main.TCset3.setText(caseInS.getText());
							Main.TCset4.setText(caseInF.getText());
						}
						
						caseOutH.setText(out[0]);
						caseOutM.setText(out[1]);
						caseOutS.setText(out[2]);
						caseOutF.setText(out[3]);
	
						//Multi cuts feature
						if (s.length > 3)
						{
							caseApplyCutToAll.setEnabled(false);
							
							int index = 0;
							for (int i = 0 ; i < s.length - 1 ; i += 2)
							{
								in = s[i+1].split(":");
								out = s[i+2].split(":");
								
								playerMarkIn = calculateMarkPosition(
							        Integer.parseInt(in[0]), 
							        Integer.parseInt(in[1]), 
							        Integer.parseInt(in[2]), 
							        Integer.parseInt(in[3])
							    );
								
								playerMarkOut = calculateMarkPosition(
							        Integer.parseInt(out[0]), 
							        Integer.parseInt(out[1]), 
							        Integer.parseInt(out[2]), 
							        Integer.parseInt(out[3])
							    );
								
								VideoPlayerMultiCuts.cutSegments.add(new CutSegment(index, playerMarkIn, playerMarkOut,
										Integer.parseInt(in[0]), 
								        Integer.parseInt(in[1]), 
								        Integer.parseInt(in[2]), 
								        Integer.parseInt(in[3]),
								        Integer.parseInt(out[0]), 
								        Integer.parseInt(out[1]), 
								        Integer.parseInt(out[2]), 
								        Integer.parseInt(out[3])));
								
								index ++;
							}
							
							activeSegmentIndex = 0;							
							
							waveformContainer.repaint();
						}
					
						fileExists = true;						
						break;
					}
					else
					{
						updateGrpIn(0);						
						updateGrpOut(totalFrames);
						
						fileExists = false;
					}
				}
	
				//Used for encoding
				if (Main.caseEnableSequence.isSelected())
				{						
					inputFramerateMS = Float.parseFloat(Main.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
				}
				else			
					inputFramerateMS = (double) (1000 / FFprobe.accurateFPS);	
																			
				setMarkers();	
				totalDuration();
				
				return fileExists;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return false;
	}

	public static void setFileList() {
		
		try {
			
			StringBuilder stb = new StringBuilder();
			
			String timeIn = String.format("%s:%s:%s:%s", caseInH.getText(), caseInM.getText(), caseInS.getText(), caseInF.getText());
	        String timeOut = String.format("%s:%s:%s:%s", caseOutH.getText(), caseOutM.getText(), caseOutS.getText(), caseOutF.getText());
	        String newEntry = videoPath;
	        
	        if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
	        	newEntry = videoPath;
	        	
	        	for (CutSegment seg : VideoPlayerMultiCuts.cutSegments)
	        	{
	        		timeIn = String.format("%02d:%02d:%02d:%02d", seg.inH, seg.inM, seg.inS, seg.inF);
	    	        timeOut = String.format("%02d:%02d:%02d:%02d", seg.outH, seg.outM, seg.outS, seg.outF);
	        		newEntry += "|" + timeIn + "|" + timeOut;
	        	}
	        	
	        	newEntry += System.lineSeparator();
			}
	        else
	        	newEntry += "|" + timeIn + "|" + timeOut + System.lineSeparator();
	        
			if (fileList.length() > 0 && fileDuration > 40 && Main.caseEnableSequence.isSelected() == false)
			{
				for (String file : fileList.toString().split(System.lineSeparator()))
				{
					stb.append(file + System.lineSeparator());
				}
	
				fileList.setLength(0);
				
				boolean fileExists = false;							
				for (String file : stb.toString().split(System.lineSeparator()))
				{
					String s[] = file.split("\\|");
					if (s[0].equals(videoPath)) //Replace at the same line
					{						
						fileList.append(newEntry);
						fileExists = true;
					}
					else if (file.equals("null") == false)
					{
						fileList.append(file + System.lineSeparator());
					}
				}
				
				if (fileExists == false)
				{
					fileList.append(newEntry);
				}
			}		
			else if (fileDuration > 40 && Main.caseEnableSequence.isSelected() == false)
			{
				fileList.append(newEntry);
			}
			
		} catch (Exception e) {}		
	}

	public static void updateGrpIn(double timeIn) {
			
		//NTSC framerate
		if (timeIn > 0)
			timeIn = Timecode.setNTSCtimecode(timeIn);
				
		if (Timecode.isDropFrame())
		{
			timeIn = Timecode.setDropFrameTimecode(timeIn);
		}
		
		caseInH.setText(Main.formatter.format(Math.floor(timeIn / getFPS() / 3600)));
		caseInM.setText(Main.formatter.format(Math.floor(timeIn / getFPS() / 60) % 60));
		caseInS.setText(Main.formatter.format(Math.floor(timeIn / getFPS()) % 60));    		
		caseInF.setText(Main.formatter.format(Math.floor(timeIn % getFPS())));
		
		if (Main.caseSetTimecode.isSelected() && Main.caseIncrementTimecode.isSelected() == false && Main.setTimecodeEdited == false)
		{
			Main.TCset1.setText(caseInH.getText());
			Main.TCset2.setText(caseInM.getText());
			Main.TCset3.setText(caseInS.getText());
			Main.TCset4.setText(caseInF.getText());
		}
		
		if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionReplaceAudio")))
		{
			Main.txtAudioOffset.setText(String.valueOf((int) timeIn));
			
			if (timeIn > 0)
			{
				if (Main.caseAudioOffset.isSelected() == false)
				{
					Main.caseAudioOffset.doClick();
				}				
			}
			else
			{
				if (Main.caseAudioOffset.isSelected())
				{
					Main.caseAudioOffset.doClick();
				}
			}
		}
	}

	public static void updateTimeIn() {
	
		setMarkers();
		
		playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * getFPS() + Integer.parseInt(caseInF.getText());
	
		//NTSC framerate
		playerCurrentFrame = Timecode.getNTSCtimecode(playerCurrentFrame);
		playerCurrentFrame = Timecode.getDropFrameTimecode(playerCurrentFrame);
		
		if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false && activeSegmentIndex != -1)
		{
			for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
		    {
				if (seg.index == activeSegmentIndex)
				{
					seg.inH = Integer.parseInt(caseInH.getText());
					seg.inM = Integer.parseInt(caseInM.getText());
					seg.inS = Integer.parseInt(caseInS.getText());
					seg.inF = Integer.parseInt(caseInF.getText());
					break;
				}
			}
		}
		
		playerSetTime(playerCurrentFrame);
	
		//FileList
		setFileList();
	}

	public static void updateGrpOut(double timeOut) {
		
		if (playerMarkOut <= waveformContainer.getWidth())
		{
			//NTSC framerate
			timeOut = Timecode.setNTSCtimecode(timeOut);
		}	
		else
		{
			//NTSC framerate
			timeOut = Timecode.setNTSCtimecode(totalFrames);
		}
		 		
		if (Timecode.isDropFrame())
		{
			timeOut = Timecode.setDropFrameTimecode(timeOut);
		}
	
		caseOutH.setText(Main.formatter.format(Math.floor(timeOut / getFPS() / 3600)));
		caseOutM.setText(Main.formatter.format(Math.floor(timeOut / getFPS() / 60) % 60));
		caseOutS.setText(Main.formatter.format(Math.floor(timeOut / getFPS()) % 60));    		
		caseOutF.setText(Main.formatter.format(Math.floor(timeOut % getFPS())));
	}

	public static void updateTimeOut() {
			
		setMarkers();		
		
		playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * getFPS() + Integer.parseInt(caseOutF.getText()) - 1;
	
		//NTSC framerate
		playerCurrentFrame = Timecode.getNTSCtimecode(playerCurrentFrame);
		playerCurrentFrame = Timecode.getDropFrameTimecode(playerCurrentFrame);
	
		if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false && activeSegmentIndex != -1)
		{
			for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
		    {
				if (seg.index == activeSegmentIndex)
				{
					seg.outH = Integer.parseInt(caseOutH.getText());
					seg.outM = Integer.parseInt(caseOutM.getText());
					seg.outS = Integer.parseInt(caseOutS.getText());
					seg.outF = Integer.parseInt(caseOutF.getText());
					break;
				}
			}
		}
	
		playerSetTime(playerCurrentFrame);
	
		//FileList
		setFileList();
	}

	public static void setMarkers() {
			
		try {
			
			playerMarkIn = calculateMarkPosition(
		        Integer.parseInt(caseInH.getText()), 
		        Integer.parseInt(caseInM.getText()), 
		        Integer.parseInt(caseInS.getText()), 
		        Integer.parseInt(caseInF.getText())
		    );
			
			playerMarkOut = calculateMarkPosition(
		        Integer.parseInt(caseOutH.getText()), 
		        Integer.parseInt(caseOutM.getText()), 
		        Integer.parseInt(caseOutS.getText()), 
		        Integer.parseInt(caseOutF.getText())
		    );
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
			    {
					seg.inMark = calculateMarkPosition(seg.inH, seg.inM, seg.inS, seg.inF);
			        seg.outMark = calculateMarkPosition(seg.outH, seg.outM, seg.outS, seg.outF);
				}
			}
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				double time = bufferCurrentFrame > 0 ? bufferCurrentFrame : playerCurrentFrame;
				
				CutSegment previousSegment = activeSegmentIndex > 0 ? VideoPlayerMultiCuts.cutSegments.get(activeSegmentIndex - 1) : null;
				CutSegment activeSegment = VideoPlayerMultiCuts.cutSegments.get(activeSegmentIndex);				
				CutSegment nextSegment = activeSegmentIndex < VideoPlayerMultiCuts.cutSegments.size() - 1 ? VideoPlayerMultiCuts.cutSegments.get(activeSegmentIndex + 1) : null;
				
				double segmentIn = VideoPlayerMultiCuts.getSegmentTime(activeSegment.inH, activeSegment.inM, activeSegment.inS, activeSegment.inF);
	            double segmentOut = VideoPlayerMultiCuts.getSegmentTime(activeSegment.outH, activeSegment.outM, activeSegment.outS, activeSegment.outF);
				
				//Mark in
				if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
				{					
					if (nextSegment != null && time > segmentOut) //Set the next segment index
					{
						nextSegment.inMark = playerMarkIn;
						nextSegment.inH = Integer.parseInt(caseInH.getText());
						nextSegment.inM = Integer.parseInt(caseInM.getText());
						nextSegment.inS = Integer.parseInt(caseInS.getText());
						nextSegment.inF = Integer.parseInt(caseInF.getText());				
				        
				        waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //Stop the dragging process
					}
					else if (dragSegmentIndex == activeSegmentIndex) //Only change values of the current segment
					{
						activeSegment.inMark = playerMarkIn;
						activeSegment.inH = Integer.parseInt(caseInH.getText());
						activeSegment.inM = Integer.parseInt(caseInM.getText());
						activeSegment.inS = Integer.parseInt(caseInS.getText());
						activeSegment.inF = Integer.parseInt(caseInF.getText());						
					}
					else
						waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //Stop the dragging process
				}
				
				//Mark out
				if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)))
				{
					if (previousSegment != null && time < segmentIn) //Set the next segment index
					{
						previousSegment.outMark = playerMarkOut;
						previousSegment.outH = Integer.parseInt(caseOutH.getText());
						previousSegment.outM = Integer.parseInt(caseOutM.getText());
						previousSegment.outS = Integer.parseInt(caseOutS.getText());
						previousSegment.outF = Integer.parseInt(caseOutF.getText());				
				        
				        waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //Stop the dragging process
					}					
					else if (dragSegmentIndex == activeSegmentIndex) //Only change values of the current segment
					{
						activeSegment.outMark = playerMarkOut;
				        activeSegment.outH = Integer.parseInt(caseOutH.getText());
				        activeSegment.outM = Integer.parseInt(caseOutM.getText());
				        activeSegment.outS = Integer.parseInt(caseOutS.getText());
				        activeSegment.outF = Integer.parseInt(caseOutF.getText());
					} 
					else
						waveformContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //Stop the dragging process
				}		
			}
	
			waveformContainer.repaint();
			
		} catch (Exception e) {
			//setMarkers can be called twice at once and crash the method
		}
	}

	protected static int calculateMarkPosition(double h, double m, double s, double f) {
	    int containerWidth = waveformContainer.getWidth();
	    double totalTime = (h * 3600 + m * 60 + s) * getFPS() + f;
	    
	    double timecode = Timecode.getDropFrameTimecode(Math.ceil(totalTime));
	    
	    if ((int) Timecode.getNTSCtimecode(timecode) < (int) totalFrames) {
	        return (int) Math.floor((containerWidth * timecode) / totalFrames);
	    }
	    return containerWidth;
	}

	public static void getTimePoint(double inputTime) {	
				
		if (inputTime >= totalFrames)
		{
			sliderChange = true;
			playerSetTime(totalFrames - 1);
			sliderChange = false;    		
		}
		else if (inputTime < 0)
		{
			inputTime = 0;
		}
		
		if (playerVideo != null && inputTime - offset < totalFrames)
		{    	    		
			if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) && mouseIsPressed)
			{
				updateGrpIn(inputTime - offset);
			}			
			
			if (waveformContainer.getCursor().equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) && mouseIsPressed)
			{
				updateGrpOut(inputTime - offset + 1);
			}
			
			//NTSC framerate
			double time = Timecode.setNTSCtimecode(inputTime);
			
			if (caseInternalTc.isSelected())
				time += offset;
			
			int newValue = (int) Math.floor((double) (waveformContainer.getSize().width * (time - offset)) / totalFrames);

			String dropFrame = ":";
			if (Timecode.isDropFrame())
			{
				time = Timecode.setDropFrameTimecode(time);		
				dropFrame = ";";
			}
			
			String h = Main.formatter.format(Math.floor(time / getFPS() / 3600));
			String m = Main.formatter.format(Math.floor(time / getFPS() / 60) % 60);
			String s = Main.formatter.format(Math.floor(time / getFPS()) % 60);   
			String f = Main.formatter.format(Math.floor(time % getFPS()));
	
			lblPosition.setText(h + ":" + m + ":" + s + dropFrame + f + " | " + Math.round(inputTime));	    
					
			if (sliderChange == false && Main.windowDrag == false)
			{   		
				if (cursorWaveform != null)
				{
					if (playerCurrentFrame <= 1)
					{
						cursorWaveform.setLocation(0, 0);
						cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
					}
					else
					{
						if (cursorWaveform.getX() > waveformContainer.getWidth())
						{
							cursorWaveform.setLocation(waveformContainer.getWidth(), 0);
							cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						}
						else if (newValue != cursorWaveform.getX()) //Only refresh when the value is different
						{					
							cursorWaveform.setLocation(newValue, 0);
							cursorHead.setLocation(cursorWaveform.getX() - 5, cursorWaveform.getY());
						}
					}
	
					if (cursorWaveform.getX() > waveformScrollPane.getWidth() + waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX() - (waveformContainer.getWidth() / waveformZoom) + 1);
					}
					else if (cursorWaveform.getX() < waveformScrollPane.getHorizontalScrollBar().getValue())
					{
						waveformScrollPane.getHorizontalScrollBar().setValue(cursorWaveform.getX());
					}				
				}
			}    
		}
		
		if (inputTime - offset >= totalFrames - 1)
		{
			btnPlay.setIcon(new FlatSVGIcon("resources/play.svg", 15, 15));
			btnPlay.setName("play");
		}			
	}

	public static void totalDuration() {	
		
		try {
									
			int inH = Integer.parseInt(caseInH.getText());
			int inM = Integer.parseInt(caseInM.getText());
			int inS = Integer.parseInt(caseInS.getText());
			int inF = Integer.parseInt(caseInF.getText());
			
			int outH = Integer.parseInt(caseOutH.getText());
			int outM = Integer.parseInt(caseOutM.getText());
			int outS = Integer.parseInt(caseOutS.getText());
			int outF = Integer.parseInt(caseOutF.getText());
			
			double totalIn =  (inH * 3600 + inM * 60 + inS) * getFPS() + inF;
			double totalOut = (outH * 3600 + outM * 60 + outS) * getFPS() + outF;
			double total = (double) Math.ceil(Timecode.getDropFrameTimecode(totalOut) - Timecode.getDropFrameTimecode(totalIn));
			
			durationH = (int) Math.floor(Timecode.setDropFrameTimecode(total) / getFPS() / 3600);
			durationM = (int) Math.floor(Timecode.setDropFrameTimecode(total) / getFPS() / 60) % 60;
			durationS = (int) Math.floor(Timecode.setDropFrameTimecode(total) / getFPS()) % 60;
			durationF = (int) Math.floor(Timecode.setDropFrameTimecode(total) % getFPS());
			
			//NTSC framerate
			total = Timecode.getNTSCtimecode(total);
			
			lblDuration.setText(Main.language.getProperty("lblBitrateTimecode") + " " + Main.formatter.format(durationH) + ":" + Main.formatter.format(durationM) + ":" + Main.formatter.format(durationS) + ":" + Main.formatter.format(durationF) + " | " + (int) total + " " + Main.language.getProperty("lblTotalFrames"));
			
			if (total <= 0)
			{
				lblDuration.setVisible(false);  
			}
			else if (waveformScrollPane.isVisible() && Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")) == false && Main.caseEnableSequence.isSelected() == false)
			{
	    		lblDuration.setVisible(true);   
	    		
	    		//Durée H264
	    		switch (Main.comboFonctions.getSelectedItem().toString())
	    		{
					case "H.264":
					case "H.265":
					case "H.266":
					case "WMV":
					case "MPEG-1":
					case "MPEG-2":
					case "VP8":
					case "VP9":
					case "AV1":
					case "Theora":
					case "MJPEG":
					case "Xvid":
					case "Blu-ray":
	
			    	    FFprobe.setFilesize();
			    	    
		    	     break;
	    		}
			}
		
		} catch (Exception e){}
	}

	public static void generateWaveform(boolean newWaveform) {

		if (FFmpeg.isRunning == false
		&& waveformContainer.isVisible()
		&& caseShowWaveform.isSelected()
		&& (FFprobe.hasAudio || Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionReplaceAudio")) && Main.list.getSize() > 1)
		&& addWaveformIsRunning == false
		&& Settings.btnDisableVideoPlayer.isSelected() == false)
		{		
			addWaveformIsRunning = true;
			
			if (newWaveform || waveform == null)
			{
				Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				if (newWaveform)
				{					
					waveform = null;
				}
			}
						
			addWaveform = new Thread(new Runnable()
			{
				@Override
				public void run() {
					
					if (newWaveform || waveform == null)
					{							
						long size = 6000;
	
						String start = "";
						String duration = "";
						if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles")))
						{	
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}
							} while (SubtitlesTimeline.frame == null);
							
							if (SubtitlesTimeline.waveform == null)
								SubtitlesTimeline.waveform = new JLabel();	
							
							long time = (long) (SubtitlesTimeline.timelineScrollBar.getValue() / SubtitlesTimeline.zoom);
	
							String h = Main.formatter.format(Math.floor(time / 1000) / 3600);
							String m = Main.formatter.format((Math.floor(time / 1000) / 60) % 60);
							String s = Main.formatter.format(Math.floor(time / 1000) % 60);    		
							String f = Main.formatterToMs.format(time % 1000);
							
							start = " -ss " + h + ":" + m + ":" + s + "." + f;
							duration = "atrim=duration=" + (SubtitlesTimeline.frame.getWidth() / 100) + ",";								
							size = (long) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom);
						}
						
						//IMPORTANT
						if (size > 549944)
							size = 549944;
						
						String inputMedia = videoPath;
						if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionReplaceAudio")) && Main.fileList.getSelectedIndex() + 1 < Main.list.getSize())
							inputMedia = Main.list.getElementAt(Main.fileList.getSelectedIndex() + 1);
						
						if (FFprobe.channels > 1 && comboAudioTrack.isVisible())
						{		
							if (comboAudioTrack.getSelectedItem() != null && comboAudioTrack.getSelectedItem().equals("Mix"))
							{
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + inputMedia + '"' + " -filter_complex " + '"' + "[0:a]amerge=inputs=" + FFprobe.channels + ",aresample=" + size +"," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"'  + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
							else
							{
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + inputMedia + '"' + " -filter_complex " + '"' + "[0:a:" + comboAudioTrack.getSelectedIndex() + "]aresample=" + size + "," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
						}
						else
						{
							LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + inputMedia + '"' + " -filter_complex " + '"' + "[0:a]aresample=" + size + "," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -");  																
						}						
						
						if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
						{
							Main.btnStart.setText(Main.language.getProperty("btnAddToRender"));
						}
						else
							Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
					}

					//add Waveform to Subtitling function
					if (Main.list.getSize() > 0 && waveform != null && SubtitlesTimeline.frame != null)
					{
						if (Main.comboFonctions.getSelectedItem().equals(Main.language.getProperty("functionSubtitles"))) //Ne charge plus l'image si la fenêtre est fermée entre temps
						{
							ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance((int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));
							
							SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
							SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
							SubtitlesTimeline.waveform.repaint();
						}
					}
					
					addWaveformIsRunning = false;
					Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}				
			});
			addWaveform.start();
		}
	}
	
	public static void stopWaveformCreation() {
		
		if (addWaveform != null && addWaveform.isAlive())
	    {
			addWaveform.interrupt();
			
			try {
				LibraryUtils.waveformWriter.write('q');
				LibraryUtils.waveformWriter.flush();
				LibraryUtils.waveformWriter.close();
			} catch (IOException er) {}
	        
	        if (LibraryUtils.waveformProcess != null && LibraryUtils.waveformProcess.isAlive())
	        {
	        	LibraryUtils.waveformProcess.destroyForcibly();
	        }
	        
	        waveform = null;
			waveformContainer.repaint();
	    }
	}

	public static void loadImage() {

	    if (videoPath == null || Main.list.getSize() <= 0 || Main.doNotLoadImage)
	    {
	        return;
	    }

	    //Stop player
		if (playerIsPlaying())
		{
			playerLoop = false;
		}

		if (loadImageThread != null && loadImageThread.isAlive()) {
		    loadImagePending = true;
		    return;
		}
		
		loadImagePending = false;
	
	    loadImageThread = new Thread(() -> {
		    		
	    	//Clear the buffer
			if (bufferedFrames.size() > 0)
			{				
				bufferedFrames.clear();
				waveformContainer.repaint();
			}
			
	        try
	        {		        		
	        	File file = new File(videoPath);
	        			        						
				String extension =  file.toString().substring(file.toString().lastIndexOf("."));	
				boolean isRaw = false;
				
				//FFprobe with RAW files
				switch (extension.toLowerCase()) { 
					case ".3fr":
					case ".arw":
					case ".crw":
					case ".cr2":
					case ".cr3":
					case ".dng":
					case ".kdc":
					case ".mrw":
					case ".nef":
					case ".nrw":
					case ".orf":
					case ".ptx":
					case ".pef":
					case ".raf":
					case ".r3d":
					case ".rw2":
					case ".srw":
					case ".x3f":
						isRaw = true;
				}
				
				if (Main.caseShowTimecode.isSelected() && FFprobe.timecode1.equals(""))
				{
					Main.caseShowTimecode.setSelected(false);
					Main.caseShowTimecode.setEnabled(false);
					Main.caseAddTimecode.setSelected(true);
					Main.TC1.setEnabled(true);
					Main.TC2.setEnabled(true);
					Main.TC3.setEnabled(true);
					Main.TC4.setEnabled(true);	
				}			
						
				//Player size
				final int playerWidth = player.getWidth();
				final int playerHeight = player.getHeight();
				
				//Deinterlace
				String deinterlace = "";
				
				//Format
				String colorFormat = FFprobe.hasAlpha ? "bgra64le" : "bgr48le";
				
				if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFprobe.interlaced != null && FFprobe.interlaced.equals("1"))
					deinterlace = " -vf bwdif=0:" + FFprobe.fieldOrder + ":0";		
	
				//Input point
				String inputPoint = " -ss " + (long) ((double) playerCurrentFrame * inputFramerateMS) + "ms";
				
				if (fileDuration <= 40 || Main.caseEnableSequence.isSelected()) //Image
					inputPoint = "";
		
				//Creating preview file													
				String cmd = deinterlace + " -frames:v 1 -an -sn -s " + playerWidth + "x" + playerHeight + " -scaler bicubic -y ";	

				if (preview == null && Main.caseAddSubtitles.isSelected() == false)
				{
					if (extension.toLowerCase().equals(".pdf"))
					{
						Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Xpdfreader.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
					
						try {
							Xpdfreader.runProcess.join();
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					}
					else if (isRaw)
					{									
						Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Dcraw.run(" -v -w -q 0 -o 1 -g 2.4 12.92 -Z - " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
						
						try {
							Dcraw.runProcess.join();
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
					}
					else if (Main.comboResolution.getSelectedItem().toString().contains("AI"))							
					{													
						Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						File preview = new File(Main.dirTemp + "preview.png");
						
						FFmpeg.run(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + deinterlace + " -frames:v 1 -an -sn -y " + '"' + preview + '"');		
						try {
							FFmpeg.runProcess.join();
						} catch (InterruptedException e) {
						    Thread.currentThread().interrupt();
						}
						
						String model = "realesr-general-wdn-x4v3";							
						if (Main.comboResolution.getSelectedItem().toString().contains("animation"))
						{
							model = "realesrgan-x4plus-anime";
						}
						else if (Main.comboResolution.getSelectedItem().toString().contains("photo"))
						{
							model = "4x_NMKD-Siax_200k";
						}
	
						Main.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
						Main.lblCurrentEncoding.setText(new File(videoPath).getName());
																						
						Ncnn.run(" -v -i " + '"' + preview + '"' + " -m " + '"' + Ncnn.modelsPath + '"' + " -n " + model + " -o " + '"' + preview + '"', true);
	
						try {
							Ncnn.runProcess.join();
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}
													
						Main.progressBar.setValue(0);
						Main.lblCurrentEncoding.setText(Main.language.getProperty("lblEncodageEnCours"));
														
						if (preview.exists())
						{									
							generatePreview(" -v quiet -hide_banner -i " + '"' + preview + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -", playerWidth, playerHeight); 
	
							if (mouseIsPressed == false)
							{
								previewUpscale = true;
							}
						}
						else
						{
							generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + '"' + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -", playerWidth, playerHeight);
						}
							
						if (preview.exists())
							preview.delete();
					}		
					else									
					{	
						generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -sn -f rawvideo -", playerWidth, playerHeight);
					}		
	
		            Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				            
				}	
				if (preview != null || Main.caseAddSubtitles.isSelected())
				{
					//Format
					String outputFormat = FFprobe.hasAlpha ? "abgr" : "rgb565le";
					
					//Subtitles are visible only from a video file
					if (Main.caseAddSubtitles.isSelected())
					{				
						generatePreview(Colorimetry.setInputCodec(extension) + " -v quiet -hide_banner" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + outputFormat + " -an -sn -f rawvideo -", playerWidth, playerHeight); 
					}
					else
					{															
						generatePreview(" -v quiet -hide_banner -f rawvideo -pixel_format " + colorFormat + " -video_size " + playerWidth + "x" + playerHeight + " -i pipe:0" + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + outputFormat + " -f rawvideo -", playerWidth, playerHeight);
					}							
				}
	        }
		    catch (Exception e)
		    {				
		    	e.printStackTrace();
	 	       	//JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
		    }
	        finally {
	
	  			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
	  			{
	  				Main.btnStart.setText(Main.language.getProperty("btnAddToRender"));
	  			}
  				else
					Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
	  			
	  			if (loadImagePending)
	  			{	  				
	  		        loadImagePending = false;
	  		        SwingUtilities.invokeLater(() -> loadImage());
	  		    }
	        }
	
	    });
	
	    loadImageThread.start();
	}
	
	public static void restartPlayback() {
		
		if (playerIsPlaying())				
		{
			//Do not generate the last pending image
			loadImagePending = false;
			
			if (loadImageThread != null)
			{
				try {
					loadImageThread.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
			
			if (VideoPlayerCore.setTime != null)
			{
				try {
					VideoPlayerCore.setTime.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
			
			processLoadImage.destroyForcibly();
			loadImageThread.interrupt();	
			
			VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
		}
	}

	private static void generatePreview(String cmd, int playerWidth, int playerHeight) {

	    try {
	    	
	        ProcessBuilder pbv = new ProcessBuilder(formatCommand(cmd));
	        pbv.redirectError(ProcessBuilder.Redirect.DISCARD);

	        processLoadImage = pbv.start();

	        // Always close stdin
	        try (OutputStream outputStream = processLoadImage.getOutputStream())
	        {
	            if (preview != null)
	                outputStream.write(preview);	            
	        }	        

	        try (BufferedInputStream inputStream = new BufferedInputStream(processLoadImage.getInputStream()))
	        {
	            if (preview == null && Main.caseAddSubtitles.isSelected() == false)
	            {
	                int bpp = FFprobe.hasAlpha ? 8 : 6;
					int frameSize = playerWidth * playerHeight * bpp;
					preview = inputStream.readNBytes(frameSize);
	            }
	            else
	            	readFrame(inputStream, playerWidth, playerHeight, false);
	        }

	        if (processLoadImage.waitFor(5, TimeUnit.SECONDS) == false) {
	            processLoadImage.destroyForcibly();
	        }	        
	        
	    } catch (Exception e) {
	        e.printStackTrace();

	        if (processLoadImage != null && processLoadImage.isAlive())
	        {
	            processLoadImage.destroyForcibly();
	        }
	    }

	    if (frameVideo != null) {
	        player.repaint();
	    }
	}
	
	public static void generateThumbnails() {
		
	    allThumbnails = null;
	    thumbnails.repaint();
	    
	    if (fileDuration <= 40)
	        return;

	    // Stop any previously running generation task/processes
	    stopThumbnailsCreation();
	    
	    int numberOfThumbnails = 20;
	    int numThreads = 4;
	    
	    //Format gpu
  		String bitDepth = FFprobe.imageDepth == 10 ? "p010" : "nv12";

	    thumbnailExecutor = Executors.newFixedThreadPool(numThreads);

	    thumbnailThread = new Thread(() -> {
	        double totalSeconds = fileDuration / 1000.0;
	        double intervalSeconds = totalSeconds / numberOfThumbnails;

	        BufferedImage[] images = new BufferedImage[numberOfThumbnails];

	        String ffmpeg = FFmpeg.PathToFFMPEG;
	        if (!System.getProperty("os.name").contains("Windows")) {
	            ffmpeg = ffmpeg.replace("\\", "");
	        }
	        final String ffmpegCmd = ffmpeg;
	        
	        List<Future<?>> futures = new ArrayList<>();

	        for (int i = 0; i < numberOfThumbnails; i++)
	        {
	            final int index = i;
	            
	            Future<?> future = thumbnailExecutor.submit(() -> {
	            	
	                if (Thread.currentThread().isInterrupted())
	                    return;

	                double seekTime = index * intervalSeconds;
	                String formattedSeek = String.format("%.3f", seekTime);
	                String crop = FFprobe.imageRatio >= 1.7 ? ",crop=34:in_h" : "";

	        	    //Get GPU decoding and scaling
	                String gpuDecoding = "-hwaccel none";
	        	    String scaling = "scale=-1:20";;
	        		if (LibraryUtils.cudaAvailable)
	        		{		
	        			gpuDecoding = "-hwaccel cuda -init_hw_device cuda -hwaccel_output_format cuda";	    				
	        			scaling = "scale_cuda=-1:20,hwdownload,format=" + bitDepth;
	        		}
	        		else if (LibraryUtils.amfAvailable)
	        		{
	        			gpuDecoding = "-hwaccel d3d11va -hwaccel_output_format d3d11";	    				
	        			scaling = "vpp_amf=-1:20,hwdownload,format=" + bitDepth;
	        		}
	        		else if (LibraryUtils.qsvAvailable)
	        		{		
	        			String child = "dxva2";
	    				if (LibraryUtils.detectIntelGen() >= 9 || LibraryUtils.isIntelArc)
	    				{
	    					child = "d3d11va";
	    				}					
	    	
	    				gpuDecoding = "-hwaccel qsv -init_hw_device qsv:hw,child_device_type=" + child + " -hwaccel_output_format qsv";	    				
	        			scaling = "scale_qsv=-1:20,hwdownload,format=" + bitDepth;
	        		}	
	        		else if (LibraryUtils.videotoolboxAvailable)
	        		{
	        			gpuDecoding = "-hwaccel videotoolbox -init_hw_device videotoolbox -hwaccel_output_format videotoolbox_vld";
	        			scaling = "scale_vt=-1:20,hwdownload,format=" + bitDepth;
	        		}

	        		List<String> command = new ArrayList<>();
	        		command.add(ffmpegCmd);

	        		Collections.addAll(command, gpuDecoding.trim().split("\\s+"));
	        		
	        		command.add("-nostdin");
	        		command.add("-flags2");
	        		command.add("+fast");
	        		command.add("-err_detect");
	        		command.add("ignore_err");
	        		command.add("-ss");
	        		command.add(formattedSeek);
	        		command.add("-i");
	        		command.add(videoPath);
	        		command.add("-vf");
	        		command.add(scaling + crop);
	        		command.add("-analyzeduration");
	        		command.add("0");
	        		command.add("-probesize");
	        		command.add("32");
	        		command.add("-frames:v");
	        		command.add("1");
	        		command.add("-f");
	        		command.add("image2pipe");
	        		command.add("-c:v");
	        		command.add("png");
	        		command.add("pipe:1");
	        		
	        		Process process = null;
	        		try {
	        		    ProcessBuilder pb = new ProcessBuilder(command);
	        		    pb.redirectError(ProcessBuilder.Redirect.DISCARD);
	        		    process = pb.start();
	        		    activeFFmpegProcesses.add(process);

	                    BufferedImage img;
	                    try (InputStream in = process.getInputStream()) {
	                        img = ImageIO.read(in);
	                    }
	                    	                    
	                    int exitCode = process.waitFor();
	                    if (exitCode == 0 && img != null) {
	                        images[index] = img;
	                        allThumbnails = images;
	                        
	                        // Thread-safe Swing UI update
	                        SwingUtilities.invokeLater(thumbnails::repaint);
	                    }

	                } catch (InterruptedException e) {
	                    Thread.currentThread().interrupt();
	                } catch (Exception ignored) {
	                } finally {
	                    if (process != null) {
	                        activeFFmpegProcesses.remove(process);
	                    }
	                }
	            });

	            futures.add(future);
	        }

	        // Wait for all worker tasks to complete or handle interruption
	        for (Future<?> f : futures) {
	            try {
	                f.get();
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                break;
	            } catch (ExecutionException ignored) {
	            }
	        }
	        
	        thumbnailExecutor.shutdownNow();
	    });

	    thumbnailThread.start();
	}
	
	public static void stopThumbnailsCreation() {
		
	    if (thumbnailExecutor != null && !thumbnailExecutor.isShutdown()) {
	        thumbnailExecutor.shutdownNow();
	    }
	    
	    if (thumbnailThread != null && thumbnailThread.isAlive()) {
	        thumbnailThread.interrupt();
	    }

	    // Kill all active concurrent FFmpeg processes forcibly
	    for (Process p : activeFFmpegProcesses) {
	        if (p != null && p.isAlive()) {
	            p.destroyForcibly();
	        }
	    }
	    activeFFmpegProcesses.clear();
	}
	
	public static void clearVideoPlayerStatus() {
		
		// Waveform
		if (addWaveformIsRunning)
		{		
			stopWaveformCreation();
			
			try {
				addWaveform.join();
			} catch (InterruptedException er) {
			    Thread.currentThread().interrupt();
			}
		}
		
		// Thumbnails
		if (thumbnails != null)
		{			
			allThumbnails = null;
			stopThumbnailsCreation();
		}
		
		if (playerIsPlaying())
			VideoPlayerUI.btnPlay.doClick();
		
		playerStop();
		VideoPlayerUI.player.removeAll();					
		frameVideo = null;
		preview = null;
		VideoPlayerUI.resizeAll();

	}
}
