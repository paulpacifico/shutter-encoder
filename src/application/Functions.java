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

import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import library.SEVENZIP;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ListSelectionModel;

public class Functions {

	public static JFrame frame;
	private static DefaultListModel<String> liste = new DefaultListModel<String>();	
	public static JList<String> listeDeFonctions;
	public static JLabel lblSave;
	public static JLabel lblDrop;
	private JScrollPane scrollPane;
	private JPopupMenu popupListe;
	private JPanel topPanel;
	private JLabel topImage;
	private JLabel quit;
	private JLabel reduce;
	private boolean drag;
	public static JLabel lblFlecheBas;
	
	public static File fonctionsFolder = new File(Shutter.documents + "/Functions");

	@SuppressWarnings("serial")
	public Functions() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(333,270);
		frame.setBackground(new Color(50,50,50));
		frame.setTitle(Shutter.language.getProperty("frameFonctions"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		frame.setLocation(Shutter.frame.getLocation().x - frame.getSize().width -20, Shutter.frame.getLocation().y + frame.getSize().height / 2);		
		
		frame.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {	
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
		 		//Border
				listeDeFonctions.setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
			
		});
		
		frame.addWindowListener(new WindowListener(){
			
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Shutter.iconPresets.setVisible(true);
				if (Shutter.iconList.isVisible())
				{
					Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 46);
					Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
				}
				else
				{
					Shutter.iconPresets.setBounds(180, 46, 21, 21);
					Shutter.btnCancel.setBounds(207, 46, 97, 21);
				}
				
				Utils.changeFrameVisibility(frame, true);				
			}

			@Override
			public void windowClosed(WindowEvent e) {	
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {										
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
		
		lblSave = new JLabel(Shutter.language.getProperty("lblSaveMac"));
		
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
			lblSave = new JLabel(Shutter.language.getProperty("lblSaveMac"));
		else
			lblSave = new JLabel(Shutter.language.getProperty("lblSavePC"));
				
		lblSave.setHorizontalAlignment(SwingConstants.CENTER);
		lblSave.setFont(new Font("FreeSans", Font.PLAIN, 16));
		lblSave.setBounds(10, 112, 313, 45);
		lblSave.setVisible(false);
		frame.getContentPane().add(lblSave);
		
		lblDrop = new JLabel(Shutter.language.getProperty("lblDrop"));
		lblDrop.setHorizontalAlignment(SwingConstants.CENTER);
		lblDrop.setFont(new Font("FreeSans", Font.PLAIN, 16));
		lblDrop.setBounds(10, 136, 313, 45);
		lblDrop.setVisible(false);
		frame.getContentPane().add(lblDrop);
		
		listeDeFonctions = new JList<String>(liste)
		{
			 Image image = new ImageIcon(getClass().getClassLoader().getResource("contents/zebra.jpg")).getImage();
	         {
	            setOpaque(false);
	         }
	         public void paintComponent(Graphics g) {
	        	 super.paintComponent(g);
	             int iw = image.getWidth(this);
	             int ih = image.getHeight(this);
	             if (iw > 0 && ih > 0) {
	                 for (int x = 0; x < getWidth(); x += iw) {
	                     for (int y = 0; y < getHeight(); y += ih) {
	                         g.drawImage(image, x, y, iw, ih, this);
	                     }
	                 }
	             }
	            super.paintComponent(g);
	         }
		};
		listeDeFonctions.setForeground(Color.BLACK);
		listeDeFonctions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listeDeFonctions.setFont(new Font("FreeSans", Font.PLAIN, 11));
		listeDeFonctions.setCellRenderer(new FonctionsRenderer());
		listeDeFonctions.setFixedCellHeight(17);
		listeDeFonctions.setBounds(0, 52, 333, 198);
		
		listeDeFonctions.setTransferHandler(new FonctionsTransferHandler());   	
		
		scrollPane = new JScrollPane();		
		scrollPane.getViewport().add(listeDeFonctions);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBounds(0,52,333,198);
		
		frame.getContentPane().add(scrollPane);
		
		popupListe = new JPopupMenu();
		final JMenuItem load = new JMenuItem(Shutter.language.getProperty("menuItemLoad"));
		final JMenuItem update = new JMenuItem(Shutter.language.getProperty("menuItemUpdate"));
		final JMenuItem delete = new JMenuItem(Shutter.language.getProperty("menuItemDelete"));
		final JMenuItem openFolder = new JMenuItem(Shutter.language.getProperty("menuItemOpenFolder"));
		
		popupListe.add(load);
		popupListe.add(update);
		popupListe.add(delete);
		popupListe.add(openFolder);
		
		load.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Shutter.btnReset.doClick();
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Utils.loadSettings(new File(fonctionsFolder + "/" + listeDeFonctions.getSelectedValue()));
			}
			
		});
		
