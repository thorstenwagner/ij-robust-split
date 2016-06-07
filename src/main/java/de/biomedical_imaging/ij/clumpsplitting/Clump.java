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

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.SplitLines.*;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 * Represents a connected component, it includes the boundary and the convex
 * hull of the Clump, which is detected by package ij.gui.PolygonRoi, it also
 * includes the resulting concavityregions
 * 
 * @author Louise
 *
 */
public class Clump
{
	/**
	 * the concavityRegionList includes all useful concavityRegion the criteria,
	 * if a ConcavityRegion is useful is the CONCAVITY_DEPTH_THRESHOLD from
	 * class ConcavityRegionAdministration
	 */
	public static int done=0;
	public static ArrayList<Roi> overlayConvexHull=new ArrayList<Roi>();
	public static ArrayList<Roi> overlaySplitPoints=new ArrayList<Roi>();
	public static ArrayList<Roi> overlayConcavityDepth=new ArrayList<Roi>();
	public static ArrayList<Roi> overlayAngleCtrl=new ArrayList<Roi>();
	private ArrayList<InnerContour> innerList=new ArrayList<InnerContour>();
	private ArrayList<ConcavityRegion> concavityRegionList=new ArrayList<ConcavityRegion>();
	/**
	 * the boundary describes the outer border of the Clump
	 */
	private Polygon boundary;
	/**
	 * the convexHull is detected by Class ij.gui.PolygonRoi
	 */
	private Polygon convexHull;
	/**
	 * the variable indexOfMaxConcavityRegion represents the index based on the
	 * concavityRegionList, which contains the concavityRegion with the largest
	 * distance between convexHull and boundary, this one is used for the
	 * detection of a Splitline between a concavityRegion and a boundarypoint
	 */
	public static int STOP=0;
	private int indexOfMaxConcavityRegion;
	/**
	 * the variable secondMaxConcavityDepth represents the distance of the
	 * concavityRegion with the second largest distance from boundary to
	 * convexHull. It is used to detect a SplitLine between a concavityRegion
	 * and a boundarypoint
	 */
	private double secondMaxConcavityDepth;

	/**
	 * produces a Clump from the boundary, starts the algorithm
	 * 
	 * @param boundary
	 *            the outer border of the Clump
	 * @param ip
	 *            ImageProcessor to visualize the detected Points and lines of
	 *            the Image
	 */
	public Clump(Polygon boundary,ArrayList<Polygon> innerContours, ImageProcessor ip)
	{
		this.boundary = boundary;
		this.convexHull = this.computeConvexHull(boundary);
		if(convexHull!=null)
		{
		for(Polygon innerContour: innerContours)
		{
			Polygon innerConvexHull=this.computeConvexHull(innerContour);
			InnerContour inner=new InnerContour(innerContour, innerConvexHull);
			innerList.add(inner);
		}
		 
		
		if(Clump_Splitting.SHOWCONVEXHULL&&Clump.done<Clump_Splitting.count)
		{
		this.drawConvexHull(ip);
		//this.done=true;
		Clump.done++;
		}
		else{
			if(!Clump_Splitting.SHOWCONVEXHULL)
			{
			//	IJ.log("Fehler");
				Clump.done=0;
				Clump.overlayConvexHull.clear();
			}
		}
		}
	//	IJ.log("Anzahl der Inneren Konturen"+innerList.size()+"");
	    
		this.concavityRegionList.clear();
		this.concavityRegionList = this.computeConcavityRegions();
		for(ConcavityRegion cr:concavityRegionList)
		{
			cr.drawInformation();
		}
	//	IJ.log(concavityRegionList.size()+"");
		
	/*ArrayList<AbstractSplitLine> possibleSplitLines=*/this.computeSplitLines(ip);
//		this.selectBestSplitLine(possibleSplitLines);
	}

	/**
	 * computes the convexHull, using the boundary of the Clump
	 * 
	 * @return the convexhull
	 */

	private Polygon computeConvexHull(Polygon contour)
	{
		PolygonRoi pr = new PolygonRoi(contour, Roi.POLYGON);
		Polygon convexHull = pr.getConvexHull();
		
		return convexHull;
	}

