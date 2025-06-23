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
import java.awt.Component;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class Equalizer {

	public static JFrame frame;
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	private JButton btnApply;
	private JButton btnReset;
	
	private static JLabel sliderText1 = new JLabel("60");
	public static JSlider sliderEQ1 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText2 = new JLabel("170");
	public static JSlider sliderEQ2 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText3 = new JLabel("310");
	public static JSlider sliderEQ3 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText4 = new JLabel("600");
	public static JSlider sliderEQ4 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText5 = new JLabel("1K");
	public static JSlider sliderEQ5 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText6 = new JLabel("3K");
	public static JSlider sliderEQ6 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText7 = new JLabel("6K");
	public static JSlider sliderEQ7 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText8 = new JLabel("12K");
	public static JSlider sliderEQ8 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText9 = new JLabel("14K");
	public static JSlider sliderEQ9 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderText10 = new JLabel("16K");
	public static JSlider sliderEQ10 = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static JLabel sliderTextGain = new JLabel("0dB");
	public static JSlider sliderGain = new JSlider(JSlider.VERTICAL,-10,10,0);
	
	private static int MousePositionX;
	private static int MousePositionY;

	public Equalizer() {	
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Utils.bg32);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameEqualizer"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(355, 240);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);	
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45,45,45)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);
		}		
		
		topPanel();
		addEQ();
				
		btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		btnReset.setBounds(7, frame.getHeight() - 21 - 7, frame.getWidth() / 2 - 7, 21);		
		frame.getContentPane().add(btnReset);	
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (Component c : frame.getContentPane().getComponents())
				{			
					if (c instanceof JSlider)
					{
						((JSlider) c).setValue(0);
					}
				}
				
				VideoPlayer.playerAudioSetTime(VideoPlayer.playerCurrentFrame);
			}
			
		});
		
		btnApply = new JButton(Shutter.language.getProperty("btnApply"));
		btnApply.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		btnApply.setBounds(btnReset.getX() + btnReset.getWidth() + 4, btnReset.getY(), frame.getWidth() / 2 - 7 - 4, 21);
		frame.getContentPane().add(btnApply);
		
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
								
				frame.dispose();
			}
			
		});
				
		Utils.changeFrameVisibility(frame, false);	
	}

	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 1000, 28);
		
		quit.addMouseListener(new MouseListener(){

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
					Shutter.caseEqualizer.setSelected(false);
		            
		            Utils.changeFrameVisibility(frame, true);
	            	VideoPlayer.playerAudioSetTime(VideoPlayer.playerCurrentFrame);
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("frameEqualizer"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45,45,45)));		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);
		topPanel.setBounds(0, 0, 1000, 24);
		frame.getContentPane().add(topPanel);
		
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
		
	}	

	private void addEQ() {

		frame.getContentPane().add(sliderText1);
		frame.getContentPane().add(sliderEQ1);
		sliderEQ1.setName("sliderEQ1");
		
		frame.getContentPane().add(sliderText2);
		frame.getContentPane().add(sliderEQ2);
		sliderEQ2.setName("sliderEQ2");
		
		frame.getContentPane().add(sliderText3);
		frame.getContentPane().add(sliderEQ3);
		sliderEQ3.setName("sliderEQ3");
		
		frame.getContentPane().add(sliderText4);
		frame.getContentPane().add(sliderEQ4);
		sliderEQ4.setName("sliderEQ4");
		
		frame.getContentPane().add(sliderText5);
		frame.getContentPane().add(sliderEQ5);
		sliderEQ5.setName("sliderEQ5");
		
		frame.getContentPane().add(sliderText6);
		frame.getContentPane().add(sliderEQ6);
		sliderEQ6.setName("sliderEQ6");
		
		frame.getContentPane().add(sliderText7);
		frame.getContentPane().add(sliderEQ7);
		sliderEQ7.setName("sliderEQ7");
		
		frame.getContentPane().add(sliderText8);
		frame.getContentPane().add(sliderEQ8);
		sliderEQ8.setName("sliderEQ8");
		
		frame.getContentPane().add(sliderText9);
		frame.getContentPane().add(sliderEQ9);
		sliderEQ9.setName("sliderEQ9");
		
		frame.getContentPane().add(sliderText10);
		frame.getContentPane().add(sliderEQ10);
		sliderEQ10.setName("sliderEQ10");
		
		frame.getContentPane().add(sliderTextGain);
		sliderTextGain.setName("sliderTextGain");
		frame.getContentPane().add(sliderGain);
		sliderGain.setName("sliderGain");
		
		sliderGain.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				sliderTextGain.setText(sliderGain.getValue() + "dB");				
			}
			
		});
		
		int labelX = 7;		
		for (Component c : frame.getContentPane().getComponents())
		{			
			if (c instanceof JLabel)
			{
				c.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
				((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
				
				if (c.getName() != null && c.getName().equals("sliderTextGain"))
				{
					c.setBounds(labelX + 6, topPanel.getHeight() + 7 + 150 + 7, 38, 16);
				}
				else
					c.setBounds(labelX, topPanel.getHeight() + 7 + 150 + 7, 24, 16);
				
				frame.getContentPane().add(c);	
			}
			else if (c instanceof JSlider)
			{
				if (c.getName() != null && c.getName().equals("sliderGain"))
				{
					c.setBounds(labelX + 15, topPanel.getHeight() + 7, 24, 150);
				}
				else
					c.setBounds(labelX, topPanel.getHeight() + 7, 24, 150);
				
				frame.getContentPane().add(c);
				
				c.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {	
						
						if (e.getClickCount() == 2)
						{
							((JSlider) c).setValue(0);
						}
					}
					
					@Override
					public void mouseReleased(MouseEvent e) {
						VideoPlayer.playerAudioSetTime(VideoPlayer.playerCurrentFrame);
					}
				});
				
				/*
				((JSlider) c).addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent arg0) {
						VideoPlayer.playerAudioSetTime(VideoPlayer.playerCurrentFrame);						
					}
					
				});*/

				labelX += 30;
			}
		}	
	}

}
