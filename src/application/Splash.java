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
import java.awt.*;

@SuppressWarnings("serial")
public class Splash extends Frame {

	public static Graphics2D g;
	public final static SplashScreen splash = SplashScreen.getSplashScreen();
	private static int Value = 0;
	
	static void renderSplashFrame(Graphics2D g, int progress) {
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0,128,260,40);
        g.setPaintMode();
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRoundRect(26, 128, 200, 10, 10, 10);
        g.fillRoundRect(26, 128, progress, 10, 10, 10);
    }
    
    public static void increment(){    
		if (Splash.splash.isVisible())
		{
    		Value += 8;  
			if (Value > 200) 
				Value = 200;
			renderSplashFrame(g, Value);
			splash.update();     
		}
    }
}