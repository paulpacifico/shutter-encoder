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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import library.FFMPEG;
import library.FFPROBE;

public class RecordInputDevice {

	public static JDialog frame;
	static JComboBox<String> comboScreenVideo;
	static JComboBox<String> comboScreenAudio;
	static JComboBox<String> comboInputVideo;
	static JComboBox<String> comboInputAudio;

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
		screenVideo.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		comboScreenVideo.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboScreenVideo.setEditable(false);
		comboScreenVideo.setMaximumRowCount(20);
		comboScreenVideo.setLocation(screenVideo.getX() + screenVideo.getWidth() + 4, screenVideo.getLocation().y - 3);
		if (System.getProperty("os.name").contains("Windows"))
			comboScreenVideo.setSize(frame.getWidth() - screenVideo.getX() - screenVideo.getWidth() - 32, 22);
		else
			comboScreenVideo.setSize(frame.getWidth() - screenVideo.getX() - screenVideo.getWidth() - 22, 22);
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
		screenAudio.setFont(new Font("FreeSans", Font.PLAIN, 12));
		screenAudio.setBounds(12, screenVideo.getY() + screenVideo.getHeight() + 14, 40, 14);
		frame.getContentPane().add(screenAudio);
		
		comboScreenAudio = new JComboBox<String>(FFMPEG.audioDevices.toString().split(":"));
		comboScreenAudio.setFont(new Font("FreeSans", Font.PLAIN, 10));
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
			}
			
		});
		
		JLabel inputVideo = new JLabel(Shutter.language.getProperty("video") + Shutter.language.getProperty("colon"));
		inputVideo.setFont(new Font("FreeSans", Font.PLAIN, 12));
		inputVideo.setBounds(12, screenAudio.getY() + screenAudio.getHeight() + 25, 40, 14);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(inputVideo);
		
		comboInputVideo = new JComboBox<String>(FFMPEG.videoDevices.toString().split(":"));
		comboInputVideo.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboInputVideo.setEditable(false);
		comboInputVideo.setEnabled(true);
		comboInputVideo.setMaximumRowCount(20);
		comboInputVideo.setBounds(comboScreenVideo.getX(), inputVideo.getLocation().y - 3, comboScreenVideo.getWidth(), 22);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(comboInputVideo);
		
		JLabel inputAudio = new JLabel(Shutter.language.getProperty("audio") + Shutter.language.getProperty("colon"));
		inputAudio.setFont(new Font("FreeSans", Font.PLAIN, 12));
		inputAudio.setBounds(12, inputVideo.getY() + inputVideo.getHeight() + 14, 40, 14);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(inputAudio);
		
		comboInputAudio = new JComboBox<String>(FFMPEG.audioDevices.toString().split(":"));
		comboInputAudio.setFont(new Font("FreeSans", Font.PLAIN, 10));
		comboInputAudio.setEditable(false);
		comboInputAudio.setEnabled(false);
		comboInputAudio.setMaximumRowCount(20);
		comboInputAudio.setBounds(comboScreenVideo.getX(), inputAudio.getLocation().y - 3, comboScreenVideo.getWidth(), 22);
		if (System.getProperty("os.name").contains("Windows"))
			frame.getContentPane().add(comboInputAudio);
		
		JButton btnOK = new JButton("OK");
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setSize(screenVideo.getWidth() + comboScreenVideo.getWidth() + 4, 21);	
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
						Utils.videoDeviceIndex = (comboInputVideo.getSelectedIndex());						
				}
				else
				{
					Shutter.liste.addElement("Capture.input.device");
					if (System.getProperty("os.name").contains("Windows"))
						Utils.videoDeviceIndex = (comboInputVideo.getSelectedIndex() + 1);
					else
						Utils.videoDeviceIndex = (comboInputVideo.getSelectedIndex());
				}

				Utils.inputDeviceIsRunning = true;
																
				//Overlay
				if (comboInputVideo.getSelectedIndex() > 0)
					Utils.overlayDeviceIsRunning = true;
				
				//Main audio
				if (comboScreenAudio.getSelectedIndex() > 0)
					Utils.audioDeviceIndex = comboScreenAudio.getSelectedIndex();
				else
					Utils.audioDeviceIndex = -1;

				//Second audio
				if (comboInputAudio.getSelectedIndex() > 0)	
					Utils.overlayAudioDeviceIndex = comboInputAudio.getSelectedIndex();
				else
					Utils.overlayAudioDeviceIndex = -1;
				
				//Permet d'injecter la resolution à FFPROBE
				Utils.setInputDevices();
				
				//Important
				Utils.inputDeviceResolution = "";

				//Permet d'injecter la resolution à FFPROBE
				if (comboInputVideo.getSelectedIndex() > 0 || comboScreenVideo.getSelectedItem().toString().equals("Capture.current.screen") == false)
				{	
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
					try {
					 
						FFPROBE.Data("Capture.input.device");	
						do
						{
							Thread.sleep(10);
						}
						while (FFPROBE.isRunning);
					
					} catch (InterruptedException er) {}
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				
				//Important
				Utils.inputDeviceResolution = FFPROBE.imageResolution;	
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
						WatermarkWindow.loadImage("0", "0", "0", true, -1, true);
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
				
				frame.setVisible(false);
			}
			
		});
	}
}
