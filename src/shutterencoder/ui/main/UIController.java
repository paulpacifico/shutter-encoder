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
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.settings.FunctionUtils;
import shutterencoder.library.FFMPEG;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.Ftp;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;
import shutterencoder.utils.Utils;

public class UIController extends Shutter {

	public static boolean extendSectionsIsRunning = false;	
	
	public static void changeFunction(final boolean anim) {

		String function = comboFonctions.getSelectedItem().toString();
		
		if (language.getProperty("functionSubtitles").equals(function)) //Width need to be loaded after
		{
			changeSections(anim);
		}
		else if (language.getProperty("functionConform").equals(function)				
			|| language.getProperty("functionCut").equals(function)
			|| language.getProperty("functionRewrap").equals(function)
			|| language.getProperty("functionMerge").equals(function)
			|| language.getProperty("functionReplaceAudio").equals(function) 
			|| language.getProperty("functionSeparation").equals(function)
			|| language.getProperty("functionTranscribe").equals(function)
			|| "WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function) || "ALAC".equals(function)
			|| "MP3".equals(function) || "AAC".equals(function) || "AC3".equals(function) || "Opus".equals(function)
			|| "Vorbis".equals(function) || "Dolby Digital Plus".equals(function) || "Dolby TrueHD".equals(function)
			|| "Loudness & True Peak".equals(function)
			|| language.getProperty("functionBlackDetection").equals(function)
			|| language.getProperty("functionOfflineDetection").equals(function) || "VMAF".equals(function)
			|| "FrameMD5".equals(function) || "DNxHD".equals(function) || "DNxHR".equals(function)
			|| "Apple ProRes".equals(function) || "QT Animation".equals(function)
			|| "GoPro CineForm".equals(function) || "Uncompressed".equals(function) || "H.264".equals(function)
			|| "H.265".equals(function) || "H.266".equals(function) || "DV".equals(function)
			|| "WMV".equals(function) || "MPEG-1".equals(function) || "MPEG-2".equals(function)
			|| "VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function)
			|| "Theora".equals(function) || "MJPEG".equals(function) || "Xvid".equals(function)
			|| "XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function) || "AVC-Intra 100".equals(function)
			|| "XAVC".equals(function) || "XAVC Long GOP".equals(function) || "HAP".equals(function) || "FFV1".equals(function)
			|| "DVD".equals(function) || "Blu-ray".equals(function) || "QT JPEG".equals(function)
			|| language.getProperty("functionPicture").equals(function) || "JPEG".equals(function)
			|| "JPEG XL".equals(function) || language.getProperty("functionNormalization").equals(function)) {
			changeWidth(true);
			changeSections(anim);

		} else if (language.getProperty("functionExtract").equals(function)
				|| language.getProperty("functionInsert").equals(function)				
				|| language.getProperty("functionTranslate").equals(function)
				|| language.getProperty("functionColorize").equals(function)
				|| language.getProperty("functionBlurFaces").equals(function)
				|| language.getProperty("functionBackgroundRemover").equals(function)
				|| "CD RIP".equals(function) || "DVD Rip".equals(function)
				|| language.getProperty("functionSceneDetection").equals(function)
				|| language.getProperty("functionWeb").equals(function)) {
			changeWidth(false);
			changeSections(anim);
		}

		// Render queue
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionExtract")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSeparation")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionTranscribe")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlurFaces")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionTranslate")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionColorize")) == false
		&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBackgroundRemover")) == false
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
		&& (RenderQueue.frame == null || RenderQueue.frame != null && RenderQueue.frame.isVisible() == false)
		&& comboResolution.getSelectedItem().toString().contains("AI") == false) {
			iconList.setVisible(true);

			if (iconPresets.isVisible()) {
				iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
				btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() - 4, 21);
			} else {
				iconPresets.setBounds(180, 45, 21, 21);
				btnCancel.setBounds(207, 46, 97, 21);
			}
		} else {
			iconList.setVisible(false);

			if (iconPresets.isVisible()) {
				iconPresets.setBounds(180, 45, 21, 21);
				btnCancel.setBounds(207, 46, 97, 21);
			} else {
				btnCancel.setBounds(184, 46, 120, 21);
			}
		}

		// Case Conform
		if (caseConform.isSelected()) {
			comboConform.setEnabled(true);
			comboFPS.setEnabled(true);
		} else {
			comboConform.setEnabled(false);
			comboFPS.setEnabled(false);
		}

		// Case Convert FPS Audio
		if (caseConvertAudioFramerate.isSelected()) {
			comboAudioIn.setEnabled(true);
			comboAudioOut.setEnabled(true);
		} else {
			comboAudioIn.setEnabled(false);
			comboAudioOut.setEnabled(false);
		}

		// Désactivation du grpChooseFiles
		Component[] components = grpChooseFiles.getComponents();
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP")) {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(false);
		} else {
			for (int i = 0; i < components.length; i++)
				components[i].setEnabled(true);
		}

		// Modification des cases par rapport aux fonctions
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
				|| comboFonctions.getSelectedItem().toString().equals("CD RIP") || inputDeviceIsRunning) {
			if (caseChangeFolder1.isSelected() == false) {

				if (System.getProperty("os.name").contains("Windows")) {
					if (Settings.lblDestination1.getText() != ""
							&& new File(Settings.lblDestination1.getText()).exists())
						lblDestination1.setText(Settings.lblDestination1.getText());
					else
						lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
				} else {
					if (Settings.lblDestination1.getText() != ""
							&& new File(Settings.lblDestination1.getText()).exists())
						lblDestination1.setText(Settings.lblDestination1.getText());
					else
						lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
				}

			}
		} else {
			if (caseChangeFolder1.isSelected() == false)
				lblDestination1.setText(language.getProperty("sameAsSource"));
		}

		// Modifications du statut des cases
		if (comboFonctions.getSelectedItem().equals("Loudness & True Peak")
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection"))
		|| comboFonctions.getSelectedItem().equals("VMAF")
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSeparation"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles"))
		|| comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))) {

			caseOpenFolderAtEnd1.setEnabled(false);
			caseChangeFolder1.setEnabled(false);
			lblDestination1.setVisible(false);

			casePrefix.setEnabled(false);
			txtPrefix.setEnabled(false);
			btnExtension.setEnabled(false);
			txtExtension.setEnabled(false);
			caseSubFolder.setEnabled(false);
			txtSubFolder.setEnabled(false);
			caseDeleteSourceFile.setEnabled(false);
			caseDeleteSourceFile.setSelected(false);
			caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());

			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")))
			{
				setDestinationTabs(5);
			}
			else
			{
				if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
				|| comboFonctions.getSelectedItem().equals("Loudness & True Peak")
				|| comboFonctions.getSelectedItem().equals(language.getProperty("functionSeparation"))
				|| comboFonctions.getSelectedItem().equals("VMAF"))
				{
					caseOpenFolderAtEnd1.setEnabled(true);
					caseChangeFolder1.setEnabled(true);
					lblDestination1.setVisible(true);

					casePrefix.setEnabled(true);
					txtPrefix.setEnabled(true);
					btnExtension.setEnabled(true);
					txtExtension.setEnabled(true);
					caseSubFolder.setEnabled(true);
					txtSubFolder.setEnabled(true);

					setDestinationTabs(2);
				} else
					setDestinationTabs(1);
			}

		} else {
			if (((comboFonctions.getSelectedItem().toString().equals("DNxHD")
					|| (comboFonctions.getSelectedItem().toString().equals("DNxHR"))) && caseCreateOPATOM.isSelected())
					|| caseCreateTree.isSelected()) // OP-Atom
			{
				setDestinationTabs(2);
			} else if (comboFonctions.getSelectedItem().toString().equals("H.264")) {
				setDestinationTabs(6);
			} else
				setDestinationTabs(5);

			caseOpenFolderAtEnd1.setEnabled(true);
			caseChangeFolder1.setEnabled(true);
			lblDestination1.setVisible(true);

			if (comboFonctions.getSelectedItem().toString().equals("DVD")
					|| comboFonctions.getSelectedItem().toString().equals("Blu-ray")) {
				casePrefix.setEnabled(false);
				txtPrefix.setEnabled(false);
				btnExtension.setEnabled(false);
				txtExtension.setEnabled(false);
				caseSubFolder.setEnabled(false);
				txtSubFolder.setEnabled(false);
				caseDeleteSourceFile.setEnabled(false);
				caseDeleteSourceFile.setSelected(false);
				caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
			} else {
				casePrefix.setEnabled(true);
				txtPrefix.setEnabled(true);
				btnExtension.setEnabled(true);
				txtExtension.setEnabled(true);
				caseSubFolder.setEnabled(true);
				txtSubFolder.setEnabled(true);
				caseDeleteSourceFile.setEnabled(true);
			}
		}

		if (casePrefix.isSelected() == false)
			txtPrefix.setEnabled(false);

		if (btnExtension.isSelected() == false)
			txtExtension.setEnabled(false);

		if (caseSubFolder.isSelected() == false)
			txtSubFolder.setEnabled(false);

		// btnStart text
		if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb")))
		{
			btnStart.setText(language.getProperty("btnDownload"));
		}
		else if (RenderQueue.frame != null)
		{
			if (RenderQueue.frame.isVisible()
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionMerge")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionInsert")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSubtitles")) == false
				&& comboFonctions.getSelectedItem().equals("DVD Rip") == false
				&& comboFonctions.getSelectedItem().equals("Loudness & True Peak") == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionNormalization")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionSceneDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionBlackDetection")) == false
				&& comboFonctions.getSelectedItem().equals(language.getProperty("functionOfflineDetection")) == false
				&& comboFonctions.getSelectedItem().equals("VMAF") == false
				&& comboFonctions.getSelectedItem().equals("FrameMD5") == false) {
				btnStart.setText(language.getProperty("btnAddToRender"));
			} else {
				if (FFMPEG.isRunning == false)
					btnStart.setText(language.getProperty("btnStartFunction"));
			}
		} else {
			if (FFMPEG.isRunning == false)
				btnStart.setText(language.getProperty("btnStartFunction"));
		}

		// caseForcerDesentrelacement
		if (caseForcerDesentrelacement.isSelected())
			comboForcerDesentrelacement.setEnabled(true);
		else
			comboForcerDesentrelacement.setEnabled(false);

		// caseForceDar
		if (caseForcerDAR.isSelected())
			comboDAR.setEnabled(true);
		else
			comboDAR.setEnabled(false);

		// Case Accélération
		if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
				&& (comboFonctions.getSelectedItem().toString().contains("H.26")
						|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes"))) {
			// Nothing
		} else {
			caseForcerEntrelacement.setEnabled(true);
		}

		if (caseForceLevel.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26")
				|| comboFonctions.getSelectedItem().toString().equals("AV1"))) {
			caseForceLevel.setEnabled(true);
			comboForceProfile.setEnabled(true);
			comboForceLevel.setEnabled(true);
		} else {
			comboForceProfile.setEnabled(false);
			comboForceLevel.setEnabled(false);
		}

		if (caseForcePreset.isSelected() && comboFonctions.getSelectedItem().toString().contains("H.26")
				&& caseQMax.isSelected() == false) {
			caseForcePreset.setEnabled(true);
			comboForcePreset.setEnabled(true);
		} else
			comboForcePreset.setEnabled(false);

		if (caseForceTune.isSelected() && (comboFonctions.getSelectedItem().toString().contains("H.26")
				|| comboFonctions.getSelectedItem().toString().equals("VP8")
				|| comboFonctions.getSelectedItem().toString().equals("VP9")
				|| comboFonctions.getSelectedItem().toString().contains("AV1"))) {
			caseForceTune.setEnabled(true);
			comboForceTune.setEnabled(true);
		} else
			comboForceTune.setEnabled(false);

		if (caseForceQuality.isSelected()
				&& (comboFonctions.getSelectedItem().toString().equals("VP8")
						|| comboFonctions.getSelectedItem().toString().equals("VP9"))
				&& caseQMax.isSelected() == false) {
			caseForceQuality.setEnabled(true);
			comboForceQuality.setEnabled(true);
		} else
			comboForceQuality.setEnabled(false);

		if (caseForceSpeed.isSelected() && caseQMax.isSelected() == false
				&& (comboFonctions.getSelectedItem().toString().equals("VP8")
						|| comboFonctions.getSelectedItem().toString().equals("VP9")
						|| comboFonctions.getSelectedItem().toString().equals("AV1"))) {
			caseForceSpeed.setEnabled(true);
			comboForceSpeed.setEnabled(true);
		} else
			comboForceSpeed.setEnabled(false);
		
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")))
		{
			comboSubsSource.setEnabled(false);
			comboSubsSource.setSelectedIndex(0);
		}
		else
			comboSubsSource.setEnabled(true);
	}

	public static void changeWidth(final boolean bigger) {

		if (VideoPlayerCore.loadMedia != null && VideoPlayerCore.loadMedia.isAlive())
		{
			while (VideoPlayerCore.loadMedia.isAlive())
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
			}
		}
		
		String function = comboFonctions.getSelectedItem().toString();

		noSettings = false;

		boolean noVideoPlayer = false;
		if (Settings.btnDisableVideoPlayer.isSelected() && function.isEmpty() == false && bigger)
			noVideoPlayer = true;

		boolean forceFullSize = false;
		if (caseAddWatermark.isSelected() || caseAddTimecode.isSelected() || caseShowTimecode.isSelected()
				|| caseAddText.isSelected() || caseShowFileName.isSelected()) {
			forceFullSize = true;
		}

		if (bigger == false && FFMPEG.isRunning && caseDisplay.isSelected() == false && forceFullSize == false) {
			noSettings = true;

			frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2, frame.getY() + (frame.getHeight() - minHeight) / 2, 332, minHeight);
			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);

			lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
			lblYears.setVisible(false);
		} else if (language.getProperty("functionConform").equals(function) || "DV".equals(function)
				|| language.getProperty("functionSubtitles").equals(function) || "Loudness & True Peak".equals(function)
				|| language.getProperty("functionBlackDetection").equals(function)
				|| language.getProperty("functionOfflineDetection").equals(function)
				|| language.getProperty("functionSeparation").equals(function)
				|| language.getProperty("functionTranscribe").equals(function)
				|| "VMAF".equals(function)) {
			noSettings = true;

			if (Settings.btnDisableVideoPlayer.isSelected()) {
				frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2,
						frame.getY() + (frame.getHeight() - minHeight) / 2, 332, minHeight);
				lblArrows.setVisible(true);
				lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
				lblGpuDecoding.setVisible(false);
				comboGPUDecoding.setVisible(false);
				lblGpuFiltering.setVisible(false);
				comboGPUFilter.setVisible(false);

				lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
				lblYears.setVisible(false);
			} else {
				if (frame.getSize().width == 332) {
					frame.setBounds(frame.getX() - (1350 - 312 - 332) / 2, frame.getY(), 1350 - 312, frame.getHeight());
				} else if (frame.getSize().width == 654) {
					frame.setBounds(frame.getX() - (1350 - 312 - 654) / 2, frame.getY(), 1350 - 312, frame.getHeight());
				} else if (frame.getSize().width != (1350 - 312)) {
					frame.setBounds(frame.getX() - (1350 - 312 - extendedWidth) / 2, frame.getY(), 1350 - 312,
							frame.getHeight());
				}
			}

			VideoPlayerUI.setPlayerButtons(true);
			VideoPlayerUI.player.setVisible(true);

			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) {
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}

			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);
			lblYears.setLocation(frame.getWidth() - lblYears.getWidth() - 8, lblBy.getY());
			lblYears.setVisible(false);
		} else if (language.getProperty("functionMerge").equals(function)
				|| language.getProperty("functionNormalization").equals(function) || noVideoPlayer) {
			noSettings = true;

			if (frame.getSize().width == 332) {
				frame.setBounds(frame.getX() - (654 - 332) / 2, frame.getY(), 654, frame.getHeight());
			} else if (frame.getSize().width == 1350 - 312) {
				frame.setBounds(frame.getX() - (654 - (1350 - 312)) / 2, frame.getY(), 654, frame.getHeight());
			} else if (frame.getSize().width != 654) {
				frame.setBounds(frame.getX() + (extendedWidth - 654) / 2, frame.getY(), 654, frame.getHeight());
			}

			VideoPlayerUI.setPlayerButtons(false);
			VideoPlayerUI.player.setVisible(false);

			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);

			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);
			lblYears.setLocation(frame.getWidth() - lblYears.getWidth() - 8, lblBy.getY());
			lblYears.setVisible(false);
		} else if (bigger && frame.getSize().width < 1350) {
			if (frame.getSize().width == (1350 - 312)) {
				frame.setBounds(frame.getX() - (extendedWidth - (1350 - 312)) / 2, frame.getY(), extendedWidth,
						frame.getHeight());
			} else if (frame.getSize().width == 654) {
				frame.setBounds(frame.getX() - (extendedWidth - 654) / 2, frame.getY(), extendedWidth,
						frame.getHeight());
			} else if (frame.getSize().width == 332) {
				frame.setBounds(frame.getX() - (extendedWidth - 332) / 2, frame.getY(), extendedWidth,
						frame.getHeight());
			}

			VideoPlayerUI.setPlayerButtons(true);
			VideoPlayerUI.player.setVisible(true);

			lblArrows.setVisible(false);
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) {
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}

			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);
			lblYears.setLocation(frame.getWidth() - lblYears.getWidth() - 8, lblBy.getY());
			lblYears.setVisible(true);
		} else if (bigger == false && frame.getSize().width > 332 && forceFullSize == false) {
			noSettings = true;

			frame.setBounds(frame.getX() + (frame.getWidth() - 332) / 2, frame.getY() + (frame.getHeight() - minHeight) / 2,
					332, minHeight);
			lblArrows.setVisible(true);
			lblArrows.setLocation(frame.getWidth() - lblArrows.getWidth() - 7, lblArrows.getY());
			lblGpuDecoding.setVisible(false);
			comboGPUDecoding.setVisible(false);
			lblGpuFiltering.setVisible(false);
			comboGPUFilter.setVisible(false);

			lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
			lblYears.setVisible(false);
		} else if (bigger && frame.getSize().width > 332) {
			lblGpuDecoding.setVisible(true);
			comboGPUDecoding.setVisible(true);
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) {
				lblGpuFiltering.setVisible(true);
				comboGPUFilter.setVisible(true);
			}
		}
		
		if (FFMPEG.multiGPU > 0)
	    {
		    if (comboGPUDecoding.isVisible())
		    {
		    	comboSelectedGPU.setVisible(true);
		    }
		    else
		    	comboSelectedGPU.setVisible(false);	
	    }

		// Checking maximum screen width
		GraphicsConfiguration config = frame.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int screenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
			if (allScreens[i].equals(myScreen)) {
				screenIndex = i;
				break;
			}
		}

		double dpiScaleFactor = 1.0;
		if (System.getProperty("os.name").contains("Windows")) {
			double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
		}

		int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);

		if (frame.getWidth() > screenWidth) {
			extendedWidth = screenWidth;
			toggleFullscreen();
		}

		resizeAll(frame.getWidth(), 0);
	}

	public static void toggleFullscreen() {

		//Avoid glitch when resizing while playing
		if (VideoPlayerCore.playerIsPlaying())
			VideoPlayerUI.playerLoop = false;
		
		// IMPORTANT
		if (FFPROBE.totalLength <= 40 && VideoPlayerCore.preview != null)
		{
			VideoPlayerCore.preview = null;
		}

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		GraphicsConfiguration config = frame.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int screenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
			if (allScreens[i].equals(myScreen)) {
				screenIndex = i;
				break;
			}
		}

		double dpiScaleFactor = 1.0;
		if (System.getProperty("os.name").contains("Windows")) {
			double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds().getHeight();
			double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
			dpiScaleFactor = trueHorizontalLines / scaledHorizontalLines;
		}

		int screenHeight = (int) (allScreens[screenIndex].getDisplayMode().getHeight() * dpiScaleFactor);
		int screenWidth = (int) (allScreens[screenIndex].getDisplayMode().getWidth() * dpiScaleFactor);
		int screenX = (int) allScreens[screenIndex].getDefaultConfiguration().getBounds().getX();

		int height = 0;
		int screenOffset = allScreens[screenIndex].getDefaultConfiguration().getBounds().y;

		if ((frame.getHeight() < screenHeight - taskBarHeight || frame.getWidth() < screenWidth)
				&& frame.getWidth() > 332 && frame.getWidth() != 654 && frame.getWidth() != (extendedWidth - 312)) {
			height = screenHeight - taskBarHeight - frame.getHeight();
			resizeAll(screenWidth, height);
			frame.setLocation(screenX, screenOffset);
		} else if ((frame.getHeight() == screenHeight - taskBarHeight && frame.getWidth() == screenWidth)) {
			height = minHeight - frame.getHeight();
			resizeAll(extendedWidth, height);
			frame.setLocation(screenX + dim.width / 2 - frame.getSize().width / 2,
					dim.height / 2 - frame.getSize().height / 2 + screenOffset);
		} else if (frame.getHeight() >= screenHeight - taskBarHeight) {
			height = minHeight - frame.getHeight();
			resizeAll(frame.getWidth(), height);
			frame.setLocation(frame.getX(), dim.height / 2 - frame.getSize().height / 2 + screenOffset);
		} else {
			height = screenHeight - taskBarHeight - frame.getHeight();
			resizeAll(frame.getWidth(), height);
			frame.setLocation(frame.getX(), screenOffset);
		}

		if (frame.getWidth() > 332 && VideoPlayerCore.setTime != null && VideoPlayerUI.isPiping == false)
		{
			VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame

			// grpWatermark
			if (caseAddWatermark.isSelected()
					&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnPauseFunction")) == false
					&& Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStopRecording")) == false) {
				VideoPlayerOverlay.loadWatermark(Integer.parseInt(Shutter.textWatermarkSize.getText()));
				Shutter.logo.setLocation(
						(int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosX.getText()) / Shutter.playerRatio),
						(int) Math.floor(Integer.valueOf(Shutter.textWatermarkPosY.getText()) / Shutter.playerRatio));
			}
		}
	}

	public static void resizeAll(int width, int height) {

		if (frame.getWidth() >= 1130 && width >= 1130) {
			frame.setSize(width, frame.getHeight() + height);
		} else
			frame.setSize(frame.getSize().width, frame.getHeight() + height);

		if (frame.getWidth() < 1350 && frame.getWidth() >= 1130) {
			extendedWidth = frame.getWidth();
		}
		
		if (frame.getWidth() < 1320 && noSettings == false)
		{
			VideoPlayerUI.lblSpeed.setVisible(false);
			VideoPlayerUI.lblVolume.setVisible(false);

			if (frame.getWidth() < 1300)
			{
				VideoPlayerUI.sliderSpeed.setVisible(false);
				VideoPlayerUI.sliderVolume.setVisible(false);
			}
			else if (Shutter.frame.getSize().width > 654 && FFPROBE.totalLength > 40 && Shutter.caseEnableSequence.isSelected() == false && VideoPlayerUI.isPiping == false && inputDeviceIsRunning == false)
			{
				VideoPlayerUI.sliderSpeed.setVisible(true);
				VideoPlayerUI.sliderVolume.setVisible(true);
			}
		}
		else if (Shutter.frame.getSize().width > 654 && FFPROBE.totalLength > 40 && Shutter.caseEnableSequence.isSelected() == false && VideoPlayerUI.isPiping == false && inputDeviceIsRunning == false)
		{
			VideoPlayerUI.lblSpeed.setVisible(true);
			VideoPlayerUI.lblVolume.setVisible(true);
			VideoPlayerUI.sliderSpeed.setVisible(true);
			VideoPlayerUI.sliderVolume.setVisible(true);
		}
		
		if (frame.getWidth() > 332)
		{
			lblShutterEncoder.setLocation((frame.getWidth() / 2 - lblShutterEncoder.getPreferredSize().width / 2), 1);
			lblV.setVisible(true);
		}
		else
		{
			lblShutterEncoder.setLocation((320 - lblShutterEncoder.getPreferredSize().width) / 2 - 26, 1);
			lblV.setVisible(false);
		}

		setGPUOptions();

		lblV.setLocation(lblShutterEncoder.getX() + lblShutterEncoder.getWidth(), 5);

		if (frame.getWidth() > 332)
		{
			int grpX = frame.getWidth() - 312 - 12;
			grpResolution.setLocation(grpX, grpResolution.getLocation().y);
			grpBitrate.setLocation(grpX, grpBitrate.getLocation().y);
			grpSetAudio.setLocation(grpX, grpSetAudio.getLocation().y);
			grpAudio.setLocation(grpX, grpAudio.getLocation().y);
			grpCrop.setLocation(grpX, grpCrop.getLocation().y);
			grpOverlay.setLocation(grpX, grpOverlay.getLocation().y);
			grpSubtitles.setLocation(grpX, grpSubtitles.getLocation().y);
			grpWatermark.setLocation(grpX, grpWatermark.getLocation().y);
			grpColorimetry.setLocation(grpX, grpColorimetry.getLocation().y);
			grpImageAdjustement.setLocation(grpX, grpImageAdjustement.getLocation().y);
			grpCorrections.setLocation(grpX, grpCorrections.getLocation().y);
			grpTransitions.setLocation(grpX, grpTransitions.getLocation().y);
			grpImageSequence.setLocation(grpX, grpImageSequence.getLocation().y);
			grpImageFilter.setLocation(grpX, grpImageFilter.getLocation().y);
			grpSetTimecode.setLocation(grpX, grpSetTimecode.getLocation().y);
			grpAdvanced.setLocation(grpX, grpAdvanced.getLocation().y);
			btnReset.setLocation((grpX + 2), btnReset.getLocation().y);
		}

		topPanel.setBounds(0, 0, frame.getWidth(), 28);
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);
		quit.setLocation(frame.getSize().width - 20, 4);
		expand.setLocation(quit.getLocation().x - 20, 4);
		minimize.setLocation(expand.getLocation().x - 20, 4);
		help.setLocation(minimize.getLocation().x - 20, 4);
		newInstance.setLocation(help.getLocation().x - 20, 4);

		grpChooseFiles.setSize(grpChooseFiles.getWidth(), frame.getHeight() - 423);
		fileList.setSize(292, frame.getHeight() - 483);
		addToList.setSize(fileList.getSize());
		scrollBar.setSize(292, fileList.getHeight());
		grpChooseFunction.setLocation(grpChooseFunction.getX(), grpChooseFiles.getY() + grpChooseFiles.getHeight() + 4);
		grpDestination.setLocation(grpDestination.getX(), grpChooseFunction.getY() + grpChooseFunction.getHeight() + 6);
		grpProgression.setLocation(grpProgression.getX(), grpDestination.getY() + grpDestination.getHeight() + 6);

		// On récupère le groupe qui est le plus haut
		JPanel top;

		if (grpResolution.isVisible()) {
			top = grpResolution;
		} else if (grpSetTimecode.isVisible()) {
			top = grpSetTimecode;
		} else if (grpSetAudio.isVisible()) {
			top = grpSetAudio;
		} else {
			top = grpAudio;
		}

		// Empêche de faire dépasser la position minimale de top
		if (height < 0 && frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) >= 31) {
			height = 0;
		}

		// Pré calcul
		if (top.getY() + height >= grpChooseFiles.getY() && height > 0) {
			height = grpChooseFiles.getY() - top.getY();
		}

		if (frame.getWidth() > 332 && top.getY() < 30)
		{
			grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + height);
			grpBitrate.setLocation(grpBitrate.getLocation().x, grpBitrate.getLocation().y + height);
			grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + height);
			grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + height);
			grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + height);
			grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + height);
			grpSubtitles.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getLocation().y + height);
			grpWatermark.setLocation(grpWatermark.getLocation().x, grpWatermark.getLocation().y + height);
			grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + height);
			grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x,
					grpImageAdjustement.getLocation().y + height);
			grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + height);
			grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + height);
			grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + height);
			grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + height);
			grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + height);
			grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + height);
			btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + height);
		}
				
		if (noSettings == false && (frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) < 31 || top.getY() < 30))
		{
			settingsScrollBar.setVisible(true);
		}
		else
			settingsScrollBar.setVisible(false);

		if (System.getProperty("os.name").contains("Mac") && windowDrag)
		{
			frame.setShape(null);
		}
		else
		{
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
			Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
			shape1.add(shape2);
			frame.setShape(shape1);
		}

		settingsScrollBar.setBounds(frame.getWidth() - settingsScrollBar.getWidth() - 2, topPanel.getHeight() - 4, 11, frame.getHeight() - topPanel.getHeight() - statusBar.getHeight() + 4);

		// For grpOverlay resizing
		if (windowDrag == false && (caseAddTimecode.isSelected() || caseShowTimecode.isSelected() || caseAddText.isSelected() || caseShowFileName.isSelected()))
		{
			windowDrag = true;
			VideoPlayerUI.resizeAll();
			windowDrag = false;
		}
		else if (frame.getWidth() > 332)
		{
			VideoPlayerUI.resizeAll();
		}
		else if (frame.getWidth() == 332)
		{
			statusBar.setBounds(0, frame.getHeight() - 23, frame.getWidth(), 22);
		}

	}

	public static void setGPUOptions() {

		String function = comboFonctions.getSelectedItem().toString();

		if ("Apple ProRes".equals(function) && System.getProperty("os.name").contains("Mac") && arch.equals("arm64")
		|| "H.264".equals(function) || "H.265".equals(function) || "H.266".equals(function)
		|| "AV1".equals(function) || System.getProperty("os.name").contains("Windows") && "VP9".equals(function)
		|| System.getProperty("os.name").contains("Windows") && "FFV1".equals(function)
		|| System.getProperty("os.name").contains("Windows") && language.getProperty("functionPicture").equals(function) && comboFilter.getSelectedItem().toString().equals(".avif"))
		{
			lblHWaccel.setVisible(true);
			comboAccel.setVisible(true);
		} else {
			lblHWaccel.setVisible(false);
			comboAccel.setVisible(false);
		}

		if (frame.getWidth() > 332) {
			if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac")) {
				if (lblHWaccel.isVisible()) {
					lblGpuDecoding
							.setLocation(
									frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth()
											+ 6 + lblGpuFiltering.getWidth() + 6 + comboGPUFilter.getWidth() + 6
											+ lblHWaccel.getWidth() + 6 + comboAccel.getWidth()) / 2,
									lblGpuDecoding.getY());
				} else
					lblGpuDecoding.setLocation(
							frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth() + 6
									+ lblGpuFiltering.getWidth() + 6 + comboGPUFilter.getWidth()) / 2,
							lblGpuDecoding.getY());

				comboGPUDecoding.setLocation(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6,
						lblGpuDecoding.getLocation().y - 1);
				lblGpuFiltering.setLocation(comboGPUDecoding.getLocation().x + comboGPUDecoding.getWidth() + 6,
						lblBy.getY());
				comboGPUFilter.setLocation(lblGpuFiltering.getX() + lblGpuFiltering.getWidth() + 6,
						comboGPUDecoding.getY());
				lblHWaccel.setLocation(comboGPUFilter.getX() + comboGPUFilter.getWidth() + 6, lblBy.getY());
			} else {
				if (lblHWaccel.isVisible()) {
					lblGpuDecoding
							.setLocation(
									frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth()
											+ 6 + lblHWaccel.getWidth() + 6 + comboAccel.getWidth()) / 2,
									lblGpuDecoding.getY());
				} else
					lblGpuDecoding.setLocation(
							frame.getWidth() / 2 - (lblGpuDecoding.getWidth() + 6 + comboGPUDecoding.getWidth()) / 2,
							lblGpuDecoding.getY());

				comboGPUDecoding.setLocation(lblGpuDecoding.getX() + lblGpuDecoding.getWidth() + 6,
						lblGpuDecoding.getLocation().y - 1);
				lblHWaccel.setLocation(comboGPUDecoding.getX() + comboGPUDecoding.getWidth() + 6, lblBy.getY());
			}
			comboAccel.setLocation(lblHWaccel.getLocation().x + lblHWaccel.getWidth() + 4, comboGPUDecoding.getY());
			
			if (FFMPEG.multiGPU > 0 && comboGPUDecoding.isVisible())
		    {
			    comboSelectedGPU.setLocation(lblGpuDecoding.getLocation().x - comboSelectedGPU.getWidth() - 6, comboGPUDecoding.getY());
		    }	
			else
				comboSelectedGPU.setVisible(false);
		}
	}
	
	public static void changeSections(final boolean anim) {
		
		String function = comboFonctions.getSelectedItem().toString();
		
		//Allows to load this method only once
		if (Utils.loadEncFile != null && Utils.loadEncFile.isAlive())
		{
			if (function.equals(loadedFunction))
			{
				return;		
			}
			else
				loadedFunction = function;
		}
		
		if (frame.getWidth() > 332 //Other functions are for addToList text
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionExtract"))		
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranscribe"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBlurFaces"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranslate"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionColorize"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionBackgroundRemover"))
		|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSceneDetection")))
		{		
			Thread changeSize = new Thread(new Runnable() {

				@Override
				public void run() {

					if (changeGroupes == false) // permet d'attendre la fin de l'action
					{
						try {
														
							if (frame.getSize().width >= 1130 && anim)
							{
								int i = frame.getWidth() - 312 - 12;

								do {
									
									changeGroupes = true;

									long startTime = System.nanoTime();
									
									if (Settings.btnDisableAnimations.isSelected())
										i = frame.getWidth();
									else
									{
										i += 20;
										
										if (i > frame.getWidth())
											i = frame.getWidth();
									}
									
									grpResolution.setLocation(i, grpResolution.getLocation().y);
									grpBitrate.setLocation(i, grpBitrate.getLocation().y);
									grpSetAudio.setLocation(i, grpSetAudio.getLocation().y);
									grpAudio.setLocation(i, grpAudio.getLocation().y);
									grpCrop.setLocation(i, grpCrop.getLocation().y);
									grpOverlay.setLocation(i, grpOverlay.getLocation().y);
									grpSubtitles.setLocation(i, grpSubtitles.getLocation().y);
									grpWatermark.setLocation(i, grpWatermark.getLocation().y);
									grpColorimetry.setLocation(i, grpColorimetry.getLocation().y);
									grpImageAdjustement.setLocation(i, grpImageAdjustement.getLocation().y);
									grpCorrections.setLocation(i, grpCorrections.getLocation().y);
									grpTransitions.setLocation(i, grpTransitions.getLocation().y);
									grpImageSequence.setLocation(i, grpImageSequence.getLocation().y);
									grpImageFilter.setLocation(i, grpImageFilter.getLocation().y);
									grpSetTimecode.setLocation(i, grpSetTimecode.getLocation().y);
									grpAdvanced.setLocation(i, grpAdvanced.getLocation().y);
									btnReset.setLocation((i + 2), btnReset.getLocation().y);

									// Animate size
									animateSections(startTime);

								} while (i < frame.getWidth());
							}

							VideoPlayerUI.seekOnKeyFrames = false;

							List<String> graphicsAccel = new ArrayList<String>();
							graphicsAccel.add(language.getProperty("aucune").toLowerCase());

							if (noSettings == false && frame.getSize().getHeight()
									- (btnReset.getLocation().y + btnReset.getHeight()) < 31) {
								settingsScrollBar.setVisible(true);
							} else
								settingsScrollBar.setVisible(false);

							if (anim)
							{
								grpSetTimecode.setSize(grpSetTimecode.getSize().width, 17);
								grpSetAudio.setSize(grpSetAudio.getSize().width, 17);
								grpCrop.setSize(grpCrop.getSize().width, 17);
								grpOverlay.setSize(grpOverlay.getSize().width, 17);
								grpSubtitles.setSize(grpSubtitles.getSize().width, 17);
								grpWatermark.setSize(grpWatermark.getSize().width, 17);
								grpColorimetry.setSize(grpColorimetry.getSize().width, 17);
								grpImageAdjustement.setSize(grpImageAdjustement.getSize().width, 17);
								grpCorrections.setSize(grpCorrections.getSize().width, 17);
								grpTransitions.setSize(grpTransitions.getSize().width, 17);
								grpImageSequence.setSize(grpImageSequence.getSize().width, 17);
								grpImageFilter.setSize(grpImageFilter.getSize().width, 17);
								grpAdvanced.setSize(grpAdvanced.getSize().width, 17);

								// Reset Screenshot icon
								if (grpResolution.isVisible()) {
									if (lblPad.isVisible()) {
										lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);
									} else
										lblScreenshot.setLocation(
												comboResolution.getX() + comboResolution.getWidth() + 9, 21);
								}
							}

							btnStart.setEnabled(true);
							btnReset.setVisible(true);

							if (language.getProperty("functionConform").equals(function) || language.getProperty("functionSubtitles").equals(function))
							{
								addToList.setText(language.getProperty("filesVideo"));

								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);
								
								if (language.getProperty("functionSubtitles").equals(function))
								{
									btnStart.setEnabled(false);

									if (inputDeviceIsRunning)
									{
										JOptionPane.showMessageDialog(frame,
												language.getProperty("incompatibleInputDevice"),
												language.getProperty("menuItemScreenRecord"),
												JOptionPane.ERROR_MESSAGE);
									} else if (scanIsRunning) {
										JOptionPane.showMessageDialog(frame, language.getProperty("scanIncompatible"),
												language.getProperty("scanActivated"), JOptionPane.ERROR_MESSAGE);
									}
									else if (anim) 
									{
										VideoPlayerCore.videoPath = null;
										changeWidth(true);
										VideoPlayerCore.setMedia();										
									}
									
								} else
									VideoPlayerUI.seekOnKeyFrames = true;

							} else if (language.getProperty("functionRewrap").equals(function)
							|| language.getProperty("functionCut").equals(function)
							|| language.getProperty("functionMerge").equals(function)) {
								VideoPlayerUI.seekOnKeyFrames = true;

								if (language.getProperty("functionCut").equals(function)
										|| language.getProperty("functionMerge").equals(function)) {
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								} else
									addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));

								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);

								if (language.getProperty("functionRewrap").equals(function)
										|| language.getProperty("functionCut").equals(function)) {
									grpSetTimecode.setVisible(true);
									grpSetTimecode.setLocation(grpSetTimecode.getX(), 30);
									grpSetAudio.setLocation(grpSetAudio.getX(),
											grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);

									if (caseChangeAudioCodec.isSelected() == false && caseNormalizeAudio.isEnabled())
									{
										caseNormalizeAudio.setSelected(false);
										caseNormalizeAudio.setEnabled(false);
										comboNormalizeAudio.setEnabled(false);
									}	
									if (caseChangeAudioCodec.isSelected() == false)
									{
										caseEqualizer.setEnabled(false);
										caseEqualizer.setSelected(false);
									}
									
								} else {
									grpSetTimecode.setVisible(false);
									grpSetAudio.setLocation(grpSetAudio.getX(), 30);
								}

								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7,caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (anim)
									grpSetAudio.setSize(312, 17);
								grpAudio.setVisible(false);

								if (language.getProperty("functionRewrap").equals(function)
										|| language.getProperty("functionCut").equals(function)) {
									grpAdvanced.removeAll();

									grpSubtitles.setVisible(true);
									grpSubtitles.setLocation(grpSubtitles.getX(),
											grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									grpAdvanced.setVisible(true);
									grpAdvanced.setLocation(grpAdvanced.getX(),
											grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
									casePreserveSubs.setLocation(7, 14);
									grpAdvanced.add(casePreserveSubs);
									caseCreateTree.setLocation(7, casePreserveSubs.getLocation().y + 17);
									grpAdvanced.add(caseCreateTree);
									comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
											caseCreateTree.getY() + 4);
									grpAdvanced.add(comboCreateTree);
									casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);

									if (language.getProperty("functionRewrap").equals(function)) {
										grpAdvanced.add(caseForcerDAR);
										caseForcerDAR.setLocation(7, casePreserveMetadata.getLocation().y + 17);
										grpAdvanced.add(comboDAR);
										comboDAR.setLocation(
												caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
												caseForcerDAR.getLocation().y + 3);
										grpAdvanced.add(caseRotate);
										caseRotate.setLocation(7, caseForcerDAR.getLocation().y + 17);
										grpAdvanced.add(comboRotate);
										comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
												caseRotate.getLocation().y + 3);
										caseCreateOPATOM.setLocation(7, caseRotate.getLocation().y + 17);
										caseCreateOPATOM.setEnabled(true);
										grpAdvanced.add(caseCreateOPATOM);
										lblOPATOM.setLocation(
												caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4,
												caseCreateOPATOM.getLocation().y + 3);
										grpAdvanced.add(lblOPATOM);
										lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4,
												caseCreateOPATOM.getLocation().y);
										lblCreateOPATOM.setEnabled(true);
										grpAdvanced.add(lblCreateOPATOM);
									}

									btnReset.setLocation(btnReset.getX(),
											grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
								} else if (language.getProperty("functionMerge").equals(function)) {
									grpAdvanced.removeAll();

									grpAdvanced.setVisible(true);
									grpAdvanced.setLocation(grpAdvanced.getX(),
											grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									casePreserveMetadata.setLocation(7, 14);
									grpAdvanced.add(casePreserveMetadata);
									caseOpenGop.setLocation(7, casePreserveMetadata.getLocation().y + 17);
									grpAdvanced.add(caseOpenGop);
									btnReset.setLocation(btnReset.getX(),
											grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
								}

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);

								if (language.getProperty("functionMerge").equals(function))
								{
									caseNormalizeAudio.setSelected(false);
									caseNormalizeAudio.setEnabled(false);
									comboNormalizeAudio.setEnabled(false);
									caseEqualizer.setEnabled(false);
									caseEqualizer.setSelected(false);
								}

								if ((comboAudioCodec.getItemCount() != 13 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false) && anim)
								{
									if (lblAudioMapping.getItemCount() != 4) {
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
										
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									}

									comboAudioCodec.setModel(
											new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits" , "PCM 32Float", "FLAC", "AAC", "MP3", "AC3", "Opus", "Vorbis",
													"Dolby Digital Plus", language.getProperty("noAudio"), language.getProperty("custom") }));
									comboAudioCodec.setSelectedIndex(0);
									caseNormalizeAudio.setEnabled(false);
									caseNormalizeAudio.setSelected(false);
									comboNormalizeAudio.setEnabled(false);
									caseEqualizer.setEnabled(false);
									caseEqualizer.setSelected(false);
									caseChangeAudioCodec.setSelected(false);
									comboAudioCodec.setEnabled(false);
									comboAudioBitrate.setEnabled(false);
									lbl48k.setEnabled(false);
								}
								caseChangeAudioCodec.setEnabled(true);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(comboAudioBitrate);
								grpSetAudio.add(lblKbs);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								grpSetAudio.add(lblAudio1);
								grpSetAudio.add(comboAudio1);
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								grpSetAudio.add(lblAudio2);
								grpSetAudio.add(comboAudio2);
								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								grpSetAudio.add(lblAudio3);
								grpSetAudio.add(comboAudio3);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								grpSetAudio.add(lblAudio4);
								grpSetAudio.add(comboAudio4);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								grpSetAudio.add(lblAudio5);
								grpSetAudio.add(comboAudio5);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								grpSetAudio.add(lblAudio6);
								grpSetAudio.add(comboAudio6);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								grpSetAudio.add(lblAudio7);
								grpSetAudio.add(comboAudio7);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);
								grpSetAudio.add(lblAudio8);
								grpSetAudio.add(comboAudio8);

								if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
										&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
										&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
										&& comboAudio7.getSelectedIndex() == 16
										&& comboAudio8.getSelectedIndex() == 16) {
									comboAudio1.setSelectedIndex(0);
									comboAudio2.setSelectedIndex(1);
									comboAudio3.setSelectedIndex(2);
									comboAudio4.setSelectedIndex(3);
									comboAudio5.setSelectedIndex(4);
									comboAudio6.setSelectedIndex(5);
									comboAudio7.setSelectedIndex(6);
									comboAudio8.setSelectedIndex(7);
								}

							} else if (language.getProperty("functionReplaceAudio").equals(function)
							|| language.getProperty("functionNormalization").equals(function))
							{

								if (language.getProperty("functionReplaceAudio").equals(function))
									addToList.setText(language.getProperty("fileVideoAndAudio"));
								else
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.setLocation(grpSetAudio.getX(), 30);
								
								if (caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.setSize(312, 241);
									if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
									{
										grpSetAudio.setSize(312, 218);					
									}
								}
								else if (language.getProperty("functionReplaceAudio").equals(function))
								{
									grpSetAudio.setSize(312, 93);
								}
								else
									grpSetAudio.setSize(312, 70);

								grpAudio.setVisible(false);

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseChangeAudioCodec);

								if ((comboAudioCodec.getItemCount() != 13 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false) && anim)
								{
									if (lblAudioMapping.getItemCount() != 4) {
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
										
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									}

									comboAudioCodec.setModel(
											new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits" , "PCM 32Float", "FLAC", "AAC", "MP3", "AC3", "Opus", "Vorbis",
													"Dolby Digital Plus", language.getProperty("noAudio"), language.getProperty("custom") }));
									comboAudioCodec.setSelectedIndex(0);
									caseNormalizeAudio.setEnabled(false);
									caseNormalizeAudio.setSelected(false);
									comboNormalizeAudio.setEnabled(false);
									caseEqualizer.setEnabled(false);
									caseEqualizer.setSelected(false);
									caseChangeAudioCodec.setSelected(false);
									comboAudioCodec.setEnabled(false);
									comboAudioBitrate.setEnabled(false);
									lbl48k.setEnabled(false);
								}
								caseChangeAudioCodec.setEnabled(true);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(comboAudioBitrate);
								grpSetAudio.add(lblKbs);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);
								grpSetAudio.add(caseAudioOffset);
								grpSetAudio.add(txtAudioOffset);
								grpSetAudio.add(lblOffsetFPS);
								if (language.getProperty("functionReplaceAudio").equals(function)) {
									grpSetAudio.add(caseKeepSourceTracks);
								}
								grpSetAudio.repaint();
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(false);

								if (language.getProperty("functionNormalization").equals(function))
								{
									// grpAdvanced
									grpAdvanced.removeAll();
									grpAdvanced.setVisible(true);
									caseTruePeak.setLocation(7, 14);
									grpAdvanced.add(caseTruePeak);
									comboTruePeak.setLocation(
											caseTruePeak.getLocation().x + caseTruePeak.getWidth() + 4,
											caseTruePeak.getLocation().y + 4);
									grpAdvanced.add(comboTruePeak);
									caseLRA.setLocation(7, caseTruePeak.getLocation().y + 17);
									grpAdvanced.add(caseLRA);
									comboLRA.setLocation(caseLRA.getLocation().x + caseLRA.getWidth() + 4,
											caseLRA.getLocation().y + 4);
									grpAdvanced.add(comboLRA);
									grpAdvanced.setLocation(grpAdvanced.getX(), grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
									caseCreateTree.setLocation(7, caseLRA.getLocation().y + 17);
									grpAdvanced.add(caseCreateTree);
									comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
											caseCreateTree.getY() + 4);
									grpAdvanced.add(comboCreateTree);
									btnReset.setLocation(btnReset.getX(),
											grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);
								} else {
									grpAdvanced.setVisible(false);
									btnReset.setLocation(btnReset.getX(),
											grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								}

							} else if ("FrameMD5".equals(function)) {

								addToList.setText(language.getProperty("filesVideo"));

								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(), 30);								
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setLocation(btnReset.getX(),
										grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);

							} else if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function)
									|| "ALAC".equals(function) || "MP3".equals(function) || "AAC".equals(function)
									|| "AC3".equals(function) || "Opus".equals(function) || "Vorbis".equals(function)
									|| "Dolby Digital Plus".equals(function) || "Dolby TrueHD".equals(function)) {

								if (anim) {
									if (comboFonctions.getSelectedItem().toString().equals("MP3")
											|| comboFonctions.getSelectedItem().toString().equals("AAC")
											|| comboFonctions.getSelectedItem().toString().equals("Vorbis")) {
										comboFilter.setSelectedIndex(9);
									} else if (comboFonctions.getSelectedItem().toString().equals("AC3")
											|| comboFonctions.getSelectedItem().toString()
													.equals("Dolby Digital Plus")) {
										comboFilter.setSelectedIndex(7);
									} else if (comboFonctions.getSelectedItem().toString().equals("Opus")) {
										comboFilter.setSelectedIndex(11);
									}
								}

								addToList.setText(language.getProperty("filesVideoOrAudio"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(true);
								grpAudio.setLocation(grpAudio.getX(), 30);
								caseNormalizeAudio.setLocation(7, 16);
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7, caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								grpAudio.add(caseNormalizeAudio);
								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								grpAudio.add(comboNormalizeAudio);
								grpAudio.add(caseEqualizer);
								grpAudio.add(lbl48k);
								lbl48k.setLocation(caseSampleRate.getLocation().x + caseSampleRate.getWidth() + 3,
										caseSampleRate.getLocation().y + 3);
								if (caseSampleRate.isSelected() == false) {
									lbl48k.setEnabled(false);
								}
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(true);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpTransitions.setLocation(grpTransitions.getX(), grpAudio.getSize().height + grpAudio.getLocation().y + 6);
								
								if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function))
								{
									grpSetTimecode.setVisible(true);
									grpSetTimecode.setLocation(grpSetTimecode.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								}
								else
									grpSetTimecode.setVisible(false);
								
								grpAdvanced.removeAll();
								grpAdvanced.setVisible(true);
								caseCreateTree.setLocation(7, 14);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4, caseCreateTree.getY() + 4);
								grpAdvanced.add(comboCreateTree);
								caseDRC.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(caseDRC);
								
								if ("WAV".equals(function) || "AIFF".equals(function) || "FLAC".equals(function))
								{
									grpAdvanced.setLocation(grpAdvanced.getX(), grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								}
								else
									grpAdvanced.setLocation(grpAdvanced.getX(), grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);

								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

							} else if ("Loudness & True Peak".equals(function)
							|| language.getProperty("functionBlackDetection").equals(function)
							|| language.getProperty("functionOfflineDetection").equals(function)
							|| language.getProperty("functionSeparation").equals(function)
							|| language.getProperty("functionTranscribe").equals(function)
							|| language.getProperty("functionBlurFaces").equals(function)
							|| "VMAF".equals(function) || "FrameMD5".equals(function)
							|| language.getProperty("functionInsert").equals(function)) {

								if (language.getProperty("functionBlackDetection").equals(function)
										|| language.getProperty("functionOfflineDetection").equals(function)
										|| "VMAF".equals(function) || "FrameMD5".equals(function)) {
									addToList.setText(language.getProperty("filesVideo"));
								} else if (language.getProperty("functionInsert").equals(function)) {
									addToList.setText(language.getProperty("fileMaster"));
								} else
									addToList.setText(language.getProperty("filesVideoOrAudio"));

								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);

							} else if ("XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function)
							|| "AVC-Intra 100".equals(function) || "XAVC".equals(function) || "XAVC Long GOP".equals(function)
							|| "HAP".equals(function) || "FFV1".equals(function)) {

								if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
										&& caseAS10.isSelected()) {
									final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(
											new String[] { ".mxf" });
									comboFilter.setModel(model);
								}

								// HWaccel								
								if (anim)
									FFMPEG.detectHardwareAcceleration(function);							 

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 5 || comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false)
								{
									if (lblAudioMapping.getItemCount() > 1)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { "Multi" }));
									}
									lblAudioMapping.setSelectedItem("Multi");

									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"), language.getProperty("noAudio") }));
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									lbl48k.setEnabled(true);

									if ("XDCAM HD422".equals(function) || "XDCAM HD 35".equals(function) || "AVC-Intra 100".equals(function) || ("XAVC").equals(function) || "XAVC Long GOP".equals(function))
									{
										comboAudioCodec.setSelectedIndex(1);
									}
									else
										comboAudioCodec.setSelectedIndex(0);
									
									if (lblAudioMapping.getItemCount() > 1)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { "Multi" }));
									}
									lblAudioMapping.setSelectedItem("Multi");

									comboAudioCodec.setSelectedIndex(1);
								}

								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								grpSetAudio.add(lblAudio1);
								grpSetAudio.add(comboAudio1);
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								grpSetAudio.add(lblAudio2);
								grpSetAudio.add(comboAudio2);

								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);

								if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
								|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.add(lblAudio3);
									grpSetAudio.add(comboAudio3);
									grpSetAudio.add(lblAudio4);
									grpSetAudio.add(comboAudio4);
									grpSetAudio.add(lblAudio5);
									grpSetAudio.add(comboAudio5);
									grpSetAudio.add(lblAudio6);
									grpSetAudio.add(comboAudio6);
									grpSetAudio.add(lblAudio7);
									grpSetAudio.add(comboAudio7);
									grpSetAudio.add(lblAudio8);
									grpSetAudio.add(comboAudio8);
								}
								
								if (Utils.loadEncFile == null || Utils.loadEncFile.isAlive() == false)
								{
									if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
									&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
									&& comboAudio5.getSelectedIndex() == 4 && comboAudio6.getSelectedIndex() == 5
									&& comboAudio7.getSelectedIndex() == 6 && comboAudio8.getSelectedIndex() == 7
									&& function.equals("HAP") == false && function.equals("FFV1") == false
									&& caseAS10.isSelected() == false)
									{
										comboAudio5.setSelectedIndex(16);
										comboAudio6.setSelectedIndex(16);
										comboAudio7.setSelectedIndex(16);
										comboAudio8.setSelectedIndex(16);
									}
									else if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
									&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
									&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
									&& comboAudio7.getSelectedIndex() == 16 && comboAudio8.getSelectedIndex() == 16
									&& (function.equals("HAP") || function.equals("FFV1") || caseAS10.isSelected()))
									{
										comboAudio1.setSelectedIndex(0);
										comboAudio2.setSelectedIndex(1);
										comboAudio3.setSelectedIndex(2);
										comboAudio4.setSelectedIndex(3);
										comboAudio5.setSelectedIndex(4);
										comboAudio6.setSelectedIndex(5);
										comboAudio7.setSelectedIndex(6);
										comboAudio8.setSelectedIndex(7);
									}
								}

								// Ajout partie résolution
								grpResolution.removeAll();

								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
										caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
										caseForcerDAR.getLocation().y + 3);

								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
								}
								else
									lblPad.setVisible(true);

								grpResolution.add(comboResolution);

								if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
										|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")) {
									if (comboResolution.getItemCount() > 3) {
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
												language.getProperty("source"), "1920x1080", "1280x720" }));
									}
								} else {
									if (comboResolution.getItemCount() != 26) {
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
												language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
												"AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160",
												"2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x791",
												"1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto",
												"1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
								}
								
								// Set comboOptions
								changeComboOptions();

								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));
								if (subtitlesBurn)
									caseDisplay.setEnabled(true);
								else {
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpBitrate.setVisible(false);
								grpAudio.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7,caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (function.equals("HAP") == false && function.equals("FFV1") == false)
								{
									if (caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
									{
										grpSetAudio.setSize(312, 241);
									}
									else if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
									{
										grpSetAudio.setSize(312, 169);
									}
								}
								else if (anim)
								{
									grpSetAudio.setSize(312, 17);
								}
								grpSetAudio.setLocation(grpSetAudio.getX(),
										grpResolution.getSize().height + grpResolution.getLocation().y + 6);
								grpSetAudio.repaint();
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(),
										grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(),
										grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(),
										grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(),
										grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(),
										grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(),
										grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(),
										grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(),
										grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(),
										grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(),
										grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);

								if (function.equals("XAVC Long GOP"))
								{
									if (comboColorspace.getItemCount() != 4) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								else 
								{
									if (comboColorspace.getItemCount() != 3)
									{
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

								// Ajout des fonctions avancées
								grpAdvanced.removeAll();

								// grpAdvanced
								caseConform.setLocation(7, 14);
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4,
										comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4,
										comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);

								caseForcerProgressif.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseForcerProgressif);

								caseForcerDesentrelacement.setLocation(7, caseForcerProgressif.getLocation().y + 17);
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(
										caseForcerDesentrelacement.getLocation().x
												+ caseForcerDesentrelacement.getWidth() + 4,
										caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4,
										lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);

								caseForcerEntrelacement.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
								grpAdvanced.add(caseForcerEntrelacement);

								if (function.contains("XDCAM")) {
									casePreserveMetadata.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);

									caseAS10.setText(language.getProperty("caseAS10"));
									caseAS10.setLocation(7, casePreserveMetadata.getLocation().y + 17);
									grpAdvanced.add(caseAS10);
									comboAS10.setLocation(caseAS10.getX() + caseAS10.getWidth() + 4,
											caseAS10.getLocation().y + 4);
									grpAdvanced.add(comboAS10);

									caseCreateOPATOM.setEnabled(true);
									lblCreateOPATOM.setEnabled(true);
									if ((caseCreateOPATOM.isSelected() || caseCreateTree.isSelected())
											&& grpDestination.getTabCount() > 2) {
										setDestinationTabs(2);
									}

									caseCreateOPATOM.setLocation(7, caseAS10.getLocation().y + 17);
									grpAdvanced.add(caseCreateOPATOM);
									lblOPATOM.setLocation(
											caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4,
											caseCreateOPATOM.getLocation().y + 3);
									grpAdvanced.add(lblOPATOM);
									lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4,
											caseCreateOPATOM.getLocation().y);
									grpAdvanced.add(lblCreateOPATOM);
									caseOPATOM.setLocation(7, caseCreateOPATOM.getLocation().y + 17);
									grpAdvanced.add(caseOPATOM);
								} else if (function.equals("AVC-Intra 100")) {
									caseAS10.setText(language.getProperty("caseAS10")
											.replace("10" + language.getProperty("colon"), "11")
											.replace("10 format" + language.getProperty("colon"), "11 format"));
									caseAS10.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseAS10);
									casePreserveMetadata.setLocation(7, caseAS10.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);
								} else if (function.equals("HAP")) {
									caseChunks.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseChunks);
									chunksSize.setLocation(caseChunks.getX() + caseChunks.getWidth() + 3,
											caseChunks.getY() + 3);
									grpAdvanced.add(chunksSize);
									casePreserveMetadata.setLocation(7, caseChunks.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);
								} else if (function.equals("XAVC")) {
									caseGOP.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);

									casePreserveMetadata.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);
								} else if (function.equals("XAVC Long GOP")) {
									caseForcePreset.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcePreset);
									comboForcePreset.setLocation(caseForcePreset.getLocation().x + caseForcePreset.getWidth() + 4, caseForcePreset.getLocation().y + 4);
									grpAdvanced.add(comboForcePreset);
									
									caseGOP.setLocation(7, caseForcePreset.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);

									casePreserveMetadata.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);
								} else {
									casePreserveMetadata.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(casePreserveMetadata);
								}

							} else if ("DNxHD".equals(function) || "DNxHR".equals(function)
									|| "Apple ProRes".equals(function) || "QT Animation".equals(function)
									|| ("GoPro CineForm").equals(function) || "Uncompressed".equals(function)) {

								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));

								// HWaccel
								if (anim)
									FFMPEG.detectHardwareAcceleration(function);

								if (comboFonctions.getSelectedItem().equals("QT Animation") || subtitlesBurn == false) {
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								} else
									caseDisplay.setEnabled(true);

								if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
										|| comboFonctions.getSelectedItem().toString().equals("DNxHR")) {
									caseCreateOPATOM.setEnabled(true);
									lblCreateOPATOM.setEnabled(true);
									if ((caseCreateOPATOM.isSelected() || caseCreateTree.isSelected())
											&& grpDestination.getTabCount() > 2) {
										setDestinationTabs(2);
									}
								} else {
									caseCreateOPATOM.setEnabled(false);
									caseCreateOPATOM.setSelected(false);
									lblCreateOPATOM.setEnabled(false);
								}

								if (comboFilter.getSelectedItem().toString().equals("36")) {
									caseForcerEntrelacement.setEnabled(false);
									caseForcerInversion.setEnabled(false);
									caseForcerEntrelacement.setSelected(false);
									caseForcerInversion.setSelected(false);
								} else {
									caseForcerEntrelacement.setEnabled(true);
									caseForcerInversion.setEnabled(true);
								}

								// Ajout partie résolution
								grpResolution.removeAll();

								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
										caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
										caseForcerDAR.getLocation().y + 3);

								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
								}
								else
									lblPad.setVisible(true);

								grpResolution.add(comboResolution);

								if (comboFonctions.getSelectedItem().toString().equals("DNxHD")) {
									if (comboResolution.getItemCount() > 3) {
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
												language.getProperty("source"), "1920x1080", "1280x720" }));
									}
								} else {
									if (comboResolution.getItemCount() != 26) {
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
												language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
												"AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160",
												"2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x768",
												"1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto",
												"1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
								}
								
								// Set comboOptions
								changeComboOptions();

								grpBitrate.setVisible(false);
								grpAudio.setVisible(false);
								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (anim)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(),
										grpResolution.getSize().height + grpResolution.getLocation().y + 6);
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(),
										grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(),
										grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(),
										grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(),
										grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								grpAudio.setVisible(false);
								grpImageFilter.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(),
										grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(),
										grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(),
										grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(),
										grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(),
										grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(),
										grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);

								if ("Uncompressed".equals(function)) {
									if (comboColorspace.getItemCount() != 4) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
														"Rec. 2020 HLG 10bits" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								} else {
									if (comboColorspace.getItemCount() != 3) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 6
										|| comboAudioCodec.getModel().getElementAt(0).equals("PCM 16Bits") == false) {
									if (lblAudioMapping.getItemCount() != 4) {
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
									}
									lblAudioMapping.setSelectedItem("Multi");

									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {
											"PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"),
											language.getProperty("noAudio"), language.getProperty("custom") }));
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									comboAudioCodec.setSelectedIndex(0);
									lbl48k.setEnabled(true);
								}

								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}

								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);

								if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
								|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.add(lblAudio3);
									grpSetAudio.add(comboAudio3);
									grpSetAudio.add(lblAudio4);
									grpSetAudio.add(comboAudio4);
									grpSetAudio.add(lblAudio5);
									grpSetAudio.add(comboAudio5);
									grpSetAudio.add(lblAudio6);
									grpSetAudio.add(comboAudio6);
									grpSetAudio.add(lblAudio7);
									grpSetAudio.add(comboAudio7);
									grpSetAudio.add(lblAudio8);
									grpSetAudio.add(comboAudio8);
								}

								if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
										&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
										&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
										&& comboAudio7.getSelectedIndex() == 16
										&& comboAudio8.getSelectedIndex() == 16) {
									comboAudio1.setSelectedIndex(0);
									comboAudio2.setSelectedIndex(1);
									comboAudio3.setSelectedIndex(2);
									comboAudio4.setSelectedIndex(3);
									comboAudio5.setSelectedIndex(4);
									comboAudio6.setSelectedIndex(5);
									comboAudio7.setSelectedIndex(6);
									comboAudio8.setSelectedIndex(7);
								}

								// Ajout des fonctions avancées
								grpAdvanced.removeAll();

								// grpAdvanced
								caseConform.setLocation(7, 14);
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4,
										comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4,
										comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);

								caseForcerProgressif.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseForcerProgressif);

								caseForcerDesentrelacement.setLocation(7, caseForcerProgressif.getLocation().y + 17);
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(
										caseForcerDesentrelacement.getLocation().x
												+ caseForcerDesentrelacement.getWidth() + 4,
										caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4,
										lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);

								if (comboFonctions.getSelectedItem().toString().equals("DNxHR") == false) {
									caseForcerEntrelacement.setLocation(7,
											caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcerEntrelacement);

									caseForcerInversion.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcerInversion);
								}

								if (comboFonctions.getSelectedItem().equals("GoPro CineForm")) {
									caseAlpha.setLocation(7, caseForcerInversion.getY() + 17);
									grpAdvanced.add(caseAlpha);
									comboAlpha.setLocation(caseAlpha.getX() + caseAlpha.getWidth() + 4, caseAlpha.getLocation().y + 4);
									grpAdvanced.add(comboAlpha);
									caseCreateTree.setLocation(7, caseAlpha.getLocation().y + 17);
								} else {
									if (comboFonctions.getSelectedItem().toString().equals("DNxHR"))
										caseCreateTree.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									else
										caseCreateTree.setLocation(7, caseForcerInversion.getLocation().y + 17);
								}
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
										caseCreateTree.getY() + 4);
								grpAdvanced.add(comboCreateTree);

								casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);
								caseCreateOPATOM.setLocation(7, casePreserveMetadata.getLocation().y + 17);
								grpAdvanced.add(caseCreateOPATOM);
								lblOPATOM.setLocation(
										caseCreateOPATOM.getLocation().x + caseCreateOPATOM.getWidth() + 4,
										caseCreateOPATOM.getLocation().y + 3);
								grpAdvanced.add(lblOPATOM);
								lblCreateOPATOM.setLocation(lblOPATOM.getX() + lblOPATOM.getWidth() + 4,
										caseCreateOPATOM.getLocation().y);
								grpAdvanced.add(lblCreateOPATOM);
								caseOPATOM.setLocation(7, caseCreateOPATOM.getLocation().y + 17);
								grpAdvanced.add(caseOPATOM);

							} else if ("H.264".equals(function) || "H.265".equals(function)
									|| "H.266".equals(function)) {

								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));

								// HWaccel
								if (anim)
									FFMPEG.detectHardwareAcceleration(function);

								if (comboFonctions.getSelectedItem().equals("H.266") || subtitlesBurn == false) {
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								} else
									caseDisplay.setEnabled(true);

								if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())) {
									caseForcerEntrelacement.setEnabled(true);
									lblVBR.setVisible(true);

									if (caseQMax.isSelected() == false)
										caseForcePreset.setEnabled(true);
									caseForceTune.setEnabled(true);

									if (lblVBR.getText().equals("CQ")) {
										bitrateSize.setText("-");
										lblVideoBitrate.setText(language.getProperty("lblValue"));
										lblKbsH264.setVisible(false);
										h264lines.setVisible(false);
										case2pass.setSelected(false);
										case2pass.setEnabled(false);
									}
								} else {
									caseForcerEntrelacement.setSelected(false);
									caseForcerEntrelacement.setEnabled(false);
									
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

								if ("H.264".equals(function)) {

									//comboForceProfile
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false && comboForceProfile.getModel().getSize() != 3 || comboForceProfile.getModel().getElementAt(0).toString().equals("base") == false)
									{
										comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high" }));
										comboForceProfile.setSelectedIndex(2);
									}
									else if (comboForceProfile.getModel().getSize() != 5)
									{
										comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high", "high422", "high444" }));
										comboForceProfile.setSelectedIndex(2);
									}

									//comboForceTune								
									if (comboAccel.isEnabled() && (comboAccel.getSelectedItem().equals("Nvidia NVENC") || comboAccel.getSelectedItem().equals("Vulkan Video")))
									{
										if (comboForceTune.getModel().getSize() != 4)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "hq", "ll", "ull", "lossless" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									else
									{
										if (comboForceTune.getModel().getSize() != 8)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "film", "animation", "grain", "stillimage", "fastdecode","zerolatency", "psnr", "ssim" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
								}
								else if ("H.265".equals(function))
								{
									
									//comboForceProfile
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
									{
										if (comboAccel.getSelectedItem().equals("OSX VideoToolbox") && comboForceProfile.getModel().getSize() != 2)
										{
											comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main", "main422" }));
											comboForceProfile.setSelectedIndex(0);
										}
										else if (comboAccel.getSelectedItem().equals("OSX VideoToolbox") == false && comboForceProfile.getModel().getSize() != 1)
										{
											comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main" }));
											comboForceProfile.setSelectedIndex(0);
										}
									}
									else if (comboForceProfile.getModel().getSize() != 3 || comboForceProfile.getModel().getElementAt(0).toString().equals("main") == false)
									{
										comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "main", "main422", "main444" }));
										comboForceProfile.setSelectedIndex(0);
									}

									//comboForceTune					
									if (comboAccel.isEnabled() && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
									{
										if (comboForceTune.getModel().getSize() != 5)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "hq", "uhq", "ll", "ull", "lossless" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									else if (comboAccel.getSelectedItem().equals("Vulkan Video"))
									{
										if (comboForceTune.getModel().getSize() != 4)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "hq", "ll", "ull", "lossless" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									else
									{
										if (comboForceTune.getModel().getSize() != 6)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "grain", "animation", "fastdecode", "zerolatency", "psnr", "ssim" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
								}

								// Presets
								if (comboAccel.getSelectedItem()
										.equals(language.getProperty("aucune").toLowerCase()) == false) {
									if (comboAccel.getSelectedItem().equals("Nvidia NVENC")
											|| comboAccel.getSelectedItem().equals("Intel Quick Sync")) {
										if (comboForcePreset.getModel().getSize() != 7) {
											comboForcePreset.setModel(new DefaultComboBoxModel<String>(
													new String[] { "veryfast", "faster", "fast", "medium", "slow",
															"slower", "veryslow" }));
											comboForcePreset.setSelectedIndex(3);
										}
									} else if (comboAccel.getSelectedItem().equals("AMD AMF Encoder")
											|| comboAccel.getSelectedItem().equals("OSX VideoToolbox")
											|| comboAccel.getSelectedItem().equals("Vulkan Video")) {
										caseForcePreset.setSelected(false);
										caseForcePreset.setEnabled(false);
										comboForcePreset.setEnabled(false);
									}
								} else {

									if (("H.264".equals(function) || "H.265".equals(function))
											&& comboForcePreset.getModel().getSize() != 10) {
										comboForcePreset.setModel(new DefaultComboBoxModel<String>(
												new String[] { "ultrafast", "superfast", "veryfast", "faster", "fast",
														"medium", "slow", "slower", "veryslow", "placebo" }));
										comboForcePreset.setSelectedIndex(5);
										
									} else if ("H.266".equals(function) && comboForcePreset.getModel().getSize() != 5) {
										comboForcePreset.setModel(new DefaultComboBoxModel<String>(
												new String[] { "faster", "fast", "medium", "slow", "slower" }));
										comboForcePreset.setSelectedIndex(2);

									}
								}

								lblNiveaux.setVisible(true);

								// Ajout partie résolution
								grpResolution.removeAll();

								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
										caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
										caseForcerDAR.getLocation().y + 3);

								if (comboResolution.getItemCount() != 26) {
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
											language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
											"AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440",
											"1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "854x480",
											"720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160",
											"auto:1080", "auto:720", "50%", "25%" }));
								}
								
								// Set comboOptions
								changeComboOptions();

								grpBitrate.setVisible(true);
								grpBitrate.setBounds(grpBitrate.getX(),
										grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312, 156);
								lblMaximumBitrate.setVisible(true);
								maximumBitrate.setVisible(true);
								lblMaximumKbs.setVisible(true);
								lblAudioBitrate.setLocation(lblVideoBitrate.getX(),
										lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
								debitAudio.setLocation(debitVideo.getX(),
										maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
								lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
								lblSize.setLocation(lblVideoBitrate.getX(),
										lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
								bitrateSize.setLocation(debitVideo.getX(),
										debitAudio.getY() + debitAudio.getHeight() + 5);
								lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
								lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
								case2pass.setLocation(7, grpBitrate.getHeight() - 32);
								caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());

								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
								}
								else
									lblPad.setVisible(true);

								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7,caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (anim)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(),
										grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);
								if (comboAudioCodec.getItemCount() != 14 || comboAudioCodec.getModel().getElementAt(0).equals("AAC") == false)
								{
									if (lblAudioMapping.getItemCount() != 4) {
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
										
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									}									
									
									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "AAC",
											"MP3", "AC3", "Opus", "FLAC", "PCM 16Bits", "PCM 24Bits", "PCM 32Bits",
											"ALAC 16Bits", "ALAC 24Bits", "Dolby Digital Plus",
											language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
									comboAudioCodec.setSelectedIndex(0);
									caseChangeAudioCodec.setSelected(true);
									comboAudioCodec.setEnabled(true);
									lbl48k.setEnabled(true);

									debitAudio.setModel(comboAudioBitrate.getModel());
									debitAudio.setSelectedIndex(10);
								}

								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}

								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);

								if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
								|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.add(lblAudio3);
									grpSetAudio.add(comboAudio3);
									grpSetAudio.add(lblAudio4);
									grpSetAudio.add(comboAudio4);
									grpSetAudio.add(lblAudio5);
									grpSetAudio.add(comboAudio5);
									grpSetAudio.add(lblAudio6);
									grpSetAudio.add(comboAudio6);
									grpSetAudio.add(lblAudio7);
									grpSetAudio.add(comboAudio7);
									grpSetAudio.add(lblAudio8);
									grpSetAudio.add(comboAudio8);
								}

								if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
										&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
										&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
										&& comboAudio7.getSelectedIndex() == 16
										&& comboAudio8.getSelectedIndex() == 16) {
									comboAudio1.setSelectedIndex(0);
									comboAudio2.setSelectedIndex(1);
									comboAudio3.setSelectedIndex(2);
									comboAudio4.setSelectedIndex(3);
									comboAudio5.setSelectedIndex(4);
									comboAudio6.setSelectedIndex(5);
									comboAudio7.setSelectedIndex(6);
									comboAudio8.setSelectedIndex(7);
								}

								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(),
										grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(),
										grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(),
										grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(),
										grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(),
										grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(),
										grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(),
										grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(),
										grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(),
										grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(),
										grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								grpImageFilter.setVisible(false);

								if ("H.264".equals(function)) {
									if (comboColorspace.getItemCount() != 4) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
														"Rec. 2020 HLG 10bits" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								} else if ("H.265".equals(function)) {
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
									{
										if (comboColorspace.getItemCount() != 6) {
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(
													new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
															"Rec. 2020 HLG 10bits", "Rec. 2020 PQ 12bits",
															"Rec. 2020 HLG 12bits" }));

											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
										else
										{/*
											if (colorspacePreset != null && action == false)
											{
												comboColorspace.setSelectedItem(colorspacePreset);
												colorspacePreset = null;
											}*/
										}
									}
									else
									{
										if (comboColorspace.getItemCount() != 8) {
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(
													new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
															"Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits",
															"Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits",
															"Rec. 2020 HLG 12bits" }));

											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
								} else if ("H.266".equals(function)) {
									if (comboColorspace.getItemAt(0).equals("Rec. 709 10bits") == false) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {
												"Rec. 709 10bits", "Rec. 2020 PQ 10bits", "Rec. 2020 HLG 10bits" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}

								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

								// CalculH264
								if (list.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();

								// Qualité Max
								if (comboAccel.getSelectedItem()
										.equals(language.getProperty("aucune").toLowerCase()) == false
										&& comboAccel.getSelectedItem().equals("OSX VideoToolbox")) {
									caseQMax.setEnabled(false);
								} else
									caseQMax.setEnabled(true);

								// Ajout des fonctions avancées
								grpAdvanced.removeAll();

								// grpAdvanced
								caseForcerDesentrelacement.setLocation(7, 14);
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(
										caseForcerDesentrelacement.getLocation().x
												+ caseForcerDesentrelacement.getWidth() + 4,
										caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4,
										lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);

								if ("H.266".equals(function)) {
									caseForcePreset.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcePreset);
									comboForcePreset.setLocation(caseForcePreset.getLocation().x + caseForcePreset.getWidth() + 4, caseForcePreset.getLocation().y + 4);
									grpAdvanced.add(comboForcePreset);
									
									caseForceOutput.setLocation(7, caseForcePreset.getLocation().y + 17);
									grpAdvanced.add(caseForceOutput);
									lblNiveaux.setLocation(caseForceOutput.getLocation().x + caseForceOutput.getWidth() + 4, caseForceOutput.getLocation().y + 4);
									grpAdvanced.add(lblNiveaux);

									caseGOP.setLocation(7, caseForceOutput.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
								} else {
									caseForcerEntrelacement.setLocation(7,
											caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForcerEntrelacement);

									caseForceOutput.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForceOutput);
									lblNiveaux.setLocation(caseForceOutput.getLocation().x + caseForceOutput.getWidth() + 4, caseForceOutput.getLocation().y + 4);
									grpAdvanced.add(lblNiveaux);
									caseForceLevel.setLocation(7, caseForceOutput.getLocation().y + 17);
									grpAdvanced.add(caseForceLevel);
									comboForceProfile.setLocation(
											caseForceLevel.getLocation().x + caseForceLevel.getWidth() + 4,
											caseForceLevel.getLocation().y + 4);
									grpAdvanced.add(comboForceProfile);
									comboForceLevel.setLocation(
											comboForceProfile.getLocation().x + comboForceProfile.getWidth() + 4,
											comboForceProfile.getLocation().y);
									grpAdvanced.add(comboForceLevel);

									caseForcePreset.setLocation(7, caseForceLevel.getLocation().y + 17);
									grpAdvanced.add(caseForcePreset);
									comboForcePreset.setLocation(
											caseForcePreset.getLocation().x + caseForcePreset.getWidth() + 4,
											caseForcePreset.getLocation().y + 4);
									grpAdvanced.add(comboForcePreset);

									caseForceTune.setLocation(7, caseForcePreset.getLocation().y + 17);
									grpAdvanced.add(caseForceTune);
									comboForceTune.setLocation(
											caseForceTune.getLocation().x + caseForceTune.getWidth() + 4,
											caseForceTune.getLocation().y + 4);
									grpAdvanced.add(comboForceTune);

									if (comboFonctions.getSelectedItem().equals("H.265")) {
										caseAlpha.setLocation(7, caseForceTune.getY() + 17);
										grpAdvanced.add(caseAlpha);
										comboAlpha.setLocation(caseAlpha.getX() + caseAlpha.getWidth() + 4, caseAlpha.getLocation().y + 4);
										grpAdvanced.add(comboAlpha);

										if (comboAccel.getSelectedItem().equals("OSX VideoToolbox")
												|| comboAccel.getSelectedItem()
														.equals(language.getProperty("aucune").toLowerCase())) {
											caseAlpha.setEnabled(true);
										}
										else
										{
											caseAlpha.setEnabled(false);
											comboAlpha.setEnabled(false);
										}

										caseFastStart.setLocation(7, caseAlpha.getY() + 17);
									} else
										caseFastStart.setLocation(7, caseForceTune.getY() + 17);

									grpAdvanced.add(caseFastStart);

									caseGOP.setLocation(7, caseFastStart.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
								}

								gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
								grpAdvanced.add(gopSize);

								if ("H.264".equals(function))
								{
									caseCABAC.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseCABAC);
									
									caseEncoderParams.setText(language.getProperty("btnAdd") +  " x264-params" + language.getProperty("colon"));
									caseEncoderParams.setSize(caseEncoderParams.getPreferredSize().width, 23);
									caseEncoderParams.setLocation(7, caseCABAC.getLocation().y + 17);
									grpAdvanced.add(caseEncoderParams);
									textEncoderParams.setLocation(caseEncoderParams.getX() + caseEncoderParams.getWidth() + 3, caseEncoderParams.getY() + 3);
									grpAdvanced.add(textEncoderParams);	
								}
								else if ("H.265".equals(function))
								{									
									caseEncoderParams.setText(language.getProperty("btnAdd") +  " x265-params" + language.getProperty("colon"));
									caseEncoderParams.setSize(caseEncoderParams.getPreferredSize().width, 23);
									caseEncoderParams.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseEncoderParams);
									textEncoderParams.setLocation(caseEncoderParams.getX() + caseEncoderParams.getWidth() + 3, caseEncoderParams.getY() + 3);
									grpAdvanced.add(textEncoderParams);	
								}
								else if ("H.266".equals(function))
								{									
									caseEncoderParams.setText(language.getProperty("btnAdd") +  " vvenc-params" + language.getProperty("colon"));
									caseEncoderParams.setSize(caseEncoderParams.getPreferredSize().width, 23);
									caseEncoderParams.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseEncoderParams);
									textEncoderParams.setLocation(caseEncoderParams.getX() + caseEncoderParams.getWidth() + 3, caseEncoderParams.getY() + 3);
									grpAdvanced.add(textEncoderParams);	
								}
								
								
								if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
								{	
									caseEncoderParams.setEnabled(true);		
								}
								else
								{
									caseEncoderParams.setSelected(false);
									caseEncoderParams.setEnabled(false);
									textEncoderParams.setEnabled(false);	
								}

								caseDecimate.setLocation(7, caseEncoderParams.getLocation().y + 17);
								grpAdvanced.add(caseDecimate);								
								caseConform.setLocation(7, caseDecimate.getLocation().y + 17);
								grpAdvanced.add(caseConform);
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4,
										comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4,
										comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);
								caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
										caseCreateTree.getY() + 4);
								grpAdvanced.add(comboCreateTree);
								casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);
								casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
								grpAdvanced.add(casePreserveSubs);

							} else if ("WMV".equals(function) || "MPEG-1".equals(function) || "MPEG-2".equals(function)
									|| "VP8".equals(function) || "VP9".equals(function) || "AV1".equals(function)
									|| "Theora".equals(function) || "MJPEG".equals(function)
									|| "Xvid".equals(function)) {

								addToList.setText(language.getProperty("filesVideoOrAudioOrPicture"));

								// HWaccel
								if (anim)
									FFMPEG.detectHardwareAcceleration(function);

								if (subtitlesBurn) {
									caseDisplay.setEnabled(true);
								} else {
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								}

								case2pass.setEnabled(true);
								lblNiveaux.setVisible(true);
								grpColorimetry.setVisible(false);

								// Ajout partie résolution
								grpResolution.removeAll();

								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(lblPad);
								grpResolution.add(lblScreenshot);
								grpResolution.add(btnNoUpscale);
								btnNoUpscale.setLocation(7, 47);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
										caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(caseForcerDAR);
								caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
								grpResolution.add(comboDAR);
								comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
										caseForcerDAR.getLocation().y + 3);

								if (comboResolution.getItemCount() != 26) {
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
											language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
											"AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160", "2560x1440",
											"1920x1080", "1440x1080", "1280x720", "1024x768", "1024x576", "854x480",
											"720x576", "640x360", "320x180", "3840:auto", "1920:auto", "auto:2160",
											"auto:1080", "auto:720", "50%", "25%" }));
								}
								
								// Set comboOptions
								changeComboOptions();

								grpBitrate.setVisible(true);
								
								if ("AV1".equals(function) && lblVBR.getText().equals("CQ")) {
									grpBitrate.setBounds(grpBitrate.getX(),
											grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312,
											156);
									lblMaximumBitrate.setVisible(true);
									maximumBitrate.setVisible(true);
									lblMaximumKbs.setVisible(true);
									lblAudioBitrate.setLocation(lblVideoBitrate.getX(),
											lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(),
											maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
								} else {
									grpBitrate.setBounds(grpBitrate.getX(),
											grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312,
											130);
									lblMaximumBitrate.setVisible(false);
									maximumBitrate.setVisible(false);
									lblMaximumKbs.setVisible(false);
									lblAudioBitrate.setLocation(lblVideoBitrate.getX(),
											lblVideoBitrate.getY() + lblVideoBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(),
											debitVideo.getY() + debitVideo.getHeight() + 5);
								}
								lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
								lblSize.setLocation(lblVideoBitrate.getX(),
										lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
								bitrateSize.setLocation(debitVideo.getX(),
										debitAudio.getY() + debitAudio.getHeight() + 5);
								lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
								lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
								case2pass.setLocation(7, grpBitrate.getHeight() - 32);
								caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());

								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
								{
									lblPad.setVisible(false);
								}
								else
									lblPad.setVisible(true);

								if ("VP9".equals(function) || "AV1".equals(function)) {
									lblVBR.setVisible(true);

									if (lblVBR.getText().equals("CQ")) {
										bitrateSize.setText("-");
										lblVideoBitrate.setText(language.getProperty("lblValue"));
										lblKbsH264.setVisible(false);
										h264lines.setVisible(false);
										case2pass.setSelected(false);
										case2pass.setEnabled(false);
									}
								} else {
									lblVBR.setVisible(false);
									case2pass.setEnabled(true);

									if (lblVBR.getText().equals("CQ")) // Si la fonction ne prend pas en charge CQ
									{
										debitVideo.setModel(new DefaultComboBoxModel<String>(
												new String[] { "50000", "40000", "30000", "25000", "20000", "15000",
														"10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000",
														"500", language.getProperty("lblBest").toLowerCase(),
														language.getProperty("lblGood").toLowerCase(), "auto" }));
										debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
										lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
										lblKbsH264.setVisible(true);
										h264lines.setVisible(true);
										FFPROBE.setLength();
									}
									lblVBR.setText("VBR");
								}

								if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source"))
										|| comboResolution.getSelectedItem().toString().contains("AI")) {
									lblPad.setVisible(false);
								} else {
									lblPad.setVisible(true);
								}

								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7,caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (anim)
									grpSetAudio.setSize(312, 17);
								grpSetAudio.setLocation(grpSetAudio.getX(),
										grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);

								if (comboAudioCodec.getItemCount() != 6 && "MJPEG".equals(function)) {
									if (lblAudioMapping.getItemCount() != 4) {
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
										
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									}									

									comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] {
											"PCM 16Bits", "PCM 24Bits", "PCM 32Bits", language.getProperty("codecCopy"),
											language.getProperty("noAudio"), language.getProperty("custom") }));
									comboAudioCodec.setSelectedIndex(0);
									debitAudio.setModel(comboAudioBitrate.getModel());
									debitAudio.setSelectedIndex(0);
								} else if ("MJPEG".equals(function) == false) {	
									
									if (lblAudioMapping.getItemCount() != 4)
									{
										lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
												new String[] { language.getProperty("stereo"), "Multi",
														language.getProperty("mono"), "Mix" }));
										
										lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
									}
									
									if (comboAudioCodec.getModel().getElementAt(0).equals("WMA") == false
											&& "WMV".equals(function)) {
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA",
												language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
									} else if (comboAudioCodec.getModel().getElementAt(0).equals("MP2") == false
											&& ("MPEG-1".equals(function) || "MPEG-2".equals(function))) {
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2",
												language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
									} else if (comboAudioCodec.getModel().getElementAt(0).equals("Opus") == false
											&& ("VP8".equals(function) || "VP9".equals(function)
													|| "AV1".equals(function))) {
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus",
												"AAC", "Vorbis", "FLAC", language.getProperty("codecCopy"),
												language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
									} else if (comboAudioCodec.getModel().getElementAt(0).equals("Vorbis") == false
											&& "Theora".equals(function)) {
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(
												new String[] { "Vorbis", language.getProperty("codecCopy"),
														language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
									} else if (comboAudioCodec.getModel().getElementAt(0).equals("MP3") == false
											&& "Xvid".equals(function)) {
										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3",
												language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
									}
								}

								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);
								caseChangeAudioCodec.setSelected(true);
								comboAudioCodec.setEnabled(true);
								lbl48k.setEnabled(true);
								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}

								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);

								if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
								|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.add(lblAudio3);
									grpSetAudio.add(comboAudio3);
									grpSetAudio.add(lblAudio4);
									grpSetAudio.add(comboAudio4);
									grpSetAudio.add(lblAudio5);
									grpSetAudio.add(comboAudio5);
									grpSetAudio.add(lblAudio6);
									grpSetAudio.add(comboAudio6);
									grpSetAudio.add(lblAudio7);
									grpSetAudio.add(comboAudio7);
									grpSetAudio.add(lblAudio8);
									grpSetAudio.add(comboAudio8);
								}

								if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
										&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
										&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
										&& comboAudio7.getSelectedIndex() == 16
										&& comboAudio8.getSelectedIndex() == 16) {
									comboAudio1.setSelectedIndex(0);
									comboAudio2.setSelectedIndex(1);
									comboAudio3.setSelectedIndex(2);
									comboAudio4.setSelectedIndex(3);
									comboAudio5.setSelectedIndex(4);
									comboAudio6.setSelectedIndex(5);
									comboAudio7.setSelectedIndex(6);
									comboAudio8.setSelectedIndex(7);
								}

								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(),
										grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(),
										grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpColorimetry.getX(),
										grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(),
										grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								grpAudio.setVisible(false);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(),
										grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(),
										grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(),
										grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(true);
								grpTransitions.setLocation(grpTransitions.getX(),
										grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpImageSequence.setVisible(true);
								grpImageSequence.setLocation(grpImageSequence.getX(),
										grpTransitions.getSize().height + grpTransitions.getLocation().y + 6);
								grpSetTimecode.setVisible(true);
								grpSetTimecode.setLocation(grpSetTimecode.getX(),
										grpImageSequence.getSize().height + grpImageSequence.getLocation().y + 6);
								grpImageFilter.setVisible(false);

								if ("AV1".equals(function)) {
									if (comboAccel.getSelectedItem()
											.equals(language.getProperty("aucune").toLowerCase()) == false) {
										if (comboColorspace.getItemCount() != 6) {
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(
													new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
															"Rec. 2020 HLG 10bits", "Rec. 2020 PQ 12bits",
															"Rec. 2020 HLG 12bits" }));

											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									} else {
										if (comboColorspace.getItemCount() != 8) {
											comboColorspace.setModel(new DefaultComboBoxModel<Object>(
													new String[] { "Rec. 709", "Rec. 709 10bits", "Rec. 2020 PQ 10bits",
															"Rec. 2020 PQ 10bits HDR", "Rec. 2020 HLG 10bits",
															"Rec. 2020 HLG 10bits HDR", "Rec. 2020 PQ 12bits",
															"Rec. 2020 HLG 12bits" }));

											comboHDRvalue.setVisible(false);
											lblHDR.setVisible(false);
										}
									}
								} else if ("MPEG-2".equals(function)) {
									if (comboColorspace.getItemCount() != 6) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(new String[] {
												"Rec. 709", "Rec. 709 4:2:2", "Rec. 2020 PQ", "Rec. 2020 PQ 4:2:2",
												"Rec. 2020 HLG", "Rec. 2020 HLG 4:2:2" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								} else {
									if (comboColorspace.getItemCount() != 3) {
										comboColorspace.setModel(new DefaultComboBoxModel<Object>(
												new String[] { "Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG" }));

										comboHDRvalue.setVisible(false);
										lblHDR.setVisible(false);
									}
								}
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpSetTimecode.getSize().height + grpSetTimecode.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

								// CalculH264
								if (list.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();
								// Qualité Max
								if (comboFonctions.getSelectedItem().equals("Theora")
										|| comboFonctions.getSelectedItem().equals("MJPEG"))
									caseQMax.setEnabled(false);
								else
									caseQMax.setEnabled(true);

								// Ajout des fonctions avancées
								grpAdvanced.removeAll();

								// grpAdvanced
								if (System.getProperty("os.name").contains("Windows")
										&& ("VP9".equals(function) || "AV1".equals(function))) {
									lblVBR.setVisible(true);

									if (lblVBR.getText().equals("CQ") || comboAccel.getSelectedItem()
											.equals(language.getProperty("aucune").toLowerCase()) == false) {
										case2pass.setSelected(false);
										case2pass.setEnabled(false);
									}
								}

								caseForcerDesentrelacement.setLocation(7, 14);
								grpAdvanced.add(caseForcerDesentrelacement);
								lblTFF.setLocation(
										caseForcerDesentrelacement.getLocation().x
												+ caseForcerDesentrelacement.getWidth() + 4,
										caseForcerDesentrelacement.getLocation().y + 4);
								grpAdvanced.add(lblTFF);
								comboForcerDesentrelacement.setLocation(lblTFF.getLocation().x + lblTFF.getWidth() + 4,
										lblTFF.getLocation().y - 1);
								grpAdvanced.add(comboForcerDesentrelacement);

								if ("VP8".equals(function) || "VP9".equals(function))
								{
									if (caseQMax.isSelected() == false) {
										caseForceQuality.setEnabled(true);
										caseForcePreset.setEnabled(true);
									}

									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())) {
										caseForceTune.setEnabled(true);
									}

									if (comboForceTune.getModel().getSize() != 3) {
										comboForceTune.setModel(new DefaultComboBoxModel<String>(
												new String[] { "default", "screen", "film" }));
										comboForceTune.setSelectedIndex(0);
									}

									caseForceQuality.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForceQuality);
									comboForceQuality.setLocation(
											caseForceQuality.getLocation().x + caseForceQuality.getWidth() + 4,
											caseForceQuality.getLocation().y + 4);
									grpAdvanced.add(comboForceQuality);
									caseForceSpeed.setLocation(7, caseForceQuality.getLocation().y + 17);
									grpAdvanced.add(caseForceSpeed);
									comboForceSpeed.setLocation(
											caseForceSpeed.getLocation().x + caseForceSpeed.getWidth() + 4,
											caseForceSpeed.getLocation().y + 4);
									grpAdvanced.add(comboForceSpeed);
									caseForceTune.setLocation(7, caseForceSpeed.getLocation().y + 17);
									grpAdvanced.add(caseForceTune);
									comboForceTune.setLocation(
											caseForceTune.getLocation().x + caseForceTune.getWidth() + 4,
											caseForceTune.getLocation().y + 4);
									grpAdvanced.add(comboForceTune);
									comboForceTune.setModel(new DefaultComboBoxModel<String>(
											new String[] { "film", "animation", "grain", "stillimage", "fastdecode",
													"zerolatency", "psnr", "ssim" }));
									if (caseColorspace.isSelected() == false || caseColorspace.isSelected()
											&& comboColorspace.getSelectedItem().toString().equals("Rec. 709")) {
										caseAlpha.setEnabled(true);
									}
									caseAlpha.setLocation(7, caseForceTune.getY() + 17);
									grpAdvanced.add(caseAlpha);
									comboAlpha.setLocation(caseAlpha.getX() + caseAlpha.getWidth() + 4, caseAlpha.getLocation().y + 4);
									grpAdvanced.add(comboAlpha);
									caseGOP.setLocation(7, caseAlpha.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
									caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);
									caseConform.setLocation(7, caseDecimate.getY() + 17);
									grpAdvanced.add(caseConform);
								}
								else if ("AV1".equals(function))
								{
									if (caseQMax.isSelected() == false)
										caseForceLevel.setEnabled(true);

									if (comboForceProfile.getModel().getSize() != 1) {
										comboForceProfile
												.setModel(new DefaultComboBoxModel<String>(new String[] { "main" }));
										comboForceProfile.setSelectedIndex(0);
									}

									caseForceOutput.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseForceOutput);
									lblNiveaux.setLocation(caseForceOutput.getLocation().x + caseForceOutput.getWidth() + 4, caseForceOutput.getLocation().y + 4);
									grpAdvanced.add(lblNiveaux);
									
									caseForceLevel.setLocation(7, caseForceOutput.getLocation().y + 17);
									grpAdvanced.add(caseForceLevel);
									comboForceProfile.setLocation(caseForceLevel.getLocation().x + caseForceLevel.getWidth() + 4,caseForceLevel.getLocation().y + 4);
									grpAdvanced.add(comboForceProfile);
									comboForceLevel.setLocation(comboForceProfile.getLocation().x + comboForceProfile.getWidth() + 4, comboForceProfile.getLocation().y);
									grpAdvanced.add(comboForceLevel);
									caseForceSpeed.setLocation(7, caseForceLevel.getLocation().y + 17);
									grpAdvanced.add(caseForceSpeed);
									comboForceSpeed.setLocation(
											caseForceSpeed.getLocation().x + caseForceSpeed.getWidth() + 4,
											caseForceSpeed.getLocation().y + 4);
									grpAdvanced.add(comboForceSpeed);
									caseForceTune.setLocation(7, caseForceSpeed.getLocation().y + 17);
									grpAdvanced.add(caseForceTune);
									comboForceTune.setLocation(
											caseForceTune.getLocation().x + caseForceTune.getWidth() + 4,
											caseForceTune.getLocation().y + 4);
									grpAdvanced.add(comboForceTune);
									
									//comboForceTune					
									if (comboAccel.isEnabled() && comboAccel.getSelectedItem().equals("Nvidia NVENC"))
									{
										if (comboForceTune.getModel().getSize() != 5)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "hq", "uhq", "ll", "ull", "lossless" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									else if (comboAccel.getSelectedItem().equals("Vulkan Video"))
									{
										if (comboForceTune.getModel().getSize() != 4)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "hq", "ll", "ull", "lossless" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									else
									{
										if (comboForceTune.getModel().getSize() != 3)
										{
											comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "visual quality", "psnr", "ssim" }));
											comboForceTune.setSelectedIndex(0);
										}
									}
									
									caseFastStart.setLocation(7, caseForceTune.getLocation().y + 17);
									grpAdvanced.add(caseFastStart);
									caseFastDecode.setLocation(7, caseFastStart.getLocation().y + 17);
									grpAdvanced.add(caseFastDecode);									
									comboFastDecode.setLocation(caseFastDecode.getX() + caseFastDecode.getWidth() + 3, caseFastDecode.getY() + 3);
									grpAdvanced.add(comboFastDecode);									
									caseVarianceBoost.setLocation(7, caseFastDecode.getLocation().y + 17);
									grpAdvanced.add(caseVarianceBoost);									
									comboVarianceBoost.setLocation(caseVarianceBoost.getX() + caseVarianceBoost.getWidth() + 3, caseVarianceBoost.getY() + 3);
									grpAdvanced.add(comboVarianceBoost);									
									caseGOP.setLocation(7, caseVarianceBoost.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
									caseFilmGrain.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseFilmGrain);
									comboFilmGrain.setLocation(caseFilmGrain.getX() + caseFilmGrain.getWidth() + 3, caseFilmGrain.getY() + 3);
									grpAdvanced.add(comboFilmGrain);
									caseFilmGrainDenoise.setLocation(7, caseFilmGrain.getLocation().y + 17);
									grpAdvanced.add(caseFilmGrainDenoise);
									comboFilmGrainDenoise.setLocation(caseFilmGrainDenoise.getX() + caseFilmGrainDenoise.getWidth() + 3, caseFilmGrainDenoise.getY() + 3);
									grpAdvanced.add(comboFilmGrainDenoise);
																		
									caseEncoderParams.setText(language.getProperty("btnAdd") +  " svtav1-params" + language.getProperty("colon"));
									caseEncoderParams.setSize(caseEncoderParams.getPreferredSize().width, 23);
									caseEncoderParams.setLocation(7, caseFilmGrainDenoise.getLocation().y + 17);
									grpAdvanced.add(caseEncoderParams);
									textEncoderParams.setLocation(caseEncoderParams.getX() + caseEncoderParams.getWidth() + 3, caseEncoderParams.getY() + 3);
									grpAdvanced.add(textEncoderParams);					
									
									caseDecimate.setLocation(7, caseEncoderParams.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);									
									caseConform.setLocation(7, caseDecimate.getY() + 17);
									grpAdvanced.add(caseConform);
									
									if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))
									{	
										caseEncoderParams.setEnabled(true);		
									}
									else
									{
										caseEncoderParams.setSelected(false);
										caseEncoderParams.setEnabled(false);
										textEncoderParams.setEnabled(false);	
									}									
								}
								else if ("MPEG-1".equals(function) || "MPEG-2".equals(function) || "Theora".equals(function))
								{
									caseGOP.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
									caseDecimate.setLocation(7, caseGOP.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);
									caseConform.setLocation(7, caseDecimate.getY() + 17);
									grpAdvanced.add(caseConform);
								}
								else
								{
									caseDecimate.setLocation(7, caseForcerDesentrelacement.getLocation().y + 17);
									grpAdvanced.add(caseDecimate);
									caseConform.setLocation(7, caseDecimate.getY() + 17);
									grpAdvanced.add(caseConform);
								}
								
								comboConform.setLocation(caseConform.getX() + caseConform.getWidth() + 4, caseConform.getLocation().y + 4);
								grpAdvanced.add(comboConform);
								lblToConform.setLocation(comboConform.getX() + comboConform.getWidth() + 4,
										comboConform.getLocation().y - 2);
								grpAdvanced.add(lblToConform);
								comboFPS.setLocation(lblToConform.getX() + lblToConform.getWidth() + 3,
										caseConform.getLocation().y + 4);
								grpAdvanced.add(comboFPS);
								lblIsConform.setLocation(comboFPS.getX() + comboFPS.getWidth() + 4,
										comboFPS.getLocation().y);
								grpAdvanced.add(lblIsConform);
								caseCreateTree.setLocation(7, caseConform.getLocation().y + 17);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
										caseCreateTree.getY() + 4);
								grpAdvanced.add(comboCreateTree);
								casePreserveMetadata.setLocation(7, caseCreateTree.getLocation().y + 17);
								grpAdvanced.add(casePreserveMetadata);
								casePreserveSubs.setLocation(7, casePreserveMetadata.getLocation().y + 17);
								grpAdvanced.add(casePreserveSubs);

							} else if ("DV".equals(function)) {

								addToList.setText(language.getProperty("filesVideo"));
								caseDisplay.setEnabled(true);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpAdvanced.setVisible(false);
								btnReset.setVisible(false);

							} else if ("DVD".equals(function) || "Blu-ray".equals(function)) {

								addToList.setText(language.getProperty("filesVideo"));

								if (comboFonctions.getSelectedItem().equals("DVD") || subtitlesBurn == false) {
									caseDisplay.setEnabled(false);
									caseDisplay.setSelected(false);
								} else
									caseDisplay.setEnabled(true);

								caseForcerProgressif.setEnabled(true);
								caseForcerEntrelacement.setEnabled(true);
								grpImageSequence.setVisible(false);
								grpResolution.setVisible(false);
								grpBitrate.setVisible(false);

								if ("Blu-ray".equals(function)) {
									if (anim) {
										if (comboFilter.getSelectedIndex() == 0) // H.264
										{
											debitVideo.setSelectedItem(38000);
										} else // H.265
											debitVideo.setSelectedItem(50000);
									}

									// Ajout partie résolution
									grpResolution.removeAll();

									grpResolution.setVisible(true);
									grpResolution.setLocation(grpResolution.getX(), 30);
									grpResolution.add(lblImageSize);
									grpResolution.add(comboResolution);
									grpResolution.add(lblPad);
									grpResolution.add(lblScreenshot);
									grpResolution.add(btnNoUpscale);
									btnNoUpscale.setLocation(7, 47);
									grpResolution.add(caseRotate);
									caseRotate.setLocation(7,
											btnNoUpscale.getLocation().y + btnNoUpscale.getHeight() + 3);
									grpResolution.add(comboRotate);
									comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
											caseRotate.getLocation().y + 3);
									grpResolution.add(caseMiror);
									caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
									grpResolution.add(caseForcerDAR);
									caseForcerDAR.setLocation(7, caseRotate.getLocation().y + caseRotate.getHeight());
									grpResolution.add(comboDAR);
									comboDAR.setLocation(caseForcerDAR.getLocation().x + caseForcerDAR.getWidth() + 4,
											caseForcerDAR.getLocation().y + 3);

									if (comboResolution.getItemCount() != 26) {
										comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
												language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
												"AI animation 4x", "AI animation 2x", "4096x2160", "3840x2160",
												"2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x768",
												"1024x576", "854x480", "720x576", "640x360", "320x180", "3840:auto",
												"1920:auto", "auto:2160", "auto:1080", "auto:720", "50%", "25%" }));
									}
									
									// Set comboOptions
									changeComboOptions();

									grpBitrate.setVisible(true);
									grpBitrate.setBounds(grpBitrate.getX(),
											grpResolution.getSize().height + grpResolution.getLocation().y + 6, 312,
											156);
									lblMaximumBitrate.setVisible(true);
									maximumBitrate.setVisible(true);
									lblMaximumKbs.setVisible(true);
									lblAudioBitrate.setLocation(lblVideoBitrate.getX(),
											lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11);
									debitAudio.setLocation(debitVideo.getX(),
											maximumBitrate.getY() + maximumBitrate.getHeight() + 5);
									lblAudioKbs.setLocation(lblKbsH264.getX(), debitAudio.getY() + 3);
									lblSize.setLocation(lblVideoBitrate.getX(),
											lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11);
									bitrateSize.setLocation(debitVideo.getX(),
											debitAudio.getY() + debitAudio.getHeight() + 5);
									lblFileSizeMo.setLocation(lblKbsH264.getX(), bitrateSize.getY() + 3);
									lock.setLocation(bitrateSize.getX() - 21 - 3, bitrateSize.getY());
									case2pass.setLocation(7, grpBitrate.getHeight() - 32);
									caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());

									if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source")) || comboResolution.getSelectedItem().toString().contains("AI"))
									{
										lblPad.setVisible(false);
									}
									else
										lblPad.setVisible(true);
								}

								grpSetAudio.setVisible(true);
								caseNormalizeAudio.setLocation(7,caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight());
								comboNormalizeAudio.setLocation(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,caseNormalizeAudio.getLocation().y + 3);
								caseEqualizer.setLocation(7,caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight());
								
								if (anim)
									grpSetAudio.setSize(312, 17);

								if ("Blu-ray".equals(function)) {
									grpSetAudio.setLocation(grpSetAudio.getX(),
											grpBitrate.getSize().height + grpBitrate.getLocation().y + 6);
								} else
									grpSetAudio.setLocation(grpSetAudio.getX(), 30);

								// grpSetAudio
								grpSetAudio.removeAll();
								grpSetAudio.add(caseNormalizeAudio);
								grpSetAudio.add(comboNormalizeAudio);
								grpSetAudio.add(caseEqualizer);
								grpSetAudio.add(caseChangeAudioCodec);

								caseChangeAudioCodec.setSelected(true);
								comboAudioCodec.setEnabled(true);
								lbl48k.setEnabled(true);

								if ("Blu-ray".equals(function)) {
									if (comboAudioCodec.getItemCount() != 5
											|| comboAudioCodec.getItemAt(0).equals("AC3") == false) {
										if (lblAudioMapping.getItemCount() != 4) {
											lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
													new String[] { language.getProperty("stereo"), "Multi",
															language.getProperty("mono"), "Mix" }));
											
											lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
										}

										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "AC3",
												"Dolby Digital Plus", language.getProperty("codecCopy"),
												language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
										debitAudio.setModel(comboAudioBitrate.getModel());
										debitAudio.setSelectedIndex(5);
									}
								} else {
									if (comboAudioCodec.getItemCount() != 4
											|| comboAudioCodec.getItemAt(0).equals("AC3") == false) {
										if (lblAudioMapping.getItemCount() != 4) {
											lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
													new String[] { language.getProperty("stereo"), "Multi",
															language.getProperty("mono"), "Mix" }));
											
											lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
										}

										comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "AC3",
												language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
										comboAudioCodec.setSelectedIndex(0);
										debitAudio.setModel(comboAudioBitrate.getModel());
										debitAudio.setSelectedIndex(5);
									}
								}

								caseNormalizeAudio.setEnabled(true);
								caseEqualizer.setEnabled(true);
								caseChangeAudioCodec.setEnabled(false);

								grpSetAudio.add(comboAudioCodec);
								grpSetAudio.add(lblAudioMapping);
								grpSetAudio.add(lbl48k);
								lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5,
										lblKbs.getLocation().y);

								lblAudio1.setLocation(12,
										caseEqualizer.getY() + caseEqualizer.getHeight() + 2);
								comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7,
										lblAudio1.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio1);
									grpSetAudio.add(comboAudio1);
								}
								lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12,
										lblAudio1.getLocation().y);
								comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7,
										lblAudio2.getLocation().y + 1);
								if (lblAudioMapping.getSelectedItem().toString().equals("Mix") == false) {
									grpSetAudio.add(lblAudio2);
									grpSetAudio.add(comboAudio2);
								}

								lblAudio3.setLocation(lblAudio1.getX(),
										lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
								comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7,
										lblAudio3.getLocation().y + 1);
								lblAudio4.setLocation(lblAudio2.getX(),
										lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
								comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7,
										lblAudio4.getLocation().y + 1);
								lblAudio5.setLocation(lblAudio3.getX(),
										lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
								comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7,
										lblAudio5.getLocation().y + 1);
								lblAudio6.setLocation(lblAudio4.getX(),
										lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
								comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7,
										lblAudio6.getLocation().y + 1);
								lblAudio7.setLocation(lblAudio5.getX(),
										lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
								comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7,
										lblAudio7.getLocation().y + 1);
								lblAudio8.setLocation(lblAudio6.getX(),
										lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
								comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7,
										lblAudio8.getLocation().y + 1);

								if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
								|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
								{
									grpSetAudio.add(lblAudio3);
									grpSetAudio.add(comboAudio3);
									grpSetAudio.add(lblAudio4);
									grpSetAudio.add(comboAudio4);
									grpSetAudio.add(lblAudio5);
									grpSetAudio.add(comboAudio5);
									grpSetAudio.add(lblAudio6);
									grpSetAudio.add(comboAudio6);
									grpSetAudio.add(lblAudio7);
									grpSetAudio.add(comboAudio7);
									grpSetAudio.add(lblAudio8);
									grpSetAudio.add(comboAudio8);
								}

								if (comboAudio1.getSelectedIndex() == 0 && comboAudio2.getSelectedIndex() == 1
										&& comboAudio3.getSelectedIndex() == 2 && comboAudio4.getSelectedIndex() == 3
										&& comboAudio5.getSelectedIndex() == 16 && comboAudio6.getSelectedIndex() == 16
										&& comboAudio7.getSelectedIndex() == 16
										&& comboAudio8.getSelectedIndex() == 16) {
									comboAudio1.setSelectedIndex(0);
									comboAudio2.setSelectedIndex(1);
									comboAudio3.setSelectedIndex(2);
									comboAudio4.setSelectedIndex(3);
									comboAudio5.setSelectedIndex(4);
									comboAudio6.setSelectedIndex(5);
									comboAudio7.setSelectedIndex(6);
									comboAudio8.setSelectedIndex(7);
								}

								grpAudio.setVisible(false);
								grpCrop.setVisible(false);
								grpOverlay.setVisible(false);
								grpSubtitles.setVisible(true);
								grpSubtitles.setLocation(grpSubtitles.getX(),
										grpSetAudio.getSize().height + grpSetAudio.getLocation().y + 6);
								grpWatermark.setVisible(false);
								grpColorimetry.setVisible(false);
								grpImageAdjustement.setVisible(false);
								grpCorrections.setVisible(false);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);
								grpSetTimecode.setVisible(false);
								grpAdvanced.setVisible(true);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpSubtitles.getSize().height + grpSubtitles.getLocation().y + 6);
								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

								// CalculH264
								if (list.getSize() > 0 && FFPROBE.calcul == false)
									FFPROBE.setLength();
								// Qualité Max
								caseQMax.setEnabled(true);

								// Ajout des fonctions avancées
								grpAdvanced.removeAll();

								// grpAdvanced
								caseForcerProgressif.setLocation(7, 14);
								grpAdvanced.add(caseForcerProgressif);

								if ("Blu-ray".equals(function)) {
									caseForcerEntrelacement.setLocation(caseForcerProgressif.getX(),
											caseForcerProgressif.getY() + 17);
									grpAdvanced.add(caseForcerEntrelacement);
									caseGOP.setLocation(7, caseForcerEntrelacement.getLocation().y + 17);
									grpAdvanced.add(caseGOP);
									gopSize.setLocation(caseGOP.getX() + caseGOP.getWidth(), caseGOP.getY() + 3);
									grpAdvanced.add(gopSize);
								}

							} else if (language.getProperty("functionPicture").equals(function)
									|| "JPEG".equals(function) || "JPEG XL".equals(function)) {

								addToList.setText(language.getProperty("filesVideoOrPicture"));
								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
								grpImageSequence.setVisible(false);

								// Ajout partie résolution
								grpResolution.removeAll();

								grpResolution.setVisible(true);
								grpResolution.setLocation(grpResolution.getX(), 30);
								grpResolution.add(lblImageSize);
								grpResolution.add(comboResolution);
								grpResolution.add(caseRotate);
								caseRotate.setLocation(7, 47);
								grpResolution.add(comboRotate);
								comboRotate.setLocation(caseRotate.getWidth() + caseRotate.getLocation().x + 4,
										caseRotate.getLocation().y + 3);
								grpResolution.add(caseMiror);
								caseMiror.setLocation(caseMiror.getX(), caseRotate.getY());
								grpResolution.add(iconTVInterpret);
								grpResolution.add(lblScreenshot);

								grpResolution.add(comboResolution);

								if (comboResolution.getItemCount() != 33) {
									comboResolution.setModel(new DefaultComboBoxModel<String>(new String[] {
											language.getProperty("source"), "AI photo 4x", "AI photo 2x", "AI video 4x", "AI video 2x",
											"AI animation 4x", "AI animation 2x", "1:2", "1:4", "1:8", "1:16",
											"3840:auto", "1920:auto", "auto:2160", "auto:1080", "auto:720", "4096x2160",
											"3840x2160", "2560x1440", "1920x1080", "1440x1080", "1280x720", "1024x768",
											"1024x576", "1000x1000", "854x480", "720x576", "640x360", "500x500",
											"320x180", "200x200", "100x100", "50x50" }));
								}

								// Set comboOptions
								changeComboOptions();

								if (comboResolution.getSelectedItem().toString().contains("AI")) {
									if (VideoPlayerCore.preview != null)
										VideoPlayerCore.preview = null;

									VideoPlayerCore.loadImage(true);
								}

								grpResolution.repaint();

								// lblInterpretation location
								lblInterpretation.setLocation(30,
										caseCreateSequence.getLocation().y + caseCreateSequence.getHeight());
								grpResolution.add(lblInterpretation);
								comboInterpret.setLocation(lblInterpretation.getX() + lblInterpretation.getWidth() + 4,
										lblInterpretation.getLocation().y);
								grpResolution.add(comboInterpret);
								lblIsInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5,
										lblInterpretation.getLocation().y - 1);
								grpResolution.add(lblIsInterpret);
								if (getLanguage.equals(Locale.of("ru").getDisplayLanguage())
										|| getLanguage.equals(Locale.of("uk").getDisplayLanguage())
										|| getLanguage.equals(Locale.of("vi").getDisplayLanguage())
										|| getLanguage.equals(Locale.of("id").getDisplayLanguage())
										|| getLanguage.equals(Locale.of("ro").getDisplayLanguage())) {
									iconTVInterpret.setLocation(comboInterpret.getX() + comboInterpret.getWidth() + 5,
											lblIsInterpret.getY() + 1);
								} else
									iconTVInterpret.setLocation(lblIsInterpret.getX() + lblIsInterpret.getWidth() + 1,
											lblIsInterpret.getY() + 1);
								grpResolution.add(iconTVInterpret);

								caseCreateSequence.setBounds(7, caseRotate.getLocation().y + caseRotate.getHeight(),
										caseCreateSequence.getPreferredSize().width, 23);
								grpResolution.add(caseCreateSequence);

								grpBitrate.setVisible(false);

								if (comboColorspace.getItemCount() != 3) {
									comboColorspace.setModel(new DefaultComboBoxModel<Object>(
											new String[] { "Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG" }));

									comboHDRvalue.setVisible(false);
									lblHDR.setVisible(false);
								}

								grpSetAudio.setVisible(false);
								grpAudio.setVisible(false);
								grpCrop.setVisible(true);
								grpCrop.setLocation(grpCrop.getX(),
										grpResolution.getSize().height + grpResolution.getLocation().y + 6);
								grpOverlay.setVisible(true);
								grpOverlay.setLocation(grpColorimetry.getX(),
										grpCrop.getSize().height + grpCrop.getLocation().y + 6);
								grpSubtitles.setVisible(false);
								grpWatermark.setVisible(true);
								grpWatermark.setLocation(grpColorimetry.getX(),
										grpOverlay.getSize().height + grpOverlay.getLocation().y + 6);
								grpColorimetry.setVisible(true);
								grpColorimetry.setLocation(grpColorimetry.getX(),
										grpWatermark.getSize().height + grpWatermark.getLocation().y + 6);
								grpImageAdjustement.setVisible(true);
								grpImageAdjustement.setLocation(grpImageAdjustement.getX(),
										grpColorimetry.getSize().height + grpColorimetry.getLocation().y + 6);
								grpCorrections.setVisible(true);
								grpCorrections.setLocation(grpCorrections.getX(),
										grpImageAdjustement.getSize().height + grpImageAdjustement.getLocation().y + 6);
								grpTransitions.setVisible(false);
								grpImageSequence.setVisible(false);
								grpImageFilter.setVisible(false);

								grpImageFilter.setVisible(true);
								grpImageFilter.setLocation(grpImageFilter.getX(),
										grpCorrections.getSize().height + grpCorrections.getLocation().y + 6);
								grpSetTimecode.setVisible(false);

								grpAdvanced.removeAll();
								grpAdvanced.setVisible(true);
								caseCreateTree.setLocation(7, 14);
								grpAdvanced.add(caseCreateTree);
								comboCreateTree.setLocation(caseCreateTree.getX() + caseCreateTree.getWidth() + 4,
										caseCreateTree.getY() + 4);
								grpAdvanced.add(comboCreateTree);
								grpAdvanced.setLocation(grpAdvanced.getX(),
										grpImageFilter.getSize().height + grpImageFilter.getLocation().y + 6);

								btnReset.setLocation(btnReset.getX(),
										grpAdvanced.getSize().height + grpAdvanced.getLocation().y + 6);

							} else {
								
								if (language.getProperty("functionExtract").equals(function))
								{
									addToList.setText(language.getProperty("filesVideo"));
								}
								else if (language.getProperty("functionMerge").equals(function)
								|| language.getProperty("functionSeparation").equals(function)
								|| language.getProperty("functionTranscribe").equals(function))
								{
									addToList.setText(language.getProperty("filesVideoOrAudio"));
								}
								else if (language.getProperty("functionSubtitles").equals(function) || language.getProperty("functionBlurFaces").equals(function))
								{
									addToList.setText(language.getProperty("fileVideo"));
								}
								else if (language.getProperty("functionSceneDetection").equals(function))
								{
									addToList.setText(language.getProperty("fileVideo"));
								}
								else if (language.getProperty("functionTranslate").equals(function)
								|| language.getProperty("functionColorize").equals(function)
								|| language.getProperty("functionBackgroundRemover").equals(function))
								{
									addToList.setText(language.getProperty("dropFilesHere"));
								}
								else if (language.getProperty("functionTranslate").equals(function))
								{
									addToList.setText("<html>.txt<br>.srt<br>.vtt</html>");
								}
								else if (comboFonctions.getEditor().getItem().toString().isEmpty())
								{
									addToList.setText(language.getProperty("dropFilesHere"));
								}								
								else
									addToList.setText(language.getProperty(""));

								caseDisplay.setEnabled(false);
								caseDisplay.setSelected(false);
							}

							grpAdvanced.repaint();
							
							//Add language to grpSetAudio
							advancedAudioSettings();

							if (anim && frame.getWidth() > 332)
							{

								int i2 = frame.getWidth();

								do {
									
									changeGroupes = true;
									
									long startTime = System.nanoTime();

									if (Settings.btnDisableAnimations.isSelected())
										i2 = frame.getWidth() - 312 - 12;
									else
									{
										i2 -= 20;
									
										if (i2 < frame.getWidth() - 312 - 12)
											i2 = frame.getWidth() - 312 - 12;
									}
									grpResolution.setLocation(i2, grpResolution.getLocation().y);
									grpBitrate.setLocation(i2, grpBitrate.getLocation().y);
									grpSetAudio.setLocation(i2, grpSetAudio.getLocation().y);
									grpAudio.setLocation(i2, grpAudio.getLocation().y);
									grpCrop.setLocation(i2, grpCrop.getLocation().y);
									grpOverlay.setLocation(i2, grpOverlay.getLocation().y);
									grpSubtitles.setLocation(i2, grpSubtitles.getLocation().y);
									grpWatermark.setLocation(i2, grpWatermark.getLocation().y);
									grpColorimetry.setLocation(i2, grpColorimetry.getLocation().y);
									grpImageAdjustement.setLocation(i2, grpImageAdjustement.getLocation().y);
									grpCorrections.setLocation(i2, grpCorrections.getLocation().y);
									grpTransitions.setLocation(i2, grpTransitions.getLocation().y);
									grpImageSequence.setLocation(i2, grpImageSequence.getLocation().y);
									grpImageFilter.setLocation(i2, grpImageFilter.getLocation().y);
									grpSetTimecode.setLocation(i2, grpSetTimecode.getLocation().y);
									grpAdvanced.setLocation(i2, grpAdvanced.getLocation().y);
									btnReset.setLocation((i2 + 2), btnReset.getLocation().y);

									// Animate size
									animateSections(startTime);

								} while (i2 > frame.getWidth() - 312 - 12);

								changeGroupes = false;
								
								changeSections(false); // une fois l'action terminé on vérifie que les groupes correspondent
							}

							// Right_to_left
							if (getLanguage.contains(Locale.of("ar").getDisplayLanguage())) {
								// destinationStream
								for (Component c : destinationStream.getComponents()) {
									if (c instanceof JCheckBox) {
										c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
									}
								}

								// grpAdvanced
								for (Component c : grpAdvanced.getComponents()) {
									if (c instanceof JCheckBox) {
										c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
									}
								}
							}

							// Important
							grpResolution.repaint();
							topPanel.repaint();
							statusBar.repaint();

						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			changeSize.start();
		}
	}

	public static void extendSections(Component grpPanel, int maxSize) {
           
		if (extendSectionsIsRunning) //Avoid double-clicking
			return;
	    
	    extendSectionsIsRunning = true;
	    
	    // Cache component references once
	    List<Component> componentsToMove = new ArrayList<>();
	    Component firstComponent = null;
	    Component lastComponent = grpPanel;
	    int maxY = 0;

	    for (Component a : frame.getContentPane().getComponents())
	    {
	        if (a instanceof JPanel && a.isVisible() && a.getX() == grpPanel.getX()) {
	            if (firstComponent == null) {
	                firstComponent = a;
	            }
	            
	            if (a.getY() > grpPanel.getY() + grpPanel.getHeight()) {
	                componentsToMove.add(a);
	            }
	            
	            if (a.getY() > maxY) {
	                maxY = a.getY();
	                lastComponent = a;
	            }
	        }
	    }

	    final Component finalFirstComponent = firstComponent;
	    final Component finalLastComponent = lastComponent;
	    
	    // Determine animation parameters
	    boolean expanding = grpPanel.getHeight() < maxSize;
	    int startSize = grpPanel.getHeight() > 17 ? grpPanel.getHeight() : 17;
	    int minSize = 17;
	    
	    // Handle special case for grpAudio
	    if (grpPanel.getHeight() > maxSize)
	    {
	        minSize = maxSize;
	        startSize = grpPanel.getHeight();
	    }
	    
	    final int targetSize = expanding ? maxSize : minSize;
	    final int[] currentSize = {startSize};
	    
	    if (Settings.btnDisableAnimations.isSelected())
	    {	    	
	        // Instant update - no animation
	        int delta = targetSize - startSize;
	        
	        for (Component c : componentsToMove) {
	            if (expanding) {
	                c.setLocation(c.getX(), c.getY() + delta);
	            } else {
	                c.setLocation(c.getX(), c.getY() - Math.abs(delta));
	            }
	        }
	        
	        grpPanel.setSize(312, targetSize);
	        btnReset.setLocation(btnReset.getX(), finalLastComponent.getY() + finalLastComponent.getHeight() + 6);
	        
	        boolean needsScroll = frame.getSize().getHeight() - (btnReset.getY() + btnReset.getHeight()) < 31;
	        if (!needsScroll && finalFirstComponent != null && finalFirstComponent.getY() == 30) {
	            settingsScrollBar.setVisible(false);
	        } else {
	            settingsScrollBar.setVisible(needsScroll);
	        }
	        
	        extendSectionsIsRunning = false;
	    } else {
	    	
	        // Smooth animation with Timer
	        int step = expanding ? 10 : -10;
	        
	        Timer timer = new Timer(5, null);
	        timer.addActionListener(e -> {
	            int previousSize = currentSize[0];
	            currentSize[0] += step;
	            
	            // Check if animation is complete
	            boolean complete = false;
	            if (expanding && currentSize[0] >= targetSize) {
	                currentSize[0] = targetSize;
	                complete = true;
	            } else if (!expanding && currentSize[0] <= targetSize) {
	                currentSize[0] = targetSize;
	                complete = true;
	            }
	            
	            int delta = Math.abs(currentSize[0] - previousSize);
	            
	            // Move components below the expanding/collapsing panel
	            for (Component c : componentsToMove) {
	                if (expanding) {
	                    c.setLocation(c.getX(), c.getY() + delta);
	                } else {
	                    c.setLocation(c.getX(), c.getY() - delta);
	                }
	            }
	            
	            // Resize the panel
	            grpPanel.setSize(312, currentSize[0]);
	            
	            // Handle scrollbar visibility and scroll position
	            if (frame.getSize().getHeight() - (btnReset.getY() + btnReset.getHeight()) < 31) {
	                settingsScrollBar.setVisible(true);
	                
	                if (expanding && (grpPanel.getName() == null || !grpPanel.getName().equals("grpImageAdjustement"))) {
	                    // Scroll down when expanding
	                    for (Component c2 : frame.getContentPane().getComponents()) {
	                        if (c2 instanceof JPanel && c2.isVisible() && c2.getX() == grpPanel.getX()) {
	                            c2.setLocation(c2.getX(), c2.getY() - delta);
	                        }
	                    }
	                }
	            } else {
	                settingsScrollBar.setVisible(false);
	            }
	            
	            // Handle scroll up when collapsing
	            if (!expanding && finalFirstComponent != null && finalFirstComponent.getY() < grpChooseFiles.getY() && finalFirstComponent.isVisible()) {
	                for (Component c2 : frame.getContentPane().getComponents()) {
	                    if (c2 instanceof JPanel && c2.isVisible() && c2.getX() == grpPanel.getX()) {
	                        c2.setLocation(c2.getX(), c2.getY() + delta);
	                    }
	                }
	            }
	            
	            // Update reset button position
	            btnReset.setLocation(btnReset.getX(), finalLastComponent.getY() + finalLastComponent.getHeight() + 6);
	            
	            // Check scrollbar visibility after collapse
	            if (!expanding && frame.getSize().getHeight() - (btnReset.getY() + btnReset.getHeight()) >= 31) {
	                if (finalFirstComponent != null && finalFirstComponent.getY() == 30) {
	                    settingsScrollBar.setVisible(false);
	                }
	            }
	            
	            // Stop timer when complete
	            if (complete) {
	                ((Timer)e.getSource()).stop();
	                extendSectionsIsRunning = false;
	            }
	        });
	        
	        timer.start();
	    }

	}

	public static void animateSections(long startTime) {

		int time = 2000000;

		while (System.nanoTime() - startTime < time) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public static void changeFilters() {

		if (comboFonctions.getEditor().getItem().toString().length() == 0) {
			lblFilter.setVisible(false);
			comboFilter.setVisible(false);
			lblFilter.setLocation(164, 21);
			lblFilter.setIcon(null);

			final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv",
					".mp4", ".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd",
					".webm", ".webp", ".avif" };
			final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
			comboFilter.setModel(model);
			comboFilter.setSelectedIndex(0);
		} else {
			if (comboFonctions.getSelectedItem().toString().contains("H.26")) {
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String[] extensions = new String[] { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts",
						".m2ts" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionConform"))) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				final String types[] = { "23,98 " + Shutter.language.getProperty("fps"),
						"24 " + Shutter.language.getProperty("fps"), "25 " + Shutter.language.getProperty("fps"),
						"29,97 " + Shutter.language.getProperty("fps"), "30 " + Shutter.language.getProperty("fps"),
						"48 " + Shutter.language.getProperty("fps"), "50 " + Shutter.language.getProperty("fps"),
						"59,94 " + Shutter.language.getProperty("fps"), "60 " + Shutter.language.getProperty("fps") };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				comboFilter.setModel(model);
				comboFilter.setSelectedIndex(2);

			} else if (comboFonctions.getSelectedItem().toString()
					.equals(language.getProperty("functionReplaceAudio"))) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				final String types[] = { language.getProperty("shortest"), language.getProperty("longest") };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionRewrap"))) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".mp4",
						".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".webm" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionExtract"))) {

				String value = comboFilter.getSelectedItem().toString();
								
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);					
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				comboFilter.removeAllItems();
				comboFilter.addItem(language.getProperty("setAll"));			
				comboFilter.addItem(language.getProperty("video"));	
				if (list.getSize() == 0)
				{
					comboFilter.addItem(language.getProperty("audio"));		
					comboFilter.addItem(language.getProperty("subtitles"));
				}
				else
				{					
					if (FFPROBE.audioStreams > 0)
					{
						//Add each audio track
						comboFilter.addItem(language.getProperty("audio"));					
						if (FFPROBE.audioStreams > 1)
						{
							for (int i = 1 ; i < FFPROBE.audioStreams + 1 ; i++)
							{
								comboFilter.addItem(language.getProperty("audio") + " #" + i);
							}	
						}
					}
						
					if (FFPROBE.subtitleStreams > 0)
					{
						//Add each subtitles track
						comboFilter.addItem(language.getProperty("subtitles"));
						if (FFPROBE.subtitleStreams > 1)
						{
							for (int i = 1 ; i < FFPROBE.subtitleStreams + 1 ; i++)
							{
								comboFilter.addItem(language.getProperty("subtitles") + " #" + i);
							}	
						}
					}
				}
				
				comboFilter.setSelectedIndex(0);
				for (int i = 0 ; i < comboFilter.getModel().getSize() ; i++)
				{
					if (comboFilter.getModel().getElementAt(i).equals(value))
					{
						comboFilter.setSelectedIndex(i);
						break;
					}
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionSeparation"))) {
					
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				final String types[] = { language.getProperty("setAll"), "drums", "bass", "other", "vocals", "guitar", "piano" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionTranscribe")))	{
				
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				final String types[] = { ".srt", ".vtt", ".txt" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionTranslate"))) {
				
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				//Add comboBox languages
				String[] allLanguages = new String[Utils.ISO_639_2_LANGUAGES.length];				
				int x = 0;
				for (String[] language : Utils.ISO_639_2_LANGUAGES)
				{
				    allLanguages[x] = language[2];
				    x++;
				}
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(allLanguages);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					
					if (Utils.getLanguage.contains("("))
					{
						String s[] = Utils.getLanguage.split(" ");
						comboFilter.setSelectedItem(s[0]);
					}
					else
						comboFilter.setSelectedItem(Utils.getLanguage);
				}
			
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionColorize"))) {
				
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "artistic", "stable" };
				if (FFPROBE.totalLength > 40)
					types = new String[]{ "video" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false)
				{
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("DV")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "16/9", "4/3" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("AV1")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String[] extensions = new String[] { ".mp4", ".mkv", ".webm" };

				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("Blu-ray")) {

				/*
				 * lblFilter.setText(" "); lblFilter.setVisible(true);
				 * comboFilter.setVisible(true); lblFilter.setLocation(165, 23);
				 * lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));
				 */

				lblFilter.setVisible(false);
				comboFilter.setVisible(false);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);

				String[] extensions = new String[] { "H.264", "H.265" };

				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP8")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String[] extensions = new String[] { ".webm", ".mkv" };

				String types[] = extensions;
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("VP9")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String[] extensions = new String[] { ".webm", ".mkv", ".mp4" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(extensions);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals("WAV")
					|| comboFonctions.getSelectedItem().toString().equals("AIFF")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "16 Bits", "24 Bits", "32 Bits", "32 Float" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false
						|| comboFilter.getModel().getSize() != 4) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("FLAC")) {

				lblFilter.setText("Comp.:");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);

				String types[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(5);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("ALAC")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "16 Bits", "24 Bits" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false
						|| comboFilter.getModel().getSize() != 2) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("MP3")
					|| comboFonctions.getSelectedItem().toString().equals("AAC")
					|| comboFonctions.getSelectedItem().toString().equals("AC3")
					|| comboFonctions.getSelectedItem().toString().equals("Opus")
					|| comboFonctions.getSelectedItem().toString().equals("Vorbis")
					|| comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(audioValues);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);

					if (comboFonctions.getSelectedItem().toString().equals("MP3")
							|| comboFonctions.getSelectedItem().toString().equals("AAC")
							|| comboFonctions.getSelectedItem().toString().equals("Vorbis")) {
						comboFilter.setSelectedIndex(9);
					} else if (comboFonctions.getSelectedItem().toString().equals("AC3")
							|| comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus")) {
						comboFilter.setSelectedIndex(7);
					} else if (comboFonctions.getSelectedItem().toString().equals("Opus")) {
						comboFilter.setSelectedIndex(11);
					}
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("HAP")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model;
				String types[] = { "Standard", "Alpha", "Q" };
				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
					|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model;
				String types[] = { ".mxf", ".mov" };

				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHD")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model;
				if (comboResolution.getSelectedItem().toString().equals("1280x720")) {
					String types[] = { "60", "90", "90 X", "75", "110", "145", "220", "220 X" };
					model = new DefaultComboBoxModel<Object>(types);
					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(0);
					}
				} else {
					String types[] = { "36", "115", "175", "175 X", "36", "120", "185", "185 X", "45", "145", "220",
							"220 X", "75", "240", "365", "365 X", "90", "290", "440", "440 X" };
					model = new DefaultComboBoxModel<Object>(types);

					if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
						comboFilter.setModel(model);
						comboFilter.setSelectedIndex(5);
					}
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("DNxHR")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				DefaultComboBoxModel<Object> model;
				String types[] = { "LB", "SQ", "HQ", "HQX", "444" };
				model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("Apple ProRes")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = new String[] { "Proxy", "LT", "422", "422 HQ", "444", "4444", "4444 XQ" };
				if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
						&& comboAccel.getSelectedItem().equals("OSX VideoToolbox")) {
					types = new String[] { "Proxy", "LT", "422", "422 HQ", "4444", "4444 XQ" };
				}

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(4).equals(comboFilter.getModel().getElementAt(4)) == false
						|| model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "Low", "Medium", "High", "Film Scan", "Film Scan 2", "Film Scan 3", "Film Scan 3+" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(3);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("Uncompressed")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "YUV", "RGB" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("XAVC")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "25", "35", "50", "100", "150", "240", "300", "480", "960" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(7);
				}
				
			} else if (comboFonctions.getSelectedItem().toString().equals("XAVC Long GOP")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "25", "35", "50", "100", "150" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals("MPEG-2")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { ".mpg", ".ts" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}

			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionNormalization"))) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = new String[31];

				types[0] = "0 LUFS";
				int i = 1;
				do {
					types[i] = ("-" + i + " LUFS");
					i++;
				} while (i < 31);

				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(23);
				}
			} else if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture"))) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { ".png", ".tif", ".tga", ".dpx", ".j2k", ".exr", ".webp", ".avif", ".bmp", ".ico",
						".gif", ".apng" };

				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);

				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false
						|| comboFilter.getItemCount() != model.getSize()) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			} else if (comboFonctions.getSelectedItem().toString().contains("JPEG")) {

				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { "100%", "95%", "90%", "85%", "80%", "75%", "70%", "65%", "60%", "55%", "50%", "45%",
						"40%", "35%", "30%", "25%", "20%", "15%", "10%", "5%", "0%" };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(2);
				}
			}
			else if (comboFonctions.getSelectedItem().toString().equals("Loudness & True Peak")
			|| comboFonctions.getSelectedItem().toString().equals("VMAF")
			|| comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionBlackDetection"))
			|| comboFonctions.getSelectedItem().toString().equals(Shutter.language.getProperty("functionOfflineDetection")))
			{
				lblFilter.setText(" ");
				lblFilter.setVisible(true);
				comboFilter.setVisible(true);
				lblFilter.setLocation(165, 23);
				lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));

				String types[] = { language.getProperty("menuItemVisualiser"), language.getProperty("btnSave") };
				DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			}
			else
			{
				lblFilter.setVisible(false);
				comboFilter.setVisible(false);
				lblFilter.setLocation(164, 21);
				lblFilter.setIcon(null);

				if (comboFonctions.getEditor().getItem().toString().contains("ffmpeg")) {
					lblFilter.setText(" ");
					lblFilter.setVisible(true);
					comboFilter.setVisible(true);
					lblFilter.setLocation(165, 23);
					lblFilter.setIcon(new FlatSVGIcon("resources/arrow.svg", 30, 30));
				}

				final String types[] = { language.getProperty("aucun"), ".mp3", ".wav", ".aif", ".m4a", ".avi", ".flv",
						".mp4", ".mov", ".mkv", ".mts", ".mxf", ".mpg", ".jpg", ".png", ".tif", ".cr2", ".nef", ".psd",
						".webm", ".webp", ".avif" };
				final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
				if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
					comboFilter.setModel(model);
					comboFilter.setSelectedIndex(0);
				}
			}
		}
	}
	
	public static void changeComboOptions() {
	
		if (comboFonctions.getSelectedItem().toString().contains("JPEG")
		|| comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionPicture")))
		{
			if (comboFilter.getSelectedItem().toString().equals(".png")) {
				if (comboImageOption.getItemAt(0).equals("0%") == false) {
					comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "0%", "5%",
							"10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "55%", "60%",
							"65", "70%", "75%", "80%", "85%", "90%", "95%", "100%" }));
				}
				comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(),
						lblImageQuality.getLocation().y);
				comboImageOption.setSize(50, 16);
				comboImageOption.setEditable(false);
				lblImageQuality.setText("Comp.:");
				grpResolution.add(lblImageQuality);
				grpResolution.add(comboImageOption);
				lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
				comboImageOption.repaint();
			} else if (comboFilter.getSelectedItem().toString().equals(".webp")
					|| comboFilter.getSelectedItem().toString().equals(".avif")) {
				if (comboImageOption.getItemAt(0).equals("100%") == false) {
					comboImageOption.setModel(new DefaultComboBoxModel<String>(new String[] { "100%", "95%",
							"90%", "85%", "80%", "75%", "70%", "65%", "60%", "55%", "50%", "45%", "40%",
							"35%", "30%", "25%", "20%", "15%", "10%", "5%", "0%" }));
				}
				comboImageOption.setLocation(lblImageQuality.getX() + lblImageQuality.getWidth(),
						lblImageQuality.getLocation().y);
				comboImageOption.setSize(50, 16);
				comboImageOption.setEditable(false);
				lblImageQuality.setText(language.getProperty("lblQualit"));
				grpResolution.add(lblImageQuality);
				grpResolution.add(comboImageOption);
				lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
				comboImageOption.repaint();
			} else if (comboFilter.getSelectedItem().toString().equals(".tif")) {
				if (comboImageOption.getItemAt(0).equals("packbits") == false) {
					comboImageOption.setModel(new DefaultComboBoxModel<String>(
							new String[] { "packbits", "raw", "lzw", "deflate" }));
				}
				comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6,
						lblImageQuality.getLocation().y);
				comboImageOption.setSize(90, 16);
				comboImageOption.setEditable(false);
				lblImageQuality.setText(language.getProperty("lblQualit"));
				grpResolution.remove(lblImageQuality);
				grpResolution.add(comboImageOption);
				lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
				comboImageOption.repaint();
			} else if (comboFilter.getSelectedItem().toString().equals(".gif")
					|| comboFilter.getSelectedItem().toString().equals(".apng")) {
				if (comboImageOption.getItemAt(0)
						.equals("15 " + Shutter.language.getProperty("fps")) == false) {
					String fps[] = new String[17];
					int a = 0;
					for (int f = 15; f < 24; f++) {
						fps[a] = f + " " + Shutter.language.getProperty("fps");
						a++;
					}

					fps[a] = "23,98 " + Shutter.language.getProperty("fps");
					fps[a + 1] = "24 " + Shutter.language.getProperty("fps");
					fps[a + 2] = "25 " + Shutter.language.getProperty("fps");
					fps[a + 3] = "29,97 " + Shutter.language.getProperty("fps");
					fps[a + 4] = "30 " + Shutter.language.getProperty("fps");
					fps[a + 5] = "50 " + Shutter.language.getProperty("fps");
					fps[a + 6] = "59,94 " + Shutter.language.getProperty("fps");
					fps[a + 7] = "60 " + Shutter.language.getProperty("fps");

					comboImageOption.setModel(new DefaultComboBoxModel<String>(fps));
				}
				comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6,
						lblImageQuality.getLocation().y);
				comboImageOption.setSize(90, 16);
				comboImageOption.setEditable(false);
				grpResolution.remove(lblImageQuality);
				grpResolution.add(comboImageOption);
				lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
				comboImageOption.repaint();
			} else {
				grpResolution.remove(lblImageQuality);
				grpResolution.remove(comboImageOption);
				lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
			}	
		}
		else if (comboResolution.getSelectedItem().toString().contains("AI"))
		{
			if (comboImageOption.getItemAt(0).equals(".png") == false) {
				comboImageOption.setModel(new DefaultComboBoxModel<String>(
						new String[] { ".png", ".webp", ".jpg" }));
			}
			comboImageOption.setLocation(comboResolution.getX() + comboResolution.getWidth() + 6, lblImageQuality.getLocation().y);
			comboImageOption.setSize(90, 16);
			comboImageOption.setEditable(false);
			grpResolution.remove(lblImageQuality);
			grpResolution.add(comboImageOption);
			lblScreenshot.setLocation(comboImageOption.getX() + comboImageOption.getWidth() + 9, 21);
			comboImageOption.repaint();
		}
		else
		{
			grpResolution.remove(lblImageQuality);
			grpResolution.remove(comboImageOption);
			
			if (lblPad.isVisible())
			{
				lblScreenshot.setLocation(lblPad.getX() + lblPad.getWidth() + 9, 21);	
			}
			else
				lblScreenshot.setLocation(comboResolution.getX() + comboResolution.getWidth() + 9, 21);
		}
		
		grpResolution.repaint();
	}

	public static void setDestinationTabs(int tabs) {

		try {
			// Affichage des titres
			Font tabFont = new Font(boldFont, Font.PLAIN, 12);

			JLabel output = new JLabel(language.getProperty("output"));
			output.setFont(tabFont);
			JLabel output1 = new JLabel(language.getProperty("output") + "1");
			output1.setFont(tabFont);
			JLabel output2 = new JLabel(language.getProperty("output") + "2");
			output2.setFont(tabFont);
			JLabel output3 = new JLabel(language.getProperty("output") + "3");
			output3.setFont(tabFont);
			JLabel ftpTab = new JLabel("FTP");
			ftpTab.setFont(tabFont);
			JLabel mailTab = new JLabel(mailIcon);
			mailTab.setFont(tabFont);
			JLabel streamTab = new JLabel(streamIcon);
			streamTab.setFont(tabFont);

			if (tabs != grpDestination.getTabCount()) {
				grpDestination.removeAll();

				// Ajout des titres
				if (tabs == 1) {
					grpDestination.addTab(language.getProperty("output"), null);
				} else if (tabs == 2) {
					grpDestination.addTab(language.getProperty("output"), destination1);
					grpDestination.addTab("Mail", destinationMail);

					grpDestination.setTabComponentAt(0, output);
					grpDestination.setTabComponentAt(1, mailTab);
				} else if (tabs == 5) {
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);
					grpDestination.addTab("FTP", new JPanel());
					grpDestination.addTab("Mail", destinationMail);
					;

					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					grpDestination.setTabComponentAt(4, mailTab);
				} else if (tabs == 6) {
					grpDestination.addTab(language.getProperty("output") + "1", destination1);
					grpDestination.addTab(language.getProperty("output") + "2", destination2);
					grpDestination.addTab(language.getProperty("output") + "3", destination3);
					grpDestination.addTab("FTP", new JPanel());
					grpDestination.addTab("Mail", destinationMail);
					grpDestination.addTab("Stream", destinationStream);

					grpDestination.setTabComponentAt(0, output1);
					grpDestination.setTabComponentAt(1, output2);
					grpDestination.setTabComponentAt(2, output3);
					grpDestination.setTabComponentAt(3, ftpTab);
					grpDestination.setTabComponentAt(4, mailTab);
					grpDestination.setTabComponentAt(5, streamTab);
				}
			}
		} catch (Exception e) {
		}
	}

	public static void disableAll() {

		Utils.breakSleepMode = false;
		Utils.disableSleepMode();

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChooseFiles.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(false);
			}
			fileList.setEnabled(false);
		}

		components = grpDestination.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination1.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination2.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destination3.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destinationMail.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = destinationStream.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpBitrate.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSetAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpCrop.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSubtitles.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpWatermark.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpColorimetry.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageAdjustement.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpCorrections.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpTransitions.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageSequence.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpImageFilter.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpSetTimecode.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}
		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(false);
		}

		// Status bar GPU
		comboGPUDecoding.setEnabled(false);
		comboGPUFilter.setEnabled(false);
		comboAccel.setEnabled(false);

		// Disable buttons
		VideoPlayerUI.setPlayerButtons(false);
		VideoPlayerUI.player.remove(selection);
		VideoPlayerUI.player.remove(overImage);
		VideoPlayerUI.player.remove(timecode);
		VideoPlayerUI.player.remove(fileName);
		VideoPlayerUI.player.remove(subsCanvas);
		VideoPlayerUI.player.remove(logo);
		VideoPlayerUI.showScale.setVisible(false);
		VideoPlayerCore.playerStop();

		lblFiles.setEnabled(true);
		lblFilesEnded.setEnabled(true);

		comboFonctions.setEnabled(false);
		comboFilter.setEnabled(false);
		btnReset.setEnabled(false);

		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		if (FunctionUtils.completed > 0) {
			lblFilesEnded.setVisible(true);
		}
		cancelled = false;
		btnCancel.setEnabled(true);

		if (inputDeviceIsRunning)
			progressBar.setIndeterminate(true);

		progressBar.setValue(0);

		if (FFMPEG.isRunning)
		{
			if (comboFonctions.getSelectedItem().equals(language.getProperty("functionPicture")) == false
			&& comboFonctions.getSelectedItem().toString().contains("JPEG") == false)
			{
				progressBar.setValue(0);
			}

			if (caseDisplay.isSelected() == false)
			{
				changeWidth(false);
				caseRunInBackground.setEnabled(true);
			}

			caseDisplay.setEnabled(false);
			btnStart.setEnabled(true);

			if (inputDeviceIsRunning || caseStream.isSelected())
			{
				btnStart.setText(language.getProperty("btnStopRecording"));
			}
			else
				btnStart.setText(language.getProperty("btnPauseFunction"));
		}

		// Important
		topPanel.repaint();
		statusBar.repaint();
		topImage.repaint();
		frame.repaint();
	}

	public static void enableAll() {

		Utils.breakSleepMode = true;

		Component[] components = frame.getContentPane().getComponents();

		if (scanIsRunning) {
			components = grpChooseFiles.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(true);
			}
			fileList.setEnabled(true);
		}

		components = grpDestination.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = destination1.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = destination2.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (lblDestination2.getText().equals(language.getProperty("aucune")))
				caseOpenFolderAtEnd2.setEnabled(false);
		}
		components = destination3.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (lblDestination3.getText().equals(language.getProperty("aucune")))
				caseOpenFolderAtEnd3.setEnabled(false);
			if (caseChangeFolder2.isSelected() == false)
				caseChangeFolder3.setEnabled(false);

		}
		components = destinationMail.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (caseSendMail.isSelected() == false)
				textMail.setEnabled(false);
		}
		components = destinationStream.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
			if (caseStream.isSelected() == false) {
				textStream.setEnabled(false);
			}
		}

		components = grpResolution.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComboBox == false)
				components[i].setEnabled(true);
		}

		// .webp .avif
		comboImageOption.setEnabled(true);

		if (caseCreateSequence.isSelected())
			comboInterpret.setEnabled(true);

		components = grpImageSequence.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseEnableSequence.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}
		if (caseEnableSequence.isSelected() == false)
			caseSequenceFPS.setEnabled(false);

		components = grpImageFilter.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		if (caseYear.isSelected() == false)
			comboYear.setEnabled(false);

		if (caseMonth.isSelected() == false)
			comboMonth.setEnabled(false);

		if (caseYear.isSelected() == false)
			comboYear.setEnabled(false);

		if (caseDay.isSelected() == false)
			comboDay.setEnabled(false);

		if (caseFrom.isSelected() == false) {
			comboFrom.setEnabled(false);
			comboTo.setEnabled(false);
		}

		comboResolution.setEnabled(true);

		if (caseRotate.isSelected())
			comboRotate.setEnabled(true);
		else
			comboRotate.setEnabled(false);

		if (caseConvertAudioFramerate.isSelected()) {
			comboAudioIn.setEnabled(true);
			comboAudioOut.setEnabled(true);
		} else {
			comboAudioIn.setEnabled(false);
			comboAudioOut.setEnabled(false);
		}

		components = grpColorimetry.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		if (caseLUTs.isSelected() == false)
			comboLUTs.setEnabled(false);

		if (caseColorspace.isSelected() == false)
			comboColorspace.setEnabled(false);

		if (caseLevels.isSelected() == false) {
			comboInLevels.setEnabled(false);
			comboOutLevels.setEnabled(false);
		}

		if (caseGamma.isSelected() == false) {
			comboGamma.setEnabled(false);
		}

		if (caseColormatrix.isSelected() == false) {
			comboInColormatrix.setEnabled(false);
			comboOutColormatrix.setEnabled(false);
		}

		components = grpSetAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		if (caseChangeAudioCodec.isSelected() == false) {
			comboAudioCodec.setEnabled(false);
			comboAudioBitrate.setEnabled(false);
			lbl48k.setEnabled(false);
		}
		
		if (caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
		{
			lblAudioMapping.setEnabled(false);
			comboAudioBitrate.setEnabled(false);
			lbl48k.setEnabled(false);
		}

		if (caseAudioOffset.isSelected() == false)
			txtAudioOffset.setEnabled(false);

		components = grpAudio.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}
		components = grpSetTimecode.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseSetTimecode.isSelected() == false && components[i] instanceof JTextField)
				components[i].setEnabled(false);
			else
				components[i].setEnabled(true);
		}

		if (caseNormalizeAudio.isSelected() == false)
		{
			comboNormalizeAudio.setEnabled(false);
		}
		
		if (caseReadAudioTimecode.isSelected() == false) {
			comboReadAudioTimecode.setEnabled(false);
		}
		
		if (caseGenerateFromDate.isSelected() || caseReadAudioTimecode.isSelected()) {
			caseSetTimecode.setSelected(false);
			caseSetTimecode.setEnabled(false);
		}

		if (caseSetTimecode.isSelected() == false)
		{
			caseIncrementTimecode.setEnabled(false);
		}
		else
		{
			caseGenerateFromDate.setEnabled(false);
			caseReadAudioTimecode.setEnabled(false);
			comboReadAudioTimecode.setEnabled(false);
		}

		components = grpAdvanced.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		if (comboForcerDesentrelacement.getModel().getSize() == 2 && comboForcerDesentrelacement.getModel().getElementAt(1).equals("advanced"))
		{
			lblTFF.setEnabled(false);
		}
		else
			lblTFF.setEnabled(true);
		
		if (caseCreateTree.isSelected() == false) {
			comboCreateTree.setEnabled(false);
		}

		components = grpBitrate.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		components = grpCrop.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseEnableCrop.isSelected() == false && components[i] instanceof JCheckBox == false) {
				components[i].setEnabled(false);
			} else
				components[i].setEnabled(true);
		}

		components = grpOverlay.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		if (caseAddTimecode.isSelected() == false && caseShowTimecode.isSelected() == false) {
			components = grpOverlay.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i].getY() > caseShowTimecode.getY() && components[i].getY() < caseAddText.getY())
					components[i].setEnabled(false);
			}
		}

		if (caseAddTimecode.isSelected() == false) {
			TC1.setEnabled(false);
			TC2.setEnabled(false);
			TC3.setEnabled(false);
			TC4.setEnabled(false);
		}

		if (caseAddText.isSelected() == false && caseShowFileName.isSelected() == false) {
			components = grpOverlay.getComponents();
			for (int i = 0; i < components.length; i++) {
				if (components[i].getY() > caseShowFileName.getY())
					components[i].setEnabled(false);
			}
		}

		if (caseAddText.isSelected() == false) {
			overlayText.setEnabled(false);
		}

		components = grpSubtitles.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseAddSubtitles.isSelected() == false && components[i] instanceof JCheckBox == false) {
				components[i].setEnabled(false);
			} else
				components[i].setEnabled(true);
		}
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
			comboSubsSource.setEnabled(true);

		if (caseAddSubtitles.isSelected() && subtitlesBurn == false) {
			for (Component c : grpSubtitles.getComponents()) {
				if (c instanceof JCheckBox == false) {
					c.setEnabled(false);
				}
			}
			if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
				comboSubsSource.setEnabled(true);
		}

		components = grpWatermark.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (caseAddWatermark.isSelected() == false && components[i] instanceof JCheckBox == false) {
				components[i].setEnabled(false);
			} else
				components[i].setEnabled(true);
		}

		if (inputDeviceIsRunning == false) {
			components = grpImageAdjustement.getComponents();
			for (int i = 0; i < components.length; i++) {
				components[i].setEnabled(true);
			}
		}

		components = grpCorrections.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		components = grpTransitions.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setEnabled(true);
		}

		// Status bar GPU
		comboGPUDecoding.setEnabled(true);
		if (comboGPUDecoding.getSelectedItem().equals(language.getProperty("aucun")) == false)
			comboGPUFilter.setEnabled(true);

		if (comboAccel.getItemCount() > 1)
			comboAccel.setEnabled(true);

		if (FFPROBE.audioOnly) {
			caseVideoFadeIn.setEnabled(false);
			caseVideoFadeOut.setEnabled(false);
		}

		if (caseVideoFadeIn.isSelected() == false)
			spinnerVideoFadeIn.setEnabled(false);

		if (caseAudioFadeIn.isSelected() == false)
			spinnerAudioFadeIn.setEnabled(false);

		if (caseVideoFadeOut.isSelected() == false)
			spinnerVideoFadeOut.setEnabled(false);

		if (caseAudioFadeOut.isSelected() == false)
			spinnerAudioFadeOut.setEnabled(false);

		if (caseAS10.isSelected() == false)
			comboAS10.setEnabled(false);

		if (caseGOP.isSelected() == false)
			gopSize.setEnabled(false);
		
		if (caseFastDecode.isSelected() == false)
			comboFastDecode.setEnabled(false);

		if (caseVarianceBoost.isSelected() == false)
			comboVarianceBoost.setEnabled(false);
		
		if (caseAlpha.isSelected() == false)
			comboAlpha.setEnabled(false);
		
		if (caseFilmGrain.isSelected() == false)
			comboFilmGrain.setEnabled(false);

		if (caseFilmGrainDenoise.isSelected() == false)
			comboFilmGrainDenoise.setEnabled(false);

		if (caseChunks.isSelected() == false)
			chunksSize.setEnabled(false);
		
		if (VideoPlayerUI.caseApplyCutToAll.isVisible() && VideoPlayerUI.comboMode.getSelectedItem().equals(Shutter.language.getProperty("removeMode")))
			VideoPlayerUI.caseApplyCutToAll.setEnabled(false);

		if ((comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false || lblVBR.getText().equals("CQ")))
		{
			case2pass.setEnabled(false);
		}

		if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
				&& (comboAccel.getSelectedItem().equals("AMD AMF Encoder")
						|| comboAccel.getSelectedItem().equals("OSX VideoToolbox")
						|| comboAccel.getSelectedItem().equals("Vulkan Video"))) {
			caseForcePreset.setSelected(false);
			caseForcePreset.setEnabled(false);
			comboForcePreset.setEnabled(false);
		}

		if (comboFilter.getSelectedItem().toString().equals(".mp4")
				|| comboFilter.getSelectedItem().toString().equals(".mov"))
			caseFastStart.setEnabled(true);
		else
			caseFastStart.setEnabled(false);

		if (comboFonctions.getSelectedItem().toString().equals("VP9") && (comboColorspace.getSelectedItem().toString().contains("10bits")
		|| comboColorspace.getSelectedItem().toString().contains("12bits")
		|| comboColorspace.getSelectedItem().toString().contains("422"))) {
			caseAlpha.setSelected(false);
			caseAlpha.setEnabled(false);
			comboAlpha.setEnabled(false);
		} else if (comboFonctions.getSelectedItem().toString().equals("VP9")
				|| comboFonctions.getSelectedItem().toString().equals("H.265")
						&& (comboAccel.getSelectedItem().equals("OSX VideoToolbox")
								|| comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))) {
			caseAlpha.setEnabled(true);
		}

		if (caseQMax.isSelected()) {
			caseForcePreset.setEnabled(false);
			comboForcePreset.setEnabled(false);
			caseForceSpeed.setEnabled(false);
			comboForceSpeed.setEnabled(false);
			caseForceQuality.setEnabled(false);
			comboForceQuality.setEnabled(false);
		}

		if (caseTruePeak.isSelected() == false) {
			comboTruePeak.setEnabled(false);
		}

		if (caseLRA.isSelected() == false) {
			comboLRA.setEnabled(false);
		}

		// Dans tous les cas
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setSelected(false);
		tempsRestant.setVisible(false);
		comboFonctions.setEnabled(true);
		comboFilter.setEnabled(true);
		btnReset.setEnabled(true);
		btnStart.setEnabled(true);
		btnCancel.setEnabled(false);
		changeFunction(false);

		// Important
		topPanel.repaint();
		statusBar.repaint();
		topImage.repaint();
		frame.repaint();

		if (inputDeviceIsRunning || RenderQueue.frame != null && RenderQueue.frame.isVisible()
				&& RenderQueue.caseRunParallel.isSelected()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setIndeterminate(false);
				}
			});
		}

		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public static void endOfFunction() {

		if (errorList.length() != 0) {
			if (Settings.btnDisableSound.isSelected() == false) {
				try {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundErrorURL);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				}
			}

			if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported()) {
				Taskbar.getTaskbar().setWindowProgressValue(frame, 100);
				Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.ERROR);
			}

			JTextArea errorText = new JTextArea(errorList.toString());
			errorText.setWrapStyleWord(true);

			JScrollPane scrollPane = new JScrollPane(errorText);
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false);
			scrollPane.setPreferredSize(new Dimension(500, 400));

			if (scanIsRunning == false) {
				Object[] moreInfo = { "OK", language.getProperty("menuItemConsole") };

				int result = JOptionPane.showOptionDialog(Shutter.frame, scrollPane,
						Shutter.language.getProperty("notProcessedFiles"), JOptionPane.YES_NO_OPTION,
						JOptionPane.ERROR_MESSAGE, null, moreInfo, null);

				if (result == JOptionPane.NO_OPTION) {
					if (Console.frmConsole != null) {
						if (Console.frmConsole.isVisible()) {
							Console.frmConsole.toFront();
						} else
							new Console();
					} else
						new Console();
				}
			}

			if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
			{
				String s[] = errorList.toString().split(System.lineSeparator());

				int startRowCount = 0;
				int removedRows = 0;

				for (String line : s)
				{
					if (line.contains(Shutter.language.getProperty("file") + " N°")) {
						String item[] = line.split("-");
						String clearItemNumber = item[0].replace(Shutter.language.getProperty("file") + " N°", "");
						Integer getItemNumber = (int) (Integer.parseInt(clearItemNumber.replace(" ", "")) - removedRows
								- 1);

						for (int r = startRowCount; r < RenderQueue.tableRow.getRowCount(); r++) {
							if (r == getItemNumber) {
								startRowCount = r + 1;
								break;
							} else {
								RenderQueue.tableRow.removeRow(r);
								getItemNumber--;
								r--;
								removedRows++;
							}
						}
					}
				}

				// Une fois terminé on supprime les fichiers au delà du dernier fichier qui
				// contient l'erreur
				for (int r = startRowCount; r < RenderQueue.tableRow.getRowCount(); r++)
				{
					RenderQueue.tableRow.removeRow(r);
					r--;
				}
			}

			FFMPEG.errorLog.setLength(0);
			errorList.setLength(0);
		} else if (RenderQueue.frame != null && RenderQueue.frame.isVisible() && Shutter.cancelled == false) {
			RenderQueue.tableRow.setRowCount(0);
		}

		if (VideoPlayerUI.fullscreenPlayer) {
			VideoPlayerUI.fullscreenPlayer = false;

			topPanel.setVisible(true);
			grpChooseFiles.setVisible(true);
			grpChooseFunction.setVisible(true);
			grpDestination.setVisible(true);
			grpProgression.setVisible(true);
			statusBar.setVisible(true);

			frame.getContentPane().setBackground(Utils.background);

			changeSections(false);

			VideoPlayerUI.setPlayerButtons(true);

			VideoPlayerUI.mouseIsPressed = false;

			VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame

			VideoPlayerUI.resizeAll();

			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, Shutter.frame.getWidth(), Shutter.frame.getHeight(), 15, 15));
			Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
			shape1.add(shape2);
			frame.setShape(shape1);
		}

		// Unlock the file to be deletable
		if (scanIsRunning == false && screenshotIsRunning == false)
		{
			VideoPlayerCore.videoPath = null;
			fileList.clearSelection();
			VideoPlayerCore.frameVideo = null;			
			VideoPlayerUI.player.repaint();

			// Lecteur
			if (VideoPlayerCore.waveform != null) {
				VideoPlayerCore.waveform = null;
				VideoPlayerCore.waveformIcon.setIcon(null);
				VideoPlayerCore.waveformIcon.repaint();
			}
		} else if (screenshotIsRunning) {
			VideoPlayerCore.addWaveform(false);
		}

		if (scanIsRunning == false) {
			enableAll();
		}

		if (cancelled == true) {
			lblCurrentEncoding.setForeground(Utils.red);
			lblCurrentEncoding.setText(language.getProperty("processCancelled"));
			progressBar.setValue(0);
		} else {
			lblCurrentEncoding.setText(language.getProperty("processEnded"));
			if (progressBar.getMaximum() == 0)
				progressBar.setMaximum(1);
			progressBar.setValue(progressBar.getMaximum());

			if (Settings.btnDisableSound.isSelected() == false) {
				try {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				}
			}
		}

		if (grpDestination.isEnabled())
			grpDestination.setSelectedIndex(0);

		// IMPORTANT
		screenshotIsRunning = false;

		FunctionUtils.sendMail();
		lastActions();
	}

	public static void lastActions() {

		if (Settings.btnEmptyListAtEnd.isSelected() && cancelled == false && FFMPEG.error == false && comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionReplaceAudio")) == false)
			list.clear();

		Thread thread = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {

				do {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
				} while (Ftp.isRunning || sendMailIsRunning);

				if (Settings.btnEndingAction.isSelected() && cancelled == false) {
					final JOptionPane msg = new JOptionPane(language.getProperty("shutdown"),
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					final JDialog dlg = msg.createDialog(frame, Settings.comboAction.getSelectedItem().toString());

					msg.setInitialSelectionValue(JOptionPane.CANCEL_OPTION);
					dlg.setAlwaysOnTop(true);
					dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dlg.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentShown(ComponentEvent e) {
							super.componentShown(e);
							final Timer t = new Timer(60000, new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									dlg.setVisible(false);
								}
							});
							t.start();
						}
					});
					dlg.setVisible(true);

					Object selectedvalue = msg.getValue();
					if (selectedvalue.equals(JOptionPane.CANCEL_OPTION) == false) {
						switch (Settings.comboAction.getSelectedIndex()) {

						case 0:
							System.exit(0);
							break;
						case 1:
							if (System.getProperty("os.name").contains("Mac")) {
								try {
									Runtime.getRuntime().exec(new String[] { "osascript", "-e",
											"tell application \"System Events\" to shut down" });
								} catch (IOException e) {
								}
							} else if (System.getProperty("os.name").contains("Linux")) {
								try {
									Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", "shutdown -h now" });
								} catch (IOException e) {
								}
							} else // Windows
							{
								try {
									Runtime.getRuntime().exec("shutdown.exe -s -t 0");
								} catch (IOException e) {
								}
							}
							break;
						}
					}
				}

				saveCode = false;
				cancelled = false;
			}
		});
		thread.start();
	}
}
