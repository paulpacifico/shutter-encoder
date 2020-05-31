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

import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.alee.laf.text.WebTextPane;
import com.alee.managers.style.StyleId;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class Help {

	public static JFrame frame;
	private String pathToFonctions;
	private WebTextPane txtFonctions;
	private JComboBox<String[]> comboFonctions;
	private JButton btnVideoDePresentation;
	private JButton btnFaq;

	/**
	 * @wbp.parser.entryPoint
	 */
	public Help() {
		frame = new JFrame();
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			frame.setSize(625, 64);
		else
			frame.setSize(625, 74);
		frame.setTitle(Shutter.language.getProperty("frameAstuces"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		frame.setLocation(Shutter.frame.getLocation().x + Shutter.frame.getSize().width + 20, Shutter.frame.getLocation().y);
				
		loadFonctions();
		
		frame.setVisible(true);
		
		try {
			txtFonctions.setText(readFile(pathToFonctions + "/" + comboFonctions.getSelectedItem().toString() + ".txt"));
		} catch (IOException e) {}
		if (txtFonctions.getText().length() > 0)
		{
			txtFonctions.setSize(572, txtFonctions.getPreferredSize().height);		
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				frame.setSize(625, txtFonctions.getHeight() + 56);
			else
				frame.setSize(625, txtFonctions.getHeight() + 66);
		}	
		
	}
	
	private void loadFonctions(){		
		pathToFonctions = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("Windows"))
			pathToFonctions = pathToFonctions.substring(1,pathToFonctions.length()-1);
		else
			pathToFonctions = pathToFonctions.substring(0,pathToFonctions.length()-1);
		
		if (Shutter.getLanguage.equals("Français"))
			pathToFonctions = pathToFonctions.substring(0,(int) (pathToFonctions.lastIndexOf("/"))).replace("%20", " ")  + "/Help/fr";
		else if (Shutter.getLanguage.equals("Italiano"))
			pathToFonctions = pathToFonctions.substring(0,(int) (pathToFonctions.lastIndexOf("/"))).replace("%20", " ")  + "/Help/it";
		else
			pathToFonctions = pathToFonctions.substring(0,(int) (pathToFonctions.lastIndexOf("/"))).replace("%20", " ")  + "/Help/en";	

		
		JLabel lblFonctions = new JLabel(Shutter.language.getProperty("lblFonctions"));
		lblFonctions.setForeground(Color.WHITE);
		lblFonctions.setFont(new Font("Arial", Font.PLAIN, 12));
		lblFonctions.setBounds(6, 11, lblFonctions.getPreferredSize().width, 15);
		frame.getContentPane().add(lblFonctions);
						
		comboFonctions = new JComboBox<String[]>();		
		comboFonctions.setModel(Shutter.comboFonctions.getModel());
		comboFonctions.setFont(new Font("Arial", Font.PLAIN, 11));
		comboFonctions.setEditable(true);
		comboFonctions.setMaximumRowCount(Toolkit.getDefaultToolkit().getScreenSize().height / 33);
		comboFonctions.setBounds(133, 8, 168, 22);
		comboFonctions.setRenderer(new ComboBoxRenderer());
		frame.getContentPane().add(comboFonctions);
		
		if (Shutter.comboFonctions.isEnabled() == false)
			comboFonctions.setEnabled(false);
		else
			comboFonctions.setEnabled(true);
		
		btnFaq = new JButton(Shutter.language.getProperty("btnFaq"));
		btnFaq.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnFaq.setBounds(308, 7, 130, 25);
		frame.getContentPane().add(btnFaq);
		
		btnVideoDePresentation = new JButton(Shutter.language.getProperty("btnVideoDePresentation"));
		btnVideoDePresentation.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnVideoDePresentation.setBounds(btnFaq.getX() + btnFaq.getWidth() + 4, 7, 160, 25);
		frame.getContentPane().add(btnVideoDePresentation);
		
		
		txtFonctions = new WebTextPane(StyleId.textpaneTransparent);
		txtFonctions.setFont(new Font("Arial", Font.PLAIN, 14));
		txtFonctions.setLocation(10, 38);
		txtFonctions.setForeground(Color.WHITE);
		txtFonctions.setSize(572, txtFonctions.getPreferredSize().height);
		txtFonctions.setBackground(new Color(50,50,50));
		txtFonctions.setEditable(false);
		frame.getContentPane().add(txtFonctions);
		
		comboFonctions.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					txtFonctions.setText(readFile(pathToFonctions + "/" + comboFonctions.getSelectedItem().toString() + ".txt"));
				} catch (IOException e) {}
				if (txtFonctions.getText().length() > 0)
				{
					txtFonctions.setSize(572, txtFonctions.getPreferredSize().height);				
					if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
						frame.setSize(625, txtFonctions.getHeight() + 56);
					else
						frame.setSize(625, txtFonctions.getHeight() + 66);
				}				
			}
			
		});
		
		btnVideoDePresentation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Shutter.getLanguage.equals("Français"))
						Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/fr/#videoYT"));
					else
						Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/en/#videoYT"));
				}catch(Exception e){}				
			}
					
		});
		
		btnFaq.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Shutter.getLanguage.equals("Français"))
						Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/fr/faq.html"));
					else
						Desktop.getDesktop().browse(new URI("http://www.shutterencoder.com/en/faq.html"));
				}catch(Exception e){}				
			}
					
		});
	}
	
	private String readFile(String fileName) throws IOException {
		
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
}
