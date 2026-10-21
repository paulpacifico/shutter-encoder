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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.SystemFileChooser;

import shutterencoder.ui.handlers.DestinationFileTransferHandler;
import shutterencoder.ui.others.Ftp;
import shutterencoder.ui.others.Settings;
import shutterencoder.utils.Utils;

public class Destination extends Main {

	public static void grpDestination() {

		grpDestination = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		grpDestination.setBounds(12, grpChooseFunction.getY() + grpChooseFunction.getHeight() + 6, 308, 168);
		grpDestination.setBorder(BorderFactory.createTitledBorder(new FlatLineBorder(new Insets(1, 1, 1, 1), Utils.c42, 1, 10) ));
		grpDestination.setFont(new Font(boldFont, Font.PLAIN, 11));
		frame.getContentPane().add(grpDestination);

		// DESTINATION 1

		destination1 = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Utils.c25);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		destination1.setLayout(null);
		destination1.setOpaque(false);
		destination1.setFont(new Font(boldFont, Font.PLAIN, 12));

		caseOpenFolderAtEnd1 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd1.setName("caseOpenFolderAtEnd1");
		caseOpenFolderAtEnd1.setSelected(true);
		caseOpenFolderAtEnd1.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd1.setBounds(6, 23, caseOpenFolderAtEnd1.getPreferredSize().width, 23);
		destination1.add(caseOpenFolderAtEnd1);

		caseChangeFolder1 = new JCheckBox(language.getProperty("caseChangeFolder"));
		caseChangeFolder1.setName("caseChangeFolder1");
		caseChangeFolder1.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseChangeFolder1.setBounds(212, 23, 89, 23);
		destination1.add(caseChangeFolder1);

