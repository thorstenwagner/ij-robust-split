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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import ij.process.ImageProcessor;

/**
 * Represents a SplitLine consisting of constituend Points in a 4 point
 * neigbourhood
 * 
 * @author Louise
 *
 */
public class PointSplitLine implements AbstractSplitLine
{
	/**
	 * List of all Points of the SplitLine
	 */
	private ArrayList<Point2D> cutPoints;

	public PointSplitLine(ArrayList<Point2D> points)
	{
		this.cutPoints = points;
	}

	/**
	 * draws each point of the cutpoints, so a constituent Line appears between
	 * the ConcavityPixels
	 */
	@Override
	public void drawLine(ImageProcessor ip, ImageProcessor binary)
	{
		if (Clump_Splitting.BACKGROUNDCOLOR == 0)
		{

			binary.setColor(Color.black);
			ip.setColor(Color.black);
		} else
		{

			binary.setColor(Color.white);
			ip.setColor(Color.white);
		}
		for (Point2D point : cutPoints)
		{

			ip.drawLine4((int) point.getX(), (int) point.getY(), (int) point.getX(), (int) point.getY());

			binary.drawLine4((int) point.getX(), (int) point.getY(), (int) point.getX(), (int) point.getY());

		}
	}

	@Override
	public Point2D getStartPoint()
	{

		return this.cutPoints.get(0);
	}

	@Override
	public Point2D getEndPoint()
	{
		return this.cutPoints.get(cutPoints.size() - 1);
	}

	@Override
	public double distance()
	{

		return this.cutPoints.size();
	}

}
