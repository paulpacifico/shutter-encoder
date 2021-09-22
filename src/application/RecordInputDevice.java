/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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

package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;
import library.FFPROBE;

public class RecordInputDevice {

	public static JDialog frame;
	static JComboBox<String> comboScreenVideo;
	static JComboBox<String> comboScreenAudio;
	static JComboBox<String> comboInputVideo;
	static JComboBox<String> comboInputAudio;
	
	public static Integer videoDeviceIndex = 0;
	public static Integer audioDeviceIndex = -1;
	public static Integer overlayAudioDeviceIndex = -1;
	public static Integer screenWidth = 0;
	public static Integer screenHeigth = 0;
	public static String inputDeviceResolution = "";

	public RecordInputDevice() {
		frame = new JDialog();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setModal(true);
		if (System.getProperty("os.name").contains("Windows"))
			frame.setSize(350, 210);
		else
			frame.setSize(330, 130);
		frame.setTitle( Shutter.language.getProperty("menuItemInputDevice"));
		
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		frame.setLocation((Shutter.frame.getLocation().x + Shutter.frame.getWidth() / 2) - frame.getWidth() / 2,
				(Shutter.frame.getLocation().y + Shutter.frame.getHeight() / 3) - frame.getHeight() / 2);
		
		devices();
						
		frame.setVisible(true);	
	}	
	
