/*******************************************************************************************
* Copyright (C) 2023 PACIFICO PAUL
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

package library;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import application.Shutter;
import application.VideoPlayer;

public class PDF extends Shutter {
	
public static boolean error = false;
public static boolean isRunning = false;
public static Thread runProcess;
public static int pagesCount = 1;

	public static void run(File file, int pageNumber) {
					
		error = false;
		progressBar1.setValue(0);	
		
		disableAll();
		btnStart.setEnabled(false);
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {
					
					isRunning = true;
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					PDDocument document = PDDocument.load(file);
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					
					pagesCount = document.getNumberOfPages();	
					
					BufferedImage image = pdfRenderer.renderImageWithDPI(pageNumber, 300, ImageType.RGB);
					ImageIOUtil.writeImage(image, VideoPlayer.preview.toString(), 300);					
															
					document.close();
				
				} catch (Exception e) {
					error = true;
				} finally {
					isRunning = false;
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}				
		});		
		runProcess.start();	
	
	}
	
	public static void info(String file) {
		
		error = false;	
		
		FFPROBE.hasAudio = false;
		FFPROBE.totalLength = 0;
		
		runProcess = new Thread(new Runnable()  {
			
			@Override
			public void run() {
				
				try {
					
					isRunning = true;
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					PDDocument document = PDDocument.load(new File(file));
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					
					pagesCount = document.getNumberOfPages();
					
					BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

		            FFPROBE.imageResolution = image.getWidth() + "x" + image.getHeight();
		        	FFPROBE.imageWidth = image.getWidth();
		        	FFPROBE.imageHeight = image.getHeight();
		        	FFPROBE.imageRatio = (float) image.getWidth() / image.getHeight();
					
					document.close();
				
				} catch (Exception e) {
					error = true;
				} finally {
					isRunning = false;
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}				
		});		
		runProcess.start();	

	}
	
}