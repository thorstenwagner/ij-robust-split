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

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * administrates the concavityRegions
 * 
 * @author Louise
 *
 */
public class ConcavityRegionAdministration
{
	/**
	 * represents the bounds of the affiliated Clump
	 */
	private Polygon boundaryArc;
	/**
	 * represents the convexHull of the affiliated Clump
	 */
	private Polygon convexHull;

	

	

	/**
	 * 
	 * @param boundaryArc
	 *            bounds of the affiliated Clump
	 * @param convexHull
	 *            convexHull of the affiliated Clump
	 */
	public ConcavityRegionAdministration(Polygon boundaryArc, Polygon convexHull)
	{
		this.boundaryArc = boundaryArc;
		this.convexHull = convexHull;
	}

	/**
	 * computes the valid concavityRegions of a Clump. A concavityRegion is a
	 * valid ConcavityRegion if the largest ConcavityDepth is larger than the
	 * CONCAVITY_DEPTH_THRESHOLD
	 * 
	 * @return List of all valid concavityRegions
	 */
	public ArrayList<ConcavityRegion> computeConcavityRegions()
	{
		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		int startX;
		int startY;
		int endX;
		int endY;
		for (int i = 1; i < convexHull.npoints; i++)
		{
			startX = convexHull.xpoints[i - 1];
			startY = convexHull.ypoints[i - 1];
			endX = convexHull.xpoints[i];
			endY = convexHull.ypoints[i];

			ArrayList<Point2D> pointList = getAllEmbeddedPointsFromBoundaryArc(startX, startY, endX, endY);
			ArrayList<Double> doubleList = computeDistance(pointList, startX, startY, endX, endY);
			double[] maxData = getMaxDist(doubleList);
			if (maxData[0] > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
			{
				ConcavityRegion concavityRegion = new ConcavityRegion(startX, startY, endX, endY, pointList, doubleList,
						maxData[0], (int) maxData[1]);
				concavityRegionList.add(concavityRegion);
			}

		}
		return concavityRegionList;
	}

	/**
	 * computes all Points, which are on the boundary and are embedded from the
	 * start and end Coordinates. Note: The convexHull is definded
	 * counterClockWise, the outerContours of a Blob clockwise don't use for
	 * inner Contours they are computed counterclockwise, in this case i has to
	 * increase from 0 to boundaryArc.npoints
	 * 
	 * @param startX
	 *            X-Coordinate of the startValue of the Possible ConcavityRegion
	 * @param startY
	 *            Y-Coordinate of the startValue of the Possible ConcavityRegion
	 * @param endX
	 *            X-Coordinate of the endValue of the Possible ConcavityRegion
	 * @param endY
	 *            Y-Coordinate of the endValue of the Possible ConcavityRegion
	 * @return List with all embedded Points on the grid
	 */
	private ArrayList<Point2D> getAllEmbeddedPointsFromBoundaryArc(int startX, int startY, int endX, int endY)
	{
		int i = boundaryArc.npoints - 1;
		boolean ended = false;
		boolean started = false;
		ArrayList<Point2D> pointList = new ArrayList<Point2D>();
		while (i >= 0 && !ended)
		{
			if (boundaryArc.xpoints[i] == startX && boundaryArc.ypoints[i] == startY && !started)
			{
				pointList.add(new Point2D.Double(boundaryArc.xpoints[i], boundaryArc.ypoints[i]));
				started = true;
			} else
			{

				if (started && (boundaryArc.xpoints[i] != endX || boundaryArc.ypoints[i] != endY) && !ended)
				{
					pointList.add(new Point2D.Double(boundaryArc.xpoints[i], boundaryArc.ypoints[i]));

				} else
				{
					if (started && boundaryArc.xpoints[i] == endX && boundaryArc.ypoints[i] == endY)
					{
						pointList.add(new Point2D.Double(boundaryArc.xpoints[i], boundaryArc.ypoints[i]));
						ended = true;
					}

				}
			}
			i--;
		}
		return pointList;

	}

	public void setConvexHull(Polygon convexHull)
	{
		this.convexHull = convexHull;
	}

	public void setBoundaryArc(Polygon boundaryArc)
	{
		this.boundaryArc = boundaryArc;
	}

	/**
	 * computes the concavityDepth of all points on the boundaryList
	 * 
	 * @param boundaryPointList
	 *            List with all points embedded by the boundary of the Clump and
	 *            contains to the possible concavityRegion
	 * @param startX
	 *            x-Coordinate of the start-Point of the possible
	 *            ConcavityRegion
	 * @param startY
	 *            y-Coordinate of the start-Point of the possible
	 *            ConcavityRegion
	 * @param endX
	 *            x-Coordinate of the end-Point of the possible ConcavityRegion
	 * @param endY
	 *            y-Coordinate of the end-Point of the possible ConcavityRegion
	 * @return List with all concavityDepth of the possible ConcavityRegion
	 */
	private ArrayList<Double> computeDistance(ArrayList<Point2D> boundaryPointList, int startX, int startY, int endX,
			int endY)
	{
		ArrayList<Double> doubleList = new ArrayList<Double>();
		Line2D.Double line = new Line2D.Double(startX, startY, endX, endY);
		Double dist;
		for (Point2D point : boundaryPointList)
		{
			dist = line.ptSegDistSq(point);
			// dist=Math.abs(line.ptLineDist(point));
			doubleList.add(dist);
		}
		return doubleList;
	}

	/**
	 * filters the List for the largest Value
	 * 
	 * @param distList
	 *            List with all concavityDepths of the possible ConcavityRegion
	 * @return array with length=2 of type double array[0]= largest
	 *         concavityDepth of the possible ConcavityRegion, array[1]= index
	 *         of the Point with the largest concavityDepth based on the input
	 *         List
	 */
	private double[] getMaxDist(ArrayList<Double> distList)
	{
		//int indexMax = 0;
		double max = 0;
		int index = 0;
		ArrayList<Integer> moreThanOneMax= new ArrayList<Integer>();
		for (double d : distList)
		{
			if(d>=max)
			{
				if (d > max)
				{
					moreThanOneMax.clear();
					moreThanOneMax.add(index);
					max = d;
					//indexMax = index;
				}
				else{
					if(d==max)
					{
						moreThanOneMax.add(index);
						max=d;
					}
				}
			}
			index++;
		}
		int indexMax=0;
		if(moreThanOneMax.size()>0)
		{
			indexMax=moreThanOneMax.get((moreThanOneMax.size()/2));
		}
		double[] tmp =
		{ max, indexMax };
		return tmp;
	}

}
