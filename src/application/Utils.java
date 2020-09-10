package application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import library.FFMPEG;

public class Utils extends Shutter {
	
	public static String getTheme = null;
	
	public static void changeFrameVisibility(final JFrame f, final boolean isVisible) {

		if (isVisible == false) {
			f.setVisible(true);
		} else {
			f.setVisible(false);
		}

	}

	public static void changeDialogVisibility(final JDialog f, final boolean isVisible) {

		if (isVisible == false) {
			f.setVisible(true);
		} else {
			f.setVisible(false);
		}

	}
	
	public static void sendMail(final String fichier) {
		if (caseSendMail.isSelected()) {
			Thread thread = new Thread(new Runnable() {
				
				public void run() {
					sendMailIsRunning = true;
					final String username = "info@shutterencoder.com";
					final String password = "shutterencodermail";

					Properties props = new Properties();
					props.put("mail.smtp.auth", "true");
					props.put("mail.smtp.starttls.enable", "true");
					props.put("mail.smtp.host", "auth.smtp.1and1.fr");
					props.put("mail.smtp.port", "587");

					Session session = Session.getInstance(props, new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});
					
					try {
						Message message = new MimeMessage(session);
						message.setFrom(new InternetAddress(username));
						message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(textMail.getText()));
						if (FFMPEG.error) {
							message.setSubject(Shutter.language.getProperty("shutterEncodingError"));
							message.setText(fichier + " " + Shutter.language.getProperty("notEncoded"));
						} else {
							message.setSubject(Shutter.language.getProperty("shutterEncodingCompleted"));
							if (caseChangeFolder3.isSelected())
								message.setText(fichier + " " + Shutter.language.getProperty("isEncoded") + " "	+ lblDestination1.getText() + " | " + lblDestination2.getText() + " | " + lblDestination3.getText());
							else if (caseChangeFolder2.isSelected())
								message.setText(fichier + " " + Shutter.language.getProperty("isEncoded") + " "	+ lblDestination1.getText() + " | " + lblDestination2.getText());
							else
								message.setText(fichier + " " + Shutter.language.getProperty("isEncoded") + " "	+ lblDestination1.getText());
						}

						Transport.send(message);						
						
					    Shutter.lblEncodageEnCours.setForeground(Color.LIGHT_GRAY);
				        Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailSuccessful"));
					} catch (MessagingException e) {
						Console.consoleFFMPEG.append(System.lineSeparator() + e + System.lineSeparator());
						
						Shutter.lblEncodageEnCours.setForeground(Color.RED);
			        	Shutter.lblEncodageEnCours.setText(Shutter.language.getProperty("mailFailed"));
						Shutter.progressBar1.setValue(0);
					} finally {
						sendMailIsRunning = false;
					}
				}
			});
			thread.start();

		}
	}

	public static void copyFile(File fichier) {		
		//Destination 2
		if (caseChangeFolder2.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(1);
			File filein  = fichier;
	        File fileout = new File(lblDestination2.getText() + "/" + fichier.getName());
			try {		
		        long length  = filein.length();
				progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
		
		//Destination 3
		if (caseChangeFolder3.isSelected())
		{
			btnStart.setEnabled(false);
			grpDestination.setSelectedIndex(2);
			File filein  = fichier;
	        File fileout = new File(lblDestination3.getText() + "/" + fichier.getName());
			try {		
		        long length  = filein.length();
		        progressBar1.setMaximum((int) length);
		        long counter = 0;
		        int r = 0;
		        byte[] b = new byte[1024];
				FileInputStream fin = new FileInputStream(filein);
		        FileOutputStream fout = new FileOutputStream(fileout);
		        copyFileIsRunning = true;
	                while( (r = fin.read(b)) != -1) 
	                {	                	
                        counter += r;
                        progressBar1.setValue((int) counter);
                        fout.write(b, 0, r);
                        
                        if (cancelled)
	                		break;
	                }
	                fin.close();
	                fout.close();
	                
	                if (cancelled)
	                	fileout.delete();
				}
		        catch(Exception e){
		        	copyFileIsRunning = false;
		        	fileout.delete();
		        }
			copyFileIsRunning = false;
			btnStart.setEnabled(true);
		}
	}

	public static File UNCPath(File file) {		
		
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	try {
			String root[] = file.toString().substring(2, file.toString().length()).split("\\\\");
			String rootFolder = "\\\\" + root[0] + "\\" + root[1];
			
			//Récupération du nom et label des disques connectés
	        for (File path:File.listRoots()) 
	        {
		         FileSystemView view = FileSystemView.getFileSystemView();
		         File dir = new File(path.toString());
		         
		         String name = view.getSystemDisplayName(dir);
		         
		         //Si le disque réseau contient le dossier root du fichier
		         if (name.contains(root[1])) 
			         return new File (file.toString().replace(rootFolder, path.toString().substring(0, path.toString().length() - 1)));
	        }         
	        
	    	//Si le disque n'existe pas alors on le map
			Process process = Runtime.getRuntime().exec("net.exe use * " + '"' + rootFolder + '"');
			
			BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(process.getInputStream()));
			
			String line;
			while ((line = stdInput.readLine()) != null) {
			    if (line.contains(":"))
			    {
			    	String s[] = line.split(":");
			    	String d[] = s[0].split(" ");
					return new File(file.toString().replace(rootFolder, d[d.length - 1] + ":"));
			    }
			}
	         
		} catch (IOException e) {			 
		} finally {			
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	
		return file;
	}
	
	public static String nombreDeFichiers() {
		if (scanIsRunning) {
			return "Scan...";
		}

		String nomDuLabel;
		int total = liste.getSize();
		if (total > 1 && total < 1000)
			nomDuLabel = total + " " + language.getProperty("files");
		else if (total <= 1)
			nomDuLabel = total + " " + language.getProperty("file");
		else
			nomDuLabel = total / 1000 + "k " + language.getProperty("files");
		return nomDuLabel;
	}

	public static void FileFinder(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				FileFinder(f.getAbsolutePath());
			} else {
				int s = f.getAbsoluteFile().toString().lastIndexOf('.');
				String ext = f.getAbsoluteFile().toString().substring(s);
				if (f.getAbsoluteFile().toString().substring(s).toLowerCase()
						.equals(comboFilter.getSelectedItem().toString())
						|| comboFilter.getSelectedItem().toString().equals(language.getProperty("aucun"))
						|| lblFilter.getText().equals(language.getProperty("lblFilter")) == false) {
					if (ext.equals(".enc")) {
						loadSettings(new File (f.getAbsoluteFile().toString()));
					}
					if (f.isHidden() == false && f.getName().contains("."))
						liste.addElement(f.getAbsoluteFile().toString());
				}
			}
		}
		lblFichiers.setText(nombreDeFichiers());
	}

	public static File scanFolder(String folder) {
		progressBar1.setIndeterminate(true);
		lblEncodageEnCours.setText(language.getProperty("waitingFiles"));
		tempsRestant.setVisible(false);

		disableAll();

		File actualScanningFile = null;
		do {
			File dir = new File(folder);
			btnStart.setEnabled(false);

			for (File f : dir.listFiles()) // Récupère chaque fichier du dossier
			{
				if (f.isHidden() || f.isFile() == false)
					continue;

				actualScanningFile = f;

				// Lorque un fichier est entrain d'être copié
				progressBar1.setIndeterminate(true);
				
				long fileSize = 0;		
				do {
					fileSize = f.length();
					try {
						Thread.sleep(3000); // Permet d'attendre la nouvelle valeur de la copie
					} catch (InterruptedException e) {}
				} while (fileSize != f.length());

				// pour Windows
				while (f.renameTo(f) == false) {
					if (f.exists() == false) // Dans le cas où on annule la copie en cours
						break;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}

				if (actualScanningFile != null)
					return actualScanningFile;
			} // End for
		} while (scanIsRunning);

		// Action de fin
		progressBar1.setIndeterminate(false);
		enableAll();
		btnEmptyList.doClick();

		return null;
	}

	public static File fileReplacement(String path, String file, String oldExt, String surname, String newExt) {
		
		int n = 1;
		File fileOut = new File(path + "/" + file.replace(oldExt, surname.substring(0, surname.length() - 1) + newExt));
		
		//Nom identique à la source
		if (file.equals(file.replace(oldExt, surname.substring(0, surname.length() - 1) + newExt)) && caseChangeFolder1.isSelected() == false)
		{
			do {
				fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
				n++;
			} while (fileOut.exists());
		}
		else
		{
			int q = JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("eraseFile"), Shutter.language.getProperty("File") + " " + fileOut.getName() + " " + Shutter.language.getProperty("alreadyExist"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (q == JOptionPane.NO_OPTION)
			{
				do {
					fileOut = new File(path + "/" + file.replace(oldExt, surname + n + newExt));
					n++;
				} while (fileOut.exists());
			}
			else if (q == JOptionPane.CANCEL_OPTION)
				return null;	
		}
			
		return fileOut;				
	}

	public static void moveScannedFiles(String fichier)
	{
		File folder = new File(liste.getElementAt(0) + "completed");
		
		//Si erreur
		if (FFMPEG.error || cancelled)
			folder = new File(liste.getElementAt(0) + "error");
		
		if (folder.exists() == false)
			folder.mkdir();
		
		File fileToMove = new File(folder + "/" + fichier);
		
		// Récupère le fichier du dossier
		for (int i = 0 ; i < liste.getSize() ; i++)
		{						
			File getFile = new File(liste.getElementAt(i) + fichier);
			
			if (getFile.exists()) //Si le fichier correspond on le déplace dans le dossier
			{
					if (fileToMove.exists()) //Nom identique à la source
					{
						int n = 1;
						
						String ext =  fichier.substring(fichier.lastIndexOf("."));
						
						do {
							fileToMove = new File(folder + "/" + fichier.replace(ext, "") + "_" + n + ext);
							n++;
						} while (fileToMove.exists());
					}
					
					//Déplacement du fichier
					getFile.renameTo(fileToMove);
					
					break;	
			}
		}
	}
	
	public static String fichiersTermines(int nombre) {
		String nomDuLabel;
		if (nombre > 1 && nombre < 1000)
			nomDuLabel = nombre + " " + Shutter.language.getProperty("filesEnded");
		else if( nombre <= 1)
			nomDuLabel = nombre + " " +  Shutter.language.getProperty("fileEnded");
		else
			nomDuLabel = nombre / 1000 + "k " + Shutter.language.getProperty("filesEnded");		
		return nomDuLabel;
	}
		
	@SuppressWarnings({"rawtypes"})
	public static void saveSettings(boolean update) {
		
		File oldFolder = new File(Functions.fonctionsFolder.toString().replace("Functions", "Fonctions"));
		if (oldFolder.exists())
		{
			oldFolder.renameTo(Functions.fonctionsFolder);
		}
		
		if (Functions.fonctionsFolder.exists() == false)
			Functions.fonctionsFolder.mkdir();
		
		
		FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveSettings"), FileDialog.SAVE);
		dialog.setDirectory(Functions.fonctionsFolder.toString()); 
		dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
		dialog.setAlwaysOnTop(true);
	
		if (update == false)
			dialog.setVisible(true);
		else
			dialog.setFile("/" + Functions.listeDeFonctions.getSelectedValue()); //le "/" Contourne un bug							
		
		 if (dialog.getFile() != null)
		 { 
			try {
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
				Document document = documentBuilder.newDocument();
				
				Element root = document.createElement("Shutter");
				document.appendChild(root);
				
				Element settings = document.createElement("Settings");
				
				for (Component c : frame.getContentPane().getComponents())
				{
					if (c instanceof JPanel)
					{
						for (Component p : ((JPanel) c).getComponents())
						{
							if (p.getName() != "" && p.getName() != null)
							{
								if (p instanceof JLabel)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JLabel"));
									component.appendChild(cType);
									
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JLabel) p).getText()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JRadioButton)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JRadioButton"));
									component.appendChild(cType);
									
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JRadioButton) p).isSelected())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JComboBox)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JComboBox"));
									component.appendChild(cType);
																		
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JComboBox) p).getSelectedItem().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JTextField)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JTextField"));
									component.appendChild(cType);
									
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JSlider)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JSlider"));
									component.appendChild(cType);
	
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JSlider) p).getValue())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
								else if (p instanceof JSpinner)
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JSpinner"));
									component.appendChild(cType);
		
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(String.valueOf(((JSpinner) p).getValue())));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									settings.appendChild(component);
								}
							}
						}
					}
				}
				
				root.appendChild(settings);
				
				Element player = document.createElement("Player");
				
				if (caseInAndOut.isSelected())
				{
					for (Component c : VideoPlayer.frame.getContentPane().getComponents())
					{
						if (c instanceof JPanel)
						{
							for (Component p : ((JPanel) c).getComponents())
							{
								if (p.getName() != "" && p.getName() != null)
								{
									if (p.getName().contains("caseIn") && VideoPlayer.sliderIn.getValue() > 0 || p.getName().contains("caseOut") && VideoPlayer.sliderOut.getValue() != VideoPlayer.sliderOut.getMaximum())
									{
										if (p instanceof JTextField)
										{
											//Component
											Element component = document.createElement("Component");
											
											//Type
											Element cType = document.createElement("Type");
											cType.appendChild(document.createTextNode("JTextField"));
											component.appendChild(cType);
											
											//Name
											Element cName = document.createElement("Name");
											cName.appendChild(document.createTextNode(p.getName()));
											component.appendChild(cName);
											
											//Value
											Element cValue = document.createElement("Value");
											cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
											component.appendChild(cValue);
											
											//State
											Element cState = document.createElement("Enable");
											cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
											component.appendChild(cState);
											
											//Visible
											Element cVisible = document.createElement("Visible");
											cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
											component.appendChild(cVisible);		
											
											player.appendChild(component);
										}
									}
								}								
							}
						}						
					}
					
				root.appendChild(player);
				}
				
				Element color = document.createElement("Color");
				
				if (caseColor.isSelected())
				{
					for (Component p : ColorImage.frame.getContentPane().getComponents())
					{
						if (p.getName() != "" && p.getName() != null)
						{	
							if (p instanceof JSlider)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JSlider"));
								component.appendChild(cType);
	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JSlider) p).getValue())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								color.appendChild(component);
							}
						}												
					}
					
					//Sauvegarde des valeurs	
						
					//allR
					Element componentallR = document.createElement("Component");
					
					//Type
					Element cTypeallR = document.createElement("Type");
					cTypeallR.appendChild(document.createTextNode("String"));
					componentallR.appendChild(cTypeallR);
	
					//Name
					Element cNameallR = document.createElement("Name");
					cNameallR.appendChild(document.createTextNode("allR"));
					componentallR.appendChild(cNameallR);
					
					//Value
					Element cValueallR = document.createElement("Value");
					cValueallR.appendChild(document.createTextNode(String.valueOf(ColorImage.allR)));
					componentallR.appendChild(cValueallR);
	
					color.appendChild(componentallR);
					
					//allG
					Element componentallG = document.createElement("Component");
					
					//Type
					Element cTypeallG = document.createElement("Type");
					cTypeallG.appendChild(document.createTextNode("String"));
					componentallG.appendChild(cTypeallG);
	
					//Name
					Element cNameallG = document.createElement("Name");
					cNameallG.appendChild(document.createTextNode("allG"));
					componentallG.appendChild(cNameallG);
					
					//Value
					Element cValueallG = document.createElement("Value");
					cValueallG.appendChild(document.createTextNode(String.valueOf(ColorImage.allG)));
					componentallG.appendChild(cValueallG);
	
					color.appendChild(componentallG);
					
					//allB
					Element componentallB = document.createElement("Component");
					
					//Type
					Element cTypeallB = document.createElement("Type");
					cTypeallB.appendChild(document.createTextNode("String"));
					componentallB.appendChild(cTypeallB);
	
					//Name
					Element cNameallB = document.createElement("Name");
					cNameallB.appendChild(document.createTextNode("allB"));
					componentallB.appendChild(cNameallB);
					
					//Value
					Element cValueallB = document.createElement("Value");
					cValueallB.appendChild(document.createTextNode(String.valueOf(ColorImage.allB)));
					componentallB.appendChild(cValueallB);
	
					color.appendChild(componentallB);
					
					//highR
					Element componenthighR = document.createElement("Component");
					
					//Type
					Element cTypehighR = document.createElement("Type");
					cTypehighR.appendChild(document.createTextNode("String"));
					componenthighR.appendChild(cTypehighR);
	
					//Name
					Element cNamehighR = document.createElement("Name");
					cNamehighR.appendChild(document.createTextNode("highR"));
					componenthighR.appendChild(cNamehighR);
					
					//Value
					Element cValuehighR = document.createElement("Value");
					cValuehighR.appendChild(document.createTextNode(String.valueOf(ColorImage.highR)));
					componenthighR.appendChild(cValuehighR);
	
					color.appendChild(componenthighR);
					
					//highG
					Element componenthighG = document.createElement("Component");
					
					//Type
					Element cTypehighG = document.createElement("Type");
					cTypehighG.appendChild(document.createTextNode("String"));
					componenthighG.appendChild(cTypehighG);
	
					//Name
					Element cNamehighG = document.createElement("Name");
					cNamehighG.appendChild(document.createTextNode("highG"));
					componenthighG.appendChild(cNamehighG);
					
					//Value
					Element cValuehighG = document.createElement("Value");
					cValuehighG.appendChild(document.createTextNode(String.valueOf(ColorImage.highG)));
					componenthighG.appendChild(cValuehighG);
	
					color.appendChild(componenthighG);
					
					//highB
					Element componenthighB = document.createElement("Component");
					
					//Type
					Element cTypehighB = document.createElement("Type");
					cTypehighB.appendChild(document.createTextNode("String"));
					componenthighB.appendChild(cTypehighB);
	
					//Name
					Element cNamehighB = document.createElement("Name");
					cNamehighB.appendChild(document.createTextNode("highB"));
					componenthighB.appendChild(cNamehighB);
					
					//Value
					Element cValuehighB = document.createElement("Value");
					cValuehighB.appendChild(document.createTextNode(String.valueOf(ColorImage.highB)));
					componenthighB.appendChild(cValuehighB);
	
					color.appendChild(componenthighB);
					
					//mediumR
					Element componentmediumR = document.createElement("Component");
					
					//Type
					Element cTypemediumR = document.createElement("Type");
					cTypemediumR.appendChild(document.createTextNode("String"));
					componentmediumR.appendChild(cTypemediumR);
	
					//Name
					Element cNamemediumR = document.createElement("Name");
					cNamemediumR.appendChild(document.createTextNode("mediumR"));
					componentmediumR.appendChild(cNamemediumR);
					
					//Value
					Element cValuemediumR = document.createElement("Value");
					cValuemediumR.appendChild(document.createTextNode(String.valueOf(ColorImage.mediumR)));
					componentmediumR.appendChild(cValuemediumR);
	
					color.appendChild(componentmediumR);
					
					//mediumG
					Element componentmediumG = document.createElement("Component");
					
					//Type
					Element cTypemediumG = document.createElement("Type");
					cTypemediumG.appendChild(document.createTextNode("String"));
					componentmediumG.appendChild(cTypemediumG);
	
					//Name
					Element cNamemediumG = document.createElement("Name");
					cNamemediumG.appendChild(document.createTextNode("mediumG"));
					componentmediumG.appendChild(cNamemediumG);
					
					//Value
					Element cValuemediumG = document.createElement("Value");
					cValuemediumG.appendChild(document.createTextNode(String.valueOf(ColorImage.mediumG)));
					componentmediumG.appendChild(cValuemediumG);
	
					color.appendChild(componentmediumG);
					
					//mediumB
					Element componentmediumB = document.createElement("Component");
					
					//Type
					Element cTypemediumB = document.createElement("Type");
					cTypemediumB.appendChild(document.createTextNode("String"));
					componentmediumB.appendChild(cTypemediumB);
	
					//Name
					Element cNamemediumB = document.createElement("Name");
					cNamemediumB.appendChild(document.createTextNode("mediumB"));
					componentmediumB.appendChild(cNamemediumB);
					
					//Value
					Element cValuemediumB = document.createElement("Value");
					cValuemediumB.appendChild(document.createTextNode(String.valueOf(ColorImage.mediumB)));
					componentmediumB.appendChild(cValuemediumB);
	
					color.appendChild(componentmediumB);										
									
					//lowR
					Element componentlowR = document.createElement("Component");
					
					//Type
					Element cTypelowR = document.createElement("Type");
					cTypelowR.appendChild(document.createTextNode("String"));
					componentlowR.appendChild(cTypelowR);
	
					//Name
					Element cNamelowR = document.createElement("Name");
					cNamelowR.appendChild(document.createTextNode("lowR"));
					componentlowR.appendChild(cNamelowR);
					
					//Value
					Element cValuelowR = document.createElement("Value");
					cValuelowR.appendChild(document.createTextNode(String.valueOf(ColorImage.lowR)));
					componentlowR.appendChild(cValuelowR);
	
					color.appendChild(componentlowR);
					
					//lowG
					Element componentlowG = document.createElement("Component");
					
					//Type
					Element cTypelowG = document.createElement("Type");
					cTypelowG.appendChild(document.createTextNode("String"));
					componentlowG.appendChild(cTypelowG);
	
					//Name
					Element cNamelowG = document.createElement("Name");
					cNamelowG.appendChild(document.createTextNode("lowG"));
					componentlowG.appendChild(cNamelowG);
					
					//Value
					Element cValuelowG = document.createElement("Value");
					cValuelowG.appendChild(document.createTextNode(String.valueOf(ColorImage.lowG)));
					componentlowG.appendChild(cValuelowG);
	
					color.appendChild(componentlowG);
					
					//lowB
					Element componentlowB = document.createElement("Component");
					
					//Type
					Element cTypelowB = document.createElement("Type");
					cTypelowB.appendChild(document.createTextNode("String"));
					componentlowB.appendChild(cTypelowB);
	
					//Name
					Element cNamelowB = document.createElement("Name");
					cNamelowB.appendChild(document.createTextNode("lowB"));
					componentlowB.appendChild(cNamelowB);
					
					//Value
					Element cValuelowB = document.createElement("Value");
					cValuelowB.appendChild(document.createTextNode(String.valueOf(ColorImage.lowB)));
					componentlowB.appendChild(cValuelowB);
	
					color.appendChild(componentlowB);
					
					
				root.appendChild(color);
				}
				
				Element videoCropping = document.createElement("Video");
				
				if (caseRognage.isSelected())
				{
					for (Component p : CropVideo.frame.getContentPane().getComponents())
					{					
						if (p.getName() != "" && p.getName() != null)
						{
							if (p instanceof JLabel)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JLabel"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JLabel) p).getText()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								videoCropping.appendChild(component);
							}
							else if (p instanceof JRadioButton)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JRadioButton"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JRadioButton) p).isSelected())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								videoCropping.appendChild(component);
							}
							else if (p instanceof JComboBox && CropVideo.comboPreset.isEnabled())
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JComboBox"));
								component.appendChild(cType);
																	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JComboBox) p).getSelectedItem().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								videoCropping.appendChild(component);
							}									
						}
					}
					
				root.appendChild(videoCropping);
				}
				
				Element imageCropping = document.createElement("Image");
				
				if (caseRognerImage.isSelected())
				{
					for (Component p : CropImage.frame.getContentPane().getComponents())
					{
						if (p.getName() != "" && p.getName() != null)
						{
							if (p instanceof JTextField)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JTextField"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								imageCropping.appendChild(component);
							}									
						}
					}
					
				root.appendChild(imageCropping);
				}
							
				Element overlay = document.createElement("Overlay");
				
				if (caseAddOverlay.isSelected())
				{
					for (Component p : OverlayWindow.frame.getContentPane().getComponents())
					{
						if (p.getName() != "" && p.getName() != null)
						{									
							if (p instanceof JRadioButton)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JRadioButton"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JRadioButton) p).isSelected())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								overlay.appendChild(component);
							}
							else if (p instanceof JLabel)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JLabel"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JLabel) p).getText()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								overlay.appendChild(component);
							}
							else if (p instanceof JComboBox)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JComboBox"));
								component.appendChild(cType);
																	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JComboBox) p).getSelectedItem().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								overlay.appendChild(component);
							}
							else if (p instanceof JTextField)
							{
								if ((((JTextField) p).getText().toString()).isEmpty() == false && (((JTextField) p).getText().toString()) != "")
								{
									//Component
									Element component = document.createElement("Component");
									
									//Type
									Element cType = document.createElement("Type");
									cType.appendChild(document.createTextNode("JTextField"));
									component.appendChild(cType);
									
									//Name
									Element cName = document.createElement("Name");
									cName.appendChild(document.createTextNode(p.getName()));
									component.appendChild(cName);
									
									//Value
									Element cValue = document.createElement("Value");
									cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
									component.appendChild(cValue);
									
									//State
									Element cState = document.createElement("Enable");
									cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
									component.appendChild(cState);
									
									//Visible
									Element cVisible = document.createElement("Visible");
									cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
									component.appendChild(cVisible);		
									
									overlay.appendChild(component);
								}
							}
							else if (p instanceof JSpinner)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JSpinner"));
								component.appendChild(cType);
	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JSpinner) p).getValue())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								overlay.appendChild(component);
							}
							else if (p instanceof JPanel)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JPanel"));
								component.appendChild(cType);
	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JPanel) p).getBackground())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								overlay.appendChild(component);
							}
						}				
					}	
					
				root.appendChild(overlay);
				}
							
				Element subtitles = document.createElement("Subtitles");
				
				if (caseSubtitles.isSelected())
				{
					for (Component p : SubtitlesWindow.frame.getContentPane().getComponents())
					{
						if (p.getName() != "" && p.getName() != null)
						{
							if (p instanceof JButton)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JButton"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");								
								cValue.appendChild(document.createTextNode(((JButton) p).getForeground().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
							else if (p instanceof JLabel)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JLabel"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JLabel) p).getText()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
							else if (p instanceof JComboBox)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JComboBox"));
								component.appendChild(cType);
																	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JComboBox) p).getSelectedItem().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
							else if (p instanceof JTextField)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JTextField"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
							else if (p instanceof JSpinner)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JSpinner"));
								component.appendChild(cType);
	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JSpinner) p).getValue())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
							else if (p instanceof JPanel)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JPanel"));
								component.appendChild(cType);
	
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(String.valueOf(((JPanel) p).getBackground())));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								subtitles.appendChild(component);
							}
						}
					}
					
				root.appendChild(subtitles);
				}
								
				Element watermark = document.createElement("Watermark");
				
				if (caseLogo.isSelected())
				{
					for (Component p : WatermarkWindow.frame.getContentPane().getComponents())
					{
						if (p.getName() != "" && p.getName() != null)
						{
							if (p instanceof JTextField)
							{
								//Component
								Element component = document.createElement("Component");
								
								//Type
								Element cType = document.createElement("Type");
								cType.appendChild(document.createTextNode("JTextField"));
								component.appendChild(cType);
								
								//Name
								Element cName = document.createElement("Name");
								cName.appendChild(document.createTextNode(p.getName()));
								component.appendChild(cName);
								
								//Value
								Element cValue = document.createElement("Value");
								cValue.appendChild(document.createTextNode(((JTextField) p).getText().toString()));
								component.appendChild(cValue);
								
								//State
								Element cState = document.createElement("Enable");
								cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
								component.appendChild(cState);
								
								//Visible
								Element cVisible = document.createElement("Visible");
								cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
								component.appendChild(cVisible);		
								
								watermark.appendChild(component);
							}									
						}
					}
					
				root.appendChild(watermark);
				}
								
				// creation du fichier XML
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new File(dialog.getDirectory() + dialog.getFile().toString().replace(".enc", "")) + ".enc");
	
				transformer.transform(domSource, streamResult);
				
				//Mise à jour de l'affichage
				if (update == false && Functions.frame != null)
				{
					if (Functions.frame.isVisible())					
						changeFrameVisibility(Functions.frame, true);
					
					changeFrameVisibility(Functions.frame, false);
				}
				else if (update == false)
				{
					new Functions();
					Functions.frame.setVisible(true);
					Functions.frame.toFront();
				}
				else
					Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				iconPresets.setVisible(false);
				if (iconList.isVisible())
					btnCancel.setBounds(207, 46, 97, 21);
				else
					btnCancel.setBounds(184, 46, 120, 21);
				
			} catch (ParserConfigurationException | TransformerException e) {}
		 }
	}
		
	@SuppressWarnings("rawtypes")
	public static void loadSettings(File encFile) {
	
	if (liste.getSize() == 0)
	{
		JOptionPane.showMessageDialog(frame, language.getProperty("addFileToList"),	language.getProperty("noFile"), JOptionPane.ERROR_MESSAGE);
		Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	else
	{				
		try {
			File fXmlFile = encFile;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Component");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					//Type						
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JPanel)
						{
							for (Component p : ((JPanel) c).getComponents())
							{
								if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
								{
									if (p instanceof JLabel)
									{
										//Value
										((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																			
										//State
										((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
										
										//Visible
										((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));																			
									}
									else if (p instanceof JRadioButton)
									{
										
										if (p.getName().equals("caseInAndOut") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseInAndOut.doClick();
											VideoPlayer.loadSettings(encFile);
										}
										else if (p.getName().equals("caseRognage") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseRognage.doClick();
											CropVideo.loadSettings(encFile);
										}											
										else if (p.getName().equals("caseRognerImage") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseRognerImage.doClick();
											CropImage.loadSettings(encFile);
										}											
										else if (p.getName().equals("caseAddOverlay") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseAddOverlay.doClick();
											OverlayWindow.loadSettings(encFile);
										}
										else if (p.getName().equals("caseSubtitles") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseSubtitles.doClick();
											SubtitlesWindow.loadSettings(encFile);
										}										
										else if (p.getName().equals("caseLogo") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseLogo.doClick();
											WatermarkWindow.loadSettings(encFile);
										}
										else if (p.getName().equals("caseColor") && Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
										{
											caseColor.doClick();
											ColorImage.loadSettings(encFile);
										}	
										else
										{
											//Value
											if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
											{
												if (((JRadioButton) p).isSelected() == false)
													((JRadioButton) p).doClick();
											}
											else
											{
												if (((JRadioButton) p).isSelected())
													((JRadioButton) p).doClick();
											}
										}
																												
										//State
										((JRadioButton) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
										
										//Visible
										((JRadioButton) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									}
									else if (p instanceof JComboBox)
									{		
										if (p.getName().equals("comboLUTs"))
										{
											comboLUTs.setSelectedIndex(10);

									        for (int i = 0 ; i < comboLUTs.getModel().getSize() ; i++) 
									        { 
									        	if (comboLUTs.getModel().getElementAt(i).toString().equals(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									        	{
									        		comboLUTs.setSelectedIndex(i);
									        		break;
									        	}
									        }
										}
										else
										{
											//Value
											((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																				
											//State
											((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
											
											//Visible
											((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
															
											if (p.getName().equals("comboFonctions"))
											{
												do {
													Thread.sleep(100);
												} while (btnReset.getX() > 336);
											}										
										}
									}
									else if (p instanceof JTextField)
									{
										//Value
										((JTextField) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																			
										//State
										((JTextField) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
										
										//Visible
										((JTextField) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									}
									else if (p instanceof JSlider)
									{
										//Value
										((JSlider) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																			
										//State
										((JSlider) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
										
										//Visible
										((JSlider) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									}
									else if (p instanceof JSpinner)
									{							
										//Value
										((JSpinner) p).setValue(Integer.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
																			
										//State
										((JSpinner) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
										
										//Visible
										((JSpinner) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
									}
								}
							}
						}
					}					
				}
			}
			
			changementDeFonction(false);							
			
			if (lblPad.getText().equals(language.getProperty("lblPad")))
			{
				lblPad.setText(language.getProperty("lblPad"));
				lblPadLeft.setBackground(Color.black);
				lblPadLeft.setVisible(true);
				lblPadRight.setBackground(Color.black);
				lblPadRight.setVisible(true);
			}
			else if (lblPad.getText().equals(language.getProperty("lblStretch")))
			{
				lblPadLeft.setVisible(false);
				lblPadRight.setVisible(false);												
			}
			else
			{
				lblPad.setText(language.getProperty("lblCrop"));
				lblPadLeft.setBackground(new Color(50,50,50));
				lblPadLeft.setVisible(true);
				lblPadRight.setBackground(new Color(50,50,50));
				lblPadRight.setVisible(true);
			}
				
		} catch (Exception e) {}
		finally {
			Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}

	public static void loadThemes() {

		//Theme
		if (new File(Shutter.documents + "/settings.xml").exists())
		{				
			try {
				File fXmlFile = new File(Shutter.documents + "/settings.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
			
				NodeList nList = doc.getElementsByTagName("Component");
								
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("comboTheme"))
							getTheme = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent();							
					}
				}	
								
				if (getTheme != null && getTheme != "" && getTheme.equals(Shutter.language.getProperty("darkTheme")))
					FlatLaf.install( new FlatDarkLaf() );
				else
					FlatLaf.install( new FlatLightLaf() );
				
			} catch (Exception e) {
				try {
				    FlatLaf.install( new FlatLightLaf() );
				} catch( Exception ex ) {}
			}
		}
		else
		{
			try {
			    FlatLaf.install( new FlatLightLaf() );
			} catch( Exception ex ) {}
		}

		UIManager.put("Component.focusWidth", 0 );
		UIManager.put("ScrollBar.thumbArc", 999);
		UIManager.put("Button.arc", 6);
		UIManager.put("TextField.arc", 6);
		UIManager.put("ProgressBar.arc", 6);
		UIManager.put("TextComponent.arc", 6);
		UIManager.put("Component.arc", 6);
		
		if (getTheme != null && getTheme != "" && getTheme.equals(Shutter.language.getProperty("darkTheme")))
		{
			UIManager.put("Component.borderColor", new Color(40,40,40));
			UIManager.put("Component.disabledBorderColor", new Color(40,40,40));
			
			UIManager.put("Button.startBorderColor", new Color(40,40,40));
			UIManager.put("Button.endBorderColor", new Color(40,40,40));
			UIManager.put("Button.startBackground", new Color(100,100,100));
			UIManager.put("Button.endBackground", new Color(80,80,80));
			UIManager.put("Button.disabledBorderColor", new Color(40,40,40));
			UIManager.put("Button.disabledBackground", new Color(60,60,60));
			UIManager.put("Button.foreground", new Color(245,245,245));
			UIManager.put("Button.default.foreground", new Color(245,245,245));
			UIManager.put("Button.default.startBackground", new Color(100,100,100));
			UIManager.put("Button.default.endBackground", new Color(80,80,80));
							
			UIManager.put("ComboBox.background", new Color(80,80,80));		
			UIManager.put("ComboBox.foreground", new Color(245,245,245));
			UIManager.put("ComboBox.disabledBackground", new Color(60,60,60));
			UIManager.put("ComboBox.selectionBackground", new Color(100,100,100));
			UIManager.put("ComboBox.disabledForeground", new Color(120,120,120));
			UIManager.put("ComboBox.buttonBackground", new Color(80,80,80));			
			
			UIManager.put("MenuItem.background", new Color(80,80,80));		
			UIManager.put("MenuItem.foreground", new Color(245,245,245));
			UIManager.put("MenuItem.selectionBackground", new Color(100,100,100));
			
			UIManager.put("CheckBoxMenuItem.background", new Color(80,80,80));		
			UIManager.put("CheckBoxMenuItem.foreground", new Color(245,245,245));
			UIManager.put("CheckBoxMenuItem.selectionBackground", new Color(100,100,100));
			
			UIManager.put("CheckBox.icon.borderColor", new Color(40,40,40));		
			UIManager.put("CheckBox.icon.background", new Color(80,80,80));
			UIManager.put("CheckBox.icon.disabledBorderColor", new Color(60,60,60));	
			UIManager.put("CheckBox.icon.disabledBackground", new Color(60,60,60));	
			UIManager.put("CheckBox.icon.focusedBorderColor", new Color(40,40,40));
			
			UIManager.put("TableHeader.foreground", new Color(245,245,245));
			UIManager.put("Table.foreground", new Color(245,245,245));
			UIManager.put("Table.selectionBackground", new Color(100,100,100));
						
			UIManager.put("TextField.foreground",new Color(245,245,245));
			UIManager.put("TextField.background", new Color(80,80,80));
			UIManager.put("TextField.selectionBackground", new Color(100,100,100));
			UIManager.put("TextField.inactiveForeground", new Color(120,120,120));
			
			UIManager.put("TextArea.foreground",new Color(245,245,245));
			UIManager.put("TextArea.background", new Color(80,80,80));
			UIManager.put("TextArea.selectionBackground", new Color(100,100,100));
			UIManager.put("TextArea.inactiveForeground", new Color(120,120,120));
			
			UIManager.put("PasswordField.foreground", new Color(245,245,245));
			UIManager.put("PasswordField.background", new Color(80,80,80));	
			UIManager.put("PasswordField.selectionBackground", new Color(100,100,100));			
			UIManager.put("PasswordField.inactiveForeground", new Color(120,120,120));
			
			UIManager.put("Spinner.foreground", new Color(245,245,245));
			UIManager.put("Spinner.disabledBackground", new Color(80,80,80));
			UIManager.put("Spinner.background", new Color(80,80,80));
			UIManager.put("FormattedTextField.selectionBackground", new Color(100,100,100));
						
			UIManager.put("ScrollBar.background", new Color(50,50,50));
			UIManager.put("ScrollBar.thumb", new Color(80,80,80));
						
			UIManager.put("MenuBar.foreground", new Color(245,245,245));
			
			UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(40,40,40)));
		}
		else
		{
			UIManager.put("Component.borderColor", new Color(150,150,150));
			UIManager.put("Component.disabledBorderColor", new Color(150,150,150));
			
			UIManager.put("Button.startBorderColor", new Color(150,150,150));
			UIManager.put("Button.endBorderColor", new Color(150,150,150));
			UIManager.put("Button.startBackground", new Color(245,245,245));
			UIManager.put("Button.endBackground", new Color(225,225,225));
			UIManager.put("Button.disabledBorderColor", new Color(150,150,150));
			UIManager.put("Button.foreground", Color.BLACK);
			UIManager.put("Button.default.foreground", Color.BLACK);
			UIManager.put("Button.default.startBackground", new Color(245,245,245));
			UIManager.put("Button.default.endBackground", new Color(225,225,225));
			
			UIManager.put("ComboBox.background", new Color(245,245,245));		
			UIManager.put("ComboBox.foreground", Color.BLACK);
			UIManager.put("ComboBox.disabledBackground", new Color(245,245,245));
			UIManager.put("ComboBox.selectionBackground", new Color(71, 163, 236));
			
			UIManager.put("MenuItem.background", new Color(245,245,245));		
			UIManager.put("MenuItem.foreground", Color.BLACK);
			UIManager.put("MenuItem.selectionBackground", new Color(71, 163, 236));
			
			UIManager.put("CheckBoxMenuItem.background", new Color(245,245,245));		
			UIManager.put("CheckBoxMenuItem.foreground", Color.BLACK);
			UIManager.put("CheckBoxMenuItem.selectionBackground", new Color(71, 163, 236));
			
			UIManager.put("TableHeader.foreground", Color.BLACK);
			UIManager.put("Table.foreground", Color.BLACK);
			UIManager.put("Table.selectionBackground", new Color(71, 163, 236));
						
			UIManager.put("TextField.foreground", Color.BLACK);
			UIManager.put("TextField.background", new Color(245,245,245));
			UIManager.put("TextField.selectionBackground", new Color(71, 163, 236));
			
			UIManager.put("TextArea.foreground", Color.BLACK);
			UIManager.put("TextArea.background", new Color(245,245,245));
			UIManager.put("TextArea.selectionBackground", new Color(71, 163, 236));

			UIManager.put("PasswordField.foreground", Color.BLACK);
			UIManager.put("PasswordField.background", new Color(245,245,245));	
			UIManager.put("PasswordField.selectionBackground", new Color(71, 163, 236));
			
			UIManager.put("Spinner.foreground", Color.BLACK);
			UIManager.put("Spinner.disabledBackground", new Color(245,245,245));
			UIManager.put("Spinner.background", new Color(245,245,245));
			UIManager.put("FormattedTextField.selectionBackground", new Color(71, 163, 236));
									
			UIManager.put("MenuBar.foreground", Color.BLACK);
			UIManager.put("CheckBoxMenuItem.foreground", Color.BLACK);
			
			UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(150,150,150)));
		}
				
		UIManager.put("Panel.selectionForeground", Color.WHITE);
		UIManager.put("TextPane.selectionForeground", Color.WHITE);
		UIManager.put("FormattedTextField.selectionForeground",  Color.WHITE);
		UIManager.put("TextArea.selectionForeground", Color.WHITE);
		UIManager.put("TextField.selectionForeground", Color.WHITE);		
		UIManager.put("PasswordField.selectionForeground", Color.WHITE);		
		UIManager.put("CheckBoxMenuItem.selectionForeground", Color.WHITE);
		UIManager.put("MenuItem.selectionForeground", Color.WHITE);
		UIManager.put("ComboBox.selectionForeground", Color.WHITE);
		UIManager.put("Menu.selectionForeground", Color.WHITE);
										
		UIManager.put("TabbedPane.focusColor", new Color(50,50,50));
		UIManager.put("TabbedPane.tabInsets", new Insets(0,5,0,5));
	    UIManager.put("TabbedPane.tabAreaInsets", new Insets(-5,0,-5,0));
		UIManager.put("TabbedPane.background", new Color(40,40,40));
		UIManager.put("TabbedPane.selectedBackground", new Color(50,50,50));
		UIManager.put("TabbedPane.hoverColor", new Color(50,50,50));
		UIManager.put("TabbedPane.highlight", new Color(50,50,50));
		UIManager.put("TabbedPane.underlineColor", new Color(50,50,50));
		UIManager.put("TabbedPane.disabledUnderlineColor", new Color(50,50,50));
		UIManager.put("TabbedPane.contentAreaColor", new Color(50,50,50));
		UIManager.put("TabbedPane.foreground", new Color(245,245,245));
		
		UIManager.put("CheckBox.icon.checkmarkColor", new Color(71, 163, 236));	
		UIManager.put("CheckBox.icon.disabledCheckmarkColor", new Color(100, 100, 100));	
		UIManager.put("RadioButton.icon.centerDiameter", 9);
		
		UIManager.put("ProgressBar.background" , new Color(40, 40, 40));
		UIManager.put("ProgressBar.foreground" , new Color(71, 163, 236));	
		UIManager.put("ProgressBar.selectionBackground", new Color(245,245,245));
        UIManager.put("ProgressBar.selectionForeground", new Color(245,245,245));
		
		UIManager.put("Slider.thumbColor", new Color(71, 163, 236));
		UIManager.put("Slider.hoverColor", new Color(129,198,253));
		UIManager.put("Slider.trackColor", new Color(40,40,40));
		
		UIManager.put("RadioButton.foreground" , new Color(245,245,245));
		UIManager.put("RadioButton.background" , new Color(50,50,50,0));	
		
		UIManager.put("ColorChooser.background", new Color(50,50,50));
		UIManager.put("ColorChooser.foreground", new Color(245,245,245));
		        
		UIManager.put("TextPane.foreground", Color.BLACK);
		UIManager.put("TextPane.background", Color.WHITE);
		UIManager.put("TextPane.selectionBackground", new Color(71, 163, 236));		
		
		UIManager.put("Component.arrowType", "triangle");
			
		UIManager.put("ComboBox.padding", new Insets(2,2,2,0));
				
		UIManager.put("Spinner.padding", new Insets(2,2,2,0));
		
		UIManager.put("Panel.background", new Color(50,50,50));
		
		UIManager.put("TextField.margin", new Insets(0,0,0,0));
				
		UIManager.put("ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ));
		
		UIManager.put("Label.foreground", new Color(245,245,245));	
		
		UIManager.put("OptionPane.background", new Color(50,50,50));
		
		UIManager.put("TitledBorder.titleColor", new Color(245,245,245));
		
	}

	public static void textFieldBackground() {
		
		if (getTheme != null && getTheme != "" && getTheme.equals(Shutter.language.getProperty("darkTheme")))
		{
			if (Settings.txtExtension.isEnabled())
				Settings.txtExtension.setBackground(new Color(80,80,80));
			else
				Settings.txtExtension.setBackground(new Color(60,60,60));
			
			if (txtAudioOffset.isEnabled())
				txtAudioOffset.setBackground(new Color(80,80,80));
			else
				txtAudioOffset.setBackground(new Color(60,60,60));
			
			if (textH.isEnabled())
				textH.setBackground(new Color(80,80,80));
			else
				textH.setBackground(new Color(60,60,60));
			
			if (textMin.isEnabled())
				textMin.setBackground(new Color(80,80,80));
			else
				textMin.setBackground(new Color(60,60,60));
			
			if (textSec.isEnabled())
				textSec.setBackground(new Color(80,80,80));
			else
				textSec.setBackground(new Color(60,60,60));
			
			if (taille.isEnabled())
				taille.setBackground(new Color(80,80,80));
			else
				taille.setBackground(new Color(60,60,60));
			
			if (gopSize.isEnabled())
				gopSize.setBackground(new Color(80,80,80));
			else
				gopSize.setBackground(new Color(60,60,60));
			
			if (spinnerVideoFadeIn.isEnabled())
				spinnerVideoFadeIn.setBackground(new Color(80,80,80));
			else
				spinnerVideoFadeIn.setBackground(new Color(60,60,60));
			
			if (spinnerAudioFadeIn.isEnabled())
				spinnerAudioFadeIn.setBackground(new Color(80,80,80));
			else
				spinnerAudioFadeIn.setBackground(new Color(60,60,60));
			
			if (spinnerVideoFadeOut.isEnabled())
				spinnerVideoFadeOut.setBackground(new Color(80,80,80));
			else
				spinnerVideoFadeOut.setBackground(new Color(60,60,60));
			
			if (spinnerAudioFadeOut.isEnabled())
				spinnerAudioFadeOut.setBackground(new Color(80,80,80));
			else
				spinnerAudioFadeOut.setBackground(new Color(60,60,60));
			
		}		
	}
}
