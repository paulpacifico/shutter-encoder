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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.MatteBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import library.FFMPEG;

public class Settings {

	public static JFrame frame = new JFrame();
	int scrollValue = 0;
	private JLabel quit;
	private JLabel reduce;
	private JLabel help;
	private JPanel topPanel;
	private JLabel topImage;
	private JLabel lblThreads = new JLabel(Shutter.language.getProperty("lblThreads"));
	public static JTextField txtThreads = new JTextField();
	private JLabel lblImageToVideo = new JLabel(Shutter.language.getProperty("lblImageToVideo"));
	public static JTextField txtImageDuration = new JTextField();
	private JLabel lblScaleMode = new JLabel(Shutter.language.getProperty("lblScaleMode"));
	public static JComboBox<String> comboScale = new JComboBox<String>(new String [] {"fast_bilinear", "bilinear", "bicubic", "neighbor", "area", "gauss", "sinc", "lanczos", "spline"});	
	public static JCheckBox btnPreviewOutput = new JCheckBox(Shutter.language.getProperty("btnPreviewOutput"));
	private JLabel lblSyncMode = new JLabel(Shutter.language.getProperty("lblSyncMode"));
	public static JComboBox<String> comboSync = new JComboBox<String>(new String [] {"auto", "passthrough", "cfr", "vfr", "drop"});	
	private JLabel lblLanguage = new JLabel(Shutter.language.getProperty("lblLanguage"));
	private JLabel lblTheme = new JLabel(Shutter.language.getProperty("lblTheme"));
	private static JPanel accentColor = new JPanel();
	public static JComboBox<String> comboLanguage = new JComboBox<String>();
	public static JCheckBox btnSetBab = new JCheckBox(Shutter.language.getProperty("btnSetBab"));
	public static JCheckBox btnExclude = new JCheckBox(Shutter.language.getProperty("btnExclude"));
	public static JCheckBox btnHidePath = new JCheckBox(Shutter.language.getProperty("btnHidePath"));
	public static JCheckBox btnLoadPreset = new JCheckBox(Shutter.language.getProperty("btnLoadPreset"));
	public static JComboBox<String> comboLoadPreset = new JComboBox<String>();
	public static JCheckBox btnWaitFileComplete = new JCheckBox(Shutter.language.getProperty("btnWaitFileComplete"));
	public static JCheckBox btnEmptyListAtEnd = new JCheckBox(Shutter.language.getProperty("btnEmptyListAtEnd"));
	public static JCheckBox btnEndingAction = new JCheckBox(Shutter.language.getProperty("btnEndingAction"));
	public static JComboBox<String> comboAction = new JComboBox<String>();
	public static JCheckBox btnDisableAnimations = new JCheckBox(Shutter.language.getProperty("btnDisableAnimations"));
	public static JCheckBox btnDisableSound = new JCheckBox(Shutter.language.getProperty("btnDisableSound"));
	public static JCheckBox btnDisableUpdate = new JCheckBox(Shutter.language.getProperty("btnDisableUpdate"));
	public static JCheckBox btnDisableVideoPlayer = new JCheckBox(Shutter.language.getProperty("btnDisableVideoPlayer"));	
	public static JCheckBox btnDisableMinimizedWindow = new JCheckBox(Shutter.language.getProperty("btnDisableMinimizedWindow"));	
	public static JTextField txtExclude = new JTextField();
	public static JCheckBox btnCustomFFmpegPath = new JCheckBox(Shutter.language.getProperty("btnCustomFFmpegPath"));
	public static JTextField txtCustomFFmpegPath = new JTextField();
	private JLabel defaultOutput1 = new JLabel(Shutter.language.getProperty("output") + "1 " + Shutter.language.getProperty("toDefault"));
	private JLabel defaultOutput2 = new JLabel(Shutter.language.getProperty("output") + "2 " + Shutter.language.getProperty("toDefault"));
	private JLabel defaultOutput3 = new JLabel(Shutter.language.getProperty("output") + "3 " + Shutter.language.getProperty("toDefault"));
	public static JCheckBox lastUsedOutput1 = new JCheckBox(Shutter.language.getProperty("lastUsed"));
	public static JCheckBox lastUsedOutput2 = new JCheckBox(Shutter.language.getProperty("lastUsed"));
	public static JCheckBox lastUsedOutput3 = new JCheckBox(Shutter.language.getProperty("lastUsed"));
	public static JLabel lblDestination1 = new JLabel(); 
	public static JLabel lblDestination2 = new JLabel(); 
	public static JLabel lblDestination3 = new JLabel(); 
	public static boolean videoWebCaseMetadata = false;
	
	private static int MousePositionX;
	private static int MousePositionY;
	
