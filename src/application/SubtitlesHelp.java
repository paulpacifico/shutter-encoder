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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextPane;

import java.awt.*;

public class SubtitlesHelp {

	public static JFrame frame;

	public SubtitlesHelp() {
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setTitle(Shutter.language.getProperty("frameSubtitles"));
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		frame.setLocation(VideoPlayer.frame.getLocation().x + VideoPlayer.frame.getSize().width/2 - 200, VideoPlayer.frame.getLocation().y + VideoPlayer.frame.getHeight() /2 - 150);
				
		JTextPane lblHelp = new JTextPane();
		lblHelp.setBackground(new Color(50,50,50));
		lblHelp.setText(Shutter.language.getProperty("txtShortcuts"));
		lblHelp.setForeground(Color.WHITE);
		lblHelp.setHighlighter(null);
		lblHelp.setEditable(false);
		lblHelp.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		lblHelp.setBounds(10, 12, 420, 290);
    	frame.getContentPane().add(lblHelp);	
		
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			frame.setSize(lblHelp.getWidth() + 10, lblHelp.getHeight() - 10);
		else
			frame.setSize(lblHelp.getWidth() + 10, lblHelp.getHeight() + 10);
    	
		frame.setVisible(true);
		
	}
}
