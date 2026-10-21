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

package shutterencoder.ui.handlers;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import shutterencoder.ui.main.Main;
import shutterencoder.ui.others.Settings;
import shutterencoder.utils.Utils;

// Drag & Drop lblDestination
@SuppressWarnings("serial")
public class DestinationFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent comp, DataFlavor[] arg1) {

		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];

			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				if (comp.getName().equals("lblDestination3") && Main.caseChangeFolder2.isSelected() == false) {
					return false;
				} else {
					comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
					return true;
				}
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {

		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];

			try {

				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();

					while (iter.hasNext()) {
						File file = (File) iter.next();

						// Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows")
								&& file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);

						if (file.getName().contains(".")) {
							((JTextField) comp).setText(file.getParent());
						} else
							((JTextField) comp).setText(file.getAbsolutePath());

						if (comp.getName().equals("lblDestination1")) {
							// Si destination identique à l'une des autres
							if (Main.lblDestination1.getText().equals(Main.lblDestination2.getText())
									|| Main.lblDestination1.getText().equals(Main.lblDestination3.getText())) {
								JOptionPane.showMessageDialog(Main.frame,
										Main.language.getProperty("ChooseDifferentFolder"),
										Main.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Main.lblDestination1.setText(Main.language.getProperty("sameAsSource"));
								Main.caseChangeFolder1.setSelected(false);
							} else {
								Main.caseChangeFolder1.setSelected(true);
								Main.caseOpenFolderAtEnd1.setSelected(false);
							}

							if (Main.scan.getText().equals(Main.language.getProperty("menuItemStopScan"))) {
								// Si le dossier d'entrée et de sortie est identique
								if (Main.list.firstElement().substring(0, Main.list.firstElement().length() - 1)
										.equals(Main.lblDestination1.getText())) {
									JOptionPane.showMessageDialog(Main.frame,
											Main.language.getProperty("ChooseDifferentFolder"),
											Main.language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
									Main.lblDestination1.setText(Main.language.getProperty("sameAsSource"));
									Main.caseChangeFolder1.setSelected(false);
								} else {
									Main.scanIsRunning = true;
								}
							}

							if (Main.lblDestination1.getText() != Main.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(Main.lblDestination1.getText());
						} else if (comp.getName().equals("lblDestination2")) {
							// Si destination identique à l'une des autres
							if (Main.lblDestination2.getText().equals(Main.lblDestination1.getText())
									|| Main.lblDestination2.getText().equals(Main.lblDestination3.getText())) {
								JOptionPane.showMessageDialog(Main.frame,
										Main.language.getProperty("ChooseDifferentFolder"),
										Main.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Main.lblDestination2.setText(Main.language.getProperty("aucune"));
								Main.caseChangeFolder2.setSelected(false);
								Main.caseOpenFolderAtEnd2.setSelected(false);
								Main.caseOpenFolderAtEnd2.setEnabled(false);
							} else {
								Main.caseChangeFolder2.setSelected(true);
								Main.caseOpenFolderAtEnd2.setSelected(false);
								Main.caseOpenFolderAtEnd2.setEnabled(true);
								Main.caseChangeFolder3.setEnabled(true);
							}

							if (Main.scan.getText().equals(Main.language.getProperty("menuItemStopScan")))
								Main.scanIsRunning = true;

							if (Main.lblDestination2.getText() != Main.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(Main.lblDestination2.getText());
						} else if (comp.getName().equals("lblDestination3")) {
							// Si destination identique à l'une des autres
							if (Main.lblDestination3.getText().equals(Main.lblDestination1.getText())
									|| Main.lblDestination3.getText().equals(Main.lblDestination2.getText())) {
								JOptionPane.showMessageDialog(Main.frame,
										Main.language.getProperty("ChooseDifferentFolder"),
										Main.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Main.lblDestination3.setText(Main.language.getProperty("aucune"));
								Main.caseChangeFolder3.setSelected(false);
								Main.caseOpenFolderAtEnd3.setSelected(false);
								Main.caseOpenFolderAtEnd3.setEnabled(false);
							} else {
								Main.caseChangeFolder3.setSelected(true);
								Main.caseOpenFolderAtEnd3.setSelected(false);
								Main.caseOpenFolderAtEnd3.setEnabled(true);
							}

							if (Main.scan.getText().equals(Main.language.getProperty("menuItemStopScan")))
								Main.scanIsRunning = true;

							if (Main.lblDestination3.getText() != Main.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(Main.lblDestination3.getText());
						}
					}

					// Border
					comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}
