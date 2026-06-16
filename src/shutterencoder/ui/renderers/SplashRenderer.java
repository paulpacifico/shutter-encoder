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

package shutterencoder.ui.renderers;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.Timer;

import shutterencoder.ui.main.Shutter;
import shutterencoder.utils.Utils;

@SuppressWarnings("serial")
public class SplashRenderer extends JWindow {

	public static SplashRenderer instance;
	private Image bufferImage;
	private Graphics2D bufferGraphics;
	public static float progress = 0.0f;
	private float textAlpha = 0.0f;
	private Timer fadeTimer;
	private static Image splashScreen;
	private static Font font = new Font("Montserrat", Font.PLAIN, 12);
	
	public SplashRenderer() {		
	    instance = this;
	    splashScreen = new ImageIcon(getClass().getClassLoader().getResource("resources/SplashScreen.png")).getImage();
	    
	    setBackground(new Color(0,0,0,0));		
	    setSize(640, 368);
	    setLocationRelativeTo(null);
	    setVisible(true);

	    // Start the fade-in timer
	    fadeTimer = new javax.swing.Timer(16, e -> {
	        textAlpha += 0.05f;
	        if (textAlpha >= 1.0f) {
	            textAlpha = 1.0f;
	            ((javax.swing.Timer)e.getSource()).stop();
	        }
	        repaint();
	    });
	    fadeTimer.start();
	}

	public void paint(Graphics g) {
		
		// Create a BufferedImage with Alpha support if it doesn't exist
	    if (bufferImage == null || bufferImage.getWidth(this) != getWidth() || bufferImage.getHeight(this) != getHeight()) {
	        bufferImage = new java.awt.image.BufferedImage(getWidth(), getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
	        bufferGraphics = (Graphics2D) bufferImage.getGraphics();
	    }
	    
	    // Set renderingHint
	    try { //Might fail and block the startup
		    bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    bufferGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		    bufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    } catch (Exception e) {};
	    
	    // Clear the buffer with transparency
	    bufferGraphics.setComposite(AlphaComposite.Clear);
	    bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
	    
	    // Reset to SRC_OVER to start drawing
	    bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	    bufferGraphics.drawImage(splashScreen, 0, 0, this);

	    // Setup Text
	    bufferGraphics.setFont(font);
	    FontMetrics fm = bufferGraphics.getFontMetrics();
	    String versionText = "v" + Shutter.actualVersion;
	    String years = "2013–2026";
	    int xVersion = getWidth() - fm.stringWidth(versionText) - 15;
	    int yVersion = getHeight() - 15;

	    // Apply the independent Fade to text only
	    bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha));
	    bufferGraphics.setColor(Utils.c225);
	    bufferGraphics.drawString(versionText, xVersion, yVersion);
	    bufferGraphics.drawString(years, 15, yVersion);

	    // Reset Composite for Progress Bar
	    bufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	    	    
	    int barWidth = 200;
	    int barHeight = 4;
	    int xBar = (getWidth() - barWidth) / 2;
	    int yBar = getHeight() - 55;
	    
	    // Draw initializing text
	    bufferGraphics.setColor(Utils.c120);
	    String initText = "Initializing...";
	    int xInit = (getWidth() - fm.stringWidth(initText)) / 2;
	    int yInit = yBar - 10;
	    bufferGraphics.drawString(initText, xInit, yInit);

	    // Draw Bar Background
	    bufferGraphics.setColor(Utils.c42);
	    bufferGraphics.fillRoundRect(xBar, yBar, barWidth, barHeight, 4, 4);

	    // Draw Progression
	    int progressWidth = (int) (barWidth * progress);
	    if (progressWidth > 0) {
	        bufferGraphics.setColor(Utils.themeColor);
	        bufferGraphics.fillRoundRect(xBar, yBar, progressWidth, barHeight, 4, 4);
	    }

	    // Draw the completed buffer to the screen in one go
	    g.drawImage(bufferImage, 0, 0, this);
	}
      
	public static void increment() {		
		progress = Math.min(1.0f, progress + 0.037f);
		instance.repaint();
	}
}