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
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityPixel;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.gui.Line;
import ij.process.ImageProcessor;

/**
 * represents a SplitLine between a ConcavityRegion and a point on the boundary,
 * this kind of SplitLine is only used, if no other possible SplitLines can be
 * detected
 * 
 * @author Louise
 *
 */
public class StraightSplitLineBetweenConcavityRegionAndPoint extends StraightSplitLine
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
	 * the selected Point to split the Clump at the boundary
	 */
	private ConcavityPixel endConcavityPixel;
	/**
	 * StartPoint for the SplitLine, which contains to a ConcavityRegion
	 */
	private ConcavityPixel startConcavityPixel;

	/**
	 * 
	 * @param cI
	 *            concavityRegion with the largest concavityDepth of the Clump
	 *            which should be used to split the Clump with a boundaryPoint
	 * 
	 * @param concavityAngle
	 *            angle between startPoint of the concavityRegion, endPoint of
	 *            the concavityRegion and the Point with the largest
	 *            concavityDepth of the concavityRegion
	 * 
	 * @param concavityRatio
	 *            ratio between the actual largest concavityDepth and the second
	 *            largest concavityDepth
	 * 
	 * @param endConcavityPixel
	 *            the selected Point to split the Clump at the boundary
	 *
	 * @param startConcavityPixel
	 *            StartPoint for the SplitLine, which contains to a
	 *            ConcavityRegion
	 * 
	 */
	public StraightSplitLineBetweenConcavityRegionAndPoint(ConcavityRegion cI, double concavityAngle,
			double concavityRatio, Point2D endConcavityPixel, ConcavityPixel startConcavityPixel)
	{
		this.cI = cI;
		this.concavityAngle = concavityAngle;
		this.concavityRatio = concavityRatio;
		this.endConcavityPixel = new ConcavityPixel(endConcavityPixel, 0, null);
		this.startConcavityPixel = startConcavityPixel;
	}

	public double getConcavityAngle()
	{
		return concavityAngle;
	}

	public double getConcavityRatio()
	{
		return concavityRatio;
	}

	public ConcavityPixel getEndConcavityPixel()
	{
		return endConcavityPixel;
	}

	public ConcavityPixel getStartConcavityPixel()
	{

		return this.startConcavityPixel;
	}

	public ConcavityRegion getCI()
	{
		return cI;
	}

	/**
	 * draws the Splitline as a Straight Line Between the selected Points
	 * 
	 * @param ip
	 *            ImageProcessor to draw the SplitLine
	 */
	@Override
	public void drawLine(ImageProcessor ip, ImageProcessor binary)
	{
		if (cI != null)
		{
			if (this.getEndConcavityPixel().getPosition() != null)
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
				ip.drawLine4((int) this.getStartConcavityPixel().getPosition().getX(),
						(int) this.getStartConcavityPixel().getPosition().getY(),
						(int) this.getEndConcavityPixel().getPosition().getX(),
						(int) this.getEndConcavityPixel().getPosition().getY());

				binary.drawLine4((int) this.getStartConcavityPixel().getPosition().getX(),
						(int) this.getStartConcavityPixel().getPosition().getY(),
						(int) this.getEndConcavityPixel().getPosition().getX(),
						(int) this.getEndConcavityPixel().getPosition().getY());

				if (Clump_Splitting.SHOWPIXELS)
				{

					Line polygonRoi = new Line(this.getEndConcavityPixel().getPosition().getX(),
							this.getEndConcavityPixel().getPosition().getY(),
							this.getEndConcavityPixel().getPosition().getX(),
							this.getEndConcavityPixel().getPosition().getY());

					polygonRoi.setStrokeWidth(3);
					polygonRoi.setStrokeColor(Color.red);

					Clump_Splitting.overlaySplitPoints.add(polygonRoi);
				} else
				{
					Clump_Splitting.overlaySplitPoints.clear();
				}

			}
		}
	}

	@Override
	public String toString()
	{
		return "StraightSplitLineBetweenConcavityRegionAndPointX: " + this.getEndConcavityPixel().getPosition().getX()
				+ " Y: " + this.getEndConcavityPixel().getPosition().getY() + " MaxX: "
				+ this.getStartConcavityPixel().getPosition().getX() + " MaxY: "
				+ this.getStartConcavityPixel().getPosition().getY();

	}

	@Override
	public Point2D getStartPoint()
	{

		return this.getStartConcavityPixel().getPosition();
	}

	@Override
	public Point2D getEndPoint()
	{
		return this.getEndConcavityPixel().getPosition();
	}

	@Override
	public double distance()
	{

		double distX = Math.abs(this.getStartPoint().getX() - this.getEndPoint().getX());
		double distY = Math.abs(this.getStartPoint().getY() - this.getEndPoint().getY());
		double dist = Math.sqrt(distX * distX + distY * distY);
		return dist;
	}

}
