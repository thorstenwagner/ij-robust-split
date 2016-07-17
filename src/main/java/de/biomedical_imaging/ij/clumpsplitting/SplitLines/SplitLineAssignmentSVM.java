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

/**
 * responsible for the storage of the SplitLines to serialize and to train an
 * SVM to find optimal Parameters C1 and C2 for the plugin
 * 
 * @author Louise
 *
 */
public class SplitLineAssignmentSVM
{

	/**
	 * x Coordinate of the StartPoint of the SplitLine
	 */
	private int startX;
	/**
	 * y Coordinate of the StartPoint of the SplitLine
	 */
	private int startY;
	/**
	 * x Coordinate of the EndPoint of the SplitLine
	 */
	private int endX;
	/**
	 * y Coordinate of the EndPoint of the SplitLine
	 */
	private int endY;
	/**
	 * value 1 if it is a good SplitLine or value -1 if it is not a splitLine
	 */
	private int classificationValue;
	/**
	 * distance between ConcavityPixels, corresponds to the y Axis of the SVM
	 */
	private double distance;
	/**
	 * sum of the ConcavityDepths, corresponds to the x Axis of the SVM
	 */
	private double sumConcavityDepth;

	public int getClassificationValue()
	{
		return classificationValue;
	}

	public void setClassificationValue(int classificationValue)
	{
		this.classificationValue = classificationValue;
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

	public double getDistance()
	{
		return distance;
	}

	public double getSumConcavityDepth()
	{
		return sumConcavityDepth;
	}

	/**
	 * 
	 * @param startX
	 *            x Coordinate of the StartPoint of the SplitLine
	 * 
	 * @param startY
	 *            y Coordinate of the StartPoint of the SplitLine
	 * 
	 * @param endX
	 *            x Coordinate of the EndPoint of the SplitLine
	 * 
	 * @param endY
	 *            y Coordinate of the EndPoint of the SplitLine
	 * 
	 * @param classificationValue
	 *            value 1 if it is a valid SplitLine or value -1 if it is not a
	 *            splitLine
	 *
	 * @param distance
	 *            distance between ConcavityPixels, corresponds to the y Axis of
	 *            the SVM
	 * 
	 * @param sumConcavityDepth
	 *            sum of the ConcavityDepths, corresponds to the x Axis of the
	 *            SVM
	 * 
	 */
	public SplitLineAssignmentSVM(int startX, int startY, int endX, int endY, int classificationValue, double distance,
			double sumConcavityDepth)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.classificationValue = classificationValue;
		this.distance = distance;
		this.sumConcavityDepth = sumConcavityDepth;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof SplitLineAssignmentSVM)
		{
			SplitLineAssignmentSVM slaSVM = (SplitLineAssignmentSVM) o;
			if (slaSVM.getStartX() == this.getStartX() && slaSVM.getEndX() == this.getEndX()
					&& slaSVM.getStartY() == this.getStartY() && slaSVM.getEndY() == this.getEndY())
			{
				return true;
			} else
			{
				if (slaSVM.getStartX() == this.getEndX() && slaSVM.getEndX() == this.getStartX()
						&& slaSVM.getEndY() == this.getEndY() && slaSVM.getEndY() == this.getStartY())
				{
					return true;
				} else
				{
					return false;
				}
			}
		} else
		{
			return false;
		}
	}
}
