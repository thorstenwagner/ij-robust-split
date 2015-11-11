package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ConcavityRegionAdministration {
private Polygon boundaryArc;
private Polygon convexHull;


public ConcavityRegionAdministration(Polygon boundaryArc,Polygon convexHull)
{
	this.boundaryArc=boundaryArc;
	this.convexHull=convexHull;
}
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
		ConcavityRegion concavityRegion=new ConcavityRegion(startX,startY,endX,endY,pointList);
		concavityRegionList.add(concavityRegion);
	}
	return concavityRegionList;
}
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
			
			if(started&&(boundaryArc.xpoints[i]!=endX|| boundaryArc.ypoints[i]!=endY))
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
	}	return pointList;
	
}

public void setConvexHull(Polygon convexHull)
{
	this.convexHull=convexHull;
}

public void setBoundaryArc(Polygon boundaryArc)
{
	this.boundaryArc=boundaryArc;
}
}
