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

package shutterencoder.ui.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import shutterencoder.ui.handlers.ListFileTransferHandler;
import shutterencoder.ui.renderers.CollapsiblePanel;
import shutterencoder.utils.Utils;

public class FileInformation extends Main {

	public static void grpFileInformation() {
		
		grpFileInformation = new CollapsiblePanel(language.getProperty("frameInformations"), true);
		grpFileInformation.setName("grpFileInformation");
		grpFileInformation.setLayout(null);
		grpFileInformation.setVisible(true);
		grpFileInformation.setOpaque(false);
		grpFileInformation.setBackground(Utils.c30);
		grpFileInformation.setBounds(frame.getWidth() - 312 - 12, grpChooseFiles.getY(), 312, frame.getHeight() - grpChooseFiles.getY() - statusBar.getHeight() - 10);
		frame.getContentPane().add(grpFileInformation);

		grpFileInformation.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) 
			{				
				UIController.extendSections(grpFileInformation, frame.getHeight() - grpChooseFiles.getY() - statusBar.getHeight() - 10);
			}

		});
		
		grpFileInformation.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent arg0) {
				settingsScrollBar.setVisible(false);				
			}
			
		});

		grpFileInformation.setTransferHandler(new ListFileTransferHandler());
		
		labelFileInfo = new JLabel(language.getProperty("noFileInList"));
		labelFileInfo.setHorizontalAlignment(SwingConstants.CENTER);
		labelFileInfo.setForeground(Color.DARK_GRAY);
		labelFileInfo.setFont(new Font("SansSerif", Font.BOLD, 17));
		labelFileInfo.setVisible(true);
		labelFileInfo.setBounds(0, (grpFileInformation.getHeight() - labelFileInfo.getPreferredSize().height) / 2, grpFileInformation.getWidth(), labelFileInfo.getPreferredSize().height);
		
		grpFileInformation.add(labelFileInfo);
	}
}
