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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.functions.settings.Timecode;
import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFprobe;
import shutterencoder.library.Mediainfo;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.renderers.ComboRendererOverlay;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerMultiCuts;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Overlay extends Main {

	public static void grpOverlay() {

		grpOverlay = new CollapsiblePanel(language.getProperty("caseAddOverlay"), true);
		grpOverlay.setLayout(null);
		grpOverlay.setBackground(Utils.c30);
		grpOverlay.setSize(312, 17);
		grpOverlay.setLocation(frame.getWidth(), grpCrop.getY() + grpCrop.getHeight() + 6);
		grpOverlay.setVisible(false);
		grpOverlay.setOpaque(false);
		frame.getContentPane().add(grpOverlay);

		grpOverlay.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpOverlay, 278);
			}

		});

		JLabel lblFont = new JLabel(Main.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setForeground(Utils.themeColor);
		lblFont.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblFont.setBounds(12, 24, lblFont.getPreferredSize().width + 4, 16);
		grpOverlay.add(lblFont);

		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		comboOverlayFont = new JComboBox<String>(Fonts);
		comboOverlayFont.setName("comboOverlayFont");
		comboOverlayFont.setSelectedItem("Arial");
		comboOverlayFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboOverlayFont.setRenderer(new ComboRendererOverlay(comboOverlayFont));
		comboOverlayFont.setEditable(true);
		comboOverlayFont.setLocation(lblFont.getX() + lblFont.getWidth() + 3, lblFont.getY() - 4);
		comboOverlayFont.setSize(grpOverlay.getWidth() - comboOverlayFont.getX() - 10, 22);
		grpOverlay.add(comboOverlayFont);

		comboOverlayFont.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			String text = "";

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void keyReleased(KeyEvent e) {

				if (comboOverlayFont.getEditor().toString().length() <= 1)
					text = String.valueOf(e.getKeyChar());

				if (Character.isLetterOrDigit(e.getKeyChar())) {
					comboOverlayFont.setModel(new DefaultComboBoxModel(Fonts));
					text += e.getKeyChar();

					ArrayList<String> newList = new ArrayList<String>();
					for (int i = 0; i < comboOverlayFont.getItemCount(); i++) {
						if (Fonts[i].toString().length() >= text.length()) {
							if (Fonts[i].toString().toLowerCase().substring(0, text.length()).contains(text)
									&& Fonts[i].toString().contains(":") == false) {
								newList.add(Fonts[i].toString());
							}
						}
					}

					// Pour éviter d'afficher le premier item
					comboOverlayFont.getEditor().setItem(text);

					if (newList.isEmpty() == false) {
						comboOverlayFont.setModel(new DefaultComboBoxModel(newList.toArray()));
						comboOverlayFont.showPopup();
					}

				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					comboOverlayFont.setModel(new DefaultComboBoxModel(Fonts));
					comboOverlayFont.getEditor().setItem("");
					comboOverlayFont.hidePopup();
					text = "";
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					e.consume();// Contournement pour éviter le listeDrop
				else
					comboOverlayFont.hidePopup();
			}

		});

		comboOverlayFont.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				comboOverlayFont.setFont(new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN, 11));
				timecode.repaint();
				fileName.repaint();
			}

		});

		JLabel lblColor = new JLabel(Main.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setForeground(Utils.themeColor);
		lblColor.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblColor.setBounds(lblFont.getX(), lblFont.getY() + lblFont.getHeight() + 11,
				lblColor.getPreferredSize().width + 4, 16);
		grpOverlay.add(lblColor);

		panelTcColor = new JPanel();
		panelTcColor.setName("panelTcColor");
		panelTcColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelTcColor.setBackground(foregroundColor);
		panelTcColor.setBounds(lblColor.getLocation().x + lblColor.getWidth(), lblColor.getY() - 4, 41, 22);
		grpOverlay.add(panelTcColor);

		panelTcColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				foregroundColor = JColorChooser.showDialog(frame, Main.language.getProperty("chooseColor"),
						Color.WHITE);

				if (foregroundColor != null) {
					panelTcColor.setBackground(foregroundColor);
					timecode.repaint();
					fileName.repaint();
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
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});

		lblTcBackground = new JLabel(Main.language.getProperty("lblBackgroundOn"));
		lblTcBackground.setName("lblTcBackground");
		lblTcBackground.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblTcBackground.setBackground(Utils.c42);
		lblTcBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblTcBackground.setOpaque(true);
		lblTcBackground.setFont(new Font(Main.boldFont, Font.PLAIN, 11));
		lblTcBackground.setBounds(panelTcColor.getLocation().x + panelTcColor.getWidth() + 7,
				lblColor.getLocation().y - 1, 80, 16);
		grpOverlay.add(lblTcBackground);

		lblTcBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (lblTcBackground.getText().equals(Main.language.getProperty("lblBackgroundOn"))) {
					lblTcBackground.setText(language.getProperty("aucun"));
					textTcOpacity.setText("100");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					textNameOpacity.setText("100");
				} else {
					lblTcBackground.setText(Main.language.getProperty("lblBackgroundOn"));
					textTcOpacity.setText("50");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					textNameOpacity.setText("50");
				}

				timecode.repaint();
				fileName.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		JLabel lblColor2 = new JLabel(Main.language.getProperty("lblColor"));
		lblColor2.setAlignmentX(SwingConstants.RIGHT);
		lblColor2.setForeground(Utils.themeColor);
		lblColor2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblColor2.setBounds(lblTcBackground.getLocation().x + lblTcBackground.getWidth() + 7, lblColor.getY(),
				lblColor2.getPreferredSize().width + 4, 16);
		grpOverlay.add(lblColor2);

		panelTcColor2 = new JPanel();
		panelTcColor2.setName("panelTcColor2");
		panelTcColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelTcColor2.setBackground(backgroundColor);
		panelTcColor2.setBounds(lblColor2.getLocation().x + lblColor2.getWidth(), panelTcColor.getY(), 41, 22);
		grpOverlay.add(panelTcColor2);

		panelTcColor2.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				Main.backgroundColor = JColorChooser.showDialog(frame, Main.language.getProperty("chooseColor"),
						Color.WHITE);

				if (Main.backgroundColor != null) {
					panelTcColor2.setBackground(backgroundColor);
					timecode.repaint();
					fileName.repaint();
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
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});

		caseAddTimecode = new JCheckBox(Main.language.getProperty("caseAddTimecode"));
		caseAddTimecode.setName("caseAddTimecode");
		caseAddTimecode.setSelected(false);
		caseAddTimecode.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAddTimecode.setSize(caseAddTimecode.getPreferredSize().width, 23);
		caseAddTimecode.setLocation(8, lblColor.getY() + lblColor.getHeight() + 9);
		grpOverlay.add(caseAddTimecode);

		timecode = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {

				if ((caseAddTimecode.isSelected() || caseShowTimecode.isSelected()) && list.getSize() > 0 && VideoPlayerCore.videoPath != null)
				{
					super.paintComponent(g);

					Graphics2D g2 = (Graphics2D) g;

					// First initialisation
					boolean resetLocation = false;
					if (getWidth() == 1) {
						resetLocation = true;
					}

					// Saving height for spinnerSize
					int width = getWidth();
					int height = getHeight();

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN,
							(int) Math.round((float) Integer.parseInt(textTcSize.getText()) / playerRatio));
					font.deriveFont((float) Integer.parseInt(textTcSize.getText()) / playerRatio);
					g2.setFont(font);
  		
		    		String dropFrame = ":";
					if (Timecode.isDropFrame())
					{
						dropFrame = ";";
					}

					if (Main.caseConform.isSelected())
					{
						if (Main.comboFPS.getSelectedItem().toString().equals("29,97") || Main.comboFPS.getSelectedItem().toString().equals("59,94"))
							dropFrame = ";";
						else
							dropFrame = ":";
					}

					String str = "00:00:00" + dropFrame + "00";

					if (caseAddTimecode.isSelected() || caseShowTimecode.isSelected())
					{
						double tcH = 0;
						double tcM = 0;
						double tcS = 0;
						double tcF = 0;

						if (caseAddTimecode.isSelected()
						&& TC1.getText().isEmpty() == false
						&& TC2.getText().isEmpty() == false && TC3.getText().isEmpty() == false
						&& TC4.getText().isEmpty() == false)
						{
							tcH = Integer.valueOf(TC1.getText());
							tcM = Integer.valueOf(TC2.getText());
							tcS = Integer.valueOf(TC3.getText());
							tcF = Integer.valueOf(TC4.getText());
						}
						else if (caseShowTimecode.isSelected() && FFprobe.timecode1 == "" == false)
						{
							tcH = Integer.valueOf(FFprobe.timecode1);
							tcM = Integer.valueOf(FFprobe.timecode2);
							tcS = Integer.valueOf(FFprobe.timecode3);
							tcF = Integer.valueOf(FFprobe.timecode4);			
						}

						tcH = (double) (tcH * 3600 * FFprobe.accurateFPS);
						tcM = (double) (tcM * 60 * FFprobe.accurateFPS);
						tcS = (double) (tcS * FFprobe.accurateFPS);

						double timeIn = (Integer.parseInt(VideoPlayerUI.caseInH.getText()) * 3600
								+ Integer.parseInt(VideoPlayerUI.caseInM.getText()) * 60
								+ Integer.parseInt(VideoPlayerUI.caseInS.getText())) * VideoPlayerUtils.getFPS()
								+ Integer.parseInt(VideoPlayerUI.caseInF.getText());

						if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
						{
							VideoPlayerMultiCuts.CutSegment seg = VideoPlayerMultiCuts.cutSegments.get(0);
							timeIn = (seg.inH * 3600 + seg.inM * 60 + seg.inS) * FFprobe.currentFPS + seg.inF;
						}
						
						if (caseShowTimecode.isSelected())
							timeIn = 0;
						
						double currentTime = VideoPlayerCore.bufferedFrames.size() > 0 ? VideoPlayerCore.bufferCurrentFrame : VideoPlayerCore.playerCurrentFrame;
						
						//NTSC framerate
						currentTime = Timecode.setNTSCtimecode(currentTime);
						
						double offset = (currentTime - timeIn) + tcH + tcM + tcS + tcF;

						if (offset < 0)
							offset = 0;

						double fps = VideoPlayerUtils.getFPS();
						if (Timecode.isDropFrame())
						{
							offset = Timecode.setDropFrameTimecode(offset);
											
							if (FFprobe.currentFPS == 29.97f)
							{
								fps = 30;
							}
							else if (FFprobe.currentFPS == 59.94f)
							{
								fps = 60;
							}
							
							dropFrame = ";";
						}
						
						String h = formatter.format(Math.floor(offset / fps / 3600));
						String m = formatter.format(Math.floor(offset / fps / 60) % 60);
						String s = formatter.format(Math.floor(offset / fps) % 60);
						String f = formatter.format(Math.floor(offset % fps));

						if (caseAddTimecode.isSelected() && lblTimecode.getText().equals(Main.language.getProperty("lblFrameNumber")))
						{
							str = String.format("%.0f", offset);
						}
						else
							str = h + ":" + m + ":" + s + dropFrame + f;
					}

					Rectangle bounds = getStringBounds(g2, str, 0, 0);

					if (lblTcBackground.getText().equals(Main.language.getProperty("lblBackgroundOn")))
						g2.setColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(),
								backgroundColor.getBlue(),
								(int) ((float) (Integer.parseInt(textTcOpacity.getText()) * 255) / 100)));
					else
						g2.setColor(new Color(0, 0, 0, 0));

					GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration();
					AffineTransform transform = gfxConfig.getDefaultTransform();

					// Checking screen DPI
					GraphicsConfiguration config = frame.getGraphicsConfiguration();
					GraphicsDevice myScreen = config.getDevice();
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] allScreens = env.getScreenDevices();
					int screenIndex = -1;
					for (int i = 0; i < allScreens.length; i++) {
						if (allScreens[i].equals(myScreen)) {
							screenIndex = i;
							break;
						}
					}

					float dpiScaleFactor = 1.0f;
					if (System.getProperty("os.name").contains("Windows")) {
						double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds()
								.getHeight();
						double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
						dpiScaleFactor = (float) (trueHorizontalLines / scaledHorizontalLines);
					}

					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false =
																											// RetinaScreen
					{
						g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					} else if (System.getProperty("os.name").contains("Windows")) {
						g2.fillRect(0, 0, (int) Math.round(bounds.width * dpiScaleFactor),
								(int) Math.round(bounds.height * dpiScaleFactor));
					} else
						g2.fillRect(0, 0, bounds.width, bounds.height);

					if (lblTcBackground.getText().equals(language.getProperty("aucun"))) {
						g2.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(),
								foregroundColor.getBlue(),
								(int) ((float) (Integer.parseInt(textTcOpacity.getText()) * 255) / 100)));
					} else
						g2.setColor(foregroundColor);

					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false =
																											// RetinaScreen
					{
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2, bounds.height / 2 - offset / 2);
					} else if (System.getProperty("os.name").contains("Windows")) {
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2 * dpiScaleFactor, (bounds.height - offset) * dpiScaleFactor);
					} else {
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2, bounds.height - offset);
					}

					setSize(bounds.width, bounds.height);

					// Define center position after the size is correct
					if (resetLocation) {
						timecode.setLocation(VideoPlayerUI.player.getWidth() / 2 - timecode.getWidth() / 2,
								timecode.getHeight());
						tcLocX = timecode.getLocation().x;
						tcLocY = timecode.getLocation().y;
					} else if (textTcSize.hasFocus()) {
						timecode.setLocation(timecode.getX() + (width - timecode.getWidth()) / 2,
								timecode.getY() + (height - timecode.getHeight()) / 2);
						tcLocX = timecode.getLocation().x;
						tcLocY = timecode.getLocation().y;
					}

					VideoPlayerOverlay.refreshTimecodeAndText();
				}
			}

			private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y) {
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
				return gv.getPixelBounds(null, x, y);
			}
		};

		timecode.setSize(1, 1);
		timecode.setLayout(null);
		timecode.setOpaque(false);
		timecode.setBackground(new Color(0, 0, 0, 0));

		timecode.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2) {
					timecode.setLocation(VideoPlayerUI.player.getWidth() / 2 - timecode.getWidth() / 2,
							timecode.getHeight());

					VideoPlayerOverlay.refreshTimecodeAndText();

					// Saving the location
					tcLocX = timecode.getLocation().x;
					tcLocY = timecode.getLocation().y;
				}

				tcPosX = e.getLocationOnScreen().x;
				tcPosY = e.getLocationOnScreen().y;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tcLocX = timecode.getLocation().x;
				tcLocY = timecode.getLocation().y;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		timecode.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				timecode.setLocation(MouseInfo.getPointerInfo().getLocation().x - tcPosX + tcLocX,
						MouseInfo.getPointerInfo().getLocation().y - tcPosY + tcLocY);
				textTcPosX.setText(String.valueOf((int) Math.round(timecode.getLocation().x * playerRatio)));
				textTcPosY.setText(String.valueOf((int) Math.round(timecode.getLocation().y * playerRatio)));
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});

		lblTimecode = new JLabel(Main.language.getProperty("lblTimecode"));
		lblTimecode.setName("lblTimecode");
		lblTimecode.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblTimecode.setBackground(Utils.c42);
		lblTimecode.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimecode.setOpaque(true);
		lblTimecode.setFont(new Font(Main.boldFont, Font.PLAIN, 11));
		lblTimecode.setBounds(caseAddTimecode.getLocation().x + caseAddTimecode.getWidth() + 2,
				caseAddTimecode.getLocation().y + 3, 70, 16);
		grpOverlay.add(lblTimecode);

		lblTimecode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (lblTimecode.getText().equals(Main.language.getProperty("lblTimecode")))
					lblTimecode.setText(Main.language.getProperty("lblFrameNumber"));
				else
					lblTimecode.setText(Main.language.getProperty("lblTimecode"));

				if (VideoPlayerCore.frameVideo != null) {
					VideoPlayerUI.player.repaint();
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

		TC1 = new JTextField("00");
		TC1.setName("TC1");
		TC1.setEnabled(false);
		TC1.setText("00");
		TC1.setHorizontalAlignment(SwingConstants.CENTER);
		TC1.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		TC1.setColumns(10);
		TC1.setBounds(lblTimecode.getX() + lblTimecode.getWidth() + 7, caseAddTimecode.getY(), 32, 21);
		grpOverlay.add(TC1);

		TC2 = new JTextField("00");
		TC2.setName("TC2");
		TC2.setEnabled(false);
		TC2.setText("00");
		TC2.setHorizontalAlignment(SwingConstants.CENTER);
		TC2.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		TC2.setColumns(10);
		TC2.setBounds(TC1.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC2);

		TC3 = new JTextField("00");
		TC3.setName("TC3");
		TC3.setEnabled(false);
		TC3.setText("00");
		TC3.setHorizontalAlignment(SwingConstants.CENTER);
		TC3.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		TC3.setColumns(10);
		TC3.setBounds(TC2.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC3);

		TC4 = new JTextField("00");
		TC4.setName("TC4");
		TC4.setEnabled(false);
		TC4.setText("00");
		TC4.setHorizontalAlignment(SwingConstants.CENTER);
		TC4.setFont(new Font(Main.mainFont, Font.PLAIN, 13));
		TC4.setColumns(10);
		TC4.setBounds(TC3.getX() + 34, TC1.getY(), 32, 21);
		grpOverlay.add(TC4);

		TC1.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC1.getText().length() >= 2)
					TC1.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				timecode.repaint();
			}
		});

		TC2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC2.getText().length() >= 2)
					TC2.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				timecode.repaint();
			}
		});

		TC3.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC3.getText().length() >= 2)
					TC3.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				timecode.repaint();
			}
		});

		TC4.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (TC4.getText().length() >= 2)
					TC4.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				timecode.repaint();
			}
		});

		caseShowTimecode = new JCheckBox(Main.language.getProperty("caseTcInterne"));
		caseShowTimecode.setName("caseShowTimecode");
		caseShowTimecode.setSelected(false);
		caseShowTimecode.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseShowTimecode.setEnabled(true);
		caseShowTimecode.setSize(caseShowTimecode.getPreferredSize().width, 23);
		caseShowTimecode.setLocation(caseAddTimecode.getX(), caseAddTimecode.getY() + caseAddTimecode.getHeight());
		grpOverlay.add(caseShowTimecode);

		JLabel posX = new JLabel(Main.language.getProperty("posX"));
		posX.setHorizontalAlignment(SwingConstants.LEFT);
		posX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		posX.setForeground(Utils.themeColor);
		posX.setAlignmentX(SwingConstants.RIGHT);
		posX.setEnabled(false);
		posX.setBounds(24, caseShowTimecode.getY() + caseShowTimecode.getHeight() + 6, posX.getPreferredSize().width,
				16);
		grpOverlay.add(posX);

		textTcPosX = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().x * playerRatio))));
		textTcPosX.setName("textTcPosX");
		textTcPosX.setBounds(posX.getLocation().x + posX.getWidth() + 2, posX.getLocation().y, 34, 16);
		textTcPosX.setEnabled(false);
		textTcPosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		grpOverlay.add(textTcPosX);

		textTcPosX.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textTcPosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / playerRatio),
							timecode.getLocation().y);

				tcLocX = timecode.getLocation().x;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textTcPosX.getText().length() >= 4)
					textTcPosX.setText("");
			}

		});

		textTcPosX.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcPosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcPosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tcLocX = timecode.getLocation().x;
			}

		});

		textTcPosX.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textTcPosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textTcPosX.setText(String.valueOf(MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX)));
					timecode.setLocation((int) Math.round(Integer.valueOf(textTcPosX.getText()) / playerRatio),
							timecode.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel px1 = new JLabel("px");
		px1.setEnabled(false);
		px1.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px1.setForeground(Utils.themeColor);
		px1.setBounds(textTcPosX.getLocation().x + textTcPosX.getWidth() + 2, posX.getY(), px1.getPreferredSize().width,
				16);
		grpOverlay.add(px1);

		JLabel posY = new JLabel(Main.language.getProperty("posY"));
		posY.setEnabled(false);
		posY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		posY.setForeground(Utils.themeColor);
		posY.setAlignmentX(SwingConstants.RIGHT);
		posY.setBounds(px1.getX() + px1.getWidth() + 30, posX.getLocation().y, posY.getPreferredSize().width, 16);
		grpOverlay.add(posY);

		textTcPosY = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.round(timecode.getLocation().y * playerRatio))));
		textTcPosY.setName("textTcPosY");
		textTcPosY.setEnabled(false);
		textTcPosY.setBounds(posY.getLocation().x + posY.getWidth() + 2, posY.getLocation().y, 34, 16);
		textTcPosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcPosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		grpOverlay.add(textTcPosY);

		textTcPosY.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textTcPosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					timecode.setLocation(timecode.getLocation().x,
							(int) Math.round(Integer.valueOf(textTcPosY.getText()) / playerRatio));

				tcLocY = timecode.getLocation().y;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textTcPosY.getText().length() >= 4)
					textTcPosY.setText("");
			}

		});

		textTcPosY.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcPosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseY = e.getY();
				MouseTcPosition.offsetY = Integer.parseInt(textTcPosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				tcLocY = timecode.getLocation().y;
			}

		});

		textTcPosY.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (textTcPosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textTcPosY.setText(String.valueOf(MouseTcPosition.offsetY + (e.getY() - MouseTcPosition.mouseY)));
					timecode.setLocation(timecode.getLocation().x,
							(int) Math.round(Integer.valueOf(textTcPosY.getText()) / playerRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel px2 = new JLabel("px");
		px2.setEnabled(false);
		px2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px2.setForeground(Utils.themeColor);
		px2.setBounds(textTcPosY.getLocation().x + textTcPosY.getWidth() + 2, posX.getY(), px1.getPreferredSize().width,
				16);
		grpOverlay.add(px2);

		JLabel lblSizeTC = new JLabel(Main.language.getProperty("lblSize"));
		lblSizeTC.setEnabled(false);
		lblSizeTC.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblSizeTC.setAlignmentX(SwingConstants.RIGHT);
		lblSizeTC.setForeground(Utils.themeColor);
		lblSizeTC.setBounds(textTcPosX.getX() - lblSizeTC.getPreferredSize().width - 2,
				posX.getY() + posX.getHeight() + 6, lblSizeTC.getPreferredSize().width + 2, 16);
		grpOverlay.add(lblSizeTC);

		textTcSize = new JTextField(String.valueOf(Math.round((float) 27 * playerRatio)));
		textTcSize.setEnabled(false);
		textTcSize.setName("textTcSize");
		textTcSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcSize.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textTcSize.setBounds(textTcPosX.getLocation().x, lblSizeTC.getLocation().y, 34, 16);
		grpOverlay.add(textTcSize);

		textTcSize.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (textTcSize.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
					frame.requestFocus();
			}

		});

		textTcSize.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

				textTcSize.requestFocus();
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textTcSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (Integer.parseInt(textTcSize.getText()) < 5) {
						textTcSize.setText("5");
					}

					timecode.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textTcSize.getText().length() >= 3)
					textTcSize.setText("");
			}

		});

		textTcSize.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textTcSize.getText().length() > 0) {
					int value = MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX);

					if (value < 5) {
						textTcSize.setText("5");
					} else
						textTcSize.setText(String.valueOf(value));

					timecode.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel percent1 = new JLabel("%");
		percent1.setEnabled(false);
		percent1.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent1.setForeground(Utils.themeColor);
		percent1.setBounds(textTcSize.getLocation().x + textTcSize.getWidth() + 2, lblSizeTC.getY(),
				percent1.getPreferredSize().width + 4, 16);
		grpOverlay.add(percent1);

		JLabel lblOpacityTC = new JLabel(Main.language.getProperty("lblOpacity"));
		lblOpacityTC.setEnabled(false);
		lblOpacityTC.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblOpacityTC.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityTC.setForeground(Utils.themeColor);
		lblOpacityTC.setBounds(textTcPosY.getX() - lblOpacityTC.getPreferredSize().width - 2, lblSizeTC.getLocation().y,
				lblOpacityTC.getPreferredSize().width, 16);
		grpOverlay.add(lblOpacityTC);

		textTcOpacity = new JTextField("50");
		textTcOpacity.setName("textTcOpacity");
		textTcOpacity.setEnabled(false);
		textTcOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textTcOpacity.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textTcOpacity.setBounds(textTcPosY.getLocation().x, lblSizeTC.getLocation().y, textTcSize.getWidth(), 16);
		grpOverlay.add(textTcOpacity);

		textTcOpacity.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textTcOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseTcPosition.mouseX = e.getX();
				MouseTcPosition.offsetX = Integer.parseInt(textTcOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textTcOpacity.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textTcOpacity.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
						&& textTcOpacity.getText().length() > 0) {
					if (Integer.valueOf(textTcOpacity.getText()) > 100) {
						textTcOpacity.setText("100");
					} else if (Integer.valueOf(textTcOpacity.getText()) < 1) {
						textTcOpacity.setText("1");
					}

					timecode.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textTcOpacity.getText().length() >= 3)
					textTcOpacity.setText("");
			}

		});

		textTcOpacity.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textTcOpacity.getText().length() > 0) {
					int value = MouseTcPosition.offsetX + (e.getX() - MouseTcPosition.mouseX);

					if (value > 100) {
						textTcOpacity.setText("100");
					} else if (value < 1) {
						textTcOpacity.setText("1");
					} else
						textTcOpacity.setText(String.valueOf(value));

					timecode.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel percent2 = new JLabel("%");
		percent2.setEnabled(false);
		percent2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent2.setForeground(Utils.themeColor);
		percent2.setBounds(textTcOpacity.getLocation().x + textTcOpacity.getWidth() + 2, lblOpacityTC.getY(),
				percent1.getPreferredSize().width + 4, 16);
		grpOverlay.add(percent2);

		caseAddText = new JCheckBox(Main.language.getProperty("caseShowText"));
		caseAddText.setName("caseAddText");
		caseAddText.setSelected(false);
		caseAddText.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAddText.setSize(caseAddText.getPreferredSize().width + 4, 23);
		caseAddText.setLocation(caseAddTimecode.getX(), lblSizeTC.getY() + lblSizeTC.getHeight() + 8);
		grpOverlay.add(caseAddText);

		overlayText = new JTextField("");
		overlayText.setName("overlayText");
		overlayText.setEnabled(false);
		overlayText.setLocation(caseAddText.getLocation().x + caseAddText.getWidth() + 7,
				caseAddText.getLocation().y + 1);
		overlayText.setSize(grpOverlay.getWidth() - overlayText.getX() - 10, 21);
		overlayText.setHorizontalAlignment(SwingConstants.LEFT);
		overlayText.setFont(new Font("SansSerif", Font.PLAIN, 12));
		overlayText.setToolTipText(
				"<html>{codec/function}<br>{preset}<br>{resolution/scale}<br>{width}<br>{height}<br>{ratio/aspect}<br>{framerate/fps}<br>{bitrate}<br>{timecode}<br>{duration/time}<br>{date}</html>");
		grpOverlay.add(overlayText);

		overlayText.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

				textTime = System.currentTimeMillis();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {

				if (e.getKeyChar() == ':' || e.getKeyChar() == '\"' || e.getKeyChar() == '\'' || e.getKeyChar() == ';'
						|| e.getKeyChar() == ',') {
					e.consume();
				} else {
					if (changeText == null || changeText.isAlive() == false) {
						changeText = new Thread(new Runnable() {

							@Override
							public void run() {

								do {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {
									}
								} while (System.currentTimeMillis() - textTime < 500);

								VideoPlayerUI.player.add(fileName);

								// Overimage need to be the last component added
								if (caseEnableCrop.isSelected()) {
									VideoPlayerUI.player.remove(selection);
									VideoPlayerUI.player.remove(overImage);
									VideoPlayerUI.player.add(selection);
									VideoPlayerUI.player.add(overImage);
								}

								fileName.repaint();
							}
						});
						changeText.start();
					}
				}
			}

		});

		caseShowFileName = new JCheckBox(Main.language.getProperty("caseShowFileName"));
		caseShowFileName.setName("caseShowFileName");
		caseShowFileName.setSelected(false);
		caseShowFileName.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseShowFileName.setSize(caseShowFileName.getPreferredSize().width, 23);
		caseShowFileName.setLocation(caseAddText.getX(), caseAddText.getY() + caseAddText.getHeight());
		grpOverlay.add(caseShowFileName);

		fileName = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				if ((caseShowFileName.isSelected() || caseAddText.isSelected() && overlayText.getText().length() > 0)
						&& list.getSize() > 0 && VideoPlayerCore.videoPath != null) {
					super.paintComponent(g);

					Graphics2D g2 = (Graphics2D) g;

					// First initialisation
					boolean resetLocation = false;
					if (getWidth() == 1) {
						resetLocation = true;
					}

					// Saving height for spinnerSize
					int width = getWidth();
					int height = getHeight();

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					Font font = new Font(comboOverlayFont.getSelectedItem().toString(), Font.PLAIN,
							(int) Math.round((float) Integer.parseInt(textNameSize.getText()) / playerRatio));
					font.deriveFont((float) Integer.parseInt(textNameSize.getText()) / playerRatio);
					g2.setFont(font);

					String file = VideoPlayerCore.videoPath;
					if (Main.scanIsRunning) {
						File dir = new File(Main.list.firstElement());
						for (File f : dir.listFiles()) {
							if (f.isHidden() == false && f.isFile()) {
								file = f.toString();
								break;
							}
						}
					}

					String ext = file.substring(file.lastIndexOf("."));
					String str = new File(file).getName().replace(ext, "");

					if (caseAddText.isSelected()) {
						str = FunctionUtils.setPrefixSuffix(overlayText.getText(), true);
					}

					Rectangle bounds = getStringBounds(g2, str, 0, 0);

					if (lblTcBackground.getText().equals(Main.language.getProperty("lblBackgroundOn")))
						g2.setColor(new Color(backgroundColor.getRed(), backgroundColor.getGreen(),
								backgroundColor.getBlue(),
								(int) ((float) (Integer.parseInt(textNameOpacity.getText()) * 255) / 100)));
					else
						g2.setColor(new Color(0, 0, 0, 0));

					GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().getDefaultConfiguration();
					AffineTransform transform = gfxConfig.getDefaultTransform();

					// Checking screen DPI
					GraphicsConfiguration config = frame.getGraphicsConfiguration();
					GraphicsDevice myScreen = config.getDevice();
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] allScreens = env.getScreenDevices();
					int screenIndex = -1;
					for (int i = 0; i < allScreens.length; i++) {
						if (allScreens[i].equals(myScreen)) {
							screenIndex = i;
							break;
						}
					}

					float dpiScaleFactor = 1.0f;
					if (System.getProperty("os.name").contains("Windows")) {
						double trueHorizontalLines = allScreens[screenIndex].getDefaultConfiguration().getBounds()
								.getHeight();
						double scaledHorizontalLines = allScreens[screenIndex].getDisplayMode().getHeight();
						dpiScaleFactor = (float) (trueHorizontalLines / scaledHorizontalLines);
					}

					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false =
																											// RetinaScreen
					{
						g2.fillRect(0, 0, bounds.width, bounds.height / 2);
					} else if (System.getProperty("os.name").contains("Windows")) {
						g2.fillRect(0, 0, (int) Math.round(bounds.width * dpiScaleFactor),
								(int) Math.round(bounds.height * dpiScaleFactor));
					} else
						g2.fillRect(0, 0, bounds.width, bounds.height);

					if (lblTcBackground.getText().equals(language.getProperty("aucun")))
						g2.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(),
								foregroundColor.getBlue(),
								(int) ((float) (Integer.parseInt(textNameOpacity.getText()) * 255) / 100)));
					else
						g2.setColor(foregroundColor);

					if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false =
																											// RetinaScreen
					{
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2, bounds.height / 2 - offset / 2);
					} else if (System.getProperty("os.name").contains("Windows")) {
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2 * dpiScaleFactor, (bounds.height - offset) * dpiScaleFactor);
					} else {
						Integer offset = bounds.height + (int) bounds.getY();
						g2.drawString(str, -2, bounds.height - offset);
					}

					fileName.setSize(bounds.width, bounds.height);

					// Define center position after the size is correct
					if (resetLocation) {
						fileName.setLocation(VideoPlayerUI.player.getWidth() / 2 - fileName.getWidth() / 2,
								VideoPlayerUI.player.getHeight() - fileName.getHeight() - fileName.getHeight() / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					} else if (textNameSize.hasFocus()) {
						fileName.setLocation(fileName.getX() + (width - fileName.getWidth()) / 2,
								fileName.getY() + (height - fileName.getHeight()) / 2);
						fileLocX = fileName.getLocation().x;
						fileLocY = fileName.getLocation().y;
					}

					VideoPlayerOverlay.refreshTimecodeAndText();
				}
			}

			private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y) {
				FontRenderContext frc = g2.getFontRenderContext();
				GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
				return gv.getPixelBounds(null, x, y);
			}
		};

		fileName.setSize(1, 1);
		fileName.setLayout(null);
		fileName.setOpaque(false);
		fileName.setBackground(new Color(0, 0, 0, 0));

		fileName.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2) {
					fileName.setLocation(VideoPlayerUI.player.getWidth() / 2 - fileName.getWidth() / 2,
							VideoPlayerUI.player.getHeight() - fileName.getHeight() - fileName.getHeight() / 2);

					VideoPlayerOverlay.refreshTimecodeAndText();

					// Saving the location
					fileLocX = fileName.getLocation().x;
					fileLocY = fileName.getLocation().y;
				}

				filePosX = e.getLocationOnScreen().x;
				filePosY = e.getLocationOnScreen().y;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				fileLocX = fileName.getLocation().x;
				fileLocY = fileName.getLocation().y;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		fileName.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				fileName.setLocation(MouseInfo.getPointerInfo().getLocation().x - filePosX + fileLocX,
						MouseInfo.getPointerInfo().getLocation().y - filePosY + fileLocY);
				textNamePosX.setText(String.valueOf((int) Math.round(fileName.getLocation().x * playerRatio)));
				textNamePosY.setText(String.valueOf((int) Math.round(fileName.getLocation().y * playerRatio)));
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

		});

		JLabel posX2 = new JLabel(Main.language.getProperty("posX"));
		posX2.setEnabled(false);
		posX2.setHorizontalAlignment(SwingConstants.LEFT);
		posX2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		posX2.setForeground(Utils.themeColor);
		posX2.setAlignmentX(SwingConstants.RIGHT);
		posX2.setBounds(posX.getX(), caseShowFileName.getY() + caseShowFileName.getHeight() + 6, posX.getWidth(), 16);
		grpOverlay.add(posX2);

		textNamePosX = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().x * playerRatio))));
		textNamePosX.setName("textNamePosX");
		textNamePosX.setEnabled(false);
		textNamePosX.setBounds(posX2.getLocation().x + posX2.getWidth() + 2, posX2.getLocation().y, 34, 16);
		textNamePosX.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosX.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		grpOverlay.add(textNamePosX);

		textNamePosX.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNamePosX.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / playerRatio),
							fileName.getLocation().y);

				fileLocX = fileName.getLocation().x;
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textNamePosX.getText().length() >= 4)
					textNamePosX.setText("");
			}

		});

		textNamePosX.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNamePosX.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNamePosX.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				fileLocX = fileName.getLocation().x;
			}

		});

		textNamePosX.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textNamePosX.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textNamePosX
							.setText(String.valueOf(MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX)));
					fileName.setLocation((int) Math.round(Integer.valueOf(textNamePosX.getText()) / playerRatio),
							fileName.getLocation().y);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel px3 = new JLabel("px");
		px3.setEnabled(false);
		px3.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px3.setForeground(Utils.themeColor);
		px3.setBounds(textNamePosX.getLocation().x + textNamePosX.getWidth() + 2, posX2.getY(),
				px1.getPreferredSize().width, 16);
		grpOverlay.add(px3);

		JLabel posY2 = new JLabel(Main.language.getProperty("posY"));
		posY2.setEnabled(false);
		posY2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		posY2.setForeground(Utils.themeColor);
		posY2.setAlignmentX(SwingConstants.RIGHT);
		posY2.setBounds(px3.getX() + px3.getWidth() + 30, posX2.getLocation().y, posX.getWidth(), 16);
		grpOverlay.add(posY2);

		textNamePosY = new JTextField(
				String.valueOf(Integer.valueOf((int) Math.round(fileName.getLocation().y * playerRatio))));
		textNamePosY.setEnabled(false);
		textNamePosY.setName("textNamePosY");
		textNamePosY.setBounds(posY2.getLocation().x + posY2.getWidth() + 2, posY2.getLocation().y, 34, 16);
		textNamePosY.setHorizontalAlignment(SwingConstants.RIGHT);
		textNamePosY.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		grpOverlay.add(textNamePosY);

		textNamePosY.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNamePosY.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
					fileName.setLocation(fileName.getLocation().x,
							(int) Math.round(Integer.valueOf(textNamePosY.getText()) / playerRatio));

				fileLocY = fileName.getLocation().y;
			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textNamePosY.getText().length() >= 4)
					textNamePosY.setText("");
			}

		});

		textNamePosY.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNamePosY.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseY = e.getY();
				MouseNamePosition.offsetY = Integer.parseInt(textNamePosY.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				fileLocY = fileName.getLocation().y;
			}

		});

		textNamePosY.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textNamePosY.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					textNamePosY
							.setText(String.valueOf(MouseNamePosition.offsetY + (e.getY() - MouseNamePosition.mouseY)));
					fileName.setLocation(fileName.getLocation().x,
							(int) Math.round(Integer.valueOf(textNamePosY.getText()) / playerRatio));
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel px4 = new JLabel("px");
		px4.setEnabled(false);
		px4.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		px4.setForeground(Utils.themeColor);
		px4.setBounds(textNamePosY.getLocation().x + textNamePosY.getWidth() + 2, posY2.getY(),
				px1.getPreferredSize().width, 16);
		grpOverlay.add(px4);

		JLabel lblSizeName = new JLabel(Main.language.getProperty("lblSize"));
		lblSizeName.setEnabled(false);
		lblSizeName.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblSizeName.setAlignmentX(SwingConstants.RIGHT);
		lblSizeName.setForeground(Utils.themeColor);
		lblSizeName.setBounds(lblSizeTC.getX(), posX2.getY() + posX2.getHeight() + 6, lblSizeTC.getWidth(), 16);
		grpOverlay.add(lblSizeName);

		textNameSize = new JTextField(String.valueOf(Math.round((float) 27 * playerRatio)));
		textNameSize.setEnabled(false);
		textNameSize.setName("textNameSize");
		textNameSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textNameSize.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textNameSize.setBounds(textNamePosX.getLocation().x, lblSizeName.getLocation().y, textNamePosX.getWidth(), 16);
		grpOverlay.add(textNameSize);

		textNameSize.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNameSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNameSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (textNameSize.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
					frame.requestFocus();
			}

		});

		textNameSize.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

				textNameSize.requestFocus();
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNameSize.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (Integer.parseInt(textNameSize.getText()) < 5) {
						textNameSize.setText("5");
					}

					fileName.repaint();
				}

			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textNameSize.getText().length() >= 3)
					textNameSize.setText("");
			}

		});

		textNameSize.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textNameSize.getText().length() > 0) {
					int value = MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX);

					if (value < 5) {
						textNameSize.setText("5");
					} else
						textNameSize.setText(String.valueOf(value));

					fileName.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel percent3 = new JLabel("%");
		percent3.setEnabled(false);
		percent3.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent3.setForeground(Utils.themeColor);
		percent3.setBounds(textNameSize.getLocation().x + textNameSize.getWidth() + 2, lblSizeName.getY(),
				percent1.getPreferredSize().width, 16);
		grpOverlay.add(percent3);

		JLabel lblOpacityName = new JLabel(Main.language.getProperty("lblOpacity"));
		lblOpacityName.setEnabled(false);
		lblOpacityName.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblOpacityName.setAlignmentX(SwingConstants.RIGHT);
		lblOpacityName.setForeground(Utils.themeColor);
		lblOpacityName.setBounds(lblOpacityTC.getX(), lblSizeName.getLocation().y, lblOpacityTC.getWidth(), 16);
		grpOverlay.add(lblOpacityName);

		textNameOpacity = new JTextField("50");
		textNameOpacity.setEnabled(false);
		textNameOpacity.setName("spinnerNameOpacity");
		textNameOpacity.setHorizontalAlignment(SwingConstants.RIGHT);
		textNameOpacity.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textNameOpacity.setBounds(textNamePosY.getX(), lblSizeName.getLocation().y, textNameSize.getWidth(), 16);
		grpOverlay.add(textNameOpacity);

		textNameOpacity.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textNameOpacity.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseNamePosition.mouseX = e.getX();
				MouseNamePosition.offsetX = Integer.parseInt(textNameOpacity.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		textNameOpacity.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textNameOpacity.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
						&& textNameOpacity.getText().length() > 0) {
					if (Integer.valueOf(textNameOpacity.getText()) > 100) {
						textNameOpacity.setText("100");
					} else if (Integer.valueOf(textNameOpacity.getText()) < 1) {
						textNameOpacity.setText("1");
					}

					fileName.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textNameOpacity.getText().length() >= 3)
					textNameOpacity.setText("");
			}

		});

		textNameOpacity.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textNameOpacity.getText().length() > 0) {
					int value = MouseNamePosition.offsetX + (e.getX() - MouseNamePosition.mouseX);

					if (value > 100) {
						textNameOpacity.setText("100");
					} else if (value < 1) {
						textNameOpacity.setText("1");
					} else
						textNameOpacity.setText(String.valueOf(value));

					fileName.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel percent4 = new JLabel("%");
		percent4.setEnabled(false);
		percent4.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		percent4.setForeground(Utils.themeColor);
		percent4.setBounds(textNameOpacity.getLocation().x + textNameOpacity.getWidth() + 2, lblOpacityName.getY(),
				percent1.getPreferredSize().width, 16);
		grpOverlay.add(percent4);

		caseAddTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseAddTimecode.isSelected()) {
					posX.setEnabled(true);
					textTcPosX.setEnabled(true);
					px1.setEnabled(true);
					posY.setEnabled(true);
					textTcPosY.setEnabled(true);
					px2.setEnabled(true);
					lblSizeTC.setEnabled(true);
					textTcSize.setEnabled(true);
					percent1.setEnabled(true);
					lblOpacityTC.setEnabled(true);
					textTcOpacity.setEnabled(true);
					percent2.setEnabled(true);

					timecode.repaint();
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					TC4.setEnabled(true);
					caseShowTimecode.setSelected(false);
					VideoPlayerUI.player.add(timecode);

					// Overimage need to be the last component added
					if (caseEnableCrop.isSelected()) {
						VideoPlayerUI.player.remove(selection);
						VideoPlayerUI.player.remove(overImage);
						VideoPlayerUI.player.add(selection);
						VideoPlayerUI.player.add(overImage);
					}

				} else {
					posX.setEnabled(false);
					textTcPosX.setEnabled(false);
					px1.setEnabled(false);
					posY.setEnabled(false);
					textTcPosY.setEnabled(false);
					px2.setEnabled(false);
					lblSizeTC.setEnabled(false);
					textTcSize.setEnabled(false);
					percent1.setEnabled(false);
					lblOpacityTC.setEnabled(false);
					textTcOpacity.setEnabled(false);
					percent2.setEnabled(false);

					FFprobe.timecode1 = "";
					FFprobe.timecode2 = "";
					FFprobe.timecode3 = "";
					FFprobe.timecode4 = "";
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					TC4.setEnabled(false);
					VideoPlayerUI.player.remove(timecode);
				}

				VideoPlayerOverlay.refreshTimecodeAndText();

				VideoPlayerUI.player.repaint();
			}

		});

		caseShowTimecode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (caseShowTimecode.isSelected()
				&& list.getSize() > 0
				&& VideoPlayerCore.videoPath != null)
				{
					posX.setEnabled(true);
					textTcPosX.setEnabled(true);
					px1.setEnabled(true);
					posY.setEnabled(true);
					textTcPosY.setEnabled(true);
					px2.setEnabled(true);
					lblSizeTC.setEnabled(true);
					textTcSize.setEnabled(true);
					percent1.setEnabled(true);
					lblOpacityTC.setEnabled(true);
					textTcOpacity.setEnabled(true);
					percent2.setEnabled(true);

					// Timecode info
					if (Utils.inputDeviceIsRunning == false) {
						FFprobe.analyzedMedia = null;
						FFprobe.Data(VideoPlayerCore.videoPath);
						try {
							FFprobe.processData.join();
						} catch (InterruptedException er) {
						    Thread.currentThread().interrupt();
						}

						if (FFprobe.timecode1 == "") {
							Mediainfo.run(VideoPlayerCore.videoPath, false);

							do {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e1) {
								}
							} while (Mediainfo.isRunning);
						}

						if (FFprobe.timecode1 == "") {
							caseShowTimecode.setSelected(false);
							caseAddTimecode.doClick();
						} else {
							TC1.setEnabled(false);
							TC2.setEnabled(false);
							TC3.setEnabled(false);
							TC4.setEnabled(false);
							caseAddTimecode.setSelected(false);
							VideoPlayerUI.player.add(timecode);

							// Overimage need to be the last component added
							if (caseEnableCrop.isSelected()) {
								VideoPlayerUI.player.remove(selection);
								VideoPlayerUI.player.remove(overImage);
								VideoPlayerUI.player.add(selection);
								VideoPlayerUI.player.add(overImage);
							}
						}
					}
				} else {
					posX.setEnabled(false);
					textTcPosX.setEnabled(false);
					px1.setEnabled(false);
					posY.setEnabled(false);
					textTcPosY.setEnabled(false);
					px2.setEnabled(false);
					lblSizeTC.setEnabled(false);
					textTcSize.setEnabled(false);
					percent1.setEnabled(false);
					lblOpacityTC.setEnabled(false);
					textTcOpacity.setEnabled(false);
					percent2.setEnabled(false);

					VideoPlayerUI.player.remove(timecode);
				}

				VideoPlayerOverlay.refreshTimecodeAndText();

				VideoPlayerUI.player.repaint();
			}

		});

		caseAddText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAddText.isSelected()) {
					posX2.setEnabled(true);
					textNamePosX.setEnabled(true);
					px3.setEnabled(true);
					posY2.setEnabled(true);
					textNamePosY.setEnabled(true);
					px4.setEnabled(true);
					lblSizeName.setEnabled(true);
					textNameSize.setEnabled(true);
					percent3.setEnabled(true);
					lblOpacityName.setEnabled(true);
					textNameOpacity.setEnabled(true);
					percent4.setEnabled(true);

					caseShowFileName.setSelected(false);
					overlayText.setEnabled(true);

					if (overlayText.getText().length() > 0) {
						VideoPlayerUI.player.add(fileName);

						// Overimage need to be the last component added
						if (caseEnableCrop.isSelected()) {
							VideoPlayerUI.player.remove(selection);
							VideoPlayerUI.player.remove(overImage);
							VideoPlayerUI.player.add(selection);
							VideoPlayerUI.player.add(overImage);
						}
					}
				} else {
					posX2.setEnabled(false);
					textNamePosX.setEnabled(false);
					px3.setEnabled(false);
					posY2.setEnabled(false);
					textNamePosY.setEnabled(false);
					px4.setEnabled(false);
					lblSizeName.setEnabled(false);
					textNameSize.setEnabled(false);
					percent3.setEnabled(false);
					lblOpacityName.setEnabled(false);
					textNameOpacity.setEnabled(false);
					percent4.setEnabled(false);

					overlayText.setEnabled(false);
					VideoPlayerUI.player.remove(fileName);
				}

				VideoPlayerOverlay.refreshTimecodeAndText();

				VideoPlayerUI.player.repaint();

			}
		});

		caseShowFileName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseShowFileName.isSelected()) {
					posX2.setEnabled(true);
					textNamePosX.setEnabled(true);
					px3.setEnabled(true);
					posY2.setEnabled(true);
					textNamePosY.setEnabled(true);
					px4.setEnabled(true);
					lblSizeName.setEnabled(true);
					textNameSize.setEnabled(true);
					percent3.setEnabled(true);
					lblOpacityName.setEnabled(true);
					textNameOpacity.setEnabled(true);
					percent4.setEnabled(true);

					caseAddText.setSelected(false);
					overlayText.setEnabled(false);
					VideoPlayerUI.player.add(fileName);

					// Overimage need to be the last component added
					if (caseEnableCrop.isSelected()) {
						VideoPlayerUI.player.remove(selection);
						VideoPlayerUI.player.remove(overImage);
						VideoPlayerUI.player.add(selection);
						VideoPlayerUI.player.add(overImage);
					}
				} else {
					posX2.setEnabled(false);
					textNamePosX.setEnabled(false);
					px3.setEnabled(false);
					posY2.setEnabled(false);
					textNamePosY.setEnabled(false);
					px4.setEnabled(false);
					lblSizeName.setEnabled(false);
					textNameSize.setEnabled(false);
					percent3.setEnabled(false);
					lblOpacityName.setEnabled(false);
					textNameOpacity.setEnabled(false);
					percent4.setEnabled(false);

					VideoPlayerUI.player.remove(fileName);
				}

				VideoPlayerOverlay.refreshTimecodeAndText();

				VideoPlayerUI.player.repaint();
			}

		});

	}
}
