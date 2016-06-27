/*
The MIT License (MIT)
Copyright (c) 2016 Louise Bloch (louise.bloch001@stud.fh-dortmund.de), Thorsten Wagner (wagner@b
iomedical-imaging.de)

Permission is hereby granted, free of charge, to any person obtaining a
copy
of this software and associated documentation files (the "Software"),
to deal
in the Software without restriction, including without limitation the
rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE
SOFTWARE.
*/

package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.AWTEvent;
import java.awt.Polygon;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.JWindow;

import de.biomedical_imaging.ij.clumpsplitting.SplitLines.SplitLineAssignmentSVM;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.AutoThresholder;
import ij.process.AutoThresholder.Method;
import ij.process.ImageProcessor;

/**
 * 
 * @author Louise
 *
 */

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener
{
	/**
	 * panel to show Information about the ConcavityRegions to optimize
	 * parameter
	 */
	public static JWindow windowPanelConcavityRegion = new JWindow();
	/**
	 * List To Train SVM for SplitLineParameters C1 and C2. It contains the sum
	 * of both ConcavityDepths of the ConcavityRegions for a SplitLine and the
	 * distance between both SplitPoints for
	 * StraightSplitLinesBetweenTwoConcavityRegions and if it is a valid
	 * splitline or not.
	 */
	public static ArrayList<SplitLineAssignmentSVM> listOfAllPossibleSplitLinesAndClassForSVM = new ArrayList<SplitLineAssignmentSVM>();
	public static ConcavityPixelDetectorType CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS;
	/**
	 * represents the status of the Plugin, if ok button is pressed it is true,
	 * shows if preview is running or if the final decision is made
	 */
	public static boolean WASOKED = false;
	/**
	 * TextBox to write Information about the ConcavityRegions into
	 */
	public static JTextArea textAreaForConcavityInformation = new JTextArea();
	/**
	 * variable which tells if all ConvexHull for the Clumps in the original
	 * image are drawn. A Convex Hull for all seperated clumps is very
	 * irritating, because some of them could overlap
	 */
	private static boolean done;
	/**
	 * if it is true and the ok button was pressed Data of
	 * StraightSplitLineBetweenTwoConcavityRegions is written to a LibSVM file
	 * to analyse it by a SVM
	 */
	public static boolean WRITEDATAINFILE = false;
	/**
	 * Type of the splitLine 0 corresponds to StraightSplitLine, 1 corresponds
	 * to Maximum-Intensity-Split-Line, 2 corresponds to
	 * Minimum-Intensity-Split-Line, 3 corresponds to
	 * Geodesic-Distance-Split-Line, 4 corresponds to
	 * Maximum-Intensity-Split-Line Farhan and 5 corresponds to
	 * Minimum-Intensity-Split-Line Farhan"))
	 */
	public static SplitLineType SPLITLINETYPE = SplitLineType.STRAIGHTSPLITLINE;
	/**
	 * the threshold defines if a ConcavityRegion is valid or not. If the
	 * largest concavityDepth of a concavityRegion is larger than the threshold
	 * the concavityRegion is accepted as a valid concavityRegion
	 */

	public static double CONCAVITY_DEPTH_THRESHOLD = 3;
	/**
	 * used for SplitLinesBetweenTwoConcavityRegions. SALIENCY evaluates, if the
	 * concavityRegion has a large Concaveness and the possible Line is short
	 * enough. If the Value is larger than the threshold the possible Split Line
	 * could be a valid SplitLine
	 */
	public static double SALIENCY_THRESHOLD = 0.12;
	/**
	 * used for SplitLinesBetweenTwoConcavityRegions.
	 * CONCAVITYCONCAVITYALIGNMENT evaluates the angle between the
	 * concavityRegionorientations. If the angle is smaller than the threshold
	 * the possible SplitLine could be a valid SplitLine
	 */
	public static double CONCAVITYCONCAVITY_THRESHOLD = 1.8325957;
	/**
	 * used for SplitLinesBetweenTwoConcavityRegions. CONCAVITYLINEALIGNMENT
	 * evaluates the largest angle between the concavityRegion Orientation and
	 * the Possible Splitline. If the angle is smaller than the threshold the
	 * possible SplitLine could be a valid SplitLine
	 */
	public static double CONCAVITYLINE_THRESHOLD = 1.2217305;
	/**
	 * used for SplitLinesBetweenConcavityRegionAndPoint. angle between
	 * startPoint of the concavityRegion, endPoint of the concavityRegion and
	 * the Point with the largest concavityDepth of the concavityRegion. If the
	 * angle is smaller than the threshold the possible SplitLine could be a
	 * valid SplitLine
	 */
	public static double CONCAVITYANGLE_THRESHOLD = 1.5707963;
	public static double CHI_THRESHOLD=0.5;
	/**
	 * used for SplitLinesBetweenConcavityRegionAndPoint. ratio between the
	 * actual largest concavityDepth and the second largest concavityDepth. If
	 * the ratio is larger than the threshold the possible SplitLine could be a
	 * valid SplitLine
	 */
	public static double CONCAVITYRATIO_THRESHOLD = 6;
	/**
	 * user choosed parameter/ optimized by SVM for Nanoparticles to find the
	 * best splitLine of an actual Clump
	 */
	public static double C1 = 1.73;
	/**
	 * user choosed parameter/ optimized by SVM for Nanoparticles to find the
	 * best splitLine of an actual Clump
	 */
	public static double C2 = -4.72;

	/**
	 * if show ConcavityDepth is true the ConvexHull is drawn into the picture
	 */
	public static boolean SHOWCONVEXHULL = false;

	/**
	 * if show ConcavityDepth is true the ConcavityPixels and the Pixel at the
	 * end of each SplitLine are drawn into the picture
	 */
	public static boolean SHOWPIXELS = false;

	/**
	 * auxiliar variable which tells if all ConvexHull for the Clumps in the
	 * original image are drawn. A Convex Hull for all seperated clumps is very
	 * irritating, because some of them could overlap
	 */
	public static int count;
	/**
	 * establish if the binary backgroundcolor of an image is black(0) or
	 * white(1)
	 */
	public static int BACKGROUNDCOLOR = 1;

	/**
	 * instance of the actual image
	 */
	public static ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (imp == null)
		{
			IJ.error("No image open");
			return DONE;
		}

		Clump_Splitting.imp = imp;
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip)
	{

		// overlays are cleared to show actual overlays
		Clump.overlayForOrientation.clear();
		Clump.overlayConvexHull.clear();
		Clump.overlaySplitPoints.clear();
		Clump.boundaryOverlay.clear();
		ArrayList<Clump> clumpList = new ArrayList<Clump>();

		ImagePlus imp = IJ.getImage();
		// int i = 0;
		/* TODO */
		IJ.showProgress(0.0);
		do
		{

			// generates a copy of the original image to binarize the image to
			// find ConcavityRegions and SplitPoints
			ImageProcessor imageProcessorBinary = ip.duplicate();

			AutoThresholder at = new AutoThresholder();
			int[] histogram = imageProcessorBinary.getHistogram();
			int threshold = at.getThreshold(Method.Default, histogram);

			// pre-processing
			//imageProcessorBinary.blurGaussian(2.0);
			imageProcessorBinary.threshold(threshold);
			// preprocessing /*TODO*/
			/*
			 * if(Clump_Splitting.BACKGROUNDCOLOR==1) {
			 * imageProcessorBinary.erode();
			 * 
			 * imageProcessorBinary.dilate(); } else{
			 * imageProcessorBinary.invert(); imageProcessorBinary.erode();
			 * 
			 * imageProcessorBinary.dilate(); imageProcessorBinary.invert();
			 * 
			 * }
			 */
			// computes the blobs at the image
			ManyBlobs blobList = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
			blobList.setBackground(BACKGROUNDCOLOR);

			/*
			 * Counter of ready Clumps has to be zero at the beginning of each
			 * step, because all Clumps are detected at first. Counts the number
			 * of Clumps for which no possible splitlines can be found
			 */
			Clump.STOP = 0;

			clumpList.clear();
			/*
			 * if the background is white backgroundColor must be 1 Clumps of
			 * the Image will be detected
			 */
			blobList.findConnectedComponents();

			Clump clump = null;
			for (Blob b : blobList)
			{
				// outerContour is detected
				Polygon p = b.getOuterContour();
				// innerContours of a Clump are detected
				ArrayList<Polygon> q = b.getInnerContours();
				clump = new Clump(p, q, ip);
				clumpList.add(clump);
			}

			if (!Clump_Splitting.done)
			{
				Clump_Splitting.count = clumpList.size();
				Clump_Splitting.done = true;
			}
			/*
			 * crash condition stops, if no more possible best SplitLines are
			 * found
			 */
		} while (clumpList.size() > Clump.STOP);

		/*
		 * writes data of each StraightSplitLine into a LibSVM file
		 */
		if (Clump_Splitting.WASOKED)
		{
			if (Clump_Splitting.WRITEDATAINFILE)
			{
				this.writeDataInFile();
			}
		}
		/*
		 * If the condition is true all Clumps are split and the plugin is
		 * completed
		 */
		if (Clump.STOP == clumpList.size())
		{
			for (Clump clump : clumpList)
			{
				clump.drawBoundaryOverlay();
			}
			IJ.log("Die Anzahl der gefundenen Klumpen beträgt: " + clumpList.size());
		}
		/*
		 * manages the overlays
		 */
		Overlay o = new Overlay();
		for (Roi overlay : Clump.overlayForOrientation)
		{
			o.addElement(overlay);
		}
		for (Roi overlay : Clump.overlayConvexHull)
		{
			o.addElement(overlay);
		}
		for (Roi overlay : Clump.overlaySplitPoints)
		{
			o.addElement(overlay);
		}
		for (Roi overlay : Clump.boundaryOverlay)
		{
			o.addElement(overlay);
		}

		imp.setOverlay(o);
		/*
		 * adds MouseListener to each ConcavityRegion for the Bounding Box to
		 * show information about the ConcavityRegions
		 */
		for (int n = 0; n < Clump.allRegions.size(); n++)
		{
			ConcavityRegion cr = Clump.allRegions.get(n);

			imp.getCanvas().addMouseListener(new MouseListenerConcavityRegions(cr));
		}

	}

	/**
	 * writes Data about all StraightSplitLines into a LibSVM file to save Data
	 * and to analyse it with a SVM and optimize parameter for the cell type:
	 * data which is saved is the distance between the ConcavityPixel of the
	 * ConcavityRegion of the SplitLine and the sum of the ConcavityDeoth of all
	 * ConcavityPixel
	 */
	private void writeDataInFile()
	{

		/*
		 * SparkConf conf = new
		 * SparkConf().setAppName("Test").setMaster("local"); JavaSparkContext
		 * sc = new JavaSparkContext(conf);
		 * 
		 * ArrayList<LabeledPoint> listOfAllLabeledPoints = new
		 * ArrayList<LabeledPoint>();
		 */

		FileWriter writer = null;
		try
		{
			writer = new FileWriter("yourfile.csv");
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (SplitLineAssignmentSVM slaSVM : Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM)
		{
			Double maxDistSum = slaSVM.getSumConcavityDepth();
			maxDistSum = maxDistSum * 1000;
			maxDistSum = (double) Math.round(maxDistSum);
			maxDistSum = maxDistSum / 1000;
			Double distance = slaSVM.getDistance();
			distance = distance * 1000;
			distance = (double) Math.round(distance);
			distance = distance / 1000;
			// feed in your array (or convert your data to an array)
			String[] entrie =
			{ String.valueOf(slaSVM.getClassificationValue()), maxDistSum.toString(), distance.toString() };
			try
			{
				writer.write(entrie[0] + "," + entrie[1] + "," + entrie[2] + "\n");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try
		{
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{

		gd.setEnabled(true);
		String selection = gd.getNextRadioButton();
		boolean showConvexHull = gd.getNextBoolean();
		boolean showPixels = gd.getNextBoolean();
		boolean writeDataInFile = false;
		Double concavityDepthThreshold = gd.getNextNumber();
		Double saliencyThreshold = 0.0;
		Double concavityConcavityAlignmentThreshold = 0.0;
		Double concavityLineAlignmentThreshold = 0.0;
		Double concavityAngleThreshold = 0.0;
		Double concavityRatioThreshold = 0.0;
		Double c1 = 0.0;
		Double c2 = 0.0;
		Double chi=0.0;
		if (Clump_Splitting.SPLITLINETYPE == SplitLineType.STRAIGHTSPLITLINE || Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.MINIMUMINTENSITYSPLITLINE || Clump_Splitting.SPLITLINETYPE == SplitLineType.GEODESICDISTANCESPLITLINE)
		{
			writeDataInFile = gd.getNextBoolean();
			saliencyThreshold = gd.getNextNumber();
			concavityConcavityAlignmentThreshold = gd.getNextNumber();
			concavityLineAlignmentThreshold = gd.getNextNumber();
			concavityAngleThreshold = gd.getNextNumber();
			concavityRatioThreshold = gd.getNextNumber();
			c1 = gd.getNextNumber();
			c2 = gd.getNextNumber();
			chi=gd.getNextNumber();
		}
		if (gd.invalidNumber())
		{
			return false;
		} else
		{

			if (selection.equals("black"))
			{
				BACKGROUNDCOLOR = 0;
			} else
			{
				if (selection.equals("white"))
				{
					BACKGROUNDCOLOR = 1;
				}
			}

			SHOWCONVEXHULL = showConvexHull;
			Clump_Splitting.WRITEDATAINFILE = writeDataInFile;
			SHOWPIXELS = showPixels;
			if (!saliencyThreshold.isNaN())
			{
				SALIENCY_THRESHOLD = saliencyThreshold;
			}
			if (!concavityConcavityAlignmentThreshold.isNaN())
			{
				CONCAVITYCONCAVITY_THRESHOLD = ((2 * Math.PI) / 360) * concavityConcavityAlignmentThreshold;
			}
			if (!concavityDepthThreshold.isNaN())
			{
				CONCAVITY_DEPTH_THRESHOLD = concavityDepthThreshold;
			}
			if (!concavityLineAlignmentThreshold.isNaN())
			{
				CONCAVITYLINE_THRESHOLD = ((2 * Math.PI) / 360) * concavityLineAlignmentThreshold;
			}
			if (!concavityAngleThreshold.isNaN())
			{
				CONCAVITYANGLE_THRESHOLD = ((2 * Math.PI) / 360) * concavityAngleThreshold;
			}
			if (!concavityRatioThreshold.isNaN())
			{
				CONCAVITYRATIO_THRESHOLD = concavityRatioThreshold;
			}
			if (!c1.isNaN())
			{
				Clump_Splitting.C1 = c1;
			}
			if (!c2.isNaN())
			{
				Clump_Splitting.C2 = c2;
			}

			if (!chi.isNaN())
			{
				Clump_Splitting.CHI_THRESHOLD = chi;
			}
			

			return true;
		}
	}

	/**
	 * Dialogue where you can decide which one is your backgroundcolor
	 */
	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{
		GenericDialog gd = new NonBlockingGenericDialog("Choose your Split-Line-Type");

		String[] items =
		{ "Straight Split-Line", "Maximum-Intensity-Split-Line", "Minimum-Intensity-Split-Line",
				"Geodesic-Distance-Split-Line", "Maximum-Intensity-Split-Line Farhan",
				"Minimum-Intensity-Split-Line Farhan" };
		gd.addChoice("Split-Line-Type:", items, "Straight Split-Line");
		String[] itemsDetector =
		{ "Detect all Concavity-Pixels", "Detect all Concavity-Pixels with largest Concavity-Depth" };
		gd.addChoice("Concavity-Pixel-Detector-Type", itemsDetector, "Detect all Concavity-Pixels");

		gd.showDialog();

		if (gd.wasCanceled())
		{
			return DONE;
		}

		if (gd.wasOKed())
		{
			/*
			 * different Menues to chose parameter for each SplitLineType
			 */
			String splitLineType = gd.getNextChoice();
			String detectorType = gd.getNextChoice();
			
			if (splitLineType.equals("Straight Split-Line") || splitLineType.equals("Maximum-Intensity-Split-Line")
					|| splitLineType.equals("Minimum-Intensity-Split-Line")
					|| splitLineType.equals("Geodesic-Distance-Split-Line"))
			{
				if (splitLineType.equals("Straight Split-Line"))
				{
					Clump_Splitting.SPLITLINETYPE = SplitLineType.STRAIGHTSPLITLINE;
				} else
				{
					if (splitLineType.equals("Maximum-Intensity-Split-Line"))
					{
						Clump_Splitting.SPLITLINETYPE = SplitLineType.MAXIMUMINTENSITYSPLITLINE;
					} else
					{
						if (splitLineType.equals("Minimum-Intensity-Split-Line"))
						{
							Clump_Splitting.SPLITLINETYPE = SplitLineType.MINIMUMINTENSITYSPLITLINE;
						} else
						{
							if (splitLineType.equals("Geodesic-Distance-Split-Line"))
							{
								Clump_Splitting.SPLITLINETYPE = SplitLineType.GEODESICDISTANCESPLITLINE;
							}

						}
					}
				}
				if (detectorType.equals("Detect all Concavity-Pixels"))
				{
					Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS;
				}
				else{
					if(detectorType.equals("Detect all Concavity-Pixels with largest Concavity-Depth"))
					{
						Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE=ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH;
					}
				}
				GenericDialog dialog1 = new NonBlockingGenericDialog("Choose Parameters for Clump Splitting");

				String[] radioboxValues =
				{ "black", "white" };
				dialog1.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");

				dialog1.addCheckbox("Show Convex Hull", false);
				dialog1.addCheckbox("Show Concavity Pixel and Split Points", false);
				dialog1.addCheckbox("Write data in file to train SVM", false);
				dialog1.addNumericField("Concavity-Depth threshold", 3, 0);
				dialog1.addSlider("Saliency threshold", 0, 1, 0.12);
				dialog1.addSlider("Concavity-Concavity-Alignment threshold in Degrees", 0, 180, 105);
				dialog1.addSlider("Concavity-Line-Alignment threshold in Degrees", 0, 180, 70);
				dialog1.addSlider("Concavity-Angle threshold in Degrees", 0, 180, 90);
				dialog1.addNumericField("Concavity-Ratio threshold", 6, 1);
				dialog1.addNumericField("C1", 1.73, 3);
				dialog1.addNumericField("C2", -4.72, 3);
				dialog1.addNumericField("Chi-Threshold", 0.5, 3);
				dialog1.addPreviewCheckbox(pfr);
				dialog1.addDialogListener(this);
				dialog1.showDialog();
				dialog1.setFocusable(true);
				WindowManager.getCurrentImage().getWindow().getCanvas().setFocusable(true);

				gd.setVisible(false);
				gd.dispose();
				if (dialog1.wasOKed())
				{
					Clump_Splitting.WASOKED = true;
				}
				if (dialog1.wasCanceled())
				{
					return DONE;
				}
			} else
			{
				if (splitLineType.equals("Maximum-Intensity-Split-Line Farhan")
						|| splitLineType.equals("Minimum-Intensity-Split-Line Farhan"))
				{
					if (splitLineType.equals("Maximum-Intensity-Split-Line Farhan"))
					{
						Clump_Splitting.SPLITLINETYPE = SplitLineType.MAXIMUMINTENSITYSPLITLINEFARHAN;
					} else
					{
						if (splitLineType.equals("Minimum-Intensity-Split-Line Farhan"))
						{
							Clump_Splitting.SPLITLINETYPE = SplitLineType.MINIMUMINTENSITYSPLITLINEFARHAN;
						}
					}
					
					if (detectorType.equals("Detect all Concavity-Pixels"))
					{
						Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS;
					}
					else{
						if(detectorType.equals("Detect all Concavity-Pixels with largest Concavity-Depth"))
						{
							Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE=ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH;
						}
					}
					GenericDialog dialog1 = new NonBlockingGenericDialog("Choose Parameters for Clump Splitting");

					String[] radioboxValues =
					{ "black", "white" };
					dialog1.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");

					dialog1.addCheckbox("Show Convex Hull", false);
					dialog1.addCheckbox("Show Concavity Pixel and Split Points", false);
					dialog1.addNumericField("Concavity-Depth threshold", 3, 0);

					dialog1.addPreviewCheckbox(pfr);
					dialog1.addDialogListener(this);
					dialog1.showDialog();
					dialog1.setFocusable(true);
					WindowManager.getCurrentImage().getWindow().getCanvas().setFocusable(true);

					gd.setVisible(false);
					gd.dispose();
					if (dialog1.wasCanceled())
					{
						return DONE;
					}
				}
			}

		}

		return IJ.setupDialog(imp, DOES_8G);
	}

	@Override
	public void setNPasses(int nPasses)
	{
		// TODO Auto-generated method stub

	}

}
