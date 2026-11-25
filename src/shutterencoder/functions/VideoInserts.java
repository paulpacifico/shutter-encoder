/*******************************************************************************************
* Copyright (C) 2027 PACIFICO PAUL
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

package shutterencoder.functions;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import shutterencoder.functions.utils.FunctionUtils;
import shutterencoder.library.FFmpeg;
import shutterencoder.library.FFprobe;
import shutterencoder.ui.main.Main;
import shutterencoder.ui.main.UIController;
import shutterencoder.ui.others.Console;
import shutterencoder.ui.others.Ftp;
import shutterencoder.ui.others.RenderQueue;
import shutterencoder.ui.others.Settings;
import shutterencoder.ui.videoplayer.VideoPlayerCore;
import shutterencoder.ui.videoplayer.VideoPlayerUtils;
import shutterencoder.utils.Utils;

/**
 * Overwrite-style inserts: the longest file is the "master", every other file
 * replaces the part of the master located at its start timecode, for its own duration.
 *
 * Design: everything is computed in integer FRAMES (never in milliseconds), and the
 * frame rate is handled as an exact rational (24000/1001, 30000/1001, 60000/1001, 25/1 ...).
 * Timecodes are frame labels counted at the nominal rate (24, 30, 60 ...), as in any
 * non-drop-frame timecode, so there is no rounding drift on NTSC rates.
 */
public class VideoInserts extends Main {

	/** One source file expressed on the frame grid. */
	private static final class Clip {
		String path;
		long startFrame;      // start timecode, in frames (nominal-rate label count)
		long durationFrames;  // real number of frames in the file
		double fps;           // accurate fps reported by ffprobe
	}

	/** One entry of the concat list. */
	private static final class Segment {
		String path;
		long inFrame;     // offset inside the file
		long outFrame;    // offset inside the file, -1 = until the end
	}

	public static void main() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				if (scanIsRunning == false)
					FunctionUtils.completed = 0;

				lblFilesEnded.setText(FunctionUtils.completedFiles(FunctionUtils.completed));

				try {
					process();
				} catch (Exception e) {
					FFmpeg.error = true;
				}

