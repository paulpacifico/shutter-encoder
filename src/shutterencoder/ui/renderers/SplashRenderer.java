/*******************************************************************************************
* Copyright (C) 2027 PACIFICO PAUL
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
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.Timer;

import shutterencoder.ui.main.Main;
import shutterencoder.utils.Utils;

@SuppressWarnings("serial")
public class SplashRenderer extends JWindow {

	public static SplashRenderer instance;
	private BufferedImage bufferImage;
	public static volatile float progress = 0.0f;
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
	    initializeBuffer();
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
	
	private void initializeBuffer() {
	    int width = Math.max(1, getWidth());
	    int height = Math.max(1, getHeight());
	
	    bufferImage = new BufferedImage(
	        width,
	        height,
	        BufferedImage.TYPE_INT_ARGB
	    );
	}

	@Override
	public void paint(Graphics g) {
	
	    // Recreate the buffer if necessary
	    if (bufferImage == null ||
	        bufferImage.getWidth() != getWidth() ||
	        bufferImage.getHeight() != getHeight()) {
	
	        initializeBuffer();
	    }
	
	    Graphics2D bufferGraphics = bufferImage.createGraphics();
	
	    try {
	        // Rendering hints
	        bufferGraphics.setRenderingHint(
	            RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON
	        );
	
	        bufferGraphics.setRenderingHint(
	            RenderingHints.KEY_TEXT_ANTIALIASING,
	            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
	        );
	
	        bufferGraphics.setRenderingHint(
	            RenderingHints.KEY_RENDERING,
	            RenderingHints.VALUE_RENDER_QUALITY
	        );
	
	        // Clear buffer
	        bufferGraphics.setComposite(AlphaComposite.Clear);
	        bufferGraphics.fillRect(
	            0, 0, getWidth(), getHeight()
	        );
	
	        // Draw splash image
	        bufferGraphics.setComposite(AlphaComposite.SrcOver);
	        bufferGraphics.drawImage(splashScreen, 0, 0, this);
	
	        // Setup text
	        bufferGraphics.setFont(font);
	        FontMetrics fm = bufferGraphics.getFontMetrics();
	
	        String versionText = "v" + Main.actualVersion;
	        String years = "2013–2027";
	
	        int xVersion = getWidth() - fm.stringWidth(versionText) - 15;
	        int yVersion = getHeight() - 15;
	
	        // Fade-in text
	        bufferGraphics.setComposite(
	            AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER,
	                textAlpha
	            )
	        );
	
	        bufferGraphics.setColor(Utils.c225);
	        bufferGraphics.drawString(versionText, xVersion, yVersion);
	        bufferGraphics.drawString(years, 15, yVersion);
	
	        // Progress bar
	        bufferGraphics.setComposite(AlphaComposite.SrcOver);
	
	        int barWidth = 200;
	        int barHeight = 4;
	        int xBar = (getWidth() - barWidth) / 2;
	        int yBar = getHeight() - 55;
	
	        // Initializing text
	        bufferGraphics.setColor(Utils.c120);
	
	        String initText = "Initializing...";
	        int xInit = (getWidth() - fm.stringWidth(initText)) / 2;
	        int yInit = yBar - 10;
	
	        bufferGraphics.drawString(initText, xInit, yInit);
	
	        // Progress background
	        bufferGraphics.setColor(Utils.c42);
	        bufferGraphics.fillRoundRect(
	            xBar, yBar, barWidth, barHeight, 4, 4
	        );
	
	        // Progression
	        int progressWidth = (int) (barWidth * progress);
	
	        if (progressWidth > 0) {
	            bufferGraphics.setColor(Utils.themeColor);
	            bufferGraphics.fillRoundRect(
	                xBar, yBar, progressWidth, barHeight, 4, 4
	            );
	        }
	
	    } finally {
	        bufferGraphics.dispose();
	    }
	
	    // Draw completed buffer
	    Graphics2D screenGraphics = (Graphics2D) g.create();
	
	    try {
	        screenGraphics.drawImage(bufferImage, 0, 0, this);
	    } finally {
	        screenGraphics.dispose();
	    }
	}
      
	public static void increment() {
	    progress = Math.min(1.0f, progress + 0.11111f);
	    
	    SplashRenderer splash = instance;

	    if (splash != null) {
	        splash.repaint();
	    }
	}
}