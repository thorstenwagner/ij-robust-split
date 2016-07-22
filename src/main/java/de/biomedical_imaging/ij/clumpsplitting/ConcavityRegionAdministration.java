/*

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

import ij.process.ImageProcessor;

/**
 * administrates the concavityRegions
 * 
 * @author Louise
 *
 */
public class ConcavityRegionAdministration
{
	private Clump clump;
	public static ArrayList<Point2D> allConcavityRegionPoints = new ArrayList<Point2D>();

	/**
	 * 
	 * @param boundaryArc
	 *            bounds of the affiliated Clump
	 * @param convexHull
	 *            convexHull of the affiliated Clump
	 */
	public ConcavityRegionAdministration(Clump clump)
	{
		this.clump = clump;

	}

	

	/**
	 * Computes the ConcavityRegions of the inner Contour by locally check if a
	 * Contour has a ConcavityRegion by analyse if the Midpoint of a Line
	 * Between point at positions i+konstante, i-konstante contains to the
	 * Contour or doesn't. Points has to be examined if they are discretesation
	 * artifacts
	 * 
	 * Result depends on the choosed ConcavityPixelDetectorType
	 * 
	 * @param binary
	 *            ImageProcessor of the Binarized image
	 * @return List of all detected inner ConcavityRegions
	 */
	private ArrayList<ConcavityRegion> computeInnerConcavityRegions(ImageProcessor binary)
	{
		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		int konstante = Clump_Splitting.INNERCONTOURPARAMETER;
		/*
		 * for each inner Contour the ConcavityRegions should be detected
		 */
		for (InnerContour inner : clump.getInnerContours())
		{
			boolean switched = false;
			boolean isBackground = false;

			boolean isStarted = false;
			ArrayList<Point2D> intList = new ArrayList<Point2D>();
			/*
			 * analyse the Contour if it has localy Corners. It isn't sufficient
			 * to check if there are Convex Parts because they can also be
			 * formed nearly straight between the edges, but are ConcavityPixel
			 * as well.
			 * 
			 * START "ConcavityRegion" if the midpoint of Line between Points
			 * i+konstante i-konstante contains to the polygon given by the
			 * inner Contour END "ConcavityRegion" at the first point where
			 * Midpoint isn't part of the polygon
			 */
			for (int i = 0; i < inner.getContour().npoints - 1; i++)
			{
				Point2D iMinus = null;
				Point2D iPlus = null;
				if (inner.getContour().npoints > 2 * konstante)
				{
					if (i < konstante)
					{
						iMinus = new Point2D.Double(
								inner.getContour().xpoints[inner.getContour().npoints - (konstante + 2) + i],
								inner.getContour().ypoints[inner.getContour().npoints - (konstante + 2) + i]);
						iPlus = new Point2D.Double(inner.getContour().xpoints[i + konstante],
								inner.getContour().ypoints[i + konstante]);
					} else
					{
						if (i + konstante > inner.getContour().npoints - 1)
						{
							iMinus = new Point2D.Double(inner.getContour().xpoints[i - konstante],
									inner.getContour().ypoints[i - konstante]);
							iPlus = new Point2D.Double(
									inner.getContour().xpoints[i + konstante - (inner.getContour().npoints - 1)],
									inner.getContour().ypoints[i + konstante - (inner.getContour().npoints - 1)]);
						} else
						{
							iMinus = new Point2D.Double(inner.getContour().xpoints[i - konstante],
									inner.getContour().ypoints[i - konstante]);
							iPlus = new Point2D.Double(inner.getContour().xpoints[i + konstante],
									inner.getContour().ypoints[i + konstante]);
						}
					}

					double xDist = iMinus.getX() + iPlus.getX();
					double yDist = iMinus.getY() + iPlus.getY();
					Point2D midPoint = new Point2D.Double(Math.round((xDist / 2)), Math.round((yDist / 2)));
					if (Clump_Splitting.BACKGROUNDCOLOR == 0)
					{
						if (binary.getPixel((int) midPoint.getX(), (int) midPoint.getY()) == 0)
						{
							if (isBackground == false)
							{
								isBackground = true;
								switched = true;
								isStarted = true;
							}

						} else
						{
							if (this.pixelContainsContour(inner.getContour(), midPoint))
							{

								if (isBackground == true)
								{
									isBackground = false;
									switched = true;
									isStarted = false;
								}

							} else

							{
								if (isBackground == true)
								{
									isBackground = false;
									switched = true;
									isStarted = false;
								}
							}
						}
					} else
					{
						if (binary.getPixel((int) midPoint.getX(), (int) midPoint.getY()) == 255)
						{
							if (isBackground == false)
							{
								isBackground = true;
								switched = true;
								isStarted = true;
							}

						} else
						{
							if (this.pixelContainsContour(inner.getContour(), midPoint))
							{

								if (isBackground == true)
								{
									isBackground = false;
									switched = true;
									isStarted = false;
								}

							} else
							{
								if (isBackground == true)
								{
									isBackground = false;
									switched = true;
									isStarted = false;
								}
							}
						}
					}
					if (switched == true)
					{

						if (isStarted == true)
						{
							ArrayList<Point2D> embeddedPoints = this.getAllEmbeddedPointsFromInnerContour(
									(int) iMinus.getX(), (int) iMinus.getY(), (int) iPlus.getX(), (int) iPlus.getY(),
									inner);
							ArrayList<Double> distList = this.computeDistance(embeddedPoints, (int) iMinus.getX(),
									(int) iMinus.getY(), (int) iPlus.getX(), (int) iPlus.getY());
							double max = 0;
							for (int p = 0; p < distList.size(); p++)
							{
								if (distList.get(p) > max)
								{
									max = distList.get(p);
								}
							}
							if (max > 1)
							{
								intList.add(iMinus);
							} else
							{
								isStarted = false;
								isBackground = false;
							}
						} else
						{
							intList.add(iPlus);
						}
						switched = false;

					}
				}
			}
			/*
			 * reduce Points, to reduce Discretation artifacts
			 */
			ArrayList<Point2D> pointsList = new ArrayList<Point2D>();
			for (int n = 0; n < intList.size(); n++)
			{
				int position1;
				int position2;
				if (n == 0)
				{
					position1 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
							(int) intList.get(intList.size() - 1).getX(), (int) intList.get(intList.size() - 1).getY());
					position2 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
							(int) intList.get(n).getX(), (int) intList.get(n).getY());

				} else
				{
					position1 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
							(int) pointsList.get(pointsList.size() - 1).getX(),
							(int) pointsList.get(pointsList.size() - 1).getY());
					position2 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
							(int) intList.get(n).getX(), (int) intList.get(n).getY());

				}
				if ((Math.abs(position1 - position2) <= konstante))
				{
					if (pointsList.size() > 0)
					{
						pointsList.remove(pointsList.size() - 1);
						Point2D midPoint = new Point2D.Double(
								inner.getContour().xpoints[(int) Math.round((position1 + position2) / 2)],
								inner.getContour().ypoints[(int) Math.round((position1 + position2) / 2)]);
						pointsList.add(midPoint);
					} else
					{
						Point2D midPoint = new Point2D.Double(
								inner.getContour().xpoints[(int) Math.round((position1 + position2) / 2)],
								inner.getContour().ypoints[(int) Math.round((position1 + position2) / 2)]);
						pointsList.add(midPoint);
					}
				} else
				{
					pointsList.add(intList.get(n));
				}
			}
			if (pointsList.size() > 1)
			{
				int position1 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
						(int) pointsList.get(0).getX(), (int) intList.get(0).getY());
				int position2 = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
						(int) pointsList.get(pointsList.size() - 1).getX(),
						(int) intList.get(pointsList.size() - 1).getY());
				if ((Math.abs(position1 - position2) <= konstante))
				{
					pointsList.remove(0);
					pointsList.remove(pointsList.size() - 1);
					Point2D midPoint = new Point2D.Double(
							inner.getContour().xpoints[(int) Math.round((position1 + position2) / 2)],
							inner.getContour().ypoints[(int) Math.round((position1 + position2) / 2)]);
					pointsList.add(midPoint);

				}
			}
			/**
			 * reduce Points to get Maximum ConcavityDepth of the Regions
			 */
			ArrayList<Point2D> reducedPointList = new ArrayList<Point2D>();
			for (int l = 0; l < pointsList.size(); l++)
			{
				Point2D punkt = null;
				Point2D punkt2 = null;
				if (reducedPointList.size() <= 0)
				{
					punkt = pointsList.get(pointsList.size() - 1);
					punkt2 = pointsList.get(0);
				} else
				{
					punkt = reducedPointList.get(reducedPointList.size() - 1);
					punkt2 = pointsList.get(l);

				}

				int xDist = (int) Math.round((punkt.getX() + punkt2.getX()) / 2);
				int yDist = (int) Math.round((punkt.getY() + punkt2.getY()) / 2);
				Point2D midPoint = new Point2D.Double(xDist, yDist);
				if (Clump_Splitting.BACKGROUNDCOLOR == 0)
				{
					if (binary.getPixel((int) Math.round(midPoint.getX()), (int) Math.round(midPoint.getY())) != 0)
					{
						if (reducedPointList.size() > 0)
						{
							reducedPointList.remove(reducedPointList.size() - 1);
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt.getX(), (int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt2.getX(), (int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						} else
						{
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt.getX(), (int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt2.getX(), (int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						}

					} else
					{
						reducedPointList.add(punkt2);
					}
				} else
				{
					if (binary.getPixel((int) Math.round(midPoint.getX()), (int) Math.round(midPoint.getY())) != 255)
					{
						if (reducedPointList.size() > 0)
						{
							reducedPointList.remove(reducedPointList.size() - 1);
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt.getX(), (int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt2.getX(), (int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						} else
						{
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt.getX(), (int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner.getContour(),
									(int) punkt2.getX(), (int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						}

					} else
					{
						reducedPointList.add(punkt2);
					}
				}
			}
			if (reducedPointList.size() > 0)
			{
				Point2D punkt = reducedPointList.get(reducedPointList.size() - 1);
				Point2D punkt2 = reducedPointList.get(0);

				int xDist = (int) Math.round((punkt.getX() + punkt2.getX()) / 2);
				int yDist = (int) Math.round((punkt.getY() + punkt2.getY()) / 2);
				Point2D midPoint = new Point2D.Double(xDist, yDist);

				if (Clump_Splitting.BACKGROUNDCOLOR == 0)
				{
					if (binary.getPixel((int) Math.round(midPoint.getX()), (int) Math.round(midPoint.getY())) != 0)
					{
						if (reducedPointList.size() > 0)
						{
							reducedPointList.remove(0);
						}

					}
				} else
				{
					if (binary.getPixel((int) Math.round(midPoint.getX()), (int) Math.round(midPoint.getY())) != 255)
					{
						if (reducedPointList.size() > 0)
						{
							reducedPointList.remove(0);

						}
					}

				}
				/*
				 * construct ConcavityRegions for inner Contour
				 */
				for (int k = 0; k < reducedPointList.size(); k++)
				{
					int innerStartX = 0;
					int innerEndX = 0;
					int innerStartY = 0;
					int innerEndY = 0;
					if (k == 0)
					{
						innerStartX = (int) reducedPointList.get(reducedPointList.size() - 1).getX();
						innerStartY = (int) reducedPointList.get(reducedPointList.size() - 1).getY();
						innerEndX = (int) reducedPointList.get(k).getX();
						innerEndY = (int) reducedPointList.get(k).getY();
					} else
					{

						innerStartX = (int) reducedPointList.get(k - 1).getX();
						innerStartY = (int) reducedPointList.get(k - 1).getY();
						innerEndX = (int) reducedPointList.get(k).getX();
						innerEndY = (int) reducedPointList.get(k).getY();

					}

					if (this.getPositionOfConvexHullPointAtBoundary(inner.getContour(), innerStartX, innerStartY) < this
							.getPositionOfConvexHullPointAtBoundary(inner.getContour(), innerEndX, innerEndY) || k == 0)
					{
						ArrayList<Point2D> pointList = getAllEmbeddedPointsFromInnerContour(innerStartX, innerStartY,
								innerEndX, innerEndY, inner);
						if (pointList.size() > 3)
						{
							ArrayList<Double> doubleList = computeDistance(pointList, innerStartX, innerStartY,
									innerEndX, innerEndY);
							AbstractConcavityPixelDetector ldcpd = null;
							if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS)
							{
								ldcpd = new AllConcavityPixelDetector();
							} else
							{
								if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH)
								{
									ldcpd = new LargestDistanceConcavityPixelDetector();
								}
							}
							ConcavityRegion concavityRegion = new ConcavityRegion(innerStartX, innerStartY, innerEndX,
									innerEndY, pointList, doubleList);

							ArrayList<ConcavityPixel> concavityPixelList = ldcpd.computeConcavityPixel(concavityRegion);

							if (concavityPixelList.size() > 0)
							{
								concavityRegion.setConcavityPixelList(concavityPixelList);

								concavityRegionList.add(concavityRegion);
							}
						}
					}
				}
			}
		}
		return concavityRegionList;

	}

	/**
	 * computes the valid concavityRegions of a Clump. A concavityRegion is a
	 * valid ConcavityRegion if the largest ConcavityDepth is larger than the
	 * CONCAVITY_DEPTH_THRESHOLD
	 * 
	 * @param binary
	 *            binarized and preprocessed image
	 * @return List of all valid concavityRegions
	 */

	public ArrayList<ConcavityRegion> computeConcavityRegions(ImageProcessor binary)
	{
		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		AbstractOuterConcavityRegionDetector aocrd= null;
		if(Clump_Splitting.OUTERCONCAVITYREGIONDETECTORTYPE==OuterConcavityRegionDetectorType.DETECTOUTERCONCAVITYREGIONSLOCAL)
		{
		aocrd= new LocalOuterConcavityRegionDetector();	
		}
		else{
			aocrd= new ConvexHullOuterConcavityRegionDetector();
		}
		concavityRegionList.addAll(aocrd.computeOuterConcavityRegions(binary, clump));
		concavityRegionList.addAll(this.computeInnerConcavityRegions(binary));
		return concavityRegionList;

	}

	/**
	 * controls if a pixel contains the BondaryContour
	 * 
	 * @param contour
	 *            Contour
	 * @param midPoint
	 *            Point to find into Contour
	 * @return true if pixel contains Contour, else: false
	 */

	private boolean pixelContainsContour(Polygon contour, Point2D midPoint)
	{

		for (int i = 0; i < contour.npoints; i++)
		{
			if (contour.xpoints[i] == midPoint.getX() && contour.ypoints[i] == midPoint.getY())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * computes the Index of a Point at a Boundary
	 * 
	 * @param polygon
	 *            polygon, which contains the boundary Points
	 * @param x
	 *            Coordinate of specified Point
	 * @param y
	 *            Coordinate of specified Point
	 * @return index of the Point with regard to the Polygon
	 */
	private int getPositionOfConvexHullPointAtBoundary(Polygon polygon, double x, double y)
	{
		for (int i = 0; i < polygon.npoints; i++)
		{
			if (polygon.xpoints[i] == x && polygon.ypoints[i] == y)
			{
				return i;
			}
		}
		return 0;
	}

	

	/**
	 * computes all Points, which are on the boundary of an inner contour and
	 * are embedded from the start and end Coordinates. Note: The convexHull is
	 * defined counterClockWise, the outerContours of a Blob use only for inner
	 * Contours they are computed counterclockwise, in this case i has to
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
	 * 
	 * @param innerContour
	 *            Boundary Contour of the inner Contour
	 * @return ArrayList of all embedded Points
	 */
	private ArrayList<Point2D> getAllEmbeddedPointsFromInnerContour(int startX, int startY, int endX, int endY,
			InnerContour innerContour)
	{
		int i = 0;
		boolean ended = false;
		boolean started = false;
		ArrayList<Point2D> pointList = new ArrayList<Point2D>();
		while (i < innerContour.getContour().npoints - 1 && !ended)
		{
			if (innerContour.getContour().xpoints[i] == startX && innerContour.getContour().ypoints[i] == startY
					&& !started)
			{
				pointList.add(
						new Point2D.Double(innerContour.getContour().xpoints[i], innerContour.getContour().ypoints[i]));
				started = true;
			} else
			{

				if (started && (innerContour.getContour().xpoints[i] != endX
						|| innerContour.getContour().ypoints[i] != endY) && !ended)
				{
					pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i],
							innerContour.getContour().ypoints[i]));

				} else
				{
					if (started && innerContour.getContour().xpoints[i] == endX
							&& innerContour.getContour().ypoints[i] == endY)
					{
						pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i],
								innerContour.getContour().ypoints[i]));
						ended = true;
					}

				}
			}
			i++;
		}
		if (!ended)
		{
			i = 0;
			while (i < innerContour.getContour().npoints - 1 && !ended)
			{
				if (started && (innerContour.getContour().xpoints[i] != endX
						|| innerContour.getContour().ypoints[i] != endY) && !ended)
				{
					pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i],
							innerContour.getContour().ypoints[i]));

				} else
				{
					if (started && innerContour.getContour().xpoints[i] == endX
							&& innerContour.getContour().ypoints[i] == endY)
					{
						pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i],
								innerContour.getContour().ypoints[i]));
						ended = true;
					}

				}
				i++;
			}
		}
		return pointList;

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
			dist = Math.sqrt(line.ptSegDistSq(point));
			// dist=Math.abs(line.ptLineDist(point));
			doubleList.add(dist);
		}
		return doubleList;
	}

}
