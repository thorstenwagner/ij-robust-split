/*

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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import ij.gui.Line;

/**
 * administrates the concavityRegions
 * 
 * @author Louise
 *
 */
public class ConcavityRegionAdministration
{
	private Clump clump;
	public static ArrayList<Point2D> allConcavityRegionPoints=new ArrayList<Point2D>();


	/**
	 * 
	 * @param boundaryArc
	 *            bounds of the affiliated Clump
	 * @param convexHull
	 *            convexHull of the affiliated Clump
	 */
	public ConcavityRegionAdministration(Clump clump)
	{
		this.clump=clump;
		
	}

	/**
	 * computes the valid concavityRegions of a Clump. A concavityRegion is a
	 * valid ConcavityRegion if the largest ConcavityDepth is larger than the
	 * CONCAVITY_DEPTH_THRESHOLD
	 * 
	 * @return List of all valid concavityRegions
	 */
	public ArrayList<ConcavityRegion> computeConcavityRegions()
	{
		ArrayList<ConcavityRegion> concavityRegionList = new ArrayList<ConcavityRegion>();
		int startX;
		int startY;
		int endX;
		int endY;
		if(clump.getConvexHull()!=null)
		{
		for (int i = 1; i < clump.getConvexHull().npoints; i++)
		{
			startX = clump.getConvexHull().xpoints[i - 1];
			startY =clump.getConvexHull().ypoints[i - 1];
			endX = clump.getConvexHull().xpoints[i];
			endY = clump.getConvexHull().ypoints[i];

			
			ArrayList<Point2D> pointList = getAllEmbeddedPointsFromBoundaryArc(startX, startY, endX, endY);
			ConcavityRegionAdministration.allConcavityRegionPoints.addAll(pointList);
			if(pointList.size()>3)
			{
			ArrayList<Double> doubleList = computeDistance(pointList, startX, startY, endX, endY);
			double[] maxData = getMaxDist(doubleList);
			if (maxData[0] > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
			{
				ArrayList<Integer> maxDataList=new ArrayList<Integer>();
				for(int m=1;m<maxData.length;m++)
				{
					maxDataList.add((int)maxData[m]);
				}
				ConcavityRegion concavityRegion = new ConcavityRegion(startX, startY, endX, endY, pointList, doubleList,
						maxData[0], maxDataList);
				concavityRegionList.add(concavityRegion);
		//		System.out.println("außen "+concavityRegion);
			}

		}
		}
		//IJ.log("Anzahl ohne innere"+ concavityRegionList.size());
		}
		for(InnerContour inner:clump.getInnerContours())
		{
			//IJ.log("innere Kontur");
			Polygon innerConvexHull=inner.getConvexHull();
			//PolygonRoi roi=new PolygonRoi(innerConvexHull,Roi.POLYGON);
			//Clump.o.add(roi);
			//Polygon innerContour=inner.getContour();
			int innerStartX;
			int innerStartY;
			int innerEndX;
			int innerEndY;
			if(innerConvexHull!=null)
			{
				ArrayList<ConcavityRegion> tempInner=new ArrayList<ConcavityRegion>();
			//	IJ.log("convexHullPoints"+innerConvexHull.npoints);
			for (int i = 1; i < innerConvexHull.npoints; i++)
			{
							
				innerStartX = innerConvexHull.xpoints[i - 1];
				innerStartY =innerConvexHull.ypoints[i - 1];
				innerEndX = innerConvexHull.xpoints[i];
				innerEndY = innerConvexHull.ypoints[i];

				//IJ.log(innerStartX+ " "+innerStartY+ " "+innerEndX+ " "+innerEndY+ " ");
				ArrayList<Point2D> pointList = getAllEmbeddedPointsFromInnerContour(innerStartX, innerStartY, innerEndX, innerEndY,inner);
			//	IJ.log(innerStartX+ " "+innerStartY+ " "+innerEndX+ " "+innerEndY+ " "+pointList.size());
				
				if(pointList.size()>3)
				{
				ArrayList<Double> doubleList = computeDistance(pointList, innerStartX, innerStartY, innerEndX, innerEndY);
				double[] maxData = getMaxDist(doubleList);
			//	System.out.println("Threshold:"+Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD);
				//if (maxData[0] > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
				//{
					
				//	IJ.log("test");
				ArrayList<Integer> maxDataList=new ArrayList<Integer>();
				for(int m=1;m<maxData.length;m++)
				{
					maxDataList.add((int)maxData[m]);
				}
					ConcavityRegion concavityRegion = new ConcavityRegion(innerStartX, innerStartY, innerEndX, innerEndY, pointList, doubleList,
							maxData[0], maxDataList);

				//	System.out.println("innenTemp "+concavityRegion);
					
				//	Line polygonRoi = new Line(innerStartX, innerStartY,innerEndX, innerEndY);
				//	polygonRoi.setStrokeColor(Color.magenta);
					
				//	Clump.p.add(polygonRoi);
				//	polygonRoi = new Line(concavityRegion.getMaxDistCoord().getX(), concavityRegion.getMaxDistCoord().getY(),concavityRegion.getMaxDistCoord().getX(), concavityRegion.getMaxDistCoord().getY());
				//	polygonRoi.setStrokeColor(Color.pink);
				//	polygonRoi.setLineWidth(10);
				//	Clump.q.add(polygonRoi);
					
					tempInner.add(concavityRegion);
					
				//	concavityRegionList.add(concavityRegion);
				//}
				}
				//IJ.log("Anzahl der inneren Konkavitätsregionen"+tempInner.size()+"");
			}
				for(int j=0;j<tempInner.size();j++)
				{
					ConcavityRegion teil1;
					ConcavityRegion teil2;
					
					if(j>0)
					{
					teil1=tempInner.get(j);
					teil2=tempInner.get(j-1);
					}
					else{
						teil1=tempInner.get(j);
						teil2=tempInner.get(tempInner.size()-1);
					}
					ArrayList<Point2D> list= teil1.getMaxDistCoord();
					Point2D endPoint=list.get(list.size()/2);
					ArrayList<Point2D> list2= teil2.getMaxDistCoord();
					
					Point2D startPoint=list2.get(list2.size()/2);
				/*	Line polygonRoi = new Line(startPoint.getX(), startPoint.getY(),endPoint.getX(), endPoint.getY());
					polygonRoi.setStrokeColor(Color.green);
				*/	
					ArrayList<Point2D> embeddedPoints=this.getAllEmbeddedPointsFromInnerContour((int)startPoint.getX(),(int)startPoint.getY(),(int)endPoint.getX(),(int)endPoint.getY(), inner);
					if(embeddedPoints.size()>3)
					{
					ArrayList<Double> doubleListInner = computeDistance(embeddedPoints, (int)startPoint.getX(), (int)startPoint.getY(), (int)endPoint.getX(), (int)endPoint.getY());
					double[] maxDataInner = getMaxDist(doubleListInner);
					ArrayList<Integer> maxDataList= new ArrayList<Integer>();
					for(int i=1;i<maxDataInner.length;i++)
					{
						maxDataList.add((int)maxDataInner[i]);
					}
					if (maxDataInner[0] > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
					{
						
						ConcavityRegion crReal=new ConcavityRegion((int)startPoint.getX(),(int)startPoint.getY(),(int)endPoint.getX(),(int)endPoint.getY(),embeddedPoints,doubleListInner,maxDataInner[0],maxDataList);
								//	tempInner.add(crReal);
					//	IJ.log("innen");
						concavityRegionList.add(crReal);

						if(Clump_Splitting.SHOWCONVEXHULL)
						{
							Line linie = new Line(crReal.getStartX(),crReal.getStartY(),crReal.getEndX(),crReal.getEndY());


							
						      linie.setStrokeWidth(1);;
						   //  Roi.setColor(Color.cyan);
						      linie.setStrokeColor(Color.cyan);
						      Clump.overlayConvexHull.add(linie);
						}
					//	System.out.println("innen"+crReal);
					}
					}
				
				}
				
			}
			}
		
	//	IJ.log("Anzahl der Konkavitätsregionen"+ concavityRegionList.size());
		
		return concavityRegionList;
	}

	/**
	 * computes all Points, which are on the boundary and are embedded from the
	 * start and end Coordinates. Note: The convexHull is definded
	 * counterClockWise, the outerContours of a Blob clockwise don't use for
	 * inner Contours they are computed counterclockwise, in this case i has to
	 * increase from 0 to boundaryArc.npoints
	 * 
	 * @param startX
	 *            X-Coordinate of the startValue of the Possible ConcavityRegion
	 * @param startY
	 *            Y-Coordinate of the startValue of the Possible ConcavityRegion
	 * @param endX
	 *            X-Coordinate of the endValue of the Possible ConcavityRegion
	 * @param endY
	 *            Y-Coordinate of the endValue of the Possible ConcavityRegion
	 * @return List with all embedded Points on the grid
	 */
	private ArrayList<Point2D> getAllEmbeddedPointsFromBoundaryArc(int startX, int startY, int endX, int endY)
	{
		int i = clump.getBoundary().npoints - 1;
		boolean ended = false;
		boolean started = false;
		ArrayList<Point2D> pointList = new ArrayList<Point2D>();
		while (i >= 0 && !ended)
		{
			if (clump.getBoundary().xpoints[i] == startX && clump.getBoundary().ypoints[i] == startY && !started)
			{
				pointList.add(new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));
				started = true;
			} else
			{

				if (started && (clump.getBoundary().xpoints[i] != endX || clump.getBoundary().ypoints[i] != endY) && !ended)
				{
					pointList.add(new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));

				} else
				{
					if (started && clump.getBoundary().xpoints[i] == endX && clump.getBoundary().ypoints[i] == endY)
					{
						pointList.add(new Point2D.Double(clump.getBoundary().xpoints[i], clump.getBoundary().ypoints[i]));
						ended = true;
					}

				}
			}
			i--;
		}
		return pointList;

	}

