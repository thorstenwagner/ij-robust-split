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
	public void drawLine(ImageProcessor ip, ImageProcessor binary)
	{
		for(Point2D p: points)
		{
		//	ip.setLineWidth(3);
			if(Clump_Splitting.BACKGROUNDCOLOR==0)
			{

				binary.setColor(Color.black);
			ip.setColor(Color.black);
			}
			else{

				binary.setColor(Color.white);
				ip.setColor(Color.white);
			}
			ip.drawLine4((int)p.getX(), (int)p.getY(),(int)p.getX(), (int)p.getY());
			binary.drawLine4((int)p.getX(), (int)p.getY(),(int)p.getX(), (int)p.getY());
		}
	
	}
	@Override
	public double distance()
	{
		return this.points.size();
	}		
	
}
