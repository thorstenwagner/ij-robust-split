package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;

public class ConcavityPixel
{

	private Point2D position;
	private double distance;
	private ConcavityRegion concavityRegion;
	
	public ConcavityPixel(Point2D position, double distance, ConcavityRegion concavityRegion)
	{
		this.position=position;
		this.distance= distance;
		this.concavityRegion=concavityRegion;
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
