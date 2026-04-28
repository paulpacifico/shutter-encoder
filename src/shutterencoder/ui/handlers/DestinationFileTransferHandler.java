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

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Settings;
import shutterencoder.utils.Utils;

// Drag & Drop lblDestination
@SuppressWarnings("serial")
public class DestinationFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent comp, DataFlavor[] arg1) {

		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];

			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				if (comp.getName().equals("lblDestination3") && Shutter.caseChangeFolder2.isSelected() == false) {
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
							if (Shutter.lblDestination1.getText().equals(Shutter.lblDestination2.getText())
									|| Shutter.lblDestination1.getText().equals(Shutter.lblDestination3.getText())) {
								JOptionPane.showMessageDialog(Shutter.frame,
										Shutter.language.getProperty("ChooseDifferentFolder"),
										Shutter.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
								Shutter.caseChangeFolder1.setSelected(false);
							} else {
								Shutter.caseChangeFolder1.setSelected(true);
								Shutter.caseOpenFolderAtEnd1.setSelected(false);
							}

							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan"))) {
								// Si le dossier d'entrée et de sortie est identique
								if (Shutter.list.firstElement().substring(0, Shutter.list.firstElement().length() - 1)
										.equals(Shutter.lblDestination1.getText())) {
									JOptionPane.showMessageDialog(Shutter.frame,
											Shutter.language.getProperty("ChooseDifferentFolder"),
											Shutter.language.getProperty("sameFolder"), JOptionPane.ERROR_MESSAGE);
									Shutter.lblDestination1.setText(Shutter.language.getProperty("sameAsSource"));
									Shutter.caseChangeFolder1.setSelected(false);
								} else {
									Shutter.scanIsRunning = true;
								}
							}

							if (Shutter.lblDestination1.getText() != Shutter.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput1.isSelected())
								Settings.lblDestination1.setText(Shutter.lblDestination1.getText());
						} else if (comp.getName().equals("lblDestination2")) {
							// Si destination identique à l'une des autres
							if (Shutter.lblDestination2.getText().equals(Shutter.lblDestination1.getText())
									|| Shutter.lblDestination2.getText().equals(Shutter.lblDestination3.getText())) {
								JOptionPane.showMessageDialog(Shutter.frame,
										Shutter.language.getProperty("ChooseDifferentFolder"),
										Shutter.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination2.setText(Shutter.language.getProperty("aucune"));
								Shutter.caseChangeFolder2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setEnabled(false);
							} else {
								Shutter.caseChangeFolder2.setSelected(true);
								Shutter.caseOpenFolderAtEnd2.setSelected(false);
								Shutter.caseOpenFolderAtEnd2.setEnabled(true);
								Shutter.caseChangeFolder3.setEnabled(true);
							}

							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
								Shutter.scanIsRunning = true;

							if (Shutter.lblDestination2.getText() != Shutter.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput2.isSelected())
								Settings.lblDestination2.setText(Shutter.lblDestination2.getText());
						} else if (comp.getName().equals("lblDestination3")) {
							// Si destination identique à l'une des autres
							if (Shutter.lblDestination3.getText().equals(Shutter.lblDestination1.getText())
									|| Shutter.lblDestination3.getText().equals(Shutter.lblDestination2.getText())) {
								JOptionPane.showMessageDialog(Shutter.frame,
										Shutter.language.getProperty("ChooseDifferentFolder"),
										Shutter.language.getProperty("chooseDestinationFolder"),
										JOptionPane.ERROR_MESSAGE);
								Shutter.lblDestination3.setText(Shutter.language.getProperty("aucune"));
								Shutter.caseChangeFolder3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setEnabled(false);
							} else {
								Shutter.caseChangeFolder3.setSelected(true);
								Shutter.caseOpenFolderAtEnd3.setSelected(false);
								Shutter.caseOpenFolderAtEnd3.setEnabled(true);
							}

							if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan")))
								Shutter.scanIsRunning = true;

							if (Shutter.lblDestination3.getText() != Shutter.language.getProperty("sameAsSource")
									&& Settings.lastUsedOutput3.isSelected())
								Settings.lblDestination3.setText(Shutter.lblDestination3.getText());
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
