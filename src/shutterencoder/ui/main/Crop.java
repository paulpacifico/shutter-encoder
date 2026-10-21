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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.library.FFPROBE;
import shutterencoder.library.LibraryUtils;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class Crop extends Main {

	public static void grpCrop() {

		grpCrop = new CollapsiblePanel(language.getProperty("frameCropImage"), true);
		grpCrop.setLayout(null);
		grpCrop.setVisible(false);
		grpCrop.setOpaque(false);
		grpCrop.setBackground(Utils.c30);
		grpCrop.setSize(312, 17);
		grpCrop.setLocation(frame.getWidth(), grpImageAdjustement.getY());
		frame.getContentPane().add(grpCrop);

		grpCrop.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpCrop, 90);
			}

		});

		caseEnableCrop = new JCheckBox(Main.language.getProperty("enable"));
		caseEnableCrop.setName("caseEnableCrop");
		caseEnableCrop.setBounds(8, 16, caseEnableCrop.getPreferredSize().width + 4, 23);
		caseEnableCrop.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseEnableCrop.setSelected(false);
		grpCrop.add(caseEnableCrop);

		caseEnableCrop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseEnableCrop.isSelected())
				{
					// Make sure the crop is accurate to pixel resolution
					float squareRatio = (float) FFPROBE.imageWidth / FFPROBE.imageHeight;
					if (Main.inputDeviceIsRunning == false && FFPROBE.imageRatio != squareRatio) {
						ratioChanged = true;
						FFPROBE.imageRatio = squareRatio;
						VideoPlayerUI.resizeAll();

						VideoPlayerUI.frameIsComplete = false;

						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
					}

					// Important
					selection.setBounds(VideoPlayerUI.player.getWidth() / 4, VideoPlayerUI.player.getHeight() / 4,
							VideoPlayerUI.player.getWidth() / 2, VideoPlayerUI.player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();

					for (Component c : grpCrop.getComponents()) {
						c.setEnabled(true);
					}

					frameCropX = VideoPlayerUI.player.getLocation().x;
					frameCropY = VideoPlayerUI.player.getLocation().y;

					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();
					VideoPlayerOverlay.checkSelection();

					VideoPlayerUI.player.add(selection);
					VideoPlayerUI.player.add(overImage);
				} else {
					// Come back to original DAR
					if (Main.inputDeviceIsRunning == false && ratioChanged && VideoPlayerCore.videoPath != null) {
						FFPROBE.Data(VideoPlayerCore.videoPath);
						try {
							FFPROBE.processData.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}
						
						VideoPlayerUI.resizeAll();

						VideoPlayerUI.frameIsComplete = false;

						VideoPlayerCore.playerSetTime(VideoPlayerCore.playerCurrentFrame);
					}
					ratioChanged = false;

					for (Component c : grpCrop.getComponents()) {
						if (c instanceof JCheckBox == false) {
							c.setEnabled(false);
						}
					}

					VideoPlayerUI.player.remove(selection);
					VideoPlayerUI.player.remove(overImage);

					comboPreset.setSelectedIndex(0);
				}
				
				LibraryUtils.checkGPUDeinterlacing();

				VideoPlayerUI.player.repaint();
				
				if (VideoPlayerCore.frameVideo != null)
				{
					FFPROBE.setFilesize();
				}
			}

		});

		JLabel lblPresets = new JLabel(Main.language.getProperty("lblPresets"));
		lblPresets.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblPresets.setEnabled(false);
		lblPresets.setBounds(caseEnableCrop.getX() + caseEnableCrop.getWidth() + 7, caseEnableCrop.getY() + 3,
				lblPresets.getPreferredSize().width, 16);
		grpCrop.add(lblPresets);

		final String presetsList[] = { language.getProperty("aucun"), "auto", "2.75", "2.55", "2.39", "2.35", "2.33",
				"1.91", "1.85", "16/9", "4/3", "4/5", "1", "9/16" };

		comboPreset = new JComboBox<String>();
		comboPreset.setName("comboPreset");
		comboPreset.setModel(new DefaultComboBoxModel<String>(presetsList));
		comboPreset.setMaximumRowCount(10);
		comboPreset.setEnabled(false);
		comboPreset.setEditable(true);
		comboPreset.setSelectedIndex(0);
		comboPreset.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		comboPreset.setBounds(lblPresets.getX() + lblPresets.getWidth() + 4, lblPresets.getY(), 82, 16);
		grpCrop.add(comboPreset);

		ActionListener comboListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (comboPreset.getSelectedIndex() == 0) //None
				{
					cropLock.setVisible(false);
					
					selection.setBounds(VideoPlayerUI.player.getWidth() / 4, VideoPlayerUI.player.getHeight() / 4,
							VideoPlayerUI.player.getWidth() / 2, VideoPlayerUI.player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();

					VideoPlayerOverlay.checkSelection();
				}
				else if (comboPreset.getSelectedIndex() == 1) // Auto
				{
					cropLock.setVisible(false);
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {

							File file = new File(VideoPlayerCore.videoPath);

							LibraryUtils.setCropDetect(file);

							frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					});
					t.start();
				}
				else if (comboPreset.getSelectedItem().toString().isEmpty() == false)
				{
					try {

						String s[] = FFPROBE.imageResolution.split("x");
						int w = Integer.parseInt(s[0]);
						int h = Integer.parseInt(s[1]);

						String inputRatio = String.valueOf((float) Integer.parseInt(s[0]) / Integer.parseInt(s[1]));

						if (inputRatio.length() > 4)
							inputRatio = inputRatio.substring(0, 4);

						float outputRatio;						
						if (comboPreset.getSelectedItem().toString().contains("/")) {
							String d[] = comboPreset.getSelectedItem().toString().split("/");
							outputRatio = (float) Integer.parseInt(d[0]) / Integer.parseInt(d[1]);
						} else
							outputRatio = Float.parseFloat(comboPreset.getSelectedItem().toString());

						if (outputRatio == 1.33f) {
							outputRatio = 4 / 3f;
						} else if (outputRatio == 0.8f) {
							outputRatio = 4 / 5f;
						} else if (outputRatio == 1.77f) {
							outputRatio = 16 / 9f;
						} else if (outputRatio == 2.33f) {
							outputRatio = 21 / 9f;
						}

						if (outputRatio < Float.parseFloat(inputRatio)) {
							textCropPosY.setText("0");
							textCropHeight.setText(s[1]);
							textCropWidth.setText(String.valueOf(Math.round(h * outputRatio)));
							textCropPosX.setText(String.valueOf((w - Integer.parseInt(textCropWidth.getText())) / 2));
						} else {
							textCropPosX.setText("0");
							textCropWidth.setText(s[0]);
							textCropHeight.setText(String.valueOf(Math.round(w / outputRatio)));
							textCropPosY.setText(String.valueOf((h - Integer.parseInt(textCropHeight.getText())) / 2));
						}

						int x = (int) Math.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayerUI.player.getHeight()) / FFPROBE.imageHeight);
						
						int y = (int) Math.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayerUI.player.getWidth()) / FFPROBE.imageWidth);
						
						int width = (int) Math.ceil((float) (Integer.valueOf(textCropWidth.getText()) * VideoPlayerUI.player.getHeight()) / FFPROBE.imageHeight);
						int height = (int) Math.floor((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayerUI.player.getWidth()) / FFPROBE.imageWidth);

						if (width > VideoPlayerUI.player.getWidth())
							width = VideoPlayerUI.player.getWidth();

						if (height > VideoPlayerUI.player.getHeight())
							height = VideoPlayerUI.player.getHeight();

						selection.setBounds(x, y, width, height);

						cropLock.setVisible(true);
						
					} catch (Exception er) {}
				}
			}

		};

		comboPreset.addActionListener(comboListener);
		
		cropLock = new JLabel(new FlatSVGIcon("resources/unlock.svg", 16, 16));
		cropLock.setName("cropUnlock");
		cropLock.setVisible(false);
		cropLock.setHorizontalAlignment(SwingConstants.CENTER);
		cropLock.setBounds(comboPreset.getX() + comboPreset.getWidth() + 3, comboPreset.getY(), 21, 21);
		grpCrop.add(cropLock);

		cropLock.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (cropIsLocked) {
					cropLock.setIcon(new FlatSVGIcon("resources/unlock.svg", 16, 16));
					cropIsLocked = false;
					cropLock.setName("cropUnlock");
				} else {
					cropLock.setIcon(new FlatSVGIcon("resources/lock.svg", 16, 16));
					cropIsLocked = true;
					cropLock.setName("cropLock");
				}

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});
		
		// Selection
		selection = new JPanel();
		selection.setOpaque(false);
		selection.setBorder(BorderFactory.createDashedBorder(Utils.themeColor, 4, 4));

		selection.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				selectionDrag = true;

				// Mouse position from click mouse
				int mouseX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - startCropX - frameCropX;
				int mouseY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - startCropY - frameCropY;

				if (mouseX < 0)
					mouseX = 0;

				if (mouseY < 0)
					mouseY = 0;

				int mouseInPictureX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - frameCropX;
				int mouseInPictureY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - frameCropY;

				float selectionRatio = 1;
				if (comboPreset.getSelectedItem().toString().contains("/"))
				{
					String d[] = comboPreset.getSelectedItem().toString().split("/");
					selectionRatio = (float) Integer.parseInt(d[0]) / Integer.parseInt(d[1]);
				}
				else if (comboPreset.getSelectedItem().toString().contains("."))
				{
					selectionRatio = Float.parseFloat(comboPreset.getSelectedItem().toString());
				}
				
				if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
				{
					if (mouseInPictureX >= 0 && mouseInPictureY >= 0)
					{
						selection.setLocation(mouseX, mouseY);

						if (shift)
						{
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), 2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
							selection.setSize(anchorRight - mouseX, anchorBottom - mouseY);
					}
					else
					{
						if (mouseInPictureX < 0)
						{
							selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
						}
						else if (mouseInPictureY < 0)
						{
							selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());
						}
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
				{
					if (mouseInPictureY >= 0)
					{
						selection.setLocation(selection.getLocation().x, mouseY);

						if (shift)
						{
							selection.setSize(selection.getSize().width, 2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
							selection.setSize(selection.getSize().width, anchorBottom - mouseY);
						
					}
					else
						selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());					
					
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
				{
					if (mouseInPictureY >= 0 && mouseInPictureX <= VideoPlayerUI.player.getWidth())
					{
						if (shift)
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()),
									mouseY);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()),
									2 * anchorBottom - 2 * mouseY - selection.getHeight());
						}
						else
						{
							selection.setLocation(selection.getLocation().x, mouseY);
							selection.setSize(e.getX(), anchorBottom - mouseY);
						}
						
					}
					else
					{
						if (mouseInPictureY < 0)
						{
							selection.setBounds(selection.getX(), 0, selection.getWidth(), selection.getHeight());
						}
						else if (mouseInPictureX > VideoPlayerUI.player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), VideoPlayerUI.player.getWidth() - selection.getX(), selection.getHeight());
						}
					}
					
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
				{
					if (mouseInPictureX <= VideoPlayerUI.player.getWidth())
					{
						if (shift)
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y);
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), selection.getSize().height);							
						}
						else
							selection.setSize(e.getX(), selection.getSize().height);
						
					}
					else
					{
						selection.setBounds(selection.getX(), selection.getY(), VideoPlayerUI.player.getWidth() - selection.getX(), selection.getHeight());
					}

				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))
				{
					if (mouseInPictureY <= VideoPlayerUI.player.getHeight() && mouseInPictureX <= VideoPlayerUI.player.getWidth())
					{ 
						if (shift)
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - e.getX()), selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(e.getX() - (selection.getWidth() - e.getX()), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(e.getX(), e.getY());
						
					} else
					{
						if (mouseInPictureY > VideoPlayerUI.player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayerUI.player.getHeight() - selection.getY());
						}
						else if (mouseInPictureX > VideoPlayerUI.player.getWidth())
						{
							selection.setBounds(selection.getX(), selection.getY(), VideoPlayerUI.player.getWidth() - selection.getX(), selection.getHeight());
						}
					}

				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
				{
					if (mouseInPictureY < VideoPlayerUI.player.getHeight())
					{
						if (shift)
						{
							textCropPosY.setText("0");
							
							selection.setLocation(selection.getLocation().x, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(selection.getSize().width, e.getY() - (selection.getHeight() - e.getY()));
						}
						else
							selection.setSize(selection.getSize().width, e.getY());
						
					}
					else
					{
						selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayerUI.player.getHeight() - selection.getY());
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
				{
					if (mouseInPictureY <= VideoPlayerUI.player.getHeight() && mouseInPictureX >= 0)
					{
						if (shift)
						{
							selection.setLocation(mouseX, selection.getLocation().y + (selection.getHeight() - e.getY()));
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), e.getY() - (selection.getHeight() - e.getY()));
						}
						else
						{
							selection.setLocation(mouseX, selection.getLocation().y);
							selection.setSize(anchorRight - mouseX, e.getY());
						}
						
					}
					else
					{
						if (mouseInPictureY > VideoPlayerUI.player.getHeight())
						{
							selection.setBounds(selection.getX(), selection.getY(), selection.getWidth(), VideoPlayerUI.player.getHeight() - selection.getY());
						}
						else if (mouseInPictureX < 0)
						{
							selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
						}
					}
				}
				else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))
				{
					if (mouseInPictureX >= 0)
					{
						selection.setLocation(mouseX, selection.getLocation().y);

						if (shift)
						{
							selection.setSize(2 * anchorRight - 2 * mouseX - selection.getWidth(), selection.getSize().height);
						}
						else
							selection.setSize(anchorRight - mouseX, selection.getSize().height);
					}
					else
					{
						selection.setBounds(0, selection.getY(), selection.getWidth(), selection.getHeight());
					}
					
				} else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
				{
					if (shift && ctrl)
					{
						selection.setLocation(mouseX, selection.getLocation().y);
					}
					else if (shift)
					{
						selection.setLocation(selection.getLocation().x, mouseY);
					}
					else if (ctrl)
					{
						selection.setLocation(mouseX, selection.getLocation().y);
					}
					else
						selection.setLocation(mouseX, mouseY);
				}		

				if (cropLock.isVisible() && (cropIsLocked || alt))
				{				
					if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) || frame.getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
					{
						int newValue = (int) Math.round((float) selection.getWidth() / selectionRatio);
						
						if (newValue < VideoPlayerUI.player.getHeight())
						{
							selection.setSize(selection.getWidth(), newValue);
						}
						else
						{
							newValue = (int) Math.round((float) VideoPlayerUI.player.getHeight() * selectionRatio);							
							selection.setSize(newValue, VideoPlayerUI.player.getHeight());
						}							
					}
					else
					{
						int newValue = (int) Math.round((float) selection.getHeight() * selectionRatio);
						
						if (shift)
						{
							if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR) == false && frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR) == false)
							{
								selection.setLocation(selection.getLocation().x + (selection.getWidth() - newValue), selection.getLocation().y);
							}
						}
						else if (frame.getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR) || frame.getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))
						{
							selection.setLocation(selection.getLocation().x + (selection.getWidth() - newValue), selection.getLocation().y);
						}
						
						if (newValue < VideoPlayerUI.player.getWidth())
						{
							selection.setSize(newValue, selection.getHeight());
						}
						else
						{
							newValue = (int) Math.round((float) VideoPlayerUI.player.getWidth() / selectionRatio);
							selection.setSize(VideoPlayerUI.player.getWidth(), newValue);
						}
					}
				}
				else
				{
					comboPreset.removeActionListener(comboListener);
					float ratio = (float) selection.getWidth() / selection.getHeight();
					comboPreset.setSelectedItem(Math.round(ratio * 100.0) / 100.0);
					comboPreset.addActionListener(comboListener);
					
					cropLock.setVisible(false);
				}
				
				// Location limits
				if (selection.getX() < 0) {
					selection.setLocation(0, selection.getY());
				}

				if (selection.getY() < 0) {
					selection.setLocation(selection.getX(), 0);
				}

				if (selection.getX() + selection.getWidth() > VideoPlayerUI.player.getWidth()) {
					selection.setLocation(VideoPlayerUI.player.getWidth() - selection.getWidth(), selection.getY());
				}

				if (selection.getY() + selection.getHeight() > VideoPlayerUI.player.getHeight()) {
					selection.setLocation(selection.getX(), VideoPlayerUI.player.getHeight() - selection.getHeight());
				}

				// Anchor points
				anchorRight = selection.getLocation().x + selection.getWidth();
				anchorBottom = selection.getLocation().y + selection.getHeight();

				VideoPlayerOverlay.checkSelection();
			}

			@Override
			public void mouseMoved(MouseEvent e) {

				if (selectionDrag == false) {
					if (e.getX() <= 10 && e.getY() <= 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					} else if (e.getX() <= selection.getWidth() - 10 && e.getY() <= 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					} else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10
							&& e.getY() <= 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					} else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10
							&& e.getY() >= 10 && e.getY() <= selection.getHeight() - 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					} else if (e.getX() <= selection.getWidth() && e.getX() >= selection.getWidth() - 10
							&& e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					} else if (e.getX() <= selection.getWidth() - 10 && e.getX() >= 10
							&& e.getY() <= selection.getHeight() && e.getY() >= selection.getHeight() - 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					} else if (e.getX() <= 10 && e.getY() <= selection.getHeight()
							&& e.getY() >= selection.getHeight() - 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					} else if (e.getX() <= 10 && e.getY() >= 10 && e.getY() <= selection.getHeight() - 10) {
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					} else
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}

		});

		selection.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				startCropX = e.getPoint().x;
				startCropY = e.getPoint().y;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				selectionDrag = false;

				if (selection.getLocation().x <= 0 && selection.getLocation().y <= 0) {
					selection.setLocation(0, 0);
				} else if (selection.getLocation().x + selection.getWidth() > VideoPlayerUI.player.getWidth()
						&& selection.getLocation().y <= 0) {
					selection.setLocation(VideoPlayerUI.player.getWidth() - selection.getWidth(), 0);
				} else if (selection.getLocation().x <= 0
						&& selection.getLocation().y + selection.getHeight() > VideoPlayerUI.player.getHeight()) {
					selection.setLocation(0, VideoPlayerUI.player.getHeight() - selection.getHeight());
				} else if (selection.getLocation().x + selection.getWidth() > VideoPlayerUI.player.getWidth()
						&& selection.getLocation().y + selection.getHeight() > VideoPlayerUI.player.getHeight()) {
					selection.setLocation(VideoPlayerUI.player.getWidth() - selection.getWidth(),
							VideoPlayerUI.player.getHeight() - selection.getHeight());
				} else if (selection.getLocation().x + selection.getWidth() > VideoPlayerUI.player.getWidth()) {
					selection.setLocation(VideoPlayerUI.player.getWidth() - selection.getWidth(),
							selection.getLocation().y);
				} else if (selection.getLocation().y + selection.getHeight() > VideoPlayerUI.player.getHeight()) {
					selection.setLocation(selection.getLocation().x,
							VideoPlayerUI.player.getHeight() - selection.getHeight());
				} else if (selection.getLocation().x <= 0) {
					selection.setLocation(0, selection.getLocation().y);
				} else if (selection.getLocation().y <= 0) {
					selection.setLocation(selection.getLocation().x, 0);
				}
			}

		});

		// Outside of selection
		overImage = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				this.setBounds(0, 0, VideoPlayerUI.player.getWidth(), VideoPlayerUI.player.getHeight());

				Area outter = new Area(
						new Rectangle(0, 0, VideoPlayerUI.player.getWidth(), VideoPlayerUI.player.getHeight()));
				Rectangle inner = new Rectangle(selection.getLocation().x, selection.getLocation().y,
						selection.getWidth(), selection.getHeight());
				outter.subtract(new Area(inner));

				g2d.setColor(new Color(0, 0, 0, 180));
				g2d.fill(outter);

				FFPROBE.setFilesize();
			}
		};
		overImage.setSize(640, 360);
		overImage.setOpaque(false);
		overImage.setLayout(null);

		overImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2 && !e.isConsumed()) {
					selection.setBounds(VideoPlayerUI.player.getWidth() / 4, VideoPlayerUI.player.getHeight() / 4,
							VideoPlayerUI.player.getWidth() / 2, VideoPlayerUI.player.getHeight() / 2);
					anchorRight = selection.getLocation().x + selection.getWidth();
					anchorBottom = selection.getLocation().y + selection.getHeight();
					VideoPlayerOverlay.checkSelection();
					comboPreset.setSelectedIndex(0);
				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {

				if (selectionDrag == false)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}

		});

		JLabel cropPosX = new JLabel(Main.language.getProperty("posX"));
		cropPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		cropPosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPosX.setForeground(Utils.themeColor);
		cropPosX.setEnabled(false);
		cropPosX.setBounds(24, caseEnableCrop.getY() + caseEnableCrop.getHeight() + 4,
				cropPosX.getPreferredSize().width, 16);

		textCropPosX = new JTextField("0");
		textCropPosX.setName("textCropPosX");
		textCropPosX.setBounds(cropPosX.getLocation().x + cropPosX.getWidth() + 2, cropPosX.getLocation().y, 34, 16);
		textCropPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropPosX.setEnabled(false);
		textCropPosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		JLabel cropPx1 = new JLabel("px");
		cropPx1.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPx1.setForeground(Utils.themeColor);
		cropPx1.setEnabled(false);
		cropPx1.setBounds(textCropPosX.getLocation().x + textCropPosX.getWidth() + 2, cropPosX.getLocation().y,
				cropPx1.getPreferredSize().width, 16);

		textCropPosX.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textCropPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropPosX.getText()) * VideoPlayerUI.player.getHeight())
									/ FFPROBE.imageHeight);
					selection.setLocation(value, selection.getLocation().y);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textCropPosX.getText().length() >= 4)
					textCropPosX.setText("");
			}

		});

		textCropPosX.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionX = e.getX();
				mouseCropOffsetX = Integer.parseInt(textCropPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textCropPosX.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textCropPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textCropPosX.setText(String.valueOf(mouseCropOffsetX + (e.getX() - MousePositionX)));
					selection.setLocation((int) Math.round(Integer.valueOf(textCropPosX.getText()) / playerRatio),
							selection.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel cropPosY = new JLabel(Main.language.getProperty("posY"));
		cropPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		cropPosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPosY.setForeground(Utils.themeColor);
		cropPosY.setEnabled(false);
		cropPosY.setBounds(cropPx1.getX() + cropPx1.getWidth() + 30, cropPosX.getLocation().y, cropPosX.getWidth(), 16);

		textCropPosY = new JTextField("0");
		textCropPosY.setName("textCropPosY");
		textCropPosY.setBounds(cropPosY.getLocation().x + cropPosY.getWidth() + 2, cropPosY.getLocation().y,
				textCropPosX.getWidth(), 16);
		textCropPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropPosY.setEnabled(false);
		textCropPosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textCropPosY.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textCropPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropPosY.getText()) * VideoPlayerUI.player.getWidth())
									/ FFPROBE.imageWidth);
					selection.setLocation(selection.getLocation().x, value);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textCropPosY.getText().length() >= 4)
					textCropPosY.setText("");
			}

		});

		textCropPosY.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionY = e.getY();
				mouseCropOffsetY = Integer.parseInt(textCropPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textCropPosY.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textCropPosY.setText(String.valueOf(mouseCropOffsetY + (e.getY() - MousePositionY)));
					selection.setLocation(selection.getLocation().x,
							(int) Math.round(Integer.valueOf(textCropPosY.getText()) / playerRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel cropPx2 = new JLabel("px");
		cropPx2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPx2.setForeground(Utils.themeColor);
		cropPx2.setEnabled(false);
		cropPx2.setBounds(textCropPosY.getLocation().x + textCropPosY.getWidth() + 2, cropPosY.getLocation().y,
				cropPosX.getPreferredSize().width, 16);

		JLabel lblWidth = new JLabel(Main.language.getProperty("lblWidth"));
		lblWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWidth.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblWidth.setForeground(Utils.themeColor);
		lblWidth.setEnabled(false);
		lblWidth.setBounds(textCropPosX.getX() - lblWidth.getPreferredSize().width - 2,
				cropPosX.getY() + cropPosX.getHeight() + 4, lblWidth.getPreferredSize().width, 16);

		textCropWidth = new JTextField("0");
		textCropWidth.setName("textCropWidth");
		textCropWidth.setBounds(textCropPosX.getX(), textCropPosX.getY() + textCropPosX.getHeight() + 4,
				textCropPosX.getWidth(), 16);
		textCropWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropWidth.setEnabled(false);
		textCropWidth.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textCropWidth.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textCropWidth.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropWidth.getText()) * VideoPlayerUI.player.getHeight())
									/ FFPROBE.imageHeight);
					selection.setSize(value, selection.getHeight());
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textCropWidth.getText().length() >= 4)
					textCropWidth.setText("");
				
				comboPreset.removeActionListener(comboListener);
				float ratio = (float) selection.getWidth() / selection.getHeight();
				comboPreset.setSelectedItem(Math.round(ratio * 100.0) / 100.0);
				comboPreset.addActionListener(comboListener);
				
				cropLock.setVisible(false);	
			}

		});

		textCropWidth.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionX = e.getX();
				mouseCropOffsetX = Integer.parseInt(textCropWidth.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textCropWidth.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropWidth.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textCropWidth.setText(String.valueOf(mouseCropOffsetX + (e.getX() - MousePositionX)));
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropWidth.getText()) * VideoPlayerUI.player.getHeight())
									/ FFPROBE.imageHeight);
					selection.setSize(value, selection.getHeight());
				}

				comboPreset.removeActionListener(comboListener);
				float ratio = (float) selection.getWidth() / selection.getHeight();
				comboPreset.setSelectedItem(Math.round(ratio * 100.0) / 100.0);
				comboPreset.addActionListener(comboListener);
				
				cropLock.setVisible(false);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel cropPx3 = new JLabel("px");
		cropPx3.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPx3.setForeground(Utils.themeColor);
		cropPx3.setEnabled(false);
		cropPx3.setBounds(cropPx1.getX(), lblWidth.getY(), cropPosX.getPreferredSize().width, 16);

		JLabel lblHeight = new JLabel(Main.language.getProperty("lblHeight"));
		lblHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHeight.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblHeight.setForeground(Utils.themeColor);
		lblHeight.setEnabled(false);
		lblHeight.setBounds(textCropPosY.getX() - lblHeight.getPreferredSize().width - 2,
				cropPosY.getY() + cropPosY.getHeight() + 4, lblHeight.getPreferredSize().width, 16);

		textCropHeight = new JTextField("0");
		textCropHeight.setName("textCropHeight");
		textCropHeight.setBounds(lblHeight.getLocation().x + lblHeight.getWidth() + 2, textCropWidth.getY(),
				textCropPosX.getWidth(), 16);
		textCropHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		textCropHeight.setEnabled(false);
		textCropHeight.setFont(new Font(Main.mainFont, Font.PLAIN, 12));

		textCropHeight.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textCropHeight.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayerUI.player.getWidth())
									/ FFPROBE.imageWidth);
					selection.setSize(selection.getWidth(), value);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textCropHeight.getText().length() >= 4)
					textCropHeight.setText("");
				
				comboPreset.removeActionListener(comboListener);
				float ratio = (float) selection.getWidth() / selection.getHeight();
				comboPreset.setSelectedItem(Math.round(ratio * 100.0) / 100.0);
				comboPreset.addActionListener(comboListener);
				
				cropLock.setVisible(false);				
			}

		});

		textCropHeight.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textCropHeight.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MousePositionY = e.getY();
				mouseCropOffsetY = Integer.parseInt(textCropHeight.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textCropHeight.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textCropHeight.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textCropHeight.setText(String.valueOf(mouseCropOffsetY + (e.getY() - MousePositionY)));
					int value = (int) Math
							.round((float) (Integer.valueOf(textCropHeight.getText()) * VideoPlayerUI.player.getHeight())
									/ FFPROBE.imageHeight);
					selection.setSize(selection.getWidth(), value);
				}
				
				comboPreset.removeActionListener(comboListener);
				float ratio = (float) selection.getWidth() / selection.getHeight();
				comboPreset.setSelectedItem(Math.round(ratio * 100.0) / 100.0);
				comboPreset.addActionListener(comboListener);
				
				cropLock.setVisible(false);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel cropPx4 = new JLabel("px");
		cropPx4.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		cropPx4.setForeground(Utils.themeColor);
		cropPx4.setEnabled(false);
		cropPx4.setBounds(cropPx2.getX(), lblHeight.getY(), cropPosX.getPreferredSize().width, 16);

		grpCrop.add(cropPosX);
		grpCrop.add(textCropPosX);
		grpCrop.add(cropPx1);
		grpCrop.add(cropPosY);
		grpCrop.add(textCropPosY);
		grpCrop.add(cropPx2);
		grpCrop.add(lblWidth);
		grpCrop.add(textCropWidth);
		grpCrop.add(cropPx3);
		grpCrop.add(lblHeight);
		grpCrop.add(textCropHeight);
		grpCrop.add(cropPx4);

		for (Component c : grpCrop.getComponents()) {
			if (c instanceof JCheckBox == false) {
				c.setEnabled(false);
			}
		}
	}
}
