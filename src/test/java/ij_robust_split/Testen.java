package ij_robust_split;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;

public class Testen
{
	
		
	public static void main(String[] args) {
		   
	    new ImageJ();
	    ImagePlus image = IJ.openImage("D:/Projektarbeit/LiveDemo/data_binary.tif");
	    image.show();
	    IJ.runPlugIn(image, "de.biomedical_imaging.ij.clumpsplitting.Clump_Splitting", null);

	    //image.show();
	    WindowManager.addWindow(image.getWindow());
	}



}
