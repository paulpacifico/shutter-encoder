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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

import shutterencoder.functions.VideoEncoders;
import shutterencoder.functions.settings.AdvancedFeatures;
import shutterencoder.functions.settings.AudioSettings;
import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.functions.settings.Corrections;
import shutterencoder.functions.settings.ImageSequence;
import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.settings.Transitions;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.functions.utils.Libplacebo;
import shutterencoder.library.DCRAW;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
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
	private static InputStream video = null;
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
	private static int maximumSeek = 60;
    public static BufferedImage frameVideo;
    public static BufferedImage fullSizeWatermark;
    private static final Object lock = new Object();
    public static int activeSegmentIndex = -1;
    public static int dragSegmentIndex = -1;
    public static boolean gpuDecodingIsFaster = false;
        
    private static long lastEvTime = 0;
    private static String freezeFrame = "";
        
    //Waveform
    public static Thread addWaveform = new Thread();
  	public static boolean addWaveformIsRunning = false;
  	public static BufferedImage waveform = null;  	
  		
	//Preview
	public static byte[] preview = null;
	private static volatile boolean loadImageRunning = false;
	private static volatile boolean loadImagePending = false;
	public static Thread loadImageProcess = new Thread();
	private static final Object loadImageLock = new Object();
	
	private static final ExecutorService videoProcessExecutor =
    Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "ffmpeg-prestart");
        t.setDaemon(true);
        return t;
    });
	
	private static Process startVideoProcess(double inputTime) throws IOException, InterruptedException {

	    String args = setVideoCommand(inputTime, player.getWidth(), player.getHeight(), playerPlayVideo);

	    File workingDir = System.getProperty("os.name").contains("Windows")
	            ? new File(Utils.getLibraryPath()).getParentFile()
	            : null;

	    if (args.contains("pipe:1"))
	    {
	        int pipeIndex = args.indexOf('|');
	        String firstPart = args.substring(0, pipeIndex).trim();
	        String secondPart = args.substring(pipeIndex + 1).trim();

	        ProcessBuilder pb1 = new ProcessBuilder(formatCommand(firstPart));
	        if (workingDir != null) pb1.directory(workingDir);
	        else if (LibraryUtils.libplaceboAvailable) FFMPEG.setEnvironment(pb1);

	        ProcessBuilder pb2 = new ProcessBuilder(tokenize(secondPart));
	        if (workingDir != null) pb2.directory(workingDir);
	        else if (LibraryUtils.libplaceboAvailable) FFMPEG.setEnvironment(pb2);

	        List<Process> processes = ProcessBuilder.startPipeline(List.of(pb1, pb2));
	        return processes.get(processes.size() - 1);
	    }
	    else
	    {
	        ProcessBuilder pb = new ProcessBuilder(formatCommand(args));
	        if (workingDir != null) pb.directory(workingDir);
	        else if (LibraryUtils.libplaceboAvailable) FFMPEG.setEnvironment(pb);

	        return pb.start();
	    }
	}
	
	public static void playerProcess(double inputTime, Process preStartedVideoProcess) {

	    if (Utils.loadEncFile != null && Utils.loadEncFile.isAlive())
	        return;

	    try {

	        //VIDEO STREAM
	        playerVideo = (preStartedVideoProcess != null)
	                ? preStartedVideoProcess
	                : startVideoProcess(inputTime);

	        video = playerVideo.getInputStream();
	        videoInputStream = new BufferedInputStream(video);
	        
	        //AUDIO STREAM
			if ((casePlaySound.isSelected() && (mouseIsPressed == false || FFPROBE.audioOnly)) || mouseIsPressed == false)						       
			{					
				ProcessBuilder pba = new ProcessBuilder(formatCommand(setAudioCommand(inputTime, false)));	
				playerAudio = pba.start();

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
			
			synchronized (lock)
			{				
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
										VideoPlayerUtils.getTimePoint(playerCurrentFrame);
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
									
									synchronized (frameCompleteLock) {
									    frameIsComplete = true;
									    frameCompleteLock.notifyAll();
									}
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
									//Allows to wait for the last frame to load	
									synchronized (frameCompleteLock)
									{
									    long remaining = 5000;
									    long deadline = System.currentTimeMillis() + remaining;
	
									    while (!frameIsComplete && remaining > 0) {
									        try {
									            frameCompleteLock.wait(remaining);
									        } catch (InterruptedException e) {
									            Thread.currentThread().interrupt();
									            break;
									        }
	
									        if (frameVideo == null) {
									            frameIsComplete = true;
									            frameCompleteLock.notifyAll();
									            break;
									        }
	
									        remaining = deadline - System.currentTimeMillis();
									    }
	
									    frameIsComplete = true;
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
										            	
										            	if (line != null)
										    				line.flush();
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
			}
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
	        
			final boolean fullRange = ("0-255".equals(FFPROBE.lumaLevel) && !Shutter.caseLevels.isSelected()) || (Shutter.caseLevels.isSelected() && Shutter.comboOutLevels.getSelectedIndex() == 1);

	        for (int y = 0; y < h; y++)
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

	                int Y1 = yuvRef[yRowBase + x] & 0xFF;
	                int Y2 = yuvRef[yRowBase + x + 1] & 0xFF;

	                if (fullRange)
	                {
	                    // Full range
	                	int R1 = clamp(Y1 + ((chromaR) >> 8), 0, 255);
	                	int G1 = clamp(Y1 + ((chromaG) >> 8), 0, 255);
	                	int B1 = clamp(Y1 + ((chromaB) >> 8), 0, 255);

	                	int R2 = clamp(Y2 + ((chromaR) >> 8), 0, 255);
	                	int G2 = clamp(Y2 + ((chromaG) >> 8), 0, 255);
	                	int B2 = clamp(Y2 + ((chromaB) >> 8), 0, 255);

	                    pixels[yRowBase + x]     = (R1 << 16) | (G1 << 8) | B1;
	                    pixels[yRowBase + x + 1] = (R2 << 16) | (G2 << 8) | B2;
	                }
	                else
	                {
	                    // Limited range
	                    int C1 = Y1 - 16;
	                    int C2 = Y2 - 16;

	                    int base1 = 298 * C1;
	                    int base2 = 298 * C2;

	                    int R1 = clamp((base1 + chromaR) >> 8, 0, 255);
	                    int G1 = clamp((base1 + chromaG) >> 8, 0, 255);
	                    int B1 = clamp((base1 + chromaB) >> 8, 0, 255);

	                    int R2 = clamp((base2 + chromaR) >> 8, 0, 255);
	                    int G2 = clamp((base2 + chromaG) >> 8, 0, 255);
	                    int B2 = clamp((base2 + chromaB) >> 8, 0, 255);

	                    pixels[yRowBase + x]     = (R1 << 16) | (G1 << 8) | B1;
	                    pixels[yRowBase + x + 1] = (R2 << 16) | (G2 << 8) | B2;
	                }
	            }
	        }
	    }
	}
	
	private static int clamp(long value, int min, int max) {
	    if (value < min) {
	        return min;
	    }
	    if (value > max) {
	        return max;
	    }
	    return (int) value;
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
				
				ProcessBuilder pba = new ProcessBuilder(formatCommand(setAudioCommand(inputTime, true)));
				Process playerAudio = pba.start();			
					
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
				
				playerAudio.destroyForcibly();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	public static void playerPlay() {
		
	    synchronized (lock)
	    {
	        if (playerVideo == null || playerVideo.isAlive() == false)
	        {
	            playerProcess(playerCurrentFrame, null);
	        }
	    }
	}
	
	public static void playerStop() {
		
		playerLoop = false;
				
		if (playerVideo != null)
		{
			try {
				video.close();
			} catch (IOException e) {}		
			try {
				videoInputStream.close();
			} catch (IOException e) {}
			
			playerVideo.destroyForcibly();
			try {
				playerThread.interrupt();
			} catch(Exception e) {}
		}
		
		if (playerAudio != null)
		{
			try {
				audio.close();
			} catch (IOException e) {}
			
			if (audioInputStream != null)
			{
				try {
					audioInputStream.close();
				} catch (IOException e) {}
			}
			
			playerAudio.destroyForcibly();	
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
		    	VideoPlayerUtils.getTimePoint(playerCurrentFrame); 
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
					
		if (fileDuration <= 40)
		{	
			loadImage(true);
			return;
		}

		if (setTime != null && setTime.isAlive())
	        return;
		
		if ((frameVideo != null || playerCurrentFrame > 0)
		&& playerThread != null && Shutter.doNotLoadImage == false && inputTime < totalFrames && videoPath != null)
		{			
			setTime = new Thread(new Runnable() {

				@Override
				public void run() {					
					
					previewUpscale = false;
					Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					double requestedFrame = Math.floor(inputTime);
					
					boolean useBuffer = false;
					if (preview != null || Shutter.caseAddSubtitles.isSelected())
					{
						preview = null;
					}					
					else if (FFPROBE.audioOnly == false && (mouseIsPressed || frameControl) && playerIsPlaying() == false && playerCurrentFrame != requestedFrame && freezeFrame == "" && Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
					{
						useBuffer = true;
					}			
					
					//Forward value
					int framesToSkip = (int) (requestedFrame - playerCurrentFrame);
					
					//Backward value
					int framesToSkipBackward = (int) (playerCurrentFrame - bufferCurrentFrame);
					if (mouseIsPressed)
					{
						framesToSkipBackward = (int) (playerCurrentFrame - requestedFrame);
					}
					
					//Allows to only use read buffered frames forward
					boolean readNextFrame = false;
					if ((requestedFrame - framesToSkipBackward > bufferCurrentFrame && frameControl) || (requestedFrame >= bufferCurrentFrame && mouseIsPressed))
					{
						readNextFrame = true;
					}
					
					//Read buffered frames if they exists
					if (bufferedFrames.size() > 0 && bufferCurrentFrame < playerCurrentFrame && framesToSkipBackward > 0 && readNextFrame && useBuffer)
					{
						//System.out.println("Read buffered frames");

						int framesToRemove = (int) (mouseIsPressed ? playerCurrentFrame - requestedFrame : framesToSkipBackward);						
						frameVideo = (BufferedImage) bufferedFrames.get((int) (bufferedFrames.size() - framesToRemove));	
						
						bufferCurrentFrame = mouseIsPressed ? requestedFrame + 1 : requestedFrame - framesToRemove;
						
						//Read 1 audio frame
						playerPlayAudioOnly(bufferCurrentFrame);
						
						VideoPlayerUtils.getTimePoint(bufferCurrentFrame); 						
						player.repaint();
					}					
					else if (framesToSkip < maximumSeek && framesToSkip >= 0 && useBuffer) //Read forward is faster until maximumSeek than recreating the process
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
								
								if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source"))
								&& Shutter.caseRotate.isSelected() && (Shutter.comboRotate.getSelectedIndex() == 1 || Shutter.comboRotate.getSelectedIndex() == 2))
								{	
									readFrame(videoInputStream, frameVideo.getHeight(), frameVideo.getWidth(), false, true);
								}
								else
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
											
							VideoPlayerUtils.getTimePoint(bufferCurrentFrame);
							
							//Read 1 audio frame
							playerPlayAudioOnly(bufferCurrentFrame);
							
							player.repaint();
							
						} catch (Exception er) {							
							//System.out.println("CLEARED");
							bufferedFrames.clear();
							bufferCurrentFrame = 0;
						}
					}
					else if (bufferedFrames.size() > 1 && framesToSkipBackward < bufferedFrames.size() - 1 && framesToSkip < 0 && useBuffer) //Read available buffered frames backward
					{	
						if (requestedFrame < bufferCurrentFrame - 1 || mouseIsPressed == false)
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
							
							VideoPlayerUtils.getTimePoint(bufferCurrentFrame); 						
							player.repaint();
						}
					}
					else if (framesToSkip != 0 || (framesToSkip == 0 && mouseIsPressed == false)) //Do not use if there is no time difference and user is currently scrolling
					{					
						//Clear the buffer
						if (bufferedFrames.size() > 0 && playerCurrentFrame != requestedFrame && (framesToSkip >= maximumSeek || 0 - framesToSkip >= bufferedFrames.size() || useBuffer == false))
						{		
							//System.out.println("CLEARED");
							bufferedFrames.clear();
							bufferCurrentFrame = 0;
							
							//IMPORTANT
							requestedFrame += 1;
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
								
								requestedFrame = bufferCurrentFrame - d;
								
								if (bufferedFrames.size() == 0)
									bufferCurrentFrame = 0;
							}
						}
						
						VideoPlayerOverlay.writeCurrentSubs(requestedFrame, false);
						
						playerPlayVideo = false;
						
						boolean playback;
						if (playerIsPlaying())
						{
							playback = true;
						}
						else
							playback = false;
										
						final double time = requestedFrame;
						
						Future<Process> nextVideoProcess = videoProcessExecutor.submit(() -> startVideoProcess(time));

						playerStop();
						try {
						    playerThread.join();
						} catch (InterruptedException e) {
						    Thread.currentThread().interrupt();
						}

						frameControl = true;
						frameIsComplete = false;
						playerLoop = true;

						Process started;
						try {
						    started = nextVideoProcess.get();
						} catch (Exception e) {
						    started = null;
						}

						playerProcess(requestedFrame, started);
						
						playerLoop = true;
						VideoPlayerCore.waitForLastFrame();
													
						if (playback && mouseIsPressed == false)
						{									
							playerLoop = true;
						}
						else if (playback && mouseIsPressed)
						{
							playerCurrentFrame = requestedFrame;
						}
						else
							playerLoop = false;
											
						VideoPlayerUtils.getTimePoint(requestedFrame); 
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
							
					VideoPlayerMultiCuts.updateCurrentSegment();
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
					ProcessBuilder pba = new ProcessBuilder(formatCommand(setAudioCommand(inputTime, false)));						
					playerAudio = pba.start();
					
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
		
	public static void waitForLastFrame() {
		
		//Allows to wait for the last frame to load
		synchronized (frameCompleteLock)
		{
		    long remaining = 5000;
		    long deadline = System.currentTimeMillis() + remaining;
		    while (!frameIsComplete && remaining > 0) {
		        try {
		            frameCompleteLock.wait(remaining);
		        } catch (InterruptedException e) {
		            Thread.currentThread().interrupt();
		            break;
		        }
		        remaining = deadline - System.currentTimeMillis();
		    }
		    frameIsComplete = true;
		}
	}
	
	public static void playerFreeze() {

		synchronized(lock)
		{
			if ((setTime == null || setTime.isAlive() == false) && Shutter.doNotLoadImage == false && (playerVideo == null || playerVideo.isAlive() == false))
			{
				setTime = new Thread(new Runnable() {

					@Override
					public void run() {

						frameVideo = null;

						playerPlayVideo = false;

						VideoPlayerOverlay.writeCurrentSubs(0, false);

						Future<Process> nextVideoProcess = null;
						if (playerThread != null)
						{
							nextVideoProcess = videoProcessExecutor.submit(() -> startVideoProcess(playerCurrentFrame));

							playerStop();
							try {
							    playerThread.join();
							} catch (InterruptedException e) {
							    Thread.currentThread().interrupt();
							}
						}

						frameControl = true; //IMPORTANT to stop the player loop
						frameIsComplete = false;
						playerLoop = true;

						Process started = null;
						if (nextVideoProcess != null)
						{
							try {
								started = nextVideoProcess.get();
							} catch (Exception e) {
								started = null; // falls back to starting it inline below
							}
						}

						playerProcess(playerCurrentFrame, started);

						playerLoop = true;
						VideoPlayerCore.waitForLastFrame();

						if (playerCurrentFrame > 0)
							playerCurrentFrame -= 1;

						VideoPlayerUtils.getTimePoint(playerCurrentFrame);

						frameControl = false;
						playerPlayVideo = true;
					}
				});
				setTime.start();
			}
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
			if (Shutter.comboGPUDecoding.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false
			&& (mouseIsPressed == false || gpuDecodingIsFaster) && previousFrame == false
			&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false)
			{
				gpuDecoding = LibraryUtils.setGPUDevice(setFilter(false, false));
			}

			String extension = videoPath.substring(videoPath.lastIndexOf("."));	
					
			int framesToSkip = (int) ((double) inputTime - playerCurrentFrame);
			
			String decodingOptions = "";
			if (mouseIsPressed && (framesToSkip > maximumSeek || framesToSkip < 0))
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
				colorFormat = "abgr";
			
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
				String deinterlace = AdvancedFeatures.setDeinterlace(true, Settings.btnPreviewOutput.isSelected(), "");		
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
				|| Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan")
				|| Libplacebo.useLibplaceboFilters) //Always need to choose the GPU
				{
					if (LibraryUtils.GPUCount > 1) //GPU 0 is always the integrated, GPU 1 is AMD or Nvidia or Intel which should be much faster
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
						
					break;
						
					case "Apple ProRes":
						
						if (Shutter.comboAccel.getSelectedItem().equals(Shutter.language.getProperty("aucune").toLowerCase()) == false
						&& Shutter.comboAccel.getSelectedItem().equals("Vulkan Video"))			
						{		
							if (deinterlace != "")
							{
								hwupload = ",format=yuv422p10,hwupload";
							}							
							else
								hwupload = " -vf format=yuv422p10,hwupload";
						}
						
					break;
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
					codec += '"' + FFMPEG.PathToFFMPEG.replace("\\", "") + '"';
				
				codec += " -v quiet -hide_banner -i pipe:0" + setFilter(false, true);
								
				cmd = device + Colorimetry.setInputCodec(extension) + " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -hide_banner -ss " + (long) ((double) inputTime * inputFramerateMS) + "ms" + concat + " -i " + '"' + video + '"' + " -r " + FFPROBE.currentFPS +  codec + freezeFrame + " -c:v " + outputFormat + " -pix_fmt " + colorFormat + " -an -sn -f rawvideo -";
			}
									
			if (Shutter.inputDeviceIsRunning)
			{
				cmd = " -strict " + Settings.comboStrict.getSelectedItem() + " -v quiet -hide_banner " + RecordInputDevice.setInputDevices() + setFilter(false, false) + " -c:v " + outputFormat + " -pix_fmt " + colorFormat + " -an -sn -f rawvideo -";
			}
			
			//System.out.println(cmd);

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
		
	private static List<String> formatCommand(String args) {
	    List<String> command = new ArrayList<>();
	    command.add(System.getProperty("os.name").contains("Windows") ? FFMPEG.PathToFFMPEG : FFMPEG.PathToFFMPEG.replace("\\", ""));
	    command.addAll(tokenize(args));
	    return command;
	}
	
	private static List<String> tokenize(String args) {
	    int length = args.length();
	    List<String> result = new ArrayList<>(8);

	    int i = 0;

	    while (i < length) {
	        while (i < length && args.charAt(i) <= ' ') {
	            i++;
	        }

	        if (i >= length) {
	            break;
	        }

	        if (args.charAt(i) == '"') {
	            int start = ++i;

	            while (i < length && args.charAt(i) != '"') {
	                i++;
	            }

	            result.add(args.substring(start, i));

	            if (i < length) {
	                i++;
	            }
	        } else {
	            int start = i;

	            while (i < length && args.charAt(i) > ' ') {
	                i++;
	            }

	            result.add(args.substring(start, i));
	        }
	    }

	    return result;
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
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]amerge=inputs=" + FFPROBE.channels + ",aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"'  + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
							else
							{
								LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a:" + comboAudioTrack.getSelectedIndex() + "]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -"); 
							}
						}
						else
						{
							LibraryUtils.playerWaveform(start + " -v quiet -hide_banner -i " + '"' + videoPath + '"' + " -filter_complex " + '"' + "[0:a]aresample=1000," + duration + "aformat=channel_layouts=mono,compand,showwavespic=size=" + size + "x360:colors=0xE1E1E1,format=rgba,colorkey=black:0.01" + '"' + " -vn -frames:v 1 -c:v png -f image2pipe -");  																
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
					//Format
					String outputFormat = FFPROBE.hasAlpha ? "abgr" : "bgr24";
					
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
				readFrame(inputStream, player.getWidth(), player.getHeight(), true, false);
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
				
		if (Settings.btnPreviewOutput.isSelected()
		|| (mouseIsPressed && gpuDecodingIsFaster == false)
		|| previousFrame)
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
		
		//Libplacebo score
		Libplacebo.getLibplaceboScore(noGPU, true);
		
		//Deinterlacer		
		String deinterlace = "";
		if (noDeinterlacing == false && mouseIsPressed == false)
		{
			deinterlace = AdvancedFeatures.setDeinterlace(true, noGPU, "");
		}
		
		//Global Filter
		String filter = "";
		if (deinterlace != "")
		{
			filter += deinterlace;
		}	
		
		//Rotate
		if (Shutter.caseRotate.isSelected() || Shutter.caseMiror.isSelected())
		{
			filter = shutterencoder.functions.settings.Image.setRotate(filter, noGPU);
		}
		
		//Scaling
		int width = player.getWidth();
		int height = player.getHeight();
		
		//Format
		String bitDepth = FFPROBE.imageDepth == 10 ? "p010" : "nv12";	

		//Crop & Pad
		if (Shutter.comboResolution.getSelectedItem().toString().equals(Shutter.language.getProperty("source")) == false && Shutter.comboResolution.getSelectedItem().toString().contains("AI") == false && noGPU == false && Shutter.inputDeviceIsRunning == false)
		{				
			filter = shutterencoder.functions.settings.Image.setScale(filter, false, noGPU);

			if (filter.contains("scale"))
			{
				filter += shutterencoder.functions.settings.Image.setPad("", false, noGPU);
			}
			else if (filter.contains("libplacebo") && filter.contains("w=") && Shutter.lblPad.getText().equals(Shutter.language.getProperty("lblStretch")) == false)
			{
				filter = shutterencoder.functions.settings.Image.setPad(filter, false, noGPU);
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
			filter = Colorimetry.setZoom(filter);	
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
			height = (height - (height % 4));
		}
						
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
		else if (filterGPU && noGPU == false && FFPROBE.isRotated == false
		&& Shutter.comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
		&& Shutter.comboGPUFilter.getSelectedItem().toString().equals(Shutter.language.getProperty("aucun")) == false)
		{
			//Auto GPU
			if (LibraryUtils.autoCUDA || (LibraryUtils.cudaAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("cuda")))
			{		
				if (filter != "") filter += ",";
				
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_cuda=" + width + ":" + height + ":interp_algo=" + algorithm.replace("neighbor", "nearest").replace("bilinear", "bicubic") + ",hwdownload,format=" + bitDepth;
			}
			else if ((LibraryUtils.autoAMF || (LibraryUtils.amfAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("amf"))) && deinterlace == "")
			{
				if (filter != "") filter += ",";
				
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "vpp_amf=" + width + ":" + height + ":scale_type=" + algorithm.replace("neighbor", "bilinear").replace("bilinear", "bicubic") + ",hwdownload,format=" + bitDepth;
			}
			else if (LibraryUtils.autoQSV || (LibraryUtils.qsvAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("qsv")))
			{		
				if (filter != "") filter += ",";
				
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_qsv=" + width + ":" + height + ":mode=" + algorithm.replace("neighbor", "low_power").replace("bilinear", "hq") + ",hwdownload,format=" + bitDepth;
			}	
			else if ((LibraryUtils.autoVIDEOTOOLBOX || (LibraryUtils.videotoolboxAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("videotoolbox"))) && deinterlace == "")
			{
				if (filter != "") filter += ",";
				
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_vt=" + width + ":" + height + ",hwdownload,format=" + bitDepth;
			}/*
			else if (LibraryUtils.autoVULKAN || (LibraryUtils.vulkanAvailable && Shutter.comboGPUFilter.getSelectedItem().toString().equals("vulkan")))
			{
				if (filter != "") filter += ",";
				
				filter = filter.replace(",hwdownload,format=" + bitDepth, ""); //Removes hwdownload if the scaling is also using GPU to avoid GPU->CPU->GPU transfert
				filter += "scale_vulkan=" + width + ":" + height + ":scaler=" + algorithm.replace("neighbor", "nearest") + ",hwdownload,format=" + bitDepth;
			}*/
			else
			{
				if (Libplacebo.useLibplaceboFilters && filter.contains("hwdownload") == false
				&& (filter.contains("libplacebo") == false || filter.replace(",scale", "scale").substring(filter.indexOf("libplacebo")).contains(",") == false))
				{			
					filter = Libplacebo.setLibplaceboFilter(filter, "w=" + width + ":h=" + height + ":downscaler=" + algorithm.replace("neighbor", "nearest") + ":upscaler=" + algorithm.replace("neighbor", "nearest") + ":reset_sar=1");
				}
				else
				{
					if (filter != "") filter += ",";
					
					filter += "scale=" + width + ":" + height + ":scaler=" + algorithm + ":sws_dither=none";
				}
			}
		}
		else
		{
			if (Libplacebo.useLibplaceboFilters && filter.contains("hwdownload") == false
			&& (filter.contains("libplacebo") == false || filter.replace(",scale", "scale").substring(filter.indexOf("libplacebo")).contains(",") == false))
			{		
				filter = Libplacebo.setLibplaceboFilter(filter, "w=" + width + ":h=" + height + ":downscaler=" + algorithm.replace("neighbor", "nearest") + ":upscaler=" + algorithm.replace("neighbor", "nearest") + ":reset_sar=1");
			}
			else
			{
				if (filter != "") filter += ",";
				
				filter += "scale=" + width + ":" + height + ":scaler=" + algorithm + ":sws_dither=none";
			}
		}
			
		//Reset Libplacebo score to allow using CPU + GPU filters after
		Libplacebo.getLibplaceboScore(noGPU, false);
		
		//Colormatrix
		filter = Colorimetry.setColormatrix(filter);
		
		//LUTs
		filter = Colorimetry.setLUT(filter);
				
		//Deband			
		filter = Corrections.setDeband(filter);
				
		//Colorspace metadata
		filter = Colorimetry.setMetadata(filter);
		
		//Blend
		if (preview == null) //Show only on playing
		{
			filter = ImageSequence.setBlend(filter);
			filter = ImageSequence.setMotionBlur(filter);
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
				 
		//Details			
		filter = Corrections.setDetails(filter);				
											            	
		//Denoise			
		filter = Corrections.setDenoiser(filter, noGPU);
		
		//Exposure
		if (preview == null) //Show only on playing
			filter = Corrections.setSmoothExposure(filter);	
		
		//Levels
		filter = Colorimetry.setLevels(filter);

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
		
		//System.out.println(filter);
		
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

				String subtitlesPath = Shutter.subtitlesFile.toString();
				if (System.getProperty("os.name").contains("Windows"))
				{
					subtitlesPath = Shutter.subtitlesFile.getName(); //Only use the name because of the workingDir from the process
				}
				
				filter = " -f lavfi -i " + '"' + "color=black@0.0,format=rgba,scale=" + subsWidth + ":" + playerHeight + "+" + subsPosY
			  			+ ",subtitles='" + subtitlesPath + "':alpha=1:force_style='FontName=" + Shutter.comboSubsFont.getSelectedItem().toString() + ",FontSize=" + Shutter.textSubsSize.getText() + ",PrimaryColour=&H" + Shutter.subsHex + "&" + background + "'" + '"'
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
}
