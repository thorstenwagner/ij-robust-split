/*

Copyright (c) 2016 Louise Bloch (louise.bloch001@stud.fh-dortmund.de), Thorsten Wagner (wagner@b
iomedical-imaging.de)

Permission is hereby granted, free of charge, to any person obtaining a
copy
of this software and associated documentation files (the "Software"),
to deal
in the Software without restriction, including without limitation the
rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE
SOFTWARE.
*/
package de.biomedical_imaging.ij.clumpsplitting;

import java.util.ArrayList;

import ij.plugin.filter.MaximumFinder;

/**
 * detects all ConcavityPixels with the global Maximum distance between
 * concavityRegion and Contour of a Clump
 * 
 * @author Louise
 *
 */
public class AllConcavityPixelDetector implements AbstractConcavityPixelDetector
{
	/**
	 * Computes all ConcavityPixels for a ConcavityRegion, this means, which are
	 * included by the Contour of the Clump at the ConcavityRegion and the Line
	 * which connects start- and endPoint of the ConcavityRegion
	 * 
	 * @param concavityRegion
	 *            ConcavityRegion to find ConcavityPixels for
	 * @return List of all detected ConcavityPixel of the region (all Maxima of
	 *         the distanceList of the ConcavityRegion)
	 */
	@Override
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion)
	{
		double[] p = new double[concavityRegion.getDistList().size()];
		for (int i = 0; i < concavityRegion.getDistList().size(); i++)
		{
			p[i] = concavityRegion.getDistList().get(i);
		}
		boolean excludeOnEdges = true;

		int[] maxima = MaximumFinder.findMaxima(p, 1, excludeOnEdges);

		ArrayList<ConcavityPixel> concavityPixelList = new ArrayList<ConcavityPixel>();
		for (int i = 0; i < maxima.length; i++)
		{
			if (concavityRegion.getDistList().get(maxima[i]) > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
			{
				ConcavityPixel concavityPixel = new ConcavityPixel(
						concavityRegion.getBoundaryPointList().get(maxima[i]),
						(concavityRegion.getDistList().get(maxima[i])), concavityRegion);
				concavityPixelList.add(concavityPixel);
			}
		}
		return concavityPixelList;
	}

}
