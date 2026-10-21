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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.Picture;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.library.NCNN;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Scaling extends Main {

	public static void grpScaling() {

		grpResolution = new CollapsiblePanel(language.getProperty("grpResolution"), false);
		grpResolution.setLayout(null);
		grpResolution.setVisible(false);
		grpResolution.setOpaque(false);
		grpResolution.setBackground(Utils.c30);
		grpResolution.setBounds(frame.getWidth(), 30, 312, 121);
		frame.getContentPane().add(grpResolution);

		comboResolution = new JComboBox<String>();
		comboResolution.setName("comboResolution");
		comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("source"),
				"AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x", "AI animation 4x", "AI animation 2x",
				"1:2", "1:4", "1:8", "1:16",
				"3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "4096x2160", "3840x2160", "2560x1440",
				"1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "1000x1000", "854x480", "720x576",
				"640x360", "500x500", "320x180", "200x200", "100x100", "50x50" }));
		comboResolution.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboResolution.setEditable(true);
		comboResolution.setBounds(58, 18, 118, 22);
		comboResolution.setMaximumRowCount(30);
		grpResolution.add(comboResolution);

		comboResolution.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboResolution.getItemCount() > 0) // Contourne un bug lors de l'action sur le btnReset
				{
					if (comboFonctions.getSelectedItem().toString().contains("JPEG")
					|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
					{
						UIController.changeFilters();
					}
					else
					{
						if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source"))
						|| comboResolution.getSelectedItem().toString().contains("AI"))
						{
							lblPad.setVisible(false);
						}
						else
							lblPad.setVisible(true);						
						
						UIController.changeFilters();
						FFPROBE.setFilesize();
					}
					
					UIController.changeComboOptions();
				}
				
				LibraryUtils.checkGPUDeinterlacing();

				if (NCNN.isRunning && NCNN.process != null) {
					NCNN.process.destroyForcibly();

					do {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					} while (NCNN.isRunning);

					if (VideoPlayerUtils.preview != null)
						VideoPlayerUtils.preview = null;
				}

				if (FFPROBE.totalLength <= 40 || comboResolution.getSelectedItem().toString().contains("AI"))
				{
					if (VideoPlayerUtils.preview != null)
						VideoPlayerUtils.preview = null;

					VideoPlayerUtils.loadImage();
				}
				else
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame

				VideoPlayerUI.resizeAll();
				
				if (comboResolution.getSelectedItem().toString().contains("AI")) {
					iconList.setVisible(false);

					if (iconPresets.isVisible()) {
						iconPresets.setBounds(180, 45, 21, 21);
						btnCancel.setBounds(207, 46, 97, 21);
					} else {
						btnCancel.setBounds(184, 46, 120, 21);
					}

					if (RenderQueue.frame != null && RenderQueue.frame.isVisible()) {
						Utils.changeFrameVisibility(RenderQueue.frame, true);
						btnStart.setText(language.getProperty("btnStartFunction"));
					}
				} else if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionExtract")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionTranscribe")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlurFaces")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
						&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
						&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
						&& comboFonctions.getSelectedItem().equals("VMAF") == false
						&& comboFonctions.getSelectedItem().equals("FrameMD5") == false
						&& comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")) == false
						&& (RenderQueue.frame == null
								|| RenderQueue.frame != null && RenderQueue.frame.isVisible() == false)
						&& comboResolution.getSelectedItem().toString().contains("AI") == false) {
					iconList.setVisible(true);

					if (iconPresets.isVisible()) {
						iconPresets.setLocation(Main.iconList.getX() + Main.iconList.getWidth() + 2, 45);
						btnCancel.setBounds(207 + Main.iconList.getWidth(), 46,
								101 - Main.iconList.getWidth() - 4, 21);
					} else {
						iconPresets.setBounds(180, 45, 21, 21);
						btnCancel.setBounds(207, 46, 97, 21);
					}
				}
			}

		});

		comboResolution.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				if (comboFonctions.getSelectedItem().toString().contains("JPEG") == false && comboFonctions
						.getSelectedItem().toString().equals(language.getProperty("functionPicture")) == false) {
					lblPad.setVisible(true);
				}

				char caracter = e.getKeyChar();
				if (caracter != '-' && caracter != 'x' && caracter != '%' && caracter != ':' && caracter != 'a'
						&& caracter != 'u' && caracter != 't' && caracter != 'o') {
					if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
							|| String.valueOf(caracter).matches("[éèçàù]"))
						e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		caseRotate = new JCheckBox(language.getProperty("caseRotate"));
		caseRotate.setName("caseRotate");
		caseRotate.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseRotate.setBounds(7, 47, caseRotate.getPreferredSize().width, 23);
		grpResolution.add(caseRotate);

		caseRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseRotate.isSelected())
				{
					comboRotate.setEnabled(true);
				}
				else
					comboRotate.setEnabled(false);

				VideoPlayerUtils.preview = null; //Reload the preview image before rotation
				
				boolean isPlaying = VideoPlayerUtils.playerIsPlaying();
				
				VideoPlayerUI.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame
				
				if (isPlaying)
				{
					VideoPlayerUI.btnPlay.doClick();
				}
			}

		});

		comboRotate = new JComboBox<String>();
		comboRotate.setName("comboRotate");
		comboRotate.setEnabled(false);
		comboRotate.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "90", "-90", "180" }));
		comboRotate.setSelectedIndex(3);
		comboRotate.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboRotate.setEditable(false);
		comboRotate.setBounds(caseRotate.getWidth() + caseRotate.getLocation().x + 4, caseRotate.getLocation().y + 3,
				46, 16);
		comboRotate.setMaximumRowCount(20);
		grpResolution.add(comboRotate);

		comboRotate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayerUtils.preview = null; //Reload the preview image before rotation
				
				boolean isPlaying = VideoPlayerUtils.playerIsPlaying();
				
				VideoPlayerUI.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame
				
				if (isPlaying)
				{
					VideoPlayerUI.btnPlay.doClick();
				}
			}

		});

		caseMiror = new JCheckBox(language.getProperty("caseMiror"));
		caseMiror.setName("caseMiror");
		caseMiror.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseMiror.setBounds(comboRotate.getWidth() + comboRotate.getLocation().x + 6, caseRotate.getLocation().y,
				caseMiror.getPreferredSize().width, 23);
		grpResolution.add(caseMiror);

		caseMiror.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		caseForcerDAR = new JCheckBox(language.getProperty("caseForcerDAR"));
		caseForcerDAR.setName("caseForcerDAR");
		caseForcerDAR.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcerDAR.setSize(caseForcerDAR.getPreferredSize().width, 23);
		caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
		grpResolution.add(caseForcerDAR);

		caseForcerDAR.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (caseForcerDAR.isSelected())
					comboDAR.setEnabled(true);
				else
					comboDAR.setEnabled(false);

				boolean isPlaying = VideoPlayerUtils.playerIsPlaying();
				
				VideoPlayerUI.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame
				
				if (isPlaying)
				{
					VideoPlayerUI.btnPlay.doClick();
				}
			}

		});

		comboDAR = new JComboBox<String>();
		comboDAR.setName("comboDAR");
		comboDAR.setMaximumRowCount(20);
		comboDAR.setModel(new DefaultComboBoxModel<String>(
				new String[] { "1:1", "4:3", "9:16", "16:9", "21:9", "1.85", "2.35", "2.39" }));
		comboDAR.setSelectedIndex(3);
		comboDAR.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboDAR.setEditable(true);
		comboDAR.setSize(54, 16);
		comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
				caseForcerDAR.getLocation().y + 3);
		grpResolution.add(comboDAR);

		comboDAR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				boolean isPlaying = VideoPlayerUtils.playerIsPlaying();
				
				VideoPlayerUI.btnStop.doClick(); //Use VideoPlayer.resizeAll and reload the frame
				
				if (isPlaying)
				{
					VideoPlayerUI.btnPlay.doClick();
				}
			}

		});

		btnNoUpscale = new JCheckBox(Main.language.getProperty("btnNoUpscale"));
		btnNoUpscale.setName("btnNoUpscale");
		btnNoUpscale.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		btnNoUpscale.setBounds(12, caseRotate.getLocation().y + caseRotate.getHeight(),
				btnNoUpscale.getPreferredSize().width, 16);
		grpResolution.add(btnNoUpscale);

		btnNoUpscale.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
				VideoPlayerUI.resizeAll();
			}

		});

		caseCreateSequence = new JCheckBox(language.getProperty("caseCreateSequence"));
		caseCreateSequence.setName("caseCreateSequence");
		caseCreateSequence.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(),
				caseCreateSequence.getPreferredSize().width, 23);
		grpResolution.add(caseCreateSequence);

		caseCreateSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (inputDeviceIsRunning) {
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"),
							language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseCreateSequence.setSelected(false);
				}

				if (caseCreateSequence.isSelected()) {
					if (list.getSize() > 0 && inputDeviceIsRunning == false) {
						// Analyse
						FFPROBE.Data(list.firstElement().toString());
						try {
							FFPROBE.processData.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}

						comboInterpret.setSelectedItem(
								String.valueOf(FFPROBE.currentFPS).replace(".0", "").replace(".", ","));
					}

					comboInterpret.setEnabled(true);
				} else
					comboInterpret.setEnabled(false);
			}

		});

		lblInterpretation = new JLabel(Main.language.getProperty("lblInterpretation"));
		lblInterpretation.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblInterpretation.setBounds(28, caseCreateSequence.getY() + caseCreateSequence.getHeight() + 2,
				lblInterpretation.getPreferredSize().width, 16);
		grpResolution.add(lblInterpretation);

		comboInterpret = new JComboBox<String>();
		comboInterpret.setName("comboInterpret");
		comboInterpret.setEnabled(false);
		comboInterpret.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "5", "10", "15", "20", "23,98",
				"24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboInterpret.setSelectedIndex(7);
		comboInterpret.setMaximumRowCount(20);
		comboInterpret.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboInterpret.setEditable(true);
		comboInterpret.setSize(63, 16);
		comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4,
				lblInterpretation.getLocation().y);
		grpResolution.add(comboInterpret);

		lblIsInterpret = new JLabel(Main.language.getProperty("fps"));
		lblIsInterpret.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblIsInterpret.setSize(20, 16);
		lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5,
				lblInterpretation.getLocation().y - 1);
		grpResolution.add(lblIsInterpret);
		if (getLanguage.equals(Locale.of("ru").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("uk").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("vi").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("id").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("ro").getDisplayLanguage())) {
			lblIsInterpret.setVisible(false);
		}

		iconTVInterpret = new JLabel(new FlatSVGIcon("resources/preview.svg", 16, 16));
		iconTVInterpret.setHorizontalAlignment(SwingConstants.CENTER);
		iconTVInterpret.setBounds(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1, lblIsInterpret.getY() + 1, 16, 16);
		iconTVInterpret.setToolTipText(language.getProperty("preview"));
		grpResolution.add(iconTVInterpret);

		iconTVInterpret.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Définition de la taille
				if (list.getSize() > 0) {

					String file = "";
					// Fichiers sélectionnés ?
					if (fileList.getSelectedIndices().length > 0) {
						if (scanIsRunning) {
							File dir = new File(Main.list.firstElement());
							for (File f : dir.listFiles()) {
								if (f.isHidden() == false && f.isFile()) {
									file = f.toString();
									break;
								}
							}
						} else
							file = fileList.getSelectedValue().toString();

					} else
						file = list.firstElement();

					// Analyse
					FFPROBE.Data(file.toString());
					try {
						FFPROBE.processData.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}

					// FFMPEGTOFFPLAY
					FFMPEG.toFFPLAY(" -r "
							+ (float) FFPROBE.currentFPS
									/ Float.parseFloat(comboInterpret.getSelectedItem().toString().replace(",", "."))
							+ " -hwaccel "
							+ Main.comboGPUDecoding.getSelectedItem().toString()
									.replace(Main.language.getProperty("aucun"), "none")
							+ " -v quiet -i " + '"' + file + '"'
							+ " -vf scale=1000:-1:scaler=bilinear:sws_dither=none -r 1 -c:v rawvideo -map v:0 -an -f nut pipe:1");
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconTVInterpret.setIcon(new FlatSVGIcon("resources/preview.svg", 16, 16).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconTVInterpret.setIcon(new FlatSVGIcon("resources/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		lblImageSize = new JLabel(Main.language.getProperty("lblBitrateSize"));
		lblImageSize.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblImageSize.setBounds(12, 20, 48, 16);
		grpResolution.add(lblImageSize);

		lblScreenshot = new JLabel(new FlatSVGIcon("resources/screenshot.svg", 16, 16));
		lblScreenshot.setHorizontalAlignment(SwingConstants.CENTER);
		lblScreenshot.setBounds(comboResolution.getX() + comboResolution.getWidth() + 9, 21, 16, 16);
		grpResolution.add(lblScreenshot);

		lblScreenshot.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Picture.main(true, true);

				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				{
					Main.btnStart.setText(Main.language.getProperty("btnAddToRender"));
				}
				else
					Main.btnStart.setText(Main.language.getProperty("btnStartFunction"));
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		lblImageQuality = new JLabel(language.getProperty("lblQualit"));
		lblImageQuality.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblImageQuality.setBounds(comboResolution.getX() + comboResolution.getWidth() + 6, comboResolution.getY() + 3,
				lblImageQuality.getPreferredSize().width + 4, 16);
		grpResolution.add(lblImageQuality);

		comboImageOption = new JComboBox<String>();
		comboImageOption.setName("comboImageOption");
		comboImageOption.setModel(
				new DefaultComboBoxModel<String>(new String[] { "100%", "95%", "90%", "85%", "80%", "75%", "70%", "65%",
						"60%", "55%", "50%", "45%", "40%", "35%", "30%", "25%", "20%", "15%", "10%", "5%", "0%" }));
		comboImageOption.setSelectedIndex(0);
		comboImageOption.setMaximumRowCount(20);
		comboImageOption.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboImageOption.setEditable(false);
		comboImageOption.setSize(90, 16);
		comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(),
				lblImageQuality.getLocation().y);
		grpResolution.add(comboImageOption);
	}
}
