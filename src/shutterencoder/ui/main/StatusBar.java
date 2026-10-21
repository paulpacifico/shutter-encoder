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
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import shutterencoder.library.Bmxtranswrap;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.Ytdlp;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class StatusBar extends Main {

	public static void statusBar() {

		statusBar = new JPanel();
		statusBar.setName("statusBar");
		statusBar.setBackground(new Color(35, 35, 40));
		statusBar.setOpaque(true);
		statusBar.setLayout(null);
		statusBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(45, 45, 45)));
		statusBar.setBounds(0, frame.getHeight() - 23, extendedWidth, 22);

		statusBar.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				windowDrag = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (VideoPlayerCore.setTime != null && VideoPlayerUI.isPiping == false)
				{
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload
				}

				windowDrag = false;

				// IMPORTANT
				if (FFprobe.totalLength <= 40 && VideoPlayerUtils.preview != null)
				{
					VideoPlayerUtils.preview = null;
				}

				UIController.resizeAll(frame.getWidth(), 0);
			}

		});

		statusBar.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				int i = e.getY() - 10;

				if (frame.getSize().getHeight() + i < 650)
					i = 0;

				int width = e.getX() + 10;

				if (windowDrag) {
					UIController.resizeAll(width, i);
				}

			}

			@Override
			public void mouseMoved(MouseEvent e) {

				if (MouseInfo.getPointerInfo().getLocation().x
						- Main.frame.getLocation().x > Main.frame.getSize().width - 20) {
					Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				} else if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height
						- 20) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				} else {
					if (Main.windowDrag == false && Main.frame.getCursor()
							.equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)) == false) {
						Main.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});

		lblBy = new JLabel(language.getProperty("lblCrParPaul"));
		lblBy.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblBy.setForeground(Color.WHITE);
		lblBy.setBounds(6, 4, lblBy.getPreferredSize().width, 15);
		statusBar.add(lblBy);

		lblBy.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				String PathToGame = Utils.getJavaPath().getParent().getParent() + "/lib/Game.jar";
				
				try {
					ProcessBuilder processGame;
					if (System.getProperty("os.name").contains("Windows")) {
						processGame = new ProcessBuilder('"' + Utils.getJavaPath().toString() + '"' + " -jar " + '"' + PathToGame + '"');
					} else {
						processGame = new ProcessBuilder("/bin/bash", "-c", '"' + Utils.getJavaPath().toString() + '"' + " -jar " + '"' + PathToGame + '"');
					}

					processGame.start();
				} catch (Exception e) {
					System.out.println(e);
				}

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		lblBy.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				lblElapsedTime.setLocation(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY());
				lblRemainingTime.setLocation(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY());
			}

			public void componentHidden(ComponentEvent e) {
				lblElapsedTime.setLocation(lblBy.getLocation());
				lblRemainingTime.setLocation(lblBy.getLocation());
			}
		});

		lblGpuDecoding = new JLabel(Main.language.getProperty("lblGpuDecoding"));
		lblGpuDecoding.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblGpuDecoding.setBounds(750, lblBy.getY(), lblGpuDecoding.getPreferredSize().width, 15);
		statusBar.add(lblGpuDecoding);
		
		// GPU decoding
		if (System.getProperty("os.name").contains("Windows"))
		{
			LibraryUtils.checkHWaccel("-hwaccels" + '"');
		}
		else
			LibraryUtils.checkHWaccel("-hwaccels");

		comboGPUDecoding = new JComboBox<String>(LibraryUtils.hwaccels.toString().split(System.lineSeparator()));
		comboGPUDecoding.setName("comboGPUDecoding");
		comboGPUDecoding.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboGPUDecoding.setEditable(false);
		
		if (System.getProperty("os.name").contains("Linux"))
		{
			comboGPUDecoding.setSelectedItem(language.getProperty("aucun"));
		}
		else
			comboGPUDecoding.setSelectedItem("auto");
		
		comboGPUDecoding.setBounds(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6, lblGpuDecoding.getLocation().y - 1, comboGPUDecoding.getPreferredSize().width, 16);
		comboGPUDecoding.setMaximumRowCount(10);
		comboGPUDecoding.setVisible(true);
		statusBar.add(comboGPUDecoding);

		comboGPUDecoding.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac"))
				{
					if (comboGPUDecoding.getSelectedItem().equals("auto"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "auto", language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("qsv"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "qsv", language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("cuda"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "cuda", language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("vulkan"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "vulkan", language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(true);
					}
					else if (comboGPUDecoding.getSelectedItem().equals("videotoolbox"))
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { "videotoolbox", language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(true);
					}
					else
					{
						comboGPUFilter.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("aucun") }));
						comboGPUFilter.setEnabled(false);
					}
					
					VideoPlayerUI.frameIsComplete = false;
					
					if (VideoPlayerCore.videoPath != null)
						LibraryUtils.checkGPUCapabilities(VideoPlayerCore.videoPath);
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);			
				}
			}

		});
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for (int i = 0 ; i < LibraryUtils.multiGPU + 1 ; i++)
		{
			model.addElement("GPU #" + i);
		}
		
		comboSelectedGPU = new JComboBox<String>(model);
		comboSelectedGPU.setName("comboSelectedGPU");
		comboSelectedGPU.setMaximumRowCount(20);
		comboSelectedGPU.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboSelectedGPU.setEditable(false);
		comboSelectedGPU.setVisible(false);
		comboSelectedGPU.setBounds(lblGpuDecoding.getLocation().x - comboSelectedGPU.getWidth() - 6, comboSelectedGPU.getY(), 60, 16);	
		statusBar.add(comboSelectedGPU);

		lblGpuFiltering = new JLabel(Main.language.getProperty("lblGpuFiltering"));
		lblGpuFiltering.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblGpuFiltering.setBounds(comboGPUDecoding.getLocation().x + comboGPUDecoding.getWidth() + 6, lblBy.getY(), lblGpuFiltering.getPreferredSize().width, 15);
		statusBar.add(lblGpuFiltering);

		comboGPUFilter = new JComboBox<String>(new String[] { "auto", language.getProperty("aucun") });
		comboGPUFilter.setName("comboGPUFilter");
		comboGPUFilter.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboGPUFilter.setEditable(false);
		comboGPUFilter.setVisible(true);
		comboGPUFilter.setBounds(lblGpuFiltering.getX() + lblGpuFiltering.getWidth() + 6, comboGPUDecoding.getY(), comboGPUDecoding.getPreferredSize().width, 16);
		comboGPUFilter.setMaximumRowCount(10);

		if ((System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) && comboGPUDecoding.getSelectedItem().equals(language.getProperty("aucun")) == false)
		{
			comboGPUFilter.setSelectedItem("auto");
			comboGPUFilter.setEnabled(true);
		}
		else
		{
			comboGPUFilter.setSelectedItem(language.getProperty("aucun"));
			comboGPUFilter.setEnabled(false);
		}
		statusBar.add(comboGPUFilter);

		comboGPUFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				VideoPlayerUI.frameIsComplete = false;
				
				if (VideoPlayerCore.videoPath != null)
					LibraryUtils.checkGPUCapabilities(VideoPlayerCore.videoPath);
				
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				
			}

		});

		lblHWaccel = new JLabel(language.getProperty("caseAccel"));
		lblHWaccel.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblHWaccel.setVisible(false);
		lblHWaccel.setBounds(comboGPUFilter.getX() + comboGPUFilter.getWidth() + 6, lblBy.getY(),
				lblHWaccel.getPreferredSize().width, 15);
		statusBar.add(lblHWaccel);

		comboAccel = new JComboBox<String>();
		comboAccel.setName("comboAccel");
		comboAccel.setMaximumRowCount(20);
		comboAccel.setVisible(false);
		comboAccel.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("aucune").toLowerCase() }));
		comboAccel.setSelectedIndex(0);
		comboAccel.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAccel.setEditable(false);
		comboAccel.setBounds(lblHWaccel.getLocation().x + lblHWaccel.getWidth() + 4, comboGPUDecoding.getY(), 115, 16);
		statusBar.add(comboAccel);
				
		comboAccel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
				{
					if ("H.264".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high" }));
						comboForceProfile.setSelectedIndex(2);
					}
					else
					{					
						if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
						{
							comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main", "main422" }));
						}
						else
							comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main" }));

						comboForceProfile.setSelectedIndex(0);

						if (comboFonctions.getSelectedItem().equals("H.265") && comboAccel.getSelectedItem().equals("Intel Quick Sync"))
						{
							comboForceLevel.setVisible(false);
						}
					}

					if (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Intel Quick Sync"))
					{
						if (comboForcePreset.getModel().getSize() != 7)
						{
							comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "veryfast",
									"faster", "fast", "medium", "slow", "slower", "veryslow" }));
							comboForcePreset.setSelectedIndex(3);
						}
						
					}
					else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
					{
						if (comboForcePreset.getModel().getSize() != 4)
						{
							comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "speed", "balanced", "quality", "high_quality" }));
							comboForcePreset.setSelectedIndex(2);
						}	
					}
					else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox")
					|| comboAccel.getSelectedItem().equals("Vulkan Video"))
					{
						caseForcePreset.setSelected(false);
						caseForcePreset.setEnabled(false);
						comboForcePreset.setEnabled(false);
					}

					if (comboAccel.getSelectedItem().equals("OSX VideoToolbox"))
					{
						caseQMax.setEnabled(false);
						caseQMax.setSelected(false);
					}
					else
					{
						caseAlpha.setEnabled(false);
						caseAlpha.setSelected(false);
					}
					
					if (lblVBR.getText().equals("CBR")
							|| lblVBR.getText().equals("CQ") && comboAccel.getSelectedItem().equals("OSX VideoToolbox")
									&& System.getProperty("os.arch").equals("amd64")) {
						lblVBR.setText("VBR");
						debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000",
								"25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500",
								"1000", "500", language.getProperty("lblBest").toLowerCase(),
								language.getProperty("lblGood").toLowerCase(), "auto" }));
						debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
						lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
						lblKbsH264.setVisible(true);
						h264lines.setVisible(true);
						if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
							case2pass.setEnabled(true);

						FFprobe.setLength();
					}
					
					if (lblVBR.getText().equals("CQ") && comboFonctions.getSelectedItem().toString().equals("AV1"))
					{
						String[] values = new String[257];					
						if (System.getProperty("os.name").contains("Windows") && debitVideo.getItemCount() != 257)
						{
							values[0] = language.getProperty("lblBest");
							for (int i = 1; i < 256; i++) {
								values[i] = String.valueOf(i);
							}
							values[256] = language.getProperty("lblWorst");
							
							debitVideo.setModel(new DefaultComboBoxModel<String>(values));
							debitVideo.setSelectedIndex(127);
						}
					}

					caseAlpha.setEnabled(true);
					caseForcerEntrelacement.setSelected(false);
					caseForcerEntrelacement.setEnabled(false);
					
					String function = comboFonctions.getSelectedItem().toString();
					if (("H.264".equals(function) || "H.265".equals(function) || "AV1".equals(function)) && comboAccel.isEnabled()
					&& (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Vulkan Video")))
					{
						caseForceTune.setEnabled(true);
					}
					else
					{
						caseForceTune.setSelected(false);
						caseForceTune.setEnabled(false);
						comboForceTune.setEnabled(false);
					}
					
					case2pass.setSelected(false);
					case2pass.setEnabled(false);
				}
				else
				{
					comboForceLevel.setVisible(true);

					if ("H.264".equals(comboFonctions.getSelectedItem().toString()))
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high", "high422", "high444" }));
						comboForceProfile.setSelectedIndex(2);
					}
					else
					{
						comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main", "main422", "main444" }));
						comboForceProfile.setSelectedIndex(0);
					}

					if (("H.264".equals(comboFonctions.getSelectedItem().toString()) || "H.265".equals(comboFonctions.getSelectedItem().toString())) && comboForcePreset.getModel().getSize() != 10)
					{
						comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast", "medium", "slow", "slower", "veryslow", "placebo" }));
						comboForcePreset.setSelectedIndex(5);
					}
					else if ("H.266".equals(comboFonctions.getSelectedItem().toString()) && comboForcePreset.getModel().getSize() != 5)
					{
						comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "faster", "fast", "medium", "slow", "slower" }));
						comboForcePreset.setSelectedIndex(2);
					}
					
					if (lblVBR.getText().equals("CQ") && debitVideo.getItemCount() != 53)
					{
						String[] values = new String[53];
						values[0] = language.getProperty("lblBest");
						for (int i = 1; i < 52; i++) {
							values[i] = String.valueOf(i);
						}
						values[52] = language.getProperty("lblWorst");
						
						debitVideo.setModel(new DefaultComboBoxModel<String>(values));
						debitVideo.setSelectedIndex(23);
					}
					
					caseForcerEntrelacement.setEnabled(true);

					lblVBR.setVisible(true);

					caseQMax.setEnabled(true);

					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);

					caseForceTune.setEnabled(true);

					if (lblVBR.getText().equals("CQ") == false)
						case2pass.setEnabled(true);

					caseForcerEntrelacement.setEnabled(true);

					lblVBR.setVisible(true);

					if (caseQMax.isSelected() == false)
						caseForcePreset.setEnabled(true);

					caseForceTune.setEnabled(true);
				}
				
				VideoPlayerUI.frameIsComplete = false;
				
				if (VideoPlayerCore.videoPath != null)
					LibraryUtils.checkGPUCapabilities(VideoPlayerCore.videoPath);
				
				if (comboAccel.hasFocus())
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

				UIController.changeSections(false);

				if ("Apple ProRes".equals(comboFonctions.getSelectedItem().toString())
						&& System.getProperty("os.name").contains("Mac") && arch.equals("arm64")) {
					UIController.changeFilters();
				}
			}

		});

		lblRemainingTime = new JLabel(language.getProperty("tempsRestant"));
		lblRemainingTime.setVisible(false);
		lblRemainingTime.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblRemainingTime.setForeground(Color.WHITE);
		lblRemainingTime.setBounds(lblBy.getX() + lblBy.getWidth() + 10, lblBy.getY(),
				lblRemainingTime.getPreferredSize().width, 15);
		statusBar.add(lblRemainingTime);

		lblRemainingTime.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				lblElapsedTime.setVisible(false);
			}

			public void componentHidden(ComponentEvent e) {
				if (lblElapsedTime != null && lblElapsedTime.getText().equals(language.getProperty("tempsEcoule")) == false) {
					if (FFmpeg.isRunning == false && Ytdlp.isRunning == false) {
						lblBy.setVisible(true);
					}
					lblElapsedTime.setVisible(true);
				}
			}
		});

		lblRemainingTime.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				lblRemainingTime.setVisible(false);

				if (lblElapsedTime != null && lblElapsedTime.getText().equals(language.getProperty("tempsEcoule")) == false) {
					lblElapsedTime.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

				if (caseDisplay.isSelected() == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		lblElapsedTime = new JLabel(language.getProperty("tempsEcoule"));
		lblElapsedTime.setVisible(false);
		lblElapsedTime.setForeground(Color.WHITE);
		lblElapsedTime.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblElapsedTime.setBounds(lblRemainingTime.getX(), lblBy.getY(), lblElapsedTime.getPreferredSize().width, 15);
		statusBar.add(lblElapsedTime);

		lblElapsedTime.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (FFmpeg.isRunning || Bmxtranswrap.isRunning) {
					lblElapsedTime.setVisible(false);
					lblRemainingTime.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (FFmpeg.isRunning || Bmxtranswrap.isRunning) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		lblYears = new JLabel("2013-2026");
		lblYears.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYears.setForeground(Color.WHITE);
		lblYears.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblYears.setBounds(frame.getWidth() - lblYears.getPreferredSize().width - 10, lblBy.getY(), lblYears.getPreferredSize().width + 4, 15);
		statusBar.add(lblYears);

		lblYears.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				final JFrame oldVersions = new JFrame();
				oldVersions.setUndecorated(true);
				oldVersions.getContentPane().setBackground(Utils.c30);
				oldVersions.setBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f));
				oldVersions.setSize(1600, 423);
				oldVersions.setShape(new AntiAliasedRoundRectangle(0, 0, oldVersions.getWidth() + 18,
						oldVersions.getHeight(), 15, 15));
				
				if (System.getProperty("os.name").contains("Mac") == false)
					oldVersions.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/icon.png")).getImage());
				
				oldVersions.setLocation(frame.getX() + (frame.getWidth() - oldVersions.getWidth()) / 2, frame.getY() + (frame.getHeight() - oldVersions.getHeight()) / 2);
				oldVersions.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				oldVersions.setTitle("2013-2017");
				oldVersions.getContentPane().setLayout(null);
				oldVersions.setResizable(false);
				oldVersions.setVisible(true);
				oldVersions.setAlwaysOnTop(true);

				oldVersions.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
							oldVersions.dispose();
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
					}

					@Override
					public void keyTyped(KeyEvent e) {
					}

				});

				JLabel image = new JLabel(
						new ImageIcon(getClass().getClassLoader().getResource("resources/2013-2016.jpg")));
				image.setSize(1600, 423);
				image.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						oldVersions.dispose();
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
					}

					@Override
					public void mousePressed(MouseEvent down) {
						MousePositionX = down.getPoint().x;
						MousePositionY = down.getPoint().y;
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}

				});

				image.addMouseMotionListener(new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent e) {
						oldVersions.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX,
								MouseInfo.getPointerInfo().getLocation().y - MousePositionY);
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
					}

				});

				oldVersions.getContentPane().add(image);
			}

			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}

		});

		frame.getContentPane().add(statusBar);

	}
}
