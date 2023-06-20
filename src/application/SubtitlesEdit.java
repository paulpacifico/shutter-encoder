/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class SubtitlesEdit {

	public static JFrame frame;
	public static int textPosition;
	private static JScrollBar scrollBar;
	private static int lastScrollBarValue;
	private static int scrollValue = 0;
	private static long keyboardTime;
	private static boolean keyboardLoop = false;
	public static boolean isWriting = true;
	private boolean drag = false;
	private static boolean refreshSubs = false;
	
	public SubtitlesEdit() {	
		
		textPosition = 12;
		
		frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setSize(620, 640);
		frame.setAlwaysOnTop(true);
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		frame.getContentPane().setBackground(new Color(45, 45, 45));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameSubtitlesEdit"));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);	
		
		scrollBar = new JScrollBar();
		scrollBar.setBackground(new Color(45, 45, 45));
		scrollBar.setOrientation(JScrollBar.VERTICAL);
						
		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				if (scrollBar.isVisible())
				{
					scrollBar.setValue(scrollBar.getValue() + e.getWheelRotation() * 10);				
				}
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
		
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				int i = 0;
				int posY = 0;
				
				for (Component c : frame.getContentPane().getComponents())
				{
					if (c instanceof JTextPane)
					{
						i = c.getY() + c.getHeight() + 60 - frame.getHeight();
						posY = c.getY() + c.getHeight() + 36;
					}
				}
				
				if (i > 0)
				{					
					scrollBar.setVisible(true);	
					scrollBar.setMaximum(i);
					frame.getContentPane().add(scrollBar);
				}
				else
				{
					scrollBar.setVisible(false);
					frame.getContentPane().remove(scrollBar);
				}
								
				if (e.getY() > posY)
				{
					drag = false;
				}
				else
					drag = true;
				
				if (drag && frame.getSize().height > 90)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);		
			                
			    	if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			    	{
			    		scrollBar.setBounds(frame.getWidth() - 17, 0, 17, frame.getHeight() - 35);
			    	}
					else
						scrollBar.setBounds(frame.getWidth() - 34, 0, 17, frame.getHeight() - 40);
		       	}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height - 20)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				}
				else 
				{
					if (drag == false)
					{
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}				
			
		});
		
		frame.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
				{
					drag = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				
				drag = false;
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
				if (frame.getSize().height <= 90)
				{
					frame.setSize(frame.getSize().width, 100);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
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
		frame.repaint();			

	}
	
	private static JTextPane addText(String subContent) {
		
		JTextPane text = new JTextPane();
		text.setText(subContent);
		text.setCaretColor(Color.BLACK);
		text.setBounds(180, textPosition, 400, 48);
		textPosition += 60; 
		
		text.addKeyListener(new KeyAdapter()
    	{

			@Override
			public void keyPressed(KeyEvent e) {

				keyboardTime = System.currentTimeMillis();
				
				boolean control = false;
				boolean shift = false;
				
				if ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)
				{								        
					control = true;
					
					if (e.getKeyCode() == KeyEvent.VK_SHIFT)
						shift = true;	
				}
				
				if (control)
				{													        
			        if (e.getKeyCode() == KeyEvent.VK_Z && shift == false) 
			        {
			        	SubtitlesTimeline.loadBackupSubtitles();		
			        }
			        else if (e.getKeyCode() == KeyEvent.VK_Z && shift || e.getKeyCode() == KeyEvent.VK_Y)
			        {
			        	SubtitlesTimeline.loadRestoreSubtitles();	
			        }							
				}	
				
				text.requestFocus();
								
			}

			@Override
			public void keyReleased(KeyEvent e) {
		
				keyboardSaveTime();	
			}
			
			private void keyboardSaveTime() {
				
				if (keyboardLoop == false)
				{
					Thread t = new Thread(new Runnable() {
						
						public void run() 
						{
							do {
								keyboardLoop = true;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while ((keyboardTime + 1000) > System.currentTimeMillis());
							
							updateTimeline();
							
							keyboardLoop = false;
						}					
					});
					t.start();	
				}
			}
    		
    	});
		
		text.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
				SubtitlesEdit.isWriting = true;		
				
				//Current sub number
				int selectedSub = 0;
				for (Component c : frame.getContentPane().getComponents())
				{
					if (c instanceof JTextPane)
					{
						selectedSub ++;
						
						if (c.getY() == text.getY())
						{
							break;
						}
					}
				}

				//Timline Cursor position
				int currentSubtitle = 0;
				for (Component c : SubtitlesTimeline.timeline.getComponents())
				{
					if (c instanceof JTextPane)
					{
						currentSubtitle ++;
						
						if (currentSubtitle == selectedSub)
						{		
							SubtitlesTimeline.cursor.setLocation(c.getX(), SubtitlesTimeline.cursor.getY());
							VideoPlayer.sliderChange = true;	

							if (c.getX() <= 0)
							{
								SubtitlesTimeline.cursor.setLocation(0, SubtitlesTimeline.cursor.getLocation().y);
								VideoPlayer.slider.setValue(0);	
							}
							else
							{
								SubtitlesTimeline.cursor.setLocation(c.getX(), SubtitlesTimeline.cursor.getLocation().y);
								VideoPlayer.slider.setValue((int) ((c.getX())/SubtitlesTimeline.zoom/VideoPlayer.inputFramerateMS));	
							}
							
							VideoPlayer.sliderChange = false;						

							//Then refresh the slider position
							VideoPlayer.getTimePoint(VideoPlayer.playerCurrentFrame - 1);
							
							break;
						}
					}
				}				
			}
			
		});
		
	    return text;
	}
	
	private static JTextField addInPoint(String subIn) {
		
		JTextField in = new JTextField();
		in.setText(subIn);
		in.setBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true));
		in.setForeground(Utils.themeColor);
		in.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		in.setBounds(77, textPosition - 2, 94, 25);	
		in.setBackground(new Color(45, 45, 45));
		in.setHorizontalAlignment(SwingConstants.CENTER);
			   	
		in.addKeyListener(new KeyAdapter()
    	{

			@Override
			public void keyPressed(KeyEvent e) {

				keyboardTime = System.currentTimeMillis();
								
			}

			@Override
			public void keyReleased(KeyEvent e) {
		
				keyboardSaveTime();	
			}
			
			private void keyboardSaveTime() {
				
				if (keyboardLoop == false)
				{
					Thread t = new Thread(new Runnable() {
						
						public void run() 
						{
							do {
								keyboardLoop = true;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while ((keyboardTime + 1000) > System.currentTimeMillis());
							
							updateTimeline();
							
							keyboardLoop = false;
						}					
					});
					t.start();	
				}
			}
    		
    	});
    			
		in.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
				SubtitlesEdit.isWriting = true;				
			}
			
		});
		
	    return in;
	}
	
	private static JTextField addOutPoint(String subOut) {
		
		JTextField out = new JTextField();
		out.setText(subOut);
		out.setBorder(new RoundedLineBorder(new Color(65, 65, 65), 1, 5, true));
		out.setForeground(Utils.themeColor);
		out.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		out.setBounds(77, textPosition + 24, 94, 25);	
		out.setBackground(new Color(45, 45, 45));
		out.setHorizontalAlignment(SwingConstants.CENTER);
		
		out.addKeyListener(new KeyAdapter()
    	{

			@Override
			public void keyPressed(KeyEvent e) {

				keyboardTime = System.currentTimeMillis();
								
			}

			@Override
			public void keyReleased(KeyEvent e) {
		
				keyboardSaveTime();	
			}
			
			private void keyboardSaveTime() {
				
				if (keyboardLoop == false)
				{
					Thread t = new Thread(new Runnable() {
						
						public void run() 
						{
							do {
								keyboardLoop = true;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {}
							} while ((keyboardTime + 1000) > System.currentTimeMillis());
							
							updateTimeline();
							
							keyboardLoop = false;
						}					
					});
					t.start();	
				}
			}
    		
    	});
		
		out.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
				SubtitlesEdit.isWriting = true;				
			}
			
		});
		
	    return out;
	}
	
	private static JLabel addNumber(int number) {
		
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
					//Removes UTF-8 with BOM
	            	line = line.replace("\uFEFF", "");
					
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
						{
							frame.getContentPane().add(addText(subContent.toString().substring(0, subContent.length() - 1)));	
						}
						else
							frame.getContentPane().add(addText(subContent.toString().substring(0, subContent.length() - 2)));
					}
				}			
			}					
		}
		catch (Exception e) {}
		finally 
		{
			try {
				reader.close();
			} catch (IOException e) {}	
			
			frame.repaint();
			frame.getContentPane().repaint();
		}			
	}
	
	public static void refreshSubtitles() {
				
		if (SubtitlesEdit.frame != null && SubtitlesEdit.frame.isVisible() && VideoPlayer.playerIsPlaying() == false && VideoPlayer.sliderChange == false && refreshSubs == false)		
		{
			refreshSubs = true;
			
			Thread refresh = new Thread(new Runnable() {
		
				@Override
				public void run() {
		
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JScrollBar == false)
						{
							frame.remove(c);
						}
					}		
										
					//IMPORTANT
					textPosition = 12;
					scrollBar.setValue(0);
							
					//Add subs
					addSubtitles();
					
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					{
						scrollBar.setBounds(frame.getWidth() - 17, 0, 17, frame.getHeight() - 35);
					}
					else
						scrollBar.setBounds(frame.getWidth() - 34, 0, 17, frame.getHeight() - 40);		
					
					int i = 0;
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JTextPane)
						{
							i = c.getY() + c.getHeight() + 60 - frame.getHeight();
						}
					}
									
					if (i > 0)
					{
						scrollBar.setVisible(true);						
						scrollBar.setMaximum(i);
						frame.getContentPane().add(scrollBar);
						
						int currentSubtitle = 0;
						boolean subExists = false;
						for (Component c : SubtitlesTimeline.timeline.getComponents())
						{
							if (c instanceof JTextPane)
							{
								currentSubtitle ++;
								
								if (SubtitlesTimeline.cursor.getLocation().x >= c.getLocation().x && SubtitlesTimeline.cursor.getLocation().x < (c.getLocation().x + c.getWidth()))
								{				
									subExists = true;
									
									break;
								}
							}
						}
												
						if (subExists)
						{
							//Get the currentSub position
							int currentText = 0;
							int currentTextPosY = 0;
							for (Component c : frame.getContentPane().getComponents())
							{
								if (c instanceof JTextPane)
								{
									currentText ++;
									
									if (currentText == currentSubtitle)
									{
										currentTextPosY = c.getY() - 12;
										break;
									}
								}
							}
							
							//Set the ScrollBarValue
							if (currentTextPosY < scrollBar.getMaximum())
							{
								scrollBar.setValue(currentTextPosY);
							}
							else
								scrollBar.setValue(scrollBar.getMaximum());	
							
							lastScrollBarValue = scrollBar.getValue();
						}
						else
							scrollBar.setValue(lastScrollBarValue);
					}
					else
					{
						scrollBar.setVisible(false);		
						frame.getContentPane().remove(scrollBar);
					}
					
					frame.repaint();
					
					refreshSubs = false;		
				}		
				
			});
			
			refresh.start();
		}
	}
	
	private static void updateTimeline() {
				
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
						//Number
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
		}
		catch (IOException e1) {}
		finally
		{
			try {
				writer.close();
			}
			catch (IOException e1) {}
			finally {
				
				SubtitlesTimeline.timeline.removeAll();
				SubtitlesTimeline.setSubtitles(SubtitlesTimeline.srt);
			}
		}
	}
	
}
