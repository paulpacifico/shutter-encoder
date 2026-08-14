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

package shutterencoder.ui.videoplayer;

import java.util.ArrayList;
import java.util.List;

import shutterencoder.functions.settings.Timecode;
import shutterencoder.ui.main.Shutter;

public class VideoPlayerMultiCuts extends VideoPlayerCore {

	//History stacks for Undo / Redo
	private static final java.util.ArrayDeque<List<CutSegment>> undoStack = new java.util.ArrayDeque<>();
	private static final java.util.ArrayDeque<List<CutSegment>> redoStack = new java.util.ArrayDeque<>();
	private static final int MAX_UNDO_STEPS = 20;
	
	//List of cuts
	public static class CutSegment {
		public int index = -1;
	    public int inMark;
	    public int outMark;
	    public int inH;
	    public int inM;
	    public int inS;
	    public int inF;
	    public int outH;
	    public int outM;
	    public int outS;
	    public int outF;
	   
	    public CutSegment(int index, int inMark, int outMark, int inH, int inM, int inS, int inF, int outH, int outM, int outS, int outF) {
	    	this.index = index;
	    	this.inMark = inMark;
	        this.outMark = outMark;
	        this.inH = inH;
	        this.inM = inM;
	        this.inS = inS;
	        this.inF = inF;
	        this.outH = outH;
	        this.outM = outM;
	        this.outS = outS;
	        this.outF = outF;	        
	    }
	}

	public static List<CutSegment> cutSegments = new ArrayList<>();
				
