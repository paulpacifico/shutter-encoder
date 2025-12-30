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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class Update {

	public static JDialog frame;
	private final static JProgressBar progressBar = new JProgressBar();
	public static JLabel lblNewVersion = new JLabel(Shutter.language.getProperty("lblNewVersion"));
	
	private static JPanel topPanel;
	private static JLabel quit;
	
	private static boolean cancelled = false;
			
	private static int MousePositionY;
	
	public Update() {
		
		frame = new JDialog();
		frame.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		frame.setResizable(false);
		frame.setModal(false);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(346, 59);
		frame.getContentPane().setBackground(Utils.bg32);
		frame.setTitle(Shutter.language.getProperty("frameUpdate"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		
		if (frame.isUndecorated() == false) //Evite un bug lors de la seconde ouverture
		{
			frame.setUndecorated(true);
			frame.setShape(new AntiAliasedRoundRectangle(0, 0, frame.getWidth() + 15, frame.getHeight(), 15, 15));
			
			if (System.getProperty("os.name").contains("Mac") == false)
				frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		}
		
		content();		
				
		GraphicsConfiguration config = Shutter.frame.getGraphicsConfiguration();
		GraphicsDevice myScreen = config.getDevice();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] allScreens = env.getScreenDevices();
		int screenIndex = -1;
		for (int i = 0; i < allScreens.length; i++) {
		    if (allScreens[i].equals(myScreen))
		    {
		    	screenIndex = i;
		        break;
		    }
		}
		
		if (System.getProperty("os.name").contains("Windows"))
			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDefaultConfiguration().getBounds().width - frame.getSize().width, allScreens[screenIndex].getDefaultConfiguration().getBounds().height - 99);	
		else
			frame.setLocation(allScreens[screenIndex].getDefaultConfiguration().getBounds().x + allScreens[screenIndex].getDisplayMode().getWidth() - frame.getSize().width, allScreens[screenIndex].getDisplayMode().getHeight() - 99);	
		
		
		frame.setVisible(true);		
		
   	}
	
	private void content() {
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBackground(Utils.bg32);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 3, 15, 15);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 346, 59);
		
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
					cancelled = true;
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
						
		progressBar.setBounds(6, 29, 334, 23);		
		topPanel.add(progressBar);
		
		lblNewVersion.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		lblNewVersion.setBounds(6, 11, 295, 16);		
		topPanel.add(lblNewVersion);
		
		topPanel.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {	
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
		
		topPanel.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(frame.getLocation().x,MouseInfo.getPointerInfo().getLocation().y - MousePositionY);
				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
			
		});
		
		frame.getContentPane().add(topPanel);
	}

	public static void newVersion() {	
		
		cancelled = false;
		
	        try {
	        	
	        	JLabel news = new JLabel(Shutter.language.getProperty("wantToDownload"));
	        	
	        	try {
		        	Document changelog = Jsoup.connect("https://www.shutterencoder.com/changelog.txt").get();
		        	changelog.outputSettings().prettyPrint(false);
		        	String[] versions = changelog.select("body").toString().split("Version");	        	
	  				news = new JLabel("<html>Version" + versions[1].replace(System.lineSeparator(), "<br>") + Shutter.language.getProperty("wantToDownload") + "</html>");   
	        	}
	        	catch (Exception er){}
	        	
			    Document doc = Jsoup.connect("https://www.shutterencoder.com").get();
			    			    
			    for (Element file : doc.select("a"))
			    {		
			    	if (System.getProperty("os.name").contains("Windows"))
	            	{
	            		try {
	            			
		            		if (file.attr("href").contains("Shutter Encoder") && file.attr("href").contains("Windows 64bits."))
			            	{
		            			String s[] = file.attr("href").split(" ");
		            			int newVersion = Integer.parseInt(s[2].replace(".", ""));
		            			
		            			//Checking new update
		            			if (newVersion > Integer.parseInt(Shutter.actualVersion.replace(".", "")) )
		            			{
			            				
		            				 int q =  JOptionPane.showConfirmDialog(Shutter.frame, news, Shutter.language.getProperty("updateAvailable") + " (v"+ s[2]+ ")", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
		    						 if (q == JOptionPane.YES_OPTION)
		    						 {	    									
			    							runProcess(file.attr("href"));	
		    							 	new Update();		    							 	
		    						 }	  		
		    						 break;
		            			}
			            	}
		            		
	            		} catch (Exception e) {}
	            	}			    	
			    	else if (System.getProperty("os.name").contains("Mac"))
	            	{
	            		try {
	            			
	            			if (Shutter.arch.equals("x86_64"))
	            			{	            			
			            		if (file.attr("href").contains("Shutter Encoder") && file.attr("href").contains("Mac 64bits."))
			            		{
			            			String s[] = file.attr("href").split(" ");
			            			int newVersion = Integer.parseInt(s[2].replace(".", ""));
			            			
			            			//Checking new update
			            			if (newVersion > Integer.parseInt(Shutter.actualVersion.replace(".", "")) )
			            			{
			            				 int q =  JOptionPane.showConfirmDialog(Shutter.frame, news, Shutter.language.getProperty("updateAvailable") + " (v"+ s[2]+ ")", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			    						 if (q == JOptionPane.YES_OPTION)
			    						 {
			    								runProcess(file.attr("href"));	   
			    							 	new Update();		    							 	
			    						 }	  
			    						 break;
			            			}
			            		}
	            			}
	            			else //ARM64 Apple Silicon
	            			{
	            				if (file.attr("href").contains("Shutter Encoder") && file.attr("href").contains("Apple Silicon."))
			            		{
			            			String s[] = file.attr("href").split(" ");
			            			int newVersion = Integer.parseInt(s[2].replace(".", ""));
			            			
			            			//Checking new update
			            			if (newVersion > Integer.parseInt(Shutter.actualVersion.replace(".", "")) )
			            			{
			            				 int q =  JOptionPane.showConfirmDialog(Shutter.frame, news, Shutter.language.getProperty("updateAvailable") + " (v"+ s[2]+ ")", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
			    						 if (q == JOptionPane.YES_OPTION)
			    						 {
			    								runProcess(file.attr("href"));	   
			    							 	new Update();		    							 	
			    						 }	  
			    						 break;
			            			}
			            		}
	            			}
	            		} catch (Exception e) {}
	            	}
	            	else //Linux
	            	{
	            		try {
	            			
		            		if (file.attr("href").contains("Shutter Encoder") && file.attr("href").contains("Linux 64bits.deb") && new File("/usr/lib/Shutter Encoder").exists()) //DEB package installed
		            		{
		            			String s[] = file.attr("href").split(" ");
		            			int newVersion = Integer.parseInt(s[2].replace(".", ""));
		            			
		            			//Checking new update
		            			if (newVersion > Integer.parseInt(Shutter.actualVersion.replace(".", "")) )
		            			{
		            				 int q =  JOptionPane.showConfirmDialog(Shutter.frame, news, Shutter.language.getProperty("updateAvailable") + " (v"+ s[2]+ ")", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
		    						 if (q == JOptionPane.YES_OPTION)
		    						 {
		    								runProcess(file.attr("href"));	   
		    							 	new Update();		    							 	
		    						 }	  			
		    						 break;
		            			}
		            		}		            	
		            		else if (file.attr("href").contains("Shutter Encoder") && file.attr("href").contains("Linux 64bits.AppImage"))
		            		{
		            			String s[] = file.attr("href").split(" ");
		            			int newVersion = Integer.parseInt(s[2].replace(".", ""));
		            			
		            			//Checking new update
		            			if (newVersion > Integer.parseInt(Shutter.actualVersion.replace(".", "")) )
		            			{
		            				 int q =  JOptionPane.showConfirmDialog(Shutter.frame, news, Shutter.language.getProperty("updateAvailable") + " (v"+ s[2]+ ")", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);	    						 
		    						 if (q == JOptionPane.YES_OPTION)
		    						 {
		    								runProcess(file.attr("href"));	   
		    							 	new Update();		    							 	
		    						 }	  				
		    						 break;
		            			}
		            		}
		            		
	            		} catch (Exception e) {}
	            	}
	            }
	        } catch (IOException ex) {
	        }
	}
	
	private static void runProcess(final String file) {
		
		Thread download = new Thread(new Runnable()  {
			
			public void run() {			
				
				String newVersion = file;
				if (System.getProperty("os.name").contains("Windows"))
				{
					String mainFolder = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					mainFolder = mainFolder.substring(1,mainFolder.length()-1);
					mainFolder = mainFolder.substring(0,(int) (mainFolder.lastIndexOf("/"))).replace("%20", " ") ;
					
					boolean portableVersion = true;
					for (File f : new File(mainFolder).listFiles())
					{
						if (f.toString().contains("unins") && f.toString().contains(".exe"))
						{
							portableVersion = false;
							break;
						}
					}
					
					if (portableVersion)
					{
						newVersion = newVersion.toString().replace(".exe", ".zip");
					}
				}
				
				String userFolder = System.getProperty("user.home");
				if (System.getProperty("os.name").contains("Windows"))
            	{
					userFolder = System.getenv("USERPROFILE");
            	}				
				
				File appPath = new File(userFolder + "/Downloads/" + newVersion);
				if (new File(userFolder + "/Downloads").exists() == false)
				{
					if (new File(userFolder + "/Desktop").exists() == false)
					{
						appPath = new File(Shutter.dirTemp + newVersion);
					}
					else
						appPath = new File(userFolder + "/Desktop/" + newVersion);						
				}
								
				//Download
				HTTPDownload("https://www.shutterencoder.com/" + newVersion, appPath.toString());
	
				if (cancelled == false && new File(appPath.toString()).exists())
				{
					int q =  JOptionPane.showConfirmDialog(Shutter.frame, Shutter.language.getProperty("installNewVersion"), Shutter.language.getProperty("downloadEnded"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			 
					if (q == JOptionPane.YES_OPTION)
					{
						try {
							Desktop.getDesktop().open(appPath);
						} catch (IOException e) {}
											
						System.exit(0);
					}	
					else
					{	    	
						JOptionPane.showMessageDialog(Shutter.frame, '"' + appPath.toString() + '"', Shutter.language.getProperty("downloadEnded"), JOptionPane.INFORMATION_MESSAGE);	 
					
						frame.dispose();
					}
				}
				else
				{
					if (new File(appPath.toString()).exists())
					{
						JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("downloadStopped"), Shutter.language.getProperty("downloadCancelled"), JOptionPane.ERROR_MESSAGE);
						
						if (appPath.exists())
							appPath.delete();
					}
					
					frame.dispose();
				}
			}
		});
		download.start();

	}
	
	@SuppressWarnings("deprecation")
	public static void HTTPDownload(String address, String destination) {
	
		cancelled = false;
		
		OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
		 try {
	        	URL url = new URL(address.replace(" ", "%20"));
	            out = new BufferedOutputStream(new FileOutputStream(destination));
	            conn = url.openConnection();
	            in = conn.getInputStream();
	            byte[] buffer = new byte[1024];
	            
	            progressBar.setMaximum(100);
	            
	            int numRead;
	            long numWritten = 0;
	            long fileSize = conn.getContentLength();

	            while ((numRead = in.read(buffer)) != -1)
	            {
	                out.write(buffer, 0, numRead);
	                numWritten += numRead;
	               
	                progressBar.setValue((int) ((float) ((float) numWritten * 100) / fileSize));
	                
	                if (cancelled)
	                	break;
            	}
		 	}
            catch (Exception exception)
		 	{ 
                exception.printStackTrace();
                
				JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("downloadFailed"), Shutter.language.getProperty("downloadError"), JOptionPane.ERROR_MESSAGE);	 
				
				try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
            	} catch (IOException io) {}
				
				try {
					File toDelete = new File(destination);
					toDelete.delete();
				} catch (Exception e) {}					
                         
            } 
		 	finally {
		 		
		 		try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
            	} catch (IOException io) {}
		 		
		 		if (cancelled)
	            {
	            	try {
						File toDelete = new File(destination);
						toDelete.delete();
					} catch (Exception e) {}	
	            }
		 	}
	}
}