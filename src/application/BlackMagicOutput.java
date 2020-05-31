/*******************************************************************************************
* Copyright (C) 2020 PACIFICO PAUL
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

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import library.DECKLINK;
import library.FFMPEG;
import library.FFPROBE;

public class BlackMagicOutput {

	public static JFrame frame; 
	public static JSlider slider;
	public static boolean sliderChange = false;
	private JRadioButton caseHD;
	private JButton btnPrevious;
	private JButton btnNext;
	public static JButton btnLire;
	
	private JRadioButton caseForceInterlace;
	private JRadioButton caseAfficherLeTimecode;
	public static int sliderValue = 0;
	public static JLabel lblTimecode;
	public static Integer timecode1 = 0;
	public static Integer timecode2 = 0;
	public static Integer timecode3 = 0;
	public static Integer timecode4 = 0;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public BlackMagicOutput() {
		
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			frame.setSize(488, 156);
		else
			frame.setSize(498, 166);	
		
		frame.setTitle(Shutter.language.getProperty("frameBlackMagicOutput") + " " + DECKLINK.getBlackMagic);
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		frame.setLocation(Shutter.frame.getLocation().x - frame.getWidth() - 20, Shutter.frame.getLocation().y + Shutter.frame.getHeight() / 2 - 40);
				
		frame.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent arg0) {
				if (DECKLINK.isRunning)
					DECKLINK.process.destroy();
				
				 
				 File stopframe = new File(Shutter.dirTemp + "frame.png");
				 if (stopframe.exists()) stopframe.delete();
			}
			
		});
		
		load();
		
		frame.setVisible(true);
		
		btnLire.doClick();		
	}
	
	private void load(){				
		caseHD = new JRadioButton(Shutter.language.getProperty("caseHD"));
		caseHD.setSelected(true);
		caseHD.setBackground(new Color(50,50,50));
		caseHD.setFont(new Font("Arial", Font.PLAIN, 12));
		caseHD.setBounds(6, 38, caseHD.getPreferredSize().width, 23);
		frame.getContentPane().add(caseHD);
		
		frame.addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (DECKLINK.isRunning)
					DECKLINK.process.destroy();
				
			}			
			
		});
		
		btnPrevious = new JButton("<");
		btnPrevious.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnPrevious.setBounds(6, 6, 30, 25);
		frame.getContentPane().add(btnPrevious);				
		
		btnPrevious.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
	      		if (Shutter.listeDeFichiers.getSelectedIndex() > 0)
	      		{     				
	      			slider.setValue(0);
	      			Shutter.listeDeFichiers.setSelectedIndex(Shutter.listeDeFichiers.getSelectedIndex() - 1);
      				btnLire.setText(Shutter.language.getProperty("btnLire"));
      				btnLire.doClick();	      				
	      		}					
			}
			
		});
		
		btnLire = new JButton(Shutter.language.getProperty("btnLire"));
		btnLire.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnLire.setBounds(12 + 30, 6, 461 - 60, 25);
		frame.getContentPane().add(btnLire);
		
		btnNext = new JButton(">");
		btnNext.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnNext.setBounds(btnLire.getX() + btnLire.getWidth() + 6, 6, 30, 25);
		frame.getContentPane().add(btnNext);
		
		btnNext.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
	      		if (Shutter.listeDeFichiers.getSelectedIndex() < Shutter.liste.getSize())
	      		{      				
	      			slider.setValue(0);
	      			Shutter.listeDeFichiers.setSelectedIndex(Shutter.listeDeFichiers.getSelectedIndex() + 1);
      				btnLire.setText(Shutter.language.getProperty("btnLire"));
      				btnLire.doClick();
	      		}				
			}
			
		});
		
		caseForceInterlace = new JRadioButton(Shutter.language.getProperty("caseForcerEntrelacement"));
		caseForceInterlace.setSelected(false);
		caseForceInterlace.setBounds(170, 38, caseForceInterlace.getPreferredSize().width + 20, 23);
		caseForceInterlace.setFont(new Font("Arial", Font.PLAIN, 12));
		caseForceInterlace.setBackground(new Color(50,50,50));
		frame.getContentPane().add(caseForceInterlace);
		
		caseAfficherLeTimecode = new JRadioButton(Shutter.language.getProperty("caseAfficherLeTimecode"));
		caseAfficherLeTimecode.setSelected(true);
		caseAfficherLeTimecode.setBounds(338, 38, caseAfficherLeTimecode.getPreferredSize().width + 20, 23);
		caseAfficherLeTimecode.setFont(new Font("Arial", Font.PLAIN, 12));
		caseAfficherLeTimecode.setBackground(new Color(50,50,50));
		frame.getContentPane().add(caseAfficherLeTimecode);
		
		slider = new JSlider();
		slider.setEnabled(false);
		slider.setValue(0);
		slider.setBounds(6, 62, 461, 22);
		frame.getContentPane().add(slider);
		
		lblTimecode = new JLabel("00:00:00:00");
		lblTimecode.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimecode.setFont(new Font("Arial", Font.PLAIN, 30));
		lblTimecode.setForeground(Color.RED);
		lblTimecode.setBounds(6, 93, 461, 22);
		frame.getContentPane().add(lblTimecode);
				
		slider.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent e) {			
				sliderChange = true;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {			
				if (sliderChange && DECKLINK.isRunning)
				{
					//On attends que le slider soit relaché pour faire le changement
					Thread runProcess = new Thread(new Runnable()  {
						@Override
						public void run() {
						try {
							DECKLINK.process.destroy();								
							
							do
								Thread.sleep(100);
							while (DECKLINK.isRunning);					
							
							DecimalFormat tc = new DecimalFormat("00");							
							String h = String.valueOf(tc.format(slider.getValue() / 3600));
							String m = String.valueOf(tc.format(slider.getValue() / 60 % 60));
							String s = String.valueOf(tc.format(slider.getValue() % 60));
							
							sliderValue = slider.getValue();
							playVideo(" -ss " + h + ":" + m + ":" + s + ".0");
						} catch (Exception e){}
						}					
						
					});
					runProcess.start();							
				}
				else if (sliderChange && slider.getValue() == 0)
					{
						if (DECKLINK.isRunning)
							DECKLINK.process.destroy();	
						
						btnLire.setText(Shutter.language.getProperty("btnLire"));
						caseForceInterlace.setEnabled(true);
			      		caseAfficherLeTimecode.setEnabled(true);
			      		caseHD.setEnabled(true);
					}
					
				sliderChange = false;
			}
			
		});
				
		btnLire.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnLire.getText().equals(Shutter.language.getProperty("btnLire")))
				{
					if (DECKLINK.isRunning)
					DECKLINK.process.destroy();		
					
					 do
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}		
					 while (DECKLINK.isRunning);	
					
					if (slider.getValue() == slider.getMaximum())
						slider.setValue(0);
					
					DecimalFormat tc = new DecimalFormat("00");							
					String h = String.valueOf(tc.format(slider.getValue() / 3600));
					String m = String.valueOf(tc.format(slider.getValue() / 60 % 60));
					String s = String.valueOf(tc.format(slider.getValue() % 60));
					
					sliderValue = slider.getValue();
					playVideo(" -ss " + h + ":" + m + ":" + s + ".0");
					
					btnLire.setText(Shutter.language.getProperty("btnArret"));
				}
				else if (btnLire.getText().equals(Shutter.language.getProperty("btnArret")))
				{						
					if (DECKLINK.isRunning)
						DECKLINK.process.destroy();		
					
					 do
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}		
					 while (DECKLINK.isRunning);	
					
					btnLire.setText(Shutter.language.getProperty("btnLire"));
					caseForceInterlace.setEnabled(true);
		      		caseAfficherLeTimecode.setEnabled(true);
		      		caseHD.setEnabled(true);
										
					DecimalFormat tc = new DecimalFormat("00");							
					String h = String.valueOf(tc.format(slider.getValue() / 3600));
					String m = String.valueOf(tc.format(slider.getValue() / 60 % 60));
					String s = String.valueOf(tc.format(slider.getValue() % 60));				
					
					sliderValue = slider.getValue();
					playVideo(" -ss " + h + ":" + m + ":" + s + ".0");					
										
				}
			}
					
		});
		
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				DecimalFormat tc = new DecimalFormat("00");	
				final String h = String.valueOf(tc.format(slider.getValue() / 3600));
				final String m = String.valueOf(tc.format(slider.getValue() / 60 % 60));
				final String s = String.valueOf(tc.format(slider.getValue() % 60));
				
				BlackMagicOutput.lblTimecode.setText(h+":"+m+":"+s+":00");
			}
			
		});
	
	}
	
	private void playVideo(final String position) {	

		if (DECKLINK.isRunning == false)
		{
			
		Thread runProcess = new Thread(new Runnable()  {
			@Override
			public void run() {
			try {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				String file;
				if (Shutter.listeDeFichiers.getSelectedIndices().length == 0)			
					file = Shutter.liste.firstElement();
				else
					file = Shutter.listeDeFichiers.getSelectedValue().toString();
				
				FFPROBE.Data(file);
				
				do {
					Thread.sleep(100);
				} while (FFPROBE.isRunning);
				
				FFPROBE.FrameData(file);
				
				do {
					Thread.sleep(100);
				} while (FFPROBE.isRunning);				
				
				DecimalFormat tc = new DecimalFormat("00");	
				final String h = String.valueOf(tc.format(slider.getValue() / 3600 + timecode1));
				final String m = String.valueOf(tc.format(slider.getValue() / 60 % 60 + timecode2));
				final String s = String.valueOf(tc.format(slider.getValue() % 60 + timecode3));
				final String f = String.valueOf(tc.format(timecode4));
				
				String videoFilter = "";
				if (caseAfficherLeTimecode.isSelected())		
					videoFilter = " -vf " + '"' + "drawtext=fontfile=" + Shutter.pathToFont + ":timecode='"+h+"\\:"+m+"\\:"+s+"\\:"+f+"':r=" + FFPROBE.currentFPS + ":x=(w-tw)*0.5:y=(lh*0.5):fontcolor=white:fontsize=(" + FFPROBE.imageResolution.substring(FFPROBE.imageResolution.lastIndexOf("x") + 1) + "*0.0422):box=1:boxcolor=0x00000099" + '"';		
						
				String format = "";
				if (caseHD.isSelected())
					format = " -s 1920x1080";
				
				String fieldOrder = " -field_order progressive";
				if (caseForceInterlace.isSelected())
					fieldOrder = " -field_order tt";
				else if (FFPROBE.entrelaced.equals("1") && FFPROBE.fieldOrder.equals("0"))
					fieldOrder = " -field_order bt";
				else if (FFPROBE.entrelaced.equals("1"))
					fieldOrder = " -field_order tt";
				
				String audio = " -ar 48000";
				if (FFPROBE.channels > 1)
					audio = " -ar 48000 -filter_complex " + '"' + "amerge=inputs=2,channelmap=0|1:channel_layout=stereo" + '"';
				else if (FFPROBE.channels == 0)
					audio = " -an";
				
				 
				 File stopframe = new File(Shutter.dirTemp + "frame.png");
				 if (stopframe.exists()) stopframe.delete();
				
				 if (btnLire.getText().equals(Shutter.language.getProperty("btnLire")))
				 {
					 Console.consoleFFMPEG.append(Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());
					 
					 btnPrevious.setEnabled(false);
					 btnNext.setEnabled(false);
					 btnLire.setEnabled(false);
					 slider.setEnabled(false);
					 
					 FFMPEG.run(position + " -i " + '"' + file + '"' + videoFilter + " -vframes 1 -an -y " + '"' + stopframe + '"');
					
					 do {
						 Thread.sleep(100);
					 } while (stopframe.exists() == false && FFMPEG.error == false);
				
					 btnPrevious.setEnabled(true);
					 btnNext.setEnabled(true);
					 btnLire.setEnabled(true);
					 slider.setEnabled(true);
					 
					 Shutter.enableAll();
					 
					 DECKLINK.run(" -loop 1 -i " + '"' + stopframe + '"' + format + fieldOrder + " -an -f decklink -pix_fmt uyvy422 " + '"' + DECKLINK.getBlackMagic + '"');
				 }
				 else	
				 {
					DECKLINK.run(position + " -i " + '"' + file + '"' +  videoFilter + format + audio + fieldOrder + " -f decklink -pix_fmt uyvy422 " + '"' + DECKLINK.getBlackMagic + '"');	
				
			  		Shutter.lblTermine.setVisible(false);
			  		Shutter.enableAll();
			  		caseForceInterlace.setEnabled(false);
			  		caseAfficherLeTimecode.setEnabled(false);
			  		caseHD.setEnabled(false);
			  		slider.setEnabled(true);
				 }
		  		
		  		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      		  	
				do
					Thread.sleep(100);
				while (DECKLINK.isRunning);
					
				} catch (InterruptedException e) {}	
				finally {
					if (slider.getValue() == DECKLINK.dureeTotale)
					{
						btnLire.setText(Shutter.language.getProperty("btnLire"));
						caseForceInterlace.setEnabled(true);
			      		caseAfficherLeTimecode.setEnabled(true);
			      		caseHD.setEnabled(true);
			      		
			      		if (Shutter.listeDeFichiers.getSelectedIndex() < Shutter.liste.getSize() - 1)
			      		{
			      			Shutter.listeDeFichiers.setSelectedIndex(Shutter.listeDeFichiers.getSelectedIndex() + 1);
			      			btnLire.doClick();
			      		}
					}
				}
			}
		});
		runProcess.start();
		}
	}

}
