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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import library.FFMPEG;

public class Wetransfer {
	public static JDialog frame;
	public static JDialog shadow;
	public static boolean isRunning = false;
	public static boolean error = false;
	public static Thread runProcess;
	public static Process process;
	private static StringBuilder uploadFileList = new StringBuilder();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel panelHaut;
	private JLabel topImage;	
	private static JTextField textTo = new JTextField();
	private static JTextArea textMessage = new JTextArea();
	private JLabel lblYourMail;
	private JLabel lblRecipient;
	private JLabel lblMessage;
	private static JTextField textFrom = new JTextField();
	private static JRadioButton casePlus = new JRadioButton(Shutter.language.getProperty("casePlus"));
	private JLabel lblUser;
	private static JTextField textUser = new JTextField();
	private JLabel lblMotDePasse;
	private static JPasswordField textPassword = new JPasswordField();
	public static JButton btnOK; //Si le bouton est disable alors la connexion est établie
	private JButton btnReset;
	private static String wetransferAdress = "";

	/**
	 * @wbp.parser.entryPoint
	 */
	public Wetransfer() {
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameWetransfer"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(267, 340);
		frame.setResizable(false);
		frame.setModal(true);
		frame.setAlwaysOnTop(true);
		frame.getRootPane().putClientProperty( "Window.shadow", Boolean.FALSE );
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight() + 18, 15, 15));
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			setShadow();
		}
						
		panelHaut();
		grpWetransfer();
		
		frame.getRootPane().setDefaultButton(btnOK);	
			
		Utils.changeDialogVisibility(Wetransfer.frame, Wetransfer.shadow, false);		
	}

	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
			
	private void panelHaut() {
		
		panelHaut = new JPanel();		
		panelHaut.setLayout(null);
			
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 35,0,35, 15);
		panelHaut.add(quit);
		panelHaut.setBounds(0, 0, 267, 44);
		
		quit.addMouseListener(new MouseListener(){

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit3.png"))));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {	
				if (accept)		
				{
					//Suppression image temporaire
							    		
					File file = new File(Shutter.dirTemp + "fileToCrop.jpg");
					if (file.exists()) file.delete();
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);
		            btnReset.doClick();
					quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit2.png"))));
					Utils.changeDialogVisibility(frame, shadow, true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				quit.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/quit2.png"))));
				accept = false;
			}
					
		});

		JLabel title = new JLabel("WeTransfer");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 44);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		panelHaut.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(panelHaut.getSize().width, panelHaut.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		panelHaut.add(topImage);

		frame.getContentPane().add(panelHaut);
		
		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;					
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX, MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	private void grpWetransfer() {
		lblYourMail = new JLabel(Shutter.language.getProperty("lblYourMail"));
		lblYourMail.setBounds(7, 56, 90, 16);
		lblYourMail.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYourMail.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblYourMail);		

		textFrom.setBounds(101, lblYourMail.getY() - 1, 154, 21);
		textFrom.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textFrom.setColumns(10);		
		frame.getContentPane().add(textFrom);
		
		textFrom.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {				
				//CTRL + V coller
		        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
		        	PasteFromClipBoard(textFrom);
			}

			@Override
			public void keyReleased(KeyEvent e) {				
			}
		});
					
		lblRecipient = new JLabel(Shutter.language.getProperty("lblRecipient"));
		lblRecipient.setBounds(2, lblYourMail.getY() + lblYourMail.getHeight() + 17, 95, 16);
		lblRecipient.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRecipient.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblRecipient);		
				
		textTo.setBounds(textFrom.getX(), lblRecipient.getY() - 1, 154, 21);
		textTo.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textTo.setColumns(10);
		frame.getContentPane().add(textTo);		
		
		lblMessage = new JLabel("Message :");
		lblMessage.setBounds(2, lblRecipient.getY() + lblRecipient.getHeight() + 17, 95, 16);
		lblMessage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMessage.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblMessage);
		
		textMessage.setBounds(textTo.getX(), lblMessage.getY() - 1, 154, 83);
		textMessage.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textMessage.setLineWrap(true);
		
		JScrollPane scrollBar = new JScrollPane();
		scrollBar.setBackground(Color.LIGHT_GRAY);
		scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollBar.setBounds(textMessage.getBounds());
		scrollBar.setViewportView(textMessage);
		frame.getContentPane().add(scrollBar);
		
		casePlus.setBackground(new Color(50, 50, 50));
		casePlus.setFont(new Font("Arial", Font.PLAIN, 12));
		casePlus.setBounds(10, scrollBar.getY() + scrollBar.getHeight() + 7, casePlus.getPreferredSize().width, 23);
		frame.getContentPane().add(casePlus);
		
		casePlus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (casePlus.isSelected())
				{
					lblUser.setEnabled(true);
					textUser.setEnabled(true);
					lblMotDePasse.setEnabled(true);
					textPassword.setEnabled(true);
				}
				else
				{
					lblUser.setEnabled(false);
					textUser.setEnabled(false);
					lblMotDePasse.setEnabled(false);
					textPassword.setEnabled(false);
				}					
				
			}
			
		});
		
		lblUser = new JLabel(Shutter.language.getProperty("lblUser"));
		lblUser.setBounds(10, casePlus.getY() + casePlus.getHeight() + 7, 86, 16);
		lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
		if (casePlus.isSelected())
			lblUser.setEnabled(true);
		else
			lblUser.setEnabled(false);
		lblUser.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblUser);		
		
		textUser.setBounds(textFrom.getX(), lblUser.getY() - 1, 154, 21);
		textUser.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		if (casePlus.isSelected())
			textUser.setEnabled(true);
		else
			textUser.setEnabled(false);
		textUser.setColumns(10);
		frame.getContentPane().add(textUser);
		
		lblMotDePasse = new JLabel(Shutter.language.getProperty("lblPassword"));
		lblMotDePasse.setHorizontalAlignment(SwingConstants.RIGHT);
		if (casePlus.isSelected())
			lblMotDePasse.setEnabled(true);
		else
			lblMotDePasse.setEnabled(false);
		lblMotDePasse.setBounds(8, lblUser.getY() + lblUser.getHeight() + 17, 88, 16);
		frame.getContentPane().add(lblMotDePasse);
		
		lblMotDePasse.setFont(new Font("Montserrat", Font.PLAIN, 12));
		
		textPassword.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		if (casePlus.isSelected())
			textPassword.setEnabled(true);
		else
			textPassword.setEnabled(false);
		textPassword.setColumns(10);
		textPassword.setEchoChar('•');
		textPassword.setBounds(101, lblMotDePasse.getY() - 1, 154, 21);
		frame.getContentPane().add(textPassword);
		
		textPassword.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {				
				//CTRL + V coller
		        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
		        	PasteFromClipBoard(textPassword);
			}

			@Override
			public void keyReleased(KeyEvent e) {				
			}
		});
		
		btnOK = new JButton("OK");
		btnOK.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnOK.setBounds(99, lblMotDePasse.getY() + lblMotDePasse.getHeight() + 17, 158, 25);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				error = false;

				//Suppression du compte enregistré
				for (File f : new File(System.getProperty("user.home")).listFiles())
				{
					if (f.toString().contains(".wtclient_config"))
						f.delete();
				}
				
				if (casePlus.isSelected())
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					plusAccount();
				}
				
				if (error == false)
				{					
					if (textFrom.getText().toString().isEmpty() == false || textTo.getText().toString().isEmpty() == false
					|| (casePlus.isSelected() && textUser.getText().toString().isEmpty() == false && new String(textPassword.getPassword()).isEmpty() == false))
						btnOK.setEnabled(false);
					
					Utils.changeDialogVisibility(frame, shadow, true);
				}
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnReset.setBounds(10, btnOK.getY(), 86, 25);		
		frame.getContentPane().add(btnReset);	
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				textFrom.setEnabled(true);
				textFrom.setText(null);
				textTo.setEnabled(true);
				textTo.setText(null);
				textMessage.setEnabled(true);
				textMessage.setText(null);
				textUser.setText(null);
				textUser.setEnabled(false);
				casePlus.setSelected(false);
				textPassword.setText(null);
				textPassword.setEnabled(false);
			}
			
		});
	}

	public static void addFile(final File fichier) {
		uploadFileList.append(" " +  '"'+ fichier.toString() + '"');
	}
	
	public static void plusAccount() {	
		try {
			String PathToWTCLIENT;
			ProcessBuilder processWTCLIENT;
	
			if (System.getProperty("os.name").contains("Windows"))
			{
				PathToWTCLIENT = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToWTCLIENT = PathToWTCLIENT.substring(1,PathToWTCLIENT.length()-1);
				PathToWTCLIENT = '"' + PathToWTCLIENT.substring(0,(int) (PathToWTCLIENT.lastIndexOf("/"))).replace("%20", " ")  + "/Library/wtclient.exe" + '"';
				processWTCLIENT = new ProcessBuilder(PathToWTCLIENT + " " + '"' + "login" + '"');
			}
			else
			{
				PathToWTCLIENT = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				PathToWTCLIENT = PathToWTCLIENT.substring(0,PathToWTCLIENT.length()-1);
				PathToWTCLIENT = PathToWTCLIENT.substring(0,(int) (PathToWTCLIENT.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/wtclient";
				processWTCLIENT = new ProcessBuilder("/bin/bash", "-c" , PathToWTCLIENT + " " + '"' + "login" + '"');
			}
			
			process = processWTCLIENT.start();
													
			String line;
	        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));		
	        
	        //Error Stream
	        String errorStream;
			BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));		
			
			//Permet d'écrire dans le flux
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));			
			
			//Analyse des données	
			while ((line = br.readLine()) != null) {		
				
				Console.consoleFFMPEG.append(line + System.lineSeparator());		
	
				if (line.contains("Please enter your Plus user e-mail address"))
				{
					writer.write(textUser.getText());
					writer.write("\n");
					writer.flush();
				}
				else if (line.contains("enter your Plus password"))
				{
					writer.write(new String(textPassword.getPassword()));
					writer.write("\n");
					writer.flush();
				}	
											    
			}//While				
			
			while ((errorStream = bre.readLine()) != null) {
				Console.consoleFFMPEG.append(errorStream + System.lineSeparator());		
				if (errorStream.contains("ERROR"))
				{
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("checkIdentity"), Shutter.language.getProperty("wrongIdentity"), JOptionPane.ERROR_MESSAGE);
					error = true;
				}
			}
			
			process.waitFor();				
									
			} catch (Exception e) {
				error = true;
				System.out.println(e);
			}
	}
	
	public static void sendToWeTransfer() {	
		if (btnOK != null) 
		{
			if (btnOK.isEnabled() == false && Shutter.cancelled == false && uploadFileList.length() != 0)
			{		
						
					FFMPEG.disableAll();
					Shutter.btnStart.setEnabled(false);
					Shutter.btnAnnuler.setEnabled(true);
					
					wetransferAdress = "";
					Shutter.cancelled = false;
					error = false;				
					isRunning = true;
					
					Shutter.progressBar1.setValue(0);
			        Shutter.progressBar1.setMaximum(100);		
			        
				    Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
			        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingFile") + " WeTransfer");
									        
					try {
						String PathToWTCLIENT;
						ProcessBuilder processWTCLIENT;
						
						String cmd;
						if (casePlus.isSelected())
						{
							cmd = " upload" + uploadFileList.toString()
								+ " --from=" + textFrom.getText() 
								+ " --to=" + textTo.getText().replace(" ", ",")
								+ " --message=" + '"' + textMessage.getText() + System.lineSeparator() + System.lineSeparator() + Shutter.language.getProperty("sentFrom") + '"';
						}
						else
							cmd = " upload" + uploadFileList.toString();
						
						Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("command") + " " + cmd + System.lineSeparator() + System.lineSeparator());
						
						if (System.getProperty("os.name").contains("Windows"))
						{
							PathToWTCLIENT = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToWTCLIENT = PathToWTCLIENT.substring(1,PathToWTCLIENT.length()-1);
							PathToWTCLIENT = '"' + PathToWTCLIENT.substring(0,(int) (PathToWTCLIENT.lastIndexOf("/"))).replace("%20", " ")  + "/Library/wtclient.exe" + '"';
							processWTCLIENT = new ProcessBuilder(PathToWTCLIENT + " " + cmd);
						}
						else
						{
							PathToWTCLIENT = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							PathToWTCLIENT = PathToWTCLIENT.substring(0,PathToWTCLIENT.length()-1);
							PathToWTCLIENT = PathToWTCLIENT.substring(0,(int) (PathToWTCLIENT.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/wtclient";
							processWTCLIENT = new ProcessBuilder("/bin/bash", "-c" , PathToWTCLIENT + " " + cmd);
						}
												
						process = processWTCLIENT.start();
						
						String url;
				        InputStreamReader isr = new InputStreamReader(process.getInputStream());
				        BufferedReader br = new BufferedReader(isr);		
													
						String line;
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));							

						//Analyse des données	
						while ((line = input.readLine()) != null) {	
														
							Console.consoleFFMPEG.append(line + System.lineSeparator());							
							
							if (line.contains("%"))
							{
								String s[] = line.split("%");
								String s2[] = s[0].split(" ");
								String s3[] = s2[(s2.length - 1)].split("\\.");
								Shutter.progressBar1.setValue(Integer.parseInt(s3[0]));
							}
														
							if (line.contains("ERROR"))
								error = true;
						    
						}//While		
						
						//Récupération de l'adresse http
						while ((url = br.readLine()) != null) {							
							
							Console.consoleFFMPEG.append(url + System.lineSeparator());	

							if (url.contains("http"))
							{								
								wetransferAdress = url.substring(url.lastIndexOf(" ") + 1);								
								Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(wetransferAdress), null);
							}
						}
						
						process.waitFor();	
						
						if (casePlus.isSelected() == false && Shutter.cancelled == false)
							sendMailForWT(error);
												
						} catch (IOException | InterruptedException e) {
							error = true;
						} finally {
							isRunning = false;
							
							FFMPEG.enableAll();
							uploadFileList.setLength(0);
							
							if (error == false)
						    {
					        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingWTSuccessful"));
								Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
						    }
						    else
						    {
								Shutter.lblEncodageEnCours.setForeground(Color.RED);
					        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingWTFailed"));
								Shutter.progressBar1.setValue(0);
						    }
								
							if (Shutter.cancelled)
							{
						    	Shutter.lblEncodageEnCours.setForeground(Color.RED);
					        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingWTCancelled"));
								Shutter.progressBar1.setValue(0);
							}
						}
								
					}					              
			}
		}//BtnOK
	
	public static void sendMailForWT(boolean error) {
	
		Thread thread = new Thread(new Runnable(){
			public void run() {
				
				StringBuilder adresses = new StringBuilder();
				
				adresses.append(textFrom.getText() + System.lineSeparator());	
				
				if (textTo.getText().contains(" "))
				{
					for (String add : textTo.getText().split(" "))
					{
						adresses.append(add + System.lineSeparator());
					}				
				}
				else if (textTo.getText().contains(","))
				{
					for (String add : textTo.getText().split(","))
					{
						adresses.append(add + System.lineSeparator());
					}
				}
				else
					adresses.append(textTo.getText() + System.lineSeparator());
						
				for (String adds : adresses.toString().split("\\r?\\n"))
				{					
					Shutter.sendMailIsRunning = true;
					final String username = "info@shutterencoder.com";
					final String password = "***ENCRYPTED***";
	
					Properties props = new Properties();
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", "auth.smtp.1and1.fr");
					props.put("mail.smtp.port", "587");
	
					Session session = Session.getInstance(props,
					  new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					  });
	
					try {
						Message message = new MimeMessage(session);
						message.setFrom(new InternetAddress(username));
						message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(adds));
						if (error)
						{
							message.setSubject("WeTransfer " + textFrom.getText());
							message.setText(Shutter.language.getProperty("linkError") + System.lineSeparator() + System.lineSeparator() + textMessage.getText());					
						}
						else
						{
							message.setSubject("WeTransfer " + textFrom.getText());
							message.setText(textMessage.getText() + System.lineSeparator() + System.lineSeparator() + Shutter.language.getProperty("link") + System.lineSeparator() + wetransferAdress);
						}
						
						Transport.send(message);						
						
					    Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
				        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailSuccessful"));
					} catch (MessagingException e) {					
						Shutter.lblEncodageEnCours.setForeground(Color.RED);
			        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailFailed"));
			        	
						if (error == false)
						{
							try {
								Thread.sleep(3000);
							
								if (wetransferAdress != "")
								{
									Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
							        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("linkCopied"));
								}
							
								Thread.sleep(3000);
							} catch (Exception e1) {}
						}
					}
					finally {
						Shutter.sendMailIsRunning = false;
					}
				}
			}
		});
		thread.start();
	}
	
	private void PasteFromClipBoard(JComponent component){
		if (System.getProperty("os.name").contains("Mac"))
		{
    	   Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
           Transferable clipTf = sysClip.getContents(null);
           if (clipTf != null) {
               if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                   try {
                	   ((JTextComponent) component).setText((String) clipTf.getTransferData(DataFlavor.stringFlavor));
                   } catch (Exception er) {}
               }
           }
		}
	}

	private void setShadow() {
		shadow = new JDialog();
		shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
    	shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
    	shadow.setUndecorated(true);
    	shadow.setAlwaysOnTop(true);
    	shadow.setContentPane(new WetransferShadow());
    	shadow.setBackground(new Color(255,255,255,0));
		
		shadow.setFocusableWindowState(false);
		
		shadow.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent down) {
				frame.toFront();
			}
    		
    	});

    	frame.addComponentListener(new ComponentAdapter() {
 		    public void componentMoved(ComponentEvent e) {
 		        shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
 		    }
 		});
 		
	}
}

//Ombre
@SuppressWarnings("serial")
class WetransferShadow extends JPanel {
public void paintComponent(Graphics g){
	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  Graphics2D g1 = (Graphics2D)g.create();
	  g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
	  g1.setRenderingHints(qualityHints);
	  g1.setColor(new Color(0,0,0));
	  g1.fillRect(0,0,Wetransfer.frame.getWidth() + 14, Wetransfer.frame.getHeight() + 7);
	  
	  for (int i = 0 ; i < 7; i++) 
	  {
		  Graphics2D g2 = (Graphics2D)g.create();
		  g2.setRenderingHints(qualityHints);
		  g2.setColor(new Color(0,0,0, i * 10));
		  g2.drawRoundRect(i, i, Wetransfer.frame.getWidth() + 13 - i * 2, Wetransfer.frame.getHeight() + 7, 20, 20);
	  }
}
}