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

package shutterencoder.ui.others;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

import shutterencoder.library.FFprobe;
import shutterencoder.ui.main.Main;
import shutterencoder.utils.Utils;

public class GOP {

	public static JDialog frame;
	private JScrollBar scrollBar;

	public GOP() {
		
		frame = new JDialog();
		frame.setResizable(false);		
		frame.setModal(false);
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Utils.background);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		if (System.getProperty("os.name").contains("Mac") == false)
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/icon.png")).getImage());			
				
		scrollBar = new JScrollBar();
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.setVisible(true);
		scrollBar.setBackground(Utils.c30);
		frame.getContentPane().add(scrollBar);
		
		scrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				if (scrollBar.getValueIsAdjusting())
				{
					int i = 12;
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JLabel)
						{
							c.setLocation(i - scrollBar.getValue(), 12);
							i += 112;
						}
					}
				}
		      }			
			
		});
		
		JLabel image1 = new JLabel("I");
		image1.setFont(new Font(Main.mainFont, Font.PLAIN, 40));
		image1.setHorizontalAlignment(SwingConstants.CENTER);
		image1.setBackground(new Color(232,67,67));
		image1.setOpaque(true);
		image1.setForeground(Color.BLACK);
		image1.setBounds(12, 12, 100, 100);
		frame.getContentPane().add(image1);
		
		File fichier = new File(Main.fileList.getSelectedValue());
		
		FFprobe.AnalyzeGOP(fichier.toString(), true);
		try {
			FFprobe.processGOP.join();
		} catch (InterruptedException er) {
		    Thread.currentThread().interrupt();
		}
		
		frame.setTitle(Main.language.getProperty("analyzeOf") + " " + fichier.getName() + " | GOP " + Main.language.getProperty("of") + " " + FFprobe.gopCount);
		
		if (FFprobe.gopCount <= 12)
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				frame.setSize(FFprobe.gopSpace + 8, 154);
			else
				frame.setSize(FFprobe.gopSpace + 8 + 10, 164);
			
			scrollBar.setVisible(false);
		}
		else if (FFprobe.gopSpace + 8 > 1476)
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				frame.setSize(1476, 171);
			else
				frame.setSize(1486, 181);
			
			scrollBar.setVisible(true);
		}
		else
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				frame.setSize(FFprobe.gopSpace + 8, 171);
			else
				frame.setSize(FFprobe.gopSpace + 8 + 10, 181);
			scrollBar.setVisible(true);
		}
		
		frame.setLocation(Main.frame.getLocation().x + (Main.frame.getSize().width / 2) - (frame.getSize().width / 2), Main.frame.getLocation().y + 150);
		
		scrollBar.setBounds(0, 125, frame.getSize().width - 6, 17);
		scrollBar.setMaximum((112 * (FFprobe.gopCount - 12) + 8)); 
		
		frame.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				frame.getContentPane().removeAll();
			}

			@Override
			public void windowClosing(WindowEvent arg0) {

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			
		});
		
		frame.setVisible(true);
	}
	
	public static void newImage(char type, int gopSpace)
	{
		JLabel image;
		if (type == 'I')
		{
			image = new JLabel("I");
			image.setFont(new Font(Main.mainFont, Font.PLAIN, 40));
			image.setHorizontalAlignment(SwingConstants.CENTER);
			image.setForeground(Color.BLACK);
			image.setBackground(new Color(232,67,67));
			image.setOpaque(true);
		}
		else if (type == 'P')
		{
			image = new JLabel("P");
			image.setFont(new Font(Main.mainFont, Font.PLAIN, 40));
			image.setHorizontalAlignment(SwingConstants.CENTER);
			image.setForeground(Color.BLACK);
			image.setBackground(new Color(68,139,233));
			image.setOpaque(true);
		}
		else
		{
			image = new JLabel("B");
			image.setFont(new Font(Main.mainFont, Font.PLAIN, 40));
			image.setHorizontalAlignment(SwingConstants.CENTER);
			image.setForeground(Color.BLACK);
			image.setBackground(new Color(100,232,67));
			image.setOpaque(true);
		}
	    image.setSize(100, 100);
	    image.setLocation(gopSpace, 12);
	    frame.getContentPane().add(image);
	}
}
