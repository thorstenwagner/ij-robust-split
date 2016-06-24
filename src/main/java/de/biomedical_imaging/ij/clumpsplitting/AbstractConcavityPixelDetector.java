package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

public interface AbstractConcavityPixelDetector
{
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion);

}
