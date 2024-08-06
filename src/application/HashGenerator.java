/*******************************************************************************************
* Copyright (C) 2024 PACIFICO PAUL
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;

public class HashGenerator {
	
	private static JDialog frame;
	private static JLabel quit;
	private static JPanel topPanel;
	private static JLabel topImage;
	private static JRadioButton MD5;
	private static JRadioButton SHA1;
	private static JRadioButton SHA256;	
	private static JTextField txtGenerate;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
    public HashGenerator() {
    	
    	frame = new JDialog();	
      	frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setTitle(Shutter.language.getProperty("menuItemHash"));
		frame.setModal(true);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setSize(600, 143);
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		
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
				
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Shutter.taskBarHeight = (int) (dim.getHeight() - winSize.height);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2 - frame.getHeight());			
	        	
		topPanel();
		
		JPanel panelHash = new JPanel();
		
		MD5 = new JRadioButton("MD5");
		MD5.setSelected(true);
		MD5.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		MD5.setForeground(new Color(235,235,240));
		MD5.setBounds(0, 0, MD5.getPreferredSize().width + 4, 21);
		panelHash.add(MD5);
		
		SHA1 = new JRadioButton("SHA-1");
		SHA1.setSelected(false);
		SHA1.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		SHA1.setForeground(new Color(235,235,240));
		SHA1.setBounds(MD5.getX() + MD5.getWidth() + 7, 0, SHA1.getPreferredSize().width + 4, 21);
		panelHash.add(SHA1);
		
		SHA256 = new JRadioButton("SHA-256");
		SHA256.setSelected(false);
		SHA256.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		SHA256.setForeground(new Color(235,235,240));
		SHA256.setBounds(SHA1.getX() + SHA1.getWidth() + 7, 0, SHA256.getPreferredSize().width + 4, 21);
		panelHash.add(SHA256);	
				
		panelHash.setLayout(null);
		panelHash.setSize(MD5.getWidth() + SHA1.getWidth() + SHA256.getWidth() + 14, 21);
		panelHash.setLocation(frame.getWidth() / 2  - panelHash.getWidth() / 2 - 7, topPanel.getY() + topPanel.getHeight() + 2);
		frame.getContentPane().add(panelHash);
		
        JLabel btnBrowse = new JLabel();
        btnBrowse.setIcon(new FlatSVGIcon("contents/drop.svg", 21, 21));
        btnBrowse.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
        btnBrowse.setLocation(7, panelHash.getY() + panelHash.getHeight() + 5);
        btnBrowse.setSize(btnBrowse.getPreferredSize().width, 21);
        frame.getContentPane().add(btnBrowse);

        JTextField txtFile = new JTextField();
        if (Shutter.fileList.getSelectedIndices().length > 0)
        {
        	txtFile.setFont(new Font("SansSerif", Font.PLAIN, 12));
        	txtFile.setForeground(new Color(235,235,240));
        	txtFile.setText(Shutter.fileList.getSelectedValue());        
        }
        else
        {
        	txtFile.setFont(new Font("SansSerif", Font.ITALIC, 12));
        	txtFile.setForeground(Color.LIGHT_GRAY);
        	txtFile.setText(Shutter.language.getProperty("dropFilesHere"));
        }        
        txtFile.setLocation(btnBrowse.getX() + btnBrowse.getWidth() + 7, btnBrowse.getY());
        txtFile.setSize(frame.getWidth() -  txtFile.getX() - 9, 21);
       
        frame.getContentPane().add(txtFile);
        
        // Drag & Drop
        txtFile.setTransferHandler(new FileTransferHandler());
        
        JLabel btnGenerate = new JLabel();
        btnGenerate.setIcon(new FlatSVGIcon("contents/generate.svg", 18, 18));
        btnGenerate.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
        btnGenerate.setSize(80, btnBrowse.getPreferredSize().height);
        btnGenerate.setBounds(9, btnBrowse.getY() + btnBrowse.getHeight() + 7, btnBrowse.getWidth(), btnBrowse.getHeight());
        frame.getContentPane().add(btnGenerate);
        
        txtGenerate = new JTextField();
        txtGenerate.setEditable(false);
        txtGenerate.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtGenerate.setLocation(txtFile.getX(), btnGenerate.getY());
        txtGenerate.setSize(txtFile.getSize());
        txtGenerate.setForeground(new Color(235,235,240));
        frame.getContentPane().add(txtGenerate);
                
        JLabel btnCheck = new JLabel();
        btnCheck.setIcon(new FlatSVGIcon("contents/check.svg", 21, 21));
        btnCheck.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
        btnCheck.setBounds(7, btnGenerate.getY() + btnGenerate.getHeight() + 7, btnBrowse.getWidth(), btnBrowse.getHeight());
        frame.getContentPane().add(btnCheck);        
 
        JTextField txtCheck = new JTextField();
        txtCheck.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtCheck.setLocation(txtFile.getX(), btnCheck.getY());
        txtCheck.setSize(txtFile.getSize());
        txtCheck.setForeground(new Color(235,235,240));
        frame.getContentPane().add(txtCheck);

		MD5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (MD5.isSelected())
				{
					SHA1.setSelected(false);
					SHA256.setSelected(false);
					
					txtGenerate.setText("");
                	txtGenerate.setForeground(new Color(235,235,240));
				}
				else
					MD5.setSelected(true);
				
			}
			
		});
		
		SHA1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (SHA1.isSelected())
				{
					MD5.setSelected(false);
					SHA256.setSelected(false);
					
					txtGenerate.setText("");
                	txtGenerate.setForeground(new Color(235,235,240));
				}
				else
					SHA1.setSelected(true);
				
			}
			
		});
		
		SHA256.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (SHA256.isSelected())
				{
					SHA1.setSelected(false);
					MD5.setSelected(false);
					
					txtGenerate.setText("");
                	txtGenerate.setForeground(new Color(235,235,240));
				}
				else
					SHA256.setSelected(true);
				
			}
			
		});
		
        btnGenerate.addMouseListener(new MouseAdapter() {
        	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				txtGenerate.setForeground(new Color(235,235,240));
            	txtCheck.setForeground(new Color(235,235,240));
            	
                String filePath = txtFile.getText();
                if (filePath.isEmpty() == false)
                {         
                    File file = new File(filePath);
                    Shutter.cancelled = false;
                    
                    Thread t = new Thread(new Runnable() {

                		@Override
                		public void run() {
                    
		                    try {
		                    	
		                    	frame.setModal(false);
		                        String hash = generateHash(file);
		                        
		                        if (Shutter.cancelled == false)
		                        {
			                        txtGenerate.setText(hash);
			                       			                        
			                        if (file.isFile())
			                        {
				                        String extension = filePath.substring(filePath.lastIndexOf("."));	
				                        PrintWriter writer = new PrintWriter(new FileWriter(filePath.replace(extension, ".txt"), false));
				                        writer.println(hash);
				                        writer.close();
			                        }
			                        else if (file.isDirectory())
			                        {
			                        	PrintWriter writer = new PrintWriter(new FileWriter(filePath + ".txt"), false);
				                        writer.println(hash);
				                        writer.close();
			                        }
		                        }
		                        else
								{
		                        	txtGenerate.setText("");
								}
		                        
		                    } catch (Exception ex) {
		                        JOptionPane.showMessageDialog(null, "Error generating hash: " + ex.getMessage());
		                    }
		                    finally {
		                    	
		                    	frame.setModal(true);
		                        Shutter.progressBar1.setValue(100);
		                		FFMPEG.enableAll();	 
		                		FFMPEG.enfOfFunction();	     
		                		
		                		try {
		                			Shutter.frame.setOpacity(0.5f);
		                		} catch (Exception er) {}
		                        Utils.changeDialogVisibility(frame, false);   
		                    }
                		}    		    		
                	});
                	t.start();  
                }
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
			}
			
        });

        btnCheck.addMouseListener(new MouseAdapter() {
        	
            @Override
            public void mouseClicked(MouseEvent e) {
            	
                String filePath = txtFile.getText();
                String enteredHash = txtCheck.getText().trim();
                if (filePath.isEmpty() == false && enteredHash.isEmpty() == false) 
                {
                    File file = new File(filePath);
                    Shutter.cancelled = false;
                                       	
                	txtGenerate.setText("");
                	txtGenerate.setForeground(new Color(235,235,240));
                	txtCheck.setForeground(new Color(235,235,240));
                	
                	Thread t = new Thread(new Runnable() {

                		@Override
                		public void run() {
                			
                			 try {
                				 
                				frame.setModal(false);
								String hash = generateHash(file);			
								
								if (Shutter.cancelled == false)
		                        {
	                                txtGenerate.setText(hash);
	                                 
	                                if (hash.equalsIgnoreCase(enteredHash))
	                             	{
	                                	txtGenerate.setForeground(Color.GREEN);
	                                 	txtCheck.setForeground(Color.GREEN);
	                             	}
	                                else
	                                {
	                                	txtCheck.setForeground(Color.RED);
	                                }
		                        }
								else
								{
									txtCheck.setText("");
								}                                
                                
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error generating hash: " + ex.getMessage());
                            }
                			 finally {
 		                    	
 		                    	frame.setModal(true);
 		                        Shutter.progressBar1.setValue(100);
 		                		FFMPEG.enableAll();	 
 		                		FFMPEG.enfOfFunction();	     
 		                		
 		                		try {
 		                			Shutter.frame.setOpacity(0.5f);
 		                		} catch (Exception er) {}
 		                        Utils.changeDialogVisibility(frame, false);   
 		                    }
                		}    		    		
                	});
                	t.start();  
                }
            }
            
            @Override
			public void mouseEntered(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
			}
			
        });
        
        frame.setVisible(true);
    }

	private void topPanel() {	
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setBounds(0, 0, frame.getWidth(), 28);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		topPanel.add(quit);
		
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
					Shutter.frame.setOpacity(1f);
					frame.dispose();
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("menuItemHash"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
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
    
    private String generateHash(File file) throws NoSuchAlgorithmException, IOException {
    	
    	Utils.changeDialogVisibility(frame, true);
    	
    	FFMPEG.disableAll();
		Shutter.btnStart.setEnabled(false);		
		
    	Shutter.frame.setOpacity(1f);
    	Shutter.progressBar1.setValue(0);
    	Shutter.progressBar1.setMaximum(100);
    	
        MessageDigest digest;
        if (SHA1.isSelected())
        {
        	digest = MessageDigest.getInstance("SHA-1");
        }
        else if (SHA256.isSelected())
        {
        	digest = MessageDigest.getInstance("SHA-256");
        }
        else
        	digest = MessageDigest.getInstance("MD5");
                
        if (file.isFile())
        {
        	Shutter.lblCurrentEncoding.setText(file.getName());
            digestFile(file, digest);
        }
        else if (file.isDirectory())
        {
            Files.walk(file.toPath()).filter(Files::isRegularFile).forEach(path -> {
            	
            	Shutter.lblCurrentEncoding.setText(path.toFile().getName());
            	
                try {
                    digestFile(path.toFile(), digest);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
        byte[] hashBytes = digest.digest();
                    	    	
        return bytesToHex(hashBytes);        
    }

    private void digestFile(File file, MessageDigest digest) throws IOException {
	
		long fileSize = file.length();
		long currentSize = 0;    	

		InputStream is = Files.newInputStream(file.toPath());		    
		DigestInputStream dis = new DigestInputStream(is, digest);

        byte[] buffer = new byte[4096];
        while (dis.read(buffer) != -1) {
        	
        	currentSize += 4096;
        	Shutter.progressBar1.setValue(Math.round((float) ((float) currentSize / fileSize) * 100));
        	
        	if (Shutter.cancelled)
        		break;
        }
	
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Drag & Drop
    @SuppressWarnings("serial")
    class FileTransferHandler extends TransferHandler {

    	public boolean canImport(JComponent comp, DataFlavor[] arg1) {
    		
    		for (int i = 0; i < arg1.length; i++)
    		{
    			DataFlavor flavor = arg1[i];
    			
    			if (flavor.equals(DataFlavor.javaFileListFlavor))
    			{
    				comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
    				return true;
    			}
    		}
    		return false;
    	}

    	public boolean importData(JComponent comp, Transferable t) {
    				
    		DataFlavor[] flavors = t.getTransferDataFlavors();
    		for (int i = 0; i < flavors.length; i++)
    		{
    			DataFlavor flavor = flavors[i];
    			
    			try {
    				
    				if (flavor.equals(DataFlavor.javaFileListFlavor))
    				{
    					List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
    					Iterator<?> iter = l.iterator();
    					
    					((JTextField) comp).setFont(new Font("SansSerif", Font.PLAIN, 12));
    					((JTextField) comp).setForeground(new Color(235,235,240));
    					
    					while (iter.hasNext())
    					{						
    						File file = (File) iter.next();
    						
    						//Montage du chemin UNC
    						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
    							file = Utils.UNCPath(file);
    						
    						((JTextField) comp).setText(file.getAbsolutePath());   						    						
    					}

    					// Border
    					comp.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

    					return true;
    				}
    			} catch (IOException | UnsupportedFlavorException ex) {
    			}
    		}
    		return false;
    	}
    }
}
