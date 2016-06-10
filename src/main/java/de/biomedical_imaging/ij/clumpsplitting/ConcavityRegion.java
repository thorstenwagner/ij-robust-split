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
	 * 
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
	 * max is the largest ConcavityDepth of the ConcavityRegion
	 */
	private double max;
	/**
	 * indexMax is the index of the Point with the largest concavityDepth of the
	 * ConcavityRegion based on the boundaryPointList
	 */
	private int indexMax;
	/**
	 * distList is the List of the concavityDepht of each Point on the boundary
	 * to the convexHull
	 */
	private ArrayList<Double> distList;
	/**
	 * represents the point in the middle of the convexHull
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
		Point2D p = boundaryPointList.get(indexMax);
		
		/*float[] i=new float[1];
		float[] j=new float[1];
		i[0]=(int)p.getX();
		j[0]=(int)p.getY();*/
		Line polygonRoi = new Line(p.getX(), p.getY(),p.getX(), p.getY());


	      polygonRoi.setStrokeWidth(10);;
	      polygonRoi.setStrokeColor(Color.red);
		     
	    // Roi.setColor(Color.red);
	      
	      Clump.overlaySplitPoints.add(polygonRoi);
		//ip.setColor(Color.gray);
		//ip.setLineWidth(10);
		//ip.drawDot((int) p.getX(), (int) p.getY());
		//ip.setLineWidth(1);
		//if(Clump_Splitting.BACKGROUNDCOLOR==0)
		//{
		//ip.setColor(Color.black);
		//}
		//else{
		//	ip.setColor(Color.white);
		//}
	}
	

	/**
	 * marks the Pixel in the middle of the ConvexHull
	 * 
	 * @param ip
	 *            ImageProcessor to mark the Pixel
	 */
	public void markMidPointOfConvexHull()
	{
	//	ip.setColor(Color.gray);
		Line polygonRoi = new Line((int) this.getMidPointOfConvexHull().getX(), (int) this.getMidPointOfConvexHull().getY(),(int) this.getMidPointOfConvexHull().getX(), (int) this.getMidPointOfConvexHull().getY());


	      polygonRoi.setStrokeWidth(4);
	      polygonRoi.setStrokeColor(Color.green);
		     
	      
	      Clump.overlayConvexHull.add(polygonRoi);
		
	}
	public double getOrientation()
	{
		Point2D midPointI = this.getMidPointOfConvexHull();
		Point2D maxPointI = this.getMaxDistCoord();
		double xPointDistOne = midPointI.getX() - maxPointI.getX();
		double yPointDistOne = midPointI.getY() - maxPointI.getY();
		
		Vector2d vi = new Vector2d(xPointDistOne, yPointDistOne);
		Vector2d vj= new Vector2d(10,0);
		vi.normalize();
		vj.normalize();
		double angle = Math.PI - Math.acos(vi.dot(vj));

		if(yPointDistOne<=0)
		{
			if(0<angle&&angle<=((Math.PI)/2))
			{
				angle=angle+1.5*Math.PI;
			}
			else{
				angle=angle+((Math.PI)/2);
			}
		}
	/*	else{	if(0<angle&&angle<=((Math.PI)/2))
		{
			angle=angle+((Math.PI)/2);
		}
		else{
			angle=angle-((Math.PI)/2);
		}
		}*/
	return angle;
	}
	/**
	 * marks the Distance between the MidPointOfConvexHull and the maxDistCoord
	 * @param ip
	 * 				ImageProcessor to mark the Line
	 */
	public void markConcavityDepth()
	{
		double x1=this.getStartX();
		double x2=this.getEndX();
		double y1=this.getStartY();
		double y2=this.getEndY();
		double yEnd;
		double xEnd;
		if(x2-x1!=0)
		{
		double m= ((y2-y1)/(x2-x1));
		
		if(m!=0)
		{
		
			double m2=(-1/m);
		
		
			double b=(-m2)*(int)this.getMaxDistCoord().getX()+(int)this.getMaxDistCoord().getY();
		//
			if(m>=0)
			{
				if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>=0)
				{
		
					xEnd=this.getMaxDistCoord().getX()+Math.sqrt(((this.getMaxDist())*this.getMaxDist())/(m2*m2+1));
				}
				else{
			
					xEnd=this.getMaxDistCoord().getX()-Math.sqrt(((this.getMaxDist())*this.getMaxDist())/(m2*m2+1));
			
				}
			}
			else{
				if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>=0)
				{
				
					xEnd=this.getMaxDistCoord().getX()-Math.sqrt(((this.getMaxDist())*this.getMaxDist())/(m2*m2+1));
				}
				else{
				
					xEnd=this.getMaxDistCoord().getX()+Math.sqrt(((this.getMaxDist())*this.getMaxDist())/(m2*m2+1));
				
				}
			}
			yEnd=m2*xEnd+b;
		}
		else{
			if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>=0)
			{
			yEnd=this.getMaxDistCoord().getY()-this.getMaxDist();
			xEnd=this.getMaxDistCoord().getX();
			}
			else{
				yEnd=this.getMaxDistCoord().getY()+this.getMaxDist();
				xEnd=this.getMaxDistCoord().getX();
				
			}
		
		}
		
		}
		else{
			if((this.getMaxDistCoord().getY()-this.getMidPointOfConvexHull().getY())>=0)
			{
			xEnd=this.getMaxDistCoord().getX()-this.getMaxDist();
			yEnd=this.getMaxDistCoord().getY();
			}
			else{
				xEnd=this.getMaxDistCoord().getX()+this.getMaxDist();
				yEnd=this.getMaxDistCoord().getY();
				
			}
		}
		
	/*	Line polygonRoi = new Line((int)this.getMaxDistCoord().getX(), (int)this.getMaxDistCoord().getY() ,(int)xEnd,(int)yEnd);


	      polygonRoi.setStrokeWidth(1);
	      polygonRoi.setStrokeColor(Color.green);
		     
	      
	      Clump.overlayConcavityDepth.add(polygonRoi);*/
	//	ip.drawLine((int)this.getMaxDistCoord().getX(), (int)this.getMaxDistCoord().getY() ,(int)xEnd,(int)yEnd);
	//	ip.drawLine((int)this.getMidPointOfConvexHull().getX(),(int)this.getMidPointOfConvexHull().getY() , (int)this.getMaxDistCoord().getX(), (int) this.getMaxDistCoord().getY());
		//IJ.log(this.getMaxDist()+"");
	//	ip.drawLine((int)this.getStartX(),(int)this.getStartY() , (int)this.getStartX()+(int)this.getMaxDist(), (int)this.getStartY());
		
		/*if(Clump_Splitting.BACKGROUNDCOLOR==0)
		{
		ip.setColor(Color.black);
		}
		else{
			ip.setColor(Color.white);
		}*/
	}
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
	 * @param max
	 *            largest concavityDepth of the ConcavityRegion
	 * @param maxIndex
	 *            index of the point with the largest concavityDepth based by
	 *            the boundaryPointList
	 */
	public ConcavityRegion(int startX, int startY, int endX, int endY, ArrayList<Point2D> boundaryPointList,
			ArrayList<Double> distList, double max, int maxIndex)
	{
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		
		this.boundaryPointList = boundaryPointList;
		this.distList = distList;
		this.max = max;
		this.indexMax = maxIndex;
		this.midPointOfConvexHull = this.computeMidPointOfConvexHull();

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

	public Point2D getMaxDistCoord()
	{
		return boundaryPointList.get(indexMax);
	}
	public ArrayList<Point2D> getBoundaryPointList()
	{
		return boundaryPointList;
	}

	
	public String getInformation()
	{

		double cangle= (360/(2*Math.PI))*this.getOrientation();
		
		cangle=Math.round(cangle*100);
		cangle=cangle/100;
		double concavityDepth= Math.round(this.getMaxDist()*100);
		concavityDepth=concavityDepth/100;
		Point2D.Double a = new Point2D.Double(this.getStartX(), this.getStartY());
		Point2D.Double b = new Point2D.Double(this.getEndX(), this.getEndY());
		Point2D c = this.getMaxDistCoord();

		double clength = Math
				.sqrt((b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() - a.getY()));
		double alength = Math
				.sqrt((b.getX() - c.getX()) * (b.getX() - c.getX()) + (b.getY() - c.getY()) * (b.getY() - c.getY()));
		double blength = Math
				.sqrt((c.getX() - a.getX()) * (c.getX() - a.getX()) + (c.getY() - a.getY()) * (c.getY() - a.getY()));

		double gamma = Math
				.acos(((clength * clength) - (alength * alength) - (blength * blength)) / (-2 * Math.abs(alength) * Math.abs(blength)));
		gamma=(360/(2*Math.PI))*gamma;
		gamma=Math.round(gamma*100);
		gamma=gamma/100;
		String text= "Ausrichtung: "+cangle+"\nKonkavitätstiefe: "+ concavityDepth+"\nKonkavitätswinkel: "+gamma;
		return text;
		
	}

	/**
	 * computes the point of the ConvexHull, which is in middle of the convexHull
	 * @return Point in the middle of the ConvexHull
	 */
	private Point2D computeMidPointOfConvexHull()
	{
		double xDist = ((double) endX - (double) startX) / 2;
		double yDist = ((double) endY - (double) startY) / 2;
		double xCoord = startX + xDist;
		double yCoord = startY + yDist;
		Point2D.Double midPoint = new Point2D.Double(xCoord, yCoord);
		return midPoint;
	}

	public Point2D getMidPointOfConvexHull()
	{
		return midPointOfConvexHull;
	}

	public double getMaxDist()
	{
		return max;
	}

	@Override
	public int compareTo(ConcavityRegion o)
	{
		if(this.getMaxDist()<o.getMaxDist())
		{
			return -1;
		}
		else{
			if(this.getMaxDist()>o.getMaxDist())
			{
				return 1;
			}
			else{
				return 0;
			}
		}
	}
	public Rectangle getRectangle()
	{
		Polygon p= new Polygon();
		for(Point2D point: boundaryPointList)
		{
			p.addPoint((int)point.getX(), (int)point.getY());
		}
		return p.getBounds();
	}
	@Override
	public boolean equals(Object o)
	{
		if(o.getClass().equals(this.getClass()))
		{
			ConcavityRegion other=(ConcavityRegion)(o);
			if(other.getEndX()==this.getEndX()&&other.getEndY()==this.getEndY()&&other.getStartX()==this.getStartX()&&other.getStartY()==this.getStartY())
			{
				return true;
			}
			else
			{
				if(other.getEndX()==this.getStartX()&&other.getEndY()==this.getStartY()&&other.getStartX()==this.getEndX()&&other.getStartY()==this.getEndY())
				{
					return true;
				}
				else{
					return false;
				}
			}
		}
		else{
			return false;
		}
	}
	@Override
	public String toString()
	{
		String st= "StartX:"+this.startX+" StartY:"+this.startY+ " EndX:"+ this.endX+ " EndY:" + this.endY+ " Max:"+this.max +" MaxCoord: "+ this.getMaxDistCoord().getX()+" "+this.getMaxDistCoord().getY() ;
		return st;
	}
}
