/*******************************************************************************************
* Copyright (C) 2025 PACIFICO PAUL
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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class Donate {

	private static int MousePositionX;
	private static int MousePositionY;
	
	public Donate()  {
		
		JFrame frame = new JFrame();
		frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.setTitle("Thanks!");		
		frame.setBackground(new Color(30,30,35));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(280, 305);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100, 100, 100)));
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	    Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	    shape1.add(shape2);
		frame.setShape(shape1);
		frame.setAlwaysOnTop(true);
		
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, frame.getHeight() + (Shutter.frame.getHeight() / 2 - frame.getHeight()));
				
		JPanel topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setBounds(0, 0, frame.getWidth(), 28);
		
		JLabel quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);	
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		
		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				quit.setIcon(new FlatSVGIcon("contents/quit_pressed.svg", 15, 15));				
				accept = true;
			}
		 	
			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (accept)
				{					
					System.exit(0);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("contents/quit_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("contents/quit.svg", 15, 15));
				accept = false;
			}

		});
		
		JLabel title = new JLabel(frame.getTitle());
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		JLabel topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 24);		
		topPanel.add(topImage);
		
		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePositionX = down.getPoint().x;
				MousePositionY = down.getPoint().y;					
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
		
		topImage.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
				
		frame.getContentPane().add(topPanel);
		
		JLabel qrcode = new JLabel();
		qrcode.setIcon(new ImageIcon(getClass().getClassLoader().getResource("contents/qrcode.png")));
		qrcode.setHorizontalAlignment(SwingConstants.CENTER);	
		qrcode.setBounds((frame.getWidth() - 250) / 2, topPanel.getHeight() + 10, 250, 250);
		frame.getContentPane().add(qrcode);
		
		qrcode.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("https://donate.stripe.com/28o29m0QwfRZ4U0cMM"));
				} catch (IOException | URISyntaxException e) {}
				
				System.exit(0);
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
		
		frame.setVisible(true);
	}
}
