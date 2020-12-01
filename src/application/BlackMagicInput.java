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

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import library.DECKLINK;

public class BlackMagicInput {

	public static JFrame frame; 
	public static JButton btnRecord;
	public static JRadioButton caseStopAt;
	public static JLabel lblTimecode;
	public static JComboBox<String[]> comboInput;
	private JComboBox<String[]> comboOutput;
	protected static JTextField lblDestination1;
	protected static JTextField lblDestination2;
	protected static JTextField lblDestination3;
	private JRadioButton caseDeinterlace;
	public static JTextField TC1;
	public static JTextField TC2;
	public static JTextField TC3;
	
	public BlackMagicInput() {		
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		if (System.getProperty("os.name").contains("Windows"))
			frame.setSize(473, 284);
		else
			
			frame.setSize(453, 264);

		frame.setTitle(Shutter.language.getProperty("frameBlackMagicInput"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		frame.setLocation(Shutter.frame.getLocation().x - frame.getWidth() - 20, Shutter.frame.getLocation().y + Shutter.frame.getHeight() / 2 - 40);
				
		frame.addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent arg0) {
				if (DECKLINK.isRunning)
				{
					try {
						DECKLINK.writer.write('q');
						DECKLINK.writer.flush();
						DECKLINK.writer.close();
					} catch (IOException er) {}
				}
				
				if (DECKLINK.isRunning)
					DECKLINK.process.destroy();				

			}
			
		});
		
		load();
		
		frame.setVisible(true);
				
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void load(){
		
		frame.addWindowListener(new WindowAdapter(){

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (DECKLINK.isRunning)
					DECKLINK.process.destroy();				
			}			
			
		});
				
		Label lblInput = new Label(Shutter.language.getProperty("lblInput"));
		lblInput.setAlignment(Label.RIGHT);
		lblInput.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblInput.setForeground(Color.WHITE);
		lblInput.setBackground(new Color(50,50,50));
		lblInput.setBounds(10, 9, 46, 22);
		frame.getContentPane().add(lblInput);
		
		comboInput = new JComboBox<String[]>();
		comboInput.setBounds(62, 9, 374, 22);	
		comboInput.setModel(new DefaultComboBoxModel(DECKLINK.formatsList.toArray()));
		comboInput.setSelectedItem(null);
		comboInput.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboInput.setEditable(false);
		frame.getContentPane().add(comboInput);
		
		Label lblOutput = new Label(Shutter.language.getProperty("lblOutput"));
		lblOutput.setAlignment(Label.RIGHT);
		lblOutput.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblOutput.setForeground(Color.WHITE);
		lblOutput.setBackground(new Color(50, 50, 50));
		lblOutput.setBounds(10, 37, 46, 22);
		frame.getContentPane().add(lblOutput);
		
		comboOutput = new JComboBox<String[]>();
		comboOutput.setFont(new Font("FreeSans", Font.PLAIN, 11));
		comboOutput.setEditable(false);
		comboOutput.setBounds(62, 37, comboInput.getWidth(), 22);
		frame.getContentPane().add(comboOutput);
		
		final String codecs[] = {"DV PAL 4/3", "DV PAL 16/9", "DNxHD 120", "DNxHD 185", "Apple ProRes 422", "Apple ProRes 422 HQ", "H.264 15Mb/s 320kb/s", "H.264 10Mb/s 256kb/s", "H.264 5Mb/s 128kb/s"};
	
		comboOutput.setModel(new DefaultComboBoxModel(codecs));
		comboOutput.setSelectedIndex(2);
		
