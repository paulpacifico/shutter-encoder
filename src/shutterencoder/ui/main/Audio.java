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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.utils.Utils;

public class Audio extends Main {

	public static void grpAudio() {
	
		grpAudio = new CollapsiblePanel(language.getProperty("grpAudio"), false);
		grpAudio.setLayout(null);
		grpAudio.setVisible(false);
		grpAudio.setOpaque(false);
		grpAudio.setBackground(Utils.c30);
		grpAudio.setBounds(frame.getWidth(), 343, 312, 162);
		frame.getContentPane().add(grpAudio);
	
		caseMixAudio = new JCheckBox(language.getProperty("caseMixAudio"));
		caseMixAudio.setName("caseMixAudio");
		caseMixAudio.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseMixAudio.setBounds(7, 62, caseMixAudio.getPreferredSize().width, 23);
		caseMixAudio.setToolTipText(language.getProperty("tooltipMixAudio"));
		grpAudio.add(caseMixAudio);
	
		caseMixAudio.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseMixAudio.isSelected()) {
					caseSplitAudio.setSelected(false);
	
					if (lblMix.getText().equals("2.1")) {
						addToList.setText("<html>FL<br>FR<br>LFE</html>");
					} else if (lblMix.getText().equals("5.1")) {
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>BL<br>BR</html>");
					} else
						addToList.setText("<html>FL<br>FR<br>FL<br>FR<br>...<br>...</html>");
				} else
					addToList.setText(language.getProperty("filesVideoOrAudio"));
			}
	
		});
	
		lblMix = new JLabel(language.getProperty("stereo"));
		lblMix.setName("lblMix");
		lblMix.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblMix.setBackground(Utils.c42);
		lblMix.setHorizontalAlignment(SwingConstants.CENTER);
		lblMix.setOpaque(true);
		lblMix.setFont(new Font(boldFont, Font.PLAIN, 12));
		lblMix.setBounds(caseMixAudio.getLocation().x + caseMixAudio.getWidth() + 5, caseMixAudio.getY() + 3, 46, 16);
		grpAudio.add(lblMix);
	
		lblMix.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblMix.getText().equals(language.getProperty("mono"))) {
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FL<br>FR<br>...<br>...</html>");
	
					lblMix.setText(language.getProperty("stereo"));
				} else if (lblMix.getText().equals(language.getProperty("stereo"))) {
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>LFE</html>");
	
					lblMix.setText("2.1");
				} else if (lblMix.getText().equals("2.1")) {
					if (caseMixAudio.isSelected())
						addToList.setText("<html>FL<br>FR<br>FC<br>LFE<br>BL<br>BR</html>");
	
					lblMix.setText("5.1");
				} else if (lblMix.getText().equals("5.1")) {
					if (caseMixAudio.isSelected())
						addToList.setText(language.getProperty("filesVideoOrAudio"));
	
					lblMix.setText(language.getProperty("mono"));
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
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
	
			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	
		});
	
		caseSplitAudio = new JCheckBox(language.getProperty("caseSplitAudio"));
		caseSplitAudio.setName("caseSplitAudio");
		caseSplitAudio.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseSplitAudio.setBounds(7, caseMixAudio.getY() + caseMixAudio.getHeight(),
				caseSplitAudio.getPreferredSize().width, 23);
		grpAudio.add(caseSplitAudio);
	
		caseSplitAudio.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseSplitAudio.isSelected())
					caseMixAudio.setSelected(false);
			}
	
		});
	
		lblSplit = new JLabel(language.getProperty("mono"));
		lblSplit.setName("lblSplit");
		lblSplit.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblSplit.setBackground(Utils.c42);
		lblSplit.setHorizontalAlignment(SwingConstants.CENTER);
		lblSplit.setOpaque(true);
		lblSplit.setFont(new Font(boldFont, Font.PLAIN, 12));
		lblSplit.setBounds(caseSplitAudio.getLocation().x + caseSplitAudio.getWidth() + 5, caseSplitAudio.getY() + 3,
				46, 16);
		grpAudio.add(lblSplit);
	
		lblSplit.addMouseListener(new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				if (lblSplit.getText().equals(language.getProperty("mono")))
					lblSplit.setText(language.getProperty("stereo"));
				else
					lblSplit.setText(language.getProperty("mono"));
			}
	
			@Override
			public void mousePressed(MouseEvent e) {
			}
	
			@Override
			public void mouseReleased(MouseEvent e) {
			}
	
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
	
			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
	
		});
	
		caseSampleRate = new JCheckBox(language.getProperty("caseSampleRate"));
		caseSampleRate.setName("caseSampleRate");
		caseSampleRate.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseSampleRate.setBounds(7, caseSplitAudio.getY() + caseSplitAudio.getHeight(),
				caseSampleRate.getPreferredSize().width, 23);
		grpAudio.add(caseSampleRate);
	
		caseSampleRate.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
	
				if (caseSampleRate.isSelected()) {
					lbl48k.setEnabled(true);
				} else
					lbl48k.setEnabled(false);
			}
	
		});
	
		caseConvertAudioFramerate = new JCheckBox(language.getProperty("caseConvertAudioFramerate"));
		caseConvertAudioFramerate.setName("caseConvertAudioFramerate");
		caseConvertAudioFramerate.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseConvertAudioFramerate.setBounds(7, caseSampleRate.getY() + caseSampleRate.getHeight(), caseConvertAudioFramerate.getPreferredSize().width, 23);
		grpAudio.add(caseConvertAudioFramerate);
	
		caseConvertAudioFramerate.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseConvertAudioFramerate.isSelected()) {
					comboAudioIn.setEnabled(true);
					comboAudioOut.setEnabled(true);
				} else {
					comboAudioIn.setEnabled(false);
					comboAudioOut.setEnabled(false);
				}
			}
	
		});
	
		comboAudioIn = new JComboBox<String>();
		comboAudioIn.setName("comboAudioIn");
		comboAudioIn.setEnabled(false);
		comboAudioIn.setModel(new DefaultComboBoxModel<String>(
				new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboAudioIn.setSelectedIndex(2);
		comboAudioIn.setMaximumRowCount(20);
		comboAudioIn.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboAudioIn.setEditable(true);
		comboAudioIn.setSize(63, 16);
		comboAudioIn.setLocation(caseConvertAudioFramerate.getLocation().x + caseConvertAudioFramerate.getWidth() + 7,
				caseConvertAudioFramerate.getLocation().y + 4);
		grpAudio.add(comboAudioIn);
	
		comboAudioIn.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboAudioIn.getSelectedIndex();
	
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (caseConvertAudioFramerate.isSelected()) {
					newitem = newitem + e.getWheelRotation();
					if (newitem >= 0 && newitem < comboAudioIn.getItemCount())
						comboAudioIn.setSelectedIndex(newitem);
					else
						newitem = comboAudioIn.getSelectedIndex();
				}
			}
		});
	
		lblFromTo = new JLabel(language.getProperty("lblFromTo"));
		lblFromTo.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblFromTo.setSize(lblFromTo.getPreferredSize().width + 4, 16);
		lblFromTo.setLocation(comboAudioIn.getLocation().x + comboAudioIn.getWidth() + 7,
				caseConvertAudioFramerate.getLocation().y + 3);
		grpAudio.add(lblFromTo);
	
		comboAudioOut = new JComboBox<String>();
		comboAudioOut.setName("comboAudioOut");
		comboAudioOut.setEnabled(false);
		comboAudioOut.setModel(new DefaultComboBoxModel<String>(
				new String[] { "23,98", "24", "25", "29,97", "30", "48", "50", "59,94", "60" }));
		comboAudioOut.setSelectedIndex(1);
		comboAudioOut.setMaximumRowCount(20);
		comboAudioOut.setFont(new Font(mainFont, Font.PLAIN, 11));
		comboAudioOut.setEditable(true);
		comboAudioOut.setSize(63, 16);
		comboAudioOut.setLocation(lblFromTo.getLocation().x + lblFromTo.getWidth() + 7,
				caseConvertAudioFramerate.getLocation().y + 4);
		grpAudio.add(comboAudioOut);
	
		comboAudioOut.addMouseWheelListener(new MouseWheelListener() {
			int newitem = comboAudioOut.getSelectedIndex();
	
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (caseConvertAudioFramerate.isSelected()) {
					newitem = newitem + e.getWheelRotation();
					if (newitem >= 0 && newitem < comboAudioOut.getItemCount())
						comboAudioOut.setSelectedIndex(newitem);
					else
						newitem = comboAudioOut.getSelectedIndex();
				}
			}
		});
	
		lblAudioIs = new JLabel(Main.language.getProperty("fps"));
		lblAudioIs.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblAudioIs.setSize(lblAudioIs.getPreferredSize().width, 16);
		lblAudioIs.setLocation(comboAudioOut.getLocation().x + comboAudioOut.getWidth() + 7,
				caseConvertAudioFramerate.getLocation().y + 3);
	
		if (getLanguage.equals(Locale.of("nl").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("ru").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("uk").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("id").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("ro").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("fi").getDisplayLanguage()) == false
		&& getLanguage.equals(Locale.of("bg").getDisplayLanguage()) == false)
		{
			grpAudio.add(lblAudioIs);
		}
	}
}
