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

package shutterencoder.ui.main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.apache.commons.io.FileUtils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.SystemFileChooser;

import shutterencoder.functions.AudioEncoders;
import shutterencoder.functions.AudioNormalization;
import shutterencoder.functions.AudioSeparation;
import shutterencoder.functions.BackgroundRemover;
import shutterencoder.functions.BlackDetection;
import shutterencoder.functions.BlurFaces;
import shutterencoder.functions.Colorize;
import shutterencoder.functions.Command;
import shutterencoder.functions.Conform;
import shutterencoder.functions.DVDRIP;
import shutterencoder.functions.Extract;
import shutterencoder.functions.FrameMD5;
import shutterencoder.functions.LoudnessTruePeak;
import shutterencoder.functions.Merge;
import shutterencoder.functions.OfflineDetection;
import shutterencoder.functions.Picture;
import shutterencoder.functions.ReplaceAudio;
import shutterencoder.functions.Rewrap;
import shutterencoder.functions.Transcribe;
import shutterencoder.functions.Translate;
import shutterencoder.functions.VMAF;
import shutterencoder.functions.VideoEncoders;
import shutterencoder.functions.VideoInserts;
import shutterencoder.functions.settings.InputAndOutput;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.Anonymizer;
import shutterencoder.library.Bmxtranswrap;
import shutterencoder.library.Dcraw;
import shutterencoder.library.Demucs;
import shutterencoder.library.Deoldify;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.Ncnn;
import shutterencoder.library.Python;
import shutterencoder.library.Whisper;
import shutterencoder.library.Xpdfreader;
import shutterencoder.library.Ytdlp;
import shutterencoder.ui.others.Ftp;
import shutterencoder.ui.others.Functions;
import shutterencoder.ui.others.ManageFunctions;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.SceneDetection;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.others.VideoWeb;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.renderers.ComboBoxRenderer;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class ChooseFunction extends Main {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void grpChooseFunction() {

		grpChooseFunction = new CollapsiblePanel(language.getProperty("grpChooseFunction"), false);
		grpChooseFunction.setLayout(null);
		grpChooseFunction.setOpaque(false);
		grpChooseFunction.setBounds(10, grpChooseFiles.getY() + grpChooseFiles.getHeight() + 4, 312, 78);
		grpChooseFunction.setBackground(Utils.c30);
		frame.getContentPane().add(grpChooseFunction);

		btnCancel = new JButton(language.getProperty("btnCancel"));
		btnCancel.setEnabled(false);
		btnCancel.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnCancel.setMargin(new Insets(0, 0, 0, 0));
		btnCancel.setBounds(207, 46, 97, 21);
		grpChooseFunction.add(btnCancel);

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (FFmpeg.runProcess != null && FFmpeg.runProcess.isAlive())
				{
					//Suspend the process
					if (btnStart.getText().equals(language.getProperty("btnPauseFunction")))
						btnStart.doClick();
					
					int q = JOptionPane.showConfirmDialog(frame, Main.language.getProperty("areYouSure"),
							btnCancel.getText(), JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE);

					if (q == JOptionPane.NO_OPTION)
					{
						//Resume the process
						if (btnStart.getText().equals(language.getProperty("btnResumeFunction")))
							btnStart.doClick();		
						
						return;
					}
					
					cancelled = true;
					
					FFmpeg.isRunning = false;

					if (btnStart.getText().equals(language.getProperty("btnResumeFunction")))
						FFmpeg.resumeProcess(); // Si le process est en pause il faut le rédemarrer avant de le détruire
				
					if (InputAndOutput.segments != "")
					{
						FFmpeg.process.destroyForcibly();
						
						try {
							FFmpeg.runProcess.interrupt();
						} catch(Exception err) {}
					}
					else
					{
						try {
							FFmpeg.writer.write('q');
							FFmpeg.writer.flush();
							FFmpeg.writer.close();
						} catch (IOException er) {}	
					}

					Thread wait = new Thread(() -> {

						do {
							try {
								frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								btnStart.setEnabled(false);
								Thread.sleep(10);
							} catch (InterruptedException e1) {
							}
						} while (FFmpeg.runProcess.isAlive());

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));					
					});
					wait.start();
				}

				if (Dcraw.runProcess != null) {
					if (Dcraw.runProcess.isAlive()) {
						Dcraw.process.destroyForcibly();
					}
				}
				
				if (Xpdfreader.runProcess != null) {
					if (Xpdfreader.runProcess.isAlive()) {
						Xpdfreader.process.destroyForcibly();
					}
				}

				if (Ytdlp.runProcess != null) {
					if (Ytdlp.runProcess.isAlive()) {
						if (System.getProperty("os.name").contains("Windows")) {
							try {
								@SuppressWarnings("unused")
								Process processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", "yt-dlp.exe")
										.start();
							} catch (Exception e1) {
							}
						} else {
							Ytdlp.process.destroyForcibly();
						}
					}
				}

				if (Bmxtranswrap.runProcess != null) {
					if (Bmxtranswrap.runProcess.isAlive()) {
						Bmxtranswrap.process.destroyForcibly();
					}
				}

				if (Ncnn.runProcess != null) {
					if (Ncnn.runProcess.isAlive()) {
						Ncnn.process.destroyForcibly();
					}
				}
				
				if (Whisper.runProcess != null) {
					if (Whisper.runProcess.isAlive()) {
						Whisper.process.destroyForcibly();
					}
				}
				
				if (Python.runProcess != null) {
					if (Python.runProcess.isAlive()) {
						Python.process.destroyForcibly();
					}
				}
				
				if (Demucs.runProcess != null) {
					if (Demucs.runProcess.isAlive()) {
						Demucs.process.destroyForcibly();
					}
				}
				
				if (Deoldify.runProcess != null) {
					if (Deoldify.runProcess.isAlive()) {
						Deoldify.process.destroyForcibly();
					}
				}
				
				if (Anonymizer.runProcess != null) {
					if (Anonymizer.runProcess.isAlive()) {
						Anonymizer.process.destroyForcibly();
					}
				}
				
				if (shutterencoder.library.BackgroundRemover.runProcess != null) {
					if (shutterencoder.library.BackgroundRemover.runProcess.isAlive()) {
						shutterencoder.library.BackgroundRemover.process.destroyForcibly();
					}
				}
				
				if (scanIsRunning) {
					UIController.enableAll();
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;

					FunctionUtils.watchFolder.setLength(0);
				}

				if (Ftp.isRunning) {
					try {
						Ftp.ftp.abort();
					} catch (Exception e1) {}
				}

				progressBar.setValue(0);
			}
		});

		iconList = new JLabel(new FlatSVGIcon("resources/list.svg", 15, 15));
		iconList.setToolTipText(language.getProperty("frameFileDeRendus"));
		iconList.setHorizontalAlignment(SwingConstants.CENTER);
		iconList.setVisible(false);
		iconList.setBounds(180, 46, 21, 21);
		grpChooseFunction.add(iconList);

		iconList.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("resources/list.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept && btnStart.getText().equals(language.getProperty("btnStartFunction"))) {
					iconList.setVisible(false);
					btnCancel.setBounds(207, 46, 97, 21);

					if (iconPresets.isVisible()) {
						iconPresets.setBounds(180, 45, 21, 21);
						btnCancel.setBounds(207, 46, 97, 21);
					} else
						btnCancel.setBounds(184, 46, 120, 21);

					btnStart.setText(language.getProperty("btnAddToRender"));
					if (RenderQueue.frame == null)
						new RenderQueue();
					else
						Utils.changeFrameVisibility(RenderQueue.frame, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("resources/list.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconList.setIcon(new FlatSVGIcon("resources/list.svg", 15, 15));
				accept = false;
			}

		});

		iconPresets = new JLabel(new FlatSVGIcon("resources/presets.svg", 15, 15));
		iconPresets.setToolTipText(language.getProperty("frameFonctions"));
		iconPresets.setHorizontalAlignment(SwingConstants.CENTER);
		iconPresets.setVisible(true);
		iconPresets.setBounds(180, 45, 21, 21);
		grpChooseFunction.add(iconPresets);

		iconPresets.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("resources/presets.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept) {
					iconPresets.setVisible(false);
					if (iconList.isVisible())
						btnCancel.setBounds(207, 46, 97, 21);
					else
						btnCancel.setBounds(184, 46, 120, 21);

					if (Functions.frame == null) {
						if (frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))) {
							long time = System.currentTimeMillis();

							do {

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
								}

								if (System.currentTimeMillis() - time > 3000)
									break;

							} while (frame.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
						}
						new Functions();
					} else {
						if (Functions.presetsList.getModel().getSize() > 0) {
							Functions.lblSave.setVisible(false);
							Functions.lblDrop.setVisible(false);
						}

						Functions.frame.setVisible(true);
					}
					iconPresets.setIcon(new FlatSVGIcon("resources/presets.svg", 15, 15));
					Utils.changeFrameVisibility(Functions.frame, false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("resources/presets.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconPresets.setIcon(new FlatSVGIcon("resources/presets.svg", 15, 15));
				accept = false;
			}

		});

		btnStart = new JButton(language.getProperty("btnStartFunction"));
		btnStart.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnStart.setMargin(new Insets(0, 0, 0, 0));
		btnStart.setBounds(8, 46, 168, 21);
		grpChooseFunction.add(btnStart);

		btnStart.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {

				lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
				
				FunctionUtils.yesToAll = false;
				FunctionUtils.noToAll = false;
				FunctionUtils.skipToAll = false;
				
				if (btnStart.getText().equals(language.getProperty("btnStartFunction")) && comboFonctions.getEditor().getItem().equals("") == false && list.getSize() > 0)
				{
					btnStart.setEnabled(false);						
				}

				if ((btnStart.getText().equals(language.getProperty("btnStartFunction")) || btnStart.getText().equals(language.getProperty("btnAddToRender"))) && list.getSize() > 0)
				{
					grpDestination.setSelectedIndex(0);
					FFmpeg.error = false;
					FFmpeg.errorLog.setLength(0);
					errorList.setLength(0);
					
					// Temps écoulé
					lblElapsedTime.setVisible(false);
					FFmpeg.elapsedTime = 0;
					FFmpeg.previousElapsedTime = 0;

					if (btnStart.getText().equals(language.getProperty("btnAddToRender")))
						RenderQueue.btnStartRender.setEnabled(true);

					// Command directe FFMPEG
					if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")) {
						if (comboFilter.getEditor().getItem().toString().equals(language.getProperty("aucun"))
								|| comboFilter.getEditor().getItem().toString().equals("")
								|| comboFilter.getEditor().getItem().toString().equals(" ")
								|| comboFilter.getEditor().getItem().toString().contains(".") == false) {
							JOptionPane.showMessageDialog(frame, language.getProperty("chooseExtension"),
									language.getProperty("extensionError"), JOptionPane.INFORMATION_MESSAGE);
						} else
							Command.main();

					} else if (comboFonctions.getEditor().getItem().toString().contains("exiftool")) {
						Command.main();
					} else {
						// Scan Folder
						if (scan.getText().equals(language.getProperty("menuItemStopScan")) && scanIsRunning == false) {
							JOptionPane.showMessageDialog(Main.frame,
									Main.language.getProperty("dragFolderToDestination"),
									Main.language.getProperty("chooseDestinationFolder"),
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							if (inputDeviceIsRunning) {
								VideoPlayerCore.playerStop();
							}

							String function = comboFonctions.getSelectedItem().toString();
							if (language.getProperty("functionCut").equals(function)) {
								if (inputDeviceIsRunning) {
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								} else
									Rewrap.main();
							} else if ("WAV".equals(function) || "MP3".equals(function) || "AC3".equals(function)
									|| "Opus".equals(function) || "Vorbis".equals(function) || "AIFF".equals(function)
									|| "FLAC".equals(function) || "ALAC".equals(function) || "AAC".equals(function)
									|| "Dolby Digital Plus".equals(function) || "Dolby TrueHD".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									AudioEncoders.main();

							} else if ("Loudness & True Peak".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									LoudnessTruePeak.main();
							} else if (language.getProperty("functionMerge").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									Merge.main();
							} else if (language.getProperty("functionExtract").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (comboFilter.getSelectedItem().toString()
										.equals(language.getProperty("setAll")))
									Extract.extractAll();
								else
									Extract.main();
							} else if (language.getProperty("functionConform").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									Conform.main();
							} else if (language.getProperty("functionInsert").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else
									VideoInserts.main();
							} else if (language.getProperty("functionSeparation").equals(function)) {
								
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
								{
									AudioSeparation.main();
								}
								
							} else if (language.getProperty("functionTranscribe").equals(function)) {
														
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									Transcribe.main();							
								
							} else if (language.getProperty("functionTranslate").equals(function)) {
								
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									Translate.main();	
							}
							else if (language.getProperty("functionColorize").equals(function))
							{								
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									Colorize.main();
							}
							else if (language.getProperty("functionBlurFaces").equals(function))
							{
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}		
								else
									BlurFaces.main();
							}
							else if (language.getProperty("functionBackgroundRemover").equals(function))
							{
								if (inputDeviceIsRunning)
								{
									JOptionPane.showMessageDialog(frame,								
									language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								}
								else
									BackgroundRemover.main();							
							}
							else if (language.getProperty("functionReplaceAudio").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									ReplaceAudio.setStreams();
								}

							} else if (language.getProperty("functionNormalization").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									AudioNormalization.main(new File(""));
							} else if (language.getProperty("functionSceneDetection").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else if (scanIsRunning)
									JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
											language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
								else {
									if (SceneDetection.frame == null)
										new SceneDetection(true);
									else {
										Utils.changeFrameVisibility(SceneDetection.frame, false);
										SceneDetection.btnAnalyze.doClick();
									}
								}
							} else if (language.getProperty("functionBlackDetection").equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									BlackDetection.main();
							} else if (language.getProperty("functionOfflineDetection").equals(function)) {

								Object[] options = { "Avid", "Davinci", "Premiere", "Custom" };

								int NLE = JOptionPane.showOptionDialog(frame, language.getProperty("chooseSoftware"),
										language.getProperty("functionOfflineDetection"),
										JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
										options[0]);

								URL sourceFile = getClass().getResource("/resources/avid.png");
								File destinationFile = new File(dirTemp + "offline.png");

								switch (NLE) {
								case 0:
									FFmpeg.mseSensibility = 150f;
									break;
								case 1:
									FFmpeg.mseSensibility = 150f;
									sourceFile = getClass().getResource("/resources/davinci.png");
									break;
								case 2:
									FFmpeg.mseSensibility = 800f;
									sourceFile = getClass().getResource("/resources/premiere.png");
									break;
								case 3:
									FFmpeg.mseSensibility = 800f;
									
									SystemFileChooser fc = new SystemFileChooser();
									fc.setCurrentDirectory(new File(list.elementAt(0).toString()).getParentFile());
									
									if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
									{
										try {
											sourceFile = fc.getSelectedFile().toURL();
										} catch (MalformedURLException e2) {}
									}

									break;
								}

								if (destinationFile.exists())
									destinationFile.delete();

								try {
									FileUtils.copyURLToFile(sourceFile, destinationFile);
								} catch (Exception e1) {
								}

								if (destinationFile.exists())
									OfflineDetection.main();

							} else if ("VMAF".equals(function)) {

								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									VMAF.main();

							} else if ("FrameMD5".equals(function)) {

								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									FrameMD5.main();

							} else if ("DNxHD".equals(function) || "DNxHR".equals(function)
									|| "Apple ProRes".equals(function) || "GoPro CineForm".equals(function)
									|| "QT Animation".equals(function) || "Uncompressed".equals(function)
									|| "H.264".equals(function) || "H.265".equals(function) || "H.266".equals(function)
									|| "WMV".equals(function) || "MPEG-1".equals(function) || "MPEG-2".equals(function)
									|| "VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function)
									|| "Theora".equals(function) || "MJPEG".equals(function) || "Xvid".equals(function)
									|| "XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function)
									|| "AVC-Intra 100".equals(function) || "XAVC".equals(function) || "XAVC Long GOP".equals(function)
									|| "HAP".equals(function) || "FFV1".equals(function) || "DV".equals(function)) {
								VideoEncoders.main();

							} else if ("DVD".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									VideoEncoders.main();
							} else if ("Blu-ray".equals(function)) {
								if (inputDeviceIsRunning)
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								else
									VideoEncoders.main();
							} else if (language.getProperty("functionPicture").equals(function)
									|| "JPEG".equals(function) || "JPEG XL".equals(function)) {
								Picture.main(true, false);
							} else if (language.getProperty("functionRewrap").equals(function)) {
								if (inputDeviceIsRunning) {
									JOptionPane.showMessageDialog(frame,
											language.getProperty("incompatibleInputDevice"),
											language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
								} else
									Rewrap.main();
							}
						}
					}
				}
				else // Fonctions n'ayant pas de fichiers dans la liste
				{
					if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) {
						
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}

						new VideoWeb();
						frame.setOpacity(1.0f);										
						
					} else if (comboFonctions.getSelectedItem().equals("DVD Rip")
							&& btnStart.getText().equals(language.getProperty("btnStartFunction"))) {
						if (inputDeviceIsRunning)
							JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"),
									language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
						else if (scanIsRunning)
							JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
									language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
						else {
							UIController.disableAll();
							DVDRIP.main();
						}
					}

					if (btnStart.getText().equals(language.getProperty("btnPauseFunction"))) {
						caseRunInBackground.setEnabled(false);
						caseRunInBackground.setSelected(false);

						FFmpeg.suspendProcess();

						btnStart.setText(language.getProperty("btnResumeFunction"));
						lblRemainingTime.setText(language.getProperty("timePause"));
						lblRemainingTime.setSize(lblRemainingTime.getPreferredSize().width, 15);
					} else if (btnStart.getText().equals(language.getProperty("btnResumeFunction"))) {
						caseRunInBackground.setEnabled(true);

						FFmpeg.resumeProcess();

						btnStart.setText(language.getProperty("btnPauseFunction"));
					} else if (btnStart.getText().equals(language.getProperty("btnStopRecording"))) {
						if (FFmpeg.runProcess != null) {
							if (FFmpeg.runProcess.isAlive()) {

								try {
									FFmpeg.writer.write('q');
									FFmpeg.writer.flush();
									FFmpeg.writer.close();
								} catch (IOException er) {}

								try {
									FFmpeg.runProcess.join();
								} catch (InterruptedException er) {
								    Thread.currentThread().interrupt();
								}
							}
						}

						btnStart.setText(language.getProperty("btnStartFunction"));
					}
				}
			}
		});

		functionsList = new ArrayList<>(Arrays.asList(
				
			    language.getProperty("itemAITools"), language.getProperty("functionSeparation"), language.getProperty("functionTranscribe"),
			    language.getProperty("functionTranslate"), language.getProperty("functionColorize"), language.getProperty("functionBlurFaces"),
			    language.getProperty("functionBackgroundRemover"),

			    language.getProperty("itemNoConversion"), language.getProperty("functionCut"),
			    language.getProperty("functionReplaceAudio"), language.getProperty("functionRewrap"),
			    language.getProperty("functionConform"), language.getProperty("functionMerge"),
			    language.getProperty("functionExtract"), language.getProperty("functionSubtitles"),
			    language.getProperty("functionInsert"),

			    language.getProperty("itemAudioConversion"), "WAV", "AIFF", "FLAC", "ALAC", "MP3", "AAC", "AC3", "Opus",
			    "Vorbis", "Dolby Digital Plus", "Dolby TrueHD",

			    language.getProperty("itemEditingCodecs"), "DNxHD", "DNxHR", "Apple ProRes", "QT Animation",
			    "GoPro CineForm", "Uncompressed",

			    language.getProperty("itemOuputCodecs"), "H.264", "H.265", "H.266", "VP8", "VP9", "AV1",

			    language.getProperty("itemBroadcastCodecs"), "XDCAM HD422", "XDCAM HD 35", "AVC-Intra 100", "XAVC", "XAVC Long GOP", "HAP",

			    language.getProperty("itemOldCodecs"), "Theora", "MPEG-2", "MJPEG", "Xvid", "DV", "WMV", "MPEG-1",

			    language.getProperty("itemArchiveCodecs"), "FFV1",

			    language.getProperty("itemImage"), "JPEG", "JPEG XL", language.getProperty("functionPicture"),

			    language.getProperty("itemBurnRip"), "DVD", "Blu-ray", "DVD Rip",

			    language.getProperty("itemAnalyze"), "Loudness & True Peak",
			    language.getProperty("functionNormalization"), language.getProperty("functionSceneDetection"),
			    language.getProperty("functionBlackDetection"), language.getProperty("functionOfflineDetection"),
			    "VMAF", "FrameMD5",

			    language.getProperty("itemDownload"), language.getProperty("functionWeb")
			));  
		
		if (System.getProperty("os.name").contains("Mac") && arch.equals("x86_64"))
		{
			functionsList.remove("H.266");
		    functionsList.remove(language.getProperty("functionTranscribe"));
		    functionsList.remove(language.getProperty("functionBlurFaces"));
		}
		else if (System.getProperty("os.name").contains("Linux"))
		{
			functionsList.remove(language.getProperty("functionTranscribe"));
			functionsList.remove(language.getProperty("functionBlurFaces"));
			
		}
		
		functionsList.add("- " + language.getProperty("btnManage").toUpperCase() + " -");

		comboFonctions = new JComboBox<String[]>();
		comboFonctions.setName("comboFonctions");
		comboFonctions.setModel(new DefaultComboBoxModel(functionsList.toArray(new String[0])));
		comboFonctions.setSelectedItem("");
		comboFonctions.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboFonctions.setEditable(true);
		comboFonctions.setMaximumRowCount(Toolkit.getDefaultToolkit().getScreenSize().height / 33);
		comboFonctions.setBounds(8, 19, 168, 22);
		comboFonctions.getModel().setSelectedItem("");
		comboFonctions.setRenderer(new ComboBoxRenderer());
		grpChooseFunction.add(comboFonctions);
		
		comboFonctions.addActionListener(new ActionListener() {

			ArrayList<String> newList = new ArrayList<String>();

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboFonctions.getSelectedItem().toString().contains("-")
				&& comboFonctions.getSelectedItem().toString().contains(":")
				&& comboFonctions.getSelectedItem().toString().contains("ffmpeg") == false
				&& comboFonctions.getSelectedItem().toString().contains("exiftool") == false)
				{
					comboFonctions.setSelectedIndex(comboFonctions.getSelectedIndex() + 1);
				}

				if (newList.isEmpty() == false && comboFonctions.getSelectedItem().toString().contains(language.getProperty("btnManage").toUpperCase()) == false)
				{
					comboFonctions.hidePopup();
					comboFonctions.setModel(new DefaultComboBoxModel(newList.toArray()));
					UIController.changeFilters();
					UIController.changeFunction(true);
					UIController.changeSections(false);
					comboFonctions.showPopup();

					newList.clear();
				}

				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) && Settings.modelIsSaving == false) 
				{
					if (scanIsRunning == false) {
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {}

						new VideoWeb();
						frame.setOpacity(1.0f);
					}
				}
				
				if (comboFonctions.getSelectedItem().toString().contains(language.getProperty("btnManage").toUpperCase()))
				{		
					UIController.changeWidth();
					
					try {
						frame.setOpacity(0.5f);
					} catch (Exception er) {}
					
					new ManageFunctions();	
					frame.setOpacity(1.0f);
					
					comboFonctions.setSelectedItem("");
				}
			}

		});

		comboFonctions.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

			String text = "";
			
			private Timer debounceTimer;
		    private final int DEBOUNCE_DELAY = 500;

			@Override
			public void keyReleased(KeyEvent e) {

				// Stop the running timer on every keystroke
		        if (debounceTimer != null && debounceTimer.isRunning()) {
		            debounceTimer.stop();
		        }
				
				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")
				|| comboFonctions.getEditor().getItem().toString().contains("exiftool"))
				{
					UIController.changeFilters();
					UIController.changeWidth();

					topPanel.setBounds(0, 0, frame.getWidth(), 28);
					topImage.setBounds(0, 0, topPanel.getWidth(), 24);
					
					UIController.setButtonsLocation();

					addToList.setText(language.getProperty("dropFilesHere"));

					if (list.getSize() == 0)
						addToList.setVisible(true);
					else
						addToList.setVisible(false);
					
				} else {
					
					if (comboFonctions.getEditor().toString().length() <= 1)
						text = String.valueOf(e.getKeyChar()).toLowerCase();

					if (Character.isLetterOrDigit(e.getKeyChar()))
					{						
						comboFonctions.setModel(new DefaultComboBoxModel(functionsList.toArray(new String[0])));
						
						text += String.valueOf(e.getKeyChar()).toLowerCase();

						ArrayList<String> newList = new ArrayList<String>();
						
						boolean addTranscribeFunction = false;
						boolean addSeparationFunction = false;
						boolean addColorizeFunction = false;
						boolean addBlurFaces = false;
						boolean addBackgroundRemover = false;
						for (int i = 0; i < comboFonctions.getItemCount(); i++)
						{
							if (functionsList.get(i).toString().length() >= text.length())
							{
							    if (functionsList.get(i).toString().toLowerCase().substring(0, text.length()).contains(text)
							    && functionsList.get(i).toString().contains(":") == false)
							    {
							        if (functionsList.get(i).toString().equals(language.getProperty("functionTranscribe")))
							        {
							            addTranscribeFunction = true;
							        }
							        else if (functionsList.get(i).toString().equals(language.getProperty("functionSeparation")))
							        {
							            addSeparationFunction = true;
							        }
							        else if (functionsList.get(i).toString().equals(language.getProperty("functionColorize")))
							        {
							            addColorizeFunction = true;
							        }
							        else if (functionsList.get(i).toString().equals(language.getProperty("functionBlurFaces")))
							        {
							        	addBlurFaces = true;
							        }
							        else if (functionsList.get(i).toString().equals(language.getProperty("functionBackgroundRemover")))
							        {
							            addBackgroundRemover = true;
							        }
							        else
							            newList.add(functionsList.get(i).toString());
							    }
							}
						}
						
						if (addTranscribeFunction)
						{
							newList.add(language.getProperty("functionTranscribe"));
						}
						
						if (addSeparationFunction)
						{
							newList.add(language.getProperty("functionSeparation"));
						}
						
						if (addColorizeFunction)
						{
							newList.add(language.getProperty("functionColorize"));
						}
						
						if (addBlurFaces)
						{
							newList.add(language.getProperty("functionBlurFaces"));
						}
						
						if (addBackgroundRemover)
						{
							newList.add(language.getProperty("functionBackgroundRemover"));
						}

						// Pour éviter d'afficher le premier item
						comboFonctions.getEditor().setItem(text);

						if (newList.isEmpty() == false)
						{
							comboFonctions.setModel(new DefaultComboBoxModel(newList.toArray()));
							comboFonctions.showPopup();
							
							debounceTimer = new Timer(DEBOUNCE_DELAY, event -> {
		                        UIController.changeFilters();
		                        UIController.changeFunction(true);
		                    });
		                    debounceTimer.setRepeats(false);
		                    debounceTimer.start();
						}
					}
					else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						if (ManageFunctions.selectedFunctions != null)
						{
							comboFonctions.setModel(new DefaultComboBoxModel(ManageFunctions.selectedFunctions));
						}
						else
							comboFonctions.setModel(new DefaultComboBoxModel(functionsList.toArray(new String[0])));
						
						comboFonctions.getEditor().setItem("");
						comboFonctions.hidePopup();
						UIController.changeFilters();
						UIController.changeSections(true);
						UIController.changeWidth();
						text = "";
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						e.consume();// Contournement pour éviter le listeDrop
					} else
						comboFonctions.hidePopup();
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (VideoPlayerUI.btnPlay.isVisible() && list.getSize() > 0) {
						VideoPlayerUI.btnPlay.requestFocus();
					} else {
						frame.requestFocus();
					}
				}
			}
		});

		String AllFilters[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv", ".mp4",
				".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd", ".webm",
				".webp", ".avif" };
		comboFilter = new JComboBox<Object>(AllFilters);
		comboFilter.setVisible(false);
		comboFilter.setName("comboFilter");
		comboFilter.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboFilter.setEditable(true);
		comboFilter.setMaximumRowCount(20);
		comboFilter.setBounds(228, 19, 76, 22);
		grpChooseFunction.add(comboFilter);

		comboFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (Main.lblFilter.isVisible()) {
					if (comboFonctions.getSelectedItem().toString().contains("H.26")
							|| comboFonctions.getSelectedItem().toString().equals("AV1")) {
						if (comboFilter.getSelectedItem().toString().equals(".mp4")
								|| comboFilter.getSelectedItem().toString().equals(".mov")) {
							caseFastStart.setEnabled(true);
						} else
							caseFastStart.setEnabled(false);
					}

					// Add quality selection
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
					{
						UIController.setGPUOptions();

						if (comboFilter.getSelectedItem().toString().equals(".avif"))
						{
							LibraryUtils.detectHardwareAcceleration("AV1");
						}
					}

					if (comboFonctions.getSelectedItem().toString().equals("Blu-ray")) {
						if (comboFilter.getSelectedIndex() == 0) // H.264
						{
							debitVideo.setSelectedItem(38000);
						} else // H.265
							debitVideo.setSelectedItem(50000);
					}

					if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed")) {
						if (comboFilter.getSelectedItem().toString().equals("36")) {
							caseForcerEntrelacement.setEnabled(false);
							caseForcerInversion.setEnabled(false);
							caseForcerEntrelacement.setSelected(false);
							caseForcerInversion.setSelected(false);
						} else {
							caseForcerEntrelacement.setEnabled(true);
							caseForcerInversion.setEnabled(true);
						}
					}
					
					UIController.changeComboOptions();
				}
			}

		});

		comboFilter.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboFilter.getSelectedIndex();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				newitem = newitem + e.getWheelRotation();
				if (newitem >= 0 && newitem < comboFilter.getItemCount())
					comboFilter.setSelectedIndex(newitem);
				else
					newitem = comboFilter.getSelectedIndex();
			}
		});

		lblFilter = new JLabel();
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(new Font(boldFont, Font.PLAIN, 13));
		lblFilter.setBounds(164, 21, 60, 16);
		grpChooseFunction.add(lblFilter);

	}
}
