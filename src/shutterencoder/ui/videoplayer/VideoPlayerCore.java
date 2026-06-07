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
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.VideoEncoders;
import shutterencoder.functions.settings.AdvancedFeatures;
import shutterencoder.functions.settings.AudioSettings;
import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.functions.settings.Corrections;
import shutterencoder.functions.settings.FunctionUtils;
import shutterencoder.functions.settings.ImageSequence;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.settings.Transitions;
import shutterencoder.library.DCRAW;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.NCNN;
import shutterencoder.library.XPDFREADER;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.subtitling.SubtitlesTimeline;
import shutterencoder.utils.Utils;

public class VideoPlayerCore extends VideoPlayerUI {

	//Player
	public static Process playerVideo;
    public static Process bufferVideo;
    public static Process playerAudio;	
    public static String videoPath = null;
    public static Thread loadMedia;
    public static Thread setTime;
	public static double playerCurrentFrame = 0;
	public static double bufferCurrentFrame = 0;
	private static BufferedInputStream videoInputStream;
    private static InputStream audio = null;	
    private static AudioInputStream audioInputStream = null;
    public static Mixer audioHardwareOutput;
    private static SourceDataLine line;
    public static FloatControl gainControl;
    private static double offsetVideo = 0f;
    private static double offsetAudio = 0f;
    public static Thread playerThread;
    private static Thread playerAudiothread;
    private static boolean closeAudioStream = false;
    public static ArrayList<Image> bufferedFrames = new ArrayList<Image>();
    public static int maxBufferedFrames = 500;
    public static BufferedImage frameVideo;
    public static BufferedImage fullSizeWatermark;
        
    private static long lastEvTime = 0;
    private static String freezeFrame = "";
        
    //Waveform
    public static Thread addWaveform = new Thread();
  	public static boolean addWaveformIsRunning = false;
  	public static BufferedImage waveform = null;  	
  		
	//Preview
	public static BufferedImage preview = null;
	public static Thread runProcess = new Thread();
	
	//FileList
	public static StringBuilder fileList = new StringBuilder();
	
	public static void playerProcess(double inputTime) {

		if (Utils.loadEncFile != null && Utils.loadEncFile.isAlive())
			return;
		
		try {	
			
			//VIDEO STREAM
			if (System.getProperty("os.name").contains("Windows"))
			{					
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();
			}	
			else
			{
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo));
				playerVideo = pbv.start();	
			}		
			
			InputStream video = playerVideo.getInputStream();				
			videoInputStream = new BufferedInputStream(video);
			
