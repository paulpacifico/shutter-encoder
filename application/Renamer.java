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

package application;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Renamer {

	public static JDialog frame;
	private JLabel lblReplace;
	private JTextField txtReplace;
	private JLabel lblBy;
	private JTextField txtBy;
	private JButton Rename;
	private JLabel lblExample;
	private JCheckBox btnLimit;
	private JSpinner spinnerLimit;
	private JCheckBox btnIncrement;
	private JSpinner spinnerIncrement;
	private JCheckBox btnAddBefore;
	private JTextField txtAddBefore;
	private JCheckBox btnAddAfter;
	private JTextField txtAddAfter;
	private JCheckBox convertToLower;
	private JCheckBox convertToUpper;
	private JCheckBox removeSpecialCharacters;
	
	private static StringBuilder errorList;
	
	private String fichier;	
	private String ext;

	public Renamer() {
				
		frame = new JDialog();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setModal(true);
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			frame.setSize(541, 153);
		else
			frame.setSize(551, 163);
		frame.setTitle(Shutter.language.getProperty("frameRenommage"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(Utils.bg32);	
		
		if (System.getProperty("os.name").contains("Mac") == false)
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBackground(Utils.c30);
		frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() / 2 - frame.getHeight()));
			
		String fullName = new File(Shutter.fileList.getSelectedValuesList().get(0)).getName();
		
		ext = fullName.substring(fullName.lastIndexOf("."));	
		fichier = new File(fullName).getName().replace(ext, "");
		
		btnLimit = new JCheckBox(Shutter.language.getProperty("btnLimit"));
		btnLimit.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		btnLimit.setBounds(7, 12, 211, 16);
		frame.getContentPane().add(btnLimit);
		
		btnLimit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnLimit.isSelected())
					spinnerLimit.setEnabled(true);
				else
					spinnerLimit.setEnabled(false);
				
			}
			
		});
		
		spinnerLimit = new JSpinner(new SpinnerNumberModel(fichier.length(), 1, fichier.length(), 1));
		spinnerLimit.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		spinnerLimit.setEnabled(false);
		spinnerLimit.setBounds(226, 10, 46, 20);
		frame.getContentPane().add(spinnerLimit);
		
		btnIncrement = new JCheckBox(Shutter.language.getProperty("btnIncrementer"));
		btnIncrement.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		btnIncrement.setBounds(7, 34, btnIncrement.getPreferredSize().width, 16);
		frame.getContentPane().add(btnIncrement);
		
		btnIncrement.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnIncrement.isSelected())
					spinnerIncrement.setEnabled(true);
				else
					spinnerIncrement.setEnabled(false);
				
			}
			
		});
		
		spinnerIncrement = new JSpinner(new SpinnerNumberModel(0, 0, 99999999, 1));
		spinnerIncrement.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		spinnerIncrement.setEnabled(false);
		spinnerIncrement.setBounds(166, 33, 106, 20);
		frame.getContentPane().add(spinnerIncrement);
		
		lblReplace = new JLabel(Shutter.language.getProperty("lblRemplacer"));
		lblReplace.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		lblReplace.setBounds(12, 80, 70, 14);
		frame.getContentPane().add(lblReplace);
		
		txtReplace = new JTextField();
		txtReplace.setColumns(10);
		txtReplace.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtReplace.setBounds(84, 77, 130, 21);
		frame.getContentPane().add(txtReplace);;		
				
		lblBy = new JLabel(Shutter.language.getProperty("lblPar"));
		lblBy.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		lblBy.setBounds(226, 80, lblBy.getPreferredSize().width, 14);
		frame.getContentPane().add(lblBy);
		
		txtBy = new JTextField();
		txtBy.setColumns(10);
		txtBy.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtBy.setBounds(267, 77, txtReplace.getWidth(), 21);
		frame.getContentPane().add(txtBy);
		
		lblExample = new JLabel(Shutter.language.getProperty("lblExemple") + " " + fichier + ext);
		lblExample.setForeground(Utils.themeColor);
		lblExample.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblExample.setBounds(12, 104, 481, 14);
		frame.getContentPane().add(lblExample);
						
		Rename = new JButton(Shutter.language.getProperty("renommer"));
		Rename.setBounds(410, 77, 108, 21);
		Rename.setMargin(new Insets(0,0,0,0));
		Rename.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		frame.getContentPane().add(Rename);
		
		btnAddBefore = new JCheckBox(Shutter.language.getProperty("btnAjouterAvant"));
		btnAddBefore.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		btnAddBefore.setBounds(280, 12, 130, 16);
		frame.getContentPane().add(btnAddBefore);
		
		btnAddBefore.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnAddBefore.isSelected())
					txtAddBefore.setEnabled(true);
				else
					txtAddBefore.setEnabled(false);
				
			}
			
		});
		
		txtAddBefore = new JTextField();
		txtAddBefore.setEnabled(false);
		txtAddBefore.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtAddBefore.setColumns(10);
		txtAddBefore.setBounds(410, 10, 108, 21);
		frame.getContentPane().add(txtAddBefore);
		
		btnAddAfter = new JCheckBox(Shutter.language.getProperty("btnAjouterApres"));
		btnAddAfter.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		btnAddAfter.setBounds(280, 34, 130, 16);
		frame.getContentPane().add(btnAddAfter);
		
		btnAddAfter.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnAddAfter.isSelected())
					txtAddAfter.setEnabled(true);
				else
					txtAddAfter.setEnabled(false);
				
			}
			
		});
		
		txtAddAfter = new JTextField();
		txtAddAfter.setEnabled(false);
		txtAddAfter.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtAddAfter.setColumns(10);
		txtAddAfter.setBounds(410, 32, 108, 21);
		frame.getContentPane().add(txtAddAfter);
		
		convertToLower = new JCheckBox(Shutter.language.getProperty("convertToLower"));
		convertToLower.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		convertToLower.setBounds(7, 57, convertToLower.getPreferredSize().width, 16);
		frame.getContentPane().add(convertToLower);
		
		convertToLower.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (convertToLower.isSelected())
					convertToUpper.setSelected(false);	
				
				update();
			}			
			
		});
		
		convertToUpper = new JCheckBox(Shutter.language.getProperty("convertToUpper"));
		convertToUpper.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		convertToUpper.setBounds(convertToLower.getWidth() + convertToLower.getLocation().x + 7, 57, convertToUpper.getPreferredSize().width, 16);
		frame.getContentPane().add(convertToUpper);
		
		convertToUpper.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (convertToUpper.isSelected())
					convertToLower.setSelected(false);	
				
				update();
			}			
			
		});
		
		removeSpecialCharacters = new JCheckBox(Shutter.language.getProperty("removeSpecialCharacters"));
		removeSpecialCharacters.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		removeSpecialCharacters.setBounds(convertToUpper.getWidth() + convertToUpper.getLocation().x + 7, 57, removeSpecialCharacters.getPreferredSize().width, 16);
		frame.getContentPane().add(removeSpecialCharacters);
		
		removeSpecialCharacters.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {				
				update();
			}			
			
		});
		
		KeyListener key = new KeyAdapter(){

			@Override
			public void keyReleased(KeyEvent arg0) {
				update();				
			}

		};
				
		ActionListener action = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {	
				update();
			}
		};
		
		ChangeListener change = new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				update();
			}
			
		};		
		
		txtReplace.addKeyListener(key);
		txtBy.addKeyListener(key);
		txtAddBefore.addKeyListener(key);
		txtAddAfter.addKeyListener(key);
		
		btnAddBefore.addActionListener(action);
		btnAddAfter.addActionListener(action);
		btnIncrement.addActionListener(action);
		btnLimit.addActionListener(action);
		
		spinnerLimit.addChangeListener(change);
		spinnerIncrement.addChangeListener(change);
		
		Rename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				errorList = new StringBuilder();
				
				String count = String.valueOf(Shutter.fileList.getSelectedIndices().length); 		
				String format = "";
				
				for (int i = 0 ; i < count.length(); i++)
				{	
					format += "0";
				}
				
				NumberFormat formatter = new DecimalFormat(format);
				
				Thread rename = new Thread(new Runnable() {
					@Override
					public void run() {				
						int number = (int) spinnerIncrement.getValue();
						for (String item : Shutter.fileList.getSelectedValuesList())
						{				
							ext = item.substring(item.lastIndexOf("."));	
							fichier = new File(item).getName().replace(ext, "");;
							
							String newName;
							if (btnLimit.isSelected())
								newName = fichier.replace(txtReplace.getText(), txtBy.getText()).substring(0,(int) spinnerLimit.getValue());
							else
								newName = fichier.replace(txtReplace.getText(), txtBy.getText());
						
							if (btnAddBefore.isSelected())
								newName = txtAddBefore.getText() + newName;
							
							if (btnAddAfter.isSelected())
								newName += txtAddAfter.getText();
							
							if (btnIncrement.isSelected())
								newName += formatter.format(number);					
							
							if (convertToLower.isSelected())
								newName = newName.toLowerCase();
										
							if (convertToUpper.isSelected())
								newName = newName.toUpperCase();
							
							if (removeSpecialCharacters.isSelected())
								newName = Normalizer.normalize(newName, Normalizer.Form.NFD).replace(" ", "_").replaceAll("[^\\w\\s]+","");
							
							//Ajout de l'extension
							newName += ext.replace(txtReplace.getText(), txtBy.getText());
							
							number ++;
							
							File file = new File(item);
							File newFile = new File(file.getParent() + "/" + newName);
							
							if (newFile.exists() && convertToLower.isSelected() == false && convertToUpper.isSelected() == false)
							{
								if (file.toString().equals(newFile.toString()) == false)
								{
									errorList.append(file.getName());
									errorList.append(System.lineSeparator());
								}
							}
							else
							{						
								file.renameTo(newFile);
								lblExample.setText(newFile.getName());
								
								for (int i = 0 ; i < Shutter.list.getSize() ; i++)				
								{
									if (Shutter.list.getElementAt(i).equals(item))
									{
										Shutter.list.add(i, newFile.toString());
										Shutter.list.remove(i + 1);
										break;
									}
								}
							}
							
						}	
						
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						
						//Affichage des erreurs
						if (errorList.length() != 0)
							JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("notProcessedFiles") + " " + '\n' + '\n' + errorList.toString() ,Shutter.language.getProperty("filesAlreadyExists"), JOptionPane.INFORMATION_MESSAGE);
						errorList.setLength(0);
									
						Shutter.lblFiles.setText(Utils.filesNumber());
						
						frame.dispose();
						
						Shutter.fileList.clearSelection();
					}
				});
				rename.start();
			}
			
		});
		
		//Right_to_left
		if (Shutter.getLanguage.contains(Locale.of("ar").getDisplayLanguage()))
		{
			//Frame
			for (Component c : frame.getContentPane().getComponents())
			{				
				if (c instanceof JCheckBox)
				{
					c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
			}		
		}
		
		frame.setVisible(true);
		
	}

	private void update() {	
		
		String count = String.valueOf(Shutter.fileList.getSelectedIndices().length); 		
		String format = "";
		
		for (int i = 0 ; i < count.length(); i++)
		{	
			format += "0";
		}
		NumberFormat formatter = new DecimalFormat(format);
		
		String exemple = Shutter.language.getProperty("lblExemple") + " ";
		
		if (btnLimit.isSelected())
			lblExample.setText(fichier.replace(txtReplace.getText(), txtBy.getText()).substring(0,(int) spinnerLimit.getValue()));
		else
			lblExample.setText(fichier.replace(txtReplace.getText(), txtBy.getText()));
		
		if (btnAddBefore.isSelected())
			lblExample.setText(txtAddBefore.getText() + lblExample.getText());
		
		if (btnAddAfter.isSelected())
			lblExample.setText(lblExample.getText() + txtAddAfter.getText());
		
		if (btnIncrement.isSelected())
			lblExample.setText(lblExample.getText() + formatter.format(spinnerIncrement.getValue()));
		
		if (convertToLower.isSelected())
			lblExample.setText(lblExample.getText().toLowerCase());
					
		if (convertToUpper.isSelected())
			lblExample.setText(lblExample.getText().toUpperCase());
		
		if (removeSpecialCharacters.isSelected())			
			lblExample.setText(Normalizer.normalize(lblExample.getText(), Normalizer.Form.NFD).replace(" ", "_").replaceAll("[^\\w\\s]+",""));
			
		lblExample.setText(exemple + lblExample.getText());
		
		//Ajout de l'extension
		lblExample.setText(lblExample.getText() + ext.replace(txtReplace.getText(), txtBy.getText()));		
	}
}
