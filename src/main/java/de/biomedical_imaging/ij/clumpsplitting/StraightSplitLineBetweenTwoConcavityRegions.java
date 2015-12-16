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
