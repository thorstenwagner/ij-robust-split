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
public class StraightSplitLineBetweenTwoConcavityRegions implements StraightSplitLine
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
	 */
	public StraightSplitLineBetweenTwoConcavityRegions(ConcavityRegion cI, ConcavityRegion cJ, double saliency,
			double concavityConcavityAlignment, double concavityLineAlignment)
	{
		this.cI = cI;
		this.cJ = cJ;
		this.saliency = saliency;
		this.concavityConcavityAlignment = concavityConcavityAlignment;
		this.concavityConcavityAlignment = concavityLineAlignment;

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

	/**
	 * draws the Straight Line Between 2 ConcavityRegions between the ConcavityPoints
	 * @param ip Image Processor to draw the Line
	 */
	public void drawLine(ImageProcessor ip)
	{
		ip.drawLine((int) cI.getMaxDistCoord().getX(), (int) cI.getMaxDistCoord().getY(),
				(int) cJ.getMaxDistCoord().getX(), (int) cJ.getMaxDistCoord().getY());
	}

}
