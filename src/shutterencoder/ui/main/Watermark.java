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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.SystemFileChooser;

import shutterencoder.ui.others.RecordInputDevice;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class Watermark extends Main {

	public static void grpWatermark() {

		grpWatermark = new CollapsiblePanel(language.getProperty("caseLogo"), true);
		grpWatermark.setLayout(null);
		grpWatermark.setBackground(Utils.c30);
		grpWatermark.setSize(312, 17);
		grpWatermark.setLocation(frame.getWidth(), grpSubtitles.getY() + grpSubtitles.getHeight() + 6);
		grpWatermark.setVisible(false);
		grpWatermark.setOpaque(false);
		frame.getContentPane().add(grpWatermark);

		grpWatermark.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpWatermark, 113);
			}

		});

		logo = new JPanel() {

			protected void paintComponent(Graphics g) {

				Graphics2D g2d = (Graphics2D) g;

				if (textWatermarkOpacity.getText().length() > 0
						&& Integer.parseInt(textWatermarkOpacity.getText()) <= 100) {
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
							(float) Integer.valueOf(textWatermarkOpacity.getText()) / 100));
				}

				if (logoPNG != null && VideoPlayerCore.videoPath != null) {
					g2d.drawImage(logoPNG, 0, 0, null);
				}
			}
		};
		logo.setLayout(null);
		logo.setOpaque(false);
		logo.setBackground(new Color(0, 0, 0, 50));

		logo.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed()) {
					logo.setLocation((int) Math.floor(VideoPlayerUI.player.getWidth() / 2 - logo.getWidth() / 2),
							(int) Math.floor(VideoPlayerUI.player.getHeight() / 2 - logo.getHeight() / 2));
					textWatermarkPosX.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
					textWatermarkPosY.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));
				} else {
					logoPosX = e.getLocationOnScreen().x;
					logoPosY = e.getLocationOnScreen().y;
				}

				watermarkPreset = null;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;

			}

		});

		logo.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				logo.setLocation(MouseInfo.getPointerInfo().getLocation().x - logoPosX + logoLocX,
						MouseInfo.getPointerInfo().getLocation().y - logoPosY + logoLocY);
				textWatermarkPosX
						.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
				textWatermarkPosY
						.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));

				watermarkPreset = null;
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});

		caseAddWatermark = new JCheckBox(Main.language.getProperty("frameLogo"));
		caseAddWatermark.setName("caseAddWatermark");
		caseAddWatermark.setBounds(8, 16, caseAddWatermark.getPreferredSize().width + 8, 23);
		caseAddWatermark.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAddWatermark.setSelected(false);
		grpWatermark.add(caseAddWatermark);

		caseSafeArea = new JCheckBox(Main.language.getProperty("caseSafeArea"));
		caseSafeArea.setName("caseSafeArea");
		caseSafeArea.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseSafeArea.setSize(caseSafeArea.getPreferredSize().width, 23);
		caseSafeArea.setLocation(caseAddWatermark.getX() + caseAddWatermark.getWidth() + 4, caseAddWatermark.getY());
		grpWatermark.add(caseSafeArea);

		JPanel safeArea = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {

				super.paintComponent(g);
				g.setColor(new Color(200, 200, 200));

				// Action safe
				g.drawRect(
						VideoPlayerUI.player.getX() + (int) ((float) ((float) VideoPlayerUI.player.getWidth() * 0.07) / 2),
						VideoPlayerUI.player.getY() + (int) ((float) ((float) VideoPlayerUI.player.getHeight() * 0.07) / 2),
						(int) ((float) VideoPlayerUI.player.getWidth() * 0.93),
						(int) ((float) VideoPlayerUI.player.getHeight() * 0.93));

				// Title safe
				g.drawRect(
						VideoPlayerUI.player.getX() + (int) ((float) ((float) VideoPlayerUI.player.getWidth() * 0.1) / 2),
						VideoPlayerUI.player.getY() + (int) ((float) ((float) VideoPlayerUI.player.getHeight() * 0.1) / 2),
						(int) ((float) VideoPlayerUI.player.getWidth() * 0.9),
						(int) ((float) VideoPlayerUI.player.getHeight() * 0.9));
			}
		};

		safeArea.setOpaque(false);
		safeArea.setBackground(new Color(0, 0, 0, 0));
		safeArea.setSize(640, 360);
		frame.getGlassPane().setVisible(false);

		caseSafeArea.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseSafeArea.isSelected()) {
					frame.setGlassPane(safeArea);
					frame.getGlassPane().setVisible(true);
				} else {
					frame.getGlassPane().setVisible(false);
				}
			}

		});

		JLabel watermarkTopLeft = new JLabel("\u2196");
		watermarkTopLeft.setName("watermarkTopLeft");
		watermarkTopLeft.setBackground(new Color(42, 42, 47, 0));
		watermarkTopLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTopLeft.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkTopLeft.setSize(28, 16);
		watermarkTopLeft.setLocation(grpWatermark.getWidth() / 2 - 270 / 2,
				caseAddWatermark.getY() + caseAddWatermark.getHeight() + 2);
		grpWatermark.add(watermarkTopLeft);

		JLabel watermarkLeft = new JLabel("\u2190");
		watermarkLeft.setName("watermarkLeft");
		watermarkLeft.setBackground(new Color(42, 42, 47, 0));
		watermarkLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkLeft.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkLeft.setSize(watermarkTopLeft.getSize());
		watermarkLeft.setLocation(watermarkTopLeft.getX() + watermarkTopLeft.getWidth() + 2,
				watermarkTopLeft.getY() - 2);
		grpWatermark.add(watermarkLeft);

		JLabel watermarkBottomLeft = new JLabel("\u2199");
		watermarkBottomLeft.setName("watermarkBottomLeft");
		watermarkBottomLeft.setBackground(new Color(42, 42, 47, 0));
		watermarkBottomLeft.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottomLeft.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkBottomLeft.setSize(watermarkTopLeft.getSize());
		watermarkBottomLeft.setLocation(watermarkLeft.getX() + watermarkLeft.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottomLeft);

		JLabel watermarkTop = new JLabel("\u2191");
		watermarkTop.setName("watermarkTop");
		watermarkTop.setBackground(new Color(42, 42, 47, 0));
		watermarkTop.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTop.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkTop.setSize(watermarkTopLeft.getSize());
		watermarkTop.setLocation(watermarkBottomLeft.getX() + watermarkBottomLeft.getWidth() + 2,
				watermarkTopLeft.getY());
		grpWatermark.add(watermarkTop);

		JLabel watermarkCenter = new JLabel("\u2500");
		watermarkCenter.setName("watermarkCenter");
		watermarkCenter.setBackground(new Color(42, 42, 47, 0));
		watermarkCenter.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkCenter.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkCenter.setSize(watermarkTopLeft.getSize());
		watermarkCenter.setLocation(watermarkTop.getX() + watermarkTop.getWidth() + 2, watermarkTopLeft.getY() - 1);
		grpWatermark.add(watermarkCenter);

		JLabel watermarkBottom = new JLabel("\u2193");
		watermarkBottom.setName("watermarkBottom");
		watermarkBottom.setBackground(new Color(42, 42, 47, 0));
		watermarkBottom.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottom.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkBottom.setSize(watermarkTopLeft.getSize());
		watermarkBottom.setLocation(watermarkCenter.getX() + watermarkCenter.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottom);

		JLabel watermarkTopRight = new JLabel("\u2197");
		watermarkTopRight.setName("watermarkTopRight");
		watermarkTopRight.setBackground(new Color(42, 42, 47, 0));
		watermarkTopRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkTopRight.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkTopRight.setSize(watermarkTopLeft.getSize());
		watermarkTopRight.setLocation(watermarkBottom.getX() + watermarkBottom.getWidth() + 2, watermarkTopLeft.getY());
		grpWatermark.add(watermarkTopRight);

		JLabel watermarkRight = new JLabel("\u2192");
		watermarkRight.setName("watermarkRight");
		watermarkRight.setBackground(new Color(42, 42, 47, 0));
		watermarkRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkRight.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkRight.setSize(watermarkTopLeft.getSize());
		watermarkRight.setLocation(watermarkTopRight.getX() + watermarkTopRight.getWidth() + 2,
				watermarkTopLeft.getY() - 2);
		grpWatermark.add(watermarkRight);

		JLabel watermarkBottomRight = new JLabel("\u2198");
		watermarkBottomRight.setName("watermarkBottomRight");
		watermarkBottomRight.setBackground(new Color(42, 42, 47, 0));
		watermarkBottomRight.setHorizontalAlignment(SwingConstants.CENTER);
		watermarkBottomRight.setFont(new Font("Noto Sans SC Medium", Font.PLAIN, 12));
		watermarkBottomRight.setSize(watermarkTopLeft.getSize());
		watermarkBottomRight.setLocation(watermarkRight.getX() + watermarkRight.getWidth() + 2,
				watermarkTopLeft.getY());
		grpWatermark.add(watermarkBottomRight);

		MouseListener watermarkPositions = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
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
			public void mousePressed(MouseEvent e) {
				VideoPlayerOverlay.watermarkPositions(e.getComponent().getName());
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		};

		watermarkTopLeft.addMouseListener(watermarkPositions);
		watermarkLeft.addMouseListener(watermarkPositions);
		watermarkBottomLeft.addMouseListener(watermarkPositions);
		watermarkTop.addMouseListener(watermarkPositions);
		watermarkCenter.addMouseListener(watermarkPositions);
		watermarkBottom.addMouseListener(watermarkPositions);
		watermarkTopRight.addMouseListener(watermarkPositions);
		watermarkRight.addMouseListener(watermarkPositions);
		watermarkBottomRight.addMouseListener(watermarkPositions);
		
		JPanel panelForButtons = new JPanel();
		panelForButtons.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 15));
		panelForButtons.setBackground(Utils.c42);
		panelForButtons.setOpaque(false);
		panelForButtons.setBounds(watermarkTopLeft.getX() - 2, watermarkTopLeft.getY() - 2, (watermarkBottomRight.getX() + watermarkBottomRight.getWidth()) - watermarkTopLeft.getX() + 4, 18);
		grpWatermark.add(panelForButtons);
				
		caseAddWatermark.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAddWatermark.isSelected()
				&& VideoPlayerCore.videoPath != null)
				{
					// Initiate location
					if (logo.getWidth() == 0) {
						logo.setSize(VideoPlayerUI.player.getWidth(), VideoPlayerUI.player.getHeight());
					}

					boolean addDevice = false;
					if (RecordInputDevice.comboInputVideo != null && RecordInputDevice.comboInputVideo.getSelectedIndex() > 0)
						addDevice = true;

					if (Main.inputDeviceIsRunning && Main.list.getElementAt(0).equals("Capture.current.screen")
							&& System.getProperty("os.name").contains("Windows") && addDevice == false) {
						int reply = JOptionPane.showConfirmDialog(frame, Main.language.getProperty("addInputDevice"),
								Main.language.getProperty("menuItemInputDevice"), JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE);

						if (reply == JOptionPane.YES_OPTION) {
							addDevice = true;
							caseAddWatermark.doClick();
							inputDevice.doClick();
							inputDeviceIsRunning = false;
							grpImageAdjustement.setEnabled(true);
							Component[] components = Main.grpImageAdjustement.getComponents();
							for (int i = 0; i < components.length; i++) {
								components[i].setEnabled(true);
							}
						} else {
							Main.overlayDeviceIsRunning = false;
						}
					}

					if (Main.list.getSize() == 0) {
						JOptionPane.showMessageDialog(frame, Main.language.getProperty("addFileToList"),
								Main.language.getProperty("noFileInList"), JOptionPane.ERROR_MESSAGE);
						caseAddWatermark.setSelected(false);
					} else if (addDevice) {
						for (Component c : grpWatermark.getComponents()) {
							if (c instanceof JComboBox == false) {
								c.setEnabled(true);
							}
						}

						VideoPlayerOverlay.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
						VideoPlayerUI.player.add(logo);

						textWatermarkPosX.setText(
								String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
						textWatermarkPosY.setText(
								String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));

						// Overimage need to be the last component added
						if (caseEnableCrop.isSelected()) {
							VideoPlayerUI.player.remove(selection);
							VideoPlayerUI.player.remove(overImage);
							VideoPlayerUI.player.add(selection);
							VideoPlayerUI.player.add(overImage);
						}

						VideoPlayerUI.resizeAll();
					} else {
						// Preset loaded
						if (logoFile != null) {
							for (Component c : grpWatermark.getComponents()) {
								if (c instanceof JComboBox == false) {
									c.setEnabled(true);
								}
							}

							VideoPlayerOverlay.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
							VideoPlayerUI.player.add(logo);

							textWatermarkPosX.setText(String
									.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
							textWatermarkPosY.setText(String
									.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));

							// Overimage need to be the last component added
							if (caseEnableCrop.isSelected()) {
								VideoPlayerUI.player.remove(selection);
								VideoPlayerUI.player.remove(overImage);
								VideoPlayerUI.player.add(selection);
								VideoPlayerUI.player.add(overImage);
							}
						} else {
							
							SystemFileChooser fc = new SystemFileChooser();
							fc.setCurrentDirectory(new File(VideoPlayerCore.videoPath).getParentFile());

							if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
							{
								logoFile = fc.getSelectedFile().toString();

								for (Component c : grpWatermark.getComponents()) {
									if (c instanceof JComboBox == false) {
										c.setEnabled(true);
									}
								}

								VideoPlayerOverlay.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
								VideoPlayerUI.player.add(logo);

								textWatermarkPosX.setText(String.valueOf(
										Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
								textWatermarkPosY.setText(String.valueOf(
										Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));

								// Overimage need to be the last component added
								if (caseEnableCrop.isSelected()) {
									VideoPlayerUI.player.remove(selection);
									VideoPlayerUI.player.remove(overImage);
									VideoPlayerUI.player.add(selection);
									VideoPlayerUI.player.add(overImage);
								}
							} else {
								if (Main.overlayDeviceIsRunning)
									Main.overlayDeviceIsRunning = false;

								caseAddWatermark.setSelected(false);
							}
						}
					}

				} else {
					for (Component c : grpWatermark.getComponents()) {
						if (c instanceof JCheckBox == false) {
							c.setEnabled(false);
						}
					}

					VideoPlayerUI.player.remove(logo);
					logoFile = null;
					logoPNG = null;
				}
				
				panelForButtons.repaint();
				
				watermarkTopLeft.repaint();	
				watermarkLeft.repaint();	
				watermarkBottomLeft.repaint();	
				watermarkTop.repaint();	
				watermarkCenter.repaint();	
				watermarkBottom.repaint();	
				watermarkTopRight.repaint();	
				watermarkRight.repaint();	
				watermarkBottomRight.repaint();

				VideoPlayerUI.player.repaint();

			}

		});
		
		JLabel watermarkPosX = new JLabel(Main.language.getProperty("posX"));
		watermarkPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkPosX.setEnabled(false);
		watermarkPosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		watermarkPosX.setForeground(Utils.themeColor);
		watermarkPosX.setBounds(24, watermarkBottomRight.getY() + watermarkBottomRight.getHeight() + 6,
				watermarkPosX.getPreferredSize().width, 16);

		textWatermarkPosX = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
		textWatermarkPosX.setName("textWatermarkPosX");
		textWatermarkPosX.setEnabled(false);
		textWatermarkPosX.setBounds(watermarkPosX.getLocation().x + watermarkPosX.getWidth() + 2,
				watermarkPosX.getLocation().y, 34, 16);
		textWatermarkPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkPosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		JLabel px1 = new JLabel("px");
		px1.setEnabled(false);
		px1.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textWatermarkPosX.getLocation().x + textWatermarkPosX.getWidth() + 2, watermarkPosX.getY(),
				px1.getPreferredSize().width, 16);

		textWatermarkPosX.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textWatermarkPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / playerRatio),
							logo.getLocation().y);
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;

					watermarkPreset = null;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textWatermarkPosX.getText().length() >= 4)
					textWatermarkPosX.setText("");
			}

		});

		textWatermarkPosX.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				watermarkPreset = null;

				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}

		});

		textWatermarkPosX.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textWatermarkPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textWatermarkPosX
							.setText(String.valueOf(MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX)));
					logo.setLocation((int) Math.floor(Integer.valueOf(textWatermarkPosX.getText()) / playerRatio),
							logo.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel watermarkPosY = new JLabel(Main.language.getProperty("posY"));
		watermarkPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkPosY.setEnabled(false);
		watermarkPosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		watermarkPosY.setForeground(Utils.themeColor);
		watermarkPosY.setBounds(px1.getX() + px1.getWidth() + 30, watermarkPosX.getLocation().y,
				watermarkPosX.getWidth(), 16);

		textWatermarkPosY = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));
		textWatermarkPosY.setName("textWatermarkPosY");
		textWatermarkPosY.setEnabled(false);
		textWatermarkPosY.setBounds(watermarkPosY.getLocation().x + watermarkPosY.getWidth() + 2, watermarkPosX.getY(),
				34, 16);
		textWatermarkPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkPosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textWatermarkPosY.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textWatermarkPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					logo.setLocation(logo.getLocation().x,
							(int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / playerRatio));
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;

					watermarkPreset = null;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textWatermarkPosY.getText().length() >= 4)
					textWatermarkPosY.setText("");
			}

		});

		textWatermarkPosY.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				watermarkPreset = null;

				MouseLogoPosition.mouseY = e.getY();
				MouseLogoPosition.offsetY = Integer.parseInt(textWatermarkPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}

		});

		textWatermarkPosY.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textWatermarkPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textWatermarkPosY
							.setText(String.valueOf(MouseLogoPosition.offsetY + (e.getY() - MouseLogoPosition.mouseY)));
					logo.setLocation(logo.getLocation().x,
							(int) Math.floor(Integer.valueOf(textWatermarkPosY.getText()) / playerRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel px2 = new JLabel("px");
		px2.setEnabled(false);
		px2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textWatermarkPosY.getLocation().x + textWatermarkPosY.getWidth() + 2, watermarkPosX.getY(),
				watermarkPosX.getPreferredSize().width, 16);

		textWatermarkSize = new JTextField("100");
		textWatermarkSize.setName("textWatermarkSize");
		textWatermarkSize.setEnabled(false);
		textWatermarkSize.setBounds(textWatermarkPosX.getLocation().x,
				watermarkPosX.getY() + watermarkPosX.getHeight() + 4, 34, 16);
		textWatermarkSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkSize.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textWatermarkSize.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				watermarkPreset = null;

				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				textWatermarkPosX
						.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
				textWatermarkPosY
						.setText(String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));
				logoLocX = logo.getLocation().x;
				logoLocY = logo.getLocation().y;
			}

		});

		textWatermarkSize.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textWatermarkSize.getText().length() > 0) {
					VideoPlayerOverlay.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));

					textWatermarkPosX.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
					textWatermarkPosY.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textWatermarkSize.getText().length() >= 3)
					textWatermarkSize.setText("");
			}

		});

		textWatermarkSize.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textWatermarkSize.getText().length() > 0) {
					int value = MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX);

					if (value < 2) {
						textWatermarkSize.setText("2");
					} else
						textWatermarkSize.setText(String.valueOf(value));

					VideoPlayerOverlay.loadWatermark(Integer.parseInt(textWatermarkSize.getText()));
					textWatermarkPosX.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().x * playerRatio))));
					textWatermarkPosY.setText(
							String.valueOf(Integer.valueOf((int) Math.floor(logo.getLocation().y * playerRatio))));
					logoLocX = logo.getLocation().x;
					logoLocY = logo.getLocation().y;
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel watermarkSize = new JLabel(Main.language.getProperty("lblSize"));
		watermarkSize.setEnabled(false);
		watermarkSize.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkSize.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		watermarkSize.setForeground(Utils.themeColor);
		watermarkSize.setBounds(textWatermarkSize.getX() - watermarkSize.getPreferredSize().width - 2,
				watermarkPosX.getY() + watermarkPosX.getHeight() + 4, watermarkSize.getPreferredSize().width, 16);

		JLabel percent1 = new JLabel("%");
		percent1.setEnabled(false);
		percent1.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent1.setForeground(Utils.themeColor);
		percent1.setBounds(textWatermarkSize.getLocation().x + textWatermarkSize.getWidth() + 2, watermarkSize.getY(),
				percent1.getPreferredSize().width, 16);

		JLabel watermarkOpacity = new JLabel(Main.language.getProperty("lblOpacity"));
		watermarkOpacity.setEnabled(false);
		watermarkOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		watermarkOpacity.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		watermarkOpacity.setForeground(Utils.themeColor);
		watermarkOpacity.setBounds(textWatermarkPosY.getX() - watermarkOpacity.getPreferredSize().width - 2,
				textWatermarkPosY.getY() + textWatermarkPosY.getHeight() + 4, watermarkOpacity.getPreferredSize().width,
				16);

		textWatermarkOpacity = new JTextField("100");
		textWatermarkOpacity.setEnabled(false);
		textWatermarkOpacity.setName("textWatermarkOpacity");
		textWatermarkOpacity.setBounds(textWatermarkPosY.getLocation().x, watermarkOpacity.getY(), 34, 16);
		textWatermarkOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textWatermarkOpacity.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textWatermarkOpacity.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textWatermarkOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseLogoPosition.mouseX = e.getX();
				MouseLogoPosition.offsetX = Integer.parseInt(textWatermarkOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textWatermarkOpacity.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				logo.repaint();
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textWatermarkSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
						&& textWatermarkOpacity.getText().length() > 0) {
					if (Integer.valueOf(textWatermarkOpacity.getText()) > 100) {
						textWatermarkOpacity.setText("100");
					} else if (Integer.valueOf(textWatermarkOpacity.getText()) < 1) {
						textWatermarkOpacity.setText("1");
					}

					logo.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textWatermarkOpacity.getText().length() >= 3)
					textWatermarkOpacity.setText("");
			}

		});

		textWatermarkOpacity.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textWatermarkOpacity.getText().length() > 0) {
					int value = MouseLogoPosition.offsetX + (e.getX() - MouseLogoPosition.mouseX);

					if (value > 100) {
						textWatermarkOpacity.setText("100");
					} else if (value < 1) {
						textWatermarkOpacity.setText("1");
					} else
						textWatermarkOpacity.setText(String.valueOf(value));

					logo.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel percent2 = new JLabel("%");
		percent2.setEnabled(false);
		percent2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent2.setForeground(Utils.themeColor);
		percent2.setBounds(textWatermarkOpacity.getLocation().x + textWatermarkOpacity.getWidth() + 2,
				watermarkOpacity.getY(), percent2.getPreferredSize().width, 16);

		grpWatermark.add(watermarkPosX);
		grpWatermark.add(textWatermarkPosX);
		grpWatermark.add(px1);
		grpWatermark.add(watermarkPosY);
		grpWatermark.add(textWatermarkPosY);
		grpWatermark.add(px2);
		grpWatermark.add(watermarkSize);
		grpWatermark.add(textWatermarkSize);
		grpWatermark.add(percent1);
		grpWatermark.add(watermarkOpacity);
		grpWatermark.add(textWatermarkOpacity);
		grpWatermark.add(percent2);

		for (Component c : grpWatermark.getComponents()) {
			if (c instanceof JCheckBox == false) {
				c.setEnabled(false);
			}
		}
	}
}
