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

package shutterencoder.ui.handlers;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.swing.JPanel;

import shutterencoder.functions.Transcribe;
import shutterencoder.functions.settings.FunctionUtils;
import shutterencoder.library.FFPROBE;
import shutterencoder.ui.main.Shutter;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.others.Renamer;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUI;
import shutterencoder.utils.Utils;

public class HardwareListener extends Shutter {

	// Keyboard shortcuts
	public static void keyboardListener() {
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {

				if (comboFonctions.getSelectedItem() != "" && comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false) {
					
					KeyEvent ke = (KeyEvent) event;

					if (ke.getID() == KeyEvent.KEY_PRESSED) {
						// Use ESCAPE only for FFPLAY
						if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
							frame.requestFocus();
						}

						if (VideoPlayerUI.fullscreenPlayer)
						{
							if (ke.getKeyCode() == KeyEvent.VK_K || ke.getKeyCode() == KeyEvent.VK_SPACE) {
								ke.consume();
								VideoPlayerUI.btnPlay.doClick();
							}

							if (ke.getKeyCode() == KeyEvent.VK_J) {
								VideoPlayerUI.previousFrame = true;
								VideoPlayerCore.playerSetTime((float) (VideoPlayerCore.playerCurrentFrame - 10));
							}

							if (ke.getKeyCode() == KeyEvent.VK_L) {
								VideoPlayerUI.previousFrame = true;
								VideoPlayerCore.playerSetTime((float) (VideoPlayerCore.playerCurrentFrame + 10));
							}

							if (ke.getID() == KeyEvent.KEY_PRESSED) {
								if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
									shift = true;
							}

							if (ke.getKeyCode() == KeyEvent.VK_I) {
								if (shift) {
									VideoPlayerUI.btnGoToIn.doClick();
								} else {
									VideoPlayerUI.btnMarkIn.doClick();
								}
							}

							if (ke.getKeyCode() == KeyEvent.VK_O) {
								if (shift) {
									VideoPlayerUI.btnGoToOut.doClick();
								} else {
									VideoPlayerUI.btnMarkOut.doClick();
								}
							}
							
							if (ke.getKeyCode() == KeyEvent.VK_HOME)
							{
								ke.consume();
								VideoPlayerCore.playerSetTime(0);
							}
								
							if (ke.getKeyCode() == KeyEvent.VK_END)
							{
								ke.consume();
								VideoPlayerCore.playerSetTime((double) VideoPlayerUI.totalFrames - 2);
							}
							
							if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP)
							{								
								VideoPlayerUI.btnGoToIn.doClick();
							}
							
							if (ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN )
							{
								VideoPlayerUI.btnGoToOut.doClick();
							}
							
							if (ke.getKeyCode() == KeyEvent.VK_LEFT)
							{					
								if (Shutter.shift)
								{
									VideoPlayerUI.previousFrame = true;
									VideoPlayerCore.playerSetTime((double) VideoPlayerCore.playerCurrentFrame - Math.ceil(FFPROBE.accurateFPS));
								}
								else if (Shutter.alt)
								{
									VideoPlayerUI.previousFrame = true;
									VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame - Math.ceil(FFPROBE.accurateFPS) * 10));
								}
								else
								{
									VideoPlayerUI.btnPrevious.doClick();
								}
			  				}
							
							if (ke.getKeyCode() == KeyEvent.VK_RIGHT)
							{						
								if (Shutter.shift)
								{
									VideoPlayerUI.previousFrame = true;
									VideoPlayerCore.playerSetTime((double) VideoPlayerCore.playerCurrentFrame + Math.ceil(FFPROBE.accurateFPS));
								}
								else if (Shutter.alt)
								{
									VideoPlayerUI.previousFrame = true;
									VideoPlayerCore.playerSetTime((double) (VideoPlayerCore.playerCurrentFrame + Math.ceil(FFPROBE.accurateFPS) * 10));
								}
								else
								{
									VideoPlayerUI.btnNext.doClick();
								}
			  				}

							if (ke.getKeyCode() == KeyEvent.VK_UP)
								VideoPlayerUI.btnGoToOut.doClick();

							if (ke.getKeyCode() == KeyEvent.VK_DOWN)
								VideoPlayerUI.btnGoToIn.doClick();

							// Volume up
							if (ke.getKeyCode() == 107
									&& VideoPlayerUI.sliderVolume.getValue() < VideoPlayerUI.sliderVolume.getMaximum()) {
								VideoPlayerUI.sliderVolume.setValue(VideoPlayerUI.sliderVolume.getValue() + 10);
							}

							// Volume down
							if (ke.getKeyCode() == 109 && VideoPlayerUI.sliderVolume.getValue() > 0) {
								VideoPlayerUI.sliderVolume.setValue(VideoPlayerUI.sliderVolume.getValue() - 10);
							}
							if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)
							{	
								VideoPlayerUI.toggleFullscreen();
							}
						}

						// CMD + Q
						if (System.getProperty("os.name").contains("Mac") && (ke.getKeyCode() == KeyEvent.VK_Q)
								&& ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)) {
							Runtime.getRuntime().addShutdownHook(new Thread() {
								@Override
								public void run() {
									Settings.saveSettings();

									Utils.killProcesses();
									
									//Removing temporary files
									if (Transcribe.transcriptionFolder != null && Transcribe.transcriptionFolder.exists())
									{					
										for (File f : Transcribe.transcriptionFolder.listFiles()) 
										{
											f.delete();
										}
										
										Transcribe.transcriptionFolder.delete();
									}

									if (FunctionUtils.deleteSRT && subtitlesFilePath != null)
									{
										subtitlesFilePath.delete();
									}
									
									if (VideoPlayerCore.waveform != null)
									{
										VideoPlayerCore.waveform = null;
									}
								}
							});
						}

						// Save settings
						if ((ke.getKeyCode() == KeyEvent.VK_S) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
								|| (ke.getKeyCode() == KeyEvent.VK_S)
										&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
							if ((btnStart.getText().equals(Shutter.language.getProperty("btnStartFunction"))
									|| btnStart.getText().equals(Shutter.language.getProperty("btnAddToRender")))
									&& comboFonctions.getSelectedItem() != "") {
								if (Renamer.frame == null
										|| Renamer.frame != null && Renamer.frame.isVisible() == false) {
									Utils.saveSettings(false);
								}
							}
						}

						// btnStart
						if ((ke.getKeyCode() == KeyEvent.VK_ENTER)
								&& ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
								|| (ke.getKeyCode() == KeyEvent.VK_ENTER)
										&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
							btnStart.doClick();
						}

						// Informations
						if ((ke.getKeyCode() == KeyEvent.VK_I) && ((ke.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0)
								|| (ke.getKeyCode() == KeyEvent.VK_I)
										&& ((ke.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
							if (fileList.getSelectedIndices().length > 0) {
								informations.doClick();
							}
						}

					} else if (ke.getID() == KeyEvent.KEY_RELEASED) {
						if (ke.getKeyCode() == KeyEvent.VK_SHIFT)
							shift = false;
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
	}
	
	// Mouse wheel for panels
	public static void mouseListener() {
				
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			public void eventDispatched(AWTEvent event) {

				// Mouse position
				MouseWheelEvent me = (MouseWheelEvent) event;
		
				// On récupère le groupe qui est le plus haut
				JPanel top;

				if (grpResolution.isVisible()) {
					top = grpResolution;
				} else if (grpSetTimecode.isVisible()) {
					top = grpSetTimecode;
				} else if (grpSetAudio.isVisible()) {
					top = grpSetAudio;
				} else {
					top = grpAudio;
				}
				
				if (me.getX() < top.getX()
				|| VideoPlayerCore.waveformContainerHasMouse 
				|| UIController.extendSectionsIsRunning)
				{
					return;
				}
				
				if (comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
				&& frame.getWidth() > 332
				&& frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()) <= 31
				|| comboFonctions.getSelectedItem().equals(Shutter.language.getProperty("functionSubtitles")) == false
				&& frame.getWidth() > 332
				&& Settings.btnDisableAnimations.isSelected() && top.getY() < 30)
				{	
					int i = (0 - me.getWheelRotation()) * 20;

					// Empêche de faire un scroll vers le bas pour ne pas dépasser la position
					// minimale de top
					if (i < 0 && Settings.btnDisableAnimations.isSelected() && frame.getSize().getHeight()
							- (btnReset.getLocation().y + btnReset.getHeight()) >= 31) {
						i = 0;
					}

					// Pré calcul
					if (top.getY() + i >= grpChooseFiles.getY() && i > 0) {
						i = grpChooseFiles.getY() - top.getY();
					}

					if (frame.getSize().getHeight() - (btnReset.getLocation().y + i + btnReset.getHeight()) >= 31
							&& i < 0) {
						if (i < frame.getSize().getHeight() - (btnReset.getLocation().y + btnReset.getHeight()))
							i = (int) (frame.getSize().getHeight()
									- (btnReset.getLocation().y + btnReset.getHeight()) - 31);
						else
							i = 0;
					}

					grpResolution.setLocation(grpResolution.getLocation().x, grpResolution.getLocation().y + i);
					grpBitrate.setLocation(grpBitrate.getLocation().x, grpBitrate.getLocation().y + i);
					grpSetAudio.setLocation(grpSetAudio.getLocation().x, grpSetAudio.getLocation().y + i);
					grpAudio.setLocation(grpAudio.getLocation().x, grpAudio.getLocation().y + i);
					grpCrop.setLocation(grpCrop.getLocation().x, grpCrop.getLocation().y + i);
					grpOverlay.setLocation(grpOverlay.getLocation().x, grpOverlay.getLocation().y + i);
					grpSubtitles.setLocation(grpSubtitles.getLocation().x, grpSubtitles.getLocation().y + i);
					grpWatermark.setLocation(grpWatermark.getLocation().x, grpWatermark.getLocation().y + i);
					grpColorimetry.setLocation(grpColorimetry.getLocation().x, grpColorimetry.getLocation().y + i);
					grpImageAdjustement.setLocation(grpImageAdjustement.getLocation().x, grpImageAdjustement.getLocation().y + i);
					grpCorrections.setLocation(grpCorrections.getLocation().x, grpCorrections.getLocation().y + i);
					grpTransitions.setLocation(grpTransitions.getLocation().x, grpTransitions.getLocation().y + i);
					grpImageSequence.setLocation(grpImageSequence.getLocation().x, grpImageSequence.getLocation().y + i);
					grpImageFilter.setLocation(grpImageFilter.getLocation().x, grpImageFilter.getLocation().y + i);
					grpSetTimecode.setLocation(grpSetTimecode.getLocation().x, grpSetTimecode.getLocation().y + i);
					grpAdvanced.setLocation(grpAdvanced.getLocation().x, grpAdvanced.getLocation().y + i);
					btnReset.setLocation(btnReset.getLocation().x, btnReset.getLocation().y + i);
				}
			}
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);

	}
}
