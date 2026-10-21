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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.others.Equalizer;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.utils.Utils;

public class SetAudio extends Main {

	public static void grpSetAudio() {

		grpSetAudio = new CollapsiblePanel(language.getProperty("grpAudio"), true);
		grpSetAudio.setLayout(null);
		grpSetAudio.setVisible(false);
		grpSetAudio.setOpaque(false);
		grpSetAudio.setBackground(Utils.c30);
		grpSetAudio.setBounds(frame.getWidth(), 343, 312, 47);
		frame.getContentPane().add(grpSetAudio);

		grpSetAudio.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int size;	
				if (caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					size = 241;
					if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
						size = 218;					
				}
				else if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()))			
				{
					size = 93;
				}
				else if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
				{
					size = 70;
				}				
				else if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())
				|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
				|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()))
				{
					size = 169;
				}					 
				else // Codecs de sortie
				{
					if (lblAudioMapping.getSelectedItem().toString().equals("Multi")) {
						size = 169;
					} else if (lblAudioMapping.getSelectedItem().toString().equals("Mix")) {
						size = 91;
					} else
						size = 115;
				}

				UIController.extendSections(grpSetAudio, size);
			}

		});

		caseChangeAudioCodec = new JCheckBox(language.getProperty("caseAudioCodec"));
		caseChangeAudioCodec.setName("caseChangeAudioCodec");
		caseChangeAudioCodec.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseChangeAudioCodec.setBounds(7, 16, caseChangeAudioCodec.getPreferredSize().width, 23);
		grpSetAudio.add(caseChangeAudioCodec);

		caseChangeAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseChangeAudioCodec.isSelected())
				{
					comboAudioCodec.setEnabled(true);
					
					if (comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
					{
						if (grpSetAudio.getSize().getHeight() != 241)
						{
							int size = 241;
							if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
								size = 218;
								
							UIController.extendSections(grpSetAudio, size);
						}
					}
					else
					{
						comboAudioBitrate.setEnabled(true);
						lbl48k.setEnabled(true);
					}
					
					if (language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()) == false)
					{
						caseNormalizeAudio.setEnabled(true);	
						caseEqualizer.setEnabled(true);
					}
				}
				else
				{
					comboAudioCodec.setEnabled(false);
					comboAudioBitrate.setEnabled(false);
					lbl48k.setEnabled(false);
					caseNormalizeAudio.setEnabled(false);
					caseNormalizeAudio.setSelected(false);
					comboNormalizeAudio.setEnabled(false);
					caseEqualizer.setEnabled(false);
					caseEqualizer.setSelected(false);
					
					if (grpSetAudio.getSize().getHeight() == 241
					|| (grpSetAudio.getSize().getHeight() == 218 && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString())))
					{						
						int size;
						if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()))			
						{
							size = 93;
						}
						else if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
						{
							size = 70;
						}
						else if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())
						|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
						|| language.getProperty("functionMerge")
										.equals(comboFonctions.getSelectedItem().toString())) {
							size = 169;
						}
						else // Codecs de sortie
						{
							if (lblAudioMapping.getSelectedItem().toString().equals("Multi")) {
								size = 169;
							} else if (lblAudioMapping.getSelectedItem().toString().equals("Mix")) {
								size = 91;
							} else
								size = 115;
						}
						
						UIController.extendSections(grpSetAudio, size);						
					}
				}

				if (comboAudioCodec.getSelectedItem().toString().contains("PCM")
						|| comboAudioCodec.getSelectedItem().toString().contains("FLAC")
						|| comboAudioCodec.getSelectedItem().toString().contains("ALAC")) {
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
					comboAudioBitrate.setSelectedIndex(0);
				} else if (comboAudioCodec.getSelectedItem().toString().equals("AAC")
						|| comboAudioCodec.getSelectedItem().toString().equals("MP3")) {
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
				} else if (comboAudioCodec.getSelectedItem().toString().equals("AC3")
						|| comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus")) {
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(8);
				} else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio"))
						|| comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("codecCopy"))) {
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));
					comboAudioBitrate.setSelectedIndex(0);
				}
				
				//Add language to grpSetAudio
				advancedAudioSettings();
			}

		});

		comboAudioCodec = new JComboBox<String>();
		comboAudioCodec.setName("comboAudioCodec");
		comboAudioCodec.setEnabled(false);
		comboAudioCodec.setMaximumRowCount(20);
		comboAudioCodec.setModel(new DefaultComboBoxModel<String>(
		new String[] { "PCM 16Bits", "PCM 24Bits", "PCM 32Bits" , "PCM 32Float", "AAC", "MP3", "AC3", "Opus",
						"Vorbis", "Dolby Digital Plus", language.getProperty("noAudio"), language.getProperty("custom") }));
		comboAudioCodec.setSelectedIndex(0);
		comboAudioCodec.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec.setEditable(false);
		comboAudioCodec.setSize(80, 16);
		comboAudioCodec.setLocation(caseChangeAudioCodec.getLocation().x + caseChangeAudioCodec.getWidth() + 7,
				caseChangeAudioCodec.getLocation().y + 3);
		grpSetAudio.add(comboAudioCodec);

		comboAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					lblAudioMapping.setEnabled(false);
					comboAudioBitrate.setEnabled(false);
					lbl48k.setEnabled(false);
					
					if (grpSetAudio.getSize().getHeight() != 241)
					{
						int size = 241;
						if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
							size = 218;
						
						UIController.extendSections(grpSetAudio, size);
					}
				}
				else
				{
					if (caseChangeAudioCodec.isSelected())
					{
						lblAudioMapping.setEnabled(true);
						comboAudioBitrate.setEnabled(true);
						lbl48k.setEnabled(true);
					}
					
					if (comboAudioCodec.getSelectedItem().toString().equals("FLAC"))
					{
						lblKbs.setVisible(false);
					}
					else
						lblKbs.setVisible(true);
					
					if (grpSetAudio.getSize().getHeight() == 241
					|| (grpSetAudio.getSize().getHeight() == 218 && language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString())))
					{						
						int size;
						if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()))			
						{
							size = 93;
						}
						else if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
						{
							size = 70;
						}
						else if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())
						|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
						|| language.getProperty("functionMerge")
										.equals(comboFonctions.getSelectedItem().toString())) {
							size = 169;
						}
						else // Codecs de sortie
						{
							if (lblAudioMapping.getSelectedItem().toString().equals("Multi")) {
								size = 169;
							} else if (lblAudioMapping.getSelectedItem().toString().equals("Mix")) {
								size = 91;
							} else
								size = 115;
						}
						
						UIController.extendSections(grpSetAudio, size);						
					}
				}
				
				//Add language to grpSetAudio
				advancedAudioSettings();
			}
		});
		
		comboAudioBitrate = new JComboBox<String>();
		comboAudioBitrate.setName("comboAudioBitrate");
		comboAudioBitrate.setEnabled(false);
		comboAudioBitrate.setMaximumRowCount(20);
		comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
		comboAudioBitrate.setSelectedIndex(0);
		comboAudioBitrate.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate.setEditable(false);
		comboAudioBitrate.setSize(44, 16);
		comboAudioBitrate.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7,
				comboAudioCodec.getLocation().y);
		grpSetAudio.add(comboAudioBitrate);

		comboAudioBitrate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (comboAudioCodec.getSelectedItem().equals(language.getProperty("custom"))
				&& (comboAudioBitrate.getSelectedItem().equals(language.getProperty("custom").toLowerCase()) == false || grpBitrate.isVisible() == false))
				{
					try {
						comboAudioBitrate1.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate2.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate3.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate4.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate5.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate6.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate7.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
					try {
						comboAudioBitrate8.setSelectedIndex(comboAudioBitrate.getSelectedIndex());
					} catch (Exception e1) {}
						
					if (grpBitrate.isVisible())
					{
						comboAudioBitrate.setSelectedIndex(comboAudioBitrate.getItemCount() - 1);
					}
				}
			}

		});
		
		lblKbs = new JLabel("kb/s");
		lblKbs.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblKbs.setBounds(comboAudioBitrate.getLocation().x + comboAudioBitrate.getWidth() + 3,
				caseChangeAudioCodec.getLocation().y + 3, 33, 16);
		grpSetAudio.add(lblKbs);

		lblAudioMapping = new JComboBox<String>();
		lblAudioMapping.setModel(new DefaultComboBoxModel<String>(
				new String[] { language.getProperty("stereo"), "Multi", language.getProperty("mono"), "Mix" }));
		lblAudioMapping.setName("lblAudioMapping");
		lblAudioMapping.setBackground(Utils.c42);
		lblAudioMapping.setEditable(false);
		lblAudioMapping.setFont(new Font(mainFont, Font.PLAIN, 10));
		lblAudioMapping.setLocation(comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7,
				comboAudioCodec.getLocation().y);
		lblAudioMapping.setSize(lblKbs.getLocation().x + lblKbs.getSize().width - 5 - 7
				- (comboAudioCodec.getLocation().x + comboAudioCodec.getWidth() + 7), 16);
		grpSetAudio.add(lblAudioMapping);

		lblAudioMapping.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {

				if (lblAudioMapping.isFocusOwner())
				{
					if (lblAudioMapping.getSelectedItem().toString().equals("Multi"))
					{
						grpSetAudio.add(lblAudio1);
						grpSetAudio.add(comboAudio1);
						grpSetAudio.add(lblAudio2);
						grpSetAudio.add(comboAudio2);
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

						if (grpSetAudio.getHeight() != 169)
							UIController.extendSections(grpSetAudio, 169);
						
					}
					else if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
					{
						if (grpSetAudio.getHeight() != 91)
							UIController.extendSections(grpSetAudio, 91);

						grpSetAudio.remove(lblAudio1);
						grpSetAudio.remove(comboAudio1);
						grpSetAudio.remove(lblAudio2);
						grpSetAudio.remove(comboAudio2);
						grpSetAudio.remove(lblAudio3);
						grpSetAudio.remove(comboAudio3);
						grpSetAudio.remove(lblAudio4);
						grpSetAudio.remove(comboAudio4);
						grpSetAudio.remove(lblAudio5);
						grpSetAudio.remove(comboAudio5);
						grpSetAudio.remove(lblAudio6);
						grpSetAudio.remove(comboAudio6);
						grpSetAudio.remove(lblAudio7);
						grpSetAudio.remove(comboAudio7);
						grpSetAudio.remove(lblAudio8);
						grpSetAudio.remove(comboAudio8);
					}
					else
					{
						if (grpSetAudio.getHeight() != 115)
							UIController.extendSections(grpSetAudio, 115);

						grpSetAudio.add(lblAudio1);
						grpSetAudio.add(comboAudio1);
						grpSetAudio.add(lblAudio2);
						grpSetAudio.add(comboAudio2);
						grpSetAudio.remove(lblAudio3);
						grpSetAudio.remove(comboAudio3);
						grpSetAudio.remove(lblAudio4);
						grpSetAudio.remove(comboAudio4);
						grpSetAudio.remove(lblAudio5);
						grpSetAudio.remove(comboAudio5);
						grpSetAudio.remove(lblAudio6);
						grpSetAudio.remove(comboAudio6);
						grpSetAudio.remove(lblAudio7);
						grpSetAudio.remove(comboAudio7);
						grpSetAudio.remove(lblAudio8);
						grpSetAudio.remove(comboAudio8);
					}
					
					grpSetAudio.repaint();

					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {}
				}
			}

		});

		lbl48k = new JComboBox<String>();
		lbl48k.setMaximumRowCount(20);
		lbl48k.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		lbl48k.setSelectedIndex(2);
		lbl48k.setName("lbl48k");
		lbl48k.setEditable(false);
		lbl48k.setEnabled(false);
		lbl48k.setFont(new Font(mainFont, Font.PLAIN, 10));
		lbl48k.setSize(46, 16);
		lbl48k.setLocation(lblKbs.getLocation().x + lblKbs.getSize().width - 5, lblKbs.getLocation().y);
		grpSetAudio.add(lbl48k);

		comboAudioCodec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboAudioCodec.getSelectedItem().toString().equals("FLAC")) {
					lblAudioBitrate.setText(language.getProperty("lblValue"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(
							new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
					comboAudioBitrate.setSelectedIndex(5);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(5);
				} else if (comboAudioCodec.getSelectedItem().toString().contains("PCM")
						|| comboAudioCodec.getSelectedItem().toString().contains("ALAC")) {
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
					comboAudioBitrate.setSelectedIndex(0);
					if (comboFonctions.getSelectedItem().toString().equals("MJPEG")
							|| comboFonctions.getSelectedItem().toString().contains("H.26")) {
						debitAudio.setModel(comboAudioBitrate.getModel());
						debitAudio.setSelectedIndex(0);
					}
				} else if (comboAudioCodec.getSelectedItem().toString().equals("AAC")
						|| comboAudioCodec.getSelectedItem().toString().equals("MP3")) {
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(10);
				} else if (comboAudioCodec.getSelectedItem().toString().equals("AC3")
						|| comboAudioCodec.getSelectedItem().toString().equals("Dolby Digital Plus")) {
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(8);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(8);
				} else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("noAudio"))
						|| comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("codecCopy"))) {
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));
					comboAudioBitrate.setSelectedIndex(0);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(0);
				} else if (comboAudioCodec.getSelectedItem().toString().equals("Opus")) {
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(11);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(11);
				}
				else if (comboAudioCodec.getSelectedItem().toString().equals(language.getProperty("custom")) == false) // Codecs de sortie
				{
					lblAudioBitrate.setText(language.getProperty("lblAudioBitrate"));
					comboAudioBitrate.setModel(new DefaultComboBoxModel<String>(audioValues));
					comboAudioBitrate.setSelectedIndex(10);
					debitAudio.setModel(comboAudioBitrate.getModel());
					debitAudio.setSelectedIndex(10);
				}

				if (comboFonctions.getSelectedItem().toString().contains("H.26")) {
					if (comboAudioCodec.getSelectedItem().toString().contains("FLAC")) {
						comboFilter.setSelectedIndex(2);
						lblAudioKbs.setVisible(false);
					} else
						lblAudioKbs.setVisible(true);
				}
			}

		});

		caseNormalizeAudio = new JCheckBox(language.getProperty("functionNormalization") + language.getProperty("colon"));
		caseNormalizeAudio.setName("caseNormalizeAudio");
		caseNormalizeAudio.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseNormalizeAudio.setBounds(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight(),caseNormalizeAudio.getPreferredSize().width, 23);
		grpSetAudio.add(caseNormalizeAudio);

		caseNormalizeAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseNormalizeAudio.isSelected()) {
					comboNormalizeAudio.setEnabled(true);
				} else {
					comboNormalizeAudio.setEnabled(false);
				}
			}

		});

		String types[] = new String[31];

		types[0] = "0 LUFS";
		int i = 1;
		do {
			types[i] = ("-" + i + " LUFS");
			i++;
		} while (i < 31);

		final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(types);
		comboNormalizeAudio = new JComboBox<Object>(model);
		comboNormalizeAudio.setName("comboNormalizeAudio");
		comboNormalizeAudio.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboNormalizeAudio.setEnabled(false);
		comboNormalizeAudio.setSelectedIndex(23);
		comboNormalizeAudio.setMaximumRowCount(20);
		comboNormalizeAudio.setBounds(caseNormalizeAudio.getX() + caseNormalizeAudio.getWidth() + 7,
				caseNormalizeAudio.getLocation().y + 3, 82, 16);
		grpSetAudio.add(comboNormalizeAudio);

		caseEqualizer = new JCheckBox(language.getProperty("frameEqualizer"));
		caseEqualizer.setName("caseEqualizer");
		caseEqualizer.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseEqualizer.setBounds(7, caseNormalizeAudio.getY() + caseNormalizeAudio.getHeight(),
		caseEqualizer.getPreferredSize().width, 23);
		grpSetAudio.add(caseEqualizer);

		caseEqualizer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseEqualizer.isSelected())
				{
					if (Equalizer.frame == null)
						new Equalizer();
					else
					{
						Utils.changeFrameVisibility(Equalizer.frame, false);
						VideoPlayerCore.playerAudioSetTime(VideoPlayerCore.playerCurrentFrame);
					}
				}
				else
				{
					Utils.changeFrameVisibility(Equalizer.frame, true);
					VideoPlayerCore.playerAudioSetTime(VideoPlayerCore.playerCurrentFrame);
				}
			}

		});
		
		caseAudioOffset = new JCheckBox(language.getProperty("caseAudioOffset"));
		caseAudioOffset.setName("caseAudioOffset");
		caseAudioOffset.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseAudioOffset.setBounds(7, caseChangeAudioCodec.getY() + caseChangeAudioCodec.getHeight(),
				caseAudioOffset.getPreferredSize().width + 4, 23);
		grpSetAudio.add(caseAudioOffset);

		caseAudioOffset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseAudioOffset.isSelected()) {
					txtAudioOffset.setEnabled(true);
				} else {
					txtAudioOffset.setEnabled(false);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		txtAudioOffset = new JTextField("0");
		txtAudioOffset.setName("txtAudioOffset");
		txtAudioOffset.setFont(new Font(mainFont, Font.PLAIN, 11));
		txtAudioOffset.setSize(32, 16);
		txtAudioOffset.setEnabled(false);
		txtAudioOffset.setLocation(caseAudioOffset.getLocation().x + caseAudioOffset.getWidth() + 7,
				caseAudioOffset.getLocation().y + 4);
		grpSetAudio.add(txtAudioOffset);

		txtAudioOffset.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' && caracter != '-'
						|| txtAudioOffset.getText().length() >= 4) {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (txtAudioOffset.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload
																				// the frame
				}
			}
		});

		lblOffsetFPS = new JLabel(Main.language.getProperty("lblFrames"));
		lblOffsetFPS.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblOffsetFPS.setBounds(txtAudioOffset.getLocation().x + txtAudioOffset.getWidth() + 3,
				caseAudioOffset.getLocation().y + 4, lblOffsetFPS.getPreferredSize().width, 16);
		grpSetAudio.add(lblOffsetFPS);

		caseKeepSourceTracks = new JCheckBox(language.getProperty("caseKeepSourceTracks"));
		caseKeepSourceTracks.setName("caseKeepSourceTracks");
		caseKeepSourceTracks.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseKeepSourceTracks.setBounds(7, caseAudioOffset.getY() + caseAudioOffset.getHeight(),
				caseKeepSourceTracks.getPreferredSize().width + 4, 23);
		grpSetAudio.add(caseKeepSourceTracks);

		// Audio Mapping
		lblAudio1 = new JLabel(language.getProperty("audio") + " 1" + language.getProperty("colon"));
		lblAudio1.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio1.setBounds(12, caseNormalizeAudio.getLocation().y + caseNormalizeAudio.getHeight() + 2,
				lblAudio1.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio1);

		comboAudio1 = new JComboBox<String>();
		comboAudio1.setName("comboAudio1");
		comboAudio1.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio1.setSelectedIndex(0);
		comboAudio1.setMaximumRowCount(20);
		comboAudio1.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio1.setEditable(false);
		comboAudio1.setSize(65, 16);
		comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, lblAudio1.getLocation().y + 1);
		grpSetAudio.add(comboAudio1);
		
		comboAudioCodec1 = new JComboBox<String>();
		comboAudioCodec1.setName("comboAudioCodec1");
		comboAudioCodec1.setMaximumRowCount(20);
		comboAudioCodec1.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec1.setEditable(false);
		comboAudioCodec1.setSize(55, 16);
		
		comboAudioCodec1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec1.getSelectedItem() != null)
				{				
					if (comboAudioCodec1.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate1.setSelectedIndex(5);
					}
					else if (comboAudioCodec1.getSelectedItem().toString().contains("PCM") || comboAudioCodec1.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate1.setSelectedIndex(0);
					}
					else if (comboAudioCodec1.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec1.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate1.setSelectedIndex(0);
					}				
					else if (comboAudioCodec1.getSelectedItem().toString().equals("AC3") || comboAudioCodec1.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate1.setSelectedIndex(8);
					}
					else if (comboAudioCodec1.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate1.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate1.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate1.setSelectedIndex(10);
					}		
				}
			}
		
		});
 		
		comboAudioCodec2 = new JComboBox<String>();
		comboAudioCodec2.setName("comboAudioCodec2");
		comboAudioCodec2.setMaximumRowCount(20);
		comboAudioCodec2.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec2.setEditable(false);
		comboAudioCodec2.setSize(comboAudioCodec1.getSize());

		comboAudioCodec2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec2.getSelectedItem() != null)
				{
					if (comboAudioCodec2.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate2.setSelectedIndex(5);
					}
					else if (comboAudioCodec2.getSelectedItem().toString().contains("PCM") || comboAudioCodec2.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate2.setSelectedIndex(0);
					}
					else if (comboAudioCodec2.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec2.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate2.setSelectedIndex(0);
					}				
					else if (comboAudioCodec2.getSelectedItem().toString().equals("AC3") || comboAudioCodec2.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate2.setSelectedIndex(8);
					}
					else if (comboAudioCodec2.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate2.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate2.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate2.setSelectedIndex(10);
					}	
				}
			}
		
		});
		
		comboAudioCodec3 = new JComboBox<String>();
		comboAudioCodec3.setName("comboAudioCodec3");
		comboAudioCodec3.setMaximumRowCount(20);
		comboAudioCodec3.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec3.setEditable(false);
		comboAudioCodec3.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec3.getSelectedItem() != null)
				{
					if (comboAudioCodec3.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate3.setSelectedIndex(5);
					}
					else if (comboAudioCodec3.getSelectedItem().toString().contains("PCM") || comboAudioCodec3.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate3.setSelectedIndex(0);
					}
					else if (comboAudioCodec3.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec3.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate3.setSelectedIndex(0);
					}				
					else if (comboAudioCodec3.getSelectedItem().toString().equals("AC3") || comboAudioCodec3.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate3.setSelectedIndex(8);
					}
					else if (comboAudioCodec3.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate3.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate3.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate3.setSelectedIndex(10);
					}	
				}
			}
		
		});
		
		comboAudioCodec4 = new JComboBox<String>();
		comboAudioCodec4.setName("comboAudioCodec4");
		comboAudioCodec4.setMaximumRowCount(20);
		comboAudioCodec4.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec4.setEditable(false);
		comboAudioCodec4.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec4.getSelectedItem() != null)
				{
					if (comboAudioCodec4.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate4.setSelectedIndex(5);
					}
					else if (comboAudioCodec4.getSelectedItem().toString().contains("PCM") || comboAudioCodec4.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate4.setSelectedIndex(0);
					}
					else if (comboAudioCodec4.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec4.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate4.setSelectedIndex(0);
					}				
					else if (comboAudioCodec4.getSelectedItem().toString().equals("AC3") || comboAudioCodec4.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate4.setSelectedIndex(8);
					}
					else if (comboAudioCodec4.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate4.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate4.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate4.setSelectedIndex(10);
					}		
				}
			}
		
		});
		
		comboAudioCodec5 = new JComboBox<String>();
		comboAudioCodec5.setName("comboAudioCodec5");
		comboAudioCodec5.setMaximumRowCount(20);
		comboAudioCodec5.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec5.setEditable(false);
		comboAudioCodec5.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec5.getSelectedItem() != null)
				{
					if (comboAudioCodec5.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate5.setSelectedIndex(5);
					}
					else if (comboAudioCodec5.getSelectedItem().toString().contains("PCM") || comboAudioCodec5.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate5.setSelectedIndex(0);
					}
					else if (comboAudioCodec5.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec5.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate5.setSelectedIndex(0);
					}				
					else if (comboAudioCodec5.getSelectedItem().toString().equals("AC3") || comboAudioCodec5.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate5.setSelectedIndex(8);
					}
					else if (comboAudioCodec5.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate5.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate5.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate5.setSelectedIndex(10);
					}		
				}
			}
		
		});
		
		comboAudioCodec6 = new JComboBox<String>();
		comboAudioCodec6.setName("comboAudioCodec6");
		comboAudioCodec6.setMaximumRowCount(20);
		comboAudioCodec6.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec6.setEditable(false);
		comboAudioCodec6.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (comboAudioCodec6.getSelectedItem() != null)
				{
					if (comboAudioCodec6.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate6.setSelectedIndex(5);
					}
					else if (comboAudioCodec6.getSelectedItem().toString().contains("PCM") || comboAudioCodec6.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate6.setSelectedIndex(0);
					}
					else if (comboAudioCodec6.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec6.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate6.setSelectedIndex(0);
					}				
					else if (comboAudioCodec6.getSelectedItem().toString().equals("AC3") || comboAudioCodec6.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate6.setSelectedIndex(8);
					}
					else if (comboAudioCodec6.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate6.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate6.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate6.setSelectedIndex(10);
					}	
				}
			}
		
		});
		
		comboAudioCodec7 = new JComboBox<String>();
		comboAudioCodec7.setName("comboAudioCodec7");
		comboAudioCodec7.setMaximumRowCount(20);
		comboAudioCodec7.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec7.setEditable(false);
		comboAudioCodec7.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec7.getSelectedItem() != null)
				{
					if (comboAudioCodec7.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate7.setSelectedIndex(5);
					}
					else if (comboAudioCodec7.getSelectedItem().toString().contains("PCM") || comboAudioCodec7.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate7.setSelectedIndex(0);
					}
					else if (comboAudioCodec7.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec7.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate7.setSelectedIndex(0);
					}				
					else if (comboAudioCodec7.getSelectedItem().toString().equals("AC3") || comboAudioCodec7.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate7.setSelectedIndex(8);
					}
					else if (comboAudioCodec7.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate7.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate7.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate7.setSelectedIndex(10);
					}	
				}
			}
		
		});
		
		comboAudioCodec8 = new JComboBox<String>();
		comboAudioCodec8.setName("comboAudioCodec8");
		comboAudioCodec8.setMaximumRowCount(20);
		comboAudioCodec8.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioCodec8.setEditable(false);
		comboAudioCodec8.setSize(comboAudioCodec1.getSize());
		
		comboAudioCodec8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboAudioCodec8.getSelectedItem() != null)
				{
					if (comboAudioCodec8.getSelectedItem().toString().equals("FLAC"))
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
						comboAudioBitrate8.setSelectedIndex(5);
					}
					else if (comboAudioCodec8.getSelectedItem().toString().contains("PCM") || comboAudioCodec8.getSelectedItem().toString().contains("ALAC"))
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
						comboAudioBitrate8.setSelectedIndex(0);
					}
					else if (comboAudioCodec8.getSelectedItem().toString().equals(language.getProperty("noAudio")) || comboAudioCodec8.getSelectedItem().toString().equals(language.getProperty("codecCopy")))
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
						comboAudioBitrate8.setSelectedIndex(0);
					}				
					else if (comboAudioCodec8.getSelectedItem().toString().equals("AC3") || comboAudioCodec8.getSelectedItem().toString().equals("Dolby Digital Plus"))
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate8.setSelectedIndex(8);
					}
					else if (comboAudioCodec8.getSelectedItem().toString().equals("Opus"))
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate8.setSelectedIndex(11);
					}
					else
					{
						comboAudioBitrate8.setModel(new DefaultComboBoxModel<String>(audioValues));
						comboAudioBitrate8.setSelectedIndex(10);
					}	
				}
			}
		
		});
		
		comboAudioBitrate1 = new JComboBox<String>();
		comboAudioBitrate1.setName("comboAudioBitrate1");
		comboAudioBitrate1.setMaximumRowCount(20);
		comboAudioBitrate1.setEditable(false);
		comboAudioBitrate1.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate1.setSize(45, 16);
		
		comboAudioBitrate2 = new JComboBox<String>();
		comboAudioBitrate2.setName("comboAudioBitrate2");
		comboAudioBitrate2.setMaximumRowCount(20);
		comboAudioBitrate2.setEditable(false);
		comboAudioBitrate2.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate2.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate3 = new JComboBox<String>();
		comboAudioBitrate3.setName("comboAudioBitrate3");
		comboAudioBitrate3.setMaximumRowCount(20);
		comboAudioBitrate3.setEditable(false);
		comboAudioBitrate3.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate3.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate4 = new JComboBox<String>();
		comboAudioBitrate4.setName("comboAudioBitrate4");
		comboAudioBitrate4.setMaximumRowCount(20);
		comboAudioBitrate4.setEditable(false);
		comboAudioBitrate4.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate4.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate5 = new JComboBox<String>();
		comboAudioBitrate5.setName("comboAudioBitrate5");
		comboAudioBitrate5.setMaximumRowCount(20);
		comboAudioBitrate5.setEditable(false);
		comboAudioBitrate5.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate5.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate6 = new JComboBox<String>();
		comboAudioBitrate6.setName("comboAudioBitrate6");
		comboAudioBitrate6.setMaximumRowCount(20);
		comboAudioBitrate6.setEditable(false);
		comboAudioBitrate6.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate6.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate7 = new JComboBox<String>();
		comboAudioBitrate7.setName("comboAudioBitrate7");
		comboAudioBitrate7.setMaximumRowCount(20);
		comboAudioBitrate7.setEditable(false);
		comboAudioBitrate7.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate7.setSize(comboAudioBitrate1.getSize());
		
		comboAudioBitrate8 = new JComboBox<String>();
		comboAudioBitrate8.setName("comboAudioBitrate8");
		comboAudioBitrate8.setMaximumRowCount(20);
		comboAudioBitrate8.setEditable(false);
		comboAudioBitrate8.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioBitrate8.setSize(comboAudioBitrate1.getSize());
		
		ActionListener fileSize = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					FFPROBE.setFilesize();		
				} catch (Exception er) {}
			}			
		};
		
		comboAudioBitrate1.addActionListener(fileSize);
		comboAudioBitrate2.addActionListener(fileSize);
		comboAudioBitrate3.addActionListener(fileSize);
		comboAudioBitrate4.addActionListener(fileSize);
		comboAudioBitrate5.addActionListener(fileSize);
		comboAudioBitrate6.addActionListener(fileSize);
		comboAudioBitrate7.addActionListener(fileSize);
		comboAudioBitrate8.addActionListener(fileSize);
		
		comboAudioRate1 = new JComboBox<String>();
		comboAudioRate1.setMaximumRowCount(20);
		comboAudioRate1.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate1.setSelectedIndex(2);
		comboAudioRate1.setName("comboAudioRate1");
		comboAudioRate1.setEditable(false);
		comboAudioRate1.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate1.setSize(46, 16);
		
		comboAudioRate2 = new JComboBox<String>();
		comboAudioRate2.setMaximumRowCount(20);
		comboAudioRate2.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate2.setSelectedIndex(2);
		comboAudioRate2.setName("comboAudioRate2");
		comboAudioRate2.setEditable(false);
		comboAudioRate2.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate2.setSize(comboAudioRate1.getSize());

		comboAudioRate3 = new JComboBox<String>();
		comboAudioRate3.setMaximumRowCount(20);
		comboAudioRate3.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate3.setSelectedIndex(2);
		comboAudioRate3.setName("comboAudioRate3");
		comboAudioRate3.setEditable(false);
		comboAudioRate3.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate3.setSize(comboAudioRate1.getSize());
		
		comboAudioRate4 = new JComboBox<String>();
		comboAudioRate4.setMaximumRowCount(20);
		comboAudioRate4.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate4.setSelectedIndex(2);
		comboAudioRate4.setName("comboAudioRate4");
		comboAudioRate4.setEditable(false);
		comboAudioRate4.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate4.setSize(comboAudioRate1.getSize());
		
		comboAudioRate5 = new JComboBox<String>();
		comboAudioRate5.setMaximumRowCount(20);
		comboAudioRate5.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate5.setSelectedIndex(2);
		comboAudioRate5.setName("comboAudioRate5");
		comboAudioRate5.setEditable(false);
		comboAudioRate5.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate5.setSize(comboAudioRate1.getSize());
		
		comboAudioRate6 = new JComboBox<String>();
		comboAudioRate6.setMaximumRowCount(20);
		comboAudioRate6.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate6.setSelectedIndex(2);
		comboAudioRate6.setName("comboAudioRate6");
		comboAudioRate6.setEditable(false);
		comboAudioRate6.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate6.setSize(comboAudioRate1.getSize());

		comboAudioRate7 = new JComboBox<String>();
		comboAudioRate7.setMaximumRowCount(20);
		comboAudioRate7.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate7.setSelectedIndex(2);
		comboAudioRate7.setName("comboAudioRate7");
		comboAudioRate7.setEditable(false);
		comboAudioRate7.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate7.setSize(comboAudioRate1.getSize());
		
		comboAudioRate8 = new JComboBox<String>();
		comboAudioRate8.setMaximumRowCount(20);
		comboAudioRate8.setModel(new DefaultComboBoxModel<String>(new String[] { "192k", "96k", "48k", "44.1k", "16k", "8k" }));
		comboAudioRate8.setSelectedIndex(2);
		comboAudioRate8.setName("comboAudioRate8");
		comboAudioRate8.setEditable(false);
		comboAudioRate8.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudioRate8.setSize(comboAudioRate1.getSize());
		
		//Add comboBox languages
		String[] allLanguages = new String[Utils.ISO_639_2_LANGUAGES.length + 1];	
		allLanguages[0] = "default";		
		
		int x = 1;
		for (String[] language : Utils.ISO_639_2_LANGUAGES)
		{
		    allLanguages[x] = language[2].toLowerCase();
		    x++;
		}
		
		comboLanguage1 = new JComboBox<String>();
		comboLanguage1.setName("comboLanguage1");
		comboLanguage1.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage1.setSelectedIndex(0);
		comboLanguage1.setMaximumRowCount(20);
		comboLanguage1.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage1.setEditable(false);
		comboLanguage1.setSize(55, 16);

		comboAudio1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboAudio1.getSelectedIndex() == 16) {
					comboAudio2.setSelectedIndex(16);
					comboAudio3.setSelectedIndex(16);
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);

					if (comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")) == false)
					{
						comboAudioCodec.setSelectedIndex(comboAudioCodec.getItemCount() - 2);
					}
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		lblAudio2 = new JLabel(language.getProperty("audio") + " 2" + language.getProperty("colon"));
		lblAudio2.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio2.setBounds(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y,
				lblAudio2.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio2);

		comboAudio2 = new JComboBox<String>();
		comboAudio2.setName("comboAudio2");
		comboAudio2.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio2.setSelectedIndex(1);
		comboAudio2.setMaximumRowCount(20);
		comboAudio2.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio2.setEditable(false);
		comboAudio2.setSize(comboAudio1.getWidth(), 16);
		comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
		grpSetAudio.add(comboAudio2);

		comboAudio2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio2.getSelectedIndex() == 16) {
					comboAudio3.setSelectedIndex(16);
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage2 = new JComboBox<String>();
		comboLanguage2.setName("comboLanguage2");
		comboLanguage2.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage2.setSelectedIndex(0);
		comboLanguage2.setMaximumRowCount(20);
		comboLanguage2.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage2.setEditable(false);
		comboLanguage2.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio3 = new JLabel(language.getProperty("audio") + " 3" + language.getProperty("colon"));
		lblAudio3.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio3.setBounds(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2,
				lblAudio3.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio3);

		comboAudio3 = new JComboBox<String>();
		comboAudio3.setName("comboAudio3");
		comboAudio3.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio3.setSelectedIndex(2);
		comboAudio3.setMaximumRowCount(20);
		comboAudio3.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio3.setEditable(false);
		comboAudio3.setSize(comboAudio1.getWidth(), 16);
		comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);
		grpSetAudio.add(comboAudio3);

		comboAudio3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio3.getSelectedIndex() == 16) {
					comboAudio4.setSelectedIndex(16);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage3 = new JComboBox<String>();
		comboLanguage3.setName("comboLanguage3");
		comboLanguage3.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage3.setSelectedIndex(0);
		comboLanguage3.setMaximumRowCount(20);
		comboLanguage3.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage3.setEditable(false);
		comboLanguage3.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio4 = new JLabel(language.getProperty("audio") + " 4" + language.getProperty("colon"));
		lblAudio4.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio4.setBounds(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2,
				lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio4);

		comboAudio4 = new JComboBox<String>();
		comboAudio4.setName("comboAudio4");
		comboAudio4.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio4.setSelectedIndex(3);
		comboAudio4.setMaximumRowCount(20);
		comboAudio4.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio4.setEditable(false);
		comboAudio4.setSize(comboAudio1.getWidth(), 16);
		comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);
		grpSetAudio.add(comboAudio4);

		comboAudio4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio4.getSelectedIndex() == 16) {
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage4 = new JComboBox<String>();
		comboLanguage4.setName("comboLanguage4");
		comboLanguage4.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage4.setSelectedIndex(0);
		comboLanguage4.setMaximumRowCount(20);
		comboLanguage4.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage4.setEditable(false);
		comboLanguage4.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio5 = new JLabel(language.getProperty("audio") + " 5" + language.getProperty("colon"));
		lblAudio5.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio5.setBounds(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2,
				lblAudio5.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio5);

		comboAudio5 = new JComboBox<String>();
		comboAudio5.setName("comboAudio5");
		comboAudio5.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio5.setSelectedIndex(4);
		comboAudio5.setMaximumRowCount(20);
		comboAudio5.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio5.setEditable(false);
		comboAudio5.setSize(comboAudio1.getWidth(), 16);
		comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
		grpSetAudio.add(comboAudio5);

		comboAudio5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio5.getSelectedIndex() == 16) {
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage5 = new JComboBox<String>();
		comboLanguage5.setName("comboLanguage5");
		comboLanguage5.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage5.setSelectedIndex(0);
		comboLanguage5.setMaximumRowCount(20);
		comboLanguage5.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage5.setEditable(false);
		comboLanguage5.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio6 = new JLabel(language.getProperty("audio") + " 6" + language.getProperty("colon"));
		lblAudio6.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio6.setBounds(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2,
				lblAudio4.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio6);

		comboAudio6 = new JComboBox<String>();
		comboAudio6.setName("comboAudio6");
		comboAudio6.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio6.setSelectedIndex(5);
		comboAudio6.setMaximumRowCount(20);
		comboAudio6.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio6.setEditable(false);
		comboAudio6.setSize(comboAudio1.getWidth(), 16);
		comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
		grpSetAudio.add(comboAudio6);

		comboAudio6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio6.getSelectedIndex() == 16) {
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage6 = new JComboBox<String>();
		comboLanguage6.setName("comboLanguage6");
		comboLanguage6.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage6.setSelectedIndex(0);
		comboLanguage6.setMaximumRowCount(20);
		comboLanguage6.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage6.setEditable(false);
		comboLanguage6.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio7 = new JLabel(language.getProperty("audio") + " 7" + language.getProperty("colon"));
		lblAudio7.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio7.setBounds(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2,
				lblAudio7.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio7);

		comboAudio7 = new JComboBox<String>();
		comboAudio7.setName("comboAudio7");
		comboAudio7.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio7.setSelectedIndex(6);
		comboAudio7.setMaximumRowCount(20);
		comboAudio7.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio7.setEditable(false);
		comboAudio7.setSize(comboAudio1.getWidth(), 16);
		comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
		grpSetAudio.add(comboAudio7);

		comboAudio7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboAudio7.getSelectedIndex() == 16) {
					comboAudio8.setSelectedIndex(16);
				}

				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});

		comboLanguage7 = new JComboBox<String>();
		comboLanguage7.setName("comboLanguage7");
		comboLanguage7.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage7.setSelectedIndex(0);
		comboLanguage7.setMaximumRowCount(20);
		comboLanguage7.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage7.setEditable(false);
		comboLanguage7.setSize(comboLanguage1.getWidth(), 16);
		
		lblAudio8 = new JLabel(language.getProperty("audio") + " 8" + language.getProperty("colon"));
		lblAudio8.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudio8.setBounds(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2,
				lblAudio8.getPreferredSize().width, 16);
		grpSetAudio.add(lblAudio8);

		comboAudio8 = new JComboBox<String>();
		comboAudio8.setName("comboAudio8");
		comboAudio8.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16", language.getProperty("noAudio") }));
		comboAudio8.setSelectedIndex(7);
		comboAudio8.setMaximumRowCount(20);
		comboAudio8.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAudio8.setEditable(false);
		comboAudio8.setSize(comboAudio1.getWidth(), 16);
		comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
		grpSetAudio.add(comboAudio8);

		comboAudio8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (lblAudioMapping.getSelectedItem().toString().equals("Multi")
				|| caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
				{
					try {
						FFPROBE.setFilesize();
					} catch (Exception e1) {
					}
				}
			}

		});
	
		comboLanguage8 = new JComboBox<String>();
		comboLanguage8.setName("comboLanguage8");
		comboLanguage8.setModel(new DefaultComboBoxModel<String>(allLanguages));
		comboLanguage8.setSelectedIndex(0);
		comboLanguage8.setMaximumRowCount(20);
		comboLanguage8.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLanguage8.setEditable(false);
		comboLanguage8.setSize(comboLanguage1.getWidth(), 16);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void advancedAudioSettings() {
		
		if (grpSetAudio.isVisible() && caseChangeAudioCodec.isSelected() && comboAudioCodec.getSelectedItem().equals(language.getProperty("custom")))
		{			
			grpSetAudio.add(lblAudio1);
			grpSetAudio.add(comboAudio1);
			grpSetAudio.add(lblAudio2);
			grpSetAudio.add(comboAudio2);
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
			
			for (Component c : grpSetAudio.getComponents())
			{
				if (c instanceof JLabel && ((JLabel) c).getText().contains(language.getProperty("audio")))
				{
					if (getLanguage.contains(Locale.of("ar").getDisplayLanguage())
					|| getLanguage.contains(Locale.of("zh").getDisplayLanguage()))
					{
						((JLabel) c).setText(((JLabel) c).getText().replace(language.getProperty("audio") + " ","A"));
					}
					else
						((JLabel) c).setText(((JLabel) c).getText().replace(language.getProperty("audio") + " ", language.getProperty("audio").substring(0,1).toUpperCase()));
					
					c.setSize(((JLabel) c).getPreferredSize().width, c.getHeight());
				}
			}
			
			if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString()))
			{
				lblAudio1.setLocation(12, caseKeepSourceTracks.getY() + caseKeepSourceTracks.getHeight() + 2);
			}
			else if (language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
			{
				lblAudio1.setLocation(12, caseAudioOffset.getY() + caseAudioOffset.getHeight() + 2);
			}	
			else
				lblAudio1.setLocation(12, caseEqualizer.getY() + caseEqualizer.getHeight() + 2);

			comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 2, lblAudio1.getLocation().y + 1);			
			comboAudioCodec1.setLocation(comboAudio1.getX() + comboAudio1.getWidth(), comboAudio1.getLocation().y);
			grpSetAudio.add(comboAudioCodec1);
			comboAudioBitrate1.setLocation(comboAudioCodec1.getX() + comboAudioCodec1.getWidth(), comboAudioCodec1.getLocation().y);
			grpSetAudio.add(comboAudioBitrate1);
			comboAudioRate1.setLocation(comboAudioBitrate1.getX() + comboAudioBitrate1.getWidth(), comboAudioBitrate1.getLocation().y);
			grpSetAudio.add(comboAudioRate1);
			comboLanguage1.setLocation(comboAudioRate1.getX() + comboAudioRate1.getWidth(), comboAudioRate1.getLocation().y);	
			grpSetAudio.add(comboLanguage1);
			
			lblAudio2.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
			comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 2, lblAudio2.getLocation().y + 1);			
			comboAudioCodec2.setLocation(comboAudio2.getX() + comboAudio2.getWidth(), comboAudio2.getLocation().y);
			grpSetAudio.add(comboAudioCodec2);
			comboAudioBitrate2.setLocation(comboAudioCodec2.getX() + comboAudioCodec2.getWidth(), comboAudioCodec2.getLocation().y);
			grpSetAudio.add(comboAudioBitrate2);
			comboAudioRate2.setLocation(comboAudioBitrate2.getX() + comboAudioBitrate2.getWidth(), comboAudioBitrate2.getLocation().y);
			grpSetAudio.add(comboAudioRate2);			
			comboLanguage2.setLocation(comboAudioRate2.getX() + comboAudioRate2.getWidth(), comboAudioRate2.getLocation().y);	
			grpSetAudio.add(comboLanguage2);
			
			lblAudio3.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
			comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 2, lblAudio3.getLocation().y + 1);
			comboAudioCodec3.setLocation(comboAudio3.getX() + comboAudio3.getWidth(), comboAudio3.getLocation().y);
			grpSetAudio.add(comboAudioCodec3);
			comboAudioBitrate3.setLocation(comboAudioCodec3.getX() + comboAudioCodec3.getWidth(), comboAudioCodec3.getLocation().y);
			grpSetAudio.add(comboAudioBitrate3);
			comboAudioRate3.setLocation(comboAudioBitrate3.getX() + comboAudioBitrate3.getWidth(), comboAudioBitrate3.getLocation().y);
			grpSetAudio.add(comboAudioRate3);
			comboLanguage3.setLocation(comboAudioRate3.getX() + comboAudioRate3.getWidth(), comboAudioRate3.getLocation().y);	
			grpSetAudio.add(comboLanguage3);
			
			lblAudio4.setLocation(lblAudio3.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
			comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 2, lblAudio4.getLocation().y + 1);
			comboAudioCodec4.setLocation(comboAudio4.getX() + comboAudio4.getWidth(), comboAudio4.getLocation().y);
			grpSetAudio.add(comboAudioCodec4);
			comboAudioBitrate4.setLocation(comboAudioCodec4.getX() + comboAudioCodec4.getWidth(), comboAudioCodec4.getLocation().y);
			grpSetAudio.add(comboAudioBitrate4);
			comboAudioRate4.setLocation(comboAudioBitrate4.getX() + comboAudioBitrate4.getWidth(), comboAudioBitrate4.getLocation().y);
			grpSetAudio.add(comboAudioRate4);
			comboLanguage4.setLocation(comboAudioRate4.getX() + comboAudioRate4.getWidth(), comboAudioRate4.getLocation().y);	
			grpSetAudio.add(comboLanguage4);
			
			lblAudio5.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
			comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 2, lblAudio5.getLocation().y + 1);
			comboAudioCodec5.setLocation(comboAudio5.getX() + comboAudio5.getWidth(), comboAudio5.getLocation().y);
			grpSetAudio.add(comboAudioCodec5);
			comboAudioBitrate5.setLocation(comboAudioCodec5.getX() + comboAudioCodec5.getWidth(), comboAudioCodec5.getLocation().y);
			grpSetAudio.add(comboAudioBitrate5);
			comboAudioRate5.setLocation(comboAudioBitrate5.getX() + comboAudioBitrate5.getWidth(), comboAudioBitrate5.getLocation().y);
			grpSetAudio.add(comboAudioRate5);
			comboLanguage5.setLocation(comboAudioRate5.getX() + comboAudioRate5.getWidth(), comboAudioRate5.getLocation().y);
			grpSetAudio.add(comboLanguage5);
			
			lblAudio6.setLocation(lblAudio5.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
			comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 2, lblAudio6.getLocation().y + 1);
			comboAudioCodec6.setLocation(comboAudio6.getX() + comboAudio6.getWidth(), comboAudio6.getLocation().y);
			grpSetAudio.add(comboAudioCodec6);
			comboAudioBitrate6.setLocation(comboAudioCodec6.getX() + comboAudioCodec6.getWidth(), comboAudioCodec6.getLocation().y);
			grpSetAudio.add(comboAudioBitrate6);
			comboAudioRate6.setLocation(comboAudioBitrate6.getX() + comboAudioBitrate6.getWidth(), comboAudioBitrate6.getLocation().y);
			grpSetAudio.add(comboAudioRate6);
			comboLanguage6.setLocation(comboAudioRate6.getX() + comboAudioRate6.getWidth(), comboAudioRate6.getLocation().y);
			grpSetAudio.add(comboLanguage6);
			
			lblAudio7.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
			comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 2, lblAudio7.getLocation().y + 1);
			comboAudioCodec7.setLocation(comboAudio7.getX() + comboAudio7.getWidth(), comboAudio7.getLocation().y);
			grpSetAudio.add(comboAudioCodec7);
			comboAudioBitrate7.setLocation(comboAudioCodec7.getX() + comboAudioCodec7.getWidth(), comboAudioCodec7.getLocation().y);
			grpSetAudio.add(comboAudioBitrate7);
			comboAudioRate7.setLocation(comboAudioBitrate7.getX() + comboAudioBitrate7.getWidth(), comboAudioBitrate7.getLocation().y);
			grpSetAudio.add(comboAudioRate7);
			comboLanguage7.setLocation(comboAudioRate7.getX() + comboAudioRate7.getWidth(), comboAudioRate7.getLocation().y);
			grpSetAudio.add(comboLanguage7);
			
			lblAudio8.setLocation(lblAudio7.getX(), lblAudio7.getLocation().y + lblAudio7.getHeight() + 2);
			comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 2, lblAudio8.getLocation().y + 1);
			comboAudioCodec8.setLocation(comboAudio8.getX() + comboAudio8.getWidth(), comboAudio8.getLocation().y);
			grpSetAudio.add(comboAudioCodec8);
			comboAudioBitrate8.setLocation(comboAudioCodec8.getX() + comboAudioCodec8.getWidth(), comboAudioCodec8.getLocation().y);
			grpSetAudio.add(comboAudioBitrate8);
			comboAudioRate8.setLocation(comboAudioBitrate8.getX() + comboAudioBitrate8.getWidth(), comboAudioBitrate8.getLocation().y);
			grpSetAudio.add(comboAudioRate8);
			comboLanguage8.setLocation(comboAudioRate8.getX() + comboAudioRate8.getWidth(), comboAudioRate8.getLocation().y);
			grpSetAudio.add(comboLanguage8);
					
			int itemsCount = 0;
			if (language.getProperty("functionRewrap").equals(comboFonctions.getSelectedItem().toString())
			|| language.getProperty("functionCut").equals(comboFonctions.getSelectedItem().toString())
			|| language.getProperty("functionMerge").equals(comboFonctions.getSelectedItem().toString()))
			{
				itemsCount = 1;
			}
			
			//comboAudioCodec Model
			if (comboAudioCodec1.getModel().getSize() != comboAudioCodec.getModel().getSize() - (2 - itemsCount))
			{				
				for (Component c : grpSetAudio.getComponents())
				{
					if (c instanceof JComboBox && ((JComboBox) c).getName().matches("comboAudioCodec[0-9]+"))
					{
						((JComboBox) c).removeAllItems();
					}
				}
			
				for (int i = 0 ; i < comboAudioCodec.getModel().getSize() - (3 - itemsCount) ; i++)
				{
					for (Component c : grpSetAudio.getComponents())
					{
						if (c instanceof JComboBox && ((JComboBox) c).getName().matches("comboAudioCodec[0-9]+"))
						{
							((JComboBox) c).addItem(comboAudioCodec.getModel().getElementAt(i));
						}
					}
				}	
				
				for (Component c : grpSetAudio.getComponents())
				{
					if (c instanceof JComboBox && ((JComboBox) c).getName().matches("comboAudioCodec[0-9]+"))
					{
						((JComboBox) c).addItem(language.getProperty("codecCopy"));
					}
				}
			}
						
			//comboAudioBitrate Model
			if (comboAudioBitrate1.getModel().getSize() != comboAudioBitrate.getModel().getSize() && comboAudioBitrate.getSelectedItem().equals(language.getProperty("custom").toLowerCase()) == false || grpBitrate.isVisible() == false)
			{												
				for (Component c : grpSetAudio.getComponents())
				{
					if (c instanceof JComboBox && ((JComboBox) c).getName().matches("comboAudioCodec[0-9]+"))
					{	
						for (Component c2 : grpSetAudio.getComponents())
						{								
							if (c2 instanceof JComboBox && ((JComboBox) c2).getName().matches("comboAudioBitrate[0-9]+") && ((JComboBox) c2).getName().replace("comboAudioBitrate", "comboAudioCodec").equals(c.getName()))
							{																		
								if (((JComboBox) c).getSelectedItem().toString().equals("FLAC") && ((JComboBox) c2).getModel().getSize() != 13)
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
									((JComboBox) c2).setSelectedIndex(5);
								}
								else if (((JComboBox) c).getSelectedItem().toString().contains("PCM") || ((JComboBox) c).getSelectedItem().toString().contains("ALAC"))
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(new String[] { "1536" }));
									((JComboBox) c2).setSelectedIndex(0);
								}
								else if (((JComboBox) c).getSelectedItem().toString().equals(language.getProperty("noAudio")) || ((JComboBox) c).getSelectedItem().toString().equals(language.getProperty("codecCopy")))
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(new String[] { "0" }));	
									((JComboBox) c2).setSelectedIndex(0);
								}				
								else if ((((JComboBox) c).getSelectedItem().toString().equals("AC3") || ((JComboBox) c).getSelectedItem().toString().equals("Dolby Digital Plus")) && ((JComboBox) c2).getModel().getSize() != 17)
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(audioValues));
									((JComboBox) c2).setSelectedIndex(8);
								}
								else if (((JComboBox) c).getSelectedItem().toString().equals("Opus") && ((JComboBox) c2).getModel().getSize() != 17)
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(audioValues));
									((JComboBox) c2).setSelectedIndex(11);
								}
								else if (((JComboBox) c2).getModel().getSize() != 17)
								{
									((JComboBox) c2).setModel(new DefaultComboBoxModel<String>(audioValues));
									((JComboBox) c2).setSelectedIndex(10);
								}
								
								break;
							}
						}							
					}			
				}				
			}
			
			if (grpBitrate.isVisible())
			{
				if (comboAudioBitrate.getItemAt(comboAudioBitrate.getItemCount() - 1).equals(language.getProperty("custom").toLowerCase()) == false)
				{
					comboAudioBitrate.addItem(language.getProperty("custom").toLowerCase());
				}
				
				comboAudioBitrate.setSelectedIndex(comboAudioBitrate.getItemCount() - 1);
			}
			
			grpSetAudio.repaint();
		}
		else
		{		
			if (caseChangeAudioCodec.isEnabled() == false)			
			{				
				if (lblAudioMapping.getSelectedItem().toString().equals("Mix"))
				{
					grpSetAudio.remove(lblAudio1);
					grpSetAudio.remove(comboAudio1);
					grpSetAudio.remove(lblAudio2);
					grpSetAudio.remove(comboAudio2);
					grpSetAudio.remove(lblAudio3);
					grpSetAudio.remove(comboAudio3);
					grpSetAudio.remove(lblAudio4);
					grpSetAudio.remove(comboAudio4);
					grpSetAudio.remove(lblAudio5);
					grpSetAudio.remove(comboAudio5);
					grpSetAudio.remove(lblAudio6);
					grpSetAudio.remove(comboAudio6);
					grpSetAudio.remove(lblAudio7);
					grpSetAudio.remove(comboAudio7);
					grpSetAudio.remove(lblAudio8);
					grpSetAudio.remove(comboAudio8);
				}
				else if (lblAudioMapping.getSelectedItem().toString().equals("Multi") == false)
				{
					grpSetAudio.remove(lblAudio3);
					grpSetAudio.remove(comboAudio3);
					grpSetAudio.remove(lblAudio4);
					grpSetAudio.remove(comboAudio4);
					grpSetAudio.remove(lblAudio5);
					grpSetAudio.remove(comboAudio5);
					grpSetAudio.remove(lblAudio6);
					grpSetAudio.remove(comboAudio6);
					grpSetAudio.remove(lblAudio7);
					grpSetAudio.remove(comboAudio7);
					grpSetAudio.remove(lblAudio8);
					grpSetAudio.remove(comboAudio8);
				}
			}
			
			if (language.getProperty("functionReplaceAudio").equals(comboFonctions.getSelectedItem().toString())
			|| language.getProperty("functionNormalization").equals(comboFonctions.getSelectedItem().toString()))
			{
				grpSetAudio.remove(lblAudio1);
				grpSetAudio.remove(comboAudio1);
				grpSetAudio.remove(lblAudio2);
				grpSetAudio.remove(comboAudio2);
			}
			
			lblAudio1.setText(language.getProperty("audio") + " 1" + language.getProperty("colon"));
			lblAudio1.setSize(lblAudio1.getPreferredSize().width, lblAudio1.getSize().height);
			lblAudio2.setText(language.getProperty("audio") + " 2" + language.getProperty("colon"));
			lblAudio2.setSize(lblAudio2.getPreferredSize().width, lblAudio2.getSize().height);
			lblAudio3.setText(language.getProperty("audio") + " 3" + language.getProperty("colon"));
			lblAudio3.setSize(lblAudio3.getPreferredSize().width, lblAudio3.getSize().height);
			lblAudio4.setText(language.getProperty("audio") + " 4" + language.getProperty("colon"));
			lblAudio4.setSize(lblAudio4.getPreferredSize().width, lblAudio4.getSize().height);
			lblAudio5.setText(language.getProperty("audio") + " 5" + language.getProperty("colon"));
			lblAudio5.setSize(lblAudio5.getPreferredSize().width, lblAudio5.getSize().height);
			lblAudio6.setText(language.getProperty("audio") + " 6" + language.getProperty("colon"));
			lblAudio6.setSize(lblAudio6.getPreferredSize().width, lblAudio6.getSize().height);
			lblAudio7.setText(language.getProperty("audio") + " 7" + language.getProperty("colon"));
			lblAudio7.setSize(lblAudio7.getPreferredSize().width, lblAudio7.getSize().height);
			lblAudio8.setText(language.getProperty("audio") + " 8" + language.getProperty("colon"));
			lblAudio8.setSize(lblAudio8.getPreferredSize().width, lblAudio8.getSize().height);
			
			comboAudio1.setLocation(lblAudio1.getX() + lblAudio1.getWidth() + 7, comboAudio1.getLocation().y);	
			grpSetAudio.remove(comboAudioCodec1);
			grpSetAudio.remove(comboAudioBitrate1);
			grpSetAudio.remove(comboAudioRate1);
			grpSetAudio.remove(comboLanguage1);
			
			lblAudio2.setLocation(comboAudio1.getX() + comboAudio1.getWidth() + 12, lblAudio1.getLocation().y);
			comboAudio2.setLocation(lblAudio2.getX() + lblAudio2.getWidth() + 7, lblAudio2.getLocation().y + 1);
			grpSetAudio.remove(comboAudioCodec2);
			grpSetAudio.remove(comboAudioBitrate2);
			grpSetAudio.remove(comboAudioRate2);
			grpSetAudio.remove(comboLanguage2);
			
			lblAudio3.setLocation(lblAudio1.getX(), lblAudio1.getLocation().y + lblAudio1.getHeight() + 2);
			comboAudio3.setLocation(lblAudio3.getX() + lblAudio3.getWidth() + 7, lblAudio3.getLocation().y + 1);	
			grpSetAudio.remove(comboAudioCodec3);
			grpSetAudio.remove(comboAudioBitrate3);
			grpSetAudio.remove(comboAudioRate3);
			grpSetAudio.remove(comboLanguage3);
			
			lblAudio4.setLocation(lblAudio2.getX(), lblAudio2.getLocation().y + lblAudio2.getHeight() + 2);
			comboAudio4.setLocation(lblAudio4.getX() + lblAudio4.getWidth() + 7, lblAudio4.getLocation().y + 1);	
			grpSetAudio.remove(comboAudioCodec4);
			grpSetAudio.remove(comboAudioBitrate4);
			grpSetAudio.remove(comboAudioRate4);
			grpSetAudio.remove(comboLanguage4);
			
			lblAudio5.setLocation(lblAudio1.getX(), lblAudio3.getLocation().y + lblAudio3.getHeight() + 2);
			comboAudio5.setLocation(lblAudio5.getX() + lblAudio5.getWidth() + 7, lblAudio5.getLocation().y + 1);
			grpSetAudio.remove(comboAudioCodec5);
			grpSetAudio.remove(comboAudioBitrate5);			
			grpSetAudio.remove(comboAudioRate5);
			grpSetAudio.remove(comboLanguage5);
			
			lblAudio6.setLocation(lblAudio4.getX(), lblAudio4.getLocation().y + lblAudio4.getHeight() + 2);
			comboAudio6.setLocation(lblAudio6.getX() + lblAudio6.getWidth() + 7, lblAudio6.getLocation().y + 1);
			grpSetAudio.remove(comboAudioCodec6);
			grpSetAudio.remove(comboAudioBitrate6);
			grpSetAudio.remove(comboAudioRate6);
			grpSetAudio.remove(comboLanguage6);
			
			lblAudio7.setLocation(lblAudio1.getX(), lblAudio5.getLocation().y + lblAudio5.getHeight() + 2);
			comboAudio7.setLocation(lblAudio7.getX() + lblAudio7.getWidth() + 7, lblAudio7.getLocation().y + 1);
			grpSetAudio.remove(comboAudioCodec7);
			grpSetAudio.remove(comboAudioBitrate7);
			grpSetAudio.remove(comboAudioRate7);
			grpSetAudio.remove(comboLanguage7);	
			
			lblAudio8.setLocation(lblAudio6.getX(), lblAudio6.getLocation().y + lblAudio6.getHeight() + 2);
			comboAudio8.setLocation(lblAudio8.getX() + lblAudio8.getWidth() + 7, lblAudio8.getLocation().y + 1);
			grpSetAudio.remove(comboAudioCodec8);
			grpSetAudio.remove(comboAudioBitrate8);
			grpSetAudio.remove(comboAudioRate8);
			grpSetAudio.remove(comboLanguage8);
			
			grpSetAudio.repaint();
		}
	}
}