	public static void editCurrentSegment() {	
			
		if (cutSegments.isEmpty() || activeSegmentIndex != - 1)
		{			
			if (cutSegments.isEmpty() == false)
			{
				saveCutState();
			}
			
			caseApplyCutToAll.setEnabled(false);
			
			double time = bufferCurrentFrame > 0 ? bufferCurrentFrame : playerCurrentFrame;
			
			double inputMark = getSegmentTime(Integer.parseInt(caseInH.getText()), Integer.parseInt(caseInM.getText()), Integer.parseInt(caseInS.getText()), Integer.parseInt(caseInF.getText()));
	        double outputMark = getSegmentTime(Integer.parseInt(caseOutH.getText()), Integer.parseInt(caseOutM.getText()), Integer.parseInt(caseOutS.getText()), Integer.parseInt(caseOutF.getText()));
			
	        boolean newSegment = false;
	        
	        if (time < outputMark - 1 && time > inputMark && VideoPlayerUI.btnEdit.getName().equals("cut")) //Do not do anything if the cursor is in the same place as marker In or Out
		    {	        	
				if (activeSegmentIndex != -1)
				{
					//Increment segments after activeSegmentIndex
			        for (int i = activeSegmentIndex + 1 ; i < cutSegments.size() ; i++)
			        {
			        	cutSegments.get(i).index += 1;
			        }
	
					cutSegments.remove(activeSegmentIndex);				
					newSegment = true;			
				}
				
				//Saving mark out
				String outH = caseOutH.getText();
				String outM = caseOutM.getText();
				String outS = caseOutS.getText();
				String outF = caseOutF.getText();
				
				if (bufferCurrentFrame > 0)
				{
					VideoPlayerUtils.updateGrpOut(bufferCurrentFrame);		
				}
				else
					VideoPlayerUtils.updateGrpOut(playerCurrentFrame);	
				
				int index = newSegment ? activeSegmentIndex : 0;
				
				cutSegments.add(new CutSegment(index, playerMarkIn, cursorCurrentFrame.getX(),
		        		Integer.parseInt(caseInH.getText()),
		        		Integer.parseInt(caseInM.getText()),
		        		Integer.parseInt(caseInS.getText()),
		        		Integer.parseInt(caseInF.getText()),
		        		Integer.parseInt(caseOutH.getText()),
		        		Integer.parseInt(caseOutM.getText()),
		        		Integer.parseInt(caseOutS.getText()),
		        		Integer.parseInt(caseOutF.getText())));
				
				//Set saved mark out for the next segment
				caseOutH.setText(outH);
				caseOutM.setText(outM);
				caseOutS.setText(outS);
				caseOutF.setText(outF);
					
				if (bufferCurrentFrame > 0)
				{
					VideoPlayerUtils.updateGrpIn(bufferCurrentFrame);		
				}
				else
					VideoPlayerUtils.updateGrpIn(playerCurrentFrame);
				
				index = newSegment ? activeSegmentIndex + 1 : 1;
	
		        cutSegments.add(new CutSegment(index, cursorCurrentFrame.getX(), playerMarkOut,
		        		Integer.parseInt(caseInH.getText()),
		        		Integer.parseInt(caseInM.getText()),
		        		Integer.parseInt(caseInS.getText()),
		        		Integer.parseInt(caseInF.getText()),
		        		Integer.parseInt(caseOutH.getText()),
		        		Integer.parseInt(caseOutM.getText()),
		        		Integer.parseInt(caseOutS.getText()),
		        		Integer.parseInt(caseOutF.getText())));        
		        
		        if (newSegment)
		        	activeSegmentIndex += 1;
		    }
	        else if (VideoPlayerUI.btnEdit.getName().equals("add")) //Add segment
	        {
	        	//Save the current segment
	        	if (activeSegmentIndex == -1)
				{
	        		newSegment = true;
	        		
	        		int index = cursorCurrentFrame.getX() < inputMark ? 1 : 0;

					cutSegments.add(new CutSegment(index, playerMarkIn, playerMarkOut,
			        		Integer.parseInt(caseInH.getText()),
			        		Integer.parseInt(caseInM.getText()),
			        		Integer.parseInt(caseInS.getText()),
			        		Integer.parseInt(caseInF.getText()),
			        		Integer.parseInt(caseOutH.getText()),
			        		Integer.parseInt(caseOutM.getText()),
			        		Integer.parseInt(caseOutS.getText()),
			        		Integer.parseInt(caseOutF.getText())));
				}
	        	
	        	if (bufferCurrentFrame > 0)
				{
					VideoPlayerUtils.updateGrpIn(bufferCurrentFrame);		
				}
				else
					VideoPlayerUtils.updateGrpIn(playerCurrentFrame);

	        	if (newSegment)
	        	{
	        		int index;
	        		int playerMarkOut;
	        		if (cursorCurrentFrame.getX() < inputMark) //When the current segment is after the cursor
	        		{
	        			index = 0;
	        			VideoPlayerUtils.updateGrpOut(inputMark);
	        			playerMarkOut = playerMarkIn;
	        		}
	        		else //When the current segment is before the cursor
		        	{
	        			index = 1;
	        			VideoPlayerUtils.updateGrpOut(totalFrames);
	        			playerMarkOut = waveformContainer.getWidth();
		        	}
		        	
	        		cutSegments.add(new CutSegment(index, cursorCurrentFrame.getX(), playerMarkOut,
			        		Integer.parseInt(caseInH.getText()),
			        		Integer.parseInt(caseInM.getText()),
			        		Integer.parseInt(caseInS.getText()),
			        		Integer.parseInt(caseInF.getText()),
			        		Integer.parseInt(caseOutH.getText()),
			        		Integer.parseInt(caseOutM.getText()),
			        		Integer.parseInt(caseOutS.getText()),
			        		Integer.parseInt(caseOutF.getText())));   
	        	}
	        	else //New segment is added between existing segments
	        	{
	        		int playerMarkOut;
	        		int outH;
	        		int outM;
	        		int outS;
	        		int outF;

	        		//Fetch the segment after the cursor location
        			for (CutSegment seg : cutSegments)
	        		{
        				outputMark = VideoPlayerMultiCuts.getSegmentTime(seg.outH, seg.outM, seg.outS, seg.outF);
	        			
	        			if (time < outputMark)
	        			{
	        				activeSegmentIndex = seg.index;
	        				inputMark = VideoPlayerMultiCuts.getSegmentTime(seg.inH, seg.inM, seg.inS, seg.inF);
	        				break;
	        			}
	        		}
        			
	        		if (time < inputMark)
	        		{	        	
	        			//Increment segments after activeSegmentIndex
				        for (int i = activeSegmentIndex ; i < cutSegments.size() ; i++)
				        {
				        	cutSegments.get(i).index += 1;
				        }
				        
	        			CutSegment seg = cutSegments.get(activeSegmentIndex);
	        			
	        			playerMarkOut = seg.inMark;	        			
	        			outH = seg.inH;
		        		outM = seg.inM;
		        		outS = seg.inS;
		        		outF = seg.inF;
	        		}
	        		else //When there is no segment after the cursorCurrentFrame.getX()
	        		{
	        			VideoPlayerUtils.updateGrpOut(totalFrames);
	        			
	        			playerMarkOut = waveformContainer.getWidth();	        			
	        			outH = Integer.parseInt(caseOutH.getText());
		        		outM = Integer.parseInt(caseOutM.getText());
		        		outS = Integer.parseInt(caseOutS.getText());
		        		outF = Integer.parseInt(caseOutF.getText());
		        		
		        		activeSegmentIndex += 1;
	        		}
	        		
	        		cutSegments.add(new CutSegment(activeSegmentIndex, cursorCurrentFrame.getX(), playerMarkOut,
			        		Integer.parseInt(caseInH.getText()),
			        		Integer.parseInt(caseInM.getText()),
			        		Integer.parseInt(caseInS.getText()),
			        		Integer.parseInt(caseInF.getText()),
			        		outH,
			        		outM,
			        		outS,
			        		outF));
	        	}
	        }
	        
	        //Sort in correct index position
	        cutSegments.sort((s1, s2) -> Integer.compare(s1.inMark, s2.inMark));
	        
	        //FileList
			VideoPlayerUtils.setFileList();
	        
		    waveformContainer.repaint();
		}
    }
		