				if (btnStart.getText().equals(Main.language.getProperty("btnAddToRender")))
				{
					//Reset data for the current selected file
					VideoPlayerCore.videoPath = null;
					VideoPlayerUtils.setMedia();

					try {
						VideoPlayerCore.loadMedia.join();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}

					RenderQueue.completeQueueBuild();
				}
				else
				{
					UIController.endOfFunction();
				}
			}
		});
		thread.start();
	}

	private static void process() throws Exception {

		//1. Probe every file --------------------------------------------------------------
		List<Clip> clips = new ArrayList<Clip>();

		for (int i = 0; i < list.getSize(); i++)
		{
			if (Settings.btnWaitFileComplete.isSelected())
			{
				if (FunctionUtils.waitFileCompleted(new File(list.getElementAt(i))) == false)
					return;
			}

			FFprobe.Data(list.getElementAt(i));
			joinQuietly(FFprobe.processData);

			Clip c = new Clip();
			c.path = list.getElementAt(i);
			c.fps = FFprobe.accurateFPS;
			c.durationFrames = Math.round(FFprobe.totalLength / 1000.0 * c.fps);
			c.startFrame = timecodeToFrames(nominalFps(c.fps));
			clips.add(c);
		}

		if (clips.size() < 2)
			return;

		//2. Master = longest file ---------------------------------------------------------
		Clip master = clips.get(0);
		for (Clip c : clips)
			if (c.durationFrames > master.durationFrames)
				master = c;

		long[] fps = rationalFps(master.fps); //{num, den}
		int nominal = nominalFps(master.fps);

		List<Clip> inserts = new ArrayList<Clip>();
		for (Clip c : clips)
		{
			if (c == master)
				continue;

			long[] r = rationalFps(c.fps);
			if (r[0] * fps[1] != fps[0] * r[1])
			{
				Console.consoleFFMPEG.append(System.lineSeparator() + "Frame rate mismatch: " + new File(c.path).getName()
						+ " (" + c.fps + ") vs master (" + master.fps + ")");
				FFmpeg.error = true;
				return;
			}
			inserts.add(c);
		}

		inserts.sort(new Comparator<Clip>() {
			@Override
			public int compare(Clip a, Clip b) { return Long.compare(a.startFrame, b.startFrame); }
		});

		//3. Master analysis (timescale) ---------------------------------------------------
		FFprobe.FrameData(master.path);
		joinQuietly(FFprobe.processFrameData);
		String timeBase = String.valueOf(FFprobe.timeBase);

		//4. Output names ------------------------------------------------------------------
		File masterFile = new File(master.path);
		String masterName = masterFile.getName();
		int dot = masterName.lastIndexOf('.');
		String extension = dot >= 0 ? masterName.substring(dot) : "";

		String prefix = "";
		if (casePrefix.isSelected())
			prefix = FunctionUtils.setPrefixSuffix(txtPrefix.getText(), false);

		String extensionName = "";
		if (btnExtension.isSelected())
			extensionName = FunctionUtils.setPrefixSuffix(txtExtension.getText(), false);

		String labelOutput = FunctionUtils.setOutputDestination("", masterFile);
		String baseName = dot >= 0 ? masterName.substring(0, dot) : masterName;

		File fileOut = new File(labelOutput + "/" + prefix + baseName + extensionName + extension);
		if (fileOut.exists())
		{
			fileOut = FunctionUtils.fileReplacement(labelOutput, prefix + masterName, extension, extensionName + "_", extension);

			if (fileOut == null || fileOut.toString().equals("skip"))
			{
				cancelled = true;
				return;
			}
		}

		File temp = new File(labelOutput + "/inserts");
		temp.mkdir();

		File concatList = new File(fileOut.toString() + ".txt");

		//5. Re-mux inserts with the master timescale so the concat demuxer can join them --
		List<String> tempPaths = new ArrayList<String>();
		for (Clip ins : inserts)
		{
			File tmp = new File(temp, new File(ins.path).getName());
			tempPaths.add(tmp.toString());

			if (cancelled)
				continue;

			progressBar.setMaximum(100);
			lblCurrentEncoding.setText(new File(ins.path).getName());

			FFmpeg.run(" -i " + '"' + ins.path + '"' + " -video_track_timescale " + timeBase
					+ " -c copy -map v:0? -map a? -map s? -y " + '"' + tmp.toString() + '"');
			joinQuietly(FFmpeg.runProcess);
		}

		//6. Build the segment list, all in frames -----------------------------------------
		List<Segment> segments = new ArrayList<Segment>();
		long masterDur = master.durationFrames;
		long cursor = 0; //frames of the master already consumed

		for (int i = 0; i < inserts.size(); i++)
		{
			Clip ins = inserts.get(i);

			long in = ins.startFrame - master.startFrame;
			long out = in + ins.durationFrames;

			if (out <= cursor)  //fully hidden (before the master or covered by a previous insert)
				continue;
			if (in >= masterDur) //starts after the master ends: would create a gap
				continue;

			long trim = Math.max(0, cursor - in); //head of the insert to skip if it overlaps
			long effectiveIn = in + trim;

			if (effectiveIn > cursor)
				segments.add(segment(master.path, cursor, effectiveIn));

			segments.add(segment(tempPaths.get(i), trim, -1));
			cursor = out;
		}

		if (cursor < masterDur)
			segments.add(segment(master.path, cursor, -1));

		writeConcatFile(concatList, segments, fps);

		//7. Final mux ---------------------------------------------------------------------
		progressBar.setMaximum((int) Math.max(1, Math.round(masterDur * (double) fps[1] / fps[0])));
		lblCurrentEncoding.setText(fileOut.getName());

		if (cancelled == false)
		{
			String cmd = " -i " + '"' + master.path + '"' + " -timecode " + framesToTimecode(master.startFrame, nominal)
					+ " -c:v copy -c:a copy -c:s copy -map 0:v:0? -map 1:a? -map 1:s? -y ";

			FFmpeg.run(" -safe 0 -f concat -i " + '"' + concatList.toString() + '"' + cmd + '"' + fileOut.toString() + '"');
			joinQuietly(FFmpeg.runProcess);
		}

		if (FFmpeg.saveCode == false && btnStart.getText().equals(Main.language.getProperty("btnAddToRender")) == false)
			lastActions(masterFile, fileOut, concatList, labelOutput, temp);
	}

	//--------------------------------------------------------------------------------------
	// Frame-rate helpers
	//--------------------------------------------------------------------------------------

	/** Nominal rate used by timecodes: 23.976 -> 24, 29.97 -> 30, 59.94 -> 60, 25 -> 25 */
	private static int nominalFps(double fps) {
		return (int) Math.round(fps);
	}

	/** Exact fps as {numerator, denominator}. */
	private static long[] rationalFps(double fps) {

		long nominal = Math.round(fps);

		if (Math.abs(fps - nominal * 1000.0 / 1001.0) < 0.005) //NTSC family
			return new long[] { nominal * 1000, 1001 };

		if (Math.abs(fps - nominal) < 0.005)                   //integer rates
			return new long[] { nominal, 1 };

		return new long[] { Math.round(fps * 1000), 1000 };   //anything else
	}

	/** Start timecode of the file just probed, as a frame count at the nominal rate. */
	private static long timecodeToFrames(int nominal) {

		try {
			long h = Long.parseLong(FFprobe.timecode1);
			long m = Long.parseLong(FFprobe.timecode2);
			long s = Long.parseLong(FFprobe.timecode3);
			long f = Long.parseLong(FFprobe.timecode4);

			return ((h * 60 + m) * 60 + s) * nominal + f;
		} catch (Exception e) {
			return 0; //no timecode in the file
		}
	}

	private static String framesToTimecode(long frames, int nominal) {

		NumberFormat nf = new DecimalFormat("00");

		long f = frames % nominal;
		long totalSeconds = frames / nominal;
		long s = totalSeconds % 60;
		long m = (totalSeconds / 60) % 60;
		long h = totalSeconds / 3600;

		return nf.format(h) + ":" + nf.format(m) + ":" + nf.format(s) + ":" + nf.format(f);
	}

	/** Exact position in seconds of a frame, formatted for the concat demuxer. */
	private static String framesToSeconds(long frames, long[] fps) {
		return String.format(Locale.ROOT, "%.6f", frames * (double) fps[1] / fps[0]);
	}

	//--------------------------------------------------------------------------------------
	// Concat list
	//--------------------------------------------------------------------------------------

	private static Segment segment(String path, long in, long out) {
		Segment s = new Segment();
		s.path = path;
		s.inFrame = in;
		s.outFrame = out;
		return s;
	}

	private static void writeConcatFile(File concatList, List<Segment> segments, long[] fps) throws Exception {

		try (PrintWriter writer = new PrintWriter(concatList, "UTF-8"))
		{
			for (Segment s : segments)
			{
				String line = "file '" + s.path.replace("'", "'\\''") + "'";
				writer.println(line);
				Console.consoleFFMPEG.append(System.lineSeparator() + line);

				if (s.inFrame > 0)
					writer.println("inpoint " + framesToSeconds(s.inFrame, fps));

				if (s.outFrame >= 0)
					writer.println("outpoint " + framesToSeconds(s.outFrame, fps));
			}
		}
	}

	//--------------------------------------------------------------------------------------
	// Misc
	//--------------------------------------------------------------------------------------

	private static void joinQuietly(Thread t) {
		try {
			if (t != null)
				t.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static boolean deleteDirectory(File dir) {

		if (!dir.exists() || !dir.isDirectory())
			return false;

		File[] files = dir.listFiles();
		if (files != null)
		{
			for (File f : files)
			{
				if (f.isDirectory())
					deleteDirectory(f);
				else
					f.delete();
			}
		}
		return dir.delete();
	}

	private static void lastActions(File file, File fileOut, File concatList, String output, File temp) {

		//Temporary files
		deleteDirectory(temp);
		concatList.delete();

		FunctionUtils.cleanFunction(file, fileOut.toString(), fileOut, output);

		//Sending processes
		FunctionUtils.addFileForMail(fileOut.toString());
		Ftp.sendToFtp(fileOut);
		Utils.copyFile(fileOut);
	}
}