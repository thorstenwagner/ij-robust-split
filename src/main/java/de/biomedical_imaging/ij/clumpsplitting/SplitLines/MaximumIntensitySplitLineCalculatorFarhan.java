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
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegionAdministration;

import ij.process.ImageProcessor;

/**
 * Class to compute MaximumIntensity SplitLine, a maximumintensity SplitLine
 * follows the path between start and endPoint of maximum intensity
 * 
 * the idea of Farhan et al. is, to follow the adjacent Pixel in the direction
 * of the computed orientation of the ConcavityRegion, the pixel with the
 * largest ConcavityValue in this direction is choosed, as long, as another
 * boundaryPoint of the Countour is reached
 *
 * @author Louise
 *
 */

public class MaximumIntensitySplitLineCalculatorFarhan implements AbstractSplitLineCalculator
{

	/**
	 * neighborhood to check if the orientation of the ConcavityRegion is
	 * between 0 and 90 degrees to the horizontal
	 */
	static final int[][] NULLTONINETY =
	{
			{ 1, 1, 1 },
			{ 0, 0, 1 },
			{ 0, 0, 0 } };

	/**
	 * neighborhood to check if the orientation of the ConcavityRegion is
	 * between 90 and 180 degrees to the horizontal
	 */

	static final int[][] NINETYTOHUNDREDEIGHTY =
	{
			{ 1, 1, 0 },
			{ 1, 0, 0 },
			{ 1, 0, 0 } };

	/**
	 * neighborhood to check if the orientation of the ConcavityRegion is
	 * between 180 and 270 degrees to the horizontal
	 */

	static final int[][] HUNDREDEIGHTYTOTWOHUNDREDSEVENTY =
	{
			{ 0, 0, 0 },
			{ 1, 0, 0 },
			{ 1, 1, 1 } };

	/**
	 * neighborhood to check if the orientation of the ConcavityRegion is
	 * between 270 and 360 degrees to the horizontal
	 */

	static final int[][] TWOHUNDREDSEVENTTOTHREEHUNDREDSIXTY =
	{
			{ 0, 0, 1 },
			{ 0, 0, 1 },
			{ 0, 1, 1 } };

	/**
	 * calculates a possible splitLine by first compute the orientation of the
	 * ConcavityRegion and than looks for adjacent points in this direction to
	 * find pixel with high Intensity to calculate SplitLine
	 */
	@Override
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip, ImageProcessor binary)
	{
		ArrayList<AbstractSplitLine> splitLines = new ArrayList<AbstractSplitLine>();
		// take SplitLine with largest concavityDepth of the Clump
		Collections.sort(concavityRegionList);

		ArrayList<Point2D> points = new ArrayList<Point2D>();
		if (concavityRegionList.size() > 0)
		{
			ConcavityRegion cr = concavityRegionList.get(concavityRegionList.size() - 1);
			ArrayList<Point2D> maxDistList = cr.getMaxDistCoord();
			Point2D aktuellerPunkt = maxDistList.get(maxDistList.size() / 2);
			points.add(aktuellerPunkt);
			// compute orientation of the ConcavityRegion
			double orientation = cr.getOrientation(aktuellerPunkt);
			int[][] filter;
			if (orientation > 0 && orientation <= (Math.PI / 2))
			{
				filter = MaximumIntensitySplitLineCalculatorFarhan.NULLTONINETY;
			} else
			{
				if (orientation > (Math.PI / 2) && orientation <= (Math.PI))
				{
					filter = MaximumIntensitySplitLineCalculatorFarhan.NINETYTOHUNDREDEIGHTY;

				} else
				{
					if (orientation > (Math.PI) && orientation <= ((Math.PI) * 1.5))
					{
						filter = MaximumIntensitySplitLineCalculatorFarhan.HUNDREDEIGHTYTOTWOHUNDREDSEVENTY;

					} else
					{
						filter = MaximumIntensitySplitLineCalculatorFarhan.TWOHUNDREDSEVENTTOTHREEHUNDREDSIXTY;

					}
				}
			}
			int value;
			if (Clump_Splitting.BACKGROUNDCOLOR == 1)
			{
				value = 255;
			} else
			{
				value = 0;
			}
			boolean equals = (binary.getPixel((int) aktuellerPunkt.getX(), (int) aktuellerPunkt.getY()) == value);
			/*
			 * search for largest Intensity Path by taking the next neighbor in
			 * the computed direction with the largest intensity, as long as it
			 * hasn't reached the Contour of the Clump
			 */
			while (!equals && aktuellerPunkt.getX() > 0 && aktuellerPunkt.getY() > 0
					&& aktuellerPunkt.getX() < ip.getWidth() & aktuellerPunkt.getY() < ip.getHeight())
			{
				int max = -10;
				Point2D temp = null;
				for (int m = -1; m <= 1; m++)
				{
					for (int n = -1; n <= 1; n++)
					{
						if (filter[m + 1][n + 1] == 1)
						{
							if ((orientation > 0 && orientation <= (Math.PI / 2) && m == -1 && n == 1)
									|| (orientation > (Math.PI / 2) && orientation <= (Math.PI) && m == -1 && n == -1)
									|| (orientation > (Math.PI) && orientation <= ((Math.PI) * 1.5) && m == 1
											&& n == -1)
									|| (orientation > ((Math.PI) * 1.5) && orientation <= ((Math.PI) * 2) && m == 1
											&& n == 1))
							{
								if (ip.getPixel((int) aktuellerPunkt.getX() + n,
										(int) aktuellerPunkt.getY() + m) >= max)
								{
									max = ip.getPixel((int) aktuellerPunkt.getX() + n, (int) aktuellerPunkt.getY() + m);
									temp = new Point2D.Double(aktuellerPunkt.getX() + n, aktuellerPunkt.getY() + m);
								}
							} else
							{
								if (ip.getPixel((int) aktuellerPunkt.getX() + n, (int) aktuellerPunkt.getY() + m) < max)
								{
									max = ip.getPixel((int) aktuellerPunkt.getX() + n, (int) aktuellerPunkt.getY() + m);
									temp = new Point2D.Double(aktuellerPunkt.getX() + n, aktuellerPunkt.getY() + m);
								}
							}
						}
					}
				}
				points.add(temp);
				aktuellerPunkt = temp;
				equals = (binary.getPixel((int) aktuellerPunkt.getX(), (int) aktuellerPunkt.getY()) == value);
			}
		}
		if (points.size() > 3)
		{
			if (points.get(points.size() - 1).getX() == 0 || points.get(points.size() - 1).getY() == 0
					|| points.get(points.size() - 1).getX() >= ip.getWidth()
					|| points.get(points.size() - 1).getY() >= ip.getHeight()
					|| ConcavityRegionAdministration.allConcavityRegionPoints.contains(points.get(points.size() - 1)))
			{
				PointSplitLine mmis = new PointSplitLine(points);
				splitLines.add(mmis);
			}
		}
		return splitLines;
	}

}