	public Settings() {
				
		//For saving	
		btnExclude.setName("btnExclude");
		txtExclude.setName("txtExclude");
		btnHidePath.setName("btnHidePath");
		btnLoadPreset.setName("btnLoadPreset");
		comboLoadPreset.setName("comboLoadPreset");
		btnPreviewOutput.setName("btnPreviewOutput");
		btnWaitFileComplete.setName("btnWaitFileComplete");
		btnDisableAnimations.setName("btnDisableAnimations");
		btnDisableSound.setName("btnDisableSound");	
		btnDisableUpdate.setName("btnDisableUpdate");
		btnDisableVideoPlayer.setName("btnDisableVideoPlayer");
		btnDisableMinimizedWindow.setName("btnDisableMinimizedWindow");
		btnEmptyListAtEnd.setName("btnEmptyListAtEnd");
		btnCustomFFmpegPath.setName("btnCustomFFmpegPath");
		txtCustomFFmpegPath.setName("txtCustomFFmpegPath");
		lblDestination1.setName("lblDestination1");
		lblDestination2.setName("lblDestination2");
		lblDestination3.setName("lblDestination3");
		lastUsedOutput1.setName("lastUsedOutput1");
		lastUsedOutput2.setName("lastUsedOutput2");
		lastUsedOutput3.setName("lastUsedOutput3");
		comboScale.setName("comboScale");
		comboSync.setName("comboSync");
		txtThreads.setName("txtThreads");
		txtImageDuration.setName("txtImageDuration");
		comboLanguage.setName("comboLanguage");

		frame.setSize(370, 750);
		if (Shutter.getLanguage.equals(Locale.of("ru").getDisplayLanguage()) || Shutter.getLanguage.equals(Locale.of("uk").getDisplayLanguage()))
		{
			frame.setSize(frame.getWidth() + 50, frame.getHeight());
		}
		
		frame.getContentPane().setBackground(new Color(30,30,35));
		frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
		frame.setTitle(Shutter.language.getProperty("frameSettings"));
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
				
		topPanel();
		
		JScrollBar scrollBar = new JScrollBar();
		scrollBar.setMaximum(40);
		scrollBar.setBackground(new Color(30,30,35));
		scrollBar.setOrientation(JScrollBar.VERTICAL);
		scrollBar.setSize(11, frame.getHeight() - topPanel.getHeight());
		scrollBar.setLocation(frame.getWidth() - scrollBar.getWidth() - 2, topPanel.getHeight());
		
		scrollBar.addAdjustmentListener(new AdjustmentListener(){
			
			public void adjustmentValueChanged(AdjustmentEvent ae) {
					int scrollIncrement = scrollBar.getValue() - scrollValue;
					for (Component c : frame.getContentPane().getComponents())
					{
						if (c instanceof JScrollBar == false)
						{
							if (c.getName() == null 
							|| (c.getName().equals("backgroundPanel") == false
								&& c.getName().equals("btnReset") == false
								&& c.getName().equals("donate") == false
								&& c.getName().equals("topPanel") == false))
							{
								c.setLocation(c.getLocation().x, c.getLocation().y - scrollIncrement);
							}
						}
					}
					scrollValue = scrollBar.getValue();
		      }			
			
		});
		
		//frame.getContentPane().add(scrollBar);
				
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setName("backgroundPanel");
		backgroundPanel.setLayout(null);
		backgroundPanel.setBackground(new Color(30,30,35));
		backgroundPanel.setOpaque(true);
		backgroundPanel.setSize(frame.getWidth(), 50);
		backgroundPanel.setLocation(0, frame.getHeight() - backgroundPanel.getHeight());	
		frame.getContentPane().add(backgroundPanel);
		
		btnHidePath.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnHidePath.setBounds(12, 34, btnHidePath.getPreferredSize().width, 16);
		frame.getContentPane().add(btnHidePath);
		
		btnHidePath.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Shutter.fileList.repaint();
			}
			
		});
		
		btnLoadPreset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnLoadPreset.setBounds(12, btnHidePath.getLocation().y + btnHidePath.getHeight() + 10, btnLoadPreset.getPreferredSize().width, 16);
		frame.getContentPane().add(btnLoadPreset);
		
		btnLoadPreset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {

				int index = comboLoadPreset.getSelectedIndex();
				
				if (btnLoadPreset.isSelected())
				{
					comboLoadPreset.setEnabled(true);
					comboLoadPreset.removeAllItems();
					
					if (Functions.functionsFolder.exists())
					{
						if (Functions.functionsFolder.listFiles().length != 0)
						{
							for (File f : Functions.functionsFolder.listFiles())
							{
								if (f.getName().toString().equals(".DS_Store") == false)
								{
									comboLoadPreset.addItem(f.getName());
								}
							}
						}
						else
						{
							btnLoadPreset.setSelected(false);
							comboLoadPreset.setEnabled(false);
						}
						
						comboLoadPreset.setSelectedIndex(index);
					}
					else
					{
						btnLoadPreset.setSelected(false);
						comboLoadPreset.setEnabled(false);
					}
				}
				else
				{
					comboLoadPreset.setEnabled(false);
				}
			}
			
		});
			
		try {
			if (Functions.functionsFolder.exists())
			{
				if (Functions.functionsFolder.listFiles().length != 0)
				{
					for (File f : Functions.functionsFolder.listFiles())
					{
						if (f.getName().toString().equals(".DS_Store") == false)
						{
							comboLoadPreset.addItem(f.getName());
						}
					}
				}
			}
		} catch (Exception e) {}
		
		comboLoadPreset.setEnabled(false);
		comboLoadPreset.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboLoadPreset.setEditable(false);
		comboLoadPreset.setBounds(btnLoadPreset.getX() + btnLoadPreset.getWidth() + 6, btnLoadPreset.getLocation().y - 4,  frame.getWidth() - (btnLoadPreset.getLocation().x + btnLoadPreset.getWidth()) - 32, 22);
		comboLoadPreset.setMaximumRowCount(10);	
		frame.getContentPane().add(comboLoadPreset);
		
		btnWaitFileComplete.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnWaitFileComplete.setBounds(12, btnLoadPreset.getLocation().y + btnLoadPreset.getHeight() + 10, btnWaitFileComplete.getPreferredSize().width, 16);
		frame.getContentPane().add(btnWaitFileComplete);
				
		btnExclude.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnExclude.setBounds(12, btnWaitFileComplete.getLocation().y + btnWaitFileComplete.getHeight() + 10, btnExclude.getPreferredSize().width, 16);
		frame.getContentPane().add(btnExclude);
		
		btnExclude.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (btnExclude.isSelected())
				{
					txtExclude.setEnabled(true);
				}
				else
				{
					txtExclude.setEnabled(false);
				}
			}
			
		});
				
		if (btnExclude.isSelected())
			txtExclude.setEnabled(true);
		else
			txtExclude.setEnabled(false);
		
		txtExclude.setColumns(10);
		txtExclude.setEnabled(false);
		txtExclude.setText("*.xml,*.bin,*.ind,*.ctg,*.mif,*.sif,*.cpf,*.cif,*.bdm");
		txtExclude.setFont(new Font("SansSerif", Font.PLAIN, 12));
		txtExclude.setBounds(btnExclude.getLocation().x + btnExclude.getWidth() + 6, btnExclude.getLocation().y - 2, frame.getWidth() - (btnExclude.getLocation().x + btnExclude.getWidth()) - 32, 21);
		frame.getContentPane().add(txtExclude);	
				
		btnSetBab.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnSetBab.setBounds(12, btnExclude.getLocation().y + btnExclude.getHeight() + 10, btnSetBab.getPreferredSize().width, 16);
		frame.getContentPane().add(btnSetBab);
		
		btnDisableAnimations.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnDisableAnimations.setBounds(12, btnSetBab.getLocation().y + btnSetBab.getHeight() + 10, btnDisableAnimations.getPreferredSize().width, 16);
		frame.getContentPane().add(btnDisableAnimations);
		
		btnDisableSound.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnDisableSound.setBounds(12, btnDisableAnimations.getLocation().y + btnDisableAnimations.getHeight() + 10, btnDisableSound.getPreferredSize().width, 16);
		frame.getContentPane().add(btnDisableSound);

		btnDisableUpdate.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnDisableUpdate.setBounds(12, btnDisableSound.getLocation().y + btnDisableSound.getHeight() + 10, btnDisableUpdate.getPreferredSize().width, 16);
		frame.getContentPane().add(btnDisableUpdate);
		
		btnDisableVideoPlayer.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnDisableVideoPlayer.setBounds(12, btnDisableUpdate.getLocation().y + btnDisableUpdate.getHeight() + 10, btnDisableVideoPlayer.getPreferredSize().width, 16);
		frame.getContentPane().add(btnDisableVideoPlayer);
		
		btnDisableVideoPlayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (btnDisableVideoPlayer.isSelected())
				{
					Shutter.caseDisplay.setVisible(false);
					Shutter.caseDisplay.setSelected(false);
					
					if (Shutter.comboFonctions.getSelectedItem() != "")
					{					
						Shutter.changeFunction(true);
					}
				}
				else
				{
					Shutter.caseDisplay.setVisible(true);
					
					if (Shutter.comboFonctions.getSelectedItem() != "")
					{
						Shutter.changeFunction(true);
						
						if (VideoPlayer.setTime != null && VideoPlayer.setTime.isAlive())
						{
							while (VideoPlayer.setTime.isAlive())
							{
								try {
									Thread.sleep(1);
								} catch (InterruptedException e) {}
							}
						}
						
						VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame			
					}
				}
			}
			
		});
		
		btnDisableMinimizedWindow.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnDisableMinimizedWindow.setBounds(12, btnDisableVideoPlayer.getLocation().y + btnDisableVideoPlayer.getHeight() + 10, btnDisableMinimizedWindow.getPreferredSize().width, 16);
		frame.getContentPane().add(btnDisableMinimizedWindow);
		
		btnEmptyListAtEnd.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnEmptyListAtEnd.setBounds(12, btnDisableMinimizedWindow.getLocation().y + btnDisableMinimizedWindow.getHeight() + 10, btnEmptyListAtEnd.getPreferredSize().width, 16);
		frame.getContentPane().add(btnEmptyListAtEnd);
		
		btnEndingAction.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnEndingAction.setBounds(12, btnEmptyListAtEnd.getLocation().y + btnEmptyListAtEnd.getHeight() + 10, btnEndingAction.getPreferredSize().width, 16);
		frame.getContentPane().add(btnEndingAction);
		
		btnEndingAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnEndingAction.isSelected())
					comboAction.setEnabled(true);
				else
					comboAction.setEnabled(false);
			}
			
		});
		
		if (comboAction.getModel().getSize() == 0)
		{
			comboAction.setModel(new DefaultComboBoxModel<String>(new String [] {
					Shutter.language.getProperty("lblActionClose"), 
					Shutter.language.getProperty("lblActionShutdown")
					}));
			comboAction.setSelectedIndex(1);	
			comboAction.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
			comboAction.setEditable(false);
			comboAction.setBounds(btnEndingAction.getX() + btnEndingAction.getWidth() + 6, btnEndingAction.getLocation().y - 4,  frame.getWidth() - (btnEndingAction.getLocation().x + btnEndingAction.getWidth()) - 32, 22);
			comboAction.setMaximumRowCount(10);
		}
		comboAction.setEnabled(false);
		frame.getContentPane().add(comboAction);
			
		btnCustomFFmpegPath.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnCustomFFmpegPath.setBounds(12, btnEndingAction.getLocation().y + btnEndingAction.getHeight() + 10, btnCustomFFmpegPath.getPreferredSize().width, 16);
		frame.getContentPane().add(btnCustomFFmpegPath);
		
		btnCustomFFmpegPath.addActionListener(new ActionListener(){
		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (btnCustomFFmpegPath.isSelected())
				{
					txtCustomFFmpegPath.setEnabled(true);
					
					if (Settings.txtCustomFFmpegPath.getText().equals("") == false)
					{
						FFMPEG.PathToFFMPEG = Settings.txtCustomFFmpegPath.getText();
					}
				}
				else
				{
					txtCustomFFmpegPath.setEnabled(false);
					
					FFMPEG.PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					
					if (System.getProperty("os.name").contains("Windows"))
					{							
						FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(1,FFMPEG.PathToFFMPEG.length()-1);
						FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,(int) (FFMPEG.PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
					}	
					else
					{
						FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,FFMPEG.PathToFFMPEG.length()-1);
						FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,(int) (FFMPEG.PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
					}
				}	
				
				if (VideoPlayer.videoPath != null)
				{
					VideoPlayer.btnStop.doClick();					
				}
			}
		});

		txtCustomFFmpegPath.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		
		if (btnCustomFFmpegPath.isSelected())
		{
			txtCustomFFmpegPath.setEnabled(true);
		}
		else
			txtCustomFFmpegPath.setEnabled(false);
				
		txtCustomFFmpegPath.setText("/usr/bin/ffmpeg");			
		txtCustomFFmpegPath.setBounds(btnCustomFFmpegPath.getX() + btnCustomFFmpegPath.getWidth() + 6, btnCustomFFmpegPath.getLocation().y - 4,  frame.getWidth() - (btnCustomFFmpegPath.getLocation().x + btnCustomFFmpegPath.getWidth()) - 32, 21);
		frame.getContentPane().add(txtCustomFFmpegPath);
				
		txtCustomFFmpegPath.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				if (txtCustomFFmpegPath.getText().length() > 0 && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (btnCustomFFmpegPath.isSelected())
					{
						txtCustomFFmpegPath.setEnabled(true);
						
						if (Settings.txtCustomFFmpegPath.getText().equals("") == false)
						{
							FFMPEG.PathToFFMPEG = Settings.txtCustomFFmpegPath.getText();
						}
					}
					else
					{
						txtCustomFFmpegPath.setEnabled(false);
						
						FFMPEG.PathToFFMPEG = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						
						if (System.getProperty("os.name").contains("Windows"))
						{							
							FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(1,FFMPEG.PathToFFMPEG.length()-1);
							FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,(int) (FFMPEG.PathToFFMPEG.lastIndexOf("/"))).replace("%20", " ")  + "\\Library\\ffmpeg.exe";
						}	
						else
						{
							FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,FFMPEG.PathToFFMPEG.length()-1);
							FFMPEG.PathToFFMPEG = FFMPEG.PathToFFMPEG.substring(0,(int) (FFMPEG.PathToFFMPEG.lastIndexOf("/"))).replace("%20", "\\ ")  + "/Library/ffmpeg";	
						}
					}	
					
					if (VideoPlayer.videoPath != null)
					{
						VideoPlayer.btnStop.doClick();					
					}
				}
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
		});
		
		btnPreviewOutput.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		btnPreviewOutput.setBounds(12, btnCustomFFmpegPath.getLocation().y + btnCustomFFmpegPath.getHeight() + 10, btnPreviewOutput.getPreferredSize().width, 16);
		frame.getContentPane().add(btnPreviewOutput);
			
		btnPreviewOutput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				VideoPlayer.playerSetTime(VideoPlayer.playerCurrentFrame); //Use VideoPlayer.resizeAll and reload the frame
				VideoPlayer.resizeAll();				
			}
			
		});
		
		lblScaleMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblScaleMode.setBounds(12, btnPreviewOutput.getLocation().y + btnPreviewOutput.getHeight() + 10, lblScaleMode.getPreferredSize().width, 16);
		frame.getContentPane().add(lblScaleMode);
				
		comboScale.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboScale.setEditable(false);
		comboScale.setSelectedItem("bicubic");
		comboScale.setBounds(lblScaleMode.getX() + lblScaleMode.getWidth() + 6, lblScaleMode.getLocation().y - 4, comboScale.getPreferredSize().width, 22);
		comboScale.setMaximumRowCount(10);
		frame.getContentPane().add(comboScale);
		
		lblSyncMode.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSyncMode.setBounds(12, lblScaleMode.getLocation().y + lblScaleMode.getHeight() + 10, lblSyncMode.getPreferredSize().width, 16);
		frame.getContentPane().add(lblSyncMode);
		
		comboSync.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboSync.setEditable(false);
		comboSync.setSelectedItem("auto");
		comboSync.setBounds(lblSyncMode.getX() + lblSyncMode.getWidth() + 6, lblSyncMode.getLocation().y - 4, comboSync.getPreferredSize().width, 22);
		comboSync.setMaximumRowCount(10);
		frame.getContentPane().add(comboSync);
		
		lblThreads.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblThreads.setBounds(12, lblSyncMode.getLocation().y + lblSyncMode.getHeight() + 10, lblThreads.getPreferredSize().width, lblThreads.getPreferredSize().height);
		frame.getContentPane().add(lblThreads);
		
		txtThreads.setHorizontalAlignment(SwingConstants.CENTER);
		txtThreads.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		txtThreads.setColumns(10);
		txtThreads.setBounds(lblThreads.getLocation().x + lblThreads.getWidth() + 6, lblThreads.getLocation().y - 4, 36, 21);
		frame.getContentPane().add(txtThreads);
		
		txtThreads.addKeyListener(new KeyAdapter(){

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (txtThreads.getText().length() >= 3)
					txtThreads.setText("");				
			}			
			
		});
		
		lblImageToVideo.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblImageToVideo.setBounds(12, lblThreads.getLocation().y + lblThreads.getHeight() + 10, lblImageToVideo.getPreferredSize().width + 4, lblImageToVideo.getPreferredSize().height);
		frame.getContentPane().add(lblImageToVideo);
		
		txtImageDuration.setHorizontalAlignment(SwingConstants.CENTER);
		txtImageDuration.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		txtImageDuration.setColumns(10);
		txtImageDuration.setBounds(lblImageToVideo.getLocation().x + lblImageToVideo.getWidth() + 6, lblImageToVideo.getLocation().y - 4, 36, 21);
		frame.getContentPane().add(txtImageDuration);
		
		JLabel lblSec = new JLabel("sec");
		lblSec.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblSec.setBounds(txtImageDuration.getLocation().x + txtImageDuration.getWidth() + 4, lblImageToVideo.getLocation().y, 34, lblImageToVideo.getPreferredSize().height);
		frame.getContentPane().add(lblSec);

		txtImageDuration.addKeyListener(new KeyAdapter(){

			@Override
			public void keyTyped(KeyEvent e) {	
				char caracter = e.getKeyChar();											
				if (String.valueOf(caracter).matches("[0-9]+") == false && caracter != '￿' || String.valueOf(caracter).matches("[éèçàù]"))
					e.consume(); 
				else if (txtImageDuration.getText().length() >= 4)
					txtImageDuration.setText("");				
			}			
			
		});		
		
		lblTheme.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblTheme.setBounds(12, lblSec.getLocation().y + lblSec.getHeight() + 10, lblTheme.getPreferredSize().width + 4, lblImageToVideo.getPreferredSize().height);
		frame.getContentPane().add(lblTheme);

		accentColor = new JPanel();
		accentColor.setName("accentColor");
		accentColor.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
		accentColor.setBackground(Utils.themeColor);
		accentColor.setBounds(lblTheme.getLocation().x + lblTheme.getWidth() + 6, lblTheme.getLocation().y - 4, 41, 22);
		frame.getContentPane().add(accentColor);
		
		accentColor.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				Utils.themeColor = JColorChooser.showDialog(frame, Shutter.language.getProperty("chooseColor"), new Color(71, 163, 236));
								
				if (Utils.themeColor != null)
				{
					accentColor.setBackground(Utils.themeColor);	
					
					saveSettings();
					
					if (FFMPEG.isRunning)
						Shutter.btnCancel.doClick();
					
					if (FFMPEG.isRunning == false)
					{
						int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("restart"), Shutter.language.getProperty("frameSettings"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);	
						
						if (reply == JOptionPane.YES_OPTION) 
						{													
							Utils.restartApp();
						}
					}
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {		
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
		
		lblLanguage.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lblLanguage.setBounds(12, lblTheme.getLocation().y + lblTheme.getHeight() + 10, lblLanguage.getPreferredSize().width, lblImageToVideo.getPreferredSize().height);
		frame.getContentPane().add(lblLanguage);
			
		comboLanguage.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 10));
		comboLanguage.setEditable(false);		
		
		//load languages
		int count = 0;
		for (File f : new File(Utils.pathToLanguages).listFiles())
		{
			if (f.isHidden() == false)
			{
				count++;
			}
		}
		
		String[] data = new String[count]; //Avoid any hidden file which freeze the startup

		int d = 0;
		for (File f : new File(Utils.pathToLanguages).listFiles())
		{
			if (f.isHidden() == false)
			{
				String l[] = f.getName().split("\\.");
				
				String language = Locale.of(l[0]).getDisplayLanguage();
				String country = "";
											
				//Country
				if (l[0].contains("_"))
				{				
					String c[] = l[0].split("_");
					language = Locale.of(c[0]).getDisplayLanguage();
					country = " (" + Locale.of(c[0], c[1]).getDisplayCountry() + ")";
				}
				
				data[d] = (language + country);	
				d++;				
			}
		}
		
		//Sort
		Arrays.sort(data);
		
		//Add to comboLanguage
		for (int i = 0 ; i < data.length ; i++) 
        {
			comboLanguage.addItem(data[i].toString());
	    }
		
		//Set comboItem
		comboLanguage.setSelectedItem(Shutter.getLanguage);
		comboLanguage.setBounds(btnEndingAction.getX() + lblLanguage.getWidth() + 6, lblLanguage.getLocation().y - 4, comboLanguage.getPreferredSize().width, 22);
		comboLanguage.setMaximumRowCount(30);
		frame.getContentPane().add(comboLanguage);
		
		comboLanguage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (frame.isVisible())
				{
					saveSettings();
					
					if (FFMPEG.isRunning)
						Shutter.btnCancel.doClick();
					
					if (FFMPEG.isRunning == false)
					{
						int reply = JOptionPane.showConfirmDialog(frame, Shutter.language.getProperty("restart"), Shutter.language.getProperty("frameSettings"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);	
						
						if (reply == JOptionPane.YES_OPTION) 
						{													
							Utils.restartApp();
						}
					}
				}
			}
			
		});
					
		defaultOutput1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		defaultOutput1.setBounds(12,  lblLanguage.getLocation().y + lblLanguage.getHeight() + 10, defaultOutput1.getPreferredSize().width, defaultOutput1.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput1);

		if (lastUsedOutput1.isSelected())
			lblDestination1.setForeground(Color.LIGHT_GRAY);
		else
			lblDestination1.setForeground(Utils.themeColor);
		lblDestination1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination1.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblDestination1.setBackground(new Color(30,30,35));
		if (lblDestination1.getText() == "")
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				lblDestination1.setText(System.getProperty("user.home") + "/Desktop");
			else
				lblDestination1.setText(System.getProperty("user.home") + "\\Desktop");
		}
		lblDestination1.setBounds(12, defaultOutput1.getLocation().y + defaultOutput1.getHeight() + 6, frame.getWidth() - 36, lblDestination1.getPreferredSize().height);
		frame.getContentPane().add(lblDestination1);

		lblDestination1.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lastUsedOutput1.isSelected() == false)
				{
					File destination = null;
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);
	
						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();
	
						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
	
						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}
	
						shell.dispose();
					}
	
					if (destination != null) {					
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
							destination = Utils.UNCPath(destination);
						
						if (destination.isFile())
							lblDestination1.setText(destination.getParent());
						else
							lblDestination1.setText(destination.toString());
						
						if (destination.toString().equals(System.getProperty("user.home") + "/Desktop") == false
						&& destination.toString().equals(System.getProperty("user.home") + "\\Desktop") == false)
						{
							Shutter.lblDestination1.setText(destination.toString());
							Shutter.caseChangeFolder1.setSelected(true);
							Shutter.caseOpenFolderAtEnd1.setSelected(false);
						}
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (lastUsedOutput1.isSelected() == false)
					lblDestination1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
			
		lastUsedOutput1.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lastUsedOutput1.setBounds(defaultOutput1.getX() + defaultOutput1.getWidth() + 10, defaultOutput1.getLocation().y, lastUsedOutput1.getPreferredSize().width, 16);
		frame.getContentPane().add(lastUsedOutput1);
		
		lastUsedOutput1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lastUsedOutput1.isSelected())
					lblDestination1.setForeground(Color.LIGHT_GRAY);
				else
					lblDestination1.setForeground(Utils.themeColor);				
			}	
		});
		
		defaultOutput2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		defaultOutput2.setBounds(12,  lblDestination1.getLocation().y + lblDestination1.getHeight() + 10, defaultOutput2.getPreferredSize().width, defaultOutput2.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput2);
		
		if (lastUsedOutput2.isSelected())
			lblDestination2.setForeground(Color.LIGHT_GRAY);
		else
			lblDestination2.setForeground(Utils.themeColor);
		lblDestination2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination2.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblDestination2.setBackground(new Color(30,30,35));
		if (lblDestination2.getText() == "")
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				lblDestination2.setText(System.getProperty("user.home") + "/Desktop");
			else
				lblDestination2.setText(System.getProperty("user.home") + "\\Desktop");
		}
		lblDestination2.setBounds(12, defaultOutput2.getLocation().y + defaultOutput2.getHeight() + 6, frame.getWidth() - 36, lblDestination2.getPreferredSize().height);
		frame.getContentPane().add(lblDestination2);

		lblDestination2.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (lastUsedOutput2.isSelected() == false)
				{
					File destination = null;
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);
	
						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();
	
						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
	
						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}
	
						shell.dispose();
					}
	
					if (destination != null) {					
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
							destination =Utils.UNCPath(destination);
						
						if (destination.isFile())
							lblDestination2.setText(destination.getParent());
						else
							lblDestination2.setText(destination.toString());		
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (lastUsedOutput2.isSelected() == false)
					lblDestination2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination2.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		lastUsedOutput2.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lastUsedOutput2.setBounds(defaultOutput2.getX() + defaultOutput2.getWidth() + 10, defaultOutput2.getLocation().y, lastUsedOutput2.getPreferredSize().width, 16);
		frame.getContentPane().add(lastUsedOutput2);
		
		lastUsedOutput2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lastUsedOutput2.isSelected())
					lblDestination2.setForeground(Color.LIGHT_GRAY);
				else
					lblDestination2.setForeground(Utils.themeColor);				
			}	
		});
		
		defaultOutput3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		defaultOutput3.setBounds(12,  lblDestination2.getLocation().y + lblDestination2.getHeight() + 10, defaultOutput3.getPreferredSize().width, defaultOutput3.getPreferredSize().height);
		frame.getContentPane().add(defaultOutput3);
		
		if (lastUsedOutput3.isSelected())
			lblDestination3.setForeground(Color.LIGHT_GRAY);
		else
			lblDestination3.setForeground(Utils.themeColor);
		lblDestination3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		lblDestination3.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblDestination3.setBackground(new Color(30,30,35));
		if (lblDestination3.getText() == "")
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("Linux"))
				lblDestination3.setText(System.getProperty("user.home") + "/Desktop");
			else
				lblDestination3.setText(System.getProperty("user.home") + "\\Desktop");
		}
		lblDestination3.setBounds(12, defaultOutput3.getLocation().y + defaultOutput3.getHeight() + 6, frame.getWidth() - 36, lblDestination3.getPreferredSize().height);
		frame.getContentPane().add(lblDestination3);
		
		lblDestination3.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {				
				if (lastUsedOutput3.isSelected() == false)
				{
					File destination = null;
					if (System.getProperty("os.name").contains("Mac")) {
						FileDialog dialog = new FileDialog(frame, Shutter.language.getProperty("chooseDestinationFolder"),
								FileDialog.LOAD);
						dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
						dialog.setLocation(frame.getLocation().x - 50, frame.getLocation().y + 50);
						dialog.setAlwaysOnTop(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
						dialog.setVisible(true);
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
						if (dialog.getDirectory() != null)
							destination = new File(dialog.getDirectory() + dialog.getFile());
					} else if (System.getProperty("os.name").contains("Linux")) {
						JFileChooser dialog = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						dialog.setDialogTitle(Shutter.language.getProperty("chooseDestinationFolder"));
						dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						
						if (Settings.lblDestination1.getText() != "" && new File(Settings.lblDestination1.getText()).exists())
							dialog.setSelectedFile(new File(Settings.lblDestination1.getText()));
						else
							dialog.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop"));

						int result = dialog.showOpenDialog(frame);
						if (result == JFileChooser.APPROVE_OPTION) 
			               destination = new File(dialog.getSelectedFile().toString());				   
					} else {
						Shell shell = new Shell(SWT.ON_TOP);
	
						shell.setSize(frame.getSize().width, frame.getSize().height);
						shell.setLocation(frame.getLocation().x, frame.getLocation().y);
						shell.setAlpha(0);
						shell.open();
	
						DirectoryDialog dialog = new DirectoryDialog(shell);
						dialog.setText(Shutter.language.getProperty("chooseDestinationFolder"));							
						dialog.setFilterPath(System.getProperty("user.home") + "\\Desktop");
	
						try {
							destination = new File(dialog.open());
						} catch (Exception e1) {}
	
						shell.dispose();
					}
	
					if (destination != null) {					
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && destination.toString().substring(0, 2).equals("\\\\"))
							destination =Utils.UNCPath(destination);
						
						if (destination.isFile())
							lblDestination3.setText(destination.getParent());
						else
							lblDestination3.setText(destination.toString());					
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (lastUsedOutput3.isSelected() == false)
					lblDestination3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				lblDestination3.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		lastUsedOutput3.setFont(new Font(Shutter.freeSansFont, Font.PLAIN, 12));
		lastUsedOutput3.setBounds(defaultOutput3.getX() + defaultOutput3.getWidth() + 10, defaultOutput3.getLocation().y, lastUsedOutput3.getPreferredSize().width, 16);
		frame.getContentPane().add(lastUsedOutput3);
		
		lastUsedOutput3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lastUsedOutput3.isSelected())
					lblDestination3.setForeground(Color.LIGHT_GRAY);
				else
					lblDestination3.setForeground(Utils.themeColor);				
			}	
		});
				
		// Drag & Drop
		lblDestination1.setTransferHandler(new OutputTransferHandler());
		lblDestination2.setTransferHandler(new OutputTransferHandler());
		lblDestination3.setTransferHandler(new OutputTransferHandler());
		
		JButton btnReset = new JButton(Shutter.language.getProperty("btnReset"));
		btnReset.setName("btnReset");
		btnReset.setFont(new Font(Shutter.montserratFont, Font.PLAIN, 12));
		btnReset.setSize(btnReset.getPreferredSize().width + 4, 21);
		btnReset.setLocation(backgroundPanel.getWidth() / 2 - (btnReset.getWidth() + 14), backgroundPanel.getHeight() / 2 - btnReset.getHeight() / 2);
		backgroundPanel.add(btnReset);

		btnReset.addActionListener(new ActionListener() {

			@SuppressWarnings("unused")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int reply = JOptionPane.showConfirmDialog(frame,
						Shutter.language.getProperty("areYouSure"),
						Shutter.language.getProperty("frameSettings"), JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);					
				if (reply == JOptionPane.YES_OPTION) 
				{	
					if (Shutter.settingsXML.exists())
						Shutter.settingsXML.delete();
					
					try {
						String newShutter;
						if (System.getProperty("os.name").contains("Windows")) {
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							newShutter = '"' + newShutter.substring(1, newShutter.length()).replace("%20", " ") + '"';
							String[] arguments = new String[] { newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						} else if (System.getProperty("os.name").contains("Mac")) {
							newShutter = Shutter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
							newShutter = newShutter.substring(0, newShutter.length() - 1);
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/")));
							newShutter = newShutter.substring(0, (int) (newShutter.lastIndexOf("/"))).replace(" ",
									"\\ ");
							String[] arguments = new String[] { "/bin/bash", "-c", "open -n " + newShutter };
							Process proc = new ProcessBuilder(arguments).start();
						} else { //Linux	
							String[] arguments = new String[] { "/bin/bash", "-c", "shutter-encoder"};
							Process proc = new ProcessBuilder(arguments).start();
						}
	
					} catch (Exception error) {
					}
				
					System.exit(0);
				}
				
			}
		
		});
		
		JButton donate;		
		if (Shutter.getLanguage.equals(Locale.of("fr").getDisplayLanguage()))
			donate = new JButton("Faire un don");
		else
			donate = new JButton("Donate");
		
		donate.setName("donate");
		donate.setSize(donate.getPreferredSize().width, donate.getPreferredSize().height);
		donate.setLocation(backgroundPanel.getWidth() / 2 + 14, backgroundPanel.getHeight() / 2 - donate.getHeight() / 2);
		backgroundPanel.add(donate);

		donate.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				try {
					
					if (Shutter.getLanguage.equals(Locale.of("fr").getDisplayLanguage())
					|| Shutter.getLanguage.equals(Locale.of("it").getDisplayLanguage())
					|| Shutter.getLanguage.equals(Locale.of("es").getDisplayLanguage()))
					{
						Desktop.getDesktop().browse(new URI("https://www.paypal.com/donate/?cmd=_donations&business=paulpacifico974@gmail.com&item_name=Shutter+Encoder&currency_code=EUR"));
					}
					else
						Desktop.getDesktop().browse(new URI("https://www.paypal.com/donate/?cmd=_donations&business=paulpacifico974@gmail.com&item_name=Shutter+Encoder&currency_code=USD"));
				
				} catch (IOException | URISyntaxException e) {
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

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		/*			
		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollBar.setValue(scrollBar.getValue() + e.getWheelRotation() * 10);				
			}
			
		});*/
		
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
				lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
			}

		});
		
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	if (txtThreads.getText().isEmpty() ||  txtThreads.getText() == null)
            		txtThreads.setText("0");
            		
				Settings.saveSettings();
            }
        });
		
		loadSettings();
		
	}
	
	private void topPanel() {
		
		topPanel = new JPanel();	
		topPanel.setName("topPanel");
		topPanel.setBackground(new Color(30,30,35));
		topPanel.setLayout(null);
			
		quit = new JLabel(new FlatSVGIcon("contents/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 20, 4, 15, 15);
		
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
					
	            	if (txtThreads.getText().isEmpty() || txtThreads.getText() == null)
	            		txtThreads.setText("0");
	            		
					Settings.saveSettings();
					
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

		help = new JLabel(new FlatSVGIcon("contents/help.svg", 15, 15));
		help.setHorizontalAlignment(SwingConstants.CENTER);
		help.setBounds(reduce.getLocation().x - 20, 4, 15, 15);
		topPanel.add(help);

		help.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("contents/help_pressed.svg", 15, 15));
				accept = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (accept)
				{
					try {
						Desktop.getDesktop().browse(new URI("https://www.shutterencoder.com/documentation.html#Settings-icon"));
					}catch(Exception er){}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("contents/help_hover.svg", 15, 15));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				help.setIcon(new FlatSVGIcon("contents/help.svg", 15, 15));
				accept = false;
			}

		});
		
		topPanel.add(quit);
		topPanel.add(reduce);
		topPanel.add(help);
		topPanel.setBounds(0, 0, frame.getWidth(), 24);
		
		JLabel title = new JLabel(Shutter.language.getProperty("frameSettings"));
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

	@SuppressWarnings("rawtypes")
	public static void loadSettings() {	
		
	try {
		if (Shutter.settingsXML.exists())
		{
			File fXmlFile = Shutter.settingsXML;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		
			NodeList nList = doc.getElementsByTagName("Component");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
								
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;

					for (Component p : frame.getContentPane().getComponents())
					{		
						if (p.getName() != "" && p.getName() != null && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
						{								
							if (p instanceof JPanel && p.getName().equals("backgroundPanel") == false)
							{						
								//Value
								String s[] = eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split("\\[");
								String s2[] = s[1].split(",");
								((JPanel) p).setBackground(new Color(Integer.valueOf(s2[0]), Integer.valueOf(s2[1]), Integer.valueOf(s2[2])));
								
								if (p.getName().equals("accentColor"))
									Utils.themeColor = accentColor.getBackground();
							}
							else if (p instanceof JCheckBox)
							{
								//Value
								if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()))
								{
									if (((JCheckBox) p).isSelected() == false)
										((JCheckBox) p).doClick();
								}
								else
								{
									if (((JCheckBox) p).isSelected())
										((JCheckBox) p).doClick();
								}
																	
								//State
								((JCheckBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
								
								//Visible
								((JCheckBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
							}
							else if (p instanceof JLabel 
									&& p.getName().equals("backgroundPanel") == false
									&& p.getName().equals("btnReset") == false
									&& p.getName().equals("donate") == false
									&& p.getName().equals("topPanel") == false)
							{			
								//Value
								((JLabel) p).setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
																	
								//State
								((JLabel) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
								
								//Visible
								((JLabel) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));	
								
								if (p.getName().equals("lblDestination1")
									&& eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().equals(System.getProperty("user.home") + "/Desktop") == false
									&& eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent().equals(System.getProperty("user.home") + "\\Desktop") == false)
								{
									Shutter.lblDestination1.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
									Shutter.caseChangeFolder1.setSelected(true);
									Shutter.caseOpenFolderAtEnd1.setSelected(false);
								}
								
							}
							else if (p instanceof JComboBox)
							{				
								//Value
								((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								
								//State
								((JComboBox) p).setEnabled(Boolean.valueOf(eElement.getElementsByTagName("Enable").item(0).getFirstChild().getTextContent()));
								
								//Visible
								((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
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
						}
					}
					
					for (Component p : Shutter.statusBar.getComponents())
					{
						if (p.getName() != "" && p.getName() != null && p.getName().equals("comboAccel") == false && p.getName().equals(eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent()))
						{
							if (p instanceof JComboBox)
							{															
								//Value
								((JComboBox) p).setSelectedItem(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
								
								//Visible
								((JComboBox) p).setVisible(Boolean.valueOf(eElement.getElementsByTagName("Visible").item(0).getFirstChild().getTextContent()));
								
							}
						}
					}
					
					//Open folder at end
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseOpenFolderAtEnd1"))
					{
						if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == true)
							Shutter.caseOpenFolderAtEnd1.setSelected(true);
						else
							Shutter.caseOpenFolderAtEnd1.setSelected(false);
					}
					
					//Change folder
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseChangeFolder1"))
					{
						if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == false
							&& lblDestination1.getText().equals(System.getProperty("user.home") + "/Desktop") == false
							&& lblDestination1.getText().equals(System.getProperty("user.home") + "\\Desktop") == false)
						{
							Shutter.caseChangeFolder1.doClick();
						}
					}
					
					//btnExtension
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("btnExtension"))
					{
						if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == true)
						{
							Shutter.btnExtension.setSelected(true);
							Shutter.txtExtension.setEnabled(true);
						}
						else
							Shutter.btnExtension.setSelected(false);
					}
						
					//Suffix
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("txtExtension"))
					{
						if (eElement.getElementsByTagName("Value").item(0).getFirstChild() != null)
						{
							Shutter.txtExtension.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
						}
						else
							Shutter.txtExtension.setText("");
					}
					
					//caseSubFolder
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseSubFolder"))
					{
						if (Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()) == true)
						{
							Shutter.caseSubFolder.setSelected(true);							
							Shutter.txtSubFolder.setEnabled(true);
						}
						else
							Shutter.caseSubFolder.setSelected(false);
					}
					
					//SubFolder
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("txtSubFolder"))
					{
						if (eElement.getElementsByTagName("Value").item(0).getFirstChild() != null)
						{
							Shutter.txtSubFolder.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
						}
						else
							Shutter.txtSubFolder.setText("");
					}
					
					//Volume video player
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("sliderVolume"))
					{
						VideoPlayer.sliderVolume.setValue(Integer.parseInt(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
					}
					
					//casePlaySound video player
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("casePlaySound"))
					{
						VideoPlayer.casePlaySound.setSelected(Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
					}
					
					//caseVuMeter video player
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseVuMeter"))
					{
						VideoPlayer.caseVuMeter.setSelected(Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
					}	
					
					//caseShowWaveform video player
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseShowWaveform"))
					{
						VideoPlayer.caseShowWaveform.setSelected(Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent()));
					}	
										
					//FTP
					if (eElement.getParentNode().getNodeName().equals("Ftp"))
					{		
						if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("textFtp"))
						{
							Ftp.textFtp.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
						}
						else if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("textUser"))
						{
							Ftp.textUser.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
						}
					}
					
					//CaseSendMail
					if (eElement.getParentNode().getNodeName().equals("caseSendMail"))
					{
						//Value
						Shutter.textMail.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
					}
					
					//CaseStream
					if (eElement.getParentNode().getNodeName().equals("caseStream"))
					{
						//Value
						Shutter.textStream.setText(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
					}
					
					//caseMetadata
					if (eElement.getElementsByTagName("Name").item(0).getFirstChild().getTextContent().equals("caseMetadata"))
					{
						videoWebCaseMetadata = Boolean.valueOf(eElement.getElementsByTagName("Value").item(0).getFirstChild().getTextContent());
					}
					
					//customFFmpeg
					if (btnCustomFFmpegPath.isSelected() && txtCustomFFmpegPath.getText().equals("") == false)
					{
						FFMPEG.PathToFFMPEG = txtCustomFFmpegPath.getText();
					}
				}
			}		
		}							
	} catch (Exception e) {}		
}
	
	@SuppressWarnings({ "rawtypes" })
	public static void saveSettings() {	
					
		try {
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			Element root = document.createElement("Settings");
			document.appendChild(root);

			for (Component p : frame.getContentPane().getComponents())
			{
				if (p.getName() != "" && p.getName() != null)
				{
					if (p instanceof JCheckBox)
					{
						//Component
						Element component = document.createElement("Component");
						
						//Type
						Element cType = document.createElement("Type");
						cType.appendChild(document.createTextNode("JCheckBox"));
						component.appendChild(cType);
						
						//Name
						Element cName = document.createElement("Name");
						cName.appendChild(document.createTextNode(p.getName()));
						component.appendChild(cName);
						
						//Value
						Element cValue = document.createElement("Value");
						cValue.appendChild(document.createTextNode(String.valueOf(((JCheckBox) p).isSelected())));
						component.appendChild(cValue);
						
						//State
						Element cState = document.createElement("Enable");
						cState.appendChild(document.createTextNode(String.valueOf(p.isEnabled())));
						component.appendChild(cState);
						
						//Visible
						Element cVisible = document.createElement("Visible");
						cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
						component.appendChild(cVisible);		
						
						root.appendChild(component);
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
						
						root.appendChild(component);
					}
					else if (p instanceof JComboBox)
					{
						if (p.getName().equals(comboLoadPreset.getName()))
						{
							if (comboLoadPreset.getItemCount() == 0)
								continue;
						}
						
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
						
						root.appendChild(component);
					}
					else if (p instanceof JTextField && ((JTextField) p).getText().length() > 0)
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
						
						root.appendChild(component);
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
						
						root.appendChild(component);
					}
				}
			}
			
			for (Component p : Shutter.statusBar.getComponents())
			{
				if (p.getName() != "" && p.getName() != null)
				{
					if (p instanceof JComboBox)
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
						
						//Visible
						Element cVisible = document.createElement("Visible");
						cVisible.appendChild(document.createTextNode(String.valueOf(p.isVisible())));
						component.appendChild(cVisible);		
						
						root.appendChild(component);
					}
				}
			}
						
			//Open folder at end
			//Component
			Element component = document.createElement("Component");
			
			//Type
			Element cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			Element cName = document.createElement("Name");
			cName.appendChild(document.createTextNode(Shutter.caseOpenFolderAtEnd1.getName()));
			component.appendChild(cName);
			
			//Value
			Element cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(Shutter.caseOpenFolderAtEnd1.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//Change folder
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");
			cName.appendChild(document.createTextNode(Shutter.caseChangeFolder1.getName()));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(Shutter.caseChangeFolder1.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//btnExtension
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");
			cName.appendChild(document.createTextNode(Shutter.btnExtension.getName()));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(Shutter.btnExtension.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//Suffix
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JTextField"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");
			cName.appendChild(document.createTextNode("txtExtension"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");				
			cValue.appendChild(document.createTextNode(Shutter.txtExtension.getText().toString()));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//caseSubFolder
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");
			cName.appendChild(document.createTextNode(Shutter.caseSubFolder.getName()));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(Shutter.caseSubFolder.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//SubFolder
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JTextField"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");
			cName.appendChild(document.createTextNode("txtSubFolder"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");				
			cValue.appendChild(document.createTextNode(Shutter.txtSubFolder.getText().toString()));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//Volume video player
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JSlider"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("sliderVolume"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(VideoPlayer.sliderVolume.getValue())));
			component.appendChild(cValue);	
			
			root.appendChild(component);
			
			//casePlaySound video player
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("casePlaySound"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(VideoPlayer.casePlaySound.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//caseVuMeter video player
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("caseVuMeter"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(VideoPlayer.caseVuMeter.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			
			//caseShowWaveform video player
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("caseShowWaveform"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(VideoPlayer.caseShowWaveform.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);
			/*
			//caseGPU video player
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("caseGPU"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			cValue.appendChild(document.createTextNode(String.valueOf(VideoPlayer.caseGPU.isSelected())));
			component.appendChild(cValue);
			
			root.appendChild(component);*/
			
			//FTP
			Element ftp = document.createElement("Ftp");
			
			if (Ftp.textFtp != null && Ftp.textFtp.getText().length() > 0)
			{
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("textFtp"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");				
				cValue.appendChild(document.createTextNode(Ftp.textFtp.getText().toString()));
				component.appendChild(cValue);
					
				ftp.appendChild(component);
			}
			
			if (Ftp.textUser != null && Ftp.textUser.getText().length() > 0)
			{
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("textUser"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");
				cValue.appendChild(document.createTextNode(Ftp.textUser.getText().toString()));
				component.appendChild(cValue);
					
				ftp.appendChild(component);		
			}
			
			if (Ftp.textFtp != null && Ftp.textFtp.getText().length() > 0 || Ftp.textUser != null && Ftp.textUser.getText().length() > 0)
				root.appendChild(ftp);
						
			//CaseSendMail	
			if (Shutter.textMail != null && Shutter.textMail.getText().length() > 0 && Shutter.textMail.getText().equals(Shutter.language.getProperty("textMail")) == false)
			{
				Element sendMail = document.createElement("caseSendMail");
				
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("textMail"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");
				cValue.appendChild(document.createTextNode(Shutter.textMail.getText().toString()));
				component.appendChild(cValue);
							
				sendMail.appendChild(component);
				
				root.appendChild(sendMail);
			}
			
			//caseStream	
			if (Shutter.textStream != null && Shutter.textStream.getText().length() > 0 && Shutter.textStream.getText().equals("rtmp://a.rtmp.youtube.com/live2/xxxx-xxxx-xxxx-xxxx-xxxx") == false)
			{
				Element stream = document.createElement("caseStream");
				
				//Component
				component = document.createElement("Component");
				
				//Type
				cType = document.createElement("Type");
				cType.appendChild(document.createTextNode("JTextField"));
				component.appendChild(cType);
				
				//Name
				cName = document.createElement("Name");
				cName.appendChild(document.createTextNode("textStream"));
				component.appendChild(cName);
				
				//Value
				cValue = document.createElement("Value");
				cValue.appendChild(document.createTextNode(Shutter.textStream.getText().toString()));
				component.appendChild(cValue);
							
				stream.appendChild(component);
				
				root.appendChild(stream);
			}
			
			//CaseMetadata
			//Component
			component = document.createElement("Component");
			
			//Type
			cType = document.createElement("Type");
			cType.appendChild(document.createTextNode("JCheckBox"));
			component.appendChild(cType);
			
			//Name
			cName = document.createElement("Name");			
			cName.appendChild(document.createTextNode("caseMetadata"));
			component.appendChild(cName);
			
			//Value
			cValue = document.createElement("Value");
			if (VideoWeb.caseMetadata != null)
				cValue.appendChild(document.createTextNode(String.valueOf(VideoWeb.caseMetadata.isSelected())));
			else
				cValue.appendChild(document.createTextNode(String.valueOf(videoWebCaseMetadata)));
			component.appendChild(cValue);
			
			root.appendChild(component);		
						
			// creation du fichier XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(Shutter.settingsXML);

			transformer.transform(domSource, streamResult);
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}				
}

//Drag & Drop lblDestination
@SuppressWarnings("serial")
class OutputTransferHandler extends TransferHandler {

	public boolean canImport(JComponent comp, DataFlavor[] arg1) {
		
		for (int i = 0; i < arg1.length; i++) 
		{
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor))
			{
				if (comp.getName().equals("lblDestination1"))
				{
					if (Settings.lastUsedOutput1.isSelected() == false)
					{
						Settings.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));					
						return true;
					}
					else
						return false;
				}
				else if (comp.getName().equals("lblDestination2"))
				{
					if (Settings.lastUsedOutput2.isSelected() == false)
					{
						Settings.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));					
						return true;
					}
					else
						return false;
				}
				else if (comp.getName().equals("lblDestination3"))
				{
					if (Settings.lastUsedOutput3.isSelected() == false)
					{
						Settings.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));					
						return true;
					}
					else
						return false;
				}
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
					
					while (iter.hasNext())
					{						
						File file = (File) iter.next();
						
						//Montage du chemin UNC
						if (System.getProperty("os.name").contains("Windows") && file.toString().substring(0, 2).equals("\\\\"))
							file = Utils.UNCPath(file);
						
						if (file.getName().contains(".")) 
						{
							((JLabel) comp).setText(file.getParent());
						} 
						else 
						{
							((JLabel) comp).setText(file.getAbsolutePath());
						}

					}

					// Border
					Settings.lblDestination1.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					Settings.lblDestination2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));
					Settings.lblDestination3.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0));

					return true;
				}
			} catch (IOException | UnsupportedFlavorException ex) {
			}
		}
		return false;
	}
}