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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.functions.VideoEncoders;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Bitrate extends Main {

	public static void grpBitrate() {

		grpBitrate = new CollapsiblePanel(language.getProperty("grpBitrate"), false);
		grpBitrate.setLayout(null);
		grpBitrate.setVisible(false);
		grpBitrate.setBackground(Utils.c30);
		grpBitrate.setBounds(658, 30, 312, 156);
		grpBitrate.setOpaque(false);
		frame.getContentPane().add(grpBitrate);

		lblPad = new JLabel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (lblPad.getText().equals(Main.language.getProperty("lblPad"))
						|| lblPad.getText().equals(Main.language.getProperty("lblCrop"))) {
					if (lblPad.getText().equals(Main.language.getProperty("lblPad")))
						g.setColor(Color.BLACK);
					else
						g.setColor(Utils.c25);

					g.fillRect(0, 0, 8, 16);
					g.fillRect((70 - 8), 0, (70 - 8), 16);
				}
			}
		};
		lblPad.setText(language.getProperty("lblPad"));
		lblPad.setName("lblPad");
		lblPad.setBackground(Utils.c42);
		lblPad.setHorizontalAlignment(SwingConstants.CENTER);
		lblPad.setOpaque(true);
		lblPad.setVisible(false);
		lblPad.setFont(new Font(boldFont, Font.PLAIN, 11));
		lblPad.setBounds(comboResolution.getLocation().x + comboResolution.getWidth() + 8,
				comboResolution.getLocation().y + 3, 70, 16);
		grpBitrate.add(lblPad);

		lblPad.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (caseEnableCrop.isSelected()) {
					if (lblPad.getText().equals(language.getProperty("lblPad"))) {
						lblPad.setText(language.getProperty("lblCrop"));
					} else {
						lblPad.setText(language.getProperty("lblPad"));
					}
				} else {
					if (lblPad.getText().equals(language.getProperty("lblPad"))) {
						lblPad.setText(language.getProperty("lblStretch"));
					} else if (lblPad.getText().equals(language.getProperty("lblStretch"))) {
						lblPad.setText(language.getProperty("lblCrop"));
					} else {
						lblPad.setText(language.getProperty("lblPad"));
					}
				}

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
				VideoPlayerUI.resizeAll();
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
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		lblPad.addComponentListener(new ComponentAdapter() {

			public void componentShown(ComponentEvent e) {
				UIController.changeComboOptions();
			}

			public void componentHidden(ComponentEvent e) {
				UIController.changeComboOptions();
			}

		});

		lblVideoBitrate = new JLabel(language.getProperty("lblVideoBitrate"));
		lblVideoBitrate.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblVideoBitrate.setBounds(12, 20, 80, 16);
		grpBitrate.add(lblVideoBitrate);

		debitVideo = new JComboBox<String>();
		debitVideo.setName("debitVideo");
		debitVideo.setModel(new DefaultComboBoxModel<String>(
				new String[] { "50000", "40000", "30000", "25000", "20000", "15000", "10000", "8000", "5000", "3000",
						"2500", "2000", "1500", "1000", "500", language.getProperty("lblBest").toLowerCase(),
						language.getProperty("lblGood").toLowerCase(), "auto" }));
		debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
		debitVideo.setMaximumRowCount(20);
		debitVideo.setFont(new Font(mainFont, Font.PLAIN, 11));
		debitVideo.setEditable(true);
		debitVideo.setBounds(92, lblVideoBitrate.getY() - 2, 93, 22);
		grpBitrate.add(debitVideo);

		debitVideo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ac) {

				if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != "") {
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload
																				// the frame
					VideoPlayerUI.resizeAll();
				}
			}

		});

		lblKbsH264 = new JLabel("kb/s");
		lblKbsH264.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblKbsH264.setBounds(188, debitVideo.getY() + 3, 33, 16);
		grpBitrate.add(lblKbsH264);

		lblMaximumBitrate = new JLabel(language.getProperty("lblMaximumBitrate"));
		lblMaximumBitrate.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblMaximumBitrate.setBounds(lblVideoBitrate.getX(), lblVideoBitrate.getY() + lblVideoBitrate.getHeight() + 11, 80, 16);
		grpBitrate.add(lblMaximumBitrate);

		maximumBitrate = new JComboBox<String>();
		maximumBitrate.setName("maximumBitrate");
		maximumBitrate.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000", "25000",
				"20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000", "500", "auto" }));
		maximumBitrate.setSelectedIndex(maximumBitrate.getModel().getSize() - 1);
		maximumBitrate.setMaximumRowCount(20);
		maximumBitrate.setFont(new Font(mainFont, Font.PLAIN, 11));
		maximumBitrate.setEditable(true);
		maximumBitrate.setBounds(debitVideo.getX(), lblMaximumBitrate.getY() - 2, debitVideo.getWidth(), 22);
		grpBitrate.add(maximumBitrate);

		lblMaximumKbs = new JLabel("kb/s");
		lblMaximumKbs.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblMaximumKbs.setBounds(lblKbsH264.getX(), maximumBitrate.getY() + 3, 33, 16);
		grpBitrate.add(lblMaximumKbs);

		lblAudioBitrate = new JLabel(language.getProperty("lblAudioBitrate"));
		lblAudioBitrate.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudioBitrate.setBounds(lblVideoBitrate.getX(), lblMaximumBitrate.getY() + lblMaximumBitrate.getHeight() + 11, 80, 16);
		grpBitrate.add(lblAudioBitrate);

		debitAudio = new JComboBox<String>();
		debitAudio.setName("debitAudio");
		debitAudio.setModel(new DefaultComboBoxModel<String>(audioValues));
		debitAudio.setSelectedIndex(10);
		debitAudio.setMaximumRowCount(20);
		debitAudio.setFont(new Font(mainFont, Font.PLAIN, 11));
		debitAudio.setEditable(true);
		debitAudio.setBounds(debitVideo.getX(), lblAudioBitrate.getY() - 2, debitVideo.getWidth(), 22);
		grpBitrate.add(debitAudio);

		lblAudioKbs = new JLabel("kb/s");
		lblAudioKbs.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudioKbs.setBounds(lblKbsH264.getX(), debitAudio.getY() + 3, 33, 16);
		grpBitrate.add(lblAudioKbs);

		lblSize = new JLabel(language.getProperty("size"));
		lblSize.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblSize.setBounds(lblVideoBitrate.getX(), lblAudioBitrate.getY() + lblAudioBitrate.getHeight() + 11,
				lblSize.getPreferredSize().width, 16);
		grpBitrate.add(lblSize);

		bitrateSize = new JTextField();
		bitrateSize.setName("bitrateSize");
		bitrateSize.setHorizontalAlignment(SwingConstants.CENTER);
		bitrateSize.setText("2000");
		bitrateSize.setFont(new Font(mainFont, Font.PLAIN, 11));
		bitrateSize.setColumns(10);
		bitrateSize.setBounds(debitVideo.getX(), lblSize.getY() - 2, debitVideo.getWidth(),
				21);
		grpBitrate.add(bitrateSize);

		bitrateSize.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '.' && caracter != ','
						&& caracter != '￿' || bitrateSize.getText().length() >= 5
						|| String.valueOf(caracter).matches("[éèçàù]")
						|| lblVBR.getText().equals("CQ") && lblVBR.isVisible())
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (lblVBR.getText().equals("CQ") == false || lblVBR.isVisible() == false) {
					if (list.getSize() > 0 && fileList.getSelectedIndex() == -1) {
						fileList.setSelectedIndex(0);
						VideoPlayerUtils.setMedia();
					}

					try {
						if (e.getKeyCode() != KeyEvent.VK_DELETE) {
							int h = VideoPlayerUI.durationH;
							int min = VideoPlayerUI.durationM;
							int sec = VideoPlayerUI.durationS;
							int frames = VideoPlayerUI.durationF;
							
							int audio = Integer.parseInt(debitAudio.getSelectedItem().toString());
							float finalSize = Float.parseFloat(bitrateSize.getText().replace(",", "."));
							float result = (float) finalSize / ((h * 3600) + (min * 60) + sec + (frames * ((float) 1 / FFPROBE.currentFPS)));
							float resultAudio = (float) audio / 8 / 1024;
							float resultatdebit = (result - resultAudio) * 8 * 1024;
							debitVideo.getModel().setSelectedItem((int) resultatdebit);
						}
					} catch (Exception e1) {
					}
				}
			}
		});

		lblFileSizeMo = new JLabel(language.getProperty("mo"));
		lblFileSizeMo.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblFileSizeMo.setBounds(lblKbsH264.getX(), bitrateSize.getY() + 3, 33, 16);
		grpBitrate.add(lblFileSizeMo);

		lock = new JLabel(new FlatSVGIcon("resources/unlock.svg", 16, 16));
		lock.setName("unlock");
		lock.setHorizontalAlignment(SwingConstants.CENTER);
		lock.setBounds(bitrateSize.getX() - 21 - 3, bitrateSize.getY(), 21, 21);
		grpBitrate.add(lock);

		lock.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (isLocked) {
					lock.setIcon(new FlatSVGIcon("resources/unlock.svg", 16, 16));
					isLocked = false;
					lock.setName("unlock");
				} else {
					lock.setIcon(new FlatSVGIcon("resources/lock.svg", 16, 16));
					isLocked = true;
					lock.setName("lock");
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

		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (bitrateSize.isFocusOwner() == false)
						FFPROBE.setFilesize();
				} catch (Exception e1) {
				}
			}
		};

		debitVideo.getEditor().getEditorComponent().addKeyListener(keyListener);
		debitAudio.getEditor().getEditorComponent().addKeyListener(keyListener);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (bitrateSize.isFocusOwner() == false)
						FFPROBE.setFilesize();

					if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblBest")))
						debitVideo.setSelectedIndex(1);
					else if (debitVideo.getSelectedItem().toString().equals(language.getProperty("lblWorst")))
						debitVideo.setSelectedIndex(51);

				} catch (Exception e1) {
				}

			}

		};
		debitVideo.addActionListener(actionListener);
		debitAudio.addActionListener(actionListener);

		case2pass = new JCheckBox(language.getProperty("case2pass"));
		case2pass.setName("case2pass");
		case2pass.setFont(new Font(mainFont, Font.PLAIN, 12));
		case2pass.setSize(case2pass.getPreferredSize().width, 23);
		case2pass.setLocation(7, grpBitrate.getHeight() - 32);
		grpBitrate.add(case2pass);

		case2pass.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (inputDeviceIsRunning) {
					JOptionPane.showMessageDialog(frame, language.getProperty("incompatibleInputDevice"),
							language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					case2pass.setSelected(false);
				}

				if (case2pass.isSelected())
				{
					comboResolution.setEnabled(true);
					debitVideo.setEnabled(true);
					bitrateSize.setEnabled(true);
				}
			}
		});

		caseQMax = new JCheckBox(language.getProperty("caseQMax"));
		caseQMax.setName("caseQMax");
		caseQMax.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseQMax.setSize(caseQMax.getPreferredSize().width, 23);
		caseQMax.setLocation(case2pass.getX() + case2pass.getWidth() + 4, case2pass.getY());
		grpBitrate.add(caseQMax);

		caseQMax.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseQMax.isSelected()) {
					caseForcePreset.setSelected(false);
					caseForcePreset.setEnabled(false);
					comboForcePreset.setEnabled(false);

					caseForceSpeed.setSelected(false);
					caseForceSpeed.setEnabled(false);
					comboForceSpeed.setEnabled(false);

					caseForceQuality.setSelected(false);
					caseForceQuality.setEnabled(false);
					comboForceQuality.setEnabled(false);
				} else {
					caseForceSpeed.setEnabled(true);
					caseForceQuality.setEnabled(true);

					if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) || comboAccel
							.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false
							&& (comboAccel.getSelectedItem().equals("Nvidia NVENC")
									|| comboAccel.getSelectedItem().equals("Intel Quick Sync"))) {
						caseForcePreset.setEnabled(true);
					}
				}

				if (Settings.btnPreviewOutput.isSelected() && VideoEncoders.setCodec() != "") {
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload
																				// the frame
					VideoPlayerUI.resizeAll();
				}
			}

		});

		// Links
		h264lines = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);

		        Graphics2D g2 = (Graphics2D) g.create();
		        g2.setColor(Color.LIGHT_GRAY);

		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		        Path2D path = new Path2D.Float();
		        int r = 2; // radius

		        if (comboFonctions.getSelectedItem().toString().contains("H.26"))
		        {
		            // Top horizontal → rounded corner → vertical
		            path.moveTo(6, 15);
		            path.lineTo(41 - r, 15);
		            path.quadTo(41, 15, 41, 15 + r);
		            path.lineTo(41, 95 - r);

		            // Middle branch (rounded)
		            path.moveTo(41, 69 - r);
		            path.quadTo(41, 69, 41, 69);
		            path.lineTo(6, 69);

		            // Bottom branch (rounded)
		            path.moveTo(41, 95 - r);
		            path.quadTo(41, 95, 41 - r, 95);
		            path.lineTo(6, 95);

		        } else {

		            path.moveTo(6, 15);
		            path.lineTo(41 - r, 15);
		            path.quadTo(41, 15, 41, 15 + r);
		            path.lineTo(41, 69 - r);

		            path.moveTo(41, 42 - r);
		            path.quadTo(41, 42, 41, 42);
		            path.lineTo(6, 42);

		            path.moveTo(41, 69 - r);
		            path.quadTo(41, 69, 41 - r, 69);
		            path.lineTo(6, 69);
		        }

		        g2.draw(path);
		        g2.dispose();
		    }
		};
		h264lines.setOpaque(false);
		h264lines.setBounds(212, lblKbsH264.getY() - 7, 53, 105);
		grpBitrate.add(h264lines);
		grpBitrate.validate();
		grpBitrate.repaint();

		h264lines.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				lblVBR.setLocation(h264lines.getX() + h264lines.getWidth(), debitVideo.getY() + 3);
			}

			public void componentHidden(ComponentEvent e) {
				lblVBR.setLocation(debitVideo.getX() + debitVideo.getWidth() + 3, debitVideo.getY() + 3);
			}

		});

		lblVBR = new JLabel("VBR");
		lblVBR.setName("lblVBR");
		lblVBR.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblVBR.setBackground(Utils.c42);
		lblVBR.setHorizontalAlignment(SwingConstants.CENTER);
		lblVBR.setFont(new Font(boldFont, Font.PLAIN, 11));
		lblVBR.setBounds(h264lines.getX() + h264lines.getWidth(), debitVideo.getY() + 3, 32, 16);
		grpBitrate.add(lblVBR);

		lblVBR.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				if (lblVBR.getText().equals("VBR")
						&& (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())
								|| comboAccel.getSelectedItem()
										.equals(language.getProperty("aucune").toLowerCase()) == false
										&& comboAccel.getSelectedItem().equals("Nvidia NVENC")
								|| comboAccel.getSelectedItem()
										.equals(language.getProperty("aucune").toLowerCase()) == false
										&& comboAccel.getSelectedItem().equals("AMD AMF Encoder"))
						&& (comboFonctions.getSelectedItem().toString().equals("H.264")
								|| comboFonctions.getSelectedItem().toString().equals("H.265"))) {
					lblVBR.setText("CBR");
				} else if (lblVBR.getText().equals("CBR")
						|| lblVBR.getText().equals("VBR")
								&& comboAccel.getSelectedItem()
										.equals(language.getProperty("aucune").toLowerCase()) == false
								&& (comboAccel.getSelectedItem().equals("OSX VideoToolbox") == false
										|| System.getProperty("os.arch").equals("aarch64"))
								&& comboFonctions.getSelectedItem().toString().contains("H.26")
								&& comboAccel.getSelectedItem().equals("Vulkan Video") == false
						|| (lblVBR.getText().equals("VBR") && (comboFonctions.getSelectedItem().toString().equals("VP9")
								|| comboFonctions.getSelectedItem().toString().equals("AV1")
								|| comboFonctions.getSelectedItem().toString().equals("H.266")))) {
					lblVBR.setText("CQ");
					bitrateSize.setText("-");
					
					String[] values = new String[257];					
					if (comboFonctions.getSelectedItem().toString().equals("AV1")
					&& System.getProperty("os.name").contains("Windows")
					&& comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase()) == false)
					{
						values[0] = language.getProperty("lblBest");
						for (int i = 1; i < 256; i++) {
							values[i] = String.valueOf(i);
						}
						values[256] = language.getProperty("lblWorst");
						
						debitVideo.setModel(new DefaultComboBoxModel<String>(values));
						debitVideo.setSelectedIndex(127);
					}
					else
					{
						values = new String[53];
						values[0] = language.getProperty("lblBest");
						for (int i = 1; i < 52; i++) {
							values[i] = String.valueOf(i);
						}
						values[52] = language.getProperty("lblWorst");
						
						debitVideo.setModel(new DefaultComboBoxModel<String>(values));
						debitVideo.setSelectedIndex(23);
					}
					
					lblVideoBitrate.setText(language.getProperty("lblValue"));
					lblKbsH264.setVisible(false);
					h264lines.setVisible(false);
					case2pass.setSelected(false);
					case2pass.setEnabled(false);
				} else {
					lblVBR.setText("VBR");
					debitVideo.setModel(new DefaultComboBoxModel<String>(new String[] { "50000", "40000", "30000",
							"25000", "20000", "15000", "10000", "8000", "5000", "3000", "2500", "2000", "1500", "1000",
							"500", language.getProperty("lblBest").toLowerCase(),
							language.getProperty("lblGood").toLowerCase(), "auto" }));
					debitVideo.setSelectedIndex(debitVideo.getModel().getSize() - 1);
					lblVideoBitrate.setText(language.getProperty("lblVideoBitrate"));
					lblKbsH264.setVisible(true);
					h264lines.setVisible(true);
					if (comboAccel.getSelectedItem().equals(language.getProperty("aucune").toLowerCase())
							|| comboFonctions.getSelectedItem().toString().equals("VP8") == false
									&& comboFonctions.getSelectedItem().toString().equals("VP9") == false
									&& comboFonctions.getSelectedItem().toString().contains("H.26") == false)
						case2pass.setEnabled(true);
					FFPROBE.setLength();
				}

				if (comboFonctions.getSelectedItem().toString().equals("AV1"))
				{
					UIController.changeSections(false);
				}
				
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

	}
}