	/*private Polygon computeConvexBubble(Polygon innerContour)
	{
		
	}*/
	/**
	 * computes the areas with high concavity
	 * 
	 * @param ip
	 *            ImageProcessor to mark the concavityPoints on the boundary
	 * 
	 * @return returns the valid concavityRegions detected in the Clump
	 */
	private ArrayList<ConcavityRegion> computeConcavityRegions()
	{
		ConcavityRegionAdministration cra = new ConcavityRegionAdministration(this);
		ArrayList<ConcavityRegion> concavityRegionList = cra.computeConcavityRegions();

	
		for (ConcavityRegion cr : concavityRegionList)
		{
			
			// marks the concavityPoint on the boundary
			if(Clump_Splitting.SHOWPIXELS)
			{
			cr.markMax();
			}
			else{
				Clump.overlaySplitPoints.clear();
			}
			cr.markMidPointOfConvexHull();
			if(Clump_Splitting.SHOWCONCAVITYDEPTH)
			{
			cr.markConcavityDepth();
			}
			else{
				
					//Clump.done=0;
					Clump.overlayConcavityDepth.clear();
				
			}

		}
		return concavityRegionList;

	}
	/**
	 * computes the SplitLines of the Clump
	 * 
	 * @param ip
	 *            ImageProcessor to draw the detectedSplitLine
	 */

	private ArrayList<AbstractSplitLine> computeSplitLines(ImageProcessor ip)
	{
		this.computeFirstAndSecondLargestConcavityDepth();
		ArrayList<AbstractSplitLine> possibleSplitLines=null;
		AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
		possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, this,ip);
		
				
		
				
			
		
	//	System.out.println(possibleSplitLines.get(0));
	//	IJ.log(possibleSplitLines.size()+"Anzahl trennungslinien");
	//	IJ.log(possibleSplitLines.get(0)+"Erste Stelle");
	//	System.out.println("PossibleSplitLineSize: " + possibleSplitLines.size());
		if(possibleSplitLines.size()==0)
		{
			Clump.STOP++;
		}
		else{
			if(possibleSplitLines.get(0)==null)
			{

				Clump.STOP++;
			}
		}
		for (AbstractSplitLine asl : possibleSplitLines)
		{
			if(asl!=null)
			{
			this.drawSplitLine(ip, asl);
			}
		}

		return possibleSplitLines;
	}

	/**
	 * draws the splitLine
	 * 
	 * @param ip
	 *            ImageProcessor to draw the SplitLine
	 * @param asl
	 *            SplitLine to draw
	 */
	private void drawSplitLine(ImageProcessor ip, AbstractSplitLine asl)
	{
		
		asl.drawLine(ip);
	}

	/**
	 * draws the ConvexHull
	 * 
	 * @param ip
	 *            ImageProcessor to draw the ConvexHull
	 */

	private void drawConvexHull(ImageProcessor ip)
	{
		PolygonRoi polygonRoi = new PolygonRoi(convexHull, Roi.POLYGON);


	      polygonRoi.setStrokeWidth(1);;
	   //  Roi.setColor(Color.cyan);
	      polygonRoi.setStrokeColor(Color.cyan);
	      overlayConvexHull.add(polygonRoi);
	      
	//	o.setStrokeColor(Color.red);
	//	o.addElement(polygonRoi);
		//ip.setOverlay(o);
		//ip.drawOverlay(o);
	//	ImageProcessor imapr=Clump_Splitting.imp.getProcessor();
	//	imapr.setOverlay(o);
	//	imapr.drawOverlay(o);
	//	Clump_Splitting.imp.setOverlay(o);
	//	o.
		//ip.setColor(Color.gray);
		//ip.setLineWidth(1);
		//ip.draw(polygonRoi);
	}

	/**
	 * computes the concavityRegions with the first and second largest concavity
	 * Depth and stores it to the variables indexOfMaxConcavityRegion and
	 * secondMaxConcavityDepth
	 */

	private void computeFirstAndSecondLargestConcavityDepth()
	{
		double[] max =
		{ 0, 0, 0, 0 };
		int i = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			if (cr.getMaxDist() >= max[0])
			{
				max[0] = cr.getMaxDist();
				max[1] = i;
			} else
			{
				if (cr.getMaxDist() >= max[2])
				{
					max[2] = cr.getMaxDist();
					max[3] = i;
				}
			}
			i++;
		}
		if (max[2] < Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
		{
			max[2] = Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD;
		}
		this.indexOfMaxConcavityRegion = (int) max[1];
		this.secondMaxConcavityDepth = max[2];

	}

	/**
	 * returns the concavityRegion with the largest ConcavityDepth computet by
	 * method computeFirstAndSecondLargestConcavityDepth
	 * 
	 * @return the concavityRegion with the largest ConcavityDepth of the Clump
	 */
	public ConcavityRegion getRegionOfMaxConcavityDepth()
	{

		return concavityRegionList.get(indexOfMaxConcavityRegion);
	}

	/**
	 * 
	 * 
	 * @return returns the concavityDepth of the concavityRegion with the second
	 *         Largest concavityDepth
	 */
	public double getSecondMaxConcavityRegionDepth()
	{
		return secondMaxConcavityDepth;
	}

	public Polygon getConvexHull()
	{
		return convexHull;
	}
	public Polygon getBoundary()
	{
		return boundary;
	}
	public ArrayList<InnerContour> getInnerContours()
	{
		return innerList;
	}
}
