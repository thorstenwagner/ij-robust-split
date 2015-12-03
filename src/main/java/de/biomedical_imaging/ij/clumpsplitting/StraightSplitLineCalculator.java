package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.vecmath.Vector2d;

import ij.IJ;
import ij.process.ImageProcessor;


public class StraightSplitLineCalculator implements AbstractSplitLineCalculator{
private final double SALIENCY_THRESHOLD=0.12;
private final double CONCAVITYCONCAVITY_THRESHOLD=1.8325957;
private final double CONCAVITYLINE_THRESHOLD=1.2217305;


public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList)
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
							StraightSplitLine ssl=new StraightSplitLine(cOne,cTwo,saliency,concavityConcavityAlignment,concavityLineAlignment);
							possibleSplitLines.add(ssl);
						}
					}
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
}
