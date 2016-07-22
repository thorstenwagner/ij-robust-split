package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

import ij.process.ImageProcessor;

public interface AbstractOuterConcavityRegionDetector
{
	public ArrayList<ConcavityRegion> computeOuterConcavityRegions(ImageProcessor binary,Clump clump);
	
}
