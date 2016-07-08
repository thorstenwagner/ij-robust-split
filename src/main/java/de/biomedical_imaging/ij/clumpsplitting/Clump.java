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
	 * List of rois, which manages the overlay for the Orientation
	 */
	public static ArrayList<Roi> overlayForOrientation = new ArrayList<Roi>();
	public static ArrayList<Roi> overlayTextConvexHull = new ArrayList<Roi>();
	/**
	 * List of rois, which manages the overlay for the ConvexHulls
	 */
	public static ArrayList<Roi> overlayConvexHull = new ArrayList<Roi>();
	/**
	 * List of rois, which manages the overlay for the SplitPoints
	 */
	public static ArrayList<Roi> overlaySplitPoints = new ArrayList<Roi>();

	/**
	 * List of all innerContours of a Clump
	 */
	private ArrayList<InnerContour> innerList = new ArrayList<InnerContour>();
	/**
	 * List of all concavityRegions of a Clump
	 */
	private ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
	/**
	 * the boundary describes the outer border of the Clump
	 */
	private Polygon boundary;
	/**
	 * the convexHull is detected by Class ij.gui.PolygonRoi
	 */
	private Polygon convexHull;
	/**
	 * Counter of ready Clumps has to be zero at the beginning of each step,
	 * because all Clumps are detected at first. Counts the number of Clumps for
	 * which no possible splitlines can be found
	 */
	public static int STOP = 0;

	private ConcavityPixel largestConcavityPixel;
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
	 *            the outer boundary of the Clump
	 * @param innerContours
	 *            the boundary of all inner contours of a Clump (holes in the
	 *            Clump)
	 * @param ip
	 *            ImageProcessor to visualize the detected Points and lines of
	 *            the Image
	 */
	public Clump(Polygon boundary, ArrayList<Polygon> innerContours, ImageProcessor ip)
	{
		this.boundary = boundary;
		this.convexHull = this.computeConvexHull(boundary);
		if (convexHull != null)
		{
			for (Polygon innerContour : innerContours)
			{
				Polygon p = new Polygon();
				for (int i = innerContour.npoints - 1; i >= 0; i--)
				{
					p.addPoint(innerContour.xpoints[i], innerContour.ypoints[i]);
				}
				/*
				 * convex Hull of inner Contour to seperate the outstanding
				 * points of each inner Contour
				 */
				Polygon innerConvexHull = this.computeConvexHull(p);
				Polygon innerCH = new Polygon();
				for (int i = innerConvexHull.npoints - 1; i >= 0; i--)
				{
					innerCH.addPoint(innerConvexHull.xpoints[i], innerConvexHull.ypoints[i]);
				}
				InnerContour inner = new InnerContour(innerContour, innerCH);
				innerList.add(inner);
			}

			/*
			 * draws only one ConvexHull for each Clump which is detected in the
			 * original picture to recieve clarity
			 */
			if (Clump_Splitting.SHOWCONVEXHULL && Clump.done < Clump_Splitting.count)
			{
				this.drawConvexHull(ip);
				Clump.done++;
			} else
			{
				if (!Clump_Splitting.SHOWCONVEXHULL)
				{
					Clump.done = 0;
					Clump.overlayConvexHull.clear();
				}
			}
		}

		this.concavityRegionList.clear();
		/*
		 * computes all concavityRegions
		 */
		this.concavityRegionList = this.computeConcavityRegions();
		/*
		 * adds all ConcavityRegions to ConcavityRegionList to draw information
		 * about it
		 */
		for (ConcavityRegion cr : concavityRegionList)
		{
			if (!Clump.allRegions.contains(cr))
			{
				Clump.allRegions.add(cr);
			}

		}
		this.computeSplitLines(ip);
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
		// System.out.println(convexHull.npoints-1+ "aaaar");
		return convexHull;
	}

	/**
	 * computes the areas with high concavity
	 * 
	 * @return returns the valid concavityRegions detected in the Clump
	 */
	private ArrayList<ConcavityRegion> computeConcavityRegions()
	{
		ConcavityRegionAdministration cra = new ConcavityRegionAdministration(this);
		ArrayList<ConcavityRegion> concavityRegionList = cra.computeConcavityRegions();

		for (ConcavityRegion cr : concavityRegionList)
		{
			// System.out.println(cr.getMaxDistCoord().get(0));

			if (Clump_Splitting.SHOWPIXELS)
			{
				cr.markMax();
			} else
			{
				Clump.overlaySplitPoints.clear();
			}
			// cr.markMidPointOfConvexHull();

		}
		return concavityRegionList;

	}

	/**
	 * computes the SplitLines of the Clump to compute SplitLines SplitLineType
	 * has to be chosen
	 * 
	 * @param ip
	 *            ImageProcessor to draw the detectedSplitLine
	 */

	private ArrayList<AbstractSplitLine> computeSplitLines(ImageProcessor ip)
	{
		this.computeFirstAndSecondLargestConcavityDepth();
		ArrayList<AbstractSplitLine> possibleSplitLines = null;
		/*
		 * For SplitLineTypes 0-3 first the Concavitypixels of a
		 * StraightSplitLine are detected
		 */
		if (Clump_Splitting.SPLITLINETYPE == SplitLineType.STRAIGHTSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.MINIMUMINTENSITYSPLITLINE
				|| Clump_Splitting.SPLITLINETYPE == SplitLineType.GEODESICDISTANCESPLITLINE)
		{
			AbstractSplitLineCalculator sslc = new StraightSplitLineCalculator();
			possibleSplitLines = sslc.calculatePossibleSplitLines(concavityRegionList, this, ip);
		} else
		{
			if (Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINEFARHAN)
			{
				AbstractSplitLineCalculator mislcf = new MaximumIntensitySplitLineCalculatorFarhan();
				possibleSplitLines = mislcf.calculatePossibleSplitLines(concavityRegionList, this, ip);

			} else
			{
				if (Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINEFARHAN)
				{
					AbstractSplitLineCalculator mislcf = new MinimumIntensitySplitLineCalculatorFarhan();
					possibleSplitLines = mislcf.calculatePossibleSplitLines(concavityRegionList, this, ip);

				}
			}
		}

		// System.out.println(possibleSplitLines.get(0));
		// IJ.log(possibleSplitLines.size()+"Anzahl trennungslinien");
		// IJ.log(possibleSplitLines.get(0)+"Erste Stelle");
		// System.out.println("PossibleSplitLineSize: " +
		// possibleSplitLines.size());
		if (possibleSplitLines.size() == 0)
		{
			Clump.STOP++;
		} else
		{
			if (possibleSplitLines.get(0) == null)
			{

				Clump.STOP++;
			}
		}
		for (AbstractSplitLine asl : possibleSplitLines)
		{
			if (asl != null)
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

		polygonRoi.setStrokeWidth(1);
		;
		// Roi.setColor(Color.cyan);
		polygonRoi.setStrokeColor(Color.cyan);
		overlayConvexHull.add(polygonRoi);

		// o.setStrokeColor(Color.red);
		// o.addElement(polygonRoi);
		// ip.setOverlay(o);
		// ip.drawOverlay(o);
		// ImageProcessor imapr=Clump_Splitting.imp.getProcessor();
		// imapr.setOverlay(o);
		// imapr.drawOverlay(o);
		// Clump_Splitting.imp.setOverlay(o);
		// o.
		// ip.setColor(Color.gray);
		// ip.setLineWidth(1);
		// ip.draw(polygonRoi);
	}

	/**
	 * computes the concavityRegions with the first and second largest concavity
	 * Depth and stores it to the variables indexOfMaxConcavityRegion and
	 * secondMaxConcavityDepth
	 */

	private void computeFirstAndSecondLargestConcavityDepth()
	{
		ConcavityPixel maxDistPixel = null;

		double max = 0;
		double secondMax = 0;
		for (ConcavityRegion cr : concavityRegionList)
		{
			for (ConcavityPixel cp : cr.getConcavityPixelList())
			{

				if (cp.distance() >= max)
				{
					secondMax = max;
					maxDistPixel = cp;
					max = cp.distance();

				} else
				{
					if (cp.distance() >= secondMax)
					{
						secondMax = cp.distance();
					}
				}

			}
		}
		if (secondMax < Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
		{
			secondMax = Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD;
		}
		this.largestConcavityPixel = maxDistPixel;
		this.secondMaxConcavityDepth = secondMax;

	}

	/**
	 * returns the concavityRegion with the largest ConcavityDepth computet by
	 * method computeFirstAndSecondLargestConcavityDepth
	 * 
	 * @return the concavityRegion with the largest ConcavityDepth of the Clump
	 */
	public ConcavityPixel getPixelOfMaxConcavityDepth()
	{

		return this.largestConcavityPixel;
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
