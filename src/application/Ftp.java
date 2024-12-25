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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;

public class Ftp {
	public static JDialog frame;
	public static FTPClient ftp;
	public static boolean isRunning = false;
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	public static JTextField textFtp = new JTextField();
	private JLabel lblFtp;
	private JLabel lblUtilisateur;
	public static JTextField textUser = new JTextField();
	private JLabel lblMotDePasse;
	private static JPasswordField textPassword = new JPasswordField();
	public static JButton btnOK; //Si le bouton est disable alors la connexion est établie
	private JButton btnReset;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	public Ftp() {
		
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameFtp"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null); 
		frame.setSize(267, 165);
		frame.setResizable(false);
		frame.setModal(true);
		frame.setAlwaysOnTop(true);		
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	        shape1.add(shape2);
			frame.setShape(shape1);
			frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
			frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
			
		}
						
		topPanel();
		grpFtp();
		
		frame.getRootPane().setDefaultButton(btnOK);	
			
		Utils.changeDialogVisibility(Ftp.frame, false);		
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setLayout(null);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 267, 28);
		
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
					quit.setIcon(new FlatSVGIcon("contents/quit.svg", 15, 15));
					Utils.changeDialogVisibility(frame, true);
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

		JLabel title = new JLabel(Shutter.language.getProperty("frameFtp"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, -1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 16));
		topPanel.add(title);
		
		topImage = new JLabel();	
		topImage.setBackground(new Color(35,35,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);

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

	private void grpFtp() {
		
		lblFtp = new JLabel(Shutter.language.getProperty("lblFtp"));
		lblFtp.setBounds(10, 36, 90, 16);
		lblFtp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFtp.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		frame.getContentPane().add(lblFtp);		

		textFtp.setBounds(101, lblFtp.getY() - 1, 154, 21);
		textFtp.setName("textFtp");
		frame.getContentPane().add(textFtp);
		textFtp.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textFtp.setColumns(10);		
								
		lblUtilisateur = new JLabel(Shutter.language.getProperty("lblUser"));
		lblUtilisateur.setBounds(10, 69, 90, 16);
		lblUtilisateur.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUtilisateur.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		frame.getContentPane().add(lblUtilisateur);		
			
		textUser.setBounds(101, lblUtilisateur.getY() - 1, 154, 21);
		textUser.setName("textUser");
		textUser.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textUser.setColumns(10);
		frame.getContentPane().add(textUser);
		
		lblMotDePasse = new JLabel(Shutter.language.getProperty("lblPassword"));
		lblMotDePasse.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMotDePasse.setBounds(8, 103, 92, 16);
		frame.getContentPane().add(lblMotDePasse);
		
		lblMotDePasse.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		
		textPassword.setFont(new Font("SansSerif", Font.PLAIN, 12));
		textPassword.setColumns(10);
		textPassword.setEchoChar('•');
		textPassword.setBounds(101, lblMotDePasse.getY() - 1, 154, 21);
		frame.getContentPane().add(textPassword);
				
		btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnReset.setBounds(12, textPassword.getY() + textPassword.getHeight() + 12, frame.getWidth() / 2 - 12, 21);		
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
		
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOK.setBounds(btnReset.getX() + btnReset.getWidth() + 4, btnReset.getY(), frame.getWidth() / 2 - 12 - 4, 21);		
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
							Utils.changeDialogVisibility(frame, true);
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
	}

	public static void sendToFtp(final File fichier) {
	if (btnOK != null) 
	{
		if (btnOK.isEnabled() == false && Shutter.cancelled == false)
		{			
				FFMPEG.disableAll();
				FFMPEG.btnStart.setEnabled(false);
				FFMPEG.btnCancel.setEnabled(true);
			
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
			        	
				    Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
			        Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("sendingFile") + " " + fichier.getName());

			        Shutter.progressBar1.setMaximum((int) fichier.length());
			        	
					Shutter.btnCancel.setEnabled(true);
						   
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
				        	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("sendingSuccessful"));
							Shutter.progressBar1.setValue(Shutter.progressBar1.getMaximum());
					    }
					    else
					    {
					    	Shutter.lblCurrentEncoding.setForeground(Color.RED);
				        	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("sendingFailed"));
							Shutter.progressBar1.setValue(0);
					    }
					  
					    isRunning = false;
						FFMPEG.enableAll();
						
				    	  if (Shutter.cancelled)
				    	  {
						    	Shutter.lblCurrentEncoding.setForeground(Color.RED);
					        	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("sendingCancelled"));
								Shutter.progressBar1.setValue(0);
				    	  }
					    
					  //Envoi du mail de confirmation
					  sendMailForFtp(uploaded, fichier.toString());
				 	}
				}
		}
	}
	
	public static void sendMailForFtp(final boolean uploaded, final String file) {
		
		if (Shutter.caseSendMail.isSelected())
		{	       
			Thread thread = new Thread(new Runnable()
			{			
				public void run() {
					
					Shutter.sendMailIsRunning = true;
	
					Properties props = new Properties();
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", "auth.smtp.1and1.fr");
					props.put("mail.smtp.port", "587");
	
					Session session = Session.getInstance(props, new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(Utils.username, Utils.password);
						}
					});
	
					try {
						Message message = new MimeMessage(session);
						message.setFrom(new InternetAddress(Utils.username));
						message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(Shutter.textMail.getText()));
						if (uploaded == false)
						{
							message.setSubject(Shutter.language.getProperty("shutterMailFailed"));
							message.setText(Shutter.language.getProperty("theFile") + " " + new File(file).getName() + " " + Shutter.language.getProperty("notSended") + " " + textFtp.getText());
						}
						else
						{
							message.setSubject(Shutter.language.getProperty("shutterMailSuccessful"));
							message.setText(Shutter.language.getProperty("theFile") + " " + new File(file).getName() + " " + Shutter.language.getProperty("isSended") + " " + textFtp.getText());
						}
						
						Transport.send(message);
						
						Shutter.sendMailIsRunning = false;
					    Shutter.lblCurrentEncoding.setForeground(Color.LIGHT_GRAY);
				        Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("mailSuccessful"));
				        
					} catch (MessagingException e) {
						
						Shutter.lblCurrentEncoding.setForeground(Color.RED);
			        	Shutter.lblCurrentEncoding.setText(Shutter.language.getProperty("mailFailed"));
					}		
				}
			});
			thread.start();
		
	   }
	}
}