		caseChangeFolder1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (scanIsRunning)
				{
					caseChangeFolder1.setSelected(true);
				}
				else
				{
					if (caseChangeFolder1.isSelected() || inputDeviceIsRunning)
					{	
						File destination = null;
						
						SystemFileChooser fc = new SystemFileChooser();
						fc.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
						{
							fc.setCurrentDirectory(new File(Settings.lblDestination1.getText()));
						}
						else
							fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

						if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
						{
							destination = fc.getSelectedFile();
							
							// Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows")
									&& destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);

							if (destination.isFile())
								lblDestination1.setText(destination.getParent());
							else
								lblDestination1.setText(destination.toString());

							// Si destination identique à l'une des autres
							if (lblDestination1.getText().equals(lblDestination2.getText())
									|| lblDestination1.getText().equals(lblDestination3.getText())) {
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination1.setText(language.getProperty("sameAsSource"));
								caseChangeFolder1.setSelected(false);
							} else {
								caseOpenFolderAtEnd1.setSelected(false);

								if (scan.getText().equals(language.getProperty("menuItemStopScan"))
										&& list.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (list.firstElement().substring(0, list.firstElement().length() - 1)
											.equals(lblDestination1.getText())) {
										JOptionPane.showMessageDialog(frame,
												language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination1.setText(language.getProperty("sameAsSource"));
										caseChangeFolder1.setSelected(false);
										caseChangeFolder1.doClick();
									} else {
										scanIsRunning = true;
										UIController.changeFilters();
									}
								}
							}

							if (lblDestination1.getText() != language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(lblDestination1.getText());
						}
						else
						{
							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();

							if (inputDeviceIsRunning == false)
								caseChangeFolder1.setSelected(false);
						}
					} else {

						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
						|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
						|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
						|| inputDeviceIsRunning)
						{
							if (System.getProperty("os.name").contains("Windows"))
							{
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
						} else
							lblDestination1.setText(language.getProperty("sameAsSource"));
					}

					if (inputDeviceIsRunning)
						caseChangeFolder1.setSelected(true);

				}
			}
		});

		lblDestination1 = new JTextField() {

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				g2d.setRenderingHints(qualityHints);

				if (lblDestination1.getText().equals(language.getProperty("sameAsSource")) == false
						&& lblDestination1.getText().equals(language.getProperty("aucune")) == false) {
					File drive = new File(lblDestination1.getText());

					Integer fill = lblDestination1.getWidth()
							- ((int) ((float) ((float) drive.getFreeSpace() * lblDestination1.getWidth())
									/ drive.getTotalSpace()));

					Color c = new Color(75, 75, 75);
					if (fill > lblDestination1.getWidth() * 0.9) // Only 10% space left
						c = new Color(150, 75, 75);

					// Fill
					GradientPaint gp = new GradientPaint(0, 0, Utils.c30, fill, lblDestination1.getHeight(),
							c);
					g2d.setPaint(gp);
					g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination1.getHeight()));

					// Borders
					GradientPaint gp2 = new GradientPaint(0, 0, Utils.c30, lblDestination1.getWidth(),
							lblDestination1.getHeight(), new Color(75, 75, 75));
					g2d.setPaint(gp2);
					g2d.drawRoundRect(0, 0, lblDestination1.getWidth() - 1, lblDestination1.getHeight() - 1, 10, 10);

				}

				g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination1.getText(), 0, 15);

				lblDestination1.setToolTipText(lblDestination1.getText());
			}

		};

		lblDestination1.setName("lblDestination1");
		lblDestination1.setEditable(false);
		lblDestination1.setForeground(Utils.themeColor);
		lblDestination1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination1.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination1.setBackground(Utils.c30);
		lblDestination1.setText(language.getProperty("sameAsSource"));
		lblDestination1.setBounds(6, 0, 290, 22);
		destination1.add(lblDestination1);

		// DESTINATION 2

		destination2 = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Utils.c25);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		destination2.setLayout(null);
		destination2.setOpaque(false);
		destination2.setFont(new Font(boldFont, Font.PLAIN, 12));

		caseOpenFolderAtEnd2 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd2.setName("caseOpenFolderAtEnd2");
		caseOpenFolderAtEnd2.setSelected(false);
		caseOpenFolderAtEnd2.setEnabled(false);
		caseOpenFolderAtEnd2.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd2.setBounds(6, 23, caseOpenFolderAtEnd2.getPreferredSize().width, 23);
		destination2.add(caseOpenFolderAtEnd2);

		caseChangeFolder2 = new JCheckBox(language.getProperty("caseChangeFolder"));
		caseChangeFolder2.setName("caseChangeFolder2");
		caseChangeFolder2.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseChangeFolder2.setBounds(212, 23, 89, 23);
		destination2.add(caseChangeFolder2);

		caseChangeFolder2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (scanIsRunning) 
				{
					caseChangeFolder2.setSelected(true);
				}
				else
				{
					if (caseChangeFolder2.isSelected())
					{
						File destination = null;
						
						SystemFileChooser fc = new SystemFileChooser();
						fc.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination2.getText() != "" && new File(Settings.lblDestination2.getText()).exists())
						{
							fc.setCurrentDirectory(new File(Settings.lblDestination2.getText()));
						}
						else
							fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

						if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
						{	
							destination = fc.getSelectedFile();
							
							// Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows")
									&& destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);

							if (destination.isFile())
								lblDestination2.setText(destination.getParent());
							else
								lblDestination2.setText(destination.toString());

							// Si destination identique à l'une des autres
							if (lblDestination2.getText().equals(lblDestination1.getText())
									|| lblDestination2.getText().equals(lblDestination3.getText())) {
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination2.setText(language.getProperty("aucune"));
								caseChangeFolder2.setSelected(false);

								lblDestination3.setText(language.getProperty("aucune"));
								caseOpenFolderAtEnd3.setSelected(false);
								caseOpenFolderAtEnd3.setEnabled(false);
								caseChangeFolder3.setSelected(false);
								caseChangeFolder3.setEnabled(false);
							} else {
								caseOpenFolderAtEnd2.setSelected(false);
								caseOpenFolderAtEnd2.setEnabled(true);

								caseChangeFolder3.setEnabled(true);

								if (scan.getText().equals(language.getProperty("menuItemStopScan"))
										&& list.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (list.firstElement().substring(0, list.firstElement().length() - 1)
											.equals(lblDestination2.getText())) {
										JOptionPane.showMessageDialog(frame,
												language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination2.setText(language.getProperty("aucune"));
										caseChangeFolder2.setSelected(false);
										caseChangeFolder2.doClick();
									} else {
										scanIsRunning = true;
										UIController.changeFilters();
									}
								}
							}

							if (lblDestination2.getText() != language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(lblDestination2.getText());

						}
						else
						{
							caseChangeFolder2.setSelected(false);

							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();
						}
					} else {
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
								|| inputDeviceIsRunning) {
							if (System.getProperty("os.name").contains("Mac")
									|| System.getProperty("os.name").contains("Linux"))
								lblDestination2.setText(System.getProperty("user.home") + "/Desktop");
							else
								lblDestination2.setText(System.getProperty("user.home") + "\\Desktop");
						} else
							lblDestination2.setText(language.getProperty("aucune"));

						caseOpenFolderAtEnd2.setSelected(false);
						caseOpenFolderAtEnd2.setEnabled(false);

						lblDestination3.setText(language.getProperty("aucune"));
						caseOpenFolderAtEnd3.setSelected(false);
						caseOpenFolderAtEnd3.setEnabled(false);
						caseChangeFolder3.setSelected(false);
						caseChangeFolder3.setEnabled(false);
					}
				}
			}
		});

		lblDestination2 = new JTextField() {

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				g2d.setRenderingHints(qualityHints);

				if (lblDestination2.getText().equals(language.getProperty("sameAsSource")) == false
						&& lblDestination2.getText().equals(language.getProperty("aucune")) == false) {
					File drive = new File(lblDestination2.getText());

					Integer fill = lblDestination2.getWidth()
							- ((int) ((float) ((float) drive.getFreeSpace() * lblDestination2.getWidth())
									/ drive.getTotalSpace()));

					Color c = new Color(75, 75, 75);
					if (fill > lblDestination2.getWidth() * 0.9) // Only 10% space left
						c = new Color(150, 75, 75);

					// Fill
					GradientPaint gp = new GradientPaint(0, 0, Utils.c30, fill, lblDestination2.getHeight(),
							c);
					g2d.setPaint(gp);
					g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination2.getHeight()));

					// Borders
					GradientPaint gp2 = new GradientPaint(0, 0, Utils.c30, lblDestination2.getWidth(),
							lblDestination2.getHeight(), new Color(75, 75, 75));
					g2d.setPaint(gp2);
					g2d.drawRoundRect(0, 0, lblDestination2.getWidth() - 1, lblDestination2.getHeight() - 1, 10, 10);
				}

				g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination2.getText(), 0, 15);

				lblDestination2.setToolTipText(lblDestination2.getText());
			}

		};

		lblDestination2.setName("lblDestination2");
		lblDestination2.setEditable(false);
		lblDestination2.setForeground(Utils.themeColor);
		lblDestination2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination2.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination2.setBackground(Utils.c30);
		lblDestination2.setText(language.getProperty("aucune"));
		lblDestination2.setBounds(6, 0, 290, 22);
		destination2.add(lblDestination2);

		// DESTINATION 3

		destination3 = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Utils.c25);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		destination3.setLayout(null);
		destination3.setOpaque(false);
		destination3.setFont(new Font(boldFont, Font.PLAIN, 12));

		caseOpenFolderAtEnd3 = new JCheckBox(language.getProperty("caseOpenFolderAtEnd"));
		caseOpenFolderAtEnd3.setName("caseOpenFolderAtEnd3");
		caseOpenFolderAtEnd3.setSelected(false);
		caseOpenFolderAtEnd3.setEnabled(false);
		caseOpenFolderAtEnd3.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseOpenFolderAtEnd3.setBounds(6, 23, caseOpenFolderAtEnd3.getPreferredSize().width, 23);
		destination3.add(caseOpenFolderAtEnd3);

		caseChangeFolder3 = new JCheckBox(language.getProperty("caseChangeFolder"));
		caseChangeFolder3.setName("caseChangeFolder3");
		caseChangeFolder3.setEnabled(false);
		caseChangeFolder3.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseChangeFolder3.setBounds(212, 23, 89, 23);
		destination3.add(caseChangeFolder3);

		caseChangeFolder3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (scanIsRunning)
				{
					caseChangeFolder3.setSelected(true);
				}
				else
				{
					if (caseChangeFolder3.isSelected())
					{
						File destination = null;
						
						SystemFileChooser fc = new SystemFileChooser();
						fc.setFileSelectionMode(SystemFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination3.getText() != "" && new File(Settings.lblDestination3.getText()).exists())
						{
							fc.setCurrentDirectory(new File(Settings.lblDestination3.getText()));
						}
						else
							fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

						if (fc.showOpenDialog(frame) == SystemFileChooser.APPROVE_OPTION)
						{	
							destination = fc.getSelectedFile();

							// Montage du chemin UNC
							if (System.getProperty("os.name").contains("Windows")
									&& destination.toString().substring(0, 2).equals("\\\\"))
								destination = Utils.UNCPath(destination);

							if (destination.isFile())
								lblDestination3.setText(destination.getParent());
							else
								lblDestination3.setText(destination.toString());

							// Si destination identique à l'une des autres
							if (lblDestination3.getText().equals(lblDestination1.getText())
									|| lblDestination3.getText().equals(lblDestination2.getText())) {
								JOptionPane.showMessageDialog(frame, language.getProperty("ChooseDifferentFolder"),
										language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
								lblDestination3.setText(language.getProperty("aucune"));
								caseChangeFolder3.setSelected(false);
							} else {
								caseOpenFolderAtEnd3.setSelected(false);
								caseOpenFolderAtEnd3.setEnabled(true);

								if (scan.getText().equals(language.getProperty("menuItemStopScan"))
										&& list.getSize() > 0) {
									// Si le dossier d'entrée et de sortie est identique
									if (list.firstElement().substring(0, list.firstElement().length() - 1)
											.equals(lblDestination3.getText())) {
										JOptionPane.showMessageDialog(frame,
												language.getProperty("ChooseDifferentFolder"),
												language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
										lblDestination3.setText(language.getProperty("aucune"));
										caseChangeFolder3.setSelected(false);
										caseChangeFolder3.doClick();
									} else {
										scanIsRunning = true;
										UIController.changeFilters();
									}
								}
							}

							if (lblDestination3.getText() != language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(lblDestination3.getText());

						} 
						else
						{
							caseChangeFolder3.setSelected(false);

							if (scan.getText().equals(language.getProperty("menuItemStopScan")))
								btnEmptyList.doClick();
						}
					}
					else
					{
						if (comboFonctions.getSelectedItem().equals(language.getProperty("functionWeb"))
								|| comboFonctions.getSelectedItem().toString().equals("DVD Rip")
								|| comboFonctions.getSelectedItem().toString().equals("CD RIP")
								|| inputDeviceIsRunning) {
							if (System.getProperty("os.name").contains("Mac")
									|| System.getProperty("os.name").contains("Linux"))
								lblDestination3.setText(System.getProperty("user.home") + "/Desktop");
							else
								lblDestination3.setText(System.getProperty("user.home") + "\\Desktop");
						} else
							lblDestination3.setText(language.getProperty("aucune"));

						caseOpenFolderAtEnd3.setSelected(false);
						caseOpenFolderAtEnd3.setEnabled(false);
					}
				}
			}
		});

		lblDestination3 = new JTextField() {

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				g2d.setRenderingHints(qualityHints);

				if (lblDestination3.getText().equals(language.getProperty("sameAsSource")) == false
						&& lblDestination3.getText().equals(language.getProperty("aucune")) == false) {
					File drive = new File(lblDestination3.getText());

					Integer fill = lblDestination3.getWidth()
							- ((int) ((float) ((float) drive.getFreeSpace() * lblDestination3.getWidth())
									/ drive.getTotalSpace()));

					Color c = new Color(75, 75, 75);
					if (fill > lblDestination3.getWidth() * 0.9) // Only 10% space left
						c = new Color(150, 75, 75);

					// Fill
					GradientPaint gp = new GradientPaint(0, 0, Utils.c30, fill, lblDestination3.getHeight(),
							c);
					g2d.setPaint(gp);
					g2d.fill(new Rectangle2D.Double(0, 0, fill, lblDestination3.getHeight()));

					// Borders
					GradientPaint gp2 = new GradientPaint(0, 0, Utils.c30, lblDestination3.getWidth(),
							lblDestination3.getHeight(), new Color(75, 75, 75));
					g2d.setPaint(gp2);
					g2d.drawRoundRect(0, 0, lblDestination3.getWidth() - 1, lblDestination3.getHeight() - 1, 10, 10);
				}

				g2d.setPaint(Utils.themeColor);
				g2d.drawString(lblDestination3.getText(), 0, 15);

				lblDestination3.setToolTipText(lblDestination3.getText());
			}

		};

		lblDestination3.setName("lblDestination3");
		lblDestination3.setEditable(false);
		lblDestination3.setForeground(Utils.themeColor);
		lblDestination3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination3.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination3.setBackground(Utils.c30);
		lblDestination3.setText(language.getProperty("aucune"));
		lblDestination3.setBounds(6, 0, 290, 22);
		destination3.add(lblDestination3);

		// MAIL
		destinationMail = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Utils.c25);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		destinationMail.setLayout(null);
		destinationMail.setOpaque(false);
		destinationMail.setFont(new Font(boldFont, Font.PLAIN, 12));

		caseSendMail = new JCheckBox(Main.language.getProperty("caseSendMail"));
		caseSendMail.setName("caseSendMail");
		caseSendMail.setSelected(false);
		caseSendMail.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseSendMail.setBounds(6, 1, caseSendMail.getPreferredSize().width, 23);
		destinationMail.add(caseSendMail);

		caseSendMail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseSendMail.isSelected()) {
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					if (textMail.getText().length() <= 0)
						textMail.setText(language.getProperty("textMail"));
					else if (textMail.getText().equals(language.getProperty("textMail")) == false) {
						textMail.setFont(new Font("SansSerif", Font.PLAIN, 12));
						textMail.setForeground(Color.WHITE);
					}
					textMail.setEnabled(true);
				} else {
					textMail.setEnabled(false);
				}
			}
		});

		textMail = new JTextField();
		textMail.setName("textMail");
		textMail.setEnabled(false);
		textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textMail.setForeground(Color.LIGHT_GRAY);
		textMail.setBounds(6, 24, 290, 21);
		textMail.setColumns(1);
		textMail.setText(language.getProperty("textMail"));
		destinationMail.add(textMail);

		textMail.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (textMail.getText().equals(language.getProperty("textMail"))) {
					textMail.setText("");
					textMail.setFont(new Font("SansSerif", Font.PLAIN, 12));
					textMail.setForeground(Color.WHITE);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textMail.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMail.setForeground(Color.LIGHT_GRAY);
					textMail.setText(language.getProperty("textMail"));
					caseSendMail.setSelected(false);
					textMail.setEnabled(false);
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER)
					grpDestination.setSelectedIndex(0);

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		// Stream
		destinationStream = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Utils.c25);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		destinationStream.setLayout(null);
		destinationStream.setOpaque(false);
		destinationStream.setFont(new Font(boldFont, Font.PLAIN, 12));

		caseStream = new JCheckBox(Main.language.getProperty("caseStream"));
		caseStream.setName("caseStream");
		caseStream.setSelected(false);
		caseStream.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseStream.setBounds(6, 1, caseStream.getPreferredSize().width, 23);
		destinationStream.add(caseStream);

		caseLoop = new JCheckBox(Main.language.getProperty("caseLoop"));
		caseLoop.setName("caseLoop");
		caseLoop.setEnabled(false);
		caseLoop.setSelected(false);
		caseLoop.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseLoop.setBounds(caseStream.getX() + caseStream.getWidth() + 7, caseStream.getY(), caseLoop.getPreferredSize().width, 23);
		destinationStream.add(caseLoop);

		caseStream.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseStream.isSelected()) {
					textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textStream.setForeground(Color.LIGHT_GRAY);
					if (textStream.getText().length() <= 0)
						textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
					else if (textStream.getText()
							.equals("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx") == false) {
						textStream.setFont(new Font("SansSerif", Font.PLAIN, 12));
						textStream.setForeground(Color.WHITE);
					}

					textStream.setEnabled(true);
					caseLoop.setEnabled(true);
				} else {
					textStream.setEnabled(false);
					caseLoop.setEnabled(false);
				}
			}
		});

		textStream = new JTextField();
		textStream.setName("textStream");
		textStream.setEnabled(false);
		textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textStream.setForeground(Color.LIGHT_GRAY);
		textStream.setBounds(6, 24, 290, 21);
		textStream.setColumns(1);
		textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
		destinationStream.add(textStream);

		textStream.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (textStream.getText().equals("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx")) {
					textStream.setText("");
					textStream.setFont(new Font("SansSerif", Font.PLAIN, 12));
					textStream.setForeground(Color.WHITE);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textStream.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textStream.setForeground(Color.LIGHT_GRAY);
					textStream.setText("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx");
					caseStream.setSelected(false);
					textStream.setEnabled(false);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		// Destinations icons
		mailIcon = new FlatSVGIcon("resources/mail.svg", 18, 18);
		streamIcon = new FlatSVGIcon("resources/stream.svg", 18, 18);

		// Ajout des tabs
		UIController.setDestinationTabs(5);

		casePrefix = new JCheckBox(Main.language.getProperty("casePrefix"));
		casePrefix.setName("casePrefix");
		casePrefix.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		casePrefix.setBounds(6, caseOpenFolderAtEnd1.getLocation().y + caseOpenFolderAtEnd1.getHeight(),
				casePrefix.getPreferredSize().width, 23);
		destination1.add(casePrefix);

		txtPrefix = new JTextField();
		txtPrefix.setName("txtPrefix");
		txtPrefix.setColumns(10);
		txtPrefix.setEnabled(false);
		txtPrefix.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtPrefix.setBounds(casePrefix.getLocation().x + casePrefix.getWidth() + 6, casePrefix.getLocation().y + 1,
				grpDestination.getWidth() - (casePrefix.getLocation().x + casePrefix.getWidth()) - 18, 21);
		txtPrefix.setToolTipText(
				"<html>{codec/function}<br>{preset}<br>{resolution/scale}<br>{width}<br>{height}<br>{ratio/aspect}<br>{framerate/fps}<br>{bitrate}<br>{timecode}<br>{duration/time}<br>{date}</html>");
		destination1.add(txtPrefix);

		casePrefix.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (casePrefix.isSelected()) {
					txtPrefix.setEnabled(true);

					ToolTipManager.sharedInstance().setInitialDelay(0);
					ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(txtPrefix, MouseEvent.MOUSE_MOVED,
							System.currentTimeMillis(), 0, 10, 10, 0, false));
					ToolTipManager.sharedInstance().setInitialDelay(750); // Reset to default delay
				} else {
					txtPrefix.setEnabled(false);
				}
			}

		});

		btnExtension = new JCheckBox(Main.language.getProperty("btnExtension"));
		btnExtension.setName("btnExtension");
		btnExtension.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		btnExtension.setBounds(6, casePrefix.getLocation().y + casePrefix.getHeight(),
				btnExtension.getPreferredSize().width, 23);
		destination1.add(btnExtension);

		txtExtension = new JTextField();
		txtExtension.setName("txtExtension");
		txtExtension.setColumns(10);
		txtExtension.setEnabled(false);
		txtExtension.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtExtension.setBounds(btnExtension.getLocation().x + btnExtension.getWidth() + 6,
				btnExtension.getLocation().y + 1,
				grpDestination.getWidth() - (btnExtension.getLocation().x + btnExtension.getWidth()) - 18, 21);
		txtExtension.setToolTipText(
				"<html>{codec/function}<br>{preset}<br>{resolution/scale}<br>{width}<br>{height}<br>{ratio/aspect}<br>{framerate/fps}<br>{bitrate}<br>{timecode}<br>{duration/time}<br>{date}</html>");
		destination1.add(txtExtension);

		btnExtension.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (btnExtension.isSelected()) {
					txtExtension.setEnabled(true);

					ToolTipManager.sharedInstance().setInitialDelay(0);
					ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(txtExtension, MouseEvent.MOUSE_MOVED,
							System.currentTimeMillis(), 0, 10, 10, 0, false));
					ToolTipManager.sharedInstance().setInitialDelay(750); // Reset to default delay

					if (txtExtension.getText().isEmpty()) {
						String extensionName = "_" + comboFonctions.getSelectedItem().toString().replace(" ", "_");

						if (comboFilter.getSelectedItem().toString().contains(".") == false
								&& comboFilter.getSelectedItem().toString()
										.equals(language.getProperty("aucun")) == false
								&& comboFilter.getSelectedItem().toString().contains("/") == false) {
							extensionName += "_" + comboFilter.getSelectedItem().toString().replace(" ", "_");
						}

						if ((Main.caseAddTimecode.isSelected() || Main.caseShowTimecode.isSelected())) {
							extensionName += "_TC";
						}

						txtExtension.setText(extensionName);
					}
				} else {
					txtExtension.setEnabled(false);

					if (txtExtension.getText()
							.contains(comboFonctions.getSelectedItem().toString().replace(" ", "_"))) {
						txtExtension.setText("");
					}
				}
			}

		});

		caseSubFolder = new JCheckBox(Main.language.getProperty("caseSubFolder"));
		caseSubFolder.setName("caseSubFolder");
		caseSubFolder.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseSubFolder.setBounds(6, btnExtension.getY() + btnExtension.getHeight(),
				caseSubFolder.getPreferredSize().width, 23);
		destination1.add(caseSubFolder);

		txtSubFolder = new JTextField();
		txtSubFolder.setName("txtSubFolder");
		txtSubFolder.setColumns(10);
		txtSubFolder.setEnabled(false);
		txtSubFolder.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtSubFolder.setBounds(caseSubFolder.getLocation().x + caseSubFolder.getWidth() + 6,
				caseSubFolder.getLocation().y + 1,
				grpDestination.getWidth() - (caseSubFolder.getLocation().x + caseSubFolder.getWidth()) - 18, 21);
		destination1.add(txtSubFolder);

		caseSubFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseSubFolder.isSelected()) {
					txtSubFolder.setEnabled(true);
				} else {
					txtSubFolder.setEnabled(false);
				}
			}

		});

		caseDeleteSourceFile = new JCheckBox(Main.language.getProperty("caseDeleteSourceFile"));
		caseDeleteSourceFile.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseDeleteSourceFile.setBounds(6, caseSubFolder.getY() + caseSubFolder.getHeight(),
				caseDeleteSourceFile.getPreferredSize().width, 23);
		destination1.add(caseDeleteSourceFile);

		caseDeleteSourceFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseDeleteSourceFile.isSelected()) {
					int reply = JOptionPane.showConfirmDialog(frame, Main.language.getProperty("areYouSure"),
							Main.language.getProperty("caseDeleteSourceFile"), JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE);

					if (reply == JOptionPane.NO_OPTION) {
						caseDeleteSourceFile.setSelected(false);
						caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
					} else
						caseDeleteSourceFile.setForeground(Utils.red);
				} else {
					caseDeleteSourceFile.setForeground(caseSubFolder.getForeground());
				}

			}

		});

		grpDestination.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				try {

					if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString()
							.equals(language.getProperty("output"))) {
						destination1.add(casePrefix);
						destination1.add(txtPrefix);
						destination1.add(btnExtension);
						destination1.add(txtExtension);
						destination1.add(caseSubFolder);
						destination1.add(txtSubFolder);
						destination1.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString()
							.equals(language.getProperty("output") + "1")) {
						destination1.add(casePrefix);
						destination1.add(txtPrefix);
						destination1.add(btnExtension);
						destination1.add(txtExtension);
						destination1.add(caseSubFolder);
						destination1.add(txtSubFolder);
						destination1.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString()
							.equals(language.getProperty("output") + "2")) {
						destination2.add(casePrefix);
						destination2.add(txtPrefix);
						destination2.add(btnExtension);
						destination2.add(txtExtension);
						destination2.add(caseSubFolder);
						destination2.add(txtSubFolder);
						destination2.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString()
							.equals(language.getProperty("output") + "3")) {
						destination3.add(casePrefix);
						destination3.add(txtPrefix);
						destination3.add(btnExtension);
						destination3.add(txtExtension);
						destination3.add(caseSubFolder);
						destination3.add(txtSubFolder);
						destination3.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("FTP")) {
						grpDestination.setSelectedComponent(destinationMail);
						try {
							frame.setOpacity(0.5f);
						} catch (Exception er) {
						}
						new Ftp();
						frame.setOpacity(1.0f);

						destinationMail.add(casePrefix);
						destinationMail.add(txtPrefix);
						destinationMail.add(btnExtension);
						destinationMail.add(txtExtension);
						destinationMail.add(caseSubFolder);
						destinationMail.add(txtSubFolder);
						destinationMail.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString().equals("Mail")) {
						destinationMail.add(casePrefix);
						destinationMail.add(txtPrefix);
						destinationMail.add(btnExtension);
						destinationMail.add(txtExtension);
						destinationMail.add(caseSubFolder);
						destinationMail.add(txtSubFolder);
						destinationMail.add(caseDeleteSourceFile);
					} else if (grpDestination.getTitleAt(grpDestination.getSelectedIndex()).toString()
							.equals("Stream")) {
						destinationStream.add(casePrefix);
						destinationStream.add(txtPrefix);
						destinationStream.add(btnExtension);
						destinationStream.add(txtExtension);
						destinationStream.add(caseSubFolder);
						destinationStream.add(txtSubFolder);
						destinationStream.add(caseDeleteSourceFile);
					}

				} catch (Exception e) {
				}
			}

		});

		popupDestination = new JPopupMenu();
		final JMenuItem openFolder = new JMenuItem(language.getProperty("menuItemOpenFolder"));
		popupDestination.add(openFolder);

		openFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				switch (grpDestination.getSelectedIndex())
				{
					case 0:
						Utils.openFile(new File(lblDestination1.getText()));
						break;
					case 1:
						Utils.openFile(new File(lblDestination2.getText()));
						break;
					case 2:
						Utils.openFile(new File(lblDestination3.getText()));
						break;
				}
			}
		});

		// Drag & Drop
		lblDestination1.setTransferHandler(new DestinationFileTransferHandler());
		lblDestination2.setTransferHandler(new DestinationFileTransferHandler());
		lblDestination3.setTransferHandler(new DestinationFileTransferHandler());

		MouseListener mouseListener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)
						|| (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && SwingUtilities.isLeftMouseButton(e)) {

					switch (grpDestination.getSelectedIndex()) {
					case 0:
						if (lblDestination1.getText().equals(language.getProperty("sameAsSource")) == false)
							openFolder.setVisible(true);
						else
							openFolder.setVisible(false);

						popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
						break;
					case 1:
						if (lblDestination2.getText().equals(language.getProperty("aucune")) == false) {
							openFolder.setVisible(true);
							popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
						} else
							openFolder.setVisible(false);

						break;
					case 2:
						if (lblDestination3.getText().equals(language.getProperty("aucune")) == false) {
							openFolder.setVisible(true);
							popupDestination.show(grpDestination, e.getX() - 25, e.getY() + 10);
						} else
							openFolder.setVisible(false);

						break;
					}

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
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
			}

		};

		destination1.addMouseListener(mouseListener);

		for (Component component : destination1.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination1.setToolTipText(language.getProperty("rightClick"));

		destination2.addMouseListener(mouseListener);

		for (Component component : destination2.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination2.setToolTipText(language.getProperty("rightClick"));

		destination3.addMouseListener(mouseListener);

		for (Component component : destination3.getComponents()) {
			component.addMouseListener(mouseListener);
		}

		destination3.setToolTipText(language.getProperty("rightClick"));

	}
}
