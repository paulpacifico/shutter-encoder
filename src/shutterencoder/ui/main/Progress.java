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
import java.awt.MouseInfo;
import java.awt.Taskbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shutterencoder.library.FFMPEG;
import shutterencoder.ui.handlers.GlobalKeyListener;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.utils.Utils;

public class Progress extends Main {

	public static void grpProgress() {

		grpProgression = new CollapsiblePanel(language.getProperty("grpProgression"), false);
		grpProgression.setLayout(null);
		grpProgression.setOpaque(false);
		grpProgression.setBounds(10, grpDestination.getY() + grpDestination.getHeight() + 6, 312, 94);
		grpProgression.setBackground(Utils.c30);
		grpProgression.setToolTipText(language.getProperty("rightClick"));
		frame.getContentPane().add(grpProgression);

		caseRunInBackground = new JCheckBox(language.getProperty("caseRunInBackground"));
		caseRunInBackground.setName("caseRunInBackground");
		caseRunInBackground.setEnabled(false);
		caseRunInBackground.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseRunInBackground.setBounds(9, 64, caseRunInBackground.getPreferredSize().width, 23);
		grpProgression.add(caseRunInBackground);

		// Inactivité
		caseRunInBackground.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseRunInBackground.isSelected()) {

					Thread thread = new Thread(new Runnable() {
						boolean running = true;

						@Override
						public void run() {

							// Appuie clavier en dehors de l'application
							GlobalKeyListener.main(null);

							int x = MouseInfo.getPointerInfo().getLocation().x;
							int y = MouseInfo.getPointerInfo().getLocation().y;
							long lastInputTime = System.currentTimeMillis();
							do {
								long timeSinceLastInput = System.currentTimeMillis() - lastInputTime;

								if (x != MouseInfo.getPointerInfo().getLocation().x
										|| y != MouseInfo.getPointerInfo().getLocation().y
										|| GlobalKeyListener.keyIsPressed) {
									lastInputTime = System.currentTimeMillis();
									x = MouseInfo.getPointerInfo().getLocation().x;
									y = MouseInfo.getPointerInfo().getLocation().y;
								}
								if (timeSinceLastInput >= 1000) {
									if (running == false)
										FFMPEG.resumeProcess();
									running = true;
								} else {

									lblRemainingTime.setText(Main.language.getProperty("timePause"));
									lblRemainingTime.setSize(lblRemainingTime.getPreferredSize().width, 15);

									if (running == true)
										FFMPEG.suspendProcess();
									running = false;
								}

								if (caseRunInBackground.isSelected() == false)
									FFMPEG.resumeProcess();

							} while (caseRunInBackground.isSelected()
									&& btnStart.getText().equals(Main.language.getProperty("btnPauseFunction")));

						}

					});
					thread.start();
				}
			}

		});

		caseDisplay = new JCheckBox(language.getProperty("menuItemVisualiser"));
		caseDisplay.setName("caseDisplay");
		caseDisplay.setEnabled(false);
		caseDisplay.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseDisplay.setBounds(215, 64, caseDisplay.getPreferredSize().width, 23);
		grpProgression.add(caseDisplay);

		progressBar = new JProgressBar();
		progressBar.setBounds(6, 42, 300, 14);
		progressBar.setFont(new Font(boldFont, Font.PLAIN, 12));
		progressBar.setStringPainted(true);
		grpProgression.add(progressBar);

		progressBar.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (System.getProperty("os.name").contains("Windows") && Taskbar.isTaskbarSupported()) {
					if (progressBar.isIndeterminate())
						Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.INDETERMINATE);
					else
						Taskbar.getTaskbar().setWindowProgressState(frame, Taskbar.State.NORMAL);

					if (progressBar.getValue() > 0)
						Taskbar.getTaskbar().setWindowProgressValue(frame,
								(100 * progressBar.getValue()) / progressBar.getMaximum());
					else
						Taskbar.getTaskbar().setWindowProgressValue(frame, 0);
				}
			}

		});

		lblCurrentEncoding = new JLabel(language.getProperty("lblEncodageEnCours"));
		lblCurrentEncoding.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
		lblCurrentEncoding.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblCurrentEncoding.setBounds(6, 19, 300, 16);
		grpProgression.add(lblCurrentEncoding);

		popupProgression = new JPopupMenu();
		JMenuItem console = new JMenuItem(language.getProperty("menuItemConsole"));
		popupProgression.add(console);

		console.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Console.frmConsole != null) {
					if (Console.frmConsole.isVisible())
						Console.frmConsole.toFront();
					else
						new Console();
				} else
					new Console();
			}
		});

		grpProgression.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)
						|| (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && SwingUtilities.isLeftMouseButton(e))
					popupProgression.show(grpProgression, e.getX() - 30, e.getY());
			}

		});
	}
}
