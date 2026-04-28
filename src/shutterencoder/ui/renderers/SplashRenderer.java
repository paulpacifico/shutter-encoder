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

	    String versionText = version;
	    String years = "2013–2026";

	    int xVersion = width - fm.stringWidth(versionText) - 15;
	    int y = height - 15;

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

	        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	        g.setColor(Utils.c225);

	        // Draw text
	        g.drawString(versionText, xVersion, y);
	        g.drawString(years, 15, y);

	        splash.update();

	        try {
	            Thread.sleep(delay);
	        } catch (InterruptedException ignored) {}
	    }
	    
	    g.dispose();
	}
}