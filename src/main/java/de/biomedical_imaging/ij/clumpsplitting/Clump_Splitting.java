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
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.JWindow;

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
public static JWindow pane=new JWindow();
	
	public static JTextArea window= new JTextArea("haaaaaloo");
		public static boolean ISPREVIEWCHECKED=false;
	private static boolean done;
	public static FileWriter fw=null;
	public static BufferedWriter bw;
	
	public static int SPLITLINETYPE=0;
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
	
	public static double C1=1.73;
	public static double C2=-4.72;
	
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
	
	public static int count;
	public static int BACKGROUNDCOLOR = 1;
	int arbitraryNumber;
	public static ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (imp == null)
		{
			IJ.error("No image open");
			return DONE;
		}

	/*	if (!imp.getProcessor().isBinary())
		{
			IJ.error("Only binary images are supported");
			return DONE;
		}*/
		Clump_Splitting.imp = imp;
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip)
	{

		Clump.overlayConcavityDepth.clear();
		Clump.overlayConvexHull.clear();
		Clump.overlaySplitPoints.clear();
		try
		{
			fw= new FileWriter("test/test.txt");
			bw= new BufferedWriter(fw);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Clump> clumpList = new ArrayList<Clump>();
		
		ImagePlus imp = IJ.getImage(); 
	      int i=0;
		IJ.showProgress(0.0);
	do{
		i++;

		ImageProcessor imageProcessorBinary= ip.duplicate();
	//	Clump.o.clear();

		AutoThresholder at= new AutoThresholder();
		int[] histogram = imageProcessorBinary.getHistogram();
		int threshold=at.getThreshold(Method.Default, histogram);
		
		imageProcessorBinary.blurGaussian(1.0);
		imageProcessorBinary.threshold(threshold);
	//	imageProcessorBinary.autoThreshold();
		if(Clump_Splitting.BACKGROUNDCOLOR==1)
		{
		imageProcessorBinary.erode();
		
		imageProcessorBinary.dilate();
		} else{
			imageProcessorBinary.invert();
			imageProcessorBinary.erode();
			
			imageProcessorBinary.dilate();
			imageProcessorBinary.invert();
			
		}
		ManyBlobs blobList = new ManyBlobs(new ImagePlus("", imageProcessorBinary));
		blobList.setBackground(BACKGROUNDCOLOR);
		
		Clump.STOP=0;
		clumpList.clear();
		// if the background is white backgroundColor must be 1
		// Clumps of the Image will be detected
		blobList.findConnectedComponents();
		Clump clump = null;
		//ImageProcessor ipr = imp.getProcessor();
		for (Blob b : blobList)
		{
			// right now only the outer contours are considered
			Polygon p = b.getOuterContour();
			ArrayList<Polygon> q= b.getInnerContours();
			clump = new Clump(p,q, ip);
		//	System.out.println(Clump.STOP);
			clumpList.add(clump);
		}
		IJ.showProgress(i/10);
		
		if(!Clump_Splitting.done)
		{
		Clump_Splitting.count=clumpList.size();
		Clump_Splitting.done=true;
		}
		//IJ.log(clumpList.size()+"");
		//System.out.println(Clump.STOP + " " + clumpList.size());
	}while(clumpList.size()>Clump.STOP);
	if(Clump.STOP==clumpList.size())
	{
		
		IJ.log("Die Anzahl der gefundenen Klumpen beträgt: "+ clumpList.size());
		//IJ.showStatus(s);
	}
	Overlay o=new Overlay();
	for(Roi overlay:Clump.overlayConvexHull)
	{
		o.addElement(overlay);
	}
	for(Roi overlay:Clump.overlaySplitPoints)
	{
		o.addElement(overlay);
	}
	for(Roi overlay:Clump.overlayConcavityDepth)
	{
		o.addElement(overlay);
	}
	
	imp.setOverlay(o);
	try
	{
		bw.close();
	} catch (IOException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	for(int n=0;n<Clump.allRegions.size();n++)
	{
		ConcavityRegion cr= Clump.allRegions.get(n);
	
		
	//	System.out.println("XLänge"+xZoom+" 2 " +s.getWidth());
		imp.getCanvas().addMouseListener(new MouseListenerConcavityRegions(cr));
	}

	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{	
		
		gd.setEnabled(true);
		String selection=gd.getNextRadioButton();
		//String splitLineType=gd.getNextChoice();
		boolean showConvexHull=gd.getNextBoolean();
		boolean showConcavityDepth=gd.getNextBoolean();
		boolean showPixels=gd.getNextBoolean();
		Double concavityDepthThreshold=gd.getNextNumber();
		Double saliencyThreshold=0.0;
		Double concavityConcavityAlignmentThreshold=0.0;
		Double concavityLineAlignmentThreshold=0.0;
		Double concavityAngleThreshold=0.0;
		Double concavityRatioThreshold=0.0;
		Double c1=0.0;
		Double c2=0.0;
		if(Clump_Splitting.SPLITLINETYPE==0||Clump_Splitting.SPLITLINETYPE==1||Clump_Splitting.SPLITLINETYPE==2||Clump_Splitting.SPLITLINETYPE==3)
		 {saliencyThreshold=gd.getNextNumber();
		 concavityConcavityAlignmentThreshold=gd.getNextNumber();
		 concavityLineAlignmentThreshold=gd.getNextNumber();
		 concavityAngleThreshold=gd.getNextNumber();
		 concavityRatioThreshold=gd.getNextNumber();
		 c1=gd.getNextNumber();
		 c2=gd.getNextNumber();
		}
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
		/*	if(splitLineType.equals("Straight Split-Line"))
			{
				Clump_Splitting.SPLITLINETYPE=0;
			}
			else{
				if(splitLineType.equals("Maximum-Intensity-Split-Line"))
				{
					Clump_Splitting.SPLITLINETYPE=1;
				}
				else{
					if(splitLineType.equals("Minimum-Intensity-Split-Line"))
					{
						Clump_Splitting.SPLITLINETYPE=2;
					}
					else{
						if(splitLineType.equals("Geodesic-Distance-Split-Line"))
						{
							Clump_Splitting.SPLITLINETYPE=3;
						}
					}
				}
			}*/
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
			if(!c1.isNaN())
			{
				Clump_Splitting.C1=c1;
			}
			if(!c2.isNaN())
			{
				Clump_Splitting.C2=c2;
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
		GenericDialog gd= new NonBlockingGenericDialog("Choose your Split-Line-Type");
		
		String[] items={"Straight Split-Line","Maximum-Intensity-Split-Line","Minimum-Intensity-Split-Line", "Geodesic-Distance-Split-Line","Maximum-Intensity-Split-Line Farhan","Minimum-Intensity-Split-Line Farhan"};
		gd.addChoice("Split-Line-Type:", items, "Straight Split-Line");
		gd.showDialog();
		
		/*
		GenericDialog gd = new NonBlockingGenericDialog("Set Parameters");
		
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
		String[] items={"Straight Split-Line","Maximum-Intensity-Split-Line","Minimum-Intensity-Split-Line", "Geodesic-Distance-Split-Line"};
		gd.addChoice("Split-Line-Type:", items, "Straight Split-Line");
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
		gd.setFocusable(true);
		WindowManager.getCurrentImage().getWindow().getCanvas().setFocusable(true);
      
		/*if(gd.isPreviewActive())
		{
		gd.previewRunning(true);
		
		}*/
		if (gd.wasCanceled())
		{
			return DONE;
		}
		
		if (gd.wasOKed())
		{
			String splitLineType=gd.getNextChoice();
			if(splitLineType.equals("Straight Split-Line")||splitLineType.equals("Maximum-Intensity-Split-Line")||splitLineType.equals("Minimum-Intensity-Split-Line")||splitLineType.equals("Geodesic-Distance-Split-Line"))
			{
				if(splitLineType.equals("Straight Split-Line"))
				{
				Clump_Splitting.SPLITLINETYPE=0;
				}
				else{
					if(splitLineType.equals("Maximum-Intensity-Split-Line"))
					{
					Clump_Splitting.SPLITLINETYPE=1;
					}
					else{
						if(splitLineType.equals("Minimum-Intensity-Split-Line"))
						{
						Clump_Splitting.SPLITLINETYPE=2;
						}
						else{
							if(splitLineType.equals("Geodesic-Distance-Split-Line"))
							{
							Clump_Splitting.SPLITLINETYPE=3;
							}
							
						}
					}
				}
				GenericDialog dialog1= new NonBlockingGenericDialog("Choose Parameters for Straight-Split-Line");
				
				String[] radioboxValues =
					{ "black", "white" };
					dialog1.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");
					//String[] checkboxes= new String[2];
					//checkboxes[0]="Show Convexhull";
					//checkboxes[1]="Show Concavitydepth";
					//boolean[] checkboxValues=new boolean[2];
					//checkboxValues[0]=false;
					//checkboxValues[1]=false;
					
				//	gd.addCheckboxGroup(1, 2,checkboxes, checkboxValues);
				//	String[] items={"Straight Split-Line","Maximum-Intensity-Split-Line","Minimum-Intensity-Split-Line", "Geodesic-Distance-Split-Line"};
				//	dialog1.addChoice("Split-Line-Type:", items, "Straight Split-Line");
					dialog1.addCheckbox("Show Convex Hull",false);
					dialog1.addCheckbox("Show Concavity-Depth", false);
					dialog1.addCheckbox("Show Concavity Pixel and Split Points", false);
					dialog1.addNumericField("Concavity-Depth threshold", 3, 0);
					dialog1.addSlider("Saliency threshold", 0, 1, 0.12);
					dialog1.addSlider("Concavity-Concavity-Alignment threshold in Degrees", 0, 180, 105);
					dialog1.addSlider("Concavity-Line-Alignment threshold in Degrees",0,180,70);
					dialog1.addSlider("Concavity-Angle threshold in Degrees",0,180, 90);
					dialog1.addNumericField("Concavity-Ratio threshold", 6, 1);
					dialog1.addNumericField("C1", 1.73, 3);
					dialog1.addNumericField("C2",-4.72,3);
					
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
			else{
					if(splitLineType.equals("Maximum-Intensity-Split-Line Farhan")||splitLineType.equals("Minimum-Intensity-Split-Line Farhan"))
					{
						if(splitLineType.equals("Maximum-Intensity-Split-Line Farhan"))
						{
							Clump_Splitting.SPLITLINETYPE=4;
						}
						else{ 
							if(splitLineType.equals("Minimum-Intensity-Split-Line Farhan"))
							{
								Clump_Splitting.SPLITLINETYPE=5;
							}
						}
						GenericDialog dialog1= new NonBlockingGenericDialog("Choose Parameters for Straight-Split-Line");
						
						String[] radioboxValues =
							{ "black", "white" };
							dialog1.addRadioButtonGroup("Choose your Backgroundcolor", radioboxValues, 1, 2, "white");
							//String[] checkboxes= new String[2];
							//checkboxes[0]="Show Convexhull";
							//checkboxes[1]="Show Concavitydepth";
							//boolean[] checkboxValues=new boolean[2];
							//checkboxValues[0]=false;
							//checkboxValues[1]=false;
							
						//	gd.addCheckboxGroup(1, 2,checkboxes, checkboxValues);
						//	String[] items={"Straight Split-Line","Maximum-Intensity-Split-Line","Minimum-Intensity-Split-Line", "Geodesic-Distance-Split-Line"};
						//	dialog1.addChoice("Split-Line-Type:", items, "Straight Split-Line");
							dialog1.addCheckbox("Show Convex Hull",false);
							dialog1.addCheckbox("Show Concavity-Depth", false);
							dialog1.addCheckbox("Show Concavity Pixel and Split Points", false);
							dialog1.addNumericField("Concavity-Depth threshold", 3, 0);
						/*	dialog1.addSlider("Saliency threshold", 0, 1, 0.12);
							dialog1.addSlider("Concavity-Concavity-Alignment threshold in Degrees", 0, 180, 105);
							dialog1.addSlider("Concavity-Line-Alignment threshold in Degrees",0,180,70);
							dialog1.addSlider("Concavity-Angle threshold in Degrees",0,180, 90);
							dialog1.addNumericField("Concavity-Ratio threshold", 6, 1);
							dialog1.addNumericField("C1", 1.73, 3);
							dialog1.addNumericField("C2",-4.72,3);
							*/
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


