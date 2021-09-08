/*******************************************************************************************
* Copyright (C) 2021 PACIFICO PAUL
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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import library.FFMPEG;
import library.FFPROBE;

public class CropVideo {
	public static JDialog frame;
	private static JPanel image = new JPanel();
	public static JComboBox<String> comboPreset = new JComboBox<String>();
	
	/*
	 * Composants
	 */
	private JLabel quit;
	private JPanel topPanel;
	private JLabel topImage;	
	private static JPanel haut;
	private static JPanel bas;
	private static JPanel gauche;
	private static JPanel droit;
	private JButton btnOK;
	private JLabel lblPresets;
	private static JRadioButton caseManuel = new JRadioButton(Shutter.language.getProperty("caseManuel"));
	public static JLabel lblPad;
	private static JLabel lblRatio = new JLabel();
	private JSlider positionVideo;
	public static boolean sliderChange = false;
	@SuppressWarnings("unused")
	private int x;
	private static int y;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	/*
	 * Valeurs
	 */
    public static int ImageWidth;
    public static int ImageHeight;

    public static int containerWidth =  854;	
    public static int containerHeight = 480;
	
	public CropVideo() {	
		frame = new JDialog();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameCropVideo"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(878, 578);
		frame.setResizable(false);
		
		if (Functions.frame != null && Functions.frame.isVisible())
			frame.setModal(false);	
		else
			frame.setModal(true);
		
		frame.setAlwaysOnTop(true);
		
		
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
		image();
		boutons();
		loadImage("00","00","00");
		globalTimer();
		Shutter.ratioFinal = 0;

		Utils.changeDialogVisibility(frame, false);
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();		
		topPanel.setLayout(null);
			
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 1000, 52);
		
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
					Shutter.tempsRestant.setVisible(false);
		            Shutter.progressBar1.setValue(0);

		            Shutter.ratioFinal = 0;
		            Utils.changeDialogVisibility(frame, true);
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
	
		JLabel title = new JLabel(Shutter.language.getProperty("frameCropVideo"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);
		topPanel.setBounds(0, 0, 1000, 52);
		frame.getContentPane().add(topPanel);
		image.setBounds(12, 58, 854, 480);
		
		frame.getContentPane().add(image);
		
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
	
	private void image()
	{
		image.setLayout(null);        
		image.setOpaque(false);

		Color c = new Color(50,50,50);
		if (lblPad != null && lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
			c = Color.BLACK;
		
		haut = new JPanel();
		haut.setBackground(c);
		haut.setForeground(c);
		haut.setBounds(0, 0, 854, 0);
		image.add(haut);
		
		bas = new JPanel();
		bas.setForeground(c);
		bas.setBackground(c);
		bas.setBounds(0, 480, 854, 0);
		image.add(bas);
		
		gauche = new JPanel();
		gauche.setBackground(c);
		gauche.setForeground(c);
		gauche.setBounds(0, 0, 0, 480);
		image.add(gauche);
		
		droit = new JPanel();
		droit.setBackground(c);
		droit.setForeground(c);
		droit.setBounds(854, 0, 0, 480);
		image.add(droit);
		
		MouseListener mouseListener =  new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (comboPreset.isEnabled())
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (comboPreset.isEnabled())
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		        else if (y >= 0 && y <= ((int) (float) containerHeight/2 - 1))
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		        else if (y <= containerHeight && y >= ((int) (float) containerHeight/2 + 1))
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		};
		
		haut.addMouseListener(mouseListener);
		image.addMouseListener(mouseListener);
		bas.addMouseListener(mouseListener);
		
		MouseMotionListener mouseMotionListener = new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {	
				
				if (caseManuel.isSelected())
				{					                
			        if (y >= 0 && y <= ((int) (float) containerHeight/2 - 1) && comboPreset.isEnabled() == false) {
			            haut.setSize(haut.getSize().width, y);
			            bas.setSize(haut.getSize().width, y);
			            bas.setLocation(bas.getLocation().x, y + (image.getSize().height - (2 * haut.getSize().height)));
			        }
			        else if (y <= containerHeight && y >= ((int) (float) containerHeight/2 + 1) && comboPreset.isEnabled() == false) {
			            haut.setSize(haut.getSize().width, containerHeight - y);
			            bas.setSize(haut.getSize().width, containerHeight - y);
			            bas.setLocation(bas.getLocation().x, containerHeight - y + (image.getSize().height - (2 * bas.getSize().height)));
			        }		
			        
			        int borderSize = image.getSize().height - (2 * (haut.getSize().height));
			        lblRatio.setText(Shutter.language.getProperty("ratio") + " " + Math.floor(((float) containerWidth/borderSize) * 100) /100);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		};
		
		haut.addMouseMotionListener(mouseMotionListener);
		image.addMouseMotionListener(mouseMotionListener);
		bas.addMouseMotionListener(mouseMotionListener);

	}

	@SuppressWarnings("serial")
	private void boutons()	{
		btnOK = new JButton(Shutter.language.getProperty("btnApply"));
		btnOK.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnOK.setBounds(frame.getWidth() - 200 - 12, frame.getHeight() - 31, 200, 21);		
		frame.getContentPane().add(btnOK);
		
		btnOK.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				float ratio = 0.0f;
				if (caseManuel.isSelected() == false)
				{
					if (Float.parseFloat(comboPreset.getSelectedItem().toString()) == 1.33f) //Pour une parfaite précision
						ratio = 4/3f;
					else if (Float.parseFloat(comboPreset.getSelectedItem().toString()) == 1f)
						ratio = 1f;
					else if (Float.parseFloat(comboPreset.getSelectedItem().toString()) == 0.8f)
						ratio = 4/5f;
					else if (Float.parseFloat(comboPreset.getSelectedItem().toString()) == 1.77f)
						ratio = 16/9f;					
					else if (Float.parseFloat(comboPreset.getSelectedItem().toString()) < 1.77f)
						ratio = (float) (containerWidth / (image.getSize().width - (2 * (gauche.getSize().getWidth()) ) ) );
					else if (Float.parseFloat(comboPreset.getSelectedItem().toString()) == 2.33f)
						ratio = 21/9f;
					else
						ratio = (float) (containerWidth / (image.getSize().height - (2 * (haut.getSize().getHeight()) ) ) );
				}
				else
					ratio = (float) (containerWidth / (image.getSize().height - (2 * (haut.getSize().getHeight()) ) ) );
				
		        Shutter.ratioFinal = ratio;
				Shutter.tempsRestant.setVisible(false);
	            Shutter.progressBar1.setValue(0);
	            
				Utils.changeDialogVisibility(frame, true);
			}
			
		});
		
		final String listePreset[] = {"2.75", "2.55", "2.39", "2.35", "2.33", "1.91", "1.85", "1.77", "1.33", "1", "0.8"};
		
		comboPreset.setName("comboPreset");
		comboPreset.setModel(new DefaultComboBoxModel<String>(listePreset));
		comboPreset.setMaximumRowCount(10);
		comboPreset.setEnabled(false);
		comboPreset.setEditable(true);
		comboPreset.setSelectedIndex(-1);
		comboPreset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		comboPreset.setBounds(btnOK.getX() - 63 - 12, btnOK.getY(), 63, 22);		
		frame.getContentPane().add(comboPreset);
		
		comboPreset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (comboPreset.getSelectedItem().toString().isEmpty() == false)
				{
					try {
						
						String r[] = FFPROBE.imageResolution.split("x");
						String original = String.valueOf((float) Integer.parseInt(r[0]) / Integer.parseInt(r[1]));
	
						if (original.length() > 4)
							original = original.substring(0,4);					
												
						//On affiche les côtés
						if (Float.parseFloat(comboPreset.getSelectedItem().toString()) < Float.parseFloat(original))
						{
							Float ratio = (Float.parseFloat(comboPreset.getSelectedItem().toString()) * containerHeight);		        	
				        	int largeur = (int) Math.ceil((image.getSize().width - ratio) / 2);
				        	
				        	gauche.setSize(largeur, gauche.getSize().height);	        	
				        	droit.setSize(largeur, gauche.getSize().height);
			
				        	droit.setLocation(largeur + (image.getSize().width - (2 * gauche.getSize().width)), droit.getLocation().y);
				        	
				    		haut.setBounds(0, 0, 854, 0);
				    		bas.setBounds(0, 480, 854, 0);		
				    	}
						else
						{
							Float ratio = (containerWidth / Float.parseFloat(comboPreset.getSelectedItem().toString()) );		        	
				        	int hauteur = (int) Math.ceil((image.getSize().height - ratio) / 2);
				        	
				        	haut.setSize(haut.getSize().width, hauteur);	        	
				        	bas.setSize(haut.getSize().width, hauteur);
			
				        	bas.setLocation(bas.getLocation().x, hauteur + (image.getSize().height - (2 * haut.getSize().height)));
				        	
				    		gauche.setBounds(0, 0, 0, 480);
				    		droit.setBounds(854, 0, 0, 480);
						}		        	
		            
			        	lblRatio.setText(Shutter.language.getProperty("ratio") + " " + comboPreset.getSelectedItem().toString());
		            
			        } catch (Exception er) {
			        	if (comboPreset.getSelectedItem().toString() != "")
			        		JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("wrongValue"), Shutter.language.getProperty("wrongFormat"), JOptionPane.ERROR_MESSAGE);
			        }	
				}
			}
			
		});
		
		lblPresets = new JLabel(Shutter.language.getProperty("lblPresets"));
		lblPresets.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 13));
		lblPresets.setBounds(comboPreset.getX() - lblPresets.getPreferredSize().width - 4, btnOK.getY() + 2, lblPresets.getPreferredSize().width, 16);		
		frame.getContentPane().add(lblPresets);
						
		caseManuel.setName("caseManuel");
		caseManuel.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		caseManuel.setSelected(true);
		caseManuel.setSize(caseManuel.getPreferredSize().width, 16);
		caseManuel.setLocation(lblPresets.getX() - caseManuel.getWidth() - 12, lblPresets.getY());		
		frame.getContentPane().add(caseManuel);
		
		caseManuel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (caseManuel.isSelected())
				{
					comboPreset.setEnabled(false);
					comboPreset.setSelectedItem("");
		    		gauche.setBounds(0, 0, 0, 480);
		    		droit.setBounds(854, 0, 0, 480);
				}
				else
				{
					comboPreset.setEnabled(true);			
					comboPreset.setSelectedIndex(0);
				}				
			}
			
		});
				
		positionVideo = new JSlider();
		if (Shutter.scanIsRunning)
		{
			File dir = new File(Shutter.liste.firstElement());
        	for (File f : dir.listFiles())
        	{
	        	if (f.isHidden() == false && f.isFile())
	        	{    	    
	        		FFPROBE.Data(f.toString());
	        	}
        	}
		}
		else		 
		{
			if (Utils.inputDeviceIsRunning == false)
				FFPROBE.Data(Shutter.liste.firstElement());
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
		} while (FFPROBE.totalLength == 0 && FFPROBE.isRunning);
		
		positionVideo.setMaximum(FFPROBE.totalLength);
		positionVideo.setValue(0);		
		positionVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		positionVideo.setBounds(12, frame.getHeight() - 32, 181, 22);	
		
		lblPad = new JLabel() {			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")) || lblPad.getText().equals(Shutter.language.getProperty("lblCrop")))
				{
					if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
						g.setColor(Color.BLACK);
					else
						g.setColor(new Color(50,50,50));
					
					g.fillRect(0, 0, 8, 16);
					g.fillRect((70 - 8), 0, (70 - 8), 16);
				}
			}
		};
		lblPad.setText(Shutter.language.getProperty("lblCrop"));
		lblPad.setName("lblPad");
		lblPad.setBackground(new Color(80, 80, 80));
		lblPad.setHorizontalAlignment(SwingConstants.CENTER);
		lblPad.setOpaque(true);
		lblPad.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 11));
		lblPad.setBounds(positionVideo.getX() + positionVideo.getWidth() + 7, positionVideo.getY() + 3, 70, 16);
		frame.getContentPane().add(lblPad);
		
		lblPad.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
				{
					lblPad.setText(Shutter.language.getProperty("lblCrop"));
				}
				else
				{
					lblPad.setText(Shutter.language.getProperty("lblPad"));
				}
				
				Color c = new Color(50,50,50);
				if (lblPad != null && lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
					c = Color.BLACK;
				
				haut.setBackground(c);
				bas.setBackground(c);
				gauche.setBackground(c);
				droit.setBackground(c);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		
		lblRatio.setText(Shutter.language.getProperty("ratio") + "100.00"); //Needed for preferredSize
		lblRatio.setName("lblRatio");
		lblRatio.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblRatio.setForeground(Utils.themeColor);
		lblRatio.setSize(lblRatio.getPreferredSize().width, 16);
		lblRatio.setLocation(lblPad.getX() + lblPad.getWidth() + 7, lblPresets.getY() - 1);		
		frame.getContentPane().add(lblRatio);
		
		//Contournement d'un bug
		Component[] components = frame.getContentPane().getComponents();		    
		boolean addToFrame = true;
	    for(int i = 0; i < components.length; i++) {
	    	if (components[i] instanceof JSlider)
	    		addToFrame = false;
	    }	    
	    if (addToFrame)
	    	frame.getContentPane().add(positionVideo);
		
		positionVideo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {			
					//On attends que le slider soit relaché pour faire le changement
					Thread runProcess = new Thread(new Runnable()  {
						@Override
						public void run() {
							DecimalFormat tc = new DecimalFormat("00");			
							String h = String.valueOf(tc.format((positionVideo.getValue() / 3600000)));
							String m = String.valueOf(tc.format((positionVideo.getValue() / 60000) % 60));
							String s = String.valueOf(tc.format((positionVideo.getValue() / 1000) % 60));
							
							loadImage(h,m,s);
						}
					});
					runProcess.start();					
					
					sliderChange = false;
			}
			
		});
		
	}

	public static void loadImage(String h, String m, String s) {
        try
        {
        	String fichier = Shutter.liste.firstElement();
			if (Shutter.scanIsRunning)
			{
	            File dir = new File(Shutter.liste.firstElement());
	            for (File f : dir.listFiles())
	            {
	            	if (f.isHidden() == false && f.isFile())
	            	{    	
	            		fichier = f.toString();
	            		break;
	            	}
	            }
			}
			
			Console.consoleFFMPEG.append(System.lineSeparator() + Shutter.language.getProperty("tempFolder") + " "  + Shutter.dirTemp + System.lineSeparator() + System.lineSeparator());		
				
    	  	//On récupère la taille du logo pour l'adater à l'image vidéo
			if (Utils.inputDeviceIsRunning == false)
				FFPROBE.Data(fichier);	
			
			do {
				Thread.sleep(100);
			} while (FFPROBE.isRunning);
							
			//Ratio Widescreen
			if ((float) ImageWidth/ImageHeight >= (float) 854/480)
			{
				containerHeight = (int) Math.ceil((float) 854 / ((float) ImageWidth / ImageHeight));
				containerWidth = 854;
			}
			else
			{
				containerWidth = (int) Math.ceil((float) ((float) ImageWidth / ImageHeight) * 480);	
				containerHeight = 480;
			}
			
			//Envoi de la commande
			String cmd = " -vframes 1 -an -vf scale=" + containerWidth +":" + containerHeight + " -c:v bmp -sws_flags bicubic -f image2pipe pipe:-";
			
			boolean isVisible = false;
			if (Shutter.inputDeviceIsRunning) //Screen capture			
			{				
				if (frame.isVisible())
				{
					frame.setVisible(false);
					isVisible = true;
				}
				
				FFMPEG.run(" -v quiet " +  RecordInputDevice.setInputDevices() + cmd);
			}
			else					
			{					
      			FFMPEG.run(" -ss "+h+":"+m+":"+s+".0 -v quiet -i " + '"' + fichier + '"' + cmd);
     		}	
			
			do {
				Thread.sleep(10);
			} while (FFMPEG.process.isAlive() == false);
			
			if (isVisible)
				frame.setVisible(true);
			
			InputStream videoInput = FFMPEG.process.getInputStream();
 
			InputStream is = new BufferedInputStream(videoInput);
			Image imageBMP = ImageIO.read(is);
			
	        if (FFMPEG.error == false)
            {	
		        image.removeAll();  
		        
				image.add(haut);	    		
				image.add(bas);	    		
				image.add(gauche);
				image.add(droit);
		       	
				ImageIcon imageIcon = new ImageIcon(imageBMP);
	    		JLabel newImage = new JLabel(imageIcon);
	            imageIcon.getImage().flush();	
	            
	    		newImage.setHorizontalAlignment(SwingConstants.CENTER);
	    		newImage.setBounds(0, 0, containerWidth, containerHeight);
				
				image.setLocation(12 + ((854 - containerWidth) / 2), 58 + (int) ((float)(480 - containerHeight) / 2));
				image.setSize(newImage.getSize());	
				
				image.add(newImage);
				image.repaint();
				frame.getContentPane().repaint();
		
				if (caseManuel.isSelected())
				{
					int borderSize = image.getSize().height - (2 * (haut.getSize().height));
					lblRatio.setText(Shutter.language.getProperty("ratio") + " " + Math.floor(((float) containerWidth/borderSize) * 100) /100);
					bas.setLocation(bas.getLocation().x, image.getHeight() - bas.getHeight());
				}
				else
				{
					lblRatio.setText(Shutter.language.getProperty("ratio") + " " + comboPreset.getSelectedItem().toString());
					comboPreset.setSelectedItem(comboPreset.getSelectedItem().toString()); //Equivalent doClick();
				}
				
			        
				Shutter.tempsRestant.setVisible(false);
		        Shutter.progressBar1.setValue(0);
            }
        }
	    catch (Exception e)
	    {
 	       JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("cantLoadFile"), Shutter.language.getProperty("error"), JOptionPane.ERROR_MESSAGE);
	    }
        finally {
        	
        	Shutter.enableAll();     
        	if (RenderQueue.frame != null && RenderQueue.frame.isVisible())
				Shutter.btnStart.setText(Shutter.language.getProperty("btnAddToRender"));
			else
				Shutter.btnStart.setText(Shutter.language.getProperty("btnStartFunction"));
        }
	}
	
	private void globalTimer() {	     
		TimerTask task = new TimerTask()
		{
			@Override
			public void run() 
			{
				x = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x - image.getLocation().x;
	        	y = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y - image.getLocation().y;
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 10);
		
	}

	public static void loadSettings(File encFile) {
		
		Thread t = new Thread (new Runnable() 
		{
			@SuppressWarnings("rawtypes")
			@Override
			public void run() {
				
			try {
				do {
					Thread.sleep(100);
				} while (frame == null && frame.isVisible() == false);
					
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
						
						for (Component p : frame.getContentPane().getComponents())
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
									//Value
									if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
									{
										if (((JRadioButton) p).isSelected() == false)
											((JRadioButton) p).setSelected(true);
									}
									else
									{
										if (((JRadioButton) p).isSelected())
											((JRadioButton) p).setSelected(true);
									}
									//State
									((JRadioButton) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JRadioButton) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
								else if (p instanceof JComboBox)
								{						
									comboPreset.setSelectedIndex(0); //IMPORTANT contourne un bug
										
									//Value
									((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																		
									//State
									((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
									
									//Visible
									((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								}
							}
						}
					}
				}
				
				if (caseManuel.isSelected())
				{					  
					String s[] = lblRatio.getText().split(":");
					float ratio = Float.parseFloat(s[1].replace(" ", ""));	
					
		        	int hauteur = (int) (Math.ceil(image.getSize().height - image.getSize().width / ratio) / 2);
		        	
		        	haut.setSize(haut.getSize().width, hauteur);	        	
		        	bas.setSize(haut.getSize().width, hauteur);
	
		        	bas.setLocation(bas.getLocation().x, hauteur + (image.getSize().height - (2 * haut.getSize().height)));
		        	
		    		gauche.setBounds(0, 0, 0, 480);
		    		droit.setBounds(854, 0, 0, 480);
				}
				
				Color c = new Color(50,50,50);
				if (lblPad != null && lblPad.getText().equals(Shutter.language.getProperty("lblPad")))
					c = Color.BLACK;
				
				haut.setBackground(c);
				bas.setBackground(c);
				gauche.setBackground(c);
				droit.setBackground(c);
				
			} catch (Exception e) {}	
			}					
		});
		t.start();	
	}
}