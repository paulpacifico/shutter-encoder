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

package application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class Splash extends JWindow {

	public static Graphics g;
	private static int loading = 0;	
	Image splashScreen;
	
	public Splash() {
		
		splashScreen = new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage();
		
		setBackground(new Color(0,0,0,0));
		
		setSize(256, 256);

		setLocationRelativeTo(null);

      	setVisible(true);          	
      	
      	if (System.getProperty("os.name").contains("Windows"))
      		updateProgressBar();
   }

	public void paint(Graphics g) {
      super.paint(g);

	  RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
	  qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	  ((Graphics2D) g).setRenderingHints(qualityHints);
      
      g.drawImage(splashScreen, 0, 0, this);  
      g.setColor(Color.WHITE);
      g.drawRoundRect(26, 128, 200, 10, 10, 10);
      g.fillRoundRect(26, 128, loading, 10, 10, 10);
      
      if (System.getProperty("os.name").contains("Windows") == false)
      {
    	  do {
    		  g.fillRoundRect(26, 128, loading, 10, 10, 10); 
    	  } while (Shutter.frame.isVisible() == false);
    		  
		  dispose();
      }
   }
      
	public static void increment() {   
    	loading += 7;  
		if (loading > 200) 
			loading = 200;	
   }
    
	public void updateProgressBar() {
    	
    	Thread refresh = new Thread(new Runnable() {

			@Override
			public void run() {
		    	do {
		    		repaint();	
		    		try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
		    	} while (loading < 200);
		    	
		    	dispose();
		    }
    		
    	});
    	refresh.start();
    }
}