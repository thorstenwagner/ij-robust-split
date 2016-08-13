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

package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.vecmath.Vector2d;

import ij.gui.Line;

/**
 * a ConcavityRegion is a Region of a Clump with high concavity.
 * 
 * @author Louise
 *
 */
public class ConcavityRegion implements Comparable<ConcavityRegion>
{

	/**
	 * startX is the x-Coordinate of the StartingPoint of the ConcavityRegion
	 * the StartingPoint is detected by a point of the ConvexHull
	 */

	private int startX;
	/**
	 * startY is the y-Coordinate of the StartingPoint of the ConcavityRegion
	 * the StartingPoint is detected by a point of the ConvexHull
	 */
	private int startY;
	/**
	 * endX is the x-Coordinate of the endPoint of the ConcavityRegion the
	 * endPoint is detected by a point of the ConvexHull
	 */
	private int endX;
	/**
	 * endX is the y-Coordinate of the endPoint of the ConcavityRegion the
	 * endPoint is detected by a point of the ConvexHull
	 */
	private int endY;
	/**
	 * List of all concavityPixels detected in a ConcavityRegion. The result
	 * depends on the method they are detected. They are explained in enum
	 * ConcavityPixelDetectorType
	 */
	private ArrayList<ConcavityPixel> concavityPixelList;
	/**
	 * distList is the List of the concavityDepht of each Point on the boundary
	 * to the convexHull
	 */
	private ArrayList<Double> distList;
	/**
	 * represents the point in the middle of the convexHull. On this points many
	 * angle features depend to evaluate the SplitLine
	 */
	private Point2D midPointOfConvexHull;
	/**
	 * boundaryPointList is a List with all Points based on the grid, which is
	 * on the boundary of the Concavity Region
	 */
	private ArrayList<Point2D> boundaryPointList;

	/**
	 * marks the Pixel with the largest concavityDepth of the boundary
	 * 
	 * @param ip
	 *            ImageProcessor to mark the Pixel
	 */
	public void markMax()
	{
		for (int i = 0; i < concavityPixelList.size(); i++)
		{
			Point2D p = concavityPixelList.get(i).getPosition();

			Line polygonRoi = new Line(p.getX(), p.getY(), p.getX(), p.getY());

			polygonRoi.setStrokeWidth(3);

			polygonRoi.setStrokeColor(Color.red);

			// Roi.setColor(Color.red);

			Clump_Splitting.overlaySplitPoints.add(polygonRoi);
		}
	}

	/**
	 * marks the Pixel in the middle of the ConvexHull
	 * 
	 * @param ip
	 *            ImageProcessor to mark the Pixel
	 */
	/*
	 * public void markMidPointOfConcavityRegion() { Line polygonRoi = new
	 * Line((int) this.getMidPointOfConcavityRegion().getX(), (int)
	 * this.getMidPointOfConcavityRegion().getY(), (int)
	 * this.getMidPointOfConcavityRegion().getX(), (int)
	 * this.getMidPointOfConcavityRegion().getY());
	 * 
	 * polygonRoi.setStrokeWidth(3); polygonRoi.setStrokeColor(Color.green);
	 * 
	 * Clump_Splitting.overlayConvexHull.add(polygonRoi);
	 * 
	 * }
	 */

