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

import java.io.File;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;

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
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Renamer {

	public static JDialog frame;
	private JLabel lblRemplacer;
	private JTextField txtRemplacer;
	private JLabel lblPar;
	private JTextField txtPar;
	private JButton Renommer;
	private JLabel lblExemple;
	private JRadioButton btnLimit;
	private JSpinner spinnerLimit;
	private JRadioButton btnIncrementer;
	private JSpinner spinnerIncrementer;
	private JRadioButton btnAjouterAvant;
	private JTextField txtAjouterAvant;
	private JRadioButton btnAjouterApres;
	private JTextField txtAjouterApres;
	private JRadioButton convertToLower;
	private JRadioButton convertToUpper;
	private JRadioButton removeSpecialCharacters;
	
	private static StringBuilder errorList;
	
	private String fichier;	
	private String ext;

	/**
	 * @wbp.parser.entryPoint
	 */
	public Renamer() {
		frame = new JDialog();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setModal(true);
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			frame.setSize(511, 153);
		else
			frame.setSize(521, 163);
		frame.setTitle(Shutter.language.getProperty("frameRenommage"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(new Color(50,50,50));				
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setLocation(Shutter.frame.getLocation().x - 85, Shutter.frame.getLocation().y + 200);
			
		String fullName = new File(Shutter.listeDeFichiers.getSelectedValuesList().get(0)).getName();
		
		ext = fullName.substring(fullName.lastIndexOf("."));	
		fichier = new File(fullName).getName().replace(ext, "");
		
		btnLimit = new JRadioButton(Shutter.language.getProperty("btnLimit"));
		btnLimit.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		spinnerLimit.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerLimit.setEnabled(false);
		spinnerLimit.setBounds(226, 10, 46, 20);
		frame.getContentPane().add(spinnerLimit);
		
		btnIncrementer = new JRadioButton(Shutter.language.getProperty("btnIncrementer"));
		btnIncrementer.setFont(new Font("FreeSans", Font.PLAIN, 12));
		btnIncrementer.setBounds(7, 34, btnIncrementer.getPreferredSize().width, 16);
		frame.getContentPane().add(btnIncrementer);
		
		btnIncrementer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnIncrementer.isSelected())
					spinnerIncrementer.setEnabled(true);
				else
					spinnerIncrementer.setEnabled(false);
				
			}
			
		});
		
		spinnerIncrementer = new JSpinner(new SpinnerNumberModel(0, 0, 99999999, 1));
		spinnerIncrementer.setFont(new Font("FreeSans", Font.PLAIN, 11));
		spinnerIncrementer.setEnabled(false);
		spinnerIncrementer.setBounds(166, 33, 106, 20);
		frame.getContentPane().add(spinnerIncrementer);
		
		lblRemplacer = new JLabel(Shutter.language.getProperty("lblRemplacer"));
		lblRemplacer.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblRemplacer.setBounds(12, 80, 70, 14);
		frame.getContentPane().add(lblRemplacer);
		
		txtRemplacer = new JTextField();
		txtRemplacer.setColumns(10);
		txtRemplacer.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtRemplacer.setBounds(84, 77, 135, 21);
		frame.getContentPane().add(txtRemplacer);;		
				
		lblPar = new JLabel(Shutter.language.getProperty("lblPar"));
		lblPar.setFont(new Font("FreeSans", Font.PLAIN, 12));
		lblPar.setBounds(226, 80, 25, 14);
		frame.getContentPane().add(lblPar);
		
		txtPar = new JTextField();
		txtPar.setColumns(10);
		txtPar.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtPar.setBounds(257, 77, txtRemplacer.getWidth(), 21);
		frame.getContentPane().add(txtPar);
		
		lblExemple = new JLabel(Shutter.language.getProperty("lblExemple") + " " + fichier + ext);
		lblExemple.setForeground(new Color(71,163,236));
		lblExemple.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblExemple.setBounds(12, 104, 481, 14);
		frame.getContentPane().add(lblExemple);
						
		Renommer = new JButton(Shutter.language.getProperty("renommer"));
		Renommer.setBounds(400, 77, 98, 21);
		Renommer.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(Renommer);
		
		btnAjouterAvant = new JRadioButton(Shutter.language.getProperty("btnAjouterAvant"));
		btnAjouterAvant.setFont(new Font("FreeSans", Font.PLAIN, 12));
		btnAjouterAvant.setBounds(280, 12, 115, 16);
		frame.getContentPane().add(btnAjouterAvant);
		
		btnAjouterAvant.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnAjouterAvant.isSelected())
					txtAjouterAvant.setEnabled(true);
				else
					txtAjouterAvant.setEnabled(false);
				
			}
			
		});
		
		txtAjouterAvant = new JTextField();
		txtAjouterAvant.setEnabled(false);
		txtAjouterAvant.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtAjouterAvant.setColumns(10);
		txtAjouterAvant.setBounds(390, 10, 108, 21);
		frame.getContentPane().add(txtAjouterAvant);
		
		btnAjouterApres = new JRadioButton(Shutter.language.getProperty("btnAjouterApres"));
		btnAjouterApres.setFont(new Font("FreeSans", Font.PLAIN, 12));
		btnAjouterApres.setBounds(280, 34, 115, 16);
		frame.getContentPane().add(btnAjouterApres);
		
		btnAjouterApres.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnAjouterApres.isSelected())
					txtAjouterApres.setEnabled(true);
				else
					txtAjouterApres.setEnabled(false);
				
			}
			
		});
		
		txtAjouterApres = new JTextField();
		txtAjouterApres.setEnabled(false);
		txtAjouterApres.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtAjouterApres.setColumns(10);
		txtAjouterApres.setBounds(390, 32, 108, 21);
		frame.getContentPane().add(txtAjouterApres);
		
		convertToLower = new JRadioButton(Shutter.language.getProperty("convertToLower"));
		convertToLower.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		
		convertToUpper = new JRadioButton(Shutter.language.getProperty("convertToUpper"));
		convertToUpper.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		
		removeSpecialCharacters = new JRadioButton(Shutter.language.getProperty("removeSpecialCharacters"));
		removeSpecialCharacters.setFont(new Font("FreeSans", Font.PLAIN, 12));
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
		
		txtRemplacer.addKeyListener(key);
		txtPar.addKeyListener(key);
		txtAjouterAvant.addKeyListener(key);
		txtAjouterApres.addKeyListener(key);
		
		btnAjouterAvant.addActionListener(action);
		btnAjouterApres.addActionListener(action);
		btnIncrementer.addActionListener(action);
		btnLimit.addActionListener(action);
		
		spinnerLimit.addChangeListener(change);
		spinnerIncrementer.addChangeListener(change);
		
		Renommer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				errorList = new StringBuilder();
				
				String count = String.valueOf(Shutter.listeDeFichiers.getSelectedIndices().length); 		
				String format = "";
				
				for (int i = 0 ; i < count.length(); i++)
				{	
					format += "0";
				}
				
				NumberFormat formatter = new DecimalFormat(format);
				
				Thread rename = new Thread(new Runnable() {
					@Override
					public void run() {				
						int number = (int) spinnerIncrementer.getValue();
						for (String item : Shutter.listeDeFichiers.getSelectedValuesList())
						{				
							ext = item.substring(item.lastIndexOf("."));	
							fichier = new File(item).getName().replace(ext, "");;
							
							String newName;
							if (btnLimit.isSelected())
								newName = fichier.replace(txtRemplacer.getText(), txtPar.getText()).substring(0,(int) spinnerLimit.getValue());
							else
								newName = fichier.replace(txtRemplacer.getText(), txtPar.getText());
						
							if (btnAjouterAvant.isSelected())
								newName = txtAjouterAvant.getText() + newName;
							
							if (btnAjouterApres.isSelected())
								newName += txtAjouterApres.getText();
							
							if (btnIncrementer.isSelected())
								newName += formatter.format(number);					
							
							if (convertToLower.isSelected())
								newName = newName.toLowerCase();
										
							if (convertToUpper.isSelected())
								newName = newName.toUpperCase();
							
							if (removeSpecialCharacters.isSelected())
								newName = Normalizer.normalize(newName, Normalizer.Form.NFD).replace(" ", "_").replaceAll("[^\\w\\s]+","");
							
							//Ajout de l'extension
							newName += ext;
							
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
								lblExemple.setText(newFile.getName());
								
								for (int i = 0 ; i < Shutter.liste.getSize() ; i++)				
								{
									if (Shutter.liste.getElementAt(i).equals(item))
									{
										Shutter.liste.add(i, newFile.toString());
										Shutter.liste.remove(i + 1);
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
									
						Shutter.lblFichiers.setText(Utils.nombreDeFichiers());
						
						frame.dispose();
					}
				});
				rename.start();
			}
			
		});
		
		frame.setVisible(true);
		
	}

	private void update() {	
		
		String count = String.valueOf(Shutter.listeDeFichiers.getSelectedIndices().length); 		
		String format = "";
		
		for (int i = 0 ; i < count.length(); i++)
		{	
			format += "0";
		}
		NumberFormat formatter = new DecimalFormat(format);
		
		String exemple = Shutter.language.getProperty("lblExemple") + " ";
		
		if (btnLimit.isSelected())
			lblExemple.setText(fichier.replace(txtRemplacer.getText(), txtPar.getText()).substring(0,(int) spinnerLimit.getValue()));
		else
			lblExemple.setText(fichier.replace(txtRemplacer.getText(), txtPar.getText()));
		
		if (btnAjouterAvant.isSelected())
			lblExemple.setText(txtAjouterAvant.getText() + lblExemple.getText());
		
		if (btnAjouterApres.isSelected())
			lblExemple.setText(lblExemple.getText() + txtAjouterApres.getText());
		
		if (btnIncrementer.isSelected())
			lblExemple.setText(lblExemple.getText() + formatter.format(spinnerIncrementer.getValue()));
		
		if (convertToLower.isSelected())
			lblExemple.setText(lblExemple.getText().toLowerCase());
					
		if (convertToUpper.isSelected())
			lblExemple.setText(lblExemple.getText().toUpperCase());
		
		if (removeSpecialCharacters.isSelected())			
			lblExemple.setText(Normalizer.normalize(lblExemple.getText(), Normalizer.Form.NFD).replace(" ", "_").replaceAll("[^\\w\\s]+",""));
			
		lblExemple.setText(exemple + lblExemple.getText());
		
		//Ajout de l'extension
		lblExemple.setText(lblExemple.getText() + ext);		
	}
}
