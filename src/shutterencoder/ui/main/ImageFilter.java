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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Year;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.utils.Utils;

public class ImageFilter extends Main {

	public static void grpImageFilter() {

		grpImageFilter = new CollapsiblePanel(language.getProperty("grpFiltreImage"), true);
		grpImageFilter.setLayout(null);
		grpImageFilter.setVisible(false);
		grpImageFilter.setOpaque(false);
		grpImageFilter.setBackground(Utils.c30);
		grpImageFilter.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpImageFilter);

		grpImageFilter.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpImageFilter, 122);
			}
			
		});

		caseYear = new JCheckBox(language.getProperty("caseYear"));
		caseYear.setName("caseYear");
		caseYear.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseYear.setBounds(7, 16, caseYear.getPreferredSize().width, 23);
		grpImageFilter.add(caseYear);

		caseYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseYear.isSelected())
					comboYear.setEnabled(true);
				else
					comboYear.setEnabled(false);
			}
		});

		comboYear = new JComboBox<String>();
		comboYear.setName("comboYear");
		comboYear.setEnabled(false);

		int actualYear = Year.now().getValue();
		String[] years = new String[actualYear + 1 - 2000];

		int y = 0;
		for (int i = actualYear; i > 1999; i--) {
			years[y] = String.valueOf(i);
			y++;
		}

		comboYear.setModel(new DefaultComboBoxModel<String>(years));
		comboYear.setSelectedIndex(0);
		comboYear.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboYear.setEditable(true);
		comboYear.setBounds(caseYear.getWidth() + caseYear.getLocation().x + 4, caseYear.getLocation().y + 4, 54, 16);
		comboYear.setMaximumRowCount(15);
		grpImageFilter.add(comboYear);

		caseMonth = new JCheckBox(language.getProperty("caseMonth"));
		caseMonth.setName("caseMonth");
		caseMonth.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseMonth.setBounds(7, caseYear.getLocation().y + caseYear.getHeight() + 2, caseMonth.getPreferredSize().width,
				23);
		grpImageFilter.add(caseMonth);

		caseMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseMonth.isSelected())
					comboMonth.setEnabled(true);
				else
					comboMonth.setEnabled(false);
			}
		});

		comboMonth = new JComboBox<String>();
		comboMonth.setName("comboMonth");
		comboMonth.setEnabled(false);

		String[] months = new String[12];
		int m = 0;
		for (int i = 1; i < 13; i++) {
			months[m] = String.valueOf(i);
			if (i < 10)
				months[m] = "0" + months[m];

			m++;
		}

		comboMonth.setModel(new DefaultComboBoxModel<String>(months));
		comboMonth.setSelectedIndex(0);
		comboMonth.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboMonth.setEditable(true);
		comboMonth.setBounds(caseMonth.getWidth() + caseMonth.getLocation().x + 4, caseMonth.getLocation().y + 4, 40,
				16);
		comboMonth.setMaximumRowCount(15);
		grpImageFilter.add(comboMonth);

		caseDay = new JCheckBox(language.getProperty("caseDay"));
		caseDay.setName("caseDay");
		caseDay.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseDay.setBounds(7, caseMonth.getLocation().y + caseMonth.getHeight() + 2, caseDay.getPreferredSize().width,
				23);
		grpImageFilter.add(caseDay);

		caseDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseDay.isSelected())
					comboDay.setEnabled(true);
				else
					comboDay.setEnabled(false);
			}
		});

		comboDay = new JComboBox<String>();
		comboDay.setName("comboDay");
		comboDay.setEnabled(false);

		String[] days = new String[31];
		int d = 0;
		for (int i = 1; i < 32; i++) {
			days[d] = String.valueOf(i);
			if (i < 10)
				days[d] = "0" + days[d];

			d++;
		}

		comboDay.setModel(new DefaultComboBoxModel<String>(days));
		comboDay.setSelectedIndex(0);
		comboDay.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboDay.setEditable(true);
		comboDay.setBounds(caseDay.getWidth() + caseDay.getLocation().x + 4, caseDay.getLocation().y + 4, 40, 16);
		comboDay.setMaximumRowCount(15);
		grpImageFilter.add(comboDay);

		caseFrom = new JCheckBox(language.getProperty("caseFrom"));
		caseFrom.setName("caseFrom");
		caseFrom.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseFrom.setBounds(7, caseDay.getLocation().y + caseDay.getHeight() + 2, caseFrom.getPreferredSize().width, 23);
		grpImageFilter.add(caseFrom);

		caseFrom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseFrom.isSelected()) {
					comboFrom.setEnabled(true);
					comboTo.setEnabled(true);
				} else {
					comboFrom.setEnabled(false);
					comboTo.setEnabled(false);
				}
			}
		});

		comboFrom = new JComboBox<String>();
		comboFrom.setName("comboFrom");
		comboFrom.setEnabled(false);

		String[] from = new String[49];
		int h = 0;
		for (int i = 0; i < 49; i++) {
			if ((i % 2) == 0)
				from[i] = h + ":00";
			else {
				from[i] = h + ":30";
				h++;
			}

			if (i < 20)
				from[i] = "0" + from[i];

		}
		comboFrom.setModel(new DefaultComboBoxModel<String>(from));
		comboFrom.setSelectedIndex(0);
		comboFrom.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboFrom.setEditable(true);
		comboFrom.setBounds(caseFrom.getWidth() + caseFrom.getLocation().x + 4, caseFrom.getLocation().y + 4, 54, 16);
		comboFrom.setMaximumRowCount(15);
		grpImageFilter.add(comboFrom);

		JLabel h1 = new JLabel(language.getProperty("lblH"));
		h1.setFont(new Font(mainFont, Font.PLAIN, 12));
		h1.setBounds(comboFrom.getLocation().x + comboFrom.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpImageFilter.add(h1);

		comboTo = new JComboBox<String>();
		comboTo.setName("comboTo");
		comboTo.setEnabled(false);

		String[] to = new String[49];
		h = 0;
		for (int i = 0; i < 49; i++) {
			if ((i % 2) == 0)
				to[i] = h + ":00";
			else {
				to[i] = h + ":30";
				h++;
			}

			if (i < 20)
				to[i] = "0" + to[i];

		}

		comboTo.setModel(new DefaultComboBoxModel<String>(to));
		comboTo.setSelectedIndex(48);
		comboTo.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboTo.setEditable(true);
		comboTo.setBounds(h1.getWidth() + h1.getLocation().x, comboFrom.getLocation().y, 54, 16);
		comboTo.setMaximumRowCount(15);
		grpImageFilter.add(comboTo);

		JLabel h2 = new JLabel(language.getProperty("lblH"));
		h2.setFont(new Font(mainFont, Font.PLAIN, 12));
		h2.setBounds(comboTo.getLocation().x + comboTo.getWidth() + 5, caseFrom.getLocation().y + 3, 16, 16);
		grpImageFilter.add(h2);

	}
}