		comboOutput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboOutput.getSelectedItem().toString().contains("DV PAL") || comboInput.getSelectedItem().toString().contains("interlaced") == false)
				{
					caseDeinterlace.setSelected(false);
					caseDeinterlace.setEnabled(false);
				}
				else
					caseDeinterlace.setEnabled(true);
				
				if (comboOutput.getSelectedItem().toString().contains("H.264"))
				{
					lblDestination2.setEnabled(true);
					lblDestination3.setEnabled(true);
				}
				else
				{
					lblDestination2.setEnabled(false);
					lblDestination2.setText(Shutter.language.getProperty("aucune"));
					lblDestination3.setEnabled(false);
					lblDestination3.setText(Shutter.language.getProperty("aucune"));
				}
			}
			
		});
		
		JLabel defaultOutput1 = new JLabel(Shutter.language.getProperty("output") + "1" + Shutter.language.getProperty("colon"));
		defaultOutput1.setFont(new Font("FreeSans", Font.PLAIN, 12));
		defaultOutput1.setBounds(10,  comboOutput.getLocation().y + comboOutput.getHeight() + 6, defaultOutput1.getPreferredSize().width + 4, defaultOutput1.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput1);
		
		lblDestination1 = new JTextField();
		lblDestination1.setEditable(false);
	  	lblDestination1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination1.setForeground(Utils.themeColor);
		lblDestination1.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination1.setBackground(new Color(50,50,50));
		if (System.getProperty("os.name").contains("Windows"))
			lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
		else
			lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
			
		lblDestination1.setBounds(comboOutput.getX() + 2, defaultOutput1.getY() - 4, comboOutput.getWidth() - 2, 22);
		frame.getContentPane().add(lblDestination1);
		
		//Drag & Drop
		lblDestination1.setTransferHandler(new BlackMagicOutputTransferHandler1());   	
		
		lblDestination1.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				File destination = null;
				if (System.getProperty("os.name").contains("Mac")) {
					FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
							FileDialog.LOAD);
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
					dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
					dialog.setAlwaysOnTop(true);
					System.setProperty("apple.awt.fileDialogForDirectories", "true");
					dialog.setVisible(true);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					if (dialog.getDirectory() != null)
						destination = new File(dialog.getDirectory() + dialog.getFile());
				} else if (System.getProperty("os.name").contains("Linux")) {
					JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
					dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
					dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					
					if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
						dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
					else
						dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

					int result = dialog.showOpenDialog(frame);
					if (result == JFileChooser.APPROVE_OPTION) 
		               destination = new File(dialog.getSelectedFile().toString());				   
				} else {
					Shell shell = new Shell(SWT.ON_TOP);

					shell.setSize(frame.getSize().width, frame.getSize().height);
					shell.setLocation(frame.getLocation().x, frame.getLocation().y);
					shell.setAlpha(0);
					shell.open();

					DirectoryDialog dialog = new DirectoryDialog(shell);
					dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
					dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");

					try {
						destination = new File(dialog.open());
					} catch (Exception e1) {}

					shell.dispose();
				}

				if (destination != null) {					
					//Montage du chemin UNC
					if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
						destination = Utils.UNCPath(destination);
					
					if (destination.isFile())
						lblDestination1.setText(destination.getParent());
					else
						lblDestination1.setText(destination.toString());
					
					//Si destination identique à l'une des autres
					if (lblDestination1.getText().equals(lblDestination2.getText()) || lblDestination1.getText().equals(lblDestination3.getText())) 
					{
						JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("ChooseDifferentFolder"),
								Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
						if (System.getProperty("os.name").contains("Windows"))
							lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
						else
							lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblDestination1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		JLabel defaultOutput2 = new JLabel(Shutter.language.getProperty("output") + "2" + Shutter.language.getProperty("colon"));
		defaultOutput2.setFont(new Font("FreeSans", Font.PLAIN, 12));
		defaultOutput2.setBounds(10,  defaultOutput1.getLocation().y + defaultOutput1.getHeight() + 6, defaultOutput2.getPreferredSize().width + 4, defaultOutput2.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput2);
		
		lblDestination2 = new JTextField();
		lblDestination2.setEditable(false);
		lblDestination2.setEnabled(false);
	  	lblDestination2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination2.setForeground(Utils.themeColor);
		lblDestination2.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination2.setBackground(new Color(50,50,50));
		lblDestination2.setText(Shutter.language.getProperty("aucune"));
			
		lblDestination2.setBounds(comboOutput.getX() + 2, defaultOutput2.getY() - 4, comboOutput.getWidth() - 2, 22);
		frame.getContentPane().add(lblDestination2);
		
		//Drag & Drop
		lblDestination2.setTransferHandler(new BlackMagicOutputTransferHandler2());  
		
		lblDestination2.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (BlackMagicInput.lblDestination2.isEnabled())
				{
					File destination = null;
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));
	
						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);
	
						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();
	
						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
	
						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}
	
						shell.dispose();
					}
	
					if (destination != null) {					
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
							destination = Utils.UNCPath(destination);
						
						if (destination.isFile())
							lblDestination2.setText(destination.getParent());
						else
							lblDestination2.setText(destination.toString());
						
						//Si destination identique à l'une des autres
						if (lblDestination2.getText().equals(lblDestination1.getText()) || lblDestination2.getText().equals(lblDestination3.getText())) 
						{
							JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
							lblDestination2.setText(Shutter.language.getProperty("aucune"));																			
						}
					}
					else
						lblDestination2.setText(Shutter.language.getProperty("aucune"));
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblDestination2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		JLabel defaultOutput3 = new JLabel(Shutter.language.getProperty("output") + "3" + Shutter.language.getProperty("colon"));
		defaultOutput3.setFont(new Font("FreeSans", Font.PLAIN, 12));
		defaultOutput3.setBounds(10,  defaultOutput2.getLocation().y + defaultOutput2.getHeight() + 6, defaultOutput3.getPreferredSize().width + 4, defaultOutput3.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput3);
		
		lblDestination3 = new JTextField();
		lblDestination3.setEditable(false);
		lblDestination3.setEnabled(false);
	  	lblDestination3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination3.setForeground(Utils.themeColor);
		lblDestination3.setFont(new Font("SansSerif", Font.BOLD, 13));
		lblDestination3.setBackground(new Color(50,50,50));
		lblDestination3.setText(Shutter.language.getProperty("aucune"));
			
		lblDestination3.setBounds(comboOutput.getX() + 2, defaultOutput3.getY() - 4, comboOutput.getWidth() - 2, 22);
		frame.getContentPane().add(lblDestination3);
		
		//Drag & Drop
		lblDestination3.setTransferHandler(new BlackMagicOutputTransferHandler3());  

		lblDestination3.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (BlackMagicInput.lblDestination3.isEnabled())
				{
					File destination = null;
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));
	
						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);
	
						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();
	
						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
	
						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}
	
						shell.dispose();
					}
	
					if (destination != null) {					
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
							destination = Utils.UNCPath(destination);
		
						if (destination.isFile())
							lblDestination3.setText(destination.getParent());
						else
							lblDestination3.setText(destination.toString());
						
						//Si destination identique à l'une des autres
						if (lblDestination3.getText().equals(lblDestination1.getText()) || lblDestination3.getText().equals(lblDestination2.getText())) 
						{
							JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
							lblDestination3.setText(Shutter.language.getProperty("aucune"));
						}
					}
					else
						lblDestination3.setText(Shutter.language.getProperty("aucune"));
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblDestination3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination3.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		comboInput.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
	            DECKLINK.toFFPLAY("-f decklink -draw_bars 0 -i " + '"' + DECKLINK.getBlackMagic + "@" + (comboInput.getSelectedIndex() + 1) + '"' + " -c:a copy -c:v copy -f matroska pipe:play |");				
			}

        });
							
		caseDeinterlace = new JRadioButton(Shutter.language.getProperty("caseDeinterlace"));
		caseDeinterlace.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseDeinterlace.setBounds(10, defaultOutput3.getY() + defaultOutput3.getHeight() + 6, 157, 23);
		frame.getContentPane().add(caseDeinterlace);
		
		caseStopAt = new JRadioButton(Shutter.language.getProperty("caseStopAt"));
		caseStopAt.setFont(new Font("FreeSans", Font.PLAIN, 12));
		caseStopAt.setBounds(169, caseDeinterlace.getY(), 169, 23);
		frame.getContentPane().add(caseStopAt);
		
		caseStopAt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (caseStopAt.isSelected())
				{
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
				}
				else
				{
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
				}
			}
			
		});
		
		TC1 = new JTextField("00");
		TC1.setBounds(336, caseStopAt.getY() + 1, 32, 21);
		TC1.setHorizontalAlignment(SwingConstants.CENTER);
		TC1.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC1.setColumns(10);
		TC1.setEnabled(false);
		frame.getContentPane().add(TC1);
				
		TC2 = new JTextField("00");
		TC2.setBounds(370, caseStopAt.getY() + 1, 32, 21);
		TC2.setHorizontalAlignment(SwingConstants.CENTER);
		TC2.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC2.setColumns(10);
		TC2.setEnabled(false);
		frame.getContentPane().add(TC2);
		
		TC3 = new JTextField("00");
		TC3.setBounds(404, caseStopAt.getY() + 1, 32, 21);
		TC3.setHorizontalAlignment(SwingConstants.CENTER);
		TC3.setFont(new Font("FreeSans", Font.PLAIN, 14));
		TC3.setColumns(10);
		TC3.setEnabled(false);
		frame.getContentPane().add(TC3);
				
		TC1.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e) {		
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (TC1.getText().length() >= 2)					
					TC1.setText("");
					
			}

			@Override
			public void keyPressed(KeyEvent e) {					 
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}	
		});

		TC2.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e) {		
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume();
				else if (TC2.getText().length() >= 2)					
					TC2.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}	
		});
		
		TC3.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e) {		
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
		            e.consume(); 
				else if (TC3.getText().length() >= 2)					
					TC3.setText("");
			}

			@Override
			public void keyPressed(KeyEvent e) { 
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}	
		});

		btnRecord = new JButton(Shutter.language.getProperty("btnRecord"));
		btnRecord.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnRecord.setBounds(12, caseStopAt.getY() + caseStopAt.getHeight() + 6, 423, 21);
		frame.getContentPane().add(btnRecord);
		
		btnRecord.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnRecord.getText().equals(Shutter.language.getProperty("btnRecord")))
				{
					record();
					comboInput.setEnabled(false);
					comboOutput.setEnabled(false);
					caseDeinterlace.setEnabled(false);
					caseStopAt.setEnabled(false);
					TC1.setEnabled(false);
					TC2.setEnabled(false);
					TC3.setEnabled(false);
					btnRecord.setText(Shutter.language.getProperty("btnStopRecording"));
				}
				else if (btnRecord.getText().equals(Shutter.language.getProperty("btnStopRecording")))
				{															
					try {
						DECKLINK.writer.write('q');
						DECKLINK.writer.flush();
						DECKLINK.writer.close();
					} catch (IOException er) {}
					
					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} while (DECKLINK.process.isAlive());
										
					comboInput.setEnabled(true);
					comboOutput.setEnabled(true);
					caseDeinterlace.setEnabled(true);
					caseStopAt.setEnabled(true);
					TC1.setEnabled(true);
					TC2.setEnabled(true);
					TC3.setEnabled(true);
					btnRecord.setText(Shutter.language.getProperty("btnRecord"));
					
					DECKLINK.toFFPLAY("-f decklink -draw_bars 0 -i " + '"' + DECKLINK.getBlackMagic + "@" + (comboInput.getSelectedIndex() + 1) + '"' + " -c:a copy -c:v copy -f matroska pipe:play |");
				}
			}
					
		});
		
		lblTimecode = new JLabel("00:00:00");
		lblTimecode.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimecode.setFont(new Font("FreeSans", Font.PLAIN, 30));
		lblTimecode.setForeground(Color.RED);
		lblTimecode.setBounds(10, btnRecord.getY() + btnRecord.getHeight() + 18, 422, 24);
		frame.getContentPane().add(lblTimecode);
		
	}
	
	private void record() {
		
		String interlaced = "";
		if (comboInput.getSelectedItem().toString().contains("(interlaced, upper field first)") && caseDeinterlace.isSelected() == false)
		{
			if (comboOutput.getSelectedItem().toString().contains("Apple ProRes"))
				interlaced = " -field_order tt";
			else if (comboOutput.getSelectedItem().toString().contains("DNxHD"))
				interlaced = " -flags +ildct -top 1";
			else if (comboOutput.getSelectedItem().toString().contains("DV PAL"))
				interlaced = " -field_order tb";
		}
		else if (comboInput.getSelectedItem().toString().contains("(interlaced, lower field first)")  && caseDeinterlace.isSelected() == false)
		{
			if (comboOutput.getSelectedItem().toString().contains("Apple ProRes"))
				interlaced = " -field_order bt";
			else if (comboOutput.getSelectedItem().toString().contains("DNxHD"))
				interlaced = " -field_order bt -flags +ildct -top 1";
		}	
		
		String deinterlace = "";
		if (caseDeinterlace.isSelected() || comboInput.getSelectedItem().toString().contains("H.264"))
		{			
			if (comboInput.getSelectedItem().toString().contains("(interlaced, upper field first)"))
				deinterlace = "yadif=0:0:0";
			else if (comboInput.getSelectedItem().toString().contains("(interlaced, lower field first)"))
				deinterlace = "yadif=0:1:0";
			
			if (comboOutput.getSelectedItem().toString().contains("DNxHD"))
				deinterlace = "," + deinterlace;
			else
				deinterlace = " -filter:v " + deinterlace;
		}
				
		boolean cancelled = false;
		
		//Si le fichier existe
		File fileOut = new File(lblDestination1.getText() + "/Num_" + comboOutput.getSelectedItem().toString().replace(" 4/3", "").replace(" 16/9", "").replace(" ","_") + ".mov");
		
		if (comboOutput.getSelectedItem().toString().contains("H.264"))
			fileOut = new File(lblDestination1.getText() + "/Num_" + "H.264" + ".mp4");
			
		if(fileOut.exists())
		{						
			if (comboOutput.getSelectedItem().toString().contains("H.264"))
				fileOut = Utils.fileReplacement(lblDestination1.getText(), "Num.mp4", ".mp4", "_H.264_", ".mp4");
			else
				fileOut = Utils.fileReplacement(lblDestination1.getText(), "Num.mov", ".mov", "_" + comboOutput.getSelectedItem().toString().replace(" 4/3", "").replace(" 16/9", "").replace(" ","_") + "_", ".mov");
						
			if (fileOut == null)
			{
				cancelled = true;
				comboInput.setEnabled(true);
				comboOutput.setEnabled(true);
				caseDeinterlace.setEnabled(true);
				caseStopAt.setEnabled(true);
				TC1.setEnabled(true);
				TC2.setEnabled(true);
				TC3.setEnabled(true);
				btnRecord.setText(Shutter.language.getProperty("btnRecord"));
			}
		}
		
		if (cancelled == false)
		{
			String decklink = "-f decklink -draw_bars 0 -i " + '"' + DECKLINK.getBlackMagic + "@" + (comboInput.getSelectedIndex() + 1) + '"';
			String output = "-f tee " + '"' + fileOut.toString().replace("\\", "/") + "|[f=matroska]pipe:play" + '"';
			
			if (comboOutput.getSelectedItem().toString().contains("H.264"))
			{
				if (lblDestination2.getText().equals(Shutter.language.getProperty("aucune")) == false)
					output += " -f mp4 " + '"' + fileOut.toString().replace("\\", "/").replace(lblDestination1.getText(), lblDestination2.getText()) + '"';
				
				if (lblDestination3.getText().equals(Shutter.language.getProperty("aucune")) == false)
					output += " -f mp4 " + '"' + fileOut.toString().replace("\\", "/").replace(lblDestination1.getText(), lblDestination3.getText()) + '"';
			}
			
			switch (comboOutput.getSelectedItem().toString())
			{
				case "DV PAL 4/3":
					DECKLINK.toFFMPEG(decklink + " -aspect 4:3 -s 720x576 -c:a copy -c:v dvvideo -b:v 25000 -r 25" + interlaced + " -map v? -map a? -y " + output);
					break;
				case "DV PAL 16/9":
					DECKLINK.toFFMPEG(decklink + " -aspect 16:9 -s 720x576 -c:a copy -c:v dvvideo -b:v 25000 -r 25" + interlaced + " -map v? -map a? -y " + output);
					break;
				case "DNxHD 120":
					DECKLINK.toFFMPEG(decklink + " -s 1920x1080 -filter:v scale=1920:1080:force_original_aspect_ratio=decrease,pad=" + '"' + "1920:1080:(ow-iw)/2:(oh-ih)/2" + '"' + deinterlace + " -c:a copy -c:v dnxhd -b:v 120M -pix_fmt yuv422p" + interlaced + " -map v? -map a? -y " + output);
					break;
				case "DNxHD 185":
					DECKLINK.toFFMPEG(decklink + " -s 1920x1080 -filter:v scale=1920:1080:force_original_aspect_ratio=decrease,pad=" + '"' + "1920:1080:(ow-iw)/2:(oh-ih)/2" + '"'  + deinterlace   + " -c:a copy -c:v dnxhd -b:v 185M -pix_fmt yuv422p" + interlaced + " -map v? -map a? -y " + output);
					break;
				case "Apple ProRes 422":
					DECKLINK.toFFMPEG(decklink + " -c:a copy -c:v prores -profile:v 2" + deinterlace + interlaced + " -pix_fmt yuv422p10 -map v? -map a? -y " + output);
					break;
				case "Apple ProRes 422 HQ":
					DECKLINK.toFFMPEG(decklink + " -c:a copy -c:v prores -profile:v 3" + deinterlace + interlaced + " -pix_fmt yuv422p10 -map v? -map a? -y " + output);
					break;	
				case "H.264 15Mb/s 320kb/s":
					DECKLINK.toFFMPEG(decklink + " -c:v h264 -b:v 15000k -c:a aac -b:a 320k" + deinterlace + " -profile:v high -level 5.1 -pix_fmt yuv420p -map v? -map a? -y " + output);
					break;
				case "H.264 10Mb/s 256kb/s":
					DECKLINK.toFFMPEG(decklink + " -c:v h264 -b:v 10000k -c:a aac -b:a 256k" + deinterlace + " -profile:v high -level 5.1 -pix_fmt yuv420p -map v? -map a? -y " + output);
					break;
				case "H.264 5Mb/s 128kb/s":
					DECKLINK.toFFMPEG(decklink + " -c:v h264 -b:v 5000k -c:a aac -b:a 128k" + deinterlace + " -profile:v high -level 5.1 -pix_fmt yuv420p -map v? -map a? -y " + output);
					break;					
			}
		}
	}
}