	/**
	 * Method to compute Orientation of a Convex HUll, the orientation is
	 * defined as the angle between horizontal and the Line Between midPoint Of
	 * ConcavityRegion and ConcavityPixel
	 * 
	 * @param maxPointI
	 *            ConcavityPixel for which Orientation should be computed
	 * @return Orientation as radians
	 */
	public double getOrientation(Point2D maxPointI)
	{
		Point2D midPointI = this.getMidPointOfConcavityRegion();
		double xPointDistOne = midPointI.getX() - maxPointI.getX();
		double yPointDistOne = midPointI.getY() - maxPointI.getY();

		Line l = new Line(midPointI.getX(), midPointI.getY(), maxPointI.getX(), maxPointI.getY());
		Line m = new Line(midPointI.getX(), midPointI.getY(), midPointI.getX() + 50, midPointI.getY());
		l.setStrokeColor(Color.red);
		m.setStrokeColor(Color.red);
		Clump_Splitting.overlayForOrientation.add(l);
		Clump_Splitting.overlayForOrientation.add(m);
		Vector2d vi = new Vector2d(xPointDistOne, yPointDistOne);
		Vector2d vj = new Vector2d(10, 0);
		vi.normalize();
		vj.normalize();
		double angle = Math.PI - Math.acos(vi.dot(vj));

		if (yPointDistOne <= 0)
		{
			if (0 < angle && angle <= ((Math.PI) / 2))
			{
				double dist = 0.5 * Math.PI - angle;
				angle = dist + 1.5 * Math.PI;

			} else
			{
				double dist = 1.5 * Math.PI - angle;

				angle = dist + 0.5 * Math.PI;

			}
		}

		return angle;
	}
	/*
	 * /** marks the Distance between the MidPointOfConvexHull and the
	 * maxDistCoord
	 * 
	 * @param ip ImageProcessor to mark the Line
	 */
	/*
	 * public void markConcavityDepth() { double x1=this.getStartX(); double
	 * x2=this.getEndX(); double y1=this.getStartY(); double y2=this.getEndY();
	 * double yEnd; double xEnd; if(x2-x1!=0) { double m= ((y2-y1)/(x2-x1));
	 * 
	 * if(m!=0) {
	 * 
	 * double m2=(-1/m);
	 * 
	 * 
	 * double
	 * b=(-m2)*(int)this.getMaxDistCoord().getX()+(int)this.getMaxDistCoord().
	 * getY(); // if(m>=0) {
	 * if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>
	 * =0) {
	 * 
	 * xEnd=this.getMaxDistCoord().getX()+Math.sqrt(((this.getMaxDist())*this.
	 * getMaxDist())/(m2*m2+1)); } else{
	 * 
	 * xEnd=this.getMaxDistCoord().getX()-Math.sqrt(((this.getMaxDist())*this.
	 * getMaxDist())/(m2*m2+1));
	 * 
	 * } } else{
	 * if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>
	 * =0) {
	 * 
	 * xEnd=this.getMaxDistCoord().getX()-Math.sqrt(((this.getMaxDist())*this.
	 * getMaxDist())/(m2*m2+1)); } else{
	 * 
	 * xEnd=this.getMaxDistCoord().getX()+Math.sqrt(((this.getMaxDist())*this.
	 * getMaxDist())/(m2*m2+1));
	 * 
	 * } } yEnd=m2*xEnd+b; } else{
	 * if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>
	 * =0) { yEnd=this.getMaxDistCoord().getY()-this.getMaxDist();
	 * xEnd=this.getMaxDistCoord().getX(); } else{
	 * yEnd=this.getMaxDistCoord().getY()+this.getMaxDist();
	 * xEnd=this.getMaxDistCoord().getX();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } else{
	 * if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>
	 * =0) { xEnd=this.getMaxDistCoord().getX()-this.getMaxDist();
	 * yEnd=this.getMaxDistCoord().getY(); } else{
	 * xEnd=this.getMaxDistCoord().getX()+this.getMaxDist();
	 * yEnd=this.getMaxDistCoord().getY();
	 * 
	 * } }
	 * 
	 * /* Line polygonRoi = new Line((int)this.getMaxDistCoord().getX(),
	 * (int)this.getMaxDistCoord().getY() ,(int)xEnd,(int)yEnd);
	 * 
	 * 
	 * polygonRoi.setStrokeWidth(1); polygonRoi.setStrokeColor(Color.green);
	 * 
	 * 
	 * Clump.overlayConcavityDepth.add(polygonRoi);
	 */
	// ip.drawLine((int)this.getMaxDistCoord().getX(),
	// (int)this.getMaxDistCoord().getY() ,(int)xEnd,(int)yEnd);
	// ip.drawLine((int)this.getMidPointOfConvexHull().getX(),(int)this.getMidPointOfConvexHull().getY()
	// , (int)this.getMaxDistCoord().getX(), (int)
	// this.getMaxDistCoord().getY());
	// IJ.log(this.getMaxDist()+"");
	// ip.drawLine((int)this.getStartX(),(int)this.getStartY() ,
	// (int)this.getStartX()+(int)this.getMaxDist(), (int)this.getStartY());

