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

package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;

/**
 * Class represents a ConcavityPixel, a ConcavityPixel normally contains to a
 * Concavity Region, but it is also used for the SplitPoints of SplitLines
 * between a ConcavityRegion and a contour point, in this case ConcavityRegion
 * is null. A concavityRegion can have more than one ConcavityPixel A
 * concavityPixel is represented by a position of the image and the distance
 * between ConcavityRegion and Contour
 * 
 * @author Louise
 *
 */
public class ConcavityPixel
{

	/**
	 * position of the ConcavityPixel
	 */
	private Point2D position;
	/**
	 * distance between concavityPixel and Convex Hull
	 */
	private double distance;
	/**
	 * ConcavityPixel contains to a Concavity Region, if ConcavityRegion is
	 * null, it should be a Pixel of Contour for a SplitLine between
	 * concavityRegion and contour point
	 */
	private ConcavityRegion concavityRegion;

	/**
	 * Constructs a ConcavityPixel
	 * 
	 * @param position
	 *            position of the ConcavityPixel at the image
	 * @param distance
	 *            distance between concavityPixel and Convex Hull in pixels
	 * 
	 * @param concavityRegion
	 *            ConcavityPixel contains to a Concavity Region, if
	 *            ConcavityRegion is null, it should be a Pixel of Contour for a
	 *            SplitLine between concavityRegion and contour point
	 */
	public ConcavityPixel(Point2D position, double distance, ConcavityRegion concavityRegion)
	{
		this.position = position;
		this.distance = distance;
		this.concavityRegion = concavityRegion;
	}

	public Point2D getPosition()
	{
		return position;
	}

	public double distance()
	{
		return distance;
	}

	public ConcavityRegion getConcavityRegion()
	{
		return concavityRegion;
	}
}
