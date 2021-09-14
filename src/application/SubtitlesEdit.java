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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SubtitlesEdit {

	private static JDialog frame;
	private final JButton btnApply = new JButton(Shutter.language.getProperty("btnApply"));
	private final JButton btnCancel = new JButton(Shutter.language.getProperty("btnCancel"));
	
	static int textPosition;
	private JScrollBar scrollBar;
	int scrollValue = 0;
	private final JPanel panelHide; 
	
	public SubtitlesEdit(final int numberOfSubs) {		
		textPosition = 12;
		
		frame = new JDialog();
		frame.setModal(true);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setSize(620, 670);
		frame.setAlwaysOnTop(true);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameSubtitlesEdit"));
				
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);		
						
		
		btnCancel.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));	
		btnCancel.setBounds(6, btnApply.getY(), frame.getWidth() / 2 - 12, 21);	
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
		{
			btnCancel.setLocation(btnCancel.getX(), frame.getHeight() - 54);
		}
		frame.getContentPane().add(btnCancel);		
		
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		
		btnApply.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));	
		btnApply.setBounds(frame.getWidth() / 2 + 6, btnCancel.getY(), btnCancel.getWidth(), 21);				
		frame.getContentPane().add(btnApply);
		
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {		
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				BufferedWriter writer = null;
				
				try {
					writer = Files.newBufferedWriter(Paths.get(SubtitlesTimeline.srt.toString()),  StandardCharsets.UTF_8);
					boolean isInPoint = true;
					for (Component c : frame.getContentPane().getComponents())
					{	
						if (c instanceof JLabel)
						{
							if (((JLabel) c).getText().matches("[0-9]+"))
							{
								//Numéro
								if (((JLabel) c).getText().equals("1"))
									writer.write(((JLabel) c).getText() + System.lineSeparator());
								else
									writer.write(System.lineSeparator()  + System.lineSeparator() + ((JLabel) c).getText() + System.lineSeparator());
							}							
						}
						else if (c instanceof JTextField)
						{
							if (isInPoint)
							{
								writer.write(((JTextField) c).getText() + " --> ");
								isInPoint = false;
							}
							else
							{
								writer.write(((JTextField) c).getText() + System.lineSeparator() );
								isInPoint = true;
							}
						}							
						else if (c instanceof JTextPane)
								writer.write(((JTextPane) c).getText());	
					}
				} catch (IOException e1) {}
				finally {
					try {
						writer.close();
					} catch (IOException e1) {}
					finally {
						SubtitlesTimeline.timeline.removeAll();
						SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);
						frame.dispose();
					}
					
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			
		});
		
		panelHide = new JPanel();
		panelHide.setBounds(0, 610, frame.getWidth(), 38);	
		panelHide.setBackground(new Color(50,50,50));
		frame.getContentPane().add(panelHide);
				
		//Ajout des subs
		addSubtitles();
				
		scrollBar = new JScrollBar();
		scrollBar.setBackground(new Color(50,50,50));
		scrollBar.setOrientation(JScrollBar.VERTICAL);
		if (numberOfSubs > 10)
			scrollBar.setVisible(true);
		else
			scrollBar.setVisible(false);
		
		scrollBar.setMaximum(60 * (numberOfSubs + 1) - 648);
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			scrollBar.setBounds(frame.getWidth() - 17, 0, 17, frame.getHeight() - 60);
		else
			scrollBar.setBounds(frame.getWidth() - 34, 0, 17, frame.getHeight() - 65);
		
		frame.getContentPane().add(scrollBar);
				
		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollBar.setValue(scrollBar.getValue() + e.getWheelRotation() * 10);				
			}
			
		});	
		
		frame.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {				
			}

			@Override
			public void windowClosing(WindowEvent e) {				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);				
			}

			@Override
			public void windowIconified(WindowEvent e) {				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {				
			}

			@Override
			public void windowActivated(WindowEvent e) {				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {				
			}
			
		});
		
		scrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {
					int scrollIncrement = scrollBar.getValue() - scrollValue;
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JButton == false && c instanceof JScrollBar == false && c instanceof JPanel == false)
						{
								c.setLocation(c.getLocation().x, c.getLocation().y - scrollIncrement);
						}
					}
					scrollValue = scrollBar.getValue();
		      }			
			
		});
		
		frame.setVisible(true);
	}
	
	private static JTextPane addText(String subContent){
		JTextPane text = new JTextPane();
		text.setText(subContent);
		text.setCaretColor(Color.BLACK);
		text.setBounds(180, textPosition, 400, 48);
		textPosition += 60; 	
	    return text;
	}
	
	private static JTextField addInPoint(String subIn){
		JTextField in = new JTextField();
		in.setText(subIn);
		in.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));
		in.setForeground(Utils.themeColor);
		in.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		in.setBounds(77, textPosition - 2, 94, 25);	
		in.setBackground(new Color(50,50,50));
		in.setHorizontalAlignment(SwingConstants.CENTER);
	    return in;
	}
	
	private static JTextField addOutPoint(String subOut){
		JTextField out = new JTextField();
		out.setText(subOut);
		out.setBorder(new RoundedLineBorder(new Color(70,70,70), 1, 5, true));
		out.setForeground(Utils.themeColor);
		out.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		out.setBounds(77, textPosition + 24, 94, 25);	
		out.setBackground(new Color(50,50,50));
		out.setHorizontalAlignment(SwingConstants.CENTER);
	    return out;
	}
	
	private static JLabel addNumber(int number){
		JLabel nb = new JLabel(String.valueOf(number));
		nb.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		nb.setHorizontalAlignment(SwingConstants.CENTER);
		nb.setBounds(9, textPosition + 16, 61, 16);
	    return nb;
	}

	private static void addSubtitles() {		
		BufferedReader reader = null;		
		
		try {
			if (SubtitlesTimeline.srt.exists())
			{
				reader = Files.newBufferedReader(Paths.get(SubtitlesTimeline.srt.toString()),  StandardCharsets.UTF_8);
				
				String line;					
				while((line = reader.readLine()) != null)
				{		
					
					if (line.matches("[0-9]+"))
					{
						frame.getContentPane().add(addNumber(Integer.valueOf(line)));
					}
					else if (line.isEmpty() == false)
					{
						String[] s = line.split(" ");						
						frame.getContentPane().add(addInPoint(s[0]));
						frame.getContentPane().add(addOutPoint(s[2]));	
						
						StringBuilder subContent = new StringBuilder();
						
						 while ((line = reader.readLine()) != null && line.isEmpty() == false)
						 {
							subContent.append(line + System.lineSeparator());						
						 }
						
							if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
								frame.getContentPane().add(addText(subContent.toString().substring(0, subContent.length() - 1)));	
							else
								frame.getContentPane().add(addText(subContent.toString().substring(0, subContent.length() - 2)));
					}
				}			
			}					
			} catch (Exception e) {}
			finally 
			{
				try {
					reader.close();
				} catch (IOException e) {}	
			}
			
	}
}
