package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;

import java.awt.Polygon;

import java.awt.geom.Path2D;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;


public class BoundaryArcAdministration {
	
	public static void administrate(ImagePlus imp)
	{
		ManyBlobs blobList=new ManyBlobs(imp);
	//	computeBoundaryArcs(blobList);
		drawPolygons(blobList,imp);
	}
	public static BoundaryArc computeBoundaryArc(int[] startingPoint,int[] endPoint, Polygon p)
	{	
		IJ.showMessage("compute");
		BoundaryArc ba=new BoundaryArc(startingPoint[0],startingPoint[1],endPoint[0],endPoint[1]);
		boolean started=false;
		boolean ended=false;
		//ba.setStartingPoint(startingPoint[0], startingPoint[1]);
		Path2D path= new Path2D.Double();
		int i=0;
		while(i<p.npoints&&!ended)
		{
			if(p.xpoints[i]==startingPoint[0]&& p.ypoints[i]==startingPoint[1]&&!started)
			{
				path.moveTo(p.xpoints[i], p.ypoints[i]);
				started=true;
			}
			else
			{
				/** *TODO* überdenken!!
			*/
				if(started&&(p.xpoints[i]!=endPoint[0]|| p.ypoints[i]!=endPoint[1]))
				{
					path.lineTo(p.xpoints[i], p.ypoints[i]);
				}
				else
				{
					if(started&&p.xpoints[i]==endPoint[0]&& p.ypoints[i]==endPoint[1])
					{
						path.lineTo(p.xpoints[i], p.ypoints[i]);
						ended=true;
					}
					
				}
			}
			i++;
		}
		ba.setPath(path);
		ba.computeBoundaryArcNumber();
		return ba;
		
	}
	/*public static void computeBoundaryArcs(ManyBlobs blobList)
	{
	//	ManyBlobs blobList= new ManyBlobs(imp);
		blobList.findConnectedComponents();
		for(Blob b: blobList)
		{
			Polygon p=b.getOuterContour();
		//	BoundaryArc outer=computeBoundaryArc(p);
			IJ.showMessage(outer.getNumber());
			controlBoundaryArcs(outer);
			ArrayList<Polygon> innerContours=new ArrayList<Polygon>();
	    	   innerContours=b.getInnerContours();
	    	   
	    	   for(Polygon inner:innerContours)
	    	   {
	    		  BoundaryArc innerba= computeBoundaryArc(inner);
	    		   IJ.showMessage(innerba.getNumber());
	    		   controlBoundaryArcs(innerba);
	    	   }
		}
		
	}*/
	public static void controlBoundaryArcs(BoundaryArc ba)
	{int j=0;
		for(int i=0;i<ba.getNumber().length()-1;i++)
		{	
			int distance=ba.getDigit(i)-ba.getDigit(i+1);
			
			if(distance<-1 || distance>1)
			{
				j++;
			}
			
		}
		IJ.showMessage(j+"nicht gerade Bereiche");
	}
	
	public static void drawPolygons(ManyBlobs allBlobs,ImagePlus imp)
	{
	//	ManyBlobs allBlobs= new ManyBlobs(imp);
		allBlobs.findConnectedComponents();
		Overlay o=new Overlay();
		 for(Blob b:allBlobs)
	       {
	    	   Polygon p= b.getOuterContour();
	    	
	    	   PolygonRoi roi=new PolygonRoi(p,Roi.POLYGON);
	    	  
	    	  
	    	   roi.setImage(imp);
	    	   roi.setColor(Color.RED);
	    	//   ImageProcessor ip=imp.getProcessor();
	    	   o.add(roi);
	    	   
	    	   
	    	   ArrayList<Polygon> innerContours=new ArrayList<Polygon>();
	    	   innerContours=b.getInnerContours();
	    	   for(Polygon inner:innerContours)
	    	   {
	    		   PolygonRoi proi=new PolygonRoi(inner,Roi.POLYGON);
	    		   proi.setImage(imp);
	    		   proi.setColor(Color.BLUE);
	    		   o.add(proi);
	    	   }
	    	   
	       }
		 imp.setOverlay(o);
	}
	

}
