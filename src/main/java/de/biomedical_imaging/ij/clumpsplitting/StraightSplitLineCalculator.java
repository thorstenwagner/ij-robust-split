package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.vecmath.Vector2d;




/**
 * calculates Straight SplitLines
 * 
 * @author Louise
 *
 */

public class StraightSplitLineCalculator implements AbstractSplitLineCalculator
{
	
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c)
	{
		ArrayList<AbstractSplitLine> possibleSplitLines = this
				.computeStraigthSplitLineBetweenTwoConcavityRegions(concavityRegionList);
		if (possibleSplitLines.size() == 0)
		{
			possibleSplitLines = this.computeStraightSplitLineBetweenConcavityRegionAndPoint(concavityRegionList, c,
					possibleSplitLines);
		}
		return possibleSplitLines;
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

	private ArrayList<AbstractSplitLine> computeStraigthSplitLineBetweenTwoConcavityRegions(
			ArrayList<ConcavityRegion> concavityRegionList)
	{
		ArrayList<AbstractSplitLine> possibleSplitLines = new ArrayList<AbstractSplitLine>();
		for (int i = 0; i < concavityRegionList.size() - 1; i++)
		{
			ConcavityRegion cOne = concavityRegionList.get(i);
			for (int j = i + 1; j < concavityRegionList.size(); j++)
			{

				ConcavityRegion cTwo = concavityRegionList.get(j);

				if (!cOne.equals(cTwo))
				{
					double saliency = this.computeSaliency(cOne, cTwo);
					double concavityConcavityAlignment = this.computeConcavityConcavityAlignment(cOne, cTwo);
					double concavityLineAlignment = this.computeConcavityLineAlignment(cOne, cTwo);
					if (saliency > Clump_Splitting.SALIENCY_THRESHOLD)
					{
						if (concavityConcavityAlignment < Clump_Splitting.CONCAVITYCONCAVITY_THRESHOLD)
						{
							if (concavityLineAlignment < Clump_Splitting.CONCAVITYLINE_THRESHOLD)
							{
								StraightSplitLineBetweenTwoConcavityRegions ssl = new StraightSplitLineBetweenTwoConcavityRegions(
										cOne, cTwo, saliency, concavityConcavityAlignment, concavityLineAlignment);
								possibleSplitLines.add(ssl);
							}
						}
					}

				}
			}
		}
		return possibleSplitLines;

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
	private ArrayList<AbstractSplitLine> computeStraightSplitLineBetweenConcavityRegionAndPoint(
			ArrayList<ConcavityRegion> concavityRegionList, Clump c, ArrayList<AbstractSplitLine> possibleSplitLines)
	{
		if (concavityRegionList.size() > 0)
		{

			ConcavityRegion concavityRegion = c.getRegionOfMaxConcavityDepth();
			double concavityAngle = this.computeConcavityAngle(concavityRegion);
			double concavityRatio = this.computeConcavityRatio(c, concavityRegion);
			if (concavityAngle < Clump_Splitting.CONCAVITYANGLE_THRESHOLD)
			{
				if (concavityRatio > Clump_Splitting.CONCAVITYRATIO_THRESHOLD)
				{
					StraightSplitLineBetweenConcavityRegionAndPoint ssl = this
							.computeSplitLineBetweenConcavityPointAndPoint(concavityRegion, c, concavityAngle,
									concavityRatio);
					possibleSplitLines.add(ssl);
				}
			}
		}
		return possibleSplitLines;
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
	 *            the ConcavityPoint of this ConcavityRegion
	 * @return value of saliency
	 */
	private double computeSaliency(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double minCDi = 0;
		if (cI.getMaxDist() > cJ.getMaxDist())
		{
			minCDi = cJ.getMaxDist();
		} else
		{
			minCDi = cI.getMaxDist();
		}
		double distance;
		double saliency = 0;
		Point2D pOne = cI.getMaxDistCoord();
		Point2D pTwo = cJ.getMaxDistCoord();
		double distX;
		double distY;
		distX = pOne.getX() - pTwo.getX();
		distY = pOne.getY() + pTwo.getY();
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
	 * @return the Vector between the middelpoint of the convexhull and the
	 *         concavityPoint of the ConcavityRegion
	 */

	private Vector2d computeVi(ConcavityRegion cI)
	{
		Point2D midPointI = cI.getMidPointOfConvexHull();
		Point2D maxPointI = cI.getMaxDistCoord();
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
	 * @return value of concavityConcavityAlignment
	 */
	private double computeConcavityConcavityAlignment(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double concavityConcavityAlignment = 0;
		Vector2d vI = this.computeVi(cI);
		Vector2d vJ = this.computeVi(cJ);
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
	 * @return value of concavityLineAlignment
	 */

	private double computeConcavityLineAlignment(ConcavityRegion cI, ConcavityRegion cJ)
	{
		double concavityLineAlignment = 0;
		Point2D maxPointOne = cI.getMaxDistCoord();
		Point2D maxPointTwo = cJ.getMaxDistCoord();
		double xDist = maxPointTwo.getX() - maxPointOne.getX();
		double yDist = maxPointTwo.getY() - maxPointOne.getY();

		Vector2d uij = new Vector2d(xDist, yDist);
		Vector2d vOne = this.computeVi(cI);
		Vector2d vTwo = this.computeVi(cJ);
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
	 * @return value of concavityAngle
	 */
	private double computeConcavityAngle(ConcavityRegion cI)
	{
		Point2D.Double a = new Point2D.Double(cI.getStartX(), cI.getStartY());
		Point2D.Double b = new Point2D.Double(cI.getEndX(), cI.getEndY());
		Point2D c = cI.getMaxDistCoord();

		double clength = Math
				.sqrt((b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() - a.getY()));
		double alength = Math
				.sqrt((b.getX() - c.getX()) * (b.getX() - c.getX()) + (b.getY() - c.getY()) * (b.getY() - c.getY()));
		double blength = Math
				.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY()) * (c.getY() - a.getY()));

		double gamma = Math
				.acos(((clength * clength) - (alength * alength) - (blength * blength)) / (-2 * Math.abs(alength) * Math.abs(blength)));
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
	 * @return value of concavityRatio
	 */
	private double computeConcavityRatio(Clump c, ConcavityRegion cI)
	{
		double concavityRatio;
		concavityRatio = ((cI.getMaxDist()) / (c.getSecondMaxConcavityRegionDepth()));
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
	 * @return Straight SplitLine for the concavityRegion
	 */
	private StraightSplitLineBetweenConcavityRegionAndPoint computeSplitLineBetweenConcavityPointAndPoint(
			ConcavityRegion cI, Clump c, double concavityAngle, double concavityRatio)
	{
		Point2D midPoint = cI.getMidPointOfConvexHull();
		Point2D concavityPoint = cI.getMaxDistCoord();
		Line2D.Double line = new Line2D.Double(midPoint, concavityPoint);
		Polygon p = c.getBoundary();
		double minDist = line.ptLineDist(p.xpoints[0], p.ypoints[0]);
		int indexMinDist = 0;
		double dist = 0;
		for (int i = 0; i < p.npoints; i++)
		{
			Point2D.Double point = new Point2D.Double(p.xpoints[i], p.ypoints[i]);
			if (point.getX() != concavityPoint.getX() && point.getY() != concavityPoint.getY())
			{
				dist = line.ptLineDist(point);
				if (dist < minDist)
				{
					minDist = dist;
					indexMinDist = i;
				}
			}
		}
		StraightSplitLineBetweenConcavityRegionAndPoint s = new StraightSplitLineBetweenConcavityRegionAndPoint(cI,
				concavityAngle, concavityRatio, new Point2D.Double(p.xpoints[indexMinDist], p.ypoints[indexMinDist]));
		return s;
	}
}
