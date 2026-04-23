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

package renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import application.Utils;

//Edit panel UI
@SuppressWarnings("serial")
public class CollapsiblePanel extends JPanel {

 private final String title;
 private final boolean displayIcon;
 private final int CORNER_RADIUS = 12;

 public CollapsiblePanel(String title, boolean displayIcon) {
     this.title = title + " ";
     this.displayIcon = displayIcon;
     setOpaque(false);
     setLayout(null);
 }

 @Override
 protected void paintComponent(Graphics g) {
 	
     super.paintComponent(g);

     Graphics2D g2 = (Graphics2D) g.create();
     
     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
     g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

     int y = 12;
     int height = 16;
     int textY = 9;
     int iconY = 7;
     if (this.getHeight() <= 17)
     {
         y = 0;
         height = 0;
         textY = 13;
         iconY = 10;
     }
     
     // Background   
 	if (this.getHeight() <= 17)
 	{
 		g2.setPaint(new GradientPaint(2, 0, Utils.c42,getWidth() - 4, 0, new Color(Utils.c42.getRed(), Utils.c42.getGreen(), Utils.c42.getBlue(), 80)));
 	}
 	else
 		g2.setColor(Utils.c25);        

     g2.fillRoundRect(2, y, getWidth() - 4, getHeight() - height, CORNER_RADIUS, CORNER_RADIUS);

     // Border
     if (this.getHeight() <= 17)
 	{
     	g2.setPaint(new GradientPaint(2, 0, Utils.c50,getWidth() - 4, 0, new Color(Utils.c50.getRed(), Utils.c50.getGreen(), Utils.c50.getBlue(), 120)));
 	}
 	else
 		g2.setColor(Utils.c42);    
             
     g2.drawRoundRect(2, y, getWidth() - 4, getHeight() - height, CORNER_RADIUS, CORNER_RADIUS);

     // Title
     g2.setColor(new Color(235, 235, 240));
     g2.setFont(new Font(Utils.boldFont, Font.PLAIN, 13));
     g2.drawString(title, 7, textY);
     
     // Chevron icon
     if (displayIcon)
     {
        int iconX = getWidth() - 18;
        
        g2.setStroke(new BasicStroke(2.0f));
        
        if (this.getHeight() <= 17) {
            g2.drawLine(iconX, iconY - 4, iconX + 4, iconY);
            g2.drawLine(iconX + 4, iconY, iconX + 8, iconY - 4);
        } else {
            g2.drawLine(iconX, iconY, iconX + 4, iconY - 4);
            g2.drawLine(iconX + 4, iconY - 4, iconX + 8, iconY);
        }

     }
     g2.dispose();
 }
}
