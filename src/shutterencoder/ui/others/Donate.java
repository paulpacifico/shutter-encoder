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

package shutterencoder.ui.others;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.renderers.AntiAliasedRoundRectangle;
import shutterencoder.utils.Utils;

public class Donate {

	private static int MousePositionX;
	private static int MousePositionY;

	private static final Color ACCENT = Utils.themeColor;
	private static final Color MUTED_TEXT = new Color(150, 150, 150);
	
	public static JFrame frame;
	public static JCheckBox boxMail;
	public static JTextField textMailDonate = new JTextField();
	public static JButton btnCheckMail;

	public Donate()  {

		frame = new JFrame();
		frame.getContentPane().setBackground(Utils.background);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Thanks!");
		frame.setBackground(Utils.background);
		frame.setForeground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setSize(300, 530);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(45,45,45)));
		Area shape1 = new Area(new AntiAliasedRoundRectangle(0, 0, frame.getWidth(), frame.getHeight(), 15, 15));
	    Area shape2 = new Area(new AntiAliasedRoundRectangle(0, frame.getHeight() - 15, frame.getWidth(), 15, 15, 15));
	    shape1.add(shape2);
		frame.setShape(shape1);
		frame.setAlwaysOnTop(true);

		if (System.getProperty("os.name").contains("Mac") == false)
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("resources/icon.png")).getImage());

		frame.setLocation(Shutter.frame.getX() + (Shutter.frame.getWidth() - frame.getWidth()) / 2, Shutter.frame.getY() + (Shutter.frame.getHeight() - frame.getHeight()) / 2);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(null);
		topPanel.setBackground(Utils.background);
		topPanel.setBounds(0, 0, frame.getWidth(), 30);

		JLabel quit = new JLabel(new FlatSVGIcon("resources/quit.svg", 15, 15));
		quit.setHorizontalAlignment(SwingConstants.CENTER);
		quit.setBounds(frame.getSize().width - 22, 7, 15, 15);
		topPanel.add(quit);

		quit.addMouseListener(new MouseListener() {

			private boolean accept = false;

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 0.9f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));				
				accept = true;
			}
		 	
			@Override
			public void mouseReleased(MouseEvent e) {

				if (accept)
				{			
					textMailDonate.setText("");
					
					System.exit(0);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15).setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
				    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				    float newBrightness = Math.min(1.0f, hsb[2] * 1.1f); 				    
				    return Color.getHSBColor(hsb[0], hsb[1], newBrightness);
				})));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				quit.setIcon(new FlatSVGIcon("resources/quit.svg", 15, 15));
				accept = false;
			}

		});

		JLabel title = new JLabel(frame.getTitle());
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBounds(0, 2, frame.getWidth(), 26);
		title.setFont(new Font(Shutter.mainFont, Font.BOLD, 14));
		title.setForeground(Color.WHITE);
		topPanel.add(title);

		JLabel topImage = new JLabel();
		topImage.setBackground(Utils.c42);
		topImage.setOpaque(true);
		topImage.setBorder(new MatteBorder(1, 0, 1, 0, new Color(45,45,45)));
		topImage.setBounds(0, 0, topPanel.getWidth(), 26);
		topPanel.add(topImage);

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

		frame.getContentPane().add(topPanel);

		final int cardPadding = 18;
		final int qrSize = 220;
		final int cardSize = qrSize + cardPadding * 2;
		final boolean[] hovered = { false };

		JPanel qrCard = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        Graphics2D g2 = (Graphics2D) g.create();
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		        g2.setColor(Color.WHITE);
		        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

		        g2.setColor(hovered[0] ? ACCENT : new Color(225, 225, 225));
		        g2.setStroke(new java.awt.BasicStroke(hovered[0] ? 2f : 1f));
		        int inset = hovered[0] ? 1 : 0;
		        g2.drawRoundRect(inset, inset, getWidth() - 1 - inset * 2, getHeight() - 1 - inset * 2, 15, 15);

		        g2.dispose();
		        super.paintComponent(g);
		    }
		};
		qrCard.setOpaque(false);
		qrCard.setLayout(null);
		qrCard.setBounds((frame.getWidth() - cardSize) / 2, topPanel.getHeight() + 14, cardSize, cardSize);
		frame.getContentPane().add(qrCard);

		JLabel qrcode = new JLabel();
		qrcode.setIcon(new ImageIcon(getClass().getClassLoader().getResource("resources/qrcode.png")));
		qrcode.setHorizontalAlignment(SwingConstants.CENTER);
		qrcode.setBounds(cardPadding, cardPadding, qrSize, qrSize);
		qrCard.add(qrcode);

		MouseAdapter hoverListener = new MouseAdapter() {
		    @Override
		    public void mouseEntered(MouseEvent e) {
		        hovered[0] = true;
		        qrCard.repaint();
		    }

		    @Override
		    public void mouseExited(MouseEvent e) {
		        hovered[0] = false;
		        qrCard.repaint();
		    }
		};
		qrCard.addMouseListener(hoverListener);
		qrcode.addMouseListener(hoverListener);

		MouseListener qrClickListener = new MouseListener() {

		    @Override
		    public void mouseClicked(MouseEvent arg0) {
		        try {
		            Desktop.getDesktop().browse(new URI("https://donate.stripe.com/28o29m0QwfRZ4U0cMM"));
		        } catch (IOException | URISyntaxException e) {}

		        System.exit(0);
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

		};

		qrCard.addMouseListener(qrClickListener);
		qrcode.addMouseListener(qrClickListener);

		JLabel line1 = new JLabel("Hope you're enjoying the software!");
		line1.setHorizontalAlignment(SwingConstants.CENTER);
		line1.setSize(frame.getWidth() - 20, 18);
		line1.setFont(new Font(Shutter.mainFont, Font.BOLD, 13));
		line1.setForeground(Color.WHITE);
		line1.setLocation((frame.getWidth() - line1.getWidth()) / 2, qrCard.getY() + qrCard.getHeight() + 16);
		frame.getContentPane().add(line1);

		JLabel line2 = new JLabel("Scan the QR code to support development");
		line2.setHorizontalAlignment(SwingConstants.CENTER);
		line2.setSize(frame.getWidth() - 20, 16);
		line2.setFont(new Font(Shutter.mainFont, Font.PLAIN, 11));
		line2.setForeground(MUTED_TEXT);
		line2.setLocation((frame.getWidth() - line2.getWidth()) / 2, line1.getY() + line1.getHeight() + 4);
		frame.getContentPane().add(line2);

		JLabel divider = new JLabel();
		divider.setOpaque(true);
		divider.setBackground(new Color(45, 45, 45));
		divider.setBounds(20, line2.getY() + line2.getHeight() + 14, frame.getWidth() - 40, 1);
		frame.getContentPane().add(divider);

		final String checkColor = "#5ED9A6";
		final Color itemTextColor = new Color(200, 200, 200);
		final Font checklistFont = new Font("SansSerif", Font.PLAIN, 11);
		final int colWidth = 130;
		final int colGap = 10;
		final int listY = divider.getY() + divider.getHeight() + 14;
		final int listHeight = 64;
		final int groupX = (frame.getWidth() - (colWidth * 2 + colGap)) / 2 + 14;

		JLabel featuresLeft = new JLabel(
			"<html>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No subscription<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No account<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No advertising<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No bloatware"
			+ "</html>"
		);
		featuresLeft.setHorizontalAlignment(SwingConstants.LEFT);
		featuresLeft.setVerticalAlignment(SwingConstants.TOP);
		featuresLeft.setFont(checklistFont);
		featuresLeft.setForeground(itemTextColor);
		featuresLeft.setBounds(groupX, listY, colWidth, listHeight);
		frame.getContentPane().add(featuresLeft);

		JLabel featuresRight = new JLabel(
			"<html>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No limitations<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No watermark<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> No telemetry<br>"
			+ "<font color='" + checkColor + "'>&#10003;</font> Open source"
			+ "</html>"
		);
		featuresRight.setHorizontalAlignment(SwingConstants.LEFT);
		featuresRight.setVerticalAlignment(SwingConstants.TOP);
		featuresRight.setFont(checklistFont);
		featuresRight.setForeground(itemTextColor);
		featuresRight.setBounds(groupX + colWidth + colGap, listY, colWidth, listHeight);
		frame.getContentPane().add(featuresRight);

		boxMail = new JCheckBox("Don't show this window again");
		boxMail.setName("boxMail");
		boxMail.setSelected(false);
		boxMail.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
		boxMail.setBounds(7 ,featuresLeft.getY() + featuresLeft.getHeight(), boxMail.getPreferredSize().width + 4, 23);
		frame.getContentPane().add(boxMail);

		boxMail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (boxMail.isSelected())
				{
					textMailDonate.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMailDonate.setForeground(Color.LIGHT_GRAY);
					
					if (textMailDonate.getText().length() <= 0)
					{
						textMailDonate.setText(Shutter.language.getProperty("textMail"));
					}
					else if (textMailDonate.getText().equals(Shutter.language.getProperty("textMail")) == false)
					{
						textMailDonate.setFont(new Font("SansSerif", Font.PLAIN, 12));
						textMailDonate.setForeground(Color.WHITE);
					}
					
					textMailDonate.setEnabled(true);					
				}
				else
				{
					textMailDonate.setEnabled(false);
				}
			}
		});

		textMailDonate.setName("textMailAccount");
		textMailDonate.setEnabled(false);
		textMailDonate.setFont(new Font("SansSerif", Font.ITALIC, 12));
		textMailDonate.setForeground(Color.LIGHT_GRAY);
		textMailDonate.setBounds(10, boxMail.getY() + boxMail.getHeight() + 2, frame.getWidth() - 20, 21);
		textMailDonate.setColumns(1);
		
		if (textMailDonate.getText().isEmpty())
			textMailDonate.setText(Shutter.language.getProperty("textMail"));
		
		frame.getContentPane().add(textMailDonate);

		textMailDonate.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				
				if (textMailDonate.getText().equals(Shutter.language.getProperty("textMail"))) {
					textMailDonate.setText("");
					textMailDonate.setFont(new Font("SansSerif", Font.PLAIN, 12));
					textMailDonate.setForeground(Color.WHITE);
				}

				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					textMailDonate.setFont(new Font("SansSerif", Font.ITALIC, 12));
					textMailDonate.setForeground(Color.LIGHT_GRAY);
					textMailDonate.setText(Shutter.language.getProperty("textMail"));
					boxMail.setSelected(false);
					textMailDonate.setEnabled(false);
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) 
				{
					btnCheckMail.doClick();
				}
			}

		});
		
		textMailDonate.addPropertyChangeListener("enabled", evt -> {			
		    btnCheckMail.setEnabled((Boolean) evt.getNewValue());
		});
		
		btnCheckMail = new JButton("Check donation");
		btnCheckMail.setEnabled(false);
		btnCheckMail.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
		btnCheckMail.setBounds(textMailDonate.getX(), textMailDonate.getY() + textMailDonate.getHeight() + 6, textMailDonate.getWidth(), 21);
		frame.getContentPane().add(btnCheckMail);
		
		btnCheckMail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boxMail.setEnabled(false);
				textMailDonate.setEnabled(false);		
				
				checkMailAddress(false, false);
			}
			
		});
		
		frame.setVisible(true);
	}
	
	public static void checkMailAddress(boolean newMachineID, boolean noWarning) {

		Thread check = new Thread(new Runnable() {

			@Override
			public void run() {
				
				if (frame != null && frame.isVisible())
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				try {
					
					String phpFile = newMachineID ? "transfer.php" : "verify.php";
					
					@SuppressWarnings("deprecation")
					URL url = new URL(
					    "https://www.shutterencoder.com/Stripe/" + phpFile + "?email="
					    + URLEncoder.encode(textMailDonate.getText(), "UTF-8")
					    + "&machine="
					    + URLEncoder.encode(MachineId.get(), "UTF-8"));
			
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
					String response = in.readLine();

					if (noWarning)
					{
						if ("OK".equals(response))
						{
							System.exit(0);
						}
						else
							new Donate();
						
						return; //Break to display the donate frame
					}
					else
					{
						if ("OK".equals(response))
						{
							Settings.saveSettings();
							
							System.exit(0);
						}
						else if ("INVALID".equals(response))
						{
							JOptionPane.showMessageDialog(Donate.frame, "No donation found!", "Check donation", JOptionPane.ERROR_MESSAGE);
							
							boxMail.setEnabled(true);
							textMailDonate.setEnabled(true);	
							textMailDonate.requestFocusInWindow();
						}
						else if ("DIFFERENT_MACHINE".equals(response))
						{
							int result = JOptionPane.showConfirmDialog(
							        Donate.frame,
							        "This email is already connected on another device.\nDo you want to disconnect it and connect here?",
							        "Already connected",
							        JOptionPane.YES_NO_OPTION,
							        JOptionPane.WARNING_MESSAGE);
	
						    if (result == JOptionPane.YES_OPTION)
						    {
						    	checkMailAddress(true, false);
						    }
						    else
						    {
						        boxMail.setEnabled(true);
						        textMailDonate.setText("");
								textMailDonate.setEnabled(true);	
								textMailDonate.requestFocusInWindow();
						    }
						}
					}
					
				}
				catch (Exception e)
				{					
					if (noWarning)
					{
						System.exit(0);					
					}
					else
					{
						JOptionPane.showMessageDialog(Donate.frame, "Please check your internet connection!", "Check donation", JOptionPane.ERROR_MESSAGE);
						
						boxMail.setEnabled(true);
				        textMailDonate.setText("");
						textMailDonate.setEnabled(true);	
						textMailDonate.requestFocusInWindow();
					}
				}	
				finally {
					
					if (frame != null && frame.isVisible())
						Donate.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}			
		});
		check.start();		
	}
}

