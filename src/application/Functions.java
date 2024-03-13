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

import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.geom.Area;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.SEVENZIP;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
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
	private JLabel newInstance;
	private boolean drag;
	public static JLabel lblFlecheBas;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	public static File functionsFolder = new File(Shutter.documents + "/Functions");

	public Functions() {
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(333,317);
		frame.setBackground(new Color(30,30,35));
		frame.setTitle(Shutter.language.getProperty("frameFonctions"));
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setUndecorated(true);
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
        Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
        shape1.add(shape2);
		frame.setShape(shape1);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(100,100,100)));
		frame.setIconImage(new ImageIcon((getClass().getClassLoader().getResource("contents/icon.png"))).getImage());
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Shutter.taskBarHeight = (int) (dim.getHeight() - winSize.height);
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);	
		
		frame.addWindowListener(new WindowListener(){
			
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Shutter.iconPresets.setVisible(true);
				if (Shutter.iconList.isVisible())
				{
					Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
					Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
				}
				else
				{
					Shutter.iconPresets.setBounds(180, 45, 21, 21);
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
		lblSave.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 16));
		lblSave.setBounds(10, 112, 313, 45);
		lblSave.setVisible(false);
		frame.getContentPane().add(lblSave);
		
		lblDrop = new JLabel(Shutter.language.getProperty("lblDrop"));
		lblDrop.setHorizontalAlignment(SwingConstants.CENTER);
		lblDrop.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 16));
		lblDrop.setBounds(10, 136, 313, 45);
		lblDrop.setVisible(false);
		frame.getContentPane().add(lblDrop);
		
		listeDeFonctions = new JList<String>(liste);
		listeDeFonctions.setBackground(new Color(42,42,47));
		listeDeFonctions.setForeground(Color.LIGHT_GRAY);
		listeDeFonctions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listeDeFonctions.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 11));
		listeDeFonctions.setCellRenderer(new FonctionsRenderer());
		listeDeFonctions.setFixedCellHeight(17);
		listeDeFonctions.setBounds(0, 28, 333, 269);
		
		listeDeFonctions.setTransferHandler(new FonctionsTransferHandler());   	
		
		scrollPane = new JScrollPane();		
		scrollPane.getViewport().add(listeDeFonctions);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBounds(0, 26, 333, 269);
		
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
				Utils.loadSettings(new File(functionsFolder + "/" + listeDeFonctions.getSelectedValue()));
			}
			
		});
		
		update.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("areYouSure"), Shutter.language.getProperty("menuItemUpdate"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);					
				
				if (reply == JOptionPane.YES_OPTION)
				{
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Utils.saveSettings(true);
				}
			}
			
		});
		
		delete.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("areYouSure"), Shutter.language.getProperty("menuItemDelete"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);					
				
				if (liste.getSize() > 0 && listeDeFonctions.getSelectedIndices().length == 1 && reply == JOptionPane.YES_OPTION)
				{
					if (liste.getSize() == 1)
					{
						lblSave.setVisible(true);
						lblDrop.setVisible(true);
					}
	
					File enc = new File(functionsFolder + "/" + listeDeFonctions.getSelectedValue());
					enc.delete();
					liste.remove(listeDeFonctions.getSelectedIndex());
				}
			}
	
		});
		
		openFolder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {							        	
				try {
					Desktop.getDesktop().open(functionsFolder);
					Utils.changeFrameVisibility(frame, true);
					
					Shutter.iconPresets.setVisible(true);
					if (Shutter.iconList.isVisible())
					{
						Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
						Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
					}
					else
					{
						Shutter.iconPresets.setBounds(180, 45, 21, 21);
						Shutter.btnCancel.setBounds(207, 46, 97, 21);
					}
					
				} catch (IOException e1) {}
			}
	
		});
		
		listeDeFonctions.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 && listeDeFonctions.getSelectedIndices().length > 0)
				{	
					popupListe.show(listeDeFonctions, e.getX() - 30, e.getY());
					
					load.setVisible(true);
					update.setVisible(true);
					delete.setVisible(true);
					openFolder.setVisible(true);
					
				}
				else
				{
					if (listeDeFonctions.getSelectedIndices().length > 0 && e.getClickCount() == 2)
					{					
						Shutter.btnReset.doClick();
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Utils.loadSettings(new File(functionsFolder + "/" + listeDeFonctions.getSelectedValue()));
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
		lblFlecheBas.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 20));
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
				
				Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
	            shape1.add(shape2);
	    		frame.setShape(shape1);
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
		    	if (System.getProperty("os.name").contains("Mac") && drag)
				{
					frame.setShape(null);
				}
				else
				{
					Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
		            Area shape2 = new Area(new Rectangle(0, frame.getHeight()-15, frame.getWidth(), 15));
		            shape1.add(shape2);
		    		frame.setShape(shape1);
				}
		    }
		});
			
		frame.addWindowListener(new WindowAdapter(){			
			public void windowDeiconified(WindowEvent we)
		    {
		       
			   frame.toFront();
		    }
		});
	}
	
	private void topPanel() {
				
		topPanel = new JPanel();		
		topPanel.setLayout(null);
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setBounds(0, 0, 333, 28);
	
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
					Shutter.iconPresets.setVisible(true);
					if (Shutter.iconList.isVisible())
					{
						Shutter.iconPresets.setLocation(Shutter.iconList.getX() + Shutter.iconList.getWidth() + 2, 45);
						Shutter.btnCancel.setBounds(207 + Shutter.iconList.getWidth(), 46, 101 - Shutter.iconList.getWidth() -  4, 21);
					}
					else
					{
						Shutter.iconPresets.setBounds(180, 45, 21, 21);
						Shutter.btnCancel.setBounds(207, 46, 97, 21);
					}
					
					Utils.changeFrameVisibility(frame, true);
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
		
		reduce = new JLabel(new FlatSVGIcon("contents/reduce.svg", 15, 15));
		reduce.setHorizontalAlignment(SwingConstants.CENTER);
		reduce.setBounds(quit.getLocation().x - 20, 4, 15, 15);
		topPanel.add(reduce);
		
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

			@Override
			public void mouseReleased(MouseEvent e) {			
				if (accept)
				{
					
					frame.setState(Frame.ICONIFIED);
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
				
		newInstance = new JLabel(new FlatSVGIcon("contents/new.svg", 15, 15));
		newInstance.setHorizontalAlignment(SwingConstants.CENTER);
		newInstance.setBounds(reduce.getLocation().x - 20, 4, 15, 15);
		newInstance.setToolTipText(Shutter.language.getProperty("btnSave"));
		topPanel.add(newInstance);

		newInstance.addMouseListener(new MouseListener() {	

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept) 
				{
					if ((Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnStartFunction")) || Shutter.btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender"))) && Shutter.comboFonctions.getSelectedItem() != "") 
					{
						if (Renamer.frame == null || Renamer.frame != null && Renamer.frame.isVisible() == false)
						{
							Utils.saveSettings(false);
						}
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				newInstance.setIcon(new FlatSVGIcon("contents/new.svg", 15, 15));
				accept = false;
			}

		});
				
		JLabel title = new JLabel(Shutter.language.getProperty("frameFonctions"));
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
					frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - MousePositionX, MouseInfo.getPointerInfo().getLocation().y - MousePositionY);	
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
		
	}

	public static void addFonctions() {
		
		liste.clear();
		
		File oldFolder = new File(functionsFolder.toString().replace("Functions", "Fonctions"));
		if (oldFolder.exists())
		{
			oldFolder.renameTo(functionsFolder);
		}
		
		if (functionsFolder.exists() == false)
		{
			functionsFolder.mkdir();
			lblSave.setVisible(true);
			lblDrop.setVisible(true);
		}
		else
		{
			if (functionsFolder.listFiles().length == 0)
			{
				lblSave.setVisible(true);
				lblDrop.setVisible(true);
			}
			else
			{
				for (File f : functionsFolder.listFiles())
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

// Editing functions list
@SuppressWarnings("serial")
class FonctionsRenderer extends DefaultListCellRenderer {
  @Override
  public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
      	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);   
	    ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png"));
	    ImageIcon icon = new ImageIcon(imageIcon.getImage().getScaledInstance(15, 15 , Image.SCALE_SMOOTH));		
	    setIcon(icon);
	    setFont(new Font("SansSerif", Font.PLAIN, 12));
	    setForeground(Color.LIGHT_GRAY);
		setOpaque(true);
			
		if (isSelected)
		{
			setBackground(new Color(75,75,80));
		}
		else
		{			
			if (index % 2 == 1)
				setBackground(new Color(42,42,47));
			else
				setBackground(new Color(51,51,56));
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
	            	File toCopy = new File(Functions.functionsFolder + "/" + droppedFile.getName());
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
	            	SEVENZIP.run("e " + '"' + droppedFile.toString() + '"' + " -y -o" + '"' + Functions.functionsFolder + '"', false);
	            	
	            	try {
		            	do {
		            		Thread.sleep(100);
		            	} while (SEVENZIP.isRunning);
	            	} catch (Exception e){}
	            	
	            	Functions.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	            }
	            	
	          }
	
	        }
	 
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