	private static void devices()
	{
		JLabel screenVideo = new JLabel(Shutter.language.getProperty("video") + Shutter.language.getProperty("colon"));
		screenVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		screenVideo.setBounds(12, 12, 40, 14);
		frame.getContentPane().add(screenVideo);		
						
		String firstInput[] = new String[FFMPEG.videoDevices.toString().split(":").length];
		int i = 0;
		for (String videoDevice : FFMPEG.videoDevices.toString().split(":"))
		{		
			firstInput[i] = videoDevice.replace(Shutter.language.getProperty("noVideo"), "Capture.current.screen");
			i++;
		}
			
		comboScreenVideo = new JComboBox<String>(firstInput);	
		comboScreenVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboScreenVideo.setEditable(false);
		comboScreenVideo.setMaximumRowCount(20);
		comboScreenVideo.setLocation(screenVideo.getX() + screenVideo.getWidth() + 4, screenVideo.getLocation().y - 3);
		if (System.getProperty("os.name").contains("Windows"))
			comboScreenVideo.setSize(frame.getWidth() - screenVideo.getX() - screenVideo.getWidth() - 52, 22);
		else
			comboScreenVideo.setSize(frame.getWidth() - screenVideo.getX() - screenVideo.getWidth() - 42, 22);
		frame.getContentPane().add(comboScreenVideo);
		
		comboScreenVideo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboScreenVideo.getSelectedItem().equals("Capture.current.screen") == false)
				{
					comboInputVideo.setEnabled(false);
					comboInputVideo.setSelectedIndex(0);
					comboInputAudio.setEnabled(false);
					comboInputAudio.setSelectedIndex(0);
				}
				else
				{
					if (comboScreenAudio.getSelectedIndex() > 0)
						comboInputAudio.setEnabled(true);
					
					comboInputVideo.setEnabled(true);
				}
			}
			
		});
		
		JLabel screenAudio = new JLabel(Shutter.language.getProperty("audio") + Shutter.language.getProperty("colon"));
		screenAudio.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		screenAudio.setBounds(12, screenVideo.getY() + screenVideo.getHeight() + 14, 40, 14);
		frame.getContentPane().add(screenAudio);
		
		comboScreenAudio = new JComboBox<String>(FFMPEG.audioDevices.toString().split(":"));
		comboScreenAudio.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboScreenAudio.setEditable(false);
		comboScreenAudio.setMaximumRowCount(20);
		comboScreenAudio.setBounds(comboScreenVideo.getX(), screenAudio.getLocation().y - 3, comboScreenVideo.getWidth(), 22);
		frame.getContentPane().add(comboScreenAudio);
		
		comboScreenAudio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboScreenAudio.getSelectedIndex() == 0 || comboScreenVideo.getSelectedItem().equals("Capture.current.screen") == false)
				{
					comboInputAudio.setEnabled(false);
					comboInputAudio.setSelectedIndex(0);
				}
				else
					comboInputAudio.setEnabled(true);
				
				if (comboScreenAudio.getSelectedIndex() >= 0 && System.getProperty("os.name").contains("Mac"))
				{
					try {
						
						String pathToSwitchAudioSource = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						pathToSwitchAudioSource = pathToSwitchAudioSource.substring(0,pathToSwitchAudioSource.length()-1);
						pathToSwitchAudioSource = pathToSwitchAudioSource.substring(0,(int) (pathToSwitchAudioSource.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/SwitchAudioSource";
					
						ProcessBuilder switchAudioSource = new ProcessBuilder("/bin/bash", "-c" , pathToSwitchAudioSource + " -t input -s " + '"' + comboScreenAudio.getSelectedItem().toString() + '"');			
						switchAudioSource.start();
						
					} catch (IOException e) {}				
				}
			}
			
		});
		
		JLabel iconScreenPreview = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconScreenPreview.setHorizontalAlignment(SwingConstants.CENTER);
		iconScreenPreview.setBounds(comboScreenVideo.getX() + comboScreenVideo.getWidth() + 8, comboScreenVideo.getLocation().y + 3, 16, 16);
		iconScreenPreview.setToolTipText(Shutter.language.getProperty("preview"));
		frame.getContentPane().add(iconScreenPreview);
		
		iconScreenPreview.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {

					String channels = "";
					String videoOutput = "";
					String audioOutput = "";
					
					Shutter.liste.removeAllElements();
					
					if (comboScreenVideo.getSelectedItem().toString().equals("Capture.current.screen"))
					{
						Shutter.liste.addElement("Capture.current.screen");
						if (comboInputVideo.getSelectedIndex() > 0 && System.getProperty("os.name").contains("Windows"))
							videoDeviceIndex = (comboInputVideo.getSelectedIndex());						
					}
					else
					{
						Shutter.liste.addElement("Capture.input.device");
						if (System.getProperty("os.name").contains("Windows"))
							videoDeviceIndex = (comboScreenVideo.getSelectedIndex());
						else
							videoDeviceIndex = (comboScreenVideo.getSelectedIndex() - 1);
					}	
					
					//Main audio
					if (comboScreenAudio.getSelectedIndex() > 0)
						audioDeviceIndex = comboScreenAudio.getSelectedIndex();
					else
						audioDeviceIndex = -1;
					
					//Permet d'injecter la resolution à FFPROBE
					setInputDevices();
					
					if (comboScreenAudio.getSelectedIndex() > 0) 
					{
						channels = "[0:a:0]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
	
					// On ajoute la vidéo
					videoOutput = "[0:v]scale=1080:-1[v]" + ";" + channels + "[v]";
					
					if (FFPROBE.channels == 0 || Shutter.liste.getElementAt(0).equals("Capture.input.device") && System.getProperty("os.name").contains("Windows")) {
						videoOutput = "scale=1080:-1" + '"';
						audioOutput = "";
					}
										
					String cmd = " -filter_complex " + '"' + videoOutput + audioOutput
							+ " -c:v rawvideo -map a? -f nut pipe:play |";

					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					if (Shutter.liste.getElementAt(0).equals("Capture.current.screen") && RecordInputDevice.audioDeviceIndex > 0 || System.getProperty("os.name").contains("Mac") && Shutter.liste.getElementAt(0).equals("Capture.input.device") && RecordInputDevice.audioDeviceIndex > 0)
						cmd = cmd.replace("0:v", "1:v");					
														
					FFMPEG.toFFPLAY(RecordInputDevice.setInputDevices() + cmd);
					
					if (FFMPEG.isRunning) {
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (FFMPEG.isRunning && FFMPEG.error == false);
					}
					
					Shutter.liste.removeAllElements();
					
					Shutter.enableAll();
					Shutter.progressBar1.setValue(0);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				iconScreenPreview.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconScreenPreview.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		@SuppressWarnings("serial")
		JPanel screenLines = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				float dash[] = { 5.0f };
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f));
				g2.draw(new Line2D.Float(14, 0, 14, 20));
				g2.draw(new Line2D.Float(1, 18, 14, 18));
			}
		};
		screenLines.setBackground(new Color(50, 50, 50));
		screenLines.setBounds(comboScreenVideo.getX() + comboScreenVideo.getWidth() + 2, iconScreenPreview.getY() + iconScreenPreview.getHeight() + 2, 30, 30);
		frame.getContentPane().add(screenLines);
		
		JLabel inputVideo = new JLabel(Shutter.language.getProperty("video") + Shutter.language.getProperty("colon"));
		inputVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		inputVideo.setBounds(12, screenAudio.getY() + screenAudio.getHeight() + 25, 40, 14);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(inputVideo);
		
		comboInputVideo = new JComboBox<String>(FFMPEG.videoDevices.toString().split(":"));
		comboInputVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboInputVideo.setEditable(false);
		comboInputVideo.setEnabled(true);
		comboInputVideo.setMaximumRowCount(20);
		comboInputVideo.setBounds(comboScreenVideo.getX(), inputVideo.getLocation().y - 3, comboScreenVideo.getWidth(), 22);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(comboInputVideo);
		
		JLabel inputAudio = new JLabel(Shutter.language.getProperty("audio") + Shutter.language.getProperty("colon"));
		inputAudio.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		inputAudio.setBounds(12, inputVideo.getY() + inputVideo.getHeight() + 14, 40, 14);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(inputAudio);
		
		comboInputAudio = new JComboBox<String>(FFMPEG.audioDevices.toString().split(":"));
		comboInputAudio.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboInputAudio.setEditable(false);
		comboInputAudio.setEnabled(false);
		comboInputAudio.setMaximumRowCount(20);
		comboInputAudio.setBounds(comboScreenVideo.getX(), inputAudio.getLocation().y - 3, comboScreenVideo.getWidth(), 22);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(comboInputAudio);
		
		JLabel iconDevicePreview = new JLabel(new FlatSVGIcon("contents/preview.svg", 16, 16));
		iconDevicePreview.setHorizontalAlignment(SwingConstants.CENTER);
		iconDevicePreview.setBounds(comboInputVideo.getX() + comboInputVideo.getWidth() + 8, comboInputVideo.getLocation().y + 3, 16, 16);
		iconDevicePreview.setToolTipText(Shutter.language.getProperty("preview"));
		
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(iconDevicePreview);
		
		iconDevicePreview.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {

				if (comboInputVideo.getSelectedIndex() > 0 || comboInputAudio.getSelectedIndex() > 0)
				{
					String channels = "";
					String videoOutput = "";
					String audioOutput = "";
					
					Shutter.liste.removeAllElements();
					
					Shutter.liste.addElement("Capture.input.device");
					videoDeviceIndex = comboInputVideo.getSelectedIndex();
					
					//Second audio
					if (comboInputAudio.getSelectedIndex() > 0)	
						overlayAudioDeviceIndex = comboInputAudio.getSelectedIndex();
					else
						overlayAudioDeviceIndex = -1;
					
					//Permet d'injecter la resolution à FFPROBE
					setOverlayDevice();
							
					//Audio only
					if (comboInputVideo.getSelectedIndex() == 0 && comboInputAudio.getSelectedIndex() > 0) 
					{
						audioOutput = "[0:a:0]showvolume=f=0.001:b=4:w=720:h=12[volume]" + '"' + " -map " + '"' + "[volume]" + '"';
					}
					else
					{
						channels = "[0:a:0]showvolume=f=0.001:b=4:w=1080:h=12[a0];";
						audioOutput = "[a0]vstack" + "[volume]" + '"' + " -map " + '"' + "[volume]" + '"';

						// On ajoute la vidéo
						videoOutput = "[0:v]scale=1080:-1[v]" + ";" + channels + "[v]";
						
						if (FFPROBE.channels == 0 || Shutter.liste.getElementAt(0).equals("Capture.input.device")) {
							videoOutput = "scale=1080:-1" + '"';
							audioOutput = "";
						}
					} 	
										
					String cmd = " -filter_complex " + '"' + videoOutput + audioOutput
							+ " -c:v rawvideo -map a? -f nut pipe:play |";

					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				
										
					if (comboInputVideo.getSelectedIndex() == 0 && comboInputAudio.getSelectedIndex() > 0) 
						FFMPEG.toFFPLAY(RecordInputDevice.setOverlayDevice().replace("video=" + '"' + "No video" + '"' + ":", "") + cmd);
					else
						FFMPEG.toFFPLAY(RecordInputDevice.setOverlayDevice() + cmd);
					
					if (FFMPEG.isRunning) {
						do {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {}
						} while (FFMPEG.isRunning && FFMPEG.error == false);
					}
					
					Shutter.liste.removeAllElements();
					
					Shutter.enableAll();
					Shutter.progressBar1.setValue(0);
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
				iconDevicePreview.setIcon(new FlatSVGIcon("contents/preview_hover.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				iconDevicePreview.setIcon(new FlatSVGIcon("contents/preview.svg", 16, 16));
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		@SuppressWarnings("serial")
		JPanel devicesLines = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				float dash[] = { 5.0f };
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 3.0f, dash, 0.0f));
				g2.draw(new Line2D.Float(14, 0, 14, 20));
				g2.draw(new Line2D.Float(1, 18, 14, 18));
			}
		};
		devicesLines.setBackground(new Color(50, 50, 50));
		devicesLines.setBounds(comboInputVideo.getX() + comboInputVideo.getWidth() + 2, iconDevicePreview.getY() + iconDevicePreview.getHeight() + 2, 30, 30);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(devicesLines);
		
		JButton btnOK = new JButton("OK");
		btnOK.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOK.setSize(screenVideo.getWidth() + comboScreenVideo.getWidth() + 24, 21);	
		if (System.getProperty("os.name").contains("Windows"))
			btnOK.setLocation(12, comboInputAudio.getY() + comboInputAudio.getHeight() + 14);	
		else
			btnOK.setLocation(12, comboScreenAudio.getY() + comboScreenAudio.getHeight() + 14);	
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Shutter.liste.removeAllElements();
				
				if (comboScreenVideo.getSelectedItem().toString().equals("Capture.current.screen"))
				{
					Shutter.liste.addElement("Capture.current.screen");
					if (comboInputVideo.getSelectedIndex() > 0 && System.getProperty("os.name").contains("Windows"))
						videoDeviceIndex = (comboInputVideo.getSelectedIndex());						
				}
				else
				{
					Shutter.liste.addElement("Capture.input.device");
					if (System.getProperty("os.name").contains("Windows"))
						videoDeviceIndex = (comboScreenVideo.getSelectedIndex());
					else
						videoDeviceIndex = (comboScreenVideo.getSelectedIndex() - 1);
				}
				
				//Overlay
				if (comboInputVideo.getSelectedIndex() > 0)
					Shutter.overlayDeviceIsRunning = true;
				
				//Main audio
				if (comboScreenAudio.getSelectedIndex() > 0)
					audioDeviceIndex = comboScreenAudio.getSelectedIndex();
				else
					audioDeviceIndex = -1;

				//Second audio
				if (comboInputAudio.getSelectedIndex() > 0)	
					overlayAudioDeviceIndex = comboInputAudio.getSelectedIndex();
				else
					overlayAudioDeviceIndex = -1;
				
				//Permet d'injecter la resolution à FFPROBE
				setInputDevices();
				
				//Important
				Shutter.inputDeviceIsRunning = true;
				inputDeviceResolution = "";

				//Permet d'injecter la resolution à FFPROBE
				if (comboInputVideo.getSelectedIndex() > 0 || comboScreenVideo.getSelectedItem().toString().equals("Capture.current.screen") == false)
				{	
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
					try {
					 
						FFPROBE.Data("Capture.input.device");	
						do
						{
							Thread.sleep(100);
						}
						while (FFPROBE.isRunning);
					
					} catch (InterruptedException er) {}
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				
				//Important
				inputDeviceResolution = FFPROBE.imageResolution;	
				if (FFPROBE.entrelaced == null)
				  	FFPROBE.entrelaced = "0";
				
				//Watermark
				if (comboInputVideo.getSelectedIndex() > 0)
				{
					if (Shutter.caseLogo.isSelected() == false)
						Shutter.caseLogo.setSelected(true);
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Shutter.caseLogo.setSelected(true);
					
					if (WatermarkWindow.frame == null)
						new WatermarkWindow();
					else {
						WatermarkWindow.loadImage("0", "0", "0", true, -1);
						Utils.changeDialogVisibility(WatermarkWindow.frame, false);
					}
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
								
				//Set options
				Shutter.addToList.setVisible(false);
				Shutter.case2pass.setSelected(false);
				
				if (Shutter.caseDisplay.isEnabled())
					Shutter.caseDisplay.setSelected(true);
				
				if (Shutter.caseChangeFolder1.isSelected() == false)
				{
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						Shutter.lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
					else
						Shutter.lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
				}
				
				Shutter.caseChangeFolder1.setSelected(true);
				
				Shutter.changeFilters();
				
				frame.dispose();
				
				JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("chooseFunction"),
						Shutter.language.getProperty("menuItemInputDevice"), JOptionPane.PLAIN_MESSAGE,
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
	}
	
	public static String setInputDevices() {
		
		String videoDevice = "";
		if (Shutter.liste.getElementAt(0).equals("Capture.input.device"))
		{
			String getVideoDevices[] = FFMPEG.videoDevices.toString().split(":");
			videoDevice = getVideoDevices[videoDeviceIndex];
		}
		
		String setAudio = setAudioDevice();
				
		String setSecondAudio = "";
		if (videoDeviceIndex == 0 && System.getProperty("os.name").contains("Windows") && overlayAudioDeviceIndex > 0)
		{
			setSecondAudio = " -thread_queue_size 4096 -f dshow -i " + setOverlayAudioDevice();			
		}
		
		GraphicsConfiguration config = frame.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int screenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
		    if (allScreens[i].equals(myScreen))
		    {
		    	screenIndex = i;
		        break;
		    }
		}
		
        AffineTransform transform =  allScreens[screenIndex].getDefaultConfiguration().getDefaultTransform();
        
		if (System.getProperty("os.name").contains("Mac") && transform.isIdentity() == false) // false = Retina			
		{
			screenWidth = allScreens[screenIndex].getDisplayMode().getWidth() * 2;
			screenHeigth = allScreens[screenIndex].getDisplayMode().getHeight() * 2;
		}
		else
		{
			screenWidth = allScreens[screenIndex].getDisplayMode().getWidth();
			screenHeigth = allScreens[screenIndex].getDisplayMode().getHeight();
		}
		
		Integer screenPositionX = allScreens[screenIndex].getDefaultConfiguration().getBounds().x;
		Integer screenPositionY = allScreens[screenIndex].getDefaultConfiguration().getBounds().y;	
		
		if (Shutter.liste.getElementAt(0).equals("Capture.current.screen"))
		{
	        CropVideo.ImageWidth = screenWidth;
	        CropVideo.ImageHeight = screenHeigth;
	        CropImage.ImageWidth = screenWidth;
	        CropImage.ImageHeight = screenHeigth;
	    	WatermarkWindow.ImageWidth = screenWidth;
	    	WatermarkWindow.ImageHeight = screenHeigth;
	    	OverlayWindow.ImageWidth = screenWidth;
	    	OverlayWindow.ImageHeight = screenHeigth;
	    	SubtitlesWindow.ImageWidth = screenWidth;
	    	SubtitlesWindow.ImageHeight = screenHeigth;
	    	ColorImage.ImageWidth = screenWidth;
	    	ColorImage.ImageHeight = screenHeigth;
	        	
			FFPROBE.imageResolution = screenWidth + "x" + screenHeigth;
			FFPROBE.entrelaced = "0";
		}
			
		FFPROBE.audioOnly = false;
		
		if (System.getProperty("os.name").contains("Mac"))
		{			
			if (Shutter.liste.getElementAt(0).equals("Capture.current.screen"))
			{
				if (setAudio != "") //Audio needs to be first for sync
					return setAudio + " -thread_queue_size 4096 -f avfoundation -pix_fmt uyvy422 -probesize 100M -rtbufsize 100M -capture_cursor 1 -framerate " + Settings.txtScreenRecord.getText() + " -i " + '"' + (int) ( FFMPEG.firstScreenIndex + screenIndex) + '"';
				else
					return "-thread_queue_size 4096 -f avfoundation -pix_fmt uyvy422 -probesize 100M -rtbufsize 100M -capture_cursor 1 -framerate " + Settings.txtScreenRecord.getText() + " -i " + '"' + (int) ( FFMPEG.firstScreenIndex + screenIndex) + '"';
			}
			else
			{				
				return setAudio + " -thread_queue_size 4096 -f avfoundation -pix_fmt uyvy422 -probesize 100M -rtbufsize 100M -framerate " + Settings.txtInputDevice.getText() + " -i " + '"' + videoDeviceIndex + '"';
			}
		}
		else if (System.getProperty("os.name").contains("Windows"))
		{
			if (Shutter.liste.getElementAt(0).equals("Capture.current.screen"))
			{				
				if (setAudio != "") //Audio needs to be first for sync
					return "-thread_queue_size 4096 -f dshow -i " + setAudio + " -thread_queue_size 4096 -f gdigrab -draw_mouse 1 -framerate " + Settings.txtScreenRecord.getText() + " -offset_x " + screenPositionX + " -offset_y " + screenPositionY + " -video_size " + screenWidth + "x" + screenHeigth + " -probesize 100M -rtbufsize 100M -i " + '"' + "desktop" + '"' + setSecondAudio;
				else
					return "-thread_queue_size 4096 -f gdigrab -draw_mouse 1 -framerate " + Settings.txtScreenRecord.getText() + " -offset_x " + screenPositionX + " -offset_y " + screenPositionY + " -video_size " + screenWidth + "x" + screenHeigth + " -probesize 100M -rtbufsize 100M -i " + '"' + "desktop" + '"';
			}
			else
			{
				if (setAudio != "" && videoDeviceIndex > 0)
					setAudio = ":" + setAudio;	
				
				if (videoDeviceIndex > 0)
					return "-thread_queue_size 4096 -f dshow -probesize 100M -rtbufsize 100M -framerate " + Settings.txtInputDevice.getText() + " -i video=" + '"' + videoDevice + '"' + setAudio;
				else
					return "-thread_queue_size 4096 -f dshow -probesize 100M -rtbufsize 100M -framerate " + Settings.txtInputDevice.getText() + " -i " + setAudio;					
			}
		}
		else
			return "-thread_queue_size 4096 -f x11grab -framerate " + Settings.txtScreenRecord.getText() + " -video_size " + screenWidth + "x" + screenHeigth + " -probesize 100M -rtbufsize 100M -i :0.0+" + screenPositionX + "," + screenPositionY + setAudio;
	}
	
	public static String setOverlayDevice() {
		
		String getVideoDevices[] = FFMPEG.videoDevices.toString().split(":");
		String videoDevice = getVideoDevices[videoDeviceIndex];
		
		if (inputDeviceResolution != "")
		{
			FFPROBE.imageResolution = inputDeviceResolution;
					
	        String splitx[]= inputDeviceResolution.split("x");
	
	        int deviceWidth = Integer.parseInt(splitx[0]);
	        int deviceHeight = Integer.parseInt(splitx[1]);
			        
	    	WatermarkWindow.ImageWidth = deviceWidth;
	    	WatermarkWindow.ImageHeight = deviceHeight;
		}		
		
		FFPROBE.audioOnly = false;
		    	
		if (System.getProperty("os.name").contains("Mac"))
		{
			return "-thread_queue_size 4096 -f avfoundation -pix_fmt uyvy422 -probesize 100M -rtbufsize 100M -framerate " + Settings.txtInputDevice.getText() + " -i " + '"' + videoDeviceIndex + '"';
		}
		else if (System.getProperty("os.name").contains("Windows"))
		{
			String setAudio = setOverlayAudioDevice();
			
			if (setAudio != "")
				setAudio = ":" + setAudio;
			
			return "-thread_queue_size 4096 -f dshow -probesize 100M -rtbufsize 100M -framerate " + Settings.txtInputDevice.getText() + " -i video=" + '"' + videoDevice + '"' + setAudio;
		}
		else
			return "";
	}
	
	public static String setAudioDevice() {
		
		String setAudio = "";
		FFPROBE.channels = 0;
		FFPROBE.stereo = false;
		
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Windows"))
		{
			String getAudioDevices[] = FFMPEG.audioDevices.toString().split(":");
			
			if (audioDeviceIndex > 0)
			{
				String audioDevice = getAudioDevices[audioDeviceIndex];
				
				if (System.getProperty("os.name").contains("Mac"))
				{					
					setAudio = "-thread_queue_size 4096 -f openal -sample_rate 48k -i " + '"' + audioDevice + '"';		
				}
				else if (System.getProperty("os.name").contains("Windows"))
				{
					setAudio = "audio=" + '"' + audioDevice + '"';	
				}
				
				FFPROBE.channels = 1;
				FFPROBE.stereo = true;
			}				
		}
		
		return setAudio;
	}
	
	public static String setOverlayAudioDevice() {
				
		String setAudio = "";
		FFPROBE.channels = 0;
		FFPROBE.stereo = false;
		String getAudioDevices[] = FFMPEG.audioDevices.toString().split(":");

		if (overlayAudioDeviceIndex > 0)
		{
			String audioDevice = getAudioDevices[overlayAudioDeviceIndex];
			
			if (System.getProperty("os.name").contains("Windows"))
			{
				setAudio = "audio=" + '"' + audioDevice + '"';	
			}
			
			if (audioDeviceIndex > 0)
				FFPROBE.channels = 2;
			else
				FFPROBE.channels = 1;
		}
		else if (audioDeviceIndex > 0)
			FFPROBE.channels = 1;			
		
		if (FFPROBE.channels == 1)
			FFPROBE.stereo = true;	
		else
			FFPROBE.stereo = false;
			
		
		return setAudio;
	}
	
}
