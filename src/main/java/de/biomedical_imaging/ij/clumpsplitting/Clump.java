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
	for(ConcavityRegion cr:concavityRegionList)
	{
			cr.markMax(ip);
			
		
	}
	AbstractSplitLineCalculator sslc=new StraightSplitLineCalculator();
	ArrayList<AbstractSplitLine> possibleSplitLines=sslc.calculatePossibleSplitLines(concavityRegionList);
	for(AbstractSplitLine asl:possibleSplitLines)
	{
		StraightSplitLine ssl=(StraightSplitLine)asl;
		ssl.drawLine(ip);
	}
	this.convexHull=convexHull;
	
	
	PolygonRoi polygonRoi=new PolygonRoi(convexHull,Roi.POLYGON);
	//ip.setLineWidth(10);
	ip.setColor(Color.CYAN);
	ip.draw(polygonRoi);

	}
}

