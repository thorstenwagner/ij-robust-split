package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.geom.Point2D;

public class AccessiblePoint implements Comparable<AccessiblePoint>
{

	private Point2D point;
	private double weight;
	private AccessiblePoint previous;
	
	public AccessiblePoint(Point2D point, double weight, AccessiblePoint previous){
		this.point=point;
		this.weight=weight;
		this.previous=previous;
	}
	
	public Point2D getPoint()
	{
		return point;
	}
	public void setWeight(double weight)
	{
		this.weight=weight;
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
		if(this.getWeight()>o.getWeight())
		{
			return 1;
		}
		else{
			if(this.getWeight()<o.getWeight())
			{
				return -1;
			}
			else{
				return 0;
			}
		}
	}
}
