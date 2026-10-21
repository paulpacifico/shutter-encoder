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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Advanced extends Main {

	public static void grpAdvanced() {

		grpAdvanced = new CollapsiblePanel(language.getProperty("grpAdvanced"), true);
		grpAdvanced.setLayout(null);
		grpAdvanced.setOpaque(false);
		grpAdvanced.setVisible(false);
		grpAdvanced.setBackground(Utils.c30);
		grpAdvanced.setBounds(frame.getWidth(), 396, 312, 17);
		frame.getContentPane().add(grpAdvanced);

		grpAdvanced.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				int size = 25;
				for (Component c : grpAdvanced.getComponents()) {
					if (c instanceof JCheckBox)
						size += 17;
				}

				UIController.extendSections(grpAdvanced, size);
			}

		});

		caseOpenGop = new JCheckBox(Main.language.getProperty("btnOpenGOP"));
		caseOpenGop.setName("caseOpenGop");
		caseOpenGop.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseOpenGop.setSize(caseOpenGop.getPreferredSize().width, 23);

		caseForcerProgressif = new JCheckBox(language.getProperty("caseForcerProgressif"));
		caseForcerProgressif.setName("caseForcerProgressif");
		caseForcerProgressif.setToolTipText(language.getProperty("tooltipProgressif"));
		caseForcerProgressif.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcerProgressif.setSize(caseForcerProgressif.getPreferredSize().width + 4, 23);

		caseForcerEntrelacement = new JCheckBox(language.getProperty("caseForcerEntrelacement"));
		caseForcerEntrelacement.setName("caseForcerEntrelacement");
		caseForcerEntrelacement.setToolTipText(language.getProperty("tooltipEntrelacement"));
		caseForcerEntrelacement.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcerEntrelacement.setSize(caseForcerEntrelacement.getPreferredSize().width + 4, 23);

		caseForcerInversion = new JCheckBox(language.getProperty("caseForcerInversion"));
		caseForcerInversion.setName("caseForcerInversion");
		caseForcerInversion.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcerInversion.setSize(caseForcerInversion.getPreferredSize().width, 23);

		caseForcerDesentrelacement = new JCheckBox(language.getProperty("caseForcerDesentrelacement"));
		caseForcerDesentrelacement.setName("caseForcerDesentrelacement");
		caseForcerDesentrelacement.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcerDesentrelacement.setSize(caseForcerDesentrelacement.getPreferredSize().width, 23);

		caseForcerDesentrelacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (lblTFF.getText().equals("TFF"))
					FFPROBE.fieldOrder = "0";
				else if (lblTFF.getText().equals("BFF"))
					FFPROBE.fieldOrder = "1";
				else
					FFPROBE.fieldOrder = "0";

				if (caseForcerDesentrelacement.isSelected()) {
					comboForcerDesentrelacement.setEnabled(true);
				} else {
					comboForcerDesentrelacement.setSelectedItem("bwdif");
					comboForcerDesentrelacement.setEnabled(false);
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
				VideoPlayerUtils.setInfo();
			}
		});

		comboForcerDesentrelacement = new JComboBox<String>();
		comboForcerDesentrelacement.setName("comboForcerDesentrelacement");
		comboForcerDesentrelacement.setModel(new DefaultComboBoxModel<String>(new String[] { "bwdif", "yadif", "estdif", "w3fdif", "detelecine" }));
		comboForcerDesentrelacement.setSelectedIndex(0);
		comboForcerDesentrelacement.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForcerDesentrelacement.setEditable(false);
		comboForcerDesentrelacement.setSize(78, 18);

		comboForcerDesentrelacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") && lblTFF.getText().contains("x2"))
				{
					lblTFF.setText("TFF");
					
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}

				if (comboForcerDesentrelacement.getModel().getSize() == 2 && comboForcerDesentrelacement.getModel().getElementAt(1).equals("advanced"))
				{
					lblTFF.setEnabled(false);
				}
				else
					lblTFF.setEnabled(true);
					
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		lblTFF = new JLabel("TFF");
		lblTFF.setName("lblTFF");
		lblTFF.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblTFF.setBackground(Utils.c42);
		lblTFF.setHorizontalAlignment(SwingConstants.CENTER);
		lblTFF.setOpaque(true);
		lblTFF.setFont(new Font(boldFont, Font.PLAIN, 12));
		lblTFF.setSize(32, 16);

		lblTFF.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (lblTFF.getText().equals("TFF")) {
					lblTFF.setText("BFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "1";
				} else if (lblTFF.getText().equals("BFF")
						&& comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") == false) {
					lblTFF.setText("x2 T");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				} else if (lblTFF.getText().equals("x2 T")
						&& comboForcerDesentrelacement.getSelectedItem().toString().equals("detelecine") == false) {
					lblTFF.setText("x2 B");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				} else {
					lblTFF.setText("TFF");
					if (caseForcerDesentrelacement.isSelected())
						FFPROBE.fieldOrder = "0";
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
				VideoPlayerUtils.setInfo();
			}

			@Override
			public void mousePressed(MouseEvent e) {
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

		caseCreateTree = new JCheckBox(language.getProperty("caseCreateTree"));
		caseCreateTree.setName("caseCreateTree");
		caseCreateTree.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseCreateTree.setSize(caseCreateTree.getPreferredSize().width, 23);

		caseCreateTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseCreateTree.isSelected()) {
					UIController.setDestinationTabs(2);
					comboCreateTree.setEnabled(true);
				} else {
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));

					if (caseCreateOPATOM.isSelected() == false) {
						if (comboFonctions.getSelectedItem().toString().equals("H.264")) {
							UIController.setDestinationTabs(6);
						} else
							UIController.setDestinationTabs(5);
					}

					comboCreateTree.setEnabled(false);
				}
			}
		});

		comboCreateTree = new JComboBox<String>();
		comboCreateTree.setName("comboCreateTree");
		comboCreateTree.setEnabled(false);
		comboCreateTree.setMaximumRowCount(15);

		String[] levels = new String[11];
		for (int i = 0; i < 11; i++) {
			levels[i] = String.valueOf(i);
		}

		comboCreateTree.setModel(new DefaultComboBoxModel<String>(levels));
		comboCreateTree.setSelectedIndex(0);
		comboCreateTree.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboCreateTree.setEditable(false);
		comboCreateTree.setSize(35, 16);

		casePreserveMetadata = new JCheckBox(language.getProperty("casePreserveMetadata"));
		casePreserveMetadata.setName("casePreserveMetadata");
		casePreserveMetadata.setFont(new Font(mainFont, Font.PLAIN, 12));
		casePreserveMetadata.setSize(casePreserveMetadata.getPreferredSize().width, 23);

		casePreserveSubs = new JCheckBox(language.getProperty("casePreserveSubs"));
		casePreserveSubs.setName("casePreserveSubs");
		casePreserveSubs.setFont(new Font(mainFont, Font.PLAIN, 12));
		casePreserveSubs.setSize(casePreserveSubs.getPreferredSize().width, 23);

		casePreserveSubs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (casePreserveSubs.isSelected()
						&& (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))
								|| comboFonctions.getSelectedItem().toString()
										.equals(language.getProperty("functionCut"))
								|| subtitlesBurn == false)) {
					caseAddSubtitles.setSelected(false);
				}
			}

		});

		caseCreateOPATOM = new JCheckBox(language.getProperty("caseCreateOPATOM"));
		caseCreateOPATOM.setName("caseCreateOPATOM");
		caseCreateOPATOM.setToolTipText(language.getProperty("tooltipCreateOpatom"));
		caseCreateOPATOM.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseCreateOPATOM.setSize((int) caseCreateOPATOM.getPreferredSize().getWidth(), 23);

		caseCreateOPATOM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseCreateOPATOM.isSelected()) {
					UIController.setDestinationTabs(2);

					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))) {
						comboFilter.setSelectedItem(".mxf");
					}

					if (caseAS10.isSelected()) {
						caseAS10.doClick();
					}
				} else {
					if (caseChangeFolder1.isSelected() == false)
						lblDestination1.setText(language.getProperty("sameAsSource"));

					if (caseCreateTree.isSelected() == false) {
						UIController.setDestinationTabs(5);
					}

					if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap"))) {
						comboFilter.setSelectedItem(language.getProperty("aucun"));
					}
				}
			}
		});

		lblCreateOPATOM = new JLabel(language.getProperty("lblCreateOPATOM"));
		lblCreateOPATOM.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblCreateOPATOM.setBackground(Utils.c30);
		lblCreateOPATOM.setSize((int) lblCreateOPATOM.getPreferredSize().getWidth(), 23);

		lblOPATOM = new JLabel("OP-Atom");
		lblOPATOM.setName("lblOPATOM");
		lblOPATOM.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblOPATOM.setBackground(Utils.c42);
		lblOPATOM.setHorizontalAlignment(SwingConstants.CENTER);
		lblOPATOM.setOpaque(true);
		lblOPATOM.setFont(new Font(boldFont, Font.PLAIN, 12));
		lblOPATOM.setSize(65, 16);

		lblOPATOM.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (lblOPATOM.getText().equals("OP-Atom") && comboFonctions.getSelectedItem().toString()
						.equals(language.getProperty("functionRewrap")) == false) {
					lblOPATOM.setText("OP1a");
				} else
					lblOPATOM.setText("OP-Atom");
			}

			@Override
			public void mousePressed(MouseEvent e) {
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

		caseOPATOM = new JCheckBox(language.getProperty("caseOPATOM"));
		caseOPATOM.setName("caseOPATOM");
		caseOPATOM.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseOPATOM.setSize(caseOPATOM.getPreferredSize().width + 4, 23);

		caseOPATOM.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int m = 0; m < list.getSize(); m++) {
					int s = list.getElementAt(m).toString().lastIndexOf('.');
					if (list.getElementAt(m).toString().substring(s).toLowerCase().equals(".mxf") == false) {
						list.remove(m);
						m = -1;
					}

					String[] data = new String[list.getSize()];

					for (int i = 0; i < list.getSize(); i++) {
						data[i] = (String) list.getElementAt(i);
					}

					Arrays.sort(data);
					list.clear();

					for (int i = 0; i < data.length; i++) {
						list.addElement(data[i].toString());
					}
				}
				lblFiles.setText(Utils.filesNumber());
			}

		});

		caseConform = new JCheckBox(language.getProperty("caseConform"));
		caseConform.setName("caseConform");
		caseConform.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseConform.setSize(caseConform.getPreferredSize().width, 23);
		caseConform.setToolTipText(language.getProperty("tooltipConform"));

		caseConform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseConform.isSelected()) {
					comboConform.setEnabled(true);
					comboFPS.setEnabled(true);
				} else {
					comboConform.setEnabled(false);
					comboFPS.setEnabled(false);
				}

				FFPROBE.setFilesize();

				// VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use
				// VideoPlayer.resizeAll and reload the frame
			}

		});

		caseDecimate = new JCheckBox(language.getProperty("caseDecimate"));
		caseDecimate.setName("caseDecimate");
		caseDecimate.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseDecimate.setSize(caseDecimate.getPreferredSize().width, 23);

		comboConform = new JComboBox<String>();
		comboConform.setName("comboConform");
		comboConform.setEnabled(false);
		comboConform.setModel(new DefaultComboBoxModel<String>(
				new String[] { language.getProperty("conformByReverse"), language.getProperty("conformBySpeed"),
						language.getProperty("conformByDrop"), language.getProperty("conformByBlending"),
						language.getProperty("conformByInterpolation"), language.getProperty("conformBySlowMotion") }));
		comboConform.setSelectedIndex(3);
		comboConform.setMaximumRowCount(20);
		comboConform.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboConform.setEditable(false);
		comboConform.setSize(100, 16);

		comboConform.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * String newFPS = AdvancedFeatures.setFramerate(false); if
				 * (newFPS.contains("/") == false && newFPS.contains("vfr") == false) {
				 * VideoPlayer.inputFramerateMS = (float) (1000 /
				 * Float.parseFloat(newFPS.replace(" -r ", ""))); }
				 * VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use
				 * VideoPlayer.resizeAll and reload the frame
				 */
			}

		});

		lblToConform = new JLabel(">");
		lblToConform.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblToConform.setSize(lblToConform.getPreferredSize().width + 1, 16);

		comboFPS = new JComboBox<String>();
		comboFPS.setName("comboFPS");
		comboFPS.setEnabled(false);
		comboFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,98", "24", "25", "29,97", "30", "48",
				"50", "59,94", "60", "100", "120", "150", "200", "250" }));
		comboFPS.setSelectedIndex(2);
		comboFPS.setMaximumRowCount(20);
		comboFPS.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboFPS.setEditable(true);
		comboFPS.setSize(50, 16);

		comboFPS.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				FFPROBE.setFilesize();
			}

		});

		lblIsConform = new JLabel(Main.language.getProperty("fps"));
		lblIsConform.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblIsConform.setSize(20, 16);
		if (getLanguage.equals(Locale.of("ru").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("uk").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("vi").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("cs").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("id").getDisplayLanguage())
				|| getLanguage.equals(Locale.of("ro").getDisplayLanguage())) {
			lblIsConform.setVisible(false);
		}

		caseForcerProgressif.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerProgressif.isSelected()) {
					caseForcerEntrelacement.setSelected(false);
					caseForcerInversion.setSelected(false);
				} else if (caseForcerDesentrelacement.isSelected()) {
					caseForcerDesentrelacement.setSelected(false);
				}
			}
		});
		caseForcerEntrelacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerEntrelacement.isSelected()) {
					caseForcerDesentrelacement.setSelected(false);
					caseForcerProgressif.setSelected(false);
				} else
					caseForcerInversion.setSelected(false);
			}
		});
		caseForcerInversion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerInversion.isSelected()) {
					caseForcerDesentrelacement.setSelected(false);
					caseForcerProgressif.setSelected(false);
					caseForcerEntrelacement.setSelected(true);
				}
			}
		});
		caseForcerDesentrelacement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseForcerDesentrelacement.isSelected()) {
					caseForcerEntrelacement.setSelected(false);
					caseForcerInversion.setSelected(false);
					caseForcerProgressif.setSelected(true);
				}
			}
		});

		caseForceOutput = new JCheckBox(language.getProperty("caseForceOutput"));
		caseForceOutput.setName("caseForceOutput");
		caseForceOutput.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForceOutput.setSize(caseForceOutput.getPreferredSize().width, 23);

		lblNiveaux = new JLabel("0-255");
		lblNiveaux.setName("lblNiveaux");
		lblNiveaux.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblNiveaux.setBackground(Utils.c42);
		lblNiveaux.setHorizontalAlignment(SwingConstants.CENTER);
		lblNiveaux.setOpaque(true);
		lblNiveaux.setFont(new Font(boldFont, Font.PLAIN, 12));
		lblNiveaux.setSize(45, 16);

		lblNiveaux.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblNiveaux.getText().equals("0-255")) {
					lblNiveaux.setText("16-235");
				} else {
					lblNiveaux.setText("0-255");
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		caseFastStart = new JCheckBox(language.getProperty("caseFastStart"));
		caseFastStart.setName("caseFastStart");
		caseFastStart.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseFastStart.setSize(caseFastStart.getPreferredSize().width, 23);

		caseFastDecode = new JCheckBox(language.getProperty("caseFastDecode"));
		caseFastDecode.setName("caseFastDecode");
		caseFastDecode.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseFastDecode.setSize(caseFastDecode.getPreferredSize().width, 23);
		
		caseFastDecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseFastDecode.isSelected()) {
					comboFastDecode.setEnabled(true);
				} else {
					comboFastDecode.setEnabled(false);
				}
			}

		});

		comboFastDecode = new JComboBox<String>();
		comboFastDecode.setName("comboFastDecode");
		comboFastDecode.setEnabled(false);
		comboFastDecode.setMaximumRowCount(15);
		comboFastDecode.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "2" }));
		comboFastDecode.setSelectedIndex(0);
		comboFastDecode.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboFastDecode.setEditable(false);
		comboFastDecode.setSize(40, 16);
		
		caseVarianceBoost = new JCheckBox(language.getProperty("caseVarianceBoost"));
		caseVarianceBoost.setName("caseVarianceBoost");
		caseVarianceBoost.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseVarianceBoost.setSize(caseVarianceBoost.getPreferredSize().width, 23);
		
		caseVarianceBoost.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseVarianceBoost.isSelected()) {
					comboVarianceBoost.setEnabled(true);
				} else {
					comboVarianceBoost.setEnabled(false);
				}
			}

		});

		comboVarianceBoost = new JComboBox<String>();
		comboVarianceBoost.setName("comboVarianceBoost");
		comboVarianceBoost.setEnabled(false);
		comboVarianceBoost.setMaximumRowCount(15);
		comboVarianceBoost.setModel(new DefaultComboBoxModel<String>(new String[] { "1", "2", "3", "4" }));
		comboVarianceBoost.setSelectedIndex(1);
		comboVarianceBoost.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboVarianceBoost.setEditable(false);
		comboVarianceBoost.setSize(40, 16);
		
		caseAlpha = new JCheckBox(language.getProperty("caseAlpha") + language.getProperty("colon"));
		caseAlpha.setName("caseAlpha");
		caseAlpha.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseAlpha.setSize(caseAlpha.getPreferredSize().width, 23);

		caseAlpha.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAlpha.isSelected()) {
					comboAlpha.setEnabled(true);
				} else {
					comboAlpha.setEnabled(false);
				}
			}

		});
		
		comboAlpha = new JComboBox<String>();
		comboAlpha.setName("comboAlpha");
		comboAlpha.setEnabled(false);
		comboAlpha.setMaximumRowCount(15);
		comboAlpha.setModel(new DefaultComboBoxModel<String>(new String[] { language.getProperty("alphaStraight"), language.getProperty("alphaPremultiply") }));
		comboAlpha.setSelectedIndex(0);
		comboAlpha.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAlpha.setEditable(false);
		comboAlpha.setSize(80, 16);
		
		caseGOP = new JCheckBox(language.getProperty("caseGOP"));
		caseGOP.setName("caseGOP");
		caseGOP.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseGOP.setSize(caseGOP.getPreferredSize().width + 4, 23);

		caseGOP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseGOP.isSelected()) {
					gopSize.setEnabled(true);
				} else {
					gopSize.setEnabled(false);
				}
			}

		});

		gopSize = new JTextField();
		gopSize.setName("gopSize");
		gopSize.setEnabled(false);
		gopSize.setHorizontalAlignment(SwingConstants.CENTER);
		gopSize.setText("250");
		gopSize.setFont(new Font(mainFont, Font.PLAIN, 11));
		gopSize.setColumns(10);
		gopSize.setSize(35, 16);

		gopSize.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (gopSize.getText().length() >= 3)
					gopSize.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		
		caseFilmGrain = new JCheckBox(language.getProperty("caseFilmGrain"));
		caseFilmGrain.setName("caseFilmGrain");
		caseFilmGrain.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseFilmGrain.setSize(caseFilmGrain.getPreferredSize().width + 4, 23);

		caseFilmGrain.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseFilmGrain.isSelected()) {
					comboFilmGrain.setEnabled(true);
				} else {
					comboFilmGrain.setEnabled(false);
				}
			}

		});

		comboFilmGrain = new JComboBox<String>();
		comboFilmGrain.setName("comboFilmGrain");
		comboFilmGrain.setEnabled(false);
		comboFilmGrain.setMaximumRowCount(15);
		String[] grainValues = new String[51];
		for (int i = 0; i < 51; i++) {
			grainValues[i] = String.valueOf(i);
		}
		comboFilmGrain.setModel(new DefaultComboBoxModel<String>(grainValues));
		comboFilmGrain.setSelectedIndex(6);
		comboFilmGrain.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboFilmGrain.setEditable(false);
		comboFilmGrain.setSize(40, 16);

		caseFilmGrainDenoise = new JCheckBox(language.getProperty("caseFilmGrainDenoise"));
		caseFilmGrainDenoise.setName("caseFilmGrainDenoise");
		caseFilmGrainDenoise.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseFilmGrainDenoise.setSize(caseFilmGrainDenoise.getPreferredSize().width + 4, 23);

		caseFilmGrainDenoise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseFilmGrainDenoise.isSelected()) {
					comboFilmGrainDenoise.setEnabled(true);
				} else {
					comboFilmGrainDenoise.setEnabled(false);
				}
			}

		});

		comboFilmGrainDenoise = new JComboBox<String>();
		comboFilmGrainDenoise.setName("comboFilmGrainDenoise");
		comboFilmGrainDenoise.setEnabled(false);
		comboFilmGrainDenoise.setMaximumRowCount(15);
		comboFilmGrainDenoise.setModel(new DefaultComboBoxModel<String>(new String[] { "0", "1" }));
		comboFilmGrainDenoise.setSelectedIndex(1);
		comboFilmGrainDenoise.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboFilmGrainDenoise.setEditable(false);
		comboFilmGrainDenoise.setSize(40, 16);

		caseCABAC = new JCheckBox(language.getProperty("caseCABAC"));
		caseCABAC.setName("caseCABAC");
		caseCABAC.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseCABAC.setSize(caseCABAC.getPreferredSize().width + 4, 23);

		caseForceLevel = new JCheckBox(language.getProperty("caseForceLevel"));
		caseForceLevel.setName("caseForceLevel");
		caseForceLevel.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForceLevel.setSize(caseForceLevel.getPreferredSize().width, 23);

		caseForceLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseForceLevel.isSelected()) {
					comboForceProfile.setEnabled(true);
					comboForceLevel.setEnabled(true);
				} else {
					comboForceProfile.setEnabled(false);
					comboForceLevel.setEnabled(false);
				}
			}

		});

		comboForceLevel = new JComboBox<String>();
		comboForceLevel.setName("comboForceLevel");
		comboForceLevel.setEnabled(false);
		comboForceLevel.setMaximumRowCount(15);
		comboForceLevel.setModel(new DefaultComboBoxModel<String>(new String[] { "1.0", "1.1", "1.2", "1.3", "2.0",
				"2.1", "2.2", "3.0", "3.1", "3.2", "4.0", "4.1", "4.2", "5.0", "5.1", "5.2", "6", "6.1", "6.2" }));
		comboForceLevel.setSelectedIndex(14);
		comboForceLevel.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForceLevel.setEditable(false);
		comboForceLevel.setSize(50, 16);

		comboForceProfile = new JComboBox<String>();
		comboForceProfile.setName("comboForceProfile");
		comboForceProfile.setEnabled(false);
		comboForceProfile.setMaximumRowCount(4);
		comboForceProfile.setModel(new DefaultComboBoxModel<String>(new String[] { "base", "main", "high" }));
		comboForceProfile.setSelectedIndex(2);
		comboForceProfile.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForceProfile.setEditable(false);
		comboForceProfile.setSize(66, 16);

		caseForcePreset = new JCheckBox(language.getProperty("caseForcePreset"));
		caseForcePreset.setName("caseForcePreset");
		caseForcePreset.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForcePreset.setSize(caseForcePreset.getPreferredSize().width, 23);

		caseForcePreset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForcePreset.isSelected()) {
					comboForcePreset.setEnabled(true);
				} else {
					comboForcePreset.setEnabled(false);
				}
			}

		});

		comboForcePreset = new JComboBox<String>();
		comboForcePreset.setName("comboForcePreset");
		comboForcePreset.setEnabled(false);
		comboForcePreset.setMaximumRowCount(15);
		comboForcePreset.setModel(new DefaultComboBoxModel<String>(new String[] { "ultrafast", "superfast", "veryfast",
				"faster", "fast", "medium", "slow", "slower", "veryslow", "placebo" }));
		comboForcePreset.setSelectedIndex(5);
		comboForcePreset.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForcePreset.setEditable(false);
		comboForcePreset.setSize(comboForcePreset.getPreferredSize().width, 16);

		caseForceTune = new JCheckBox(language.getProperty("caseForceTune"));
		caseForceTune.setName("caseForceTune");
		caseForceTune.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForceTune.setSize(caseForceTune.getPreferredSize().width + 4, 23);

		caseForceTune.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceTune.isSelected())
					comboForceTune.setEnabled(true);
				else
					comboForceTune.setEnabled(false);
			}

		});

		comboForceTune = new JComboBox<String>();
		comboForceTune.setName("comboForceTune");
		comboForceTune.setEnabled(false);
		comboForceTune.setMaximumRowCount(15);
		comboForceTune.setModel(new DefaultComboBoxModel<String>(new String[] { "film", "animation", "grain",
				"stillimage", "fastdecode", "zerolatency", "psnr", "ssim" }));
		comboForceTune.setSelectedIndex(0);
		comboForceTune.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForceTune.setEditable(false);
		comboForceTune.setSize(81, 16);

		caseForceQuality = new JCheckBox(language.getProperty("caseForceQuality"));
		caseForceQuality.setName("caseForceQuality");
		caseForceQuality.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForceQuality.setSize(caseForceQuality.getPreferredSize().width, 23);

		caseForceQuality.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceQuality.isSelected())
					comboForceQuality.setEnabled(true);
				else
					comboForceQuality.setEnabled(false);
			}

		});

		comboForceQuality = new JComboBox<String>();
		comboForceQuality.setName("comboForceQuality");
		comboForceQuality.setEnabled(false);
		comboForceQuality.setMaximumRowCount(15);
		comboForceQuality.setModel(new DefaultComboBoxModel<String>(new String[] { "best", "good", "realtime" }));
		comboForceQuality.setSelectedIndex(1);
		comboForceQuality.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForceQuality.setEditable(false);
		comboForceQuality.setSize(comboForcePreset.getPreferredSize().width, 16);

		caseForceSpeed = new JCheckBox(language.getProperty("caseForceSpeed"));
		caseForceSpeed.setName("caseForceSpeed");
		caseForceSpeed.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseForceSpeed.setSize(caseForceSpeed.getPreferredSize().width, 23);

		caseForceSpeed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseForceSpeed.isSelected())
					comboForceSpeed.setEnabled(true);
				else
					comboForceSpeed.setEnabled(false);
			}

		});

		comboForceSpeed = new JComboBox<String>();
		comboForceSpeed.setName("comboForceSpeed");
		comboForceSpeed.setEnabled(false);
		comboForceSpeed.setMaximumRowCount(15);
		comboForceSpeed.setModel(new DefaultComboBoxModel<String>(
				new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "10", "11", "12", "13" }));
		comboForceSpeed.setSelectedIndex(4);
		comboForceSpeed.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboForceSpeed.setEditable(false);
		comboForceSpeed.setSize(40, 16);

		caseEncoderParams = new JCheckBox(language.getProperty("btnAdd") + "x265-params" + language.getProperty("colon"));
		caseEncoderParams.setName("caseEncoderParams");
		caseEncoderParams.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseEncoderParams.setSize(caseEncoderParams.getPreferredSize().width, 23);
		
		caseEncoderParams.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseEncoderParams.isSelected())
					textEncoderParams.setEnabled(true);
				else
					textEncoderParams.setEnabled(false);
			}

		});
		
		textEncoderParams = new JTextField();
		textEncoderParams.setEnabled(false);
		textEncoderParams.setName("textEncoderParams");
		textEncoderParams.setHorizontalAlignment(SwingConstants.LEFT);
		textEncoderParams.setFont(new Font(mainFont, Font.PLAIN, 11));
		textEncoderParams.setSize(grpAdvanced.getWidth() - caseEncoderParams.getWidth() - 28, 16);
		
		caseAS10 = new JCheckBox(language.getProperty("caseAS10"));
		caseAS10.setName("caseAS10");
		caseAS10.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseAS10.setSize(caseAS10.getPreferredSize().width, 23);

		caseAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAS10.isSelected()) {
					if (comboFonctions.getSelectedItem().toString().contains("XDCAM")) {
						final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(
								new String[] { ".mxf" });
						comboFilter.setModel(model);

						if (comboAS10.getSelectedItem().toString().equals("NRK_HD_2012")) {
							comboAudioCodec.setSelectedIndex(0);
						}
					}

					comboAS10.setEnabled(true);
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);
					comboAudio5.setSelectedIndex(4);
					comboAudio6.setSelectedIndex(5);
					comboAudio7.setSelectedIndex(6);
					comboAudio8.setSelectedIndex(7);

					caseCreateOPATOM.setSelected(false);
				} else {
					comboAudioCodec.setSelectedIndex(1);

					comboAS10.setEnabled(false);
					comboAudio1.setSelectedIndex(0);
					comboAudio2.setSelectedIndex(1);
					comboAudio3.setSelectedIndex(2);
					comboAudio4.setSelectedIndex(3);
					comboAudio5.setSelectedIndex(16);
					comboAudio6.setSelectedIndex(16);
					comboAudio7.setSelectedIndex(16);
					comboAudio8.setSelectedIndex(16);

					if (comboFonctions.getSelectedItem().toString().contains("XDCAM")) {
						final DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>(
								new String[] { ".mxf", ".mov" });
						comboFilter.setModel(model);
					}
				}
			}

		});

		comboAS10 = new JComboBox<String>();
		comboAS10.setName("comboAS10");
		comboAS10.setEnabled(false);
		comboAS10.setMaximumRowCount(4);
		comboAS10.setModel(new DefaultComboBoxModel<String>(new String[] { "HIGH_HD_2014", "NRK_HD_2012" }));
		comboAS10.setSelectedIndex(0);
		comboAS10.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboAS10.setEditable(false);
		comboAS10.setSize(129, 16);

		comboAS10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAS10.isSelected() && comboFonctions.getSelectedItem().toString().contains("XDCAM")
						&& comboAS10.getSelectedItem().toString().equals("NRK_HD_2012")) {
					comboAudioCodec.setSelectedIndex(0);
				} else
					comboAudioCodec.setSelectedIndex(1);
			}

		});

		caseChunks = new JCheckBox(language.getProperty("caseChunks"));
		caseChunks.setName("caseChunks");
		caseChunks.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseChunks.setSize(caseChunks.getPreferredSize().width + 4, 23);

		caseChunks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseChunks.isSelected()) {
					chunksSize.setEnabled(true);
				} else {
					chunksSize.setEnabled(false);
				}
			}

		});

		chunksSize = new JComboBox<String>();
		chunksSize.setName("chunksSize");
		chunksSize.setEnabled(false);
		chunksSize.setMaximumRowCount(15);
		String[] values = new String[64];
		for (int i = 0; i < 64; i++) {
			values[i] = String.valueOf(i + 1);
		}
		chunksSize.setModel(new DefaultComboBoxModel<String>(values));
		chunksSize.setSelectedIndex(3);
		chunksSize.setFont(new Font(mainFont, Font.PLAIN, 10));
		chunksSize.setEditable(false);
		chunksSize.setSize(40, 16);

		caseDRC = new JCheckBox(language.getProperty("caseDRC"));
		caseDRC.setName("caseDRC");
		caseDRC.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseDRC.setSize(caseDRC.getPreferredSize().width + 4, 23);

		caseTruePeak = new JCheckBox("True Peak" + language.getProperty("colon"));
		caseTruePeak.setName("caseTruePeak");
		caseTruePeak.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseTruePeak.setSize(caseTruePeak.getPreferredSize().width + 4, 23);

		caseTruePeak.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseTruePeak.isSelected())
					comboTruePeak.setEnabled(true);
				else
					comboTruePeak.setEnabled(false);
			}

		});

		comboTruePeak = new JComboBox<Object>();
		comboTruePeak.setName("comboTruePeak");
		comboTruePeak.setEnabled(false);
		comboTruePeak.setMaximumRowCount(15);

		String truePeakValues[] = new String[10];

		truePeakValues[0] = "0.0 dBFS";
		int i = 1;
		do {
			truePeakValues[i] = ("-" + i + ".0 dBFS");
			i++;
		} while (i < 10);

		final ComboBoxModel<Object> truePeakModel = new DefaultComboBoxModel<Object>(truePeakValues);
		comboTruePeak.setModel(truePeakModel);
		comboTruePeak.setSelectedIndex(3);
		comboTruePeak.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboTruePeak.setEditable(true);
		comboTruePeak.setSize(75, 16);

		casePeakNormalization = new JCheckBox("Peak normalization" + language.getProperty("colon"));
		casePeakNormalization.setName("casePeakNormalization");
		casePeakNormalization.setFont(new Font(mainFont, Font.PLAIN, 12));
		casePeakNormalization.setSize(casePeakNormalization.getPreferredSize().width + 4, 23);

		casePeakNormalization.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				boolean peakNormalization = casePeakNormalization.isSelected();
				comboPeakNormalization.setEnabled(peakNormalization);
				comboFilter.setEnabled(peakNormalization == false);
				caseTruePeak.setEnabled(peakNormalization == false);
				caseLRA.setEnabled(peakNormalization == false);

				if (peakNormalization)
				{
					caseTruePeak.setSelected(false);
					comboTruePeak.setEnabled(false);
					caseLRA.setSelected(false);
					comboLRA.setEnabled(false);
				}
			}

		});

		comboPeakNormalization = new JComboBox<Object>();
		comboPeakNormalization.setName("comboPeakNormalization");
		comboPeakNormalization.setEnabled(false);
		comboPeakNormalization.setMaximumRowCount(15);

		String peakNormalizationValues[] = new String[101];
		peakNormalizationValues[0] = "0.0 dBFS";
		for (int peakIndex = 1; peakIndex < peakNormalizationValues.length; peakIndex++)
		{
			peakNormalizationValues[peakIndex] = String.format(Locale.US, "-%.1f dBFS", peakIndex / 10.0);
		}

		comboPeakNormalization.setModel(new DefaultComboBoxModel<Object>(peakNormalizationValues));
		comboPeakNormalization.setSelectedIndex(10);
		comboPeakNormalization.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboPeakNormalization.setEditable(true);
		comboPeakNormalization.setSize(75, 16);

		caseLRA = new JCheckBox("LRA" + language.getProperty("colon"));
		caseLRA.setName("caseLRA");
		caseLRA.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseLRA.setSize(caseLRA.getPreferredSize().width + 4, 23);

		caseLRA.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseLRA.isSelected())
					comboLRA.setEnabled(true);
				else
					comboLRA.setEnabled(false);
			}

		});

		comboLRA = new JComboBox<Object>();
		comboLRA.setName("comboLRA");
		comboLRA.setEnabled(false);
		comboLRA.setMaximumRowCount(15);

		String LRAValues[] = new String[21];
		LRAValues[0] = "1.0 LU";
		int o = 1;
		do {
			LRAValues[o] = (o + " LU");
			o++;
		} while (o < 21);

		final ComboBoxModel<Object> LRAModel = new DefaultComboBoxModel<Object>(LRAValues);
		comboLRA.setModel(LRAModel);
		comboLRA.setSelectedIndex(15);
		comboLRA.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboLRA.setEditable(true);
		comboLRA.setSize(55, 16);

	}
}
