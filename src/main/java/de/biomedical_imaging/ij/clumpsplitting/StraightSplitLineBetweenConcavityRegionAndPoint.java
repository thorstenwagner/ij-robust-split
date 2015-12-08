package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;

import ij.IJ;
import ij.process.ImageProcessor;

public class StraightSplitLineBetweenConcavityRegionAndPoint implements StraightSplitLine{

	ConcavityRegion cOne;
	double concavityAngle;
	double concavityRatio;
	Point2D.Double point;
	public StraightSplitLineBetweenConcavityRegionAndPoint(ConcavityRegion cOne,double concavityAngle, double concavityRatio, Point2D.Double point)
	{
		this.cOne=cOne;
		this.concavityAngle=concavityAngle;
		this.concavityRatio=concavityRatio;
		this.point=point;
	}
	public void drawLine(ImageProcessor ip)
	{
		IJ.log("cOne"+cOne.getMaxDistCoord().getX()+" "+ cOne.getMaxDistCoord().getY());
		IJ.log("line:"+ point.getX()+" "+ point.getY());
		ip.drawLine((int)cOne.getMaxDistCoord().getX(), (int)cOne.getMaxDistCoord().getY(), (int)point.getX(), (int)point.getY());
		ip.setLineWidth(10);
		ip.drawDot((int)point.getX(), (int)point.getY());
		ip.setLineWidth(1);
	}
}