		update.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Utils.saveSettings(true);
			}
			
		});
		
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (liste.getSize() > 0 && listeDeFonctions.getSelectedIndices().length == 1)
				{
					if (liste.getSize() == 1)
					{
						lblSave.setVisible(true);
						lblDrop.setVisible(true);
					}
	
					File enc = new File(fonctionsFolder + "/" + listeDeFonctions.getSelectedValue());
					enc.delete();
					liste.remove(listeDeFonctions.getSelectedIndex());
				}
			}
	
		});
		
		openFolder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {							        	
				try {
					Desktop.getDesktop().open(fonctionsFolder);
					Utils.changeFrameVisibility(frame, true);
					
					Shutter.iconPresets.setVisible(true);
					if (Shutter.iconList.isVisible())
					{
						Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 46);
						Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
					}
					else
					{
						Shutter.iconPresets.setBounds(180, 46, 21, 21);
						Shutter.btnCancel.setBounds(207, 46, 97, 21);
					}
					
				} catch (IOException e1) {}
			}
	
		});
		
		listeDeFonctions.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3)
				{	
					popupListe.show(listeDeFonctions, e.getX() - 30, e.getY());
					
					if (listeDeFonctions.getSelectedIndices().length > 0)
		        	{
						load.setVisible(true);
						update.setVisible(true);
						delete.setVisible(true);
						openFolder.setVisible(true);
		        	}
					else
					{
						load.setVisible(false);
						update.setVisible(false);
						delete.setVisible(false);
						openFolder.setVisible(true);
					}
					
				}
				else
				{
					if (listeDeFonctions.getSelectedIndices().length > 0 && e.getClickCount() == 2)
					{					
						Shutter.btnReset.doClick();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Utils.loadSettings(new File(fonctionsFolder + "/" + listeDeFonctions.getSelectedValue()));
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});

		lblFlecheBas = new JLabel("▲▼");
		lblFlecheBas.setHorizontalAlignment(SwingConstants.CENTER);
		lblFlecheBas.setFont(new Font("FreeSans", Font.PLAIN, 20));
		lblFlecheBas.setSize(new Dimension(frame.getSize().width, 20));
		lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
		
		frame.getContentPane().add(lblFlecheBas);
		
		topPanel();
		
		drag = false;		
		
		frame.addMouseMotionListener (new MouseMotionListener(){
 			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag && frame.getSize().height > 90)
		       	{	
			        frame.setSize(frame.getSize().width, e.getY() + 10);		
			    	lblFlecheBas.setLocation(0, frame.getSize().height - lblFlecheBas.getSize().height);
			    	scrollPane.setBounds(0, topPanel.getSize().height, frame.getSize().width, frame.getSize().height - topPanel.getSize().height - 20);	
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
		    		scrollPane.setBounds(0, topPanel.getSize().height, frame.getSize().width, frame.getSize().height - topPanel.getSize().height - 20);	
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {		
				if (frame.getCursor().getType() == Cursor.S_RESIZE_CURSOR)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});		
		
		frame.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				addFonctions();		
			}
			
		    public void componentResized(ComponentEvent e2)
		    {
				Area shape1 = new Area(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		        shape1.add(shape2);
		    	frame.setShape(shape1);
		    }
		});
			
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we)
		    {
		       
			   frame.toFront();
		    }
		});
	}

	private static class MousePosition {
		static int mouseX;
		static int mouseY;
	}
	
	private void topPanel() {
				
		topPanel = new JPanel();		
		topPanel.setLayout(null);
	
	
		quit = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/quit2.png")));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 24,0,21, 21);
		topPanel.add(quit);
		topPanel.setBounds(0, 0, 1000, 53);
		
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
					Shutter.iconPresets.setVisible(true);
					if (Shutter.iconList.isVisible())
					{
						Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 46);
						Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
					}
					else
					{
						Shutter.iconPresets.setBounds(180, 46, 21, 21);
						Shutter.btnCancel.setBounds(207, 46, 97, 21);
					}
					
					Utils.changeFrameVisibility(frame, true);
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
		
		reduce = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("contents/reduce2.png")));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 21,0,21, 21);
		topPanel.add(reduce);
		topPanel.setBounds(0, 0, 852, 53);
			
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

			@Override
			public void mouseReleased(MouseEvent e) {			
				if (accept)
				{
					
					frame.setState(Frame.ICONIFIED);
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
		
		topPanel.setBounds(0, 0, 333, 52);
			
		JLabel title = new JLabel(Shutter.language.getProperty("frameFonctions"));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 0, frame.getWidth(), 52);
		title.setFont(new Font("Magneto", Font.PLAIN, 26));
		topPanel.add(title);
		
		topImage = new JLabel();
		ImageIcon header = new ImageIcon(getClass().getClassLoader().getResource("contents/header.png"));
		ImageIcon imageIcon = new ImageIcon(header.getImage().getScaledInstance(topPanel.getSize().width, topPanel.getSize().height, Image.SCALE_DEFAULT));
		topImage.setIcon(imageIcon);		
		topImage.setBounds(title.getBounds());
		
		topPanel.add(topImage);		
		topPanel.setBounds(0, 0, 1000, 53);
		frame.getContentPane().add(topPanel);
		
		topImage.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent down) {
			}

			@Override
			public void mousePressed(MouseEvent down) {	
				MousePosition.mouseX = down.getPoint().x;
				MousePosition.mouseY = down.getPoint().y;
				
				frame.toFront();
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

	public static void addFonctions(){
		liste.clear();
		
		File oldFolder = new File(fonctionsFolder.toString().replace("Functions", "Fonctions"));
		if (oldFolder.exists())
		{
			oldFolder.renameTo(fonctionsFolder);
		}
		
		if (fonctionsFolder.exists() == false)
		{
			fonctionsFolder.mkdir();
			lblSave.setVisible(true);
			lblDrop.setVisible(true);
		}
		else
		{
			if (fonctionsFolder.listFiles().length == 0)
			{
				lblSave.setVisible(true);
				lblDrop.setVisible(true);
			}
			else
			{
				for (File f : fonctionsFolder.listFiles())
				{
					if(f.getName().toString().equals(".DS_Store") == false)
						liste.addElement(f.getName());
				}
				
				lblSave.setVisible(false);
				lblDrop.setVisible(false);
			}
		}
		
		String[] data = new String[liste.getSize()]; 

        for (int i = 0 ; i < liste.getSize() ; i++) { 
           data[i] = (String) liste.getElementAt(i); 
        }

        Arrays.sort(data); 
        liste.clear();
        
        for (int i = 0 ; i < data.length ; i++) { 
			liste.addElement(data[i].toString());
	    }
}

//Modifications de la liste de fonctions
@SuppressWarnings("serial")
class FonctionsRenderer extends DefaultListCellRenderer {
  @Override
  public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);   
	      ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png"));
	      ImageIcon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(15, 15 , Image.SCALE_DEFAULT));		
	      setIcon(icon);
	      setFont(new Font("SansSerif", Font.PLAIN, 12));
	      setForeground(Color.BLACK);
	      
	      if (isSelected)
	      {
	    	  setBackground(new Color(215,215,215));  
	    	  setBorder(new LineBorder(new Color(129,198,253)));
	    	  setOpaque(true);
	      }
	      else
	      {
	    	  setBorder(new LineBorder(new Color(204,204,204,0)));
	    	  setOpaque(false);
	      }
      return this;
  }
}

