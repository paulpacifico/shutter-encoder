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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFprobe;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Reset extends Main {

	public static void btnReset() {

		btnReset = new JButton(language.getProperty("btnReset"));
		btnReset.setVisible(false);
		btnReset.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnReset.setBounds(frame.getWidth(), 569, 309, 21);
		btnReset.setBackground(Utils.c42);
		frame.getContentPane().add(btnReset);

		btnReset.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {

				doNotLoadImage = true;

			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (btnReset.isEnabled())
				{
					double position = VideoPlayerCore.playerCurrentFrame;
					
					// Resolution
					comboResolution.setSelectedIndex(0);
					comboImageOption.setSelectedIndex(0);
					caseCreateSequence.setSelected(false);
					comboInterpret.setEnabled(false);
					comboInterpret.setSelectedIndex(7);
					caseRotate.setSelected(false);
					comboRotate.setSelectedIndex(3);
					comboRotate.setEnabled(false);
					caseMiror.setSelected(false);
					btnNoUpscale.setSelected(false);

					// SetTimecode
					TCset1.setEnabled(false);
					TCset1.setText("00");
					TCset2.setEnabled(false);
					TCset2.setText("00");
					TCset3.setEnabled(false);
					TCset3.setText("00");
					TCset4.setEnabled(false);
					TCset4.setText("00");
					caseSetTimecode.setSelected(false);
					caseSetTimecode.setEnabled(true);
					caseIncrementTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);
					caseGenerateFromDate.setSelected(false);
					caseGenerateFromDate.setEnabled(true);
					caseReadAudioTimecode.setSelected(false);
					caseReadAudioTimecode.setEnabled(true);
					comboReadAudioTimecode.setEnabled(false);

					// grpH264
					lock.setIcon(new FlatSVGIcon("resources/unlock.svg", 16, 16));
					isLocked = false;
					comboResolution.setEnabled(true);
					debitVideo.setEnabled(true);
					debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000",
							"25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000",
							"500", language.getProperty("lblBest").toLowerCase(),
							language.getProperty("lblGood").toLowerCase(), "auto" }));
					debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
					maximumBitrate.setEnabled(true);
					maximumBitrate.setSelectedIndex(maximumBitrate.getModel().getSize() - 1);
					bitrateSize.setEnabled(true);
					bitrateSize.setText("2000");
					lblVBR.setText("VBR");
					lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
					lblKbsH264.setVisible(true);
					h264lines.setVisible(true);

					if (comboFonctions.getSelectedItem().toString().contains("Blu-ray"))
						FFprobe.setLength();

					case2pass.setSelected(false);
					case2pass.setEnabled(true);
					caseQMax.setSelected(false);

					// grpSetAudio
					grpSetAudio.removeAll();

					if (caseNormalizeAudio.isSelected())
					{
						caseNormalizeAudio.doClick();
						comboNormalizeAudio.setSelectedIndex(23);
					}

					if (caseEqualizer.isSelected())
						caseEqualizer.doClick();
					
					if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed")) {
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] {
								language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
						lblAudioMapping.setSelectedItem("Multi");
					} else if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
							|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
							|| comboFonctions.getSelectedItem().toString().contains("XAVC")
							|| comboFonctions.getSelectedItem().toString().equals("HAP")
							|| comboFonctions.getSelectedItem().toString().equals("FFV1")) {
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] { "Multi" }));
						lblAudioMapping.setSelectedItem("Multi");
					} else {
						lblAudioMapping.setModel(new DefaultComboBoxModel<String>(new String[] {
								language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
						lblAudioMapping.setSelectedItem(language.getProperty("stereo"));
					}
					lbl48k.setSelectedIndex(2);

					caseAudioOffset.setSelected(false);
					txtAudioOffset.setEnabled(false);
					caseKeepSourceTracks.setSelected(false);

					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);

					if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
							|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
							|| comboFonctions.getSelectedItem().toString().contains("XAVC")
							|| comboFonctions.getSelectedItem().toString().equals("HAP")
							|| comboFonctions.getSelectedItem().toString().equals("FFV1")) {
						comboAudio5.setSelectedIndex(16);
						comboAudio6.setSelectedIndex(16);
						comboAudio7.setSelectedIndex(16);
						comboAudio8.setSelectedIndex(16);
					} else {
						comboAudio5.setSelectedIndex(4);
						comboAudio6.setSelectedIndex(5);
						comboAudio7.setSelectedIndex(6);
						comboAudio8.setSelectedIndex(7);
					}
					
					if (comboAudioCodec1.getModel().getSize() > 0)
					{
						comboAudioCodec1.setSelectedIndex(0);
						comboAudioCodec2.setSelectedIndex(0);
						comboAudioCodec3.setSelectedIndex(0);
						comboAudioCodec4.setSelectedIndex(0);
						comboAudioCodec5.setSelectedIndex(0);
						comboAudioCodec6.setSelectedIndex(0);
						comboAudioCodec7.setSelectedIndex(0);
						comboAudioCodec8.setSelectedIndex(0);
						
						comboAudioRate1.setSelectedIndex(2);
						comboAudioRate2.setSelectedIndex(2);
						comboAudioRate3.setSelectedIndex(2);
						comboAudioRate4.setSelectedIndex(2);
						comboAudioRate5.setSelectedIndex(2);
						comboAudioRate6.setSelectedIndex(2);
						comboAudioRate7.setSelectedIndex(2);
						comboAudioRate8.setSelectedIndex(2);
						
						comboLanguage1.setSelectedIndex(0);
						comboLanguage2.setSelectedIndex(0);
						comboLanguage3.setSelectedIndex(0);
						comboLanguage4.setSelectedIndex(0);
						comboLanguage5.setSelectedIndex(0);
						comboLanguage6.setSelectedIndex(0);
						comboLanguage7.setSelectedIndex(0);
						comboLanguage8.setSelectedIndex(0);
					}
					
					if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false
					&& language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
					{
						grpSetAudio.add(lblAudio1);
						grpSetAudio.add(comboAudio1);
						grpSetAudio.add(lblAudio2);
						grpSetAudio.add(comboAudio2);
					}
					else
					{
						grpSetAudio.add(caseAudioOffset);
						grpSetAudio.add(txtAudioOffset);
						txtAudioOffset.setText("0");
						grpSetAudio.add(lblOffsetFPS);
						
						if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
						{
							grpSetAudio.add(caseKeepSourceTracks);
						}
					}

					if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
					|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits" , "PCM 32Float", "FLAC", "AAC", "MP3", "AC3", "Opus", "Vorbis",
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

						if (language.getProperty("functionReplaceAudio")
								.equals(comboFonctions.getSelectedItem().toString()) == false
								&& language.getProperty("functionNormalization")
										.equals(comboFonctions.getSelectedItem().toString()) == false) {
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

						if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()) == false
						&& language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()) == false)
						{
							grpSetAudio.add(caseNormalizeAudio);
							grpSetAudio.add(comboNormalizeAudio);
							grpSetAudio.add(caseEqualizer);
						}
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(comboAudioBitrate);
						grpSetAudio.add(lblKbs);

					} else if ("MJPEG".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(
								new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits",
										language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(0);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if (comboFonctions.getSelectedItem().toString().contains("H.26")) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(
								new String[] { "AAC", "MP3", "AC3", "Opus", "FLAC", "PCM 16Bits", "PCM 24Bits",
										"PCM 32Bits", "ALAC 16Bits", "ALAC 24Bits", "Dolby Digital Plus",
										language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
					} else if ("WMV".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "WMA",
								language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
					} else if ("MPEG-1".equals(comboFonctions.getSelectedItem().toString())
							|| "MPEG-2".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP2", "AC3",
								language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if ("VP8".equals(comboFonctions.getSelectedItem().toString())
							|| "VP9".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec
								.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus", "AAC", "Vorbis",
										"FLAC", language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(11);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if ("AV1".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec
								.setModel(new DefaultComboBoxModel<String>(new String[] { "Opus", "AAC", "Vorbis",
										"FLAC", language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(11);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if ("Theora".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "Vorbis",
								language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if ("Xvid".equals(comboFonctions.getSelectedItem().toString())) {
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "MP3",
								language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(10);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if (comboFonctions.getSelectedItem().toString().equals("DVD")
							|| comboFonctions.getSelectedItem().toString().equals("Blu-ray")) {
						if (comboFonctions.getSelectedItem().toString().equals("DVD")) {
							comboAudioCodec.setModel(new DefaultComboBoxModel<String>(new String[] { "AC3",
									language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));
						} else
							comboAudioCodec.setModel(
									new DefaultComboBoxModel<String>(new String[] { "AC3", "Dolby Digital Plus",
											language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));

						comboAudioCodec.setSelectedIndex(0);
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(5);
						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

						if (grpSetAudio.getHeight() > 115 || grpSetAudio.getHeight() == 91)
							UIController.extendSections(grpSetAudio, 115);
						
					} else if (comboFonctions.getSelectedItem().toString().equals("WAV")
							|| comboFonctions.getSelectedItem().toString().equals("AIFF")
							|| comboFonctions.getSelectedItem().toString().equals("FLAC")
							|| comboFonctions.getSelectedItem().toString().equals("ALAC")
							|| comboFonctions.getSelectedItem().toString().equals("MP3")
							|| comboFonctions.getSelectedItem().toString().equals("AAC")
							|| comboFonctions.getSelectedItem().toString().equals("AC3")
							|| comboFonctions.getSelectedItem().toString().equals("Opus")
							|| comboFonctions.getSelectedItem().toString().equals("Vorbis")
							|| comboFonctions.getSelectedItem().toString().equals("Dolby Digital Plus")
							|| comboFonctions.getSelectedItem().toString().equals("Dolby TrueHD")) {
						// Don't change anything
					}
					else // Editing codecs
					{
						comboAudioCodec.setModel(new DefaultComboBoxModel<String>(
								new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits",
										language.getProperty("codecCopy"), language.getProperty("noAudio"), language.getProperty("custom") }));

						if (comboFonctions.getSelectedItem().toString().contains("XDCAM")
								|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
								|| comboFonctions.getSelectedItem().toString().contains("XAVC")) {
							comboAudioCodec.setSelectedIndex(1);
						} else {
							comboAudioCodec.setSelectedIndex(0);
						}

						caseNormalizeAudio.setEnabled(true);
						caseEqualizer.setEnabled(true);
						caseChangeAudioCodec.setEnabled(false);
						grpSetAudio.add(caseNormalizeAudio);
						grpSetAudio.add(comboNormalizeAudio);
						grpSetAudio.add(caseEqualizer);
						grpSetAudio.add(caseChangeAudioCodec);
						grpSetAudio.add(comboAudioCodec);
						grpSetAudio.add(lbl48k);
						lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
						grpSetAudio.add(lblAudioMapping);

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

						if (grpSetAudio.getHeight() < 169 && grpSetAudio.getHeight() > 17)
							UIController.extendSections(grpSetAudio, 169);
					}

					grpSetAudio.repaint();

					if (comboFonctions.getSelectedItem().toString().equals("DNxHD")
							|| comboFonctions.getSelectedItem().toString().equals("DNxHR")
							|| comboFonctions.getSelectedItem().toString().equals("Apple ProRes")
							|| comboFonctions.getSelectedItem().toString().equals("QT Animation")
							|| comboFonctions.getSelectedItem().toString().equals("GoPro CineForm")
							|| comboFonctions.getSelectedItem().toString().equals("Uncompressed")
							|| comboFonctions.getSelectedItem().toString().contains("XDCAM")
							|| comboFonctions.getSelectedItem().toString().equals("AVC-Intra 100")
							|| comboFonctions.getSelectedItem().toString().contains("XAVC")
							|| comboFonctions.getSelectedItem().toString().equals("HAP")
							|| comboFonctions.getSelectedItem().toString().equals("FFV1")) {
						lblPad.setVisible(true);
					}

					// grpAudio
					caseSampleRate.setSelected(false);
					if (caseMixAudio.isSelected())
						caseMixAudio.doClick();
					caseSplitAudio.setSelected(false);
					caseConvertAudioFramerate.setSelected(false);
					comboAudioIn.setEnabled(false);
					comboAudioOut.setEnabled(false);

					// grpCrop
					if (caseEnableCrop.isSelected())
						caseEnableCrop.doClick();
					
					cropLock.setIcon(new FlatSVGIcon("resources/unlock.svg", 16, 16));
					cropIsLocked = false;

					// grpOverlay
					if (caseShowTimecode.isSelected())
						caseShowTimecode.doClick();

					if (caseAddTimecode.isSelected())
						caseAddTimecode.doClick();

					if (caseShowFileName.isSelected())
						caseShowFileName.doClick();

					if (caseAddText.isSelected())
						caseAddText.doClick();

					// grpSubtitles
					if (caseAddSubtitles.isSelected())
						FunctionUtils.addSubtitles(false);

					// grpWatermark
					if (caseAddWatermark.isSelected())
						caseAddWatermark.doClick();

					// grpCorrections
					for (Component c : grpCorrections.getComponents()) {
						if (c != null && c instanceof JCheckBox && ((JCheckBox) c).isSelected()) {
							((JCheckBox) c).doClick();
						}
					}

					// grpTransitions
					for (Component c : grpTransitions.getComponents()) {
						if (c != null && c instanceof JCheckBox && ((JCheckBox) c).isSelected()) {
							((JCheckBox) c).doClick();
						}
					}

					if (comboAccel.isVisible())
						comboAccel.setSelectedIndex(0);

					caseForcerProgressif.setSelected(false);
					caseForcerEntrelacement.setSelected(false);
					caseForcerInversion.setSelected(false);
					caseForcerDesentrelacement.setSelected(false);
					comboForcerDesentrelacement.setEnabled(false);
					comboForcerDesentrelacement.setSelectedIndex(0);
					caseForcerDAR.setSelected(false);
					comboDAR.setEnabled(false);
					comboDAR.setSelectedIndex(3);
					UIController.changeFilters();

					if (comboResolution.getSelectedItem().toString().equals(language.getProperty("source"))
							|| comboResolution.getSelectedItem().toString().contains("AI")) {
						lblPad.setVisible(false);
					} else {
						lblPad.setVisible(true);
					}
					lblPad.setText(language.getProperty("lblPad"));

					caseOpenGop.setSelected(false);
					caseForceOutput.setSelected(false);
					caseFastStart.setSelected(false);
					caseFastDecode.setSelected(false);
					comboFastDecode.setEnabled(false);
					caseVarianceBoost.setSelected(false);
					comboVarianceBoost.setEnabled(false);
					caseAlpha.setSelected(false);
					comboAlpha.setEnabled(false);
					caseGOP.setSelected(false);
					gopSize.setEnabled(false);
					caseFilmGrain.setSelected(false);
					comboFilmGrain.setEnabled(false);
					caseFilmGrainDenoise.setSelected(false);
					comboFilmGrainDenoise.setEnabled(false);
					caseCABAC.setSelected(false);
					gopSize.setText("250");
					caseChunks.setSelected(false);
					chunksSize.setEnabled(false);
					chunksSize.setSelectedIndex(3);
					caseDRC.setSelected(false);
					caseTruePeak.setSelected(false);
					comboTruePeak.setEnabled(false);
					comboTruePeak.setSelectedIndex(3);
					casePeakNormalization.setSelected(false);
					comboPeakNormalization.setEnabled(false);
					comboPeakNormalization.setSelectedIndex(10);
					comboFilter.setEnabled(true);
					caseTruePeak.setEnabled(true);
					caseLRA.setEnabled(true);
					caseLRA.setSelected(false);
					comboLRA.setEnabled(false);
					comboLRA.setSelectedIndex(15);
					caseForceLevel.setSelected(false);
					comboForceProfile.setEnabled(false);
					comboForceLevel.setEnabled(false);
					caseForcePreset.setSelected(false);
					caseForcePreset.setEnabled(true);
					comboForcePreset.setEnabled(false);
					caseForceTune.setSelected(false);
					comboForceTune.setEnabled(false);
					caseForceQuality.setSelected(false);
					caseForceQuality.setEnabled(true);
					comboForceQuality.setEnabled(false);
					caseForceSpeed.setSelected(false);
					caseForceSpeed.setEnabled(true);
					comboForceSpeed.setEnabled(false);
					if (caseCreateTree.isSelected())
						caseCreateTree.doClick();
					if (casePreserveMetadata.isSelected())
						casePreserveMetadata.doClick();
					if (casePreserveSubs.isSelected())
						casePreserveSubs.doClick();
					if (caseCreateOPATOM.isSelected())
						caseCreateOPATOM.doClick();
					lblOPATOM.setText("OP-Atom");
					caseOPATOM.setSelected(false);
					if (caseAddSubtitles != null)
						caseAddSubtitles.setSelected(false);					
					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
						comboSubsSource.setEnabled(true);	
					comboSubsSource.setSelectedIndex(0);
					caseConform.setSelected(false);
					comboConform.setEnabled(false);
					caseDecimate.setSelected(false);
					comboConform.setSelectedIndex(3);
					comboFPS.setEnabled(false);
					comboFPS.setSelectedIndex(2);
					lblTFF.setText("TFF");
					if (caseAS10.isSelected())
						caseAS10.doClick();
					if (caseEncoderParams.isSelected())
						caseEncoderParams.doClick();

					// grpSequenceImages
					caseEnableSequence.setSelected(false);
					caseSequenceFPS.setSelectedIndex(2);
					caseSequenceFPS.setEnabled(false);
					caseMotionBlur.setSelected(false);
					if (caseBlend.isSelected())
						caseBlend.doClick();

					// grpFiltreImage
					caseYear.setSelected(false);
					comboYear.setSelectedIndex(0);
					comboYear.setEnabled(false);
					caseMonth.setSelected(false);
					comboMonth.setSelectedIndex(0);
					comboMonth.setEnabled(false);
					caseDay.setSelected(false);
					comboDay.setSelectedIndex(0);
					comboDay.setEnabled(false);
					caseFrom.setSelected(false);
					comboFrom.setEnabled(false);
					comboFrom.setSelectedIndex(0);
					comboTo.setEnabled(false);
					comboTo.setSelectedIndex(48);

					// grpLUTs
					if (caseLUTs.isSelected())
						caseLUTs.doClick();
					if (caseColorspace.isSelected())
						caseColorspace.doClick();
					comboColorspace.setSelectedIndex(0);

					if (caseLevels.isSelected())
						caseLevels.doClick();

					if (caseGamma.isSelected())
						caseGamma.doClick();

					if (caseColormatrix.isSelected())
						caseColormatrix.doClick();

					comboInColormatrix.setModel(new DefaultComboBoxModel<Object>(
							new String[] { "Rec. 601", "Rec. 709", "Rec. 2020", "Linear", "HDR" }));
					comboInColormatrix.setSelectedIndex(0);
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(
							new String[] { "Rec. 601", "Rec. 709", "Rec. 2020", "SDR" }));
					comboOutColormatrix.setSelectedIndex(1);

					topPanel.repaint();
					statusBar.repaint();
					topImage.repaint();

					doNotLoadImage = false;

					VideoPlayerCore.playerCurrentFrame = position;
					VideoPlayerUI.resizeAll();
					
					if (VideoPlayerUtils.playerIsPlaying())
					{
						if (VideoPlayerCore.setTime != null)
						{
							try {
								VideoPlayerCore.setTime.join();
							} catch (InterruptedException ex) {
								ex.printStackTrace();
								Thread.currentThread().interrupt();
							}
						}
						
						VideoPlayerCore.playerLoop = true;
					}
				}
			}

		});

	}
}
