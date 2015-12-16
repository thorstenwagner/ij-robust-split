package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import ij.process.ImageProcessor;

/**
 * a ConcavityRegion is a Region of a Clump with high concavity.
 * 
 * @author Louise
 *
 */
public class ConcavityRegion
{

	/**
	 * startX is the x-Coordinate of the StartingPoint of the ConcavityRegion
	 * the StartingPoint is detected by a point of the ConvexHull
	 */
	private int startX;
	/**
	 * startY is the y-Coordinate of the StartingPoint of the ConcavityRegion
	 * the StartingPoint is detected by a point of the ConvexHull
	 */
	private int startY;
	/**
	 * endX is the x-Coordinate of the endPoint of the ConcavityRegion the
	 * endPoint is detected by a point of the ConvexHull
	 */
	private int endX;
	/**
	 * endX is the y-Coordinate of the endPoint of the ConcavityRegion the
	 * endPoint is detected by a point of the ConvexHull
	 */
	private int endY;
	/**
	 * max is the largest ConcavityDepth of the ConcavityRegion
	 */
	private double max;
	/**
	 * indexMax is the index of the Point with the largest concavityDepth of the
	 * ConcavityRegion based on the boundaryPointList
	 */
	private int indexMax;
	/**
	 * distList is the List of the concavityDepht of each Point on the boundary
	 * to the convexHull
	 */
	private ArrayList<Double> distList;
	/**
	 * represents the point in the middle of the convexHull
	 */
	private Point2D midPointOfConvexHull;
	/**
	 * boundaryPointList is a List with all Points based on the grid, which is
	 * on the boundary
	 */
	private ArrayList<Point2D> boundaryPointList;

	/**
	 * marks the Pixel with the largest concavityDepth of the boundary
	 * 
	 * @param ip
	 *            ImageProcessor to mark the Pixel
	 */
	public void markMax(ImageProcessor ip)
	{
		Point2D p = boundaryPointList.get(indexMax);
		ip.setColor(Color.BLUE);
		ip.setLineWidth(10);
		ip.drawDot((int) p.getX(), (int) p.getY());
		ip.setLineWidth(1);
	}

	/**
	 * marks the Pixel in the middle of the ConvexHull
	 * 
	 * @param ip
	 *            ImageProcessor to mark the Pixel
	 */
	public void markMidPointOfConvexHull(ImageProcessor ip)
	{
		ip.setLineWidth(10);
		ip.drawDot((int) this.getMidPointOfConvexHull().getX(), (int) this.getMidPointOfConvexHull().getY());
		ip.setLineWidth(1);
	}

	/**
	 * produces a ConcavityRegion
	 * 
	 * @param startX
	 *            x-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param startY
	 *            y-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param endX
	 *            x-Coordinate of the endPoint of the ConcavityRegion
	 * @param endY
	 *            y-Coordinate of the endPoint of the ConcavityRegion
	 * @param boundaryPointList
	 *            List of all Pixels of the part of the boundary included by the
	 *            ConcavityRegion
	 * @param distList
	 *            List of the concavityDepth of all points of the part of the
	 *            boundary included by the ConcavityRegion
	 * @param max
	 *            largest concavityDepth of the ConcavityRegion
	 * @param maxIndex
	 *            index of the point with the largest concavityDepth based by
	 *            the boundaryPointList
	 */
	public ConcavityRegion(int startX, int startY, int endX, int endY, ArrayList<Point2D> boundaryPointList,
			ArrayList<Double> distList, double max, int maxIndex)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.boundaryPointList = boundaryPointList;
		this.distList = distList;
		this.max = max;
		this.indexMax = maxIndex;
		this.midPointOfConvexHull = this.computeMidPointOfConvexHull();

	}

	public int getStartX()
	{
		return startX;
	}

	public int getStartY()
	{
		return startY;
	}

	public int getEndX()
	{
		return endX;
	}

	public int getEndY()
	{
		return endY;
	}

	public ArrayList<Double> getDistList()
	{
		return distList;
	}

	public Point2D getMaxDistCoord()
	{
		return boundaryPointList.get(indexMax);
	}

	/**
	 * computes the point of the ConvexHull, which is in middle of the convexHull
	 * @return Point in the middle of the ConvexHull
	 */
	private Point2D computeMidPointOfConvexHull()
	{
		double xDist = ((double) endX - (double) startX) / 2;
		double yDist = ((double) endY - (double) startY) / 2;
		double xCoord = startX + xDist;
		double yCoord = startY + yDist;
		Point2D.Double midPoint = new Point2D.Double(xCoord, yCoord);
		return midPoint;
	}

	public Point2D getMidPointOfConvexHull()
	{
		return midPointOfConvexHull;
	}

	public double getMaxDist()
	{
		return max;
	}

}
