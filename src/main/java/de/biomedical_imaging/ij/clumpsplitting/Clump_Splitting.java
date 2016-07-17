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
import java.awt.Component;
import java.awt.Polygon;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

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
	 * Counter of ready Clumps has to be zero at the beginning of each step,
	 * because all Clumps are detected at first. Counts the number of Clumps for
	 * which no possible splitlines can be found
	 */
	public static int STOP = 0;

	/**
	 * parameter to estabish in which range the detection of the innercontour
	 * Convex Hulls should be detected. If the inner Contours are small you
	 * should choose a small value. If your inner Contours are larger maybe less
	 * points are detected. It could be helpful to increase
	 * innercontourparameter default-value: 2
	 */
	public static int INNERCONTOURPARAMETER = 2;
	/**
	 * List To Train SVM for SplitLineParameters C1 and C2. It contains the sum
	 * of both ConcavityDepths of the ConcavityRegions for a SplitLine and the
	 * distance between both SplitPoints for
	 * StraightSplitLinesBetweenTwoConcavityRegions and if it is a valid
	 * splitline or not.
	 */
	public static ArrayList<SplitLineAssignmentSVM> listOfAllPossibleSplitLinesAndClassForSVM = new ArrayList<SplitLineAssignmentSVM>();
	/**
	 * Type of the ConcavityPixelDetector Type. Values are described and
	 * established in enum ConcavityPixel detector type
	 */
	public static ConcavityPixelDetectorType CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS;
	/**
	 * represents the status of the Plugin, if ok button is pressed it is true,
	 * shows if preview is running or if the final decision is made
	 */
	public static boolean WASOKED = false;
	/**
	 * List for all possible ConcavityRegions to show parameter for it to choose
	 * best parameter for the Clump splitting
	 */
	public static ArrayList<ConcavityRegion> allRegions = new ArrayList<ConcavityRegion>();

	/**
	 * the concavityRegionList includes all useful concavityRegion the criteria,
	 * if a ConcavityRegion is useful is the CONCAVITY_DEPTH_THRESHOLD from
	 * class ConcavityRegionAdministration
	 */
	public static int done = 0;
	/**
	 * List of ROIs, which manages the overlay for the Orientation
	 */
	public static ArrayList<Roi> overlayForOrientation = new ArrayList<Roi>();
	/**
	 * List of ROIs, which manages description Texts for each concavityRegion to
	 * optimize parameters
	 */
	public static ArrayList<Roi> overlayTextConvexHull = new ArrayList<Roi>();
	/**
	 * List of ROIs, which manages the overlay for the ConvexHulls
	 */
	public static ArrayList<Roi> overlayConvexHull = new ArrayList<Roi>();

	/**
	 * List of ROIs, which manages the overlay for the SplitPoints
	 */
	public static ArrayList<Roi> overlaySplitPoints = new ArrayList<Roi>();

	/**
	 * variable which tells if all ConvexHull for the Clumps in the original
	 * image are drawn. A Convex Hull for all seperated clumps is very
	 * irritating, because some of them could overlap
	 */

	private static boolean isReady;
	/**
	 * if it is true and the OK button was pressed Data of
	 * StraightSplitLineBetweenTwoConcavityRegions is written to a LibSVM file
	 * to analyze and optimize it by a SVM
	 */
	public static boolean WRITEDATAINFILE = false;
	/**
	 * Type of the splitLine, types are specified by enum SplitLineType
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
	/**
	 * threshold Chi, which specifies if a SplitLine is valid or not. A high
	 * chi-Value represents a high ratio between concavityDepths of the
	 * ConcavityRegions and Distance between the Splitpoints. The Chi value
	 * depends on the Values of parameters C1 and C2
	 */
	public static double CHI_THRESHOLD = 0.5;
	/**
	 * used for SplitLinesBetweenConcavityRegionAndPoint. ratio between the
	 * actual largest concavityDepth and the second largest concavityDepth. If
	 * the ratio is larger than the threshold the possible SplitLine could be a
	 * valid SplitLine
	 */
	public static double CONCAVITYRATIO_THRESHOLD = 6;
	/**
	 * specifies if a picture is already preprocessed, to prevent another
	 * preprocession to recieve the size of the inner Contours
	 */
	public static boolean ISPREPROCESSED = false;
	/**
	 * user choosed parameter/ optimized by SVM for Nanoparticles to find the
	 * best splitLine of an actual Clump is C1 influences the value of chi of
	 * the Actual splitLine
	 */
	public static double C1 = 1.73;
	/**
	 * user choosed parameter/ optimized by SVM for Nanoparticles to find the
	 * best splitLine of an actual Clump C2 influences the value of chi of the
	 * Actual splitLine
	 */
	public static double C2 = -4.72;

	/**
	 * if show ConcavityDepth is true the ConvexHull is added to static
	 * overlayConvexHull
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
	 * establish if the binary backgroundcolor of an image is black(=0) or
	 * white(=1) by default background color will be assumed as white
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
		Clump_Splitting.overlayForOrientation.clear();
		Clump_Splitting.overlayConvexHull.clear();
		Clump_Splitting.overlaySplitPoints.clear();

		ArrayList<Clump> clumpList = new ArrayList<Clump>();

		ImagePlus imp = IJ.getImage();
		imp.setOverlay(null);
		// int i = 0;
		/* TODO */
		IJ.showProgress(0.0);

		ImageProcessor imageProcessorBinary = ip.duplicate();

		if (!Clump_Splitting.ISPREPROCESSED)
		{
			imageProcessorBinary.blurGaussian(2.0);
		}

		// preprocessing
		AutoThresholder at = new AutoThresholder();

		int[] histogram = imageProcessorBinary.getHistogram();
		// computes the binary image of the input image
		int threshold = at.getThreshold(Method.Default, histogram);
		imageProcessorBinary.threshold(threshold);

		/*
		 * ShapeSmoothingUtil ssu= new ShapeSmoothingUtil();
		 * ssu.setBlackBackground(true);
		 * ssu.fourierFilter(imageProcessorBinary,40, true,false);
		 */

		ImageProcessor binary = imageProcessorBinary;
		do
		{
			// iteratively computes the Splitlines
			clumpList = Clump_Splitting.computeClumps(ip, binary);
			/*
			 * Crash condition stops if no possible ConcavityRegions are found
			 * in an iteration for all Clumps
			 */
		} while (clumpList.size() > Clump_Splitting.STOP);

		/*
		 * writes data of each StraightSplitLine into a csv file
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
		if (Clump_Splitting.STOP == clumpList.size())
		{
			IJ.log("Die Anzahl der gefundenen Klumpen beträgt: " + clumpList.size());
		}
		/*
		 * manages the overlays
		 */
		Clump_Splitting.showOverlay();
		/*
		 * adds MouseListeners to each ConcavityRegion for the Bounding Box to
		 * show information about the ConcavityRegions
		 */
		for (int n = 0; n < Clump_Splitting.allRegions.size(); n++)
		{
			ConcavityRegion cr = Clump_Splitting.allRegions.get(n);

			imp.getCanvas().addMouseListener(new MouseListenerConcavityRegions(cr));
		}

	}

	/**
	 * private Method to compute the SplitLines of a Clump (normally one
	 * SplitLine is computed per iteration)(the one with the best chi value) and
	 * is drawn into the image
	 * 
	 * @param ip
	 *            ImageProcessor, which contains the original Image, used to
	 *            show SplitLines and to compute Geodesic-/Maximum-/ Minimum-
	 *            Splitlines
	 * @param binary
	 *            ImageProcessor of the binarized and preprocessed image to
	 *            analyse the Contour of a Clump to find concavity Regions and
	 *            ConcavityPixel to Split a Clump
	 * @return List of all detected Clumps
	 */
	private static ArrayList<Clump> computeClumps(ImageProcessor ip, ImageProcessor binary)
	{
		ManyBlobs blobList = new ManyBlobs(new ImagePlus("", binary));
		blobList.setBackground(BACKGROUNDCOLOR);

		/*
		 * Counter of ready Clumps has to be zero at the beginning of each step,
		 * because all Clumps are detected at first. Counts the number of Clumps
		 * for which no possible splitlines can be found the instantiation of a
		 * Clump already computes the SplitLines for the Clump
		 */
		Clump_Splitting.STOP = 0;

		ArrayList<Clump> clumpList = new ArrayList<Clump>();
		/*
		 * if the background is white backgroundColor must be 1 Clumps of the
		 * Image will be detected
		 */
		blobList.findConnectedComponents();

		Clump clump = null;
		for (Blob b : blobList)
		{
			// outerContour is detected
			Polygon p = b.getOuterContour();
			// innerContours of a Clump are detected
			ArrayList<Polygon> q = b.getInnerContours();
			clump = new Clump(p, q, ip, binary);
			clumpList.add(clump);
		}

		if (!Clump_Splitting.isReady)
		{
			Clump_Splitting.count = clumpList.size();
			Clump_Splitting.isReady = true;
		}

		return clumpList;

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
		FileWriter writer = null;
		try
		{
			String filename="";
			  JFileChooser chooser = new JFileChooser();
		        // Dialog zum Oeffnen von Dateien anzeigen
		        int rueckgabeWert = chooser.showSaveDialog(null);
		        
		        /* Abfrage, ob auf "Öffnen" geklickt wurde */
		        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
		        {
		             // Ausgabe der ausgewaehlten Datei
		            filename=chooser.getSelectedFile().getAbsolutePath();
		        }//String title = imp.getTitle();

			
			String st =  filename+".csv";
			writer = new FileWriter(st);
		} catch (IOException e1)
		{
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
				e.printStackTrace();
			}

		}
		try
		{
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{

		gd.setEnabled(true);
		String selection = gd.getNextRadioButton();
		boolean isPreprocessed = gd.getNextBoolean();
		boolean showConvexHull = gd.getNextBoolean();
		boolean showPixels = gd.getNextBoolean();
		boolean writeDataInFile = false;
		Double concavityDepthThreshold = gd.getNextNumber();
		Double saliencyThreshold = 0.0;
		Double concavityConcavityAlignmentThreshold = 0.0;
		Double concavityLineAlignmentThreshold = 0.0;
		Double concavityAngleThreshold = 0.0;
		Double concavityRatioThreshold = 0.0;
		int innerContourParameter = 0;
		Double c1 = 0.0;
		Double c2 = 0.0;
		Double chi = 0.0;
		if (Clump_Splitting.SPLITLINETYPE == SplitLineType.STRAIGHTSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.MINIMUMINTENSITYSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.GEODESICDISTANCESPLITLINE)
		{
			writeDataInFile = gd.getNextBoolean();
			saliencyThreshold = gd.getNextNumber();
			concavityConcavityAlignmentThreshold = gd.getNextNumber();
			concavityLineAlignmentThreshold = gd.getNextNumber();
			concavityAngleThreshold = gd.getNextNumber();
			concavityRatioThreshold = gd.getNextNumber();
			innerContourParameter = (int) gd.getNextNumber();
			c1 = gd.getNextNumber();
			c2 = gd.getNextNumber();
			chi = gd.getNextNumber();
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

			Clump_Splitting.ISPREPROCESSED = isPreprocessed;
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

			Clump_Splitting.INNERCONTOURPARAMETER = innerContourParameter;
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
	 * Opens 2 Dialogs. In the first dialog you have to decide what kind of
	 * Splitline and ConcavityPixel you want to use and the second one manages
	 * the parameters of the choosed Types
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
				} else
				{
					if (detectorType.equals("Detect all Concavity-Pixels with largest Concavity-Depth"))
					{
						Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH;
					}
				}
				GenericDialog dialog1 = new NonBlockingGenericDialog("Choose Parameters for Clump Splitting");

				String[] radioboxValues =
				{ "black", "white" };
				dialog1.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");

				dialog1.addCheckbox("Is already pre-processed", false);
				dialog1.addCheckbox("Show Convex Hull", false);
				dialog1.addCheckbox("Show Concavity Pixel and Split Points", false);
				dialog1.addCheckbox("Write data in file to train SVM", false);
				dialog1.addNumericField("Concavity-Depth threshold", 3, 0);
				dialog1.addSlider("Saliency threshold", 0, 1, 0.12);
				dialog1.addSlider("Concavity-Concavity-Alignment threshold in Degrees", 0, 180, 105);
				dialog1.addSlider("Concavity-Line-Alignment threshold in Degrees", 0, 180, 70);
				dialog1.addSlider("Concavity-Angle threshold in Degrees", 0, 180, 90);
				dialog1.addNumericField("Concavity-Ratio threshold", 6, 1);
				dialog1.addNumericField("Inner-Contour-Parameter", 2, 0);
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
					} else
					{
						if (detectorType.equals("Detect all Concavity-Pixels with largest Concavity-Depth"))
						{
							Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE = ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH;
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

	/**
	 * manages which ROIs should be shown into overlay
	 */
	public static void showOverlay()
	{
		Overlay o = new Overlay();
		for (Roi overlay : Clump_Splitting.overlayConvexHull)
		{
			o.addElement(overlay);
		}
		for (Roi overlay : Clump_Splitting.overlaySplitPoints)
		{
			o.addElement(overlay);
		}

		for (Roi overlay : Clump_Splitting.overlayTextConvexHull)
		{
			o.addElement(overlay);
		}
		imp.setOverlay(o);

	}

}
