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

/**
 * Point for Dijkstra Algorithm for GeodesicDistance- MinimumIntensity and
 * MaximumintensitySplitLines represents the shortest Path of the shortest path
 * from Startnode to the actual node to Compute optimal Path between the
 * detected ConcavityPoints it follows the 4 connected pixel connectivity
 * 
 * @author Louise
 *
 */
public class AccessiblePoint implements Comparable<AccessiblePoint>
{
	/**
	 * Position of the Point related on the Actual Image
	 */
	private Point2D point;
	/**
	 * distance between startPoint and Actual Point
	 */
	private double weight;
	/**
	 * relation to previous Accessible Point to trace back the optimal path
	 * linked like a simple linked list
	 */
	private AccessiblePoint previous;

	/**
	 * 
	 * @param point
	 *            Position of the Point related on the Actual Image
	 * @param weight
	 *            distance between startPoint and Actual Point
	 * @param previous
	 *            relation to previous Accessible Point to trace back the
	 *            optimal path
	 */
	public AccessiblePoint(Point2D point, double weight, AccessiblePoint previous)
	{
		this.point = point;
		this.weight = weight;
		this.previous = previous;
	}

	public Point2D getPoint()
	{
		return point;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	public double getWeight()
	{
		return weight;
	}

	public AccessiblePoint getPrevious()
	{
		return previous;
	}

	@Override
	public int compareTo(AccessiblePoint o)
	{
		if (this.getWeight() > o.getWeight())
		{
			return 1;
		} else
		{
			if (this.getWeight() < o.getWeight())
			{
				return -1;
			} else
			{
				return 0;
			}
		}
	}
}
