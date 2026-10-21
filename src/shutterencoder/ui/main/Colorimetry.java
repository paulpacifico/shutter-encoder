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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.utils.Utils;

public class Colorimetry extends Main {

	public static void grpColorimetry() {

		grpColorimetry = new CollapsiblePanel(language.getProperty("grpColorimetry"), true);
		grpColorimetry.setLayout(null);
		grpColorimetry.setVisible(false);
		grpColorimetry.setOpaque(false);
		grpColorimetry.setBackground(Utils.c30);
		grpColorimetry.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpColorimetry);

		grpColorimetry.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpColorimetry, 165);
			}

		});

		caseGamma = new JCheckBox(language.getProperty("caseGamma"));
		caseGamma.setName("caseGamma");
		caseGamma.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseGamma.setBounds(7, 16, caseGamma.getPreferredSize().width, 22);
		grpColorimetry.add(caseGamma);

		caseGamma.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseGamma.isSelected()) {
					comboGamma.setEnabled(true);
				} else {
					comboGamma.setEnabled(false);
				}

				// VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use
				// VideoPlayer.resizeAll and reload the frame
			}

		});

		comboGamma = new JComboBox<Object>(new String[] { "1.8", "2.0", "2.2", "2.4", "2.6" });
		comboGamma.setName("comboGamma");
		comboGamma.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboGamma.setEditable(false);
		comboGamma.setEnabled(false);
		comboGamma.setSelectedIndex(3);
		comboGamma.setMaximumRowCount(20);
		comboGamma.setBounds(grpColorimetry.getWidth() - 130 - 7, caseGamma.getLocation().y + 4, 42, 16);
		grpColorimetry.add(comboGamma);

		comboGamma.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				// VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use
				// VideoPlayer.resizeAll and reload the frame
			}

		});

		caseLevels = new JCheckBox(language.getProperty("caseLevels"));
		caseLevels.setName("caseLevels");
		caseLevels.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseLevels.setBounds(7, caseGamma.getLocation().y + caseGamma.getHeight(), caseLevels.getPreferredSize().width,
				22);
		grpColorimetry.add(caseLevels);

		caseLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseLevels.isSelected()) {
					comboInLevels.setEnabled(true);
					comboOutLevels.setEnabled(true);
				} else {
					comboInLevels.setEnabled(false);
					comboOutLevels.setEnabled(false);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		comboInLevels = new JComboBox<Object>(new String[] { "16-235", "0-255" });
		comboInLevels.setName("comboInLevels");
		comboInLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboInLevels.setEditable(false);
		comboInLevels.setEnabled(false);
		comboInLevels.setSelectedIndex(0);
		comboInLevels.setMaximumRowCount(20);
		comboInLevels.setBounds(grpColorimetry.getWidth() - 150 - 7, caseLevels.getLocation().y + 4, 62, 16);
		grpColorimetry.add(comboInLevels);

		comboInLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboInLevels.getSelectedIndex() == 1)
					comboOutLevels.setSelectedIndex(0);
				else
					comboOutLevels.setSelectedIndex(1);

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		JLabel lblTo = new JLabel(">");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblTo.setBounds(comboInLevels.getX() + comboInLevels.getWidth() + 5, caseLevels.getLocation().y + 4,
				lblTo.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo);

		comboOutLevels = new JComboBox<Object>(new String[] { "16-235", "0-255" });
		comboOutLevels.setName("comboOutLevels");
		comboOutLevels.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutLevels.setEditable(false);
		comboOutLevels.setEnabled(false);
		comboOutLevels.setSelectedIndex(1);
		comboOutLevels.setMaximumRowCount(20);
		comboOutLevels.setBounds(lblTo.getX() + lblTo.getWidth() + 4, caseLevels.getLocation().y + 4,
				comboInLevels.getWidth(), 16);
		grpColorimetry.add(comboOutLevels);

		comboOutLevels.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboOutLevels.getSelectedIndex() == 1)
					comboInLevels.setSelectedIndex(0);
				else
					comboInLevels.setSelectedIndex(1);

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		caseColormatrix = new JCheckBox(language.getProperty("caseColormatrix"));
		caseColormatrix.setName("caseColormatrix");
		caseColormatrix.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseColormatrix.setBounds(7, caseLevels.getLocation().y + caseLevels.getHeight(),
				caseColormatrix.getPreferredSize().width, 22);
		grpColorimetry.add(caseColormatrix);

		caseColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseColormatrix.isSelected()) {
					comboInColormatrix.setEnabled(true);
					comboOutColormatrix.setEnabled(true);
				} else {
					comboInColormatrix.setEnabled(false);
					comboOutColormatrix.setEnabled(false);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		comboInColormatrix = new JComboBox<Object>(new String[] { "Rec. 601", "Rec. 709", "Rec. 2020", "Linear", "HDR" });
		comboInColormatrix.setName("comboInColormatrix");
		comboInColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboInColormatrix.setEditable(false);
		comboInColormatrix.setEnabled(false);
		comboInColormatrix.setSelectedIndex(0);
		comboInColormatrix.setMaximumRowCount(20);
		comboInColormatrix.setBounds(grpColorimetry.getWidth() - 160 - 7, caseColormatrix.getLocation().y + 4, 72, 16);
		grpColorimetry.add(comboInColormatrix);

		comboInColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 601")) {
					comboOutColormatrix.setModel(
							new DefaultComboBoxModel<Object>(new String[] { "Rec. 709", "Rec. 2020", "SDR" }));
				} else if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 709")) {
					comboOutColormatrix.setModel(
							new DefaultComboBoxModel<Object>(new String[] { "Rec. 601", "Rec. 2020", "SDR" }));
					comboOutColormatrix.setSelectedIndex(1);
				} else if (comboInColormatrix.getSelectedItem().toString().contentEquals("Rec. 2020")) {
					comboOutColormatrix
							.setModel(new DefaultComboBoxModel<Object>(new String[] { "Rec. 601", "Rec. 709", "SDR" }));
					comboOutColormatrix.setSelectedIndex(1);
				} else if (comboInColormatrix.getSelectedItem().toString().contentEquals("Linear")) {
					comboOutColormatrix
							.setModel(new DefaultComboBoxModel<Object>(new String[] { "Rec. 601", "Rec. 709", "Rec. 2020", "SDR" }));
					comboOutColormatrix.setSelectedIndex(1);
				} else if (comboInColormatrix.getSelectedItem().toString().equals("HDR")) {
					comboOutColormatrix.setModel(new DefaultComboBoxModel<Object>(new String[] { "SDR" }));
					comboOutColormatrix.setSelectedIndex(0);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		JLabel lblTo2 = new JLabel(">");
		lblTo2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo2.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblTo2.setBounds(lblTo.getX(), caseColormatrix.getLocation().y + 2, lblTo2.getPreferredSize().width, 16);
		grpColorimetry.add(lblTo2);

		comboOutColormatrix = new JComboBox<Object>(new String[] { "Rec. 601", "Rec. 709", "Rec. 2020", "SDR" });
		comboOutColormatrix.setName("comboOutColormatrix");
		comboOutColormatrix.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboOutColormatrix.setEditable(false);
		comboOutColormatrix.setEnabled(false);
		comboOutColormatrix.setSelectedIndex(1);
		comboOutColormatrix.setMaximumRowCount(20);
		comboOutColormatrix.setBounds(grpColorimetry.getWidth() - 70 - 9, caseColormatrix.getLocation().y + 4,
				comboInColormatrix.getWidth(), 16);
		grpColorimetry.add(comboOutColormatrix);

		comboOutColormatrix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboOutColormatrix.getSelectedItem().toString().equals("SDR")) {
					comboInColormatrix.setSelectedIndex(4);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame
			}

		});

		caseColorspace = new JCheckBox(language.getProperty("caseColorspace"));
		caseColorspace.setName("caseColorspace");
		caseColorspace.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseColorspace.setBounds(7, caseColormatrix.getLocation().y + caseColormatrix.getHeight(),
				caseColorspace.getPreferredSize().width, 22);
		grpColorimetry.add(caseColorspace);

		caseColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseColorspace.isSelected())
				{
					comboColorspace.setEnabled(true);

					if (comboFonctions.getSelectedItem().toString().equals("VP9")
							&& (comboColorspace.getSelectedItem().toString().contains("10bits")
									|| comboColorspace.getSelectedItem().toString().contains("12bits")
									|| comboColorspace.getSelectedItem().toString().contains("422"))) {
						caseAlpha.setSelected(false);
						caseAlpha.setEnabled(false);
						comboAlpha.setEnabled(false);
					}

					if (comboColorspace.getSelectedItem().toString().contains("HDR")) {
						comboHDRvalue.setVisible(true);
						lblHDR.setVisible(true);
					} else {
						comboHDRvalue.setVisible(false);
						lblHDR.setVisible(false);
					}
				} else {
					comboColorspace.setEnabled(false);

					if (comboFonctions.getSelectedItem().toString().equals("VP9")
							|| comboFonctions.getSelectedItem().toString().equals("H.265")
									&& (comboAccel.getSelectedItem().equals("OSX VideoToolbox") || comboAccel
											.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))) {
						caseAlpha.setEnabled(true);
					}

					if (comboFonctions.getSelectedItem().toString().equals("VP8")) {
						if (comboFilter.getModel().getSize() != 2) {
							String filtres[] = { ".webm", ".mkv" };
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					} else if (comboFonctions.getSelectedItem().toString().equals("VP9")) {
						if (comboFilter.getModel().getSize() != 3) {
							String filtres[] = { ".webm", ".mkv", ".mp4" };
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					} else if (comboFonctions.getSelectedItem().toString().equals("AV1")) {
						if (comboFilter.getModel().getSize() != 3) {
							String filtres[] = { ".mp4", ".mkv", ".webm" };
							final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							comboFilter.setModel(model);
							comboFilter.setSelectedIndex(0);
						}
					} else if (comboFonctions.getSelectedItem().toString().equals("H.265")) {
						if (comboFilter.getModel().getSize() != 9) {
							String filtres[] = { ".mp4", ".mov", ".mkv", ".avi", ".flv", ".f4v", ".mpg", ".ts",
									".m2ts" };
							DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(filtres);
							if (model.getElementAt(0).equals(comboFilter.getModel().getElementAt(0)) == false) {
								comboFilter.setModel(model);
								comboFilter.setSelectedIndex(0);
							}
						}
					}

					comboHDRvalue.setVisible(false);
					lblHDR.setVisible(false);
				}

				FFPROBE.setFilesize();
				
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame
			}

		});

		comboColorspace = new JComboBox<Object>(new String[] { "Rec. 709", "Rec. 2020 PQ", "Rec. 2020 HLG" });
		comboColorspace.setName("comboColorspace");
		comboColorspace.setFont(new Font("Free Sans", Font.PLAIN, 10));
		comboColorspace.setEditable(false);
		comboColorspace.setEnabled(false);
		comboColorspace.setSelectedIndex(0);
		comboColorspace.setMaximumRowCount(20);
		comboColorspace.setBounds(grpColorimetry.getWidth() - 160 - 7, caseColorspace.getLocation().y + 4, 160, 16);
		grpColorimetry.add(comboColorspace);

		comboColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the frame
			}
			
		});
		
		comboHDRvalue = new JComboBox<String>(new String[] { "auto", "400 nits", "500 nits", "600 nits", "1000 nits",
				"1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits" });
		comboHDRvalue.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboHDRvalue.setEditable(true);
		comboHDRvalue.setVisible(false);
		comboHDRvalue.setSelectedIndex(0);
		comboHDRvalue.setBounds(comboColorspace.getX() + comboColorspace.getWidth() - 80,
				comboColorspace.getLocation().y + comboColorspace.getHeight() + 2, 80, 16);
		comboHDRvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboHDRvalue);

		comboCLLvalue = new JComboBox<String>(new String[] { "auto", "400 nits", "500 nits", "600 nits", "1000 nits",
				"1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits" });
		comboCLLvalue.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboCLLvalue.setEditable(true);
		comboCLLvalue.setVisible(false);
		comboCLLvalue.setSelectedIndex(0);
		comboCLLvalue.setBounds(comboHDRvalue.getX(), comboHDRvalue.getLocation().y + comboHDRvalue.getHeight() + 2, 80,
				16);
		comboCLLvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboCLLvalue);

		JLabel lblMaxCLL = new JLabel("MaxCLL" + language.getProperty("colon"));
		lblMaxCLL.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblMaxCLL.setBounds(comboCLLvalue.getX() - lblMaxCLL.getPreferredSize().width - 4, comboCLLvalue.getY(),
				lblMaxCLL.getPreferredSize().width, lblMaxCLL.getPreferredSize().height);
		lblMaxCLL.setVisible(false);
		grpColorimetry.add(lblMaxCLL);

		comboFALLvalue = new JComboBox<String>(new String[] { "auto", "400 nits", "500 nits", "600 nits", "1000 nits",
				"1400 nits", "2000 nits", "4000 nits", "6000 nits", "8000 nits", "10000 nits" });
		comboFALLvalue.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboFALLvalue.setEditable(true);
		comboFALLvalue.setVisible(false);
		comboFALLvalue.setSelectedIndex(0);
		comboFALLvalue.setBounds(comboHDRvalue.getX(), comboCLLvalue.getLocation().y + comboCLLvalue.getHeight() + 2,
				80, 16);
		comboFALLvalue.setMaximumRowCount(10);
		grpColorimetry.add(comboFALLvalue);

		JLabel lblMaxFALL = new JLabel("MaxFALL" + language.getProperty("colon"));
		lblMaxFALL.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblMaxFALL.setBounds(comboFALLvalue.getX() - lblMaxFALL.getPreferredSize().width - 4, comboFALLvalue.getY(),
				lblMaxFALL.getPreferredSize().width, lblMaxFALL.getPreferredSize().height);
		lblMaxFALL.setVisible(false);
		grpColorimetry.add(lblMaxFALL);

		lblHDR = new JLabel("luminance" + language.getProperty("colon"));
		lblHDR.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblHDR.setBounds(comboHDRvalue.getX() - lblHDR.getPreferredSize().width - 4, comboHDRvalue.getY(),
				lblHDR.getPreferredSize().width, lblHDR.getPreferredSize().height);
		lblHDR.setVisible(false);
		grpColorimetry.add(lblHDR);

		lblHDR.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				lblMaxCLL.setVisible(true);
				comboCLLvalue.setVisible(true);
				lblMaxFALL.setVisible(true);
				comboFALLvalue.setVisible(true);

				btnLUTs.setVisible(false);
				caseLUTs.setVisible(false);
				comboLUTs.setVisible(false);
			}

			public void componentHidden(ComponentEvent e) {
				lblMaxCLL.setVisible(false);
				comboCLLvalue.setVisible(false);
				lblMaxFALL.setVisible(false);
				comboFALLvalue.setVisible(false);

				btnLUTs.setVisible(true);
				caseLUTs.setVisible(true);
				comboLUTs.setVisible(true);
			}
		});

		comboColorspace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboFonctions.getSelectedItem().toString().equals("VP9")
						&& (comboColorspace.getSelectedItem().toString().contains("10bits")
								|| comboColorspace.getSelectedItem().toString().contains("12bits")
								|| comboColorspace.getSelectedItem().toString().contains("422"))) {
					caseAlpha.setSelected(false);
					caseAlpha.setEnabled(false);
					comboAlpha.setEnabled(false);
				} else if (comboFonctions.getSelectedItem().toString().equals("VP9") || comboFonctions.getSelectedItem()
						.toString().equals("H.265")
						&& (comboAccel.getSelectedItem().equals("OSX VideoToolbox")
								|| comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()))) {
					caseAlpha.setEnabled(true);
				}

				if (comboColorspace.getSelectedItem().toString().contains("HDR")) {
					comboHDRvalue.setVisible(true);
					lblHDR.setVisible(true);
				} else {
					comboHDRvalue.setVisible(false);
					lblHDR.setVisible(false);
				}

				FFPROBE.setFilesize();
			}

		});

		caseLUTs = new JCheckBox(language.getProperty("caseLUTs"));
		caseLUTs.setName("caseLUTs");
		caseLUTs.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseLUTs.setBounds(7, caseColorspace.getLocation().y + caseColorspace.getHeight(),
				caseLUTs.getPreferredSize().width, 22);
		grpColorimetry.add(caseLUTs);

		ArrayList<Object> LUTs = new ArrayList<Object>();

		String PathToLUTs = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			PathToLUTs = PathToLUTs.substring(0, PathToLUTs.length() - 1);
		else
			PathToLUTs = PathToLUTs.substring(1, PathToLUTs.length() - 1);

		PathToLUTs = PathToLUTs.substring(0, (int) (PathToLUTs.lastIndexOf("/"))).replace("%20", " ") + "/LUTs";
		lutsFolder = new File(PathToLUTs);

		if (lutsFolder.exists() == false)
			lutsFolder.mkdir();
		else {
			File[] luts = lutsFolder.listFiles();
			String[] data = new String[luts.length];

			for (int i = 0; i < luts.length; i++) {
				data[i] = (String) luts[i].toString();
			}

			Arrays.sort(data);
			LUTs.clear();

			for (int i = 0; i < data.length; i++)
			{
				if (new File(data[i].toString()).isHidden() == false)
				{
					LUTs.add(new File(data[i].toString()).getName());
				}
			}
		}

		btnLUTs = new JButton(language.getProperty("btnManage"));
		btnLUTs.setFont(new Font(boldFont, Font.PLAIN, 12));
		btnLUTs.setBackground(Utils.c42);
		btnLUTs.setBounds(comboColorspace.getX(), caseLUTs.getY() + 3, grpColorimetry.getWidth() - comboColorspace.getX() - 6, 21);
		grpColorimetry.add(btnLUTs);

		btnLUTs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Utils.openFile(lutsFolder);

				if (caseLUTs.isSelected())
					caseLUTs.doClick();
			}

		});

		comboLUTs = new JComboBox<Object>(LUTs.toArray());
		comboLUTs.setName("comboLUTs");
		comboLUTs.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLUTs.setEditable(false);
		comboLUTs.setEnabled(false);
		comboLUTs.setMaximumRowCount(20);
		comboLUTs.setBounds(7, caseLUTs.getLocation().y + caseLUTs.getHeight() + 7, grpColorimetry.getWidth() - 14, 22);
		grpColorimetry.add(comboLUTs);

		comboLUTs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		caseLUTs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int index = comboLUTs.getSelectedIndex();

				File[] luts = lutsFolder.listFiles();
				String[] data = new String[luts.length];

				for (int i = 0; i < luts.length; i++) {
					data[i] = (String) luts[i].toString();
				}

				Arrays.sort(data);
				LUTs.clear();

				if (caseLUTs.isSelected()) {
					for (int i = 0; i < data.length; i++) {
						File lut = new File(data[i].toString());

						if (lut.isHidden() == false)
						{
							if (lut.getName().contains(" ")) {
								File newLutName = new File(lutsFolder + "/" + lut.getName().replace(" ", "-"));
								lut.renameTo(newLutName);

								if (newLutName.exists())
									LUTs.add(newLutName.getName());
								else
									LUTs.add(lut.getName());
							} else
								LUTs.add(lut.getName());
						}
					}

					comboLUTs.setModel(new DefaultComboBoxModel<Object>(LUTs.toArray()));
					comboLUTs.setEnabled(true);
					comboLUTs.setSelectedIndex(index);
				} else {
					comboLUTs.setEnabled(false);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});
	}
}
