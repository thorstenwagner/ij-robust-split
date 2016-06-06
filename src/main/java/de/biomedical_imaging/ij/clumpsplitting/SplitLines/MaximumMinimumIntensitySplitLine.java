package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import ij.process.ImageProcessor;

public class MaximumMinimumIntensitySplitLine implements AbstractSplitLine
{

	private ArrayList<Point2D> points= new ArrayList<Point2D>();
	
	public Point2D getStartPoint()
	{
		if(points.size()>0)
		{
		return points.get(0);
		}
		return null;
	}
	public Point2D getEndPoint()
	{
		if(points.size()>0)
		{
		return points.get(points.size()-1);
		}
		return null;
	}
	public MaximumMinimumIntensitySplitLine(ArrayList<Point2D> points)
	{
		this.points=points;
	}
	@Override
	public void drawLine(ImageProcessor ip)
	{
		for(Point2D p: points)
		{
			ip.setLineWidth(3);
			if(Clump_Splitting.BACKGROUNDCOLOR==0)
			{
			ip.setColor(Color.black);
			}
			else{
				ip.setColor(Color.white);
			}
			ip.drawDot((int)p.getX(), (int)p.getY());
		}
	
	}		
	
}
