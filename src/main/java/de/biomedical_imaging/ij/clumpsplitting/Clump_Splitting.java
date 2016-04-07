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

import java.awt.AWTEvent;import java.awt.Polygon;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

/**
 * 
 * @author Louise
 *
 */

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener
{
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
	 * used for SplitLinesBetweenConcavityRegionAndPoint. ratio between the
	 * actual largest concavityDepth and the second largest concavityDepth. If
	 * the ratio is larger than the threshold the possible SplitLine could be a
	 * valid SplitLine
	 */
	public static double CONCAVITYRATIO_THRESHOLD = 6;
	
	/**
	 * if show ConcavityDepth is true the ConcavityDepth is drawn into the picture
	 */
	public static boolean SHOWCONCAVITYDEPTH=false;

	/**
	 * if show ConcavityDepth is true the ConvexHull is drawn into the picture
	 */
	public static boolean SHOWCONVEXHULL=false;

	/**
	 * if show ConcavityDepth is true the ConcavityPixels and the Pixel at the end of each SplitLine are drawn into the picture
	 */
	public static boolean SHOWPIXELS=false;

	public static int BACKGROUNDCOLOR = 1;
	int arbitraryNumber;
	ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (imp == null)
		{
			IJ.error("No image open");
			return DONE;
		}

		if (!imp.getProcessor().isBinary())
		{
			IJ.error("Only binary images are supported");
			return DONE;
		}
		this.imp = imp;
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip)
	{

		

		
		ArrayList<Clump> clumpList = new ArrayList<Clump>();
		
		ManyBlobs blobList = new ManyBlobs(new ImagePlus("", ip));
		// if the background is white backgroundColor must be 1
		blobList.setBackground(BACKGROUNDCOLOR);
		// Clumps of the Image will be detected
		blobList.findConnectedComponents();
		Clump clump = null;
		//ImageProcessor ipr = imp.getProcessor();
		for (Blob b : blobList)
		{
			// right now only the outer contours are considered
			Polygon p = b.getOuterContour();
			clump = new Clump(p, ip);
			clumpList.add(clump);
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{	
		gd.setEnabled(true);
		String selection=gd.getNextRadioButton();
		boolean showConvexHull=gd.getNextBoolean();
		boolean showConcavityDepth=gd.getNextBoolean();
		boolean showPixels=gd.getNextBoolean();
		Double concavityDepthThreshold=gd.getNextNumber();
		Double saliencyThreshold=gd.getNextNumber();
		Double concavityConcavityAlignmentThreshold=gd.getNextNumber();
		Double concavityLineAlignmentThreshold=gd.getNextNumber();
		Double concavityAngleThreshold=gd.getNextNumber();
		Double concavityRatioThreshold=gd.getNextNumber();
		if(gd.invalidNumber()){
			return false;
		}else{
		
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
			
			SHOWCONCAVITYDEPTH=showConcavityDepth;
			SHOWCONVEXHULL=showConvexHull;
			SHOWPIXELS=showPixels;
			if(!saliencyThreshold.isNaN())
			{
				SALIENCY_THRESHOLD=saliencyThreshold;
			//	IJ.log(saliencyThreshold+"");
			}
			if(!concavityConcavityAlignmentThreshold.isNaN())
			{
				CONCAVITYCONCAVITY_THRESHOLD=((2*Math.PI)/360)*concavityConcavityAlignmentThreshold;
			}
			if(!concavityDepthThreshold.isNaN())
			{
				CONCAVITY_DEPTH_THRESHOLD=concavityDepthThreshold;
			}
			//IJ.log(concavityDepthThreshold+"");
			if(!concavityLineAlignmentThreshold.isNaN())
			{
				CONCAVITYLINE_THRESHOLD=((2*Math.PI)/360)*concavityLineAlignmentThreshold;
			}
			//IJ.log("concavitylinethreshold1="+concavityLineAlignmentThreshold);
			
			//IJ.log("concavitylinethreshold2="+CONCAVITYLINE_THRESHOLD);
			if(!concavityAngleThreshold.isNaN())
			{
				CONCAVITYANGLE_THRESHOLD=((2*Math.PI)/360)*concavityAngleThreshold;
			}
			//IJ.log("concavitylinethreshold1="+concavityAngleThreshold);
			
			//IJ.log("concavityanglethreshold2="+CONCAVITYLINE_THRESHOLD);
			if(!concavityRatioThreshold.isNaN())
			{
				CONCAVITYRATIO_THRESHOLD=concavityRatioThreshold;
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

		GenericDialog gd = new GenericDialog("Set Parameters");
		String[] radioboxValues =
		{ "black", "white" };
		gd.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");
		//String[] checkboxes= new String[2];
		//checkboxes[0]="Show Convexhull";
		//checkboxes[1]="Show Concavitydepth";
		//boolean[] checkboxValues=new boolean[2];
		//checkboxValues[0]=false;
		//checkboxValues[1]=false;
		
	//	gd.addCheckboxGroup(1, 2,checkboxes, checkboxValues);
		gd.addCheckbox("Show Convex Hull",false);
		gd.addCheckbox("Show Concavity-Depth", false);
		gd.addCheckbox("Show Concavity Pixel and Split Points", false);
		gd.addNumericField("Concavity-Depth threshold", 3, 0);
		gd.addSlider("Saliency threshold", 0, 1, 0.12);
		gd.addSlider("Concavity-Concavity-Alignment threshold in Degrees", 0, 180, 105);
		gd.addSlider("Concavity-Line-Alignment threshold in Degrees",0,180,70);
		gd.addSlider("Concavity-Angle threshold in Degrees",0,180, 90);
		gd.addNumericField("Concavity-Ratio threshold", 6, 1);
		gd.addPreviewCheckbox(pfr);
		gd.addDialogListener(this);
		gd.showDialog();
		/*if(gd.isPreviewActive())
		{
		gd.previewRunning(true);
		
		}*/
		if (gd.wasCanceled())
		{
			return DONE;
		}
		/*
		if (gd.wasOKed())
		{
		}
*/
		return IJ.setupDialog(imp, DOES_8G);
	}

	@Override
	public void setNPasses(int nPasses)
	{
		// TODO Auto-generated method stub

	}

}


