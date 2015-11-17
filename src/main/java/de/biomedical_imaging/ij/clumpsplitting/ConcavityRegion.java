package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ConcavityRegion {
private int startX;
private int startY;
private int endX;
private int endY;
private double max;
private int indexMax;
private ArrayList<Point2D> boundaryPointList;

public void markMax(ImagePlus imp)
{
	Point2D p=boundaryPointList.get(indexMax);
	ImageProcessor ip=imp.getProcessor();
	ip.setColor(Color.ORANGE);
	ip.setLineWidth(10);
	ip.drawDot((int)p.getX(), (int)p.getY());
}
public ConcavityRegion(int startX,int startY, int endX, int endY,ArrayList<Point2D> boundaryPointList)
{
	this.startX=startX;
	this.startY=startY;
	this.endX=endX;
	this.endY=endY;
	this.boundaryPointList=boundaryPointList;
	ArrayList<Double> a=this.computeDistance();
	this.getMaxDist(a);
	
}
public Point2D getMaxDistCoord()
{
	return boundaryPointList.get(indexMax);
}
public ArrayList<Double> computeDistance()
{
	ArrayList<Double> doubleList=new ArrayList<Double>();
	Line2D.Double line=new Line2D.Double(startX, startY, endX, endY);
	Double dist;
	for(Point2D point:boundaryPointList)
	{
		dist=line.ptLineDist(point);
		doubleList.add(dist);
	}
	return doubleList;
}
 
public double getMaxDist(){
	return max;
}

private void getMaxDist(ArrayList<Double> distList)
{
	
	int index=0;
	for(double d:distList)
	{
		if(d>max)
		{
			max=d;
			indexMax=index;
		}
		index++;
	}
}
}
