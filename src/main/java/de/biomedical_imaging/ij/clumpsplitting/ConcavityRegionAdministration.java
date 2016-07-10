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

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.gui.Line;
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
	 * computes the valid concavityRegions of a Clump. A concavityRegion is a
	 * valid ConcavityRegion if the largest ConcavityDepth is larger than the
	 * CONCAVITY_DEPTH_THRESHOLD
	 * 
	 * @return List of all valid concavityRegions
	 */
	public ArrayList<ConcavityRegion> computeConcavityRegions(ImageProcessor binary)
	{
		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		int startX;
		int startY;
		int endX;
		int endY;
		if (clump.getConvexHull() != null)
		{
			for (int i = 1; i < clump.getConvexHull().npoints; i++)
			{
				startX = clump.getConvexHull().xpoints[i - 1];
				startY = clump.getConvexHull().ypoints[i - 1];
				endX = clump.getConvexHull().xpoints[i];
				endY = clump.getConvexHull().ypoints[i];

				ArrayList<Point2D> pointList = getAllEmbeddedPointsFromBoundaryArc(startX, startY, endX, endY);
				ConcavityRegionAdministration.allConcavityRegionPoints.addAll(pointList);
				if (pointList.size() > 3)
				{
					ArrayList<Double> doubleList = computeDistance(pointList, startX, startY, endX, endY);
					AbstractConcavityPixelDetector acpd = null;
					if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS)
					{
						acpd = new AllConcavityPixelDetector();
					} else
					{
						if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH)
						{
							acpd = new LargestDistanceConcavityPixelDetector();
						}
					}

					ConcavityRegion concavityRegion = new ConcavityRegion(startX, startY, endX, endY, pointList,
							doubleList);
					ArrayList<ConcavityPixel> concavityPixelList = acpd.computeConcavityPixel(concavityRegion);
					if (concavityPixelList.size() > 0)
					{
						concavityRegion.setConcavityPixelList(concavityPixelList);
						concavityRegionList.add(concavityRegion);
					}
				}

			}
		}
		// System.out.println(concavityRegionList.size());
		// IJ.log("Anzahl ohne innere"+ concavityRegionList.size());
		/*
		 * for (InnerContour inner : clump.getInnerContours()) { Polygon
		 * innerConvexHull = inner.getConvexHull(); /* int innerStartX; int
		 * innerStartY; int innerEndX; int innerEndY;
		 */
		/*
		 * int concavityPointY;
		 * 
		 * int concavityPointX;
		 * 
		 * if (innerConvexHull != null) { ArrayList<Point2D> pointListI = new
		 * ArrayList<Point2D>();
		 * 
		 * for (int i = 0; i < innerConvexHull.npoints - 1; i++) {
		 * 
		 * if (i == 0) { Point2D iMinus1 = new
		 * Point2D.Double(innerConvexHull.xpoints[innerConvexHull.npoints - 2],
		 * innerConvexHull.ypoints[innerConvexHull.npoints - 2]); Point2D iPlus1
		 * = new Point2D.Double(innerConvexHull.xpoints[i + 1],
		 * innerConvexHull.ypoints[i + 1]); Point2D iPoint = new
		 * Point2D.Double(innerConvexHull.xpoints[i],
		 * innerConvexHull.ypoints[i]); Line2D gerade = new
		 * Line2D.Double(iMinus1, iPlus1);
		 * 
		 * System.out.println("Anfang" + gerade.ptLineDist(iPoint)); if
		 * (gerade.ptLineDist(iPoint) > 0.98) { Line lineRoi = new
		 * Line(iPoint.getX(), iPoint.getY(), iPoint.getX(), iPoint.getY());
		 * lineRoi.setStrokeColor(Color.green); lineRoi.setStrokeWidth(4);
		 * 
		 * Clump.boundaryOverlay.add(lineRoi); pointListI.add(iPoint); } } else
		 * { if (i == innerConvexHull.npoints - 2) { Point2D iMinus1 = new
		 * Point2D.Double(innerConvexHull.xpoints[i - 1],
		 * innerConvexHull.ypoints[i - 1]); Point2D iPlus1 = new
		 * Point2D.Double(innerConvexHull.xpoints[0],
		 * innerConvexHull.ypoints[0]); Point2D iPoint = new
		 * Point2D.Double(innerConvexHull.xpoints[i],
		 * innerConvexHull.ypoints[i]); Line2D gerade = new
		 * Line2D.Double(iMinus1, iPlus1);
		 * 
		 * System.out.println("Ende" + gerade.ptLineDist(iPoint));
		 * 
		 * if (gerade.ptLineDist(iPoint) > 0.98) { Line lineRoi = new
		 * Line(iPoint.getX(), iPoint.getY(), iPoint.getX(), iPoint.getY());
		 * lineRoi.setStrokeColor(Color.green); lineRoi.setStrokeWidth(4);
		 * 
		 * Clump.boundaryOverlay.add(lineRoi); pointListI.add(iPoint); } } else
		 * { Point2D iMinus1 = new Point2D.Double(innerConvexHull.xpoints[i -
		 * 1], innerConvexHull.ypoints[i - 1]); Point2D iPlus1 = new
		 * Point2D.Double(innerConvexHull.xpoints[i + 1],
		 * innerConvexHull.ypoints[i + 1]); Point2D iPoint = new
		 * Point2D.Double(innerConvexHull.xpoints[i],
		 * innerConvexHull.ypoints[i]); Line2D gerade = new
		 * Line2D.Double(iMinus1, iPlus1); System.out.println("Mitte" +
		 * gerade.ptLineDist(iPoint));
		 * 
		 * if (gerade.ptLineDist(iPoint) > 0.98) { Line lineRoi = new
		 * Line(iPoint.getX(), iPoint.getY(), iPoint.getX(), iPoint.getY());
		 * lineRoi.setStrokeColor(Color.green); lineRoi.setStrokeWidth(4);
		 * Clump.boundaryOverlay.add(lineRoi); pointListI.add(iPoint); } } } }
		 * //
		 * 
		 */
		// System.out.println(pointList.size() + "PointListSize"); if
		/*
		 * (inner.getContour().npoints > 10) { for (int i = 0; i <
		 * pointList.size(); i++) { concavityPointX = (int)
		 * pointList.get(i).getX(); concavityPointY = (int)
		 * pointList.get(i).getY(); int position =
		 * this.getPositionOfConvexHullPointAtBoundary(inner, concavityPointX,
		 * concavityPointY); // int position = //
		 * this.getPositionOfConvexHullPointAtBoundary(inner, //
		 * concavityPointX, concavityPointY);
		 * 
		 * int startPointConvexHullx; int startPointConvexHully; int
		 * endPointConvexHullx; int endPointConvexHully; if (position - 10 >= 0)
		 * { startPointConvexHullx = inner.getContour().xpoints[position - 10];
		 * startPointConvexHully = inner.getContour().ypoints[position - 10]; }
		 * else { // System.out.println(inner.getContour().npoints + " " +
		 * position); startPointConvexHullx =
		 * inner.getContour().xpoints[inner.getContour().npoints + (position -
		 * 10)];
		 * 
		 * startPointConvexHully =
		 * inner.getContour().ypoints[inner.getContour().npoints + (position -
		 * 10)]; }
		 * 
		 * if (position + 10 < inner.getContour().npoints) { endPointConvexHullx
		 * = inner.getContour().xpoints[position + 10]; endPointConvexHully =
		 * inner.getContour().ypoints[position + 10];
		 * 
		 * } else { endPointConvexHullx = inner.getContour().xpoints[position +
		 * 10 - inner.getContour().npoints]; endPointConvexHully =
		 * inner.getContour().ypoints[position + 10 -
		 * inner.getContour().npoints];
		 * 
		 * } ArrayList<Point2D> boundaryList =
		 * this.getAllEmbeddedPointsFromInnerContour( startPointConvexHullx,
		 * startPointConvexHully, endPointConvexHullx, endPointConvexHully,
		 * inner); ArrayList<Double> distanceList =
		 * this.computeDistance(boundaryList, startPointConvexHullx,
		 * startPointConvexHully, endPointConvexHullx, endPointConvexHully); //
		 * ArrayList<ConcavityPixel> concavityPixelList= new //
		 * ArrayList<ConcavityPixel>();
		 * 
		 * if (distanceList.get(9) > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
		 * { // System.out.println(distanceList.get(9)); //
		 * System.out.println(this.getMaxDist(distanceList)); if
		 * (distanceList.get(9) - 0.0001 < this.getMaxDist(distanceList) &&
		 * distanceList.get(9) + 0.0001 > this.getMaxDist(distanceList)) {
		 * ConcavityRegion cr = new ConcavityRegion(startPointConvexHullx,
		 * startPointConvexHully, endPointConvexHullx, endPointConvexHully,
		 * boundaryList, distanceList);
		 * 
		 * ConcavityPixel cp = new ConcavityPixel( new
		 * Point2D.Double(concavityPointX, concavityPointY),
		 * distanceList.get(9), cr); cr.addConcavityPixel(cp);
		 * concavityRegionList.add(cr); } } } }
		 */
		// IJ.log("convexHullPoints"+innerConvexHull.npoints);

		///////////////////////////////////////////////////////////////////////
		int konstante = 2;

		for (InnerContour inner : clump.getInnerContours())
		{
			System.out.println("------------------------------------------------" + concavityRegionList.size());
			boolean switched = false;
			boolean isBackground = false;

			boolean isStarted = false;
			ArrayList<Point2D> intList = new ArrayList<Point2D>();
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

			ArrayList<Point2D> pointsList = new ArrayList<Point2D>();
			for (int n = 0; n < intList.size(); n++)
			{
				int position1;
				int position2;
				if (n == 0)
				{
					position1 = this.getPositionOfConvexHullPointAtBoundary(inner,
							(int) intList.get(intList.size() - 1).getX(), (int) intList.get(intList.size() - 1).getY());
					position2 = this.getPositionOfConvexHullPointAtBoundary(inner, (int) intList.get(n).getX(),
							(int) intList.get(n).getY());

				} else
				{
					position1 = this.getPositionOfConvexHullPointAtBoundary(inner,
							(int) pointsList.get(pointsList.size() - 1).getX(),
							(int) pointsList.get(pointsList.size() - 1).getY());
					position2 = this.getPositionOfConvexHullPointAtBoundary(inner, (int) intList.get(n).getX(),
							(int) intList.get(n).getY());

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
				int position1 = this.getPositionOfConvexHullPointAtBoundary(inner, (int) pointsList.get(0).getX(),
						(int) intList.get(0).getY());
				int position2 = this.getPositionOfConvexHullPointAtBoundary(inner,
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
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt.getX(),
									(int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt2.getX(),
									(int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						} else
						{
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt.getX(),
									(int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt2.getX(),
									(int) punkt2.getY());
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
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt.getX(),
									(int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt2.getX(),
									(int) punkt2.getY());
							int indexmid = (int) Math.round((ersterPunkt + zweiterPunkt) / 2);
							Point2D middlePoint = new Point2D.Double(inner.getContour().xpoints[indexmid],
									inner.getContour().ypoints[indexmid]);

							reducedPointList.add(middlePoint);
						} else
						{
							int ersterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt.getX(),
									(int) punkt.getY());
							int zweiterPunkt = this.getPositionOfConvexHullPointAtBoundary(inner, (int) punkt2.getX(),
									(int) punkt2.getY());
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
					/*
					 * Line l = new Line(iPlus.getX(), iPlus.getY(),
					 * iPlus.getX(), iPlus.getY()); l.setStrokeWidth(3);
					 * l.setStrokeColor(Color.green);
					 * Clump.overlayForOrientation.add(l);
					 * System.out.println(iPlus.getX() + " " + iPlus.getY());
					 * System.out.println("Ende");
					 * 
					 */

					innerStartX = (int) reducedPointList.get(k - 1).getX();
					innerStartY = (int) reducedPointList.get(k - 1).getY();
					innerEndX = (int) reducedPointList.get(k).getX();
					innerEndY = (int) reducedPointList.get(k).getY();

				}

				if (this.getPositionOfConvexHullPointAtBoundary(inner, innerStartX, innerStartY) < this
						.getPositionOfConvexHullPointAtBoundary(inner, innerEndX, innerEndY) || k == 0)
				{
					ArrayList<Point2D> pointList = getAllEmbeddedPointsFromInnerContour(innerStartX, innerStartY,
							innerEndX, innerEndY, inner); //
					// IJ.log(innerStartX+ " "+innerStartY+ " "+innerEndX+ " //
					// "+innerEndY+ " "+pointList.size());

					if (pointList.size() > 3)
					{
						ArrayList<Double> doubleList = computeDistance(pointList, innerStartX, innerStartY, innerEndX,
								innerEndY);
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
						Line l = new Line(innerEndX, innerEndY, innerEndX, innerEndY);
						l.setStrokeColor(Color.blue);
						l.setStrokeWidth(3);
						Clump.overlaySplitPoints.add(l);
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
		///////////////////////////////////////////////////////////////////////
		/*
		 * int m = 0; for (InnerContour inner : clump.getInnerContours()) { int
		 * innerStartX; int innerStartY; int innerEndX; int innerEndY; Polygon
		 * innerConvexHull = inner.getConvexHull();
		 * 
		 * ArrayList<Integer> intList = new ArrayList<Integer>(); for (int i =
		 * 0; i < innerConvexHull.npoints; i++) {
		 * 
		 * intList.add(this.getPositionOfConvexHullPointAtBoundary(inner,
		 * innerConvexHull.xpoints[i], innerConvexHull.ypoints[i])); }
		 * ArrayList<Point2D> sinnvollePunkte = new ArrayList<Point2D>();
		 * ArrayList<Integer> bucket = new ArrayList<Integer>();
		 * 
		 * for (int i = 1; i < intList.size(); i++) { if (i == intList.size() -
		 * 1) { if (Math.abs(intList.get(i) - intList.get(i - 1)) < 10) { if
		 * (!sinnvollePunkte.isEmpty()) { sinnvollePunkte.remove(0); } Point2D
		 * punkt = new Point2D.Double(inner.getContour().xpoints[0],
		 * inner.getContour().ypoints[0]);
		 * 
		 * sinnvollePunkte.add(punkt); } } else { //
		 * System.out.println(intList.get(i)+ " "+ // intList.get(i-1)); if
		 * (Math.abs(intList.get(i) - intList.get(i - 1)) <= 10) {
		 * bucket.add(intList.get(i)); } else { int length = bucket.size(); if
		 * (length > 0) { Point2D punkt = new
		 * Point2D.Double(inner.getContour().xpoints[bucket.get(length / 2)],
		 * inner.getContour().ypoints[bucket.get(length / 2)]);
		 * sinnvollePunkte.add(punkt); bucket.clear();
		 * bucket.add(intList.get(i)); } else { bucket.add(intList.get(i)); }
		 * 
		 * } } }
		 * 
		 * ArrayList<ConcavityRegion> tempInner = new
		 * ArrayList<ConcavityRegion>();
		 * 
		 * for (int i = 0; i < sinnvollePunkte.size(); i++) {
		 * 
		 * if (i == 0) { innerStartX = (int)
		 * sinnvollePunkte.get(sinnvollePunkte.size() - 1).getX(); innerStartY =
		 * (int) sinnvollePunkte.get(sinnvollePunkte.size() - 1).getY();
		 * innerEndX = (int) sinnvollePunkte.get(i).getX(); innerEndY = (int)
		 * sinnvollePunkte.get(i).getY();
		 * 
		 * } else { innerStartX = (int) sinnvollePunkte.get(i - 1).getX();
		 * innerStartY = (int) sinnvollePunkte.get(i - 1).getY(); innerEndX =
		 * (int) sinnvollePunkte.get(i).getX(); innerEndY = (int)
		 * sinnvollePunkte.get(i).getY(); } // IJ.log(innerStartX+ " "
		 * +innerStartY+ " "+innerEndX+ " // // "+innerEndY+ " ");
		 * 
		 * ArrayList<Point2D> pointList =
		 * getAllEmbeddedPointsFromInnerContour(innerEndX, innerEndY,
		 * innerStartX, innerStartY, inner); // // IJ.log(innerStartX+ " "
		 * +innerStartY+ " " +innerEndX+ " // // "+innerEndY+ " "
		 * +pointList.size());
		 * 
		 * if (pointList.size() > 3) { ArrayList<Double> doubleList =
		 * computeDistance(pointList, innerStartX, innerStartY, innerEndX,
		 * innerEndY); LargestDistanceConcavityPixelDetector ldcpd = new
		 * LargestDistanceConcavityPixelDetector();
		 * 
		 * ConcavityRegion concavityRegion = new ConcavityRegion(innerStartX,
		 * innerStartY, innerEndX, innerEndY, pointList, doubleList);
		 * 
		 * ArrayList<ConcavityPixel> concavityPixelList =
		 * ldcpd.computeConcavityPixel(concavityRegion);
		 * 
		 * if (concavityPixelList.size() > 0) {
		 * 
		 * concavityRegion.setConcavityPixelList(concavityPixelList);
		 * 
		 * tempInner.add(concavityRegion); } else { int posStart =
		 * this.getPositionOfConvexHullPointAtBoundary(inner, innerStartX,
		 * innerStartY); int posEnd =
		 * this.getPositionOfConvexHullPointAtBoundary(inner, innerEndX,
		 * innerEndY); int posges = (posStart + posEnd) / 2; ConcavityPixel cp =
		 * new ConcavityPixel(new
		 * Point2D.Double(inner.getContour().xpoints[posges],
		 * inner.getContour().ypoints[posges]), 0, concavityRegion);
		 * 
		 * concavityPixelList.add(cp);
		 * concavityRegion.setConcavityPixelList(concavityPixelList);
		 * tempInner.add(concavityRegion); } } } for (int j = 0; j <
		 * tempInner.size(); j++) { ConcavityRegion teil1; ConcavityRegion
		 * teil2;
		 * 
		 * if (j > 0) { teil1 = tempInner.get(j); teil2 = tempInner.get(j - 1);
		 * } else { teil1 = tempInner.get(j); teil2 =
		 * tempInner.get(tempInner.size() - 1); } ArrayList<Point2D> list =
		 * teil1.getMaxDistCoord(); Point2D endPoint = list.get(list.size() /
		 * 2); ArrayList<Point2D> list2 = teil2.getMaxDistCoord();
		 * 
		 * Point2D startPoint = list2.get(list2.size() / 2); ArrayList<Point2D>
		 * embeddedPoints = this.getAllEmbeddedPointsFromInnerContour((int)
		 * startPoint.getX(), (int) startPoint.getY(), (int) endPoint.getX(),
		 * (int) endPoint.getY(), inner); if (embeddedPoints.size() > 3) {
		 * ArrayList<Double> doubleListInner = computeDistance(embeddedPoints,
		 * (int) startPoint.getX(), (int) startPoint.getY(), (int)
		 * endPoint.getX(), (int) endPoint.getY());
		 * AbstractConcavityPixelDetector acpd = null; if
		 * (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE ==
		 * ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS) { acpd = new
		 * AllConcavityPixelDetector(); } else { if
		 * (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE ==
		 * ConcavityPixelDetectorType.
		 * DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH) { acpd = new
		 * LargestDistanceConcavityPixelDetector(); } }
		 * 
		 * ConcavityRegion crReal = new ConcavityRegion((int) startPoint.getX(),
		 * (int) startPoint.getY(), (int) endPoint.getX(), (int)
		 * endPoint.getY(), embeddedPoints, doubleListInner);
		 * 
		 * ArrayList<ConcavityPixel> concavityPixelList =
		 * acpd.computeConcavityPixel(crReal);
		 * crReal.setConcavityPixelList(concavityPixelList); if
		 * (concavityPixelList.size() > 0) { // tempInner.add(crReal); //
		 * IJ.log("innen"); concavityRegionList.add(crReal);
		 * 
		 * } if (Clump_Splitting.SHOWCONVEXHULL) { Line linie = new
		 * Line(crReal.getStartX(), crReal.getStartY(), crReal.getEndX(),
		 * crReal.getEndY());
		 * 
		 * linie.setStrokeWidth(1); // Roi.setColor(Color.cyan);
		 * linie.setStrokeColor(Color.orange);
		 * Clump.overlayConvexHull.add(linie); } // //
		 * System.out.println("innen" + crReal);
		 * 
		 * }
		 * 
		 * }
		 * 
		 * System.out.println(m + " " + concavityRegionList.size()); m++; }
		 * 
		 * System.out.println(concavityRegionList.size()); // IJ.log(
		 * "Anzahl der Konkavitätsregionen"+ concavityRegionList.size());
		 */
		return concavityRegionList;

	}

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

	private int getPositionOfConvexHullPointAtBoundary(InnerContour inner, int concavityPointX, int concavityPointY)
	{
		for (int i = 0; i < inner.getContour().npoints; i++)
		{
			if (inner.getContour().xpoints[i] == concavityPointX && inner.getContour().ypoints[i] == concavityPointY)
			{
				return i;
			}
		}
		return 0;
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
		int i = clump.getBoundary().npoints - 1;
		boolean ended = false;
		boolean started = false;
		ArrayList<Point2D> pointList = new ArrayList<Point2D>();
		while (i >= 0 && !ended)
		{
			if (clump.getBoundary().xpoints[i] == startX && clump.getBoundary().ypoints[i] == startY && !started)
			{
				pointList.add(new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));
				started = true;
			} else
			{

				if (started && (clump.getBoundary().xpoints[i] != endX || clump.getBoundary().ypoints[i] != endY)
						&& !ended)
				{
					pointList.add(new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));

				} else
				{
					if (started && clump.getBoundary().xpoints[i] == endX && clump.getBoundary().ypoints[i] == endY)
					{
						pointList.add(
								new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));
						ended = true;
					}

				}
			}
			i--;
		}
		return pointList;

	}

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
	/*
	 * private double[] getMaxDist(ArrayList<Double> distList) {
	 * 
	 * //int indexMax = 0; double max = 0; int index = 0; ArrayList<Integer>
	 * moreThanOneMax= new ArrayList<Integer>(); for (int
	 * i=0;i<distList.size();i++) { double d = distList.get(i); if(d>=max) { if
	 * (d > max) { moreThanOneMax.clear(); moreThanOneMax.add(index); max = d;
	 * //indexMax = index; } else{ if(d==max) { moreThanOneMax.add(index);
	 * max=d; } } } index++; } double[] tmp= new
	 * double[moreThanOneMax.size()+1]; tmp[0]=max; for(int
	 * i=0;i<moreThanOneMax.size();i++) { tmp[i+1]=moreThanOneMax.get(i); }
	 * 
	 * return tmp; }
	 */

}
