/*
The MIT License (MIT)

Copyright (c) 2016 Louise Bloch (louise.bloch001@stud.fh-dortmund.de), Thorsten Wagner (wagner@b
iomedical-imaging.de)

Permission is hereby granted, free of charge, to any person obtaining a
copy
of this software and associated documentation files (the "Software"),
to deal
in the Software without restriction, including without limitation the
rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE
SOFTWARE.
*/

package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.gui.Line;
import ij.process.ImageProcessor;

/**
 * represents a SplitLine between a ConcavityRegion and a point on the boundary,
 * this kind of SplitLine is only used, if no other possible SplitLines can be
 * detected
 * 
 * @author Louise
 *
 */
public class StraightSplitLineBetweenConcavityRegionAndPoint extends StraightSplitLine
{

	/**
	 * valid ConcavityRegion with the largest concavityDepth of a Clump
	 */
	ConcavityRegion cI;
	/**
	 * angle between startPoint of the concavityRegion, endPoint of the
	 * concavityRegion and the Point with the largest concavityDepth of the
	 * concavityRegion
	 */
	double concavityAngle;
	/**
	 * ratio between the actual largest concavityDepth and the second largest
	 * concavityDepth
	 */
	double concavityRatio;
	/**
	 * the selected Point to split the Clump
	 */
	Point2D.Double point;

	/**
	 * 
	 * @param cI
	 *            concavityRegion with the largest concavityDepth of the Clump
	 *            which should be used to split the Clump with a boundaryPoint
	 * @param concavityAngle
	 *            angle between startPoint of the concavityRegion, endPoint of
	 *            the concavityRegion and the Point with the largest
	 *            concavityDepth of the concavityRegion
	 * 
	 * @param concavityRatio
	 *            ratio between the actual largest concavityDepth and the
	 *            sencond largest concavityDepth
	 * @param point
	 *            the selected Point to split the Clump
	 */
	public StraightSplitLineBetweenConcavityRegionAndPoint(ConcavityRegion cI, double concavityAngle,
			double concavityRatio, Point2D.Double point)
	{
		this.cI = cI;
		this.concavityAngle = concavityAngle;
		this.concavityRatio = concavityRatio;
		this.point = point;
	}

/*	public boolean contains(Point2D p)
	{
		Line2D.Double linie=new Line2D.Double((int) cI.getMaxDistCoord().getX(), (int) cI.getMaxDistCoord().getY(), (int) point.getX(),
				(int) point.getY());
		if(linie.contains(p))
		{
			return true;
		}
		else{
			return false;
		}
	}*/
	public double getConcavityAngle()
	{
		return concavityAngle;
	}
	public double getConcavityRatio()
	{
		return concavityRatio;
	}
	public Point2D getEndPoint()
	{
		return point;
	}
	public Point2D getStartPoint()
	{
		ArrayList<Point2D> pointList= cI.getMaxDistCoord();
		return pointList.get(pointList.size()/2);
	}
	public ConcavityRegion getCI()
	{
		return cI;
	}
		/**
	 * draws the Splitline
	 * @param ip ImageProcessor to draw the SplitLine
	 */
	public void drawLine(ImageProcessor ip)
	{
		//System.out.println("ConcAndPoint Error");
		if(cI!=null){
			if(point!=null)
			{
		ip.setLineWidth(3);
		if(Clump_Splitting.BACKGROUNDCOLOR==0)
		{
		ip.setColor(Color.black);
		}
		else{
			ip.setColor(Color.white);
		}
		ip.drawLine((int) this.getStartPoint().getX(), (int) this.getStartPoint().getY(), (int) point.getX(),
				(int) point.getY());
		if(Clump_Splitting.SHOWPIXELS)
		{
		//	ip.setColor(Color.gray);
		//ip.setLineWidth(10);
		
		
		Line polygonRoi = new Line(point.getX(), point.getY(),point.getX(), point.getY());


	      polygonRoi.setStrokeWidth(10);
	      polygonRoi.setStrokeColor(Color.red);
		     
	    // Roi.setColor(Color.red);
	      
	      Clump.overlaySplitPoints.add(polygonRoi);
		/*ip.drawDot((int) point.getX(), (int) point.getY());
		ip.setLineWidth(1);
		if(Clump_Splitting.BACKGROUNDCOLOR==0)
		{
		ip.setColor(Color.black);
		}
		else{
			ip.setColor(Color.white);
		}*/
		}
		else{
			Clump.overlaySplitPoints.clear();
		}
		
	}
	}
	}

	@Override
	public String toString(){
		return "X: " + point.getX() + " Y: " + point.getY() +" MaxX: " + this.getStartPoint().getX() +" MaxY: " + this.getStartPoint().getY();
		
	}
	
	}
