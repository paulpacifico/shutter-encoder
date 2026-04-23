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

package renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import application.Shutter;
import application.Utils;

//Edit functions list
@SuppressWarnings("serial")
public class ComboBoxRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (isSelected)
		{
			if (value.toString().contains(":") || value.toString().contains(Shutter.language.getProperty("btnManage").toUpperCase()))
			{
				setBackground(Utils.c225);
			}
			else
				setBackground(Utils.themeColor);
			
		} else {
			setBackground(new Color(Utils.c42.getRed(), Utils.c42.getGreen(), Utils.c42.getBlue(), 200));
		}

		if (value.toString().contains(":") || value.toString().contains(Shutter.language.getProperty("btnManage").toUpperCase()))
		{
			setForeground(Utils.themeColor);
			setFont(new Font(Shutter.boldFont, Font.BOLD, 12));
		}
		else
			setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));		

		list.setFixedCellHeight(18);

		return this;
	}
}