//Drag & Drop lblDestination1
@SuppressWarnings("serial")
class BlackMagicOutputTransferHandler1 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				BlackMagicInput.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				return true;
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file =Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							BlackMagicInput.lblDestination1.setText(file.getParent());
						} else {
							BlackMagicInput.lblDestination1.setText(file.getAbsolutePath());
						}
						
						//Si destination identique à l'une des autres
						if (BlackMagicInput.lblDestination1.getText().equals(BlackMagicInput.lblDestination2.getText()) || BlackMagicInput.lblDestination1.getText().equals(BlackMagicInput.lblDestination3.getText())) 
						{
							JOptionPane.showMessageDialog(BlackMagicInput.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
							if (System.getProperty("os.name").contains("Windows"))
								BlackMagicInput.lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
							else
								BlackMagicInput.lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
						}

					}

					// Border
					BlackMagicInput.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

//Drag & Drop lblDestination2
@SuppressWarnings("serial")
class BlackMagicOutputTransferHandler2 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && BlackMagicInput.lblDestination2.isEnabled()) {
				BlackMagicInput.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				return true;
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor) && BlackMagicInput.lblDestination2.isEnabled()) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file =Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							BlackMagicInput.lblDestination2.setText(file.getParent());
						} else {
							BlackMagicInput.lblDestination2.setText(file.getAbsolutePath());
						}		
						
						//Si destination identique à l'une des autres
						if (BlackMagicInput.lblDestination2.getText().equals(BlackMagicInput.lblDestination1.getText()) || BlackMagicInput.lblDestination2.getText().equals(BlackMagicInput.lblDestination3.getText())) 
						{
							JOptionPane.showMessageDialog(BlackMagicInput.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
							BlackMagicInput.lblDestination2.setText(Shutter.language.getProperty("aucune"));																			
						}
					}

					// Border
					BlackMagicInput.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}

