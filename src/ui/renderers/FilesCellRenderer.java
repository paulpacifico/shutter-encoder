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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.others.Settings;
import shutterencoder.utils.Utils;

//Editing file list
@SuppressWarnings({ "serial", "rawtypes" })
public class FilesCellRenderer extends JLabel implements ListCellRenderer {

	 public FilesCellRenderer() {
	     setOpaque(false);
	 }
	
	 public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
		        boolean cellHasFocus) {
	
		    if (Settings.btnHidePath.isSelected() && Shutter.scanIsRunning == false)
		    {
		        setText(new File(value.toString()).getName());
		    } else {
		        setText(value.toString());
		    }
	
		    // Determine icon based on file extension
		    String fileName = value.toString().toLowerCase();
		    String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1) : "";
	
		    int iconSize = 14;
		    
		    switch (extension) {
		        case "jpg": case "jpeg": case "png": case "gif": case "bmp":
		        case "tiff": case "tif": case "webp": case "svg": case "ico":
		        case "heic": case "heif": case "raw": case "cr2": case "nef":
		        case "arw": case "dng": case "orf": case "psd": case "dpx":
		        case "exr": case "tga":
		            setIcon(new FlatSVGIcon("resources/image.svg", iconSize, iconSize));
		            break;
	
		        case "mp4": case "mkv": case "avi": case "mov": case "wmv":
		        case "flv": case "webm": case "mpeg": case "mpg": case "m4v":
		        case "3gp": case "ts": case "mts": case "m2ts": case "vob":
		        case "ogv": case "rm": case "rmvb": case "divx": case "xvid":
		        case "mxf": case "f4v": case "asf":
		            setIcon(new FlatSVGIcon("resources/video.svg", iconSize, iconSize));
		            break;
	
		        case "mp3": case "wav": case "aac": case "flac": case "ogg":
		        case "wma": case "m4a": case "opus": case "aiff": case "aif":
		        case "alac": case "ape": case "mid": case "midi": case "ra":
		        case "ac3": case "dts": case "amr": case "au":
		            setIcon(new FlatSVGIcon("resources/audio.svg", iconSize, iconSize));
		            break;
	
		        default:
		            setIcon(new FlatSVGIcon("resources/file.svg", iconSize, iconSize));
		            break;
		    }
	
		    setToolTipText(value.toString());
		    setFont(new Font("SansSerif", Font.PLAIN, 12));
	
		    if (isSelected)
		    {
		        setBackground(new Color(75, 75, 80));
		        setForeground(Utils.themeColor);
		    }
		    else
		    {
		        if (index % 2 == 1)
		        {
		            setBackground(Utils.c35);
		        }
		        else
		            setBackground(new Color(Utils.c35.getRed() + 9, Utils.c35.getGreen() + 9, Utils.c35.getBlue() + 9));
	
		        setForeground(Color.LIGHT_GRAY);
		    }
	
		    return this;
	 }

	 @Override
	 protected void paintComponent(Graphics g) {
	     Graphics2D g2 = (Graphics2D) g.create();
	     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	     
	     g2.setColor(getBackground());
	     g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
	     
	     g2.dispose();
	
	     super.paintComponent(g);
	 }
}