//Drag & Drop listeDeFonctions
@SuppressWarnings("serial")
class FonctionsTransferHandler extends TransferHandler {
	
public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
  for (int i = 0; i < arg1.length; i++) {
    DataFlavor flavor = arg1[i];
    if (flavor.equals(DataFlavor.javaFileListFlavor)) {
  	  Functions.listeDeFonctions.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
      return true;
    }
  }
  return false;
}

public boolean importData(JComponent comp, Transferable t) {
	  DataFlavor[] flavors = t.getTransferDataFlavors();
	  for (int i = 0; i < flavors.length; i++) {
	    DataFlavor flavor = flavors[i];
	    try {
	      if (flavor.equals(DataFlavor.javaFileListFlavor)) {
	        List<?> l = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
	        Iterator<?> iter = l.iterator();
	        while (iter.hasNext()) {
	          File file = (File) iter.next();	                        
	          
	          if (file.isFile() && file.getName().contains("."))
	          {
	          	int s = file.getCanonicalPath().toString().lastIndexOf('.');	
	          	String ext = file.getCanonicalFile().toString().substring(s);
	            if (ext.equals(".enc"))
	            {
	            	File droppedFile = new File(file.getCanonicalPath().toString());
	            	File toCopy = new File(Functions.fonctionsFolder + "/" + droppedFile.getName());
	            	InputStream inStream = new FileInputStream(droppedFile);
	            	OutputStream outStream = new FileOutputStream(toCopy);

	        	    byte[] buffer = new byte[1024];

	        	    int length;
	        	    while ((length = inStream.read(buffer)) > 0){
	        	    	outStream.write(buffer, 0, length);
	        	    }

	        	    inStream.close();
	        	    outStream.close();

	        	    try {
	        	    	droppedFile.delete();
	        	    } catch (Exception e){}

	            }
	            else if (ext.equals(".zip"))
	            {
	            	Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	            	File droppedFile = new File(file.getCanonicalPath().toString());
	            	SEVENZIP.run("e " + '"' + droppedFile.toString() + '"' + " -y -o" + '"' + Functions.fonctionsFolder + '"', false);
	            	
	            	try {
		            	do {
		            		Thread.sleep(100);
		            	} while (SEVENZIP.isRunning);
	            	} catch (Exception e){}
	            	
	            	Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	            }
	            	
	          }
	
	        }
	 
	 		//Border
			Functions.listeDeFonctions.setBorder(BorderFactory.createLineBorder(Color.BLUE, 0));
			
			Functions.addFonctions();
			
  			Functions.lblSave.setVisible(false);
  			Functions.lblDrop.setVisible(false);
			
	        return true;
	      }        
	    } catch (IOException | UnsupportedFlavorException ex) {}
	  }
	  return false;
	}
	}
}