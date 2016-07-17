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

/**
 * Calculator for ConcavityPixel, detects Concavity Pixel of a local Maximum of
 * the Distance List of a Concavity Region
 * 
 * @author Louise
 *
 */
public class LargestDistanceConcavityPixelDetector implements AbstractConcavityPixelDetector
{

	/**
	 * detects local Maxima for a ConcavityRegion
	 */
	@Override
	public ArrayList<ConcavityPixel> computeConcavityPixel(ConcavityRegion concavityRegion)
	{
		// int indexMax = 0;
		double max = 0;
		// int index = 0;
		ArrayList<ConcavityPixel> moreThanOneMax = new ArrayList<ConcavityPixel>();
		for (int i = 0; i < concavityRegion.getDistList().size(); i++)
		{
			double d = concavityRegion.getDistList().get(i);
			if (d >= max)
			{
				if (d > max)
				{
					moreThanOneMax.clear();
					if (d > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
					{
						ConcavityPixel concavityPixel = new ConcavityPixel(
								concavityRegion.getBoundaryPointList().get(i), d, concavityRegion);

						moreThanOneMax.add(concavityPixel);
					}
					max = d;

					// indexMax = index;
				} else
				{
					if (d == max)
					{
						if (d > Clump_Splitting.CONCAVITY_DEPTH_THRESHOLD)
						{

							ConcavityPixel concavityPixel = new ConcavityPixel(
									concavityRegion.getBoundaryPointList().get(i), d, concavityRegion);

							moreThanOneMax.add(concavityPixel);
						}
						max = d;
					}
				}
			}
		}

		return moreThanOneMax;
	}

}
