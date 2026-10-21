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
import java.util.Arrays;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shutterencoder.library.FFPROBE;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class ImageSequence extends Main {

	public static void grpImageSequence() {

		grpImageSequence = new CollapsiblePanel(language.getProperty("grpSequenceImage"), true);
		grpImageSequence.setLayout(null);
		grpImageSequence.setVisible(false);
		grpImageSequence.setOpaque(false);
		grpImageSequence.setBackground(Utils.c30);
		grpImageSequence.setBounds(frame.getWidth(), 199, 312, 17);
		frame.getContentPane().add(grpImageSequence);

		grpImageSequence.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpImageSequence, 93);
			}

		});

		caseEnableSequence = new JCheckBox(language.getProperty("caseActiverSequence"));
		caseEnableSequence.setName("caseActiverSequence");
		caseEnableSequence.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseEnableSequence.setBounds(7, 16, caseEnableSequence.getPreferredSize().width, 23);
		grpImageSequence.add(caseEnableSequence);

		caseSequenceFPS = new JComboBox<String>();
		caseSequenceFPS.setName("caseSequenceFPS");
		caseSequenceFPS.setEnabled(false);
		caseSequenceFPS.setModel(new DefaultComboBoxModel<String>(new String[] { "23,98", "24", "25", "29,97", "30",
				"48", "50", "59,94", "60", "100", "120", "150", "200", "250" }));
		caseSequenceFPS.setSelectedIndex(2);
		caseSequenceFPS.setMaximumRowCount(20);
		caseSequenceFPS.setFont(new Font(mainFont, Font.PLAIN, 11));
		caseSequenceFPS.setEditable(true);
		caseSequenceFPS.setBounds(caseEnableSequence.getLocation().x + caseEnableSequence.getWidth() + 4,
				caseEnableSequence.getLocation().y + 3, 56, 16);
		grpImageSequence.add(caseSequenceFPS);

		JLabel lblSequenceFPS = new JLabel(Main.language.getProperty("fps"));
		lblSequenceFPS.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblSequenceFPS.setBounds(caseSequenceFPS.getLocation().x + caseSequenceFPS.getWidth() + 4, caseSequenceFPS.getLocation().y, lblSequenceFPS.getPreferredSize().width, 16);
		if (getLanguage.equals(Locale.of("ro").getDisplayLanguage()) == false)
			grpImageSequence.add(lblSequenceFPS);

		caseEnableSequence.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (list.size() > 0)
				{
					FFPROBE.analyzedMedia = null;
					
					if (caseEnableSequence.isSelected())
					{
						String[] data = new String[list.getSize()];
	
						for (int i = 0; i < list.getSize(); i++) {
							data[i] = (String) list.getElementAt(i);
						}
	
						Arrays.sort(data);
						list.clear();
	
						for (int i = 0; i < data.length; i++) {
							list.addElement(data[i].toString());
						}
						addToList.setVisible(false);
	
						caseSequenceFPS.setEnabled(true);
					}
					else
					{
						VideoPlayerCore.videoPath = null;					
						
						caseSequenceFPS.setEnabled(false);
					}
	
					VideoPlayerUtils.setMedia();
				}
			}

		});

		caseBlend = new JCheckBox(language.getProperty("caseBlend"));
		caseBlend.setName("caseBlend");
		caseBlend.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseBlend.setBounds(7, caseEnableSequence.getLocation().y + caseEnableSequence.getHeight(),
				caseBlend.getPreferredSize().width + 14, 23);
		grpImageSequence.add(caseBlend);

		caseBlend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseBlend.isSelected() == false)
					sliderBlend.setValue(0);

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

		sliderBlend = new JSlider();
		sliderBlend.setName("sliderBlend");
		sliderBlend.setMinorTickSpacing(1);
		sliderBlend.setMaximum(16);
		sliderBlend.setMinimum(0);
		sliderBlend.setValue(0);
		sliderBlend.setBounds(grpImageSequence.getWidth() - 120 - 14, caseBlend.getLocation().y, 120, 22);
		grpImageSequence.add(sliderBlend);

		sliderBlend.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = sliderBlend.getValue();

				if (value != 0)
					caseBlend.setSelected(true);
				else
					caseBlend.setSelected(false);

				caseBlend.setText(language.getProperty("blend") + " x" + value);
			}

		});

		sliderBlend.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				caseBlend.setSelected(true);
				sliderBlend.setEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				//Only visible when playing the video
				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
			}

		});

		caseMotionBlur = new JCheckBox(language.getProperty("caseMotionBlur"));
		caseMotionBlur.setName("caseMotionBlur");
		caseMotionBlur.setFont(new Font(mainFont, Font.PLAIN, 12));
		caseMotionBlur.setBounds(7, caseBlend.getHeight() + caseBlend.getLocation().y,
				caseMotionBlur.getPreferredSize().width, 23);
		grpImageSequence.add(caseMotionBlur);

		caseMotionBlur.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame); // Use VideoPlayer.resizeAll and reload the
																			// frame
			}

		});

	}
}