	public static void removeCurrentSegment() {
		
		if (cutSegments.isEmpty() == false && activeSegmentIndex != - 1)
		{
			saveCutState();
			
	        for (int i = 0 ; i < cutSegments.size() ; i++)
	        {
	        	if (cutSegments.get(i).index == activeSegmentIndex) //Remove current index
	        	{
	        		cutSegments.remove(i);
	        	}
	        }
			
			//Decrement segments after remove
			for (CutSegment seg : cutSegments)
			{
				if (seg.index > activeSegmentIndex)
	        	{        		
	        		seg.index -= 1;
	        	}
			}
			
			//Set the correct values then clear all
			if (cutSegments.size() == 1)
			{
				CutSegment seg = cutSegments.get(0);
				
				playerMarkIn = seg.inMark;
				playerMarkOut = seg.outMark;
				
				caseInH.setText(Shutter.formatter.format(seg.inH));
            	caseInM.setText(Shutter.formatter.format(seg.inM));
            	caseInS.setText(Shutter.formatter.format(seg.inS));
            	caseInF.setText(Shutter.formatter.format(seg.inF));
            	
            	caseOutH.setText(Shutter.formatter.format(seg.outH));
            	caseOutM.setText(Shutter.formatter.format(seg.outM));
            	caseOutS.setText(Shutter.formatter.format(seg.outS));
            	caseOutF.setText(Shutter.formatter.format(seg.outF));
				
				cutSegments.clear();
				
				caseApplyCutToAll.setEnabled(true);
			}
			
			if (activeSegmentIndex + 1 <= cutSegments.size())
			{
				//Do nothing keep the current index value
			}
			else if (activeSegmentIndex - 1 > 0)
			{
				activeSegmentIndex --;
			}
			else
				activeSegmentIndex = -1;
				
			//FileList
			VideoPlayerUtils.setFileList();
			
			//Display current segment in/out						
			if (cutSegments.isEmpty() == false)
	    	{
	    		setCurrentSegmentValues();
	    	}
			
			waveformContainer.repaint();
		}
	}
	
