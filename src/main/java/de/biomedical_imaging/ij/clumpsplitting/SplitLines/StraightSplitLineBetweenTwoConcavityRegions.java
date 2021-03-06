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
import ij.process.ImageProcessor;

/**
 * Straight SplitLine between two concavityRegions, this kind of SplitLine is
 * chosen if the saliency is larger than the threshold, the
 * concavityconcavityAlignement is smaller than the Threshold and the
 * concavityLineAlignement is smaller than the threshold
 * 
 * @author Louise
 *
 */
public class StraightSplitLineBetweenTwoConcavityRegions extends StraightSplitLine
		implements Comparable<StraightSplitLineBetweenTwoConcavityRegions>
{
	/**
	 * first concavityRegion at its concavityPoint would start the
	 * possibleSplitLine
	 */
	private ConcavityRegion cI;
	/**
	 * second concavityRegion at its concavityPoint would end the
	 * possibleSplitLine
	 */

	private ConcavityRegion cJ;
	/**
	 * a measure of the concaveness measure and the distance between the points,
	 * which is used to evaluate the quality of the possible Splitline
	 */
	private double saliency;
	/**
	 * Value to Classify if a SplitLine is valid or isn't.A high chi value is a
	 * sign for a good SplitLine
	 */
	private double chi;
	/**
	 * EndPoint for the SplitLine, which contains to a ConcavityRegion
	 */
	private ConcavityPixel endConcavityPixel;
	/**
	 * StartPoint for the SplitLine, which contains to a ConcavityRegion
	 */
	private ConcavityPixel startConcavityPixel;
	/**
	 * angle between the orientation of the two concavityRegions, which is used
	 * to evaluate the quality of the possible Splitline
	 */
	private double concavityConcavityAlignment;
	/**
	 * Maximum of the angles between the Splitline and the orientation of the
	 * concavityRegion, which is used to evaluate the quality of the possible
	 * Splitline
	 */
	private double concavityLineAlignment;

	/**
	 * 
	 * 
	 * @param cI
	 *            the first concavityRegion of a SplitLine
	 * @param cJ
	 *            the second concavityRegion of a SplitLine
	 * @param saliency
	 *            a measure of the concaveness measure and the distance between
	 *            the points
	 * @param concavityConcavityAlignment
	 *            angle between the orientation of the two concavityRegions
	 * @param concavityLineAlignment
	 *            Maximum of the angles between the Splitline and the
	 *            orientation of the concavityRegion
	 * @param chi
	 *            Value to Classify if a SplitLine is valid or isn't.A high chi
	 *            value is a sign for a good SplitLine
	 * 
	 * @param startConcavityPixel
	 *            StartPoint for the SplitLine, which contains to a
	 *            ConcavityRegion
	 *
	 * @param endConcavityPixel
	 *            EndPoint for the SplitLine, which contains to a
	 *            ConcavityRegion
	 *
	 */
	public StraightSplitLineBetweenTwoConcavityRegions(ConcavityRegion cI, ConcavityRegion cJ, double saliency,
			double concavityConcavityAlignment, double concavityLineAlignment, double chi,
			ConcavityPixel startConcavityPixel, ConcavityPixel endConcavityPixel)
	{
		this.cI = cI;
		this.cJ = cJ;
		this.saliency = saliency;
		this.concavityConcavityAlignment = concavityConcavityAlignment;
		this.concavityLineAlignment = concavityLineAlignment;
		this.chi = chi;
		this.startConcavityPixel = startConcavityPixel;
		this.endConcavityPixel = endConcavityPixel;
	}

	public double getConcavityConcavityAlignment()
	{
		return concavityConcavityAlignment;
	}

	public double getConcavityLineAlignment()
	{
		return concavityLineAlignment;
	}

	public double getSaliency()
	{
		return saliency;
	}

	public ConcavityPixel getStartConcavityPixel()
	{
		return this.startConcavityPixel;
	}

	public ConcavityPixel getEndConcavityPixel()
	{
		return this.endConcavityPixel;
	}

	/**
	 * draws the Straight Line Between 2 ConcavityRegions between the
	 * ConcavityPoints
	 * 
	 * @param ip
	 *            Image Processor to draw the Line
	 */
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
		ip.drawLine4((int) this.getStartConcavityPixel().getPosition().getX(),
				(int) this.getStartConcavityPixel().getPosition().getY(),
				(int) this.getEndConcavityPixel().getPosition().getX(),
				(int) this.getEndConcavityPixel().getPosition().getY());
		binary.drawLine4((int) this.getStartConcavityPixel().getPosition().getX(),
				(int) this.getStartConcavityPixel().getPosition().getY(),
				(int) this.getEndConcavityPixel().getPosition().getX(),
				(int) this.getEndConcavityPixel().getPosition().getY());
	}

	public double getChi()
	{
		return chi;
	}

	@Override
	public String toString()
	{
		return "X: " + this.getStartConcavityPixel().getPosition().getX() + " Y: "
				+ this.getStartConcavityPixel().getPosition().getY() + " X2: "
				+ this.getEndConcavityPixel().getPosition().getX() + " Y2: "
				+ this.getEndConcavityPixel().getPosition().getY();

	}

	public ConcavityRegion getCI()
	{
		return cI;
	}

	public ConcavityRegion getCJ()
	{
		return cJ;
	}

	@Override
	public int compareTo(StraightSplitLineBetweenTwoConcavityRegions o)
	{
		if (this.getChi() < o.getChi())
		{
			return -1;
		} else
		{
			if (this.getChi() > o.getChi())
			{
				return 1;
			} else
			{
				return 0;
			}
		}
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
