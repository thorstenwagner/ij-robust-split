package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.process.ImageProcessor;

public class OuterConcavityRegionDetectorFarhan implements AbstractOuterConcavityRegionDetector
{

	@Override
	public ArrayList<ConcavityRegion> computeOuterConcavityRegions(ImageProcessor binary, Clump clump)
	{
		Polygon contour = clump.getBoundary();

		int konstante = 10;

		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		/*
		 * find Concave Regions by iterate about the Contour of the Clump BEGIN
		 * Region if midPoint of the Line between i+10 and i-10 doesn't contain
		 * the Polygon definded by the Contour CLOSE REGION at The First Point
		 * where the midpoint contains the the Polygon, which is definded by the
		 * Contour
		 */
		for (int i = 0; i < contour.npoints - 1; i++)
		{
			Point2D iMinus = null;
			Point2D iPlus = null;
			if (contour.npoints > 2 * konstante)
			{
				if (i < konstante)
				{
					iMinus = new Point2D.Double(contour.xpoints[contour.npoints - (konstante + 2) + i],
							contour.ypoints[contour.npoints - (konstante + 2) + i]);
					iPlus = new Point2D.Double(contour.xpoints[i + konstante], contour.ypoints[i + konstante]);
				} else
				{
					if (i + konstante > contour.npoints - 1)
					{
						iMinus = new Point2D.Double(contour.xpoints[i - konstante], contour.ypoints[i - konstante]);
						iPlus = new Point2D.Double(contour.xpoints[i + konstante - (contour.npoints - 1)],
								contour.ypoints[i + konstante - (contour.npoints - 1)]);
					} else
					{
						iMinus = new Point2D.Double(contour.xpoints[i - konstante], contour.ypoints[i - konstante]);
						iPlus = new Point2D.Double(contour.xpoints[i + konstante], contour.ypoints[i + konstante]);
					}
				}

				double xDist = iMinus.getX() + iPlus.getX();
				double yDist = iMinus.getY() + iPlus.getY();
				Point2D midPoint = new Point2D.Double(Math.round((xDist / 2)), Math.round((yDist / 2)));

				if (!contour.contains(midPoint))
				{
					ArrayList<Point2D> embeddedPoints = this.getAllEmbeddedPointsFromBoundaryArc((int) iPlus.getX(),
							(int) iPlus.getY(), (int) iMinus.getX(), (int) iMinus.getY(), clump);
					ArrayList<Double> distList = this.computeDistance(embeddedPoints, (int) iMinus.getX(),
							(int) iMinus.getY(), (int) iPlus.getX(), (int) iPlus.getY());

					LargestDistanceConcavityPixelDetector ldcpd = new LargestDistanceConcavityPixelDetector();
					ConcavityRegion concavityRegion = new ConcavityRegion((int) iMinus.getX(), (int) iMinus.getY(),
							(int) iPlus.getX(), (int) iPlus.getY(), embeddedPoints, distList);
					ArrayList<ConcavityPixel> p = ldcpd.computeConcavityPixel(concavityRegion);
					if (p.size() > 0)
					{
						if (p.get(0).distance() > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
						{
							int minusX = 0;
							int minusY = 0;
							int plusX = 0;
							int plusY = 0;
							ConcavityPixel cp = p.get(0);
							int x = this.getPositionOfPointAtBoundary(contour, cp.getPosition().getX(),
									cp.getPosition().getY());
							if (x >= 10)
							{
								minusX = contour.xpoints[x - 10];
								minusY = contour.ypoints[x - 10];

							} else
							{

								minusX = contour.xpoints[(contour.npoints - 2) + (x - 10)];
								minusY = contour.ypoints[(contour.npoints - 2) + (x - 10)];

							}
							if (x + 10 <= contour.npoints - 2)
							{
								plusX = contour.xpoints[x + 10];
								plusY = contour.ypoints[x + 10];

							} else
							{
								plusX = contour.xpoints[(x + 10) - (contour.npoints - 2)];
								plusY = contour.ypoints[(x + 10) - (contour.npoints - 2)];

							}
							ArrayList<Point2D> pointList = getAllEmbeddedPointsFromBoundaryArc(plusX,
									plusY,minusX,minusY, clump); //
							if (pointList.size() > 3)
							{
								ArrayList<Double> doubleList = computeDistance(pointList, minusX, minusY, plusX, plusY);
								AbstractConcavityPixelDetector adcpd = null;

								if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTALLCONCAVITYPIXELS)
								{
									adcpd = new AllConcavityPixelDetector();
								} else
								{
									if (Clump_Splitting.CONCAVITYPIXELDETECOTORTYPE == ConcavityPixelDetectorType.DETECTCONCAVITYPIXELSWITHLARGESTCONCAVITYDEPTH)
									{
										adcpd = new LargestDistanceConcavityPixelDetector();
									}
								}
								ConcavityRegion concavityRegion1 = new ConcavityRegion(minusX, minusY, plusX, plusY,
										pointList, doubleList);

								ArrayList<ConcavityPixel> concavityPixelList = adcpd
										.computeConcavityPixel(concavityRegion1);

								if (concavityPixelList.size() > 0)
								{
									concavityRegion1.setConcavityPixelList(concavityPixelList);

									concavityRegionList.add(concavityRegion1);
									i+=10;
								}
							}
						}
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
	private ArrayList<Point2D> getAllEmbeddedPointsFromBoundaryArc(int startX, int startY, int endX, int endY,
			Clump clump)
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
	private int getPositionOfPointAtBoundary(Polygon polygon, double x, double y)
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

}
