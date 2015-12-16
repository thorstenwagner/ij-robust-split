package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;
import ij.process.ImageProcessor;

/**
 * represents a SplitLine between a ConcavityRegion and a point on the boundary,
 * this kind of SplitLine is only used, if no other possible SplitLines can be
 * detected
 * 
 * @author Louise
 *
 */
public class StraightSplitLineBetweenConcavityRegionAndPoint implements StraightSplitLine
{

	/**
	 * valid ConcavityRegion with the largest concavityDepth of a Clump
	 */
	ConcavityRegion cI;
	/**
	 * angle between startPoint of the concavityRegion, endPoint of the
	 * concavityRegion and the Point with the largest concavityDepth of the
	 * concavityRegion
	 */
	double concavityAngle;
	/**
	 * ratio between the actual largest concavityDepth and the second largest
	 * concavityDepth
	 */
	double concavityRatio;
	/**
	 * the selected Point to split the Clump
	 */
	Point2D.Double point;

	/**
	 * 
	 * @param cI
	 *            concavityRegion with the largest concavityDepth of the Clump
	 *            which should be used to split the Clump with a boundaryPoint
	 * @param concavityAngle
	 *            angle between startPoint of the concavityRegion, endPoint of
	 *            the concavityRegion and the Point with the largest
	 *            concavityDepth of the concavityRegion
	 * 
	 * @param concavityRatio
	 *            ratio between the actual largest concavityDepth and the
	 *            sencond largest concavityDepth
	 * @param point
	 *            the selected Point to split the Clump
	 */
	public StraightSplitLineBetweenConcavityRegionAndPoint(ConcavityRegion cI, double concavityAngle,
			double concavityRatio, Point2D.Double point)
	{
		this.cI = cI;
		this.concavityAngle = concavityAngle;
		this.concavityRatio = concavityRatio;
		this.point = point;
	}

	
	/**
	 * draws the Splitline
	 * @param ip ImageProcessor to draw the SplitLine
	 */
	public void drawLine(ImageProcessor ip)
	{
		ip.drawLine((int) cI.getMaxDistCoord().getX(), (int) cI.getMaxDistCoord().getY(), (int) point.getX(),
				(int) point.getY());
		ip.setLineWidth(10);
		ip.drawDot((int) point.getX(), (int) point.getY());
		ip.setLineWidth(1);
	}
}
