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

package shutterencoder.ui.others;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.ui.main.Shutter;
import shutterencoder.utils.Utils;

/**
 * Popup shown at the end of a conversion job: how much disk space the
 * conversion saved (or added), in absolute size and percent, e.g.
 * "−12.4 GB  |  −78.5 %". Values >= 1 GB are shown in GB, smaller ones in MB.
 * A two-bar comparison visualizes the original size against the compressed size.
 */
public class SpaceSavedDialog {

	private static final Color COLOR_SAVED = new Color(111, 191, 95);
	private static final Color COLOR_KEPT = new Color(79, 143, 218);
	private static final Color COLOR_ADDED = new Color(217, 154, 78);
	private static final Color COLOR_TEXT = new Color(232, 232, 232);
	private static final Color COLOR_TEXT_DIM = new Color(154, 154, 154);
	private static final Color COLOR_PANEL = new Color(60, 63, 65);

	public static void showIfNeeded() {

		final long sourceSize = FunctionUtils.totalSourceSize;
		final long outputSize = FunctionUtils.totalOutputSize;
		final String destination = FunctionUtils.lastOutputFolder;

		//Reset for the next job
		FunctionUtils.totalSourceSize = 0;
		FunctionUtils.totalOutputSize = 0;
		FunctionUtils.lastOutputFolder = "";

		//Only show when real files have been converted
		if (sourceSize <= 0 || outputSize <= 0)
			return;

		final long diff = sourceSize - outputSize;
		final double percent = diff * 100.0 / sourceSize;

		//Sign: − for space freed, + for space added
		final String sign = diff >= 0 ? "−" : "+";

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(COLOR_PANEL);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 26, 14, 26));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 4, 0);

		//Hero value: "−12.4 GB  |  −78.5 %"
		JLabel hero = new JLabel(sign + formatSize(Math.abs(diff)) + "   |   " + sign + String.format("%.1f", Math.abs(percent)) + " %");
		hero.setFont(hero.getFont().deriveFont(Font.BOLD, 26f));
		hero.setForeground(diff >= 0 ? COLOR_SAVED : COLOR_ADDED);
		panel.add(hero, gbc);

		//Caption
		JLabel caption;
		if (diff >= 0)
			caption = new JLabel("Disk space saved");
		else
			caption = new JLabel("The converted files are larger than the originals");
		caption.setFont(caption.getFont().deriveFont(Font.PLAIN, 11f));
		caption.setForeground(COLOR_TEXT_DIM);
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 18, 0);
		panel.add(caption, gbc);

		//Comparison: two lines, original above, compressed below
		gbc.insets = new Insets(0, 0, 0, 0);
		addComparisonLine(panel, gbc, "Original", sourceSize, Math.max(sourceSize, outputSize), COLOR_KEPT);
		addComparisonLine(panel, gbc, "Compressed", outputSize, Math.max(sourceSize, outputSize), diff >= 0 ? COLOR_SAVED : COLOR_ADDED);

		//Files converted
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(12, 0, 0, 0);
		JLabel files = new JLabel("Files converted:  " + FunctionUtils.completed);
		files.setForeground(COLOR_TEXT);
		files.setFont(files.getFont().deriveFont(Font.PLAIN, 13f));
		panel.add(files, gbc);

		//Buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(COLOR_PANEL);

		if (destination != null && destination.isEmpty() == false && new File(destination).isDirectory()) {
			JButton openFolder = new JButton("Open destination");
			openFolder.setForeground(Color.WHITE);
			openFolder.setBackground(new Color(75, 78, 80));
			openFolder.setFocusPainted(false);
			openFolder.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
			openFolder.addActionListener(e -> Utils.openFile(new File(destination)));
			buttonPanel.add(openFolder);
			buttonPanel.add(Box.createHorizontalStrut(10));
		}

		JButton ok = new JButton("OK");
		ok.setBackground(new Color(79, 143, 218));
		ok.setForeground(Color.WHITE);
		ok.setFocusPainted(false);
		ok.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
		buttonPanel.add(ok);

		JDialog dialog = new JDialog(Shutter.frame, "Conversion complete", JDialog.ModalityType.APPLICATION_MODAL);
		dialog.getContentPane().setBackground(panel.getBackground());
		ok.addActionListener(e -> dialog.dispose());

		gbc.gridy++;
		gbc.insets = new Insets(18, 0, 4, 0);
		panel.add(buttonPanel, gbc);

		dialog.getRootPane().setDefaultButton(ok);
		dialog.add(panel);
		dialog.pack();
		dialog.setMinimumSize(new Dimension(Math.max(dialog.getWidth(), 420), dialog.getHeight()));
		dialog.setLocationRelativeTo(Shutter.frame);
		dialog.setVisible(true);
	}

	/**
	 * One comparison line: label, proportional bar, size value.
	 */
	private static void addComparisonLine(JPanel panel, GridBagConstraints gbc, String label, long size, long maxSize, Color color) {

		gbc.gridwidth = 1;
		gbc.gridy++;

		JLabel lbl = new JLabel(label);
		lbl.setForeground(COLOR_TEXT_DIM);
		lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.insets = new Insets(4, 0, 4, 10);
		panel.add(lbl, gbc);

		JLabel bar = new JLabel() {

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(color);
				int w = (int) (Math.max(4, (double) size / (double) maxSize * (getWidth() - 2)));
				g2.fillRoundRect(1, 1, w, getHeight() - 2, 8, 8);
			}
		};		bar.setPreferredSize(new Dimension(220, 12));
		gbc.gridx = 1;
		gbc.weightx = 1;
		panel.add(bar, gbc);

		JLabel val = new JLabel(formatSize(size));
		val.setForeground(COLOR_TEXT);
		val.setFont(val.getFont().deriveFont(Font.PLAIN, 13f));
		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.insets = new Insets(4, 10, 4, 0);
		panel.add(val, gbc);
	}

	/**
	 * GB with one decimal when the value is >= 1 GB, otherwise MB.
	 */
	private static String formatSize(long bytes) {

		if (bytes >= 1024L * 1024 * 1024)
			return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
		else
			return String.format("%.0f MB", bytes / (1024.0 * 1024));
	}
}
