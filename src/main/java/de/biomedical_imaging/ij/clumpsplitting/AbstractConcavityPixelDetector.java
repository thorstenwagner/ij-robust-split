package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

public interface AbstractConcavityPixelDetector
{
	/**
	 * Computes ConcavityPixels for a ConcavityRegion
	 * 
	 * @param concavityRegion
	 *            ConcavityRegion to find ConcavityPixels for
	 * @return List of all detected ConcavityPixel of the region
	 */
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion);

}