class MachineId {
	
    public static String get() {
    	
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String raw;

            if (os.contains("windows")) {
                raw = getWindowsId();
            } else if (os.contains("mac")) {
                raw = getMacId();
            } else {
                raw = getLinuxId();
            }

            // Hash into clean ID
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes());
            StringBuilder hexId = new StringBuilder();
            for (byte b : hash) {
                hexId.append(String.format("%02x", b));
            }
            return hexId.toString();

        } catch (Exception e) {
            return "unknown";
        }
    }

    @SuppressWarnings("deprecation")
	private static String getWindowsId() throws Exception {
        // Motherboard serial — never changes, even after reboot or WiFi change
        Process process = Runtime.getRuntime().exec(
            "wmic baseboard get serialnumber"
        );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("SerialNumber")) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static String getMacId() throws Exception {
        // Hardware UUID — stable on macOS
        Process process = Runtime.getRuntime().exec(
            new String[]{"system_profiler", "SPHardwareDataType"}
        );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Hardware UUID")) {
                return line.split(":")[1].trim();
            }
        }
        return "unknown-mac";
    }

    private static String getLinuxId() throws Exception {
        // Machine ID — stable on Linux
        Process process = Runtime.getRuntime().exec(
            new String[]{"cat", "/etc/machine-id"}
        );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        String line = reader.readLine();
        return line != null ? line.trim() : "unknown-linux";
    }
}