package de.biomedical_imaging.ij.clumpsplitting;



import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
/**
 * 
 * @author Louise
 *
 */
public class ConcavityRegionAdministration {
private Polygon boundaryArc;
private Polygon convexHull;

final double CONCAVITY_DEPTH_THRESHOLD = 10;

public ConcavityRegionAdministration(Polygon boundaryArc,Polygon convexHull)
{
	this.boundaryArc=boundaryArc;
	this.convexHull=convexHull;
}
/**
 * ermittelt alle ConcavityRegions eines Clumps
 * @return Liste mit allen Regionen
 */
public ArrayList<ConcavityRegion> computeConcavityRegions()
{
	ArrayList<ConcavityRegion> concavityRegionList=new ArrayList<ConcavityRegion>();
	int startX;
	int startY;
	int endX;
	int endY;
	for(int i=1;i<convexHull.npoints;i++)
	{
		startX=convexHull.xpoints[i-1];
		startY=convexHull.ypoints[i-1];
		endX=convexHull.xpoints[i];
		endY=convexHull.ypoints[i];

		ArrayList<Point2D> pointList=getAllEmbeddedPointsFromBoundaryArc(startX,startY,endX,endY);
		ArrayList<Double> doubleList= computeDistance(pointList,startX,startY,endX,endY);
		double[] maxData= getMaxDist(doubleList);
		if(maxData[0]>CONCAVITY_DEPTH_THRESHOLD)
		{
			ConcavityRegion concavityRegion=new ConcavityRegion(startX,startY,endX,endY,pointList,doubleList,maxData[0],(int)maxData[1]);
			concavityRegionList.add(concavityRegion);
		}
		
	}
	return concavityRegionList;
}
/**
 * ermittelt alle Punkte, die zwischen 2 werten eingeschlossen werden
 * d.h. es werden alle Punkte ermittelt, die auf dem BoundaryArc liegen und sich innerhalb einer ConcavityRegion befinden
 * @param startX Anfangswert X
 * @param startY Anfangswert Y
 * @param endX Endwert X
 * @param endY Endwert Y
 * @return Liste mit allen Eingeschlossenen Punkten im Raster
 */
private ArrayList<Point2D> getAllEmbeddedPointsFromBoundaryArc(int startX,int startY,int endX,int endY){
	int i=0;
	boolean ended=false;
	boolean started=false;
	ArrayList<Point2D> pointList=new ArrayList<Point2D>();
	while(i<boundaryArc.npoints&&!ended)
	{
		if(boundaryArc.xpoints[i]==startX&& boundaryArc.ypoints[i]==startY&&!started)
		{
			pointList.add(new Point2D.Double(boundaryArc.xpoints[i],boundaryArc.ypoints[i]));
			started=true;
		}
		else
		{
			
			if(started&&(boundaryArc.xpoints[i]!=endX|| boundaryArc.ypoints[i]!=endY)&&!ended)
			{
				pointList.add(new Point2D.Double(boundaryArc.xpoints[i],boundaryArc.ypoints[i]));
				
			}
			else
			{
				if(started&&boundaryArc.xpoints[i]==endX&& boundaryArc.ypoints[i]==endY)
				{
					pointList.add(new Point2D.Double(boundaryArc.xpoints[i],boundaryArc.ypoints[i]));
					ended=true;
				}
				
			}
		}
		i++;
	}
	return pointList;
	
}

public void setConvexHull(Polygon convexHull)
{
	this.convexHull=convexHull;
}

public void setBoundaryArc(Polygon boundaryArc)
{
	this.boundaryArc=boundaryArc;
}
/**
 * berechnet die Distanz zwischen den Punkten der BoundaryPoint List und der COnvexHull
 * @param boundaryPointList Liste mit allen zu berechnenden Punkten
 * @param startX Anfangswert X der Geraden
 * @param startY AnfangswertY der Geraden
 * @param endX EndwertX der Geraden
 * @param endY EndwertY der Geraden
 * @return Liste, die alle Abstände enthält
 */
private ArrayList<Double> computeDistance(ArrayList<Point2D> boundaryPointList,int startX,int startY, int endX,int endY)
{
	ArrayList<Double> doubleList=new ArrayList<Double>();
	Line2D.Double line=new Line2D.Double(startX, startY, endX, endY);
	Double dist;
	for(Point2D point:boundaryPointList)
	{
		dist=line.ptSegDistSq(point);
		//dist=Math.abs(line.ptLineDist(point));
		doubleList.add(dist);
	}
	return doubleList;
}
/**
 * durchläuft die mitgegebene Liste und berechnet den größten Wert
 * @param distList Liste mit Abständen zwischen Punkt und Gerade
 * @return Array der Länge 2 mit an Stelle 0 dem max. Abstand und an Stelle 1 den Index, bezogen auf die Liste an dem dieser Abstand auftritt
 */
private double[] getMaxDist(ArrayList<Double> distList)
{
	int indexMax=0;
	double max=0;
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
	double[] tmp={max, indexMax};
	return tmp;
}

}
