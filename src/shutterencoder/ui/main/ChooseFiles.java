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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.SystemFileChooser;

import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.BMXTRANSWRAP;
import shutterencoder.library.DVDAUTHOR;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.MEDIAINFO;
import shutterencoder.library.SEVENZIP;
import shutterencoder.library.TSMUXER;
import shutterencoder.ui.handlers.ListFileTransferHandler;
import shutterencoder.ui.others.Ftp;
import shutterencoder.ui.others.GOP;
import shutterencoder.ui.others.HashGenerator;
import shutterencoder.ui.others.Informations;
import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.others.Renamer;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.renderers.FilesCellRenderer;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class ChooseFiles extends Main {

	@SuppressWarnings({ "unchecked" })
	public static void grpChooseFiles() {

		grpChooseFiles = new CollapsiblePanel(language.getProperty("grpChooseFiles"), false);
		grpChooseFiles.setLayout(null);
		grpChooseFiles.setOpaque(false);
		grpChooseFiles.setBounds(10, 30, 312, frame.getHeight() - 423);
		grpChooseFiles.setBackground(Utils.c30);
		frame.getContentPane().add(grpChooseFiles);

		fileList = new JList<String>(list);
		fileList.setBackground(Utils.c35);
		fileList.setCellRenderer(new FilesCellRenderer());
		fileList.setFixedCellHeight(17);
		fileList.setBounds(10, 50, 292, frame.getHeight() - 483);
		fileList.setToolTipText(language.getProperty("rightClick"));

		fileList.getModel().addListDataListener(new ListDataListener() {
			
		    private void checkVisibility() {
		    	
		        boolean hasFiles = fileList.getModel().getSize() > 0;
		        
		        if (hasFiles == false && comboFonctions.getSelectedItem().toString().equals(Main.language.getProperty("functionSubtitles")) == false)
				{
					// Screen record
					if (inputDeviceIsRunning)
						caseDisplay.setSelected(false);

					inputDeviceIsRunning = false;
					grpImageAdjustement.setEnabled(true);
					Component[] components = Main.grpImageAdjustement.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(true);
					}

					if (overlayDeviceIsRunning) {
						caseAddWatermark.setSelected(false);
						overlayDeviceIsRunning = false;
					}

					// Scan
					scan.setText(language.getProperty("menuItemStartScan"));
					scanIsRunning = false;

					FunctionUtils.watchFolder.setLength(0);

					addToList.setVisible(true);
					lblFilesEnded.setVisible(false);
	
					if (FunctionUtils.deleteSRT && subtitlesFilePath != null)
					{
						subtitlesFilePath.delete();
					}						
					
					// H264 Settings
					if (isLocked == false) {
						bitrateSize.setText("-");
					}		
					
					if (VideoPlayerCore.playerIsPlaying())
						VideoPlayerUI.btnPlay.doClick();
					
					VideoPlayerCore.playerStop();
					VideoPlayerUI.setPlayerButtons(false);
					VideoPlayerUI.player.remove(selection);
					VideoPlayerUI.player.remove(overImage);
					VideoPlayerUI.player.remove(timecode);
					VideoPlayerUI.player.remove(fileName);
					VideoPlayerUI.player.remove(subsCanvas);
					VideoPlayerUI.player.remove(logo);
					VideoPlayerUI.player.repaint();

					UIController.changeFilters();

					lblFiles.setText(Utils.filesNumber());

					FFPROBE.analyzedMedia = null;
					VideoPlayerUtils.preview = null;
					VideoPlayerCore.videoPath = null;
					VideoPlayerCore.frameVideo = null;
					
					// Waveform					
					if (VideoPlayerUtils.addWaveformIsRunning)
					{		
						VideoPlayerUtils.stopWaveformCreation();
						
						try {
							VideoPlayerUtils.addWaveform.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}
					}
					
					// Thumbnails
					if (VideoPlayerCore.thumbnails != null)
					{			
						VideoPlayerUtils.allThumbnails = null;
						VideoPlayerUtils.stopThumbnailsCreation();
					}
					
					grpFileInformation.removeAll();
		        	grpFileInformation.setLayout(null);
		        	grpFileInformation.add(labelFileInfo);
			        grpFileInformation.revalidate();
			        grpFileInformation.repaint();
				}		        
		    }

		    @Override
		    public void intervalAdded(ListDataEvent e) {
		        checkVisibility();
		    }

		    @Override
		    public void intervalRemoved(ListDataEvent e) {
		        checkVisibility();
		    }

		    @Override
		    public void contentsChanged(ListDataEvent e) {
		        checkVisibility();
		    }
		});
		
		addToList.setIcon(new FlatSVGIcon("resources/drop.svg", 40, 40));
		addToList.setText(language.getProperty("dropFilesHere"));
		addToList.setVerticalTextPosition(JLabel.BOTTOM);
		addToList.setHorizontalTextPosition(JLabel.CENTER);
		addToList.setSize(fileList.getSize());
		addToList.setForeground(new Color(120, 120, 120));
		addToList.setBackground(new Color(0, 0, 0, 0));
		addToList.setFont(new Font(mainFont, Font.PLAIN, 16));
		addToList.setHorizontalAlignment(SwingConstants.CENTER);
		addToList.setVerticalAlignment(SwingConstants.CENTER);
		fileList.add(addToList);

		btnEmptyList = new JButton(language.getProperty("btnEmptyList"));
		btnEmptyList.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnEmptyList.setBounds(124, 21, 82, 21);
		grpChooseFiles.add(btnEmptyList);

		btnEmptyList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				list.clear();
				
			}
			
		});
		
		scrollBar = new JScrollPane();
		scrollBar.getViewport().add(fileList);
		scrollBar.setBounds(10, 50, 292, fileList.getHeight());
		scrollBar.setOpaque(false);
		scrollBar.getViewport().setOpaque(false);
		grpChooseFiles.add(scrollBar);
		
		JScrollBar verticalBar = scrollBar.getVerticalScrollBar();
		verticalBar.setBackground(Utils.c35);

		JScrollBar horizontalBar = scrollBar.getHorizontalScrollBar();
		horizontalBar.setBackground(Utils.c35);

		// Drag & Drop
		fileList.setTransferHandler(new ListFileTransferHandler());

		fileList.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				if (list.getSize() == 0) {
					if (inputDeviceIsRunning)
						caseDisplay.setSelected(false);

					inputDeviceIsRunning = false;
					grpImageAdjustement.setEnabled(true);
					Component[] components = Main.grpImageAdjustement.getComponents();
					for (int i = 0; i < components.length; i++) {
						components[i].setEnabled(true);
					}

					if (overlayDeviceIsRunning) {
						caseAddWatermark.setSelected(false);
						overlayDeviceIsRunning = false;
					}

					UIController.changeFilters();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (scanIsRunning == false) {
					if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0))
						fileList.setSelectionInterval(0, list.getSize() - 1);

					if (e.getKeyCode() == 127 && list.getSize() > 0 || e.getKeyCode() == 8 && list.getSize() > 0) {
						do {
							list.remove(fileList.getSelectedIndex());
						} while (fileList.getSelectedIndices().length > 0);

						if (list.getSize() == 0)
							addToList.setVisible(true);

						lblFiles.setText(Utils.filesNumber());
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				// VideoPlayer.player
				VideoPlayerUtils.setMedia();
			}

		});

		popupList = new JPopupMenu();
		menuDisplay = new JMenuItem(language.getProperty("menuItemVisualiser"));
		final JMenuItem silentTrack = new JMenuItem(language.getProperty("menuItemSilentTrack"));
		final JMenuItem menuOpenFolder = new JMenuItem(language.getProperty("menuItemOuvrirDossier"));
		informations = new JMenuItem(language.getProperty("menuItemInfo"));
		final JMenuItem rename = new JMenuItem(language.getProperty("menuItemRename"));
		inputDevice = new JMenuItem(Main.language.getProperty("menuItemInputDevice"));
		final JMenuItem arborescence = new JMenuItem(language.getProperty("menuItemArborescence"));
		final JMenuItem hash = new JMenuItem(Main.language.getProperty("menuItemHash"));
		final JMenuItem tempsTotal = new JMenuItem(language.getProperty("menuItemTempsTotal"));
		final JMenuItem poids = new JMenuItem(language.getProperty("menuItemPoids"));
		final JMenuItem gop = new JMenuItem(language.getProperty("menuItemGop"));
		final JMenuItem ftp = new JMenuItem(language.getProperty("menuItemFtp"));
		final JMenuItem zip = new JMenuItem(language.getProperty("menuItemZip"));
		final JMenuItem unzip = new JMenuItem(language.getProperty("menuItemUnzip"));

		silentTrack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				list.addElement(" -f lavfi -i anullsrc=r=" + lbl48k.getSelectedItem().toString() + ":cl=mono");
			}
		});

		menuDisplay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					FFPROBE.processData.join();
				} catch (InterruptedException er) {
				    Thread.currentThread().interrupt();
				}

				FFMPEG.toSDL();
			}
		});

		menuOpenFolder.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.getProperty("os.name").contains("Mac"))
				{
					try {
						Runtime.getRuntime().exec(new String[] { "/usr/bin/open", "-R", fileList.getSelectedValue() });
					} catch (Exception e2) {}
				} else if (System.getProperty("os.name").contains("Linux"))
				{
					Utils.openFile(new File(fileList.getSelectedValue()).getParentFile());
				}
				else // Windows
				{
					try {
						Runtime.getRuntime().exec("explorer.exe /select," + fileList.getSelectedValue());
					} catch (IOException e1) {
					}
				}
			}
		});

		tempsTotal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				double totalLength = 0;
				FFPROBE.totalLength = 0;
				FFPROBE.analyzedMedia = null;

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				for (String file : fileList.getSelectedValuesList())
				{					
					//IMPORTANT RESET MEDIA
					FFPROBE.analyzedMedia = null;
					
					FFPROBE.Data(file);					
					try {
						FFPROBE.processData.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}

					totalLength += Math.round((double) FFPROBE.totalLength / 1000 * FFPROBE.accurateFPS);					
					FFPROBE.totalLength = 0;
					
					Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
				
				// IMPORTANT get the current length of the displayed file
				if (VideoPlayerCore.videoPath != null)
				{
					//IMPORTANT RESET MEDIA
					FFPROBE.analyzedMedia = null;
					
					FFPROBE.Data(VideoPlayerCore.videoPath);
					try {
						FFPROBE.processData.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
				}

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				//NTSC framerate
				totalLength = Timecode.setNTSCtimecode(totalLength);
							
				// Formatage
				int h = (int) Math.floor(totalLength / VideoPlayerUtils.getFPS() / 3600);
				int m = (int) (Math.floor(totalLength / VideoPlayerUtils.getFPS() / 60) % 60);
				int s = (int) (Math.floor(totalLength / VideoPlayerUtils.getFPS()) % 60);   
				int f = (int) Math.floor(totalLength % VideoPlayerUtils.getFPS());

				String finalDuration;
				if (h > 0)
					finalDuration = h + "h " + m + "min " + s + "sec " + f + "i";
				else if (m > 0)
					finalDuration = m + "min " + s + "sec " + f + "i";
				else
					finalDuration = s + "sec " + f + "i";

				JOptionPane.showMessageDialog(frame,
						fileList.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + language.getProperty("totalTime")
								+ " " + finalDuration,
						language.getProperty("totalTimeFiles"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		poids.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int totalLength = 0;
				FFPROBE.totalLength = 0;
				FFPROBE.analyzedMedia = null;

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				for (String file : fileList.getSelectedValuesList())
				{					
					//IMPORTANT RESET MEDIA
					FFPROBE.analyzedMedia = null;
					
					FFPROBE.Data(file);					
					try {
						FFPROBE.processData.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}

					totalLength += Math.round((double) FFPROBE.totalLength / 1000 * FFPROBE.accurateFPS);					
					FFPROBE.totalLength = 0;
					
					Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				int finalSize = 0;
				String codec = "";
				int bitrate = 0;
				switch (comboFonctions.getSelectedItem().toString()) {
				case "DNxHD":
					codec = "DNxHD " + comboFilter.getSelectedItem().toString();
					bitrate = Integer.parseInt(comboFilter.getSelectedItem().toString().replace(" X", ""));
					break;
				case "Apple ProRes":
					codec = "Apple ProRes " + comboFilter.getSelectedItem().toString();
					switch (comboFilter.getSelectedItem().toString()) {
					case "Proxy":
						bitrate = (int) ((float) 1.52 * FFPROBE.accurateFPS);
						break;
					case "LT":
						bitrate = (int) ((float) 3.4 * FFPROBE.accurateFPS);
						break;
					case "422":
						bitrate = (int) ((float) 4.88 * FFPROBE.accurateFPS);
						break;
					case "422 HQ":
						bitrate = (int) ((float) 7.4 * FFPROBE.accurateFPS);
						break;
					case "444":
						bitrate = (int) ((float) 11 * FFPROBE.accurateFPS);
						break;
					case "4444":
						bitrate = (int) ((float) 11 * FFPROBE.accurateFPS);
						break;
					case "4444 XQ":
						bitrate = (int) ((float) 16.5 * FFPROBE.accurateFPS);
						break;
					}
					break;
				case "DNxHR":
					Object[] options = { "4K", "UHD", "2K", "HD" };
					int q = JOptionPane.showOptionDialog(frame, language.getProperty("chooseResolution"),
							language.getProperty("resolution"), JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					int resolution = 4096;
					switch (q) {
					case 1:
						resolution = 3840;
						break;
					case 2:
						resolution = 2048;
						break;
					case 3:
						resolution = 1920;
						break;
					}

					codec = "DNxHR " + comboFilter.getSelectedItem().toString();
					switch (comboFilter.getSelectedItem().toString()) {
					case "LB":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 0.7616 * 8 * FFPROBE.accurateFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 0.7148 * 8 * FFPROBE.accurateFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.1916 * 8 * FFPROBE.accurateFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.1796 * 8 * FFPROBE.accurateFPS);
							break;
						}
						break;
					case "SQ":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 2.4492 * 8 * FFPROBE.accurateFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 2.2968 * 8 * FFPROBE.accurateFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.6132 * 8 * FFPROBE.accurateFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.5744 * 8 * FFPROBE.accurateFPS);
							break;
						}
						break;
					case "HQ":
					case "HQX":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 3.7072 * 8 * FFPROBE.accurateFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 3.4728 * 8 * FFPROBE.accurateFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 0.9256 * 8 * FFPROBE.accurateFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 0.8672 * 8 * FFPROBE.accurateFPS);
							break;
						}
						break;
					case "444":
						switch (resolution) {
						case 4096:
							bitrate = (int) ((float) 7.41 * 8 * FFPROBE.accurateFPS);
							break;
						case 3840:
							bitrate = (int) ((float) 6.9492 * 8 * FFPROBE.accurateFPS);
							break;
						case 2048:
							bitrate = (int) ((float) 1.8516 * 8 * FFPROBE.accurateFPS);
							break;
						case 1920:
							bitrate = (int) ((float) 1.7384 * 8 * FFPROBE.accurateFPS);
							break;
						}
						break;
					}
					break;
				}

				finalSize = (int) (((float) totalLength / 1000) * ((float) bitrate / 8));

				String fileSize;
				if (finalSize >= 1000)
					fileSize = String.valueOf(Math.round((float) finalSize / 1024)) + " Go";
				else
					fileSize = finalSize + " Mo";
				
				// IMPORTANT get the current length of the displayed file
				if (VideoPlayerCore.videoPath != null)
				{
					//IMPORTANT RESET MEDIA
					FFPROBE.analyzedMedia = null;
					
					FFPROBE.Data(VideoPlayerCore.videoPath);
					try {
						FFPROBE.processData.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
				}

				JOptionPane.showMessageDialog(frame,
						fileList.getSelectedIndices().length + " " + language.getProperty("selectedFiles")
								+ System.lineSeparator() + System.lineSeparator() + fileSize + " "
								+ language.getProperty("to") + " " + codec,
						language.getProperty("approximativeWeight"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

		informations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Informations.frame == null)
					new Informations();
				else
					Informations.frame.setVisible(true);

				if (Informations.infoTabbedPane.getTabCount() == 0)
					Utils.changeFrameVisibility(Informations.frame, false);

				for (String item : fileList.getSelectedValuesList()) {
					MEDIAINFO.run(item, true);
				}
			}
		});

		rename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Unlock the file to be deletable
				if (VideoPlayerCore.videoPath != null)
				{
					VideoPlayerCore.videoPath = null;
					VideoPlayerUtils.preview = null;
					VideoPlayerCore.frameVideo = null;
					VideoPlayerCore.playerRepaint();
					
					// Waveform					
					if (VideoPlayerUtils.addWaveformIsRunning)
					{		
						VideoPlayerUtils.stopWaveformCreation();
						
						try {
							VideoPlayerUtils.addWaveform.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}
					}

					// Thumbnails
					if (VideoPlayerCore.thumbnails != null)
					{			
						VideoPlayerUtils.allThumbnails = null;
						VideoPlayerUtils.stopThumbnailsCreation();
					}
					
					VideoPlayerCore.playerStop();
				}

				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {
				}
				new Renamer();
				frame.setOpacity(1.0f);

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});

		inputDevice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Thread checkDevices = new Thread(new Runnable() {

					@Override
					public void run() {

						// list devices
						if (System.getProperty("os.name").contains("Mac")) {
							LibraryUtils.devices("-hide_banner -f avfoundation -list_devices true -i dummy");
						} else if (System.getProperty("os.name").contains("Windows")) {
							LibraryUtils.devices("-hide_banner -f dshow -list_devices true -i dummy" + '"');
						} else // Linux
						{
							LibraryUtils.videoDevices = new StringBuilder();
							LibraryUtils.videoDevices.append(language.getProperty("noVideo"));

							LibraryUtils.audioDevices = new StringBuilder();
							LibraryUtils.audioDevices.append(language.getProperty("noAudio"));
						}

						try {
							FFMPEG.runProcess.join();
						} catch (InterruptedException e) {
						    Thread.currentThread().interrupt();
						}

						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						new RecordInputDevice();
					}
				});
				checkDevices.start();

			}
		});

		arborescence.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				
				File source = null;
				
				SystemFileChooser fc = new SystemFileChooser();
				fc.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
				
				if (fileList.getSelectedIndices().length > 0)
				{
					fc.setCurrentDirectory(new File(fileList.getSelectedValue().toString()).getParentFile());
				}
				else
					fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

				if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
				{
					source = fc.getSelectedFile();
				}

				File destination = null;
				
				if (source != null)
				{
					fc = new SystemFileChooser();
					fc.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
					
					if (fileList.getSelectedIndices().length > 0)
					{
						fc.setCurrentDirectory(new File(fileList.getSelectedValue().toString()).getParentFile());
					}
					else
						fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

					if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
					{
						destination = fc.getSelectedFile();
					}

					if (destination != null)
					{
						if (source.toString().equals(destination.toString()))
							JOptionPane.showMessageDialog(frame, language.getProperty("sameFolders"),
									language.getProperty("copyError"), JOptionPane.ERROR_MESSAGE);
						else {
							try {
								if (System.getProperty("os.name").contains("Mac")
										|| System.getProperty("os.name").contains("Linux")) {
									ProcessBuilder rsync = new ProcessBuilder("/bin/bash", "-c",
											"rsync -a  " + source.toString().replace(" ", "\\ ") + "/ "
													+ destination.toString().replace(" ", "\\ ")
													+ " --include \\*/ --exclude \\*");
									rsync.start();
								} else
									Runtime.getRuntime().exec("cmd /c xcopy /t /e " + '"' + source.toString() + '"'
											+ " " + '"' + destination.toString() + '"');
							} catch (IOException e1) {
							}

							JOptionPane.showMessageDialog(frame, language.getProperty("copyFinished"),
									language.getProperty("treeCopy"), JOptionPane.INFORMATION_MESSAGE);
						}
					}

				}
			}
		});

		hash.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {
				}
				new HashGenerator();
			}

		});

		gop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new GOP();
			}

		});

		ftp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					frame.setOpacity(0.5f);
				} catch (Exception er) {
				}
				new Ftp();
				frame.setOpacity(1.0f);
			}
		});

		zip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				SystemFileChooser fc = new SystemFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

				if (fc.showSaveDialog(frame) == SystemFileChooser.APPROVE_OPTION)
				{
					lblCurrentEncoding.setText(language.getProperty("compression"));
					lblDestination1.setText(fc.getCurrentDirectory().toString());
					progressBar.setIndeterminate(true);

					StringBuilder items = new StringBuilder();
					for (String item : fileList.getSelectedValuesList()) {
						items.append(" " + '"' + item + '"');
					}

					UIController.disableAll();
					SEVENZIP.run("a " + '"' + fc.getSelectedFile().toString().replace(".zip", "") + ".zip" + '"' + items, true);
				}

			}
		});

		unzip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fichier = new File(fileList.getSelectedValue());
				String path = fichier.getParentFile() + "/"
						+ fichier.getName().substring(0, fichier.getName().toString().lastIndexOf("."));

				lblCurrentEncoding.setText(language.getProperty("decompression"));
				lblDestination1.setText(path);
				progressBar.setIndeterminate(true);

				UIController.disableAll();
				SEVENZIP.run("e " + '"' + fichier.toString() + '"' + " -y -o" + '"' + path + '"', true);
			}
		});

		scanListe = new JPopupMenu();
		scan = new JMenuItem(language.getProperty("menuItemStartScan"));

		scan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (scan.getText().equals(language.getProperty("menuItemStartScan"))) {
					File destination = null;
					if (list.getSize() > 0) {
						if (fileList.getSelectedIndices().length > 0)
							destination = new File(new File(fileList.getSelectedValue()).getParent());
						else
							destination = new File(new File(list.getElementAt(0)).getParent());

						list.clear();

						if (System.getProperty("os.name").contains("Mac")
								|| System.getProperty("os.name").contains("Linux"))
							list.add(0, destination + "/");
						else
							list.add(0, destination + "\\");

						scan.setText(language.getProperty("menuItemStopScan"));
						if (lblDestination1.getText().equals(language.getProperty("sameAsSource")))
							JOptionPane.showMessageDialog(Main.frame,
									Main.language.getProperty("dragFolderToDestination"),
									Main.language.getProperty("chooseDestinationFolder"),
									JOptionPane.INFORMATION_MESSAGE);
						else
							scanIsRunning = true;
					} else {
						scan.setText(language.getProperty("menuItemStopScan"));
						JOptionPane.showMessageDialog(frame, language.getProperty("dragFolderToList"),
								language.getProperty("chooseScanFolder"), JOptionPane.INFORMATION_MESSAGE);
					}

				} else {
					scan.setText(language.getProperty("menuItemStartScan"));
					btnEmptyList.doClick();
					scanIsRunning = false;

					FunctionUtils.watchFolder.setLength(0);
				}
				lblFiles.setText(Utils.filesNumber());
			}
		});

		fileList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
						&& TSMUXER.isRunning == false && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)
						&& list.getSize() > 0) {
					menuDisplay.doClick();
				}

				if (SwingUtilities.isRightMouseButton(e) || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && SwingUtilities.isLeftMouseButton(e))
				{					
					if (inputDeviceIsRunning)
					{						
						popupList.removeAll();
								
						if (FFMPEG.process.isAlive() == false && comboFonctions.getSelectedItem().toString().equals(""))
						{
							popupList.add(menuDisplay);
							popupList.show(fileList, e.getX() - 30, e.getY());
						}
					} else if (fileList.getSelectedIndices().length > 0
							&& scan.getText().equals(language.getProperty("menuItemStartScan"))) {
						if (FFMPEG.isRunning == false && BMXTRANSWRAP.isRunning == false && DVDAUTHOR.isRunning == false
								&& TSMUXER.isRunning == false) {
							// Ajout à la liste
							popupList.removeAll();
							if (comboFonctions.getSelectedItem().toString()
									.equals(language.getProperty("functionReplaceAudio")))
								popupList.add(silentTrack);
							popupList.add(menuDisplay);
							popupList.add(menuOpenFolder);
							popupList.add(scan);
							popupList.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupList.add(poids);
								break;
							}
							popupList.add(informations);
							popupList.add(rename);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(hash);
							popupList.add(gop);
							popupList.add(ftp);
							popupList.add(zip);
							popupList.add(unzip);

							// Décompression d'archives
							String firstElement = fileList.getSelectedValue();
							if (firstElement.contains(".zip") || firstElement.contains(".rar")
									|| firstElement.contains(".7z") || firstElement.contains(".iso")
									|| firstElement.contains(".tar") || firstElement.contains(".xz")
									|| firstElement.contains(".bz2") || firstElement.contains(".bzip2")
									|| firstElement.contains(".tbz2") || firstElement.contains(".tbz")
									|| firstElement.contains(".gz") || firstElement.contains(".gzip")
									|| firstElement.contains(".tgz") || firstElement.contains(".lzma")
									|| firstElement.contains(".xar") || firstElement.contains(".taz")) {
								if (fileList.getSelectedIndices().length == 1)
									unzip.setVisible(true);
								else
									unzip.setVisible(false);

								tempsTotal.setVisible(false);
								gop.setVisible(false);
							} else {
								tempsTotal.setVisible(true);
								gop.setVisible(true);
								unzip.setVisible(false);
							}

							File fileOrDirectory = new File(fileList.getSelectedValue());

							if (fileOrDirectory.isFile())
								popupList.show(fileList, e.getX() - 30, e.getY());
							else
								scanListe.show(fileList, e.getX() - 30, e.getY());
						} else {
							// Ajout à la liste
							popupList.removeAll();
							popupList.add(menuOpenFolder);
							popupList.add(tempsTotal);
							switch (comboFonctions.getSelectedItem().toString()) {
							case "DNxHD":
							case "DNxHR":
							case "Apple ProRes":
								popupList.add(poids);
								break;
							}
							popupList.add(informations);
							popupList.add(inputDevice);
							popupList.add(arborescence);
							popupList.add(hash);
							popupList.add(gop);
							popupList.show(fileList, e.getX() - 30, e.getY());
						}

					} else {
						scanListe.removeAll();
						scanListe.add(scan);
						scanListe.add(inputDevice);
						scanListe.add(arborescence);
						scanListe.add(hash);
						scanListe.show(fileList, e.getX() - 30, e.getY());
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e) && list.getSize() > 0 && fileList.getSelectedValue().equals(VideoPlayerCore.videoPath) == false)
				{
					VideoPlayerUtils.setMedia();
				}
			}

		});

		fileList.addMouseMotionListener(new MouseMotionListener() {

			int anchor = -1;

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if (anchor == -1)
					anchor = fileList.getSelectedIndex();

				fileList.setSelectionInterval(anchor, fileList.getSelectedIndex());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				anchor = -1;

			}

		});

		lblFilesEnded = new JLabel(language.getProperty("lblTermine"));
		lblFilesEnded.setFont(new Font(boldFont, Font.PLAIN, 13));
		lblFilesEnded.setForeground(Utils.themeColor);
		lblFilesEnded.setVisible(false);
		lblFilesEnded.setBounds(213, 15, 94, 16);
		grpChooseFiles.add(lblFilesEnded);

		lblFiles = new JLabel(Utils.filesNumber());
		lblFiles.setForeground(Color.WHITE);
		lblFiles.setFont(new Font(boldFont, Font.PLAIN, 13));
		lblFiles.setBounds(213, 30, 83, 16);
		grpChooseFiles.add(lblFiles);

		btnBrowse = new JButton(language.getProperty("btnBrowse"));
		btnBrowse.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnBrowse.setBounds(8, 21, 113, 21);
		grpChooseFiles.add(btnBrowse);

		btnBrowse.addActionListener(new ActionListener() {

			File defaultFolder = null;

			@Override
			public void actionPerformed(ActionEvent e) {

				SystemFileChooser fc = new SystemFileChooser();
				fc.setMultiSelectionEnabled(true);
				
				if (defaultFolder == null)
					fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

				if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
				{
				    File[] files = fc.getSelectedFiles();

					for (int i = 0; i < files.length; i++) {
						int s = files[i].getAbsolutePath().toString().lastIndexOf('.');
						String ext = files[i].getAbsolutePath().toString().substring(s);
						
						if (ext.equals(".enc"))
						{
							Utils.loadSettings(files[i]);
						}

						File file = files[i];

						// Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows")
						&& file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);

						if (file.getAbsolutePath().toString().contains("\"")
						|| file.getAbsolutePath().toString().contains("\'") || file.getName().contains("/")
						|| file.getName().contains("\\"))
						{
							if (FunctionUtils.allowsInvalidCharacters == false) {
								JOptionPane.showConfirmDialog(Main.frame,
										file.getAbsoluteFile().toString() + System.lineSeparator()
												+ Main.language.getProperty("invalidCharacter"),
										Main.language.getProperty("import"), JOptionPane.PLAIN_MESSAGE,
										JOptionPane.WARNING_MESSAGE);

								FunctionUtils.allowsInvalidCharacters = true;
							}
						}

						list.addElement(file.getAbsolutePath());
						addToList.setVisible(false);
						lblFiles.setText(Utils.filesNumber());
					}

					UIController.changeFilters();

					switch (comboFonctions.getSelectedItem().toString()) {
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
						FFPROBE.setLength();
						break;
					}

					// VideoPlayer.player
					VideoPlayerUtils.setMedia();

					defaultFolder = fc.getCurrentDirectory();
				}

			}
		});

	}
}
