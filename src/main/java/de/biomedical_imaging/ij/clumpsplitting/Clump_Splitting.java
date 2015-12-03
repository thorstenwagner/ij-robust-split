package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.AWTEvent;
import java.awt.Polygon;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;

import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;
/**
 * 
 * @author Louise
 *
 */

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener {
	int backgroundColor=1;
	int arbitraryNumber;
	ImagePlus imp;
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
		this.imp=imp;
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip) {
		ArrayList<Clump> clumpList=new ArrayList<Clump>();
		ManyBlobs blobList=new ManyBlobs(imp);
		blobList.setBackground(backgroundColor);
		blobList.findConnectedComponents(); 
		Clump clump=null;
		ImageProcessor ipr=imp.getProcessor();
		for(Blob b: blobList)
		{
			Polygon p=b.getOuterContour();
			clump=new Clump(p,ipr);
			clumpList.add(clump);
			/*ArrayList<Polygon> innerContours=new ArrayList<Polygon>();
	    	innerContours=b.getInnerContours();
	    	   
	    	  for(Polygon inner:innerContours)
	    	  {
	    		  clump=new Clump(inner,ipr);
	    		  clumpList.add(clump);   
	    	  }*/
		}
		
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
		GenericDialog gd=new GenericDialog("Choose Background");
		gd.addMessage("What ist your Background Color?");
		String[] checkboxValues={"black", "white"};
		//boolean[] checkBoxChoose={false,true};
		gd.addRadioButtonGroup("BackgroundColor", checkboxValues,2,1,"white");
		
		gd.showDialog();
		if(gd.wasCanceled())
		{
			return DONE;
		}
		if(gd.wasOKed())
		{
			String ausw=gd.getNextRadioButton();
			if(ausw.equals("black"))
			{
				backgroundColor=0;
			}
			else{
				if(ausw.equals("white"))
				{
					backgroundColor=1;
				}
			}
		}
		
		/*GenericDialog gd = new GenericDialog(command + "...");
		gd.addMessage("Hello world!");
		gd.addNumericField("Some arbitrary positive number:", 0, 0);
		gd.addDialogListener(this);
		gd.addPreviewCheckbox(pfr);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return DONE;
		}*/
		return IJ.setupDialog(imp, DOES_8G);
	}

	@Override
	public void setNPasses(int nPasses) {
		// TODO Auto-generated method stub
		
	}

}
