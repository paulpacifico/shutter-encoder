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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import shutterencoder.library.LibraryUtils;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Corrections extends Main {

	public static void grpCorrections() {

		grpCorrections = new CollapsiblePanel(language.getProperty("grpCorrections"), true);
		grpCorrections.setLayout(null);
		grpCorrections.setVisible(false);
		grpCorrections.setOpaque(false);
		grpCorrections.setBackground(Utils.c30);
		grpCorrections.setBounds(frame.getWidth(), grpImageAdjustement.getY() + grpImageAdjustement.getHeight() + 6,
				312, 17);
		frame.getContentPane().add(grpCorrections);

		grpCorrections.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				int size = 25;
				for (Component c : grpCorrections.getComponents()) {
					if (c instanceof JCheckBox)
						size += 17;
				}

				UIController.extendSections(grpCorrections, size);
			}

		});

		caseStabilisation = new JCheckBox(Main.language.getProperty("caseStabilisation"));
		caseStabilisation.setName("caseStabilisation");
		caseStabilisation.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseStabilisation.setSize(caseStabilisation.getPreferredSize().width, 23);

		caseStabilisation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LibraryUtils.checkGPUDeinterlacing();
			}
			
		});
		
		caseDeflicker = new JCheckBox(Main.language.getProperty("caseDeflicker"));
		caseDeflicker.setName("caseDeflicker");
		caseDeflicker.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseDeflicker.setSize(caseDeflicker.getPreferredSize().width, 23);

		caseDeflicker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
				        VideoPlayerUtils.loadImageThread.join();
				    } catch (InterruptedException e) {
				        Thread.currentThread().interrupt();
				    }
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});
		
		caseLimiter = new JCheckBox(Main.language.getProperty("caseLimiter"));
		caseLimiter.setName("caseLimiter");
		caseLimiter.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseLimiter.setSize(caseLimiter.getPreferredSize().width, 23);

		caseLimiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		caseDetails = new JCheckBox(Main.language.getProperty("caseDetails"));
		caseDetails.setName("caseDetails");
		caseDetails.setToolTipText(Main.language.getProperty("tooltipDetails"));
		caseDetails.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseDetails.setSize(170, 23);

		caseDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderDetails = new JSlider();
		sliderDetails.setName("sliderDetails");
		sliderDetails.setMinorTickSpacing(1);
		sliderDetails.setMaximum(10);
		sliderDetails.setMinimum(-10);
		sliderDetails.setValue(0);
		sliderDetails.setSize(120, 22);

		sliderDetails.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				float value = (float) sliderDetails.getValue() / 10;

				if (value != 0)
					caseDetails.setSelected(true);
				else
					caseDetails.setSelected(false);

				caseDetails.setText(Main.language.getProperty("details") + " " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderDetails.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {				
				caseDetails.setSelected(true);
				sliderDetails.setEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderDetails.setValue(0);
			}

		});
		
		caseDeblock = new JCheckBox("Deblock: 0");
		caseDeblock.setName("caseDeblock");
		caseDeblock.setToolTipText(Main.language.getProperty("tooltipDeblock"));
		caseDeblock.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseDeblock.setSize(caseDetails.getWidth(), 23);

		caseDeblock.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderDeblock = new JSlider();
		sliderDeblock.setName("sliderDeblock");
		sliderDeblock.setMinorTickSpacing(1);
		sliderDeblock.setMaximum(100);
		sliderDeblock.setMinimum(0);
		sliderDeblock.setValue(0);
		sliderDeblock.setSize(120, 22);

		sliderDeblock.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				int value = sliderDeblock.getValue();

				if (value != 0)
					caseDeblock.setSelected(true);
				else
					caseDeblock.setSelected(false);

				caseDeblock.setText("Deblock: " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderDeblock.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {				
				caseDeblock.setSelected(true);
				sliderDeblock.setEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderDeblock.setValue(0);
			}

		});
		
		caseBanding = new JCheckBox(Main.language.getProperty("caseBanding") + Main.language.getProperty("colon") + " 0");
		caseBanding.setName("caseBanding");
		caseBanding.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseBanding.setSize(caseDetails.getWidth(), 23);

		caseBanding.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderBanding = new JSlider();
		sliderBanding.setName("sliderBanding");
		sliderBanding.setMinorTickSpacing(1);
		sliderBanding.setMaximum(100);
		sliderBanding.setMinimum(0);
		sliderBanding.setValue(0);
		sliderBanding.setSize(120, 22);

		sliderBanding.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				int value = sliderBanding.getValue();

				if (value != 0)
					caseBanding.setSelected(true);
				else
					caseBanding.setSelected(false);

				caseBanding.setText(Main.language.getProperty("caseBanding") + Main.language.getProperty("colon") + " " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderBanding.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {				
				caseBanding.setSelected(true);
				sliderBanding.setEnabled(true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderBanding.setValue(0);
			}

		});

		caseAddGrain = new JCheckBox(Main.language.getProperty("caseAddGrain") + " 0");
		caseAddGrain.setName("caseAddGrain");
		caseAddGrain.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAddGrain.setSize(caseDetails.getWidth(), 23);

		caseAddGrain.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderAddGrain = new JSlider();
		sliderAddGrain.setName("sliderAddGrain");
		sliderAddGrain.setMinorTickSpacing(1);
		sliderAddGrain.setMaximum(100);
		sliderAddGrain.setMinimum(0);
		sliderAddGrain.setValue(0);
		sliderAddGrain.setSize(sliderDetails.getWidth(), 22);

		sliderAddGrain.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				int value = sliderAddGrain.getValue();

				if (value != 0)
					caseAddGrain.setSelected(true);
				else
					caseAddGrain.setSelected(false);

				caseAddGrain.setText(Main.language.getProperty("caseAddGrain") + " " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderAddGrain.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseAddGrain.setSelected(true);
				sliderAddGrain.setEnabled(true);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderAddGrain.setValue(0);
			}

		});
		
		caseDenoise = new JCheckBox(Main.language.getProperty("caseBruit"));
		caseDenoise.setName("caseDenoise");
		caseDenoise.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseDenoise.setSize(caseDetails.getWidth(), 23);

		caseDenoise.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderDenoise = new JSlider();
		sliderDenoise.setName("sliderDenoise");
		sliderDenoise.setMinorTickSpacing(1);
		sliderDenoise.setMaximum(10);
		sliderDenoise.setMinimum(0);
		sliderDenoise.setValue(0);
		sliderDenoise.setSize(sliderDetails.getWidth(), 22);

		sliderDenoise.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				int value = sliderDenoise.getValue();

				if (value != 0)
					caseDenoise.setSelected(true);
				else
					caseDenoise.setSelected(false);

				caseDenoise.setText(Main.language.getProperty("noiseSuppression") + " " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderDenoise.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseDenoise.setSelected(true);
				sliderDenoise.setEnabled(true);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderDenoise.setValue(0);
			}

		});

		caseSmoothExposure = new JCheckBox(Main.language.getProperty("caseExposure"));
		caseSmoothExposure.setName("caseSmoothExposure");
		caseSmoothExposure.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseSmoothExposure.setSize(caseDetails.getWidth(), 23);

		caseSmoothExposure.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (VideoPlayerUtils.playerIsPlaying())
				{
					try {
						VideoPlayerUtils.loadImageThread.join();
					} catch (InterruptedException er) {
					    Thread.currentThread().interrupt();
					}
					
					VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
				}
				else
					VideoPlayerUtils.loadImage();
			}

		});

		sliderSmoothExposure = new JSlider();
		sliderSmoothExposure.setName("sliderSmoothExposure");
		sliderSmoothExposure.setMinorTickSpacing(1);
		sliderSmoothExposure.setMaximum(100);
		sliderSmoothExposure.setMinimum(0);
		sliderSmoothExposure.setValue(0);
		sliderSmoothExposure.setSize(sliderDetails.getWidth(), 22);

		sliderSmoothExposure.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {

				int value = sliderSmoothExposure.getValue();

				if (value != 0)
					caseSmoothExposure.setSelected(true);
				else
					caseSmoothExposure.setSelected(false);

				caseSmoothExposure.setText(Main.language.getProperty("smoothExposure") + " " + value);

				VideoPlayerUtils.loadImage();
			}

		});

		sliderSmoothExposure.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				caseSmoothExposure.setSelected(true);
				sliderSmoothExposure.setEnabled(true);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				VideoPlayerUtils.restartPlayback();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					sliderSmoothExposure.setValue(0);
			}

		});

		caseStabilisation.setLocation(7, 14);
		grpCorrections.add(caseStabilisation);
		caseDeflicker.setLocation(7, caseStabilisation.getLocation().y + 17);
		grpCorrections.add(caseDeflicker);		
		caseLimiter.setLocation(7, caseDeflicker.getLocation().y + 17);
		grpCorrections.add(caseLimiter);			
		caseDetails.setLocation(7, caseLimiter.getLocation().y + 17);
		grpCorrections.add(caseDetails);
		sliderDetails.setLocation(grpCorrections.getWidth() - sliderDetails.getWidth() - 14, caseDetails.getLocation().y);
		grpCorrections.add(sliderDetails);			
		caseDeblock.setLocation(7, caseDetails.getLocation().y + 17);
		grpCorrections.add(caseDeblock);
		sliderDeblock.setLocation(grpCorrections.getWidth() - sliderDeblock.getWidth() - 14, caseDeblock.getLocation().y);
		grpCorrections.add(sliderDeblock);
		caseBanding.setLocation(7, caseDeblock.getLocation().y + 17);
		grpCorrections.add(caseBanding);		
		sliderBanding.setLocation(grpCorrections.getWidth() - sliderBanding.getWidth() - 14, caseBanding.getLocation().y);
		grpCorrections.add(sliderBanding);			
		caseAddGrain.setLocation(7, caseBanding.getLocation().y + 17);
		grpCorrections.add(caseAddGrain);
		sliderAddGrain.setLocation(sliderDetails.getX(), caseAddGrain.getLocation().y);
		grpCorrections.add(sliderAddGrain);		
		caseDenoise.setLocation(7, caseAddGrain.getLocation().y + 17);
		grpCorrections.add(caseDenoise);
		sliderDenoise.setLocation(sliderDetails.getX(), caseDenoise.getLocation().y);
		grpCorrections.add(sliderDenoise);
		caseSmoothExposure.setLocation(7, caseDenoise.getLocation().y + 17);
		grpCorrections.add(caseSmoothExposure);
		sliderSmoothExposure.setLocation(sliderDetails.getX(), caseSmoothExposure.getLocation().y);
		grpCorrections.add(sliderSmoothExposure);
	}
}
