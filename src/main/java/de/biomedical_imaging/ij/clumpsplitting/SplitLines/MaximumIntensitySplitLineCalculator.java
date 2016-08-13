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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.process.ImageProcessor;

/**
 * Class to compute MaximumIntensity SplitLine, a maximumintensity SplitLine
 * follows the path between start and endPoint of maximum intensity
 *
 * @author Louise
 *
 */
public class MaximumIntensitySplitLineCalculator implements AbstractSplitLineCalculator
{

	/**
	 * Start Point, computed by StraightSplitLineCalculator
	 */
	private Point2D startPoint;
	/**
	 * end Point, computed by StraightSplitLineCalculator
	 */
	private Point2D endPoint;

	/**
	 * 
	 * @param startPoint
	 *            defines StartPoint, computed by StraightSplitLineCalculator
	 * @param endPoint
	 *            defines endPoint computed by StraightSplitLineCalculator, path
	 *            search should stop if endPoint is reached
	 */
	public MaximumIntensitySplitLineCalculator(Point2D startPoint, Point2D endPoint)
	{
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	/**
	 * calculates the Possible SplitLines of Maximumintensity by first define a
	 * region between the Points in which we would like to look for the path,
	 * and convert pixel intensities to a format, which is processible for
	 * Dijstra algorithm and last execute Dijkstra Algorithm
	 */
	@Override
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip, ImageProcessor binary)
	{

		ArrayList<AbstractSplitLine> splitLineList = new ArrayList<AbstractSplitLine>();
		int minX;
		int maxX;
		int konstante = 30;
		// define region for the splitLine
		if (startPoint != null && endPoint != null)
		{
			if (startPoint.getX() < endPoint.getX())
			{
				if (startPoint.getX() - konstante > 0)
				{
					// TODO Konstante
					minX = (int) startPoint.getX() - konstante;
				} else
				{
					minX = 0;
				}
				if (endPoint.getX() + konstante < ip.getWidth())
				{
					maxX = (int) endPoint.getX() + konstante;
				} else
				{
					maxX = ip.getWidth();
				}

			} else
			{
				if (endPoint.getX() - konstante > 0)
				{
					// TODO Konstante
					minX = (int) endPoint.getX() - konstante;
				} else
				{
					minX = 0;
				}
				if (startPoint.getX() + konstante < ip.getWidth())
				{
					maxX = (int) startPoint.getX()+konstante;
				} else
				{
					maxX = ip.getWidth();
				}

			}
			int minY;
			int maxY;
			if (startPoint.getY() < endPoint.getY())
			{
				// TODO Konstante
				if (startPoint.getY() - konstante > 0)
				{
					minY = (int) startPoint.getY() - konstante;
				} else
				{
					minY = 0;
				}
				if (endPoint.getY() + konstante < ip.getHeight())
				{
					maxY = (int) endPoint.getY() + konstante;
				} else
				{
					maxY = ip.getHeight();
				}

			} else
			{
				if (endPoint.getY() - konstante > 0)
				{
					minY = (int) endPoint.getY() - konstante;
				} else
				{
					minY = 0;
				}
				if (startPoint.getY() + konstante < ip.getHeight())
				{
					maxY = (int) startPoint.getY() + konstante;
				} else
				{
					maxY = ip.getHeight();
				}
			}
			double[][] werte = new double[maxX - minX + 2][maxY - minY + 2];
			// reverse scale of Intensity to make Dijkstra Processable
			for (int i = 0; i < maxX - minX+2; i++)
			{

				for (int j = 0; j < maxY - minY+2; j++)
				{
					werte[i][j] = -(ip.getPixel(i + minX, j + minY)) + 256;
				}
			}
			Point2D aktuellerPunkt = startPoint;
			// Dijkstra Algorithm
			ArrayList<AccessiblePoint> unusedPoints = new ArrayList<AccessiblePoint>();
			ArrayList<AccessiblePoint> usedPoints = new ArrayList<AccessiblePoint>();
			AccessiblePoint first = new AccessiblePoint(aktuellerPunkt, 0, null);
			usedPoints.add(first);

			// While
			while (!(aktuellerPunkt.getX() == endPoint.getX() && aktuellerPunkt.getY() == endPoint.getY()))
			{
				for (int i = -1; i <= 1; i++)
				{
					for (int j = -1; j <= 1; j++)
					{
						if (j == 0 || i == 0)
						{

							if (aktuellerPunkt.getX() + i >= minX && aktuellerPunkt.getX() + i <= maxX)
							{

								if (aktuellerPunkt.getY() + j >= minY && aktuellerPunkt.getY() + j <= maxY)
								{

									boolean besucht = false;
									boolean ready = false;
									for (AccessiblePoint ap : unusedPoints)
									{
										if (ap.getPoint().getX() == aktuellerPunkt.getX() + i
												&& ap.getPoint().getY() == aktuellerPunkt.getY() + j)
										{
											besucht = true;
											if (ap.getWeight() > (werte[(int) aktuellerPunkt.getX() + i
													- minX][(int) aktuellerPunkt.getY() + j - minY]
													+ first.getWeight()))
											{
												ap.setWeight((werte[(int) aktuellerPunkt.getX() + i
														- minX][(int) aktuellerPunkt.getY() + j - minY]
														+ first.getWeight()));
											}
										}
									}
									for (AccessiblePoint ap : usedPoints)
									{
										if (ap.getPoint().getX() == aktuellerPunkt.getX() + i
												&& ap.getPoint().getY() == aktuellerPunkt.getY() + j)
										{
											ready = true;
										}
									}

									if (besucht == false && ready == false)
									{
										Point2D temp = new Point2D.Double(aktuellerPunkt.getX() + i,
												aktuellerPunkt.getY() + j);
										AccessiblePoint ap = new AccessiblePoint(temp,
												(werte[(int) aktuellerPunkt.getX() + i
														- minX][(int) aktuellerPunkt.getY() + j - minY]
														+ first.getWeight()),
												first);
										unusedPoints.add(ap);
									}
								}
							}
						}
					}
				}
				Collections.sort(unusedPoints);
				if (unusedPoints.size() != 0)
				{
					aktuellerPunkt = unusedPoints.get(0).getPoint();
					first = unusedPoints.get(0);
					usedPoints.add(unusedPoints.get(0));
					unusedPoints.remove(0);
				}
			}
			ArrayList<Point2D> pointList = new ArrayList<Point2D>();
			// trace back path

			while (first != null)
			{
				pointList.add(first.getPoint());
				first = first.getPrevious();
			}

			PointSplitLine gdsl = new PointSplitLine(pointList);
			splitLineList.add(gdsl);
			return splitLineList;
		}
		else{
			return null;
		}
	}

}
