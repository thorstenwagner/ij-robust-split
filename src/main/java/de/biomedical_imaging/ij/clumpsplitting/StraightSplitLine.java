package de.biomedical_imaging.ij.clumpsplitting;

import ij.process.ImageProcessor;

public class StraightSplitLine implements AbstractSplitLine{
	private ConcavityRegion cOne;
	private ConcavityRegion cTwo;
	private double saliency;
	private double concavityConcavityAlignment;
	private double concavityLineAlignment;
	
	public StraightSplitLine(ConcavityRegion cOne, ConcavityRegion cTwo,double saliency, double concavityConcavityAlignment, double concavityLineAlignment)
	{
		this.cOne=cOne;
		this.cTwo=cTwo;
		this.saliency=saliency;
		this.concavityConcavityAlignment=concavityConcavityAlignment;
		this.concavityConcavityAlignment=concavityLineAlignment;
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
	public void drawLine(ImageProcessor ip)
	{
		ip.drawLine((int)cOne.getMaxDistCoord().getX(), (int)cOne.getMaxDistCoord().getY(), (int)cTwo.getMaxDistCoord().getX(), (int)cTwo.getMaxDistCoord().getY());
	}
	
	

}
