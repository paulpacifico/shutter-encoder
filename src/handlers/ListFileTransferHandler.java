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

package handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import application.Settings;
import application.Shutter;
import application.UIController;
import application.Utils;
import application.VideoPlayer;
import settings.FunctionUtils;

//Drag & Drop file list
@SuppressWarnings("serial")
public class ListFileTransferHandler extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {

		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && Shutter.inputDeviceIsRunning == false
					&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false) {
				return true;
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {

		DataFlavor[] flavors = t.getTransferDataFlavors();

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];

			try {

				if (flavor.equals(DataFlavor.javaFileListFlavor)
						&& Shutter.comboFonctions.getSelectedItem().equals("DVD Rip") == false) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						File file = (File) iter.next();

						// Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows")
								&& file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);

						if (Shutter.scan.getText().equals(Shutter.language.getProperty("menuItemStopScan"))) {
							if (file.isDirectory()) {
								if (file.toString().contains("completed") == false
										&& file.toString().contains("error") == false) {
									boolean folderExists = false;
									for (int f = 0; f < Shutter.list.getSize(); f++) {
										if (Shutter.list.getElementAt(f).equals(file.toString())) {
											folderExists = true;
										}
									}

									if (folderExists == false) {
										Utils.findDirectories(file.toString());
									}
								}
							} else
								file = new File(file.getParent());

							if (System.getProperty("os.name").contains("Mac")
									|| System.getProperty("os.name").contains("Linux"))
								Shutter.list.addElement(file + "/");
							else
								Shutter.list.addElement(file + "\\");

							Shutter.addToList.setVisible(false);
							Shutter.lblFiles.setText(Utils.filesNumber());

							if (file != null) {
								if (Shutter.caseChangeFolder1.isSelected()) {
									Shutter.scanIsRunning = true;
									UIController.changeFilters();
								} else
									JOptionPane.showMessageDialog(Shutter.frame,
											Shutter.language.getProperty("dragFolderToDestination"),
											Shutter.language.getProperty("chooseDestinationFolder"),
											JOptionPane.INFORMATION_MESSAGE);
							}

						} else {
							if (file.isFile() && file.getName().contains(".")) {
								int s = file.toString().lastIndexOf('.');
								String ext = file.getCanonicalFile().toString().substring(s);

								if (ext.equals(".enc")) {
									Utils.loadSettings(new File(file.toString()));
								} else {
									if (file.isHidden() == false) {
										boolean allowed = true;
										if (Settings.btnExclude.isSelected()) {
											for (String excludeExt : Settings.txtExclude.getText().replace(" ", "")
													.split("\\*")) {
												if (excludeExt.contains(".") && ext.toLowerCase()
														.equals(excludeExt.replace(",", "").toLowerCase())) {
													allowed = false;
													break;
												}
											}

											if (allowed == false) {
												continue;// Next
											}
										}

										if (file.toString().contains("\"")
												|| file.toString().contains("\'")
												|| file.getName().contains("/") || file.getName().contains("\\")) {
											if (FunctionUtils.allowsInvalidCharacters == false) {
												JOptionPane.showConfirmDialog(Shutter.frame,
														file.getAbsoluteFile().toString() + System.lineSeparator()
																+ Shutter.language.getProperty("invalidCharacter"),
														Shutter.language.getProperty("import"),
														JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);

												FunctionUtils.allowsInvalidCharacters = true;
											}
										}

										Shutter.list.addElement(file.toString());
										Shutter.addToList.setVisible(false);
										Shutter.lblFiles.setText(Utils.filesNumber());
									}
								}
							} else {
								Utils.findFiles(file.toString());
							}
						}
					}

					// CaseOPATOM
					switch (Shutter.comboFonctions.getSelectedItem().toString()) {
					case "DNxHD":
					case "DNxHR":
					case "Apple ProRes":
					case "GoPro CineForm":
					case "QT Animation":
					case "Uncompressed":
						if (Shutter.caseOPATOM.isSelected()) {
							for (int item = 0; item < Shutter.list.getSize(); item++) {
								int s = Shutter.list.getElementAt(item).toString().lastIndexOf('.');
								if (Shutter.list.getElementAt(item).toString().substring(s).toLowerCase()
										.equals(".mxf") == false) {
									Shutter.list.remove(item);
									item = -1;
								}
							}
							Shutter.lblFiles.setText(Utils.filesNumber());
						}
						break;
					}

					// VideoPlayer.player
					Shutter.fileList.setSelectedIndex(Shutter.list.getSize() - 1);

					VideoPlayer.setMedia();
					
					// Filter
					UIController.changeFilters();

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}

		return false;
	}
}