package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;

import ij.process.ImageProcessor;

public class StraightSplitLineBetweenTwoConcavityRegions implements StraightSplitLine{
	private ConcavityRegion cOne;
	private ConcavityRegion cTwo;
	private double saliency;
	private double concavityConcavityAlignment;
	private double concavityLineAlignment;
	
	public StraightSplitLineBetweenTwoConcavityRegions(ConcavityRegion cOne, ConcavityRegion cTwo,double saliency, double concavityConcavityAlignment, double concavityLineAlignment)
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
