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

package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import javax.vecmath.Vector2d;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityPixel;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import de.biomedical_imaging.ij.clumpsplitting.SplitLineType;
import ij.process.ImageProcessor;

/**
 * calculates Straight SplitLines
 * 
 * @author Louise
 *
 */

public class StraightSplitLineCalculator implements AbstractSplitLineCalculator
{
	/**
	 * Calculates possible Straight SplitLines by validate them by different
	 * criteria
	 * 
	 * Manages the calculation of Minimum and MaximumIntensity SplitLines and of
	 * Geodesic Distance SplitLines
	 */
	@Override
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip, ImageProcessor binary)
	{
		StraightSplitLine bestSplitLine = null;
		StraightSplitLine allSplitLines = this.calculatePossibleStraightSplitLines(concavityRegionList, c);
		if (allSplitLines != null)
		{
			bestSplitLine = allSplitLines;
		}
		ArrayList<AbstractSplitLine> possibleSplitLines = new ArrayList<AbstractSplitLine>();

		if (Clump_Splitting.SPLITLINETYPE == SplitLineType.STRAIGHTSPLITLINE)
		{
			possibleSplitLines.add(bestSplitLine);
		} else
		{

			if (Clump_Splitting.SPLITLINETYPE == SplitLineType.MAXIMUMINTENSITYSPLITLINE)
			{
				if (bestSplitLine != null)
				{
					AbstractSplitLineCalculator mmislc = new MaximumIntensitySplitLineCalculator(
							bestSplitLine.getStartConcavityPixel().getPosition(),
							bestSplitLine.getEndConcavityPixel().getPosition());
					possibleSplitLines = mmislc.calculatePossibleSplitLines(concavityRegionList, c, ip, binary);
				}
			} else
			{
				if (Clump_Splitting.SPLITLINETYPE == SplitLineType.GEODESICDISTANCESPLITLINE)
				{
					if (bestSplitLine != null)
					{
						GeodesicDistanceSplitLineCalculator gdslc = new GeodesicDistanceSplitLineCalculator(
								bestSplitLine.getStartConcavityPixel().getPosition(),
								bestSplitLine.getEndConcavityPixel().getPosition());
						possibleSplitLines = gdslc.calculatePossibleSplitLines(concavityRegionList, c, ip, binary);
					}
				} else
				{
					if (Clump_Splitting.SPLITLINETYPE == SplitLineType.MINIMUMINTENSITYSPLITLINE)
					{
						if (bestSplitLine != null)
						{
							AbstractSplitLineCalculator mmislc = new MinimumIntensitySplitLineCalculator(
									bestSplitLine.getStartConcavityPixel().getPosition(),
									bestSplitLine.getEndConcavityPixel().getPosition());
							possibleSplitLines = mmislc.calculatePossibleSplitLines(concavityRegionList, c, ip, binary);
						}

					}

				}
			}
		}
		return possibleSplitLines;
	}

	/**
	 * Calculates best SplitLines for a Clump
	 * 
	 * @param concavityRegionList
	 *            all ConcavityRegions of a Clump
	 * @param c
	 *            Clump
	 * @return bestSplitLine
	 */
	private StraightSplitLine calculatePossibleStraightSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c)
	{
		ArrayList<StraightSplitLine> possibleSplitLines = this
				.computeStraigthSplitLineBetweenTwoConcavityRegions(concavityRegionList);
		StraightSplitLine bestSplitLine = null;

		if (possibleSplitLines.size() >= 1)
		{
			double maxChi = 0;
			for (StraightSplitLine ssl : possibleSplitLines)
			{
				if (ssl instanceof StraightSplitLineBetweenTwoConcavityRegions)
				{
					StraightSplitLineBetweenTwoConcavityRegions sslbrcr = (StraightSplitLineBetweenTwoConcavityRegions) ssl;
					double chi = sslbrcr.getChi();
					if (chi > maxChi)
					{
						maxChi = chi;
						bestSplitLine = sslbrcr;
					}
				}
			}
			double distance = bestSplitLine.getStartConcavityPixel().getPosition()
					.distance(bestSplitLine.getEndConcavityPixel().getPosition());
			distance = distance * 1000;
			distance = Math.round(distance);
			distance = distance / 1000;
			double maxDistSum = bestSplitLine.getStartConcavityPixel().distance() + bestSplitLine.distance();
			maxDistSum = maxDistSum * 1000;
			maxDistSum = Math.round(maxDistSum);
			maxDistSum = maxDistSum / 1000;

			SplitLineAssignmentSVM splitLine = new SplitLineAssignmentSVM(
					(int) bestSplitLine.getStartConcavityPixel().getPosition().getX(),
					(int) bestSplitLine.getStartConcavityPixel().getPosition().getY(),
					(int) bestSplitLine.getEndConcavityPixel().getPosition().getX(),
					(int) bestSplitLine.getEndConcavityPixel().getPosition().getY(), -1, distance, maxDistSum);
			if (!Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM.contains(splitLine))
			{
				Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM.add(splitLine);
			} else
			{
				for (SplitLineAssignmentSVM slaSVM : Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM)
				{
					if (slaSVM.equals(splitLine))
					{
						slaSVM.setClassificationValue(1);
					}
				}
			}

		}
		if (possibleSplitLines.size() == 0)
		{
			ArrayList<StraightSplitLine> splitLineList = this
					.computeStraightSplitLineBetweenConcavityRegionAndPoint(concavityRegionList, c);
			if (splitLineList.size() > 0)
			{
				bestSplitLine = splitLineList.get(0);
			}

		}
		return bestSplitLine;
	}

	/**
	 * computes all possible Straight SplitLines between two concavityRegions by
	 * caluculating the needed parameters and evaluating them
	 * 
	 * @param concavityRegionList
	 *            List with all concavityRegions of a Clump
	 * @return List including all possible SplitLines for the Clump if the List
	 *         is empty it would be tried if there are any possible SplitLines
	 *         between ConcavityRegion and BoundaryPoint
	 */

	private ArrayList<StraightSplitLine> computeStraigthSplitLineBetweenTwoConcavityRegions(
			ArrayList<ConcavityRegion> concavityRegionList)
	{

		ArrayList<StraightSplitLine> possibleSplitLines = new ArrayList<StraightSplitLine>();
		for (int i = 0; i < concavityRegionList.size() - 1; i++)
		{
			ConcavityRegion cOne = concavityRegionList.get(i);
			for (int j = i + 1; j < concavityRegionList.size(); j++)
			{

				ConcavityRegion cTwo = concavityRegionList.get(j);
				if (!cOne.equals(cTwo))
				{

					for (ConcavityPixel cpOne : cOne.getConcavityPixelList())
					{

						for (ConcavityPixel cpTwo : cTwo.getConcavityPixelList())
						{

							double chi = this.computeChi(cOne, cTwo, cpOne, cpTwo);
							double distance = cpOne.getPosition().distance(cpTwo.getPosition());
							distance = distance * 1000;
							distance = Math.round(distance);
							distance = distance / 1000;
							double maxDistSum = cpOne.distance() + cpTwo.distance();
							maxDistSum = maxDistSum * 1000;
							maxDistSum = Math.round(maxDistSum);
							maxDistSum = maxDistSum / 1000;
							double saliency = this.computeSaliency(cOne, cTwo, cpOne, cpOne);
							if (saliency > Clump_Splitting.SALIENCY_THRESHOLD)
							{
								double concavityConcavityAlignment = this.computeConcavityConcavityAlignment(cOne, cTwo,
										cpOne.getPosition(), cpTwo.getPosition());

								if (concavityConcavityAlignment < Clump_Splitting.CONCAVITYCONCAVITY_THRESHOLD)
								{
									double concavityLineAlignment = this.computeConcavityLineAlignment(cOne, cTwo,
											cpOne.getPosition(), cpTwo.getPosition());

									if (concavityLineAlignment < Clump_Splitting.CONCAVITYLINE_THRESHOLD)
									{
										if (chi > Clump_Splitting.CHI_THRESHOLD)
										{
											StraightSplitLineBetweenTwoConcavityRegions splitLineAll = new StraightSplitLineBetweenTwoConcavityRegions(
													cOne, cTwo, saliency, concavityConcavityAlignment,
													concavityLineAlignment, chi, cpOne, cpTwo);

											possibleSplitLines.add(splitLineAll);
										}
										SplitLineAssignmentSVM splitLine = new SplitLineAssignmentSVM(
												(int) cpOne.getPosition().getX(), (int) cpOne.getPosition().getY(),
												(int) cpTwo.getPosition().getX(), (int) cpTwo.getPosition().getY(), -1,
												distance, maxDistSum);
										if (!Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM
												.contains(splitLine))
										{
											Clump_Splitting.listOfAllPossibleSplitLinesAndClassForSVM.add(splitLine);
										}

									}

								}
							}
						}
					}

				}
			}
		}

		return possibleSplitLines;

	}

	/**
	 * Computes chi value
	 * 
	 * @param cI
	 *            First ConcavityRegion to Split
	 * @param cJ
	 *            Second ConcavityRegion to Split
	 * @param cpOne
	 *            ConcavityPixel of First ConcavityRegion to Split which is
	 *            choosed to Split
	 * @param cpTwo
	 *            ConcavityPixel of Second ConcavityRegion to Split which is
	 *            choosed to Split
	 * @return chi value
	 */

	private double computeChi(ConcavityRegion cI, ConcavityRegion cJ, ConcavityPixel cpOne, ConcavityPixel cpTwo)
	{
		double distance;
		Point2D pOne = cpOne.getPosition();
		Point2D pTwo = cpTwo.getPosition();
		distance = pOne.distance(pTwo);
		double chi = ((Clump_Splitting.C1 * cpOne.distance() + Clump_Splitting.C1 * cpTwo.distance()
				+ Clump_Splitting.C2)
				/ (distance + Clump_Splitting.C1 * cpOne.distance() + Clump_Splitting.C1 * cpTwo.distance()
						+ Clump_Splitting.C2));
		return chi;
	}

	/**
	 * Computes a SplitLine Between a concavityRegion and a point on the
	 * Boundary therefor it needs the ConcavityRegion with the largest and the
	 * second largest ConcavityDepth of the Clump, the Concavity Region with the
	 * largest ConcavityDepth is set as the ConcavityRegion to Split the Clump
	 * and the ConcavityRegion with the second largest ConcavityDepth is used as
	 * reference value
	 * 
	 * @param concavityRegionList
	 *            List with all concavityRegions of the Clump
	 * @param c
	 *            Clump to get ConcavityRegions with largest and second largest
	 *            ConcavityDepth
	 * @param possibleSplitLines
	 *            ArrayList with all SplitLines has to be empty to get into this
	 *            method
	 * @return List of SplitLines has only one Element if this method is used
	 */
	private ArrayList<StraightSplitLine> computeStraightSplitLineBetweenConcavityRegionAndPoint(
			ArrayList<ConcavityRegion> concavityRegionList, Clump c)
	{
		ArrayList<StraightSplitLine> liste = new ArrayList<StraightSplitLine>();
		if (concavityRegionList.size() > 0)
		{

			ConcavityPixel concavityPixel = c.getPixelOfMaxConcavityDepth();
			if (concavityPixel != null)
			{
				ConcavityRegion concavityRegion = concavityPixel.getConcavityRegion();
				for (ConcavityPixel cpOne : concavityRegion.getConcavityPixelList())
				{
					double concavityAngle = this.computeConcavityAngle(concavityRegion, cpOne.getPosition());
					double concavityRatio = this.computeConcavityRatio(c, cpOne);

					StraightSplitLineBetweenConcavityRegionAndPoint ssl = this
							.computeSplitLineBetweenConcavityPointAndPoint(concavityRegion, c, concavityAngle,
									concavityRatio, cpOne);
					if (concavityAngle < Clump_Splitting.CONCAVITYANGLE_THRESHOLD)
					{
						if (concavityRatio > Clump_Splitting.CONCAVITYRATIO_THRESHOLD)
						{
							liste.add(ssl);
						}
					}
				}
			}
		}
		return liste;
	}

	/**
	 * computes the Saliency of the ConcavityRegion used for
	 * SplitLinesBetweenTwoConcavityRegions. SALIENCY evaluates, if the
	 * concavityRegion has a large Concaveness and the possible Line is short
	 * enough.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion * @param cpOne
	 *            ConcavityPixel of First ConcavityRegion to Split which is
	 *            choosed to Split
	 * @param cpTwo
	 *            ConcavityPixel of Second ConcavityRegion to Split which is
	 *            choosed to Split
	 * 
	 * @return value of saliency
	 */
	private double computeSaliency(ConcavityRegion cI, ConcavityRegion cJ, ConcavityPixel cpOne, ConcavityPixel cpTwo)
	{
		double minCDi = 0;
		if (cpOne.distance() > cpTwo.distance())
		{
			minCDi = cpTwo.distance();
		} else
		{
			minCDi = cpOne.distance();
		}
		double distance;
		double saliency = 0;
		Point2D pOne = cpOne.getPosition();
		Point2D pTwo = cpTwo.getPosition();
		double distX;
		double distY;
		distX = Math.abs(pOne.getX() - pTwo.getX());
		distY = Math.abs(pOne.getY() - pTwo.getY());
		distance = Math.sqrt((distX * distX) + (distY * distY));
		if ((minCDi + distance) > 0)
		{
			saliency = minCDi / (minCDi + distance);
		}
		return saliency;

	}

	/**
	 * computes the Vector vi of a ConcavityRegion. The Vector2d vi is a Vector
	 * with the direction from the middelpoint of the convexhull to the
	 * ConcavityPoint
	 * 
	 * @param cI
	 *            the concavityRegion of Interest
	 * @param pointCI
	 *            ConcavityPixel of ConcavityRegion to Split which is choosed to
	 *            Split
	 * 
	 * @return the Vector between the middelpoint of the convexhull and the
	 *         concavityPoint of the ConcavityRegion
	 */

	private Vector2d computeVi(ConcavityRegion cI, Point2D pointcI)
	{
		Point2D midPointI = cI.getMidPointOfConcavityRegion();
		Point2D maxPointI = pointcI;
		double xPointDistOne = maxPointI.getX() - midPointI.getX();
		double yPointDistOne = maxPointI.getY() - midPointI.getY();
		Vector2d vi = new Vector2d(xPointDistOne, yPointDistOne);
		return vi;
	}

	/**
	 * computes the concavityConcavityAlignment. used for
	 * SplitLinesBetweenTwoConcavityRegions. CONCAVITYCONCAVITYALIGNMENT
	 * evaluates the angle between the concavityRegionorientations.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param pointCI
	 *            ConcavityPixel of First ConcavityRegion to Split which is
	 *            choosed to Split
	 * @param PointCJ
	 *            ConcavityPixel of Second ConcavityRegion to Split which is
	 *            choosed to Split
	 * 
	 * @return value of concavityConcavityAlignment
	 */
	private double computeConcavityConcavityAlignment(ConcavityRegion cI, ConcavityRegion cJ, Point2D pointCI,
			Point2D pointCJ)
	{
		double concavityConcavityAlignment = 0;
		Vector2d vI = this.computeVi(cI, pointCI);
		Vector2d vJ = this.computeVi(cJ, pointCJ);
		vI.normalize();
		vJ.normalize();

		concavityConcavityAlignment = Math.PI - Math.acos(vI.dot(vJ));
		return concavityConcavityAlignment;

	}

	/**
	 * computes the ConcavityLineAlignment. used for
	 * SplitLinesBetweenTwoConcavityRegions. CONCAVITYLINEALIGNMENT evaluates
	 * the largest angle between the concavityRegion Orientation and the
	 * Possible Splitline.
	 * 
	 * @param cI
	 *            first ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param cJ
	 *            second ConcavityRegion. The possible SplitLine is computed for
	 *            the ConcavityPoint of this ConcavityRegion
	 * @param pointCI
	 *            ConcavityPixel of First ConcavityRegion to Split which is
	 *            choosed to Split
	 * @param PointCJ
	 *            ConcavityPixel of Second ConcavityRegion to Split which is
	 *            choosed to Split
	 * 
	 * @return value of concavityLineAlignment
	 */

	private double computeConcavityLineAlignment(ConcavityRegion cI, ConcavityRegion cJ, Point2D pointCI,
			Point2D pointCJ)
	{
		double concavityLineAlignment = 0;
		Point2D maxPointOne = pointCI;
		Point2D maxPointTwo = pointCJ;
		double xDist = maxPointTwo.getX() - maxPointOne.getX();
		double yDist = maxPointTwo.getY() - maxPointOne.getY();

		Vector2d uij = new Vector2d(xDist, yDist);
		Vector2d vOne = this.computeVi(cI, pointCI);
		Vector2d vTwo = this.computeVi(cJ, pointCJ);
		uij.normalize();
		vOne.normalize();
		vTwo.normalize();
		Vector2d muij = new Vector2d(-uij.getX(), -uij.getY());

		double phiOne = Math.acos(vOne.dot(uij));
		double phiTwo = Math.acos(vTwo.dot(muij));
		if (phiOne > phiTwo)
		{
			concavityLineAlignment = phiOne;
		} else
		{
			concavityLineAlignment = phiTwo;
		}
		return concavityLineAlignment;
	}

	/**
	 * computes the ConcavityAngle. used for
	 * SplitLinesBetweenConcavityRegionAndPoint. angle between startPoint of the
	 * concavityRegion, endPoint of the concavityRegion and the Point with the
	 * largest concavityDepth of the concavityRegion.
	 * 
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @param pointCI
	 *            ConcavityPixel of ConcavityRegion to Split which is choosed to
	 *            Split
	 *
	 * 
	 * @return value of concavityAngle
	 */
	private double computeConcavityAngle(ConcavityRegion cI, Point2D pointCI)
	{
		Point2D.Double a = new Point2D.Double(cI.getStartX(), cI.getStartY());
		Point2D.Double b = new Point2D.Double(cI.getEndX(), cI.getEndY());
		Point2D c = pointCI;

		double clength = Math
				.sqrt((b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() - a.getY()));
		double alength = Math
				.sqrt((b.getX() - c.getX()) * (b.getX() - c.getX()) + (b.getY() - c.getY()) * (b.getY() - c.getY()));
		double blength = Math
				.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY()) * (c.getY() - a.getY()));

		double gamma = Math.acos(((clength * clength) - (alength * alength) - (blength * blength))
				/ (-2 * Math.abs(alength) * Math.abs(blength)));
		return gamma;
	}

	/**
	 * computes the concavityRatio. used for
	 * SplitLinesBetweenConcavityRegionAndPoint. ratio between the actual
	 * largest concavityDepth and the second largest concavityDepth.
	 * 
	 * @param c
	 *            the actual Clump is needed to get the ConcavityRegions with
	 *            the largest and the second largest ConcavityDepth
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @param cpOne
	 *            ConcavityPixel of ConcavityRegion to Split which is choosed to
	 *            Split
	 * 
	 * @return value of concavityRatio
	 */
	private double computeConcavityRatio(Clump c, ConcavityPixel cpOne)
	{
		double concavityRatio;
		concavityRatio = ((cpOne.distance()) / (c.getSecondMaxConcavityRegionDepth()));
		return concavityRatio;
	}

	/**
	 * computes the SplitLine between a concavityRegion and a Point
	 * 
	 * @param cI
	 *            The possible SplitLine is computed for the ConcavityPoint of
	 *            this ConcavityRegion. The concavityRegion cI should be the
	 *            ConcavityRegion with the largest concavityDepth
	 * @param c
	 *            the actual Clump is needed to get the ConcavityRegions with
	 *            the largest and the second largest ConcavityDepth
	 * @param concavityAngle
	 *            the concavityAngle computed for the concavityRegion
	 * @param concavityRatio
	 *            the concavityRatio computed for the concavityRegion
	 * @param cpOne
	 *            ConcavityPixel of ConcavityRegion to Split which is choosed to
	 *            Split
	 * 
	 * @return Straight SplitLine for the concavityRegion
	 * 
	 */
	private StraightSplitLineBetweenConcavityRegionAndPoint computeSplitLineBetweenConcavityPointAndPoint(
			ConcavityRegion cI, Clump c, double concavityAngle, double concavityRatio, ConcavityPixel cpOne)
	{
		Point2D midPoint = cI.getMidPointOfConcavityRegion();
		Point2D concavityPoint = cpOne.getPosition();
		Line2D.Double line = new Line2D.Double(midPoint, concavityPoint);
		Polygon p = c.getBoundary();
		HashSet<Point2D.Double> test = new HashSet<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++)
		{
			test.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		test.removeAll(cI.getBoundaryPointList());

		double minDist = 10000;
		double dist = 0;
		java.util.Iterator<Point2D.Double> it = test.iterator();
		Point2D.Double minDistPoint = null;
		while (it.hasNext())
		{
			Point2D.Double point = it.next();
			if (point.getX() != concavityPoint.getX() && point.getY() != concavityPoint.getY())
			{
				dist = line.ptLineDist(point);
				if (dist <= minDist)
				{

					minDist = dist;
					minDistPoint = point;
				}
			}
		}
		StraightSplitLineBetweenConcavityRegionAndPoint s = null;
		if (minDistPoint != null)
		{
			s = new StraightSplitLineBetweenConcavityRegionAndPoint(cI, concavityAngle, concavityRatio, minDistPoint,
					cpOne);
		}
		return s;
	}
}
