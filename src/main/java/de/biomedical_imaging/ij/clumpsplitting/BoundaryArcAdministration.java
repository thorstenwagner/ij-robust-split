package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Color;

import java.awt.Polygon;
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
		computeBoundaryArcs(blobList);
		drawPolygons(blobList,imp);
	}
	public static void computeBoundaryArc(int[] startingPoint,int[] endPoint)
	{
		//*TODO* zwischenBogen zwischen den beiden Punkten berechnen*//
	}
	public static void computeBoundaryArcs(ManyBlobs blobList)
	{
	//	ManyBlobs blobList= new ManyBlobs(imp);
		blobList.findConnectedComponents();
		for(Blob b: blobList)
		{
			Polygon p=b.getOuterContour();
			BoundaryArc outer=computeBoundaryArc(p);
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
		
	}
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
	

	public static BoundaryArc computeBoundaryArc(Polygon p)
	{
		BoundaryArc ba=new BoundaryArc();
		
		
		ba.setStartingPoint(0, p.xpoints[0]);
		ba.setStartingPoint(1,p.ypoints[1]);
		int[] deltaX=new int[p.npoints];
		int[] deltaY=new int[p.npoints];
		//IJ.showMessage(p.xpoints[0]+" xkoord "+p.ypoints[0]+" ykoord "+ "letzter: " + p.xpoints[p.npoints-2] +" x "+ p.ypoints[p.npoints-2]+" y ");
		for(int i=0;i<p.npoints-1;i++)
		{
			
				deltaX[i]= p.xpoints[i+1]-p.xpoints[i];
				deltaY[i]=p.ypoints[i+1]-p.ypoints[i];
				switch(deltaX[i])
			{
			case -1:
				switch(deltaY[i])
				{
				case -1:
					
					ba.concatNumber(3);
					//IJ.showMessage("3"+" "+number);
					
					break;
				case 0:
					ba.concatNumber(4);
					//IJ.showMessage("4"+" "+number);
					
					break;
				case 1:
					ba.concatNumber(5);
				//IJ.showMessage("5"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis1");
					break;
				}
				break;
			case 0:
				switch(deltaY[i])
				{
				case -1:
					ba.concatNumber(2);
					
					//IJ.showMessage("2"+" "+number);
					
					break;
				case 1:	
					ba.concatNumber(6);
				
					//IJ.showMessage("6"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis2"+deltaY[i]);
					break;
				}
				break;
			case 1:
				switch(deltaY[i])
				{
				case -1:
					ba.concatNumber(1);
				//	IJ.showMessage("1"+" "+number);
					
					break;
				case 0:
					ba.concatNumber(0);
					//IJ.showMessage("0"+" "+number);
					
					break;
				case 1:
					ba.concatNumber(7);
				//	IJ.showMessage("7"+" "+number);
					
					break;
				default:
					IJ.error("unvorhergesehenes Ereignis3");
					break;
				}
				break;
			default:
				IJ.error("unvorhergesehenes Ereignis4");
				break;
			}
			
				
		}
		return ba;
	}
}
