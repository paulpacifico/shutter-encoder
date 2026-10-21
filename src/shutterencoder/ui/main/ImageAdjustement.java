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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shutterencoder.functions.settings.Colorimetry;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class ImageAdjustement extends Main {

	public static void grpImageAdjustement() {

		grpImageAdjustement = new CollapsiblePanel(language.getProperty("frameColorImage"), true);
		grpImageAdjustement.setName("grpImageAdjustement");
		grpImageAdjustement.setLayout(null);
		grpImageAdjustement.setOpaque(false);
		grpImageAdjustement.setBackground(Utils.c30);
		grpImageAdjustement.setBounds(frame.getWidth(), 258, 312, 17);
		grpImageAdjustement.setVisible(false);

		frame.getContentPane().add(grpImageAdjustement);

		grpImageAdjustement.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpImageAdjustement, 792);
			}

		});

		caseEnableColorimetry = new JCheckBox(Main.language.getProperty("enable"));
		caseEnableColorimetry.setName("caseEnableColorimetry");
		caseEnableColorimetry.setBounds(7, 16, caseEnableColorimetry.getPreferredSize().width + 4, 23);
		caseEnableColorimetry.setForeground(Color.WHITE);
		caseEnableColorimetry.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseEnableColorimetry.setSelected(true);
		grpImageAdjustement.add(caseEnableColorimetry);

		caseEnableColorimetry.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		JLabel lblExposure = new JLabel(Main.language.getProperty("lblExposure"));
		lblExposure.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblExposure.setBounds(13, caseEnableColorimetry.getY() + caseEnableColorimetry.getHeight() + 4, 250, 16);
		grpImageAdjustement.add(lblExposure);

		sliderExposure = new JSlider();
		sliderExposure.setName("sliderExposure");
		sliderExposure.setMaximum(100);
		sliderExposure.setMinimum(-100);
		sliderExposure.setValue(0);
		sliderExposure.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderExposure.setBounds(11, lblExposure.getY() + lblExposure.getHeight() - 2, 284, 22);

		sliderExposure.addMouseListener(new MouseAdapter() {			
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderExposure.setValue(0);
					lblExposure.setText(Main.language.getProperty("lblExposure"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				if (sliderExposure.getValue() == 0) {
					lblExposure.setText(Main.language.getProperty("lblExposure"));
				} else {
					lblExposure.setText(Main.language.getProperty("lblExposure") + " " + sliderExposure.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderExposure);

		JLabel lblGamma = new JLabel(Main.language.getProperty("lblGamma"));
		lblGamma.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblGamma.setBounds(lblExposure.getX(), sliderExposure.getY() + sliderExposure.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblGamma);

		grpImageAdjustement.add(lblGamma);

		sliderGamma = new JSlider();
		sliderGamma.setName("sliderGamma");
		sliderGamma.setMaximum(90);
		sliderGamma.setMinimum(-90);
		sliderGamma.setValue(0);
		sliderGamma.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderGamma.setBounds(sliderExposure.getX(), lblGamma.getY() + lblGamma.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderGamma.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {				
				if (e.getClickCount() == 2) {
					sliderGamma.setValue(0);
					lblGamma.setText(Main.language.getProperty("lblGamma"));					
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderGamma.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGamma.getValue() == 0) {
					lblGamma.setText(Main.language.getProperty("lblGamma"));
				} else {
					lblGamma.setText(Main.language.getProperty("lblGamma") + " " + sliderGamma.getValue());
				}
				
				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderGamma);

		JLabel lblContrast = new JLabel(Main.language.getProperty("lblContrast"));
		lblContrast.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblContrast.setBounds(lblExposure.getX(), sliderGamma.getY() + sliderGamma.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblContrast);

		grpImageAdjustement.add(lblContrast);

		sliderContrast = new JSlider();
		sliderContrast.setName("sliderContrast");
		sliderContrast.setMaximum(100);
		sliderContrast.setMinimum(-100);
		sliderContrast.setValue(0);
		sliderContrast.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderContrast.setBounds(sliderExposure.getX(), lblContrast.getY() + lblContrast.getHeight() - 2,
				sliderExposure.getWidth(), 22);

		sliderContrast.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderContrast.setValue(0);
					lblContrast.setText(Main.language.getProperty("lblContrast"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderContrast.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderContrast.getValue() == 0) {
					lblContrast.setText(Main.language.getProperty("lblContrast"));
				} else {
					lblContrast.setText(Main.language.getProperty("lblContrast") + " " + sliderContrast.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderContrast);

		JLabel lblWhite = new JLabel(Main.language.getProperty("lblWhite"));
		lblWhite.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblWhite.setBounds(lblExposure.getX(), sliderContrast.getY() + sliderContrast.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblWhite);

		grpImageAdjustement.add(lblWhite);

		sliderWhite = new JSlider();
		sliderWhite.setName("sliderWhite");
		sliderWhite.setMaximum(100);
		sliderWhite.setMinimum(-100);
		sliderWhite.setValue(0);
		sliderWhite.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderWhite.setBounds(sliderExposure.getX(), lblWhite.getY() + lblWhite.getHeight() - 2,
				sliderExposure.getWidth(), 22);

		sliderWhite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderWhite.setValue(0);
					lblWhite.setText(Main.language.getProperty("lblWhite"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderWhite.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderWhite.getValue() == 0) {
					lblWhite.setText(Main.language.getProperty("lblWhite"));
				} else {
					lblWhite.setText(Main.language.getProperty("lblWhite") + " " + sliderWhite.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderWhite);

		JLabel lblBlack = new JLabel(Main.language.getProperty("lblBlack"));
		lblBlack.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblBlack.setBounds(lblExposure.getX(), sliderWhite.getY() + sliderWhite.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblBlack);

		grpImageAdjustement.add(lblBlack);

		sliderBlack = new JSlider();
		sliderBlack.setName("sliderBlack");
		sliderBlack.setMaximum(100);
		sliderBlack.setMinimum(-100);
		sliderBlack.setValue(0);
		sliderBlack.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderBlack.setBounds(sliderExposure.getX(), lblBlack.getY() + lblBlack.getHeight() - 2,
				sliderExposure.getWidth(), 22);

		sliderBlack.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderBlack.setValue(0);
					lblBlack.setText(Main.language.getProperty("lblBlack"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderBlack.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBlack.getValue() == 0) {
					lblBlack.setText(Main.language.getProperty("lblBlack"));
				} else {
					lblBlack.setText(Main.language.getProperty("lblBlack") + " " + sliderBlack.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderBlack);

		JLabel lblHighlights = new JLabel(Main.language.getProperty("lblHighlights"));
		lblHighlights.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblHighlights.setBounds(lblExposure.getX(), sliderBlack.getY() + sliderBlack.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblHighlights);

		grpImageAdjustement.add(lblHighlights);

		sliderHighlights = new JSlider();
		sliderHighlights.setName("sliderHighlights");
		sliderHighlights.setMaximum(100);
		sliderHighlights.setMinimum(-100);
		sliderHighlights.setValue(0);
		sliderHighlights.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderHighlights.setBounds(sliderExposure.getX(), lblHighlights.getY() + lblHighlights.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderHighlights.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderHighlights.setValue(0);
					lblHighlights.setText(Main.language.getProperty("lblHighlights"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderHighlights.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHighlights.getValue() == 0) {
					lblHighlights.setText(Main.language.getProperty("lblHighlights"));
				} else {
					lblHighlights .setText(Main.language.getProperty("lblHighlights") + " " + sliderHighlights.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderHighlights);

		JLabel lblMediums = new JLabel(Main.language.getProperty("lblMediums"));
		lblMediums.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblMediums.setBounds(lblExposure.getX(), sliderHighlights.getY() + sliderHighlights.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblMediums);

		grpImageAdjustement.add(lblMediums);

		sliderMediums = new JSlider();
		sliderMediums.setName("sliderMediums");
		sliderMediums.setMaximum(100);
		sliderMediums.setMinimum(-100);
		sliderMediums.setValue(0);
		sliderMediums.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderMediums.setBounds(sliderExposure.getX(), lblMediums.getY() + lblMediums.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderMediums.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderMediums.setValue(0);
					lblMediums.setText(Main.language.getProperty("lblMediums"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderMediums.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderMediums.getValue() == 0) {
					lblMediums.setText(Main.language.getProperty("lblMediums"));
				} else {
					lblMediums.setText(Main.language.getProperty("lblMediums") + " " + sliderMediums.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderMediums);

		JLabel lblShadows = new JLabel(Main.language.getProperty("lblShadows"));
		lblShadows.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblShadows.setBounds(lblExposure.getX(), sliderMediums.getY() + sliderMediums.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblShadows);

		grpImageAdjustement.add(lblShadows);

		sliderShadows = new JSlider();
		sliderShadows.setName("sliderShadows");
		sliderShadows.setMaximum(100);
		sliderShadows.setMinimum(-100);
		sliderShadows.setValue(0);
		sliderShadows.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderShadows.setBounds(sliderExposure.getX(), lblShadows.getY() + lblShadows.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderShadows.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderShadows.setValue(0);
					lblShadows.setText(Main.language.getProperty("lblShadows"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderShadows.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderShadows.getValue() == 0) {
					lblShadows.setText(Main.language.getProperty("lblShadows"));
				} else {
					lblShadows.setText(Main.language.getProperty("lblShadows") + " " + sliderShadows.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderShadows);

		JLabel lblBalance = new JLabel(Main.language.getProperty("lblBalance"));
		lblBalance.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblBalance.setBounds(lblExposure.getX(), sliderShadows.getY() + sliderShadows.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblBalance);

		grpImageAdjustement.add(lblBalance);

		sliderBalance = new JSlider();
		sliderBalance.setName("sliderBalance");
		sliderBalance.setMaximum(12000);
		sliderBalance.setMinimum(1000);
		sliderBalance.setValue(6500);
		sliderBalance.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderBalance.setBounds(sliderExposure.getX(), lblBalance.getY() + lblBalance.getHeight() - 2,
				sliderExposure.getWidth(), 22);

		sliderBalance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderBalance.setValue(6500);
					lblBalance.setText(Main.language.getProperty("lblBalance"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderBalance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBalance.getValue() == 6500) {
					lblBalance.setText(Main.language.getProperty("lblBalance"));
				} else {
					lblBalance.setText(Main.language.getProperty("lblBalance") + " " + sliderBalance.getValue() + "k");
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderBalance);

		JLabel lblHUE = new JLabel(Main.language.getProperty("lblHUE"));
		lblHUE.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblHUE.setBounds(lblExposure.getX(), sliderBalance.getY() + sliderBalance.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblHUE);

		grpImageAdjustement.add(lblHUE);

		sliderHUE = new JSlider();
		sliderHUE.setName("sliderHUE");
		sliderHUE.setMaximum(180);
		sliderHUE.setMinimum(-180);
		sliderHUE.setValue(0);
		sliderHUE.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderHUE.setBounds(sliderExposure.getX(), lblHUE.getY() + lblHUE.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderHUE.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderHUE.setValue(0);
					lblHUE.setText(Main.language.getProperty("lblHUE"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderHUE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderHUE.getValue() == 0) {
					lblHUE.setText(Main.language.getProperty("lblHUE"));
				} else {
					lblHUE.setText(Main.language.getProperty("lblHUE") + " " + sliderHUE.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderHUE);

		JLabel lblRGB = new JLabel(Main.language.getProperty("lblRGB"));
		lblRGB.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblRGB.setBounds(lblExposure.getX(), sliderHUE.getY() + sliderHUE.getHeight() + 6, lblRGB.getPreferredSize().width + 4, 16);
		grpImageAdjustement.add(lblRGB);

		comboRGB = new JComboBox<String>();
		comboRGB.setName("comboRGB");
		comboRGB.setModel(new DefaultComboBoxModel<String>(
				new String[] { Main.language.getProperty("setAll"), Main.language.getProperty("setHigh"),
						Main.language.getProperty("setMedium"), Main.language.getProperty("setLow") }));
		comboRGB.setMaximumRowCount(10);
		comboRGB.setEditable(false);
		comboRGB.setSelectedIndex(0);
		comboRGB.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboRGB.setBounds(lblRGB.getX() + lblRGB.getWidth() + 3, lblRGB.getY() - 3, 100, 22);
		grpImageAdjustement.add(comboRGB);

		comboRGB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll"))) {
					sliderRED.setValue(Colorimetry.allR);
					sliderGREEN.setValue(Colorimetry.allG);
					sliderBLUE.setValue(Colorimetry.allB);
				} else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow"))) {
					sliderRED.setValue(Colorimetry.lowR);
					sliderGREEN.setValue(Colorimetry.lowG);
					sliderBLUE.setValue(Colorimetry.lowB);
				} else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium"))) {
					sliderRED.setValue(Colorimetry.mediumR);
					sliderGREEN.setValue(Colorimetry.mediumG);
					sliderBLUE.setValue(Colorimetry.mediumB);
				} else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh"))) {
					sliderRED.setValue(Colorimetry.highR);
					sliderGREEN.setValue(Colorimetry.highG);
					sliderBLUE.setValue(Colorimetry.highB);
				}
			}

		});

		JLabel lblR = new JLabel(Main.language.getProperty("lblRED"));
		lblR.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblR.setBounds(lblExposure.getX(), comboRGB.getY() + comboRGB.getHeight() + 3, lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblR);

		grpImageAdjustement.add(lblR);

		sliderRED = new JSlider();
		sliderRED.setName("sliderRED");
		sliderRED.setMaximum(100);
		sliderRED.setMinimum(-100);
		sliderRED.setValue(0);
		sliderRED.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderRED.setBounds(sliderExposure.getX(), lblR.getY() + lblR.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderRED.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {				
				if (e.getClickCount() == 2) {
					sliderRED.setValue(0);
					lblR.setText(Main.language.getProperty("lblRED"));
				}
				
				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highR = sliderRED.getValue();
				
				VideoPlayerUtils.restartPlayback();				
			}

		});

		sliderRED.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderRED.getValue() == 0) {
					lblR.setText(Main.language.getProperty("lblRED"));
				} else {
					lblR.setText(Main.language.getProperty("lblRED") + " " + sliderRED.getValue());
				}

				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumR = sliderRED.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highR = sliderRED.getValue();

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderRED);

		JLabel lblG = new JLabel(Main.language.getProperty("lblGREEN"));
		lblG.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblG.setBounds(lblExposure.getX(), sliderRED.getY() + sliderRED.getHeight(), lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblG);

		grpImageAdjustement.add(lblG);

		sliderGREEN = new JSlider();
		sliderGREEN.setName("sliderGREEN");
		sliderGREEN.setMaximum(100);
		sliderGREEN.setMinimum(-100);
		sliderGREEN.setValue(0);
		sliderGREEN.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderGREEN.setBounds(sliderExposure.getX(), lblG.getY() + lblG.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderGREEN.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderGREEN.setValue(0);
					lblG.setText(Main.language.getProperty("lblGREEN"));
				}
				
				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highG = sliderGREEN.getValue();
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderGREEN.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGREEN.getValue() == 0) {
					lblG.setText(Main.language.getProperty("lblGREEN"));
				} else {
					lblG.setText(Main.language.getProperty("lblGREEN") + " " + sliderGREEN.getValue());
				}

				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumG = sliderGREEN.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highG = sliderGREEN.getValue();

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderGREEN);

		JLabel lblB = new JLabel(Main.language.getProperty("lblBLUE"));
		lblB.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblB.setBounds(lblExposure.getX(), sliderGREEN.getY() + sliderGREEN.getHeight(), lblExposure.getSize().width,
				16);
		grpImageAdjustement.add(lblB);

		grpImageAdjustement.add(lblB);

		sliderBLUE = new JSlider();
		sliderBLUE.setName("sliderBLUE");
		sliderBLUE.setMaximum(100);
		sliderBLUE.setMinimum(-100);
		sliderBLUE.setValue(0);
		sliderBLUE.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderBLUE.setBounds(sliderExposure.getX(), lblB.getY() + lblB.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderBLUE.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderBLUE.setValue(0);
					lblB.setText(Main.language.getProperty("lblBLUE"));
				}
				
				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highB = sliderBLUE.getValue();
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderBLUE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderBLUE.getValue() == 0) {
					lblB.setText(Main.language.getProperty("lblBLUE"));
				} else {
					lblB.setText(Main.language.getProperty("lblBLUE") + " " + sliderBLUE.getValue());
				}

				if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setAll")))
					Colorimetry.allB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setLow")))
					Colorimetry.lowB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setMedium")))
					Colorimetry.mediumB = sliderBLUE.getValue();
				else if (comboRGB.getSelectedItem().equals(Main.language.getProperty("setHigh")))
					Colorimetry.highB = sliderBLUE.getValue();

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderBLUE);

		JLabel lblSaturation = new JLabel(Main.language.getProperty("lblSaturation"));
		lblSaturation.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblSaturation.setBounds(lblExposure.getX(), sliderBLUE.getY() + sliderBLUE.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblSaturation);

		grpImageAdjustement.add(lblSaturation);

		sliderSaturation = new JSlider();
		sliderSaturation.setName("sliderSaturation");
		sliderSaturation.setMaximum(100);
		sliderSaturation.setMinimum(-100);
		sliderSaturation.setValue(0);
		sliderSaturation.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderSaturation.setBounds(sliderExposure.getX(), lblSaturation.getY() + lblSaturation.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderSaturation.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderSaturation.setValue(0);
					lblSaturation.setText(Main.language.getProperty("lblSaturation"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderSaturation.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderSaturation.getValue() == 0) {
					lblSaturation.setText(Main.language.getProperty("lblSaturation"));
				} else {
					lblSaturation.setText(Main.language.getProperty("lblSaturation") + " " + sliderSaturation.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderSaturation);

		JLabel lblVibrance = new JLabel(Main.language.getProperty("lblVibrance"));
		lblVibrance.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblVibrance.setBounds(lblExposure.getX(), sliderSaturation.getY() + sliderSaturation.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblVibrance);

		grpImageAdjustement.add(lblVibrance);

		sliderVibrance = new JSlider();
		sliderVibrance.setName("sliderVibrance");
		sliderVibrance.setMaximum(100);
		sliderVibrance.setMinimum(-100);
		sliderVibrance.setValue(0);
		sliderVibrance.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderVibrance.setBounds(sliderExposure.getX(), lblVibrance.getY() + lblVibrance.getHeight() - 2, sliderExposure.getWidth(), 22);

		grpImageAdjustement.add(sliderVibrance);

		comboVibrance = new JComboBox<String>();
		comboVibrance.setName("comboVibrance");
		comboVibrance.setModel(new DefaultComboBoxModel<String>(
				new String[] { Main.language.getProperty("intensity"), Main.language.getProperty("red"),
						Main.language.getProperty("green"), Main.language.getProperty("blue") }));
		comboVibrance.setMaximumRowCount(10);
		comboVibrance.setEditable(false);
		comboVibrance.setSelectedIndex(0);
		comboVibrance.setFont(new Font(Main.mainFont, Font.PLAIN, 10));
		comboVibrance.setBounds(grpImageAdjustement.getWidth() - 108, lblVibrance.getY() - 5, 100, 22);
		grpImageAdjustement.add(comboVibrance);

		comboVibrance.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("intensity"))) {
					sliderVibrance.setValue(Colorimetry.vibranceValue);
				} else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("red"))) {
					sliderVibrance.setValue(Colorimetry.vibranceR);
				} else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("green"))) {
					sliderVibrance.setValue(Colorimetry.vibranceG);
				} else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("blue"))) {
					sliderVibrance.setValue(Colorimetry.vibranceB);
				}
			}

		});

		sliderVibrance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderVibrance.setValue(0);
					lblVibrance.setText(Main.language.getProperty("lblVibrance"));
				}
				
				if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("intensity")))
					Colorimetry.vibranceValue = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("red")))
					Colorimetry.vibranceR = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("green")))
					Colorimetry.vibranceG = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("blue")))
					Colorimetry.vibranceB = sliderVibrance.getValue();
				
				VideoPlayerUtils.restartPlayback();			
			}

		});

		sliderVibrance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVibrance.getValue() == 0) {
					lblVibrance.setText(Main.language.getProperty("lblVibrance"));
				} else {
					lblVibrance.setText(Main.language.getProperty("lblVibrance") + " " + sliderVibrance.getValue());
				}

				if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("intensity")))
					Colorimetry.vibranceValue = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("red")))
					Colorimetry.vibranceR = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("green")))
					Colorimetry.vibranceG = sliderVibrance.getValue();
				else if (comboVibrance.getSelectedItem().equals(Main.language.getProperty("blue")))
					Colorimetry.vibranceB = sliderVibrance.getValue();

				VideoPlayerUtils.loadImage();
			}

		});

		JLabel lblGrain = new JLabel(Main.language.getProperty("lblGrain"));
		lblGrain.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblGrain.setBounds(lblExposure.getX(), sliderVibrance.getY() + sliderVibrance.getHeight(), lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblGrain);

		sliderGrain = new JSlider();
		sliderGrain.setName("sliderGrain");
		sliderGrain.setMaximum(100);
		sliderGrain.setMinimum(-100);
		sliderGrain.setValue(0);
		sliderGrain.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderGrain.setBounds(sliderExposure.getX(), lblGrain.getY() + lblGrain.getHeight() - 2, sliderExposure.getWidth(), 22);

		sliderGrain.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderGrain.setValue(0);
					lblGrain.setText(Main.language.getProperty("lblGrain"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderGrain.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderGrain.getValue() == 0) {
					lblGrain.setText(Main.language.getProperty("lblGrain"));
				} else {
					lblGrain.setText(Main.language.getProperty("lblGrain") + " " + sliderGrain.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		grpImageAdjustement.add(sliderGrain);

		JLabel lblVignette = new JLabel(Main.language.getProperty("lblVignette"));
		lblVignette.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblVignette.setBounds(lblExposure.getX(), sliderGrain.getY() + sliderGrain.getHeight(), lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblVignette);

		sliderVignette = new JSlider();
		sliderVignette.setName("sliderVignette");
		sliderVignette.setMaximum(100);
		sliderVignette.setMinimum(-100);
		sliderVignette.setValue(0);
		sliderVignette.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderVignette.setBounds(sliderExposure.getX(), lblVignette.getY() + lblVignette.getHeight() - 2, sliderExposure.getWidth(), 22);
		grpImageAdjustement.add(sliderVignette);

		sliderVignette.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderVignette.setValue(0);
					lblVignette.setText(Main.language.getProperty("lblVignette"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderVignette.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (sliderVignette.getValue() == 0) {
					lblVignette.setText(Main.language.getProperty("lblVignette"));
				} else {
					lblVignette.setText(Main.language.getProperty("lblVignette") + " " + sliderVignette.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		JLabel lblAngle = new JLabel(Main.language.getProperty("caseAngle"));
		lblAngle.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblAngle.setBounds(lblExposure.getX(), sliderVignette.getY() + sliderVignette.getHeight(),
				lblExposure.getSize().width, 16);
		grpImageAdjustement.add(lblAngle);

		sliderAngle = new JSlider();
		sliderAngle.setName("sliderAngle");
		sliderAngle.setMaximum(100);
		sliderAngle.setMinimum(-100);
		sliderAngle.setValue(0);
		sliderAngle.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderAngle.setBounds(sliderExposure.getX(), lblAngle.getY() + lblAngle.getHeight() - 2, sliderExposure.getWidth(), 22);
		grpImageAdjustement.add(sliderAngle);

		sliderAngle.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderAngle.setValue(0);
					lblAngle.setText(Main.language.getProperty("caseAngle"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderAngle.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				if (sliderAngle.getValue() == 0) {
					lblAngle.setText(Main.language.getProperty("caseAngle"));
				} else {
					lblAngle.setText(Main.language.getProperty("caseAngle") + " " + sliderAngle.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		JLabel lblZoom = new JLabel(Main.language.getProperty("lblZoom"));
		lblZoom.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		lblZoom.setBounds(lblExposure.getX(), sliderAngle.getY() + sliderAngle.getHeight(), lblExposure.getSize().width,
				16);
		grpImageAdjustement.add(lblZoom);

		sliderZoom = new JSlider();
		sliderZoom.setName("sliderZoom");
		sliderZoom.setMaximum(100);
		sliderZoom.setMinimum(0);
		sliderZoom.setValue(0);
		sliderZoom.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		sliderZoom.setBounds(sliderExposure.getX(), lblZoom.getY() + lblZoom.getHeight() - 2, sliderExposure.getWidth(), 22);
		grpImageAdjustement.add(sliderZoom);

		sliderZoom.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sliderZoom.setValue(0);
					lblZoom.setText(Main.language.getProperty("lblZoom"));
				}
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		sliderZoom.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				if (sliderZoom.getValue() == 0) {
					lblZoom.setText(Main.language.getProperty("lblZoom"));
				} else {
					lblZoom.setText(Main.language.getProperty("lblZoom") + " " + sliderZoom.getValue());
				}

				VideoPlayerUtils.loadImage();
			}

		});

		btnResetColor = new JButton(Main.language.getProperty("btnReset"));
		btnResetColor.setFont(new Font(Main.boldFont, Font.PLAIN, 12));
		btnResetColor.setBounds(lblExposure.getX(), sliderZoom.getY() + sliderZoom.getHeight() + 2, sliderZoom.getWidth(), 21);
		grpImageAdjustement.add(btnResetColor);

		btnResetColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				Colorimetry.allR = 0;
				Colorimetry.allG = 0;
				Colorimetry.allB = 0;
				Colorimetry.highR = 0;
				Colorimetry.highG = 0;
				Colorimetry.highB = 0;
				Colorimetry.mediumR = 0;
				Colorimetry.mediumG = 0;
				Colorimetry.mediumB = 0;
				Colorimetry.lowR = 0;
				Colorimetry.lowG = 0;
				Colorimetry.lowB = 0;
				Colorimetry.vibranceValue = 0;
				Colorimetry.vibranceR = 0;
				Colorimetry.vibranceG = 0;
				Colorimetry.vibranceB = 0;
				Colorimetry.balanceAll = "";
				Colorimetry.balanceHigh = "";
				Colorimetry.balanceMedium = "";
				Colorimetry.balanceLow = "";
				sliderExposure.setValue(0);
				sliderVignette.setValue(0);
				sliderGamma.setValue(0);
				sliderContrast.setValue(0);
				sliderHighlights.setValue(0);
				sliderMediums.setValue(0);
				sliderShadows.setValue(0);
				sliderWhite.setValue(0);
				sliderBlack.setValue(0);
				sliderBalance.setValue(6500);
				sliderHUE.setValue(0);
				sliderRED.setValue(0);
				sliderGREEN.setValue(0);
				sliderBLUE.setValue(0);
				sliderVibrance.setValue(0);
				sliderSaturation.setValue(0);
				sliderGrain.setValue(0);
				sliderVignette.setValue(0);
				sliderAngle.setValue(0);
				sliderZoom.setValue(0);

				// important
				comboRGB.setSelectedIndex(0);
				comboVibrance.setSelectedIndex(0);
				
				VideoPlayerUtils.restartPlayback();
				
			}

		});

	}
}
