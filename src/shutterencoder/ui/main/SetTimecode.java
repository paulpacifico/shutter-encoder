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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class SetTimecode extends Main {

	public static void grpSetTimecode() {

		grpSetTimecode = new CollapsiblePanel(language.getProperty("grpTimecode"), true);
		grpSetTimecode.setLayout(null);
		grpSetTimecode.setVisible(false);
		grpSetTimecode.setOpaque(false);
		grpSetTimecode.setBackground(Utils.c30);
		grpSetTimecode.setBounds(frame.getWidth(), 258, 312, 17);
		frame.getContentPane().add(grpSetTimecode);

		caseSetTimecode = new JCheckBox(language.getProperty("caseSetTimecode"));
		caseSetTimecode.setName("caseSetTimecode");
		caseSetTimecode.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseSetTimecode.setBounds(7, 16, 150, 23);
		grpSetTimecode.add(caseSetTimecode);

		grpSetTimecode.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpSetTimecode, 117);
			}

		});

		caseSetTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (caseSetTimecode.isSelected())
				{
					setTimecodeEdited = false;
					
					TCset1.setEnabled(true);
					TCset2.setEnabled(true);
					TCset3.setEnabled(true);
					TCset4.setEnabled(true);

					TCset1.setText(VideoPlayerUI.caseInH.getText());
					TCset2.setText(VideoPlayerUI.caseInM.getText());
					TCset3.setText(VideoPlayerUI.caseInS.getText());
					TCset4.setText(VideoPlayerUI.caseInF.getText());

					caseIncrementTimecode.setEnabled(true);
					caseGenerateFromDate.setSelected(false);
					caseGenerateFromDate.setEnabled(false);
					caseReadAudioTimecode.setEnabled(false);
					comboReadAudioTimecode.setEnabled(false);
					
				} else {
					TCset1.setEnabled(false);
					TCset2.setEnabled(false);
					TCset3.setEnabled(false);
					TCset4.setEnabled(false);
					caseIncrementTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);
					caseGenerateFromDate.setEnabled(true);
					caseReadAudioTimecode.setEnabled(true);
				}

			}

		});
		
		TCset1 = new JTextField();
		TCset1.setName("TCset1");
		TCset1.setEnabled(false);
		TCset1.setText("00");
		TCset1.setHorizontalAlignment(SwingConstants.CENTER);
		TCset1.setFont(new Font(mainFont, Font.PLAIN, 13));
		TCset1.setColumns(10);
		TCset1.setBounds(156, 18, 32, 21);
		grpSetTimecode.add(TCset1);

		TCset2 = new JTextField();
		TCset2.setName("TCset2");
		TCset2.setEnabled(false);
		TCset2.setText("00");
		TCset2.setHorizontalAlignment(SwingConstants.CENTER);
		TCset2.setFont(new Font(mainFont, Font.PLAIN, 13));
		TCset2.setColumns(10);
		TCset2.setBounds(192, TCset1.getY(), 32, 21);
		grpSetTimecode.add(TCset2);

		TCset3 = new JTextField();
		TCset3.setName("TCset3");
		TCset3.setEnabled(false);
		TCset3.setText("00");
		TCset3.setHorizontalAlignment(SwingConstants.CENTER);
		TCset3.setFont(new Font(mainFont, Font.PLAIN, 13));
		TCset3.setColumns(10);
		TCset3.setBounds(228, TCset1.getY(), 32, 21);
		grpSetTimecode.add(TCset3);

		TCset4 = new JTextField();
		TCset4.setName("TCset4");
		TCset4.setEnabled(false);
		TCset4.setText("00");
		TCset4.setHorizontalAlignment(SwingConstants.CENTER);
		TCset4.setFont(new Font(mainFont, Font.PLAIN, 13));
		TCset4.setColumns(10);
		TCset4.setBounds(264, TCset1.getY(), 32, 21);
		grpSetTimecode.add(TCset4);

		TCset1.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset1.getText().length() >= 2)
					TCset1.setText("");

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setTimecodeEdited = true;
			}
		});

		TCset2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset2.getText().length() >= 2)
					TCset2.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setTimecodeEdited = true;
			}
		});

		TCset3.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset3.getText().length() >= 2)
					TCset3.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setTimecodeEdited = true;
			}
		});

		TCset4.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TCset4.getText().length() >= 2)
					TCset4.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setTimecodeEdited = true;
			}
		});

		caseIncrementTimecode = new JCheckBox(language.getProperty("caseIncrementTimecode"));
		caseIncrementTimecode.setName("caseIncrementTimecode");
		caseIncrementTimecode.setEnabled(false);
		caseIncrementTimecode.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseIncrementTimecode.setBounds(7, caseSetTimecode.getY() + caseSetTimecode.getHeight(),
				caseIncrementTimecode.getPreferredSize().width, 23);
		grpSetTimecode.add(caseIncrementTimecode);

		caseGenerateFromDate = new JCheckBox(language.getProperty("caseGenerateFromDate"));
		caseGenerateFromDate.setName("caseGenerateFromDate");
		caseGenerateFromDate.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseGenerateFromDate.setBounds(7, caseIncrementTimecode.getY() + caseIncrementTimecode.getHeight(),
				caseGenerateFromDate.getPreferredSize().width + 4, 23);
		grpSetTimecode.add(caseGenerateFromDate);

		caseGenerateFromDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseGenerateFromDate.isSelected()) {
					caseSetTimecode.setSelected(false);
					caseSetTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);
					caseIncrementTimecode.setEnabled(false);
					caseReadAudioTimecode.setSelected(false);
					comboReadAudioTimecode.setEnabled(false);
				} else {
					caseSetTimecode.setEnabled(true);
				}
			}

		});
		
		caseReadAudioTimecode = new JCheckBox(language.getProperty("caseReadAudioTimecode") + language.getProperty("colon"));
		caseReadAudioTimecode.setName("caseReadAudioTimecode");
		caseReadAudioTimecode.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseReadAudioTimecode.setBounds(7, caseGenerateFromDate.getY() + caseGenerateFromDate.getHeight(), caseReadAudioTimecode.getPreferredSize().width + 4, 23);
		grpSetTimecode.add(caseReadAudioTimecode);
		
		caseReadAudioTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (caseReadAudioTimecode.isSelected())
				{
					caseSetTimecode.setSelected(false);
					caseSetTimecode.setEnabled(false);
					caseIncrementTimecode.setSelected(false);
					caseIncrementTimecode.setEnabled(false);
					caseGenerateFromDate.setSelected(false);
					
					comboReadAudioTimecode.setEnabled(true);
				} else {
					caseSetTimecode.setEnabled(true);
					comboReadAudioTimecode.setEnabled(false);
				}
			}

		});
		
		comboReadAudioTimecode = new JComboBox<String>();
		comboReadAudioTimecode.setName("comboReadAudioTimecode");
		comboReadAudioTimecode.setModel(new DefaultComboBoxModel<String>(new String[] {
				language.getProperty("audio") + " 1",
				language.getProperty("audio") + " 2", language.getProperty("audio") + " 3",
				language.getProperty("audio") + " 4", language.getProperty("audio") + " 5",
				language.getProperty("audio") + " 6", language.getProperty("audio") + " 7",
				language.getProperty("audio") + " 8", language.getProperty("audio") + " 9",
				language.getProperty("audio") + " 10", language.getProperty("audio") + " 11",
				language.getProperty("audio") + " 12", language.getProperty("audio") + " 13",
				language.getProperty("audio") + " 14", language.getProperty("audio") + " 15",
				language.getProperty("audio") + " 16" }));
		comboReadAudioTimecode.setSelectedIndex(0);
		comboReadAudioTimecode.setMaximumRowCount(20);
		comboReadAudioTimecode.setFont(new Font(mainFont, Font.PLAIN, 10));
		comboReadAudioTimecode.setEditable(false);
		comboReadAudioTimecode.setEnabled(false);
		comboReadAudioTimecode.setSize(65, 16);
		comboReadAudioTimecode.setLocation(caseReadAudioTimecode.getX() + caseReadAudioTimecode.getWidth(), caseReadAudioTimecode.getLocation().y + 4);
		grpSetTimecode.add(comboReadAudioTimecode);
	}
}
