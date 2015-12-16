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

public class Clump_Splitting implements ExtendedPlugInFilter, DialogListener
{
	int backgroundColor = 1;
	int arbitraryNumber;
	ImagePlus imp;

	@Override
	public int setup(String arg, ImagePlus imp)
	{
		if (imp == null)
		{
			IJ.error("No image open");
			return DONE;
		}

		if (!imp.getProcessor().isBinary())
		{
			IJ.error("Only binary images are supported");
			return DONE;
		}
		this.imp = imp;
		return DOES_8G + DOES_STACKS + FINAL_PROCESSING + PARALLELIZE_STACKS;
	}

	@Override
	public void run(ImageProcessor ip)
	{

		ArrayList<Clump> clumpList = new ArrayList<Clump>();
		ManyBlobs blobList = new ManyBlobs(imp);
		// if the background is white backgroundColor must be 1
		blobList.setBackground(backgroundColor);
		// Clumps of the Image will be detected
		blobList.findConnectedComponents();
		Clump clump = null;
		ImageProcessor ipr = imp.getProcessor();
		for (Blob b : blobList)
		{
			// right now only the outer contours are considered
			Polygon p = b.getOuterContour();
			clump = new Clump(p, ipr);
			clumpList.add(clump);
		}

	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e)
	{

		return arbitraryNumber >= 0;
	}

	/**
	 * Dialogue where you can decide which one is your backgroundcolor
	 */
	@Override
	public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr)
	{

		GenericDialog gd = new GenericDialog("Choose Background");
		gd.addMessage("What ist your Background Color?");
		String[] checkboxValues =
		{ "black", "white" };
		gd.addRadioButtonGroup("BackgroundColor", checkboxValues, 2, 1, "white");
		gd.addPreviewCheckbox(pfr);
		gd.showDialog();
		if (gd.wasCanceled())
		{
			return DONE;
		}
		if (gd.wasOKed())
		{
			String selection = gd.getNextRadioButton();
			if (selection.equals("black"))
			{
				backgroundColor = 0;
			} else
			{
				if (selection.equals("white"))
				{
					backgroundColor = 1;
				}
			}
		}

		return IJ.setupDialog(imp, DOES_8G);
	}

	@Override
	public void setNPasses(int nPasses)
	{
		// TODO Auto-generated method stub

	}

}
