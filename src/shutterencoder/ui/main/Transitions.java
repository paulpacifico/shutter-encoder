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
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.library.FFprobe;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class Transitions extends Main {

	public static void grpTransitions() {

		grpTransitions = new CollapsiblePanel(language.getProperty("grpTransitions"), true);
		grpTransitions.setLayout(null);
		grpTransitions.setVisible(false);
		grpTransitions.setOpaque(false);
		grpTransitions.setBackground(Utils.c30);
		grpTransitions.setBounds(frame.getWidth(), grpCorrections.getY() + grpCorrections.getHeight() + 6, 312, 17);
		frame.getContentPane().add(grpTransitions);

		grpTransitions.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpTransitions, 104);
			}

		});

		// Input
		caseVideoFadeIn = new JCheckBox(Main.language.getProperty("lblVideoFadeIn"));
		caseVideoFadeIn.setName("caseVideoFadeIn");
		caseVideoFadeIn.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseVideoFadeIn.setBounds(7, 16, caseVideoFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeIn);

		caseVideoFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseVideoFadeIn.isSelected()) {
					spinnerVideoFadeIn.setEnabled(true);

					float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseInF.getText());

					VideoPlayerCore.playerCurrentFrame = timeIn;
					
					VideoPlayerUI.playTransition = true;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
				} else {
					spinnerVideoFadeIn.setEnabled(false);
				}
			}

		});

		spinnerVideoFadeIn = new JTextField("25");
		spinnerVideoFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeIn.setName("spinnerVideoFadeIn");
		spinnerVideoFadeIn.setEnabled(false);
		spinnerVideoFadeIn.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		if (getLanguage.equals(Locale.of("en").getDisplayLanguage()))
		{
			spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 12,
					caseVideoFadeIn.getLocation().y + 3, 41, 16);
		}
		else if (getLanguage.equals(Locale.of("bg").getDisplayLanguage()))
		{
			spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 26,
					caseVideoFadeIn.getLocation().y + 3, 41, 16);
		}
		else
			spinnerVideoFadeIn.setBounds(caseVideoFadeIn.getLocation().x + caseVideoFadeIn.getWidth() + 6,
					caseVideoFadeIn.getLocation().y + 3, 41, 16);
		grpTransitions.add(spinnerVideoFadeIn);

		spinnerVideoFadeIn.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		JLabel videoInFrames = new JLabel(Main.language.getProperty("lblFrames"));
		videoInFrames.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		videoInFrames.setBounds(spinnerVideoFadeIn.getLocation().x + spinnerVideoFadeIn.getWidth() + 4,
				spinnerVideoFadeIn.getY(), videoInFrames.getPreferredSize().width + 4, 16);
		grpTransitions.add(videoInFrames);

		lblFadeInColor = new JLabel(Main.language.getProperty("black"));
		lblFadeInColor.setName("lblFadeInColor");
		lblFadeInColor.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblFadeInColor.setBackground(Utils.c42);
		lblFadeInColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeInColor.setOpaque(true);
		lblFadeInColor.setFont(new Font(Main.boldFont, Font.PLAIN, 12));
		lblFadeInColor.setSize(55, 16);
		lblFadeInColor.setLocation(videoInFrames.getLocation().x + videoInFrames.getWidth() + 3,
				spinnerVideoFadeIn.getY() + 1);
		grpTransitions.add(lblFadeInColor);

		lblFadeInColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (lblFadeInColor.getText().equals(Main.language.getProperty("black")))
					lblFadeInColor.setText(Main.language.getProperty("white"));
				else
					lblFadeInColor.setText(Main.language.getProperty("black"));

				if (caseVideoFadeIn.isSelected())
				{
					float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseInF.getText());

					VideoPlayerCore.playerCurrentFrame = timeIn;
					
					VideoPlayerUI.playTransition = true;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		caseAudioFadeIn = new JCheckBox(Main.language.getProperty("lblAudioFadeIn"));
		caseAudioFadeIn.setName("caseAudioFadeIn");
		caseAudioFadeIn.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAudioFadeIn.setBounds(7, caseVideoFadeIn.getY() + 17, caseAudioFadeIn.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeIn);

		caseAudioFadeIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAudioFadeIn.isSelected()) {
					spinnerAudioFadeIn.setEnabled(true);

					float timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseInS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseInF.getText());

					VideoPlayerCore.playerCurrentFrame = timeIn;
					
					VideoPlayerUI.playTransition = true;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
				} else {
					spinnerAudioFadeIn.setEnabled(false);
				}

			}

		});

		spinnerAudioFadeIn = new JTextField("25");
		spinnerAudioFadeIn.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeIn.setName("spinnerAudioFadeIn");
		spinnerAudioFadeIn.setEnabled(false);
		spinnerAudioFadeIn.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		spinnerAudioFadeIn.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeIn.getLocation().y + 3,
				spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeIn);

		spinnerAudioFadeIn.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		JLabel audioInFrames = new JLabel(videoInFrames.getText());
		audioInFrames.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		audioInFrames.setBounds(spinnerAudioFadeIn.getLocation().x + spinnerAudioFadeIn.getWidth() + 4,
				spinnerAudioFadeIn.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioInFrames);

		// Output
		caseVideoFadeOut = new JCheckBox(Main.language.getProperty("lblVideoFadeOut"));
		caseVideoFadeOut.setName("caseVideoFadeOut");
		caseVideoFadeOut.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseVideoFadeOut.setBounds(7, caseAudioFadeIn.getY() + caseAudioFadeIn.getHeight(),
				caseVideoFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseVideoFadeOut);

		caseVideoFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (Main.inputDeviceIsRunning) {
					JOptionPane.showMessageDialog(frame, Main.language.getProperty("incompatibleInputDevice"),
							Main.language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseVideoFadeOut.setSelected(false);
				}

				if (caseVideoFadeOut.isSelected() && list.getSize() > 0) {
					spinnerVideoFadeOut.setEnabled(true);

					float timeOut = (Integer.parseInt(VideoPlayerUI.caseOutH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseOutM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseOutS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseOutF.getText());

					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue) {
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}

					VideoPlayerCore.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					// Allows to wait for the last frame to load
					long time = System.currentTimeMillis();

					if (VideoPlayerCore.playerIsPlaying() == false) {
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {
							}

							if (System.currentTimeMillis() - time > 1000)
								break;

						} while (VideoPlayerCore.setTime.isAlive());
					}

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
				} else {
					spinnerVideoFadeOut.setEnabled(false);
				}
			}

		});

		spinnerVideoFadeOut = new JTextField("25");
		spinnerVideoFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerVideoFadeOut.setName("spinnerVideoFadeOut");
		spinnerVideoFadeOut.setEnabled(false);
		spinnerVideoFadeOut.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		spinnerVideoFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseVideoFadeOut.getLocation().y + 3,
				spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerVideoFadeOut);

		spinnerVideoFadeOut.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		JLabel videoOutFrames = new JLabel(videoInFrames.getText());
		videoOutFrames.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		videoOutFrames.setBounds(spinnerVideoFadeOut.getLocation().x + spinnerVideoFadeOut.getWidth() + 4,
				spinnerVideoFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(videoOutFrames);

		lblFadeOutColor = new JLabel(Main.language.getProperty("black"));
		lblFadeOutColor.setName("lblFadeOutColor");
		lblFadeOutColor.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblFadeOutColor.setBackground(Utils.c42);
		lblFadeOutColor.setHorizontalAlignment(SwingConstants.CENTER);
		lblFadeOutColor.setOpaque(true);
		lblFadeOutColor.setFont(new Font(Main.boldFont, Font.PLAIN, 12));
		lblFadeOutColor.setSize(55, 16);
		lblFadeOutColor.setLocation(lblFadeInColor.getX(), spinnerVideoFadeOut.getY() + 1);
		grpTransitions.add(lblFadeOutColor);

		lblFadeOutColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (lblFadeOutColor.getText().equals(Main.language.getProperty("black")))
					lblFadeOutColor.setText(Main.language.getProperty("white"));
				else
					lblFadeOutColor.setText(Main.language.getProperty("black"));

				if (caseVideoFadeOut.isSelected()) {
					float timeOut = (Integer.parseInt(VideoPlayerUI.caseOutH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseOutM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseOutS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseOutF.getText());

					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue) {
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}

					VideoPlayerCore.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					// Allows to wait for the last frame to load
					long time = System.currentTimeMillis();

					if (VideoPlayerCore.playerIsPlaying() == false) {
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {
							}

							if (System.currentTimeMillis() - time > 1000)
								break;

						} while (VideoPlayerCore.setTime.isAlive());
					}

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		caseAudioFadeOut = new JCheckBox(Main.language.getProperty("lblAudioFadeOut"));
		caseAudioFadeOut.setName("caseAudioFadeOut");
		caseAudioFadeOut.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAudioFadeOut.setBounds(7, caseVideoFadeOut.getY() + 17, caseAudioFadeOut.getPreferredSize().width, 23);
		grpTransitions.add(caseAudioFadeOut);

		caseAudioFadeOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (Main.inputDeviceIsRunning) {
					JOptionPane.showMessageDialog(frame, Main.language.getProperty("incompatibleInputDevice"),
							Main.language.getProperty("menuItemScreenRecord"), JOptionPane.ERROR_MESSAGE);
					caseAudioFadeOut.setSelected(false);
				}

				if (caseAudioFadeOut.isSelected() && list.getSize() > 0) {
					spinnerAudioFadeOut.setEnabled(true);

					float timeOut = (Integer.parseInt(VideoPlayerUI.caseOutH.getText()) * 3600
							+ Integer.parseInt(VideoPlayerUI.caseOutM.getText()) * 60
							+ Integer.parseInt(VideoPlayerUI.caseOutS.getText())) * FFprobe.currentFPS
							+ Integer.parseInt(VideoPlayerUI.caseOutF.getText());

					int spinnerValue = Integer.parseInt(spinnerVideoFadeOut.getText());
					if (Integer.parseInt(spinnerAudioFadeOut.getText()) > spinnerValue) {
						spinnerValue = Integer.parseInt(spinnerAudioFadeOut.getText());
					}

					VideoPlayerCore.playerCurrentFrame = timeOut - spinnerValue * 2;
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);

					// Allows to wait for the last frame to load
					long time = System.currentTimeMillis();

					if (VideoPlayerCore.playerIsPlaying() == false) {
						do {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e1) {
							}

							if (System.currentTimeMillis() - time > 1000)
								break;

						} while (VideoPlayerCore.setTime.isAlive());
					}

					VideoPlayerUI.btnPlay.setIcon(new FlatSVGIcon("resources/pause.svg", 15, 15));
					VideoPlayerUI.btnPlay.setName("pause");
					VideoPlayerUI.playerLoop = true;
				} else {
					spinnerAudioFadeOut.setEnabled(false);
				}

			}

		});

		spinnerAudioFadeOut = new JTextField("25");
		spinnerAudioFadeOut.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerAudioFadeOut.setName("spinnerAudioFadeOut");
		spinnerAudioFadeOut.setEnabled(false);
		spinnerAudioFadeOut.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		spinnerAudioFadeOut.setBounds(spinnerVideoFadeIn.getX(), caseAudioFadeOut.getLocation().y + 3,
				spinnerVideoFadeIn.getWidth(), 16);
		grpTransitions.add(spinnerAudioFadeOut);

		spinnerAudioFadeOut.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		JLabel audioOutFrames = new JLabel(videoInFrames.getText());
		audioOutFrames.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		audioOutFrames.setBounds(spinnerAudioFadeOut.getLocation().x + spinnerAudioFadeOut.getWidth() + 4,
				spinnerAudioFadeOut.getY(), videoInFrames.getWidth(), 16);
		grpTransitions.add(audioOutFrames);

	}
}
