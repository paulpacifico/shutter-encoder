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
import java.awt.Component;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import library.FFMPEG;

public class Ftp {
	public static JDialog frame;
	public static JDialog shadow;
	public static FTPClient ftp;
	public static boolean isRunning = false;
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel panelHaut;
	private JLabel topImage;	
	private static JTextField textFtp = new JTextField();
	private JLabel lblFtp;
	private JLabel lblUtilisateur;
	private static JTextField textUser = new JTextField();
	private JLabel lblMotDePasse;
	private static JPasswordField textPassword = new JPasswordField();
	public static JButton btnOK; //Si le bouton est disable alors la connexion est établie
	private JButton btnReset;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public Ftp() {
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameFtp"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(267, 185);
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
		grpFtp();
		
		frame.getRootPane().setDefaultButton(btnOK);	
			
		Utils.changeDialogVisibility(Ftp.frame, Ftp.shadow, false);		
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

		JLabel title = new JLabel(Shutter.language.getProperty("frameFtp"));
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

	private void grpFtp() {
		lblFtp = new JLabel(Shutter.language.getProperty("lblFtp"));
		lblFtp.setBounds(10, 56, 86, 16);
		lblFtp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFtp.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblFtp);		

		textFtp.setBounds(101, 55, 154, 21);
		frame.getContentPane().add(textFtp);
		textFtp.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textFtp.setColumns(10);		
		
		textFtp.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {				
				//CTRL + V coller
		        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
		        	PasteFromClipBoard(textFtp);
			}

			@Override
			public void keyReleased(KeyEvent e) {				
			}
		});
		
		textUser.setBounds(101, 88, 154, 21);
		textUser.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textUser.setColumns(10);
		frame.getContentPane().add(textUser);
		
		textUser.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {				
				//CTRL + V coller
		        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) || (e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) 
		        	PasteFromClipBoard(textUser);
			}

			@Override
			public void keyReleased(KeyEvent e) {				
			}
		});
		
		lblUtilisateur = new JLabel(Shutter.language.getProperty("lblUser"));
		lblUtilisateur.setBounds(10, 89, 86, 16);
		lblUtilisateur.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUtilisateur.setFont(new Font("Montserrat", Font.PLAIN, 12));
		frame.getContentPane().add(lblUtilisateur);		
			
		lblMotDePasse = new JLabel(Shutter.language.getProperty("lblPassword"));
		lblMotDePasse.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMotDePasse.setBounds(8, 123, 88, 16);
		frame.getContentPane().add(lblMotDePasse);
		
		lblMotDePasse.setFont(new Font("Montserrat", Font.PLAIN, 12));
		
		textPassword.setFont(new Font("Arial Unicode MS", Font.PLAIN, 12));
		textPassword.setColumns(10);
		textPassword.setEchoChar('•');
		textPassword.setBounds(101, 122, 154, 21);
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
		btnOK.setBounds(99, textPassword.getX() + textPassword.getHeight() + 30, 158, 25);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				btnOK.setEnabled(false);
				
				//Test de connexion
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				try {
					
						String getFtpFolder = textFtp.getText().replace("ftp://", "");
						String ftpFolder = "/";
						if (getFtpFolder.contains("/"))
							ftpFolder = getFtpFolder.substring(getFtpFolder.indexOf("/"));	 
						
						ftp = new FTPClient();
						ftp.connect(textFtp.getText().replace("ftp://", "").replace(ftpFolder, ""));
						
						boolean login = ftp.login(textUser.getText(), new String(textPassword.getPassword()));		
						if (login == false)
						{
							JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("checkIdentity"), Shutter.language.getProperty("wrongIdentity"), JOptionPane.ERROR_MESSAGE);
							for (Component component : frame.getContentPane().getComponents())
							{
								component.setEnabled(true);
							}
						}
						else
							Utils.changeDialogVisibility(frame, shadow, true);
				} catch (IOException er) {
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("connectionRefused"), Shutter.language.getProperty("connectionError"), JOptionPane.ERROR_MESSAGE);
					for (Component component : frame.getContentPane().getComponents())
					{
						component.setEnabled(true);
					}
				}		
				
				btnReset.setEnabled(true);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		btnReset = new JButton("Reset");
		btnReset.setFont(new Font("Montserrat", Font.PLAIN, 12));
		btnReset.setBounds(10, textPassword.getX() + textPassword.getHeight() + 30, 86, 25);		
		frame.getContentPane().add(btnReset);	
		
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				textFtp.setText(null);
				textUser.setText(null);
				textPassword.setText(null);
				for (Component component : frame.getContentPane().getComponents())
				{
					component.setEnabled(true);
				}
			}
			
		});
	}

	private void PasteFromClipBoard(JComponent component){
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
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

	public static void sendToFtp(final File fichier) {
	if (btnOK != null) 
	{
		if (btnOK.isEnabled() == false && Shutter.cancelled == false)
		{			
				FFMPEG.disableAll();
				FFMPEG.btnStart.setEnabled(false);
				FFMPEG.btnAnnuler.setEnabled(true);
			
				Shutter.cancelled = false;
				boolean uploaded = false;
				isRunning = true;
				InputStream input = null;
				
				String getFtpFolder = textFtp.getText().replace("ftp://", "");
				String ftpFolder = "/";
				if (getFtpFolder.contains("/"))
					ftpFolder = getFtpFolder.substring(getFtpFolder.indexOf("/"));	 					
								
				 try {
					 ftp.connect(textFtp.getText().replace("ftp://", "").replace(ftpFolder, ""));
					 ftp.login(textUser.getText(), new String(textPassword.getPassword()));
					 ftp.setFileType(FTP.BINARY_FILE_TYPE);
					 ftp.enterLocalPassiveMode();

					 input = new FileInputStream(fichier);
			        	
				    Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
			        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingFile") + " " + fichier.getName());

			        Shutter.progressBar1.setMaximum((int) fichier.length());
			        	
					Shutter.btnAnnuler.setEnabled(true);
						   
			        ftp.setCopyStreamListener(new CopyStreamAdapter() {
						 @Override
						 public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
							 Shutter.progressBar1.setValue((int)(totalBytesTransferred));
						 }		
					 });
				   
					
					//Ajout du dernier slash
					if (ftpFolder.substring(ftpFolder.length() - 1).equals("/") == false)
						ftpFolder = ftpFolder + "/";	
					
			        uploaded = ftp.storeFile(ftpFolder + fichier.getName(), input);
				    		
				  } catch (IOException e) {}
				 	finally {
			            try {
						    input.close();
			                ftp.logout();
			                ftp.disconnect();
			            } catch (IOException ex) {}	
			            
					    if (uploaded)
					    {
				        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingSuccessful"));
							Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
					    }
					    else
					    {
					    	Shutter.lblEncodageEnCours.setForeground(Color.RED);
				        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingFailed"));
							Shutter.progressBar1.setValue(0);
					    }
					  
					    isRunning = false;
						FFMPEG.enableAll();
						
				    	  if (Shutter.cancelled)
				    	  {
						    	Shutter.lblEncodageEnCours.setForeground(Color.RED);
					        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("sendingCancelled"));
								Shutter.progressBar1.setValue(0);
				    	  }
					    
					  //Envoi du mail de confirmation
					  sendMailForFtp(uploaded, fichier.toString());
				 	}//End Try
				}
		}//BtnOK
	}
	
	public static void sendMailForFtp(final boolean uploaded, final String fichier) {
		if (Shutter.caseSendMail.isSelected())
		{	       
		Thread thread = new Thread(new Runnable(){
			public void run() {
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
					InternetAddress.parse(Shutter.textMail.getText()));
					if (uploaded == false)
					{
						message.setSubject(Shutter.language.getProperty("shutterMailFailed"));
						message.setText(Shutter.language.getProperty("theFile") + " " + new File(fichier).getName() + " " + Shutter.language.getProperty("notSended") + " " + textFtp.getText());
					}
					else
					{
						message.setSubject(Shutter.language.getProperty("shutterMailSuccessful"));
						message.setText(Shutter.language.getProperty("theFile") + " " + new File(fichier).getName() + " " + Shutter.language.getProperty("isSended") + " " + textFtp.getText());
					}
					
					Transport.send(message);
					
					Shutter.sendMailIsRunning = false;
				    Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
			        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailSuccessful"));
				} catch (MessagingException e) {
					Shutter.lblEncodageEnCours.setForeground(Color.RED);
		        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailFailed"));
				}		
			}
		});
		thread.start();
		
	   }
	}

	private void setShadow() {
		shadow = new JDialog();
		shadow.setSize(frame.getSize().width + 14, frame.getSize().height + 7);
    	shadow.setLocation(frame.getLocation().x - 7, frame.getLocation().y - 7);
    	shadow.setUndecorated(true);
    	shadow.setAlwaysOnTop(true);
    	shadow.setContentPane(new FtpShadow());
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
class FtpShadow extends JPanel {
public void paintComponent(Graphics g){
	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  Graphics2D g1 = (Graphics2D)g.create();
	  g1.setComposite(AlphaComposite.SrcIn.derive(0.0f));
	  g1.setRenderingHints(qualityHints);
	  g1.setColor(new Color(0,0,0));
	  g1.fillRect(0,0,Ftp.frame.getWidth() + 14, Ftp.frame.getHeight() + 7);
	  
	  for (int i = 0 ; i < 7; i++) 
	  {
		  Graphics2D g2 = (Graphics2D)g.create();
		  g2.setRenderingHints(qualityHints);
		  g2.setColor(new Color(0,0,0, i * 10));
		  g2.drawRoundRect(i, i, Ftp.frame.getWidth() + 13 - i * 2, Ftp.frame.getHeight() + 7, 20, 20);
	  }
}
}