			//AUDIO STREAM
			if ((casePlaySound.isSelected() && (mouseIsPressed == false || FFPROBE.audioOnly)) || mouseIsPressed == false)						       
			{					
				if (System.getProperty("os.name").contains("Windows"))
				{							
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, false));	
					playerAudio = pba.start();					
				}	
				else
				{
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, false));	
					playerAudio = pba.start();					
				}

				//Avoid a crashing issue
				try {
							
					audio = playerAudio.getInputStream();	
					audioInputStream = null;
					audioInputStream = AudioSystem.getAudioInputStream(audio);		    
				    AudioFormat audioFormat = audioInputStream.getFormat();
			        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
			        
			        line = (SourceDataLine) AudioSystem.getLine(info);
			        if (audioHardwareOutput != null)
			        {
			        	line = (SourceDataLine) audioHardwareOutput.getLine(info);
			        }			        	
			        
		            line.open(audioFormat);
		            gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		            
		            float gain = (float) sliderVolume.getValue() / 100;   
		            float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
			        gainControl.setValue(dB);
			        			        
		            line.start();	
					
				} catch (Exception e) {}
			}
								
			//Video thread
			playerThread = new Thread(new Runnable() {
				
				@Override
				public void run() {																			
					
					do {
												
						long startTime = System.nanoTime() + (int) ((double) inputFramerateMS * 1000000);
						
						if (playerLoop)
						{			
							try {
												 				        		
					    		//Read 1 video frame	
								if (playerCurrentFrame >= offsetVideo || Shutter.caseAudioOffset.isSelected() == false)
								{		
									if (Shutter.inputDeviceIsRunning)
									{
										readFrame(videoInputStream, FFPROBE.imageWidth, FFPROBE.imageHeight, false, false);	
									}
									else
									{
										if (Shutter.windowDrag && fullscreenPlayer == false)
										{
											readFrame(videoInputStream, frameVideo.getWidth(), frameVideo.getHeight(), false, false);
										}
										else
											readFrame(videoInputStream, player.getWidth(), player.getHeight(), false, false);															
									}
									
									playerRepaint();
							    	fps ++;	
								}

								if (playerIsPlaying())
								{
									updateCurrentFrame();
								}
								else
									playerCurrentFrame = inputTime;															
															
							} catch (Exception e) {}
							finally {

								if (frameControl && Shutter.inputDeviceIsRunning == false)
								{
									playerLoop = false;
									getTimePoint(playerCurrentFrame);
								}
								else if (playerPlayVideo)
								{										
					            	long delay = startTime - System.nanoTime();
					            						                			
					            	if (delay > 0)
					            	{							            		
					            		//Because the next loop is very cpu intensive but accurate, this sleep reduce the cpu usage by waiting just less than needed
						            	try {
						            		Thread.sleep((int) (delay / 1500000));
										} catch (InterruptedException e) {}

						            	delay = startTime - System.nanoTime();
						            	
						            	long time = System.nanoTime();
						            	while (System.nanoTime() - time < delay) {}		
					                }
								}								
								
								frameIsComplete = true;		
							}
						}   
						else
						{																							
							//IMPORTANT reduce CPU usage
							do {
								try {
								Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLoop == false && playerVideo.isAlive());
						}
					} while (playerVideo.isAlive());
					
					try {
						video.close();
					} catch (IOException e) {}		
					try {
						videoInputStream.close();
					} catch (IOException e) {}
					
					if (audio != null && audioInputStream != null && closeAudioStream)	       
					{						
						try {
							audio.close();
						} catch (IOException e) {}
						try {
							audioInputStream.close();
						} catch (IOException e) {}
						line.flush();
					}
				}
				
			});
			playerThread.setPriority(Thread.MAX_PRIORITY);
			playerThread.start();	
			
			//Audio thread
			playerAudiothread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					byte buffer[] = new byte[4096]; //(int) Math.ceil(48000*2/FFPROBE.accurateFPS)
		            int bytesRead = 0;

		            boolean forceLoop = frameControl; //Allow to read only 1 frame
		            boolean inputAudioStreamIsDone = false;
		            		         
		            //Replace audio offset		    		
					if (Shutter.caseAudioOffset.isSelected())
					{
						offsetVideo = (long) inputTime - Integer.parseInt(Shutter.txtAudioOffset.getText());
						offsetAudio = (long) inputTime + Integer.parseInt(Shutter.txtAudioOffset.getText());
					}	
					
					double inputVideoFrameToSeconds = (double) inputTime / FFPROBE.accurateFPS;
						
					do {
						
						if (playerLoop && (forceLoop || playerIsPlaying()))
						{		
							if (playerIsPlaying())
							{
								long time = System.currentTimeMillis();
								
								while (frameIsComplete == false)
								{	
									try {
										Thread.sleep(1);
									} catch (InterruptedException e) {}
									
									if (frameVideo == null || System.currentTimeMillis() - time > 5000)
									{
										frameIsComplete = true;
									}						
								}
							}
							
							//Audio volume	
							if (audioInputStream != null && audioSetTimeIsRunning == false && ((casePlaySound.isSelected() || playerIsPlaying()) && (mouseIsPressed == false || FFPROBE.audioOnly)))					       
							{										
								closeAudioStream = true;
		
								///Read 1 audio frame
								if (playerCurrentFrame >= offsetAudio)
								{
									if (inputAudioStreamIsDone == false)
									{
										try {
											
											bytesRead = audioInputStream.read(buffer, 0, buffer.length);
											
											if (playerIsPlaying() || inputTime > 0)
												line.write(buffer, 0, bytesRead);
							        		
											if (playerPlayVideo && FFPROBE.audioOnly == false)
											{
												if (audioSetTimeIsRunning)
													inputVideoFrameToSeconds = (double) playerCurrentFrame / FFPROBE.accurateFPS - (double) line.getLongFramePosition() / 48000;
												
												double videoClock = (double) ((double) playerCurrentFrame / FFPROBE.accurateFPS) * 1000;
												double audioClock = (double) ((double) line.getLongFramePosition() / 48000 + inputVideoFrameToSeconds) * 1000;
												double delay = (audioClock - videoClock);
																							
												if (delay >= 50) //When the unsync is more than 50ms
												{	
									            	try {
														Thread.sleep(Math.round(delay));
													} catch (InterruptedException e) {}	
												}
											}
							        		
										} catch (Exception e) {
											
											if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio"))
											&& Shutter.comboFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("longest"))) //When the audio is empty
											{	
												inputAudioStreamIsDone = true;
											}											
										}
									}
								}
							}
							else
								closeAudioStream = false;	
														
							forceLoop = false;
						}
						else
						{									
							if (line != null && closeAudioStream && sliderChange == false && frameControl == false)		       
							{
								line.flush();	
							}
														
							//IMPORTANT reduce CPU usage
							do {
								try {
								Thread.sleep(1);
								} catch (InterruptedException e) {}
							} while (playerLoop == false && playerVideo.isAlive());
						}
												
					} while (playerThread.isAlive());	
				}
				
			});
			playerAudiothread.setPriority(Thread.MAX_PRIORITY);
			playerAudiothread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static Dimension getDimension(int width, int height, int value) {
		
		if (comboPlayerQuality.isVisible())
		{
		    switch (value)
		    {
		        case 1: // 1:2
		            width = (width / 2);
		            width = width - width % 4;
		            height = (height / 2);
		            height = height - height % 4;
		            break;
		        case 2: // 1:4
		            width = (width / 4);
		            width = width - width % 4;
		            height = (height / 4);
		            height = height - height % 4;
		            break;
		        case 3: // auto
		        	if (sliderChange)
		        	{
		        		width = (width / 4);
			            width = width - width % 4;
			            height = (height / 4);
			            height = height - height % 4;
			            break;
		        	}
		    }
		}
	    
	    return new Dimension(width, height);
	}
	
	private static byte[] yuv = new byte[0];
	
	public synchronized static void readFrame(BufferedInputStream is, int width, int height, boolean RGB, boolean isBuffering) throws IOException {
		
		if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source"))
		&& Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
		{
			int w = width;
			int h = height;			
			
			width = h;
			height = w;
		}
		
		//Reduce quality
		if (Shutter.windowDrag == false && isBuffering == false && RGB == false)
		{
			Dimension dim = getDimension(width, height, comboPlayerQuality.getSelectedIndex());
			width = dim.width;
			height = dim.height;
		}

		//MJPEG compression
		if (comboPlayerQuality.isVisible() && comboPlayerQuality.getSelectedItem().equals("auto") && FFPROBE.hasAlpha == false && RGB == false && Settings.btnPreviewOutput.isSelected() == false)
		{
			// Find SOI (FF D8)
		    while (true) {
		        int b = is.read();
		        if (b == -1) return;
		        if (b == 0xFF) {
		            int b2 = is.read();
		            if (b2 == -1) return;
		            if (b2 == 0xD8) break; // found SOI
		        }
		    }

		    // Read JPEG segments until EOI (FF D9)
		    ByteArrayOutputStream buf = new ByteArrayOutputStream(width * height);
		    buf.write(0xFF);
		    buf.write(0xD8);

		    int prev = -1, curr;
		    while ((curr = is.read()) != -1) {
		        buf.write(curr);
		        if (prev == 0xFF && curr == 0xD9) break; // found EOI
		        prev = curr;
		    }

		    frameVideo = ImageIO.read(new ByteArrayInputStream(buf.toByteArray()));
		}
		else if (FFPROBE.hasAlpha)
		{
		    int frameSize = width * height * 4; // RGBA

		    if (frameVideo == null || frameVideo.getWidth() != width || frameVideo.getHeight() != height)
		    {
		        frameVideo = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		    }
		    byte[] abgr = ((DataBufferByte) frameVideo.getRaster().getDataBuffer()).getData();

		    int read = is.readNBytes(abgr, 0, frameSize);
		    if (read != frameSize)
		    {
		        frameVideo = null;
		    }
		    else
		    {
		        // RGBA -> ABGR reorder
		        for (int i = 0; i < frameSize; i += 4) {
		            byte r = abgr[i];
		            byte g = abgr[i + 1];
		            byte b = abgr[i + 2];
		            byte a = abgr[i + 3];
		            abgr[i]     = a;
		            abgr[i + 1] = b;
		            abgr[i + 2] = g;
		            abgr[i + 3] = r;
		        }
		    }
		}
		else if (RGB)
		{
			int frameSize = width * height * 3;

		    if (frameVideo == null || frameVideo.getWidth() != width || frameVideo.getHeight() != height
		    || frameVideo.getType() != BufferedImage.TYPE_3BYTE_BGR)
		    {
		        frameVideo = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		    }

		    byte[] bgr = ((DataBufferByte) frameVideo.getRaster().getDataBuffer()).getData();

		    int read = is.readNBytes(bgr, 0, frameSize);
		    if (read != frameSize)
		    {
		        frameVideo = null;
		    }
		    else
		    {
		        // Swap R and B only (G is already in the right place)
		        for (int i = 0; i < frameSize; i += 3) {
		            byte tmp = bgr[i];
		            bgr[i]     = bgr[i + 2]; // B <- R
		            bgr[i + 2] = tmp;        // R <- B
		        }
		    }
		}
		else
		{
	        // YUV420p path — TYPE_INT_BGR for fastest pixel writes
	        int frameSize = width * height * 3 / 2;

	        // Reuse yuv buffer to avoid per-frame allocation
	        if (yuv.length != frameSize)
	            yuv = new byte[frameSize];

	        int read = is.readNBytes(yuv, 0, frameSize);
	        if (read != frameSize) {
	            frameVideo = null;
	            return;
	        }

	        if (frameVideo == null || frameVideo.getWidth() != width || frameVideo.getHeight() != height
	        || frameVideo.getType() != BufferedImage.TYPE_INT_RGB)
	        {
	        	frameVideo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        }

	        final int[] pixels = ((DataBufferInt) frameVideo.getRaster().getDataBuffer()).getData();
	        final int frameSizeY = width * height;
	        final int uIndex = frameSizeY;
	        final int vIndex = frameSizeY + frameSizeY / 4;
	        final int w = width;
	        final int h = height;
	        final byte[] yuvRef = yuv;

	        // Parallel rows
	        IntStream.range(0, h).parallel().forEach(y ->
	        {
	            int yRowBase = y * w;
	            int uvRowBase = (y >> 1) * (w >> 1);

	            for (int x = 0; x < w; x += 2) // step by 2
	            {
	                int U = yuvRef[uIndex + uvRowBase + (x >> 1)] & 0xFF;
	                int V = yuvRef[vIndex + uvRowBase + (x >> 1)] & 0xFF;

	                int D = U - 128;
	                int E = V - 128;

	                // Precompute shared UV terms (same for both pixels in the pair)
	                int chromaR = 409 * E + 128;
	                int chromaG = -100 * D - 208 * E + 128;
	                int chromaB = 516 * D + 128;

	                // Pixel 1
	                int C1 = (yuvRef[yRowBase + x] & 0xFF) - 16;
	                int base1 = 298 * C1;
	                int R1 = clamp((base1 + chromaR) >> 8);
	                int G1 = clamp((base1 + chromaG) >> 8);
	                int B1 = clamp((base1 + chromaB) >> 8);
	                pixels[yRowBase + x] = (R1 << 16) | (G1 << 8) | B1;

	                // Pixel 2 (reuses same U/V chroma)
	                int C2 = (yuvRef[yRowBase + x + 1] & 0xFF) - 16;
	                int base2 = 298 * C2;
	                int R2 = clamp((base2 + chromaR) >> 8);
	                int G2 = clamp((base2 + chromaG) >> 8);
	                int B2 = clamp((base2 + chromaB) >> 8);
	                pixels[yRowBase + x + 1] = (R2 << 16) | (G2 << 8) | B2;
	            }
	        });
	    }	
	}

	private static int clamp(int val) {
	    return (val < 0) ? 0 : ((val > 255) ? 255 : val);
	}
	
	public static BufferedImage cloneBufferedImage(BufferedImage source) throws IOException {
	    ColorModel cm = source.getColorModel();
	    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	    WritableRaster raster = source.copyData(null);
	    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private static void playerPlayAudioOnly(double inputTime) {

		if (casePlaySound.isSelected() && FFPROBE.hasAudio && mouseIsPressed == false)
		{		
			if (line != null)
				line.flush();
			
			try {	
				
				Process playerAudio;
				
				//AUDIO STREAM
				if (System.getProperty("os.name").contains("Windows"))
				{						
					ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, true));	
					playerAudio = pba.start();
				}	
				else
				{		
					ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, true));	
					playerAudio = pba.start();
				}			
					
				InputStream audio = playerAudio.getInputStream();							
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audio);		    
			    AudioFormat audioFormat = audioInputStream.getFormat();
		        DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		        
		        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		        if (audioHardwareOutput != null)
		        {
		        	line = (SourceDataLine) audioHardwareOutput.getLine(info);
		        }
		        
	            line.open(audioFormat);
	            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
	            line.start();	
	            
	            byte bytes[] = new byte[4096];
	            int bytesRead = 0;
	            
				float gain = (float) sliderVolume.getValue() / 100;   
				float dB = (float) ((float) (Math.log(gain) / Math.log(10.0) * 20.0) + ((float) sliderVolume.getValue() / ((float) 100 / 6)));
		        
		        gainControl.setValue(dB);		        
		        bytesRead = audioInputStream.read(bytes, 0, bytes.length);
        		line.write(bytes, 0, bytesRead);

				try {
					audio.close();
				} catch (IOException e) {}
				try {
					audioInputStream.close();
				} catch (IOException e) {}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public static void playerPlay() {

		if (playerVideo == null || playerVideo.isAlive() == false)		
		{		
			playerProcess(playerCurrentFrame);							
		}		
	}
	
	public static void playerStop() {
		
		playerLoop = false;
				
		if (playerVideo != null)
		{
			playerVideo.destroy();
			try {
				playerThread.interrupt();
			} catch(Exception e) {}
		}
		
		if (playerAudio != null)
		{
			playerAudio.destroy();	
			try {
				playerAudiothread.interrupt();
			} catch(Exception e) {}
		}
	}

	public static void playerRepaint() {
				
		if (frameVideo != null)
		{			  
		    long time = System.currentTimeMillis();
		    
		    if ((time - lastEvTime) >= screenRefreshRate) //Vsync
		    {			    	
		    	lastEvTime = time;		      
		    	player.repaint();
		    	getTimePoint(playerCurrentFrame); 
		    }	
		}			
	}
	
	public static boolean playerIsPlaying() {

		if (btnPlay.getName().equals("pause"))
		{
			return true;
		}
		
		return false;
	}
	
	public static void playerSetTime(double inputTime) {
					
		if ((setTime == null || setTime.isAlive() == false)
		&& (frameVideo != null || playerCurrentFrame > 0)
		&& playerThread != null && Shutter.doNotLoadImage == false && inputTime < totalFrames && videoPath != null)
		{			
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {					
					
					previewUpscale = false;
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					double t = Math.floor(inputTime);
					
					if (t < 0)
						t = 0;
					
					boolean useBuffer = false;
					if (preview != null || Shutter.caseAddSubtitles.isSelected())
					{
						preview = null;
					}					
					else if (FFPROBE.audioOnly == false && (mouseIsPressed || frameControl) && playerIsPlaying() == false && playerCurrentFrame != t && freezeFrame == "")
					{
						useBuffer = true;
					}			
					
					//Forward value
					int framesToSkip = (int) (t - playerCurrentFrame);
					
					//Backward value
					int framesToSkipBackward = (int) (playerCurrentFrame - bufferCurrentFrame);
					if (mouseIsPressed)
					{
						framesToSkipBackward = (int) (playerCurrentFrame - t);
					}
					
					//Allows to only use read buffered frames forward
					boolean readNextFrame = false;
					if ((t - framesToSkipBackward > bufferCurrentFrame && frameControl) || (t >= bufferCurrentFrame && mouseIsPressed))
					{
						readNextFrame = true;
					}
					
					//Read buffered frames if they exists
					if (bufferedFrames.size() > 0 && bufferCurrentFrame < playerCurrentFrame && framesToSkipBackward > 0 && readNextFrame && useBuffer)
					{
						//System.out.println("Read buffered frames");

						int framesToRemove = (int) (mouseIsPressed ? playerCurrentFrame - t : framesToSkipBackward);						
						frameVideo = (BufferedImage) bufferedFrames.get((int) (bufferedFrames.size() - framesToRemove));	
						
						bufferCurrentFrame = mouseIsPressed ? t + 1 : t - framesToRemove;
						
						//Read 1 audio frame
						playerPlayAudioOnly(bufferCurrentFrame);
						
						getTimePoint(bufferCurrentFrame); 						
						player.repaint();
					}					
					else if (framesToSkip < 60 && framesToSkip >= 0 && useBuffer) //Read forward is faster until 60 frames than recreating the process
					{
						try {
							
							//IMPORTANT avoid to display the last read frame when then buffer is filled again
							if (frameVideo != null)
					            frameVideo = cloneBufferedImage(frameVideo);					       
							
							//Add the current frame displayed to the buffer					
							if (bufferedFrames.size() == 0)
								bufferedFrames.add(cloneBufferedImage(frameVideo));
													
							int i = 0;
							do {
								
								i ++;
								
								readFrame(videoInputStream, frameVideo.getWidth(), frameVideo.getHeight(), false, true);
								playerCurrentFrame += 1;
								
								//Limit the buffer size into memory								
								if (bufferedFrames.size() > maxBufferedFrames) 
								{
									bufferedFrames.remove(0);
								}
								
								//Add the frame to the buffer
								bufferedFrames.add(cloneBufferedImage(frameVideo));
								
							} while (i < framesToSkip);
							
							bufferCurrentFrame = playerCurrentFrame;
											
							getTimePoint(bufferCurrentFrame);
							
							//Read 1 audio frame
							playerPlayAudioOnly(bufferCurrentFrame);
							
							player.repaint();							
							waveformContainer.repaint();
							
						} catch (Exception er) {							
							//System.out.println("CLEARED");
							bufferedFrames.clear();
							bufferCurrentFrame = 0;
							waveformContainer.repaint();
						}
					}
					else if (bufferedFrames.size() > 1 && framesToSkipBackward < bufferedFrames.size() - 1 && framesToSkip < 0 && useBuffer) //Read available buffered frames backward
					{	
						if (t < bufferCurrentFrame - 1 || mouseIsPressed == false)
						{
							//System.out.println("Read buffered frames backward");
											
							if (mouseIsPressed == false)
							{
								framesToSkipBackward += 2;
							}
							
							frameVideo = (BufferedImage) bufferedFrames.get((int) (bufferedFrames.size() - framesToSkipBackward));	
							bufferCurrentFrame = playerCurrentFrame - framesToSkipBackward + 1;
															
							//Read 1 audio frame
							playerPlayAudioOnly(bufferCurrentFrame);
							
							getTimePoint(bufferCurrentFrame); 						
							player.repaint();
						}
					}
					else if (framesToSkip != 0 || (framesToSkip == 0 && mouseIsPressed == false)) //Do not use if there is no time difference and user is currently scrolling
					{												
						//Clear the buffer
						if (bufferedFrames.size() > 0 && playerCurrentFrame != t && (framesToSkip >= 60 || 0 - framesToSkip >= bufferedFrames.size() || useBuffer == false))
						{		
							//System.out.println("CLEARED");
							bufferedFrames.clear();
							bufferCurrentFrame = 0;
							waveformContainer.repaint();
							
							//IMPORTANT
							t += 1;
						}
						else
						{							
							//System.out.println("Set Time");
							
							//Remove all buffered frames after the playerCurrentFrame
							if (bufferedFrames.size() > 0)
							{
								int d = frameControl ? 1 : 0;
								int framesToRemove = (int) (playerCurrentFrame - bufferCurrentFrame) + d;								
								for (int i = 0 ; i < framesToRemove ; i++)
								{
									bufferedFrames.remove(bufferedFrames.size() - 1);
								}
								
								t = bufferCurrentFrame - d;
								
								if (bufferedFrames.size() == 0)
									bufferCurrentFrame = 0;
							}
						}
						
						VideoPlayerOverlay.writeCurrentSubs(t, false);
						
						playerPlayVideo = false;
						
						boolean playback;
						if (playerIsPlaying())
						{
							playback = true;
						}
						else
							playback = false;
												
						playerStop();
						do {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						} while (playerThread.isAlive());				
						
						frameControl = true; //IMPORTANT to stop the player loop
						frameIsComplete = false;
						playerLoop = true;
						playerProcess(t);
						
						long time = System.currentTimeMillis();
												
						do {

							//IMPORTANT
							playerLoop = true;
							
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
							
							if (System.currentTimeMillis() - time > 5000)
							{
								frameIsComplete = true;
							}
							
						} while (frameIsComplete == false);	
													
						if (playback && mouseIsPressed == false)
						{						
							playerLoop = true;
						}
						else
							playerLoop = false;
						
						getTimePoint(t); 
						Shutter.timecode.repaint();
						
						frameControl = false;
						playerPlayVideo = true;	
					}					
					
					if (bufferedFrames.size() > 0)
					{
						cursorCurrentFrame.setLocation((int) Math.floor((double) (waveformContainer.getWidth() * Timecode.setNTSCtimecode(bufferCurrentFrame)) / totalFrames), 0);
					}
					else
						cursorCurrentFrame.setLocation((int) Math.floor((double) (waveformContainer.getWidth() * Timecode.setNTSCtimecode(playerCurrentFrame)) / totalFrames), 0);
					
					frameControl = false;
					
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					Shutter.windowDrag = false;
				}
				
			});
			setTime.start();			
		}			
	}

	public static void playerAudioSetTime(double inputTime) {
		
		if (playerAudio != null)
		{			
			playerAudio.destroy();
			
			audioSetTimeIsRunning = true;
			
			if (line != null)
				line.flush();
						
			try {

				if ((casePlaySound.isSelected() && (mouseIsPressed == false || FFPROBE.audioOnly)) || mouseIsPressed == false)						       
				{	
					//AUDIO STREAM
					if (System.getProperty("os.name").contains("Windows"))
					{							
						ProcessBuilder pba = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + setAudioCommand(inputTime, false));	
						playerAudio = pba.start();					
					}	
					else
					{
						ProcessBuilder pba = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + setAudioCommand(inputTime, false));	
						playerAudio = pba.start();					
					}
					
					//Avoid a crashing issue
					try {
						audio = playerAudio.getInputStream();	
						audioInputStream = null;
						audioInputStream = AudioSystem.getAudioInputStream(audio);		    
					} catch (Exception e) {}	
				}
				
				if (playerIsPlaying())
					playerLoop = true;
				
			} catch (Exception e) {
				playerAudio.destroy();					
			}
			
			audioSetTimeIsRunning = false;
		}	
	}
		
	public static void playerFreeze() {
							
		if ((setTime == null || setTime.isAlive() == false) && (playerVideo == null || playerVideo.isAlive() == false))
		{		
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {		
					
					frameVideo = null;
					
					playerPlayVideo = false;
					
					VideoPlayerOverlay.writeCurrentSubs(0, false);
					
					if (playerThread != null)
					{						
						playerStop();
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
						} while (playerThread.isAlive());	
					}
										
					frameControl = true; //IMPORTANT to stop the player loop
					frameIsComplete = false;						
					playerLoop = true;
					playerProcess(playerCurrentFrame);							
												
					long time = System.currentTimeMillis();
					
					do {

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
						
						if (System.currentTimeMillis() - time > 5000)
							frameIsComplete = true;
						
					} while (frameIsComplete == false);
											
					if (playerCurrentFrame > 0)
						playerCurrentFrame -= 1;

					getTimePoint(playerCurrentFrame); 

					frameControl = false;
					playerPlayVideo = true;	
				}
			});
			setTime.start();
		}			
	}
		
	public static String setVideoCommand(double inputTime, int width, int height, boolean isPlaying) throws InterruptedException {
				
		if (FFPROBE.audioOnly)
		{			
			//Important
			FFPROBE.accurateFPS = 25.0f;
			
			String filter = "";
			
			if (caseVuMeter.isSelected())
			{		
				String aspeed = "";
				
				if (sliderSpeed.getValue() != 2)
				{
					if (sliderSpeed.getValue() != 0)
					{
						aspeed += "atempo=" + ((double) sliderSpeed.getValue() / 2) + ",";
					}
					else
						aspeed += "atempo=0.5,atempo=0.5,";				
				}	
				
				String channels = "";
				String audioOutput = "";
				int i;
				for (i = 0; i < FFPROBE.channels; i++) {
					channels += "[0:a:" + i + "]" + aspeed + "showvolume=f=0:w=" + width + ":h=" + (int) Math.round(height / 30) + ":b=4:s=0[a" + i + "];";
					audioOutput += "[a" + i + "]";
				}
				
				if (FFPROBE.channels > 1)
				{
					audioOutput += "vstack=" + i + "[volume];";
					filter = " -filter_complex " + '"' + channels + audioOutput + "[1:v][volume]overlay=W*0.5-w*0.5:H*0.5-h*0.5" + '"';
				}
				else
				{
					audioOutput = audioOutput.replace("[a0]", "");
					filter = " -filter_complex " + '"' + channels + audioOutput + "[1:v][a0]overlay=W*0.5-w*0.5:H*0.5-h*0.5" + '"';
				}
			}
			
			return " -v quiet -hide_banner -ss " + (long) ((double) inputTime * inputFramerateMS) + "ms -i " + '"' + videoPath + '"' + " -f lavfi -i " + '"' + "color=c=black:r=25:s=" + width + "x" + height + '"' + filter + " -c:v rawvideo -pix_fmt yuv420p -an -sn -f rawvideo -";
		}
		else
		{
			String video = videoPath;
			String concat = "";
			
			//Image sequence
			if (Shutter.caseEnableSequence.isSelected())
			{		
				concat = FunctionUtils.setConcat(new File("concat.txt"), Shutter.dirTemp);					
				video = Shutter.dirTemp + "concat.txt";
			}	

			String gpuDecoding = "";			
			if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false && mouseIsPressed == false && previousFrame == false && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
			{
				gpuDecoding = FFMPEG.setGPUDevice(setFilter(false, false));
			}
			
			String extension = videoPath.substring(videoPath.lastIndexOf("."));	
					
			int framesToSkip = (int) ((double) inputTime - playerCurrentFrame);
			
			String decodingOptions = "";
			if (mouseIsPressed && (framesToSkip > 60 || framesToSkip < 0))
			{					
				String format = "";
				if (FFPROBE.videoFormat != null)
					format = " -f " + FFPROBE.videoFormat;
					
				decodingOptions = format + " -nostdin -flags2 +fast -fflags +nobuffer+flush_packets -err_detect ignore_err";
				freezeFrame = " -analyzeduration 0 -probesize 32 -frames:v 1";	
			}
			else
				freezeFrame = "";
			
			//Alpha
			String colorFormat = "yuv420p";
			if (FFPROBE.hasAlpha)
				colorFormat = "rgba";
			
			//Output
			String outputFormat = "rawvideo";
			if (comboPlayerQuality.isVisible() && comboPlayerQuality.getSelectedItem().equals("auto") && FFPROBE.hasAlpha == false && Settings.btnPreviewOutput.isSelected() == false)
			{
				outputFormat  = "mjpeg -q:v 3";
			}

			String cmd = gpuDecoding + Colorimetry.setInputCodec(extension) +" -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -hide_banner" + decodingOptions + " -ss " + (long) ((double) inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + setFilter(false, false) + " -r " + FFPROBE.currentFPS + freezeFrame + " -c:v " + outputFormat + " -pix_fmt " + colorFormat + " -an -sn -f rawvideo -";
			
			String codec = "";
			if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != ""
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("QT Animation") == false
			&& Shutter.comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100") == false)
			{
				String format = "matroska";
				
				if (Shutter.comboFonctions.getSelectedItem().toString().contains("XAVC"))
				{
					format = "mxf";
				}	
				
				//Deinterlacer		
				String deinterlace = AdvancedFeatures.setDeinterlace(true, Settings.btnPreviewOutput.isSelected());		
				if (mouseIsPressed)
				{
					deinterlace = "";
				}
										
				//Deinterlacer
				if (deinterlace != "")
				{
					deinterlace = " -vf " + deinterlace;
				}
				
				String device = "";				
				if (Shutter.comboAccel.getSelectedItem().equals("Vulkan Video")
				|| Shutter.comboGPUDecoding.getSelectedItem().toString().equals("vulkan")
				|| Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan")) //Always need to choose the GPU
				{
					if (FFMPEG.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
					{
						device = " -init_hw_device vulkan=gpu:1";
					}
					else
						device = " -init_hw_device vulkan=gpu:0";
				}	
				else if (Shutter.comboAccel.getSelectedItem().equals("VAAPI"))			
				{
					device = " -vaapi_device /dev/dri/renderD128";
				}
				
				//Hardware encoding
				String hwupload = "";
				switch (Shutter.comboFonctions.getSelectedItem().toString())
				{
					case "H.264":
					case "H.265":
					case "H.266":
					case "AV1":
					case "VP9":
					case "FFV1":
						
						if (Shutter.comboAccel.getSelectedItem().equals(Shutter.language.getProperty("aucune").toLowerCase()) == false
						&& Shutter.comboAccel.getSelectedItem().equals("VAAPI") || Shutter.comboAccel.getSelectedItem().equals("Vulkan Video"))			
						{		
							if (deinterlace != "")
							{
								hwupload = ",format=nv12,hwupload";
							}
							else
								hwupload = " -vf format=nv12,hwupload";
						}
				}
				
				String pixelFormat = "";
				if (Shutter.comboAccel.getSelectedItem().equals(Shutter.language.getProperty("aucune").toLowerCase()) == false)
				{
					pixelFormat = " -pix_fmt yuv420p";
				}	
				
				codec = VideoEncoders.setCodec() + VideoEncoders.setBitrate() + AdvancedFeatures.setPreset() + deinterlace + hwupload + freezeFrame + pixelFormat + " -f " + format + " pipe:1 | ";
				
				if (System.getProperty("os.name").contains("Windows"))
				{	
					codec += '"' + FFMPEG.PathToFFMPEG + '"';
				}
				else
					codec += FFMPEG.PathToFFMPEG;
				
				codec += " -v quiet -hide_banner -i pipe:0" + setFilter(false, true);
								
				cmd = device + Colorimetry.setInputCodec(extension) + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -hide_banner -ss " + (long) ((double) inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + " -r " + FFPROBE.currentFPS +  codec + freezeFrame + " -c:v " + outputFormat + " -pix_fmt " + colorFormat + " -an -sn -f rawvideo -";
			}
									
			if (Shutter.inputDeviceIsRunning)
			{
				cmd = " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -hide_banner " + RecordInputDevice.setInputDevices() + setFilter(false, false) + " -c:v " + outputFormat + " -pix_fmt " + colorFormat + " -an -sn -f rawvideo -";
			}

			//Console.consoleFFMPEG.append(cmd + System.lineSeparator());

			return cmd;			
		}
	}
	
	public static String setAudioCommand(double inputTime, boolean frameByFrame) {
					
		String duration = "";
		if (frameByFrame)
		{
			duration = " -t " + (int) inputFramerateMS + "ms";
		}
		
		if (playTransition)
		{
			playTransition = false;
		}
		
		if (FFPROBE.hasAudio == false && (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) == false || Shutter.list.getSize() == 1))
		{
			return " -v quiet -hide_banner -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"' + setAudioFilter() + duration +  " -vn -c:a pcm_s16le -ar 48k -ac 2 -f wav -";				
		}
		else
		{
			String input = " -i " + '"' + videoPath + '"';
			String mapping = "";
			
			if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionReplaceAudio")) && Shutter.fileList.getSelectedIndex() + 1 < Shutter.list.getSize())
			{
				if (Shutter.list.getElementAt(Shutter.fileList.getSelectedIndex() + 1).contains("lavfi"))
				{
					input =  " -f lavfi -i " + '"' + "anullsrc=channel_layout=stereo:sample_rate=48000" + '"';
				}
				else
				{
					input = " -i " + '"' + Shutter.list.getElementAt(Shutter.fileList.getSelectedIndex() + 1) + '"';
				}
			}
			else
			{	
				if (FFPROBE.channels > 0 && comboAudioTrack.isVisible())
				{
					if (comboAudioTrack.getSelectedItem() != null && comboAudioTrack.getSelectedItem().equals("Mix"))
					{						
						mapping = " -filter_complex amerge=inputs=" + FFPROBE.channels + setAudioFilter().replace(" -filter:a ", ",");
					}
					else
						mapping = " -map a:" + comboAudioTrack.getSelectedIndex() + setAudioFilter();
				}
				else
					mapping = setAudioFilter();
			}

			return " -v quiet -hide_banner -ss " + (long) ((double) inputTime * inputFramerateMS) + "ms" + input + duration + " -vn -c:a pcm_s16le -ar 48k -ac 2" + mapping + " -f wav -";
		}		
		
	}
    
	private static void updateCurrentFrame() {
				
		if (sliderSpeed.getValue() != 2)
		{													
			if (sliderSpeed.getValue() != 0)
			{
				playerCurrentFrame += 1 * ((double) sliderSpeed.getValue() / 2);
			}
			else
				playerCurrentFrame += 1 * 0.25f;
		}
		else
			playerCurrentFrame += 1;
	}
	        
	public static void addWaveform(boolean newWaveform) {
		
		if (caseShowWaveform.isSelected() && FFPROBE.hasAudio && addWaveformIsRunning == false && Shutter.frame.getSize().width > 654 && Settings.btnDisableVideoPlayer.isSelected() == false)
		{			
			addWaveformIsRunning = true;
			
			if (newWaveform || waveform == null)
			{
				Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				waveformIcon.setVisible(false);
				
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
						long size = 2000;

						String start = "";
						String duration = "";
						if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")))
						{	
							do {
								try {
									Thread.sleep(100);
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
								FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]amerge=inputs=" + FFPROBE.channels + ",aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"'  + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
							else
							{
								FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a:" + comboAudioTrack.getSelectedIndex() + "]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
						}
						else
						{
							FFMPEG.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -");  																
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
								
								waveformIcon.setIcon(null);
								waveformContainer.repaint();
								
								SubtitlesTimeline.waveform.setIcon(resizedWaveform);							
								SubtitlesTimeline.waveform.setBounds(SubtitlesTimeline.timelineScrollBar.getValue(), SubtitlesTimeline.waveform.getY(), (int) (SubtitlesTimeline.frame.getWidth() * 10 * SubtitlesTimeline.zoom), SubtitlesTimeline.timeline.getHeight());
								SubtitlesTimeline.waveform.repaint();
							}
							else
							{	    		
								waveformIcon.setSize(waveformContainer.getSize());
								ImageIcon resizedWaveform = new ImageIcon(new ImageIcon(waveform).getImage().getScaledInstance(waveformContainer.getWidth(), waveformContainer.getHeight(), Image.SCALE_AREA_AVERAGING));
								
								waveformIcon.setIcon(resizedWaveform);
								waveformContainer.repaint();

								if ((RenderQueue.frame != null && RenderQueue.frame.isVisible() && FFMPEG.isRunning) || isPiping || videoPath == null)
								{
									waveformIcon.setVisible(false);
								}
								else
									waveformIcon.setVisible(true);
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
		
	public static void loadImage(boolean forceRefresh) {

		if (forceRefresh && videoPath != null)
		{
			Thread waitProcess = new Thread (new Runnable() {
				
				@Override
				public void run() {
										
					while (runProcess.isAlive())
					{
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					}
				}
			});
			waitProcess.start();
		}
		
		if ((forceRefresh || runProcess.isAlive() == false) && videoPath != null && Shutter.list.getSize() >  0 && Shutter.doNotLoadImage == false)
		{				
			runProcess = new Thread (new Runnable() {

			@Override
			public void run() {
												
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
						
						//Alpha
						String colorFormat = "rgb24";
						if (FFPROBE.hasAlpha)
							colorFormat = "rgba";
						
						if (isRaw == false && extension.toLowerCase().equals(".pdf") == false && FFPROBE.interlaced != null && FFPROBE.interlaced.equals("1"))
							deinterlace = " -vf bwdif=0:" + FFPROBE.fieldOrder + ":0";		
	
						//Input point
						String inputPoint = " -ss " + (long) ((double) playerCurrentFrame * inputFramerateMS) + "ms";
						if (fileDuration <= 40 || Shutter.caseEnableSequence.isSelected()) //Image
							inputPoint = "";
						
						String tiles = "";
						int tilesNumber = FFPROBE.gridRows * FFPROBE.gridCols;
						if ((extension.toLowerCase().equals(".heic") || extension.toLowerCase().equals(".heif")) && FFPROBE.gridRows != 0 && FFPROBE.gridCols != 0)
						{
							for (int i = 0 ; i < tilesNumber ; i++)
							{
								tiles += "[0:v:" + i + "]";
							}
							
							String scale = FFPROBE.imageWidth + ":" + FFPROBE.imageHeight + ":0:0";
							if (FFPROBE.isRotated)
							{
								scale = FFPROBE.imageHeight + ":" + FFPROBE.imageWidth + ":0:0,transpose=1";
							}
							
							tiles = " -filter_complex " + '"' + tiles  + "concat=n=" + tilesNumber + ",tile=" + FFPROBE.gridRows + "x" + FFPROBE.gridCols + ",crop=" + scale + '"'; 
						}
				
						//Creating preview file																
						String cmd = deinterlace + tiles + " -frames:v 1 -an -sn -s " + player.getWidth() + "x" + player.getHeight() + " -sws_flags bicubic -y ";	
						if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
						{
							cmd = deinterlace + tiles + " -frames:v 1 -an -sn -s " + player.getHeight() + "x" + player.getWidth() + " -sws_flags bicubic -y ";
						}
						
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
								
								do {
					            	Thread.sleep(10);  
					            } while (FFMPEG.isRunning && FFMPEG.error == false);
								
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
							//Subtitles are visible only from a video file
							if (Shutter.caseAddSubtitles.isSelected())
							{				
								generatePreview(Colorimetry.setInputCodec(extension) + " -v quiet -hide_banner" + inputPoint + " -i " + '"' + videoPath + '"' + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + colorFormat + " -an -sn -f rawvideo -"); 
							}
							else
							{	
								//Input pipe format
								String inputFormat = "bgr24";
								if (FFPROBE.hasAlpha)
									inputFormat = "abgr";
																
								generatePreview(" -v quiet -hide_banner -f rawvideo -pixel_format " + inputFormat +" -video_size " + player.getWidth() + "x" + player.getHeight() + " -i pipe:0" + setFilter(true, true) + " -frames:v 1 -c:v rawvideo -pix_fmt " + colorFormat + " -f rawvideo -");
							}
						}
			        }
				    catch (Exception e)
				    {				
				    	e.printStackTrace();
			 	       	//JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
				    }
			        finally 
			        {	
			        	while (FFMPEG.isRunning)
			        	{
			        		try {
								Thread.sleep(10);
							} catch (InterruptedException e) {}
			        	} 
						
	          			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
	        			else
	        				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
						
			        }
				}
			});
			runProcess.start();
		}
	}

	private static void generatePreview(String cmd) {
		
		try {		
						
			Process process;
			
			if (System.getProperty("os.name").contains("Windows"))
			{							
				ProcessBuilder pbv = new ProcessBuilder("cmd.exe" , "/c", '"' + FFMPEG.PathToFFMPEG + '"' + cmd);
				process = pbv.start();	
			}	
			else
			{
				ProcessBuilder pbv = new ProcessBuilder("/bin/bash", "-c", FFMPEG.PathToFFMPEG + cmd);
				process = pbv.start();	
			}	
						
			//Console.consoleFFMPEG.append(cmd + System.lineSeparator());

			if (preview != null)
			{
		        OutputStream outputStream = process.getOutputStream();
		        
		        byte[] frame = ((DataBufferByte) preview.getRaster().getDataBuffer()).getData();
		        outputStream.write(frame);
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

			readFrame(inputStream, player.getWidth(), player.getHeight(), true, false);
			
			if (preview == null && frameVideo != null && Shutter.caseAddSubtitles.isSelected() == false)
			{				
				preview = cloneBufferedImage(frameVideo);
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
		
	private static String setFilter(boolean noGPU, boolean noDeinterlacing) {
				
		if (Settings.btnPreviewOutput.isSelected() || mouseIsPressed)
		{
			noGPU = true;
		}
		
		//Subtitles
		String background = "" ;
		if (Shutter.caseAddSubtitles.isSelected() && Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt"))
		{			
			//Color	
			if (Shutter.fontSubsColor != null)
			{
				 String c = Integer.toHexString(Shutter.fontSubsColor.getRGB()).substring(2);
				 Shutter.subsHex = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}
			
			if (Shutter.backgroundSubsColor != null)
			{
				 String c = Integer.toHexString(Shutter.backgroundSubsColor.getRGB()).substring(2);
				 Shutter.subsHex2 = c.substring(4, 6) + c.substring(2, 4) + c.substring(0, 2);
			}		
			
			Shutter.subsAlpha = "00";
			Shutter.outline = "1";
			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
			{
				int o = (int) (255 - (double) ((int) Integer.valueOf(Shutter.textSubsOutline.getText()) * 255) / 100);
				Shutter.subsAlpha = Integer.toHexString(o);
			}
			else
			{
				Shutter.outline = String.valueOf((double) ((double) ((int) Integer.valueOf(Shutter.textSubsOutline.getText())) * 2) / 100);
			}
			
			//Fond sous-titres							
			if (Shutter.lblSubsBackground.getText().equals(Shutter.language.getProperty("lblBackgroundOn")))
				background = ",BorderStyle=4,BackColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&,outline=0";
			else
				background = ",outline=" + Shutter.outline + ",outlineColour=&H" + Shutter.subsAlpha + Shutter.subsHex2 + "&";
				
			//Bold
			if (Shutter.btnG.getForeground() != Color.BLACK)
				background += ",Bold=1";
			
			//Italic
			if (Shutter.btnI.getForeground() != Color.BLACK)
				background += ",Italic=1";
		}
		
		//Deinterlacer		
		String deinterlace = "";
		if (noDeinterlacing == false && mouseIsPressed == false)
		{
			deinterlace = AdvancedFeatures.setDeinterlace(true, noGPU);
		}
			
		//Global Filter
		String filter = "";
		if (deinterlace != "")
		{
			filter += deinterlace;
		}	
		
		//Scaling
		int width = player.getWidth();
		int height = player.getHeight();
		
		String bitDepth = "nv12";
		if (FFPROBE.imageDepth == 10)
		{
			bitDepth = "p010";
		}	

		//Crop & Pad
		if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false && Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false && noGPU == false && Shutter.inputDeviceIsRunning == false)
		{				
			filter = shutterencoder.functions.settings.Image.setScale(filter, false, noGPU);
			
			if (filter.contains("scale"))
			{
				filter += shutterencoder.functions.settings.Image.setPad("", false, noGPU);
			}
		}
		else			
		{
			if (Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
			{
				width = player.getHeight();
				height = player.getWidth();		
			}
		}		
		
		//Zoom
		if (Shutter.caseEnableColorimetry.isSelected() && Shutter.sliderZoom.getValue() != 0)
		{	
			filter = Colorimetry.setZoom(filter, false);	
		}
		
		//Reduce quality
		if (preview == null)
		{
			Dimension dim = getDimension(width, height, comboPlayerQuality.getSelectedIndex());
			width  = dim.width;
			height = dim.height;
		}
		
		if (Shutter.grpColorimetry.isVisible() && Shutter.caseColormatrix.isSelected() && Shutter.comboInColormatrix.getSelectedItem().equals("HDR") == false)
		{
			//IMPORTANT scaling must be a multiple of 4!
			width = (width - (width % 4));
			height = (height - (height % 4));}
		
						
		//Stabilisation
		//if (Shutter.stabilisation != "")
		//	setEQ = Shutter.stabilisation;
		
		//Blend
		if (preview == null) //Show only on playing
		{
			filter = ImageSequence.setBlend(filter);
			filter = ImageSequence.setMotionBlur(filter);
		}
		
		//LUTs
		filter = Colorimetry.setLUT(filter);	
		
		//Levels
		filter = Colorimetry.setLevels(filter);
		
		//Colorspace metadata
		filter = Colorimetry.setMetadata(filter);
		
		if (Shutter.caseLevels.isSelected() == false && fileDuration > 40 && FFPROBE.lumaLevel.equals("0-255"))
		{
			if (filter != "") filter += ",";
			{
				if (comboPlayerQuality.isVisible() && comboPlayerQuality.getSelectedItem().equals("auto") && FFPROBE.hasAlpha == false && preview == null && Settings.btnPreviewOutput.isSelected() == false)
				{
					filter += "scale=in_range=limited:out_range=full";
				}
				else
					filter += "scale=in_range=full:out_range=full";
			}
		}
		
		//Colormatrix
		filter = Colorimetry.setColormatrix(filter);
		
		//Rotate
		if (Shutter.caseRotate.isSelected() || Shutter.caseMiror.isSelected())
		{
			filter = shutterencoder.functions.settings.Image.setRotate(filter, noGPU);
		}
		
		//Colorimetry
		if (Shutter.caseEnableColorimetry.isSelected())
		{			
			String color = Colorimetry.setEQ(false);
						
			if (filter != "" && color != "")
			{
				filter += "," + color;
			}
			else if (color != "")
			{
				filter += color;
			}
			
			if (Shutter.sliderAngle.getValue() != 0)
			{
				if (filter.contains("scale"))
				{
					filter = filter.replace("scale=" + FFPROBE.imageWidth + ":" + FFPROBE.imageHeight,  "scale=" + player.getWidth() + ":" + player.getHeight());
				}
				else
				{
					filter += ",scale=" + player.getWidth() + ":" + player.getHeight();
				}
			}
		}
				
		//Deflicker			
		filter = Corrections.setDeflicker(filter);
			
		//Deband			
		filter = Corrections.setDeband(filter);
				 
		//Details			
		filter = Corrections.setDetails(filter);				
											            	
		//Denoise			
		filter = Corrections.setDenoiser(filter, noGPU);
		
		//Exposure
		if (preview == null) //Show only on playing
			filter = Corrections.setSmoothExposure(filter);	
		
		//Limiter
		filter = Corrections.setLimiter(filter);

		//Fade-in Fade-out
		if (Shutter.caseVideoFadeIn.isSelected() || Shutter.caseVideoFadeOut.isSelected())
		{
			filter = Transitions.setVideoFade(filter, true);
		}
		
		/*
		//Interpolation
		filter = AdvancedFeatures.setInterpolation(filter);
		
		//Slow motion
		filter = AdvancedFeatures.setSlowMotion(filter);
							
        //PTS
		filter = AdvancedFeatures.setPTS(filter);		      		                     	

		//Conform
		filter = AdvancedFeatures.setConform(filter);
		*/

		String algorithm = "bilinear";
		if (mouseIsPressed)
		{
			algorithm = "neighbor";
		}
		
		//Checking if last filter is GPU accelerated
		boolean filterGPU = FunctionUtils.checkPreviousFilter(filter);
		
		if (Shutter.inputDeviceIsRunning)
		{
			filter += "null";
		}
		else if (filterGPU && noGPU == false && FFPROBE.isRotated == false && previousFrame == false
		&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
		&& Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false)
		{
			if (filter != "") filter += ",";
			
			//Auto GPU
			if (FFMPEG.autoCUDA || (FFMPEG.cudaAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("cuda")))
			{			
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_cuda=" + width + ":" + height + ":interp_algo=" + algorithm.replace("neighbor", "nearest").replace("bilinear", "bicubic") + ",hwdownload,format=" + bitDepth;
			}
			else if ((FFMPEG.autoAMF || (FFMPEG.amfAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("amf"))) && deinterlace == "")
			{
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "vpp_amf=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.autoQSV || (FFMPEG.qsvAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("qsv")))
			{		
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_qsv=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}	
			else if ((FFMPEG.autoVIDEOTOOLBOX || (FFMPEG.videotoolboxAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("videotoolbox"))) && deinterlace == "")
			{
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_vt=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}
			else if (FFMPEG.autoVULKAN || (FFMPEG.vulkanAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan")))
			{
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_vulkan=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}
			else
			{
				filter += "scale=" + width + ":" + height + ":sws_flags=" + algorithm + ":sws_dither=none";
			}
		}
		else
		{
			if (filter != "") filter += ",";
			
			filter += "scale=" + width + ":" + height + ":sws_flags=" + algorithm + ":sws_dither=none";		
		}				
		
		//Speed slider
		if (sliderSpeed.getValue() != 2)
		{
			if (sliderSpeed.getValue() != 0)
			{
				filter += ",setpts=" + (double) 1 / ((double) sliderSpeed.getValue() / 2) + "*PTS";
			}
			else
				filter += ",setpts=4*PTS";				
		}
		
		//Add filters
		filter = " -vf " + '"' + filter;	
				
		if (caseVuMeter.isSelected() && FFPROBE.hasAudio && Shutter.caseAddSubtitles.isSelected() == false && preview == null)
		{
			String aspeed = "";
						
			if (sliderSpeed.getValue() != 2)
			{
				if (sliderSpeed.getValue() != 0)
				{
					aspeed += "atempo=" + ((double) sliderSpeed.getValue() / 2) + ",";
				}
				else
					aspeed += "atempo=0.5,atempo=0.5,";				
			}	
			
			String channels = "";
			String audioOutput = "";
			int i = 0;
			for (int a = 0; a < FFPROBE.channels; a++)
			{
				if (FFPROBE.audioCodecs[a].equals("none") == false)
				{
					channels += "[0:a:" + a + "]" + aspeed + "showvolume=f=0:w=" + player.getWidth() + ":h=" + (int) Math.round(player.getHeight() / 90) + ":t=0:b=0:v=0:o=v:s=0:p=0.5[a" + a + "];";
					audioOutput += "[a" + a + "]";
					i++;
				}
			}
			
			if (i > 1)
			{							
				audioOutput += "hstack=" + i + "[volume];";
			}
			else
			{
				audioOutput = audioOutput.replace("[a0]", "");
				channels = channels.replace("[a0]", "[volume]");
			}
			
			filter = " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];" + channels + audioOutput + "[v][volume]overlay=W-w:H-h";
		}	
		
		//Close filter
		filter += '"';	
		
		try {
			
			if (Shutter.caseAddSubtitles.isSelected()
			&& Shutter.subtitlesBurn
			&& Shutter.subtitlesFile.toString().substring(Shutter.subtitlesFile.toString().lastIndexOf(".")).equals(".srt")
			&& Files.size(Shutter.subtitlesFile.toPath()) > 0)
			{						
				caseVuMeter.setEnabled(false);
				
				int subsWidth = (int) ((double) (Integer.parseInt(Shutter.textSubsWidth.getText()) / Shutter.playerRatio));
				int subsPosY = (int) ((double) Integer.parseInt(Shutter.textSubtitlesPosition.getText()) / Shutter.playerRatio);
				
				int playerWidth = player.getWidth();
				int playerHeight = player.getHeight();
				
				if (comboPlayerQuality.isVisible() && comboPlayerQuality.getSelectedIndex()!= 0 && FFPROBE.hasAlpha == false && preview == null && Settings.btnPreviewOutput.isSelected() == false)
				{
					Dimension dim = getDimension(subsWidth, subsPosY, comboPlayerQuality.getSelectedIndex());
					subsWidth = dim.width;
					subsPosY = dim.height;
					
					dim = getDimension(playerWidth, playerHeight, comboPlayerQuality.getSelectedIndex());
					playerWidth = dim.width;
					playerHeight = dim.height;
				}
				
				filter = " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + subsWidth + ":" + playerHeight + "+" + subsPosY
			  			+ ",subtitles='" + Shutter.subtitlesFile.toString() + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Shutter.textSubsSize.getText() + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"'
			  			+ " -filter_complex " + '"' + "[0:v]" + filter.replace(" -vf ", "").replace("\"", "") + "[v];[v][1:v]overlay=x=" + (int) ((playerWidth - subsWidth) / 2) + ",scale=" + playerWidth + ":" + playerHeight + '"';	
			}
			else
			{
				caseVuMeter.setEnabled(true);				
			}
			
		} catch (Exception e) {}
		
		return filter;
	}

	private static String setAudioFilter() {
		
		String filter = "";	
		
		//EQ
		filter = AudioSettings.setEQ(filter);
		
		if (sliderSpeed.getValue() != 2)
		{
			if (filter != "") filter += ",";
			
			if (sliderSpeed.getValue() != 0)
				filter += "atempo=" + (double) sliderSpeed.getValue() / 2;
			else
				filter += "atempo=0.5,atempo=0.5";
		}
				
		if (Shutter.caseAudioFadeIn.isSelected() || Shutter.caseAudioFadeOut.isSelected())
		{
			if (filter != "") filter += ",";	
			
			filter += Transitions.setAudioFadeIn(true);
			
			if (Transitions.setAudioFadeIn(true) != "" && Transitions.setAudioFadeOut(true) != "")
			{
				filter += ",";
			}
			
			filter += Transitions.setAudioFadeOut(true);
		
		}
		
		if (filter != "")
		{
			filter = " -filter:a " + filter;
		}

		return filter;
	}
	
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
						if (Shutter.fileList.getSelectedValue().equals(videoPath) == false && (new File(Shutter.fileList.getSelectedValue()).isFile() || Shutter.scanIsRunning))
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
								videoPath = Shutter.fileList.getSelectedValue();
							
							if (frameVideo != null)
								frameVideo = null;
							
							if (preview != null)
								preview = null;
							
							if (waveform != null)
							{
								waveform = null;
								waveformIcon.setIcon(null);
								waveformIcon.repaint();
							}
							
							waveformZoom = 1;
												
							if (addWaveformIsRunning && FFMPEG.waveformWriter != null)
							{
								try {
									FFMPEG.waveformWriter.write('q');
									FFMPEG.waveformWriter.flush();
									FFMPEG.waveformWriter.close();
								} catch (IOException er) {}
								
								FFMPEG.waveformProcess.destroy();
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
								FFPROBE.AnalyzeGOP(videoPath, false);
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
				    			FFMPEG.setCropDetect(new File(videoPath));	  
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
									if (runProcess != null)
									{
										do {
											try {
												Thread.sleep(100);
											} catch (InterruptedException e) {}
										} while (runProcess.isAlive());
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
								updateGrpIn(Timecode.getNTSCtimecode(InputAndOutput.savedInPoint));
								updateGrpOut(Timecode.getNTSCtimecode(totalFrames - InputAndOutput.savedOutPoint));
							}
							else
							{
								updateGrpIn(0);
								updateGrpOut(totalFrames);
								
								playerInMark = 0;
								playerOutMark = waveformContainer.getWidth() - 2;
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
						
					if (Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) && videoPath != null)
					{						
						File video = new File(videoPath);
						String videoWithoutExt = video.getName().substring(0, video.getName().lastIndexOf("."));
						
						SubtitlesTimeline.srt = new File(video.getParent() + "/" + videoWithoutExt + ".srt");
						SubtitlesTimeline.timelineScrollBar.setMaximum((int) totalFrames);
									
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
							Shutter.TCset1.setText(VideoPlayerUI.caseInH.getText());
							Shutter.TCset2.setText(VideoPlayerUI.caseInM.getText());
							Shutter.TCset3.setText(VideoPlayerUI.caseInS.getText());
							Shutter.TCset4.setText(VideoPlayerUI.caseInF.getText());
						}
						
						caseOutH.setText(out[0]);
						caseOutM.setText(out[1]);
						caseOutS.setText(out[2]);
						caseOutF.setText(out[3]);
						
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
				
				if (fileExists)
				{
					return true;
				}
				else
					return false;
			}		
			
		} catch (Exception e) {}		
		
		return false;
	}
	
	public static void setFileList() {
		
		try {
			
			StringBuilder stb = new StringBuilder();
			
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
						fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
						fileExists = true;
					}
					else if (file.equals("null") == false)
					{
						fileList.append(file + System.lineSeparator());
					}
				}
				
				if (fileExists == false)
				{
					fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
				}
			}		
			else if (fileDuration > 40 && Shutter.caseEnableSequence.isSelected() == false)
			{
				fileList.append(videoPath + "|" + caseInH.getText() + ":" + caseInM.getText() + ":" + caseInS.getText() + ":" + caseInF.getText() + "|" + caseOutH.getText() + ":" + caseOutM.getText() + ":" + caseOutS.getText() + ":" + caseOutF.getText() + System.lineSeparator());
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

		setMarkers();
		
		playerCurrentFrame = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * getFPS() + Integer.parseInt(caseInF.getText());

		//NTSC framerate
		playerCurrentFrame = Timecode.getNTSCtimecode(playerCurrentFrame);
		playerCurrentFrame = Timecode.getDropFrameTimecode(playerCurrentFrame);
		
		playerSetTime(playerCurrentFrame);
		
		waveformContainer.repaint();

		//FileList
		setFileList();
	}
	
	public static void updateGrpOut(double timeOut) {
		
		if (playerOutMark < waveformContainer.getWidth() - 2)
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
		
		playerSetTime(playerCurrentFrame);
		
		waveformContainer.repaint();

		//FileList
		setFileList();
	}
	
	public static void setMarkers() {
				
		double timeIn = (Integer.parseInt(caseInH.getText()) * 3600 + Integer.parseInt(caseInM.getText()) * 60 + Integer.parseInt(caseInS.getText())) * getFPS() + Integer.parseInt(caseInF.getText());
		double timeOut = (Integer.parseInt(caseOutH.getText()) * 3600 + Integer.parseInt(caseOutM.getText()) * 60 + Integer.parseInt(caseOutS.getText())) * getFPS() + Integer.parseInt(caseOutF.getText());
		
		timeIn = Math.ceil(timeIn);
		timeOut = Math.ceil(timeOut);

		timeIn = Timecode.getDropFrameTimecode(timeIn);
		timeOut = Timecode.getDropFrameTimecode(timeOut);

		playerInMark = (int) Math.floor((double) (waveformContainer.getSize().width * timeIn) / totalFrames);					
		if ((int) Timecode.getNTSCtimecode(timeOut) < (int) totalFrames)
		{
			playerOutMark = (int) Math.floor((double) (waveformContainer.getSize().width * timeOut) / totalFrames);
		}
		else
			playerOutMark = waveformContainer.getWidth();
		
		waveformContainer.repaint();
	}
	
	public static void getTimePoint(double inputTime) {	
				
		if (inputTime >= totalFrames)
		{
			sliderChange = true;
			playerSetTime(totalFrames);
			sliderChange = false;    		
		}
		
		if (caseInternalTc.isSelected())
			inputTime += offset;
		
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
    					if (cursorWaveform.getX() > waveformContainer.getWidth() - 2)
						{
							cursorWaveform.setLocation(waveformContainer.getWidth() - 2, 0);
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
			
			if (comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
				total = totalFrames - total;
			
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
