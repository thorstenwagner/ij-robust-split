package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Polygon;

import ij.gui.Roi;
import ij.gui.PolygonRoi;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.blob.*;

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener {

	int arbitraryNumber;
	@Override
	public int setup(String arg, ImagePlus imp) {
		if(imp==null){
			IJ.error("No image open");
			return DONE;
		}
		
		if (!imp.getProcessor().isBinary()) {
			IJ.error("Only binary images are supported");
			return DONE;
		}
		
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		//IJ.showMessage("");
		//IJ.showMessage("The arbitraryNumber is: " + arbitraryNumber);
		ImagePlus imp=IJ.getImage();
		BoundaryArcsAdministration.drawPolygons(imp);
		
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		//arbitraryNumber = (int) gd.getNextNumber();
		
		return arbitraryNumber>=0; 
	}
	/*public void doing(ImagePlus imp)
	{
	
		ManyBlobs allBlobs = new ManyBlobs(imp); // Extended ArrayList
	       allBlobs.findConnectedComponents(); // Start the Connected Component Algorithm
	       Overlay o=new Overlay();
	       for(Blob b:allBlobs)
	       {
	    	   Polygon p= b.getOuterContour();
	    	   PolygonRoi roi=new PolygonRoi(p,Roi.POLYGON);
	    	   
	    	   roi.setImage(imp);
	    	   roi.setColor(Color.RED);
	    	   ImageProcessor ip=imp.getProcessor();
	    	  // ip.setRoi(roi);
	    	  // roi.drawPixels(ip);
	    	   o.add(roi);
	    	   //imp.setRoi(roi);
	    	   imp.setOverlay(o);
	    	   
	    	   
	    	   IJ.showMessage(""+p.xpoints[0]+" "+p.ypoints[0]);
	    	  
	       }
	       o.drawLabels(true);
		//ImagePlus imp=IJ.getImage();
		/*ImageStatistics stats=imp.getStatistics();
		if(!imp.getProcessor().isBinary())
		{
			IJ.showMessage("isnBinary");
		}
		boolean notBinary = (stats.histogram[0] + stats.histogram[255]) != stats.pixelCount;
        boolean toManyChannels = (imp.getNChannels()>1);
        boolean wrongBitDepth = (imp.getBitDepth()!=8);
        if(notBinary)
        {
        	IJ.showMessage("notBinary");
        	IJ.showMessage(stats.histogram[0]+" + "+stats.histogram[255]+" = " +stats.pixelCount);
        	
        }
        if(toManyChannels)
        {
        	IJ.showMessage("tooManyChannels");
        }
        if(wrongBitDepth)
        {
        	IJ.showMessage("wrongBitDepth");
        }

	}*/
	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
		GenericDialog gd = new GenericDialog(command + "...");
		gd.addMessage("Hello world!");
		gd.addNumericField("Some arbitrary positive number:", 0, 0);
		gd.addDialogListener(this);
		gd.addPreviewCheckbox(pfr);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}
		return IJ.setupDialog(imp, DOES_8G);
	}

	@Override
	public void setNPasses(int nPasses) {
		// TODO Auto-generated method stub
		
	}

}
