/*
The MIT License (MIT)

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

package de.biomedical_imaging.ij.clumpsplitting.SplitLines;

import java.util.ArrayList;

import de.biomedical_imaging.ij.clumpsplitting.Clump;
import de.biomedical_imaging.ij.clumpsplitting.ConcavityRegion;
import ij.process.ImageProcessor;

/**
 * interface for different Types of Abstract SplitLine Calculation, the
 * CalculationTypes are definded in enum SplitLineTypes
 * 
 * @author Louise
 *
 */
public interface AbstractSplitLineCalculator
{
	/**
	 * Calculation of the Possible SplitLines
	 * 
	 * @param concavityRegionList
	 *            List of all ConcavityRegions of a Clump (outer and inner
	 *            Contours)
	 * @param c
	 *            the Clump to Split
	 * @param ip
	 *            Image Processor to detect intensityInformation
	 * @return List of all detected SplitLines
	 */
	public ArrayList<AbstractSplitLine> calculatePossibleSplitLines(ArrayList<ConcavityRegion> concavityRegionList,
			Clump c, ImageProcessor ip, ImageProcessor binary);

}