	private ArrayList<Point2D> getAllEmbeddedPointsFromInnerContour(int startX, int startY, int endX, int endY,InnerContour innerContour)
	{
		int i = 0;
		boolean ended = false;
		boolean started = false;
		ArrayList<Point2D> pointList = new ArrayList<Point2D>();
		while (i < innerContour.getContour().npoints - 1 && !ended)
		{
			if (innerContour.getContour().xpoints[i] == startX && innerContour.getContour().ypoints[i] == startY && !started)
			{
				pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i], innerContour.getContour().ypoints[i]));
				started = true;
			} else
			{

				if (started && (innerContour.getContour().xpoints[i] != endX || innerContour.getContour().ypoints[i] != endY) && !ended)
				{
					pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i], innerContour.getContour().ypoints[i]));

				} else
				{
					if (started && innerContour.getContour().xpoints[i] == endX && innerContour.getContour().ypoints[i] == endY)
					{
						pointList.add(new Point2D.Double(innerContour.getContour().xpoints[i], innerContour.getContour().ypoints[i]));
						ended = true;
					}

				}
			}
			i++;
		}
		return pointList;

	}

	
	/**
	 * computes the concavityDepth of all points on the boundaryList
	 * 
	 * @param boundaryPointList
	 *            List with all points embedded by the boundary of the Clump and
	 *            contains to the possible concavityRegion
	 * @param startX
	 *            x-Coordinate of the start-Point of the possible
	 *            ConcavityRegion
	 * @param startY
	 *            y-Coordinate of the start-Point of the possible
	 *            ConcavityRegion
	 * @param endX
	 *            x-Coordinate of the end-Point of the possible ConcavityRegion
	 * @param endY
	 *            y-Coordinate of the end-Point of the possible ConcavityRegion
	 * @return List with all concavityDepth of the possible ConcavityRegion
	 */
	private ArrayList<Double> computeDistance(ArrayList<Point2D> boundaryPointList, int startX, int startY, int endX,
			int endY)
	{
		ArrayList<Double> doubleList = new ArrayList<Double>();
		Line2D.Double line = new Line2D.Double(startX, startY, endX, endY);
		Double dist;
		for (Point2D point : boundaryPointList)
		{
			dist = Math.sqrt(line.ptSegDistSq(point));
			// dist=Math.abs(line.ptLineDist(point));
			doubleList.add(dist);
		}
		return doubleList;
	}

	

	/**
	 * filters the List for the largest Value
	 * 
	 * @param distList
	 *            List with all concavityDepths of the possible ConcavityRegion
	 * @return array with length=2 of type double array[0]= largest
	 *         concavityDepth of the possible ConcavityRegion, array[1]= index
	 *         of the Point with the largest concavityDepth based on the input
	 *         List
	 */
	private double[] getMaxDist(ArrayList<Double> distList)
	{
		//int indexMax = 0;
		double max = 0;
		int index = 0;
		ArrayList<Integer> moreThanOneMax= new ArrayList<Integer>();
		for (int i=0;i<distList.size();i++)
		{
			double d = distList.get(i);
			if(d>=max)
			{
				if (d > max)
				{
					moreThanOneMax.clear();
					moreThanOneMax.add(index);
					max = d;
					//indexMax = index;
				}
				else{
					if(d==max)
					{
						moreThanOneMax.add(index);
						max=d;
					}
				}
			}
			index++;
		}
		double[] tmp= new double[moreThanOneMax.size()+1];
		tmp[0]=max;
		for(int i=0;i<moreThanOneMax.size();i++)
		{
			tmp[i+1]=moreThanOneMax.get(i);
		}
		
		return tmp;
	}

}