//Drag & Drop lblDestination3
@SuppressWarnings("serial")
class BlackMagicOutputTransferHandler3 extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor) && BlackMagicInput.lblDestination3.isEnabled()) {
				BlackMagicInput.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				return true;
			}
		}
		return false;
	}

	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor) && BlackMagicInput.lblDestination3.isEnabled()) {
					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					Iterator<?> iter = l.iterator();
					while (iter.hasNext()) {
						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file =Utils.UNCPath(file);
						
						if (file.getName().contains(".")) {
							BlackMagicInput.lblDestination3.setText(file.getParent());
						} else {
							BlackMagicInput.lblDestination3.setText(file.getAbsolutePath());
						}						
						
						//Si destination identique à l'une des autres
						if (BlackMagicInput.lblDestination3.getText().equals(BlackMagicInput.lblDestination1.getText()) || BlackMagicInput.lblDestination3.getText().equals(BlackMagicInput.lblDestination2.getText())) 
						{
							JOptionPane.showMessageDialog(BlackMagicInput.frame, Shutter.language.getProperty("ChooseDifferentFolder"),
									Shutter.language.getProperty("chooseDestinationFolder"), JOptionPane.ERROR_MESSAGE);
							BlackMagicInput.lblDestination3.setText(Shutter.language.getProperty("aucune"));
						}
					}

					// Border
					BlackMagicInput.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					BlackMagicInput.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}
