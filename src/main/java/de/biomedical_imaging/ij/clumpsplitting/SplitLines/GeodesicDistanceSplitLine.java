package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import ij.process.ImageProcessor;

public class GeodesicDistanceSplitLine implements AbstractSplitLine
{
	private ArrayList<Point2D> cutPoints;

	public GeodesicDistanceSplitLine(ArrayList<Point2D> points)
	{
		this.cutPoints=points;
	}
	@Override
	public void drawLine(ImageProcessor ip)
	{
		//ip.setLineWidth(3);
		if(Clump_Splitting.BACKGROUNDCOLOR==0)
		{
		ip.setColor(Color.black);
		}
		else{
			ip.setColor(Color.white);
		}
		for(Point2D point:cutPoints)
		{
			
			ip.drawLine4((int)point.getX(), (int)point.getY(),(int)point.getX(), (int)point.getY());
	
		}
	}

}
