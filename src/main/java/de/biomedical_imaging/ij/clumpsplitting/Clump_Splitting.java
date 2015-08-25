package de.biomedical_imaging.ij.clumpsplitting;

import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener {

	int arbitraryNumber;
	@Override
	public int setup(String arg, ImagePlus imp) {
		if(imp==null){
			IJ.error("No image open");
			return DONE;
		}
		
		if (imp.getProcessor().isBinary()) {
			IJ.error("Only binary images are supported");
			return DONE;
		}
		
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip) {
		// TODO Auto-generated method stub
		IJ.showMessage("The arbitraryNumber is: " + arbitraryNumber);
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		arbitraryNumber = (int) gd.getNextNumber();
		
		return arbitraryNumber>=0; 
	}

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
