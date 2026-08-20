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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.subtitling.SubtitlesTimeline;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts.CutSegment;

public class VideoPlayerUtils extends VideoPlayerCore {

	//FileList
	public static StringBuilder fileList = new StringBuilder();
	
	public static void setMedia() {
		
		VideoPlayerCore.loadMedia = new Thread(new Runnable()
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
									VideoPlayerCore.videoPath = f.toString();
									break;
								}
							}
						} 
						else if (Shutter.inputDeviceIsRunning)
						{
							VideoPlayerCore.videoPath = Shutter.list.firstElement();
							VideoPlayerUtils.setInfo();
						}
						
						//Reset when changing file													
						if (Shutter.fileList.getSelectedValue().equals(VideoPlayerCore.videoPath) == false && (new File(Shutter.fileList.getSelectedValue()).isFile() || Shutter.scanIsRunning) || Shutter.inputDeviceIsRunning)
						{				
							//Stop player
							if (VideoPlayerCore.playerIsPlaying())
							{
								btnPlay.doClick();
							}
							
							//Clear the buffer
							if (VideoPlayerCore.bufferedFrames.size() > 0)
							{											
								VideoPlayerCore.bufferedFrames.clear();
								waveformContainer.repaint();
							}
									
							//Clear the segment list
							VideoPlayerCore.activeSegmentIndex = -1;
							if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
							{					
								VideoPlayerMultiCuts.clearCutHistory();
								VideoPlayerMultiCuts.cutSegments.clear();
							}
							
							//IMPORTANT
							if (FFPROBE.isRunning)
							{
								do {								
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {}								
								} 
								while (FFPROBE.isRunning);
							}
							
							if (Shutter.scanIsRunning == false)
								VideoPlayerCore.videoPath = Shutter.fileList.getSelectedValue();
							
							if (VideoPlayerCore.frameVideo != null)
								VideoPlayerCore.frameVideo = null;
							
							if (VideoPlayerCore.preview != null)
								VideoPlayerCore.preview = null;
							
							if (VideoPlayerCore.waveform != null)
							{
								VideoPlayerCore.waveform = null;
								waveformIcon.setIcon(null);
								waveformIcon.repaint();
							}
							
							waveformZoom = 1;
												
							if (VideoPlayerCore.addWaveformIsRunning && LibraryUtils.waveformWriter != null)
							{
								try {
									LibraryUtils.waveformWriter.write('q');
									LibraryUtils.waveformWriter.flush();
									LibraryUtils.waveformWriter.close();
								} catch (IOException er) {}
								
								LibraryUtils.waveformProcess.destroy();
							}
													
							String extension = VideoPlayerCore.videoPath.substring(VideoPlayerCore.videoPath.lastIndexOf("."));	
								
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
								FunctionUtils.analyze(new File(VideoPlayerCore.videoPath), isRaw, true);
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
							
							seekOnKeyFrames = false;
							
							if (FFPROBE.audioOnly == false
							&& (Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionCut"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))
							|| Shutter.comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionConform"))))
							{
								FFPROBE.AnalyzeGOP(VideoPlayerCore.videoPath, false);
								do {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {}
									
									if (FFPROBE.gopCount > 2)
									{
										seekOnKeyFrames = true;
										FFPROBE.process.destroy();
										break;
									}
								} while (FFPROBE.isRunning);	
							}
							else
							{
								Shutter.caseEnableCrop.setEnabled(true);
								Shutter.caseAddWatermark.setEnabled(true);
								Shutter.caseSafeArea.setEnabled(true);
							}
							
							//Autocrop
							if (Shutter.caseEnableCrop.isSelected() && Shutter.comboPreset.getSelectedIndex() == 1)
				    		{
				    			LibraryUtils.setCropDetect(new File(VideoPlayerCore.videoPath));	  
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
								
								String ext = VideoPlayerCore.videoPath.substring(VideoPlayerCore.videoPath.lastIndexOf("."));
																
								if (new File(VideoPlayerCore.videoPath.replace(ext, ".srt")).exists()
								|| new File (VideoPlayerCore.videoPath.replace(ext, ".vtt")).exists()
								|| new File (VideoPlayerCore.videoPath.replace(ext, ".ass")).exists()
								|| new File (VideoPlayerCore.videoPath.replace(ext, ".ssa")).exists()
								|| new File (VideoPlayerCore.videoPath.replace(ext, ".scc")).exists()
								|| Shutter.comboSubsSource.getSelectedIndex() != 0)
								{
									FunctionUtils.addSubtitles(false);
									if (VideoPlayerCore.runProcess != null)
									{
										do {
											try {
												Thread.sleep(100);
											} catch (InterruptedException e) {}
										} while (VideoPlayerCore.runProcess.isAlive());
									}
									FunctionUtils.addSubtitles(true);
								}
								
								Shutter.autoBurn = false;
								Shutter.autoEmbed = false;
								
								try {
									do {
										Thread.sleep(100);
									} while (FFMPEG.isRunning);
								} catch (InterruptedException e) {}		
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
									
							VideoPlayerCore.bufferCurrentFrame = 0;
							VideoPlayerCore.playerCurrentFrame = 0;
			
							caseInternalTc.setEnabled(true);	
							Shutter.caseShowTimecode.setEnabled(true);
							
							Shutter.textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
							
							VideoPlayerUtils.setInfo();
							
							btnPlay.setEnabled(true);
							btnPrevious.setEnabled(true);
							btnNext.setEnabled(true);
							btnStop.setEnabled(true);
							btnMarkIn.setEnabled(true);
							btnMarkOut.setEnabled(true);
							btnGoToIn.setEnabled(true);
							btnGoToOut.setEnabled(true);	
							
							if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
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
								VideoPlayerUtils.updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
								VideoPlayerUtils.updateGrpOut(Timecode.getNTSCtimecode(totalFrames - InputAndOutput.savedOutPoint));
							}
							else
							{
								VideoPlayerUtils.updateGrpIn(0);
								VideoPlayerUtils.updateGrpOut(totalFrames);
								
								playerMarkIn = 0;
								playerMarkOut = waveformContainer.getWidth();
							}
	
							waveformContainer.repaint();
							
							//Setup fileList
							if (caseApplyCutToAll.isVisible() == false || caseApplyCutToAll.isSelected() == false)
							{
								VideoPlayerUtils.getFileList(VideoPlayerCore.videoPath, fileDuration);
							}
								
							VideoPlayerUtils.setFileList();	
							
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
						
						VideoPlayerCore.videoPath = null;
						showScale.setVisible(false);
						VideoPlayerCore.playerStop();
						VideoPlayerCore.playerSetTime(0);
			
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
						
						if (VideoPlayerCore.waveform != null)
						{
							VideoPlayerCore.waveform = null;
							waveformIcon.setIcon(null);
							waveformIcon.repaint();
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
						
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && VideoPlayerCore.videoPath != null)
					{						
						File video = new File(VideoPlayerCore.videoPath);
						String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
						
						SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");		
						SubtitlesTimeline.timelineScrollBar.setMaximum((int) (((totalFrames - 2) * VideoPlayerUI.inputFramerateMS) * SubtitlesTimeline.zoom));
									
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			    		Shutter.frame.setLocation(Shutter.frame.getLocation().x , dim.height/3 - Shutter.frame.getHeight()/2);
			
			    		if (Shutter.caseAddSubtitles.isSelected())
			    		{
			    			VideoPlayerUI.player.remove(Shutter.subsCanvas);
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
			    		
			    		VideoPlayerCore.playerFreeze();	
			    		
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
		VideoPlayerCore.loadMedia.start();
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
	
		if (FFPROBE.videoCodec != null && VideoPlayerUI.fileDuration > 40 && Shutter.inputDeviceIsRunning == false)
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
	
			VideoPlayerUI.showScale.setText(FFPROBE.imageResolution + " " + vcodec + tff + " " + FFPROBE.imageDepth + "-bit");
		}
		else
			VideoPlayerUI.showScale.setText(FFPROBE.imageResolution + tff);
		
		VideoPlayerUI.showScale.repaint();
		VideoPlayerUI.showFPS.repaint();
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
								
								playerMarkIn = VideoPlayerUtils.calculateMarkPosition(
							        Integer.parseInt(in[0]), 
							        Integer.parseInt(in[1]), 
							        Integer.parseInt(in[2]), 
							        Integer.parseInt(in[3])
							    );
								
								playerMarkOut = VideoPlayerUtils.calculateMarkPosition(
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
							
							VideoPlayerCore.activeSegmentIndex = 0;							
							
							waveformContainer.repaint();
						}
					
						fileExists = true;						
						break;
					}
					else
					{
						VideoPlayerUtils.updateGrpIn(0);						
						VideoPlayerUtils.updateGrpOut(totalFrames);
						
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
																			
				VideoPlayerUtils.setMarkers();	
				VideoPlayerUtils.totalDuration();
				
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
	        String newEntry = VideoPlayerCore.videoPath;
	        
	        if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
	        	newEntry = VideoPlayerCore.videoPath;
	        	
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
					if (s[0].equals(VideoPlayerCore.videoPath)) //Replace at the same line
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
			Shutter.TCset1.setText(VideoPlayerUI.caseInH.getText());
			Shutter.TCset2.setText(VideoPlayerUI.caseInM.getText());
			Shutter.TCset3.setText(VideoPlayerUI.caseInS.getText());
			Shutter.TCset4.setText(VideoPlayerUI.caseInF.getText());
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
	
		VideoPlayerUtils.setMarkers();
		
		VideoPlayerCore.playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * getFPS() + Integer.parseInt(caseInF.getText());
	
		//NTSC framerate
		VideoPlayerCore.playerCurrentFrame = Timecode.getNTSCtimecode(VideoPlayerCore.playerCurrentFrame);
		VideoPlayerCore.playerCurrentFrame = Timecode.getDropFrameTimecode(VideoPlayerCore.playerCurrentFrame);
		
		if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false && VideoPlayerCore.activeSegmentIndex != -1)
		{
			for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
		    {
				if (seg.index == VideoPlayerCore.activeSegmentIndex)
				{
					seg.inH = Integer.parseInt(caseInH.getText());
					seg.inM = Integer.parseInt(caseInM.getText());
					seg.inS = Integer.parseInt(caseInS.getText());
					seg.inF = Integer.parseInt(caseInF.getText());
					break;
				}
			}
		}
		
		VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
	
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
			
		VideoPlayerUtils.setMarkers();		
		
		VideoPlayerCore.playerCurrentFrame = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * getFPS() + Integer.parseInt(caseOutF.getText()) - 1;
	
		//NTSC framerate
		VideoPlayerCore.playerCurrentFrame = Timecode.getNTSCtimecode(VideoPlayerCore.playerCurrentFrame);
		VideoPlayerCore.playerCurrentFrame = Timecode.getDropFrameTimecode(VideoPlayerCore.playerCurrentFrame);
	
		if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false && VideoPlayerCore.activeSegmentIndex != -1)
		{
			for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
		    {
				if (seg.index == VideoPlayerCore.activeSegmentIndex)
				{
					seg.outH = Integer.parseInt(caseOutH.getText());
					seg.outM = Integer.parseInt(caseOutM.getText());
					seg.outS = Integer.parseInt(caseOutS.getText());
					seg.outF = Integer.parseInt(caseOutF.getText());
					break;
				}
			}
		}
	
		VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
	
		//FileList
		setFileList();
	}

	public static void setMarkers() {
			
		try {
			
			playerMarkIn = VideoPlayerUtils.calculateMarkPosition(
		        Integer.parseInt(caseInH.getText()), 
		        Integer.parseInt(caseInM.getText()), 
		        Integer.parseInt(caseInS.getText()), 
		        Integer.parseInt(caseInF.getText())
		    );
			
			playerMarkOut = VideoPlayerUtils.calculateMarkPosition(
		        Integer.parseInt(caseOutH.getText()), 
		        Integer.parseInt(caseOutM.getText()), 
		        Integer.parseInt(caseOutS.getText()), 
		        Integer.parseInt(caseOutF.getText())
		    );
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				for (CutSegment seg : VideoPlayerMultiCuts.cutSegments) 
			    {
					seg.inMark = VideoPlayerUtils.calculateMarkPosition(seg.inH, seg.inM, seg.inS, seg.inF);
			        seg.outMark = VideoPlayerUtils.calculateMarkPosition(seg.outH, seg.outM, seg.outS, seg.outF);
				}
			}
			
			if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
			{
				double time = VideoPlayerCore.bufferCurrentFrame > 0 ? VideoPlayerCore.bufferCurrentFrame : VideoPlayerCore.playerCurrentFrame;
				
				CutSegment previousSegment = VideoPlayerCore.activeSegmentIndex > 0 ? VideoPlayerMultiCuts.cutSegments.get(VideoPlayerCore.activeSegmentIndex - 1) : null;
				CutSegment activeSegment = VideoPlayerMultiCuts.cutSegments.get(VideoPlayerCore.activeSegmentIndex);				
				CutSegment nextSegment = VideoPlayerCore.activeSegmentIndex < VideoPlayerMultiCuts.cutSegments.size() - 1 ? VideoPlayerMultiCuts.cutSegments.get(VideoPlayerCore.activeSegmentIndex + 1) : null;
				
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
					else if (VideoPlayerCore.dragSegmentIndex == VideoPlayerCore.activeSegmentIndex) //Only change values of the current segment
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
					else if (VideoPlayerCore.dragSegmentIndex == VideoPlayerCore.activeSegmentIndex) //Only change values of the current segment
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
			VideoPlayerCore.playerSetTime(totalFrames);
			sliderChange = false;    		
		}
		
		if (caseInternalTc.isSelected())
			inputTime += offset;
		
		if (VideoPlayerCore.playerVideo != null && inputTime - offset < totalFrames)
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
					if (VideoPlayerCore.playerCurrentFrame <= 1)
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

}
