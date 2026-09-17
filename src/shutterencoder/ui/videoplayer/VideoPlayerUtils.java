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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.DCRAW;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.NCNN;
import shutterencoder.library.XPDFREADER;
import shutterencoder.ui.main.Shutter;
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
	protected static volatile boolean loadImageRunning = false;
	protected static volatile boolean loadImagePending = false;
	public static Thread loadImageProcess = new Thread();
	protected static final Object loadImageLock = new Object();
	
	public static void setMedia() {

		loadMedia = new Thread(new Runnable()
		{
			@Override
			public void run()
			{    			
				if (FFMPEG.isRunning == false
		    	|| (Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false
		    	&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("resume")) == false
		    	&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")) == false))
		    	{
		   	    	//Updating video file
					if (Shutter.list.getSize() > 0)
					{				
						if (Shutter.fileList.getSelectedIndices().length == 0)
			      		{
							Shutter.fileList.setSelectedIndex(0);
			      		}
														
						//set timecode & Shutter.fileName locations
						VideoPlayerOverlay.refreshTimecodeAndText();				
						
						if (Shutter.scanIsRunning)
						{
							File dir = new File(Shutter.list.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									videoPath = f.toString();
									break;
								}
							}
						} 
						else if (Shutter.inputDeviceIsRunning)
						{
							videoPath = Shutter.list.firstElement();
							setInfo();
						}
						
						//Reset when changing file													
						if (Shutter.fileList.getSelectedValue().equals(videoPath) == false && (new File(Shutter.fileList.getSelectedValue()).isFile() || Shutter.scanIsRunning) || Shutter.inputDeviceIsRunning)
						{				
							//Stop player
							if (playerIsPlaying())
							{
								btnPlay.doClick();
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
							if (FFPROBE.isRunning)
							{
								try {
									FFPROBE.processData.join();
								} catch (InterruptedException er) {
								    Thread.currentThread().interrupt();
								}
							}
							
							if (Shutter.scanIsRunning == false)
								videoPath = Shutter.fileList.getSelectedValue();
							
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
									FFPROBE.totalLength = 0;
							}
				
							try {
								FunctionUtils.analyze(new File(videoPath), isRaw, true);
							} catch (InterruptedException e) {}
							
							//IMPORTANT							
							btnStop.doClick();							
							Shutter.fileList.repaint();							
							fileDuration = FFPROBE.totalLength; //Avoid a bug when totalLength is loader somewhere else

							if (isRaw)
							{
								Shutter.btnStart.setEnabled(true);
							}
																					
							cursorCurrentFrame.setBounds(0, 0, 1, waveformContainer.getHeight() - 1);
							setPlayerButtons(true);	

							//Generate filmstrip							
							if (caseShowThumbnails.isSelected())
								generateThumbnails();
							
							//Add layers
							if (Shutter.caseAddWatermark.isSelected()) {
								player.add(Shutter.logo);
							}
	
							if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesBurn) {
								player.add(Shutter.subsCanvas);
							}
	
							if (Shutter.caseAddTimecode.isSelected() || Shutter.caseShowTimecode.isSelected()) {
								player.add(Shutter.timecode);
							}
	
							if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected()) {
								player.add(Shutter.fileName);
							}
	
							if (Shutter.caseEnableCrop.isSelected()) {
								// Shutter.overImage need to be the last component added
								player.add(Shutter.selection);
								player.add(Shutter.overImage);
							}
														
							//Get GOP size
							seekOnKeyFrames = false;
							if (FFPROBE.audioOnly == false)
							{							
								FFPROBE.AnalyzeGOP(videoPath, false);
								do {
									//Slow down the loop
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (FFPROBE.gopCount > 2)
									{										
										FFPROBE.process.destroyForcibly();
										break;
									}
								} while (FFPROBE.processGOP.isAlive());								
							}
							
							//Jump on key frames only
							if (FFPROBE.audioOnly == false
							&& (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionConform"))))
							{
								if (FFPROBE.gopCount > 2)
								{
									seekOnKeyFrames = true;
								}
							}
							
							//Autocrop
							if (Shutter.caseEnableCrop.isSelected() && Shutter.comboPreset.getSelectedIndex() == 1)
				    		{
				    			LibraryUtils.setCropDetect(new File(videoPath));	  
				    		}
							
							//Burn subtitles
							if (Shutter.caseAddSubtitles.isSelected())
							{	
								if (Shutter.subtitlesBurn)
								{
									Shutter.autoBurn = true;
								}
								else
									Shutter.autoEmbed = true;
								
								String ext = videoPath.substring(videoPath.lastIndexOf("."));
																
								if (new File(videoPath.replace(ext, ".srt")).exists()
								|| new File (videoPath.replace(ext, ".vtt")).exists()
								|| new File (videoPath.replace(ext, ".ass")).exists()
								|| new File (videoPath.replace(ext, ".ssa")).exists()
								|| new File (videoPath.replace(ext, ".scc")).exists()
								|| Shutter.comboSubsSource.getSelectedIndex() != 0)
								{
									FunctionUtils.addSubtitles(false);
									if (loadImageProcess != null)
									{
										try {
											loadImageProcess.join();
										} catch (InterruptedException e) {
										    Thread.currentThread().interrupt();
										}
									}
									FunctionUtils.addSubtitles(true);
								}
								
								Shutter.autoBurn = false;
								Shutter.autoEmbed = false;
								
								try {
									FFMPEG.runProcess.join();
								} catch (InterruptedException e) {
								    Thread.currentThread().interrupt();
								}
							}
														
							if ((System.getProperty("os.name").contains("Mac") && Shutter.arch.equals("x86_64")) || System.getProperty("os.name").contains("Linux"))
							{
								if (FFPROBE.subtitleStreams != Shutter.comboSubsSource.getItemCount() - 1)
								{
									Shutter.comboSubsSource.removeAllItems();
									Shutter.comboSubsSource.addItem(Shutter.language.getProperty("file"));
									for (int i = 0 ; i < FFPROBE.subtitleStreams ; i++)
									{
										Shutter.comboSubsSource.addItem(Shutter.language.getProperty("source") + " #" + (i + 1));
									}
								}
							}
							else
							{
								if (FFPROBE.subtitleStreams != Shutter.comboSubsSource.getItemCount() - 2)
								{
									Shutter.comboSubsSource.removeAllItems();
									Shutter.comboSubsSource.addItem(Shutter.language.getProperty("file"));								
									for (int i = 0 ; i < FFPROBE.subtitleStreams ; i++)
									{
										Shutter.comboSubsSource.addItem(Shutter.language.getProperty("source") + " #" + (i + 1));
									}
									Shutter.comboSubsSource.addItem(Shutter.language.getProperty("functionTranscribe"));
								}
							}
							
							//Image sequence
							if (Shutter.caseEnableSequence.isSelected())
							{	
								//Create the concat text file
								FunctionUtils.setConcat(new File("concat.txt"), Shutter.dirTemp);						
								inputFramerateMS = Float.parseFloat(Shutter.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
							}
							else					
								inputFramerateMS = (double) (1000 / FFPROBE.accurateFPS);		
									
							bufferCurrentFrame = 0;
							playerCurrentFrame = 0;
			
							caseInternalTc.setEnabled(true);	
							Shutter.caseShowTimecode.setEnabled(true);
							
							Shutter.textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
							
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
							
							if (fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false && Shutter.frame.getSize().width > 654)
							{
								lblPosition.setVisible(true);
								lblDuration.setVisible(true);
							}
																					
							totalFrames = Math.round((double) fileDuration / 1000 * FFPROBE.accurateFPS);
							
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
							if (FFPROBE.previousImageWidth > 0)	
							{
								scale = ((double) FFPROBE.imageWidth / FFPROBE.previousImageWidth);
								
								if (scale != 0.0f)
								{
									//Display timecode
									if (Shutter.caseShowTimecode.isSelected() || Shutter.caseAddTimecode.isSelected())
									{
										Shutter.textTcSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textTcSize.getText()) * scale)));		
									}
									
									//Display text
									if (Shutter.caseShowFileName.isSelected() || Shutter.caseAddText.isSelected())
									{
										Shutter.textNameSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textNameSize.getText()) * scale)));
									}
									
									//Watermark
									if (Shutter.caseAddWatermark.isSelected() && (FFPROBE.imageWidth != FFPROBE.previousImageWidth || FFPROBE.imageHeight != FFPROBE.previousImageHeight))				
									{	
										Shutter.textWatermarkSize.setText(String.valueOf(Math.round(Integer.parseInt(Shutter.textWatermarkSize.getText()) * scale)));	
									}
									
									resizeAll();
								}
							}						
						}	
						
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
					
					if (Shutter.lblCurrentEncoding.getText().equals(Shutter.language.getProperty("processEnded")))
					{
						Shutter.progressBar.setValue(Shutter.progressBar.getMaximum());
					}
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
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
					else if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
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
					
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
					{						
						File video = new File(videoPath);
						String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
						
						SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");		
						SubtitlesTimeline.timelineScrollBar.setMaximum((int) (((totalFrames - 2) * inputFramerateMS) * SubtitlesTimeline.zoom));
									
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			    		Shutter.frame.setLocation(Shutter.frame.getLocation().x , dim.height/3 - Shutter.frame.getHeight()/2);
			
			    		if (Shutter.caseAddSubtitles.isSelected())
			    		{
			    			player.remove(Shutter.subsCanvas);
							Shutter.caseAddSubtitles.setSelected(false);	    	
			    		}
							    		    	
			    		if (SubtitlesTimeline.frame == null) 
			    		{	    	
			    			new SubtitlesTimeline();		
			    		}
			    		else
			    		{
			    			SubtitlesTimeline.frame.setVisible(true);
			    			SubtitlesTimeline.frame.setLocation((Shutter.frame.getLocation().x + Shutter.frame.getWidth() / 2) - SubtitlesTimeline.frame.getWidth() / 2, Shutter.frame.getLocation().y + Shutter.frame.getHeight() + 7);
			    	    	
							SubtitlesTimeline.subtitlesNumber();					
							SubtitlesTimeline.timeline.remove(SubtitlesTimeline.waveform);
							SubtitlesTimeline.repaintTimeline();
							SubtitlesTimeline.timeline.removeAll();
							SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);	
			    		}
			    		
			    		playerFreeze();	
			    		
						Shutter.btnStart.setEnabled(false);						    		
						Shutter.comboFonctions.setEnabled(false);	
						
						//IMPORTANT Correct focus bug on Mac
						Shutter.frame.setVisible(false);
						Shutter.frame.setVisible(true);
					}
					else		
						resizeAll();
							
					if (Shutter.fileList.hasFocus() == false)
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
		if (FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
		{
			if (FFPROBE.fieldOrder.equals("0"))
			{
				tff = " TFF";
			}
			else
				tff = " BFF";
		}
	
		if (FFPROBE.videoCodec != null && fileDuration > 40 && Shutter.inputDeviceIsRunning == false)
		{
			String vcodec = FFPROBE.videoCodec.replace("video", "");
			for (String s : Shutter.functionsList)
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
	
			showScale.setText(FFPROBE.imageResolution + " " + vcodec + tff + " " + FFPROBE.imageDepth + "-bit");
		}
		else
			showScale.setText(FFPROBE.imageResolution + tff);
		
		showScale.repaint();
		showFPS.repaint();
	}

	public static double getFPS() {
		
		if (Timecode.isDropFrame())
		{		
			if (FFPROBE.currentFPS == 29.97f)
			{
				return 30;
			}
			else if (FFPROBE.currentFPS == 59.94f)
			{
				return 60;
			}
		}
		
		return FFPROBE.accurateFPS;
	}

	public static boolean getFileList(String file, double fileDuration) {
		
		try {
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				VideoPlayerMultiCuts.clearCutHistory();
				VideoPlayerMultiCuts.cutSegments.clear();
			}
			
			if (fileList.length() > 0 && fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
			{
				boolean fileExists = false;
				for (String line : fileList.toString().split(System.lineSeparator()))
				{	
					String s[] = line.split("\\|");
					String in[] = s[1].split(":");
					String out[] = s[2].split(":");					
					
					totalFrames = Math.round((double) fileDuration / 1000 * FFPROBE.accurateFPS);
					
					if (s[0].equals(file))
					{											
						caseInH.setText(in[0]);
						caseInM.setText(in[1]);
						caseInS.setText(in[2]);
						caseInF.setText(in[3]);
						
						if (Shutter.caseSetTimecode.isSelected() && Shutter.caseIncrementTimecode.isSelected() == false && Shutter.setTimecodeEdited == false)
						{
							Shutter.TCset1.setText(caseInH.getText());
							Shutter.TCset2.setText(caseInM.getText());
							Shutter.TCset3.setText(caseInS.getText());
							Shutter.TCset4.setText(caseInF.getText());
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
				if (Shutter.caseEnableSequence.isSelected())
				{						
					inputFramerateMS = Float.parseFloat(Shutter.caseSequenceFPS.getSelectedItem().toString().replace(",", "."));
				}
				else			
					inputFramerateMS = (double) (1000 / FFPROBE.accurateFPS);	
																			
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
	        
			if (fileList.length() > 0 && fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
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
			else if (fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
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
		
		caseInH.setText(Shutter.formatter.format(Math.floor(timeIn / getFPS() / 3600)));
		caseInM.setText(Shutter.formatter.format(Math.floor(timeIn / getFPS() / 60) % 60));
		caseInS.setText(Shutter.formatter.format(Math.floor(timeIn / getFPS()) % 60));    		
		caseInF.setText(Shutter.formatter.format(Math.floor(timeIn % getFPS())));
		
		if (Shutter.caseSetTimecode.isSelected() && Shutter.caseIncrementTimecode.isSelected() == false && Shutter.setTimecodeEdited == false)
		{
			Shutter.TCset1.setText(caseInH.getText());
			Shutter.TCset2.setText(caseInM.getText());
			Shutter.TCset3.setText(caseInS.getText());
			Shutter.TCset4.setText(caseInF.getText());
		}
		
		if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")))
		{
			Shutter.txtAudioOffset.setText(String.valueOf((int) timeIn));
			
			if (timeIn > 0)
			{
				if (Shutter.caseAudioOffset.isSelected() == false)
				{
					Shutter.caseAudioOffset.doClick();
				}				
			}
			else
			{
				if (Shutter.caseAudioOffset.isSelected())
				{
					Shutter.caseAudioOffset.doClick();
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
	
		caseOutH.setText(Shutter.formatter.format(Math.floor(timeOut / getFPS() / 3600)));
		caseOutM.setText(Shutter.formatter.format(Math.floor(timeOut / getFPS() / 60) % 60));
		caseOutS.setText(Shutter.formatter.format(Math.floor(timeOut / getFPS()) % 60));    		
		caseOutF.setText(Shutter.formatter.format(Math.floor(timeOut % getFPS())));
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
			playerSetTime(totalFrames);
			sliderChange = false;    		
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
			
			String h = Shutter.formatter.format(Math.floor(time / getFPS() / 3600));
			String m = Shutter.formatter.format(Math.floor(time / getFPS() / 60) % 60);
			String s = Shutter.formatter.format(Math.floor(time / getFPS()) % 60);   
			String f = Shutter.formatter.format(Math.floor(time % getFPS()));
	
			lblPosition.setText(h + ":" + m + ":" + s + dropFrame + f + " | " + Math.round(inputTime));	    
					
			if (sliderChange == false && Shutter.windowDrag == false)
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
		
		if (inputTime - offset >= totalFrames - 2)
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
			
			lblDuration.setText(Shutter.language.getProperty("lblBitrateTimecode") + " " + Shutter.formatter.format(durationH) + ":" + Shutter.formatter.format(durationM) + ":" + Shutter.formatter.format(durationS) + ":" + Shutter.formatter.format(durationF) + " | " + (int) total + " " + Shutter.language.getProperty("lblTotalFrames"));
			
			if (total <= 0)
			{
				lblDuration.setVisible(false);  
			}
			else if (waveformScrollPane.isVisible() && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false && Shutter.caseEnableSequence.isSelected() == false)
			{
	    		lblDuration.setVisible(true);   
	    		
	    		//Durée H264
	    		switch (Shutter.comboFonctions.getSelectedItem().toString())
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
	
			    	    FFPROBE.setFilesize();
			    	    
		    	     break;
	    		}
			}
		
		} catch (Exception e){}
	}

	public static void addWaveform(boolean newWaveform) {

		if (FFMPEG.isRunning == false && caseShowWaveform.isSelected() && FFPROBE.hasAudio && addWaveformIsRunning == false && Settings.btnDisableVideoPlayer.isSelected() == false)
		{		
			addWaveformIsRunning = true;
			
			if (newWaveform || waveform == null)
			{
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
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
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						{	
							do {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {}
							} while (SubtitlesTimeline.frame == null);
							
							if (SubtitlesTimeline.waveform == null)
								SubtitlesTimeline.waveform = new JLabel();	
							
							long time = (long) (SubtitlesTimeline.timelineScrollBar.getValue() / SubtitlesTimeline.zoom);
	
							String h = Shutter.formatter.format(Math.floor(time / 1000) / 3600);
							String m = Shutter.formatter.format((Math.floor(time / 1000) / 60) % 60);
							String s = Shutter.formatter.format(Math.floor(time / 1000) % 60);    		
							String f = Shutter.formatterToMs.format(time % 1000);
							
							start = " -ss " + h + ":" + m + ":" + s + "." + f;
							duration = "atrim=duration=" + (SubtitlesTimeline.frame.getWidth() / 100) + ",";								
							size = (long) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom);
						}
						
						//IMPORTANT
						if (size > 549944)
							size = 549944;
						
						if (FFPROBE.channels > 1 && comboAudioTrack.isVisible())
						{		
							if (comboAudioTrack.getSelectedItem() != null && comboAudioTrack.getSelectedItem().equals("Mix"))
							{
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]amerge=inputs=" + FFPROBE.channels + ",aresample=" + size +"," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"'  + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
							else
							{
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a:" + comboAudioTrack.getSelectedIndex() + "]aresample=" + size + "," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
						}
						else
						{
							LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]aresample=" + size + "," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x" + waveformContainer.getHeight() + ":colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -");  																
						}
						
						if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
						{
							Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
						}
						else
							Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
					}
					
					//add Waveform		
					try {
						
						if (Shutter.list.getSize() > 0 && isPiping == false && waveform != null)
						{
							if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles"))) //Ne charge plus l'image si la fenêtre est fermée entre temps
							{
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance((int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight(), Image.SCALE_AREA_AVERAGING));
								
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
								SubtitlesTimeline.waveform.repaint();
							}
						}
					}
					catch (Exception e) {}
					finally
					{					
						addWaveformIsRunning = false;
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}	
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

	public static void loadImage(boolean forceRefresh) {
		
	    if (videoPath == null || Shutter.list.getSize() <= 0 || Shutter.doNotLoadImage)
	    {
	        return;
	    }
	    	
	    synchronized (loadImageLock) {
	
	        if (loadImageRunning)
	        {
	            loadImagePending = true;
	            return;
	        }
	
	        loadImageRunning = true;
	    }
	
	    loadImageProcess = new Thread(() -> {
	
	    	//Clear the buffer
			if (bufferedFrames.size() > 0)
			{				
				bufferedFrames.clear();
				waveformContainer.repaint();
			}
						
			//Stop player
			if (playerIsPlaying())
			{
				btnPlay.doClick();
			}
		
	        try
	        {	
	        	do {
	        		Thread.sleep(10);
	        	} while (videoPath == null);
	        		
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
				
				if (Shutter.caseShowTimecode.isSelected() && FFPROBE.timecode1.equals(""))
				{
					Shutter.caseShowTimecode.setSelected(false);
					Shutter.caseShowTimecode.setEnabled(false);
					Shutter.caseAddTimecode.setSelected(true);
					Shutter.TC1.setEnabled(true);
					Shutter.TC2.setEnabled(true);
					Shutter.TC3.setEnabled(true);
					Shutter.TC4.setEnabled(true);	
				}			
						
				//Deinterlace
				String deinterlace = "";
				
				//Format
				String colorFormat = FFPROBE.hasAlpha ? "bgra64le" : "bgr48le";
				
				if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
					deinterlace = " -vf bwdif=0:" + FFPROBE.fieldOrder + ":0";		
	
				//Input point
				String inputPoint = " -ss " + (long) ((double) playerCurrentFrame * inputFramerateMS) + "ms";
				
				if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
					inputPoint = "";
		
				//Creating preview file													
				String cmd = deinterlace + " -frames:v 1 -an -sn -s " + player.getWidth() + "x" + player.getHeight() + " -scaler bicubic -y ";	
				
				if (preview == null && Shutter.caseAddSubtitles.isSelected() == false)
				{
					if (extension.toLowerCase().equals(".pdf"))
					{
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						XPDFREADER.run(" -r 300 -f 1 -l 1 " + '"' + file.toString() + '"' + " - | PathToFFMPEG -i -" + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
					
						do {
			            	Thread.sleep(10);  					            	
			            } while (XPDFREADER.isRunning && XPDFREADER.error == false);	
					}
					else if (isRaw)
					{									
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						DCRAW.run(" -v -w -q 0 -o 1 -g 2.4 12.92 -Z - " + '"' + file.toString() + '"' + " | PathToFFMPEG -i -" + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
						
			            do {
			            	Thread.sleep(10);  					            	
			            } while (DCRAW.isRunning && DCRAW.error == false);	
					}
					else if (Shutter.comboResolution.getSelectedItem().toString().contains("AI"))							
					{													
						Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						File preview = new File(Shutter.dirTemp + "preview.png");
						
						FFMPEG.run(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + deinterlace + " -frames:v 1 -an -sn -y " + '"' + preview + '"');		
						try {
							FFMPEG.runProcess.join();
						} catch (InterruptedException e) {
						    Thread.currentThread().interrupt();
						}
						
						String model = "realesr-general-wdn-x4v3";							
						if (Shutter.comboResolution.getSelectedItem().toString().contains("animation"))
						{
							model = "realesrgan-x4plus-anime";
						}
						else if (Shutter.comboResolution.getSelectedItem().toString().contains("photo"))
						{
							model = "4x_NMKD-Siax_200k";
						}
	
						Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
						Shutter.lblCurrentEncoding.setText(new File(videoPath).getName());
																						
						NCNN.run(" -v -i " + '"' + preview + '"' + " -m " + '"' + NCNN.modelsPath + '"' + " -n " + model + " -o " + '"' + preview + '"', true);
	
						do {									
							Thread.sleep(10);
						} while (NCNN.isRunning);
													
						Shutter.progressBar.setValue(0);
						Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("lblEncodageEnCours"));
														
						if (preview.exists())
						{									
							generatePreview(" -v quiet -hide_banner -i " + '"' + preview + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -"); 
	
							if (mouseIsPressed == false)
							{
								previewUpscale = true;
							}
						}
						else
						{
							generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + '"' + " -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
						}
							
						if (preview.exists())
							preview.delete();
					}		
					else									
					{	
						generatePreview(Colorimetry.setInputCodec(extension) + inputPoint + " -v quiet -hide_banner -i " + '"' + file.toString() + '"' + cmd + " -c:v rawvideo -pix_fmt " + colorFormat + " -an -sn -f rawvideo -");
					}		
	
		            Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				            
				}	
										
				if (preview != null || Shutter.caseAddSubtitles.isSelected())
				{		
					//Format
					String outputFormat = FFPROBE.hasAlpha ? "abgr" : "rgb565be";
					
					//Subtitles are visible only from a video file
					if (Shutter.caseAddSubtitles.isSelected())
					{				
						generatePreview(Colorimetry.setInputCodec(extension) + " -v quiet -hide_banner" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + outputFormat + " -an -sn -f rawvideo -"); 
					}
					else
					{															
						generatePreview(" -v quiet -hide_banner -f rawvideo -pixel_format " + colorFormat + " -video_size " + player.getWidth() + "x" + player.getHeight() + " -i pipe:0" + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + outputFormat + " -f rawvideo -");
					}							
				}
	        }
		    catch (Exception e)
		    {				
		    	e.printStackTrace();
	 	       	//JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
		    }
	        finally {
	
	        	try {
					FFMPEG.runProcess.join();
				} catch (InterruptedException e) {
				    Thread.currentThread().interrupt();
				}
				
	  			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
					Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
				else
					Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
	        	
	  			boolean reload;
	
	  			synchronized (loadImageLock)
	  			{
	  			    loadImageRunning = false;
	  			    reload = loadImagePending;
	  			    loadImagePending = false;
	  			}
	
	  			if (reload) {
	  			    loadImage(false);
	  			}
	        }
	
	    });
	
	    loadImageProcess.start();
	}

	private static void generatePreview(String cmd) {
		
		try {		
						
			ProcessBuilder pbv = new ProcessBuilder(formatCommand(cmd));
			Process process = pbv.start();
						
			//Console.consoleFFMPEG.append(cmd + System.lineSeparator());
	
			//Write preview frame to ffmpeg input
			if (preview != null)
			{
		        OutputStream outputStream = process.getOutputStream();
		        process.getOutputStream().write(preview);
		        outputStream.close();
			}				     	
			/*
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));	
			
			while ((line = input.readLine()) != null)
			{
				System.out.println(line);
			}*/
	        
	        InputStream is = process.getInputStream();				
			BufferedInputStream inputStream = new BufferedInputStream(is);
	
			if (preview == null && Shutter.caseAddSubtitles.isSelected() == false)
			{	
				int bpp = FFPROBE.hasAlpha ? 8 : 6;
				int frameSize = player.getWidth() * player.getHeight() * bpp;
				preview = inputStream.readNBytes(frameSize);
			}
			else
			{
				readFrame(inputStream, player.getWidth(), player.getHeight(), false);
			}
	
			inputStream.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (frameVideo != null)
		{
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
  		String bitDepth = FFPROBE.imageDepth == 10 ? "p010" : "nv12";

	    thumbnailExecutor = Executors.newFixedThreadPool(numThreads);

	    thumbnailThread = new Thread(() -> {
	        double totalSeconds = fileDuration / 1000.0;
	        double intervalSeconds = totalSeconds / numberOfThumbnails;

	        BufferedImage[] images = new BufferedImage[numberOfThumbnails];

	        String ffmpeg = FFMPEG.PathToFFMPEG;
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
	                String crop = FFPROBE.imageRatio >= 1.7 ? ",crop=34:in_h" : "";

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