	public static void setCurrentSegmentValues() {
		
		for (CutSegment seg : cutSegments)
        {
            //Current segment
            if (activeSegmentIndex == seg.index)
            {
            	playerMarkIn = seg.inMark;
            	playerMarkOut = seg.outMark;
            	
            	caseInH.setText(Shutter.formatter.format(seg.inH));
            	caseInM.setText(Shutter.formatter.format(seg.inM));
            	caseInS.setText(Shutter.formatter.format(seg.inS));
            	caseInF.setText(Shutter.formatter.format(seg.inF));
            	
            	caseOutH.setText(Shutter.formatter.format(seg.outH));
            	caseOutM.setText(Shutter.formatter.format(seg.outM));
            	caseOutS.setText(Shutter.formatter.format(seg.outS));
            	caseOutF.setText(Shutter.formatter.format(seg.outF));            	
            }
        }
	}
	
	public static double getSegmentTime(double h, double m, double s, double f) {		
	    double totalFrames = (h * 3600 + m * 60 + s) * VideoPlayerUtils.getFPS() + f;
	    totalFrames = Timecode.getNTSCtimecode(totalFrames);
	    return Timecode.getDropFrameTimecode(totalFrames);
	}
	
	private static List<CutSegment> cloneCutSegments(List<CutSegment> source) {
		
	    List<CutSegment> copy = new ArrayList<>();
	    
	    for (CutSegment seg : source)
	    {
	        copy.add(new CutSegment(seg.index, seg.inMark, seg.outMark,
	            seg.inH, seg.inM, seg.inS, seg.inF,
	            seg.outH, seg.outM, seg.outS, seg.outF
	        ));
	    }
	    
	    return copy;
	}
	
	public static void saveCutState() {
		
	    if (undoStack.size() >= MAX_UNDO_STEPS)
	        undoStack.removeLast();
	    
	    undoStack.push(cloneCutSegments(cutSegments));
	    redoStack.clear();
	}

	public static void clearCutHistory() {
	    undoStack.clear();
	    redoStack.clear();
	}
	
	public static void undoCut() {
		
	    if (undoStack.isEmpty())	    
	    	return;

	    // Save current state to redo stack
	    redoStack.push(cloneCutSegments(cutSegments));

	    // Restore state from undo stack
	    cutSegments = undoStack.pop();
	    	    
	    updateCurrentSegment();
	}

	public static void redoCut() {
		
	    if (redoStack.isEmpty())
	        return;

	    // Save current state to undo stack
	    undoStack.push(cloneCutSegments(cutSegments));

	    // Restore state from redo stack
	    cutSegments = redoStack.pop();

	    updateCurrentSegment();
	}
	
	private static void updateCurrentSegment() {
		
		//Set activeSegmentIndex
        if (VideoPlayerMultiCuts.cutSegments.isEmpty() == false)
   		{	           
        	double time = VideoPlayerCore.bufferCurrentFrame > 0 ? VideoPlayerCore.bufferCurrentFrame : VideoPlayerCore.playerCurrentFrame;
        	
            for (VideoPlayerMultiCuts.CutSegment seg : VideoPlayerMultiCuts.cutSegments)
            {
                double inputMark = VideoPlayerMultiCuts.getSegmentTime(seg.inH, seg.inM, seg.inS, seg.inF);
                double outputMark = VideoPlayerMultiCuts.getSegmentTime(seg.outH, seg.outM, seg.outS, seg.outF);

                //Current segment
                if (time >= inputMark && time < outputMark)
                {
                	VideoPlayerCore.activeSegmentIndex = seg.index;
                }
            }
   		}
        
	    if (VideoPlayerCore.activeSegmentIndex >= VideoPlayerMultiCuts.cutSegments.size())
        {
    		VideoPlayerCore.activeSegmentIndex = VideoPlayerMultiCuts.cutSegments.size() - 1;
        }

	    if (cutSegments.isEmpty() == false)
	    {
	        setCurrentSegmentValues();
	    }
	    
	    waveformContainer.repaint();
	    
	    VideoPlayerUtils.setFileList();
	}
}

