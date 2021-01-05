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

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Console extends JFrame {

	public static JFrame frmConsole;
	public static JTextArea consoleFFMPEG =  new JTextArea();
	public static JTextArea consoleFFPLAY =  new JTextArea();
	public static JTextArea consoleFFPROBE =  new JTextArea();
	public static JTextArea consoleBMXTRANSWRAP =  new JTextArea();	
	public static JTextArea consoleDVDAUTHOR =  new JTextArea();
	public static JTextArea consoleTSMUXER =  new JTextArea();
	public static JTextArea consoleMEDIAINFO =  new JTextArea();
	public static JTextArea consoleYOUTUBEDL =  new JTextArea();
	public static JTextArea consoleEXIFTOOL =  new JTextArea();
	public static JTextArea consoleMKVMERGE =  new JTextArea();
	public static JTabbedPane tabbedPane;
	private JScrollPane scrollFFMPEG;
	private JScrollPane scrollFFPLAY;
	private JScrollPane scrollFFPROBE;
	private JScrollPane scrollBMXTRANSWRAP;
	private JScrollPane scrollDVDAUTHOR;
	private JScrollPane scrollTSMUXER;
	private JScrollPane scrollMEDIAINFO;
	private JScrollPane scrollYOUTUBEDL;
	private JScrollPane scrollEXIFTOOL;
	private JScrollPane scrollMKVMERGE;
	private JMenuBar menuBar;
	private final JSpinner spinner;
	private JCheckBoxMenuItem followLine;
	

	public Console() {	
		frmConsole = new JFrame();
		frmConsole.setTitle("Console");
		frmConsole.setLayout(null);
		frmConsole.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frmConsole.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmConsole.setSize(500, Shutter.frame.getHeight());
		frmConsole.setLocation(Shutter.frame.getLocation().x + Shutter.frame.getSize().width + 20, Shutter.frame.getLocation().y);			
		frmConsole.getContentPane().setLayout(null);
		
		System.setProperty("apple.laf.useScreenMenuBar", "false");
		
		JMenu menu = new JMenu("Console");
		menu.setLayout(null);
		menu.setFont(new Font("Montserrat", Font.PLAIN, 12));
		menu.setMnemonic(KeyEvent.VK_ALT);

		menuBar = new JMenuBar();
		menuBar.setLayout(null);
		menuBar.add(menu);
		
		JMenuItem clear = new JMenuItem(Shutter.language.getProperty("btnEmptyList"), KeyEvent.VK_V);
		clear.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {				
				switch (tabbedPane.getSelectedIndex())
				{
					case 0:
						consoleFFMPEG.setText("");
						break;
					case 1:
						consoleFFPLAY.setText("");
						break;
					case 2:
						consoleFFPROBE.setText("");
						break;
					case 3:
						consoleBMXTRANSWRAP.setText("");
						break;
					case 4:
						consoleDVDAUTHOR.setText("");
						break;
					case 5: 
						consoleTSMUXER.setText("");
						break;
					case 6:
						consoleMEDIAINFO.setText("");
						break;
					case 7:
						consoleYOUTUBEDL.setText("");
						break;
					case 8:
						consoleEXIFTOOL.setText("");
						break;
					case 9:
						consoleMKVMERGE.setText("");
						break;
				}
			}
			
		});
		
		JMenuItem save = new JMenuItem(Shutter.language.getProperty("btnSave"), KeyEvent.VK_S);
		save.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog dialog = new FileDialog(frmConsole, Shutter.language.getProperty("saveConsole"), FileDialog.SAVE);
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
				else
					dialog.setDirectory(System.getProperty("user.home") + "\\Desktop");
				dialog.setLocation(frmConsole.getLocation().x - 50, frmConsole.getLocation().y + 50);
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);

				if (dialog.getFile() != null)
				 { 
					try {
						PrintWriter writer = new PrintWriter(dialog.getDirectory() + dialog.getFile().replace(".txt", "") + ".txt", "UTF-8");
							switch (tabbedPane.getSelectedIndex())
							{
								case 0:
									writer.write(consoleFFMPEG.getText());
									break;
								case 1:
									writer.write(consoleFFPLAY.getText());
									break;
								case 2:
									writer.write(consoleFFPROBE.getText());
									break;
								case 3:
									writer.write(consoleBMXTRANSWRAP.getText());
									break;
								case 4:
									writer.write(consoleDVDAUTHOR.getText());
									break;
								case 5: 
									writer.write(consoleTSMUXER.getText());
									break;
								case 6:
									writer.write(consoleMEDIAINFO.getText());
									break;
								case 7:
									writer.write(consoleYOUTUBEDL.getText());
									break;
								case 8:
									writer.write(consoleEXIFTOOL.getText());
									break;
								case 9:
									writer.write(consoleMKVMERGE.getText());
									break;
							}
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException er) {}
				 }
			}
			
		});
		
		followLine = new JCheckBoxMenuItem(Shutter.language.getProperty("followLine"));		
		menu.add(clear);
		menu.add(save);
		menu.add(followLine);								

		frmConsole.setJMenuBar(menuBar);
		
		spinner = new JSpinner( new SpinnerNumberModel( 12,1,100,1 ));	
		spinner.setFont(new Font("Montserrat", Font.PLAIN, 12));				
		menuBar.add(spinner);
		
		consoleAll();
				
		frmConsole.setVisible(true);
		
		frmConsole.addComponentListener(new ComponentAdapter(){

			public void componentResized(ComponentEvent e) {
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))					
						tabbedPane.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 49);
					else			
						tabbedPane.setBounds(0, 0, frmConsole.getSize().width - 15, frmConsole.getSize().height - 64);
						
			}

			
		});
		
	}

	private void consoleAll() {
		
		KeyListener kl = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					followLine.setSelected(false);
			}			
		};
		
		spinner.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				consoleFFMPEG.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));			
				consoleFFPLAY.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleFFPROBE.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleBMXTRANSWRAP.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleDVDAUTHOR.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleTSMUXER.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleMEDIAINFO.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleYOUTUBEDL.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));	
				consoleEXIFTOOL.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));
				consoleMKVMERGE.setFont(new Font("FreeSans", Font.PLAIN, (int) spinner.getValue()));
			}
			
		});
		
		consoleFFMPEG.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleFFMPEG.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleFFMPEG.setWrapStyleWord(true);
		consoleFFMPEG.addKeyListener(kl);
		consoleFFPLAY.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleFFPLAY.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleFFPLAY.setWrapStyleWord(true);
		consoleFFPLAY.addKeyListener(kl);
		consoleFFPROBE.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleFFPROBE.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleFFPROBE.setWrapStyleWord(true);
		consoleFFPROBE.addKeyListener(kl);
		consoleBMXTRANSWRAP.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleBMXTRANSWRAP.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleBMXTRANSWRAP.setWrapStyleWord(true);
		consoleBMXTRANSWRAP.addKeyListener(kl);
		consoleDVDAUTHOR.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleDVDAUTHOR.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleDVDAUTHOR.setWrapStyleWord(true);
		consoleDVDAUTHOR.addKeyListener(kl);
		consoleTSMUXER.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleTSMUXER.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleTSMUXER.setWrapStyleWord(true);
		consoleTSMUXER.addKeyListener(kl);
		consoleMEDIAINFO.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleMEDIAINFO.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleMEDIAINFO.setWrapStyleWord(true);
		consoleMEDIAINFO.addKeyListener(kl);
		consoleYOUTUBEDL.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleYOUTUBEDL.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleYOUTUBEDL.setWrapStyleWord(true);
		consoleYOUTUBEDL.addKeyListener(kl);
		consoleEXIFTOOL.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleEXIFTOOL.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleEXIFTOOL.setWrapStyleWord(true);
		consoleEXIFTOOL.addKeyListener(kl);
		consoleMKVMERGE.setFont(new Font("FreeSans", Font.PLAIN, 12));	
		consoleMKVMERGE.setBounds(0, 0, frmConsole.getSize().width, frmConsole.getSize().height - 65);
		consoleMKVMERGE.setWrapStyleWord(true);
		consoleMKVMERGE.addKeyListener(kl);
		
		scrollFFMPEG = new JScrollPane();	
		scrollFFMPEG.getViewport().add(consoleFFMPEG);
		scrollFFPLAY = new JScrollPane();	
		scrollFFPLAY.getViewport().add(consoleFFPLAY);
		scrollFFPROBE = new JScrollPane();	
		scrollFFPROBE.getViewport().add(consoleFFPROBE);
		scrollBMXTRANSWRAP = new JScrollPane();	
		scrollBMXTRANSWRAP.getViewport().add(consoleBMXTRANSWRAP);
		scrollDVDAUTHOR = new JScrollPane();	
		scrollDVDAUTHOR.getViewport().add(consoleDVDAUTHOR);
		scrollTSMUXER = new JScrollPane();	
		scrollTSMUXER.getViewport().add(consoleTSMUXER);
		scrollMEDIAINFO = new JScrollPane();	
		scrollMEDIAINFO.getViewport().add(consoleMEDIAINFO);
		scrollYOUTUBEDL = new JScrollPane();	
		scrollYOUTUBEDL.getViewport().add(consoleYOUTUBEDL);
		scrollEXIFTOOL = new JScrollPane();	
		scrollEXIFTOOL.getViewport().add(consoleEXIFTOOL);
		scrollMKVMERGE = new JScrollPane();	
		scrollMKVMERGE.getViewport().add(consoleMKVMERGE);
		
		scrollFFMPEG.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollFFPLAY.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollFFPROBE.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollBMXTRANSWRAP.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollDVDAUTHOR.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });		
		scrollTSMUXER.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollMEDIAINFO.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollYOUTUBEDL.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });		
		scrollEXIFTOOL.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		scrollMKVMERGE.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	        	if (followLine.isSelected())
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
				
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, frmConsole.getWidth(), frmConsole.getHeight());
		tabbedPane.add("FFMPEG", scrollFFMPEG);
		tabbedPane.add("FFPLAY", scrollFFPLAY);
		tabbedPane.add("FFPROBE", scrollFFPROBE);
		tabbedPane.add("BMXTRANSWRAP", scrollBMXTRANSWRAP);
		tabbedPane.add("DVDAUTHOR", scrollDVDAUTHOR);
		tabbedPane.add("TSMUXER", scrollTSMUXER);
		tabbedPane.add("MEDIAINFO", scrollMEDIAINFO);
		tabbedPane.add("YOUTUBEDL", scrollYOUTUBEDL);
		tabbedPane.add("EXIFTOOL", scrollEXIFTOOL);
		tabbedPane.add("MKVMERGE", scrollMKVMERGE);
		frmConsole.getContentPane().add(tabbedPane);

	}
}
