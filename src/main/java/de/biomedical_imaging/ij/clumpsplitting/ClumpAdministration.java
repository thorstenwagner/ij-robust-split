package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.Polygon;
import java.util.ArrayList;

import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;

public class ClumpAdministration {

	public ArrayList<Clump> getClumps(ImagePlus imp)
	{
		ArrayList<Clump> clumpList=new ArrayList<Clump>();
		ManyBlobs blobList=new ManyBlobs(imp);
		blobList.findConnectedComponents();
		for(Blob b: blobList)
		{
			Polygon p=b.getOuterContour();
			Clump temp=new Clump(p,imp);
			clumpList.add(temp);
			 
			ArrayList<Polygon> innerContours=b.getInnerContours();
	    	   
	    	   for(Polygon inner:innerContours)
	    	   {
	    		   Clump tempInner=new Clump(inner,imp);
	    		   clumpList.add(tempInner);
	    	   }
		}
		return clumpList;
	}
}
