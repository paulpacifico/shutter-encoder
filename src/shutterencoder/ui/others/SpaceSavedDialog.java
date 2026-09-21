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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.ui.main.Shutter;

/**
 * Popup shown at the end of a conversion job: how much disk space the
 * conversion saved (or added), in absolute size and percent, e.g.
 * "−12.4 GB  |  −78.5 %". Values >= 1 GB are shown in GB, smaller ones in MB.
 */
public class SpaceSavedDialog {

	private static final Color COLOR_SAVED = new Color(111, 191, 95);
	private static final Color COLOR_KEPT = new Color(79, 143, 218);
	private static final Color COLOR_ADDED = new Color(217, 154, 78);

	public static void showIfNeeded() {

		final long sourceSize = FunctionUtils.totalSourceSize;
		final long outputSize = FunctionUtils.totalOutputSize;

		//Reset for the next job
		FunctionUtils.totalSourceSize = 0;
		FunctionUtils.totalOutputSize = 0;

		//Only show when real files have been converted
		if (sourceSize <= 0 || outputSize <= 0)
			return;

		final long diff = sourceSize - outputSize;
		final double percent = diff * 100.0 / sourceSize;

		//Sign: − for space freed, + for space added
		final String sign = diff >= 0 ? "−" : "+";

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(60, 63, 65));
		panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 12, 24));
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
		caption.setForeground(new Color(154, 154, 154));
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 16, 0);
		panel.add(caption, gbc);

		//Stats table
		gbc.insets = new Insets(0, 0, 0, 0);
		addStat(panel, gbc, "Original size", formatSize(sourceSize), false);
		addStat(panel, gbc, "New size", formatSize(outputSize), false);
		addStat(panel, gbc, "Files converted", String.valueOf(FunctionUtils.completed), true);

		//Bar: green saved vs blue kept, or blue original vs orange added
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(14, 0, 0, 0);
		panel.add(new BarPanel(diff, sourceSize, outputSize), gbc);

		JButton ok = new JButton("OK");
		ok.setBackground(new Color(79, 143, 218));
		ok.setForeground(Color.WHITE);
		ok.setFocusPainted(false);
		ok.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

		JDialog dialog = new JDialog(Shutter.frame, "Conversion complete", JDialog.ModalityType.APPLICATION_MODAL);
		dialog.getContentPane().setBackground(panel.getBackground());
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBackground(panel.getBackground());
		buttonPanel.add(ok);
		gbc.gridy++;
		gbc.insets = new Insets(16, 0, 4, 0);
		panel.add(buttonPanel, gbc);
		ok.addActionListener(e -> dialog.dispose());

		dialog.getRootPane().setDefaultButton(ok);
		dialog.add(panel);
		dialog.pack();
		dialog.setMinimumSize(new Dimension(Math.max(dialog.getWidth(), 400), dialog.getHeight()));
		dialog.setLocationRelativeTo(Shutter.frame);
		dialog.setVisible(true);
	}

	private static void addStat(JPanel panel, GridBagConstraints gbc, String label, String value, boolean last) {

		gbc.gridwidth = 1;
		gbc.gridy++;

		JLabel lbl = new JLabel(label);
		lbl.setForeground(new Color(154, 154, 154));
		lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
		gbc.gridx = 0;
		gbc.insets = new Insets(7, 0, last ? 0 : 7, 24);
		panel.add(lbl, gbc);

		JLabel val = new JLabel(value);
		val.setForeground(new Color(232, 232, 232));
		val.setFont(val.getFont().deriveFont(Font.PLAIN, 13f));
		gbc.gridx = 1;
		gbc.insets = new Insets(7, 0, last ? 0 : 7, 0);
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

	@SuppressWarnings("serial")
	private static class BarPanel extends JPanel {

		private final long diff;
		private final long sourceSize;
		private final long outputSize;

		BarPanel(long diff, long sourceSize, long outputSize) {
			this.diff = diff;
			this.sourceSize = sourceSize;
			this.outputSize = outputSize;
			setPreferredSize(new Dimension(350, 10));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			//Background = remaining color, drawn as two segments
			if (diff >= 0) {
				int savedWidth = (int) (w * diff / (double) sourceSize);
				g2.setColor(COLOR_SAVED);
				g2.fillRoundRect(0, 0, savedWidth, h, h, h);
				g2.setColor(COLOR_KEPT);
				if (w - savedWidth > 0)
					g2.fillRoundRect(savedWidth, 0, w - savedWidth, h, h, h);
			} else {
				//Files grew: blue = original size, orange = added space (total = new size)
				int originalWidth = (int) (w * sourceSize / (double) outputSize);
				g2.setColor(COLOR_KEPT);
				g2.fillRoundRect(0, 0, originalWidth, h, h, h);
				g2.setColor(COLOR_ADDED);
				if (w - originalWidth > 0)
					g2.fillRoundRect(originalWidth, 0, w - originalWidth, h, h, h);
			}
		}
	}
}
