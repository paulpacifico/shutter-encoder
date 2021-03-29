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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.Image;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import library.FFMPEG;
import library.FFPROBE;

import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JScrollPane;

	public class SceneDetection {
	public static JFrame frame;
	
	/*
	 * Composants
	 */
	private static JPanel topPanel;
	private boolean drag;
	private JLabel quit;
	private JLabel reduce;
	private JLabel topImage;
	private JLabel bottomImage;
	private static JLabel lblFlecheBas;
	private static JButton btnEDL;
	public static JLabel lblEdit;
	public static JTable table;
	public static DefaultTableModel tableRow;
	private static JSpinner tolerance;
	public static JButton btnAnalyse;
	public static JScrollPane scrollPane;
	
	public static File sortieDossier;
	public static File sortieFichier;
	
	public static boolean isRunning = false;
	private static StringBuilder errorList = new StringBuilder();
	private static int complete;

	public SceneDetection(boolean runAnalyse) {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(50,50,50));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameDetectionCoupe"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(400, 600);
		frame.setResizable(false);
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
			frame.setLocation(Shutter.frame.getLocation().x - frame.getSize().width -20, Shutter.frame.getLocation().y);
	    	
		}
				
		topPanel();
		contenu();
		
		drag = false;
				
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag && frame.getSize().height > 90)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);	
			        scrollPane.setSize(scrollPane.getSize().width, frame.getSize().height - 160);
			    	lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);		
					btnEDL.setBounds(9, 89 + scrollPane.getHeight() + 6, scrollPane.getWidth(), 21);
					lblEdit.setBounds(frame.getWidth() / 2 - 119, 89 + scrollPane.getHeight() + 31, 245, 15);
		       	}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height - 20)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				 else 
				{
					if (drag == false)
					 frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}				
			
		});
		
		frame.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {				
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
					drag = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {		
				drag = false;
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (frame.getSize().height <= 90)
				{
					frame.setSize(frame.getSize().width, 100);
		    		lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});		

		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we)
		    {
		       
			   frame.toFront();
		    }
		});
		
    	frame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e2)
		    {
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
		    	frame.setShape(shape1);
		    }
 		});
		    	
		Utils.changeFrameVisibility(frame, false);
		
		if (runAnalyse)
			btnAnalyse.doClick();
	}
		
	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private void topPanel() {	
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBounds(0, 0, frame.getSize().width, 51);
		
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
				
		ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		Image scaledImage = image.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_SMOOTH);
		ImageIcon header = new ImageIcon(scaledImage);
		bottomImage = new JLabel(header);
		bottomImage.setBounds(0 ,0, frame.getSize().width, 51);
			
		JLabel title = new JLabel(Shutter.language.getProperty("frameDetectionCoupe"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		reduce = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/reduce2.png")));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 21,0,21, 21);
			
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce3.png"))));
				accept = true;
			}

			@SuppressWarnings("static-access")
			@Override
			public void mouseReleased(MouseEvent e) {		
				
				if (accept)
				{							
					
					frame.setState(frame.ICONIFIED);	
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {			
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce.png"))));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new ImageIcon((getClass().getClassLoader().getResource("contents/reduce2.png"))));
				accept = false;
			}
			
			
		});
				
		topPanel.add(quit);	
		topPanel.add(reduce);
		topPanel.add(topImage);
		topPanel.add(bottomImage);
		
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
					if (FFMPEG.runProcess.isAlive())
						Shutter.btnCancel.doClick();
					
					if (Shutter.btnCancel.isEnabled() == false)
					{
						if (sortieDossier.exists())
							deleteDirectory(sortieDossier);
						
						Utils.changeFrameVisibility(frame, true);	
						}
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
		
		topPanel.setBounds(0, 0, frame.getSize().width, 51);
		frame.getContentPane().add(topPanel);						

		bottomImage.addMouseListener(new MouseListener() {

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
		 		
		bottomImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePosition.mouseX, MouseInfo.getPointerInfo().getLocation().y - MousePosition.mouseY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	private void contenu() {
		scrollPane = new JScrollPane();
		scrollPane.setBounds(9, 89, 380, frame.getSize().height - 160);
		frame.getContentPane().add(scrollPane);
		
		JLabel lblPourcentage = new JLabel("%");
		lblPourcentage.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblPourcentage.setBounds(143, 63, 11, 15);
		frame.getContentPane().add(lblPourcentage);
			
		btnAnalyse = new JButton(Shutter.language.getProperty("btnAnalyse"));
		btnAnalyse.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnAnalyse.setBounds(164, 59, 223, 21);
		frame.getContentPane().add(btnAnalyse);
		
		btnAnalyse.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tolerance.setEnabled(false);
				btnAnalyse.setEnabled(false);
				btnEDL.setEnabled(false);
				lblEdit.setVisible(false);
				if (sortieDossier != null && sortieDossier.exists())
					deleteDirectory(sortieDossier);
				

				if (tableRow != null)
					tableRow.setRowCount(0);
								
				runAnalyse();
			}
			
		});
		
		JLabel lblSensibilit = new JLabel(Shutter.language.getProperty("lblSensibility"));
		lblSensibilit.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSensibilit.setBounds(10, 62, lblSensibilit.getPreferredSize().width, 15);
		frame.getContentPane().add(lblSensibilit);
		
		tolerance = new JSpinner(new SpinnerNumberModel(80, 0, 100, 10));
		tolerance.setBounds(86, 59, 55, 21);
		frame.getContentPane().add(tolerance);	
		
		lblFlecheBas = new JLabel("▲▼");
		lblFlecheBas.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlecheBas.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 20));
		lblFlecheBas.setSize(new Dimension(frame.getSize().width, 20));
		lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
		lblFlecheBas.setVisible(true);		
		frame.getContentPane().add(lblFlecheBas);
		
		btnEDL = new JButton(Shutter.language.getProperty("btnEDL"));
		btnEDL.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnEDL.setEnabled(false);
		btnEDL.setBounds(9, 89 + scrollPane.getHeight() + 6, scrollPane.getWidth(), 21);
		frame.getContentPane().add(btnEDL);
		
		btnEDL.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
    		
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				frame.setVisible(false);
				
				final FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("saveEDL"), FileDialog.SAVE);
				if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
					dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
				else
					dialog.setDirectory(System.getProperty("user.home") + "\\Desktop");
				dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
			    				
			    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));		
				frame.setVisible(true);
				
				frame.toFront();
			    
			    if (dialog.getFile() != null)
				{ 							
			    	Thread runProcess = new Thread(new Runnable()  {
					@Override
					public void run() {
						
					PrintWriter writer = null;
				    try {
						writer = new PrintWriter(dialog.getDirectory() + dialog.getFile().replace(".edl", "") + ".edl", "UTF-8");
						NumberFormat formatEDL = new DecimalFormat("000000");
						writer.println("TITLE : " + dialog.getFile());
						
						int countItemsEDL = 0;
				    	for (int i = 0 ; i < tableRow.getRowCount(); i++)
						{   							
							String timecodeStart = String.valueOf(tableRow.getValueAt(i, 2));
							String tcStart[] = timecodeStart.split(":");
							int tcStartToMs = (int) (Integer.valueOf(tcStart[0]) * 3600000 + Integer.valueOf(tcStart[1]) * 60000 + Integer.valueOf(tcStart[2]) * 1000 + Integer.valueOf(tcStart[3]) * (1000 / FFPROBE.currentFPS));
							
			    			NumberFormat formatter = new DecimalFormat("00");
			    			String tcInTimeLine = (formatter.format(tcStartToMs / 3600000)) 
			    					+ ":" + (formatter.format((tcStartToMs / 60000) % 60))
			    					+ ":" + (formatter.format((tcStartToMs / 1000) % 60)) 		
			    					+ ":" + (formatter.format((int) (tcStartToMs / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
			    			
			    			//Timecode Initial de la vidéo
			    			int preTcMs; 
			    			if (FFPROBE.timecode1 != "" && FFPROBE.timecode2 != "" && FFPROBE.timecode3 != "" && FFPROBE.timecode4 != "" )
			    			{    			
		    					preTcMs = (int) (Integer.parseInt(FFPROBE.timecode1) * 3600000 
		    					+ Integer.parseInt(FFPROBE.timecode2) * 60000 
		    					+ Integer.parseInt(FFPROBE.timecode3) * 1000 
		    					+ (Integer.parseInt(FFPROBE.timecode4) * (1000 / FFPROBE.currentFPS)));
			    			}
			    			else
			    				preTcMs = 0;
			    			
			    			//Timecode de la coupe
			    			int preTcInVideo = (int) (Integer.parseInt(tcStart[0]) * 3600000 
			    					+ Integer.parseInt(tcStart[1]) * 60000 
			    					+ Integer.parseInt(tcStart[2]) * 1000 
			    					+ (Integer.parseInt(tcStart[3]) * (1000 / FFPROBE.currentFPS))); 	
			    			
			    			//Total
			    			int totalTcIn = (preTcMs + preTcInVideo);
			    			
			    			//Mise en forme
			    			String tcInVideo = (formatter.format(totalTcIn / 3600000)) 
			    					+ ":" + (formatter.format((totalTcIn / 60000) % 60))
			    					+ ":" + (formatter.format((totalTcIn / 1000) % 60)) 		
			    					+ ":" + (formatter.format((int) (totalTcIn / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));							
			    			
			    			int tcEndToMS;
			    			int preTcOutVideo;
			    			String tcOutVideo;
			    			if (i < tableRow.getRowCount() - 1)
			    			{
								String timecodeEnd = String.valueOf(tableRow.getValueAt(i + 1, 2));
								String tcEnd[] = timecodeEnd.split(":");
								tcEndToMS = (int) (Integer.valueOf(tcEnd[0]) * 3600000 + Integer.valueOf(tcEnd[1]) * 60000 + Integer.valueOf(tcEnd[2]) * 1000 + Integer.valueOf(tcEnd[3]) * (1000 / FFPROBE.currentFPS));
	
				    			//Timecode de la coupe
				    			 preTcOutVideo = (int) (Integer.parseInt(tcEnd[0]) * 3600000 
				    					+ Integer.parseInt(tcEnd[1]) * 60000 
				    					+ Integer.parseInt(tcEnd[2]) * 1000 
				    					+ (Integer.parseInt(tcEnd[3]) * (1000 / FFPROBE.currentFPS))); 	
			    			}
			    			else //Si c'est le dernier fichier on récupère la durée de la vidéo
							{								
								String tc[] = FFPROBE.getVideoLengthTC.split(":");
								int h = (Integer.valueOf(tc[0]) * 3600000);
								int m = (Integer.valueOf(tc[1]) * 60000);
								int s = (Integer.valueOf(tc[2]) * 1000);
								int f = (int) (Integer.valueOf(tc[3]) * 10);
										
								tcEndToMS = (h+m+s+f);
															
				    			//Timecode de la coupe
				    			preTcOutVideo = tcEndToMS;			
							}							
							
			    			//Total
			    			int totalTcOut = (preTcMs + preTcOutVideo);
			    			
			    			//Mise en forme
			    			tcOutVideo = (formatter.format(totalTcOut / 3600000)) 
			    					+ ":" + (formatter.format((totalTcOut / 60000) % 60))
			    					+ ":" + (formatter.format((totalTcOut / 1000) % 60)) 		
			    					+ ":" + (formatter.format((int) (totalTcOut / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));	
			    			
			    			
			    			String tcOutTimeLine = (formatter.format(tcEndToMS / 3600000)) 
			    					+ ":" + (formatter.format((tcEndToMS / 60000) % 60))
			    					+ ":" + (formatter.format((tcEndToMS / 1000) % 60)) 		
			    					+ ":" + (formatter.format((int) (tcEndToMS / (1000 / FFPROBE.currentFPS) % FFPROBE.currentFPS)));
			    			
							String cutName;
							String ext = sortieFichier.toString().substring(sortieFichier.toString().lastIndexOf("."));
							if (i % 2 == 0)
								cutName = sortieFichier.toString().replace(" ", "_");
							else
								cutName = sortieFichier.toString().replace(" ", "_").replace(ext, "_" + Shutter.language.getProperty("cut") + ext);
			    																	
							writer.println(formatEDL.format(countItemsEDL + 1) + "  " + cutName + " V     C        " + tcInVideo + " " + tcOutVideo + " " + tcInTimeLine + " " + tcOutTimeLine);
							writer.println(formatEDL.format(countItemsEDL + 2) + "  " + cutName + " A     C        " + tcInVideo + " " + tcOutVideo + " " + tcInTimeLine + " " + tcOutTimeLine);
							writer.println(formatEDL.format(countItemsEDL + 3) + "  " + cutName + " A2    C        " + tcInVideo + " " + tcOutVideo + " " + tcInTimeLine + " " + tcOutTimeLine);
							
							countItemsEDL += 3;	
						} //End for
				    	
				    } catch (Exception e){
				    	System.out.println(e);
				    }
				    				    
					writer.close();	
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("fileCreated"), "EDL", JOptionPane.INFORMATION_MESSAGE);
						
					}//End run
					
			    }); runProcess.start();					
				}
			}			
		});
		
		lblEdit = new JLabel(Shutter.language.getProperty("lblEdit"));
		lblEdit.setForeground(Utils.themeColor);
		lblEdit.setHorizontalAlignment(SwingConstants.CENTER);
		lblEdit.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblEdit.setBounds(frame.getWidth() / 2 - 119, 89 + scrollPane.getHeight() + 31, 245, 15);
		lblEdit.setVisible(false);
		frame.getContentPane().add(lblEdit);
									
	}
	
	@SuppressWarnings("serial")
	private static void newTable() {
		ImageIcon imageIcon = new ImageIcon(SceneDetection.sortieDossier.toString() + "/0.png");
		ImageIcon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(142, 80, Image.SCALE_DEFAULT));			
		Object[][] firstImage = {{"1", icon, "00:00:00:00"}};

		tableRow = new DefaultTableModel(firstImage, new String[] {"N\u00B0", "Plans", "Timecode"});
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        table = new JTable(tableRow)
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		table.setShowVerticalLines(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(80);
		table.getColumnModel().getColumn(0).setPreferredWidth(18);
		table.getColumnModel().getColumn(1).setPreferredWidth(table.getColumnModel().getColumn(1).getPreferredWidth());
		table.setBounds(9, 89, 380, frame.getHeight() - 134);
		scrollPane.setViewportView(table);
		
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && FFMPEG.isRunning == false && table.getSelectedRowCount() > 0)
				{	
					File imageToDelete = new File(sortieDossier.toString() + "/" + (table.getSelectedRow()) + ".png");
					imageToDelete.delete();
					
					int i = 0; //On renomme toutes les images
					for (File file : sortieDossier.listFiles())
					{
						if (file.toString().substring(file.toString().lastIndexOf(".")).equals(".png"))
							file.renameTo(new File(sortieDossier.toString() + "/" + i + ".png"));
						
						i++;			
					}
					
					tableRow.removeRow(table.getSelectedRow());	
					
					//On remet les chiffres dans l'ordre
					for (int n = 0 ; n < tableRow.getRowCount() ; n++)
					{	
						table.getModel().setValueAt(String.valueOf(n + 1), n, 0);	
					}					

					table.repaint();
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {	
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		});
				
		final JPopupMenu popupListe = new JPopupMenu();
		JMenuItem visualiser = new JMenuItem(Shutter.language.getProperty("menuItemVisualiser"));
		JMenuItem ouvrirDossier = new JMenuItem(Shutter.language.getProperty("menuItemOuvrirDossier"));
		JMenuItem copieTimeCode = new JMenuItem(Shutter.language.getProperty("menuItemCopyTimecode"));
		
		visualiser.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
	            try {
					Desktop.getDesktop().open(new File(sortieDossier + "/" + table.getSelectedRow() + ".png"));
				} catch (IOException e1) {}			
			}		
		});

		ouvrirDossier.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
	            try {
					Desktop.getDesktop().open(sortieDossier);
				} catch (IOException e1) {}
			}		
		});
		
		copieTimeCode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String timecode = tableRow.getValueAt(table.getSelectedRow(), 2).toString();
				StringSelection stringSelection = new StringSelection(timecode);
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}		
		});
		
		popupListe.add(visualiser);
		popupListe.add(ouvrirDossier);
		popupListe.add(copieTimeCode);
		
		table.addMouseListener(new MouseAdapter(){
		    @Override
		    public void mouseClicked(MouseEvent e){
		    	//Double clic
		        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && table.getSelectedRowCount() == 1)
		        {
		            try {
						Desktop.getDesktop().open(new File(sortieDossier + "/" + table.getSelectedRow() + ".png"));
					} catch (IOException e1) {}
		        }
		        
		        //Clic droit
				if (e.getButton() == MouseEvent.BUTTON3 || (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == ActionEvent.CTRL_MASK && e.getButton() == MouseEvent.BUTTON1)
				{
					popupListe.show(table, e.getX() - 30, e.getY());
				}					
		    }
		});

	}

	public static void runAnalyse() {
		
		Thread thread = new Thread(new Runnable(){			
			@Override
			public void run() {				
				complete = 0;
			
				Shutter.lblTermine.setText(Utils.completedFiles(complete));

					
				for (int i = 0 ; i < Shutter.liste.getSize() ; i++)
				{
					File file = new File(Shutter.liste.getElementAt(i));
					
					if (i > 0)
					{
						new SceneDetection(false);
					}
			            
					try {
						// Analyse des données					 
						 FFPROBE.Data(file.toString());						 
						 do
							Thread.sleep(100);	
						 while (FFPROBE.isRunning);
							 																	
						String fichier = file.getName();
						Shutter.lblEncodageEnCours.setText(fichier);
						
						String sortie = file.getParent();					
						final String extension =  fichier.substring(fichier.lastIndexOf("."));
						sortieDossier =  new File(sortie + "/" + fichier.replace(extension, ""));		
						sortieDossier.mkdir();
						
						sortieFichier =  new File(file.getName());
						
						//Envoi de la commande
						String cmd;
						cmd = " -f image2 -vframes 1 ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + "-y " + '"'  + sortieDossier.toString() + "/0.png" + '"');
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());										
						
						//On créer le tableau ici après la première image
						newTable();
					        		
						//Envoi de la commande
						String tol = String.valueOf((float) (100 - Integer.valueOf(application.SceneDetection.tolerance.getValue().toString())) / 100);
						cmd = " -vf select=" + '"' + "gt(scene\\," + tol  + ")" + '"' + ",showinfo -vsync 2 -f image2 ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + "-y " + '"'  + sortieDossier.toString() + "/%01d.png" + '"');		
						
						//Attente de la fin de FFMPEG
						do
							Thread.sleep(100);
						while(FFMPEG.runProcess.isAlive());						
					
						actionsDeFin();
						
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}//End Try
				}//End for
				
				//Affichage des erreurs
				if (errorList.length() != 0)
					JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("notProcessedFiles") + " " + '\n' + '\n' + errorList.toString() ,Shutter.language.getProperty("encodingError"), JOptionPane.ERROR_MESSAGE);
				errorList.setLength(0);
				
				FFMPEG.enfOfFunction();
			}//run
			
		});
		thread.start();
		
    }//main

	private static void actionsDeFin() {
		//Erreurs
		if (FFMPEG.error)
		{
		    errorList.append(System.lineSeparator());
		}

		//Fichiers terminés
		if (FFMPEG.cancelled == false && FFMPEG.error == false)
		{
			complete++;
			Shutter.lblTermine.setText(Utils.completedFiles(complete));
		}
		
		tolerance.setEnabled(true);
		btnAnalyse.setEnabled(true);
		btnEDL.setEnabled(true);
		lblEdit.setVisible(true);

	}
	
	public static boolean deleteDirectory(File dir) {
	    if(! dir.exists() || !dir.isDirectory())    {
	        return false;
	    }

	    String[] files = dir.list();
	    for(int i = 0, len = files.length; i < len; i++)    {
	        File f = new File(dir, files[i]);
	        if(f.isDirectory()) {
	            deleteDirectory(f);
	        }else   {
	            f.delete();
	        }
	    }
	    return dir.delete();
	}
	
}