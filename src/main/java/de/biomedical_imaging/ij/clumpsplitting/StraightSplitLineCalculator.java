package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.vecmath.Vector2d;
import ij.IJ;


public class StraightSplitLineCalculator implements AbstractSplitLineCalculator{
private final double SALIENCY_THRESHOLD=0.12;
private final double CONCAVITYCONCAVITY_THRESHOLD=1.8325957;
private final double CONCAVITYLINE_THRESHOLD=1.2217305;
private final double CONCAVITYANGLE_THRESHOLD=1.5707963;
private final double CONCAVITYRATIO_THRESHOLD=6;


public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList, Clump c)
{
	ArrayList<AbstractSplitLine> possibleSplitLines=new ArrayList<AbstractSplitLine>();
	for(int i=0;i<concavityRegionList.size()-1;i++)
	{
		ConcavityRegion cOne=concavityRegionList.get(i);
		for(int j=i+1;j<concavityRegionList.size();j++)
		{
			
			ConcavityRegion cTwo=concavityRegionList.get(j);
		
			if(!cOne.equals(cTwo))
			{
				double saliency=this.computeSaliency(cOne,cTwo);
				double concavityConcavityAlignment= this.computeConcavityConcavityAlignment(cOne, cTwo);
				double concavityLineAlignment=this.computeConcavityLineAlignment(cOne, cTwo);
				//IJ.showMessage("Saliency:"+ saliency+"ConcavityConcavityAlignment:"+concavityConcavityAlignment+"ConcavityLineAlignment"+ concavityLineAlignment);
				if(saliency>SALIENCY_THRESHOLD)
				{
					if(concavityConcavityAlignment<CONCAVITYCONCAVITY_THRESHOLD)
					{
						if(concavityLineAlignment<CONCAVITYLINE_THRESHOLD)
						{
							StraightSplitLineBetweenTwoConcavityRegions ssl=new StraightSplitLineBetweenTwoConcavityRegions(cOne,cTwo,saliency,concavityConcavityAlignment,concavityLineAlignment);
							possibleSplitLines.add(ssl);
						}
					}
				}
				
			}
		}
	}
	if(possibleSplitLines.size()==0)
	{
		if(concavityRegionList.size()>0)
		{
		
			ConcavityRegion concavityRegion=c.getRegionOfMaxConcavityDepth();
			double concavityAngle=this.computeConcavityAngle(concavityRegion);
			double concavityRatio=this.computeConcavityRatio(c, concavityRegion);
			IJ.log("concavityAngle:"+concavityAngle+ "concavityRatio"+concavityRatio);
			if(concavityAngle<CONCAVITYANGLE_THRESHOLD)
			{
				if(concavityRatio>CONCAVITYRATIO_THRESHOLD)
				{
					StraightSplitLineBetweenConcavityRegionAndPoint ssl=this.computeSplitLineBetweenConcavityPointAndPoint(concavityRegion,c,concavityAngle,concavityRatio);
					possibleSplitLines.add(ssl);
				}
			}
		}
	}
	return possibleSplitLines;
}
private double computeSaliency(ConcavityRegion cOne, ConcavityRegion cTwo)
{
	double minCDi=0;
	if(cOne.getMaxDist()>cTwo.getMaxDist())
	{
		minCDi=cTwo.getMaxDist();
	}
	else{
		minCDi=cOne.getMaxDist();
	}
	double distance;
	double saliency=0;
	Point2D pOne=cOne.getMaxDistCoord();
	Point2D pTwo=cTwo.getMaxDistCoord();
	double distX;
	double distY;
	distX=pOne.getX()-pTwo.getX();
	distY=pOne.getY()+pTwo.getY();
	distance=Math.sqrt((distX*distX)+(distY*distY));
	if((minCDi+distance)>0)
	{
	saliency=minCDi/(minCDi+distance);
	}
	return saliency;
	
}
private Vector2d computeVi(ConcavityRegion ci)
{
	Point2D midPointI=ci.getMidPointOfConvexHull();
	Point2D maxPointI=ci.getMaxDistCoord();
	double xPointDistOne= maxPointI.getX()-midPointI.getX();
	double yPointDistOne= maxPointI.getY()-midPointI.getY();
	Vector2d vi=new Vector2d(xPointDistOne,yPointDistOne);
	return vi;
}
private double computeConcavityConcavityAlignment(ConcavityRegion cOne, ConcavityRegion cTwo)
{
	double concavityConcavityAlignment=0;
	Vector2d vOne=this.computeVi(cOne);
	Vector2d vTwo=this.computeVi(cTwo);
	vOne.normalize();
	vTwo.normalize();
	
	concavityConcavityAlignment=Math.PI-Math.acos(vOne.dot(vTwo));
	return concavityConcavityAlignment;
	
}
private double computeConcavityLineAlignment(ConcavityRegion cOne, ConcavityRegion cTwo)
{
	double concavityLineAlignment=0;
	Point2D maxPointOne=cOne.getMaxDistCoord();
	Point2D maxPointTwo=cTwo.getMaxDistCoord();
	double xDist=maxPointTwo.getX()-maxPointOne.getX();
	double yDist=maxPointTwo.getY()-maxPointOne.getY();
	
	Vector2d uij=new Vector2d(xDist,yDist);
	Vector2d vOne=this.computeVi(cOne);
	Vector2d vTwo=this.computeVi(cTwo);
	uij.normalize();
	vOne.normalize();
	vTwo.normalize();
	Vector2d muij=new Vector2d(-uij.getX(),-uij.getY());
	//IJ.showMessage("vekt"+uij.getX()+"/"+ uij.getY()+"vekt-"+muij.getX()+"/"+ muij.getY());
	double phiOne= Math.acos(vOne.dot(uij));
	double phiTwo=Math.acos(vTwo.dot(muij));
	if(phiOne>phiTwo)
	{
		concavityLineAlignment=phiOne;
	}
	else
	{
		concavityLineAlignment=phiTwo;
	}
	return concavityLineAlignment;
}
	private double computeConcavityAngle(ConcavityRegion cOne)
	{
		Point2D.Double a=new Point2D.Double(cOne.getStartX(),cOne.getStartY());
		Point2D.Double b=new Point2D.Double(cOne.getEndX(), cOne.getEndY());
		Point2D c=cOne.getMaxDistCoord();
		
		double clength=Math.sqrt((b.getX()-a.getX())*(b.getX()-a.getX())+(b.getY()-a.getY())*(b.getY()-a.getY()));
		double alength=Math.sqrt((b.getX()-c.getX())*(b.getX()-c.getX())+(b.getY()-c.getY())*(b.getY()-c.getY()));
		double blength=Math.sqrt((c.getX()-a.getX())*(c.getX()-a.getX())+(c.getY()-a.getY())*(c.getY()-a.getY()));
		
		double gamma=Math.acos(((clength*clength)-(alength*alength)-(blength*blength))/(-2*alength*blength));
		return gamma;
	}
	private double computeConcavityRatio(Clump c, ConcavityRegion cOne)
	{
		double concavityRatio;
		IJ.log(cOne.getMaxDist()+"");
		
		concavityRatio= ((cOne.getMaxDist())/(c.getSecondMaxConcavityRegionDepth()));
		return concavityRatio;
	}
	private StraightSplitLineBetweenConcavityRegionAndPoint computeSplitLineBetweenConcavityPointAndPoint(ConcavityRegion cOne, Clump c,double concavityAngle, double concavityRatio)
	{
		Point2D midPoint=cOne.getMidPointOfConvexHull();
		Point2D concavityPoint=cOne.getMaxDistCoord();
		Line2D.Double line =new Line2D.Double(midPoint,concavityPoint);
		Polygon p=c.getBoundary();
		double minDist=line.ptLineDist(p.xpoints[0],p.ypoints[0]);
		int indexMinDist=0;
		double dist=0;
		for(int i=0;i<p.npoints;i++)
		{
			Point2D.Double point=new Point2D.Double(p.xpoints[i], p.ypoints[i]);
			if(point.getX()!=concavityPoint.getX()&&point.getY()!=concavityPoint.getY())
			{
				dist=line.ptLineDist(point);
				if(dist<minDist)
				{
					minDist=dist;
					indexMinDist=i;
				}
			}
		}
		StraightSplitLineBetweenConcavityRegionAndPoint s=new StraightSplitLineBetweenConcavityRegionAndPoint(cOne,concavityAngle,concavityRatio,new Point2D.Double(p.xpoints[indexMinDist],p.ypoints[indexMinDist]));
		return s;
	}
}
