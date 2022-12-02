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
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class SubtitlesEmbed {

	public static JDialog frame;
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	private JButton btnApply;
	private JButton btnReset;
	
	private static JLabel subtitlesText1 = new JLabel();
	public static JTextField subtitlesFile1 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile1 = new JComboBox<String>();
	
	private static JLabel subtitlesText2 = new JLabel();
	public static JTextField subtitlesFile2 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile2 = new JComboBox<String>();
	
	private static JLabel subtitlesText3 = new JLabel();
	public static JTextField subtitlesFile3 = new JTextField();
	public static JComboBox<String> comboSubtitlesFile3 = new JComboBox<String>();
	
	private static JLabel subtitlesText4 = new JLabel();
	public static JTextField subtitlesFile4 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile4 = new JComboBox<String>();
	
	private static JLabel subtitlesText5 = new JLabel();
	public static JTextField subtitlesFile5 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile5 = new JComboBox<String>();
	
	private static JLabel subtitlesText6 = new JLabel();
	public static JTextField subtitlesFile6 = new JTextField();
	public static JComboBox<String> comboSubtitlesFile6 = new JComboBox<String>();
	
	private static JLabel subtitlesText7 = new JLabel();
	public static JTextField subtitlesFile7 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile7 = new JComboBox<String>();
	
	private static JLabel subtitlesText8 = new JLabel();
	public static JTextField subtitlesFile8 = new JTextField(); 
	public static JComboBox<String> comboSubtitlesFile8 = new JComboBox<String>();
	
	private static JLabel subtitlesText9 = new JLabel();
	public static JTextField subtitlesFile9 = new JTextField();
	public static JComboBox<String> comboSubtitlesFile9 = new JComboBox<String>();
	
	private static JLabel subtitlesText10 = new JLabel();
	public static JTextField subtitlesFile10 = new JTextField();
	public static JComboBox<String> comboSubtitlesFile10 = new JComboBox<String>();
	
	private static int MousePositionX;
	private static int MousePositionY;

	public SubtitlesEmbed() {	
		
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(45, 45, 45));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameAddSubtitles"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 366);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);	
		
		if (frame != null && frame.isVisible())
			frame.setModal(false);	
		else
			frame.setModal(true);
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);				
			
		}		
		
		topPanel();
		addSubtitles();
				
		btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(7, frame.getHeight() - 21 - 7, frame.getWidth() / 2 - 7, 21);		
		frame.getContentPane().add(btnReset);	
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				for (Component c : frame.getContentPane().getComponents())
				{			
					if (c instanceof JTextField)
					{
						((JTextField) c).setText(Shutter.language.getProperty("aucun"));
						((JTextField) c).setHorizontalAlignment(SwingConstants.CENTER);
					}
				}
			}
			
		});
		
		btnApply = new JButton(Shutter.language.getProperty("btnApply"));
		btnApply.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnApply.setBounds(btnReset.getX() + btnReset.getWidth() + 4, btnReset.getY(), frame.getWidth() / 2 - 7 - 4, 21);
		frame.getContentPane().add(btnApply);
		
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (subtitlesFile1.getText().equals(Shutter.language.getProperty("aucun")))
				{
					VideoPlayer.caseAddSubtitles.setSelected(false);
				}
								
				frame.dispose();
			}
			
		});
				
		Utils.changeDialogVisibility(frame, false);	
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
					VideoPlayer.caseAddSubtitles.setSelected(false);
		            
		            Utils.changeDialogVisibility(frame, true);
	            	Shutter.frame.setOpacity(1.0f);
	            	Shutter.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	            	
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("frameAddSubtitles"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 28);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);
		topPanel.setBounds(0, 0, 1000, 28);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addSubtitles() {

		frame.getContentPane().add(subtitlesText1);
		frame.getContentPane().add(subtitlesFile1);
		frame.getContentPane().add(comboSubtitlesFile1);
		
		frame.getContentPane().add(subtitlesText2);
		frame.getContentPane().add(subtitlesFile2);
		frame.getContentPane().add(comboSubtitlesFile2);
		
		frame.getContentPane().add(subtitlesText3);
		frame.getContentPane().add(subtitlesFile3);
		frame.getContentPane().add(comboSubtitlesFile3);
		
		frame.getContentPane().add(subtitlesText4);
		frame.getContentPane().add(subtitlesFile4);
		frame.getContentPane().add(comboSubtitlesFile4);
		
		frame.getContentPane().add(subtitlesText5);
		frame.getContentPane().add(subtitlesFile5);
		frame.getContentPane().add(comboSubtitlesFile5);
		
		frame.getContentPane().add(subtitlesText6);
		frame.getContentPane().add(subtitlesFile6);
		frame.getContentPane().add(comboSubtitlesFile6);
		
		frame.getContentPane().add(subtitlesText7);
		frame.getContentPane().add(subtitlesFile7);
		frame.getContentPane().add(comboSubtitlesFile7);
		
		frame.getContentPane().add(subtitlesText8);
		frame.getContentPane().add(subtitlesFile8);
		frame.getContentPane().add(comboSubtitlesFile8);
		
		frame.getContentPane().add(subtitlesText9);
		frame.getContentPane().add(subtitlesFile9);
		frame.getContentPane().add(comboSubtitlesFile9);
		
		frame.getContentPane().add(subtitlesText10);
		frame.getContentPane().add(subtitlesFile10);
		frame.getContentPane().add(comboSubtitlesFile10);
		
		int i = 0;
		int labelY = topPanel.getHeight() + 12;

		//Add comboBox languages
		String[] languages = Locale.getISOLanguages();
		String[] allLanguages = new String[languages.length];		
		for (int l = 0; l < languages.length; l++)
		{
		    Locale loc = new Locale(languages[l]);
		    allLanguages[l] = loc.getDisplayLanguage();
		}
		
		//Set default comboBox item
		String language = Settings.comboLanguage.getSelectedItem().toString();
		if (language.contains("("))
		{				
			String l[] = language.split(" ");
			language = l[0];
		}

		for (Component c : frame.getContentPane().getComponents())
		{			
			if (c instanceof JLabel)
			{
				i++;
				
				((JLabel) c).setText(Shutter.language.getProperty("lblsubtitleNumber") + i + Shutter.language.getProperty("colon"));
				c.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
				((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
				c.setBounds(7, labelY, 100, 16);
				frame.getContentPane().add(c);	
			}
			else if (c instanceof JTextField)
			{
				((JTextField) c).setEditable(false);
				c.setForeground(Utils.themeColor);
				((JTextField) c).setBorder(BorderFactory.createLineBorder(new Color(75,75,75)));
				c.setFont(new Font("SansSerif", Font.BOLD, 13));
				c.setBackground(new Color(45, 45, 45));
				if (((JTextField) c).getText() == null || ((JTextField) c).getText() == "" || ((JTextField) c).getText().isEmpty())
				{
					((JTextField) c).setText(Shutter.language.getProperty("aucun"));
				}
				((JTextField) c).setHorizontalAlignment(SwingConstants.CENTER);
				c.setBounds(7 + 100 + 7, labelY - 3, frame.getWidth() - 7 - 100 - 100 - 21, 22);
				frame.getContentPane().add(c);
				
				// Drag & Drop
				((JTextField) c).setTransferHandler(new DragAndDropJTextField());

				c.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {				
						getSubtitlesPath(c);
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}
				});
			}
			else if (c instanceof JComboBox)
			{
				((JComboBox) c).setModel(new DefaultComboBoxModel<String>(allLanguages));				
				((JComboBox) c).setSelectedItem(language);
				c.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
				((JComboBox) c).setEditable(false);
				c.setBounds(7 + 100 + 7 + frame.getWidth() - 7 - 100 - 100 - 21 + 7, labelY - 1, 100, 18);
				frame.getContentPane().add(c);
				
				labelY += 30;
			}
		}	
	}

	private void getSubtitlesPath(Component c) {
		
		frame.setAlwaysOnTop(false);
		
		FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseSubtitles"),	FileDialog.LOAD);
		dialog.setDirectory(new File(Shutter.liste.elementAt(0).toString()).getParent());
		dialog.setFile("*.srt;*.vtt;*.ass;*.ssa;*.scc");
		dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
		dialog.setAlwaysOnTop(true);
		dialog.setMultipleMode(false);
		dialog.setVisible(true);
		
		if (dialog.getFile() != null) 
		{
			String input = dialog.getFile().substring(dialog.getFile().lastIndexOf("."));
			if (input.equals(".srt") || input.equals(".vtt") || input.equals(".ssa") || input.equals(".ass") || input.equals(".scc"))
			{
				((JTextField) c).setText(dialog.getDirectory() + dialog.getFile().toString());
			}
			else 
			{
				JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("invalidSubtitles"), Shutter.language.getProperty("subtitlesFileError"), JOptionPane.PLAIN_MESSAGE);
			}

			if (((JTextField) c).getText().length() > 50)
			{
				((JTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
			}
			else
				((JTextField) c).setHorizontalAlignment(SwingConstants.CENTER);
		}
		else
		{
			((JTextField) c).setText(Shutter.language.getProperty("aucun"));
			((JTextField) c).setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		frame.setAlwaysOnTop(true);
	}

	//Drag & Drop lblDestination
	@SuppressWarnings("serial")
	class DragAndDropJTextField extends TransferHandler {

		public boolean canImport(JComponent comp, DataFlavor[] arg1) {
			
			for (int i = 0; i < arg1.length; i++) 
			{
				DataFlavor flavor = arg1[i];
				if (flavor.equals(DataFlavor.javaFileListFlavor))
				{				
					return true;
				}
			}
			return false;
		}

		public boolean importData(JComponent c, Transferable t) {
			
			DataFlavor[] flavors = t.getTransferDataFlavors();
					
			for (int i = 0; i < flavors.length; i++) 
			{
				DataFlavor flavor = flavors[i];
				
				try {
					
					if (flavor.equals(DataFlavor.javaFileListFlavor))
					{
						List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
						Iterator<?> iter = l.iterator();
						
						while (iter.hasNext())
						{						
							File file = (File) iter.next();

							if (file.toString().contains(".srt") || file.toString().contains(".vtt") || file.toString().contains(".ssa") || file.toString().contains(".ass") || file.toString().contains(".scc"))
							{
								((JTextField) c).setText(file.getAbsolutePath());
								
								if (((JTextField) c).getText().length() > 50)
								{
									((JTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
								}
								else
									((JTextField) c).setHorizontalAlignment(SwingConstants.CENTER);
							}
							else
							{
								JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("invalidSubtitles"), Shutter.language.getProperty("subtitlesFileError"), JOptionPane.PLAIN_MESSAGE);
							}

						}

						return true;
					}
				} catch (IOException | UnsupportedFlavorException ex) {
				}
			}
			return false;
		}
	}
}
