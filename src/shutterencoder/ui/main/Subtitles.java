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
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
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
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.ui.FlatLineBorder;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.ui.renderers.ComboRenderer;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerOverlay;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

public class Subtitles extends Main {

	public static void grpSubtitles() {

		grpSubtitles = new CollapsiblePanel(language.getProperty("subtitles"), true);
		grpSubtitles.setLayout(null);
		grpSubtitles.setBackground(Utils.c30);
		grpSubtitles.setSize(312, 17);
		grpSubtitles.setLocation(frame.getWidth(), grpOverlay.getY() + grpOverlay.getHeight() + 6);
		grpSubtitles.setVisible(false);
		grpSubtitles.setOpaque(false);
		frame.getContentPane().add(grpSubtitles);

		grpSubtitles.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				UIController.extendSections(grpSubtitles, 131);
			}

		});

		caseAddSubtitles = new JCheckBox(Main.language.getProperty("caseSubtitles") + Main.language.getProperty("colon"));
		caseAddSubtitles.setName("caseAddSubtitles");
		caseAddSubtitles.setBounds(8, 16, caseAddSubtitles.getPreferredSize().width + 5, 23);
		caseAddSubtitles.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		caseAddSubtitles.setSelected(false);
		grpSubtitles.add(caseAddSubtitles);

		caseAddSubtitles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				FunctionUtils.addSubtitles(caseAddSubtitles.isSelected());
				
			}
		});

		if ((System.getProperty("os.name").contains("Mac") && Main.arch.equals("x86_64")) || System.getProperty("os.name").contains("Linux"))
		{
			comboSubsSource = new JComboBox<String>(new String[] { language.getProperty("file") });
		}
		else
			comboSubsSource = new JComboBox<String>(new String[] { language.getProperty("file"), language.getProperty("functionTranscribe") });
		
		comboSubsSource.setName("comboSubsSource");	
		if (System.getProperty("os.name").contains("Mac"))
		{
			comboSubsSource.setLocation(caseAddSubtitles.getX() + caseAddSubtitles.getWidth() - 12, caseAddSubtitles.getY() + 1);
		}
		else
			comboSubsSource.setLocation(caseAddSubtitles.getX() + caseAddSubtitles.getWidth(), caseAddSubtitles.getY() + 1);
		
		if (getLanguage.equals(Locale.of("bg").getDisplayLanguage()))
		{
			comboSubsSource.setSize(comboSubsSource.getPreferredSize().width - 10, 20);
		}
		else
			comboSubsSource.setSize(comboSubsSource.getPreferredSize().width + 8, 20);
		
		comboSubsSource.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		grpSubtitles.add(comboSubsSource);
		
		comboSubsSource.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (caseAddSubtitles.isSelected() && VideoPlayerCore.loadMedia.isAlive() == false) //LoadMedia already load the subs
				{
					FunctionUtils.addSubtitles(false);
					if (VideoPlayerUtils.loadImageThread != null)
					{
						try {
							VideoPlayerUtils.loadImageThread.join();
						} catch (InterruptedException e) {
						    Thread.currentThread().interrupt();
						}
					}
					FunctionUtils.addSubtitles(true);
				}
				
			}
			
		});
		
		JLabel lblFont = new JLabel(Main.language.getProperty("lblFont"));
		lblFont.setAlignmentX(SwingConstants.RIGHT);
		lblFont.setForeground(Utils.themeColor);
		lblFont.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblFont.setBounds(12, caseAddSubtitles.getY() + caseAddSubtitles.getHeight() + 7, lblFont.getPreferredSize().width + 4, 16);
		grpSubtitles.add(lblFont);

		String Fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		comboSubsFont = new JComboBox<String>(Fonts);
		comboSubsFont.setName("comboSubsFont");
		comboSubsFont.setSelectedItem("Arial");
		comboSubsFont.setFont(new Font("Arial", Font.PLAIN, 11));
		comboSubsFont.setRenderer(new ComboRenderer(comboSubsFont));
		comboSubsFont.setEditable(true);
		comboSubsFont.setBounds(lblFont.getX() + lblFont.getWidth() + 3, lblFont.getY() - 4, 110, 22);
		grpSubtitles.add(comboSubsFont);

		comboSubsFont.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			String text = "";

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void keyReleased(KeyEvent e) {

				if (comboSubsFont.getEditor().toString().length() <= 1)
					text = String.valueOf(e.getKeyChar());

				if (Character.isLetterOrDigit(e.getKeyChar())) {
					comboSubsFont.setModel(new DefaultComboBoxModel(Fonts));
					text += e.getKeyChar();

					ArrayList<String> newList = new ArrayList<String>();
					for (int i = 0; i < comboSubsFont.getItemCount(); i++) {
						if (Fonts[i].toString().length() >= text.length()) {
							if (Fonts[i].toString().toLowerCase().substring(0, text.length()).contains(text)
									&& Fonts[i].toString().contains(":") == false) {
								newList.add(Fonts[i].toString());
							}
						}
					}

					// Pour éviter d'afficher le premier item
					comboSubsFont.getEditor().setItem(text);

					if (newList.isEmpty() == false) {
						comboSubsFont.setModel(new DefaultComboBoxModel(newList.toArray()));
						comboSubsFont.showPopup();
					}

				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					comboSubsFont.setModel(new DefaultComboBoxModel(Fonts));
					comboSubsFont.getEditor().setItem("");
					comboSubsFont.hidePopup();
					text = "";
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					e.consume();// Contournement pour éviter le listeDrop
				else
					comboSubsFont.hidePopup();
			}

		});

		comboSubsFont.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				comboSubsFont.setFont(new Font(comboSubsFont.getSelectedItem().toString(), Font.PLAIN, 11));
				
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

		btnG = new JButton(Main.language.getProperty("btnG"));
		btnG.setFont(new Font(Main.boldFont, Font.PLAIN, 13));
		btnG.setName("btnG");
		btnG.setBounds(comboSubsFont.getLocation().x + comboSubsFont.getWidth() + 4, comboSubsFont.getY(), 22, 22);
		grpSubtitles.add(btnG);

		btnG.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (btnG.getForeground() == Color.BLACK)
					btnG.setForeground(Utils.themeColor);
				else
					btnG.setForeground(Color.BLACK);

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

		btnI = new JButton("I");
		btnI.setFont(new Font("Courier New", Font.ITALIC, 13));
		btnI.setName("btnI");
		btnI.setBounds(btnG.getLocation().x + btnG.getWidth() + 2, comboSubsFont.getY(), 22, 22);
		grpSubtitles.add(btnI);

		btnI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (btnI.getForeground() == Color.BLACK)
					btnI.setForeground(Utils.themeColor);
				else
					btnI.setForeground(Color.BLACK);

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

		JLabel lblSubtitlesPosition = new JLabel(Main.language.getProperty("lblSubtitlesPosition"));
		lblSubtitlesPosition.setAlignmentX(SwingConstants.RIGHT);
		lblSubtitlesPosition.setForeground(Utils.themeColor);
		lblSubtitlesPosition.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblSubtitlesPosition.setBounds(btnI.getLocation().x + btnI.getWidth() + 6, comboSubsFont.getY() + 3, lblSubtitlesPosition.getPreferredSize().width + 2, 16);
		grpSubtitles.add(lblSubtitlesPosition);

		textSubtitlesPosition = new JTextField("0");
		textSubtitlesPosition.setName("textSubtitlesPosition");
		textSubtitlesPosition.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textSubtitlesPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubtitlesPosition.setBounds(lblSubtitlesPosition.getLocation().x + lblSubtitlesPosition.getWidth(),
				lblSubtitlesPosition.getLocation().y, 34, 16);
		grpSubtitles.add(textSubtitlesPosition);

		textSubtitlesPosition.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubtitlesPosition.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				MouseSubsPosition.mouseY = e.getY();
				MouseSubsPosition.offsetY = Integer.parseInt(textSubtitlesPosition.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		textSubtitlesPosition.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textSubtitlesPosition.getText().isEmpty() || textSubtitlesPosition.getText().equals("-"))
					return;
					
				VideoPlayerOverlay.refreshSubtitles();
				
				VideoPlayerUtils.restartPlayback();
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		textSubtitlesPosition.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubtitlesPosition.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) && textSubtitlesPosition.getText().length() > 0)
				{
					textSubtitlesPosition.setText(String.valueOf(MouseSubsPosition.offsetY + (e.getY() - MouseSubsPosition.mouseY)));
					VideoPlayerOverlay.refreshSubtitles();
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
		px1.setBounds(textSubtitlesPosition.getLocation().x + textSubtitlesPosition.getWidth() + 2,
				lblSubtitlesPosition.getY(), px1.getPreferredSize().width, 16);
		grpSubtitles.add(px1);

		JLabel lblWidth = new JLabel(Main.language.getProperty("lblWidth"));
		lblWidth.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblWidth.setAlignmentX(SwingConstants.RIGHT);
		lblWidth.setForeground(Utils.themeColor);
		lblWidth.setBounds(lblFont.getX(), lblFont.getY() + lblFont.getHeight() + 11, lblWidth.getPreferredSize().width,
				16);
		grpSubtitles.add(lblWidth);

		textSubsWidth = new JTextField();
		textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
		textSubsWidth.setName("textSubsWidth");
		textSubsWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsWidth.setBounds(lblWidth.getX() + lblWidth.getWidth() + 5, lblWidth.getLocation().y, 34, 16);
		textSubsWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsWidth.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		grpSubtitles.add(textSubsWidth);

		textSubsWidth.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (textSubsWidth.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth) {
						textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
						subsCanvas.setBounds(0, 0, VideoPlayerUI.player.getWidth(),
								(int) (VideoPlayerUI.player.getHeight()
										+ (float) Integer.parseInt(textSubtitlesPosition.getText())
												/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));
					} else {
						subsCanvas.setSize(
								(int) ((float) Integer.parseInt(textSubsWidth.getText())
										/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())),
								(int) (VideoPlayerUI.player.getHeight()
										+ (float) Integer.parseInt(textSubtitlesPosition.getText())
												/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));

						subsCanvas.setLocation((VideoPlayerUI.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
					}
					
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
			}

			@Override
			public void keyTyped(KeyEvent e) {
				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textSubsWidth.getText().length() >= 4)
					textSubsWidth.setText("");
			}

		});

		textSubsWidth.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsWidth.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseSubSize.mouseX = e.getX();
				MouseSubSize.offsetX = Integer.parseInt(textSubsWidth.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
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

		textSubsWidth.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubsWidth.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
					textSubsWidth.setText(String.valueOf(MouseSubSize.offsetX + (e.getX() - MouseSubSize.mouseX)));

				if (Integer.parseInt(textSubsWidth.getText()) >= FFPROBE.imageWidth) {
					textSubsWidth.setText(String.valueOf(FFPROBE.imageWidth));
					subsCanvas.setBounds(0, 0, VideoPlayerUI.player.getWidth(),
							(int) (VideoPlayerUI.player.getHeight()
									+ (float) Integer.parseInt(textSubtitlesPosition.getText())
											/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));
				} else {
					subsCanvas.setSize(
							(int) ((float) Integer.parseInt(textSubsWidth.getText())
									/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())),
							(int) (VideoPlayerUI.player.getHeight()
									+ (float) Integer.parseInt(textSubtitlesPosition.getText())
											/ ((float) FFPROBE.imageHeight / VideoPlayerUI.player.getHeight())));

					subsCanvas.setLocation((VideoPlayerUI.player.getWidth() - subsCanvas.getWidth()) / 2, 0);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		JLabel widthPx = new JLabel("px");
		widthPx.setEnabled(false);
		widthPx.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		widthPx.setForeground(Utils.themeColor);
		widthPx.setBounds(textSubsWidth.getLocation().x + textSubsWidth.getWidth() + 2, lblWidth.getY(),
				px1.getPreferredSize().width, 16);
		grpSubtitles.add(widthPx);

		JLabel lblColor = new JLabel(Main.language.getProperty("lblColor"));
		lblColor.setAlignmentX(SwingConstants.RIGHT);
		lblColor.setForeground(Utils.themeColor);
		lblColor.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblColor.setBounds(widthPx.getLocation().x + widthPx.getWidth() + 7, lblWidth.getY(),
				lblColor.getPreferredSize().width + 4, 16);
		grpSubtitles.add(lblColor);

		panelSubsColor = new JPanel();
		panelSubsColor.setName("panelSubsColor");
		panelSubsColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelSubsColor.setBackground(fontSubsColor);
		panelSubsColor.setBounds(lblColor.getLocation().x + lblColor.getWidth(), lblColor.getY() - 4, 41, 22);
		grpSubtitles.add(panelSubsColor);

		panelSubsColor.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				fontSubsColor = JColorChooser.showDialog(frame, Main.language.getProperty("chooseColor"),
						Color.WHITE);

				if (fontSubsColor != null) {
					panelSubsColor.setBackground(fontSubsColor);
					
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
			}

		});

		JLabel lblSize = new JLabel(Main.language.getProperty("lblSize"));
		lblSize.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblSize.setAlignmentX(SwingConstants.RIGHT);
		lblSize.setForeground(Utils.themeColor);
		lblSize.setBounds(panelSubsColor.getLocation().x + panelSubsColor.getWidth() + 7, lblColor.getLocation().y, lblSize.getPreferredSize().width, 16);
		grpSubtitles.add(lblSize);

		textSubsSize = new JTextField("18");
		textSubsSize.setName("textSubsSize");
		textSubsSize.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsSize.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textSubsSize.setBounds(lblSize.getLocation().x + lblSize.getWidth() + 5, lblColor.getLocation().y, 34, 16);
		grpSubtitles.add(textSubsSize);

		textSubsSize.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsSize.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				MouseSubsPosition.mouseX = e.getX();
				MouseSubsPosition.offsetX = Integer.parseInt(textSubsSize.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		textSubsSize.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

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

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textSubsSize.getText().length() >= 3)
					textSubsSize.setText("");
			}

		});

		textSubsSize.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubsSize.getText().length() > 0
						&& textSubsSize.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {

					int value = MouseSubsPosition.offsetX + (e.getX() - MouseSubsPosition.mouseX);

					if (value < 1) {
						textSubsSize.setText("1");
					} else
						textSubsSize.setText(String.valueOf(value));

					VideoPlayerUtils.loadImage();
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
		percent1.setBounds(textSubsSize.getLocation().x + textSubsSize.getWidth() + 2, lblSize.getY(),
				px1.getPreferredSize().width, 16);
		if (Main.getLanguage.equals(Locale.of("pl").getDisplayLanguage()) == false)
			grpSubtitles.add(percent1);

		lblSubsBackground = new JLabel(Main.language.getProperty("lblBackgroundOff"));
		lblSubsBackground.setName("lblSubsBackground");
		lblSubsBackground.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), Utils.c42, 1, 10));
		lblSubsBackground.setBackground(Utils.c42);
		lblSubsBackground.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubsBackground.setOpaque(true);
		lblSubsBackground.setFont(new Font(Main.boldFont, Font.PLAIN, 11));
		lblSubsBackground.setBounds(lblWidth.getX(), lblColor.getY() + lblColor.getHeight() + 13, 80, 16);
		grpSubtitles.add(lblSubsBackground);

		lblSubsBackground.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

				if (lblSubsBackground.getText().equals(Main.language.getProperty("lblBackgroundOff"))) {

					lblSubsBackground.setText(Main.language.getProperty("lblBackgroundOn"));

					lblSubsOutline.setText(Main.language.getProperty("lblOpacity"));
				} else {
					lblSubsBackground.setText(Main.language.getProperty("lblBackgroundOff"));

					lblSubsOutline.setText(Main.language.getProperty("lblSize"));
				}

				VideoPlayerOverlay.writeCurrentSubs(VideoPlayerCore.playerCurrentFrame, false);
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

		JLabel lblColor2 = new JLabel(Main.language.getProperty("lblColor"));
		lblColor2.setAlignmentX(SwingConstants.RIGHT);
		lblColor2.setForeground(Utils.themeColor);
		lblColor2.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblColor2.setBounds(lblSubsBackground.getLocation().x + lblSubsBackground.getWidth() + 7,
				lblSubsBackground.getY(), lblColor2.getPreferredSize().width + 4, 16);
		grpSubtitles.add(lblColor2);

		panelSubsColor2 = new JPanel();
		panelSubsColor2.setName("panelSubsColor2");
		panelSubsColor2.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		panelSubsColor2.setBackground(backgroundSubsColor);
		panelSubsColor2.setBounds(lblColor2.getLocation().x + lblColor2.getWidth(), lblColor2.getY() - 4, 41, 22);
		grpSubtitles.add(panelSubsColor2);

		panelSubsColor2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				backgroundSubsColor = JColorChooser.showDialog(frame, Main.language.getProperty("chooseColor"),
						Color.WHITE);

				if (backgroundSubsColor != null) {
					panelSubsColor2.setBackground(backgroundSubsColor);
					
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
			}

		});

		if (Main.getLanguage.equals(Locale.of("pl").getDisplayLanguage())) {
			lblSubsOutline = new JLabel(Main.language.getProperty("lblSize"));
		} else
			lblSubsOutline = new JLabel(Main.language.getProperty("lblOpacity"));

		lblSubsOutline.setFont(new Font(Main.mainFont, Font.PLAIN, 12));
		lblSubsOutline.setName("lblSubsOutline");
		lblSubsOutline.setForeground(Utils.themeColor);
		lblSubsOutline.setAlignmentX(SwingConstants.RIGHT);
		lblSubsOutline.setBounds(panelSubsColor2.getLocation().x + panelSubsColor2.getWidth() + 7,
				lblColor2.getLocation().y, lblSubsOutline.getPreferredSize().width + 2, 16);
		lblSubsOutline.setText(Main.language.getProperty("lblSize"));
		grpSubtitles.add(lblSubsOutline);

		textSubsOutline = new JTextField("50");
		textSubsOutline.setName("textSubsOutline");
		textSubsOutline.setHorizontalAlignment(SwingConstants.RIGHT);
		textSubsOutline.setFont(new Font(Main.mainFont, Font.PLAIN, 11));
		textSubsOutline.setBounds(lblSubsOutline.getLocation().x + lblSubsOutline.getWidth() + 5,
				lblColor2.getLocation().y, 34, 16);
		grpSubtitles.add(textSubsOutline);

		textSubsOutline.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				textSubsOutline.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {

				MouseSubsPosition.mouseX = e.getX();
				MouseSubsPosition.offsetX = Integer.parseInt(textSubsOutline.getText());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				VideoPlayerUtils.restartPlayback();
			}

		});

		textSubsOutline.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

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

			@Override
			public void keyTyped(KeyEvent e) {

				char caracter = e.getKeyChar();
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿'
						|| String.valueOf(caracter).matches("[éèçàù]"))
					e.consume();
				else if (textSubsOutline.getText().length() >= 3)
					textSubsOutline.setText("");
			}

		});

		textSubsOutline.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				if (textSubsOutline.getText().length() > 0
						&& textSubsOutline.getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
					int value = MouseSubsPosition.offsetX + (e.getX() - MouseSubsPosition.mouseX);

					if (value < 1) {
						textSubsOutline.setText("1");
					} else if (value > 100) {
						textSubsOutline.setText("100");
					} else
						textSubsOutline.setText(String.valueOf(value));

					VideoPlayerUtils.loadImage();
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
		percent2.setBounds(textSubsOutline.getLocation().x + textSubsOutline.getWidth() + 2, lblSubsOutline.getY(),
				px1.getPreferredSize().width, 16);
		grpSubtitles.add(percent2);

		// Border
		subsCanvas = new JPanel();
		subsCanvas.setBorder(BorderFactory.createDashedBorder(Color.WHITE, 4, 4));
		subsCanvas.setOpaque(false);
		subsCanvas.setSize(640, 360);
		subsCanvas.setBackground(new Color(0, 0, 0, 0));

		for (Component c : grpSubtitles.getComponents()) {
			if (c instanceof JCheckBox == false) {
				c.setEnabled(false);
			}
		}
		if (comboFonctions.getSelectedItem().toString().equals(language.getProperty("functionRewrap")) == false)
			comboSubsSource.setEnabled(true);
	}
}
