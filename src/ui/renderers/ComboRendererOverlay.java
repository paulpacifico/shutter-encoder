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

package shutterencoder.ui.renderers;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

@SuppressWarnings({ "unused", "rawtypes" })
public class ComboRendererOverlay extends BasicComboBoxRenderer {

	private static final long serialVersionUID = 1L;
	private JComboBox comboBox;
	final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	private int row;

	public ComboRendererOverlay(JComboBox fontsBox) {
		comboBox = fontsBox;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (list.getModel().getSize() > 0) {
			final Object comp = comboBox.getUI().getAccessibleChild(comboBox, 0);
		}
		final JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, row, isSelected,
				cellHasFocus);
		final Object fntObj = value;
		final String fontFamilyName = (String) fntObj;

		setFont(new Font(fontFamilyName, Font.PLAIN, 16));

		return this;
	}
}
