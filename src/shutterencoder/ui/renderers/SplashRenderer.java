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
import java.awt.SplashScreen;

import shutterencoder.utils.Utils;

public class SplashRenderer {

	static float progress = 0.0f;
	
	public static void render(String version) {
		
	    SplashScreen splash = SplashScreen.getSplashScreen();
	    if (splash == null) return;

	    Graphics2D g = splash.createGraphics();
	    if (g == null) return;

	    int width = splash.getSize().width;
	    int height = splash.getSize().height;

	    Font font = new Font("Montserrat", Font.PLAIN, 12);
	    g.setFont(font);

	    FontMetrics fm = g.getFontMetrics();

	    // Info text
	    String versionText = version;
	    String years = "2013–2026";
	    int xVersion = width - fm.stringWidth(versionText) - 15;
	    int yVersion = height - 15;
	    
	    // Progress bar layout
	    int barWidth = 200;
	    int barHeight = 4;
	    int barArc = 4;
	    int xBar = (width - barWidth) / 2;
	    int yBar = height - 55;
	    
	    // Initiliazing text
	    String initText = "Initializing...";
	    int xInit = (width - fm.stringWidth(initText)) / 2;
	    int yInit = yBar - 10;

	    // Fade over ~300ms
	    int frames = 15;
	    int delay = 20; // ms

	    for (int i = 0; i <= frames; i++) {

	        float alpha = i / (float) frames;

	        // Clear previous frame (IMPORTANT)
	        g.setComposite(AlphaComposite.Clear);
	        g.fillRect(0, 0, width, height);

	        // Restore normal drawing
	        g.setComposite(AlphaComposite.SrcOver.derive(alpha));

	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

	        g.setColor(Utils.c225);

	        // Draw text
	        g.drawString(versionText, xVersion, yVersion);
	        g.drawString(years, 15, yVersion);
	        
	        g.setColor(Utils.c120);

	        // Draw initializing
	        g.drawString(initText, xInit, yInit);
	        
	        // Progressbar background
	        g.setColor(Utils.c42);
	        g.fillRoundRect(xBar, yBar, barWidth, barHeight, barArc, barArc);

	        // Alpha
	        g.setComposite(AlphaComposite.SrcOver.derive(alpha));
	        
	        splash.update();

	        try {
	            Thread.sleep(delay);
	        } catch (InterruptedException ignored) {}
	    }

	    // Progression
	    while (splash.isVisible())
	    {
	    	g.setComposite(AlphaComposite.Clear);
            g.fillRect(xBar - 2, yBar - 2, barWidth + 4, barHeight + 4);

            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(Utils.c42);
            g.fillRoundRect(xBar, yBar, barWidth, barHeight, barArc, barArc);

            int progressWidth = (int) (barWidth * progress);
            g.setColor(Utils.themeColor);
            g.fillRoundRect(xBar, yBar, progressWidth, barHeight, barArc, barArc);

            splash.update();

            //Slow down the loop
            try {
            	Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
        }
	    
        g.dispose();
	}

	public static void increaseProgress() {
		 progress = Math.min(1.0f, progress + 0.037f);
	}
}