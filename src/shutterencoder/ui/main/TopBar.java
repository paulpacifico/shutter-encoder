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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.library.FFMPEG;
import shutterencoder.ui.others.ReducedWindow;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class TopBar extends Main{

	public static void topBar() {

		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(Utils.background);
		topPanel.setBounds(0, 0, extendedWidth, 28);

		settingsIcon = new JLabel(new FlatSVGIcon("resources/settings.svg", 13, 13));
		settingsIcon.setHorizontalAlignment(SwingConstants.CENTER);		
		topPanel.add(settingsIcon);

		settingsIcon.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				settingsIcon.setIcon(new FlatSVGIcon("resources/settings.svg", 13, 13).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept)
				{
					Settings.frame.setLocation(frame.getX() + (frame.getWidth() - Settings.frame.getWidth()) / 2, frame.getY() + (frame.getHeight() - Settings.frame.getHeight()) / 2);
					Settings.frame.setVisible(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				settingsIcon.setIcon(new FlatSVGIcon("resources/settings.svg", 13, 13).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				settingsIcon.setIcon(new FlatSVGIcon("resources/settings.svg", 13, 13));
				accept = false;
			}

		});

		quit = new JLabel(new FlatSVGIcon("resources/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);		
		topPanel.add(quit);

		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));

				if (FFMPEG.isRunning && btnCancel.isEnabled()) {
					btnCancel.doClick();
				} else
					accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept)
				{
					Utils.exitApp(true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15));
				accept = false;
			}

		});

		expand = new JLabel(new FlatSVGIcon("resources/expand.svg", 15, 15));
		expand.setHorizontalAlignment(SwingConstants.CENTER);		
		topPanel.add(expand);

		expand.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				expand.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept) {
					UIController.toggleFullscreen();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				expand.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				expand.setIcon(new FlatSVGIcon("resources/expand.svg", 15, 15));
				accept = false;
			}

		});

		minimize = new JLabel(new FlatSVGIcon("resources/minimize.svg", 15, 15));
		minimize.setHorizontalAlignment(SwingConstants.CENTER);		
		topPanel.add(minimize);

		minimize.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				minimize.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@SuppressWarnings("static-access")
			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept) {
					if (inputDeviceIsRunning == false && Settings.btnDisableMinimizedWindow.isSelected() == false
							&& lblCurrentEncoding.getText().equals(language.getProperty("lblEncodageEnCours")) == false
							&& lblCurrentEncoding.getText().equals(language.getProperty("processCancelled")) == false
							&& lblCurrentEncoding.getText().equals(language.getProperty("processEnded")) == false) {
						new ReducedWindow();
					}

					frame.setState(frame.ICONIFIED);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				minimize.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				minimize.setIcon(new FlatSVGIcon("resources/minimize.svg", 15, 15));
				accept = false;
			}

		});

		help = new JLabel(new FlatSVGIcon("resources/help.svg", 15, 15));
		help.setHorizontalAlignment(SwingConstants.CENTER);		
		topPanel.add(help);

		help.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("resources/help.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept) {

					try {
						Desktop.getDesktop().browse(new URI("https://www.shutterencoder.com/documentation/"));
					} catch (IOException | URISyntaxException er) {}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("resources/help.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("resources/help.svg", 15, 15));
				accept = false;
			}

		});

		newInstance = new JLabel(new FlatSVGIcon("resources/new.svg", 15, 15));
		newInstance.setHorizontalAlignment(SwingConstants.CENTER);
		newInstance.setToolTipText(language.getProperty("newInstance"));
		topPanel.add(newInstance);

		newInstance.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("resources/new.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept) {
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

					if (frame.getLocation().x == dim.width / 2 - frame.getSize().width / 2
							&& frame.getLocation().y == dim.height / 2 - frame.getSize().height / 2) {
						frame.setLocation(frame.getLocation().x + new Random().nextInt(50),
								frame.getLocation().y + new Random().nextInt(50));
					}

					Utils.restartApp(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("resources/new.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("resources/new.svg", 15, 15));
				accept = false;
			}

		});

		lblShutterEncoder = new JLabel(language.getProperty("panelShutter"));
		lblShutterEncoder.setFont(new Font("Magneto", Font.PLAIN, 17));
		topPanel.add(lblShutterEncoder);

		lblV = new JLabel("v" + actualVersion);
		lblV.setFont(new Font(mainFont, Font.PLAIN, 12));
		lblV.setSize(lblV.getPreferredSize().width + 4, 16);
		topPanel.add(lblV);

		lblV.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.shutterencoder.com/changelog/"));
				} catch (IOException | URISyntaxException e) {
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblV.setFont(new Font(mainFont, Font.BOLD, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblV.setFont(new Font(mainFont, Font.PLAIN, 12));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

		});

		topImage = new JLabel();
		topImage.setBackground(new Color(35, 35, 40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45, 45, 45)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);

		topPanel.add(topImage);
		frame.getContentPane().add(topPanel);

		topImage.addMouseListener(new MouseAdapter() {

			int screenIndex = 0;

			@Override
			public void mouseClicked(MouseEvent down) {

				if (down.getClickCount() == 2) {
					UIController.toggleFullscreen();
				}
			}

			@Override
			public void mousePressed(MouseEvent down) {

				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;

				frame.toFront();
			}

			@Override
			public void mouseReleased(MouseEvent down) {

				if (System.getProperty("os.name").contains("Windows")) {
					GraphicsConfiguration config = frame.getGraphicsConfiguration();
					GraphicsDevice myScreen = config.getDevice();
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] allScreens = env.getScreenDevices();
					int newScreenIndex = -1;
					for (int i = 0; i < allScreens.length; i++) {
						if (allScreens[i].equals(myScreen)) {
							newScreenIndex = i;
							break;
						}
					}

					if (screenIndex != newScreenIndex) {
						screenIndex = newScreenIndex;

						/*
						 * if (comboFonctions.getSelectedItem().toString().isEmpty() == false) {
						 * changeFunction(false); }
						 */

						Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
						Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
						shape1.add(shape2);
						frame.setShape(shape1);

						if (FFMPEG.isRunning) {
							VideoPlayerUI.setPlayerButtons(false);
						}
					}
				}
			}
		});

		topImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX,
						MouseInfo.getPointerInfo().getLocation().y - MousePositionY);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}

		});

		UIController.setTitleLocation(false);
		UIController.setButtonsLocation();
	}
}
