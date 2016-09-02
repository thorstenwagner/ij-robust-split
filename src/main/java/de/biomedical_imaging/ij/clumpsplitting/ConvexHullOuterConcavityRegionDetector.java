package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.process.ImageProcessor;

public class ConvexHullOuterConcavityRegionDetector implements AbstractOuterConcavityRegionDetector
{

	@Override
	public ArrayList<ConcavityRegion> computeOuterConcavityRegions(ImageProcessor binary, Clump clump)
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

				ArrayList<Point2D> pointList = getAllEmbeddedPointsFromBoundaryArc(startX, startY, endX, endY,clump);
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
						ConcavityRegionAdministration.allConcavityRegionPoints.addAll(pointList);
						concavityRegion.setConcavityPixelList(concavityPixelList);
						concavityRegionList.add(concavityRegion);
					}
				}
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
	private ArrayList<Point2D> getAllEmbeddedPointsFromBoundaryArc(int startX, int startY, int endX, int endY, Clump clump)
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
		if (!ended)
		{
			i = clump.getBoundary().npoints - 1;
			while (i >= 0 && !ended)
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
				i--;
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
