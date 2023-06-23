/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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
import javax.swing.border.MatteBorder;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;
import library.FFPROBE;
import settings.FunctionUtils;

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
	private static JLabel lblBottomArrow;
	private static JButton btnEDL;
	public static JLabel lblEdit;
	public static JTable table;
	public static DefaultTableModel tableRow;
	private static JSpinner tolerance;
	public static JButton btnAnalyze;
	public static JScrollPane scrollPane;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	public static File outputFolder;
	public static File outputFile;
	
	public static boolean isRunning = false;
	private static StringBuilder errorList = new StringBuilder();
	private static int complete;

	public SceneDetection(boolean runAnalyse) {
		
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(35,35,35));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle(Shutter.language.getProperty("frameDetectionCoupe"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(400, 584);
		frame.setMinimumSize(new Dimension(400, 138));
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
		content();
		
		drag = false;
				
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (drag)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);	
			        scrollPane.setSize(scrollPane.getSize().width, frame.getSize().height - 136);
			    	lblBottomArrow.setLocation(0, frame.getSize().height - lblBottomArrow.getSize().height);		
					btnEDL.setBounds(9, 65 + scrollPane.getHeight() + 6, scrollPane.getWidth(), 21);
					lblEdit.setLocation(frame.getWidth() / 2 - lblEdit.getWidth() / 2, 65 + scrollPane.getHeight() + 31);
		       	}	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if ((MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y) > frame.getSize().height - 20)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				}
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
				
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
		        frame.setShape(shape1);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (frame.getSize().height <= 90)
				{
					frame.setSize(frame.getSize().width, 100);
		    		lblBottomArrow.setLocation(0, frame.getSize().height - lblBottomArrow.getSize().height);
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
		    	if (System.getProperty("os.name").contains("Mac") && drag)
				{
					frame.setShape(null);
				}
				else
				{
					Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		            shape1.add(shape2);
		    		frame.setShape(shape1);
				}
		    }
 		});
		    	
		Utils.changeFrameVisibility(frame, false);
		
		if (runAnalyse)
			btnAnalyze.doClick();
	}
		
	private void topPanel() {	
		
		topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(35,35,35));
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
		
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);

		JLabel title = new JLabel(Shutter.language.getProperty("frameDetectionCoupe"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 1, frame.getWidth(), 24);
		title.setFont(new Font(Shutter.magnetoFont, Font.PLAIN, 17));
		topPanel.add(title);
		
		topImage = new JLabel();
		topImage.setBackground(new Color(40,40,40));
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(65, 65, 65)));	
		topImage.setBounds(title.getBounds());
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 20, 4, 15, 15);
		
		reduce.addMouseListener(new MouseListener(){
			
			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {			
			}

			@Override
			public void mousePressed(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce_pressed.svg", 15, 15));
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
				reduce.setIcon(new FlatSVGIcon("contents/reduce_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				reduce.setIcon(new FlatSVGIcon("contents/reduce.svg", 15, 15));
				accept = false;
			}
			
			
		});
				
		topPanel.add(quit);	
		topPanel.add(reduce);
		topPanel.add(topImage);
		
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
					if (FFMPEG.runProcess.isAlive())
						Shutter.btnCancel.doClick();
					
					if (Shutter.btnCancel.isEnabled() == false)
					{
						if (outputFolder.exists())
							deleteDirectory(outputFolder);
						
						Utils.changeFrameVisibility(frame, true);	
						}
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
		
		topPanel.setBounds(0, 0, frame.getSize().width, 28);
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
		 		
		topImage.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	private void content() {
		
		scrollPane = new JScrollPane();
		scrollPane.setBackground(new Color(35,35,35));
		scrollPane.setBounds(9, 65, 380, frame.getSize().height - 136);
		frame.getContentPane().add(scrollPane);
			
		JLabel lblSensibility = new JLabel(Shutter.language.getProperty("lblSensibility"));
		lblSensibility.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSensibility.setBounds(10, 38, lblSensibility.getPreferredSize().width, 15);
		frame.getContentPane().add(lblSensibility);
		
		tolerance = new JSpinner(new SpinnerNumberModel(80, 0, 100, 10));
		tolerance.setBounds(lblSensibility.getX() + lblSensibility.getWidth() + 4, lblSensibility.getY() - 3, 55, 21);
		frame.getContentPane().add(tolerance);	
		
		JLabel lblPourcentage = new JLabel("%");
		lblPourcentage.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblPourcentage.setBounds(tolerance.getX() + tolerance.getWidth() + 4, lblSensibility.getY(), 11, 15);
		frame.getContentPane().add(lblPourcentage);
		
		btnAnalyze = new JButton(Shutter.language.getProperty("btnAnalyse"));
		btnAnalyze.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnAnalyze.setLocation(lblPourcentage.getX() + lblPourcentage.getWidth() + 7, lblSensibility.getY() - 3);
		btnAnalyze.setSize(frame.getWidth() - (tolerance.getX() + tolerance.getWidth()) - 34, 21);
		frame.getContentPane().add(btnAnalyze);
		
		btnAnalyze.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tolerance.setEnabled(false);
				btnAnalyze.setEnabled(false);
				btnEDL.setEnabled(false);
				lblEdit.setVisible(false);
				if (outputFolder != null && outputFolder.exists())
					deleteDirectory(outputFolder);
				

				if (tableRow != null)
					tableRow.setRowCount(0);
								
				runAnalyse();
			}
			
		});
				
		lblBottomArrow = new JLabel("▲▼");
		lblBottomArrow.setHorizontalAlignment(SwingConstants.CENTER);
		lblBottomArrow.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 20));
		lblBottomArrow.setSize(new Dimension(frame.getSize().width, 20));
		lblBottomArrow.setLocation(0, frame.getSize().height - lblBottomArrow.getSize().height);
		lblBottomArrow.setVisible(true);		
		frame.getContentPane().add(lblBottomArrow);
		
		btnEDL = new JButton(Shutter.language.getProperty("btnEDL"));
		btnEDL.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnEDL.setEnabled(false);
		btnEDL.setBounds(9, 65 + scrollPane.getHeight() + 6, scrollPane.getWidth(), 21);
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
						writer.println("TITLE: " + dialog.getFile());
						
						int countItemsEDL = 0;
				    	for (int i = 0 ; i < tableRow.getRowCount(); i++)
						{   							

				    		///////////////////////////////////IN POINT
				    		
				    		String timecodeStart = String.valueOf(tableRow.getValueAt(i, 2));
							String tcStart[] = timecodeStart.split(":");
			    			String tcIn = tcStart[0] + ":" + tcStart[1] + ":" + tcStart[2] + ":" + tcStart[3];
			    			
			    			//Timecode offset
			    			long offset; 
			    			if (tcStart[0] != "" && FFPROBE.timecode2 != "" && FFPROBE.timecode3 != "" && FFPROBE.timecode4 != "" )
			    			{    			
		    					offset = Math.round(Integer.parseInt(FFPROBE.timecode1) * 3600 * Math.round(FFPROBE.currentFPS))
	    							+ Math.round(Integer.parseInt(FFPROBE.timecode2) * 60 * Math.round(FFPROBE.currentFPS))
	    							+ Math.round(Integer.parseInt(FFPROBE.timecode3) * Math.round(FFPROBE.currentFPS))
	    							+ Integer.parseInt(FFPROBE.timecode4);
			    			}
			    			else
			    				offset = 0;
			    			
			    			//Timecode of start point	
			    			long totalFramesIn = Math.round(Integer.parseInt(tcStart[0]) * 3600 * Math.round(FFPROBE.currentFPS))
			    					+ Math.round(Integer.parseInt(tcStart[1]) * 60 * Math.round(FFPROBE.currentFPS))
			    					+ Math.round(Integer.parseInt(tcStart[2]) * Math.round(FFPROBE.currentFPS))
			    					+ Integer.parseInt(tcStart[3]);
			    			
			    			//Total
			    			totalFramesIn = (offset + totalFramesIn);
			    			
			    			NumberFormat formatter = new DecimalFormat("00");
			    			
			    			//Convert total frames to timecode	        
			    			String tcInVideo = formatter.format(Math.round(totalFramesIn / Math.round(FFPROBE.currentFPS)) / 3600)
			    					+ ":" + formatter.format(Math.round((totalFramesIn / Math.round(FFPROBE.currentFPS)) / 60) % 60)
		    						+ ":" + formatter.format(Math.round((totalFramesIn / Math.round(FFPROBE.currentFPS)) % 60))        
	    							+ ":" + formatter.format(totalFramesIn % Math.round(FFPROBE.currentFPS));
			    			
			    			///////////////////////////////////OUT POINT
			    			
			    			long totalFramesOut = 0;
			    			if (i < tableRow.getRowCount() - 1)
			    			{
								String timecodeEnd = String.valueOf(tableRow.getValueAt(i + 1, 2));
								String tcEnd[] = timecodeEnd.split(":");

				    			//Timecode of the cut
								totalFramesOut = Math.round(Integer.parseInt(tcEnd[0]) * 3600 * Math.round(FFPROBE.currentFPS))
				    					+ Math.round(Integer.parseInt(tcEnd[1]) * 60 * Math.round(FFPROBE.currentFPS))
				    					+ Math.round(Integer.parseInt(tcEnd[2]) * Math.round(FFPROBE.currentFPS))
				    					+ Integer.parseInt(tcEnd[3]);
			    			}
			    			else //On last the cut we take the length of the video
							{								
								String tc[] = FFPROBE.getVideoLengthTC.split(":");		

								totalFramesOut = Math.round(Integer.parseInt(tc[0]) * 3600 * Math.round(FFPROBE.currentFPS))
			    					+ Math.round(Integer.parseInt(tc[1]) * 60 * Math.round(FFPROBE.currentFPS))
			    					+ Math.round(Integer.parseInt(tc[2]) * Math.round(FFPROBE.currentFPS))
			    					+ (int) (Integer.parseInt(tc[3]) * 10) / (1000 / Math.round(FFPROBE.currentFPS));	
							}							
							
			    			//Set timeline outpoint = no offset
			    			String tcOutTimeLine = formatter.format(Math.round(totalFramesOut / Math.round(FFPROBE.currentFPS)) / 3600)
			    					+ ":" + formatter.format(Math.round((totalFramesOut / Math.round(FFPROBE.currentFPS)) / 60) % 60)
		    						+ ":" + formatter.format(Math.round((totalFramesOut / Math.round(FFPROBE.currentFPS)) % 60))        
	    							+ ":" + formatter.format(totalFramesOut % Math.round(FFPROBE.currentFPS));
			    			
			    			//Adding the offset for tcOutVideo
			    			totalFramesOut = (offset + totalFramesOut);
			    			
			    			//Convert total frames to timecode	        
			    			String tcOutVideo = formatter.format(Math.round(totalFramesOut / Math.round(FFPROBE.currentFPS)) / 3600)
			    					+ ":" + formatter.format(Math.round((totalFramesOut / Math.round(FFPROBE.currentFPS)) / 60) % 60)
		    						+ ":" + formatter.format(Math.round((totalFramesOut / Math.round(FFPROBE.currentFPS)) % 60))        
	    							+ ":" + formatter.format(totalFramesOut % Math.round(FFPROBE.currentFPS));

							String cutName;
							String ext = outputFile.toString().substring(outputFile.toString().lastIndexOf("."));
							if (i % 2 == 0)
								cutName = outputFile.toString().replace(" ", "_");
							else
								cutName = outputFile.toString().replace(" ", "_").replace(ext, "_" + Shutter.language.getProperty("cut") + ext);
			    																	
							writer.println(formatEDL.format(countItemsEDL + 1) + "  " + cutName + " V     C        " + tcInVideo + " " + tcOutVideo + " " + tcIn + " " + tcOutTimeLine);
							writer.println(formatEDL.format(countItemsEDL + 2) + "  " + cutName + " A     C        " + tcInVideo + " " + tcOutVideo + " " + tcIn + " " + tcOutTimeLine);
							writer.println(formatEDL.format(countItemsEDL + 3) + "  " + cutName + " A2    C        " + tcInVideo + " " + tcOutVideo + " " + tcIn + " " + tcOutTimeLine);
							
							countItemsEDL += 3;	
						}
				    	
				    } catch (Exception e){
				    	System.out.println(e);
				    }
				    				    
					writer.close();	
					JOptionPane.showMessageDialog(frame, Shutter.language.getProperty("fileCreated"), "EDL", JOptionPane.INFORMATION_MESSAGE);
						
					}
					
			    }); runProcess.start();					
				}
			}			
		});
		
		lblEdit = new JLabel(Shutter.language.getProperty("lblEdit"));
		lblEdit.setForeground(Utils.themeColor);
		lblEdit.setHorizontalAlignment(SwingConstants.CENTER);
		lblEdit.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 13));
		lblEdit.setSize(lblEdit.getPreferredSize().width, 15);
		lblEdit.setLocation(frame.getWidth() / 2 - lblEdit.getWidth() / 2, 65 + scrollPane.getHeight() + 31);
		lblEdit.setVisible(false);
		frame.getContentPane().add(lblEdit);
									
	}
	
	@SuppressWarnings("serial")
	private static void newTable() {
		
		ImageIcon imageIcon = new ImageIcon(SceneDetection.outputFolder.toString() + "/0.png");
		ImageIcon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(142, 80, Image.SCALE_DEFAULT));			
		Object[][] firstImage = {{"1", icon, "00:00:00:00"}};

		tableRow = new DefaultTableModel(firstImage, new String[] {"N\u00B0", Shutter.language.getProperty("cut"), Shutter.language.getProperty("lblTimecode")});
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
		table.setBackground(new Color(50,50,50));
		table.getColumnModel().getColumn(0).setPreferredWidth(18);
		table.getColumnModel().getColumn(1).setPreferredWidth(table.getColumnModel().getColumn(1).getPreferredWidth());
		table.setBounds(9, 65, 380, frame.getHeight() - 134);
		scrollPane.setViewportView(table);
		
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && FFMPEG.isRunning == false && table.getSelectedRowCount() > 0)
				{	
					File imageToDelete = new File(outputFolder.toString() + "/" + (table.getSelectedRow()) + ".png");
					imageToDelete.delete();
					
					int i = 0; //On renomme toutes les images
					for (File file : outputFolder.listFiles())
					{
						if (file.toString().substring(file.toString().lastIndexOf(".")).equals(".png"))
							file.renameTo(new File(outputFolder.toString() + "/" + i + ".png"));
						
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
					Desktop.getDesktop().open(new File(outputFolder + "/" + table.getSelectedRow() + ".png"));
				} catch (IOException e1) {}			
			}		
		});

		ouvrirDossier.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
	            try {
					Desktop.getDesktop().open(outputFolder);
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
						Desktop.getDesktop().open(new File(outputFolder + "/" + table.getSelectedRow() + ".png"));
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
			
				Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));

					
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
							 																	
						String filename = file.getName();
						Shutter.lblCurrentEncoding.setText(filename);
						
						String sortie = file.getParent();					
						final String extension =  filename.substring(filename.lastIndexOf("."));
						outputFolder =  new File(sortie + "/" + filename.replace(extension, ""));		
						outputFolder.mkdir();
						
						outputFile =  new File(file.getName());
						
						//Command
						String cmd;
						cmd = " -f image2 -vframes 1 ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + "-y " + '"'  + outputFolder.toString() + "/0.png" + '"');
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());										
						
						//On créer le tableau ici après la première image
						newTable();
					        		
						//Command
						String tol = String.valueOf((float) (100 - Integer.valueOf(application.SceneDetection.tolerance.getValue().toString())) / 100);
						cmd = " -vf select=" + '"' + "gt(scene\\," + tol  + ")" + '"' + ",showinfo -vsync 2 -f image2 ";
						FFMPEG.run(" -i " + '"' + file.toString() + '"' + cmd + "-y " + '"'  + outputFolder.toString() + "/%01d.png" + '"');		
						
						do
						{
							Thread.sleep(100);
						}
						while(FFMPEG.runProcess.isAlive());						
					
						lastAction();
						
					} catch (InterruptedException e) {
						FFMPEG.error  = true;
					}
				}
				
				//Errors
				if (errorList.length() != 0)
				{
					JOptionPane.showMessageDialog(Shutter.frame, Shutter.language.getProperty("notProcessedFiles") + " " + '\n' + '\n' + errorList.toString() ,Shutter.language.getProperty("encodingError"), JOptionPane.ERROR_MESSAGE);
				}
				
				errorList.setLength(0);
				
				FFMPEG.enfOfFunction();
			}
			
		});
		thread.start();
		
    }

	private static void lastAction() {
		
		//Erros
		if (FFMPEG.error)
		{
		    errorList.append(System.lineSeparator());
		}

		//Ended files
		if (FFMPEG.cancelled == false && FFMPEG.error == false)
		{
			complete++;
			Shutter.lblFilesEnded.setText(FunctionUtils.completedFiles(complete));
		}
		
		tolerance.setEnabled(true);
		btnAnalyze.setEnabled(true);
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