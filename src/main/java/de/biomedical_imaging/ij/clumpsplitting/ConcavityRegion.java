package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import ij.IJ;
import ij.process.ImageProcessor;
/**
 * 
 * @author Louise
 *
 */
public class ConcavityRegion {
	
private int startX;
private int startY;
private int endX;
private int endY;
private double max;
private int indexMax;
private ArrayList<Double> doubleList;
private ArrayList<Point2D> boundaryPointList;
/**
 * Markiert den Pixel mit der größten Distanz zur konvexenHülle
 * @param imp
 */
public void markMax(ImageProcessor ip)
{	IJ.showMessage(max+" "+indexMax+ " "+ boundaryPointList.get(indexMax).getX()+ " "+ boundaryPointList.get(indexMax).getY());
	Point2D p=boundaryPointList.get(indexMax);
	//ImageProcessor ip=imp.getProcessor();
	ip.setColor(Color.BLUE);
	ip.setLineWidth(10);
	ip.drawDot((int)p.getX(), (int)p.getY());
	ip.setLineWidth(1);
}
/**
 * erzeugt eine ConcavityRegion, d.h. eine Region, mit großer Konkavität
 * @param startX x-Anfangspunkt der Region
 * @param startY  y-Anfangspunkt der Region
 * @param endX  x-Endpunkt der Region
 * @param endY  y-Endpunkt der Region
 * @param boundaryPointList Liste aller Pixel, die in der ConcavityRegion liegen
 * @param doubleList Liste mit allen Abständen der Punkte
 * @param max größter Abstand zwischen Boundary und Convex Hull
 * @param maxIndex index des größten Abstands bezogen auf die boundaryPointList
 */
public ConcavityRegion(int startX,int startY, int endX, int endY,ArrayList<Point2D> boundaryPointList, ArrayList<Double> doubleList, double max, int maxIndex)
{
	this.startX=startX;
	this.startY=startY;
	this.endX=endX;
	this.endY=endY;
	this.boundaryPointList=boundaryPointList;
	this.doubleList=doubleList;
	this.max=max;
	this.indexMax=maxIndex;

	
}
public int getStartX()
{
	return startX;
}
public int getStartY()
{
	return startY;
}
public int getEndX()
{
	return endX;
}
public int getEndY()
{
	return endY;
}
public ArrayList<Double> getDistList()
{
	return doubleList;
}
public Point2D getMaxDistCoord()
{
	return boundaryPointList.get(indexMax);
}

 
public double getMaxDist(){
	return max;
}


}
