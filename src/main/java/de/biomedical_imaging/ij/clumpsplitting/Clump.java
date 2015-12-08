package de.biomedical_imaging.ij.clumpsplitting;


import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

import ij.IJ;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;
/**
 * 
 * @author Louise
 *
 */
public class Clump {
private ArrayList<ConcavityRegion> concavityRegionList;
private Polygon boundary;
private Polygon convexHull;
private int indexOfMaxConcavityRegion;
private double secondMaxConcavityDepth;

/**
 * erzeugt einen Clump, ein Clump repräsentiert zusammenhängende Partikel
 * @param boundary Umrandungen des Clumps
 * @param ip Bild
 */
public Clump(Polygon boundary,ImageProcessor ip)
{
	this.boundary=boundary;
	//IJ.showMessage("Clump Konstruktor");
	this.computeConcavityRegions(ip);
	
}
/**
 * Ermittelt die Bereiche mit großer Konkavität dabei wird zunächst die konvexe Hülle bestimmt und dann die Konvexen Bereiche
 * @param imp Bild wird benötigt um konkavitätspixel einzuzeichnen
 */
private void computeConcavityRegions(ImageProcessor ip)
{
	
	//IJ.showMessage("computeConcavityRegions");
	PolygonRoi pr=new PolygonRoi(boundary,Roi.POLYGON);
	Polygon convexHull=pr.getConvexHull();
	this.convexHull=convexHull;
	
	ConcavityRegionAdministration cra=new ConcavityRegionAdministration(boundary,this.convexHull);
	concavityRegionList=cra.computeConcavityRegions();
	IJ.log("CRL.length"+concavityRegionList.size());
	for(ConcavityRegion cr:concavityRegionList)
	{
			cr.markMax(ip);
			
	}
	this.computeFirstAndSecondLargestConcavityDepth();
	
	AbstractSplitLineCalculator sslc=new StraightSplitLineCalculator();
	ArrayList<AbstractSplitLine> possibleSplitLines=sslc.calculatePossibleSplitLines(concavityRegionList,this);
	for(AbstractSplitLine asl:possibleSplitLines)
	{
		StraightSplitLine ssl=(StraightSplitLine)asl;
		ssl.drawLine(ip);
	}

	
	
	PolygonRoi polygonRoi=new PolygonRoi(convexHull,Roi.POLYGON);
	//ip.setLineWidth(10);
	ip.setColor(Color.CYAN);
	ip.draw(polygonRoi);

	}
	private void computeFirstAndSecondLargestConcavityDepth()
	{
		double[] max= {0,0,0,0};
		int i=0;
		for(ConcavityRegion cr: concavityRegionList)
		{
			if(cr.getMaxDist()>=max[0])
			{
				max[0]=cr.getMaxDist();
				max[1]=i;
			}	
			else
			{
				if(cr.getMaxDist()>=max[2])
				{
					max[2]=cr.getMaxDist();
					max[3]=i;
				}
			}
			i++;
		}
		if(max[2]<ConcavityRegionAdministration.CONCAVITY_DEPTH_THRESHOLD)
		{
			max[2]=ConcavityRegionAdministration.CONCAVITY_DEPTH_THRESHOLD;
		}
		indexOfMaxConcavityRegion= (int)max[1];
		secondMaxConcavityDepth=max[2];
		IJ.log(secondMaxConcavityDepth+"");
	}
	public ConcavityRegion getRegionOfMaxConcavityDepth()
	{
		IJ.log(indexOfMaxConcavityRegion+"");
		return concavityRegionList.get(indexOfMaxConcavityRegion);
	}
	public double getSecondMaxConcavityRegionDepth()
	{
		return secondMaxConcavityDepth;
	}
	public Polygon getBoundary()
	{
		return boundary;
	}
}