	/*
	 * if(Clump_Splitting.BACKGROUNDCOLOR==0) { ip.setColor(Color.black); }
	 * else{ ip.setColor(Color.white); }
	 */
	// }
	/**
	 * produces a ConcavityRegion
	 * 
	 * @param startX
	 *            x-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param startY
	 *            y-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param endX
	 *            x-Coordinate of the endPoint of the ConcavityRegion
	 * @param endY
	 *            y-Coordinate of the endPoint of the ConcavityRegion
	 * @param boundaryPointList
	 *            List of all Pixels of the part of the boundary included by the
	 *            ConcavityRegion
	 * @param distList
	 *            List of the concavityDepth of all points of the part of the
	 *            boundary included by the ConcavityRegion
	 * @param concavityPixelList
	 *            List of All ConcavityPixels of the ConvexHull
	 */
	public ConcavityRegion(int startX, int startY, int endX, int endY, ArrayList<Point2D> boundaryPointList,
			ArrayList<Double> distList, ArrayList<ConcavityPixel> concavityPixelList)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		this.boundaryPointList = boundaryPointList;
		this.distList = distList;
		this.concavityPixelList = concavityPixelList;
		this.midPointOfConvexHull = this.computeMidPointOfConcavityRegion();

	}

	/**
	 * produces a ConcavityRegion without ConcavityPixels, because it is a
	 * bidirectionally relation, ConcavityPixel can be added by
	 * setConcavityPixelList or by add concavityPixel
	 * 
	 * @param startX
	 *            x-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param startY
	 *            y-Coordinate of the StartingPoint of the ConcavityRegion
	 * @param endX
	 *            x-Coordinate of the endPoint of the ConcavityRegion
	 * @param endY
	 *            y-Coordinate of the endPoint of the ConcavityRegion
	 * @param boundaryPointList
	 *            List of all Pixels of the part of the boundary included by the
	 *            ConcavityRegion
	 * @param distList
	 *            List of the concavityDepth of all points of the part of the
	 *            boundary included by the ConcavityRegion
	 */
	public ConcavityRegion(int startX, int startY, int endX, int endY, ArrayList<Point2D> boundaryPointList,
			ArrayList<Double> distList)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		this.concavityPixelList = new ArrayList<ConcavityPixel>();
		this.boundaryPointList = boundaryPointList;
		this.distList = distList;
		this.midPointOfConvexHull = this.computeMidPointOfConcavityRegion();

	}

	public void setConcavityPixelList(ArrayList<ConcavityPixel> cpl)
	{
		this.concavityPixelList = cpl;

	}

	public void addConcavityPixel(ConcavityPixel cp)
	{
		this.concavityPixelList.add(cp);

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
		return distList;
	}

	public ArrayList<Point2D> getMaxDistCoord()
	{
		ArrayList<Point2D> maxDistList = new ArrayList<Point2D>();
		for (int i = 0; i < concavityPixelList.size(); i++)
		{
			maxDistList.add((concavityPixelList.get(i).getPosition()));
		}
		return maxDistList;
	}

	public ArrayList<Point2D> getBoundaryPointList()
	{
		return boundaryPointList;
	}

	/**
	 * Collects all information about a Concavity Region and formats it to show
	 * it at an overlay to optimize Parmeters
	 * 
	 * @param maxDistPoint
	 *            ConcavityPixel with maximum DIst to ConcavityRegion
	 * @return formatted String with all information
	 */
	public String getInformation(ConcavityPixel maxDistPoint)
	{

		double cangle = (360 / (2 * Math.PI)) * this.getOrientation(maxDistPoint.getPosition());

		cangle = Math.round(cangle * 100);
		cangle = cangle / 100;
		double concavityDepth = Math.round(maxDistPoint.distance() * 100);
		concavityDepth = concavityDepth / 100;
		Point2D.Double a = new Point2D.Double(this.getStartX(), this.getStartY());
		Point2D.Double b = new Point2D.Double(this.getEndX(), this.getEndY());
		Point2D c = maxDistPoint.getPosition();

		double clength = Math
				.sqrt((b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() - a.getY()));
		double alength = Math
				.sqrt((b.getX() - c.getX()) * (b.getX() - c.getX()) + (b.getY() - c.getY()) * (b.getY() - c.getY()));
		double blength = Math
				.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY()) * (c.getY() - a.getY()));

		double gamma = Math.acos(((clength * clength) - (alength * alength) - (blength * blength))
				/ (-2 * Math.abs(alength) * Math.abs(blength)));
		gamma = (360 / (2 * Math.PI)) * gamma;
		gamma = Math.round(gamma * 100);
		gamma = gamma / 100;
		String text = "Ausrichtung: " + cangle + "\nKonkavitätstiefe: " + concavityDepth + "\nKonkavitätswinkel: "
				+ gamma;
		return text;

	}

	/**
	 * computes the point of the ConvexHull, which is in middle of the
	 * convexHull
	 * 
	 * @return Point in the middle of the ConvexHull
	 */
	private Point2D computeMidPointOfConcavityRegion()
	{
		double xDist = ((double) endX - (double) startX) / 2;
		double yDist = ((double) endY - (double) startY) / 2;
		double xCoord = startX + xDist;
		double yCoord = startY + yDist;
		Point2D.Double midPoint = new Point2D.Double(xCoord, yCoord);
		return midPoint;
	}

	public Point2D getMidPointOfConcavityRegion()
	{
		return midPointOfConvexHull;
	}

	public ArrayList<ConcavityPixel> getConcavityPixelList()
	{
		return concavityPixelList;
	}

	private double getMaxDistToCompare()
	{
		double max = 0;
		for (int i = 0; i < concavityPixelList.size(); i++)
		{
			if (concavityPixelList.get(i).distance() > max)
			{
				max = concavityPixelList.get(i).distance();
			}
		}
		return max;
	}

	@Override
	public int compareTo(ConcavityRegion o)
	{

		if (this.getMaxDistToCompare() < o.getMaxDistToCompare())
		{
			return -1;
		} else
		{
			if (this.getMaxDistToCompare() > o.getMaxDistToCompare())
			{
				return 1;
			} else
			{
				return 0;
			}
		}
	}

	/**
	 * Computes the Bounding box of a Concavity Region to register Mouse
	 * Listener in this area of the Image
	 * 
	 */
	public Rectangle getRectangle()
	{
		Polygon p = new Polygon();
		for (Point2D point : boundaryPointList)
		{
			p.addPoint((int) point.getX(), (int) point.getY());
		}
		return p.getBounds();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o.getClass().equals(this.getClass()))
		{
			ConcavityRegion other = (ConcavityRegion) (o);
			if (other.getEndX() == this.getEndX() && other.getEndY() == this.getEndY()
					&& other.getStartX() == this.getStartX() && other.getStartY() == this.getStartY())
			{
				return true;
			} else
			{
				if (other.getEndX() == this.getStartX() && other.getEndY() == this.getStartY()
						&& other.getStartX() == this.getEndX() && other.getStartY() == this.getEndY())
				{
					return true;
				} else
				{
					return false;
				}
			}
		} else
		{
			return false;
		}
	}

	public Vector2d getOrientationVector(Point2D cr){
		double x=cr.getX()-this.getMidPointOfConcavityRegion().getX();
		double y=cr.getY()-this.getMidPointOfConcavityRegion().getY();
		Vector2d v= new Vector2d(x,y);
		v.normalize();
		return v;
	}
	@Override
	public String toString()
	{
		String st = "StartX:" + this.startX + " StartY:" + this.startY + " EndX:" + this.endX + " EndY:" + this.endY;
		return st;
	}
}
