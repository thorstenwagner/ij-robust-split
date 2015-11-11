package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class Clump {
private ArrayList<ConcavityRegionAdministration> concavityRegionList;
private Polygon boundary;
private Polygon convexHull;

public Clump(Polygon boundary,ImagePlus imp)
{
	this.boundary=boundary;
	IJ.showMessage("Clump Konstruktor");
	this.computeConcavityRegions(imp);
	
}
private void computeConcavityRegions(ImagePlus imp)
{
	//für jeden Clump
	IJ.showMessage("computeConcavityRegions");
	ConvexHullAdministration cha=new ConvexHullAdministration();
	Polygon convexHull=cha.computeConvexHull(boundary);
	ConcavityRegionAdministration cra=new ConcavityRegionAdministration(boundary,convexHull);
	ArrayList<ConcavityRegion> conList=cra.computeConcavityRegions();
	for(ConcavityRegion cr:conList)
	{
		//IJ.showMessage(cr.getMaxDistCoord().getX()+" "+cr.getMaxDistCoord().getY());
		cr.markMax(imp);
	}
	this.convexHull=convexHull;
	/**for(int i=0;i<convexHull.npoints;i++)
	{
	IJ.showMessage("Hull:"+convexHull.xpoints[i]+ " "+ convexHull.ypoints[i]);
	}*/

	ImageProcessor ip=imp.getProcessor();
	PolygonRoi polygonRoi=new PolygonRoi(convexHull,Roi.POLYGON);
	ip.draw(polygonRoi);
/*	for(ConvexHull convexHull:convexHullList)
	{
		ip.setColor(Color.MAGENTA);
		ip.drawLine(convexHull.getStartingPoint()[0], convexHull.getStartingPoint()[1], convexHull.getEndPoint()[0], convexHull.getEndPoint()[1]);
		IJ.showMessage(convexHull.getStartingPoint()[0]+" "+convexHull.getStartingPoint()[1] +"\n"+convexHull.getEndPoint()[0]+ " "+convexHull.getEndPoint()[1]);
		/*BoundaryArc boundaryArc=BoundaryArcAdministration.computeBoundaryArc(convexHull.getStartingPoint(),convexHull.getEndPoint(),boundary);
		ConcavityRegion concavityRegion=new ConcavityRegion();
		concavityRegion.setConvexHull(convexHull);
		concavityRegion.setBoundaryArc(boundaryArc);
		concavityRegionList.add(concavityRegion);*/
	}
}

